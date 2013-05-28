package com.mmmthatsgoodcode.hesperides.transform.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;
import com.mmmthatsgoodcode.hesperides.ComplexHBeanAnnotatedType;
import com.mmmthatsgoodcode.hesperides.ComplexHConstructorAnnotatedType;
import com.mmmthatsgoodcode.hesperides.ComplexPublicFieldsType;
import com.mmmthatsgoodcode.hesperides.ComplexType;
import com.mmmthatsgoodcode.hesperides.annotation.Id;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.transform.RegisteredTransformerNotGenericException;
import com.mmmthatsgoodcode.hesperides.transform.TransformerRegistry;
import com.mmmthatsgoodcode.hesperides.transform.impl.AnnotatedObjectTransformer;
import com.mmmthatsgoodcode.hesperides.transform.impl.MapTransformer;

public class AnnotatedObjectTransformerTest {

	private ComplexPublicFieldsType complexPublicFieldsType;
	private ComplexHBeanAnnotatedType complexHBeanAnnotatedType;
	private ComplexHConstructorAnnotatedType complexHConstructorAnnotatedType;
	
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
	
		Node serializedCo = transformer.transform(complexPublicFieldsType);
		
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
	
		Node serializedCo = transformer.transform(complexHBeanAnnotatedType);
		
		ComplexHBeanAnnotatedType deserializedCo = transformer.transform(serializedCo);
		
		assertTrue(deserializedCo.equals(complexHBeanAnnotatedType));		
		
	}
	
	@Test
	public void testHConstructorInstantiationStrategy() throws NoSuchFieldException, SecurityException, RegisteredTransformerNotGenericException, TransformationException {
		
		AnnotatedObjectTransformer<ComplexHConstructorAnnotatedType> transformer = new AnnotatedObjectTransformer<ComplexHConstructorAnnotatedType>();

		TransformerRegistry.getInstance().register(new Class[]{Integer.class, String.class}, ComplexHConstructorAnnotatedType.class.getField("integerKeyedMap"));
		TransformerRegistry.getInstance().register(new Class[]{ComplexType.EnclosedType.class, Integer.class}, ComplexHConstructorAnnotatedType.class.getField("objectKeyedMap"));

		TransformerRegistry.getInstance().register(new Class[]{Integer.class}, ComplexHConstructorAnnotatedType.class.getField("integerList"));
		TransformerRegistry.getInstance().register(new Class[]{ComplexType.EnclosedType.class}, ComplexHConstructorAnnotatedType.class.getField("objectList"));
		
		Node serializedCo = transformer.transform(complexHConstructorAnnotatedType);

		ComplexHConstructorAnnotatedType deserializedCo = transformer.transform(serializedCo);
		
		
		assertTrue(deserializedCo.equals(complexHConstructorAnnotatedType));		
		
	}
	
}
