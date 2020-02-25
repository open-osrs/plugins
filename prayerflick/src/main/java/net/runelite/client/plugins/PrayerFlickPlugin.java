package net.runelite.client.plugins.prayerflick;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

@PluginDescriptor(
		name = "Prayer Flick",
		description = "Automatically Flicks Auto-Prayer and drinks Prayer potions (for training, not PvP)",
		tags = {"combat", "flicking", "overlay", "prayer"},
		enabledByDefault = false,
		type = PluginType.EXTERNAL
)
public class PrayerFlickPlugin extends Plugin
{
	private final Instant startOfLastTick = Instant.now();

	@Getter(AccessLevel.PACKAGE)
	private boolean prayersActive;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	public boolean prayFlickClick;

	@Inject
	private Client client;

	@Inject
	private KeyManager keyManager;

	@Inject
	private PrayerFlickHotkeyListener prayerHotkeyListener;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PrayerFlickOverlay flickOverlay;

	@Inject
	private EventBus eventBus;

	@Override
	protected void startUp()
	{
		this.eventBus.subscribe(GameTick.class, this, this::onGameTick);
		this.overlayManager.add(this.flickOverlay);
		this.keyManager.registerKeyListener(this.prayerHotkeyListener);
	}

	@Override
	protected void shutDown()
	{
		this.eventBus.unregister(this);
		this.overlayManager.remove(this.flickOverlay);
		this.keyManager.unregisterKeyListener(this.prayerHotkeyListener);
	}

	public void onGameTick(final GameTick tick)
	{
		this.prayersActive = this.isAnyPrayerActive();
	}

	double getTickProgress()
	{
		final long timeSinceLastTick = Duration.between(this.startOfLastTick, Instant.now()).toMillis();

		final float tickProgress = (timeSinceLastTick % 600) / 600f;
		return tickProgress * Math.PI;
	}

	private boolean isAnyPrayerActive()
	{
		for (final Prayer pray : Prayer.values())//Check if any prayers are active
		{
			if (this.client.isPrayerActive(pray))
			{
				return true;
			}
		}

		return false;
	}
}
