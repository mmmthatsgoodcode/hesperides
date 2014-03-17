package com.mmmthatsgoodcode.hesperides.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.hash.Hashing;
import com.mmmthatsgoodcode.hesperides.core.type.NullValue;

public class NodeImpl<N, T> implements Node<N, T> {

	public static class Builder<N, T> implements Node.Builder<N, T> {

		private final NodeImpl<N, T> node = new NodeImpl();
		private final Set<Node.Builder<?, ?>> children = new HashSet<Node.Builder<?, ?>>();
		
		@Override
		public Builder<N, T> setName(AbstractType<N> name) {
			node.setName(name);
			return this;
		}
		
		@Override
		public AbstractType<N> getName() {
			return node.getName();
		}

		@Override
		public Builder<N, T> setTtl(long ttl) {
			node.setTtl(ttl);
			return this;
		}

		@Override
		public Builder<N, T> setCreated(Date created) {
			node.setCreated(created);
			return this;
		}

		@Override
		public Builder<N, T> setIndexed(boolean indexed) {
			node.setIndexed(indexed);
			return this;
		}

		@Override
		public Builder<N, T> setValue(AbstractType<T> value) {
			if (children.size() > 0) throw new IllegalStateException("A Node may have either a Value or child nodes. Not Both. This Node already has children.");
			node.setValue(value);
			return this;
		}
		
		@Override
		public Builder<N, T> addChild(Node.Builder<?, ?> child) {
			if (node.getValue().equals(new NullValue()) == false) throw new IllegalStateException("A Node may have either a Value or child nodes. Not Both. This Node already has value.");
			children.add(child);
			return this;
		}
		

		@Override
		public Node.Builder addOrGetChild(Node.Builder<?, ?> child) {
			
			for (Node.Builder c:children) {
				if (c.getName().equals(child.getName())) return c;
			}
			
			addChild(child);
			
			return child;
			
		}

		@Override
		public Builder<N, T> addChildren(Collection<Node.Builder<?, ?>> children) {
			children.addAll(children);
			return this;
		}

		@Override
		public Builder<N, T> setRepresentedType(Class<T> representedType) {
			node.setRepresentedType(representedType);
			return this;
			
		}

		@Override	
		public Node<N, T> build(Node<?, ?> parent) {

//			if (node.getName() == null) throw new IllegalStateException("Node name may not be null");
			
			if (parent == null) parent = node;
			else node.setParent(parent);
			
			for(Node.Builder<?, ?> child:children) {
				
				node.addChild(child.build(parent));
			}
			
			return node;
			
		}
		
		@Override
		public String toString() {
			return "Builder for: "+this.node;
		}

		
	}
	
	public static class Locator implements Node.Locator {

		private Node<?, ?> actualNode = null;
		private List<Node<?, ?>> parentNodes = new ArrayList<Node<?, ?>>();

		
		
		@Override
		public Locator p(Node<?, ?>...parents) {
			for (Node<?, ?> parent:Arrays.asList(parents)) {
				p(parent);
			}
			
			return this;
		}
		
		@Override
		public Locator p(AbstractType<?>...parentNames) {
			for (AbstractType<?> parentName:Arrays.asList(parentNames)) {
				p(new NodeImpl.Builder().setName(parentName).build(null));
			}
			
			return this;
		}
		
		private Locator p(Node<?, ?> parent) {
			if (actualNode != null) throw new IllegalStateException("The actual node is already defined, can not add parent");
			parentNodes.add(parent);
			return this;
		}
		
		
		
		@Override
		public Locator n(Node<?, ?> node) {
			actualNode = node;
			return this;
		}
		
		@Override
		public Locator n(AbstractType<?> nodeName) {
			actualNode = new NodeImpl.Builder().setName(nodeName).build(null);
			return this;
		}
		
		@Override
		public List<Node<?, ?>> parents() {
			return parentNodes;
		}
		
		@Override
		public List<AbstractType<?>> parentNames() {
			
			List<AbstractType<?>> parentNames = new ArrayList<AbstractType<?>>();
			for (Node parent:parentNodes) {
				parentNames.add(parent.getName());
			}
			
			return parentNames;
			
		}

		@Override
		public Node<?, ?> node() {
			return actualNode;
		}

		@Override
		public String toString() {
			return "Parents: "+parentNames()+", actualNode: "+node();
		}

		
	}
	
	private int hash;
	private Date created = new Date();
	private long ttl = 0;
	
	private boolean indexed = false;
	
	private Hesperides.Hint valueHint = Hesperides.Hint.OBJECT;
	private Hesperides.Hint nameHint = Hesperides.Hint.STRING;

	private Class<? extends Object> representedType = NodeImpl.class;

	private AbstractType<T> value = new NullValue();
	private AbstractType<N> name = new NullValue();
	
	private Node<?, ?> parent;
	private Set<Node<?, ?>> children = new HashSet<Node<?, ?>>();

	protected NodeImpl() {

	}
	
	@Override
	public boolean isIndexed() {
		return this.indexed;
	}
	
	private void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

	@Override
	public AbstractType<T> getValue() {
		return this.value;
	}
	
	private void setValue(AbstractType<T> value) {
		this.value = value;
	}

	@Override
	public Node getParent() {
		return parent;
	}
	
	private void setParent(Node<?, ?> parent) {
		this.parent = parent;
	}

	@Override
	public Iterator<Node<?, ?>> iterator() {
		return this.children.iterator();
	}

	@Override
	public AbstractType<N> getName() {
		return this.name;
	}
	
	private void setName(AbstractType<N> name) {
		this.name = name;
	}

	@Override
	public Class getRepresentedType() {
		return this.representedType;
	}
	
	private void setRepresentedType(Class representedType) {
		this.representedType = representedType;
	}
	
	@Override
	public boolean equals(Object object) {
		
		if (!Node.class.isAssignableFrom(object.getClass())) return false;
		NodeImpl other = (NodeImpl) object;
		
		
		// same represented type&name
		if (other.getRepresentedType().equals(this.getRepresentedType()) == false
		|| other.getName().equals(this.getName()) == false) return false;
		
		// same parents..
		if (getUpstreamNodeNames().equals(other.getUpstreamNodeNames()) == false) return false;
		
		// same children
		return getChildren().equals(other.getChildren());
		
	}

	@Override
	public int hashCode() {
		
		Long sum = new Long(getRepresentedType().hashCode());
		sum += (getName()==null?0:getName().hashCode());
		sum += (getValue().hashCode());
		
		for(Node child:getChildren()) {
			sum += child.hashCode();
		}
		
		return Hashing.murmur3_32().hashLong(sum).asInt();
		
	}

	@Override
	public long getTtl() {
		return this.ttl;
	}
	
	private void setTtl(long ttl) {
		this.ttl = ttl;
	}
	
	@Override
	public Date getCreated() {
		return this.created;
	}
	
	private void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public Set<Node<?, ?>> getChildren() {
		return this.children;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <C> Node<C, ?> getChild(AbstractType<C> name) {
		for (Node<?, ?> child:this.children) {
			if (child.getName().equals(name)) return (Node<C, ?>) child;
		}
		
		return null;
	}
	
	private void addChild(Node<?, ?> child) {
		for(Node c:children) {
			if (c.getName().equals(child.getName())) return;
		}
		
		this.children.add(child);
	}
	
	private void addChildren(Set<Node<?, ?>> children) {
		for(Node child:children) {
			addChild(child);
		}
	}
	
	@Override
	public Set<AbstractType<?>> getUpstreamNodeNames() {
		
		Set<AbstractType<?>> upstreamNodes = new HashSet<AbstractType<?>>();
		if (getParent() != null) {
		
			upstreamNodes.add(parent.getName());
			upstreamNodes.addAll(parent.getUpstreamNodeNames());
		
			return upstreamNodes;

		}
		
		return upstreamNodes;
		
	}
	
	@Override
	public String toString() {
		return "("+getRepresentedType()+")"+getName()+":"+getValue()+(getChildren().size()>0?(" - "+getChildren()):"");
	}


	@Override
	public Node<?, ?> locate(Node.Locator locator) {
				
		Node node = this;
		for(AbstractType<?> parentName:locator.parentNames()) {
			
			boolean match = false;
			// is there a child node under "node" with this name?
			for (Object o:node.getChildren()) {
				Node child = (Node) o; // eclipse bug..?
				
				if (child.getName().equals(parentName)) {
					node = child; match = true; break;
				}
				
			}
			
			// there wasnt..
			if (match == false) return null;
			
		}
		
		
		// found all parents
		if (locator.node() != null) {
						
			for (Object o:node.getChildren()) {
				Node child = (Node) o;
				
				if (child.getName().equals(locator.node().getName())) return child;
				
			}
			
			return null;
			
		}
		
		return node;
		
	}


}
