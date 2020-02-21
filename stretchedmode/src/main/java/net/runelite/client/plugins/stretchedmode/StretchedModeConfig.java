/*
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.stretchedmode;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Title;
import net.runelite.client.config.Units;

@ConfigGroup("stretchedmode")
public interface StretchedModeConfig extends Config
{
	@ConfigTitleSection(
		keyName = "modeTitle",
		name = "Mode",
		description = "",
		position = 1
	)
	default Title modeTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "increasedPerformance",
		name = "Increased performance mode",
		description = "Uses a fast algorithm when stretching, lowering quality but increasing performance.",
		titleSection = "modeTitle",
		position = 2
	)
	default boolean increasedPerformance()
	{
		return false;
	}

	@ConfigTitleSection(
		keyName = "scalingTitle",
		name = "Scaling",
		description = "",
		position = 3
	)
	default Title scalingTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "keepAspectRatio",
		name = "Keep aspect ratio",
		description = "Keeps the aspect ratio when stretching.",
		titleSection = "scalingTitle",
		position = 4
	)
	default boolean keepAspectRatio()
	{
		return false;
	}

	@ConfigItem(
		keyName = "integerScaling",
		name = "Integer Scaling",
		description = "Forces use of a whole number scale factor when stretching.",
		titleSection = "scalingTitle",
		position = 5
	)
	default boolean integerScaling()
	{
		return false;
	}

	@ConfigItem(
		keyName = "scalingFactor",
		name = "Resizable Scaling",
		description = "In resizable mode, the game is reduced in size this much before it's stretched.",
		titleSection = "scalingTitle",
		position = 6
	)
	@Units(Units.PERCENT)
	default int scalingFactor()
	{
		return 50;
	}
}
