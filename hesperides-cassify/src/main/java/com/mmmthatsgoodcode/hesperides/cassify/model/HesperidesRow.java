package com.mmmthatsgoodcode.hesperides.cassify.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class HesperidesRow {

	private String key = null;
	private List<HesperidesColumn> columns = new ArrayList<HesperidesColumn>();
	
	public HesperidesRow(String key) {
		this.key = key;
	}
	
	public HesperidesRow(String key, List<HesperidesColumn> columns) {
		this(key);
		this.columns = columns;
	}
	
	public void addColumn(HesperidesColumn column) {
		this.columns.add(column);
	}
	
	public void addColumns(Collection<HesperidesColumn> columns) {
		this.columns.addAll(columns);
	}
	
	public List<HesperidesColumn> getColumns() {
		return this.columns;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String toString() {
		return "Row key: "+this.getKey()+", ("+columns.size()+") Columns:\n"+StringUtils.join(columns, "\n");
	}
	
	public boolean equals(Object object) {
		
		if (!(object instanceof HesperidesRow)) return false;
		HesperidesRow other = (HesperidesRow) object;
		
		return getKey().equals(other.getKey())
				&& getColumns().equals(other.getColumns());
		
	}
	
}
