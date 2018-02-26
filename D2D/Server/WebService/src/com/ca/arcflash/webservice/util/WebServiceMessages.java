package com.ca.arcflash.webservice.util;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.StringUtil;

public class WebServiceMessages {

	private static ResourceBundle labels;

	static {
		 Locale locale = DataFormatUtil.getServerLocale();
		labels = ResourceBundle
			.getBundle("com.ca.arcflash.webservice.util.wsmessages", locale, 
					Control.getNoFallbackControl(Control.FORMAT_DEFAULT));
	}
	
	public static String getResource(String key){
		return labels.getString(key);
	}
	
	public static String getResource(String key,String... pars){
		
		String labelValue= labels.getString(key);
		return StringUtil.format(labelValue, (Object[])pars);
		
	}
	
	public static String getResource(String key,Object[] pars){
		String labelValue= labels.getString(key);
		return StringUtil.format(labelValue, pars);
		
	}
}
