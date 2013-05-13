package com.mmmthatsgoodcode.hesperides.serialize;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;
import com.mmmthatsgoodcode.hesperides.core.Node;

public class AnnotatedObjectSerializerTest {

	private ComplexObject co;
	
	public final static class ComplexObject {

		public String name;
		public int redBalloons, blueBalls;
		public boolean goodDayToDieHard;
		public ByteBuffer lolcat;
		public ArrayList<ComplexObject> someMoreComplexObject = new ArrayList<ComplexObject>();
		
		public ComplexObject(String name, int redBalloons, int blueBalls, boolean goodDayToDieHard, byte[] lolcat, ArrayList<ComplexObject> someMoreComplexObjects) {
			this.name = name;
			this.redBalloons = redBalloons;
			this.blueBalls = blueBalls;
			this.goodDayToDieHard = goodDayToDieHard;
			if (lolcat != null) {
				this.lolcat = ByteBuffer.allocate(lolcat.length); this.lolcat.put(lolcat);
			}
			
			if (someMoreComplexObjects != null) this.someMoreComplexObject = someMoreComplexObjects;
		}
		
		public boolean equals(Object object) {
			if (!(object instanceof ComplexObject)) return false;
			ComplexObject co = (ComplexObject) object;
			return co.name.equals(this.name)
					&& co.redBalloons == this.redBalloons
					&& co.blueBalls == this.blueBalls
					&& co.goodDayToDieHard == this.goodDayToDieHard
					&& ((co.lolcat == null && this.lolcat == null) || (co.lolcat != null?co.lolcat.equals(this.lolcat):false))
					&& co.someMoreComplexObject.equals(this.someMoreComplexObject);
		}
		
		public String toString() {
			return name + "/ " + redBalloons + "/ " + blueBalls + "/ " + (goodDayToDieHard==true?"it's a good day to die hard":"it's not a good day to die hard");
		}
		
	}
	
	@Before
	public void setUp() throws IOException {
		
		
		ArrayList<ComplexObject> complexObjectsList = new ArrayList<ComplexObject>();
		complexObjectsList.add(new ComplexObject("absolutely not complex object", 1, 3, false, null, null));
		complexObjectsList.add(new ComplexObject("slightly complex object", 4, 99, true, Files.toByteArray(new File("src/test/resources/14684_10151122609582396_558775634_n.jpg")), null));
		
		co = new ComplexObject("not too complex object", 99, 2, false, Files.toByteArray(new File("src/test/resources/1367665605997.gif")), complexObjectsList);

		
	}
	
	@Test
	public void testComplexObjectEquityOp() throws IOException {

		

		ArrayList<ComplexObject> complexObjectsList = new ArrayList<ComplexObject>();
		complexObjectsList.add(new ComplexObject("absolutely not complex object", 1, 3, false, null, null));
		complexObjectsList.add(new ComplexObject("slightly complex object", 4, 99, true, Files.toByteArray(new File("src/test/resources/14684_10151122609582396_558775634_n.jpg")), null));
		
		ComplexObject complexObject = new ComplexObject("not too complex object", 99, 2, false, Files.toByteArray(new File("src/test/resources/1367665605997.gif")), complexObjectsList);


		assertTrue(co.equals(complexObject));		
		
	}
	
	@Test
	public void testSerializeComplexObject() {
		
	
		Node serializedCo = SerializerRegistry.getInstance().get(ComplexObject.class).serialize(ComplexObject.class, co);
		System.out.println(serializedCo);
	}
}
