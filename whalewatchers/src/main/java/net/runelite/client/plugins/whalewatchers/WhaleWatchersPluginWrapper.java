package net.runelite.client.plugins.whalewatchers;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhaleWatchersPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.whalewatchers.WhaleWatchersPluginWrapper.class);

	public WhaleWatchersPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
