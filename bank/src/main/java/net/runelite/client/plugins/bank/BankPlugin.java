/*
 * Copyright (c) 2018, TheLonelyDev <https://github.com/TheLonelyDev>
 * Copyright (c) 2018, Jeremy Plsek <https://github.com/jplsek>
 * Copyright (c) 2019, Hydrox6 <ikada@protonmail.ch>
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
package net.runelite.client.plugins.bank;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.inject.Provides;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemDefinition;
import net.runelite.api.ItemID;
import net.runelite.api.MenuEntry;
import net.runelite.api.ScriptID;
import net.runelite.api.VarClientStr;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuShouldLeftClick;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.banktags.tabs.BankSearch;
import net.runelite.client.util.QuantityFormatter;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
	name = "Bank",
	description = "Modifications to the banking interface",
	tags = {"grand", "exchange", "high", "alchemy", "prices", "deposit"},
	type = PluginType.UTILITY
)
public class BankPlugin extends Plugin implements KeyListener
{
	private static final List<WidgetInfo> BANK_PINS = List.of(
		WidgetInfo.BANK_PIN_1,
		WidgetInfo.BANK_PIN_2,
		WidgetInfo.BANK_PIN_3,
		WidgetInfo.BANK_PIN_4,
		WidgetInfo.BANK_PIN_5,
		WidgetInfo.BANK_PIN_6,
		WidgetInfo.BANK_PIN_7,
		WidgetInfo.BANK_PIN_8,
		WidgetInfo.BANK_PIN_9,
		WidgetInfo.BANK_PIN_10
	);

	private static final String DEPOSIT_WORN = "Deposit worn items";
	private static final String DEPOSIT_INVENTORY = "Deposit inventory";
	private static final String DEPOSIT_LOOT = "Deposit loot";
	private static final String DISABLE = "Disable";
	private static final String ENABLE = "Enable";
	private static final String RELEASE_ALL_PLACEHOLDERS = "Release all placeholders";
	private static final String SEARCH = "Search";
	private static final String FILL = "Fill";

	private static final String SEED_VAULT_TITLE = "Seed Vault";
	private static final int PIN_FONT_OFFSET = 5;

	private static final String NUMBER_REGEX = "[0-9]+(\\.[0-9]+)?[kmb]?";
	private static final Pattern VALUE_SEARCH_PATTERN = Pattern.compile("^(?<mode>ge|ha|alch)?" +
		" *(((?<op>[<>=]|>=|<=) *(?<num>" + NUMBER_REGEX + "))|" +
		"((?<num1>" + NUMBER_REGEX + ") *- *(?<num2>" + NUMBER_REGEX + ")))$", Pattern.CASE_INSENSITIVE);

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ItemManager itemManager;

	@Inject
	private BankConfig config;

	@Inject
	private BankSearch bankSearch;

	@Inject
	private KeyManager keyManager;

	private boolean forceRightClickFlag;
	private Multiset<Integer> itemQuantities; // bank item quantities for bank value search
	private String searchString;

	@Provides
	BankConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BankConfig.class);
	}

	@Override
	protected void startUp()
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			keyManager.registerKeyListener(this);
		}
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(this);
		clientThread.invokeLater(() -> bankSearch.reset(false));
		forceRightClickFlag = false;
		itemQuantities = null;
		searchString = null;
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			keyManager.unregisterKeyListener(this);
			return;
		}

		keyManager.registerKeyListener(this);
	}

	@Subscribe
	private void onMenuShouldLeftClick(MenuShouldLeftClick event)
	{
		if (!forceRightClickFlag)
		{
			return;
		}

		forceRightClickFlag = false;
		MenuEntry[] menuEntries = client.getMenuEntries();
		for (MenuEntry entry : menuEntries)
		{
			if ((entry.getOption().equals(DEPOSIT_WORN) && config.rightClickBankEquip())
				|| (entry.getOption().equals(DEPOSIT_INVENTORY) && config.rightClickBankInventory())
				|| (entry.getOption().equals(DEPOSIT_LOOT) && config.rightClickBankLoot())
				|| (entry.getOption().equals(DISABLE) && config.rightClickSetPlaceholders())
				|| (entry.getOption().equals(ENABLE) && config.rightClickSetPlaceholders())
				|| (entry.getOption().equals(RELEASE_ALL_PLACEHOLDERS) && config.rightClickReleasePlaceholders())
				|| (entry.getOption().equals(SEARCH) && config.rightClickSearch())
				|| (entry.getOption().equals(FILL) && config.rightClickFillBankFiller()))
			{
				event.setForceRightClick(true);
				return;
			}
		}
	}

	@Subscribe
	private void onMenuEntryAdded(MenuEntryAdded event)
	{
		if ((event.getOption().equals(DEPOSIT_WORN) && config.rightClickBankEquip())
			|| (event.getOption().equals(DEPOSIT_INVENTORY) && config.rightClickBankInventory())
			|| (event.getOption().equals(DEPOSIT_LOOT) && config.rightClickBankLoot())
			|| (event.getOption().startsWith(DISABLE) && config.rightClickSetPlaceholders())
			|| (event.getOption().startsWith(ENABLE) && config.rightClickSetPlaceholders())
			|| (event.getOption().equals(RELEASE_ALL_PLACEHOLDERS) && config.rightClickReleasePlaceholders())
			|| (event.getOption().equals(SEARCH) && config.rightClickSearch())
			|| (event.getOption().equals(FILL) && config.rightClickFillBankFiller()))
		{
			forceRightClickFlag = true;
		}
	}

	@Subscribe
	private void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		if (event.getEventName().equals("bankPinButtons") && config.largePinNumbers())
		{
			updateBankPinSizes();
		}

		int[] intStack = client.getIntStack();
		String[] stringStack = client.getStringStack();
		int intStackSize = client.getIntStackSize();
		int stringStackSize = client.getStringStackSize();

		switch (event.getEventName())
		{
			case "bankSearchFilter":
				int itemId = intStack[intStackSize - 1];
				String search = stringStack[stringStackSize - 1];

				if (valueSearch(itemId, search))
				{
					// return true
					intStack[intStackSize - 2] = 1;
				}

				break;
		}
	}

	@Subscribe
	private void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() != WidgetID.SEED_VAULT_GROUP_ID || !config.seedVaultValue())
		{
			return;
		}

		updateSeedVaultTotal();
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() == ScriptID.BANKMAIN_BUILD)
		{
			// Compute bank prices using only the shown items so that we can show bank value during searches
			final Widget bankItemContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
			final ItemContainer bankContainer = client.getItemContainer(InventoryID.BANK);
			final Widget[] children = bankItemContainer.getChildren();
			long geTotal = 0, haTotal = 0;

			log.debug("Computing bank price of {} items", bankContainer.size());

			// The first components are the bank items, followed by tabs etc. There are always 816 components regardless
			// of bank size, but we only need to check up to the bank size.
			for (int i = 0; i < bankContainer.size(); ++i)
			{
				Widget child = children[i];
				if (child != null && !child.isSelfHidden() && child.getItemId() > -1)
				{
					final int alchPrice = getHaPrice(child.getItemId());
					geTotal += (long) itemManager.getItemPrice(child.getItemId()) * child.getItemQuantity();
					haTotal += (long) alchPrice * child.getItemQuantity();
				}
			}

			Widget bankTitle = client.getWidget(WidgetInfo.BANK_TITLE_BAR);
			bankTitle.setText(bankTitle.getText() + createValueText(geTotal, haTotal));
		}
		else if (event.getScriptId() == ScriptID.BANKMAIN_SEARCH_REFRESH)
		{
			// vanilla only lays out the bank every 40 client ticks, so if the search input has changed,
			// and the bank wasn't laid out this tick, lay it out early
			final String inputText = client.getVar(VarClientStr.INPUT_TEXT);
			if (searchString != inputText && client.getGameCycle() % 40 != 0)
			{
				clientThread.invokeLater(bankSearch::layoutBank);
				searchString = inputText;
			}
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		int containerId = event.getContainerId();

		if (containerId == InventoryID.BANK.getId())
		{
			itemQuantities = null;
		}
		else if (containerId == InventoryID.SEED_VAULT.getId() && config.seedVaultValue())
		{
			updateSeedVaultTotal();
		}
	}

	private String createValueText(long gePrice, long haPrice)
	{
		StringBuilder stringBuilder = new StringBuilder();
		if (config.showGE() && gePrice != 0)
		{
			stringBuilder.append(" (");

			if (config.showHA())
			{
				stringBuilder.append("GE: ");
			}

			if (config.showExact())
			{
				stringBuilder.append(QuantityFormatter.formatNumber(gePrice));
			}
			else
			{
				stringBuilder.append(QuantityFormatter.quantityToStackSize(gePrice));
			}
			stringBuilder.append(')');
		}

		if (config.showHA() && haPrice != 0)
		{
			stringBuilder.append(" (");

			if (config.showGE())
			{
				stringBuilder.append("HA: ");
			}

			if (config.showExact())
			{
				stringBuilder.append(QuantityFormatter.formatNumber(haPrice));
			}
			else
			{
				stringBuilder.append(QuantityFormatter.quantityToStackSize(haPrice));
			}

			stringBuilder.append(')');
		}

		return stringBuilder.toString();
	}

	private void updateSeedVaultTotal()
	{
		final Widget titleContainer = client.getWidget(WidgetInfo.SEED_VAULT_TITLE_CONTAINER);
		if (titleContainer == null)
		{
			return;
		}

		final Widget title = titleContainer.getChild(1);
		if (title == null)
		{
			return;
		}

		final ContainerPrices prices = calculate(getSeedVaultItems());
		if (prices == null)
		{
			return;
		}

		final String titleText = createValueText(prices.getGePrice(), prices.getHighAlchPrice());
		title.setText(SEED_VAULT_TITLE + titleText);
	}

	private Item[] getSeedVaultItems()
	{
		final ItemContainer itemContainer = client.getItemContainer(InventoryID.SEED_VAULT);
		if (itemContainer == null)
		{
			return null;
		}

		return itemContainer.getItems();
	}

	private void updateBankPinSizes()
	{
		for (final WidgetInfo widgetInfo : BANK_PINS)
		{
			final Widget pin = client.getWidget(widgetInfo);
			if (pin == null)
			{
				continue;
			}

			final Widget[] children = pin.getDynamicChildren();
			if (children.length < 2)
			{
				continue;
			}

			final Widget button = children[0];
			final Widget number = children[1];

			// Change to a bigger font size
			number.setFontId(FontID.QUILL_CAPS_LARGE);
			number.setYTextAlignment(0);

			// Change size to match container widths
			number.setOriginalWidth(button.getWidth());
			// The large font id text isn't centered, we need to offset it slightly
			number.setOriginalHeight(button.getHeight() + PIN_FONT_OFFSET);
			number.setOriginalY(-PIN_FONT_OFFSET);
			number.setOriginalX(0);

			number.revalidate();
		}
	}

	@VisibleForTesting
	public boolean valueSearch(final int itemId, final String str)
	{
		final Matcher matcher = VALUE_SEARCH_PATTERN.matcher(str);
		if (!matcher.matches())
		{
			return false;
		}

		// Count bank items and remember it for determining item quantity
		if (itemQuantities == null)
		{
			itemQuantities = getBankItemSet();
		}

		final ItemDefinition itemComposition = itemManager.getItemDefinition(itemId);
		final int qty = itemQuantities.count(itemId);
		final long gePrice = (long) itemManager.getItemPrice(itemId) * qty;
		final long haPrice = (long) itemComposition.getHaPrice() * qty;

		long value = Math.max(gePrice, haPrice);

		final String mode = matcher.group("mode");
		if (mode != null)
		{
			value = mode.toLowerCase().equals("ge") ? gePrice : haPrice;
		}

		final String op = matcher.group("op");
		if (op != null)
		{
			long compare;
			try
			{
				compare = QuantityFormatter.parseQuantity(matcher.group("num"));
			}
			catch (ParseException e)
			{
				return false;
			}

			switch (op)
			{
				case ">":
					return value > compare;
				case "<":
					return value < compare;
				case "=":
					return value == compare;
				case ">=":
					return value >= compare;
				case "<=":
					return value <= compare;
			}
		}

		final String num1 = matcher.group("num1");
		final String num2 = matcher.group("num2");
		if (num1 != null && num2 != null)
		{
			long compare1, compare2;
			try
			{
				compare1 = QuantityFormatter.parseQuantity(num1);
				compare2 = QuantityFormatter.parseQuantity(num2);
			}
			catch (ParseException e)
			{
				return false;
			}

			return compare1 <= value && compare2 >= value;
		}

		return false;
	}

	private Multiset<Integer> getBankItemSet()
	{
		ItemContainer itemContainer = client.getItemContainer(InventoryID.BANK);
		if (itemContainer == null)
		{
			return HashMultiset.create();
		}

		Multiset<Integer> set = HashMultiset.create();
		for (Item item : itemContainer.getItems())
		{
			if (item.getId() != ItemID.BANK_FILLER)
			{
				set.add(item.getId(), item.getQuantity());
			}
		}
		return set;
	}

	@Nullable
	public ContainerPrices calculate(@Nullable Item[] items)
	{
		if (items == null)
		{
			return null;
		}

		long ge = 0;
		long alch = 0;

		for (final Item item : items)
		{
			final int qty = item.getQuantity();
			final int id = item.getId();

			if (id <= 0 || qty == 0)
			{
				continue;
			}

			alch += (long) getHaPrice(id) * qty;
			ge += (long) itemManager.getItemPrice(id) * qty;
		}

		return new ContainerPrices(ge, alch);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (config.ctrlfSearch() && e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F)
		{
			Widget bankContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
			if (bankContainer == null || bankContainer.isHidden())
			{
				return;
			}

			bankSearch.initSearch();
			e.consume();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	private int getHaPrice(int itemId)
	{
		switch (itemId)
		{
			case ItemID.COINS_995:
				return 1;
			case ItemID.PLATINUM_TOKEN:
				return 1000;
			default:
				return itemManager.getItemDefinition(itemId).getHaPrice();
		}
	}
}
