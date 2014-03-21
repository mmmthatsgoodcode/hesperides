package com.mmmthatsgoodcode.hesperides;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.type.FloatValue;
import com.mmmthatsgoodcode.hesperides.core.type.IntegerValue;
import com.mmmthatsgoodcode.hesperides.core.type.NullValue;
import com.mmmthatsgoodcode.hesperides.core.type.StringValue;

public class NodeImplTest {
	
	private final Node complexTree = new NodeImpl.Builder<StringValue, NullValue>()
			.setName(new StringValue("rootNode"))
			.addChild(new NodeImpl.Builder<StringValue, IntegerValue>()
					.setName(new StringValue("child1-1"))
					.addChild(new NodeImpl.Builder<IntegerValue, FloatValue>()
							.setName(new IntegerValue(42))
							.setValue(new FloatValue(3.14f)))
					.addChild(new NodeImpl.Builder<StringValue, StringValue>()
							.setName(new StringValue("child1-2"))))
			.build(null);
	
	@Test
	public void testLocate() {
		
		assertEquals( new FloatValue(3.14f), complexTree.locate( new NodeImpl.Locator().p(new StringValue("child1-1")).n(new IntegerValue(42)) ).getValue() );
		
	}

	@Test
	public void testUpstreamNodes() {
				
		assertEquals(new HashSet<AbstractType<?>>(Arrays.asList(new AbstractType<?>[] {new StringValue("rootNode")})), complexTree.locate(new NodeImpl.Locator().n(new StringValue("child1-1"))).getUpstreamNodeNames());
		
	}
	
}
