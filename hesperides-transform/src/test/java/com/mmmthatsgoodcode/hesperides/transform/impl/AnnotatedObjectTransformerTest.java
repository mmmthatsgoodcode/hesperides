package com.mmmthatsgoodcode.hesperides.transform.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.hesperides.ComplexHBeanAnnotatedType;
import com.mmmthatsgoodcode.hesperides.ComplexHConstructorAnnotatedType;
import com.mmmthatsgoodcode.hesperides.ComplexPublicFieldsType;
import com.mmmthatsgoodcode.hesperides.ComplexType;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.type.NullValue;
import com.mmmthatsgoodcode.hesperides.transform.RegisteredTransformerNotGenericException;
import com.mmmthatsgoodcode.hesperides.transform.TransformerRegistry;
import com.mmmthatsgoodcode.hesperides.transform.impl.AnnotatedObjectTransformer;

public class AnnotatedObjectTransformerTest {

	private ComplexPublicFieldsType complexPublicFieldsType;
	private ComplexHBeanAnnotatedType complexHBeanAnnotatedType;
	private ComplexHConstructorAnnotatedType complexHConstructorAnnotatedType;
	
	private static final Logger LOG = LoggerFactory.getLogger(AnnotatedObjectTransformerTest.class);
	
	@Before
	public void setUp() throws IOException {
		
		complexPublicFieldsType = new ComplexPublicFieldsType();
		complexPublicFieldsType.generateFields();
		
		complexHBeanAnnotatedType = new ComplexHBeanAnnotatedType();
		complexHBeanAnnotatedType.generateFields();
		
		complexHConstructorAnnotatedType = new ComplexHConstructorAnnotatedType();
		complexHConstructorAnnotatedType.generateFields();
		
		
	}
	
	@Test
	public void testTransformStrategy3() throws TransformationException, NoSuchFieldException, SecurityException, RegisteredTransformerNotGenericException {
		
		AnnotatedObjectTransformer<ComplexPublicFieldsType> transformer = new AnnotatedObjectTransformer<ComplexPublicFieldsType>();

		TransformerRegistry.getInstance().register(new Class[]{Integer.class, String.class}, ComplexPublicFieldsType.class.getField("integerKeyedMap"));
		TransformerRegistry.getInstance().register(new Class[]{ComplexType.EnclosedType.class, Integer.class}, ComplexPublicFieldsType.class.getField("objectKeyedMap"));

		TransformerRegistry.getInstance().register(new Class[]{Integer.class}, ComplexPublicFieldsType.class.getField("integerList"));
		TransformerRegistry.getInstance().register(new Class[]{ComplexType.EnclosedType.class}, ComplexPublicFieldsType.class.getField("objectList"));
	
		Node serializedCo = transformer.transform(complexPublicFieldsType).setName(new NullValue()).build(null);
		
		ComplexPublicFieldsType deserializedCo = transformer.transform(serializedCo);

		assertTrue(deserializedCo.equals(complexPublicFieldsType));
		
	}
	
	@Test
	public void testTransformStrategy1() throws NoSuchFieldException, SecurityException, RegisteredTransformerNotGenericException, TransformationException {
		
		AnnotatedObjectTransformer<ComplexHBeanAnnotatedType> transformer = new AnnotatedObjectTransformer<ComplexHBeanAnnotatedType>();

		TransformerRegistry.getInstance().register(new Class[]{Integer.class, String.class}, ComplexHBeanAnnotatedType.class.getField("integerKeyedMap"));
		TransformerRegistry.getInstance().register(new Class[]{ComplexType.EnclosedType.class, Integer.class}, ComplexHBeanAnnotatedType.class.getField("objectKeyedMap"));

		TransformerRegistry.getInstance().register(new Class[]{Integer.class}, ComplexHBeanAnnotatedType.class.getField("integerList"));
		TransformerRegistry.getInstance().register(new Class[]{ComplexType.EnclosedType.class}, ComplexHBeanAnnotatedType.class.getField("objectList"));
	
		Node serializedCo = transformer.transform(complexHBeanAnnotatedType).build(null);
		
		LOG.debug("Built {}", serializedCo);
		
		ComplexHBeanAnnotatedType deserializedCo = transformer.transform(serializedCo);
		
		LOG.debug("Built {}", deserializedCo);
		
		assertTrue(deserializedCo.equals(complexHBeanAnnotatedType));		
		
	}
	
	@Test
	public void testHConstructorInstantiationStrategy() throws NoSuchFieldException, SecurityException, RegisteredTransformerNotGenericException, TransformationException {
		
		AnnotatedObjectTransformer<ComplexHConstructorAnnotatedType> transformer = new AnnotatedObjectTransformer<ComplexHConstructorAnnotatedType>();

		TransformerRegistry.getInstance().register(new Class[]{Integer.class, String.class}, ComplexHConstructorAnnotatedType.class.getField("integerKeyedMap"));
		TransformerRegistry.getInstance().register(new Class[]{ComplexType.EnclosedType.class, Integer.class}, ComplexHConstructorAnnotatedType.class.getField("objectKeyedMap"));

		TransformerRegistry.getInstance().register(new Class[]{Integer.class}, ComplexHConstructorAnnotatedType.class.getField("integerList"));
		TransformerRegistry.getInstance().register(new Class[]{ComplexType.EnclosedType.class}, ComplexHConstructorAnnotatedType.class.getField("objectList"));
		
		Node serializedCo = transformer.transform(complexHConstructorAnnotatedType).build(null);

		ComplexHConstructorAnnotatedType deserializedCo = transformer.transform(serializedCo);
		
		
		assertTrue(deserializedCo.equals(complexHConstructorAnnotatedType));		
		
	}
	
	@Test
	public void testVolume() throws TransformationException, NoSuchFieldException, SecurityException, RegisteredTransformerNotGenericException {
		
		List<ComplexType> objects = new ArrayList<ComplexType>();
		List<Node> nodes = new ArrayList<Node>();
		AnnotatedObjectTransformer transformer = new AnnotatedObjectTransformer();
		
		TransformerRegistry.getInstance().register(new Class[]{Integer.class, String.class}, ComplexHConstructorAnnotatedType.class.getField("integerKeyedMap"));
		TransformerRegistry.getInstance().register(new Class[]{ComplexType.EnclosedType.class, Integer.class}, ComplexHConstructorAnnotatedType.class.getField("objectKeyedMap"));

		TransformerRegistry.getInstance().register(new Class[]{Integer.class}, ComplexHConstructorAnnotatedType.class.getField("integerList"));
		TransformerRegistry.getInstance().register(new Class[]{ComplexType.EnclosedType.class}, ComplexHConstructorAnnotatedType.class.getField("objectList"));

		
		LOG.debug("Generating 1000 objects");
		for(int i = 1; i < 1000; i++) {
						
		
			objects.add(new ComplexHConstructorAnnotatedType().generateFields());
			
		}
		LOG.debug("Done");
		
		LOG.debug("Transforming {} objects to Node", objects.size());
		
		Long start = System.nanoTime();
		for(ComplexType object:objects) {
			
			nodes.add(transformer.transform(object).build(null));
			
		}
		Float time = new Float((System.nanoTime() - start)/1000000);
		
		LOG.debug("Done in {}ms or {}ms/object", time, time/objects.size());
		
		
		
		
	}
	
}
