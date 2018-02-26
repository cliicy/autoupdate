package com.ca.arcserve.edge.app.base.webservice.policymanagement;

import javax.servlet.http.HttpSession;

import com.ca.arcserve.edge.app.base.util.CommonUtil;

public class PolicyEditSessionManager
{
	private static PolicyEditSessionManager instance = null;
	
	//////////////////////////////////////////////////////////////////////////
	
	private PolicyEditSessionManager()
	{
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public static PolicyEditSessionManager getInstance()
	{
		if (instance == null)
			instance = new PolicyEditSessionManager();
		
		return instance;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	private void setSessionAttribute(
		HttpSession httpSession, String name, Object value )
	{
		httpSession.setAttribute( name, value );
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	private Object getSessionAttribute(
		HttpSession httpSession, String name )
	{
		return httpSession.getAttribute( name );
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public PolicyEditSession createSession( HttpSession httpSession )
	{
		PolicyEditSession newSession = new PolicyEditSession();
		setSessionAttribute( httpSession,
			CommonUtil.STRING_SESSION_POLICYEDITSESSION, newSession );
		
		return newSession;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public PolicyEditSession getSession( HttpSession httpSession )
	{
		return (PolicyEditSession)getSessionAttribute( httpSession,
			CommonUtil.STRING_SESSION_POLICYEDITSESSION );
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public void deleteSession( HttpSession httpSession )
	{
		setSessionAttribute( httpSession,
			CommonUtil.STRING_SESSION_POLICYEDITSESSION, null );
	}
}
