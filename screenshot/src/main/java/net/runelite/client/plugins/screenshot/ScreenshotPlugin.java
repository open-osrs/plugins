/*
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
 *     Modified by Jason <https://github.com/JasonT20015>
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
package net.runelite.client.plugins.screenshot;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.Varbits;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import static net.runelite.api.widgets.WidgetID.BARROWS_REWARD_GROUP_ID;
import static net.runelite.api.widgets.WidgetID.CHAMBERS_OF_XERIC_REWARD_GROUP_ID;
import static net.runelite.api.widgets.WidgetID.CHATBOX_GROUP_ID;
import static net.runelite.api.widgets.WidgetID.CLUE_SCROLL_REWARD_GROUP_ID;
import static net.runelite.api.widgets.WidgetID.DIALOG_SPRITE_GROUP_ID;
import static net.runelite.api.widgets.WidgetID.KINGDOM_GROUP_ID;
import static net.runelite.api.widgets.WidgetID.LEVEL_UP_GROUP_ID;
import static net.runelite.api.widgets.WidgetID.PLAYER_TRADE_CONFIRM_GROUP_ID;
import static net.runelite.api.widgets.WidgetID.QUEST_COMPLETED_GROUP_ID;
import static net.runelite.api.widgets.WidgetID.THEATRE_OF_BLOOD_REWARD_GROUP_ID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import static net.runelite.client.RuneLite.SCREENSHOT_DIR;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PlayerLootReceived;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayRenderer;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.ImageCapture;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Screenshot",
	description = "Enable the manual and automatic taking of screenshots",
	tags = {"external", "images", "imgur", "integration", "notifications"},
	type = PluginType.MISCELLANEOUS
)
@Slf4j
public class ScreenshotPlugin extends Plugin
{
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

	private static final String CHEST_LOOTED_MESSAGE = "You find some treasure in the chest!";
	private static final Map<Integer, String> CHEST_LOOT_EVENTS = Map.of(12127, "The Gauntlet");
	private static final int GAUNTLET_REGION = 7512;
	private static final int CORRUPTED_GAUNTLET_REGION = 7768;
	private static final Pattern NUMBER_PATTERN = Pattern.compile("([0-9]+)");
	private static final Pattern LEVEL_UP_PATTERN = Pattern.compile(".*Your ([a-zA-Z]+) (?:level is|are)? now (\\d+)\\.");
	private static final Pattern BOSSKILL_MESSAGE_PATTERN = Pattern.compile("Your (.+) kill count is: <col=ff0000>(\\d+)</col>.");
	private static final Pattern VALUABLE_DROP_PATTERN = Pattern.compile(".*Valuable drop: ([^<>]+)(?:</col>)?");
	private static final Pattern UNTRADEABLE_DROP_PATTERN = Pattern.compile(".*Untradeable drop: ([^<>]+)(?:</col>)?");
	private static final Pattern QUEST_PATTERN_1 = Pattern.compile(".+?ve\\.*? (?<verb>been|rebuilt|.+?ed)? ?(?:the )?'?(?<quest>.+?)'?(?: [Qq]uest)?[!.]?$");
	private static final Pattern QUEST_PATTERN_2 = Pattern.compile("'?(?<quest>.+?)'?(?: [Qq]uest)? (?<verb>[a-z]\\w+?ed)?(?: f.*?)?[!.]?$");
	private static final List<String> RFD_TAGS = List.of("Another Cook", "freed", "defeated", "saved");
	private static final List<String> WORD_QUEST_IN_NAME_TAGS = List.of("Another Cook", "Doric", "Heroes", "Legends", "Observatory", "Olaf", "Waterfall");
	private static final Pattern DUEL_END_PATTERN = Pattern.compile("You have now (won|lost) ([0-9]+) duels?\\.");
	private static final List<String> PET_MESSAGES = List.of("You have a funny feeling like you're being followed",
		"You feel something weird sneaking into your backpack",
		"You have a funny feeling like you would have been followed");

	private static String format(Date date)
	{
		synchronized (TIME_FORMAT)
		{
			return TIME_FORMAT.format(date);
		}
	}

	private String clueType;
	private Integer clueNumber;

	private Integer barrowsNumber;

	private Integer chambersOfXericNumber;

	private Integer chambersOfXericChallengeNumber;

	private Integer theatreOfBloodNumber;

	private boolean shouldTakeScreenshot;

	@Inject
	private ScreenshotConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ScreenshotOverlay screenshotOverlay;

	@Inject
	private Notifier notifier;

	@Inject
	private Client client;

	@Inject
	private ClientUI clientUi;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private DrawManager drawManager;

	@Inject
	private OverlayRenderer overlayRenderer;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private KeyManager keyManager;

	@Inject
	private SpriteManager spriteManager;

	@Inject
	private ImageCapture imageCapture;

	@Getter(AccessLevel.PACKAGE)
	private BufferedImage reportButton;

	private NavigationButton titleBarButton;

	private String kickPlayerName;

	private final HotkeyListener hotkeyListener = new HotkeyListener(() -> config.hotkey())
	{
		@Override
		public void hotkeyPressed()
		{
			manualScreenshot();
		}
	};

	@Provides
	ScreenshotConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ScreenshotConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(screenshotOverlay);
		SCREENSHOT_DIR.mkdirs();
		keyManager.registerKeyListener(hotkeyListener);

		final BufferedImage iconImage = ImageUtil.getResourceStreamFromClass(getClass(), "screenshot.png");

		titleBarButton = NavigationButton.builder()
			.tab(false)
			.tooltip("Take screenshot")
			.icon(iconImage)
			.onClick(this::manualScreenshot)
			.popup(ImmutableMap
				.<String, Runnable>builder()
				.put("Open screenshot folder...", () ->
				{
					LinkBrowser.open(SCREENSHOT_DIR.toString());
				})
				.build())
			.build();

		clientToolbar.addNavigation(titleBarButton);

		spriteManager.getSpriteAsync(SpriteID.CHATBOX_REPORT_BUTTON, 0, s -> reportButton = s);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(screenshotOverlay);
		clientToolbar.removeNavigation(titleBarButton);
		keyManager.unregisterKeyListener(hotkeyListener);
		overlayRenderer.setShouldRender(true);
		kickPlayerName = null;
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN
			&& reportButton == null)
		{
			reportButton = spriteManager.getSprite(SpriteID.CHATBOX_REPORT_BUTTON, 0);
		}
	}

	@Subscribe
	void onGameTick(GameTick event)
	{
		if (!shouldTakeScreenshot)
		{
			return;
		}

		shouldTakeScreenshot = false;
		String screenshotSubDir = null;

		String fileName = null;
		if (client.getWidget(WidgetInfo.LEVEL_UP_LEVEL) != null)
		{
			fileName = parseLevelUpWidget(client.getWidget(WidgetInfo.LEVEL_UP_LEVEL));
			screenshotSubDir = "Levels";
		}
		else if (client.getWidget(WidgetInfo.DIALOG_SPRITE_TEXT) != null)
		{
			fileName = parseLevelUpWidget(client.getWidget(WidgetInfo.DIALOG_SPRITE_TEXT));
			screenshotSubDir = "Levels";
		}
		else if (client.getWidget(WidgetInfo.QUEST_COMPLETED_NAME_TEXT) != null)
		{
			// "You have completed The Corsair Curse!"
			String text = client.getWidget(WidgetInfo.QUEST_COMPLETED_NAME_TEXT).getText();
			fileName = parseQuestCompletedWidget(text);
			screenshotSubDir = "Quests";
		}
		else if (client.getWidget(WidgetInfo.CHATBOX_CONTAINER).getChild(1) != null)
		{
			fileName = parseLevelUpWidget(client.getWidget(WidgetInfo.CHATBOX_CONTAINER).getChild(1));
			screenshotSubDir = "Levels";
		}

		if (fileName != null)
		{
			takeScreenshot(fileName, screenshotSubDir);
		}
	}

	@Subscribe
	private void onActorDeath(ActorDeath event)
	{
		Actor actor = event.getActor();

		if (actor instanceof Player)
		{
			Player player = (Player) actor;

			if (player == client.getLocalPlayer() && config.screenshotPlayerDeath())
			{
				takeScreenshot("Death", "Deaths");
			}
			if (config.screenshotKills() && (config.pvpKillScreenshotMode() == PvPKillScreenshotMode.ON_KILL ||
				config.pvpKillScreenshotMode() == PvPKillScreenshotMode.BOTH))
			{
				if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting() == player)
				{
					takeScreenshot("KO " + player.getName(), "PvP Kills");
				}
			}

			int tob = client.getVar(Varbits.THEATRE_OF_BLOOD);
			if (config.screenshotFriendDeath() && player != client.getLocalPlayer() && player.getName() != null
				&& (player.isFriend() || player.isFriendsChatMember()
						|| (client.getVar(Varbits.IN_RAID) == 1 || tob == 2 || tob == 3)))
			{
				takeScreenshot("Death " + player.getName(), "Deaths");
			}
		}
	}

	@Subscribe
	private void onPlayerLootReceived(final PlayerLootReceived playerLootReceived)
	{
		if (config.screenshotKills() && (config.pvpKillScreenshotMode() == PvPKillScreenshotMode.ON_LOOT ||
			config.pvpKillScreenshotMode() == PvPKillScreenshotMode.BOTH))
		{
			final Player player = playerLootReceived.getPlayer();
			final String name = player.getName();
			String fileName = "Kill " + name;
			takeScreenshot(fileName, "PvP Kills");
		}
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent e)
	{
		if (!"confirmFriendsChatKick".equals(e.getEventName()))
		{
			return;
		}

		final String[] stringStack = client.getStringStack();
		final int stringSize = client.getStringStackSize();
		kickPlayerName = stringStack[stringSize - 1];
	}

	@Subscribe
	void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE
			&& event.getType() != ChatMessageType.SPAM
			&& event.getType() != ChatMessageType.TRADE
			&& event.getType() != ChatMessageType.FRIENDSCHATNOTIFICATION)
		{
			return;
		}

		String chatMessage = event.getMessage();

		if (chatMessage.contains("You have completed") && chatMessage.contains("Treasure"))
		{
			Matcher m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage));
			if (m.find())
			{
				clueNumber = Integer.valueOf(m.group());
				clueType = chatMessage.substring(chatMessage.lastIndexOf(m.group()) + m.group().length() + 1, chatMessage.indexOf("Treasure") - 1);
				return;
			}
		}

		if (chatMessage.startsWith("Your Barrows chest count is"))
		{
			Matcher m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage));
			if (m.find())
			{
				barrowsNumber = Integer.valueOf(m.group());
				return;
			}
		}

		if (chatMessage.startsWith("Your completed Chambers of Xeric count is:"))
		{
			Matcher m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage));
			if (m.find())
			{
				chambersOfXericNumber = Integer.valueOf(m.group());
				return;
			}
		}

		if (chatMessage.startsWith("Your completed Chambers of Xeric Challenge Mode count is:"))
		{
			Matcher m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage));
			if (m.find())
			{
				chambersOfXericChallengeNumber = Integer.valueOf(m.group());
				return;
			}
		}

		if (chatMessage.startsWith("Your completed Theatre of Blood count is:"))
		{
			Matcher m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage));
			if (m.find())
			{
				theatreOfBloodNumber = Integer.valueOf(m.group());
				return;
			}
		}

		if (config.screenshotKick() && chatMessage.equals("Your request to kick/ban this user was successful."))
		{
			if (kickPlayerName == null)
			{
				return;
			}

			takeScreenshot("Kick " + kickPlayerName, "Clan Chat Kicks");
			kickPlayerName = null;
		}

		if (config.screenshotPet() && PET_MESSAGES.stream().anyMatch(chatMessage::contains))
		{
			String fileName = "Pet";
			takeScreenshot(fileName, "Pets");
		}

		if (config.screenshotBossKills())
		{
			Matcher m = BOSSKILL_MESSAGE_PATTERN.matcher(chatMessage);
			if (m.matches())
			{
				String bossName = m.group(1);
				String bossKillcount = m.group(2);
				String fileName = bossName + "(" + bossKillcount + ")";
				takeScreenshot(fileName, "Boss Kills");
			}
		}

		if (chatMessage.equals(CHEST_LOOTED_MESSAGE) && config.screenshotRewards())
		{
			final int regionID = client.getLocalPlayer().getWorldLocation().getRegionID();
			String eventName = CHEST_LOOT_EVENTS.get(regionID);
			if (eventName != null)
			{
				takeScreenshot(eventName, "Chest Loot");
			}
		}

		if (config.screenshotValuableDrop())
		{
			Matcher m = VALUABLE_DROP_PATTERN.matcher(chatMessage);
			if (m.matches())
			{
				String valuableDropName = m.group(1);
				String fileName = "Valuable drop " + valuableDropName;
				takeScreenshot(fileName, "Valuable Drops");
			}
		}

		if (config.screenshotUntradeableDrop() && !isInsideGauntlet())
		{
			Matcher m = UNTRADEABLE_DROP_PATTERN.matcher(chatMessage);
			if (m.matches())
			{
				String untradeableDropName = m.group(1);
				String fileName = "Untradeable drop " + untradeableDropName;
				takeScreenshot(fileName, "Untradeable Drops");
			}
		}

		if (config.screenshotDuels())
		{
			Matcher m = DUEL_END_PATTERN.matcher(chatMessage);
			if (m.find())
			{
				String result = m.group(1);
				String count = m.group(2);
				String fileName = "Duel " + result + " (" + count + ")";
				takeScreenshot(fileName, "Duels");
			}
		}
	}

	@Subscribe
	void onWidgetLoaded(WidgetLoaded event)
	{
		String fileName;
		String screenshotSubDir;
		int groupId = event.getGroupId();

		switch (groupId)
		{
			case QUEST_COMPLETED_GROUP_ID:
			case CLUE_SCROLL_REWARD_GROUP_ID:
			case CHAMBERS_OF_XERIC_REWARD_GROUP_ID:
			case THEATRE_OF_BLOOD_REWARD_GROUP_ID:
			case BARROWS_REWARD_GROUP_ID:
				if (!config.screenshotRewards())
				{
					return;
				}
				break;
			case LEVEL_UP_GROUP_ID:
			case DIALOG_SPRITE_GROUP_ID:
			case CHATBOX_GROUP_ID:
				if (!config.screenshotLevels())
				{
					return;
				}
				break;
			case KINGDOM_GROUP_ID:
				if (!config.screenshotKingdom())
				{
					return;
				}
				break;
			case PLAYER_TRADE_CONFIRM_GROUP_ID:
				if (!config.screenshotSecondTradeScreen())
				{
					return;
				}
				break;
		}

		switch (groupId)
		{
			case KINGDOM_GROUP_ID:
			{
				fileName = "Kingdom " + LocalDate.now();
				screenshotSubDir = "Kingdom Rewards";
				break;
			}
			case CHAMBERS_OF_XERIC_REWARD_GROUP_ID:
			{
				if (chambersOfXericNumber != null)
				{
					fileName = "Chambers of Xeric(" + chambersOfXericNumber + ")";
					screenshotSubDir = "Boss Kills";
					chambersOfXericNumber = null;
					break;
				}
				else if (chambersOfXericChallengeNumber != null)
				{
					fileName = "Chambers of Xeric Challenge Mode(" + chambersOfXericChallengeNumber + ")";
					screenshotSubDir = "Boss Kills";
					chambersOfXericChallengeNumber = null;
					break;
				}
				else
				{
					return;
				}
			}
			case THEATRE_OF_BLOOD_REWARD_GROUP_ID:
			{
				if (theatreOfBloodNumber == null)
				{
					return;
				}

				fileName = "Theatre of Blood(" + theatreOfBloodNumber + ")";
				screenshotSubDir = "Boss Kills";
				theatreOfBloodNumber = null;
				break;
			}
			case BARROWS_REWARD_GROUP_ID:
			{
				if (barrowsNumber == null)
				{
					return;
				}

				fileName = "Barrows(" + barrowsNumber + ")";
				screenshotSubDir = "Boss Kills";
				barrowsNumber = null;
				break;
			}
			case LEVEL_UP_GROUP_ID:
			case DIALOG_SPRITE_GROUP_ID:
			case QUEST_COMPLETED_GROUP_ID:
			case CHATBOX_GROUP_ID:
			{
				// level up widget gets loaded prior to the text being set, so wait until the next tick
				shouldTakeScreenshot = true;
				return;
			}
			case CLUE_SCROLL_REWARD_GROUP_ID:
			{
				if (clueType == null || clueNumber == null)
				{
					return;
				}

				fileName = Character.toUpperCase(clueType.charAt(0)) + clueType.substring(1) + "(" + clueNumber + ")";
				screenshotSubDir = "Clue Scroll Rewards";
				clueType = null;
				clueNumber = null;
				break;
			}
			case PLAYER_TRADE_CONFIRM_GROUP_ID:
			{
				String secondTradeName = Text.standardize(client.getWidget(WidgetInfo.SECOND_TRADING_WITH).getText()).replace("trading with:", "");

				fileName = secondTradeName + " Second Trade Screen " + LocalDate.now();
				screenshotSubDir = "Trade Window";
				break;
			}
			default:
				return;
		}

		takeScreenshot(fileName, screenshotSubDir);
	}

	private void manualScreenshot()
	{
		takeScreenshot("", null);
	}

	/**
	 * Receives a Widget containing the level-up dialog,
	 * and parses it into a shortened string for filename usage.
	 *
	 * @param levelUpWidget Widget containing the level-up text,
	 *                      with the format "Your Skill (level is/are) now 99."
	 * @return Shortened string in the format "Skill(99)"
	 */
	String parseLevelUpWidget(Widget levelUpWidget)
	{
		if (levelUpWidget == null)
		{
			return null;
		}

		Matcher m = LEVEL_UP_PATTERN.matcher(levelUpWidget.getText());
		if (!m.matches())
		{
			return null;
		}

		String skillName = m.group(1);
		String skillLevel = m.group(2);
		return skillName + "(" + skillLevel + ")";
	}

	/**
	 * Parses the passed quest completion dialog text into a shortened string for filename usage.
	 *
	 * @param text The {@link Widget#getText() text} of the {@link WidgetInfo#QUEST_COMPLETED_NAME_TEXT} widget.
	 * @return Shortened string in the format "Quest(The Corsair Curse)"
	 */
	@VisibleForTesting
	static String parseQuestCompletedWidget(final String text)
	{
		// "You have completed The Corsair Curse!"
		final Matcher questMatch1 = QUEST_PATTERN_1.matcher(text);
		// "'One Small Favour' completed!"
		final Matcher questMatch2 = QUEST_PATTERN_2.matcher(text);
		final Matcher questMatchFinal = questMatch1.matches() ? questMatch1 : questMatch2;
		if (!questMatchFinal.matches())
		{
			return "Quest(quest not found)";
		}

		String quest = questMatchFinal.group("quest");
		String verb = questMatchFinal.group("verb") != null ? questMatchFinal.group("verb") : "";

		if (verb.contains("kind of"))
		{
			quest += " partial completion";
		}
		else if (verb.contains("completely"))
		{
			quest += " II";
		}

		if (RFD_TAGS.stream().anyMatch((quest + verb)::contains))
		{
			quest = "Recipe for Disaster - " + quest;
		}

		if (WORD_QUEST_IN_NAME_TAGS.stream().anyMatch(quest::contains))
		{
			quest += " Quest";
		}

		return "Quest(" + quest + ')';
	}

	/**
	 * Saves a screenshot of the client window to the screenshot folder as a PNG,
	 * and optionally uploads it to an image-hosting service.
	 *
	 * @param fileName Filename to use, without file extension.
	 */
	private void takeScreenshot(String fileName)
	{
		if (!config.includeOverlays())
		{
			overlayRenderer.setShouldRender(false);
		}
		if (client.getGameState() == GameState.LOGIN_SCREEN)
		{
			// Prevent the screenshot from being captured
			log.info("Login screenshot prevented");
			return;
		}

		Consumer<Image> imageCallback = (img) ->
		{
			// This callback is on the game thread, move to executor thread
			executor.submit(() -> takeScreenshot(fileName, null, img));
		};

		if (config.displayDate())
		{
			screenshotOverlay.queueForTimestamp(imageCallback);
		}
		else
		{
			drawManager.requestNextFrameListener(imageCallback);
		}
	}

	/**
	 * Saves a screenshot of the client window to the screenshot folder as a PNG,
	 * and optionally uploads it to an image-hosting service.
	 *
	 * @param fileName     Filename to use, without file extension.
	 * @param subdirectory The subdirectory to save it in
	 */
	private void takeScreenshot(String fileName, String subdirectory)
	{
		if (client.getGameState() == GameState.LOGIN_SCREEN)
		{
			// Prevent the screenshot from being captured
			log.info("Login screenshot prevented");
			return;
		}
		if (!config.includeOverlays())
		{
			overlayRenderer.setShouldRender(false);
		}

		Consumer<Image> imageCallback = (img) ->
		{
			// This callback is on the game thread, move to executor thread
			executor.submit(() -> takeScreenshot(fileName, subdirectory, img));

		};

		if (config.displayDate())
		{
			screenshotOverlay.queueForTimestamp(imageCallback);
		}
		else
		{
			drawManager.requestNextFrameListener(imageCallback);
		}
		overlayRenderer.setShouldRender(true);
	}

	private void takeScreenshot(String fileName, String subDir, Image image)
	{
		if (!config.includeOverlays())
		{
			overlayRenderer.setShouldRender(false);
		}
		BufferedImage screenshot = config.includeFrame()
			? new BufferedImage(clientUi.getWidth(), clientUi.getHeight(), BufferedImage.TYPE_INT_ARGB)
			: new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		Graphics graphics = screenshot.getGraphics();

		int gameOffsetX = 0;
		int gameOffsetY = 0;

		if (config.includeFrame())
		{
			// Draw the client frame onto the screenshot
			try
			{
				SwingUtilities.invokeAndWait(() -> clientUi.paint(graphics));
			}
			catch (InterruptedException | InvocationTargetException e)
			{
				log.warn("unable to paint client UI on screenshot", e);
			}

			// Evaluate the position of the game inside the frame
			final Point canvasOffset = clientUi.getCanvasOffset();
			gameOffsetX = canvasOffset.getX();
			gameOffsetY = canvasOffset.getY();
		}

		// Draw the game onto the screenshot
		graphics.drawImage(image, gameOffsetX, gameOffsetY, null);
		imageCapture.takeScreenshot(screenshot, fileName, subDir, config.notifyWhenTaken(), config.uploadScreenshot());
		overlayRenderer.setShouldRender(true);
	}

	private boolean isInsideGauntlet()
	{
		return this.client.isInInstancedRegion()
			&& this.client.getMapRegions().length > 0
			&& (this.client.getMapRegions()[0] == GAUNTLET_REGION
			|| this.client.getMapRegions()[0] == CORRUPTED_GAUNTLET_REGION);
	}

	@VisibleForTesting
	int getClueNumber()
	{
		return clueNumber;
	}

	@VisibleForTesting
	String getClueType()
	{
		return clueType;
	}

	@VisibleForTesting
	int getBarrowsNumber()
	{
		return barrowsNumber;
	}

	@VisibleForTesting
	int getChambersOfXericNumber()
	{
		return chambersOfXericNumber;
	}

	@VisibleForTesting
	int getChambersOfXericChallengeNumber()
	{
		return chambersOfXericChallengeNumber;
	}

	@VisibleForTesting
	int gettheatreOfBloodNumber()
	{
		return theatreOfBloodNumber;
	}

}
