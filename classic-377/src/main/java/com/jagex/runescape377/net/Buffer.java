package com.jagex.runescape377.net;

import com.jagex.runescape377.collection.CacheableNode;
import java.math.BigInteger;

public class Buffer extends CacheableNode
{


	private static final int[] BIT_MASKS = {0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383,
		32767, 65535, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff,
		0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, -1};
	public byte[] buffer;
	public int currentPosition;
	public int bitPosition;
	public ISAACCipher random;


	public Buffer()
	{
	}

	public Buffer(byte[] buffer)
	{
		this.buffer = buffer;
		this.currentPosition = 0;
	}

	public static Buffer allocate(int sizeMode)
	{
		Buffer buffer = new Buffer();
		buffer.currentPosition = 0;
		if (sizeMode == 0)
		{
			buffer.buffer = new byte[100];
		}
		else if (sizeMode == 1)
		{
			buffer.buffer = new byte[5000];
		}
		else
		{
			buffer.buffer = new byte[30000];
		}
		return buffer;
	}

	public void putOpcode(int opcode)
	{
		buffer[currentPosition++] = (byte) (opcode + random.nextInt());
	}

	public void putByte(int value)
	{
		buffer[currentPosition++] = (byte) value;
	}

	public void putShortBE(int value)
	{
		buffer[currentPosition++] = (byte) (value >> 8);
		buffer[currentPosition++] = (byte) value;
	}

	public void putShortLECopy(int value)
	{
		buffer[currentPosition++] = (byte) value;
		buffer[currentPosition++] = (byte) (value >> 8);
	}

	public void putMediumBE(int value)
	{
		buffer[currentPosition++] = (byte) (value >> 16);
		buffer[currentPosition++] = (byte) (value >> 8);
		buffer[currentPosition++] = (byte) value;
	}

	public void putIntBE(int value)
	{
		buffer[currentPosition++] = (byte) (value >> 24);
		buffer[currentPosition++] = (byte) (value >> 16);
		buffer[currentPosition++] = (byte) (value >> 8);
		buffer[currentPosition++] = (byte) value;
	}

	public void putIntLE(int value)
	{
		buffer[currentPosition++] = (byte) value;
		buffer[currentPosition++] = (byte) (value >> 8);
		buffer[currentPosition++] = (byte) (value >> 16);
		buffer[currentPosition++] = (byte) (value >> 24);
	}

	public void putLongBE(long value)
	{
		buffer[currentPosition++] = (byte) (int) (value >> 56);
		buffer[currentPosition++] = (byte) (int) (value >> 48);
		buffer[currentPosition++] = (byte) (int) (value >> 40);
		buffer[currentPosition++] = (byte) (int) (value >> 32);
		buffer[currentPosition++] = (byte) (int) (value >> 24);
		buffer[currentPosition++] = (byte) (int) (value >> 16);
		buffer[currentPosition++] = (byte) (int) (value >> 8);
		buffer[currentPosition++] = (byte) (int) value;
	}

	public void putString(String str)
	{
		if (str == null)
		{
			str = "";
		}

		byte[] bytes = new byte[str.length()];
		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] = (byte) str.charAt(i);
		}
		System.arraycopy(bytes, 0, buffer, currentPosition, bytes.length);
		currentPosition += str.length();
		buffer[currentPosition++] = 10;
	}

	public void putBytes(byte[] bytes, int start, int length)
	{
		for (int pos = start; pos < start + length; pos++)
		{
			buffer[currentPosition++] = bytes[pos];
		}
	}

	public void putLength(int length)
	{
		buffer[currentPosition - length - 1] = (byte) length;
	}

	public int getUnsignedByte()
	{
		return buffer[currentPosition++] & 0xff;
	}

	public byte getByte()
	{
		return buffer[currentPosition++];
	}

	public int getUnsignedShortBE()
	{
		currentPosition += 2;
		return ((buffer[currentPosition - 2] & 0xff) << 8) + (buffer[currentPosition - 1] & 0xff);
	}

	public int getShortBE()
	{
		currentPosition += 2;
		int i = ((buffer[currentPosition - 2] & 0xff) << 8) + (buffer[currentPosition - 1] & 0xff);
		if (i > 32767)
		{
			i -= 0x10000;
		}
		return i;
	}

	public int getMediumBE()
	{
		currentPosition += 3;
		return ((buffer[currentPosition - 3] & 0xff) << 16) + ((buffer[currentPosition - 2] & 0xff) << 8)
			+ (buffer[currentPosition - 1] & 0xff);
	}

	public int getIntBE()
	{
		currentPosition += 4;
		return ((buffer[currentPosition - 4] & 0xff) << 24) + ((buffer[currentPosition - 3] & 0xff) << 16)
			+ ((buffer[currentPosition - 2] & 0xff) << 8) + (buffer[currentPosition - 1] & 0xff);
	}

	public long getLongBE()
	{
		long l = getIntBE() & 0xffffffffL;
		long l1 = getIntBE() & 0xffffffffL;
		return (l << 32) + l1;
	}

	public String getString()
	{
		int start = currentPosition;
		moveBufferToTarget((byte) 10);
		return new String(buffer, start, currentPosition - start - 1);
	}

	private void moveBufferToTarget(byte target)
	{
		if (buffer[currentPosition++] != target)
		{
			moveBufferToTarget(target);
		}
	}

	public byte[] getStringBytes()
	{
		int start = currentPosition;
		moveBufferToTarget((byte) 10);
		byte[] bytes = new byte[currentPosition - start - 1];
		if (currentPosition - 1 - start >= 0)
		{
			System.arraycopy(buffer, start, bytes, 0, currentPosition - 1 - start);
		}
		return bytes;
	}

	public void getBytes(byte[] bytes, int start, int len)
	{
		for (int pos = start; pos < start + len; pos++)
		{
			bytes[pos] = buffer[currentPosition++];
		}
	}

	public void initBitAccess()
	{
		bitPosition = currentPosition * 8;
	}

	public int getBits(int numBits)
	{
		int k = bitPosition >> 3;
		int l = 8 - (bitPosition & 7);
		int value = 0;
		bitPosition += numBits;
		for (; numBits > l; l = 8)
		{
			value += (buffer[k++] & BIT_MASKS[l]) << numBits - l;
			numBits -= l;
		}

		if (numBits == l)
		{
			value += buffer[k] & BIT_MASKS[l];
		}
		else
		{
			value += buffer[k] >> l - numBits & BIT_MASKS[numBits];
		}
		return value;
	}

	public void finishBitAccess()
	{
		currentPosition = (bitPosition + 7) / 8;
	}

	public int getSignedSmart()
	{
		int peek = buffer[currentPosition] & 0xff;
		if (peek < 128)
		{
			return getUnsignedByte() - 64;
		}
		else
		{
			return getUnsignedShortBE() - 49152;
		}
	}

	public int getSmart()
	{
		int peek = buffer[currentPosition] & 0xff;
		if (peek < 128)
		{
			return getUnsignedByte();
		}
		else
		{
			return getUnsignedShortBE() - 32768;
		}
	}

	public void encrypt(BigInteger modulus, BigInteger key)
	{
		int length = currentPosition;
		currentPosition = 0;
		byte[] bytes = new byte[length];

		getBytes(bytes, 0, length);

		BigInteger raw = new BigInteger(bytes);
		BigInteger encrypted = raw.modPow(key, modulus);
		bytes = encrypted.toByteArray();
		currentPosition = 0;

		putByte(bytes.length);
		putBytes(bytes, 0, bytes.length);
	}

	public void putOffsetByte(int value)
	{
		buffer[currentPosition++] = (byte) (value + 128);
	}

	public void putInvertedByte(int value)
	{
		buffer[currentPosition++] = (byte) (-value);
	}

	public void putNegativeOffsetByte(int value)
	{
		buffer[currentPosition++] = (byte) (128 - value);
	}

	public int getUnsignedPostNegativeOffsetByte()
	{
		return buffer[currentPosition++] - 128 & 0xff;
	}

	public int getUnsignedInvertedByte()
	{
		return -buffer[currentPosition++] & 0xff;
	}

	public int getUnsignedPreNegativeOffsetByte()
	{
		return 128 - buffer[currentPosition++] & 0xff;
	}

	public byte getPostNegativeOffsetByte()
	{
		return (byte) (buffer[currentPosition++] - 128);
	}

	public byte getInvertedByte()
	{
		return (byte) (-buffer[currentPosition++]);
	}

	public byte getPreNegativeOffsetByte()
	{
		return (byte) (128 - buffer[currentPosition++]);
	}

	public void putShortLE(int value)
	{
		buffer[currentPosition++] = (byte) value;
		buffer[currentPosition++] = (byte) (value >> 8);
	}

	public void putOffsetShortBE(int value)
	{
		buffer[currentPosition++] = (byte) (value >> 8);
		buffer[currentPosition++] = (byte) (value + 128);
	}

	public void putOffsetShortLE(int value)
	{
		buffer[currentPosition++] = (byte) (value + 128);
		buffer[currentPosition++] = (byte) (value >> 8);
	}

	public int getUnsignedShortLE()
	{
		currentPosition += 2;
		return ((buffer[currentPosition - 1] & 0xff) << 8) + (buffer[currentPosition - 2] & 0xff);
	}

	public int getUnsignedNegativeOffsetShortBE()
	{
		currentPosition += 2;
		return ((buffer[currentPosition - 2] & 0xff) << 8) + (buffer[currentPosition - 1] - 128 & 0xff);
	}

	public int getUnsignedNegativeOffsetShortLE()
	{
		currentPosition += 2;
		return ((buffer[currentPosition - 1] & 0xff) << 8) + (buffer[currentPosition - 2] - 128 & 0xff);
	}

	public int getShortLE()
	{
		currentPosition += 2;
		int j = ((buffer[currentPosition - 1] & 0xff) << 8) + (buffer[currentPosition - 2] & 0xff);
		if (j > 0x7fff)
		{
			j -= 0x10000;
		}
		return j;
	}

	public int getNegativeOffsetShortBE()
	{
		currentPosition += 2;
		int i = ((buffer[currentPosition - 2] & 0xff) << 8) + (buffer[currentPosition - 1] - 128 & 0xff);
		if (i > 32767)
		{
			i -= 0x10000;
		}
		return i;
	}

	public int getMediumME()
	{
		currentPosition += 3;
		return ((buffer[currentPosition - 2] & 0xff) << 16) + ((buffer[currentPosition - 3] & 0xff) << 8)
			+ (buffer[currentPosition - 1] & 0xff);
	}

	public int getIntLE()
	{
		currentPosition += 4;
		return ((buffer[currentPosition - 1] & 0xff) << 24) + ((buffer[currentPosition - 2] & 0xff) << 16)
			+ ((buffer[currentPosition - 3] & 0xff) << 8) + (buffer[currentPosition - 4] & 0xff);
	}

	public int getIntME1()
	{
		currentPosition += 4;
		return ((buffer[currentPosition - 2] & 0xff) << 24) + ((buffer[currentPosition - 1] & 0xff) << 16)
			+ ((buffer[currentPosition - 4] & 0xff) << 8) + (buffer[currentPosition - 3] & 0xff);
	}

	public int getIntME2()
	{
		currentPosition += 4;
		return ((buffer[currentPosition - 3] & 0xff) << 24) + ((buffer[currentPosition - 4] & 0xff) << 16)
			+ ((buffer[currentPosition - 1] & 0xff) << 8) + (buffer[currentPosition - 2] & 0xff);
	}

	public void getBytesReverse(byte[] bytes, int start, int len)
	{
		for (int pos = (start + len) - 1; pos >= start; pos--)
		{
			bytes[pos] = buffer[currentPosition++];
		}
	}

	public void getBytesAdded(byte[] bytes, int start, int len)
	{
		for (int pos = start; pos < start + len; pos++)
		{
			bytes[pos] = (byte) (buffer[currentPosition++] - 128);
		}
	}


}
