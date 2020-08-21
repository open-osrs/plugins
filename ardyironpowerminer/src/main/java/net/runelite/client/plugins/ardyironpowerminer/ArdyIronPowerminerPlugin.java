package net.runelite.client.plugins.ardyironpowerminer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.inject.Inject;
import com.google.inject.Provides;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.Random;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Ardy Iron Powerminer",
	description = "Automatically powermines iron @ ardy mine",
	tags = {"mining", "mine", "powermine", "iron", "ardy", "ardougne", "skill", "skilling"},
	enabledByDefault = false,
	type = PluginType.SKILLING
)
public class ArdyIronPowerminerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ArdyIronPowerminerConfig config;

	private MenuEntry entry;

	private Random r = new Random();

	private boolean pluginStarted = false;

	private int tickDelay = 0;
	private int frameDelay = 0;

	private int dropAt = r.nextInt((27 - 3) + 1) + 3;

	boolean isDropping = false;

	private WorldPoint basePoint = new WorldPoint(2692, 3329, 0);

	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 25, TimeUnit.SECONDS, queue,
		new ThreadPoolExecutor.DiscardPolicy());

	@Provides
	ArdyIronPowerminerConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(ArdyIronPowerminerConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
		executor.shutdownNow();
	}

	@Subscribe
	public void onConfigButtonClicked(ConfigButtonClicked event)
	{
		if (!event.getGroup().equals("ardyironpowerminer"))
		{
			return;
		}

		if (event.getKey().equals("startButton"))
		{
			pluginStarted = true;
		}
		else if (event.getKey().equals("stopButton"))
		{
			pluginStarted = false;
		}
	}

	public GameObject findNearestGameObjectWithin(WorldPoint worldPoint, int dist, int id)
	{
		if (client.getLocalPlayer() == null)
		{
			return null;
		}

		return new GameObjectQuery()
			.idEquals(id)
			.isWithinDistance(worldPoint, dist)
			.result(client)
			.nearestTo(client.getLocalPlayer());
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if (frameDelay > 0)
		{
			frameDelay--;
			return;
		}

		if (!isDropping)
		{
			return;
		}

		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

		if (inventoryWidget == null)
		{
			return;
		}

		List<WidgetItem> list = inventoryWidget.getWidgetItems().stream().filter(item -> item.getId() == ItemID.IRON_ORE).collect(Collectors.toList());

		if (list == null || list.isEmpty())
		{
			isDropping = false;
			return;
		}

		entry = new MenuEntry("Drop", "<col=ff9040>Iron ore", list.get(0).getId(), MenuOpcode.ITEM_FIFTH_OPTION.getId(), list.get(0).getIndex(), WidgetInfo.INVENTORY.getId(), false);
		click();
		frameDelay = 10;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (tickDelay > 0) {
			tickDelay--;
			return;
		}

		if (!pluginStarted)
			return;

		if (isDropping)
			return;

		if (isInventoryFull())
		{
			tickDelay = 1;
			isDropping = true;
			return;
		}

		if (isMining()) {
			tickDelay = 1;
			return;
		}

		GameObject rock = findNearestGameObjectWithin(basePoint, 2, 11364);

		if (rock == null)
		{
			rock = findNearestGameObjectWithin(basePoint, 2, 11365);
		}

		if (rock == null)
		{
			tickDelay = 9;
			return;
		}

		entry = new MenuEntry("Mine", "<col=ffff>Rocks", rock.getId(), MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId(), rock.getSceneMinLocation().getX(), rock.getSceneMinLocation().getY(), false);
		click();
		tickDelay = 1;
	}

	public boolean isInventoryFull()
	{
		Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

		if (inventory == null)
		{
			return false;
		}

		if (inventory.getWidgetItems().size() > dropAt)
		{
			dropAt = r.nextInt((27 - 3) + 1) + 3;
			return true;
		}

		return false;
	}

	public boolean isMining()
	{
		Player localPlayer = client.getLocalPlayer();

		if (localPlayer == null)
		{
			return false;
		}

		if (localPlayer.getAnimation() != -1)
		{
			return true;
		}

		if (localPlayer.getPoseAnimation() != 808)
		{
			return true;
		}

		return false;
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

	public WidgetItem getItemFromInventory(int itemId)
	{
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

		if (inventoryWidget == null)
		{
			return null;
		}

		for (WidgetItem item : inventoryWidget.getWidgetItems())
		{
			if (itemId == item.getId())
			{
				return item;
			}
		}

		return null;
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