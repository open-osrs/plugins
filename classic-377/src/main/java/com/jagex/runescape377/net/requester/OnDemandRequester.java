package com.jagex.runescape377.net.requester;

import com.jagex.runescape377.Game;
import com.jagex.runescape377.cache.Archive;
import com.jagex.runescape377.collection.Queue;
import com.jagex.runescape377.config.Configuration;
import com.jagex.runescape377.net.Buffer;
import com.jagex.runescape377.util.LinkedList;
import com.jagex.runescape377.util.SignLink;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;

public class OnDemandRequester extends Requester implements Runnable
{

	public int anInt1334;
	public byte modelIndex[];
	public int regShouldPreload[];
	public byte filePriorities[][] = new byte[4][];
	public boolean expectData = false;
	public boolean running = true;
	public LinkedList wanted = new LinkedList();
	public int highestPriority;
	public int immediateRequestsSent;
	public int anInt1343;
	public int fileCrc[][] = new int[4][];
	public int anInt1345;
	public int regHash[];
	public String message = "";
	public int cycle;
	public OutputStream outputStream;
	public int anInt1350;
	public LinkedList aClass6_1351 = new LinkedList();
	public boolean aBoolean1352 = false;
	public int idleCycles;
	public CRC32 crc32 = new CRC32();
	public Socket socket;
	public LinkedList completed = new LinkedList();
	public LinkedList immediateRequests = new LinkedList();
	public byte deflateOut[] = new byte[65000];
	public int regMapIndex[];
	public int offset;
	public int toRead;
	public int anInt1363;
	public byte inputBuffer[] = new byte[500];
	public int regLandIndex[];
	public int midiIndex[];
	public int anInt1367 = 591;
	public Queue immediateRequests1 = new Queue();
	public InputStream inputStream;
	public OnDemandNode onDemandNode;
	public Game client;
	public LinkedList toRequest = new LinkedList();
	public int sinceKeepAlive;
	public int animIndex[];
	public int fileVersions[][] = new int[4][];
	public long lastSocketOpen;
	public int requestFails;

	public boolean verify(int expectedVersion, int expectedCrc, byte[] data)
	{
		if (data == null || data.length < 2)
		{
			return false;
		}
		int length = data.length - 2;
		int version = ((data[length] & 0xff) << 8) + (data[length + 1] & 0xff);
		crc32.reset();
		crc32.update(data, 0, length);
		int crc = (int) crc32.getValue();
		if (version != expectedVersion)
		{
			return false;
		}
		return crc == expectedCrc;
	}

	public void handleResp()
	{
		try
		{
			int available = inputStream.available();
			if (toRead == 0 && available >= 6)
			{
				expectData = true;
				for (int read = 0; read < 6; read += inputStream.read(inputBuffer, read, 6 - read))
				{
					;
				}
				int type = inputBuffer[0] & 0xff;
				int id = ((inputBuffer[1] & 0xff) << 8) + (inputBuffer[2] & 0xff);
				int size = ((inputBuffer[3] & 0xff) << 8) + (inputBuffer[4] & 0xff);
				int chunk = inputBuffer[5] & 0xff;
				onDemandNode = null;
				for (OnDemandNode ondemandnode = (OnDemandNode) toRequest.first(); ondemandnode != null; ondemandnode = (OnDemandNode) toRequest.next())
				{
					if (ondemandnode.type == type && ondemandnode.id == id)
					{
						onDemandNode = ondemandnode;
					}
					if (onDemandNode != null)
					{
						ondemandnode.cyclesSinceSend = 0;
					}
				}

				if (onDemandNode != null)
				{
					idleCycles = 0;
					if (size == 0)
					{
						SignLink.reportError("Rej: " + type + "," + id);
						onDemandNode.buffer = null;
						if (onDemandNode.immediate)
						{
							synchronized (completed)
							{
								completed.pushBack(onDemandNode);
							}
						}
						else
						{
							onDemandNode.remove();
						}
						onDemandNode = null;
					}
					else
					{
						if (onDemandNode.buffer == null && chunk == 0)
						{
							onDemandNode.buffer = new byte[size];
						}
						if (onDemandNode.buffer == null && chunk != 0)
						{
							throw new IOException("missing initializeApplication of file");
						}
					}
				}
				offset = chunk * 500;
				toRead = 500;
				if (toRead > size - chunk * 500)
				{
					toRead = size - chunk * 500;
				}
			}
			if (toRead > 0 && available >= toRead)
			{
				expectData = true;
				byte buffer[] = inputBuffer;
				int bufferOffset = 0;
				if (onDemandNode != null)
				{
					buffer = onDemandNode.buffer;
					bufferOffset = offset;
				}
				for (int i = 0; i < toRead; i += inputStream.read(buffer, i + bufferOffset, toRead - i))
				{
					;
				}
				if (toRead + offset >= buffer.length && onDemandNode != null)
				{
					if (client.stores[0] != null)
					{
						client.stores[onDemandNode.type + 1].put(buffer.length, buffer,
							onDemandNode.id);
					}
					if (!onDemandNode.immediate && onDemandNode.type == 3)
					{
						onDemandNode.immediate = true;
						onDemandNode.type = 93;
					}
					if (onDemandNode.immediate)
					{
						synchronized (completed)
						{
							completed.pushBack(onDemandNode);
						}
					}
					else
					{
						onDemandNode.remove();
					}
				}
				toRead = 0;
				return;
			}
		}
		catch (IOException ioexception)
		{
			try
			{
				socket.close();
			}
			catch (Exception _ex)
			{
			}
			socket = null;
			inputStream = null;
			outputStream = null;
			toRead = 0;
		}
	}

	public int modelId(int model)
	{
		return modelIndex[model] & 0xff;
	}

	@Override
	public void requestModel(int id)
	{
		request(0, id);
	}

	public void passivesRequest(int i)
	{
		if (i != 0)
		{
			return;
		}
		while (immediateRequestsSent == 0 && anInt1343 < 10)
		{
			if (highestPriority == 0)
			{
				break;
			}
			OnDemandNode class50_sub1_sub3;
			synchronized (immediateRequests)
			{
				class50_sub1_sub3 = (OnDemandNode) immediateRequests.pop();
			}
			while (class50_sub1_sub3 != null)
			{
				if (filePriorities[class50_sub1_sub3.type][class50_sub1_sub3.id] != 0)
				{
					filePriorities[class50_sub1_sub3.type][class50_sub1_sub3.id] = 0;
					toRequest.pushBack(class50_sub1_sub3);
					sendRequest(class50_sub1_sub3);
					expectData = true;
					if (anInt1334 < anInt1350)
					{
						anInt1334++;
					}
					message = "Loading extra files - " + (anInt1334 * 100) / anInt1350 + "%";
					anInt1343++;
					if (anInt1343 == 10)
					{
						return;
					}
				}
				synchronized (immediateRequests)
				{
					class50_sub1_sub3 = (OnDemandNode) immediateRequests.pop();
				}
			}
			for (int j = 0; j < 4; j++)
			{
				byte abyte0[] = filePriorities[j];
				int k = abyte0.length;
				for (int l = 0; l < k; l++)
				{
					if (abyte0[l] == highestPriority)
					{
						abyte0[l] = 0;
						OnDemandNode class50_sub1_sub3_1 = new OnDemandNode();
						class50_sub1_sub3_1.type = j;
						class50_sub1_sub3_1.id = l;
						class50_sub1_sub3_1.immediate = false;
						toRequest.pushBack(class50_sub1_sub3_1);
						sendRequest(class50_sub1_sub3_1);
						expectData = true;
						if (anInt1334 < anInt1350)
						{
							anInt1334++;
						}
						message = "Loading extra files - " + (anInt1334 * 100) / anInt1350 + "%";
						anInt1343++;
						if (anInt1343 == 10)
						{
							return;
						}
					}
				}

			}

			highestPriority--;
		}
	}

	public void setPriority(byte byte0, int j, int k)
	{
		if (client.stores[0] == null)
		{
			return;
		}
		if (fileVersions[j][k] == 0)
		{
			return;
		}
		byte abyte0[] = client.stores[j + 1].get(k);
		if (verify(fileVersions[j][k], fileCrc[j][k], abyte0))
		{
			return;
		}
		filePriorities[j][k] = byte0;
		if (byte0 > highestPriority)
		{
			highestPriority = byte0;
		}
		anInt1350++;
	}

	public boolean midiIdEqualsOne(int i)
	{
		return midiIndex[i] == 1;
	}

	public void request(int type, int id)
	{
		if (type < 0 || type > fileVersions.length || id < 0 || id > fileVersions[type].length)
		{
			return;
		}
		if (fileVersions[type][id] == 0)
		{
			return;
		}
		synchronized (immediateRequests1)
		{
			for (OnDemandNode onDemandNode = (OnDemandNode) immediateRequests1.first(); onDemandNode != null; onDemandNode = (OnDemandNode) immediateRequests1
				.next())
			{
				if (onDemandNode.type == type && onDemandNode.id == id)
				{
					return;
				}
			}

			OnDemandNode onDemandNode = new OnDemandNode();
			onDemandNode.type = type;
			onDemandNode.id = id;
			onDemandNode.immediate = true;
			synchronized (wanted)
			{
				wanted.pushBack(onDemandNode);
			}
			immediateRequests1.push(onDemandNode);
		}
	}

	public OnDemandNode next()
	{
		OnDemandNode onDemandNode;
		synchronized (completed)
		{
			onDemandNode = (OnDemandNode) completed.pop();
		}
		if (onDemandNode == null)
		{
			return null;
		}
		synchronized (immediateRequests1)
		{
			onDemandNode.clear();
		}
		if (onDemandNode.buffer == null)
		{
			return onDemandNode;
		}
		int offset = 0;
		try
		{
			GZIPInputStream gzipinputstream = new GZIPInputStream(new ByteArrayInputStream(onDemandNode.buffer));
			do
			{
				if (offset == deflateOut.length)
				{
					throw new RuntimeException("buffer overflow!");
				}
				int k = gzipinputstream.read(deflateOut, offset, deflateOut.length - offset);
				if (k == -1)
				{
					break;
				}
				offset += k;
			} while (true);
		}
		catch (IOException _ex)
		{
			throw new RuntimeException("error unzipping");
		}
		onDemandNode.buffer = new byte[offset];
		for (int position = 0; position < offset; position++)
		{
			onDemandNode.buffer[position] = deflateOut[position];
		}

		return onDemandNode;
	}

	public void run()
	{
		try
		{
			while (running)
			{
				cycle++;
				int toWait = 20;
				if (highestPriority == 0 && client.stores[0] != null)
				{
					toWait = 50;
				}
				try
				{
					Thread.sleep(toWait);
				}
				catch (Exception _ex)
				{
				}
				expectData = true;
				for (int i = 0; i < 100; i++)
				{
					if (!expectData)
					{
						break;
					}
					expectData = false;
					localComplete(true);
					remainingRequest(0);
					if (immediateRequestsSent == 0 && i >= 5)
					{
						break;
					}
					passivesRequest(0);
					if (inputStream != null)
					{
						handleResp();
					}
				}

				boolean idle = false;
				for (OnDemandNode onDemandNode = (OnDemandNode) toRequest.first(); onDemandNode != null; onDemandNode = (OnDemandNode) toRequest.next())
				{
					if (onDemandNode.immediate)
					{
						idle = true;
						onDemandNode.cyclesSinceSend++;
						if (onDemandNode.cyclesSinceSend > 50)
						{
							onDemandNode.cyclesSinceSend = 0;
							sendRequest(onDemandNode);
						}
					}
				}

				if (!idle)
				{
					for (OnDemandNode onDemandNode = (OnDemandNode) toRequest.first(); onDemandNode != null; onDemandNode = (OnDemandNode) toRequest.next())
					{
						idle = true;
						onDemandNode.cyclesSinceSend++;
						if (onDemandNode.cyclesSinceSend > 50)
						{
							onDemandNode.cyclesSinceSend = 0;
							sendRequest(onDemandNode);
						}
					}

				}
				if (idle)
				{
					idleCycles++;
					if (idleCycles > 750)
					{
						try
						{
							socket.close();
						}
						catch (Exception _ex)
						{
						}
						socket = null;
						inputStream = null;
						outputStream = null;
						toRead = 0;
					}
				}
				else
				{
					idleCycles = 0;
					message = "";
				}
				if (client.loggedIn && socket != null && outputStream != null
					&& (highestPriority > 0 || client.stores[0] == null))
				{
					sinceKeepAlive++;
					if (sinceKeepAlive > 500)
					{
						sinceKeepAlive = 0;
						inputBuffer[0] = 0;
						inputBuffer[1] = 0;
						inputBuffer[2] = 0;
						inputBuffer[3] = 10;
						try
						{
							outputStream.write(inputBuffer, 0, 4);
						}
						catch (IOException _ex)
						{
							idleCycles = 5000;
						}
					}
				}
			}
			return;
		}
		catch (Exception exception)
		{
			SignLink.reportError("od_ex " + exception.getMessage());
		}
	}

	public void remainingRequest(int i)
	{
		immediateRequestsSent = 0;
		anInt1343 = 0;
		if (i != 0)
		{
			return;
		}
		for (OnDemandNode class50_sub1_sub3 = (OnDemandNode) toRequest.first(); class50_sub1_sub3 != null; class50_sub1_sub3 = (OnDemandNode) toRequest
			.next())
		{
			if (class50_sub1_sub3.immediate)
			{
				immediateRequestsSent++;
			}
			else
			{
				anInt1343++;
			}
		}

		while (immediateRequestsSent < 10)
		{
			OnDemandNode class50_sub1_sub3_1 = (OnDemandNode) aClass6_1351.pop();
			if (class50_sub1_sub3_1 == null)
			{
				break;
			}
			if (filePriorities[class50_sub1_sub3_1.type][class50_sub1_sub3_1.id] != 0)
			{
				anInt1334++;
			}
			filePriorities[class50_sub1_sub3_1.type][class50_sub1_sub3_1.id] = 0;
			toRequest.pushBack(class50_sub1_sub3_1);
			immediateRequestsSent++;
			sendRequest(class50_sub1_sub3_1);
			expectData = true;
		}
	}

	public void preloadRegions(boolean members)
	{
		for (int reg = 0; reg < regHash.length; reg++)
		{
			if (members || regShouldPreload[reg] != 0)
			{
				setPriority((byte) 2, 3, regLandIndex[reg]);
				setPriority((byte) 2, 3, regMapIndex[reg]);
			}
		}

	}

	public int method333()
	{
		synchronized (immediateRequests1)
		{
			int i = immediateRequests1.size();
			return i;
		}
	}

	public boolean method334(int i, boolean flag)
	{
		for (int j = 0; j < regHash.length; j++)
		{
			if (regLandIndex[j] == i)
			{
				return true;
			}
		}

		if (flag)
		{
			anInt1363 = -405;
		}
		return false;
	}

	public void init(Archive archive, Game client)
	{
		String versionFiles[] = {"model_version", "anim_version", "midi_version", "map_version"};
		for (int version = 0; version < 4; version++)
		{
			byte[] data = archive.getFile(versionFiles[version]);
			int versionCount = data.length / 2;
			Buffer buffer = new Buffer(data);
			fileVersions[version] = new int[versionCount];
			filePriorities[version] = new byte[versionCount];
			for (int file = 0; file < versionCount; file++)
			{
				fileVersions[version][file] = buffer.getUnsignedShortBE();
			}

		}

		String crcFiles[] = {"model_crc", "anim_crc", "midi_crc", "map_crc"};
		for (int crc = 0; crc < 4; crc++)
		{
			byte[] data = archive.getFile(crcFiles[crc]);
			int crcCount = data.length / 4;
			Buffer buffer = new Buffer(data);
			fileCrc[crc] = new int[crcCount];
			for (int file = 0; file < crcCount; file++)
			{
				fileCrc[crc][file] = buffer.getIntBE();
			}

		}

		byte[] data = archive.getFile("model_index");
		int count = fileVersions[0].length;
		modelIndex = new byte[count];
		for (int i = 0; i < count; i++)
		{
			if (i < data.length)
			{
				modelIndex[i] = data[i];
			}
			else
			{
				modelIndex[i] = 0;
			}
		}

		data = archive.getFile("map_index");
		Buffer buffer = new Buffer(data);
		count = data.length / 7;
		regHash = new int[count];
		regMapIndex = new int[count];
		regLandIndex = new int[count];
		regShouldPreload = new int[count];
		for (int reg = 0; reg < count; reg++)
		{
			regHash[reg] = buffer.getUnsignedShortBE();
			regMapIndex[reg] = buffer.getUnsignedShortBE();
			regLandIndex[reg] = buffer.getUnsignedShortBE();
			regShouldPreload[reg] = buffer.getUnsignedByte();
		}

		data = archive.getFile("anim_index");
		buffer = new Buffer(data);
		count = data.length / 2;
		animIndex = new int[count];
		for (int i = 0; i < count; i++)
		{
			animIndex[i] = buffer.getUnsignedShortBE();
		}

		data = archive.getFile("midi_index");
		buffer = new Buffer(data);
		count = data.length;
		midiIndex = new int[count];
		for (int i = 0; i < count; i++)
		{
			midiIndex[i] = buffer.getUnsignedByte();
		}

		this.client = client;
		this.running = true;
		this.client.startRunnable(this, 2);
	}

	public void immediateRequestCount()
	{
		synchronized (immediateRequests)
		{
			immediateRequests.clear();
		}
	}

	public void passiveRequest(int i, int j)
	{
		if (client.stores[0] == null)
		{
			return;
		}
		if (fileVersions[j][i] == 0)
		{
			return;
		}
		if (filePriorities[j][i] == 0)
		{
			return;
		}
		if (highestPriority == 0)
		{
			return;
		}
		OnDemandNode class50_sub1_sub3 = new OnDemandNode();
		class50_sub1_sub3.type = j;
		class50_sub1_sub3.id = i;
		class50_sub1_sub3.immediate = false;
		synchronized (immediateRequests)
		{
			immediateRequests.pushBack(class50_sub1_sub3);
		}
	}

	public void localComplete(boolean flag)
	{
		OnDemandNode class50_sub1_sub3;
		synchronized (wanted)
		{
			class50_sub1_sub3 = (OnDemandNode) wanted.pop();
		}
		if (!flag)
		{
			for (int i = 1; i > 0; i++)
			{
				;
			}
		}
		while (class50_sub1_sub3 != null)
		{
			expectData = true;
			byte abyte0[] = null;
			if (client.stores[0] != null)
			{
				abyte0 = client.stores[class50_sub1_sub3.type + 1].get(class50_sub1_sub3.id);
			}
			if (!verify(fileVersions[class50_sub1_sub3.type][class50_sub1_sub3.id], fileCrc[class50_sub1_sub3.type][class50_sub1_sub3.id], abyte0
			))
			{
				abyte0 = null;
			}
			synchronized (wanted)
			{
				if (abyte0 == null)
				{
					aClass6_1351.pushBack(class50_sub1_sub3);
				}
				else
				{
					class50_sub1_sub3.buffer = abyte0;
					synchronized (completed)
					{
						completed.pushBack(class50_sub1_sub3);
					}
				}
				class50_sub1_sub3 = (OnDemandNode) wanted.pop();
			}
		}
	}

	public void stop()
	{
		running = false;
	}

	public int fileCount(int file)
	{
		return fileVersions[file].length;
	}


	public void sendRequest(OnDemandNode onDemandNode)
	{
		try
		{
			if (socket == null)
			{
				long currentTime = System.currentTimeMillis();
				if (currentTime - lastSocketOpen < 4000L)
				{
					return;
				}
				lastSocketOpen = currentTime;
				socket = client.openSocket(Configuration.ONDEMAND_PORT + client.portOffset);
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
				outputStream.write(15);
				for (int i = 0; i < 8; i++)
				{
					inputStream.read();
				}

				idleCycles = 0;
			}
			inputBuffer[0] = (byte) onDemandNode.type;
			inputBuffer[1] = (byte) (onDemandNode.id >> 8);
			inputBuffer[2] = (byte) onDemandNode.id;
			if (onDemandNode.immediate)
			{
				inputBuffer[3] = 2;
			}
			else if (!client.loggedIn)
			{
				inputBuffer[3] = 1;
			}
			else
			{
				inputBuffer[3] = 0;
			}
			outputStream.write(inputBuffer, 0, 4);
			sinceKeepAlive = 0;
			requestFails = -10000;
			return;
		}
		catch (IOException ioexception)
		{
		}
		try
		{
			socket.close();
		}
		catch (Exception _ex)
		{
		}
		socket = null;
		inputStream = null;
		outputStream = null;
		toRead = 0;
		requestFails++;
	}

	public int animCount()
	{
		return animIndex.length;
	}

	public int regId(int i, int regX, int regY, int l)
	{
		int localRegHash = (regX << 8) + regY;
		for (int reg = 0; reg < regHash.length; reg++)
		{
			if (regHash[reg] == localRegHash)
			{
				if (l == 0)
				{
					return regMapIndex[reg];
				}
				else
				{
					return regLandIndex[reg];
				}
			}
		}

		return -1;
	}


}
