package net.runelite.client.plugins.pktools;

import com.google.inject.Provides;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.AccessLevel;
import lombok.Getter;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.pktools.ScriptCommand.ScriptCommand;
import net.runelite.client.ui.overlay.OverlayManager;

import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Extension
@PluginDescriptor(
	name = "PKing Tools",
	description = "Arsenal of PKing Tools",
	tags = {"combat", "player", "enemy", "tracking", "overlay"},
	enabledByDefault = false,
	type = PluginType.PVP
)
public class PkToolsPlugin extends Plugin
{
	private static final Duration WAIT = Duration.ofSeconds(5);

	public Queue<ScriptCommand> commandList = new ConcurrentLinkedQueue<>();
	public Queue<MenuEntry> entryList = new ConcurrentLinkedQueue<>();

	@Inject
	public Client client;

	@Inject
	private PkToolsConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PkToolsOverlay pkToolsOverlay;

	@Inject
	private PkToolsHotkeyListener pkToolsHotkeyListener;

	@Inject
	private KeyManager keyManager;

	@Getter(AccessLevel.PACKAGE)
	public Player lastEnemy;

	private Instant lastTime;

	@Provides
	PkToolsConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(PkToolsConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(pkToolsOverlay);
		keyManager.registerKeyListener(pkToolsHotkeyListener);
	}

	@Override
	protected void shutDown()
	{
		lastTime = null;
		overlayManager.remove(pkToolsOverlay);
		keyManager.unregisterKeyListener(pkToolsHotkeyListener);
	}

	@Subscribe
	public void onInteractingChanged(final InteractingChanged event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (event.getSource() != client.getLocalPlayer())
		{
			return;
		}

		final Actor opponent = event.getTarget();

		if (opponent == null)
		{
			lastTime = Instant.now();
			return;
		}

		Player localPlayer = client.getLocalPlayer();
		final List<Player> players = client.getPlayers();

		for (final Player player : players)
		{
			if (localPlayer != null && player == localPlayer.getInteracting())
			{
				lastEnemy = player;
			}
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		lastEnemyTimer();

		processCommands();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		doAutoSwapPrayers();
	}

	private void processCommands()
	{
		while (commandList.peek() != null)
		{
			commandList.poll().execute(client, config, this, configManager);
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (entryList != null && !entryList.isEmpty())
		{
			event.setMenuEntry(entryList.poll());
			handleHotkeyTasks();
		}
	}

	public void handleHotkeyTasks()
	{
		if (entryList == null || entryList.isEmpty())
		{
			return;
		}

		click();
	}

	public void lastEnemyTimer()
	{
		Player localPlayer = client.getLocalPlayer();

		if (localPlayer == null)
		{
			return;
		}

		if (lastEnemy == null)
		{
			return;
		}

		if (localPlayer.getInteracting() == null)
		{
			if (Duration.between(lastTime, Instant.now()).compareTo(PkToolsPlugin.WAIT) > 0)
			{
				lastEnemy = null;
			}
		}
	}

	public void activatePrayer(WidgetInfo widgetInfo)
	{
		Widget prayer_widget = client.getWidget(widgetInfo);

		if (prayer_widget == null)
		{
			return;
		}

		if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
		{
			return;
		}

		entryList.add(new MenuEntry("Activate", prayer_widget.getName(), 1, MenuOpcode.CC_OP.getId(), prayer_widget.getItemId(), prayer_widget.getId(), false));
		click();
	}

	public void doAutoSwapPrayers()
	{
		if (!config.autoPrayerSwitcher())
		{
			return;
		}

		if (!config.autoPrayerSwitcherEnabled())
		{
			return;
		}

		try
		{
			boolean PROTECT_MELEE = client.getVar(Prayer.PROTECT_FROM_MELEE.getVarbit()) != 0;
			boolean PROTECT_RANGED = client.getVar(Prayer.PROTECT_FROM_MISSILES.getVarbit()) != 0;
			boolean PROTECT_MAGIC = client.getVar(Prayer.PROTECT_FROM_MAGIC.getVarbit()) != 0;

			if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
			{
				return;
			}

			if (lastEnemy == null)
			{
				return;
			}

			PlayerAppearance lastEnemyAppearance = lastEnemy.getPlayerAppearance();

			if (lastEnemyAppearance == null)
			{
				return;
			}

			int WEAPON_INT = lastEnemyAppearance.getEquipmentId(KitType.WEAPON);

			if (WEAPON_INT <= 0)
			{
				return;
			}

			if (Arrays.stream(PkToolsOverlay.MELEE_LIST).anyMatch(x -> x == WEAPON_INT) && !PROTECT_MELEE)
			{
				activatePrayer(WidgetInfo.PRAYER_PROTECT_FROM_MELEE);
			}
			else if (Arrays.stream(PkToolsOverlay.RANGED_LIST).anyMatch(x -> x == WEAPON_INT) && !PROTECT_RANGED)
			{
				activatePrayer(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES);
			}
			else if (Arrays.stream(PkToolsOverlay.MAGIC_LIST).anyMatch(x -> x == WEAPON_INT) && !PROTECT_MAGIC)
			{
				activatePrayer(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void click()
	{
		Point pos = client.getMouseCanvasPosition();

		if (client.isStretchedEnabled())
		{
			final Dimension stretched = client.getStretchedDimensions();
			final Dimension real = client.getRealDimensions();
			final double width = (stretched.width / real.getWidth());
			final double height = (stretched.height / real.getHeight());
			final Point point = new Point((int) (pos.getX() * width), (int) (pos.getY() * height));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			return;
		}

		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
	}
}
