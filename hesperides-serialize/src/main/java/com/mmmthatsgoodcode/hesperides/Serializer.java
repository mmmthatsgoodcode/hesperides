package com.mmmthatsgoodcode.hesperides;
import com.mmmthatsgoodcode.hesperides.core.Graph;

/**
 * Create an object Graph from an Object via reflection and vica-versa
 * @author andras
 *
 * @param <T>
 */
public interface Serializer<T> {

	public Graph serialize(T o);
	public T deserialize(Graph graph);
	
}
