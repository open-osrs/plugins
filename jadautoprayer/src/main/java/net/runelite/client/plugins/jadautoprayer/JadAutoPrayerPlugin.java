package net.runelite.client.plugins.jadautoprayer;

import java.lang.reflect.Field;
import java.util.Set;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;

@Extension
@PluginDescriptor(
	name = "Jad Auto Prayer",
	description = "Auto click proper prayers against Jad(s).",
	tags = {"bosses", "combat", "minigame", "overlay", "prayer", "pve", "pvm", "jad", "firecape", "fight", "cave", "caves"},
	enabledByDefault = false,
	type = PluginType.MINIGAME
)
public class JadAutoPrayerPlugin extends Plugin
{
	@Inject
	private Client client;

	public MenuEntry entry;

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		Actor actor = event.getActor();

		if (actor == null)
		{
			return;
		}

		switch (actor.getAnimation())
		{
			case AnimationID.TZTOK_JAD_MAGIC_ATTACK:
			case AnimationID.JALTOK_JAD_MAGE_ATTACK:
				if (client.getVar(Prayer.PROTECT_FROM_MAGIC.getVarbit()) == 0)
				{
					activatePrayer(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);
				}
				break;
			case AnimationID.TZTOK_JAD_RANGE_ATTACK:
			case AnimationID.JALTOK_JAD_RANGE_ATTACK:
				if (client.getVar(Prayer.PROTECT_FROM_MISSILES.getVarbit()) == 0)
				{
					activatePrayer(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES);
				}
				break;
			default:
				break;
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

	public void activatePrayer(WidgetInfo widgetInfo)
	{
		Widget prayer_widget = client.getWidget(widgetInfo);

		if (prayer_widget == null)
		{
			return;
		}

		if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
		{
			return;
		}

		entry = new MenuEntry("Activate", prayer_widget.getName(), 1, MenuOpcode.CC_OP.getId(), prayer_widget.getItemId(), prayer_widget.getId(), false);
		click();
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