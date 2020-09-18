/*
 * Copyright (c) 2017, Seth <Sethtroll3@gmail.com>
 * Copyright (c) 2018, Jordan Atwood <jordan.atwood423@gmail.com>
 * Copyright (c) 2019, Lucas <https://github.com/Lucwousin>
 * Copyright (c) 2019, winterdaze
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
package net.runelite.client.plugins.timers;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.AnimationID;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import static net.runelite.api.ItemID.FIRE_CAPE;
import static net.runelite.api.ItemID.INFERNAL_CAPE;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.SkullIcon;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.WorldType;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.SpotAnimationChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetHiddenChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import static net.runelite.api.widgets.WidgetInfo.PVP_WORLD_SAFE_ZONE;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import static net.runelite.client.plugins.timers.GameIndicator.VENGEANCE_ACTIVE;
import static net.runelite.client.plugins.timers.GameTimer.*;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
		name = "Timers",
		description = "Show various timers in an infobox",
		tags = {"combat", "items", "magic", "potions", "prayer", "overlay", "abyssal", "sire", "inferno", "fight", "caves", "cape", "timer", "tzhaar"},
		type = PluginType.MISCELLANEOUS
)
@Slf4j
public class TimersPlugin extends Plugin
{
	private static final String ANTIFIRE_DRINK_MESSAGE = "You drink some of your antifire potion.";
	private static final String ANTIFIRE_EXPIRED_MESSAGE = "<col=7f007f>Your antifire potion has expired.</col>";
	private static final String CANNON_FURNACE_MESSAGE = "You add the furnace.";
	private static final String CANNON_PICKUP_MESSAGE = "You pick up the cannon. It's really heavy.";
	private static final String CANNON_REPAIR_MESSAGE = "You repair your cannon, restoring it to working order.";
	private static final String CHARGE_EXPIRED_MESSAGE = "<col=ef1020>Your magical charge fades away.</col>";
	private static final String CHARGE_MESSAGE = "<col=ef1020>You feel charged with magic power.</col>";
	private static final String EXTENDED_ANTIFIRE_DRINK_MESSAGE = "You drink some of your extended antifire potion.";
	private static final String EXTENDED_SUPER_ANTIFIRE_DRINK_MESSAGE = "You drink some of your extended super antifire potion.";
	private static final String FROZEN_MESSAGE = "<col=ef1020>You have been frozen!</col>";
	private static final String GAUNTLET_ENTER_MESSAGE = "You enter the Gauntlet.";
	private static final String GOD_WARS_ALTAR_MESSAGE = "you recharge your prayer.";
	private static final String IMBUED_HEART_READY_MESSAGE = "<col=ef1020>Your imbued heart has regained its magical power.</col>";
	private static final String IMBUED_HEART_NOTREADY_MESSAGE = "The heart is still drained of its power.";
	private static final String MAGIC_IMBUE_EXPIRED_MESSAGE = "Your Magic Imbue charge has ended.";
	private static final String MAGIC_IMBUE_MESSAGE = "You are charged to combine runes!";
	private static final String STAFF_OF_THE_DEAD_SPEC_EXPIRED_MESSAGE = "Your protection fades away";
	private static final String STAFF_OF_THE_DEAD_SPEC_MESSAGE = "Spirits of deceased evildoers offer you their protection";
	private static final String STAMINA_DRINK_MESSAGE = "You drink some of your stamina potion.";
	private static final String STAMINA_SHARED_DRINK_MESSAGE = "You have received a shared dose of stamina potion.";
	private static final String STAMINA_EXPIRED_MESSAGE = "<col=8f4808>Your stamina potion has expired.</col>";
	private static final String SUPER_ANTIFIRE_DRINK_MESSAGE = "You drink some of your super antifire potion";
	private static final String SUPER_ANTIFIRE_EXPIRED_MESSAGE = "<col=7f007f>Your super antifire potion has expired.</col>";
	private static final int VENOM_VALUE_CUTOFF = -40; // Antivenom < -40 =< Antipoison < 0
	private static final int POISON_TICK_LENGTH = 30;
	private static final String KILLED_TELEBLOCK_OPPONENT_TEXT = "Your Tele Block has been removed because you killed ";
	private static final String PRAYER_ENHANCE_EXPIRED = "<col=ff0000>Your prayer enhance effect has worn off.</col>";
	private static final String ENDURANCE_EFFECT_MESSAGE = "Your Ring of endurance doubles the duration of your stamina potion's effect.";
	private static final Pattern DEADMAN_HALF_TELEBLOCK_PATTERN = Pattern.compile("A Tele Block spell has been cast on you by (.+)\\. It will expire in 1 minute, 15 seconds\\.</col>");
	private static final Pattern FULL_TELEBLOCK_PATTERN = Pattern.compile("A Tele Block spell has been cast on you by (.+)\\. It will expire in 5 minutes\\.</col>");
	private static final Pattern HALF_TELEBLOCK_PATTERN = Pattern.compile("A Tele Block spell has been cast on you by (.+)\\. It will expire in 2 minutes, 30 seconds\\.</col>");
	private static final Pattern DIVINE_POTION_PATTERN = Pattern.compile("You drink some of your divine (.+) potion\\.");
	private static final int FIGHT_CAVES_REGION_ID = 9551;
	private static final int INFERNO_REGION_ID = 9043;
	private static final Pattern TZHAAR_WAVE_MESSAGE = Pattern.compile("Wave: (\\d+)");
	private static final String TZHAAR_DEFEATED_MESSAGE = "You have been defeated!";
	private static final Pattern TZHAAR_COMPLETE_MESSAGE = Pattern.compile("Your (TzTok-Jad|TzKal-Zuk) kill count is:");
	private static final Pattern TZHAAR_PAUSED_MESSAGE = Pattern.compile("The (Inferno|Fight Cave) has been paused. You may now log out.");

	private TimerTimer freezeTimer;
	private int freezeTime = -1; // time frozen, in game ticks

	private TimerTimer staminaTimer;
	private boolean wasWearingEndurance;

	private int lastRaidVarb;
	private int lastWildernessVarb;
	private int lastVengCooldownVarb;
	private int lastIsVengeancedVarb;
	private int lastPoisonVarp;
	private int nextPoisonTick;
	private WorldPoint lastPoint;
	private TeleportWidget lastTeleportClicked;
	private int lastAnimation;
	private boolean loggedInRace;
	private boolean widgetHiddenChangedOnPvpWorld;
	private ElapsedTimer tzhaarTimer;
	private boolean skulledLastTick = false;
	private boolean imbuedHeartClicked;

	@Inject
	private ItemManager itemManager;

	@Inject
	private SpriteManager spriteManager;

	@Inject
	private Client client;

	@Inject
	private TimersConfig config;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Provides
	TimersConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TimersConfig.class);
	}

	@Override
	protected void shutDown()
	{
		infoBoxManager.removeIf(t -> t instanceof TimerTimer);
		lastRaidVarb = -1;
		lastPoint = null;
		lastTeleportClicked = null;
		lastAnimation = -1;
		loggedInRace = false;
		widgetHiddenChangedOnPvpWorld = false;
		lastPoisonVarp = 0;
		nextPoisonTick = 0;
		imbuedHeartClicked = false;
		removeTzhaarTimer();
		staminaTimer = null;
		removeTzhaarTimer();
	}

	@Subscribe
	private void onVarbitChanged(VarbitChanged event)
	{
		int raidVarb = client.getVar(Varbits.IN_RAID);
		int vengCooldownVarb = client.getVar(Varbits.VENGEANCE_COOLDOWN);
		int isVengeancedVarb = client.getVar(Varbits.VENGEANCE_ACTIVE);
		int poisonVarp = client.getVar(VarPlayer.POISON);

		if (lastRaidVarb != raidVarb)
		{
			removeGameTimer(OVERLOAD_RAID);
			removeGameTimer(PRAYER_ENHANCE);
			lastRaidVarb = raidVarb;
		}

		if (lastVengCooldownVarb != vengCooldownVarb && config.showVengeance())
		{
			if (vengCooldownVarb == 1)
			{
				createGameTimer(VENGEANCE);
			}
			else
			{
				removeGameTimer(VENGEANCE);
			}

			lastVengCooldownVarb = vengCooldownVarb;
		}

		if (lastIsVengeancedVarb != isVengeancedVarb && config.showVengeanceActive())
		{
			if (isVengeancedVarb == 1)
			{
				createGameIndicator(VENGEANCE_ACTIVE);
			}
			else
			{
				removeGameIndicator(VENGEANCE_ACTIVE);
			}

			lastIsVengeancedVarb = isVengeancedVarb;
		}

		int inWilderness = client.getVar(Varbits.IN_WILDERNESS);

		if (lastWildernessVarb != inWilderness
				&& client.getGameState() == GameState.LOGGED_IN
				&& !loggedInRace)
		{
			if (!WorldType.isPvpWorld(client.getWorldType())
					&& inWilderness == 0)
			{
				log.debug("Left wilderness in non-PVP world, clearing Teleblock timer.");
				removeTbTimers();
			}

			lastWildernessVarb = inWilderness;
		}

		if (lastPoisonVarp != poisonVarp && config.showAntiPoison())
		{
			if (nextPoisonTick - client.getTickCount() <= 0 || lastPoisonVarp == 0)
			{
				nextPoisonTick = client.getTickCount() + 30;
			}

			if (poisonVarp >= 0)
			{
				removeGameTimer(ANTIPOISON);
				removeGameTimer(ANTIVENOM);
			}
			else if (poisonVarp >= VENOM_VALUE_CUTOFF)
			{
				Duration duration = Duration.ofMillis((long) 600 * (nextPoisonTick - client.getTickCount() + Math.abs((poisonVarp + 1) * POISON_TICK_LENGTH)));
				removeGameTimer(ANTIVENOM);
				createGameTimer(ANTIPOISON, duration);
			}
			else
			{
				Duration duration = Duration.ofMillis((long) 600 * (nextPoisonTick - client.getTickCount() + Math.abs((poisonVarp + 1 - VENOM_VALUE_CUTOFF) * POISON_TICK_LENGTH)));
				removeGameTimer(ANTIPOISON);
				createGameTimer(ANTIVENOM, duration);
			}

			lastPoisonVarp = poisonVarp;
		}
	}

	@Subscribe
	private void onWidgetHiddenChanged(WidgetHiddenChanged event)
	{
		Widget widget = event.getWidget();
		if (WorldType.isPvpWorld(client.getWorldType())
				&& WidgetInfo.TO_GROUP(widget.getId()) == WidgetID.PVP_GROUP_ID)
		{
			widgetHiddenChangedOnPvpWorld = true;
		}
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("timers"))
		{
			return;
		}

		if (!config.showHomeMinigameTeleports())
		{
			removeGameTimer(HOME_TELEPORT);
			removeGameTimer(MINIGAME_TELEPORT);
		}

		if (!config.showAntiFire())
		{
			removeGameTimer(ANTIFIRE);
			removeGameTimer(EXANTIFIRE);
			removeGameTimer(SUPERANTIFIRE);
		}

		if (!config.showStamina())
		{
			removeGameTimer(STAMINA);
		}

		if (!config.showOverload())
		{
			removeGameTimer(OVERLOAD);
			removeGameTimer(OVERLOAD_RAID);
		}

		if (!config.showPrayerEnhance())
		{
			removeGameTimer(PRAYER_ENHANCE);
		}

		if (!config.showDivine())
		{
			removeGameTimer(DIVINE_SUPER_ATTACK);
			removeGameTimer(DIVINE_SUPER_STRENGTH);
			removeGameTimer(DIVINE_SUPER_DEFENCE);
			removeGameTimer(DIVINE_SUPER_COMBAT);
			removeGameTimer(DIVINE_RANGING);
			removeGameTimer(DIVINE_MAGIC);
		}

		if (!config.showCannon())
		{
			removeGameTimer(CANNON);
		}

		if (!config.showMagicImbue())
		{
			removeGameTimer(MAGICIMBUE);
		}

		if (!config.showCharge())
		{
			removeGameTimer(CHARGE);
		}

		if (!config.showImbuedHeart())
		{
			removeGameTimer(IMBUEDHEART);
		}

		if (!config.showStaffOfTheDead())
		{
			removeGameTimer(STAFF_OF_THE_DEAD);
		}

		if (!config.showVengeance())
		{
			removeGameTimer(VENGEANCE);
		}

		if (!config.showVengeanceActive())
		{
			removeGameIndicator(VENGEANCE_ACTIVE);
		}

		if (!config.showTeleblock())
		{
			removeTbTimers();
		}

		if (!config.showFreezes())
		{
			removeGameTimer(BIND);
			removeGameTimer(SNARE);
			removeGameTimer(ENTANGLE);
			removeGameTimer(ICERUSH);
			removeGameTimer(ICEBURST);
			removeGameTimer(ICEBLITZ);
			removeGameTimer(ICEBARRAGE);
		}

		if (!config.showAntiPoison())
		{
			removeGameTimer(ANTIPOISON);
			removeGameTimer(ANTIVENOM);
		}

		if (!config.showTzhaarTimers())
		{
			removeTzhaarTimer();
		}
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (config.showStamina()
				&& event.getOption().contains("Drink")
				&& (event.getIdentifier() == ItemID.STAMINA_MIX1
				|| event.getIdentifier() == ItemID.STAMINA_MIX2
				|| event.getIdentifier() == ItemID.EGNIOL_POTION_1
				|| event.getIdentifier() == ItemID.EGNIOL_POTION_2
				|| event.getIdentifier() == ItemID.EGNIOL_POTION_3
				|| event.getIdentifier() == ItemID.EGNIOL_POTION_4))
		{
			// Needs menu option hook because mixes use a common drink message, distinct from their standard potion messages
			createStaminaTimer();
			return;
		}

		if (config.showAntiFire()
				&& event.getOption().contains("Drink")
				&& (event.getIdentifier() == ItemID.ANTIFIRE_MIX1
				|| event.getIdentifier() == ItemID.ANTIFIRE_MIX2))
		{
			// Needs menu option hook because mixes use a common drink message, distinct from their standard potion messages
			createGameTimer(ANTIFIRE);
			return;
		}

		if (config.showAntiFire()
				&& event.getOption().contains("Drink")
				&& (event.getIdentifier() == ItemID.EXTENDED_ANTIFIRE_MIX1
				|| event.getIdentifier() == ItemID.EXTENDED_ANTIFIRE_MIX2))
		{
			// Needs menu option hook because mixes use a common drink message, distinct from their standard potion messages
			createGameTimer(EXANTIFIRE);
			return;
		}

		if (config.showAntiFire()
				&& event.getOption().contains("Drink")
				&& (event.getIdentifier() == ItemID.SUPER_ANTIFIRE_MIX1
				|| event.getIdentifier() == ItemID.SUPER_ANTIFIRE_MIX2))
		{
			// Needs menu option hook because mixes use a common drink message, distinct from their standard potion messages
			createGameTimer(SUPERANTIFIRE);
			return;
		}

		if (config.showAntiFire()
				&& event.getOption().contains("Drink")
				&& (event.getIdentifier() == ItemID.EXTENDED_SUPER_ANTIFIRE_MIX1
				|| event.getIdentifier() == ItemID.EXTENDED_SUPER_ANTIFIRE_MIX2))
		{
			// Needs menu option hook because mixes use a common drink message, distinct from their standard potion messages
			createGameTimer(EXSUPERANTIFIRE);
			return;
		}

		TeleportWidget teleportWidget = TeleportWidget.of(event.getParam1());
		if (teleportWidget != null)
		{
			lastTeleportClicked = teleportWidget;
		}

		if (config.showImbuedHeart()
				&& event.getOption().contains("Invigorate"))
		{
			// Needs a hook as there's a few cases where potions boost the same amount as the heart
			imbuedHeartClicked = true;
		}
	}

	@Subscribe
	void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.SPAM && event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		if (event.getMessage().equals(ENDURANCE_EFFECT_MESSAGE))
		{
			wasWearingEndurance = true;
		}

		if (config.showStamina() && (event.getMessage().equals(STAMINA_DRINK_MESSAGE) || event.getMessage().equals(STAMINA_SHARED_DRINK_MESSAGE)))
		{
			createStaminaTimer();
		}

		if (event.getMessage().equals(STAMINA_EXPIRED_MESSAGE) || event.getMessage().equals(GAUNTLET_ENTER_MESSAGE))
		{
			removeGameTimer(STAMINA);
			staminaTimer = null;
		}

		if (config.showAntiFire() && event.getMessage().equals(ANTIFIRE_DRINK_MESSAGE))
		{
			createGameTimer(ANTIFIRE);
		}

		if (config.showAntiFire() && event.getMessage().equals(EXTENDED_ANTIFIRE_DRINK_MESSAGE))
		{
			createGameTimer(EXANTIFIRE);
		}

		if (config.showGodWarsAltar() && event.getMessage().equalsIgnoreCase(GOD_WARS_ALTAR_MESSAGE))//Normal altars are "You recharge your Prayer points." while gwd is "You recharge your Prayer."
		{
			createGameTimer(GOD_WARS_ALTAR);
		}

		if (config.showAntiFire() && event.getMessage().equals(EXTENDED_SUPER_ANTIFIRE_DRINK_MESSAGE))
		{
			createGameTimer(EXSUPERANTIFIRE);
		}

		if (config.showAntiFire() && event.getMessage().equals(ANTIFIRE_EXPIRED_MESSAGE))
		{
			//they have the same expired message
			removeGameTimer(ANTIFIRE);
			removeGameTimer(EXANTIFIRE);
		}

		if (config.showOverload() && event.getMessage().startsWith("You drink some of your") && event.getMessage().contains("overload"))
		{
			if (client.getVar(Varbits.IN_RAID) == 1)
			{
				createGameTimer(OVERLOAD_RAID);
			}
			else
			{
				createGameTimer(OVERLOAD);
			}

		}

		if (config.showCannon() && (event.getMessage().equals(CANNON_FURNACE_MESSAGE) || event.getMessage().contains(CANNON_REPAIR_MESSAGE)))
		{
			createGameTimer(CANNON);
		}

		if (config.showCannon() && event.getMessage().equals(CANNON_PICKUP_MESSAGE))
		{
			removeGameTimer(CANNON);
		}

		if (config.showMagicImbue() && event.getMessage().equals(MAGIC_IMBUE_MESSAGE))
		{
			createGameTimer(MAGICIMBUE);
		}

		if (event.getMessage().equals(MAGIC_IMBUE_EXPIRED_MESSAGE))
		{
			removeGameTimer(MAGICIMBUE);
		}

		if (config.showTeleblock())
		{
			if (FULL_TELEBLOCK_PATTERN.matcher(event.getMessage()).find())
			{
				createGameTimer(FULLTB);
			}
			else if (HALF_TELEBLOCK_PATTERN.matcher(event.getMessage()).find())
			{
				if (client.getWorldType().contains(WorldType.DEADMAN))
				{
					createGameTimer(DMM_FULLTB);
				}
				else
				{
					createGameTimer(HALFTB);
				}
			}
			else if (DEADMAN_HALF_TELEBLOCK_PATTERN.matcher(event.getMessage()).find())
			{
				createGameTimer(DMM_HALFTB);
			}
			else if (event.getMessage().contains(KILLED_TELEBLOCK_OPPONENT_TEXT))
			{
				removeTbTimers();
			}
		}

		if (config.showAntiFire() && event.getMessage().contains(SUPER_ANTIFIRE_DRINK_MESSAGE))
		{
			createGameTimer(SUPERANTIFIRE);
		}

		if (config.showAntiFire() && event.getMessage().equals(SUPER_ANTIFIRE_EXPIRED_MESSAGE))
		{
			removeGameTimer(SUPERANTIFIRE);
		}

		if (config.showImbuedHeart() && event.getMessage().contains(IMBUED_HEART_NOTREADY_MESSAGE))
		{
			imbuedHeartClicked = false;
			return;
		}

		if (config.showImbuedHeart() && event.getMessage().equals(IMBUED_HEART_READY_MESSAGE))
		{
			removeGameTimer(IMBUEDHEART);
		}

		if (config.showPrayerEnhance() && event.getMessage().startsWith("You drink some of your") && event.getMessage().contains("prayer enhance"))
		{
			createGameTimer(PRAYER_ENHANCE);
		}

		if (config.showPrayerEnhance() && event.getMessage().equals(PRAYER_ENHANCE_EXPIRED))
		{
			removeGameTimer(PRAYER_ENHANCE);
		}

		if (config.showCharge() && event.getMessage().equals(CHARGE_MESSAGE))
		{
			createGameTimer(CHARGE);
		}

		if (config.showCharge() && event.getMessage().equals(CHARGE_EXPIRED_MESSAGE))
		{
			removeGameTimer(CHARGE);
		}

		if (config.showStaffOfTheDead() && event.getMessage().contains(STAFF_OF_THE_DEAD_SPEC_MESSAGE))
		{
			createGameTimer(STAFF_OF_THE_DEAD);
		}

		if (config.showStaffOfTheDead() && event.getMessage().contains(STAFF_OF_THE_DEAD_SPEC_EXPIRED_MESSAGE))
		{
			removeGameTimer(STAFF_OF_THE_DEAD);
		}

		if (config.showFreezes() && event.getMessage().equals(FROZEN_MESSAGE))
		{
			freezeTimer = createGameTimer(ICEBARRAGE);
			freezeTime = client.getTickCount();
		}

		if (config.showDivine())
		{
			Matcher mDivine = DIVINE_POTION_PATTERN.matcher(event.getMessage());
			if (mDivine.find())
			{
				switch (mDivine.group(1))
				{
					case "super attack":
						createGameTimer(DIVINE_SUPER_ATTACK);
						break;

					case "super strength":
						createGameTimer(DIVINE_SUPER_STRENGTH);
						break;

					case "super defence":
						createGameTimer(DIVINE_SUPER_DEFENCE);
						break;

					case "combat":
						createGameTimer(DIVINE_SUPER_COMBAT);
						break;

					case "ranging":
						createGameTimer(DIVINE_RANGING);
						break;

					case "magic":
						createGameTimer(DIVINE_MAGIC);
						break;

					case "bastion":
						createGameTimer(DIVINE_BASTION);
						break;

					case "battlemage":
						createGameTimer(DIVINE_BATTLEMAGE);
						break;
				}
			}
		}

		if (config.showTzhaarTimers())
		{
			String message = event.getMessage();
			Matcher matcher = TZHAAR_COMPLETE_MESSAGE.matcher(message);

			if (message.contains(TZHAAR_DEFEATED_MESSAGE) || matcher.matches())
			{
				removeTzhaarTimer();
				config.tzhaarStartTime(null);
				config.tzhaarLastTime(null);
				return;
			}

			Instant now = Instant.now();
			matcher = TZHAAR_PAUSED_MESSAGE.matcher(message);
			if (matcher.find())
			{
				config.tzhaarLastTime(now);
				createTzhaarTimer();
				return;
			}

			matcher = TZHAAR_WAVE_MESSAGE.matcher(message);
			if (!matcher.find())
			{
				return;
			}

			if (config.tzhaarStartTime() == null)
			{
				int wave = Integer.parseInt(matcher.group(1));
				if (wave == 1)
				{
					config.tzhaarStartTime(now);
					createTzhaarTimer();
				}
			}
			else if (config.tzhaarLastTime() != null)
			{
				log.debug("Unpausing tzhaar timer");

				// Advance start time by how long it has been paused
				Instant tzhaarStartTime = config.tzhaarStartTime();
				tzhaarStartTime = tzhaarStartTime.plus(Duration.between(config.tzhaarLastTime(), now));
				config.tzhaarStartTime(tzhaarStartTime);

				config.tzhaarLastTime(null);
				createTzhaarTimer();
			}
		}
	}

	private boolean isInFightCaves()
	{
		return client.getMapRegions() != null && ArrayUtils.contains(client.getMapRegions(), FIGHT_CAVES_REGION_ID);
	}

	private boolean isInInferno()
	{
		return client.getMapRegions() != null && ArrayUtils.contains(client.getMapRegions(), INFERNO_REGION_ID);
	}

	private void createTzhaarTimer()
	{
		removeTzhaarTimer();

		int imageItem = isInFightCaves() ? FIRE_CAPE : (isInInferno() ? INFERNAL_CAPE : -1);
		if (imageItem == -1)
		{
			return;
		}

		tzhaarTimer = new ElapsedTimer(itemManager.getImage(imageItem), this, config.tzhaarStartTime(), config.tzhaarLastTime());
		infoBoxManager.addInfoBox(tzhaarTimer);
	}

	private void removeTzhaarTimer()
	{
		if (tzhaarTimer != null)
		{
			infoBoxManager.removeInfoBox(tzhaarTimer);
			tzhaarTimer = null;
		}
	}

	@Subscribe
	private void onGameTick(GameTick event)
	{
		loggedInRace = false;

		Player player = client.getLocalPlayer();

		if (player == null)
		{
			return;
		}

		WorldPoint currentWorldPoint = player.getWorldLocation();

		final boolean isSkulled = player.getSkullIcon() != null && player.getSkullIcon() != SkullIcon.SKULL_FIGHT_PIT;

		if (isSkulled != skulledLastTick && config.showSkull())
		{
			skulledLastTick = isSkulled;
			if (isSkulled)
			{
				createGameTimer(SKULL);
			}
			else
			{
				removeGameTimer(SKULL);
			}
		}

		if (freezeTimer != null &&
				// assume movement means unfrozen
				freezeTime != client.getTickCount()
				&& !currentWorldPoint.equals(lastPoint))
		{
			removeGameTimer(freezeTimer.getTimer());
			freezeTimer = null;
		}

		lastPoint = currentWorldPoint;

		if (!widgetHiddenChangedOnPvpWorld)
		{
			return;
		}

		widgetHiddenChangedOnPvpWorld = false;

		Widget widget = client.getWidget(PVP_WORLD_SAFE_ZONE);
		if (widget != null && !widget.isSelfHidden())
		{
			log.debug("Entered safe zone in PVP world, clearing Teleblock timer.");
			removeTbTimers();
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		switch (gameStateChanged.getGameState())
		{
			case LOADING:
				if (tzhaarTimer != null && !isInFightCaves() && !isInInferno())
				{
					removeTzhaarTimer();
					config.tzhaarStartTime(null);
					config.tzhaarLastTime(null);
				}
				break;
			case HOPPING:
			case LOGIN_SCREEN:
				// pause tzhaar timer if logged out without pausing
				if (config.tzhaarStartTime() != null && config.tzhaarLastTime() == null)
				{
					config.tzhaarLastTime(Instant.now());
					log.debug("Pausing tzhaar timer");
				}

				removeTzhaarTimer(); // will be readded by the wave message
				removeTbTimers();
				break;
			case LOGGED_IN:
				loggedInRace = true;
				break;
		}
	}

	@Subscribe
	private void onAnimationChanged(AnimationChanged event)
	{
		Actor actor = event.getActor();

		if (config.showAbyssalSireStun()
				&& actor instanceof NPC)
		{
			int npcId = ((NPC) actor).getId();

			switch (npcId)
			{
				// Show the countdown when the Sire enters the stunned state.
				case NpcID.ABYSSAL_SIRE_5888:
					createGameTimer(ABYSSAL_SIRE_STUN);
					break;

				// Hide the countdown if the Sire isn't in the stunned state.
				// This is necessary because the Sire leaves the stunned
				// state early once all all four respiratory systems are killed.
				case NpcID.ABYSSAL_SIRE:
				case NpcID.ABYSSAL_SIRE_5887:
				case NpcID.ABYSSAL_SIRE_5889:
				case NpcID.ABYSSAL_SIRE_5890:
				case NpcID.ABYSSAL_SIRE_5891:
				case NpcID.ABYSSAL_SIRE_5908:
					removeGameTimer(ABYSSAL_SIRE_STUN);
					break;
			}
		}

		if (actor != client.getLocalPlayer())
		{
			return;
		}

		if (config.showHomeMinigameTeleports()
				&& client.getLocalPlayer().getAnimation() == AnimationID.IDLE
				&& (lastAnimation == AnimationID.BOOK_HOME_TELEPORT_5
				|| lastAnimation == AnimationID.COW_HOME_TELEPORT_6))
		{
			if (lastTeleportClicked == TeleportWidget.HOME_TELEPORT)
			{
				createGameTimer(HOME_TELEPORT);
			}
			else if (lastTeleportClicked == TeleportWidget.MINIGAME_TELEPORT)
			{
				createGameTimer(MINIGAME_TELEPORT);
			}
		}

		if (config.showDFSSpecial() && lastAnimation == AnimationID.DRAGONFIRE_SHIELD_SPECIAL)
		{
			createGameTimer(DRAGON_FIRE_SHIELD);
		}

		lastAnimation = actor.getAnimation();
	}

	@Subscribe
	private void onSpotAnimationChanged(SpotAnimationChanged event)
	{
		Actor actor = event.getActor();
		Player player = client.getLocalPlayer();

		if (player == null || actor != client.getLocalPlayer())
		{
			return;
		}

		if (config.showImbuedHeart() && actor.getSpotAnimation() == IMBUEDHEART.getGraphicId())
		{
			createGameTimer(IMBUEDHEART);
		}

		if (config.showFreezes())
		{
			if (actor.getSpotAnimation() == BIND.getGraphicId())
			{
				createGameTimer(BIND);
			}

			if (actor.getSpotAnimation() == SNARE.getGraphicId())
			{
				createGameTimer(SNARE);
			}

			if (actor.getSpotAnimation() == ENTANGLE.getGraphicId())
			{
				createGameTimer(ENTANGLE);
			}

			// downgrade freeze based on graphic, if at the same tick as the freeze message
			if (freezeTime == client.getTickCount())
			{
				if (actor.getSpotAnimation() == ICERUSH.getGraphicId())
				{
					removeGameTimer(ICEBARRAGE);
					freezeTimer = createGameTimer(ICERUSH);
				}

				if (actor.getSpotAnimation() == ICEBURST.getGraphicId())
				{
					removeGameTimer(ICEBARRAGE);
					freezeTimer = createGameTimer(ICEBURST);
				}

				if (actor.getSpotAnimation() == ICEBLITZ.getGraphicId())
				{
					removeGameTimer(ICEBARRAGE);
					freezeTimer = createGameTimer(ICEBLITZ);
				}
			}
		}
	}

	/**
	 * Remove SOTD timer and update stamina timer when equipment is changed.
	 */
	@Subscribe
	void onItemContainerChanged(ItemContainerChanged itemContainerChanged)
	{
		if (itemContainerChanged.getContainerId() != InventoryID.EQUIPMENT.getId())
		{
			return;
		}

		ItemContainer container = itemContainerChanged.getItemContainer();
		Item weapon = container.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
		if (weapon == null ||
				(weapon.getId() != ItemID.STAFF_OF_THE_DEAD &&
						weapon.getId() != ItemID.TOXIC_STAFF_OF_THE_DEAD &&
						weapon.getId() != ItemID.STAFF_OF_LIGHT &&
						weapon.getId() != ItemID.TOXIC_STAFF_UNCHARGED))
		{
			// remove sotd timer if the staff has been unwielded
			removeGameTimer(STAFF_OF_THE_DEAD);
		}

		if (wasWearingEndurance)
		{
			Item ring = container.getItem(EquipmentInventorySlot.RING.getSlotIdx());

			// when using the last ring charge the ring changes to the uncharged version, ignore that and don't
			// halve the timer
			if (ring == null || (ring.getId() != ItemID.RING_OF_ENDURANCE && ring.getId() != ItemID.RING_OF_ENDURANCE_UNCHARGED_24844))
			{
				wasWearingEndurance = false;
				if (staminaTimer != null)
				{
					// Remaining duration gets divided by 2
					Duration remainingDuration = Duration.between(Instant.now(), staminaTimer.getEndTime()).dividedBy(2);
					// This relies on the chat message to be removed, which could be after the timer has been culled;
					// so check there is still remaining time
					if (!remainingDuration.isNegative() && !remainingDuration.isZero())
					{
						log.debug("Halving stamina timer");
						staminaTimer.setDuration(remainingDuration);
					}
				}
			}
		}
	}

	@Subscribe
	private void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC npc = npcDespawned.getNpc();

		if (!npc.isDead())
		{
			return;
		}

		int npcId = npc.getId();

		if (npcId == NpcID.ZOMBIFIED_SPAWN || npcId == NpcID.ZOMBIFIED_SPAWN_8063)
		{
			removeGameTimer(ICEBARRAGE);
		}
	}

	@Subscribe
	private void onActorDeath(ActorDeath playerDeath)
	{
		if (playerDeath.getActor() == client.getLocalPlayer())
		{
			infoBoxManager.removeIf(t -> t instanceof TimerTimer && ((TimerTimer) t).getTimer().isRemovedOnDeath());
		}
	}

	@Subscribe
	private void onStatChanged(StatChanged event)
	{
		Skill skill = event.getSkill();

		if (skill != Skill.MAGIC || !config.showImbuedHeart() || !imbuedHeartClicked)
		{
			return;
		}

		int magicLvl = client.getRealSkillLevel(skill);
		int magicBoost = client.getBoostedSkillLevel(skill);
		int heartBoost = 1 + (int) (magicLvl * 0.1);

		if (magicBoost - magicLvl != heartBoost)
		{
			return;
		}

		imbuedHeartClicked = false;
		createGameTimer(IMBUEDHEART);
	}

	private TimerTimer createGameTimer(final GameTimer timer)
	{
		removeGameTimer(timer);

		TimerTimer t = new TimerTimer(timer, this);
		switch (timer.getImageType())
		{
			case SPRITE:
				spriteManager.getSpriteAsync(timer.getImageId(), 0, t);
				break;
			case ITEM:
				t.setImage(itemManager.getImage(timer.getImageId()));
				break;
		}
		t.setTooltip(timer.getDescription());
		infoBoxManager.addInfoBox(t);
		return t;
	}

	private void createStaminaTimer()
	{
		Duration duration = Duration.ofMinutes(wasWearingEndurance ? 4 : 2);
		staminaTimer = createGameTimer(STAMINA, duration);
	}

	private TimerTimer createGameTimer(final GameTimer timer, Duration duration)
	{
		removeGameTimer(timer);

		TimerTimer t = new TimerTimer(timer, duration, this);
		switch (timer.getImageType())
		{
			case SPRITE:
				spriteManager.getSpriteAsync(timer.getImageId(), 0, t);
				break;
			case ITEM:
				t.setImage(itemManager.getImage(timer.getImageId()));
				break;
		}
		t.setTooltip(timer.getDescription());
		infoBoxManager.addInfoBox(t);
		return t;
	}

	private void removeGameTimer(GameTimer timer)
	{
		infoBoxManager.removeIf(t -> t instanceof TimerTimer && ((TimerTimer) t).getTimer() == timer);
	}

	private IndicatorIndicator createGameIndicator(GameIndicator gameIndicator)
	{
		removeGameIndicator(gameIndicator);

		IndicatorIndicator indicator = new IndicatorIndicator(gameIndicator, this);
		switch (gameIndicator.getImageType())
		{
			case SPRITE:
				spriteManager.getSpriteAsync(gameIndicator.getImageId(), 0, indicator);
				break;
			case ITEM:
				indicator.setImage(itemManager.getImage(gameIndicator.getImageId()));
				break;
		}
		indicator.setTooltip(gameIndicator.getDescription());
		infoBoxManager.addInfoBox(indicator);

		return indicator;
	}

	private void removeGameIndicator(GameIndicator indicator)
	{
		infoBoxManager.removeIf(t -> t instanceof IndicatorIndicator && ((IndicatorIndicator) t).getIndicator() == indicator);
	}

	private void removeTbTimers()
	{
		removeGameTimer(FULLTB);
		removeGameTimer(HALFTB);
		removeGameTimer(DMM_FULLTB);
		removeGameTimer(DMM_HALFTB);
	}
}