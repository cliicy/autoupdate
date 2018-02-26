package com.ca.arcserve.edge.app.base.webservice.contract.log;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.ServerDate;
import com.ca.arcserve.edge.app.base.webservice.contract.filter.BaseFilter;

/**
 * Filters for getting activity logs.
 * 
 * @author panbo01
 *
 */
public class LogFilter implements Serializable {

	private static final long serialVersionUID = 1084934550214286484L;
	
	private Severity severity = Severity.ErrorAndWarning;
	private Module module = Module.All;
	private LogProductType productType;
	private String nodeName = "";
	private String serverName = "";
	private String vmName = "";
	private int nodeId = -1;
	private int jobId = -1;
	private String message = "";
	private ServerDate startDate;
	private ServerDate endDate;
	private int serverId = -1;
	private BaseFilter timeFilter = new BaseFilter();
	private LogJobType jobType = LogJobType.All;
	private String dataStoreUUID;
	
	/**
	 * Get logs of which types of jobs will be returned.
	 * 
	 * @return
	 */
	public LogJobType getJobType() {
		return jobType;
	}
	
	/**
	 * Specify logs of which types of jobs will be returned.
	 * 
	 * @param jobType
	 */
	public void setJobType(LogJobType jobType) {
		this.jobType = jobType;
	}
	
	/**
	 * Get time filter settings.
	 * 
	 * @return
	 */
	public BaseFilter getTimeFilter() {
		return timeFilter;
	}
	
	/**
	 * Set time filter settings.
	 * 
	 * @param timeFilter
	 */
	public void setTimeFilter(BaseFilter timeFilter) {
		this.timeFilter = timeFilter;
	}
	
	/**
	 * Get RPS ID filter setting. Set it to -1 to ignore it.
	 * 
	 * @return
	 */
	public int getServerId() {
		return serverId;
	}
	
	/**
	 * Set RPS ID filter setting. Set it to -1 to ignore it.
	 * 
	 * @param serverId
	 */
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	/**
	 * Get RPS name filter setting. Set it to empty string to ignore it.
	 * 
	 * @return
	 */
	public String getServerName() {
		return serverName;
	}
	
	/**
	 * Set RPS name filter setting. Set it to empty string to ignore it.
	 * 
	 * @param serverName
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	/**
	 * Get message filter setting.
	 * 
	 * @return
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Set message filter setting.
	 * 
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Get severity filter setting.
	 * 
	 * @return
	 */
	public Severity getSeverity() {
		return severity;
	}
	
	/**
	 * Set severity filter setting.
	 * 
	 * @param severity
	 */
	public void setSeverity(Severity severity) {
		this.severity = severity;
	}
	
	/**
	 * Get module filter setting.
	 * 
	 * @return
	 */
	public Module getModule() {
		return module;
	}
	
	/**
	 * Set module filter setting.
	 * 
	 * @param module
	 */
	public void setModule(Module module) {
		this.module = module;
	}
	
	/**
	 * Get product type filter setting.
	 * 
	 * @return
	 */
	public LogProductType getProductType() {
		return productType;
	}
	
	/**
	 * Set product type filter setting.
	 * 
	 * @param productType
	 */
	public void setProductType(LogProductType productType) {
		this.productType = productType;
	}
	
	/**
	 * Get node name filter setting.
	 * 
	 * @return
	 */
	public String getNodeName() {
		return nodeName;
	}
	
	/**
	 * Set node name filter setting.
	 * 
	 * @param nodeName
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	/**
	 * Get VM name filter setting.
	 * 
	 * @return
	 */
	public String getVmName() {
		return vmName;
	}
	
	/**
	 * Set VM name filter setting.
	 * 
	 * @param vmName
	 */
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	
	/**
	 * Get node ID filter setting.
	 * 
	 * @return
	 */
	public int getNodeId() {
		return nodeId;
	}
	
	/**
	 * Set node ID filter setting.
	 * 
	 * @param nodeId
	 */
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	
	/**
	 * Get job ID filter setting.
	 * 
	 * @return
	 */
	public int getJobId() {
		return jobId;
	}
	
	/**
	 * Set job ID filter setting.
	 * 
	 * @param jobId
	 */
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	
	/**
	 * Get after when the returned logs should be generated.
	 * 
	 * @return
	 */
	public ServerDate getStartDate() {
		return startDate;
	}
	
	/**
	 * Set after when the returned logs should be generated.
	 * 
	 * @param startDate
	 */
	public void setStartDate(ServerDate startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Get before when the returned logs should be generated.
	 * 
	 * @return
	 */
	public ServerDate getEndDate() {
		return endDate;
	}
	
	/**
	 * Set before when the returned logs should be generated.
	 * 
	 * @param endDate
	 */
	public void setEndDate(ServerDate endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * Get data store filter setting.
	 * 
	 * @return
	 */
	public String getDataStoreUUID() {
		return dataStoreUUID;
	}
	
	/**
	 * Set data store filter setting.
	 * 
	 * @param dataStoreUUID
	 */
	public void setDataStoreUUID(String dataStoreUUID) {
		this.dataStoreUUID = dataStoreUUID;
	}
	
}
