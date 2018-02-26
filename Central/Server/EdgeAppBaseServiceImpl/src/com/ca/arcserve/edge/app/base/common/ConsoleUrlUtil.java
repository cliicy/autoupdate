package com.ca.arcserve.edge.app.base.common;

import org.apache.log4j.Logger;

public class ConsoleUrlUtil {
	private static final Logger logger = Logger.getLogger(ConsoleUrlUtil.class);
	
	public static String getConsoleHostName(String consoleURL) {
		if (consoleURL == null||consoleURL.isEmpty()) {
			logger.error("[ConsoleUrlUtil]:getConsoleHostName(), ConsoleUrl is null");
			return "";
		}				
		String url = consoleURL.replace("http://", "").replace("https://", "").replace("HTTP://", "").replace("HTTPS://", "");
		if(url.contains(":")){
			url = url.substring(0,url.indexOf(":"));
		} else {
			logger.error("[ConsoleUrlUtil]:getConsoleHostName(), edgeInfo has error value ="+consoleURL);
			if(url.contains("/")){
				url = url.substring(0,url.indexOf("/"));
			} else 
				url = "";
		}
		return url;		
	}
}
