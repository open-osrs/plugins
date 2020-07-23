package com.jagex.runescape377.media.renderable;

import com.jagex.runescape377.Game;
import com.jagex.runescape377.cache.cfg.Varbit;
import com.jagex.runescape377.cache.def.GameObjectDefinition;
import com.jagex.runescape377.cache.media.AnimationSequence;

public class GameObject extends Renderable
{
	public static Game client;
	public int vertexHeight;
	public int vertexHeightRight;
	public int vertexHeightTopRight;
	public int vertexHeightTop;
	public int id;
	public int clickType;
	public int face;
	public AnimationSequence animationSequence;
	public int varbitId;
	public int configId;
	public int[] childrenIds;
	public int animationCycleDelay;
	public int animationFrame;

	public GameObject(int id, int face, int clickType, int vertexHeightRight, int vertexHeightTopRight, int vertexHeight, int vertexHeightTop, int animationId, boolean flag)
	{
		this.id = id;
		this.clickType = clickType;
		this.face = face;
		this.vertexHeight = vertexHeight;
		this.vertexHeightRight = vertexHeightRight;
		this.vertexHeightTopRight = vertexHeightTopRight;
		this.vertexHeightTop = vertexHeightTop;
		if (animationId != -1)
		{
			animationSequence = AnimationSequence.animations[animationId];
			animationFrame = 0;
			animationCycleDelay = client.pulseCycle - 1;
			if (flag && animationSequence.frameStep != -1)
			{
				animationFrame = (int) (Math.random() * animationSequence.frameCount);
				animationCycleDelay -= (int) (Math.random() * animationSequence.getFrameLength(animationFrame));
			}
		}
		GameObjectDefinition gameObjectDefinition = GameObjectDefinition.getDefinition(this.id);
		varbitId = gameObjectDefinition.varbitId;
		configId = gameObjectDefinition.configId;
		childrenIds = gameObjectDefinition.childrenIds;
	}

	public GameObjectDefinition getChildDefinition()
	{
		int child = -1;
		if (varbitId != -1)
		{
			Varbit varbit = Varbit.cache[varbitId];
			int configId = varbit.configId;
			int leastSignificantBit = varbit.leastSignificantBit;
			int mostSignificantBit = varbit.mostSignificantBit;
			int bit = client.BITFIELD_MAX_VALUE[mostSignificantBit - leastSignificantBit];
			child = client.widgetSettings[configId] >> leastSignificantBit & bit;
		}
		else if (configId != -1)
		{
			child = client.widgetSettings[configId];
		}
		if (child < 0 || child >= childrenIds.length || childrenIds[child] == -1)
		{
			return null;
		}
		else
		{
			return GameObjectDefinition.getDefinition(childrenIds[child]);
		}
	}


	@Override
	public Model getRotatedModel()
	{
		int animation = -1;
		if (animationSequence != null)
		{
			int step = client.pulseCycle - animationCycleDelay;
			if (step > 100 && animationSequence.frameStep > 0)
			{
				step = 100;
			}
			while (step > animationSequence.getFrameLength(animationFrame))
			{
				step -= animationSequence.getFrameLength(animationFrame);
				animationFrame++;
				if (animationFrame < animationSequence.frameCount)
				{
					continue;
				}
				animationFrame -= animationSequence.frameStep;
				if (animationFrame >= 0 && animationFrame < animationSequence.frameCount)
				{
					continue;
				}
				animationSequence = null;
				break;
			}
			animationCycleDelay = client.pulseCycle - step;
			if (animationSequence != null)
			{
				animation = animationSequence.getPrimaryFrame[animationFrame];
			}
		}
		GameObjectDefinition gameObjectDefinition;
		if (childrenIds != null)
		{
			gameObjectDefinition = getChildDefinition();
		}
		else
		{
			gameObjectDefinition = GameObjectDefinition.getDefinition(id);
		}
		if (gameObjectDefinition == null)
		{
			return null;
		}
		else
		{
			Model model = gameObjectDefinition.getGameObjectModel(clickType, face, vertexHeight,
				vertexHeightRight, vertexHeightTopRight, vertexHeightTop, animation);
			return model;
		}
	}


}
