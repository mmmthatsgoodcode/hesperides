package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;
import java.util.Date;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Serializer;
import com.mmmthatsgoodcode.hesperides.core.type.DateValue;

public class DateSerializer extends AbstractSerializer<Date> {

	@Override
	public AbstractType<Date> fromByteBuffer(ByteBuffer byteBuffer) {
		return new DateValue(new Date(byteBuffer.asLongBuffer().get()));
	}

	@Override
	public ByteBuffer toByteBuffer(AbstractType<Date> object) {
		return ByteBuffer.allocate(8).putLong(object.getValue().getTime());
	}

}
