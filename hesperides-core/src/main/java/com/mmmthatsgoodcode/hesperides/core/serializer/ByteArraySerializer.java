package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Serializer;
import com.mmmthatsgoodcode.hesperides.core.type.ByteArrayValue;

public class ByteArraySerializer extends AbstractSerializer<byte[]> {

	@Override
	public AbstractType<byte[]> fromByteBuffer(ByteBuffer byteBuffer) {
		return new ByteArrayValue(byteBuffer.array());
	}

	@Override
	public ByteBuffer toByteBuffer(AbstractType<byte[]> object) {
		return ByteBuffer.wrap(object.getValue());
	}

}
