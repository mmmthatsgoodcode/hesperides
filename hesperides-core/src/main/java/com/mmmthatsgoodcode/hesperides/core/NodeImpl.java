package com.mmmthatsgoodcode.hesperides.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NodeImpl<T> implements Node<T> {

	private int type = 0;
	private T value = null;
	private Node root = null;
	private ArrayList<Node> children = new ArrayList<Node>();
	
	@Override
	public T getValue() {
		return this.value;
	}

	@Override
	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public int getType() {
		return this.type;
	}

	@Override
	public Node getRoot() {
		return this.root;
	}

	@Override
	public void setRoot(Node root) {
		this.root = root;
	}

	@Override
	public Node addChild(Node child) {
		this.children.add(child);
		return child;
	}

	@Override
	public void addChildren(Iterable<Node> children) {
		for(Node child:children) {
			this.children.add(child);
		}
	}	
	
	@Override
	public List<Node> getChildren() {
		return children;
	}

	@Override
	public Iterator<Node> iterator() {
		return this.children.iterator();
	}




}
