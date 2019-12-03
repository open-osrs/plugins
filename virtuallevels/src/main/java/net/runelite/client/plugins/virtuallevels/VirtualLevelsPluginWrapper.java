package net.runelite.client.plugins.virtuallevels;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualLevelsPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.virtuallevels.VirtualLevelsPluginWrapper.class);

	public VirtualLevelsPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
