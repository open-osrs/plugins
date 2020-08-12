package net.runelite.client.plugins.nmzhelper.Tasks;

import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.Task;

public class SpecialAttackTask extends Task
{
	@Override
	public boolean validate()
	{
		//option is disabled in config
		if (config.useSpecialAttack() == false)
		{
			return false;
		}

		//not in the nightmare zone
		if (!MiscUtils.isInNightmareZone(client))
		{
			return false;
		}

		//spec already enabled
		if (client.getVar(VarPlayer.SPECIAL_ATTACK_ENABLED) == 1)
		{
			return false;
		}

		//value returns 1000 for 100% spec, 500 for 50%, etc
		if (client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) < config.specialAttackValue() * 10)
		{
			return false;
		}

		Widget specialOrb = client.getWidget(160, 30);

		if (specialOrb == null || specialOrb.isHidden())
		{
			return false;
		}

		return true;
	}

	@Override
	public String getTaskDescription()
	{
		return "Use Special Attack";
	}

	@Override
	public void onGameTick(GameTick event)
	{
		entry = new MenuEntry("Use <col=00ff00>Special Attack</col>", "", 1, MenuOpcode.CC_OP.getId(), -1, 38862884, false);
		click();
	}
}
