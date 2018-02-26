package com.ca.arcflash.ui.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ui.server.servlet.ContextListener;
import com.ca.arcflash.ui.server.servlet.SessionInformation;

public class InternalSessionLoginServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7275661657302410789L;

	Logger logger = Logger.getLogger(this.getClass());
	public static final String SESSIONID = "jsid";
	public static final String LOCATION = "location";
	public static final String VMNAME = "vmname";
	public static final String VMINSTANCEUUID = "instanceuuid";
	public static final String DETAIL = "detail";
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		String redirect = GetRedirectLocation(req);
		resp.sendRedirect(redirect);
	}
	
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
		String redirect = GetRedirectLocation(req);
		resp.sendRedirect(redirect);
    }
    
    
    private String GetRedirectLocation(HttpServletRequest req)
    {
    	String location = req.getParameter(LOCATION);
    	String sessionID = req.getParameter(SESSIONID);
    	SessionInformation sessInfo = (SessionInformation) this.getServletContext().getAttribute(sessionID);
    	String detail = req.getParameter(DETAIL);
		String userName, password, Domain;
		
		LoginServiceImpl login = new LoginServiceImpl();
		String redirect = "/index.html";
		try {
			userName = sessInfo.strUsername;
			password = sessInfo.strPassword;
			Domain = "";
			
			logger.info("User: " + userName + " Login");
			
			if(userName != null && userName.length() > 0)
			{
				String Protocol = req.getScheme();
				
				Protocol += ":";
				
				logger.info(userName + "," + "," + Protocol + "," + ContextListener.webServicePort + "," + req.getServerName());
				
				int pos = userName.indexOf("\\"); // ex) tant-a01\kimwo01
				if (pos != -1) // If not exist.
				{
					// Extract domain part
					Domain = userName.substring(0, pos);
					userName = userName.substring(pos+1);
				}
				
				login.validateUser(req, Protocol, "localhost", ContextListener.webServicePort, Domain, userName, password,detail);
			}
			if(!StringUtil.isEmptyOrNull(location)){
				redirect = "/index.html?location=" + location;
				
				String VMInstance = req.getParameter(VMINSTANCEUUID);
				String VMName = req.getParameter(VMNAME);
				if(!StringUtil.isEmptyOrNull(VMInstance) && !StringUtil.isEmptyOrNull(VMName))
				{
					redirect += "&" + VMNAME + "="+ VMName + "&" + VMINSTANCEUUID + "=" + VMInstance;
				}
			}
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		logger.info(redirect);
		return 	redirect;
    }
}
