/*
 * Copyright (c) 2020, chestnut1693 <chestnut1693@gmail.com>
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

import java.util.ArrayList;
import java.util.Comparator;
import static java.util.Comparator.comparing;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import static net.runelite.api.Constants.CHUNK_SIZE;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

public class BatsLocator
{
	static final int TROUGH = ObjectID.TROUGH_29746;
	static final int CLOSED = ObjectID.CHEST_29742;
	static final int OPENED_POISON_OR_BATS = ObjectID.CHEST_29743;
	static final int OPENED_WITHOUT_GRUBS = ObjectID.CHEST_29744;
	static final int OPENED_WITH_GRUBS = ObjectID.CHEST_29745;
	static final int POISON_SPLAT = 184;

	@Getter(AccessLevel.PACKAGE)
	private RoomType roomType;

	@Getter(AccessLevel.PACKAGE)
	private final Map<WorldPoint, Chest> chests = new HashMap<>();

	@Getter(AccessLevel.PACKAGE)
	private final Set<Chest> poisonBatsChests = new HashSet<>();

	@Getter(AccessLevel.PACKAGE)
	private final Set<Chest> grubsChests = new HashSet<>();

	@Getter(AccessLevel.PACKAGE)
	private final List<List<Chest>> solutionSets = new ArrayList<>();

	@Getter(AccessLevel.PACKAGE)
	private int highestSolutionSetCount = 0;

	@Getter(AccessLevel.PACKAGE)
	private boolean drawChestStates = false;

	private final Client client;
	private int rotation = -1;

	public BatsLocator(Client client)
	{
		this.client = client;
	}

	public void troughSpawnEvent(GameObject trough)
	{
		if (roomType == null && rotation == -1)
		{
			int plane = trough.getPlane();
			int chunkX = trough.getLocalLocation().getSceneX() / CHUNK_SIZE;
			int chunkY = trough.getLocalLocation().getSceneY() / CHUNK_SIZE;
			int chunkData = client.getInstanceTemplateChunks()[plane][chunkX][chunkY];
			int instanceX = (chunkData >> 14 & 0x3FF) * CHUNK_SIZE;
			rotation = chunkData >> 1 & 0x3;
			switch (instanceX)
			{
				case 3264:
				case 3272:
					roomType = RoomType.LEFT;
					break;
				case 3344:
					roomType = RoomType.RIGHT;
					break;
				case 3312:
					roomType = RoomType.STRAIGHT;
					break;
			}
			//This code is repeated from the chest spawn event since the room type may not have been set when the last chest spawns, same goes for the rotation.
			assignChestNumbersAndGenerateSolutionSets();
		}
	}

	public void chestSpawnEvent(GameObject chestObject)
	{
		WorldPoint chestLocation = chestObject.getWorldLocation();
		Chest chest = chests.get(chestLocation);
		if (chest == null)
		{
			chest = new Chest(chestLocation);
			chests.put(chestLocation, chest);
			switch (chestObject.getId())
			{
				case OPENED_POISON_OR_BATS:
					poisonBatsChests.add(chest);
					break;
				case OPENED_WITHOUT_GRUBS:
				case OPENED_WITH_GRUBS:
					grubsChests.add(chest);
					break;
			}
			//This code is repeated at the trough spawn event since the room type may not have been set when the last chest spawns, same goes for the rotation.
			assignChestNumbersAndGenerateSolutionSets();
		}
		else
		{
			switch (chestObject.getId())
			{
				case OPENED_POISON_OR_BATS:
					poisonBatsChests.add(chest);
					openChest(chest, Chest.State.POISON);
					break;
				case OPENED_WITHOUT_GRUBS:
				case OPENED_WITH_GRUBS:
					grubsChests.add(chest);
					openChest(chest, Chest.State.GRUBS);
					break;
			}
		}
	}

	private List<List<Chest>> solutionSetsContaining(Chest chest)
	{
		List<List<Chest>> solutionSets = new ArrayList<>();
		for (List<Chest> solutionSet : this.solutionSets)
		{
			if (solutionSet.contains(chest))
			{
				solutionSets.add(solutionSet);
			}
		}
		return solutionSets;
	}

	private boolean solutionSetContains(List<Chest> solutionSet, Chest.State state)
	{
		for (Chest chest : solutionSet)
		{
			if (chest.getState() == state)
			{
				return true;
			}
		}
		return false;
	}

	private void openChest(Chest openedChest, Chest.State state)
	{
		openedChest.setState(state);

		if (solutionSets.size() == 0)
		{
			if (poisonBatsChests.size() == 4)
			{
				for (Chest chest : chests.values())
				{
					if (!poisonBatsChests.contains(chest))
					{
						chest.setState(Chest.State.GRUBS);
					}
				}
			}
			return;
		}

		if (state == Chest.State.POISON || state == Chest.State.BATS)
		{
			Set<Chest> possiblePoisonBatsChests = new HashSet<>();
			for (List<Chest> solutionSet : solutionSetsContaining(openedChest))
			{
				for (Chest chest : solutionSet)
				{
					if (chest.getState() != Chest.State.GRUBS)
					{
						possiblePoisonBatsChests.add(chest);
					}
				}
			}
			if (possiblePoisonBatsChests.size() == 0)
			{
				solutionSets.clear();
			}
			for (List<Chest> solutionSet : solutionSets)
			{
				for (Chest chest : solutionSet)
				{
					if (!possiblePoisonBatsChests.contains(chest) && chest.getState() == Chest.State.UNVISITED)
					{
						chest.setState(Chest.State.GRUBS);
					}
				}
			}
		}

		Iterator<List<Chest>> solutionSets = this.solutionSets.iterator();
		while (solutionSets.hasNext())
		{
			List<Chest> solutionSet = solutionSets.next();
			for (Chest chest : solutionSet)
			{
				if (chest.getState() == Chest.State.UNVISITED)
				{
					boolean setState = true;
					for (List<Chest> otherSolutionSet : solutionSetsContaining(chest))
					{
						if (!solutionSetContains(otherSolutionSet, Chest.State.GRUBS))
						{
							setState = false;
							break;
						}
					}
					if (setState)
					{
						chest.setState(Chest.State.GRUBS);
					}
				}
			}
			if (solutionSetContains(solutionSet, Chest.State.GRUBS))
			{
				solutionSets.remove();
			}
		}

		if (this.solutionSets.size() == 1 && solutionSetContains(this.solutionSets.get(0), Chest.State.BATS))
		{
			for (Chest chest : this.solutionSets.get(0))
			{
				if (chest.getState() == Chest.State.UNVISITED)
				{
					chest.setState(Chest.State.POISON);
				}
			}
		}

		if (this.solutionSets.size() == 0)
		{
			if (poisonBatsChests.size() == 4)
			{
				for (Chest chest : chests.values())
				{
					if (!poisonBatsChests.contains(chest))
					{
						chest.setState(Chest.State.GRUBS);
					}
				}
			}
			else
			{
				for (Chest chest : chests.values())
				{
					if (!poisonBatsChests.contains(chest) && !grubsChests.contains(chest))
					{
						chest.setState(Chest.State.UNVISITED);
					}
				}
			}
		}

		findSolutionSetCounts();
	}

	private void findSolutionSetCounts()
	{
		highestSolutionSetCount = 0;
		for (Chest chest : chests.values())
		{
			chest.setSolutionSetCount(0);
			if (chest.getState() != Chest.State.UNVISITED)
			{
				continue;
			}
			for (List<Chest> solutionSet : solutionSetsContaining(chest))
			{
				if (!solutionSetContains(solutionSet, Chest.State.GRUBS))
				{
					chest.setSolutionSetCount(chest.getSolutionSetCount() + 1);
				}
			}
			if (chest.getSolutionSetCount() > highestSolutionSetCount)
			{
				highestSolutionSetCount = chest.getSolutionSetCount();
			}
		}
	}

	private void assignChestNumbersAndGenerateSolutionSets()
	{
		if (rotation != -1 && roomType != null && chests.size() == roomType.getChestCount())
		{
			Comparator<Chest> comparator;
			switch (rotation)
			{
				case 0:
					comparator = comparing(Chest::getLocation, comparing(WorldPoint::getY).thenComparing(WorldPoint::getX));
					break;
				case 1:
					comparator = comparing(Chest::getLocation, comparing(WorldPoint::getX).reversed().thenComparing(WorldPoint::getY).reversed());
					break;
				case 2:
					comparator = comparing(Chest::getLocation, comparing(WorldPoint::getY).thenComparing(WorldPoint::getX).reversed());
					break;
				case 3:
					comparator = comparing(Chest::getLocation, comparing(WorldPoint::getX).reversed().thenComparing(WorldPoint::getY));
					break;
				default:
					//This should never be reached.
					comparator = comparing(Chest::getNumber);
					break;
			}

			List<Chest> chests = new ArrayList<>(this.chests.values());
			chests.sort(comparator);

			for (int i = 0; i < chests.size(); i++)
			{
				chests.get(i).setNumber(i);
			}

			for (int[] indices : roomType.getSolutionSets())
			{
				List<Chest> solutionSet = new ArrayList<>();
				for (int index : indices)
				{
					solutionSet.add(chests.get(index));
				}
				solutionSets.add(solutionSet);
			}

			for (Chest chest : this.chests.values())
			{
				if (solutionSetsContaining(chest).size() == 0)
				{
					chest.setState(Chest.State.GRUBS);
				}
			}
			for (Chest chest : poisonBatsChests)
			{
				openChest(chest, Chest.State.POISON);
			}
			for (Chest chest : grubsChests)
			{
				openChest(chest, Chest.State.GRUBS);
			}

			findSolutionSetCounts();
			drawChestStates = true;
		}
	}

	public void poisonSplatEvent(WorldPoint worldPoint)
	{
		Chest chest = chests.get(worldPoint);
		if (chest != null)
		{
			chest.setTickPoison(client.getTickCount());
		}
	}

	public void gameTickEvent()
	{
		for (Chest chest : poisonBatsChests)
		{
			if (chest.getState() == Chest.State.POISON && chest.getTickPoison() == -1)
			{
				openChest(chest, Chest.State.BATS);
			}
		}
	}
}