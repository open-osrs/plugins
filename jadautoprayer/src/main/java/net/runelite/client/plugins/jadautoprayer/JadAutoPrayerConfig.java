package net.runelite.client.plugins.jadautoprayer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("jadautoprayerplugin")
public interface JadAutoPrayerConfig extends Config
{
	@ConfigItem(
		position = 0,
		keyName = "autoSwitchPrayers",
		name = "Auto Prayer Switcher",
		description = "Automatically switches prayers against Jad."
	)
	default boolean autoSwitchPrayers()
	{
		return true;
	}
}
