package com.ca.arcserve.edge.app.rps.webservice.i18n;

import java.util.Locale;

import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;

public class RpsMessageReader {
	
	private static final String BUNDLE_NAME_ERROR_MESSAGES	= "com.ca.arcserve.edge.app.rps.webservice.i18n.ErrorMessages";
	
	public static String getErrorMessage(String key, Locale locale, Object... args) {
		return MessageReader.getMessage(BUNDLE_NAME_ERROR_MESSAGES, key, locale, args);
	}
	
	public static String getErrorMessage(String key, Object... args) {
		return MessageReader.getMessage(BUNDLE_NAME_ERROR_MESSAGES, key, args);
	}

}
