package com.mmmthatsgoodcode.hesperides.core;

import java.nio.ByteBuffer;

public interface Serializer<T> {

	public T fromByteBuffer(ByteBuffer byteBuffer);
	public ByteBuffer toByteBuffer(T object);
	
}
