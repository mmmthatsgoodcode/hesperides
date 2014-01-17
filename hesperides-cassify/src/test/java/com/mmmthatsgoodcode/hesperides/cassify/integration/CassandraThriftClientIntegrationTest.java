package com.mmmthatsgoodcode.hesperides.cassify.integration;

import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mmmthatsgoodcode.hesperides.ComplexRow;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.utils.other.RiggedRand.ParticipantDistributionException;

public abstract class CassandraThriftClientIntegrationTest {

    	protected final static String cassandraNode = System.getProperty("cassandra", "embed");
	protected CassandraThriftClientIntegration integration = null;
	protected static EmbeddedCassandraDaemon cassandra = null;
	protected final Logger LOG;
	
	public CassandraThriftClientIntegrationTest() {
		this.LOG = LoggerFactory.getLogger(this.getClass());
	}
	
	@BeforeClass
	public static void startCassandra() throws Exception {

	    if (cassandraNode.equals("embed")) {
		
		cassandra = new EmbeddedCassandraDaemon();
		cassandra.cleanupDirectories();
		cassandra.start();
		
	    }
	    
	    
		
	}
	
	
	@AfterClass
	public static void stopCassandra() throws Exception {
		
		if (cassandraNode.equals("embed") && cassandra != null) {
			
			cassandra.stop();
			
		}
		
	}
	
	@Test
	public void testSimpleStoreRetrieve() throws ParticipantDistributionException, CassandraThriftClientException, TransformationException {
		
        	HesperidesRow row = ComplexRow.generate(1).get(0);
        	integration.store("ComplexRow", row);
        
        	HesperidesRow retrievedRow = integration.retrieve("ComplexRow", row.getKey());
        	
        	assertEquals(row, retrievedRow);
		
	}
	
	@Test
	public void testRetrieveWithIndex() throws CassandraThriftClientException, TransformationException {
	    
	    HesperidesRow row = new HesperidesRow(UUID.randomUUID().toString().getBytes(),
		    new HesperidesColumn().addNameComponent("awesome column"),
		    new HesperidesColumn().addNameComponent("awesome and indexed column").addNameComponent(456).setValue("indexFuckyeeeeah").setIndexed(true));
	    
	    integration.store("ComplexRow", row);
	    
	   List<HesperidesRow> retrievedRows = integration.retrieve("ComplexRow", new NodeLocator().n(new HesperidesColumn.StringValue("awesome and indexed column")).n(new HesperidesColumn.IntegerValue(456)), "indexFuckyeeeeah", 0);
	       
	   assertTrue(retrievedRows.contains(row));
	    

	    
	}
	
}
