package net.runelite.client.plugins.warindicators;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarIndicatorPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.warindicators.WarIndicatorPluginWrapper.class);

	public WarIndicatorPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
