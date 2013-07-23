package com.mmmthatsgoodcode.hesperides.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

public class NodeImpl<N, T extends Object> implements Node<N, T> {

	private Date created = new Date();
	private int ttl = 0;
	
	private int valueHint = Hesperides.Hints.OBJECT;
	private int nameHint = Hesperides.Hints.STRING;

	private Class<? extends Object> representedType = NodeImpl.class;

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
	public int getValueHint() {
		return this.valueHint;
	}

	@Override
	public Node addChild(Node child) {
		this.children.add(child);
		return child;
	}
	
	@Override
	public void removeChild(Object name) {
		
		for(Iterator<Node> iterator = children.iterator(); iterator.hasNext(); ) {
			
			Node child = iterator.next();
			if (child.getName().equals(name)) iterator.remove();
			
		}
		
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
	public Node getChild(Object name) {
		
		for(Node child:getChildren()) {
			
			if (child.getName().equals(name)) return child;
			
		}
		
		return null;
		
	}

	@Override
	public Iterator<Node> iterator() {
		return this.children.iterator();
	}


	@Override
	public void setValue(String value) {
		this.value = (T) value;
		this.valueHint = Hesperides.Hints.STRING;
		this.representedType = String.class;
	}


	@Override
	public void setValue(Integer value) {
		this.value = (T) value;
		this.valueHint = Hesperides.Hints.INT;		
		this.representedType = Integer.class;
	}


	@Override
	public void setValue(Long value) {
		this.value = (T) value;
		this.valueHint = Hesperides.Hints.LONG;		
		this.representedType = Long.class;
	}


	@Override
	public void setValue(Float value) {
		this.value = (T) value;
		this.valueHint = Hesperides.Hints.FLOAT;		
		this.representedType = Float.class;
	}


	@Override
	public void setValue(Boolean value) {
		this.value = (T) value;
		this.valueHint = Hesperides.Hints.BOOLEAN;
		this.representedType = Boolean.class;
	}


	@Override
	public void setValue(ByteBuffer value) {
		this.value = (T) value;
		this.valueHint = Hesperides.Hints.BYTES;	
		this.representedType = ByteBuffer.class;
	}
	
	public void setNullValue() {
		this.value = null;
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
	public void setRepresentedType(Class type) {
		this.representedType = type;
	}


	@Override
	public Class getRepresentedType() {
		return this.representedType;
	}



	@Override
	public int getNameHint() {
		return nameHint;
	}

	public String toString() {
		
		return toString(0);
		
	}
	
	public String toString(int depth) {
		
		ArrayList<String> out = new ArrayList<String>();
		
		out.add("Hint: "+this.valueHint);
		out.add("Type: "+this.representedType.getSimpleName());
		out.add("Name: "+this.name);
		out.add("Value: "+this.value);
		
		
		
		String outString = (depth>0?StringUtils.repeat("--", depth)+"| ":"")+"(@"+getCreated().getTime()+")"+StringUtils.join(out, ", ")+"\n";
		depth++;
		for (Node child:getChildren()) {
			outString += ((NodeImpl) child).toString(depth);
		}
		
		return outString;
		
	}
	
	public boolean equals(Object object) {
		
		if (!Node.class.isAssignableFrom(object.getClass())) return false;
		NodeImpl other = (NodeImpl) object;
		
//		System.out.println(other.getRepresentedType().equals(this.getRepresentedType())?"yes":"no");
//		System.out.println(other.getValueHint() == this.getValueHint()?"yes":"no, "+other.getValueHint()+" != "+this.getValueHint());
//		System.out.println(other.getNameHint() == this.getNameHint()?"yes":"no");
//		System.out.println(other.getChildren().equals(this.getChildren())?"yes":"no");
//		System.out.println("---");
		
		return other.getRepresentedType().equals(this.getRepresentedType())
				&& other.getValueHint() == this.getValueHint()
				&& other.getNameHint() == this.getNameHint()
				&& other.getName().equals(this.getName())
				&& other.getCreated().equals(this.getCreated())
				&& other.getChildren().equals(this.getChildren());
	}

	@Override
	public int getTtl() {
		return this.ttl;
	}

	@Override
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	@Override
	public Date getCreated() {
		return this.created;
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;
	}


}
