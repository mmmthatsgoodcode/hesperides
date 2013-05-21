package com.mmmthatsgoodcode.hesperides.core;

import java.nio.ByteBuffer;

public class Hesperides {

	public static class Hints {
		
		public final static int NULL = 0;
		public final static int CLASS_NAME = 1;
		public final static int OBJECT = 2;
		public final static int INT = 3;
		public final static int LONG = 4;
		public final static int FLOAT = 5;
		public final static int STRING = 6;
		public final static int BOOLEAN = 7;
		public final static int BYTES = 8;
		
		public static int typeToHint(Class<? extends Object> type) {
			
			if (type == null) return NULL;
			if (type.equals(Integer.class) || type.equals(Integer.TYPE)) return INT;
			if (type.equals(Long.class) || type.equals(Long.TYPE)) return LONG;
			if (type.equals(Float.class) || type.equals(Float.TYPE)) return LONG;
			if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) return BOOLEAN;
			if (type.equals(String.class)) return STRING;
			if (type.equals(ByteBuffer.class)) return BYTES;
			
			return OBJECT;
		}
		
	}
	
}
