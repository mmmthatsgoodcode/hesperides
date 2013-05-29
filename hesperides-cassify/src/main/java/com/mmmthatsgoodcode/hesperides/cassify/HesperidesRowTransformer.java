package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.ArrayList;
import java.util.List;

import com.mmmthatsgoodcode.hesperides.cassify.HesperidesColumn.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.Transformer;

public class HesperidesRowTransformer implements Transformer<HesperidesRow> {
	
	@Override
	public Node transform(HesperidesRow row) throws TransformationException {
		return transform(row.getColumns(), null, null);
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
	public HesperidesRow transform(
			Node parent)
			throws TransformationException {
		
		HesperidesRow row = new HesperidesRow((String)parent.getName());
		row.addColumns(transform(parent, null));
		
		return row;
		
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
		if (previous != null) hesperidesColumn.addNameComponents(previous.getNameComponents());
		
//		if (previous != null) {
		

		
//		}
		
		/* Add The actual type if this node represents a Type 
		------------------------------------------------------ */
		if (node.getValueHint() == Hesperides.Hints.OBJECT) {
		
			hesperidesColumn.addNameComponent(node.getRepresentedType()); // add type
		
		/* Create a Cassandra-compatible serialized byte array from node.value
		----------------------------------------------------------------------- */
		} else {

			hesperidesColumn.addNullNameComponent(); // add placeholder for type
			
			switch(node.getValueHint()) {
			
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
				hesperidesColumn.addNameComponent((String) node.getName());
			break;
			case Hesperides.Hints.INT:
				hesperidesColumn.addNameComponent((Integer) node.getName());
			break;
			case Hesperides.Hints.LONG:
				hesperidesColumn.addNameComponent((Long) node.getName());
			break;
			case Hesperides.Hints.FLOAT:
				hesperidesColumn.addNameComponent((Float) node.getName());
			break;
			default:
				throw new TransformationException("HesperidesColumnTransformer does not support node name of type "+node.getName().getClass().getSimpleName());
		
		}
		
		return hesperidesColumn;
		
	}
	
	public Node hesperidesColumnToNode(HesperidesColumn column) {
		
		Node node = new NodeImpl();
		
		HesperidesColumn.ClassValue typeComponent = null;
		if (column.getNameComponents().get(column.getNameComponents().size()-1) instanceof HesperidesColumn.ClassValue) typeComponent = (HesperidesColumn.ClassValue) column.getNameComponents().get(column.getNameComponents().size()-1);
		
		/* Get type from Column's component list
		----------------------------------------- */
		if (typeComponent != null) node.setRepresentedType(typeComponent.getValue());
		
		/* Get name from Column's component list
		----------------------------------------- */
		HesperidesColumn.AbstractType nameComponent = column.getNameComponents().get(column.getNameComponents().size()-1);
		
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
					hay.getNameComponents().size() == needle.getNameComponents().size()+2 // any direct descendant will have 2 more components than the parents inheritable component
					&& hay.getNameComponents().subList(0, needle.getNameComponents().size()).equals(needle.getNameComponents()) // the beginning of the hay's components must match the parents inheritable components
					) {
				descendants.add(hay);
			}
			
		}
		return descendants;
		
	}
	
	public HesperidesColumn rootColumnIn(List<HesperidesColumn> haystack) {
		
		for (HesperidesColumn hay:haystack) {
			if (hay.getNameComponents().size() == 2 && hay.getNameComponents().get(0) instanceof HesperidesColumn.ClassValue) {
				return hay;
			}
		}
			
		return null;
		
	}

}
