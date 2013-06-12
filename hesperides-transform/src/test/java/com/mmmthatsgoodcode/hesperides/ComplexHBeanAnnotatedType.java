package com.mmmthatsgoodcode.hesperides;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.mmmthatsgoodcode.hesperides.annotation.HBean;
import com.mmmthatsgoodcode.hesperides.annotation.HBeanGetter;
import com.mmmthatsgoodcode.hesperides.annotation.HBeanSetter;

@HBean
public class ComplexHBeanAnnotatedType extends ComplexType {

	private int answer;
	private int secretAnswer;
	
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
