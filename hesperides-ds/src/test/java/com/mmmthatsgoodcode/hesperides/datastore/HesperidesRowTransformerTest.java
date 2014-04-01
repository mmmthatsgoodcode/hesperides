package com.mmmthatsgoodcode.hesperides.datastore;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.type.IntegerValue;
import com.mmmthatsgoodcode.hesperides.core.type.NullValue;
import com.mmmthatsgoodcode.hesperides.core.type.StringValue;
import com.mmmthatsgoodcode.hesperides.datastore.HesperidesRowTransformer;
import com.mmmthatsgoodcode.hesperides.datastore.model.HesperidesRow;
import com.mmmthatsgoodcode.utils.other.RiggedRand.ParticipantDistributionException;

public class HesperidesRowTransformerTest {

	private Node node = null;
	private HesperidesRowTransformer transformer = new HesperidesRowTransformer();
	private static final Logger LOG = LoggerFactory.getLogger(HesperidesRowTransformerTest.class);

	@Before
	public void setUp() {
		
		Random rand = new Random();
		
		node = new NodeImpl.Builder<StringValue, NullValue>()
				.setName(new StringValue("rootNode"))
				.setRepresentedType(NodeImpl.class)
				.addChild(new NodeImpl.Builder<StringValue, IntegerValue>()
						.setName(new StringValue("child1-1"))
						.setRepresentedType(NodeImpl.class)
						.addChild(new NodeImpl.Builder<IntegerValue, IntegerValue>()
								.setName(new IntegerValue(42))
								.setValue(new IntegerValue(13)))
								.setIndexed(rand.nextBoolean())
						.addChild(new NodeImpl.Builder<IntegerValue, StringValue>()
								.setName(new IntegerValue(rand.nextInt(47293449)))
								.setValue(new StringValue("Yeah!"))
								.setIndexed(rand.nextBoolean()))
						.addChild(new NodeImpl.Builder<StringValue, StringValue>()
								.setName(new StringValue("child1-2"))
								.setValue(new StringValue("Grandchild!"))))
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
