package renderer.world;

import org.joml.Vector3d;

public enum OverlayShape
{
	BLANK(new Triangle(false, 3, 5, 7), new Triangle(false, 1, 3, 7)),
	FULL(new Triangle(true, 3, 5, 7), new Triangle(true, 1, 3, 7)),
	DIAGONAL(new Triangle(false, 3, 5, 7), new Triangle(true, 1, 3, 7)),
	HALF_DIAGONAL(new Triangle(false, 1, 3, 5), new Triangle(false, 1, 5, 6), new Triangle(true, 1, 6, 7)),
	HALF_DIAGONAL_MIRRORED(new Triangle(false, 1, 3, 6), new Triangle(false, 1, 6, 7), new Triangle(true, 3, 5, 6)),
	HALF_DIAGONAL_INVERTED(new Triangle(false, 1, 6, 7), new Triangle(true, 1, 3, 5), new Triangle(true, 1, 5, 6)),
	HALF_DIAGONAL_INVERTED_MIRRORED(new Triangle(false, 3, 5, 6), new Triangle(true, 1, 3, 6), new Triangle(true, 1, 6, 7)),
	HALF(new Triangle(false, 2, 3, 5), new Triangle(false, 2, 5, 6), new Triangle(true, 1, 2, 6), new Triangle(true, 1, 6, 7)),
	CORNER(new Triangle(false, 2, 3, 5), new Triangle(false, 2, 5, 7), new Triangle(false, 2, 7, 8), new Triangle(true, 1, 2, 8)),
	CORNER_INVERTED(new Triangle(false, 1, 2, 8), new Triangle(true, 2, 3, 5), new Triangle(true, 2, 5, 7), new Triangle(true, 2, 7, 8)),
	CURVED_CORNER(new Triangle(false, 1, 3, 12), new Triangle(false, 3, 11, 12), new Triangle(false, 3, 5, 11), new Triangle(true, 1, 12, 7), new Triangle(true, 12, 11, 7), new Triangle(true, 11, 5, 7)),
	CURVED_CORNER_INVERTED(new Triangle(true, 1, 3, 12), new Triangle(true, 3, 11, 12), new Triangle(true, 3, 5, 11), new Triangle(false, 1, 12, 7), new Triangle(false, 12, 11, 7), new Triangle(false, 11, 5, 7)),
	TAB(new Triangle(true, 1, 14, 13), new Triangle(true, 1, 3, 14), new Triangle(false, 1, 13, 7), new Triangle(false, 13, 14, 7), new Triangle(false, 14, 5, 7), new Triangle(false, 3, 5, 14));

	public static final Vector3d[] POSITIONS = {
		null,
		new Vector3d(-0.5, -0.5, 0),
		new Vector3d(0, -0.5, 0),
		new Vector3d(0.5, -0.5, 0),
		new Vector3d(0.5, 0, 0),
		new Vector3d(0.5, 0.5, 0),
		new Vector3d(0, 0.5, 0),
		new Vector3d(-0.5, 0.5, 0),
		new Vector3d(-0.5, 0, 0),
		new Vector3d(0, -0.25, 0),
		new Vector3d(0.25, 0, 0),
		new Vector3d(0, 0.25, 0),
		new Vector3d(-0.25, 0, 0),
		new Vector3d(-0.25, -0.25, 0),
		new Vector3d(0.25, -0.25, 0),
		new Vector3d(0.25, 0.25, 0),
		new Vector3d(-0.25, 0.25, 0)
	};

	public final Triangle[] triangles;

	OverlayShape(Triangle... triangles)
	{
		this.triangles = triangles;
	}

	public static Vector3d position(int location)
	{
		return new Vector3d(POSITIONS[location]);
	}

	public static class Triangle
	{
		public final boolean overlay;
		public final int a;
		public final int b;
		public final int c;

		public Triangle(boolean overlay, int a, int b, int c)
		{
			this.overlay = overlay;
			this.a = a;
			this.b = b;
			this.c = c;
		}

		public Vector3d positionA()
		{
			return position(a);
		}

		public Vector3d positionB()
		{
			return position(b);
		}

		public Vector3d positionC()
		{
			return position(c);
		}
	}
}
