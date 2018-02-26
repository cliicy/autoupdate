package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VDSInfoModel extends BaseModelData{
	private static final long serialVersionUID = -7722996135503447005L;
	private String tag_vDSSwitchName = "vDSSwitchName";
	private String tag_vDSSwitchUUID = "vDSSwitchUUID";
	private List<vDSPortGroupModel> portGroups;
	
	public String getvDSSwitchName() {
		return get(tag_vDSSwitchName);
	}
	
	public void setvDSSwitchName(String vDSSwitchName) {
		set(tag_vDSSwitchName, vDSSwitchName);
	}
	
	public List<vDSPortGroupModel> getPortGroups() {
		return portGroups;
	}
	
	public void setPortGroups(List<vDSPortGroupModel> portGroups) {
		this.portGroups = portGroups;
	}

	public String getvDSSwitchUUID() {
		return get(tag_vDSSwitchUUID);
	}
	
	public void setvDSSwitchUUID(String vDSSwitchUUID) {
		set(tag_vDSSwitchUUID, vDSSwitchUUID);
	}
}
