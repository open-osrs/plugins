package net.runelite.client.plugins.prayerflick;

import net.runelite.client.input.KeyListener;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.time.Instant;

public class PrayerFlickHotkeyListener implements KeyListener
{
	private static final int KEY_P = KeyEvent.VK_P;

	private Instant lastPress;

	@Inject
	private PrayerFlickPlugin plugin;

	@Override
	public void keyTyped(final KeyEvent e)
	{

	}

	@Override
	public void keyPressed(final KeyEvent e)
	{
		if (this.lastPress != null && Duration.between(this.lastPress, Instant.now()).getNano() > 1000)
		{
			this.lastPress = null;
		}
		if (e.isControlDown() && e.getKeyCode() == PrayerFlickHotkeyListener.KEY_P && this.lastPress == null)
		{
			if (this.plugin.prayFlickClick)
			{
				System.out.println("Setting to false");
				this.lastPress = Instant.now();
				this.plugin.prayFlickClick = false;
			}
			if (this.lastPress == null)
			{
				System.out.println("Setting to true");
				this.lastPress = Instant.now();
				this.plugin.prayFlickClick = true;
			}
		}
	}

	@Override
	public void keyReleased(final KeyEvent e)
	{

	}
}