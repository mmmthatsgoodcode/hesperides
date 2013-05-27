package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.List;
import java.util.Map.Entry;

import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;

public interface Cassifier<T> {

	public HesperidesRow cassify(Entry<String, List<T>> object) throws TransformationException;
	public Entry<String, List<T>> cassify(HesperidesRow row) throws TransformationException;
	
}
