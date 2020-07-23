package com.jagex.runescape377.cache.cfg;

import com.jagex.runescape377.cache.Archive;
import com.jagex.runescape377.net.Buffer;

public class Varbit
{
	public static int count;
	public static Varbit cache[];
	public int configId;
	public int leastSignificantBit;
	public int mostSignificantBit;
	public boolean aBoolean829 = false;
	public boolean aBoolean832 = true;

	public static void load(Archive archive)
	{
		Buffer buffer = new Buffer(archive.getFile("varbit.dat"));
		count = buffer.getUnsignedShortBE();

		if (cache == null)
		{
			cache = new Varbit[count];
		}

		for (int index = 0; index < count; index++)
		{
			if (cache[index] == null)
			{
				cache[index] = new Varbit();
			}
			cache[index].init(buffer);
			if (cache[index].aBoolean829)
			{
				Varp.cache[cache[index].configId].aBoolean716 = true;
			}
		}

		if (buffer.currentPosition != buffer.buffer.length)
		{
			System.out.println("varbit load mismatch");
		}
	}

	public void init(Buffer buf)
	{
		while (true)
		{
			int attribute = buf.getUnsignedByte();
			if (attribute == 0)
			{
				return;
			}
			if (attribute == 1)
			{
				configId = buf.getUnsignedShortBE();
				leastSignificantBit = buf.getUnsignedByte();
				mostSignificantBit = buf.getUnsignedByte();
			}
			else if (attribute == 10)
			{
				buf.getString(); // dummy
			}
			else if (attribute == 2)
			{
				aBoolean829 = true;
			}
			else if (attribute == 3)
			{
				buf.getIntBE(); // dummy
			}
			else if (attribute == 4)
			{
				buf.getIntBE(); // dummy
			}
			else if (attribute == 5)
			{
				aBoolean832 = false;
			}
			else
			{
				System.out.println("Error unrecognised config code: " + attribute);
			}
		}
	}


}
