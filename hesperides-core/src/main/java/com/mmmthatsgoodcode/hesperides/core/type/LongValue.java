package com.mmmthatsgoodcode.hesperides.core.type;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Serializer;
import com.mmmthatsgoodcode.hesperides.core.Hesperides.Hint;
import com.mmmthatsgoodcode.hesperides.core.serializer.ByteArraySerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.LongSerializer;


public class LongValue extends AbstractType<Long> {

	private static class SerializerHolder {
		private static final Serializer<Long> INSTANCE = new LongSerializer();
	}
	
	public LongValue(Long value) {
		setValue(value);
	}

	@Override
	public Hesperides.Hint getHint() {
		return Hesperides.Hint.LONG;
	}		
	
	@Override
	public boolean equals(Object object) {
		
		if (!(object instanceof LongValue)) return false;
		LongValue other = (LongValue) object;
		
		return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
		
	}

	@Override
	public Serializer<Long> getSerializer() {
		return SerializerHolder.INSTANCE;
	}

	
}