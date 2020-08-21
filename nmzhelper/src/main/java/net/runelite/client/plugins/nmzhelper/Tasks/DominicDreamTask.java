package net.runelite.client.plugins.nmzhelper.Tasks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.ItemID;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.QueryResults;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.Task;

public class DominicDreamTask extends Task
{
	@Override
	public boolean validate()
	{
		//fail if:

		//check if dream is created
		if (MiscUtils.isDreamCreated(client))
		{
			return false;
		}

		//in the nightmare zone
		if (MiscUtils.isInNightmareZone(client))
			return false;

		//has absorptions
		if (getAbsorptionDoseCount() >= config.absorptionDoses())
			return false;

		//has overloads
		if (getOverloadDoseCount() >= config.overloadDoses())
			return false;

		//DIALOG_OPTION_OPTION1[0] == Which dream would you like to experience?
		Widget dialogOption1Widget = client.getWidget(WidgetInfo.DIALOG_OPTION_OPTION1);

		if (dialogOption1Widget != null && !dialogOption1Widget.isHidden())
		{
			return false;
		}

		Widget dialogNpcContinueWidget = client.getWidget(WidgetInfo.DIALOG_NPC_CONTINUE);

		return dialogNpcContinueWidget == null || dialogNpcContinueWidget.isHidden();
	}

	@Override
	public String getTaskDescription()
	{
		return "Clicking on Dominic";
	}

	@Override
	public void onGameTick(GameTick event)
	{
		QueryResults<NPC> result = new NPCQuery()
			.idEquals(NpcID.DOMINIC_ONION)
			.result(client);

		if (result == null || result.isEmpty())
		{
			return;
		}

		NPC dominicOnion = result.first();

		if (dominicOnion == null)
			return;

		//String option, String target, int identifier, int opcode, int param0, int param1, boolean forceLeftClick
		entry = new MenuEntry("Dream", "<col=ffff00>Dominic Onion", dominicOnion.getIndex(), MenuOpcode.NPC_THIRD_OPTION.getId(), 0, 0, false);
		click();
	}

	public int getAbsorptionDoseCount()
	{
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

		if (inventoryWidget == null)
		{
			return 0;
		}

		List<WidgetItem> result = inventoryWidget.getWidgetItems()
			.stream()
			.filter(item -> Arrays.asList(ItemID.ABSORPTION_1, ItemID.ABSORPTION_2,
				ItemID.ABSORPTION_3, ItemID.ABSORPTION_4).contains(item.getId()))
			.collect(Collectors.toList());

		if (result.isEmpty())
			return 0;

		int doseCount = (int) result.stream().filter(item -> item.getId() == ItemID.ABSORPTION_1).count();
		doseCount += 2 * (int) result.stream().filter(item -> item.getId() == ItemID.ABSORPTION_2).count();
		doseCount += 3 * (int) result.stream().filter(item -> item.getId() == ItemID.ABSORPTION_3).count();
		doseCount += 4 * (int) result.stream().filter(item -> item.getId() == ItemID.ABSORPTION_4).count();

		return doseCount;
	}

	public int getOverloadDoseCount()
	{
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

		if (inventoryWidget == null)
		{
			return 0;
		}

		List<WidgetItem> result = inventoryWidget.getWidgetItems()
			.stream()
			.filter(item -> Arrays.asList(ItemID.OVERLOAD_1, ItemID.OVERLOAD_2,
				ItemID.OVERLOAD_3, ItemID.OVERLOAD_4)
				.contains(item.getId()))
			.collect(Collectors.toList());

		if (result.isEmpty())
			return 0;

		int doseCount = (int) result.stream().filter(item -> item.getId() == ItemID.OVERLOAD_1).count();
		doseCount += 2 * (int) result.stream().filter(item -> item.getId() == ItemID.OVERLOAD_2).count();
		doseCount += 3 * (int) result.stream().filter(item -> item.getId() == ItemID.OVERLOAD_3).count();
		doseCount += 4 * (int) result.stream().filter(item -> item.getId() == ItemID.OVERLOAD_4).count();

		return doseCount;
	}
}
