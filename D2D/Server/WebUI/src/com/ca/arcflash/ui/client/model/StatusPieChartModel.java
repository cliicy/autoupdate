package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class StatusPieChartModel extends BaseModelData {
	private static final long serialVersionUID = -7415660429947991002L;
	
	public StatusPieChartModel(Integer count, String name){
		setCount(count);
		setName(name);
	}
	
	public Integer getCount() {
		return (Integer)get("count");
	}
	public void setCount(Integer count) {
		set("count",count);
	}
	public String getName() {
		return get("name");
	}
	public void setName(String name) {
		set("name",name);
	}
}
