package net.runelite.client.plugins.ardyknighttheiver;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.inject.Inject;
import com.google.inject.Provides;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import static net.runelite.client.plugins.ardyknighttheiver.HealthCheckStyle.EXACT_HEALTH;
import static net.runelite.client.plugins.ardyknighttheiver.HealthCheckStyle.PERCENTAGE;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Ardy Knight Theiver",
	description = "Automatically theives ardy knights",
	tags = {"knight", "theiver", "theiving", "ardy", "ardougne", "skill", "skilling"},
	enabledByDefault = false,
	type = PluginType.SKILLING
)
public class ArdyKnightTheiverPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ArdyKnightTheiverConfig config;

	@Inject
	private ItemManager itemManager;

	private MenuEntry entry;
	private int entryTimeout;

	private Random r = new Random();

	private boolean emptyPouches = false;

	private boolean pluginStarted = false;
	private int tickDelay = 0;
	private int frameDelay = 0;

	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 25, TimeUnit.SECONDS, queue,
		new ThreadPoolExecutor.DiscardPolicy());

	@Provides
	ArdyKnightTheiverConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(ArdyKnightTheiverConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
		executor.shutdownNow();
	}

	@Subscribe
	public void onConfigButtonClicked(ConfigButtonClicked event)
	{
		if (!event.getGroup().equals("ardyknighttheiver"))
		{
			return;
		}

		if (event.getKey().equals("startButton"))
		{
			pluginStarted = true;
		}
		else if (event.getKey().equals("stopButton"))
		{
			pluginStarted = false;
		}
	}

	@Subscribe
	private void onChatMessage(ChatMessage event)
	{
		final String message = event.getMessage();

		if (event.getType() == ChatMessageType.SPAM)
		{
			if (message.startsWith("You pickpocket") || message.startsWith("You pick-pocket") || message.startsWith("You steal") || message.startsWith("You successfully pick-pocket") || message.startsWith("You successfully pick") || message.startsWith("You successfully steal") || message.startsWith("You pick the knight") || message.startsWith("You pick the Elf"))
			{
				tickDelay = 0;
			}
			else if (message.startsWith("You fail to pick") || message.startsWith("You fail to steal"))
			{
				tickDelay = getRandom(config.clickDelayMin(), config.clickDelayMax());
			}
			else if (message.startsWith("You open all of the pouches"))
			{
				emptyPouches = false;
			}
		}
		else if (event.getType() == ChatMessageType.GAMEMESSAGE)
		{
			if (message.startsWith("You need to empty your"))
			{
				emptyPouches = true;
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (tickDelay > 0) {
			tickDelay--;
			return;
		}

		if (!pluginStarted)
			return;

		if (shouldEat())
		{
			eat();
			return;
		}

		if (emptyPouches)
		{
			openPouches();
			return;
		}

		NPC knight = new NPCQuery()
			.idEquals(NpcID.KNIGHT_OF_ARDOUGNE)
			.result(client)
			.nearestTo(client.getLocalPlayer());

		if (knight == null)
		{
			return;
		}

		//String option, String target, int identifier, int opcode, int param0, int param1, boolean forceLeftClick
		entry = new MenuEntry("Pickpocket", "<col=ffff00>" + knight.getName() + "<col=ff00>  (level-" + knight.getCombatLevel() + ")", knight.getIndex(), MenuOpcode.NPC_THIRD_OPTION.getId(), 0, 0, false);
		click();
		tickDelay = 1;
	}

	public void openPouches()
	{
		List<WidgetItem> list = new InventoryWidgetItemQuery()
			.idEquals(ItemID.COIN_POUCH_22531)
			.result(client)
			.list;

		if (list == null || list.isEmpty())
		{
			return;
		}

		WidgetItem item = list.get(0);

		if (item == null)
		{
			return;
		}

		//String option, String target, int identifier, int opcode, int param0, int param1, boolean forceLeftClick
		entry = new MenuEntry("Open-all", "<col=ff9040>" + itemManager.getItemDefinition(item.getId()).getName(), item.getId(), MenuOpcode.ITEM_FIRST_OPTION.getId(), item.getIndex(), 9764864, false);
		click();
		tickDelay = 1;
	}

	public void eat()
	{
		List<WidgetItem> list = new InventoryWidgetItemQuery()
			.idEquals(config.itemId())
			.result(client)
			.list;

		if (list == null || list.isEmpty())
		{
			return;
		}

		WidgetItem item = list.get(0);

		if (item == null)
		{
			return;
		}

		entry = new MenuEntry("Eat", "<col=ff9040>" + this.itemManager.getItemDefinition(item.getId()).getName(), item.getId(), MenuOpcode.ITEM_FIRST_OPTION.getId(), item.getIndex(), 9764864, false);
		click();
		tickDelay = 4;
	}

	public boolean shouldEat()
	{
		switch (config.hpCheckStyle())
		{
			case EXACT_HEALTH:
				return client.getBoostedSkillLevel(Skill.HITPOINTS) <= config.hpToEat();

			case PERCENTAGE:
				return (((float)client.getBoostedSkillLevel(Skill.HITPOINTS) / (float)client.getRealSkillLevel(Skill.HITPOINTS)) * 100.f) <= (float)config.hpToEat();
		}

		return false;
	}

	public int getRandom(int min, int max)
	{
		return r.nextInt((max - min) + 1) + min;
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (entry != null)
		{
			event.setMenuEntry(entry);
		}

		entry = null;
		entryTimeout = 0;
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