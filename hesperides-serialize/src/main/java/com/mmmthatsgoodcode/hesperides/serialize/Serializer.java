package com.mmmthatsgoodcode.hesperides.serialize;

import com.mmmthatsgoodcode.hesperides.core.Node;

public interface Serializer<T extends Object> {

	public Node serialize(Class type, T object);
	public T deserialize(Node node);
}
