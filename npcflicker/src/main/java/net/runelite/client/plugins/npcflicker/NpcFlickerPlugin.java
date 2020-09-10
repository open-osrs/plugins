package net.runelite.client.plugins.npcflicker;

import com.google.inject.Provides;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.GraphicID;
import net.runelite.api.Hitsplat;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.SpotAnimationChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.NPCManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "NPC Flicker",
	enabledByDefault = false,
	description = "Adds a timer on NPC's for their attacks and flinching.",
	tags = {"flinch", "npc"},
	type = PluginType.PVM
)
public class NpcFlickerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private NPCManager npcManager;

	@Inject
	private NpcFlickerConfig config;

	@Getter(AccessLevel.PACKAGE)
	private final Set<MemorizedNPC> memorizedNPCs = new HashSet<>();

	private WorldArea lastPlayerLocation;

	MenuEntry entry;

	@Provides
	NpcFlickerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NpcFlickerConfig.class);
	}

	@Override
	protected void startUp()
	{
	}

	@Override
	protected void shutDown()
	{
		memorizedNPCs.clear();
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned npcSpawned)
	{
		final NPC npc = npcSpawned.getNpc();
		final String npcName = npc.getName();

		if (npcName == null || !Arrays.asList(npc.getDefinition().getActions()).contains("Attack"))
		{
			return;
		}
		int AttackSpeed = npcManager.getAttackSpeed(npc.getId());
		if (AttackSpeed == 0)
		{
			AttackSpeed = 4;
		}
		memorizedNPCs.add(new MemorizedNPC(npc, AttackSpeed, npc.getWorldArea()));
	}

	@Subscribe
	private void onNpcDespawned(NpcDespawned npcDespawned)
	{
		final NPC npc = npcDespawned.getNpc();
		memorizedNPCs.removeIf(c -> c.getNpc() == npc);
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN ||
			event.getGameState() == GameState.HOPPING)
		{
			memorizedNPCs.clear();
		}
	}

	@Subscribe
	private void onHitsplatApplied(HitsplatApplied event)
	{
		if (event.getActor().getInteracting() != client.getLocalPlayer())
		{
			return;
		}
		final Hitsplat hitsplat = event.getHitsplat();
		if ((hitsplat.getHitsplatType() == Hitsplat.HitsplatType.DAMAGE_ME || hitsplat.getHitsplatType() == Hitsplat.HitsplatType.BLOCK_ME) && event.getActor() instanceof NPC)
		{
			for (MemorizedNPC mn : memorizedNPCs)
			{
				if (mn.getNpcIndex() != ((NPC) event.getActor()).getIndex())
				{
					continue;
				}
				if (mn.getStatus() == MemorizedNPC.Status.OUT_OF_COMBAT || (mn.getStatus() == MemorizedNPC.Status.IN_COMBAT && mn.getCombatTimerEnd() - client.getTickCount() < 1) || mn.getLastinteracted() == null)
				{
					mn.setStatus(MemorizedNPC.Status.FLINCHING);
					mn.setCombatTimerEnd(-1);
					if (config.isCustomAttSpeed())
					{
						mn.setFlinchTimerEnd(client.getTickCount() + config.getCustomAttSpeed() / 2 + 1);
					}
					else
					{
						mn.setFlinchTimerEnd(client.getTickCount() + mn.getAttackSpeed() / 2 + 1);
					}
				}
			}
		}

	}

	@Subscribe
	private void onSpotAnimationChanged(SpotAnimationChanged event)
	{
		if ((event.getActor().getSpotAnimation() == GraphicID.SPLASH) && event.getActor() instanceof NPC)
		{
			for (MemorizedNPC mn : memorizedNPCs)
			{
				if (mn.getNpcIndex() != ((NPC) event.getActor()).getIndex())
				{
					continue;
				}
				if (mn.getStatus() == MemorizedNPC.Status.OUT_OF_COMBAT || (mn.getStatus() == MemorizedNPC.Status.IN_COMBAT && mn.getCombatTimerEnd() - client.getTickCount() < 2) || event.getActor().getInteracting() == null)
				{
					mn.setStatus(MemorizedNPC.Status.FLINCHING);
					mn.setCombatTimerEnd(-1);
					if (config.isCustomAttSpeed())
					{
						mn.setFlinchTimerEnd(client.getTickCount() + config.getCustomAttSpeed() / 2 + 2);
					}
					else
					{
						mn.setFlinchTimerEnd(client.getTickCount() + mn.getAttackSpeed() / 2 + 2);
					}
				}
			}
		}
	}

	private void checkStatus()
	{
		if (lastPlayerLocation == null)
		{
			return;
		}
		for (MemorizedNPC npc : memorizedNPCs)
		{
			final double CombatTime = npc.getCombatTimerEnd() - client.getTickCount();
			final double FlinchTime = npc.getFlinchTimerEnd() - client.getTickCount();
			if (npc.getNpc().getWorldArea() == null)
			{
				continue;
			}
			if (npc.getNpc().getInteracting() == client.getLocalPlayer())
			{
				//Checks: will the NPC attack this tick?
				if (((npc.getNpc().getWorldArea().canMelee(client, lastPlayerLocation) && config.getRange() == 1) //Separate mechanics for meleerange-only NPC's because they have extra collisiondata checks (fences etc.) and can't attack diagonally
					|| (lastPlayerLocation.hasLineOfSightTo(client, npc.getNpc().getWorldArea()) && npc.getNpc().getWorldArea().distanceTo(lastPlayerLocation) <= config.getRange() && config.getRange() > 1))
					&& ((npc.getStatus() != MemorizedNPC.Status.FLINCHING && CombatTime < 9) || (npc.getStatus() == MemorizedNPC.Status.FLINCHING && FlinchTime < 2))
					&& npc.getNpc().getAnimation() != -1 //Failsafe, attacking NPC's always have an animation.
					&& !(npc.getLastnpcarea().distanceTo(lastPlayerLocation) == 0 && npc.getLastnpcarea() != npc.getNpc().getWorldArea())) //Weird mechanic: NPC's can't attack on the tick they do a random move
				{
					npc.setStatus(MemorizedNPC.Status.IN_COMBAT_DELAY);
					npc.setLastnpcarea(npc.getNpc().getWorldArea());
					npc.setLastinteracted(npc.getNpc().getInteracting());
					if (config.isCustomAttSpeed())
					{
						npc.setCombatTimerEnd(client.getTickCount() + config.getCustomAttSpeed() + 8);
					}
					else
					{
						npc.setCombatTimerEnd(client.getTickCount() + npc.getAttackSpeed() + 8);
					}
					continue;
				}
			}
			switch (npc.getStatus())
			{
				case IN_COMBAT:
					if (CombatTime < 2)
					{
						npc.setStatus(MemorizedNPC.Status.OUT_OF_COMBAT);
					}
					break;
				case IN_COMBAT_DELAY:
					if (CombatTime < 9)
					{
						npc.setStatus(MemorizedNPC.Status.IN_COMBAT);
					}
					break;
				case FLINCHING:
					if (FlinchTime < 2)
					{
						npc.setStatus(MemorizedNPC.Status.IN_COMBAT);
						npc.setCombatTimerEnd(client.getTickCount() + 8);
					}
			}
			npc.setLastnpcarea(npc.getNpc().getWorldArea());
			npc.setLastinteracted(npc.getNpc().getInteracting());
		}
	}

	@Subscribe
	private void onGameTick(GameTick event)
	{
		checkStatus();
		lastPlayerLocation = client.getLocalPlayer().getWorldArea();

		for (MemorizedNPC npc : memorizedNPCs)
		{
			if (npc.getNpc().getInteracting() == client.getLocalPlayer() || client.getLocalPlayer().getInteracting() == npc.getNpc())
			{
				if (npc.getTimeLeft() == 2)
				{
					activatePrayer(Prayer.PROTECT_FROM_MELEE);
				}
				else
				{
					deactivatePrayer(Prayer.PROTECT_FROM_MELEE);
				}
			}
		}
	}

	@Subscribe
	private void onClientTick(ClientTick event)
	{
		for (MemorizedNPC npc : memorizedNPCs)
		{
			if (npc.getNpc().getInteracting() == client.getLocalPlayer() || client.getLocalPlayer().getInteracting() == npc.getNpc())
			{
				switch (npc.getStatus())
				{
					case FLINCHING:
						npc.setTimeLeft(Math.max(0, npc.getFlinchTimerEnd() - client.getTickCount()));
						break;
					case IN_COMBAT_DELAY:
						npc.setTimeLeft(Math.max(0, npc.getCombatTimerEnd() - client.getTickCount() - 7));
						break;
					case IN_COMBAT:
						npc.setTimeLeft(Math.max(0, npc.getCombatTimerEnd() - client.getTickCount()));
						break;
					case OUT_OF_COMBAT:
					default:
						npc.setTimeLeft(0);
						break;
				}
			}
		}
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

	public void activatePrayer(Prayer prayer)
	{
		if (prayer == null)
		{
			return;
		}

		if (client.isPrayerActive(prayer))
		{
			return;
		}

		if (client.getVarbitValue(prayer.getVarbit().getId()) == 1)
		{
			return;
		}

		Widget prayer_widget = client.getWidget(prayer.getWidgetInfo());

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
	}

	public void deactivatePrayer(Prayer prayer)
	{
		if (prayer == null)
		{
			return;
		}

		if (!client.isPrayerActive(prayer))
		{
			return;
		}

		if (client.getVarbitValue(prayer.getVarbit().getId()) != 1)
		{
			return;
		}

		Widget prayer_widget = client.getWidget(prayer.getWidgetInfo());

		if (prayer_widget == null)
		{
			return;
		}

		if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
		{
			return;
		}

		entry = new MenuEntry("Deactivate", prayer_widget.getName(), 1, MenuOpcode.CC_OP.getId(), prayer_widget.getItemId(), prayer_widget.getId(), false);
		click();
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