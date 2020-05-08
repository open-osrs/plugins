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
}
