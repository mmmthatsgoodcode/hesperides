package com.mmmthatsgoodcode.hesperides.datastore.cassandra;


import com.google.common.collect.BiMap;
import com.mmmthatsgoodcode.hesperides.datastore.model.HesperidesColumn;

public interface ConfigurableCassandraColumnTransformer<C> extends HesperidesColumn.Transformer<C> {
	
	public BiMap<String, String> getCassandraTypeAliases();
	public String getColumnFamilyName();
	public String getKeyspaceName();
	public String dynamicCompositeTypeDescriptor();
	
}
