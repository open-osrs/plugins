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

import renderer.model.TextureDefinition;
import renderer.util.CacheBuffer;
import renderer.util.Colors;

public class TextureLoader
{
	public static TextureDefinition load(int id, byte[] b)
	{
		TextureDefinition def = new TextureDefinition();
		CacheBuffer is = new CacheBuffer(b);

		def.averageColor = Colors.hsl(is.getShort() & 0xFFFF);
		def.unknown6 = is.get() != 0;
		def.id = id;

		int count = is.get() & 0xFF;
		int[] files = new int[count];

		for (int i = 0; i < count; ++i)
		{
			files[i] = is.getShort() & 0xFFFF;
		}

		def.fileIds = files;

		if (count > 1)
		{
			def.unknown1 = new int[count - 1];

			for (int var3 = 0; var3 < count - 1; ++var3)
			{
				def.unknown1[var3] = is.get() & 0xFF;
			}
		}

		if (count > 1)
		{
			def.unknown2 = new int[count - 1];

			for (int var3 = 0; var3 < count - 1; ++var3)
			{
				def.unknown2[var3] = is.get() & 0xFF;
			}
		}

		def.unknown3 = new int[count];

		for (int var3 = 0; var3 < count; ++var3)
		{
			def.unknown3[var3] = is.getInt();
		}

		def.unknown5 = is.get() & 0xFF;
		def.unknown4 = is.get() & 0xFF;

		return def;
	}
}
