/**
 * @(#)EdgeRPSWebServiceMessages.java 7/20/2011
 * Copyright 2011 CA Technologies, Inc. All rights reserved. 
 */
package com.ca.arcserve.edge.app.rps.webservice.i18n;

import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;

/**
 * Class<code>EdgeRPSWebServiceMessages</code> is for storing the message info of web
 * service side.
 * 
 * @author lijbi02
 * @version 1.0 7/20/2011
 * @since JDK1.6
 *
 */
public class EdgeRPSWebServiceMessages {
	
	private static String getMessage(String key) {
		return MessageReader.getMessage("com.ca.arcserve.edge.app.rps.webservice.i18n.rpswsmessages", key);
	}
	
	public static String getResource(String key) {
		return getMessage(key);
	}
	
	public static String getResource(String key, String... pars) {
		String message = getMessage(key);
		return String.format(message, (Object[])pars);
	}
	
	public static String getMessage(String key, Object... arguments) {
		String message = getMessage(key);
		if (arguments != null && arguments.length > 0){
			return Utils.getMessage(message, arguments);
		}
		return message;
	}

}
