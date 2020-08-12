package net.runelite.client.plugins.nmzhelper.Tasks;

import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.nmzhelper.Task;

public class BuyAbsorptionsTask extends Task
{
	@Override
	public boolean validate()
	{
		return false;
	}
	
	@Override
	public void onGameTick(GameTick event)
	{
		/*
		Option:	Buy-50
		Target:	<col=ff9040>Absorption (1)
		Identifier:	4
		Opcode:	57
		Param0:	9
		Param1:	13500422
		*/

		//client.getWidget(206 6)[9]
		Widget rewardsShopWidget = client.getWidget(206, 6);

		if (rewardsShopWidget == null || rewardsShopWidget.isHidden())
		{
			return;
		}

		Widget absorptionWidget;
		try
		{
			absorptionWidget = rewardsShopWidget.getChild(9);
		}
		catch (IndexOutOfBoundsException e)
		{
			//absorption potion doesnt exist? lets dip...
			return;
		}

		if (absorptionWidget == null || absorptionWidget.isHidden() || !absorptionWidget.getName().equals("<col=ff9040>Absorption (1)"))
		{
			return;
		}

		entry = new MenuEntry("Buy-50", "<col=ff9040>Absorption (1)", 4, MenuOpcode.CC_OP.getId(), 9, absorptionWidget.getId(), false);
		click();
	}
}
