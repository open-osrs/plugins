package net.runelite.client.plugins.pktools;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;

import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 25, TimeUnit.SECONDS, queue,
		new ThreadPoolExecutor.DiscardPolicy());

	private static final Duration WAIT = Duration.ofSeconds(5);

	@Getter(AccessLevel.PUBLIC)
	@Setter(AccessLevel.PUBLIC)
	public int pietyVarbit, auguryVarbit, rigourVarbit,
		protectItemVarbit,
		mysticMightVarbit, eagleEyeVarbit,
		steelSkinVarbit, ultimateStrengthVarbit, incredibleReflexesVarbit, protectMeleeVarbit, protectMageVarbit, protectRangeVarbit;

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

	@Inject
	public ItemManager itemManager;

	@Getter(AccessLevel.PACKAGE)
	public Player lastEnemy;

	private Instant lastTime;

	//this is our current custom entry to swap in
	public MenuEntry entry;

	@Provides
	PkToolsConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(PkToolsConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(pkToolsOverlay);
		keyManager.registerKeyListener(pkToolsHotkeyListener);
	}

	@Override
	protected void shutDown() throws Exception
	{
		lastTime = null;
		overlayManager.remove(pkToolsOverlay);
		keyManager.unregisterKeyListener(pkToolsHotkeyListener);
	}

	@Subscribe
	public void onVarbitChanged(final VarbitChanged event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		setProtectMeleeVarbit((client.getVar(Prayer.PROTECT_FROM_MELEE.getVarbit()) == 1) ? 1 : 0);
		setProtectMageVarbit((client.getVar(Prayer.PROTECT_FROM_MAGIC.getVarbit()) == 1) ? 1 : 0);
		setProtectRangeVarbit((client.getVar(Prayer.PROTECT_FROM_MISSILES.getVarbit()) == 1) ? 1 : 0);

		setPietyVarbit((client.getVar(Prayer.PIETY.getVarbit()) == 1) ? 1 : 0);
		setAuguryVarbit((client.getVar(Prayer.AUGURY.getVarbit()) == 1) ? 1 : 0);
		setRigourVarbit((client.getVar(Prayer.RIGOUR.getVarbit()) == 1) ? 1 : 0);

		setProtectItemVarbit((client.getVar(Prayer.PROTECT_ITEM.getVarbit()) == 1) ? 1 : 0);

		setMysticMightVarbit((client.getVar(Prayer.MYSTIC_MIGHT.getVarbit()) == 1) ? 1 : 0);
		setEagleEyeVarbit((client.getVar(Prayer.EAGLE_EYE.getVarbit()) == 1) ? 1 : 0);

		setSteelSkinVarbit((client.getVar(Prayer.STEEL_SKIN.getVarbit()) == 1) ? 1 : 0);
		setUltimateStrengthVarbit((client.getVar(Prayer.ULTIMATE_STRENGTH.getVarbit()) == 1) ? 1 : 0);
		setIncredibleReflexesVarbit((client.getVar(Prayer.INCREDIBLE_REFLEXES.getVarbit()) == 1) ? 1 : 0);
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
			if (player == localPlayer.getInteracting())
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
		doAutoSwapPrayers();
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (entry != null)
		{
			event.setMenuEntry(entry);
		}
		entry = null;
	}

	public void lastEnemyTimer()
	{
		if (lastEnemy != null && client.getLocalPlayer().getInteracting() == null)
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

		entry = new MenuEntry("Activate", prayer_widget.getName(), 1, MenuOpcode.CC_OP.getId(), prayer_widget.getItemId(), prayer_widget.getId(), false);
		click();

		try
		{
			Thread.sleep(config.clickDelay());
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
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

		this.executor.submit(() -> {
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
		});
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
