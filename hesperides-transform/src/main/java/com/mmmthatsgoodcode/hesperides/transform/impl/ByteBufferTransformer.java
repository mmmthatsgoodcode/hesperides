package com.mmmthatsgoodcode.hesperides.transform.impl;

import java.nio.ByteBuffer;

import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.Transformer;

public class ByteBufferTransformer implements Transformer<ByteBuffer> {

	@Override
	public Node transform(ByteBuffer object) {
		Node byteNode = new NodeImpl();
		
		byteNode.setValue((ByteBuffer)object);
		byteNode.setType(ByteBuffer.class);
		
		return byteNode;
	}

	@Override
	public ByteBuffer transform(Node node) {
		
		return (ByteBuffer) node.getValue();
	}

}
