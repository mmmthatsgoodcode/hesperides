package com.mmmthatsgoodcode.hesperides.core;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

public interface Node<N, T extends Object> extends Iterable<Node> {

	public T getValue();
	public void setValue(String value);
	public void setValue(Integer value);
	public void setValue(Long value);
	public void setValue(Float value);
	public void setValue(Boolean value);
	public void setValue(ByteBuffer value);
	public void setNullValue();

	public void setName(int hint, N name);
	public N getName();
	
	public int getValueHint();
	
	public int getNameHint();

	public void setRepresentedType(Class<T> type);
	public Class<T> getRepresentedType();
	
	public Node addChild(Node child);
	public void removeChild(Object name);
	
	public void addChildren(Iterable<Node> children);
	
	public List<Node> getChildren();
	public Node getChild(Object name);
	
}
