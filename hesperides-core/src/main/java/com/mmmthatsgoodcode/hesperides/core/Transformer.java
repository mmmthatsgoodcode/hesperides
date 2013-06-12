package com.mmmthatsgoodcode.hesperides.core;

import com.mmmthatsgoodcode.hesperides.core.Node;

public interface Transformer<T extends Object> {

	public Node transform(T object) throws TransformationException;
	public T transform(Node<? extends Object, T> node) throws TransformationException;
}
