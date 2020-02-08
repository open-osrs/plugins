package net.runelite.client.plugins.raids.shortcuts;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Raids Shortcuts",
	description = "Highlights Raid Shortcuts",
	tags = {"boulder", "cox", "raids", "highlight"},
	type = PluginType.PVM
)
public class ShortcutPlugin extends Plugin
{
	private final List<TileObject> shortcut = new ArrayList<>();

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ShortcutOverlay overlay;

	List<TileObject> getShortcut()
	{
		return shortcut;
	}

	@Provides
	ShortcutConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ShortcutConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event)
	{
		WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, event.getGameObject().getLocalLocation());
		if (worldPoint == null)
		{
			return;
		}
		if ((event.getGameObject().getId() == 29740) || (event.getGameObject().getId() == 29736) || (event.getGameObject().getId() == 29738))
		{
			shortcut.add(event.getGameObject());
		}
	}

	@Subscribe
	private void onGameObjectDespawned(GameObjectDespawned event)
	{
		shortcut.remove(event.getGameObject());
	}

	@Subscribe
	private void onGameTick(GameTick tick)
	{
		shortcut.removeIf(object -> object.getCanvasLocation() == null);
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("shortcut"))
		{
			return;
		}
	}
}
