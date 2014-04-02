package com.mmmthatsgoodcode.hesperides.transform.impl;

import java.nio.ByteBuffer;

import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.type.ByteBufferValue;

public class ByteBufferTransformer implements Node.Transformer<ByteBuffer> {

	@Override
	public Node.Builder transform(ByteBuffer object) {
		Node.Builder byteNode = new NodeImpl.Builder();
		
		byteNode.setValue(new ByteBufferValue(object));
		byteNode.setRepresentedType(ByteBuffer.class);
		
		return byteNode;
	}

	@Override
	public ByteBuffer transform(Node node) {
		
		return (ByteBuffer) node.getValue().getValue();
	}

}
