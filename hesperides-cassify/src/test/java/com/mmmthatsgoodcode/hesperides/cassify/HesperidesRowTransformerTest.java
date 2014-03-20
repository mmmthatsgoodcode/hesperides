package com.mmmthatsgoodcode.hesperides.cassify;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.type.IntegerValue;
import com.mmmthatsgoodcode.hesperides.core.type.NullValue;
import com.mmmthatsgoodcode.hesperides.core.type.StringValue;
import com.mmmthatsgoodcode.utils.other.RiggedRand.ParticipantDistributionException;

public class HesperidesRowTransformerTest {

	private Node node = null;
	private HesperidesRowTransformer transformer = new HesperidesRowTransformer();
	private static final Logger LOG = LoggerFactory.getLogger(HesperidesRowTransformerTest.class);

	@Before
	public void setUp() {
		
		Random rand = new Random();
		
		node = new NodeImpl.Builder<String, NullValue>()
				.setName(new StringValue("rootNode"))
				.setRepresentedType(NodeImpl.class)
				.addChild(new NodeImpl.Builder<String, Integer>()
						.setName(new StringValue("child1-1"))
						.setRepresentedType(NodeImpl.class)
						.addChild(new NodeImpl.Builder<Integer, NullValue>()
								.setName(new IntegerValue(42)))
								.setRepresentedType(NodeImpl.class)
								.setIndexed(rand.nextBoolean())
						.addChild(new NodeImpl.Builder<Integer, StringValue>()
								.setName(new IntegerValue(rand.nextInt(47293449)))
								.setRepresentedType(NodeImpl.class)
								.setIndexed(rand.nextBoolean()))
						.addChild(new NodeImpl.Builder<String, StringValue>()
								.setName(new StringValue("child1-2"))))
								.setRepresentedType(NodeImpl.class)
				.build(null);

		
	}
	
	@Test
	public void testTransform() throws TransformationException {
		
		HesperidesRow row = this.transformer.transform(this.node);
//		System.out.println("--" + node);


		Node node = this.transformer.transform(row).build(null);
		
		System.out.println(this.node);

		System.out.println(node);
		
		assertTrue(this.node.equals(node));

		
	}
	
}
