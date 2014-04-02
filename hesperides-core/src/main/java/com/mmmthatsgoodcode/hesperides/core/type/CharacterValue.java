package com.mmmthatsgoodcode.hesperides.core.type;

import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Serializer;
import com.mmmthatsgoodcode.hesperides.core.Hesperides.Hint;
import com.mmmthatsgoodcode.hesperides.core.serializer.ByteArraySerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.CharacterSerializer;


public class CharacterValue extends AbstractType<Character> {

	private static class SerializerHolder {
		private static final Serializer<Character> INSTANCE = new CharacterSerializer();
	}
	public CharacterValue(Character value) {
		setValue(value);
	}
	
	@Override
	public Hint getHint() {
		return Hesperides.Hint.CHARACTER;
	}
	
	@Override
	public boolean equals(Object object) {
		
		if (!(object instanceof CharacterValue)) return false;
		CharacterValue other = (CharacterValue) object;
		
		return this.getValue()==null?other.getValue()==null:this.getValue().equals(other.getValue());
		
	}
	
	@Override
	public Serializer<Character> getSerializer() {
		return SerializerHolder.INSTANCE;
	}
	
	
	
}