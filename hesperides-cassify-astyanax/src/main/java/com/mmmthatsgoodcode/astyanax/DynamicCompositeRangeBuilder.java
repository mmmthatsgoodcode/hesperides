package com.mmmthatsgoodcode.astyanax;

import java.nio.ByteBuffer;

import com.netflix.astyanax.model.ByteBufferRange;
import com.netflix.astyanax.model.Equality;
import com.netflix.astyanax.serializers.ByteBufferOutputStream;
import com.netflix.astyanax.serializers.CompositeRangeBuilder;

public abstract class DynamicCompositeRangeBuilder implements ByteBufferRange {

    private ByteBufferOutputStream start = new ByteBufferOutputStream();
    private ByteBufferOutputStream end = new ByteBufferOutputStream();
    private int limit = Integer.MAX_VALUE;
    private boolean reversed = false;
    private boolean lockComponent = false;

    abstract protected void nextComponent();

    abstract protected void append(ByteBufferOutputStream out, Object value, Equality equality);

    public DynamicCompositeRangeBuilder withPrefix(Object object) {
        if (lockComponent) {
            throw new IllegalStateException("Prefix cannot be added once equality has been specified");
        }
        append(start, object, Equality.EQUAL);
        append(end, object, Equality.EQUAL);
        nextComponent();
        return this;
    }
    
    public DynamicCompositeRangeBuilder beginsWith(Object object) {
        if (lockComponent) {
            throw new IllegalStateException("Prefix cannot be added once equality has been specified");
        }
        lockComponent = true;
        append(start, object, Equality.EQUAL);
        append(end, object, Equality.GREATER_THAN);
        nextComponent();
        return this;	
    }

    public DynamicCompositeRangeBuilder limit(int count) {
        this.limit = count;
        return this;
    }

    public DynamicCompositeRangeBuilder reverse() {
        reversed = true;
        ByteBufferOutputStream temp = start;
        start = end;
        end = temp;
        return this;
    }

    public DynamicCompositeRangeBuilder greaterThan(Object value) {
        lockComponent = true;
        append(start, value, Equality.GREATER_THAN);
        return this;
    }

    public DynamicCompositeRangeBuilder greaterThanEquals(Object value) {
        lockComponent = true;
        append(start, value, Equality.GREATER_THAN_EQUALS);
        return this;
    }

    public DynamicCompositeRangeBuilder lessThan(Object value) {
        lockComponent = true;
        append(end, value, Equality.LESS_THAN);
        return this;
    }

    public DynamicCompositeRangeBuilder lessThanEquals(Object value) {
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
