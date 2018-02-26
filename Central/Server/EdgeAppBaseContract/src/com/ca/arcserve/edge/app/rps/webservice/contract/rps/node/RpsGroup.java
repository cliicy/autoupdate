package com.ca.arcserve.edge.app.rps.webservice.contract.rps.node;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class RpsGroup implements Serializable, BeanModelTag{
	private static final long serialVersionUID = 1787253511358778367L;
	
	public static final int UNGROUP			=	0;
	public static final int ALLGROUP		=	-1;
	public static final int Failed 			= 	-100;
	
	private int group_id;
	private String group_name;
	private String group_description;
	public int getGroup_id() {
		return group_id;
	}
	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	public String getGroup_description() {
		return group_description;
	}
	public void setGroup_description(String group_description) {
		this.group_description = group_description;
	}
}
