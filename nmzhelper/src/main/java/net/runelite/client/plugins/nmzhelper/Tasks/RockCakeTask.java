package net.runelite.client.plugins.nmzhelper.Tasks;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.QueryResults;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.Task;
import net.runelite.client.plugins.nmzhelper.NMZHelperConfig;
import net.runelite.client.plugins.nmzhelper.NMZHelperPlugin;

public class RockCakeTask extends Task
{
	public RockCakeTask(Client client, NMZHelperConfig config)
	{
		super(client, config);
	}

	@Override
	public int priority()
	{
		return 3;
	}

	@Override
	public boolean validate()
	{
		//fail if:

		//not in the nightmare zone
		if (!MiscUtils.isInNightmareZone(client))
			return false;

		//not overloaded
		if (client.getVar(Varbits.NMZ_OVERLOAD) == 0)
			return false;

		//don't have rock cake
		if (new InventoryWidgetItemQuery()
			.idEquals(ItemID.DWARVEN_ROCK_CAKE_7510)
			.result(client)
			.isEmpty())
			return false;

		//already 1 hp
		if (client.getBoostedSkillLevel(Skill.HITPOINTS) <= 1)
			return false;

		//out of absorption points
		if (client.getVar(Varbits.NMZ_ABSORPTION) <= 0)
			return false;

		return true;
	}

	@Override
	public String getTaskDescription()
	{
		return "Rock caking";
	}

	@Override
	public void onGameTick(GameTick event)
	{
		if (NMZHelperPlugin.rockCakeDelay > 0)
		{
			NMZHelperPlugin.rockCakeDelay--;
			return;
		}

		QueryResults<WidgetItem> items = new InventoryWidgetItemQuery()
			.idEquals(ItemID.DWARVEN_ROCK_CAKE_7510)
			.result(client);

		if (items == null || items.isEmpty())
		{
			return;
		}

		entry = new MenuEntry("Guzzle", "<col=ff9040>Dwarven rock cake", items.first().getId(), MenuOpcode.ITEM_THIRD_OPTION.getId(), items.first().getIndex(), 9764864, false);
		click();
	}
}
