package com.mmmthatsgoodcode.hesperides.core;

import java.nio.ByteBuffer;

import com.mmmthatsgoodcode.hesperides.core.serializer.BooleanSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.FloatSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.IntegerSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.LongSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.NullSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.ShortSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.StringSerializer;

public abstract class AbstractSerializer<T> implements Serializer<T> {

	public static <T> Serializer<T> infer(Object object) {
		
		Serializer serializer = null;
		if (object == null) serializer = new NullSerializer();
		else if (Boolean.class.isAssignableFrom(object.getClass())) serializer = new BooleanSerializer();
		else if (Integer.class.isAssignableFrom(object.getClass())) serializer = new IntegerSerializer();
		else if (Long.class.isAssignableFrom(object.getClass())) serializer = new LongSerializer();
		else if (Float.class.isAssignableFrom(object.getClass())) serializer = new FloatSerializer();
		else if (Short.class.isAssignableFrom(object.getClass())) serializer = new ShortSerializer();
		else if (String.class.isAssignableFrom(object.getClass())) serializer = new StringSerializer();
		else throw new IllegalArgumentException("Could not find Serializer for type "+object.getClass());
		
		return serializer;
		
		
	}

}
