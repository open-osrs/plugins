/*
 * Copyright (c) 2018, Daddy Dozer <https://github.com/Dyldozer>
 * Copyright (c) 2018, Davis Cook <daviscook447@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *	list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *	this list of conditions and the following disclaimer in the documentation
 *	and/or other materials provided with the distribution.
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
package net.runelite.client.plugins.suppliestracker.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import lombok.AccessLevel;
import lombok.Getter;
import static net.runelite.api.ItemID.*;
import net.runelite.api.util.Text;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.suppliestracker.ItemType;
import net.runelite.client.plugins.suppliestracker.SuppliesTrackerItem;
import net.runelite.client.plugins.suppliestracker.SuppliesTrackerPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.QuantityFormatter;

@Singleton
public abstract class SuppliesBox extends JPanel
{
	private static final int ITEMS_PER_ROW = 5;
	protected final SuppliesTrackerPlugin plugin;
	private final JPanel itemContainer = new JPanel();
	private final JLabel priceLabel = new JLabel();
	private final JLabel subTitleLabel = new JLabel();
	private final SuppliesTrackerPanel panel;
	ItemManager itemManager;

	@Getter(AccessLevel.PUBLIC)
	private final String id;
	@Getter(AccessLevel.PUBLIC)
	private final ItemType type;

	@Getter(AccessLevel.PUBLIC)
	private final List<SuppliesTrackerItem> trackedItems = new ArrayList<>();
	private long totalPrice;

	protected SuppliesBox(
		ItemManager itemManager,
		String id,
		SuppliesTrackerPlugin plugin,
		SuppliesTrackerPanel panel,
		ItemType type)
	{
		this.itemManager = itemManager;
		this.id = id;
		this.plugin = plugin;
		this.panel = panel;
		this.type = type;

		render();
	}

	abstract String buildTooltip(int itemId, int qty, SuppliesTrackerItem item);

	public static SuppliesBox of(
		ItemManager itemManager,
		String id,
		SuppliesTrackerPlugin plugin,
		SuppliesTrackerPanel panel,
		ItemType type)
	{
		switch (type)
		{
			case JEWELLERY:
				return new JewellerySuppliesBox(itemManager, id, plugin, panel, type);
			case CHARGES:
				return new ChargesSuppliesBox(itemManager, id, plugin, panel, type);
			case FOOD:
				return new FoodSuppliesBox(itemManager, id, plugin, panel, type);
			case POTION:
				return new PotionSuppliesBox(itemManager, id, plugin, panel, type);
			case DEATH:
				return new DeathSuppliesBox(itemManager, id, plugin, panel, type);
		}

		return new DefaultSuppliesBox(itemManager, id, plugin, panel, type);
	}

	/**
	 * Builds the box onto the panel
	 */
	private void render()
	{
		setLayout(new BorderLayout(0, 1));
		setBorder(new EmptyBorder(5, 0, 0, 0));

		final JPanel logTitle = new JPanel(new BorderLayout(5, 0));
		logTitle.setBorder(new EmptyBorder(7, 7, 7, 7));
		logTitle.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());

		final JLabel titleLabel = new JLabel(Text.removeTags(id));
		titleLabel.setFont(FontManager.getRunescapeSmallFont());
		titleLabel.setForeground(Color.WHITE);

		logTitle.add(titleLabel, BorderLayout.WEST);

		subTitleLabel.setFont(FontManager.getRunescapeSmallFont());
		subTitleLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		logTitle.add(subTitleLabel, BorderLayout.CENTER);

		priceLabel.setFont(FontManager.getRunescapeSmallFont());
		priceLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		logTitle.add(priceLabel, BorderLayout.EAST);

		add(logTitle, BorderLayout.NORTH);
		add(itemContainer, BorderLayout.CENTER);

		// Create popup menu
		final JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
		setComponentPopupMenu(popupMenu);

		// Create reset menu
		final JMenuItem reset = new JMenuItem("Reset Category");
		reset.addActionListener(e ->
		{
			for (SuppliesTrackerItem item : trackedItems)
			{
				plugin.clearItem(item.getId());
			}
			clearAll();
			rebuild();
			panel.updateOverall();
		});

		popupMenu.add(reset);

		setVisible(false);
	}

	/**
	 * Adds item to trackedItems
	 *
	 * @param item item to be checked
	 */
	public void update(SuppliesTrackerItem item)
	{
		trackedItems.removeIf(r -> r.getId() == item.getId());
		if (item.getName() == null || item.getId() == 0 || item.getName().toLowerCase().equals("null"))
		{
			plugin.clearItem(item.getId());
			return;
		}
		trackedItems.add(item);
		setVisible(trackedItems.size() > 0);
	}

	/**
	 * Removes item from trackedItems
	 *
	 * @param item item to be checked
	 */
	private void remove(SuppliesTrackerItem item)
	{
		trackedItems.removeIf(r -> r.getId() == item.getId());
		plugin.clearItem(item.getId());
		setVisible(trackedItems.size() > 0);
	}

	/**
	 * Clears trackedItems
	 */
	public void clearAll()
	{
		trackedItems.clear();
		totalPrice = 0;
		setVisible(false);
	}

	/**
	 * Adds the total cost of all items to be returned
	 *
	 * @return the total cost of all tracked items
	 */
	public long getTotalSupplies()
	{
		long totalSupplies = 0;
		for (SuppliesTrackerItem item : trackedItems)
		{
			totalSupplies += item.getQuantity();
		}
		return totalSupplies;
	}

	public long getTotalPrice()
	{
		return totalPrice;
	}

	/**
	 * Runs buildItems method and recalculates supplies cost and quantity.
	 */
	public void rebuild()
	{
		buildItems();

		priceLabel.setText(QuantityFormatter.quantityToStackSize(totalPrice) + " gp");
		priceLabel.setToolTipText(QuantityFormatter.formatNumber(totalPrice) + " gp");

		final long supplies = getTotalSupplies();
		if (supplies > 0)
		{
			subTitleLabel.setText("x " + supplies);
		}
		else
		{
			subTitleLabel.setText("");
		}

		validate();
		repaint();
	}

	/**
	 * Changes itemId to single use variant
	 *
	 * @param name   name to be checked
	 * @param itemId is either changed or returned to method
	 * @return returns updated itemId
	 */
	abstract int getModifiedItemId(String name, int itemId);

	/**
	 * Builds an arraylist of items based off trackedItems and populates
	 * boxes with item information
	 */
	void buildItems()
	{
		final List<SuppliesTrackerItem> items = new ArrayList<>(trackedItems);
		totalPrice = 0;

		for (SuppliesTrackerItem item : items)
		{
			if (item.getId() == HEALER_ICON_20802 || item.getId() == HEALER_ICON_22308)
			{
				totalPrice += item.getQuantity() * 100000;
			}
			else
			{
				totalPrice += item.getPrice();
			}
		}

		items.sort((i1, i2) -> Long.compare(i2.getPrice(), i1.getPrice()));

		// calculates how many rows need to be displayed to fit all item
		final int rowSize = ((items.size() % ITEMS_PER_ROW == 0) ? 0 : 1) + items.size() / ITEMS_PER_ROW;

		itemContainer.removeAll();
		itemContainer.setLayout(new GridLayout(rowSize, ITEMS_PER_ROW, 1, 1));

		for (int i = 0; i < rowSize * ITEMS_PER_ROW; i++)
		{
			final JPanel slotContainer = new JPanel();
			slotContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

			if (i < items.size())
			{
				final SuppliesTrackerItem item = items.get(i);
				final JLabel imageLabel = new JLabel();
				imageLabel.setToolTipText(buildTooltip(getModifiedItemId(item.getName(), item.getId()), item.getQuantity(), item));
				imageLabel.setVerticalAlignment(SwingConstants.CENTER);
				imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

				AsyncBufferedImage itemImage = itemManager.getImage(getModifiedItemId(item.getName(), item.getId()), item.getQuantity(), item.getQuantity() > 1);

				if (item.getId() == HEALER_ICON_20802)
				{
					itemImage = itemManager.getImage(LIL_ZIK, item.getQuantity(), item.getQuantity() > 1);
				}

				if (item.getId() == HEALER_ICON_22308)
				{
					itemImage = itemManager.getImage(VORKI, item.getQuantity(), item.getQuantity() > 1);
				}

				itemImage.addTo(imageLabel);
				slotContainer.add(imageLabel);

				if (item.getName() == null || item.getId() == 0
					|| item.getName().toLowerCase().equals("null")
					|| getModifiedItemId(item.getName(), item.getId()) == 0
					|| itemManager.getImage(getModifiedItemId(item.getName(), item.getId()), item.getQuantity(), item.getQuantity() > 1) == null)
				{
					continue;
				}

				// create popup menu
				final JPopupMenu popupMenu = new JPopupMenu();
				popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
				slotContainer.setComponentPopupMenu(popupMenu);

				final JMenuItem reset = new JMenuItem("Reset");
				reset.addActionListener(e ->
				{
					remove(item);
					rebuild();
					panel.updateOverall();
				});

				popupMenu.add(reset);
			}
			itemContainer.add(slotContainer);
		}
		itemContainer.repaint();
	}

	private static class JewellerySuppliesBox extends SuppliesBox
	{
		protected JewellerySuppliesBox(ItemManager itemManager, String id, SuppliesTrackerPlugin plugin, SuppliesTrackerPanel panel, ItemType type)
		{
			super(itemManager, id, plugin, panel, type);
		}

		@Override
		final String buildTooltip(int itemId, int qty, SuppliesTrackerItem item)
		{
			String name = item.getName();
			StringBuilder tooltip = new StringBuilder();
			long price = item.getPrice();

			if (name.toLowerCase().contains("glory"))
			{
				tooltip.append("Amulet of Glory(6) x ")
					.append(qty)
					.append("/6 (")
					.append(QuantityFormatter.quantityToStackSize(price))
					.append("gp)");
			}
			else if (name.toLowerCase().contains("dueling"))
			{
				tooltip.append("Ring of Dueling(8) x ")
					.append(qty)
					.append("/8 (")
					.append(QuantityFormatter.quantityToStackSize(price))
					.append("gp)");
			}
			else if (name.toLowerCase().contains("wealth"))
			{
				tooltip.append("Ring of Wealth(5) x ")
					.append(qty)
					.append("/5 (")
					.append(QuantityFormatter.quantityToStackSize(price))
					.append("gp)");
			}
			else if (name.toLowerCase().contains("combat"))
			{
				tooltip.append("Combat Bracelet(6) x ")
					.append(qty)
					.append("/6 (")
					.append(QuantityFormatter.quantityToStackSize(price))
					.append("gp)");
			}
			else if (name.toLowerCase().contains("games"))
			{
				tooltip.append("Games Necklace(8) x ")
					.append(qty)
					.append("/8 (")
					.append(QuantityFormatter.quantityToStackSize(price))
					.append("gp)");
			}
			else if (name.toLowerCase().contains("skills"))
			{
				tooltip.append("Skills Necklace(6) x ")
					.append(qty)
					.append("/6 (")
					.append(QuantityFormatter.quantityToStackSize(price))
					.append("gp)");
			}
			else if (name.toLowerCase().contains("passage"))
			{
				tooltip.append("Necklace of Passage(5) x ")
					.append(qty)
					.append("/5 (")
					.append(QuantityFormatter.quantityToStackSize(price))
					.append("gp)");
			}
			else if (name.toLowerCase().contains("burning"))
			{
				tooltip.append("Burning Amulet(5) x ")
					.append(qty)
					.append("/5 (")
					.append(QuantityFormatter.quantityToStackSize(price))
					.append("gp)");
			}
			return tooltip.toString();
		}

		@Override
		int getModifiedItemId(String name, int itemId)
		{
			return itemId;
		}
	}

	private static class ChargesSuppliesBox extends SuppliesBox
	{
		protected ChargesSuppliesBox(ItemManager itemManager, String id, SuppliesTrackerPlugin plugin, SuppliesTrackerPanel panel, ItemType type)
		{
			super(itemManager, id, plugin, panel, type);
		}

		@Override
		final String buildTooltip(int itemId, int qty, SuppliesTrackerItem item)
		{
			StringBuilder tooltip = new StringBuilder();

			switch (itemId)
			{
				case SCYTHE_OF_VITUR:
					tooltip.append("<html>")
						.append("Blood Rune x ")
						.append(qty * 3)
						.append("<br>")
						.append("Vial of Blood x ")
						.append(qty).append("/100")
						.append("</br>")
						.append("<br>")
						.append("Total: ")
						.append(QuantityFormatter.quantityToStackSize(item.getPrice()))
						.append("gp")
						.append("</html>");
					return tooltip.toString();

				case SANGUINESTI_STAFF:

					tooltip.append("Blood Rune x ")
						.append(qty * 3).append(" (")
						.append(QuantityFormatter.quantityToStackSize(item.getPrice()))
						.append("gp)");
					return tooltip.toString();

				case BLADE_OF_SAELDOR:

					tooltip.append("Crystal Shard x ")
						.append(qty).append(" / 100");
					return tooltip.toString();

				case TRIDENT_OF_THE_SEAS:
					tooltip.append("<html>")
						.append("Chaos Rune x ")
						.append(qty)
						.append("<br>")
						.append("Death Rune x ")
						.append(qty)
						.append("</br>")
						.append("<br>")
						.append("Fire Rune x ")
						.append(qty * 5)
						.append("</br>")
						.append("<br>")
						.append("Coins x ")
						.append(qty * 10)
						.append("</br>")
						.append("<br>")
						.append("Total: ")
						.append(QuantityFormatter.quantityToStackSize(item.getPrice()))
						.append("gp")
						.append("</html>");
					return tooltip.toString();

				case TRIDENT_OF_THE_SWAMP:
					tooltip.append("<html>")
						.append("Chaos Rune x ")
						.append(qty)
						.append("<br>")
						.append("Death Rune x ")
						.append(qty)
						.append("</br>")
						.append("<br>")
						.append("Fire Rune x ")
						.append(qty * 5)
						.append("</br>")
						.append("<br>")
						.append("Zulrah's Scales x ")
						.append(qty)
						.append("</br>")
						.append("<br>")
						.append("Total: ")
						.append(QuantityFormatter.quantityToStackSize(item.getPrice()))
						.append("gp")
						.append("</html>");
					return tooltip.toString();
			}

			return tooltip.toString();
		}

		@Override
		int getModifiedItemId(String name, int itemId)
		{
			return itemId;
		}
	}

	private static class FoodSuppliesBox extends SuppliesBox
	{
		protected FoodSuppliesBox(ItemManager itemManager, String id, SuppliesTrackerPlugin plugin, SuppliesTrackerPanel panel, ItemType type)
		{
			super(itemManager, id, plugin, panel, type);
		}

		@Override
		final String buildTooltip(int itemId, int qty, SuppliesTrackerItem item)
		{
			final long price = item.getPrice();
			return item.getName() + " x " + qty + " (" + QuantityFormatter.quantityToStackSize(price) + "gp) ";
		}

		@Override
		int getModifiedItemId(String name, int itemId)
		{
			if (SuppliesTrackerPlugin.isCake(name, itemId))
			{
				return getSlice(itemId);
			}
			if (SuppliesTrackerPlugin.isPizzaPie(name))
			{
				return getHalf(itemId);
			}

			return itemId;
		}

		//Switches full cake ids to get the image for slice
		private static int getSlice(int itemId)
		{
			switch (itemId)
			{
				case CAKE:
					itemId = SLICE_OF_CAKE;
					break;
				case CHOCOLATE_CAKE:
					itemId = CHOCOLATE_SLICE;
					break;
			}
			return itemId;
		}

		//Switches full pizza and pie ids to get the image for half
		private static int getHalf(int itemId)
		{
			switch (itemId)
			{
				case ANCHOVY_PIZZA:
					itemId = _12_ANCHOVY_PIZZA;
					break;
				case MEAT_PIZZA:
					itemId = _12_MEAT_PIZZA;
					break;
				case PINEAPPLE_PIZZA:
					itemId = _12_PINEAPPLE_PIZZA;
					break;
				case PLAIN_PIZZA:
					itemId = _12_PLAIN_PIZZA;
					break;
				case REDBERRY_PIE:
					itemId = HALF_A_REDBERRY_PIE;
					break;
				case GARDEN_PIE:
					itemId = HALF_A_GARDEN_PIE;
					break;
				case SUMMER_PIE:
					itemId = HALF_A_SUMMER_PIE;
					break;
				case FISH_PIE:
					itemId = HALF_A_FISH_PIE;
					break;
				case BOTANICAL_PIE:
					itemId = HALF_A_BOTANICAL_PIE;
					break;
				case MUSHROOM_PIE:
					itemId = HALF_A_MUSHROOM_PIE;
					break;
				case ADMIRAL_PIE:
					itemId = HALF_AN_ADMIRAL_PIE;
					break;
				case WILD_PIE:
					itemId = HALF_A_WILD_PIE;
					break;
				case APPLE_PIE:
					itemId = HALF_AN_APPLE_PIE;
					break;
				case MEAT_PIE:
					itemId = HALF_A_MEAT_PIE;
					break;

			}
			return itemId;
		}
	}

	private static class PotionSuppliesBox extends SuppliesBox
	{

		protected PotionSuppliesBox(ItemManager itemManager, String id, SuppliesTrackerPlugin plugin, SuppliesTrackerPanel panel, ItemType type)
		{
			super(itemManager, id, plugin, panel, type);

		}

		@Override
		final String buildTooltip(int itemId, int qty, SuppliesTrackerItem item)
		{

			final long price = item.getPrice();
			return item.getName() + " x " + qty + "/4 (" + QuantityFormatter.quantityToStackSize(price) + "gp) ";
		}

		@Override
		int getModifiedItemId(String name, int itemId)
		{
			if (name.contains("(4)") ||
				name.contains("(3)") ||
				name.contains("(2)") ||
				name.contains("(1)"))
			{
				return getSingleDose(name);
			}

			return itemId;
		}

		/**
		 * Turns a potion itemid into the single dose id
		 *
		 * @param name potion name to be checked
		 * @return itemid of single dose potion
		 */
		private int getSingleDose(String name)
		{
			String nameModified = name.replace("(4)", "(1)");
			int itemId = 0;


			if (itemManager.search(nameModified).size() > 0)
			{
				itemId = itemManager.search(nameModified).get(0).getId();
			}

			return itemId;
		}
	}

	private static class DeathSuppliesBox extends SuppliesBox
	{
		protected DeathSuppliesBox(ItemManager itemManager, String id, SuppliesTrackerPlugin plugin, SuppliesTrackerPanel panel, ItemType type)
		{
			super(itemManager, id, plugin, panel, type);
		}

		@Override
		final String buildTooltip(int itemId, int qty, SuppliesTrackerItem item)
		{
			if (itemId == HEALER_ICON_20802)
			{
				final long price = 100000 * qty;
				return "ToB Deaths" + " x " + qty + " (" + QuantityFormatter.quantityToStackSize(price) + "gp) ";
			}
			if (itemId == HEALER_ICON_22308)
			{
				final long price = 100000 * qty;
				return "Vorkath Deaths" + " x " + qty + " (" + QuantityFormatter.quantityToStackSize(price) + "gp) ";
			}
			return "";
		}

		@Override
		int getModifiedItemId(String name, int itemId)
		{
			return itemId;
		}
	}


	private static class DefaultSuppliesBox extends SuppliesBox
	{
		protected DefaultSuppliesBox(ItemManager itemManager, String id, SuppliesTrackerPlugin plugin, SuppliesTrackerPanel panel, ItemType type)
		{
			super(itemManager, id, plugin, panel, type);
		}

		@Override
		final String buildTooltip(int itemId, int qty, SuppliesTrackerItem item)
		{

			final long price = item.getPrice();
			return item.getName() + " x " + qty + " (" + QuantityFormatter.quantityToStackSize(price) + ") ";
		}

		@Override
		int getModifiedItemId(String name, int itemId)
		{
			return itemId;
		}
	}
}