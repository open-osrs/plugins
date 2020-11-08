package renderer.cache;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import renderer.util.FastIntMap;
import renderer.util.NetworkBuffer;
import renderer.util.Util;

public class Archive
{
	public int id;
	public int hash;
	public int crc;
	public int version;
	private final Int2ObjectMap<Group> groups = new FastIntMap<>();
	private final Int2ObjectMap<Group> namedGroups = new Int2ObjectOpenHashMap<>();

	public Archive(int id)
	{
		this.id = id;
	}

	public void load(int indexCrc, int indexVersion)
	{
		crc = indexCrc;
		version = indexVersion;

		byte[] data = CacheFiles.read(255, id);

		if (data == null || Util.crc(data) != indexCrc && indexCrc != 0)
		{
			requestMaster();
			return;
		}

		NetworkBuffer decompressed = new NetworkBuffer(decompress(data));
		int type = decompressed.readUnsignedByte();

		if (type != 5 && type != 6)
		{
			throw new IllegalStateException();
		}

		if (type == 6)
		{
			decompressed.readInt();
		}

		loadIndex(data);
	}

	public void requestMaster()
	{
		Js5.request(255, id, true, data -> {
			if (Util.crc(data) != crc)
			{
				throw new AssertionError("crc");
			}

			CacheFiles.write(255, id, data);
			loadIndex(data);
		});
	}

	private void loadIndex(byte[] data)
	{
		hash = Util.crc(data);
		NetworkBuffer buffer = new NetworkBuffer(decompress(data));
		int type = buffer.readUnsignedByte();

		if (type != 5 && type != 6)
		{
			throw new RuntimeException();
		}

		if (type == 6)
		{
			buffer.readInt();
		}

		boolean hasNames = buffer.readUnsignedByte() != 0;
		int gc = type >= 7 ? buffer.readShortOrInt() : buffer.readUnsignedShort();

		Group[] groups = new Group[gc];

		for (int i = 0; i < groups.length; i++)
		{
			groups[i] = new Group();
		}

		int id = 0;

		for (Group group : groups)
		{
			id += buffer.readUnsignedShort();
			group.id = id;
			this.groups.put(id, group);
		}

		if (hasNames)
		{
			for (Group group : groups)
			{
				namedGroups.put(buffer.readInt(), group);
			}
		}

		for (Group group : groups)
		{
			group.crc = buffer.readInt();
		}
		for (Group group : groups)
		{
			group.version = buffer.readInt();
		}
		for (Group group : groups)
		{
			group.fileCount = buffer.readUnsignedShort();
		}

		for (Group group : groups)
		{
			group.fileIds = new int[group.fileCount];
			int fileId = 0;

			for (int i = 0; i < group.fileCount; ++i)
			{
				fileId += buffer.readUnsignedShort();
				group.fileIds[i] = fileId;
			}
		}

		if (hasNames)
		{
			for (Group group : groups)
			{
				group.fileNameHashes = new int[group.fileCount];

				for (int i = 0; i < group.fileCount; ++i)
				{
					group.fileNameHashes[group.fileNameHashes[i]] = buffer.readInt();
				}
			}
		}

		for (Group group : this.groups.values())
		{
			byte[] groupData = CacheFiles.read(this.id, group.id);

			if (groupData == null || Util.crc(groupData) != group(group.id).crc)
			{
				Js5.request(this.id, group.id, false, d -> {
					if (Util.crc(d) != group(group.id).crc)
					{
						throw new IllegalStateException("received crc doesn't match");
					}

					CacheFiles.write(this.id, group.id, d);
					group(group.id).data = Util.toBuffer(d);
				});
			}
			else
			{
				group(group.id).data = Util.toBuffer(groupData);
			}
		}
	}

	public Group group(int group)
	{
		return groups.get(group);
	}

	public Group group(String name)
	{
		return namedGroups.get(hash(name.toLowerCase()));
	}

	public static int hash(String s)
	{
		int length = s.length();
		int hash = 0;

		for (int i = 0; i < length; ++i)
		{
			hash = (hash << 5) - hash + Util.toCp1252(s.charAt(i));
		}

		return hash;
	}

	public static byte[] decompress(byte[] bytes)
	{
		try
		{
			NetworkBuffer buffer = new NetworkBuffer(bytes);
			int compressionType = buffer.readUnsignedByte();
			int size = buffer.readInt();

			switch (compressionType)
			{
				case 0:
				{
					return buffer.read(size);
				}
				case 1:
				{
					int uncompressedSize = buffer.readInt();
					byte[] data = new byte[4 + buffer.array.length - buffer.offset];
					data[0] = 'B';
					data[1] = 'Z';
					data[2] = 'h';
					data[3] = '1';
					System.arraycopy(buffer.array, buffer.offset, data, 4, buffer.array.length - buffer.offset);
					return Util.readNBytes(new BZip2CompressorInputStream(new ByteArrayInputStream(data)), uncompressedSize);
				}
				case 2:
				{
					int uncompressedSize = buffer.readInt();
					return Util.readNBytes(new GZIPInputStream(buffer.stream()), uncompressedSize);
				}
				default:
				{
					throw new AssertionError("unknown compression type " + compressionType);
				}
			}
		}
		catch (IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}
}
