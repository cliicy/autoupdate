package com.ca.arcserve.edge.app.rps.webservice.contract.rps.node;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;

public class RPSDataStoreNode extends RpsNode {

	private static final long serialVersionUID = 1L;

	private DataStoreStatusListElem datastoreInfo;
	
	public RPSDataStoreNode(RpsNode rpsNode , DataStoreStatusListElem datastoreInfo){
		this.setUsername(rpsNode.getUsername());
		this.setPassword(rpsNode.getPassword());
		this.setPort(rpsNode.getPort());
		this.setProtocol(rpsNode.getProtocol());
		this.setNode_id(rpsNode.getNode_id());
		this.setNode_name(rpsNode.getNode_name());
		this.setNode_type(rpsNode.getNode_type());
		this.setNode_description(rpsNode.getNode_description());
		this.setDataStoreModels(rpsNode.getDataStoreModels());
		this.setDatastoreInfo(datastoreInfo);
	}
	
	public DataStoreStatusListElem getDatastoreInfo() {
		return datastoreInfo;
	}

	public void setDatastoreInfo(DataStoreStatusListElem datastoreInfo) {
		this.datastoreInfo = datastoreInfo;
	}
	
}
