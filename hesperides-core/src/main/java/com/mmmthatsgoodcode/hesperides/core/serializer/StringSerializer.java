package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.type.StringValue;

public class StringSerializer extends AbstractSerializer<String> {

	@Override
	public AbstractType<String> fromByteBuffer(ByteBuffer byteBuffer) {
		byte[] stringBytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(stringBytes);
		return new StringValue( new String(stringBytes, Charset.forName("UTF-8")) );
	}

	@Override
	public ByteBuffer toByteBuffer(AbstractType<String> object) {
		return ByteBuffer.wrap( object.getValue().getBytes(Charset.forName("UTF-8")) );
	}

}
