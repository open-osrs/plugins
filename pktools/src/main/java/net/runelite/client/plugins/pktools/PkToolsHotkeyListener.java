package net.runelite.client.plugins.pktools;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.plugins.pktools.ScriptCommand.ScriptCommandFactory;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PkToolsHotkeyListener extends MouseAdapter implements KeyListener
{
	private final Client client;

	private Instant lastPress;

	@Inject
	private PkToolsPlugin plugin;

	@Inject
	private PkToolsConfig config;

	@Inject
	private PkToolsOverlay overlay;

	@Inject
	private ConfigManager configManager;

	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 25, TimeUnit.SECONDS, queue,
		new ThreadPoolExecutor.DiscardPolicy());

	@Inject
	private PkToolsHotkeyListener(final Client client, final PkToolsConfig config, final PkToolsPlugin plugin, final PkToolsOverlay overlay)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		this.overlay = overlay;
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (this.client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (e.getKeyCode() == this.config.prayerKey().getKeyCode())
		{
			configManager.setConfiguration("pktools", "autoPrayerSwitcherEnabled", !config.autoPrayerSwitcherEnabled());
		}


		try
		{
			if (this.lastPress != null && Duration.between(this.lastPress, Instant.now()).getNano() > 1000)
			{
				this.lastPress = null;
			}

			if (this.lastPress != null)
			{
				return;
			}

			final int key_code = e.getKeyCode();

			executor.submit(() -> {
				if (key_code == this.config.key1().getKeyCode())
				{
					this.processCommands(this.config.key1_script(), this.client, this.config, this.plugin, this.overlay, this.configManager);
				}
				else if (key_code == this.config.key2().getKeyCode())
				{
					this.processCommands(this.config.key2_script(), this.client, this.config, this.plugin, this.overlay, this.configManager);
				}
				else if (key_code == this.config.key3().getKeyCode())
				{
					this.processCommands(this.config.key3_script(), this.client, this.config, this.plugin, this.overlay, this.configManager);
				}
				else if (key_code == this.config.key4().getKeyCode())
				{
					this.processCommands(this.config.key4_script(), this.client, this.config, this.plugin, this.overlay, this.configManager);
				}
				else if (key_code == this.config.key5().getKeyCode())
				{
					this.processCommands(this.config.key5_script(), this.client, this.config, this.plugin, this.overlay, this.configManager);
				}
			});
		}
		catch (final Throwable ex)
		{
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void processCommands(final String command, final Client client, final PkToolsConfig config, final PkToolsPlugin plugin, final PkToolsOverlay overlay, final ConfigManager configManager)
	{
		for (final String c : command.split("\\s*\n\\s*"))
		{
			ScriptCommandFactory.builder(c).execute(client, config, plugin, overlay, configManager);
		}
	}

	public static String getTag(final ConfigManager configManager, final int itemId)
	{
		final String tag = configManager.getConfiguration("inventorytags", "item_" + itemId);
		if (tag == null || tag.isEmpty())
		{
			return "";
		}

		return tag;
	}

	@Override
	public void keyTyped(final KeyEvent e)
	{
	}

	@Override
	public void keyReleased(final KeyEvent e)
	{
	}
}
