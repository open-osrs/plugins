/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package renderer.loader;

import renderer.model.ModelDefinition;
import renderer.util.CacheBuffer;
import renderer.util.Colors;

public class ModelLoader
{
	public static ModelDefinition load(int id, byte[] b)
	{
		ModelDefinition def = new ModelDefinition();

		if (b[b.length - 1] == -1 && b[b.length - 2] == -1)
		{
			load1(def, b);
		}
		else
		{
			load2(def, b);
		}

		for (ModelDefinition.Face face : def.faces)
		{
			face.color = Colors.hsl(face.color);
		}

		def.id = id;

		def.translucent = def.faces.stream().anyMatch(f -> (f.transparency != 0 && f.transparency < 254));
		def.calculateNormals();

		return def;
	}

	private static void load1(ModelDefinition model, byte[] var1)
	{
		CacheBuffer in1 = new CacheBuffer(var1);
		CacheBuffer in2 = new CacheBuffer(var1);
		CacheBuffer in3 = new CacheBuffer(var1);
		CacheBuffer in4 = new CacheBuffer(var1);
		CacheBuffer in5 = new CacheBuffer(var1);
		CacheBuffer in6 = new CacheBuffer(var1);
		CacheBuffer in7 = new CacheBuffer(var1);

		in1.position(var1.length - 23);
		int vertexCount = in1.getShort() & 0xFFFF;
		int faceCount = in1.getShort() & 0xFFFF;
		int textureTriangleCount = in1.get() & 0xFF;
		int var13 = in1.get() & 0xFF;
		int modelPriority = in1.get() & 0xFF;
		int hasAlpha = in1.get() & 0xFF;
		int var17 = in1.get() & 0xFF;
		int modelTexture = in1.get() & 0xFF;
		int modelVertexSkins = in1.get() & 0xFF;
		int var20 = in1.getShort() & 0xFFFF;
		int var21 = in1.getShort() & 0xFFFF;
		int var42 = in1.getShort() & 0xFFFF;
		int var22 = in1.getShort() & 0xFFFF;
		int var38 = in1.getShort() & 0xFFFF;

		int textureAmount = 0;
		int var7 = 0;
		int var29 = 0;
		int position;

		if (textureTriangleCount > 0)
		{
			model.textureRenderTypes = new byte[textureTriangleCount];
			in1.position(0);

			for (position = 0; position < textureTriangleCount; ++position)
			{
				byte renderType = model.textureRenderTypes[position] = in1.get();
				if (renderType == 0)
				{
					++textureAmount;
				}

				if (renderType >= 1 && renderType <= 3)
				{
					++var7;
				}

				if (renderType == 2)
				{
					++var29;
				}
			}
		}

		position = textureTriangleCount + vertexCount;
		int renderTypePos = position;
		if (var13 == 1)
		{
			position += faceCount;
		}

		int var49 = position;
		position += faceCount;
		int priorityPos = position;
		if (modelPriority == 255)
		{
			position += faceCount;
		}

		int triangleSkinPos = position;
		if (var17 == 1)
		{
			position += faceCount;
		}

		int vertexSkinPos = position;
		if (modelVertexSkins == 1)
		{
			position += vertexCount;
		}

		int alphaPos = position;
		if (hasAlpha == 1)
		{
			position += faceCount;
		}

		int var11 = position;
		position += var22;
		int texturePos = position;
		if (modelTexture == 1)
		{
			position += faceCount * 2;
		}

		int textureCoordPos = position;
		position += var38;
		int colorPos = position;
		position += faceCount * 2;
		int var40 = position;
		position += var20;
		int var41 = position;
		position += var21;
		int var8 = position;
		position += var42;
		int var43 = position;
		position += textureAmount * 6;
		int var37 = position;
		position += var7 * 6;
		int var48 = position;
		position += var7 * 6;
		int var56 = position;
		position += var7 * 2;
		int var45 = position;
		position += var7;
		int var46 = position;
		position += var7 * 2 + var29 * 2;

		model.textureTriangleCount = textureTriangleCount;

		if (modelPriority != 0xff)
		{
			model.priority = (byte) modelPriority;
		}

		boolean hasTextureCoordinates = modelTexture == 1 && textureTriangleCount > 0;

		if (textureTriangleCount > 0)
		{
			model.textureTriangleVertexIndices1 = new short[textureTriangleCount];
			model.textureTriangleVertexIndices2 = new short[textureTriangleCount];
			model.textureTriangleVertexIndices3 = new short[textureTriangleCount];
			if (var7 > 0)
			{
				model.unknown1 = new short[var7];
				model.unknown2 = new short[var7];
				model.unknown6 = new short[var7];
				model.unknown3 = new short[var7];
				model.unknown5 = new byte[var7];
				model.unknown4 = new short[var7];
			}

			if (var29 > 0)
			{
				model.texturePrimaryColors = new short[var29];
			}
		}

		in1.position(textureTriangleCount);
		in2.position(var40);
		in3.position(var41);
		in4.position(var8);
		in5.position(vertexSkinPos);
		int vX = 0;
		int vY = 0;
		int vZ = 0;

		int vertexZOffset;
		int vertexYOffset;
		int point;
		for (point = 0; point < vertexCount; ++point)
		{
			int vertexFlags = in1.get() & 0xFF;
			int vertexXOffset = 0;
			if ((vertexFlags & 1) != 0)
			{
				vertexXOffset = in2.getSpecial1();
			}

			vertexYOffset = 0;
			if ((vertexFlags & 2) != 0)
			{
				vertexYOffset = in3.getSpecial1();
			}

			vertexZOffset = 0;
			if ((vertexFlags & 4) != 0)
			{
				vertexZOffset = in4.getSpecial1();
			}

			vX += vertexXOffset;
			vY += vertexYOffset;
			vZ += vertexZOffset;
			ModelDefinition.Vertex vertex = new ModelDefinition.Vertex();
			vertex.x = vX;
			vertex.y = vY;
			vertex.z = vZ;
			vertex.index = point;
			model.vertices.add(vertex);

			if (modelVertexSkins == 1)
			{
				vertex.label = in5.get() & 0xFF;
			}
		}

		in1.position(colorPos);
		in2.position(renderTypePos);
		in3.position(priorityPos);
		in4.position(alphaPos);
		in5.position(triangleSkinPos);
		in6.position(texturePos);
		in7.position(textureCoordPos);

		for (point = 0; point < faceCount; ++point)
		{
			ModelDefinition.Face face = new ModelDefinition.Face();
			model.faces.add(face);

			face.color = (short) (in1.getShort() & 0xFFFF);

			if (var13 == 1)
			{
				face.renderType = in2.get();
			}

			if (modelPriority == 255)
			{
				face.priority = in3.get();
			}

			if (hasAlpha == 1)
			{
				face.transparency = in4.get() & 0xff;
			}

			if (var17 == 1)
			{
				face.label = in5.get() & 0xFF;
			}

			if (modelTexture == 1)
			{
				face.texture = (short) ((in6.getShort() & 0xFFFF) - 1);
			}

			if (hasTextureCoordinates && face.texture != -1)
			{
				face.textureCoordinates = (byte) ((in7.get() & 0xFF) - 1);
			}
		}

		in1.position(var11);
		in2.position(var49);
		int trianglePointX = 0;
		int trianglePointY = 0;
		int trianglePointZ = 0;
		vertexYOffset = 0;

		for (vertexZOffset = 0; vertexZOffset < faceCount; ++vertexZOffset)
		{
			ModelDefinition.Face face = model.faces.get(vertexZOffset);

			int numFaces = in2.get() & 0xFF;
			if (numFaces == 1)
			{
				trianglePointX = in1.getSpecial1() + vertexYOffset;
				trianglePointY = in1.getSpecial1() + trianglePointX;
				trianglePointZ = in1.getSpecial1() + trianglePointY;
				vertexYOffset = trianglePointZ;
				face.a = model.vertices.get(trianglePointX);
				face.b = model.vertices.get(trianglePointY);
				face.c = model.vertices.get(trianglePointZ);
			}

			if (numFaces == 2)
			{
				trianglePointY = trianglePointZ;
				trianglePointZ = in1.getSpecial1() + vertexYOffset;
				vertexYOffset = trianglePointZ;
				face.a = model.vertices.get(trianglePointX);
				face.b = model.vertices.get(trianglePointY);
				face.c = model.vertices.get(trianglePointZ);
			}

			if (numFaces == 3)
			{
				trianglePointX = trianglePointZ;
				trianglePointZ = in1.getSpecial1() + vertexYOffset;
				vertexYOffset = trianglePointZ;
				face.a = model.vertices.get(trianglePointX);
				face.b = model.vertices.get(trianglePointY);
				face.c = model.vertices.get(trianglePointZ);
			}

			if (numFaces == 4)
			{
				int var57 = trianglePointX;
				trianglePointX = trianglePointY;
				trianglePointY = var57;
				trianglePointZ = in1.getSpecial1() + vertexYOffset;
				vertexYOffset = trianglePointZ;
				face.a = model.vertices.get(trianglePointX);
				face.b = model.vertices.get(var57);
				face.c = model.vertices.get(trianglePointZ);
			}
		}

		in1.position(var43);
		in2.position(var37);
		in3.position(var48);
		in4.position(var56);
		in5.position(var45);
		in6.position(var46);

		for (int texIndex = 0; texIndex < textureTriangleCount; ++texIndex)
		{
			int type = model.textureRenderTypes[texIndex] & 255;

			if (type == 0)
			{
				model.textureTriangleVertexIndices1[texIndex] = (short) (in1.getShort() & 0xFFFF);
				model.textureTriangleVertexIndices2[texIndex] = (short) (in1.getShort() & 0xFFFF);
				model.textureTriangleVertexIndices3[texIndex] = (short) (in1.getShort() & 0xFFFF);
			}

			if (type == 1)
			{
				model.textureTriangleVertexIndices1[texIndex] = (short) (in2.getShort() & 0xFFFF);
				model.textureTriangleVertexIndices2[texIndex] = (short) (in2.getShort() & 0xFFFF);
				model.textureTriangleVertexIndices3[texIndex] = (short) (in2.getShort() & 0xFFFF);
				model.unknown1[texIndex] = (short) (in3.getShort() & 0xFFFF);
				model.unknown2[texIndex] = (short) (in3.getShort() & 0xFFFF);
				model.unknown6[texIndex] = (short) (in3.getShort() & 0xFFFF);
				model.unknown3[texIndex] = (short) (in4.getShort() & 0xFFFF);
				model.unknown5[texIndex] = in5.get();
				model.unknown4[texIndex] = (short) (in6.getShort() & 0xFFFF);
			}

			if (type == 2)
			{
				model.textureTriangleVertexIndices1[texIndex] = (short) (in2.getShort() & 0xFFFF);
				model.textureTriangleVertexIndices2[texIndex] = (short) (in2.getShort() & 0xFFFF);
				model.textureTriangleVertexIndices3[texIndex] = (short) (in2.getShort() & 0xFFFF);
				model.unknown1[texIndex] = (short) (in3.getShort() & 0xFFFF);
				model.unknown2[texIndex] = (short) (in3.getShort() & 0xFFFF);
				model.unknown6[texIndex] = (short) (in3.getShort() & 0xFFFF);
				model.unknown3[texIndex] = (short) (in4.getShort() & 0xFFFF);
				model.unknown5[texIndex] = in5.get();
				model.unknown4[texIndex] = (short) (in6.getShort() & 0xFFFF);
				model.texturePrimaryColors[texIndex] = (short) (in6.getShort() & 0xFFFF);
			}

			if (type == 3)
			{
				model.textureTriangleVertexIndices1[texIndex] = (short) (in2.getShort() & 0xFFFF);
				model.textureTriangleVertexIndices2[texIndex] = (short) (in2.getShort() & 0xFFFF);
				model.textureTriangleVertexIndices3[texIndex] = (short) (in2.getShort() & 0xFFFF);
				model.unknown1[texIndex] = (short) (in3.getShort() & 0xFFFF);
				model.unknown2[texIndex] = (short) (in3.getShort() & 0xFFFF);
				model.unknown6[texIndex] = (short) (in3.getShort() & 0xFFFF);
				model.unknown3[texIndex] = (short) (in4.getShort() & 0xFFFF);
				model.unknown5[texIndex] = in5.get();
				model.unknown4[texIndex] = (short) (in6.getShort() & 0xFFFF);
			}
		}

		in1.position(position);
		vertexZOffset = in1.get() & 0xFF;
		if (vertexZOffset != 0)
		{
			in1.getShort();
			in1.getShort();
			in1.getShort();
			in1.getInt();
		}
	}

	private static void load2(ModelDefinition model, byte[] var1)
	{
		CacheBuffer in1 = new CacheBuffer(var1);
		CacheBuffer in2 = new CacheBuffer(var1);
		CacheBuffer in3 = new CacheBuffer(var1);
		CacheBuffer in4 = new CacheBuffer(var1);
		CacheBuffer in5 = new CacheBuffer(var1);
		in1.position(var1.length - 18);
		int var10 = in1.getShort() & 0xFFFF;
		int var11 = in1.getShort() & 0xFFFF;
		int var12 = in1.get() & 0xFF;
		int var13 = in1.get() & 0xFF;
		int var14 = in1.get() & 0xFF;
		int var30 = in1.get() & 0xFF;
		int var15 = in1.get() & 0xFF;
		int var28 = in1.get() & 0xFF;
		int var27 = in1.getShort() & 0xFFFF;
		int var20 = in1.getShort() & 0xFFFF;
		int var36 = in1.getShort() & 0xFFFF;
		int var23 = in1.getShort() & 0xFFFF;
		byte var16 = 0;
		int var46 = var16 + var10;
		int var24 = var46;
		var46 += var11;
		int var25 = var46;

		if (var14 == 255)
		{
			var46 += var11;
		}

		int var4 = var46;
		if (var15 == 1)
		{
			var46 += var11;
		}

		int var42 = var46;
		if (var13 == 1)
		{
			var46 += var11;
		}

		int var37 = var46;
		if (var28 == 1)
		{
			var46 += var10;
		}

		int var29 = var46;
		if (var30 == 1)
		{
			var46 += var11;
		}

		int var44 = var46;
		var46 += var23;
		int var17 = var46;
		var46 += var11 * 2;
		int var32 = var46;
		var46 += var12 * 6;
		int var34 = var46;
		var46 += var27;
		int var35 = var46;
		var46 += var20;
		model.textureTriangleCount = var12;

		if (var12 > 0)
		{
			model.textureRenderTypes = new byte[var12];
			model.textureTriangleVertexIndices1 = new short[var12];
			model.textureTriangleVertexIndices2 = new short[var12];
			model.textureTriangleVertexIndices3 = new short[var12];
		}

		boolean hasTextureCoordinates = var13 == 1;

		if (var14 != 255)
		{
			model.priority = (byte) var14;
		}

		in1.position(var16);
		in2.position(var34);
		in3.position(var35);
		in4.position(var46);
		in5.position(var37);
		int vX = 0;
		int vY = 0;
		int vZ = 0;

		int var6;
		int var7;
		int var8;
		int var18;
		int var31;
		for (var18 = 0; var18 < var10; ++var18)
		{

			var8 = in1.get() & 0xFF;
			var31 = 0;
			if ((var8 & 1) != 0)
			{
				var31 = in2.getSpecial1();
			}

			var6 = 0;
			if ((var8 & 2) != 0)
			{
				var6 = in3.getSpecial1();
			}

			var7 = 0;
			if ((var8 & 4) != 0)
			{
				var7 = in4.getSpecial1();
			}

			vX += var31;
			vY += var6;
			vZ += var7;

			ModelDefinition.Vertex vertex = new ModelDefinition.Vertex();
			vertex.x = vX;
			vertex.y = vY;
			vertex.z = vZ;
			vertex.index = var18;
			model.vertices.add(vertex);

			if (var28 == 1)
			{
				vertex.label = in5.get() & 0xFF;
			}
		}

		in1.position(var17);
		in2.position(var42);
		in3.position(var25);
		in4.position(var29);
		in5.position(var4);

		for (var18 = 0; var18 < var11; ++var18)
		{
			ModelDefinition.Face face = new ModelDefinition.Face();
			model.faces.add(face);

			face.color = in1.getShort() & 0xFFFF;
			if (var13 == 1)
			{
				var8 = in2.get() & 0xFF;
				if ((var8 & 1) == 1)
				{
					face.renderType = 1;
				}
				else
				{
					face.renderType = 0;
				}

				if ((var8 & 2) == 2)
				{
					face.textureCoordinates = (byte) (var8 >> 2);
					face.texture = face.color;
					face.color = 0x7f;
				}
				else
				{
					face.textureCoordinates = -1;
					face.texture = -1;
				}
			}

			if (var14 == 255)
			{
				face.priority = in3.get();
			}

			if (var30 == 1)
			{
				face.transparency = in4.get() & 0xff;
			}

			if (var15 == 1)
			{
				face.label = in5.get() & 0xFF;
			}
		}

		in1.position(var44);
		in2.position(var24);
		var18 = 0;
		var8 = 0;
		var31 = 0;
		var6 = 0;

		int var21;
		int var22;
		for (var7 = 0; var7 < var11; ++var7)
		{
			ModelDefinition.Face face = model.faces.get(var7);
			var22 = in2.get() & 0xFF;
			if (var22 == 1)
			{
				var18 = in1.getSpecial1() + var6;
				var8 = in1.getSpecial1() + var18;
				var31 = in1.getSpecial1() + var8;
				var6 = var31;
				face.a = model.vertices.get(var18);
				face.b = model.vertices.get(var8);
				face.c = model.vertices.get(var31);
			}

			if (var22 == 2)
			{
				var8 = var31;
				var31 = in1.getSpecial1() + var6;
				var6 = var31;
				face.a = model.vertices.get(var18);
				face.b = model.vertices.get(var8);
				face.c = model.vertices.get(var31);
			}

			if (var22 == 3)
			{
				var18 = var31;
				var31 = in1.getSpecial1() + var6;
				var6 = var31;
				face.a = model.vertices.get(var18);
				face.b = model.vertices.get(var8);
				face.c = model.vertices.get(var31);
			}

			if (var22 == 4)
			{
				var21 = var18;
				var18 = var8;
				var8 = var21;
				var31 = in1.getSpecial1() + var6;
				var6 = var31;
				face.a = model.vertices.get(var18);
				face.b = model.vertices.get(var21);
				face.c = model.vertices.get(var31);
			}
		}

		in1.position(var32);

		for (var7 = 0; var7 < var12; ++var7)
		{
			model.textureRenderTypes[var7] = 0;
			model.textureTriangleVertexIndices1[var7] = (short) (in1.getShort() & 0xFFFF);
			model.textureTriangleVertexIndices2[var7] = (short) (in1.getShort() & 0xFFFF);
			model.textureTriangleVertexIndices3[var7] = (short) (in1.getShort() & 0xFFFF);
		}

		if (hasTextureCoordinates)
		{
			for (var22 = 0; var22 < var11; ++var22)
			{
				ModelDefinition.Face face = model.faces.get(var22);
				var21 = face.textureCoordinates & 255;
				if (var21 != 255)
				{
					if ((model.textureTriangleVertexIndices1[var21] & '\uffff') == face.a.index &&
						(model.textureTriangleVertexIndices2[var21] & '\uffff') == face.b.index &&
						(model.textureTriangleVertexIndices3[var21] & '\uffff') == face.c.index)
					{
						face.textureCoordinates = -1;
					}
				}
			}
		}
	}
}
