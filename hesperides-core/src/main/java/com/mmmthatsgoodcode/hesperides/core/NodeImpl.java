package com.mmmthatsgoodcode.hesperides.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

public class NodeImpl<N, T extends Object> implements Node<N, T> {

	private Date created = new Date();
	private int ttl = 0;
	
	private boolean indexed = false;
	
	private int valueHint = Hesperides.Hints.OBJECT;
	private int nameHint = Hesperides.Hints.STRING;

	private Class<? extends Object> representedType = NodeImpl.class;

	private T value = null;
	private N name = null;
	private Map<Object, Node> children = new HashMap<Object, Node>();
	
	public NodeImpl() {
		
	}
	
	public NodeImpl(N name) {
		setName(name);
	}
	
	public NodeImpl(N name, boolean indexed) {
		this(name);
		setIndexed(indexed);
	}
	
	@Override
	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}
	
	@Override
	public boolean isIndexed() {
		return this.indexed;
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
		this.children.put(child.getName(), child);
		return child;
	}
	
	@Override
	public void removeChild(Object name) {
		
		for(Iterator<Node> iterator = children.values().iterator(); iterator.hasNext(); ) {
			
			Node child = iterator.next();
			if (child.getName().equals(name)) iterator.remove();
			
		}
		
	}

	@Override
	public void addChildren(Iterable<Node> children) {
		for(Node child:children) {
			this.children.put(child.getName(), child);
		}
	}	
	
	@Override
	public Collection<Node> getChildren() {
		return children.values();
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
		return this.children.values().iterator();
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
		if (this.isIndexed()) out.add("indexed");
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
		
		if (other.getRepresentedType().equals(this.getRepresentedType()) == false
		|| other.getValueHint() != this.getValueHint()
		|| other.getNameHint() != this.getNameHint()
		|| other.getName().equals(this.getName()) == false
		|| other.getCreated().equals(this.getCreated()) == false) return false;
		
		if (getChildren().size() != other.getChildren().size()) return false;
		for (Node child:getChildren()) {
		    if (other.getChildren().contains(child) == false) return false;
		}
		
		return true;
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
