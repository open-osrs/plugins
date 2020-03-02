package net.runelite.client.plugins.jadautoprayer;

import net.runelite.api.Point;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.VarClientInt;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class JadAutoPrayerOverlay extends Overlay
{
	private final Client client;
	private final JadAutoPrayerPlugin plugin;
	private final JadAutoPrayerConfig config;

	@Inject
	private JadAutoPrayerOverlay(final Client client, final JadAutoPrayerPlugin plugin, final JadAutoPrayerConfig config)
	{
		this.setPosition(OverlayPosition.BOTTOM_RIGHT);
		this.setPriority(OverlayPriority.HIGH);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(final Graphics2D graphics)
	{
		JadAttack attack = this.plugin.getAttack();

		if (attack == null)
			return null;

		if (this.config.autoSwitchPrayers())
			this.doAutoSwitchPrayers(attack);

		return null;
	}

	private void doAutoSwitchPrayers(JadAttack attack)
	{
		if (attack == null)
			return;

		final boolean PROTECT_RANGED = this.client.getVar(Prayer.PROTECT_FROM_MISSILES.getVarbit()) != 0;
		final boolean PROTECT_MAGIC = this.client.getVar(Prayer.PROTECT_FROM_MAGIC.getVarbit()) != 0;
		final boolean onPrayerTab = this.client.getVar(VarClientInt.INTERFACE_TAB) == 5;

		if (attack == JadAttack.MAGIC)
		{
			//if we are already praying properly or if we have autoSwitchPrayers disabled, we're done here
			if (!this.config.autoSwitchPrayers() || PROTECT_MAGIC)
				return;

			//swap to pray tab if necessary
			if (!onPrayerTab)
				InputHandler.sendKey(this.client.getCanvas(), this.config.prayerTabKey().getKey());

			try
			{
				Thread.sleep(60);
			} catch (final InterruptedException e)
			{
				//ignored
			}

			//get protect from magic widget
			final Widget PROTECT_FROM_MAGIC = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);

			//null check
			if (PROTECT_FROM_MAGIC == null)
				return;

			//get random point to click inside of the widget's bounds
			final Point p = InputHandler.getClickPoint(PROTECT_FROM_MAGIC.getBounds());

			//click the widget at the point
			InputHandler.leftClick(this.client, p);
		} else if (attack == JadAttack.RANGE)
		{
			//if we are already praying properly or if we have autoSwitchPrayers disabled, we're done here
			if (!this.config.autoSwitchPrayers() || PROTECT_RANGED)
				return;

			//swap to pray tab if necessary
			if (!onPrayerTab)
				InputHandler.sendKey(this.client.getCanvas(), this.config.prayerTabKey().getKey());

			//get protect from missiles widget
			final Widget PROTECT_FROM_RANGED = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES);

			//null check
			if (PROTECT_FROM_RANGED == null)
				return;

			//get random point to click inside of the widget's bounds
			final Point p = InputHandler.getClickPoint(PROTECT_FROM_RANGED.getBounds());

			//click the widget at the point
			InputHandler.leftClick(this.client, p);
		}
	}

}
