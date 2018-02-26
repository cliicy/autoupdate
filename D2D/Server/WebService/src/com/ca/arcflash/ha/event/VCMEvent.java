package com.ca.arcflash.ha.event;

import java.io.Serializable;
import java.util.Date;

public class VCMEvent implements Serializable{

	private static final long serialVersionUID = -2486931936794113637L;

	private String taskGuid;
	private String taskName;
	private String taskType; //OfflineCopy,Failover,V2P
	private Date startTime;
	private Date endTime;
	private String srcHostName;
	private String srcVMName;
	private String srcVirtualCenterName;
	private String srcVMUUID;
	private String srcVMType;
	private String destHostName;
	private String destVMName;
	private String destVirtualCenterName;
	private String destVMUUID;
	private String destVMType; //Physical,VMware,VirtualCenter,HyperV
	private String status;
	private String statusComment;
	private String vcmMonitorHost;
	private boolean isProxy; 
	private String afGuid;
	private long jobID;
	
	
	public String getTaskGuid() {
		return taskGuid;
	}
	public void setTaskGuid(String taskGuid) {
		this.taskGuid = taskGuid;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getSrcHostName() {
		return srcHostName;
	}
	public void setSrcHostName(String srcHostName) {
		this.srcHostName = srcHostName;
	}
	public String getSrcVMName() {
		return srcVMName;
	}
	public void setSrcVMName(String srcVMName) {
		this.srcVMName = srcVMName;
	}
	public String getSrcVirtualCenterName() {
		return srcVirtualCenterName;
	}
	public void setSrcVirtualCenterName(String srcVirtualCenterName) {
		this.srcVirtualCenterName = srcVirtualCenterName;
	}
	public String getSrcVMUUID() {
		return srcVMUUID;
	}
	public void setSrcVMUUID(String srcVMUUID) {
		this.srcVMUUID = srcVMUUID;
	}
	public String getSrcVMType() {
		return srcVMType;
	}
	public void setSrcVMType(String srcVMType) {
		this.srcVMType = srcVMType;
	}
	public String getDestHostName() {
		return destHostName;
	}
	public void setDestHostName(String destHostName) {
		this.destHostName = destHostName;
	}
	public String getDestVMName() {
		return destVMName;
	}
	public void setDestVMName(String destVMName) {
		this.destVMName = destVMName;
	}
	public String getDestVirtualCenterName() {
		return destVirtualCenterName;
	}
	public void setDestVirtualCenterName(String destVirtualCenterName) {
		this.destVirtualCenterName = destVirtualCenterName;
	}
	public String getDestVMUUID() {
		return destVMUUID;
	}
	public void setDestVMUUID(String destVMUUID) {
		this.destVMUUID = destVMUUID;
	}
	public String getDestVMType() {
		return destVMType;
	}
	public void setDestVMType(String destVMType) {
		this.destVMType = destVMType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusComment() {
		return statusComment;
	}
	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}
	
	public String getVcmMonitorHost() {
		return vcmMonitorHost;
	}
	public void setVcmMonitorHost(String vcmMonitorHost) {
		this.vcmMonitorHost = vcmMonitorHost;
	}
	
	public boolean isProxy() {
		return isProxy;
	}
	public void setProxy(boolean isProxy) {
		this.isProxy = isProxy;
	}
	
	public String getAfGuid() {
		return afGuid;
	}
	public void setAfGuid(String afGuid) {
		this.afGuid = afGuid;
	}
	public long getJobID() {
		return jobID;
	}
	public void setJobID(long jobID) {
		this.jobID = jobID;
	}
	@Override
	public String toString() {
		
		StringBuilder attributes = new StringBuilder();
		attributes.append(taskGuid + "\t").append(taskName + "\t")
				  .append(taskType + "\t").append(startTime.getTime() + "\t")
				  .append(endTime.getTime() + "\t").append(srcHostName + "\t")
				  .append(srcVMName + "\t").append(srcVirtualCenterName + "\t")
				  .append(srcVMUUID + "\t").append(srcVMType + "\t")
				  .append(destHostName +"\t").append(destVMName + "\t")
				  .append(destVirtualCenterName + "\t").append(destVMUUID + "\t")
				  .append(destVMType + "\t").append(status + "\t")
				  .append(statusComment + "\t");
		
		return attributes.toString();
	}
	
}
