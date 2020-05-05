/*
 * Copyright (c) 2019, 7ate9 <https://github.com/se7enAte9>
 * Copyright (c) 2019, https://openosrs.com
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

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import net.runelite.api.NPC;


@Data
class Healer
{
	@Getter(AccessLevel.NONE)
	private static final List<List<int[]>> CODES = List.of(
		// List.of(firstCallFood, secondCallFood, lastFoodTime),
		List.of(new int[]{1, 1}, new int[]{0, 0}, new int[]{0, 0}),
		List.of(new int[]{2, 3, 4}, new int[]{0, 0, 0}, new int[]{24, 0, 0}),
		List.of(new int[]{1, 6, 2}, new int[]{0, 0, 3}, new int[]{0, 0, 0}),
		List.of(new int[]{2, 5, 2, 0}, new int[]{1, 0, 1, 6}, new int[]{36, 0, 36, 0}),
		List.of(new int[]{2, 7, 2, 1, 0}, new int[]{0, 0, 0, 2, 6}, new int[]{18, 0, 24, 0, 0}),
		List.of(new int[]{3, 5, 2, 2, 0, 0}, new int[]{0, 0, 0, 1, 10, 11}, new int[]{18, 0, 24, 27, 0, 0}),
		List.of(new int[]{3, 5, 6, 1, 0, 0, 0}, new int[]{0, 0, 0, 1, 1, 6, 4}, new int[]{21, 27, 0, 0, 0, 0, 0}),
		List.of(new int[]{2, 8, 1, 1, 0, 0, 0}, new int[]{1, 0, 1, 1, 3, 1, 0}, new int[]{36, 0, 0, 39, 45, 0, 0}),
		List.of(new int[]{2, 8, 1, 1, 0, 0, 0, 0}, new int[]{1, 0, 1, 1, 1, 1, 1, 0}, new int[]{33, 0, 33, 42, 0, 0, 0, 0, 0}),
		List.of(new int[]{2, 5, 1, 1, 0, 0, 0}, new int[]{1, 0, 1, 1, 3, 1, 0}, new int[]{33, 0, 0, 33, 48, 0, 0}));

	private final NPC npc;

	private int wave;

	private int spawnNumber;

	private int foodRemaining;

	private int lastFoodTime;

	private int firstCallFood;

	private int secondCallFood;

	private Instant timeLastPoisoned = null;

	Healer(NPC npc, int spawnNumber, int wave)
	{
		this.npc = npc;
		this.wave = wave;
		this.spawnNumber = spawnNumber;
		List<int[]> code = CODES.get(wave - 1);
		this.firstCallFood = code.get(0)[spawnNumber];
		this.secondCallFood = code.get(1)[spawnNumber];
		this.lastFoodTime = code.get(2)[spawnNumber];
		this.foodRemaining = firstCallFood + secondCallFood;
	}

	int timeToPoison()
	{
		if (timeLastPoisoned == null)
		{
			return -1;
		}
		else
		{
			long time = Duration.between(timeLastPoisoned, Instant.now()).getSeconds();
			return time > 20 ? 0 : (int) (20 - time);
		}
	}
}
