package net.runelite.client.plugins.nmzhelper;

import java.util.Arrays;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.widgets.WidgetInfo;

public class MiscUtils
{
	private static final int[] NMZ_MAP_REGION = {9033};

	public static boolean isInNightmareZone(Client client)
	{
		if (client.getLocalPlayer() == null)
		{
			return false;
		}

		// NMZ and the KBD lair uses the same region ID but NMZ uses planes 1-3 and KBD uses plane 0
		return client.getLocalPlayer().getWorldLocation().getPlane() > 0 && Arrays.equals(client.getMapRegions(), NMZ_MAP_REGION);
	}

	public static MenuEntry getConsumableEntry(String itemName, int itemId, int itemIndex)
	{
		return new MenuEntry("Drink", "<col=ff9040>" + itemName, itemId, MenuOpcode.ITEM_FIRST_OPTION.getId(), itemIndex, WidgetInfo.INVENTORY.getId(), false);
	}

	public static boolean isDreamCreated(Client client)
	{
		return client.getVarbitValue(3946) == 123;
	}
}
