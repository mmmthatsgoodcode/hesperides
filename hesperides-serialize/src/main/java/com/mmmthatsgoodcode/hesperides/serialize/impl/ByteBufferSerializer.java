package com.mmmthatsgoodcode.hesperides.serialize.impl;

import java.nio.ByteBuffer;

import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.serialize.Serializer;

public class ByteBufferSerializer implements Serializer<ByteBuffer> {

	@Override
	public Node serialize(Class type, ByteBuffer object) {
		Node byteNode = new NodeImpl();
		
		byteNode.setValue((ByteBuffer)object);
		byteNode.setType(ByteBuffer.class);
		
		return byteNode;
	}

	@Override
	public ByteBuffer deserialize(Node node) {
		return (ByteBuffer) node.getValue();
	}

}
