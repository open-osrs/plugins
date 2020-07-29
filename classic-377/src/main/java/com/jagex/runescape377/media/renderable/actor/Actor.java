package com.jagex.runescape377.media.renderable.actor;

import com.jagex.runescape377.cache.media.AnimationSequence;
import com.jagex.runescape377.media.renderable.Renderable;

public abstract class Actor extends Renderable
{
	public String forcedChat;
	public int textCycle = 100;
	public int textColour;
	public int nextStepOrientation;
	public int pulseCycle;
	public int[] pathX = new int[10];
	public int[] pathY = new int[10];
	public int movementAnimation = -1;
	public int displayedMovementFrames;
	public int movementCycle;
	public boolean[] runningQueue = new boolean[10];
	public boolean dynamic = false;
	public int textEffect;
	public int modelHeight = 200;
	public int endCycle = -1000;
	public int anInt1596;
	public int anInt1597;
	public int faceX;
	public int faceY;
	public int turnSpeed = 32;
	public int boundaryDimension = 1;
	public int movementStartX;
	public int movementEndX;
	public int movementStartY;
	public int movementEndY;
	public int moveCycleEnd;
	public int moveCycleStart;
	public int moveDirection;
	public int faceActor = -1;
	public int worldX;
	public int worldY;
	public int currentRotation;
	public int stillPathPosition;
	public int graphic = -1;
	public int currentAnimation;
	public int animationCycle;
	public int spotGraphicDelay;
	public int spotGraphicHeight;
	public int walkAnimationId = -1;
	public int turnAroundAnimationId = -1;
	public int turnRightAnimationId = -1;
	public int turnLeftAnimationId = -1;
	public int resyncWalkCycle;
	public int emoteAnimation = -1;
	public int displayedEmoteFrames;
	public int animationSequence;
	public int animationDelay;
	public int animationResetCycle;
	public int runAnimationId = -1;
	public int[] hitDamages = new int[4];
	public int[] hitTypes = new int[4];
	public int[] hitCycles = new int[4];
	public int pathLength;
	public int idleAnimation = -1;
	public int standTurnAnimationId = -1;

	public void resetPath()
	{
		pathLength = 0;
		stillPathPosition = 0;
	}

	public boolean isVisible()
	{
		return false;
	}

	public void move(int direction, boolean running)
	{
		int x = pathX[0];
		int y = pathY[0];
		if (direction == 0)
		{
			x--;
			y++;
		}
		if (direction == 1)
		{
			y++;
		}
		if (direction == 2)
		{
			x++;
			y++;
		}
		if (direction == 3)
		{
			x--;
		}
		if (direction == 4)
		{
			x++;
		}
		if (direction == 5)
		{
			x--;
			y--;
		}
		if (direction == 6)
		{
			y--;
		}
		if (direction == 7)
		{
			x++;
			y--;
		}
		if (emoteAnimation != -1 && AnimationSequence.animations[emoteAnimation].priority == 1)
		{
			emoteAnimation = -1;
		}
		if (pathLength < 9)
		{
			pathLength++;
		}
		for (int pos = pathLength; pos > 0; pos--)
		{
			pathX[pos] = pathX[pos - 1];
			pathY[pos] = pathY[pos - 1];
			runningQueue[pos] = runningQueue[pos - 1];
		}

		pathX[0] = x;
		pathY[0] = y;
		runningQueue[0] = running;
	}

	public void updateHits(int hitType, int hitDamage, int hitCycle)
	{
		for (int hit = 0; hit < 4; hit++)
		{
			if (hitCycles[hit] <= hitCycle)
			{
				hitDamages[hit] = hitDamage;
				hitTypes[hit] = hitType;
				hitCycles[hit] = hitCycle + 70;
				return;
			}
		}

	}

	public void setPosition(int x, int y, boolean discard)
	{
		if (emoteAnimation != -1 && AnimationSequence.animations[emoteAnimation].priority == 1)
		{
			emoteAnimation = -1;
		}
		if (!discard)
		{
			int k = x - pathX[0];
			int i1 = y - pathY[0];
			if (k >= -8 && k <= 8 && i1 >= -8 && i1 <= 8)
			{
				if (pathLength < 9)
				{
					pathLength++;
				}
				for (int j1 = pathLength; j1 > 0; j1--)
				{
					pathX[j1] = pathX[j1 - 1];
					pathY[j1] = pathY[j1 - 1];
					runningQueue[j1] = runningQueue[j1 - 1];
				}

				pathX[0] = x;
				pathY[0] = y;
				runningQueue[0] = false;
				return;
			}
		}
		pathLength = 0;
		stillPathPosition = 0;
		resyncWalkCycle = 0;
		pathX[0] = x;
		pathY[0] = y;
		worldX = pathX[0] * 128 + boundaryDimension * 64;
		worldY = pathY[0] * 128 + boundaryDimension * 64;
	}
}
