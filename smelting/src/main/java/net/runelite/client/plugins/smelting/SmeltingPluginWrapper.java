package net.runelite.client.plugins.smelting;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmeltingPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.smelting.SmeltingPluginWrapper.class);

	public SmeltingPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
