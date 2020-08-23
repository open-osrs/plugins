package net.runelite.client.plugins.cannonreloader;

import com.google.inject.Provides;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import net.runelite.api.AnimationID;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import static net.runelite.api.ObjectID.BROKEN_MULTICANNON_14916;
import static net.runelite.api.ObjectID.CANNON_BASE;
import static net.runelite.api.ObjectID.DWARF_MULTICANNON;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Projectile;
import static net.runelite.api.ProjectileID.CANNONBALL;
import static net.runelite.api.ProjectileID.GRANITE_CANNONBALL;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Cannon Reloader",
	description = "Automatically reload your cannon",
	tags = {"combat", "notifications", "ranged"},
	enabledByDefault = false,
	type = PluginType.PVM
)
public class CannonReloaderPlugin extends Plugin
{
	private static final Pattern NUMBER_PATTERN = Pattern.compile("([0-9]+)");
	private static final int MAX_CBALLS = 30;
	private static final int MAX_DISTANCE = 2500;
	private int nextReloadCount = 10;
	private boolean skipProjectileCheckThisTick;

	@Inject
	private CannonReloaderConfig config;

	@Provides
	CannonReloaderConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(CannonReloaderConfig.class);
	}

	private int cballsLeft;

	private Random r = new Random();

	private boolean cannonPlaced;

	private WorldPoint cannonPosition;

	private GameObject cannon;

	private MenuEntry entry;
	private int tickDelay;

	@Inject
	private Client client;

	@Override
	protected void startUp() throws Exception
	{
		nextReloadCount = r.nextInt(config.maxReloadAmount() - config.minReloadAmount()) + config.minReloadAmount();
	}

	@Override
	protected void shutDown() throws Exception
	{
		cannonPlaced = false;
		cannonPosition = null;
		cballsLeft = 0;
		skipProjectileCheckThisTick = false;
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject gameObject = event.getGameObject();

		Player localPlayer = client.getLocalPlayer();

		if (gameObject.getId() == CANNON_BASE && !cannonPlaced)
		{
			if (localPlayer != null && localPlayer.getWorldLocation().distanceTo(gameObject.getWorldLocation()) <= 2
				&& localPlayer.getAnimation() == AnimationID.BURYING_BONES)
			{
				cannonPosition = gameObject.getWorldLocation();
				cannon = gameObject;
			}
		}

		//Object ID = 14916
		if (gameObject.getId() == BROKEN_MULTICANNON_14916 && cannonPlaced)
		{
			if (cannonPosition.equals(gameObject.getWorldLocation()))
			{
				entry = new MenuEntry("Repair", "<col=ffff>Broken multicannon", gameObject.getId(), MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId(), cannon.getSceneMinLocation().getX(), cannon.getSceneMinLocation().getY(), false);
				click();
				tickDelay = 3;
			}
		}
	}

	@Subscribe
	private void onProjectileMoved(ProjectileMoved event)
	{
		Projectile projectile = event.getProjectile();

		if ((projectile.getId() == CANNONBALL || projectile.getId() == GRANITE_CANNONBALL) && cannonPosition != null)
		{
			WorldPoint projectileLoc = WorldPoint.fromLocal(client, projectile.getX1(), projectile.getY1(), client.getPlane());

			//Check to see if projectile x,y is 0 else it will continuously decrease while ball is flying.
			if (projectileLoc.equals(cannonPosition) && projectile.getX() == 0 && projectile.getY() == 0)
			{
				// When there's a chat message about cannon reloaded/unloaded/out of ammo,
				// the message event runs before the projectile event. However they run
				// in the opposite order on the server. So if both fires in the same tick,
				// we don't want to update the cannonball counter if it was set to a specific
				// amount.
				if (!skipProjectileCheckThisTick)
				{
					cballsLeft--;
				}
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.SPAM && event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		if (event.getMessage().equals("You add the furnace."))
		{
			cannonPlaced = true;
			cballsLeft = 0;
		}

		if (event.getMessage().contains("You pick up the cannon"))
		{
			cannonPlaced = false;
			cballsLeft = 0;
		}

		if (event.getMessage().startsWith("You load the cannon with"))
		{
			nextReloadCount = r.nextInt(config.maxReloadAmount() - config.minReloadAmount()) + config.minReloadAmount();

			Matcher m = NUMBER_PATTERN.matcher(event.getMessage());
			if (m.find())
			{
				// The cannon will usually refill to MAX_CBALLS, but if the
				// player didn't have enough cannonballs in their inventory,
				// it could fill up less than that. Filling the cannon to
				// cballsLeft + amt is not always accurate though because our
				// counter doesn't decrease if the player has been too far away
				// from the cannon due to the projectiels not being in memory,
				// so our counter can be higher than it is supposed to be.
				int amt = Integer.parseInt(m.group());
				if (cballsLeft + amt >= MAX_CBALLS)
				{
					skipProjectileCheckThisTick = true;
					cballsLeft = MAX_CBALLS;
				}
				else
				{
					cballsLeft += amt;
				}
			}
			else if (event.getMessage().equals("You load the cannon with one cannonball."))
			{
				if (cballsLeft + 1 >= MAX_CBALLS)
				{
					skipProjectileCheckThisTick = true;
					cballsLeft = MAX_CBALLS;
				}
				else
				{
					cballsLeft++;
				}
			}
		}

		if (event.getMessage().contains("Your cannon is out of ammo!"))
		{
			skipProjectileCheckThisTick = true;

			// If the player was out of range of the cannon, some cannonballs
			// may have been used without the client knowing, so having this
			// extra check is a good idea.
			cballsLeft = 0;
		}

		if (event.getMessage().startsWith("You unload your cannon and receive Cannonball")
			|| event.getMessage().startsWith("You unload your cannon and receive Granite cannonball"))
		{
			skipProjectileCheckThisTick = true;

			cballsLeft = 0;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		skipProjectileCheckThisTick = false;

		if (tickDelay > 0)
		{
			tickDelay--;
			return;
		}

		if (!cannonPlaced || cannonPosition == null || cballsLeft > nextReloadCount)
		{
			return;
		}

		entry = new MenuEntry("Fire", "<col=ffff>Dwarf multicannon", DWARF_MULTICANNON, MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId(), cannon.getSceneMinLocation().getX(), cannon.getSceneMinLocation().getY(), false);
		click();
		tickDelay = 3;

		nextReloadCount = r.nextInt(config.maxReloadAmount() - config.minReloadAmount()) + config.minReloadAmount();
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