package com.mmmthatsgoodcode.hesperides;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.mmmthatsgoodcode.hesperides.annotation.HBean;
import com.mmmthatsgoodcode.hesperides.annotation.HBeanGetter;
import com.mmmthatsgoodcode.hesperides.annotation.HBeanSetter;
import com.mmmthatsgoodcode.hesperides.annotation.HField;

@HBean
public class ComplexHBeanAnnotatedType extends ComplexType {

	private int answer;
	private int secretAnswer;
	@HField(indexed=true, ttl = 0) private String secondaryId;
	
	public String getSecondaryId() {
		return secondaryId;
	}
	
	public void setSecondaryId(String secondaryId) {
		this.secondaryId = secondaryId;
	}
	
	@HBeanGetter(field="answer")
	public int whatIsTheMeaningOfLifeTheUniverseAndEverything() {
		return answer;
	}
	
	@HBeanSetter(field="answer")
	public void theMeaningOfLifeTheUniverseAndEverything(int answer) {
		this.answer = answer;
	}
	

	public Integer getSecretAnswer() {
		return secretAnswer;
	}

	public void setSecretAnswer(Integer secretAnswer) {
		this.secretAnswer = secretAnswer;
	}	
	
	@Override
	public String toString() {
		
		List<String> out = new ArrayList<String>();
		
		out.add("answer => "+whatIsTheMeaningOfLifeTheUniverseAndEverything());
		out.add("secretAnswer => "+getSecretAnswer());

		return StringUtils.join(out, ", \n")+"\n"+super.toString();
		
	}
	
	@Override
	public boolean equals(Object object) {
		
		if (!ComplexHBeanAnnotatedType.class.isAssignableFrom(object.getClass())) return false;
		
		ComplexHBeanAnnotatedType other = (ComplexHBeanAnnotatedType) object;
		
		return this.answer == other.whatIsTheMeaningOfLifeTheUniverseAndEverything()
				&& this.secretAnswer == other.getSecretAnswer()
				&& super.equals(object);
		
	}
	
	@Override
	public ComplexType generateFields() {
		Random rand = new Random();
		this.answer = rand.nextInt(43);
		this.secretAnswer = rand.nextInt(43);
		return super.generateFields();
	}

	
	
}
