package com.jagex.runescape377.cache.media;

import com.jagex.runescape377.cache.Archive;
import com.jagex.runescape377.media.Rasterizer;
import com.jagex.runescape377.net.Buffer;
import java.awt.Color;
import java.util.Random;

public class TypeFace extends Rasterizer
{

	private static int strikethroughColor = -1;
	private static int underlineColor = -1;
	private static int anInt3748 = 0;
	private static int defaultTextColor = 0;
	private static int opacity = 256;
	private static int defaultOpacity = 256;
	private static int defaultShadowColor = -1;
	private static int shadowColor = -1;
	private static int textColor = 0xff00ff;
	private static int whiteSpace = 0;
	private static String greaterThan = "gt";
	private static String lessThan = "lt";
	private static String euroSymbol = "euro";
	private static String startShadow = "shad=";
	private static String softHyphen = "shy";
	private static String startTrans = "trans=";
	private static String startUnderline = "u=";
	private static String startStrikethrough = "str=";
	private static String endColor = "/col";
	private static String endShadow = "/shad";
	private static String endTrans = "/trans";
	private static String endUnderline = "/u";
	private static String endStrikeThrough = "/str";
	private static String startDefaultUnderline = "u";
	private static String startDefaultStrikeThrough = "str";
	private static String startDefaultShadow = "shad";
	private static String startColor = "col=";
	private static String multiplicationSymbol = "times";
	private static String nonBreakingSpace = "nbsp";
	private static String image = "img=";
	private static String copyright = "copy";
	private static String lineBreak = "br";
	private static String registeredTrademark = "reg";
	// Todo: Clean up duplicate legacy methods, by checking if images are not loaded when they are needed instead
	public byte[][] characterPixels = new byte[256][];
	public int[] characterWidths = new int[256];
	public int[] characterHeights = new int[256];
	public int[] characterXOffsets = new int[256];
	public int[] characterYOffsets = new int[256];
	public int[] characterScreenWidths = new int[256];
	public int characterDefaultHeight;
	public Random random = new Random();
	private ImageRGB[] moderatorIcon = null;
	private int[] imageWidths;

	public TypeFace(boolean monospace, Archive archive, String archiveName)
	{
		Buffer dataBuffer = new Buffer(archive.getFile(archiveName + ".dat"));
		Buffer indexBuffer = new Buffer(archive.getFile("index.dat"));
		indexBuffer.currentPosition = dataBuffer.getUnsignedShortBE() + 4;
		int k = indexBuffer.getUnsignedByte();
		if (k > 0)
		{
			indexBuffer.currentPosition += 3 * (k - 1);
		}
		for (int character = 0; character < 256; character++)
		{
			characterXOffsets[character] = indexBuffer.getUnsignedByte();
			characterYOffsets[character] = indexBuffer.getUnsignedByte();
			int characterWidth = characterWidths[character] = indexBuffer.getUnsignedShortBE();
			int characterHeight = characterHeights[character] = indexBuffer.getUnsignedShortBE();
			int characterType = indexBuffer.getUnsignedByte();
			int characterSize = characterWidth * characterHeight;
			characterPixels[character] = new byte[characterSize];
			if (characterType == 0)
			{
				for (int pixel = 0; pixel < characterSize; pixel++)
				{
					characterPixels[character][pixel] = dataBuffer.getByte();
				}

			}
			else if (characterType == 1)
			{
				for (int characterX = 0; characterX < characterWidth; characterX++)
				{
					for (int characterY = 0; characterY < characterHeight; characterY++)
					{
						characterPixels[character][characterX + characterY * characterWidth] = dataBuffer.getByte();
					}

				}

			}
			if (characterHeight > characterDefaultHeight && character < 128)
			{
				characterDefaultHeight = characterHeight;
			}
			characterXOffsets[character] = 1;
			characterScreenWidths[character] = characterWidth + 2;
			int pixelCount = 0;
			for (int characterY = characterHeight / 7; characterY < characterHeight; characterY++)
			{
				pixelCount += characterPixels[character][characterY * characterWidth];
			}

			if (pixelCount <= characterHeight / 7)
			{
				characterScreenWidths[character]--;
				characterXOffsets[character] = 0;
			}
			pixelCount = 0;
			for (int characterY = characterHeight / 7; characterY < characterHeight; characterY++)
			{
				pixelCount += characterPixels[character][(characterWidth - 1) + characterY * characterWidth];
			}

			if (pixelCount <= characterHeight / 7)
			{
				characterScreenWidths[character]--;
			}
		}

		if (monospace)
		{
			characterScreenWidths[32] = characterScreenWidths[73];
		}
		else
		{
			characterScreenWidths[32] = characterScreenWidths[105];
		}
	}

	public void drawStringRight(String string, int x, int y, int colour)
	{
		drawString(string, x - getDisplayedWidth(string), y, colour);
	}

	public void drawStringLeft(String string, int x, int y, int colour)
	{
		drawString(string, x - getDisplayedWidth(string) / 2, y, colour);
	}

	public void drawStringCenter(String string, int x, int y, int colour, boolean shadowed)
	{
		drawShadowedString(string, x - getStringEffectWidth(string) / 2, y, shadowed, colour);
	}

	public int getStringEffectWidth(String string)
	{
		return getDisplayedWidth(string);
	}

	public final int getDisplayedWidth(String string)
	{
		if (string == null)
		{
			return 0;
		}
		int index = -1;
		int width = 0;
		int length = string.length();

		for (int idx = 0; idx < length; ++idx)
		{
			int character = string.charAt(idx);
			if (character == 60)
			{

				index = idx;
			}
			else
			{
				if (character == 62 && index != -1)
				{
					String effect = string.substring(index + 1, idx);
					index = -1;
					if (effect.equals(lessThan))
					{
						character = 60;
					}
					else if (effect.equals(greaterThan))
					{
						character = 62;
					}
					else if (effect.equals(nonBreakingSpace))
					{
						character = 160;
					}
					else if (effect.equals(softHyphen))
					{
						character = 173;
					}
					else if (effect.equals(multiplicationSymbol))
					{
						character = 215;
					}
					else if (effect.equals(euroSymbol))
					{
						character = 128;
					}
					else if (effect.equals(copyright))
					{
						character = 169;
					}
					else
					{
						if (!effect.equals(registeredTrademark))
						{
							if (effect.startsWith(image, 0))
							{
								try
								{
									int icon = Integer.parseInt(effect.substring(4));
									width += moderatorIcon[icon].maxWidth;
								}
								catch (Exception var10)
								{

								}
							}
							continue;
						}

						character = 174;
					}
				}
				if (character == '@' && idx + 4 < string.length() && string.charAt(idx + 4) == '@')
				{
					idx += 4;
					continue;
				}

				if (index == -1)
				{
					width += characterScreenWidths[character];
				}
			}
		}

		return width;

	}

	public void drawString(String string, int x, int y, int colour)
	{
		if (this.moderatorIcon != null)
		{
			this.drawString(string, x, y, colour, -1);

		}
		else
		{
			if (string == null)
			{
				return;
			}
			y -= characterDefaultHeight;
			for (int index = 0; index < string.length(); index++)
			{
				char character = string.charAt(index);
				if (character != ' ')
				{
					drawCharacterLegacy(characterPixels[character], x + characterXOffsets[character], y + characterYOffsets[character], characterWidths[character],
						characterHeights[character], colour);
				}
				x += characterScreenWidths[character];
			}
		}


	}

	public void drawCenteredStringWaveY(String string, int x, int y, int wave, int colour)
	{
		if (string == null)
		{
			return;
		}
		x -= getDisplayedWidth(string) / 2;
		y -= characterDefaultHeight;
		for (int index = 0; index < string.length(); index++)
		{
			char character = string.charAt(index);
			if (character != ' ')
			{
				drawCharacterLegacy(characterPixels[character], x + characterXOffsets[character], y + characterYOffsets[character]
					+ (int) (Math.sin(index / 2D + wave / 5D) * 5D), characterWidths[character], characterHeights[character], colour);
			}
			x += characterScreenWidths[character];
		}

	}

	public void drawCenteredString(String text, int x, int y, int color, int shadow)
	{
		if (text != null)
		{
			setEffects(color, shadow);
			drawBasicString(text, x - getDisplayedWidth(text) / 2, y);
		}
	}

	public void drawCenteredStringWaveXY(String string, int x, int y, int wave, int colour)
	{
		if (string == null)
		{
			return;
		}
		x -= getDisplayedWidth(string) / 2;
		y -= characterDefaultHeight;
		for (int index = 0; index < string.length(); index++)
		{
			char character = string.charAt(index);
			if (character != ' ')
			{
				drawCharacterLegacy(characterPixels[character], x + characterXOffsets[character] + (int) (Math.sin(index / 5D + wave / 5D) * 5D), y
						+ characterYOffsets[character] + (int) (Math.sin(index / 3D + wave / 5D) * 5D), characterWidths[character],
					characterHeights[character], colour);
			}
			x += characterScreenWidths[character];
		}

	}

	public void drawCenteredStringWaveXYMove(String string, int x, int y, int waveAmount, int waveSpeed, int colour)
	{
		if (string == null)
		{
			return;
		}
		double speed = 7D - waveSpeed / 8D;
		if (speed < 0.0D)
		{
			speed = 0.0D;
		}
		x -= getDisplayedWidth(string) / 2;
		y -= characterDefaultHeight;
		for (int index = 0; index < string.length(); index++)
		{
			char character = string.charAt(index);
			if (character != ' ')
			{
				drawCharacterLegacy(characterPixels[character], x + characterXOffsets[character], y + characterYOffsets[character]
					+ (int) (Math.sin(index / 1.5D + waveAmount) * speed), characterWidths[character], characterHeights[character], colour);
			}
			x += characterScreenWidths[character];
		}

	}

	public void drawShadowedString(String string, int x, int y, boolean shadow, int colour)
	{
		if (!shadow)
		{
			drawString(string, x, y, colour);
			return;
		}
		if (this.moderatorIcon != null)
		{
			this.drawString(string, x, y, colour, 0);
		}
		else
		{
			strikethroughColor = -1;
			int originalX = x;
			if (string == null)
			{
				return;
			}
			y -= characterDefaultHeight;
			for (int character = 0; character < string.length(); character++)
			{
				if (string.charAt(character) == '@' && character + 4 < string.length() && string.charAt(character + 4) == '@')
				{
					int stringColour = getColour(string.substring(character + 1, character + 4));
					if (stringColour != -1)
					{
						colour = stringColour;
					}
					character += 4;
				}
				else
				{
					char c = string.charAt(character);
					if (c != ' ')
					{
						if (shadow)
						{
							drawCharacterLegacy(characterPixels[c], x + characterXOffsets[c] + 1, y + characterYOffsets[c] + 1,
								characterWidths[c], characterHeights[c], 0);
						}
						drawCharacterLegacy(characterPixels[c], x + characterXOffsets[c], y + characterYOffsets[c], characterWidths[c],
							characterHeights[c], colour);
					}
					x += characterScreenWidths[c];
				}
			}

			if (strikethroughColor != -1)
			{
				Rasterizer.drawHorizontalLine(originalX, y + (int) (characterDefaultHeight * 0.69999999999999996D), x - originalX, strikethroughColor);
			}
		}
	}

	public void drawShadowedSeededAlphaString(String string, int x, int y, int colour, int seed)
	{
		if (string == null)
		{
			return;
		}
		random.setSeed(seed);
		int alpha = 192 + (random.nextInt() & 0x1f);
		y -= characterDefaultHeight;
		for (int index = 0; index < string.length(); index++)
		{
			if (string.charAt(index) == '@' && index + 4 < string.length() && string.charAt(index + 4) == '@')
			{
				int stringColour = getColour(string.substring(index + 1, index + 4));
				if (stringColour != -1)
				{
					colour = stringColour;
				}
				index += 4;
			}
			else
			{
				char c = string.charAt(index);
				if (c != ' ')
				{

					drawAlphaCharacter(characterPixels[c], x + characterXOffsets[c] + 1, y + characterYOffsets[c] + 1, characterWidths[c], characterHeights[c], 0,
						192);
					drawAlphaCharacter(characterPixels[c], x + characterXOffsets[c], y + characterYOffsets[c], characterWidths[c], characterHeights[c], colour,
						alpha);
				}
				x += characterScreenWidths[c];
				if ((random.nextInt() & 3) == 0)
				{
					x++;
				}
			}
		}

	}

	public int getColour(String code)
	{
		if (code.equals("red"))
		{
			return 0xff0000;
		}
		if (code.equals("gre"))
		{
			return 65280;
		}
		if (code.equals("blu"))
		{
			return 255;
		}
		if (code.equals("yel"))
		{
			return 0xffff00;
		}
		if (code.equals("cya"))
		{
			return 0x00ffff;
		}
		if (code.equals("mag"))
		{
			return 0xff00ff;
		}
		if (code.equals("whi"))
		{
			return 0xffffff;
		}
		if (code.equals("bla"))
		{
			return 0;
		}
		if (code.equals("lre"))
		{
			return 0xff9040;
		}
		if (code.equals("dre"))
		{
			return 0x800000;
		}
		if (code.equals("dbl"))
		{
			return 128;
		}
		if (code.equals("or1"))
		{
			return 0xffb000;
		}
		if (code.equals("or2"))
		{
			return 0xff7000;
		}
		if (code.equals("or3"))
		{
			return 0xff3000;
		}
		if (code.equals("gr1"))
		{
			return 0xc0ff00;
		}
		if (code.equals("gr2"))
		{
			return 0x80ff00;
		}
		if (code.equals("gr3"))
		{
			return 0x40ff00;
		}
		if (code.equals("str"))
		{
			strikethroughColor = 0;
		}
		if (code.equals("end"))
		{
			strikethroughColor = -1;
		}
		return -1;
	}


	public void drawCharacter(int character, int x, int y, int width, int height,
								int colour)
	{
		int rasterizerPixel = x + y * Rasterizer.width;
		int rasterizerPixelOffset = Rasterizer.width - width;
		int characterPixelOffset = 0;
		int characterPixel = 0;
		if (y < Rasterizer.topY)
		{
			int offsetY = Rasterizer.topY - y;
			height -= offsetY;
			y = Rasterizer.topY;
			characterPixel += offsetY * width;
			rasterizerPixel += offsetY * Rasterizer.width;
		}
		if (y + height > Rasterizer.bottomY)
		{
			height -= y + height - Rasterizer.bottomY;
		}
		if (x < Rasterizer.topX)
		{
			int offsetX = Rasterizer.topX - x;
			width -= offsetX;
			x = Rasterizer.topX;
			characterPixel += offsetX;
			rasterizerPixel += offsetX;
			characterPixelOffset += offsetX;
			rasterizerPixelOffset += offsetX;
		}
		if (x + width > Rasterizer.bottomX)
		{
			int endOffsetX = x + width - Rasterizer.bottomX;
			width -= endOffsetX;
			characterPixelOffset += endOffsetX;
			rasterizerPixelOffset += endOffsetX;
		}
		if (width > 0 && height > 0)
		{
			drawCharacterPixels(characterPixels[character], Rasterizer.pixels, characterPixel, rasterizerPixel,
				characterPixelOffset, rasterizerPixelOffset, width, height, colour);

		}
	}


	public void drawCharacterLegacy(byte[] pixels, int x, int y, int width, int height, int colour)
	{
		int rasterizerPixel = x + y * Rasterizer.width;
		int remainingWidth = Rasterizer.width - width;
		int characterPixelOffset = 0;
		int characterPixel = 0;
		if (y < Rasterizer.topY)
		{
			int offsetY = Rasterizer.topY - y;
			height -= offsetY;
			y = Rasterizer.topY;
			characterPixel += offsetY * width;
			rasterizerPixel += offsetY * Rasterizer.width;
		}
		if (y + height >= Rasterizer.bottomY)
		{
			height -= ((y + height) - Rasterizer.bottomY) + 1;
		}
		if (x < Rasterizer.topX)
		{
			int offsetX = Rasterizer.topX - x;
			width -= offsetX;
			x = Rasterizer.topX;
			characterPixel += offsetX;
			rasterizerPixel += offsetX;
			characterPixelOffset += offsetX;
			remainingWidth += offsetX;
		}
		if (x + width >= Rasterizer.bottomX)
		{
			int endOffsetX = ((x + width) - Rasterizer.bottomX) + 1;
			width -= endOffsetX;
			characterPixelOffset += endOffsetX;
			remainingWidth += endOffsetX;
		}
		if (width > 0 && height > 0)
		{
			drawCharacterPixels(pixels, Rasterizer.pixels, characterPixel, rasterizerPixel, characterPixelOffset, remainingWidth, width, height, colour);
		}
	}

	public void drawCharacterPixels(byte[] characterPixels, int[] rasterizerPixels, int characterPixel,
									int rasterizerPixel, int characterPixelOffset, int rasterizerPixelOffset, int width, int height, int colour)
	{
		int negativeQuaterWidth = -(width >> 2);
		width = -(width & 3);
		for (int heightCounter = -height; heightCounter < 0; heightCounter++)
		{
			for (int widthCounter = negativeQuaterWidth; widthCounter < 0; widthCounter++)
			{
				if (characterPixels[characterPixel++] != 0)
				{
					rasterizerPixels[rasterizerPixel++] = colour;
				}
				else
				{
					rasterizerPixel++;
				}
				if (characterPixels[characterPixel++] != 0)
				{
					rasterizerPixels[rasterizerPixel++] = colour;
				}
				else
				{
					rasterizerPixel++;
				}
				if (characterPixels[characterPixel++] != 0)
				{
					rasterizerPixels[rasterizerPixel++] = colour;
				}
				else
				{
					rasterizerPixel++;
				}
				if (characterPixels[characterPixel++] != 0)
				{
					rasterizerPixels[rasterizerPixel++] = colour;
				}
				else
				{
					rasterizerPixel++;
				}
			}

			for (int widthCounter = width; widthCounter < 0; widthCounter++)
			{
				if (characterPixels[characterPixel++] != 0)
				{
					rasterizerPixels[rasterizerPixel++] = colour;
				}
				else
				{
					rasterizerPixel++;
				}
			}

			rasterizerPixel += rasterizerPixelOffset;
			characterPixel += characterPixelOffset;
		}

	}

	private void setEffectsAlpha(int color, int shadow, int opac)
	{
		strikethroughColor = -1;
		underlineColor = -1;
		defaultShadowColor = shadow;
		shadowColor = shadow;
		defaultTextColor = color;
		textColor = color;
		defaultOpacity = opac;
		opacity = opac;
		whiteSpace = 0;
		anInt3748 = 0;
	}

	public void parseStringForEffects(String string)
	{
		do
		{
			try
			{
				if (string.startsWith(startColor))
				{
					String color = string.substring(4);
					textColor = color.length() < 6 ? Color.decode(color).getRGB() : Integer.parseInt(color, 16);

				}
				else if (string.equals(endColor))
				{
					textColor = defaultTextColor;
				}
				else if (string.startsWith(startTrans))
				{
					opacity = Integer.valueOf(string.substring(6));
				}
				else if (string.equals(endTrans))
				{
					opacity = defaultOpacity;
				}
				else if (string.startsWith(startStrikethrough))
				{
					String color = string.substring(4);
					strikethroughColor = color.length() < 6 ? Color.decode(color).getRGB() : Integer.parseInt(color, 16);
				}
				else if (string.equals(startDefaultStrikeThrough))
				{
					strikethroughColor = 8388608;
				}
				else if (string.equals(endStrikeThrough))
				{
					strikethroughColor = -1;
				}
				else if (string.startsWith(startUnderline))
				{
					String color = string.substring(2);
					underlineColor = color.length() < 6 ? Color.decode(color).getRGB() : Integer.parseInt(color, 16);
				}
				else if (string.equals(startDefaultUnderline))
				{
					underlineColor = 0;
				}
				else if (string.equals(endUnderline))
				{
					underlineColor = -1;
				}
				else if (string.startsWith(startShadow))
				{
					String color = string.substring(5);
					shadowColor = color.length() < 6 ? Color.decode(color).getRGB() : Integer.parseInt(color, 16);
				}
				else if (string.equals(startDefaultShadow))
				{
					shadowColor = 0;
				}
				else if (string.equals(endShadow))
				{
					shadowColor = defaultShadowColor;
				}
				else
				{
					if (!string.equals(lineBreak))
					{
						break;
					}
					setEffectsAlpha(defaultTextColor, defaultShadowColor, defaultOpacity);
				}
			}
			catch (Exception exception)
			{
				break;
			}
			break;
		} while (false);
	}

	public final void drawBasicStringXYMods(String string, int drawX, int drawY, int[] xmodifiers, int[] ymodifiers)
	{
		drawY -= characterDefaultHeight;
		int effect = -1;
		int var7 = 0;
		int modifier = 0;
		int length = string.length();

		for (int pos = 0; pos < length; ++pos)
		{
			int character = string.charAt(pos);
			if (character == 60)
			{
				effect = pos;
			}
			else
			{
				int xOff;
				int yOffset;
				int symbolWidth;
				if (character == 62 && effect != -1)
				{
					String symbol = string.substring(pos, effect + 1);
					effect = -1;
					if (symbol.equals(lessThan))
					{
						character = 60;
					}
					else if (symbol.equals(greaterThan))
					{
						character = 62;
					}
					else if (symbol.equals(nonBreakingSpace))
					{
						character = 160;
					}
					else if (symbol.equals(softHyphen))
					{
						character = 173;
					}
					else if (symbol.equals(multiplicationSymbol))
					{
						character = 215;
					}
					else if (symbol.equals(euroSymbol))
					{
						character = 128;
					}
					else if (symbol.equals(copyright))
					{
						character = 169;
					}
					else
					{
						if (!symbol.equals(registeredTrademark))
						{
							if (symbol.startsWith(image, 0))
							{
								try
								{
									if (xmodifiers != null)
									{
										xOff = xmodifiers[modifier];
									}
									else
									{
										xOff = 0;
									}

									if (ymodifiers != null)
									{
										yOffset = ymodifiers[modifier];
									}
									else
									{
										yOffset = 0;
									}

									++modifier;
									symbolWidth = Integer.parseInt(symbol.substring(4));
									ImageRGB image = moderatorIcon[symbolWidth];
									int imageHeight = imageWidths != null ? imageWidths[symbolWidth] : image.maxHeight;
									if (opacity == 256)
									{
										image.drawImage(drawX + xOff, drawY + characterDefaultHeight - imageHeight + yOffset);
									}
									else
									{
										image.drawImageAlpha(
											drawX + xOff, drawY + characterDefaultHeight - imageHeight + yOffset, opacity);
									}

									drawX += image.width;
									var7 = 0;
								}
								catch (Exception var18)
								{
								}
							}
							else
							{
								parseStringForEffects(symbol);
							}
							continue;
						}

						character = 174;
					}
				}
				if (character == '@' && character + 4 < string.length() && string.charAt(character + 4) == '@')
				{
					int stringColour = getColour(string.substring(character + 1, character + 4));
					if (stringColour != -1)
					{
						textColor = stringColour;
					}
					pos += 4;
					continue;
				}

				if (effect == -1)
				{
					int cWidth = characterWidths[character];
					xOff = characterHeights[character];
					if (xmodifiers != null)
					{
						yOffset = xmodifiers[modifier];
					}
					else
					{
						yOffset = 0;
					}

					if (ymodifiers != null)
					{
						symbolWidth = ymodifiers[modifier];
					}
					else
					{
						symbolWidth = 0;
					}

					++modifier;
					if (character != 32)
					{
						if (opacity == 256)
						{
							if (shadowColor != -1)
							{
								drawCharacter(character, drawX + characterXOffsets[character] + 1 + yOffset, drawY + characterYOffsets[character] + 1 + symbolWidth, cWidth, xOff, shadowColor);
							}

							drawCharacter(character, drawX + characterXOffsets[character] + yOffset, drawY + characterYOffsets[character] + symbolWidth, cWidth, xOff, textColor);
						}
						else
						{
							if (shadowColor != -1)
							{
								drawCharacterAlpha(character, drawX + characterXOffsets[character] + 1 + yOffset, drawY + characterYOffsets[character] + 1 + symbolWidth, cWidth, xOff, shadowColor, opacity);
							}

							drawCharacterAlpha(character, drawX + characterXOffsets[character] + yOffset, drawY + characterYOffsets[character] + symbolWidth, cWidth, xOff, textColor, opacity);
						}
					}
					else if (whiteSpace > 0)
					{
						anInt3748 += whiteSpace;
						drawX += anInt3748 >> 8;
						anInt3748 &= 255;
					}

					int charWidth = characterScreenWidths[character];
					if (strikethroughColor != -1)
					{
						if (opacity > 255)
						{
							Rasterizer.drawHorizontalLine(drawX, drawY + (int) ((double) characterDefaultHeight * 0.7D), charWidth, strikethroughColor);
						}
						else
						{
							Rasterizer.drawHorizontalLineAlpha(drawX, drawY + (int) ((double) characterDefaultHeight * 0.7D), charWidth, strikethroughColor, opacity);

						}
					}

					if (underlineColor != -1)
					{
						if (opacity > 255)
						{
							Rasterizer.drawHorizontalLine(drawX, drawY + characterDefaultHeight, charWidth, underlineColor);

						}
						else
						{

							Rasterizer.drawHorizontalLineAlpha(drawX, drawY + characterDefaultHeight, charWidth, underlineColor, opacity);
						}
					}

					drawX += charWidth;
					var7 = character;
				}
			}
		}

	}

	public void drawString(String string, int x, int y, int color, int shadow)
	{
		if (string != null)
		{
			setEffects(color, shadow);
			drawBasicString(string, x, y);
		}
	}


	public void drawStringAlignedLeft(String string, int x, int y, int color, int shadow)
	{
		if (string != null)
		{
			setEffects(color, shadow);
			drawBasicString(string, x - getDisplayedWidth(string), y);
		}
	}

	public void drawStringWave(String string, int x, int y, int color, int shadow, int var6)
	{
		if (string != null)
		{
			setEffects(color, shadow);
			int length = string.length();
			int[] var8 = new int[length];
			int[] var9 = new int[length];

			for (int pos = 0; pos < length; ++pos)
			{
				var8[pos] = (int) (Math.sin((double) pos / 5.0D + (double) var6 / 5.0D) * 5.0D);
				var9[pos] = (int) (Math.sin((double) pos / 3.0D + (double) var6 / 5.0D) * 5.0D);
			}

			drawBasicStringXYMods(string, x - getDisplayedWidth(string) / 2, y, var8, var9);
		}
	}

	public void drawStringWaveY(String string, int x, int y, int color, int shadow, int tick)
	{
		if (string != null)
		{
			setEffects(color, shadow);
			int var7 = string.length();
			int[] vertWaveOffset = new int[var7];

			for (int whichChar = 0; whichChar < var7; ++whichChar)
			{
				vertWaveOffset[whichChar] = (int) (Math.sin((double) whichChar / 2.0D + (double) tick / 5.0D) * 5.0D);
			}

			drawBasicStringXYMods(string, x - getDisplayedWidth(string) / 2, y, (int[]) null, vertWaveOffset);
		}
	}


	public void drawCenteredStringXMod(String var1, int var2, int var3, int var4, int var5, int var6, int var7)
	{
		if (var1 != null)
		{
			setEffects(var4, var5);
			double amplitude = 7.0D - (double) var7 / 8.0D;
			if (amplitude < 0.0D)
			{
				amplitude = 0.0D;
			}

			int length = var1.length();
			int[] xmod = new int[length];

			for (int pos = 0; pos < length; ++pos)
			{
				xmod[pos] = (int) (Math.sin((double) pos / 1.5D + (double) var6 / 1.0D) * amplitude);
			}

			drawBasicStringXYMods(var1, var2 - getDisplayedWidth(var1) / 2, var3, (int[]) null, xmod);
		}
	}


	private void setEffects(int color, int shadow)
	{
		strikethroughColor = -1;
		underlineColor = -1;
		defaultShadowColor = shadow;
		shadowColor = shadow;
		defaultTextColor = color;
		textColor = color;
		defaultOpacity = 256;
		opacity = 256;
		whiteSpace = 0;
		anInt3748 = 0;
	}

	public void drawBasicString(String string, int x, int y)
	{
		y -= characterDefaultHeight;
		int effectIndex = -1;
		int var5 = 0;
		int textLength = string.length();

		for (int character = 0; character < textLength; ++character)
		{
			int c = string.charAt(character);
			if (c > 255)
			{
				c = 32;
			}
			if (c == 60)
			{
				effectIndex = character;

			}
			else
			{
				if (c == 62 && effectIndex != -1)
				{

					String effectString = string.substring(effectIndex + 1, character);
					effectIndex = -1;
					if (effectString.equals(lessThan))
					{
						c = 60;
					}
					else if (effectString.equals(greaterThan))
					{
						c = 62;
					}
					else if (effectString.equals(nonBreakingSpace))
					{
						c = 160;
					}
					else if (effectString.equals(softHyphen))
					{
						c = 173;
					}
					else if (effectString.equals(multiplicationSymbol))
					{
						c = 215;
					}
					else if (effectString.equals(euroSymbol))
					{
						c = 128;
					}
					else if (effectString.equals(copyright))
					{
						c = 169;
					}
					else
					{
						if (!effectString.equals(registeredTrademark))
						{
							if (effectString.startsWith(image, 0))
							{
								try
								{
									int icon = Integer.valueOf(effectString.substring(4));
									ImageRGB nameIcon = moderatorIcon[icon];
									int imageHeight = imageWidths != null ? imageWidths[icon] : nameIcon.maxHeight;
									if (opacity == 256)
									{
										nameIcon.drawImage(x, y + characterDefaultHeight - imageHeight);
									}
									else
									{
										nameIcon.drawImageAlpha(x, y + characterDefaultHeight - imageHeight, opacity);
									}

									x += nameIcon.maxWidth;
									var5 = 0;
								}
								catch (Exception e)
								{

								}
							}
							else
							{
								parseStringForEffects(effectString);
							}
							continue;
						}
						c = 174;
					}
				}
				if (c == '@' && character + 4 < string.length() && string.charAt(character + 4) == '@')
				{
					int stringColour = getColour(string.substring(character + 1, character + 4));
					if (stringColour != -1)
					{
						textColor = stringColour;
					}
					character += 4;
					continue;
				}
				if (effectIndex == -1)
				{
					int width = characterWidths[c];
					int height = characterHeights[c];
					if (c != 32)
					{
						if (opacity == 256)
						{

							if (shadowColor != -1)
							{
								drawCharacter(c, x + characterXOffsets[c] + 1, y + characterYOffsets[c] + 1, width, height, shadowColor);
							}

							drawCharacter(c, x + characterXOffsets[c], y + characterYOffsets[c], width, height, textColor);
						}
						else
						{

							if (shadowColor != -1)
							{
								drawCharacterAlpha(c, x + characterXOffsets[c] + 1, y + characterYOffsets[c] + 1, width, height, shadowColor, opacity);

							}
							drawCharacterAlpha(c, x + characterXOffsets[c], y + characterYOffsets[c], width, height, textColor, opacity);
						}
					}
					else if (whiteSpace > 0)
					{
						anInt3748 += whiteSpace;
						x += anInt3748 >> 8;
						anInt3748 &= 255;
					}

					int charWidth = characterScreenWidths[c];
					if (strikethroughColor != -1)
					{
						Rasterizer.drawHorizontalLine(x, y + (int) ((double) characterDefaultHeight * 0.7D), charWidth, strikethroughColor);
					}
					if (underlineColor != -1)
					{
						Rasterizer.drawHorizontalLine(x, y + characterDefaultHeight + 1, charWidth, underlineColor);
					}
					x += charWidth;
					var5 = c;
				}
			}
		}

	}

	public void drawCharacterAlpha(int character, int x, int y, int width, int height,
										int colour, int alpha)
	{
		this.drawAlphaCharacter(characterPixels[character], x, y, width, height, colour, alpha);
	}

	public void drawAlphaCharacter(byte[] characterPixels, int x, int y, int width, int height, int colour,
									int alpha)
	{
		int rasterizerPixel = x + y * Rasterizer.width;
		int rasterizerPixelOffset = Rasterizer.width - width;
		int characterPixelOffset = 0;
		int characterPixel = 0;
		if (y < Rasterizer.topY)
		{
			int yOffset = Rasterizer.topY - y;
			height -= yOffset;
			y = Rasterizer.topY;
			characterPixel += yOffset * width;
			rasterizerPixel += yOffset * Rasterizer.width;
		}
		if (y + height >= Rasterizer.bottomY)
		{
			height -= ((y + height) - Rasterizer.bottomY) + 1;
		}
		if (x < Rasterizer.topX)
		{
			int xOffset = Rasterizer.topX - x;
			width -= xOffset;
			x = Rasterizer.topX;
			characterPixel += xOffset;
			rasterizerPixel += xOffset;
			characterPixelOffset += xOffset;
			rasterizerPixelOffset += xOffset;
		}
		if (x + width >= Rasterizer.bottomX)
		{
			int widthoffset = ((x + width) - Rasterizer.bottomX) + 1;
			width -= widthoffset;
			characterPixelOffset += widthoffset;
			rasterizerPixelOffset += widthoffset;
		}
		if (width > 0 && height > 0)
		{
			drawCharacterPixelsAlpha(characterPixel, rasterizerPixelOffset, characterPixelOffset, rasterizerPixel, alpha, Rasterizer.pixels, colour, height, width, characterPixels);
		}
	}


	public void drawCharacterPixelsAlpha(int characterPixel, int rasterizerPixelOffset, int characterPixelOffset,
											int rasterizerPixel, int alpha,
											int[] rasterizerPixels, int colour, int height, int width,
											byte[] characterPixels)
	{
		colour = ((colour & 0xff00ff) * alpha & 0xff00ff00) + ((colour & 0xff00) * alpha & 0xff0000) >> 8;
		alpha = 256 - alpha;
		for (int heightCounter = -height; heightCounter < 0; heightCounter++)
		{
			for (int widthCounter = -width; widthCounter < 0; widthCounter++)
			{
				if (characterPixels[characterPixel++] != 0)
				{
					int rasterizerPixelColor = rasterizerPixels[rasterizerPixel];
					rasterizerPixels[rasterizerPixel++] = (((rasterizerPixelColor & 0xff00ff) * alpha & 0xff00ff00) + ((rasterizerPixelColor & 0xff00) * alpha & 0xff0000) >> 8) + colour;
				}
				else
				{
					rasterizerPixel++;
				}
			}

			rasterizerPixel += rasterizerPixelOffset;
			characterPixel += characterPixelOffset;
		}

	}

	public void setNameIcons(ImageRGB[] images, int[] widths)
	{
		if (widths != null && widths.length != images.length)
		{
			throw new IllegalArgumentException();
		}
		else
		{
			moderatorIcon = images;
			imageWidths = widths;
		}
	}
}
