/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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
package net.runelite.client.plugins.gpu;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Title;
import static net.runelite.client.plugins.gpu.GpuPlugin.MAX_DISTANCE;
import static net.runelite.client.plugins.gpu.GpuPlugin.MAX_FOG_DEPTH;
import net.runelite.client.plugins.gpu.config.AntiAliasingMode;
import net.runelite.client.plugins.gpu.config.ColorBlindMode;
import net.runelite.client.plugins.gpu.config.UIScalingMode;
import net.runelite.client.plugins.gpu.config.WindowsScalingMode;

@ConfigGroup("gpu")
public interface GpuPluginConfig extends Config
{
	@ConfigTitleSection(
		keyName = "drawingTitle",
		name = "Drawing",
		description = "",
		position = 0
	)
	default Title drawingTitle()
	{
		return new Title();
	}

	@Range(
		min = 20,
		max = MAX_DISTANCE
	)
	@ConfigItem(
		keyName = "drawDistance",
		name = "Draw Distance",
		description = "Draw distance",
		position = 1,
		titleSection = "drawingTitle"
	)
	default int drawDistance()
	{
		return 25;
	}

	@ConfigItem(
		keyName = "smoothBanding",
		name = "Remove Color Banding",
		description = "Smooths out the color banding that is present in the CPU renderer",
		position = 2,
		titleSection = "drawingTitle"
	)
	default boolean smoothBanding()
	{
		return false;
	}

	@ConfigItem(
		keyName = "useComputeShaders",
		name = "Compute Shaders",
		description = "Offloads face sorting to GPU, enabling extended draw distance. Requires plugin restart.",
		position = 3,
		titleSection = "drawingTitle",
		warning = "This option requires a plugin restart and disables draw distance.\nOnly use as a last resort."
	)
	default boolean useComputeShaders()
	{
		return true;
	}

	@ConfigTitleSection(
		keyName = "scalingTitle",
		name = "Scaling",
		description = "",
		position = 4
	)
	default Title scalingTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "uiScalingMode",
		name = "UI scaling mode",
		description = "Sampling function to use for the UI in stretched mode",
		titleSection = "scalingTitle",
		position = 5
	)
	default UIScalingMode uiScalingMode()
	{
		return UIScalingMode.CATMULL_ROM;
	}

	@ConfigTitleSection(
		keyName = "ppTitle",
		name = "Post processing",
		description = "",
		position = 6
	)
	default Title ppTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "colorBlindMode",
		name = "Colorblindness Correction",
		description = "Adjusts colors to account for colorblindness",
		position = 7
	)
	default ColorBlindMode colorBlindMode()
	{
		return ColorBlindMode.NONE;
	}

	@ConfigItem(
		keyName = "antiAliasingMode",
		name = "Anti Aliasing",
		description = "Configures the anti-aliasing mode",
		position = 8,
		titleSection = "ppTitle"
	)
	default AntiAliasingMode antiAliasingMode()
	{
		return AntiAliasingMode.DISABLED;
	}

	@Range(
		min = 0,
		max = 16
	)
	@ConfigItem(
		keyName = "anisotropicFilteringLevel",
		name = "Anisotropic Filtering",
		description = "Configures the anisotropic filtering level.",
		position = 9,
		titleSection = "ppTitle"
	)
	default int anisotropicFilteringLevel()
	{
		return 0;
	}

	@ConfigTitleSection(
		keyName = "fogTitle",
		name = "Fog",
		description = "",
		position = 10
	)
	default Title fogTitle()
	{
		return new Title();
	}

	@Range(
		max = MAX_FOG_DEPTH
	)
	@ConfigItem(
		keyName = "fogDepth",
		name = "Depth",
		description = "Distance from the scene edge the fog starts",
		position = 11,
		titleSection = "fogTitle"
	)
	default int fogDepth()
	{
		return 30;
	}

	@Range(
		max = MAX_FOG_DEPTH
	)
	@ConfigItem(
		keyName = "fogCircularity",
		name = "Roundness",
		description = "Fog circularity in %",
		position = 12,
		titleSection = "fogTitle"
	)
	default int fogCornerRadius()
	{
		return 30;
	}

	@Range(
		max = MAX_FOG_DEPTH
	)
	@ConfigItem(
		keyName = "fogDensity",
		name = "Density",
		description = "Relative fog thickness",
		position = 13,
		titleSection = "fogTitle"
	)
	default int fogDensity()
	{
		return 11;
	}

	@ConfigTitleSection(
		keyName = "mirrorTitle",
		name = "Mirror",
		description = "",
		position = 14
	)
	default Title mirrorTitle()
	{
		return new Title();
	}

	@Range(
		min = 100,
		max = 500
	)
	@ConfigItem(
		keyName = "windowsScale",
		name = "Windows Scale",
		description = "Set this to the scale you use in Windows if mirror comes out incorrectly.",
		position = 15,
		titleSection = "mirrorTitle"
	)
	default WindowsScalingMode windowsScale()
	{
		return WindowsScalingMode.ONE;
	}
}