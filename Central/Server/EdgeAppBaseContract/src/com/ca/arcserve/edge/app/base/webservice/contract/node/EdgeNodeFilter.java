package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.DeployStatusFilterType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.JobStatusFilterType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.LastBackupFilterType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NodeStatusFilterType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NotnullFilterType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.OSFilterType;

/**
 * The filter information used when querying node list.
 * 
 * @author panbo01
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@XmlSeeAlso( {
	JobStatusFilterType.class,
	DiscoveryApplication.class,
	ProtectionType.class,
	NodeStatusFilterType.class,
	OSFilterType.class,
	DeployStatusFilterType.class,
	HostType.class,
	NotnullFilterType.class,
	LastBackupFilterType.class
} )
public class EdgeNodeFilter implements Serializable{

	private static final long serialVersionUID = 8949234238560513811L;
	
	private String nodeName = "";
	
	private int jobStatusBitmap;
	private int applicationBitmap;
	private int protectionTypeBitmap;
	private int nodeStatusBitmap;
	private int osBitmap;
	private int remoteDeployBitmap;
	private int hostTypeBitmap;
	private int notnullfieldBitmap;
	private int lastBackupStatusBitmap;
	private int nodeVisibleLevel = 1; //This representative the node display level, nodeVisibleLevel = isVisible in DB, for vApp feature, we define the isVisble=2 for the vapp's VM.
	private int vappId=0;
	private int gatewayId=0;
	
	/**
	 * This is no longer used.
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isD2dOnDInstalled() {
		return Utils.hasBit(applicationBitmap, DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2DOD);
	}
	
	/**
	 * This is no longer used.
	 * 
	 * @param	d2dOnDInstalled
	 */
	@Deprecated
	public void setD2dOnDInstalled(boolean d2dOnDInstalled) {
		applicationBitmap = Utils.setBit(applicationBitmap, DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2DOD, d2dOnDInstalled);
	}
	
	/**
	 * If returned nodes should have SQL Server installed.
	 * 
	 * @return	True if the returned nodes should have SQL Server installed,
	 * 			otherwise false will be returned.
	 */
	public boolean isSqlServerInstalled() {
		return Utils.hasBit(applicationBitmap, DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_SQL);
	}
	
	/**
	 * Set whether returned nodes should have SQL Server installed.
	 * 
	 * @param	sqlServerInstalled
	 * 			Whether SQL Server should be installed.
	 */
	public void setSqlServerInstalled(boolean sqlServerInstalled) {
		applicationBitmap = Utils.setBit(applicationBitmap, DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_SQL, sqlServerInstalled);
	}
	
	/**
	 * If returned nodes should have Exchange installed.
	 * 
	 * @return	True if the returned nodes should have Exchange installed,
	 * 			otherwise false will be returned.
	 */
	public boolean isExchangeInstalled() {
		return Utils.hasBit(applicationBitmap, DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_EXCH);
	}

	/**
	 * Set whether returned nodes should have Exchange installed.
	 * 
	 * @param	exchangeInstalled
	 * 			Whether Exchange should be installed.
	 */
	public void setExchangeInstalled(boolean exchangeInstalled) {
		applicationBitmap = Utils.setBit(applicationBitmap, DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_EXCH, exchangeInstalled);
	}

	/**
	 * If returned nodes should have D2D installed.
	 * 
	 * @return	True if the returned nodes should have D2D installed,
	 * 			otherwise false will be returned.
	 */
	public boolean isD2dInstalled() {
		return Utils.hasBit(applicationBitmap, DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2D);
	}

	/**
	 * Set whether returned nodes should have D2D installed.
	 * 
	 * @param	d2dInstalled
	 * 			Whether UDP agent should be installed.
	 */
	public void setD2dInstalled(boolean d2dInstalled) {
		applicationBitmap = Utils.setBit(applicationBitmap, DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2D, d2dInstalled);
	}

	/**
	 * If returned nodes should have ARCserve Backup installed.
	 * 
	 * @return	True if the returned nodes should have ARCserve Backup installed,
	 * 			otherwise false will be returned.
	 */
	public boolean isArcserveInstalled() {
		return Utils.hasBit(applicationBitmap, DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_BACKUP);
	}

	/**
	 * Set whether returned nodes should have ARCserve Backup installed.
	 * 
	 * @param	arcserveInstalled
	 * 			Whether ARCserve Backup should be installed.
	 */
	public void setArcserveInstalled(boolean arcserveInstalled) {
		applicationBitmap = Utils.setBit(applicationBitmap, DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_BACKUP, arcserveInstalled);
	}
	
	/**
	 * Get the pattern for node name.
	 * 
	 * @return	The pattern for node name.
	 */
	public String getNodeName() {
		return nodeName;
	}
	
	/**
	 * Set the pattern for node name. Use % to match multiple characters and
	 * _ to match one character.
	 * 
	 * @param	nodeName
	 * 			The pattern the name of the node should match
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	/**
	 * Get job status filters. See {@link JobStatusFilterType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @return	Job status filters.
	 * @see		JobStatusFilterType
	 */
	public int getJobStatusBitmap() {
		return jobStatusBitmap;
	}
	
	/**
	 * Set job status filters. See {@link JobStatusFilterType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @param	jobStatusBitmap
	 * 			Filter value.
	 * @see		JobStatusFilterType
	 */
	public void setJobStatusBitmap(int jobStatusBitmap) {
		this.jobStatusBitmap = jobStatusBitmap;
	}
	
	/**
	 * Get application filters. See {@link DiscoveryApplication} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @return	Application filters.
	 * @see		DiscoveryApplication
	 */
	public int getApplicationBitmap() {
		return applicationBitmap;
	}
	
	/**
	 * Set application filters. See {@link DiscoveryApplication} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @param	applicationBitmap
	 * 			Filter value.
	 * @see		DiscoveryApplication
	 */
	public void setApplicationBitmap(int applicationBitmap) {
		this.applicationBitmap = applicationBitmap;
	}
	
	/**
	 * Get protection type filters. See {@link ProtectionType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @return	Protection type filters.
	 * @see		ProtectionType
	 */
	public int getProtectionTypeBitmap() {
		return protectionTypeBitmap;
	}
	
	/**
	 * Set protection type filters. See {@link ProtectionType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @param	protectionTypeBitmap
	 * 			Filter value.
	 * @see		ProtectionType
	 */
	public void setProtectionTypeBitmap(int protectionTypeBitmap) {
		this.protectionTypeBitmap = protectionTypeBitmap;
	}
	
	/**
	 * Get node status filters. See {@link NodeStatusFilterType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @return	Node status filters.
	 * @see		NodeStatusFilterType
	 */
	public int getNodeStatusBitmap() {
		return nodeStatusBitmap;
	}
	
	/**
	 * Set node status filters. See {@link NodeStatusFilterType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @param	nodeStatusBitmap
	 * 			Filter value.
	 * @see		NodeStatusFilterType
	 */
	public void setNodeStatusBitmap(int nodeStatusBitmap) {
		this.nodeStatusBitmap = nodeStatusBitmap;
	}

	/**
	 * Get operating system filters. See {@link OSFilterType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @return	Operating system filters.
	 * @see		OSFilterType
	 */
	public int getOsBitmap() {
		return osBitmap;
	}
	
	/**
	 * Set operating system filters. See {@link OSFilterType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @param	osBitmap
	 * 			Filter value.
	 * @see		OSFilterType
	 */
	public void setOsBitmap(int osBitmap) {
		this.osBitmap = osBitmap;
	}

	/**
	 * Get deploy status filters. See {@link DeployStatusFilterType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @return	Deploy status filters.
	 * @see		DeployStatusFilterType
	 */
	public int getRemoteDeployBitmap() {
		return remoteDeployBitmap;
	} 
	
	/**
	 * Set deploy status filters. See {@link DeployStatusFilterType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @param	remoteDeployBitmap
	 * 			Filter value.
	 * @see		DeployStatusFilterType
	 */
	public void setRemoteDeployBitmap(int remoteDeployBitmap) {
		this.remoteDeployBitmap = remoteDeployBitmap;
	}
	
	/**
	 * Get host type filters. See {@link HostType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @return	Host type filters.
	 * @see		HostType
	 */
	public int getHostTypeBitmap() {
		return hostTypeBitmap;
	}
	
	/**
	 * Set host type filters. See {@link HostType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @param	hostTypeBitmap
	 * 			Filter value.
	 * @see		HostType
	 */
	public void setHostTypeBitmap(int hostTypeBitmap) {
		this.hostTypeBitmap = hostTypeBitmap;
	}
	
	/**
	 * Get not null filters. Not null filter is used to specify which property
	 * should not be empty. See {@link NotnullFilterType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @return	Host type filters.
	 * @see		HostType
	 */
	public int getNotnullfieldBitmap() {
		return notnullfieldBitmap;
	}
	
	/**
	 * Set not null filters. Not null filter is used to specify which property
	 * should not be empty. See {@link NotnullFilterType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @param	hostTypeBitmap
	 * 			Filter value.
	 * @see		HostType
	 */
	public void setNotnullfieldBitmap(int notnullfieldBitmap) {
		this.notnullfieldBitmap = notnullfieldBitmap;
	}

	/**
	 * Set Last Backup Status filters. See {@link LastBackupFilterType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @param	lastBackupStatusBitmap
	 * 			Filter value.
	 * @see		LastBackupFilterType
	 */
	public void setLastBackupStatusBitmap(int lastBackupStatusBitmap){
		this.lastBackupStatusBitmap = lastBackupStatusBitmap;
	}
	
	/**
	 * Get Last Backup Status filters. See {@link LastBackupFilterType} for value
	 * definitions. The filter can be a combination of available values.
	 * 
	 * @return	Last Backup Status filters.
	 * @see		LastBackupFilterType
	 */
	public int getLastBackupStatusBitmap(){
		return lastBackupStatusBitmap;
	}
	
	public boolean hasFilter(){
		if((jobStatusBitmap | applicationBitmap | protectionTypeBitmap 
			| nodeStatusBitmap | osBitmap | remoteDeployBitmap | hostTypeBitmap
			| notnullfieldBitmap | lastBackupStatusBitmap) > 0)
			return true;
		else
			return false;
	}

	public int getNodeVisibleLevel() {
		return nodeVisibleLevel;
	}
	public void setNodeVisibleLevel(int nodeVisibleLevel) {
		this.nodeVisibleLevel = nodeVisibleLevel;
	}
	public int getVappId() {
		return vappId;
	}
	public void setVappId(int vappId) {
		this.vappId = vappId;
	}

	public int getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(int gatewayId) {
		this.gatewayId = gatewayId;
	}
}
