package com.mmmthatsgoodcode.hesperides.core;

import java.nio.ByteBuffer;

import com.google.common.collect.ImmutableBiMap;
import com.mmmthatsgoodcode.hesperides.core.serializer.BooleanSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.FloatSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.IntegerSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.LongSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.NullSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.ShortSerializer;
import com.mmmthatsgoodcode.hesperides.core.serializer.StringSerializer;

public class Hesperides {

	public static class Hints {
		
		public final static int NULL = 0;
		public final static int OBJECT = 1;
		public final static int DATE = 2;
		public final static int SHORT = 3;
		public final static int INT = 4;
		public final static int INT32 = 5;
		public final static int LONG = 6;
		public final static int FLOAT = 7;
		public final static int STRING = 8;
		public final static int LEXICALUUID = 9;
		public final static int TIMEUUID = 10;	
		public final static int UUID = 11;
		public final static int BOOLEAN = 12;
		public final static int BYTES = 13;
		
		public final static ImmutableBiMap<Class<? extends Object>, Integer> TYPE_TO_HINT = new ImmutableBiMap.Builder<Class<? extends Object>, Integer>()
				.put(Integer.class, INT)
				.put(Short.class, SHORT)
				.put(Long.class, LONG)
				.put(Float.class, FLOAT)
				.put(Boolean.class, BOOLEAN)
				.put(String.class, STRING)
				.put(ByteBuffer.class, BYTES)
				.build();
		
		
		public final static ImmutableBiMap<Integer, Serializer> HINT_TO_SERIALIZER = new ImmutableBiMap.Builder<Integer, Serializer>()
				.put(NULL, new NullSerializer())
				.put(INT, new IntegerSerializer())
				.put(SHORT, new ShortSerializer())
				.put(LONG, new LongSerializer())
				.put(FLOAT, new FloatSerializer())
				.put(BOOLEAN, new BooleanSerializer())
				.put(STRING, new StringSerializer())
				.build();
		
		public static int typeToHint(Class<? extends Object> type) {
			if (type == null) return NULL;
			return TYPE_TO_HINT.containsKey(type)?TYPE_TO_HINT.get(type):OBJECT;
		}
		
		public static Serializer hintToSerializer(Integer hint) {
			if (HINT_TO_SERIALIZER.containsKey(hint)) return HINT_TO_SERIALIZER.get(hint);
			throw new IllegalArgumentException("No serializer for hint "+hint);
		}
		
		
	}
	
}
