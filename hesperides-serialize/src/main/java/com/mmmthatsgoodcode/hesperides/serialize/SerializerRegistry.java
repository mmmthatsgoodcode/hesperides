package com.mmmthatsgoodcode.hesperides.serialize;

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

	private SerializerRegistry() {

		register(HashMap.class, new MapSerializer());
		register(ArrayList.class, new ListSerializer());
		register(ByteBuffer.class, new ByteBufferSerializer());
		register(new PrimitiveSerializer(), int.class, Integer.class, String.class, boolean.class, Boolean.class, float.class, Float.class, long.class, Long.class);
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
	
	public void register(Serializer serializer, Class<? extends Object>... classes) {
		for (Class<? extends Object> type:classes) {
			register(type, serializer);
		}
	}

	public Serializer get(Class<? extends Object> type) {
		if (this.serializers.containsKey(type)) {
			return this.serializers.get(type);
		}

		return SerializerRegistry.DEFAULT_SERIALIZER;
	}

}
