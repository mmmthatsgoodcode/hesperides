package com.mmmthatsgoodcode.hesperides.core;

import java.nio.ByteBuffer;

import com.google.common.collect.ImmutableBiMap;
import com.mmmthatsgoodcode.hesperides.core.serializer.AsciiSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.BooleanSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.ByteBufferSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.CharacterSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.DateSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.FloatSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.IntegerSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.LongSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.NullSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.ObjectSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.ShortSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.StringSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.UUIDSerializer;

public class Hesperides {

	public enum Hint {
		
		ASCII		("x", new AsciiSerializer()),
		BOOLEAN		("b", new BooleanSerializer()),
		BYTES		("a", new ByteBufferSerializer()),
		CHARACTER	("c", new CharacterSerializer()),
		DATE		("d", new DateSerializer()),
		FLOAT		("f", new FloatSerializer()),

		LONG		("l", new LongSerializer()),
		NULL		("n", new NullSerializer()),
		OBJECT		("o", new ObjectSerializer()),
		SHORT		("s", new ShortSerializer()),
		STRING		("u", new StringSerializer()),
		
		UUID		("p", new UUIDSerializer()),
		TIMEUUID	("t", new UUIDSerializer()),
		LEXICALUUID ("q", new UUIDSerializer()),
		
		INT			("i", new IntegerSerializer()),
		INT32		("j", new IntegerSerializer()),		
		
		WILDCARD	("w", null);
		
		private final String alias;
		private final Serializer serializer;
		
		private final static ImmutableBiMap<Class<? extends Object>, Hint> TYPE_TO_HINT = new ImmutableBiMap.Builder<Class<? extends Object>, Hint>()
				.put(Integer.class, INT)
				.put(Short.class, SHORT)
				.put(Long.class, LONG)
				.put(Float.class, FLOAT)
				.put(Boolean.class, BOOLEAN)
				.put(String.class, STRING)
				.put(ByteBuffer.class, BYTES)
				.build();

		public static Hint fromType(Class<? extends Object> type) {
			if (type == null) return NULL;
			return TYPE_TO_HINT.containsKey(type)?TYPE_TO_HINT.get(type):OBJECT;
		}
		
		public static Hint fromStringAlias(String alias) {
			for (Hint h:Hint.values()) {
				if (h.alias().equals(alias)) return h;
			}
			
			return null;
		}
		
		public static Hint fromCharacterAlias(char alias) {
			
			for(Hint h:Hint.values()) {
				if (h.alias().toCharArray()[0] == alias) return h;
			}
			
			return null;
			
		}
		
		public static Hint fromSerializer(Serializer serializer) {
			for (Hint h:Hint.values()) {
				if (h.serializer() != null && h.serializer().getClass().equals(serializer.getClass())) return h;
			}
			
			return null;
		}
		
		Hint(String alias, Serializer serializer) {
			this.alias = alias;
			this.serializer = serializer;
		}
		
		public String alias() {
			return alias;
		}
		
		public Serializer serializer() {
			return serializer;
		}
		
	}
	
}
