/*
 * Copyright (c) 2018, Magic fTail
 * Copyright (c) 2019, osrs-music-map <osrs-music-map@users.noreply.github.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.chatfilter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.Player;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.util.Text;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Chat Filter",
	enabledByDefault = false,
	description = "Censor user configurable words or patterns from chat",
	type = PluginType.MISCELLANEOUS
)
public class ChatFilterPlugin extends Plugin
{
	private static final Splitter NEWLINE_SPLITTER = Splitter
		.on("\n")
		.omitEmptyStrings()
		.trimResults();

	@VisibleForTesting
	static final String CENSOR_MESSAGE = "Hey, everyone, I just tried to say something very silly!";

	private final CharMatcher jagexPrintableCharMatcher = Text.JAGEX_PRINTABLE_CHAR_MATCHER;
	private final List<Pattern> filteredPatterns = new CopyOnWriteArrayList<>();
	private final List<Pattern> filteredNamePatterns = new ArrayList<>();

	@Inject
	private Client client;

	@Inject
	private ChatFilterConfig config;

	@Provides
	ChatFilterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ChatFilterConfig.class);
	}

	@Override
	protected void startUp()
	{
		updateFilteredPatterns();
		client.refreshChat();
	}

	@Override
	protected void shutDown()
	{
		filteredPatterns.clear();
		client.refreshChat();
	}

	@Subscribe
	void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		if (!"chatFilterCheck".equals(event.getEventName()))
		{
			return;
		}

		int[] intStack = client.getIntStack();
		int intStackSize = client.getIntStackSize();
		int messageType = intStack[intStackSize - 2];
		int messageId = intStack[intStackSize - 1];

		ChatMessageType chatMessageType = ChatMessageType.of(messageType);

		// Only filter public chat and private messages
		switch (chatMessageType)
		{
			case PUBLICCHAT:
			case MODCHAT:
			case AUTOTYPER:
			case PRIVATECHAT:
			case MODPRIVATECHAT:
			case FRIENDSCHAT:
			case GAMEMESSAGE:
				break;
			case LOGINLOGOUTNOTIFICATION:
				if (config.filterLogin())
				{
					// Block the message
					intStack[intStackSize - 3] = 0;
				}
				return;
			default:
				return;
		}

		MessageNode messageNode = client.getMessages().get(messageId);
		String name = messageNode.getName();

		if (client.getLocalPlayer() != null && client.getLocalPlayer().getName() != null && client.getLocalPlayer().getName().equals(messageNode.getName()) ||
			!config.filterFriends() && messageNode.isFromFriend() ||
			!config.filterClan() && messageNode.isFromClanMate())
		{
			return;
		}

		String[] stringStack = client.getStringStack();
		int stringStackSize = client.getStringStackSize();

		String message = stringStack[stringStackSize - 1];
		String censoredMessage = censorMessage(name, message);

		if (censoredMessage == null)
		{
			// Block the message
			intStack[intStackSize - 3] = 0;
		}
		else
		{
			// Replace the message
			stringStack[stringStackSize - 1] = censoredMessage;
		}
	}

	@Subscribe
	private void onOverheadTextChanged(OverheadTextChanged event)
	{
		if (!(event.getActor() instanceof Player) || event.getActor().getName() == null || !shouldFilterPlayerMessage(event.getActor().getName()))
		{
			return;
		}

		String message = censorMessage(event.getActor().getName(), event.getOverheadText());

		if (message == null)
		{
			message = " ";
		}

		event.getActor().setOverheadText(message);
	}

	boolean shouldFilterPlayerMessage(String playerName)
	{
		boolean isMessageFromSelf = playerName.equals(client.getLocalPlayer().getName());
		return !isMessageFromSelf &&
			(config.filterFriends() || !client.isFriended(playerName, false)) &&
			(config.filterClan() || !client.isClanMember(playerName));
	}

	String censorMessage(final String username, final String message)
	{
		String strippedMessage = jagexPrintableCharMatcher.retainFrom(message)
			.replace('\u00A0', ' ');

		if (shouldFilterByName(username))
		{
			switch (config.filterType())
			{
				case CENSOR_WORDS:
					return StringUtils.repeat('*', strippedMessage.length());
				case CENSOR_MESSAGE:
					return CENSOR_MESSAGE;
				case REMOVE_MESSAGE:
					return null;
			}
		}

		boolean filtered = false;
		for (Pattern pattern : filteredPatterns)
		{
			Matcher m = pattern.matcher(strippedMessage);

			StringBuffer sb = new StringBuffer();

			while (m.find())
			{
				switch (config.filterType())
				{
					case CENSOR_WORDS:
						m.appendReplacement(sb, StringUtils.repeat('*', m.group(0).length()));
						filtered = true;
						break;
					case CENSOR_MESSAGE:
						return CENSOR_MESSAGE;
					case REMOVE_MESSAGE:
						return null;
				}
			}
			m.appendTail(sb);

			strippedMessage = sb.toString();
		}

		return filtered ? strippedMessage : message;
	}

	void updateFilteredPatterns()
	{
		filteredPatterns.clear();
		filteredNamePatterns.clear();

		Text.fromCSV(config.filteredWords()).stream()
			.map(s -> Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE))
			.forEach(filteredPatterns::add);

		NEWLINE_SPLITTER.splitToList(config.filteredRegex()).stream()
			.map(ChatFilterPlugin::compilePattern)
			.filter(Objects::nonNull)
			.forEach(filteredPatterns::add);

		NEWLINE_SPLITTER.splitToList(config.filteredNames()).stream()
			.map(ChatFilterPlugin::compilePattern)
			.filter(Objects::nonNull)
			.forEach(filteredNamePatterns::add);
	}

	private static Pattern compilePattern(String pattern)
	{
		try
		{
			return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		}
		catch (PatternSyntaxException ex)
		{
			return null;
		}
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (!"chatfilter".equals(event.getGroup()))
		{
			return;
		}

		updateFilteredPatterns();

		//Refresh chat after config change to reflect current rules
		client.refreshChat();
	}

	@VisibleForTesting
	boolean shouldFilterByName(final String playerName)
	{
		String sanitizedName = Text.standardize(playerName);
		for (Pattern pattern : filteredNamePatterns)
		{
			Matcher m = pattern.matcher(sanitizedName);
			if (m.find())
			{
				return true;
			}
		}
		return false;
	}
}