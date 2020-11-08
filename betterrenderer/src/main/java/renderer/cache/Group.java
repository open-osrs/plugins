package renderer.cache;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.nio.ByteBuffer;
import renderer.util.NetworkBuffer;
import renderer.util.Util;

public class Group
{
	public int id;
	public int crc;
	public int version;
	public ByteBuffer data;
	public Int2ObjectMap<ByteBuffer> files;
	public int fileCount;
	public int[] fileIds;
	public int[] fileNameHashes;

	public void buildFiles(int[] key)
	{
		byte[] data = Util.toBytes(this.data);

		if (key != null && (key[0] != 0 || key[1] != 0 || key[2] != 0 || key[3] != 0))
		{
			data = data.clone();
			Util.unxtea(data, 5, data.length, key);
		}

		byte[] decompressedBytes = Archive.decompress(data);

		files = new Int2ObjectOpenHashMap<>();

		if (fileCount == 1)
		{
			files.put(fileIds[0], Util.toBuffer(decompressedBytes));
			return;
		}

		int length = decompressedBytes.length;
		--length;
		int var10 = decompressedBytes[length] & 255;
		length -= var10 * fileCount * 4;
		NetworkBuffer buffer = new NetworkBuffer(decompressedBytes);
		int[] var12 = new int[fileCount];
		buffer.offset = length;

		int var14;
		for (int var13 = 0; var13 < var10; ++var13)
		{
			var14 = 0;

			for (int i = 0; i < fileCount; ++i)
			{
				var14 += buffer.readInt();
				var12[i] += var14;
			}
		}

		byte[][] var19 = new byte[fileCount][];

		for (var14 = 0; var14 < fileCount; ++var14)
		{
			var19[var14] = new byte[var12[var14]];
			var12[var14] = 0;
		}

		buffer.offset = length;
		var14 = 0;

		for (int i = 0; i < var10; ++i)
		{
			int var16 = 0;

			for (int var17 = 0; var17 < fileCount; ++var17)
			{
				var16 += buffer.readInt();
				System.arraycopy(decompressedBytes, var14, var19[var17], var12[var17], var16);
				var12[var17] += var16;
				var14 += var16;
			}
		}

		for (int i = 0; i < fileCount; ++i)
		{
			files.put(fileIds[i], Util.toBuffer(var19[i]));
		}
	}

	public byte[] file(int id)
	{
		if (files == null)
		{
			buildFiles(null);
		}

		byte[] file = Util.toBytes(files.get(id));

		if (file == null)
		{
			throw new IllegalArgumentException("file " + id + " doesn't exist");
		}

		return file;
	}
}
