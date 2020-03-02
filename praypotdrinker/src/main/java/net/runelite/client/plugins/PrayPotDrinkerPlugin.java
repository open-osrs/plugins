package net.runelite.client.plugins.praypotdrinker;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@PluginDescriptor(
		name = "Prayer Pot Drinker",
		description = "Automatically drink pray pots",
		tags = {"combat", "notifications", "prayer"},
		enabledByDefault = false,
		type = PluginType.EXTERNAL
)
public class PrayPotDrinkerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private Notifier notifier;

	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 25, TimeUnit.SECONDS, queue,
			new ThreadPoolExecutor.DiscardPolicy());

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		this.executor.submit(() ->
		{
			try
			{
				
				//7 + 25%
				int currentPrayerPoints = client.getBoostedSkillLevel(Skill.PRAYER);
				int maxPrayerPoints = client.getRealSkillLevel(Skill.PRAYER);
				int boostAmount = 7 + (int) Math.floor(maxPrayerPoints * .25);
				
				if (currentPrayerPoints + boostAmount > maxPrayerPoints)
				{
					return;
				}
				
				//flexo.keyPress(KeyEvent.VK_ESCAPE);
				//flexo.delay(50);

				Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

				if (inventory == null)
					return;

				for (WidgetItem item : inventory.getWidgetItems())
				{
					int itemid = item.getId();

					if (itemid == ItemID.PRAYER_POTION1 || itemid == ItemID.PRAYER_POTION2 || itemid == ItemID.PRAYER_POTION3 || itemid == ItemID.PRAYER_POTION4)
					{
						InputHandler.pressKey(client.getCanvas(), KeyEvent.VK_ESCAPE);
						Thread.sleep(50);
						Point p = InputHandler.getClickPoint(item.getCanvasBounds());
						InputHandler.leftClick(client, p);
						Thread.sleep(50);
						return;
					}
				}

				notifier.notify("No more prayer potions left!", TrayIcon.MessageType.WARNING);
			}
			catch (Throwable e)
			{
				System.out.println(e.getMessage());
			}
		});
	}
}
