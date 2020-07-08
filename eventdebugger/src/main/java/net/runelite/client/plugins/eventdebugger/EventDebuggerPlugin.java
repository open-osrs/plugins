package net.runelite.client.plugins.eventdebugger;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(
	name = "Event Debugger",
	description = "",
	tags = {"combat", "notifications", "health", "food", "eat"},
	enabledByDefault = false,
	type = PluginType.UTILITY
)
@Slf4j
public class EventDebuggerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private Notifier notifier;

	@Inject
	private EventDebuggerConfig config;

	@Provides
	EventDebuggerConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(EventDebuggerConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Subscribe
	public void onGameTick(final GameTick event)
	{

	}

	@Subscribe
	public void onMenuEntryAdded(final MenuEntryAdded event)
	{

		if (!config.menuEntryAdded())
		{
			return;
		}

		if (config.optionCheckbox() && !event.getOption().contains(config.optionFilter()))
		{
			return;
		}

		if (config.targetCheckbox() && !event.getTarget().contains(config.targetFilter()))
		{
			return;
		}

		if (config.identifierCheckbox() && event.getIdentifier() != config.identifierFilter())
		{
			return;
		}

		if (config.opcodeCheckbox() && event.getOpcode() != config.opcodeFilter())
		{
			return;
		}

		if (config.param0Checkbox() && event.getParam0() != config.param0Filter())
		{
			return;
		}

		if (config.param1Checkbox() && event.getParam1() != config.param1Filter())
		{
			return;
		}

		log.info("MenuEntryAdded:");
		log.info("\tOption:\t" + event.getOption());
		log.info("\tTarget:\t" + event.getTarget());
		log.info("\tIdentifier:\t" + event.getIdentifier());
		log.info("\tOpcode:\t" + event.getOpcode());
		log.info("\tParam0:\t" + event.getParam0());
		log.info("\tParam1:\t" + event.getParam1());
		log.info("\tForceLeftClick:\t" + event.isForceLeftClick());
		log.info("\tModified:\t" + event.isModified());

	}
}
