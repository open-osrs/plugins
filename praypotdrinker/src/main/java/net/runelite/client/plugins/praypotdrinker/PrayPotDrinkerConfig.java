package net.runelite.client.plugins.praypotdrinker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("praypotdrinker")
public interface PrayPotDrinkerConfig extends Config
{
	@ConfigItem(
		keyName = "potionNames",
		name = "Potion Names",
		description = "The names of the prayer potions to drink, separated by commas.",
		position = 1
	)
	default String potionNames()
	{
		return new String("Prayer potion(1),Prayer potion(2),Prayer potion(3),Prayer potion(4),Super restore(4),Super restore(3),Super restore(2),Super restore(1)");
	}
}
