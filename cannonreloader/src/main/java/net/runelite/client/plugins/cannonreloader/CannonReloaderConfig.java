package net.runelite.client.plugins.cannonreloader;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("cannonreloader")
public interface CannonReloaderConfig extends Config
{
	@ConfigItem(
		keyName = "minReloadAmount",
		name = "Minimum count for reload",
		description = "The minimum cannonball count when we want to reload",
		position = 1
	)
	default int minReloadAmount()
	{
		return 9;
	}

	@ConfigItem(
		keyName = "maxReloadAmount",
		name = "Maximum count for reload",
		description = "The maximum cannonball count when we want to reload",
		position = 2
	)
	default int maxReloadAmount()
	{
		return 14;
	}

	@ConfigItem(
		keyName = "clickDelay",
		name = "Click Delay (ms)",
		description = "The delay between clicks on the cannon (in milliseconds)",
		position = 3
	)
	default int clickDelay()
	{
		return 2000;
	}
}