package net.runelite.client.plugins.nmzhelper.Tasks;

import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.NMZHelperConfig;
import net.runelite.client.plugins.nmzhelper.Task;

public class ContinueDialogTask extends Task
{
	public ContinueDialogTask(Client client, NMZHelperConfig config)
	{
		super(client, config);
	}

	@Override
	public int priority()
	{
		return 6;
	}

	@Override
	public boolean validate()
	{
		//in the nightmare zone
		if (MiscUtils.isInNightmareZone(client))
			return false;

		Widget widget = client.getWidget(WidgetInfo.DIALOG_NPC_CONTINUE);

		if (widget != null)
			return true;

		return false;
	}

	@Override
	public String getTaskDescription()
	{
		return "Continuing Dialog";
	}

	@Override
	public void onGameTick(GameTick event)
	{
		Widget widget = client.getWidget(WidgetInfo.DIALOG_NPC_CONTINUE);

		if (widget == null)
		{
			return;
		}

		entry = new MenuEntry("Continue", "", 0, MenuOpcode.WIDGET_TYPE_6.getId(), -1, widget.getId(), false);
		click();
	}
}
