package com.ca.arcflash.ui.server;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

public class ResourcesReader {
	
	public static String getResource(String key, String locale){
		ResourceBundle labels;
		
		if(locale == null || locale.isEmpty()) {
			labels = ResourceBundle.getBundle(
					"com.ca.arcflash.common.properties.resources", 
					new Locale("en"),
					Control.getNoFallbackControl(Control.FORMAT_DEFAULT));
			
			return labels.getString(key);
		}
		
		if(!locale.contains("_")) {
			labels = ResourceBundle.getBundle(
					"com.ca.arcflash.common.properties.resources", 
					new Locale(locale),
					Control.getNoFallbackControl(Control.FORMAT_DEFAULT));
		}
		else {
			int index = locale.indexOf("_");
			String language = locale.substring(0, index);
			String country = locale.substring(index + 1);
			
			labels = ResourceBundle.getBundle(
					"com.ca.arcflash.common.properties.resources", 
					new Locale(language,country),
					Control.getNoFallbackControl(Control.FORMAT_DEFAULT));
		}
		return labels.getString(key);
	}
}
