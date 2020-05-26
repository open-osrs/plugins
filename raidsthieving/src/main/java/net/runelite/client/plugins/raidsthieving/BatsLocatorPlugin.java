/*
 * Copyright (c) 2020, chestnut1693 <chestnut1693@gmail.com>
 * Copyright (c) 2018, Kamiel
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.raidsthieving;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import static net.runelite.client.plugins.raidsthieving.BatsLocator.CLOSED;
import static net.runelite.client.plugins.raidsthieving.BatsLocator.OPENED_POISON_OR_BATS;
import static net.runelite.client.plugins.raidsthieving.BatsLocator.OPENED_WITHOUT_GRUBS;
import static net.runelite.client.plugins.raidsthieving.BatsLocator.OPENED_WITH_GRUBS;
import static net.runelite.client.plugins.raidsthieving.BatsLocator.POISON_SPLAT;
import static net.runelite.client.plugins.raidsthieving.BatsLocator.TROUGH;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Bats Locator",
	description = "Helps locate the chest with bats at the thieving room inside Chambers of Xeric",
	tags = {"finder", "thieving"},
	enabledByDefault = false,
	type = PluginType.PVM
)
public class BatsLocatorPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BatsOverlay overlay;

	@Getter(AccessLevel.PACKAGE)
	private BatsLocator batsLocator;

	@Getter(AccessLevel.PACKAGE)
	private boolean inRaidChambers;

	@Provides
	BatsLocatorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BatsLocatorConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		clientThread.invokeLater(() -> checkRaidPresence(true));
		batsLocator = new BatsLocator(client);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		inRaidChambers = false;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("raids"))
		{
			return;
		}

		clientThread.invokeLater(() -> checkRaidPresence(true));
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		checkRaidPresence(false);
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject gameObject = event.getGameObject();
		switch (gameObject.getId())
		{
			case TROUGH:
				batsLocator.troughSpawnEvent(gameObject);
				break;
			case CLOSED:
			case OPENED_POISON_OR_BATS:
			case OPENED_WITHOUT_GRUBS:
			case OPENED_WITH_GRUBS:
				batsLocator.chestSpawnEvent(gameObject);
				break;
		}
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated event)
	{
		if (inRaidChambers)
		{
			GraphicsObject graphicsObject = event.getGraphicsObject();
			if (graphicsObject.getId() == POISON_SPLAT)
			{
				batsLocator.poisonSplatEvent(WorldPoint.fromLocal(client, graphicsObject.getLocation()));
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (inRaidChambers)
		{
			batsLocator.gameTickEvent();
		}
	}

	private void checkRaidPresence(boolean force)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		boolean setting = client.getVar(Varbits.IN_RAID) == 1;

		if (force || inRaidChambers != setting)
		{
			//A new instance is created when leaving the raid chambers instead of entering the raid chambers.
			//Entering the raid chambers will change the IN_RAID varbit but game objects spawn before the varbit change.
			if (!setting)
			{
				batsLocator = new BatsLocator(client);
			}

			inRaidChambers = setting;
		}
	}
}