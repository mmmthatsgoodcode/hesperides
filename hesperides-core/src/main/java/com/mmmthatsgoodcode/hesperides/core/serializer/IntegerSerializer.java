package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import org.apache.cassandra.utils.ByteBufferUtil;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.type.IntegerValue;

public class IntegerSerializer extends AbstractSerializer<Integer> {

	@Override
	public AbstractType<Integer> fromByteBuffer(ByteBuffer byteBuffer) {
		return new IntegerValue( byteBuffer.asIntBuffer().get() );
	}

	@Override
	public ByteBuffer toByteBuffer(AbstractType<Integer> object) {
		return ByteBufferUtil.bytes(object.getValue());
	}

}
