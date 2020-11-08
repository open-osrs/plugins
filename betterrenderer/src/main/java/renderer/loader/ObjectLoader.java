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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import renderer.cache.CacheSystem;
import renderer.model.ModelDefinition;
import renderer.util.CacheBuffer;
import renderer.util.Colors;
import renderer.world.LocationType;
import renderer.world.ObjectDefinition;

public class ObjectLoader
{
	public static ObjectDefinition load(int id, byte[] b)
	{
		ObjectDefinition def = new ObjectDefinition();
		CacheBuffer is = new CacheBuffer(b);

		while (true)
		{
			int opcode = is.get() & 0xFF;
			if (opcode == 0)
			{
				break;
			}

			processOp(opcode, def, is);
		}

		def.id = id;

		return def;
	}

	private static void processOp(final int opcode, ObjectDefinition def, CacheBuffer is)
	{
		switch (opcode)
		{
			case 1:
				int length = is.get() & 0xFF;
				if (length > 0)
				{
					if (def.typeModels != null || def.models != null)
					{
						for (int index = 0; index < length; ++index)
						{
							is.getShort();
							is.get();
						}
					}
					else
					{
						def.typeModels = new EnumMap<>(LocationType.class);

						for (int i = 0; i < length; ++i)
						{
							ModelDefinition model = CacheSystem.getModelDefinition(is.getShort() & 0xFFFF);
							LocationType type = LocationType.values()[is.get() & 0xFF];
							def.typeModels.put(type, model);
						}
					}
				}
				break;
			case 2:
				def.name = is.getString();
				break;
			case 5:
				length = is.get() & 0xFF;
				if (length > 0)
				{
					if (def.typeModels != null || def.models != null)
					{
						for (int i = 0; i < length; ++i)
						{
							is.getShort();
						}
					}
					else
					{
						def.typeModels = null;
						def.models = new ArrayList<>();

						for (int i = 0; i < length; ++i)
						{
							def.models.add(CacheSystem.getModelDefinition(is.getShort() & 0xFFFF));
						}
					}
				}
				break;
			case 14:
				def.sizeX = is.get() & 0xFF;
				break;
			case 15:
				def.sizeY = is.get() & 0xFF;
				break;
			case 17:
				def.interactType = 0;
				def.blocksProjectile = false;
				break;
			case 18:
				def.blocksProjectile = false;
				break;
			case 19:
				def.wall = is.get() & 0xFF;
				break;
			case 21:
				def.contouredGround = 0;
				break;
			case 22:
				def.mergeNormals = true;
				break;
			case 23:
				def.unknown1 = true;
				break;
			case 24:
				int animationId = is.getShort() & 0xFFFF;
				if (animationId != 0xFFFF)
				{
					def.animation = CacheSystem.getAnimationDefinition(animationId);
				}
				break;
			case 27:
				def.interactType = 1;
				break;
			case 28:
				def.decorationOffset = is.get() & 0xFF;
				break;
			case 29:
				def.ambient = is.get();
				break;
			case 39:
				def.contrast = is.get() * 25;
				break;
			case 30:
				def.actions[0] = is.getString();
				break;
			case 31:
				def.actions[1] = is.getString();
				break;
			case 32:
				def.actions[2] = is.getString();
				break;
			case 33:
				def.actions[3] = is.getString();
				break;
			case 34:
				def.actions[4] = is.getString();
				break;
			case 40:
				length = is.get() & 0xFF;
				for (int index = 0; index < length; ++index)
				{
					def.colorSubstitutions.put(Colors.hsl(is.getShort()), Colors.hsl(is.getShort()));
				}
				break;
			case 41:
				length = is.get() & 0xFF;
				for (int index = 0; index < length; ++index)
				{
					def.textureSubstitutions.put(is.getShort(), is.getShort());
				}
				break;
			case 62:
				def.mirror = true;
				break;
			case 64:
				def.shadow = false;
				break;
			case 65:
				def.scaleX = is.getShort() & 0xFFFF;
				break;
			case 66:
				def.scaleZ = is.getShort() & 0xFFFF;
				break;
			case 67:
				def.scaleY = is.getShort() & 0xFFFF;
				break;
			case 68:
				def.mapSceneID = is.getShort() & 0xFFFF;
				break;
			case 69:
				def.blockingMask = is.get();
				break;
			case 70:
				def.offsetX = is.getShort();
				break;
			case 71:
				def.offsetZ = is.getShort();
				break;
			case 72:
				def.offsetY = is.getShort();
				break;
			case 73:
				def.obstructsGround = true;
				break;
			case 74:
				def.hollow = true;
				break;
			case 75:
				def.supportsItems = is.get() & 0xFF;
				break;
			case 77:
				int varpID = is.getShort() & 0xFFFF;
				if (varpID == 0xFFFF)
				{
					varpID = -1;
				}
				def.varbit = varpID;
				int configId = is.getShort() & 0xFFFF;
				if (configId == 0xFFFF)
				{
					configId = -1;
				}
				def.varp = configId;
				length = is.get() & 0xFF;
				int[] configChangeDest = new int[length + 2];
				for (int index = 0; index <= length; ++index)
				{
					configChangeDest[index] = is.getShort() & 0xFFFF;
					if (0xFFFF == configChangeDest[index])
					{
						configChangeDest[index] = -1;
					}
				}
				configChangeDest[length + 1] = -1;
				def.configChangeDest = configChangeDest;
				break;
			case 78:
				def.ambientSound = is.getShort() & 0xFFFF;
				def.ambientSoundRadius = is.get() & 0xFF;
				break;
			case 79:
				def.ambientSoundMinLoopTime = is.getShort() & 0xFFFF;
				def.ambientSoundMaxLoopTime = is.getShort() & 0xFFFF;
				def.ambientSoundRadius = is.get() & 0xFF;
				length = is.get() & 0xFF;
				def.ambientSounds = new ArrayList<>(length);
				for (int i = 0; i < length; ++i)
				{
					def.ambientSounds.add(is.getShort() & 0xFFFF);
				}
				break;
			case 81:
				def.contouredGround = (is.get() & 0xFF) * 256;
				break;
			case 82:
				def.mapAreaId = is.getShort() & 0xFFFF;
				break;
			case 92:
				varpID = is.getShort() & 0xFFFF;
				if (varpID == 0xFFFF)
				{
					varpID = -1;
				}
				def.varbit = varpID;
				configId = is.getShort() & 0xFFFF;
				if (configId == 0xFFFF)
				{
					configId = -1;
				}
				def.varp = configId;
				int var = is.getShort() & 0xFFFF;
				if (var == 0xFFFF)
				{
					var = -1;
				}
				length = is.get() & 0xFF;
				configChangeDest = new int[length + 2];
				for (int index = 0; index <= length; ++index)
				{
					configChangeDest[index] = is.getShort() & 0xFFFF;
					if (0xFFFF == configChangeDest[index])
					{
						configChangeDest[index] = -1;
					}
				}
				configChangeDest[length + 1] = var;
				def.configChangeDest = configChangeDest;
				break;
			case 249:
				length = is.get() & 0xFF;
				Map<Integer, Object> params = new HashMap<>(length);
				for (int i = 0; i < length; i++)
				{
					boolean isString = (is.get() & 0xFF) == 1;
					int key = is.getMedium();
					Object value;

					if (isString)
					{
						value = is.getString();
					}
					else
					{
						value = is.getInt();
					}

					params.put(key, value);
				}
				def.params = params;
				break;
			default:
				throw new IllegalStateException("opcode " + opcode);
		}
	}
}
