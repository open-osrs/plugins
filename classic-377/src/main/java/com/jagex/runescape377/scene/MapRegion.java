package com.jagex.runescape377.scene;

import com.jagex.runescape377.cache.def.FloorDefinition;
import com.jagex.runescape377.cache.def.GameObjectDefinition;
import com.jagex.runescape377.media.Rasterizer3D;
import com.jagex.runescape377.media.renderable.GameObject;
import com.jagex.runescape377.media.renderable.Model;
import com.jagex.runescape377.media.renderable.Renderable;
import com.jagex.runescape377.net.Buffer;
import com.jagex.runescape377.net.requester.OnDemandRequester;
import com.jagex.runescape377.scene.util.CollisionMap;
import com.jagex.runescape377.scene.util.TiledUtils;

public class MapRegion
{
	public static final int[] FACE_OFFSET_X = {1, 0, -1, 0};
	public static final int[] FACE_OFFSET_Y = {0, -1, 0, 1};
	public static final int[] WALL_CORNER_ORIENTATION = {16, 32, 64, 128};
	public static final int[] POWERS_OF_TWO = {1, 2, 4, 8};
	public static int hue_offset = (int) (Math.random() * 17.0) - 8;
	public static int setZ = 99;
	public static int onBuildTimePlane;
	public static int lightness_offset = (int) (Math.random() * 33.0) - 16;
	public static boolean lowMemory = true;
	public byte[][][] tile_flags;
	public byte[][][] overlayRotations;
	public int[] hue_buffer;
	public int[] saturation_buffer;
	public int[] lightness_buffer;
	public int[] huedivider;
	public int[] buffer_size;
	public int[][][] tile_height;
	public int size_x;
	public int size_z;
	public byte[][][] overlayClippingPaths;
	public byte[][][] overlayFloorIds;
	public byte[][][] underlayFloorIds;
	public byte[][][] object_shadow_data;
	public int[][] tile_lightness;
	public int anInt166 = 69;
	public int[][][] tile_culling_bitmap;


	public MapRegion(int width, int height, byte[][][] tile_flags, int[][][] tile_height)
	{
		setZ = 99;
		size_x = width;
		size_z = height;
		this.tile_height = tile_height;
		this.tile_flags = tile_flags;
		underlayFloorIds = new byte[4][size_x][size_z];
		overlayFloorIds = new byte[4][size_x][size_z];
		overlayClippingPaths = new byte[4][size_x][size_z];
		overlayRotations = new byte[4][size_x][size_z];
		tile_culling_bitmap = new int[4][size_x + 1][size_z + 1];
		object_shadow_data = new byte[4][size_x + 1][size_z + 1];
		tile_lightness = new int[size_x + 1][size_z + 1];
		hue_buffer = new int[size_z];
		saturation_buffer = new int[size_z];
		lightness_buffer = new int[size_z];
		huedivider = new int[size_z];
		buffer_size = new int[size_z];
	}

	/**
	 * 2D Pseudorandom noise generator
	 *
	 * @param x The horizontal coordinate to get the noise for
	 * @param y The vertical coordinate to get the noise for
	 * @return The value of the noise at [<b>x</b>;<b>y</b>]
	 */
	public static int random_noise(int x, int y)
	{
		int n = x + y * 57;
		n = n << 13 ^ n;
		int noise = n * (n * n * 15731 + 789221) + 1376312589 & 0x7fffffff;
		return noise >> 19 & 0xff;
	}

	/**
	 * Perlin noise generator
	 *
	 * @param x     The horizontal coordinate to get the noise for
	 * @param y     The vertical coordinate to get the noise for
	 * @param scale The scale of the coordinate set
	 * @return The value of the noise at [<b>x</b>;<b>y</b>]
	 */
	public static int perlin_noise(int x, int y, int scale)
	{
		int scaled_x = x / scale;
		int mu_x = x & scale - 1;
		int scaled_y = y / scale;
		int mu_y = y & scale - 1;
		int s = random_noise_weighed_sum(scaled_x, scaled_y);
		int t = random_noise_weighed_sum(scaled_x + 1, scaled_y);
		int u = random_noise_weighed_sum(scaled_x, scaled_y + 1);
		int v = random_noise_weighed_sum(scaled_x + 1, scaled_y + 1);
		int i_v1 = interpolate(s, t, mu_x, scale);
		int i_v2 = interpolate(u, v, mu_x, scale);
		return interpolate(i_v1, i_v2, mu_y, scale);
	}

	public static void forceRenderObject(int x, int y, int z, int objectId, int type, int plane, int face, Scene scene, CollisionMap collisionMap, int[][][] groundArray)
	{
		int vertexHeightSW = groundArray[plane][x][y];
		int vertexHeightSE = groundArray[plane][x + 1][y];
		int vertexHeightNE = groundArray[plane][x + 1][y + 1];
		int vertexHeightNW = groundArray[plane][x][y + 1];
		int drawHeight = vertexHeightSW + vertexHeightSE + vertexHeightNE + vertexHeightNW >> 2;
		GameObjectDefinition definition = GameObjectDefinition.getDefinition(objectId);
		int hash = x + (y << 7) + (objectId << 14) + 1073741824;
		if (!definition.hasActions)
		{
			hash += -2147483648;
		}
		byte config = (byte) ((face << 6) + type);
		if (type == 22)
		{
			Renderable renderable;
			if (definition.animationId == -1 && definition.childrenIds == null)
			{
				renderable = definition.getGameObjectModel(22, face, vertexHeightSW, vertexHeightSE, vertexHeightNE, vertexHeightNW, -1);
			}
			else
			{
				renderable = new GameObject(objectId, face, 22, vertexHeightSE, vertexHeightNE, vertexHeightSW, vertexHeightNW, definition.animationId, true);
			}
			scene.addGroundDecoration(x, y, z, drawHeight, hash, renderable, config);
			if (definition.solid && definition.hasActions)
			{
				collisionMap.markBlocked(x, y);
			}
		}
		else if (type == 10 || type == 11)
		{
			Renderable renderable;
			if (definition.animationId == -1 && definition.childrenIds == null)
			{
				renderable = definition.getGameObjectModel(10, face, vertexHeightSW, vertexHeightSE, vertexHeightNE, vertexHeightNW, -1);
			}
			else
			{
				renderable = new GameObject(objectId, face, 10, vertexHeightSE, vertexHeightNE, vertexHeightSW, vertexHeightNW, definition.animationId, true);
			}
			if (renderable != null)
			{
				int rotation = 0;
				if (type == 11)
				{
					rotation += 256;
				}
				int sizeX;
				int sizeY;
				if (face == 1 || face == 3)
				{
					sizeX = definition.sizeY;
					sizeY = definition.sizeX;
				}
				else
				{
					sizeX = definition.sizeX;
					sizeY = definition.sizeY;
				}
				scene.addEntityB(x, y, z, drawHeight, rotation, sizeY, sizeX, hash, renderable, config);
			}
			if (definition.solid)
			{
				collisionMap.markSolidOccupant(y, face, definition.sizeY, definition.sizeX, definition.walkable, x);
			}
		}
		else if (type >= 12)
		{
			Renderable renderable;
			if (definition.animationId == -1 && definition.childrenIds == null)
			{
				renderable = definition.getGameObjectModel(type, face, vertexHeightSW, vertexHeightSE, vertexHeightNE, vertexHeightNW, -1);
			}
			else
			{
				renderable = new GameObject(objectId, face, type, vertexHeightSE, vertexHeightNE, vertexHeightSW, vertexHeightNW, definition.animationId, true);
			}
			scene.addEntityB(x, y, z, drawHeight, 0, 1, 1, hash, renderable, config);
			if (definition.solid)
			{
				collisionMap.markSolidOccupant(y, face, definition.sizeY, definition.sizeX, definition.walkable, x);
			}
		}
		else if (type == 0)
		{
			Renderable renderable;
			if (definition.animationId == -1 && definition.childrenIds == null)
			{
				renderable = definition.getGameObjectModel(0, face, vertexHeightSW, vertexHeightSE, vertexHeightNE, vertexHeightNW, -1);
			}
			else
			{
				renderable = new GameObject(objectId, face, 0, vertexHeightSE, vertexHeightNE, vertexHeightSW, vertexHeightNW, definition.animationId, true);
			}
			scene.addWall(x, y, z, drawHeight, POWERS_OF_TWO[face], 0, hash, renderable, null, config);
			if (definition.solid)
			{
				collisionMap.markWall(x, y, type, face, definition.walkable);
			}
		}
		else if (type == 1)
		{
			Renderable renderable;
			if (definition.animationId == -1 && definition.childrenIds == null)
			{
				renderable = definition.getGameObjectModel(1, face, vertexHeightSW, vertexHeightSE, vertexHeightNE, vertexHeightNW, -1);
			}
			else
			{
				renderable = new GameObject(objectId, face, 1, vertexHeightSE, vertexHeightNE, vertexHeightSW, vertexHeightNW, definition.animationId, true);
			}
			scene.addWall(x, y, z, drawHeight, WALL_CORNER_ORIENTATION[face], 0, hash, renderable, null, config);
			if (definition.solid)
			{
				collisionMap.markWall(x, y, type, face, definition.walkable);
			}
		}
		else if (type == 2)
		{
			int _face = face + 1 & 0x3;
			Renderable renderable;
			Renderable renderable1;
			if (definition.animationId == -1 && definition.childrenIds == null)
			{
				renderable = definition.getGameObjectModel(2, 4 + face, vertexHeightSW, vertexHeightSE, vertexHeightNE, vertexHeightNW, -1);
				renderable1 = definition.getGameObjectModel(2, _face, vertexHeightSW, vertexHeightSE, vertexHeightNE, vertexHeightNW, -1);
			}
			else
			{
				renderable = new GameObject(objectId, 4 + face, 2, vertexHeightSE, vertexHeightNE, vertexHeightSW, vertexHeightNW, definition.animationId, true);
				renderable1 = new GameObject(objectId, _face, 2, vertexHeightSE, vertexHeightNE, vertexHeightSW, vertexHeightNW, definition.animationId, true);
			}
			scene.addWall(x, y, z, drawHeight, POWERS_OF_TWO[face], POWERS_OF_TWO[_face], hash, renderable, renderable1, config);
			if (definition.solid)
			{
				collisionMap.markWall(x, y, type, face, definition.walkable);
			}
		}
		else if (type == 3)
		{
			Renderable renderable;
			if (definition.animationId == -1 && definition.childrenIds == null)
			{
				renderable = definition.getGameObjectModel(3, face, vertexHeightSW, vertexHeightSE, vertexHeightNE, vertexHeightNW, -1);
			}
			else
			{
				renderable = new GameObject(objectId, face, 3, vertexHeightSE, vertexHeightNE, vertexHeightSW, vertexHeightNW, definition.animationId, true);
			}
			scene.addWall(x, y, z, drawHeight, WALL_CORNER_ORIENTATION[face], 0, hash, renderable, null, config);
			if (definition.solid)
			{
				collisionMap.markWall(x, y, type, face, definition.walkable);
			}
		}
		else if (type == 9)
		{
			Renderable renderable;
			if (definition.animationId == -1 && definition.childrenIds == null)
			{
				renderable = definition.getGameObjectModel(type, face, vertexHeightSW, vertexHeightSE, vertexHeightNE, vertexHeightNW, -1);
			}
			else
			{
				renderable = new GameObject(objectId, face, type, vertexHeightSE, vertexHeightNE, vertexHeightSW, vertexHeightNW, definition.animationId, true);
			}
			scene.addEntityB(x, y, z, drawHeight, 0, 1, 1, hash, renderable, config);
			if (definition.solid)
			{
				collisionMap.markSolidOccupant(y, face, definition.sizeY, definition.sizeX, definition.walkable, x);
			}
		}
		else
		{
			if (definition.adjustToTerrain)
			{
				if (face == 1)
				{
					int temp = vertexHeightNW;
					vertexHeightNW = vertexHeightNE;
					vertexHeightNE = vertexHeightSE;
					vertexHeightSE = vertexHeightSW;
					vertexHeightSW = temp;
				}
				else if (face == 2)
				{
					int temp = vertexHeightNW;
					vertexHeightNW = vertexHeightSE;
					vertexHeightSE = temp;
					temp = vertexHeightNE;
					vertexHeightNE = vertexHeightSW;
					vertexHeightSW = temp;
				}
				else if (face == 3)
				{
					int temp = vertexHeightNW;
					vertexHeightNW = vertexHeightSW;
					vertexHeightSW = vertexHeightSE;
					vertexHeightSE = vertexHeightNE;
					vertexHeightNE = temp;
				}
			}
			if (type == 4)
			{
				Renderable renderable;
				if (definition.animationId == -1 && definition.childrenIds == null)
				{
					renderable = definition.getGameObjectModel(4, 0, vertexHeightSW, vertexHeightSE, vertexHeightNE, vertexHeightNW, -1);
				}
				else
				{
					renderable = new GameObject(objectId, 0, 4, vertexHeightSE, vertexHeightNE, vertexHeightSW, vertexHeightNW, definition.animationId, true);
				}
				scene.addWallDecoration(x, y, z, drawHeight, 0, 0, face * 512, hash, renderable, config, POWERS_OF_TWO[face]);
			}
			else if (type == 5)
			{
				int offsetAmplifier = 16;
				int objectHash = scene.getWallObjectHash(x, y, z);
				if (objectHash > 0)
				{
					offsetAmplifier = GameObjectDefinition.getDefinition(objectHash >> 14 & 0x7fff).offsetAmplifier;
				}
				Renderable renderable;
				if (definition.animationId == -1 && definition.childrenIds == null)
				{
					renderable = definition.getGameObjectModel(4, 0, vertexHeightSW, vertexHeightSE, vertexHeightNE, vertexHeightNW, -1);
				}
				else
				{
					renderable = new GameObject(objectId, 0, 4, vertexHeightSE, vertexHeightNE, vertexHeightSW, vertexHeightNW, definition.animationId, true);
				}
				scene.addWallDecoration(x, y, z, drawHeight, FACE_OFFSET_X[face] * offsetAmplifier, FACE_OFFSET_Y[face] * offsetAmplifier, face * 512, hash, renderable, config, POWERS_OF_TWO[face]);
			}
			else if (type == 6)
			{
				Renderable renderable;
				if (definition.animationId == -1 && definition.childrenIds == null)
				{
					renderable = definition.getGameObjectModel(4, 0, vertexHeightSW, vertexHeightSE, vertexHeightNE, vertexHeightNW, -1);
				}
				else
				{
					renderable = new GameObject(objectId, 0, 4, vertexHeightSE, vertexHeightNE, vertexHeightSW, vertexHeightNW, definition.animationId, true);
				}
				scene.addWallDecoration(x, y, z, drawHeight, 0, 0, face, hash, renderable, config, 256);
			}
			else if (type == 7)
			{
				Renderable renderable;
				if (definition.animationId == -1 && definition.childrenIds == null)
				{
					renderable = definition.getGameObjectModel(4, 0, vertexHeightSW, vertexHeightSE, vertexHeightNE, vertexHeightNW, -1);
				}
				else
				{
					renderable = new GameObject(objectId, 0, 4, vertexHeightSE, vertexHeightNE, vertexHeightSW, vertexHeightNW, definition.animationId, true);
				}
				scene.addWallDecoration(x, y, z, drawHeight, 0, 0, face, hash, renderable, config, 512);
			}
			else if (type == 8)
			{
				Renderable renderable;
				if (definition.animationId == -1 && definition.childrenIds == null)
				{
					renderable = definition.getGameObjectModel(4, 0, vertexHeightSW, vertexHeightSE, vertexHeightNE, vertexHeightNW, -1);
				}
				else
				{
					renderable = new GameObject(objectId, 0, 4, vertexHeightSE, vertexHeightNE, vertexHeightSW, vertexHeightNW, definition.animationId, true);
				}
				scene.addWallDecoration(x, y, z, drawHeight, 0, 0, face, hash, renderable, config, 768);
			}
		}
	}

	public static void passiveRequestGameObjectModels(OnDemandRequester onDemandRequester, Buffer buffer)
	{

		int gameObjectId = -1;
		while (true)
		{
			int gameObjectIdOffset = buffer.getSmart();
			if (gameObjectIdOffset == 0)
			{
				break;
			}
			gameObjectId += gameObjectIdOffset;
			GameObjectDefinition gameObjectDefinition = GameObjectDefinition.getDefinition(gameObjectId);
			gameObjectDefinition.passiveRequestModels(onDemandRequester);
			while (true)
			{
				int terminate = buffer.getSmart();
				if (terminate == 0)
				{
					break;
				}
				buffer.getUnsignedByte();
			}

		}
	}

	public static boolean method170(int i, int i_163_)
	{
		GameObjectDefinition gameObjectDefinition = GameObjectDefinition.getDefinition(i_163_);
		if (i == 11)
		{
			i = 10;
		}
		if (i >= 5 && i <= 8)
		{
			i = 4;
		}
		return gameObjectDefinition.method432(26261, i);
	}

	public static int trimHSLLightness(int i, int i_165_)
	{
		if (i == -1)
		{
			return 12345678;
		}
		i_165_ = i_165_ * (i & 0x7f) / 128;
		if (i_165_ < 2)
		{
			i_165_ = 2;
		}
		else if (i_165_ > 126)
		{
			i_165_ = 126;
		}
		return (i & 0xff80) + i_165_;
	}

	public static int interpolate(int a, int b, int delta, int deltaScale)
	{
		int f = (65536 - Rasterizer3D.COSINE[delta * 1024 / deltaScale] >> 1);
		return (a * (65536 - f) >> 16) + (b * f >> 16);
	}

	public static int random_noise_weighed_sum(int x, int y)
	{
		int v_dist2 = random_noise(x - 1, y - 1) + random_noise(x + 1, y - 1) + random_noise(x - 1, y + 1) + random_noise(x + 1, y + 1);
		int v_dist1 = random_noise(x - 1, y) + random_noise(x + 1, y) + random_noise(x, y - 1) + random_noise(x, y + 1);
		int v_local = random_noise(x, y);
		return v_dist2 / 16 + v_dist1 / 8 + v_local / 4;
	}

	public static boolean regionCached(int regionX, int regionY, byte[] objectData)
	{
		boolean cached = true;
		Buffer objectDataStream = new Buffer(objectData);
		int objectId = -1;
		while (true)
		{
			int objectIdIncrement = objectDataStream.getSmart();
			if (objectIdIncrement == 0)
			{
				break;
			}
			objectId += objectIdIncrement;
			int pos = 0;
			boolean readSecondValue = false;
			while (true)
			{
				if (readSecondValue)
				{
					int secondValue = objectDataStream.getSmart();
					if (secondValue == 0)
					{
						break;
					}
					objectDataStream.getUnsignedByte();
				}
				else
				{
					int positionoffset = objectDataStream.getSmart();
					if (positionoffset == 0)
					{
						break;
					}
					pos += positionoffset - 1;
					int regionOffsetY = pos & 0x3f;
					int regionOffsetX = pos >> 6 & 0x3f;
					int objectType = objectDataStream.getUnsignedByte() >> 2;
					int objectX = regionOffsetX + regionX;
					int objectY = regionOffsetY + regionY;
					if (objectX > 0 && objectY > 0 && objectX < 103 && objectY < 103)
					{
						GameObjectDefinition definition = GameObjectDefinition.getDefinition(objectId);
						if (objectType != 22 || !lowMemory || definition.hasActions || definition.unknown)
						{
							cached &= definition.isModelCached();
							readSecondValue = true;
						}
					}
				}
			}
		}
		return cached;
	}

	public static int calculateVertexHeight(int i, int i_281_)
	{
		int mapHeight = (perlin_noise(i + 45365, i_281_ + 91923, 4) - 128 + (perlin_noise(i + 10294, i_281_ + 37821, 2) - 128 >> 1) + (perlin_noise(i, i_281_, 1) - 128 >> 2));
		mapHeight = (int) (mapHeight * 0.3) + 35;
		if (mapHeight < 10)
		{
			mapHeight = 10;
		}
		else if (mapHeight > 60)
		{
			mapHeight = 60;
		}
		return mapHeight;
	}

	public int getVisibilityPlaneFor(int x, int y, int plane)
	{
		if ((this.tile_flags[plane][x][y] & 8) != 0)
		{
			return 0;
		}
		if (plane > 0 && (this.tile_flags[1][x][y] & 2) != 0)
		{
			return plane - 1;
		}
		else
		{
			return plane;
		}
	}

	public void method166(int i_39_, int i_40_, int i_41_)
	{
		for (int i_42_ = 0; i_42_ < 8; i_42_++)
		{
			for (int i_43_ = 0; i_43_ < 8; i_43_++)
			{
				tile_height[i_39_][i_41_ + i_42_][i_40_ + i_43_] = 0;
			}
		}
		if (i_41_ > 0)
		{
			for (int i_44_ = 1; i_44_ < 8; i_44_++)
			{
				tile_height[i_39_][i_41_][i_40_ + i_44_] = tile_height[i_39_][i_41_ - 1][i_40_ + i_44_];
			}
		}
		if (i_40_ > 0)
		{
			for (int i_45_ = 1; i_45_ < 8; i_45_++)
			{
				tile_height[i_39_][i_41_ + i_45_][i_40_] = tile_height[i_39_][i_41_ + i_45_][i_40_ - 1];
			}
		}
		if (i_41_ > 0 && tile_height[i_39_][i_41_ - 1][i_40_] != 0)
		{
			tile_height[i_39_][i_41_][i_40_] = tile_height[i_39_][i_41_ - 1][i_40_];
		}
		else if (i_40_ > 0 && tile_height[i_39_][i_41_][i_40_ - 1] != 0)
		{
			tile_height[i_39_][i_41_][i_40_] = tile_height[i_39_][i_41_][i_40_ - 1];
		}
		else if (i_41_ > 0 && i_40_ > 0 && tile_height[i_39_][i_41_ - 1][i_40_ - 1] != 0)
		{
			tile_height[i_39_][i_41_][i_40_] = tile_height[i_39_][i_41_ - 1][i_40_ - 1];
		}
	}

	public void addTiles(CollisionMap[] collisionMaps, Scene scene, int render_mode)
	{
		for (int tile_y = 0; tile_y < 4; tile_y++)
		{
			for (int tile_x = 0; tile_x < 104; tile_x++)
			{
				for (int tile_z = 0; tile_z < 104; tile_z++)
				{
					if ((tile_flags[tile_y][tile_x][tile_z] & 0x1) == 1)
					{
						int originalPlane = tile_y;
						if ((tile_flags[1][tile_x][tile_z] & 0x2) == 2)
						{
							originalPlane--;
						}
						if (originalPlane >= 0)
						{
							collisionMaps[originalPlane].markBlocked(tile_x, tile_z);
						}
					}
				}
			}
		}
		hue_offset += (int) (Math.random() * 5.0) - 2;
		if (hue_offset < -8)
		{
			hue_offset = -8;
		}
		if (hue_offset > 8)
		{
			hue_offset = 8;
		}
		lightness_offset += (int) (Math.random() * 5.0) - 2;
		if (lightness_offset < -16)
		{
			lightness_offset = -16;
		}
		if (lightness_offset > 16)
		{
			lightness_offset = 16;
		}
		for (int plane = 0; plane < 4; plane++)
		{
			byte[][] shadowIntensity = object_shadow_data[plane];
			int light_off = 96;
			char specularDistributionFactor = '\u0300';
			int directionalLightX = -50;
			int directionalLightY = -10;
			int directionalLightZ = -50;
			int directionalLightLength = (int) Math.sqrt((directionalLightX * directionalLightX + directionalLightY * directionalLightY + directionalLightZ * directionalLightZ));
			int specularDistribution = specularDistributionFactor * directionalLightLength >> 8;
			for (int y = 1; y < size_z - 1; y++)
			{
				for (int x = 1; x < size_x - 1; x++)
				{
					int xHeightDifference = (tile_height[plane][x + 1][y] - tile_height[plane][x - 1][y]);
					int yHeightDifference = (tile_height[plane][x][y + 1] - tile_height[plane][x][y - 1]);
					int normalizedLength = (int) Math.sqrt((xHeightDifference * xHeightDifference + 65536 + yHeightDifference * yHeightDifference));
					int normalizedNormalX = (xHeightDifference << 8) / normalizedLength;
					int normalizedNormalY = 65536 / normalizedLength;
					int normalizedNormalZ = (yHeightDifference << 8) / normalizedLength;
					int directionalLightIntensity = light_off + (directionalLightX * normalizedNormalX + directionalLightY * normalizedNormalY + directionalLightZ * normalizedNormalZ) / specularDistribution;
					int weightedShadowIntensity = ((shadowIntensity[x - 1][y] >> 2) + (shadowIntensity[x + 1][y] >> 3) + (shadowIntensity[x][y - 1] >> 2) + (shadowIntensity[x][y + 1] >> 3) + (shadowIntensity[x][y] >> 1));
					tile_lightness[x][y] = directionalLightIntensity - weightedShadowIntensity;
				}
			}
			for (int y = 0; y < size_z; y++)
			{
				hue_buffer[y] = 0;
				saturation_buffer[y] = 0;
				lightness_buffer[y] = 0;
				huedivider[y] = 0;
				buffer_size[y] = 0;
			}
			for (int x = -5; x < size_x + 5; x++)
			{
				for (int y = 0; y < size_z; y++)
				{
					int xPositiveOffset = x + 5;
					if (xPositiveOffset >= 0 && xPositiveOffset < size_x)
					{
						int floorId = underlayFloorIds[plane][xPositiveOffset][y] & 0xff;
						if (floorId > 0)
						{
							FloorDefinition floor = FloorDefinition.cache[floorId - 1];
							hue_buffer[y] += floor.hue;
							saturation_buffer[y] += floor.saturation;
							lightness_buffer[y] += floor.lightness;
							huedivider[y] += floor.hueDivisor;
							buffer_size[y]++;
						}
					}
					int xNegativeOffset = x - 5;
					if (xNegativeOffset >= 0 && xNegativeOffset < size_x)
					{
						int floorId = underlayFloorIds[plane][xNegativeOffset][y] & 0xff;
						if (floorId > 0)
						{
							FloorDefinition floor = FloorDefinition.cache[floorId - 1];
							hue_buffer[y] -= floor.hue;
							saturation_buffer[y] -= floor.saturation;
							lightness_buffer[y] -= floor.lightness;
							huedivider[y] -= floor.hueDivisor;
							buffer_size[y]--;
						}
					}
				}
				if (x >= 1 && x < size_x - 1)
				{
					int i_75_ = 0;
					int i_76_ = 0;
					int i_77_ = 0;
					int i_78_ = 0;
					int i_79_ = 0;
					for (int y = -5; y < size_z + 5; y++)
					{
						int yPositiveOffset = y + 5;
						if (yPositiveOffset >= 0 && yPositiveOffset < size_z)
						{
							i_75_ += hue_buffer[yPositiveOffset];
							i_76_ += saturation_buffer[yPositiveOffset];
							i_77_ += lightness_buffer[yPositiveOffset];
							i_78_ += huedivider[yPositiveOffset];
							i_79_ += buffer_size[yPositiveOffset];
						}
						int yNegativeOffset = y - 5;
						if (yNegativeOffset >= 0 && yNegativeOffset < size_z)
						{
							i_75_ -= hue_buffer[yNegativeOffset];
							i_76_ -= saturation_buffer[yNegativeOffset];
							i_77_ -= lightness_buffer[yNegativeOffset];
							i_78_ -= huedivider[yNegativeOffset];
							i_79_ -= buffer_size[yNegativeOffset];
						}
						if (y >= 1 && y < size_z - 1 && (!lowMemory || (tile_flags[0][x][y] & 0x2) != 0 || ((tile_flags[plane][x][y] & 0x10) == 0 && (getVisibilityPlaneFor(x, y, plane) == onBuildTimePlane))))
						{
							if (plane < setZ)
							{
								setZ = plane;
							}
							int underlayFloorId = (underlayFloorIds[plane][x][y] & 0xff);
							int overlayFloorId = (overlayFloorIds[plane][x][y] & 0xff);
							if (underlayFloorId > 0 || overlayFloorId > 0)
							{
								int vertexSouthWest = tile_height[plane][x][y];
								int vertexSouthEast = (tile_height[plane][x + 1][y]);
								int vertexNorthEast = (tile_height[plane][x + 1][y + 1]);
								int vertexNorthWest = (tile_height[plane][x][y + 1]);
								int lightSouthWest = tile_lightness[x][y];
								int lightSouthEast = tile_lightness[x + 1][y];
								int lightNorthEast = tile_lightness[x + 1][y + 1];
								int lightNorthWest = tile_lightness[x][y + 1];
								int hslBitsetUnmodified = -1;
								int hslBitsetRandomized = -1;
								if (underlayFloorId > 0)
								{
									int hue = i_75_ * 256 / i_78_;
									int saturation = i_76_ / i_79_;
									int lightness = i_77_ / i_79_;
									hslBitsetUnmodified = getHSLBitset(hue, saturation, lightness);
									hue = hue + hue_offset & 0xff;
									lightness += lightness_offset;
									if (lightness < 0)
									{
										lightness = 0;
									}
									else if (lightness > 255)
									{
										lightness = 255;
									}
									hslBitsetRandomized = getHSLBitset(hue, saturation, lightness);
								}
								if (plane > 0)
								{
									boolean bool = true;
									if (underlayFloorId == 0 && (overlayClippingPaths[plane][x][y]) != 0)
									{
										bool = false;
									}
									if (overlayFloorId > 0 && !(FloorDefinition.cache[overlayFloorId - 1].occlude))
									{
										bool = false;
									}
									if (bool && vertexSouthWest == vertexSouthEast && vertexSouthWest == vertexNorthEast && vertexSouthWest == vertexNorthWest)
									{
										tile_culling_bitmap[plane][x][y] |= 0x924;
									}
								}
								int rgbBitsetRandomized = 0;
								if (hslBitsetUnmodified != -1)
								{
									rgbBitsetRandomized = (Rasterizer3D.hsl2rgb[trimHSLLightness(hslBitsetRandomized, 96)]);
								}
								if (overlayFloorId == 0)
								{
									scene.addTile(plane, x, y, 0, 0, -1, vertexSouthWest, vertexSouthEast, vertexNorthEast, vertexNorthWest, trimHSLLightness(hslBitsetUnmodified, lightSouthWest), trimHSLLightness(hslBitsetUnmodified, lightSouthEast), trimHSLLightness(hslBitsetUnmodified, lightNorthEast), trimHSLLightness(hslBitsetUnmodified, lightNorthWest), 0, 0, 0, 0, rgbBitsetRandomized, 0);
								}
								else
								{
									int clippingPath = ((overlayClippingPaths[plane][x][y]) + 1);
									byte clippingPathRotation = (overlayRotations[plane][x][y]);
									FloorDefinition floor = FloorDefinition.cache[overlayFloorId - 1];
									int textureid = floor.textureId;
									int hslBitset;
									int rgbBitset;
									if (textureid >= 0)
									{
										rgbBitset = Rasterizer3D.getAverageRgbColorForTexture(textureid);
										hslBitset = -1;

									}
									else if (floor.rgbColor == 0xff00ff)
									{
										hslBitset = -2;
										textureid = -1;
										rgbBitset = 0;
									}
									else
									{
										hslBitset = getHSLBitset(floor.hue2, floor.saturation, floor.lightness);
										rgbBitset = (Rasterizer3D.hsl2rgb[mixLightnessSigned(floor.hslColor2, 96)]);

									}
									scene.addTile(plane, x, y, clippingPath, clippingPathRotation, textureid, vertexSouthWest, vertexSouthEast, vertexNorthEast, vertexNorthWest, trimHSLLightness(hslBitsetUnmodified, lightSouthWest), trimHSLLightness(hslBitsetUnmodified, lightSouthEast), trimHSLLightness(hslBitsetUnmodified, lightNorthEast), trimHSLLightness(hslBitsetUnmodified, lightNorthWest), mixLightnessSigned(hslBitset, lightSouthWest), mixLightnessSigned(hslBitset, lightSouthEast), mixLightnessSigned(hslBitset, lightNorthEast), mixLightnessSigned(hslBitset, lightNorthWest), rgbBitsetRandomized, rgbBitset);
								}
							}
						}
					}
				}
			}
			for (int i_104_ = 1; i_104_ < size_z - 1; i_104_++)
			{
				for (int i_105_ = 1; i_105_ < size_x - 1; i_105_++)
				{
					scene.setTileLogicHeight(plane, i_105_, i_104_, getVisibilityPlaneFor(i_105_, i_104_, plane));
				}
			}
		}
		scene.shadeModels(-10, -50, -50);
		for (int y = 0; y < size_x; y++)
		{
			for (int x = 0; x < size_z; x++)
			{
				if ((tile_flags[1][y][x] & 0x2) == 2)
				{
					scene.setBridgeMode(y, x);
				}
			}
		}
		int renderRule1 = 1;
		int renderRule2 = 2;
		int renderRule3 = 4;
		for (int currentPlane = 0; currentPlane < 4; currentPlane++)
		{
			if (currentPlane > 0)
			{
				renderRule1 <<= 3;
				renderRule2 <<= 3;
				renderRule3 <<= 3;
			}
			for (int plane = 0; plane <= currentPlane; plane++)
			{
				for (int y = 0; y <= size_z; y++)
				{
					for (int x = 0; x <= size_x; x++)
					{
						if ((tile_culling_bitmap[plane][x][y] & renderRule1) != 0)
						{
							int lowestOcclussionY = y;
							int higestOcclussionY = y;
							int lowestOcclussionPlane = plane;
							int higestOcclussionPlane = plane;
							for (/**/; lowestOcclussionY > 0; lowestOcclussionY--)
							{
								if (((tile_culling_bitmap[plane][x][lowestOcclussionY - 1]) & renderRule1) == 0)
								{
									break;
								}
							}
							for (/**/; higestOcclussionY < size_z; higestOcclussionY++)
							{
								if (((tile_culling_bitmap[plane][x][higestOcclussionY + 1]) & renderRule1) == 0)
								{
									break;
								}
							}
							while_0_:
							for (/**/; lowestOcclussionPlane > 0; lowestOcclussionPlane--)
							{
								for (int occludedY = lowestOcclussionY; occludedY <= higestOcclussionY; occludedY++)
								{
									if (((tile_culling_bitmap[lowestOcclussionPlane - 1][x][occludedY]) & renderRule1) == 0)
									{
										break while_0_;
									}
								}
							}
							while_1_:
							for (/**/; higestOcclussionPlane < currentPlane; higestOcclussionPlane++)
							{
								for (int occludedY = lowestOcclussionY; occludedY <= higestOcclussionY; occludedY++)
								{
									if (((tile_culling_bitmap[higestOcclussionPlane + 1][x][occludedY]) & renderRule1) == 0)
									{
										break while_1_;
									}
								}
							}
							int occlussionSurface = (higestOcclussionPlane + 1 - lowestOcclussionPlane) * (higestOcclussionY - lowestOcclussionY + 1);
							if (occlussionSurface >= 8)
							{
								int highestOcclussionVertexHeightOffset = 240;
								int highestOcclussionVertexHeight = ((tile_height[higestOcclussionPlane][x][lowestOcclussionY]) - highestOcclussionVertexHeightOffset);
								int lowestOcclussionVertexHeight = (tile_height[lowestOcclussionPlane][x][lowestOcclussionY]);
								Scene.createCullingCluster(currentPlane, x * 128, x * 128, higestOcclussionY * 128 + 128, lowestOcclussionY * 128, highestOcclussionVertexHeight, lowestOcclussionVertexHeight, 1);
								for (int occludedPlane = lowestOcclussionPlane; occludedPlane <= higestOcclussionPlane; occludedPlane++)
								{
									for (int occludedY = lowestOcclussionY; occludedY <= higestOcclussionY; occludedY++)
									{
										tile_culling_bitmap[occludedPlane][x][occludedY] &= renderRule1 ^ 0xffffffff;
									}
								}
							}
						}
						if ((tile_culling_bitmap[plane][x][y] & renderRule2) != 0)
						{
							int i_127_ = x;
							int i_128_ = x;
							int i_129_ = plane;
							int i_130_ = plane;
							for (/**/; i_127_ > 0; i_127_--)
							{
								if (((tile_culling_bitmap[plane][i_127_ - 1][y]) & renderRule2) == 0)
								{
									break;
								}
							}
							for (/**/; i_128_ < size_x; i_128_++)
							{
								if (((tile_culling_bitmap[plane][i_128_ + 1][y]) & renderRule2) == 0)
								{
									break;
								}
							}
							while_2_:
							for (/**/; i_129_ > 0; i_129_--)
							{
								for (int i_131_ = i_127_; i_131_ <= i_128_; i_131_++)
								{
									if (((tile_culling_bitmap[i_129_ - 1][i_131_][y]) & renderRule2) == 0)
									{
										break while_2_;
									}
								}
							}
							while_3_:
							for (/**/; i_130_ < currentPlane; i_130_++)
							{
								for (int i_132_ = i_127_; i_132_ <= i_128_; i_132_++)
								{
									if (((tile_culling_bitmap[i_130_ + 1][i_132_][y]) & renderRule2) == 0)
									{
										break while_3_;
									}
								}
							}
							int i_133_ = (i_130_ + 1 - i_129_) * (i_128_ - i_127_ + 1);
							if (i_133_ >= 8)
							{
								int i_134_ = 240;
								int i_135_ = ((tile_height[i_130_][i_127_][y]) - i_134_);
								int i_136_ = (tile_height[i_129_][i_127_][y]);
								Scene.createCullingCluster(currentPlane, i_128_ * 128 + 128, i_127_ * 128, y * 128, y * 128, i_135_, i_136_, 2);
								for (int i_137_ = i_129_; i_137_ <= i_130_; i_137_++)
								{
									for (int i_138_ = i_127_; i_138_ <= i_128_; i_138_++)
									{
										tile_culling_bitmap[i_137_][i_138_][y] &= renderRule2 ^ 0xffffffff;
									}
								}
							}
						}
						if ((tile_culling_bitmap[plane][x][y] & renderRule3) != 0)
						{
							int i_139_ = x;
							int i_140_ = x;
							int i_141_ = y;
							int i_142_ = y;
							for (/**/; i_141_ > 0; i_141_--)
							{
								if (((tile_culling_bitmap[plane][x][i_141_ - 1]) & renderRule3) == 0)
								{
									break;
								}
							}
							for (/**/; i_142_ < size_z; i_142_++)
							{
								if (((tile_culling_bitmap[plane][x][i_142_ + 1]) & renderRule3) == 0)
								{
									break;
								}
							}
							while_4_:
							for (/**/; i_139_ > 0; i_139_--)
							{
								for (int i_143_ = i_141_; i_143_ <= i_142_; i_143_++)
								{
									if (((tile_culling_bitmap[plane][i_139_ - 1][i_143_]) & renderRule3) == 0)
									{
										break while_4_;
									}
								}
							}
							while_5_:
							for (/**/; i_140_ < size_x; i_140_++)
							{
								for (int i_144_ = i_141_; i_144_ <= i_142_; i_144_++)
								{
									if (((tile_culling_bitmap[plane][i_140_ + 1][i_144_]) & renderRule3) == 0)
									{
										break while_5_;
									}
								}
							}
							if ((i_140_ - i_139_ + 1) * (i_142_ - i_141_ + 1) >= 4)
							{
								int i_145_ = (tile_height[plane][i_139_][i_141_]);
								Scene.createCullingCluster(currentPlane, i_140_ * 128 + 128, i_139_ * 128, i_142_ * 128 + 128, i_141_ * 128, i_145_, i_145_, 4);
								for (int i_146_ = i_139_; i_146_ <= i_140_; i_146_++)
								{
									for (int i_147_ = i_141_; i_147_ <= i_142_; i_147_++)
									{
										tile_culling_bitmap[plane][i_146_][i_147_] &= renderRule3 ^ 0xffffffff;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void method168(int i, int i_148_, boolean bool, byte[] is, int i_149_, int i_150_, int i_151_, CollisionMap[] class46s, int i_152_, int i_153_)
	{
		if (bool)
		{
			anInt166 = 476;
		}
		for (int i_154_ = 0; i_154_ < 8; i_154_++)
		{
			for (int i_155_ = 0; i_155_ < 8; i_155_++)
			{
				if (i_151_ + i_154_ > 0 && i_151_ + i_154_ < 103 && i_152_ + i_155_ > 0 && i_152_ + i_155_ < 103)
				{
					class46s[i_149_].clippingData[i_151_ + i_154_][(i_152_ + i_155_)] &= ~0x1000000;
				}
			}
		}
		Buffer class50_sub1_sub2 = new Buffer(is);
		for (int i_156_ = 0; i_156_ < 4; i_156_++)
		{
			for (int i_157_ = 0; i_157_ < 64; i_157_++)
			{
				for (int i_158_ = 0; i_158_ < 64; i_158_++)
				{
					if (i_156_ == i_150_ && i_157_ >= i_153_ && i_157_ < i_153_ + 8 && i_158_ >= i_148_ && i_158_ < i_148_ + 8)
					{
						loadTerrainTile(i_151_ + TiledUtils.getRotatedMapChunkX(i_157_ & 0x7, i_158_ & 0x7, i), 0, i_152_ + TiledUtils.getRotatedMapChunkY(i_157_ & 0x7, i_158_ & 0x7, i), 0, i_149_, class50_sub1_sub2, i);
					}
					else
					{
						loadTerrainTile(-1, 0, -1, 0, 0, class50_sub1_sub2, 0);
					}
				}
			}
		}
	}

	public void method172(int i, CollisionMap[] collisionMaps, Scene scene, boolean bool, byte[] blockData, int i_166_, int i_167_, int i_168_, int i_169_, int i_170_, int i_171_)
	{
		Buffer buffer = new Buffer(blockData);
		if (!bool)
		{
			int objectId = -1;
			for (; ; )
			{
				int delta_id = buffer.getSmart();
				if (delta_id == 0)
				{
					break;
				}
				objectId += delta_id;
				int pos = 0;
				for (; ; )
				{
					int delta_pos = buffer.getSmart();
					if (delta_pos == 0)
					{
						break;
					}
					pos += delta_pos - 1;
					int tile_z = pos & 0x3f;
					int tile_x = pos >> 6 & 0x3f;
					int tile_y = pos >> 12;
					int object_info = buffer.getUnsignedByte();
					int object_type = object_info >> 2;
					int object_orientation = object_info & 0x3;
					if (tile_y == i_171_ && tile_x >= i_168_ && tile_x < i_168_ + 8 && tile_z >= i_170_ && tile_z < i_170_ + 8)
					{
						GameObjectDefinition definition = GameObjectDefinition.getDefinition(objectId);
						int i_182_ = (i_169_ + TiledUtils.getRotatedLandscapeChunkX(i_167_, definition.sizeY, tile_x & 0x7, tile_z & 0x7, definition.sizeX));
						int i_183_ = (i_166_ + TiledUtils.getRotatedLandscapeChunkY(tile_z & 0x7, definition.sizeY, i_167_, definition.sizeX, tile_x & 0x7));
						if (i_182_ > 0 && i_183_ > 0 && i_182_ < 103 && i_183_ < 103)
						{
							int logic_y = i;
							if ((tile_flags[1][i_182_][i_183_] & 0x2) == 2)
							{
								logic_y--;
							}
							CollisionMap collisionMap = null;
							if (logic_y >= 0)
							{
								collisionMap = collisionMaps[logic_y];
							}
							renderObject(scene, collisionMap, i_183_, i, i_182_, object_orientation + i_167_ & 0x3, object_type, objectId);
						}
					}
				}
			}
		}
	}

	public void renderObject(Scene scene, CollisionMap collisionMap, int y, int plane, int x, int face, int type, int objectId)
	{
		if (!lowMemory || (tile_flags[0][x][y] & 0x2) != 0 || ((tile_flags[plane][x][y] & 0x10) == 0 && getVisibilityPlaneFor(x, y, plane) == onBuildTimePlane))
		{
			if (plane < setZ)
			{
				setZ = plane;
			}
			int vertexHeight = tile_height[plane][x][y];
			int vertexHeightRight = tile_height[plane][x + 1][y];
			int vertexHeightTopRight = tile_height[plane][x + 1][y + 1];
			int vertexHeightTop = tile_height[plane][x][y + 1];
			int vertexMix = vertexHeight + vertexHeightRight + vertexHeightTopRight + vertexHeightTop >> 2;
			GameObjectDefinition gameObjectDefinition = GameObjectDefinition.getDefinition(objectId);
			int hash = x + (y << 7) + (objectId << 14) + 1073741824;
			if (!gameObjectDefinition.hasActions)
			{
				hash += -2147483648;
			}
			byte objectConfig = (byte) ((face << 6) + type);
			if (type == 22)
			{
				if (!lowMemory || gameObjectDefinition.hasActions || gameObjectDefinition.unknown)
				{
					Renderable renderable;
					if (gameObjectDefinition.animationId == -1 && gameObjectDefinition.childrenIds == null)
					{
						renderable = gameObjectDefinition.getGameObjectModel(22, face, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
					}
					else
					{
						renderable = new GameObject(objectId, face, 22, vertexHeightRight, vertexHeightTopRight, vertexHeight, vertexHeightTop, gameObjectDefinition.animationId, true);
					}
					scene.addGroundDecoration(x, y, plane, vertexMix, hash, renderable, objectConfig);
					if (gameObjectDefinition.solid && gameObjectDefinition.hasActions && collisionMap != null)
					{
						collisionMap.markBlocked(x, y);
					}
				}
			}
			else if (type == 10 || type == 11)
			{
				Renderable renderable;
				if (gameObjectDefinition.animationId == -1 && gameObjectDefinition.childrenIds == null)
				{
					renderable = gameObjectDefinition.getGameObjectModel(10, face, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
				}
				else
				{
					renderable = new GameObject(objectId, face, 10, vertexHeightRight, vertexHeightTopRight, vertexHeight, vertexHeightTop, gameObjectDefinition.animationId, true);
				}
				if (renderable != null)
				{
					int i_198_ = 0;
					if (type == 11)
					{
						i_198_ += 256;
					}
					int sizeX;
					int sizeY;
					if (face == 1 || face == 3)
					{
						sizeX = gameObjectDefinition.sizeY;
						sizeY = gameObjectDefinition.sizeX;
					}
					else
					{
						sizeX = gameObjectDefinition.sizeX;
						sizeY = gameObjectDefinition.sizeY;
					}
					if (scene.addEntityB(x, y, plane, vertexMix, i_198_, sizeY, sizeX, hash, renderable, objectConfig) && gameObjectDefinition.castsShadow)
					{
						Model model;
						if (renderable instanceof Model)
						{
							model = (Model) renderable;
						}
						else
						{
							model = gameObjectDefinition.getGameObjectModel(10, face, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
						}
						if (model != null)
						{
							for (int sizeXCounter = 0; sizeXCounter <= sizeX; sizeXCounter++)
							{
								for (int sizeYCounter = 0; sizeYCounter <= sizeY; sizeYCounter++)
								{
									int shadowIntensity = model.diagonal2DAboveOrigin / 4;
									if (shadowIntensity > 30)
									{
										shadowIntensity = 30;
									}
									if (shadowIntensity > (object_shadow_data[plane][x + sizeXCounter][y + sizeYCounter]))
									{
										object_shadow_data[plane][x + sizeXCounter][y + sizeYCounter] = (byte) shadowIntensity;
									}
								}
							}
						}
					}
				}
				if (gameObjectDefinition.solid && collisionMap != null)
				{
					collisionMap.markSolidOccupant(y, face, gameObjectDefinition.sizeY, gameObjectDefinition.sizeX, gameObjectDefinition.walkable, x);
				}
			}
			else if (type >= 12)
			{
				Renderable renderable;
				if (gameObjectDefinition.animationId == -1 && gameObjectDefinition.childrenIds == null)
				{
					renderable = gameObjectDefinition.getGameObjectModel(type, face, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
				}
				else
				{
					renderable = new GameObject(objectId, face, type, vertexHeightRight, vertexHeightTopRight, vertexHeight, vertexHeightTop, gameObjectDefinition.animationId, true);
				}
				scene.addEntityB(x, y, plane, vertexMix, 0, 1, 1, hash, renderable, objectConfig);
				if (type >= 12 && type <= 17 && type != 13 && plane > 0)
				{
					tile_culling_bitmap[plane][x][y] |= 0x924;
				}
				if (gameObjectDefinition.solid && collisionMap != null)
				{
					collisionMap.markSolidOccupant(y, face, gameObjectDefinition.sizeY, gameObjectDefinition.sizeX, gameObjectDefinition.walkable, x);
				}
			}
			else if (type == 0)
			{
				Renderable renderable;
				if (gameObjectDefinition.animationId == -1 && gameObjectDefinition.childrenIds == null)
				{
					renderable = gameObjectDefinition.getGameObjectModel(0, face, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
				}
				else
				{
					renderable = new GameObject(objectId, face, 0, vertexHeightRight, vertexHeightTopRight, vertexHeight, vertexHeightTop, gameObjectDefinition.animationId, true);
				}
				scene.addWall(x, y, plane, vertexMix, POWERS_OF_TWO[face], 0, hash, renderable, null, objectConfig);
				if (face == 0)
				{
					if (gameObjectDefinition.castsShadow)
					{
						object_shadow_data[plane][x][y] = (byte) 50;
						object_shadow_data[plane][x][y + 1] = (byte) 50;
					}
					if (gameObjectDefinition.wall)
					{
						tile_culling_bitmap[plane][x][y] |= 0x249;
					}
				}
				else if (face == 1)
				{
					if (gameObjectDefinition.castsShadow)
					{
						object_shadow_data[plane][x][y + 1] = (byte) 50;
						object_shadow_data[plane][x + 1][y + 1] = (byte) 50;
					}
					if (gameObjectDefinition.wall)
					{
						tile_culling_bitmap[plane][x][y + 1] |= 0x492;
					}
				}
				else if (face == 2)
				{
					if (gameObjectDefinition.castsShadow)
					{
						object_shadow_data[plane][x + 1][y] = (byte) 50;
						object_shadow_data[plane][x + 1][y + 1] = (byte) 50;
					}
					if (gameObjectDefinition.wall)
					{
						tile_culling_bitmap[plane][x + 1][y] |= 0x249;
					}
				}
				else if (face == 3)
				{
					if (gameObjectDefinition.castsShadow)
					{
						object_shadow_data[plane][x][y] = (byte) 50;
						object_shadow_data[plane][x + 1][y] = (byte) 50;
					}
					if (gameObjectDefinition.wall)
					{
						tile_culling_bitmap[plane][x][y] |= 0x492;
					}
				}
				if (gameObjectDefinition.solid && collisionMap != null)
				{
					collisionMap.markWall(x, y, type, face, gameObjectDefinition.walkable);
				}
				if (gameObjectDefinition.offsetAmplifier != 16)
				{
					scene.displaceWallDecoration(x, y, plane, gameObjectDefinition.offsetAmplifier);
				}
			}
			else if (type == 1)
			{
				Renderable renderable;
				if (gameObjectDefinition.animationId == -1 && gameObjectDefinition.childrenIds == null)
				{
					renderable = gameObjectDefinition.getGameObjectModel(1, face, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
				}
				else
				{
					renderable = new GameObject(objectId, face, 1, vertexHeightRight, vertexHeightTopRight, vertexHeight, vertexHeightTop, gameObjectDefinition.animationId, true);
				}
				scene.addWall(x, y, plane, vertexMix, WALL_CORNER_ORIENTATION[face], 0, hash, renderable, null, objectConfig);
				if (gameObjectDefinition.castsShadow)
				{
					if (face == 0)
					{
						object_shadow_data[plane][x][y + 1] = (byte) 50;
					}
					else if (face == 1)
					{
						object_shadow_data[plane][x + 1][y + 1] = (byte) 50;
					}
					else if (face == 2)
					{
						object_shadow_data[plane][x + 1][y] = (byte) 50;
					}
					else if (face == 3)
					{
						object_shadow_data[plane][x][y] = (byte) 50;
					}
				}
				if (gameObjectDefinition.solid && collisionMap != null)
				{
					collisionMap.markWall(x, y, type, face, gameObjectDefinition.walkable);
				}
			}
			else if (type == 2)
			{
				int i_204_ = face + 1 & 0x3;
				Renderable renderable;
				Renderable renderable1;
				if (gameObjectDefinition.animationId == -1 && gameObjectDefinition.childrenIds == null)
				{
					renderable = gameObjectDefinition.getGameObjectModel(2, 4 + face, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
					renderable1 = gameObjectDefinition.getGameObjectModel(2, i_204_, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
				}
				else
				{
					renderable = new GameObject(objectId, 4 + face, 2, vertexHeightRight, vertexHeightTopRight, vertexHeight, vertexHeightTop, gameObjectDefinition.animationId, true);
					renderable1 = new GameObject(objectId, i_204_, 2, vertexHeightRight, vertexHeightTopRight, vertexHeight, vertexHeightTop, gameObjectDefinition.animationId, true);
				}
				scene.addWall(x, y, plane, vertexMix, POWERS_OF_TWO[face], POWERS_OF_TWO[i_204_], hash, renderable, renderable1, objectConfig);
				if (gameObjectDefinition.wall)
				{
					if (face == 0)
					{
						tile_culling_bitmap[plane][x][y] |= 0x249;
						tile_culling_bitmap[plane][x][y + 1] |= 0x492;
					}
					else if (face == 1)
					{
						tile_culling_bitmap[plane][x][y + 1] |= 0x492;
						tile_culling_bitmap[plane][x + 1][y] |= 0x249;
					}
					else if (face == 2)
					{
						tile_culling_bitmap[plane][x + 1][y] |= 0x249;
						tile_culling_bitmap[plane][x][y] |= 0x492;
					}
					else if (face == 3)
					{
						tile_culling_bitmap[plane][x][y] |= 0x492;
						tile_culling_bitmap[plane][x][y] |= 0x249;
					}
				}
				if (gameObjectDefinition.solid && collisionMap != null)
				{
					collisionMap.markWall(x, y, type, face, gameObjectDefinition.walkable);
				}
				if (gameObjectDefinition.offsetAmplifier != 16)
				{
					scene.displaceWallDecoration(x, y, plane, gameObjectDefinition.offsetAmplifier);
				}
			}
			else if (type == 3)
			{
				Renderable renderable;
				if (gameObjectDefinition.animationId == -1 && gameObjectDefinition.childrenIds == null)
				{
					renderable = gameObjectDefinition.getGameObjectModel(3, face, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
				}
				else
				{
					renderable = new GameObject(objectId, face, 3, vertexHeightRight, vertexHeightTopRight, vertexHeight, vertexHeightTop, gameObjectDefinition.animationId, true);
				}
				scene.addWall(x, y, plane, vertexMix, WALL_CORNER_ORIENTATION[face], 0, hash, renderable, null, objectConfig);
				if (gameObjectDefinition.castsShadow)
				{
					if (face == 0)
					{
						object_shadow_data[plane][x][y + 1] = (byte) 50;
					}
					else if (face == 1)
					{
						object_shadow_data[plane][x + 1][y + 1] = (byte) 50;
					}
					else if (face == 2)
					{
						object_shadow_data[plane][x + 1][y] = (byte) 50;
					}
					else if (face == 3)
					{
						object_shadow_data[plane][x][y] = (byte) 50;
					}
				}
				if (gameObjectDefinition.solid && collisionMap != null)
				{
					collisionMap.markWall(x, y, type, face, gameObjectDefinition.walkable);
				}
			}
			else if (type == 9)
			{
				Renderable renderable;
				if (gameObjectDefinition.animationId == -1 && gameObjectDefinition.childrenIds == null)
				{
					renderable = gameObjectDefinition.getGameObjectModel(type, face, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
				}
				else
				{
					renderable = new GameObject(objectId, face, type, vertexHeightRight, vertexHeightTopRight, vertexHeight, vertexHeightTop, gameObjectDefinition.animationId, true);
				}
				scene.addEntityB(x, y, plane, vertexMix, 0, 1, 1, hash, renderable, objectConfig);
				if (gameObjectDefinition.solid && collisionMap != null)
				{
					collisionMap.markSolidOccupant(y, face, gameObjectDefinition.sizeY, gameObjectDefinition.sizeX, gameObjectDefinition.walkable, x);
				}
			}
			else
			{
				if (gameObjectDefinition.adjustToTerrain)
				{
					if (face == 1)
					{
						int temp = vertexHeightTop;
						vertexHeightTop = vertexHeightTopRight;
						vertexHeightTopRight = vertexHeightRight;
						vertexHeightRight = vertexHeight;
						vertexHeight = temp;
					}
					else if (face == 2)
					{
						int temp = vertexHeightTop;
						vertexHeightTop = vertexHeightRight;
						vertexHeightRight = temp;
						temp = vertexHeightTopRight;
						vertexHeightTopRight = vertexHeight;
						vertexHeight = temp;
					}
					else if (face == 3)
					{
						int temp = vertexHeightTop;
						vertexHeightTop = vertexHeight;
						vertexHeight = vertexHeightRight;
						vertexHeightRight = vertexHeightTopRight;
						vertexHeightTopRight = temp;
					}
				}
				if (type == 4)
				{
					Renderable renderable;
					if (gameObjectDefinition.animationId == -1 && gameObjectDefinition.childrenIds == null)
					{
						renderable = gameObjectDefinition.getGameObjectModel(4, 0, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
					}
					else
					{
						renderable = new GameObject(objectId, 0, 4, vertexHeightRight, vertexHeightTopRight, vertexHeight, vertexHeightTop, gameObjectDefinition.animationId, true);
					}
					scene.addWallDecoration(x, y, plane, vertexMix, 0, 0, face * 512, hash, renderable, objectConfig, POWERS_OF_TWO[face]);
				}
				else if (type == 5)
				{
					int offset = 16;
					int i_210_ = scene.getWallObjectHash(x, y, plane);
					if (i_210_ > 0)
					{
						offset = GameObjectDefinition.getDefinition(i_210_ >> 14 & 0x7fff).offsetAmplifier;
					}
					Renderable renderable;
					if (gameObjectDefinition.animationId == -1 && gameObjectDefinition.childrenIds == null)
					{
						renderable = gameObjectDefinition.getGameObjectModel(4, 0, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
					}
					else
					{
						renderable = new GameObject(objectId, 0, 4, vertexHeightRight, vertexHeightTopRight, vertexHeight, vertexHeightTop, gameObjectDefinition.animationId, true);
					}
					scene.addWallDecoration(x, y, plane, vertexMix, FACE_OFFSET_X[face] * offset, FACE_OFFSET_Y[face] * offset, face * 512, hash, renderable, objectConfig, POWERS_OF_TWO[face]);
				}
				else if (type == 6)
				{
					Renderable renderable;
					if (gameObjectDefinition.animationId == -1 && gameObjectDefinition.childrenIds == null)
					{
						renderable = gameObjectDefinition.getGameObjectModel(4, 0, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
					}
					else
					{
						renderable = new GameObject(objectId, 0, 4, vertexHeightRight, vertexHeightTopRight, vertexHeight, vertexHeightTop, gameObjectDefinition.animationId, true);
					}
					scene.addWallDecoration(x, y, plane, vertexMix, 0, 0, face, hash, renderable, objectConfig, 256);
				}
				else if (type == 7)
				{
					Renderable renderable;
					if (gameObjectDefinition.animationId == -1 && gameObjectDefinition.childrenIds == null)
					{
						renderable = gameObjectDefinition.getGameObjectModel(4, 0, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
					}
					else
					{
						renderable = new GameObject(objectId, 0, 4, vertexHeightRight, vertexHeightTopRight, vertexHeight, vertexHeightTop, gameObjectDefinition.animationId, true);
					}
					scene.addWallDecoration(x, y, plane, vertexMix, 0, 0, face, hash, renderable, objectConfig, 512);
				}
				else if (type == 8)
				{
					Renderable renderable;
					if (gameObjectDefinition.animationId == -1 && gameObjectDefinition.childrenIds == null)
					{
						renderable = gameObjectDefinition.getGameObjectModel(4, 0, vertexHeight, vertexHeightRight, vertexHeightTopRight, vertexHeightTop, -1);
					}
					else
					{
						renderable = new GameObject(objectId, 0, 4, vertexHeightRight, vertexHeightTopRight, vertexHeight, vertexHeightTop, gameObjectDefinition.animationId, true);
					}
					scene.addWallDecoration(x, y, plane, vertexMix, 0, 0, face, hash, renderable, objectConfig, 768);
				}
			}
		}
	}

	public void loadTerrainBlock(int blockX, int offsetX, int blockY, int offsetY, byte[] blockData, CollisionMap[] collisionMap)
	{
		for (int plane = 0; plane < 4; plane++)
		{
			for (int tileX = 0; tileX < 64; tileX++)
			{
				for (int tileY = 0; tileY < 64; tileY++)
				{
					if (blockX + tileX > 0 && blockX + tileX < 103 && blockY + tileY > 0 && blockY + tileY < 103)
					{
						collisionMap[plane].clippingData[blockX + tileX][blockY + tileY] &= ~0x1000000;
					}
				}
			}
		}
		Buffer stream = new Buffer(blockData);
		for (int plane = 0; plane < 4; plane++)
		{
			for (int tileX = 0; tileX < 64; tileX++)
			{
				for (int tileY = 0; tileY < 64; tileY++)
				{
					loadTerrainTile(tileX + blockX, offsetX, tileY + blockY, offsetY, plane, stream, 0);
				}
			}
		}
	}

	public int getHSLBitset(int h, int s, int l)
	{
		if (l > 179)
		{
			s /= 2;
		}
		if (l > 192)
		{
			s /= 2;
		}
		if (l > 217)
		{
			s /= 2;
		}
		if (l > 243)
		{
			s /= 2;
		}
		return (h / 4 << 10) + (s / 32 << 7) + l / 2;
	}

	public void loadObjectBlock(int blockX, int blockY, CollisionMap[] collisionMap, Scene scene, byte[] blockData)
	{
		Buffer stream = new Buffer(blockData);
		int objectId = -1;
		while (true)
		{
			int objectIdOffset = stream.getSmart();
			if (objectIdOffset == 0)
			{
				break;
			}
			objectId += objectIdOffset;
			int position = 0;
			while (true)
			{
				int positionOffset = stream.getSmart();
				if (positionOffset == 0)
				{
					break;
				}
				position += positionOffset - 1;
				int tileY = position & 0x3f;
				int tileX = position >> 6 & 0x3f;
				int tilePlane = position >> 12;
				int hash = stream.getUnsignedByte();
				int type = hash >> 2;
				int orientation = hash & 0x3;
				int x = tileX + blockX;
				int y = tileY + blockY;
				if (x > 0 && y > 0 && x < 103 && y < 103)
				{
					int markingPlane = tilePlane;
					if ((tile_flags[1][x][y] & 0x2) == 2)
					{
						markingPlane--;
					}
					CollisionMap collisionMap_ = null;
					if (markingPlane >= 0)
					{
						collisionMap_ = collisionMap[markingPlane];
					}
					renderObject(scene, collisionMap_, y, tilePlane, x, orientation, type, objectId);
				}
			}
		}
	}

	public void initiateVertexHeights(int xOffset, int xLength, int yOffset, int yLength)
	{
		for (int y = yOffset; y <= yOffset + yLength; y++)
		{
			for (int x = xOffset; x <= xOffset + xLength; x++)
			{
				if (x >= 0 && x < size_x && y >= 0 && y < size_z)
				{
					object_shadow_data[0][x][y] = (byte) 127;
					if (x == xOffset && x > 0)
					{
						tile_height[0][x][y] = tile_height[0][x - 1][y];
					}
					if (x == xOffset + xLength && x < size_x - 1)
					{
						tile_height[0][x][y] = tile_height[0][x + 1][y];
					}
					if (y == yOffset && y > 0)
					{
						tile_height[0][x][y] = tile_height[0][x][y - 1];
					}
					if (y == yOffset + yLength && y < size_z - 1)
					{
						tile_height[0][x][y] = tile_height[0][x][y + 1];
					}
				}
			}
		}

	}

	public int mixLightnessSigned(int hsl, int lightness)
	{
		if (hsl == -2)
		{
			return 12345678;
		}
		if (hsl == -1)
		{
			if (lightness < 0)
			{
				lightness = 0;
			}
			else if (lightness > 127)
			{
				lightness = 127;
			}
			lightness = 127 - lightness;
			return lightness;
		}
		lightness = lightness * (hsl & 0x7f) / 128;
		if (lightness < 2)
		{
			lightness = 2;
		}
		else if (lightness > 126)
		{
			lightness = 126;
		}
		return (hsl & 0xff80) + lightness;
	}

	public void loadTerrainTile(int tileX, int offsetX, int tileY, int offsetY, int tileZ, Buffer stream, int i1)
	{
		if (tileX >= 0 && tileX < 104 && tileY >= 0 && tileY < 104)
		{
			tile_flags[tileZ][tileX][tileY] = (byte) 0;
			for (; ; )
			{
				int value = stream.getUnsignedByte();
				if (value == 0)
				{
					if (tileZ == 0)
					{
						tile_height[0][tileX][tileY] = -calculateVertexHeight(932731 + tileX + offsetX, 556238 + tileY + offsetY) * 8;
					}
					else
					{
						tile_height[tileZ][tileX][tileY] = (tile_height[tileZ - 1][tileX][tileY] - 240);
						break;
					}
					break;
				}
				if (value == 1)
				{
					int height = stream.getUnsignedByte();
					if (height == 1)
					{
						height = 0;
					}
					if (tileZ == 0)
					{
						tile_height[0][tileX][tileY] = -height * 8;
					}
					else
					{
						tile_height[tileZ][tileX][tileY] = (tile_height[tileZ - 1][tileX][tileY] - height * 8);
						break;
					}
					break;
				}
				if (value <= 49)
				{
					overlayFloorIds[tileZ][tileX][tileY] = stream.getByte();
					overlayClippingPaths[tileZ][tileX][tileY] = (byte) ((value - 2) / 4);
					overlayRotations[tileZ][tileX][tileY] = (byte) (value - 2 + i1 & 0x3);
				}
				else if (value <= 81)
				{
					tile_flags[tileZ][tileX][tileY] = (byte) (value - 49);
				}
				else
				{
					underlayFloorIds[tileZ][tileX][tileY] = (byte) (value - 81);
				}
			}
		}
		else
		{
			for (; ; )
			{
				int value = stream.getUnsignedByte();
				if (value == 0)
				{
					break;
				}
				if (value == 1)
				{
					stream.getUnsignedByte();
					break;
				}
				if (value <= 49)
				{
					stream.getUnsignedByte();
				}
			}
		}
	}
}
