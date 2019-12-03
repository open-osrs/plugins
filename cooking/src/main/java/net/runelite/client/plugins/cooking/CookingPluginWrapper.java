package net.runelite.client.plugins.cooking;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CookingPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.cooking.CookingPluginWrapper.class);

	public CookingPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
