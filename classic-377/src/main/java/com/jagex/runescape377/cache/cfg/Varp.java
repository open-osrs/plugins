package com.jagex.runescape377.cache.cfg;

import com.jagex.runescape377.cache.Archive;
import com.jagex.runescape377.net.Buffer;

public class Varp
{

	public static int count;
	public static Varp cache[];
	public static int currentIndex;
	public static int anIntArray706[];
	public String aString707;
	public int anInt708;
	public int anInt709;
	public boolean aBoolean710 = false;
	public boolean aBoolean711 = true;
	public int anInt712;
	public boolean aBoolean713 = false;
	public int anInt714;
	public int anInt715;
	public boolean aBoolean716 = false;
	public int anInt717 = -1;
	public boolean aBoolean718 = true;

	public static void load(Archive archive)
	{
		Buffer buffer = new Buffer(archive.getFile("varp.dat"));
		currentIndex = 0;
		count = buffer.getUnsignedShortBE();

		if (cache == null)
		{
			cache = new Varp[count];
		}

		if (anIntArray706 == null)
		{
			anIntArray706 = new int[count];
		}

		for (int index = 0; index < count; index++)
		{
			if (cache[index] == null)
			{
				cache[index] = new Varp();
			}
			cache[index].loadDefinition(index, buffer);
		}

		if (buffer.currentPosition != buffer.buffer.length)
		{
			System.out.println("varptype load mismatch");
		}
	}

	public void loadDefinition(int index, Buffer buffer)
	{
		while (true)
		{
			int attribute = buffer.getUnsignedByte();
			if (attribute == 0)
			{
				return;
			}
			if (attribute == 1)
			{
				anInt708 = buffer.getUnsignedByte();
			}
			else if (attribute == 2)
			{
				anInt709 = buffer.getUnsignedByte();
			}
			else if (attribute == 3)
			{
				aBoolean710 = true;
				anIntArray706[currentIndex++] = index;
			}
			else if (attribute == 4)
			{
				aBoolean711 = false;
			}
			else if (attribute == 5)
			{
				anInt712 = buffer.getUnsignedShortBE();
			}
			else if (attribute == 6)
			{
				aBoolean713 = true;
			}
			else if (attribute == 7)
			{
				anInt714 = buffer.getIntBE();
			}
			else if (attribute == 8)
			{
				anInt715 = 1;
				aBoolean716 = true;
			}
			else if (attribute == 10)
			{
				aString707 = buffer.getString();
			}
			else if (attribute == 11)
			{
				aBoolean716 = true;
			}
			else if (attribute == 12)
			{
				anInt717 = buffer.getIntBE();
			}
			else if (attribute == 13)
			{
				anInt715 = 2;
				aBoolean716 = true;
			}
			else if (attribute == 14)
			{
				aBoolean718 = false;
			}
			else
			{
				System.out.println("Error unrecognised config code: " + attribute);
			}
		}
	}


}
