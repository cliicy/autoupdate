package com.ca.arcflash.ui.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ui.server.servlet.ContextListener;
import com.ca.arcflash.ui.server.servlet.SessionConstants;
import com.ca.arcflash.ui.server.servlet.SessionInformation;
import com.ca.arcflash.webservice.data.login.LoginDetail;

public class InternalLoginServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8282686618333071696L;
	Logger logger = Logger.getLogger(this.getClass());
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String LOCATION = "location";
	public static final String UUID = "uuid";
	public static final String VMNAME = "vmname";
	public static final String VMINSTANCEUUID = "instanceuuid";
	public static final String LOCATION_VSPHERE = "vm"; 
	public static final String LOCATION_BACKUP_SETTINGS = "backupSettingsIndividual";
	public static final String LOCATION_RESTORE_MAIN   = "restoreMainIndividual";
	public static final String SELECT_TAB = "selecttab";
	public static final String TASK = "task";
	public static final String DETAIL = "detail";
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		login(req, resp);
	}
	
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    	login(req, resp);
    }

	private void login(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String userName = req.getParameter(USERNAME);
		String password = req.getParameter(PASSWORD);
		String location = req.getParameter(LOCATION);
		String uuid = req.getParameter(UUID);
		String selectTab = req.getParameter(SELECT_TAB);
		String SessUser = userName;
		String detail = req.getParameter(DETAIL);
		LoginServiceImpl login = new LoginServiceImpl();
		String redirect = "/index.html";
		
		boolean isValidateUserNeeded = false;
		String Protocol = req.getScheme();
		Protocol += ":";
		String Domain = "";

		try {
			if(uuid != null && uuid.trim().length() > 0)
			{
				logger.info("Protocol ==" + Protocol + ",Port ==" + ContextListener.webServicePort + ", ServerName ==" + req.getServerName());

				login.validateUserByUuid(req, uuid, "localhost", ContextListener.webServicePort,Protocol,detail);
							
				Cookie cookie = new Cookie("requestMethod","1");
				resp.addCookie(cookie);
				HttpSession session = req.getSession();
				session.setAttribute(SessionConstants.SRING_LOGIN_FLAG, 1);
				
				SessionInformation sessionInfo = new SessionInformation();
				sessionInfo.strUsername = "";
				sessionInfo.strPassword = "";
				
				getServletContext().setAttribute(session.getId(), sessionInfo);
				
				logger.info("Session recorded " + session.getId());	
			} else
				if(userName != null && userName.length() > 0)
				{
					isValidateUserNeeded = true; 
					
					logger.info(userName + "," + Protocol + "," + ContextListener.webServicePort + "," + req.getServerName());
					
					int pos = userName.indexOf("\\"); // ex) tant-a01\kimwo01
					if (pos != -1) // If not exist.
					{
						// Extract domain part
						Domain = userName.substring(0, pos);
						userName = userName.substring(pos+1);
					}
	
//					login.validateUser(req, Protocol, "localhost", ContextListener.webServicePort, Domain, userName, password);
					Cookie cookie = new Cookie("requestMethod","1");
					resp.addCookie(cookie);
					
					HttpSession session = req.getSession();
					session.setAttribute(SessionConstants.SRING_LOGIN_FLAG, 1);
					
					SessionInformation sessionInfo = new SessionInformation();
					sessionInfo.strUsername = SessUser;
					sessionInfo.strPassword = password;//(String)session.getAttribute(SessionConstants.SRING_PASSWORD);
					
					getServletContext().setAttribute(session.getId(), sessionInfo);
					Cookie cookieSession = new Cookie(getServletContext().getSessionCookieConfig().getName(),session.getId());
					resp.addCookie(cookieSession);
					
					logger.info("Session recorded " + session.getId());
				}
			if(!StringUtil.isEmptyOrNull(location)){
				redirect = "/index.html?location=" + location;
				//redirect = "/index.html?gwt.codesvr=127.0.0.1:9997&location=" + location;
				if(location.equalsIgnoreCase(LOCATION_VSPHERE)){
					redirect += "&" + VMNAME + "="+req.getParameter(VMNAME) + "&" + VMINSTANCEUUID + "=" + req.getParameter(VMINSTANCEUUID);
					
					String task = req.getParameter(TASK);
					if (!StringUtil.isEmptyOrNull(task)) {
						redirect += "&" + TASK + "=" + task;
					}
				}
				else if(location.equalsIgnoreCase(LOCATION_BACKUP_SETTINGS) || location.equalsIgnoreCase(LOCATION_RESTORE_MAIN))
				{
					String VMInstance = req.getParameter(VMINSTANCEUUID);
					String VMName = req.getParameter(VMNAME);
					if(!StringUtil.isEmptyOrNull(VMInstance) && !StringUtil.isEmptyOrNull(VMName))
					{
						redirect += "&" + VMNAME + "="+ VMName + "&" + VMINSTANCEUUID + "=" + VMInstance;
					}
				}
			}
			
			if(!StringUtil.isEmptyOrNull(selectTab)) {
				if(redirect.indexOf('?') > 0) {
					redirect += "&";
				}
				else {
					redirect += "?";
				}
				
				redirect += (SELECT_TAB + "=" + selectTab);
			}
			
			if (isValidateUserNeeded) {
				login.validateUser(req, Protocol, "localhost", ContextListener.webServicePort, Domain, userName, password,detail);
			}
			
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			HttpSession session = req.getSession();
			if (session != null) {
				session.removeAttribute(SessionConstants.SRING_UUID);
				session.removeAttribute(SessionConstants.SRING_USERNAME);
				session.removeAttribute(SessionConstants.SRING_PASSWORD);
				session.removeAttribute(SessionConstants.SRING_LOGIN_DETAIL);
			}
		} 
		logger.info(redirect);
		resp.sendRedirect(redirect);
	}

}
