package com.mmmthatsgoodcode.hesperides.cassify.integration;

import java.util.ArrayList;
import java.util.List;

import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn.BooleanValue;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;

public interface CassandraThriftClientIntegration {

	public static final byte[] INDEX_VALUE_SEPARATOR = ":".getBytes();
	public static final String INDEX_CF_SUFFIX = "-indexes";
	public static final byte[] COMPONENT_DELIMITER = ".".getBytes();
	
	public static final class IndexedRows {
		
		public static List<HesperidesColumn> find(HesperidesRow row) {
			
			List<HesperidesColumn> indexes = new ArrayList<HesperidesColumn>();
			
			for(HesperidesColumn column:row.getColumns()) {
				
				if ( ((BooleanValue) column.getNameComponents().get(column.getNameComponents().size()-1)).getValue() ) indexes.add(column);
				
			}
			
			return indexes;
			
		}
		
	}
	
	public boolean store(String cfName, HesperidesRow row) throws CassandraThriftClientException;
	public List<String> retrieveRowKeysByIndex(String cfName, String indexName, String indexValue, int limit) throws CassandraThriftClientException;
	public HesperidesRow retrieve(String cfName, String rowKey) throws CassandraThriftClientException;
	public HesperidesRow retrieve(String cfName, String rowKey, NodeLocator locator) throws CassandraThriftClientException;
	public HesperidesRow retrieve(String cfName, String indexName, String indexValue, int limit) throws CassandraThriftClientException;
	public HesperidesRow retrieve(String cfName, String indexName, String indexValue, int limit, NodeLocator locator) throws CassandraThriftClientException;
	public HesperidesRow retrieve(String cfName, String indexName, Integer indexValue, int limit) throws CassandraThriftClientException;
	public HesperidesRow retrieve(String cfName, String indexName, Integer indexValue, int limit, NodeLocator locator) throws CassandraThriftClientException;
	
}
