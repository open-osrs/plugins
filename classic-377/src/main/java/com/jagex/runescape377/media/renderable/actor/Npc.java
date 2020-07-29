package com.jagex.runescape377.media.renderable.actor;

import com.jagex.runescape377.cache.def.ActorDefinition;
import com.jagex.runescape377.cache.media.AnimationSequence;
import com.jagex.runescape377.cache.media.SpotAnimation;
import com.jagex.runescape377.media.Animation;
import com.jagex.runescape377.media.renderable.Model;

public class Npc extends Actor
{
	public ActorDefinition npcDefinition;

	public Model getChildModel()
	{
		if (super.emoteAnimation >= 0 && super.animationDelay == 0)
		{
			int frameId = AnimationSequence.animations[super.emoteAnimation].getPrimaryFrame[super.displayedEmoteFrames];
			int frameId2 = -1;
			if (super.movementAnimation >= 0 && super.movementAnimation != super.idleAnimation)
			{
				frameId2 = AnimationSequence.animations[super.movementAnimation].getPrimaryFrame[super.displayedMovementFrames];
			}
			return npcDefinition.getChildModel(frameId, frameId2, AnimationSequence.animations[super.emoteAnimation].flowControl);
		}
		int j = -1;
		if (super.movementAnimation >= 0)
		{
			j = AnimationSequence.animations[super.movementAnimation].getPrimaryFrame[super.displayedMovementFrames];
		}
		return npcDefinition.getChildModel(j, -1, null);
	}

	@Override
	public Model getRotatedModel()
	{
		if (npcDefinition == null)
		{
			return null;
		}
		Model model = getChildModel();
		if (model == null)
		{
			return null;
		}
		super.modelHeight = model.modelHeight;
		if (super.graphic != -1 && super.currentAnimation != -1)
		{
			SpotAnimation spotanimation = SpotAnimation.cache[super.graphic];
			Model model_4_ = spotanimation.getModel();
			if (model_4_ != null)
			{
				int animationId = spotanimation.sequences.getPrimaryFrame[super.currentAnimation];
				Model animationModel = new Model(true,
					model_4_, Animation.exists(animationId));
				animationModel.translate(0, 0, -super.spotGraphicHeight);
				animationModel.createBones();
				animationModel.applyTransform(animationId);
				animationModel.triangleSkin = null;
				animationModel.vectorSkin = null;
				if (spotanimation.resizeXY != 128 || spotanimation.resizeZ != 128)
				{
					animationModel.scaleT(spotanimation.resizeZ, spotanimation.resizeXY, 9, spotanimation.resizeXY);
				}
				animationModel.applyLighting(64 + spotanimation.modelLightFalloff, 850 + spotanimation.modelLightAmbient, -30, -50, -30, true);
				Model[] models = {model, animationModel};
				model = new Model(models);
			}
		}
		if (npcDefinition.boundaryDimension == 1)
		{
			model.singleTile = true;
		}
		return model;
	}

	@Override
	public boolean isVisible()
	{
		return npcDefinition != null;
	}
}
