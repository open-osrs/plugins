package net.runelite.client.plugins.praypotdrinker;

import java.util.Arrays;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.WidgetItem;

public enum PrayerRestoreType
{
	PRAYER_POTION(ItemID.PRAYER_POTION1, ItemID.PRAYER_POTION2, ItemID.PRAYER_POTION3, ItemID.PRAYER_POTION4),
	SUPER_RESTORE(ItemID.SUPER_RESTORE1, ItemID.SUPER_RESTORE2, ItemID.SUPER_RESTORE3, ItemID.SUPER_RESTORE4,
		ItemID.BLIGHTED_SUPER_RESTORE1, ItemID.BLIGHTED_SUPER_RESTORE2, ItemID.BLIGHTED_SUPER_RESTORE3,
		ItemID.BLIGHTED_SUPER_RESTORE4),
	SANFEW_SERUM(ItemID.SANFEW_SERUM1, ItemID.SANFEW_SERUM2, ItemID.SANFEW_SERUM3, ItemID.SANFEW_SERUM4);

	public int[] ItemIDs;

	PrayerRestoreType(int... ids)
	{
		this.ItemIDs = ids;
	}

	public boolean containsId(int id)
	{
		return Arrays.stream(this.ItemIDs).anyMatch(x -> x == id);
	}

	public WidgetItem getItemFromInventory(Client client)
	{
		return new InventoryWidgetItemQuery().idEquals(ItemIDs).result(client).first();
	}
}
