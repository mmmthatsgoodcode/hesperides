package com.mmmthatsgoodcode.hesperides.core;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public interface Node<N, T extends Object> extends Iterable<Node> {

	public int getTtl();
	public void setTtl(int ttl);
	
	public Date getCreated();
	public void setCreated(Date created);
	
	public void setIndexed(boolean indexed);
	public boolean isIndexed();
	
	public void setName(int hint, N name);
	public N getName();
	public int getNameHint();
	
	public T getValue();
	
	public void setValue(String value);
	public void setValue(Integer value);
	public void setValue(Long value);
	public void setValue(Float value);
	public void setValue(Boolean value);
	public void setValue(ByteBuffer value);
	public void setNullValue();
	public int getValueHint();
	

	public void setRepresentedType(Class<T> type);
	public Class<T> getRepresentedType();
	
	public Node addChild(Node child);
	public void removeChild(Object name);
	
	public void addChildren(Iterable<Node> children);
	
	public Collection<Node> getChildren();
	public Node getChild(Object name);
	
	public boolean equals(Object object);
	
}
