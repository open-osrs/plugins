package net.runelite.client.plugins.mirrormode;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("mirrormode")
public interface MirrorModeConfig extends Config
{
	@ConfigItem(
		position = 0,
		keyName = "mirrorName",
		name = "Display player name in title",
		description = "Append the player's to the mirrored window title (if logged in)"
	)
	default boolean mirrorName()
	{
		return true;
	}
}