package com.mmmthatsgoodcode.hesperides.transform;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

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
	
	private TransformerRegistry() {

		register(new ListTransformer<ArrayList>(), ArrayList.class);
//		register(new MapSerializer<HashMap>(), HashMap.class);
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
	
	public void register(Field field, Transformer serializer) {
		this.fieldSpecificSerializers.put(field, serializer);
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

	public Transformer get(Class<? extends Object> type) {
		if (this.serializers.containsKey(type)) {
			return this.serializers.get(type);
		}

		return TransformerRegistry.DEFAULT_SERIALIZER;
	}
	
	public Transformer get(Field field) {
		Transformer serializer = null;
		if (this.fieldSpecificSerializers.containsKey(field)) {
			serializer = this.fieldSpecificSerializers.get(field);
		} else {
			serializer = get(field.getType());
		}
		return serializer;
	}

}
