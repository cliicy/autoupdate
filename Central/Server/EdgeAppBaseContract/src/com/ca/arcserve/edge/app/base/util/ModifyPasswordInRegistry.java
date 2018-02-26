package com.ca.arcserve.edge.app.base.util;

import com.arcserve.edge.util.WindowsServiceUtils;
import com.ca.arcserve.edge.app.base.jni.BaseWSJNI;
import com.ca.arcserve.edge.webservice.jni.model.EdgeAccount;


public class ModifyPasswordInRegistry {
	static{
		System.loadLibrary("ASNative");
	}
	public static void modifyPassword(String username, String password){
		if(!username.equalsIgnoreCase(getRegistryUsername())){ 
			System.out.println("Please input username which inputed at install build, run bat and try again");
			return;
		}
		String domain = "";
		String usernameWithoutDomain = username;
		if(username != null && username.indexOf("\\") != -1){
			domain = username.split("\\\\")[0];
			usernameWithoutDomain = username.split("\\\\")[1];
		}
		int result = BaseWSJNI.validate(usernameWithoutDomain, domain, password);
		if(result == 0){
			BaseWSJNI.saveEdgeAccount(username, password);
			System.out.println("Update password successful, begin to restart UDP service...");
			WindowsServiceUtils.restartUDPService();
			System.out.println("UDP service restarted successful");
		}else{
			System.out.println("Failed to validate credential, update password fail, run bat and try again");
		}
	}
	public static String getRegistryUsername(){
		EdgeAccount account = new EdgeAccount();
		BaseWSJNI.getEdgeAccount(account);
		final String domain = account.getDomain();
		String username = account.getUserName();
		if(!".".equals(domain)){
			username = domain + "\\" + username;
		}
		return username;
	}
	public static void main(String[] args) {
		if(args != null && args.length == 2){
			modifyPassword(args[0], args[1]);
		} else{
			System.out.println("Input arguments have errors!");
		}
	}
}
