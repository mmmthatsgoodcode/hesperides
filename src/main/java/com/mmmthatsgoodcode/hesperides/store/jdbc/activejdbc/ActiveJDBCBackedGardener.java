package com.mmmthatsgoodcode.hesperides.store.jdbc.activejdbc;

import com.mmmthatsgoodcode.hesperides.flora.Branch;
import com.mmmthatsgoodcode.hesperides.flora.Node;
import com.mmmthatsgoodcode.hesperides.flora.Tree;
import com.mmmthatsgoodcode.hesperides.store.jdbc.JDBCBackedGardener;
import com.mmmthatsgoodcode.hesperides.store.jdbc.JDBCStoreDescription;

public class ActiveJDBCBackedGardener extends JDBCBackedGardener {

	public ActiveJDBCBackedGardener(JDBCStoreDescription storeDescription) {
		super(storeDescription);
		
		// set up connection pool
		
	}

	@Override
	public void plant(Tree tree) {

	}

	@Override
	public Tree fetch(String id) {
		return null;
	}

	@Override
	public Branch fetch(String id, Node... node) {
		return null;
	}

}
