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

public class SearchRewardsChestTask extends Task
{
	@Override
	public boolean validate()
	{
		//in the nightmare zone
		if (MiscUtils.isInNightmareZone(client))
			return false;

		//if we have enough absorption doses in storage already
		if (client.getVarbitValue(3954) >= config.absorptionDoses() &&
			client.getVarbitValue(3953) >= config.overloadDoses())
			return false;

		//has absorptions && has overloads
		if (getAbsorptionDoseCount() >= config.absorptionDoses() &&
			getOverloadDoseCount() >= config.overloadDoses())
			return false;

		//get the game object
		QueryResults<GameObject> results = new GameObjectQuery()
			.idEquals(ObjectID.REWARDS_CHEST)
			.result(client);

		if (results == null || results.isEmpty())
		{
			return false;
		}

		GameObject obj = results.first();

		if (obj == null)
		{
			return false;
		}

		Widget rewardsShopWidget = client.getWidget(206, 0);

		if (rewardsShopWidget != null && !rewardsShopWidget.isHidden())
		{
			return false;
		}

		return true;
	}

	@Override
	public String getTaskDescription()
	{
		return "Search Rewards Chest";
	}
	
	@Override
	public void onGameTick(GameTick event)
	{
		QueryResults<GameObject> results = new GameObjectQuery()
			.idEquals(ObjectID.REWARDS_CHEST)
			.result(client);

		if (results == null || results.isEmpty())
		{
			return;
		}

		GameObject obj = results.first();

		if (obj == null)
		{
			return;
		}

		entry = new MenuEntry("Search", "<col=ffff>Rewards chest", ObjectID.REWARDS_CHEST, MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId(), obj.getSceneMinLocation().getX(), obj.getSceneMinLocation().getY(), false);
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
