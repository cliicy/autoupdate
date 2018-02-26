package com.ca.arcserve.edge.app.base.resources.messages;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Locale.Category;

import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;

public class MessageReader {
	
	private static final String BUNDLE_NAME_ERROR_MESSAGES		= "com.ca.arcserve.edge.app.base.resources.messages.ErrorMessages";
	private static final String BUNDLE_NAME_ERROR_MESSAGES_ASBU		= "com.ca.arcserve.edge.app.base.resources.messages.asbu.ErrorMessages";
	private static final String BUNDLE_NAME_DATE_FORMAT			= "com.ca.arcserve.edge.app.base.resources.messages.dateFormatEdge";
	private static final String BUNDLE_NAME_RPS_ERROR_MESSAGES	= "com.ca.arcflash.rps.webservice.properties.resources";
	private static final String BUNDLE_NAME_CM_LOG_UI_Const	  	= "com.ca.arcserve.edge.app.base.ui.client.components.log.i18n.UIConstants";
	private static final String BUNDLE_NAME_FLASH_UI_Const	= "com.ca.arcflash.ui.client.FlashUIConstants"; 
	private static final String BUNDLE_NAME_FLASH_UI_Msg	= "com.ca.arcflash.ui.client.FlashUIMessages"; 
	private static final String BUNDLE_NAME_CM_DASHBOARD_UI_Const	= "com.ca.arcserve.edge.app.base.ui.client.components.dashboard.i18n.UIConstants"; 
	
	private static final String PRODUCT_NAME_PLACEHOLDER = "^AU_ProductName_CONSOLE_SHORT^";
	
	private static Locale defaultLocale;
	private static Locale defaultDateFormatLocale;
	private static Map<String, Map<Locale, ResourceBundle>> cachedBundles = new HashMap<String, Map<Locale, ResourceBundle>>();

	static {
		defaultLocale = getDefaultLocale();
		defaultDateFormatLocale = getDefaultDateFormatLocale();
	}
	
	private static Locale getDefaultLocale() {
		/* fix 158826
		 * 1. before, ResourceBundle.getBundle() use Locale.getDefault() to get localization info. in jdk 1.6 getDefault() get locale information from control panel
		 *  .region.format. but in 1.7 it by default get localization info from control panel.region.keyboardandlanguage.displayLanguage.
		 *  so we use  Locale.getDefault(Category.FORMAT) to force it get local info from region.format.
		 *  the same modification is add in replicationMessage and DashboardResourceReader. DocTypeFilter,UILibraryServiceImpl
		 *  2. our localization message property file has both language and country suffix, for example de_DE,it's problem.
		 *   if our user use Germany, but in other country not German ( such as Austria ,AT ),
		 *  getLocale() get  country info AT, so java try to get resource file XXX_de_AT, but it doesn't exist. then it  try to get XXX_de, it doesn't exist, 
		 *  then it use XXX_en. so user get wrong message. so we change every "de" language user 's country to DE(German) to avoid this problem.
		 *  3. Zhang wenya has fix issue 1, 2 in DataFormatUtil, but this package( EdgeAppBaseContract ) cannot get that class, so we copy it here.
		 */
		Locale formatLocale = Locale.getDefault(Category.FORMAT);
		String country = formatLocale.getCountry();
		String language = formatLocale.getLanguage();
		
		if ("ja".equals(language))
			return new Locale(language, "JP");
		else if ("fr".equals(language))
			return new Locale(language, "FR");
		else if ("de".equals(language))
			return new Locale(language, "DE");
		else if ("pt".equals(language))
			return new Locale(language, "BR");
		else if ("es".equals(language))
			return new Locale(language, "ES");
		else if ("it".equals(language))
			return new Locale(language, "IT");
		else if (language.equals("zh")) {
			if (country.equalsIgnoreCase("CN") || country.equalsIgnoreCase("SG"))
				return new Locale(language, "CN");
			else
				return new Locale(language, "TW");
		} else {
			return new Locale("en");
		}
	}
	
	//defect 189031, get correct dateFormatlocale. For UK format, its locale should be en_GB
	private static Locale getDefaultDateFormatLocale(){		
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
			
		return locale;
	}
	
	private static synchronized ResourceBundle getBundle(String bundleName, Locale locale) {
		if (!cachedBundles.containsKey(bundleName)) {
			cachedBundles.put(bundleName, new HashMap<Locale, ResourceBundle>());
		}
		
		if (!cachedBundles.get(bundleName).containsKey(locale)) {
			ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
			cachedBundles.get(bundleName).put(locale, bundle);
		}
		
		return cachedBundles.get(bundleName).get(locale);
	}
	
	public static String getMessage(String bundleName, String key, Locale locale, Object... args) {
		try {
			ResourceBundle bundle = getBundle(bundleName, locale == null ? defaultLocale : locale);
			String message = bundle.getString(key);
			
			if (args != null && args.length > 0) {
				message = Utils.getMessage(message, args);
			}
			
			return message;
		} catch (Exception e) {
			return "";
		}
	}
	
	public static String getMessage(String bundleName, String key, Object... args) {
		return getMessage(bundleName, key, defaultLocale, args);
	}
	
	public static String getCMUILogConst( String key, Object... args ) {
		return getMessage(BUNDLE_NAME_CM_LOG_UI_Const, key, defaultLocale, args);
	}
	public static String getCMUIDashboardConst( String key, Object... args ) {
		return getMessage(BUNDLE_NAME_CM_DASHBOARD_UI_Const, key, defaultLocale, args);
	}
	public static String getFlashUIConst( String key, Object... args ){
		return getMessage(BUNDLE_NAME_FLASH_UI_Const, key, defaultLocale, args);
	}
	public static String getFlashUIMsg( String key, Object... args ){
		return getMessage(BUNDLE_NAME_FLASH_UI_Msg, key, defaultLocale, args);
	}
	
	public static String getErrorMessage(String key, Locale locale, Object... args) {
		String message = getMessage(BUNDLE_NAME_ERROR_MESSAGES, key, locale, args);
		return replaceProductNamePlaceholder(message);
	}
	
	public static String getASBUErrorMessage(String key, Locale locale, Object... args) {
		String message = getMessage(BUNDLE_NAME_ERROR_MESSAGES_ASBU, key, locale, args);
		return replaceProductNamePlaceholder(message);
	}
	
	public static String getASBUErrorMessage(String key, Object... args) {
		return getASBUErrorMessage(key, defaultLocale, args);
	}
	
	public static String getErrorMessage(String key, Object... args) {
		return getErrorMessage(key, defaultLocale, args);
	}
	
	public static String replaceProductNamePlaceholder(String message) {
		return message.contains(PRODUCT_NAME_PLACEHOLDER) ? message.replaceAll(PRODUCT_NAME_PLACEHOLDER, CommonUtil.getApplicationTitle()) : message;
	}
	
	public static String getDateFormat(String key) {
		return getMessage(BUNDLE_NAME_DATE_FORMAT, key, getDefaultDateFormatLocale());
	}
	
	public static String getDateFormat( String key, Locale locale )
	{
		return getMessage( BUNDLE_NAME_DATE_FORMAT, key, locale );
	}
	
	public static String getRpsErrorMessage(String errorCode, Locale locale, Object... args) {
		String key = "ServiceError_" + errorCode;
		String message = getMessage(BUNDLE_NAME_RPS_ERROR_MESSAGES, key, locale, args);
		return message;
	}
	
	public static String getRpsErrorMessage(String errorCode, Object... args) {
		return getRpsErrorMessage(errorCode, defaultLocale, args);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T loadMessages(final Class<T> type) {
		return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String bundleName = type.getName();
				return MessageReader.getMessage(bundleName, method.getName(), args);
			}
			
		});
	}
	
}
