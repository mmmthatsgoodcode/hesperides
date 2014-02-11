package com.mmmthatsgoodcode.hesperides.cassify.astyanax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.netflix.astyanax.Serializer;
import com.netflix.astyanax.model.AbstractComposite;

public class HesperidesDynamicComposite extends AbstractComposite {

	public static String dynamicCompositeTypeDescriptor() {
		
		List<String> types = new ArrayList<String>();
		for (Entry<Byte, String> aliasAndType:DEFAULT_ALIAS_TO_COMPARATOR_MAPPING.entrySet()) {
			types.add(new String(new byte[] {aliasAndType.getKey()})+"=>"+aliasAndType.getValue());
		}
		
		return "DynamicCompositeType("+StringUtils.join(types.toArray(), ",")+")";
		
	}
	
	public HesperidesDynamicComposite() {
		super(true);
	}

	public HesperidesDynamicComposite(Object... o) {
		super(true, o);
	}

	public HesperidesDynamicComposite(List<?> l) {
		super(true, l);
	}
	
}
