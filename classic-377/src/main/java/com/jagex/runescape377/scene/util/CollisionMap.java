package com.jagex.runescape377.scene.util;

public class CollisionMap
{

	public int insetX;
	public int insetY;
	public int width;
	public int height;
	public int[][] clippingData;

	public CollisionMap(int height, int width)
	{
		insetX = 0;
		insetY = 0;
		this.width = width;
		this.height = height;
		clippingData = new int[width][height];
		reset();

	}

	public void reset()
	{
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				if (x == 0 || y == 0 || x == width - 1 || y == height - 1)
				{
					clippingData[x][y] = 0xffffff;
				}
				else
				{
					clippingData[x][y] = 0x1000000;
				}
			}

		}

	}

	public void markWall(int x, int y, int position, int orientation, boolean impenetrable)
	{
		x -= insetX;
		y -= insetY;
		if (position == 0)
		{
			if (orientation == 0)
			{
				orClipTable(x, y, 128);
				orClipTable(x - 1, y, 8);
			}
			if (orientation == 1)
			{
				orClipTable(x, y, 2);
				orClipTable(x, y + 1, 32);
			}
			if (orientation == 2)
			{
				orClipTable(x, y, 8);
				orClipTable(x + 1, y, 128);
			}
			if (orientation == 3)
			{
				orClipTable(x, y, 32);
				orClipTable(x, y - 1, 2);
			}
		}
		if (position == 1 || position == 3)
		{
			if (orientation == 0)
			{
				orClipTable(x, y, 1);
				orClipTable(x - 1, y + 1, 16);
			}
			if (orientation == 1)
			{
				orClipTable(x, y, 4);
				orClipTable(x + 1, y + 1, 64);
			}
			if (orientation == 2)
			{
				orClipTable(x, y, 16);
				orClipTable(x + 1, y - 1, 1);
			}
			if (orientation == 3)
			{
				orClipTable(x, y, 64);
				orClipTable(x - 1, y - 1, 4);
			}
		}
		if (position == 2)
		{
			if (orientation == 0)
			{
				orClipTable(x, y, 130);
				orClipTable(x - 1, y, 8);
				orClipTable(x, y + 1, 32);
			}
			if (orientation == 1)
			{
				orClipTable(x, y, 10);
				orClipTable(x, y + 1, 32);
				orClipTable(x + 1, y, 128);
			}
			if (orientation == 2)
			{
				orClipTable(x, y, 40);
				orClipTable(x + 1, y, 128);
				orClipTable(x, y - 1, 2);
			}
			if (orientation == 3)
			{
				orClipTable(x, y, 160);
				orClipTable(x, y - 1, 2);
				orClipTable(x - 1, y, 8);
			}
		}
		if (impenetrable)
		{
			if (position == 0)
			{
				if (orientation == 0)
				{
					orClipTable(x, y, 0x10000);
					orClipTable(x - 1, y, 4096);
				}
				if (orientation == 1)
				{
					orClipTable(x, y, 1024);
					orClipTable(x, y + 1, 16384);
				}
				if (orientation == 2)
				{
					orClipTable(x, y, 4096);
					orClipTable(x + 1, y, 0x10000);
				}
				if (orientation == 3)
				{
					orClipTable(x, y, 16384);
					orClipTable(x, y - 1, 1024);
				}
			}
			if (position == 1 || position == 3)
			{
				if (orientation == 0)
				{
					orClipTable(x, y, 512);
					orClipTable(x - 1, y + 1, 8192);
				}
				if (orientation == 1)
				{
					orClipTable(x, y, 2048);
					orClipTable(x + 1, y + 1, 32768);
				}
				if (orientation == 2)
				{
					orClipTable(x, y, 8192);
					orClipTable(x + 1, y - 1, 512);
				}
				if (orientation == 3)
				{
					orClipTable(x, y, 32768);
					orClipTable(x - 1, y - 1, 2048);
				}
			}
			if (position == 2)
			{
				if (orientation == 0)
				{
					orClipTable(x, y, 0x10400);
					orClipTable(x - 1, y, 4096);
					orClipTable(x, y + 1, 16384);
				}
				if (orientation == 1)
				{
					orClipTable(x, y, 5120);
					orClipTable(x, y + 1, 16384);
					orClipTable(x + 1, y, 0x10000);
				}
				if (orientation == 2)
				{
					orClipTable(x, y, 20480);
					orClipTable(x + 1, y, 0x10000);
					orClipTable(x, y - 1, 1024);
				}
				if (orientation == 3)
				{
					orClipTable(x, y, 0x14000);
					orClipTable(x, y - 1, 1024);
					orClipTable(x - 1, y, 4096);
				}
			}
		}
	}

	public void markSolidOccupant(int objectY, int orient, int objectSizeY, int objectSizeX, boolean impenetrable, int objectX)
	{
		int occupied = 256;
		if (impenetrable)
		{
			occupied += 0x20000;
		}
		objectX -= insetX;
		objectY -= insetY;
		if (orient == 1 || orient == 3)
		{
			int l1 = objectSizeX;
			objectSizeX = objectSizeY;
			objectSizeY = l1;
		}
		for (int x = objectX; x < objectX + objectSizeX; x++)
		{
			if (x >= 0 && x < width)
			{
				for (int y = objectY; y < objectY + objectSizeY; y++)
				{
					if (y >= 0 && y < height)
					{
						orClipTable(x, y, occupied);
					}
				}

			}
		}

	}

	public void markBlocked(int x, int y)
	{
		x -= insetX;
		y -= insetY;
		clippingData[x][y] |= 0x200000;
	}

	public void orClipTable(int x, int y, int flag)
	{
		clippingData[x][y] |= flag;
	}

	public void unmarkWall(int orientation, int x, int y, int position, boolean impenetrable)
	{
		x -= insetX;
		y -= insetY;
		if (position == 0)
		{
			if (orientation == 0)
			{
				unset(128, x, y);
				unset(8, x - 1, y);
			}
			if (orientation == 1)
			{
				unset(2, x, y);
				unset(32, x, y + 1);
			}
			if (orientation == 2)
			{
				unset(8, x, y);
				unset(128, x + 1, y);
			}
			if (orientation == 3)
			{
				unset(32, x, y);
				unset(2, x, y - 1);
			}
		}
		if (position == 1 || position == 3)
		{
			if (orientation == 0)
			{
				unset(1, x, y);
				unset(16, x - 1, y + 1);
			}
			if (orientation == 1)
			{
				unset(4, x, y);
				unset(64, x + 1, y + 1);
			}
			if (orientation == 2)
			{
				unset(16, x, y);
				unset(1, x + 1, y - 1);
			}
			if (orientation == 3)
			{
				unset(64, x, y);
				unset(4, x - 1, y - 1);
			}
		}
		if (position == 2)
		{
			if (orientation == 0)
			{
				unset(130, x, y);
				unset(8, x - 1, y);
				unset(32, x, y + 1);
			}
			if (orientation == 1)
			{
				unset(10, x, y);
				unset(32, x, y + 1);
				unset(128, x + 1, y);
			}
			if (orientation == 2)
			{
				unset(40, x, y);
				unset(128, x + 1, y);
				unset(2, x, y - 1);
			}
			if (orientation == 3)
			{
				unset(160, x, y);
				unset(2, x, y - 1);
				unset(8, x - 1, y);
			}
		}
		if (impenetrable)
		{
			if (position == 0)
			{
				if (orientation == 0)
				{
					unset(0x10000, x, y);
					unset(4096, x - 1, y);
				}
				if (orientation == 1)
				{
					unset(1024, x, y);
					unset(16384, x, y + 1);
				}
				if (orientation == 2)
				{
					unset(4096, x, y);
					unset(0x10000, x + 1, y);
				}
				if (orientation == 3)
				{
					unset(16384, x, y);
					unset(1024, x, y - 1);
				}
			}
			if (position == 1 || position == 3)
			{
				if (orientation == 0)
				{
					unset(512, x, y);
					unset(8192, x - 1, y + 1);
				}
				if (orientation == 1)
				{
					unset(2048, x, y);
					unset(32768, x + 1, y + 1);
				}
				if (orientation == 2)
				{
					unset(8192, x, y);
					unset(512, x + 1, y - 1);
				}
				if (orientation == 3)
				{
					unset(32768, x, y);
					unset(2048, x - 1, y - 1);
				}
			}
			if (position == 2)
			{
				if (orientation == 0)
				{
					unset(0x10400, x, y);
					unset(4096, x - 1, y);
					unset(16384, x, y + 1);
				}
				if (orientation == 1)
				{
					unset(5120, x, y);
					unset(16384, x, y + 1);
					unset(0x10000, x + 1, y);
				}
				if (orientation == 2)
				{
					unset(20480, x, y);
					unset(0x10000, x + 1, y);
					unset(1024, x, y - 1);
				}
				if (orientation == 3)
				{
					unset(0x14000, x, y);
					unset(1024, x, y - 1);
					unset(4096, x - 1, y);
				}
			}
		}
	}

	public void unmarkSolidOccupant(int impenetrable, int y, int x, int orientation, int height, int width)
	{
		int occupied = 256;
		x -= insetX;
		y -= insetY;
		if (orientation == 1 || orientation == 3)
		{
			int originalWidth = width;
			width = height;
			height = originalWidth;
		}
		for (int xCounter = x; xCounter < x + width; xCounter++)
		{
			if (xCounter >= 0 && xCounter < this.width)
			{
				for (int yCounter = y; yCounter < y + height; yCounter++)
				{
					if (yCounter >= 0 && yCounter < this.height)
					{
						unset(occupied, xCounter, yCounter);
					}
				}

			}
		}

	}

	public void unset(int i, int x, int y)
	{
		clippingData[x][y] &= 0xffffff - i;
	}

	public void unmarkConcealed(int x, int y)
	{
		x -= insetX;
		y -= insetY;
		clippingData[x][y] &= 0xdfffff;
	}

	public boolean isWalkableA(int currentX, int currentY, int goalX, int goalY, int goalPosition, int goalOrientation)
	{
		if (currentX == goalX && currentY == goalY)
		{
			return true;
		}
		currentX -= insetX;
		currentY -= insetY;
		goalX -= insetX;
		goalY -= insetY;
		if (goalPosition == 0)
		{
			if (goalOrientation == 0)
			{
				if (currentX == goalX - 1 && currentY == goalY)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY + 1 && (clippingData[currentX][currentY] & 0x1280120) == 0)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY - 1 && (clippingData[currentX][currentY] & 0x1280102) == 0)
				{
					return true;
				}
			}
			else if (goalOrientation == 1)
			{
				if (currentX == goalX && currentY == goalY + 1)
				{
					return true;
				}
				if (currentX == goalX - 1 && currentY == goalY && (clippingData[currentX][currentY] & 0x1280108) == 0)
				{
					return true;
				}
				if (currentX == goalX + 1 && currentY == goalY && (clippingData[currentX][currentY] & 0x1280180) == 0)
				{
					return true;
				}
			}
			else if (goalOrientation == 2)
			{
				if (currentX == goalX + 1 && currentY == goalY)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY + 1 && (clippingData[currentX][currentY] & 0x1280120) == 0)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY - 1 && (clippingData[currentX][currentY] & 0x1280102) == 0)
				{
					return true;
				}
			}
			else if (goalOrientation == 3)
			{
				if (currentX == goalX && currentY == goalY - 1)
				{
					return true;
				}
				if (currentX == goalX - 1 && currentY == goalY && (clippingData[currentX][currentY] & 0x1280108) == 0)
				{
					return true;
				}
				if (currentX == goalX + 1 && currentY == goalY && (clippingData[currentX][currentY] & 0x1280180) == 0)
				{
					return true;
				}
			}
		}
		if (goalPosition == 2)
		{
			if (goalOrientation == 0)
			{
				if (currentX == goalX - 1 && currentY == goalY)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY + 1)
				{
					return true;
				}
				if (currentX == goalX + 1 && currentY == goalY && (clippingData[currentX][currentY] & 0x1280180) == 0)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY - 1 && (clippingData[currentX][currentY] & 0x1280102) == 0)
				{
					return true;
				}
			}
			else if (goalOrientation == 1)
			{
				if (currentX == goalX - 1 && currentY == goalY && (clippingData[currentX][currentY] & 0x1280108) == 0)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY + 1)
				{
					return true;
				}
				if (currentX == goalX + 1 && currentY == goalY)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY - 1 && (clippingData[currentX][currentY] & 0x1280102) == 0)
				{
					return true;
				}
			}
			else if (goalOrientation == 2)
			{
				if (currentX == goalX - 1 && currentY == goalY && (clippingData[currentX][currentY] & 0x1280108) == 0)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY + 1 && (clippingData[currentX][currentY] & 0x1280120) == 0)
				{
					return true;
				}
				if (currentX == goalX + 1 && currentY == goalY)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY - 1)
				{
					return true;
				}
			}
			else if (goalOrientation == 3)
			{
				if (currentX == goalX - 1 && currentY == goalY)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY + 1 && (clippingData[currentX][currentY] & 0x1280120) == 0)
				{
					return true;
				}
				if (currentX == goalX + 1 && currentY == goalY && (clippingData[currentX][currentY] & 0x1280180) == 0)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY - 1)
				{
					return true;
				}
			}
		}
		if (goalPosition == 9)
		{
			if (currentX == goalX && currentY == goalY + 1 && (clippingData[currentX][currentY] & 0x20) == 0)
			{
				return true;
			}
			if (currentX == goalX && currentY == goalY - 1 && (clippingData[currentX][currentY] & 2) == 0)
			{
				return true;
			}
			if (currentX == goalX - 1 && currentY == goalY && (clippingData[currentX][currentY] & 8) == 0)
			{
				return true;
			}
			if (currentX == goalX + 1 && currentY == goalY && (clippingData[currentX][currentY] & 0x80) == 0)
			{
				return true;
			}
		}
		return false;
	}

	public boolean isWalkableB(int currentX, int currentY, int goalX, int goalY, int goalPosition, int goalOrientation)
	{
		if (currentX == goalX && currentY == goalY)
		{
			return true;
		}
		currentX -= insetX;
		currentY -= insetY;
		goalX -= insetX;
		goalY -= insetY;
		if (goalPosition == 6 || goalPosition == 7)
		{
			if (goalPosition == 7)
			{
				goalOrientation = goalOrientation + 2 & 3;
			}
			if (goalOrientation == 0)
			{
				if (currentX == goalX + 1 && currentY == goalY && (clippingData[currentX][currentY] & 0x80) == 0)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY - 1 && (clippingData[currentX][currentY] & 2) == 0)
				{
					return true;
				}
			}
			else if (goalOrientation == 1)
			{
				if (currentX == goalX - 1 && currentY == goalY && (clippingData[currentX][currentY] & 8) == 0)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY - 1 && (clippingData[currentX][currentY] & 2) == 0)
				{
					return true;
				}
			}
			else if (goalOrientation == 2)
			{
				if (currentX == goalX - 1 && currentY == goalY && (clippingData[currentX][currentY] & 8) == 0)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY + 1 && (clippingData[currentX][currentY] & 0x20) == 0)
				{
					return true;
				}
			}
			else if (goalOrientation == 3)
			{
				if (currentX == goalX + 1 && currentY == goalY && (clippingData[currentX][currentY] & 0x80) == 0)
				{
					return true;
				}
				if (currentX == goalX && currentY == goalY + 1 && (clippingData[currentX][currentY] & 0x20) == 0)
				{
					return true;
				}
			}
		}
		if (goalPosition == 8)
		{
			if (currentX == goalX && currentY == goalY + 1 && (clippingData[currentX][currentY] & 0x20) == 0)
			{
				return true;
			}
			if (currentX == goalX && currentY == goalY - 1 && (clippingData[currentX][currentY] & 2) == 0)
			{
				return true;
			}
			if (currentX == goalX - 1 && currentY == goalY && (clippingData[currentX][currentY] & 8) == 0)
			{
				return true;
			}
			if (currentX == goalX + 1 && currentY == goalY && (clippingData[currentX][currentY] & 0x80) == 0)
			{
				return true;
			}
		}
		return false;
	}

	public boolean reachedFacingObject(int currentX, int currentY, int goalX, int goalY, int goalDX, int goalDY, int surroundings)
	{
		int goalX2 = (goalX + goalDX) - 1;
		int i2 = (goalY + goalDY) - 1;
		if (currentX >= goalX && currentX <= goalX2 && currentY >= goalY && currentY <= i2)
		{
			return true;
		}
		if (currentX == goalX - 1 && currentY >= goalY && currentY <= i2 && (clippingData[currentX - insetX][currentY - insetY] & 8) == 0 && (surroundings & 8) == 0)
		{
			return true;
		}
		if (currentX == goalX2 + 1 && currentY >= goalY && currentY <= i2 && (clippingData[currentX - insetX][currentY - insetY] & 0x80) == 0 && (surroundings & 2) == 0)
		{
			return true;
		}
		return currentY == goalY - 1 && currentX >= goalX && currentX <= goalX2 && (clippingData[currentX - insetX][currentY - insetY] & 2) == 0 && (surroundings & 4) == 0 || currentY == i2 + 1 && currentX >= goalX && currentX <= goalX2 && (clippingData[currentX - insetX][currentY - insetY] & 0x20) == 0 && (surroundings & 1) == 0;
	}


}
