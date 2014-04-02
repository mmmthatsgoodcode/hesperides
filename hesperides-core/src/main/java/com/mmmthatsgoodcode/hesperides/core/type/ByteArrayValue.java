package com.mmmthatsgoodcode.hesperides.core.type;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Serializer;
import com.mmmthatsgoodcode.hesperides.core.Hesperides.Hint;
import com.mmmthatsgoodcode.hesperides.core.serializer.BooleanSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.ByteArraySerializer;

public class ByteArrayValue extends AbstractType<byte[]> {

	private static class SerializerHolder {
		private static final Serializer<byte[]> INSTANCE = new ByteArraySerializer();
	}
	
	public ByteArrayValue(ByteBuffer value) {
		byte[] arrayValue = new byte[value.remaining()];
		value.get(arrayValue, 0, value.remaining());
		setValue(arrayValue);
	}
	
	public ByteArrayValue(byte[] value) {
		setValue(value);
	}

	@Override
	public Hesperides.Hint getHint() {
		return Hesperides.Hint.BYTES;
	}		
	
	@Override
	public boolean equals(Object object) {
		
		if (!(object instanceof ByteArrayValue)) return false;
		ByteArrayValue other = (ByteArrayValue) object;
		
		return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
		
	}
	
	@Override
	public int hashCode() {
		
		return Arrays.hashCode(this.getValue());
		
	}

	@Override
	public Serializer<byte[]> getSerializer() {
		return SerializerHolder.INSTANCE;
	}
	
}