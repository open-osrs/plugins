package com.jagex.runescape377.media;

import com.jagex.runescape377.net.Buffer;

public class Skins
{

	public int count;
	public int[] opcodes;
	public int[][] skinList;

	public Skins(Buffer buffer)
	{
		count = buffer.getUnsignedByte();
		opcodes = new int[count];
		skinList = new int[count][];
		for (int opcode = 0; opcode < count; opcode++)
		{
			opcodes[opcode] = buffer.getUnsignedByte();
		}

		for (int skin = 0; skin < count; skin++)
		{
			int subSkinAmount = buffer.getUnsignedByte();
			skinList[skin] = new int[subSkinAmount];
			for (int subSkin = 0; subSkin < subSkinAmount; subSkin++)
			{
				skinList[skin][subSkin] = buffer.getUnsignedByte();
			}

		}

	}


}
