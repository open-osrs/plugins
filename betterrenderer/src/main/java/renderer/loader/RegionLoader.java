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

import java.util.ArrayList;
import java.util.List;
import renderer.cache.CacheSystem;
import renderer.util.CacheBuffer;
import renderer.world.Location;
import renderer.world.LocationType;
import renderer.world.MapDefinition;
import renderer.world.OverlayShape;
import renderer.world.Position;

public class RegionLoader
{
	public static MapDefinition.Tile[][][] readTerrain(byte[] buf)
	{
		CacheBuffer in = new CacheBuffer(buf);

		MapDefinition.Tile[][][] tiles = new MapDefinition.Tile[4][64][64];

		for (int z = 0; z < 4; z++)
		{
			for (int x = 0; x < 64; x++)
			{
				for (int y = 0; y < 64; y++)
				{
					tiles[z][x][y] = readTile(in);
				}
			}
		}

		return tiles;
	}

	private static MapDefinition.Tile readTile(CacheBuffer in)
	{
		MapDefinition.Tile tile = new MapDefinition.Tile();

		while (true)
		{
			int attribute = in.get() & 0xFF;
			if (attribute == 0)
			{
				return tile;
			}

			if (attribute == 1)
			{
				tile.height = in.get() & 0xFF;
				return tile;
			}

			if (attribute <= 49)
			{
				tile.overlay = CacheSystem.getOverlayDefinition((in.get() & 0xff) - 1);
				int overlayShape = attribute - 2;
				tile.overlayShape = OverlayShape.values()[(overlayShape >> 2) + 1];
				tile.overlayRotation = (byte) (overlayShape & 0b11);
				continue;
			}

			if (attribute <= 81)
			{
				tile.settings = (byte) (attribute - 49);
				continue;
			}

			tile.underlay = CacheSystem.getUnderlayDefinition((attribute - 81) - 1);
		}
	}

	public static List<Location> loadLocations(byte[] b)
	{
		List<Location> loc = new ArrayList<>();
		CacheBuffer buf = new CacheBuffer(b);

		int id = -1;
		int idOffset;

		while ((idOffset = buf.getSpecial3()) != 0)
		{
			id += idOffset;

			int position = 0;
			int positionOffset;

			while ((positionOffset = buf.getSpecial2()) != 0)
			{
				position += positionOffset - 1;

				int localY = position & 0x3F;
				int localX = position >> 6 & 0x3F;
				int height = position >> 12 & 0b11;

				int attributes = buf.get() & 0xFF;
				LocationType type = LocationType.values()[attributes >> 2];
				int orientation = attributes & 0x3;

				loc.add(new Location(CacheSystem.getObjectDefinition(id), type, orientation, new Position(localX, localY, height)));
			}
		}

		return loc;
	}
}
