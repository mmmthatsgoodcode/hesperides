package com.mmmthatsgoodcode.hesperides.datastore.cassandra;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mmmthatsgoodcode.hesperides.ComplexRow;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.SerializationException;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.type.BooleanValue;
import com.mmmthatsgoodcode.hesperides.core.type.FloatValue;
import com.mmmthatsgoodcode.hesperides.core.type.IntegerValue;
import com.mmmthatsgoodcode.hesperides.core.type.StringValue;
import com.mmmthatsgoodcode.hesperides.core.type.WildcardValue;
import com.mmmthatsgoodcode.hesperides.datastore.HesperidesColumnSliceTransformer;
import com.mmmthatsgoodcode.hesperides.datastore.cassandra.EmbeddedCassandraDaemon;
import com.mmmthatsgoodcode.hesperides.datastore.integration.DataStoreIntegration;
import com.mmmthatsgoodcode.hesperides.datastore.integration.DataStoreIntegrationException;
import com.mmmthatsgoodcode.hesperides.datastore.integration.DataStoreIntegrationTest;
import com.mmmthatsgoodcode.hesperides.datastore.model.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.datastore.model.HesperidesColumnSlice;
import com.mmmthatsgoodcode.hesperides.datastore.model.HesperidesRow;
import com.mmmthatsgoodcode.utils.other.RiggedRand.ParticipantDistributionException;

public abstract class CassandraIntegrationTest extends DataStoreIntegrationTest {

    	protected final static String cassandraNode = System.getProperty("cassandra", "embed");
	protected DataStoreIntegration integration = null;
	protected static EmbeddedCassandraDaemon cassandra = null;
	protected final Logger LOG;
	
	public CassandraIntegrationTest() {
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
	public void testSimpleStorePartialRetrieve() throws DataStoreIntegrationException, TransformationException, ParticipantDistributionException, SerializationException {
		
		HesperidesRow row = new HesperidesRow(new StringValue( UUID.randomUUID().toString() ));
		row.addColumn(new HesperidesColumn().addNameComponent("foo").setValue(new BooleanValue(true)));
		row.addColumn(new HesperidesColumn().addNameComponent("no-row").setValue(new StringValue("no-value")));
		
    	integration.store("ComplexRow", row);
            	
    	HesperidesColumn firstColumn = row.getColumns().get(0);
    	HesperidesRow singleColumnRow = new HesperidesRow(row.getKey());
    	singleColumnRow.addColumn(firstColumn);
    	
    	HesperidesRow retrievedRow = integration.retrieveMatching("ComplexRow", row.getKey(), Arrays.asList( new HesperidesColumnSlice[] { new HesperidesColumnSlice().n(firstColumn.getNameComponents())} ));
            	
    	assertEquals(singleColumnRow, retrievedRow);
		
	}
	
	@Test
	public void testStoreRangeRetrieve() throws DataStoreIntegrationException, TransformationException, ParticipantDistributionException, SerializationException {
		
		HesperidesRow row = new HesperidesRow( new StringValue( UUID.randomUUID().toString() ));
		row.addColumn(new HesperidesColumn().addNameComponent("foo").addNameComponent(123).setValue(new BooleanValue(true)));
		row.addColumn(new HesperidesColumn().addNameComponent("foo").addNameComponent(true).addNameComponent(3.14f).setValue(new StringValue("yeah!")));
		row.addColumn(new HesperidesColumn().addNameComponent("no-row").setValue(new StringValue("no-value")));
		row.addColumn(new HesperidesColumn().addNameComponent("foo").addNameComponent(true).setValue(new BooleanValue(true)));
		row.addColumn(new HesperidesColumn().addNameComponent("bar").addNameComponent(42).addNameComponent(3.15f).setValue(new StringValue("ooh yeah!")));
		row.addColumn(new HesperidesColumn().addNameComponent("bar").addNameComponent(42).addNameComponent("baz").setValue(new StringValue("ooh yeah!!!!")));
		row.addColumn(new HesperidesColumn().addNameComponent("bar").addNameComponent(43).addNameComponent("baz").setValue(new StringValue("nope")));
		row.addColumn(new HesperidesColumn().addNameComponent("bar").addNameComponent("definitely not").addNameComponent("baz").setValue(new StringValue("nope")));

		
    	integration.store("ComplexRow", row);

    	HesperidesRow expectedRow = new HesperidesRow(row.getKey());
    	expectedRow.addColumn(row.getColumn(new StringValue("foo"), new IntegerValue(123)));
    	expectedRow.addColumn(row.getColumn(new StringValue("foo"), new BooleanValue(true)));
    	expectedRow.addColumn(row.getColumn(new StringValue("foo"), new BooleanValue(true), new FloatValue(3.14f)));
    	expectedRow.addColumn(row.getColumn(new StringValue("bar"), new IntegerValue(42), new FloatValue(3.15f)));
    	expectedRow.addColumn(row.getColumn(new StringValue("bar"), new IntegerValue(42), new StringValue("baz")));
    	
    	HesperidesRow retrievedRow = integration.retrieveMatching("ComplexRow", row.getKey(), Arrays.asList( new HesperidesColumnSlice[] {
    			new HesperidesColumnSlice().n(new StringValue("foo")).n(new WildcardValue()),
    			new HesperidesColumnSlice().n(new StringValue("bar")).n(new IntegerValue(42)).n(new WildcardValue())}));
  	
    	assertEquals(expectedRow, retrievedRow);

	}	
	
	@Test
	public void testStoreDeepRangeRetrieve() throws DataStoreIntegrationException, TransformationException, ParticipantDistributionException, SerializationException {
		
		HesperidesRow row = new HesperidesRow(new StringValue( UUID.randomUUID().toString() ));
		row.addColumn(new HesperidesColumn().addNameComponent("foo").addNameComponent(123).setValue(new BooleanValue(true)));
		row.addColumn(new HesperidesColumn().addNameComponent("foo").addNameComponent(true).addNameComponent(3.14f).setValue(new StringValue("yeah!")));
		row.addColumn(new HesperidesColumn().addNameComponent("no-row").setValue(new StringValue("no-value")));
		row.addColumn(new HesperidesColumn().addNameComponent("foo").addNameComponent(true).setValue(new BooleanValue(true)));

		
    	integration.store("ComplexRow", row);

    	System.out.println(row.getColumns());
    	
    	HesperidesRow expectedRow = new HesperidesRow(row.getKey());
    	expectedRow.addColumn(row.getColumn(new StringValue("foo"), new BooleanValue(true)));
    	expectedRow.addColumn(row.getColumn(new StringValue("foo"), new BooleanValue(true), new FloatValue(3.14f)));
    	
    	HesperidesRow retrievedRow = integration.retrieveMatching("ComplexRow", row.getKey(), Arrays.asList( new HesperidesColumnSlice[] { new HesperidesColumnSlice().n(new StringValue("foo")).n(new BooleanValue(true)).n(new WildcardValue())}));
  	
    	assertEquals(expectedRow, retrievedRow);

	}
	
	@Test
	public void testExists() throws DataStoreIntegrationException, TransformationException, SerializationException, ParticipantDistributionException {
		
    	HesperidesRow row = ComplexRow.generate(1).get(0);
    	integration.store("ComplexRow", row);

    	assertTrue( integration.exists("ComplexRow", row.getKey()));
    	    	
	}
	
	@Test
	public void testSimpleStoreRetrieve() throws ParticipantDistributionException, DataStoreIntegrationException, TransformationException, InterruptedException, SerializationException {
		
        	HesperidesRow row = ComplexRow.generate(1).get(0);
        	integration.store("ComplexRow", row);
                	
        	HesperidesRow retrievedRow = integration.retrieve("ComplexRow", row.getKey());
        	
        	assertEquals(row, retrievedRow);
		
	}
	
	@Test
	public void testRetrieveWithIndex() throws DataStoreIntegrationException, TransformationException, SerializationException {
	    
	    HesperidesRow row = new HesperidesRow(new StringValue( UUID.randomUUID().toString() ),
		    new HesperidesColumn().addNameComponent("awesome column"),
		    new HesperidesColumn().addNameComponent("awesome and indexed column").addNameComponent(456).setValue(new StringValue("indexFuckyeeeeah")).setIndexed(true));
	    
	    integration.store("ComplexRow", row);
	    
	   Set<HesperidesRow> retrievedRows = integration.retrieve("ComplexRow", new HesperidesColumnSlice().n(new StringValue("awesome and indexed column")).n(new IntegerValue(456)), new StringValue("indexFuckyeeeeah"), 0);
	       	   
	   assertTrue(retrievedRows.contains(row));
	    
	}
	
	@Test
	public void testRetrieveWithMultipleIndexesOrRelation() throws DataStoreIntegrationException, TransformationException, SerializationException {
		
		StringValue keyA = new StringValue( UUID.randomUUID().toString() );
		StringValue keyB = new StringValue( UUID.randomUUID().toString() );
	    HesperidesRow rowA = new HesperidesRow(keyA,
			    new HesperidesColumn().addNameComponent("awesome column"),
			    new HesperidesColumn().addNameComponent(keyA+" - foo").addNameComponent("index").setValue(new StringValue("fuckyeeeeah")).setIndexed(true));
	    
	    
	    HesperidesRow rowB = new HesperidesRow( keyB,
			    new HesperidesColumn().addNameComponent("pretty cool column"),
			    new HesperidesColumn().addNameComponent("pretty cool column").addNameComponent("anotherPrettyCoolColumn").setValue(new IntegerValue(42)),
			    new HesperidesColumn().addNameComponent(keyB+" - nothing here").addNameComponent(123).setValue(new StringValue("yeah.")).setIndexed(true));
	    
	    integration.store("ComplexRow", Arrays.asList(new HesperidesRow[] {rowA, rowB}));
	    
	    Multimap<HesperidesColumnSlice, AbstractType> indexes = HashMultimap.create();
	    
	    indexes.put( new HesperidesColumnSlice().n(new StringValue(keyA+" - foo")).n(new StringValue("index")), new StringValue("fuckyeeeeah"));
	    indexes.put( new HesperidesColumnSlice().n(new StringValue(keyB+" - nothing here")).n(new IntegerValue(123)), new StringValue("yeah."));
	    
	    Set<HesperidesRow> results = integration.retrieve("ComplexRow", indexes, HesperidesColumnSlice.Relation.OR);
	    
	    assertEquals(2, results.size());
	    assertTrue(results.contains(rowA) && results.contains(rowB));
	    
	}
	
	@Test
	public void testRetrieveWithMultipleIndexesAndRelation() throws DataStoreIntegrationException, TransformationException, SerializationException {
		
		AbstractType keyA = new StringValue( UUID.randomUUID().toString() );
		AbstractType keyB = new StringValue( UUID.randomUUID().toString() );
		AbstractType keyC = new StringValue( UUID.randomUUID().toString() );
	    HesperidesRow rowA = new HesperidesRow(keyA,
			    new HesperidesColumn().addNameComponent("awesome column"),
			    new HesperidesColumn().addNameComponent(keyC+" - foo").addNameComponent("index").setValue(new StringValue("fuckyeeeeah")).setIndexed(true));
	    
	    
	    HesperidesRow rowB = new HesperidesRow( keyB,
			    new HesperidesColumn().addNameComponent("pretty cool column"),
			    new HesperidesColumn().addNameComponent("pretty cool column").addNameComponent("anotherPrettyCoolColumn").setValue(new IntegerValue(42)),
			    new HesperidesColumn().addNameComponent(keyB+" - nothing here").addNameComponent(123).setValue(new StringValue("yeah.")).setIndexed(true),
			    new HesperidesColumn().addNameComponent(keyC+" - foo").addNameComponent("index").setValue(new StringValue("fuckyeeeeah")).setIndexed(true));
	    
	    integration.store("ComplexRow", Arrays.asList(new HesperidesRow[] {rowA, rowB}));
	    
	    Multimap<HesperidesColumnSlice, AbstractType> indexes = HashMultimap.create();
	    
	    indexes.put( new HesperidesColumnSlice().n(new StringValue(keyC+" - foo")).n(new StringValue("index")), new StringValue("fuckyeeeeah"));
	    indexes.put( new HesperidesColumnSlice().n(new StringValue(keyB+" - nothing here")).n(new IntegerValue(123)), new StringValue("yeah."));
	    
	    Set<HesperidesRow> results = integration.retrieve("ComplexRow", indexes, HesperidesColumnSlice.Relation.AND);
	    
	    assertEquals(1, results.size());
	    assertTrue(results.contains(rowB));
	    
	}
	
	@Test
	public void testCompleteDelete() throws DataStoreIntegrationException, TransformationException, ParticipantDistributionException, SerializationException {
		
		HesperidesRow row = ComplexRow.generate(1).get(0);
    	integration.store("ComplexRow", row);
    	
    	integration.delete("ComplexRow", row.getKey());
		
    	assertTrue(integration.retrieve("ComplexRow", row.getKey()).getColumns().size() == 0);
		
		
	}
	
	@Test
	public void testCompleteDeleteCleansUpIndexes() throws DataStoreIntegrationException, TransformationException, ParticipantDistributionException, SerializationException {
		
		HesperidesRow row = ComplexRow.generate(1).get(0);
    	integration.store("ComplexRow", row);
    	
    	List<HesperidesColumn> indexedColumns = row.getIndexedColumns();
    	
    	integration.delete("ComplexRow", row.getKey());
		
    	assertTrue(integration.retrieveRowKeysByIndex("ComplexRow", new HesperidesColumnSlice().n(indexedColumns.get(0).getNameComponents()), indexedColumns.get(0).getValue(), 100).size() == 0);
		
		
	}
	
	@Test
	public void testPartialDeleteColumnList() throws DataStoreIntegrationException, TransformationException, SerializationException {
		
		HesperidesRow row = new HesperidesRow(new StringValue( UUID.randomUUID().toString() ));
		row.addColumn(new HesperidesColumn().addNameComponent("foo").addNameComponent(123).setValue(new BooleanValue(true)));
		row.addColumn(new HesperidesColumn().addNameComponent("foo").addNameComponent(true).addNameComponent(3.14f).setValue(new StringValue("yeah!")));
		row.addColumn(new HesperidesColumn().addNameComponent("no-row").setValue(new StringValue("no-value")));
		row.addColumn(new HesperidesColumn().addNameComponent("foo").addNameComponent(true).setValue(new BooleanValue(true)));
		
		integration.store("ComplexRow", row);
		integration.delete("ComplexRow", row.getKey(), Arrays.asList(new HesperidesColumn[] {new HesperidesColumn().addNameComponent("no-row").setValue(new StringValue("no-value"))}));
		
		HesperidesRow retrievedRow = integration.retrieve("ComplexRow", row.getKey());
		
		assertEquals(row.diff(retrievedRow), Arrays.asList(new HesperidesColumn[] {new HesperidesColumn().addNameComponent("no-row").setValue(new StringValue("no-value"))}));
		
	}
	
	@Test
	public void testPartialDeleteColumnSlice() throws DataStoreIntegrationException, TransformationException, SerializationException {
		
		HesperidesRow row = new HesperidesRow(new StringValue( UUID.randomUUID().toString() ));
		row.addColumn(new HesperidesColumn().addNameComponent("foo").addNameComponent(123).setValue(new BooleanValue(true)));
		row.addColumn(new HesperidesColumn().addNameComponent("foo").addNameComponent(true).addNameComponent(3.14f).setValue(new StringValue("yeah!")));
		row.addColumn(new HesperidesColumn().addNameComponent("no-row").setValue(new StringValue("no-value")));
		row.addColumn(new HesperidesColumn().addNameComponent("foo").addNameComponent(true).setValue(new BooleanValue(true)));
		
		integration.store("ComplexRow", row);
		integration.deleteMatching("ComplexRow", row.getKey(), Arrays.asList( new HesperidesColumnSlice[] { new HesperidesColumnSlice().n(new StringValue("foo")).n(new BooleanValue(true)).n(new WildcardValue())}));

		HesperidesRow retrievedRow = integration.retrieve("ComplexRow", row.getKey());

		HesperidesRow expectedRow = new HesperidesRow(row.getKey());
		expectedRow.addColumn(new HesperidesColumn().addNameComponent("foo").addNameComponent(123).setValue(new BooleanValue(true)));
		expectedRow.addColumn(new HesperidesColumn().addNameComponent("no-row").setValue(new StringValue("no-value")));

		assertEquals(expectedRow, retrievedRow);
		
	}
	
}
