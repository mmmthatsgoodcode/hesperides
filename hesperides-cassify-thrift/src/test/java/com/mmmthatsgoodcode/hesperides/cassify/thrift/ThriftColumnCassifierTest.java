package com.mmmthatsgoodcode.hesperides.cassify.thrift;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.junit.Before;
import org.junit.Test;

import com.mmmthatsgoodcode.hesperides.cassify.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.cassify.HesperidesRow;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;

public class ThriftColumnCassifierTest {

	ThriftColumnCassifier cassifier = new ThriftColumnCassifier();
	
	List<HesperidesRow> rows = new ArrayList<HesperidesRow>();
	
	@Before
	public void setUp() {
		
		HesperidesRow strRow = new HesperidesRow("strRow");
		rows.add(strRow);
		
		HesperidesColumn strValueColumn = new HesperidesColumn();
		
		strValueColumn = new HesperidesColumn();
		strValueColumn.addNameComponent(new Date());
		strValueColumn.addNameComponent(3.14f);
		strValueColumn.addNameComponent(123);
		strValueColumn.addNameComponent(9999999999999999l);
		strValueColumn.addNameComponent("foo! bar!");
		strValueColumn.addNullNameComponent();
		strValueColumn.setValue("String value and stuff");
		
		strRow.addColumn(strValueColumn);
		
		HesperidesRow intRow = new HesperidesRow("intRow");
		rows.add(intRow);
		
		HesperidesColumn intValueColumn = new HesperidesColumn();
		
		intValueColumn = new HesperidesColumn();
		intValueColumn.addNameComponent(123);
		intValueColumn.setValue(456);
		
		intRow.addColumn(intValueColumn);

		HesperidesColumn longValueColumn = new HesperidesColumn();
		
		longValueColumn = new HesperidesColumn();
		longValueColumn.addNameComponent(123);
		longValueColumn.setValue(456l);
		
		intRow.addColumn(longValueColumn);
		
		HesperidesColumn floatValueColumn = new HesperidesColumn();
		
		floatValueColumn = new HesperidesColumn();
		floatValueColumn.addNameComponent(123);
		floatValueColumn.setValue(456f);
		
		intRow.addColumn(floatValueColumn);
		
		HesperidesColumn booleanValueColumn = new HesperidesColumn();
		
		booleanValueColumn = new HesperidesColumn();
		booleanValueColumn.addNameComponent(123);
		booleanValueColumn.setValue(true);
		
		intRow.addColumn(booleanValueColumn);
		
		
	}
	
	@Test
	public void testCassify() throws TransformationException {
		
		for (HesperidesRow row:rows) {

			Entry<String, List<Column>> thriftRow = cassifier.cassify(row);
			HesperidesRow deserializedRow = cassifier.cassify(thriftRow);

			System.out.print(deserializedRow+"\n");
			System.out.println(row);
			
			assertTrue(row.equals(deserializedRow));			
			
		}
		

		
	}
	
}
