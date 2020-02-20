/*
 * Copyright (c) 2018, Jeremy Plsek <https://github.com/jplsek>
 * Copyright (c) 2019, gregg1494 <https://github.com/gregg1494>
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
package net.runelite.client.plugins.inventorygrid;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Title;
import net.runelite.client.config.Units;

@ConfigGroup("inventorygrid")
public interface InventoryGridConfig extends Config
{
	@ConfigTitleSection(
		keyName = "gridsTitle",
		name = "Grids",
		description = "",
		position = 1
	)

	default Title gridsTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "showInventoryGrid",
		name = "Show inventory grid",
		description = "Show a grid on the inventory while dragging",
		position = 2,
		titleSection = "gridsTitle"
	)
	default boolean showInventoryGrid()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showBankGrid",
		name = "Show bank grid",
		description = "Show a grid on the bank while dragging",
		position = 3,
		titleSection = "gridsTitle"
	)
	default boolean showBankGrid()
	{
		return true;
	}

	@ConfigTitleSection(
		keyName = "overlayTitle",
		name = "Overlay",
		description = "",
		position = 4
	)
	default Title overlayTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "showItem",
		name = "Show item",
		description = "Show a preview of the item in the new slot",
		position = 5,
		titleSection = "overlayTitle"
	)
	default boolean showItem()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showHighlight",
		name = "Highlight background",
		description = "Show a background highlight on the new slot",
		position = 6,
		titleSection = "overlayTitle"
	)
	default boolean showHighlight()
	{
		return true;
	}

	@ConfigItem(
		keyName = "dragDelay",
		name = "Drag Delay",
		description = "Time in ms to wait after item press before showing grid",
		position = 7,
		titleSection = "overlayTitle"
	)
	@Range(min = 100)
	@Units(Units.MILLISECONDS)
	default int dragDelay()
	{
		return 100;
	}

	@ConfigTitleSection(
		keyName = "colorsTitle",
		name = "Colors",
		description = "",
		position = 8
	)
	default Title colorsTitle()
	{
		return new Title();
	}

	@Alpha
	@ConfigItem(
		keyName = "gridColor",
		name = "Grid color",
		description = "The color of the grid",
		position = 9,
		titleSection = "colorsTitle"
	)
	default Color gridColor()
	{
		return new Color(255, 255, 255, 45);
	}

	@Alpha
	@ConfigItem(
		keyName = "highlightColor",
		name = "Highlight color",
		description = "The color of the new inventory slot highlight",
		position = 10,
		titleSection = "colorsTitle"
	)
	default Color highlightColor()
	{
		return new Color(0, 255, 0, 45);
	}
}
