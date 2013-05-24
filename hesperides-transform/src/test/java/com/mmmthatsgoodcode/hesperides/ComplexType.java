package com.mmmthatsgoodcode.hesperides;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

/**
 * This is a type for the unit tests of AnnotatedSerializer
 * @author andras
 *
 */
public class ComplexType {

	public String stringField = null;
	public Integer intField = null;
	public Long longField = null;
	public Float floatField = null;
	public Boolean booleanField = null;
	public EnclosedType objectField = null;
	public List<EnclosedType> objectList = new ArrayList<EnclosedType>();
	public Map<EnclosedType, Integer> objectKeyedMap = new HashMap<EnclosedType, Integer>();
	public List<Integer> integerList = new ArrayList<Integer>();
	public Map<Integer, String> integerKeyedMap = new HashMap<Integer, String>();
	
	public static class EnclosedType {
		
		public Boolean booleanField = true;
		
	}
	
	public ComplexType generateFields() {
		
		Random rand = new Random();
		
		this.setStringField(UUID.randomUUID().toString());
		this.setIntField(rand.nextInt(100));
		this.setLongField(rand.nextLong());
		this.setFloatField(rand.nextFloat());
		this.setBooleanField(rand.nextBoolean());
		
		this.setObjectField(new EnclosedType());
		
		for (int i=1;i<=rand.nextInt(10);i++) {
			this.getObjectList().add(new EnclosedType());
		}
		
		for (int i=1;i<=rand.nextInt(10);i++) {
			this.getObjectKeyedMap().put(new EnclosedType(), rand.nextInt(100));
		}
		
		for (int i=1;i<=rand.nextInt(10);i++) {
			this.getIntegerList().add(rand.nextInt(100));
		}
		
		for (int i=1;i<=rand.nextInt(10);i++) {
			this.getIntegerKeyedMap().put(rand.nextInt(1000), UUID.randomUUID().toString());
		}
		
		return this;
		
	}
	
	public String toString() {
		
		List<String> out = new ArrayList<String>();
		
		out.add("stringField => "+getStringField());
		out.add("intField => "+getIntField());
		out.add("longField => "+getLongField());
		out.add("floatField => "+getFloatField());
		out.add("booleanField => "+getBooleanField());
		out.add("objectField => "+getObjectField());
		out.add("objectList => "+getObjectList());
		out.add("objectKeyedMap => "+getObjectKeyedMap());
		out.add("integerList => "+getIntegerList());
		out.add("interedKeyedMap => "+getIntegerKeyedMap());
		
		return StringUtils.join(out, ", \n");
		
	}	
	
	public boolean equals(Object object) {
		
		if (!ComplexType.class.isAssignableFrom(object.getClass())) return false;
		ComplexType other = (ComplexType) object;
		
		return (getStringField()==null||other.getStringField()==null?getStringField()==other.getStringField():getStringField().equals(other.getStringField()));
	}

	public String getStringField() {
		return stringField;
	}

	public void setStringField(String stringField) {
		this.stringField = stringField;
	}

	public Integer getIntField() {
		return intField;
	}

	public void setIntField(Integer intField) {
		this.intField = intField;
	}

	public Long getLongField() {
		return longField;
	}

	public void setLongField(Long longField) {
		this.longField = longField;
	}

	public Float getFloatField() {
		return floatField;
	}

	public void setFloatField(Float floatField) {
		this.floatField = floatField;
	}

	public Boolean getBooleanField() {
		return booleanField;
	}

	public void setBooleanField(Boolean booleanField) {
		this.booleanField = booleanField;
	}

	public EnclosedType getObjectField() {
		return objectField;
	}

	public void setObjectField(EnclosedType objectField) {
		this.objectField = objectField;
	}

	public List<EnclosedType> getObjectList() {
		return objectList;
	}

	public void setObjectList(List<EnclosedType> objectList) {
		this.objectList = objectList;
	}

	public Map<EnclosedType, Integer> getObjectKeyedMap() {
		return objectKeyedMap;
	}

	public void setObjectKeyedMap(Map<EnclosedType, Integer> objectKeyedMap) {
		this.objectKeyedMap = objectKeyedMap;
	}

	public List<Integer> getIntegerList() {
		return integerList;
	}

	public void setIntegerList(List<Integer> integerList) {
		this.integerList = integerList;
	}

	public Map<Integer, String> getIntegerKeyedMap() {
		return integerKeyedMap;
	}

	public void setIntegerKeyedMap(Map<Integer, String> integerKeyedMap) {
		this.integerKeyedMap = integerKeyedMap;
	}
	
}
