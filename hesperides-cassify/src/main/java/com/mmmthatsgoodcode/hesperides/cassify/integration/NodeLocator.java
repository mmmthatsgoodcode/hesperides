package com.mmmthatsgoodcode.hesperides.cassify.integration;

import java.util.ArrayList;
import java.util.List;

import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn.AbstractType;

public class NodeLocator {

	private List<AbstractType> components = new ArrayList<AbstractType>();
	
	public NodeLocator n(AbstractType name) {
		components.add(name);
		
		return this;
	}
	public List<AbstractType> components() {
		return components;
	}
	
}
