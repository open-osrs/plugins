package com.jagex.runescape377.world;


public class GroundArray<T>
{
	private static final int MAX_HEIGHT = 4;
	private static final int MAX_TILES = 104;
	private Object[][][] groundArray;

	public GroundArray(T[][][] groundArray)
	{
		this.groundArray = groundArray;
	}

	public GroundArray()
	{
		this.groundArray = new Object[MAX_HEIGHT][MAX_TILES][MAX_TILES];
	}

	private boolean checkIfIndexDoesNotExist(int plane, int x, int y)
	{
		if (plane < 0 || plane > this.groundArray.length - 1)
		{
			String error = String.format("Plane must be between 0 and %d, requested plane was: %d.\n",
				this.groundArray.length - 1, plane);
			new ArrayIndexOutOfBoundsException(error).printStackTrace();
			return true;
		}
		if (x < 0 || x > MAX_TILES - 1)
		{
			String error = String.format("X must be between 0 and %d, requested x was: %d.\n", MAX_TILES - 1, x);
			new ArrayIndexOutOfBoundsException(error).printStackTrace();
			return true;
		}
		if (y < 0 || y > MAX_TILES - 1)
		{
			String error = String.format("Y must be between 0 and %d, requested y was: %d.\n", MAX_TILES - 1, y);
			new ArrayIndexOutOfBoundsException(error).printStackTrace();
			return true;
		}
		return false;
	}

	public boolean isTileEmpty(int plane, int x, int y)
	{
		if (checkIfIndexDoesNotExist(plane, x, y))
		{
			return false;
		}
		return groundArray[plane][x][y] == null;
	}

	public void clearTile(int plane, int x, int y)
	{
		if (checkIfIndexDoesNotExist(plane, x, y))
		{
			return;
		}
		groundArray[plane][x][y] = null;
	}

	public T getTile(int plane, int x, int y)
	{
		if (checkIfIndexDoesNotExist(plane, x, y))
		{
			return null;
		}
		return (T) groundArray[plane][x][y];
	}

	public T setTile(int plane, int x, int y, T tile)
	{
		if (checkIfIndexDoesNotExist(plane, x, y))
		{
			return null;
		}
		groundArray[plane][x][y] = tile;
		return tile;
	}
}
