package com.mmmthatsgoodcode.hesperides.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ClassResolver {

	private static class ClassResolverHolder {
		
		private static final ClassResolver INSTANCE = new ClassResolver();
		
	}
	
	public static ClassResolver getInstance() {
		return ClassResolverHolder.INSTANCE;
	}
	
	
	private ClassResolver() {
		
	}
	
	Cache<String, Class> classCache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(1, TimeUnit.HOURS).build();
	
	public Class resolve(final String className) throws ExecutionException {
		
		return classCache.get(className, new Callable<Class>() {

			@Override
			public Class call() throws Exception {
				
				return ClassLoader.getSystemClassLoader().loadClass(className);
				
			}});
		
	}
}
