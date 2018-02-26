package com.ca.arcserve.edge.app.base.webservice.contract.jobhistory;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.extjs.gxt.ui.client.data.BeanModelTag;

/**
 * @author lvwch01
 *
 */
public class JobHistory implements Serializable,BeanModelTag {

	private static final long serialVersionUID = -3695293040171146557L;
	
	private long jobId;
	private long jobType;
	private JobStatus jobStatus;
	private Date jobUTCStartDate;
	private Date jobLocalStartDate;
	@Deprecated
	private String jobDisplayName;
	private String version;
	private int productType;
	private long jobMethod;
	private Date jobUTCEndDate;
	private Date jobLocalEndDate;
	private String serverId;
	private String agentId;
	private String sourceRPSId;
	private String targetRPSId;
	private String sourceDataStoreUUID;
	private String targetDataStoreUUID;
	
	private String nodeName;
	private String planName;
	private int planId;
	private String planUuid;
	private int nodeId;
	private String serverName;
	private boolean emptyNodeName = false;
	private String jobUUID;
	private String agentNodeName;
	private String serverNodeName;
	private String agentUUID;
	private Double progress; // use only for DashBoard ProgressBar to show
	private long historyId;		// use to store DB's Id
	
    public String getAgentUUID() {
		return agentUUID;
	}

	public void setAgentUUID(String agentUUID) {
		this.agentUUID = agentUUID;
	}

	/**
	 * Get UUID of the job.
	 * 
	 * @return
	 */
	public String getJobUUID() {
		return jobUUID;
	}
	
	/**
	 * Set UUID of the job.
	 * 
	 * @param jobUUID
	 */
	public void setJobUUID(String jobUUID) {
		this.jobUUID = jobUUID;
	}
	
	/**
	 * Get name of the node.
	 * 
	 * @return
	 */
	public String getAgentNodeName() {
		return agentNodeName;
	}
	
	/**
	 * Set name of the node.
	 * 
	 * @param agentNodeName
	 */
	public void setAgentNodeName(String agentNodeName) {
		this.agentNodeName = agentNodeName;
	}
	
	/**
	 * Not used.
	 * 
	 * @return
	 */
	public String getServerNodeName() {
		return serverNodeName;
	}
	
	/**
	 * Not used.
	 * 
	 * @param serverNodeName
	 */
	public void setServerNodeName(String serverNodeName) {
		this.serverNodeName = serverNodeName;
	}
	
	public String getServerName() {
		if (serverName == null || serverName.length() == 0) {
			return serverNodeName;
		}
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	/**
	 * Get ID of the node on which the job is running.
	 * 
	 * @return
	 */
	public int getNodeId() {
		return nodeId;
	}

	/**
	 * Set ID of the node on which the job is running.
	 * 
	 * @param nodeId
	 */
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	
	/**
	 * Get the ID of the plan according to which the job is running.
	 * 
	 * @return
	 */
	public int getPlanId() {
		return planId;
	}

	/**
	 * Set the ID of the plan according to which the job is running.
	 * 
	 * @param planId
	 */
	public void setPlanId(int planId) {
		this.planId = planId;
	}

	/**
	 * Get the UUID of the plan according to which the job is running.
	 * 
	 * @return
	 */
	public String getPlanUuid() {
		return planUuid;
	}

	/**
	 * Set the UUID of the plan according to which the job is running.
	 * 
	 * @param planUuid
	 */
	public void setPlanUuid(String planUuid) {
		this.planUuid = planUuid;
	}

	/**
	 * Get name of the node on which the job is running.
	 * 
	 * @return
	 */
	public String getNodeName() {
		if (nodeName == null || nodeName.length() == 0) {
			return agentNodeName;
		}
		return nodeName;
	}

	/**
	 * Set name of the node on which the job is running.
	 * 
	 * @param nodeName
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * Get the name of the plan according to which the job is running.
	 * 
	 * @return
	 */
	public String getPlanName() {
		return planName;
	}

	/**
	 * Set the name of the plan according to which the job is running.
	 * 
	 * @param planName
	 */
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	
	/**
	 * Get ID of the job.
	 * 
	 * @return
	 */
	public long getJobId() {
		return jobId;
	}

	/**
	 * Set ID of the job.
	 * 
	 * @param jobId
	 */
	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	/**
	 * Get version of the node on which the job is running.
	 * 
	 * @return
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Set version of the node on which the job is running.
	 * 
	 * @param version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	/**
	 * Get job method.
	 * <p>
	 * <ul>
	 * <li>-1: Unknown
	 * <li>0: Full
	 * <li>1: Incremental
	 * <li>2: Resync
	 * </ul>
	 * 
	 * @return
	 */
	public long getJobMethod() {
		return jobMethod;
	}

	/**
	 * Set job method.
	 * <p>
	 * <ul>
	 * <li>-1: Unknown
	 * <li>0: Full
	 * <li>1: Incremental
	 * <li>2: Resync
	 * </ul>
	 * 
	 * @param jobMethod
	 */
	public void setJobMethod(long jobMethod) {
		this.jobMethod = jobMethod;
	}

	/**
	 * Get job end time in UTC time.
	 * 
	 * @return
	 */
	public Date getJobUTCEndDate() {
		return jobUTCEndDate;
	}

	/**
	 * Set job end time in UTC time.
	 * 
	 * @param jobUTCEndDate
	 */
	public void setJobUTCEndDate(Date jobUTCEndDate) {
		this.jobUTCEndDate = jobUTCEndDate;
	}

	/**
	 * Get job end time in local time.
	 * 
	 * @return
	 */
	public Date getJobLocalEndDate() {
		return jobLocalEndDate;
	}

	/**
	 * Set job end time in local time.
	 * 
	 * @param jobLocalEndDate
	 */
	public void setJobLocalEndDate(Date jobLocalEndDate) {
		this.jobLocalEndDate = jobLocalEndDate;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	/**
	 * Get the ID of the source RPS server.
	 * 
	 * @return
	 */
	public String getSourceRPSId() {
		return sourceRPSId;
	}

	/**
	 * Set the ID of the source RPS server.
	 * 
	 * @param sourceRPSId
	 */
	public void setSourceRPSId(String sourceRPSId) {
		this.sourceRPSId = sourceRPSId;
	}

	/**
	 * Get the ID of the target RPS server.
	 * 
	 * @return
	 */
	public String getTargetRPSId() {
		return targetRPSId;
	}

	/**
	 * Set the ID of the target RPS server.
	 * 
	 * @param targetRPSId
	 */
	public void setTargetRPSId(String targetRPSId) {
		this.targetRPSId = targetRPSId;
	}
	
	/**
	 * Get type of the job. See {@link com.ca.arcflash.webservice.constants.JobType}
	 * for available values.
	 * 
	 * @return
	 */
	public long getJobType() {
		return jobType;
	}

	/**
	 * Set type of the job. See {@link com.ca.arcflash.webservice.constants.JobType}
	 * for available values.
	 * 
	 * @param jobType
	 */
	public void setJobType(long jobType) {
		this.jobType = jobType;
	}
	
	/**
	 * Get status of the job.
	 * 
	 * @return
	 */
	public JobStatus getJobStatus() {
		return jobStatus;
	}

	/**
	 * Set status of the job.
	 * 
	 * @param jobStatus
	 */
	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}
	
	/**
	 * Get start time of the job in UTC time.
	 * 
	 * @return
	 */
	public Date getJobUTCStartDate() {
		return jobUTCStartDate;
	}

	/**
	 * Set start time of the job in UTC time.
	 * 
	 * @param jobUTCStartDate
	 */
	public void setJobUTCStartDate(Date jobUTCStartDate) {
		this.jobUTCStartDate = jobUTCStartDate;
	}
	
	/**
	 * Get start time of the job in local time.
	 * 
	 * @return
	 */
	public Date getJobLocalStartDate() {
		return jobLocalStartDate;
	}

	/**
	 * Set start time of the job in local time.
	 * 
	 * @param jobLocalStartDate
	 */
	public void setJobLocalStartDate(Date jobLocalStartDate) {
		this.jobLocalStartDate = jobLocalStartDate;
	}
	@Deprecated
	public String getJobDisplayName() {
		return jobDisplayName;
	}
	@Deprecated
	public void setJobDisplayName(String jobDisplayName) {
		this.jobDisplayName = jobDisplayName;
	}

	/**
	 * Get UUID of the source data store.
	 * 
	 * @return
	 */
	public String getSourceDataStoreUUID() {
		return sourceDataStoreUUID;
	}

	/**
	 * Set UUID of the source data store.
	 * 
	 * @param sourceDataStoreUUID
	 */
	public void setSourceDataStoreUUID(String sourceDataStoreUUID) {
		this.sourceDataStoreUUID = sourceDataStoreUUID;
	}

	/**
	 * Get UUID of the target data store.
	 * 
	 * @return
	 */
	public String getTargetDataStoreUUID() {
		return targetDataStoreUUID;
	}

	/**
	 * Set UUID of the target data store.
	 * 
	 * @param targetDataStoreUUID
	 */
	public void setTargetDataStoreUUID(String targetDataStoreUUID) {
		this.targetDataStoreUUID = targetDataStoreUUID;
	}

	public int getAgentOrserverId() {
		if (this.productType == 1) {
			return Integer.valueOf(agentId);
		} else {
			if (targetRPSId == null) {
				targetRPSId = "0";
			}
			return Integer.valueOf(targetRPSId);
		}
	}
	
	/**
	 * Get the name of the type of the node on which the job is running.
	 * 
	 * @return
	 */
	public String getNodeTypeName() {
		if (this.productType == 1) {
			return "D2D";
		} else {			
			return "RPS";
		}
	}

	/**
	 * Whether the name of the node is empty.
	 * 
	 * @return
	 */
	public boolean isEmptyNodeName() {
		return emptyNodeName;
	}

	/**
	 * Set whether the name of the node is empty.
	 * 
	 * @param emptyNodeName
	 */
	public void setEmptyNodeName(boolean emptyNodeName) {
		this.emptyNodeName = emptyNodeName;
	}
	
	public Double getProgress() {
		return progress;
	}

	public void setProgress(Double progress) {
		this.progress = progress;
	}

	public long getHistoryId() {
		return historyId;
	}

	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobHistory other = (JobHistory) obj;
		
		if(historyId != other.getHistoryId())
			return false;
		if(jobId != other.getJobId())
			return false;
		if(jobType != other.getJobType())
			return false;
		if(!Utils.simpleObjectEquals(jobStatus, other.getJobStatus()))
			return false;
		if(!Utils.simpleObjectEquals(jobUTCStartDate, other.getJobUTCStartDate()))
			return false;
		if(!Utils.simpleObjectEquals(jobLocalStartDate, other.getJobLocalStartDate()))
			return false;
		if(!StringUtil.isEqual(jobDisplayName, other.getJobDisplayName()))
			return false;
		if(!StringUtil.isEqual(version, other.getVersion()))
			return false;
		if(productType != other.getProductType())
			return false;
		if(jobMethod != other.getJobMethod())
			return false;
		if(!Utils.simpleObjectEquals(jobUTCEndDate, other.getJobUTCEndDate()))
			return false;
		if(!Utils.simpleObjectEquals(jobLocalEndDate, other.getJobLocalEndDate()))
			return false;
		if(!StringUtil.isEqual(serverId, other.getServerId()))
			return false;
		if(!StringUtil.isEqual(agentId, other.getAgentId()))
			return false;
		if(!StringUtil.isEqual(sourceRPSId, other.getSourceRPSId()))
			return false;
		if(!StringUtil.isEqual(targetRPSId, other.getTargetRPSId()))
			return false;
		if(!StringUtil.isEqual(sourceDataStoreUUID, other.getSourceDataStoreUUID()))
			return false;
		if(!StringUtil.isEqual(targetDataStoreUUID, other.getTargetDataStoreUUID()))
			return false;
		if(!StringUtil.isEqual(nodeName, other.getNodeName()))
			return false;
		if(!StringUtil.isEqual(planName, other.getPlanName()))
			return false;
		if(planId != other.getPlanId())
			return false;
		if(!StringUtil.isEqual(planUuid, other.getPlanUuid()))
			return false;
		if(nodeId != other.getNodeId())
			return false;
		if(!StringUtil.isEqual(this.getServerName(), other.getServerName()))
			return false;
		if(emptyNodeName != other.isEmptyNodeName())
			return false;
		if(!StringUtil.isEqual(jobUUID, other.getJobUUID()))
			return false;
		if(!StringUtil.isEqual(agentNodeName, other.getAgentNodeName()))
			return false;
		if(!StringUtil.isEqual(serverNodeName, other.getServerNodeName()))
			return false;
		if(!StringUtil.isEqual(agentUUID, other.getAgentUUID()))
			return false;
		if(progress!=other.getProgress())
			return false;
		return true;
	}	

	public static JobHistory clone(JobHistory object) {
		JobHistory history = new JobHistory();
		history.setHistoryId(object.getHistoryId());
		history.setJobId(object.getJobId());
		history.setJobType(object.getJobType());
		history.setJobStatus(object.getJobStatus());
		history.setJobUTCStartDate(object.getJobUTCStartDate());
		history.setJobLocalStartDate(object.getJobLocalStartDate());
		history.setJobDisplayName(object.getJobDisplayName());
		history.setVersion(object.getVersion());
		history.setProductType(object.getProductType());
		history.setJobMethod(object.getJobMethod());
		history.setJobUTCEndDate(object.getJobUTCEndDate());
		history.setJobLocalEndDate(object.getJobLocalEndDate());
		history.setServerId(object.getServerId());
		history.setAgentId(object.getAgentId());
		history.setSourceRPSId(object.getSourceRPSId());
		history.setTargetRPSId(object.getTargetRPSId());
		history.setSourceDataStoreUUID(object.getSourceDataStoreUUID());
		history.setTargetDataStoreUUID(object.getTargetDataStoreUUID());
		history.setNodeName(object.getNodeName());
		history.setPlanName(object.getPlanName());
		history.setPlanId(object.getPlanId());
		history.setPlanUuid(object.getPlanUuid());
		history.setNodeId(object.getNodeId());
		history.setServerName(object.getServerName());
		history.setEmptyNodeName(object.isEmptyNodeName());
		history.setJobUUID(object.getJobUUID());
		history.setAgentNodeName(object.getAgentNodeName());
		history.setServerNodeName(object.getServerNodeName());
		history.setAgentUUID(object.getAgentUUID());
		return history;
	}
	
	@Override
	public String toString() {	
		return "JOBHISTORY [ jobType:"+jobType+", jobId:"+jobId
				+"; jobMethod:"+jobMethod
				+"; jobstatus:"+jobStatus
				+"; jobUTCStartTime:"+jobUTCStartDate
				+"; serverID:"+serverId
				+"; agentID:"+agentId
				+"; soruceRPSId:"+sourceRPSId
				+"; targetrpsID:"+targetRPSId
				+"; sourceDataStoreUUID:"+sourceDataStoreUUID
				+"; targetDataStoreUUID:"+targetDataStoreUUID
				+"; productType:"+productType
				+"; planUUID:"+planUuid
				+"; jobUUID:"+jobUUID
				+"; agentNodeName:"+agentNodeName
				+"; serveNodeName:"+serverNodeName
				+"; agentUUID:"+agentUUID
				+"; historyId:"+historyId
				+ "]";
	}
}
