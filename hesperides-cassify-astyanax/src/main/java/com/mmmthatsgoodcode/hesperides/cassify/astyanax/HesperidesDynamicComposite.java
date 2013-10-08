package com.mmmthatsgoodcode.hesperides.cassify.astyanax;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableBiMap;
import com.mmmthatsgoodcode.hesperides.cassify.AbstractConfigurableCassifier.CassandraTypes;
import com.netflix.astyanax.Serializer;
import com.netflix.astyanax.model.DynamicComposite;
import com.netflix.astyanax.serializers.BytesArraySerializer;
import com.netflix.astyanax.serializers.ComparatorType;

public class HesperidesDynamicComposite extends DynamicComposite {

	
	private static ImmutableBiMap<String, Character> cassandraTypeAliases = new ImmutableBiMap.Builder<String, Character>()
			.put(CassandraTypes.UTF8Type, 's')
			.put(CassandraTypes.IntegerType, 'i')
			.put(CassandraTypes.Int32Type, 'h')
			.put(CassandraTypes.FloatType, 'f')
			.put(CassandraTypes.LongType, 'l')
			.put(CassandraTypes.BooleanType, 'b')
			.put(CassandraTypes.LexicalUUIDType, 'e')
			.put(CassandraTypes.TimeUUIDType, 't')
			.put(CassandraTypes.UUIDType, 'g')
			.put(CassandraTypes.BytesType, 'c')
			.put(CassandraTypes.AsciiType, 'a')
			.put(CassandraTypes.DateType, 'd')
//			.put(CassandraTypes.EmptyType, 'n')
			.build();
	
	public static ImmutableBiMap<String, Character> getCassandraTypeAliases() {
		
    	// TODO parse&cache configuration
		return cassandraTypeAliases;
	}
	
	public static String dynamicCompositeTypeDescriptor() {
		
		List<String> types = new ArrayList<String>();
		for (Entry<Character, String> aliasAndType:cassandraTypeAliases.inverse().entrySet()) {
			types.add(aliasAndType.getKey()+"=>"+aliasAndType.getValue());
		}
		
		return "DynamicCompositeType("+StringUtils.join(types.toArray(), ",")+")";
		
	}
	
    public HesperidesDynamicComposite() {
        super();
        updateMappings();
    }

    public HesperidesDynamicComposite(Object... o) {
        super(true, o);
        updateMappings();
    }

    public HesperidesDynamicComposite(List<?> l) {
        super(true, l);
        updateMappings();
    }
    
    public void updateMappings() {
    
    	// extend serializer to comparator mapping with bytes array..not sure why this isnt on the default in AbstractComposite
    	setSerializerToComparatorMapping(new ImmutableBiMap.Builder<Class<? extends Serializer>, String>().putAll(DEFAULT_SERIALIZER_TO_COMPARATOR_MAPPING).put(BytesArraySerializer.class, BytesArraySerializer.get().getComparatorType().getTypeName()).build());
    	
    	setAliasesToComparatorMapping(new ImmutableBiMap.Builder<Byte, String>()
    	        .put((byte) ((char) getCassandraTypeAliases().get(CassandraTypes.AsciiType)), ComparatorType.ASCIITYPE.getTypeName())
    	        .put((byte) ((char) getCassandraTypeAliases().get(CassandraTypes.BytesType)), ComparatorType.BYTESTYPE.getTypeName())
    	        .put((byte) ((char) getCassandraTypeAliases().get(CassandraTypes.BooleanType)), ComparatorType.BOOLEANTYPE.getTypeName())
    	        .put((byte) ((char) getCassandraTypeAliases().get(CassandraTypes.DateType)), ComparatorType.DATETYPE.getTypeName())
    	        .put((byte) ((char)	getCassandraTypeAliases().get(CassandraTypes.IntegerType)), ComparatorType.INTEGERTYPE.getTypeName())
    	        .put((byte) ((char) getCassandraTypeAliases().get(CassandraTypes.LexicalUUIDType)), ComparatorType.LEXICALUUIDTYPE.getTypeName())
    	        .put((byte) ((char) getCassandraTypeAliases().get(CassandraTypes.LongType)), ComparatorType.LONGTYPE.getTypeName())
    	        .put((byte) ((char) getCassandraTypeAliases().get(CassandraTypes.TimeUUIDType)), ComparatorType.TIMEUUIDTYPE.getTypeName())
    	        .put((byte) ((char) getCassandraTypeAliases().get(CassandraTypes.UTF8Type)), ComparatorType.UTF8TYPE.getTypeName())
    	        .put((byte) ((char) getCassandraTypeAliases().get(CassandraTypes.UUIDType)), ComparatorType.UUIDTYPE.getTypeName()).build());

    }
    
	
	
}
