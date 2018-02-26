package com.ca.arcflash.ui.server.properties;

import java.util.ResourceBundle;
/**
 * this class is used to get resource from client's two resource file.
 * @author gonro07
 *
 */
public class JSPMessage {

	public static String getRssURLResource(String language,String country){
		java.util.Locale lo = new java.util.Locale (language,country);
		ResourceBundle bundle = ResourceBundle.getBundle("com.ca.arcflash.ui.client.Links",lo);
		return bundle.getString("rssURL");
	}
	public static String getLoadingResource(String language,String country){
		java.util.Locale lo = new java.util.Locale (language,country);
		ResourceBundle bundle = ResourceBundle.getBundle("com.ca.arcflash.ui.client.FlashUIConstants",lo);
		return bundle.getString("loadingIndicatorText");
	}
}
