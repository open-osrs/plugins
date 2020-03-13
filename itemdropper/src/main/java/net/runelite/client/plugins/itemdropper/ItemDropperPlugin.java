package net.runelite.client.plugins.itemdropper;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(
		name = "Item Dropper",
		description = "Automatically drop all specified items",
		tags = {"skilling", "notifications", "drop", "item"},
		enabledByDefault = false,
		type = PluginType.UTILITY
)
public class ItemDropperPlugin extends Plugin
{

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	ItemDropperConfig config;

	@Inject
	private ItemDropperHotkeyListener itemDropperHotkeyListener;

	@Inject
	private KeyManager keyManager;

	@Provides
	ItemDropperConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(ItemDropperConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		this.keyManager.registerKeyListener(this.itemDropperHotkeyListener);
	}

	@Override
	protected void shutDown() throws Exception
	{
		this.keyManager.unregisterKeyListener(this.itemDropperHotkeyListener);
	}
}