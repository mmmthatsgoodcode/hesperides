package com.mmmthatsgoodcode.hesperides.core.type;

import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Hesperides.Hint;


public class WildcardValue extends NullValue {

	@Override
	public Hesperides.Hint getHint() {
		return Hesperides.Hint.WILDCARD;
	}
	
}