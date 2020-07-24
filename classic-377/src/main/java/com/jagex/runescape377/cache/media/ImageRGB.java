package com.jagex.runescape377.cache.media;

import com.jagex.runescape377.cache.Archive;
import com.jagex.runescape377.media.Rasterizer;
import com.jagex.runescape377.net.Buffer;
import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.PixelGrabber;

public class ImageRGB extends Rasterizer
{


	public int[] pixels;
	public int width;
	public int height;
	public int offsetX;
	public int offsetY;
	public int maxWidth;
	public int maxHeight;

	public ImageRGB(int width, int height)
	{
		pixels = new int[width * height];
		this.width = maxWidth = width;
		this.height = maxHeight = height;
		offsetX = offsetY = 0;
	}

	public ImageRGB(byte[] imagedata, Component component)
	{
		try
		{
			Image image = Toolkit.getDefaultToolkit().createImage(imagedata);
			MediaTracker mediatracker = new MediaTracker(component);
			mediatracker.addImage(image, 0);
			mediatracker.waitForAll();
			width = image.getWidth(component);
			height = image.getHeight(component);
			maxWidth = width;
			maxHeight = height;
			offsetX = 0;
			offsetY = 0;
			pixels = new int[width * height];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, width, height, pixels, 0,
				width);
			pixelgrabber.grabPixels();
			return;
		}
		catch (Exception _ex)
		{
			System.out.println("Error converting jpg");
		}
	}

	public ImageRGB(Archive archive, String archiveName, int archiveIndex)
	{
		Buffer dataBuffer = new Buffer(archive.getFile(archiveName + ".dat"));
		Buffer indexBuffer = new Buffer(archive.getFile("index.dat"));
		indexBuffer.currentPosition = dataBuffer.getUnsignedShortBE();
		maxWidth = indexBuffer.getUnsignedShortBE();
		maxHeight = indexBuffer.getUnsignedShortBE();
		int length = indexBuffer.getUnsignedByte();
		int[] pixels = new int[length];
		for (int pixel = 0; pixel < length - 1; pixel++)
		{
			pixels[pixel + 1] = indexBuffer.getMediumBE();
			if (pixels[pixel + 1] == 0)
			{
				pixels[pixel + 1] = 1;
			}
		}

		for (int index = 0; index < archiveIndex; index++)
		{
			indexBuffer.currentPosition += 2;
			dataBuffer.currentPosition += indexBuffer.getUnsignedShortBE() * indexBuffer.getUnsignedShortBE();
			indexBuffer.currentPosition++;
		}

		offsetX = indexBuffer.getUnsignedByte();
		offsetY = indexBuffer.getUnsignedByte();
		width = indexBuffer.getUnsignedShortBE();
		height = indexBuffer.getUnsignedShortBE();
		int type = indexBuffer.getUnsignedByte();
		int pixelCount = width * height;
		this.pixels = new int[pixelCount];
		if (type == 0)
		{
			for (int pixel = 0; pixel < pixelCount; pixel++)
			{
				this.pixels[pixel] = pixels[dataBuffer.getUnsignedByte()];
			}

			return;
		}
		if (type == 1)
		{
			for (int x = 0; x < width; x++)
			{
				for (int y = 0; y < height; y++)
				{
					this.pixels[x + y * width] = pixels[dataBuffer.getUnsignedByte()];
				}

			}

		}
	}

	public void createRasterizer()
	{
		Rasterizer.createRasterizer(pixels, width, height);
	}

	public void adjustRGB(int redOffset, int greenOffset, int blueOffset)
	{
		for (int pixel = 0; pixel < pixels.length; pixel++)
		{
			int originalColor = pixels[pixel];
			if (originalColor != 0)
			{
				int red = originalColor >> 16 & 0xff;
				red += redOffset;
				if (red < 1)
				{
					red = 1;
				}
				else if (red > 255)
				{
					red = 255;
				}
				int green = originalColor >> 8 & 0xff;
				green += greenOffset;
				if (green < 1)
				{
					green = 1;
				}
				else if (green > 255)
				{
					green = 255;
				}
				int blue = originalColor & 0xff;
				blue += blueOffset;
				if (blue < 1)
				{
					blue = 1;
				}
				else if (blue > 255)
				{
					blue = 255;
				}
				pixels[pixel] = (red << 16) + (green << 8) + blue;
			}
		}


	}

	public void trim()
	{
		int[] newPixels = new int[maxWidth * maxHeight];
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				newPixels[(y + offsetY) * maxWidth + (x + offsetX)] = pixels[y * width + x];
			}

		}

		pixels = newPixels;
		width = maxWidth;
		height = maxHeight;
		offsetX = 0;
		offsetY = 0;
	}

	public void drawInverse(int x, int y)
	{
		x += offsetX;
		y += offsetY;
		int rasterizerPixel = x + y * Rasterizer.width;
		int pixel = 0;
		int newHeight = height;
		int newWidth = width;
		int rasterizerPixelOffset = Rasterizer.width - newWidth;
		int pixelOffset = 0;
		if (y < Rasterizer.topY)
		{
			int yOffset = Rasterizer.topY - y;
			newHeight -= yOffset;
			y = Rasterizer.topY;
			pixel += yOffset * newWidth;
			rasterizerPixel += yOffset * Rasterizer.width;
		}
		if (y + newHeight > Rasterizer.bottomY)
		{
			newHeight -= (y + newHeight) - Rasterizer.bottomY;
		}
		if (x < Rasterizer.topX)
		{
			int xOffset = Rasterizer.topX - x;
			newWidth -= xOffset;
			x = Rasterizer.topX;
			pixel += xOffset;
			rasterizerPixel += xOffset;
			pixelOffset += xOffset;
			rasterizerPixelOffset += xOffset;
		}
		if (x + newWidth > Rasterizer.bottomX)
		{
			int widthOffset = (x + newWidth) - Rasterizer.bottomX;
			newWidth -= widthOffset;
			pixelOffset += widthOffset;
			rasterizerPixelOffset += widthOffset;
		}
		if (newWidth <= 0 || newHeight <= 0)
		{
			return;
		}
		copyPixels(pixels, Rasterizer.pixels, pixel, rasterizerPixel, pixelOffset, rasterizerPixelOffset, newWidth, newHeight);
	}

	public void copyPixels(int[] pixels, int[] rasterizerPixels, int pixel, int rasterizerPixel, int pixelOffset, int rasterizerPixelOffset, int width, int height)
	{
		int shiftedWidth = -(width >> 2);
		width = -(width & 3);
		for (int heightCounter = -height; heightCounter < 0; heightCounter++)
		{
			for (int widthCounter = shiftedWidth; widthCounter < 0; widthCounter++)
			{
				rasterizerPixels[rasterizerPixel++] = pixels[pixel++];
				rasterizerPixels[rasterizerPixel++] = pixels[pixel++];
				rasterizerPixels[rasterizerPixel++] = pixels[pixel++];
				rasterizerPixels[rasterizerPixel++] = pixels[pixel++];
			}

			for (int widthCounter = width; widthCounter < 0; widthCounter++)
			{
				rasterizerPixels[rasterizerPixel++] = pixels[pixel++];
			}

			rasterizerPixel += rasterizerPixelOffset;
			pixel += pixelOffset;
		}

	}

	public void drawImage(int x, int y)
	{
		x += offsetX;
		y += offsetY;
		int rasterizerOffset = x + y * Rasterizer.width;
		int pixelOffset = 0;
		int imageHeight = height;
		int imageWidth = width;
		int deviation = Rasterizer.width - imageWidth;
		int originalDeviation = 0;
		if (y < Rasterizer.topY)
		{
			int yOffset = Rasterizer.topY - y;
			imageHeight -= yOffset;
			y = Rasterizer.topY;
			pixelOffset += yOffset * imageWidth;
			rasterizerOffset += yOffset * Rasterizer.width;
		}
		if (y + imageHeight > Rasterizer.bottomY)
		{
			imageHeight -= (y + imageHeight) - Rasterizer.bottomY;
		}
		if (x < Rasterizer.topX)
		{
			int xOffset = Rasterizer.topX - x;
			imageWidth -= xOffset;
			x = Rasterizer.topX;
			pixelOffset += xOffset;
			rasterizerOffset += xOffset;
			originalDeviation += xOffset;
			deviation += xOffset;
		}
		if (x + imageWidth > Rasterizer.bottomX)
		{
			int xOffset = (x + imageWidth) - Rasterizer.bottomX;
			imageWidth -= xOffset;
			originalDeviation += xOffset;
			deviation += xOffset;
		}
		if (imageWidth <= 0 || imageHeight <= 0)
		{
			return;
		}
		else
		{
			shapeImageToPixels(pixels, Rasterizer.pixels, pixelOffset, rasterizerOffset, imageWidth, imageHeight, originalDeviation, deviation, 0);
			return;
		}
	}

	public void shapeImageToPixels(int[] pixels, int[] rasterizerPixels, int pixel, int rasterizerPixel, int width, int height, int pixelOffset, int rasterizerPixelOffset, int pixelColor)
	{
		int shiftedWidth = -(width >> 2);
		width = -(width & 3);
		for (int heightCounter = -height; heightCounter < 0; heightCounter++)
		{
			for (int widthCounter = shiftedWidth; widthCounter < 0; widthCounter++)
			{
				pixelColor = pixels[pixel++];
				if (pixelColor != 0)
				{
					rasterizerPixels[rasterizerPixel++] = pixelColor;
				}
				else
				{
					rasterizerPixel++;
				}
				pixelColor = pixels[pixel++];
				if (pixelColor != 0)
				{
					rasterizerPixels[rasterizerPixel++] = pixelColor;
				}
				else
				{
					rasterizerPixel++;
				}
				pixelColor = pixels[pixel++];
				if (pixelColor != 0)
				{
					rasterizerPixels[rasterizerPixel++] = pixelColor;
				}
				else
				{
					rasterizerPixel++;
				}
				pixelColor = pixels[pixel++];
				if (pixelColor != 0)
				{
					rasterizerPixels[rasterizerPixel++] = pixelColor;
				}
				else
				{
					rasterizerPixel++;
				}
			}

			for (int widthCounter = width; widthCounter < 0; widthCounter++)
			{
				pixelColor = pixels[pixel++];
				if (pixelColor != 0)
				{
					rasterizerPixels[rasterizerPixel++] = pixelColor;
				}
				else
				{
					rasterizerPixel++;
				}
			}

			rasterizerPixel += rasterizerPixelOffset;
			pixel += pixelOffset;
		}

	}

	public void drawImageAlpha(int x, int y, int alpha)
	{
		x += offsetX;
		y += offsetY;
		int rasterizerPixel = x + y * Rasterizer.width;
		int pixel = 0;
		int newHeight = height;
		int newWidth = width;
		int rasterizerPixelOffset = Rasterizer.width - newWidth;
		int pixelOffset = 0;
		if (y < Rasterizer.topY)
		{
			int yOffset = Rasterizer.topY - y;
			newHeight -= yOffset;
			y = Rasterizer.topY;
			pixel += yOffset * newWidth;
			rasterizerPixel += yOffset * Rasterizer.width;
		}
		if (y + newHeight > Rasterizer.bottomY)
		{
			newHeight -= (y + newHeight) - Rasterizer.bottomY;
		}
		if (x < Rasterizer.topX)
		{
			int xOffset = Rasterizer.topX - x;
			newWidth -= xOffset;
			x = Rasterizer.topX;
			pixel += xOffset;
			rasterizerPixel += xOffset;
			pixelOffset += xOffset;
			rasterizerPixelOffset += xOffset;
		}
		if (x + newWidth > Rasterizer.bottomX)
		{
			int xOffset = (x + newWidth) - Rasterizer.bottomX;
			newWidth -= xOffset;
			pixelOffset += xOffset;
			rasterizerPixelOffset += xOffset;
		}
		if (newWidth > 0 && newHeight > 0)
		{
			copyPixelsAlpha(pixels, Rasterizer.pixels, pixel, rasterizerPixel, pixelOffset, rasterizerPixelOffset, newWidth, newHeight, 0, alpha);
		}
	}

	public void copyPixelsAlpha(int[] pixels, int[] rasterizerPixels, int pixel, int rasterizerPixel, int pixelOffset, int rasterizerPixelOffset, int width, int height, int color, int alpha)
	{
		int alphaValue = 256 - alpha;
		for (int heightCounter = -height; heightCounter < 0; heightCounter++)
		{
			for (int widthCounter = -width; widthCounter < 0; widthCounter++)
			{
				color = pixels[pixel++];
				if (color != 0)
				{
					int rasterizerPixelColor = rasterizerPixels[rasterizerPixel];
					rasterizerPixels[rasterizerPixel++] = ((color & 0xff00ff) * alpha + (rasterizerPixelColor & 0xff00ff) * alphaValue & 0xff00ff00)
						+ ((color & 0xff00) * alpha + (rasterizerPixelColor & 0xff00) * alphaValue & 0xff0000) >> 8;
				}
				else
				{
					rasterizerPixel++;
				}
			}

			rasterizerPixel += rasterizerPixelOffset;
			pixel += pixelOffset;
		}

	}

	public void shapeImageToPixels(int x, int y, int width, int height, int zoom, int l, int[] ai, int k1, int[] ai1, int i2)
	{
		try
		{
			int centerX = -width / 2;
			int centerY = -height / 2;
			int sine = (int) (Math.sin(k1 / 326.11000000000001D) * 65536D);
			int cosine = (int) (Math.cos(k1 / 326.11000000000001D) * 65536D);
			sine = sine * zoom >> 8;
			cosine = cosine * zoom >> 8;
			int sourceOffsetX = (l << 16) + (centerY * sine + centerX * cosine);
			int sourceOffsetY = (i2 << 16) + (centerY * cosine - centerX * sine);
			int destinationOffset = x + y * Rasterizer.width;
			for (y = 0; y < height; y++)
			{
				int i4 = ai1[y];
				int j4 = destinationOffset + i4;
				int k4 = sourceOffsetX + cosine * i4;
				int l4 = sourceOffsetY - sine * i4;
				for (x = -ai[y]; x < 0; x++)
				{
					Rasterizer.pixels[j4++] = pixels[(k4 >> 16) + (l4 >> 16) * this.width];
					k4 += cosine;
					l4 -= sine;
				}

				sourceOffsetX += sine;
				sourceOffsetY += cosine;
				destinationOffset += Rasterizer.width;
			}

		}
		catch (Exception _ex)
		{
		}
	}

	public void drawRotated(int x, int y, int pivotX, int pivotY, int width, int height, int zoom, double angle)
	{
		try
		{
			int centerX = -width / 2;
			int centerY = -height / 2;
			int sine = (int) (Math.sin(angle) * 65536D);
			int cosine = (int) (Math.cos(angle) * 65536D);
			sine = sine * zoom >> 8;
			cosine = cosine * zoom >> 8;
			int sourceOffsetX = (pivotX << 16) + (centerY * sine + centerX * cosine);
			int sourceOffsetY = (pivotY << 16) + (centerY * cosine - centerX * sine);
			int destinationOffset = x + y * Rasterizer.width;
			for (y = 0; y < height; y++)
			{
				int i = destinationOffset;
				int offsetX = sourceOffsetX;
				int offsetY = sourceOffsetY;
				for (x = -width; x < 0; x++)
				{
					int colour = pixels[(offsetX >> 16) + (offsetY >> 16) * this.width];
					if (colour != 0)
					{
						Rasterizer.pixels[i++] = colour;
					}
					else
					{
						i++;
					}
					offsetX += cosine;
					offsetY -= sine;
				}

				sourceOffsetX += sine;
				sourceOffsetY += cosine;
				destinationOffset += Rasterizer.width;
			}

		}
		catch (Exception _ex)
		{
		}
	}

	public void drawTo(IndexedImage indexedImage, int x, int y)
	{
		x += offsetX;
		y += offsetY;
		int l = x + y * Rasterizer.width;
		int i1 = 0;
		int j1 = height;
		int k1 = width;
		int l1 = Rasterizer.width - k1;
		int i2 = 0;
		if (y < Rasterizer.topY)
		{
			int j2 = Rasterizer.topY - y;
			j1 -= j2;
			y = Rasterizer.topY;
			i1 += j2 * k1;
			l += j2 * Rasterizer.width;
		}
		if (y + j1 > Rasterizer.bottomY)
		{
			j1 -= (y + j1) - Rasterizer.bottomY;
		}
		if (x < Rasterizer.topX)
		{
			int k2 = Rasterizer.topX - x;
			k1 -= k2;
			x = Rasterizer.topX;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if (x + k1 > Rasterizer.bottomX)
		{
			int l2 = (x + k1) - Rasterizer.bottomX;
			k1 -= l2;
			i2 += l2;
			l1 += l2;
		}
		if (k1 <= 0 || j1 <= 0)
		{
			return;
		}
		else
		{
			method468(l, l1, pixels, k1, Rasterizer.pixels,
				indexedImage.imgPixels, 40303, j1, i1, 0, i2);
			return;
		}
	}

	public void method468(int i, int j, int ai[], int k, int ai1[], byte abyte0[], int l, int i1, int j1, int k1, int l1)
	{
		int i2 = -(k >> 2);
		k = -(k & 3);
		for (int j2 = -i1; j2 < 0; j2++)
		{
			for (int k2 = i2; k2 < 0; k2++)
			{
				k1 = ai[j1++];
				if (k1 != 0 && abyte0[i] == 0)
				{
					ai1[i++] = k1;
				}
				else
				{
					i++;
				}
				k1 = ai[j1++];
				if (k1 != 0 && abyte0[i] == 0)
				{
					ai1[i++] = k1;
				}
				else
				{
					i++;
				}
				k1 = ai[j1++];
				if (k1 != 0 && abyte0[i] == 0)
				{
					ai1[i++] = k1;
				}
				else
				{
					i++;
				}
				k1 = ai[j1++];
				if (k1 != 0 && abyte0[i] == 0)
				{
					ai1[i++] = k1;
				}
				else
				{
					i++;
				}
			}

			for (int l2 = k; l2 < 0; l2++)
			{
				k1 = ai[j1++];
				if (k1 != 0 && abyte0[i] == 0)
				{
					ai1[i++] = k1;
				}
				else
				{
					i++;
				}
			}

			i += j;
			j1 += l1;
		}

	}


}
