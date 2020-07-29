package com.jagex.runescape377.media.renderable;

import com.jagex.runescape377.cache.media.SpotAnimation;
import com.jagex.runescape377.media.Animation;

public class GameAnimableObject extends Renderable
{

	public int plane;
	public int x;
	public int y;
	public int z;
	public boolean transformCompleted = false;
	public int eclapsedFrames;
	public int duration;
	public SpotAnimation animation;
	public int loopCycle;


	public GameAnimableObject(int plane, int loopCycle, int loopCycleOffset, int animationIndex, int z, int y, int x)
	{
		this.animation = SpotAnimation.cache[animationIndex];
		this.plane = plane;
		this.x = x;
		this.y = y;
		this.z = z;
		this.loopCycle = loopCycle + loopCycleOffset;
		this.transformCompleted = false;
	}

	public void nextFrame(int frame)
	{
		duration += frame;
		while (duration > animation.sequences.getFrameLength(eclapsedFrames))
		{
			duration -= animation.sequences.getFrameLength(eclapsedFrames);
			eclapsedFrames++;
			if (eclapsedFrames >= animation.sequences.frameCount
				&& (eclapsedFrames < 0 || eclapsedFrames >= animation.sequences.frameCount))
			{
				eclapsedFrames = 0;
				transformCompleted = true;
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
		int frame = animation.sequences.getPrimaryFrame[eclapsedFrames];
		Model animatedModel = new Model(true,
			model, Animation.exists(frame));
		if (!transformCompleted)
		{
			animatedModel.createBones();
			animatedModel.applyTransform(frame);
			animatedModel.triangleSkin = null;
			animatedModel.vectorSkin = null;
		}
		if (animation.resizeXY != 128 || animation.resizeZ != 128)
		{
			animatedModel.scaleT(animation.resizeZ, animation.resizeXY, 9,
				animation.resizeXY);
		}
		if (animation.rotation != 0)
		{
			if (animation.rotation == 90)
			{
				animatedModel.rotate90Degrees();
			}
			if (animation.rotation == 180)
			{
				animatedModel.rotate90Degrees();
				animatedModel.rotate90Degrees();
			}
			if (animation.rotation == 270)
			{
				animatedModel.rotate90Degrees();
				animatedModel.rotate90Degrees();
				animatedModel.rotate90Degrees();
			}
		}
		animatedModel.applyLighting(64 + animation.modelLightFalloff, 850 + animation.modelLightAmbient, -30, -50, -30,
			true);
		return animatedModel;
	}


}
