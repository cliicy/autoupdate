package com.ca.arcflash.ui.client.model.rps;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RpsHostModel extends BaseModelData{
	public void setHostName(String hostName){
		set("hostName", hostName);
	}
	public String getHostName(){
		return get("hostName");
	}
	public void setUserName(String userName){
		set("userName", userName);
	}
	public String getUserName(){
		return get("userName");
	}
	public void setPassword(String password){
		set("password", password);
	}
	public String getPassword(){
		return get("password");
	}
	public void setPort(int port){
		set("port", port);
	}
	public Integer getPort(){
		return get("port",8014);
	}
	public void setIsHttpProtocol(Boolean isHttpProtocol){
		set("isHttpProtocol", isHttpProtocol);
	}
	public Boolean getIsHttpProtocol(){
		return get("isHttpProtocol",true);
	}
	public void setUUID(String UUID){
		set("UUID",UUID);
	}
	public String getUUID(){
		return get("UUID");
	}
}