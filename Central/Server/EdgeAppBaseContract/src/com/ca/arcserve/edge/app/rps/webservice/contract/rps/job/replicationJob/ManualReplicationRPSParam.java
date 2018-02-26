package com.ca.arcserve.edge.app.rps.webservice.contract.rps.job.replicationJob;


import java.util.List;

import com.ca.arcflash.rps.webservice.replication.ManualReplicationItem;

public class ManualReplicationRPSParam implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<String> nodeUuidList;
	private int srcRpsHostId;
	private String srcRpsHostName;
	private String srcDataStoreUUID;
	private String srcDataStoreName;
	private int dstRpsHostId;
	private String dstRpsHostName;
	private String dstDataStoreUUID;
	private String dstDataStoreName;	
	private ManualReplicationItem replicationItem;
	private boolean isMspReplicate = false;
		
	public List<String> getNodeUuidList() {
		return nodeUuidList;
	}
	public void setNodeUuidList(List<String> nodeUuidList) {
		this.nodeUuidList = nodeUuidList;
	}
	public int getSrcRpsHostId() {
		return srcRpsHostId;
	}
	public void setSrcRpsHostId(int srcRpsHostId) {
		this.srcRpsHostId = srcRpsHostId;
	}
	public String getSrcRpsHostName() {
		return srcRpsHostName;
	}
	public void setSrcRpsHostName(String srcRpsHostName) {
		this.srcRpsHostName = srcRpsHostName;
	}
	public String getSrcDataStoreUUID() {
		return srcDataStoreUUID;
	}
	public void setSrcDataStoreUUID(String srcDataStoreUUID) {
		this.srcDataStoreUUID = srcDataStoreUUID;
	}
	public String getSrcDataStoreName() {
		return srcDataStoreName;
	}
	public void setSrcDataStoreName(String srcDataStoreName) {
		this.srcDataStoreName = srcDataStoreName;
	}
	public int getDstRpsHostId() {
		return dstRpsHostId;
	}
	public void setDstRpsHostId(int dstRpsHostId) {
		this.dstRpsHostId = dstRpsHostId;
	}
	public String getDstRpsHostName() {
		return dstRpsHostName;
	}
	public void setDstRpsHostName(String dstRpsHostName) {
		this.dstRpsHostName = dstRpsHostName;
	}
	public String getDstDataStoreUUID() {
		return dstDataStoreUUID;
	}
	public void setDstDataStoreUUID(String dstDataStoreUUID) {
		this.dstDataStoreUUID = dstDataStoreUUID;
	}
	public String getDstDataStoreName() {
		return dstDataStoreName;
	}
	public void setDstDataStoreName(String dstDataStoreName) {
		this.dstDataStoreName = dstDataStoreName;
	}
	public ManualReplicationItem getReplicationItem() {
		return replicationItem;
	}
	public void setReplicationItem(ManualReplicationItem replicationItem) {
		this.replicationItem = replicationItem;
	}
	public boolean isMspReplicate() {
		return isMspReplicate;
	}
	public void setMspReplicate(boolean isMspReplicate) {
		this.isMspReplicate = isMspReplicate;
	}
	@Override
	public String toString(){
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("ManualReplicationRPSParam[");
		sBuffer.append(" srcRpsHostId="+srcRpsHostId + ", srcRpsHostName="+srcRpsHostName);
		sBuffer.append(", srcDataStoreUUID="+srcDataStoreUUID + ", srcDataStoreName="+srcDataStoreName);
		sBuffer.append(", dstRpsHostId="+dstRpsHostId + ", dstRpsHostName="+dstRpsHostName);
		sBuffer.append(", dstDataStoreUUID="+dstDataStoreUUID + ", dstDataStoreName="+dstDataStoreName);
		sBuffer.append(", isMspReplicate="+isMspReplicate );
		if(replicationItem==null)
			sBuffer.append( ", replicationItem=null");
		else {
			sBuffer.append( ", replicationItem( ");
			sBuffer.append( " nodeUUID="+replicationItem.getNodeUUID());
			sBuffer.append( ", policyUUID="+replicationItem.getPolicyUUID());
			sBuffer.append(")");
		}	
		sBuffer.append("]");
		return sBuffer.toString();
	}
}