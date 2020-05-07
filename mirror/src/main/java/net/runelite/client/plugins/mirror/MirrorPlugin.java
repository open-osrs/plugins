/*
 * Copyright (c) 2020, Null (zeruth) <TheRealNulL@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.mirror;

import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.DrawFinished;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;
import java.awt.Canvas;
import java.awt.Image;
import javax.inject.Inject;
import javax.swing.JFrame;
import java.awt.event.MouseEvent;

/**
	Mirror Plugin - Creates a new window that draws only the game canvas, and ignores the AFTER_MIRROR Overlay layer
 */
@Extension
@PluginDescriptor(
	name = "Mirror",
	description = "Create a new window with the game image minus the top overlay layer",
	type = PluginType.UTILITY,
	enabledByDefault = false
)
public class MirrorPlugin extends Plugin implements MouseListener
{

	private int mouseX = 0;
	private int mouseY = 0;
	public static JFrame jframe;
	public final Canvas canvas = new Canvas();
	private final Image cursor = ImageUtil.getResourceStreamFromClass(MirrorPlugin.class, "cursor.png");

	@Inject
	private MouseManager mouseManager;

	@Override
	public void startUp()
	{
		if (jframe == null)
		{
			jframe = new JFrame("OpenOSRS");
			jframe.setSize(1280, 720);
			canvas.setSize(1280, 720);
			jframe.add(canvas);
		}
		mouseManager.registerMouseListener(this);
	}

	@Override
	public void shutDown()
	{
		if (jframe != null)
		{
			jframe.dispose();
			jframe = null;
		}
		mouseManager.unregisterMouseListener(this);
	}

	@Subscribe
	private void onDrawFinished(DrawFinished event)
	{

		if (!jframe.isVisible())
			jframe.setVisible(true);

		if (canvas.getWidth() != event.image.getWidth(canvas) + 14 || (canvas.getHeight() != event.image.getHeight(canvas) + 40))
			{
				canvas.setSize(event.image.getWidth(canvas) + 14, event.image.getHeight(canvas) + 40);
				jframe.setSize(canvas.getSize());
			}
		event.image.getGraphics().drawImage(cursor, mouseX, mouseY, canvas);
		canvas.getGraphics().drawImage(event.image, 0, 0, canvas);
	}


	@Override
	public MouseEvent mouseClicked(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mousePressed(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseReleased(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseEntered(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseExited(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseDragged(MouseEvent mouseEvent)
	{
		mouseX = mouseEvent.getX();
		mouseY = mouseEvent.getY();
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseMoved(MouseEvent mouseEvent)
	{
		mouseX = mouseEvent.getX();
		mouseY = mouseEvent.getY();
		return mouseEvent;
	}
}
