package com.jagex.runescape377.cache.media;

import com.jagex.runescape377.cache.Archive;
import com.jagex.runescape377.collection.Cache;
import com.jagex.runescape377.media.renderable.Model;
import com.jagex.runescape377.net.Buffer;

public class SpotAnimation
{

	public static int spotAnimationCount;
	public static SpotAnimation cache[];
	public static Cache modelCache = new Cache(30);
	public int id;
	public int modelId;
	public int animationId = -1;
	public AnimationSequence sequences;
	public int originalModelColors[] = new int[6];
	public int modifiedModelColors[] = new int[6];
	public int resizeXY = 128;
	public int resizeZ = 128;
	public int rotation;
	public int modelLightFalloff;
	public int modelLightAmbient;

	public static void load(Archive archive)
	{
		Buffer buffer = new Buffer(archive.getFile("spotanim.dat"));
		SpotAnimation.spotAnimationCount = buffer.getUnsignedShortBE();
		if (SpotAnimation.cache == null)
		{
			SpotAnimation.cache = new SpotAnimation[SpotAnimation.spotAnimationCount];
		}
		for (int spotAnimation = 0; spotAnimation < spotAnimationCount; spotAnimation++)
		{
			if (SpotAnimation.cache[spotAnimation] == null)
			{
				SpotAnimation.cache[spotAnimation] = new SpotAnimation();
			}
			SpotAnimation.cache[spotAnimation].id = spotAnimation;
			SpotAnimation.cache[spotAnimation].loadDefinition(buffer);
		}

	}

	public void loadDefinition(Buffer buffer)
	{
		while (true)
		{
			int attributeId = buffer.getUnsignedByte();
			if (attributeId == 0)
			{
				return;
			}
			if (attributeId == 1)
			{
				modelId = buffer.getUnsignedShortBE();
			}
			else if (attributeId == 2)
			{
				animationId = buffer.getUnsignedShortBE();
				if (AnimationSequence.animations != null)
				{
					sequences = AnimationSequence.animations[animationId];
				}
			}
			else if (attributeId == 4)
			{
				resizeXY = buffer.getUnsignedShortBE();
			}
			else if (attributeId == 5)
			{
				resizeZ = buffer.getUnsignedShortBE();
			}
			else if (attributeId == 6)
			{
				rotation = buffer.getUnsignedShortBE();
			}
			else if (attributeId == 7)
			{
				modelLightFalloff = buffer.getUnsignedByte();
			}
			else if (attributeId == 8)
			{
				modelLightAmbient = buffer.getUnsignedByte();
			}
			else if (attributeId >= 40 && attributeId < 50)
			{
				originalModelColors[attributeId - 40] = buffer.getUnsignedShortBE();
			}
			else if (attributeId >= 50 && attributeId < 60)
			{
				modifiedModelColors[attributeId - 50] = buffer.getUnsignedShortBE();
			}
			else
			{
				System.out.println("Error unrecognised spotanim config code: " + attributeId);
			}
		}
	}

	public Model getModel()
	{
		Model model = (Model) modelCache.get(id);
		if (model != null)
		{
			return model;
		}

		model = Model.getModel(modelId);
		if (model == null)
		{
			return null;
		}

		for (int nodelColor = 0; nodelColor < 6; nodelColor++)
		{
			if (originalModelColors[0] != 0)
			{
				model.replaceColor(originalModelColors[nodelColor], modifiedModelColors[nodelColor]);
			}
		}

		SpotAnimation.modelCache.put(model, id);
		return model;
	}


}
