package com.jagex.runescape377.cache;

import com.jagex.runescape377.cache.bzip.BZip2Decompressor;
import com.jagex.runescape377.net.Buffer;

public class Archive
{

	public byte archiveBuffer[];
	public int dataSize;
	public int nameHashes[];
	public int uncompressedSizes[];
	public int compressedSizes[];
	public int startOffsets[];
	public boolean compressed;

	/**
	 * Creates the archive.
	 *
	 * @param dataBuffer The buffer of the archive.
	 */
	public Archive(byte[] dataBuffer)
	{
		Buffer buffer = new Buffer(dataBuffer);
		int uncompressed = buffer.getMediumBE();
		int compressed = buffer.getMediumBE();
		if (compressed != uncompressed)
		{
			byte[] data = new byte[uncompressed];
			BZip2Decompressor.decompress(data, uncompressed, dataBuffer, compressed, 6);
			archiveBuffer = data;
			buffer = new Buffer(archiveBuffer);
			this.compressed = true;
		}
		else
		{
			archiveBuffer = dataBuffer;
			this.compressed = false;
		}
		dataSize = buffer.getUnsignedShortBE();
		nameHashes = new int[dataSize];
		uncompressedSizes = new int[dataSize];
		compressedSizes = new int[dataSize];
		startOffsets = new int[dataSize];
		int offset = buffer.currentPosition + dataSize * 10;
		for (int index = 0; index < dataSize; index++)
		{
			nameHashes[index] = buffer.getIntBE();
			uncompressedSizes[index] = buffer.getMediumBE();
			compressedSizes[index] = buffer.getMediumBE();
			startOffsets[index] = offset;
			offset += compressedSizes[index];
		}
	}

	/**
	 * Gets a file by its name.
	 *
	 * @param file The file name.
	 * @return The file contents.
	 */
	public byte[] getFile(String file)
	{
		byte[] dataBuffer = null;
		int hash = 0;
		file = file.toUpperCase();

		for (int pos = 0; pos < file.length(); pos++)
		{
			hash = (hash * 61 + file.charAt(pos)) - 32;
		}

		for (int index = 0; index < dataSize; index++)
		{
			if (nameHashes[index] == hash)
			{
				if (dataBuffer == null)
				{
					dataBuffer = new byte[uncompressedSizes[index]];
				}

				if (!compressed)
				{
					BZip2Decompressor.decompress(dataBuffer, uncompressedSizes[index], archiveBuffer, compressedSizes[index], startOffsets[index]);
				}
				else
				{
					for (int pos = 0; pos < uncompressedSizes[index]; pos++)
					{
						dataBuffer[pos] = archiveBuffer[startOffsets[index] + pos];
					}

				}
				return dataBuffer;
			}
		}
		return null;
	}

}
