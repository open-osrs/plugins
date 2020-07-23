package com.jagex.runescape377.media.renderable;

import com.jagex.runescape377.cache.media.SpotAnimation;
import com.jagex.runescape377.media.Animation;

public class Projectile extends Renderable
{
	public SpotAnimation animation;
	public int sceneId;
	public double currentX;
	public double currentY;
	public double currentHeight;
	public int startSlope;
	public int startDistanceFromTarget;
	public int targetedEntityId;
	public boolean aBoolean1561;
	public int anInt1562;
	public int anInt1563;
	public int delay;
	public int endCycle;
	public int animationFrame;
	public int duration;
	public double speedVectorX;
	public double speedVectorY;
	public double speedVectorScalar;
	public double speedVectorZ;
	public boolean aBoolean1573;
	public double heightOffset;
	public boolean moving;
	public int startX;
	public int startY;
	public int startHeight;
	public int endHeight;

	public Projectile(int sceneId, int endHeight, int startDistanceFromTarget, int projectileY, int graphicsId, int speed, int startSlope,
						int targetedEntityIndex, int height, int projectileX, int delay)
	{
		this.aBoolean1561 = false;
		this.aBoolean1573 = true;
		this.animation = SpotAnimation.cache[graphicsId];
		this.sceneId = sceneId;
		this.startX = projectileX;
		this.startY = projectileY;
		this.startHeight = height;
		this.delay = delay;
		this.endCycle = speed;
		this.startSlope = startSlope;
		this.startDistanceFromTarget = startDistanceFromTarget;
		this.targetedEntityId = targetedEntityIndex;
		this.endHeight = endHeight;
		this.moving = false;
	}

	public void trackTarget(int targetX, int targetY, int k, int loopCycle)
	{
		if (!moving)
		{
			double distanceX = targetX - startX;
			double distanceY = targetY - startY;
			double distanceScalar = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
			currentX = startX + (distanceX * startDistanceFromTarget) / distanceScalar;
			currentY = startY + (distanceY * startDistanceFromTarget) / distanceScalar;
			currentHeight = startHeight;
		}
		double cyclesRemaining = (endCycle + 1) - loopCycle;
		speedVectorX = (targetX - currentX) / cyclesRemaining;
		speedVectorY = (targetY - currentY) / cyclesRemaining;
		speedVectorScalar = Math.sqrt(speedVectorX * speedVectorX + speedVectorY * speedVectorY);
		if (!moving)
		{
			speedVectorZ = -speedVectorScalar * Math.tan(startSlope * 0.02454369D);
		}
		heightOffset = (2D * (k - currentHeight - speedVectorZ * cyclesRemaining)) / (cyclesRemaining * cyclesRemaining);
	}

	public void move(int time)
	{
		moving = true;
		currentX += speedVectorX * time;
		currentY += speedVectorY * time;
		currentHeight += speedVectorZ * time + 0.5D * heightOffset * time * time;
		speedVectorZ += heightOffset * time;
		anInt1562 = (int) (Math.atan2(speedVectorX, speedVectorY) * 325.94900000000001D) + 1024 & 0x7ff;
		anInt1563 = (int) (Math.atan2(speedVectorZ, speedVectorScalar) * 325.94900000000001D) & 0x7ff;
		if (animation.sequences != null)
		{
			for (duration += time; duration > animation.sequences.getFrameLength(animationFrame); )
			{
				duration -= animation.sequences.getFrameLength(animationFrame);
				animationFrame++;
				if (animationFrame >= animation.sequences.frameCount)
				{
					animationFrame = 0;
				}
			}
		}

	}

	@Override
	public Model getRotatedModel()
	{
		Model model = animation.getModel();
		if (model == null)
		{
			return null;
		}
		int frameId = -1;
		if (animation.sequences != null)
		{
			frameId = animation.sequences.getPrimaryFrame[animationFrame];
		}
		Model projectileModel = new Model(true,
			model, Animation.exists(frameId));
		if (frameId != -1)
		{
			projectileModel.createBones();
			projectileModel.applyTransform(frameId);
			projectileModel.triangleSkin = null;
			projectileModel.vectorSkin = null;
		}
		if (animation.resizeXY != 128 || animation.resizeZ != 128)
		{
			projectileModel.scaleT(animation.resizeZ, animation.resizeXY, 9,
				animation.resizeXY);
		}
		projectileModel.rotateX(anInt1563);
		projectileModel.applyLighting(64 + animation.modelLightFalloff, 850 + animation.modelLightAmbient, -30, -50, -30,
			true);
		return projectileModel;
	}


}
