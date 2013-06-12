package com.mmmthatsgoodcode.hesperides;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.mmmthatsgoodcode.hesperides.annotation.HBean;
import com.mmmthatsgoodcode.hesperides.annotation.HConstructor;
import com.mmmthatsgoodcode.hesperides.annotation.HConstructorField;

@HBean
public class ComplexHConstructorAnnotatedType extends ComplexType {

	private String secretField = null;
	private Integer anotherSecretField = null;
	private Integer nullField = 1;
	
	@HConstructor
	public ComplexHConstructorAnnotatedType(@HConstructorField(field="secretField") String secretField, @HConstructorField(field="anotherSecretField") Integer a, Integer b) {
		
		this.secretField = secretField;
		this.anotherSecretField = a;
		this.nullField = b;
		
	}
	
	public ComplexHConstructorAnnotatedType() {
		
	}
	
	public ComplexType generateFields() {
		
		Random rand = new Random();
		
		secretField = UUID.randomUUID().toString();
		anotherSecretField = rand.nextInt(99);
		nullField = null;
		
		super.generateFields();

		return this;
	}
	
	public String getSecretField() {
		return this.secretField;
	}
	
	public Integer getAnotherSecretField() {
		return anotherSecretField;
	}
	
	public Integer getNullField() {
		return nullField;
	}
	
	@Override
	public String toString() {
		
		List<String> out = new ArrayList<String>();
		
		out.add("secretField => "+getSecretField());
		out.add("anotherSecretField => "+getAnotherSecretField());
		out.add("nullField => "+getNullField());

		return StringUtils.join(out, ", \n")+"\n"+super.toString();
		
	}	
	
	@Override
	public boolean equals(Object object) {
		
		if (!ComplexHConstructorAnnotatedType.class.isAssignableFrom(object.getClass())) return false;
		
		ComplexHConstructorAnnotatedType other = (ComplexHConstructorAnnotatedType) object;
		
		return this.secretField == other.getSecretField()
				&& this.anotherSecretField == other.getAnotherSecretField()
				&& this.nullField == other.getNullField()
				&& super.equals(object);
		
	}
	
}
