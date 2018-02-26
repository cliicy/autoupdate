package com.ca.arcserve.edge.app.base.appdaos;

import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.Task;

public class EdgeDaoTask extends Task{
	private static final long serialVersionUID = 1L;
	
	private String detailXml;

	public String getDetailXml() {
		return detailXml;
	}

	public void setDetailXml(String detailXml) {
		this.detailXml = detailXml;
	}

}
