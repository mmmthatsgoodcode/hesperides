package com.mmmthatsgoodcode.hesperides.cassify.astyanax;

import java.nio.ByteBuffer;
import java.util.Map;

import com.mmmthatsgoodcode.astyanax.HesperidesDynamicCompositeRangeBuilder;
import com.netflix.astyanax.Serializer;
import com.netflix.astyanax.model.Equality;
import com.netflix.astyanax.serializers.ByteBufferOutputStream;
import com.netflix.astyanax.serializers.DynamicCompositeSerializer;
import com.netflix.astyanax.serializers.SerializerTypeInferer;

public class HesperidesDynamicCompositeSerializer extends DynamicCompositeSerializer {

	
    private static final ByteBuffer EMPTY_BYTE_BUFFER  = ByteBuffer.allocate(0);
    private static final HesperidesDynamicCompositeSerializer instance = new HesperidesDynamicCompositeSerializer();

    public static HesperidesDynamicCompositeSerializer get() {
        return instance;
    }
   
    public HesperidesDynamicCompositeRangeBuilder buildRange(final Map<String, Byte> comparatorToAliasMapping) {
        return new HesperidesDynamicCompositeRangeBuilder() {
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

                out.write(aliasFlag);

                out.write((byte)  comparatorToAliasMapping.get( serializer.getComparatorType().getTypeName() ));
                out.writeShort((short) cb.remaining());
                out.write(cb.slice());
                out.write(equality.toByte());
            }

        };
    }
	
	
}
