package com.mmmthatsgoodcode.hesperides.cassify.astyanax;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.mmmthatsgoodcode.hesperides.cassify.astyanax.AstyanaxCassifier.HesperidesDynamicComposite;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.DynamicComposite;

public class AstyanaxCassifierTest {

	private AstyanaxCassifier cassifier = new AstyanaxCassifier();
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
		intValueColumn.addNameComponent(Integer.class.getName());
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
		
		for(HesperidesRow row:this.rows) {

			Entry<String, List<Column<HesperidesDynamicComposite>>> astyanaxRow = cassifier.cassify(row);
			System.out.println(astyanaxRow);

			
			HesperidesRow transformedRow = cassifier.cassify(astyanaxRow.getValue(), astyanaxRow.getKey());
			
			System.out.println(row);
			System.out.println(transformedRow);
			
			
		}
		
		
		
	}
	
}
