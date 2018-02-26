package com.ca.arcflash.ui.server.servlet;



import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

public final class SessionListener implements HttpSessionListener {

	public static Logger logger = Logger.getLogger(SessionListener.class);
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		final HttpSession session = arg0.getSession();
		final ServletContext context = session.getServletContext();
		
		context.removeAttribute(session.getId());
		
		logger.debug("Session removed" + session.getId());
	}

}
