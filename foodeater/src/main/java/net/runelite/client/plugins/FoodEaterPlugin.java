package net.runelite.client.plugins.foodeater;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
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
import java.awt.event.KeyEvent;
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

	@Provides
	FoodEaterConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(FoodEaterConfig.class);
	}

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
	public void onGameTick(final GameTick event)
	{
		int health = this.client.getBoostedSkillLevel(Skill.HITPOINTS);

		if (health > this.config.minimumHealth())
			return;

		InputHandler.sendKey(this.client.getCanvas(), KeyEvent.VK_ESCAPE);

		try
		{
			Thread.sleep(50);
		} catch (final Throwable e)
		{
			System.out.println(e.getMessage());
		}

		final Widget inventory = this.client.getWidget(WidgetInfo.INVENTORY);

		if (inventory == null)
			return;

		for (final WidgetItem item : inventory.getWidgetItems())
		{
			final String name = this.itemManager.getItemDefinition(item.getId()).getName();
			if (name.equalsIgnoreCase(this.config.foodToEat()))
			{
				executor.submit(() -> {
					try
					{
						final Point p = InputHandler.getClickPoint(item.getCanvasBounds());
						InputHandler.leftClick(this.client, p);
						Thread.sleep(50);
					} catch (final Throwable e)
					{
						System.out.println(e.getMessage());
					}
				});
				return;
			}
		}

		this.notifier.notify("No more food to eat!", TrayIcon.MessageType.WARNING);
	}
}
