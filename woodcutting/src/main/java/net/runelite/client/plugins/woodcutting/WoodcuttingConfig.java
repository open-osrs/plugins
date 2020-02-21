/*
 * Copyright (c) 2017, Seth <Sethtroll3@gmail.com>
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
package net.runelite.client.plugins.woodcutting;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Title;
import net.runelite.client.config.Units;

@ConfigGroup("woodcutting")
public interface WoodcuttingConfig extends Config
{
	@ConfigTitleSection(
		keyName = "sessionTitle",
		name = "Session",
		description = "",
		position = 1
	)
	default Title sessionTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 2,
		keyName = "statTimeout",
		name = "Reset stats",
		description = "Configures the time until statistic is reset. Also configures when tree indicator is hidden",
		titleSection = "sessionTitle"
	)
	@Units(Units.MINUTES)
	default int statTimeout()
	{
		return 5;
	}

	@ConfigItem(
		position = 3,
		keyName = "showWoodcuttingStats",
		name = "Show session stats",
		description = "Configures whether to display woodcutting session stats",
		titleSection = "sessionTitle"
	)
	default boolean showWoodcuttingStats()
	{
		return true;
	}

	@ConfigTitleSection(
		keyName = "notificationTitle",
		name = "Notification",
		description = "",
		position = 4
	)
	default Title notificationTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 5,
		keyName = "showNestNotification",
		name = "Bird nest notification",
		description = "Configures whether to notify you of a bird nest spawn",
		titleSection = "notificationTitle"
	)
	default boolean showNestNotification()
	{
		return true;
	}

	@ConfigTitleSection(
		keyName = "overlayTitle",
		name = "Overlay",
		description = "",
		position = 6
	)
	default Title overlayTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 7,
		keyName = "showGPEarned",
		name = "Show GP earned",
		description = "Configures whether to show amount of gp earned by chopping trees",
		titleSection = "overlayTitle"
	)
	default boolean showGPEarned()
	{
		return false;
	}

	@ConfigItem(
		position = 8,
		keyName = "showRedwoods",
		name = "Show Redwood trees",
		description = "Configures whether to show a indicator for redwood trees",
		titleSection = "overlayTitle"
	)
	default boolean showRedwoodTrees()
	{
		return true;
	}

	@ConfigItem(
		position = 9,
		keyName = "showRespawnTimers",
		name = "Show respawn timers",
		description = "Configures whether to display the respawn timer overlay",
		titleSection = "overlayTitle"
	)
	default boolean showRespawnTimers()
	{
		return true;
	}
}