package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VSphereProxyModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1592156275935720108L;
	
	public void setVSphereProxyName(String vSphereProxyName){
		set("vSphereProxyName",vSphereProxyName);
	}
	
	public String getVSphereProxyName(){
		return get("vSphereProxyName");
	}
	
	public void setVSphereProxyUsername(String vSphereProxyUsername){
		set("vSphereProxyUsername",vSphereProxyUsername);
	}
	
	public String getVSphereProxyUsername(){
		return get("vSphereProxyUsername");
	}
	
	public void setVSphereProxyPassword(String vSphereProxyPassword){
		set("vSphereProxyPassword",vSphereProxyPassword);
	}
	
	public String getVSphereProxyPassword(){
		return get("vSphereProxyPassword");
	}
	
	public void setVSphereProxyProtocol(String vSphereProxyProtocol){
		set("vSphereProxyProtocol",vSphereProxyProtocol);
	}
	
	public String getVSphereProxyProtocol(){
		return get("vSphereProxyProtocol");
	}
	
	public void setVSphereProxyPort(Integer vSphereProxyPort){
		set("vSphereProxyPort",vSphereProxyPort);
	}
	
	public Integer getvSphereProxyPort(){
		return get("vSphereProxyPort");
	}
	
}
