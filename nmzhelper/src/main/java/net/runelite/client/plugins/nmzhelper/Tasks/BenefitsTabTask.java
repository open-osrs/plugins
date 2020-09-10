package net.runelite.client.plugins.nmzhelper.Tasks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.ItemID;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.Task;

public class BenefitsTabTask extends Task
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

		Widget rewardsShopWidget = client.getWidget(206, 0);

		if (rewardsShopWidget == null || rewardsShopWidget.isHidden())
		{
			return false;
		}

		Widget rewardsTabList = client.getWidget(206, 2);

		if (rewardsTabList == null || rewardsTabList.isHidden())
		{
			return false;
		}

		try
		{
			Widget benefitTab = rewardsTabList.getChild(5);

			if (benefitTab == null || !benefitTab.getText().equals("Benefits"))
			{
				return false;
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			//child not found somehow...lets get out of here...
			return false;
		}

		try
		{
			Widget benefitsPanel = client.getWidget(206, 6);

			if (benefitsPanel != null && !benefitsPanel.isHidden())
			{
				return false;
			}

			Widget absorptionWidget = benefitsPanel.getChild(9);

			if (absorptionWidget != null && !absorptionWidget.isHidden())
			{
				return false;
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			//
		}

		return true;
	}

	@Override
	public String getTaskDescription()
	{
		return "Benefits Tab";
	}
	
	@Override
	public void onGameTick(GameTick event)
	{
		/*
		Option:	Benefits
		Target:
		Identifier:	1
		Opcode:	57
		Param0:	4 //widget.getType() ???
		Param1:	13500418
		 */
		Widget rewardsTabList = client.getWidget(206, 2);

		if (rewardsTabList == null || rewardsTabList.isHidden())
		{
			return;
		}

		Widget benefitTab;
		try
		{
			benefitTab = rewardsTabList.getChild(5);
		}
		catch (IndexOutOfBoundsException e)
		{
			//child not found somehow...lets get out of here...
			return;
		}

		if (benefitTab == null || !benefitTab.getText().equals("Benefits"))
		{
			return;
		}

		entry = new MenuEntry("Benefits", "", 1, MenuOpcode.CC_OP.getId(), 4, benefitTab.getId(), false);
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
