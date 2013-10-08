package com.mmmthatsgoodcode.hesperides.cassify.astyanax;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableBiMap;
import com.mmmthatsgoodcode.hesperides.cassify.AbstractConfigurableCassifier;
import com.mmmthatsgoodcode.hesperides.cassify.AbstractConfigurableCassifier.CassandraTypes;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn.IntegerValue;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.model.AbstractColumnImpl;
import com.netflix.astyanax.model.AbstractComposite.Component;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.DynamicComposite;
import com.netflix.astyanax.serializers.AbstractSerializer;
import com.netflix.astyanax.serializers.AsciiSerializer;
import com.netflix.astyanax.serializers.BigDecimalSerializer;
import com.netflix.astyanax.serializers.BigIntegerSerializer;
import com.netflix.astyanax.serializers.BooleanSerializer;
import com.netflix.astyanax.serializers.ByteBufferSerializer;
import com.netflix.astyanax.serializers.ByteSerializer;
import com.netflix.astyanax.serializers.BytesArraySerializer;
import com.netflix.astyanax.serializers.ComparatorType;
import com.netflix.astyanax.serializers.CompositeSerializer;
import com.netflix.astyanax.serializers.DateSerializer;
import com.netflix.astyanax.serializers.DoubleSerializer;
import com.netflix.astyanax.serializers.DynamicCompositeSerializer;
import com.netflix.astyanax.serializers.FloatSerializer;
import com.netflix.astyanax.serializers.Int32Serializer;
import com.netflix.astyanax.serializers.IntegerSerializer;
import com.netflix.astyanax.serializers.LongSerializer;
import com.netflix.astyanax.serializers.SerializerTypeInferer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.serializers.TimeUUIDSerializer;
import com.netflix.astyanax.serializers.UUIDSerializer;
import com.netflix.astyanax.shallows.EmptyColumn;
import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.Serializer;

public class AstyanaxCassifier extends AbstractConfigurableCassifier<Column<HesperidesDynamicComposite>> {
	
	
	public static class AstyanaxColumn extends AbstractColumnImpl<HesperidesDynamicComposite> {

		private Object value = null;
		private Date created;
		private int ttl;
		
		
		public AstyanaxColumn(HesperidesDynamicComposite name, Object value, Date created, int ttl) {
			super(name);
//			System.out.println("Incoming date "+created.getTime());
			this.value = value;
			this.created = created;
			this.ttl = ttl;
		}

		@Override
		public ByteBuffer getRawName() {
			return DynamicCompositeSerializer.get().toByteBuffer(getName());
		}

		@Override
		public long getTimestamp() {
			return created.getTime();
		}

		@Override
		public <V> V getValue(Serializer<V> valSer) {
			return (V) this.value;
		}

		@Override
		public int getTtl() {
			return 0;
		}

		@Override
		public boolean hasValue() {
			return this.value!=null;
		}
		
	}
	

	public AstyanaxCassifier() {

		
	}
	
	/* Astyanax -> Hesperides
	--------------------------- */

	public HesperidesRow cassify(OperationResult<ColumnList<HesperidesDynamicComposite>> opResult, String id)
			throws TransformationException {
		
		HesperidesRow hesperidesRow = new HesperidesRow(id);
		
		if (opResult != null && opResult.getResult() != null) {
			for (Column<HesperidesDynamicComposite> column:opResult.getResult()) {
				hesperidesRow.addColumn(cassify(column));
				
			}
		}
		
		return hesperidesRow;
		
	}
	
	public HesperidesRow cassify(Entry<String, List<Column<HesperidesDynamicComposite>>> columns) {
		
		HesperidesRow hesperidesRow = new HesperidesRow(columns.getKey());

		for (Column<HesperidesDynamicComposite> column:columns.getValue()) {
			hesperidesRow.addColumn(cassify(column));
			
		}
		
		return hesperidesRow;
		
	}
	
	public HesperidesColumn cassify(Column<HesperidesDynamicComposite> column) {
		
		HesperidesColumn hesperidesColumn = new HesperidesColumn();
		
		// re-build name
		for(Component component:column.getName().getComponents()) {
			hesperidesColumn.addNameComponent(AbstractType.infer(component.getValue()));
			
		}
		
		// re-build value from value hint in last name component
		IntegerValue valueTypeHint = (IntegerValue) hesperidesColumn.getNameComponents().remove(hesperidesColumn.getNameComponents().size()-1);		
		
		switch(valueTypeHint.getValue()) {
		
			case Hesperides.Hints.OBJECT:
			case Hesperides.Hints.NULL:
				hesperidesColumn.setNullValue();
			break;
			case Hesperides.Hints.BOOLEAN:
				hesperidesColumn.setValue((Boolean) Hesperides.Hints.hintToSerializer(valueTypeHint.getValue()).fromByteBuffer( ByteBuffer.wrap( column.getValue(BytesArraySerializer.get()))));
			break;
			case Hesperides.Hints.INT:
				hesperidesColumn.setValue((Integer) Hesperides.Hints.hintToSerializer(valueTypeHint.getValue()).fromByteBuffer( ByteBuffer.wrap( column.getValue(BytesArraySerializer.get()))));
			break;
			case Hesperides.Hints.FLOAT:
				hesperidesColumn.setValue((Float) Hesperides.Hints.hintToSerializer(valueTypeHint.getValue()).fromByteBuffer( ByteBuffer.wrap( column.getValue(BytesArraySerializer.get()))));
			break;
			case Hesperides.Hints.LONG:
				hesperidesColumn.setValue((Long) Hesperides.Hints.hintToSerializer(valueTypeHint.getValue()).fromByteBuffer( ByteBuffer.wrap( column.getValue(BytesArraySerializer.get()))));
			break;
			case Hesperides.Hints.STRING:
				hesperidesColumn.setValue((String) Hesperides.Hints.hintToSerializer(valueTypeHint.getValue()).fromByteBuffer( ByteBuffer.wrap( column.getValue(BytesArraySerializer.get()))));
			break;
			case Hesperides.Hints.BYTES:
				
			break;
			
		
		}
			
		hesperidesColumn.setCreated(new Date(column.getTimestamp()));
								
		return hesperidesColumn;
		
	}
	
	
	/* Hesperides -> Astyanax
	--------------------------- */
	
	/**
	 * Since a mutation does not take a Column..
	 * @param mutation
	 * @param row
	 */
	public void populateColumnListMutation(ColumnListMutation<HesperidesDynamicComposite> mutation, HesperidesRow row) {
		
		for (HesperidesColumn hesperidesColumn:row.getColumns()) {

			List<AbstractType> nameComponents = new ArrayList<AbstractType>(hesperidesColumn.getNameComponents());
			
			// append value hint
			Integer valueHint = Hesperides.Hints.typeToHint( hesperidesColumn.getValue().getValue()==null?null:hesperidesColumn.getValue().getValue().getClass() );
			nameComponents.add(new IntegerValue(valueHint));

			// encode value
			com.mmmthatsgoodcode.hesperides.core.Serializer valueSerializer = hesperidesColumn.getValue().getSerializer();
			
			// set timestamp
			mutation.setTimestamp(hesperidesColumn.getCreated().getTime());
			
			// add to mutation
			mutation.putColumn( cassify( nameComponents ), valueSerializer.toByteBuffer( hesperidesColumn.getValue().getValue() ).array(), BytesArraySerializer.get(), hesperidesColumn.getTtl());			

		}
		
	}

	public Entry<String, List<Column<HesperidesDynamicComposite>>> cassify(HesperidesRow row)
			throws TransformationException {
		
		Entry<String, List<Column<HesperidesDynamicComposite>>> rowKeyAndColumns = new SimpleEntry<String, List<Column<HesperidesDynamicComposite>>>(row.getKey(), new ArrayList<Column<HesperidesDynamicComposite>>());
		
		for (HesperidesColumn hesperidesColumn:row.getColumns()) {
			
			rowKeyAndColumns.getValue().add( cassify(hesperidesColumn) );
			
		}
		
		return rowKeyAndColumns;
		
	}
	
	public Column<HesperidesDynamicComposite> cassify(HesperidesColumn hesperidesColumn) {
	
		// encode name components
		List<AbstractType> nameComponents = new ArrayList<AbstractType>(hesperidesColumn.getNameComponents());
		
		// append value hint
		Integer valueHint = Hesperides.Hints.typeToHint( hesperidesColumn.getValue().getValue()==null?null:hesperidesColumn.getValue().getValue().getClass() );
		nameComponents.add(new IntegerValue(valueHint));
		
		// encode value
		com.mmmthatsgoodcode.hesperides.core.Serializer valueSerializer = hesperidesColumn.getValue().getSerializer();
		
		
		return new AstyanaxColumn(cassify( nameComponents ), valueSerializer.toByteBuffer( hesperidesColumn.getValue().getValue() ).array(), hesperidesColumn.getCreated(), hesperidesColumn.getTtl());	
		
	}
	
	public HesperidesDynamicComposite cassify(List<AbstractType> nameComponents) {
		
		HesperidesDynamicComposite name = new HesperidesDynamicComposite();
		
		for (AbstractType component:nameComponents) {
			name.addComponent(component.getValue(), SerializerTypeInferer.getSerializer(component.getValue()));
			
		}
		
		return name;
		
	}
	

}
