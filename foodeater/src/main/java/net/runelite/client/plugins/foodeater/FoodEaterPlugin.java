package net.runelite.client.plugins.foodeater;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Extension
@PluginDescriptor(
	name = "Food Eater",
	description = "Automatically eats food",
	tags = {"combat", "notifications", "health", "food", "eat"},
	enabledByDefault = false,
	type = PluginType.PVM
)
public class FoodEaterPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private Notifier notifier;

	@Inject
	private FoodEaterConfig config;

	private MenuEntry entry;

	@Provides
	FoodEaterConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(FoodEaterConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Subscribe
	public void onGameTick(final GameTick event)
	{
		try
		{
			int health = this.client.getBoostedSkillLevel(Skill.HITPOINTS);

			if (health > this.config.minimumHealth())
			{
				return;
			}

			Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

			if (inventory == null)
			{
				return;
			}

			for (WidgetItem item : inventory.getWidgetItems())
			{
				final String name = this.itemManager.getItemDefinition(item.getId()).getName();

				if (name.equalsIgnoreCase(this.config.foodToEat()))
				{
					entry = getConsumableEntry(name, item.getId(), item.getIndex());
					click();
					return;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (entry != null)
		{
			event.setMenuEntry(entry);
		}

		entry = null;
	}

	private MenuEntry getConsumableEntry(String itemName, int itemId, int itemIndex)
	{
		return new MenuEntry("Eat", "<col=ff9040>" + itemName, itemId, MenuOpcode.ITEM_FIRST_OPTION.getId(), itemIndex, WidgetInfo.INVENTORY.getId(), false);
	}

	public void click()
	{
		Point pos = client.getMouseCanvasPosition();

		if (client.isStretchedEnabled())
		{
			final Dimension stretched = client.getStretchedDimensions();
			final Dimension real = client.getRealDimensions();
			final double width = (stretched.width / real.getWidth());
			final double height = (stretched.height / real.getHeight());
			final Point point = new Point((int) (pos.getX() * width), (int) (pos.getY() * height));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			return;
		}

		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
	}
}
