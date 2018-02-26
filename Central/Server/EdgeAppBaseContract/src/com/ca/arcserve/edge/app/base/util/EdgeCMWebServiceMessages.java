package com.ca.arcserve.edge.app.base.util;

import java.util.Locale;

import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;

public class EdgeCMWebServiceMessages {

	private static String getMessage(String key) {
		return MessageReader.getMessage("com.ca.arcserve.edge.app.base.resources.messages.cmwsmessages", key);
	}
	
	private static String getMessage(Locale locale, String key) {
		return MessageReader.getMessage("com.ca.arcserve.edge.app.base.resources.messages.cmwsmessages", key, locale);
	}
	
	public static String getResource(String key) {
		String message = getMessage(key);
		return MessageReader.replaceProductNamePlaceholder(message);
	}
	
	public static String getResource(String key, String... pars) {
		String message = getMessage(key);
		message = String.format(message, (Object[])pars);
		return MessageReader.replaceProductNamePlaceholder(message);
	}
	
	public static String getMessage(String key, Object... arguments) {
		String message = getMessage(key);
		if (arguments != null && arguments.length > 0){
			message = Utils.getMessage(message, arguments);
		}
		return MessageReader.replaceProductNamePlaceholder(message);
	}
	
	public static String getMessage(Locale locale, String key, Object... arguments) {
		String message = getMessage(locale, key);
		if (arguments != null && arguments.length > 0){
			message = Utils.getMessage(message, arguments);
		}
		return MessageReader.replaceProductNamePlaceholder(message);
	}
	
}
