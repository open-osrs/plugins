package net.runelite.client.plugins.nmzhelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("nmzhelper")
public interface NMZHelperConfig extends Config
{
	@ConfigItem(
		keyName = "absorptionThreshold",
		name = "Absorption Threshold",
		description = "The amount of points to drink absorptions at.",
		position = 1
	)
	default int absorptionThreshold()
	{
		return 200;
	}

	@ConfigItem(
		keyName = "autoOverload",
		name = "Drink Overloads",
		description = "Automatically drink overloads",
		position = 2
	)
	default boolean autoOverload()
	{
		return true;
	}

	@ConfigItem(
		keyName = "autoRockCake",
		name = "Rock Cake",
		description = "Automatically use rock cake to 1 hp?",
		position = 3
	)
	default boolean autoRockCake()
	{
		return true;
	}

	@ConfigItem(
		keyName = "maxRockCakeDelay",
		name = "Max Rock Cake Delay",
		description = "The maximum ticks to wait before rock caking when HP is above 1.",
		position = 4
	)
	default int maxRockCakeDelay()
	{
		return 20;
	}
}
