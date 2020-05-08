package net.runelite.client.plugins.specialattackuser;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("specialattackuser")
public interface SpecialAttackUserConfig extends Config
{
	@ConfigItem(
		keyName = "specialPercent",
		name = "Percent",
		description = "The special percent to enable special attack at.",
		position = 1
	)
	default int specialPercent()
	{
		return 100;
	}
}
