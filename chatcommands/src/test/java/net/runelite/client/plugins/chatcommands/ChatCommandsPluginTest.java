/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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
package net.runelite.client.plugins.chatcommands;

import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import static net.runelite.api.ChatMessageType.FRIENDSCHATNOTIFICATION;
import static net.runelite.api.ChatMessageType.GAMEMESSAGE;
import static net.runelite.api.ChatMessageType.TRADE;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ChatColorConfig;
import net.runelite.client.config.ConfigManager;
import static net.runelite.api.widgets.WidgetID.ADVENTURE_LOG_ID;
import static net.runelite.api.widgets.WidgetID.COUNTERS_LOG_GROUP_ID;
import net.runelite.client.config.OpenOSRSConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ChatCommandsPluginTest
{
	private static final String PLAYER_NAME = "Adam";
	
	@Mock
	@Bind
	Client client;

	@Mock
	@Bind
	ConfigManager configManager;

	@Mock
	@Bind
	ScheduledExecutorService scheduledExecutorService;

	@Mock
	@Bind
	ChatColorConfig chatColorConfig;

	@Mock
	@Bind
	ChatCommandsConfig chatCommandsConfig;

	@Mock
	@Bind
	private OpenOSRSConfig openOSRSConfig;

	@Inject
	ChatCommandsPlugin chatCommandsPlugin;

	@Before
	public void before()
	{
		Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
		
		when(client.getUsername()).thenReturn(PLAYER_NAME);
	}

	@Test
	public void testCorporealBeastKill()
	{
		ChatMessage chatMessageEvent = new ChatMessage(null, GAMEMESSAGE, "", "Your Corporeal Beast kill count is: <col=ff0000>4</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessageEvent);

		verify(configManager).setConfiguration("killcount.adam", "corporeal beast", 4);
	}

	@Test
	public void testTheatreOfBlood()
	{
		ChatMessage chatMessageEvent = new ChatMessage(null, GAMEMESSAGE, "", "Your completed Theatre of Blood count is: <col=ff0000>73</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessageEvent);

		verify(configManager).setConfiguration("killcount.adam", "theatre of blood", 73);
	}

	@Test
	public void testWintertodt()
	{
		ChatMessage chatMessageEvent = new ChatMessage(null, GAMEMESSAGE, "", "Your subdued Wintertodt count is: <col=ff0000>4</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessageEvent);

		verify(configManager).setConfiguration("killcount.adam", "wintertodt", 4);
	}

	@Test
	public void testKreearra()
	{
		ChatMessage chatMessageEvent = new ChatMessage(null, GAMEMESSAGE, "", "Your Kree'arra kill count is: <col=ff0000>4</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessageEvent);

		verify(configManager).setConfiguration("killcount.adam", "kree'arra", 4);
	}

	@Test
	public void testBarrows()
	{
		ChatMessage chatMessageEvent = new ChatMessage(null, GAMEMESSAGE, "", "Your Barrows chest count is: <col=ff0000>277</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessageEvent);

		verify(configManager).setConfiguration("killcount.adam", "barrows chests", 277);
	}

	@Test
	public void testHerbiboar()
	{
		ChatMessage chatMessageEvent = new ChatMessage(null, GAMEMESSAGE, "", "Your herbiboar harvest count is: <col=ff0000>4091</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessageEvent);

		verify(configManager).setConfiguration("killcount.adam", "herbiboar", 4091);
	}

	@Test
	public void testGauntlet()
	{
		ChatMessage gauntletMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your Gauntlet completion count is: <col=ff0000>123</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(gauntletMessage);

		verify(configManager).setConfiguration("killcount.adam", "gauntlet", 123);
	}

	@Test
	public void testCorruptedGauntlet()
	{
		ChatMessage corruptedGauntletMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your Corrupted Gauntlet completion count is: <col=ff0000>4729</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(corruptedGauntletMessage);

		verify(configManager).setConfiguration("killcount.adam", "corrupted gauntlet", 4729);
	}

	@Test
	public void testPersonalBest()
	{
		final String FIGHT_DURATION = "Fight duration: <col=ff0000>2:06</col>. Personal best: 1:19.";

		// This sets lastBoss
		ChatMessage chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your Kree'arra kill count is: <col=ff0000>4</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		chatMessage = new ChatMessage(null, GAMEMESSAGE, "", FIGHT_DURATION, null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("kree'arra"), eq(79));
	}

	@Test
	public void testPersonalBestNoTrailingPeriod()
	{
		final String FIGHT_DURATION = "Fight duration: <col=ff0000>0:59</col>. Personal best: 0:55";

		// This sets lastBoss
		ChatMessage chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your Zulrah kill count is: <col=ff0000>4</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		chatMessage = new ChatMessage(null, GAMEMESSAGE, "", FIGHT_DURATION, null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("zulrah"), eq(55));
	}

	@Test
	public void testNewPersonalBest()
	{
		final String NEW_PB = "Fight duration: <col=ff0000>3:01</col> (new personal best).";

		// This sets lastBoss
		ChatMessage chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your Kree'arra kill count is: <col=ff0000>4</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		chatMessage = new ChatMessage(null, GAMEMESSAGE, "", NEW_PB, null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("kree'arra"), eq(181));
	}

	@Test
	public void testDuelArenaWin()
	{
		ChatMessage chatMessageEvent = new ChatMessage(null, TRADE, "", "You won! You have now won 27 duels.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessageEvent);

		verify(configManager).setConfiguration("killcount.adam", "duel arena wins", 27);
		verify(configManager).setConfiguration("killcount.adam", "duel arena win streak", 1);
	}

	@Test
	public void testDuelArenaWin2()
	{
		ChatMessage chatMessageEvent = new ChatMessage(null, TRADE, "", "You were defeated! You have won 22 duels.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessageEvent);

		verify(configManager).setConfiguration("killcount.adam", "duel arena wins", 22);
	}

	@Test
	public void testDuelArenaLose()
	{
		ChatMessage chatMessageEvent = new ChatMessage(null, TRADE, "", "You have now lost 999 duels.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessageEvent);

		verify(configManager).setConfiguration("killcount.adam", "duel arena losses", 999);
	}

	@Test
	public void testAgilityLap()
	{
		final String NEW_PB = "Lap duration: <col=ff0000>1:01</col> (new personal best).";

		// This sets lastBoss
		ChatMessage chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your Prifddinas Agility Course lap count is: <col=ff0000>2</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		chatMessage = new ChatMessage(null, GAMEMESSAGE, "", NEW_PB, null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("prifddinas agility course"), eq(61));
		verify(configManager).setConfiguration(eq("killcount.adam"), eq("prifddinas agility course"), eq(2));
	}

	@Test
	public void testZukNewPb()
	{
		ChatMessage chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your TzKal-Zuk kill count is: <col=ff0000>2</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Duration: <col=ff0000>104:31</col> (new personal best)", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("tzkal-zuk"), eq(104 * 60 + 31));
		verify(configManager).setConfiguration(eq("killcount.adam"), eq("tzkal-zuk"), eq(2));
	}

	@Test
	public void testZukKill()
	{
		ChatMessage chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your TzKal-Zuk kill count is: <col=ff0000>3</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Duration: <col=ff0000>172:18</col>. Personal best: 134:52", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("tzkal-zuk"), eq(134 * 60 + 52));
		verify(configManager).setConfiguration(eq("killcount.adam"), eq("tzkal-zuk"), eq(3));
	}

	@Test
	public void testGgNewPb()
	{
		ChatMessage chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Fight duration: <col=ff0000>1:36</col> (new personal best)", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your Grotesque Guardians kill count is: <col=ff0000>179</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("grotesque guardians"), eq(96));
		verify(configManager).setConfiguration(eq("killcount.adam"), eq("grotesque guardians"), eq(179));
	}

	@Test
	public void testGgKill()
	{
		ChatMessage chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Fight duration: <col=ff0000>2:41</col>. Personal best: 2:14", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your Grotesque Guardians kill count is: <col=ff0000>32</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("grotesque guardians"), eq(2 * 60 + 14));
		verify(configManager).setConfiguration(eq("killcount.adam"), eq("grotesque guardians"), eq(32));
	}

	@Test
	public void testGuantletPersonalBest()
	{
		ChatMessage chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Challenge duration: <col=ff0000>10:24</col>. Personal best: 7:59.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your Gauntlet completion count is: <col=ff0000>124</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		verify(configManager).setConfiguration(eq("killcount.adam"), eq("gauntlet"), eq(124));
		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("gauntlet"), eq(7 * 60 + 59));
	}

	@Test
	public void testGuantletNewPersonalBest()
	{
		ChatMessage chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Challenge duration: <col=ff0000>10:24</col> (new personal best).", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your Gauntlet completion count is: <col=ff0000>124</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("gauntlet"), eq(10 * 60 + 24));
		verify(configManager).setConfiguration(eq("killcount.adam"), eq("gauntlet"), eq(124));
	}

	@Test
	public void testCoXKill()
	{
		ChatMessage chatMessage = new ChatMessage(null, FRIENDSCHATNOTIFICATION, "", "<col=ef20ff>Congratulations - your raid is complete! Duration:</col> <col=ff0000>37:04</col>", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your completed Chambers of Xeric count is: <col=ff0000>51</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		verify(configManager).setConfiguration(eq("killcount.adam"), eq("chambers of xeric"), eq(51));
		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("chambers of xeric"), eq(37 * 60 + 4));
	}

	@Test
	public void testCoXKillNoPb()
	{
		when(configManager.getConfiguration(anyString(), anyString(), any())).thenReturn(2224);

		ChatMessage chatMessage = new ChatMessage(null, FRIENDSCHATNOTIFICATION, "", "<col=ef20ff>Congratulations - your raid is complete! Duration:</col> <col=ff0000>1:45:04</col>", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		chatMessage = new ChatMessage(null, GAMEMESSAGE, "", "Your completed Chambers of Xeric count is: <col=ff0000>52</col>.", null, 0);
		chatCommandsPlugin.onChatMessage(chatMessage);

		verify(configManager).setConfiguration(eq("killcount.adam"), eq("chambers of xeric"), eq(52));
		verify(configManager, never()).setConfiguration(eq("personalbest.adam"), eq("chambers of xeric"), anyInt());
	}
	
	@Test
	public void testAdventureLogCountersPage()
	{
		Player player = mock(Player.class);
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(client.getLocalPlayer()).thenReturn(player);

		Widget advLogWidget = mock(Widget.class);
		Widget advLogExploitsTextWidget = mock(Widget.class);
		when(advLogWidget.getChild(ChatCommandsPlugin.ADV_LOG_EXPLOITS_TEXT_INDEX)).thenReturn(advLogExploitsTextWidget);
		when(advLogExploitsTextWidget.getText()).thenReturn("The Exploits of " + PLAYER_NAME);
		when(client.getWidget(WidgetInfo.ADVENTURE_LOG)).thenReturn(advLogWidget);

		WidgetLoaded advLogEvent = new WidgetLoaded();
		advLogEvent.setGroupId(ADVENTURE_LOG_ID);
		chatCommandsPlugin.onWidgetLoaded(advLogEvent);
		chatCommandsPlugin.onGameTick(new GameTick());

		String COUNTER_TEXT = "Duel Arena<br>Wins: <col=d0c0b0>4</col><br>Losses: <col=d0c0b0>2</col>" +
				"<br><br>Last Man Standing<br>Rank: <col=d0c0b0>0</col>" +
				"<br><br>Treasure Trails<br>Beginner: <col=d0c0b0>0</col><br>Easy: <col=d0c0b0>7</col>" +
				"<br>Medium: <col=d0c0b0>28</col><br>Hard: <col=d0c0b0>108</col><br>Elite: <col=d0c0b0>15</col>" +
				"<br>Master: <col=d0c0b0>27</col><br>Rank: <col=d0c0b0>Novice</col>" +
				"<br><br>Chompy Hunting<br>Kills: <col=d0c0b0>1,000</col><br>Rank: <col=d0c0b0>Ogre Expert</col>" +
				"<br><br>Order of the White Knights<br>Rank: <col=d0c0b0>Master</col><br>with a kill score of <col=d0c0b0>1,300</col>" +
				"<br><br>TzHaar Fight Cave<br>Fastest run: <col=d0c0b0>38:10</col>" +
				"<br><br>Inferno<br>Fastest run: <col=d0c0b0>-</col><br><br>Zulrah<br>" +
				"Fastest kill: <col=d0c0b0>5:48</col><br><br>Vorkath<br>Fastest kill: <col=d0c0b0>1:21</col>" +
				"<br><br>Galvek<br>Fastest kill: <col=d0c0b0>-</col><br><br>Grotesque Guardians<br>" +
				"Fastest kill: <col=d0c0b0>2:49</col><br><br>Alchemical Hydra<br>Fastest kill: <col=d0c0b0>-</col>" +
				"<br><br>Hespori<br>Fastest kill: <col=d0c0b0>0:57</col><br><br>Nightmare<br>" +
				"Fastest kill: <col=d0c0b0>3:30</col><br><br>The Gauntlet<br>Fastest run: <col=d0c0b0>-</col>" +
				"<br><br>The Corrupted Gauntlet<br>Fastest run: <col=d0c0b0>-</col><br><br>Fragment of Seren<br>" +
				"Fastest kill: <col=d0c0b0>-</col><br><br>Barbarian Assault<br>High-level gambles: " +
				"<col=d0c0b0>15</col><br><br>Fremennik spirits rested: <col=d0c0b0>0</col>";

		Widget countersPage = mock(Widget.class);
		when(countersPage.getText()).thenReturn(COUNTER_TEXT);
		when(client.getWidget(WidgetInfo.COUNTERS_LOG_TEXT)).thenReturn(countersPage);

		WidgetLoaded countersLogEvent = new WidgetLoaded();
		countersLogEvent.setGroupId(COUNTERS_LOG_GROUP_ID);
		chatCommandsPlugin.onWidgetLoaded(countersLogEvent);
		chatCommandsPlugin.onGameTick(new GameTick());

		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("tztok-jad"), eq(38 * 60 + 10));
		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("zulrah"), eq(5 * 60 + 48));
		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("vorkath"), eq(1 * 60 + 21));
		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("grotesque guardians"), eq(2 * 60 + 49));
		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("hespori"), eq(57));
		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("nightmare"), eq( 3 * 60 + 30));
	}

	@Test
	public void testAdventurerLogCountersPage2()
	{
		Player player = mock(Player.class);
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(client.getLocalPlayer()).thenReturn(player);

		Widget advLogWidget = mock(Widget.class);
		Widget advLogExploitsTextWidget = mock(Widget.class);
		when(advLogWidget.getChild(ChatCommandsPlugin.ADV_LOG_EXPLOITS_TEXT_INDEX)).thenReturn(advLogExploitsTextWidget);
		when(advLogExploitsTextWidget.getText()).thenReturn("The Exploits of " + PLAYER_NAME);
		when(client.getWidget(WidgetInfo.ADVENTURE_LOG)).thenReturn(advLogWidget);

		WidgetLoaded advLogEvent = new WidgetLoaded();
		advLogEvent.setGroupId(ADVENTURE_LOG_ID);
		chatCommandsPlugin.onWidgetLoaded(advLogEvent);
		chatCommandsPlugin.onGameTick(new GameTick());

		String COUNTER_TEXT = "Duel Arena<br>Wins: <col=d0c0b0>12</col><br>Losses: <col=d0c0b0>20</col>" +
			"<br><br>Last Man Standing<br>Rank: <col=d0c0b0>0</col>" +
			"<br><br>Treasure Trails<br>Beginner: <col=d0c0b0>1</col><br>Easy: <col=d0c0b0>4</col>" +
			"<br>Medium: <col=d0c0b0>35</col><br>Hard: <col=d0c0b0>66</col><br>Elite: <col=d0c0b0>2</col>" +
			"<br>Master: <col=d0c0b0>0</col><br>Rank: <col=d0c0b0>Novice</col>" +
			"<br><br>Chompy Hunting<br>Kills: <col=d0c0b0>300</col><br>Rank: <col=d0c0b0>Ogre Forester</col>" +
			"<br><br>Order of the White Knights<br>Rank: <col=d0c0b0>Unrated</col><br>with a kill score of <col=d0c0b0>99</col>" +
			"<br><br>TzHaar Fight Cave<br>Fastest run: <col=d0c0b0>65:12</col>" +
			"<br><br>Inferno<br>Fastest run: <col=d0c0b0>-</col><br><br>Zulrah<br>" +
			"Fastest kill: <col=d0c0b0>2:55</col><br><br>Vorkath<br>Fastest kill: <col=d0c0b0>1:37</col>" +
			"<br><br>Galvek<br>Fastest kill: <col=d0c0b0>-</col><br><br>Grotesque Guardians<br>" +
			"Fastest kill: <col=d0c0b0>-</col><br><br>Alchemical Hydra<br>Fastest kill: <col=d0c0b0>-</col>" +
			"<br><br>Hespori<br>Fastest kill: <col=d0c0b0>1:42</col><br><br>Nightmare<br>" +
			"Fastest kill: <col=d0c0b0>-</col><br><br>The Gauntlet<br>Fastest run: <col=d0c0b0>-</col>" +
			"<br><br>The Corrupted Gauntlet<br>Fastest run: <col=d0c0b0>-</col><br><br>Fragment of Seren<br>" +
			"Fastest kill: <col=d0c0b0>-</col><br><br>Barbarian Assault<br>High-level gambles: " +
			"<col=d0c0b0>0</col><br><br>Fremennik spirits rested: <col=d0c0b0>0</col>";

		Widget countersPage = mock(Widget.class);
		when(countersPage.getText()).thenReturn(COUNTER_TEXT);
		when(client.getWidget(WidgetInfo.COUNTERS_LOG_TEXT)).thenReturn(countersPage);

		WidgetLoaded countersLogEvent = new WidgetLoaded();
		countersLogEvent.setGroupId(COUNTERS_LOG_GROUP_ID);
		chatCommandsPlugin.onWidgetLoaded(countersLogEvent);
		chatCommandsPlugin.onGameTick(new GameTick());

		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("tztok-jad"), eq(65 * 60 + 12));
		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("zulrah"), eq(2 * 60 + 55));
		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("vorkath"), eq(1 * 60 + 37));
		verify(configManager).setConfiguration(eq("personalbest.adam"), eq("hespori"), eq(1 * 60 + 42));
	}

	@Test
	public void testNotYourAdventureLogCountersPage()
	{
		Player player = mock(Player.class);
		when(player.getName()).thenReturn(PLAYER_NAME);
		when(client.getLocalPlayer()).thenReturn(player);

		Widget advLogWidget = mock(Widget.class);
		Widget advLogExploitsTextWidget = mock(Widget.class);
		when(advLogWidget.getChild(ChatCommandsPlugin.ADV_LOG_EXPLOITS_TEXT_INDEX)).thenReturn(advLogExploitsTextWidget);
		when(advLogExploitsTextWidget.getText()).thenReturn("The Exploits of " + "not the player");
		when(client.getWidget(WidgetInfo.ADVENTURE_LOG)).thenReturn(advLogWidget);

		WidgetLoaded advLogEvent = new WidgetLoaded();
		advLogEvent.setGroupId(ADVENTURE_LOG_ID);
		chatCommandsPlugin.onWidgetLoaded(advLogEvent);
		chatCommandsPlugin.onGameTick(new GameTick());

		WidgetLoaded countersLogEvent = new WidgetLoaded();
		countersLogEvent.setGroupId(COUNTERS_LOG_GROUP_ID);
		chatCommandsPlugin.onWidgetLoaded(countersLogEvent);
		chatCommandsPlugin.onGameTick(new GameTick());

		verifyNoMoreInteractions(configManager);
	}
}
