package com.jagex.runescape377.collection;

public class Cache
{

	public int misses;
	public int hits;
	public int capacity;
	public int remaining;
	public HashTable hashTable;
	public Queue queue = new Queue();

	public Cache(int size)
	{
		this.capacity = size;
		this.remaining = size;
		this.hashTable = new HashTable(1024);
	}

	public CacheableNode get(long id)
	{
		CacheableNode cacheablenode = (CacheableNode) hashTable.get(id);
		if (cacheablenode != null)
		{
			queue.push(cacheablenode);
		}
		return cacheablenode;
	}

	public void put(CacheableNode cacheableNode, long id)
	{
		if (remaining == 0)
		{
			CacheableNode oldestNode = queue.pop();
			oldestNode.remove();
			oldestNode.clear();
		}
		else
		{
			remaining--;
		}
		hashTable.put(cacheableNode, id);
		queue.push(cacheableNode);
	}

	public void removeAll()
	{
		do
		{
			CacheableNode node = queue.pop();
			if (node != null)
			{
				node.remove();
				node.clear();
			}
			else
			{
				remaining = capacity;
				return;
			}
		} while (true);
	}


}
