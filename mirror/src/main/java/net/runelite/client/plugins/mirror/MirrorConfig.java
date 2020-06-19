package net.runelite.client.plugins.mirror;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("mirror")
public interface MirrorConfig extends Config
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
