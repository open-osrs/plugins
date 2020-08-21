package net.runelite.client.plugins.nmzhelper.Tasks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.ItemID;
import net.runelite.api.QueryResults;
import net.runelite.api.ScriptID;
import net.runelite.api.VarClientInt;
import net.runelite.api.VarClientStr;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.Task;

public class WithdrawAbsorptionTask extends Task
{
	@Override
	public boolean validate()
	{
		//fail if:

		//not in the nightmare zone
		if (MiscUtils.isInNightmareZone(client))
			return false;

		//check if dream is not created
		if (!MiscUtils.isDreamCreated(client))
		{
			return false;
		}

		//already have overloads
		if (getDoseCount() >= config.absorptionDoses())
			return false;

		Widget chatTitle = client.getWidget(WidgetInfo.CHATBOX_TITLE);

		if (chatTitle == null || chatTitle.isHidden())
		{
			return false;
		}

		return chatTitle.getText().contains("How many doses of absorption potion will you withdraw?");
	}

	@Override
	public String getTaskDescription()
	{
		return "Withdrawing Absorptions";
	}

	@Override
	public void onGameTick(GameTick event)
	{
		client.setVar(VarClientInt.INPUT_TYPE, 7);
		client.setVar(VarClientStr.INPUT_TEXT, String.valueOf(config.absorptionDoses() - getDoseCount()));
		client.runScript(681);
		client.runScript(ScriptID.MESSAGE_LAYER_CLOSE);
	}

	public int getDoseCount()
	{
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

		if (inventoryWidget == null)
		{
			return 0;
		}

		List<WidgetItem> result = inventoryWidget.getWidgetItems()
			.stream()
			.filter(item -> Arrays.asList(ItemID.ABSORPTION_1, ItemID.ABSORPTION_2,
				ItemID.ABSORPTION_3, ItemID.ABSORPTION_4)
				.contains(item.getId()))
			.collect(Collectors.toList());

		if (result == null || result.isEmpty())
			return 0;

		int doseCount = (int) result.stream().filter(item -> item.getId() == ItemID.ABSORPTION_1).count();
		doseCount += 2 * (int) result.stream().filter(item -> item.getId() == ItemID.ABSORPTION_2).count();
		doseCount += 3 * (int) result.stream().filter(item -> item.getId() == ItemID.ABSORPTION_3).count();
		doseCount += 4 * (int) result.stream().filter(item -> item.getId() == ItemID.ABSORPTION_4).count();

		return doseCount;
	}
}
