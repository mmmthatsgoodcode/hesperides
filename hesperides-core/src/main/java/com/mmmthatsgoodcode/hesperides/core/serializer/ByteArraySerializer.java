package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import com.mmmthatsgoodcode.hesperides.core.Serializer;

public class ByteArraySerializer implements Serializer<byte[]> {

    @Override
    public byte[] fromByteBuffer(ByteBuffer byteBuffer) {
	return byteBuffer.array();
    }

    @Override
    public ByteBuffer toByteBuffer(byte[] object) {
	return ByteBuffer.wrap(object);
    }

}
