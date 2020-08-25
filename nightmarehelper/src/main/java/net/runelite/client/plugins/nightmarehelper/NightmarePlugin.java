package net.runelite.client.plugins.nightmarehelper;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDefinitionChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Nightmare Auto Prayer",
	enabledByDefault = false,
	description = "Automatically swap prayers in Nightmare of Ashihama",
	tags = {"bosses", "combat", "nm", "overlay", "nightmare", "pve", "pvm", "ashihama", "prayer", "pray", "ben", "ben93riggs"},
	type = PluginType.PVM
)

@Slf4j
@Singleton
public class NightmarePlugin extends Plugin
{
	@Inject
	private Client client;

	@Nullable
	private NPC nm;

	private boolean inFight;
	private boolean cursed;
	private int attacksSinceCurse;

	private MenuEntry entry;

	public NightmarePlugin()
	{
		inFight = false;
	}

	@Override
	protected void startUp()
	{
		reset();
	}

	@Override
	protected void shutDown()
	{
		reset();
	}

	private void reset()
	{
		inFight = false;
		nm = null;
		cursed = false;
		attacksSinceCurse = 0;
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (!inFight || nm == null)
		{
			return;
		}

		Actor actor = event.getActor();
		if (!(actor instanceof NPC))
		{
			return;
		}

		NPC npc = (NPC) actor;
		int animationId = npc.getAnimation();

		switch (animationId)
		{
			case NightmareAttackAnimations.NIGHTMARE_MAGIC_ATTACK:
				attacksSinceCurse++;
				activatePrayer(cursed ? Prayer.PROTECT_FROM_MELEE : Prayer.PROTECT_FROM_MAGIC);
				break;
			case NightmareAttackAnimations.NIGHTMARE_MELEE_ATTACK:
				attacksSinceCurse++;
				activatePrayer(cursed ? Prayer.PROTECT_FROM_MISSILES : Prayer.PROTECT_FROM_MELEE);
				break;
			case NightmareAttackAnimations.NIGHTMARE_RANGE_ATTACK:
				attacksSinceCurse++;
				activatePrayer(cursed ? Prayer.PROTECT_FROM_MAGIC : Prayer.PROTECT_FROM_MISSILES);
				break;
			case NightmareAttackAnimations.NIGHTMARE_CURSE:
				cursed = true;
				attacksSinceCurse = 0;
				break;
			default:
				break;
		}

		if (cursed && attacksSinceCurse == 5)
		{
			//curse is removed when she phases, or does 5 attacks
			cursed = false;
			attacksSinceCurse = -1;
		}
	}

	@Subscribe
	public void onNpcDefinitionChanged(NpcDefinitionChanged event)
	{
		final NPC npc = event.getNpc();

		if (npc == null)
		{
			return;
		}

		//this will trigger once when the fight begins
		if (npc.getId() == NpcID.THE_NIGHTMARE_9432)
		{
			//reset everything
			reset();
			nm = npc;
			inFight = true;
		}

		//if ID changes to 9431 (3rd phase) and is cursed, remove the curse
		if (cursed && npc.getId() == NpcID.THE_NIGHTMARE_9431)
		{
			cursed = false;
			attacksSinceCurse = -1;
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		GameState gamestate = event.getGameState();

		//if loading happens while inFight, the user has left the area (either via death or teleporting).
		if (gamestate == GameState.LOADING && inFight)
		{
			reset();
		}
	}

	@Subscribe
	private void onGameTick(final GameTick event)
	{
		if (!inFight || nm == null)
		{
			return;
		}

		//if nightmare's id is 9433, the fight has ended and everything should be reset
		if (nm.getId() == NpcID.THE_NIGHTMARE_9433)
		{
			reset();
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
