package com.mmmthatsgoodcode.hesperides;

import org.junit.Test;

public class ComplexPublicFieldsTypeTest {

	@Test
	public void testEquality() {
		
		ComplexPublicFieldsType complexPublicFieldsType = new ComplexPublicFieldsType();
		complexPublicFieldsType.generateFields();
		ComplexPublicFieldsType anotherComplexPublicFieldsType = new ComplexPublicFieldsType();
		anotherComplexPublicFieldsType.generateFields();
		
		System.out.println(complexPublicFieldsType);
		System.out.println("\n --vs-- \n");
		System.out.println(anotherComplexPublicFieldsType);

//		assertTrue(complexPublicFieldsType.equals(anotherComplexPublicFieldsType));
		
	}

	
}
