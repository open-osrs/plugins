package net.runelite.client.plugins.chattranslation;

import com.google.inject.Provides;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.util.Text;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ChatboxInput;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Chat Translator",
	description = "Translates messages from one Language to another.",
	tags = {"translate", "language", "english", "spanish", "dutch", "french", "welsh", "german"},
	type = PluginType.MISCELLANEOUS
)
public class ChatTranslationPlugin extends Plugin
{
	private static final Object PUBLIC = new Object();
	private static final Object OPTION = new Object();
	private static final String TRANSLATE = "Translate";

	// TODO: Find out if "Remove friend" is correct here, aka if clan tab should have the Translate option
	private static final List<String> AFTER_OPTIONS = List.of("Message", "Add ignore", "Remove friend", "Kick");

	private final Set<String> playerNames = new HashSet<>();

	@Inject
	private Client client;

	@Inject
	private MenuManager menuManager;

	@Inject
	private ChatTranslationConfig config;

	@Inject
	private EventBus eventBus;

	@Inject
	private Translator translator;

	@Provides
	ChatTranslationConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ChatTranslationConfig.class);
	}

	@Override
	protected void startUp()
	{
		translator.setInLang(config.publicTargetLanguage());
		translator.setOutLang(config.playerTargetLanguage());

		if (config.playerChat())
		{
			eventBus.subscribe(ChatboxInput.class, this, this::onChatSent);
		}

		if (config.publicChat())
		{
			eventBus.subscribe(ChatMessage.class, PUBLIC, this::onChatMessage);
		}

		if (config.translateOptionVisible())
		{
			menuManager.addPlayerMenuItem(TRANSLATE);
			eventBus.subscribe(MenuOpened.class, OPTION, this::onMenuOpened);
			eventBus.subscribe(MenuOptionClicked.class, OPTION, this::onMenuOptionClicked);
		}

		for (String name : Text.fromCSV(config.playerNames().toLowerCase()))
		{
			playerNames.add(Text.toJagexName(name));
		}
	}

	@Override
	protected void shutDown()
	{
		eventBus.unregister(OPTION);
		eventBus.unregister(PUBLIC);
		eventBus.unregister(this);
		menuManager.removePlayerMenuItem(TRANSLATE);
		playerNames.clear();
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("chattranslation"))
		{
			return;
		}

		switch (event.getKey())
		{
			case "translateOptionVisible":
				if (config.translateOptionVisible())
				{
					menuManager.addPlayerMenuItem(TRANSLATE);
					eventBus.subscribe(MenuOpened.class, TRANSLATE, this::onMenuOpened);
					eventBus.subscribe(MenuOptionClicked.class, TRANSLATE, this::onMenuOptionClicked);
				}
				else
				{
					menuManager.removePlayerMenuItem(TRANSLATE);
					eventBus.unregister(TRANSLATE);
				}
				break;
			case "publicChat":
				if (config.publicChat())
				{
					eventBus.subscribe(ChatMessage.class, PUBLIC, this::onChatMessage);
				}
				else
				{
					eventBus.unregister(PUBLIC);
				}
				break;
			case "playerNames":
				playerNames.clear();
				for (String names : Text.fromCSV(config.playerNames().toLowerCase()))
				{
					playerNames.add(Text.toJagexName(names));
				}
				break;
			case "publicTargetLanguage":
				translator.setInLang(config.publicTargetLanguage());
				break;
			case "playerChat":
				if (config.playerChat())
				{
					eventBus.subscribe(ChatboxInput.class, this, this::onChatSent);
				}
				else
				{
					eventBus.unregister(this);
				}
				break;
			case "playerTargetLanguage":
				translator.setOutLang(config.playerTargetLanguage());
				break;
		}
	}

	private void onMenuOpened(MenuOpened event)
	{
		MenuEntry[] entries = event.getMenuEntries();

		for (int i = 0; i < event.getMenuEntries().length; i++)
		{
			if (!AFTER_OPTIONS.contains(entries[i].getOption()))
			{
				continue;
			}

			final MenuEntry entry = entries[i].clone();
			entry.setOption(TRANSLATE);
			entry.setOpcode(MenuOpcode.RUNELITE.getId());

			event.setMenuEntries(ArrayUtils.insert(i, entries, entry));
			event.setModified();
			return;
		}
	}

	private void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getOpcode() != MenuOpcode.RUNELITE.getId() ||
			!event.getOption().equals(TRANSLATE))
		{
			return;
		}

		String name =
			Text.toJagexName(
				Text.removeTags(event.getTarget(), true)
					.toLowerCase()
			);

		playerNames.add(name);

		config.playerNames(Text.toCSV(playerNames));
	}

	@Subscribe
	private void onChatMessage(ChatMessage chatMessage)
	{
		if (client.getGameState() != GameState.LOADING && client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		switch (chatMessage.getType())
		{
			case PUBLICCHAT:
			case MODCHAT:
			case FRIENDSCHAT:
				break;
			default:
				return;
		}

		if (!playerNames.contains(Text.toJagexName(Text.removeTags(chatMessage.getName().toLowerCase()))))
		{
			return;
		}

		translator.translateIncoming(chatMessage);
	}

	private void onChatSent(ChatboxInput input)
	{
		if (translator.isSending())
		{
			return;
		}

		input.setStop();
		translator.translateOutgoing(input);
	}
}