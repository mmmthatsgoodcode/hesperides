package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Serializer;
import com.mmmthatsgoodcode.hesperides.core.type.ByteBufferValue;

public class ByteBufferSerializer extends AbstractSerializer<ByteBuffer> {

    @Override
    public AbstractType<ByteBuffer> fromByteBuffer(ByteBuffer byteBuffer) {
    	return new ByteBufferValue(byteBuffer);
    }

    @Override
    public ByteBuffer toByteBuffer(AbstractType<ByteBuffer> object) {
    	return object.getValue();
    }

}
