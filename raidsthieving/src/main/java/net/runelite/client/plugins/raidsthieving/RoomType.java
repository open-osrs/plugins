/*
 * Copyright (c) 2020, chestnut1693 <chestnut1693@gmail.com>
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
package net.runelite.client.plugins.raidsthieving;

import lombok.AccessLevel;
import lombok.Getter;

public enum RoomType
{
	LEFT(64, new int[][]{
		{0, 15, 16, 54},
		{0, 16, 37, 53},
		{1, 6, 20, 36},
		{2, 4, 18, 29},
		{2, 10, 14, 39},
		{3, 8, 18, 44},    //Could be instead of (4, 8, 18, 44).
		{3, 21, 26, 45},
		{4, 8, 18, 44},
		{5, 23, 25, 40},
		{5, 25, 31, 51},
		{6, 12, 43, 58},
		{7, 8, 39, 45},
		{7, 9, 27, 32},
		{7, 13, 40, 42},
		{7, 30, 46, 49},
		{9, 29, 31, 58},
		{9, 34, 53, 62},
		{11, 12, 41, 53},
		{11, 39, 52, 55},
		{12, 21, 26, 45},
		{13, 17, 22, 50},
		{13, 34, 53, 62},    //Could be instead of (9, 34, 53, 62).
		{14, 15, 41, 44},
		{14, 42, 43, 57},
		{19, 24, 31, 33},
		{19, 27, 50, 61},
		{19, 28, 44, 50},
		{20, 38, 40, 57},
		{21, 24, 53, 63},
		{22, 30, 46, 54},
		{22, 32, 36, 59},
		{23, 33, 47, 54},
		{25, 26, 49, 62},
		{28, 38, 40, 60},
		{32, 45, 51, 56},
		{33, 44, 48, 59},
		{35, 39, 41, 61},
		{36, 37, 50, 63},
		{47, 52, 54, 55},
	}),
	RIGHT(74, new int[][]{
		{0, 6, 23, 54},
		{0, 9, 20, 34},
		{1, 14, 16, 43},
		{2, 8, 21, 51},
		{2, 10, 20, 33},
		{3, 13, 18, 36},
		{3, 16, 17, 40},
		{4, 7, 29, 60},
		{4, 12, 22, 35},
		{5, 11, 53, 55},
		{5, 12, 26},    //Incomplete.
		{6, 23, 41, 72},
		{7, 8, 25, 27},
		{9, 28, 32},    //Incomplete.
		{10, 43, 46, 56},
		{11, 39, 57, 69},
		{13, 18, 45, 52},
		{14, 19, 32, 73},
		{15, 21, 39, 48},
		{17, 19, 44, 47},
		{24, 53, 61, 63},
		{26, 53, 69, 71},
		{26, 53, 69},    //Incomplete.
		{27, 29, 57, 67},
		{27, 36, 70},    //Incomplete.
		{28, 31, 43, 71},
		{30, 43, 64, 72},
		{30, 54},    //Incomplete.
		{32, 34, 61, 66},
		{33, 42, 55, 65},
		{33, 46, 68, 73},
		{35, 37, 56, 63},
		{35, 38, 48, 58},
		{39, 51, 59, 65},
		{40, 47, 60, 62},
		{41, 50, 54, 70},
		{42, 45, 52, 58},
		{44, 50, 54, 66},
	}),
	STRAIGHT(66, new int[][]{
		{0, 39, 43, 51},
		{1, 15, 20, 53},
		{2, 10, 42, 44},
		{3, 21, 54},    //Incomplete.
		{4, 14, 38, 52},
		{5, 6, 35, 41},
		{7, 16, 34, 49},
		{9, 12, 26, 27},
		{13, 25, 30, 31},
		{17, 24, 34, 58},
		{18, 23, 35, 57},
		{19, 26, 47, 65},
		{21, 33, 36, 61},
		{22, 25, 46, 55},
		{28, 40, 52, 63},
		{29, 41, 42, 64},
		{30, 32, 37, 62},
		{43, 45, 50, 60},
		{51, 53, 56, 59},
	});

	@Getter(AccessLevel.PACKAGE)
	private final int chestCount;

	@Getter(AccessLevel.PACKAGE)
	private final int[][] solutionSets;

	RoomType(int chestCount, int[][] solutionSets)
	{
		this.chestCount = chestCount;
		this.solutionSets = solutionSets;
	}
}