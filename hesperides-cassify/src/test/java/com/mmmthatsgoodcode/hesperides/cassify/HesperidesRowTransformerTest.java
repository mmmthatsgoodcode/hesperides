package com.mmmthatsgoodcode.hesperides.cassify;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.hesperides.ComplexRow;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.utils.other.RiggedRand.ParticipantDistributionException;

public class HesperidesRowTransformerTest {

	private Node node = null;
	private HesperidesRowTransformer transformer = new HesperidesRowTransformer();
	private static final Logger LOG = LoggerFactory.getLogger(HesperidesRowTransformerTest.class);

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
		
		assertTrue(this.node.equals(node));

		
	}
	
	@Test
	public void testVolume() throws ParticipantDistributionException, TransformationException {
		
		List<Node> nodes = new ArrayList<Node>();
		
		Long start = System.nanoTime();
		LOG.debug("Transforming {} Nodes to rows", nodes.size());

		
		Float time = new Float((System.nanoTime() - start)/1000000);
		LOG.debug("Done in {}ms or {}ms/object", time, time/nodes.size());

		
	}
	
	
}
