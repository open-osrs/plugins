package com.jagex.runescape377.media;

import com.jagex.runescape377.net.Buffer;

public class Animation
{
	public static Animation[] cache;
	public static boolean aBooleanArray438[];
	public int anInt431;
	public Skins animationSkins;
	public int anInt433;
	public int opcodeTable[];
	public int modifier1[];
	public int modifier2[];
	public int modifier3[];

	public Animation()
	{
	}

	public static void method235(int i)
	{
		cache = new Animation[i + 1];
		aBooleanArray438 = new boolean[i + 1];
		for (int j = 0; j < i + 1; j++)
		{
			aBooleanArray438[j] = true;
		}

	}

	public static void method236(byte[] bs)
	{
		Buffer buffer = new Buffer(bs);
		buffer.currentPosition = bs.length - 8;
		int i = buffer.getUnsignedShortBE();
		int j = buffer.getUnsignedShortBE();
		int k = buffer.getUnsignedShortBE();
		int l = buffer.getUnsignedShortBE();
		int i1 = 0;
		Buffer buffer_5_ = new Buffer(bs);
		buffer_5_.currentPosition = i1;
		i1 += i + 2;
		Buffer class50_sub1_sub2_2 = new Buffer(bs);
		class50_sub1_sub2_2.currentPosition = i1;
		i1 += j;
		Buffer class50_sub1_sub2_3 = new Buffer(bs);
		class50_sub1_sub2_3.currentPosition = i1;
		i1 += k;
		Buffer class50_sub1_sub2_4 = new Buffer(bs);
		class50_sub1_sub2_4.currentPosition = i1;
		i1 += l;
		Buffer buffer_9_ = new Buffer(bs);
		buffer_9_.currentPosition = i1;
		Skins skins = new Skins(buffer_9_);
		int animationAmount = buffer_5_.getUnsignedShortBE();
		int ai[] = new int[500];
		int ai1[] = new int[500];
		int ai2[] = new int[500];
		int ai3[] = new int[500];
		for (int k1 = 0; k1 < animationAmount; k1++)
		{
			int l1 = buffer_5_.getUnsignedShortBE();
			Animation animation = cache[l1] = new Animation();
			animation.anInt431 = class50_sub1_sub2_4.getUnsignedByte();
			animation.animationSkins = skins;
			int i2 = buffer_5_.getUnsignedByte();
			int j2 = -1;
			int k2 = 0;
			for (int l2 = 0; l2 < i2; l2++)
			{
				int i3 = class50_sub1_sub2_2.getUnsignedByte();
				if (i3 > 0)
				{
					if (skins.opcodes[l2] != 0)
					{
						for (int k3 = l2 - 1; k3 > j2; k3--)
						{
							if (skins.opcodes[k3] != 0)
							{
								continue;
							}
							ai[k2] = k3;
							ai1[k2] = 0;
							ai2[k2] = 0;
							ai3[k2] = 0;
							k2++;
							break;
						}

					}
					ai[k2] = l2;
					char c = '\0';
					if (skins.opcodes[l2] == 3)
					{
						c = '\200';
					}
					if ((i3 & 1) != 0)
					{
						ai1[k2] = class50_sub1_sub2_3.getSignedSmart();
					}
					else
					{
						ai1[k2] = c;
					}
					if ((i3 & 2) != 0)
					{
						ai2[k2] = class50_sub1_sub2_3.getSignedSmart();
					}
					else
					{
						ai2[k2] = c;
					}
					if ((i3 & 4) != 0)
					{
						ai3[k2] = class50_sub1_sub2_3.getSignedSmart();
					}
					else
					{
						ai3[k2] = c;
					}
					j2 = l2;
					k2++;
					if (skins.opcodes[l2] == 5)
					{
						aBooleanArray438[l1] = false;
					}
				}
			}

			animation.anInt433 = k2;
			animation.opcodeTable = new int[k2];
			animation.modifier1 = new int[k2];
			animation.modifier2 = new int[k2];
			animation.modifier3 = new int[k2];
			for (int j3 = 0; j3 < k2; j3++)
			{
				animation.opcodeTable[j3] = ai[j3];
				animation.modifier1[j3] = ai1[j3];
				animation.modifier2[j3] = ai2[j3];
				animation.modifier3[j3] = ai3[j3];
			}

		}

	}

	public static void reset()
	{
		cache = null;
	}

	public static Animation getAnimation(int animationId)
	{
		if (cache == null)
		{
			return null;
		}
		else
		{
			return cache[animationId];
		}
	}

	public static boolean exists(int i)
	{
		return i == -1;
	}


}
