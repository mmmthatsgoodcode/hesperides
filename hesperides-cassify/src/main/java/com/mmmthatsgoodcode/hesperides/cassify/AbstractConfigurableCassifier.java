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
		
		
		public static final String AsciiType = "AsciiType";
		public static final String BooleanType = "BooleanType";
		public static final String BytesType = "BytesType";
		public static final String DateType = "DateType";
		public static final String DecimalType = "DecimalType";
		public static final String DoubleType = "DoubleType";		
		public static final String EmptyType = "EmptyType";
		public static final String FloatType = "FloatType";
		public static final String Int32Type = "Int32Type";
		public static final String IntegerType = "IntegerType";
		public static final String LexicalUUIDType = "LexicalUUIDType";
		public static final String LongType = "LongType";
		public static final String TimeUUIDType = "TimeUUIDType";
		public static final String UUIDType = "UUIDType";
		public static final String UTF8Type = "UTF8Type";

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
