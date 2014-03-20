package com.mmmthatsgoodcode.hesperides.core;

import java.nio.ByteBuffer;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.hesperides.core.type.BooleanValue;
import com.mmmthatsgoodcode.hesperides.core.type.ByteArrayValue;
import com.mmmthatsgoodcode.hesperides.core.type.DateValue;
import com.mmmthatsgoodcode.hesperides.core.type.FloatValue;
import com.mmmthatsgoodcode.hesperides.core.type.IntegerValue;
import com.mmmthatsgoodcode.hesperides.core.type.LongValue;
import com.mmmthatsgoodcode.hesperides.core.type.NullValue;
import com.mmmthatsgoodcode.hesperides.core.type.ObjectValue;
import com.mmmthatsgoodcode.hesperides.core.type.StringValue;

public abstract class AbstractType<T> {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractType.class);
	private T value = null;
	
	public T getValue() {
		return this.value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	
	public String toString() {
		if (this.value != null) return this.value.toString();
		return "null";
	}
	
	public abstract Serializer<T> getSerializer();

	public abstract Hesperides.Hint getHint();
	
	public static final AbstractType wrap(Object object) throws IllegalArgumentException {
					
		if (object == null) return new NullValue();
		if (String.class.isAssignableFrom(object.getClass())) return new StringValue((String) object);
		if (Integer.class.isAssignableFrom(object.getClass())) return new IntegerValue((Integer) object);
		if (Float.class.isAssignableFrom(object.getClass())) return new FloatValue((Float) object);
		if (Long.class.isAssignableFrom(object.getClass())) return new LongValue((Long) object);
		if (String.class.isAssignableFrom(object.getClass())) return new StringValue((String) object);
		if (Boolean.class.isAssignableFrom(object.getClass())) return new BooleanValue((Boolean) object);
		if (ByteBuffer.class.isAssignableFrom(object.getClass())) return new ByteArrayValue((ByteBuffer) object);

		if (Date.class.isAssignableFrom(object.getClass())) return new DateValue((Date) object);

		return new ObjectValue(object);
		
	}
	
	public static final AbstractType infer(ByteBuffer bytes) throws SerializationException {
		
		Hesperides.Hint typeHint = Hesperides.Hint.fromStringAlias( new String( new byte[] { bytes.get() } ) );
		return typeHint.serializer().fromByteBuffer(bytes);
		
	}
	
	public Class getRepresentedType() {
		return (getValue()==null?NullValue.class:getValue().getClass());
	}
	
	@Override
	public boolean equals(Object object) {
	    if (!(object instanceof AbstractType)) return false;
	    AbstractType other = (AbstractType) object;
	    
	    return getValue().equals(other.getValue());
	}
	
	@Override
	public int hashCode() {
	    return (getValue()==null?0:getValue().hashCode());
	}
	
}