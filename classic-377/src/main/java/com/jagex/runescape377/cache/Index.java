package com.jagex.runescape377.cache;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Index
{

	public static byte buffer[] = new byte[520];
	public RandomAccessFile dataFile;
	public RandomAccessFile indexFile;
	public int type;
	public int maxSize;

	public Index(int _type, int _maxSize, RandomAccessFile dataFile, RandomAccessFile indexFile)
	{
		type = _type;
		this.dataFile = dataFile;
		this.indexFile = indexFile;
		maxSize = _maxSize;
	}

	public synchronized byte[] get(int index)
	{
		try
		{
			seek(indexFile, index * 6);
			int fileSize;
			for (int indexPart = 0; indexPart < 6; indexPart += fileSize)
			{
				fileSize = indexFile.read(buffer, indexPart, 6 - indexPart);
				if (fileSize == -1)
				{
					return null;
				}
			}

			fileSize = ((buffer[0] & 0xff) << 16) + ((buffer[1] & 0xff) << 8) + (buffer[2] & 0xff);
			int fileBlock = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);
			if (fileSize < 0 || fileSize > maxSize)
			{
				return null;
			}
			if (fileBlock <= 0 || fileBlock > dataFile.length() / 520L)
			{
				return null;
			}
			byte fileBuffer[] = new byte[fileSize];
			int read = 0;
			for (int cycle = 0; read < fileSize; cycle++)
			{
				if (fileBlock == 0)
				{
					return null;
				}
				seek(dataFile, fileBlock * 520);
				int size = 0;
				int remaining = fileSize - read;
				if (remaining > 512)
				{
					remaining = 512;
				}
				int nextFileId;
				for (; size < remaining + 8; size += nextFileId)
				{
					nextFileId = dataFile.read(buffer, size, (remaining + 8) - size);
					if (nextFileId == -1)
					{
						return null;
					}
				}

				nextFileId = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);
				int currentPartId = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
				int nextBlockId = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8)
					+ (buffer[6] & 0xff);
				int nextStoreId = buffer[7] & 0xff;
				if (nextFileId != index || currentPartId != cycle || nextStoreId != type)
				{
					return null;
				}
				if (nextBlockId < 0 || nextBlockId > dataFile.length() / 520L)
				{
					return null;
				}
				for (int offset = 0; offset < remaining; offset++)
				{
					fileBuffer[read++] = buffer[offset + 8];
				}

				fileBlock = nextBlockId;
			}

			return fileBuffer;
		}
		catch (IOException _ex)
		{
			return null;
		}
	}

	public synchronized boolean put(int len, byte buf[], int id)
	{
		boolean success = put(buf, id, true, len);
		if (!success)
		{
			success = put(buf, id, false, len);
		}
		return success;
	}

	public synchronized boolean put(byte buf[], int index, boolean overwrite, int len)
	{
		try
		{
			int sector;
			if (overwrite)
			{
				seek(indexFile, index * 6);
				int lenght;
				for (int offset = 0; offset < 6; offset += lenght)
				{
					lenght = indexFile.read(buffer, offset, 6 - offset);
					if (lenght == -1)
					{
						return false;
					}
				}

				sector = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);
				if (sector <= 0 || sector > dataFile.length() / 520L)
				{
					return false;
				}
			}
			else
			{
				sector = (int) ((dataFile.length() + 519L) / 520L);
				if (sector == 0)
				{
					sector = 1;
				}
			}
			buffer[0] = (byte) (len >> 16);
			buffer[1] = (byte) (len >> 8);
			buffer[2] = (byte) len;
			buffer[3] = (byte) (sector >> 16);
			buffer[4] = (byte) (sector >> 8);
			buffer[5] = (byte) sector;
			seek(indexFile, index * 6);
			indexFile.write(buffer, 0, 6);
			int written = 0;
			for (int chunk = 0; written < len; chunk++)
			{
				int nextSector = 0;
				if (overwrite)
				{
					seek(dataFile, sector * 520);
					int pos;
					int tmp;
					for (pos = 0; pos < 8; pos += tmp)
					{
						tmp = dataFile.read(buffer, pos, 8 - pos);
						if (tmp == -1)
						{
							break;
						}
					}

					if (pos == 8)
					{
						int _id = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);
						int _chunk = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
						nextSector = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8)
							+ (buffer[6] & 0xff);
						int _type = buffer[7] & 0xff;
						if (_id != index || _chunk != chunk || _type != type)
						{
							return false;
						}
						if (nextSector < 0 || nextSector > dataFile.length() / 520L)
						{
							return false;
						}
					}
				}
				if (nextSector == 0)
				{
					overwrite = false;
					nextSector = (int) ((dataFile.length() + 519L) / 520L);
					if (nextSector == 0)
					{
						nextSector++;
					}
					if (nextSector == sector)
					{
						nextSector++;
					}
				}
				if (len - written <= 512)
				{
					nextSector = 0;
				}
				buffer[0] = (byte) (index >> 8);
				buffer[1] = (byte) index;
				buffer[2] = (byte) (chunk >> 8);
				buffer[3] = (byte) chunk;
				buffer[4] = (byte) (nextSector >> 16);
				buffer[5] = (byte) (nextSector >> 8);
				buffer[6] = (byte) nextSector;
				buffer[7] = (byte) type;
				seek(dataFile, sector * 520);
				dataFile.write(buffer, 0, 8);
				int sectorLen = len - written;
				if (sectorLen > 512)
				{
					sectorLen = 512;
				}
				dataFile.write(buf, written, sectorLen);
				written += sectorLen;
				sector = nextSector;
			}

			return true;
		}
		catch (IOException _ex)
		{
			return false;
		}
	}

	public synchronized void seek(RandomAccessFile file, int position) throws IOException
	{
		if (position < 0 || position > 0x3c00000)
		{
			System.out.println("Badseek - coord:" + position + " len:" + file.length());
			position = 0x3c00000;
			try
			{
				Thread.sleep(1000L);
			}
			catch (Exception _ex)
			{
			}
		}
		file.seek(position);
	}


}
