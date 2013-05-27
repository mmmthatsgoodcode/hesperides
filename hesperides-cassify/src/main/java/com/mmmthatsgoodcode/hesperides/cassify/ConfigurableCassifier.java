package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.Map;

public interface ConfigurableCassifier {

	public Map<Integer, Character> getCassandraTypeAliases();
	public Character getCassandraTypeAlias(int hint);
	public Integer getHesperidesHint(Character alias);
	public String getColumnFamilyName();
	public String getKeyspaceName();
	
}
