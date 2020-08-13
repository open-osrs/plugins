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

public class PkToolsHotkeyListener extends MouseAdapter implements KeyListener
{
	private Client client;

	private Instant lastPress;

	@Inject
	private PkToolsPlugin plugin;

	@Inject
	private PkToolsConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private PkToolsHotkeyListener(Client client, PkToolsConfig config, PkToolsPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (e.getKeyCode() == config.prayerKey().getKeyCode())
		{
			configManager.setConfiguration("pktools", "autoPrayerSwitcherEnabled", !config.autoPrayerSwitcherEnabled());
		}

		try
		{
			if (lastPress != null && Duration.between(lastPress, Instant.now()).getNano() > 1000)
			{
				lastPress = null;
			}

			if (lastPress != null)
			{
				return;
			}

			int key_code = e.getKeyCode();

			if (key_code == config.key1().getKeyCode())
			{
				addCommands(config.key1_script(), plugin);
			}
			else if (key_code == config.key2().getKeyCode())
			{
				addCommands(config.key2_script(), plugin);
			}
			else if (key_code == config.key3().getKeyCode())
			{
				addCommands(config.key3_script(), plugin);
			}
			else if (key_code == config.key4().getKeyCode())
			{
				addCommands(config.key4_script(), plugin);
			}
			else if (key_code == config.key5().getKeyCode())
			{
				addCommands(config.key5_script(), plugin);
			}
			else if (key_code == config.key6().getKeyCode())
			{
				addCommands(config.key6_script(), plugin);
			}
			else if (key_code == config.key7().getKeyCode())
			{
				addCommands(config.key7_script(), plugin);
			}
			else if (key_code == config.key8().getKeyCode())
			{
				addCommands(config.key8_script(), plugin);
			}
		}
		catch (Throwable ex)
		{
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void addCommands(String command, PkToolsPlugin plugin)
	{
		for (String c : command.split("\\s*\n\\s*"))
		{
			plugin.commandList.add(ScriptCommandFactory.builder(c));
		}
	}

	public static String getTag(ConfigManager configManager, int itemId)
	{
		String tag = configManager.getConfiguration("inventorytags", "item_" + itemId);
		if (tag == null || tag.isEmpty())
		{
			return "";
		}

		return tag;
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}
}
