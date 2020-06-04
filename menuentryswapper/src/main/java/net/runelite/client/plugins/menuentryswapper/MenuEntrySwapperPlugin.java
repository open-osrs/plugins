/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Kamiel
 * Copyright (c) 2019, alanbaumgartner <https://github.com/alanbaumgartner>
 * Copyright (c) 2019, Kyle <https://github.com/kyleeld>
 * Copyright (c) 2019, Lucas <https://github.com/lucwousin>
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
package net.runelite.client.plugins.menuentryswapper;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import static net.runelite.api.MenuOpcode.MENU_ACTION_DEPRIORITIZE_OFFSET;
import static net.runelite.api.MenuOpcode.WALK;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import static net.runelite.api.Varbits.WITHDRAW_X_AMOUNT;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.util.Text;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.menus.AbstractComparableEntry;
import net.runelite.client.menus.BankComparableEntry;
import net.runelite.client.menus.BaseComparableEntry;
import static net.runelite.client.menus.ComparableEntries.newBankComparableEntry;
import static net.runelite.client.menus.ComparableEntries.newBaseComparableEntry;
import net.runelite.client.menus.InventoryComparableEntry;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.menus.ShopComparableEntry;
import net.runelite.client.menus.WithdrawComparableEntry;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.menuentryswapper.comparables.GrimyHerbComparableEntry;
import net.runelite.client.util.HotkeyListener;
import static net.runelite.client.util.MenuUtil.swap;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Menu Entry Swapper",
	enabledByDefault = false,
	description = "Change the default option that is displayed when hovering over objects",
	tags = {"npcs", "inventory", "items", "objects"},
	type = PluginType.UTILITY
)

public class MenuEntrySwapperPlugin extends Plugin
{
	private static final Object HOTKEY = new Object();
	private static final Object CONTROL = new Object();
	private static final Object HOTKEY_CHECK = new Object();
	private static final Object CONTROL_CHECK = new Object();
	private static final int PURO_PURO_REGION_ID = 10307;
	private static final BankComparableEntry POUCH = new BankComparableEntry("fill", "pouch", false);
	private static final BaseComparableEntry EMPTY_SMALL = newBaseComparableEntry("empty", "small pouch");
	private static final BaseComparableEntry EMPTY_MEDIUM = newBaseComparableEntry("empty", "medium pouch");
	private static final BaseComparableEntry EMPTY_LARGE = newBaseComparableEntry("empty", "large pouch");
	private static final BaseComparableEntry EMPTY_GIANT = newBaseComparableEntry("empty", "giant pouch");
	private static final Set<MenuOpcode> NPC_MENU_TYPES = Set.of(
		MenuOpcode.NPC_FIRST_OPTION, MenuOpcode.NPC_SECOND_OPTION, MenuOpcode.NPC_THIRD_OPTION,
		MenuOpcode.NPC_FOURTH_OPTION, MenuOpcode.NPC_FIFTH_OPTION, MenuOpcode.EXAMINE_NPC
	);
	private static final List<String> jewelleryBox = Arrays.asList(
		"duel arena", "castle wars", "clan wars", "burthorpe", "barbarian outpost", "corporeal beast",
		"tears of guthix", "wintertodt camp", "warriors' guild", "champions' guild", "monastery", "ranging guild",
		"fishing guild", "mining guild", "crafting guild", "cooking guild", "woodcutting guild", "farming guild",
		"miscellania", "grand exchange", "falador park", "dondakan's rock", "edgeville", "karamja",
		"draynor village", "al kharid"
	);
	private static final List<String> pharaohsSceptre = Arrays.asList(
		"Pharaoh's Sceptre (3)", "Pharaoh's Sceptre (2)", "Pharaoh's Sceptre (1)", "Pharaoh's Sceptre"
	);
	private static final List<String> npcContact = Arrays.asList(
		"honest jimmy", "bert the sandman", "advisor ghrim", "dark mage", "lanthus", "turael",
		"mazchna", "vannaka", "chaeldar", "nieve", "steve", "duradel", "krystilia", "konar",
		"murphy", "cyrisus", "smoggy", "ginea", "watson", "barbarian guard", "random"
	);
	private static final List<String> dropFish = Arrays.asList(
		"Raw shrimp", "Raw Sardine", "Raw karambwanji", "Raw herring", "Raw anchovies", "Raw mackerel",
		"Raw trout", "Raw cod", "Raw pike", "Raw slimy eel", "Raw salmon", "Raw tuna", "Raw rainbow fish",
		"Raw cave eellobster", "Raw bluegill", "Raw bass", "Leaping trout", "Raw swordfish", "Raw lava eel",
		"Raw common tench", "Leaping salmon", "Raw monkfish", "Raw karambwan", "Leaping sturgeon",
		"Raw mottled eel", "Raw shark", "Raw sea turtle", "Raw infernal eel", "Raw manta ray", "Raw angler fish",
		"Raw dark crab", "Raw sacred eel"
	);
	private static final List<String> dropOre = Arrays.asList(
		"Copper ore", "Tin ore", "Limestone", "Blurite ore", "Iron ore", "Elemental ore", "Daeyalt ore",
		"Silver ore", "Coal", "Sandstone", "Gold ore", "Granite (500g)", "Granite (2kg)", "Granite (5kg)",
		"Mithril ore", "Lovakite ore", "Adamantite ore", "Runite ore", "Amethyst ore"
	);
	private static final List<String> dropLogs = Arrays.asList(
		"Logs", "Achey tree logs", "Oak logs", "Willow logs", "Teak logs", "Juniper logs", "Maple logs",
		"Mahogany logs", "Artic pine logs", "Yew logs", "Magic logs", "Redwood logs"
	);

	private static final Splitter NEWLINE_SPLITTER = Splitter
		.on("\n")
		.omitEmptyStrings()
		.trimResults();
	private final Map<AbstractComparableEntry, Integer> customSwaps = new HashMap<>();
	private final Map<AbstractComparableEntry, Integer> customShiftSwaps = new HashMap<>();
	private final Map<AbstractComparableEntry, AbstractComparableEntry> dePrioSwaps = new HashMap<>();
	// 1, 5, 10, 50
	private final AbstractComparableEntry[][] buyEntries = new AbstractComparableEntry[4][];
	private final AbstractComparableEntry[][] sellEntries = new AbstractComparableEntry[4][];
	// 1, 5, 10, X, All
	private final AbstractComparableEntry[][] withdrawEntries = new AbstractComparableEntry[5][];

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private MenuEntrySwapperConfig config;

	@Inject
	private PluginManager pluginManager;

	@Inject
	private MenuManager menuManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private EventBus eventBus;

	@Setter(AccessLevel.PRIVATE)
	private boolean hotkeyActive;
	@Setter(AccessLevel.PRIVATE)
	private boolean controlActive;
	private String[] removedObjects;

	private List<String> bankItemNames = new ArrayList<>();
	private final HotkeyListener hotkey = new HotkeyListener(() -> config.hotkeyMod())
	{
		@Override
		public void hotkeyPressed()
		{
			startHotkey();
			setHotkeyActive(true);
		}

		@Override
		public void hotkeyReleased()
		{
			stopHotkey();
			setHotkeyActive(false);
		}
	};
	private final HotkeyListener ctrlHotkey = new HotkeyListener(() -> Keybind.CTRL)
	{
		@Override
		public void hotkeyPressed()
		{
			startControl();
			setControlActive(true);
		}

		@Override
		public void hotkeyReleased()
		{
			stopControl();
			setControlActive(false);
		}
	};

	@Provides
	MenuEntrySwapperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MenuEntrySwapperConfig.class);
	}

	@Override
	public void startUp()
	{
		addSwaps();
		loadCustomSwaps(config.customSwaps(), customSwaps);

		updateBuySellEntries();
		addBuySellEntries();

		updateWithdrawEntries();
		addWithdrawEntries();

		updateRemovedObjects();

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			keyManager.registerKeyListener(ctrlHotkey);
			keyManager.registerKeyListener(hotkey);
		}
	}

	@Override
	public void shutDown()
	{
		loadCustomSwaps("", customSwaps); // Removes all custom swaps
		removeSwaps();
		removeBuySellEntries();
		removeWithdrawEntries();

		removedObjects = null;

		keyManager.unregisterKeyListener(ctrlHotkey);
		keyManager.unregisterKeyListener(hotkey);
	}

	@Subscribe
	private void onFocusChanged(FocusChanged event)
	{
		if (!event.isFocused())
		{
			stopControl();
			stopHotkey();
		}
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (!"menuentryswapper".equals(event.getGroup()))
		{
			return;
		}

		removeSwaps();
		addSwaps();

		switch (event.getKey())
		{
			case "customSwaps":
				loadCustomSwaps(config.customSwaps(), customSwaps);
				return;
			case "removeObjects":
			case "removedObjects":
				updateRemovedObjects();
				return;
		}

		if (event.getKey().startsWith("swapSell") || event.getKey().startsWith("swapBuy") ||
			(event.getKey().startsWith("sell") || event.getKey().startsWith("buy")) && event.getKey().endsWith("Items"))
		{
			removeBuySellEntries();
			updateBuySellEntries();
			addBuySellEntries();
		}
		else if (event.getKey().startsWith("withdraw") || event.getKey().startsWith("deposit"))
		{
			removeWithdrawEntries();
			updateWithdrawEntries();
			addWithdrawEntries();
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			keyManager.unregisterKeyListener(ctrlHotkey);
			keyManager.unregisterKeyListener(hotkey);
			return;
		}

		keyManager.registerKeyListener(ctrlHotkey);
		keyManager.registerKeyListener(hotkey);
	}

	@Subscribe
	private void onVarbitChanged(VarbitChanged event)
	{
		WithdrawComparableEntry.setX(client.getVar(WITHDRAW_X_AMOUNT));
	}

	@Subscribe
	private void onMenuOpened(MenuOpened event)
	{
		Player localPlayer = client.getLocalPlayer();

		if (localPlayer == null)
		{
			return;
		}

		List<MenuEntry> menu_entries = new ArrayList<>();

		for (MenuEntry entry : event.getMenuEntries())
		{
			String option = Text.removeTags(entry.getOption()).toLowerCase();

			if (option.contains("examine") && config.hideExamine())
			{
				continue;
			}

			if (option.contains("net") && config.hideNet())
			{
				continue;
			}

			if (option.contains("bait") && config.hideBait())
			{
				continue;
			}

			if (option.contains("destroy"))
			{
				if (config.hideDestroyRunepouch() && entry.getTarget().contains("Rune pouch"))
				{
					continue;
				}
				if (config.hideDestroyCoalbag() && (entry.getTarget().contains("Coal bag") || entry.getTarget().contains("Open coal sack")))
				{
					continue;
				}
				if (config.hideDestroyHerbsack() && (entry.getTarget().contains("Herb sack") || entry.getTarget().contains("Open herb sack")))
				{
					continue;
				}
				if (config.hideDestroyBoltpouch() && entry.getTarget().contains("Bolt pouch"))
				{
					continue;
				}
				if (config.hideDestroyLootingBag() && entry.getTarget().contains("Looting bag"))
				{
					continue;
				}
				if (config.hideDestroyGembag() && (entry.getTarget().contains("Gem bag") || entry.getTarget().contains("Open gem bag")))
				{
					continue;
				}
			}

			if (option.contains("restore"))
			{
				if (config.hideRestoreTanzaniteHelm() && entry.getTarget().contains("Tanzanite helm"))
				{
					continue;
				}
				if (config.hideRestoreMagmaHelm() && entry.getTarget().contains("Magma helm"))
				{
					continue;
				}
			}

			if (option.contains("drop"))
			{
				if (config.hideDropRunecraftingPouch() && (
					entry.getTarget().contains("Small pouch")
						|| entry.getTarget().contains("Medium pouch")
						|| entry.getTarget().contains("Large pouch")
						|| entry.getTarget().contains("Giant pouch")))
				{
					continue;
				}
			}

			menu_entries.add(entry);
		}

		event.setMenuEntries(menu_entries.toArray(new MenuEntry[0]));
		event.setModified();
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		final int eventId = event.getIdentifier();
		final String option = event.getOption().toLowerCase();
		final String target = Text.standardize(event.getTarget(), true);
		final NPC hintArrowNpc = client.getHintArrowNpc();

		if (removedObjects != null)
		{
			final boolean hasArrow = target.contains("->");
			final int targetLength = target.length();

			for (final String object : removedObjects)
			{
				if (target.equals(object)
					|| hasArrow && target.endsWith(object))
				{
					client.setMenuOptionCount(client.getMenuOptionCount() - 1);
					return;
				}
			}
		}

		if (config.getSwapPuro() && isPuroPuro())
		{
			if (event.getOpcode() == WALK.getId())
			{
				MenuEntry[] menuEntries = client.getMenuEntries();
				MenuEntry menuEntry = menuEntries[menuEntries.length - 1];
				menuEntry.setOpcode(MenuOpcode.WALK.getId() + MENU_ACTION_DEPRIORITIZE_OFFSET);
				client.setMenuEntries(menuEntries);
			}
			else if (option.equalsIgnoreCase("examine"))
			{
				swap(client, "push-through", option, target);
			}
			else if (option.equalsIgnoreCase("use"))
			{
				swap(client, "escape", option, target);
			}
		}

		if (config.deprioritizeChopDown() && event.getOption().contains("Chop down"))
		{
			MenuEntry[] menuEntries = client.getMenuEntries();
			MenuEntry menuEntry = menuEntries[menuEntries.length - 1];
			menuEntry.setOpcode(MenuOpcode.WALK.getId() + MENU_ACTION_DEPRIORITIZE_OFFSET);
			client.setMenuEntries(menuEntries);
		}

		if (hintArrowNpc != null
			&& hintArrowNpc.getIndex() == eventId
			&& NPC_MENU_TYPES.contains(MenuOpcode.of(event.getOpcode())))
		{
			return;
		}

		if (config.swapTeleportNames())
		{
			MenuEntry[] menuEntries = client.getMenuEntries();
			for (MenuEntry object : menuEntries)
			{
				switch (object.getOption())
				{
					case "Jalsavrah":
						object.setOption("Pyramid Plunder");
						break;
					case "Jaleustrophos":
						object.setOption("Agility Pyramid");
						break;
					case "Jaldraocht":
						object.setOption("Desert Treasure Pyramid");
						break;
				}
				if (object.getTarget().contains("Kharyrll teleport"))
				{
					object.setTarget("<col=40b3ff>Canifis teleport");
				}
				if (object.getTarget().contains("Senntisten teleport"))
				{
					object.setTarget("<col=40b3ff>Digsite teleport");
				}
				if (object.getTarget().contains("Lassar teleport"))
				{
					object.setTarget("<col=40b3ff>Ice Mountain teleport");
				}
				if (object.getTarget().contains("Ghorrock teleport"))
				{
					object.setTarget("<col=40b3ff>Ice Plateau teleport");
				}
				if (object.getTarget().contains("Paddewwa teleport"))
				{
					object.setTarget("<col=40b3ff>Edgeville Dungeon teleport");
				}
				if (object.getTarget().contains("Dareeyak teleport"))
				{
					object.setTarget("<col=40b3ff>Crazy Archeologist Ruins teleport");
				}
				if (object.getTarget().contains("Annakarl teleport"))
				{
					object.setTarget("<col=40b3ff>Demonic Ruins teleport");
				}
				if (object.getTarget().contains("Carrallangar teleport"))
				{
					object.setTarget("<col=40b3ff>Graveyard of Shadows teleport");
				}
				if (object.getTarget().contains("Icy basalt"))
				{
					object.setTarget("<col=40b3ff>Weiss teleport");
				}
				if (object.getTarget().contains("Stony basalt"))
				{
					object.setTarget("<col=40b3ff>Troll Stronghold teleport");
				}
			}
			client.setMenuEntries(menuEntries);
		}

		if (config.swapImps() && target.contains("impling"))
		{

			if (client.getItemContainer(InventoryID.BANK) != null)
			{
				bankItemNames = new ArrayList<>();
				for (Item i : Objects.requireNonNull(client.getItemContainer(InventoryID.BANK)).getItems())
				{
					bankItemNames.add(client.getItemDefinition((i.getId())).getName());
				}
			}
			List<String> invItemNames = new ArrayList<>();
			switch (target)
			{
				case "gourmet impling jar":
					if (client.getItemContainer(InventoryID.INVENTORY) != null)
					{
						for (Item i : Objects.requireNonNull(client.getItemContainer(InventoryID.INVENTORY)).getItems())
						{
							invItemNames.add(client.getItemDefinition((i.getId())).getName());
						}
						if ((invItemNames.contains("Clue scroll (easy)") || bankItemNames.contains("Clue scroll (easy)")))
						{
							menuManager.addSwap("loot", target, "use");
						}
						else
						{
							menuManager.removeSwap("loot", target, "use");
						}
					}
					break;
				case "young impling jar":
					if (client.getItemContainer(InventoryID.INVENTORY) != null)
					{
						for (Item i : Objects.requireNonNull(client.getItemContainer(InventoryID.INVENTORY)).getItems())
						{
							invItemNames.add(client.getItemDefinition((i.getId())).getName());
						}
						if (invItemNames.contains("Clue scroll (easy)") || bankItemNames.contains("Clue scroll (easy)") || invItemNames.contains("Clue scroll (beginner)") || bankItemNames.contains("Clue scroll (beginner)"))
						{
							menuManager.addSwap("loot", target, "use");
						}
						else
						{
							menuManager.removeSwap("loot", target, "use");
						}
					}
					break;
				case "eclectic impling jar":
					if (client.getItemContainer(InventoryID.INVENTORY) != null)
					{
						for (Item i : Objects.requireNonNull(client.getItemContainer(InventoryID.INVENTORY)).getItems())
						{
							invItemNames.add(client.getItemDefinition((i.getId())).getName());
						}
						if ((invItemNames.contains("Clue scroll (medium)") || bankItemNames.contains("Clue scroll (medium)")))
						{
							menuManager.addSwap("loot", target, "use");
						}
						else
						{
							menuManager.removeSwap("loot", target, "use");
						}
					}
					break;
				case "magpie impling jar":
				case "nature impling jar":
				case "ninja impling jar":
					if (client.getItemContainer(InventoryID.INVENTORY) != null)
					{
						for (Item i : Objects.requireNonNull(client.getItemContainer(InventoryID.INVENTORY)).getItems())
						{
							invItemNames.add(client.getItemDefinition((i.getId())).getName());
						}
						if ((invItemNames.contains("Clue scroll (hard)") || bankItemNames.contains("Clue scroll (hard)")))
						{
							menuManager.addSwap("loot", target, "use");
						}
						else
						{
							menuManager.removeSwap("loot", target, "use");
						}
					}
					break;
				case "crystal impling jar":
				case "dragon impling jar":
					if (client.getItemContainer(InventoryID.INVENTORY) != null)
					{
						for (Item i : Objects.requireNonNull(client.getItemContainer(InventoryID.INVENTORY)).getItems())
						{
							invItemNames.add(client.getItemDefinition((i.getId())).getName());
						}
						if ((invItemNames.contains("Clue scroll (elite)") || bankItemNames.contains("Clue scroll (elite)")))
						{
							menuManager.addSwap("loot", target, "use");
						}
						else
						{
							menuManager.removeSwap("loot", target, "use");
						}
					}
					break;
			}
		}
	}

	private void loadCustomSwaps(String config, Map<AbstractComparableEntry, Integer> map)
	{
		final Map<AbstractComparableEntry, Integer> tmp = new HashMap<>();

		if (!Strings.isNullOrEmpty(config))
		{
			final StringBuilder sb = new StringBuilder();

			for (String str : config.split("\n"))
			{
				if (!str.startsWith("//"))
				{
					sb.append(str).append("\n");
				}
			}

			final Map<String, String> split = NEWLINE_SPLITTER.withKeyValueSeparator(':').split(sb);

			for (Map.Entry<String, String> entry : split.entrySet())
			{
				final String prio = entry.getKey();
				int priority;
				try
				{
					priority = Integer.parseInt(entry.getValue().trim());
				}
				catch (NumberFormatException e)
				{
					priority = 0;
				}
				final String[] splitFrom = Text.standardize(prio).split(",");
				final String optionFrom = splitFrom[0].trim();
				final String targetFrom;
				if (splitFrom.length == 1)
				{
					targetFrom = "";
				}
				else
				{
					targetFrom = splitFrom[1].trim();
				}

				final AbstractComparableEntry prioEntry = newBaseComparableEntry(optionFrom, targetFrom);

				tmp.put(prioEntry, priority);
			}
		}

		for (Map.Entry<AbstractComparableEntry, Integer> e : map.entrySet())
		{
			final AbstractComparableEntry key = e.getKey();
			menuManager.removePriorityEntry(key);
		}

		map.clear();
		map.putAll(tmp);

		for (Map.Entry<AbstractComparableEntry, Integer> entry : map.entrySet())
		{
			AbstractComparableEntry a1 = entry.getKey();
			int a2 = entry.getValue();
			menuManager.addPriorityEntry(a1).setPriority(a2);
		}
	}

	private void addSwaps()
	{

		if (config.getSwapTanning())
		{
			menuManager.addPriorityEntry("Tan <col=ff7000>All");
		}

		if (config.getSwapOffer())
		{
			menuManager.addPriorityEntry("Offer-All<col=ff9040>");
		}

		if (config.getSwapSawmill())
		{
			menuManager.addPriorityEntry("Buy-plank", "Sawmill operator");
		}

		if (config.getSwapSawmillPlanks())
		{
			//Not much we can do for this one, Buy all is the only thing, there is no target.
			menuManager.addPriorityEntry("Buy <col=ff7000>All").setPriority(10);
		}

		if (config.getSwapArdougneCloak())
		{
			menuManager.addPriorityEntry(config.ardougneCloakMode().toString()).setPriority(100);
			menuManager.addPriorityEntry(config.ardougneCloakMode().toString2()).setPriority(100);
		}

		if (config.getSwapCraftingCape())
		{
			menuManager.addPriorityEntry("Teleport", "Crafting cape");
			menuManager.addPriorityEntry("Teleport", "Crafting cape(t)");
		}

		if (config.getSwapConstructionCape())
		{
			menuManager.addPriorityEntry(config.constructionCapeMode().toString(), "Construct. cape").setPriority(100);
			menuManager.addPriorityEntry(config.constructionCapeMode().toString(), "Construct. cape(t)").setPriority(100);
		}

		if (config.getSwapMagicCape())
		{
			menuManager.addPriorityEntry("Spellbook", "Magic cape");
			menuManager.addPriorityEntry("Spellbook", "Magic cape(t)");
		}

		if (config.getSwapExplorersRing())
		{
			menuManager.addPriorityEntry("Teleport", "Explorer's ring 2");
			menuManager.addPriorityEntry("Teleport", "Explorer's ring 3");
			menuManager.addPriorityEntry("Teleport", "Explorer's ring 4");
		}

		if (config.swapHardWoodGrove())
		{
			menuManager.addPriorityEntry("Send-parcel", "Rionasta");
		}

		if (config.swapBankExchange())
		{
			menuManager.addPriorityEntry(new BankComparableEntry("collect", "", false));
			menuManager.addPriorityEntry("Bank");
			menuManager.addPriorityEntry("Exchange");
		}

		if (config.swapContract())
		{
			menuManager.addPriorityEntry("Contract").setPriority(10);
		}

		if (config.swapInteract())
		{
			menuManager.addPriorityEntry("Repairs").setPriority(10);
			menuManager.addPriorityEntry("Claim-slime").setPriority(10);
			menuManager.addPriorityEntry("Decant").setPriority(10);
			menuManager.addPriorityEntry("Claim").setPriority(10);
			menuManager.addPriorityEntry("Heal").setPriority(10);
			menuManager.addPriorityEntry("Help").setPriority(10);
		}

		if (config.swapAssignment())
		{
			menuManager.addPriorityEntry("Assignment").setPriority(100);
		}

		if (config.swapPlank())
		{
			menuManager.addPriorityEntry("Buy-plank").setPriority(10);
		}

		if (config.swapTrade())
		{
			menuManager.addPriorityEntry("Trade").setPriority(1);
			menuManager.addPriorityEntry("Trade-with").setPriority(1);
			menuManager.addPriorityEntry("Shop").setPriority(1);
		}

		if (config.swapMinigame())
		{
			menuManager.addPriorityEntry("Story");
			menuManager.addPriorityEntry("Escort");
			menuManager.addPriorityEntry("Dream");
			menuManager.addPriorityEntry("Start-minigame");
		}

		if (config.swapTravel())
		{
			menuManager.addPriorityEntry("Travel").setPriority(10);
			menuManager.addPriorityEntry("Pay-fare").setPriority(10);
			menuManager.addPriorityEntry("Charter").setPriority(10);
			menuManager.addPriorityEntry("Take-boat").setPriority(10);
			menuManager.addPriorityEntry("Fly").setPriority(10);
			menuManager.addPriorityEntry("Jatizso").setPriority(10);
			menuManager.addPriorityEntry("Neitiznot").setPriority(10);
			menuManager.addPriorityEntry("Rellekka").setPriority(10);
			menuManager.addPriorityEntry("Follow", "Elkoy").setPriority(10);
			menuManager.addPriorityEntry("Transport").setPriority(10);
		}

		if (config.swapAbyssTeleport())
		{
			menuManager.addPriorityEntry("Teleport", "Mage of zamorak").setPriority(10);
		}

		if (config.swapPay())
		{
			menuManager.addPriorityEntry("Pay");
			menuManager.addPriorityEntry("Pay (", false);
		}

		if (config.swapQuick())
		{
			menuManager.addPriorityEntry("Quick-travel");
		}

		if (config.swapEnchant())
		{
			menuManager.addPriorityEntry("Enchant");
		}

		if (config.swapWildernessLever())
		{
			menuManager.addPriorityEntry("Edgeville", "Lever");
		}

		if (config.swapMetamorphosis())
		{
			menuManager.addPriorityEntry("Metamorphosis", "Baby chinchompa");
		}

		if (config.swapStun())
		{
			menuManager.addPriorityEntry("Stun", "Hoop snake");
		}

		if (config.swapTravel())
		{
			menuManager.addPriorityEntry("Pay-toll(2-ecto)", "Energy barrier");
			menuManager.addPriorityEntry("Pay-toll(10gp)", "Gate");
			menuManager.addPriorityEntry("Travel", "Trapdoor");
		}

		if (config.swapHarpoon())
		{
			menuManager.addPriorityEntry("Harpoon");
		}

		if (config.swapBoxTrap())
		{
			menuManager.addPriorityEntry("Reset", "Shaking box");
			menuManager.addPriorityEntry("Lay", "Box trap");
			menuManager.addPriorityEntry("Reset", "Box trap");
			menuManager.addPriorityEntry("Activate", "Box trap");
		}

		if (config.swapChase())
		{
			menuManager.addPriorityEntry("Chase");
		}

		if (config.swapBirdhouseEmpty())
		{
			menuManager.addPriorityEntry("Empty", "Birdhouse");
			menuManager.addPriorityEntry("Empty", "Oak Birdhouse");
			menuManager.addPriorityEntry("Empty", "Willow Birdhouse");
			menuManager.addPriorityEntry("Empty", "Teak Birdhouse");
			menuManager.addPriorityEntry("Empty", "Maple Birdhouse");
			menuManager.addPriorityEntry("Empty", "Mahogany Birdhouse");
			menuManager.addPriorityEntry("Empty", "Yew Birdhouse");
			menuManager.addPriorityEntry("Empty", "Magic Birdhouse");
			menuManager.addPriorityEntry("Empty", "Redwood Birdhouse");
		}

		if (config.swapQuick())
		{
			menuManager.addPriorityEntry("Quick-enter");
			menuManager.addPriorityEntry("Quick-start");
			menuManager.addPriorityEntry("Quick-pass");
			menuManager.addPriorityEntry("Quick-open");
			menuManager.addPriorityEntry("Quick-leave");
		}

		if (config.swapAdmire())
		{
			menuManager.addPriorityEntry("Teleport", "Mounted Strength Cape").setPriority(10);
			menuManager.addPriorityEntry("Teleport", "Mounted Strength Cape (t)").setPriority(10);
			menuManager.addPriorityEntry("Teleport", "Mounted Construction Cape").setPriority(10);
			menuManager.addPriorityEntry("Teleport", "Mounted Construction Cape (t)").setPriority(10);
			menuManager.addPriorityEntry("Teleport", "Mounted Crafting Cape").setPriority(10);
			menuManager.addPriorityEntry("Teleport", "Mounted Crafting Cape (t)").setPriority(10);
			menuManager.addPriorityEntry("Teleport", "Mounted Hunter Cape").setPriority(10);
			menuManager.addPriorityEntry("Teleport", "Mounted Hunter Cape (t)").setPriority(10);
			menuManager.addPriorityEntry("Teleport", "Mounted Fishing Cape").setPriority(10);
			menuManager.addPriorityEntry("Teleport", "Mounted Fishing Cape (t)").setPriority(10);
			menuManager.addPriorityEntry("Spellbook", "Mounted Magic Cape");
			menuManager.addPriorityEntry("Spellbook", "Mounted Magic Cape (t)");
			menuManager.addPriorityEntry("Perks", "Mounted Max Cape");
		}

		if (config.swapPrivate())
		{
			menuManager.addPriorityEntry("Private");
		}

		if (config.swapPick())
		{
			menuManager.addPriorityEntry("Pick-lots");
		}

		if (config.swapSearch())
		{
			menuManager.addPriorityEntry("Search").setPriority(1);
		}

		if (config.swapRogueschests())
		{
			menuManager.addPriorityEntry("Search for traps");
		}

		if (config.rockCake())
		{
			menuManager.addPriorityEntry("Guzzle", "Dwarven rock cake");
		}

		if (config.swapTeleportItem())
		{
			menuManager.addPriorityEntry(new InventoryComparableEntry("Rub", "", false)).setPriority(1);
			menuManager.addPriorityEntry(new InventoryComparableEntry("Teleport", "", false)).setPriority(1);
		}

		if (config.swapCoalBag())
		{
			menuManager.addPriorityEntry("Fill", "Coal bag");
			menuManager.addPriorityEntry(newBankComparableEntry("Empty", "Coal bag"));
		}

		if (config.swapBones())
		{
			menuManager.addSwap("Bury", "bone", "Use");
		}

		if (config.swapSalamander())
		{
			menuManager.addSwap("Wield", "salamander", "Release");
			menuManager.addSwap("Wield", "Swamp lizard", "Release");
		}

		if (config.swapNexus())
		{
			menuManager.addPriorityEntry("Teleport menu", "Portal nexus");
		}

		switch (config.swapFairyRingMode())
		{
			case OFF:
			case ZANARIS:
				menuManager.removeSwaps("Fairy ring");
				menuManager.removeSwaps("Tree");
				break;
			case CONFIGURE:
				menuManager.addPriorityEntry("Configure", "Fairy ring");
				break;
			case LAST_DESTINATION:
				menuManager.addPriorityEntry("Last-destination", false);
				break;
		}

		switch (config.swapFairyTreeMode())
		{
			case OFF:
				break;
			case TREE:
				menuManager.addPriorityEntry("Tree", "Spiritual Fairy Tree");
				break;
			case RING_ZANARIS:
				menuManager.addPriorityEntry("Ring-Zanaris", "Spiritual Fairy Tree");
				break;
			case RING_CONFIGURE:
				menuManager.addPriorityEntry("Ring-configure", "Spiritual Fairy Tree");
				break;
			case RING_LAST_DESTINATION:
				menuManager.addPriorityEntry("Ring-last-destination", false);
				break;
		}

		switch (config.swapOccultMode())
		{
			case LUNAR:
				menuManager.addPriorityEntry("Lunar", "Altar of the Occult");
				break;
			case ANCIENT:
				menuManager.addPriorityEntry("Ancient", "Altar of the Occult");
				break;
			case ARCEUUS:
				menuManager.addPriorityEntry("Arceuus", "Altar of the Occult");
				break;
		}

		switch (config.swapObeliskMode())
		{
			case SET_DESTINATION:
				menuManager.addPriorityEntry("Set destination", "Obelisk");
				break;
			case TELEPORT_TO_DESTINATION:
				menuManager.addPriorityEntry("Teleport to destination", "Obelisk");
				break;
		}

		if (config.swapHomePortal())
		{
			menuManager.addPriorityEntry(config.swapHomePortalMode().toString(), "Portal").setPriority(10);
		}

		if (config.swapHardWoodGrove())
		{
			menuManager.addPriorityEntry("Quick-pay(100)", "Hardwood grove doors");
		}

		if (config.swapMax())
		{
			menuManager.addPriorityEntry(config.maxMode().toString(), "max cape");
		}

		if (config.swapQuestCape())
		{
			menuManager.addPriorityEntry(config.questCapeMode().toString(), "quest point cape");
		}

		if (config.swapHouseAd())
		{
			menuManager.addPriorityEntry(config.swapHouseAdMode().toString(), "House Advertisement");
		}

		if (config.getSwapGrimyHerb())
		{
			menuManager.addPriorityEntry(new GrimyHerbComparableEntry(config.swapGrimyHerbMode(), client));
		}

		if (config.swapEssPouch())
		{
			menuManager.addPriorityEntry(POUCH).setPriority(100);
			menuManager.addPriorityEntry(EMPTY_SMALL).setPriority(10);
			menuManager.addPriorityEntry(EMPTY_MEDIUM).setPriority(10);
			menuManager.addPriorityEntry(EMPTY_LARGE).setPriority(10);
			menuManager.addPriorityEntry(EMPTY_GIANT).setPriority(10);
		}

		if (config.swapJewelleryBox())
		{
			for (String jewellerybox : jewelleryBox)
			{
				menuManager.addPriorityEntry(jewellerybox, "basic jewellery box");
				menuManager.addPriorityEntry(jewellerybox, "fancy jewellery box");
				menuManager.addPriorityEntry(jewellerybox, "ornate jewellery box");
			}
		}

		if (config.swapPharaohsSceptre())
		{
			for (String pharaohsSceptre : pharaohsSceptre)
			{
				menuManager.addPriorityEntry("Wield", pharaohsSceptre);
			}
		}

		if (config.swapDropFish())
		{
			for (String dropFish : dropFish)
			{
				menuManager.addSwap("Use", dropFish, "Drop");
			}
		}

		if (config.swapDropOre())
		{
			for (String dropOre : dropOre)
			{
				menuManager.addSwap("Use", dropOre, "Drop");
			}
		}

		if (config.swapDropLogs())
		{
			for (String dropLogs : dropLogs)
			{
				menuManager.addSwap("Use", dropLogs, "Drop");
			}
		}
		
		switch (config.swapGEItemCollect())
		{
			case ITEMS:
				menuManager.addPriorityEntry(new BankComparableEntry("collect-items", "", false));
				menuManager.addPriorityEntry(new BankComparableEntry("collect-item", "", false));
				break;
			case NOTES:
				menuManager.addPriorityEntry(new BankComparableEntry("collect-notes", "", false));
				menuManager.addPriorityEntry(new BankComparableEntry("collect-note", "", false));
				break;
			case BANK:
				menuManager.addPriorityEntry(new BankComparableEntry("collect to bank", "", false));
				menuManager.addPriorityEntry(new BankComparableEntry("bank", "", false));
				break;
			case DEFAULT:
				menuManager.removePriorityEntry(new BankComparableEntry("collect to bank", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("bank", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("collect-notes", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("collect-note", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("collect-items", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("collect-item", "", false));
		}
	}

	private void removeSwaps()
	{
		final Iterator<Map.Entry<AbstractComparableEntry, AbstractComparableEntry>> dePrioIter = dePrioSwaps.entrySet().iterator();
		dePrioIter.forEachRemaining((e) ->
		{
			menuManager.removeSwap(e.getKey(), e.getValue());
			dePrioIter.remove();
		});

		menuManager.removePriorityEntry("Activate", "Box trap");
		menuManager.removePriorityEntry("Assignment");
		menuManager.removePriorityEntry("Bank");
		menuManager.removePriorityEntry("Buy <col=ff7000>All");
		menuManager.removePriorityEntry("Buy-plank");
		menuManager.removePriorityEntry("Buy-plank", "Sawmill operator");
		menuManager.removePriorityEntry("Charter");
		menuManager.removePriorityEntry("Chase");
		menuManager.removePriorityEntry("Claim");
		menuManager.removePriorityEntry("Claim-slime");
		menuManager.removePriorityEntry("Contract");
		menuManager.removePriorityEntry("Decant");
		menuManager.removePriorityEntry("Dream");
		menuManager.removePriorityEntry("Edgeville", "Lever");
		menuManager.removePriorityEntry("Empty", "Birdhouse");
		menuManager.removePriorityEntry("Empty", "Magic Birdhouse");
		menuManager.removePriorityEntry("Empty", "Mahogany Birdhouse");
		menuManager.removePriorityEntry("Empty", "Maple Birdhouse");
		menuManager.removePriorityEntry("Empty", "Oak Birdhouse");
		menuManager.removePriorityEntry("Empty", "Redwood Birdhouse");
		menuManager.removePriorityEntry("Empty", "Teak Birdhouse");
		menuManager.removePriorityEntry("Empty", "Willow Birdhouse");
		menuManager.removePriorityEntry("Empty", "Yew Birdhouse");
		menuManager.removePriorityEntry("Enchant");
		menuManager.removePriorityEntry("Escort");
		menuManager.removePriorityEntry("Exchange");
		menuManager.removePriorityEntry("Fill", "Coal bag");
		menuManager.removePriorityEntry("Fly");
		menuManager.removePriorityEntry("Follow", "Elkoy");
		menuManager.removePriorityEntry("Guzzle", "Dwarven rock cake");
		menuManager.removePriorityEntry("Harpoon");
		menuManager.removePriorityEntry("Heal");
		menuManager.removePriorityEntry("Help");
		menuManager.removePriorityEntry("Jatizso");
		menuManager.removePriorityEntry("Kandarin Monastery");
		menuManager.removePriorityEntry("Lay", "Box trap");
		menuManager.removePriorityEntry("Metamorphosis", "Baby chinchompa");
		menuManager.removePriorityEntry("Monastery Teleport");
		menuManager.removePriorityEntry("Neitiznot");
		menuManager.removePriorityEntry("Pay (", false);
		menuManager.removePriorityEntry("Pay");
		menuManager.removePriorityEntry("Pay-fare");
		menuManager.removePriorityEntry("Pay-toll(10gp)", "Gate");
		menuManager.removePriorityEntry("Pay-toll(2-ecto)", "Energy barrier");
		menuManager.removePriorityEntry("Perks", "Mounted Max Cape");
		menuManager.removePriorityEntry("Pick-lots");
		menuManager.removePriorityEntry("Private");
		menuManager.removePriorityEntry("Quick-enter");
		menuManager.removePriorityEntry("Quick-leave");
		menuManager.removePriorityEntry("Quick-open");
		menuManager.removePriorityEntry("Quick-pass");
		menuManager.removePriorityEntry("Quick-pay", "Hardwood grove doors");
		menuManager.removePriorityEntry("Quick-start");
		menuManager.removePriorityEntry("Quick-travel");
		menuManager.removePriorityEntry("Rellekka");
		menuManager.removePriorityEntry("Repairs");
		menuManager.removePriorityEntry("Reset", "Shaking box");
		menuManager.removePriorityEntry("Search for traps");
		menuManager.removePriorityEntry("Search");
		menuManager.removePriorityEntry("Send-parcel", "Rionasta");
		menuManager.removePriorityEntry("Shop");
		menuManager.removePriorityEntry("Spellbook", "Magic cape");
		menuManager.removePriorityEntry("Spellbook", "Magic cape(t)");
		menuManager.removePriorityEntry("Spellbook", "Mounted Magic Cape");
		menuManager.removePriorityEntry("Start-minigame");
		menuManager.removePriorityEntry("Story");
		menuManager.removePriorityEntry("Stun", "Hoop snake");
		menuManager.removePriorityEntry("Take-boat");
		menuManager.removePriorityEntry("Tan <col=ff7000>All");
		menuManager.removePriorityEntry("Offer-All<col=ff9040>");
		menuManager.removePriorityEntry("Teleport menu", "Portal nexus");
		menuManager.removePriorityEntry("Teleport", "Crafting cape");
		menuManager.removePriorityEntry("Teleport", "Crafting cape(t)");
		menuManager.removePriorityEntry("Teleport", "Explorer's ring 2");
		menuManager.removePriorityEntry("Teleport", "Explorer's ring 3");
		menuManager.removePriorityEntry("Teleport", "Explorer's ring 4");
		menuManager.removePriorityEntry("Teleport", "Mage of zamorak");
		menuManager.removePriorityEntry("Teleport", "Mounted Strength Cape");
		menuManager.removePriorityEntry("Teleport", "Mounted Strength Cape (t)");
		menuManager.removePriorityEntry("Teleport", "Mounted Construction Cape");
		menuManager.removePriorityEntry("Teleport", "Mounted Construction Cape (t)");
		menuManager.removePriorityEntry("Teleport", "Mounted Crafting Cape");
		menuManager.removePriorityEntry("Teleport", "Mounted Crafting Cape (t)");
		menuManager.removePriorityEntry("Teleport", "Mounted Hunter Cape");
		menuManager.removePriorityEntry("Teleport", "Mounted Hunter Cape (t)");
		menuManager.removePriorityEntry("Teleport", "Mounted Fishing Cape");
		menuManager.removePriorityEntry("Teleport", "Mounted Fishing Cape (t)");
		menuManager.removePriorityEntry("Spellbook", "Mounted Magic Cape");
		menuManager.removePriorityEntry("Spellbook", "Mounted Magic Cape (t)");
		menuManager.removePriorityEntry("Trade");
		menuManager.removePriorityEntry("Trade-with");
		menuManager.removePriorityEntry("Transport");
		menuManager.removePriorityEntry("Travel");
		menuManager.removePriorityEntry("Travel", "Trapdoor");
		menuManager.removePriorityEntry(config.ardougneCloakMode().toString());
		menuManager.removePriorityEntry(config.ardougneCloakMode().toString2());
		menuManager.removePriorityEntry(new BankComparableEntry("collect", "", false));
		menuManager.removePriorityEntry(new InventoryComparableEntry("Rub", "", false));
		menuManager.removePriorityEntry(new InventoryComparableEntry("Teleport", "", false));
		menuManager.removePriorityEntry(new GrimyHerbComparableEntry(config.swapGrimyHerbMode(), client));
		menuManager.removePriorityEntry(newBankComparableEntry("Empty", "Coal bag"));
		menuManager.removePriorityEntry(config.constructionCapeMode().toString(), "Construct. cape");
		menuManager.removePriorityEntry(config.constructionCapeMode().toString(), "Construct. cape(t)");
		menuManager.removePriorityEntry(config.maxMode().toString(), "max cape");
		menuManager.removePriorityEntry(config.questCapeMode().toString(), "quest point cape");
		menuManager.removeSwap("Bury", "bone", "Use");
		menuManager.removeSwap("Wield", "salamander", "Release");
		menuManager.removeSwap("Wield", "Swamp lizard", "Release");
		menuManager.removePriorityEntry(POUCH);
		menuManager.removePriorityEntry(EMPTY_SMALL);
		menuManager.removePriorityEntry(EMPTY_MEDIUM);
		menuManager.removePriorityEntry(EMPTY_LARGE);
		menuManager.removePriorityEntry(EMPTY_GIANT);
		menuManager.removePriorityEntry(config.swapHomePortalMode().toString(), "Portal");
		menuManager.removePriorityEntry(config.swapHouseAdMode().toString(), "House Advertisement");
		for (String jewellerybox : jewelleryBox)
		{
			menuManager.removePriorityEntry(jewellerybox, "basic jewellery box");
			menuManager.removePriorityEntry(jewellerybox, "fancy jewellery box");
			menuManager.removePriorityEntry(jewellerybox, "ornate jewellery box");
		}
		for (String pharaohsSceptre : pharaohsSceptre)
		{
			menuManager.removePriorityEntry("Wield", pharaohsSceptre);
		}

		for (String dropFish : dropFish)
		{
			menuManager.removeSwap("Use", dropFish, "Drop");
		}

		for (String dropOre : dropOre)
		{
			menuManager.removeSwap("Use", dropOre, "Drop");
		}

		for (String dropLogs : dropLogs)
		{
			menuManager.removeSwap("Use", dropLogs, "Drop");
		}

		switch (config.swapFairyRingMode())
		{
			case OFF:
			case ZANARIS:
				menuManager.removeSwaps("Fairy ring");
				menuManager.removeSwaps("Tree");
				break;
			case CONFIGURE:
				menuManager.removePriorityEntry("Configure", "Fairy ring");
				break;
			case LAST_DESTINATION:
				menuManager.removePriorityEntry("Last-destination", false);
				break;
		}

		switch (config.swapFairyTreeMode())
		{
			case OFF:
			case TREE:
				menuManager.removePriorityEntry("Tree", "Spiritual Fairy Tree");
				break;
			case RING_ZANARIS:
				menuManager.removePriorityEntry("Ring-Zanaris", "Spiritual Fairy Tree");
				break;
			case RING_CONFIGURE:
				menuManager.removePriorityEntry("Ring-configure", "Spiritual Fairy Tree");
				break;
			case RING_LAST_DESTINATION:
				menuManager.removePriorityEntry("Ring-last-destination", false);
				break;
		}

		switch (config.swapOccultMode())
		{
			case LUNAR:
				menuManager.removePriorityEntry("Lunar", "Altar of the Occult");
				break;
			case ANCIENT:
				menuManager.removePriorityEntry("Ancient", "Altar of the Occult");
				break;
			case ARCEUUS:
				menuManager.removePriorityEntry("Arceuus", "Altar of the Occult");
				break;
		}

		switch (config.swapObeliskMode())
		{
			case SET_DESTINATION:
				menuManager.removePriorityEntry("Set destination", "Obelisk");
				break;
			case TELEPORT_TO_DESTINATION:
				menuManager.removePriorityEntry("Teleport to destination", "Obelisk");
				break;
		}
		
		switch (config.swapGEItemCollect())
		{
			case ITEMS:
				menuManager.removePriorityEntry(new BankComparableEntry("collect-items", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("collect-item", "", false));
				break;
			case NOTES:
				menuManager.removePriorityEntry(new BankComparableEntry("collect-notes", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("collect-note", "", false));
				break;
			case BANK:
				menuManager.removePriorityEntry(new BankComparableEntry("collect to bank", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("bank", "", false));
				break;
			case DEFAULT:
				menuManager.removePriorityEntry(new BankComparableEntry("collect to bank", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("bank", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("collect-notes", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("collect-note", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("collect-items", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("collect-item", "", false));
		}

	}

	private boolean isPuroPuro()
	{
		Player player = client.getLocalPlayer();

		if (player == null)
		{
			return false;
		}
		else
		{
			WorldPoint location = player.getWorldLocation();
			return location.getRegionID() == PURO_PURO_REGION_ID;
		}
	}

	private void startHotkey()
	{
		eventBus.subscribe(ClientTick.class, HOTKEY, this::addHotkey);
		eventBus.subscribe(ClientTick.class, HOTKEY_CHECK, this::hotkeyCheck);
	}

	private void addHotkey(ClientTick event)
	{
		loadCustomSwaps(config.shiftCustomSwaps(), customShiftSwaps);

		if (config.swapClimbUpDown())
		{
			menuManager.addPriorityEntry("climb-up").setPriority(100);
		}

		if (config.swapNpcContact())
		{
			for (String npccontact : npcContact)
			{
				menuManager.addPriorityEntry(npccontact, "npc contact");
			}
		}
		
		if (config.swapGEAbort())
		{
			menuManager.addPriorityEntry("Abort offer");
		}
		
		switch (config.bankDepositShiftClick())
		{
			case DEPOSIT_1:
				menuManager.addPriorityEntry(new BankComparableEntry("Deposit-1", "", false));
				break;
			case DEPOSIT_5:
				menuManager.addPriorityEntry(new BankComparableEntry("Deposit-5", "", false));
				break;
			case DEPOSIT_10:
				menuManager.addPriorityEntry(new BankComparableEntry("Deposit-10", "", false));
				break;
			case DEPOSIT_X:
				menuManager.addPriorityEntry(new BankComparableEntry("Deposit-X", "", false));
				break;
			case DEPOSIT_ALL:
				menuManager.addPriorityEntry(new BankComparableEntry("Deposit-All", "", false));
				break;
			case EXTRA_OP:
				menuManager.addPriorityEntry(new BankComparableEntry("wield", "", false));
				menuManager.addPriorityEntry(new BankComparableEntry("wear", "", false));
				menuManager.addPriorityEntry(new BankComparableEntry("eat", "", false));
				menuManager.addPriorityEntry(new BankComparableEntry("drink", "", false));
				menuManager.addPriorityEntry(new BankComparableEntry("equip", "", false));
				menuManager.addPriorityEntry(new BankComparableEntry("invigorate", "", false));
				break;
			case OFF:
				menuManager.removePriorityEntry(new BankComparableEntry("wield", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("wear", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("eat", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("drink", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("equip", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("invigorate", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Deposit-All", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Deposit-1", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Deposit-5", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Deposit-10", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Deposit-X", "", false));
				break;
		}
		switch (config.bankWithdrawShiftClick())
		{
			case WITHDRAW_1:
				menuManager.addPriorityEntry(new BankComparableEntry("Withdraw-1", "", false));
				break;
			case WITHDRAW_5:
				menuManager.addPriorityEntry(new BankComparableEntry("Withdraw-5", "", false));
				break;
			case WITHDRAW_10:
				menuManager.addPriorityEntry(new BankComparableEntry("Withdraw-10", "", false));
				break;
			case WITHDRAW_X:
				menuManager.addPriorityEntry(new BankComparableEntry("Withdraw-X", "", false));
				break;
			case WITHDRAW_ALL:
				menuManager.addPriorityEntry(new BankComparableEntry("Withdraw-All", "", false));
				break;
			case WITHDRAW_ALL_BUT_1:
				menuManager.addPriorityEntry(new BankComparableEntry("Withdraw-All-But-1", "", false));
				break;
			case OFF:
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-1", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-5", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-10", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-X", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-All", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-All-But-1", "", false));
				break;
		}

		eventBus.unregister(HOTKEY);
	}

	private void stopHotkey()
	{
		eventBus.subscribe(ClientTick.class, HOTKEY, this::removeHotkey);
	}

	private void removeHotkey(ClientTick event)
	{
		if (config.swapClimbUpDown())
		{
			menuManager.removePriorityEntry("climb-up");
		}

		if (config.swapNpcContact())
		{
			for (String npccontact : npcContact)
			{
				menuManager.removePriorityEntry(npccontact, "npc contact");
			}
		}
		
				if (config.swapGEAbort())
		{
			menuManager.removePriorityEntry("Abort offer");
		}
		
		switch (config.bankDepositShiftClick())
		{
			case DEPOSIT_1:
				menuManager.removePriorityEntry(new BankComparableEntry("Deposit-1", "", false));
				break;
			case DEPOSIT_5:
				menuManager.removePriorityEntry(new BankComparableEntry("Deposit-5", "", false));
				break;
			case DEPOSIT_10:
				menuManager.removePriorityEntry(new BankComparableEntry("Deposit-10", "", false));
				break;
			case DEPOSIT_X:
				menuManager.removePriorityEntry(new BankComparableEntry("Deposit-X", "", false));
				break;
			case DEPOSIT_ALL:
				menuManager.removePriorityEntry(new BankComparableEntry("Deposit-All", "", false));
				break;
			case EXTRA_OP:
				menuManager.removePriorityEntry(new BankComparableEntry("wield", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("wear", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("eat", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("drink", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("equip", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("invigorate", "", false));
				break;
			case OFF:
				menuManager.removePriorityEntry(new BankComparableEntry("wield", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("wear", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("eat", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("drink", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("equip", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("invigorate", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Deposit-All", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Deposit-1", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Deposit-5", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Deposit-10", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Deposit-X", "", false));
				break;
		}
		switch (config.bankWithdrawShiftClick())
		{
			case WITHDRAW_1:
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-1", "", false));
				break;
			case WITHDRAW_5:
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-5", "", false));
				break;
			case WITHDRAW_10:
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-10", "", false));
				break;
			case WITHDRAW_X:
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-X", "", false));
				break;
			case WITHDRAW_ALL:
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-All", "", false));
				break;
			case WITHDRAW_ALL_BUT_1:
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-All-But-1", "", false));
				break;
			case OFF:
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-1", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-5", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-10", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-X", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-All", "", false));
				menuManager.removePriorityEntry(new BankComparableEntry("Withdraw-All-But-1", "", false));
				break;
		}

		loadCustomSwaps("", customShiftSwaps);
		eventBus.unregister(HOTKEY);
	}

	private void hotkeyCheck(ClientTick event)
	{
		if (hotkeyActive)
		{
			int i = 0;
			for (boolean bol : client.getPressedKeys())
			{
				if (bol)
				{
					i++;
				}
			}
			if (i == 0)
			{
				stopHotkey();
				setHotkeyActive(false);
				eventBus.unregister(HOTKEY_CHECK);
			}
		}
	}

	private void startControl()
	{
		if (!config.swapClimbUpDown())
		{
			return;
		}

		eventBus.subscribe(ClientTick.class, CONTROL, this::addControl);
		eventBus.subscribe(ClientTick.class, CONTROL_CHECK, this::controlCheck);
	}

	private void addControl(ClientTick event)
	{
		menuManager.addPriorityEntry("climb-down").setPriority(100);
		eventBus.unregister(CONTROL);
	}

	private void stopControl()
	{
		eventBus.subscribe(ClientTick.class, CONTROL, this::removeControl);
	}

	private void removeControl(ClientTick event)
	{
		menuManager.removePriorityEntry("climb-down");
		eventBus.unregister(CONTROL);
	}

	private void controlCheck(ClientTick event)
	{
		if (controlActive)
		{
			int i = 0;
			for (boolean bol : client.getPressedKeys())
			{
				if (bol)
				{
					i++;
				}
			}
			if (i == 0)
			{
				stopControl();
				setControlActive(false);
				eventBus.unregister(CONTROL_CHECK);
			}
		}
	}

	private void addBuySellEntries()
	{
		for (int i = 0; i < 4; i++)
		{
			if (buyEntries[i] != null)
			{
				for (AbstractComparableEntry entry : buyEntries[i])
				{
					menuManager.addPriorityEntry(entry);
				}
			}
			if (sellEntries[i] != null)
			{
				for (AbstractComparableEntry entry : sellEntries[i])
				{
					menuManager.addPriorityEntry(entry);
				}
			}
		}
	}

	private void removeBuySellEntries()
	{
		for (int i = 0; i < 4; i++)
		{
			if (buyEntries[i] != null)
			{
				for (AbstractComparableEntry entry : buyEntries[i])
				{
					menuManager.removePriorityEntry(entry);
				}
			}
			if (sellEntries[i] != null)
			{
				for (AbstractComparableEntry entry : sellEntries[i])
				{
					menuManager.removePriorityEntry(entry);
				}
			}
		}
	}

	/**
	 * Fills the buy/sell entry arrays
	 */
	private void updateBuySellEntries()
	{
		List<String> tmp;
	
		if (config.getSwapBuyOne())
		{
			tmp = Text.fromCSV(config.getBuyOneItems());
			buyEntries[0] = new AbstractComparableEntry[tmp.size()];

			ShopComparableEntry.populateArray(buyEntries[0], tmp, true, 1);
		}
		else
		{
			buyEntries[0] = null;
		}

		if (config.getSwapBuyFive())
		{
			tmp = Text.fromCSV(config.getBuyFiveItems());
			buyEntries[1] = new AbstractComparableEntry[tmp.size()];

			ShopComparableEntry.populateArray(buyEntries[1], tmp, true, 5);
		}
		else
		{
			buyEntries[1] = null;
		}

		if (config.getSwapBuyTen())
		{
			tmp = Text.fromCSV(config.getBuyTenItems());
			buyEntries[2] = new AbstractComparableEntry[tmp.size()];

			ShopComparableEntry.populateArray(buyEntries[2], tmp, true, 10);
		}
		else
		{
			buyEntries[2] = null;
		}

		if (config.getSwapBuyFifty())
		{
			tmp = Text.fromCSV(config.getBuyFiftyItems());
			buyEntries[3] = new AbstractComparableEntry[tmp.size()];

			ShopComparableEntry.populateArray(buyEntries[3], tmp, true, 50);
		}
		else
		{
			buyEntries[3] = null;
		}

		if (config.getSwapSellOne())
		{
			tmp = Text.fromCSV(config.getSellOneItems());
			sellEntries[0] = new AbstractComparableEntry[tmp.size()];

			ShopComparableEntry.populateArray(sellEntries[0], tmp, false, 1);
		}
		else
		{
			sellEntries[0] = null;
		}

		if (config.getSwapSellFive())
		{
			tmp = Text.fromCSV(config.getSellFiveItems());
			sellEntries[1] = new AbstractComparableEntry[tmp.size()];

			ShopComparableEntry.populateArray(sellEntries[1], tmp, false, 5);
		}
		else
		{
			sellEntries[1] = null;
		}

		if (config.getSwapSellTen())
		{
			tmp = Text.fromCSV(config.getSellTenItems());
			sellEntries[2] = new AbstractComparableEntry[tmp.size()];

			ShopComparableEntry.populateArray(sellEntries[2], tmp, false, 10);
		}
		else
		{
			sellEntries[2] = null;
		}

		if (config.getSwapSellFifty())
		{
			tmp = Text.fromCSV(config.getSellFiftyItems());
			sellEntries[3] = new AbstractComparableEntry[tmp.size()];

			ShopComparableEntry.populateArray(sellEntries[3], tmp, false, 50);
		}
		else
		{
			sellEntries[3] = null;
		}
	}

	private void addWithdrawEntries()
	{
		for (int i = 0; i < 5; i++)
		{
			if (withdrawEntries[i] != null)
			{
				for (AbstractComparableEntry entry : withdrawEntries[i])
				{
					menuManager.addPriorityEntry(entry);
				}
			}
		}
	}

	private void removeWithdrawEntries()
	{
		for (int i = 0; i < 5; i++)
		{
			if (withdrawEntries[i] != null)
			{
				for (AbstractComparableEntry entry : withdrawEntries[i])
				{
					menuManager.removePriorityEntry(entry);
				}
			}
		}
	}

	private void updateWithdrawEntries()
	{
		List<String> tmp;

		if (config.getWithdrawOne() && !hotkeyActive)
		{
			tmp = Text.fromCSV(config.getWithdrawOneItems());
			withdrawEntries[0] = new AbstractComparableEntry[tmp.size()];

			WithdrawComparableEntry.populateArray(withdrawEntries[0], tmp, WithdrawComparableEntry.Amount.ONE);
		}
		else
		{
			withdrawEntries[0] = null;
		}

		if (config.getWithdrawFive() && !hotkeyActive)
		{
			tmp = Text.fromCSV(config.getWithdrawFiveItems());
			withdrawEntries[1] = new AbstractComparableEntry[tmp.size()];

			WithdrawComparableEntry.populateArray(withdrawEntries[1], tmp, WithdrawComparableEntry.Amount.FIVE);
		}
		else
		{
			withdrawEntries[1] = null;
		}

		if (config.getWithdrawTen() && !hotkeyActive)
		{
			tmp = Text.fromCSV(config.getWithdrawTenItems());
			withdrawEntries[2] = new AbstractComparableEntry[tmp.size()];

			WithdrawComparableEntry.populateArray(withdrawEntries[2], tmp, WithdrawComparableEntry.Amount.TEN);
		}
		else
		{
			withdrawEntries[2] = null;
		}

		if (config.getWithdrawX() && !hotkeyActive)
		{
			tmp = Text.fromCSV(config.getWithdrawXItems());
			withdrawEntries[3] = new AbstractComparableEntry[tmp.size()];

			WithdrawComparableEntry.populateArray(withdrawEntries[3], tmp, WithdrawComparableEntry.Amount.X);
		}
		else
		{
			withdrawEntries[3] = null;
		}

		if (config.getWithdrawAll() && !hotkeyActive)
		{
			tmp = Text.fromCSV(config.getWithdrawAllItems());
			withdrawEntries[4] = new AbstractComparableEntry[tmp.size()];

			WithdrawComparableEntry.populateArray(withdrawEntries[4], tmp, WithdrawComparableEntry.Amount.ALL);
		}
		else
		{
			withdrawEntries[4] = null;
		}
	}

	private void updateRemovedObjects()
	{
		if (config.getRemoveObjects())
		{
			removedObjects = Text.fromCSV(
				Text.removeTags(config.getRemovedObjects().toLowerCase())
			).toArray(new String[0]);
		}
		else
		{
			removedObjects = null;
		}
	}
}
