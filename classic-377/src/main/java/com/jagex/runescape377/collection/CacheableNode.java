package com.jagex.runescape377.collection;

public class CacheableNode extends Node
{

	public CacheableNode next;
	public CacheableNode prev;

	public CacheableNode()
	{
	}

	public void clear()
	{
		if (prev == null)
		{
			return;
		}
		else
		{
			prev.next = next;
			next.prev = prev;
			next = null;
			prev = null;
			return;
		}
	}
}
