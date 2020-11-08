package renderer.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import org.joml.Vector3d;
import org.joml.Vector4d;

public class Util
{
	public static final int GOLDEN_RATIO = 0x9E3779B9;
	public static final int ROUNDS = 32;
	public static CRC32 crc = new CRC32();

	public static Vector3d normal(Vector3d center, Vector3d a, Vector3d b)
	{ // ccw = up (inwards)
		Vector3d da = a.sub(center, new Vector3d());
		Vector3d db = b.sub(center, new Vector3d());
		return da.cross(db).normalize();
	}

	public static Vector4d[][] boxBlur(Vector4d[][] blended, int radius, int size)
	{
		// Horizontal blur
		Vector4d[][] newBlended = new Vector4d[size + 2 * radius][size + 2 * radius];

		for (int dy = -radius; dy < size + radius; dy++)
		{
			Vector4d color = new Vector4d(0);

			for (int dx = -2 * radius; dx < size + radius; dx++)
			{
				if (dx >= 0)
				{
					color.sub(blended[radius + dx - radius][radius + dy]);
				}
				if (dx >= -radius)
				{
					newBlended[radius + dx][radius + dy] = new Vector4d(color);
				}
				if (dx < size)
				{
					color.add(blended[radius + dx + radius][radius + dy]);
				}
			}
		}

		blended = newBlended;

		// Vertical blur
		newBlended = new Vector4d[size + 2 * radius][size + 2 * radius];

		for (int dx = -radius; dx < size + radius; dx++)
		{
			Vector4d color = new Vector4d(0);

			for (int dy = -2 * radius; dy < size + radius; dy++)
			{
				if (dy >= 0)
				{
					color.sub(blended[radius + dx][radius + dy - radius]);
				}
				if (dy >= -radius)
				{
					newBlended[radius + dx][radius + dy] = new Vector4d(color);
				}
				if (dy < size)
				{
					color.add(blended[radius + dx][radius + dy + radius]);
				}
			}
		}

		blended = newBlended;
		return blended;
	}

	public static void unxtea(byte[] data, int start, int end, int[] key)
	{
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int numQuads = (end - start) / 8;

		for (int i = 0; i < numQuads; i++)
		{
			int sum = GOLDEN_RATIO * ROUNDS;
			int v0 = buffer.getInt(start + i * 8);
			int v1 = buffer.getInt(start + i * 8 + 4);

			for (int j = 0; j < ROUNDS; j++)
			{
				v1 -= (v0 << 4 ^ v0 >>> 5) + v0 ^ sum + key[sum >>> 11 & 3];
				sum -= GOLDEN_RATIO;
				v0 -= (v1 << 4 ^ v1 >>> 5) + v1 ^ sum + key[sum & 3];
			}

			buffer.putInt(start + i * 8, v0);
			buffer.putInt(start + i * 8 + 4, v1);
		}
	}

	public static byte toCp1252(char c)
	{
		if (c > 0 && c < 128 || c >= 160 && c <= 255)
		{
			return (byte) c;
		}

		switch (c)
		{
			case 8364:
				return (byte) -128;
			case 8218:
				return (byte) -126;
			case 402:
				return (byte) -125;
			case 8222:
				return (byte) -124;
			case 8230:
				return (byte) -123;
			case 8224:
				return (byte) -122;
			case 8225:
				return (byte) -121;
			case 710:
				return (byte) -120;
			case 8240:
				return (byte) -119;
			case 352:
				return (byte) -118;
			case 8249:
				return (byte) -117;
			case 338:
				return (byte) -116;
			case 381:
				return (byte) -114;
			case 8216:
				return (byte) -111;
			case 8217:
				return (byte) -110;
			case 8220:
				return (byte) -109;
			case 8221:
				return (byte) -108;
			case 8226:
				return (byte) -107;
			case 8211:
				return (byte) -106;
			case 8212:
				return (byte) -105;
			case 732:
				return (byte) -104;
			case 8482:
				return (byte) -103;
			case 353:
				return (byte) -102;
			case 8250:
				return (byte) -101;
			case 339:
				return (byte) -100;
			case 382:
				return (byte) -98;
			case 376:
				return (byte) -97;
			default:
				return (byte) 0x3f;
		}
	}

	public static int crc(byte[] data)
	{
		crc.reset();
		crc.update(data, 0, data.length);
		return (int) crc.getValue();
	}

	public static ByteBuffer toBuffer(byte[] bytes)
	{
		ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
		buffer.put(bytes);
		buffer.position(0);
		return buffer;
	}

	public static byte[] toBytes(ByteBuffer buffer)
	{
		byte[] bytes = new byte[buffer.limit()];
		buffer.position(0);
		buffer.get(bytes);
		return bytes;
	}

	public static byte[] readAllBytes(InputStream in) throws IOException
	{
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];

		while (true)
		{
			int read = in.read(buffer, 0, buffer.length);

			if (read == -1)
			{
				return result.toByteArray();
			}

			result.write(buffer, 0, read);
		}
	}

	public static byte[] readNBytes(InputStream in, int len) throws IOException
	{
		byte[] b = new byte[len];

		for (int i = 0; i < b.length; i++)
		{
			int read = in.read();

			if (read == -1)
			{
				byte[] b2 = new byte[i];
				System.arraycopy(b, 0, b2, 0, i);
				return b2;
			}

			b[i] = (byte) read;
		}

		return b;
	}
}
