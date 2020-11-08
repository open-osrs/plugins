package renderer.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class NetworkBuffer
{
	public final byte[] array;
	public int offset;

	public NetworkBuffer(int size)
	{
		array = new byte[size];
		offset = 0;
	}

	public NetworkBuffer(byte[] array)
	{
		this.array = array;
		offset = 0;
	}

	public void writeByte(int x)
	{
		array[++offset - 1] = (byte) x;
	}

	public void writeMedium(int x)
	{
		array[++offset - 1] = (byte) (x >> 16);
		array[++offset - 1] = (byte) (x >> 8);
		array[++offset - 1] = (byte) x;
	}

	public void writeInt(int x)
	{
		array[++offset - 1] = (byte) (x >> 24);
		array[++offset - 1] = (byte) (x >> 16);
		array[++offset - 1] = (byte) (x >> 8);
		array[++offset - 1] = (byte) x;
	}

	public int readUnsignedByte()
	{
		return array[++offset - 1] & 255;
	}

	public int readUnsignedShort()
	{
		offset += 2;
		return (array[offset - 1] & 255) + ((array[offset - 2] & 255) << 8);
	}

	public int readInt()
	{
		offset += 4;
		return ((array[offset - 3] & 255) << 16) + (array[offset - 1] & 255) + ((array[offset - 2] & 255) << 8) + ((array[offset - 4] & 255) << 24);
	}

	public byte[] read(int length)
	{
		byte[] result = new byte[length];

		for (int i = 0; i < length; ++i)
		{
			result[i] = array[++offset - 1];
		}

		return result;
	}

	public int readShortOrInt()
	{
		if (array[offset] < 0)
		{
			return readInt() & Integer.MAX_VALUE;
		}

		return readUnsignedShort();
	}

	public InputStream stream()
	{
		return new ByteArrayInputStream(array, offset, array.length - offset);
	}
}
