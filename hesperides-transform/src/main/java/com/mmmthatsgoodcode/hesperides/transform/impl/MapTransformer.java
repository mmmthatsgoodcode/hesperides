package com.mmmthatsgoodcode.hesperides.transform.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.transform.TransformationException;
import com.mmmthatsgoodcode.hesperides.transform.Transformer;
import com.mmmthatsgoodcode.hesperides.transform.TransformerRegistry;

public class MapTransformer<T extends Map> implements Transformer<T> {

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
	
	public Node transform(T map) throws TransformationException {
		
		Node mapNode = new NodeImpl();
		mapNode.setType(map.getClass());

		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			
			Node childNode = TransformerRegistry.getInstance().get(getValueGenericType()).transform(entry.getValue()) ;

			childNode.setName( Hesperides.Hints.typeToHint(getKeyGenericType()), entry.getKey() );
			childNode.setType(getValueGenericType());
			
			mapNode.addChild(childNode);
		}
		
		return mapNode;
		
	}
	
	public T transform(Node<? extends Object, T> node) throws TransformationException {
		
		T instance = null;
		
		try {
			instance = node.getType().newInstance();
			for (Node child:node) {
				instance.put(child.getName(), TransformerRegistry.getInstance().get(child.getType()).transform(child));
			}
			
		} catch (InstantiationException | IllegalAccessException e) {
			throw new TransformationException(e);
		}

		
		return instance;
		
	}
	
}
