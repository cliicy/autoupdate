package com.ca.arcflash.ui.server.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;

public class RSSFilter implements Filter {
	
	public static boolean showRSS = true;
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		configRSS();
		if (!showRSS){
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//configRSS(filterConfig);
	}

	private void configRSS() {
		try {
			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String showRSSValue = registry.getValue(handle, "ShowRSS");
			registry.closeKey(handle);
			
			if (StringUtil.isEmptyOrNull(showRSSValue))
				showRSS = true;
			else if (showRSSValue.equals("0"))
				showRSS = false;
			else
				showRSS = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
