package com.mmmthatsgoodcode.hesperides.cassify;

import com.google.common.collect.BiMap;

public interface ConfigurableCassifier {
	
	public BiMap<String, String> getCassandraTypeAliases();
	public String getColumnFamilyName();
	public String getKeyspaceName();
	public String dynamicCompositeTypeDescriptor();
	
}
