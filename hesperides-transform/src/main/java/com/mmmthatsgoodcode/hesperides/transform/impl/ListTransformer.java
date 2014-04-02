package com.mmmthatsgoodcode.hesperides.transform.impl;

import java.util.ArrayList;
import java.util.List;

import com.mmmthatsgoodcode.hesperides.core.GenericTransformer;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.type.NullValue;
import com.mmmthatsgoodcode.hesperides.transform.TransformerRegistry;

public class ListTransformer<T extends List> implements GenericTransformer<T> {

	private List<Class> genericTypes = new ArrayList<Class>();

	public Class<? extends Object> getValueGenericType() {
		return this.genericTypes.get(0);
	}		
	
	@Override
	public Node.Builder transform(T object) throws TransformationException {
		Node.Builder listNode = new NodeImpl.Builder();
		
		if (object == null) {
			listNode.setValue(new NullValue());
			return listNode;
		}
		
		listNode.setRepresentedType(object.getClass());
		
		for(Object child:((T) object)) {
			Node.Builder childNode = TransformerRegistry.getInstance().get(getValueGenericType()).transform(child);
			listNode.addChild(childNode);
		}
		
		return listNode;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public T transform(Node<?, ?> node) throws TransformationException {
		
		T instance = null;
		try {
			
			if (node.getValue() == null || node.getValue().equals(new NullValue())) return null;
			
			instance = (T) node.getRepresentedType().newInstance();
			
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
