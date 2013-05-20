package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;

public class HesperidesColumnTransformerTest {

	private Node node = null;
	private HesperidesColumnTransformer transformer = new HesperidesColumnTransformer();
	
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
				NodeImpl<Integer, String> intNode = new NodeImpl<Integer, String>(i);
				containerNode.addChild(intNode);
			}
			
		this.node.addChild(strNode);
		this.node.addChild(containerNode);
		
	}
	
	@Test
	public void testTransform() throws TransformationException {
		
		List<HesperidesColumn> columns = this.transformer.transform(this.node);
		
		System.out.println(columns);
		
	}
	
}
