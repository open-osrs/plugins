package com.jagex.runescape377.media.renderable.actor;

import com.jagex.runescape377.Game;
import com.jagex.runescape377.cache.def.ActorDefinition;
import com.jagex.runescape377.cache.def.ItemDefinition;
import com.jagex.runescape377.cache.media.AnimationSequence;
import com.jagex.runescape377.cache.media.IdentityKit;
import com.jagex.runescape377.cache.media.SpotAnimation;
import com.jagex.runescape377.collection.Cache;
import com.jagex.runescape377.media.Animation;
import com.jagex.runescape377.media.renderable.Model;
import com.jagex.runescape377.net.Buffer;
import com.jagex.runescape377.util.TextUtils;

public class Player extends Actor
{

	public static Cache modelCache = new Cache(260);
	public int anInt1743;
	public int drawHeight;
	public int anInt1745;
	public Model playerModel;
	public int headIcon = -1;
	public int drawHeight2;
	public String playerName;
	public int[] appearance = new int[12];
	public int combatLevel;
	public int isSkulled = -1;
	public ActorDefinition npcDefinition;
	public boolean visible = false;
	public int skillLevel;
	public int[] appearanceColors = new int[5];
	public boolean preventRotation = false;
	public int objectAppearanceStartTick;
	public int objectAppearanceEndTick;
	public int teamId;
	public int anInt1768;
	public int anInt1769;
	public int anInt1770;
	public int anInt1771;
	private long cachedModel = -1L;
	private long appearanceHash;
	private int gender;

	public Model getHeadModel()
	{
		if (!visible)
		{
			return null;
		}
		if (npcDefinition != null)
		{
			return npcDefinition.getHeadModel();
		}
		boolean cached = false;
		for (int index = 0; index < 12; index++)
		{
			int appearanceId = appearance[index];
			if (appearanceId >= 256 && appearanceId < 512 && !IdentityKit.cache[appearanceId - 256].isHeadModelCached())
			{
				cached = true;
			}
			if (appearanceId >= 512 && !ItemDefinition.lookup(appearanceId - 512).headPieceReady(gender))
			{
				cached = true;
			}
		}

		if (cached)
		{
			return null;
		}
		Model[] headModels = new Model[12];
		int headModelsOffset = 0;
		for (int modelIndex = 0; modelIndex < 12; modelIndex++)
		{
			int appearanceId = appearance[modelIndex];
			if (appearanceId >= 256 && appearanceId < 512)
			{
				Model subModel = IdentityKit.cache[appearanceId - 256].getHeadModel();
				if (subModel != null)
				{
					headModels[headModelsOffset++] = subModel;
				}
			}
			if (appearanceId >= 512)
			{
				Model subModel = ItemDefinition.lookup(appearanceId - 512).asHeadPiece(gender);
				if (subModel != null)
				{
					headModels[headModelsOffset++] = subModel;
				}
			}
		}

		Model headModel = new Model(headModelsOffset, headModels);
		for (int index = 0; index < 5; index++)
		{
			if (appearanceColors[index] != 0)
			{
				headModel.replaceColor(Game.playerColours[index][0], Game.playerColours[index][appearanceColors[index]]);
				if (index == 1)
				{
					headModel.replaceColor(Game.SKIN_COLOURS[0], Game.SKIN_COLOURS[appearanceColors[index]]);
				}
			}
		}

		return headModel;
	}

	public Model getAnimatedModel()
	{
		if (npcDefinition != null)
		{
			int frame = -1;
			if (super.emoteAnimation >= 0 && super.animationDelay == 0)
			{
				frame = AnimationSequence.animations[super.emoteAnimation].getPrimaryFrame[super.displayedEmoteFrames];
			}
			else if (super.movementAnimation >= 0)
			{
				frame = AnimationSequence.animations[super.movementAnimation].getPrimaryFrame[super.displayedMovementFrames];
			}
			Model model = npcDefinition.getChildModel(frame, -1, null);
			return model;
		}
		long hash = appearanceHash;
		int primaryFrame = -1;
		int secondaryFrame = -1;
		int shieldModel = -1;
		int weaponModel = -1;
		if (super.emoteAnimation >= 0 && super.animationDelay == 0)
		{
			AnimationSequence emote = AnimationSequence.animations[super.emoteAnimation];
			primaryFrame = emote.getPrimaryFrame[super.displayedEmoteFrames];
			if (super.movementAnimation >= 0 && super.movementAnimation != super.idleAnimation)
			{
				secondaryFrame = AnimationSequence.animations[super.movementAnimation].getPrimaryFrame[super.displayedMovementFrames];
			}
			if (emote.getPlayerShieldDelta >= 0)
			{
				shieldModel = emote.getPlayerShieldDelta;
				hash += shieldModel - appearance[5] << 8;
			}
			if (emote.getPlayerWeaponDelta >= 0)
			{
				weaponModel = emote.getPlayerWeaponDelta;
				hash += weaponModel - appearance[3] << 16;
			}
		}
		else if (super.movementAnimation >= 0)
		{
			primaryFrame = AnimationSequence.animations[super.movementAnimation].getPrimaryFrame[super.displayedMovementFrames];
		}
		Model model = (Model) modelCache.get(hash);
		if (model == null)
		{
			boolean invalid = false;
			for (int bodyPart = 0; bodyPart < 12; bodyPart++)
			{
				int appearanceModel = appearance[bodyPart];
				if (weaponModel >= 0 && bodyPart == 3)
				{
					appearanceModel = weaponModel;
				}
				if (shieldModel >= 0 && bodyPart == 5)
				{
					appearanceModel = shieldModel;
				}
				if (appearanceModel >= 256 && appearanceModel < 512 && !IdentityKit.cache[appearanceModel - 256].isBodyModelCached())
				{
					invalid = true;
				}
				if (appearanceModel >= 512 && !ItemDefinition.lookup(appearanceModel - 512).equipmentReady(gender))
				{
					invalid = true;
				}
			}

			if (invalid)
			{
				if (cachedModel != -1L)
				{
					model = (Model) modelCache.get(cachedModel);
				}
				if (model == null)
				{
					return null;
				}
			}
		}
		if (model == null)
		{
			Model models[] = new Model[12];
			int count = 0;
			for (int index = 0; index < 12; index++)
			{
				int part = appearance[index];
				if (weaponModel >= 0 && index == 3)
				{
					part = weaponModel;
				}
				if (shieldModel >= 0 && index == 5)
				{
					part = shieldModel;
				}
				if (part >= 256 && part < 512)
				{
					Model bodyModel = IdentityKit.cache[part - 256].getBodyModel();
					if (bodyModel != null)
					{
						models[count++] = bodyModel;
					}
				}
				if (part >= 512)
				{
					Model equipment = ItemDefinition.lookup(part - 512).asEquipment(gender);
					if (equipment != null)
					{
						models[count++] = equipment;
					}
				}
			}

			model = new Model(count, models);
			for (int part = 0; part < 5; part++)
			{
				if (appearanceColors[part] != 0)
				{
					model.replaceColor(Game.playerColours[part][0], Game.playerColours[part][appearanceColors[part]]);
					if (part == 1)
					{
						model.replaceColor(Game.SKIN_COLOURS[0], Game.SKIN_COLOURS[appearanceColors[part]]);
					}
				}
			}

			model.createBones();
			model.applyLighting(64, 850, -30, -50, -30, true);
			modelCache.put(model, hash);
			cachedModel = hash;
		}
		if (preventRotation)
		{
			return model;
		}
		Model empty = Model.EMPTY_MODEL;
		empty.replaceWithModel(model, Animation.exists(primaryFrame) & Animation.exists(secondaryFrame));
		if (primaryFrame != -1 && secondaryFrame != -1)
		{
			empty.mixAnimationFrames(secondaryFrame, 0, primaryFrame, AnimationSequence.animations[super.emoteAnimation].flowControl);
		}
		else if (primaryFrame != -1)
		{
			empty.applyTransform(primaryFrame);
		}
		empty.calculateDiagonals();
		empty.triangleSkin = null;
		empty.vectorSkin = null;
		return empty;
	}

	@Override
	public boolean isVisible()
	{
		return visible;
	}

	@Override
	public Model getRotatedModel()
	{
		if (!visible)
		{
			return null;
		}
		Model appearanceModel = getAnimatedModel();
		if (appearanceModel == null)
		{
			return null;
		}
		modelHeight = appearanceModel.modelHeight;
		appearanceModel.singleTile = true;
		if (preventRotation)
		{
			return appearanceModel;
		}
		if (super.graphic != -1 && super.currentAnimation != -1)
		{
			SpotAnimation spotAnimation = SpotAnimation.cache[super.graphic];
			Model spotAnimationModel = spotAnimation.getModel();
			if (spotAnimationModel != null)
			{
				Model spotAnimationModel2 = new Model(true, spotAnimationModel, Animation.exists(super.currentAnimation));
				spotAnimationModel2.translate(0, 0, -super.spotGraphicHeight);
				spotAnimationModel2.createBones();
				spotAnimationModel2.applyTransform(spotAnimation.sequences.getPrimaryFrame[super.currentAnimation]);
				spotAnimationModel2.triangleSkin = null;
				spotAnimationModel2.vectorSkin = null;
				if (spotAnimation.resizeXY != 128 || spotAnimation.resizeZ != 128)
				{
					spotAnimationModel2.scaleT(spotAnimation.resizeZ, spotAnimation.resizeXY, 9, spotAnimation.resizeXY);
				}
				spotAnimationModel2.applyLighting(64 + spotAnimation.modelLightFalloff, 850 + spotAnimation.modelLightAmbient, -30, -50, -30, true);
				Model[] models = {appearanceModel, spotAnimationModel2};
				appearanceModel = new Model(models);
			}
		}
		if (playerModel != null)
		{
			if (Game.pulseCycle >= objectAppearanceEndTick)
			{
				playerModel = null;
			}
			if (Game.pulseCycle >= objectAppearanceStartTick && Game.pulseCycle < objectAppearanceEndTick)
			{
				Model model = playerModel;
				model.translate(anInt1743 - super.worldX, anInt1745 - super.worldY, drawHeight - drawHeight2);
				if (super.nextStepOrientation == 512)
				{
					model.rotate90Degrees();
					model.rotate90Degrees();
					model.rotate90Degrees();
				}
				else if (super.nextStepOrientation == 1024)
				{
					model.rotate90Degrees();
					model.rotate90Degrees();
				}
				else if (super.nextStepOrientation == 1536)
				{
					model.rotate90Degrees();
				}
				Model[] models = {appearanceModel, model};
				appearanceModel = new Model(models);
				if (super.nextStepOrientation == 512)
				{
					model.rotate90Degrees();
				}
				else if (super.nextStepOrientation == 1024)
				{
					model.rotate90Degrees();
					model.rotate90Degrees();
				}
				else if (super.nextStepOrientation == 1536)
				{
					model.rotate90Degrees();
					model.rotate90Degrees();
					model.rotate90Degrees();
				}
				model.translate(super.worldX - anInt1743, super.worldY - anInt1745, drawHeight2 - drawHeight);
			}
		}
		appearanceModel.singleTile = true;
		return appearanceModel;
	}

	public void updateAppearance(Buffer buffer)
	{
		buffer.currentPosition = 0;
		gender = buffer.getUnsignedByte();
		isSkulled = buffer.getByte();
		headIcon = buffer.getByte();
		npcDefinition = null;
		teamId = 0;
		for (int index = 0; index < 12; index++)
		{
			int upperByte = buffer.getUnsignedByte();
			if (upperByte == 0)
			{
				appearance[index] = 0;
				continue;
			}
			int lowerByte = buffer.getUnsignedByte();
			appearance[index] = (upperByte << 8) + lowerByte;
			if (index == 0 && appearance[0] == 65535)
			{
				npcDefinition = ActorDefinition.getDefinition(buffer.getUnsignedShortBE());
				break;
			}
			if (appearance[index] >= 512 && appearance[index] - 512 < ItemDefinition.count)
			{
				int itemTeam = ItemDefinition.lookup(appearance[index] - 512).team;
				if (itemTeam != 0)
				{
					teamId = itemTeam;
				}
			}
		}

		for (int l = 0; l < 5; l++)
		{
			int j1 = buffer.getUnsignedByte();
			if (j1 < 0 || j1 >= Game.playerColours[l].length)
			{
				j1 = 0;
			}
			appearanceColors[l] = j1;
		}

		super.idleAnimation = buffer.getUnsignedShortBE();
		if (super.idleAnimation == 65535)
		{
			super.idleAnimation = -1;
		}
		super.standTurnAnimationId = buffer.getUnsignedShortBE();
		if (super.standTurnAnimationId == 65535)
		{
			super.standTurnAnimationId = -1;
		}
		super.walkAnimationId = buffer.getUnsignedShortBE();
		if (super.walkAnimationId == 65535)
		{
			super.walkAnimationId = -1;
		}
		super.turnAroundAnimationId = buffer.getUnsignedShortBE();
		if (super.turnAroundAnimationId == 65535)
		{
			super.turnAroundAnimationId = -1;
		}
		super.turnRightAnimationId = buffer.getUnsignedShortBE();
		if (super.turnRightAnimationId == 65535)
		{
			super.turnRightAnimationId = -1;
		}
		super.turnLeftAnimationId = buffer.getUnsignedShortBE();
		if (super.turnLeftAnimationId == 65535)
		{
			super.turnLeftAnimationId = -1;
		}
		super.runAnimationId = buffer.getUnsignedShortBE();
		if (super.runAnimationId == 65535)
		{
			super.runAnimationId = -1;
		}
		playerName = TextUtils.formatName(TextUtils.longToName(buffer.getLongBE()));
		combatLevel = buffer.getUnsignedByte();
		skillLevel = buffer.getUnsignedShortBE();
		visible = true;
		appearanceHash = 0L;
		int k1 = appearance[5];
		int i2 = appearance[9];
		appearance[5] = i2;
		appearance[9] = k1;
		for (int j2 = 0; j2 < 12; j2++)
		{
			appearanceHash <<= 4;
			if (appearance[j2] >= 256)
			{
				appearanceHash += appearance[j2] - 256;
			}
		}

		if (appearance[0] >= 256)
		{
			appearanceHash += appearance[0] - 256 >> 4;
		}
		if (appearance[1] >= 256)
		{
			appearanceHash += appearance[1] - 256 >> 8;
		}
		appearance[5] = k1;
		appearance[9] = i2;
		for (int k2 = 0; k2 < 5; k2++)
		{
			appearanceHash <<= 3;
			appearanceHash += appearanceColors[k2];
		}

		appearanceHash <<= 1;
		appearanceHash += gender;
	}


}
