package com.mmmthatsgoodcode.hesperides.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

public class NodeImpl<N, T> implements Node<N, T> {

	private int hint = Hesperides.Types.OBJECT;
	private int nameHint = Hesperides.Types.STRING;

	private Class type = Object.class;

	private T value = null;
	private N name = null;
	private Node root = null;
	private ArrayList<Node> children = new ArrayList<Node>();
	
	@Override
	public T getValue() {
		return this.value;
	}


	@Override
	public int getHint() {
		return this.hint;
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


	@Override
	public void setValue(String value) {
		this.name = name;
		this.value = (T) value;
		this.hint = Hesperides.Types.STRING;
	}


	@Override
	public void setValue(Integer value) {
		this.name = name;
		this.value = (T) value;
		this.hint = Hesperides.Types.INT;		
	}


	@Override
	public void setValue(Long value) {
		this.name = name;
		this.value = (T) value;
		this.hint = Hesperides.Types.LONG;		
	}


	@Override
	public void setValue(Float value) {
		this.name = name;
		this.value = (T) value;
		this.hint = Hesperides.Types.FLOAT;		
	}


	@Override
	public void setValue(Boolean value) {
		this.name = name;
		this.value = (T) value;
		this.hint = Hesperides.Types.BOOLEAN;
	}


	@Override
	public void setValue(ByteBuffer value) {
		this.name = name;
		this.value = (T) value;
		this.hint = Hesperides.Types.BYTES;		
	}


	@Override
	public N getName() {
		return this.name;
	}


	@Override
	public void setName(int hint, N name) {
		this.nameHint = hint;
		this.name = name;
	}


	@Override
	public void setType(Class type) {
		this.type = type;
	}


	@Override
	public Class getType() {
		return this.type;
	}



	@Override
	public int getNameHint() {
		return nameHint;
	}

	public String toString() {
		
		ArrayList<String> out = new ArrayList<String>();
		
		out.add("Name: "+this.name);
		out.add("Value: "+this.value);
		out.add("Children: ");
		for (Node child:children) {
			out.add(child.toString());
		}
		
		return StringUtils.join(out, ", ");
		
	}


}
