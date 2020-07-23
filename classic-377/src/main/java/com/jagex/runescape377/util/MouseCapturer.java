package com.jagex.runescape377.util;

import com.jagex.runescape377.Game;

public class MouseCapturer implements Runnable
{
	public Game _client;
	public boolean capturing = true;
	public int coordsY[] = new int[500];
	public Object objectLock = new Object();
	public Game client;
	public int coord;
	public int coordsX[] = new int[500];


	public MouseCapturer(Game _client)
	{
		client = _client;
	}

	public void run()
	{
		while (capturing)
		{
			synchronized (objectLock)
			{
				if (coord < 500)
				{
					coordsX[coord] = client.mouseX;
					coordsY[coord] = client.mouseY;
					coord++;
				}
			}
			try
			{
				Thread.sleep(50L);
			}
			catch (Exception _ex)
			{
			}
		}
	}

}
