package net.runelite.client.plugins.itemskeptondeath;

import org.pf4j.PluginWrapper;

public class ItemsKeptOnDeathPlugin extends org.pf4j.Plugin
{
	public ItemsKeptOnDeathPlugin(PluginWrapper wrapper)
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
