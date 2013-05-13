package com.mmmthatsgoodcode.hesperides.serialize.impl;

import java.lang.reflect.Field;
import java.util.Map;

import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.serialize.Serializer;
import com.mmmthatsgoodcode.hesperides.serialize.SerializerRegistry;


/**
 * A serializer that takes the follwoing Annotations in to account when serializing Objects:
 * - Id
 * - Ignored 
 * - HConstructor
 * @author andras
 *
 * @param <T>
 */
public class AnnotatedObjectSerializer<T> implements Serializer<T> {

	public Node serialize(Class type, T object) {
						
		Node node = new NodeImpl<String, T>();
		node.setType(type);
		
		for (Field field:object.getClass().getFields()) {
			try {
				field.setAccessible(true);

				// see if this is an @Ignore 'd field
				
				// see if this is an @Id field
				
				// this is something we'll have to work with
				Node childNode = new NodeImpl<String, T>();

				
				childNode = SerializerRegistry.getInstance().get(field.getType()).serialize(field.getType(), field.get(object));				
				childNode.setName(Hesperides.Types.STRING, field.getName());
				node.addChild(childNode);

			
			} catch (IllegalArgumentException | IllegalAccessException e) {

			}
		}
		
		return node;
	}

	
	public T deserialize(Node node) {
		
		T instance = null;
		try {
			instance = (T) node.getType().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
		}
		
		return instance;
		
	}

}
