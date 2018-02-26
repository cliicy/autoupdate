package com.ca.arcflash.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.sun.xml.internal.ws.util.StringUtils;

/**
 * This class provides some methods to manipulate String.
 */
public final class StringUtil {
	private static String pattenStr = ".+@.+";
	private static Pattern emailPattern = Pattern.compile(pattenStr);
	/*
	 * This is a Util Class and don't need to create any instances.
	 */
	private StringUtil(){
		
	}
	
	/**
	 * Check whether the given String is Empty or Null
	 * @param target the target String to check
	 * @return a boolean value
	 */
	public static boolean isEmptyOrNull(String target){		
		if (target == null || target.equals("") || target.trim().equals(""))
			return true;
		return false;
	}
	
	public static boolean isJustEmptyOrNull(String target){		
		if (target == null || target.equals(""))
			return true;
		return false;
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
								"is" + StringUtils.capitalize(field.getName()),
								new Class[] {});
					else
						getMethod = object.getClass()
								.getMethod(
										"get"
												+ StringUtils.capitalize(field
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
			e.printStackTrace();
		}

		return buffer.toString();
	}
	
	public static String convertArray2String(String prefix ,Object[] array){
		if (array == null)
			return null;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(prefix).append("Total:").append(array.length).append("\n");
		for(Object obj : array)
			buffer.append(prefix).append(convertObject2String(obj)).append("\n");
		return buffer.toString();
	}
	
	public static String convertArray2String(Object[] array){
		return convertArray2String("", array);
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
	
	@SuppressWarnings("unchecked")
	public static String convertList2String(List list){
		return convertList2String("",list);
	}
	
	public static int string2Int(String source, int defaultValue){
		if (isEmptyOrNull(source))
			return defaultValue;
		
		try{		
			int result = Integer.parseInt(source);
			return result;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return defaultValue;
	}
	
	public static long string2Long(String source, long defaultValue){
		if (isEmptyOrNull(source))
			return defaultValue;
		
		try{		
			long result = Long.parseLong(source);
			return result;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return defaultValue;
	}
	
	public static double string2Double(String source, double defaultValue){
		if (isEmptyOrNull(source))
			return defaultValue;
		
		try{		
			double result = Double.parseDouble(source);
			return result;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return defaultValue;
	}
	
	public static boolean string2Boolean(String source, boolean defaultValue){
		if (StringUtil.isEmptyOrNull(source))
			return defaultValue;
		else if (source.toLowerCase().equals("true") || source.toLowerCase().equals("false"))
			return Boolean.parseBoolean(source);
		
		int value = string2Int(source,0);
		return value!=0;
	}
	
	public static Date string2Date(String source, SimpleDateFormat format, Date defaultValue){
		try{
			return format.parse(source);
		}catch(Exception e){
			return defaultValue;
		}
	}
	
	public static String date2String(Date date){
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", 
						DataFormatUtil.getDateFormatLocale());
			return dateFormat.format(date);
		}catch(Exception e){
			return "";
		}
	}
	
	
	
	public static boolean isExistingPath(String path){
		if (isEmptyOrNull(path)){
			return false;
		}
		
		File f = new File(path);
		if (!f.exists()) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isExistingFolder(String path){
		if (isEmptyOrNull(path)){
			return false;
		}
		
		File f = new File(path);
		if (!f.exists() || !f.isDirectory()) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isValidEmailAddress(String email){
		if (isEmptyOrNull(email)){
			return false;
		}
		
		if (!emailPattern.matcher(email).matches()) {
			return false;
		}
		
		return true;
	}
	
	public static void copy(String inFile, String outFile) throws IOException {
		copy(new File(inFile), new File(outFile));
	}	

	public static void copy(File inFile, File outFile) throws IOException {
		BufferedInputStream in = new BufferedInputStream(
				new FileInputStream(inFile));
		
		if(!outFile.exists()){
			outFile.createNewFile();
		}
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(outFile));
		// Copy the input stream to the output stream
		byte buffer[] = new byte[2048];
		int len = buffer.length;
		try {
			while (true) {
				len = in.read(buffer);
				if (len == -1) {
					break;
				}
				out.write(buffer, 0, len);
			}
			in.close();
			out.close();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Format message with current server locale.
	 * @param format
	 * @param args
	 * @return
	 */
	@SuppressWarnings("resource")
	public static String format(String format, Object ... args){
		return new Formatter(DataFormatUtil.getServerLocale()).format(format, args).toString();
	}
	
	/**
	 * Format message with en locale used to format string used in D2D internally
	 * @param format
	 * @param args
	 * @return
	 */
	@SuppressWarnings("resource")
	public static String enFormat(String format, Object ... args){
		return new Formatter(new Locale("en")).format(format, args).toString();
	}
	
	public static boolean equals(String str1, String str2){
		if(str1 == str2)
			return true;
		if(!isEmptyOrNull(str1) && str1.equals(str2))
			return true;
		if(!isEmptyOrNull(str2) && str2.equals(str1))
			return true;
		return false;
	}
	
	public static String getD2DUUIDFromFullDestination(String destination) {
		String d2dUUID;
		String tempDestination;
		
		if (destination.endsWith("\\"))
			tempDestination = destination.substring(0, destination.length() - 1);
		else
			tempDestination = destination;
		
		if (tempDestination.endsWith("]")) {
			int leftPosition = destination.lastIndexOf("[");
			int rightPosition = destination.lastIndexOf("]");
			d2dUUID = destination.substring(leftPosition + 1, rightPosition);
		} else {
			d2dUUID = "";
		}
		
		return d2dUUID;
	}
}
