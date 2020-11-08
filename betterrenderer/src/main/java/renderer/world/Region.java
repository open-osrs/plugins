package renderer.world;

import java.util.ArrayList;
import java.util.List;
import renderer.world.MapDefinition.Tile;

public class Region
{
	private final int baseX;
	private final int baseY;

	public final int[][][] heights = new int[4][64][64];
	public final byte[][][] settings = new byte[4][64][64];
	public final OverlayDefinition[][][] overlays = new OverlayDefinition[4][64][64];
	public final OverlayShape[][][] overlayShapes = new OverlayShape[4][64][64];
	public final byte[][][] overlayRotations = new byte[4][64][64];
	public final UnderlayDefinition[][][] underlays = new UnderlayDefinition[4][64][64];
	public int[][][] blendedColors;

	public final List<Location> locations = new ArrayList<>();

	public Region(int x, int y)
	{
		baseX = x << 6;
		baseY = y << 6;
	}

	public void loadTerrain(Tile[][][] tiles)
	{
		for (int z = 0; z < 4; z++)
		{
			for (int x = 0; x < 64; x++)
			{
				for (int y = 0; y < 64; y++)
				{
					Tile tile = tiles[z][x][y];

					if (tile.height == null)
					{
						if (z == 0)
						{
							heights[0][x][y] = -HeightNoise.get(baseX + x + 0xe3b7b, baseY + y + 0x87cce) * 8;
						}
						else
						{
							heights[z][x][y] = heights[z - 1][x][y] - 240;
						}
					}
					else
					{
						int height = tile.height;
						if (height == 1)
						{
							height = 0;
						}

						if (z == 0)
						{
							heights[0][x][y] = -height * 8;
						}
						else
						{
							heights[z][x][y] = heights[z - 1][x][y] - height * 8;
						}
					}

					overlays[z][x][y] = tile.overlay;
					overlayShapes[z][x][y] = tile.overlayShape;
					overlayRotations[z][x][y] = tile.overlayRotation;

					settings[z][x][y] = tile.settings;
					underlays[z][x][y] = tile.underlay;
				}
			}
		}
	}

	public void loadLocations(List<Location> locs)
	{
		for (Location loc : locs)
		{
			Location newLoc = new Location(loc.object, loc.type, loc.rotation, new Position(baseX + loc.position.x, baseY + loc.position.y, loc.position.z));
			locations.add(newLoc);
		}
	}
}
