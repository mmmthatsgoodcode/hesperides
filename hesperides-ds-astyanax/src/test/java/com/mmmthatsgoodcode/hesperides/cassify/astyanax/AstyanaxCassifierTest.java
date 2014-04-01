package com.mmmthatsgoodcode.hesperides.cassify.astyanax;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.SerializationException;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.type.BooleanValue;
import com.mmmthatsgoodcode.hesperides.core.type.FloatValue;
import com.mmmthatsgoodcode.hesperides.core.type.IntegerValue;
import com.mmmthatsgoodcode.hesperides.core.type.LongValue;
import com.mmmthatsgoodcode.hesperides.core.type.StringValue;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.DynamicComposite;

import java.util.AbstractMap.SimpleEntry;

public class AstyanaxCassifierTest {

	private AstyanaxCassifier cassifier = new AstyanaxCassifier();
	List<HesperidesRow> rows = new ArrayList<HesperidesRow>();

	@Before
	public void setUp() {
		
		HesperidesRow strRow = new HesperidesRow(new StringValue("strRow"));
		rows.add(strRow);
		
		HesperidesColumn strValueColumn = new HesperidesColumn();
		
		strValueColumn = new HesperidesColumn();
		strValueColumn.addNameComponent(3.14f);
		strValueColumn.addNameComponent(123);
		strValueColumn.addNameComponent(9999999999999999l);
		strValueColumn.addNameComponent("foo! bar!");
		strValueColumn.setValue(new StringValue("String value and stuff"));
		strRow.addColumn(strValueColumn);
		
		HesperidesRow intRow = new HesperidesRow(new StringValue("intRow"));
		rows.add(intRow);
		
		HesperidesColumn intValueColumn = new HesperidesColumn();
		
		intValueColumn = new HesperidesColumn();
		intValueColumn.addNameComponent(Integer.class.getName());
		intValueColumn.addNameComponent(123);
		intValueColumn.setValue(new IntegerValue(456));
		
		intRow.addColumn(intValueColumn);

		HesperidesColumn longValueColumn = new HesperidesColumn();
		
		longValueColumn = new HesperidesColumn();
		longValueColumn.addNameComponent(123);
		longValueColumn.setValue(new LongValue(456l));
		
		intRow.addColumn(longValueColumn);
		
		HesperidesColumn floatValueColumn = new HesperidesColumn();
		
		floatValueColumn = new HesperidesColumn();
		floatValueColumn.addNameComponent(123);
		floatValueColumn.setValue(new FloatValue(456f));
		
		intRow.addColumn(floatValueColumn);

		
		HesperidesColumn booleanValueColumn = new HesperidesColumn();
		
		booleanValueColumn = new HesperidesColumn();
		booleanValueColumn.addNameComponent(3.14f);
		booleanValueColumn.setValue(new BooleanValue(true));
		
		intRow.addColumn(booleanValueColumn);
		
	}
	
	@Test
	public void testCassify() throws TransformationException, SerializationException {
		
		for(HesperidesRow row:this.rows) {

			Entry<AbstractType, Set<Column<DynamicComposite>>> astyanaxRow = cassifier.cassify(row);
//			System.out.println(astyanaxRow);

			// astyanaxRow.getValue(), astyanaxRow.getKey()
			HesperidesRow transformedRow = cassifier.cassify(new SimpleEntry<AbstractType, Set<Column<DynamicComposite>>>(astyanaxRow.getKey(), astyanaxRow.getValue()));
			
//			System.out.println(row);
//			System.out.println(transformedRow);
			
			assertTrue(row.equals(transformedRow));

			
			
		}
			
	}
	
	
	
}
