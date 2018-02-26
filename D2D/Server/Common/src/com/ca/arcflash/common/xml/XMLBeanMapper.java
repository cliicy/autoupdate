package com.ca.arcflash.common.xml;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.DayTime;
import com.sun.xml.internal.ws.util.StringUtils;

public class XMLBeanMapper<T> {
	private Class<T> tClass;
//	private HashMap<String, Method> setMethodMap = new HashMap<String, Method>();
//	private HashMap<String, Method> getMethodMap = new HashMap<String, Method>();
	private HashMap<String, Field> fieldMap = new HashMap<String, Field>();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", 
			DataFormatUtil.getDateFormatLocale());

	public XMLBeanMapper(Class<T> tClass) throws Exception{
		this.tClass = tClass;
		loadGetSetMethods();
	}

	@SuppressWarnings("unchecked")
	private void loadGetSetMethods() throws NoSuchMethodException {
		Class tempClass = this.tClass;
		do{
			Field[] fields = tempClass.getDeclaredFields();
//			prepareGetMethods(fields);
//			prepareSetMethods(fields);
			prepareField(fields);
			tempClass = tempClass.getSuperclass();
		} while (!tempClass.getName().equals("java.lang.Object"));
	}

	private void prepareField(Field[] fields) throws NoSuchMethodException {
		for(Field field : fields){
			if (field.getAnnotation(XMLAttribute.class)!=null){
				XMLAttribute annotaton = field.getAnnotation(XMLAttribute.class);
				fieldMap.put(annotaton.keyName(), field);
			}
		}
	}
	
//	private void prepareGetMethods(Field[] fields) throws NoSuchMethodException {
//		for(Field field : fields){
//			if (field.getAnnotation(XMLAttribute.class)!=null){
//				XMLAttribute annotaton = field.getAnnotation(XMLAttribute.class);
//				Class<?> fieldClass = field.getType();
//				Method getMethod;
//				if (fieldClass.getName().equals("boolean"))
//					getMethod = tClass.getMethod("is"+StringUtils.capitalize(field.getName()), new Class[]{} );
//				else
//					getMethod = tClass.getMethod("get"+StringUtils.capitalize(field.getName()), new Class[]{} );
//				getMethodMap.put(annotaton.keyName(), getMethod);
//			}
//		}
//	}

//	private void prepareSetMethods(Field[] fields) throws NoSuchMethodException {
//		for(Field field : fields){
//			if (field.getAnnotation(XMLAttribute.class)!=null){
//				XMLAttribute annotaton = field.getAnnotation(XMLAttribute.class);
//				Class<?> fieldClass = field.getType();
//				Method setMethod = tClass.getMethod("set"+StringUtils.capitalize(field.getName()), new Class[]{fieldClass} );
//				setMethodMap.put(annotaton.keyName(), setMethod);
//			}
//		}
//	}

	@SuppressWarnings("unchecked")
	public T loadBean(Node node) throws Exception{
		if (node == null)
			return null;

		T newInstance = (T) tClass.newInstance();
		NamedNodeMap attributes = node.getAttributes();

		Iterator<String> keys = fieldMap.keySet().iterator();
		while(keys.hasNext()){
			String attributeName = keys.next();
			Field method = fieldMap.get(attributeName);
			method.setAccessible(true);
			String typeName = method.getType().getName();

			Node attribute = attributes.getNamedItem(attributeName);
			if (attribute == null && !method.getType().isArray() && !typeName.equals("java.util.List"))
				continue;

			if (typeName.equals("java.lang.String"))
				method.set(newInstance, attribute.getTextContent());
			else if (typeName.equals("int")) {
				method.set(newInstance, StringUtil.string2Int(attribute.getTextContent(), 0));
			}else if (typeName.equals("long")) {
				method.set(newInstance, StringUtil.string2Long(attribute.getTextContent(), 0));
			}else if (typeName.equals("double")) {
				method.set(newInstance, StringUtil.string2Double(attribute.getTextContent(), 0));
			}else if (typeName.equals("boolean")) {
				method.set(newInstance, StringUtil.string2Boolean(attribute.getTextContent(),false));
			}else if (typeName.equals("java.util.Date")){
				method.set(newInstance, StringUtil.string2Date(attribute.getTextContent(), dateFormat, new Date()));
			}else if (method.getType().isArray()){
				List<String> list = new LinkedList<String>();
				NodeList childNodes = node.getChildNodes();
				for(int index=0;index<childNodes.getLength();index++){
					Node childNode = childNodes.item(index);
					if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals(attributeName)){
						list.add(childNode.getAttributes().getNamedItem("Value").getTextContent());
					}
				}

				Class componentType = method.getType().getComponentType();
				Object array = Array.newInstance(componentType, list.size());
				for(int index = 0;index<list.size();index++)
					Array.set(array, index, convertStr2Class(list.get(index), componentType));

				method.set(newInstance, array);
			}else if (typeName.equals("java.util.List") && this.isGenericElementTypeOfString(method)){
				List list = new ArrayList();
				NodeList childNodes = node.getChildNodes();
				for(int index=0;index<childNodes.getLength();index++){
					Node childNode = childNodes.item(index);
					if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals(attributeName)){
						list.add(childNode.getAttributes().getNamedItem("Value").getTextContent());
					}
				}

				method.set(newInstance, list);
			}else if(typeName.equals("com.ca.arcflash.webservice.data.DayTime")) {
				method.set(newInstance, DayTime.fromString(attribute.getTextContent()));
			}
		}

		return newInstance;
	}

	private Object convertStr2Class(String str, Class componentType) {
		String typeName=componentType.getName();
		if (typeName.equals("java.lang.String"))
			return str;
		else if (typeName.equals("int")) {
			return StringUtil.string2Int(str, 0);
		}else if (typeName.equals("long")) {
			return StringUtil.string2Long(str, 0);
		}else if (typeName.equals("double")) {
			return StringUtil.string2Double(str, 0);
		}else if (typeName.equals("boolean")) {
			return StringUtil.string2Boolean(str,false);
		}else if (typeName.equals("java.util.Date")){
			return StringUtil.string2Date(str, dateFormat, new Date());
		}else if(typeName.equals("java.lang.Boolean")){
			return StringUtil.string2Boolean(str,false);
		}
		return str;
	}

	public List<T> loadBeans(NodeList nodeList) throws Exception{
		List<T> result = new LinkedList<T>();

		for(int i=0;i<nodeList.getLength();i++){
			Node node = nodeList.item(i);
			T instance = loadBean(node);
			if (instance!=null)
				result.add(instance);
		}
		return result;
	}

	public Element saveBean(T t, Document doc, String tagName) throws Exception{
		Element element = doc.createElement(tagName);

		Set<String> keys = fieldMap.keySet();
		for(String key : keys){
			Field method = fieldMap.get(key);
			method.setAccessible(true);

			String typeName = method.getType().getName();

			Object value = method.get(t);
			if (value == null)
				continue;
			if (method.getType().isPrimitive())
				processSimpleType(t, element, key, method, typeName);
			else if (typeName.equals("java.util.Date")) {
				element.setAttribute(key, dateFormat.format(method.get(t)));
			}else if (typeName.equals("java.lang.String")){
				setStringValue(t, element, key, method);
			}else if (method.getType().isArray()){
				for (int index =0; index < Array.getLength(value);index++){
					Element arrayElement = doc.createElement(key);
					arrayElement.setAttribute("Value", Array.get(value, index).toString());
					element.appendChild(arrayElement);
				}
			} else if (typeName.equals("java.util.List") && isGenericElementTypeOfString(method)) {
				List<String> x = (List<String>) value;
				for (int index = 0; index < x.size(); index++) {
					Element arrayElement = doc.createElement(key);
					arrayElement.setAttribute("Value", x.get(index));
					element.appendChild(arrayElement);
				}
			}else if(typeName.equals("com.ca.arcflash.webservice.data.DayTime")) {
				element.setAttribute(key, value.toString());
			}
		}

		return element;
	}
	
//	private boolean isParaGenericElementTypeOfString(Field method) {
//		boolean isListString = false;
//		Type paraType = method.getGenericType();
//		if (paraType instanceof ParameterizedType) {
//		    ParameterizedType paramType = (ParameterizedType) paraType;
//		    Type[] argTypes = paramType.getActualTypeArguments();
//		    if (argTypes.length > 0) {
//		    	if(String.class.equals(argTypes[0])){
//		    		isListString = true;
//		    	}
//		    	
//		    }
//		}
//		return isListString;
//	}

	private boolean isGenericElementTypeOfString(Field method) {
		boolean isListString = false;
		Type retType = method.getGenericType();
		if (retType instanceof ParameterizedType) {
		    ParameterizedType paramType = (ParameterizedType) retType;
		    Type[] argTypes = paramType.getActualTypeArguments();
		    if (argTypes.length > 0) {
		    	if(String.class.equals(argTypes[0])){
		    		isListString = true;
		    	}
		    	
		    }
		}
		return isListString;
	}

	public Element saveBean(T t, Document doc) throws Exception{
		return saveBean(t,doc,tClass.getSimpleName());
	}

	private void processSimpleType(T t, Element element, String key,
			Field method, String typeName) throws Exception,
			IllegalAccessException, InvocationTargetException {
		if (typeName.equals("int")) {
			element.setAttribute(key, method.get(t).toString());
		}else if (typeName.equals("boolean")) {
			element.setAttribute(key, method.get(t).toString());
		}else if(typeName.endsWith("long")) {
			element.setAttribute(key, method.get(t).toString());
		}else if(typeName.endsWith("double")) {
			element.setAttribute(key, method.get(t).toString());
		}
	}

	private void setStringValue(T t, Element element, String key, Field method)
			throws Exception {
		Object value = method.get(t);
		if (value!=null)
		element.setAttribute(key, value.toString());
	}

	public List<Element> saveBeans(List<T> beans, Document doc) throws Exception{
		List<Element> result = new LinkedList<Element>();

		for(T t : beans)
			result.add(saveBean(t,doc));
		return result;
	}
}