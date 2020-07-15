package net.runelite.client.plugins.lavacrafter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import com.google.inject.Provides;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.PlayerAppearance;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.queries.BankItemQuery;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@SuppressWarnings("DuplicateBranchesInSwitch")
@Extension
@PluginDescriptor(
	name = "Lava Crafter",
	description = "Lava Crafting Helper",
	tags = {"rc", "rune", "crafting", "runecrafting", "lava", "craft", "crafter", "skilling", "helper", "automation", "ben"},
	enabledByDefault = false,
	type = PluginType.SKILLING
)
public class LavaCrafterPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private LavaCrafterConfig config;

	@Inject
	private ItemManager itemManager;

	private MenuEntry entry;

	private boolean pluginStarted = false;
	private int tickDelay = 0;

	LavaCrafterState state = LavaCrafterState.USE_BANK_CHEST;

	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 25, TimeUnit.SECONDS, queue,
		new ThreadPoolExecutor.DiscardPolicy());

	@Provides
	LavaCrafterConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(LavaCrafterConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		state = LavaCrafterState.USE_BANK_CHEST;
	}

	@Override
	protected void shutDown() throws Exception
	{
		state = LavaCrafterState.USE_BANK_CHEST;
		executor.shutdownNow();
	}

	@Subscribe
	public void onConfigButtonClicked(ConfigButtonClicked event)
	{
		if (!event.getGroup().equals("lavacrafter"))
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

	public GameObject findNearestGameObjectWithin(WorldPoint worldPoint, int dist, int id)
	{
		if (client.getLocalPlayer() == null)
		{
			return null;
		}

		return new GameObjectQuery()
			.idEquals(id)
			.isWithinDistance(worldPoint, dist)
			.result(client)
			.nearestTo(client.getLocalPlayer());
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (tickDelay > 0)
		{
			tickDelay--;
			return;
		}

		if (!pluginStarted)
			return;

		System.out.println("Current state is:\t" + state.name());

		Player localPlayer = client.getLocalPlayer();

		if (localPlayer == null)
			return;

		entry = getEntry(localPlayer);

		if (entry != null)
		{
			click();
			tickDelay = state.tickDelay;
		}

		iterateState();
	}

	private void iterateState()
	{
		switch (state)
		{
			case USE_BANK_CHEST:
				state = LavaCrafterState.WITHDRAW_DUELING_RING;
				break;
			case WITHDRAW_DUELING_RING:
				state = LavaCrafterState.WEAR_DUELING_RING;
				break;
			case WEAR_DUELING_RING:
				state = LavaCrafterState.WITHDRAW_BINDING_NECKLACE;
				break;
			case WITHDRAW_BINDING_NECKLACE:
				state = LavaCrafterState.WEAR_BINDING_NECKLACE;
				break;
			case WEAR_BINDING_NECKLACE:
				state = LavaCrafterState.DEPOSIT_LAVAS;
				break;
			case DEPOSIT_LAVAS:
				state = LavaCrafterState.WITHDRAW_TALISMAN;
				break;
			case WITHDRAW_TALISMAN:
				state = LavaCrafterState.WITHDRAW_ESSENCE;
				break;
			case WITHDRAW_ESSENCE:
				state = LavaCrafterState.TELE_DUEL_ARENA;
				break;
			case TELE_DUEL_ARENA:
				state = LavaCrafterState.ENTER_RUINS;
				break;
			case ENTER_RUINS:
				state = LavaCrafterState.USE_EARTH_RUNES;
				break;
			case USE_EARTH_RUNES:
				state = LavaCrafterState.USE_EARTHS_ON_ALTAR;
				break;
			case USE_EARTHS_ON_ALTAR:
				state = LavaCrafterState.TELE_CASTLE_WARS;
				break;
			case TELE_CASTLE_WARS:
				state = LavaCrafterState.USE_BANK_CHEST;
				break;
			default:
				state = LavaCrafterState.USE_BANK_CHEST;
				break;
		}
	}

	public MenuEntry getEntry(Player localPlayer)
	{
		switch (state.type)
		{
			case GAME_OBJECT:
				GameObject object = findNearestGameObjectWithin(localPlayer.getWorldLocation(), 100, state.identifier);

				if (object == null)
					return null;

				return new MenuEntry(state.option, state.target, state.identifier, state.opcode, object.getSceneMinLocation().getX(), object.getSceneMinLocation().getY(), false);

			case BANK_ITEM:
				WidgetItem bankItem = null;

				switch (state)
				{
					case WITHDRAW_DUELING_RING:
						if (checkHasDuelingRing(localPlayer))
						{
							//manually iterate state since it will be skipped in the main thread when we return null...
							iterateState();
							return null;
						}
						bankItem = getBankItem(ItemID.RING_OF_DUELING8, ItemID.RING_OF_DUELING7, ItemID.RING_OF_DUELING6, ItemID.RING_OF_DUELING5, ItemID.RING_OF_DUELING4, ItemID.RING_OF_DUELING3, ItemID.RING_OF_DUELING2, ItemID.RING_OF_DUELING1);
						break;
					case WITHDRAW_BINDING_NECKLACE:
						if (checkHasBindingNecklace(localPlayer))
						{
							//manually iterate state since it will be skipped in the main thread when we return null...
							iterateState();
							return null;
						}
						bankItem = getBankItem(ItemID.BINDING_NECKLACE);
						break;
					case WITHDRAW_TALISMAN:
						bankItem = getBankItem(ItemID.EARTH_TALISMAN);
						break;
					case WITHDRAW_ESSENCE:
						bankItem = getBankItem(ItemID.PURE_ESSENCE);
						break;
				}

				if (bankItem == null)
					return null;

				return new MenuEntry(state.option, state.target, state.identifier, state.opcode, bankItem.getWidget().getIndex(), state.param1, false);

			case INVENTORY_ITEM:

				WidgetItem inventoryItem = null;

				switch (state)
				{
					case DEPOSIT_LAVAS:
						inventoryItem = getInventoryItem(ItemID.LAVA_RUNE);

						if (inventoryItem == null)
						{
							this.state = LavaCrafterState.WITHDRAW_TALISMAN;
						}

						break;
					case USE_EARTH_RUNES:
						inventoryItem = getInventoryItem(ItemID.EARTH_RUNE);
						break;
					case WEAR_BINDING_NECKLACE:
						if (checkHasBindingNecklace(localPlayer))
						{
							//manually iterate state since it will be skipped in the main thread when we return null...
							iterateState();
							return null;
						}

						inventoryItem = getInventoryItem(ItemID.BINDING_NECKLACE);

						if (inventoryItem == null)
						{
							this.state = LavaCrafterState.WITHDRAW_BINDING_NECKLACE;
						}

						break;
					case WEAR_DUELING_RING:
						if (checkHasDuelingRing(localPlayer))
						{
							//manually iterate state since it will be skipped in the main thread when we return null...
							iterateState();
							return null;
						}

						inventoryItem = getInventoryItem(ItemID.RING_OF_DUELING1, ItemID.RING_OF_DUELING2, ItemID.RING_OF_DUELING3, ItemID.RING_OF_DUELING4, ItemID.RING_OF_DUELING5, ItemID.RING_OF_DUELING6, ItemID.RING_OF_DUELING7,ItemID.RING_OF_DUELING8);

						if (inventoryItem == null)
						{
							this.state = LavaCrafterState.WITHDRAW_DUELING_RING;
						}

						break;
				}

				if (inventoryItem == null)
					return null;

				return new MenuEntry(state.option, state.target, state.identifier, state.opcode, inventoryItem.getIndex(), state.param1, false);

			default:
				return new MenuEntry(state.option, state.target, state.identifier, state.opcode, state.param0, state.param1, false);
		}
	}

	public boolean checkHasBindingNecklace(Player localPlayer)
	{
		PlayerAppearance playerAppearance = localPlayer.getPlayerAppearance();

		if (playerAppearance == null)
		{
			return false;
		}

		Item[] equipmentItems = client.getItemContainer(InventoryID.EQUIPMENT).getItems();

		for (Item equipmentItem : equipmentItems)
		{
			String name = itemManager.getItemDefinition(equipmentItem.getId()).getName();
			if (name.contains("Binding necklace"))
				return true;
		}

		return false;
	}

	public boolean checkHasDuelingRing(Player localPlayer)
	{
		PlayerAppearance playerAppearance = localPlayer.getPlayerAppearance();

		if (playerAppearance == null)
		{
			return false;
		}

		Item[] equipmentItems = client.getItemContainer(InventoryID.EQUIPMENT).getItems();

		for (Item equipmentItem : equipmentItems)
		{
			String name = itemManager.getItemDefinition(equipmentItem.getId()).getName();
			if (name.contains("Ring of dueling("))
				return true;
		}

		return false;
	}

	public WidgetItem getBankItem(int... ids)
	{
		return new BankItemQuery().idEquals(ids).result(client).first();
	}

	public WidgetItem getInventoryItem(int... ids)
	{
		return new InventoryWidgetItemQuery().idEquals(ids).result(client).first();
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