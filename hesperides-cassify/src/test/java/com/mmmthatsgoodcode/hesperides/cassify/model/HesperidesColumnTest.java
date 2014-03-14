package com.mmmthatsgoodcode.hesperides.cassify.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mmmthatsgoodcode.hesperides.core.type.BooleanValue;
import com.mmmthatsgoodcode.hesperides.core.type.StringValue;

public class HesperidesColumnTest {

    @Test
    public void testEquals() {
	
	HesperidesColumn columnA = new HesperidesColumn().addNameComponent(true).addNameComponent(3.14f).addNameComponent(123).addNameComponent(1234l).addNameComponent("foo").setValue(new StringValue("bar"));
	HesperidesColumn columnB = new HesperidesColumn().addNameComponent(true).addNameComponent(3.14f).addNameComponent(123).addNameComponent(1234l).addNameComponent("foo").setValue(new StringValue("bar"));
	HesperidesColumn columnC = new HesperidesColumn().addNameComponent("nope").setValue(new BooleanValue(false));
	
	assertEquals(columnA, columnB);
	assertEquals(columnA.hashCode(), columnB.hashCode());
	assertNotSame(columnA, columnC);
	assertNotSame(columnA.hashCode(), columnC.hashCode());
	
    }
    
}
