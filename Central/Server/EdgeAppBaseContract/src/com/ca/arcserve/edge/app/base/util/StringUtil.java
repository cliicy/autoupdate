package com.ca.arcserve.edge.app.base.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.NotPrintAttribute;


/**
 * This class provides some methods to manipulate String.
 * most come from D2D flash StringUtil
 */
public final class StringUtil {
//	private static String pattenStr = ".+@.+";
//	private static Pattern emailPattern = Pattern.compile(pattenStr);
//	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Logger logger = Logger.getLogger(StringUtil.class);
	/**
	 * Check whether the given String is Empty or Null
	 * @param target the target String to check
	 * @return a boolean value
	 */
	public static boolean isEmptyOrNull(String target){
		if (target == null || target.equals("Null") || target.equals("") || target.trim().equals(""))//|| target.equals("Null") added by cliicy.luo 
			return true;
		return false;
	}
	/**
	 *
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String convertObject2String(Object object,IdentityHashMap<Object,Boolean> exists) {
		if (object == null)
			return null;
		if(exists.containsKey(object)) return object.toString();
		if (object.getClass().isArray()) {
			boolean ispri = object.getClass().getComponentType().isPrimitive();
			if(ispri){
				exists.put(object, true);
				StringBuffer buffer = new StringBuffer();
				boolean first = true;
				buffer.append("{");
				int len = Array.getLength(object);
				for (int i = 0;i<len;i++) {
					if (!first)
						buffer.append(",");
					first = false;
					buffer.append(Array.get(object, i).toString());
				}
				buffer.append("}");
				return buffer.toString();

			}
			else return convertArray2String((Object[]) object,exists);
		}else if (object instanceof List) {
			return convertArray2String(((List) object).toArray(),exists);
		}
		exists.put(object, true);
		// Filter out basic type. so that convertArray2String/convertList2String
		// can concat basic type array.
		if (object instanceof String || object instanceof StringBuffer
				|| object instanceof Long || object instanceof Integer
				|| object instanceof Boolean || object instanceof Short
				|| object instanceof Byte) {
			return object.toString();
		}
		if(object instanceof Class){
			return ((Class)object).getName();
		}


		StringBuffer buffer = new StringBuffer();
		buffer.append(object.toString()).append(" [");
		try {
			Field[] fields = object.getClass().getDeclaredFields();
			boolean first = true;
			for (Field field : fields) {
				if (field.getAnnotation(NotPrintAttribute.class) != null
						|| field.getName().equals("this$0"))
					continue;

				Object value = null;
				try {
					Method getMethod;
					Class<?> fieldClass = field.getType();
					if (fieldClass.getName().equals("boolean"))
						getMethod = object.getClass().getMethod(
								"is" + capitalize(field.getName()),
								new Class[] {});
					else
						getMethod = object.getClass()
								.getMethod(
										"get"
												+ capitalize(field
														.getName()),
										new Class[] {});
					value = getMethod.invoke(object,
							new Object[] {});

				} catch (NoSuchMethodException ex) {
					continue;
				}
				if (!first)
					buffer.append(",");
				first = false;
				buffer.append(field.getName()).append("=");
				buffer.append(convertObject2String(value,exists));
			}
			buffer.append(" ]");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return buffer.toString();
	}

	public static String convertArray2String(Object[] array,IdentityHashMap<Object,Boolean> exists) {
		if(array==null) return null;
		if(exists.containsKey(array)) return array.toString();
		exists.put(array, true);
		StringBuffer buffer = new StringBuffer();
		boolean first = true;
		buffer.append("{");
		for (Object obj : array) {
			if (!first)
				buffer.append(",");
			first = false;
			buffer.append(convertObject2String(obj,exists));
		}
		buffer.append("}");
		return buffer.toString();
	}

	public static boolean isEqual(String source, String dest){
		if(source==null){
			if(dest ==null) return true;
			else return false;
		}else
			return source.equals(dest);

	}
    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
    
    public static String convertList2String(List list){
		return convertList2String("",list);
	}
    
    @SuppressWarnings("unchecked")
	public static String convertList2String(String prefix, List list){
		if (list == null)
			return null;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(prefix).append("Total:").append(list.size()).append("\n");
		for(Object obj : list)
			buffer.append(prefix).append(convertObject2String(obj)).append("\n");
		return buffer.toString();
	}
    
	/**
	 * 
	 * @param object
	 * @return
	 */
	public static String convertObject2String(Object object){
		if (object == null)
			return null;

		// Filter out basic type. so that convertArray2String/convertList2String
		// can concat basic type array.
		if (object instanceof String || object instanceof StringBuffer
				|| object instanceof Long || object instanceof Integer
				|| object instanceof Boolean || object instanceof Short
				|| object instanceof Byte) {
			return object.toString();
		}

		StringBuffer buffer = new StringBuffer();

		try {
			Field[] fields = object.getClass().getDeclaredFields();

			for (Field field : fields) {
				if (field.getAnnotation(NotPrintAttribute.class) != null
						|| field.getName().equals("this$0"))
					continue;
					
				buffer.append("[").append(field.getName()).append(":");

				try {
					Method getMethod;
					Class<?> fieldClass = field.getType();
					if (fieldClass.getName().equals("boolean"))
						getMethod = object.getClass().getMethod(
								"is" + capitalize(field.getName()),
								new Class[] {});
					else
						getMethod = object.getClass()
								.getMethod(
										"get"
												+ capitalize(field
														.getName()),
										new Class[] {});

					buffer.append(getMethod.invoke(object, new Object[] {}))
							.append("]");
				} catch (NoSuchMethodException ex) {
					buffer.append("No such Method:" + ex.getMessage()).append(
							"]");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return buffer.toString();
	}
	
}
