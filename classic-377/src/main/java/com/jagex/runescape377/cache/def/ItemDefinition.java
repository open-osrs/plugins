package com.jagex.runescape377.cache.def;

import com.jagex.runescape377.cache.Archive;
import com.jagex.runescape377.cache.media.ImageRGB;
import com.jagex.runescape377.collection.Cache;
import com.jagex.runescape377.media.Rasterizer;
import com.jagex.runescape377.media.Rasterizer3D;
import com.jagex.runescape377.media.renderable.Model;
import com.jagex.runescape377.media.renderable.Renderable;
import com.jagex.runescape377.net.Buffer;

public class ItemDefinition
{

	public static int count;
	public static ItemDefinition cache[];
	public static Cache modelCache = new Cache(50);
	public static int offsets[];
	public static boolean memberServer = true;
	public static Cache rgbImageCache = new Cache(100);
	public static boolean aBoolean350 = true;
	public static int cacheIndex;
	public static Buffer buffer;
	public int primaryFemaleModel;
	public int modelOffsetX;
	public byte description[];
	public String name;
	public byte femaleTranslation;
	public int secondaryMaleModel;
	public int team;
	public int notedInfoId;
	public int primaryMaleHeadPiece;
	public String groundActions[];
	public int anInt339;
	public int modelOffsetY;
	public int destColors[];
	public int notedTemplateId;
	public int value;
	public String inventoryActions[];
	public int primaryMaleModel;
	public int ambience;
	public int secondaryFemaleModel;
	public int modelRotationY;
	public int groundScaleY;
	public int diffusion;
	public int modelRotationX;
	public int modelId;
	public int secondaryMaleHeadPiece;
	public int secondaryFemaleHeadPiece;
	public int id = -1;
	public int originalColours[];
	public int stackIds[];
	public int groundScaleX;
	public int tertiaryFemaleEquipmentModel;
	public int groundScaleZ;
	public int modelScale;
	public int tertiaryMaleEquipmentModel;
	public boolean stackable;
	public int anInt372;
	public int primaryFemaleHeadPiece;
	public int stackAmounts[];
	public boolean members;
	public byte maleTranslation;

	public static void dispose()
	{
		modelCache = null;
		rgbImageCache = null;
		offsets = null;
		cache = null;
		buffer = null;
	}

	public static ItemDefinition lookup(int id)
	{
		for (int i = 0; i < 10; i++)
		{
			if (cache[i].id == id)
			{
				return cache[i];
			}
		}

		cacheIndex = (cacheIndex + 1) % 10;
		ItemDefinition definition = cache[cacheIndex];
		buffer.currentPosition = offsets[id];
		definition.id = id;
		definition.reset();
		definition.decode(buffer);
		if (definition.notedTemplateId != -1)
		{
			definition.toNote();
		}
		if (!memberServer && definition.members)
		{
			definition.name = "Members Object";
			definition.description = "Login to a members' server to use this object.".getBytes();
			definition.groundActions = null;
			definition.inventoryActions = null;
			definition.team = 0;
		}
		return definition;
	}

	public static void load(Archive archive)
	{
		buffer = new Buffer(archive.getFile("obj.dat"));
		Buffer buffer = new Buffer(archive.getFile("obj.idx"));
		count = buffer.getUnsignedShortBE();
		offsets = new int[count];
		int index = 2;
		for (int i = 0; i < count; i++)
		{
			offsets[i] = index;
			index += buffer.getUnsignedShortBE();
		}

		cache = new ItemDefinition[10];
		for (int i = 0; i < 10; i++)
		{
			cache[i] = new ItemDefinition();
		}

	}

	public static ImageRGB sprite(int id, int stackSize, int backColour)
	{
		if (backColour == 0)
		{
			ImageRGB sprite = (ImageRGB) rgbImageCache.get(id);
			if (sprite != null && sprite.maxHeight != stackSize
				&& sprite.maxHeight != -1)
			{
				sprite.remove();
				sprite = null;
			}
			if (sprite != null)
			{
				return sprite;
			}
		}
		ItemDefinition definition = lookup(id);
		if (definition.stackIds == null)
		{
			stackSize = -1;
		}
		if (stackSize > 1)
		{
			int stackId = -1;
			for (int i = 0; i < 10; i++)
			{
				if (stackSize >= definition.stackAmounts[i] && definition.stackAmounts[i] != 0)
				{
					stackId = definition.stackIds[i];
				}
			}

			if (stackId != -1)
			{
				definition = lookup(stackId);
			}
		}
		Model model = definition.asGroundStack(1);
		if (model == null)
		{
			return null;
		}
		ImageRGB notedSprite = null;
		if (definition.notedTemplateId != -1)
		{
			notedSprite = sprite(definition.notedInfoId, 10, -1);
			if (notedSprite == null)
			{
				return null;
			}
		}
		ImageRGB rendered = new ImageRGB(32, 32);
		int centreX = Rasterizer3D.center_x;
		int centerY = Rasterizer3D.center_y;
		int lineOffsets[] = Rasterizer3D.lineOffsets;
		int pixels[] = Rasterizer.pixels;
		int width = Rasterizer.width;
		int height = Rasterizer.height;
		int topX = Rasterizer.topX;
		int bottomX = Rasterizer.bottomX;
		int topY = Rasterizer.topY;
		int bottomY = Rasterizer.bottomY;
		Rasterizer3D.notTextured = false;
		Rasterizer.createRasterizer(rendered.pixels, 32, 32);
		Rasterizer.drawFilledRectangle(0, 0, 32, 32, 0);
		Rasterizer3D.setDefaultBounds();
		int scale = definition.modelScale;
		if (backColour == -1)
		{
			scale = (int) (scale * 1.5D);
		}
		if (backColour > 0)
		{
			scale = (int) (scale * 1.04D);
		}
		int sin = Rasterizer3D.SINE[definition.modelRotationX] * scale >> 16;
		int cos = Rasterizer3D.COSINE[definition.modelRotationX] * scale >> 16;
		model.render(0, definition.modelRotationY, definition.anInt339, definition.modelRotationX, definition.modelOffsetX, sin
			+ ((Renderable) (model)).modelHeight / 2 + definition.modelOffsetY, cos
			+ definition.modelOffsetY);
		for (int x = 31; x >= 0; x--)
		{
			for (int y = 31; y >= 0; y--)
			{
				if (rendered.pixels[x + y * 32] == 0)
				{
					if (x > 0 && rendered.pixels[(x - 1) + y * 32] > 1)
					{
						rendered.pixels[x + y * 32] = 1;
					}
					else if (y > 0 && rendered.pixels[x + (y - 1) * 32] > 1)
					{
						rendered.pixels[x + y * 32] = 1;
					}
					else if (x < 31 && rendered.pixels[x + 1 + y * 32] > 1)
					{
						rendered.pixels[x + y * 32] = 1;
					}
					else if (y < 31 && rendered.pixels[x + (y + 1) * 32] > 1)
					{
						rendered.pixels[x + y * 32] = 1;
					}
				}
			}

		}

		if (backColour > 0)
		{
			for (int x = 31; x >= 0; x--)
			{
				for (int y = 31; y >= 0; y--)
				{
					if (rendered.pixels[x + y * 32] == 0)
					{
						if (x > 0 && rendered.pixels[(x - 1) + y * 32] == 1)
						{
							rendered.pixels[x + y * 32] = backColour;
						}
						else if (y > 0 && rendered.pixels[x + (y - 1) * 32] == 1)
						{
							rendered.pixels[x + y * 32] = backColour;
						}
						else if (x < 31 && rendered.pixels[x + 1 + y * 32] == 1)
						{
							rendered.pixels[x + y * 32] = backColour;
						}
						else if (y < 31 && rendered.pixels[x + (y + 1) * 32] == 1)
						{
							rendered.pixels[x + y * 32] = backColour;
						}
					}
				}

			}

		}
		else if (backColour == 0)
		{
			for (int x = 31; x >= 0; x--)
			{
				for (int y = 31; y >= 0; y--)
				{
					if (rendered.pixels[x + y * 32] == 0 && x > 0 && y > 0
						&& rendered.pixels[(x - 1) + (y - 1) * 32] > 0)
					{
						rendered.pixels[x + y * 32] = 0x302020;
					}
				}

			}

		}
		if (definition.notedTemplateId != -1)
		{
			int resizeWidth = notedSprite.maxWidth;
			int resizeHeight = notedSprite.maxHeight;
			notedSprite.maxWidth = 32;
			notedSprite.maxHeight = 32;
			notedSprite.drawImage(0, 0);
			notedSprite.maxWidth = resizeWidth;
			notedSprite.maxHeight = resizeHeight;
		}
		if (backColour == 0)
		{
			rgbImageCache.put(rendered, id);
		}
		Rasterizer.createRasterizer(pixels, width, height);
		Rasterizer.setCoordinates(topY, topX, bottomY, bottomX);
		Rasterizer3D.center_x = centreX;
		Rasterizer3D.center_y = centerY;
		Rasterizer3D.lineOffsets = lineOffsets;
		Rasterizer3D.notTextured = true;
		if (definition.stackable)
		{
			rendered.maxWidth = 33;
		}
		else
		{
			rendered.maxWidth = 32;
		}
		rendered.maxHeight = stackSize;
		return rendered;
	}

	public void reset()
	{
		modelId = 0;
		name = null;
		description = null;
		originalColours = null;
		destColors = null;
		modelScale = 2000;
		modelRotationX = 0;
		modelRotationY = 0;
		anInt339 = 0;
		modelOffsetX = 0;
		modelOffsetY = 0;
		anInt372 = -1;
		stackable = false;
		value = 1;
		members = false;
		groundActions = null;
		inventoryActions = null;
		primaryMaleModel = -1;
		secondaryMaleModel = -1;
		maleTranslation = 0;
		primaryFemaleModel = -1;
		secondaryFemaleModel = -1;
		femaleTranslation = 0;
		tertiaryMaleEquipmentModel = -1;
		tertiaryFemaleEquipmentModel = -1;
		primaryMaleHeadPiece = -1;
		secondaryMaleHeadPiece = -1;
		primaryFemaleHeadPiece = -1;
		secondaryFemaleHeadPiece = -1;
		stackIds = null;
		stackAmounts = null;
		notedInfoId = -1;
		notedTemplateId = -1;
		groundScaleX = 128;
		groundScaleY = 128;
		groundScaleZ = 128;
		ambience = 0;
		diffusion = 0;
		team = 0;
	}

	public boolean headPieceReady(int gender)
	{
		int primary = primaryMaleHeadPiece;
		int secondary = secondaryMaleHeadPiece;
		if (gender == 1)
		{
			primary = primaryFemaleHeadPiece;
			secondary = secondaryFemaleHeadPiece;
		}
		if (primary == -1)
		{
			return true;
		}
		boolean ready = true;
		if (!Model.loaded(primary))
		{
			ready = false;
		}
		if (secondary != -1 && !Model.loaded(secondary))
		{
			ready = false;
		}
		return ready;
	}

	public Model asEquipment(int gender)
	{
		int primaryId = primaryMaleModel;
		int secondaryId = secondaryMaleModel;
		int tertiaryId = tertiaryMaleEquipmentModel;
		if (gender == 1)
		{
			primaryId = primaryFemaleModel;
			secondaryId = secondaryFemaleModel;
			tertiaryId = tertiaryFemaleEquipmentModel;
		}
		if (primaryId == -1)
		{
			return null;
		}
		Model primary = Model.getModel(primaryId);
		if (secondaryId != -1)
		{
			if (tertiaryId == -1)
			{
				Model secondary = Model.getModel(secondaryId);
				Model parts[] = {primary, secondary};
				primary = new Model(2, parts);
			}
			else
			{
				Model secondary = Model.getModel(secondaryId);
				Model tertiary = Model.getModel(tertiaryId);
				Model parts[] = {primary,
					secondary, tertiary};
				primary = new Model(3, parts);
			}
		}
		if (gender == 0 && maleTranslation != 0)
		{
			primary.translate(0, 0, maleTranslation);
		}
		if (gender == 1 && femaleTranslation != 0)
		{
			primary.translate(0, 0, femaleTranslation);
		}
		if (originalColours != null)
		{
			for (int color = 0; color < originalColours.length; color++)
			{
				primary.replaceColor(originalColours[color], destColors[color]);
			}

		}
		return primary;
	}

	public void toNote()
	{
		ItemDefinition graphics = lookup(notedTemplateId);
		modelId = graphics.modelId;
		modelScale = graphics.modelScale;
		modelRotationX = graphics.modelRotationX;
		modelRotationY = graphics.modelRotationY;
		anInt339 = graphics.anInt339;
		modelOffsetX = graphics.modelOffsetX;
		modelOffsetY = graphics.modelOffsetY;
		originalColours = graphics.originalColours;
		destColors = graphics.destColors;
		ItemDefinition info = lookup(notedInfoId);
		name = info.name;
		members = info.members;
		value = info.value;
		String prefix = "a";
		char firstChar = info.name.charAt(0);
		if (firstChar == 'A' || firstChar == 'E' || firstChar == 'I' || firstChar == 'O' || firstChar == 'U')
		{
			prefix = "an";
		}
		description = ("Swap this note at any bank for " + prefix + " " + info.name + ".").getBytes();
		stackable = true;
	}

	public boolean equipmentReady(int gender)
	{
		int primary = primaryMaleModel;
		int secondary = secondaryMaleModel;
		int tertiary = tertiaryMaleEquipmentModel;
		if (gender == 1)
		{
			primary = primaryFemaleModel;
			secondary = secondaryFemaleModel;
			tertiary = tertiaryFemaleEquipmentModel;
		}
		if (primary == -1)
		{
			return true;
		}
		boolean ready = true;
		if (!Model.loaded(primary))
		{
			ready = false;
		}
		if (secondary != -1 && !Model.loaded(secondary))
		{
			ready = false;
		}
		if (tertiary != -1 && !Model.loaded(tertiary))
		{
			ready = false;
		}
		return ready;
	}

	public Model asStack(int stackSize)
	{
		if (stackIds != null && stackSize > 1)
		{
			int id = -1;
			for (int i = 0; i < 10; i++)
			{
				if (stackSize >= stackAmounts[i] && stackAmounts[i] != 0)
				{
					id = stackIds[i];
				}
			}

			if (id != -1)
			{
				return lookup(id).asStack(1);
			}
		}
		Model model = Model.getModel(modelId);
		if (model == null)
		{
			return null;
		}
		if (originalColours != null)
		{
			for (int i = 0; i < originalColours.length; i++)
			{
				model.replaceColor(originalColours[i], destColors[i]);
			}

		}
		return model;
	}

	public void decode(Buffer buffer)
	{
		while (true)
		{
			int opcode = buffer.getUnsignedByte();
			if (opcode == 0)
			{
				return;
			}
			if (opcode == 1)
			{
				modelId = buffer.getUnsignedShortBE();
			}
			else if (opcode == 2)
			{
				name = buffer.getString();
			}
			else if (opcode == 3)
			{
				description = buffer.getStringBytes();
			}
			else if (opcode == 4)
			{
				modelScale = buffer.getUnsignedShortBE();
			}
			else if (opcode == 5)
			{
				modelRotationX = buffer.getUnsignedShortBE();
			}
			else if (opcode == 6)
			{
				modelRotationY = buffer.getUnsignedShortBE();
			}
			else if (opcode == 7)
			{
				modelOffsetX = buffer.getUnsignedShortBE();
				if (modelOffsetX > 32767)
				{
					modelOffsetX -= 0x10000;
				}
			}
			else if (opcode == 8)
			{
				modelOffsetY = buffer.getUnsignedShortBE();
				if (modelOffsetY > 32767)
				{
					modelOffsetY -= 0x10000;
				}
			}
			else if (opcode == 10)
			{
				buffer.getUnsignedShortBE(); // Dummy
			}
			else if (opcode == 11)
			{
				stackable = true;
			}
			else if (opcode == 12)
			{
				value = buffer.getIntBE();
			}
			else if (opcode == 16)
			{
				members = true;
			}
			else if (opcode == 23)
			{
				primaryMaleModel = buffer.getUnsignedShortBE();
				maleTranslation = buffer.getByte();
			}
			else if (opcode == 24)
			{
				secondaryMaleModel = buffer.getUnsignedShortBE();
			}
			else if (opcode == 25)
			{
				primaryFemaleModel = buffer.getUnsignedShortBE();
				femaleTranslation = buffer.getByte();
			}
			else if (opcode == 26)
			{
				secondaryFemaleModel = buffer.getUnsignedShortBE();
			}
			else if (opcode >= 30 && opcode < 35)
			{
				if (groundActions == null)
				{
					groundActions = new String[5];
				}
				groundActions[opcode - 30] = buffer.getString();
				if (groundActions[opcode - 30].equalsIgnoreCase("hidden"))
				{
					groundActions[opcode - 30] = null;
				}
			}
			else if (opcode >= 35 && opcode < 40)
			{
				if (inventoryActions == null)
				{
					inventoryActions = new String[5];
				}
				inventoryActions[opcode - 35] = buffer.getString();
			}
			else if (opcode == 40)
			{
				int colorCount = buffer.getUnsignedByte();
				originalColours = new int[colorCount];
				destColors = new int[colorCount];
				for (int k = 0; k < colorCount; k++)
				{
					originalColours[k] = buffer.getUnsignedShortBE();
					destColors[k] = buffer.getUnsignedShortBE();
				}

			}
			else if (opcode == 78)
			{
				tertiaryMaleEquipmentModel = buffer.getUnsignedShortBE();
			}
			else if (opcode == 79)
			{
				tertiaryFemaleEquipmentModel = buffer.getUnsignedShortBE();
			}
			else if (opcode == 90)
			{
				primaryMaleHeadPiece = buffer.getUnsignedShortBE();
			}
			else if (opcode == 91)
			{
				primaryFemaleHeadPiece = buffer.getUnsignedShortBE();
			}
			else if (opcode == 92)
			{
				secondaryMaleHeadPiece = buffer.getUnsignedShortBE();
			}
			else if (opcode == 93)
			{
				secondaryFemaleHeadPiece = buffer.getUnsignedShortBE();
			}
			else if (opcode == 95)
			{
				anInt339 = buffer.getUnsignedShortBE();
			}
			else if (opcode == 97)
			{
				notedInfoId = buffer.getUnsignedShortBE();
			}
			else if (opcode == 98)
			{
				notedTemplateId = buffer.getUnsignedShortBE();
			}
			else if (opcode >= 100 && opcode < 110)
			{
				if (stackIds == null)
				{
					stackIds = new int[10];
					stackAmounts = new int[10];
				}
				stackIds[opcode - 100] = buffer.getUnsignedShortBE();
				stackAmounts[opcode - 100] = buffer.getUnsignedShortBE();
			}
			else if (opcode == 110)
			{
				groundScaleX = buffer.getUnsignedShortBE();
			}
			else if (opcode == 111)
			{
				groundScaleY = buffer.getUnsignedShortBE();
			}
			else if (opcode == 112)
			{
				groundScaleZ = buffer.getUnsignedShortBE();
			}
			else if (opcode == 113)
			{
				ambience = buffer.getByte();
			}
			else if (opcode == 114)
			{
				diffusion = buffer.getByte() * 5;
			}
			else if (opcode == 115)
			{
				team = buffer.getUnsignedByte();
			}
		}
	}

	public Model asHeadPiece(int gender)
	{
		int primaryId = primaryMaleHeadPiece;
		int secondaryId = secondaryMaleHeadPiece;
		if (gender == 1)
		{
			primaryId = primaryFemaleHeadPiece;
			secondaryId = secondaryFemaleHeadPiece;
		}
		if (primaryId == -1)
		{
			return null;
		}
		Model primary = Model.getModel(primaryId);
		if (secondaryId != -1)
		{
			Model secondary = Model.getModel(secondaryId);
			primary = new Model(2, new Model[]{primary, secondary});
		}
		if (originalColours != null)
		{
			for (int index = 0; index < originalColours.length; index++)
			{
				primary.replaceColor(originalColours[index], destColors[index]);
			}

		}
		return primary;
	}

	public Model asGroundStack(int amount)
	{
		if (stackIds != null && amount > 1)
		{
			int id = -1;
			for (int i = 0; i < 10; i++)
			{
				if (amount >= stackAmounts[i] && stackAmounts[i] != 0)
				{
					id = stackIds[i];
				}
			}

			if (id != -1)
			{
				return lookup(id).asGroundStack(1);
			}
		}
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
		if (groundScaleX != 128 || groundScaleY != 128 || groundScaleZ != 128)
		{
			model.scaleT(groundScaleY, groundScaleZ, 9, groundScaleX);
		}
		if (originalColours != null)
		{
			for (int l = 0; l < originalColours.length; l++)
			{
				model.replaceColor(originalColours[l], destColors[l]);
			}

		}
		model.applyLighting(64 + ambience, 768 + diffusion, -50, -10, -50, true);
		model.singleTile = true;
		modelCache.put(model, id);
		return model;
	}


}
