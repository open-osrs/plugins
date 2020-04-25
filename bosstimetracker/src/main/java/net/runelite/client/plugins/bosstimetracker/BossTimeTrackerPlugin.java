/*
 * Copyright (c) 2019, winterdaze
 * Copyright (c) 2020, Crystalknoct
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
package net.runelite.client.plugins.bosstimetracker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import static net.runelite.api.ItemID.FIRE_CAPE;
import static net.runelite.api.ItemID.INFERNAL_CAPE;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.util.Text;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Boss Time Tracker",
	enabledByDefault = false,
	description = "Display elapsed time in the Fight Caves and Inferno",
	tags = {"inferno", "fight", "caves", "cape", "timer", "tzhaar", "pvm", "noa", "nightmare", "of", "ashihama"},
	type = PluginType.PVM
)
public class BossTimeTrackerPlugin extends Plugin
{
	private static final Pattern WAVE_MESSAGE = Pattern.compile("Wave: (\\d+)");
	private static final String DEFEATED_MESSAGE = "You have been defeated!";
	private static final Pattern COMPLETE_MESSAGE = Pattern.compile("Your (TzTok-Jad|TzKal-Zuk) kill count is:");
	private static final Pattern PAUSED_MESSAGE = Pattern.compile("The (Inferno|Fight Cave) has been paused. You may now log out.");
	private static final String CONFIG_GROUP = "Boss Time Tracker";
	private static final String CONFIG_TIME = "time";
	private static final String CONFIG_STARTED = "started";
	private static final String CONFIG_LASTTIME = "lasttime";
	private static final int NM_ROOM_MASK = 66994112;
	private static final int NM_ROOM = 58205888;
	private BossTimeTrackerInfoBox nib;

	int fight_timer = -1, phase_timer = -1, subph_timer = -1;

	int last_nmstate = -1;

	int[] phase_splits = new int[4];

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ConfigManager configManager;

	@Getter(AccessLevel.PACKAGE)
	private BossTimeTracker timer;

	private Instant startTime;
	private Instant lastTime;
	private Boolean started = false;
	private boolean loggingIn;
	private ILoggingEvent e;

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOGGED_IN:
				if (loggingIn)
				{
					loggingIn = false;
					loadConfig();
					resetConfig();
				}
				break;
			case LOGGING_IN:
				loggingIn = true;
				break;
			case LOADING:
				if (!loggingIn)
				{
					updateInfoBoxState();
				}
				break;
			case HOPPING:
				loggingIn = true;
			case LOGIN_SCREEN:
				removeTimer();
				saveConfig();
				break;
			default:
				break;
		}
	}

	@Subscribe
	public void onGameTick(GameTick e)
	{
		if (!noa_instance())
		{
			try
			{
				shutDown();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			return;
		}
		int nmstate = this.client.getVarps()[1683];
		if (nmstate != -1 && nmstate != 9432 && this.nib == null)
		{
			this.nib = new BossTimeTrackerInfoBox(this.client, this);
			this.infoBoxManager.addInfoBox(this.nib);
		}
		if (nmstate != this.last_nmstate)
		{
			onNightmareStateChanged(nmstate);
			this.last_nmstate = nmstate;
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM)
		{
			return;
		}

		String message = Text.removeTags(event.getMessage());
		Matcher matcher = COMPLETE_MESSAGE.matcher(message);

		if (message.contains(DEFEATED_MESSAGE) || matcher.matches())
		{
			removeTimer();
			resetConfig();
			resetVars();
			return;
		}

		Instant now = Instant.now();
		matcher = PAUSED_MESSAGE.matcher(message);
		if (matcher.matches())
		{
			lastTime = now;
			createTimer(startTime, now);
			return;
		}

		matcher = WAVE_MESSAGE.matcher(message);
		if (!matcher.matches())
		{
			return;
		}

		if (!started)
		{
			int wave = Integer.parseInt(matcher.group(1));
			if (wave != 1)
			{
				return;
			}

			started = true;
			startTime = now;
		}
		else if (lastTime != null)
		{
			startTime = startTime.plus(Duration.between(startTime, now)).minus(Duration.between(startTime, lastTime));
			lastTime = null;
		}

		createTimer(startTime, lastTime);
		{
			if (e.getMessage().contains("All four totems are fully charged."))
			{
				onNightmareStateChanged(9999);
			}
		}
	}

	private void onNightmareStateChanged(int nmstate)
	{
		int tick_count = this.client.getTickCount();
		int phase = (this.phase_splits[1] == -1) ? 1 : ((this.phase_splits[2] == -1) ? 2 : 3);
		if (nmstate == 9425 || nmstate == 9426 || nmstate == 9427)
		{
			this.phase_timer = tick_count;
			this.subph_timer = tick_count;
			if (nmstate == 9425)
			{
				this.fight_timer = tick_count;
			}
		}
		else if (nmstate == 9428 || nmstate == 9429 || nmstate == 9430)
		{
			msg(tick_count, phase, false);
			this.subph_timer = tick_count;
		}
		else if (nmstate == 9999)
		{
			tick_count += 5;
			msg(tick_count, phase, true);
			this.phase_splits[phase] = tick_count - this.phase_timer;
			if (phase == 3)
			{
				this.phase_splits[0] = tick_count - this.fight_timer;
			}
		}
	}

	private void msg(int tick_count, int phase, boolean pillars)
	{
		StringBuilder msg = new StringBuilder();
		msg.append("Nightmare P");
		msg.append(phase);
		msg.append(pillars ? " pillars " : " boss ");
		msg.append("complete! Duration: <col=ff0000>");
		msg.append(ntpib(tick_count - this.subph_timer));
		msg.append("</col> Total: <col=ff0000>");
		msg.append(ntpib(tick_count - this.fight_timer));
		msg.append("</col>");
		this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg.toString(), null);
	}

	private boolean noa_instance()
	{
		WorldPoint wp = this.client.getLocalPlayer().getWorldArea().toWorldPoint();
		int x = wp.getX() - this.client.getBaseX();
		int y = wp.getY() - this.client.getBaseY();
		int template = this.client.getInstanceTemplateChunks()[this.client.getPlane()][x / 8][y / 8];
		return ((template & 0x3FE3FC0) == 58205888);
	}

	private String ntpib(int ticks)
	{
		int m = ticks / 100;
		int s = (ticks - m * 100) * 6 / 10;
		return m + ((s < 10) ? ":0" : ":") + s;
	}

	private void updateInfoBoxState()
	{
		if (timer == null)
		{
			return;
		}

		if (!checkInFightCaves() && !checkInInferno())
		{
			removeTimer();
			resetConfig();
			resetVars();
		}
	}

	private boolean checkInFightCaves()
	{
		return client.getMapRegions() != null && Arrays.stream(client.getMapRegions())
			.filter(x -> x == 9551)
			.toArray().length > 0;
	}

	private boolean checkInInferno()
	{
		return client.getMapRegions() != null && Arrays.stream(client.getMapRegions())
			.filter(x -> x == 9043)
			.toArray().length > 0;
	}

	private void resetVars()
	{
		startTime = null;
		lastTime = null;
		started = false;
	}

	private void removeTimer()
	{
		infoBoxManager.removeInfoBox(timer);
		timer = null;
	}

	private void createTimer(Instant startTime, Instant lastTime)
	{
		if (timer != null)
		{
			infoBoxManager.removeInfoBox(timer);
		}

		if (checkInFightCaves())
		{
			timer = new BossTimeTracker(itemManager.getImage(FIRE_CAPE), this, startTime, lastTime);
			infoBoxManager.addInfoBox(timer);
		}
		else if (checkInInferno())
		{
			timer = new BossTimeTracker(itemManager.getImage(INFERNAL_CAPE), this, startTime, lastTime);
			infoBoxManager.addInfoBox(timer);
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		{
			this.last_nmstate = this.fight_timer = this.phase_timer = this.subph_timer = -1;
			Arrays.fill(this.phase_splits, -1);
			if (this.nib != null)
			{
				this.infoBoxManager.removeInfoBox(this.nib);
				this.nib = null;
			}
		}
		removeTimer();
		resetConfig();
		resetVars();
	}

	private void loadConfig()
	{
		startTime = configManager.getConfiguration(CONFIG_GROUP, CONFIG_TIME, Instant.class);
		started = configManager.getConfiguration(CONFIG_GROUP, CONFIG_STARTED, Boolean.class);
		lastTime = configManager.getConfiguration(CONFIG_GROUP, CONFIG_LASTTIME, Instant.class);
		if (started == null)
		{
			started = false;
		}
	}

	private void resetConfig()
	{
		configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_TIME);
		configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_STARTED);
		configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_LASTTIME);
	}

	private void saveConfig()
	{
		if (startTime == null)
		{
			return;
		}

		if (lastTime == null)
		{
			lastTime = Instant.now();
		}

		configManager.setConfiguration(CONFIG_GROUP, CONFIG_TIME, startTime);
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_STARTED, started);
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_LASTTIME, lastTime);
		resetVars();
	}
}
