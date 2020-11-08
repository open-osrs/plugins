package renderer.util;

import java.nio.ByteBuffer;

public class CacheBuffer
{
	private static final char[] CHARACTERS = new char[]{
		'\u20ac', '\u0000', '\u201a', '\u0192', '\u201e', '\u2026',
		'\u2020', '\u2021', '\u02c6', '\u2030', '\u0160', '\u2039',
		'\u0152', '\u0000', '\u017d', '\u0000', '\u0000', '\u2018',
		'\u2019', '\u201c', '\u201d', '\u2022', '\u2013', '\u2014',
		'\u02dc', '\u2122', '\u0161', '\u203a', '\u0153', '\u0000',
		'\u017e', '\u0178'
	};

	private final ByteBuffer buffer;

	public CacheBuffer(byte[] buffer)
	{
		this.buffer = ByteBuffer.wrap(buffer);
	}

	public int position()
	{
		return buffer.position();
	}

	public void position(int offset)
	{
		buffer.position(offset);
	}

	public byte get()
	{
		return buffer.get();
	}

	public short getShort()
	{
		return buffer.getShort();
	}

	public int getMedium()
	{
		return ((get() & 0xFF) << 16) + ((get() & 0xFF) << 8) + (get() & 0xFF);
	}

	public int getInt()
	{
		return buffer.getInt();
	}

	public int getSpecial1()
	{
		int peek = buffer.get(buffer.position()) & 0xFF;
		return peek < 128 ? (get() & 0xFF) - 64 : (getShort() & 0xFFFF) - 0xc000;
	}

	public int getSpecial2()
	{
		int peek = buffer.get(buffer.position()) & 0xFF;
		return peek < 128 ? get() & 0xFF : (getShort() & 0xFFFF) - 0x8000;
	}

	public int getSpecial3()
	{
		int result = 0;
		int offset = getSpecial2();

		while (offset == 0x7fff)
		{
			result += 0x7fff;
			offset = getSpecial2();
		}

		result += offset;
		return result;
	}

	public String getString()
	{
		StringBuilder s = new StringBuilder();

		while (true)
		{
			int c = get() & 0xFF;

			if (c == 0)
			{
				break;
			}

			if (c >= 128 && c < 160)
			{
				c = CHARACTERS[c - 128];

				if (c == 0)
				{
					c = '?';
				}
			}

			s.append((char) c);
		}

		return s.toString();
	}
}
