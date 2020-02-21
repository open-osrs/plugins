/*
 * Copyright (c) 2017, Robin <robin.weymans@gmail.com>
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
package net.runelite.client.plugins.implings;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Title;

/**
 * @author robin
 */
@ConfigGroup("implings")
public interface ImplingsConfig extends Config
{
	enum ImplingMode
	{
		NONE,
		HIGHLIGHT,
		NOTIFY
	}

	@ConfigTitleSection(
		keyName = "puropuroTitle",
		name = "Puro puro",
		description = "",
		position = 1
	)
	default Title puropuroTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 2,
		keyName = "showspawn",
		name = "Show Spawn locations",
		description = "Configures whether or not spawn locations are displayed in Puro Puro",
		titleSection = "puropuroTitle"
	)
	default boolean showSpawn()
	{
		return false;
	}

	@ConfigItem(
		position = 3,
		keyName = "spawnColor",
		name = "Impling spawn color",
		description = "Text color for impling spawns in Puro Puro",
		titleSection = "puropuroTitle"
	)
	default Color getSpawnColor()
	{
		return Color.WHITE;
	}

	@ConfigItem(
		position = 4,
		keyName = "spawnColorDynamic",
		name = "Impling dynamic spawn color",
		description = "Text color for dynamic impling spawns in Puro Puro",
		titleSection = "puropuroTitle"
	)
	default Color getDynamicSpawnColor()
	{
		return Color.WHITE;
	}

	@ConfigTitleSection(
		keyName = "minimapTitle",
		name = "Minimap",
		description = "",
		position = 5
	)
	default Title minimapTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 6,
		keyName = "showname",
		name = "Show name on minimap",
		description = "Configures whether or not impling names are displayed on minimap",
		titleSection = "minimapTitle"
	)
	default boolean showName()
	{
		return false;
	}

	@ConfigTitleSection(
		keyName = "babyTitle",
		name = "Baby implings",
		description = "",
		position = 7
	)
	default Title babyTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 8,
		keyName = "showbaby",
		name = "Show Baby implings",
		description = "Configures whether or not Baby impling tags are displayed",
		titleSection = "babyTitle"
	)
	default ImplingMode showBaby()
	{
		return ImplingMode.NONE;
	}

	@ConfigItem(
		position = 9,
		keyName = "babyColor",
		name = "Baby impling color",
		description = "Text color for Baby implings",
		titleSection = "babyTitle",
		hidden = true,
		unhide = "showBaby",
		unhideValue = "HIGHLIGHT || NOTIFY"
	)
	default Color getBabyColor()
	{
		return new Color(177, 143, 179);
	}

	@ConfigTitleSection(
		keyName = "youngTitle",
		name = "Young implings",
		description = "",
		position = 10
	)
	default Title youngTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 11,
		keyName = "showyoung",
		name = "Show Young implings",
		description = "Configures whether or not Young impling tags are displayed",
		titleSection = "youngTitle"
	)
	default ImplingMode showYoung()
	{
		return ImplingMode.NONE;
	}

	@ConfigItem(
		position = 12,
		keyName = "youngColor",
		name = "Young impling color",
		description = "Text color for Young implings",
		titleSection = "youngTitle",
		hidden = true,
		unhide = "showyoung",
		unhideValue = "HIGHLIGHT || NOTIFY"
	)
	default Color getYoungColor()
	{
		return new Color(175, 164, 136);
	}

	@ConfigTitleSection(
		keyName = "gourmetTitle",
		name = "Gourmet implings",
		description = "",
		position = 13
	)
	default Title gourmetTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 14,
		keyName = "showgourmet",
		name = "Show Gourmet implings",
		description = "Configures whether or not Gourmet impling tags are displayed",
		titleSection = "gourmetTitle"
	)
	default ImplingMode showGourmet()
	{
		return ImplingMode.NONE;
	}

	@ConfigItem(
		position = 15,
		keyName = "gourmetColor",
		name = "Gourmet impling color",
		description = "Text color for Gourmet implings",
		titleSection = "gourmetTitle",
		hidden = true,
		unhide = "showgourmet",
		unhideValue = "HIGHLIGHT || NOTIFY"
	)
	default Color getGourmetColor()
	{
		return new Color(169, 131, 98);
	}

	@ConfigTitleSection(
		keyName = "earthTitle",
		name = "Earth implings",
		description = "",
		position = 16
	)
	default Title earthTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 17,
		keyName = "showearth",
		name = "Show Earth implings",
		description = "Configures whether or not Earth impling tags are displayed",
		titleSection = "earthTitle"
	)
	default ImplingMode showEarth()
	{
		return ImplingMode.NONE;
	}

	@ConfigItem(
		position = 18,
		keyName = "earthColor",
		name = "Earth impling color",
		description = "Text color for Earth implings",
		titleSection = "earthTitle",
		hidden = true,
		unhide = "showearth",
		unhideValue = "HIGHLIGHT || NOTIFY"
	)
	default Color getEarthColor()
	{
		return new Color(62, 86, 64);
	}

	@ConfigTitleSection(
		keyName = "essenceTitle",
		name = "Essence implings",
		description = "",
		position = 19
	)
	default Title essenceTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 20,
		keyName = "showessence",
		name = "Show Essence implings",
		description = "Configures whether or not Essence impling tags are displayed",
		titleSection = "essenceTitle"
	)
	default ImplingMode showEssence()
	{
		return ImplingMode.NONE;
	}

	@ConfigItem(
		position = 21,
		keyName = "essenceColor",
		name = "Essence impling color",
		description = "Text color for Essence implings",
		titleSection = "essenceTitle",
		hidden = true,
		unhide = "showessence",
		unhideValue = "HIGHLIGHT || NOTIFY"
	)
	default Color getEssenceColor()
	{
		return new Color(32, 89, 90);
	}

	@ConfigTitleSection(
		keyName = "eclecticTitle",
		name = "Eclectic implings",
		description = "",
		position = 22
	)
	default Title eclecticTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 23,
		keyName = "showeclectic",
		name = "Show Eclectic implings",
		description = "Configures whether or not Eclectic impling tags are displayed",
		titleSection = "eclecticTitle"
	)
	default ImplingMode showEclectic()
	{
		return ImplingMode.NONE;
	}

	@ConfigItem(
		position = 24,
		keyName = "eclecticColor",
		name = "Eclectic impling color",
		description = "Text color for Eclectic implings",
		titleSection = "eclecticTitle",
		hidden = true,
		unhide = "showeclectic",
		unhideValue = "HIGHLIGHT || NOTIFY"
	)
	default Color getEclecticColor()
	{
		return new Color(145, 155, 69);
	}

	@ConfigTitleSection(
		keyName = "natureTitle",
		name = "Nature implings",
		description = "",
		position = 25
	)
	default Title natureTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 26,
		keyName = "shownature",
		name = "Show Nature implings",
		description = "Configures whether or not Nature impling tags are displayed",
		titleSection = "natureTitle"
	)
	default ImplingMode showNature()
	{
		return ImplingMode.NONE;
	}

	@ConfigItem(
		position = 27,
		keyName = "natureColor",
		name = "Nature impling color",
		description = "Text color for Nature implings",
		titleSection = "natureTitle",
		hidden = true,
		unhide = "shownature",
		unhideValue = "HIGHLIGHT || NOTIFY"
	)
	default Color getNatureColor()
	{
		return new Color(92, 138, 95);
	}

	@ConfigTitleSection(
		keyName = "magpieTitle",
		name = "Magpie implings",
		description = "",
		position = 28
	)
	default Title magpieTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 29,
		keyName = "showmagpie",
		name = "Show Magpie implings",
		description = "Configures whether or not Magpie impling tags are displayed",
		titleSection = "magpieTitle"
	)
	default ImplingMode showMagpie()
	{
		return ImplingMode.NONE;
	}

	@ConfigItem(
		position = 30,
		keyName = "magpieColor",
		name = "Magpie impling color",
		description = "Text color for Magpie implings",
		titleSection = "magpieTitle",
		hidden = true,
		unhide = "showmagpie",
		unhideValue = "HIGHLIGHT || NOTIFY"
	)
	default Color getMagpieColor()
	{
		return new Color(142, 142, 19);
	}

	@ConfigTitleSection(
		keyName = "ninjaTitle",
		name = "Ninja implings",
		description = "",
		position = 31
	)
	default Title ninjaTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 32,
		keyName = "showninja",
		name = "Show Ninja implings",
		description = "Configures whether or not Ninja impling tags are displayed",
		titleSection = "ninjaTitle"
	)
	default ImplingMode showNinja()
	{
		return ImplingMode.NONE;
	}

	@ConfigItem(
		position = 33,
		keyName = "ninjaColor",
		name = "Ninja impling color",
		description = "Text color for Ninja implings",
		titleSection = "ninjaTitle",
		hidden = true,
		unhide = "showninja",
		unhideValue = "HIGHLIGHT || NOTIFY"
	)
	default Color getNinjaColor()
	{
		return new Color(71, 70, 75);
	}

	@ConfigTitleSection(
		keyName = "crystalTitle",
		name = "Crystal implings",
		description = "",
		position = 34
	)
	default Title crystalTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 35,
		keyName = "showCrystal",
		name = "Show Crystal implings",
		description = "Configures whether or not Crystal impling tags are displayed",
		titleSection = "crystalTitle"
	)
	default ImplingMode showCrystal()
	{
		return ImplingMode.NONE;
	}

	@ConfigItem(
		position = 36,
		keyName = "crystalColor",
		name = "Crystal impling color",
		description = "Text color for Crystal implings",
		titleSection = "crystalTitle",
		hidden = true,
		unhide = "showCrystal",
		unhideValue = "HIGHLIGHT || NOTIFY"
	)
	default Color getCrystalColor()
	{
		return new Color(93, 188, 210);
	}

	@ConfigTitleSection(
		keyName = "dragonTitle",
		name = "Dragon implings",
		description = "",
		position = 37
	)
	default Title dragonTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 38,
		keyName = "showdragon",
		name = "Show Dragon implings",
		description = "Configures whether or not Dragon impling tags are displayed",
		titleSection = "dragonTitle"
	)
	default ImplingMode showDragon()
	{
		return ImplingMode.HIGHLIGHT;
	}

	@ConfigItem(
		position = 39,
		keyName = "dragonColor",
		name = "Dragon impling color",
		description = "Text color for Dragon implings",
		titleSection = "dragonTitle",
		hidden = true,
		unhide = "showdragon",
		unhideValue = "HIGHLIGHT || NOTIFY"
	)
	default Color getDragonColor()
	{
		return new Color(210, 85, 75);
	}

	@ConfigTitleSection(
		keyName = "luckyTitle",
		name = "Lucky implings",
		description = "",
		position = 40
	)
	default Title luckyTitle()
	{
		return new Title();
	}

	@ConfigItem(
		position = 41,
		keyName = "showlucky",
		name = "Show Lucky implings",
		description = "Configures whether or not Lucky impling tags are displayed",
		titleSection = "luckyTitle"
	)
	default ImplingMode showLucky()
	{
		return ImplingMode.HIGHLIGHT;
	}

	@ConfigItem(
		position = 42,
		keyName = "luckyColor",
		name = "Lucky impling color",
		description = "Text color for Lucky implings",
		titleSection = "luckyTitle",
		hidden = true,
		unhide = "showlucky",
		unhideValue = "HIGHLIGHT || NOTIFY"
	)
	default Color getLuckyColor()
	{
		return new Color(102, 7, 101);
	}
}
