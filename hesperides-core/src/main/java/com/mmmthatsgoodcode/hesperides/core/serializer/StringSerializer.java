package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;

public class StringSerializer extends AbstractSerializer<String> {

	@Override
	public String fromByteBuffer(ByteBuffer byteBuffer) {
		return new String(byteBuffer.array(), Charset.forName("UTF-8"));
	}

	@Override
	public ByteBuffer toByteBuffer(String object) {
		return ByteBuffer.wrap( object.getBytes(Charset.forName("UTF-8")) );
	}

}
