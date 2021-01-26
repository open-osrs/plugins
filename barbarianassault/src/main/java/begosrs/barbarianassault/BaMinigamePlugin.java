/*
 * Copyright (c) 2020, BegOsrs <https://github.com/begosrs>
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
package begosrs.barbarianassault;

import begosrs.barbarianassault.api.BaObjectID;
import begosrs.barbarianassault.api.widgets.BaWidgetID;
import begosrs.barbarianassault.api.widgets.BaWidgetInfo;
import begosrs.barbarianassault.attackstyle.AttackStyle;
import begosrs.barbarianassault.attackstyle.AttackStyleWidget;
import begosrs.barbarianassault.attackstyle.WeaponType;
import begosrs.barbarianassault.deathtimes.DeathTimesMode;
import begosrs.barbarianassault.grounditems.GroundItem;
import begosrs.barbarianassault.grounditems.GroundItemsOverlay;
import begosrs.barbarianassault.grounditems.MenuHighlightMode;
import begosrs.barbarianassault.hoppers.CollectorEgg;
import begosrs.barbarianassault.hoppers.HoppersOverlay;
import begosrs.barbarianassault.inventory.InventoryOverlay;
import begosrs.barbarianassault.menuentryswapper.MenuEntrySwapper;
import begosrs.barbarianassault.points.PointsMode;
import begosrs.barbarianassault.points.RewardsBreakdownMode;
import begosrs.barbarianassault.teamhealthbar.TeamHealthBarOverlay;
import begosrs.barbarianassault.ticktimer.RunnerTickTimer;
import begosrs.barbarianassault.ticktimer.RunnerTickTimerOverlay;
import begosrs.barbarianassault.timer.DurationMode;
import begosrs.barbarianassault.timer.Timer;
import begosrs.barbarianassault.timer.TimerOverlay;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.ItemDefinition;
import net.runelite.api.ItemID;
import net.runelite.api.MenuOpcode;
import net.runelite.api.MenuEntry;
import net.runelite.api.MessageNode;
import net.runelite.api.ObjectID;
import net.runelite.api.Player;
import net.runelite.api.ScriptID;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;

@Extension
@Slf4j
@PluginDescriptor(
	name = "Ba Minigame",
	description = "Includes many features to enhance the barbarian assault minigame gameplay experience",
	tags = {"overlay", "b.a.", "barbarian assault", "minigame", "attacker", "defender", "collector", "healer", "plugin hub"},
	type = PluginType.MINIGAME
)
public class BaMinigamePlugin extends Plugin
{
	public static final Color RED = new Color(228, 18, 31);
	public static final Color DARK_GREEN = new Color(0, 153, 0);
	public static final Color LIGHT_BLUE = new Color(60, 124, 240);
	public static final Color LIGHT_RED = new Color(255, 35, 35);
	public static final int DEFAULT_ATTACK_STYLE_COLOR = 16750623;
	private static final int BA_WAVE_NUM_INDEX = 2;
	private static final String END_ROUND_REWARD_NEEDLE_TEXT = "<br>5";
	private static final int MENU_THIRD_OPTION = MenuOpcode.GROUND_ITEM_THIRD_OPTION.getId();
	private static final String BA_MINIGAME_CONFIG_GROUP = "baMinigame";
	private static final String GROUND_ITEMS_CONFIG_GROUP = "grounditems";
	private static final String GROUND_ITEMS_CONFIG_HIGHLIGHTED_ITENS = "highlightedItems";
	private static final String GROUND_ITEMS_CONFIG_HIDDEN_ITENS = "hiddenItems";
	private static final String[] GROUND_ITEMS_HIDDEN_LIST = {
		"Green egg", "Red egg", "Blue egg", "Hammer", "Logs", "Yellow egg", "Crackers", "Tofu", "Worms"
	};
	private static final String BARBARIAN_ASSAULT_CONFIG_GROUP = "barbarianAssault";
	private static final String[] BARBARIAN_ASSAULT_CONFIGS = {
		"showTimer", "showHealerBars", "waveTimes"
	};

	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private BaMinigameConfig config;
	@Inject
	private ChatMessageManager chatMessageManager;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private ItemManager itemManager;
	@Inject
	private InfoBoxManager infoBoxManager;
	@Inject
	private ConfigManager configManager;

	@Inject
	private MenuEntrySwapper menuEntrySwapper;
	@Inject
	private TimerOverlay timerOverlay;
	@Inject
	private InventoryOverlay inventoryOverlay;
	@Inject
	private GroundItemsOverlay groundItemsOverlay;
	@Inject
	private HoppersOverlay hoppersOverlay;
	@Inject
	private RunnerTickTimerOverlay runnerTickTimerOverlay;
	@Inject
	private TeamHealthBarOverlay teamHealthBarOverlay;

	@Getter
	private final List<GameObject> hoppers = new ArrayList<>(2);
	@Getter
	private final Map<CollectorEgg, Integer> cannonEggs = new HashMap<>(4);
	@Getter
	private final Map<GroundItem.Key, GroundItem> groundEggs = new LinkedHashMap<>();
	@Getter
	private final Map<GroundItem.Key, GroundItem> groundBait = new LinkedHashMap<>();
	@Getter
	private final Map<GroundItem.Key, GroundItem> groundLogsHammer = new LinkedHashMap<>(3);
	private final List<Counter> deathTimes = new ArrayList<>();
	private final Map<String, BufferedImage> npcImages = new HashMap<>(4);
	@Getter
	private RunnerTickTimer runnerTickTimer;
	@Getter
	private int inGameBit;
	@Getter(AccessLevel.PACKAGE)
	private Timer timer;
	@Getter(AccessLevel.PACKAGE)
	private Round round;
	@Getter
	private Wave wave;
	@Getter(AccessLevel.PACKAGE)
	private int currentWave;
	@Getter
	private String lastListen;
	@Getter
	private int lastListenItemId;
	private Integer attackStyleTextColor;

	@Provides
	BaMinigameConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BaMinigameConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		npcImages.put("fighters", ImageUtil.getResourceStreamFromClass(getClass(), "/fighter.png"));
		npcImages.put("rangers", ImageUtil.getResourceStreamFromClass(getClass(), "/ranger.png"));
		npcImages.put("healers", ImageUtil.getResourceStreamFromClass(getClass(), "/healer.png"));
		npcImages.put("runners", ImageUtil.getResourceStreamFromClass(getClass(), "/runner.png"));

		overlayManager.add(timerOverlay);
		overlayManager.add(inventoryOverlay);
		overlayManager.add(groundItemsOverlay);
		overlayManager.add(hoppersOverlay);
		overlayManager.add(runnerTickTimerOverlay);
		overlayManager.add(teamHealthBarOverlay);

		menuEntrySwapper.enableSwaps();

		clientThread.invokeLater(() -> updateAttackStyleText(lastListen));

		if (config.showGroundItemHighlights())
		{
			setGroundItemsPluginLists();
		}
		disableBarbarianAssaultPluginFeatures();
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(timerOverlay);
		overlayManager.remove(inventoryOverlay);
		overlayManager.remove(groundItemsOverlay);
		overlayManager.remove(hoppersOverlay);
		overlayManager.remove(runnerTickTimerOverlay);
		overlayManager.remove(teamHealthBarOverlay);

		menuEntrySwapper.disableSwaps();

		hoppers.clear();
		cannonEggs.clear();
		groundEggs.clear();
		groundBait.clear();
		groundLogsHammer.clear();

		clientThread.invokeLater(this::restoreAttackStyleText);

		displayRoleSprite();
		disableRunnerTickTimer(true);
		removeDeathTimes();

		inGameBit = 0;

		timer = null;
		round = null;
		wave = null;
		currentWave = 0;

		lastListen = null;
		lastListenItemId = 0;

		clientThread.invokeLater(this::restoreAttackStyleText);

		restoreGroundItemsPluginLists();
		restoreBarbarianAssaultPluginFeatures();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		final String group = event.getGroup();
		final String key = event.getKey();
		final String oldValue = event.getOldValue();
		final String newValue = event.getNewValue();
		switch (group)
		{
			case BARBARIAN_ASSAULT_CONFIG_GROUP:
			{
				config.setBarbarianAssaultConfigs("");
				break;
			}
			case BA_MINIGAME_CONFIG_GROUP:
				switch (key)
				{
					case "callChangeFlashColor":
					{
						if (inGameBit == 1 && wave != null && wave.getRole() != null)
						{
							setCallFlashColor(wave.getRole());
						}
						break;
					}
					case "showTimer":
					{
						if (!config.showTimer() && inGameBit == 1)
						{
							displayRoleSprite();
						}
						break;
					}
					case "showHpCountOverlay":
					{
						if (!config.showHpCountOverlay() && inGameBit == 1 && getRole() == Role.HEALER)
						{
							removeCountOverlay(Role.HEALER);
						}
						break;
					}
					case "showEggCountOverlay":
					{
						if (!config.showEggCountOverlay() && inGameBit == 1 && getRole() == Role.COLLECTOR)
						{
							removeCountOverlay(Role.COLLECTOR);
						}
						break;
					}
					case "showRunnerTickTimerDefender":
					{
						final boolean display = config.showRunnerTickTimerDefender() && inGameBit == 1 && getRole() == Role.DEFENDER;
						enableRunnerTickTimer(display);
						break;
					}
					case "showRunnerTickTimerAttacker":
					{
						final boolean display = config.showRunnerTickTimerAttacker() && inGameBit == 1 && getRole() == Role.ATTACKER;
						enableRunnerTickTimer(display);
						break;
					}
					case "deathTimesMode":
					{
						final DeathTimesMode deathTimesMode = config.deathTimesMode();
						if (deathTimesMode == DeathTimesMode.INFO_BOX || deathTimesMode == DeathTimesMode.INFOBOX_CHAT)
						{
							showDeathTimes();
						}
						else
						{
							hideDeathTimes();
						}
						break;
					}
					case "highlightAttackStyle":
					case "highlightAttackStyleColor":
					{
						clientThread.invokeLater(() -> updateAttackStyleText(lastListen));
						break;
					}
					case "showGroundItemHighlights":
					{
						if (config.showGroundItemHighlights())
						{
							setGroundItemsPluginLists();
						}
						else
						{
							restoreGroundItemsPluginLists();
						}
						break;
					}
					case "groundItemsPluginHighlightedList":
					{
						if (!oldValue.isEmpty() && newValue.isEmpty())
						{
							config.setGroundItemsPluginHighlightedList(oldValue);
						}
						break;
					}
					case "groundItemsPluginHiddenList":
					{
						if (!oldValue.isEmpty() && newValue.isEmpty())
						{
							config.setGroundItemsPluginHiddenList(oldValue);
						}
						break;
					}
					case "barbarianAssaultConfigs":
					{
						if (!oldValue.isEmpty() && newValue.isEmpty())
						{
							config.setBarbarianAssaultConfigs(oldValue);
						}
						break;
					}
				}
				break;
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		switch (event.getGroupId())
		{
			case BaWidgetID.BA_REWARD_GROUP_ID:
			{
				Widget rewardWidget = client.getWidget(BaWidgetInfo.BA_REWARD_TEXT.getGroupId(), BaWidgetInfo.BA_REWARD_TEXT.getChildId());
				if (rewardWidget == null)
				{
					break;
				}

				if (!rewardWidget.getText().contains(END_ROUND_REWARD_NEEDLE_TEXT))
				{
					onWaveEnded(false);
				}
				else if (round != null)
				{
					onRoundEnded();
				}
				break;
			}
			case BaWidgetID.BA_ATTACKER_GROUP_ID:
			{
				startWave(Role.ATTACKER);
				final boolean display = config.showRunnerTickTimerAttacker();
				enableRunnerTickTimer(display);
				break;
			}
			case BaWidgetID.BA_DEFENDER_GROUP_ID:
			{
				startWave(Role.DEFENDER);
				final boolean display = config.showRunnerTickTimerDefender();
				enableRunnerTickTimer(display);
				break;
			}
			case BaWidgetID.BA_HEALER_GROUP_ID:
			{
				startWave(Role.HEALER);
				break;
			}
			case BaWidgetID.BA_COLLECTOR_GROUP_ID:
			{
				startWave(Role.COLLECTOR);
				break;
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (!chatMessage.getType().equals(ChatMessageType.GAMEMESSAGE))
		{
			return;
		}

		final String message = chatMessage.getMessage();
		if (message.startsWith("---- Wave:"))
		{
			currentWave = Integer.parseInt(message.split(" ")[BA_WAVE_NUM_INDEX]);

			if (round == null || currentWave == Round.STARTING_WAVE)
			{
				timer = new Timer();
				round = new Round(currentWave, timer);
			}

			// wave will be set on onWidgetLoaded which happens after onChatMessage.
			// Reset here in case we are restarting the same wave (no onWidgetLoaded happened).
			wave = null;
		}
		else
		{
			if (wave != null && message.contains("exploded"))
			{
				wave.setWrongEggsCount(wave.getWrongEggsCount() + 1);
				wave.setEggsCount(wave.getEggsCount() - 1);
			}
			else if (wave != null && message.contains("You healed "))
			{
				final int health = Integer.parseInt(message.split(" ")[2]);
				wave.setHpHealed(wave.getHpHealed() + health);
			}
			else if (config.highlightNotification() && message.contains("the wrong type of poisoned food to use"))
			{
				final MessageNode messageNode = chatMessage.getMessageNode();
				final String nodeValue = Text.removeTags(messageNode.getValue());
				messageNode.setValue(ColorUtil.wrapWithColorTag(nodeValue, config.highlightNotificationColor()));
				chatMessageManager.update(messageNode);
			}
			else if (wave != null && wave.getTimer() != null && message.startsWith("All of the Penance "))
			{
				final String npc = message.split(" ")[4];
				final int time = (int) wave.getTimer().getWaveTime().getSeconds();
				addDeathTimes(npc, time);

				final DeathTimesMode deathTimesMode = config.deathTimesMode();
				if (deathTimesMode == DeathTimesMode.CHAT || deathTimesMode == DeathTimesMode.INFOBOX_CHAT)
				{
					final MessageNode node = chatMessage.getMessageNode();
					final String nodeValue = Text.removeTags(node.getValue());

					String timeElapsed = time + "s";
					if (config.enableGameChatColors())
					{
						timeElapsed = ColorUtil.wrapWithColorTag(timeElapsed, Color.RED);
					}

					node.setValue(nodeValue + " " + timeElapsed);
					chatMessageManager.update(node);
				}
			}
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		final int inGame = client.getVar(Varbits.IN_GAME_BA);

		if (inGameBit != inGame)
		{
			if (inGameBit == 1)
			{
				// Use an instance check to determine if this is exiting a game or a tutorial
				// After exiting tutorials there is a small delay before changing IN_GAME_BA back to
				// 0 whereas when in a real wave it changes while still in the instance.
				final DurationMode durationMode = config.showDurationMode();
				if ((durationMode == DurationMode.WAVE || durationMode == DurationMode.WAVE_ROUND)
					&& wave != null && client.isInInstancedRegion())
				{
					announceWaveTime();
				}

				stopWave();
			}
		}

		inGameBit = inGame;

		if (inGameBit == 1)
		{
			updateEggsCount();
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired scriptPostFired)
	{
		if (scriptPostFired.getScriptId() == ScriptID.COMBAT_INTERFACE_SETUP)
		{
			clientThread.invokeLater(() -> updateAttackStyleText(lastListen));
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (inGameBit != 1)
		{
			return;
		}

		if (wave != null)
		{
			final Role role = wave.getRole();
			if (role != null)
			{
				final String currentListen = role.getListenText(client);
				if (currentListen != null && !currentListen.equals(lastListen))
				{
					clientThread.invokeLater(() -> updateAttackStyleText(currentListen));
				}

				lastListen = currentListen;
				lastListenItemId = role.getListenItemId(client);
			}
		}

		if (runnerTickTimer != null)
		{
			runnerTickTimer.incrementCount();
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
	{
		// Don't check for inGameBit == 1. Sometimes onGameObjectSpawned is called before onVarbitChanged

		final GameObject gameObject = gameObjectSpawned.getGameObject();
		if (isHopperGameObject(gameObject.getId()))
		{
			hoppers.add(gameObject);
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned gameObjectDespawned)
	{
		final GameObject gameObject = gameObjectDespawned.getGameObject();
		hoppers.remove(gameObject);
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned itemSpawned)
	{
		final Role role = getRole();
		if (role == null)
		{
			return;
		}

		final TileItem item = itemSpawned.getItem();
		final Tile tile = itemSpawned.getTile();

		addItemSpawn(item, tile, role);
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned itemDespawned)
	{
		final Role role = getRole();
		if (role == null)
		{
			return;
		}

		TileItem item = itemDespawned.getItem();
		Tile tile = itemDespawned.getTile();

		removeItemSpawn(item, tile, role);
	}

	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		menuEntrySwapper.performSwaps();
	}

	public Role getRole()
	{
		return wave == null ? null : wave.getRole();
	}

	private void updateAttackStyleText(String listen)
	{

		restoreAttackStyleText();

		if (!config.highlightAttackStyle() || listen == null || getRole() != Role.ATTACKER)
		{
			return;
		}

		final int var = client.getVar(Varbits.EQUIPPED_WEAPON_TYPE);
		final AttackStyle[] styles = WeaponType.getWeaponType(var).getAttackStyles();
		for (int i = 0; i < styles.length; i++)
		{
			final AttackStyle style = styles[i];
			if (style == null || !listen.startsWith(style.getName()))
			{
				continue;
			}

			final int color = Integer.decode(ColorUtil.toHexColor(config.highlightAttackStyleColor()));

			final AttackStyleWidget attackStyleWidget = AttackStyleWidget.getAttackStyles()[i];

			final BaWidgetInfo attackStyleTextBaWidgetInfo = attackStyleWidget.getTextWidget();
			final Widget attackStyleTextWidget = client.getWidget(attackStyleTextBaWidgetInfo.getGroupId(),
				attackStyleTextBaWidgetInfo.getChildId());
			if (attackStyleTextWidget != null)
			{
				if (attackStyleTextColor == null)
				{
					attackStyleTextColor = attackStyleTextWidget.getTextColor();
				}
				attackStyleTextWidget.setTextColor(color);
			}
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		final Role role = getRole();
		if (role == null)
		{
			return;
		}

		final String listen = lastListen != null ? lastListen : "";

		MenuEntry[] menuEntries = client.getMenuEntries();
		final MenuEntry entry = menuEntries[menuEntries.length - 1];
		String entryOption = Text.removeTags(entry.getOption());
		String entryTarget = Text.removeTags(entry.getTarget());

		final MenuHighlightMode mode = config.menuHighlightMode();

		if (mode != MenuHighlightMode.DISABLED
			&& role == Role.COLLECTOR
			&& entryOption.equals("Take")
			&& event.getIdentifier() == MENU_THIRD_OPTION
			&& listen.startsWith(entryTarget))
		{
			Color color = getEggColorFromName(entryTarget.split(" ")[0]);
			if (color != null)
			{

				if (mode == MenuHighlightMode.OPTION_NAME || mode == MenuHighlightMode.OPTION)
				{
					entryOption = ColorUtil.prependColorTag("Take", color);
					entry.setOption(entryOption);
				}

				if (mode == MenuHighlightMode.OPTION_NAME || mode == MenuHighlightMode.NAME)
				{
					entryTarget = ColorUtil.prependColorTag(entryTarget.substring(entryTarget.indexOf('>') + 1), color);
					entry.setTarget(entryTarget);
				}
			}

		}

		client.setMenuEntries(menuEntries);
	}

	public Color getColorForInventoryItemId(int itemId)
	{
		switch (itemId)
		{
			case ItemID.BULLET_ARROW:
			case ItemID.FIELD_ARROW:
			case ItemID.BLUNT_ARROW:
			case ItemID.BARBED_ARROW:
				return config.highlightArrowColor();
			case ItemID.POISONED_TOFU:
			case ItemID.POISONED_WORMS:
			case ItemID.POISONED_MEAT:
				return config.highlightPoisonColor();
			case ItemID.CRACKERS:
			case ItemID.TOFU:
			case ItemID.WORMS:
				return config.highlightBaitColor();
		}

		return null;
	}

	public Color getColorForGroundItemId(int itemId)
	{
		switch (itemId)
		{
			case ItemID.GREEN_EGG:
				return Color.GREEN;
			case ItemID.RED_EGG:
				return BaMinigamePlugin.LIGHT_RED;
			case ItemID.BLUE_EGG:
				return BaMinigamePlugin.LIGHT_BLUE;
			case ItemID.YELLOW_EGG:
				return Color.YELLOW;
			case ItemID.CRACKERS:
			case ItemID.TOFU:
			case ItemID.WORMS:
				return config.highlightGroundBaitColor();
			case ItemID.LOGS:
			case ItemID.HAMMER:
				return config.highlightGroundLogsHammerColor();
		}
		return null;
	}

	private void restoreAttackStyleText()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		final int color = attackStyleTextColor != null ? attackStyleTextColor : DEFAULT_ATTACK_STYLE_COLOR;

		final int var = client.getVar(Varbits.EQUIPPED_WEAPON_TYPE);
		final AttackStyle[] styles = WeaponType.getWeaponType(var).getAttackStyles();
		for (int i = 0; i < styles.length; i++)
		{
			final AttackStyleWidget attackStyleWidget = AttackStyleWidget.getAttackStyles()[i];
			final BaWidgetInfo attackStyleTextBaWidgetInfo = attackStyleWidget.getTextWidget();

			final Widget attackStyleTextWidget = client.getWidget(attackStyleTextBaWidgetInfo.getGroupId(),
				attackStyleTextBaWidgetInfo.getChildId());
			if (attackStyleTextWidget != null)
			{
				attackStyleTextWidget.setTextColor(color);
			}
		}

		attackStyleTextColor = null;
	}

	private void onWaveEnded(boolean roundEnded)
	{
		Wave wave = this.wave;
		if (wave == null)
		{
			wave = new Wave(client, currentWave);
		}

		if (!roundEnded)
		{
			wave.setAmounts();
			wave.setPoints();

			final BaWidgetInfo pointsBaWidgetInfo = BaWidgetInfo.BA_REWARD_TEXT;
			final Widget pointsWidget = client.getWidget(pointsBaWidgetInfo.getGroupId(), pointsBaWidgetInfo.getChildId());
			if (pointsWidget != null)
			{
				final boolean colorful = config.enableGameChatColors();

				final PointsMode pointsMode = config.showRewardPointsMode();
				if (pointsMode == PointsMode.WAVE || pointsMode == PointsMode.WAVE_ROUND)
				{
					ChatMessageBuilder wavePoints = wave.getWavePoints(colorful);
					announce(wavePoints);
				}

				final RewardsBreakdownMode rewardsBreakdownMode = config.showRewardsBreakdownMode();
				if (rewardsBreakdownMode == RewardsBreakdownMode.WAVE || rewardsBreakdownMode == RewardsBreakdownMode.WAVE_ROUND)
				{
					ChatMessageBuilder waveSummary = wave.getWaveSummary(colorful);
					announce(waveSummary);
				}
			}
		}

		if (round != null)
		{
			round.addWave(wave);
		}

		this.wave = null;
		currentWave = 0;
	}

	private void onRoundEnded()
	{
		if (round == null)
		{
			return;
		}

		onWaveEnded(true);

		final DurationMode durationMode = config.showDurationMode();
		if (durationMode == DurationMode.ROUND || durationMode == DurationMode.WAVE_ROUND)
		{
			announceRoundTime();
		}

		if (round.isComplete())
		{
			round.endRound();

			final boolean colorful = config.enableGameChatColors();

			final PointsMode pointsMode = config.showRewardPointsMode();
			if (pointsMode == PointsMode.ROUND || pointsMode == PointsMode.WAVE_ROUND)
			{
				ChatMessageBuilder roundPoints = round.getRoundPoints(colorful);
				announce(roundPoints);
			}

			final RewardsBreakdownMode rewardsBreakdownMode = config.showRewardsBreakdownMode();
			if (rewardsBreakdownMode == RewardsBreakdownMode.ROUND || rewardsBreakdownMode == RewardsBreakdownMode.WAVE_ROUND)
			{
				ChatMessageBuilder roundSummary = round.getRoundSummary(colorful);
				announce(roundSummary);
			}
		}

		round = null;
		timer = null;
	}

	private void startWave(Role role)
	{
		// Prevent changing waves when a wave is already set, as widgets can be
		// loaded multiple times in game from eg. opening and closing the horn
		// of glory.
		if (wave != null)
		{
			return;
		}

		log.debug("Starting wave {} as {} at {}", currentWave, role, timer.getRoundTimeFormatted(false));

		timer.setWaveStartTime();
		wave = new Wave(client, currentWave, role, timer);

		setCallFlashColor(role);
	}

	private void setCallFlashColor(Role role)
	{
		final BaWidgetInfo widgetInfo = role.getCallFlash();
		final Widget callFlashWidget = client.getWidget(widgetInfo.getGroupId(), widgetInfo.getChildId());
		if (callFlashWidget != null)
		{
			final Color flashColor = config.callChangeFlashColor();
			final int color = Integer.decode(ColorUtil.toHexColor(new Color(flashColor.getRed(), flashColor.getGreen(), flashColor.getBlue())));
			callFlashWidget.setTextColor(color);
			callFlashWidget.setOpacity(255 - flashColor.getAlpha());
		}
	}

	private void displayRoleSprite()
	{
		final Role role = getRole();
		if (role == null)
		{
			return;
		}

		final BaWidgetInfo roleSpriteInfo = role.getRoleSprite();
		final Widget roleSprite = client.getWidget(roleSpriteInfo.getGroupId(), roleSpriteInfo.getChildId());
		if (roleSprite != null)
		{
			roleSprite.setHidden(false);
			roleSprite.setSpriteId(role.getSpriteId());
		}

		final BaWidgetInfo roleTextInfo = role.getRoleText();
		final Widget roleText = client.getWidget(roleTextInfo.getGroupId(), roleTextInfo.getChildId());
		if (roleText != null)
		{
			roleText.setText(role.getName());
		}
	}

	private void removeCountOverlay(Role role)
	{
		final BaWidgetInfo roleTextInfo = role.getRoleText();
		final Widget roleText = client.getWidget(roleTextInfo.getGroupId(), roleTextInfo.getChildId());
		if (roleText != null)
		{
			final String text = roleText.getText().replaceAll("\\(.*\\) ", "");
			roleText.setText(text);
		}
	}

	private void stopWave()
	{
		hoppers.clear();
		cannonEggs.clear();
		groundEggs.clear();
		groundBait.clear();
		groundLogsHammer.clear();
		disableRunnerTickTimer(true);
		removeDeathTimes();
		lastListen = null;
		lastListenItemId = 0;
		clientThread.invokeLater(this::restoreAttackStyleText);
	}

	private void announceWaveTime()
	{
		if (wave == null || wave.getTimer() == null)
		{
			return;
		}

		final String time = wave.getTimer().getWaveTimeFormatted();
		final int number = wave.getNumber();

		final StringBuilder message = new StringBuilder();
		message.append("Wave ");
		if (number > 0)
		{
			message.append(number).append(" ");
		}
		message.append("duration: ");

		announceTime(message.toString(), time);
	}

	private void announceRoundTime()
	{
		if (round == null || round.getTimer() == null)
		{
			return;
		}

		final String time = round.getTimer().getRoundTimeFormatted(true);
		final int fromWave = round.getStartingWave();
		final StringBuilder message = new StringBuilder();
		if (fromWave == 1)
		{
			message.append("Game finished, duration: ");
		}
		else
		{
			message.append("Game finished, duration from wave ").append(fromWave).append(": ");
		}
		announceTime(message.toString(), time);
	}


	private void announceTime(String preText, String time)
	{
		ChatMessageBuilder chatMessageBuilder = new ChatMessageBuilder()
			.append(ChatColorType.NORMAL)
			.append(preText);
		if (config.enableGameChatColors())
		{
			chatMessageBuilder.append(ChatColorType.HIGHLIGHT);
		}
		chatMessageBuilder = chatMessageBuilder.append(time);

		announce(chatMessageBuilder);
	}

	private void announce(final ChatMessageBuilder chatMessage)
	{
		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.CONSOLE)
			.runeLiteFormattedMessage(chatMessage.build())
			.build());
	}

	private Color getEggColorFromName(String eggName)
	{
		switch (eggName.toLowerCase())
		{
			case "green":
				return Color.GREEN;
			case "red":
				return BaMinigamePlugin.LIGHT_RED;
			case "blue":
				return BaMinigamePlugin.LIGHT_BLUE;
		}
		return null;
	}

	private void updateEggsCount()
	{
		for (CollectorEgg collectorEgg : CollectorEgg.values())
		{
			final int eggsCount = client.getVarbitValue(collectorEgg.getVarbits().getId());
			if (eggsCount < 1)
			{
				cannonEggs.remove(collectorEgg);
			}
			else
			{
				cannonEggs.put(collectorEgg, eggsCount);
			}
		}
	}

	private void addItemSpawn(TileItem item, Tile tile, Role role)
	{
		final GroundItem.Key Key = new GroundItem.Key(item.getId(), tile.getWorldLocation());
		final GroundItem groundItem = buildGroundItem(tile, item);

		switch (role)
		{
			case COLLECTOR:
			{
				if (isEggItem(item.getId()))
				{
					addEggSpawn(Key, groundItem);
				}
				break;
			}
			case DEFENDER:
			{
				if (isBaitItem(item.getId()))
				{
					addDefenderBaitSpawn(Key, groundItem);
				}
				else if (isLogsOrHammerItem(item.getId()))
				{
					addLogsHammerSpawn(Key, groundItem);
				}
				break;
			}
		}
	}

	private void removeItemSpawn(TileItem item, Tile tile, Role role)
	{
		switch (role)
		{
			case COLLECTOR:
			{
				if (isEggItem(item.getId()))
				{
					removeEggSpawn(item, tile);
				}
				break;
			}
			case DEFENDER:
			{
				if (isBaitItem(item.getId()))
				{
					removeBaitSpawn(item, tile);
				}
				else if (isLogsOrHammerItem(item.getId()))
				{
					removeLogsHammerSpawn(item, tile);
				}
				break;
			}
		}
	}

	private void addEggSpawn(GroundItem.Key Key, GroundItem groundItem)
	{
		addItemSpawn(Key, groundItem, groundEggs);
	}

	private void addDefenderBaitSpawn(GroundItem.Key Key, GroundItem groundItem)
	{
		addItemSpawn(Key, groundItem, groundBait);
	}

	private void addLogsHammerSpawn(GroundItem.Key Key, GroundItem groundItem)
	{
		addItemSpawn(Key, groundItem, groundLogsHammer);
	}

	private void addItemSpawn(GroundItem.Key Key, GroundItem groundItem, Map<GroundItem.Key, GroundItem> spawnItems)
	{
		GroundItem existing = spawnItems.putIfAbsent(Key, groundItem);
		if (existing != null)
		{
			existing.setQuantity(existing.getQuantity() + groundItem.getQuantity());
		}
	}

	private GroundItem buildGroundItem(final Tile tile, final TileItem item)
	{
		final int itemId = item.getId();
		final ItemDefinition itemComposition = itemManager.getItemDefinition(itemId);
		final int realItemId = itemComposition.getNote() != -1 ? itemComposition.getLinkedNoteId() : itemId;

		return GroundItem.builder()
			.id(itemId)
			.location(tile.getWorldLocation())
			.itemId(realItemId)
			.quantity(item.getQuantity())
			.name(itemComposition.getName())
			.height(tile.getItemLayer().getHeight())
			.spawnTime(Instant.now())
			.build();
	}

	private void removeEggSpawn(TileItem item, Tile tile)
	{
		GroundItem.Key Key = new GroundItem.Key(item.getId(), tile.getWorldLocation());
		GroundItem groundItem = removeItemSpawn(item, Key, groundEggs);
		if (groundItem == null)
		{
			return;
		}

		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}

		Instant spawnTime = groundItem.getSpawnTime();
		Instant now = Instant.now();
		if (wave != null
			&& (spawnTime == null || now.isBefore(spawnTime.plus(Duration.ofMinutes(2))))
			&& groundItem.getLocation().equals(player.getWorldLocation()))
		{
			wave.setEggsCount(wave.getEggsCount() + 1);
		}
	}

	private void removeBaitSpawn(TileItem item, Tile tile)
	{
		GroundItem.Key Key = new GroundItem.Key(item.getId(), tile.getWorldLocation());
		removeItemSpawn(item, Key, groundBait);
	}

	private void removeLogsHammerSpawn(TileItem item, Tile tile)
	{
		GroundItem.Key Key = new GroundItem.Key(item.getId(), tile.getWorldLocation());
		removeItemSpawn(item, Key, groundLogsHammer);
	}

	private GroundItem removeItemSpawn(TileItem item, GroundItem.Key Key, Map<GroundItem.Key, GroundItem> spawnItems)
	{
		GroundItem groundItem = spawnItems.get(Key);
		if (groundItem == null)
		{
			return null;
		}

		if (groundItem.getQuantity() <= item.getQuantity())
		{
			spawnItems.remove(Key);
		}
		else
		{
			groundItem.setQuantity(groundItem.getQuantity() - item.getQuantity());
			// When picking up an item when multiple stacks appear on the ground,
			// it is not known which item is picked up, so we invalidate the spawn
			// time
			groundItem.setSpawnTime(null);
		}

		return groundItem;
	}


	private boolean isEggItem(int itemId)
	{
		return itemId == ItemID.RED_EGG
			|| itemId == ItemID.GREEN_EGG
			|| itemId == ItemID.BLUE_EGG
			|| itemId == ItemID.YELLOW_EGG;
	}

	private boolean isBaitItem(int itemId)
	{
		return itemId == ItemID.TOFU
			|| itemId == ItemID.WORMS
			|| itemId == ItemID.CRACKERS;
	}

	private boolean isLogsOrHammerItem(int itemId)
	{
		return itemId == ItemID.LOGS
			|| itemId == ItemID.HAMMER;
	}

	private boolean isHopperGameObject(int gameObjectId)
	{
		return gameObjectId == ObjectID.EGG_HOPPER
			|| gameObjectId == ObjectID.EGG_HOPPER_20265
			|| gameObjectId == ObjectID.EGG_HOPPER_20266
			|| gameObjectId == BaObjectID.EGG_HOPPER_20267;
	}

	private void enableRunnerTickTimer(boolean display)
	{
		if (runnerTickTimer == null)
		{
			runnerTickTimer = new RunnerTickTimer();
		}
		runnerTickTimer.setDisplaying(display);
	}

	private void disableRunnerTickTimer(boolean remove)
	{
		if (runnerTickTimer != null)
		{
			runnerTickTimer.setDisplaying(false);
		}
		if (remove)
		{
			runnerTickTimer = null;
		}
	}

	private void showDeathTimes()
	{
		List<InfoBox> infoBoxes = infoBoxManager.getInfoBoxes();

		for (Counter counter : deathTimes)
		{
			if (!infoBoxes.contains(counter))
			{
				infoBoxManager.addInfoBox(counter);
			}
		}
	}

	private void hideDeathTimes()
	{
		for (Counter counter : deathTimes)
		{
			infoBoxManager.removeInfoBox(counter);
		}
	}

	private void addDeathTimes(String npc, long time)
	{
		final BufferedImage image = npcImages.get(npc.toLowerCase());
		final Counter counter = new Counter(image, this, (int) time);

		deathTimes.add(counter);

		final DeathTimesMode deathTimesMode = config.deathTimesMode();
		if (deathTimesMode == DeathTimesMode.INFO_BOX || deathTimesMode == DeathTimesMode.INFOBOX_CHAT)
		{
			infoBoxManager.addInfoBox(counter);
		}
	}

	private void removeDeathTimes()
	{
		hideDeathTimes();
		deathTimes.clear();
	}

	private void setGroundItemsPluginLists()
	{
		String highlightedItems = Optional
			.ofNullable(configManager.getConfiguration(GROUND_ITEMS_CONFIG_GROUP, GROUND_ITEMS_CONFIG_HIGHLIGHTED_ITENS))
			.orElse("");
		final List<String> highlightedItemsList = Arrays.stream(highlightedItems.split(","))
			.map(i -> i.trim().toLowerCase()).collect(Collectors.toList());

		final String hiddenItems = Optional
			.ofNullable(configManager.getConfiguration(GROUND_ITEMS_CONFIG_GROUP, GROUND_ITEMS_CONFIG_HIDDEN_ITENS))
			.orElse("");
		final List<String> hiddenItemsList = Arrays.stream(hiddenItems.split(","))
			.map(i -> i.trim().toLowerCase()).collect(Collectors.toList());

		final StringBuilder highlightedItemsListBuilder = new StringBuilder();
		final StringBuilder hiddenItemsListBuilder = new StringBuilder();
		for (String item : GROUND_ITEMS_HIDDEN_LIST)
		{
			if (highlightedItemsList.contains(item.toLowerCase()))
			{
				if (highlightedItemsListBuilder.length() > 0)
				{
					highlightedItemsListBuilder.append(",");
				}
				highlightedItemsListBuilder.append(item);

				// regex to replace any white spaces, followed by 0 or more commas, followed by any white spaces,
				// (?i) mode to match case insensitive, followed by any white spaces, followed by 0 or more commas,
				// and finally followed by any white spaces
				highlightedItems = highlightedItems.replaceAll("\\s*,*\\s*(?i)" + Pattern.quote(item) + "\\s*,*\\s*", ",");

				if (highlightedItems.startsWith(","))
				{
					highlightedItems = highlightedItems.substring(1);
				}
				if (highlightedItems.endsWith(","))
				{
					highlightedItems = highlightedItems.substring(0, highlightedItems.length() - 1);
				}
			}

			if (!hiddenItemsList.contains(item.toLowerCase()))
			{
				if (hiddenItemsListBuilder.length() > 0)
				{
					hiddenItemsListBuilder.append(",");
				}
				hiddenItemsListBuilder.append(item);
			}
		}

		final StringBuilder hiddenItemsBuilder = new StringBuilder(hiddenItems);
		if (hiddenItemsListBuilder.length() > 0 && !hiddenItems.endsWith(","))
		{
			hiddenItemsBuilder.append(",");
		}
		hiddenItemsBuilder.append(hiddenItemsListBuilder);

		config.setGroundItemsPluginHighlightedList(highlightedItemsListBuilder.toString());
		config.setGroundItemsPluginHiddenList(hiddenItemsListBuilder.toString());

		configManager.setConfiguration(GROUND_ITEMS_CONFIG_GROUP, GROUND_ITEMS_CONFIG_HIGHLIGHTED_ITENS, highlightedItems);
		configManager.setConfiguration(GROUND_ITEMS_CONFIG_GROUP, GROUND_ITEMS_CONFIG_HIDDEN_ITENS, hiddenItemsBuilder.toString());
	}

	private void restoreGroundItemsPluginLists()
	{
		String highlightedItems = Optional
			.ofNullable(configManager.getConfiguration(GROUND_ITEMS_CONFIG_GROUP, GROUND_ITEMS_CONFIG_HIGHLIGHTED_ITENS))
			.orElse("");
		StringBuilder highlightedItemsBuilder = new StringBuilder(highlightedItems);
		String[] highlightedItemsArray = config.getGroundItemsPluginHighlightedList().split(",");
		final List<String> highlightedItemsList = Arrays.stream(highlightedItems.split(","))
			.map(i -> i.trim().toLowerCase()).collect(Collectors.toList());

		for (String s : highlightedItemsArray)
		{
			String item = s.trim();
			if (!highlightedItemsList.contains(item.toLowerCase()))
			{
				if (!highlightedItems.isEmpty() && !highlightedItems.endsWith(","))
				{
					highlightedItemsBuilder.append(",");
				}
				highlightedItemsBuilder.append(item);
			}
		}

		configManager.setConfiguration(GROUND_ITEMS_CONFIG_GROUP, GROUND_ITEMS_CONFIG_HIGHLIGHTED_ITENS, highlightedItemsBuilder.toString());
		config.setGroundItemsPluginHighlightedList("");

		String hiddenItems = configManager.getConfiguration(GROUND_ITEMS_CONFIG_GROUP, GROUND_ITEMS_CONFIG_HIDDEN_ITENS);
		final String[] list = config.getGroundItemsPluginHiddenList().split(",");
		for (String item : list)
		{
			item = item.trim();
			if (item.length() > 0 && StringUtils.containsIgnoreCase(hiddenItems, item))
			{
				// regex to replace any white spaces, followed by 0 or more commas, followed by any white spaces,
				// (?i) mode to match case insensitive, followed by any white spaces, followed by 0 or more commas,
				// and finally followed by any white spaces
				hiddenItems = hiddenItems.replaceAll("\\s*,*\\s*(?i)" + Pattern.quote(item) + "\\s*,*\\s*", ",");

				if (hiddenItems.startsWith(","))
				{
					hiddenItems = hiddenItems.substring(1);
				}
				if (hiddenItems.endsWith(","))
				{
					hiddenItems = hiddenItems.substring(0, hiddenItems.length() - 1);
				}
			}
		}
		configManager.setConfiguration(GROUND_ITEMS_CONFIG_GROUP, GROUND_ITEMS_CONFIG_HIDDEN_ITENS, hiddenItems);
		config.setGroundItemsPluginHiddenList("");
	}

	private void disableBarbarianAssaultPluginFeatures()
	{
		StringBuilder configsBuilder = new StringBuilder();
		for (String config : BARBARIAN_ASSAULT_CONFIGS)
		{
			final String value = configManager.getConfiguration(BARBARIAN_ASSAULT_CONFIG_GROUP, config);
			if (configsBuilder.length() > 0)
			{
				configsBuilder.append(",");
			}
			configsBuilder.append(config).append("=").append(value);
			configManager.setConfiguration(BARBARIAN_ASSAULT_CONFIG_GROUP, config, false);
		}
		if (config.getBarbarianAssaultConfigs().length() == 0)
		{
			config.setBarbarianAssaultConfigs(configsBuilder.toString());
		}
	}

	private void restoreBarbarianAssaultPluginFeatures()
	{
		final String[] configs = config.getBarbarianAssaultConfigs().split(",");
		for (String config : configs)
		{
			final String[] keyValue = config.split("=");
			if (keyValue.length == 2)
			{
				final String key = keyValue[0];
				final String value = keyValue[1];
				configManager.setConfiguration(BARBARIAN_ASSAULT_CONFIG_GROUP, key, value);
			}
		}
		config.setBarbarianAssaultConfigs("");
	}
}
