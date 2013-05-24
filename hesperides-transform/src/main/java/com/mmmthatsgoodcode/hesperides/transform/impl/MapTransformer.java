package com.mmmthatsgoodcode.hesperides.transform.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mmmthatsgoodcode.hesperides.core.GenericTransformer;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.Transformer;
import com.mmmthatsgoodcode.hesperides.transform.TransformerRegistry;

public class MapTransformer<T extends Map> implements GenericTransformer<T> {

	private List<Class> genericTypes = new ArrayList<Class>();
	
	private Class<? extends Object> keyType = Object.class, valueType = Object.class;
	
	
	public Class<? extends Object> getKeyGenericType() {
		return this.genericTypes.get(0);
	}
	
	public Class<? extends Object> getValueGenericType() {
		return this.genericTypes.get(1);
	}	
	
	public Node transform(T map) throws TransformationException {
		
		Node mapNode = new NodeImpl();
		
		if (map == null) {
			mapNode.setNullValue();
			return mapNode;
		}
		
		mapNode.setRepresentedType(map.getClass());

		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			
			Node childNode = TransformerRegistry.getInstance().get(getValueGenericType()).transform(entry.getValue()) ;

			childNode.setName( Hesperides.Hints.typeToHint(getKeyGenericType()), entry.getKey() );
			childNode.setRepresentedType(getValueGenericType());
			
			mapNode.addChild(childNode);
		}
		
		return mapNode;
		
	}
	
	public T transform(Node<? extends Object, T> node) throws TransformationException {
		
		T instance = null;
		
		try {
			
			if (node.getValueHint() == Hesperides.Hints.NULL) return null;
			
			instance = node.getRepresentedType().newInstance();
			for (Node child:node) {
				instance.put(child.getName(), TransformerRegistry.getInstance().get(child.getRepresentedType()).transform(child));
			}
			
		} catch (InstantiationException | IllegalAccessException e) {
			throw new TransformationException(e);
		}

		
		return instance;
		
	}

	@Override
	public List<Class> getGenericTypes() {
		return genericTypes;
	}

	@Override
	public void addGenericType(Class clazz) {
		genericTypes.add(clazz);
	}
	
}
