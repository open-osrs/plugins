package com.jagex.runescape377.util;

import com.jagex.runescape377.collection.Node;

public class LinkedList
{

	public Node head = new Node();
	public Node current;

	public LinkedList()
	{
		head.next = head;
		head.previous = head;
	}

	public void pushBack(Node node)
	{
		if (node.previous != null)
		{
			node.remove();
		}
		node.previous = head.previous;
		node.next = head;
		node.previous.next = node;
		node.next.previous = node;
	}

	public void push(Node node)
	{
		if (node.previous != null)
		{
			node.remove();
		}
		node.previous = head;
		node.next = head.next;
		node.previous.next = node;
		node.next.previous = node;
	}

	public Node pop()
	{
		Node node = head.next;
		if (node == head)
		{
			return null;
		}
		else
		{
			node.remove();
			return node;
		}
	}

	public Node first()
	{
		Node node = head.next;
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

	public Node last()
	{
		Node node = head.previous;
		if (node == head)
		{
			current = null;
			return null;
		}
		else
		{
			current = node.previous;
			return node;
		}
	}

	public Node next()
	{
		Node node = current;
		if (node == head)
		{
			current = null;
			return null;
		}
		current = node.next;
		return node;
	}

	public Node previous()
	{
		Node node = current;
		if (node == head)
		{
			current = null;
			return null;
		}
		else
		{
			current = node.previous;
			return node;
		}
	}

	public void clear()
	{
		if (head.next == head)
		{
			return;
		}
		do
		{
			Node node = head.next;
			if (node == head)
			{
				return;
			}
			node.remove();
		} while (true);
	}

}
