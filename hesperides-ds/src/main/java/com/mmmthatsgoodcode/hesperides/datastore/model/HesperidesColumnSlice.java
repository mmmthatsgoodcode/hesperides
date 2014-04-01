package com.mmmthatsgoodcode.hesperides.datastore.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.type.WildcardValue;

/**
 * Represents a range of dynamic composite Columns by
 * @author andras
 *
 */
public class HesperidesColumnSlice {

	public enum Relation {
		
		AND("and", "&&"),
		OR("or", "||");
		
		public final String name, symbol;
		
		Relation(String name, String symbol) {
			this.name = name;
			this.symbol = symbol;
		}
		
		public static Relation fromName(String name) {
			
			for(Relation relation:Relation.values()) {
				
				if (relation.name.equals(name)) return relation;
				
			}
			
			return null;
			
		}

		
	}
	private List<AbstractType> nameComponents = new ArrayList<AbstractType>();
	private boolean locked = false;
	
	public HesperidesColumnSlice n(AbstractType...nameComponents) {
		return n(Arrays.asList(nameComponents));
	}
	
	public HesperidesColumnSlice n(List<AbstractType> nameComponents) {
		for(AbstractType nameComponent:nameComponents) {
			n(nameComponent);
		}
		
		return this;
	}
	
	private HesperidesColumnSlice n(AbstractType nameComponent) {
		if (locked) throw new IllegalStateException("Can not add name components after the Wildcard is added");
		nameComponents.add(nameComponent);
		return this;
	}
	
	public HesperidesColumnSlice n(WildcardValue nameComponent) {
		locked = true;
		return this;
	}
	
	/**
	 * Check if the supplied {@link HesperidesColumn} would match this {@link HesperidesColumnSlice}
	 * @param column
	 * @return
	 */
	public boolean matches(HesperidesColumn column) {
		
		// if this defines an actual column, name components must match..
		if (isPartial() == false) return column.getNameComponents().equals(components());
		
		// lets see if there are more name components in this slice than on the column..
		if (column.getNameComponents().size() < components().size()) return false;
		
		// match up name components
		for (int c=0; c < components().size(); c++) {
			AbstractType nameComponent = components().get(c);
			if (nameComponent.equals(column.getNameComponents().get(c)) == false) return false;
		}
		
		return true;
		
		
	}
	
	public boolean isPartial() {
		return locked;
	}
	
	public List<AbstractType> components() {
		return nameComponents;
	}
	
	@Override
	public String toString() {
		return nameComponents.toString()+", locked: "+String.valueOf(locked);
	}
	
}