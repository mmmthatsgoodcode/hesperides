package com.mmmthatsgoodcode.hesperides.cassify;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumnSlice;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.type.FloatValue;
import com.mmmthatsgoodcode.hesperides.core.type.IntegerValue;
import com.mmmthatsgoodcode.hesperides.core.type.ObjectValue;
import com.mmmthatsgoodcode.hesperides.core.type.StringValue;

public class HesperidesColumnSliceTransformerTest {

	public final static class SomeClass { }
	public final static class SomeOtherClass { }
	
	@Test
	public void testTransformLocator() throws TransformationException {
		
		Node.Locator locator = new NodeImpl.Locator()
			.p(new NodeImpl.Builder()
				.setName(new StringValue("Foo"))
				.setRepresentedType(SomeClass.class)
				.build(null))
			.p(new NodeImpl.Builder()
				.setName(new StringValue("Bar"))
				.setRepresentedType(SomeOtherClass.class)
				.build(null))
			.p(new NodeImpl.Builder()
				.setName(new FloatValue(3.14f))
				.build(null))
			.n(new NodeImpl.Builder()
				.setName(new StringValue("Answer"))
				.setValue(new IntegerValue(42))
				.build(null));
		
		HesperidesColumnSlice columnSlice = HesperidesColumnSliceTransformer.getInstance().transform(locator);
		
		assertEquals(new ArrayList<AbstractType<?>>( Arrays.asList( new AbstractType<?>[] {
				new StringValue("Foo"),
				new StringValue(SomeClass.class.getName()),
				new StringValue("Bar"),
				new StringValue(SomeOtherClass.class.getName()),
				new FloatValue(3.14f),
				new StringValue(NodeImpl.class.getName()),
				new StringValue("Answer"),
				new StringValue(Integer.class.getName())  })), columnSlice.components());		
		assertFalse(columnSlice.isPartial());
		
	}
	
	@Test
	public void testTransformPartialLocator() throws TransformationException {

		Node.Locator locator = new NodeImpl.Locator()
		.p(new NodeImpl.Builder()
			.setName(new StringValue("Foo"))
			.setRepresentedType(SomeClass.class)
			.build(null))
		.p(new NodeImpl.Builder()
			.setName(new IntegerValue(123))
			.build(null));
		
		HesperidesColumnSlice columnSlice = HesperidesColumnSliceTransformer.getInstance().transform(locator);
		
		assertEquals(new ArrayList<AbstractType<?>>( Arrays.asList( new AbstractType<?>[] {
				new StringValue("Foo"),
				new StringValue(SomeClass.class.getName()),
				new IntegerValue(123),
				new StringValue(NodeImpl.class.getName())  })), columnSlice.components());		
		assertTrue(columnSlice.isPartial());
		
		
	}
	
	
	
}
