package com.mmmthatsgoodcode.hesperides.core.type;

import java.nio.ByteBuffer;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Hesperides.Hint;
import com.mmmthatsgoodcode.hesperides.core.serializer.ByteArraySerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.ByteBufferSerializer;
import com.mmmthatsgoodcode.hesperides.core.Serializer;

public class ByteBufferValue extends AbstractType<ByteBuffer> {

	private static class SerializerHolder {
		private static final Serializer<ByteBuffer> INSTANCE = new ByteBufferSerializer();
	}
	
	public ByteBufferValue(ByteBuffer value) {
		setValue(value);
	}
	
	@Override
	public Serializer<ByteBuffer> getSerializer() {
		return SerializerHolder.INSTANCE;
	}

	@Override
	public Hint getHint() {
		return Hesperides.Hint.BYTES;
	}

}
