package net.runelite.client.plugins.attackstyles;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttackStylesPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.attackstyles.AttackStylesPluginWrapper.class);

	public AttackStylesPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
