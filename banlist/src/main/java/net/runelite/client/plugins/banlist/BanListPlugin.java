/*
 * Copyright (c) 2019, xperiaclash <https://github.com/xperiaclash>
 * Copyright (c) 2019, ganom <https://github.com/Ganom>
 * Copyright (c) 2019, gazivodag <https://github.com/gazivodag>
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
package net.runelite.client.plugins.banlist;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Provides;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.FriendsChatMember;
import net.runelite.api.GameState;
import net.runelite.api.events.FriendsChatMemberJoined;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetHiddenChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import static net.runelite.api.widgets.WidgetID.PLAYER_TRADE_SCREEN_GROUP_ID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
	name = "Ban List",
	enabledByDefault = false,
	description = "Displays warning in chat when you join a" +
		"clan chat/new member join your clan chat and he is in a WDR/RuneWatch/Manual List",
	tags = {"PVM", "WDR", "RuneWatch"},
	type = PluginType.MISCELLANEOUS
)
public class BanListPlugin extends Plugin
{
	private final Set<String> wdrScamSet = new HashSet<>();
	private final Set<String> runeWatchSet = new HashSet<>();
	private final Set<String> manualBans = new HashSet<>();

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private BanListConfig config;

	@Inject
	private ChatMessageManager chatMessageManager;

	private String tobNames = "";

	@Provides
	BanListConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BanListConfig.class);
	}

	@Override
	protected void startUp()
	{
		manualBans.addAll(Text.fromCSV(Text.standardize(config.getBannedPlayers())));

		fetchFromWebsites();
	}

	@Override
	protected void shutDown()
	{
		wdrScamSet.clear();
		runeWatchSet.clear();
		manualBans.clear();
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("banlist") && event.getKey().equals("bannedPlayers"))
		{
			manualBans.clear();

			String newValue = event.getNewValue();

			manualBans.addAll(Text.fromCSV(Text.standardize(newValue)));
		}
	}

	/**
	 * Event to keep making sure player names are highlighted red in clan chat, since the red name goes away frequently
	 */
	@Subscribe
	private void onWidgetHiddenChanged(WidgetHiddenChanged widgetHiddenChanged)
	{
		if (client.getGameState() != GameState.LOGGED_IN
			|| client.getWidget(WidgetInfo.LOGIN_CLICK_TO_PLAY_SCREEN) != null
			|| client.getViewportWidget() == null
			|| client.getWidget(WidgetInfo.FRIENDS_CHAT) == null
			|| !config.highlightInClan())
		{
			return;
		}

		clientThread.invokeLater(() ->
		{
			if (!client.getWidget(WidgetInfo.FRIENDS_CHAT).isHidden())
			{
				highlightRedInCC();
			}
		});
	}

	@Subscribe
	private void onFriendsChatMemberJoined(FriendsChatMemberJoined event)
	{
		FriendsChatMember member = event.getMember();
		String memberUsername = Text.standardize(member.getName().toLowerCase());

		ListType scamList = checkScamList(memberUsername);

		if (scamList != null)
		{
			sendWarning(memberUsername, scamList);
			if (config.highlightInClan())
			{
				highlightRedInCC();
			}
		}
	}

	/**
	 * If a trade window is opened and the person trading us is on the list, modify "trading with"
	 */
	@Subscribe
	private void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		if (config.highlightInTrade() && widgetLoaded.getGroupId() == PLAYER_TRADE_SCREEN_GROUP_ID)
		{ //if trading window was loaded
			clientThread.invokeLater(() ->
			{
				Widget tradingWith = client.getWidget(WidgetInfo.TRADING_WITH);

				String name = tradingWith.getText().replaceAll("Trading With: ", "").toLowerCase();
				if (checkScamList(name) != null)
				{
					tradingWith.setText(tradingWith.getText().replaceAll(name, "<col=ff0000>" + name + " (Scammer)" + "</col>"));
				}
			});
		}
	}

	@Subscribe
	private void onGameTick(GameTick event)
	{
		final Widget raidingParty = client.getWidget(WidgetInfo.THEATRE_OF_BLOOD_RAIDING_PARTY);
		if (raidingParty == null)
		{
			return;
		}

		String allNames = raidingParty.getText();

		if (allNames.equalsIgnoreCase(tobNames))
		{
			return;
		}

		tobNames = allNames;

		String[] split = allNames.split("<br>");

		for (String name : split)
		{
			if (!name.equals("-"))
			{
				String stdName = Text.standardize(name);

				ListType scamList = checkScamList(stdName);

				if (scamList != null)
				{
					sendWarning(name, scamList);
				}
			}
		}
	}

	/**
	 * Compares player name to everything in the ban lists
	 */
	private ListType checkScamList(String nameToBeChecked)
	{
		if (config.enableWDRScam() && wdrScamSet.contains(nameToBeChecked))
		{
			return ListType.WEDORAIDSSCAM_LIST;
		}

		if (config.enableRuneWatch() && runeWatchSet.contains(nameToBeChecked))
		{
			return ListType.RUNEWATCH_LIST;
		}

		if (manualBans.contains(nameToBeChecked))
		{
			return ListType.MANUAL_LIST;
		}

		return null;
	}

	/**
	 * Sends a warning to our player, notifying them that a player is on a ban list
	 */
	private void sendWarning(String playerName, ListType listType)
	{
		switch (listType)
		{
			case WEDORAIDSSCAM_LIST:
				final String wdr__scam_message = new ChatMessageBuilder()
					.append(ChatColorType.HIGHLIGHT)
					.append("Warning! " + playerName + " is on WeDoRaids' scammer list!")
					.build();

				chatMessageManager.queue(
					QueuedMessage.builder()
						.type(ChatMessageType.CONSOLE)
						.runeLiteFormattedMessage(wdr__scam_message)
						.build());
				break;

			case RUNEWATCH_LIST:
				final String rw_message = new ChatMessageBuilder()
					.append(ChatColorType.HIGHLIGHT)
					.append("Warning! " + playerName + " is on the Runewatch's potential scammer list!")
					.build();

				chatMessageManager.queue(
					QueuedMessage.builder()
						.type(ChatMessageType.CONSOLE)
						.runeLiteFormattedMessage(rw_message)
						.build());
				break;
			case MANUAL_LIST:
				final String manual_message = new ChatMessageBuilder()
					.append(ChatColorType.HIGHLIGHT)
					.append("Warning! " + playerName + " is on your manual scammer list!")
					.build();

				chatMessageManager.queue(
					QueuedMessage.builder()
						.type(ChatMessageType.CONSOLE)
						.runeLiteFormattedMessage(manual_message)
						.build());
				break;
		}
	}

	/**
	 * Pulls data from ThatGamerBlue's rehost of the RW and WDR mixed banlist to build a list of blacklisted usernames
	 * We use the rehost to avoid hammering the RuneWatch servers
	 */
	private void fetchFromWebsites()
	{
		Request request = new Request.Builder()
			.url("https://thatgamerblue.com/runewatch.json")
			.build();

		RuneLiteAPI.CLIENT.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e)
			{
			}

			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response)
			{
				try
				{
					wdrScamSet.clear();
					runeWatchSet.clear();
					Gson gson = new Gson();
					//@formatter:off
					List<MixedListCase> cases = gson.fromJson(response.body().string(), new TypeToken<List<MixedListCase>>() {}.getType());
					//@formatter:on
					for (MixedListCase aCase : cases)
					{
						switch (aCase.getSource())
						{
							case "RW":
								runeWatchSet.add(aCase.getRsn());
								break;
							case "WDR":
								wdrScamSet.add(aCase.getRsn());
								break;
							default:
								log.warn("Unknown case source {}", aCase.getSource());
								break;
						}
					}
				}
				catch (Exception e)
				{
					log.error("Error parsing json", e);
				}
			}
		});
	}

	/**
	 * Iterates through the clan chat list widget and checks if a string (name) is on any of the ban lists to highlight them red.
	 */
	private void highlightRedInCC()
	{
		clientThread.invokeLater(() ->
		{
			Widget widget = client.getWidget(WidgetInfo.FRIENDS_CHAT_LIST);
			for (Widget widgetChild : widget.getDynamicChildren())
			{
				String text = widgetChild.getText(), lc = text.toLowerCase();

				if (checkScamList(lc) != null)
				{
					widgetChild.setText("<col=ff0000>" + text + "</col>");
				}
			}
		});
	}
}
