package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.hesperides.core.Hesperides;


public abstract class AbstractConfigurableCassifier<T> implements Cassifier<T>,
		ConfigurableCassifier {

	public static final char DEFAULT_UTF8_ALIAS = 's';
	public static final char DEFAULT_INTEGER32_ALIAS = 'h';
	public static final char DEFAULT_INTEGER_ALIAS = 'i';
	public static final char DEFAULT_FLOAT_ALIAS = 'f';
	public static final char DEFAULT_LONG_ALIAS = 'l';
	public static final char DEFAULT_BOOLEAN_ALIAS = 'o';
	public static final char DEFAULT_BYTES_ALIAS = 'b';
	public static final char DEFAULT_EMPTY_ALIAS = 'n';
	public static final char DEFAULT_DATE_ALIAS = 'd';
	
	public static final String DEFAULT_KEYSPACE_NAME = "Hesperides";
	public static final String DEFAULT_COLUMN_FAMILY_NAME = "Objects";
	
	
	private String keyspaceName = DEFAULT_KEYSPACE_NAME;
	private String columnFamilyName = DEFAULT_COLUMN_FAMILY_NAME;
	
	private Map<Integer, Character> cassandraTypeAliases = new HashMap<Integer, Character>();
	
	protected Logger LOG;
	
	public AbstractConfigurableCassifier() {
		
		LOG = LoggerFactory.getLogger(this.getClass());
		
		// try to load properties file
		// String configurationLocation = System.getProperty("hesperides.cassify.config", "cassify.properties");
		
		// file found, parse it with commons-configuration to get aliases
		
		// file not found, use default aliases
		
		cassandraTypeAliases.put(Hesperides.Hints.BOOLEAN, DEFAULT_BOOLEAN_ALIAS);
		cassandraTypeAliases.put(Hesperides.Hints.INT, DEFAULT_INTEGER_ALIAS);
		cassandraTypeAliases.put(Hesperides.Hints.LONG, DEFAULT_LONG_ALIAS);
		cassandraTypeAliases.put(Hesperides.Hints.FLOAT, DEFAULT_FLOAT_ALIAS);
		cassandraTypeAliases.put(Hesperides.Hints.STRING, DEFAULT_UTF8_ALIAS);
		cassandraTypeAliases.put(Hesperides.Hints.DATE, DEFAULT_DATE_ALIAS);
		cassandraTypeAliases.put(Hesperides.Hints.NULL, DEFAULT_EMPTY_ALIAS);
		
	}
	
	@Override
	public Map<Integer, Character> getCassandraTypeAliases() {
		return this.cassandraTypeAliases;
	}


	@Override
	public Character getCassandraTypeAlias(int hint) {
		return this.cassandraTypeAliases.get(hint);
	}

	@Override
	public Integer getHesperidesHint(Character alias) {
		for(Entry<Integer, Character> mapping:this.cassandraTypeAliases.entrySet()) {
			if (mapping.getValue().equals(alias)) return mapping.getKey();
		}
		
		return null;
	}
	
	@Override
	public String getKeyspaceName() {
		return keyspaceName;
	}

	@Override
	public String getColumnFamilyName() {
		return columnFamilyName;
	}




}
