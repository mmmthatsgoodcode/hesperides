package com.mmmthatsgoodcode.hesperides.serialize.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.serialize.SerializationException;
import com.mmmthatsgoodcode.hesperides.serialize.Serializer;

public class PrimitiveSerializer<T extends Object> implements Serializer<T> {

	@Override
	public Node serialize(Class type, T object) {
		
		Node node = new NodeImpl();
		node.setType(type);
		
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
	public T deserialize(Node<? extends Object, T> node) throws SerializationException {
		
		return (T) node.getValue();

		
	}

}
