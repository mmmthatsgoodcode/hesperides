package com.mmmthatsgoodcode.hesperides.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.common.collect.Lists;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.ClassResolver;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.Node.Locator;
import com.mmmthatsgoodcode.hesperides.core.type.StringValue;
import com.mmmthatsgoodcode.hesperides.core.type.WildcardValue;
import com.mmmthatsgoodcode.hesperides.datastore.model.HesperidesColumnSlice;

public class HesperidesColumnSliceTransformer implements Node.Locator.Transformer<HesperidesColumnSlice> {

	private static class HesperidesColumnSliceTransformerHolder {
		public static final HesperidesColumnSliceTransformer INSTANCE = new HesperidesColumnSliceTransformer();
	}

	public static HesperidesColumnSliceTransformer getInstance() {
		return HesperidesColumnSliceTransformerHolder.INSTANCE;
	}
	
	@Override
	public HesperidesColumnSlice transform(Locator locator) throws TransformationException {
		
		HesperidesColumnSlice columnSlice = new HesperidesColumnSlice();
		
		for (Node node:locator.parents()) {
		
			columnSlice.n(extractNodeName(node));
			
		}
		
		if (locator.node() == null) columnSlice.n(new WildcardValue());
		else columnSlice.n((locator.node().getName()));
		
		return columnSlice;
	}

	@Override
	public Locator transform(HesperidesColumnSlice columnSlice) throws TransformationException {
		
		Locator locator = new NodeImpl.Locator();
		
		for(List<AbstractType> nameComponents:Lists.partition(columnSlice.components(), 2)) {
			
			Node.Builder nodeBuilder = new NodeImpl.Builder().setName(nameComponents.get(0));
			try {
				if (nameComponents.get(1) instanceof StringValue) nodeBuilder.setRepresentedType(ClassResolver.getInstance().resolve(((StringValue) nameComponents.get(1)).getValue() ));
				locator.p(nodeBuilder.build(null));
			} catch (ExecutionException e) {
				throw new TransformationException("Failed to load represented type "+nameComponents.get(1).getValue());
			}
			
		}
		
		return locator;
		
	}
		
	private List<AbstractType> extractNodeName(Node node) throws TransformationException {
		
		List<AbstractType> nameComponents = new ArrayList<AbstractType>();
		
		switch (node.getName().getHint()) {

		case STRING:
		case INT32:
		case LONG:
		case FLOAT:
			nameComponents.add(node.getName());
			break;
		default:
			throw new TransformationException("HesperidesColumnSliceTransformer does not support node name of type "
					+ node.getName().getClass().getSimpleName() + "(" + node.getName().getHint() + ")");

		}
		
		nameComponents.add(new StringValue(node.getRepresentedType().getName()));
		
		return nameComponents;
		
	}

}
