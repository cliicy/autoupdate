package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class TrustHostModel extends BaseModelData {
	private static final long serialVersionUID = -7891108958936549107L;

	public final static String tag_HostName = "hostName";
	private final static String tag_Uuid = "uuid";
	private final static String tag_User = "user";
	private final static String tag_Password = "password";
	private final static String tag_Port = "port";
	private final static String tag_Type = "type";
	private final static String tag_Selected = "Selected";
	private final static String tag_Version = "D2DVersion";
	private final static String tag_Protocol = "Protocol";

	public String getHostName() {
		return get(tag_HostName);
	}

	public void setHostName(String hostName) {
		set(tag_HostName, hostName);
	}

	public String getUuid() {
		return get(tag_Uuid);
	}

	public void setUuid(String uuid) {
		set(tag_Uuid, uuid);
	}

	public String getUser() {
		return get(tag_User);
	}

	public void setUser(String user) {
		set(tag_User, user);
	}

	public String getPassword() {
		return get(tag_Password);
	}

	public void setPassword(String password) {
		set(tag_Password, password);
	}

	public Integer getPort() {
		return get(tag_Port);
	}

	public void setPort(Integer port) {
		set(tag_Port, port);
	}

	public Integer getType() {
		return get(tag_Type);
	}

	public void setType(Integer type) {
		set(tag_Type, type);
	}
	
	public Boolean isSelected() {
		return get(tag_Selected);
	}

	public void setSelected(Boolean selected) {
		set(tag_Selected, selected);
	}

	public String getProtocol() {
		return get(tag_Protocol);
	}
	public void setProtocol(String protocol) {
		set(tag_Protocol, protocol);
	}
	
	public Integer getD2DVersion() {
		return get(tag_Version);
	}
	
	public void setD2DVersion(Integer version) {
		set(tag_Version, version);
	}
	
	public Integer getProductType(){
		return get("productType");
	}
	
	public void setProductType(Integer productType) {
		set("productType", productType);
	}
}
