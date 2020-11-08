package renderer.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import renderer.util.NetworkBuffer;

public class Js5
{
	private static final Deque<Request> requests = new ArrayDeque<>();
	private static final Map<Long, Request> sentRequests = new LinkedHashMap<>();
	private static final NetworkBuffer header = new NetworkBuffer(8);
	private static NetworkBuffer buffer;
	private static Request currentResponse;
	private static int remaining;

	public static boolean tick(OutputStream out, InputStream in) throws IOException
	{
		if (sentRequests.isEmpty() && requests.isEmpty() && currentResponse == null)
		{
			return true;
		}

		while (sentRequests.size() < 100 && requests.size() > 0)
		{
			Request request = requests.removeLast();
			NetworkBuffer buffer = new NetworkBuffer(4);
			buffer.writeByte(request.priority ? 1 : 0);
			buffer.writeMedium((int) request.key);
			out.write(buffer.array, 0, 4);

			System.out.println("sent " + request.key);
			sentRequests.put(request.key, request);
		}

		for (int i = 0; i < 100; ++i)
		{
			int available = in.available();

			if (available < 0)
			{
				throw new IOException();
			}

			if (available == 0)
			{
				break;
			}

			byte headerLength = 0;
			if (currentResponse == null)
			{
				headerLength = 8;
			}
			else if (remaining == 0)
			{
				headerLength = 1;
			}

			if (headerLength > 0)
			{
				int var6 = headerLength - header.offset;
				if (var6 > available)
				{
					var6 = available;
				}

				in.read(header.array, header.offset, var6);
				int archive;

				header.offset += var6;
				if (header.offset < headerLength)
				{
					break;
				}

				if (currentResponse == null)
				{
					header.offset = 0;
					archive = header.readUnsignedByte();
					int group = header.readUnsignedShort();
					int var9 = header.readUnsignedByte();
					int var10 = header.readInt();
					long key = key(archive, group);
					int var14 = var9 == 0 ? 5 : 9;
					Request request = sentRequests.remove(key);

					if (request == null)
					{
						throw new IllegalStateException("request not found: " + archive + " " + group);
					}

					currentResponse = request;
					buffer = new NetworkBuffer(var14 + var10);
					buffer.writeByte(var9);
					buffer.writeInt(var10);
					remaining = 8;
					header.offset = 0;
				}
				else if (remaining == 0)
				{
					if (header.array[0] == -1)
					{
						remaining = 1;
						header.offset = 0;
					}
					else
					{
						currentResponse = null;
					}
				}
			}
			else
			{
				int lengthRead = buffer.array.length;
				int var7 = 512 - remaining;
				if (var7 > lengthRead - buffer.offset)
				{
					var7 = lengthRead - buffer.offset;
				}

				if (var7 > available)
				{
					var7 = available;
				}

				in.read(buffer.array, buffer.offset, var7);
				NetworkBuffer var22 = buffer;
				var22.offset += var7;
				remaining += var7;

				if (lengthRead == buffer.offset)
				{
					System.out.println("received " + currentResponse.key);
					currentResponse.consumer.accept(buffer.array);
					remaining = 0;
					currentResponse = null;
					buffer = null;
				}
				else
				{
					if (remaining != 512)
					{
						break;
					}

					remaining = 0;
				}
			}
		}

		return false;
	}

	public static void request(int archive, int group, boolean priority, Consumer<byte[]> consumer)
	{
		Request request = new Request(key(archive, group), priority, consumer);

		if (priority)
		{
			requests.add(request);
		}
		else
		{
			requests.addFirst(request);
		}
	}

	private static int key(int archive, int group)
	{
		return (archive << 16) + group;
	}

	private static class Request
	{
		public final long key;
		public final boolean priority;
		public final Consumer<byte[]> consumer;

		public Request(long key, boolean priority, Consumer<byte[]> consumer)
		{
			this.key = key;
			this.priority = priority;
			this.consumer = consumer;
		}
	}
}
