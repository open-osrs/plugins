package net.runelite.client.plugins.itemdropper;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyListener;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ItemDropperHotkeyListener extends MouseAdapter implements KeyListener
{

	private Client client;

	private Instant lastPress;

	@Inject
	private ItemDropperPlugin plugin;

	@Inject
	private ItemDropperConfig config;

	@Inject
	private ItemManager itemManager;

	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 25, TimeUnit.SECONDS, queue,
			new ThreadPoolExecutor.DiscardPolicy());

	@Inject
	private ItemDropperHotkeyListener(final Client client, final ItemDropperConfig config, final ItemDropperPlugin plugin)
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

		if (e.getExtendedKeyCode() == this.config.dropItemsKeybind().getKeyCode())
		{
			executor.submit(() -> {
				this.dropItems();
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
			final String itemConfigString = this.config.itemsToDrop();
			final String[] items = itemConfigString.split(",\\s*");

			final Widget inventory = this.client.getWidget(WidgetInfo.INVENTORY);

			InputHandler.pressKey(this.client.getCanvas(), KeyEvent.VK_SHIFT);

			Thread.sleep(this.config.clickDelay());

			for (final WidgetItem inventoryItem : inventory.getWidgetItems())
			{
				final String itemName = this.itemManager.getItemDefinition(inventoryItem.getId()).getName();

				for (final String item : items)
				{
					if (itemName.equalsIgnoreCase(item))
					{
						final Point p = InputHandler.getClickPoint(inventoryItem.getCanvasBounds());
						InputHandler.leftClick(this.client, p);
						Thread.sleep(this.config.clickDelay());
						break;
					}
				}
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			InputHandler.releaseKey(this.client.getCanvas(), KeyEvent.VK_SHIFT);
		}
	}
}