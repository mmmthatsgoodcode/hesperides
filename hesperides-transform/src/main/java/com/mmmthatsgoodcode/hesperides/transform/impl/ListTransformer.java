package com.mmmthatsgoodcode.hesperides.transform.impl;

import java.util.List;

import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.transform.TransformationException;
import com.mmmthatsgoodcode.hesperides.transform.Transformer;
import com.mmmthatsgoodcode.hesperides.transform.TransformerRegistry;

public class ListTransformer<T extends List> implements Transformer<T> {

	@Override
	public Node serialize(T object) throws TransformationException {
		Node listNode = new NodeImpl();
		listNode.setType(object.getClass());
		
		for(Object child:((T) object)) {
			Node childNode = TransformerRegistry.getInstance().get(child.getClass()).serialize(child);
			listNode.addChild(childNode);
		}
		
		return listNode;
		
	}

	@Override
	public T deserialize(Node<? extends Object, T> node) throws TransformationException {
		
		T instance = null;
		try {
			instance = node.getType().newInstance();
			
			for (Node child:node) {
				instance.add( TransformerRegistry.getInstance().get(child.getClass()).deserialize(child) );
			}
			
		} catch (InstantiationException | IllegalAccessException e) {
			throw new TransformationException(e);
		}
		
		
		return instance;
		
	}

}
