package net.runelite.client.plugins.spawntimer;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Title;

@ConfigGroup("spawntimer")
public interface SpawnTimerConfig extends Config
{
	@ConfigTitleSection(
		keyName = "npcsTitle",
		position = 1,
		name = "NPCs",
		description = ""
	)
	default Title npcsTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 2,
		keyName = "npcToHighlight",
		name = "NPCs to show timer for",
		description = "List of NPC names to show timer for",
		titleSection = "npcsTitle"
	)
	default String getNpcToHighlight()
	{
		return "";
	}

	@ConfigTitleSection(
		keyName = "colorsTitle",
		position = 3,
		name = "Colors",
		description = ""
	)
	default Title colorsTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 4,
		keyName = "npcColor",
		name = "Text Color",
		description = "Color of the NPC timer",
		titleSection = "colorsTitle"
	)
	default Color getHighlightColor()
	{
		return Color.RED;
	}
}