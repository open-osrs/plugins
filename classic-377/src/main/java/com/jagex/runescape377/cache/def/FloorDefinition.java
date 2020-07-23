package com.jagex.runescape377.cache.def;

import com.jagex.runescape377.cache.Archive;
import com.jagex.runescape377.net.Buffer;

public class FloorDefinition
{

	public static byte aByte310 = 6;
	public static int count;
	public static FloorDefinition cache[];
	public int anInt311;
	public boolean aBoolean312 = true;
	public String name;
	public int rgbColor;
	public int textureId = -1;
	public boolean aBoolean318 = false;
	public boolean occlude = true;
	public int hue2;
	public int saturation;
	public int lightness;
	public int hue;
	public int hueDivisor;
	public int hslColor2;

	public static void load(Archive archive)
	{
		Buffer buffer = new Buffer(archive.getFile("flo.dat"));
		count = buffer.getUnsignedShortBE();
		if (cache == null)
		{
			cache = new FloorDefinition[count];
		}
		for (int floor = 0; floor < count; floor++)
		{
			if (cache[floor] == null)
			{
				cache[floor] = new FloorDefinition();
			}
			cache[floor].loadDefinition(buffer);
		}

	}

	public void loadDefinition(Buffer buffer)
	{
		while (true)
		{
			int attributeId = buffer.getUnsignedByte();
			if (attributeId == 0)
			{
				return;
			}
			switch (attributeId)
			{
				case 1:
					rgbColor = buffer.getMediumBE();
					shiftRGBColors(rgbColor);
					break;
				case 2:
					textureId = buffer.getUnsignedByte();
					break;
				case 3:
					aBoolean318 = true;
					break;
				case 5:
					occlude = false;
					break;
				case 6:
					name = buffer.getString();
					break;
				case 7:
					int oldHue2 = hue2;
					int oldSaturation = saturation;
					int oldLightness = lightness;
					int oldHue = hue;
					shiftRGBColors(buffer.getMediumBE());
					hue2 = oldHue2;
					saturation = oldSaturation;
					lightness = oldLightness;
					hue = oldHue;
					hueDivisor = oldHue;
					break;
				default:
					System.out.println("Error unrecognised config code: " + attributeId);
					break;
			}
		}
	}

	public void shiftRGBColors(int color)
	{
		double r = (color >> 16 & 0xff) / 256D;
		double b = (color >> 8 & 0xff) / 256D;
		double g = (color & 0xff) / 256D;
		double cmin = r;
		if (b < cmin)
		{
			cmin = b;
		}
		if (g < cmin)
		{
			cmin = g;
		}
		double cmax = r;
		if (b > cmax)
		{
			cmax = b;
		}
		if (g > cmax)
		{
			cmax = g;
		}
		double d5 = 0.0D;
		double d6 = 0.0D;
		double d7 = (cmin + cmax) / 2D;
		if (cmin != cmax)
		{
			if (d7 < 0.5D)
			{
				d6 = (cmax - cmin) / (cmax + cmin);
			}
			if (d7 >= 0.5D)
			{
				d6 = (cmax - cmin) / (2D - cmax - cmin);
			}
			if (r == cmax)
			{
				d5 = (b - g) / (cmax - cmin);
			}
			else if (b == cmax)
			{
				d5 = 2D + (g - r) / (cmax - cmin);
			}
			else if (g == cmax)
			{
				d5 = 4D + (r - b) / (cmax - cmin);
			}
		}
		d5 /= 6D;
		hue2 = (int) (d5 * 256D);
		saturation = (int) (d6 * 256D);
		lightness = (int) (d7 * 256D);
		if (saturation < 0)
		{
			saturation = 0;
		}
		else if (saturation > 255)
		{
			saturation = 255;
		}
		if (lightness < 0)
		{
			lightness = 0;
		}
		else if (lightness > 255)
		{
			lightness = 255;
		}
		if (d7 > 0.5D)
		{
			hueDivisor = (int) ((1.0D - d7) * d6 * 512D);
		}
		else
		{
			hueDivisor = (int) (d7 * d6 * 512D);
		}
		if (hueDivisor < 1)
		{
			hueDivisor = 1;
		}
		hue = (int) (d5 * hueDivisor);
		int huerand = (hue2 + (int) (Math.random() * 16D)) - 8;
		if (huerand < 0)
		{
			huerand = 0;
		}
		else if (huerand > 255)
		{
			huerand = 255;
		}
		int satrand = (saturation + (int) (Math.random() * 48D)) - 24;
		if (satrand < 0)
		{
			satrand = 0;
		}
		else if (satrand > 255)
		{
			satrand = 255;
		}
		int lightrand = (lightness + (int) (Math.random() * 48D)) - 24;
		if (lightrand < 0)
		{
			lightrand = 0;
		}
		else if (lightrand > 255)
		{
			lightrand = 255;
		}
		hslColor2 = shiftHSLColors(huerand, satrand, lightrand);
	}

	public int shiftHSLColors(int i, int j, int k)
	{
		if (k > 179)
		{
			j /= 2;
		}
		if (k > 192)
		{
			j /= 2;
		}
		if (k > 217)
		{
			j /= 2;
		}
		if (k > 243)
		{
			j /= 2;
		}
		int l = (i / 4 << 10) + (j / 32 << 7) + k / 2;
		return l;
	}


}
