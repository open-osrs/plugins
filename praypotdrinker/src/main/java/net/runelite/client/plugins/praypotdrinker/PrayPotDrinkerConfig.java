package net.runelite.client.plugins.praypotdrinker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("praypotdrinker")
public interface PrayPotDrinkerConfig extends Config
{
	@ConfigItem(
		keyName = "minPrayerLevel",
		name = "Minimum",
		description = "",
		position = 1
	)
	default int minPrayerLevel() { return 1; }

	@ConfigItem(
		keyName = "maxPrayerLevel",
		name = "Maximum",
		description = "",
		position = 2
	)
	default int maxPrayerLevel() { return 30; }
}
