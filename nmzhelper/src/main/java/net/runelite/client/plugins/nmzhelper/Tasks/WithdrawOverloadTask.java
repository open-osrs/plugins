package net.runelite.client.plugins.nmzhelper.Tasks;

import java.awt.event.KeyEvent;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.QueryResults;
import net.runelite.api.Script;
import net.runelite.api.ScriptID;
import net.runelite.api.VarClientInt;
import net.runelite.api.VarClientStr;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.NMZHelperConfig;
import net.runelite.client.plugins.nmzhelper.Task;

public class WithdrawOverloadTask extends Task
{
	public WithdrawOverloadTask(Client client, NMZHelperConfig config)
	{
		super(client, config);
	}

	@Override
	public int priority()
	{
		return 5;
	}

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
		if (chatTitle == null)
		{
			return false;
		}

		if (!chatTitle.getText().contains("How many doses of overload potion will you withdraw?"))
		{
			return false;
		}

		return true;
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
		QueryResults<WidgetItem> result = new InventoryWidgetItemQuery()
			.idEquals(ItemID.OVERLOAD_1, ItemID.OVERLOAD_2, ItemID.OVERLOAD_3, ItemID.OVERLOAD_4)
			.result(client);

		if (result == null || result.isEmpty())
			return 0;

		int doseCount = (int) result.stream().filter(item -> item.getId() == ItemID.OVERLOAD_1).count();
		doseCount += 2 * (int) result.stream().filter(item -> item.getId() == ItemID.OVERLOAD_2).count();
		doseCount += 3 * (int) result.stream().filter(item -> item.getId() == ItemID.OVERLOAD_3).count();
		doseCount += 4 * (int) result.stream().filter(item -> item.getId() == ItemID.OVERLOAD_4).count();

		return doseCount;
	}
}
