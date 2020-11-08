package renderer.world;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector4d;
import renderer.cache.CacheSystem;
import renderer.renderer.WorldRenderer;
import renderer.util.Colors;
import renderer.util.Util;

public class World
{
	public static final int BLEND_RADIUS = 5;
	private final Int2ObjectMap<List<Position>> roofs = new Int2ObjectOpenHashMap<>();
	public HashSet<Position> roofsRemoved = new HashSet<>();
	public int roofRemovalPlane;
	public Int2ObjectMap<Region> instanceRegions = null;

	private Region region(int x, int y)
	{
		if (instanceRegions != null)
		{
			Region instanceRegion = instanceRegions.get(x * 256 + y);

			if (instanceRegion != null)
			{
				return instanceRegion;
			}
		}

		return CacheSystem.region(x, y);
	}

	public UnderlayDefinition underlay(int x, int y, int z)
	{
		Region region = region(x / 64, y / 64);
		return region == null ? null : region.underlays[z][x % 64][y % 64];
	}

	public OverlayDefinition overlay(int x, int y, int z)
	{
		Region region = region(x / 64, y / 64);
		return region == null ? null : region.overlays[z][x % 64][y % 64];
	}

	public OverlayShape getOverlayShape(int x, int y, int plane)
	{
		Region region = region(x / 64, y / 64);
		return region == null ? null : region.overlayShapes[plane][x % 64][y % 64];
	}

	public byte getOverlayRotation(int x, int y, int plane)
	{
		Region region = region(x / 64, y / 64);
		return region == null ? 0 : region.overlayRotations[plane][x % 64][y % 64];
	}

	public byte settings(int x, int y, int plane)
	{
		Region region = region(x / 64, y / 64);
		return region == null ? 0 : region.settings[plane][x % 64][y % 64];
	}

	public List<Location> locations(int regionX, int regionY)
	{
		Region region = region(regionX, regionY);
		return region == null ? Collections.emptyList() : region.locations;
	}

	/////////////////////////////////////////////////////
	//                    Heights                      //
	/////////////////////////////////////////////////////

	public double height(double x, double y, int z)
	{
		double h00 = height((int) x, (int) y, z);
		double h10 = height((int) x + 1, (int) y, z);
		double h01 = height((int) x, (int) y + 1, z);
		double h11 = height((int) x + 1, (int) y + 1, z);

		return h00 * (1 - x % 1) * (1 - y % 1) +
			h10 * (x % 1) * (1 - y % 1) +
			h01 * (1 - x % 1) * (y % 1) +
			h11 * (x % 1) * (y % 1);
	}

	public double height(int x, int y, int z)
	{
		Region region = region(x / 64, y / 64);

		if (region == null)
		{
			return -extendedHeight(x, y, z) * WorldRenderer.SCALE;
		}

		return -region.heights[z][x % 64][y % 64] * WorldRenderer.SCALE;
	}

	private int extendedHeight(int x, int y, int z)
	{
		int height = -1;
		if (height == -1)
		{
			height = directHeight(x, y, z);
		}
		if (height == -1)
		{
			height = directHeight(x - 1, y, z);
		}
		if (height == -1)
		{
			height = directHeight(x + 1, y, z);
		}
		if (height == -1)
		{
			height = directHeight(x, y - 1, z);
		}
		if (height == -1)
		{
			height = directHeight(x, y + 1, z);
		}
		if (height == -1)
		{
			height = directHeight(x - 1, y - 1, z);
		}
		if (height == -1)
		{
			height = directHeight(x + 1, y + 1, z);
		}
		if (height == -1)
		{
			height = directHeight(x - 1, y + 1, z);
		}
		if (height == -1)
		{
			height = directHeight(x + 1, y - 1, z);
		}
		if (height == -1)
		{
			height = directHeight(x - 2, y, z);
		}
		if (height == -1)
		{
			height = directHeight(x + 2, y, z);
		}
		if (height == -1)
		{
			height = directHeight(x, y - 2, z);
		}
		if (height == -1)
		{
			height = directHeight(x, y + 2, z);
		}
		if (height == -1)
		{
			height = directHeight(x - 2, y - 2, z);
		}
		if (height == -1)
		{
			height = directHeight(x + 2, y + 2, z);
		}
		if (height == -1)
		{
			height = directHeight(x - 2, y + 2, z);
		}
		if (height == -1)
		{
			height = directHeight(x + 2, y - 2, z);
		}
		return height;
	}

	private int directHeight(int x, int y, int z)
	{
		Region region = region(x / 64, y / 64);

		if (region == null)
		{
			return -1;
		}

		return region.heights[z][x % 64][y % 64];
	}

	public Vector3d position(int x, int y, int z)
	{
		return new Vector3d(x, y, height(x, y, z));
	}

	public Vector3d position(double x, double y, int z)
	{
		return new Vector3d(x, y, height(x, y, z));
	}

	public Vector3d normal(double x, double y, int z)
	{
		Vector3d center = position(x, y, z);
		Vector3d e = position(x + 0.01, y, z);
		Vector3d n = position(x, y + 0.01, z);
		Vector3d w = position(x - 0.01, y, z);
		Vector3d s = position(x, y - 0.01, z);

		return new Vector3d()
			.add(Util.normal(center, e, n))
			.add(Util.normal(center, n, w))
			.add(Util.normal(center, w, s))
			.add(Util.normal(center, s, e))
			.normalize();
	}

	/////////////////////////////////////////////////////
	//                     Colors                      //
	/////////////////////////////////////////////////////

	public int color(double x, double y, int z)
	{
		Vector3d n00 = Colors.unpack(color((int) x, (int) y, z));
		Vector3d n10 = Colors.unpack(color((int) x + 1, (int) y, z));
		Vector3d n01 = Colors.unpack(color((int) x, (int) y + 1, z));
		Vector3d n11 = Colors.unpack(color((int) x + 1, (int) y + 1, z));

		return Colors.pack(new Vector3d()
			.add(n00.mul(1 - x % 1).mul(1 - y % 1))
			.add(n10.mul(x % 1).mul(1 - y % 1))
			.add(n01.mul(1 - x % 1).mul(y % 1))
			.add(n11.mul(x % 1).mul(y % 1))
		);
	}

	public int color(int x, int y, int z)
	{
		Region region = region(x / 64, y / 64);

		if (region == null)
		{
			return 0;
		}

		if (region.blendedColors == null)
		{
			region.blendedColors = blendColors(x / 64, y / 64);
		}

		return region.blendedColors[z][x % 64][y % 64];
	}

	private int[][][] blendColors(int regionX, int regionY)
	{
		int[][][] colors = new int[4][64][64];

		for (int plane = 0; plane < 4; plane++)
		{
			Vector4d[][] blended = new Vector4d[64 + 2 * BLEND_RADIUS][64 + 2 * BLEND_RADIUS];

			for (int dx = -BLEND_RADIUS; dx < 64 + BLEND_RADIUS; dx++)
			{
				for (int dy = -BLEND_RADIUS; dy < 64 + BLEND_RADIUS; dy++)
				{
					Vector3d color = Colors.unpack(unblendedColor(regionX * 64 + dx, regionY * 64 + dy, plane));
					blended[BLEND_RADIUS + dx][BLEND_RADIUS + dy] = color == null ?
						new Vector4d(0, 0, 0, 0) :
						new Vector4d(color.x, color.y, color.z, 1);
				}
			}

			blended = Util.boxBlur(blended, BLEND_RADIUS, 64);

			for (int dx = 0; dx < 64; dx++)
			{
				for (int dy = 0; dy < 64; dy++)
				{
					Vector4d c = blended[dx + BLEND_RADIUS][dy + BLEND_RADIUS];
					colors[plane][dx][dy] = Colors.pack(new Vector3d(c.x / c.w, c.y / c.w, c.z / c.w));
				}
			}
		}

		return colors;
	}

	public int unblendedColor(int x, int y, int z)
	{
		UnderlayDefinition underlay = underlay(x, y, z);
		return underlay == null ? -1 : underlay.color;
	}

	/////////////////////////////////////////////////////
	//                      Roofs                      //
	/////////////////////////////////////////////////////

	public List<Position> getRoof(int x, int y, int z)
	{
		if (!hasRoof(x, y, z))
		{
			return Collections.emptyList();
		}

		List<Position> roof = roofs.get((z << 30) + (x << 16) + y);

		if (roof != null)
		{
			return roof;
		}

		roof = new ArrayList<>();
		HashSet<Position> visited = new HashSet<>();
		Deque<Position> queue = new ArrayDeque<>();
		queue.add(new Position(x, y, z));

		while (!queue.isEmpty())
		{
			Position pos = queue.poll();

			if (!visited.add(pos) || !hasRoof(pos.x, pos.y, z))
			{
				continue;
			}

			if (hasRoof(pos.x, pos.y, pos.z))
			{
				roof.add(pos);
				roofs.put((pos.z << 30) + (pos.x << 16) + pos.y, roof);
			}

			queue.add(pos.north());
			queue.add(pos.south());
			queue.add(pos.east());
			queue.add(pos.west());
		}

		return roof;
	}

	private boolean hasRoof(int x, int y, int z)
	{
		return (settings(x, y, z) & 4) != 0;
	}

	public void updateRoofs(int x, int y, int z, int radius)
	{
		Set<List<Position>> roofs = Collections.newSetFromMap(new IdentityHashMap<>());

		for (int roofX = x - radius; roofX < x + radius; roofX++)
		{
			for (int roofY = y - radius; roofY < y + radius; roofY++)
			{
				if (new Vector2d(roofX - x, roofY - y).length() < radius)
				{
					roofs.add(getRoof(roofX, roofY, z));
				}
			}
		}

		roofsRemoved.clear();

		for (List<Position> roof : roofs)
		{
			for (Position p : roof)
			{
				roofsRemoved.add(new Position(p.x, p.y, 0));
			}
		}

		for (int i = 0; i < 2; i++)
		{
			for (Position p : new HashSet<>(roofsRemoved))
			{
				roofsRemoved.add(p.north());
				roofsRemoved.add(p.south());
				roofsRemoved.add(p.east());
				roofsRemoved.add(p.west());
				roofsRemoved.add(p.north().west());
				roofsRemoved.add(p.north().east());
				roofsRemoved.add(p.south().west());
				roofsRemoved.add(p.south().east());
			}
		}

		roofRemovalPlane = z + 1;
	}

	/////////////////////////////////////////////////////
	//                  Instances                      //
	/////////////////////////////////////////////////////

	public void copyInstanceChunk(Vector3i chunkPos, Vector3i templatePos, int orientation)
	{
		if (true)
		{
			return;
		}
		if (instanceRegions == null)
		{
			instanceRegions = new Int2ObjectOpenHashMap<>();
		}

		int regionX = chunkPos.x / 8;
		int regionY = chunkPos.x / 8;

		Region region = instanceRegions.computeIfAbsent(regionX * 256 + regionY, k -> new Region(regionX, regionY));
		Region template = region(templatePos.x / 8, templatePos.y / 8);

		int templateStartX = templatePos.x * 8;
		int templateEndX = (templatePos.x + 1) * 8;
		int templateStartY = templatePos.y * 8;
		int templateEndY = (templatePos.y + 1) * 8;

		for (Location location : template.locations)
		{
			Position p = location.position;

			if (p.x >= templateStartX && p.x < templateEndX && p.y >= templateStartY && p.y < templateEndY && p.z == templatePos.z)
			{
				region.locations.add(new Location(location.object, location.type, (location.rotation + orientation) % 4, adjust(p, chunkPos, templatePos, orientation)));
			}
		}

		for (int x = templateStartX; x <= templateEndX; x++)
		{
			for (int y = templateStartY; y <= templateEndY; y++)
			{
				Position pos = adjust(new Position(x, y, templatePos.z), chunkPos, templatePos, orientation);
				int xir = pos.x % 64;
				int yir = pos.y % 64;
				int z = pos.z;

				int txir = x % 64;
				int tyir = y % 64;
				int tz = templatePos.z;

				region.heights[z][xir][yir] = template.heights[tz][txir][tyir];
				region.settings[z][xir][yir] = template.settings[tz][txir][tyir];
				region.overlays[z][xir][yir] = template.overlays[tz][txir][tyir];
				region.overlayShapes[z][xir][yir] = template.overlayShapes[tz][txir][tyir];
				region.overlayRotations[z][xir][yir] = template.overlayRotations[tz][txir][tyir];
				region.underlays[z][xir][yir] = template.underlays[tz][txir][tyir];
			}
		}
	}

	private Position adjust(Position pos, Vector3i target, Vector3i source, int orientation)
	{
		int dx = (pos.x - source.x * 8);
		int dy = (pos.y - source.y * 8);

		int dxr;
		int dyr;

		switch (orientation)
		{
			case 0: // rotate 90 = mirror vertical, mirror diagonal
				break;

			case 1:
			{ // rotate 180 = mirror vertical, mirror horizontal
				dy = 8 - dy;
				int t = dx;
				dx = dy;
				dy = t;
				break;
			}

			case 2:
			{ // rotate 90 = mirror diagonal, mirror vertical
				dy = 8 - dy;
				dx = 8 - dx;
				break;
			}

			case 3:
			{
				int t = dx;
				dx = dy;
				dy = t;
				dy = 8 - dy;
				break;
			}

			default:
				throw new AssertionError();
		}

		return new Position(target.x * 8 + dx, target.y * 8 + dy, target.z);
	}
}
