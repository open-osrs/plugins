package com.jagex.runescape377.util;

import com.jagex.runescape377.net.Buffer;

public class ChatEncoder
{

	private static char[] message = new char[100];
	private static Buffer messageBuffer = new Buffer(new byte[100]);
	private static char VALID_CHARACTERS[] = {' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w',
		'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
		'9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243',
		'$', '%', '"', '[', ']'};

	public static String get(int length, Buffer buffer)
	{
		int count = 0;
		int validCharacterIndex = -1;

		for (int lengthCounter = 0; lengthCounter < length; lengthCounter++)
		{
			int character = buffer.getUnsignedByte();
			int characterBit = character >> 4 & 0xf;

			if (validCharacterIndex == -1)
			{
				if (characterBit < 13)
				{
					message[count++] = VALID_CHARACTERS[characterBit];
				}
				else
				{
					validCharacterIndex = characterBit;
				}
			}
			else
			{
				message[count++] = VALID_CHARACTERS[((validCharacterIndex << 4) + characterBit) - 195];
				validCharacterIndex = -1;
			}

			characterBit = character & 0xf;

			if (validCharacterIndex == -1)
			{
				if (characterBit < 13)
				{
					message[count++] = VALID_CHARACTERS[characterBit];
				}
				else
				{
					validCharacterIndex = characterBit;
				}
			}
			else
			{
				message[count++] = VALID_CHARACTERS[((validCharacterIndex << 4) + characterBit) - 195];
				validCharacterIndex = -1;
			}
		}

		boolean isSymbol = true;

		for (int messageIndex = 0; messageIndex < count; messageIndex++)
		{
			char c = message[messageIndex];

			if (isSymbol && c >= 'a' && c <= 'z')
			{
				message[messageIndex] += '\uFFE0';
				isSymbol = false;
			}

			if (c == '.' || c == '!' || c == '?')
			{
				isSymbol = true;
			}
		}

		return new String(message, 0, count);
	}

	public static void put(String chatMessage, Buffer buffer)
	{
		if (chatMessage.length() > 80)
		{
			chatMessage = chatMessage.substring(0, 80);
		}

		int chatMessageCharacter = -1;

		for (int index = 0; index < chatMessage.length(); index++)
		{
			char character = chatMessage.charAt(index);
			int validCharacterIndex = 0;

			for (int validIndex = 0; validIndex < VALID_CHARACTERS.length; validIndex++)
			{
				if (character != VALID_CHARACTERS[validIndex])
				{
					continue;
				}

				validCharacterIndex = validIndex;
				break;
			}

			if (validCharacterIndex > 12)
			{
				validCharacterIndex += 195;
			}

			if (chatMessageCharacter == -1)
			{
				if (validCharacterIndex < 13)
				{
					chatMessageCharacter = validCharacterIndex;
				}
				else
				{
					buffer.putByte(validCharacterIndex);
				}
			}
			else if (validCharacterIndex < 13)
			{
				buffer.putByte((chatMessageCharacter << 4) + validCharacterIndex);
				chatMessageCharacter = -1;
			}
			else
			{
				buffer.putByte((chatMessageCharacter << 4) + (validCharacterIndex >> 4));
				chatMessageCharacter = validCharacterIndex & 0xf;
			}
		}

		if (chatMessageCharacter != -1)
		{
			buffer.putByte(chatMessageCharacter << 4);
		}
	}

	public static String formatChatMessage(String chatMessage)
	{
		messageBuffer.currentPosition = 0;

		put(chatMessage, messageBuffer);

		int offset = messageBuffer.currentPosition;
		messageBuffer.currentPosition = 0;

		return get(offset, messageBuffer);
	}


}
