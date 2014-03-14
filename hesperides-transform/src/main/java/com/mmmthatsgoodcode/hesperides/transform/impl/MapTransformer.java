package com.mmmthatsgoodcode.hesperides.transform.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.GenericTransformer;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.type.NullValue;
import com.mmmthatsgoodcode.hesperides.transform.TransformerRegistry;

public class MapTransformer<T extends Map> implements GenericTransformer<T> {

	private static final Logger LOG = LoggerFactory.getLogger(MapTransformer.class);
	private List<Class> genericTypes = new ArrayList<Class>();
	
	private Class<? extends Object> keyType = Object.class, valueType = Object.class;
	
	
	public Class<? extends Object> getKeyGenericType() {
		return this.genericTypes.get(0);
	}
	
	public Class<? extends Object> getValueGenericType() {
		return this.genericTypes.get(1);
	}	
	
	public Node.Builder transform(T map) throws TransformationException {
		
		LOG.trace("Transforming map {},<{}> to Node", map.getClass(), StringUtils.join(this.genericTypes, ", "));
		
		Node.Builder mapNode = new NodeImpl.Builder();
		
		if (map == null) {
			mapNode.setValue(new NullValue());
			return mapNode;
		}
		
		mapNode.setRepresentedType(map.getClass());

		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			
			Node.Builder childNode = TransformerRegistry.getInstance().get(getValueGenericType()).transform(entry.getValue()) ;

			LOG.debug("Transformed value {}", childNode);
			
			childNode.setName( AbstractType.wrap( entry.getKey() ) );
			childNode.setRepresentedType(getValueGenericType());
			
			mapNode.addChild(childNode);
		}
		
		return mapNode;
		
	}
	
	@SuppressWarnings("unchecked")
	public T transform(Node<?, ?> node) throws TransformationException {
		
		T instance = null;
		
		try {
			
			if (node.getValue() == null || node.getValue().equals(new NullValue())) return null;
			
			instance = (T) node.getRepresentedType().newInstance();
			for (Node child:node) {
				instance.put(child.getName(), TransformerRegistry.getInstance().get(child.getRepresentedType()).transform(child));
			}
			
		} catch (InstantiationException | IllegalAccessException e) {
			throw new TransformationException(e);
		}

		
		return instance;
		
	}

	@Override
	public List<Class> getGenericTypes() {
		return genericTypes;
	}

	@Override
	public void addGenericType(Class clazz) {
		genericTypes.add(clazz);
	}
	
}
