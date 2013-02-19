package com.mmmthatsgoodcode.hesperides.store.jdbc;

import com.mmmthatsgoodcode.hesperides.Gardener;
import com.mmmthatsgoodcode.hesperides.StoreDescription;
import com.mmmthatsgoodcode.hesperides.flora.Branch;
import com.mmmthatsgoodcode.hesperides.flora.Node;
import com.mmmthatsgoodcode.hesperides.flora.Tree;

public abstract class JDBCBackedGardener extends Gardener {

	public JDBCBackedGardener(JDBCStoreDescription storeDescription) {
		super(storeDescription);
		// TODO Auto-generated constructor stub
	}


}
