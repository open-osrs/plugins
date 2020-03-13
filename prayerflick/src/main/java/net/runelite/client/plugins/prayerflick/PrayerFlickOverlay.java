package net.runelite.client.plugins.prayerflick;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class PrayerFlickOverlay extends Overlay
{
	private final Client client;
	private final PrayerFlickPlugin plugin;

	private boolean enabled;
	private long time;
	private long initiate;

	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 25, TimeUnit.SECONDS, queue,
			new ThreadPoolExecutor.DiscardPolicy());

	@Inject
	private PrayerFlickOverlay(final Client client, final PrayerFlickPlugin plugin)
	{
		this.setPosition(OverlayPosition.DYNAMIC);
		this.setLayer(OverlayLayer.ALWAYS_ON_TOP);
		this.setPriority(OverlayPriority.HIGHEST);
		this.client = client;
		this.plugin = plugin;
	}

	@Override
	public Dimension render(final Graphics2D graphics)
	{
		executor.submit(() ->
		{
			try
			{

				final Widget prayerOrb = this.client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);

				if (prayerOrb == null)
					return;

				final Rectangle2D bounds = prayerOrb.getBounds().getBounds2D();

				if (bounds.getX() <= 0)
					return;

				final int orbInnerX = (int) (bounds.getX() + 24);//x pos of the inside of the prayer orb
				final int orbInnerY = (int) (bounds.getY() - 1);//y pos of the inside of the prayer orb

				final double t = this.plugin.getTickProgress();

				if (bounds.getX() <= 0)
					return;

				if (this.plugin.prayFlickClick)
				{
					//check if minimized or client screen location is null
					if (this.client.getCanvas().getLocationOnScreen() == null)
						return;

					//if any prayer is active
					if (!this.plugin.isPrayersActive())
					{
						if (System.currentTimeMillis() - this.initiate >= 900)
						{
							InputHandler.leftClick(this.client, orbInnerX + 15, orbInnerY + 15);
							this.initiate = System.currentTimeMillis();
						}
					} else
					{
						this.initiate = System.currentTimeMillis();

						if (!this.enabled && t < 2.5 && t > 2.3)
						{
							this.time = System.currentTimeMillis();
							InputHandler.leftClick(this.client, orbInnerX + 15, orbInnerY + 15);
							this.enabled = true;
						}
						if (this.enabled && System.currentTimeMillis() - this.time > 30)
						{
							InputHandler.leftClick(this.client, orbInnerX + 15, orbInnerY + 15);
							this.enabled = false;
						}

					}
				}
			} catch (final Exception e)
			{
				System.out.println(e.getMessage());
			}
		});

		final Widget prayerOrb = this.client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);

		if (prayerOrb == null)
			return null;

		final Rectangle2D bounds = prayerOrb.getBounds().getBounds2D();

		if (bounds.getX() <= 0)
			return null;

		//Purposefully using height twice here as the bounds of the prayer orb includes the number sticking out the side
		final int orbInnerHeight = (int) bounds.getHeight();

		final int orbInnerX = (int) (bounds.getX() + 24);//x pos of the inside of the prayer orb
		final int orbInnerY = (int) (bounds.getY() - 1);//y pos of the inside of the prayer orb

		if (bounds.getX() <= 0)
			return null;

		if (this.plugin.prayFlickClick)
		{
			//check if minimized or client screen location is null
			if (this.client.getCanvas().getLocationOnScreen() == null)
				return null;

			graphics.setColor(this.enabled ? Color.RED : Color.GREEN);
			graphics.drawRect(orbInnerX, orbInnerY, orbInnerHeight, orbInnerHeight);

			return new Dimension((int) bounds.getWidth(), (int) bounds.getHeight());
		}

		return null;
	}
}
