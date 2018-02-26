package com.ca.arcflash.ui.client.common;


public class InternLoginUtils {
	
	private static native void loginD2D(String title, String action, String username, String password, String promptMessage)/*-{
			
		var newWnd = $wnd.open("", "", "");
		
		newWnd.document.write("<html><head><title>" + title + "</title></head><body><center><br>"+promptMessage+"</br></center></body></html>");
		newWnd.document.close();
		var form = newWnd.document.createElement("form");
		form.style.display = "none";
	    form.id = "loginD2D_Form";
	    form.method = "post";
	    form.target = "_self";
	    newWnd.document.body.appendChild(form);
	    form.innerHTML ="<input name='username' id='username' type='text' value='" + username + "'/>"+
	    				"<input name='password' id='password' type='password' value='" + password + "'/>";
		form.action = action;
		form.submit();		
	}-*/;
	
	public static void loginD2DHost(String host,String username, String password, int port, String protocol){
		if(!protocol.endsWith(":"))
			protocol = protocol + ":";
		
		String action = protocol + "//"+host+":"+port+"/internalLogin";
		String title = "";
		String message = "";
		loginD2D(title, action, username, password, message);
	}
}
