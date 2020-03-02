package net.runelite.client.plugins.itemuser;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class ItemUserHotkeyListener extends MouseAdapter implements KeyListener
{
	private final Client client;

	private final ItemUserPlugin plugin;

	private final ItemUserConfig config;

	private Instant lastPress;

	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final ReentrantLock lock = new ReentrantLock();

	@Inject
	private ItemUserHotkeyListener(final Client client, final ItemUserConfig config, final ItemUserPlugin plugin)
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
				this.dropItems();
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
			final List<GameObject> objectList = this.plugin.getObjectList();

			if (objectList == null || objectList.isEmpty())
				return;

			final GameObject firstObject = objectList.get(0);

			if (firstObject == null)
				return;

			final int itemId = this.config.itemId();
			final Widget inventory = this.client.getWidget(WidgetInfo.INVENTORY);
			Thread.sleep(this.config.clickDelay());

			for (final WidgetItem inventoryItem : inventory.getWidgetItems())
			{
				if (inventoryItem.getId() == itemId)
				{
					InputHandler.pressKey(this.client.getCanvas(), KeyEvent.VK_ESCAPE);
					final Rectangle p = inventoryItem.getCanvasBounds();
					InputHandler.leftClick(this.client, new Point((int) p.getCenterX(), (int) p.getCenterY()));
					Thread.sleep(this.config.clickDelay());

					//click the object
					final Rectangle r = firstObject.getConvexHull().getBounds();
					final Point objectPoint = new Point((int) r.getCenterX(), (int) r.getCenterY());
					
					InputHandler.leftClick(this.client, objectPoint);
					Thread.sleep(this.config.clickDelay());
				}
			}
		} catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
}
