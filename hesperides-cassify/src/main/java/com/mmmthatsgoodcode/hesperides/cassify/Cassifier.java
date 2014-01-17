package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.List;
import java.util.Map.Entry;

import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;

public interface Cassifier<T> {

	public HesperidesRow cassify(Entry<byte[], List<T>> object) throws TransformationException;
	public Entry<byte[], List<T>> cassify(HesperidesRow row) throws TransformationException;
	
}
