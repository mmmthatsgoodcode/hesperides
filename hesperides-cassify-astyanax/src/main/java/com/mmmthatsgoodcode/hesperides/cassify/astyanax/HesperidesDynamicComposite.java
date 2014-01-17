package com.mmmthatsgoodcode.hesperides.cassify.astyanax;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.mmmthatsgoodcode.hesperides.cassify.AbstractConfigurableCassifier.CassandraTypes;
import com.netflix.astyanax.Serializer;
import com.netflix.astyanax.model.AbstractComposite;
import com.netflix.astyanax.model.DynamicComposite;
import com.netflix.astyanax.model.AbstractComposite.Component;
import com.netflix.astyanax.model.AbstractComposite.ComponentEquality;
import com.netflix.astyanax.serializers.AsciiSerializer;
import com.netflix.astyanax.serializers.BigIntegerSerializer;
import com.netflix.astyanax.serializers.BooleanSerializer;
import com.netflix.astyanax.serializers.ByteBufferOutputStream;
import com.netflix.astyanax.serializers.ByteBufferSerializer;
import com.netflix.astyanax.serializers.ByteSerializer;
import com.netflix.astyanax.serializers.BytesArraySerializer;
import com.netflix.astyanax.serializers.ComparatorType;
import com.netflix.astyanax.serializers.FloatSerializer;
import com.netflix.astyanax.serializers.IntegerSerializer;
import com.netflix.astyanax.serializers.LongSerializer;
import com.netflix.astyanax.serializers.SerializerTypeInferer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.serializers.UUIDSerializer;

public class HesperidesDynamicComposite extends AbstractComposite {

    	private static final Logger LOG = LoggerFactory.getLogger(HesperidesDynamicComposite.class);
    
	private static ImmutableBiMap<String, Character> cassandraTypeAliases = new ImmutableBiMap.Builder<String, Character>()
			.put(CassandraTypes.UTF8Type, 'c')
			.put(CassandraTypes.IntegerType, 'i')
			.put(CassandraTypes.Int32Type, 'h')
			.put(CassandraTypes.FloatType, 'f')
			.put(CassandraTypes.LongType, 'l')
			.put(CassandraTypes.BooleanType, 'o')
			.put(CassandraTypes.LexicalUUIDType, 'x')
			.put(CassandraTypes.TimeUUIDType, 't')
			.put(CassandraTypes.UUIDType, 'u')
			.put(CassandraTypes.BytesType, 'b')
			.put(CassandraTypes.AsciiType, 'a')
			.put(CassandraTypes.DateType, 'd')
//			.put(CassandraTypes.EmptyType, 'n')
			.build();

	    static final ImmutableClassToInstanceMap<Serializer> SERIALIZERS = new ImmutableClassToInstanceMap.Builder<Serializer>()
	            .put(IntegerSerializer.class,    IntegerSerializer.get())
	            .put(BooleanSerializer.class,    BooleanSerializer.get())
	            .put(FloatSerializer.class, FloatSerializer.get()) // <--- this. fucking. line.
	            .put(AsciiSerializer.class,      AsciiSerializer.get())
	            .put(BigIntegerSerializer.class, BigIntegerSerializer.get())
	            .put(ByteBufferSerializer.class, ByteBufferSerializer.get())
	            .put(LongSerializer.class,       LongSerializer.get())
	            .put(StringSerializer.class,     StringSerializer.get())
	            .put(UUIDSerializer.class,       UUIDSerializer.get()).build();    

	    private static BiMap<Class<? extends Serializer>, String> serializerToComparatorMapping = new ImmutableBiMap.Builder<Class<? extends Serializer>, String>()
	            .put(AsciiSerializer.class,      AsciiSerializer.get().getComparatorType().getTypeName())
	            .put(BigIntegerSerializer.class, BigIntegerSerializer.get().getComparatorType().getTypeName())
	            .put(BytesArraySerializer.class,       BytesArraySerializer.get().getComparatorType().getTypeName())
	            .put(FloatSerializer.class,      FloatSerializer.get().getComparatorType().getTypeName())
	            .put(LongSerializer.class,       LongSerializer.get().getComparatorType().getTypeName())
	            .put(IntegerSerializer.class,    IntegerSerializer.get().getComparatorType().getTypeName())
	            .put(BooleanSerializer.class,    BooleanSerializer.get().getComparatorType().getTypeName())
	            .put(StringSerializer.class,     StringSerializer.get().getComparatorType().getTypeName())
	            .put(UUIDSerializer.class,       UUIDSerializer.get().getComparatorType().getTypeName()).build();
	
	    private static BiMap<Byte, String> aliasToComparatorMapping = new ImmutableBiMap.Builder<Byte, String>()
		        .put((byte) ((char) cassandraTypeAliases.get(CassandraTypes.AsciiType)), ComparatorType.ASCIITYPE.getTypeName())
		        .put((byte) ((char) cassandraTypeAliases.get(CassandraTypes.BytesType)), ComparatorType.BYTESTYPE.getTypeName())
		        .put((byte) ((char) cassandraTypeAliases.get(CassandraTypes.BooleanType)), ComparatorType.BOOLEANTYPE.getTypeName())
		        .put((byte) ((char) cassandraTypeAliases.get(CassandraTypes.DateType)), ComparatorType.DATETYPE.getTypeName())
	  	        .put((byte) ((char) cassandraTypeAliases.get(CassandraTypes.FloatType)), ComparatorType.FLOATTYPE.getTypeName())  	        
		        .put((byte) ((char) cassandraTypeAliases.get(CassandraTypes.IntegerType)), ComparatorType.INT32TYPE.getTypeName())
		        .put((byte) ((char) cassandraTypeAliases.get(CassandraTypes.LexicalUUIDType)), ComparatorType.LEXICALUUIDTYPE.getTypeName())
		        .put((byte) ((char) cassandraTypeAliases.get(CassandraTypes.LongType)), ComparatorType.LONGTYPE.getTypeName())
		        .put((byte) ((char) cassandraTypeAliases.get(CassandraTypes.TimeUUIDType)), ComparatorType.TIMEUUIDTYPE.getTypeName())
		        .put((byte) ((char) cassandraTypeAliases.get(CassandraTypes.UTF8Type)), ComparatorType.UTF8TYPE.getTypeName())
		        .put((byte) ((char) cassandraTypeAliases.get(CassandraTypes.UUIDType)), ComparatorType.UUIDTYPE.getTypeName()).build();
	
	    private static Map<Class<? extends Serializer>, Character> serializerToAliasMapping = new ImmutableBiMap.Builder<Class<? extends Serializer>, Character>()
		    .put(AsciiSerializer.class,      cassandraTypeAliases.get(CassandraTypes.AsciiType))
	            .put(BytesArraySerializer.class, cassandraTypeAliases.get(CassandraTypes.BytesType))
	            .put(FloatSerializer.class,      cassandraTypeAliases.get(CassandraTypes.FloatType))
	            .put(LongSerializer.class,       cassandraTypeAliases.get(CassandraTypes.LongType))
	            .put(IntegerSerializer.class,    cassandraTypeAliases.get(CassandraTypes.IntegerType))
	            .put(BooleanSerializer.class,    cassandraTypeAliases.get(CassandraTypes.BooleanType))
	            .put(StringSerializer.class,     cassandraTypeAliases.get(CassandraTypes.UTF8Type))
	            .put(UUIDSerializer.class,       cassandraTypeAliases.get(CassandraTypes.UUIDType)).build();		    
	    
	public static String dynamicCompositeTypeDescriptor() {
		
		List<String> types = new ArrayList<String>();
		for (Entry<Character, String> aliasAndType:cassandraTypeAliases.inverse().entrySet()) {
			types.add(aliasAndType.getKey()+"=>"+aliasAndType.getValue());
		}
		
		return "DynamicCompositeType("+StringUtils.join(types.toArray(), ",")+")";
		
	}
	
	public static Map<Class<? extends Serializer>, Character> serializerToAliasMapping() {
	    return serializerToAliasMapping;
	}
	
    public HesperidesDynamicComposite() {
        super(true);
    }

    public HesperidesDynamicComposite(Object... o) {
	super(true);
        this.addAll(Arrays.asList(o));
    }

    public HesperidesDynamicComposite(List<?> l) {
	super(true);
        this.addAll(l);
    }

    

    /* WARNING
     * --------
     * 
     * SHIT GUTTED FROM AbstractComposite below
     * 
     */
    
    ByteBuffer serialized = null;
    List<Component<?>> components = new ArrayList<Component<?>>();
    List<String> comparatorsByPosition = new ArrayList<String>();
    List<Serializer<?>> serializersByPosition = new ArrayList<Serializer<?>>();
    private boolean dynamic = true;
    
    @Override
    @SuppressWarnings("unchecked")
    public ByteBuffer serialize() {
        if (serialized != null) {
            return serialized.duplicate();
        }

        ByteBufferOutputStream out = new ByteBufferOutputStream();

        int i = 0;
        for (Component c : components) {
            Serializer<?> s = serializerForPosition(i);

            ByteBuffer cb = c.getBytes(s);
            if (cb == null) {
                cb = ByteBuffer.allocate(0);
            }

            String comparator = comparatorForPosition(i);
            if (comparator == null) {
                comparator = c.getComparator();
            }
            if (comparator == null) {
                comparator = ComparatorType.BYTESTYPE.getTypeName();
            }
            int p = comparator.indexOf("(reversed=true)");
            boolean desc = false;
            if (p >= 0) {
                comparator = comparator.substring(0, p);
                desc = true;
            }
            if (aliasToComparatorMapping.inverse().containsKey(comparator)) {
                byte a = aliasToComparatorMapping.inverse().get(comparator);
                if (desc) {
                    a = (byte) Character.toUpperCase((char) a);
                }
                out.writeShort((short) (0x8000 | a));
            }
            else {
                out.writeShort((short) comparator.length());
                out.write(ByteBufferUtil.bytes(comparator));
            }
            
            out.writeShort((short) cb.remaining());
            out.write(cb.slice());
            out.write(c.getEquality().toByte());
            i++;
        }

        serialized = out.getByteBuffer();
        
        return serialized.duplicate();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void deserialize(ByteBuffer b) {
        serialized = b.duplicate();
        components = new ArrayList<Component<?>>();
        LOG.debug("Total data size {}", b.remaining());
        
        String comparator = null;
        int i = 0;
        while ((comparator = getComparator(i, b)) != null) {
            LOG.debug("Next part is {}, remaining data {}", comparator, b.remaining());
            
            ByteBuffer data = getWithShortLength(b);
            
//            ByteBuffer inspect = data.slice(); byte[] inspectbuff = new byte[inspect.remaining()];
//            LOG.debug("Reading {} bytes..", inspect.remaining());
//            inspect.get(inspectbuff);
//            LOG.debug("Data is .. {}", new String(inspectbuff));
            if (data != null) {
                Serializer<?> s = getSerializer(i, comparator);
                
                ComponentEquality equality = ComponentEquality.fromByte(b.get());
                
                byte[] componentData = new byte[data.remaining()];
                data.get(componentData);
                components.add(new Component(null, ByteBuffer.wrap(componentData), s, comparator, equality));
            }
            else {
                throw new RuntimeException("Missing component data in composite type");
            }
            i++;
        }
                
    }
    
    private String getComparator(int i, ByteBuffer bb) {
        String name = comparatorForPosition(i);
        if (name != null) {
            return name;
        }

        if (bb.hasRemaining()) {
            try {
                int header = getShortLength(bb);
                if ((header & 0x8000) == 0) {
                    name = ByteBufferUtil.string(getBytes(bb, header));
                }
                else {
                    byte a = (byte) (header & 0xFF);
                    name = getAliasesToComparatorMapping().get(a);
                    if (name == null) {
                        a = (byte) Character.toUpperCase((char) a);
                        name = getAliasesToComparatorMapping().get(a);
                        if (name != null) {
                            name += "(reversed=true)";
                        }
                    }
                }
            }
            catch (CharacterCodingException e) {
                throw new RuntimeException(e);
            }
        }
        if ((name != null) && (name.length() == 0)) {
            name = null;
        }
        return name;
    }

    public List<Component<?>> getComponents() {
        return components;
    }

    public void setComponents(List<Component<?>> components) {
        serialized = null;
        this.components = components;
    }

    public Map<Class<? extends Serializer>, String> getSerializerToComparatorMapping() {
        return serializerToComparatorMapping;
    }

    public void setSerializerToComparatorMapping(Map<Class<? extends Serializer>, String> serializerToComparatorMapping) {
        serialized = null;
        this.serializerToComparatorMapping = new ImmutableBiMap.Builder<Class<? extends Serializer>, String>().putAll(
                serializerToComparatorMapping).build();
    }

    public Map<Byte, String> getAliasesToComparatorMapping() {
        return aliasToComparatorMapping;
    }

    public void setAliasesToComparatorMapping(Map<Byte, String> aliasesToComparatorMapping) {
        serialized = null;
        aliasToComparatorMapping = new ImmutableBiMap.Builder<Byte, String>().putAll(aliasesToComparatorMapping)
                .build();
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public List<Serializer<?>> getSerializersByPosition() {
        return serializersByPosition;
    }

    public void setSerializersByPosition(List<Serializer<?>> serializersByPosition) {
        this.serializersByPosition = serializersByPosition;
    }

    public void setSerializersByPosition(Serializer<?>... serializers) {
        serializersByPosition = Arrays.asList(serializers);
    }

    public void setSerializerByPosition(int index, Serializer<?> s) {
        if (serializersByPosition == null) {
            serializersByPosition = new ArrayList<Serializer<?>>();
        }
        while (serializersByPosition.size() <= index) {
            serializersByPosition.add(null);
        }
        serializersByPosition.set(index, s);
    }

    public List<String> getComparatorsByPosition() {
        return comparatorsByPosition;
    }

    public void setComparatorsByPosition(List<String> comparatorsByPosition) {
        this.comparatorsByPosition = comparatorsByPosition;
    }

    public void setComparatorsByPosition(String... comparators) {
        comparatorsByPosition = Arrays.asList(comparators);
    }

    public void setComparatorByPosition(int index, String c) {
        if (comparatorsByPosition == null) {
            comparatorsByPosition = new ArrayList<String>();
        }
        while (comparatorsByPosition.size() <= index) {
            comparatorsByPosition.add(null);
        }
        comparatorsByPosition.set(index, c);
    }

    @Override
    public int compareTo(AbstractComposite o) {
        return serialize().compareTo(o.serialize());
    }

    private String comparatorForSerializer(Serializer<?> s) {
        String comparator = serializerToComparatorMapping.get(s.getClass());
        if (comparator != null) {
            return comparator;
        }
        return ComparatorType.BYTESTYPE.getTypeName();
    }

    private Serializer<?> serializerForComparator(String c) {
        int p = c.indexOf('(');
        if (p >= 0) {
            c = c.substring(0, p);
        }
        if (ComparatorType.LEXICALUUIDTYPE.getTypeName().equals(c)
                || ComparatorType.TIMEUUIDTYPE.getTypeName().equals(c)) {
            return UUIDSerializer.get();
        }

        Serializer<?> s = SERIALIZERS.getInstance(serializerToComparatorMapping.inverse().get(c));
        if (s != null) {
            return s;
        }
        return ByteBufferSerializer.get();
    }

    private Serializer<?> serializerForPosition(int i) {
        if (serializersByPosition == null) {
            return null;
        }
        if (i >= serializersByPosition.size()) {
            return null;
        }
        return serializersByPosition.get(i);
    }

    private Serializer<?> getSerializer(int i, String c) {
        Serializer<?> s = serializerForPosition(i);
        if (s != null) {
            return s;
        }
        return serializerForComparator(c);
    }

    private String comparatorForPosition(int i) {
        if (comparatorsByPosition == null) {
            return null;
        }
        if (i >= comparatorsByPosition.size()) {
            return null;
        }
        return comparatorsByPosition.get(i);
    }

    @Override
    public void clear() {
        serialized = null;
        components = new ArrayList<Component<?>>();
    }

    @Override
    public int size() {
        return components.size();
    }

    public <T> AbstractComposite addComponent(T value, Serializer<T> s) {

        addComponent(value, s, comparatorForSerializer(s));

        return this;

    }

    public <T> AbstractComposite addComponent(T value, Serializer<T> s, ComponentEquality equality) {

        addComponent(value, s, comparatorForSerializer(s), equality);

        return this;

    }

    public <T> AbstractComposite addComponent(T value, Serializer<T> s, String comparator) {

        addComponent(value, s, comparator, ComponentEquality.EQUAL);

        return this;

    }

    public <T> AbstractComposite addComponent(T value, Serializer<T> s, String comparator, ComponentEquality equality) {

        addComponent(-1, value, s, comparator, equality);

        return this;

    }

    @SuppressWarnings("unchecked")
    public <T> AbstractComposite addComponent(int index, T value, Serializer<T> s, String comparator,
            ComponentEquality equality) {
        serialized = null;

        if (index < 0) {
            index = components.size();
        }

        while (components.size() < index) {
            components.add(null);
        }
        components.add(index, new Component(value, null, s, comparator, equality));

        return this;

    }

    private static Object mapIfNumber(Object o) {
        if ((o instanceof Byte) || (o instanceof Integer) || (o instanceof Short)) {
            return BigInteger.valueOf(((Number) o).longValue());
        }
        return o;
    }

    @SuppressWarnings({ "unchecked" })
    private static Collection<?> flatten(Collection<?> c) {
        if (c instanceof AbstractComposite) {
            return ((AbstractComposite) c).getComponents();
        }
        boolean hasCollection = false;
        for (Object o : c) {
            if (o instanceof Collection) {
                hasCollection = true;
                break;
            }
        }
        if (!hasCollection) {
            return c;
        }
        List newList = new ArrayList();
        for (Object o : c) {
            if (o instanceof Collection) {
                newList.addAll(flatten((Collection) o));
            }
            else {
                newList.add(o);
            }
        }
        return newList;
    }

    @Override
    public boolean addAll(Collection<? extends Object> c) {
        return super.addAll(flatten(c));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return super.containsAll(flatten(c));
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return super.removeAll(flatten(c));
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return super.retainAll(flatten(c));
    }

    @Override
    public boolean addAll(int i, Collection<? extends Object> c) {
        return super.addAll(i, flatten(c));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void add(int index, Object element) {
        serialized = null;

        if (element instanceof Component) {
            components.add(index, (Component<?>) element);
            return;
        }

        element = mapIfNumber(element);
        Serializer s = serializerForPosition(index);
        if (s == null) {
            s = SerializerTypeInferer.getSerializer(element);
        }
        String c = comparatorForPosition(index);
        if (c == null) {
            c = comparatorForSerializer(s);
        }
        components.add(index, new Component(element, null, s, c, ComponentEquality.EQUAL));
    }

    @Override
    public Object remove(int index) {
        serialized = null;
        Component prev = components.remove(index);
        if (prev != null) {
            return prev.getValue();
        }
        return null;
    }

    public <T> AbstractComposite setComponent(int index, T value, Serializer<T> s) {

        setComponent(index, value, s, comparatorForSerializer(s));

        return this;

    }

    public <T> AbstractComposite setComponent(int index, T value, Serializer<T> s, String comparator) {

        setComponent(index, value, s, comparator, ComponentEquality.EQUAL);

        return this;

    }

    @SuppressWarnings("unchecked")
    public <T> AbstractComposite setComponent(int index, T value, Serializer<T> s, String comparator,
            ComponentEquality equality) {
        serialized = null;

        while (components.size() <= index) {
            components.add(null);
        }
        components.set(index, new Component(value, null, s, comparator, equality));

        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public Object set(int index, Object element) {
        serialized = null;

        if (element instanceof Component) {
            Component prev = components.set(index, (Component<?>) element);
            if (prev != null) {
                return prev.getValue();
            }
            return null;
        }

        element = mapIfNumber(element);
        Serializer s = serializerForPosition(index);
        if (s == null) {
            s = SerializerTypeInferer.getSerializer(element);
        }
        String c = comparatorForPosition(index);
        if (c == null) {
            c = comparatorForSerializer(s);
        }
        Component prev = components.set(index, new Component(element, null, s, c, ComponentEquality.EQUAL));
        if (prev != null) {
            return prev.getValue();
        }
        return null;
    }

    @Override
    public Object get(int i) {
        Component c = components.get(i);
        if (c != null) {
            return c.getValue();
        }
        return null;
    }

    public <T> T get(int i, Serializer<T> s) throws ClassCastException {
        T value = null;
        Component<?> c = components.get(i);
        if (c != null) {
            value = c.getValue(s);
        }
        return value;
    }

    public Component getComponent(int i) {
        if (i >= components.size()) {
            return null;
        }
        Component c = components.get(i);
        return c;
    }

    public Iterator<Component<?>> componentsIterator() {
        return components.iterator();
    }

    protected static int getShortLength(ByteBuffer bb) {
        int length = (bb.get() & 0xFF) << 8;
        return length | (bb.get() & 0xFF);
    }

    protected static ByteBuffer getBytes(ByteBuffer bb, int length) {
        ByteBuffer copy = bb.duplicate();
        copy.limit(copy.position() + length);
        bb.position(bb.position() + length);
        return copy;
    }

    protected static ByteBuffer getWithShortLength(ByteBuffer bb) {
        int length = getShortLength(bb);
        return getBytes(bb, length);
    }
    
    public static HesperidesDynamicComposite fromByteBuffer(ByteBuffer byteBuffer) {
	HesperidesDynamicComposite composite = new HesperidesDynamicComposite();
        composite.deserialize(byteBuffer);

        return composite;
    }

    public static ByteBuffer toByteBuffer(Object... o) {
	HesperidesDynamicComposite composite = new HesperidesDynamicComposite(o);
        return composite.serialize();
    }

    public static ByteBuffer toByteBuffer(List<?> l) {
	HesperidesDynamicComposite composite = new HesperidesDynamicComposite(l);
        return composite.serialize();
    }
	
}
