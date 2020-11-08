package renderer.plugin;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.plugins.gpu.config.AntiAliasingMode;

@ConfigGroup("betterRenderer")
public interface BetterRendererConfig extends Config
{
	@ConfigItem(keyName = "samples", name = "Samples", description = "Number of MSAA samples (0 to disable)")
	default AntiAliasingMode samples()
	{
		return AntiAliasingMode.MSAA_8;
	}

	@ConfigItem(keyName = "viewDistance", name = "View distance", description = "View distance radius in tiles")
	@Range(max = 2500)
	default int viewDistance()
	{
		return 250;
	}

	@ConfigItem(keyName = "improvedZoom", name = "Use improved zoom", description =
		"The vanilla OSRS applies zoom after perspective projection, resulting in FOV distortion, " +
			"especially with increased zoom limit. Enabling this setting will fix this by adjusting " +
			"camera distance to zoom in. However, interface elements such as tile markers may be " +
			"slightly offset near the edges of the screen."
	)
	default boolean improvedZoom()
	{
		return false;
	}

	@ConfigItem(keyName = "minimumInterfaceFps", name = "Minimum interface FPS", description = "If FPS drops below this value, world FPS" +
		"will be lowered to keep interface FPS high enough (0 to disable)."
	)
	@Range(max = 20)
	default int minInterfaceFps()
	{
		return 10;
	}

	@ConfigItem(keyName = "offThreadRendering", name = "Off-thread rendering", description = "If enabled, world will be rendered on a separate thread", hidden = true)
	default boolean offThreadRendering()
	{ // todo: broken
		return true;
	}

	@ConfigItem(keyName = "roofRemovalRadius", name = "Roof removal radius", description = "Roofs closer than this value will be removed")
	@Range(max = 100)
	default int roofRemovalRadius()
	{
		return 10;
	}
}
