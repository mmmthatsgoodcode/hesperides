package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import org.apache.cassandra.utils.ByteBufferUtil;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.Serializer;

public class BooleanSerializer extends AbstractSerializer<Boolean> {

	@Override
	public Boolean fromByteBuffer(ByteBuffer byteBuffer) {
		if (byteBuffer.get() == (byte)1) return true;
		return false;
	}

	@Override
	public ByteBuffer toByteBuffer(Boolean object) {
		return ByteBuffer.wrap(new byte[]{ (byte) (object==true?1:0) });
	}

}
