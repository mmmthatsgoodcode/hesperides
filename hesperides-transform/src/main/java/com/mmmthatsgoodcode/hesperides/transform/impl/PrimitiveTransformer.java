package com.mmmthatsgoodcode.hesperides.transform.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.transform.TransformationException;
import com.mmmthatsgoodcode.hesperides.transform.Transformer;

public class PrimitiveTransformer<T extends Object> implements Transformer<T> {

	@Override
	public Node serialize(T object) {
		
		Node node = new NodeImpl();
		node.setType(object.getClass());
		
		if (object instanceof Integer) {
			node.setValue((Integer) object);
			return node;
		}
		
		if (object instanceof Long) {
			node.setValue((Long) object);
			return node;
		}
		
		if (object instanceof Float) {
			node.setValue((Float) object);
			return node;
		}
		
		if (object instanceof Boolean) {
			node.setValue((Boolean) object);
			return node;
		}
		
		if (object instanceof String) {
			node.setValue((String) object);
			return node;
		}
		
		return node;
		
	}

	@Override
	public T deserialize(Node<? extends Object, T> node) throws TransformationException {
		
		return (T) node.getValue();

		
	}

}
