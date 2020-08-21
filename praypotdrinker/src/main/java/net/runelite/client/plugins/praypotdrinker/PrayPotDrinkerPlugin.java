package net.runelite.client.plugins.praypotdrinker;

import com.google.inject.Provides;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Prayer Pot Drinker",
	description = "Automatically drink pray pots",
	tags = {"combat", "notifications", "prayer"},
	enabledByDefault = false,
	type = PluginType.PVM
)
public class PrayPotDrinkerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private PrayPotDrinkerConfig config;

	@Inject
	private ItemManager itemManager;

	private MenuEntry entry;

	private Random r = new Random();
	private int nextRestoreVal = 0;

	@Provides
	PrayPotDrinkerConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(PrayPotDrinkerConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		nextRestoreVal = r.nextInt(config.maxPrayerLevel() - config.minPrayerLevel()) + config.minPrayerLevel();
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("praypotdrinker"))
		{
			return;
		}

		nextRestoreVal = r.nextInt(config.maxPrayerLevel() - config.minPrayerLevel()) + config.minPrayerLevel();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		try
		{
			WidgetItem restoreItem = getRestoreItem();

			if (restoreItem == null)
			{
				return;
			}

			int currentPrayerPoints = client.getBoostedSkillLevel(Skill.PRAYER);
			int prayerLevel = client.getRealSkillLevel(Skill.PRAYER);
			int boostAmount = getBoostAmount(restoreItem, prayerLevel);

			if (currentPrayerPoints + boostAmount > prayerLevel)
			{
				return;
			}

			if (currentPrayerPoints <= nextRestoreVal)
			{
				entry = getConsumableEntry(itemManager.getItemDefinition(restoreItem.getId()).getName(), restoreItem.getId(), restoreItem.getIndex());
				click();
				nextRestoreVal = r.nextInt(config.maxPrayerLevel() - config.minPrayerLevel()) + config.minPrayerLevel();
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
		return new MenuEntry("Drink", "<col=ff9040>" + itemName, itemId, MenuOpcode.ITEM_FIRST_OPTION.getId(), itemIndex, WidgetInfo.INVENTORY.getId(), false);
	}

	public WidgetItem getRestoreItem()
	{
		WidgetItem item;

		item = PrayerRestoreType.PRAYER_POTION.getItemFromInventory(client);

		if (item != null)
		{
			return item;
		}

		item = PrayerRestoreType.SANFEW_SERUM.getItemFromInventory(client);

		if (item != null)
		{
			return item;
		}

		item = PrayerRestoreType.SUPER_RESTORE.getItemFromInventory(client);

		return item;
	}

	public int getBoostAmount(WidgetItem restoreItem, int prayerLevel)
	{
		if (PrayerRestoreType.PRAYER_POTION.containsId(restoreItem.getId()))
		{
			return 7 + (int) Math.floor(prayerLevel * .25);
		}
		else if (PrayerRestoreType.SANFEW_SERUM.containsId(restoreItem.getId()))
		{
			return 4 + (int) Math.floor(prayerLevel * (double)(3 / 10));
		}
		else if (PrayerRestoreType.SUPER_RESTORE.containsId(restoreItem.getId()))
		{
			return 8 + (int) Math.floor(prayerLevel * .25);
		}

		return 0;
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
