package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import com.mmmthatsgoodcode.hesperides.core.Serializer;

public class ByteBufferSerializer implements Serializer<ByteBuffer> {

    @Override
    public ByteBuffer fromByteBuffer(ByteBuffer byteBuffer) {
	return byteBuffer;
    }

    @Override
    public ByteBuffer toByteBuffer(ByteBuffer object) {
	return object;
    }

}
