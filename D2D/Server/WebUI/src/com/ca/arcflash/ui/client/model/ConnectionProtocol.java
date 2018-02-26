package com.ca.arcflash.ui.client.model;

public enum ConnectionProtocol {
	HTTP, HTTPS;

	@Override
	public String toString() {
		if (this == HTTP)
			return "http";
		else if (this  == HTTPS)
			return "https";
		else 
			return "";
	}
	
	static public ConnectionProtocol string2Protocol(String str){
		if(str.compareToIgnoreCase("http")==0)
		{
			return HTTP;
		}
		else {
			return HTTPS;
		}
	}
	
	
}
