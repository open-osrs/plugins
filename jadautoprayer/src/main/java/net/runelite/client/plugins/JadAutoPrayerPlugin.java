package net.runelite.client.plugins.jadautoprayer;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Prayer;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.annotation.Nullable;
import javax.inject.Inject;

@Extension
@PluginDescriptor(
		name = "Jad Auto Prayer",
		description = "Auto click proper prayers against Jad.",
		tags = {"bosses", "combat", "minigame", "overlay", "prayer", "pve", "pvm", "jad", "firecape", "fight", "cave", "caves"},
		enabledByDefault = false,
		type = PluginType.MINIGAME
)
public class JadAutoPrayerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	ConfigManager configManager;

	@Inject
	private JadAutoPrayerConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private JadAutoPrayerOverlay overlay;

	@Getter(AccessLevel.PACKAGE)
	@Nullable
	private JadAttack attack;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	public int protectMageVarbit;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	public int protectRangeVarbit;

	private NPC jad;

	@Provides
	JadAutoPrayerConfig getConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(JadAutoPrayerConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		this.overlayManager.add(this.overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		this.overlayManager.remove(this.overlay);
		this.jad = null;
		this.attack = null;
	}

	@Subscribe
	public void onVarbitChanged(final VarbitChanged event)
	{
		if (this.client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (this.client.getVar(Prayer.PROTECT_FROM_MAGIC.getVarbit()) == 1)
		{
			this.setProtectMageVarbit(1);
		} else
		{
			this.setProtectMageVarbit(0);
		}

		if (this.client.getVar(Prayer.PROTECT_FROM_MISSILES.getVarbit()) == 1)
		{
			this.setProtectRangeVarbit(1);
		} else
		{
			this.setProtectRangeVarbit(0);
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		int id = event.getNpc().getId();

		if (id == NpcID.TZTOKJAD || id == NpcID.TZTOKJAD_6506)
		{
			this.jad = event.getNpc();
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (this.jad == event.getNpc())
		{
			this.jad = null;
			this.attack = null;
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (event.getActor() != this.jad)
		{
			return;
		}

		if (this.jad.getAnimation() == JadAttack.MAGIC.getAnimation())
		{
			this.attack = JadAttack.MAGIC;
		} else if (this.jad.getAnimation() == JadAttack.RANGE.getAnimation())
		{
			this.attack = JadAttack.RANGE;
		}
	}
}