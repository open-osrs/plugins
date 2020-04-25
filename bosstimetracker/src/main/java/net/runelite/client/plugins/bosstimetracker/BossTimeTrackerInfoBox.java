package net.runelite.client.plugins.bosstimetracker;

import java.awt.Color;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.ImageUtil;

public class BossTimeTrackerInfoBox extends InfoBox
{
	private BossTimeTrackerPlugin plugin;

	private Client client;

	public BossTimeTrackerInfoBox(Client client, BossTimeTrackerPlugin plugin)
	{
		super(ImageUtil.getResourceStreamFromClass(BossTimeTrackerPlugin.class, "nightmare.png"), plugin);
		this.plugin = plugin;
		this.client = client;
	}

	public String getText()
	{
		if (this.plugin.phase_splits[0] != -1)
		{
			return ntpib(this.plugin.phase_splits[0]);
		}
		return ntpib(this.client.getTickCount() - this.plugin.fight_timer);
	}

	public Color getTextColor()
	{
		return (this.plugin.phase_splits[0] == -1) ? Color.WHITE : Color.GREEN;
	}

	public String getTooltip()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Elapsed nightmare time: ");
		if (this.plugin.phase_splits[0] != -1)
		{
			builder.append(ntpib(this.plugin.phase_splits[0]));
		}
		else
		{
			builder.append(ntpib(this.client.getTickCount() - this.plugin.fight_timer));
		}
		if (this.plugin.phase_splits[1] != -1)
		{
			builder.append("</br>First phase: ");
			builder.append(ntpib(this.plugin.phase_splits[1]));
		}
		if (this.plugin.phase_splits[2] != -1)
		{
			builder.append("</br>Second phase: ");
			builder.append(ntpib(this.plugin.phase_splits[2]));
		}
		if (this.plugin.phase_splits[3] != -1)
		{
			builder.append("</br>Third phase: ");
			builder.append(ntpib(this.plugin.phase_splits[3]));
		}
		return builder.toString();
	}

	private String ntpib(int ticks)
	{
		int m = ticks / 100;
		int s = (ticks - m * 100) * 6 / 10;
		return String.valueOf(m) + ((s < 10) ? ":0" : ":") + s;
	}
}