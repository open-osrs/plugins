/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * Copyright (c) 2020, BegOsrs <https://github.com/begosrs>
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
package net.runelite.client.plugins.barbarianassault;

import net.runelite.api.widgets.Widget;

/**
 * Represents a group-child {@link Widget} relationship.
 * <p>
 * For getting a specific widget from the client, see {@link net.runelite.api.Client#getWidget(int, int)}.
 */
public enum BaWidgetInfo
{
	BA_ATTACKER_CALL_FLASH(BaWidgetID.BA_ATTACKER_GROUP_ID, BaWidgetID.BarbarianAssault.CALL_FLASH),
	BA_ATTACKER_WAVE_TEXT(BaWidgetID.BA_ATTACKER_GROUP_ID, BaWidgetID.BarbarianAssault.CURRENT_WAVE),
	BA_ATTACKER_CALL_TEXT(BaWidgetID.BA_ATTACKER_GROUP_ID, BaWidgetID.BarbarianAssault.Attacker.TO_CALL),
	BA_ATTACKER_LISTEN_TOP_TEXT(BaWidgetID.BA_ATTACKER_GROUP_ID, BaWidgetID.BarbarianAssault.Attacker.LISTEN_TOP),
	BA_ATTACKER_ROLE_TEXT(BaWidgetID.BA_ATTACKER_GROUP_ID, BaWidgetID.BarbarianAssault.Attacker.ROLE),
	BA_ATTACKER_ROLE_SPRITE(BaWidgetID.BA_ATTACKER_GROUP_ID, BaWidgetID.BarbarianAssault.Attacker.ROLE_SPRITE),

	BA_COLLECTOR_CALL_FLASH(BaWidgetID.BA_COLLECTOR_GROUP_ID, BaWidgetID.BarbarianAssault.CALL_FLASH),
	BA_COLLECTOR_WAVE_TEXT(BaWidgetID.BA_COLLECTOR_GROUP_ID, BaWidgetID.BarbarianAssault.CURRENT_WAVE),
	BA_COLLECTOR_CALL_TEXT(BaWidgetID.BA_COLLECTOR_GROUP_ID, BaWidgetID.BarbarianAssault.TO_CALL),
	BA_COLLECTOR_LISTEN_TEXT(BaWidgetID.BA_COLLECTOR_GROUP_ID, BaWidgetID.BarbarianAssault.LISTEN),
	BA_COLLECTOR_ROLE_TEXT(BaWidgetID.BA_COLLECTOR_GROUP_ID, BaWidgetID.BarbarianAssault.ROLE),
	BA_COLLECTOR_ROLE_SPRITE(BaWidgetID.BA_COLLECTOR_GROUP_ID, BaWidgetID.BarbarianAssault.ROLE_SPRITE),

	BA_DEFENDER_CALL_FLASH(BaWidgetID.BA_DEFENDER_GROUP_ID, BaWidgetID.BarbarianAssault.CALL_FLASH),
	BA_DEFENDER_WAVE_TEXT(BaWidgetID.BA_DEFENDER_GROUP_ID, BaWidgetID.BarbarianAssault.CURRENT_WAVE),
	BA_DEFENDER_CALL_TEXT(BaWidgetID.BA_DEFENDER_GROUP_ID, BaWidgetID.BarbarianAssault.TO_CALL),
	BA_DEFENDER_LISTEN_TEXT(BaWidgetID.BA_DEFENDER_GROUP_ID, BaWidgetID.BarbarianAssault.LISTEN),
	BA_DEFENDER_ROLE_TEXT(BaWidgetID.BA_DEFENDER_GROUP_ID, BaWidgetID.BarbarianAssault.ROLE),
	BA_DEFENDER_ROLE_SPRITE(BaWidgetID.BA_DEFENDER_GROUP_ID, BaWidgetID.BarbarianAssault.ROLE_SPRITE),

	BA_HEALER_CALL_FLASH(BaWidgetID.BA_HEALER_GROUP_ID, BaWidgetID.BarbarianAssault.CALL_FLASH),
	BA_HEALER_WAVE_TEXT(BaWidgetID.BA_HEALER_GROUP_ID, BaWidgetID.BarbarianAssault.CURRENT_WAVE),
	BA_HEALER_CALL_TEXT(BaWidgetID.BA_HEALER_GROUP_ID, BaWidgetID.BarbarianAssault.TO_CALL),
	BA_HEALER_LISTEN_TEXT(BaWidgetID.BA_HEALER_GROUP_ID, BaWidgetID.BarbarianAssault.LISTEN),
	BA_HEALER_ROLE_TEXT(BaWidgetID.BA_HEALER_GROUP_ID, BaWidgetID.BarbarianAssault.ROLE),
	BA_HEALER_ROLE_SPRITE(BaWidgetID.BA_HEALER_GROUP_ID, BaWidgetID.BarbarianAssault.ROLE_SPRITE),

	BA_HEAL_TEAMMATE1(BaWidgetID.BA_HEALER_GROUP_ID, BaWidgetID.BarbarianAssault.Healer.TEAMMATE1),
	BA_HEAL_TEAMMATE2(BaWidgetID.BA_HEALER_GROUP_ID, BaWidgetID.BarbarianAssault.Healer.TEAMMATE2),
	BA_HEAL_TEAMMATE3(BaWidgetID.BA_HEALER_GROUP_ID, BaWidgetID.BarbarianAssault.Healer.TEAMMATE3),
	BA_HEAL_TEAMMATE4(BaWidgetID.BA_HEALER_GROUP_ID, BaWidgetID.BarbarianAssault.Healer.TEAMMATE4),

	BA_HORN_OF_GLORY_ATTACKER_LISTEN_TEXT(BaWidgetID.BA_HORN_OF_GLORY, BaWidgetID.BarbarianAssault.HornOfGlory.ATTACKER),
	BA_HORN_OF_GLORY_COLLECTOR_LISTEN_TEXT(BaWidgetID.BA_HORN_OF_GLORY, BaWidgetID.BarbarianAssault.HornOfGlory.COLLECTOR),
	BA_HORN_OF_GLORY_DEFENDER_LISTEN_TEXT(BaWidgetID.BA_HORN_OF_GLORY, BaWidgetID.BarbarianAssault.HornOfGlory.DEFENDER),
	BA_HORN_OF_GLORY_HEALER_LISTEN_TEXT(BaWidgetID.BA_HORN_OF_GLORY, BaWidgetID.BarbarianAssault.HornOfGlory.HEALER),

	BA_REWARD_TEXT(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.REWARD_TEXT),
	BA_RUNNERS_PASSED(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.RewardValues.RUNNERS_PASSED),
	BA_HITPOINTS_REPLENISHED(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.RewardValues.HITPOINTS_REPLENISHED),
	BA_WRONG_POISON_PACKS(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.RewardValues.WRONG_POISON_PACKS_USED),
	BA_EGGS_COLLECTED(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.RewardValues.EGGS_COLLECTED),
	BA_FAILED_ATTACKS(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.RewardValues.FAILED_ATTACKS),
	BA_RUNNERS_PASSED_POINTS(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.RewardValues.RUNNERS_PASSED_POINTS),
	BA_RANGERS_KILLED(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.RewardValues.RANGERS_KILLED),
	BA_FIGHTERS_KILLED(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.RewardValues.FIGHTERS_KILLED),
	BA_HEALERS_KILLED(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.RewardValues.HEALERS_KILLED),
	BA_RUNNERS_KILLED(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.RewardValues.RUNNERS_KILLED),
	BA_HITPOINTS_REPLENISHED_POINTS(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.RewardValues.HITPOINTS_REPLENISHED_POINTS),
	BA_WRONG_POISON_PACKS_POINTS(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.RewardValues.WRONG_POISON_PACKS_USED_POINTS),
	BA_EGGS_COLLECTED_POINTS(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.RewardValues.EGGS_COLLECTED_POINTS),
	BA_FAILED_ATTACKS_POINTS(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.RewardValues.FAILED_ATTACKS_POINTS),
	BA_BASE_POINTS(BaWidgetID.BA_REWARD_GROUP_ID, BaWidgetID.BarbarianAssault.RewardValues.BASE_POINTS),

	COMBAT_STYLE_ONE(BaWidgetID.COMBAT_GROUP_ID, BaWidgetID.Combat.STYLE_ONE),
	COMBAT_STYLE_ONE_ICON(BaWidgetID.COMBAT_GROUP_ID, BaWidgetID.Combat.STYLE_ONE_ICON),
	COMBAT_STYLE_ONE_TEXT(BaWidgetID.COMBAT_GROUP_ID, BaWidgetID.Combat.STYLE_ONE_TEXT),
	COMBAT_STYLE_TWO(BaWidgetID.COMBAT_GROUP_ID, BaWidgetID.Combat.STYLE_TWO),
	COMBAT_STYLE_TWO_ICON(BaWidgetID.COMBAT_GROUP_ID, BaWidgetID.Combat.STYLE_TWO_ICON),
	COMBAT_STYLE_TWO_TEXT(BaWidgetID.COMBAT_GROUP_ID, BaWidgetID.Combat.STYLE_TWO_TEXT),
	COMBAT_STYLE_THREE(BaWidgetID.COMBAT_GROUP_ID, BaWidgetID.Combat.STYLE_THREE),
	COMBAT_STYLE_THREE_ICON(BaWidgetID.COMBAT_GROUP_ID, BaWidgetID.Combat.STYLE_THREE_ICON),
	COMBAT_STYLE_THREE_TEXT(BaWidgetID.COMBAT_GROUP_ID, BaWidgetID.Combat.STYLE_THREE_TEXT),
	COMBAT_STYLE_FOUR(BaWidgetID.COMBAT_GROUP_ID, BaWidgetID.Combat.STYLE_FOUR),
	COMBAT_STYLE_FOUR_ICON(BaWidgetID.COMBAT_GROUP_ID, BaWidgetID.Combat.STYLE_FOUR_ICON),
	COMBAT_STYLE_FOUR_TEXT(BaWidgetID.COMBAT_GROUP_ID, BaWidgetID.Combat.STYLE_FOUR_TEXT);


	private final int groupId;
	private final int childId;

	BaWidgetInfo(int groupId, int childId)
	{
		this.groupId = groupId;
		this.childId = childId;
	}

	/**
	 * Gets the ID of the group-child pairing.
	 *
	 * @return the ID
	 */
	public int getId()
	{
		return groupId << 16 | childId;
	}

	/**
	 * Gets the group ID of the pair.
	 *
	 * @return the group ID
	 */
	public int getGroupId()
	{
		return groupId;
	}

	/**
	 * Gets the ID of the child in the group.
	 *
	 * @return the child ID
	 */
	public int getChildId()
	{
		return childId;
	}

	/**
	 * Gets the packed widget ID.
	 *
	 * @return the packed ID
	 */
	public int getPackedId()
	{
		return groupId << 16 | childId;
	}

	/**
	 * Utility method that converts an ID returned by {@link #getId()} back
	 * to its group ID.
	 *
	 * @param id passed group-child ID
	 * @return the group ID
	 */
	public static int TO_GROUP(int id)
	{
		return id >>> 16;
	}

	/**
	 * Utility method that converts an ID returned by {@link #getId()} back
	 * to its child ID.
	 *
	 * @param id passed group-child ID
	 * @return the child ID
	 */
	public static int TO_CHILD(int id)
	{
		return id & 0xFFFF;
	}

	/**
	 * Packs the group and child IDs into a single integer.
	 *
	 * @param groupId the group ID
	 * @param childId the child ID
	 * @return the packed ID
	 */
	public static int PACK(int groupId, int childId)
	{
		return groupId << 16 | childId;
	}

}
