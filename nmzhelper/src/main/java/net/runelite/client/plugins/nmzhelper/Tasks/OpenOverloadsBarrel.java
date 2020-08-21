package net.runelite.client.plugins.nmzhelper.Tasks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.ObjectID;
import net.runelite.api.QueryResults;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.Task;

public class OpenOverloadsBarrel extends Task
{
	@Override
	public boolean validate()
	{
		//fail if:

		//check if dream is not created
		if (!MiscUtils.isDreamCreated(client))
		{
			return false;
		}

		//not in the nightmare zone
		if (MiscUtils.isInNightmareZone(client))
			return false;

		//already have overloads
		if (getDoseCount() >= config.overloadDoses())
			return false;

		Widget chatTitle = client.getWidget(WidgetInfo.CHATBOX_TITLE);

		if (chatTitle != null && !chatTitle.isHidden())
		{
			return !chatTitle.getText().contains("How many doses of overload potion will you withdraw?");
		}

		return true;
	}

	@Override
	public String getTaskDescription()
	{
		return "Clicking Overload Barrel";
	}

	@Override
	public void onGameTick(GameTick event)
	{
		QueryResults<GameObject> gameObjects = new GameObjectQuery()
			.idEquals(ObjectID.OVERLOAD_POTION)
			.result(client);

		if (gameObjects == null || gameObjects.isEmpty())
		{
			return;
		}

		GameObject gameObject = gameObjects.first();

		if (gameObject == null)
			return;

		entry = new MenuEntry("Take", "<col=ffff>Overload potion", ObjectID.OVERLOAD_POTION, MenuOpcode.GAME_OBJECT_SECOND_OPTION.getId(), gameObject.getSceneMinLocation().getX(), gameObject.getSceneMinLocation().getY(), false);
		click();
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
