package com.ca.arcserve.edge.app.base.webservice.contract.common;

/**
 * This class provides some methods to manipulate String.
 * most come from D2D flash StringUtil
 */
public final class StringUtil {
//	private static String pattenStr = ".+@.+";
//	private static Pattern emailPattern = Pattern.compile(pattenStr);
//	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
	
	public static boolean isNotEmpty(String target){
		return !isEmptyOrNull(target);
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
}
