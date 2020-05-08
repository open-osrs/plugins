package net.runelite.client.plugins.itemuser;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup("itemuser")
public interface ItemUserConfig extends Config
{

	@ConfigItem(
		keyName = "itemId",
		name = "Item ID",
		description = "The ID of the item you want to use on the object.",
		position = 0
	)
	default int itemId()
	{
		return 536;
	}

	@ConfigItem(
		keyName = "objectId",
		name = "Object ID",
		description = "The ID of the object to use the item on.",
		position = 1
	)
	default int objectId()
	{
		return 13197;
	}

	@ConfigItem(
		keyName = "useItemsKeybind",
		name = "Use Keybind",
		description = "The keybind to use the items",
		position = 2
	)
	default Keybind useItemsKeybind()
	{
		return new Keybind(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK);
	}

	@ConfigItem(
		keyName = "clickDelay",
		name = "Click Delay",
		description = "The keybind to drop the items",
		position = 3
	)
	default int clickDelay()
	{
		return 30;
	}
}
