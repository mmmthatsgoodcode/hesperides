package com.mmmthatsgoodcode.hesperides.datastore.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.SerializationException;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.type.BooleanValue;
import com.mmmthatsgoodcode.hesperides.core.type.ByteArrayValue;
import com.mmmthatsgoodcode.hesperides.core.type.DateValue;
import com.mmmthatsgoodcode.hesperides.core.type.FloatValue;
import com.mmmthatsgoodcode.hesperides.core.type.IntegerValue;
import com.mmmthatsgoodcode.hesperides.core.type.LongValue;
import com.mmmthatsgoodcode.hesperides.core.type.NullValue;
import com.mmmthatsgoodcode.hesperides.core.type.ShortValue;
import com.mmmthatsgoodcode.hesperides.core.type.StringValue;

public class HesperidesColumn {

        /**
         * Translate between the Column type of the DataStore Integration and {@link HesperidesColumn}
         * @author andras
         *
         * @param <C> Column Type of the Client Integration
         */
    	public interface Transformer<C> {
    	    
        	public HesperidesRow transform(Entry<AbstractType, Set<C>> object) throws TransformationException, SerializationException;
        	public Entry<AbstractType, Set<C>> transform(HesperidesRow row) throws TransformationException, SerializationException;
	    
    	}
    
	final static Logger LOG = LoggerFactory.getLogger(HesperidesColumn.class);
	private final static HashFunction HASH_FUNCTION = Hashing.murmur3_32();
	private List<AbstractType> nameComponents = new ArrayList<AbstractType>();
	private AbstractType value = new NullValue();
	private Date created = new Date();
	private boolean indexed = false;
	private int ttl = 0;
	
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
	
	public HesperidesColumn setValue(AbstractType value) {
		this.value = value;
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
	
	@Override
	public boolean equals(Object object) {
		
		if (!(object instanceof HesperidesColumn)) return false;
		
		HesperidesColumn other = (HesperidesColumn) object;
		
		return this.getNameComponents().equals(other.getNameComponents())
				&& this.getValue().equals(other.getValue());
		
	}
	
	@Override
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
