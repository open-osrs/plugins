package net.runelite.client.plugins.itemcombiner;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(
	name = "Item Combiner",
	description = "Automatically uses items on another item",
	tags = {"skilling", "item", "object", "combiner"},
	enabledByDefault = false,
	type = PluginType.SKILLING
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

	MenuEntry entry;

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

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (entry != null)
		{
			event.setMenuEntry(entry);
		}

		entry = null;
	}
}
