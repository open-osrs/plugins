/*
 * Copyright (c) 2017, Aria <aria@ar1as.space>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.grounditemsextended;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.Rectangle;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Math.floor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemID;
import static net.runelite.api.ItemID.*;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemQuantityChanged;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.PlayerLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.grounditemsextended.config.HighlightTier;
import net.runelite.client.plugins.grounditemsextended.config.ItemHighlightMode;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.QuantityFormatter;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Ground Items Extended",
	description = "Highlight ground items and/or show price information",
	tags = {"grand", "exchange", "high", "alchemy", "prices", "highlight", "overlay"}
)
public class GroundItemsExtendedPlugin extends Plugin
{
	@Value
	static class PriceHighlight
	{
		private final int price;
		private final Color color;
	}

	@Getter(AccessLevel.PUBLIC)
	public static final Map<GroundItemExtended.GroundItemKey, GroundItemExtended> collectedGroundItems = new LinkedHashMap<>();
	// The game won't send anything higher than this value to the plugin -
	// so we replace any item quantity higher with "Lots" instead.
	static final int MAX_QUANTITY = 65535;
	// ItemID for coins
	private static final int COINS = ItemID.COINS_995;
	// items stay on the ground for 30 mins in an instance
	private static final int INSTANCE_DURATION_MILLIS = 45 * 60 * 1000;
	private static final int INSTANCE_DURATION_TICKS = (int) floor(30 * 60 / 0.6);
	//untradeables stay on the ground for 150 seconds (http://oldschoolrunescape.wikia.com/wiki/Item#Dropping_and_Destroying)
	private static final int UNTRADEABLE_DURATION_MILLIS = 150 * 1000;
	private static final int UNTRADEABLE_DURATION_TICKS = (int) floor(150 / 0.6);
	//items stay on the ground for 1 hour after death
	private static final int NORMAL_DURATION_MILLIS = 60 * 1000;
	private static final int NORMAL_DURATION_TICKS = (int) floor(60 / 0.6);
	// Ground item menu options
	private static final int FIRST_OPTION = MenuAction.GROUND_ITEM_FIRST_OPTION.getId();
	private static final int SECOND_OPTION = MenuAction.GROUND_ITEM_SECOND_OPTION.getId();
	private static final int THIRD_OPTION = MenuAction.GROUND_ITEM_THIRD_OPTION.getId(); // this is Take
	private static final int FOURTH_OPTION = MenuAction.GROUND_ITEM_FOURTH_OPTION.getId();
	private static final int FIFTH_OPTION = MenuAction.GROUND_ITEM_FIFTH_OPTION.getId();
	private static final int EXAMINE_ITEM = MenuAction.EXAMINE_ITEM_GROUND.getId();
	private static final int WALK = MenuAction.WALK.getId();
	private static final int CAST_ON_ITEM = MenuAction.WIDGET_TARGET_ON_GROUND_ITEM.getId();
	private static final String TELEGRAB_TEXT = ColorUtil.wrapWithColorTag("Telekinetic Grab", Color.GREEN) + ColorUtil.prependColorTag(" -> ", Color.WHITE);
	private static final int KRAKEN_REGION = 9116;
	private static final int KBD_NMZ_REGION = 9033;
	private List<PriceHighlight> priceChecks = List.of();
	private final Queue<Integer> droppedItemQueue = EvictingQueue.create(16); // recently dropped items
	LoadingCache<NamedQuantity, Boolean> hiddenItems;
	static final Set<Integer> herbloreItems = Set.of
		(
			//Grimy Herbs
			GRIMY_GUAM_LEAF,
			GRIMY_GUAM_LEAF + 1,
			GRIMY_MARRENTILL,
			GRIMY_MARRENTILL + 1,
			GRIMY_TARROMIN,
			GRIMY_TARROMIN + 1,
			GRIMY_HARRALANDER,
			GRIMY_HARRALANDER + 1,
			GRIMY_RANARR_WEED,
			GRIMY_RANARR_WEED + 1,
			GRIMY_TOADFLAX,
			GRIMY_TOADFLAX + 1,
			GRIMY_IRIT_LEAF,
			GRIMY_IRIT_LEAF + 1,
			GRIMY_AVANTOE,
			GRIMY_AVANTOE + 1,
			GRIMY_KWUARM,
			GRIMY_KWUARM + 1,
			GRIMY_SNAPDRAGON,
			GRIMY_SNAPDRAGON + 1,
			GRIMY_CADANTINE,
			GRIMY_CADANTINE + 1,
			GRIMY_LANTADYME,
			GRIMY_LANTADYME + 1,
			GRIMY_DWARF_WEED,
			GRIMY_DWARF_WEED + 1,
			GRIMY_TORSTOL,
			GRIMY_TORSTOL + 1,

			//Clean Herbs
			GUAM_LEAF,
			GUAM_LEAF + 1,
			MARRENTILL,
			MARRENTILL + 1,
			TARROMIN,
			TARROMIN + 1,
			HARRALANDER,
			HARRALANDER + 1,
			RANARR_WEED,
			RANARR_WEED + 1,
			TOADFLAX,
			TOADFLAX + 1,
			IRIT_LEAF,
			IRIT_LEAF + 1,
			AVANTOE,
			AVANTOE + 1,
			KWUARM,
			KWUARM + 1,
			SNAPDRAGON,
			SNAPDRAGON + 1,
			CADANTINE,
			CADANTINE + 1,
			LANTADYME,
			LANTADYME + 1,
			DWARF_WEED,
			DWARF_WEED + 1,
			TORSTOL,
			TORSTOL + 1,

			//Secondary ingredients
			EYE_OF_NEWT,
			EYE_OF_NEWT + 1,
			UNICORN_HORN,
			UNICORN_HORN + 1,
			UNICORN_HORN_DUST,
			UNICORN_HORN_DUST + 1,
			LIMPWURT_ROOT,
			LIMPWURT_ROOT + 1,
			RED_SPIDERS_EGGS,
			RED_SPIDERS_EGGS + 1,
			CHOCOLATE_BAR,
			CHOCOLATE_BAR + 1,
			CHOCOLATE_DUST,
			CHOCOLATE_DUST + 1,
			TOADS_LEGS,
			TOADS_LEGS + 1,
			GOAT_HORN_DUST,
			GOAT_HORN_DUST + 1,
			DESERT_GOAT_HORN,
			SNAPE_GRASS,
			SNAPE_GRASS + 1,
			MORT_MYRE_FUNGUS,
			MORT_MYRE_FUNGUS + 1,
			WHITE_BERRIES,
			WHITE_BERRIES + 1,
			BLUE_DRAGON_SCALE,
			BLUE_DRAGON_SCALE + 1,
			DRAGON_SCALE_DUST,
			DRAGON_SCALE_DUST + 1,
			WINE_OF_ZAMORAK,
			WINE_OF_ZAMORAK + 1,
			POTATO_CACTUS,
			POTATO_CACTUS + 1,
			BIRD_NEST,
			BIRD_NEST_5071,
			BIRD_NEST_5072,
			BIRD_NEST_5073,
			BIRD_NEST_5074,
			BIRD_NEST_5075,
			BIRD_NEST_7413,
			BIRD_NEST_13653,
			BIRD_NEST_22798,
			BIRD_NEST_22800,
			LAVA_SCALE,
			LAVA_SCALE + 1,
			LAVA_SCALE_SHARD,
			LAVA_SCALE_SHARD + 1,
			SUPERIOR_DRAGON_BONES,
			SUPERIOR_DRAGON_BONES + 1,
			CRUSHED_SUPERIOR_DRAGON_BONES,
			CRUSHED_SUPERIOR_DRAGON_BONES + 1,
			AMYLASE_CRYSTAL,
			GARLIC,
			GARLIC + 1,

			//Jungle Potion herbs
			GRIMY_ARDRIGAL,
			GRIMY_ROGUES_PURSE,
			GRIMY_SITO_FOIL,
			GRIMY_SNAKE_WEED,
			GRIMY_VOLENCIA_MOSS,

			//Herb seeds
			GUAM_SEED,
			MARRENTILL_SEED,
			TARROMIN_SEED,
			HARRALANDER_SEED,
			GOUT_TUBER,
			RANARR_SEED,
			TOADFLAX_SEED,
			IRIT_SEED,
			AVANTOE_SEED,
			KWUARM_SEED,
			SNAPDRAGON_SEED,
			CADANTINE_SEED,
			LANTADYME_SEED,
			DWARF_WEED_SEED,
			TORSTOL_SEED,

			//Secondary seeds
			LIMPWURT_SEED,
			SNAPE_GRASS_SEED,
			POTATO_CACTUS_SEED,
			JANGERBERRY_SEED,
			POISON_IVY_SEED,
			BELLADONNA_SEED
		);
	static final Set<Integer> prayerItems = Set.of
		(
			//Bones
			BONES,
			BONES + 1,
			WOLF_BONES,
			WOLF_BONES + 1,
			BURNT_BONES,
			BURNT_BONES + 1,
			MONKEY_BONES,
			MONKEY_BONES + 1,
			BAT_BONES,
			BAT_BONES + 1,
			BIG_BONES,
			BIG_BONES + 1,
			JOGRE_BONES,
			JOGRE_BONES + 1,
			ZOGRE_BONES,
			ZOGRE_BONES + 1,
			SHAIKAHAN_BONES,
			SHAIKAHAN_BONES + 1,
			BABYDRAGON_BONES,
			BABYDRAGON_BONES + 1,
			WYRM_BONES,
			WYRM_BONES + 1,
			WYVERN_BONES,
			WYVERN_BONES + 1,
			DRAGON_BONES,
			DRAGON_BONES + 1,
			DRAKE_BONES,
			DRAKE_BONES + 1,
			FAYRG_BONES,
			FAYRG_BONES + 1,
			LAVA_DRAGON_BONES,
			LAVA_DRAGON_BONES + 1,
			RAURG_BONES,
			RAURG_BONES + 1,
			HYDRA_BONES,
			HYDRA_BONES + 1,
			DAGANNOTH_BONES,
			DAGANNOTH_BONES + 1,
			OURG_BONES,
			OURG_BONES + 1,
			SUPERIOR_DRAGON_BONES,
			SUPERIOR_DRAGON_BONES + 1,

			//Ensouled heads
			ENSOULED_ABYSSAL_HEAD_13508,
			ENSOULED_ABYSSAL_HEAD_13508 + 1,
			ENSOULED_AVIANSIE_HEAD_13505,
			ENSOULED_AVIANSIE_HEAD_13505 + 1,
			ENSOULED_BEAR_HEAD_13463,
			ENSOULED_BEAR_HEAD_13463 + 1,
			ENSOULED_BLOODVELD_HEAD_13496,
			ENSOULED_BLOODVELD_HEAD_13496 + 1,
			ENSOULED_CHAOS_DRUID_HEAD_13472,
			ENSOULED_CHAOS_DRUID_HEAD_13472 + 1,
			ENSOULED_DAGANNOTH_HEAD_13493,
			ENSOULED_DAGANNOTH_HEAD_13493 + 1,
			ENSOULED_DEMON_HEAD_13502,
			ENSOULED_DEMON_HEAD_13502 + 1,
			ENSOULED_DOG_HEAD_13469,
			ENSOULED_DOG_HEAD_13469 + 1,
			ENSOULED_DRAGON_HEAD_13511,
			ENSOULED_DRAGON_HEAD_13511 + 1,
			ENSOULED_ELF_HEAD_13481,
			ENSOULED_ELF_HEAD_13481 + 1,
			ENSOULED_GIANT_HEAD_13475,
			ENSOULED_GIANT_HEAD_13475 + 1,
			ENSOULED_GOBLIN_HEAD_13448,
			ENSOULED_GOBLIN_HEAD_13448 + 1,
			ENSOULED_HORROR_HEAD_13487,
			ENSOULED_HORROR_HEAD_13487 + 1,
			ENSOULED_IMP_HEAD_13454,
			ENSOULED_IMP_HEAD_13454 + 1,
			ENSOULED_KALPHITE_HEAD_13490,
			ENSOULED_KALPHITE_HEAD_13490 + 1,
			ENSOULED_MINOTAUR_HEAD_13457,
			ENSOULED_MINOTAUR_HEAD_13457 + 1,
			ENSOULED_MONKEY_HEAD_13451,
			ENSOULED_MONKEY_HEAD_13451 + 1,
			ENSOULED_OGRE_HEAD_13478,
			ENSOULED_OGRE_HEAD_13478 + 1,
			ENSOULED_SCORPION_HEAD_13460,
			ENSOULED_SCORPION_HEAD_13460 + 1,
			ENSOULED_TROLL_HEAD_13484,
			ENSOULED_TROLL_HEAD_13484 + 1,
			ENSOULED_TZHAAR_HEAD_13499,
			ENSOULED_TZHAAR_HEAD_13499 + 1,
			ENSOULED_UNICORN_HEAD_13466,
			ENSOULED_UNICORN_HEAD_13466 + 1
		);
	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private Map.Entry<Rectangle, GroundItemExtended> textBoxBounds;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private Map.Entry<Rectangle, GroundItemExtended> hiddenBoxBounds;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private Map.Entry<Rectangle, GroundItemExtended> highlightBoxBounds;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private boolean hotKeyPressed;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private boolean hideAll;

	private List<String> hiddenItemList = new CopyOnWriteArrayList<>();
	private List<String> highlightedItemsList = new CopyOnWriteArrayList<>();

	@Inject
	private GroundItemExtendedInputListener inputListener;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private GroundItemsExtendedConfig config;

	@Inject
	private GroundItemsExtendedOverlay overlay;

	@Inject
	private Notifier notifier;

	@Inject
	private ScheduledExecutorService executor;

	private LoadingCache<NamedQuantity, Boolean> highlightedItems;

	@Provides
	GroundItemsExtendedConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(GroundItemsExtendedConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		mouseManager.registerMouseListener(inputListener);
		keyManager.registerKeyListener(inputListener);
		executor.execute(this::reset);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		mouseManager.unregisterMouseListener(inputListener);
		keyManager.unregisterKeyListener(inputListener);
		highlightedItems.invalidateAll();
		highlightedItems = null;
		hiddenItems.invalidateAll();
		hiddenItems = null;
		hiddenItemList = null;
		highlightedItemsList = null;
		collectedGroundItems.clear();
	}

	@Subscribe
	private void onGameTick(final GameTick event)
	{
		for (final GroundItemExtended item : collectedGroundItems.values())
		{
			if (item.getTicks() == -1)
			{
				continue;
			}
			item.setTicks(item.getTicks() - 1);
		}
	}

	@Subscribe
	void onConfigChanged(final ConfigChanged event)
	{
		if (event.getGroup().equals("grounditems"))
		{
			executor.execute(this::reset);
		}
	}

	@Subscribe
	private void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING)
		{
			collectedGroundItems.clear();
		}
	}

	@Subscribe
	void onItemSpawned(final ItemSpawned itemSpawned)
	{
		final TileItem item = itemSpawned.getItem();
		final Tile tile = itemSpawned.getTile();

		final GroundItemExtended groundItemExtended = buildGroundItem(tile, item);

		if (groundItemExtended == null)
		{
			return;
		}

		final GroundItemExtended.GroundItemKey groundItemKey = new GroundItemExtended.GroundItemKey(item.getId(), tile.getWorldLocation());
		final GroundItemExtended existing = collectedGroundItems.putIfAbsent(groundItemKey, groundItemExtended);
		if (existing != null)
		{
			existing.setQuantity(existing.getQuantity() + groundItemExtended.getQuantity());
			// The spawn time remains set at the oldest spawn
		}

		if (!config.onlyShowLoot())
		{
			notifyHighlightedItem(groundItemExtended);
		}
	}

	@Subscribe
	private void onItemDespawned(final ItemDespawned itemDespawned)
	{
		final TileItem item = itemDespawned.getItem();
		final Tile tile = itemDespawned.getTile();

		final GroundItemExtended.GroundItemKey groundItemKey = new GroundItemExtended.GroundItemKey(item.getId(), tile.getWorldLocation());
		final GroundItemExtended groundItemExtended = collectedGroundItems.get(groundItemKey);
		if (groundItemExtended == null)
		{
			return;
		}

		if (groundItemExtended.getQuantity() <= item.getQuantity())
		{
			collectedGroundItems.remove(groundItemKey);
		}
		else
		{
			groundItemExtended.setQuantity(groundItemExtended.getQuantity() - item.getQuantity());
			// When picking up an item when multiple stacks appear on the ground,
			// it is not known which item is picked up, so we invalidate the spawn
			// time
			groundItemExtended.setSpawnTime(null);
		}
	}

	@Subscribe
	private void onItemQuantityChanged(final ItemQuantityChanged itemQuantityChanged)
	{
		final TileItem item = itemQuantityChanged.getItem();
		final Tile tile = itemQuantityChanged.getTile();
		final int oldQuantity = itemQuantityChanged.getOldQuantity();
		final int newQuantity = itemQuantityChanged.getNewQuantity();

		final int diff = newQuantity - oldQuantity;
		final GroundItemExtended.GroundItemKey groundItemKey = new GroundItemExtended.GroundItemKey(item.getId(), tile.getWorldLocation());
		final GroundItemExtended groundItemExtended = collectedGroundItems.get(groundItemKey);
		if (groundItemExtended != null)
		{
			groundItemExtended.setQuantity(groundItemExtended.getQuantity() + diff);
		}
	}

	@Subscribe
	private void onNpcLootReceived(final NpcLootReceived npcLootReceived)
	{
		npcLootReceived.getItems().forEach(item ->
			{
				final GroundItemExtended.GroundItemKey groundItemKey = new GroundItemExtended.GroundItemKey(item.getId(), npcLootReceived.getNpc().getWorldLocation());
				if (collectedGroundItems.containsKey(groundItemKey))
				{
					collectedGroundItems.get(groundItemKey).setOwnedByPlayer(true);
				}
			}
		);

		final Collection<ItemStack> items = npcLootReceived.getItems();
		lootReceived(items, LootType.PVM);
		lootNotifier(items);
	}

	@Subscribe
	private void onPlayerLootReceived(final PlayerLootReceived playerLootReceived)
	{
		final Collection<ItemStack> items = playerLootReceived.getItems();
		lootReceived(items, LootType.PVP);
		lootNotifier(items);
	}

	private void lootNotifier(final Collection<ItemStack> items)
	{
		ItemComposition composition;
		for (final ItemStack is : items)
		{
			composition = itemManager.getItemComposition(is.getId());
			final Color itemColor = getHighlighted(new NamedQuantity(composition.getName(), is.getQuantity()), itemManager.getItemPrice(is.getId()) * is.getQuantity(), itemManager.getAlchValue(composition) * is.getQuantity());
			if (itemColor != null)
			{
				if (config.notifyHighlightedDrops() && itemColor.equals(config.highlightedColor()))
				{
					sendLootNotification(composition.getName(), "highlighted");
				}
				else if (config.notifyLowValueDrops() && itemColor.equals(config.lowValueColor()))
				{
					sendLootNotification(composition.getName(), "low value");
				}
				else if (config.notifyMediumValueDrops() && itemColor.equals(config.mediumValueColor()))
				{
					sendLootNotification(composition.getName(), "medium value");
				}
				else if (config.notifyHighValueDrops() && itemColor.equals(config.highValueColor()))
				{
					sendLootNotification(composition.getName(), "high value");
				}
				else if (config.notifyInsaneValueDrops() && itemColor.equals(config.insaneValueColor()))
				{
					sendLootNotification(composition.getName(), "insane value");
				}
			}
		}
	}

	private void sendLootNotification(final String itemName, final String message)
	{
		final Player player = client.getLocalPlayer();

		if (player == null)
		{
			return;
		}

		final String notification = String.format("[%s] Received a %s item: %s", player.getName(), message, itemName);

		notifier.notify(notification);
	}

	@Subscribe
	private void onClientTick(final ClientTick event)
	{
		final MenuEntry[] menuEntries = client.getMenuEntries();
		final List<MenuEntryWithCount> newEntries = new ArrayList<>(menuEntries.length);

		outer:
		for (int i = menuEntries.length - 1; i >= 0; i--)
		{
			final MenuEntry menuEntry = menuEntries[i];

			if (config.collapseEntries())
			{
				final int menuType = menuEntry.getOpcode();
				if (menuType == FIRST_OPTION || menuType == SECOND_OPTION || menuType == THIRD_OPTION
					|| menuType == FOURTH_OPTION || menuType == FIFTH_OPTION || menuType == EXAMINE_ITEM)
				{
					for (final MenuEntryWithCount entryWCount : newEntries)
					{
						if (entryWCount.getEntry().equals(menuEntry))
						{
							entryWCount.increment();
							continue outer;
						}
					}
				}
			}

			newEntries.add(new MenuEntryWithCount(menuEntry));
		}

		Collections.reverse(newEntries);

		newEntries.sort((a, b) ->
		{
			final int aMenuType = a.getEntry().getOpcode();
			if (aMenuType == FIRST_OPTION || aMenuType == SECOND_OPTION || aMenuType == THIRD_OPTION
				|| aMenuType == FOURTH_OPTION || aMenuType == FIFTH_OPTION || aMenuType == EXAMINE_ITEM
				|| aMenuType == WALK)
			{ // only check for item related menu types, so we don't sort other stuff
				final int bMenuType = b.getEntry().getOpcode();
				if (bMenuType == FIRST_OPTION || bMenuType == SECOND_OPTION || bMenuType == THIRD_OPTION
					|| bMenuType == FOURTH_OPTION || bMenuType == FIFTH_OPTION || bMenuType == EXAMINE_ITEM
					|| bMenuType == WALK)
				{
					final MenuEntry aEntry = a.getEntry();
					final int aId = aEntry.getIdentifier();
					final int aQuantity = getCollapsedItemQuantity(aId, aEntry.getTarget());
					final boolean aHidden = isItemIdHidden(aId, aQuantity);

					final MenuEntry bEntry = b.getEntry();
					final int bId = bEntry.getIdentifier();
					final int bQuantity = getCollapsedItemQuantity(bId, bEntry.getTarget());
					final boolean bHidden = isItemIdHidden(bId, bQuantity);

					// only put items below walk if the config is set for it
					if (config.rightClickHidden())
					{
						if (aHidden && bMenuType == WALK)
						{
							return -1;
						}
						if (bHidden && aMenuType == WALK)
						{
							return 1;
						}
					}

					// sort hidden items below non-hidden items
					if (aHidden && !bHidden && bMenuType != WALK)
					{
						return -1;
					}
					if (bHidden && !aHidden && aMenuType != WALK)
					{
						return 1;
					}


					// RS sorts by alch price by private, so no need to sort if config not set
					if (config.sortByGEPrice())
					{
						return (getGePriceFromItemId(aId) * aQuantity) - (getGePriceFromItemId(bId) * bQuantity);
					}
				}
			}

			return 0;
		});

		client.setMenuEntries(newEntries.stream().map(e ->
		{
			final MenuEntry entry = e.getEntry();

			if (config.collapseEntries())
			{
				final int count = e.getCount();
				if (count > 1)
				{
					entry.setTarget(entry.getTarget() + " x " + count);
				}
			}

			return entry;
		}).toArray(MenuEntry[]::new));
	}

	private void lootReceived(final Collection<ItemStack> items, final LootType lootType)
	{
		for (final ItemStack itemStack : items)
		{
			final WorldPoint location = WorldPoint.fromLocal(client, itemStack.getLocation());
			final GroundItemExtended.GroundItemKey groundItemKey = new GroundItemExtended.GroundItemKey(itemStack.getId(), location);
			final GroundItemExtended groundItemExtended = collectedGroundItems.get(groundItemKey);
			if (groundItemExtended != null)
			{
				groundItemExtended.setMine(true);
				groundItemExtended.setTicks(200);
				groundItemExtended.setLootType(lootType);

				if (config.onlyShowLoot())
				{
					notifyHighlightedItem(groundItemExtended);
				}
			}
		}
	}

	@Nullable
	private GroundItemExtended buildGroundItem(final Tile tile, final TileItem item)
	{
		// Collect the data for the item
		final int itemId = item.getId();
		final ItemComposition itemComposition = itemManager.getItemComposition(itemId);
		final int realItemId = itemComposition.getNote() != -1 ? itemComposition.getLinkedNoteId() : itemId;
		final int alchPrice = itemManager.getAlchValue(itemManager.getItemComposition(realItemId));

		final Player player = client.getLocalPlayer();

		if (player == null)
		{
			return null;
		}

		int durationMillis;
		int durationTicks;

		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
		final boolean dropped = tile.getWorldLocation().equals(client.getLocalPlayer().getWorldLocation()) && droppedItemQueue.remove(itemId);

		if (client.isInInstancedRegion())
		{
			if (isInKraken())
			{
				durationMillis = -1;
				durationTicks = -1;
			}
			else if (isInKBDorNMZ())
			{
				// NMZ and the KBD lair uses the same region ID but NMZ uses planes 1-3 and KBD uses plane 0
				if (client.getLocalPlayer().getWorldLocation().getPlane() == 0)
				{
					// Items in the KBD instance use the standard despawn timer
					if (dropped)
					{
						durationTicks = NORMAL_DURATION_TICKS * 3;
						durationMillis = NORMAL_DURATION_MILLIS * 3;
					}
					else
					{
						durationTicks = NORMAL_DURATION_TICKS * 2;
						durationMillis = NORMAL_DURATION_MILLIS * 2;
					}
				}
				else
				{
					// Dropped items in the NMZ instance appear to never despawn?
					if (dropped)
					{
						durationMillis = -1;
						durationTicks = -1;
					}
					else
					{
						durationTicks = NORMAL_DURATION_TICKS * 2;
						durationMillis = NORMAL_DURATION_MILLIS * 2;
					}
				}
			}
			else
			{
				durationMillis = INSTANCE_DURATION_MILLIS;
				durationTicks = INSTANCE_DURATION_TICKS;
			}
		}
		else if (!itemComposition.isTradeable() && realItemId != COINS)
		{
			durationMillis = UNTRADEABLE_DURATION_MILLIS;
			durationTicks = UNTRADEABLE_DURATION_TICKS;
		}
		else
		{
			durationTicks = dropped ? NORMAL_DURATION_TICKS * 3 : NORMAL_DURATION_TICKS * 2;
			durationMillis = dropped ? NORMAL_DURATION_MILLIS * 3 : NORMAL_DURATION_MILLIS * 2;
		}

		final GroundItemExtended groundItemExtended = GroundItemExtended.builder()
			.id(itemId)
			.location(tile.getWorldLocation())
			.itemId(realItemId)
			.quantity(item.getQuantity())
			.name(itemComposition.getName())
			.haPrice(alchPrice)
			.height(-1)
			.tradeable(itemComposition.isTradeable())
			.droppedInstant(Instant.now())
			.durationMillis(durationMillis)
			.isAlwaysPrivate(client.isInInstancedRegion() || (!itemComposition.isTradeable() && realItemId != COINS))
			.isOwnedByPlayer(tile.getWorldLocation().equals(playerLocation))
			.ticks(durationTicks)
			.lootType(dropped ? LootType.DROPPED : LootType.UNKNOWN)
			.spawnTime(Instant.now())
			.stackable(itemComposition.isStackable())
			.build();


		// Update item price in case it is coins
		if (realItemId == COINS)
		{
			groundItemExtended.setHaPrice(1);
			groundItemExtended.setGePrice(1);
		}
		else
		{
			groundItemExtended.setGePrice(itemManager.getItemPrice(realItemId));
		}

		return groundItemExtended;
	}

	private void reset()
	{
		// gets the hidden items from the text box in the config
		hiddenItemList = Text.fromCSV(config.getHiddenItems());

		// gets the highlighted items from the text box in the config
		highlightedItemsList = Text.fromCSV(config.getHighlightItems());

		highlightedItems = CacheBuilder.newBuilder()
			.maximumSize(512L)
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build(new WildcardMatchLoader(highlightedItemsList));

		hiddenItems = CacheBuilder.newBuilder()
			.maximumSize(512L)
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build(new WildcardMatchLoader(hiddenItemList));

		// Cache colors
		final ImmutableList.Builder<PriceHighlight> priceCheckBuilder = ImmutableList.builder();

		if (config.insaneValuePrice() > 0)
		{
			priceCheckBuilder.add(new PriceHighlight(config.insaneValuePrice(), config.insaneValueColor()));
		}

		if (config.highValuePrice() > 0)
		{
			priceCheckBuilder.add(new PriceHighlight(config.highValuePrice(), config.highValueColor()));
		}

		if (config.mediumValuePrice() > 0)
		{
			priceCheckBuilder.add(new PriceHighlight(config.mediumValuePrice(), config.mediumValueColor()));
		}

		if (config.lowValuePrice() > 0)
		{
			priceCheckBuilder.add(new PriceHighlight(config.lowValuePrice(), config.lowValueColor()));
		}

		priceChecks = priceCheckBuilder.build();
	}

	@Subscribe
	private void onMenuEntryAdded(final MenuEntryAdded lastEntry)
	{
		if (config.itemHighlightMode() != ItemHighlightMode.OVERLAY)
		{
			final boolean telegrabEntry = lastEntry.getOption().equals("Cast") && lastEntry.getTarget().startsWith(TELEGRAB_TEXT) && lastEntry.getOpcode() == CAST_ON_ITEM;
			if (!(lastEntry.getOption().equals("Take") && lastEntry.getOpcode() == THIRD_OPTION) && !telegrabEntry)
			{
				return;
			}

			final int itemId = lastEntry.getIdentifier();
			final int sceneX = lastEntry.getParam0();
			final int sceneY = lastEntry.getParam1();

			final WorldPoint worldPoint = WorldPoint.fromScene(client, sceneX, sceneY, client.getPlane());
			final GroundItemExtended.GroundItemKey groundItemKey = new GroundItemExtended.GroundItemKey(itemId, worldPoint);
			final GroundItemExtended groundItemExtended = collectedGroundItems.get(groundItemKey);

			if (groundItemExtended == null)
			{
				return;
			}

			final int quantity = groundItemExtended.getQuantity();

			final int gePrice = groundItemExtended.getGePrice();
			final int haPrice = groundItemExtended.getHaPrice();
			final Color hidden = getHidden(new NamedQuantity(groundItemExtended.getName(), quantity), gePrice, haPrice, groundItemExtended.isTradeable());
			final Color highlighted = getHighlighted(new NamedQuantity(groundItemExtended.getName(), quantity), gePrice, haPrice);
			final Color color = getItemColor(highlighted, hidden);
			final boolean canBeRecolored = highlighted != null || (hidden != null && config.recolorMenuHiddenItems());

			if (color != null && canBeRecolored && !color.equals(config.defaultColor()))
			{
				final net.runelite.client.plugins.grounditemsextended.config.MenuHighlightMode mode = config.menuHighlightMode();

				if (mode == net.runelite.client.plugins.grounditemsextended.config.MenuHighlightMode.BOTH || mode == net.runelite.client.plugins.grounditemsextended.config.MenuHighlightMode.OPTION)
				{
					final String optionText = telegrabEntry ? "Cast" : "Take";
					lastEntry.setOption(ColorUtil.prependColorTag(optionText, color));
					lastEntry.setModified();
				}

				if (mode == net.runelite.client.plugins.grounditemsextended.config.MenuHighlightMode.BOTH || mode == net.runelite.client.plugins.grounditemsextended.config.MenuHighlightMode.NAME)
				{
					String target = lastEntry.getTarget();

					if (telegrabEntry)
					{
						target = target.substring(TELEGRAB_TEXT.length());
					}

					target = ColorUtil.prependColorTag(target.substring(target.indexOf('>') + 1), color);

					if (telegrabEntry)
					{
						target = TELEGRAB_TEXT + target;
					}

					lastEntry.setTarget(target);
					lastEntry.setModified();
				}
			}

			if (config.showMenuItemQuantities() && groundItemExtended.isStackable() && quantity > 1)
			{
				lastEntry.setTarget(lastEntry.getTarget() + " (" + quantity + ")");
				lastEntry.setModified();
			}

			if (config.removeIgnored() && lastEntry.getOption().equals("Take") && hiddenItemList.contains(Text.removeTags(lastEntry.getTarget())))
			{
				client.setMenuOptionCount(client.getMenuOptionCount() - 1);
			}
		}
	}

	void updateList(final String item, final boolean hiddenList)
	{
		final List<String> hiddenItemSet = new ArrayList<>(hiddenItemList);
		final List<String> highlightedItemSet = new ArrayList<>(highlightedItemsList);

		if (hiddenList)
		{
			highlightedItemSet.removeIf(item::equalsIgnoreCase);
		}
		else
		{
			hiddenItemSet.removeIf(item::equalsIgnoreCase);
		}

		final List<String> items = hiddenList ? hiddenItemSet : highlightedItemSet;

		if (!items.removeIf(item::equalsIgnoreCase))
		{
			items.add(item);
		}

		config.setHiddenItems(Text.toCSV(hiddenItemSet));
		config.setHighlightedItem(Text.toCSV(highlightedItemSet));
	}

	Color getHerbloreColor()
	{
		return config.herbloreColor();
	}

	Color getPrayerColor()
	{
		return config.prayerColor();
	}

	Color getHighlighted(final NamedQuantity item, final int gePrice, final int haPrice)
	{
		if (TRUE.equals(highlightedItems.getUnchecked(item)))
		{
			return config.highlightedColor();
		}

		// Explicit hide takes priority over implicit highlight
		if (TRUE.equals(hiddenItems.getUnchecked(item)))
		{
			return null;
		}

		final int price = getValueByMode(gePrice, haPrice);
		for (final PriceHighlight highlight : priceChecks)
		{
			if (price > highlight.getPrice())
			{
				return highlight.getColor();
			}
		}

		return null;
	}

	Color getHidden(final NamedQuantity item, final int gePrice, final int haPrice, final boolean isTradeable)
	{
		final boolean isExplicitHidden = TRUE.equals(hiddenItems.getUnchecked(item));
		final boolean isExplicitHighlight = TRUE.equals(highlightedItems.getUnchecked(item));
		final boolean canBeHidden = gePrice > 0 || isTradeable || !config.dontHideUntradeables();
		final boolean underGe = gePrice < config.getHideUnderValue();
		final boolean underHa = haPrice < config.getHideUnderValue();

		// Explicit highlight takes priority over implicit hide
		return isExplicitHidden || (!isExplicitHighlight && canBeHidden && underGe && underHa)
			? config.hiddenColor()
			: null;
	}

	private int getGePriceFromItemId(final int itemId)
	{
		final ItemComposition itemComposition = itemManager.getItemComposition(itemId);
		final int realItemId = itemComposition.getNote() != -1 ? itemComposition.getLinkedNoteId() : itemId;

		return itemManager.getItemPrice(realItemId);
	}

	private boolean isItemIdHidden(final int itemId, final int quantity)
	{
		final ItemComposition itemComposition = itemManager.getItemComposition(itemId);
		final int realItemId = itemComposition.getNote() != -1 ? itemComposition.getLinkedNoteId() : itemId;
		final int alchPrice = itemManager.getAlchValue(itemManager.getItemComposition(realItemId)) * quantity;
		final int gePrice = itemManager.getItemPrice(realItemId) * quantity;

		return getHidden(new NamedQuantity(itemComposition.getName(), quantity), gePrice, alchPrice, itemComposition.isTradeable()) != null;
	}

	private int getCollapsedItemQuantity(final int itemId, final String item)
	{
		final ItemComposition itemComposition = itemManager.getItemComposition(itemId);
		final boolean itemNameIncludesQuantity = Pattern.compile("\\(\\d+\\)").matcher(itemComposition.getName()).find();

		final Matcher matcher = Pattern.compile("\\((\\d+)\\)").matcher(item);
		int matches = 0;
		String lastMatch = "1";
		while (matcher.find())
		{
			// so that "Prayer Potion (4)" returns 1 instead of 4 and "Coins (25)" returns 25 instead of 1
			if (!itemNameIncludesQuantity || matches >= 1)
			{
				lastMatch = matcher.group(1);
			}

			matches++;
		}

		return Integer.parseInt(lastMatch);
	}

	Color getItemColor(final Color highlighted, final Color hidden)
	{
		if (highlighted != null)
		{
			return highlighted;
		}

		if (hidden != null)
		{
			return hidden;
		}

		return config.defaultColor();
	}

	@Subscribe
	private void onFocusChanged(final FocusChanged focusChanged)
	{
		if (!focusChanged.isFocused())
		{
			setHotKeyPressed(false);
		}
	}

	@Subscribe
	private void onMenuOptionClicked(final MenuOptionClicked menuOptionClicked)
	{
		if (menuOptionClicked.getMenuAction() == MenuAction.ITEM_FIFTH_OPTION)
		{
			final int itemId = menuOptionClicked.getId();
			// Keep a queue of recently dropped items to better detect
			// item spawns that are drops
			droppedItemQueue.add(itemId);
		}
	}

	private void notifyHighlightedItem(final GroundItemExtended item)
	{
		final boolean shouldNotifyHighlighted = config.notifyHighlightedDrops() &&
			TRUE.equals(highlightedItems.getUnchecked(new NamedQuantity(item)));

		final boolean shouldNotifyTier = config.notifyTier() != HighlightTier.OFF &&
			getValueByMode(item.getGePrice(), item.getHaPrice()) > config.notifyTier().getValueFromTier(config) &&
			FALSE.equals(hiddenItems.getUnchecked(new NamedQuantity(item)));

		final String dropType;
		if (shouldNotifyHighlighted)
		{
			dropType = "highlighted";
		}
		else if (shouldNotifyTier)
		{
			dropType = "valuable";
		}
		else
		{
			return;
		}

		final Player local = client.getLocalPlayer();

		if (local == null)
		{
			return;
		}

		final StringBuilder notificationStringBuilder = new StringBuilder()
			.append("[")
			.append(local.getName())
			.append("] received a ")
			.append(dropType)
			.append(" drop: ")
			.append(item.getName());

		if (item.getQuantity() > 1)
		{
			if (item.getQuantity() >= MAX_QUANTITY)
			{
				notificationStringBuilder.append(" (Lots!)");
			}
			else
			{
				notificationStringBuilder.append(" (")
					.append(QuantityFormatter.quantityToStackSize(item.getQuantity()))
					.append(")");
			}
		}

		notifier.notify(notificationStringBuilder.toString());
	}

	private int getValueByMode(final int gePrice, final int haPrice)
	{
		switch (config.valueCalculationMode())
		{
			case GE:
				return gePrice;
			case HA:
				return haPrice;
			default: // Highest
				return Math.max(gePrice, haPrice);
		}
	}

	private boolean isInKraken()
	{
		return ArrayUtils.contains(client.getMapRegions(), KRAKEN_REGION);
	}

	private boolean isInKBDorNMZ()
	{
		return ArrayUtils.contains(client.getMapRegions(), KBD_NMZ_REGION);
	}

	public Widget getViewportLayer()
	{
		if (client.isResized())
		{
			if (client.getVarbitValue(Varbits.SIDE_PANELS) == 1)
			{
				return client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE);
			}
			else
			{
				return client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_OLD_SCHOOL_BOX);
			}
		}
		return client.getWidget(WidgetInfo.FIXED_VIEWPORT);
	}
}
