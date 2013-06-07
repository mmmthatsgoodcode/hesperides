package com.mmmthatsgoodcode.hesperides.transform.model;

import java.lang.reflect.Field;

import com.mmmthatsgoodcode.hesperides.annotation.HField;
import com.mmmthatsgoodcode.hesperides.annotation.Id;
import com.mmmthatsgoodcode.hesperides.annotation.Ignore;

public class HesperidesField {

	private Field field;
	
	public HesperidesField(Field field) {
		this.field = field;
	}
	
	public boolean isIgnored() {
		
		return (this.field.getAnnotation(Ignore.class) != null);
		
	}
	
	public boolean isNodeId() {
		
		return (this.field.getAnnotation(Id.class) != null);
	}
	
	public int getTtl() {
		
		HField fieldAnnotation = this.field.getAnnotation(HField.class);
		
		if (fieldAnnotation != null) return fieldAnnotation.ttl();
		return 0;
		
	}
	
	public Field toField() {
		return field;
	}
	
}
