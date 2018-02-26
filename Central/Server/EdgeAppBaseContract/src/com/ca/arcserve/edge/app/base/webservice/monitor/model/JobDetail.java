package com.ca.arcserve.edge.app.base.webservice.monitor.model;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ArcserveConnectInfo;

public class JobDetail implements Comparable<JobDetail> {
	
	public static enum SourceType {
		NO_TYPE,
		D2D,
		RPS,
		ASBU,
		LINUXD2D
	}	
	
	private SourceType source;
	
	private ArcserveConnectInfo connectInfo;
	
	private String hostName;
	
	private String nodeUUID;
	
	private List<String> nodeUUIDs;
	
	private long jobType;
	
	private long jobId;
	
	private int serverId;
	
	private int nodeId;
	
	private int ScheduleType;
	
	private String dataStoreUUID;
	
	private boolean isForVm = false;

	private List<JobHistory> historysList;
	
	private int priotity = 2; //1-high 2-common 3-low
	
	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}
	
	public SourceType getSource() {
		return source;
	}

	public void setSource(SourceType source) {
		this.source = source;
	}

	public ArcserveConnectInfo getConnectInfo() {
		return connectInfo;
	}

	public void setConnectInfo(ArcserveConnectInfo connectInfo) {
		this.connectInfo = connectInfo;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getNodeUUID() {
		return nodeUUID;
	}

	public void setNodeUUID(String nodeUUID) {
		this.nodeUUID = nodeUUID;
	}

	public List<String> getNodeUUIDs() {
		return nodeUUIDs;
	}

	public void setNodeUUIDs(List<String> nodeUUIDs) {
		this.nodeUUIDs = nodeUUIDs;
	}
	
	public boolean addNodeUUID(String nodeUUID,String agentId,String hostName){
//		if (nodeUUID == null || nodeUUID.equals("") || nodeUUID.trim().equals(""))
//			return false;
		if(nodeUUID == null || nodeUUID.isEmpty())
			nodeUUID = "";
		if(this.nodeUUIDs==null)
			this.nodeUUIDs = new ArrayList<String>();
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(nodeUUID+"|");
		if( (agentId!=null) && (!(agentId.trim().equals("")))){
			sBuilder.append("agentId:"+agentId+"|");
		}
		if( (hostName!=null) && (!(hostName.trim().equals("")))){
			sBuilder.append("hostName:"+hostName+"|");
		}		
		for (String obj: nodeUUIDs) {
			if(nodeUUID.trim().equals(obj)){
				return true;
			}
		}
		this.nodeUUIDs.add(sBuilder.toString());
		return true;
	}
	
	public boolean splitNodeUUIDStr(String nodeUUIDStr){
		if (nodeUUIDStr == null || nodeUUIDStr.equals("") || nodeUUIDStr.trim().equals(""))
			return false;
		int index= 0;
		int endIndex= 0;
		int len = 0;
		if(nodeUUIDStr.indexOf("D2D:", 0)!= -1){
			index = nodeUUIDStr.indexOf("D2D:", 0);
			endIndex = nodeUUIDStr.indexOf("|", index+1);
			len = (new String("D2D:")).length();	
			this.nodeUUID = nodeUUIDStr.substring(index+len, endIndex);
			this.isForVm = false;
		} else if(nodeUUIDStr.indexOf("VM:", 0)!= -1) {
			index = nodeUUIDStr.indexOf("VM:", 0);
			endIndex = nodeUUIDStr.indexOf("|", index+1);
			len = (new String("VM:")).length();
			this.nodeUUID = nodeUUIDStr.substring(index+len, endIndex);
			this.isForVm = true;
		} else{
			if(nodeUUIDStr.indexOf("|")>0){				
				this.nodeUUID = nodeUUIDStr.substring(0, nodeUUIDStr.indexOf("|"));
				this.isForVm = false;
			} else {
				this.nodeUUID = "";
				this.nodeId = 0;
				this.hostName = "";
				this.isForVm = true;
				return true;
			}
		}
		if(nodeUUIDStr.indexOf("agentId:", 0)!= -1){
			index = nodeUUIDStr.indexOf("agentId:", 0);
			endIndex = nodeUUIDStr.indexOf("|", index+1);
			len = (new String("agentId:")).length();
			this.nodeId = Integer.parseInt(nodeUUIDStr.substring(index+len, endIndex));
		}
		if(nodeUUIDStr.indexOf("hostName:", 0)!= -1){
			index = nodeUUIDStr.indexOf("hostName:", 0);
			endIndex = nodeUUIDStr.indexOf("|", index+1);
			len = (new String("hostName:")).length();
			this.hostName = nodeUUIDStr.substring(index+len, endIndex);
		}
		return true;
	}

	public long getJobType() {
		return jobType;
	}

	public void setJobType(long jobType) {
		this.jobType = jobType;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getScheduleType() {
		return ScheduleType;
	}

	public void setScheduleType(int scheduleType) {
		ScheduleType = scheduleType;
	}
	
	public String getDataStoreUUID() {
		return dataStoreUUID;
	}

	public void setDataStoreUUID(String dataStoreUUID) {
		this.dataStoreUUID = dataStoreUUID;
	}

	public boolean isForVm() {
		return isForVm;
	}

	public void setForVm(boolean isForVm) {
		this.isForVm = isForVm;
	}
	
	public List<JobHistory> getHistorysList() {
		return historysList;
	}

	public void setHistorysList(List<JobHistory> historysList) {
		this.historysList = historysList;
	}
	
	public int getPriotity() {
		return priotity;
	}

	public void setPriotity(int priotity) {
		this.priotity = priotity;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobDetail detail = (JobDetail)obj;
		if(serverId!=detail.getServerId())
			return false;
		if(priotity!=detail.getPriotity())
			return false;
		if(nodeId!=detail.getNodeId())
			return false;
		if(source!=detail.getSource())
			return false;
		if(jobType!=detail.getJobType())
			return false;
		if(jobId!=detail.getJobId())
			return false;
		if(ScheduleType!=detail.getScheduleType())
			return false;
		if(isForVm!=detail.isForVm())
			return false;
		if(!StringUtil.isEqual(hostName, detail.getHostName()))
			return false;
		if(!StringUtil.isEqual(nodeUUID, detail.getNodeUUID()))
			return false;
		if(!StringUtil.isEqual(dataStoreUUID, detail.getDataStoreUUID()))
			return false;	
		return true;
	}
	
	@Override
	public String toString() {	
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("JobDetail [ source=" + source);
		sBuilder.append(", serverId=" + serverId);
		sBuilder.append(", priotity=" + priotity);
		sBuilder.append(", nodeId=" + nodeId);
		sBuilder.append(", isForVm=" + isForVm);
		sBuilder.append(", jobType=" + jobType);
		sBuilder.append(", jobId=" + jobId);
		sBuilder.append(", nodeUUID=" + nodeUUID);
		sBuilder.append(", hostName=" + hostName);
		sBuilder.append(", dataStoreUUID="+dataStoreUUID);
		if(getHistorysList()!=null&&getHistorysList().size()>0){
			sBuilder.append(", historyList.size="+getHistorysList().size());
			for (JobHistory history:getHistorysList()) {
				sBuilder.append("\n\t");
				sBuilder.append(" { Id="+history.getHistoryId());
				sBuilder.append(", jobtype="+history.getJobType());
				sBuilder.append(", jobId="+history.getJobId());
				sBuilder.append(", serverId="+history.getServerId());
				sBuilder.append(", AgentId="+history.getAgentId());
				sBuilder.append(", targetRPSID="+history.getTargetRPSId());
				sBuilder.append(" }");
			}
			sBuilder.append("\n ]");
		}else{	
			sBuilder.append(", historyList.size=0 ]");
		}
		return sBuilder.toString();	
	}

	@Override
	public int compareTo(JobDetail obj) {
		if(obj==this)
			return 0;
		if(obj==null)
			return -3;
		return this.priotity - obj.getPriotity();
	}
}
