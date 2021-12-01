package net.runelite.client.plugins.mirrormode;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provides;
import java.awt.Canvas;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
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
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Mirror Mode",
	description = "Create a new window with the game image minus the top overlay layer",
	enabledByDefault = false,
	conflicts = {"GPU", "117 HD (beta)"}
)
public class MirrorModePlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private MirrorModeConfig config;
	@Inject
	private ClientThread clientThread;
	public static JFrame jframe;
	public static final Canvas canvas;
	public static BufferedImage bufferedImage;

	@Provides
	MirrorModeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MirrorModeConfig.class);
	}

	public void updateTitle()
	{
		this.clientThread.invokeLater(() ->
		{
			if (this.client.getGameState() == GameState.LOGGED_IN)
			{
				Player player = this.client.getLocalPlayer();
				if (player == null)
				{
					return false;
				}

				String name = player.getName();
				if (Strings.isNullOrEmpty(name))
				{
					return false;
				}

				if (jframe != null)
				{
					if (this.config.mirrorName())
					{
						jframe.setTitle("OpenOSRS Mirror - " + name);
					}
					else
					{
						jframe.setTitle("OpenOSRS Mirror");
					}
				}
			}
			else if (jframe != null)
			{
				jframe.setTitle("OpenOSRS Mirror");
				return true;
			}

			return false;
		});
	}

	public void startUp()
	{
		if (jframe == null)
		{
			jframe = new JFrame("OpenOSRS Mirror");
			jframe.setSize(1280, 720);
			canvas.setSize(1280, 720);
			jframe.add(canvas);
		}

		this.client.setMirrored(true);
		if (!jframe.isVisible())
		{
			jframe.setVisible(true);
		}

	}

	public void shutDown()
	{
		if (jframe != null)
		{
			jframe.dispose();
			jframe = null;
		}

		this.client.setMirrored(false);
	}

	@Subscribe
	private void onDrawFinished(DrawFinished event)
	{
		if (!jframe.isVisible())
		{
			jframe.setVisible(true);
		}

		if (canvas.getWidth() != event.image.getWidth(canvas) + 15 || canvas.getHeight() != event.image.getHeight(canvas) + 40)
		{
			canvas.setSize(event.image.getWidth(canvas) + 15, event.image.getHeight(canvas) + 40);
			jframe.setSize(canvas.getSize());
		}

		canvas.getGraphics().drawImage(event.image, 0, 0, jframe);
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			this.updateTitle();
		}
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("mirror"))
		{
			SwingUtilities.invokeLater(() ->
			{
				if (jframe != null)
				{
					this.updateTitle();
				}

			});
		}
	}

	static
	{
		canvas = new Canvas();
	}
}