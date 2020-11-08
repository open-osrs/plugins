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
import renderer.model.TransformDefinition;
import renderer.util.CacheBuffer;

public class TransformLoader
{
	public static TransformDefinition loadTransform(int id, byte[] b)
	{
		TransformDefinition transform = new TransformDefinition();
		transform.id = id;

		CacheBuffer in = new CacheBuffer(b);
		CacheBuffer data = new CacheBuffer(b);

		int framemapArchiveIndex = in.getShort() & 0xFFFF;
		SkeletonDefinition skeleton = CacheSystem.getSkeletonDefinition(framemapArchiveIndex);

		int length = in.get() & 0xFF;

		data.position(data.position() + 3 + length);

		int[] indexFrameIds = new int[500];
		int[] dx = new int[500];
		int[] dy = new int[500];
		int[] dz = new int[500];

		int lastI = -1;
		int index = 0;
		for (int i = 0; i < length; ++i)
		{
			int var9 = in.get() & 0xFF;

			if (var9 <= 0)
			{
				continue;
			}

			if (skeleton.types[i] != 0)
			{
				for (int var10 = i - 1; var10 > lastI; --var10)
				{
					if (skeleton.types[var10] == 0)
					{
						indexFrameIds[index] = var10;
						dx[index] = 0;
						dy[index] = 0;
						dz[index] = 0;
						++index;
						break;
					}
				}
			}

			indexFrameIds[index] = i;
			short var11 = 0;
			if (skeleton.types[i] == 3)
			{
				var11 = 128;
			}

			if ((var9 & 1) != 0)
			{
				dx[index] = data.getSpecial1();
			}
			else
			{
				dx[index] = var11;
			}

			if ((var9 & 2) != 0)
			{
				dy[index] = data.getSpecial1();
			}
			else
			{
				dy[index] = var11;
			}

			if ((var9 & 4) != 0)
			{
				dz[index] = data.getSpecial1();
			}
			else
			{
				dz[index] = var11;
			}

			lastI = i;
			++index;
			if (skeleton.types[i] == 5)
			{
				transform.showing = true;
			}
		}

		if (data.position() != b.length)
		{
			throw new RuntimeException();
		}

		for (int i = 0; i < index; ++i)
		{
			transform.transforms.add(new TransformDefinition.Transform(
				skeleton.types[indexFrameIds[i]],
				skeleton.labels[i],
				dx[i],
				dy[i],
				dz[i]
			));
		}

		return transform;
	}

	public static SkeletonDefinition loadSkeleton(int id, byte[] b)
	{
		SkeletonDefinition def = new SkeletonDefinition();
		CacheBuffer in = new CacheBuffer(b);

		def.id = id;

		def.length = in.get() & 0xFF;
		def.types = new int[def.length];
		def.labels = new int[def.length][];

		for (int i = 0; i < def.length; ++i)
		{
			def.types[i] = in.get() & 0xFF;
		}

		for (int i = 0; i < def.length; ++i)
		{
			def.labels[i] = new int[in.get() & 0xFF];
		}

		for (int i = 0; i < def.length; ++i)
		{
			for (int j = 0; j < def.labels[i].length; ++j)
			{
				def.labels[i][j] = in.get() & 0xFF;
			}
		}

		return def;
	}

	public static class SkeletonDefinition
	{
		public int id;
		public int[] types;
		public int[][] labels;
		public int length;
	}
}
