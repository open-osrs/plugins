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
import renderer.util.CacheBuffer;
import renderer.world.OverlayDefinition;

public class OverlayLoader
{
	public static OverlayDefinition load(int id, byte[] b)
	{
		OverlayDefinition def = new OverlayDefinition();
		def.id = id;
		CacheBuffer is = new CacheBuffer(b);

		while (true)
		{
			int opcode = is.get() & 0xFF;

			if (opcode == 0)
			{
				break;
			}

			switch (opcode)
			{
				case 1:
					def.color = is.getMedium();
					break;
				case 2:
					def.texture = CacheSystem.getTextureDefinition(is.get() & 0xFF);
					break;
				case 5:
					def.hideUnderlay = false;
					break;
				case 7:
					def.mapColor = is.getMedium();
					break;
			}
		}

		return def;
	}
}
