package net.runelite.client.plugins.pktools;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Title;

import java.awt.event.KeyEvent;

@ConfigGroup("pktools")
public interface PkToolsConfig extends Config
{
	String commands = "protectitem, rigour, augury, piety, " +
		"incrediblereflexes, ultimatestrength, steelskin, eagleeye, mysticmight, " +
		"freeze, vengeance, teleblock, entangle, spec, wait, group#, id_#, " +
		"protectfrommagic, protectfrommissiles, protectfrommelee";

	@ConfigItem(
		keyName = "label1",
		name = "Hotkeys",
		description = "",
		position = 1
	)
	default Title label1()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "key1",
		name = "Hotkey 1",
		description = "Activates script for this key.",
		position = 2
	)
	default Keybind key1()
	{
		return new Keybind(KeyEvent.VK_1, 0);
	}

	@ConfigItem(
		keyName = "key2",
		name = "Hotkey 2",
		description = "Activates script for this key.",
		position = 3
	)
	default Keybind key2()
	{
		return new Keybind(KeyEvent.VK_2, 0);
	}

	@ConfigItem(
		keyName = "key3",
		name = "Hotkey 3",
		description = "Activates script for this key.",
		position = 4
	)
	default Keybind key3()
	{
		return new Keybind(KeyEvent.VK_3, 0);
	}

	@ConfigItem(
		keyName = "key4",
		name = "Hotkey 4",
		description = "Activates script for this key.",
		position = 5
	)
	default Keybind key4()
	{
		return new Keybind(KeyEvent.VK_4, 0);
	}

	@ConfigItem(
		keyName = "key5",
		name = "Hotkey 5",
		description = "Activates script for this key.",
		position = 6
	)
	default Keybind key5()
	{
		return new Keybind(KeyEvent.VK_5, 0);
	}

	@ConfigItem(
		keyName = "key6",
		name = "Hotkey 6",
		description = "Activates script for this key.",
		position = 7
	)
	default Keybind key6()
	{
		return new Keybind(KeyEvent.VK_6, 0);
	}

	@ConfigItem(
		keyName = "key7",
		name = "Hotkey 7",
		description = "Activates script for this key.",
		position = 8
	)
	default Keybind key7()
	{
		return new Keybind(KeyEvent.VK_7, 0);
	}

	@ConfigItem(
		keyName = "key8",
		name = "Hotkey 8",
		description = "Activates script for this key.",
		position = 9
	)
	default Keybind key8()
	{
		return new Keybind(KeyEvent.VK_8, 0);
	}

	@ConfigItem(
		position = 10,
		keyName = "key1_script",
		name = "Key 1 Script",
		description = PkToolsConfig.commands
	)
	default String key1_script()
	{
		return "group1\nwait\npiety";
	}

	@ConfigItem(
		position = 11,
		keyName = "key2_script",
		name = "Key 2 Script",
		description = PkToolsConfig.commands
	)
	default String key2_script()
	{
		return "group2\nwait\nrigour";
	}

	@ConfigItem(
		position = 12,
		keyName = "key3_script",
		name = "Key 3 Script",
		description = PkToolsConfig.commands
	)
	default String key3_script()
	{
		return "group3\nwait\naugury";
	}

	@ConfigItem(
		position = 13,
		keyName = "key4_script",
		name = "Key 4 Script",
		description = PkToolsConfig.commands
	)
	default String key4_script()
	{
		return "group4\npiety\nspec\nclickenemy";
	}

	@ConfigItem(
		position = 14,
		keyName = "key5_script",
		name = "Key 5 Script",
		description = PkToolsConfig.commands
	)
	default String key5_script()
	{
		return "protectfrommagic";
	}

	@ConfigItem(
		position = 15,
		keyName = "key6_script",
		name = "Key 6 Script",
		description = PkToolsConfig.commands
	)
	default String key6_script()
	{
		return "protectfrommissiles";
	}

	@ConfigItem(
		position = 16,
		keyName = "key7_script",
		name = "Key 7 Script",
		description = PkToolsConfig.commands
	)
	default String key7_script()
	{
		return "protectfrommelee";
	}

	@ConfigItem(
		position = 17,
		keyName = "key8_script",
		name = "Key 8 Script",
		description = PkToolsConfig.commands
	)
	default String key8_script()
	{
		return "freeze";
	}

	@ConfigItem(
		keyName = "label2",
		name = "Prayer",
		description = "",
		position = 18
	)
	default Title label2()
	{
		return new Title();
	}

	@ConfigItem(
		position = 19,
		keyName = "autoPrayerSwitcher",
		name = "Auto Prayer Switcher",
		description = "Automatic Prayer Switching"
	)
	default boolean autoPrayerSwitcher()
	{
		return false;
	}

	@ConfigItem(
		position = 20,
		keyName = "autoPrayerSwitcherHotkey",
		name = "Prayer Switch Toggle Hotkey",
		description = "Hotkey to toggle the prayer switcher on/off"
	)
	default Keybind prayerKey()
	{
		return new Keybind(KeyEvent.VK_6, 0);
	}

	@ConfigItem(
		position = 21,
		keyName = "autoPrayerSwitcherEnabled",
		name = "Prayer Switcher Enabled",
		description = "",
		hidden = true
	)
	default boolean autoPrayerSwitcherEnabled()
	{
		return false;
	}

	@ConfigItem(
		position = 22,
		keyName = "prayerHelper",
		name = "Prayer Helper",
		description = "Draws icons to suggest proper prayer switches"
	)
	default boolean prayerHelper()
	{
		return true;
	}
}
