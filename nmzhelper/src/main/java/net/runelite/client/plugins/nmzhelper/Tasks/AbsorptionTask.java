package net.runelite.client.plugins.nmzhelper.Tasks;

import net.runelite.api.ItemID;
import net.runelite.api.QueryResults;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.Task;

public class AbsorptionTask extends Task
{
	public AbsorptionTask(int priority)
	{
		super(priority);
	}

	@Override
	public boolean validate()
	{
		//fail if:

		//not in the nightmare zone
		if (!MiscUtils.isInNightmareZone(client))
			return false;

		//already met the absorption point threshold
		return client.getVar(Varbits.NMZ_ABSORPTION) < config.absorptionThreshold();
	}

	@Override
	public String getTaskDescription()
	{
		return "Drinking Absorptions";
	}

	@Override
	public void onGameTick(GameTick event)
	{
		QueryResults<WidgetItem> items = new InventoryWidgetItemQuery()
			.idEquals(ItemID.ABSORPTION_1, ItemID.ABSORPTION_2, ItemID.ABSORPTION_3, ItemID.ABSORPTION_4)
			.result(client);

		if (items == null || items.isEmpty())
		{
			return;
		}

		entry = MiscUtils.getConsumableEntry("", items.first().getId(), items.first().getIndex());
		click();
	}
}
