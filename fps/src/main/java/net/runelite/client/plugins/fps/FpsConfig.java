/*
 * Copyright (c) 2017, Levi <me@levischuck.com>
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
package net.runelite.client.plugins.fps;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Title;
import net.runelite.client.config.Units;

@ConfigGroup(FpsPlugin.CONFIG_GROUP_KEY)
public interface FpsConfig extends Config
{
	@ConfigTitleSection(
		keyName = "overlayTitle",
		name = "Overlay",
		description = "",
		position = 1
	)
	default Title overlayTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "drawFps",
		name = "Draw FPS indicator",
		description = "Show a number in the corner for the current FPS",
		position = 2,
		titleSection = "overlayTitle"
	)
	default boolean drawFps()
	{
		return true;
	}

	@ConfigTitleSection(
		keyName = "globalTitle",
		name = "Global",
		description = "",
		position = 3
	)
	default Title globalTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "limitFps",
		name = "Limit Global FPS",
		description = "Global FPS limit in effect regardless of<br>" +
			"whether window is in focus or not",
		position = 4,
		titleSection = "globalTitle"
	)
	default boolean limitFps()
	{
		return false;
	}

	@Range(
		min = 1,
		max = 50
	)
	@ConfigItem(
		keyName = "maxFps",
		name = "Global FPS target",
		description = "Desired max global frames per second",
		position = 5,
		titleSection = "globalTitle"
	)
	@Units(Units.FPS)
	default int maxFps()
	{
		return 50;
	}

	@ConfigTitleSection(
		keyName = "unfocusedTitle",
		name = "Unfocused",
		description = "",
		position = 6
	)
	default Title unfocusedTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "limitFpsUnfocused",
		name = "Limit FPS unfocused",
		description = "FPS limit while window is out of focus",
		position = 7,
		titleSection = "unfocusedTitle"
	)
	default boolean limitFpsUnfocused()
	{
		return false;
	}

	@Range(
		min = 1,
		max = 50
	)
	@ConfigItem(
		keyName = "maxFpsUnfocused",
		name = "Unfocused FPS target",
		description = "Desired max frames per second for unfocused",
		position = 8,
		titleSection = "unfocusedTitle"
	)
	@Units(Units.FPS)
	default int maxFpsUnfocused()
	{
		return 50;
	}
}
