package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.ArrayList;
import java.util.List;

import com.mmmthatsgoodcode.hesperides.cassify.HesperidesColumn.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.Transformer;

public class HesperidesColumnTransformer implements Transformer<List<HesperidesColumn>> {
	
	@Override
	public Node transform(List<HesperidesColumn> columns) throws TransformationException {
		return transform(columns, null, null);
	}
	
	public Node transform(List<HesperidesColumn> columns, HesperidesColumn parentColumn, Node parentNode)
			throws TransformationException {
		
		if (parentColumn == null) { // if there is no parent column, we'll have to find the root in the list of columns and create a node for it
			
			parentColumn = rootColumnIn(columns);
			parentNode = hesperidesColumnToNode(parentColumn);
			
		}
		
		// ok, we should have a parent node of some kind now.
		
		for (HesperidesColumn descendantColumn:directDescendantsOf(parentColumn, columns)) {
			// create Node for descendant column and attach it to parent node
			Node descendantNode = hesperidesColumnToNode(descendantColumn); 
			parentNode.addChild(descendantNode);
			transform(columns, descendantColumn, descendantNode);
		}
		
		return parentNode;
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
		
		nodes.add(column);
		// add each child
		for(Node child:parent) {
			nodes.addAll(transform(child, column));
			
		}
		
		return nodes;
		
	}
	
	protected HesperidesColumn nodeToHesperidesColumn(Node node, HesperidesColumn previous) throws TransformationException {
		
		HesperidesColumn hesperidesColumn = new HesperidesColumn();
		
		/* Add ancestor components
		--------------------------- */
		if (previous != null) hesperidesColumn.addComponents(previous.getComponents());
		
//		if (previous != null) {
		

		
//		}
		
		/* Add The actual type if this node represents a Type 
		------------------------------------------------------ */
		if (node.getHint() == Hesperides.Hints.OBJECT) {
		
			hesperidesColumn.addComponent(node.getType()); // add type
		
		/* Create a Cassandra-compatible serialized byte array from node.value
		----------------------------------------------------------------------- */
		} else {

			hesperidesColumn.addNullComponent(); // add placeholder for type
			
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
			
		}
		
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
		
		return hesperidesColumn;
		
	}
	
	public Node hesperidesColumnToNode(HesperidesColumn column) {
		
		Node node = new NodeImpl();
		
		HesperidesColumn.ClassValue typeComponent = null;
		if (column.getComponents().get(column.getComponents().size()-1) instanceof HesperidesColumn.ClassValue) typeComponent = (HesperidesColumn.ClassValue) column.getComponents().get(column.getComponents().size()-1);
		
		/* Get type from Column's component list
		----------------------------------------- */
		if (typeComponent != null) node.setType(typeComponent.getValue());
		
		/* Get name from Column's component list
		----------------------------------------- */
		HesperidesColumn.AbstractType nameComponent = column.getComponents().get(column.getComponents().size()-1);
		
		node.setName(Hesperides.Hints.typeToHint(nameComponent.getValue().getClass()), nameComponent.getValue());
		
		/* Get value
		------------- */
		
		switch(column.getValue().getHint()) {
		
			case Hesperides.Hints.INT:
				node.setValue(((HesperidesColumn.IntegerValue) column.getValue()).getValue());
			break;
			case Hesperides.Hints.LONG:
				node.setValue(((HesperidesColumn.LongValue) column.getValue()).getValue());
			break;
			case Hesperides.Hints.FLOAT:
				node.setValue(((HesperidesColumn.FloatValue) column.getValue()).getValue());
			break;
			case Hesperides.Hints.STRING:
				node.setValue(((HesperidesColumn.StringValue) column.getValue()).getValue());
			break;
			case Hesperides.Hints.BOOLEAN:
				node.setValue(((HesperidesColumn.BooleanValue) column.getValue()).getValue());
			break;
			case Hesperides.Hints.NULL:
				node.setNullValue();
			break;
			// TODO byte[]
		
		}
		
		return node;
	}
	
	public List<HesperidesColumn> directDescendantsOf(HesperidesColumn needle, List<HesperidesColumn> haystack) {
		
		List<HesperidesColumn> descendants = new ArrayList<HesperidesColumn>();
		
		for (HesperidesColumn hay:haystack) {
			
			if (
					hay.getComponents().size() == needle.getComponents().size()+2 // any direct descendant will have 2 more components than the parents inheritable component
					&& hay.getComponents().subList(0, needle.getComponents().size()).equals(needle.getComponents()) // the beginning of the hay's components must match the parents inheritable components
					) {
				descendants.add(hay);
			}
			
		}
		return descendants;
		
	}
	
	public HesperidesColumn rootColumnIn(List<HesperidesColumn> haystack) {
		
		for (HesperidesColumn hay:haystack) {
			if (hay.getComponents().size() == 2 && hay.getComponents().get(0) instanceof HesperidesColumn.ClassValue) {
				return hay;
			}
		}
			
		return null;
		
	}

}
