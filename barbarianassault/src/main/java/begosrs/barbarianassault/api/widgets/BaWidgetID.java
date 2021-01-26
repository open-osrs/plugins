/*
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
package begosrs.barbarianassault.api.widgets;

/**
 * Utility class mapping widget IDs to global constants.
 * <p>
 * The constants defined directly under the {@link BaWidgetID} class are
 * Widget group IDs. All child IDs are defined in sub-classes relating
 * to their group.
 * <p>
 * For a more direct group-child widget mapping, use the
 * {@link BaWidgetInfo} enum class.
 */
public class BaWidgetID
{
	public static final int BA_HORN_OF_GLORY = 484;
	public static final int BA_ATTACKER_GROUP_ID = 485;
	public static final int BA_COLLECTOR_GROUP_ID = 486;
	public static final int BA_DEFENDER_GROUP_ID = 487;
	public static final int BA_HEALER_GROUP_ID = 488;
	public static final int BA_REWARD_GROUP_ID = 497;

	public static final int COMBAT_GROUP_ID = 593;

	static class BarbarianAssault
	{
		static class Attacker
		{
			static final int LISTEN_TOP = 8;
			static final int LISTEN_BOTTOM = 9;
			static final int TO_CALL = 11;
			static final int ROLE_SPRITE = 12;
			static final int ROLE = 13;
		}

		static class Healer
		{
			static final int TEAMMATE1 = 19;
			static final int TEAMMATE2 = 23;
			static final int TEAMMATE3 = 27;
			static final int TEAMMATE4 = 31;
		}

		static class HornOfGlory
		{
			static final int ATTACKER = 5;
			static final int DEFENDER = 6;
			static final int COLLECTOR = 7;
			static final int HEALER = 8;
		}

		static class RewardValues
		{
			static final int RUNNERS_PASSED = 14;
			static final int HITPOINTS_REPLENISHED = 19;
			static final int WRONG_POISON_PACKS_USED = 20;
			static final int EGGS_COLLECTED = 21;
			static final int FAILED_ATTACKS = 22;
			static final int RUNNERS_PASSED_POINTS = 24;
			static final int RANGERS_KILLED = 25;
			static final int FIGHTERS_KILLED = 26;
			static final int HEALERS_KILLED = 27;
			static final int RUNNERS_KILLED = 28;
			static final int HITPOINTS_REPLENISHED_POINTS = 29;
			static final int WRONG_POISON_PACKS_USED_POINTS = 30;
			static final int EGGS_COLLECTED_POINTS = 31;
			static final int FAILED_ATTACKS_POINTS = 32;
			static final int BASE_POINTS = 33;
		}

		static final int CALL_FLASH = 4;
		static final int CURRENT_WAVE = 6;
		static final int LISTEN = 8;
		static final int TO_CALL = 10;
		static final int ROLE_SPRITE = 11;
		static final int ROLE = 12;
		static final int REWARD_TEXT = 57;
	}

	static class Combat
	{
		static final int STYLE_ONE = 4;
		static final int STYLE_ONE_ICON = 6;
		static final int STYLE_ONE_TEXT = 7;
		static final int STYLE_TWO = 8;
		static final int STYLE_TWO_ICON = 10;
		static final int STYLE_TWO_TEXT = 11;
		static final int STYLE_THREE = 12;
		static final int STYLE_THREE_ICON = 14;
		static final int STYLE_THREE_TEXT = 15;
		static final int STYLE_FOUR = 16;
		static final int STYLE_FOUR_ICON = 18;
		static final int STYLE_FOUR_TEXT = 19;
	}

}
