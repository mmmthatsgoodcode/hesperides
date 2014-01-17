package com.mmmthatsgoodcode.hesperides.cassify.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

public class HesperidesRow {

	private final byte[] key;
	private Map<List<HesperidesColumn.AbstractType>, HesperidesColumn> columns = new HashMap<List<HesperidesColumn.AbstractType>, HesperidesColumn>();
	
	public HesperidesRow(byte[] key) {
		this.key = key;
	}
	
	public HesperidesRow(byte[] key, List<HesperidesColumn> columns) {
		this(key);
		addColumns(columns);
	}
	
	public HesperidesRow(byte[] key, HesperidesColumn...columns) {
	    this(key, new ArrayList<HesperidesColumn>(Arrays.asList(columns)));
	}
	
	public void addColumn(HesperidesColumn column) {
		this.columns.put(column.getNameComponents(), column);
	}
	
	public void addColumns(Collection<HesperidesColumn> columns) {
	    for (HesperidesColumn column:columns) {
		this.columns.put(column.getNameComponents(), column);
	    }
	}
	
	public List<HesperidesColumn> getColumns() {
		return new ArrayList<HesperidesColumn>(this.columns.values());
	}
	
	public byte[] getKey() {
		return this.key;
	}
	
	public String toString() {
		return "Row key: "+this.getKey()+", ("+columns.size()+") Columns:\n"+StringUtils.join(columns.values(), "\n");
	}
	
	public boolean equals(Object object) {
		
		if (!(object instanceof HesperidesRow)) return false;
		HesperidesRow other = (HesperidesRow) object;
		
		if (Arrays.equals(getKey(), other.getKey()) == false || getColumns().size() != other.getColumns().size()) return false;
		
		for(HesperidesColumn column:getColumns()) {
		    if (other.getColumns().contains(column) == false) return false;
		}
		
		return true;
	}
	
}
