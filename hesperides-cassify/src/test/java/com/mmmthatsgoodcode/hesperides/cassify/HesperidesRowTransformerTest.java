package com.mmmthatsgoodcode.hesperides.cassify;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;

public class HesperidesRowTransformerTest {

	private Node node = null;
	private HesperidesRowTransformer transformer = new HesperidesRowTransformer();
	
	@Before
	public void setUp() {
		
		this.node = new NodeImpl<String, NodeImpl>("YeahRow");
		
		NodeImpl<String, String> strNode = new NodeImpl<String, String>("Foo");
		strNode.setValue("BAR!");
		
		NodeImpl<String, NodeImpl> containerNode = new NodeImpl<String, NodeImpl>("Container");
		
			NodeImpl<String, String> strNodeInContainerNode = new NodeImpl<String, String>("IsThis");
			strNodeInContainerNode.setValue("Madness?");
			
			containerNode.addChild(strNodeInContainerNode);
		
			for (int i=1;i<100;i++) {
				NodeImpl<Integer, Boolean> intNode = new NodeImpl<Integer, Boolean>(i);
				intNode.setValue((i%2==0?true:false));
				containerNode.addChild(intNode);
			}
			
			NodeImpl<String, NodeImpl> containerInContainerNode = new NodeImpl<String, NodeImpl>("ContainerInContainer");

			containerNode.addChild(containerInContainerNode);

			
		this.node.addChild(strNode);
		this.node.addChild(containerNode);
		
	}
	
	@Test
	public void testTransform() throws TransformationException {
		
		HesperidesRow row = this.transformer.transform(this.node);
		System.out.println(row);

		Node node = this.transformer.transform(row);
		
		System.out.println(this.node);

		System.out.println(node);
		
		assertTrue(this.node.equals(node));

		
	}
	
}
