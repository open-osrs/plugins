package net.runelite.client.plugins.bronzeman;

import lombok.Getter;
import lombok.Setter;

public class ItemUnlock
{

	@Getter
	private final int itemId;

	@Getter
	private long initTime;

	@Getter
	@Setter
	private int locationY;

	public ItemUnlock(int itemId)
	{
		this.itemId = itemId;
		this.locationY = -20;
		this.initTime = -1;
	}

	/**
	 * Starts the displaying of the item unlock
	 **/
	public void display()
	{
		this.initTime = System.currentTimeMillis();
	}

	/**
	 * Returns whether or not an items has been displayed as unlocked yet
	 **/
	public boolean finishedDisplaying(int queue)
	{
		if (queue > 2)
		{
			return System.currentTimeMillis() > initTime + (750);
		}
		else
		{
			return System.currentTimeMillis() > initTime + (4000);
		}
	}
}
