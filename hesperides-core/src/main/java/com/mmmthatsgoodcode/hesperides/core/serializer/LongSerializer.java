package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import org.apache.cassandra.utils.ByteBufferUtil;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;

public class LongSerializer extends AbstractSerializer<Long> {

	@Override
	public Long fromByteBuffer(ByteBuffer byteBuffer) {
		return byteBuffer.asLongBuffer().get();
	}

	@Override
	public ByteBuffer toByteBuffer(Long object) {
		return ByteBufferUtil.bytes(object);
	}

}
