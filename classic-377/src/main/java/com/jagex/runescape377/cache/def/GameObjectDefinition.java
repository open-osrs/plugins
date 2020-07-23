package com.jagex.runescape377.cache.def;

import com.jagex.runescape377.Game;
import com.jagex.runescape377.cache.Archive;
import com.jagex.runescape377.cache.FileOperations;
import com.jagex.runescape377.cache.cfg.Varbit;
import com.jagex.runescape377.collection.Cache;
import com.jagex.runescape377.media.Animation;
import com.jagex.runescape377.media.renderable.Model;
import com.jagex.runescape377.net.Buffer;
import com.jagex.runescape377.net.requester.OnDemandRequester;

public class GameObjectDefinition
{

	public static int bufferOffsets[];
	public static Cache animatedModelCache = new Cache(40);
	public static Buffer buffer;
	public static Game client;
	public static Model models[] = new Model[4];
	public static boolean lowMemory;
	public static int cacheIndex;
	public static Cache modelCache = new Cache(500);
	public static GameObjectDefinition cache[];
	public static int definitionCount;
	public boolean hasActions;
	public int modelSizeY;
	public int translateX;
	public int modelIds[];
	public int anInt764;
	public boolean unknown;
	public int translateZ;
	public boolean adjustToTerrain;
	public int id = -1;
	public int sizeY;
	public String name = "null";
	public int varbitId;
	public int modelSizeX;
	public int configId;
	public byte description[];
	public byte modelLightFalloff;
	public int translateY;
	public boolean aBoolean786 = true;
	public byte modelLightAmbient;
	public int modelTypes[];
	public String options[];
	public boolean aBoolean791;
	public int anIntArray792[];
	public int anInt794;
	public int anInt795;
	public int modelSizeZ;
	public boolean wall;
	public boolean unknown3;
	public int modifiedModelColors[];
	public int sizeX;
	public int offsetAmplifier;
	public int animationId;
	public boolean nonFlatShading;
	public int childrenIds[];
	public int icon;
	public boolean castsShadow;
	public boolean walkable;
	public boolean solid;

	public static GameObjectDefinition getDefinition(int id)
	{
		for (int index = 0; index < 20; index++)
		{
			if (cache[index].id == id)
			{
				return cache[index];
			}
		}

		cacheIndex = (cacheIndex + 1) % 20;
		GameObjectDefinition definition = cache[cacheIndex];
		buffer.currentPosition = bufferOffsets[id];
		definition.id = id;
		definition.setDefaultValues();
		definition.load(buffer);
		return definition;
	}

	public static void load(Archive archive)
	{
		buffer = new Buffer(archive.getFile("loc.dat"));
		Buffer buffer = new Buffer(archive.getFile("loc.idx"));
		FileOperations.WriteFile("DumpedData/377_OBJECTS.dat", archive.getFile("loc.dat"));
		FileOperations.WriteFile("DumpedData/377_OBJECTS.idx", archive.getFile("loc.idx"));
		definitionCount = buffer.getUnsignedShortBE();
		bufferOffsets = new int[definitionCount];
		int offset = 2;
		for (int index = 0; index < definitionCount; index++)
		{
			bufferOffsets[index] = offset;
			offset += buffer.getUnsignedShortBE();
		}

		cache = new GameObjectDefinition[20];
		for (int definition = 0; definition < 20; definition++)
		{
			cache[definition] = new GameObjectDefinition();
		}

	}

	public static void method433(boolean flag)
	{
		modelCache = null;
		animatedModelCache = null;
		bufferOffsets = null;
		if (flag)
		{
			for (int i = 1; i > 0; i++)
			{
				;
			}
		}
		cache = null;
		buffer = null;
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
			return getDefinition(childrenIds[child]);
		}
	}

	public void setDefaultValues()
	{
		modelIds = null;
		modelTypes = null;
		name = "null";
		description = null;
		modifiedModelColors = null;
		anIntArray792 = null;
		sizeX = 1;
		sizeY = 1;
		solid = true;
		walkable = true;
		hasActions = false;
		adjustToTerrain = false;
		nonFlatShading = false;
		wall = false;
		animationId = -1;
		offsetAmplifier = 16;
		modelLightFalloff = 0;
		modelLightAmbient = 0;
		options = null;
		icon = -1;
		anInt795 = -1;
		unknown3 = false;
		castsShadow = true;
		modelSizeX = 128;
		modelSizeY = 128;
		modelSizeZ = 128;
		anInt764 = 0;
		translateX = 0;
		translateY = 0;
		translateZ = 0;
		unknown = false;
		aBoolean791 = false;
		anInt794 = -1;
		varbitId = -1;
		configId = -1;
		childrenIds = null;
	}

	public void passiveRequestModels(OnDemandRequester onDemandRequester)
	{
		if (modelIds != null)
		{
			for (int modelId : modelIds)
			{
				onDemandRequester.passiveRequest(modelId & 0xffff, 0);
			}
		}
	}

	public Model getGameObjectAnimatedModel(int type, int animationId, int face)
	{
		Model subModel = null;
		long hash;
		if (modelTypes == null)
		{
			if (type != 10)
			{
				return null;
			}
			hash = ((id << 6) + face) + ((long) (animationId + 1) << 32);
			Model cachedModel = (Model) animatedModelCache.get(hash);
			if (cachedModel != null)
			{
				return cachedModel;
			}
			if (modelIds == null)
			{
				return null;
			}
			boolean mirror = unknown3 ^ (face > 3);
			int modelCount = modelIds.length;
			for (int modelId = 0; modelId < modelCount; modelId++)
			{
				int subModelId = modelIds[modelId];
				if (mirror)
				{
					subModelId += 0x10000;
				}
				subModel = (Model) modelCache.get(subModelId);
				if (subModel == null)
				{
					subModel = Model.getModel(subModelId & 0xffff);
					if (subModel == null)
					{
						return null;
					}
					if (mirror)
					{
						subModel.mirror(0);
					}
					modelCache.put(subModel, subModelId);
				}
				if (modelCount > 1)
				{
					models[modelId] = subModel;
				}
			}

			if (modelCount > 1)
			{
				subModel = new Model(modelCount, models);
			}
		}
		else
		{
			int modelType = -1;
			for (int index = 0; index < modelTypes.length; index++)
			{
				if (modelTypes[index] != type)
				{
					continue;
				}
				modelType = index;
				break;
			}

			if (modelType == -1)
			{
				return null;
			}
			hash = ((id << 6) + (modelType << 3) + face) + ((long) (animationId + 1) << 32);
			Model model = (Model) animatedModelCache.get(hash);
			if (model != null)
			{
				return model;
			}
			int j2 = modelIds[modelType];
			boolean mirror = unknown3 ^ (face > 3);
			if (mirror)
			{
				j2 += 0x10000;
			}
			subModel = (Model) modelCache.get(j2);
			if (subModel == null)
			{
				subModel = Model.getModel(j2 & 0xffff);
				if (subModel == null)
				{
					return null;
				}
				if (mirror)
				{
					subModel.mirror(0);
				}
				modelCache.put(subModel, j2);
			}
		}
		boolean scale;
		if (modelSizeX != 128 || modelSizeY != 128 || modelSizeZ != 128)
		{
			scale = true;
		}
		else
		{
			scale = false;
		}
		boolean needsTranslation;
		if (translateX != 0 || translateY != 0 || translateZ != 0)
		{
			needsTranslation = true;
		}
		else
		{
			needsTranslation = false;
		}
		Model animatedModel = new Model(modifiedModelColors == null, subModel, Animation.exists(animationId));
		if (animationId != -1)
		{
			animatedModel.createBones();
			animatedModel.applyTransform(animationId);
			animatedModel.triangleSkin = null;
			animatedModel.vectorSkin = null;
		}
		while (face-- > 0)
		{
			animatedModel.rotate90Degrees();
		}
		if (modifiedModelColors != null)
		{
			for (int k2 = 0; k2 < modifiedModelColors.length; k2++)
			{
				animatedModel.replaceColor(modifiedModelColors[k2], anIntArray792[k2]);
			}

		}
		if (scale)
		{
			animatedModel.scaleT(modelSizeY, modelSizeZ, 9, modelSizeX);
		}
		if (needsTranslation)
		{
			animatedModel.translate(translateX, translateZ, translateY);
		}
		animatedModel.applyLighting(64 + modelLightFalloff, 768 + modelLightAmbient * 5, -50, -10, -50, !nonFlatShading);
		if (anInt794 == 1)
		{
			animatedModel.anInt1675 = animatedModel.modelHeight;
		}
		animatedModelCache.put(animatedModel, hash);
		return animatedModel;
	}

	public boolean isModelCached()
	{
		if (modelIds == null)
		{
			return true;
		}
		boolean cached = true;
		for (int modelId : modelIds)
		{
			cached &= Model.loaded(modelId & 0xffff);
		}
		return cached;
	}

	public void load(Buffer buf)
	{
		int i = -1;
		label0:
		while (true)
		{
			int attribute;
			do
			{
				attribute = buf.getUnsignedByte();
				if (attribute == 0)
				{
					break label0;
				}
				switch (attribute)
				{
					case 1:
						int k = buf.getUnsignedByte();
						if (k > 0)
						{
							if (modelIds == null || lowMemory)
							{
								modelTypes = new int[k];
								modelIds = new int[k];
								for (int k1 = 0; k1 < k; k1++)
								{
									modelIds[k1] = buf.getUnsignedShortBE();
									modelTypes[k1] = buf.getUnsignedByte();
								}

							}
							else
							{
								buf.currentPosition += k * 3;
							}
						}
						break;
					case 2:
						name = buf.getString();
						break;
					case 3:
						description = buf.getStringBytes();
						break;
					case 5:
						int l = buf.getUnsignedByte();
						if (l > 0)
						{
							if (modelIds == null || lowMemory)
							{
								modelTypes = null;
								modelIds = new int[l];
								for (int l1 = 0; l1 < l; l1++)
								{
									modelIds[l1] = buf.getUnsignedShortBE();
								}

							}
							else
							{
								buf.currentPosition += l * 2;
							}
						}
						break;
					case 14:
						sizeX = buf.getUnsignedByte();
						break;
					case 15:
						sizeY = buf.getUnsignedByte();
						break;
					case 17:
						solid = false;
						break;
					case 18:
						walkable = false;
						break;
					case 19:
						i = buf.getUnsignedByte();
						if (i == 1)
						{
							hasActions = true;
						}
						break;
					case 21:
						adjustToTerrain = true;
						break;
					case 22:
						nonFlatShading = true;
						break;
					case 23:
						wall = true;
						break;
					case 24:
						animationId = buf.getUnsignedShortBE();
						if (animationId == 65535)
						{
							animationId = -1;
						}
						break;
					case 28:
						offsetAmplifier = buf.getUnsignedByte();
						break;
					case 29:
						modelLightFalloff = buf.getByte();
						break;
					case 39:
						modelLightAmbient = buf.getByte();
						break;
				}
				if (attribute < 30 || attribute >= 39)
				{
					switch (attribute)
					{
						case 40:
							int i1 = buf.getUnsignedByte();
							modifiedModelColors = new int[i1];
							anIntArray792 = new int[i1];
							for (int i2 = 0; i2 < i1; i2++)
							{
								modifiedModelColors[i2] = buf.getUnsignedShortBE();
								anIntArray792[i2] = buf.getUnsignedShortBE();
							}

							break;
						case 60:
							icon = buf.getUnsignedShortBE();
							break;
						case 62:
							unknown3 = true;
							break;
						case 64:
							castsShadow = false;
							break;
						case 65:
							modelSizeX = buf.getUnsignedShortBE();
							break;
						case 66:
							modelSizeY = buf.getUnsignedShortBE();
							break;
						case 67:
							modelSizeZ = buf.getUnsignedShortBE();
							break;
						case 68:
							anInt795 = buf.getUnsignedShortBE();
							break;
						case 69:
							anInt764 = buf.getUnsignedByte();
							break;
						case 70:
							translateX = buf.getShortBE();
							break;
						case 71:
							translateY = buf.getShortBE();
							break;
						case 72:
							translateZ = buf.getShortBE();
							break;
						case 73:
							unknown = true;
							break;
						case 74:
							aBoolean791 = true;
							break;
						default:
							if (attribute != 75)
							{
								continue;
							}
							anInt794 = buf.getUnsignedByte();
							break;
					}
				}
				else
				{
					if (options == null)
					{
						options = new String[5];
					}
					options[attribute - 30] = buf.getString();
					if (options[attribute - 30].equalsIgnoreCase("hidden"))
					{
						options[attribute - 30] = null;
					}
				}
				continue label0;
			} while (attribute != 77);
			varbitId = buf.getUnsignedShortBE();
			if (varbitId == 65535)
			{
				varbitId = -1;
			}
			configId = buf.getUnsignedShortBE();
			if (configId == 65535)
			{
				configId = -1;
			}
			int j1 = buf.getUnsignedByte();
			childrenIds = new int[j1 + 1];
			for (int j2 = 0; j2 <= j1; j2++)
			{
				childrenIds[j2] = buf.getUnsignedShortBE();
				if (childrenIds[j2] == 65535)
				{
					childrenIds[j2] = -1;
				}
			}

		}
		if (i == -1)
		{
			hasActions = false;
			if (modelIds != null && (modelTypes == null || modelTypes[0] == 10))
			{
				hasActions = true;
			}
			if (options != null)
			{
				hasActions = true;
			}
		}
		if (aBoolean791)
		{
			solid = false;
			walkable = false;
		}
		if (anInt794 == -1)
		{
			anInt794 = solid ? 1 : 0;
		}
	}

	public Model getGameObjectModel(int type, int face, int vertexHeight, int vertexHeightRight, int vertexHeightTopRight, int vertexHeightTop, int animationId)
	{
		Model model = getGameObjectAnimatedModel(type, animationId, face);
		if (model == null)
		{
			return null;
		}
		if (adjustToTerrain || nonFlatShading)
		{
			model = new Model(adjustToTerrain, nonFlatShading, model);
		}
		if (adjustToTerrain)
		{
			int l1 = (vertexHeight + vertexHeightRight + vertexHeightTopRight + vertexHeightTop) / 4;
			for (int i2 = 0; i2 < model.vertexCount; i2++)
			{
				int j2 = model.verticesX[i2];
				int k2 = model.verticesZ[i2];
				int l2 = vertexHeight + ((vertexHeightRight - vertexHeight) * (j2 + 64)) / 128;
				int i3 = vertexHeightTop + ((vertexHeightTopRight - vertexHeightTop) * (j2 + 64)) / 128;
				int j3 = l2 + ((i3 - l2) * (k2 + 64)) / 128;
				model.verticesY[i2] += j3 - l1;
			}

			model.normalise();
		}
		return model;
	}

	public boolean method432(int i, int j)
	{
		if (i != 26261)
		{
			aBoolean786 = !aBoolean786;
		}
		if (modelTypes == null)
		{
			if (modelIds == null)
			{
				return true;
			}
			if (j != 10)
			{
				return true;
			}
			boolean flag = true;
			for (int l = 0; l < modelIds.length; l++)
			{
				flag &= Model.loaded(modelIds[l] & 0xffff);
			}

			return flag;
		}
		for (int k = 0; k < modelTypes.length; k++)
		{
			if (modelTypes[k] == j)
			{
				return Model.loaded(modelIds[k] & 0xffff);
			}
		}

		return true;
	}


}
