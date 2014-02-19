package com.mmmthatsgoodcode.hesperides.cassify.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.commons.lang.StringUtils;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Serializer;

public class HesperidesColumn {

    	private final static HashFunction HASH_FUNCTION = Hashing.murmur3_32();
	private List<AbstractType> nameComponents = new ArrayList<AbstractType>();
	private AbstractType value = new NullValue();
	private Date created = new Date();
	private boolean indexed = false;
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
		
		@Override
		public boolean equals(Object object) {
		    if (!(object instanceof AbstractType)) return false;
		    AbstractType other = (AbstractType) object;
		    
		    return getValue().equals(other.getValue());
		}
		
		@Override
		public int hashCode() {
		    return getValue().hashCode();
		}
		
	}

	public static class WildcardValue extends NullValue {

		
		@Override
		public int getHint() {
			return Hesperides.Hints.WILDCARD;
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
	
	public static class ShortValue extends AbstractType<Short> {

		public ShortValue(Short value) {
			setValue(value);
		}

		@Override
		public int getHint() {
			return Hesperides.Hints.LONG;
		}		
		
		public boolean equals(Object object) {
			
			if (!(object instanceof ShortValue)) return false;
			ShortValue other = (ShortValue) object;
			
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
	
	public HesperidesColumn addNameComponent(String value) {
		return addNameComponent(new StringValue(value));
	}


	public HesperidesColumn addNameComponent(Integer value) {
	    return addNameComponent(new IntegerValue(value));
	}
	
	public HesperidesColumn addNameComponent(Float value) {
	    return addNameComponent(new FloatValue(value));
	}
	
	public HesperidesColumn addNameComponent(Long value) {
	    return addNameComponent(new LongValue(value));
	}
	
	public HesperidesColumn addNameComponent(Boolean value) {
	    return addNameComponent(new BooleanValue(value));
	}
	
	public HesperidesColumn addNameComponent(AbstractType component) {
	    nameComponents.add(component);
	    return this;
	}
	/**
	 * This assumes that the incoming components are off a valid HesperidesColumn
	 * I.e. it does not enforce types to match one of the above setters' argument types
	 * @param components
	 */
	public HesperidesColumn addNameComponents(Collection<? extends AbstractType> components) {
		this.nameComponents.addAll(components);
		return this;
	}
	
	public List<AbstractType> getNameComponents() {
		return this.nameComponents;
	}

	public HesperidesColumn setValue(String value) {
		this.value = new StringValue(value);
		return this;
	}
	
	public HesperidesColumn setValue(Date value) {
		this.value = new DateValue(value);
		return this;
	}

	public HesperidesColumn setValue(Integer value) {
		this.value = new IntegerValue(value);
		return this;
	}
	
	public HesperidesColumn setValue(Float value) {
		this.value = new FloatValue(value);
		return this;
	}
	
	public HesperidesColumn setValue(Long value) {
		this.value = new LongValue(value);
		return this;
	}
	
	public HesperidesColumn setValue(Short value) {
		this.value = new ShortValue(value);
		return this;
	}
	
	public HesperidesColumn setValue(Boolean value) {
		this.value = new BooleanValue(value);
		return this;
	}
	
	public HesperidesColumn setValue(byte[] value) {
		this.value = new ByteValue(value);
		return this;
	}
	
	public HesperidesColumn setNullValue() {
		this.value = new NullValue();
		return this;
	}
	
	public AbstractType getValue() {
		return this.value;
	}
	
	public HesperidesColumn setCreated(Date created) {
		this.created = created;
		return this;
	}
	
	public Date getCreated() {
		return this.created;
	}
	
	public HesperidesColumn setTtl(int ttl) {
		this.ttl = ttl;
		return this;
	}
	
	public int getTtl() {
		return this.ttl;
	}

	public boolean isIndexed() {
	    return indexed;
	}

	public HesperidesColumn setIndexed(boolean indexed) {
	    this.indexed = indexed;
		return this;
	}
	
	public boolean equals(Object object) {
		
		if (!(object instanceof HesperidesColumn)) return false;
		
		HesperidesColumn other = (HesperidesColumn) object;
		
		return this.getNameComponents().equals(other.getNameComponents())
				&& this.getValue().equals(other.getValue());
		
	}
	
	public int hashCode() {
	    
	    Hasher hasher = HASH_FUNCTION.newHasher();
	    for (AbstractType nameComponent:getNameComponents()) {
		hasher.putInt(nameComponent.hashCode());
	    }
	    
	    hasher.putInt(getValue().hashCode());
	    
	    return hasher.hash().asInt();
	    
	}
	
	public String toString() {
		String out = (isIndexed()?"i ":"")+"(@ "+getCreated().getTime()+") "+StringUtils.join(nameComponents.toArray(), " -> ");
		out += " = "+this.value.toString();
		
		return out;
		
	}


	
}
