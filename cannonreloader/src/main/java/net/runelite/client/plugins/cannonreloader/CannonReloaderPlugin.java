package net.runelite.client.plugins.cannonreloader;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.AnimationID;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.queries.TileObjectQuery;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.runelite.api.ObjectID.CANNON_BASE;
import static net.runelite.api.ProjectileID.CANNONBALL;
import static net.runelite.api.ProjectileID.GRANITE_CANNONBALL;

@Extension
@PluginDescriptor(
		name = "Cannon Reloader",
		description = "Automatically reload your cannon",
		tags = { "combat", "notifications", "ranged" },
		enabledByDefault = false,
		type = PluginType.PVM
)
public class CannonReloaderPlugin extends Plugin {
	private static final Pattern NUMBER_PATTERN = Pattern.compile("([0-9]+)");
	private static final int MAX_CBALLS = 30;
	private static final int MAX_DISTANCE = 2500;
	
	private int nextReloadCount = (int) (Math.random() % 10 + 10);
	private boolean skipProjectileCheckThisTick;

	@Inject
	private CannonReloaderConfig config;

	@Provides
	CannonReloaderConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(CannonReloaderConfig.class);
	}
	
	@Getter
	private int cballsLeft;

	@Inject
	private MenuManager menuManager;
	
	@Getter
	private boolean cannonPlaced;
	
	@Getter
	private WorldPoint cannonPosition;
	
	@Getter
	private GameObject cannon;
	
	@Inject
	private Client client;

	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 25, TimeUnit.SECONDS, queue,
			new ThreadPoolExecutor.DiscardPolicy());
	
	@Override
	protected void startUp() throws Exception {
	}
	
	@Override
	protected void shutDown() throws Exception {
		cannonPlaced = false;
		cannonPosition = null;
		cballsLeft = 0;
		skipProjectileCheckThisTick = false;
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event) {
		GameObject gameObject = event.getGameObject();
		
		Player localPlayer = client.getLocalPlayer();
		if (gameObject.getId() == CANNON_BASE && !cannonPlaced) {
			if (localPlayer != null && localPlayer.getWorldLocation().distanceTo(gameObject.getWorldLocation()) <= 2
					&& localPlayer.getAnimation() == AnimationID.BURYING_BONES) {
				cannonPosition = gameObject.getWorldLocation();
				cannon = gameObject;
			}
		}
	}

	@Subscribe
	private void onProjectileMoved(ProjectileMoved event) {
		Projectile projectile = event.getProjectile();
		
		if ((projectile.getId() == CANNONBALL || projectile.getId() == GRANITE_CANNONBALL) && cannonPosition != null) {
			WorldPoint projectileLoc = WorldPoint.fromLocal(client, projectile.getX1(), projectile.getY1(), client.getPlane());
			
			//Check to see if projectile x,y is 0 else it will continuously decrease while ball is flying.
			if (projectileLoc.equals(cannonPosition) && projectile.getX() == 0 && projectile.getY() == 0) {
				// When there's a chat message about cannon reloaded/unloaded/out of ammo,
				// the message event runs before the projectile event. However they run
				// in the opposite order on the server. So if both fires in the same tick,
				// we don't want to update the cannonball counter if it was set to a specific
				// amount.
				if (!skipProjectileCheckThisTick) {
					cballsLeft--;
				}
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		if (event.getType() != ChatMessageType.SPAM && event.getType() != ChatMessageType.GAMEMESSAGE) {
			return;
		}
		
		if (event.getMessage().equals("You add the furnace.")) {
			cannonPlaced = true;
			cballsLeft = 0;
		}
		
		if (event.getMessage().contains("You pick up the cannon")) {
			cannonPlaced = false;
			cballsLeft = 0;
		}
		
		if (event.getMessage().startsWith("You load the cannon with")) {
			nextReloadCount = (int) (Math.random() % 10 + 10);
			
			Matcher m = NUMBER_PATTERN.matcher(event.getMessage());
			if (m.find()) {
				// The cannon will usually refill to MAX_CBALLS, but if the
				// player didn't have enough cannonballs in their inventory,
				// it could fill up less than that. Filling the cannon to
				// cballsLeft + amt is not always accurate though because our
				// counter doesn't decrease if the player has been too far away
				// from the cannon due to the projectiels not being in memory,
				// so our counter can be higher than it is supposed to be.
				int amt = Integer.parseInt(m.group());
				if (cballsLeft + amt >= MAX_CBALLS) {
					skipProjectileCheckThisTick = true;
					cballsLeft = MAX_CBALLS;
				} else {
					cballsLeft += amt;
				}
			} else if (event.getMessage().equals("You load the cannon with one cannonball.")) {
				if (cballsLeft + 1 >= MAX_CBALLS) {
					skipProjectileCheckThisTick = true;
					cballsLeft = MAX_CBALLS;
				} else {
					cballsLeft++;
				}
			}
		}
		
		if (event.getMessage().contains("Your cannon is out of ammo!")) {
			skipProjectileCheckThisTick = true;
			
			// If the player was out of range of the cannon, some cannonballs
			// may have been used without the client knowing, so having this
			// extra check is a good idea.
			cballsLeft = 0;
		}
		
		if (event.getMessage().startsWith("You unload your cannon and receive Cannonball")
				|| event.getMessage().startsWith("You unload your cannon and receive Granite cannonball")) {
			skipProjectileCheckThisTick = true;
			
			cballsLeft = 0;
		}

		if (event.getMessage().equalsIgnoreCase("Your cannon has broken!")) {
			this.executor.submit(() ->
			{
				if (!cannonPlaced || cannonPosition == null)
					return;

				Point p = InputHandler.getClickPoint(cannon.getClickbox().getBounds());

				if (p == null)
					return;

				if (client.getTickCount() % 5 != 1)
					return;

				InputHandler.leftClick(client, p);
			});
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		skipProjectileCheckThisTick = false;
		
		this.executor.submit(() ->
		{
			try {
				if (!cannonPlaced || cannonPosition == null || cballsLeft > nextReloadCount)
					return;

				LocalPoint cannonPoint = LocalPoint.fromWorld(client, cannonPosition);

				if (cannonPoint == null)
					return;

				Player localPlayer = client.getLocalPlayer();

				if (localPlayer == null)
					return;

				LocalPoint localLocation = localPlayer.getLocalLocation();

				if (localLocation.distanceTo(cannonPoint) > MAX_DISTANCE)
					return;

				Point p = Perspective.localToCanvas(client, cannonPoint, cannon.getPlane(), 45);

				//Point p = InputHandler.getClickPoint(cannon.getClickbox().getBounds());
				
				if (p == null)
					return;
				
				if (client.getTickCount() % 5 != 1)
					return;

				/*
				2020-04-05 19:51:54 [Client] INFO  n.r.c.p.e.EventDebuggerPlugin - MenuEntryAdded:
				2020-04-05 19:51:54 [Client] INFO  n.r.c.p.e.EventDebuggerPlugin -      Option: Fire
				2020-04-05 19:51:54 [Client] INFO  n.r.c.p.e.EventDebuggerPlugin -      Target: <col=ffff>Dwarf multicannon
				2020-04-05 19:51:54 [Client] INFO  n.r.c.p.e.EventDebuggerPlugin -      Identifier:     6
				2020-04-05 19:51:54 [Client] INFO  n.r.c.p.e.EventDebuggerPlugin -      Opcode: 3
				2020-04-05 19:51:54 [Client] INFO  n.r.c.p.e.EventDebuggerPlugin -      Param0: 52
				2020-04-05 19:51:54 [Client] INFO  n.r.c.p.e.EventDebuggerPlugin -      Param1: 55
				2020-04-05 19:51:54 [Client] INFO  n.r.c.p.e.EventDebuggerPlugin -      ForceLeftClick: false
				2020-04-05 19:51:54 [Client] INFO  n.r.c.p.e.EventDebuggerPlugin -      Modified:       false
				*/
				
				menuManager.addPriorityEntry("Fire", "Dwarf multicannon");
				client.insertMenuItem("Fire", "<col=ffff>Dwarf multicannon", 6,3,52,55,false);
				InputHandler.leftClick(client, p);
				menuManager.removePriorityEntry("Fire", "Dwarf multicannon");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		});
	}
}