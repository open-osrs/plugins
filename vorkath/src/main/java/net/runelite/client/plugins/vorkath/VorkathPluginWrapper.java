package net.runelite.client.plugins.vorkath;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VorkathPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.vorkath.VorkathPluginWrapper.class);

	public VorkathPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
