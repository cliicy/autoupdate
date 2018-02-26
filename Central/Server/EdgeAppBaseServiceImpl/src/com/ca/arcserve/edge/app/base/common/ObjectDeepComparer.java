package com.ca.arcserve.edge.app.base.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class ObjectDeepComparer {
	
	private static Logger logger = Logger.getLogger(ObjectDeepComparer.class);
	
	public static <T> boolean isDeepEquals(T one, T another, Class<?>... excludeAnnotationTypes) {
		Set<Class<?>> excludeAnnotationSet = new HashSet<Class<?>>();
		
		if (excludeAnnotationTypes != null) {
			for (Class<?> type : excludeAnnotationTypes) {
				if (type.isAnnotation()) {
					excludeAnnotationSet.add(type);
				}
			}
		}
		
		String namespace = "";
		if (one != null) {
			namespace = one.getClass().getSimpleName();
		} else if (another != null) {
			namespace = another.getClass().getSimpleName();
		}
		
		return isDeepEqualsInternal(one, another, namespace, excludeAnnotationSet);
	}
	
	private static boolean isDeepEqualsInternal(Object one, Object another, String namespace, Set<Class<?>> excludeAnnotationTypes) {
		if (one == null || "".equals(one)) {
			if (another != null && !"".equals(another)) {
				logger.debug("ObjectDeepComparer - NOT EQUAL. namespace = " + namespace + ", one is null and another is not null.");
			}
			
			return another == null || "".equals(another);
		} else if (another == null || "".equals(another)) {
			logger.debug("ObjectDeepComparer - NOT EQUAL. namespace = " + namespace + ", one is not null and another is null.");
			return false;
		}
		
		if (isBasicObjectType(one.getClass())) {
			boolean equals = one.equals(another);
			
			if (!equals) {
				logger.debug("ObjectDeepComparer - NOT EQUAL. namespace = " + namespace + ", compared Object type = " + one.getClass().getSimpleName() + ", one = " + one + ", another = " + another);
			}
			
			return equals;
		}
		
		if (one.getClass().isArray()) {
			return isArrayEquals(one, another, namespace, excludeAnnotationTypes);
		}
		
		if (List.class.isAssignableFrom(one.getClass())) {
			return isListEquals(one, another, namespace, excludeAnnotationTypes);
		}
		
		if (one.getClass().getName().indexOf("com.ca.") == -1) {
			logger.error("ObjectDeepComparer - NOT EQUAL. namespace = " + namespace + ", unhandled Object type = " + one.getClass().getName());
			return false;
		}
		
		List<Field> allDeclaredFields = getDeclaredFields(one.getClass());
		for (Field f : allDeclaredFields) {
			String fieldNamespace = namespace + "." + f.getName();
			
			if (isExclude(f, excludeAnnotationTypes)) {
				logger.debug("ObjectDeepComparer - ignore the excluded field [" + f.getName() + "], namespace = " + fieldNamespace);
				continue;
			}
			
			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			
			boolean accessibleChanged = false;
			
			if (!f.isAccessible()) {
				f.setAccessible(true);
				accessibleChanged = true;
			}
			
			try {
				Object fieldObjectOne = f.get(one);
				Object fieldObjectAnother = f.get(another);
				
				if (!isDeepEqualsInternal(fieldObjectOne, fieldObjectAnother, fieldNamespace, excludeAnnotationTypes)) {
					return false;
				}
			} catch (Exception e) {
				logger.error("ObjectDeepComparer - get field value failed, namespace = " + fieldNamespace + ", error message = " + e.getMessage());
			}
			
			if (accessibleChanged) {
				f.setAccessible(false);
			}
		}
		
		return true;
	}
	
	private static boolean isBasicObjectType(Class<?> targetType) {
		return targetType.isPrimitive() 
				|| targetType.equals(String.class) 
				|| Number.class.isAssignableFrom(targetType) 
				|| targetType.equals(Boolean.class)
				|| targetType.isEnum();
	}
	
	private static boolean isArrayEquals(Object one, Object another, String namespace, Set<Class<?>> excludeAnnotationTypes) {
		if (Array.getLength(one) != Array.getLength(another)) {
			logger.debug("ObjectDeepComparer - compared array length is not the same, namespace = " + namespace);
			return false;
		}
		
		for (int i = 0; i < Array.getLength(one); ++i) {
			String elementNamespace = namespace + "[" + i + "]";
			if (!isDeepEqualsInternal(Array.get(one, i), Array.get(another, i), elementNamespace, excludeAnnotationTypes)) {
				return false;
			}
		}
		
		return true;
	}
	
	private static boolean isListEquals(Object one, Object another, String namespace, Set<Class<?>> excludeAnnotationTypes) {
		List<?> listOne = (List<?>) one;
		List<?> listAnother = (List<?>) another;
		
		if (listOne.size() != listAnother.size()) {
			logger.debug("ObjectDeepComparer - compared list size is not the same, namespace = " + namespace);
			return false;
		}
		
		for (int i = 0; i < listOne.size(); ++i) {
			String elementNamespace = namespace + "(" + i + ")";
			if (!isDeepEqualsInternal(listOne.get(i), listAnother.get(i), elementNamespace, excludeAnnotationTypes)) {
				return false;
			}
		}
		
		return true;
	}
	
	private static List<Field> getDeclaredFields(Class<?> targetType) {
		List<Field> fields = new ArrayList<Field>();
		
		for (Class<?> type = targetType; type != null; type = type.getSuperclass()) {
			fields.addAll(Arrays.asList(type.getDeclaredFields()));
		}
		
		return fields;
	}
	
	private static boolean isExclude(Field f, Set<Class<?>> excludeAnnotationTypes) {
		for (Annotation annotation : f.getDeclaredAnnotations()) {
			if (excludeAnnotationTypes.contains(annotation.annotationType())) {
				return true;
			}
		}
		
		return false;
	}

}
