package net.runelite.client.plugins.nmzhelper.Tasks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.ItemID;
import net.runelite.api.QueryResults;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.Task;

public class OverloadTask extends Task
{
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
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

		if (inventoryWidget == null)
		{
			return false;
		}

		if (inventoryWidget.getWidgetItems()
		.stream()
		.filter(item -> Arrays.asList(ItemID.OVERLOAD_1, ItemID.OVERLOAD_2,
			ItemID.OVERLOAD_3, ItemID.OVERLOAD_4).contains(item.getId()))
		.collect(Collectors.toList())
			.isEmpty())
			return false;

		//less than 50 hp
		return client.getBoostedSkillLevel(Skill.HITPOINTS) > 50;
	}

	@Override
	public String getTaskDescription()
	{
		return "Drinking Overload";
	}

	@Override
	public void onGameTick(GameTick event)
	{
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

		if (inventoryWidget == null)
		{
			return;
		}

		List<WidgetItem> items = inventoryWidget.getWidgetItems()
			.stream()
			.filter(item -> Arrays.asList(ItemID.OVERLOAD_1, ItemID.OVERLOAD_2, ItemID.OVERLOAD_3, ItemID.OVERLOAD_4).contains(item.getId()))
			.collect(Collectors.toList());

		if (items == null || items.isEmpty())
		{
			return;
		}

		WidgetItem item = items.get(0);

		if (item == null)
			return;

		entry = MiscUtils.getConsumableEntry("", item.getId(), item.getIndex());
		click();
	}
}
