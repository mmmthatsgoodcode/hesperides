package com.mmmthatsgoodcode.hesperides.serialize;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.mmmthatsgoodcode.hesperides.serialize.impl.AnnotatedObjectSerializer;
import com.mmmthatsgoodcode.hesperides.serialize.impl.ByteBufferSerializer;
import com.mmmthatsgoodcode.hesperides.serialize.impl.ListSerializer;
import com.mmmthatsgoodcode.hesperides.serialize.impl.MapSerializer;
import com.mmmthatsgoodcode.hesperides.serialize.impl.PrimitiveSerializer;

public class SerializerRegistry {

	public static final Serializer DEFAULT_SERIALIZER = new AnnotatedObjectSerializer();
	private ConcurrentHashMap<Class<? extends Object>, Serializer> serializers = new ConcurrentHashMap<Class<? extends Object>, Serializer>();
	private ConcurrentHashMap<Field, Serializer> fieldSpecificSerializers = new ConcurrentHashMap<Field, Serializer>();
	
	private SerializerRegistry() {

		register(new ListSerializer<ArrayList>(), ArrayList.class);
//		register(new MapSerializer<HashMap>(), HashMap.class);
		register(ByteBuffer.class, new ByteBufferSerializer());
		register(new PrimitiveSerializer<Integer>(), Integer.TYPE, Integer.class);
		register(new PrimitiveSerializer<Float>(), Float.TYPE, Float.class);
		register(new PrimitiveSerializer<Long>(), Long.TYPE, Long.class);
		register(new PrimitiveSerializer<Boolean>(), Boolean.TYPE, Boolean.class);
		register(new PrimitiveSerializer<String>(), String.class);

	}

	private static class SerializerRegistryHolder {
		public static final SerializerRegistry INSTANCE = new SerializerRegistry();
	}

	public static SerializerRegistry getInstance() {
		return SerializerRegistryHolder.INSTANCE;
	}

	public void register(Class<? extends Object> type, Serializer serializer) {
		this.serializers.put(type, serializer);
	}
	
	public void register(Field field, Serializer serializer) {
		this.fieldSpecificSerializers.put(field, serializer);
	}
	
	public void register(Serializer serializer, Class<? extends Object>... classes) {
		for (Class<? extends Object> type:classes) {
			register(type, serializer);
		}
	}
	
	public void register(Serializer serializer, Field... fields) {
		for (Field field:fields) {
			register(field, serializer);
		}
	}

	public Serializer get(Class<? extends Object> type) {
		if (this.serializers.containsKey(type)) {
			return this.serializers.get(type);
		}

		return SerializerRegistry.DEFAULT_SERIALIZER;
	}
	
	public Serializer get(Field field) {
		Serializer serializer = null;
		if (this.fieldSpecificSerializers.containsKey(field)) {
			serializer = this.fieldSpecificSerializers.get(field);
		} else {
			serializer = get(field.getType());
		}
		return serializer;
	}

}
