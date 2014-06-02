package com.mmmthatsgoodcode.hesperides.datastore;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
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
import com.mmmthatsgoodcode.hesperides.datastore.model.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.datastore.model.HesperidesRow;

public class HesperidesRowTransformer implements Node.Transformer<HesperidesRow> {
	
	private static final Logger LOG = LoggerFactory.getLogger(HesperidesRowTransformer.class);
	
	private static class HesperidesRowTransformerHolder {
		public static final HesperidesRowTransformer INSTANCE = new HesperidesRowTransformer();
	}

	public static HesperidesRowTransformer getInstance() {
		return HesperidesRowTransformerHolder.INSTANCE;
	}

	@Override
	public HesperidesRow transform(Node parent) throws TransformationException {
		
		HesperidesRow row = new HesperidesRow(parent.getName());
		for(Object o:parent.getChildren()) {
			Node child = (Node) o;
			
			for (HesperidesColumn column:transform(child, null)) {
				if (column.getValue() != null && !(column.getValue() instanceof NullValue)) row.addColumn(column);
				
			}
			
		}

		LOG.trace("Created row {}", row);

		return row;
		
	}
	
	protected List<HesperidesColumn> transform(Node parent, HesperidesColumn parentColumn) throws TransformationException {
		
		List<HesperidesColumn> columns = new ArrayList<HesperidesColumn>();
		
		// add the parent itself
		HesperidesColumn column = nodeToHesperidesColumn(parent, parentColumn);
		
		columns.add(column);
		// add each child
		for(Object o:parent) {
			Node child = (Node) o;
			columns.addAll(transform(child, column));
			
		}
		
		
		return columns;
		
	}
	
	private HesperidesColumn nodeToHesperidesColumn(Node node, HesperidesColumn previous) throws TransformationException {
						
		HesperidesColumn hesperidesColumn = new HesperidesColumn();
		
		/* Add ancestor's name components
		--------------------------- */
		if (previous != null) hesperidesColumn.addNameComponents(previous.getNameComponents());

		
		/* Add name as component to the Column's name component list
		-------------------------------------------------------- */
		switch(node.getName().getHint()) {
		
			case STRING:
			case INT32:
			case LONG:
			case FLOAT:
				hesperidesColumn.addNameComponent(node.getName());
			break;
			default:
				throw new TransformationException("HesperidesRowTransformer does not support node name of type "+node.getName().getClass().getSimpleName());
		
		}
		
		/* Add The actual type if this node represents a Type 
		------------------------------------------------------ */
		if (node.getChildren().size() == 0) {
			
			switch(node.getValue().getHint()) {
			
				case NULL:
				case STRING:
				case INT32:
				case LONG:
				case FLOAT:
				case BOOLEAN:
				case BYTES:
					hesperidesColumn.setValue(node.getValue());
				break;
				default:
					throw new TransformationException("HesperidesColumnTransformer does not do serialization on non-primitive types. "+node.getValue().getClass().getSimpleName()+" is such a type. Pass in a byte array instead.");
			
			}
			
		} else {
			
			hesperidesColumn.addNameComponent(node.getRepresentedType().getName()); // add type
			
		}

		
		// add indexed flag
		hesperidesColumn.setIndexed(node.isIndexed());		
		hesperidesColumn.setCreated(node.getCreated());
		
		LOG.trace("Transformed {} to {}", node, hesperidesColumn);
		
		return hesperidesColumn;
		
	}

	
	@Override
	public Node.Builder transform(HesperidesRow row) throws TransformationException {
				
		if (row.getColumns().size() == 0) return new NodeImpl.Builder().setName(row.getKey());

//		HesperidesColumn rootColumn = rootColumnFrom(columns.get(0));
//		LOG.debug("Found root column {}", rootColumn);
//		Node.Builder rootNode = hesperidesColumnToNode(null, rootColumn);
			
		Node.Builder rootNode = new NodeImpl.Builder().setName(row.getKey());
		
		
		for (HesperidesColumn column:row.getColumns()) {
			// create Node for descendant column and attach it to parent node
			hesperidesColumnToNode(rootNode, column);
		}
		
		return rootNode;
	}	
	
	public Node.Builder hesperidesColumnToNode(Node.Builder rootNode, HesperidesColumn column) throws TransformationException {
				
		
		LOG.trace("Transforming {}", column);
//		if (column.getNameComponents().size() % 2 != 1) throw new TransformationException("Malformed column name "+column.getNameComponents());
		
		// process parent nodes..
		
		Node.Builder node = new NodeImpl.Builder().setName(column.getNameComponents().get(column.getNameComponents().size()-1)); 
		Node.Builder parentNode = rootNode;

		try {
			
			if (column.getNameComponents().size() >= 3) {
			
				for (List<AbstractType> childNodeData:Lists.partition(column.getNameComponents().subList(0, column.getNameComponents().size()-1), 2)) {
					LOG.trace("Looking at {}", childNodeData);
					Node.Builder nodeInNameComponent = new NodeImpl.Builder().setName(childNodeData.get(0)).setRepresentedType(ClassLoader.getSystemClassLoader().loadClass(((StringValue) childNodeData.get(1)).getValue()) );

					LOG.trace("Adding {} to {}", childNodeData.get(0), parentNode);
					parentNode = parentNode.addOrGetChild(nodeInNameComponent);
					
				}
				
				LOG.trace("--- Done adding nodes in name components");
			
			} else {
				
				LOG.trace("--- No nodes in name components");

				
			}
			
			/* Get indexed flag
			--------------------- */
			
			node.setIndexed(column.isIndexed());
			
			/* Get value
			------------- */
			
			switch(column.getValue().getHint()) {
			
				case INT32:
				case LONG:
				case FLOAT:
				case STRING:
				case BOOLEAN:
				case NULL:
					node.setValue(column.getValue());
				break;
				// TODO byte[]
			
			}
			
//			node.setRepresentedType(ClassLoader.getSystemClassLoader().loadClass(((StringValue) column.getNameComponents().get(column.getNameComponents().size()-1)).getValue()) );

		} catch (ClassNotFoundException e) {
			
			throw new TransformationException(e);
			
		}
		
		

		node.setCreated(column.getCreated());
		if (parentNode != null) parentNode.addOrGetChild(node);
		LOG.trace("Created {}", node);
		
		return node;
	}


}
