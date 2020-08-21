package net.runelite.client.plugins.specialattackuser;

import com.google.inject.Provides;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.Point;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Special Attack User",
	description = "Automatically enables special attack",
	tags = {"combat", "special", "attack", "spec"},
	enabledByDefault = false,
	type = PluginType.PVM
)
public class SpecialAttackUserPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SpecialAttackUserConfig config;

	private MenuEntry entry;

	@Provides
	SpecialAttackUserConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(SpecialAttackUserConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{

	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	public boolean isBankOpen()
	{
		Widget widget = client.getWidget(WidgetInfo.BANK_CONTAINER);

		if (widget != null && !widget.isHidden())
		{
			return true;
		}

		return false;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		boolean spec_enabled = (client.getVar(VarPlayer.SPECIAL_ATTACK_ENABLED) == 1);

		if (spec_enabled)
		{
			return;
		}

		//value returns 1000 for 100% spec, 500 for 50%, etc
		int spec_percent = client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT);

		if (spec_percent < config.specialPercent() * 10)
		{
			return;
		}

		if (isBankOpen())
		{
			return;
		}

		Widget specialOrb = client.getWidget(160, 30);

		if (specialOrb == null || specialOrb.isHidden())
		{
			return;
		}

		try
		{
			entry = new MenuEntry("Use <col=00ff00>Special Attack</col>", "", 1, MenuOpcode.CC_OP.getId(), -1, 38862884, false);
			click();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (entry != null)
		{
			event.setMenuEntry(entry);
		}

		entry = null;
	}

	public void click()
	{
		Point pos = client.getMouseCanvasPosition();

		if (client.isStretchedEnabled())
		{
			final Dimension stretched = client.getStretchedDimensions();
			final Dimension real = client.getRealDimensions();
			final double width = (stretched.width / real.getWidth());
			final double height = (stretched.height / real.getHeight());
			final Point point = new Point((int) (pos.getX() * width), (int) (pos.getY() * height));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1));
			return;
		}

		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
		client.getCanvas().dispatchEvent(new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, pos.getX(), pos.getY(), 1, false, 1));
	}
}
