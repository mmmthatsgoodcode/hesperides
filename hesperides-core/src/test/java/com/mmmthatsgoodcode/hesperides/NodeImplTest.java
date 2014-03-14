package com.mmmthatsgoodcode.hesperides;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.type.IntegerValue;
import com.mmmthatsgoodcode.hesperides.core.type.NullValue;
import com.mmmthatsgoodcode.hesperides.core.type.StringValue;

public class NodeImplTest {
	
	private final Node complexTree = new NodeImpl.Builder<String, NullValue>()
			.setName(new StringValue("rootNode"))
			.addChild(new NodeImpl.Builder<String, Integer>()
					.setName(new StringValue("child1-1"))
					.setValue(new IntegerValue(42))
					.addChild(new NodeImpl.Builder<Integer, NullValue>()
							.setName(new IntegerValue(42)))
					.addChild(new NodeImpl.Builder<String, StringValue>()
							.setName(new StringValue("child1-2"))))
			.build(null);
	
	@Test
	public void testLocate() {
		
		assertEquals( complexTree.locate( new NodeImpl.Locator().n(new StringValue("child1-1")) ).getValue(), new IntegerValue(42));
		
	}

	@Test
	public void testUpstreamNodes() {
				
		assertEquals(complexTree.locate(new NodeImpl.Locator().n(new StringValue("child1-1"))).getUpstreamNodes(), new HashSet<Node>(Arrays.asList(new Node[] {complexTree})));
		
	}
	
}
