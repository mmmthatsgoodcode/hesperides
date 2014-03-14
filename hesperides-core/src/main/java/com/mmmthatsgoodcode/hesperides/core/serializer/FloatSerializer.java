package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import org.apache.cassandra.utils.ByteBufferUtil;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.type.FloatValue;

public class FloatSerializer extends AbstractSerializer<Float> {

	@Override
	public AbstractType<Float> fromByteBuffer(ByteBuffer byteBuffer) {
		return new FloatValue( byteBuffer.asFloatBuffer().get() );
	}

	@Override
	public ByteBuffer toByteBuffer(AbstractType<Float> object) {
		return ByteBufferUtil.bytes(object.getValue());
	}

}
