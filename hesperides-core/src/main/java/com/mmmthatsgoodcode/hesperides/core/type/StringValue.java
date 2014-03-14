package com.mmmthatsgoodcode.hesperides.core.type;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Hesperides.Hint;
import com.mmmthatsgoodcode.hesperides.core.serializer.LongSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.StringSerializer;
import com.mmmthatsgoodcode.hesperides.core.Serializer;


public class StringValue extends AbstractType<String> {

	private static class SerializerHolder {
		private static final Serializer<String> INSTANCE = new StringSerializer();
	}
	
	public StringValue(String value) {
		setValue(value);
	}

	@Override
	public Hesperides.Hint getHint() {
		return Hesperides.Hint.STRING;
	}
	
	public boolean equals(Object object) {
		
		if (!(object instanceof StringValue)) return false;
		StringValue other = (StringValue) object;
		
		return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
		
	}

	@Override
	public Serializer<String> getSerializer() {
		return SerializerHolder.INSTANCE;
	}

	
}