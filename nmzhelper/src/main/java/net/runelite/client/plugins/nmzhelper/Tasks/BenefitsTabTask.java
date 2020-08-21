package net.runelite.client.plugins.nmzhelper.Tasks;

import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.nmzhelper.Task;

public class BenefitsTabTask extends Task
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
}
