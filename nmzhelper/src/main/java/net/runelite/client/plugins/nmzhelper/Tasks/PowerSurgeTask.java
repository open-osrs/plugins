package net.runelite.client.plugins.nmzhelper.Tasks;

import net.runelite.api.GameObject;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.ObjectID;
import net.runelite.api.QueryResults;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.Task;

public class PowerSurgeTask extends Task
{
	@Override
	public boolean validate()
	{
		if (!config.powerSurge())
			return false;

		//in the nightmare zone
		if (!MiscUtils.isInNightmareZone(client))
			return false;

		QueryResults<GameObject> results = new GameObjectQuery()
			.idEquals(ObjectID.POWER_SURGE)
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

		return true;
	}

	@Override
	public String getTaskDescription()
	{
		return "Power Surge";
	}

	@Override
	public void onGameTick(GameTick event)
	{
		QueryResults<GameObject> results = new GameObjectQuery()
			.idEquals(ObjectID.POWER_SURGE)
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

		entry = new MenuEntry("Activate", "<col=ffff>Power surge", ObjectID.POWER_SURGE, MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId(), obj.getSceneMinLocation().getX(), obj.getSceneMinLocation().getY(), false);
		click();
	}
}
