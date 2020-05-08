package net.runelite.client.plugins.jadautoprayer;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Extension
@PluginDescriptor(
	name = "Jad Auto Prayer",
	description = "Auto click proper prayers against Jad.",
	tags = {"bosses", "combat", "minigame", "overlay", "prayer", "pve", "pvm", "jad", "firecape", "fight", "cave", "caves"},
	enabledByDefault = false,
	type = PluginType.MINIGAME
)
public class JadAutoPrayerPlugin extends Plugin
{
	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 25, TimeUnit.SECONDS, queue,
		new ThreadPoolExecutor.DiscardPolicy());

	@Inject
	private Client client;

	@Inject
	ConfigManager configManager;

	@Inject
	private JadAutoPrayerConfig config;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	public int protectMageVarbit;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	public int protectRangeVarbit;

	private NPC jad;

	//this is our current custom entry to swap in
	public MenuEntry entry;

	@Provides
	JadAutoPrayerConfig getConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(JadAutoPrayerConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
		this.jad = null;
	}

	@Subscribe
	public void onVarbitChanged(final VarbitChanged event)
	{
		if (this.client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (this.client.getVar(Prayer.PROTECT_FROM_MAGIC.getVarbit()) == 1)
		{
			this.setProtectMageVarbit(1);
		}
		else
		{
			this.setProtectMageVarbit(0);
		}

		if (this.client.getVar(Prayer.PROTECT_FROM_MISSILES.getVarbit()) == 1)
		{
			this.setProtectRangeVarbit(1);
		}
		else
		{
			this.setProtectRangeVarbit(0);
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		int id = event.getNpc().getId();

		if (id == NpcID.TZTOKJAD || id == NpcID.TZTOKJAD_6506)
		{
			this.jad = event.getNpc();
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (this.jad == event.getNpc())
		{
			this.jad = null;
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (event.getActor() != this.jad)
		{
			return;
		}

		executor.submit(() -> {
			final boolean PROTECT_RANGED = this.client.getVar(Prayer.PROTECT_FROM_MISSILES.getVarbit()) != 0;
			final boolean PROTECT_MAGIC = this.client.getVar(Prayer.PROTECT_FROM_MAGIC.getVarbit()) != 0;

			if (this.jad.getAnimation() == JadAttack.MAGIC.getAnimation() && !PROTECT_MAGIC)
			{
				activatePrayer(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);
			}
			else if (this.jad.getAnimation() == JadAttack.RANGE.getAnimation() && !PROTECT_RANGED)
			{
				activatePrayer(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES);
			}
		});
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

		try
		{
			Thread.sleep(50);
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