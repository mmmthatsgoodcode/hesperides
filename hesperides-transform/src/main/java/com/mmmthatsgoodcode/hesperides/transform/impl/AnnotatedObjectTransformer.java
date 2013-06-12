package com.mmmthatsgoodcode.hesperides.transform.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.mmmthatsgoodcode.hesperides.annotation.HBean;
import com.mmmthatsgoodcode.hesperides.annotation.HBeanGetter;
import com.mmmthatsgoodcode.hesperides.annotation.HBeanSetter;
import com.mmmthatsgoodcode.hesperides.annotation.HConstructor;
import com.mmmthatsgoodcode.hesperides.annotation.HConstructorField;
import com.mmmthatsgoodcode.hesperides.annotation.Id;
import com.mmmthatsgoodcode.hesperides.annotation.Ignore;
import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.Node;
import com.mmmthatsgoodcode.hesperides.core.NodeImpl;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.core.Transformer;
import com.mmmthatsgoodcode.hesperides.transform.TransformerRegistry;
import com.mmmthatsgoodcode.hesperides.transform.model.HesperidesField;


/**
 * A transformer that may take the com.mmmthatsgoodcode.hesperides.annotation Annotations in to account when transforming Objects to Nodes
 * 
 * It uses a combination of these 4 strategies to a) extract as much state from your Objects as possible b) instantiate your Objects
 * 1) Types annotated with @HBean will be reflected on to invoke their getXXX setXXX methods to extract and restore a persisted Object's state
 * 2) When there is an @HConstructor annotated constructor, @HConstructorField(name=fieldName) annotations on its arguments will be used to create your object. Since this does not help with extracting object state, it may ( should ) be used in combination with @HBean to provide access to non-public or any field that is on the constructors argumen list. Otherwise, the object's public fields will be persisted only ( via reflection ) and any field on the argument list of the @HConstructor that was not public at the time of transformation will be null
 * 3) A no-arg constructor and getting/setting public fields via reflection
 * 4) Failing all the above, Objenesis to instantiate without a no-arg constructor and getting/setting public fields
 * 
 * @author andras
 *
 * @param <T> Type of the Object being transformed to a Node
 */
public class AnnotatedObjectTransformer<T> implements Transformer<T> {

	private static final Logger LOG = LoggerFactory.getLogger(AnnotatedObjectTransformer.class);
	
	public Node transform(T object) throws TransformationException {
						
		LOG.trace("Transforming object {} to Node", object==null?null:object.getClass());
		MethodAccess methodAccess = MethodAccess.get(object.getClass());
		FieldAccess fieldAccess = FieldAccess.get(object.getClass());
				
		Node node = new NodeImpl<String, T>();
		
		if (object == null) {
			node.setNullValue();
			return node;
		}
		
		node.setRepresentedType(object.getClass());

		List<Field> fields = getAllFields(object.getClass());

		// is this type marked @HBean ?
		if (object.getClass().getAnnotation(HBean.class) != null) {
			LOG.trace("Type is @HBean annotated");
			// yes! get fields and invoke getters
			
			// first, invoke explicitly ( @HBeanGetter ) specified getters
			for (Method method:getAllMethods(object.getClass())) {
				
				String field = null;
				try {
					
					HBeanGetter getterAnnotation = method.getAnnotation(HBeanGetter.class);
					if (getterAnnotation != null) {
						
						method.setAccessible(true);
						
						field = getterAnnotation.field();
						HesperidesField hField = null;
						Node childNode = null;
						try {
							hField = new HesperidesField( object.getClass().getDeclaredField(field) );
							fields.remove( hField.toField() ); // remove this field from fields
							if (hField.isIgnored()) continue; // looks like the matching field is @Ignored
							
							if (hField.isNodeId()) {
								LOG.trace("Field {} is an @Id field", hField.toField().getName());
								int idFieldTypeHint = Hesperides.Hints.typeToHint(hField.toField().getType());
								if (idFieldTypeHint == Hesperides.Hints.STRING) node.setName(idFieldTypeHint, method.invoke(object, (Object[])null));
								else throw new TransformationException("Id field can only be String"); // TODO add a constraint to the annotation ?
							}
							
							Object fieldValue =  methodAccess.invoke(object, method.getName());
							childNode = TransformerRegistry.getInstance().get(hField.toField()).transform(fieldValue);
							childNode.setTtl(hField.getTtl());
							
						} catch (NoSuchFieldException e) {
							LOG.debug("Field {} does not exist on {}", field, object.getClass().getSimpleName());
						}
						
						if (hField == null) {
							Object fieldValue =  methodAccess.invoke(object, method.getName());
							childNode = TransformerRegistry.getInstance().get(fieldValue.getClass()).transform(fieldValue);
						}
						
						childNode.setName(Hesperides.Hints.STRING, field);
						node.addChild(childNode);
						
					}
					
				} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new TransformationException("Could not invoke annotated getter "+method, e);
					
				}
				
			}
			
			// see if there are getFieldName methods for the remaining fields
			for (Iterator<Field> iterator = fields.iterator(); iterator.hasNext(); ) {
				Field field = iterator.next();
				
				if (field.getAnnotation(Ignore.class) == null) {
					
					try {
						Method getter = object.getClass().getMethod("get"+StringUtils.capitalize(field.getName()), (Class<?>[])null);
						iterator.remove();
						
						Node childNode = TransformerRegistry.getInstance().get(field).transform( getter.invoke(object, (Object[])null) );
						childNode.setName(Hesperides.Hints.STRING, field.getName());
						node.addChild(childNode);
						
					} catch (NoSuchMethodException e) {
						// nope
						
					} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						// could not invoke getter
						throw new TransformationException("Could not invoke getter", e);
					}
				}
				
			}
			
			if (fields.size() > 0) LOG.debug("{} fields were not accessible via getters", fields.size());
			
		} 
		
		// TODO parameter to HBean that sets if we should continue here
		// reflect on fields we still have to reflect on
	
		for (Field field:fields) {
			HesperidesField hField = new HesperidesField(field);
			
			try {
				LOG.trace("Looking at field {} with value {}", field.getName(), field.get(object));
				field.setAccessible(true);

				// see if this is an @Ignore 'd field
				if (!hField.isIgnored()) {
					
					// this is something we'll have to work with
					Node childNode;
					
					// see if this is an @Id field
					if (hField.isNodeId()) {
						LOG.trace("Field {} is an @Id field", field.getName());
						int idFieldTypeHint = Hesperides.Hints.typeToHint(field.getType());
						if (idFieldTypeHint == Hesperides.Hints.STRING) node.setName(idFieldTypeHint, field.get(object));
						else throw new TransformationException("Id field can only be String"); // TODO add a constraint to the annotation ?
					}

					childNode = TransformerRegistry.getInstance().get(field).transform(field.get(object));				
					childNode.setName(Hesperides.Hints.STRING, field.getName());
					node.addChild(childNode);
				
				} else {
					LOG.trace("Field {} is an @Ignored field, skipping.", field.getName());
				}

			
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// could not access field
				LOG.debug("Caught exception while reflecting on field {} : {}", field, e);
			}
		}
		
		
		
		return node;
	}

	
	public T transform(Node<? extends Object, T> node) throws TransformationException {
		
		T instance = null;
			try {
				if (node.getValueHint() == Hesperides.Hints.NULL) return null;
				
				Class type = node.getRepresentedType();
				List<Node> totalChildren = new ArrayList<Node>(node.getChildren()); // keep a copy of this list we can alter throughout this process without hurting the passed in node

				LOG.trace("Trasforming Node to an instance of {}", type);
				
				if (type.isPrimitive()) type = ClassUtils.primitiveToWrapper(type); // convert a primitive to its Class


				/* Create instance of represented type
				--------------------------------------- */
				
				// see if there is a constructor marked with @HConstructor
				
				for (Constructor constructor:type.getConstructors()) {
					
					if (constructor.getAnnotation(HConstructor.class) != null) {
						// collect parameter types and values in a type=>value map
						List<Map<Class, Object>> values = new ArrayList<Map<Class, Object>>();
						
						for (Class parameterType:constructor.getParameterTypes()) {
							HashMap<Class, Object> value = new HashMap<Class, Object>(); value.put(parameterType, null);
							values.add(value);
						}
						
						// it is guaranteed that constructor.getParameterAnnotations().length == constructor.getParameterTypes().length
						Iterator<Map<Class, Object>> types = values.iterator();
						for (Annotation[] annotationsOnField:constructor.getParameterAnnotations()) {
							Map<Class, Object> value = types.next(); 
							
							for (Annotation annotation:annotationsOnField) {
								if (annotation.annotationType().equals(HConstructorField.class)) {
									LOG.debug("Found {} on @HConstructor annotated constructors' parameter list", annotation);
									HConstructorField fieldAnnotation = (HConstructorField) annotation;
									Class parameterType = ((Class[])((Set<Class>)value.keySet()).toArray(new Class[]{}))[0];
									// there should be a child node with a string id of value.
									Node fieldNode = node.getChild(fieldAnnotation.field());
									if (fieldNode != null) {
										
										if (ClassUtils.isAssignable(fieldNode.getRepresentedType(), parameterType, true)) {
											
											HesperidesField hField = null;
											try {
												hField = new HesperidesField(type.getDeclaredField((String) fieldNode.getName()));
												value.put(parameterType, TransformerRegistry.getInstance().get(hField.toField()).transform(fieldNode));
											} catch (NoSuchFieldException e) {
												LOG.debug("Field {} is private or does not exist on {}", fieldNode.getName(), type.getSimpleName());
											}

											// update value
											if (hField == null) value.put(parameterType, TransformerRegistry.getInstance().get(fieldNode.getRepresentedType()).transform(fieldNode));
											
											totalChildren.remove(fieldNode); // remove this child node from the list of node fields we still need to process
											
										} else {
											throw new TransformationException(annotation+" on constructor "+constructor+" was expecting a field of type "+parameterType+" but matching field node represents incompatible type "+fieldNode.getRepresentedType());
										}
										
									} else {
										// value will stay null, and the field node will stay in totalChildren
										LOG.debug("Constructor {} is expecting field {} but no such node was found", constructor, fieldAnnotation.field());
									}

									
								}
								
							}
							
						}
						
						
						// we should now have a list of values we can invoke the constructor with
						LOG.debug("Collected {} @HConstructor arguments {}", values.size(), values);
						
						// extract actual values
						List<Object> parameters = new ArrayList<Object>();
						for (Map<Class, Object> value:values) {
							parameters.add(value.values().toArray()[0]);
						}
						
						LOG.debug("Invoking @HConstructor with {} parameters", parameters);
						try {
							instance = (T) constructor.newInstance(parameters.toArray());
							LOG.debug("Instantiated {} with @HConstructor {}!", type, constructor);
							break;
						} catch (InstantiationException | IllegalArgumentException | InvocationTargetException e) {
							throw new TransformationException("Failed to invoke @HConstructor "+constructor, e);
							
						}
						
						
						
						
					}
					
				}
				
				if (instance == null) {
				
					// fall back to using the no-arg constructor
					LOG.debug("Attempting to find no-arg constructor on {}", type);
					try {
						ConstructorAccess<T> constructor = ConstructorAccess.get(type);
						instance = constructor.newInstance();
					} catch(RuntimeException e) {
						LOG.warn("No no-arg constructor on {}, trying to create instance with Objenesis", type);
						// ReflectASM failed to instantiate, lets try with skipping the constructor
						Objenesis objenesis = new ObjenesisStd();
						ObjectInstantiator instantiator = objenesis.getInstantiatorOf(type);
						instance = (T) instantiator.newInstance();
					}
					
				}
					
				/* Start restoring Fields
				------------------------- */
				
				// see if the type is @HBean annotated
				
				if (type.getAnnotation(HBean.class) != null) {
					
					// yes! use setters
					
					// invoke explicitly ( @HBeanSetter ) marked setters first
					for (Method method:getAllMethods(type)) {

						String field = null;
						try {
							
							HBeanSetter setterAnnotation = method.getAnnotation(HBeanSetter.class);
							if (setterAnnotation != null) {
								
								method.setAccessible(true);
								
								field = setterAnnotation.field();
								
								// see if there is a similarly named child node on this Node
								Node fieldNode = node.getChild(field);
								if (fieldNode != null) {
									// there is
									
									// get the field matching the attribute on the setter ( there might be none )
									HesperidesField hField = null;
									try {
										hField = new HesperidesField( type.getField(field) );
										method.invoke(instance, TransformerRegistry.getInstance().get(hField.toField()).transform(fieldNode));
									} catch (NoSuchFieldException e) {
										LOG.debug("Field {} is private or does not exist on {}", field, type.getSimpleName());
									}
									
									LOG.debug("Invoking {} with {}", method, fieldNode);
									if (hField == null) method.invoke(instance, TransformerRegistry.getInstance().get(fieldNode.getRepresentedType()).transform(fieldNode));
											
									totalChildren.remove(fieldNode); // mark child as processed

								} else {
									// nope.. not much we can do
									LOG.debug("Found setter for field {} but no matching (String id'd) child is available on this Node!", field);
								}

							}
							
						} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new TransformationException("Could not invoke annotated setter "+method+" possibly type mismatch with field", e);
							
						}
						
					}					
					
					LOG.trace("Looking for set(FieldName) methods for {} remaining fields..", totalChildren.size());
					
					// try to find setters for the remaining fields
					for(Iterator<Node> iterator = totalChildren.iterator(); iterator.hasNext(); ) {
						Node fieldNode = iterator.next();
						String fieldName = (String) fieldNode.getName();
						String setterName = "set"+StringUtils.capitalize(fieldName);
						LOG.debug("Trying to find setter {}, with {} parameter",setterName, fieldNode.getRepresentedType());

						try {
							Method setter = type.getMethod(setterName, fieldNode.getRepresentedType());
							
							// get the field matching the attribute on the setter ( there might be none )
							HesperidesField hField = null;
							try {
								hField = new HesperidesField( type.getField((String) fieldNode.getName()) );
								setter.invoke(instance, TransformerRegistry.getInstance().get(hField.toField()).transform(fieldNode));
							} catch (NoSuchFieldException e) {
								LOG.debug("Field {} is private or does not exist on {}", fieldName, type.getSimpleName());
							}
							
							if (hField==null) setter.invoke(instance, TransformerRegistry.getInstance().get(fieldNode.getRepresentedType()).transform(fieldNode));
								
							LOG.debug("Used setter {} to set field {}", setter, fieldNode.getName());
							iterator.remove();
						} catch (NoSuchMethodException e) {
							// nope, no setter
							LOG.debug("No setter {}, with {} parameter",setterName, fieldNode.getRepresentedType());
						} catch (SecurityException | IllegalArgumentException | InvocationTargetException e) {
							throw new TransformationException("Could not invoke setter", e);
						}
						
					}
					
					if (totalChildren.size() > 0) LOG.debug("{} fields were not accessible via setters", totalChildren.size());
					
				}
				
				// use reflection to set remaining fields
				LOG.debug("Trying to set {} fields through reflection", totalChildren.size());	
				
				for(Node fieldNode:totalChildren) {
					Class fieldNodeType = fieldNode.getRepresentedType();
					LOG.trace("Trying to set {}", fieldNode.getName());

					try {
						Field field = type.getField((String) fieldNode.getName());

						field.setAccessible(true);
						
						field.set(instance, TransformerRegistry.getInstance().get(field).transform(fieldNode));
						
					} catch (SecurityException e) {
						LOG.debug("Caught SecurityException while trying to set field {} accessible on {}", fieldNode.getName(), type);
					} catch (NoSuchFieldException e) {
						LOG.debug("Field {} inaccessible on {}", fieldNode.getName(), type);
					}
					
				}
				
			} catch ( IllegalAccessException e ) {
				throw new TransformationException(e);
			}
		
		
		return instance;
		
	}
	
	private List<Field> getAllFields(Class clazz) {
		
		List<Field> fields = new ArrayList<Field>();
		
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		
		Class superClass = clazz.getSuperclass();
		if (superClass != null) fields.addAll(getAllFields(superClass));
		
		return fields;
		
	}
	
	private List<Method> getAllMethods(Class clazz) {
		
		List<Method> methods = new ArrayList<Method>();
		
		methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
		
		Class superClass = clazz.getSuperclass();
		if (superClass != null) methods.addAll(getAllMethods(superClass));
		
		return methods;
		
	}

}
