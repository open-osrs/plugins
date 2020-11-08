package renderer.model;

import java.util.ArrayList;
import java.util.List;

public class TransformDefinition
{
	public int id;
	public boolean showing;
	public List<Transform> transforms = new ArrayList<>();

	public static class Transform
	{
		public final int type;
		public final int[] labels;
		public final int x;
		public final int y;
		public final int z;

		public Transform(int type, int[] labels, int x, int y, int z)
		{
			this.type = type;
			this.labels = labels;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}
