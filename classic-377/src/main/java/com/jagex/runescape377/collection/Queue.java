package com.jagex.runescape377.collection;

public class Queue
{
	public CacheableNode head = new CacheableNode();
	public CacheableNode current;

	public Queue()
	{
		head.next = head;
		head.prev = head;
	}

	public void push(CacheableNode node)
	{
		if (node.prev != null)
		{
			node.clear();
		}
		node.prev = head.prev;
		node.next = head;
		node.prev.next = node;
		node.next.prev = node;
	}

	public CacheableNode pop()
	{
		CacheableNode node = head.next;
		if (node == head)
		{
			return null;
		}
		else
		{
			node.clear();
			return node;
		}
	}

	public CacheableNode first()
	{
		CacheableNode node = head.next;
		if (node == head)
		{
			current = null;
			return null;
		}
		else
		{
			current = node.next;
			return node;
		}
	}

	public CacheableNode next()
	{
		CacheableNode node = current;
		if (node == head)
		{
			current = null;
			return null;
		}
		current = node.next;
		return node;
	}

	public int size()
	{
		int size = 0;
		for (CacheableNode node = head.next; node != head; node = node.next)
		{
			size++;
		}
		return size;
	}


}
