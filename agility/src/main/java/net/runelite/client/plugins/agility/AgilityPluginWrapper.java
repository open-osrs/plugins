package net.runelite.client.plugins.agility;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgilityPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.agility.AgilityPluginWrapper.class);

	public AgilityPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
