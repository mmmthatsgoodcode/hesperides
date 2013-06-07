package com.mmmthatsgoodcode.hesperides.cassify.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.commons.lang.StringUtils;

import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Serializer;

public class HesperidesColumn {

	private List<AbstractType> nameComponents = new ArrayList<AbstractType>();
	private AbstractType value = new NullValue();
	private Date created = new Date();
	private int ttl = 0;
	
	public abstract static class AbstractType<T> {
		
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
		
		public Serializer<T> getSerializer() {	
			return AbstractSerializer.infer(getValue());
			
		}

		public abstract int getHint();
		
		public static final AbstractType infer(Object object) throws IllegalArgumentException {
						
			if (object == null) return new NullValue();
			if (String.class.isAssignableFrom(object.getClass())) return new StringValue((String) object);
			if (Integer.class.isAssignableFrom(object.getClass())) return new IntegerValue((Integer) object);
			if (Float.class.isAssignableFrom(object.getClass())) return new FloatValue((Float) object);
			if (Long.class.isAssignableFrom(object.getClass())) return new LongValue((Long) object);
			if (String.class.isAssignableFrom(object.getClass())) return new StringValue((String) object);
			if (Boolean.class.isAssignableFrom(object.getClass())) return new BooleanValue((Boolean) object);
			if (ByteBuffer.class.isAssignableFrom(object.getClass())) return new ByteValue((ByteBuffer) object);

			if (Date.class.isAssignableFrom(object.getClass())) return new DateValue((Date) object);

			throw new IllegalArgumentException();
			
		}
		
	}

	public static class NullValue extends AbstractType {

		public void setValue(Object value) {
			
		}

		@Override
		public int getHint() {
			return Hesperides.Hints.NULL;
		}
		
		
		
		public boolean equals(Object object) {
			
			if (!(object instanceof NullValue)) return false;
			NullValue other = (NullValue) object;
			
			return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
			
		}

		
	}
	
	public static class StringValue extends AbstractType<String> {

		public StringValue(String value) {
			setValue(value);
		}

		@Override
		public int getHint() {
			return Hesperides.Hints.STRING;
		}
		
		public boolean equals(Object object) {
			
			if (!(object instanceof StringValue)) return false;
			StringValue other = (StringValue) object;
			
			return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
			
		}

		
	}
	
	public static class DateValue extends AbstractType<Date> {

		public DateValue(Date value) {
			setValue(value);
		}

		@Override
		public int getHint() {
			return Hesperides.Hints.DATE;
		}
			
		public boolean equals(Object object) {
			
			if (!(object instanceof DateValue)) return false;
			DateValue other = (DateValue) object;
			
			return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
			
		}

	
	}
	
	public static class IntegerValue extends AbstractType<Integer> {

		public IntegerValue(Integer value) {
			setValue(value);
		}

		@Override
		public int getHint() {
			return Hesperides.Hints.INT;
		}
		
		public boolean equals(Object object) {
			
			if (!(object instanceof IntegerValue)) return false;
			IntegerValue other = (IntegerValue) object;
			
			return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
			
		}

		
	}

	public static class FloatValue extends AbstractType<Float> {

		public FloatValue(Float value) {
			setValue(value);
		}

		@Override
		public int getHint() {
			return Hesperides.Hints.FLOAT;
		}
		
		public boolean equals(Object object) {
			
			if (!(object instanceof FloatValue)) return false;
			FloatValue other = (FloatValue) object;
			
			return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
			
		}

	}
	
	public static class LongValue extends AbstractType<Long> {

		public LongValue(Long value) {
			setValue(value);
		}

		@Override
		public int getHint() {
			return Hesperides.Hints.LONG;
		}		
		
		public boolean equals(Object object) {
			
			if (!(object instanceof LongValue)) return false;
			LongValue other = (LongValue) object;
			
			return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
			
		}

		
	}
	
	public static class BooleanValue extends AbstractType<Boolean> {

		public BooleanValue(Boolean value) {
			setValue(value);
		}

		@Override
		public int getHint() {
			return Hesperides.Hints.BOOLEAN;
		}	
		
		
		public boolean equals(Object object) {
			
			if (!(object instanceof BooleanValue)) return false;
			BooleanValue other = (BooleanValue) object;
			
			return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
			
		}

		
	}
	
	public static class ByteValue extends AbstractType<byte[]> {

		public ByteValue(ByteBuffer value) {
			setValue(value.array());
		}
		
		public ByteValue(byte[] value) {
			setValue(value);
		}

		@Override
		public int getHint() {
			return Hesperides.Hints.BYTES;
		}		
		
		public boolean equals(Object object) {
			
			if (!(object instanceof ByteValue)) return false;
			ByteValue other = (ByteValue) object;
			
			return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
			
		}
		
	}
	
	public HesperidesColumn() {

	}
	
	public void addNameComponent(String value) {
		this.nameComponents.add(new StringValue(value));
	}
	
	public void addNameComponent(Date value) {
		this.nameComponents.add(new DateValue(value));
	}

	public void addNameComponent(Integer value) {
		this.nameComponents.add(new IntegerValue(value));
	}
	
	public void addNameComponent(Float value) {
		this.nameComponents.add(new FloatValue(value));
	}
	
	public void addNameComponent(Long value) {
		this.nameComponents.add(new LongValue(value));
	}
	
	public void addNameComponent(Boolean value) {
		this.nameComponents.add(new BooleanValue(value));
	}
	
	public void addNameComponent(AbstractType component) {
		this.nameComponents.add(component);
	}
	
	/**
	 * This is a placeholder component
	 */
	public void addNullNameComponent() {
		this.nameComponents.add(new NullValue());
	}
	
	/**
	 * This assumes that the incoming components are off a valid HesperidesColumn
	 * I.e. it does not enforce types to match one of the above setters' argument types
	 * @param components
	 */
	public void addNameComponents(Collection<? extends AbstractType> components) {
		this.nameComponents.addAll(components);
	}
	
	public List<AbstractType> getNameComponents() {
		return this.nameComponents;
	}

	public void setValue(String value) {
		this.value = new StringValue(value);
	}
	
	public void setValue(Date value) {
		this.value = new DateValue(value);
	}

	public void setValue(Integer value) {
		this.value = new IntegerValue(value);
	}
	
	public void setValue(Float value) {
		this.value = new FloatValue(value);
	}
	
	public void setValue(Long value) {
		this.value = new LongValue(value);
	}
	
	public void setValue(Boolean value) {
		this.value = new BooleanValue(value);
	}
	
	public void setValue(byte[] value) {
		this.value = new ByteValue(value);
	}
	
	public void setNullValue() {
		this.value = new NullValue();
	}
	
	public AbstractType getValue() {
		return this.value;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public Date getCreated() {
		return this.created;
	}
	
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	
	public int getTtl() {
		return this.ttl;
	}


	public boolean equals(Object object) {
		
		if (!(object instanceof HesperidesColumn)) return false;
		
		HesperidesColumn other = (HesperidesColumn) object;
		
		return this.getNameComponents().equals(other.getNameComponents())
				&& this.getValue().equals(other.getValue());
		
	}
	
	public String toString() {
		String out = StringUtils.join(nameComponents.toArray(), " -> ");
		out += " = "+this.value.toString();
		
		return out;
		
	}

	
}
