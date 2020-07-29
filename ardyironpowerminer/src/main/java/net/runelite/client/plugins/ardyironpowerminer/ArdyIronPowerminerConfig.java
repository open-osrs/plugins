package net.runelite.client.plugins.ardyironpowerminer;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;

@ConfigGroup("ardyironpowerminer")
public interface ArdyIronPowerminerConfig extends Config
{
	@ConfigTitleSection(
		position = 0,
		keyName = "delaySection",
		name = "Settings",
		description = ""
	)

	@ConfigItem(
		keyName = "clickDelayMin",
		name = "Click Delay Min (ms)",
		description = "The minimum delay between clicks (in milliseconds)",
		position = 0,
		titleSection = "delaySection"
	)
	default int clickDelayMin()
	{
		return 20;
	}

	@ConfigItem(
		keyName = "clickDelayMax",
		name = "Click Delay Max (ms)",
		description = "The maximum delay between clicks (in milliseconds)",
		position = 1,
		titleSection = "delaySection"
	)
		default int clickDelayMax()
		{
			return 200;
		}

	@ConfigTitleSection(
		position = 1,
		keyName = "controlsSection",
		name = "Controls",
		description = ""
	)

	@ConfigItem(keyName = "startButton",
		name = "Start",
		description = "",
		position = 0,
		titleSection = "controlsSection"
	)
	default Button startButton() { return new Button(); }

	@ConfigItem(keyName = "stopButton",
		name = "Stop",
		description = "",
		position = 1,
		titleSection = "controlsSection"
	)
	default Button stopButton() { return new Button(); }
}