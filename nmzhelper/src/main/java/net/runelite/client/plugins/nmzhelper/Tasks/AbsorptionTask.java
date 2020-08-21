package net.runelite.client.plugins.nmzhelper.Tasks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.ItemID;
import net.runelite.api.QueryResults;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.Task;

public class AbsorptionTask extends Task
{
	@Override
	public boolean validate()
	{
		//fail if:

		//not in the nightmare zone
		if (!MiscUtils.isInNightmareZone(client))
			return false;

		//doesnt have absorptions
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

		if (inventoryWidget == null)
		{
			return false;
		}

		if (inventoryWidget.getWidgetItems()
			.stream()
			.filter(item -> Arrays.asList(ItemID.ABSORPTION_1, ItemID.ABSORPTION_2,
				ItemID.ABSORPTION_3, ItemID.ABSORPTION_4).contains(item.getId()))
			.collect(Collectors.toList())
			.isEmpty())
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
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

		if (inventoryWidget == null)
		{
			return;
		}

		List<WidgetItem> items = inventoryWidget.getWidgetItems()
			.stream()
			.filter(item -> Arrays.asList(ItemID.ABSORPTION_1, ItemID.ABSORPTION_2,
				ItemID.ABSORPTION_3, ItemID.ABSORPTION_4).contains(item.getId()))
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
