package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumnSlice;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.SerializationException;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;

/**
 * Translate between the Column format of the Client Integration and {@link HesperidesColumn}
 * @author andras
 *
 * @param <C> Column Type of the Client Integration
 */
public interface Cassifier<C> {

	public HesperidesRow cassify(Entry<AbstractType, Set<C>> object) throws TransformationException, SerializationException;
	public Entry<AbstractType, Set<C>> cassify(HesperidesRow row) throws TransformationException, SerializationException;
	
}
