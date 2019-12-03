package net.runelite.client.plugins.woodcutting;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WoodcuttingPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.woodcutting.WoodcuttingPluginWrapper.class);

	public WoodcuttingPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
