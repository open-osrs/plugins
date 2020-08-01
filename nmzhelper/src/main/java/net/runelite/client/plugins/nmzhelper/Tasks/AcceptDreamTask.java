package net.runelite.client.plugins.nmzhelper.Tasks;

import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.nmzhelper.NMZHelperConfig;
import net.runelite.client.plugins.nmzhelper.Task;

public class AcceptDreamTask extends Task
{
	public AcceptDreamTask(Client client, NMZHelperConfig config)
	{
		super(client, config);
	}

	@Override
	public int priority()
	{
		return 1;
	}

	@Override
	public boolean validate()
	{
		//nmz dream accept button
		Widget acceptWidget = client.getWidget(129, 6);

		if (acceptWidget == null || acceptWidget.isHidden())
		{
			return false;
		}

		return true;
	}

	@Override
	public String getTaskDescription()
	{
		return "Accepting Dream";
	}
	
	@Override
	public void onGameTick(GameTick event)
	{
		entry = new MenuEntry("Continue", "", 0, MenuOpcode.WIDGET_TYPE_6.getId(), -1, 8454150, false);
		click();
	}
}
