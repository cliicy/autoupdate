package com.ca.arcflash.common;

import java.util.Locale;
import java.util.Locale.Category;

public class DataFormatUtil {
	//private static ResourceBundle resourceBundle;
	private static Locale serverLocale = null;
	private static Locale formatLocale = null;
	
	/*static{
		getResourceBundle();
	}
	
	private static void getResourceBundle() {
		Locale locale = getDateFormatLocale();		
		resourceBundle = ResourceBundle.getBundle(
				"com.ca.arcflash.common.properties.dataformat", locale, Control.getNoFallbackControl(Control.FORMAT_DEFAULT));	
		
	}
		
	public static String getString(String key) {
		return resourceBundle.getString(key);
	}
	
	public static DateFormat getDataFormat(){
		DateFormat dataFormat = new DateFormat();
		dataFormat.setDateFormat(getString("dateFormat"));
		dataFormat.setShortTimeFormat(getString("shortTimeFormat"));
		dataFormat.setTimeDateFormat(getString("timeDateFormat"));
		dataFormat.setTimeFormat(getString("timeFormat"));
		return dataFormat;
	}*/
	
	public synchronized static Locale getDateFormatLocale(){
		if(formatLocale != null)
			return formatLocale;
		
		Locale locale = null;
		Locale formatLocale = Locale.getDefault(Category.FORMAT);
		String country = formatLocale.getCountry();
		String language = formatLocale.getLanguage();
		
		if ("ja".equals(language))
			locale = new Locale(language, "JP");
		else if ("fr".equals(language))
			locale = new Locale(language, "FR");
		else if ("de".equals(language))
			locale = new Locale(language, "DE");
		else if ("pt".equals(language))
			locale = new Locale(language, "BR");
		else if ("es".equals(language))
			locale = new Locale(language, "ES");
		else if ("it".equals(language))
			locale = new Locale(language, "IT");
		else if (language.equals("zh")) {
			if (country.equalsIgnoreCase("CN")
					|| country.equalsIgnoreCase("SG"))
				locale = new Locale(language, "CN");
			else
				locale = new Locale(language, "TW");
		} else if(language.equals("en") && country.equalsIgnoreCase("GB")){
			locale = new Locale(language, "GB");
		}
		else {
			locale = new Locale("en");
		}
		DataFormatUtil.formatLocale = locale;		
		return locale;
	}
	
	/**
	 * In D2D, we always use the format locale to show date, number and language.
	 * @return
	 */
	public synchronized static Locale getServerLocale() {
		if(serverLocale != null)
			return serverLocale;
		
		Locale locale = null;
		Locale formatLocale = Locale.getDefault(Category.FORMAT);
		String country = formatLocale.getCountry();
		String language = formatLocale.getLanguage();
		
		if ("ja".equals(language))
			locale = new Locale(language, "JP");
		else if ("fr".equals(language))
			locale = new Locale(language, "FR");
		else if ("de".equals(language))
			locale = new Locale(language, "DE");
		else if ("pt".equals(language))
			locale = new Locale(language, "BR");
		else if ("es".equals(language))
			locale = new Locale(language, "ES");
		else if ("it".equals(language))
			locale = new Locale(language, "IT");
		else if (language.equals("zh")) {
			if (country.equalsIgnoreCase("CN")
					|| country.equalsIgnoreCase("SG"))
				locale = new Locale(language, "CN");
			else
				locale = new Locale(language, "TW");
		}else {
			locale = new Locale("en");
		}
		DataFormatUtil.serverLocale = locale;
		return locale;
	}
}
