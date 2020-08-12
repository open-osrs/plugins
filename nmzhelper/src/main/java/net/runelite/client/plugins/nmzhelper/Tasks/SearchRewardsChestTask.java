package net.runelite.client.plugins.nmzhelper.Tasks;

import net.runelite.api.GameObject;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.ObjectID;
import net.runelite.api.QueryResults;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.client.plugins.nmzhelper.Task;

public class SearchRewardsChestTask extends Task
{
	@Override
	public boolean validate()
	{
		return false;
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
}
