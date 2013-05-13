package com.mmmthatsgoodcode.hesperides.serialize.impl;

import java.util.Map;

import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.serialize.Serializer;

public class MapSerializer implements Serializer<Map> {

	public Node serialize(Class type, Map map) {
		
		Node node = new NodeImpl();
		
		
		return node;
		
	}
	
	public Map deserialize(Node node) {
		 
		return null;
		
	}
	
}
