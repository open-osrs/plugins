package net.runelite.client.plugins.nmzhelper;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.nmzhelper.Tasks.*;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Singleton
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
	/*
		varbits
		absorptions - 3954 (doses in storage)
		overloads - 3953 (doses in storage)
	 */

	@Inject
	private Client client;

	@Inject
	private NMZHelperConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NMZHelperOverlay overlay;

	@Inject
	private static ChatMessageManager chatMessageManager;

	static boolean pluginStarted;

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
		pluginStarted = false;
		overlayManager.add(overlay);
		status = "initializing...";
		tasks.clear();
		tasks.addAll(client, config,
			//loginTask,
			new SpecialAttackTask(),
			new OverloadTask(),
			new AbsorptionTask(),
			new RockCakeTask(),
			new PowerSurgeTask(),

			//--------------------------
			new SearchRewardsChestTask(),
			new BenefitsTabTask(),
			new BuyAbsorptionsTask(),
			new BuyOverloadsTask(),
			//--------------------------

			new WithdrawAbsorptionTask(),
			new WithdrawOverloadTask(),
			new OpenAbsorptionsBarrelTask(),
			new OpenOverloadsBarrel(),

			new DominicDreamTask(),
			new DominicDialogue1Task(),
			new DominicDialogue2Task(),

			new ContinueDialogTask(),

			new DrinkPotionTask(),
			new AcceptDreamTask()
		);
	}

	@Override
	protected void shutDown() throws Exception
	{
		pluginStarted = false;
		overlayManager.remove(overlay);
		tasks.clear();
	}

	@Subscribe
	public void onConfigButtonClicked(ConfigButtonClicked event)
	{
		if (!event.getGroup().equals("nmzhelper"))
		{
			return;
		}

		if (event.getKey().equals("startButton"))
		{
			pluginStarted = true;
		}
		else if (event.getKey().equals("stopButton"))
		{
			pluginStarted = false;
		}
	}

	@Subscribe
	private void onChatMessage(ChatMessage event)
	{
		if (!pluginStarted)
		{
			return;
		}

		String msg = Text.removeTags(event.getMessage()); //remove color

		switch (event.getType())
		{
			case SPAM:
				if (msg.contains("You drink some of your overload potion."))
				{
					rockCakeDelay = 12;
				}
				break;
			case GAMEMESSAGE:
				if (msg.contains("This barrel is empty.")
					|| msg.contains("There is no ammo left in your quiver.")
					|| msg.contains("Your blowpipe has run out of scales and darts.")
					|| msg.contains("Your blowpipe has run out of darts.")
					|| msg.contains("Your blowpipe needs to be charged with Zulrah's scales."))
				{
					stopPlugin("Received game message: " + msg);
				}
				break;
			default:
				break;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!pluginStarted)
		{
			return;
		}

		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		//if we don't have a rock cake, return...may need to stop the plugin but this is causing it to stop
		//	randomly for some reason
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

		if (inventoryWidget == null)
			return;

		if (inventoryWidget.getWidgetItems()
			.stream().noneMatch(item -> item.getId() == ItemID.DWARVEN_ROCK_CAKE_7510))
		{
			//pluginStarted = false;
			sendGameMessage("Rock cake not found...");
			return;
		}

		if (client.getVarbitValue(3948) < 26 && !MiscUtils.isInNightmareZone(client))
		{
			stopPlugin("You need to put money in the coffer!");
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
	public void onGameStateChanged(GameStateChanged event)
	{
		if (!pluginStarted)
			return;

		if (!config.autoRelog())
			return;

		if (client.getGameState() != GameState.LOGIN_SCREEN)
			return;

		client.setUsername(config.email());
		client.setPassword(config.password());
		client.setGameState(GameState.LOGGING_IN);
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (!pluginStarted)
		{
			return;
		}

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

	private static void sendGameMessage(String message)
	{
		chatMessageManager
			.queue(QueuedMessage.builder()
				.type(ChatMessageType.CONSOLE)
				.runeLiteFormattedMessage(
					new ChatMessageBuilder()
					.append(ChatColorType.HIGHLIGHT)
					.append(message)
					.build())
				.build());
	}

	public static void stopPlugin()
	{
		stopPlugin("");
	}

	public static void stopPlugin(String reason)
	{
		pluginStarted = false;

		if (reason != null && !reason.isEmpty())
			sendGameMessage("NMZHelper Stopped: " + reason);
	}
}
