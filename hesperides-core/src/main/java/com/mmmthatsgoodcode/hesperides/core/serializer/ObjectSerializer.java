package com.mmmthatsgoodcode.hesperides.core.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.SerializationException;
import com.mmmthatsgoodcode.hesperides.core.type.ObjectValue;

public class ObjectSerializer extends AbstractSerializer<Object> {

	@Override
	public AbstractType<Object> fromByteBuffer(ByteBuffer byteBuffer) throws SerializationException {
		byte[] bytes = new byte[byteBuffer.remaining()];
		InputStream bytesIn = new ByteArrayInputStream(byteBuffer.array());
		try {
			ObjectInput in = new ObjectInputStream(bytesIn);
			return new ObjectValue(in.readObject());
		} catch (IOException | ClassNotFoundException e) {
			throw new SerializationException(e);
		}
		
	}

	@Override
	public ByteBuffer toByteBuffer(AbstractType<Object> object) throws SerializationException {
		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			ObjectOutput out;
			try {
				out = new ObjectOutputStream(bytes);
				out.writeObject(object);

				return ByteBuffer.wrap(bytes.toByteArray());

			} catch (IOException e) {
				throw new SerializationException(e);
			}
			

	}

}
