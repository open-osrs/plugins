package com.jagex.runescape377.scene.util;

public class TiledUtils
{

	public static int getRotatedMapChunkX(int x, int y, int rotation)
	{
		rotation &= 3;
		if (rotation == 0)
		{
			return x;
		}
		if (rotation == 1)
		{
			return y;
		}
		if (rotation == 2)
		{
			return 7 - x;
		}
		else
		{
			return 7 - y;
		}
	}

	public static int getRotatedMapChunkY(int x, int y, int rotation)
	{
		rotation &= 3;
		if (rotation == 0)
		{
			return y;
		}
		if (rotation == 1)
		{
			return 7 - x;
		}
		if (rotation == 2)
		{
			return 7 - y;
		}
		else
		{
			return x;
		}
	}

	public static int getRotatedLandscapeChunkX(int rotation, int objectSizeY, int x, int y, int objectSizeX)
	{
		rotation &= 3;
		if (rotation == 0)
		{
			return x;
		}
		if (rotation == 1)
		{
			return y;
		}
		if (rotation == 2)
		{
			return 7 - x - (objectSizeX - 1);
		}
		else
		{
			return 7 - y - (objectSizeY - 1);
		}
	}

	public static int getRotatedLandscapeChunkY(int y, int objectSizeY, int rotation, int objectSizeX, int x)
	{
		rotation &= 3;
		if (rotation == 0)
		{
			return y;
		}
		if (rotation == 1)
		{
			return 7 - x - (objectSizeX - 1);
		}
		if (rotation == 2)
		{
			return 7 - y - (objectSizeY - 1);
		}
		else
		{
			return x;
		}
	}
}
