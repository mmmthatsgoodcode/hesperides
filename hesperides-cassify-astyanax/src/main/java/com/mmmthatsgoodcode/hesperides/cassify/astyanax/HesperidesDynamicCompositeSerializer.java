package com.mmmthatsgoodcode.hesperides.cassify.astyanax;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.mmmthatsgoodcode.astyanax.DynamicCompositeRangeBuilder;
import com.netflix.astyanax.Serializer;
import com.netflix.astyanax.model.Equality;
import com.netflix.astyanax.serializers.AbstractSerializer;
import com.netflix.astyanax.serializers.ByteBufferOutputStream;
import com.netflix.astyanax.serializers.ComparatorType;
import com.netflix.astyanax.serializers.CompositeRangeBuilder;
import com.netflix.astyanax.serializers.SerializerTypeInferer;
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer.ComponentSerializer;

public class HesperidesDynamicCompositeSerializer extends AbstractSerializer<HesperidesDynamicComposite> {

	
    private static final HesperidesDynamicCompositeSerializer instance = new HesperidesDynamicCompositeSerializer();
    private static final ByteBuffer EMPTY_BYTE_BUFFER  = ByteBuffer.allocate(0);

    public static HesperidesDynamicCompositeSerializer get() {
        return instance;
    }

    @Override
    public HesperidesDynamicComposite fromByteBuffer(ByteBuffer byteBuffer) {
    	HesperidesDynamicComposite composite = new HesperidesDynamicComposite();
        composite.deserialize(byteBuffer);
        return composite;
    }
    
    @Override
    public ByteBuffer toByteBuffer(HesperidesDynamicComposite obj) {
        return obj.serialize();
    }
    
    @Override
    public ComparatorType getComparatorType() {
        return ComparatorType.DYNAMICCOMPOSITETYPE;
    }

    @Override
    public ByteBuffer fromString(String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getString(ByteBuffer byteBuffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuffer getNext(ByteBuffer byteBuffer) {
        throw new IllegalStateException("DynamicComposite columns can't be paginated this way.");
    }
    
    public DynamicCompositeRangeBuilder buildRange(final Map<String, Byte> comparatorToAliasMapping) {
        return new DynamicCompositeRangeBuilder() {
            private int position = 0;

            public void nextComponent() {
                position++;
            }

            public void append(ByteBufferOutputStream out, Object value, Equality equality) {
                Serializer serializer = SerializerTypeInferer.getSerializer(value);
                // First, serialize the ByteBuffer for this component
                ByteBuffer cb;
                try {
                    cb = serializer.toByteBuffer(value);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }

                if (cb == null) {
                    cb = EMPTY_BYTE_BUFFER;
                }

                // Write the data: <alias><length><data><equality>
                byte aliasFlag = -1;
//                System.out.println("--- "+aliasFlag+", "+comparatorToAliasMapping.get( serializer.getComparatorType().getTypeName())+", "+cb.remaining());

                out.write(aliasFlag);

                out.write((byte)  comparatorToAliasMapping.get( serializer.getComparatorType().getTypeName() ));
                out.writeShort((short) cb.remaining());
                out.write(cb.slice());
                out.write(equality.toByte());
            }

        };
    }
	
	
}
