package net.runelite.client.plugins.animsmoothing;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimationSmoothingPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.animsmoothing.AnimationSmoothingPluginWrapper.class);

	public AnimationSmoothingPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
