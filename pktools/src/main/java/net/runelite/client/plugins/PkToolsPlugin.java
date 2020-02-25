package net.runelite.client.plugins.pktools;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Prayer;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@PluginDescriptor(
		name = "PKing Tools",
		description = "Arsenal of PKing Tools",
		tags = {"combat", "player", "enemy", "tracking", "overlay"},
		enabledByDefault = false,
		type = PluginType.EXTERNAL
)
public class PkToolsPlugin extends Plugin
{
	private static final Duration WAIT = Duration.ofSeconds(5);

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	public int protectMeleeVarbit;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	public int protectMageVarbit;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	public int protectRangeVarbit;

	@Getter(AccessLevel.PUBLIC)
	@Setter(AccessLevel.PUBLIC)
	public int pietyVarbit, auguryVarbit, rigourVarbit,
			protectItemVarbit,
			mysticMightVarbit, eagleEyeVarbit,
			steelSkinVarbit, ultimateStrengthVarbit, incredibleReflexesVarbit;

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
			return;

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
	public void onGameTick(final GameTick gameTick)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
			return;

		if (lastEnemy != null && client.getLocalPlayer().getInteracting() == null)
		{
			if (Duration.between(lastTime, Instant.now()).compareTo(PkToolsPlugin.WAIT) > 0)
			{
				lastEnemy = null;
			}
		}
	}
}
