package com.mmmthatsgoodcode.hesperides.core;

import java.util.List;

public interface GenericTransformer<T> extends Transformer<T> {

	public List<Class> getGenericTypes();
	public void addGenericType(Class clazz);
	
}
