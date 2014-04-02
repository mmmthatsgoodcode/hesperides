package com.mmmthatsgoodcode.hesperides.core;

import java.nio.ByteBuffer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;

public interface Serializer<T> {

	public AbstractType<T> fromByteBuffer(ByteBuffer byteBuffer) throws SerializationException;
	public ByteBuffer toByteBuffer(AbstractType<T> object) throws SerializationException;
	public ByteBuffer toByteBufferWithHint(AbstractType<T> object) throws SerializationException;
	
}
