package net.runelite.client.plugins.lavacrafter;

import net.runelite.api.widgets.WidgetInfo;

public enum LavaCrafterState
{
	USE_BANK_CHEST(20, "Use", "<col=ffff>Bank chest", 4483, 3, 52, 43, LavaCrafterStateType.GAME_OBJECT),
	DEPOSIT_LAVAS(10, "Deposit-All", "<col=ff9040>Lava rune</col>", 8, 57, 1, 983043, LavaCrafterStateType.INVENTORY_ITEM),
	WITHDRAW_TALISMAN(10, "Withdraw-1", "<col=ff9040>Earth talisman</col>", 1, 57, 36, 786444, LavaCrafterStateType.BANK_ITEM),
	WITHDRAW_ESSENCE(10, "Withdraw-All", "<col=ff9040>Pure essence</col>", 7, 57, 14, 786444, LavaCrafterStateType.BANK_ITEM),
	TELE_DUEL_ARENA(20, "Duel Arena", "<col=ff9040>Ring of dueling(4)</col>", 2, 57, -1, 25362455),
	ENTER_RUINS(20, "Enter", "<col=ffff>Mysterious ruins", 34817, 3, 48, 70, LavaCrafterStateType.GAME_OBJECT),
	CAST_MAGIC_IMBUE(10, "Cast", "<col=00ff00>Magic Imbue</col>", 1, 57, -1, 14286972),
	USE_EARTH_RUNES(1, "Use", "<col=ff9040>Earth rune", 557, 38, 0, WidgetInfo.INVENTORY.getId(), LavaCrafterStateType.INVENTORY_ITEM),
	USE_EARTHS_ON_ALTAR(20, "Use", "<col=ff9040>Earth rune<col=ffffff> -> <col=ffff>Altar", 34764, 1, 56, 37, LavaCrafterStateType.GAME_OBJECT),
	TELE_CASTLE_WARS(20, "Castle Wars", "<col=ff9040>Ring of dueling(4)</col>", 3, 57, -1, 25362455),
	WITHDRAW_DUELING_RING(10, "Withdraw-1", "<col=ff9040>Ring of dueling(8)</col>", 1, 57, 70, 786444, LavaCrafterStateType.BANK_ITEM),
	WEAR_DUELING_RING(10, "Wear", "<col=ff9040>Ring of dueling(8)</col>", 9, 1007, 2, 983043, LavaCrafterStateType.INVENTORY_ITEM),
	WITHDRAW_BINDING_NECKLACE(10, "Withdraw-1", "<col=ff9040>Binding necklace</col>", 1, 57, 15, 786444, LavaCrafterStateType.BANK_ITEM),
	WEAR_BINDING_NECKLACE(10, "Wear", "<col=ff9040>Binding necklace</col>", 9, 1007, 2, 983043, LavaCrafterStateType.INVENTORY_ITEM);

	String option;
	String target;
	int identifier;
	int opcode;
	int param0;
	int param1;

	int tickDelay;

	LavaCrafterStateType type = LavaCrafterStateType.DEFAULT;

	LavaCrafterState(int tickDelay, String option, String target, int identifier, int opcode, int param0, int param1)
	{
		this.tickDelay = tickDelay;
		this.option = option;
		this.target = target;
		this.identifier = identifier;
		this.opcode = opcode;
		this.param0 = param0;
		this.param1 = param1;
	}

	LavaCrafterState(int tickDelay, String option, String target, int identifier, int opcode, int param0, int param1, LavaCrafterStateType type)
	{
		this.tickDelay = tickDelay;
		this.option = option;
		this.target = target;
		this.identifier = identifier;
		this.opcode = opcode;
		this.param0 = param0;
		this.param1 = param1;
		this.type = type;
	}
}
