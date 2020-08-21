package net.runelite.client.plugins.nmzhelper.Tasks;

import java.awt.event.KeyEvent;
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

public class WithdrawOverloadTask extends Task
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
		if (getDoseCount() >= config.overloadDoses())
			return false;

		Widget chatTitle = client.getWidget(WidgetInfo.CHATBOX_TITLE);

		if (chatTitle == null || chatTitle.isHidden())
		{
			return false;
		}

		return chatTitle.getText().contains("How many doses of overload potion will you withdraw?");
	}

	@Override
	public String getTaskDescription()
	{
		return "Withdrawing Overloads";
	}

	@Override
	public void onGameTick(GameTick event)
	{
		client.setVar(VarClientInt.INPUT_TYPE, 7);
		client.setVar(VarClientStr.INPUT_TEXT, String.valueOf(config.overloadDoses() - getDoseCount()));
		client.runScript(681);
		client.runScript(ScriptID.MESSAGE_LAYER_CLOSE);
	}

	public void pressKey(int key)
	{
		keyEvent(401, key);
		keyEvent(402, key);
	}

	private void keyEvent(int id, int key)
	{
		KeyEvent e = new KeyEvent(
			client.getCanvas(), id, System.currentTimeMillis(),
			0, key, KeyEvent.CHAR_UNDEFINED
		);
		client.getCanvas().dispatchEvent(e);
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
			.filter(item -> Arrays.asList(ItemID.OVERLOAD_1, ItemID.OVERLOAD_2, ItemID.OVERLOAD_3, ItemID.OVERLOAD_4)
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
