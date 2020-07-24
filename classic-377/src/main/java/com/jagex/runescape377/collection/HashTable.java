package com.jagex.runescape377.collection;

public class HashTable
{

	public int size;
	public Node cache[];

	public HashTable(int _size)
	{
		size = _size;
		cache = new Node[_size];
		for (int nodeId = 0; nodeId < _size; nodeId++)
		{
			Node node = cache[nodeId] = new Node();
			node.next = node;
			node.previous = node;
		}
	}

	public Node get(long id)
	{
		Node bucket = cache[(int) (id & (size - 1))];
		for (Node node = bucket.next; node != bucket; node = node.next)
		{
			if (node.id == id)
			{
				return node;
			}
		}
		return null;
	}

	public void put(Node node, long id)
	{
		if (node.previous != null)
		{
			node.remove();
		}
		Node bucket = cache[(int) (id & (size - 1))];
		node.previous = bucket.previous;
		node.next = bucket;
		node.previous.next = node;
		node.next.previous = node;
		node.id = id;
		return;
	}


}
