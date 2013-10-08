package com.mmmthatsgoodcode.hesperides.cassify.astyanax;

import java.nio.ByteBuffer;

import com.netflix.astyanax.serializers.AbstractSerializer;
import com.netflix.astyanax.serializers.ComparatorType;

public class HesperidesDynamicCompositeSerializer extends AbstractSerializer<HesperidesDynamicComposite> {

	
    private static final HesperidesDynamicCompositeSerializer instance = new HesperidesDynamicCompositeSerializer();

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
	
	
}
