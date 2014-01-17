package com.mmmthatsgoodcode.hesperides.cassify.integration;

import java.util.ArrayList;
import java.util.List;

import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn.BooleanValue;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;

public interface CassandraThriftClientIntegration {

	public static final byte[] INDEX_VALUE_SEPARATOR = ":".getBytes();
	public static final String INDEX_CF_SUFFIX = "_indexes";
	public static final String INDEX_CACHE_CF_SUFFIX = "_indexCache";
	public static final byte[] COMPONENT_DELIMITER = ".".getBytes();
	
	public static final class IndexedRows {
		
		public static List<HesperidesColumn> find(HesperidesRow row) {
			
			List<HesperidesColumn> indexes = new ArrayList<HesperidesColumn>();
			
			for(HesperidesColumn column:row.getColumns()) {
				
				if ( column.isIndexed() ) indexes.add(column);
				
			}
			
			return indexes;
			
		}
		
	}
	
	public void store(String cfName, HesperidesRow row) throws CassandraThriftClientException;
	public List<String> retrieveRowKeysByIndex(String cfName, NodeLocator indexName, byte[] indexValue, int limit) throws CassandraThriftClientException;
	public HesperidesRow retrieve(String cfName, byte[] rowKey) throws CassandraThriftClientException, TransformationException;
	public HesperidesRow retrieve(String cfName, byte[] rowKey, NodeLocator locator) throws CassandraThriftClientException;
	public List<HesperidesRow> retrieve(String cfName, NodeLocator indexName, Object indexValue, int limit) throws CassandraThriftClientException, TransformationException;
	public List<HesperidesRow> retrieve(String cfName, NodeLocator indexName, Object indexValue, int limit, NodeLocator locator) throws CassandraThriftClientException;
	
}
