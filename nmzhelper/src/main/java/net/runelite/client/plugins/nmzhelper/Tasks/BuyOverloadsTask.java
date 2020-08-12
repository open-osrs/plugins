package net.runelite.client.plugins.nmzhelper.Tasks;

import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.nmzhelper.Task;

public class BuyOverloadsTask extends Task
{
	@Override
	public boolean validate()
	{
		return false;
	}

	@Override
	public void onGameTick(GameTick event)
	{
		Widget rewardsShopWidget = client.getWidget(206, 6);

		if (rewardsShopWidget == null || rewardsShopWidget.isHidden())
		{
			return;
		}

		Widget overloadWidget;
		try
		{
			overloadWidget = rewardsShopWidget.getChild(9);
		}
		catch (IndexOutOfBoundsException e)
		{
			//absorption potion doesnt exist? lets dip...
			return;
		}

		if (overloadWidget == null || overloadWidget.isHidden() || !overloadWidget.getName().equals("<col=ff9040>Overload (1)"))
		{
			return;
		}

		entry = new MenuEntry("Buy-50", "<col=ff9040>Overload (1)", 4, MenuOpcode.CC_OP.getId(), 6, overloadWidget.getId(), false);
		click();
	}
}
