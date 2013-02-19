package com.mmmthatsgoodcode.hesperides;

import com.mmmthatsgoodcode.hesperides.flora.Branch;
import com.mmmthatsgoodcode.hesperides.flora.Node;
import com.mmmthatsgoodcode.hesperides.flora.Tree;

/**
 * 
 * @author aszerdahelyi
 *
 */
public abstract class Gardener {

	private StoreDescription storeDescription;
	
	public Gardener(StoreDescription storeDescription) {
		setStoreDescription(storeDescription);
	}
	
	
	
	public abstract void plant(Tree tree);
	public abstract Tree fetch(String id);
	public abstract Branch fetch(String id,Node ... node);



	public StoreDescription getStoreDescription() {
		return storeDescription;
	}



	private void setStoreDescription(StoreDescription storeDescription) {
		this.storeDescription = storeDescription;
	}
	
}
