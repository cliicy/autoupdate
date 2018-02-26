package com.ca.arcserve.edge.app.rps.webservice.contract.rps.node;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.extjs.gxt.ui.client.data.BeanModelTag;

/**
 * Detailed information for a RPS node.
 * 
 * @author panbo01
 *
 */
public class RpsNode implements Serializable, BeanModelTag{
	private static final long serialVersionUID = 3236304200752288705L;
	private int node_id;
	private String node_name;
	private String ip_address;
	private String node_description;
	private int node_type;
	private Date lastUpdate;
	private int protocol;
	private int port;
	private String username;
	private @NotPrintAttribute String password;
	private @NotPrintAttribute String uuid;
	private int policy_count;
	private int dedup_store_count;
	private boolean rpsInstalled;
	private String major_version;	
	private String minor_version;
	private String update_version;
	private String build_number;
	private NodeManagedStatus managed;	
	private List<DataStoreStatusListElem> dataStoreModels;
	private boolean mspReplicateDestination;
	//remote deploy
	private int remoteDeployStatus;
	private int deployTaskStatus;
	private Date remoteDeployTime;
	
	private String siteName;
	private int islocalSite;
	/**
	 * Get number of policies deployed to the RPS node.
	 * 
	 * @return
	 */
	public int getPolicy_count() {
		return policy_count;
	}
	
	/**
	 * Set number of policies deployed to the RPS node.
	 * 
	 * @param policy_count
	 */
	public void setPolicy_count(int policy_count) {
		this.policy_count = policy_count;
	}
	
	/**
	 * Get number of the dedup data stores on the RPS node.
	 * 
	 * @return
	 */
	public int getDedup_store_count() {
		return dedup_store_count;
	}
	
	/**
	 * Set number of the dedup data stores on the RPS node.
	 * 
	 * @param dedup_store_count
	 */
	public void setDedup_store_count(int dedup_store_count) {
		this.dedup_store_count = dedup_store_count;
	}
	
	/**
	 * Set whether ARCserve RPS is installed on the node.
	 * 
	 * @param rpsInstalled
	 */
	public void setRpsInstalled(boolean rpsInstalled) {
		this.rpsInstalled = rpsInstalled;
	}
	
	/**
	 * Whether ARCserve RPS is installed on the node.
	 * 
	 * @return
	 */
	public boolean isRpsInstalled() {
		return rpsInstalled;
	}
	
	/**
	 * Get ID of the RPS node.
	 * 
	 * @return
	 */
	public int getNode_id() {
		return node_id;
	}
	
	/**
	 * Set ID of the RPS node.
	 * 
	 * @param node_id
	 */
	public void setNode_id(int node_id) {
		this.node_id = node_id;
	}
	
	/**
	 * Get name of the RPS node.
	 * 
	 * @return
	 */
	public String getNode_name() {
		return node_name;
	}
	
	/**
	 * Set name of the RPS node.
	 * 
	 * @param node_name
	 */
	public void setNode_name(String node_name) {
		this.node_name = node_name;
	}
	
	/**
	 * Get IP address of the RPS node.
	 * 
	 * @return
	 */
	public String getIp_address() {
		return ip_address;
	}
	
	/**
	 * Set IP address of the RPS node.
	 * 
	 * @param ip_address
	 */
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	
	/**
	 * Get description of the RPS node.
	 * 
	 * @return
	 */
	public String getNode_description() {
		return node_description;
	}
	
	/**
	 * Set description of the RPS node.
	 * 
	 * @param node_description
	 */
	public void setNode_description(String node_description) {
		this.node_description = node_description;
	}
	
	/**
	 * Not used.
	 * 
	 * @return
	 */
	public int getNode_type() {
		return node_type;
	}
	
	/**
	 * Not used.
	 * 
	 * @param node_type
	 */
	public void setNode_type(int node_type) {
		this.node_type = node_type;
	}
	
	/**
	 * Set the time of last update to the data record of the RPS node.
	 * 
	 * @return
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	/**
	 * Set the time of last update to the data record of the RPS node.
	 * 
	 * @param lastUpdate
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	/**
	 * Get protocol of the web service of the RPS node.
	 * 
	 * @return
	 */
	public int getProtocol() {
		return protocol;
	}
	
	/**
	 * Set protocol of the web service of the RPS node.
	 * 
	 * @param protocol
	 */
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
	
	/**
	 * Get port of the web service of the RPS node.
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Set port of the web service of the RPS node.
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Get user name used to login to the web service of the RPS node.
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Set user name used to login to the web service of the RPS node.
	 * 
	 * @param username
	 */
	public void setUsername(String username){
		this.username = username;
	}
	
	/**
	 * Get password used to login to the web service of the RPS node.
	 * 
	 * @return
	 */
	@EncryptSave
	public String getPassword() {
		return password;
	}
	
	/**
	 * Set password used to login to the web service of the RPS node.
	 * 
	 * @param password
	 */
	public void setPassword(String password){
		this.password = password;
	}
	
	/**
	 * Get UUID of the RPS node.
	 * 
	 * @return
	 */
	public String getUuid() {
		return uuid;
	}
	
	/**
	 * Set UUID of the RPS node.
	 * 
	 * @param uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * Set major version of the RPS node.
	 * 
	 * @param major_version
	 */
	public void setMajor_version(String major_version) {
		this.major_version = major_version;
	}
	
	/**
	 * Get major version of the RPS node.
	 * 
	 * @return
	 */
	public String getMajor_version() {
		return major_version;
	}
	
	/**
	 * Set minor version of the RPS node.
	 * 
	 * @param minor_version
	 */
	public void setMinor_version(String minor_version) {
		this.minor_version = minor_version;
	}
	
	/**
	 * Get minor version of the RPS node.
	 * 
	 * @return
	 */
	public String getMinor_version() {
		return minor_version;
	}
	
	/**
	 * Set managed status of the RPS node.
	 * 
	 * @param managed
	 */
	public void setManaged(NodeManagedStatus managed) {
		this.managed = managed;
	}
	
	/**
	 * Get managed status of the RPS node.
	 * 
	 * @return
	 */
	public NodeManagedStatus getManaged() {
		return managed;
	}
	
	/**
	 * Get update version of the RPS node.
	 * 
	 * @return
	 */
	public String getUpdate_version() {
		return update_version;
	}
	
	/**
	 * Set update version of the RPS node.
	 * 
	 * @param update_version
	 */
	public void setUpdate_version(String update_version) {
		this.update_version = update_version;
	}
	
	/**
	 * Get build number of the RPS node.
	 * 
	 * @return
	 */
	public String getBuild_number() {
		return build_number;
	}
	
	/**
	 * Set build number of the RPS node.
	 * 
	 * @param build_number
	 */
	public void setBuild_number(String build_number) {
		this.build_number = build_number;
	}
	
	/**
	 * Get list of information of data stores of the RPS node.
	 * 
	 * @return
	 */
	public List<DataStoreStatusListElem> getDataStoreModels() {
		return dataStoreModels;
	}
	
	/**
	 * Set list of information of data stores of the RPS node.
	 * 
	 * @param dataStoreModels
	 */
	public void setDataStoreModels(List<DataStoreStatusListElem> dataStoreModels) {
		this.dataStoreModels = dataStoreModels;
	}
	
	/**
	 * Whether the RPS node is the destination of MSP replication.
	 * 
	 * @return
	 */
	public boolean isMspReplicateDestination() {
		return mspReplicateDestination;
	}
	
	/**
	 * Set whether the RPS node is the destination of MSP replication.
	 * 
	 * @param mspReplicateDestination
	 */
	public void setMspReplicateDestination(boolean mspReplicateDestination) {
		this.mspReplicateDestination = mspReplicateDestination;
	}
	
	/**
	 * Get the status of deploying ARCserve RPS to the node.
	 * 
	 * @return
	 */
	public int getRemoteDeployStatus() {
		return remoteDeployStatus;
	}
	
	/**
	 * Set the status of deploying ARCserve RPS to the node.
	 * 
	 * @param remoteDeployStatus
	 */
	public void setRemoteDeployStatus(int remoteDeployStatus) {
		this.remoteDeployStatus = remoteDeployStatus;
	}
	
	/**
	 * Get the time of deploying ARCserve RPS to the node.
	 * 
	 * @return
	 */
	public Date getRemoteDeployTime() {
		return remoteDeployTime;
	}
	
	/**
	 * Set the time of deploying ARCserve RPS to the node.
	 * 
	 * @param remoteDeployTime
	 */
	public void setRemoteDeployTime(Date remoteDeployTime) {
		this.remoteDeployTime = remoteDeployTime;
	}
	
	public int getDeployTaskStatus() {
		return deployTaskStatus;
	}
	public void setDeployTaskStatus(int deployTaskStatus) {
		this.deployTaskStatus = deployTaskStatus;
	}	
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public int getIslocalSite() {
		return islocalSite;
	}
	public void setIslocalSite(int islocalSite) {
		this.islocalSite = islocalSite;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + node_id;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RpsNode other = (RpsNode) obj;
		if (node_id != other.node_id)
			return false;
		return true;
	}
	
}
