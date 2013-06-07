package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import org.apache.cassandra.utils.ByteBufferUtil;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;

public class ShortSerializer extends AbstractSerializer<Short> {

	@Override
	public Short fromByteBuffer(ByteBuffer byteBuffer) {
		return byteBuffer.asShortBuffer().get();
	}

	@Override
	public ByteBuffer toByteBuffer(Short object) {
		return ByteBufferUtil.bytes(object);
	}

}
