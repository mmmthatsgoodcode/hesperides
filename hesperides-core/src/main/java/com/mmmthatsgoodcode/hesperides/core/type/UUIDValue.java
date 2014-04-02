package com.mmmthatsgoodcode.hesperides.core.type;

import java.util.UUID;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Hesperides.Hint;
import com.mmmthatsgoodcode.hesperides.core.serializer.StringSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.UUIDSerializer;
import com.mmmthatsgoodcode.hesperides.core.Serializer;

public class UUIDValue extends AbstractType<UUID> {
	
	private static class SerializerHolder {
		private static final Serializer<UUID> INSTANCE = new UUIDSerializer();
	}
	
	public UUIDValue(UUID value) {
		setValue(value);
	}
	
	@Override
	public Serializer<UUID> getSerializer() {
		return SerializerHolder.INSTANCE;
	}

	@Override
	public Hint getHint() {
		return Hesperides.Hint.UUID;
	}

}
