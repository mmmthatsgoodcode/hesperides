package com.mmmthatsgoodcode.hesperides.core.type;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Serializer;
import com.mmmthatsgoodcode.hesperides.core.Hesperides.Hint;
import com.mmmthatsgoodcode.hesperides.core.serializer.ByteArraySerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.FloatSerializer;


public class FloatValue extends AbstractType<Float> {

	private static class SerializerHolder {
		private static final Serializer<Float> INSTANCE = new FloatSerializer();
	}
	
	public FloatValue(Float value) {
		setValue(value);
	}

	@Override
	public Hesperides.Hint getHint() {
		return Hesperides.Hint.FLOAT;
	}
	
	@Override
	public boolean equals(Object object) {
		
		if (!(object instanceof FloatValue)) return false;
		FloatValue other = (FloatValue) object;
		
		return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
		
	}

	@Override
	public Serializer<Float> getSerializer() {
		return SerializerHolder.INSTANCE;
	}

}