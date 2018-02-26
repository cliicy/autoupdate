package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ProvisionPoint extends BaseModelData {
	private static final long serialVersionUID = -4194925011989728883L;
	
	public Date getTime() {
		return (Date) get("time");
	}
	public void setTime(Date time) {
		set("time",time);
	}
	public Boolean isVMAssure() {
		return (Boolean) get("vmAssure");
	}
	public void setVMAssure(Boolean vmAssure) {
		set("vmAssure",vmAssure);
	}
	public String getName() {
		return (String) get("name");
	}
	public void setName(String name) {
		set("name",name);
	}
}
