package com.mmmthatsgoodcode.hesperides.cassify.model;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.utils.Hex;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;

public class HesperidesRow {
	
	private final AbstractType key;
	private Map<List<AbstractType>, HesperidesColumn> columns = new HashMap<List<AbstractType>, HesperidesColumn>();
	
	public HesperidesRow(AbstractType key) {
		this.key = key;
	}
	
	public HesperidesRow(AbstractType key, List<HesperidesColumn> columns) {
		this(key);
		addColumns(columns);
	}
	
	public HesperidesRow(AbstractType key, HesperidesColumn...columns) {
	    this(key, new ArrayList<HesperidesColumn>(Arrays.asList(columns)));
	}
	
	public List<HesperidesColumn> getIndexedColumns() {
		
		List<HesperidesColumn> indexedColumns = new ArrayList<HesperidesColumn>();
		
		for(HesperidesColumn column:getColumns()) {
			if (column.isIndexed()) indexedColumns.add(column);
		}
		
		return indexedColumns;
		
		
	}
	
	public HesperidesRow addColumn(HesperidesColumn column) {
		this.columns.put(column.getNameComponents(), column);
		return this;
	}
	
	public HesperidesRow addColumns(Collection<HesperidesColumn> columns) {
	    for (HesperidesColumn column:columns) {
		this.columns.put(column.getNameComponents(), column);
	    }
	    
	    return this;
	}
	
	public HesperidesColumn getColumn(AbstractType...nameComponents) {
		
		List<AbstractType> nameComponentsList = Arrays.asList(nameComponents);
		for (HesperidesColumn column:getColumns()) {
			
			if (column.getNameComponents().equals(nameComponentsList)) return column;
			
		}
		
		return null;
		
	}
	
	public List<HesperidesColumn> getColumns() {
		return new ArrayList<HesperidesColumn>(this.columns.values());
	}
	
	public AbstractType getKey() {
		return this.key;
	}
	
	public String toString() {
		return "Row key: "+this.getKey()+", ("+columns.size()+") Columns:\n"+StringUtils.join(columns.values(), "\n");
	}
	
	@Override
	public boolean equals(Object object) {
		
		if (!(object instanceof HesperidesRow)) return false;
		HesperidesRow other = (HesperidesRow) object;
		
		if (getKey().equals(other.getKey()) == false || getColumns().size() != other.getColumns().size()) return false;
		
		for(HesperidesColumn column:getColumns()) {
		    if (other.getColumns().contains(column) == false) return false;
		}
		
		return true;
		
	}
	
	@Override
	public int hashCode() {
		
		Long sum = new Long(getKey().hashCode());
		for(HesperidesColumn column:getColumns()) {
			sum += column.hashCode();
		}
		
		return Hashing.murmur3_32().hashLong(sum).asInt();
		
	}
	
	/**
	 * Gets differing rows
	 * @return
	 */
	public Collection<HesperidesColumn> diff(HesperidesRow other) {
	    
	    return CollectionUtils.subtract(getColumns(), other.getColumns());
	    
	}
	
}
