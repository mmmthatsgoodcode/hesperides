package com.mmmthatsgoodcode.hesperides.transform.impl;

import java.util.ArrayList;
import java.util.List;

import com.mmmthatsgoodcode.hesperides.core.GenericTransformer;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.Transformer;
import com.mmmthatsgoodcode.hesperides.transform.TransformerRegistry;

public class ListTransformer<T extends List> implements GenericTransformer<T> {

	private List<Class> genericTypes = new ArrayList<Class>();

	public Class<? extends Object> getValueGenericType() {
		return this.genericTypes.get(0);
	}		
	
	@Override
	public Node transform(T object) throws TransformationException {
		Node listNode = new NodeImpl();
		
		if (object == null) {
			listNode.setNullValue();
			return listNode;
		}
		
		listNode.setType(object.getClass());
		
		for(Object child:((T) object)) {
			Node childNode = TransformerRegistry.getInstance().get(getValueGenericType()).transform(child);
			listNode.addChild(childNode);
		}
		
		return listNode;
		
	}

	@Override
	public T transform(Node<? extends Object, T> node) throws TransformationException {
		
		T instance = null;
		try {
			
			if (node.getHint() == Hesperides.Hints.NULL) return null;
			
			instance = node.getType().newInstance();
			
			for (Node child:node) {
				instance.add( TransformerRegistry.getInstance().get(getValueGenericType()).transform(child) );
			}
			
		} catch (InstantiationException | IllegalAccessException e) {
			throw new TransformationException(e);
		}
		
		
		return instance;
		
	}

	@Override
	public List<Class> getGenericTypes() {
		// TODO Auto-generated method stub
		return this.genericTypes;
	}

	@Override
	public void addGenericType(Class clazz) {
		this.genericTypes.add(clazz);
	}

}
