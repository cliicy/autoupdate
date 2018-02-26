package com.ca.arcflash.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.prefs.Preferences;

public class WindowsRegistry {
	
	private static final Preferences systemRoot = Preferences.systemRoot();
	
	public static final int HKEY_LOCAL_MACHINE = 0x80000002;
	public static final int KEY_ALL_ACCESS = 0xf003f;
	public static final int KEY_Read_ACCESS = 0x20019;
	public static final int KEY_Write_ACCESS = 0x20006;
	
	private static Method windowsRegOpenKey = null;
	private static Method windowsRegCloseKey = null;
	private static Method windowsRegQueryValueEx = null;
	private static Method windowsRegCreateKeyEx = null;
	private static Method windowsRegSetValueEx = null;
	private static Method windowsRegDeleteKey = null;
	private static Method windowsRegDeleteValue = null;

	
	static{
		try{
			Class<?> systemClass = systemRoot.getClass();
			
			windowsRegOpenKey = systemClass.getDeclaredMethod(
					"WindowsRegOpenKey", new Class[] { int.class, byte[].class,
							int.class });
			windowsRegOpenKey.setAccessible(true);
	
			windowsRegCloseKey = systemClass.getDeclaredMethod(
					"WindowsRegCloseKey", new Class[] { int.class });
			windowsRegCloseKey.setAccessible(true);
			
			windowsRegQueryValueEx = systemClass.getDeclaredMethod(
					"WindowsRegQueryValueEx", new Class[] { int.class,
							byte[].class });
			windowsRegQueryValueEx.setAccessible(true);
			
			windowsRegCreateKeyEx = systemClass.getDeclaredMethod(
					"WindowsRegCreateKeyEx", new Class[] { int.class,
							byte[].class });
			windowsRegCreateKeyEx.setAccessible(true);
			
			windowsRegSetValueEx = systemClass.getDeclaredMethod(
					"WindowsRegSetValueEx", new Class[] { int.class,
							byte[].class, byte[].class });
			windowsRegSetValueEx.setAccessible(true);
			
			windowsRegDeleteKey = systemClass.getDeclaredMethod(  
			          "WindowsRegDeleteKey", new Class[] { int.class,  
			              byte[].class });  
			windowsRegDeleteKey.setAccessible(true); 
			
			windowsRegDeleteValue = systemClass.getDeclaredMethod(  
		              "WindowsRegDeleteValue", new Class[] { int.class,  
		                  byte[].class });  
			windowsRegDeleteValue.setAccessible(true); 
		} catch (SecurityException e){
			e.printStackTrace();
		} catch (NoSuchMethodException e){
			e.printStackTrace();
		}
	}
	
	public int openKey(String key) throws Exception{
		int[] result= (int[]) windowsRegOpenKey.invoke(systemRoot, new Object[] {
				new Integer(HKEY_LOCAL_MACHINE), stringToByteArray(key), new Integer(KEY_Read_ACCESS | KEY_Write_ACCESS) });
		return result[0];
	}
	
	public int openKey4Read(String key) throws Exception{
		int[] result= (int[]) windowsRegOpenKey.invoke(systemRoot, new Object[] {
				new Integer(HKEY_LOCAL_MACHINE), stringToByteArray(key), new Integer(KEY_Read_ACCESS) });
		return result[0];
	}
	
	public int createKey(String keyName) throws Exception{
		int result[] = (int[])windowsRegCreateKeyEx.invoke(null, new Object[] {new Integer(HKEY_LOCAL_MACHINE), stringToByteArray(keyName)});
		if (result[1]!=0 || result[0]==0)
			throw new Exception("Find create specified Key");
		return result[0];
	}
	
	public String getValue(int handle, String key) throws Exception{
		byte[] result = (byte[]) windowsRegQueryValueEx.invoke(systemRoot,
				new Object[] { new Integer(handle), stringToByteArray(key) });
		if (result == null)
			return null;
		return new String(result).trim();
	}
	
	public void deleteKey(int handle, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		windowsRegDeleteKey.invoke(systemRoot, new Object[] { new Integer(handle), stringToByteArray(key) });
	}
	
	public void setValue(int handle, String name,  String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		windowsRegSetValueEx.invoke(systemRoot, new Object[] { 
		          new Integer(handle), stringToByteArray(name), stringToByteArray(value)}); 

	}
	
	public void deleteValue(int handle, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		windowsRegDeleteValue.invoke(systemRoot, new Object[] { new Integer(handle), stringToByteArray(value) });
	}

	public void closeKey(int handle) throws Exception{
		windowsRegCloseKey.invoke(systemRoot,
				new Object[] { new Integer(handle) });
	}
	
	private byte[] stringToByteArray(String str)
	{
		byte[] result = new byte[str.length() + 1];
		for (int i = 0; i < str.length(); i++)
		{
			result[i] = (byte) str.charAt(i);
		}
		result[str.length()] = 0;
		return result;
	}
}