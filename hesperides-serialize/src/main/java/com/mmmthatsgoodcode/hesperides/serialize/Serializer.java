package com.mmmthatsgoodcode.hesperides.serialize;

import com.mmmthatsgoodcode.hesperides.core.Node;

public interface Serializer<T> {

	public Node serialize(Class type, T object);
	public T deserialize(Node node);
}
