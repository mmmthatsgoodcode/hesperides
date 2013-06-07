package com.mmmthatsgoodcode.hesperides.transform.model;

public class HesperidesClass {

	private Class clazz;
	
	public HesperidesClass(Class clazz) {
		this.clazz = clazz;
	}
	
	public Class toClass() {
		return this.clazz;
	}
	
}
