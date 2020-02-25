package net.runelite.client.plugins.itemcombiner;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;

import javax.inject.Inject;

@PluginDescriptor(
		name = "Item Combiner",
		description = "Automatically uses items on another item",
		tags = {"skilling", "item", "object", "combiner"},
		enabledByDefault = false,
		type = PluginType.EXTERNAL
)
public class ItemCombinerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	ItemCombinerConfig config;

	@Inject
	private ItemCombinerHotkeyListener itemCombinerHotkeyListener;

	@Inject
	private KeyManager keyManager;

	@Provides
	ItemCombinerConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(ItemCombinerConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		this.keyManager.registerKeyListener(this.itemCombinerHotkeyListener);
	}

	@Override
	protected void shutDown() throws Exception
	{
		this.keyManager.unregisterKeyListener(this.itemCombinerHotkeyListener);
	}
}
