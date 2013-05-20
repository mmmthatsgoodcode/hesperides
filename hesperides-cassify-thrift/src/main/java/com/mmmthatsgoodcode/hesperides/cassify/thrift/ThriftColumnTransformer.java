package com.mmmthatsgoodcode.hesperides.cassify.thrift;

import org.apache.cassandra.thrift.Column;

import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.Transformer;

public class ThriftColumnTransformer implements Transformer<Column> {

	@Override
	public Node transform(Column object) throws TransformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Column transform(Node<? extends Object, Column> node)
			throws TransformationException {
		// TODO Auto-generated method stub
		return null;
	}

}
