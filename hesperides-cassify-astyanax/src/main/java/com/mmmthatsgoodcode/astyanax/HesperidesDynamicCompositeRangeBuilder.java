package com.mmmthatsgoodcode.astyanax;

import java.nio.ByteBuffer;
import java.util.List;

import com.netflix.astyanax.model.ByteBufferRange;
import com.netflix.astyanax.model.Equality;
import com.netflix.astyanax.serializers.ByteBufferOutputStream;

public abstract class HesperidesDynamicCompositeRangeBuilder implements ByteBufferRange {

    private ByteBufferOutputStream start = new ByteBufferOutputStream();
    private ByteBufferOutputStream end = new ByteBufferOutputStream();
    private int limit = Integer.MAX_VALUE;
    private boolean reversed = false;
    private boolean lockComponent = false;

    abstract protected void nextComponent();

    abstract protected void append(ByteBufferOutputStream out, Object value, Equality equality);

    public HesperidesDynamicCompositeRangeBuilder withPrefix(Object object) {
        if (lockComponent) {
            throw new IllegalStateException("Prefix cannot be added once equality has been specified");
        }
        append(start, object, Equality.EQUAL);
        append(end, object, Equality.EQUAL);
        nextComponent();
        return this;
    }
    
    public HesperidesDynamicCompositeRangeBuilder beginsWith(Object object) {
        if (lockComponent) {
            throw new IllegalStateException("Prefix cannot be added once equality has been specified");
        }
        lockComponent = true;
        append(start, object, Equality.EQUAL);
        append(end, object, Equality.GREATER_THAN);
        nextComponent();
        return this;	
    }
    
    public HesperidesDynamicCompositeRangeBuilder beginsWith(List<Object> objects) {
    	for(Object object:objects) {
    		
    		append(start, object, Equality.EQUAL);
        	append(end, object, Equality.EQUAL);
    		nextComponent();

    	}
    	
    	append(end, 0, Equality.GREATER_THAN);
		nextComponent();

    	return this;
    	
    }

    public HesperidesDynamicCompositeRangeBuilder limit(int count) {
        this.limit = count;
        return this;
    }

    public HesperidesDynamicCompositeRangeBuilder reverse() {
        reversed = true;
        ByteBufferOutputStream temp = start;
        start = end;
        end = temp;
        return this;
    }

    public HesperidesDynamicCompositeRangeBuilder greaterThan(Object value) {
        lockComponent = true;
        append(start, value, Equality.GREATER_THAN);
        return this;
    }

    public HesperidesDynamicCompositeRangeBuilder greaterThanEquals(Object value) {
        lockComponent = true;
        append(start, value, Equality.GREATER_THAN_EQUALS);
        return this;
    }

    public HesperidesDynamicCompositeRangeBuilder lessThan(Object value) {
        lockComponent = true;
        append(end, value, Equality.LESS_THAN);
        return this;
    }

    public HesperidesDynamicCompositeRangeBuilder lessThanEquals(Object value) {
        lockComponent = true;
        append(end, value, Equality.LESS_THAN_EQUALS);
        return this;
    }

    @Override
    @Deprecated
    public ByteBuffer getStart() {
        return start.getByteBuffer();
    }

    @Override
    @Deprecated
    public ByteBuffer getEnd() {
        return end.getByteBuffer();
    }

    @Override
    @Deprecated
    public boolean isReversed() {
        return reversed;
    }

    @Override
    @Deprecated
    public int getLimit() {
        return limit;
    }

    public ByteBufferRange build() {
        return new ByteBufferRange() {
            @Override
            public ByteBuffer getStart() {
                return start.getByteBuffer();
            }

            @Override
            public ByteBuffer getEnd() {
                return end.getByteBuffer();
            }

            @Override
            public boolean isReversed() {
                return reversed;
            }

            @Override
            public int getLimit() {
                return limit;
            }
        };
    }

}
