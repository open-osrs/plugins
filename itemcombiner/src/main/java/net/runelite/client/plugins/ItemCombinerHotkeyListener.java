package net.runelite.client.plugins.itemcombiner;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.input.KeyListener;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class ItemCombinerHotkeyListener extends MouseAdapter implements KeyListener
{
	private final Client client;

	private final ItemCombinerPlugin plugin;

	private final ItemCombinerConfig config;

	private Instant lastPress;

	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final ReentrantLock lock = new ReentrantLock();

	@Inject
	private ItemCombinerHotkeyListener(final Client client, final ItemCombinerConfig config, final ItemCombinerPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
	}

	@Override
	public void keyTyped(final KeyEvent e)
	{

	}

	@Override
	public void keyPressed(final KeyEvent e)
	{
		if (this.client.getGameState() != GameState.LOGGED_IN)
			return;

		if (this.executor == null || this.lock == null)
			return;

		if (this.lastPress != null && Duration.between(this.lastPress, Instant.now()).getNano() > 1000)
		{
			this.lastPress = null;
		}

		if (this.lastPress != null)
		{
			return;
		}

		if (e.getExtendedKeyCode() == this.config.useItemsKeybind().getKeyCode())
		{
			executor.submit(() -> {
				lock.lock();
				for (int i = this.config.iterations(); i > 0; i--)
				{
					this.dropItems();
				}
				lock.unlock();
			});
		}
	}

	@Override
	public void keyReleased(final KeyEvent e)
	{

	}

	private void dropItems()
	{
		try
		{
			final int itemId = this.config.itemId();
			final int itemId2 = this.config.itemId2();
			final Widget inventory = this.client.getWidget(WidgetInfo.INVENTORY);
			Thread.sleep(this.config.clickDelay());

			final WidgetItem firstItem = inventory.getWidgetItems().stream().filter(inventoryItem -> inventoryItem.getId() == itemId).findFirst().orElse(null);

			if (firstItem == null)
			{
				return;
			}

			final WidgetItem secondItem = inventory.getWidgetItems().stream().filter(inventoryItem -> inventoryItem.getId() == itemId2).findFirst().orElse(null);

			if (secondItem == null)
			{
				return;
			}

			InputHandler.pressKey(this.client.getCanvas(), KeyEvent.VK_ESCAPE);
			final Rectangle p = firstItem.getCanvasBounds();
			InputHandler.leftClick(this.client, new Point((int) p.getCenterX(), (int) p.getCenterY()));
			Thread.sleep(this.config.clickDelay());

			//click the object
			final Rectangle p2 = secondItem.getCanvasBounds();
			InputHandler.leftClick(this.client, new Point((int) p2.getCenterX(), (int) p2.getCenterY()));
			Thread.sleep(this.config.clickDelay());
			InputHandler.releaseKey(this.client.getCanvas(), KeyEvent.VK_ESCAPE);
			Thread.sleep(this.config.clickDelay());
		} catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
}
