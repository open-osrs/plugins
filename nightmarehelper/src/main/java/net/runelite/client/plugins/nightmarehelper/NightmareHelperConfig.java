package net.runelite.client.plugins.nightmarehelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("NightmareHelperConfig")
public interface NightmareHelperConfig extends Config {

	@ConfigItem(
		position = 1,
		keyName = "swapNightmareMelee",
		name = "Swap on Nightmare's Melee Animation",
		description = "This will swap prayers for nightmare's melee attacks"
	)
	default boolean swapNightmareMelee()
	{
		return true;
	}

	@Range(
		min = 0,
		max = 1
	)
	@ConfigItem(
		keyName = "ticksSleepMelee",
		name = "Pray Melee Wait-Ticks",
		description = "Amount of ticks to wait to Pray Melee",
		position = 2
	)
	default int ticksSleepMelee()
	{
		return 0;
	}

	@Range(
		min = 0,
		max = 2
	)
	@ConfigItem(
		keyName = "ticksSleepMageRange",
		name = "Pray Mage/Range Wait-Ticks",
		description = "Amount of ticks to wait to Pray Mage/Range",
		position = 2
	)
	default int ticksSleepRangeMage()
	{
		return 0;
	}

}