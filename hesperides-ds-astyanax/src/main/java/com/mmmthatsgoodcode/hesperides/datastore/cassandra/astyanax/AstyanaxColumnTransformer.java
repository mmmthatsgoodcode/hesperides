package com.mmmthatsgoodcode.hesperides.datastore.cassandra.astyanax;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.Set;

import javax.sql.PooledConnection;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.mmmthatsgoodcode.astyanax.HesperidesDynamicCompositeRangeBuilder;
import com.mmmthatsgoodcode.astyanax.HesperidesDynamicCompositeSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.SerializationException;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.datastore.HesperidesRowTransformer;
import com.mmmthatsgoodcode.hesperides.datastore.cassandra.AbstractConfigurableCassandraColumnTransformer;
import com.mmmthatsgoodcode.hesperides.datastore.cassandra.AbstractConfigurableCassandraColumnTransformer.CassandraTypes;
import com.mmmthatsgoodcode.hesperides.datastore.model.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.datastore.model.HesperidesColumnSlice;
import com.mmmthatsgoodcode.hesperides.datastore.model.HesperidesRow;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.model.AbstractColumnImpl;
import com.netflix.astyanax.model.AbstractComposite.Component;
import com.netflix.astyanax.model.ByteBufferRange;
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

public class AstyanaxColumnTransformer extends AbstractConfigurableCassandraColumnTransformer<Column<DynamicComposite>> {

	public static class AstyanaxColumn extends AbstractColumnImpl<DynamicComposite> {

		private Object value = null;
		private Date created;
		private int ttl;

		public AstyanaxColumn(DynamicComposite name, Object value, Date created, int ttl) {
			super(name);
			this.value = value;
			this.created = created;
			this.ttl = ttl;
		}

		@Override
		public ByteBuffer getRawName() {
			return HesperidesDynamicCompositeSerializer.get().toByteBuffer(getName());
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
			return this.value != null;
		}

	}

	public AstyanaxColumnTransformer() {

	}

	/*
	 * Astyanax -> Hesperides ---------------------------
	 */
	@Override
	public HesperidesRow transform(Entry<AbstractType, Set<Column<DynamicComposite>>> columns) throws SerializationException {
	    
	    HesperidesRow hesperidesRow = new HesperidesRow(columns.getKey());
	    
	    for (Column<DynamicComposite> column : columns.getValue()) {
		hesperidesRow.addColumn(cassify(column));
		
	    }
	    
	    return hesperidesRow;
	    
	}

	public HesperidesRow cassify(OperationResult<ColumnList<DynamicComposite>> opResult, AbstractType id)
			throws TransformationException, SerializationException {

		HesperidesRow hesperidesRow = new HesperidesRow(id);

		if (opResult != null && opResult.getResult() != null) {
			for (Column<DynamicComposite> column : opResult.getResult()) {
				hesperidesRow.addColumn(cassify(column));

			}
		}

		return hesperidesRow;

	}


	public HesperidesColumn cassify(Column<DynamicComposite> column) throws SerializationException {

		HesperidesColumn hesperidesColumn = new HesperidesColumn();

		// re-build name
		LOG.debug("Re-building column name from components {}", column.getName().getComponents());
		for (Component component : column.getName().getComponents()) {
			AbstractType nameComponent = AbstractType.wrap(component.getValue());
			LOG.debug("Inferred name component to be {}, from {}", nameComponent.getClass(), component.getValue());
			hesperidesColumn.addNameComponent(nameComponent);

		}

		ByteBuffer value = ByteBuffer.wrap(column.getValue(BytesArraySerializer.get()));

		LOG.debug("Value is {}", value);
		String valueHintAlias = new String(new byte[] {value.get()});
		hesperidesColumn.setIndexed( value.get()==(byte)1?true:false );
		
		LOG.debug("Looking at value hint alias '{}'", valueHintAlias);
		Hesperides.Hint valueHint = Hesperides.Hint.fromStringAlias( valueHintAlias );
		LOG.debug("Resolved to value hint '{}'", valueHint);

		switch (valueHint) {
		
		case OBJECT:
		case NULL:
		case BOOLEAN:
		case INT:
		case FLOAT:
		case LONG:
		case STRING:
			hesperidesColumn.setValue(valueHint.serializer().fromByteBuffer(value));
			break;
		case BYTES:

			break;			
		
		}

		hesperidesColumn.setCreated(new Date(column.getTimestamp()));

		return hesperidesColumn;

	}

	/*
	 * Hesperides -> Astyanax ---------------------------
	 */

	/**
	 * Since a mutation does not take a Column..
	 * 
	 * @param mutation
	 * @param row
	 * @throws SerializationException 
	 */
	
	@Override
	public Entry<AbstractType, Set<Column<DynamicComposite>>> transform(HesperidesRow row) throws TransformationException, SerializationException {
	    
	    Entry<AbstractType, Set<Column<DynamicComposite>>> rowKeyAndColumns = new SimpleEntry<AbstractType, Set<Column<DynamicComposite>>>(
		    row.getKey(), new HashSet<Column<DynamicComposite>>());
	    
	    for (HesperidesColumn hesperidesColumn : row.getColumns()) {
		
		rowKeyAndColumns.getValue().add(cassify(hesperidesColumn));
		
	    }
	    
	    return rowKeyAndColumns;
	    
	}
	
	public void populateColumnListMutation(ColumnListMutation<DynamicComposite> mutation, HesperidesRow row) throws SerializationException {

		for (HesperidesColumn hesperidesColumn : row.getColumns()) {

			List<AbstractType> nameComponents = new ArrayList<AbstractType>(hesperidesColumn.getNameComponents());

			// set timestamp
			mutation.setTimestamp(hesperidesColumn.getCreated().getTime());

			// add hint & encode value
			com.mmmthatsgoodcode.hesperides.core.Serializer valueSerializer = hesperidesColumn.getValue().getSerializer();
			LOG.debug("Value serializer {}, hint: {}", valueSerializer, Hesperides.Hint.fromSerializer(valueSerializer).alias());
			
			
			ByteBuffer value = valueSerializer.toByteBuffer(hesperidesColumn.getValue());
			ByteBuffer valueWithHint = ByteBuffer.allocate(value.capacity()+1);

			valueWithHint.put((Hesperides.Hint.fromSerializer(valueSerializer).alias().getBytes()));
			valueWithHint.put(new byte[] {(byte) (hesperidesColumn.isIndexed()==true?1:0)});
			valueWithHint.put(value);

			
			// add to mutation
			mutation.putColumn(cassify(nameComponents),
					valueWithHint.array(),
					BytesArraySerializer.get(), hesperidesColumn.getTtl());

		}

	}


	public Column<DynamicComposite> cassify(HesperidesColumn hesperidesColumn) throws SerializationException {

		// encode name components
		List<AbstractType> nameComponents = new ArrayList<AbstractType>(hesperidesColumn.getNameComponents());

		// encode value
		// add hint & encode value
		com.mmmthatsgoodcode.hesperides.core.Serializer valueSerializer = hesperidesColumn.getValue().getSerializer();
		LOG.debug("Value serializer {}, hint: {}", valueSerializer, Hesperides.Hint.fromSerializer(valueSerializer));

		ByteBuffer value = valueSerializer.toByteBuffer(hesperidesColumn.getValue());
		ByteBuffer valueWithHint = ByteBuffer.allocate(value.capacity()+2);

		valueWithHint.put(Hesperides.Hint.fromSerializer(valueSerializer).alias().getBytes());
		valueWithHint.put(new byte[] {(byte) (hesperidesColumn.isIndexed()==true?1:0)});
		valueWithHint.put(value);

		return new AstyanaxColumn(cassify(nameComponents), valueWithHint.array(), hesperidesColumn.getCreated(),
				hesperidesColumn.getTtl());

	}

	public DynamicComposite cassify(List<AbstractType> nameComponents) {

		DynamicComposite name = new DynamicComposite();

		for (AbstractType component : nameComponents) {
			Serializer serializer = SerializerTypeInferer.getSerializer(component.getValue());

			name.addComponent(component.getValue(), serializer, serializer.getComparatorType().getTypeName());

		}

		return name;

	}

}
