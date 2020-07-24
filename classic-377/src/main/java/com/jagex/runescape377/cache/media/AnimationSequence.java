package com.jagex.runescape377.cache.media;

import com.jagex.runescape377.cache.Archive;
import com.jagex.runescape377.media.Animation;
import com.jagex.runescape377.net.Buffer;

public class AnimationSequence
{

	public static int count;
	public static AnimationSequence animations[];
	public int frameCount;
	public int getPrimaryFrame[];
	public int frame1Ids[];
	public int frameLenghts[];
	public int frameStep = -1;
	public int flowControl[];
	public boolean dynamic = false;
	public int anInt301 = 5;
	public int getPlayerShieldDelta = -1;
	public int getPlayerWeaponDelta = -1;
	public int resetCycle = 99;
	public int speedFlag = -1;
	public int priority = -1;
	public int anInt307 = 2;

	public static void load(Archive archive)
	{
		Buffer buffer = new Buffer(archive.getFile("seq.dat"));
		AnimationSequence.count = buffer.getUnsignedShortBE();
		if (AnimationSequence.animations == null)
		{
			AnimationSequence.animations = new AnimationSequence[AnimationSequence.count];
		}
		for (int animation = 0; animation < count; animation++)
		{
			if (AnimationSequence.animations[animation] == null)
			{
				AnimationSequence.animations[animation] = new AnimationSequence();
			}
			AnimationSequence.animations[animation].loadDefinition(buffer);
		}
	}

	public int getFrameLength(int animationId)
	{
		int frameLength = frameLenghts[animationId];
		if (frameLength == 0)
		{
			Animation animation = Animation.getAnimation(getPrimaryFrame[animationId]);
			if (animation != null)
			{
				frameLength = frameLenghts[animationId] = animation.anInt431;
			}
		}
		if (frameLength == 0)
		{
			frameLength = 1;
		}
		return frameLength;
	}

	public void loadDefinition(Buffer buf)
	{
		while (true)
		{
			int attributeId = buf.getUnsignedByte();
			if (attributeId == 0)
			{
				break;
			}
			switch (attributeId)
			{
				case 1:
					frameCount = buf.getUnsignedByte();
					getPrimaryFrame = new int[frameCount];
					frame1Ids = new int[frameCount];
					frameLenghts = new int[frameCount];
					for (int frame = 0; frame < frameCount; frame++)
					{
						getPrimaryFrame[frame] = buf.getUnsignedShortBE();
						frame1Ids[frame] = buf.getUnsignedShortBE();
						if (frame1Ids[frame] == 65535)
						{
							frame1Ids[frame] = -1;
						}
						frameLenghts[frame] = buf.getUnsignedShortBE();
					}

					break;
				case 2:
					frameStep = buf.getUnsignedShortBE();
					break;
				case 3:
					int flowCount = buf.getUnsignedByte();
					flowControl = new int[flowCount + 1];
					for (int flow = 0; flow < flowCount; flow++)
					{
						flowControl[flow] = buf.getUnsignedByte();
					}

					flowControl[flowCount] = 0x98967f;
					break;
				case 4:
					dynamic = true;
					break;
				case 5:
					anInt301 = buf.getUnsignedByte();
					break;
				case 6:
					getPlayerShieldDelta = buf.getUnsignedShortBE();
					break;
				case 7:
					getPlayerWeaponDelta = buf.getUnsignedShortBE();
					break;
				case 8:
					resetCycle = buf.getUnsignedByte();
					break;
				case 9:
					speedFlag = buf.getUnsignedByte();
					break;
				case 10:
					priority = buf.getUnsignedByte();
					break;
				case 11:
					anInt307 = buf.getUnsignedByte();
					break;
				case 12:
					buf.getIntBE(); //dummy
					break;
				default:
					System.out.println("Error unrecognised seq config code: " + attributeId);
					break;
			}
		}
		if (frameCount == 0)
		{
			frameCount = 1;
			getPrimaryFrame = new int[1];
			getPrimaryFrame[0] = -1;
			frame1Ids = new int[1];
			frame1Ids[0] = -1;
			frameLenghts = new int[1];
			frameLenghts[0] = -1;
		}
		if (speedFlag == -1)
		{
			if (flowControl != null)
			{
				speedFlag = 2;
			}
			else
			{
				speedFlag = 0;
			}
		}
		if (priority == -1)
		{
			if (flowControl != null)
			{
				priority = 2;
				return;
			}
			priority = 0;
		}
	}


}
