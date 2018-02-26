package com.ca.arcflash.webservice.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.NotEncryptAttribute;
import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.remotedeploy.RemoteDeployTarget;
import com.ca.arcflash.webservice.service.CommonService;
import com.sun.xml.internal.ws.util.StringUtils;

public class CryptUtil
{
	private static final Logger logger = Logger.getLogger(CryptUtil.class);
	
	public enum CRYPT_TYPE
	{
		CRYPT_TYPE_ENCRYPT,       // encrypt
		CRYPT_TYPE_DECRYPT,       // decrypt
		CRYPT_TYPE_ENCRYPT_AUTO,  // encrypt if it is not encrypted
		CRYPT_TYPE_DECRYPT_AUTO   // decrypt if it is encrypted
	}
	
	protected static String cryptString(String object, CRYPT_TYPE encryptType)
	{
		String result = object;
		
		if (object != null && !object.isEmpty())
		{
			try
			{
				switch (encryptType)
				{
				case CRYPT_TYPE_ENCRYPT:
					{
						result = CommonService.getInstance().getNativeFacade().encrypt(object);
						break;
					}

				case CRYPT_TYPE_DECRYPT:
					{
						result = CommonService.getInstance().getNativeFacade().decrypt(object);
						break;
					}
				case CRYPT_TYPE_ENCRYPT_AUTO:
					{
						if (!isEncrypted(object))
						{
							result = CommonService.getInstance().getNativeFacade().encrypt(object);
						}
						break;
					}

				case CRYPT_TYPE_DECRYPT_AUTO:
					{
						if (isEncrypted(object))
						{
							result = CommonService.getInstance().getNativeFacade().decrypt(object);
						}
						break;
					}
				}
			}
			catch(Exception e)
			{
				
				logger.error("Failed to encrypt/decrypt string = " + object + "\r\n" + e + e.getStackTrace());
			}
		}
		
		return result;
	}	
	// go through an object recursive, encrypt/decrypt the String fields with @Encrypted annotation
	public static Object crypt(Object object, CRYPT_TYPE encrypt)
	{
		Object result = object;
		
		if (object != null)
		{
			// string
			if (object instanceof String) 
			{
				result = cryptString( (String) object, encrypt);
			}
			// array
			else if (object.getClass().isArray())
			{
				if(!object.getClass().getComponentType().isPrimitive())
				for (Object item: (Object[])object)
				{
					crypt(item, encrypt);
				}
			}
			// iterable
			else if (object instanceof Iterable)
			{
				for (Object item: (Iterable)object)
				{
					crypt(item, encrypt);
				}
			}
			// customized object 
			else if (object.getClass().getName().startsWith("com.ca"))
			{
				try
				{
					// go through the fields
					Field[] fields = object.getClass().getDeclaredFields();

					for (Field field : fields)
					{
						try
						{
							// String field with @Encrypted or @NotPrintAttribute
							if (field.getType().getName().equals("java.lang.String") )
							{
								if(field.getAnnotation(NotEncryptAttribute.class) != null){
									continue;
								}
								
								if ((field.getAnnotation(NotPrintAttribute.class) != null) 
										// try to decrypt the string with password keywords
										|| ( encrypt == CRYPT_TYPE.CRYPT_TYPE_DECRYPT_AUTO && hasPasswordKeywords(field.getName())) )
								{
									Method getMethod = object.getClass().getMethod("get" + com.sun.xml.internal.ws.util.StringUtils.capitalize(field.getName()), new Class[] {});
									Method setMethod = object.getClass().getMethod("set" + StringUtils.capitalize(field.getName()),	new Class[] { field.getType() });
									
									// get the string
									String fieldObject = (String) getMethod.invoke(object, new Object[] {});
									
									if (fieldObject != null )
									{
										// set the string back after encrypt/decrypt
										setMethod.invoke(object, new Object[] { cryptString( fieldObject, encrypt) });
									}
								}
							}
							// other fields
							else if (! field.getType().isPrimitive() ) 
							{
								Method getMethod = object.getClass().getMethod("get" + StringUtils.capitalize(field.getName()), new Class[] {});
								Object fieldObject = getMethod.invoke(object, new Object[] {});								
								crypt(fieldObject, encrypt);
							}
						}
						catch (NoSuchMethodException ex)
						{
							logger.debug("No such Method:" + ex.getMessage());
						}
						catch (Exception e)
						{
							logger.error("Exception encountered during encrypting a field" + e + e.getStackTrace());
						}
					}
				}
				catch (Exception e)
				{
					logger.error("Exception encountered during encrypting" + e + e.getStackTrace());
				}
			}			
		}
		
		return result;
	}
	
	protected static boolean isEncrypted(String object)
	{
		boolean result = false;
		
		if (object != null && object.length() > 100)
		{
			result = true;
		}
		
		return result;		
	}
	
	public static Object encrypt(Object object)
	{
		return crypt(object, CRYPT_TYPE.CRYPT_TYPE_ENCRYPT_AUTO);
	}
	
	public static Object decrypt(Object object)
	{
		return crypt(object, CRYPT_TYPE.CRYPT_TYPE_DECRYPT_AUTO);
	}
	
	public static Object[] decryptParameters(Method method, String[] parameterNames, Object[] args)
	{
		if (method == null || parameterNames == null || args == null)
		{
			return args;
		}
		
		try
		{
			Annotation[][] parameterAnnotations = method.getParameterAnnotations();

			if (args != null && args.length > 0)
			{
				for (int i = 0; i < args.length; i++)
				{
					try
					{
						Object object = args[i];

						if (object != null)
						{
							boolean toBeDecrypted = false;

							if (object.getClass().isPrimitive() || object instanceof Number)
							{
								continue;
							}
							else if (object.getClass().getName().startsWith("com.ca"))
							{
								toBeDecrypted = true;
							}
							else if (object instanceof String)
							{
								// if the parameter name contains "password" or "pwd"
								String parameterName = parameterNames[i];
								if (hasPasswordKeywords(parameterName))
								{
									toBeDecrypted = true;
								}
								else
								// if the parameter is marked with @NotPrintAttribute or @Encrypted
								{
									Annotation[] argAnnos = parameterAnnotations[i];
									for (Annotation argA : argAnnos)
									{
										if (argA instanceof NotPrintAttribute)
										{
											toBeDecrypted = true;
											break;
										}
									}
								}
							}else if (object.getClass().isArray()){
								Object[] obj = (Object[])object;
								//special handle for setRemoteDeploy since D2D r16.5 GM is near
								//we need to handle all the array and collections cases properly next release.
								if(obj.length > 0 && obj[0] instanceof RemoteDeployTarget)
									toBeDecrypted = true;
							}

							if (toBeDecrypted)
							{
								args[i] = CryptUtil.decrypt(args[i]);
							}
						}
					}
					catch (Exception e)
					{
						logger.error("Error occurred when decrypt a parameter \r\n" + e + e.getStackTrace());
					}
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Error occurred when decrypt the parameters \r\n" + e + e.getStackTrace());
		}
		
		return args;
	}
	
	
	public static Object encryptReturnValue(Method method, Object result)
	{
		if (method == null || result == null)
		{
			return result;
		}
		
		Object encryptedResult = result;
		
		try 
		{
			boolean toBeEncrypted = false;

			if (method.getAnnotation(NotPrintAttribute.class) != null)
			{
				toBeEncrypted = true;
			}
			else if ((result instanceof String) || (result instanceof String[]))
			{
				if (method.getName().startsWith("get") && method.getName().contains("Password"))
				{
					toBeEncrypted = true;
				}
			}
			else if (result.getClass().getName().startsWith("com.ca"))
			{
				toBeEncrypted = true;
			}
			else if (result.getClass().isArray() || result instanceof Iterable)
			{
				toBeEncrypted = true;
			}

			if (toBeEncrypted)
			{
				// create a copy of the return value, do not change the original one
				encryptedResult = copyObject(result);
				CryptUtil.encrypt(encryptedResult);
			}
		}
		catch(Exception e)
		{
			logger.error("Error occurred when encrypt the return value \r\n", e);
			encryptedResult = result;
		}
		
		return encryptedResult;
	}
	
	// check if the current web service method is called by classes within the web service
	public static boolean isCalledWithinWebService()
	{
		boolean result = true;
		
		try
		{
			StackTraceElement stack[] = Thread.currentThread().getStackTrace();
			// a sample stack trace
//			called by java.lang.Thread.run/Thread.java
//			called by java.lang.Thread.getStackTrace/Thread.java
//			called by com.ca.arcflash.webservice.util.CryptUtil.getCaller/CryptUtil.java
//			called by com.ca.arcflash.webservice.FlashServiceImpl.getAdminAccount_aroundBody103$advice/FlashServiceImpl.java
//			called by com.ca.arcflash.webservice.FlashServiceImpl.getAdminAccount/FlashServiceImpl.java
//			called by com.ca.arcflash.webservice.FlashServiceImpl.getD2DConfiguration_aroundBody486/FlashServiceImpl.java
//			called by com.ca.arcflash.webservice.FlashServiceImpl.getD2DConfiguration_aroundBody487$advice/FlashServiceImpl.java
//			called by com.ca.arcflash.webservice.FlashServiceImpl.getD2DConfiguration/FlashServiceImpl.java
//			called by sun.reflect.NativeMethodAccessorImpl.invoke0/NativeMethodAccessorImpl.java
//			called by sun.reflect.NativeMethodAccessorImpl.invoke/NativeMethodAccessorImpl.java
//			called by sun.reflect.DelegatingMethodAccessorImpl.invoke/DelegatingMethodAccessorImpl.java
			
			for (int i=0; i<stack.length; i++)
			{
				//System.out.println("called by " + stack[i].getFileName() + stack[i].getClassName() + "." + stack[i].getMethodName() + "/");
				
				if (stack[i] != null && stack[i].getClassName().equals("com.ca.arcflash.webservice.util.CryptUtil")
						&& stack[i].getMethodName().equals("isCalledWithinWebService"))
				{
					if (i+5 < stack.length)
					{
						if (!(stack[i+5].getClassName().startsWith("com.ca")))
						{
							result = false;
							break;
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Error occurred when checking the caller \r\n" + e + e.getStackTrace());
			result = true; 
		}		
		
		return result;
	} 
	
	// create a copy of an input object, this is used for the return value of D2D web service
	public static Object copyObject(Object source) throws InstantiationException, IllegalAccessException
	{
		// do nothing for null and empty array or list
		if (source == null || (source.getClass().isArray() && Array.getLength(source) == 0) 
				|| (source instanceof List && ((List)source).size() == 0))
		{
			return source;
		}
		
		Object result = null;
		
		// copy object array
		if (source.getClass().isArray())
		{
			result = Array.newInstance(Array.get(source, 0).getClass(), Array.getLength(source));
			
			for (int i=0; i<Array.getLength(source); i++)
			{
				StringWriter buffer = new StringWriter();
				JAXB.marshal(Array.get(source, i), buffer);		
				Array.set(result, i, JAXB.unmarshal(new StringReader(buffer.toString()), Array.get(source, i).getClass()));
			}
		}
		// copy List
		else if (source instanceof List)
		{
			result = source.getClass().newInstance();
			
			for (int i=0; i<((List)source).size(); i++)
			{
				StringWriter buffer = new StringWriter();
				JAXB.marshal(((List)source).get(i), buffer);	
				((List)result).add(JAXB.unmarshal(new StringReader(buffer.toString()), ((List)source).get(i).getClass()));
			}
		}
		// copy a normal object
		else
		{
			StringWriter buffer = new StringWriter();
			JAXB.marshal(source, buffer);		
			result = JAXB.unmarshal(new StringReader(buffer.toString()), source.getClass());
		}
		
		return result;
	}
	
	// check if the name has any keyword which could indicate it is a password variable
	private static boolean hasPasswordKeywords(String name)
	{
		boolean result = false;
	
		if (name != null && !name.isEmpty())
		{
			if (name.toLowerCase().contains("password") 
					|| name.toLowerCase().contains("pwd")
					|| name.toLowerCase().contains("encrypt"))
			{
				result = true;
			}
		}	
		
		return result;
	}
}
