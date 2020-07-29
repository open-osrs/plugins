package com.jagex.runescape377;

import com.jagex.runescape377.cache.media.ImageRGB;
import com.jagex.runescape377.cache.media.Widget;
import com.jagex.runescape377.media.ProducingGraphicsBuffer;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

@SuppressWarnings("serial")
public class GameShell extends Canvas implements Runnable, MouseListener, MouseMotionListener, KeyListener,
	MouseWheelListener, FocusListener, WindowListener
{


	public static GameFrame gameFrame;
	public int mindel = 1;
	public int fps;
	public int cameraZoom = 600;
	public boolean dumpRequested = false;
	public int width;
	public int height;
	public int extraWidth = 0;
	public int extraHeight = 22;
	public Graphics gameGraphics;
	public ProducingGraphicsBuffer imageProducer;
	public ImageRGB aClass50_Sub1_Sub1_Sub1Array16[] = new ImageRGB[6];
	public boolean clearScreen = true;
	public boolean awtFocus = true;
	public int idleTime;
	public int mouseButtonPressed;
	public int mouseX;
	public int mouseY;
	public int eventMouseButtonPressed;
	public int eventClickX;
	public int eventClickY;
	public long lastClick;
	public int clickType;
	public int clickX;
	public int clickY;
	public long clickTime;
	public int keyStatus[] = new int[128];
	public boolean mouseWheelDown;
	public int mouseWheelX;
	public int mouseWheelY;
	private int gameState;
	private int deltime = 20;
	private long optims[] = new long[10];
	private int inputBuffer[] = new int[128];
	private int readIndex;
	private int writeIndex;

	public final void initializeApplication(int _width, int _height)
	{
		width = _width;
		height = _height;
		gameFrame = new GameFrame(this, width, height);
		gameGraphics = gameFrame.getGraphics();
//        this.height = this.height + this.extraHeight;
//        gameGraphics.translate(extraWidth, extraHeight);
		imageProducer = new ProducingGraphicsBuffer(width, height, gameFrame);
//        this.setPreferredSize(new Dimension(width, height));
//        this.setMaximumSize(new Dimension(width, height));
//        this.setMinimumSize(new Dimension(width, height));
//        gameFrame.add(this);
//        gameFrame.pack();

		startRunnable(this, 1);
	}

	public final void initializeApplet(int width, int height)
	{
		this.width = width;
		this.height = height;
		gameGraphics = gameFrame.getGraphics();
		imageProducer = new ProducingGraphicsBuffer(this.width, this.height, getParentComponent());
		startRunnable(this, 1);
	}

	public void run()
	{
		gameFrame.addMouseListener(this);
		gameFrame.addMouseMotionListener(this);
		gameFrame.addMouseWheelListener(this);
		gameFrame.addKeyListener(this);
		gameFrame.addFocusListener(this);

		drawLoadingText(0, "Loading...");
		startup();
		int opos = 0;
		int ratio = 256;
		int del = 1;
		int count = 0;
		int intex = 0;
		for (int optim = 0; optim < 10; optim++)
		{
			optims[optim] = System.currentTimeMillis();
		}

		while (gameState >= 0)
		{
			if (gameState > 0)
			{
				gameState--;
				if (gameState == 0)
				{
					exit();
					return;
				}
			}
			ratio = 300;
			del = 1;
			long currentTime = System.currentTimeMillis();
			if (currentTime > optims[opos])
			{
				ratio = (int) ((2560 * deltime) / (currentTime - optims[opos]));
			}
			if (ratio < 25)
			{
				ratio = 25;
			}
			if (ratio > 256)
			{
				ratio = 256;
				del = (int) (deltime - (currentTime - optims[opos]) / 10L);
			}
			if (del > deltime)
			{
				del = deltime;
			}
			optims[opos] = currentTime;
			opos = (opos + 1) % 10;
			if (del > 1)
			{
				for (int optim = 0; optim < 10; optim++)
				{
					if (optims[optim] != 0L)
					{
						optims[optim] += del;
					}
				}

			}
			if (del < mindel)
			{
				del = mindel;
			}
			try
			{
				Thread.sleep(del);
			}
			catch (InterruptedException _ex)
			{
				intex++;
			}
			for (; count < 256; count += ratio)
			{
				clickType = eventMouseButtonPressed;
				clickX = eventClickX;
				clickY = eventClickY;
				clickTime = lastClick;
				eventMouseButtonPressed = 0;
				processGameLoop();
				readIndex = writeIndex;
			}

			count &= 0xff;
			if (deltime > 0)
			{
				fps = (1000 * ratio) / (deltime * 256);
			}
			repaintGame();
			if (dumpRequested)
			{
				System.out.println("ntime:" + currentTime);
				for (int i = 0; i < 10; i++)
				{
					int optim = ((opos - i - 1) + 20) % 10;
					System.out.println("otim" + optim + ":" + optims[optim]);
				}

				System.out.println("fps:" + fps + " ratio:" + ratio + " count:" + count);
				System.out.println("del:" + del + " deltime:" + deltime + " mindel:" + mindel);
				System.out.println("intex:" + intex + " opos:" + opos);
				dumpRequested = false;
				intex = 0;
			}
		}
		if (gameState == -1)
		{
			exit();
		}
	}

	public void exit()
	{
		gameState = -2;
		shutdown();
		if (gameFrame != null)
		{
			try
			{
				Thread.sleep(1000L);
			}
			catch (Exception _ex)
			{
			}
			System.exit(0);
		}
	}

	public void setFrameRate(int i)
	{
		deltime = 1000 / i;
	}

	public void start()
	{
		if (gameState >= 0)
		{
			gameState = 0;
		}
	}

	public void stop()
	{
		if (gameState >= 0)
		{
			gameState = 4000 / deltime;
		}
	}

	public void destroy()
	{
		gameState = -1;
		try
		{
			Thread.sleep(10000L);
		}
		catch (Exception _ex)
		{
		}
		if (gameState == -1)
		{
			exit();
		}
	}

	public void update()
	{
		clearScreen = true;
		redraw();
	}

	public void paint()
	{
		clearScreen = true;
		redraw();
	}

	public void mouseClicked(MouseEvent mouseevent)
	{
	}

	public void mousePressed(MouseEvent mouseevent)
	{
		int mouseX = mouseevent.getX();
		int mouseY = mouseevent.getY();
		if (gameFrame != null)
		{
			mouseX -= extraWidth;
			mouseY -= 2 + extraHeight;
		}
		idleTime = 0;
		eventClickX = mouseX;
		eventClickY = mouseY;
		lastClick = System.currentTimeMillis();
		if (mouseevent.getButton() == MouseEvent.BUTTON2)
		{
			mouseWheelDown = true;
			mouseWheelX = mouseX;
			mouseWheelY = mouseY;
			return;
		}
		if (mouseevent.isMetaDown() || mouseevent.getButton() == MouseEvent.BUTTON3)
		{
			eventMouseButtonPressed = 2;
			mouseButtonPressed = 2;
		}
		else
		{
			eventMouseButtonPressed = 1;
			mouseButtonPressed = 1;
		}
	}

	public void mouseReleased(MouseEvent mouseevent)
	{
		idleTime = 0;
		mouseButtonPressed = 0;
		mouseWheelDown = false;
	}

	public void mouseEntered(MouseEvent mouseevent)
	{
	}

	public void mouseExited(MouseEvent mouseevent)
	{
		idleTime = 0;
		mouseX = -1;
		mouseY = -1;
	}

	public void mouseDragged(MouseEvent mouseevent)
	{
		int mouseX = mouseevent.getX();
		int mouseY = mouseevent.getY();
		if (gameFrame != null)
		{
			mouseX -= extraWidth;
			mouseY -= 2 + extraHeight;
		}
		if (mouseWheelDown)
		{
			mouseY = mouseWheelX - mouseevent.getX();
			int k = mouseWheelY - mouseevent.getY();
			mouseWheelDragged(mouseY, -k);
			mouseWheelX = mouseevent.getX();
			mouseWheelY = mouseevent.getY();
			return;
		}
		idleTime = 0;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	public void mouseMoved(MouseEvent mouseevent)
	{
		int mouseX = mouseevent.getX();
		int mouseY = mouseevent.getY();
		if (gameFrame != null)
		{
			mouseX -= extraWidth;
			mouseY -= 2 + extraHeight;
		}
		idleTime = 0;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	void mouseWheelDragged(int param1, int param2)
	{

	}

	public void keyTyped(KeyEvent keyevent)
	{
	}

	public void keyPressed(KeyEvent keyevent)
	{
		idleTime = 0;
		int keyCode = keyevent.getKeyCode();
		int keyChar = keyevent.getKeyChar();
		if (keyChar < 30)
		{
			keyChar = 0;
		}
		if (keyCode == 37)
		{
			keyChar = 1;
		}
		if (keyCode == 39)
		{
			keyChar = 2;
		}
		if (keyCode == 38)
		{
			keyChar = 3;
		}
		if (keyCode == 40)
		{
			keyChar = 4;
		}
		if (keyCode == 17)
		{
			keyChar = 5;
		}
		if (keyCode == 8)
		{
			keyChar = 8;
		}
		if (keyCode == 127)
		{
			keyChar = 8;
		}
		if (keyCode == 9)
		{
			keyChar = 9;
		}
		if (keyCode == 10)
		{
			keyChar = 10;
		}
		if (keyCode >= 112 && keyCode <= 123)
		{
			keyChar = (1008 + keyCode) - 112;
		}
		if (keyCode == 36)
		{
			keyChar = 1000;
		}
		if (keyCode == 35)
		{
			keyChar = 1001;
		}
		if (keyCode == 33)
		{
			keyChar = 1002;
		}
		if (keyCode == 34)
		{
			keyChar = 1003;
		}
		if (keyChar > 0 && keyChar < 128)
		{
			keyStatus[keyChar] = 1;
		}
		if (keyChar > 4)
		{
			inputBuffer[writeIndex] = keyChar;
			writeIndex = writeIndex + 1 & 0x7f;
		}
	}

	public void keyReleased(KeyEvent keyevent)
	{
		idleTime = 0;
		int keyCode = keyevent.getKeyCode();
		char keyChar = keyevent.getKeyChar();
		if (keyChar < '\036')
		{
			keyChar = '\0';
		}
		if (keyCode == 37)
		{
			keyChar = '\001';
		}
		if (keyCode == 39)
		{
			keyChar = '\002';
		}
		if (keyCode == 38)
		{
			keyChar = '\003';
		}
		if (keyCode == 40)
		{
			keyChar = '\004';
		}
		if (keyCode == 17)
		{
			keyChar = '\005';
		}
		if (keyCode == 8)
		{
			keyChar = '\b';
		}
		if (keyCode == 127)
		{
			keyChar = '\b';
		}
		if (keyCode == 9)
		{
			keyChar = '\t';
		}
		if (keyCode == 10)
		{
			keyChar = '\n';
		}
		if (keyChar > 0 && keyChar < '\200')
		{
			keyStatus[keyChar] = 0;
		}
	}

	public int readCharacter()
	{
		int character = -1;
		if (writeIndex != readIndex)
		{
			character = inputBuffer[readIndex];
			readIndex = readIndex + 1 & 0x7f;
		}
		return character;
	}

	public void focusGained(FocusEvent focusevent)
	{
		awtFocus = true;
		clearScreen = true;
		redraw();
	}

	public void focusLost(FocusEvent focusevent)
	{
		awtFocus = false;
		for (int key = 0; key < 128; key++)
		{
			keyStatus[key] = 0;
		}

	}

	public void windowOpened(WindowEvent windowevent)
	{
	}

	public void windowClosing(WindowEvent windowevent)
	{
		destroy();
	}

	public void windowClosed(WindowEvent windowevent)
	{
	}

	public void windowIconified(WindowEvent windowevent)
	{
	}

	public void windowDeiconified(WindowEvent windowevent)
	{
	}

	public void windowActivated(WindowEvent windowevent)
	{
	}

	public void windowDeactivated(WindowEvent windowevent)
	{
	}

	public void startup()
	{
	}

	public void processGameLoop()
	{
	}

	public void shutdown()
	{
	}

	public void repaintGame()
	{
	}

	public void redraw()
	{
	}

	public Component getParentComponent()
	{
		if (gameFrame != null)
		{
			return gameFrame;
		}
		else
		{
			return this;
		}
	}

	public void startRunnable(Runnable runnable, int priority)
	{
		Thread thread = new Thread(runnable);
		thread.start();
		thread.setPriority(priority);
	}

	public void mouseWheelMoved(MouseWheelEvent event)
	{
		int rotation = event.getWheelRotation();
		if (this instanceof Game)
		{
			if (!handleInterfaceScrolling(event, (Game) this))
			{
				if ((cameraZoom <= 300 && rotation <= 0)
					|| (cameraZoom >= 1200 && rotation >= 0))
				{
					return;
				}
				int diff = rotation * 8;
				cameraZoom = cameraZoom + diff;
			}
		}
	}

	public boolean handleInterfaceScrolling(MouseWheelEvent event, Game client)
	{
		int rotation = event.getWheelRotation();
		if (mouseX > 0 && mouseY > 346 && mouseX < 516 && mouseY < 505 && client.openChatboxWidgetId == -1)
		{
			if (rotation < 0)
			{
				if (client.chatboxInterface.scrollPosition >= 1)
				{

					if (client.inputType == 3)
					{
						client.itemSearchScroll = client.itemSearchScroll - 30;
						client.redrawChatbox = true;
					}
					else
					{
						client.chatboxScroll = client.chatboxScroll + 30;
						client.redrawChatbox = true;
					}
				}
			}
			else
			{
				if (client.inputType == 3)
				{
					client.itemSearchScroll = client.itemSearchScroll + 30;
					client.redrawChatbox = true;
				}
				else if (client.chatboxScroll < 1)
				{
					client.chatboxScroll = 0;
					client.redrawChatbox = true;
				}
				else
				{
					client.chatboxScroll = client.chatboxScroll - 30;
					client.redrawChatbox = true;
				}
			}
			return true;
		}
		else
		{
			int positionX = 0;
			int positionY = 0;
			int width = 0;
			int height = 0;
			int offsetX = 0;
			int offsetY = 0;
			int childID = 0;
			/* Tab interface scrolling */
			int tabInterfaceID = client.tabWidgetIds[client.currentTabId];
			if (tabInterfaceID != -1)
			{
				Widget tab = Widget.interfaces[tabInterfaceID];
				offsetX = 765 - 218;
				offsetY = 503 - 298;
				for (int index = 0; index < tab.children.length; index++)
				{
					if (Widget.interfaces[tab.children[index]].scrollLimit > 0)
					{
						childID = index;
						positionX = tab.childrenX[index];
						positionY = tab.childrenY[index];
						width = Widget.interfaces[tab.children[index]].width;
						height = Widget.interfaces[tab.children[index]].height;
						break;
					}
				}
				if (mouseX > offsetX + positionX && mouseY > offsetY + positionY && mouseX < offsetX + positionX + width && mouseY < offsetY + positionY + height)
				{
					Widget.interfaces[tab.children[childID]].scrollPosition += rotation * 30;
//				client.tabAreaAltered = true;
					client.redrawTabArea = true;
					return true;
				}
			}
			/* Main interface scrolling */
			if (client.openScreenWidgetId != -1)
			{
				Widget widget = Widget.interfaces[client.openScreenWidgetId];
				offsetX = 4;
				offsetY = 4;
				for (int index = 0; index < widget.children.length; index++)
				{
					if (Widget.interfaces[widget.children[index]].scrollLimit > 0)
					{
						childID = index;
						positionX = widget.childrenX[index];
						positionY = widget.childrenY[index];
						width = Widget.interfaces[widget.children[index]].width;
						height = Widget.interfaces[widget.children[index]].height;
						break;
					}
				}
				if (mouseX > offsetX + positionX && mouseY > offsetY + positionY && mouseX < offsetX + positionX + width && mouseY < offsetY + positionY + height)
				{
					Widget.interfaces[widget.children[childID]].scrollPosition += rotation * 30;
					return true;
				}

			}
		}
		return false;


	}

	public void drawLoadingText(int percent, String desc)
	{
		while (gameGraphics == null)
		{
			gameGraphics = gameFrame.getGraphics();
			try
			{
				gameFrame.repaint();
			}
			catch (Exception _ex)
			{
			}
			try
			{
				Thread.sleep(1000L);
			}
			catch (Exception _ex)
			{
			}
		}
		Font helveticaBold = new Font("Helvetica", 1, 13);
		FontMetrics fontmetrics = getParentComponent().getFontMetrics(helveticaBold);
		Font helvetica = new Font("Helvetica", 0, 13);
		getParentComponent().getFontMetrics(helvetica);
		if (clearScreen)
		{
			gameGraphics.setColor(Color.black);
			gameGraphics.fillRect(0, 0, width, height);
			clearScreen = false;
		}
		Color color = new Color(140, 17, 17);
		int centerHeight = height / 2 - 18;
		gameGraphics.setColor(color);
		gameGraphics.drawRect(width / 2 - 152, centerHeight, 304, 34);
		gameGraphics.fillRect(width / 2 - 150, centerHeight + 2, percent * 3, 30);
		gameGraphics.setColor(Color.black);
		gameGraphics.fillRect((width / 2 - 150) + percent * 3, centerHeight + 2, 300 - percent * 3, 30);
		gameGraphics.setFont(helveticaBold);
		gameGraphics.setColor(Color.white);
		gameGraphics.drawString(desc, (width - fontmetrics.stringWidth(desc)) / 2, centerHeight + 22);
	}


}
