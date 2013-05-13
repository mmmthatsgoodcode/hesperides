package com.mmmthatsgoodcode.hesperides.serialize.impl;

import java.util.List;

import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.serialize.Serializer;
import com.mmmthatsgoodcode.hesperides.serialize.SerializerRegistry;

public class ListSerializer implements Serializer<List> {

	@Override
	public Node serialize(Class type, List object) {
		
		Node listNode = new NodeImpl();
		listNode.setType(object.getClass());
		
		for(Object child:((List) object)) {
			Node childNode = SerializerRegistry.getInstance().get(child.getClass()).serialize(child.getClass(), child);
			
		}
		
		return listNode;
		
	}

	@Override
	public List deserialize(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

}
