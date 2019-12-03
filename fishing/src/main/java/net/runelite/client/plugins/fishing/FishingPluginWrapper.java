package net.runelite.client.plugins.fishing;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FishingPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.fishing.FishingPluginWrapper.class);

	public FishingPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
