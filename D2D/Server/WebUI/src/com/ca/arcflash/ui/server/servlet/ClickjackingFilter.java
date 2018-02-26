package com.ca.arcflash.ui.server.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ClickjackingFilter implements Filter 
{

    private String mode = "DENY";
    
    private boolean isEnableCheck = true;
    	
    /**
     * Add X-FRAME-OPTIONS response header to tell IE8 (and any other browsers who
     * decide to implement) not to display this content in a frame. For details, please
     * refer to http://blogs.msdn.com/sdl/archive/2009/02/05/clickjacking-defense-in-ie8.aspx.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	if(isEnableCheck) {
    		HttpServletResponse res = (HttpServletResponse)response;
    		res.addHeader("X-FRAME-OPTIONS", mode );			
    	}
        chain.doFilter(request, response);
    }
    
    public void destroy() {
    }
    
    public void init(FilterConfig filterConfig) {
        String pconfigMode = filterConfig.getInitParameter("mode");
        String penableCheck = filterConfig.getInitParameter("enableCheck");
        
        if ( pconfigMode != null ) {
            mode = pconfigMode;
        }
        
        if(penableCheck != null && !"1".equals(penableCheck.trim())){
        	isEnableCheck = false;
        }
    }
    
}
