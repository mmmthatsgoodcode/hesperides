package com.mmmthatsgoodcode.hesperides.transform;

import com.mmmthatsgoodcode.hesperides.core.Node;

public interface Transformer<T extends Object> {

	public Node serialize(Class type, T object) throws TransformationException;
	public T deserialize(Node<? extends Object, T> node) throws TransformationException;
}
