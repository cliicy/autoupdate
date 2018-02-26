package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class VirtualCenterNodeModel extends BaseTreeModel {
	
	public VirtualCenterNodeModel(){
		
	}

	public VirtualCenterNodeModel(String name) {
		set("name", name);
	}

	public VirtualCenterNodeModel(String name, VirtualCenterNodeModel[] children) {
		this(name);
		for (int i = 0; i < children.length; i++) {
			add(children[i]);
		}
	}

	public String getName() {
		return (String) get("name");
	}

	public String getIP() {
		return (String) get("ip");
	}

	public String getUUID() {
		return (String) get("UUID");
	}
	
	public Integer getType(){
		return (Integer)get("type");
	}

}
