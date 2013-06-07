package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import org.apache.cassandra.utils.ByteBufferUtil;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;

public class IntegerSerializer extends AbstractSerializer<Integer> {

	@Override
	public Integer fromByteBuffer(ByteBuffer byteBuffer) {
		return byteBuffer.asIntBuffer().get();
	}

	@Override
	public ByteBuffer toByteBuffer(Integer object) {
		return ByteBufferUtil.bytes(object);
	}

}
