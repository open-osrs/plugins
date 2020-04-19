/*
 * Copyright (c) 2020, chestnut1693 <chestnut1693@gmail.com>
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
package net.runelite.client.plugins.raidsthieving;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("batslocator")
public interface BatsLocatorConfig extends Config
{
	enum DisplayMode
	{
		DOTS,
		NUMBERS
	}

	@ConfigItem(
		keyName = "unvisitedColor",
		name = "Unvisited chest color",
		description = "Configures the color of the unvisited chest dot and number",
		position = 0
	)
	default Color unvisitedColor()
	{
		return Color.magenta;
	}

	@ConfigItem(
		keyName = "batsColor",
		name = "Bats chest color",
		description = "Configures the color of the bats chest dot and number",
		position = 1
	)
	default Color batsColor()
	{
		return Color.white;
	}

	@ConfigItem(
		keyName = "poisonColor",
		name = "Poison chest color",
		description = "Configures the color of the poison chest dot and number",
		position = 2
	)
	default Color poisonColor()
	{
		return Color.green;
	}

	@Range(
		max = 27
	)
	@ConfigItem(
		keyName = "dotSize",
		name = "Dot size",
		description = "Configures the size of the transparent dots, solid dots are one third larger",
		position = 3
	)
	default int dotSize()
	{
		return 9;
	}

	@Range(
		max = 255
	)
	@ConfigItem(
		keyName = "transparency",
		name = "Transparency",
		description = "Configures the transparency of the chest dots and numbers that are not likely to contain poison or bats",
		position = 4
	)
	default int transparency()
	{
		return 75;
	}

	@ConfigItem(
		keyName = "displayMode",
		name = "Display mode",
		description = "Configures displaying chest states as dots or numbers",
		position = 5
	)
	default DisplayMode displayMode()
	{
		return DisplayMode.DOTS;
	}
}