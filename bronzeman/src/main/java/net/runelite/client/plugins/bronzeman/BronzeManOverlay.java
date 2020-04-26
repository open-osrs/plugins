package net.runelite.client.plugins.bronzeman;

import java.awt.Dimension;
import java.awt.Graphics2D;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class BronzeManOverlay extends Overlay
{

	private final Client client;
	private final BronzeManPlugin plugin;
	private ItemUnlock currentUnlock;
	private final List<ItemUnlock> itemUnlockList;

	@Inject
	private ItemManager itemManager;

	@Inject
	public BronzeManOverlay(Client client, BronzeManPlugin plugin)
	{
		super(plugin);
		this.client = client;
		this.plugin = plugin;
		this.itemUnlockList = new CopyOnWriteArrayList<>();
		setPosition(OverlayPosition.TOP_CENTER);
	}

	public void addItemUnlock(int itemId)
	{
		itemUnlockList.add(new ItemUnlock(itemId));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return null;
		}
		if (itemUnlockList.isEmpty())
		{
			return null;
		}
		if (itemManager == null)
		{
			System.out.println("Item-manager is null");
			return null;
		}
		if (currentUnlock == null)
		{
			currentUnlock = itemUnlockList.get(0);
			currentUnlock.display();
			return null;
		}

		int drawY = currentUnlock.getLocationY();
		graphics.drawImage(plugin.getUnlockImage(), -62, drawY, null);
		graphics.drawImage(getImage(currentUnlock.getItemId()), -56, drawY + 7, null);
		if (drawY < 10)
		{
			currentUnlock.setLocationY(drawY + 1);
		}
		if (currentUnlock.finishedDisplaying(itemUnlockList.size()))
		{
			itemUnlockList.remove(currentUnlock);
			currentUnlock = null;
		}
		return null;
	}

	private BufferedImage getImage(int itemID)
	{
		return itemManager.getImage(itemID, 1, false);
	}

}