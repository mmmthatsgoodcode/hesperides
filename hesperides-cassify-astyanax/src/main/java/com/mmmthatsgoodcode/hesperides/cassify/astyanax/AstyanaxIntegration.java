package com.mmmthatsgoodcode.hesperides.cassify.astyanax;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.hesperides.cassify.HesperidesRowTransformer;
import com.mmmthatsgoodcode.hesperides.cassify.integration.CassandraThriftClientException;
import com.mmmthatsgoodcode.hesperides.cassify.integration.CassandraThriftClientIntegration;
import com.mmmthatsgoodcode.hesperides.cassify.integration.NodeLocator;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn.AbstractType;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.BytesArraySerializer;
import com.netflix.astyanax.serializers.StringSerializer;

public class AstyanaxIntegration implements CassandraThriftClientIntegration {

	private final AstyanaxContext<Keyspace> keyspaceContext;
	private AstyanaxCassifier cassifier = new AstyanaxCassifier();
	
	public AstyanaxIntegration(AstyanaxContext<Keyspace> keyspaceContext) {
		this.keyspaceContext = keyspaceContext;
	}
	
	@Override
	public boolean store(String cfName, HesperidesRow row) throws CassandraThriftClientException {
		
		ColumnFamily columnFamily = new ColumnFamily<String, HesperidesDynamicComposite>(cfName, StringSerializer.get(), HesperidesDynamicCompositeSerializer.get());
		ColumnFamily indexColumnFamily = new ColumnFamily<byte[], String>(cfName+INDEX_CF_SUFFIX, BytesArraySerializer.get(), StringSerializer.get());
		
		// first, find indexed rows
		List<HesperidesColumn> indexes = CassandraThriftClientIntegration.IndexedRows.find(row);
		
		MutationBatch mutationBatch = keyspaceContext.getClient().prepareMutationBatch();
		
		/* 1) Mutation on the index CF for any indexed columns
		----------------------------------------------------------- */
		
		for (HesperidesColumn index:indexes) {
			
			
			ByteArrayOutputStream name = new ByteArrayOutputStream();
			
			// create the name for the index row			
			try {
				
				// serialise name components to bytes
				for(AbstractType nameComponent:index.getNameComponents()) {
					name.write(nameComponent.getSerializer().toByteBuffer(nameComponent).array());
					name.write(COMPONENT_DELIMITER);
				}
	
				// add value to name
				name.write(index.getValue().getSerializer().toByteBuffer(index).array());

			} catch (IOException e) {
				// not going to happen
				
			}
			
			// TODO hash name before writing it ?
			ColumnListMutation<String> indexMutation = mutationBatch.withRow(indexColumnFamily, name.toByteArray());
			indexMutation.putColumn(row.getKey(), 0);
			
		}
		
		/* 2) Actual mutation for the row
		------------------------------------------ */

		ColumnListMutation<HesperidesDynamicComposite> mutation = mutationBatch.withRow(indexColumnFamily, row.getKey());
		cassifier.populateColumnListMutation( mutation, row );

		
		try {
			mutationBatch.execute();
		} catch (ConnectionException e) {
			throw new CassandraThriftClientException(e);
		}
		
		
		return true;
		
	}

	@Override
	public HesperidesRow retrieve(String cfName, String rowKey) throws CassandraThriftClientException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HesperidesRow retrieve(String cfName, String rowKey, NodeLocator locator) throws CassandraThriftClientException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HesperidesRow retrieve(String cfName, String indexName, String indexValue, int limit) throws CassandraThriftClientException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HesperidesRow retrieve(String cfName, String indexName, String indexValue, int limit,
			NodeLocator locator) throws CassandraThriftClientException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HesperidesRow retrieve(String cfName, String indexName, Integer indexValue, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HesperidesRow retrieve(String cfName, String indexName, Integer indexValue, int limit,
			NodeLocator locator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> retrieveRowKeysByIndex(String cfName, String indexName,
			String indexValue, int limit) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
