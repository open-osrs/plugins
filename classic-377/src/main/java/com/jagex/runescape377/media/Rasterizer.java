package com.jagex.runescape377.media;

import com.jagex.runescape377.collection.CacheableNode;

public class Rasterizer extends CacheableNode
{

	public static int[] pixels;
	public static int width;
	public static int height;
	public static int topY;
	public static int bottomY;
	public static int topX;
	public static int bottomX;
	public static int viewportRx;
	public static int centerX;
	public static int centerY;


	public Rasterizer()
	{
	}

	public static void createRasterizer(int[] pixels, int width, int height)
	{
		Rasterizer.pixels = pixels;
		Rasterizer.width = width;
		Rasterizer.height = height;
		setCoordinates(0, 0, height, width);
	}

	public static void resetCoordinates()
	{
		topX = 0;
		topY = 0;
		bottomX = width;
		bottomY = height;
		viewportRx = bottomX - 1;
		centerX = bottomX / 2;
	}

	public static void resize(int topX, int topY, int bottomX, int bottomY)
	{
		if (Rasterizer.topX < topX)
		{
			Rasterizer.topX = topX;
		}
		if (Rasterizer.topY < topY)
		{
			Rasterizer.topY = topY;
		}
		if (Rasterizer.bottomX > bottomX)
		{
			Rasterizer.bottomX = bottomX;
		}
		if (Rasterizer.bottomY > bottomY)
		{
			Rasterizer.bottomY = bottomY;
		}
	}

	public static void setCoordinates(int y, int x, int height, int width)
	{
		if (x < 0)
		{
			x = 0;
		}
		if (y < 0)
		{
			y = 0;
		}
		if (width > Rasterizer.width)
		{
			width = Rasterizer.width;
		}
		if (height > Rasterizer.height)
		{
			height = Rasterizer.height;
		}
		topX = x;
		topY = y;
		bottomX = width;
		bottomY = height;
		viewportRx = bottomX - 1;
		centerX = bottomX / 2;
		centerY = bottomY / 2;

	}

	public static void resetPixels()
	{
		int pixelCount = width * height;
		for (int pixel = 0; pixel < pixelCount; pixel++)
		{
			pixels[pixel] = 0;
		}

	}

	public static void drawFilledRectangleAlpha(int x, int y, int width, int height, int colour, int alpha)
	{
		if (x < topX)
		{
			width -= topX - x;
			x = topX;
		}
		if (y < topY)
		{
			height -= topY - y;
			y = topY;
		}
		if (x + width > bottomX)
		{
			width = bottomX - x;
		}
		if (y + height > bottomY)
		{
			height = bottomY - y;
		}
		int a = 256 - alpha;
		int r = (colour >> 16 & 0xff) * alpha;
		int g = (colour >> 8 & 0xff) * alpha;
		int b = (colour & 0xff) * alpha;
		int widthOffset = Rasterizer.width - width;
		int pixel = x + y * Rasterizer.width;
		for (int heightCounter = 0; heightCounter < height; heightCounter++)
		{
			for (int widthCounter = -width; widthCounter < 0; widthCounter++)
			{
				int red = (pixels[pixel] >> 16 & 0xff) * a;
				int green = (pixels[pixel] >> 8 & 0xff) * a;
				int blue = (pixels[pixel] & 0xff) * a;
				int rgba = ((r + red >> 8) << 16) + ((g + green >> 8) << 8) + (b + blue >> 8);
				pixels[pixel++] = rgba;
			}

			pixel += widthOffset;
		}

	}

	public static void drawFilledRectangle(int x, int y, int width, int height, int colour)
	{
		if (x < topX)
		{
			width -= topX - x;
			x = topX;
		}
		if (y < topY)
		{
			height -= topY - y;
			y = topY;
		}
		if (x + width > bottomX)
		{
			width = bottomX - x;
		}
		if (y + height > bottomY)
		{
			height = bottomY - y;
		}
		int pixelOffset = Rasterizer.width - width;
		int pixel = x + y * Rasterizer.width;
		for (int heightCounter = -height; heightCounter < 0; heightCounter++)
		{
			for (int widthCounter = -width; widthCounter < 0; widthCounter++)
			{
				pixels[pixel++] = colour;
			}

			pixel += pixelOffset;
		}
	}

	public static void drawUnfilledRectangle(int x, int y, int width, int height, int color)
	{
		drawHorizontalLine(x, y, width, color);
		drawHorizontalLine(x, (y + height) - 1, width, color);
		drawVerticalLine(x, y, height, color);
		drawVerticalLine((x + width) - 1, y, height, color);
	}

	public static void drawUnfilledRectangleAlpha(int x, int y, int width, int height, int colour, int alpha)
	{
		drawHorizontalLineAlpha(x, y, width, colour, alpha);
		drawHorizontalLineAlpha(x, (y + height) - 1, width, colour, alpha);
		if (height >= 3)
		{
			drawVerticalLineAlpha(x, y + 1, height - 2, colour, alpha);
			drawVerticalLineAlpha((x + width) - 1, y + 1, height - 2, colour, alpha);
		}
	}

	public static void drawHorizontalLine(int x, int y, int lenght, int colour)
	{
		if (y < topY || y >= bottomY)
		{
			return;
		}
		if (x < topX)
		{
			lenght -= topX - x;
			x = topX;
		}
		if (x + lenght > bottomX)
		{
			lenght = bottomX - x;
		}
		int pixelOffset = x + y * width;
		for (int pixel = 0; pixel < lenght; pixel++)
		{
			pixels[pixelOffset + pixel] = colour;
		}

	}

	public static void drawHorizontalLineAlpha(int x, int y, int length, int colour, int alpha)
	{
		if (y < topY || y >= bottomY)
		{
			return;
		}
		if (x < topX)
		{
			length -= topX - x;
			x = topX;
		}
		if (x + length > bottomX)
		{
			length = bottomX - x;
		}
		int a = 256 - alpha;
		int r = (colour >> 16 & 0xff) * alpha;
		int g = (colour >> 8 & 0xff) * alpha;
		int b = (colour & 0xff) * alpha;
		int pixelOffset = x + y * width;
		for (int lengthCounter = 0; lengthCounter < length; lengthCounter++)
		{
			int red = (pixels[pixelOffset] >> 16 & 0xff) * a;
			int green = (pixels[pixelOffset] >> 8 & 0xff) * a;
			int blue = (pixels[pixelOffset] & 0xff) * a;
			int rgba = ((r + red >> 8) << 16) + ((g + green >> 8) << 8) + (b + blue >> 8);
			pixels[pixelOffset++] = rgba;
		}
	}

	public static void drawVerticalLine(int x, int y, int lenght, int colour)
	{
		if (x < topX || x >= bottomX)
		{
			return;
		}
		if (y < topY)
		{
			lenght -= topY - y;
			y = topY;
		}
		if (y + lenght > bottomY)
		{
			lenght = bottomY - y;
		}
		int pixelOffset = x + y * width;
		for (int pixel = 0; pixel < lenght; pixel++)
		{
			pixels[pixelOffset + pixel * width] = colour;
		}

	}

	public static void drawVerticalLineAlpha(int x, int y, int lenght, int colour, int alpha)
	{
		if (x < topX || x >= bottomX)
		{
			return;
		}
		if (y < topY)
		{
			lenght -= topY - y;
			y = topY;
		}
		if (y + lenght > bottomY)
		{
			lenght = bottomY - y;
		}
		int a = 256 - alpha;
		int r = (colour >> 16 & 0xff) * alpha;
		int g = (colour >> 8 & 0xff) * alpha;
		int b = (colour & 0xff) * alpha;
		int pixel = x + y * width;
		for (int lengthCounter = 0; lengthCounter < lenght; lengthCounter++)
		{
			int red = (pixels[pixel] >> 16 & 0xff) * a;
			int blue = (pixels[pixel] >> 8 & 0xff) * a;
			int green = (pixels[pixel] & 0xff) * a;
			int rgba = ((r + red >> 8) << 16) + ((g + blue >> 8) << 8) + (b + green >> 8);
			pixels[pixel] = rgba;
			pixel += width;
		}

	}

	static final void drawDiagonalLine(int x, int y, int DestX, int destY, int linecolor)
	{
		DestX -= x;
		destY -= y;
		if (destY == 0)
		{
			if (DestX >= 0)
			{
				drawHorizontalLine(x, y, DestX + 1, linecolor);
			}
			else
			{
				drawHorizontalLine(x + DestX, y, -DestX + 1, linecolor);
			}
		}
		else if (DestX == 0)
		{
			if (destY >= 0)
			{
				drawVerticalLine(x, y, destY + 1, linecolor);
			}
			else
			{
				drawVerticalLine(x, y + destY, -destY + 1, linecolor);
			}
		}
		else
		{
			if (DestX + destY < 0)
			{
				x += DestX;
				DestX = -DestX;
				y += destY;
				destY = -destY;
			}
			int var5;
			int var6;
			if (DestX > destY)
			{
				y <<= 16;
				y += '\u8000';
				destY <<= 16;
				var5 = (int) Math.floor((double) destY / (double) DestX + 0.5D);
				DestX += x;
				if (x < topX)
				{
					y += var5 * (topX - x);
					x = topX;
				}
				if (DestX >= bottomX)
				{
					DestX = bottomX - 1;
				}
				while (x <= DestX)
				{
					var6 = y >> 16;
					if (var6 >= topY && var6 < bottomY)
					{
						pixels[x + var6 * width] = linecolor;
					}
					y += var5;
					++x;
				}
			}
			else
			{
				x <<= 16;
				x += '\u8000';
				DestX <<= 16;
				var5 = (int) Math.floor((double) DestX / (double) destY + 0.5D);
				destY += y;
				if (y < topY)
				{
					x += var5 * (topY - y);
					y = topY;
				}
				if (destY >= bottomY)
				{
					destY = bottomY - 1;
				}
				while (y <= destY)
				{
					var6 = x >> 16;
					if (var6 >= topX && var6 < bottomX)
					{
						pixels[var6 + y * width] = linecolor;
					}
					x += var5;
					++y;
				}
			}
		}
	}

	public static void drawCircle(int x, int y, int radius, int color)
	{
		if (radius == 0)
		{
			drawPixel(x, y, color);
		}
		else
		{
			if (radius < 0)
			{
				radius = -radius;
			}
			int var4 = y - radius;
			if (var4 < topY)
			{
				var4 = topY;
			}
			int var5 = y + radius + 1;
			if (var5 > bottomY)
			{
				var5 = bottomY;
			}
			int var6 = var4;
			int var7 = radius * radius;
			int var8 = 0;
			int var9 = y - var4;
			int var10 = var9 * var9;
			int var11 = var10 - var9;
			if (y > var5)
			{
				y = var5;
			}
			int var12;
			int var13;
			int var14;
			int var15;
			while (var6 < y)
			{
				while (var11 <= var7 || var10 <= var7)
				{
					var10 += var8 + var8;
					var11 += var8++ + var8;
				}
				var12 = x - var8 + 1;
				if (var12 < topX)
				{
					var12 = topX;
				}
				var13 = x + var8;
				if (var13 > bottomX)
				{
					var13 = bottomX;
				}
				var14 = var12 + var6 * width;
				for (var15 = var12; var15 < var13; ++var15)
				{
					pixels[var14++] = color;
				}
				++var6;
				var10 -= var9-- + var9;
				var11 -= var9 + var9;
			}
			var8 = radius;
			var9 = var6 - y;
			var11 = var9 * var9 + var7;
			var10 = var11 - radius;
			for (var11 -= var9; var6 < var5; var10 += var9++ + var9)
			{
				while (var11 > var7 && var10 > var7)
				{
					var11 -= var8-- + var8;
					var10 -= var8 + var8;
				}
				var12 = x - var8;
				if (var12 < topX)
				{
					var12 = topX;
				}
				var13 = x + var8;
				if (var13 > bottomX - 1)
				{
					var13 = bottomX - 1;
				}
				var14 = var12 + var6 * width;
				for (var15 = var12; var15 <= var13; ++var15)
				{
					pixels[var14++] = color;
				}
				++var6;
				var11 += var9 + var9;
			}
		}
	}

	public static void drawCircleAlpha(int x, int y, int radius, int color, int alpha)
	{
		if (alpha != 0)
		{
			if (alpha == 256)
			{
				drawCircle(x, y, radius, color);
			}
			else
			{
				if (radius < 0)
				{
					radius = -radius;
				}
				int a = 256 - alpha;
				int r = (color >> 16 & 255) * alpha;
				int g = (color >> 8 & 255) * alpha;
				int b = (color & 255) * alpha;
				int topY = y - radius;
				if (topY < Rasterizer.topY)
				{
					topY = Rasterizer.topY;
				}
				int bottomY = y + radius + 1;
				if (bottomY > Rasterizer.bottomY)
				{
					bottomY = Rasterizer.bottomY;
				}
				int var14 = topY;
				int var15 = radius * radius;
				int var16 = 0;
				int var17 = y - topY;
				int var18 = var17 * var17;
				int var19 = var18 - var17;
				if (y > bottomY)
				{
					y = bottomY;
				}
				int var9;
				int var10;
				int var11;
				int var21;
				int var20;
				int var23;
				int var22;
				int var24;
				while (var14 < y)
				{
					while (var19 <= var15 || var18 <= var15)
					{
						var18 += var16 + var16;
						var19 += var16++ + var16;
					}
					var20 = x - var16 + 1;
					if (var20 < topX)
					{
						var20 = topX;
					}
					var21 = x + var16;
					if (var21 > bottomX)
					{
						var21 = bottomX;
					}
					var22 = var20 + var14 * width;
					for (var23 = var20; var23 < var21; ++var23)
					{
						var9 = (pixels[var22] >> 16 & 255) * a;
						var10 = (pixels[var22] >> 8 & 255) * a;
						var11 = (pixels[var22] & 255) * a;
						var24 = (r + var9 >> 8 << 16) + (g + var10 >> 8 << 8) + (b + var11 >> 8);
						pixels[var22++] = var24;
					}
					++var14;
					var18 -= var17-- + var17;
					var19 -= var17 + var17;
				}
				var16 = radius;
				var17 = -var17;
				var19 = var17 * var17 + var15;
				var18 = var19 - radius;
				for (var19 -= var17; var14 < bottomY; var18 += var17++ + var17)
				{
					while (var19 > var15 && var18 > var15)
					{
						var19 -= var16-- + var16;
						var18 -= var16 + var16;
					}
					var20 = x - var16;
					if (var20 < topX)
					{
						var20 = topX;
					}
					var21 = x + var16;
					if (var21 > bottomX - 1)
					{
						var21 = bottomX - 1;
					}
					var22 = var20 + var14 * width;
					for (var23 = var20; var23 <= var21; ++var23)
					{
						var9 = (pixels[var22] >> 16 & 255) * a;
						var10 = (pixels[var22] >> 8 & 255) * a;
						var11 = (pixels[var22] & 255) * a;
						var24 = (r + var9 >> 8 << 16) + (g + var10 >> 8 << 8) + (b + var11 >> 8);
						pixels[var22++] = var24;
					}
					++var14;
					var19 += var17 + var17;
				}
			}
		}
	}

	private static void drawPixel(int x, int y, int color)
	{
		if (x >= topX && y >= topY && x < bottomX && y < bottomY)
		{
			pixels[x + y * width] = color;
		}
	}

	public static void clearPixels()
	{
		int i = 0;
		int pixeltoclear;
		for (pixeltoclear = width * height - 7; i < pixeltoclear; pixels[i++] = 0)
		{
			pixels[i++] = 0;
			pixels[i++] = 0;
			pixels[i++] = 0;
			pixels[i++] = 0;
			pixels[i++] = 0;
			pixels[i++] = 0;
			pixels[i++] = 0;
		}
		for (pixeltoclear += 7; i < pixeltoclear; pixels[i++] = 0)
		{
		}
	}

	public static void destroy()
	{
		pixels = null;
	}


}
