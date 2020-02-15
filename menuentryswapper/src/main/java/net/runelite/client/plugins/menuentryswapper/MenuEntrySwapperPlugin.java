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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
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
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Varbits;
import static net.runelite.api.Varbits.BUILDING_MODE;
import static net.runelite.api.Varbits.WITHDRAW_X_AMOUNT;
import net.runelite.api.WorldType;
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
import static net.runelite.client.menus.ComparableEntries.newBankComparableEntry;
import static net.runelite.client.menus.ComparableEntries.newBaseComparableEntry;
import net.runelite.client.menus.EquipmentComparableEntry;
import net.runelite.client.menus.InventoryComparableEntry;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.menus.ShopComparableEntry;
import net.runelite.client.menus.WithdrawComparableEntry;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.menuentryswapper.comparables.GrimyHerbComparableEntry;
import net.runelite.client.plugins.pvptools.PvpToolsConfig;
import net.runelite.client.plugins.pvptools.PvpToolsPlugin;
import net.runelite.client.util.HotkeyListener;
import static net.runelite.client.util.MenuUtil.swap;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Menu Entry Swapper",
	description = "Change the default option that is displayed when hovering over objects",
	tags = {"npcs", "inventory", "items", "objects"},
	type = PluginType.UTILITY
)
@PluginDependency(PvpToolsPlugin.class)
public class MenuEntrySwapperPlugin extends Plugin
{
	private static final Object HOTKEY = new Object();
	private static final Object CONTROL = new Object();
	private static final Object HOTKEY_CHECK = new Object();
	private static final Object CONTROL_CHECK = new Object();
	private static final int PURO_PURO_REGION_ID = 10307;
	private static final Set<MenuOpcode> NPC_MENU_TYPES = ImmutableSet.of(
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
	private static final List<String> npcContact = Arrays.asList(
		"honest jimmy", "bert the sandman", "advisor ghrim", "dark mage", "lanthus", "turael",
		"mazchna", "vannaka", "chaeldar", "nieve", "steve", "duradel", "krystilia", "konar",
		"murphy", "cyrisus", "smoggy", "ginea", "watson", "barbarian guard", "random"
	);

	private static final AbstractComparableEntry WALK = new AbstractComparableEntry()
	{
		private final int hash = "WALK".hashCode() * 79 + getPriority();

		@Override
		public int hashCode()
		{
			return hash;
		}

		@Override
		public boolean equals(Object entry)
		{
			return entry.getClass() == this.getClass() && entry.hashCode() == this.hashCode();
		}

		@Override
		public int getPriority()
		{
			return 99;
		}

		@Override
		public boolean matches(MenuEntry entry)
		{
			return
				entry.getOpcode() == MenuOpcode.WALK.getId() ||
					entry.getOpcode() == MenuOpcode.WALK.getId() + MenuOpcode.MENU_ACTION_DEPRIORITIZE_OFFSET;
		}
	};

	private static final AbstractComparableEntry TAKE = new AbstractComparableEntry()
	{
		private final int hash = "TAKE".hashCode() * 79 + getPriority();

		@Override
		public int hashCode()
		{
			return hash;
		}

		@Override
		public boolean equals(Object entry)
		{
			return entry.getClass() == this.getClass() && entry.hashCode() == this.hashCode();
		}

		@Override
		public int getPriority()
		{
			return 100;
		}

		@Override
		public boolean matches(MenuEntry entry)
		{
			int opcode = entry.getOpcode();
			if (opcode > MenuOpcode.MENU_ACTION_DEPRIORITIZE_OFFSET)
			{
				opcode -= MenuOpcode.MENU_ACTION_DEPRIORITIZE_OFFSET;
			}

			return
				opcode >= MenuOpcode.GROUND_ITEM_FIRST_OPTION.getId() &&
					opcode <= MenuOpcode.GROUND_ITEM_FIFTH_OPTION.getId();
		}
	};

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

	@Inject
	private PvpToolsPlugin pvpTools;

	@Inject
	private PvpToolsConfig pvpToolsConfig;

	private boolean buildingMode;
	private boolean inTobRaid = false;
	private boolean inCoxRaid = false;
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
		loadConstructionItems();
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
			setCastOptions(true);
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
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			resetCastOptions();
		}
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
		loadConstructionItems();

		switch (event.getKey())
		{
			case "customSwaps":
				loadCustomSwaps(config.customSwaps(), customSwaps);
				return;
			case "hideCastToB":
			case "hideCastIgnoredToB":
				if (config.hideCastToB())
				{
					setCastOptions(true);
				}
				else
				{
					resetCastOptions();
				}
				return;
			case "hideCastCoX":
			case "hideCastIgnoredCoX":
				if (config.hideCastCoX())
				{
					setCastOptions(true);
				}
				else
				{
					resetCastOptions();
				}
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

		loadConstructionItems();
		keyManager.registerKeyListener(ctrlHotkey);
		keyManager.registerKeyListener(hotkey);
	}

	@Subscribe
	private void onVarbitChanged(VarbitChanged event)
	{
		buildingMode = client.getVar(BUILDING_MODE) == 1;
		WithdrawComparableEntry.setX(client.getVar(WITHDRAW_X_AMOUNT));

		setCastOptions(false);
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

			if (option.contains("trade with") && config.hideTradeWith())
			{
				continue;
			}

			if (option.contains("lookup") && config.hideLookup())
			{
				continue;
			}

			if (option.contains("report") && config.hideReport())
			{
				continue;
			}

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
				if (config.hideDestroyCoalbag() && entry.getTarget().contains("Coal bag"))
				{
					continue;
				}
				if (config.hideDestroyHerbsack() && entry.getTarget().contains("Herb sack"))
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
				if (config.hideDestroyGembag() && entry.getTarget().contains("Gem bag"))
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

		if (hintArrowNpc != null
			&& hintArrowNpc.getIndex() == eventId
			&& NPC_MENU_TYPES.contains(MenuOpcode.of(event.getOpcode())))
		{
			return;
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
		final List<String> tmp = NEWLINE_SPLITTER.splitToList(config.prioEntry());

		for (String str : tmp)
		{
			String[] strings = str.split(",");

			if (strings.length <= 1)
			{
				continue;
			}

			final AbstractComparableEntry a = newBaseComparableEntry("", strings[1], -1, -1, false, true);
			final AbstractComparableEntry b = newBaseComparableEntry(strings[0], "", -1, -1, false, false);
			dePrioSwaps.put(a, b);
			menuManager.addSwap(a, b);
		}

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

		if (config.swapPickpocket())
		{
			menuManager.addPriorityEntry("Pickpocket").setPriority(1);
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
			menuManager.addPriorityEntry("Travel");
			menuManager.addPriorityEntry("Pay-fare");
			menuManager.addPriorityEntry("Charter");
			menuManager.addPriorityEntry("Take-boat");
			menuManager.addPriorityEntry("Fly");
			menuManager.addPriorityEntry("Jatizso");
			menuManager.addPriorityEntry("Neitiznot");
			menuManager.addPriorityEntry("Rellekka");
			menuManager.addPriorityEntry("Follow", "Elkoy").setPriority(10);
			menuManager.addPriorityEntry("Transport");
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

		switch (config.swapHomePortalMode())
		{
			case HOME:
				menuManager.addPriorityEntry("Home");
				break;
			case BUILD_MODE:
				menuManager.addPriorityEntry("Build mode");
				break;
			case FRIENDS_HOUSE:
				menuManager.addPriorityEntry("Friend's house");
				break;
		}

		if (config.swapHardWoodGrove())
		{
			menuManager.addPriorityEntry("Quick-pay(100)", "Hardwood grove doors");
		}

		if (config.getBurningAmulet())
		{
			menuManager.addPriorityEntry(new EquipmentComparableEntry(config.getBurningAmuletMode().toString(), "burning amulet"));
		}

		if (config.getCombatBracelet())
		{
			menuManager.addPriorityEntry(new EquipmentComparableEntry(config.getCombatBraceletMode().toString(), "combat bracelet"));
		}

		if (config.getGamesNecklace())
		{
			menuManager.addPriorityEntry(new EquipmentComparableEntry(config.getGamesNecklaceMode().toString(), "games necklace"));
		}

		if (config.getDuelingRing())
		{
			menuManager.addPriorityEntry(new EquipmentComparableEntry(config.getDuelingRingMode().toString(), "ring of dueling"));
		}

		if (config.getGlory())
		{
			menuManager.addPriorityEntry(new EquipmentComparableEntry(config.getGloryMode().toString(), "glory"));
		}

		if (config.getSkillsNecklace())
		{
			menuManager.addPriorityEntry(new EquipmentComparableEntry(config.getSkillsNecklaceMode().toString(), "skills necklace"));
		}

		if (config.getNecklaceofPassage())
		{
			menuManager.addPriorityEntry(new EquipmentComparableEntry(config.getNecklaceofPassageMode().toString(), "necklace of passage"));
		}

		if (config.getDigsitePendant())
		{
			menuManager.addPriorityEntry(new EquipmentComparableEntry(config.getDigsitePendantMode().toString(), "digsite pendant"));
		}

		if (config.getSlayerRing())
		{
			menuManager.addPriorityEntry(new EquipmentComparableEntry(config.getSlayerRingMode().toString(), "slayer ring"));
		}

		if (config.getXericsTalisman())
		{
			menuManager.addPriorityEntry(new EquipmentComparableEntry(config.getXericsTalismanMode().toString(), "talisman"));
		}

		if (config.getRingofWealth())
		{
			menuManager.addPriorityEntry(new EquipmentComparableEntry(config.getRingofWealthMode().toString(), "ring of wealth"));
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
			menuManager.addPriorityEntry(config.swapHouseAdMode().getEntry());
		}

		if (config.getSwapGrimyHerb())
		{
			menuManager.addPriorityEntry(new GrimyHerbComparableEntry(config.swapGrimyHerbMode(), client));
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
		menuManager.removePriorityEntry("Pickpocket");
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
		menuManager.removePriorityEntry(new BankComparableEntry("collect", "", false));
		menuManager.removePriorityEntry(new EquipmentComparableEntry(config.getBurningAmuletMode().toString(), "burning amulet"));
		menuManager.removePriorityEntry(new EquipmentComparableEntry(config.getCombatBraceletMode().toString(), "combat bracelet"));
		menuManager.removePriorityEntry(new EquipmentComparableEntry(config.getDigsitePendantMode().toString(), "digsite pendant"));
		menuManager.removePriorityEntry(new EquipmentComparableEntry(config.getDuelingRingMode().toString(), "ring of dueling"));
		menuManager.removePriorityEntry(new EquipmentComparableEntry(config.getGamesNecklaceMode().toString(), "games necklace"));
		menuManager.removePriorityEntry(new EquipmentComparableEntry(config.getGloryMode().toString(), "glory"));
		menuManager.removePriorityEntry(new EquipmentComparableEntry(config.getNecklaceofPassageMode().toString(), "necklace of passage"));
		menuManager.removePriorityEntry(new EquipmentComparableEntry(config.getRingofWealthMode().toString(), "ring of wealth"));
		menuManager.removePriorityEntry(new EquipmentComparableEntry(config.getSkillsNecklaceMode().toString(), "skills necklace"));
		menuManager.removePriorityEntry(new EquipmentComparableEntry(config.getSlayerRingMode().toString(), "slayer ring"));
		menuManager.removePriorityEntry(new EquipmentComparableEntry(config.getXericsTalismanMode().toString(), "talisman"));
		menuManager.removePriorityEntry(new InventoryComparableEntry("Rub", "", false));
		menuManager.removePriorityEntry(new InventoryComparableEntry("Teleport", "", false));
		menuManager.removePriorityEntry(new GrimyHerbComparableEntry(config.swapGrimyHerbMode(), client));
		menuManager.removePriorityEntry(newBankComparableEntry("Empty", "Coal bag"));
		menuManager.removePriorityEntry(config.constructionCapeMode().toString(), "Construct. cape");
		menuManager.removePriorityEntry(config.constructionCapeMode().toString(), "Construct. cape(t)");
		menuManager.removePriorityEntry(config.getConstructionMode().getBuild());
		menuManager.removePriorityEntry(config.getConstructionMode().getRemove());
		menuManager.removePriorityEntry(config.maxMode().toString(), "max cape");
		menuManager.removePriorityEntry(config.questCapeMode().toString(), "quest point cape");
		menuManager.removePriorityEntry(config.swapHouseAdMode().getEntry());
		menuManager.removeSwap("Bury", "bone", "Use");
		for (String jewellerybox : jewelleryBox)
		{
			menuManager.removePriorityEntry(jewellerybox, "basic jewellery box");
			menuManager.removePriorityEntry(jewellerybox, "fancy jewellery box");
			menuManager.removePriorityEntry(jewellerybox, "ornate jewellery box");
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

		switch (config.swapHomePortalMode())
		{
			case HOME:
				menuManager.removePriorityEntry("Home");
				break;
			case BUILD_MODE:
				menuManager.removePriorityEntry("Build mode");
				break;
			case FRIENDS_HOUSE:
				menuManager.removePriorityEntry("Friend's house");
				break;
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

	private void loadConstructionItems()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (!buildingMode)
		{
			menuManager.removePriorityEntry(config.getConstructionMode().getBuild());
			menuManager.removePriorityEntry(config.getConstructionMode().getRemove());
			return;
		}

		if (config.getEasyConstruction())
		{
			menuManager.addPriorityEntry(config.getConstructionMode().getBuild()).setPriority(100);
			menuManager.addPriorityEntry(config.getConstructionMode().getRemove()).setPriority(100);
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

		if (config.bankWieldItem())
		{
			menuManager.addPriorityEntry(new BankComparableEntry("wield", "", false));
		}
		if (config.bankWearItem())
		{
			menuManager.addPriorityEntry(new BankComparableEntry("wear", "", false));
		}
		if (config.bankEatItem())
		{
			menuManager.addPriorityEntry(new BankComparableEntry("eat", "", false));
		}
		if (config.bankDrinkItem())
		{
			menuManager.addPriorityEntry(new BankComparableEntry("drink", "", false));
		}
		if (config.bankEquipItem())
		{
			menuManager.addPriorityEntry(new BankComparableEntry("equip", "", false));
		}
		if (config.bankInvigorateItem())
		{
			menuManager.addPriorityEntry(new BankComparableEntry("invigorate", "", false));
		}
		if (config.swapClimbUpDown())
		{
			menuManager.addPriorityEntry("climb-up").setPriority(100);
		}
		if (config.hotKeyLoot())
		{
			menuManager.addPriorityEntry(TAKE);
		}
		if (config.hotKeyWalk())
		{
			menuManager.addPriorityEntry(WALK);
		}
		if (config.swapNpcContact())
		{
			for (String npccontact : npcContact)
			{
				menuManager.addPriorityEntry(npccontact, "npc contact");
			}
		}

		eventBus.unregister(HOTKEY);
	}

	private void stopHotkey()
	{
		eventBus.subscribe(ClientTick.class, HOTKEY, this::removeHotkey);
	}

	private void removeHotkey(ClientTick event)
	{
		menuManager.removePriorityEntry(new BankComparableEntry("wield", "", false));
		menuManager.removePriorityEntry(new BankComparableEntry("wear", "", false));
		menuManager.removePriorityEntry(new BankComparableEntry("eat", "", false));
		menuManager.removePriorityEntry(new BankComparableEntry("drink", "", false));
		menuManager.removePriorityEntry(new BankComparableEntry("equip", "", false));
		menuManager.removePriorityEntry(new BankComparableEntry("invigorate", "", false));
		menuManager.removePriorityEntry("climb-up");
		menuManager.removePriorityEntry(TAKE);
		menuManager.removePriorityEntry(WALK);

		for (String npccontact : npcContact)
		{
			menuManager.removePriorityEntry(npccontact, "npc contact");
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

	private void setCastOptions(boolean force)
	{
		clientThread.invoke(() ->
		{
			boolean tmpInCoxRaid = client.getVar(Varbits.IN_RAID) == 1;
			if (tmpInCoxRaid != inCoxRaid || force)
			{
				if (tmpInCoxRaid && config.hideCastCoX())
				{
					client.setHideFriendCastOptions(true);
					client.setHideClanmateCastOptions(true);
					client.setUnhiddenCasts(Sets.newHashSet(Text.fromCSV(config.hideCastIgnoredCoX().toLowerCase())));
				}

				inCoxRaid = tmpInCoxRaid;
			}

			boolean tmpInTobRaid = client.getVar(Varbits.THEATRE_OF_BLOOD) == 2;
			if (tmpInTobRaid != inTobRaid || force)
			{
				if (tmpInTobRaid && config.hideCastToB())
				{
					client.setHideFriendCastOptions(true);
					client.setHideClanmateCastOptions(true);
					client.setUnhiddenCasts(Sets.newHashSet(Text.fromCSV(config.hideCastIgnoredToB().toLowerCase())));
				}

				inTobRaid = tmpInTobRaid;
			}

			if (!inCoxRaid && !inTobRaid)
			{
				resetCastOptions();
			}
		});
	}

	private void resetCastOptions()
	{
		clientThread.invoke(() ->
		{
			if (client.getVar(Varbits.IN_WILDERNESS) == 1 || WorldType.isAllPvpWorld(client.getWorldType()) && pluginManager.isPluginEnabled(pvpTools) && pvpToolsConfig.hideCast())
			{
				pvpTools.setCastOptions();
			}
			else
			{
				client.setHideFriendCastOptions(false);
				client.setHideClanmateCastOptions(false);
			}
		});
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

		if (config.getWithdrawOne())
		{
			tmp = Text.fromCSV(config.getWithdrawOneItems());
			withdrawEntries[0] = new AbstractComparableEntry[tmp.size()];

			WithdrawComparableEntry.populateArray(withdrawEntries[0], tmp, WithdrawComparableEntry.Amount.ONE);
		}
		else
		{
			withdrawEntries[0] = null;
		}

		if (config.getWithdrawFive())
		{
			tmp = Text.fromCSV(config.getWithdrawFiveItems());
			withdrawEntries[1] = new AbstractComparableEntry[tmp.size()];

			WithdrawComparableEntry.populateArray(withdrawEntries[1], tmp, WithdrawComparableEntry.Amount.FIVE);
		}
		else
		{
			withdrawEntries[1] = null;
		}

		if (config.getWithdrawTen())
		{
			tmp = Text.fromCSV(config.getWithdrawTenItems());
			withdrawEntries[2] = new AbstractComparableEntry[tmp.size()];

			WithdrawComparableEntry.populateArray(withdrawEntries[2], tmp, WithdrawComparableEntry.Amount.TEN);
		}
		else
		{
			withdrawEntries[2] = null;
		}

		if (config.getWithdrawX())
		{
			tmp = Text.fromCSV(config.getWithdrawXItems());
			withdrawEntries[3] = new AbstractComparableEntry[tmp.size()];

			WithdrawComparableEntry.populateArray(withdrawEntries[3], tmp, WithdrawComparableEntry.Amount.X);
		}
		else
		{
			withdrawEntries[3] = null;
		}

		if (config.getWithdrawAll())
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
