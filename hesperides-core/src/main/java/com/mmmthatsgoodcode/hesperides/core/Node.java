package com.mmmthatsgoodcode.hesperides.core;
import java.util.Iterator;
import java.util.List;

public interface Node<T> extends Iterable<Node> {

	public T getValue();
	public void setValue(T value);
	
	public int getType();
	
	public Node getRoot();
	public void setRoot(Node root);
	
	public Node addChild(Node child);
	public void addChildren(Iterable<Node> children);
	public List<Node> getChildren();
	
}
