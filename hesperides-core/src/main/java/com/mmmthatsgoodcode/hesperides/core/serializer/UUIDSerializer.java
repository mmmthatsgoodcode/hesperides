package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Serializer;
import com.mmmthatsgoodcode.hesperides.core.type.UUIDValue;

public class UUIDSerializer extends AbstractSerializer<UUID> {

	@Override
	public AbstractType<UUID> fromByteBuffer(ByteBuffer byteBuffer) {
		return new UUIDValue( new UUID(byteBuffer.getLong(), byteBuffer.getLong()) );
	}

	@Override
	public ByteBuffer toByteBuffer(AbstractType<UUID> object) {
		ByteBuffer uuidBytes = ByteBuffer.allocate(16);
		uuidBytes.putLong(object.getValue().getMostSignificantBits());
		uuidBytes.putLong(object.getValue().getLeastSignificantBits());
		return uuidBytes;
	}

}
