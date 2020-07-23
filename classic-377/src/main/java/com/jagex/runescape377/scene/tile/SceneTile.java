package com.jagex.runescape377.scene.tile;

import com.jagex.runescape377.collection.Node;
import com.jagex.runescape377.scene.GroundItemTile;
import com.jagex.runescape377.scene.InteractiveObject;

public class SceneTile extends Node
{

	public int z;
	public int x;
	public int y;
	public int renderLevel;
	public GenericTile plainTile;
	public ComplexTile shapedTile;
	public Wall wall;
	public WallDecoration wallDecoration;
	public FloorDecoration floorDecoration;
	public GroundItemTile groundItemTile;
	public int entityCount;
	public InteractiveObject[] interactiveObjects = new InteractiveObject[5];
	public int[] sceneSpawnRequestsSize = new int[5];
	public int interactiveObjectsSizeOR;
	public int logicHeight;
	public boolean draw;
	public boolean visible;
	public boolean drawEntities;
	public int wallCullDirection;
	public int wallUncullDirection;
	public int wallCullOppositeDirection;
	public int wallDrawFlags;
	public SceneTile tileBelow;

	public SceneTile(int x, int y, int z)
	{
		renderLevel = this.z = z;
		this.x = x;
		this.y = y;
	}
}
