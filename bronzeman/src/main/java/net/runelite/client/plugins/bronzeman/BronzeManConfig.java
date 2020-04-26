package net.runelite.client.plugins.bronzeman;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;

@ConfigGroup("bronzemanmode")
public interface BronzeManConfig extends Config
{
	@ConfigTitleSection(
		name = "Unlocked Notifications",
		description = "",
		position = 0,
		keyName = "unlockNotifications"
	)
	default boolean unlockNotifications()
	{
		return false;
	}

	@ConfigTitleSection(
		name = "Game Mode Options",
		description = "",
		position = 1,
		keyName = "gameModeOptions"
	)
	default boolean gameModeOptions()
	{
		return false;
	}

	@ConfigTitleSection(
		name = "Chat Commands",
		description = "",
		position = 2,
		keyName = "chatCommands"
	)
	default boolean chatCommands()
	{
		return false;
	}

	@ConfigItem(
		keyName = "resetCommand",
		name = "Enable resetunlocks command",
		description = "Enables the !resetunlocks command used for wiping your unlocked items.",
		position = 0,
		titleSection = "chatCommands"
	)
	default boolean resetCommand()
	{
		return false;
	}

	@ConfigItem(
		keyName = "countCommand",
		name = "Enable countunlocks command",
		description = "Enables the !countunlocks command used for counting your unlocked items.",
		position = 1,
		titleSection = "chatCommands"
	)
	default boolean countCommand()
	{
		return true;
	}

	@ConfigItem(
		keyName = "backupCommand",
		name = "Enable backupunlocks command",
		description = "Enables the !backupunlocks command used for backing up your unlocked items.",
		position = 2,
		titleSection = "chatCommands"
	)
	default boolean backupCommand()
	{
		return true;
	}

	@ConfigItem(
		keyName = "restoreCommand",
		name = "Enable restoreunlocks command",
		description = "Enables the !restoreunlocks command used for restoring your unlocked items file.",
		position = 3,
		titleSection = "chatCommands"
	)
	default boolean restoreCommand()
	{
		return true;
	}

	@ConfigItem(
		keyName = "deleteCommand",
		name = "Enable deleteunlocks command",
		description = "Enables the !deleteunlocks command used for deleting your unlocked items file.",
		position = 4,
		titleSection = "chatCommands"
	)
	default boolean deleteCommand()
	{
		return false;
	}

	@ConfigItem(
		keyName = "progressionPaused",
		name = "",
		description = "",
		position = 5,
		titleSection = "chatCommands",
		hidden = true
	)
	default boolean progressionPaused()
	{
		return false;
	}

	@ConfigItem(
		keyName = "progressionPaused",
		name = "",
		description = "",
		position = 6,
		titleSection = "chatCommands"
	)
	void progressionPaused(boolean condition);

	@ConfigItem(
		keyName = "screenshotUnlocks",
		name = "Screenshot Unlocks",
		description = "Take a screenshot of item unlocks",
		position = 0,
		titleSection = "unlockNotifications"
	)
	default boolean screenshotUnlocks()
	{
		return false;
	}

	@ConfigItem(
		keyName = "itemUnlockChatMessage",
		name = "Item Unlock Chat Notification",
		description = "Sends out a chat message when you unlocked a new item.",
		position = 1,
		titleSection = "unlockNotifications"
	)
	default boolean itemUnlockChatMessage()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hardcoreBronzeMan",
		name = "Hardcore Bronzeman",
		description = "Wipes your unlocks on death and pauses unlocking new items until you type !continue",
		position = 0,
		titleSection = "gameModeOptions"
	)
	default boolean hardcoreBronzeMan()
	{
		return false;
	}

	//hidden items start here
	@ConfigItem(
		keyName = "startItemsUnlocked",
		name = "",
		description = "",
		position = 1,
		titleSection = "gameModeOptions",
		hidden = true
	)
	default boolean startItemsUnlocked()
	{
		return false;
	}

	@ConfigItem(
		keyName = "startItemsUnlocked",
		name = "",
		description = "",
		position = 2,
		titleSection = "gameModeOptions"
	)
	void startItemsUnlocked(boolean condition);

}
