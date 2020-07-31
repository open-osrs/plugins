package net.runelite.client.plugins.nmzhelper;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.util.Text;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.nmzhelper.Tasks.AbsorptionTask;
import net.runelite.client.plugins.nmzhelper.Tasks.OverloadTask;
import net.runelite.client.plugins.nmzhelper.Tasks.RockCakeTask;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "NMZ Helper",
	description = "An automation utility for NMZ",
	tags = {"combat", "potion", "overload", "absorption", "nmz", "nightmare", "zone", "helper"},
	enabledByDefault = false,
	type = PluginType.MINIGAME
)
public class NMZHelperPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private NMZHelperConfig config;

	@Provides
	NMZHelperConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(NMZHelperConfig.class);
	}

	public String status = "initializing...";
	private TaskSet tasks = new TaskSet();

	public static int rockCakeDelay = 0;

	@Override
	protected void startUp() throws Exception
	{
		tasks.clear();
		tasks.addAll(new OverloadTask(client, config), new AbsorptionTask(client, config), new RockCakeTask(client, config));
	}

	@Override
	protected void shutDown() throws Exception
	{
		tasks.clear();
	}

	@Subscribe
	private void onChatMessage(ChatMessage event)
	{
		String msg = Text.removeTags(event.getMessage()); //remove color

		if (event.getType() == ChatMessageType.SPAM
			&& msg.contains("You drink some of your overload potion."))
		{
			rockCakeDelay = 12;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		Task task = tasks.getValidTask();
		if (task != null)
		{
			status = task.getTaskDescription();
			task.onGameTick(event);
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		Task task = tasks.getValidTask();
		if (task != null)
		{
			status = task.getTaskDescription();
			task.onMenuOptionClicked(event);
		}
	}
}
