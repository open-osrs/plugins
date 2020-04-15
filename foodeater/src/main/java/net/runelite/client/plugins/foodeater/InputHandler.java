package net.runelite.client.plugins.foodeater;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.Varbits;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class InputHandler {
	public static int getTabHotkey(Client client, Varbits tab) {
		assert client.isClientThread();

		final int var = client.getVarbitValue(client.getVarps(), tab.getId());
		final int offset = 111;

		switch (var) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
				return var + offset;
			case 13:
				return 27;
			default:
				return -1;
		}
	}

	public static void leftClick(Client client, Point pos)
	{
		assert !client.isClientThread();

		if (client.isStretchedEnabled()) {
			final Dimension stretched = client.getStretchedDimensions();
			final Dimension real = client.getRealDimensions();
			final double width = (stretched.width / real.getWidth());
			final double height = (stretched.height / real.getHeight());
			final Point point = new Point((int) (pos.getX() * width), (int) (pos.getY() * height));
			moveMouse(client, point);
			clickMouse(client, point, 1);
			return;
		}

		moveMouse(client, pos);
		clickMouse(client, pos, 1);
	}

	public static void click(Client client)
	{
		Point pos = client.getMouseCanvasPosition();

		if (client.isStretchedEnabled()) {
			final Dimension stretched = client.getStretchedDimensions();
			final Dimension real = client.getRealDimensions();
			final double width = (stretched.width / real.getWidth());
			final double height = (stretched.height / real.getHeight());
			final Point point = new Point((int) (pos.getX() * width), (int) (pos.getY() * height));
			clickMouse(client, point, 1);
			return;
		}

		clickMouse(client, pos, 1);
	}
	
	private static MouseEvent createEvent(Client client, Point p, int id)
	{
		return new MouseEvent(client.getCanvas(), id, System.currentTimeMillis(), 0, p.getX(), p.getY(), 0, false);
	}
	
	private static MouseEvent createEvent(Client client, Point p, int id, int button)
	{
		return new MouseEvent(client.getCanvas(), id, System.currentTimeMillis(), 0, p.getX(), p.getY(), 1, false, button);
	}
	
	private static void clickMouse(Client client, Point p, int button)
	{
		client.getCanvas().dispatchEvent(createEvent(client, p, 501, button));
		client.getCanvas().dispatchEvent(createEvent(client, p, 502, button));
		client.getCanvas().dispatchEvent(createEvent(client, p, 500, button));
	}
	
	private static void moveMouse(Client client, Point p)
	{
		client.getCanvas().dispatchEvent(createEvent(client, p, 504));
		client.getCanvas().dispatchEvent(createEvent(client, p, 505));
		client.getCanvas().dispatchEvent(createEvent(client, p, 503));

		try
		{
			//sleep for 2 frames (just to be sure we get the menu option loaded)
			Thread.sleep(client.getFPS() / 5);
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
	}
	
	public static Point getClickPoint(Rectangle rect)
	{
		int rand = (Math.random() <= 0.5) ? 1 : 2;
		int x = (int) (rect.getX() + (rand * 3) + rect.getWidth() / 2);
		int y = (int) (rect.getY() + (rand * 3) + rect.getHeight() / 2);
		return new Point(x, y);
	}
	
	public static void sendKey(Component target, int key)
	{
		target.dispatchEvent(new KeyEvent(target, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, key, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD));
		target.dispatchEvent(new KeyEvent(target, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, key, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD));
	}
	
	public static void pressKey(Component target, int key)
	{
		target.dispatchEvent(new KeyEvent(target, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, key, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD));
	}
	
	public static void releaseKey(Component target, int key)
	{
		target.dispatchEvent(new KeyEvent(target, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, key, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD));
	}
}
