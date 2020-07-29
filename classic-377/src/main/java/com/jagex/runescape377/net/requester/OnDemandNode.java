package com.jagex.runescape377.net.requester;

import com.jagex.runescape377.collection.CacheableNode;

public class OnDemandNode extends CacheableNode
{

	public int type;
	public int id;
	public int cyclesSinceSend;
	public byte[] buffer;
	public boolean immediate = true;
}
