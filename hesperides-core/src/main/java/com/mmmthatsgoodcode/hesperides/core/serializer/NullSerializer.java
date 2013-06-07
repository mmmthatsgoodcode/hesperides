package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;

public class NullSerializer extends AbstractSerializer {

	@Override
	public Object fromByteBuffer(ByteBuffer byteBuffer) {
		return null;
	}

	@Override
	public ByteBuffer toByteBuffer(Object object) {
		return ByteBuffer.wrap(new byte[]{0});
	}

}
