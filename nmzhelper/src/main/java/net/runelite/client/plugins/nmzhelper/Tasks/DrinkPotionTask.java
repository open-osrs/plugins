package net.runelite.client.plugins.nmzhelper.Tasks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.QueryResults;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.Task;

public class DrinkPotionTask extends Task
{
	@Override
	public boolean validate()
	{
		//in nmz
		if (MiscUtils.isInNightmareZone(client))
			return false;

		//doesn't have absorptions
		if (getAbsorptionDoseCount() < config.absorptionDoses())
			return false;

		//doesn't have overloads
		if (getOverloadDoseCount() < config.overloadDoses())
			return false;

		//dream isn't created
		if (!MiscUtils.isDreamCreated(client))
		{
			return false;
		}

		Widget acceptWidget = client.getWidget(129, 6);

		return acceptWidget == null || acceptWidget.isHidden();
	}

	@Override
	public String getTaskDescription()
	{
		return "Drink Potion";
	}
	
	@Override
	public void onGameTick(GameTick event)
	{
		/*
		Option:	Drink
		Target:	<col=ffff><col=ff9040>Potion
		Identifier:	26291 //game object id
		Opcode:	MenuOpcode.GAME_OBJECT_FIRST_OPTION
		Param0:	45 //scene min x
		Param1:	53 //scene min y
		 */

		QueryResults<GameObject> results = new GameObjectQuery()
			.idEquals(26291)
			.result(client);

		if (results == null || results.isEmpty())
			return;

		GameObject object = results.first();

		if (object == null)
			return;

		entry = new MenuEntry("Drink", "<col=ffff><col=ff9040>Potion", 26291, MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId(), object.getSceneMinLocation().getX(), object.getSceneMinLocation().getY(), false);
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

		if (result == null || result.isEmpty())
			return 0;

		int doseCount = (int) result.stream().filter(item -> item.getId() == ItemID.OVERLOAD_1).count();
		doseCount += 2 * (int) result.stream().filter(item -> item.getId() == ItemID.OVERLOAD_2).count();
		doseCount += 3 * (int) result.stream().filter(item -> item.getId() == ItemID.OVERLOAD_3).count();
		doseCount += 4 * (int) result.stream().filter(item -> item.getId() == ItemID.OVERLOAD_4).count();

		return doseCount;
	}
}
