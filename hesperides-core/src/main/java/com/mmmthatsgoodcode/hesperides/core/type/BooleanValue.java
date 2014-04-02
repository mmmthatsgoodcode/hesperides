package com.mmmthatsgoodcode.hesperides.core.type;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Hesperides.Hint;
import com.mmmthatsgoodcode.hesperides.core.Serializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.BooleanSerializer;


public class BooleanValue extends AbstractType<Boolean> {

	private static class SerializerHolder {
		private static final Serializer<Boolean> INSTANCE = new BooleanSerializer();
	}
	
	public BooleanValue(Boolean value) {
		setValue(value);
	}

	@Override
	public Hesperides.Hint getHint() {
		return Hesperides.Hint.BOOLEAN;
	}	
	
	@Override
	public boolean equals(Object object) {
		
		if (!(object instanceof BooleanValue)) return false;
		BooleanValue other = (BooleanValue) object;
		
		return this.getValue().equals(other.getValue());
		
	}
	
	@Override
	public int hashCode() {
		return getValue().hashCode();
	}

	@Override
	public Serializer<Boolean> getSerializer() {
		return SerializerHolder.INSTANCE;
	}

	
}