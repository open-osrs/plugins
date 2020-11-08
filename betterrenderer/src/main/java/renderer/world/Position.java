package renderer.world;

public class Position
{
	public final int x;
	public final int y;
	public final int z;

	public Position(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Position north()
	{
		return new Position(x, y + 1, z);
	}

	public Position south()
	{
		return new Position(x, y - 1, z);
	}

	public Position east()
	{
		return new Position(x + 1, y, z);
	}

	public Position west()
	{
		return new Position(x - 1, y, z);
	}

	public Position up()
	{
		return new Position(x, y, z + 1);
	}

	public Position down()
	{
		return new Position(x, y, z - 1);
	}

	@Override
	public int hashCode()
	{
		return 31 * x + 31 * 31 * y + 31 * 31 * 31 * z;
	}

	@Override
	public boolean equals(Object other)
	{
		return other instanceof Position && x == ((Position) other).x && y == ((Position) other).y && z == ((Position) other).z;
	}

	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ", " + z + ')';
	}
}
