package net.runelite.client.plugins.autothiever;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("autothiever")
public interface AutoThieverConfig extends Config
{
	@ConfigItem(
		keyName = "hpCheckStyle",
		name = "Health Check Style",
		description = "",
		enumClass = HealthCheckStyle.class,
		position = 0
	)
	default HealthCheckStyle hpCheckStyle()
	{
		return HealthCheckStyle.PERCENTAGE;
	}

	@ConfigItem(
		keyName = "hpToEat",
		name = "Health To Eat At",
		description = "",
		position = 1
	)
	default int hpToEat()
	{
		return 50;
	}

	@ConfigItem(
		keyName = "itemId",
		name = "Food ID",
		description = "",
		position = 2
	)
	default int itemId()
	{
		return 1993;
	}

	@ConfigItem(
		keyName = "npcId",
		name = "NPC ID",
		description = "",
		position = 3
	)
	default int npcId() { return 3297; }

	@ConfigItem(
		keyName = "clickDelayMin",
		name = "Delay Min (Ticks)",
		description = "The minimum delay (in ticks)",
		position = 4
	)
	default int clickDelayMin()
	{
		return 8;
	}

	@ConfigItem(
		keyName = "clickDelayMax",
		name = "Delay Max (Ticks)",
		description = "The maximum delay (in ticks)",
		position = 5
	)
	default int clickDelayMax()
	{
		return 20;
	}

	@ConfigItem(keyName = "startButton",
		name = "Start",
		description = ""
	)
	default Button startButton() { return new Button(); }

	@ConfigItem(keyName = "stopButton",
		name = "Stop",
		description = ""
	)
	default Button stopButton() { return new Button(); }
}