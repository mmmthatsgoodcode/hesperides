package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mmmthatsgoodcode.hesperides.core.Hesperides;

public class HesperidesColumn {

	private List<AbstractType> components = new ArrayList<AbstractType>();
	private AbstractType value = new NullValue();
	
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

		public abstract int getHint();
		
		
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
	
	public static class ClassValue extends AbstractType<Class<? extends Object>> {
		
		public ClassValue(Class<? extends Object> value) {
			setValue(value);
		}

		@Override
		public int getHint() {
			return Hesperides.Hints.CLASS;
		}
		
		public boolean equals(Object object) {
			
			if (!(object instanceof ClassValue)) return false;
			ClassValue other = (ClassValue) object;
			
			return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
			
		}
		
	}
	
	public HesperidesColumn() {
//		setValueTypeHintComponent(Hesperides.Hints.NULL); // set default value type hint
	}
	
	public void addComponent(String value) {
		this.components.add(new StringValue(value));
	}
	
	public void addComponent(Date value) {
		this.components.add(new DateValue(value));
	}

	public void addComponent(Integer value) {
		this.components.add(new IntegerValue(value));
	}
	
	public void addComponent(Float value) {
		this.components.add(new FloatValue(value));
	}
	
	public void addComponent(Long value) {
		this.components.add(new LongValue(value));
	}
	
	public void addComponent(Boolean value) {
		this.components.add(new BooleanValue(value));
	}
	
	public void addComponent(Class<? extends Object> value) {
		this.components.add(new ClassValue(value));
	}
	
	/**
	 * This is a placeholder component
	 */
	public void addNullComponent() {
		this.components.add(new NullValue());
	}
	
	/**
	 * This assumes that the incoming components are off a valid HesperidesColumn
	 * I.e. it does not enforce types to match one of the above setters' argument types
	 * @param components
	 */
	public void addComponents(Collection<? extends AbstractType> components) {
		this.components.addAll(components);
	}
	
	public List<AbstractType> getComponents() {
		return this.components;
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

	public boolean equals(Object object) {
		
		if (!(object instanceof HesperidesColumn)) return false;
		
		HesperidesColumn other = (HesperidesColumn) object;
		
		return this.getComponents().equals(other.getComponents())
				&& this.getValue().equals(other.getValue());
		
	}
	
	public String toString() {
		String out = StringUtils.join(components.toArray(), " -> ");
		out += " = "+this.value.toString();
		
		return out;
		
	}

	
}
