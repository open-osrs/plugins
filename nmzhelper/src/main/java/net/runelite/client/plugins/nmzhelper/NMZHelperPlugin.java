package net.runelite.client.plugins.nmzhelper;

import com.google.inject.Provides;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
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
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Extension
@PluginDescriptor(
	name = "NMZ Helper",
	description = "An automation utility for NMZ",
	tags = {"combat", "potion", "overload", "absorption", "nmz", "nightmare", "zone", "helper"},
	enabledByDefault = false,
	type = PluginType.MINIGAME
)
public class NMZHelperPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private NMZHelperConfig config;

	@Inject
	private ItemManager itemManager;

	private MenuEntry entry;

	private static final int[] NMZ_MAP_REGION = {9033};

	private int rockCakeDelay = 0;

	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 25, TimeUnit.SECONDS, queue,
		new ThreadPoolExecutor.DiscardPolicy());

	@Provides
	NMZHelperConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(NMZHelperConfig.class);
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
	private void onChatMessage(ChatMessage event)
	{
		if (!isInNightmareZone())
		{
			return;
		}

		String msg = Text.removeTags(event.getMessage()); //remove color

		if (event.getType() == ChatMessageType.SPAM
			&& msg.contains("You drink some of your overload potion."))
		{
			rockCakeDelay = 12;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (!isInNightmareZone())
		{
			return;
		}

		checkRockCake();
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (!isInNightmareZone())
		{
			return;
		}

		checkOverload();
		checkAbsorption();
	}

	private void checkRockCake()
	{
		if (!config.autoRockCake())
		{
			return;
		}

		//check if overloaded
		if (!isOverloaded())
		{
			return;
		}

		if (rockCakeDelay > 0)
		{
			rockCakeDelay--;
			return;
		}

		//check if we're already rock caked down to 1 hp
		if (client.getBoostedSkillLevel(Skill.HITPOINTS) <= 1)
		{
			return;
		}

		guzzleRockCake();

		//delay rock click by random number of ticks (max specified in config)
		if (client.getRealSkillLevel(Skill.HITPOINTS) - client.getBoostedSkillLevel(Skill.HITPOINTS) >= 10)
		{
			rockCakeDelay = (int) (Math.random() * ((config.maxRockCakeDelay() - 1) + 1)) + 1;
		}
	}

	private boolean isOverloaded()
	{
		int overloadVarbit = client.getVar(Varbits.NMZ_OVERLOAD);

		return overloadVarbit != 0;
	}

	private void checkOverload()
	{
		if (isOverloaded())
		{
			return;
		}

		if (!config.autoOverload())
		{
			return;
		}

		drinkOverload();
	}

	private void checkAbsorption()
	{
		int absorptionPoints = client.getVar(Varbits.NMZ_ABSORPTION);

		if (absorptionPoints >= config.absorptionThreshold())
		{
			return;
		}

		drinkAbsorption();
	}

	private void guzzleRockCake()
	{
		this.executor.submit(() -> {
			try
			{
				Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

				if (inventory == null)
				{
					return;
				}

				for (WidgetItem item : inventory.getWidgetItems())
				{
					if (item.getId() == ItemID.DWARVEN_ROCK_CAKE_7510)
					{
						entry = new MenuEntry("Guzzle", "<col=ff9040>" + itemManager.getItemDefinition(item.getId()).getName(), item.getId(), MenuOpcode.ITEM_THIRD_OPTION.getId(), item.getIndex(), 9764864, false);
						click();
						Thread.sleep(100);
						break;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
	}

	private void drinkAbsorption()
	{
		executor.submit(() -> {
			try
			{
				Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

				if (inventory == null)
				{
					return;
				}

				for (WidgetItem item : inventory.getWidgetItems())
				{
					if (isAbsorptionPotion(item))
					{
						entry = getConsumableEntry(itemManager.getItemDefinition(item.getId()).getName(), item.getId(), item.getIndex());
						click();
						Thread.sleep(100);
						break;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
	}

	private void drinkOverload()
	{
		executor.submit(() -> {
			try
			{
				Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

				if (inventory == null)
				{
					return;
				}

				for (WidgetItem item : inventory.getWidgetItems())
				{
					if (isOverloadPotion(item))
					{
						entry = getConsumableEntry(itemManager.getItemDefinition(item.getId()).getName(), item.getId(), item.getIndex());
						click();
						Thread.sleep(100);
						break;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
	}

	private boolean isAbsorptionPotion(WidgetItem item)
	{
		if (item.getId() == ItemID.ABSORPTION_1)
		{
			return true;
		}
		if (item.getId() == ItemID.ABSORPTION_2)
		{
			return true;
		}
		if (item.getId() == ItemID.ABSORPTION_3)
		{
			return true;
		}
		if (item.getId() == ItemID.ABSORPTION_4)
		{
			return true;
		}

		return false;
	}

	private boolean isOverloadPotion(WidgetItem item)
	{
		if (item.getId() == ItemID.OVERLOAD_1)
		{
			return true;
		}
		if (item.getId() == ItemID.OVERLOAD_2)
		{
			return true;
		}
		if (item.getId() == ItemID.OVERLOAD_3)
		{
			return true;
		}
		if (item.getId() == ItemID.OVERLOAD_4)
		{
			return true;
		}

		return false;
	}

	private MenuEntry getConsumableEntry(String itemName, int itemId, int itemIndex)
	{
		return new MenuEntry("Drink", "<col=ff9040>" + itemName, itemId, MenuOpcode.ITEM_FIRST_OPTION.getId(), itemIndex, 9764864, false);
	}

	boolean isInNightmareZone()
	{
		if (client.getLocalPlayer() == null)
		{
			return false;
		}

		// NMZ and the KBD lair uses the same region ID but NMZ uses planes 1-3 and KBD uses plane 0
		return client.getLocalPlayer().getWorldLocation().getPlane() > 0 && Arrays.equals(client.getMapRegions(), NMZ_MAP_REGION);
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
