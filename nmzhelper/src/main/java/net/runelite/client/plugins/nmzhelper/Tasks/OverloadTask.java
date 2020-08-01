package net.runelite.client.plugins.nmzhelper.Tasks;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.QueryResults;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.Task;
import net.runelite.client.plugins.nmzhelper.NMZHelperConfig;

public class OverloadTask extends Task
{
	public OverloadTask(Client client, NMZHelperConfig config)
	{
		super(client, config);
	}

	@Override
	public int priority()
	{
		return 2;
	}

	@Override
	public boolean validate()
	{
		//fail if:

		//not in the nightmare zone
		if (!MiscUtils.isInNightmareZone(client))
			return false;

		//already overloaded
		if (client.getVar(Varbits.NMZ_OVERLOAD) != 0)
			return false;

		//don't have overloads
		if (new InventoryWidgetItemQuery()
			.idEquals(ItemID.OVERLOAD_1, ItemID.OVERLOAD_2, ItemID.OVERLOAD_3, ItemID.OVERLOAD_4)
			.result(client)
			.isEmpty())
			return false;

		//less than 50 hp
		if (client.getBoostedSkillLevel(Skill.HITPOINTS) <= 50)
			return false;

		return true;
	}

	@Override
	public String getTaskDescription()
	{
		return "Drinking Overload";
	}

	@Override
	public void onGameTick(GameTick event)
	{
		QueryResults<WidgetItem> items = new InventoryWidgetItemQuery()
			.idEquals(ItemID.OVERLOAD_1, ItemID.OVERLOAD_2, ItemID.OVERLOAD_3, ItemID.OVERLOAD_4)
			.result(client);

		if (items == null || items.isEmpty())
		{
			return;
		}

		WidgetItem item = items.first();

		if (item == null)
			return;

		entry = MiscUtils.getConsumableEntry("", item.getId(), item.getIndex());
		click();
	}
}
