package com.mmmthatsgoodcode.hesperides.core.type;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Serializer;
import com.mmmthatsgoodcode.hesperides.core.Hesperides.Hint;
import com.mmmthatsgoodcode.hesperides.core.serializer.LongSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.NullSerializer;


public class NullValue extends AbstractType {

	private static class SerializerHolder {
		private static final Serializer INSTANCE = new NullSerializer();
	}	
	
	public void setValue(Object value) {
		
	}

	@Override
	public Hesperides.Hint getHint() {
		return Hesperides.Hint.NULL;
	}
	
	@Override
	public boolean equals(Object object) {
		
		if (!(object instanceof NullValue)) return false;
		NullValue other = (NullValue) object;
		
		return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
		
	}

	@Override
	public Serializer getSerializer() {
		return SerializerHolder.INSTANCE;
	}
	

	
}