package com.mmmthatsgoodcode.hesperides.serialize;

import com.mmmthatsgoodcode.hesperides.Serializer;
import com.mmmthatsgoodcode.hesperides.core.Graph;

/**
 * A serializer that takes some Annotations in to account when serializing Objects.
 * Uses ReflectASM.
 * @author andras
 *
 * @param <T>
 */
public class AnnotatedObjectSerializer<T> implements Serializer<T> {

	@Override
	public Graph serialize(T o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T deserialize(Graph graph) {
		// TODO Auto-generated method stub
		return null;
	}

}
