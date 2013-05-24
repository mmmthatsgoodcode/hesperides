package com.mmmthatsgoodcode.hesperides.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

public class NodeImpl<N, T extends Object> implements Node<N, T> {

	private int hint = Hesperides.Hints.OBJECT;
	private int nameHint = Hesperides.Hints.STRING;

	private Class<? extends Object> type = NodeImpl.class;

	private T value = null;
	private N name = null;
	private ArrayList<Node> children = new ArrayList<Node>();
	
	public NodeImpl() {
		
	}
	
	public NodeImpl(N name) {
		setName(name);
	}
	
	@Override
	public T getValue() {
		return this.value;
	}


	@Override
	public int getHint() {
		return this.hint;
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
		this.value = (T) value;
		this.hint = Hesperides.Hints.STRING;
	}


	@Override
	public void setValue(Integer value) {
		this.value = (T) value;
		this.hint = Hesperides.Hints.INT;		
	}


	@Override
	public void setValue(Long value) {
		this.value = (T) value;
		this.hint = Hesperides.Hints.LONG;		
	}


	@Override
	public void setValue(Float value) {
		this.value = (T) value;
		this.hint = Hesperides.Hints.FLOAT;		
	}


	@Override
	public void setValue(Boolean value) {
		this.value = (T) value;
		this.hint = Hesperides.Hints.BOOLEAN;
	}


	@Override
	public void setValue(ByteBuffer value) {
		this.value = (T) value;
		this.hint = Hesperides.Hints.BYTES;		
	}
	
	public void setNullValue() {
		this.value = null;
		this.hint = Hesperides.Hints.NULL;
	}


	@Override
	public N getName() {
		return this.name;
	}

	public void setName(N name) {
		setName(Hesperides.Hints.typeToHint(name.getClass()), name);
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
		
		out.add("Hint: "+this.hint);
		out.add("Type: "+this.type.getSimpleName());
		out.add("Name: "+this.name);
		out.add("Value: "+this.value);
		out.add("Children: ");
		for (Node child:children) {
			out.add(child.toString());
		}
		
		return StringUtils.join(out, ", ");
		
	}
	
	public boolean equals(Object object) {
		
		if (!Node.class.isAssignableFrom(object.getClass())) return false;
		NodeImpl other = (NodeImpl) object;
		
//		System.out.println(other.getType().equals(this.getType())?"yes":"no");
//		System.out.println(other.getHint() == this.getHint()?"yes":"no, "+other.getHint()+" != "+this.getHint());
//		System.out.println(other.getNameHint() == this.getNameHint()?"yes":"no");
//		System.out.println(other.getChildren().equals(this.getChildren())?"yes":"no");
//		System.out.println("---");
		
		return other.getType().equals(this.getType())
				&& other.getHint() == this.getHint()
				&& other.getNameHint() == this.getNameHint()
				&& other.getName().equals(this.getName())
				&& other.getChildren().equals(this.getChildren());
	}


}
