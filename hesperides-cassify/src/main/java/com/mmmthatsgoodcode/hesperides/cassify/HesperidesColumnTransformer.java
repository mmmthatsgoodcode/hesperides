package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.ArrayList;
import java.util.List;

import com.mmmthatsgoodcode.hesperides.cassify.HesperidesColumn.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.Transformer;

public class HesperidesColumnTransformer implements Transformer<List<HesperidesColumn>> {

	@Override
	public Node transform(List<HesperidesColumn> object)
			throws TransformationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HesperidesColumn> transform(
			Node<? extends Object, List<HesperidesColumn>> parent)
			throws TransformationException {
		
		return transform(parent, null);
		
	}
	
	protected List<HesperidesColumn> transform(Node<? extends Object, List<HesperidesColumn>> parent, HesperidesColumn parentColumn) throws TransformationException {
		
		List<HesperidesColumn> nodes = new ArrayList<HesperidesColumn>();
		
		// add the parent itself
		HesperidesColumn column = nodeToHesperidesColumn(parent, parentColumn);
		if (parentColumn == null) column.setKey((String)parent.getName());
		
		nodes.add(column);
		// add each child
		for(Node child:parent) {
			nodes.addAll(transform(child, column));
			
		}
		
		return nodes;
		
	}
	
	protected HesperidesColumn nodeToHesperidesColumn(Node node, HesperidesColumn previous) throws TransformationException {
		
		HesperidesColumn hesperidesColumn = new HesperidesColumn();
		
		if (previous != null) hesperidesColumn.setKey(previous.getKey());
		
		/* Add ancestor components
		--------------------------- */
		if (previous != null) hesperidesColumn.addComponents(previous.getComponents());
			
		/* Add name as component to the Column's component list
		-------------------------------------------------------- */
		switch(node.getNameHint()) {
		
			case Hesperides.Hints.STRING:
				hesperidesColumn.addComponent((String) node.getName());
			break;
			case Hesperides.Hints.INT:
				hesperidesColumn.addComponent((Integer) node.getName());
			break;
			case Hesperides.Hints.LONG:
				hesperidesColumn.addComponent((Long) node.getName());
			break;
			case Hesperides.Hints.FLOAT:
				hesperidesColumn.addComponent((Float) node.getName());
			break;
			default:
				throw new TransformationException("HesperidesColumnTransformer does not support node name of type "+node.getName().getClass().getSimpleName());
		
		}
		
		/* Create a Cassandra-compatible serialized byte array from node.value
		----------------------------------------------------------------------- */
		switch(node.getHint()) {
		
			case Hesperides.Hints.NULL:
				hesperidesColumn.setNullValue();
			break;
			case Hesperides.Hints.STRING:
				hesperidesColumn.setValue((String) node.getValue());
			break;
			case Hesperides.Hints.INT:
				hesperidesColumn.setValue((Integer) node.getValue());
			break;
			case Hesperides.Hints.LONG:
				hesperidesColumn.setValue((Long) node.getValue());
			break;
			case Hesperides.Hints.FLOAT:
				hesperidesColumn.setValue((Float) node.getValue());
			break;
			case Hesperides.Hints.BOOLEAN:
				hesperidesColumn.setValue((Boolean) node.getValue());
			break;
			case Hesperides.Hints.BYTES:
				hesperidesColumn.setValue((byte[]) node.getValue());
			break;
			default:
				throw new TransformationException("HesperidesColumnTransformer does not do serialization on non-primitive types. "+node.getValue().getClass().getSimpleName()+" is such a type. Pass in a byte array instead.");
		
		}
		
		return hesperidesColumn;
		
	}

}
