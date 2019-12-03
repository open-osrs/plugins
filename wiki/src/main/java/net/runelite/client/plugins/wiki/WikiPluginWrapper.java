package net.runelite.client.plugins.wiki;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikiPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.wiki.WikiPluginWrapper.class);

	public WikiPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
