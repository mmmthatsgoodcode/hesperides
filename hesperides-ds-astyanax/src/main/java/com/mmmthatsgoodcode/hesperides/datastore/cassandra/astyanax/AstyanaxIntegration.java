package com.mmmthatsgoodcode.hesperides.datastore.cassandra.astyanax;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.mmmthatsgoodcode.astyanax.HesperidesDynamicCompositeRangeBuilder;
import com.mmmthatsgoodcode.astyanax.HesperidesDynamicCompositeSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.SerializationException;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.type.ByteArrayValue;
import com.mmmthatsgoodcode.hesperides.datastore.HesperidesColumnSliceTransformer;
import com.mmmthatsgoodcode.hesperides.datastore.HesperidesRowTransformer;
import com.mmmthatsgoodcode.hesperides.datastore.integration.DataStoreIntegration;
import com.mmmthatsgoodcode.hesperides.datastore.integration.DataStoreIntegrationException;
import com.mmmthatsgoodcode.hesperides.datastore.model.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.datastore.model.HesperidesColumnSlice;
import com.mmmthatsgoodcode.hesperides.datastore.model.HesperidesRow;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.DynamicComposite;
import com.netflix.astyanax.query.RowQuery;
import com.netflix.astyanax.serializers.BytesArraySerializer;
import com.netflix.astyanax.serializers.DynamicCompositeSerializer;

public class AstyanaxIntegration implements DataStoreIntegration {

	private final AstyanaxContext<Keyspace> keyspaceContext;
	private AstyanaxColumnTransformer cassifier = new AstyanaxColumnTransformer();
	private final Logger LOG = LoggerFactory.getLogger(AstyanaxIntegration.class);

	public AstyanaxIntegration(AstyanaxContext<Keyspace> keyspaceContext) {
		this.keyspaceContext = keyspaceContext;
	}

	@Override
	public void store(String entityName, HesperidesRow row) throws DataStoreIntegrationException, TransformationException, SerializationException {
		
		store(entityName, Arrays.asList(new HesperidesRow[] {row}));
		
	}
	
	@Override
	public void store(String entityName, List<HesperidesRow> rows) throws DataStoreIntegrationException, TransformationException, SerializationException {

		ColumnFamily<byte[], DynamicComposite> columnFamily = new ColumnFamily<byte[], DynamicComposite>(entityName,
				BytesArraySerializer.get(), HesperidesDynamicCompositeSerializer.get());
		ColumnFamily<byte[], DynamicComposite> indexColumnFamily = new ColumnFamily<byte[], DynamicComposite>(entityName
				+ INDEX_CF_SUFFIX, BytesArraySerializer.get(), HesperidesDynamicCompositeSerializer.get());
		ColumnFamily<byte[], DynamicComposite> indexRecordsColumnFamily = new ColumnFamily<byte[], DynamicComposite>(entityName
				+ INDEX_RECORDS_CF_SUFFIX, BytesArraySerializer.get(), HesperidesDynamicCompositeSerializer.get());
		ColumnFamily<byte[], DynamicComposite> indexCacheColumnFamily = new ColumnFamily<byte[], DynamicComposite>(
				entityName + INDEX_CACHE_CF_SUFFIX, BytesArraySerializer.get(), HesperidesDynamicCompositeSerializer.get());

		MutationBatch mutationBatch = keyspaceContext.getClient().prepareMutationBatch();

		for(HesperidesRow row:rows) {
		
			// first, find indexed rows
			List<HesperidesColumn> indexes = DataStoreIntegration.IndexedColumns.find(row);
	
	
			/*
			 * 1) Mutation on the index column families for any indexed columns
			 * -----------------------------------------------------------
			 */
	
			for (HesperidesColumn index : indexes) {
	
				LOG.debug("Saving index {}", index.getNameComponents());
				
				/*
				 * save IndexRecord row - row key is the indexed row's key, column name components are 
				 * index name components and the index value
				 * -------------
				 */
				HesperidesRow indexRecordRow = new HesperidesRow(row.getKey()).addColumn(
					new HesperidesColumn()
						.addNameComponents(index.getNameComponents())
						.addNameComponent(index.getValue())
						.setCreated(index.getCreated()));
	
				
				ColumnListMutation<DynamicComposite> indexMutation = mutationBatch.withRow(indexRecordsColumnFamily,
						indexRecordRow.getKey().getSerializer().toByteBufferWithHint(indexRecordRow.getKey()).array());
				cassifier.populateColumnListMutation(indexMutation, indexRecordRow);
				
				/*
				 * save Index row - row key is index name components joined by
				 * COMPONENT_DELIMITER + the index value - column name is indexed
				 * row's key 
				 * -------------
				 */
	
				// TODO hash name before writing it
				HesperidesRow indexRow = new HesperidesRow(new ByteArrayValue( indexRowKey(new HesperidesColumnSlice().n(index.getNameComponents()), index.getValue()).toByteArray()));
				HesperidesColumn indexColumn = new HesperidesColumn();
				indexColumn.addNameComponent(row.getKey());
				indexColumn.setCreated(index.getCreated());
				indexColumn.setTtl(index.getTtl());
	
				indexRow.addColumn(indexColumn);
	
				ColumnListMutation<DynamicComposite> indexRecordMutation = mutationBatch.withRow(indexColumnFamily,
						indexRow.getKey().getSerializer().toByteBuffer(indexRow.getKey()).array());
				cassifier.populateColumnListMutation(indexRecordMutation, indexRow);
	
				if (DataStoreIntegration.USE_INDEX_CACHE_CF) {
					
					/*
					 * save IndexCache row - row key is index name - column name is
					 * composite of index value and indexed row's key 
					 * ------------------
					 */
		
					// create the name for the index row
		
					HesperidesRow indexCacheRow = new HesperidesRow(new ByteArrayValue(indexCacheRowKey(new HesperidesColumnSlice().n(index.getNameComponents())).toByteArray()));
					HesperidesColumn indexCacheColumn = new HesperidesColumn();
					indexCacheColumn.addNameComponent(index.getValue());
					indexCacheColumn.addNameComponent(row.getKey());
					indexCacheColumn.setCreated(index.getCreated());
					indexCacheColumn.setTtl(index.getTtl()); // TODO enforce a short TTL
																// ( as this row could
																// get quite wide )
					indexCacheRow.addColumn(indexCacheColumn);
		
					ColumnListMutation<DynamicComposite> indexCacheMutation = mutationBatch.withRow(
							indexCacheColumnFamily, indexCacheRow.getKey().getSerializer().toByteBuffer(indexCacheRow.getKey()).array());
		
					cassifier.populateColumnListMutation(indexCacheMutation, indexCacheRow);
				}
	
			}
	
			/*
			 * 2) Actual mutation for the row
			 * ------------------------------------------
			 */
	
			ColumnListMutation<DynamicComposite> mutation = mutationBatch.withRow(columnFamily, row.getKey().getSerializer().toByteBufferWithHint(row.getKey()).array());
			cassifier.populateColumnListMutation(mutation, row);
	
			}
		
		try {

			mutationBatch.execute();
		} catch (ConnectionException e) {
			throw new DataStoreIntegrationException(e);
		}

	}
	
	@Override
	public boolean exists(String entityName, AbstractType rowKey) throws DataStoreIntegrationException {
		
		ColumnFamily columnFamily = new ColumnFamily<byte[], DynamicComposite>(entityName, BytesArraySerializer.get(),
				HesperidesDynamicCompositeSerializer.get());
		
		try {
			OperationResult<Integer> columns = keyspaceContext.getClient().prepareQuery(columnFamily).getKey(rowKey.getSerializer().toByteBufferWithHint(rowKey).array()).getCount().execute();
			return columns.getResult() > 0;
		} catch (ConnectionException | SerializationException e) {
			throw new DataStoreIntegrationException(e);
		}
		
	}
	
	
	@Override
	public HesperidesRow retrieve(String entityName, AbstractType rowKey) throws DataStoreIntegrationException,
			TransformationException, SerializationException {

		ColumnFamily columnFamily = new ColumnFamily<byte[], DynamicComposite>(entityName, BytesArraySerializer.get(),
				HesperidesDynamicCompositeSerializer.get());

		try {

			OperationResult<ColumnList<DynamicComposite>> results = keyspaceContext.getClient()
					.prepareQuery(columnFamily).getKey(rowKey.getSerializer().toByteBufferWithHint(rowKey).array()).execute();
			return cassifier.cassify(results, rowKey);

		} catch (ConnectionException e) {

			throw new DataStoreIntegrationException(e);
		}

	}

	@Override
	public HesperidesRow retrieve(String cfName, AbstractType rowKey, List<HesperidesColumn> columns) throws DataStoreIntegrationException, TransformationException, SerializationException {
		
		ColumnFamily<byte[], DynamicComposite> columnFamily = new ColumnFamily<byte[], DynamicComposite>(cfName, BytesArraySerializer.get(),
				HesperidesDynamicCompositeSerializer.get());
		
		List<DynamicComposite> columnNames = new ArrayList<DynamicComposite>();
		
		for (HesperidesColumn column:columns) {
			
			columnNames.add(cassifier.cassify(column.getNameComponents()));
			
		}
		
		try {
			
			OperationResult<ColumnList<DynamicComposite>> results = keyspaceContext.getClient()
					.prepareQuery(columnFamily).getKey(rowKey.getSerializer().toByteBufferWithHint(rowKey).array()).withColumnSlice( columnNames ).execute();
			
			return cassifier.cassify(results, rowKey);
			
		} catch (ConnectionException e) {
			throw new DataStoreIntegrationException(e);
		}
		
		
	}
	
	@Override
	public HesperidesRow retrieveMatching(String cfName, AbstractType rowKey, List<HesperidesColumnSlice> locators)
			throws DataStoreIntegrationException, TransformationException, SerializationException {

	
		ColumnFamily<byte[], DynamicComposite> columnFamily = new ColumnFamily<byte[], DynamicComposite>(cfName, BytesArraySerializer.get(),
				HesperidesDynamicCompositeSerializer.get());
		
		
		try {
			
			
			RowQuery<byte[], DynamicComposite> rangeQuery = keyspaceContext.getClient()
					.prepareQuery(columnFamily)
					.getKey(rowKey.getSerializer().toByteBufferWithHint(rowKey).array());
			
			RowQuery<byte[], DynamicComposite> sliceQuery = keyspaceContext.getClient()
					.prepareQuery(columnFamily)
					.getKey(rowKey.getSerializer().toByteBufferWithHint(rowKey).array());
			
			List<HesperidesColumn> columns = new ArrayList<HesperidesColumn>();
			
			for (HesperidesColumnSlice locator:locators) {
				
				LOG.debug("Looking for {} on row {}", locator, rowKey);
				
				if (locator.isPartial()) {
					
					// create composite range builder from column slice
					OperationResult<ColumnList<DynamicComposite>> results = keyspaceContext.getClient()
					.prepareQuery(columnFamily)
					.getKey(rowKey.getSerializer().toByteBufferWithHint(rowKey).array())
					.withColumnRange(columnSliceToRange(locator))
					.execute();				
				
					columns.addAll( cassifier.cassify(results, rowKey).getColumns() );
				
				} else {
					// just get the specified column
					
					// create composite range builder from column slice
					OperationResult<ColumnList<DynamicComposite>> results = keyspaceContext.getClient()
					.prepareQuery(columnFamily)
					.getKey(rowKey.getSerializer().toByteBufferWithHint(rowKey).array())
					.withColumnSlice( cassifier.cassify(locator.components()) )
					.execute();				
				
					columns.addAll( cassifier.cassify(results, rowKey).getColumns() );
					
				}
				

				
			}
			
			LOG.debug("Found columns {}", columns);
			
			return new HesperidesRow(rowKey).addColumns(columns);

		} catch (ConnectionException e) {

			throw new DataStoreIntegrationException(e);
		}		
		
	
	}
	
	@Override
	public Set<HesperidesRow> retrieve(String cfName, Multimap<HesperidesColumnSlice, AbstractType> indexes, HesperidesColumnSlice.Relation relation) throws TransformationException, DataStoreIntegrationException, SerializationException {
		

		Set<HesperidesRow> indexedRows = new HashSet<HesperidesRow>();
		
		for (AbstractType indexedRowKey:retrieveRowKeysByIndexes(cfName, indexes, relation, 100)) {
			
			indexedRows.add(retrieve(cfName, indexedRowKey));
			
		}

		return indexedRows;
		
	}

	@Override
	public Set<HesperidesRow> retrieve(String cfName, HesperidesColumnSlice indexName, AbstractType indexValue, int limit)
			throws DataStoreIntegrationException, TransformationException, SerializationException {

		Set<HesperidesRow> indexedRows = new HashSet<HesperidesRow>();
		
		for (AbstractType indexedRowKey:retrieveRowKeysByIndex(cfName, indexName, indexValue, limit)) {
			
			indexedRows.add(retrieve(cfName, indexedRowKey));
			
		}

		return indexedRows;
	}

	@Override
	public Set<HesperidesRow> retrieve(String cfName, HesperidesColumnSlice indexName, AbstractType indexValue, int limit,
			List<HesperidesColumnSlice> slice) throws DataStoreIntegrationException, TransformationException, SerializationException {
		
		Set<HesperidesRow> indexedRows = new HashSet<HesperidesRow>();
		
		for (AbstractType indexedRowKey:retrieveRowKeysByIndex(cfName, indexName, indexValue, limit)) {
			
			indexedRows.add(retrieveMatching(cfName, indexedRowKey, slice));
			
		}

		return indexedRows;
		
	}

	@Override
	public Set<AbstractType> retrieveRowKeysByIndexes(String entityName, Multimap<HesperidesColumnSlice, AbstractType> indexes, HesperidesColumnSlice.Relation relation, int limit) throws TransformationException, DataStoreIntegrationException, SerializationException {
		
		// resolve all indexes..
		List<Set<AbstractType>> allRowKeys = new ArrayList<Set<AbstractType>>();
		for (Entry<HesperidesColumnSlice, Collection<AbstractType>> index:indexes.asMap().entrySet()) {
			
			for(AbstractType indexValue:index.getValue()) {
				
				allRowKeys.add(retrieveRowKeysByIndex(entityName, index.getKey(), indexValue, limit));

			}
			
		}
		
		switch (relation) {
		
			case AND:
				// intersect
				LOG.debug("Intersecting {}", allRowKeys);
				Set<AbstractType> intersection = allRowKeys.get(0);
				for(Set<AbstractType> scan:allRowKeys.subList(1, allRowKeys.size())) {
					intersection = Sets.intersection(intersection, scan);
				}
				
				LOG.debug("Found keys {}", intersection);
				return intersection;
			case OR:
			default:
				// de-dupe
				LOG.debug("De-duping {}", allRowKeys);
				Set<AbstractType> uniqueKeys = new HashSet<AbstractType>();
				for(Set<AbstractType> scan:allRowKeys) {
					uniqueKeys.addAll(scan);
				}
				
				LOG.debug("Found keys {}", uniqueKeys);
				return uniqueKeys;
		
		}
		
	}
	
	@Override
	public Set<AbstractType> retrieveRowKeysByIndex(String entityName, HesperidesColumnSlice indexName, AbstractType indexValue, int limit) throws TransformationException, DataStoreIntegrationException, SerializationException {
		
		Set<AbstractType> rowKeys = new HashSet<AbstractType>();
		ColumnFamily indexCacheColumnFamily = new ColumnFamily<byte[], DynamicComposite>(
				entityName + INDEX_CACHE_CF_SUFFIX, BytesArraySerializer.get(), HesperidesDynamicCompositeSerializer.get());

		OperationResult<ColumnList<DynamicComposite>> results;

		try {

			if (DataStoreIntegration.USE_INDEX_CACHE_CF) {

				// figure out the indexCache row key
				AbstractType indexCacheRowKey = new ByteArrayValue( indexCacheRowKey( new HesperidesColumnSlice().n(indexName.components())).toByteArray() );

				if (LOG.isDebugEnabled())
					LOG.debug("Index name is (hex) {}", indexCacheRowKey);

				// look in indexCache CF
				LOG.debug("Building Column range for indexCache CF query with indexValue \"{}\"", indexValue);
				
				HesperidesDynamicCompositeRangeBuilder indexCacheRangeBuilder = HesperidesDynamicCompositeSerializer.get()
						.buildRange(DynamicComposite.DEFAULT_ALIAS_TO_COMPARATOR_MAPPING.inverse())
						.beginsWith(indexValue);
	
				results = keyspaceContext.getClient().prepareQuery(indexCacheColumnFamily).getKey(indexCacheRowKey.getValue())
						.withColumnRange(indexCacheRangeBuilder).execute();
	
				// see if we have a result
				if (results.getResult().isEmpty() == false) {
					LOG.debug("Found row in indexCache CF");
					HesperidesRow indexCacheRow = cassifier.cassify(results, indexCacheRowKey);
					LOG.debug("Decoded indexCache {}", indexCacheRow);
					// TODO apply limit
					for (HesperidesColumn indexCacheColumn : indexCacheRow.getColumns()) {
						AbstractType rowKey = indexCacheColumn.getNameComponents().get(1);
	
						if (rowKey != null) {
							LOG.debug("Indexed row key is {}", rowKey);
							// turn the key from byte[] to its AbstractType representation
							rowKeys.add( rowKey );
						} else {
							
							// index cf column name in unexpected format..
							
						}
	
					}
	
				}
			
			}

			// nope, look in the index CF
			ColumnFamily indexColumnFamily = new ColumnFamily<byte[], DynamicComposite>(entityName + INDEX_CF_SUFFIX,
					BytesArraySerializer.get(), DynamicCompositeSerializer.get());
			ByteArrayValue indexRowKey = new ByteArrayValue( indexRowKey( new HesperidesColumnSlice().n(indexName.components()), indexValue).toByteArray() );

			results = keyspaceContext.getClient().prepareQuery(indexColumnFamily).getKey(indexRowKey.getValue()).execute();

			// see if we have a result
			if (results.getResult().isEmpty() == false) {
				LOG.debug("Found row in index CF");
				HesperidesRow indexRow = cassifier.cassify(results, indexRowKey);
				LOG.debug("Decoded indexCache CF {}", indexRow);
				// TODO apply limit
				for (HesperidesColumn indexColumn : indexRow.getColumns()) {
					AbstractType rowKey = indexColumn.getNameComponents().get(0);
					
					if (rowKey != null) {
						LOG.debug("Indexed row key is {}", rowKey);
						rowKeys.add(rowKey);
					} else {

						// index cf column name in unexpected format..
						
					}
					
				}
				
			}

			// TODO place result on index cache CF

		} catch (ConnectionException e) {

			throw new DataStoreIntegrationException(e);
		}
		
		return rowKeys;
		
	}

	public static String dynamicCompositeTypeDescriptor() {

		List<String> types = new ArrayList<String>();
		for (Entry<Byte, String> aliasAndType : DynamicComposite.DEFAULT_ALIAS_TO_COMPARATOR_MAPPING.entrySet()) {
			types.add(new String(new byte[] { aliasAndType.getKey() }) + "=>" + aliasAndType.getValue());
		}

		return "DynamicCompositeType(" + StringUtils.join(types.toArray(), ",") + ")";

	}

	@Override
	public void delete(String entityName, AbstractType rowKey) throws TransformationException, DataStoreIntegrationException, SerializationException {
		
		ColumnFamily<byte[], DynamicComposite> columnFamily = new ColumnFamily<byte[], DynamicComposite>(entityName,
				BytesArraySerializer.get(), HesperidesDynamicCompositeSerializer.get());
		
		MutationBatch mutationBatch = keyspaceContext.getClient().prepareMutationBatch();
		
		// delete the row itself.
		mutationBatch.withRow(columnFamily, rowKey.getSerializer().toByteBufferWithHint(rowKey).array() ).delete();
		
		// delete all 3 representations of its indexes
		for(HesperidesColumn index:retrieveIndexedColumnsForRow(entityName, rowKey)) {
			
			deleteIndex(mutationBatch, entityName, rowKey, index);			
			
		}

		
		try {
			mutationBatch.execute();
		} catch (ConnectionException e) {
			throw new DataStoreIntegrationException(e);
		}

	}

	@Override
	public void delete(String entityName, AbstractType rowKey, List<HesperidesColumn> columnsToDelete) throws DataStoreIntegrationException, SerializationException {
		
		ColumnFamily<byte[], DynamicComposite> columnFamily = new ColumnFamily<byte[], DynamicComposite>(entityName,
				BytesArraySerializer.get(), HesperidesDynamicCompositeSerializer.get());
		
		MutationBatch mutationBatch = keyspaceContext.getClient().prepareMutationBatch();
		
		for (HesperidesColumn columnToDelete:columnsToDelete) {
			
			mutationBatch.withRow(columnFamily, rowKey.getSerializer().toByteBufferWithHint(rowKey).array()).deleteColumn(cassifier.cassify(columnToDelete.getNameComponents()));

			
		}
		
		try {
			mutationBatch.execute();
		} catch (ConnectionException e) {
			throw new DataStoreIntegrationException(e);
		}
	}

	@Override
	public void deleteMatching(String entityName, AbstractType rowKey, List<HesperidesColumnSlice> columns) throws DataStoreIntegrationException, TransformationException, SerializationException {

		LOG.debug("Deleting columns matching column slices {}", columns);

		ColumnFamily<byte[], DynamicComposite> columnFamily = new ColumnFamily<byte[], DynamicComposite>(entityName,
				BytesArraySerializer.get(), HesperidesDynamicCompositeSerializer.get());
		
		MutationBatch mutationBatch = keyspaceContext.getClient().prepareMutationBatch();
		
		// since there is no deleting columns on a slice ( CASSANDRA-494 ), we need to get matching columns and delete them 
		HesperidesRow row = retrieveMatching(entityName, rowKey, columns);
		LOG.debug("Deleting columns {}", row);
		for (HesperidesColumn column:row.getColumns()) {
			
			LOG.debug("Deleting column {}", column);
			
			// delete the actual column
			mutationBatch.withRow(columnFamily, rowKey.getSerializer().toByteBufferWithHint(rowKey).array() ).deleteColumn(cassifier.cassify(column.getNameComponents()));
			
			// delete indexes
			deleteIndex(mutationBatch, entityName, rowKey, column);
			
		}
		
		try {
			mutationBatch.execute();
		} catch (ConnectionException e) {
			throw new DataStoreIntegrationException(e);
		}
		
	}
	

	private List<HesperidesColumn> retrieveIndexedColumnsForRow(String cfName, AbstractType rowKey) throws TransformationException, DataStoreIntegrationException, SerializationException {
		
		List<HesperidesColumn> indexedColumns = new ArrayList<HesperidesColumn>();
		
		ColumnFamily<byte[], DynamicComposite> indexRecordsColumnFamily = new ColumnFamily<byte[], DynamicComposite>(cfName
				+ INDEX_RECORDS_CF_SUFFIX, BytesArraySerializer.get(), HesperidesDynamicCompositeSerializer.get());
		
		try {
			OperationResult<ColumnList<DynamicComposite>> results = keyspaceContext.getClient()
					.prepareQuery(indexRecordsColumnFamily).getKey(rowKey.getSerializer().toByteBufferWithHint(rowKey).array()).execute();
			
			if (results.getResult().isEmpty() == false) {
				
				HesperidesRow indexRecordRow = cassifier.cassify(results, rowKey);
				for (HesperidesColumn indexRecord:indexRecordRow.getColumns()) {
					
					indexedColumns.add( 
							new HesperidesColumn()
							.addNameComponents(ImmutableList.copyOf(Iterables.limit(indexRecord.getNameComponents(), indexRecord.getNameComponents().size()-1)) )
							.setValue(indexRecord.getNameComponents().get(indexRecord.getNameComponents().size()-1)));
					
					
				}
				
			}
			
		} catch (ConnectionException e) {
			throw new DataStoreIntegrationException(e);
		}
		
		return indexedColumns;
		
	}
	
	private void deleteIndex(MutationBatch mutationBatch, String cfName, AbstractType rowKey, HesperidesColumn indexedColumn) throws DataStoreIntegrationException, SerializationException {
		
		LOG.debug("Cleaning up indexedColumn {}", indexedColumn);
		
		ColumnFamily<byte[], DynamicComposite> indexColumnFamily = new ColumnFamily<byte[], DynamicComposite>(cfName
				+ INDEX_CF_SUFFIX, BytesArraySerializer.get(), HesperidesDynamicCompositeSerializer.get());
		ColumnFamily<byte[], DynamicComposite> indexRecordsColumnFamily = new ColumnFamily<byte[], DynamicComposite>(cfName
				+ INDEX_RECORDS_CF_SUFFIX, BytesArraySerializer.get(), HesperidesDynamicCompositeSerializer.get());
		ColumnFamily<byte[], DynamicComposite> indexCacheColumnFamily = new ColumnFamily<byte[], DynamicComposite>(
				cfName + INDEX_CACHE_CF_SUFFIX, BytesArraySerializer.get(), HesperidesDynamicCompositeSerializer.get());
		
		
		// index name column
		HesperidesColumnSlice indexColumnSlice = new HesperidesColumnSlice().n(indexedColumn.getNameComponents());
		// index value+row key column
		HesperidesColumnSlice indexCacheColumnSlice = new HesperidesColumnSlice().n(indexedColumn.getValue()).n(new ByteArrayValue( rowKey.getSerializer().toByteBufferWithHint(rowKey).array() ));
		// index name + index value column
		HesperidesColumnSlice indexRecordsColumnSlice = new HesperidesColumnSlice().n(indexedColumn.getNameComponents()).n(indexedColumn.getValue());
		
		// clean up indexCacheColumnFamily
		byte[] indexCacheRowKey = indexCacheRowKey(indexColumnSlice).toByteArray();
		mutationBatch.withRow(indexCacheColumnFamily, indexCacheRowKey).deleteColumn(cassifier.cassify(indexCacheColumnSlice.components()));
		
		// clean up indexColumnFamily
		byte[] indexRowKey = indexRowKey(indexColumnSlice, indexedColumn.getValue()).toByteArray();
		mutationBatch.withRow(indexColumnFamily, indexRowKey).delete();		
		
		// clean up indexRecordsColumnFamily
		mutationBatch.withRow(indexRecordsColumnFamily, rowKey.getSerializer().toByteBufferWithHint(rowKey).array()).deleteColumn(cassifier.cassify(indexRecordsColumnSlice.components()));

	}
	
	/**
	 * Constructs the row key used in indexColumnFamily
	 * 
	 * @param indexName
	 * @param indexValue
	 * @return
	 * @throws SerializationException 
	 */
	private ByteArrayOutputStream indexRowKey(HesperidesColumnSlice slice, AbstractType indexValue) throws SerializationException {

		LOG.debug("Constructing index row key from {} and {}", slice, indexValue);
		ByteArrayOutputStream name = indexCacheRowKey(slice);

		// add value to name
		try {
			name.write(indexValue.getSerializer().toByteBuffer(indexValue).array());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return name;

	}

	/**
	 * Constructs the row key used in the indexCache CF
	 * 
	 * @param indexName
	 * @return
	 * @throws SerializationException 
	 */
	private ByteArrayOutputStream indexCacheRowKey(HesperidesColumnSlice slice) throws SerializationException {

		LOG.debug("Constructing index cache row key from {}", slice);
		ByteArrayOutputStream name = new ByteArrayOutputStream();

		try {

			// Serialize name components to bytes
			for (AbstractType nameComponent : slice.components()) {
				name.write(nameComponent.getSerializer().toByteBuffer(nameComponent).array());
				name.write(COMPONENT_DELIMITER);
			}

		} catch (IOException e) {
			// not going to happen

		}

		return name;

	}
	
	public HesperidesDynamicCompositeRangeBuilder columnSliceToRange(HesperidesColumnSlice columnSlice) throws TransformationException {

		HesperidesDynamicCompositeRangeBuilder rangeBuilder = HesperidesDynamicCompositeSerializer.get()
                .buildRange(DynamicComposite.DEFAULT_ALIAS_TO_COMPARATOR_MAPPING.inverse());
		
		if (columnSlice.components().size() > 1) {
			
			List<Object> nameComponents = new ArrayList<Object>();
			for(AbstractType nameComponent:columnSlice.components()) {
				
				nameComponents.add(nameComponent.getValue());
				
			}

			// feed them to a composite range builder
			rangeBuilder.beginsWith(nameComponents);
			
		} else {
			
			rangeBuilder.beginsWith(columnSlice.components().get(0).getValue());
			
		}
		
		LOG.debug("Created range builder {} from {}", rangeBuilder, columnSlice);
				
		return rangeBuilder;


	}

		

}
