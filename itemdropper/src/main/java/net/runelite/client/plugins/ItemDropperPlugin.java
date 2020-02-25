package net.runelite.client.plugins.itemdropper;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;

import javax.inject.Inject;

@PluginDescriptor(
		name = "Item Dropper",
		description = "Automatically drop all specified items",
		tags = {"skilling", "notifications", "drop", "item"},
		enabledByDefault = false,
		type = PluginType.EXTERNAL
)
public class ItemDropperPlugin extends Plugin
{

	@Inject
	private Client client;

	@Inject
	private EventBus eventBus;

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
		this.eventBus.unregister(this);
		this.keyManager.unregisterKeyListener(this.itemDropperHotkeyListener);
	}
}