package com.mmmthatsgoodcode.hesperides.core.type;

import java.util.Date;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Serializer;
import com.mmmthatsgoodcode.hesperides.core.Hesperides.Hint;
import com.mmmthatsgoodcode.hesperides.core.serializer.ByteArraySerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.DateSerializer;

public class DateValue extends AbstractType<Date> {


	private static class SerializerHolder {
		private static final Serializer<Date> INSTANCE = new DateSerializer();
	}
	
	public DateValue(Date value) {
		setValue(value);
	}

	@Override
	public Hesperides.Hint getHint() {
		return Hesperides.Hint.DATE;
	}
		
	@Override
	public boolean equals(Object object) {
		
		if (!(object instanceof DateValue)) return false;
		DateValue other = (DateValue) object;
		
		return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
		
	}
	

	@Override
	public Serializer<Date> getSerializer() {
		return SerializerHolder.INSTANCE;
	}


}