package net.runelite.client.plugins.npcflicker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("npcflicker")
public interface NpcFlickerConfig extends Config
{
	@Range(
		min = 1
	)
	@ConfigItem(
		keyName = "AttackRange",
		name = "NPC attack range",
		description = "The attack range of the NPC.",
		position = 1,
		titleSection = "rangeTitle"
	)
	default int getRange()
	{
		return 1;
	}

	@ConfigItem(
		keyName = "CustomAttSpeedEnabled",
		name = "Custom attack speed",
		description = "Use this if the timer is wrong.",
		position = 2,
		titleSection = "speedTitle"
	)
	default boolean isCustomAttSpeed()
	{
		return false;
	}

	@Range(
		min = 1
	)
	@ConfigItem(
		keyName = "CustomAttSpeed",
		name = "Custom NPC att speed",
		description = "The attack speed of the NPC (amount of ticks between their attacks).",
		position = 3,
		hidden = true,
		unhide = "CustomAttSpeedEnabled",
		titleSection = "speedTitle"
	)
	default int getCustomAttSpeed()
	{
		return 4;
	}

	@ConfigItem(
		keyName = "magicNpcs",
		name = "Magic NPCs",
		description = "",
		position = 4
	)
	default String magicNpcs() { return ""; }
}
