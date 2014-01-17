package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;


public abstract class AbstractConfigurableCassifier<T> implements ConfigurableCassifier, Cassifier<T> {
	
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
	
	public static final ImmutableBiMap<String, Integer> HINT_TO_CASSANDRA_TYPE = new ImmutableBiMap.Builder<String, Integer>()
			.put(CassandraTypes.UTF8Type, Hesperides.Hints.STRING)
			.put(CassandraTypes.Int32Type, Hesperides.Hints.INT32)
			.put(CassandraTypes.IntegerType, Hesperides.Hints.INT)
			.put(CassandraTypes.FloatType, Hesperides.Hints.FLOAT)
			.put(CassandraTypes.LongType, Hesperides.Hints.LONG)
			.put(CassandraTypes.BooleanType, Hesperides.Hints.BOOLEAN)
			.put(CassandraTypes.BytesType, Hesperides.Hints.BYTES)
			.put(CassandraTypes.EmptyType, Hesperides.Hints.NULL)
			.put(CassandraTypes.DateType, Hesperides.Hints.DATE)
			.put(CassandraTypes.LexicalUUIDType, Hesperides.Hints.LEXICALUUID)
			.put(CassandraTypes.TimeUUIDType, Hesperides.Hints.TIMEUUID)
			.put(CassandraTypes.UUIDType, Hesperides.Hints.UUID)
			.build();
	
	public static final BiMap<String, Character> DEFAULT_TYPE_ALIASES = new ImmutableBiMap.Builder<String, Character>()
			.put(CassandraTypes.UTF8Type, 's')
			.put(CassandraTypes.IntegerType, 'i')
			.put(CassandraTypes.Int32Type, 'h')
			.put(CassandraTypes.FloatType, 'f')
			.put(CassandraTypes.LongType, 'l')
//			.put(CassandraTypes.LongType, 'l')
			.put(CassandraTypes.BooleanType, 'b')
			.put(CassandraTypes.LexicalUUIDType, 'e')
			.put(CassandraTypes.TimeUUIDType, 't')
			.put(CassandraTypes.UUIDType, 'g')
			.put(CassandraTypes.BytesType, 'c')
			.put(CassandraTypes.AsciiType, 'a')
			.put(CassandraTypes.DateType, 'd')
			.put(CassandraTypes.EmptyType, 'n')
			.build();
	
	public static final String DEFAULT_KEYSPACE_NAME = "Hesperides";
	public static final String DEFAULT_COLUMN_FAMILY_NAME = "Objects";
	
	private String keyspaceName = DEFAULT_KEYSPACE_NAME;
	private String columnFamilyName = DEFAULT_COLUMN_FAMILY_NAME;
	
	private BiMap<String, Character> cassandraTypeAliases = DEFAULT_TYPE_ALIASES;
	
	protected Logger LOG;
	
	public AbstractConfigurableCassifier() {
		
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
	public BiMap<String, Character> getCassandraTypeAliases() {
		return cassandraTypeAliases;
	}
	
	public String dynamicCompositeTypeDescriptor() {
		
		List<String> types = new ArrayList<String>();
		for (Entry<Character, String> aliasAndType:DEFAULT_TYPE_ALIASES.inverse().entrySet()) {
			types.add(aliasAndType.getKey()+"=>"+aliasAndType.getValue());
		}
		
		return "DynamicCompositeType("+StringUtils.join(types.toArray(), ",")+")";
		
	}





}
