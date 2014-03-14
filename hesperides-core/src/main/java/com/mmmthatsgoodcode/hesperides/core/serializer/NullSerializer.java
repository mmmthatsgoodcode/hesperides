package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.type.NullValue;

public class NullSerializer extends AbstractSerializer {

	@Override
	public NullValue fromByteBuffer(ByteBuffer byteBuffer) {
		return new NullValue();
	}

	@Override
	public ByteBuffer toByteBuffer(AbstractType object) {
		return ByteBuffer.wrap(new byte[]{0});
	}

}
