package net.runelite.client.plugins.eventdebugger;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("eventdebugger")
public interface EventDebuggerConfig extends Config
{
	@ConfigItem(
		keyName = "menuEntryAdded",
		name = "MenuEntryAdded",
		description = ""
	)
	default boolean menuEntryAdded()
	{
		return false;
	}

	@ConfigItem(
		keyName = "optionFilter",
		name = "Option Filter",
		description = "case sensitive"
	)
	default String optionFilter() { return ""; }

	@ConfigItem(
		keyName = "targetFilter",
		name = "Target Filter",
		description = "case sensitive"
	)
	default String targetFilter() { return ""; }

	@ConfigItem(
		keyName = "identifierFilter",
		name = "Identifier Filter",
		description = "case sensitive"
	)
	default int identifierFilter() { return -1; }

	@ConfigItem(
		keyName = "opcodeFilter",
		name = "Opcode Filter",
		description = "case sensitive"
	)
	default int opcodeFilter() { return -1; }

	@ConfigItem(
		keyName = "param0Filter",
		name = "Param0 Filter",
		description = "case sensitive"
	)
	default int param0Filter() { return -1; }

	@ConfigItem(
		keyName = "param1Filter",
		name = "Param1 Filter",
		description = "case sensitive"
	)
	default int param1Filter() { return -1; }
}
