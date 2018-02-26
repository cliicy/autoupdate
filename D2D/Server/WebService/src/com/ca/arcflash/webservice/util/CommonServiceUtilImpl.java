package com.ca.arcflash.webservice.util;


import java.util.Arrays;
import java.util.List;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.AxisFault;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.login.LoginDetail;
import com.ca.arcflash.webservice.data.login.LoginRole;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceException;


import org.codehaus.jackson.map.ObjectMapper;

public class CommonServiceUtilImpl {

    private WebServiceContext context;
	
	private static final Logger logger = Logger.getLogger(CommonServiceUtilImpl.class);
	private static final String STRING_SESSION_USERNAME = "com.ca.arcflash.webservice.FlashServiceImpl.UserName";
	private static final String STRING_SESSION_UUID 	= "com.ca.arcflash.webservice.FlashServiceImpl.UUID";
	
	private boolean localCheckSession = false;
	private static boolean enableSessionCheck = true;
	private volatile HttpSession session = null;
	private HttpServletRequest httpRequest;
	
	public CommonServiceUtilImpl(WebServiceContext context2, HttpServletRequest httpRequest) {
		context = context2;
		this.httpRequest = httpRequest;
	}

	public void checkSession() {
		if(localCheckSession){
			return;
		}
		if (!enableSessionCheck)
			return;
/*		HttpServletRequest request = null;
		if(context!=null)
		{
			MessageContext msgContext = context.getMessageContext();

			Object requestProperty = msgContext.get(MessageContext.SERVLET_REQUEST);
			if (requestProperty != null	&& requestProperty instanceof HttpServletRequest) {
				 request = (HttpServletRequest) requestProperty;
			}
		}
		// don't check session if it's a local call
		if (request==null || CommonService.getInstance().checkLocalHost(request.getRemoteAddr(), true))
				return;*/
		HttpSession session = getSession();
		if (session.getAttribute(STRING_SESSION_USERNAME) == null
					&& session.getAttribute(STRING_SESSION_UUID) == null)
				throw AxisFault.fromAxisFault("Service session timeout",
						FlashServiceErrorCode.Common_ServiceSessionTimeout);

	}
	
	public HttpSession getSession() {
		if (session != null)
			return session;
		else if (context != null) {
			Object requestProperty = context.getMessageContext().get(
					MessageContext.SERVLET_REQUEST);
			if (requestProperty != null
					&& requestProperty instanceof HttpServletRequest) {
				HttpServletRequest request = (HttpServletRequest) requestProperty;
				return request.getSession(true);
			}
			return null;
		}else if(httpRequest != null){
			return httpRequest.getSession(true);
		}
		return null;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}
	
	public int validateUserByUUID(String uuid) {
		logger.debug("validateUserByUUID(String) begin");
		try {
			logger.debug("validateUser(uuid) enter");
			CommonService.getInstance().validateUser(uuid);
			logger.debug("validateUser(uuid) exit");

			if (!localCheckSession) {
				HttpSession session = getSession();
				session.setAttribute(STRING_SESSION_UUID, uuid);
				logger.debug("session.setAttribute(STRING_SESSION_UUID)");
			}
			
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault4ValidateUser(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		logger.debug("validateUserByUUID(String) end");
		return 0;
	}
	
	public String validateUser(String username, String password, String domain)
			{
		try {
			String uuid = CommonService.getInstance().validateUser(username, password,
					domain);

			if (!localCheckSession) {
				HttpSession session = getSession();
				session.setAttribute(STRING_SESSION_USERNAME, username);
			}

			return uuid;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault4ValidateUser(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	
	public LoginDetail validateUserByUUIDWithDetail(String uuid,String logindetail) throws ServiceException{
		LoginDetail detail=null;
		if(StringUtil.isEmptyOrNull(logindetail)){
			detail=new LoginDetail();
			detail.setPassValidation(true);
			detail.setUsername("");
			detail.setRole(LoginRole.ROLE_ADMIN.getDescription());
			detail.setPermissions(null);
		}else{
			detail=validateLoginDetail(logindetail);
		}
		validateUserByUUID(uuid);
		detail.setUuid(uuid);
		return detail;
	}
	
	public LoginDetail validateUserWithDetail(String username, String password, String domain,String logindetail) throws ServiceException{	
		LoginDetail detail=null;
		if(StringUtil.isEmptyOrNull(logindetail)){
			detail=new LoginDetail();
			detail.setPassValidation(true);
			detail.setUsername("");
			detail.setRole(LoginRole.ROLE_ADMIN.getDescription());
			detail.setPermissions(null);
		}else{
			detail=validateLoginDetail(logindetail);	
		}
		String uuid=validateUser(username,password,domain);
		detail.setUuid(uuid);
		return detail;
	}
	
	
	public boolean validateLoginTimeWithServerTime(long logintime,long expired){
		boolean isexpired=false;
		long currenttime=System.currentTimeMillis();
		long expiredtime=(Long.MAX_VALUE-logintime<expired)?Long.MAX_VALUE:logintime+expired;
		isexpired=currenttime<expiredtime;
		return isexpired;
	}
	
	
	private LoginDetail validateLoginDetail(String encryptedlogindetail) throws ServiceException {
		ObjectMapper mapper = new ObjectMapper();
		String decryptedDetail = WSJNI.AFDecryptStringEx(encryptedlogindetail);
		LoginDetail detail = null;
		try {
			detail = mapper.readValue(decryptedDetail, LoginDetail.class);

		} catch (Exception e) {
			logger.error(e);
			throw new ServiceException(e.getMessage(), FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		boolean isExpired = (validateLoginTimeWithServerTime(detail.getLogintime(), detail.getExpired()));
		if (!isExpired)
			throw new ServiceException(FlashServiceErrorCode.Login_LoginExpired);
		detail.setPassValidation(isExpired);
		detail.setInternalLogin(true);
		return detail;
	}

	/*
	 * this API authenticates the user, and returns Node UUID or encrypted authentication UUID based on 
	 * bGetNodeUUID. bGetNodeUUID=true: returns Node UUID, bGetNodeUUID=false: returns authentication UUID
	 */
	public String validateUser(String username, String password, String domain, boolean bGetNodeID)
			{
		try {
			String uuid = CommonService.getInstance().validateUser(username, password,
					domain, bGetNodeID);

			if (!localCheckSession) {
				HttpSession session = getSession();
				session.setAttribute(STRING_SESSION_USERNAME, username);
			}

			return uuid;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault4ValidateUser(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	public String validateUserAndUpdateIfNeeded(String username, String password, String domain, boolean bGetNodeID) {
		try {
			String uuid = CommonService.getInstance().validateUserAndUpdateIfNeeded(username, password, domain, bGetNodeID);

			if (!isLocalCheckSession()) {
				HttpSession session = getSession();
				session.setAttribute(STRING_SESSION_USERNAME, username);
			}

			return uuid;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault4ValidateUser(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	public boolean isLocalCheckSession() {
		return localCheckSession;
	}

	public void setLocalCheckSession(boolean localCheckSession) {
		this.localCheckSession = localCheckSession;
	}

	public static boolean isEnableSessionCheck() {
		return enableSessionCheck;
	}

	public static void setEnableSessionCheck(boolean enableSessionCheck) {
		CommonServiceUtilImpl.enableSessionCheck = enableSessionCheck;
	}
	
	public SOAPFaultException convertServiceException2AxisFault4ValidateUser(
			ServiceException serviceException){
		
		String message = CommonService.getInstance().getServiceError(
				serviceException.getErrorCode(),
				serviceException.getMultipleArguments());
		
		if (StringUtil.isEmptyOrNull(message)) {
			logger.warn("Cannot find the error message for error code: " + serviceException.getErrorCode());
			message = serviceException.getMessage();
		}
		
		logger.error(serviceException.getErrorCode() + "[" + message + "]");
		logger.debug(serviceException.getMessage(), serviceException);
		
		return AxisFault.fromAxisFault(message,
				serviceException.getErrorCode(), serviceException.getMessage());
	}

	public SOAPFaultException convertServiceException2AxisFault(
			ServiceException serviceException) {
		logger.error(serviceException.getErrorCode());
		logger.error(serviceException.getMessage(), serviceException);

		String message = CommonService.getInstance().getServiceError(
				serviceException.getErrorCode(),
				serviceException.getMultipleArguments());
		
		if (StringUtil.isEmptyOrNull(message)) {
			logger.warn("Cannot find the error message for error code: " + serviceException.getErrorCode());
			message = serviceException.getMessage();
		}
		
		return AxisFault.fromAxisFault(message,
				serviceException.getErrorCode(), serviceException.getMessage());
		// return AxisFault.fromAxisFault(serviceException.getMessage(),
		// serviceException
		// .getErrorCode());
	}
}
