package com.mmmthatsgoodcode.hesperides.core.type;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Serializer;
import com.mmmthatsgoodcode.hesperides.core.Hesperides.Hint;
import com.mmmthatsgoodcode.hesperides.core.serializer.ByteArraySerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.IntegerSerializer;


public class IntegerValue extends AbstractType<Integer> {

	private static class SerializerHolder {
		private static final Serializer<Integer> INSTANCE = new IntegerSerializer();
	}
	
	public IntegerValue(Integer value) {
		setValue(value);
	}

	@Override
	public Hesperides.Hint getHint() {
		return Hesperides.Hint.INT32;
	}
	
	@Override
	public boolean equals(Object object) {
		
		if (!(object instanceof IntegerValue)) return false;
		IntegerValue other = (IntegerValue) object;
		
		return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
		
	}

	@Override
	public Serializer<Integer> getSerializer() {
		return SerializerHolder.INSTANCE;
	}

	
}