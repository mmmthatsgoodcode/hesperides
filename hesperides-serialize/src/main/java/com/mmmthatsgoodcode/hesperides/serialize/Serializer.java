package com.mmmthatsgoodcode.hesperides.serialize;

import com.mmmthatsgoodcode.hesperides.core.Node;

public interface Serializer<T extends Object> {

	public Node serialize(Class type, T object) throws SerializationException;
	public T deserialize(Node<? extends Object, T> node) throws SerializationException;
}
