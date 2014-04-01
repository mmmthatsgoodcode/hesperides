package com.mmmthatsgoodcode.hesperides.datastore.cassandra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;


public abstract class AbstractConfigurableCassandraColumnTransformer<C> implements ConfigurableCassandraColumnTransformer<C> {
	
	public static class CassandraTypes { // aka comparators
		
		
		public static final String AsciiType = "org.apache.cassandra.db.marshal.AsciiType";
		public static final String BooleanType = "org.apache.cassandra.db.marshal.BooleanType";
		public static final String BytesType = "org.apache.cassandra.db.marshal.BytesType";
		public static final String DateType = "org.apache.cassandra.db.marshal.DateType";
		public static final String DecimalType = "org.apache.cassandra.db.marshal.DecimalType";
		public static final String DoubleType = "org.apache.cassandra.db.marshal.DoubleType";		
		public static final String EmptyType = "org.apache.cassandra.db.marshal.EmptyType";
		public static final String FloatType = "org.apache.cassandra.db.marshal.FloatType";
		public static final String Int32Type = "org.apache.cassandra.db.marshal.Int32Type";
		public static final String IntegerType = "org.apache.cassandra.db.marshal.IntegerType";
		public static final String LexicalUUIDType = "org.apache.cassandra.db.marshal.LexicalUUIDType";
		public static final String LongType = "org.apache.cassandra.db.marshal.LongType";
		public static final String TimeUUIDType = "org.apache.cassandra.db.marshal.TimeUUIDType";
		public static final String UUIDType = "org.apache.cassandra.db.marshal.UUIDType";
		public static final String UTF8Type = "org.apache.cassandra.db.marshal.UTF8Type";

	}
	
	public static final ImmutableBiMap<String, Hesperides.Hint> HINT_TO_CASSANDRA_TYPE = new ImmutableBiMap.Builder<String, Hesperides.Hint>()
			.put(CassandraTypes.UTF8Type, Hesperides.Hint.STRING)
			.put(CassandraTypes.Int32Type, Hesperides.Hint.INT32)
			.put(CassandraTypes.IntegerType, Hesperides.Hint.INT)
			.put(CassandraTypes.FloatType, Hesperides.Hint.FLOAT)
			.put(CassandraTypes.LongType, Hesperides.Hint.LONG)
			.put(CassandraTypes.BooleanType, Hesperides.Hint.BOOLEAN)
			.put(CassandraTypes.BytesType, Hesperides.Hint.BYTES)
			.put(CassandraTypes.EmptyType, Hesperides.Hint.NULL)
			.put(CassandraTypes.DateType, Hesperides.Hint.DATE)
			.put(CassandraTypes.LexicalUUIDType, Hesperides.Hint.LEXICALUUID)
			.put(CassandraTypes.TimeUUIDType, Hesperides.Hint.TIMEUUID)
			.put(CassandraTypes.UUIDType, Hesperides.Hint.UUID)
			.build();
	
	public static final BiMap<String, String> DEFAULT_TYPE_ALIASES = new ImmutableBiMap.Builder<String, String>()
			.put(CassandraTypes.UTF8Type, Hesperides.Hint.STRING.alias())
			.put(CassandraTypes.IntegerType, Hesperides.Hint.INT.alias())
			.put(CassandraTypes.Int32Type, Hesperides.Hint.INT32.alias())
			.put(CassandraTypes.FloatType, Hesperides.Hint.FLOAT.alias())
			.put(CassandraTypes.LongType, Hesperides.Hint.LONG.alias())
			.put(CassandraTypes.BooleanType, Hesperides.Hint.BOOLEAN.alias())
			.put(CassandraTypes.LexicalUUIDType, Hesperides.Hint.LEXICALUUID.alias())
			.put(CassandraTypes.TimeUUIDType, Hesperides.Hint.TIMEUUID.alias())
			.put(CassandraTypes.UUIDType, Hesperides.Hint.UUID.alias())
			.put(CassandraTypes.BytesType, Hesperides.Hint.BYTES.alias())
			.put(CassandraTypes.AsciiType, Hesperides.Hint.ASCII.alias())
			.put(CassandraTypes.DateType, Hesperides.Hint.DATE.alias())
			.put(CassandraTypes.EmptyType, Hesperides.Hint.NULL.alias())
			.build();
	
	public static final String DEFAULT_KEYSPACE_NAME = "Hesperides";
	public static final String DEFAULT_COLUMN_FAMILY_NAME = "Objects";
	
	private String keyspaceName = DEFAULT_KEYSPACE_NAME;
	private String columnFamilyName = DEFAULT_COLUMN_FAMILY_NAME;
	
	private BiMap<String, String> cassandraTypeAliases = DEFAULT_TYPE_ALIASES;
	
	protected Logger LOG;
	
	public AbstractConfigurableCassandraColumnTransformer() {
		
		LOG = LoggerFactory.getLogger(this.getClass());
		
		// try to load properties file
			
		// String configurationLocation = System.getProperty("hesperides.cassify.config", "cassify.properties");
		
			// file found, parse it with commons-configuration to get aliases
		
			// file not found, use default aliases
				
	}
	
	@Override
	public String getKeyspaceName() {
		return keyspaceName;
	}

	@Override
	public String getColumnFamilyName() {
		return columnFamilyName;
	}

	@Override
	public BiMap<String, String> getCassandraTypeAliases() {
		return cassandraTypeAliases;
	}
	
	public String dynamicCompositeTypeDescriptor() {
		
		List<String> types = new ArrayList<String>();
		for (Entry<String, String> aliasAndType:DEFAULT_TYPE_ALIASES.inverse().entrySet()) {
			types.add(aliasAndType.getKey()+"=>"+aliasAndType.getValue());
		}
		
		return "DynamicCompositeType("+StringUtils.join(types.toArray(), ",")+")";
		
	}





}
