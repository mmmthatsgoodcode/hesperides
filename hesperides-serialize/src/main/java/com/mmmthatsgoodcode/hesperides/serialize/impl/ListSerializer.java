package com.mmmthatsgoodcode.hesperides.serialize.impl;

import java.util.List;

import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.serialize.SerializationException;
import com.mmmthatsgoodcode.hesperides.serialize.Serializer;
import com.mmmthatsgoodcode.hesperides.serialize.SerializerRegistry;

public class ListSerializer<T extends List> implements Serializer<T> {

	@Override
	public Node serialize(Class type, T object) throws SerializationException {
		Node listNode = new NodeImpl();
		listNode.setType(type);
		
		for(Object child:((T) object)) {
			Node childNode = SerializerRegistry.getInstance().get(child.getClass()).serialize(child.getClass(), child);
			listNode.addChild(childNode);
		}
		
		return listNode;
		
	}

	@Override
	public T deserialize(Node<? extends Object, T> node) throws SerializationException {
		
		T instance = null;
		try {
			instance = node.getType().newInstance();
			
			for (Node child:node) {
				instance.add( SerializerRegistry.getInstance().get(child.getClass()).deserialize(child) );
			}
			
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SerializationException(e);
		}
		
		
		return instance;
		
	}

}
