package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.Map;

import com.google.common.collect.BiMap;

public interface ConfigurableCassifier {
	
	public BiMap<String, Character> getCassandraTypeAliases();
	public String getColumnFamilyName();
	public String getKeyspaceName();
	public String dynamicCompositeTypeDescriptor();
	
}
