package com.mmmthatsgoodcode.hesperides.transform.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.type.NullValue;

public class PrimitiveTransformer<T extends Object> implements Node.Transformer<T> {

	private static final Logger LOG = LoggerFactory.getLogger(PrimitiveTransformer.class);
	
	@Override
	public Node.Builder transform(T object) {
		
		LOG.trace("Transforming primitive {}", object);
		
		Node.Builder node = new NodeImpl.Builder();
		
		if (object == null) {
			node.setValue(new NullValue());
			return node;
		}
		
		node.setRepresentedType(object.getClass());
		
		node.setValue(AbstractType.wrap(object));
		
		return node;
		
	}

	@Override
	public T transform(Node<?, ?> node) throws TransformationException {
		
		if (node.getValue() == null || node.getValue().equals(new NullValue())) return null;
		return (T) node.getValue().getValue();

		
	}

}
