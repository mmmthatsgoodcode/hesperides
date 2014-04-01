package com.mmmthatsgoodcode.hesperides.cassify.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mmmthatsgoodcode.hesperides.core.type.StringValue;
import com.mmmthatsgoodcode.hesperides.core.type.WildcardValue;

public class HesperidesColumnSliceTest {

	@Test
	public void testMatchingColumn() {
		
		HesperidesColumn matchingColumn = new HesperidesColumn().addNameComponent("foo").addNameComponent("column").addNameComponent(1337).addNameComponent(3.14f).addNameComponent("!");
		HesperidesColumnSlice matchingColumnSlice = new HesperidesColumnSlice().n(new StringValue("foo")).n(new StringValue("column")).n(new WildcardValue());
		
		assertTrue(matchingColumnSlice.matches(matchingColumn));
		
	}
	
	@Test
	public void testNonMatchingColumn() {
		
		HesperidesColumn nonMatchingColumn = new HesperidesColumn().addNameComponent("foo").addNameComponent("column").addNameComponent(1337).addNameComponent(3.14f).addNameComponent("!");
		HesperidesColumnSlice nonMatchingColumnSlice = new HesperidesColumnSlice().n(new StringValue("foo")).n(new StringValue("bar")).n(new WildcardValue());
		
		assertFalse(nonMatchingColumnSlice.matches(nonMatchingColumn));
		
	}
	
}
