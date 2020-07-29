package com.jagex.runescape377.scene;

import com.jagex.runescape377.media.Rasterizer;
import com.jagex.runescape377.media.Rasterizer3D;
import com.jagex.runescape377.media.VertexNormal;
import com.jagex.runescape377.media.renderable.Model;
import com.jagex.runescape377.media.renderable.Renderable;
import com.jagex.runescape377.scene.tile.ComplexTile;
import com.jagex.runescape377.scene.tile.FloorDecoration;
import com.jagex.runescape377.scene.tile.GenericTile;
import com.jagex.runescape377.scene.tile.SceneTile;
import com.jagex.runescape377.scene.tile.Wall;
import com.jagex.runescape377.scene.tile.WallDecoration;
import com.jagex.runescape377.util.LinkedList;
import com.jagex.runescape377.world.GroundArray;

public class Scene
{

	private static final int[] faceOffsetX2 = {53, -53, -53, 53};
	private static final int[] faceOffsetY2 = {-53, -53, 53, 53};
	private static final int[] faceOffsetX3 = {-45, 45, 45, -45};
	private static final int[] faceOffsetY3 = {45, 45, -45, -45};
	private static final int[] anIntArray493 = {19, 55, 38, 155, 255, 110, 137, 205, 76};
	private static final int[] anIntArray494 = {160, 192, 80, 96, 0, 144, 80, 48, 160};
	private static final int[] TILE_WALL_DRAW_FLAGS_1 = {76, 8, 137, 4, 0, 1, 38, 2, 19};
	private static final int[] WALL_UNCULL_FLAGS_0 = {0, 0, 2, 0, 0, 2, 1, 1, 0};
	private static final int[] anIntArray497 = {2, 0, 0, 2, 0, 0, 0, 4, 4};
	private static final int[] anIntArray498 = {0, 4, 4, 8, 0, 0, 8, 0, 0};
	private static final int[] anIntArray499 = {1, 1, 0, 0, 0, 8, 0, 0, 8};
	private static final int[] textureRGB = {41, 39248, 41, 4643, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 43086,
		41, 41, 41, 41, 41, 41, 41, 8602, 41, 28992, 41, 41, 41, 41, 41, 5056, 41, 41, 41, 7079, 41, 41, 41, 41,
		41, 41, 41, 41, 41, 41, 3131, 41, 41, 41};
	public static boolean lowMemory = true;
	public static int clickedTileX = -1;
	public static int clickedTileY = -1;
	private static int anInt461;
	private static int plane;
	private static int cycle;
	private static int currentPositionX;
	private static int mapBoundsX;
	private static int currentPositionY;
	private static int mapBoundsY;
	private static int cameraPositionTileX;
	private static int cameraPositionTileY;
	private static int cameraPosX;
	private static int cameraPosZ;
	private static int cameraPosY;
	private static int curveSineY;
	private static int curveCosineY;
	private static int curveSineX;
	private static int curveCosineX;
	private static InteractiveObject[] interactiveObjects = new InteractiveObject[100];
	private static boolean clicked;
	private static int clickX;
	private static int clickY;
	private static int anInt487 = 4;
	private static int[] cullingClusterPointer = new int[anInt487];
	private static SceneCluster[][] cullingClusters = new SceneCluster[anInt487][500];
	private static int processedCullingClustersPointer;
	private static SceneCluster[] processedCullingClusters = new SceneCluster[500];
	private static LinkedList tileList = new LinkedList();
	private static boolean[][][][] TILE_VISIBILITY_MAPS = new boolean[8][32][51][51];
	private static boolean[][] TILE_VISIBILITY_MAP;
	private static int anInt508;
	private static int anInt509;
	private static int anInt510;
	private static int anInt511;
	private static int anInt512;
	private static int anInt513;
	private int mapSizeZ;
	private int mapSizeX;
	private int mapSizeY;
	private int[][][] heightMap;
	private GroundArray<SceneTile> tileArray;
	private int currentPositionZ;
	private int sceneSpawnRequestsCacheCurrentPos;
	private InteractiveObject[] sceneSpawnRequestsCache;
	private int[][][] anIntArrayArrayArray445;
	private int[] anIntArray486;
	private int[] anIntArray487;
	private int anInt503;
	private int[][] tileShapePoints = {new int[16], {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
		{1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1}, {1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0},
		{0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1}, {0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
		{1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0}, {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1},
		{1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1},
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1}};
	private int[][] tileShapeIndices = {{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
		{12, 8, 4, 0, 13, 9, 5, 1, 14, 10, 6, 2, 15, 11, 7, 3},
		{15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0},
		{3, 7, 11, 15, 2, 6, 10, 14, 1, 5, 9, 13, 0, 4, 8, 12}};

	public Scene(int[][][] heightMap)
	{
		final int length = 104;// was parameter
		final int width = 104;// was parameter
		final int height = 4;// was parameter
		sceneSpawnRequestsCache = new InteractiveObject[5000];
		anIntArray486 = new int[10000];
		anIntArray487 = new int[10000];
		mapSizeZ = height;
		mapSizeX = width;
		mapSizeY = length;
		tileArray = new GroundArray<>(new SceneTile[height][width][length]);
		anIntArrayArrayArray445 = new int[height][width + 1][length + 1];
		this.heightMap = heightMap;
		initToNull();
	}

	public static void nullLoader()
	{
		interactiveObjects = null;
		cullingClusterPointer = null;
		cullingClusters = null;
		tileList = null;
		TILE_VISIBILITY_MAPS = null;
		TILE_VISIBILITY_MAP = null;

	}

	static void createCullingCluster(final int z, int highestX, int lowestX, int highestY, int lowestY, int highestZ, int lowestZ, int searchMask)
	{
		SceneCluster scenecluster = new SceneCluster();
		scenecluster.tileStartX = lowestX / 128;
		scenecluster.tileEndX = highestX / 128;
		scenecluster.tileStartY = lowestY / 128;
		scenecluster.tileEndY = highestY / 128;
		scenecluster.searchMask = searchMask;
		scenecluster.worldStartX = lowestX;
		scenecluster.worldEndX = highestX;
		scenecluster.worldStartY = lowestY;
		scenecluster.worldEndY = highestY;
		scenecluster.worldEndZ = highestZ;
		scenecluster.worldStartZ = lowestZ;
		cullingClusters[z][cullingClusterPointer[z]++] = scenecluster;
	}

	public static void method277(int l, int k, int i1, int i, int[] ai)
	{
		anInt510 = 0;
		anInt511 = 0;
		anInt512 = i1;
		anInt513 = i;
		anInt508 = i1 / 2;
		anInt509 = i / 2;
		boolean[][][][] aflag = new boolean[9][32][53][53];
		for (int j1 = 128; j1 <= 384; j1 += 32)
		{
			for (int k1 = 0; k1 < 2048; k1 += 64)
			{
				curveSineY = Model.SINE[j1];
				curveCosineY = Model.COSINE[j1];
				curveSineX = Model.SINE[k1];
				curveCosineX = Model.COSINE[k1];
				int i2 = (j1 - 128) / 32;
				int k2 = k1 / 64;
				for (int i3 = -26; i3 <= 26; i3++)
				{
					for (int k3 = -26; k3 <= 26; k3++)
					{
						int l3 = i3 * 128;
						int j4 = k3 * 128;
						boolean flag1 = false;
						for (int l4 = -l; l4 <= k; l4 += 128)
						{
							if (!method278(j4, l3, ai[i2] + l4))
							{
								continue;
							}
							flag1 = true;
							break;
						}

						aflag[i2][k2][i3 + 25 + 1][k3 + 25 + 1] = flag1;
					}

				}

			}

		}

		for (int l1 = 0; l1 < 8; l1++)
		{
			for (int j2 = 0; j2 < 32; j2++)
			{
				for (int l2 = -25; l2 < 25; l2++)
				{
					for (int j3 = -25; j3 < 25; j3++)
					{
						boolean flag = false;
						label0:
						for (int i4 = -1; i4 <= 1; i4++)
						{
							for (int k4 = -1; k4 <= 1; k4++)
							{
								if (aflag[l1][j2][l2 + i4 + 25 + 1][j3 + k4 + 25 + 1])
								{
									flag = true;
								}
								else if (aflag[l1][(j2 + 1) % 31][l2 + i4 + 25 + 1][j3 + k4 + 25 + 1])
								{
									flag = true;
								}
								else if (aflag[l1 + 1][j2][l2 + i4 + 25 + 1][j3 + k4 + 25 + 1])
								{
									flag = true;
								}
								else
								{
									if (!aflag[l1 + 1][(j2 + 1) % 31][l2 + i4 + 25 + 1][j3 + k4 + 25 + 1])
									{
										continue;
									}
									flag = true;
								}
								break label0;
							}

						}

						TILE_VISIBILITY_MAPS[l1][j2][l2 + 25][j3 + 25] = flag;
					}

				}

			}

		}
	}

	private static boolean method278(int i, int j, int l)
	{
		int i1 = i * curveSineX + j * curveCosineX >> 16;
		int j1 = i * curveCosineX - j * curveSineX >> 16;
		int k1 = l * curveSineY + j1 * curveCosineY >> 16;
		int l1 = l * curveCosineY - j1 * curveSineY >> 16;
		if (k1 < 50 || k1 > 3500)
		{
			return false;
		}
		int i2 = anInt508 + (i1 << 9) / k1;
		int j2 = anInt509 + (l1 << 9) / k1;
		return i2 >= anInt510 && i2 <= anInt512 && j2 >= anInt511 && j2 <= anInt513;
	}

	public void initToNull()
	{
		for (int z = 0; z < mapSizeZ; z++)
		{
			for (int x = 0; x < mapSizeX; x++)
			{
				for (int y = 0; y < mapSizeY; y++)
				{
					tileArray.clearTile(z, x, y);
				}

			}

		}

		for (int l = 0; l < anInt487; l++)
		{
			for (int j1 = 0; j1 < cullingClusterPointer[l]; j1++)
			{
				cullingClusters[l][j1] = null;
			}

			cullingClusterPointer[l] = 0;
		}

		for (int k1 = 0; k1 < sceneSpawnRequestsCacheCurrentPos; k1++)
		{
			sceneSpawnRequestsCache[k1] = null;
		}

		sceneSpawnRequestsCacheCurrentPos = 0;
		for (int l1 = 0; l1 < interactiveObjects.length; l1++)
		{
			interactiveObjects[l1] = null;
		}

	}

	public void setHeightLevel(int z)
	{
		currentPositionZ = z;
		for (int x = 0; x < mapSizeX; x++)
		{
			for (int y = 0; y < mapSizeY; y++)
			{
				if (tileArray.isTileEmpty(z, x, y))
				{
					tileArray.setTile(z, x, y, new SceneTile(x, y, z));
				}
			}

		}

	}

	void setBridgeMode(int x, int y)
	{
		SceneTile scenetile = tileArray.getTile(0, x, y);
		for (int z = 0; z < 3; z++)
		{
			SceneTile _tile = tileArray.setTile(z, x, y, tileArray.getTile(z + 1, x, y));
			if (_tile != null)
			{
				_tile.z--;
				for (int e = 0; e < _tile.entityCount; e++)
				{
					InteractiveObject entity = _tile.interactiveObjects[e];
					if ((entity.uid >> 29 & 3) == 2 && entity.tileLeft == x && entity.tileTop == y)
					{
						entity.z--;
					}
				}

			}
		}

		if (tileArray.isTileEmpty(0, x, y))
		{
			tileArray.setTile(0, x, y, new SceneTile(x, y, 0));
		}
		tileArray.getTile(0, x, y).tileBelow = scenetile;
		tileArray.clearTile(3, x, y);
	}

	void setTileLogicHeight(int z, int x, int y, int logicHeight)
	{
		SceneTile sceneTile = tileArray.getTile(z, x, y);
		if (sceneTile != null)
		{
			sceneTile.logicHeight = logicHeight;
		}
	}

	void addTile(int plane, int x, int y, int shape, int clippingPathRotation, int textureId, int vertexHeightSW, int vertexHeightSE, int vertexHeightNE, int vertexHeightNW, int cA, int cB,
					int cD, int cC, int colourA, int colourB, int colourD, int colourC, int underlayRGB, int overlayRGB)
	{
		if (shape == 0)
		{
			GenericTile tile = new GenericTile(cA, cB, cC, cD, -1, underlayRGB, false);
			for (int _z = plane; _z >= 0; _z--)
			{
				if (tileArray.isTileEmpty(_z, x, y))
				{
					tileArray.setTile(_z, x, y, new SceneTile(x, y, _z));
				}
			}

			tileArray.getTile(plane, x, y).plainTile = tile;
		}
		else if (shape == 1)
		{
			GenericTile tile = new GenericTile(colourA, colourB, colourC, colourD, textureId, overlayRGB, vertexHeightSW == vertexHeightSE && vertexHeightSW == vertexHeightNE && vertexHeightSW == vertexHeightNW);
			for (int _z = plane; _z >= 0; _z--)
			{
				if (tileArray.isTileEmpty(_z, x, y))
				{
					tileArray.setTile(_z, x, y, new SceneTile(x, y, _z));
				}
			}


			tileArray.getTile(plane, x, y).plainTile = tile;
		}
		else
		{
			ComplexTile tile = new ComplexTile(x, vertexHeightSW, vertexHeightSE, vertexHeightNW, vertexHeightNE, y, clippingPathRotation, textureId, shape, cA, colourA, cB, colourB, cC, colourC, cD, colourD, overlayRGB, underlayRGB);
			for (int _z = plane; _z >= 0; _z--)
			{
				if (tileArray.isTileEmpty(_z, x, y))
				{
					tileArray.setTile(_z, x, y, new SceneTile(x, y, _z));
				}
			}


			tileArray.getTile(plane, x, y).shapedTile = tile;
		}
	}

	void addGroundDecoration(int x, int y, int z, int drawHeight, int uid, Renderable renderable, byte config)
	{
		if (renderable == null)
		{
			return;
		}
		FloorDecoration floorDecoration = new FloorDecoration();
		floorDecoration.renderable = renderable;
		floorDecoration.x = x * 128 + 64;
		floorDecoration.y = y * 128 + 64;
		floorDecoration.z = drawHeight;
		floorDecoration.uid = uid;
		floorDecoration.config = config;
		if (tileArray.isTileEmpty(z, x, y))
		{
			tileArray.setTile(z, x, y, new SceneTile(x, y, z));
		}
		tileArray.getTile(z, x, y).floorDecoration = floorDecoration;
	}

	public void addGroundItemTile(int x, int y, int z, int drawHeight, int uid, Renderable firstGroundItem, Renderable secondGroundItem,
									Renderable thirdGroundItem)
	{
		GroundItemTile groundItemTile = new GroundItemTile();
		groundItemTile.firstGroundItem = firstGroundItem;
		groundItemTile.x = x * 128 + 64;
		groundItemTile.y = y * 128 + 64;
		groundItemTile.z = drawHeight;
		groundItemTile.uid = uid;
		groundItemTile.secondGroundItem = secondGroundItem;
		groundItemTile.thirdGroundItem = thirdGroundItem;
		int k1 = 0;
		SceneTile sceneTile = tileArray.getTile(z, x, y);
		if (sceneTile != null)
		{
			for (int e = 0; e < sceneTile.entityCount; e++)
			{
				if (sceneTile.interactiveObjects[e].renderable instanceof Model)
				{
					int i2 = ((Model) sceneTile.interactiveObjects[e].renderable).anInt1675;
					if (i2 > k1)
					{
						k1 = i2;
					}
				}
			}

		}
		groundItemTile.anInt180 = k1;
		if (tileArray.isTileEmpty(z, x, y))
		{
			tileArray.setTile(z, x, y, new SceneTile(x, y, z));
		}
		tileArray.getTile(z, x, y).groundItemTile = groundItemTile;
	}

	void addWall(int x, int y, int z, int drawHeight, int orientation, int orientation2, int uid, Renderable primary, Renderable secondary, byte config)
	{
		if (primary != null || secondary != null)
		{
			Wall wall = new Wall();
			wall.uid = uid;
			wall.config = config;
			wall.x = x * 128 + 64;
			wall.y = y * 128 + 64;
			wall.z = drawHeight;
			wall.primary = primary;
			wall.secondary = secondary;
			wall.orientation = orientation;
			wall.orientation2 = orientation2;
			for (int _z = z; _z >= 0; _z--)
			{
				if (tileArray.isTileEmpty(_z, x, y))
				{
					tileArray.setTile(_z, x, y, new SceneTile(x, y, _z));

				}
			}

			tileArray.getTile(z, x, y).wall = wall;
		}
	}

	void addWallDecoration(int x, int y, int z, int drawHeight, int offsetX, int offsetY, int face, int uid, Renderable renderable, byte config, int faceBits)
	{
		if (renderable != null)
		{
			WallDecoration wallDecoration = new WallDecoration();
			wallDecoration.uid = uid;
			wallDecoration.config = config;
			wallDecoration.x = x * 128 + 64 + offsetX;
			wallDecoration.y = y * 128 + 64 + offsetY;
			wallDecoration.z = drawHeight;
			wallDecoration.renderable = renderable;
			wallDecoration.configBits = faceBits;
			wallDecoration.face = face;
			for (int planeCounter = z; planeCounter >= 0; planeCounter--)
			{
				if (tileArray.isTileEmpty(planeCounter, x, y))
				{
					tileArray.setTile(planeCounter, x, y, new SceneTile(x, y, planeCounter));

				}
			}

			tileArray.getTile(z, x, y).wallDecoration = wallDecoration;
		}
	}

	boolean addEntityB(int x, int y, int z, int worldZ, int rotation, int tileWidth, int tileHeight, int uid, Renderable entity, byte config)
	{
		if (entity == null)
		{
			return true;
		}
		else
		{
			int worldX = x * 128 + 64 * tileHeight;
			int worldY = y * 128 + 64 * tileWidth;
			return addRenderableC(x, y, z, worldX, worldY, worldZ, rotation, tileWidth, tileHeight, uid, entity, false, config);
		}
	}

	public boolean addEntity(int z, int worldX, int worldY, int worldZ, Renderable entity, int uid, int delta, boolean accountForYaw,
								int yaw)
	{
		if (entity == null)
		{
			return true;
		}
		int minX = worldX - delta;
		int minY = worldY - delta;
		int maxX = worldX + delta;
		int maxY = worldY + delta;
		if (accountForYaw)
		{
			if (yaw > 640 && yaw < 1408)
			{
				maxY += 128;
			}
			if (yaw > 1152 && yaw < 1920)
			{
				maxX += 128;
			}
			if (yaw > 1664 || yaw < 384)
			{
				minY -= 128;
			}
			if (yaw > 128 && yaw < 896)
			{
				minX -= 128;
			}
		}
		minX /= 128;
		minY /= 128;
		maxX /= 128;
		maxY /= 128;
		return addRenderableC(minX, minY, z, worldX, worldY, worldZ, yaw, (maxY - minY) + 1, (maxX - minX) + 1, uid, entity, true, (byte) 0);
	}

	public boolean addEntity(int x, int y, int z, int worldX, int worldY, int worldZ, int rotation, int tileWidth, int tileHeight, Renderable entity,
								int uid)
	{

		return entity == null || addRenderableC(x, y, z, worldX, worldY, worldZ, rotation, (tileWidth - y) + 1, (tileHeight - x) + 1, uid, entity, true,
			(byte) 0);
	}

	private boolean addRenderableC(int minX, int minY, int z, int worldX, int worldY, int worldZ, int rotation, int tileWidth, int tileHeight,
									int uid, Renderable renderable, boolean isDynamic, byte config)
	{
		for (int x = minX; x < minX + tileHeight; x++)
		{
			for (int y = minY; y < minY + tileWidth; y++)
			{
				if (x < 0 || y < 0 || x >= mapSizeX || y >= mapSizeY)
				{
					return false;
				}
				SceneTile tile = tileArray.getTile(z, x, y);
				if (tile != null && tile.entityCount >= 5)
				{
					return false;
				}
			}

		}

		InteractiveObject interactiveObject = new InteractiveObject();
		interactiveObject.uid = uid;
		interactiveObject.config = config;
		interactiveObject.z = z;
		interactiveObject.worldX = worldX;
		interactiveObject.worldY = worldY;
		interactiveObject.worldZ = worldZ;
		interactiveObject.renderable = renderable;
		interactiveObject.rotation = rotation;
		interactiveObject.tileLeft = minX;
		interactiveObject.tileTop = minY;
		interactiveObject.tileRight = (minX + tileHeight) - 1;
		interactiveObject.tileBottom = (minY + tileWidth) - 1;
		for (int x = minX; x < minX + tileHeight; x++)
		{
			for (int y = minY; y < minY + tileWidth; y++)
			{
				int size = 0;
				if (x > minX)
				{
					size++;
				}
				if (x < (minX + tileHeight) - 1)
				{
					size += 4;
				}
				if (y > minY)
				{
					size += 8;
				}
				if (y < (minY + tileWidth) - 1)
				{
					size += 2;
				}
				for (int _z = z; _z >= 0; _z--)
				{
					if (tileArray.isTileEmpty(_z, x, y))
					{
						tileArray.setTile(_z, x, y, new SceneTile(x, y, _z));
					}
				}

				SceneTile sceneTile = tileArray.getTile(z, x, y);
				sceneTile.interactiveObjects[sceneTile.entityCount] = interactiveObject;
				sceneTile.sceneSpawnRequestsSize[sceneTile.entityCount] = size;
				sceneTile.interactiveObjectsSizeOR |= size;
				sceneTile.entityCount++;
			}

		}

		if (isDynamic)
		{
			sceneSpawnRequestsCache[sceneSpawnRequestsCacheCurrentPos++] = interactiveObject;
		}
		return true;
	}

	public void clearInteractiveObjectCache()
	{
		for (int j = 0; j < sceneSpawnRequestsCacheCurrentPos; j++)
		{
			InteractiveObject interactiveObject = sceneSpawnRequestsCache[j];
			remove(interactiveObject);
			sceneSpawnRequestsCache[j] = null;
		}

		sceneSpawnRequestsCacheCurrentPos = 0;
	}

	private void remove(InteractiveObject entity)
	{
		for (int x = entity.tileLeft; x <= entity.tileRight; x++)
		{
			for (int y = entity.tileTop; y <= entity.tileBottom; y++)
			{
				SceneTile tile = tileArray.getTile(entity.z, x, y);
				if (tile != null)
				{
					for (int e = 0; e < tile.entityCount; e++)
					{
						if (tile.interactiveObjects[e] != entity)
						{
							continue;
						}
						tile.entityCount--;
						for (int e2 = e; e2 < tile.entityCount; e2++)
						{
							tile.interactiveObjects[e2] = tile.interactiveObjects[e2 + 1];
							tile.sceneSpawnRequestsSize[e2] = tile.sceneSpawnRequestsSize[e2 + 1];
						}

						tile.interactiveObjects[tile.entityCount] = null;
						break;
					}

					tile.interactiveObjectsSizeOR = 0;
					for (int j1 = 0; j1 < tile.entityCount; j1++)
					{
						tile.interactiveObjectsSizeOR |= tile.sceneSpawnRequestsSize[j1];
					}

				}
			}

		}

	}

	void displaceWallDecoration(int x, int y, int z, int displacement)
	{
		SceneTile sceneTile = tileArray.getTile(z, x, y);
		if (sceneTile == null)
		{
			return;
		}
		WallDecoration wallDecoration = sceneTile.wallDecoration;
		if (wallDecoration == null)
		{
			return;
		}
		int absX = x * 128 + 64;
		int absY = y * 128 + 64;
		wallDecoration.x = absX + ((wallDecoration.x - absX) * displacement) / 16;
		wallDecoration.y = absY + ((wallDecoration.y - absY) * displacement) / 16;

	}

	public void removeWallObject(int x, int y, int z)
	{
		SceneTile tile = tileArray.getTile(z, x, y);
		if (tile != null)
		{
			tile.wall = null;
		}
	}

	public void removeWallDecoration(int x, int y, int z)
	{
		SceneTile tile = tileArray.getTile(z, x, y);
		if (tile != null)
		{
			tile.wallDecoration = null;
		}
	}

	public void removeInteractiveObject(int x, int y, int z)
	{
		SceneTile tile = tileArray.getTile(z, x, y);
		if (tile == null)
		{
			return;
		}
		for (int e = 0; e < tile.entityCount; e++)
		{
			InteractiveObject interactiveObject = tile.interactiveObjects[e];
			if ((interactiveObject.uid >> 29 & 3) == 2 && interactiveObject.tileLeft == x && interactiveObject.tileTop == y)
			{
				remove(interactiveObject);
				return;
			}
		}

	}

	public void method261(int x, int y, int z)
	{
		SceneTile sceneTile = tileArray.getTile(z, x, y);
		if (sceneTile == null)
		{
			return;
		}
		sceneTile.floorDecoration = null;
	}

	public void clearGroundItem(int z, int x, int y)
	{
		SceneTile sceneTile = tileArray.getTile(z, x, y);
		if (sceneTile != null)
		{
			sceneTile.groundItemTile = null;
		}
	}

	public Wall getWallObject(int level, int x, int y)
	{
		SceneTile sceneTile = tileArray.getTile(level, x, y);

		if (sceneTile == null)
		{
			return null;
		}
		else
		{
			return sceneTile.wall;
		}
	}

	public WallDecoration getWallDecoration(int level, int y, int x)
	{
		SceneTile sceneTile = tileArray.getTile(level, x, y);

		if (sceneTile == null)
		{
			return null;
		}
		else
		{
			return sceneTile.wallDecoration;
		}
	}

	public InteractiveObject method265(int x, int y, int level)
	{
		SceneTile sceneTile = tileArray.getTile(level, x, y);
		if (sceneTile == null)
		{
			return null;
		}
		for (int i = 0; i < sceneTile.entityCount; i++)
		{
			InteractiveObject interactiveObject = sceneTile.interactiveObjects[i];
			if ((interactiveObject.uid >> 29 & 3) == 2 && interactiveObject.tileLeft == x && interactiveObject.tileTop == y)
			{
				return interactiveObject;
			}
		}

		return null;
	}

	public FloorDecoration getFloorDecoration(int level, int x, int y)
	{
		SceneTile sceneTile = tileArray.getTile(level, x, y);
		if (sceneTile == null || sceneTile.floorDecoration == null)
		{
			return null;
		}
		else
		{
			return sceneTile.floorDecoration;
		}
	}

	public int getWallObjectHash(int x, int y, int z)
	{
		SceneTile sceneTile = tileArray.getTile(z, x, y);
		if (sceneTile == null || sceneTile.wall == null)
		{
			return 0;
		}
		else
		{
			return sceneTile.wall.uid;
		}
	}

	public int getWallDecorationHash(int x, int z, int y)
	{
		SceneTile sceneTile = tileArray.getTile(z, x, y);
		if (sceneTile == null || sceneTile.wallDecoration == null)
		{
			return 0;
		}
		else
		{
			return sceneTile.wallDecoration.uid;
		}
	}

	public int getLocationHash(int z, int x, int y)
	{
		SceneTile sceneTile = tileArray.getTile(z, x, y);
		if (sceneTile == null)
		{
			return 0;
		}
		for (int l = 0; l < sceneTile.entityCount; l++)
		{
			InteractiveObject interactiveObject = sceneTile.interactiveObjects[l];
			if ((interactiveObject.uid >> 29 & 3) == 2 && interactiveObject.tileLeft == x && interactiveObject.tileTop == y)
			{
				return interactiveObject.uid;
			}
		}

		return 0;
	}

	public int getFloorDecorationHash(int z, int x, int y)
	{
		SceneTile sceneTile = tileArray.getTile(z, x, y);
		if (sceneTile == null || sceneTile.floorDecoration == null)
		{
			return 0;
		}
		else
		{
			return sceneTile.floorDecoration.uid;
		}
	}

	public int getArrangement(int z, int x, int y, int l)
	{
		SceneTile sceneTile = tileArray.getTile(z, x, y);
		if (sceneTile == null)
		{
			return -1;
		}
		if (sceneTile.wall != null && sceneTile.wall.uid == l)
		{
			return sceneTile.wall.config & 0xff;
		}
		if (sceneTile.wallDecoration != null && sceneTile.wallDecoration.uid == l)
		{
			return sceneTile.wallDecoration.config & 0xff;
		}
		if (sceneTile.floorDecoration != null && sceneTile.floorDecoration.uid == l)
		{
			return sceneTile.floorDecoration.config & 0xff;
		}
		for (int i1 = 0; i1 < sceneTile.entityCount; i1++)
		{
			if (sceneTile.interactiveObjects[i1].uid == l)
			{
				return sceneTile.interactiveObjects[i1].config & 0xff;
			}
		}

		return -1;
	}

	void shadeModels(int i, int j, int k)
	{
		for (int _z = 0; _z < mapSizeZ; _z++)
		{
			for (int _x = 0; _x < mapSizeX; _x++)
			{
				for (int _y = 0; _y < mapSizeY; _y++)
				{
					SceneTile tile = tileArray.getTile(_z, _x, _y);
					if (tile != null)
					{
						Wall wall = tile.wall;
						if (wall != null && wall.primary != null
							&& wall.primary.verticesNormal != null)
						{
							method274(_y, _z, 0, 1, (Model) wall.primary, _x, 1);
							if (wall.secondary != null
								&& wall.secondary.verticesNormal != null)
							{
								method274(_y, _z, 0, 1, (Model) wall.secondary, _x, 1);
								mergeNormals((Model) wall.primary,
									(Model) wall.secondary, 0, 0, 0, false);
								((Model) wall.secondary).handleShading(i, j, 0, k);
							}
							((Model) wall.primary).handleShading(i, j, 0, k);
						}
						for (int k1 = 0; k1 < tile.entityCount; k1++)
						{
							InteractiveObject interactiveObject = tile.interactiveObjects[k1];
							if (interactiveObject != null && interactiveObject.renderable != null
								&& interactiveObject.renderable.verticesNormal != null)
							{
								method274(_y, _z, 0, (interactiveObject.tileRight - interactiveObject.tileLeft) + 1,
									(Model) interactiveObject.renderable, _x,
									(interactiveObject.tileBottom - interactiveObject.tileTop) + 1);
								((Model) interactiveObject.renderable).handleShading(i, j, 0, k);
							}
						}

						FloorDecoration floorDecoration = tile.floorDecoration;
						if (floorDecoration != null && floorDecoration.renderable.verticesNormal != null)
						{
							method273(_x, (Model) floorDecoration.renderable, _y, _z, 0);
							((Model) floorDecoration.renderable).handleShading(i, j, 0, k);
						}
					}
				}

			}

		}

	}

	private void method273(int x, Model model, int y, int z, int l)
	{
		if (l != 0)
		{
			return;
		}
		if (x < mapSizeX)
		{
			SceneTile sceneTile = tileArray.getTile(z, x + 1, y);
			if (sceneTile != null && sceneTile.floorDecoration != null
				&& sceneTile.floorDecoration.renderable.verticesNormal != null)
			{
				mergeNormals(model,
					(Model) sceneTile.floorDecoration.renderable, 128, 0, 0, true);
			}
		}
		if (y < mapSizeX)
		{
			SceneTile sceneTile = tileArray.getTile(z, x, y + 1);
			if (sceneTile != null && sceneTile.floorDecoration != null
				&& sceneTile.floorDecoration.renderable.verticesNormal != null)
			{
				mergeNormals(model,
					(Model) sceneTile.floorDecoration.renderable, 0, 0, 128, true);
			}
		}
		if (x < mapSizeX && y < mapSizeY)
		{
			SceneTile sceneTile = tileArray.getTile(z, x + 1, y + 1);
			if (sceneTile != null && sceneTile.floorDecoration != null
				&& sceneTile.floorDecoration.renderable.verticesNormal != null)
			{
				mergeNormals(model,
					(Model) sceneTile.floorDecoration.renderable, 128, 0, 128, true);
			}
		}
		if (x < mapSizeX && y > 0)
		{
			SceneTile sceneTile = tileArray.getTile(z, x + 1, y - 1);
			if (sceneTile != null && sceneTile.floorDecoration != null
				&& sceneTile.floorDecoration.renderable.verticesNormal != null)
			{
				mergeNormals(model,
					(Model) sceneTile.floorDecoration.renderable, 128, 0, -128,
					true);
			}
		}
	}

	private void method274(int i, int j, int k, int l, Model class50_sub1_sub4_sub4, int i1, int j1)
	{
		boolean flag = true;
		int k1 = i1;
		int l1 = i1 + l;
		int i2 = i - 1;
		int j2 = i + j1;
		for (int z = j; z <= j + 1; z++)
		{
			if (z != mapSizeZ)
			{
				for (int x = k1; x <= l1; x++)
				{
					if (x >= 0 && x < mapSizeX)
					{
						for (int y = i2; y <= j2; y++)
						{
							if (y >= 0 && y < mapSizeY && (!flag || x >= l1 || y >= j2 || y < i && x != i1))
							{
								SceneTile class50_sub3 = tileArray.getTile(z, x, y);
								if (class50_sub3 != null)
								{
									int j3 = (heightMap[z][x][y]
										+ heightMap[z][x + 1][y]
										+ heightMap[z][x][y + 1] + heightMap[z][x + 1][y + 1])
										/ 4
										- (heightMap[j][i1][i]
										+ heightMap[j][i1 + 1][i]
										+ heightMap[j][i1][i + 1] + heightMap[j][i1 + 1][i + 1])
										/ 4;
									Wall wall = class50_sub3.wall;
									if (wall != null && wall.primary != null
										&& wall.primary.verticesNormal != null)
									{
										mergeNormals(class50_sub1_sub4_sub4,
											(Model) wall.primary, (x - i1)
												* 128 + (1 - l) * 64, j3, (y - i) * 128 + (1 - j1) * 64, flag);
									}
									if (wall != null && wall.secondary != null
										&& wall.secondary.verticesNormal != null)
									{
										mergeNormals(class50_sub1_sub4_sub4,
											(Model) wall.secondary, (x - i1)
												* 128 + (1 - l) * 64, j3, (y - i) * 128 + (1 - j1) * 64, flag);
									}
									for (int k3 = 0; k3 < class50_sub3.entityCount; k3++)
									{
										InteractiveObject interactiveObject = class50_sub3.interactiveObjects[k3];
										if (interactiveObject != null && interactiveObject.renderable != null
											&& interactiveObject.renderable.verticesNormal != null)
										{
											int l3 = (interactiveObject.tileRight - interactiveObject.tileLeft) + 1;
											int i4 = (interactiveObject.tileBottom - interactiveObject.tileTop) + 1;
											mergeNormals(class50_sub1_sub4_sub4,
												(Model) interactiveObject.renderable,
												(interactiveObject.tileLeft - i1) * 128 + (l3 - l) * 64, j3,
												(interactiveObject.tileTop - i) * 128 + (i4 - j1) * 64, flag);
										}
									}

								}
							}
						}

					}
				}

				k1--;
				flag = false;
			}
		}

		if (k == 0)
		{
		}
	}

	private void mergeNormals(Model modelA,
								Model modelB, int i, int j, int k, boolean flag)
	{
		anInt503++;
		int count = 0;
		int[] vertices = modelB.verticesX;
		int vertexCount = modelB.vertexCount;
		int minX = modelB.worldX >> 16;
		int maxX = (modelB.worldX << 16) >> 16;
		int maxZ = modelB.worldZ >> 16;
		int minZ = (modelB.worldZ << 16) >> 16;
		for (int vertex = 0; vertex < modelA.vertexCount; vertex++)
		{
			VertexNormal vertexNormal = modelA.verticesNormal[vertex];
			VertexNormal offsetVertexNormal = modelA.vertexNormalOffset[vertex];
			if (offsetVertexNormal.magnitude != 0)
			{
				int y = modelA.verticesY[vertex] - j;
				if (y <= modelB.maxY)
				{
					int x = modelA.verticesX[vertex] - i;
					if (x >= minX && x <= maxX)
					{
						int z = modelA.verticesZ[vertex] - k;
						if (z >= minZ && z <= maxZ)
						{
							for (int v = 0; v < vertexCount; v++)
							{
								VertexNormal class40_2 = modelB.verticesNormal[v];
								VertexNormal class40_3 = modelB.vertexNormalOffset[v];
								if (x == vertices[v] && z == modelB.verticesZ[v]
									&& y == modelB.verticesY[v] && class40_3.magnitude != 0)
								{
									vertexNormal.x += class40_3.x;
									vertexNormal.y += class40_3.y;
									vertexNormal.z += class40_3.z;
									vertexNormal.magnitude += class40_3.magnitude;
									class40_2.x += offsetVertexNormal.x;
									class40_2.y += offsetVertexNormal.y;
									class40_2.z += offsetVertexNormal.z;
									class40_2.magnitude += offsetVertexNormal.magnitude;
									count++;
									anIntArray486[vertex] = anInt503;
									anIntArray487[v] = anInt503;
								}
							}

						}
					}
				}
			}
		}

		if (count < 3 || !flag)
		{
			return;
		}
		for (int k2 = 0; k2 < modelA.triangleCount; k2++)
		{
			if (anIntArray486[modelA.trianglePointsX[k2]] == anInt503
				&& anIntArray486[modelA.trianglePointsY[k2]] == anInt503
				&& anIntArray486[modelA.trianglePointsZ[k2]] == anInt503)
			{
				modelA.triangleDrawType[k2] = -1;
			}
		}

		for (int l2 = 0; l2 < modelB.triangleCount; l2++)
		{
			if (anIntArray487[modelB.trianglePointsX[l2]] == anInt503
				&& anIntArray487[modelB.trianglePointsY[l2]] == anInt503
				&& anIntArray487[modelB.trianglePointsZ[l2]] == anInt503)
			{
				modelB.triangleDrawType[l2] = -1;
			}
		}

	}

	public void renderMinimapTile(int[] pixels, int pixelPointer, int j, int z, int x, int y)
	{
		SceneTile sceneTile = tileArray.getTile(z, x, y);
		if (sceneTile == null)
		{
			return;
		}
		GenericTile genericTile = sceneTile.plainTile;
		if (genericTile != null)
		{
			int tileRGB = genericTile.rgbColor;
			if (tileRGB == 0)
			{
				return;
			}
//            if ((tileRGB & 0xFF0000) >> 16 >= 160) {
//                System.out.println("FOUND RED!!!");
//                System.out.println(genericTile.flat);
//            }
			for (int k1 = 0; k1 < 4; k1++)
			{
				pixels[pixelPointer] = tileRGB;
				pixels[pixelPointer + 1] = tileRGB;
				pixels[pixelPointer + 2] = tileRGB;
				pixels[pixelPointer + 3] = tileRGB;
				pixelPointer += j;
			}

			return;
		}
		ComplexTile complexTile = sceneTile.shapedTile;
		if (complexTile == null)
		{
			return;
		}
		int shapeA = complexTile.shape;
		int shapeB = complexTile.rotation;
		int underlayRGB = complexTile.underlayRGB;
		int overlayRGB = complexTile.overlayRGB;
		int[] shapePoints = tileShapePoints[shapeA];
		int[] shapeIndices = tileShapeIndices[shapeB];
		int shapePtr = 0;
		if (underlayRGB != 0)
		{
			for (int linePtr = 0; linePtr < 4; linePtr++)
			{
				pixels[pixelPointer] = shapePoints[shapeIndices[shapePtr++]] != 0 ? overlayRGB : underlayRGB;
				pixels[pixelPointer + 1] = shapePoints[shapeIndices[shapePtr++]] != 0 ? overlayRGB : underlayRGB;
				pixels[pixelPointer + 2] = shapePoints[shapeIndices[shapePtr++]] != 0 ? overlayRGB : underlayRGB;
				pixels[pixelPointer + 3] = shapePoints[shapeIndices[shapePtr++]] != 0 ? overlayRGB : underlayRGB;
				pixelPointer += j;
			}

			return;
		}
		for (int linePtr = 0; linePtr < 4; linePtr++)
		{
			if (shapePoints[shapeIndices[shapePtr++]] != 0)
			{
				pixels[pixelPointer] = overlayRGB;
			}
			if (shapePoints[shapeIndices[shapePtr++]] != 0)
			{
				pixels[pixelPointer + 1] = overlayRGB;
			}
			if (shapePoints[shapeIndices[shapePtr++]] != 0)
			{
				pixels[pixelPointer + 2] = overlayRGB;
			}
			if (shapePoints[shapeIndices[shapePtr++]] != 0)
			{
				pixels[pixelPointer + 3] = overlayRGB;
			}
			pixelPointer += j;
		}

	}

	public void method279(int i, int j, int k)
	{
		clicked = true;
		clickX = j;
		clickY = k;
		clickedTileX = -1;
		if (i != 0)
		{
		}
		else
		{
			clickedTileY = -1;
		}
	}

	public void render(int cameraPosX, int j, int k, int l, int cameraPosY, int curveX, int curveY)
	{
		if (cameraPosX < 0)
		{
			cameraPosX = 0;
		}
		else if (cameraPosX >= mapSizeX * 128)
		{
			cameraPosX = mapSizeX * 128 - 1;
		}
		if (cameraPosY < 0)
		{
			cameraPosY = 0;
		}
		else if (cameraPosY >= mapSizeY * 128)
		{
			cameraPosY = mapSizeY * 128 - 1;
		}
		cycle++;
		curveSineY = Model.SINE[curveY];
		curveCosineY = Model.COSINE[curveY];
		curveSineX = Model.SINE[curveX];
		curveCosineX = Model.COSINE[curveX];
		TILE_VISIBILITY_MAP = TILE_VISIBILITY_MAPS[(curveY - 128) / 32][curveX / 64];
		Scene.cameraPosX = cameraPosX;
		cameraPosZ = l;
		Scene.cameraPosY = cameraPosY;
		cameraPositionTileX = cameraPosX / 128;
		cameraPositionTileY = cameraPosY / 128;
		plane = j;
		currentPositionX = cameraPositionTileX - 25;
		if (k != 0)
		{
			return;
		}
		if (currentPositionX < 0)
		{
			currentPositionX = 0;
		}
		currentPositionY = cameraPositionTileY - 25;
		if (currentPositionY < 0)
		{
			currentPositionY = 0;
		}
		mapBoundsX = cameraPositionTileX + 25;
		if (mapBoundsX > mapSizeX)
		{
			mapBoundsX = mapSizeX;
		}
		mapBoundsY = cameraPositionTileY + 25;
		if (mapBoundsY > mapSizeY)
		{
			mapBoundsY = mapSizeY;
		}
		processCulling();
		anInt461 = 0;
		for (int z = currentPositionZ; z < mapSizeZ; z++)
		{
			for (int x = currentPositionX; x < mapBoundsX; x++)
			{
				for (int y = currentPositionY; y < mapBoundsY; y++)
				{
					SceneTile tile = tileArray.getTile(z, x, y);
					if (tile != null)
					{
						if (tile.logicHeight > j
							|| !TILE_VISIBILITY_MAP[(x - cameraPositionTileX) + 25][(y - cameraPositionTileY) + 25]
							&& heightMap[z][x][y] - l < 2000)
						{
							tile.draw = false;
							tile.visible = false;
							tile.wallCullDirection = 0;
						}
						else
						{
							tile.draw = true;
							tile.visible = true;
							tile.drawEntities = tile.entityCount > 0;
							anInt461++;
						}
					}
				}

			}

		}

		for (int z = currentPositionZ; z < mapSizeZ; z++)
		{
			for (int offsetX = -25; offsetX <= 0; offsetX++)
			{
				int x = cameraPositionTileX + offsetX;
				int x2 = cameraPositionTileX - offsetX;
				if (x >= currentPositionX || x2 < mapBoundsX)
				{
					for (int offsetY = -25; offsetY <= 0; offsetY++)
					{
						int y = cameraPositionTileY + offsetY;
						int y2 = cameraPositionTileY - offsetY;
						if (x >= currentPositionX)
						{
							if (y >= currentPositionY)
							{
								SceneTile sceneTile = tileArray.getTile(z, x, y);
								if (sceneTile != null && sceneTile.draw)
								{
									renderTile(sceneTile, true);
								}
							}
							if (y2 < mapBoundsY)
							{
								SceneTile sceneTile = tileArray.getTile(z, x, y2);
								if (sceneTile != null && sceneTile.draw)
								{
									renderTile(sceneTile, true);
								}
							}
						}
						if (x2 < mapBoundsX)
						{
							if (y >= currentPositionY)
							{
								SceneTile sceneTile = tileArray.getTile(z, x2, y);
								if (sceneTile != null && sceneTile.draw)
								{
									renderTile(sceneTile, true);
								}
							}
							if (y2 < mapBoundsY)
							{
								SceneTile sceneTile = tileArray.getTile(z, x2, y2);
								if (sceneTile != null && sceneTile.draw)
								{
									renderTile(sceneTile, true);
								}
							}
						}
						if (anInt461 == 0)
						{
							clicked = false;
							return;
						}
					}

				}
			}

		}

		for (int z = currentPositionZ; z < mapSizeZ; z++)
		{
			for (int offsetX = -25; offsetX <= 0; offsetX++)
			{
				int x = cameraPositionTileX + offsetX;
				int x2 = cameraPositionTileX - offsetX;
				if (x >= currentPositionX || x2 < mapBoundsX)
				{
					for (int offsetY = -25; offsetY <= 0; offsetY++)
					{
						int y = cameraPositionTileY + offsetY;
						int y2 = cameraPositionTileY - offsetY;
						if (x >= currentPositionX)
						{
							if (y >= currentPositionY)
							{
								SceneTile tile = tileArray.getTile(z, x, y);
								if (tile != null && tile.draw)
								{
									renderTile(tile, false);
								}
							}
							if (y2 < mapBoundsY)
							{
								SceneTile tile = tileArray.getTile(z, x, y2);
								if (tile != null && tile.draw)
								{
									renderTile(tile, false);
								}
							}
						}
						if (x2 < mapBoundsX)
						{
							if (y >= currentPositionY)
							{
								SceneTile tile = tileArray.getTile(z, x2, y);
								if (tile != null && tile.draw)
								{
									renderTile(tile, false);
								}
							}
							if (y2 < mapBoundsY)
							{
								SceneTile tile = tileArray.getTile(z, x2, y2);
								if (tile != null && tile.draw)
								{
									renderTile(tile, false);
								}
							}
						}
						if (anInt461 == 0)
						{
							clicked = false;
							return;
						}
					}

				}
			}

		}

		clicked = false;
	}

	private void renderTile(SceneTile _tile, boolean flag)
	{
		tileList.pushBack(_tile);
		do
		{
			SceneTile groundTile;
			do
			{
				groundTile = (SceneTile) tileList.pop();
				if (groundTile == null)
				{
					return;
				}
			} while (!groundTile.visible);
			int x = groundTile.x;
			int y = groundTile.y;
			int z = groundTile.z;
			int level = groundTile.renderLevel;
			if (groundTile.draw)
			{
				if (flag)
				{
					if (z > 0)
					{
						SceneTile tile = tileArray.getTile(z - 1, x, y);
						if (tile != null && tile.visible)
						{
							continue;
						}
					}
					if (x <= cameraPositionTileX && x > currentPositionX)
					{
						SceneTile tile = tileArray.getTile(z, x - 1, y);
						if (tile != null && tile.visible
							&& (tile.draw || (groundTile.interactiveObjectsSizeOR & 1) == 0))
						{
							continue;
						}
					}
					if (x >= cameraPositionTileX && x < mapBoundsX - 1)
					{
						SceneTile tile = tileArray.getTile(z, x + 1, y);
						if (tile != null && tile.visible
							&& (tile.draw || (groundTile.interactiveObjectsSizeOR & 4) == 0))
						{
							continue;
						}
					}
					if (y <= cameraPositionTileY && y > currentPositionY)
					{
						SceneTile tile = tileArray.getTile(z, x, y - 1);
						if (tile != null && tile.visible
							&& (tile.draw || (groundTile.interactiveObjectsSizeOR & 8) == 0))
						{
							continue;
						}
					}
					if (y >= cameraPositionTileY && y < mapBoundsY - 1)
					{
						SceneTile tile = tileArray.getTile(z, x, y + 1);
						if (tile != null && tile.visible
							&& (tile.draw || (groundTile.interactiveObjectsSizeOR & 2) == 0))
						{
							continue;
						}
					}
				}
				else
				{
					flag = true;
				}
				groundTile.draw = false;
				if (groundTile.tileBelow != null)
				{
					SceneTile tile = groundTile.tileBelow;
					if (tile.plainTile != null)
					{
						if (!isTileOccluded(x, y, 0))
						{
							renderPlainTile(tile.plainTile, x, y, 0, curveSineX, curveCosineX, curveSineY, curveCosineY);
						}
					}
					else if (tile.shapedTile != null && !isTileOccluded(x, y, 0))
					{
						renderShapedTile(tile.shapedTile, x, y, curveSineX, curveCosineX, curveSineY, curveCosineY);
					}
					Wall wall = tile.wall;
					if (wall != null)
					{
						wall.primary.renderAtPoint(0, curveSineY, curveCosineY, curveSineX, curveCosineX,
							wall.x - cameraPosX, wall.z - cameraPosZ, wall.y - cameraPosY,
							wall.uid);
					}
					for (int i2 = 0; i2 < tile.entityCount; i2++)
					{
						InteractiveObject interactiveObject = tile.interactiveObjects[i2];
						if (interactiveObject != null)
						{
							interactiveObject.renderable.renderAtPoint(interactiveObject.rotation, curveSineY, curveCosineY, curveSineX,
								curveCosineX, interactiveObject.worldX - cameraPosX, interactiveObject.worldZ - cameraPosZ, interactiveObject.worldY
									- cameraPosY, interactiveObject.uid);
						}
					}

				}
				boolean flag1 = false;
				if (groundTile.plainTile != null)
				{
					if (!isTileOccluded(x, y, level))
					{
						flag1 = true;
						renderPlainTile(groundTile.plainTile, x, y, level, curveSineX, curveCosineX, curveSineY, curveCosineY);
					}
				}
				else if (groundTile.shapedTile != null && !isTileOccluded(x, y, level))
				{
					flag1 = true;
					renderShapedTile(groundTile.shapedTile, x, y, curveSineX, curveCosineX, curveSineY, curveCosineY);
				}
				int j1 = 0;
				int j2 = 0;
				Wall wallObject = groundTile.wall;
				WallDecoration wallDecoration = groundTile.wallDecoration;
				if (wallObject != null || wallDecoration != null)
				{
					if (cameraPositionTileX == x)
					{
						j1++;
					}
					else if (cameraPositionTileX < x)
					{
						j1 += 2;
					}
					if (cameraPositionTileY == y)
					{
						j1 += 3;
					}
					else if (cameraPositionTileY > y)
					{
						j1 += 6;
					}
					j2 = anIntArray493[j1];
					groundTile.wallDrawFlags = TILE_WALL_DRAW_FLAGS_1[j1];
				}
				if (wallObject != null)
				{
					if ((wallObject.orientation & anIntArray494[j1]) != 0)
					{
						if (wallObject.orientation == 16)
						{
							groundTile.wallCullDirection = 3;
							groundTile.wallUncullDirection = WALL_UNCULL_FLAGS_0[j1];
							groundTile.wallCullOppositeDirection = 3 - groundTile.wallUncullDirection;
						}
						else if (wallObject.orientation == 32)
						{
							groundTile.wallCullDirection = 6;
							groundTile.wallUncullDirection = anIntArray497[j1];
							groundTile.wallCullOppositeDirection = 6 - groundTile.wallUncullDirection;
						}
						else if (wallObject.orientation == 64)
						{
							groundTile.wallCullDirection = 12;
							groundTile.wallUncullDirection = anIntArray498[j1];
							groundTile.wallCullOppositeDirection = 12 - groundTile.wallUncullDirection;
						}
						else
						{
							groundTile.wallCullDirection = 9;
							groundTile.wallUncullDirection = anIntArray499[j1];
							groundTile.wallCullOppositeDirection = 9 - groundTile.wallUncullDirection;
						}
					}
					else
					{
						groundTile.wallCullDirection = 0;
					}
					if ((wallObject.orientation & j2) != 0 && !isWallOccluded(x, y, level, wallObject.orientation))
					{
						wallObject.primary.renderAtPoint(0, curveSineY, curveCosineY, curveSineX, curveCosineX,
							wallObject.x - cameraPosX, wallObject.z - cameraPosZ, wallObject.y
								- cameraPosY, wallObject.uid);
					}
					if ((wallObject.orientation2 & j2) != 0 && !isWallOccluded(x, y, level, wallObject.orientation2))
					{
						wallObject.secondary.renderAtPoint(0, curveSineY, curveCosineY, curveSineX, curveCosineX,
							wallObject.x - cameraPosX, wallObject.z - cameraPosZ, wallObject.y
								- cameraPosY, wallObject.uid);
					}
				}
				if (wallDecoration != null && !isOccluded(level, x, y, wallDecoration.renderable.modelHeight))
				{
					if ((wallDecoration.configBits & j2) != 0)
					{
						wallDecoration.renderable.renderAtPoint(wallDecoration.face, curveSineY, curveCosineY, curveSineX,
							curveCosineX, wallDecoration.x - cameraPosX, wallDecoration.z - cameraPosZ,
							wallDecoration.y - cameraPosY, wallDecoration.uid);
					}
					else if ((wallDecoration.configBits & 0x300) != 0)
					{
						int j4 = wallDecoration.x - cameraPosX;
						int l5 = wallDecoration.z - cameraPosZ;
						int k6 = wallDecoration.y - cameraPosY;
						int i8 = wallDecoration.face;
						int k9;
						if (i8 == 1 || i8 == 2)
						{
							k9 = -j4;
						}
						else
						{
							k9 = j4;
						}
						int k10;
						if (i8 == 2 || i8 == 3)
						{
							k10 = -k6;
						}
						else
						{
							k10 = k6;
						}
						if ((wallDecoration.configBits & 0x100) != 0 && k10 < k9)
						{
							int i11 = j4 + faceOffsetX2[i8];
							int k11 = k6 + faceOffsetY2[i8];
							wallDecoration.renderable.renderAtPoint(i8 * 512 + 256, curveSineY, curveCosineY, curveSineX,
								curveCosineX, i11, l5, k11, wallDecoration.uid);
						}
						if ((wallDecoration.configBits & 0x200) != 0 && k10 > k9)
						{
							int j11 = j4 + faceOffsetX3[i8];
							int l11 = k6 + faceOffsetY3[i8];
							wallDecoration.renderable.renderAtPoint(i8 * 512 + 1280 & 0x7ff, curveSineY, curveCosineY,
								curveSineX, curveCosineX, j11, l5, l11, wallDecoration.uid);
						}
					}
				}
				if (flag1)
				{
					FloorDecoration floorDecoration = groundTile.floorDecoration;
					if (floorDecoration != null)
					{
						floorDecoration.renderable.renderAtPoint(0, curveSineY, curveCosineY, curveSineX, curveCosineX,
							floorDecoration.x - cameraPosX, floorDecoration.z - cameraPosZ, floorDecoration.y - cameraPosY,
							floorDecoration.uid);
					}
					GroundItemTile groundItemTile_1 = groundTile.groundItemTile;
					if (groundItemTile_1 != null && groundItemTile_1.anInt180 == 0)
					{
						if (groundItemTile_1.secondGroundItem != null)
						{
							groundItemTile_1.secondGroundItem.renderAtPoint(0, curveSineY, curveCosineY, curveSineX, curveCosineX,
								groundItemTile_1.x - cameraPosX, groundItemTile_1.z - cameraPosZ, groundItemTile_1.y
									- cameraPosY, groundItemTile_1.uid);
						}
						if (groundItemTile_1.thirdGroundItem != null)
						{
							groundItemTile_1.thirdGroundItem.renderAtPoint(0, curveSineY, curveCosineY, curveSineX, curveCosineX,
								groundItemTile_1.x - cameraPosX, groundItemTile_1.z - cameraPosZ, groundItemTile_1.y
									- cameraPosY, groundItemTile_1.uid);
						}
						if (groundItemTile_1.firstGroundItem != null)
						{
							groundItemTile_1.firstGroundItem.renderAtPoint(0, curveSineY, curveCosineY, curveSineX, curveCosineX,
								groundItemTile_1.x - cameraPosX, groundItemTile_1.z - cameraPosZ, groundItemTile_1.y
									- cameraPosY, groundItemTile_1.uid);
						}
					}
				}
				int k4 = groundTile.interactiveObjectsSizeOR;
				if (k4 != 0)
				{
					if (x < cameraPositionTileX && (k4 & 4) != 0)
					{
						SceneTile tile = tileArray.getTile(z, x + 1, y);
						if (tile != null && tile.visible)
						{
							tileList.pushBack(tile);
						}
					}
					if (y < cameraPositionTileY && (k4 & 2) != 0)
					{
						SceneTile tile = tileArray.getTile(z, x, y + 1);
						if (tile != null && tile.visible)
						{
							tileList.pushBack(tile);
						}
					}
					if (x > cameraPositionTileX && (k4 & 1) != 0)
					{
						SceneTile tile = tileArray.getTile(z, x - 1, y);
						if (tile != null && tile.visible)
						{
							tileList.pushBack(tile);
						}
					}
					if (y > cameraPositionTileY && (k4 & 8) != 0)
					{
						SceneTile tile = tileArray.getTile(z, x, y - 1);
						if (tile != null && tile.visible)
						{
							tileList.pushBack(tile);
						}
					}
				}
			}
			if (groundTile.wallCullDirection != 0)
			{
				boolean flag2 = true;
				for (int e = 0; e < groundTile.entityCount; e++)
				{
					if (groundTile.interactiveObjects[e].cycle == cycle
						|| (groundTile.sceneSpawnRequestsSize[e] & groundTile.wallCullDirection) != groundTile.wallUncullDirection)
					{
						continue;
					}
					flag2 = false;
					break;
				}

				if (flag2)
				{
					Wall wall_1 = groundTile.wall;
					if (!isWallOccluded(x, y, level, wall_1.orientation))
					{
						wall_1.primary.renderAtPoint(0, curveSineY, curveCosineY, curveSineX, curveCosineX,
							wall_1.x - cameraPosX, wall_1.z - cameraPosZ, wall_1.y
								- cameraPosY, wall_1.uid);
					}
					groundTile.wallCullDirection = 0;
				}
			}
			if (groundTile.drawEntities)
			{
				try
				{
					int entityCount = groundTile.entityCount;
					groundTile.drawEntities = false;
					int l1 = 0;
					label0:
					for (int e = 0; e < entityCount; e++)
					{
						InteractiveObject entity = groundTile.interactiveObjects[e];
						if (entity.cycle == cycle)
						{
							continue;
						}
						for (int _x = entity.tileLeft; _x <= entity.tileRight; _x++)
						{
							for (int _y = entity.tileTop; _y <= entity.tileBottom; _y++)
							{
								SceneTile tile = tileArray.getTile(z, _x, _y);
								if (tile.draw)
								{
									groundTile.drawEntities = true;
								}
								else
								{
									if (tile.wallCullDirection == 0)
									{
										continue;
									}
									int l6 = 0;
									if (_x > entity.tileLeft)
									{
										l6++;
									}
									if (_x < entity.tileRight)
									{
										l6 += 4;
									}
									if (_y > entity.tileTop)
									{
										l6 += 8;
									}
									if (_y < entity.tileBottom)
									{
										l6 += 2;
									}
									if ((l6 & tile.wallCullDirection) != groundTile.wallCullOppositeDirection)
									{
										continue;
									}
									groundTile.drawEntities = true;
								}
								continue label0;
							}

						}

						interactiveObjects[l1++] = entity;
						int i5 = cameraPositionTileX - entity.tileLeft;
						int i6 = entity.tileRight - cameraPositionTileX;
						if (i6 > i5)
						{
							i5 = i6;
						}
						int i7 = cameraPositionTileY - entity.tileTop;
						int j8 = entity.tileBottom - cameraPositionTileY;
						if (j8 > i7)
						{
							entity.anInt123 = i5 + j8;
						}
						else
						{
							entity.anInt123 = i5 + i7;
						}
					}

					while (l1 > 0)
					{
						int i3 = -50;
						int l3 = -1;
						for (int j5 = 0; j5 < l1; j5++)
						{
							InteractiveObject entity = interactiveObjects[j5];
							if (entity.cycle != cycle)
							{
								if (entity.anInt123 > i3)
								{
									i3 = entity.anInt123;
									l3 = j5;
								}
								else if (entity.anInt123 == i3)
								{
									int j7 = entity.worldX - cameraPosX;
									int k8 = entity.worldY - cameraPosY;
									int l9 = interactiveObjects[l3].worldX - cameraPosX;
									int l10 = interactiveObjects[l3].worldY - cameraPosY;
									if (j7 * j7 + k8 * k8 > l9 * l9 + l10 * l10)
									{
										l3 = j5;
									}
								}
							}
						}

						if (l3 == -1)
						{
							break;
						}
						InteractiveObject entity = interactiveObjects[l3];
						entity.cycle = cycle;
						if (!isAreaOccluded(entity.tileLeft, entity.tileRight, entity.tileTop, entity.tileBottom, level,
							entity.renderable.modelHeight))
						{
							entity.renderable.renderAtPoint(entity.rotation, curveSineY, curveCosineY, curveSineX,
								curveCosineX, entity.worldX - cameraPosX, entity.worldZ - cameraPosZ,
								entity.worldY - cameraPosY, entity.uid);
						}
						for (int _x = entity.tileLeft; _x <= entity.tileRight; _x++)
						{
							for (int _y = entity.tileTop; _y <= entity.tileBottom; _y++)
							{
								SceneTile tile = tileArray.getTile(z, _x, _y);
								if (tile.wallCullDirection != 0)
								{
									tileList.pushBack(tile);
								}
								else if ((_x != x || _y != y) && tile.visible)
								{
									tileList.pushBack(tile);
								}
							}

						}

					}
					if (groundTile.drawEntities)
					{
						continue;
					}
				}
				catch (Exception _ex)
				{
					groundTile.drawEntities = false;
				}
			}
			if (!groundTile.visible || groundTile.wallCullDirection != 0)
			{
				continue;
			}
			if (x <= cameraPositionTileX && x > currentPositionX)
			{
				SceneTile tile = tileArray.getTile(z, x - 1, y);
				if (tile != null && tile.visible)
				{
					continue;
				}
			}
			if (x >= cameraPositionTileX && x < mapBoundsX - 1)
			{
				SceneTile tile = tileArray.getTile(z, x + 1, y);
				if (tile != null && tile.visible)
				{
					continue;
				}
			}
			if (y <= cameraPositionTileY && y > currentPositionY)
			{
				SceneTile tile = tileArray.getTile(z, x, y - 1);
				if (tile != null && tile.visible)
				{
					continue;
				}
			}
			if (y >= cameraPositionTileY && y < mapBoundsY - 1)
			{
				SceneTile tile = tileArray.getTile(z, x, y + 1);
				if (tile != null && tile.visible)
				{
					continue;
				}
			}
			groundTile.visible = false;
			anInt461--;
			GroundItemTile groundItemTile = groundTile.groundItemTile;
			if (groundItemTile != null && groundItemTile.anInt180 != 0)
			{
				if (groundItemTile.secondGroundItem != null)
				{
					groundItemTile.secondGroundItem.renderAtPoint(0, curveSineY, curveCosineY, curveSineX, curveCosineX,
						groundItemTile.x - cameraPosX, groundItemTile.z - cameraPosZ - groundItemTile.anInt180,
						groundItemTile.y - cameraPosY, groundItemTile.uid);
				}
				if (groundItemTile.thirdGroundItem != null)
				{
					groundItemTile.thirdGroundItem.renderAtPoint(0, curveSineY, curveCosineY, curveSineX, curveCosineX,
						groundItemTile.x - cameraPosX, groundItemTile.z - cameraPosZ - groundItemTile.anInt180,
						groundItemTile.y - cameraPosY, groundItemTile.uid);
				}
				if (groundItemTile.firstGroundItem != null)
				{
					groundItemTile.firstGroundItem.renderAtPoint(0, curveSineY, curveCosineY, curveSineX, curveCosineX,
						groundItemTile.x - cameraPosX, groundItemTile.z - cameraPosZ - groundItemTile.anInt180,
						groundItemTile.y - cameraPosY, groundItemTile.uid);
				}
			}
			if (groundTile.wallDrawFlags != 0)
			{
				WallDecoration wallDecoration = groundTile.wallDecoration;
				if (wallDecoration != null && !isOccluded(level, x, y, wallDecoration.renderable.modelHeight))
				{
					if ((wallDecoration.configBits & groundTile.wallDrawFlags) != 0)
					{
						wallDecoration.renderable.renderAtPoint(wallDecoration.face, curveSineY, curveCosineY, curveSineX,
							curveCosineX, wallDecoration.x - cameraPosX, wallDecoration.z - cameraPosZ, wallDecoration.y
								- cameraPosY, wallDecoration.uid);
					}
					else if ((wallDecoration.configBits & 0x300) != 0)
					{
						int l2 = wallDecoration.x - cameraPosX;
						int j3 = wallDecoration.z - cameraPosZ;
						int i4 = wallDecoration.y - cameraPosY;
						int k5 = wallDecoration.face;
						int j6;
						if (k5 == 1 || k5 == 2)
						{
							j6 = -l2;
						}
						else
						{
							j6 = l2;
						}
						int l7;
						if (k5 == 2 || k5 == 3)
						{
							l7 = -i4;
						}
						else
						{
							l7 = i4;
						}
						if ((wallDecoration.configBits & 0x100) != 0 && l7 >= j6)
						{
							int i9 = l2 + faceOffsetX2[k5];
							int i10 = i4 + faceOffsetY2[k5];
							wallDecoration.renderable.renderAtPoint(k5 * 512 + 256, curveSineY, curveCosineY, curveSineX,
								curveCosineX, i9, j3, i10, wallDecoration.uid);
						}
						if ((wallDecoration.configBits & 0x200) != 0 && l7 <= j6)
						{
							int j9 = l2 + faceOffsetX3[k5];
							int j10 = i4 + faceOffsetY3[k5];
							wallDecoration.renderable.renderAtPoint(k5 * 512 + 1280 & 0x7ff, curveSineY, curveCosineY,
								curveSineX, curveCosineX, j9, j3, j10, wallDecoration.uid);
						}
					}
				}
				Wall wallObject = groundTile.wall;
				if (wallObject != null)
				{
					if ((wallObject.orientation2 & groundTile.wallDrawFlags) != 0 && !isWallOccluded(x, y, level, wallObject.orientation2))
					{
						wallObject.secondary.renderAtPoint(0, curveSineY, curveCosineY, curveSineX, curveCosineX,
							wallObject.x - cameraPosX, wallObject.z - cameraPosZ, wallObject.y
								- cameraPosY, wallObject.uid);
					}
					if ((wallObject.orientation & groundTile.wallDrawFlags) != 0 && !isWallOccluded(x, y, level, wallObject.orientation))
					{
						wallObject.primary.renderAtPoint(0, curveSineY, curveCosineY, curveSineX, curveCosineX,
							wallObject.x - cameraPosX, wallObject.z - cameraPosZ, wallObject.y
								- cameraPosY, wallObject.uid);
					}
				}
			}
			if (z < mapSizeZ - 1)
			{
				SceneTile tile = tileArray.getTile(z + 1, x, y);
				if (tile != null && tile.visible)
				{
					tileList.pushBack(tile);
				}
			}
			if (x < cameraPositionTileX)
			{
				SceneTile tile = tileArray.getTile(z, x + 1, y);
				if (tile != null && tile.visible)
				{
					tileList.pushBack(tile);
				}
			}
			if (y < cameraPositionTileY)
			{
				SceneTile tile = tileArray.getTile(z, x, y + 1);
				if (tile != null && tile.visible)
				{
					tileList.pushBack(tile);
				}
			}
			if (x > cameraPositionTileX)
			{
				SceneTile tile = tileArray.getTile(z, x - 1, y);
				if (tile != null && tile.visible)
				{
					tileList.pushBack(tile);
				}
			}
			if (y > cameraPositionTileY)
			{
				SceneTile tile = tileArray.getTile(z, x, y - 1);
				if (tile != null && tile.visible)
				{
					tileList.pushBack(tile);
				}
			}
		} while (true);
	}

	private void renderPlainTile(GenericTile plainTile, int tileX, int tileY, int tileZ, int sinX, int cosineX, int sinY, int cosineY)
	{
		int xC;
		int xA = xC = (tileX << 7) - cameraPosX;
		int yB;
		int yA = yB = (tileY << 7) - cameraPosY;
		int xD;
		int xB = xD = xA + 128;
		int yC;
		int yD = yC = yA + 128;
		int zA = heightMap[tileZ][tileX][tileY] - cameraPosZ;
		int zB = heightMap[tileZ][tileX + 1][tileY] - cameraPosZ;
		int zC = heightMap[tileZ][tileX + 1][tileY + 1] - cameraPosZ;
		int zD = heightMap[tileZ][tileX][tileY + 1] - cameraPosZ;
		int temp = yA * sinX + xA * cosineX >> 16;
		yA = yA * cosineX - xA * sinX >> 16;
		xA = temp;
		temp = zA * cosineY - yA * sinY >> 16;
		yA = zA * sinY + yA * cosineY >> 16;
		zA = temp;
		if (yA < 50)
		{
			return;
		}
		temp = yB * sinX + xB * cosineX >> 16;
		yB = yB * cosineX - xB * sinX >> 16;
		xB = temp;
		temp = zB * cosineY - yB * sinY >> 16;
		yB = zB * sinY + yB * cosineY >> 16;
		zB = temp;
		if (yB < 50)
		{
			return;
		}
		temp = yD * sinX + xD * cosineX >> 16;
		yD = yD * cosineX - xD * sinX >> 16;
		xD = temp;
		temp = zC * cosineY - yD * sinY >> 16;
		yD = zC * sinY + yD * cosineY >> 16;
		zC = temp;
		if (yD < 50)
		{
			return;
		}
		temp = yC * sinX + xC * cosineX >> 16;
		yC = yC * cosineX - xC * sinX >> 16;
		xC = temp;
		temp = zD * cosineY - yC * sinY >> 16;
		yC = zD * sinY + yC * cosineY >> 16;
		zD = temp;
		if (yC < 50)
		{
			return;
		}
		int screenXA = Rasterizer3D.center_x + (xA << 9) / yA;
		int screenYA = Rasterizer3D.center_y + (zA << 9) / yA;
		int screenXB = Rasterizer3D.center_x + (xB << 9) / yB;
		int screenYB = Rasterizer3D.center_y + (zB << 9) / yB;
		int screenXD = Rasterizer3D.center_x + (xD << 9) / yD;
		int screenYD = Rasterizer3D.center_y + (zC << 9) / yD;
		int screenXC = Rasterizer3D.center_x + (xC << 9) / yC;
		int screenYC = Rasterizer3D.center_y + (zD << 9) / yC;
		Rasterizer3D.alpha = 0;
		if ((screenXD - screenXC) * (screenYB - screenYC) - (screenYD - screenYC) * (screenXB - screenXC) > 0)
		{
			Rasterizer3D.restrict_edges = screenXD < 0 || screenXC < 0 || screenXB < 0 ||
				screenXD > Rasterizer.viewportRx ||
				screenXC > Rasterizer.viewportRx ||
				screenXB > Rasterizer.viewportRx;
			if (clicked && isMouseWithinTriangle(clickX, clickY, screenYD, screenYC, screenYB, screenXD, screenXC, screenXB))
			{
				clickedTileX = tileX;
				clickedTileY = tileY;
			}
			if (plainTile.texture == -1)
			{
				if (plainTile.colourD != 0xbc614e)
				{
					Rasterizer3D.drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB, plainTile.colourD, plainTile.colourC,
						plainTile.colourB);
				}
			}
			else if (!lowMemory)
			{
				if (plainTile.flat)
				{
					Rasterizer3D.drawTexturedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB, plainTile.colourD, plainTile.colourC,
						plainTile.colourB, xA, xB, xC, zA, zB, zD, yA, yB, yC, plainTile.texture);
				}
				else
				{
					Rasterizer3D.drawTexturedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB, plainTile.colourD, plainTile.colourC,
						plainTile.colourB, xD, xC, xB, zC, zD, zB, yD, yC, yB, plainTile.texture);
				}
			}
			else
			{
				int rgb = textureRGB[plainTile.texture];
				Rasterizer3D.drawShadedTriangle(screenYD, screenYC, screenYB, screenXD, screenXC, screenXB, mixColours(rgb, plainTile.colourD), mixColours(
					rgb, plainTile.colourC), mixColours(rgb, plainTile.colourB));
			}
		}
		if ((screenXA - screenXB) * (screenYC - screenYB) - (screenYA - screenYB) * (screenXC - screenXB) > 0)
		{
			Rasterizer3D.restrict_edges = screenXA < 0 || screenXB < 0 || screenXC < 0 || screenXA > Rasterizer.viewportRx || screenXB > Rasterizer.viewportRx
				|| screenXC > Rasterizer.viewportRx;
			if (clicked && isMouseWithinTriangle(clickX, clickY, screenYA, screenYB, screenYC, screenXA, screenXB, screenXC))
			{
				clickedTileX = tileX;
				clickedTileY = tileY;
			}
			if (plainTile.texture == -1)
			{
				if (plainTile.colourA != 0xbc614e)
				{
					Rasterizer3D.drawShadedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC, plainTile.colourA, plainTile.colourB,
						plainTile.colourC);
				}
			}
			else
			{
				if (!lowMemory)
				{
					Rasterizer3D.drawTexturedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC, plainTile.colourA, plainTile.colourB,
						plainTile.colourC, xA, xB, xC, zA, zB, zD, yA, yB, yC, plainTile.texture);
					return;
				}
				int rgb = textureRGB[plainTile.texture];
				Rasterizer3D.drawShadedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC, mixColours(rgb, plainTile.colourA), mixColours(
					rgb, plainTile.colourB), mixColours(rgb, plainTile.colourC));
			}
		}
	}

	private void renderShapedTile(ComplexTile shapedTile, int tileX, int tileY, int sineX, int cosineX, int sineY, int cosineY)
	{
		int triangleCount = shapedTile.originalVertexX.length;
		for (int triangle = 0; triangle < triangleCount; triangle++)
		{
			int viewspaceX = shapedTile.originalVertexX[triangle] - cameraPosX;
			int viewspaceY = shapedTile.originalVertexY[triangle] - cameraPosZ;
			int viewspaceZ = shapedTile.originalVertexZ[triangle] - cameraPosY;
			int temp = viewspaceZ * sineX + viewspaceX * cosineX >> 16;
			viewspaceZ = viewspaceZ * cosineX - viewspaceX * sineX >> 16;
			viewspaceX = temp;
			temp = viewspaceY * cosineY - viewspaceZ * sineY >> 16;
			viewspaceZ = viewspaceY * sineY + viewspaceZ * cosineY >> 16;
			viewspaceY = temp;
			if (viewspaceZ < 50)
			{
				return;
			}
			if (shapedTile.triangleTexture != null)
			{
				ComplexTile.viewspaceX[triangle] = viewspaceX;
				ComplexTile.viewspaceY[triangle] = viewspaceY;
				ComplexTile.viewspaceZ[triangle] = viewspaceZ;
			}
			ComplexTile.screenX[triangle] = Rasterizer3D.center_x + (viewspaceX << 9) / viewspaceZ;
			ComplexTile.screenY[triangle] = Rasterizer3D.center_y + (viewspaceY << 9) / viewspaceZ;
		}

		Rasterizer3D.alpha = 0;
		triangleCount = shapedTile.triangleA.length;
		for (int tirangle = 0; tirangle < triangleCount; tirangle++)
		{
			int a = shapedTile.triangleA[tirangle];
			int b = shapedTile.triangleB[tirangle];
			int c = shapedTile.triangleC[tirangle];
			int screenXA = ComplexTile.screenX[a];
			int screenXB = ComplexTile.screenX[b];
			int screenXC = ComplexTile.screenX[c];
			int screenYA = ComplexTile.screenY[a];
			int screenYB = ComplexTile.screenY[b];
			int screenYC = ComplexTile.screenY[c];
			if ((screenXA - screenXB) * (screenYC - screenYB) - (screenYA - screenYB) * (screenXC - screenXB) > 0)
			{
				Rasterizer3D.restrict_edges = screenXA < 0 || screenXB < 0 || screenXC < 0 || screenXA > Rasterizer.viewportRx || screenXB > Rasterizer.viewportRx
					|| screenXC > Rasterizer.viewportRx;
				if (clicked && isMouseWithinTriangle(clickX, clickY, screenYA, screenYB, screenYC, screenXA, screenXB, screenXC))
				{
					clickedTileX = tileX;
					clickedTileY = tileY;
				}
				if (shapedTile.triangleTexture == null || shapedTile.triangleTexture[tirangle] == -1)
				{
					if (shapedTile.triangleHSLA[tirangle] != 0xbc614e)
					{
						Rasterizer3D.drawShadedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC, shapedTile.triangleHSLA[tirangle],
							shapedTile.triangleHSLB[tirangle], shapedTile.triangleHSLC[tirangle]);
					}
				}
				else if (!lowMemory)
				{
					if (shapedTile.flat)
					{
						Rasterizer3D.drawTexturedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC, shapedTile.triangleHSLA[tirangle],
							shapedTile.triangleHSLB[tirangle], shapedTile.triangleHSLC[tirangle], ComplexTile.viewspaceX[0],
							ComplexTile.viewspaceX[1], ComplexTile.viewspaceX[3], ComplexTile.viewspaceY[0],
							ComplexTile.viewspaceY[1], ComplexTile.viewspaceY[3], ComplexTile.viewspaceZ[0],
							ComplexTile.viewspaceZ[1], ComplexTile.viewspaceZ[3], shapedTile.triangleTexture[tirangle]);
					}
					else
					{
						Rasterizer3D.drawTexturedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC, shapedTile.triangleHSLA[tirangle],
							shapedTile.triangleHSLB[tirangle], shapedTile.triangleHSLC[tirangle], ComplexTile.viewspaceX[a],
							ComplexTile.viewspaceX[b], ComplexTile.viewspaceX[c], ComplexTile.viewspaceY[a],
							ComplexTile.viewspaceY[b], ComplexTile.viewspaceY[c], ComplexTile.viewspaceZ[a],
							ComplexTile.viewspaceZ[b], ComplexTile.viewspaceZ[c], shapedTile.triangleTexture[tirangle]);
					}
				}
				else
				{
					int k5 = textureRGB[shapedTile.triangleTexture[tirangle]];
					Rasterizer3D.drawShadedTriangle(screenYA, screenYB, screenYC, screenXA, screenXB, screenXC,
						mixColours(k5, shapedTile.triangleHSLA[tirangle]), mixColours(k5, shapedTile.triangleHSLB[tirangle]),
						mixColours(k5, shapedTile.triangleHSLC[tirangle]));
				}
			}
		}

	}

	private int mixColours(int colourA, int colourB)
	{
		colourB = 127 - colourB;
		colourB = (colourB * (colourA & 0x7f)) / 160;
		if (colourB < 2)
		{
			colourB = 2;
		}
		else if (colourB > 126)
		{
			colourB = 126;
		}
		return (colourA & 0xff80) + colourB;
	}

	private boolean isMouseWithinTriangle(int mouseX, int mouseY, int pointAY, int pointBY, int pointCY, int pointAX, int pointBX, int pointCX)
	{
		if (mouseY < pointAY && mouseY < pointBY && mouseY < pointCY)
		{
			return false;
		}
		if (mouseY > pointAY && mouseY > pointBY && mouseY > pointCY)
		{
			return false;
		}
		if (mouseX < pointAX && mouseX < pointBX && mouseX < pointCX)
		{
			return false;
		}
		if (mouseX > pointAX && mouseX > pointBX && mouseX > pointCX)
		{
			return false;
		}
		int b1 = (mouseY - pointAY) * (pointBX - pointAX) - (mouseX - pointAX) * (pointBY - pointAY);
		int b2 = (mouseY - pointCY) * (pointAX - pointCX) - (mouseX - pointCX) * (pointAY - pointCY);
		int b3 = (mouseY - pointBY) * (pointCX - pointBX) - (mouseX - pointBX) * (pointCY - pointBY);
		return b1 * b3 > 0 && b3 * b2 > 0;
	}

	private void processCulling()
	{
		int clusterCount = cullingClusterPointer[plane];
		SceneCluster[] clusters = cullingClusters[plane];
		processedCullingClustersPointer = 0;
		for (int c = 0; c < clusterCount; c++)
		{
			SceneCluster cluster = clusters[c];
			if (cluster.searchMask == 1)
			{
				int distanceFromCameraStartX = (cluster.tileStartX - cameraPositionTileX) + 25;
				if (distanceFromCameraStartX < 0 || distanceFromCameraStartX > 50)
				{
					continue;
				}
				int distanceFromCameraStartY = (cluster.tileStartY - cameraPositionTileY) + 25;
				if (distanceFromCameraStartY < 0)
				{
					distanceFromCameraStartY = 0;
				}
				int cameraPositionTileY = (cluster.tileEndY - Scene.cameraPositionTileY) + 25;
				if (cameraPositionTileY > 50)
				{
					cameraPositionTileY = 50;
				}
				boolean visible = false;
				while (distanceFromCameraStartY <= cameraPositionTileY)
				{
					if (TILE_VISIBILITY_MAP[distanceFromCameraStartX][distanceFromCameraStartY++])
					{
						visible = true;
						break;
					}
				}
				if (!visible)
				{
					continue;
				}
				int realDistanceFromCameraStartX = cameraPosX - cluster.worldStartX;
				if (realDistanceFromCameraStartX > 32)
				{
					cluster.tileDistanceEnum = 1;
				}
				else
				{
					if (realDistanceFromCameraStartX >= -32)
					{
						continue;
					}
					cluster.tileDistanceEnum = 2;
					realDistanceFromCameraStartX = -realDistanceFromCameraStartX;
				}
				cluster.worldDistanceFromCameraStartY = (cluster.worldStartY - cameraPosY << 8) / realDistanceFromCameraStartX;
				cluster.worldDistanceFromCameraEndY = (cluster.worldEndY - cameraPosY << 8) / realDistanceFromCameraStartX;
				cluster.worldDistanceFromCameraStartZ = (cluster.worldEndZ - cameraPosZ << 8) / realDistanceFromCameraStartX;
				cluster.worldDistanceFromCameraEndZ = (cluster.worldStartZ - cameraPosZ << 8) / realDistanceFromCameraStartX;
				processedCullingClusters[processedCullingClustersPointer++] = cluster;
				continue;
			}
			if (cluster.searchMask == 2)
			{
				int distanceFromCameraStartY = (cluster.tileStartY - cameraPositionTileY) + 25;
				if (distanceFromCameraStartY < 0 || distanceFromCameraStartY > 50)
				{
					continue;
				}
				int distanceFromCameraStartX = (cluster.tileStartX - cameraPositionTileX) + 25;
				if (distanceFromCameraStartX < 0)
				{
					distanceFromCameraStartX = 0;
				}
				int distanceFromCameraEndX = (cluster.tileEndX - cameraPositionTileX) + 25;
				if (distanceFromCameraEndX > 50)
				{
					distanceFromCameraEndX = 50;
				}
				boolean visible = false;
				while (distanceFromCameraStartX <= distanceFromCameraEndX)
				{
					if (TILE_VISIBILITY_MAP[distanceFromCameraStartX++][distanceFromCameraStartY])
					{
						visible = true;
						break;
					}
				}
				if (!visible)
				{
					continue;
				}
				int realDistanceFromCameraStartY = cameraPosY - cluster.worldStartY;
				if (realDistanceFromCameraStartY > 32)
				{
					cluster.tileDistanceEnum = 3;
				}
				else
				{
					if (realDistanceFromCameraStartY >= -32)
					{
						continue;
					}
					cluster.tileDistanceEnum = 4;
					realDistanceFromCameraStartY = -realDistanceFromCameraStartY;
				}
				cluster.worldDistanceFromCameraStartX = (cluster.worldStartX - cameraPosX << 8) / realDistanceFromCameraStartY;
				cluster.worldDistanceFromCameraEndX = (cluster.worldEndX - cameraPosX << 8) / realDistanceFromCameraStartY;
				cluster.worldDistanceFromCameraStartZ = (cluster.worldEndZ - cameraPosZ << 8) / realDistanceFromCameraStartY;
				cluster.worldDistanceFromCameraEndZ = (cluster.worldStartZ - cameraPosZ << 8) / realDistanceFromCameraStartY;
				processedCullingClusters[processedCullingClustersPointer++] = cluster;
			}
			else if (cluster.searchMask == 4)
			{
				int realDistanceFromCameraStartZ = cluster.worldEndZ - cameraPosZ;
				if (realDistanceFromCameraStartZ > 128)
				{
					int distanceFromCameraStartY = (cluster.tileStartY - cameraPositionTileY) + 25;
					if (distanceFromCameraStartY < 0)
					{
						distanceFromCameraStartY = 0;
					}
					int distanceFromCameraEndY = (cluster.tileEndY - cameraPositionTileY) + 25;
					if (distanceFromCameraEndY > 50)
					{
						distanceFromCameraEndY = 50;
					}
					if (distanceFromCameraStartY <= distanceFromCameraEndY)
					{
						int distanceFromCameraStartX = (cluster.tileStartX - cameraPositionTileX) + 25;
						if (distanceFromCameraStartX < 0)
						{
							distanceFromCameraStartX = 0;
						}
						int distanceFromCameraEndX = (cluster.tileEndX - cameraPositionTileX) + 25;
						if (distanceFromCameraEndX > 50)
						{
							distanceFromCameraEndX = 50;
						}
						boolean visible = false;
						label0:
						for (int x = distanceFromCameraStartX; x <= distanceFromCameraEndX; x++)
						{
							for (int y = distanceFromCameraStartY; y <= distanceFromCameraEndY; y++)
							{
								if (!TILE_VISIBILITY_MAP[x][y])
								{
									continue;
								}
								visible = true;
								break label0;
							}

						}

						if (visible)
						{
							cluster.tileDistanceEnum = 5;
							cluster.worldDistanceFromCameraStartX = (cluster.worldStartX - cameraPosX << 8) / realDistanceFromCameraStartZ;
							cluster.worldDistanceFromCameraEndX = (cluster.worldEndX - cameraPosX << 8) / realDistanceFromCameraStartZ;
							cluster.worldDistanceFromCameraStartY = (cluster.worldStartY - cameraPosY << 8) / realDistanceFromCameraStartZ;
							cluster.worldDistanceFromCameraEndY = (cluster.worldEndY - cameraPosY << 8) / realDistanceFromCameraStartZ;
							processedCullingClusters[processedCullingClustersPointer++] = cluster;
						}
					}
				}
			}
		}

	}

	private boolean isTileOccluded(int x, int y, int z)
	{
		int l = anIntArrayArrayArray445[z][x][y];
		if (l == -cycle)
		{
			return false;
		}
		if (l == cycle)
		{
			return true;
		}
		int worldX = x << 7;
		int worldY = y << 7;
		if (method291(worldX + 1, worldY + 1, heightMap[z][x][y])
			&& method291((worldX + 128) - 1, worldY + 1, heightMap[z][x + 1][y])
			&& method291((worldX + 128) - 1, (worldY + 128) - 1, heightMap[z][x + 1][y + 1])
			&& method291(worldX + 1, (worldY + 128) - 1, heightMap[z][x][y + 1]))
		{
			anIntArrayArrayArray445[z][x][y] = cycle;
			return true;
		}
		else
		{
			anIntArrayArrayArray445[z][x][y] = -cycle;
			return false;
		}
	}

	private boolean isWallOccluded(int x, int y, int level, int wallType)
	{
		if (!isTileOccluded(x, y, level))
		{
			return false;
		}
		int posX = x << 7;
		int posY = y << 7;
		int posZ = heightMap[level][x][y] - 1;
		int z1 = posZ - 120;
		int z2 = posZ - 230;
		int z3 = posZ - 238;
		if (wallType < 16)
		{
			if (wallType == 1)
			{
				if (posX > cameraPosX)
				{
					if (!method291(posX, posY, posZ))
					{
						return false;
					}
					if (!method291(posX, posY + 128, posZ))
					{
						return false;
					}
				}
				if (level > 0)
				{
					if (!method291(posX, posY, z1))
					{
						return false;
					}
					if (!method291(posX, posY + 128, z1))
					{
						return false;
					}
				}
				if (!method291(posX, posY, z2))
				{
					return false;
				}
				return method291(posX, posY + 128, z2);
			}
			if (wallType == 2)
			{
				if (posY < cameraPosY)
				{
					if (!method291(posX, posY + 128, posZ))
					{
						return false;
					}
					if (!method291(posX + 128, posY + 128, posZ))
					{
						return false;
					}
				}
				if (level > 0)
				{
					if (!method291(posX, posY + 128, z1))
					{
						return false;
					}
					if (!method291(posX + 128, posY + 128, z1))
					{
						return false;
					}
				}
				if (!method291(posX, posY + 128, z2))
				{
					return false;
				}
				return method291(posX + 128, posY + 128, z2);
			}
			if (wallType == 4)
			{
				if (posX < cameraPosX)
				{
					if (!method291(posX + 128, posY, posZ))
					{
						return false;
					}
					if (!method291(posX + 128, posY + 128, posZ))
					{
						return false;
					}
				}
				if (level > 0)
				{
					if (!method291(posX + 128, posY, z1))
					{
						return false;
					}
					if (!method291(posX + 128, posY + 128, z1))
					{
						return false;
					}
				}
				if (!method291(posX + 128, posY, z2))
				{
					return false;
				}
				return method291(posX + 128, posY + 128, z2);
			}
			if (wallType == 8)
			{
				if (posY > cameraPosY)
				{
					if (!method291(posX, posY, posZ))
					{
						return false;
					}
					if (!method291(posX + 128, posY, posZ))
					{
						return false;
					}
				}
				if (level > 0)
				{
					if (!method291(posX, posY, z1))
					{
						return false;
					}
					if (!method291(posX + 128, posY, z1))
					{
						return false;
					}
				}
				if (!method291(posX, posY, z2))
				{
					return false;
				}
				return method291(posX + 128, posY, z2);
			}
		}
		if (!method291(posX + 64, posY + 64, z3))
		{
			return false;
		}
		if (wallType == 16)
		{
			return method291(posX, posY + 128, z2);
		}
		if (wallType == 32)
		{
			return method291(posX + 128, posY + 128, z2);
		}
		if (wallType == 64)
		{
			return method291(posX + 128, posY, z2);
		}
		if (wallType == 128)
		{
			return method291(posX, posY, z2);
		}
		else
		{
			System.out.println("Warning unsupported wall type");
			return true;
		}
	}

	private boolean isOccluded(int i, int j, int k, int l)
	{
		if (!isTileOccluded(j, k, i))
		{
			return false;
		}
		int i1 = j << 7;
		int j1 = k << 7;
		return method291(i1 + 1, j1 + 1, heightMap[i][j][k] - l)
			&& method291((i1 + 128) - 1, j1 + 1, heightMap[i][j + 1][k] - l)
			&& method291((i1 + 128) - 1, (j1 + 128) - 1, heightMap[i][j + 1][k + 1] - l)
			&& method291(i1 + 1, (j1 + 128) - 1, heightMap[i][j][k + 1] - l);
	}

	private boolean isAreaOccluded(int minimumX, int maximumX, int minimumY, int maximumY, int z, int offsetZ)
	{
		if (minimumX == maximumX && minimumY == maximumY)
		{
			if (!isTileOccluded(minimumX, minimumY, z))
			{
				return false;
			}
			int _x = minimumX << 7;
			int _y = minimumY << 7;
			return method291(_x + 1, _y + 1, heightMap[z][minimumX][minimumY] - offsetZ)
				&& method291((_x + 128) - 1, _y + 1, heightMap[z][minimumX + 1][minimumY] - offsetZ)
				&& method291((_x + 128) - 1, (_y + 128) - 1, heightMap[z][minimumX + 1][minimumY + 1] - offsetZ)
				&& method291(_x + 1, (_y + 128) - 1, heightMap[z][minimumX][minimumY + 1] - offsetZ);
		}
		for (int x = minimumX; x <= maximumX; x++)
		{
			for (int y = minimumY; y <= maximumY; y++)
			{
				if (anIntArrayArrayArray445[z][x][y] == -cycle)
				{
					return false;
				}
			}

		}

		int _x = (minimumX << 7) + 1;
		int _y = (minimumY << 7) + 2;
		int _z = heightMap[z][minimumX][minimumY] - offsetZ;
		if (!method291(_x, _y, _z))
		{
			return false;
		}
		int j3 = (maximumX << 7) - 1;
		if (!method291(j3, _y, _z))
		{
			return false;
		}
		int k3 = (maximumY << 7) - 1;
		if (!method291(_x, k3, _z))
		{
			return false;
		}
		return method291(j3, k3, _z);
	}

	private boolean method291(int posX, int posY, int posZ)
	{
		for (int c = 0; c < processedCullingClustersPointer; c++)
		{
			SceneCluster cluster = processedCullingClusters[c];
			if (cluster.tileDistanceEnum == 1)
			{
				int i1 = cluster.worldStartX - posX;
				if (i1 > 0)
				{
					int j2 = cluster.worldStartY + (cluster.worldDistanceFromCameraStartY * i1 >> 8);
					int k3 = cluster.worldEndY + (cluster.worldDistanceFromCameraEndY * i1 >> 8);
					int l4 = cluster.worldEndZ + (cluster.worldDistanceFromCameraStartZ * i1 >> 8);
					int i6 = cluster.worldStartZ + (cluster.worldDistanceFromCameraEndZ * i1 >> 8);
					if (posY >= j2 && posY <= k3 && posZ >= l4 && posZ <= i6)
					{
						return true;
					}
				}
			}
			else if (cluster.tileDistanceEnum == 2)
			{
				int j1 = posX - cluster.worldStartX;
				if (j1 > 0)
				{
					int k2 = cluster.worldStartY + (cluster.worldDistanceFromCameraStartY * j1 >> 8);
					int l3 = cluster.worldEndY + (cluster.worldDistanceFromCameraEndY * j1 >> 8);
					int i5 = cluster.worldEndZ + (cluster.worldDistanceFromCameraStartZ * j1 >> 8);
					int j6 = cluster.worldStartZ + (cluster.worldDistanceFromCameraEndZ * j1 >> 8);
					if (posY >= k2 && posY <= l3 && posZ >= i5 && posZ <= j6)
					{
						return true;
					}
				}
			}
			else if (cluster.tileDistanceEnum == 3)
			{
				int k1 = cluster.worldStartY - posY;
				if (k1 > 0)
				{
					int l2 = cluster.worldStartX + (cluster.worldDistanceFromCameraStartX * k1 >> 8);
					int i4 = cluster.worldEndX + (cluster.worldDistanceFromCameraEndX * k1 >> 8);
					int j5 = cluster.worldEndZ + (cluster.worldDistanceFromCameraStartZ * k1 >> 8);
					int k6 = cluster.worldStartZ + (cluster.worldDistanceFromCameraEndZ * k1 >> 8);
					if (posX >= l2 && posX <= i4 && posZ >= j5 && posZ <= k6)
					{
						return true;
					}
				}
			}
			else if (cluster.tileDistanceEnum == 4)
			{
				int l1 = posY - cluster.worldStartY;
				if (l1 > 0)
				{
					int i3 = cluster.worldStartX + (cluster.worldDistanceFromCameraStartX * l1 >> 8);
					int j4 = cluster.worldEndX + (cluster.worldDistanceFromCameraEndX * l1 >> 8);
					int k5 = cluster.worldEndZ + (cluster.worldDistanceFromCameraStartZ * l1 >> 8);
					int l6 = cluster.worldStartZ + (cluster.worldDistanceFromCameraEndZ * l1 >> 8);
					if (posX >= i3 && posX <= j4 && posZ >= k5 && posZ <= l6)
					{
						return true;
					}
				}
			}
			else if (cluster.tileDistanceEnum == 5)
			{
				int i2 = posZ - cluster.worldEndZ;
				if (i2 > 0)
				{
					int j3 = cluster.worldStartX + (cluster.worldDistanceFromCameraStartX * i2 >> 8);
					int k4 = cluster.worldEndX + (cluster.worldDistanceFromCameraEndX * i2 >> 8);
					int l5 = cluster.worldStartY + (cluster.worldDistanceFromCameraStartY * i2 >> 8);
					int i7 = cluster.worldEndY + (cluster.worldDistanceFromCameraEndY * i2 >> 8);
					if (posX >= j3 && posX <= k4 && posY >= l5 && posY <= i7)
					{
						return true;
					}
				}
			}
		}

		return false;
	}

}
//TODO:Needs more refactoring
