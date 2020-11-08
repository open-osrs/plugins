package renderer.renderer;

import java.util.Collections;
import java.util.List;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import renderer.cache.CacheSystem;
import renderer.model.ModelDefinition;
import renderer.model.TextureDefinition;
import renderer.util.Util;
import renderer.world.Location;
import renderer.world.LocationType;
import renderer.world.ObjectDefinition;
import renderer.world.OverlayDefinition;
import renderer.world.OverlayShape;
import renderer.world.Position;
import renderer.world.UnderlayDefinition;
import renderer.world.World;

public class WorldRenderer
{
	public static final double SCALE = 1 / 128.;
	public static final int CHUNK_SIZE = Renderer.CHUNK_SIZE;
	public static final double AMBIENT = 0.5;
	public static final double DIFFUSE = 2. / 3;
	public final BufferBuilder opaqueBuffer = new BufferBuilder(2500);
	public final BufferBuilder translucentBuffer = new BufferBuilder(100);
	public final World world;

	public WorldRenderer(World world)
	{
		this.world = world;
	}

	public void chunk(int chunkX, int chunkY)
	{
		int x1 = chunkX * CHUNK_SIZE;
		int y1 = chunkY * CHUNK_SIZE;
		int x2 = x1 + CHUNK_SIZE;
		int y2 = y1 + CHUNK_SIZE;

		opaqueBuffer.vertex(new Vector3d(x2, y2, -10), 0xff000000, 0);
		opaqueBuffer.vertex(new Vector3d(x1, y2, -10), 0xff000000, 0);
		opaqueBuffer.vertex(new Vector3d(x1, y1, -10), 0xff000000, 0);
		opaqueBuffer.vertex(new Vector3d(x1, y1, -10), 0xff000000, 0);
		opaqueBuffer.vertex(new Vector3d(x2, y1, -10), 0xff000000, 0);
		opaqueBuffer.vertex(new Vector3d(x2, y2, -10), 0xff000000, 0);

		for (int plane = 0; plane < 4; plane++)
		{
			for (int x = x1; x < x2; x++)
			{
				for (int y = y1; y < y2; y++)
				{
					if (plane >= world.roofRemovalPlane && world.roofsRemoved.contains(new Position(x, y, 0)))
					{
						continue;
					}

					tile(plane, x, y);
				}
			}
		}

		for (Location location : world.locations(x1 / 64, y1 / 64))
		{
			int plane = location.position.z;
			int x = location.position.x;
			int y = location.position.y;

			if (x >= x1 && x < x2 && y >= y1 && y < y2)
			{
				int actualZ = location.position.z;

				if ((world.settings(x, y, plane) & 0x2) != 0)
				{
					actualZ--; // bridge, shift down
				}

				if ((world.settings(x, y, plane) & 0x8) != 0)
				{
					actualZ = 0; // arch, always render (at the ge for example)
				}


				if (actualZ >= world.roofRemovalPlane && world.roofsRemoved.contains(new Position(x, y, 0)))
				{
					continue;
				}

				object(location.object, location.type, plane, x, y, location.rotation);
			}
		}
	}

	public void tile(int plane, int x, int y)
	{
		UnderlayDefinition underlay = world.underlay(x, y, plane);
		OverlayDefinition overlay = world.overlay(x, y, plane);

		if (underlay != null && overlay == null)
		{
			groundSquare(plane, x, y, -1);
			return;
		}

		if (overlay != null)
		{
			int color = overlay.texture == null ? overlay.color : overlay.texture.averageColor;

			if (overlay.color == 0xff00ff)
			{
				color = -1;
			}

			OverlayShape shape = world.getOverlayShape(x, y, plane);
			int rotation = world.getOverlayRotation(x, y, plane);

			if (shape == OverlayShape.FULL)
			{
				if (color != -1)
				{
					groundSquare(plane, x, y, color);
				}
				return;
			}

			for (OverlayShape.Triangle triangle : shape.triangles)
			{
				Vector3d a = triangle.positionA().rotateZ(-Math.PI / 2 * rotation).add(x + 0.5, y + 0.5, 0);
				Vector3d b = triangle.positionB().rotateZ(-Math.PI / 2 * rotation).add(x + 0.5, y + 0.5, 0);
				Vector3d c = triangle.positionC().rotateZ(-Math.PI / 2 * rotation).add(x + 0.5, y + 0.5, 0);

				if (triangle.overlay && overlay != null && color != -1)
				{
					groundVertex(plane, a.x, a.y, color);
					groundVertex(plane, b.x, b.y, color);
					groundVertex(plane, c.x, c.y, color);
				}

				if (!triangle.overlay && underlay != null)
				{
					groundVertex(plane, a.x, a.y, -1);
					groundVertex(plane, b.x, b.y, -1);
					groundVertex(plane, c.x, c.y, -1);
				}
			}
		}
	}

	public void groundSquare(int plane, int x, int y, int color)
	{
		double h00 = world.height(x, y, plane);
		double h01 = world.height(x, y + 1, plane);
		double h10 = world.height(x + 1, y, plane);
		double h11 = world.height(x + 1, y + 1, plane);

		if (Math.abs(h00 - h11) > Math.abs(h10 - h01))
		{
			groundVertex(plane, x + 1, y + 1, color);
			groundVertex(plane, x, y + 1, color);
			groundVertex(plane, x + 1, y, color);

			groundVertex(plane, x, y, color);
			groundVertex(plane, x + 1, y, color);
			groundVertex(plane, x, y + 1, color);
		}
		else
		{
			groundVertex(plane, x + 1, y + 1, color);
			groundVertex(plane, x, y + 1, color);
			groundVertex(plane, x, y, color);

			groundVertex(plane, x + 1, y + 1, color);
			groundVertex(plane, x, y, color);
			groundVertex(plane, x + 1, y, color);
		}
	}

	public void groundVertex(int plane, double x, double y, int color)
	{
		if (color == -1)
		{
			color = world.color(x, y, plane);
		}

		opaqueBuffer.vertex(world.position(x, y, plane), world.normal(x, y, plane), 0xff000000 | color, plane * 20, AMBIENT, DIFFUSE);
	}

	public void groundVertex(int plane, int x, int y, int color)
	{
		if (color == -1)
		{
			color = world.color(x, y, plane);
		}

		opaqueBuffer.vertex(world.position(x, y, plane), world.normal(x, y, plane), 0xff000000 | color, plane * 20, AMBIENT, DIFFUSE);
	}

	public void object(ObjectDefinition object, LocationType type, int plane, int x, int y, int rotation)
	{
		if (object.animation != null)
		{
			return;
		}

		if (object.models == null && object.typeModels == null)
		{
			return;
		}

		List<ModelDefinition> models = null;

		if (object.typeModels != null)
		{
			ModelDefinition model = object.typeModels.get(type.baseType);

			if (model != null)
			{
				models = Collections.singletonList(model);
			}
		}

		if (models == null && object.models != null)
		{
			models = object.models;
		}

		if (models == null || models.isEmpty())
		{
			return;
		}

		// flip
		boolean flip = object.mirror;

		// rotate

		if (rotation > 3)
		{
			throw new UnsupportedOperationException("nyi");
		}

		rotation %= 4;
		double angle = -Math.PI / 2 * rotation;

		if (type == LocationType.OBJECT_DIAGONAL || type == LocationType.WALL_DECORATION_DIAGONAL || type == LocationType.WALL_DECORATION_OPPOSITE_DIAGONAL || type == LocationType.WALL_DECORATION_DOUBLE)
		{
			angle += -Math.PI / 4;
		}

		// scale
		Vector3d scale = new Vector3d(object.scaleX / 128., object.scaleY / 128., object.scaleZ / 128.);

		// translate
		int sizeX = rotation == 0 || rotation == 2 ? object.sizeX : object.sizeY;
		int sizeY = rotation == 0 || rotation == 2 ? object.sizeY : object.sizeX;

		Vector3d pos = world.position(x + sizeX / 2., y + sizeY / 2., plane);
		double centerZ = pos.z;
		pos.z = 0;
		pos.add(object.offsetX * SCALE, object.offsetY * SCALE, -object.offsetZ * SCALE);

		double wallWidth = 0.25;
		Vector3d offset = new Vector3d();
		if (type == LocationType.WALL_DECORATION_DIAGONAL)
		{
			offset.add(0.5 + wallWidth / 2, -(0.5 + wallWidth / 2), 0);
			offset.add(0.5, 0.5, 0);
			flip = true;
		}

		if (type == LocationType.WALL_DECORATION_OPPOSITE)
		{
			offset.add(wallWidth, 0, 0);
		}

		if (type == LocationType.WALL_DECORATION_OPPOSITE_DIAGONAL)
		{
			offset.add(0.5 + wallWidth / 2, -(0.5 + wallWidth / 2), 0);
		}

		if (type == LocationType.WALL_DECORATION_DOUBLE)
		{
			return;
		}

		double extraPriority = 0;

		if (type.baseType == LocationType.WALL_DECORATION || type.baseType == LocationType.OBJECT || type == LocationType.FLOOR_DECORATION)
		{
			extraPriority += 5;
		}

		double ambient = AMBIENT + object.ambient / 128.;
		double diffuse = 1 / ((1 / DIFFUSE) + object.contrast / 512.);

		for (ModelDefinition model : models)
		{
			if (type == LocationType.WALL_CORNER)
			{
				model(object, plane, model, !flip, centerZ, extraPriority, ambient, diffuse, matrix(pos, angle, offset, scale, !flip), object.contouredGround);
				model(object, plane, model, flip, centerZ, extraPriority, ambient, diffuse, matrix(pos, angle - Math.PI / 2, offset, scale, flip), object.contouredGround);
			}
			else
			{
				model(object, plane, model, flip, centerZ, extraPriority, ambient, diffuse, matrix(pos, angle, offset, scale, flip), object.contouredGround);
			}
		}
	}

	public void model(ObjectDefinition object, int plane, ModelDefinition model, boolean flipped, double centerZ, double priority, double ambient, double diffuse, Matrix4d matrix, int contouredGround)
	{
		Matrix3d normalMatrix = matrix.normal(new Matrix3d());

		for (ModelDefinition.Face face : model.faces)
		{
			int color = 0xff - face.transparency << 24 | (object.colorSubstitutions.getOrDefault(face.color, face.color) & 0xffffff);
			TextureDefinition texture = face.texture != -1 ? CacheSystem.getTextureDefinition(object.textureSubstitutions.getOrDefault(face.texture, face.texture)) : null;
			double facePriority = model.priority + face.priority + priority + plane * 20;
			int renderType = face.renderType;
			if (face.transparency == 0xfe)
			{
				renderType = 3;
			}
			if (face.transparency == 0xff)
			{
				renderType = 2;
			}

			Vector3d a = adjustZ(plane, centerZ, matrix.transformPosition(new Vector3d(face.a.x * SCALE, face.a.z * SCALE, -face.a.y * SCALE)), contouredGround);
			Vector3d b = adjustZ(plane, centerZ, matrix.transformPosition(new Vector3d(face.b.x * SCALE, face.b.z * SCALE, -face.b.y * SCALE)), contouredGround);
			Vector3d c = adjustZ(plane, centerZ, matrix.transformPosition(new Vector3d(face.c.x * SCALE, face.c.z * SCALE, -face.c.y * SCALE)), contouredGround);

			if (flipped)
			{ // reverse vertex order for culling to work
				Vector3d t = a;
				a = c;
				c = t;
			}

			if (face.texture != -1)
			{
				color = texture.averageColor;
			}

			BufferBuilder buffer = 0xff - face.transparency == 0xff ? opaqueBuffer : translucentBuffer;

			if (renderType == 0)
			{ // smooth shading
				Vector3d na = normalMatrix.transform(new Vector3d(face.a.normal));
				Vector3d nb = normalMatrix.transform(new Vector3d(face.b.normal));
				Vector3d nc = normalMatrix.transform(new Vector3d(face.c.normal));

				buffer.vertex(a, na, color, facePriority, ambient, diffuse);
				buffer.vertex(b, nb, color, facePriority, ambient, diffuse);
				buffer.vertex(c, nc, color, facePriority, ambient, diffuse);
			}

			if (renderType == 1)
			{ // flat shading
				Vector3d normal = Util.normal(a, b, c);

				buffer.vertex(a, normal, color, facePriority, ambient, diffuse);
				buffer.vertex(b, normal, color, facePriority, ambient, diffuse);
				buffer.vertex(c, normal, color, facePriority, ambient, diffuse);
			}
		}
	}

	public Vector3d adjustZ(int plane, double centerZ, Vector3d p, int contourGround)
	{
		if (contourGround == -1)
		{
			p.z += centerZ;
		}

		if (contourGround == 0)
		{
			p.z += world.height(p.x, p.y, plane);
		}

		if (contourGround > 0)
		{
			throw new UnsupportedOperationException("???");
		}

		return p;
	}

	public Matrix4d matrix(Vector3d translate1, double rotate, Vector3d translate2, Vector3d scale, boolean mirror)
	{
		Matrix4d matrix = new Matrix4d();
		matrix.translate(translate1);
		matrix.rotateZ(rotate);
		matrix.translate(translate2);
		matrix.scale(scale);
		matrix.scale(1, mirror ? -1 : 1, 1);
		return matrix;
	}
}
