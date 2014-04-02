package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import org.apache.cassandra.utils.ByteBufferUtil;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.type.LongValue;

public class LongSerializer extends AbstractSerializer<Long> {

	@Override
	public AbstractType<Long> fromByteBuffer(ByteBuffer byteBuffer) {
		return new LongValue( byteBuffer.asLongBuffer().get() );
	}

	@Override
	public ByteBuffer toByteBuffer(AbstractType<Long> object) {
		return ByteBufferUtil.bytes(object.getValue());
	}

}
