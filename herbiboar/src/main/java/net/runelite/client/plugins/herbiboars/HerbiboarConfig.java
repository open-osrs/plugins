/*
 * Copyright (c) 2017, Tyler <https://github.com/tylerthardy>
 * Copyright (c) 2019, Gamer1120 <https://github.com/Gamer1120>
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
package net.runelite.client.plugins.herbiboars;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Title;

@ConfigGroup("herbiboar")
public interface HerbiboarConfig extends Config
{
	@ConfigTitleSection(
		keyName = "objectsTitle",
		name = "Objects",
		description = "",
		position = 0
	)
	default Title objectsTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "trailTitle",
		name = "Trail",
		description = "",
		position = 1
	)
	default Title trailTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "colorsTitle",
		name = "Colors",
		description = "",
		position = 2
	)
	default Title colorsTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 0,
		keyName = "showStart",
		name = "Show Start Objects",
		description = "Show highlights for starting rocks and logs",
		titleSection = "objectsTitle"
	)
	default boolean isStartShown()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "showClickboxes",
		name = "Show Clickboxes",
		description = "Show clickboxes on trail objects and tunnels instead of tiles",
		titleSection = "objectsTitle"
	)
	default boolean showClickBoxes()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "showTunnel",
		name = "Show End Tunnels",
		description = "Show highlights for tunnels with herbiboars",
		titleSection = "objectsTitle"
	)
	default boolean isTunnelShown()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "showObject",
		name = "Show Trail Objects",
		description = "Show highlights for mushrooms, mud, seaweed, etc",
		titleSection = "objectsTitle"
	)
	default boolean isObjectShown()
	{
		return true;
	}

	@ConfigItem(
		position = 0,
		keyName = "showTrail",
		name = "Show Trail",
		description = "Show highlights for trail prints",
		titleSection = "trailTitle"
	)
	default boolean isTrailShown()
	{
		return true;
	}

	@ConfigItem(
		position = 0,
		keyName = "colorStart",
		name = "Start Color",
		description = "Color for rocks that start the trails",
		titleSection = "colorsTitle"
	)
	default Color getStartColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		position = 1,
		keyName = "colorTunnel",
		name = "Tunnel Color",
		description = "Color for tunnels with herbiboars",
		titleSection = "colorsTitle"
	)
	default Color getTunnelColor()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		position = 2,
		keyName = "colorGameObject",
		name = "Trail Object Color",
		description = "Color for mushrooms, mud, seaweed, etc",
		titleSection = "colorsTitle"
	)
	default Color getObjectColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		position = 3,
		keyName = "colorTrail",
		name = "Trail Color",
		description = "Color for mushrooms, mud, seaweed, etc",
		titleSection = "colorsTitle"
	)
	default Color getTrailColor()
	{
		return Color.WHITE;
	}
}
