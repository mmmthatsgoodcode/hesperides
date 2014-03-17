package com.mmmthatsgoodcode.hesperides.core;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface Node<N, T> extends Iterable<Node<?, ?>> {

	public interface Builder<N, T> {
		
		
		public Builder<N, T> setTtl(long ttl);
		public Builder<N, T> setCreated(Date created);
		public Builder<N, T> setIndexed(boolean indexed);
		public Builder<N, T> setName(AbstractType<N> value);
		public AbstractType<N> getName();
		
		public Builder<N, T> setValue(AbstractType<T> value);
		public Builder<N, T> addChild(Node.Builder<?, ?> child);
		public Builder addOrGetChild(Node.Builder<?, ?> child);
		public Builder<N, T> addChildren(Collection<Node.Builder<?, ?>> children);
		public Builder<N, T> setRepresentedType(Class<T> type);
		
		public Node<N, T> build(Node<?, ?> parent);
	}
	
	public interface Locator {
		
		public interface Transformer<T extends Object> {
			
			public T transform(Locator locator) throws TransformationException;
			public Locator transform(T locator) throws TransformationException;
			
		}
		
		public Locator p(Node<?, ?>...parents);
		public Locator p(AbstractType<?>...parentNames);
		
		public Locator n(Node<?, ?> node);
		public Locator n(AbstractType<?> nodeName);
		
		public List<Node<?, ?>> parents();
		public List<AbstractType<?>> parentNames();
		public Node<?, ?> node();
		
	}
	
	public interface Transformer<T> {
		
		public Node.Builder<?, ?> transform(T node) throws TransformationException;
		public T transform(Node<?, ?> node) throws TransformationException;
		
	}
	
	public long getTtl();
	
	public Date getCreated();
	
	public boolean isIndexed();
	
	public AbstractType<N> getName();
	
	public AbstractType<T> getValue();
	
	public Class<T> getRepresentedType();
	
	public Node<?, ?> getParent();
	public Set<AbstractType<?>> getUpstreamNodeNames();	
	
	public Set<Node<?, ?>> getChildren();
	public <C> Node<C, ?> getChild(AbstractType<C> name);
	
	public Node<?, ?> locate(Node.Locator locator);
	
}
