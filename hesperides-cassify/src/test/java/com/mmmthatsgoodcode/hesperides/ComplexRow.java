package com.mmmthatsgoodcode.hesperides;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn.AbstractType;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;
import com.mmmthatsgoodcode.utils.other.RiggedRand;
import com.mmmthatsgoodcode.utils.other.RiggedRand.ParticipantDistributionException;

public class ComplexRow extends HesperidesRow {
	
	public ComplexRow(String key) {
		super(key.getBytes());
	}
	
	public ComplexRow generateColumns() throws ParticipantDistributionException {
		
		Random rand = new Random();
		
		RiggedRand<Class<? extends AbstractType>> rrand = new RiggedRand<Class<? extends AbstractType>>();
		rrand.add(
			new RiggedRand.Participant<Class<? extends AbstractType>>(20, HesperidesColumn.IntegerValue.class),
				new RiggedRand.Participant<Class<? extends AbstractType>>(50, HesperidesColumn.StringValue.class),
				new RiggedRand.Participant<Class<? extends AbstractType>>(10, HesperidesColumn.LongValue.class),
				new RiggedRand.Participant<Class<? extends AbstractType>>(10, HesperidesColumn.FloatValue.class),
				new RiggedRand.Participant<Class<? extends AbstractType>>(10, HesperidesColumn.BooleanValue.class));
		
		for(int c=1;c<100;c++) {
			
			HesperidesColumn column = new HesperidesColumn();
			
			for(int n=1;n<=(rand.nextInt(5)+1);n++) {
				
				Class<? extends AbstractType> componentClass = rrand.shuffle();
				if (componentClass.equals(HesperidesColumn.IntegerValue.class)) column.addNameComponent( rand.nextInt(99999) );
				else if (componentClass.equals(HesperidesColumn.StringValue.class)) column.addNameComponent( UUID.randomUUID().toString() );
				else if (componentClass.equals(HesperidesColumn.LongValue.class)) column.addNameComponent( rand.nextLong() );
				else if (componentClass.equals(HesperidesColumn.FloatValue.class)) column.addNameComponent( rand.nextFloat()*rand.nextInt(99) );
				else if (componentClass.equals(HesperidesColumn.BooleanValue.class)) column.addNameComponent( (rand.nextInt(10)+1)%2==0?true:false );
				
			}
			
			column.setIndexed( (rand.nextInt(10)+1)%2==0?true:false );

			if ((rand.nextInt(10)+1)%2 == 0) column.setValue( UUID.randomUUID().toString() );
			else column.setValue(rand.nextInt(9999) );
			
			addColumn(column);
			
		}
		
		return this;
	}
	
	public static List<ComplexRow> generate(int howMany) throws ParticipantDistributionException {
		
		List<ComplexRow> rows = new ArrayList<ComplexRow>();
		
		for(int i=1; i <= howMany; i++) {
			
			rows.add( new ComplexRow(UUID.randomUUID().toString()).generateColumns() );
			
		}
		
		
		return rows;
		
	}
	
}
