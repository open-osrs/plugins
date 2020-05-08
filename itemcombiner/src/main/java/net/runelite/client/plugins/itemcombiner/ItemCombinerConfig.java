package net.runelite.client.plugins.itemcombiner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup("itemcombiner")
public interface ItemCombinerConfig extends Config
{

	@ConfigItem(
		keyName = "itemId",
		name = "Item ID",
		description = "The ID of the first item.",
		position = 0
	)
	default int itemId()
	{
		return 821;
	}

	@ConfigItem(
		keyName = "itemId2",
		name = "Second Item ID",
		description = "The ID of the second item.",
		position = 1
	)
	default int itemId2()
	{
		return 314;
	}

	@ConfigItem(
		keyName = "useItemsKeybind",
		name = "Use Keybind",
		description = "The keybind to use the items",
		position = 2
	)
	default Keybind useItemsKeybind()
	{
		return new Keybind(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK);
	}

	@ConfigItem(
		keyName = "iterations",
		name = "Iterations",
		description = "The amount of times to perform the action",
		position = 3
	)
	default int iterations()
	{
		return 30;
	}

	@ConfigItem(
		keyName = "clickDelay",
		name = "Click Delay",
		description = "The keybind to drop the items",
		position = 4
	)
	default int clickDelay()
	{
		return 30;
	}
}
