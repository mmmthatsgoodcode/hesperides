package com.mmmthatsgoodcode.hesperides.cassify.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumnSlice;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.SerializationException;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;

public interface DataStoreIntegration {

	public static final boolean USE_INDEX_CACHE_CF = Boolean.valueOf( System.getProperty("hesperides.cassandra.indexcache", "false") );
	public static final byte[] INDEX_VALUE_SEPARATOR = ":".getBytes();
	public static final String INDEX_CF_SUFFIX = "_indexes"; // keeps the indexes in a format that allows lookups
	public static final String INDEX_CACHE_CF_SUFFIX = "_indexCache"; // keeps the indexes on a wide row per index name
	public static final String INDEX_RECORDS_CF_SUFFIX = "_indexRecords"; // keeps a list of all indexes for an indexed record ( for cleaning up )
	public static final byte[] COMPONENT_DELIMITER = ".".getBytes();
	
	/**
	 * 
	 * @author andras
	 *
	 */
	public static final class IndexedColumns {
		
		public static List<HesperidesColumn> find(HesperidesRow row) {
			
			List<HesperidesColumn> indexes = new ArrayList<HesperidesColumn>();
			
			for(HesperidesColumn column:row.getColumns()) {
				
				if ( column.isIndexed() ) indexes.add(column);
				
			}
			
			return indexes;
			
		}
		
	}
	
	public void store(String entityName, HesperidesRow row) throws DataStoreIntegrationException, TransformationException, SerializationException;
	public void store(String entityName, List<HesperidesRow> row) throws DataStoreIntegrationException, TransformationException, SerializationException;
	
	/**
	 * Resolve indexed columns to their primary keys
	 * @param entityName
	 * @param indexName
	 * @param indexValue
	 * @param limit
	 * @return
	 * @throws DataStoreIntegrationException
	 * @throws TransformationException
	 */
	public Set<AbstractType> retrieveRowKeysByIndex(String entityName, HesperidesColumnSlice indexName, AbstractType indexValue, int limit) throws DataStoreIntegrationException, TransformationException, SerializationException;
	public Set<AbstractType> retrieveRowKeysByIndexes(String entityName, Multimap<HesperidesColumnSlice, AbstractType> indexes, HesperidesColumnSlice.Relation relation, int limit) throws DataStoreIntegrationException, TransformationException, SerializationException;
	
	/**
	 * Read a full {@link HesperidesRow}
	 * @param entityName
	 * @param rowKey
	 * @return
	 * @throws DataStoreIntegrationException
	 * @throws TransformationException
	 */
	public HesperidesRow retrieve(String entityName, AbstractType rowKey) throws DataStoreIntegrationException, TransformationException, SerializationException;
	
	public HesperidesRow retrieve(String entityName, AbstractType rowKey, List<HesperidesColumn> columns) throws DataStoreIntegrationException, TransformationException, SerializationException;	
	public HesperidesRow retrieveMatching(String entityName, AbstractType rowKey, List<HesperidesColumnSlice> locator) throws DataStoreIntegrationException, TransformationException, SerializationException;
	public Set<HesperidesRow> retrieve(String entityName, HesperidesColumnSlice indexName, AbstractType indexValue, int limit) throws DataStoreIntegrationException, TransformationException, SerializationException;
	public Set<HesperidesRow> retrieve(String entityName, HesperidesColumnSlice indexName, AbstractType indexValue, int limit, List<HesperidesColumnSlice> locator) throws DataStoreIntegrationException, TransformationException, SerializationException;
	public Set<HesperidesRow> retrieve(String entityName, Multimap<HesperidesColumnSlice, AbstractType> indexes, HesperidesColumnSlice.Relation relation) throws DataStoreIntegrationException, TransformationException, SerializationException;	
	
	public boolean exists(String entityName, AbstractType rowKey) throws DataStoreIntegrationException;
	
	/**
	 * Delete a full row ( and it's index rows )
	 * @param entityName
	 * @param rowKey
	 * @throws TransformationException 
	 * @throws DataStoreIntegrationException 
	 * @throws SerializationException 
	 */
	public void delete(String entityName, AbstractType rowKey) throws TransformationException, DataStoreIntegrationException, SerializationException;
	
	/**
	 * Delete some {@link HesperidesColumn}s ( and their index rows ) from a {@link HesperidesRow} identified by {@code rowKey}
	 * @param entityName
	 * @param rowKey
	 * @param columns List of columns to delete off this row. Value ( if there is any ) is discarded
	 * @throws DataStoreIntegrationException 
	 * @throws SerializationException 
	 */
	public void delete(String entityName, AbstractType rowKey, List<HesperidesColumn> columns) throws DataStoreIntegrationException, SerializationException;
	
	/**
	 * Delete some {@link HesperidesColumn}s as specified by the {@link HesperidesColumnSlice} off the {@link HesperidesRow} identified by {@code rowKey}
	 * @param entityName
	 * @param rowKey
	 * @param columns
	 * @throws TransformationException 
	 * @throws DataStoreIntegrationException 
	 * @throws SerializationException 
	 */
	public void deleteMatching(String entityName, AbstractType rowKey, List<HesperidesColumnSlice> columns) throws DataStoreIntegrationException, TransformationException, SerializationException;
	
	
}
