package com.mmmthatsgoodcode.hesperides;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.mmmthatsgoodcode.hesperides.annotation.HBean;
import com.mmmthatsgoodcode.hesperides.annotation.HConstructor;
import com.mmmthatsgoodcode.hesperides.annotation.HConstructorField;

@HBean
public class ComplexHConstructorAnnotatedType extends ComplexType {

	private String secretField = null;
	
	@HConstructor
	public ComplexHConstructorAnnotatedType(@HConstructorField(field="secretField") String secretField) {
		
		this.secretField = secretField;
		
	}
	
	public ComplexHConstructorAnnotatedType() {
		
	}
	
	public ComplexType generateFields() {
		super.generateFields();
		
		secretField = UUID.randomUUID().toString();
		
		return this;
	}
	
	public String getSecretField() {
		return this.secretField;
	}
	
	@Override
	public String toString() {
		
		List<String> out = new ArrayList<String>();
		
		out.add("secretField => "+getSecretField());

		return StringUtils.join(out, ", \n")+"\n"+super.toString();
		
	}	
	
	@Override
	public boolean equals(Object object) {
		
		if (!ComplexHConstructorAnnotatedType.class.isAssignableFrom(object.getClass())) return false;
		
		ComplexHConstructorAnnotatedType other = (ComplexHConstructorAnnotatedType) object;
		
		return this.secretField == other.getSecretField()
				&& super.equals(object);
		
	}
	
}
