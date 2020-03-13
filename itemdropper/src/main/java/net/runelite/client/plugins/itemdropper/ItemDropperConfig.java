package net.runelite.client.plugins.itemdropper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup("itemdropper")
public interface ItemDropperConfig extends Config
{

	@ConfigItem(
			name = "Items to drop",
			keyName = "itemsToDrop",
			description = "list of items to drop. (Comma separated)",
			position = 1
	)
	default String itemsToDrop()
	{
		return "iron ore, coal ore";
	}

	@ConfigItem(
			name = "Drop Keybind",
			keyName = "dropItemsKeybind",
			description = "The keybind to drop the items",
			position = 2
	)
	default Keybind dropItemsKeybind()
	{
		return new Keybind(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK);
	}

	@ConfigItem(
			name = "Click Delay",
			keyName = "clickDelay",
			description = "The keybind to drop the items",
			position = 3
	)
	default int clickDelay()
	{
		return 30;
	}
}