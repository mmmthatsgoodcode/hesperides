package com.mmmthatsgoodcode.hesperides.transform.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.Transformer;

public class PrimitiveTransformer<T extends Object> implements Transformer<T> {

	private static final Logger LOG = LoggerFactory.getLogger(PrimitiveTransformer.class);
	
	@Override
	public Node transform(T object) {
		
		LOG.trace("Transforming primitive {}", object);
		
		Node node = new NodeImpl();
		
		if (object == null) {
			node.setNullValue();
			return node;
		}
		
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
	public T transform(Node<? extends Object, T> node) throws TransformationException {
		
		if (node.getValue() == null) return null;
		return (T) node.getValue();

		
	}

}
