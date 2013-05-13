package com.mmmthatsgoodcode.hesperides.core;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

public interface Node<N, T> extends Iterable<Node> {

	public Object getValue();
	public void setValue(String value);
	public void setValue(Integer value);
	public void setValue(Long value);
	public void setValue(Float value);
	public void setValue(Boolean value);
	public void setValue(ByteBuffer value);

	public void setName(int hint, N name);
	public N getName();
	
	public int getHint();
	
	public int getNameHint();

	
	public void setType(Class type);
	public Class<T> getType();
	
	
	public Node getRoot();
	public void setRoot(Node root);
	
	public Node addChild(Node child);
	public void addChildren(Iterable<Node> children);
	public List<Node> getChildren();
	
}
