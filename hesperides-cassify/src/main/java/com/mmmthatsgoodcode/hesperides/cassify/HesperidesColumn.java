package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class HesperidesColumn {

	private String key = "NOKEY";
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
		
		
	}

	public static class NullValue extends AbstractType {

		public void setValue(Object value) {
			
		}
		
		
	}
	
	public static class StringValue extends AbstractType<String> {

		public StringValue(String value) {
			setValue(value);
		}
		
	}
	
	public static class DateValue extends AbstractType<Date> {

		public DateValue(Date value) {
			setValue(value);
		}
			
	
	}
	
	public static class IntegerValue extends AbstractType<Integer> {

		public IntegerValue(Integer value) {
			setValue(value);
		}		
		
	}

	public static class FloatValue extends AbstractType<Float> {

		public FloatValue(Float value) {
			setValue(value);
		}
		
	}
	
	public static class LongValue extends AbstractType<Long> {

		public LongValue(Long value) {
			setValue(value);
		}		
		
	}
	
	public static class BooleanValue extends AbstractType<Boolean> {

		public BooleanValue(Boolean value) {
			setValue(value);
		}		
		
	}
	
	public static class ByteValue extends AbstractType<byte[]> {

		public ByteValue(byte[] value) {
			setValue(value);
		}		
		
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String toString() {
		String out = this.key + " | column: ";
		out += StringUtils.join(components.toArray(), " -> ");
		out += " value: "+this.value.toString();
		
		return out;
		
	}

	
}
