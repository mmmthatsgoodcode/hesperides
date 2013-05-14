package com.mmmthatsgoodcode.hesperides.serialize.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.serialize.SerializationException;
import com.mmmthatsgoodcode.hesperides.serialize.Serializer;
import com.mmmthatsgoodcode.hesperides.serialize.SerializerRegistry;

public class MapSerializer<T extends Map> implements Serializer<T> {

	private Class<? extends Object> keyType = Object.class, valueType = Object.class;
	
	public void setKeyGenericType(Class keyType) {
		this.keyType = keyType;
	}
	
	public Class<? extends Object> getKeyGenericType() {
		return this.keyType;
	}
	
	public void setValueGenericType(Class valueType) {
		this.valueType = valueType;
	}
	
	public Class<? extends Object> getValueGenericType() {
		return this.valueType;
	}	
	
	public Node serialize(Class type, T map) throws SerializationException {
		
		Node mapNode = new NodeImpl();
		mapNode.setType(type);

		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			
			Node childNode = SerializerRegistry.getInstance().get(getValueGenericType()).serialize(entry.getValue().getClass(), entry.getValue()) ;

			childNode.setName( Hesperides.Hints.typeToHint(getKeyGenericType()), entry.getKey() );
			childNode.setType(getValueGenericType());
			
			mapNode.addChild(childNode);
		}
		
		return mapNode;
		
	}
	
	public T deserialize(Node<? extends Object, T> node) throws SerializationException {
		
		T instance = null;
		
		try {
			instance = node.getType().newInstance();
			for (Node child:node) {
				instance.put(child.getName(), SerializerRegistry.getInstance().get(child.getType()).deserialize(child));
			}
			
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SerializationException(e);
		}

		
		return instance;
		
	}
	
}
