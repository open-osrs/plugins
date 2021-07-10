package net.runelite.client.plugins.openosrs;

import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;
import javax.inject.Inject;
import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Scanner;

@Extension
@PluginDescriptor(
		enabledByDefault = true,
		name = "OpenOSRS",
		description = "Special OSRS settings"
)
public class openosrsPlugin extends Plugin
{
	@Inject
	private openosrsConfig config;

	@Provides
	openosrsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(openosrsConfig.class);
	}

	public static final File RUNELITE_DIR = new File(System.getProperty("user.home"), ".runelite");

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("openosrs"))
		{
			switch (event.getKey())
			{
				case "bootstrapMode":
				case "askMode":
					if (!RUNELITE_DIR.exists())
					{
						RUNELITE_DIR.mkdir();
					}

					try
					{
						File properties = new File(RUNELITE_DIR, "runeliteplus.properties");
						if (!properties.exists())
						{
							properties.createNewFile();
						}
						Scanner myReader = new Scanner(properties);
						String lines = "";
						while (myReader.hasNextLine())
						{
							String str = myReader.nextLine();
							if (str.startsWith("openosrs.askMode") || str.startsWith("openosrs.bootstrapMode"))
							{
								continue;
							}
							lines += str + "\r\n";
						}
						String askString = config.askMode() ? "true" : "false";
						lines += "openosrs.askMode=" + askString + "\r\n";
						lines += "openosrs.bootstrapMode=" + config.bootStrapMode().getName();

						myReader.close();
						overrideSettings(lines);
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					catch (IOException exception)
					{
						exception.printStackTrace();
					}
					break;
			}
		}
	}

	private void overrideSettings(String newSettings)
	{
		File properties = new File(RUNELITE_DIR, "runeliteplus.properties");
		properties.delete();
		File properties2 = new File(RUNELITE_DIR, "runeliteplus.properties");
		try
		{
			FileWriter f2 = new FileWriter(properties2, Charset.defaultCharset(), false);
			f2.write(newSettings);
			f2.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void startUp()
	{

	}

	@Override
	protected void shutDown()
	{
	}

}