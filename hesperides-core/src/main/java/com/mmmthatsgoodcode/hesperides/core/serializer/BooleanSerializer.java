package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.type.BooleanValue;

public class BooleanSerializer extends AbstractSerializer<Boolean> {

	@Override
	public AbstractType<Boolean> fromByteBuffer(ByteBuffer byteBuffer) {
		if (byteBuffer.get() == (byte)1) return new BooleanValue(true);
		return new BooleanValue(false);
	}

	@Override
	public ByteBuffer toByteBuffer(AbstractType<Boolean> object) {
		return ByteBuffer.wrap(new byte[]{ (byte) (object.getValue()==true?1:0) });
	}


}
