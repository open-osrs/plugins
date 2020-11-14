package com.openosrs.classic377;

import com.google.inject.Provides;
import com.jagex.runescape377.Game;
import com.jagex.runescape377.GameShell;
import com.jagex.runescape377.config.Configuration;
import com.jagex.runescape377.util.SignLink;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.inject.Inject;
import javax.swing.JOptionPane;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

/**
 * We host our server source at https://github.com/open-osrs/apollo
 * Pull Requests are welcome to both this client and Apollo, with Apollo PRs upstream if applicable
 * <p>
 * Runs in the background while plugin is disabled (only runs if enabled first)
 */

@Extension
@PluginDescriptor(
	name = "RS Classic 377",
	description = "RS Classic Rev 377",
	type = PluginType.MISCELLANEOUS,
	enabledByDefault = false
)
@Slf4j
public class RSClassic377Plugin extends Plugin
{
	@Inject
	private RSClassic377Config config;

	@Provides
	RSClassic377Config provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RSClassic377Config.class);
	}

	@Override
	protected void startUp()
	{
		Configuration.CODEBASE = config.codebase();
		Configuration.USERNAME = config.username();
		Configuration.PASSWORD = config.password();

		if (!config.confirm())
		{
			JOptionPane.showMessageDialog(null, "Passwords are randomly generated per machine once (or on reset), Don't edit password field unless you know what you are doing.");
			JOptionPane.showMessageDialog(null, "Your config acts like a token, and can be transferred. And should be protected if it contains this config. If you reset or lose the config without a backup, your account will be lost forever!");
			JOptionPane.showMessageDialog(null, "Please use a different username, and a different password if you override the generator!");
			JOptionPane.showMessageDialog(null, "There will NEVER be account recovery! So don't come crying to us when you didnt backup your config.");
			JOptionPane.showMessageDialog(null, "To use this plugin, enable the confirm config option. Set a NEW username while you are at it, and restart this plugin.");
			return;
		}
		if (config.username().equals(""))
		{
			JOptionPane.showMessageDialog(null, "You must set a username in the configuration before starting.");
			return;
		}

		if (GameShell.gameFrame != null)
		{
			GameShell.gameFrame.setVisible(true);
			GameShell.gameFrame.requestFocus();
		}
		else
		{
			log.info("Starting Classic 377");
			Game game = new Game();
			game.world = 1;
			game.portOffset = 0;
			game.setHighMemory();
			game.memberServer = true;
			SignLink.storeId = 32;
			try
			{
				SignLink.initialize(InetAddress.getLocalHost());
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
			game.initializeApplication(765, 503);
			log.info("Classic 377 started");
		}
	}

	@Override
	protected void shutDown()
	{
		if (GameShell.gameFrame != null)
		{
			GameShell.gameFrame.setVisible(false);
		}
		log.info("Classic 377 stopped");
	}
}
