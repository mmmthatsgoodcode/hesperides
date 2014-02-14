package com.mmmthatsgoodcode.hesperides.cassify.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class HesperidesColumnTest {

    @Test
    public void testEquals() {
	
	HesperidesColumn columnA = new HesperidesColumn().addNameComponent(true).addNameComponent(3.14f).addNameComponent(123).addNameComponent(1234l).addNameComponent("foo").setValue("bar");
	HesperidesColumn columnB = new HesperidesColumn().addNameComponent(true).addNameComponent(3.14f).addNameComponent(123).addNameComponent(1234l).addNameComponent("foo").setValue("bar");
	HesperidesColumn columnC = new HesperidesColumn().addNameComponent("nope").setValue(false);
	
	assertEquals(columnA, columnB);
	assertEquals(columnA.hashCode(), columnB.hashCode());
	assertNotSame(columnA, columnC);
	assertNotSame(columnA.hashCode(), columnC.hashCode());
	
    }
    
}
