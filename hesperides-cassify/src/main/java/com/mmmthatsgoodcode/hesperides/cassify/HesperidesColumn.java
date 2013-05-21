package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mmmthatsgoodcode.hesperides.core.Hesperides;

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
		
		public boolean equals(Object object) {
			return (this.getClass().isAssignableFrom(object.getClass()) && this.getValue().equals((T) ((AbstractType) object).getValue()));
			
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
	
	public static class ClassValue extends AbstractType<Class<? extends Object>> {
		
		public ClassValue(Class<? extends Object> value) {
			setValue(value);
		}
		
	}
	
	public HesperidesColumn() {
		setValueTypeHintComponent(Hesperides.Hints.NULL); // set default value type hint
	}
	
	public void setValueTypeHintComponent(int valueTypeHint) {
		if (this.components.size() > 1)	this.components.set(this.components.size()-1, new IntegerValue(valueTypeHint));
		else this.components.add(new IntegerValue(valueTypeHint));
	}
	
	public int getValueTypeHintComponent() {
		return ((HesperidesColumn.IntegerValue) this.components.get(this.components.size()-1)).getValue();
	}
	
	public void addComponent(String value) {
		this.components.add(this.components.size()-1, new StringValue(value));
	}
	
	public void addComponent(Date value) {
		this.components.add(this.components.size()-1, new DateValue(value));
	}

	public void addComponent(Integer value) {
		this.components.add(this.components.size()-1, new IntegerValue(value));
	}
	
	public void addComponent(Float value) {
		this.components.add(this.components.size()-1, new FloatValue(value));
	}
	
	public void addComponent(Long value) {
		this.components.add(this.components.size()-1, new LongValue(value));
	}
	
	public void addComponent(Class<? extends Object> value) {
		this.components.add(this.components.size()-1, new ClassValue(value));
	}
	
	/**
	 * This is a placeholder component
	 */
	public void addNullComponent() {
		this.components.add(this.components.size()-1, new NullValue());
	}
	
	/**
	 * This assumes that the incoming components are off a valid HesperidesColumn
	 * I.e. it does not enforce types to match one of the above setters' argument types
	 * @param components
	 */
	public void addComponents(Collection<? extends AbstractType> components) {
		for (AbstractType component:components) {
			this.components.add(this.components.size()-1, component);
		}
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public List<AbstractType> getInheritableComponents() {
		return this.components.subList(0, this.components.size()-1);
	}
	
	public String toString() {
		String out = this.key + " | ";
		out += StringUtils.join(components.toArray(), " -> ");
		out += " = "+this.value.toString();
		
		return out;
		
	}

	
}
