package com.jagex.runescape377.media.renderable;

import com.jagex.runescape377.cache.def.ItemDefinition;

public class Item extends Renderable
{
	public int itemId;
	public int itemCount;

	@Override
	public Model getRotatedModel()
	{
		ItemDefinition itemDefinition = ItemDefinition.lookup(itemId);
		return itemDefinition.asGroundStack(itemCount);
	}

}
