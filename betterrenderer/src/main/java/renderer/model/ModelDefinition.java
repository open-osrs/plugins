package renderer.model;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3d;
import renderer.util.Util;

public class ModelDefinition
{
	public int id;
	public List<Vertex> vertices = new ArrayList<>();
	public List<Face> faces = new ArrayList<>();

	public int textureTriangleCount;
	public short[] textureTriangleVertexIndices1;
	public short[] textureTriangleVertexIndices2;
	public short[] textureTriangleVertexIndices3;
	public short[] texturePrimaryColors;

	public byte[] textureRenderTypes;

	public byte priority;

	public short[] unknown1;
	public short[] unknown2;
	public short[] unknown3;
	public short[] unknown4;
	public byte[] unknown5;
	public short[] unknown6;
	public boolean translucent;

	public static class Vertex
	{
		public int x;
		public int y;
		public int z;
		public int label;
		public int index;
		public Vector3d normal = new Vector3d();

		public Vector3d v()
		{
			return new Vector3d(x, y, z);
		}
	}

	public static class Face
	{
		public Vertex a;
		public Vertex b;
		public Vertex c;
		public int color = 0;
		public int transparency = 0;
		public byte priority = 0;
		public byte renderType = 0;
		public int texture = -1;
		public int label = -1;
		public byte textureCoordinates = -1;
	}

	public void calculateNormals()
	{
		for (Face face : faces)
		{
			if (face.renderType != 0)
			{
				continue;
			}

			Vector3d a = new Vector3d(face.a.x, face.a.z, -face.a.y);
			Vector3d b = new Vector3d(face.b.x, face.b.z, -face.b.y);
			Vector3d c = new Vector3d(face.c.x, face.c.z, -face.c.y);

			Vector3d normal = Util.normal(a, b, c);

			face.a.normal.add(normal);
			face.b.normal.add(normal);
			face.c.normal.add(normal);
		}

		for (Vertex vertex : vertices)
		{
			vertex.normal.normalize();

			if (!vertex.normal.isFinite())
			{
				vertex.normal = new Vector3d(0, 0, 1);
			}
		}
	}
}
