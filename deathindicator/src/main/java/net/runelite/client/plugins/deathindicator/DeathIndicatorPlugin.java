package net.runelite.client.plugins.deathindicator;

import org.pf4j.PluginWrapper;

public class DeathIndicatorPlugin extends org.pf4j.Plugin
{
	public DeathIndicatorPlugin(PluginWrapper wrapper)
	{
		super(wrapper);
	}

	@Override
	public void start()
	{
		this.getWrapper().getPluginManager().stopPlugin(this.getWrapper().getPluginId());
		this.getWrapper().getPluginManager().disablePlugin(this.getWrapper().getPluginId());
	}
}
