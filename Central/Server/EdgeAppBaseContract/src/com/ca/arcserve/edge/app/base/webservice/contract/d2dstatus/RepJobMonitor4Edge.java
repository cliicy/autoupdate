package com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus;

import com.ca.arcflash.jobscript.replication.RepJobMonitor;

public class RepJobMonitor4Edge extends RepJobMonitor {

	private static final long serialVersionUID = -7517873776205490425L;
	private D2DStatusInfo info;

	public D2DStatusInfo getInfo() {
		return info;
	}

	public void setInfo(D2DStatusInfo info) {
		this.info = info;
	}
}
