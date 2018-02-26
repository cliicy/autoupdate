package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;

import com.ca.arcflash.webservice.data.restore.RecoveryPoint;

public class InstantVM implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final int RECOVERY_SERVER_TYPE_WINDOWS = 1;
	public static final int RECOVERY_SERVER_TYPE_LINUX   = 2;
	
	private String uuid;
	private String name;
	private int nodeId;
	private String nodeUuid;
	private String nodeName;
	private RecoveryPoint recoveryPoint;
	private String vmLocation;
	private int recoveryServerId;
	private String recoveryServer;
	private int recoveryServerType;
	private String description;
	private InstantVmStatus status;
	private InstantVMDetail detail;
	private long timestamp;
	private long jobId;
	private int gatewayId=0;
	
	private boolean startJobFinished = false;
	
	public InstantVM(){
		this.timestamp = System.currentTimeMillis();
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodeUuid() {
		return nodeUuid;
	}
	public void setNodeUuid(String nodeUuid) {
		this.nodeUuid = nodeUuid;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public RecoveryPoint getRecoveryPoint() {
		return recoveryPoint;
	}
	public void setRecoveryPoint(RecoveryPoint recoveryPoint) {
		this.recoveryPoint = recoveryPoint;
	}
	public String getVmLocation() {
		return vmLocation;
	}
	public void setVmLocation(String vmLocation) {
		this.vmLocation = vmLocation;
	}
	public int getRecoveryServerId() {
		return recoveryServerId;
	}
	public void setRecoveryServerId(int recoveryServerId) {
		this.recoveryServerId = recoveryServerId;
	}
	public String getRecoveryServer() {
		return recoveryServer;
	}
	public void setRecoveryServer(String recoveryServer) {
		this.recoveryServer = recoveryServer;
	}
	public int getRecoveryServerType() {
		return recoveryServerType;
	}
	public void setRecoveryServerType(int recoveryServerType) {
		this.recoveryServerType = recoveryServerType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public InstantVmStatus getStatus() {
		return status;
	}
	public void setStatus(InstantVmStatus status) {
		this.status = status;
	}
	public InstantVMDetail getDetail() {
		return detail;
	}
	public void setDetail(InstantVMDetail detail) {
		this.detail = detail;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public long getJobId() {
		return jobId;
	}
	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public boolean isStartJobFinished() {
		return startJobFinished;
	}

	public void setStartJobFinished(boolean startJobFinished) {
		this.startJobFinished = startJobFinished;
	}

	public int getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(int gatewayId) {
		this.gatewayId = gatewayId;
	}
	
}
