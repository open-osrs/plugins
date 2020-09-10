package net.runelite.client.plugins.npcflicker;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldArea;

@Getter(AccessLevel.PACKAGE)
public class MemorizedNPC
{
	private NPC npc;
	private int npcIndex;
	private String npcName;
	private int attackSpeed;
	@Setter(AccessLevel.PACKAGE)
	private int combatTimerEnd;
	@Setter(AccessLevel.PACKAGE)
	private int timeLeft;
	@Setter(AccessLevel.PACKAGE)
	private int flinchTimerEnd;
	@Setter(AccessLevel.PACKAGE)
	private Status status;
	@Setter(AccessLevel.PACKAGE)
	private WorldArea lastnpcarea;
	@Setter(AccessLevel.PACKAGE)
	private Actor lastinteracted;

	MemorizedNPC(final NPC npc, final int attackSpeed, final WorldArea worldArea)
	{
		this.npc = npc;
		this.npcIndex = npc.getIndex();
		this.npcName = npc.getName();
		this.attackSpeed = attackSpeed;
		this.combatTimerEnd = -1;
		this.flinchTimerEnd = -1;
		this.timeLeft = 0;
		this.status = Status.OUT_OF_COMBAT;
		this.lastnpcarea = worldArea;
		this.lastinteracted = null;
	}

	@Getter(AccessLevel.PACKAGE)
	@AllArgsConstructor
	enum Status
	{
		FLINCHING("Flinching"),
		IN_COMBAT_DELAY("In Combat Delay"),
		IN_COMBAT("In Combat"),
		OUT_OF_COMBAT("Out of Combat");

		private String name;
	}
}