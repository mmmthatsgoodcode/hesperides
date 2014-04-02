package com.mmmthatsgoodcode.hesperides.core.type;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Serializer;
import com.mmmthatsgoodcode.hesperides.core.Hesperides.Hint;
import com.mmmthatsgoodcode.hesperides.core.serializer.LongSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.ShortSerializer;


public class ShortValue extends AbstractType<Short> {

	private static class SerializerHolder {
		private static final Serializer<Short> INSTANCE = new ShortSerializer();
	}
	
	public ShortValue(Short value) {
		setValue(value);
	}

	@Override
	public Hesperides.Hint getHint() {
		return Hesperides.Hint.LONG;
	}		
	
	@Override
	public boolean equals(Object object) {
		
		if (!(object instanceof ShortValue)) return false;
		ShortValue other = (ShortValue) object;
		
		return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
		
	}

	@Override
	public Serializer<Short> getSerializer() {
		return SerializerHolder.INSTANCE;
	}

	
}