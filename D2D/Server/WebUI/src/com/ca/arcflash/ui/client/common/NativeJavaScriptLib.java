package com.ca.arcflash.ui.client.common;

public class NativeJavaScriptLib {
	public static native void redirectToUrl(String url)/*-{
		$wnd.location = url;
	}-*/; 
}
