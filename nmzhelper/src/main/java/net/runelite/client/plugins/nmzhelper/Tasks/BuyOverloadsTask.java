package net.runelite.client.plugins.nmzhelper.Tasks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.ItemID;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.Task;

public class BuyOverloadsTask extends Task
{
	@Override
	public boolean validate()
	{
		//in the nightmare zone
		if (MiscUtils.isInNightmareZone(client))
			return false;

		//if we have enough overload doses in storage already
		if (client.getVarbitValue(3953) >= config.overloadDoses())
			return false;

		//has absorptions && has overloads
		if (getAbsorptionDoseCount() >= config.absorptionDoses() &&
			getOverloadDoseCount() >= config.overloadDoses())
			return false;

		Widget rewardsShopWidget = client.getWidget(206, 0);

		if (rewardsShopWidget == null || rewardsShopWidget.isHidden())
		{
			return false;
		}

		Widget benefitsPanel = client.getWidget(206, 6);

		if (benefitsPanel == null || benefitsPanel.isHidden())
		{
			return false;
		}

		Widget overloadWidget = benefitsPanel.getChild(6);

		if (overloadWidget == null || overloadWidget.isHidden() || !overloadWidget.getName().equals("<col=ff9040>Overload (1)"))
		{
			return false;
		}

		return true;
	}

	@Override
	public String getTaskDescription()
	{
		return "Buy Overloads";
	}

	@Override
	public void onGameTick(GameTick event)
	{
		//if amount of points is less than doses to buy * 1000
		if (client.getVar(VarPlayer.NMZ_REWARD_POINTS) / 1500 < (config.overloadDoses() - client.getVarbitValue(3953)))
		{
			plugin.stopPlugin("Not enough points to buy absorption potions!");
			return;
		}

		Widget benefitsPanel = client.getWidget(206, 6);

		if (benefitsPanel == null || benefitsPanel.isHidden())
		{
			return;
		}

		Widget overloadWidget = benefitsPanel.getChild(6);

		if (overloadWidget == null || overloadWidget.isHidden() || !overloadWidget.getName().equals("<col=ff9040>Overload (1)"))
		{
			return;
		}

		entry = new MenuEntry("Buy-50", "<col=ff9040>Overload (1)", 4, MenuOpcode.CC_OP.getId(), 6, overloadWidget.getId(), false);
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
