package com.mmmthatsgoodcode.hesperides.core.type;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Hesperides.Hint;
import com.mmmthatsgoodcode.hesperides.core.serializer.IntegerSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.ObjectSerializer;
import com.mmmthatsgoodcode.hesperides.core.Serializer;

public class ObjectValue extends AbstractType<Object> {

	private static class SerializerHolder {
		private static final Serializer<Object> INSTANCE = new ObjectSerializer();
	}
	
	public ObjectValue(Object value) {
		setValue(value);
	}
	
	@Override
	public Serializer<Object> getSerializer() {
		return SerializerHolder.INSTANCE;
	}

	@Override
	public Hint getHint() {
		return Hesperides.Hint.OBJECT;
	}

}
