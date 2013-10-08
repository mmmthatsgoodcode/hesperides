package com.mmmthatsgoodcode.hesperides.cassify.integration;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.hesperides.ComplexRow;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;
import com.mmmthatsgoodcode.utils.other.RiggedRand.ParticipantDistributionException;

public abstract class CassandraThriftClientIntegrationTest {

	protected CassandraThriftClientIntegration integration = null;
	protected static EmbeddedCassandraDaemon cassandra = null;
	protected final Logger LOG;
	
	public CassandraThriftClientIntegrationTest() {
		this.LOG = LoggerFactory.getLogger(this.getClass());
	}
	
	@BeforeClass
	public static void startCassandra() throws Exception {

		cassandra = new EmbeddedCassandraDaemon();
		cassandra.cleanupDirectories();
		cassandra.start();
		
	}
	
	@AfterClass
	public static void stopCassandra() throws Exception {
		
		if (cassandra != null) {
			
			cassandra.stop();
			
		}
		
	}
	
	@Test
	public void testSimpleStoreRetrieve() throws ParticipantDistributionException, CassandraThriftClientException {
		
		HesperidesRow row = ComplexRow.generate(1).get(0);
		integration.store("ComplexRow", row);
		
		
	}
	
}
