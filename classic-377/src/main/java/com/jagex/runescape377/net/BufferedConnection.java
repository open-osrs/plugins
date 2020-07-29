package com.jagex.runescape377.net;

import com.jagex.runescape377.GameShell;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class BufferedConnection implements Runnable
{

	public InputStream inputStream;
	public OutputStream outputStream;
	public Socket socket;
	public boolean closed = false;
	public GameShell gameStub;
	public byte[] buffer;
	public int writerPosition;
	public int bufferPosition;
	public boolean writing = false;
	public boolean ioError = false;

	public BufferedConnection(GameShell gameStub, Socket socket) throws IOException
	{
		this.gameStub = gameStub;
		this.socket = socket;
		this.socket.setSoTimeout(30000);
		this.socket.setTcpNoDelay(true);
		this.inputStream = socket.getInputStream();
		this.outputStream = socket.getOutputStream();
	}

	public void close()
	{
		closed = true;
		try
		{
			if (inputStream != null)
			{
				inputStream.close();
			}
			if (outputStream != null)
			{
				outputStream.close();
			}
			if (socket != null)
			{
				socket.close();
			}
		}
		catch (IOException _ex)
		{
			System.out.println("Error closing stream");
		}
		writing = false;
		synchronized (this)
		{
			notify();
		}
		buffer = null;
	}

	public int read() throws IOException
	{
		if (closed)
		{
			return 0;
		}
		else
		{
			return inputStream.read();
		}
	}

	public int getAvailable() throws IOException
	{
		if (closed)
		{
			return 0;
		}
		else
		{
			return inputStream.available();
		}
	}

	public void read(byte[] src, int offset, int length) throws IOException
	{
		if (!closed)
		{
			int byteRead;
			for (; length > 0; length -= byteRead)
			{
				byteRead = inputStream.read(src, offset, length);
				if (byteRead <= 0)
				{
					throw new IOException("EOF");
				}
				offset += byteRead;
			}
		}
	}

	public void write(int length, int offset, byte[] src) throws IOException
	{
		if (closed)
		{
			return;
		}
		if (ioError)
		{
			ioError = false;
			throw new IOException("Error in writer thread");
		}
		if (buffer == null)
		{
			buffer = new byte[5000];
		}
		synchronized (this)
		{
			for (int position = 0; position < length; position++)
			{
				buffer[bufferPosition] = src[position + offset];
				bufferPosition = (bufferPosition + 1) % 5000;
				if (bufferPosition == (writerPosition + 4900) % 5000)
				{
					throw new IOException("buffer overflow");
				}
			}

			if (!writing)
			{
				writing = true;
				gameStub.startRunnable(this, 3);
			}
			notify();
		}
	}

	public void run()
	{
		while (writing)
		{
			int writerLength;
			synchronized (this)
			{
				if (bufferPosition == writerPosition)
				{
					try
					{
						wait();
					}
					catch (InterruptedException _ex)
					{
					}
				}
				if (!writing)
				{
					return;
				}
				if (bufferPosition >= writerPosition)
				{
					writerLength = bufferPosition - writerPosition;
				}
				else
				{
					writerLength = 5000 - writerPosition;
				}
			}
			if (writerLength > 0)
			{
				try
				{
					outputStream.write(buffer, writerPosition, writerLength);
				}
				catch (IOException _ex)
				{
					ioError = true;
				}
				writerPosition = (writerPosition + writerLength) % 5000;
				try
				{
					if (bufferPosition == writerPosition)
					{
						outputStream.flush();
					}
				}
				catch (IOException _ex)
				{
					ioError = true;
				}
			}
		}
	}

	public void printDebug()
	{
		System.out.println("dummy:" + closed);
		System.out.println("tcycl:" + writerPosition);
		System.out.println("tnum:" + bufferPosition);
		System.out.println("writer:" + writing);
		System.out.println("ioerror:" + ioError);
		try
		{
			System.out.println("available:" + getAvailable());
		}
		catch (IOException _ex)
		{
		}
	}

}
