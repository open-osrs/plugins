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

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.DrawFinished;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Canvas;
import java.awt.image.BufferedImage;

/**
 * Mirror Plugin - Creates a new window that draws only the game canvas, and ignores the AFTER_MIRROR Overlay layer
 */
@Extension
@PluginDescriptor(
	name = "Mirror",
	description = "Create a new window with the game image minus the top overlay layer",
	type = PluginType.UTILITY,
	enabledByDefault = false
)
public class MirrorPlugin extends Plugin
{

	@Inject
	private Client client;

	@Inject
	private MirrorConfig config;

	@Inject
	private ClientThread clientThread;

	@Provides
	MirrorConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MirrorConfig.class);
	}

	public static JFrame jframe;
	public static final Canvas canvas = new Canvas();
	public static BufferedImage bufferedImage;


	public void updateTitle()
	{
		clientThread.invokeLater(() ->
		{
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				final Player player = client.getLocalPlayer();

				if (player == null)
				{
					return false;
				}

				final String name = player.getName();

				if (Strings.isNullOrEmpty(name))
				{
					return false;
				}

				if (jframe != null)
				{
					if (config.mirrorName())
					{
						jframe.setTitle("OpenOSRS Mirror" + " - " + name);
					}
					else
					{
						jframe.setTitle("OpenOSRS Mirror");
					}

				}
			}
			else
			{
				if (jframe != null)
				{
					jframe.setTitle("OpenOSRS Mirror");
					return true;
				}
			}
			return false;
		});
	}
	@Override
	public void startUp()
	{
		if (jframe == null)
		{
			jframe = new JFrame("OpenOSRS Mirror");
			jframe.setSize(1280, 720);
			canvas.setSize(1280, 720);
			jframe.add(canvas);
		}
		client.setMirrored(true);

		if (!jframe.isVisible())
		{
			jframe.setVisible(true);
		}
	}

	@Override
	public void shutDown()
	{
		if (jframe != null)
		{
			jframe.dispose();
			jframe = null;
		}
		client.setMirrored(false);
	}

	@Subscribe
	private void onDrawFinished(DrawFinished event)
	{
		if (!jframe.isVisible())
		{
			jframe.setVisible(true);
		}

		if (canvas.getWidth() != event.image.getWidth(canvas) + 15 || (canvas.getHeight() != event.image.getHeight(canvas) + 40))
		{
			canvas.setSize(event.image.getWidth(canvas) + 15, event.image.getHeight(canvas) + 40);
			jframe.setSize(canvas.getSize());
		}
		canvas.getGraphics().drawImage(event.image, 0, 0, jframe);
	}

	@Subscribe
	private void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		updateTitle();
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("mirror"))
		{
			return;
		}

		SwingUtilities.invokeLater(() ->
		{
			if (jframe != null)
			{
				updateTitle();
			}
		});
	}
}
