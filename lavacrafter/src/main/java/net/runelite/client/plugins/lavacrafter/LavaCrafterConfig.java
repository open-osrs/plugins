package net.runelite.client.plugins.lavacrafter;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Title;

@ConfigGroup("lavacrafter")
public interface LavaCrafterConfig extends Config
{
	/*@ConfigItem(
		position = 0,
		keyName = "optionsTitle",
		name = "Options",
		description = ""
	)
	default Title optionsTitle() { return new Title(); }

	@ConfigItem(
		position = 1,
		keyName = "useSmallPouch",
		name = "Use Small Pouch",
		description = ""
	)
	default boolean useSmallPouch() { return false; }

	@ConfigItem(
		position = 2,
		keyName = "useMediumPouch",
		name = "Use Medium Pouch",
		description = ""
	)
	default boolean useMediumPouch() { return false; }

	@ConfigItem(
		position = 3,
		keyName = "useLargePouch",
		name = "Use Large Pouch",
		description = ""
	)
	default boolean useLargePouch() { return false; }

	@ConfigItem(
		position = 4,
		keyName = "useGiantPouch",
		name = "Use Giant Pouch",
		description = ""
	)
	default boolean useGiantPouch() { return false; } */

	@ConfigItem(
		position = 5,
		keyName = "useBindingNecklace",
		name = "Use Binding Necklace",
		description = ""
	)
	default boolean useBindingNecklace() { return true; }

	@ConfigItem(
		position = 6,
		keyName = "autoEnableRun",
		name = "Auto Enable Run",
		description = "",
		titleSection = "optionSection"
	)
	default boolean autoEnableRun() { return true; }

	@ConfigItem(
		position = 7,
		keyName = "useMagicImbue",
		name = "Use Magic Imbue",
		description = "if disabled, will withdraw & use earth talismans instead",
		titleSection = "optionSection"
	)
	default boolean useMagicImbue() { return false; }

	@ConfigItem(
		position = 8,
		keyName = "disablePaint",
		name = "Disable Paint",
		description = "will disable drawing anything on screen",
		titleSection = "optionSection"
	)
	default boolean disablePaint() { return false; }

	@ConfigItem(
		position = 9,
		keyName = "stopConditions",
		name = "Stop Conditions",
		description = ""
	)
	default Title stopConditionTitle() { return new Title(); }

	@ConfigItem(
		position = 10,
		keyName = "useLevelStopCondition",
		name = "Use Level Stop Condition",
		description = ""
	)
	default boolean useLevelStopCondition() { return false; }

	@ConfigItem(
		position = 11,
		keyName = "levelStopConditionValue",
		name = "Level To Stop @",
		description = "",
		unhide = "useLevelStopCondition",
		hidden = true
	)
	default int levelStopConditionValue() { return 99; }

	@ConfigItem(
		position = 12,
		keyName = "useTimeStopCondition",
		name = "Use Time Stop Condition",
		description = ""
	)
	default boolean useTimeStopCondition() { return false; }

	@ConfigItem(
		position = 13,
		keyName = "timeStopConditionValue",
		name = "Minutes To Run For",
		description = "",
		unhide = "useTimeStopCondition",
		hidden = true
	)
	default int timeStopConditionValue() { return 60; }

	@ConfigItem(
		position = 14,
		keyName = "delayTitle",
		name = "Delay",
		description = ""
	)
	default Title delayTitle() { return new Title(); }

	@ConfigItem(
		position = 15,
		keyName = "tickDelayMin",
		name = "Tick Delay Min",
		description = "The minimum tick delay between clicks"
	)
	default int clickDelayMin() { return 1; }

	@ConfigItem(
		keyName = "tickDelayMax",
		name = "Tick Delay Max",
		description = "The maximum tick delay between clicks",
		position = 16
	)
	default int clickDelayMax() { return 5; }

	@ConfigItem(
		position = 17,
		keyName = "controlsTitle",
		name = "Controls",
		description = ""
	)
	default Title controlsTitle() { return new Title(); }

	@ConfigItem(keyName = "startButton",
		name = "Start",
		description = "",
		position = 18
	)
	default Button startButton() { return new Button(); }

	@ConfigItem(
		keyName = "stopButton",
		name = "Stop",
		description = "",
		position = 19
	)
	default Button stopButton() { return new Button(); }
}