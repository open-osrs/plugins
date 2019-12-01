package net.runelite.client.plugins.ammo;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmmoPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.ammo.AmmoPluginWrapper.class);

	public AmmoPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
