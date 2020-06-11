/*
 * Copyright 2019 Jarred Vardy <jarredvardy@gmail.com>
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
package net.runelite.client.plugins.mining;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Title;
import net.runelite.client.config.Units;

@ConfigGroup("mining")
public interface MiningConfig extends Config
{
	@ConfigTitleSection(
		keyName = "coalBagTitle",
		name = "Coal bag",
		description = "",
		position = 1
	)
	default Title coalBagTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "showCoalBagOverlay",
		name = "Show coal bag overlay",
		description = "Overlays how much coal is inside of your coal bag",
		titleSection = "coalBagTitle",
		position = 2
	)
	default boolean showCoalBagOverlay()
	{
		return true;
	}

	@ConfigTitleSection(
		keyName = "progressTitle",
		name = "Progress",
		description = "",
		position = 3
	)
	default Title progressTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "progressPieInverted",
		name = "Invert progress pie",
		description = "Configures whether the progress pie goes from empty to full or the other way around",
		titleSection = "progressTitle",
		position = 4
	)
	default boolean progressPieInverted()
	{
		return false;
	}

	@Range(
		min = 1,
		max = 50
	)
	@ConfigItem(
		keyName = "progressPieDiameter",
		name = "Progress pie diameter",
		description = "Configures how big the progress pie is",
		titleSection = "progressTitle",
		position = 5
	)
	@Units(Units.PIXELS)
	default int progressPieDiameter()
	{
		return 30;
	}

	@ConfigTitleSection(
		keyName = "colorsTitle",
		name = "Colors",
		description = "",
		position = 6
	)
	default Title colorsTitle()
	{
		return new Title();
	}

	@Alpha
	@ConfigItem(
		keyName = "progressPieColor",
		name = "Main progress pie color",
		description = "Configures the color of the main progress pie",
		titleSection = "colorsTitle",
		position = 7
	)
	default Color progressPieColor()
	{
		return Color.YELLOW;
	}

	@Alpha
	@ConfigItem(
		keyName = "progressPieColorMotherlode",
		name = "Motherlode random respawn threshold progress pie color",
		description = "Configures the color of the progress pie after Motherlode respawn threshold",
		titleSection = "colorsTitle",
		position = 8
	)
	default Color progressPieColorMotherlode()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		keyName = "amountOfCoalInCoalBag",
		name = "",
		description = "To store coal amount between sessions",
		hidden = true
	)
	default int amountOfCoalInCoalBag()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "amountOfCoalInCoalBag",
		name = "",
		description = "Overload to set coal amount",
		hidden = true
	)
	void amountOfCoalInCoalBag(int amount);

	@ConfigTitleSection(
		keyName = "other",
		name = "Other",
		description = "Other features",
		position = 9
	)
	default Title other()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "highlightActiveDaeyaltEssence",
		name = "Highlight active daeyalt essence",
		description = "Overlay the active Daeyalt Essence with a colored outline.",
		position = 10,
		titleSection = "other"
	)
	default boolean highlightActiveDaeyaltEssence()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		keyName = "activeDaeyaltEssenceColor",
		name = "Daeyalt outline color",
		description = "Color of the outline.",
		titleSection = "other",
		hidden = true,
		unhide = "highlightActiveDaeyaltEssence",
		position = 11

	)
	default Color activeDaeyaltEssenceColor()
	{
		return Color.BLUE;
	}
}
