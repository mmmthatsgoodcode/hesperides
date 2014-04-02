package com.mmmthatsgoodcode.hesperides.core;

import java.nio.ByteBuffer;
import com.mmmthatsgoodcode.hesperides.core.serializer.BooleanSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.ByteArraySerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.ByteBufferSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.FloatSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.IntegerSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.LongSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.NullSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.ShortSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.StringSerializer;

public abstract class AbstractSerializer<T> implements Serializer<T> {

	public ByteBuffer toByteBufferWithHint(AbstractType<T> object) throws SerializationException {
		ByteBuffer bytesWithoutHint = toByteBuffer(object);
		return ByteBuffer.allocate(bytesWithoutHint.capacity()+1).put( object.getHint().alias().getBytes() ).put(bytesWithoutHint);
	}

}