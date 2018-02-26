package com.ca.arcserve.edge.app.base.webservice.contract.node;

public class NodeDetail extends Node {
	private static final long serialVersionUID = -7132658728390892935L;
	private D2DConnectInfo d2dConnectInfo;
	private ArcserveConnectInfo arcserveConnectInfo;
	
	public D2DConnectInfo getD2dConnectInfo() {
		return d2dConnectInfo;
	}
	public void setD2dConnectInfo(D2DConnectInfo d2dConnectInfo) {
		this.d2dConnectInfo = d2dConnectInfo;
	}
	public ArcserveConnectInfo getArcserveConnectInfo() {
		return arcserveConnectInfo;
	}
	public void setArcserveConnectInfo(ArcserveConnectInfo arcserveConnectInfo) {
		this.arcserveConnectInfo = arcserveConnectInfo;
	}

}
