package com.ca.arcflash.webservice.aspects;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.ca.arcflash.webservice.util.CryptUtil;

@Aspect
public class D2DWebServiceEncryptionAtAspectJ {

	private static final Logger logger = Logger.getLogger(D2DWebServiceEncryptionAtAspectJ.class);
	
	private static boolean isNative(JoinPoint.StaticPart jpsp) {
		return Modifier.isNative(jpsp.getSignature().getModifiers());
	}
	
	@Pointcut("(call( * com.ca.arcflash.webservice.jni.WSJNI.*(..)) )")
	private void nativeCall(){
		
	} 
	
	@Pointcut("( " +
			// methods may have input passwords
			"execution( public * com.ca.arcflash.webservice.FlashServiceImpl.*(.., (String || String[] || com.ca.arcflash..* || com.ca.arcflash..*[] || java.lang.Iterable+), ..)) " +
			
			// methods may return passwords
			"|| execution( @com.ca.arcflash.service.common.NotPrintAttribute public !void com.ca.arcflash.webservice.FlashServiceImpl.*(..)) " +			
			"|| execution( public (com.ca.arcflash..* || com.ca.arcflash..*[]) com.ca.arcflash.webservice.FlashServiceImpl.*(..))" +
			"|| execution( public (String || String[]) com.ca.arcflash.webservice.FlashServiceImpl.get*Password*(..))" +
			") " +			
			"&& within(com.ca.arcflash.webservice.FlashServiceImpl)")
	private void InnerWebServiceExecute(){
		
	}
	
	
	@Pointcut("( " +
			// methods may have input passwords
			"execution( public * *(.., (String || String[] || com.ca.arcflash..* || com.ca.arcflash..*[] || java.lang.Iterable+), ..)) " +
			
			// methods may return passwords
			"|| execution(public !void *.*(..)))" +			
			"&& within(com.ca.arcserve.webservice.WebServiceImpl)")
	private void OuterWebServiceExecute(){
		
	}
	
	@Around("InnerWebServiceExecute() || OuterWebServiceExecute()")			
	public Object encryptDescryptPassword(ProceedingJoinPoint pjp) throws Throwable
	{
		return pwdProcess(pjp);
	}
	
	@Before("nativeCall()")
	public void loggBefore(JoinPoint jp) {
		logger.debug("Enter " + jp.getSignature().toLongString());
	}
	
	@After("nativeCall()")
	public void loggAfter(JoinPoint jp) {
		logger.debug("Exit " + jp.getSignature().toLongString());
	}
	
	
	private Object pwdProcess(ProceedingJoinPoint pjp) throws Throwable{
		Signature sig = pjp.getSignature();
		Object[] args = pjp.getArgs();
		
		// if this is called within web service (not by a web service client), do not change anything
		if (CryptUtil.isCalledWithinWebService())
		{
			return pjp.proceed(args);
		}
		
		Method method = null;		
		String[] parameterNames = null;
		
		if (sig instanceof MethodSignature)
		{
			MethodSignature ms = (MethodSignature) sig;
			method = ms.getMethod();
			parameterNames = ms.getParameterNames();
		}

		// decrypt parameters
		try
		{
			if (method != null && parameterNames != null && args != null)
			{
				args = CryptUtil.decryptParameters(method, parameterNames, args);
			}
		}
		catch (Exception e)
		{
			logger.error("Error occurred when decrypt the parameters \r\n" + e + e.getStackTrace());
		}
		
		
		Object result = pjp.proceed(args);
		
		
		// encrypt return value
		try 
		{
			if (method != null && result != null)
			{
				result = CryptUtil.encryptReturnValue(method, result);
			}
		}
		catch(Exception e)
		{
			logger.error("Error occurred when encrypt the return value \r\n" + e + e.getStackTrace());
		}
		
		return result;
	}
}
