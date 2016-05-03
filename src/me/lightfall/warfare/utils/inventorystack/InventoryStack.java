package me.lightfall.warfare.utils.inventorystack;

import org.bukkit.inventory.Inventory;

public class InventoryStack {
	private Node head;
	
	public InventoryStack()
	{
		this.head = null;
	}
	
	public void add(Inventory i)
	{
		Node n = new Node(i);
		if(head == null)
			head = n;
		else
		{
			n.setNext(head);
			head = n;
		}
	}
	
	public void clear() {head = null;}
	
	public Inventory get()
	{
		Node n = head;
		head = n.getNext();
		return n.getInventory();
	}
	
	public boolean hasNext()
	{
		if(head == null)
			return false;
		return true;
	}
	
	
	private class Node {
		private Inventory i;
		private Node next;
		
		public Node(Inventory i) {this.i = i; next = null;}
		public void setNext(Node n) {this.next = n;}
		public Node getNext() {return this.next;}
		public Inventory getInventory() {return this.i;}
	}
}
