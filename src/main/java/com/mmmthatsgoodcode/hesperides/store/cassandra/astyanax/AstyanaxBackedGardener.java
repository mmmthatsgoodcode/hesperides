package com.mmmthatsgoodcode.hesperides.store.cassandra.astyanax;

import com.mmmthatsgoodcode.hesperides.Gardener;
import com.mmmthatsgoodcode.hesperides.flora.Branch;
import com.mmmthatsgoodcode.hesperides.flora.Node;
import com.mmmthatsgoodcode.hesperides.flora.Tree;
import com.mmmthatsgoodcode.hesperides.store.cassandra.CassandraBackedGardener;
import com.mmmthatsgoodcode.hesperides.store.cassandra.CassandraClusterDescription;

public class AstyanaxBackedGardener extends CassandraBackedGardener {

	public AstyanaxBackedGardener(CassandraClusterDescription storeDescription) {
		super(storeDescription);
		
		// create Astyanax connection state
		
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
