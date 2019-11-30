package net.runelite.client.plugins.gpu;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GpuPluginWrapper extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(net.runelite.client.plugins.gpu.GpuPluginWrapper.class);

	public GpuPluginWrapper(PluginWrapper wrapper)
	{
		super(wrapper);
	}
}
