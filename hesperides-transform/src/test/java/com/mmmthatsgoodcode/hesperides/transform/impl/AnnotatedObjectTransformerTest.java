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
import com.mmmthatsgoodcode.hesperides.annotation.Id;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.transform.TransformerRegistry;
import com.mmmthatsgoodcode.hesperides.transform.impl.AnnotatedObjectTransformer;
import com.mmmthatsgoodcode.hesperides.transform.impl.MapTransformer;

public class AnnotatedObjectTransformerTest {

	private ComplexObject co;
	private AnnotatedObjectTransformer<ComplexObject> transformer = new AnnotatedObjectTransformer<ComplexObject>();
	
	public final static class SimpleObject {
		private String name = "Sss";
	}
	
	public final static class ComplexObject {

		@Id public String name;
		public int redBalloons, blueBalls;
		public boolean goodDayToDieHard;
		public ByteBuffer lolcat;
		public ArrayList<ComplexObject> someMoreComplexObject = new ArrayList<ComplexObject>();
		public HashMap<String, Integer> someScores = new HashMap<String, Integer>();
		public HashMap<SimpleObject, Integer> objectKeyedMap = new HashMap<SimpleObject, Integer>();
		public ComplexObject() {
			
		}
		
		public ComplexObject(String name, int redBalloons, int blueBalls, boolean goodDayToDieHard, byte[] lolcat, ArrayList<ComplexObject> someMoreComplexObjects, HashMap<String, Integer> someScores) {
			this.name = name;
			this.redBalloons = redBalloons;
			this.blueBalls = blueBalls;
			this.goodDayToDieHard = goodDayToDieHard;
			if (lolcat != null) {
				this.lolcat = ByteBuffer.allocate(lolcat.length); this.lolcat.put(lolcat);
			}
			
			if (someScores != null) this.someScores = someScores;
			if (someMoreComplexObjects != null) this.someMoreComplexObject = someMoreComplexObjects;
			this.objectKeyedMap.put(new SimpleObject(), 1);
		}
		
		public boolean equals(Object object) {
			if (!(object instanceof ComplexObject)) return false;
			ComplexObject co = (ComplexObject) object;
			return co.name.equals(this.name)
					&& co.redBalloons == this.redBalloons
					&& co.blueBalls == this.blueBalls
					&& co.goodDayToDieHard == this.goodDayToDieHard
					&& ((co.lolcat == null && this.lolcat == null) || (co.lolcat != null?co.lolcat.equals(this.lolcat):false))
					&& co.someScores.equals(this.someScores)
					&& co.someMoreComplexObject.equals(this.someMoreComplexObject);
		}
		
		public String toString() {
			String out = name + "/ " + redBalloons + "/ " + blueBalls + "/ " + lolcat + "/" + (goodDayToDieHard==true?"it's a good day to die hard":"it's not a good day to die hard") + "/" + someScores;
			if (someMoreComplexObject.size() > 0) out += "\nChildren: ";
			for (ComplexObject co:someMoreComplexObject) {
				out += co.toString()+", ";
			}
			return out;
		}
		
	}
	
	@Before
	public void setUp() throws IOException {
		
		ArrayList<ComplexObject> complexObjectsList = new ArrayList<ComplexObject>();
		HashMap<String, Integer> scoresOne = new HashMap<String, Integer>();
		scoresOne.put("foo", 123);
		scoresOne.put("bar", 6);

		HashMap<String, Integer> scoresTwo = new HashMap<String, Integer>();
		scoresTwo.put("bzzz", 28394);
		scoresTwo.put("a", 1);	
		
		complexObjectsList.add(new ComplexObject("absolutely not complex object", 1, 3, false, null, null, scoresOne));
		complexObjectsList.add(new ComplexObject("slightly complex object", 4, 99, true, Files.toByteArray(new File("src/test/resources/14684_10151122609582396_558775634_n.jpg")), null, null));
		
		co = new ComplexObject("not too complex object", 99, 2, false, Files.toByteArray(new File("src/test/resources/1367665605997.gif")), complexObjectsList, scoresTwo);
		
	}
	
	@Test
	public void testComplexObjectEquityOp() throws IOException {

		HashMap<String, Integer> scoresOne = new HashMap<String, Integer>();
		scoresOne.put("foo", 123);
		scoresOne.put("bar", 6);
		
		HashMap<String, Integer> scoresTwo = new HashMap<String, Integer>();
		scoresTwo.put("bzzz", 28394);
		scoresTwo.put("a", 1);
		
		ArrayList<ComplexObject> complexObjectsList = new ArrayList<ComplexObject>();
		complexObjectsList.add(new ComplexObject("absolutely not complex object", 1, 3, false, null, null, scoresOne));
		complexObjectsList.add(new ComplexObject("slightly complex object", 4, 99, true, Files.toByteArray(new File("src/test/resources/14684_10151122609582396_558775634_n.jpg")), null, null));
		
		ComplexObject complexObject = new ComplexObject("not too complex object", 99, 2, false, Files.toByteArray(new File("src/test/resources/1367665605997.gif")), complexObjectsList, scoresTwo);
		
		assertTrue(co.equals(complexObject));
		
	}
	
	@Test
	public void testSerializeComplexObject() throws TransformationException, NoSuchFieldException, SecurityException {
		
		TransformerRegistry.getInstance().register(String.class, Integer.class, ComplexObject.class.getField("someScores"));
		TransformerRegistry.getInstance().register(SimpleObject.class, Integer.class, ComplexObject.class.getField("objectKeyedMap"));
		
		ListTransformer<ArrayList> childObjectsListTransformer = new ListTransformer<ArrayList>();
		childObjectsListTransformer.setValueGenericType(ComplexObject.class);
	
		TransformerRegistry.getInstance().register(childObjectsListTransformer, ComplexObject.class.getField("someMoreComplexObject"));
	
		Node serializedCo = transformer.transform(co);

		ComplexObject deserializedCo = transformer.transform(serializedCo);
			
		assertTrue(deserializedCo.equals(co));
		
	}
}
