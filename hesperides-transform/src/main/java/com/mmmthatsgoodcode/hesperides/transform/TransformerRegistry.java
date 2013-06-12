package com.mmmthatsgoodcode.hesperides.transform;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mmmthatsgoodcode.hesperides.core.GenericTransformer;
import com.mmmthatsgoodcode.hesperides.core.Transformer;
import com.mmmthatsgoodcode.hesperides.transform.impl.AnnotatedObjectTransformer;
import com.mmmthatsgoodcode.hesperides.transform.impl.ByteBufferTransformer;
import com.mmmthatsgoodcode.hesperides.transform.impl.ListTransformer;
import com.mmmthatsgoodcode.hesperides.transform.impl.MapTransformer;
import com.mmmthatsgoodcode.hesperides.transform.impl.PrimitiveTransformer;

public class TransformerRegistry {

	public static final Transformer DEFAULT_SERIALIZER = new AnnotatedObjectTransformer();
	private ConcurrentHashMap<Class<? extends Object>, Transformer> serializers = new ConcurrentHashMap<Class<? extends Object>, Transformer>();
	private ConcurrentHashMap<Field, Transformer> fieldSpecificSerializers = new ConcurrentHashMap<Field, Transformer>();
	private ConcurrentHashMap<String, Transformer> fieldNameSpecificSerializers = new ConcurrentHashMap<String, Transformer>();
	
	/**
	 * Create the TransformerRegistry with some default transformers
	 */
	private TransformerRegistry() {

		register(new ListTransformer(), List.class);
		register(new MapTransformer(), Map.class);
		register(ByteBuffer.class, new ByteBufferTransformer());
		register(new PrimitiveTransformer<Integer>(), Integer.TYPE, Integer.class);
		register(new PrimitiveTransformer<Float>(), Float.TYPE, Float.class);
		register(new PrimitiveTransformer<Long>(), Long.TYPE, Long.class);
		register(new PrimitiveTransformer<Boolean>(), Boolean.TYPE, Boolean.class);
		register(new PrimitiveTransformer<String>(), String.class);

	}

	private static class SerializerRegistryHolder {
		public static final TransformerRegistry INSTANCE = new TransformerRegistry();
	}

	public static TransformerRegistry getInstance() {
		return SerializerRegistryHolder.INSTANCE;
	}

	
	public void register(Class<? extends Object> type, Transformer serializer) {
		this.serializers.put(type, serializer);
	}
	
	/**
	 * Register a Transformer specifically for a field. This is to reduce the overhead in having to create custom Transformers for types with generic-type fields
	 * @param field Field for which the provided Transformer will be
	 * @param serializer
	 */
	public void register(Field field, Transformer serializer) {
		this.fieldSpecificSerializers.put(field, serializer);
	}
	
	public void register(String fieldName, Transformer serializer) {
		this.fieldNameSpecificSerializers.put(fieldName, serializer);
	}
	
	public void register(Transformer serializer, Class<? extends Object>... classes) {
		for (Class<? extends Object> type:classes) {
			register(type, serializer);
		}
	}
	
	public void register(Transformer serializer, Field... fields) {
		for (Field field:fields) {
			register(field, serializer);
		}
	}
	
	public void register(Transformer serializer, String... fieldNames) {
		for (String fieldName:fieldNames) {
			register(fieldName, serializer);
		}
	}
	
	public void register(Class[] genericTypes, Field... fields) throws RegisteredTransformerNotGenericException {
		
		for (Field field:fields) {
			
			GenericTransformer baseTransformer = getFirstGenericTransformer(field);
			try {
				GenericTransformer transformer = baseTransformer.getClass().newInstance();
				for (Class genericType:genericTypes) {
					transformer.addGenericType(genericType);
				}
				
				register(transformer, field);
			} catch (InstantiationException | IllegalAccessException e) {
				// could not instantiate transformer ?
			}
			
			
		}
		
	}
	

	public Transformer get(Class<? extends Object> type) {
		if (this.serializers.containsKey(type)) {
			return this.serializers.get(type);
		}

		return TransformerRegistry.DEFAULT_SERIALIZER;
	}
	
	public Transformer get(Field field) {
		Transformer transformer = null;
		if (this.fieldSpecificSerializers.containsKey(field)) {
			transformer = this.fieldSpecificSerializers.get(field);
		} else {
			transformer = get(field.getType());
		}
		return transformer;
	}
	
	public GenericTransformer getFirstGenericTransformer(Field field) throws RegisteredTransformerNotGenericException {
		Transformer transformer = null;
		if (this.fieldSpecificSerializers.containsKey(field)) transformer = this.fieldSpecificSerializers.get(field);
		else transformer = get(field.getType());
		
		if (!GenericTransformer.class.isAssignableFrom(transformer.getClass())) throw new RegisteredTransformerNotGenericException("Could not find a registered GenericTransformer for Field "+field+" or type "+field.getType());
		return (GenericTransformer) transformer;
		
	}

}
