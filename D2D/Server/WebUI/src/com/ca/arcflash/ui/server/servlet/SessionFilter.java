package com.ca.arcflash.ui.server.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.MessageFormatEx;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.data.VersionInfo;

public class SessionFilter implements Filter {
	private FilterConfig filterConfig;
	private static final Logger logger = Logger.getLogger(SessionFilter.class);
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		resp.setCharacterEncoding("utf-8");
		HttpSession session = req.getSession(true);
		String locale = DataFormatUtil.getServerLocale().getLanguage();
		String country = DataFormatUtil.getServerLocale().getCountry();
		logger.debug("System locale:"+locale);
		logger.debug("System country:"+country);
		
		try{
			if (session.getAttribute(SessionConstants.SERVICE_CLIENT)!=null){
				WebServiceClientProxy serviceClient = (WebServiceClientProxy)session.getAttribute(SessionConstants.SERVICE_CLIENT);
				VersionInfo versionInfo = serviceClient.getService().getVersionInfo();
				locale = versionInfo.getLocale();
				country = versionInfo.getCountry();
				logger.debug("Server locale:"+locale);
				logger.debug("Server country:"+country);
			}
		}catch (Exception e){
			
		}
		
		if ("ja".equals(locale))
			locale = "ja_JP";
		else if ("fr".equals(locale))
			locale = "fr_FR";
		else if ("de".equals(locale))
			locale = "de_DE";
		else if ("pt".equals(locale))
			locale = "pt_BR";
		else if ("es".equals(locale))
			locale = "es_ES";
		else if ("it".equals(locale))
			locale = "it_IT";
		else if ("zh".equals(locale)){
			if(country.equalsIgnoreCase("CN") || country.equalsIgnoreCase("SG"))
				locale = "zh_CN";
			else
				locale = "zh_TW";
		}else
			locale = "en";
		
		int indexOf = locale.indexOf("_");
		String lang = "en";
		country = "";
		if(indexOf!=-1){
			lang = locale.substring(0,indexOf);
			country = locale.substring(indexOf+1);
		}
		String noscript_text = getNoScriptTextFromResource(lang,country);
		logger.debug("Final locale:"+locale);
		
		session.setAttribute(SessionConstants.SRING_LOCALE, locale);
		InputStream is =null;
		BufferedReader reader = null;
		try{
			
			String filename = "/index.html";
			ServletContext context = filterConfig.getServletContext();
			is = context.getResourceAsStream(filename);
			if (is != null) {
				InputStreamReader isr = new InputStreamReader(is, "utf-8");
				reader = new BufferedReader(isr);
				boolean docTypeChangeFlag = need2ChangeDocType(req);
				String text = "";
				while ((text = reader.readLine()) != null)
				{
					if (docTypeChangeFlag && text.contains("<!doctype html>")) {
						text = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">";
					} else if (text.contains("<head>")) {
						text = appendMetaDataLineForIE(req, text);
					}else if (text.contains("locale=en") )
					{
						text = text.replaceAll("locale=en", "locale="+locale);
					}
					else  if(text.contains("__noscript_html_text__"))
					{
						text = text.replaceAll("__noscript_html_text__", noscript_text);
					}
					else  if(locale.equals("ja_JP"))
					{
						if(text.contains("css/gxt-all.css"))
						{
							text = text.replaceAll("css/gxt-all.css", "css/gxt-all_ja.css");
						}
						else if(text.contains("index.css"))
						{
							text = text.replaceAll("index.css", "index_ja.css");
						}

					}
					 
					if(text.contains("version=D2DVersion")) {
						String noCacheJS = "version=" + System.currentTimeMillis();
						text = text.replace("version=D2DVersion", noCacheJS);
					}
					
					resp.getWriter().write(text);
				}
				resp.setContentType("text/html");
				resp.getWriter().flush();
				resp.getWriter().close();
			}
				
			return;
		}catch(Exception e){
			logger.error("", e);
		}finally{
			try
			{
				if (is != null) {
					is.close();
				}
				if(reader != null) reader.close();
			}catch(Exception eclose){}
		}
		
		/**
		 * to add cache control headers for GWT generated file: MODULE_NAME.nocache.js
		 * make sure that this file does not get cached by the browser nor any proxy servers along the way
		 */
		String requestURI = req.getRequestURI();
		if (requestURI.contains(".nocache.")) {
			Date now = new Date();
			resp.setDateHeader("Date", now.getTime());
			// one day old
			resp.setDateHeader("Expires", now.getTime() - 86400000L);
			resp.setHeader("Pragma", "no-cache");
			resp.setHeader("Cache-control",
					"no-cache, no-store, must-revalidate");
		}
		
		chain.doFilter(request, response);
	}
	
	private boolean need2ChangeDocType(HttpServletRequest req) {
		String userAgent = req.getHeader("User-Agent");
		logger.debug(userAgent);

		if (userAgent.indexOf("MSIE 7.0") > 0 && !userAgent.contains("Trident/4.0"))
			return true;
		else if (userAgent.indexOf("MSIE 6.0") > 0)
			return true;
		return false;
	}
	
	// //see doc http://msdn.microsoft.com/library/ms537503.aspx; prevent
	// browser automatically into compatible IE7 mode
	private String appendMetaDataLineForIE(HttpServletRequest req, String text) {
		String userAgent = req.getHeader("User-Agent");
		if (userAgent.indexOf("MSIE 7.0") > 0) {// browser mode IE7;

			if (userAgent.contains("Trident/4.0")) { // /actually browser
														// version IE8
				text += "\n <meta http-equiv=\"X-UA-Compatible\" content=\"IE=8\" /> ";
			} else if (userAgent.contains("Trident/5.0")) {
				text += "\n <meta http-equiv=\"X-UA-Compatible\" content=\"IE=9\" /> ";
			} else if (userAgent.contains("Trident/6.0")) {
				text += "\n <meta http-equiv=\"X-UA-Compatible\" content=\"IE=10\" /> ";
			} else if (  userAgent.contains("Trident/7.0") ) { //http://msdn.microsoft.com/en-us/library/jj676915(v=vs.85).aspx
				 text += "\n <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" /> ";
			}			
		}else if( userAgent.indexOf("MSIE 8.0")>0 ){ //browser mode IE8;     
			if( userAgent.contains("Trident/4.0") ) { ///actually browser version IE8
				 text += "\n <meta http-equiv=\"X-UA-Compatible\" content=\"IE=8\" /> ";
			}
		}

		return text;
	}
	
	public  String getNoScriptTextFromResource(String language,String country){
		java.util.Locale lo = new java.util.Locale (language,country);
		ResourceBundle bundle = ResourceBundle.getBundle("com.ca.arcflash.common.properties.resources",lo, 
				Control.getNoFallbackControl(Control.FORMAT_DEFAULT));
		String productName = ContextListener.ProductNameD2D;
		if(productName == null || productName.isEmpty()) {
			productName = bundle.getString("ProductNameD2D"); 
		}
		
		return MessageFormatEx.format(bundle.getString("noscript_html_text"), productName);
	}
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

}
