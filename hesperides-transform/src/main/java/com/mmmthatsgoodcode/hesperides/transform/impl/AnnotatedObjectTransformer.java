package com.mmmthatsgoodcode.hesperides.transform.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.mmmthatsgoodcode.hesperides.annotation.Id;
import com.mmmthatsgoodcode.hesperides.annotation.Ignore;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.Transformer;
import com.mmmthatsgoodcode.hesperides.transform.TransformerRegistry;


/**
 * A serializer that takes the com.mmmthatsgoodcode.hesperides.annotation Annotations in to account when serializing Objects:
 * Note that you'll want to have no-arg constructors on Classes for serialization with this. Otherwise it will do some evil things with Objenesis that might break badly.
 * @author andras
 *
 * @param <T>
 */
public class AnnotatedObjectTransformer<T> implements Transformer<T> {

	private static final Logger LOG = LoggerFactory.getLogger(AnnotatedObjectTransformer.class);
	
	public Node transform(T object) throws TransformationException {
						
		LOG.trace("Transforming object {} to Node", object);
		
		Node node = new NodeImpl<String, T>();
		
		if (object == null) {
			node.setNullValue();
			return node;
		}
		node.setType(object.getClass());

		for (Field field:getAllFields(object.getClass())) {
			try {
				LOG.trace("Looking at field {} with value {}", field.getName(), field.get(object));
				field.setAccessible(true);

				// see if this is an @Ignore 'd field
				if (field.getAnnotation(Ignore.class) == null) {
					
					// this is something we'll have to work with
					Node childNode;
					
					// see if this is an @Id field
					if (field.getAnnotation(Id.class) != null) {
						LOG.trace("Field {} is an @Id field", field.getName());
						int idFieldTypeHint = Hesperides.Hints.typeToHint(field.getType());
						if (idFieldTypeHint == Hesperides.Hints.STRING) node.setName(idFieldTypeHint, field.get(object));
						else throw new TransformationException("Id field can only be String"); // TODO add a constraint to the annotation ?
					}

					childNode = TransformerRegistry.getInstance().get(field).transform(field.get(object));				
					childNode.setName(Hesperides.Hints.STRING, field.getName());
					node.addChild(childNode);
				
				} else {
					LOG.trace("Field {} is an @Ignored field, skipping.", field.getName());
				}

			
			} catch (IllegalArgumentException | IllegalAccessException e) {

			}
		}
		
		return node;
	}

	
	public T transform(Node<? extends Object, T> node) throws TransformationException {
		
		T instance = null;
			try {
				if (node.getHint() == Hesperides.Hints.NULL) return null;
				
				Class type = node.getType();
				LOG.trace("Trasforming Node to an instance of {}", type);
				// see if there is a constructor marked with @HConstructor
				
				// otherwise, fall back to using the no-arg constructor and use reflection to set fields
				
				if (type.isPrimitive()) type = ClassUtils.primitiveToWrapper(type); // convert a primitive to its Class
				
				try {
					ConstructorAccess<T> constructor = ConstructorAccess.get(type);
					instance = constructor.newInstance();
				} catch(RuntimeException e) {
					// ReflectASM failed to instantiate, lets try with skipping the constructor
					Objenesis objenesis = new ObjenesisStd();
					ObjectInstantiator instantiator = objenesis.getInstantiatorOf(type);
					instance = (T) instantiator.newInstance();
				}
				
				// start setting fields
				for(Node fieldNode:node) {
					Class fieldNodeType = fieldNode.getType();
					
					try {
						Field field = type.getField((String) fieldNode.getName());

						field.setAccessible(true);
						
						field.set(instance, TransformerRegistry.getInstance().get(field).transform(fieldNode));
						
					} catch (SecurityException e) {
						throw new TransformationException("SecurityException caught while trying to set field "+fieldNode.getName()+" accessible on "+type.getSimpleName(), e);
					} catch (NoSuchFieldException e) {
						throw new TransformationException("Field "+fieldNode.getName()+" does not exist on "+type.getSimpleName());
					}
					
				}
				
			} catch ( IllegalAccessException e ) {
				throw new TransformationException(e);
			}
		
		
		return instance;
		
	}
	
	private List<Field> getAllFields(Class clazz) {
		
		List<Field> fields = new ArrayList<Field>();
		
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		
		Class superClass = clazz.getSuperclass();
		if (superClass != null) fields.addAll(getAllFields(superClass));
		
		return fields;
		
	}

}
