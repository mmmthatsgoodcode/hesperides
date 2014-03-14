package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;
import com.mmmthatsgoodcode.hesperides.core.AbstractSerializer;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.type.BooleanValue;
import com.mmmthatsgoodcode.hesperides.core.type.ByteArrayValue;
import com.mmmthatsgoodcode.hesperides.core.type.FloatValue;
import com.mmmthatsgoodcode.hesperides.core.type.IntegerValue;
import com.mmmthatsgoodcode.hesperides.core.type.LongValue;
import com.mmmthatsgoodcode.hesperides.core.type.NullValue;
import com.mmmthatsgoodcode.hesperides.core.type.StringValue;

public class HesperidesRowTransformer implements Node.Transformer<HesperidesRow> {
	
	private static final Logger LOG = LoggerFactory.getLogger(HesperidesRowTransformer.class);
	
	private static class HesperidesRowTransformerHolder {
		public static final HesperidesRowTransformer INSTANCE = new HesperidesRowTransformer();
	}

	public static HesperidesRowTransformer getInstance() {
		return HesperidesRowTransformerHolder.INSTANCE;
	}
	
	@Override
	public Node.Builder transform(HesperidesRow row) throws TransformationException {
				
		if (row.getColumns().size() == 0) return new NodeImpl.Builder().setName(row.getKey());
		return transform(row.getColumns());
	}
	
	public Node.Builder transform(List<HesperidesColumn> columns)
			throws TransformationException {
			
		HesperidesColumn rootColumn = rootColumnFrom(columns.get(0));
		LOG.debug("Found root column {}", rootColumn);
		Node.Builder rootNode = hesperidesColumnToNode(null, rootColumn);
			
		
		for (HesperidesColumn column:columns) {
			// create Node for descendant column and attach it to parent node
			rootNode.addChild(hesperidesColumnToNode(rootNode, column));
		}
		
		return rootNode;
	}
	
	public Node.Locator nameComponentsToLocator(List<AbstractType> nameComponents) throws TransformationException {
		
		Node.Locator nodeLocator = new NodeImpl.Locator();
		
		for (AbstractType nameComponent:nameComponents) {
			
			Node.Builder componentNodeBuilder = new NodeImpl.Builder().setName(nameComponent);
			
				if (nameComponent.getHint() == Hesperides.Hint.OBJECT) componentNodeBuilder.setRepresentedType(nameComponent.getValue().getClass());
			
			nodeLocator.p(componentNodeBuilder.build(null));
			
		}

		return nodeLocator;
		
	}

	@Override
	public HesperidesRow transform(Node parent) throws TransformationException {
		
		HesperidesRow row = new HesperidesRow(parent.getName());
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
		
		/* Add ancestor's name components
		--------------------------- */
		if (previous != null) hesperidesColumn.addNameComponents(previous.getNameComponents());

		
		hesperidesColumn.addNameComponent((node.getValue()!=null?new StringValue(node.getValue().getHint().alias()):new NullValue())); // add value type hint
		
		hesperidesColumn.addNameComponent(node.getRepresentedType().getName()); // add type

		
		/* Add The actual type if this node represents a Type 
		------------------------------------------------------ */
		if (node.getValue() != null && node.getValue().getHint() != Hesperides.Hint.OBJECT) {
			
			switch(node.getValue().getHint()) {
			
				case NULL:
				case STRING:
				case INT:
				case LONG:
				case FLOAT:
				case BOOLEAN:
				case BYTES:
					hesperidesColumn.setValue(node.getValue());
				break;
				default:
					throw new TransformationException("HesperidesColumnTransformer does not do serialization on non-primitive types. "+node.getValue().getClass().getSimpleName()+" is such a type. Pass in a byte array instead.");
			
			}
			
		}
		
		/* Add name as component to the Column's component list
		-------------------------------------------------------- */
		switch(node.getName().getHint()) {
		
			case STRING:
			case INT:
			case LONG:
			case FLOAT:
				hesperidesColumn.addNameComponent(node.getName());
			break;
			default:
				throw new TransformationException("HesperidesRowTransformer does not support node name of type "+node.getName().getClass().getSimpleName());
		
		}
		
//		// add value hint
//		hesperidesColumn.addNameComponent( node.getValueHint() );
		
		// add indexed flag
		hesperidesColumn.setIndexed(node.isIndexed());		
		hesperidesColumn.setCreated(node.getCreated());
		
		return hesperidesColumn;
		
	}
	
	public Node.Builder hesperidesColumnToNode(Node.Builder rootNode, HesperidesColumn column) throws TransformationException {
				
		if (column.getNameComponents().size() % 3 != 0) throw new TransformationException("Malformed column name "+column.getNameComponents());
		
		// process parent nodes..
		
		Node.Builder node = new NodeImpl.Builder().setName(column.getNameComponents().get(column.getNameComponents().size()-1)); Node.Builder parentNode = rootNode; 

		try {
			
			for (List<AbstractType> childNodeData:Lists.partition(column.getNameComponents(), 3)) {
				
				node = new NodeImpl.Builder().setName(childNodeData.get(2)).setRepresentedType(ClassLoader.getSystemClassLoader().loadClass(((StringValue) childNodeData.get(1)).getValue()) );
				if (parentNode != null) parentNode.addChild(node);
				parentNode = node;
				
			}

		} catch (ClassNotFoundException e) {
			
			throw new TransformationException(e);
			
		}
		
		
		/* Get indexed flag
		--------------------- */
		
		node.setIndexed(column.isIndexed());
		
		/* Get value
		------------- */
		
		switch(column.getValue().getHint()) {
		
			case INT:
			case LONG:
			case FLOAT:
			case STRING:
			case BOOLEAN:
			case NULL:
				node.setValue(column.getValue());
			break;
			// TODO byte[]
		
		}
		
		node.setCreated(column.getCreated());
		
		return node;
	}
	
	public List<HesperidesColumn> directDescendantsOf(HesperidesColumn needle, List<HesperidesColumn> haystack) {
		
		List<HesperidesColumn> descendants = new ArrayList<HesperidesColumn>();
		
		for (HesperidesColumn hay:haystack) {

			if (
					hay.getNameComponents().size() == needle.getNameComponents().size()+3 // any direct descendant will have 3 more components than the parents inheritable components
					&& hay.getNameComponents().subList(0, needle.getNameComponents().size()).equals(needle.getNameComponents()) // the beginning of the hay's components must match the parents inheritable components
					) {
//				LOG.debug("{} is a direct descendant of {}", hay, needle);
				descendants.add(hay);
			}
			
		}
		return descendants;
		
	}
	
	public HesperidesColumn rootColumnFrom(HesperidesColumn column) {
		
		return new HesperidesColumn().addNameComponents(column.getNameComponents().subList(0, 3));
		
	}

}
