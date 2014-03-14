package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Serializer;
import com.mmmthatsgoodcode.hesperides.core.type.StringValue;

public class AsciiSerializer extends AbstractSerializer<String> {

	@Override
	public AbstractType<String> fromByteBuffer(ByteBuffer byteBuffer) {
		byte[] asciiBytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(asciiBytes);
		
		return new StringValue(new String(asciiBytes, Charset.forName("ASCII")));
		
	}

	@Override
	public ByteBuffer toByteBuffer(AbstractType<String> object) {
		return ByteBuffer.wrap(object.getValue().getBytes(Charset.forName("ASCII")));
	}

}
