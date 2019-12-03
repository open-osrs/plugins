package net.runelite.client.plugins.thieving;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThievingPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.thieving.ThievingPluginWrapper.class);

	public ThievingPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
