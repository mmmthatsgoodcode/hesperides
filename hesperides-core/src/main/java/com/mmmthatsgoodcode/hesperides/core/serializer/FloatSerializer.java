package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import org.apache.cassandra.utils.ByteBufferUtil;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;

public class FloatSerializer extends AbstractSerializer<Float> {

	@Override
	public Float fromByteBuffer(ByteBuffer byteBuffer) {
		return byteBuffer.asFloatBuffer().get();
	}

	@Override
	public ByteBuffer toByteBuffer(Float object) {
		return ByteBufferUtil.bytes(object);
	}

}
