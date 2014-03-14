package com.mmmthatsgoodcode.hesperides.cassify;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumnSlice;
import com.mmmthatsgoodcode.hesperides.core.AbstractType;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.Node.Locator;
import com.mmmthatsgoodcode.hesperides.core.type.BooleanValue;
import com.mmmthatsgoodcode.hesperides.core.type.StringValue;
import com.mmmthatsgoodcode.hesperides.core.type.WildcardValue;

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
		else columnSlice.n(extractNodeName(locator.node()));
		
		return columnSlice;
	}

	@Override
	public Locator transform(HesperidesColumnSlice columnSlice) throws TransformationException {
		
		Locator locator = new NodeImpl.Locator();
		
		for(List<AbstractType> nameComponents:Lists.partition(columnSlice.components(), 3)) {
			
			Node.Builder nodeBuilder = new NodeImpl.Builder().setName(nameComponents.get(2));
			try {
				if (nameComponents.get(0) instanceof StringValue && ((StringValue) nameComponents.get(0)).getValue() == Hesperides.Hint.OBJECT.alias()) nodeBuilder.setRepresentedType(ClassLoader.getSystemClassLoader().loadClass( ((StringValue) nameComponents.get(1)).getValue() ));
				locator.p(nodeBuilder.build(null));
			} catch (ClassNotFoundException e) {
				throw new TransformationException("Failed to load represented type "+nameComponents.get(0).getValue());
			}
			
		}
		
		return locator;
		
	}
		
	private List<AbstractType> extractNodeName(Node node) throws TransformationException {
		
		List<AbstractType> nameComponents = new ArrayList<AbstractType>();
		
		nameComponents.add(new StringValue(node.getValue().getHint().alias()));
		if (node.getValue().getHint() == Hesperides.Hint.OBJECT) nameComponents.add(new StringValue(node.getRepresentedType().getName()));
		else nameComponents.add(new BooleanValue(false));
		
		switch (node.getName().getHint()) {

		case STRING:
		case INT:
		case LONG:
		case FLOAT:
			nameComponents.add(AbstractType.wrap(node.getName()));
			break;
		default:
			throw new TransformationException("HesperidesColumnSliceTransformer does not support node name of type "
					+ node.getName().getClass().getSimpleName() + "(" + node.getName().getHint() + ")");

		}
		
		return nameComponents;
		
	}

}
