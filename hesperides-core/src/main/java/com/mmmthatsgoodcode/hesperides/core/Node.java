package com.mmmthatsgoodcode.hesperides.core;
import java.util.List;

public interface Node<T> {

	public T getValue();
	public void setValue(T value);
	
	public Node getRoot();
	public void setRoot(Node root);
	
	public Node addChild(Node child);
	public List<Node> getChildren();
	
}
