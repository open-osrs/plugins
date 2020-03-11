/*
 * Copyright (c) 2017, Seth <Sethtroll3@gmail.com>
 * Copyright (c) 2018, Shaun Dreclin <shaundreclin@gmail.com>
 * Copyright (c) 2018, Robin Withes <https://github.com/robinwithes>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.slayer;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Title;
import net.runelite.client.config.Units;

@ConfigGroup("slayer")
public interface SlayerConfig extends Config
{
	@ConfigTitleSection(
		keyName = "infoBoxTitle",
		name = "InfoBox",
		description = "",
		position = 1
	)
	default Title infoBoxTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 2,
		keyName = "infobox",
		name = "Task InfoBox",
		description = "Display task information in an InfoBox",
		titleSection = "infoBoxTitle"
	)
	default boolean showInfobox()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "statTimeout",
		name = "InfoBox Expiry",
		description = "Set the time until the InfoBox expires",
		titleSection = "infoBoxTitle"
	)
	@Units(Units.MINUTES)
	default int statTimeout()
	{
		return 5;
	}

	@ConfigTitleSection(
		keyName = "highlightTitle",
		name = "Highlight",
		description = "",
		position = 4
	)
	default Title highlightTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 5,
		keyName = "highlightTargets",
		name = "Highlight Targets",
		description = "Highlight monsters you can kill for your current slayer assignment",
		titleSection = "highlightTitle"
	)
	default boolean highlightTargets()
	{
		return false;
	}

	@ConfigItem(
		position = 6,
		keyName = "highlightStyle",
		name = "Highlight Style",
		description = "Highlight setting",
		titleSection = "highlightTitle"
	)
	default RenderStyle renderStyle()
	{
		return RenderStyle.THIN_OUTLINE;
	}

	@ConfigTitleSection(
		keyName = "colorsTitle",
		name = "Colors",
		description = "",
		position = 7
	)
	default Title colorsTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 8,
		keyName = "targetColor",
		name = "Target Color",
		description = "Color of the highlighted targets",
		titleSection = "colorsTitle"
	)
	default Color getTargetColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		position = 9,
		keyName = "superiorColor",
		name = "Superior Color",
		description = "Color of the highlighted superior slayer creatures",
		titleSection = "colorsTitle"
	)
	default Color getSuperiorColor()
	{
		return Color.MAGENTA;
	}

	@ConfigTitleSection(
		keyName = "notificationTitle",
		name = "Notification",
		description = "",
		position = 10
	)
	default Title notificationTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 11,
		keyName = "superiornotification",
		name = "Superior foe notification",
		description = "Toggles notifications on superior foe encounters",
		titleSection = "notificationTitle"
	)
	default boolean showSuperiorNotification()
	{
		return true;
	}

	@ConfigItem(
		position = 12,
		keyName = "taskDoneNotification",
		name = "Task completed notification",
		description = "Gives you a notification when you complete a task.",
		titleSection = "notificationTitle"
	)
	default boolean taskDoneNotification()
	{
		return false;
	}

	@ConfigTitleSection(
		keyName = "overlayTitle",
		name = "Overlay",
		description = "",
		position = 13
	)
	default Title overlayTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 14,
		keyName = "itemoverlay",
		name = "Count on Items",
		description = "Display task count remaining on slayer items",
		titleSection = "overlayTitle"
	)
	default boolean showItemOverlay()
	{
		return true;
	}

	@ConfigItem(
		position = 15,
		keyName = "drawNames",
		name = "Draw names above NPC",
		description = "Configures whether or not NPC names should be drawn above the NPC",
		titleSection = "overlayTitle"
	)
	default boolean drawNames()
	{
		return false;
	}

	@ConfigItem(
		position = 16,
		keyName = "drawMinimapNames",
		name = "Draw names on minimap",
		description = "Configures whether or not NPC names should be drawn on the minimap",
		titleSection = "overlayTitle"
	)
	default boolean drawMinimapNames()
	{
		return false;
	}

	@ConfigItem(
		position = 17,
		keyName = "weaknessPrompt",
		name = "Show Monster Weakness",
		description = "Show an overlay on a monster when it is weak enough to finish off (Only Lizards, Gargoyles & Rockslugs)",
		titleSection = "overlayTitle"
	)
	default boolean weaknessPrompt()
	{
		return true;
	}

	@ConfigTitleSection(
		keyName = "commandsTitle",
		name = "Commands",
		description = "",
		position = 18
	)
	default Title commandsTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 19,
		keyName = "taskCommand",
		name = "Task Command",
		description = "Configures whether the slayer task command is enabled<br> !task",
		titleSection = "commandsTitle"
	)
	default boolean taskCommand()
	{
		return true;
	}

	@ConfigItem(
		position = 20,
		keyName = "pointsCommand",
		name = "Points Command",
		description = "Configures whether the slayer points command is enabled<br> !points",
		titleSection = "commandsTitle"
	)
	default boolean pointsCommand()
	{
		return true;
	}

	@ConfigTitleSection(
		keyName = "streakTitle",
		name = "Streak",
		description = "",
		position = 21
	)
	default Title streakTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 22,
		keyName = "maximumPointsNotification",
		name = "Slayer Streak Notification",
		description = "Gives you a warning when you should do a task with your highest level slayer master for the most points.",
		titleSection = "streakTitle"
	)
	default boolean maximumPointsNotification()
	{
		return false;
	}

	// Stored data
	@ConfigItem(
		keyName = "taskName",
		name = "",
		description = "",
		hidden = true
	)
	default String taskName()
	{
		return "";
	}

	@ConfigItem(
		keyName = "taskName",
		name = "",
		description = ""
	)
	void taskName(String key);

	@ConfigItem(
		keyName = "amount",
		name = "",
		description = "",
		hidden = true
	)
	default int amount()
	{
		return -1;
	}

	@ConfigItem(
		keyName = "amount",
		name = "",
		description = ""
	)
	void amount(int amt);

	@ConfigItem(
		keyName = "initialAmount",
		name = "",
		description = "",
		hidden = true
	)
	default int initialAmount()
	{
		return -1;
	}

	@ConfigItem(
		keyName = "initialAmount",
		name = "",
		description = ""
	)
	void initialAmount(int initialAmount);

	@ConfigItem(
		keyName = "taskLocation",
		name = "",
		description = "",
		hidden = true
	)
	default String taskLocation()
	{
		return "";
	}

	@ConfigItem(
		keyName = "taskLocation",
		name = "",
		description = ""
	)
	void taskLocation(String key);

	@ConfigItem(
		keyName = "lastCertainAmount",
		name = "",
		description = "",
		hidden = true
	)
	default int lastCertainAmount()
	{
		return -1;
	}

	@ConfigItem(
		keyName = "lastCertainAmount",
		name = "",
		description = ""
	)
	void lastCertainAmount(int lastCertainAmount);

	@ConfigItem(
		keyName = "streak",
		name = "",
		description = "",
		hidden = true
	)
	default int streak()
	{
		return -1;
	}

	@ConfigItem(
		keyName = "streak",
		name = "",
		description = ""
	)
	void streak(int streak);
}
