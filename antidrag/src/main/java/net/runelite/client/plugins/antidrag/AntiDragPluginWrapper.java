package net.runelite.client.plugins.antidrag;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AntiDragPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.antidrag.AntiDragPluginWrapper.class);

	public AntiDragPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
