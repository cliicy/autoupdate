package com.ca.arcserve.edge.app.rps.webservice.contract.rps.node;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class RpsNodeStatisticsModel implements Serializable, BeanModelTag {

	private static final long serialVersionUID = -4254858482738336275L;
	
	private int count;
	
	private int type;
	
	private String name;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
