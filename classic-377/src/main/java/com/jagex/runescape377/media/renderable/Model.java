package com.jagex.runescape377.media.renderable;

import com.jagex.runescape377.media.Animation;
import com.jagex.runescape377.media.Rasterizer;
import com.jagex.runescape377.media.Rasterizer3D;
import com.jagex.runescape377.media.Skins;
import com.jagex.runescape377.media.VertexNormal;
import com.jagex.runescape377.net.Buffer;
import com.jagex.runescape377.net.requester.Requester;

public class Model extends Renderable
{


	public static Model EMPTY_MODEL = new Model();
	public static Requester requester;
	public static boolean gameScreenClickable;
	public static int cursorX;
	public static int cursorY;
	public static int resourceCount;
	public static int[] hoveredHash = new int[1000];
	public static int[] SINE;
	public static int[] COSINE;
	private static int[] anIntArray1644 = new int[2000];
	private static int[] anIntArray1645 = new int[2000];
	private static int[] anIntArray1646 = new int[2000];
	private static int[] anIntArray1647 = new int[2000];
	private static ModelHeader[] modelHeaders;
	private static boolean[] restrictEdges = new boolean[4096];
	private static boolean[] aBooleanArray1685 = new boolean[4096];
	private static int[] vertexScreenX = new int[4096];
	private static int[] vertexScreenY = new int[4096];
	private static int[] vertexScreenZ = new int[4096];
	private static int[] vertexMovedX = new int[4096];
	private static int[] vertexMovedY = new int[4096];
	private static int[] vertexMovedZ = new int[4096];
	private static int[] anIntArray1692 = new int[1500];
	private static int[][] anIntArrayArray1693 = new int[1500][512];
	private static int[] anIntArray1694 = new int[12];
	private static int[][] anIntArrayArray1695 = new int[12][2000];
	private static int[] anIntArray1696 = new int[2000];
	private static int[] anIntArray1697 = new int[2000];
	private static int[] anIntArray1698 = new int[12];
	private static int[] anIntArray1699 = new int[10];
	private static int[] anIntArray1700 = new int[10];
	private static int[] anIntArray1701 = new int[10];
	private static int vertexXModifier;
	private static int vertexYModifier;
	private static int vertexZModifier;
	private static int[] HSLtoRGB;
	private static int[] anIntArray1713;

	static
	{
		SINE = Rasterizer3D.SINE;
		COSINE = Rasterizer3D.COSINE;
		HSLtoRGB = Rasterizer3D.hsl2rgb;
		anIntArray1713 = Rasterizer3D.anIntArray1469;
	}

	public int vertexCount;
	public int[] verticesX;
	public int[] verticesY;
	public int[] verticesZ;
	public int triangleCount;
	public int[] trianglePointsX;
	public int[] trianglePointsY;
	public int[] trianglePointsZ;
	public int[] triangleDrawType;
	public int[] triangleColorValues;
	public int worldX;
	public int worldZ;
	public int diagonal2DAboveOrigin;
	public int maxY;
	public int anInt1675;
	public int[][] vectorSkin;
	public int[][] triangleSkin;
	public boolean singleTile = false;
	public VertexNormal[] vertexNormalOffset;
	private int[] triangleHSLA;
	private int[] triangleHSLB;
	private int[] triangleHSLC;
	private int[] trianglePriorities;
	private int[] triangleAlphaValues;
	private int trianglePriority;
	private int texturedTriangleCount;
	private int[] texturedTrianglePointsX;
	private int[] texturedTrianglePointsY;
	private int[] texturedTrianglePointsZ;
	private int anInt1668;
	private int diagonal3D;
	private int diagonal3DAboveOrigin;
	private int[] vertexSkins;
	private int[] triangleSkinValues;


	public Model()
	{
	}

	public Model(int modelId)
	{
		ModelHeader modelHeader = modelHeaders[modelId];
		vertexCount = modelHeader.vertexCount;
		triangleCount = modelHeader.triangleCount;
		texturedTriangleCount = modelHeader.texturedTriangleCount;
		verticesX = new int[vertexCount];
		verticesY = new int[vertexCount];
		verticesZ = new int[vertexCount];
		trianglePointsX = new int[triangleCount];
		trianglePointsY = new int[triangleCount];
		trianglePointsZ = new int[triangleCount];
		texturedTrianglePointsX = new int[texturedTriangleCount];
		texturedTrianglePointsY = new int[texturedTriangleCount];
		texturedTrianglePointsZ = new int[texturedTriangleCount];
		if (modelHeader.vertexSkinOffset >= 0)
		{
			vertexSkins = new int[vertexCount];
		}
		if (modelHeader.texturePointerOffset >= 0)
		{
			triangleDrawType = new int[triangleCount];
		}
		if (modelHeader.trianglePriorityOffset >= 0)
		{
			trianglePriorities = new int[triangleCount];
		}
		else
		{
			trianglePriority = -modelHeader.trianglePriorityOffset - 1;
		}
		if (modelHeader.triangleAlphaOffset >= 0)
		{
			triangleAlphaValues = new int[triangleCount];
		}
		if (modelHeader.triangleSkinOffset >= 0)
		{
			triangleSkinValues = new int[triangleCount];
		}
		triangleColorValues = new int[triangleCount];
		Buffer vertexDirectionOffsetBuffer = new Buffer(modelHeader.modelData);
		vertexDirectionOffsetBuffer.currentPosition = modelHeader.vertexDirectionOffset;
		Buffer xDataOffsetBuffer = new Buffer(modelHeader.modelData);
		xDataOffsetBuffer.currentPosition = modelHeader.xDataOffset;
		Buffer yDataOffsetBuffer = new Buffer(modelHeader.modelData);
		yDataOffsetBuffer.currentPosition = modelHeader.yDataOffset;
		Buffer zDataOffsetBuffer = new Buffer(modelHeader.modelData);
		zDataOffsetBuffer.currentPosition = modelHeader.zDataOffset;
		Buffer vertexSkinOffsetBuffer = new Buffer(modelHeader.modelData);
		vertexSkinOffsetBuffer.currentPosition = modelHeader.vertexSkinOffset;
		int baseOffsetX = 0;
		int baseOffsetY = 0;
		int baseOffsetz = 0;
		for (int vertex = 0; vertex < vertexCount; vertex++)
		{
			int flag = vertexDirectionOffsetBuffer.getUnsignedByte();
			int currentOffsetX = 0;
			if ((flag & 1) != 0)
			{
				currentOffsetX = xDataOffsetBuffer.getSignedSmart();
			}
			int currentOffsetY = 0;
			if ((flag & 2) != 0)
			{
				currentOffsetY = yDataOffsetBuffer.getSignedSmart();
			}
			int currentOffsetZ = 0;
			if ((flag & 4) != 0)
			{
				currentOffsetZ = zDataOffsetBuffer.getSignedSmart();
			}
			verticesX[vertex] = baseOffsetX + currentOffsetX;
			verticesY[vertex] = baseOffsetY + currentOffsetY;
			verticesZ[vertex] = baseOffsetz + currentOffsetZ;
			baseOffsetX = verticesX[vertex];
			baseOffsetY = verticesY[vertex];
			baseOffsetz = verticesZ[vertex];
			if (vertexSkins != null)
			{
				vertexSkins[vertex] = vertexSkinOffsetBuffer.getUnsignedByte();
			}
		}

		vertexDirectionOffsetBuffer.currentPosition = modelHeader.colorDataOffset;
		xDataOffsetBuffer.currentPosition = modelHeader.texturePointerOffset;
		yDataOffsetBuffer.currentPosition = modelHeader.trianglePriorityOffset;
		zDataOffsetBuffer.currentPosition = modelHeader.triangleAlphaOffset;
		vertexSkinOffsetBuffer.currentPosition = modelHeader.triangleSkinOffset;
		for (int l1 = 0; l1 < triangleCount; l1++)
		{
			triangleColorValues[l1] = vertexDirectionOffsetBuffer.getUnsignedShortBE();
			if (triangleDrawType != null)
			{
				triangleDrawType[l1] = xDataOffsetBuffer.getUnsignedByte();
			}
			if (trianglePriorities != null)
			{
				trianglePriorities[l1] = yDataOffsetBuffer.getUnsignedByte();
			}
			if (triangleAlphaValues != null)
			{
				triangleAlphaValues[l1] = zDataOffsetBuffer.getUnsignedByte();
			}
			if (triangleSkinValues != null)
			{
				triangleSkinValues[l1] = vertexSkinOffsetBuffer.getUnsignedByte();
			}
		}

		vertexDirectionOffsetBuffer.currentPosition = modelHeader.triangleDataOffset;
		xDataOffsetBuffer.currentPosition = modelHeader.triangleTypeOffset;
		int trianglePointOffsetX = 0;
		int trianglePointOffsetY = 0;
		int trianglePointOffsetZ = 0;
		int offset = 0;
		for (int triangle = 0; triangle < triangleCount; triangle++)
		{
			int type = xDataOffsetBuffer.getUnsignedByte();
			if (type == 1)
			{
				trianglePointOffsetX = vertexDirectionOffsetBuffer.getSignedSmart() + offset;
				offset = trianglePointOffsetX;
				trianglePointOffsetY = vertexDirectionOffsetBuffer.getSignedSmart() + offset;
				offset = trianglePointOffsetY;
				trianglePointOffsetZ = vertexDirectionOffsetBuffer.getSignedSmart() + offset;
				offset = trianglePointOffsetZ;
				trianglePointsX[triangle] = trianglePointOffsetX;
				trianglePointsY[triangle] = trianglePointOffsetY;
				trianglePointsZ[triangle] = trianglePointOffsetZ;
			}
			if (type == 2)
			{
				trianglePointOffsetY = trianglePointOffsetZ;
				trianglePointOffsetZ = vertexDirectionOffsetBuffer.getSignedSmart() + offset;
				offset = trianglePointOffsetZ;
				trianglePointsX[triangle] = trianglePointOffsetX;
				trianglePointsY[triangle] = trianglePointOffsetY;
				trianglePointsZ[triangle] = trianglePointOffsetZ;
			}
			if (type == 3)
			{
				trianglePointOffsetX = trianglePointOffsetZ;
				trianglePointOffsetZ = vertexDirectionOffsetBuffer.getSignedSmart() + offset;
				offset = trianglePointOffsetZ;
				trianglePointsX[triangle] = trianglePointOffsetX;
				trianglePointsY[triangle] = trianglePointOffsetY;
				trianglePointsZ[triangle] = trianglePointOffsetZ;
			}
			if (type == 4)
			{
				int oldTrianglePointOffsetX = trianglePointOffsetX;
				trianglePointOffsetX = trianglePointOffsetY;
				trianglePointOffsetY = oldTrianglePointOffsetX;
				trianglePointOffsetZ = vertexDirectionOffsetBuffer.getSignedSmart() + offset;
				offset = trianglePointOffsetZ;
				trianglePointsX[triangle] = trianglePointOffsetX;
				trianglePointsY[triangle] = trianglePointOffsetY;
				trianglePointsZ[triangle] = trianglePointOffsetZ;
			}
		}

		vertexDirectionOffsetBuffer.currentPosition = modelHeader.uvMapTriangleOffset;
		for (int triangle = 0; triangle < texturedTriangleCount; triangle++)
		{
			texturedTrianglePointsX[triangle] = vertexDirectionOffsetBuffer.getUnsignedShortBE();
			texturedTrianglePointsY[triangle] = vertexDirectionOffsetBuffer.getUnsignedShortBE();
			texturedTrianglePointsZ[triangle] = vertexDirectionOffsetBuffer.getUnsignedShortBE();
		}

	}

	public Model(int modelCount, Model[] subModels)
	{
		boolean setDrawType = false;
		boolean setPriority = false;
		boolean setAlpha = false;
		boolean setSkins = false;
		vertexCount = 0;
		triangleCount = 0;
		texturedTriangleCount = 0;
		trianglePriority = -1;
		for (int m = 0; m < modelCount; m++)
		{
			Model model = subModels[m];
			if (model != null)
			{
				vertexCount += model.vertexCount;
				triangleCount += model.triangleCount;
				texturedTriangleCount += model.texturedTriangleCount;
				setDrawType |= model.triangleDrawType != null;
				if (model.trianglePriorities == null)
				{
					if (trianglePriority == -1)
					{
						trianglePriority = model.trianglePriority;
					}
					if (trianglePriority != model.trianglePriority)
					{
						setPriority = true;
					}
				}
				else
				{
					setPriority = true;
				}
				setAlpha |= model.triangleAlphaValues != null;
				setSkins |= model.triangleSkinValues != null;
			}
		}

		verticesX = new int[vertexCount];
		verticesY = new int[vertexCount];
		verticesZ = new int[vertexCount];
		vertexSkins = new int[vertexCount];
		trianglePointsX = new int[triangleCount];
		trianglePointsY = new int[triangleCount];
		trianglePointsZ = new int[triangleCount];
		texturedTrianglePointsX = new int[texturedTriangleCount];
		texturedTrianglePointsY = new int[texturedTriangleCount];
		texturedTrianglePointsZ = new int[texturedTriangleCount];
		if (setDrawType)
		{
			triangleDrawType = new int[triangleCount];
		}
		if (setPriority)
		{
			trianglePriorities = new int[triangleCount];
		}
		if (setAlpha)
		{
			triangleAlphaValues = new int[triangleCount];
		}
		if (setSkins)
		{
			triangleSkinValues = new int[triangleCount];
		}
		triangleColorValues = new int[triangleCount];
		vertexCount = 0;
		triangleCount = 0;
		texturedTriangleCount = 0;
		int count = 0;
		for (int m = 0; m < modelCount; m++)
		{
			Model model = subModels[m];
			if (model != null)
			{
				for (int triangle = 0; triangle < model.triangleCount; triangle++)
				{
					if (setDrawType)
					{
						if (model.triangleDrawType == null)
						{
							triangleDrawType[triangleCount] = 0;
						}
						else
						{
							int drawType = model.triangleDrawType[triangle];
							if ((drawType & 2) == 2)
							{
								drawType += count << 2;
							}
							triangleDrawType[triangleCount] = drawType;
						}
					}
					if (setPriority)
					{
						if (model.trianglePriorities == null)
						{
							trianglePriorities[triangleCount] = model.trianglePriority;
						}
						else
						{
							trianglePriorities[triangleCount] = model.trianglePriorities[triangle];
						}
					}
					if (setAlpha)
					{
						if (model.triangleAlphaValues == null)
						{
							triangleAlphaValues[triangleCount] = 0;
						}
						else
						{
							triangleAlphaValues[triangleCount] = model.triangleAlphaValues[triangle];
						}
					}
					if (setSkins && model.triangleSkinValues != null)
					{
						triangleSkinValues[triangleCount] = model.triangleSkinValues[triangle];
					}
					triangleColorValues[triangleCount] = model.triangleColorValues[triangle];
					trianglePointsX[triangleCount] = getFirstIdenticalVertexIndex(model,
						model.trianglePointsX[triangle]);
					trianglePointsY[triangleCount] = getFirstIdenticalVertexIndex(model,
						model.trianglePointsY[triangle]);
					trianglePointsZ[triangleCount] = getFirstIdenticalVertexIndex(model,
						model.trianglePointsZ[triangle]);
					triangleCount++;
				}

				for (int triangle = 0; triangle < model.texturedTriangleCount; triangle++)
				{
					texturedTrianglePointsX[texturedTriangleCount] = getFirstIdenticalVertexIndex(model,
						model.texturedTrianglePointsX[triangle]);
					texturedTrianglePointsY[texturedTriangleCount] = getFirstIdenticalVertexIndex(model,
						model.texturedTrianglePointsY[triangle]);
					texturedTrianglePointsZ[texturedTriangleCount] = getFirstIdenticalVertexIndex(model,
						model.texturedTrianglePointsZ[triangle]);
					texturedTriangleCount++;
				}

				count += model.texturedTriangleCount;
			}
		}

	}

	public Model(Model[] models)
	{
		final int modelCount = 2;// was parameter
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		boolean flag4 = false;
		vertexCount = 0;
		triangleCount = 0;
		texturedTriangleCount = 0;
		trianglePriority = -1;
		for (int m = 0; m < modelCount; m++)
		{
			Model model = models[m];
			if (model != null)
			{
				vertexCount += model.vertexCount;
				triangleCount += model.triangleCount;
				texturedTriangleCount += model.texturedTriangleCount;
				flag1 |= model.triangleDrawType != null;
				if (model.trianglePriorities != null)
				{
					flag2 = true;
				}
				else
				{
					if (trianglePriority == -1)
					{
						trianglePriority = model.trianglePriority;
					}
					if (trianglePriority != model.trianglePriority)
					{
						flag2 = true;
					}
				}
				flag3 |= model.triangleAlphaValues != null;
				flag4 |= model.triangleColorValues != null;
			}
		}

		verticesX = new int[vertexCount];
		verticesY = new int[vertexCount];
		verticesZ = new int[vertexCount];
		trianglePointsX = new int[triangleCount];
		trianglePointsY = new int[triangleCount];
		trianglePointsZ = new int[triangleCount];
		triangleHSLA = new int[triangleCount];
		triangleHSLB = new int[triangleCount];
		triangleHSLC = new int[triangleCount];
		texturedTrianglePointsX = new int[texturedTriangleCount];
		texturedTrianglePointsY = new int[texturedTriangleCount];
		texturedTrianglePointsZ = new int[texturedTriangleCount];
		if (flag1)
		{
			triangleDrawType = new int[triangleCount];
		}
		if (flag2)
		{
			trianglePriorities = new int[triangleCount];
		}
		if (flag3)
		{
			triangleAlphaValues = new int[triangleCount];
		}
		if (flag4)
		{
			triangleColorValues = new int[triangleCount];
		}
		vertexCount = 0;
		triangleCount = 0;
		texturedTriangleCount = 0;
		int count = 0;
		for (int m = 0; m < modelCount; m++)
		{
			Model model = models[m];
			if (model != null)
			{
				int v = vertexCount;
				for (int vertex = 0; vertex < model.vertexCount; vertex++)
				{
					verticesX[vertexCount] = model.verticesX[vertex];
					verticesY[vertexCount] = model.verticesY[vertex];
					verticesZ[vertexCount] = model.verticesZ[vertex];
					vertexCount++;
				}

				for (int triangle = 0; triangle < model.triangleCount; triangle++)
				{
					trianglePointsX[triangleCount] = model.trianglePointsX[triangle] + v;
					trianglePointsY[triangleCount] = model.trianglePointsY[triangle] + v;
					trianglePointsZ[triangleCount] = model.trianglePointsZ[triangle] + v;
					triangleHSLA[triangleCount] = model.triangleHSLA[triangle];
					triangleHSLB[triangleCount] = model.triangleHSLB[triangle];
					triangleHSLC[triangleCount] = model.triangleHSLC[triangle];
					if (flag1)
					{
						if (model.triangleDrawType == null)
						{
							triangleDrawType[triangleCount] = 0;
						}
						else
						{
							int drawtype = model.triangleDrawType[triangle];
							if ((drawtype & 2) == 2)
							{
								drawtype += count << 2;
							}
							triangleDrawType[triangleCount] = drawtype;
						}
					}
					if (flag2)
					{
						if (model.trianglePriorities == null)
						{
							trianglePriorities[triangleCount] = model.trianglePriority;
						}
						else
						{
							trianglePriorities[triangleCount] = model.trianglePriorities[triangle];
						}
					}
					if (flag3)
					{
						if (model.triangleAlphaValues == null)
						{
							triangleAlphaValues[triangleCount] = 0;
						}
						else
						{
							triangleAlphaValues[triangleCount] = model.triangleAlphaValues[triangle];
						}
					}
					if (flag4 && model.triangleColorValues != null)
					{
						triangleColorValues[triangleCount] = model.triangleColorValues[triangle];
					}
					triangleCount++;
				}

				for (int triangle = 0; triangle < model.texturedTriangleCount; triangle++)
				{
					texturedTrianglePointsX[texturedTriangleCount] = model.texturedTrianglePointsX[triangle] + v;
					texturedTrianglePointsY[texturedTriangleCount] = model.texturedTrianglePointsY[triangle] + v;
					texturedTrianglePointsZ[texturedTriangleCount] = model.texturedTrianglePointsZ[triangle] + v;
					texturedTriangleCount++;
				}

				count += model.texturedTriangleCount;
			}
		}

		calculateDiagonals();
	}

	public Model(boolean flag2,
					Model model, boolean flag3)
	{
		vertexCount = model.vertexCount;
		triangleCount = model.triangleCount;
		texturedTriangleCount = model.texturedTriangleCount;
		verticesX = new int[vertexCount];
		verticesY = new int[vertexCount];
		verticesZ = new int[vertexCount];
		for (int i = 0; i < vertexCount; i++)
		{
			verticesX[i] = model.verticesX[i];
			verticesY[i] = model.verticesY[i];
			verticesZ[i] = model.verticesZ[i];
		}


		if (flag2)
		{
			triangleColorValues = model.triangleColorValues;
		}
		else
		{
			triangleColorValues = new int[triangleCount];
			System.arraycopy(model.triangleColorValues, 0, triangleColorValues, 0, triangleCount);

		}
		if (flag3)
		{
			triangleAlphaValues = model.triangleAlphaValues;
		}
		else
		{
			triangleAlphaValues = new int[triangleCount];
			if (model.triangleAlphaValues == null)
			{
				for (int triangle = 0; triangle < triangleCount; triangle++)
				{
					triangleAlphaValues[triangle] = 0;
				}

			}
			else
			{
				System.arraycopy(model.triangleAlphaValues, 0, triangleAlphaValues, 0, triangleCount);

			}
		}

		vertexSkins = model.vertexSkins;
		triangleSkinValues = model.triangleSkinValues;
		triangleDrawType = model.triangleDrawType;
		trianglePointsX = model.trianglePointsX;
		trianglePointsY = model.trianglePointsY;
		trianglePointsZ = model.trianglePointsZ;
		trianglePriorities = model.trianglePriorities;
		trianglePriority = model.trianglePriority;
		texturedTrianglePointsX = model.texturedTrianglePointsX;
		texturedTrianglePointsY = model.texturedTrianglePointsY;
		texturedTrianglePointsZ = model.texturedTrianglePointsZ;
	}

	public Model(boolean adjustToTerrain, boolean nonFlatShading, Model model)
	{
		vertexCount = model.vertexCount;
		triangleCount = model.triangleCount;
		texturedTriangleCount = model.texturedTriangleCount;
		if (adjustToTerrain)
		{
			verticesY = new int[vertexCount];
			System.arraycopy(model.verticesY, 0, verticesY, 0, vertexCount);

		}
		else
		{
			verticesY = model.verticesY;
		}
		if (nonFlatShading)
		{
			triangleHSLA = new int[triangleCount];
			triangleHSLB = new int[triangleCount];
			triangleHSLC = new int[triangleCount];
			for (int triangle = 0; triangle < triangleCount; triangle++)
			{
				triangleHSLA[triangle] = model.triangleHSLA[triangle];
				triangleHSLB[triangle] = model.triangleHSLB[triangle];
				triangleHSLC[triangle] = model.triangleHSLC[triangle];
			}

			triangleDrawType = new int[triangleCount];
			if (model.triangleDrawType == null)
			{
				for (int triangle = 0; triangle < triangleCount; triangle++)
				{
					triangleDrawType[triangle] = 0;
				}

			}
			else
			{
				System.arraycopy(model.triangleDrawType, 0, triangleDrawType, 0, triangleCount);

			}
			super.verticesNormal = new VertexNormal[vertexCount];
			for (int vertex = 0; vertex < vertexCount; vertex++)
			{
				VertexNormal vertexNormalNew = super.verticesNormal[vertex] = new VertexNormal();
				VertexNormal vertexNormalOld = model.verticesNormal[vertex];
				vertexNormalNew.x = vertexNormalOld.x;
				vertexNormalNew.y = vertexNormalOld.y;
				vertexNormalNew.z = vertexNormalOld.z;
				vertexNormalNew.magnitude = vertexNormalOld.magnitude;
			}

			vertexNormalOffset = model.vertexNormalOffset;
		}
		else
		{
			triangleHSLA = model.triangleHSLA;
			triangleHSLB = model.triangleHSLB;
			triangleHSLC = model.triangleHSLC;
			triangleDrawType = model.triangleDrawType;
		}
		verticesX = model.verticesX;
		verticesZ = model.verticesZ;
		triangleColorValues = model.triangleColorValues;
		triangleAlphaValues = model.triangleAlphaValues;
		trianglePriorities = model.trianglePriorities;
		trianglePriority = model.trianglePriority;
		trianglePointsX = model.trianglePointsX;
		trianglePointsY = model.trianglePointsY;
		trianglePointsZ = model.trianglePointsZ;
		texturedTrianglePointsX = model.texturedTrianglePointsX;
		texturedTrianglePointsY = model.texturedTrianglePointsY;
		texturedTrianglePointsZ = model.texturedTrianglePointsZ;
		super.modelHeight = model.modelHeight;
		maxY = model.maxY;
		diagonal2DAboveOrigin = model.diagonal2DAboveOrigin;
		diagonal3DAboveOrigin = model.diagonal3DAboveOrigin;
		diagonal3D = model.diagonal3D;
		worldX = model.worldX;
		worldZ = model.worldZ;
		anInt1668 = model.anInt1668;
	}

	public static void reset()
	{
		Model.modelHeaders = null;
		Model.restrictEdges = null;
		Model.aBooleanArray1685 = null;
		Model.vertexScreenX = null;
		Model.vertexScreenY = null;
		Model.vertexScreenZ = null;
		Model.vertexMovedX = null;
		Model.vertexMovedY = null;
		Model.vertexMovedZ = null;
		Model.anIntArray1692 = null;
		Model.anIntArrayArray1693 = null;
		Model.anIntArray1694 = null;
		Model.anIntArrayArray1695 = null;
		Model.anIntArray1696 = null;
		Model.anIntArray1697 = null;
		Model.anIntArray1698 = null;
		Model.SINE = null;
		Model.COSINE = null;
		Model.HSLtoRGB = null;
		Model.anIntArray1713 = null;

	}

	public static void init(int modelCount, Requester requester)
	{
		Model.modelHeaders = new ModelHeader[modelCount];
		Model.requester = requester;
	}

	public static void loadModelHeader(byte[] modelData, int modelId)
	{
		if (modelData == null)
		{
			ModelHeader modelHeader = modelHeaders[modelId] = new ModelHeader();
			modelHeader.vertexCount = 0;
			modelHeader.triangleCount = 0;
			modelHeader.texturedTriangleCount = 0;
			return;
		}
		Buffer buffer = new Buffer(modelData);
		buffer.currentPosition = modelData.length - 18;
		ModelHeader modelHeader = modelHeaders[modelId] = new ModelHeader();
		modelHeader.modelData = modelData;
		modelHeader.vertexCount = buffer.getUnsignedShortBE();
		modelHeader.triangleCount = buffer.getUnsignedShortBE();
		modelHeader.texturedTriangleCount = buffer.getUnsignedByte();
		int useTextures = buffer.getUnsignedByte();
		int useTrianglePriority = buffer.getUnsignedByte();
		int useTransparency = buffer.getUnsignedByte();
		int useTriangleSkinning = buffer.getUnsignedByte();
		int useVertexSkinning = buffer.getUnsignedByte();
		int xDataLength = buffer.getUnsignedShortBE();
		int yDataLength = buffer.getUnsignedShortBE();
		int zDataLength = buffer.getUnsignedShortBE();
		int triangleDataLength = buffer.getUnsignedShortBE();
		int offset = 0;
		modelHeader.vertexDirectionOffset = offset;
		offset += modelHeader.vertexCount;
		modelHeader.triangleTypeOffset = offset;
		offset += modelHeader.triangleCount;
		modelHeader.trianglePriorityOffset = offset;
		if (useTrianglePriority == 255)
		{
			offset += modelHeader.triangleCount;
		}
		else
		{
			modelHeader.trianglePriorityOffset = -useTrianglePriority - 1;
		}
		modelHeader.triangleSkinOffset = offset;
		if (useTriangleSkinning == 1)
		{
			offset += modelHeader.triangleCount;
		}
		else
		{
			modelHeader.triangleSkinOffset = -1;
		}
		modelHeader.texturePointerOffset = offset;
		if (useTextures == 1)
		{
			offset += modelHeader.triangleCount;
		}
		else
		{
			modelHeader.texturePointerOffset = -1;
		}
		modelHeader.vertexSkinOffset = offset;
		if (useVertexSkinning == 1)
		{
			offset += modelHeader.vertexCount;
		}
		else
		{
			modelHeader.vertexSkinOffset = -1;
		}
		modelHeader.triangleAlphaOffset = offset;
		if (useTransparency == 1)
		{
			offset += modelHeader.triangleCount;
		}
		else
		{
			modelHeader.triangleAlphaOffset = -1;
		}
		modelHeader.triangleDataOffset = offset;
		offset += triangleDataLength;
		modelHeader.colorDataOffset = offset;
		offset += modelHeader.triangleCount * 2;
		modelHeader.uvMapTriangleOffset = offset;
		offset += modelHeader.texturedTriangleCount * 6;
		modelHeader.xDataOffset = offset;
		offset += xDataLength;
		modelHeader.yDataOffset = offset;
		offset += yDataLength;
		modelHeader.zDataOffset = offset;
		offset += zDataLength;
	}

	public static void resetModel(int model)
	{
		Model.modelHeaders[model] = null;
	}

	public static Model getModel(int model)
	{
		if (Model.modelHeaders == null)
		{
			return null;
		}
		ModelHeader modelHeader = Model.modelHeaders[model];
		if (modelHeader == null)
		{
			Model.requester.requestModel(model);
			return null;
		}
		else
		{
			return new Model(model);
		}
	}

	public static boolean loaded(int id)
	{
		if (Model.modelHeaders == null)
		{
			return false;
		}
		ModelHeader modelHeader = Model.modelHeaders[id];
		if (modelHeader == null)
		{
			Model.requester.requestModel(id);
			return false;
		}
		else
		{
			return true;
		}
	}

	private static int mixLightness(int i, int j, int k)
	{
		if ((k & 2) == 2)
		{
			if (j < 0)
			{
				j = 0;
			}
			else if (j > 127)
			{
				j = 127;
			}
			j = 127 - j;
			return j;
		}
		j = j * (i & 0x7f) >> 7;
		if (j < 2)
		{
			j = 2;
		}
		else if (j > 126)
		{
			j = 126;
		}
		return (i & 0xff80) + j;
	}

	public void replaceWithModel(Model model, boolean replaceAlphaValues)
	{
		vertexCount = model.vertexCount;
		triangleCount = model.triangleCount;
		texturedTriangleCount = model.texturedTriangleCount;
		if (Model.anIntArray1644.length < vertexCount)
		{
			Model.anIntArray1644 = new int[vertexCount + 100];
			Model.anIntArray1645 = new int[vertexCount + 100];
			Model.anIntArray1646 = new int[vertexCount + 100];
		}
		verticesX = Model.anIntArray1644;
		verticesY = Model.anIntArray1645;
		verticesZ = Model.anIntArray1646;
		for (int vertex = 0; vertex < vertexCount; vertex++)
		{
			verticesX[vertex] = model.verticesX[vertex];
			verticesY[vertex] = model.verticesY[vertex];
			verticesZ[vertex] = model.verticesZ[vertex];
		}

		if (replaceAlphaValues)
		{
			triangleAlphaValues = model.triangleAlphaValues;
		}
		else
		{
			if (Model.anIntArray1647.length < triangleCount)
			{
				Model.anIntArray1647 = new int[triangleCount + 100];
			}
			triangleAlphaValues = Model.anIntArray1647;
			if (model.triangleAlphaValues == null)
			{
				for (int triangle = 0; triangle < triangleCount; triangle++)
				{
					triangleAlphaValues[triangle] = 0;
				}

			}
			else
			{
				if (triangleCount >= 0)
				{
					System.arraycopy(model.triangleAlphaValues, 0, triangleAlphaValues, 0, triangleCount);
				}

			}
		}
		triangleDrawType = model.triangleDrawType;
		triangleColorValues = model.triangleColorValues;
		trianglePriorities = model.trianglePriorities;
		trianglePriority = model.trianglePriority;
		triangleSkin = model.triangleSkin;
		vectorSkin = model.vectorSkin;
		trianglePointsX = model.trianglePointsX;
		trianglePointsY = model.trianglePointsY;
		trianglePointsZ = model.trianglePointsZ;
		triangleHSLA = model.triangleHSLA;
		triangleHSLB = model.triangleHSLB;
		triangleHSLC = model.triangleHSLC;
		texturedTrianglePointsX = model.texturedTrianglePointsX;
		texturedTrianglePointsY = model.texturedTrianglePointsY;
		texturedTrianglePointsZ = model.texturedTrianglePointsZ;
	}

	private int getFirstIdenticalVertexIndex(Model model, int vertex)
	{
		int identicalVertexIndex = -1;
		int vertexX = model.verticesX[vertex];
		int vertexY = model.verticesY[vertex];
		int vertexZ = model.verticesZ[vertex];
		for (int index = 0; index < vertexCount; index++)
		{
			if (vertexX != verticesX[index] || vertexY != verticesY[index] || vertexZ != verticesZ[index])
			{
				continue;
			}
			identicalVertexIndex = index;
			break;
		}

		if (identicalVertexIndex == -1)
		{
			verticesX[vertexCount] = vertexX;
			verticesY[vertexCount] = vertexY;
			verticesZ[vertexCount] = vertexZ;
			if (model.vertexSkins != null)
			{
				vertexSkins[vertexCount] = model.vertexSkins[vertex];
			}
			identicalVertexIndex = vertexCount++;
		}
		return identicalVertexIndex;
	}

	public void calculateDiagonals()
	{
		super.modelHeight = 0;
		diagonal2DAboveOrigin = 0;
		maxY = 0;
		for (int vertex = 0; vertex < vertexCount; vertex++)
		{
			int vertexX = verticesX[vertex];
			int vertexY = verticesY[vertex];
			int vertexZ = verticesZ[vertex];
			if (-vertexY > super.modelHeight)
			{
				super.modelHeight = -vertexY;
			}
			if (vertexY > maxY)
			{
				maxY = vertexY;
			}
			int j1 = vertexX * vertexX + vertexZ * vertexZ;
			if (j1 > diagonal2DAboveOrigin)
			{
				diagonal2DAboveOrigin = j1;
			}
		}

		diagonal2DAboveOrigin = (int) (Math.sqrt(diagonal2DAboveOrigin) + 0.98999999999999999D);
		diagonal3DAboveOrigin = (int) (Math.sqrt(diagonal2DAboveOrigin * diagonal2DAboveOrigin + super.modelHeight * super.modelHeight) + 0.98999999999999999D);
		diagonal3D = diagonal3DAboveOrigin + (int) (Math.sqrt(diagonal2DAboveOrigin * diagonal2DAboveOrigin + maxY * maxY) + 0.98999999999999999D);
	}

	public void normalise()
	{
		super.modelHeight = 0;
		maxY = 0;
		for (int j = 0; j < vertexCount; j++)
		{
			int k = verticesY[j];
			if (-k > super.modelHeight)
			{
				super.modelHeight = -k;
			}
			if (k > maxY)
			{
				maxY = k;
			}
		}

		diagonal3DAboveOrigin = (int) (Math.sqrt(diagonal2DAboveOrigin * diagonal2DAboveOrigin + super.modelHeight * super.modelHeight) + 0.98999999999999999D);
		diagonal3D = diagonal3DAboveOrigin + (int) (Math.sqrt(diagonal2DAboveOrigin * diagonal2DAboveOrigin + maxY * maxY) + 0.98999999999999999D);
	}

	private void calculateDiagonalsAndBounds()
	{
		super.modelHeight = 0;
		diagonal2DAboveOrigin = 0;
		maxY = 0;
		int minX = 32767;
		int maxX = -32767;
		int maxZ = -32767;
		int minZ = 32767;
		for (int vertex = 0; vertex < vertexCount; vertex++)
		{
			int x = verticesX[vertex];
			int y = verticesY[vertex];
			int z = verticesZ[vertex];
			if (x < minX)
			{
				minX = x;
			}
			if (x > maxX)
			{
				maxX = x;
			}
			if (z < minZ)
			{
				minZ = z;
			}
			if (z > maxZ)
			{
				maxZ = z;
			}
			if (-y > super.modelHeight)
			{
				super.modelHeight = -y;
			}
			if (y > maxY)
			{
				maxY = y;
			}
			int bounds = x * x + z * z;
			if (bounds > diagonal2DAboveOrigin)
			{
				diagonal2DAboveOrigin = bounds;
			}
		}

		diagonal2DAboveOrigin = (int) Math.sqrt(diagonal2DAboveOrigin);
		diagonal3DAboveOrigin = (int) Math.sqrt(diagonal2DAboveOrigin * diagonal2DAboveOrigin + super.modelHeight * super.modelHeight);
		diagonal3D = diagonal3DAboveOrigin + (int) Math.sqrt(diagonal2DAboveOrigin * diagonal2DAboveOrigin + maxY * maxY);
		worldX = (minX << 16) + (maxX & 0xffff);
		worldZ = (maxZ << 16) + (minZ & 0xffff);
	}

	public void createBones()
	{
		if (vertexSkins != null)
		{
			int[] ai = new int[256];
			int j = 0;
			for (int l = 0; l < vertexCount; l++)
			{
				int j1 = vertexSkins[l];
				ai[j1]++;
				if (j1 > j)
				{
					j = j1;
				}
			}

			vectorSkin = new int[j + 1][];
			for (int k1 = 0; k1 <= j; k1++)
			{
				vectorSkin[k1] = new int[ai[k1]];
				ai[k1] = 0;
			}

			for (int j2 = 0; j2 < vertexCount; j2++)
			{
				int l2 = vertexSkins[j2];
				vectorSkin[l2][ai[l2]++] = j2;
			}

			vertexSkins = null;
		}
		if (triangleSkinValues != null)
		{
			int[] ai1 = new int[256];
			int k = 0;
			for (int i1 = 0; i1 < triangleCount; i1++)
			{
				int l1 = triangleSkinValues[i1];
				ai1[l1]++;
				if (l1 > k)
				{
					k = l1;
				}
			}

			triangleSkin = new int[k + 1][];
			for (int i2 = 0; i2 <= k; i2++)
			{
				triangleSkin[i2] = new int[ai1[i2]];
				ai1[i2] = 0;
			}

			for (int k2 = 0; k2 < triangleCount; k2++)
			{
				int i3 = triangleSkinValues[k2];
				triangleSkin[i3][ai1[i3]++] = k2;
			}

			triangleSkinValues = null;
		}
	}

	public void applyTransform(int frameId)
	{
		if (vectorSkin == null)
		{
			return;
		}
		if (frameId == -1)
		{
			return;
		}
		Animation animation = Animation.getAnimation(frameId);
		if (animation == null)
		{
			return;
		}
		Skins skins = animation.animationSkins;
		vertexXModifier = 0;
		vertexYModifier = 0;
		vertexZModifier = 0;
		for (int stepId = 0; stepId < animation.anInt433; stepId++)
		{
			int opcode = animation.opcodeTable[stepId];
			transformStep(skins.opcodes[opcode], skins.skinList[opcode], animation.modifier1[stepId],
				animation.modifier2[stepId], animation.modifier3[stepId]);
		}

	}

	public void mixAnimationFrames(int i, int j, int k, int[] ai)
	{
		if (k == -1)
		{
			return;
		}
		if (ai == null || i == -1)
		{
			applyTransform(k);
			return;
		}
		Animation animation = Animation.getAnimation(k);
		if (animation == null)
		{
			return;
		}
		Animation animation_1 = Animation.getAnimation(i);
		if (animation_1 == null)
		{
			applyTransform(k);
			return;
		}
		Skins skins = animation.animationSkins;
		vertexXModifier = 0;
		vertexYModifier = 0;
		vertexZModifier = 0;
		int l = 0;
		int i1 = ai[l++];
		for (int j1 = 0; j1 < animation.anInt433; j1++)
		{
			int k1;
			for (k1 = animation.opcodeTable[j1]; k1 > i1; i1 = ai[l++])
			{
				;
			}
			if (k1 != i1 || skins.opcodes[k1] == 0)
			{
				transformStep(skins.opcodes[k1], skins.skinList[k1], animation.modifier1[j1],
					animation.modifier2[j1], animation.modifier3[j1]);
			}
		}

		vertexXModifier = 0;
		vertexYModifier = 0;
		vertexZModifier = 0;
		l = 0;
		i1 = ai[l++];
		for (int l1 = 0; l1 < animation_1.anInt433; l1++)
		{
			int i2;
			for (i2 = animation_1.opcodeTable[l1]; i2 > i1; i1 = ai[l++])
			{
				;
			}
			if (i2 == i1 || skins.opcodes[i2] == 0)
			{
				transformStep(skins.opcodes[i2], skins.skinList[i2], animation_1.modifier1[l1],
					animation_1.modifier2[l1], animation_1.modifier3[l1]);
			}
		}

	}

	private void transformStep(int i, int[] ai, int j, int k, int l)
	{
		int i1 = ai.length;
		if (i == 0)
		{
			int j1 = 0;
			vertexXModifier = 0;
			vertexYModifier = 0;
			vertexZModifier = 0;
			for (int k2 = 0; k2 < i1; k2++)
			{
				int l3 = ai[k2];
				if (l3 < vectorSkin.length)
				{
					int[] ai5 = vectorSkin[l3];
					for (int i5 = 0; i5 < ai5.length; i5++)
					{
						int j6 = ai5[i5];
						vertexXModifier += verticesX[j6];
						vertexYModifier += verticesY[j6];
						vertexZModifier += verticesZ[j6];
						j1++;
					}

				}
			}

			if (j1 > 0)
			{
				vertexXModifier = vertexXModifier / j1 + j;
				vertexYModifier = vertexYModifier / j1 + k;
				vertexZModifier = vertexZModifier / j1 + l;
				return;
			}
			else
			{
				vertexXModifier = j;
				vertexYModifier = k;
				vertexZModifier = l;
				return;
			}
		}
		if (i == 1)
		{
			for (int k1 = 0; k1 < i1; k1++)
			{
				int l2 = ai[k1];
				if (l2 < vectorSkin.length)
				{
					int[] ai1 = vectorSkin[l2];
					for (int i4 = 0; i4 < ai1.length; i4++)
					{
						int j5 = ai1[i4];
						verticesX[j5] += j;
						verticesY[j5] += k;
						verticesZ[j5] += l;
					}

				}
			}

			return;
		}
		if (i == 2)
		{
			for (int l1 = 0; l1 < i1; l1++)
			{
				int i3 = ai[l1];
				if (i3 < vectorSkin.length)
				{
					int[] ai2 = vectorSkin[i3];
					for (int j4 = 0; j4 < ai2.length; j4++)
					{
						int k5 = ai2[j4];
						verticesX[k5] -= vertexXModifier;
						verticesY[k5] -= vertexYModifier;
						verticesZ[k5] -= vertexZModifier;
						int k6 = (j & 0xff) * 8;
						int l6 = (k & 0xff) * 8;
						int i7 = (l & 0xff) * 8;
						if (i7 != 0)
						{
							int j7 = SINE[i7];
							int i8 = COSINE[i7];
							int l8 = verticesY[k5] * j7 + verticesX[k5] * i8 >> 16;
							verticesY[k5] = verticesY[k5] * i8 - verticesX[k5] * j7 >> 16;
							verticesX[k5] = l8;
						}
						if (k6 != 0)
						{
							int k7 = SINE[k6];
							int j8 = COSINE[k6];
							int i9 = verticesY[k5] * j8 - verticesZ[k5] * k7 >> 16;
							verticesZ[k5] = verticesY[k5] * k7 + verticesZ[k5] * j8 >> 16;
							verticesY[k5] = i9;
						}
						if (l6 != 0)
						{
							int l7 = SINE[l6];
							int k8 = COSINE[l6];
							int j9 = verticesZ[k5] * l7 + verticesX[k5] * k8 >> 16;
							verticesZ[k5] = verticesZ[k5] * k8 - verticesX[k5] * l7 >> 16;
							verticesX[k5] = j9;
						}
						verticesX[k5] += vertexXModifier;
						verticesY[k5] += vertexYModifier;
						verticesZ[k5] += vertexZModifier;
					}

				}
			}

			return;
		}
		if (i == 3)
		{
			for (int i2 = 0; i2 < i1; i2++)
			{
				int j3 = ai[i2];
				if (j3 < vectorSkin.length)
				{
					int[] ai3 = vectorSkin[j3];
					for (int k4 = 0; k4 < ai3.length; k4++)
					{
						int l5 = ai3[k4];
						verticesX[l5] -= vertexXModifier;
						verticesY[l5] -= vertexYModifier;
						verticesZ[l5] -= vertexZModifier;
						verticesX[l5] = (verticesX[l5] * j) / 128;
						verticesY[l5] = (verticesY[l5] * k) / 128;
						verticesZ[l5] = (verticesZ[l5] * l) / 128;
						verticesX[l5] += vertexXModifier;
						verticesY[l5] += vertexYModifier;
						verticesZ[l5] += vertexZModifier;
					}

				}
			}

			return;
		}
		if (i == 5 && triangleSkin != null && triangleAlphaValues != null)
		{
			for (int j2 = 0; j2 < i1; j2++)
			{
				int k3 = ai[j2];
				if (k3 < triangleSkin.length)
				{
					int[] ai4 = triangleSkin[k3];
					for (int l4 = 0; l4 < ai4.length; l4++)
					{
						int i6 = ai4[l4];
						triangleAlphaValues[i6] += j * 8;
						if (triangleAlphaValues[i6] < 0)
						{
							triangleAlphaValues[i6] = 0;
						}
						if (triangleAlphaValues[i6] > 255)
						{
							triangleAlphaValues[i6] = 255;
						}
					}

				}
			}

		}
	}

	public void rotate90Degrees()
	{
		for (int i = 0; i < vertexCount; i++)
		{
			int j = verticesX[i];
			verticesX[i] = verticesZ[i];
			verticesZ[i] = -j;
		}

	}

	void rotateX(int i)
	{
		int k = SINE[i];
		int l = COSINE[i];
		for (int i1 = 0; i1 < vertexCount; i1++)
		{
			int j1 = verticesY[i1] * l - verticesZ[i1] * k >> 16;
			verticesZ[i1] = verticesY[i1] * k + verticesZ[i1] * l >> 16;
			verticesY[i1] = j1;
		}

	}

	public void translate(int i, int j, int k)
	{
		for (int l = 0; l < vertexCount; l++)
		{
			verticesX[l] += i;
			verticesY[l] += k;
			verticesZ[l] += j;
		}

	}

	public void replaceColor(int oldColor, int newColor)
	{
		for (int i = 0; i < triangleCount; i++)
		{
			if (triangleColorValues[i] == oldColor)
			{
				triangleColorValues[i] = newColor;
			}
		}

	}

	public void mirror(int i)
	{
		if (i != 0)
		{
			for (int j = 1; j > 0; j++)
			{
				;
			}
		}
		for (int k = 0; k < vertexCount; k++)
		{
			verticesZ[k] = -verticesZ[k];
		}

		for (int l = 0; l < triangleCount; l++)
		{
			int i1 = trianglePointsX[l];
			trianglePointsX[l] = trianglePointsZ[l];
			trianglePointsZ[l] = i1;
		}

	}

	public void scaleT(int i, int j, int k, int l)
	{
		for (int i1 = 0; i1 < vertexCount; i1++)
		{
			verticesX[i1] = (verticesX[i1] * l) / 128;
			verticesY[i1] = (verticesY[i1] * i) / 128;
			verticesZ[i1] = (verticesZ[i1] * j) / 128;
		}

	}

	public void applyLighting(int lightMod, int magnitudeMultiplier, int lightX, int lightY, int lightZ, boolean flatShading)
	{
		int lightMagnitude = (int) Math.sqrt(lightX * lightX + lightY * lightY + lightZ * lightZ);
		int magnitude = magnitudeMultiplier * lightMagnitude >> 8;
		if (triangleHSLA == null)
		{
			triangleHSLA = new int[triangleCount];
			triangleHSLB = new int[triangleCount];
			triangleHSLC = new int[triangleCount];
		}
		if (super.verticesNormal == null)
		{
			super.verticesNormal = new VertexNormal[vertexCount];
			for (int vertex = 0; vertex < vertexCount; vertex++)
			{
				super.verticesNormal[vertex] = new VertexNormal();
			}

		}
		for (int triangle = 0; triangle < triangleCount; triangle++)
		{
			int _triangleX = trianglePointsX[triangle];
			int _triangleY = trianglePointsY[triangle];
			int _triangleZ = trianglePointsZ[triangle];
			int distanceXXY = verticesX[_triangleY] - verticesX[_triangleX];
			int distanceYXY = verticesY[_triangleY] - verticesY[_triangleX];
			int distanceZXY = verticesZ[_triangleY] - verticesZ[_triangleX];
			int distanceXZX = verticesX[_triangleZ] - verticesX[_triangleX];
			int distanceYZX = verticesY[_triangleZ] - verticesY[_triangleX];
			int distanceZZX = verticesZ[_triangleZ] - verticesZ[_triangleX];
			int normalX = distanceYXY * distanceZZX - distanceYZX * distanceZXY;
			int normalY = distanceZXY * distanceXZX - distanceZZX * distanceXXY;
			int normalZ;
			for (normalZ = distanceXXY * distanceYZX - distanceXZX * distanceYXY; normalX > 8192 || normalY > 8192 || normalZ > 8192 || normalX < -8192 || normalY < -8192 || normalZ < -8192; normalZ >>= 1)
			{
				normalX >>= 1;
				normalY >>= 1;
			}

			int normalLength = (int) Math.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
			if (normalLength <= 0)
			{
				normalLength = 1;
			}
			normalX = (normalX * 256) / normalLength;
			normalY = (normalY * 256) / normalLength;
			normalZ = (normalZ * 256) / normalLength;
			if (triangleDrawType == null || (triangleDrawType[triangle] & 1) == 0)
			{
				VertexNormal vertexNormal = super.verticesNormal[_triangleX];
				vertexNormal.x += normalX;
				vertexNormal.y += normalY;
				vertexNormal.z += normalZ;
				vertexNormal.magnitude++;
				vertexNormal = super.verticesNormal[_triangleY];
				vertexNormal.x += normalX;
				vertexNormal.y += normalY;
				vertexNormal.z += normalZ;
				vertexNormal.magnitude++;
				vertexNormal = super.verticesNormal[_triangleZ];
				vertexNormal.x += normalX;
				vertexNormal.y += normalY;
				vertexNormal.z += normalZ;
				vertexNormal.magnitude++;
			}
			else
			{
				int lightness = lightMod + (lightX * normalX + lightY * normalY + lightZ * normalZ) / (magnitude + magnitude / 2);
				triangleHSLA[triangle] = mixLightness(triangleColorValues[triangle], lightness, triangleDrawType[triangle]);
			}
		}

		if (flatShading)
		{
			handleShading(lightMod, magnitude, lightX, lightY, lightZ);
		}
		else
		{
			vertexNormalOffset = new VertexNormal[vertexCount];
			for (int vertex = 0; vertex < vertexCount; vertex++)
			{
				VertexNormal vertexNormal = super.verticesNormal[vertex];
				VertexNormal shadowVertexNormal = vertexNormalOffset[vertex] = new VertexNormal();
				shadowVertexNormal.x = vertexNormal.x;
				shadowVertexNormal.y = vertexNormal.y;
				shadowVertexNormal.z = vertexNormal.z;
				shadowVertexNormal.magnitude = vertexNormal.magnitude;
			}

			anInt1668 = (lightMod << 16) + (magnitude & 0xffff);
		}
		if (flatShading)
		{
			calculateDiagonals();
		}
		else
		{
			calculateDiagonalsAndBounds();
		}
	}

	public void handleShading(int i, int j, int k, int l)
	{
		int i1 = anInt1668 >> 16;
		int j1 = (anInt1668 << 16) >> 16;
		handleShading(i1, j1, l, i, j);
	}

	private void handleShading(int i, int j, int k, int l, int i1)
	{
		for (int j1 = 0; j1 < triangleCount; j1++)
		{
			int k1 = trianglePointsX[j1];
			int i2 = trianglePointsY[j1];
			int j2 = trianglePointsZ[j1];
			if (triangleDrawType == null)
			{
				int i3 = triangleColorValues[j1];
				VertexNormal class40 = super.verticesNormal[k1];
				int k2 = i + (k * class40.x + l * class40.y + i1 * class40.z)
					/ (j * class40.magnitude);
				triangleHSLA[j1] = mixLightness(i3, k2, 0);
				class40 = super.verticesNormal[i2];
				k2 = i + (k * class40.x + l * class40.y + i1 * class40.z) / (j * class40.magnitude);
				triangleHSLB[j1] = mixLightness(i3, k2, 0);
				class40 = super.verticesNormal[j2];
				k2 = i + (k * class40.x + l * class40.y + i1 * class40.z) / (j * class40.magnitude);
				triangleHSLC[j1] = mixLightness(i3, k2, 0);
			}
			else if ((triangleDrawType[j1] & 1) == 0)
			{
				int j3 = triangleColorValues[j1];
				int k3 = triangleDrawType[j1];
				VertexNormal class40_1 = super.verticesNormal[k1];
				int l2 = i + (k * class40_1.x + l * class40_1.y + i1 * class40_1.z)
					/ (j * class40_1.magnitude);
				triangleHSLA[j1] = mixLightness(j3, l2, k3);
				class40_1 = super.verticesNormal[i2];
				l2 = i + (k * class40_1.x + l * class40_1.y + i1 * class40_1.z)
					/ (j * class40_1.magnitude);
				triangleHSLB[j1] = mixLightness(j3, l2, k3);
				class40_1 = super.verticesNormal[j2];
				l2 = i + (k * class40_1.x + l * class40_1.y + i1 * class40_1.z)
					/ (j * class40_1.magnitude);
				triangleHSLC[j1] = mixLightness(j3, l2, k3);
			}
		}

		super.verticesNormal = null;
		vertexNormalOffset = null;
		vertexSkins = null;
		triangleSkinValues = null;
		if (triangleDrawType != null)
		{
			for (int l1 = 0; l1 < triangleCount; l1++)
			{
				if ((triangleDrawType[l1] & 2) == 2)
				{
					return;
				}
			}

		}
		triangleColorValues = null;
	}

	public void render(int i, int j, int k, int l, int i1, int j1, int k1)
	{
		int l1 = Rasterizer3D.center_x;
		int i2 = Rasterizer3D.center_y;
		int j2 = SINE[i];
		int k2 = COSINE[i];
		int l2 = SINE[j];
		int i3 = COSINE[j];
		int j3 = SINE[k];
		int k3 = COSINE[k];
		int l3 = SINE[l];
		int i4 = COSINE[l];
		int j4 = j1 * l3 + k1 * i4 >> 16;
		for (int k4 = 0; k4 < vertexCount; k4++)
		{
			int l4 = verticesX[k4];
			int i5 = verticesY[k4];
			int j5 = verticesZ[k4];
			if (k != 0)
			{
				int k5 = i5 * j3 + l4 * k3 >> 16;
				i5 = i5 * k3 - l4 * j3 >> 16;
				l4 = k5;
			}
			if (i != 0)
			{
				int l5 = i5 * k2 - j5 * j2 >> 16;
				j5 = i5 * j2 + j5 * k2 >> 16;
				i5 = l5;
			}
			if (j != 0)
			{
				int i6 = j5 * l2 + l4 * i3 >> 16;
				j5 = j5 * i3 - l4 * l2 >> 16;
				l4 = i6;
			}
			l4 += i1;
			i5 += j1;
			j5 += k1;
			int j6 = i5 * i4 - j5 * l3 >> 16;
			j5 = i5 * l3 + j5 * i4 >> 16;
			i5 = j6;
			vertexScreenZ[k4] = j5 - j4;
			vertexScreenX[k4] = l1 + (l4 << 9) / j5;
			vertexScreenY[k4] = i2 + (i5 << 9) / j5;
			if (texturedTriangleCount > 0)
			{
				vertexMovedX[k4] = l4;
				vertexMovedY[k4] = i5;
				vertexMovedZ[k4] = j5;
			}
		}

		try
		{
			method599(false, false, 0);
		}
		catch (Exception _ex)
		{
		}
	}

	@Override
	public void renderAtPoint(int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2)
	{
		int j2 = l1 * i1 - j1 * l >> 16;
		int k2 = k1 * j + j2 * k >> 16;
		int l2 = diagonal2DAboveOrigin * k >> 16;
		int i3 = k2 + l2;
		if (i3 <= 50 || k2 >= 3500)
		{
			return;
		}
		int j3 = l1 * l + j1 * i1 >> 16;
		int k3 = j3 - diagonal2DAboveOrigin << 9;
		if (k3 / i3 >= Rasterizer.centerX)
		{
			return;
		}
		int l3 = j3 + diagonal2DAboveOrigin << 9;
		if (l3 / i3 <= -Rasterizer.centerX)
		{
			return;
		}
		int i4 = k1 * k - j2 * j >> 16;
		int j4 = diagonal2DAboveOrigin * j >> 16;
		int k4 = i4 + j4 << 9;
		if (k4 / i3 <= -Rasterizer.centerY)
		{
			return;
		}
		int l4 = j4 + (super.modelHeight * k >> 16);
		int i5 = i4 - l4 << 9;
		if (i5 / i3 >= Rasterizer.centerY)
		{
			return;
		}
		int j5 = l2 + (super.modelHeight * j >> 16);
		boolean flag = false;
		if (k2 - j5 <= 50)
		{
			flag = true;
		}
		boolean flag1 = false;
		if (i2 > 0 && gameScreenClickable)
		{
			int k5 = k2 - l2;
			if (k5 <= 50)
			{
				k5 = 50;
			}
			if (j3 > 0)
			{
				k3 /= i3;
				l3 /= k5;
			}
			else
			{
				l3 /= i3;
				k3 /= k5;
			}
			if (i4 > 0)
			{
				i5 /= i3;
				k4 /= k5;
			}
			else
			{
				k4 /= i3;
				i5 /= k5;
			}
			int i6 = cursorX - Rasterizer3D.center_x;
			int k6 = cursorY - Rasterizer3D.center_y;
			if (i6 > k3 && i6 < l3 && k6 > i5 && k6 < k4)
			{
				if (singleTile)
				{
					hoveredHash[resourceCount++] = i2;
				}
				else
				{
					flag1 = true;
				}
			}
		}
		int l5 = Rasterizer3D.center_x;
		int j6 = Rasterizer3D.center_y;
		int l6 = 0;
		int i7 = 0;
		if (i != 0)
		{
			l6 = SINE[i];
			i7 = COSINE[i];
		}
		for (int j7 = 0; j7 < vertexCount; j7++)
		{
			int k7 = verticesX[j7];
			int l7 = verticesY[j7];
			int i8 = verticesZ[j7];
			if (i != 0)
			{
				int j8 = i8 * l6 + k7 * i7 >> 16;
				i8 = i8 * i7 - k7 * l6 >> 16;
				k7 = j8;
			}
			k7 += j1;
			l7 += k1;
			i8 += l1;
			int k8 = i8 * l + k7 * i1 >> 16;
			i8 = i8 * i1 - k7 * l >> 16;
			k7 = k8;
			k8 = l7 * k - i8 * j >> 16;
			i8 = l7 * j + i8 * k >> 16;
			l7 = k8;
			vertexScreenZ[j7] = i8 - k2;
			if (i8 >= 50)
			{
				vertexScreenX[j7] = l5 + (k7 << 9) / i8;
				vertexScreenY[j7] = j6 + (l7 << 9) / i8;
			}
			else
			{
				vertexScreenX[j7] = -5000;
				flag = true;
			}
			if (flag || texturedTriangleCount > 0)
			{
				vertexMovedX[j7] = k7;
				vertexMovedY[j7] = l7;
				vertexMovedZ[j7] = i8;
			}
		}

		try
		{
			method599(flag, flag1, i2);
		}
		catch (Exception _ex)
		{
		}
	}

	private void method599(boolean flag, boolean flag1, int i)
	{
		for (int j = 0; j < diagonal3D; j++)
		{
			anIntArray1692[j] = 0;
		}

		for (int k = 0; k < triangleCount; k++)
		{
			if (triangleDrawType == null || triangleDrawType[k] != -1)
			{
				int l = trianglePointsX[k];
				int k1 = trianglePointsY[k];
				int j2 = trianglePointsZ[k];
				int i3 = vertexScreenX[l];
				int l3 = vertexScreenX[k1];
				int k4 = vertexScreenX[j2];
				if (flag && (i3 == -5000 || l3 == -5000 || k4 == -5000))
				{
					aBooleanArray1685[k] = true;
					int j5 = (vertexScreenZ[l] + vertexScreenZ[k1] + vertexScreenZ[j2]) / 3 + diagonal3DAboveOrigin;
					anIntArrayArray1693[j5][anIntArray1692[j5]++] = k;
				}
				else
				{
					if (flag1
						&& method602(cursorX, cursorY, vertexScreenY[l], vertexScreenY[k1],
						vertexScreenY[j2], i3, l3, k4))
					{
						hoveredHash[resourceCount++] = i;
						flag1 = false;
					}
					if ((i3 - l3) * (vertexScreenY[j2] - vertexScreenY[k1])
						- (vertexScreenY[l] - vertexScreenY[k1]) * (k4 - l3) > 0)
					{
						aBooleanArray1685[k] = false;
						if (i3 < 0 || l3 < 0 || k4 < 0 || i3 > Rasterizer.viewportRx
							|| l3 > Rasterizer.viewportRx || k4 > Rasterizer.viewportRx)
						{
							restrictEdges[k] = true;
						}
						else
						{
							restrictEdges[k] = false;
						}
						int k5 = (vertexScreenZ[l] + vertexScreenZ[k1] + vertexScreenZ[j2]) / 3 + diagonal3DAboveOrigin;
						anIntArrayArray1693[k5][anIntArray1692[k5]++] = k;
					}
				}
			}
		}

		if (trianglePriorities == null)
		{
			for (int i1 = diagonal3D - 1; i1 >= 0; i1--)
			{
				int l1 = anIntArray1692[i1];
				if (l1 > 0)
				{
					int[] ai = anIntArrayArray1693[i1];
					for (int j3 = 0; j3 < l1; j3++)
					{
						method600(ai[j3]);
					}

				}
			}

			return;
		}
		for (int j1 = 0; j1 < 12; j1++)
		{
			anIntArray1694[j1] = 0;
			anIntArray1698[j1] = 0;
		}

		for (int i2 = diagonal3D - 1; i2 >= 0; i2--)
		{
			int k2 = anIntArray1692[i2];
			if (k2 > 0)
			{
				int[] ai1 = anIntArrayArray1693[i2];
				for (int i4 = 0; i4 < k2; i4++)
				{
					int l4 = ai1[i4];
					int l5 = trianglePriorities[l4];
					int j6 = anIntArray1694[l5]++;
					anIntArrayArray1695[l5][j6] = l4;
					if (l5 < 10)
					{
						anIntArray1698[l5] += i2;
					}
					else if (l5 == 10)
					{
						anIntArray1696[j6] = i2;
					}
					else
					{
						anIntArray1697[j6] = i2;
					}
				}

			}
		}

		int l2 = 0;
		if (anIntArray1694[1] > 0 || anIntArray1694[2] > 0)
		{
			l2 = (anIntArray1698[1] + anIntArray1698[2]) / (anIntArray1694[1] + anIntArray1694[2]);
		}
		int k3 = 0;
		if (anIntArray1694[3] > 0 || anIntArray1694[4] > 0)
		{
			k3 = (anIntArray1698[3] + anIntArray1698[4]) / (anIntArray1694[3] + anIntArray1694[4]);
		}
		int j4 = 0;
		if (anIntArray1694[6] > 0 || anIntArray1694[8] > 0)
		{
			j4 = (anIntArray1698[6] + anIntArray1698[8]) / (anIntArray1694[6] + anIntArray1694[8]);
		}
		int i6 = 0;
		int k6 = anIntArray1694[10];
		int[] ai2 = anIntArrayArray1695[10];
		int[] ai3 = anIntArray1696;
		if (i6 == k6)
		{
			i6 = 0;
			k6 = anIntArray1694[11];
			ai2 = anIntArrayArray1695[11];
			ai3 = anIntArray1697;
		}
		int i5;
		if (i6 < k6)
		{
			i5 = ai3[i6];
		}
		else
		{
			i5 = -1000;
		}
		for (int l6 = 0; l6 < 10; l6++)
		{
			while (l6 == 0 && i5 > l2)
			{
				method600(ai2[i6++]);
				if (i6 == k6 && ai2 != anIntArrayArray1695[11])
				{
					i6 = 0;
					k6 = anIntArray1694[11];
					ai2 = anIntArrayArray1695[11];
					ai3 = anIntArray1697;
				}
				if (i6 < k6)
				{
					i5 = ai3[i6];
				}
				else
				{
					i5 = -1000;
				}
			}
			while (l6 == 3 && i5 > k3)
			{
				method600(ai2[i6++]);
				if (i6 == k6 && ai2 != anIntArrayArray1695[11])
				{
					i6 = 0;
					k6 = anIntArray1694[11];
					ai2 = anIntArrayArray1695[11];
					ai3 = anIntArray1697;
				}
				if (i6 < k6)
				{
					i5 = ai3[i6];
				}
				else
				{
					i5 = -1000;
				}
			}
			while (l6 == 5 && i5 > j4)
			{
				method600(ai2[i6++]);
				if (i6 == k6 && ai2 != anIntArrayArray1695[11])
				{
					i6 = 0;
					k6 = anIntArray1694[11];
					ai2 = anIntArrayArray1695[11];
					ai3 = anIntArray1697;
				}
				if (i6 < k6)
				{
					i5 = ai3[i6];
				}
				else
				{
					i5 = -1000;
				}
			}
			int i7 = anIntArray1694[l6];
			int[] ai4 = anIntArrayArray1695[l6];
			for (int j7 = 0; j7 < i7; j7++)
			{
				method600(ai4[j7]);
			}

		}

		while (i5 != -1000)
		{
			method600(ai2[i6++]);
			if (i6 == k6 && ai2 != anIntArrayArray1695[11])
			{
				i6 = 0;
				ai2 = anIntArrayArray1695[11];
				k6 = anIntArray1694[11];
				ai3 = anIntArray1697;
			}
			if (i6 < k6)
			{
				i5 = ai3[i6];
			}
			else
			{
				i5 = -1000;
			}
		}
	}

	private void method600(int i)
	{
		if (aBooleanArray1685[i])
		{
			method601(i);
			return;
		}
		int j = trianglePointsX[i];
		int k = trianglePointsY[i];
		int l = trianglePointsZ[i];
		Rasterizer3D.restrict_edges = restrictEdges[i];
		if (triangleAlphaValues == null)
		{
			Rasterizer3D.alpha = 0;
		}
		else
		{
			Rasterizer3D.alpha = triangleAlphaValues[i];
		}
		int i1;
		if (triangleDrawType == null)
		{
			i1 = 0;
		}
		else
		{
			i1 = triangleDrawType[i] & 3;
		}
		if (i1 == 0)
		{
			Rasterizer3D.drawShadedTriangle(vertexScreenY[j], vertexScreenY[k], vertexScreenY[l],
				vertexScreenX[j], vertexScreenX[k], vertexScreenX[l], triangleHSLA[i], triangleHSLB[i],
				triangleHSLC[i]);
			return;
		}
		if (i1 == 1)
		{
			Rasterizer3D.drawFlatTriangle(vertexScreenY[j], vertexScreenY[k], vertexScreenY[l],
				vertexScreenX[j], vertexScreenX[k], vertexScreenX[l], HSLtoRGB[triangleHSLA[i]]);
			return;
		}
		if (i1 == 2)
		{
			int j1 = triangleDrawType[i] >> 2;
			int l1 = texturedTrianglePointsX[j1];
			int j2 = texturedTrianglePointsY[j1];
			int l2 = texturedTrianglePointsZ[j1];
			Rasterizer3D.drawTexturedTriangle(vertexScreenY[j], vertexScreenY[k], vertexScreenY[l],
				vertexScreenX[j], vertexScreenX[k], vertexScreenX[l], triangleHSLA[i], triangleHSLB[i],
				triangleHSLC[i], vertexMovedX[l1], vertexMovedX[j2], vertexMovedX[l2], vertexMovedY[l1],
				vertexMovedY[j2], vertexMovedY[l2], vertexMovedZ[l1], vertexMovedZ[j2], vertexMovedZ[l2],
				triangleColorValues[i]);
			return;
		}
		int k1 = triangleDrawType[i] >> 2;
		int i2 = texturedTrianglePointsX[k1];
		int k2 = texturedTrianglePointsY[k1];
		int i3 = texturedTrianglePointsZ[k1];
		Rasterizer3D.drawTexturedTriangle(vertexScreenY[j], vertexScreenY[k], vertexScreenY[l],
			vertexScreenX[j], vertexScreenX[k], vertexScreenX[l], triangleHSLA[i], triangleHSLA[i],
			triangleHSLA[i], vertexMovedX[i2], vertexMovedX[k2], vertexMovedX[i3], vertexMovedY[i2],
			vertexMovedY[k2], vertexMovedY[i3], vertexMovedZ[i2], vertexMovedZ[k2], vertexMovedZ[i3],
			triangleColorValues[i]);
	}

	private void method601(int i)
	{
		int j = Rasterizer3D.center_x;
		int k = Rasterizer3D.center_y;
		int l = 0;
		int i1 = trianglePointsX[i];
		int j1 = trianglePointsY[i];
		int k1 = trianglePointsZ[i];
		int l1 = vertexMovedZ[i1];
		int i2 = vertexMovedZ[j1];
		int j2 = vertexMovedZ[k1];
		if (l1 >= 50)
		{
			anIntArray1699[l] = vertexScreenX[i1];
			anIntArray1700[l] = vertexScreenY[i1];
			anIntArray1701[l++] = triangleHSLA[i];
		}
		else
		{
			int k2 = vertexMovedX[i1];
			int k3 = vertexMovedY[i1];
			int k4 = triangleHSLA[i];
			if (j2 >= 50)
			{
				int k5 = (50 - l1) * anIntArray1713[j2 - l1];
				anIntArray1699[l] = j + (k2 + ((vertexMovedX[k1] - k2) * k5 >> 16) << 9) / 50;
				anIntArray1700[l] = k + (k3 + ((vertexMovedY[k1] - k3) * k5 >> 16) << 9) / 50;
				anIntArray1701[l++] = k4 + ((triangleHSLC[i] - k4) * k5 >> 16);
			}
			if (i2 >= 50)
			{
				int l5 = (50 - l1) * anIntArray1713[i2 - l1];
				anIntArray1699[l] = j + (k2 + ((vertexMovedX[j1] - k2) * l5 >> 16) << 9) / 50;
				anIntArray1700[l] = k + (k3 + ((vertexMovedY[j1] - k3) * l5 >> 16) << 9) / 50;
				anIntArray1701[l++] = k4 + ((triangleHSLB[i] - k4) * l5 >> 16);
			}
		}
		if (i2 >= 50)
		{
			anIntArray1699[l] = vertexScreenX[j1];
			anIntArray1700[l] = vertexScreenY[j1];
			anIntArray1701[l++] = triangleHSLB[i];
		}
		else
		{
			int l2 = vertexMovedX[j1];
			int l3 = vertexMovedY[j1];
			int l4 = triangleHSLB[i];
			if (l1 >= 50)
			{
				int i6 = (50 - i2) * anIntArray1713[l1 - i2];
				anIntArray1699[l] = j + (l2 + ((vertexMovedX[i1] - l2) * i6 >> 16) << 9) / 50;
				anIntArray1700[l] = k + (l3 + ((vertexMovedY[i1] - l3) * i6 >> 16) << 9) / 50;
				anIntArray1701[l++] = l4 + ((triangleHSLA[i] - l4) * i6 >> 16);
			}
			if (j2 >= 50)
			{
				int j6 = (50 - i2) * anIntArray1713[j2 - i2];
				anIntArray1699[l] = j + (l2 + ((vertexMovedX[k1] - l2) * j6 >> 16) << 9) / 50;
				anIntArray1700[l] = k + (l3 + ((vertexMovedY[k1] - l3) * j6 >> 16) << 9) / 50;
				anIntArray1701[l++] = l4 + ((triangleHSLC[i] - l4) * j6 >> 16);
			}
		}
		if (j2 >= 50)
		{
			anIntArray1699[l] = vertexScreenX[k1];
			anIntArray1700[l] = vertexScreenY[k1];
			anIntArray1701[l++] = triangleHSLC[i];
		}
		else
		{
			int i3 = vertexMovedX[k1];
			int i4 = vertexMovedY[k1];
			int i5 = triangleHSLC[i];
			if (i2 >= 50)
			{
				int k6 = (50 - j2) * anIntArray1713[i2 - j2];
				anIntArray1699[l] = j + (i3 + ((vertexMovedX[j1] - i3) * k6 >> 16) << 9) / 50;
				anIntArray1700[l] = k + (i4 + ((vertexMovedY[j1] - i4) * k6 >> 16) << 9) / 50;
				anIntArray1701[l++] = i5 + ((triangleHSLB[i] - i5) * k6 >> 16);
			}
			if (l1 >= 50)
			{
				int l6 = (50 - j2) * anIntArray1713[l1 - j2];
				anIntArray1699[l] = j + (i3 + ((vertexMovedX[i1] - i3) * l6 >> 16) << 9) / 50;
				anIntArray1700[l] = k + (i4 + ((vertexMovedY[i1] - i4) * l6 >> 16) << 9) / 50;
				anIntArray1701[l++] = i5 + ((triangleHSLA[i] - i5) * l6 >> 16);
			}
		}
		int j3 = anIntArray1699[0];
		int j4 = anIntArray1699[1];
		int j5 = anIntArray1699[2];
		int i7 = anIntArray1700[0];
		int j7 = anIntArray1700[1];
		int k7 = anIntArray1700[2];
		if ((j3 - j4) * (k7 - j7) - (i7 - j7) * (j5 - j4) > 0)
		{
			Rasterizer3D.restrict_edges = false;
			if (l == 3)
			{
				if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > Rasterizer.viewportRx || j4 > Rasterizer.viewportRx
					|| j5 > Rasterizer.viewportRx)
				{
					Rasterizer3D.restrict_edges = true;
				}
				int l7;
				if (triangleDrawType == null)
				{
					l7 = 0;
				}
				else
				{
					l7 = triangleDrawType[i] & 3;
				}
				if (l7 == 0)
				{
					Rasterizer3D.drawShadedTriangle(i7, j7, k7, j3, j4, j5, anIntArray1701[0], anIntArray1701[1],
						anIntArray1701[2]);
				}
				else if (l7 == 1)
				{
					Rasterizer3D.drawFlatTriangle(i7, j7, k7, j3, j4, j5, HSLtoRGB[triangleHSLA[i]]);
				}
				else if (l7 == 2)
				{
					int j8 = triangleDrawType[i] >> 2;
					int k9 = texturedTrianglePointsX[j8];
					int k10 = texturedTrianglePointsY[j8];
					int k11 = texturedTrianglePointsZ[j8];
					Rasterizer3D.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, anIntArray1701[0], anIntArray1701[1],
						anIntArray1701[2], vertexMovedX[k9], vertexMovedX[k10], vertexMovedX[k11],
						vertexMovedY[k9], vertexMovedY[k10], vertexMovedY[k11], vertexMovedZ[k9],
						vertexMovedZ[k10], vertexMovedZ[k11], triangleColorValues[i]);
				}
				else
				{
					int k8 = triangleDrawType[i] >> 2;
					int l9 = texturedTrianglePointsX[k8];
					int l10 = texturedTrianglePointsY[k8];
					int l11 = texturedTrianglePointsZ[k8];
					Rasterizer3D.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, triangleHSLA[i], triangleHSLA[i],
						triangleHSLA[i], vertexMovedX[l9], vertexMovedX[l10], vertexMovedX[l11],
						vertexMovedY[l9], vertexMovedY[l10], vertexMovedY[l11], vertexMovedZ[l9],
						vertexMovedZ[l10], vertexMovedZ[l11], triangleColorValues[i]);
				}
			}
			if (l == 4)
			{
				if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > Rasterizer.viewportRx || j4 > Rasterizer.viewportRx
					|| j5 > Rasterizer.viewportRx || anIntArray1699[3] < 0
					|| anIntArray1699[3] > Rasterizer.viewportRx)
				{
					Rasterizer3D.restrict_edges = true;
				}
				int i8;
				if (triangleDrawType == null)
				{
					i8 = 0;
				}
				else
				{
					i8 = triangleDrawType[i] & 3;
				}
				if (i8 == 0)
				{
					Rasterizer3D.drawShadedTriangle(i7, j7, k7, j3, j4, j5, anIntArray1701[0], anIntArray1701[1],
						anIntArray1701[2]);
					Rasterizer3D.drawShadedTriangle(i7, k7, anIntArray1700[3], j3, j5, anIntArray1699[3],
						anIntArray1701[0], anIntArray1701[2], anIntArray1701[3]);
					return;
				}
				if (i8 == 1)
				{
					int l8 = HSLtoRGB[triangleHSLA[i]];
					Rasterizer3D.drawFlatTriangle(i7, j7, k7, j3, j4, j5, l8);
					Rasterizer3D.drawFlatTriangle(i7, k7, anIntArray1700[3], j3, j5, anIntArray1699[3], l8);
					return;
				}
				if (i8 == 2)
				{
					int i9 = triangleDrawType[i] >> 2;
					int i10 = texturedTrianglePointsX[i9];
					int i11 = texturedTrianglePointsY[i9];
					int i12 = texturedTrianglePointsZ[i9];
					Rasterizer3D.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, anIntArray1701[0], anIntArray1701[1],
						anIntArray1701[2], vertexMovedX[i10], vertexMovedX[i11], vertexMovedX[i12],
						vertexMovedY[i10], vertexMovedY[i11], vertexMovedY[i12], vertexMovedZ[i10],
						vertexMovedZ[i11], vertexMovedZ[i12], triangleColorValues[i]);
					Rasterizer3D.drawTexturedTriangle(i7, k7, anIntArray1700[3], j3, j5, anIntArray1699[3],
						anIntArray1701[0], anIntArray1701[2], anIntArray1701[3], vertexMovedX[i10],
						vertexMovedX[i11], vertexMovedX[i12], vertexMovedY[i10], vertexMovedY[i11],
						vertexMovedY[i12], vertexMovedZ[i10], vertexMovedZ[i11], vertexMovedZ[i12],
						triangleColorValues[i]);
					return;
				}
				int j9 = triangleDrawType[i] >> 2;
				int j10 = texturedTrianglePointsX[j9];
				int j11 = texturedTrianglePointsY[j9];
				int j12 = texturedTrianglePointsZ[j9];
				Rasterizer3D.drawTexturedTriangle(i7, j7, k7, j3, j4, j5, triangleHSLA[i], triangleHSLA[i],
					triangleHSLA[i], vertexMovedX[j10], vertexMovedX[j11], vertexMovedX[j12],
					vertexMovedY[j10], vertexMovedY[j11], vertexMovedY[j12], vertexMovedZ[j10],
					vertexMovedZ[j11], vertexMovedZ[j12], triangleColorValues[i]);
				Rasterizer3D.drawTexturedTriangle(i7, k7, anIntArray1700[3], j3, j5, anIntArray1699[3],
					triangleHSLA[i], triangleHSLA[i], triangleHSLA[i], vertexMovedX[j10],
					vertexMovedX[j11], vertexMovedX[j12], vertexMovedY[j10], vertexMovedY[j11],
					vertexMovedY[j12], vertexMovedZ[j10], vertexMovedZ[j11], vertexMovedZ[j12],
					triangleColorValues[i]);
			}
		}
	}

	private boolean method602(int i, int j, int k, int l, int i1, int j1, int k1, int l1)
	{
		if (j < k && j < l && j < i1)
		{
			return false;
		}
		if (j > k && j > l && j > i1)
		{
			return false;
		}
		if (i < j1 && i < k1 && i < l1)
		{
			return false;
		}
		return i <= j1 || i <= k1 || i <= l1;
	}
}

//TODO Some more missing class names
