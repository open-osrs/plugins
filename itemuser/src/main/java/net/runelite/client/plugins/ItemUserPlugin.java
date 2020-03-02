package net.runelite.client.plugins.itemuser;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@PluginDescriptor(
		name = "Item User",
		description = "Automatically uses items on an object",
		tags = {"skilling", "item", "object", "user"},
		enabledByDefault = false,
		type = PluginType.EXTERNAL
)
public class ItemUserPlugin extends Plugin
{

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	ItemUserConfig config;

	@Inject
	private ItemUserHotkeyListener itemUserHotkeyListener;

	@Inject
	private KeyManager keyManager;

	@Getter(AccessLevel.PACKAGE)
	private final List<GameObject> objectList = new ArrayList<>();

	@Provides
	ItemUserConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(ItemUserConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		this.keyManager.registerKeyListener(this.itemUserHotkeyListener);
	}

	@Override
	protected void shutDown() throws Exception
	{
		this.keyManager.unregisterKeyListener(this.itemUserHotkeyListener);
	}

	@Subscribe
	private void onGameObjectSpawned(final GameObjectSpawned event)
	{
		GameObject gameObject = event.getGameObject();
		if (gameObject.getId() == this.config.objectId())
		{
			this.objectList.add(gameObject);
		}
	}

	@Subscribe
	private void onGameObjectDespawned(final GameObjectDespawned event)
	{
		GameObject gameObject = event.getGameObject();
		if (gameObject.getId() == this.config.objectId())
		{
			this.objectList.remove(gameObject);
		}
	}

	@Subscribe
	private void onConfigChanged(final ConfigChanged event)
	{
		this.objectList.removeIf(object -> (object.getId() != this.config.objectId()));
	}
}
