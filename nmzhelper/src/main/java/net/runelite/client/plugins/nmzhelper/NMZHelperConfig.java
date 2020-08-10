package net.runelite.client.plugins.nmzhelper;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("nmzhelper")
public interface NMZHelperConfig extends Config
{
	@ConfigItem(
		keyName = "absorptionThreshold",
		name = "Absorption Threshold",
		description = "The amount of absorption points to maintain.",
		position = 1
	)
	default int absorptionThreshold()
	{
		return 200;
	}

	@ConfigItem(
		keyName = "overloadDoses",
		name = "Overload Doses",
		description = "The amount of doses of overload to withdraw.",
		position = 2
	)
	default int overloadDoses() { return 20; }

	@ConfigItem(
		keyName = "absorptionDoses",
		name = "Absorption Doses",
		description = "The amount of doses of absorption to withdraw.",
		position = 3
	)
	default int absorptionDoses() { return 88; }

	@ConfigItem(
		keyName = "useSpecialAttack",
		name = "Use Special Attack",
		description = "Whether to use special attack or not",
		position = 4
	)
	default boolean useSpecialAttack() { return false; }

	@ConfigItem(
		keyName = "specialAttackValue",
		name = "Special Attack Value",
		description = "The value to use special attack at",
		position = 5,
		hidden = true,
		unhide = "useSpecialAttack"
	)
	default int specialAttackValue() { return 100; }

	@ConfigItem(keyName = "startButton",
		name = "Start",
		description = "",
		position = 6
	)
	default Button startButton() { return new Button(); }

	@ConfigItem(
		keyName = "stopButton",
		name = "Stop",
		description = "",
		position = 7
	)
	default Button stopButton() { return new Button(); }
}
