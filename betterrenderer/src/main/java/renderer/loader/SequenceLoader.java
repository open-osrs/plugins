/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
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
package renderer.loader;

import renderer.cache.CacheSystem;
import renderer.model.AnimationDefinition;
import renderer.util.CacheBuffer;

public class SequenceLoader
{
	public static AnimationDefinition load(int id, byte[] b)
	{
		AnimationDefinition def = new AnimationDefinition(id);
		CacheBuffer is = new CacheBuffer(b);

		while (true)
		{
			int opcode = is.get() & 0xFF;
			if (opcode == 0)
			{
				break;
			}

			decodeValues(opcode, def, is);
		}

		return def;
	}

	private static void decodeValues(int opcode, AnimationDefinition def, CacheBuffer stream)
	{
		switch (opcode)
		{
			case 1:
				int frameCount = stream.getShort() & 0xFFFF;
				int[] frameLengths = new int[frameCount];
				for (int i = 0; i < frameCount; ++i)
				{
					frameLengths[i] = stream.getShort() & 0xFFFF;
				}
				int[] frameIDs = new int[frameCount];
				for (int i = 0; i < frameCount; ++i)
				{
					frameIDs[i] = stream.getShort() & 0xFFFF;
				}
				for (int i = 0; i < frameCount; ++i)
				{
					frameIDs[i] += (stream.getShort() & 0xFFFF) << 16;
				}
				for (int i = 0; i < frameCount; ++i)
				{
					def.frames.add(new AnimationDefinition.Frame(CacheSystem.getTransformDefiniton(frameIDs[i]), frameLengths[i]));
				}
				break;
			case 2:
				def.frameStep = stream.getShort() & 0xFFFF;
				break;
			case 3:
				int var3 = stream.get() & 0xFF;
				def.interleaveLeave = new int[1 + var3];
				for (int var4 = 0; var4 < var3; ++var4)
				{
					def.interleaveLeave[var4] = stream.get() & 0xFF;
				}
				def.interleaveLeave[var3] = 9999999;
				break;
			case 4:
				def.stretches = true;
				break;
			case 5:
				def.forcedPriority = stream.get() & 0xFF;
				break;
			case 6:
				def.leftHandItem = stream.getShort() & 0xFFFF;
				break;
			case 7:
				def.rightHandItem = stream.getShort() & 0xFFFF;
				break;
			case 8:
				def.maxLoops = stream.get() & 0xFF;
				break;
			case 9:
				def.precedenceAnimating = stream.get() & 0xFF;
				break;
			case 10:
				def.priority = stream.get() & 0xFF;
				break;
			case 11:
				def.replyMode = stream.get() & 0xFF;
				break;
			case 12:
				int chatFrameCount = stream.get() & 0xFF;
				int[] chatFrameIds = new int[chatFrameCount];
				for (int i = 0; i < chatFrameCount; ++i)
				{
					chatFrameIds[i] = stream.getShort() & 0xFFFF;
				}
				for (int i = 0; i < chatFrameCount; ++i)
				{
					chatFrameIds[i] += (stream.getShort() & 0xFFFF) << 16;
				}
				for (int i = 0; i < chatFrameCount; ++i)
				{
					def.chatFrames.add((chatFrameIds[i] & 0xffff) == 0xffff ? null : CacheSystem.getTransformDefiniton(chatFrameIds[i]));
				}
				break;
			case 13:
				int frameSoundCount = stream.get() & 0xFF;
				for (int i = 0; i < frameSoundCount; ++i)
				{
					int soundEffect = stream.getMedium();

					def.soundFrames.add(new AnimationDefinition.FrameSound(
						soundEffect >> 8,
						soundEffect >> 4 & 7,
						soundEffect & 15
					));
				}
				break;
		}
	}
}
