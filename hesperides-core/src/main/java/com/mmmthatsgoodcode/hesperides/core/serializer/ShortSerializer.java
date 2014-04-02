package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import org.apache.cassandra.utils.ByteBufferUtil;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.type.ShortValue;

public class ShortSerializer extends AbstractSerializer<Short> {

	@Override
	public AbstractType<Short> fromByteBuffer(ByteBuffer byteBuffer) {
		return new ShortValue( byteBuffer.asShortBuffer().get() );
	}

	@Override
	public ByteBuffer toByteBuffer(AbstractType<Short> object) {
		return ByteBufferUtil.bytes(object.getValue());
	}

}
