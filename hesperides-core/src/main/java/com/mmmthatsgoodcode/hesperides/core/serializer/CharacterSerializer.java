package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Serializer;
import com.mmmthatsgoodcode.hesperides.core.type.CharacterValue;

public class CharacterSerializer extends AbstractSerializer<Character> {

	@Override
	public AbstractType<Character> fromByteBuffer(ByteBuffer byteBuffer) {
		return new CharacterValue(byteBuffer.asCharBuffer().get());
	}

	@Override
	public ByteBuffer toByteBuffer(AbstractType<Character> object) {
		return ByteBuffer.allocate(2).putChar(object.getValue().charValue());
	}

}
