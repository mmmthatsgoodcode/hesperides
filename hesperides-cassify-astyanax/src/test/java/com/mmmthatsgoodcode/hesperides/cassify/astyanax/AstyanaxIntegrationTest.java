package com.mmmthatsgoodcode.hesperides.cassify.astyanax;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.mmmthatsgoodcode.hesperides.cassify.integration.CassandraThriftClientIntegration;
import com.mmmthatsgoodcode.hesperides.cassify.integration.CassandraThriftClientIntegrationTest;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.AstyanaxContext.Builder;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.ConnectionPoolConfiguration;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NoAvailableHostsException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.ConsistencyLevel;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

public class AstyanaxIntegrationTest extends
		CassandraThriftClientIntegrationTest {

	
	@Before
	public void createIntegrationObject() {
		
		if (integration == null) {
			
		    	String seed = (cassandraNode.equals("embed")?"localhost:10160":cassandraNode);
		    
		    	System.out.println("Connecting to seeds - "+seed);
			try {
				
				ConnectionPoolConfiguration pool = new ConnectionPoolConfigurationImpl("testPool")
		        .setInitConnsPerHost(1)
		        .setMaxConnsPerHost(1)
		        .setMaxConns(1)
		        .setSeeds(seed);
				
				Builder builder = new AstyanaxContext.Builder()
			    .forCluster("testCluster")
			    .forKeyspace("HesperidesUnitTest")
			    .withAstyanaxConfiguration(
			    		new AstyanaxConfigurationImpl()
			    		.setDiscoveryType(NodeDiscoveryType.NONE)
			    		.setDefaultReadConsistencyLevel(ConsistencyLevel.CL_ONE)
			    		.setDefaultWriteConsistencyLevel(ConsistencyLevel.CL_ANY)
			    		.setConnectionPoolType(ConnectionPoolType.BAG)
			    		.setTargetCassandraVersion("1.2"))
			    .withConnectionPoolConfiguration(pool);
				
				AstyanaxContext<Keyspace> keyspaceContext = builder.buildKeyspace(ThriftFamilyFactory.getInstance());
			
				keyspaceContext.start();
				
				try {
				    
//				    	System.out.println(HesperidesDynamicComposite.dynamicCompositeTypeDescriptor());
        				keyspaceContext.getClient().createKeyspace(ImmutableMap.<String,Object>builder().put("name", "HesperidesUnitTest").put("strategy_class", "SimpleStrategy").put("strategy_options", ImmutableMap.<String,Object>builder().put("replication_factor", "1").build() ).build());
        				keyspaceContext.getClient().createColumnFamily(ImmutableMap.<String, Object>builder().put("name", "ComplexRow").put("comparator_type", AstyanaxIntegration.dynamicCompositeTypeDescriptor()).build());
        				keyspaceContext.getClient().createColumnFamily(ImmutableMap.<String, Object>builder().put("name", "ComplexRow"+CassandraThriftClientIntegration.INDEX_CF_SUFFIX).put("comparator_type", AstyanaxIntegration.dynamicCompositeTypeDescriptor()).build());
        				keyspaceContext.getClient().createColumnFamily(ImmutableMap.<String, Object>builder().put("name", "ComplexRow"+CassandraThriftClientIntegration.INDEX_CACHE_CF_SUFFIX).put("comparator_type", AstyanaxIntegration.dynamicCompositeTypeDescriptor()).build());

				} catch (ConnectionException e) {
				    
				}
				integration = new AstyanaxIntegration(keyspaceContext);
				

				
			} catch(Exception e) {
				
				LOG.error("Caught exception while initializing store {}", e);
				
			}		
			
		}
		
	}
	
}
