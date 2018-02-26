package com.ca.arcserve.edge.app.base.webservice.udpservice;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.util.paramvalidator.annotations.NotEmpty;
import com.ca.arcserve.edge.app.base.util.paramvalidator.annotations.NotNull;
import com.ca.arcserve.edge.app.base.webservice.contract.backup.BackupType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ItemOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SitePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SitePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.license.bundled.LicenseInformation;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AddNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTarget;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.UpdateNodeResult;
import com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.UpdateRPSResult;
import com.ca.arcserve.edge.app.base.webservice.udpservice.fault.UDPServiceFault;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsNode;

/**
 * This is the interface for all UDP service APIs.
 * <p>
 * UPD service APIs consist of following groups of APIs:
 * <p>
 * <ul>
 * <li>Node management
 * <li>RPS management
 * <li>Plan management
 * <li>Job management
 * <li>Activity Logs
 * <li>Agent deployment
 * <li>License management
 * </ul>
 */
@WebService( targetNamespace = "http://webservice.edge.arcserve.ca.com/" )
public interface IUDPService
{
	/**
	 * Get the version information of the UDP system.
	 * 
	 * @return	The version information
	 * @throws	UDPServiceFault
	 */
	EdgeVersionInfo getVersionInformation(
		) throws UDPServiceFault;
	
	// User management
	
	/**
	 * Login to UDP service. After connected to UDP service, you need to login
	 * before calling other APIs, except {@link getVersionInformation()}.
	 * 
	 * @param	username
	 * @param	password
	 * @param	domain
	 * @throws	UDPServiceFault
	 */
	void login(
		@WebParam( name = "username" ) String username,
		@WebParam( name = "password" ) String password,
		@WebParam( name = "domain" ) String domain
		) throws UDPServiceFault;
	
	/**
	 * Logout the UDP service. After logging out, you have to login again if
	 * you want to invoke APIs on the UDP service.
	 * 
	 * @throws UDPServiceFault
	 */
	void logout(
		) throws UDPServiceFault;
	
	
	
	// Node management
	
	/**
	 * This is a temporary API for testing purpose, and will be removed from
	 * release version.
	 * 
	 * @param	regInfoList
	 * @return
	 * @throws	UDPServiceFault
	 */
	//AddNodeResult addNodes2(
	//	@NotNull @NotEmpty @WebParam( name = "regInfoList" ) List<NodeRegInfo> regInfoList
	//	) throws UDPServiceFault;
	
	/**
	 * Add nodes to UDP. You can add multiple nodes at one time by specifying
	 * multiple registration information.
	 * <p>
	 * NOTE: Prior to adding a Linux node you must have at least one Linux Backup Server added first.
	 * 
	 * @param	nodeInfoList
	 * 			The list contains registration information of the nodes that
	 * 			are about to be added.
	 * 			You can set the gatewayId in node registration info if you want to add a node
	 *          to a specified site. If you not set gatewayId, the node will be added to local site.
	 * @return	The result of adding nodes, including lists of IDs of the newly
	 * 			added nodes.
	 * @throws	UDPServiceFault
	 */
	AddNodeResult addNodes(
		@NotNull @NotEmpty @WebParam( name = "nodeInfoList" ) List<NodeRegistrationInfo> nodeInfoList
		) throws UDPServiceFault;
	
	/**
	 * Update node information to database, and refresh some information by
	 * reconnecting the node.
	 * 
	 * @param	nodeId
	 * 			The ID of the node.
	 * @param	nodeInfo
	 * 			New information of the node.
	 * @return	Updating result and error code if any error occurs.
	 * @throws	UDPServiceFault
	 */
	UpdateNodeResult updateNode(
		@WebParam( name = "nodeId" ) int nodeId,
		@NotNull @WebParam( name = "nodeInfo" ) NodeRegistrationInfo nodeInfo
		) throws UDPServiceFault;
	
	/**
	 * Delete specified nodes from UDP. For nodes that were deployed plan, you
	 * can choose whether to keep the protection settings or not.
	 * 
	 * @param	nodeIdList
	 * 			The list contains the IDs of the nodes which is about to be
	 * 			deleted.
	 * @param	keepCurrentSettings
	 * 			Specify whether keep the current protection settings on the
	 * 			deleted nodes.
	 * @throws	UDPServiceFault
	 */
	void deleteNodes(
		@NotNull @WebParam( name = "nodeIdList" ) List<Integer> nodeIdList,
		@WebParam( name = "keepCurrentSettings" ) boolean keepCurrentSettings
		) throws UDPServiceFault;
	
	/**
	 * Get source node list. You can set filter to get only those nodes
	 * satisfied your condition, and you can specify how many records you want
	 * the API to return by specifying the pagingConfig.
	 * 
	 * @param	nodeFilter
	 * 			Filter for node. Set it to null if don't want to filter anything.
	 * @param	pagingConfig
	 * 			Configuration for getting node list page by page. Set it to null
	 * 			if don't want to use paging mechanism.
	 * @return	The query result including total record count and returned
	 * 			record count.
	 * @throws	UDPServiceFault
	 */
	NodePagingResult getNodeList(
		@WebParam( name = "nodeFilter" ) EdgeNodeFilter nodeFilter,
		@WebParam( name = "pagingConfig" ) NodePagingConfig pagingConfig
		) throws UDPServiceFault;
	
	/**
	 * Get detailed information of the specified node.
	 * 
	 * @param	nodeId
	 * 			The ID of the node whose information will be queried.
	 * @return	The detailed information of the node.
	 * @throws	UDPServiceFault
	 */
	Node getNodeInfo(
		@WebParam( name = "nodeId" ) int nodeId
		) throws UDPServiceFault;
	
	/**
	 * Get all Linux backup servers.
	 * 
	 * @return	The list of Linux backup servers.
	 * @throws	UDPServiceFault
	 */
	List<Node> getLinuxBackupServerList(
		) throws UDPServiceFault;
	

	
	// RPS management
	
	/**
	 * Add an RPS to UDP.
	 * 
	 * @param	rpsInfo
	 * 			The information of the RPS which is about to be added.
	 *          You can set gatewayId in RPS info if you want to add a RPS to a specified site.
	 *          If you not set gatewayId, the RPS will be added to local site.
	 * @return	The result of the operation.
	 * @throws	UDPServiceFault
	 */
	RegistrationNodeResult addRps(
		@NotNull @WebParam( name = "rpsInfo" ) NodeRegistrationInfo rpsInfo
		) throws UDPServiceFault;
	
	/**
	 * Update the information of an RPS which was already added into UDP.
	 * 
	 * @param	rpsId
	 * 			The ID of the RPS.
	 * @param	rpsInfo
	 * 			New information of the RPS.
	 * @return	The result of the operation.
	 * @throws	UDPServiceFault
	 */
	UpdateRPSResult updateRps(
		@WebParam( name = "rpsId" ) int rpsId,
		@NotNull @WebParam( name = "rpsInfo" ) NodeRegistrationInfo rpsInfo
		) throws UDPServiceFault;
	
	/**
	 * Delete a RPS which was already added into UDP.
	 * 
	 * @param	rpsId
	 * 			The ID of the RPS.
	 * @param	keepCurrentSettings
	 *			Whether to keep current RPS settings.
	 * @throws	UDPServiceFault
	 */
	void deleteRps(
		@WebParam( name = "rpsId" ) int rpsId,
		@WebParam( name = "keepCurrentSettings" ) boolean keepCurrentSettings
		) throws UDPServiceFault;
	
	/**
	 * Get detailed information of an RPS which was already added into UDP.
	 * 
	 * @param	rpsId
	 * 			The ID of the RPS.
	 * @return	Detailed information of the specified RPS.
	 * @throws	UDPServiceFault
	 */
	RpsNode getRpsInfo(
		@WebParam( name = "rpsId" ) int rpsId
		) throws UDPServiceFault;
	
	/**
	 * Get information of all RPS that were added into UDP already.
	 * 
	 * @return	The information list.
	 * @throws	UDPServiceFault
	 */
	List<RpsNode> getRpsList(
		) throws UDPServiceFault;
	
	
	
	// DataStore management
	
	/**
	 * Create a data store on the specified RPS. The RPS should be added into
	 * UDP already.
	 * 
	 * @param	rpsId
	 * 			The ID of the RPS.
	 * @param	dataStoreSettings
	 * 			Settings of the data store.
	 * @param	isOnExistingPath
	 * 			Whether to create the data store on an existing path.
	 * @return	The ID of the new data store.
	 * @throws	UDPServiceFault
	 */
	int createDataStore(
		@WebParam( name = "rpsId" ) int rpsId,
		@NotNull @WebParam( name = "dataStoreSettings" ) DataStoreSettingInfo dataStoreSettings,
		@WebParam( name = "isOnExistingPath" ) boolean isOnExistingPath
		) throws UDPServiceFault;
	
	/**
	 * Update the specified data store.
	 * 
	 * @param	rpsId
	 * 			The ID of the RPS.
	 * @param	dataStoreId
	 * 			The ID of the data store.
	 * @param	dataStoreSettings
	 * 			New settings of the data store.
	 * @throws	UDPServiceFault
	 */
	void updateDataStore(
		@WebParam( name = "rpsId" ) int rpsId,
		@WebParam( name = "dataStoreId" ) int dataStoreId,
		@NotNull @WebParam( name = "dataStoreSettings" ) DataStoreSettingInfo dataStoreSettings
		) throws UDPServiceFault;
	
	/**
	 * Delete the specified data store.
	 * 
	 * @param	rpsId
	 * 			The ID of the RPS.
	 * @param	dataStoreId
	 * 			The ID of the data store.
	 * @throws	UDPServiceFault
	 */
	void deleteDataStore(
		@WebParam( name = "rpsId" ) int rpsId,
		@WebParam( name = "dataStoreId" ) int dataStoreId
		) throws UDPServiceFault;

	/**
	 * Get the information of all data stores on the specified RPS. You can
	 * specify whether to retrieve status information of the data store.
	 * Retrieving status information need to connect the RPS on which the
	 * data store located, as a result, this is much slower.
	 * 
	 * @param	rpsId
	 * 			The ID of the RPS.
	 * @param	includeStatus
	 * 			Whether including data store status in the result. Including
	 * 			status may need more time.
	 * @return	The list of data store information
	 * @throws	UDPServiceFault
	 */
	List<DataStoreStatusListElem> getDataStoreList(
		@WebParam( name = "rpsId" ) int rpsId,
		@WebParam( name = "includeStatus" ) boolean includeStatus
		) throws UDPServiceFault;
	
	/**
	 * Get detailed information of the specified data store. You can
	 * specify whether to retrieve status information of the data store.
	 * Retrieving status information need to connect the RPS on which the
	 * data store located, as a result, this is much slower.
	 * 
	 * @param	rpsId
	 * 			The ID of the RPS.
	 * @param	dataStoreId
	 * 			The ID of the data store.
	 * @param	includeStatus
	 * 			Whether including data store status in the result. Including
	 * 			status may need more time.
	 * @return	Information of the data store.
	 * @throws	UDPServiceFault
	 */
	DataStoreStatusListElem getDataStoreInfo(
		@WebParam( name = "rpsId" ) int rpsId,
		@WebParam( name = "dataStoreId" ) int dataStoreId,
		@WebParam( name = "includeStatus" ) boolean includeStatus
		) throws UDPServiceFault;
	
	
	
	// Plan operations
	
	/**
	 * Create a plan according to specified settings. Creating plan with nodes
	 * specified will make the plan to be deployed to those nodes.
	 * 
	 * @param	policy
	 * 			The contents of the plan.
	 * @return	The ID of the newly created plan.
	 * @throws	UDPServiceFault
	 * @see		UnifiedPolicy
	 */
	int createPlan(
		@NotNull @WebParam( name = "policy" ) UnifiedPolicy policy
		) throws UDPServiceFault;
	
	/**
	 * Update an existing plan. Update a plan with nodes specified will cause
	 * the plan to be deployed to those nodes.
	 * 
	 * @param	planId
	 * 			The ID of the plan which is about to be updated.
	 * @param	policy
	 * 			The contents of the plan.
	 * @throws	UDPServiceFault
	 */
	void updatePlan(
		@WebParam( name = "planId" ) int planId,
		@NotNull @WebParam( name = "policy" ) UnifiedPolicy policy
		) throws UDPServiceFault;
	
	/**
	 * Delete specified plans. Plans used by nodes cannot be deleted.
	 * 
	 * @param	idList
	 * 			The list contains IDs of the plans which is about to be deleted.
	 * @return	The list of deleting result of each specified plan.
	 * @throws	UDPServiceFault
	 */
	List<ItemOperationResult> deletePlans(
		@NotNull @WebParam( name = "idList" ) List<Integer> idList
		) throws UDPServiceFault;
	
	/**
	 * Get the status of all plans in UDP. The status includes deployment
	 * status and job status.
	 * 
	 * @return	The list contains the plan status.
	 * @throws	UDPServiceFault
	 */
	List<PolicyInfo> getPlanStatusList(
		) throws UDPServiceFault;
	
	/**
	 * Get list of IDs of all plans in UDP.
	 * 
	 * @return	The list contains the plan IDs.
	 * @throws	UDPServiceFault
	 */
	List<Integer> getPlanIdList(
		) throws UDPServiceFault;
	
	/**
	 * Get the contents of the specified plan.
	 * 
	 * @param	planId
	 * 			The ID of the plan.
	 * @return	The contents of the plan.
	 * @throws	UDPServiceFault
	 */
	UnifiedPolicy getPlan(
		@WebParam( name = "planId" ) int planId
		) throws UDPServiceFault;
	
	
	// Job Management
	
	/**
	 * Submit a backup job to specified nodes.
	 * 
	 * @param	nodeIdList
	 * 			List of IDs of nodes on which the new job will runs.
	 * @param	backupType
	 * 			Backup types, including full, incremental, etc.
	 * @param	jobName
	 * 			The name of the new job.
	 * @throws	UDPServiceFault
	 */
	void submitBackupJob(
		@NotNull @NotEmpty @WebParam( name = "nodeIdList" ) List<Integer> nodeIdList,
		@NotNull @WebParam( name = "backupType" ) BackupType backupType,
		@NotNull @WebParam( name = "jobName" ) String jobName
		) throws UDPServiceFault;
	
	/**
	 * Cancel a job which is running on specified node.
	 * 
	 * @param	nodeId
	 * 			The ID of the node on which the job is running.
	 * @param	jobId
	 * 			The ID of the job.
	 * @throws	UDPServiceFault
	 */
	void cancelJob(
		@WebParam( name = "nodeId" ) int nodeId,
		@WebParam( name = "jobId" ) int jobId
		) throws UDPServiceFault;
	
	/**
	 * Get job monitor list of the specified node. The job monitor contains
	 * many detailed information about the running job.
	 * 
	 * @param	nodeId
	 * 			ID of the node.
	 * @return	A list of job monitors.
	 * @throws	UDPServiceFault
	 */
	List<FlashJobMonitor> getJobStatusInfoList(
		@WebParam( name = "nodeId" ) int nodeId
		) throws UDPServiceFault;
	
//	FlashJobMonitor getD2DJobMonitor(
//		@WebParam( name = "nodeId" ) int nodeId
//		) throws UDPServiceFault;
	
	/**
	 * Get job histories of the specified node. You can specify a filter to
	 * tell what kind of job history will be fetched. And you can specify
	 * how many records should be returned. 
	 * 
	 * @param	nodeId
	 * 			ID of the node.
	 * @param	filter
	 * 			Filter for job histories. Set it to null if don't want to
	 * 			filter anything. 
	 * @param	pagingConfig
	 * 			Configuration for getting job history list page by page. Set it
	 * 			to null if don't want to use paging mechanism.
	 * @return	The query result including total record count and returned
	 * 			record count.
	 * @throws	UDPServiceFault
	 */
	JobHistoryPagingResult getJobHistoryList(
		@WebParam( name = "nodeId" ) int nodeId,
		@WebParam( name = "filter" ) JobHistoryFilter filter,
		@WebParam( name = "pagingConfig" ) JobHistoryPagingConfig pagingConfig
		) throws UDPServiceFault;
	
	
	// Activity Logs
	
	/**
	 * Get activity logs of UDP.
	 * 
	 * @param	filter
	 * 			Filter for activity logs. Set it to null if don't want to
	 * 			filter anything.
	 * @param	pagingConfig
	 * 			Configuration for getting activity logs page by page. Set it
	 * 			to null if don't want to use paging mechanism.
	 * @return	The query result including total record count and returned
	 * 			record count.
	 * @throws	UDPServiceFault
	 */
	LogPagingResult getActivityLogs(
		@WebParam( name = "filter" ) LogFilter filter,
		@WebParam( name = "pagingConfig" ) LogPagingConfig pagingConfig
		) throws UDPServiceFault;
	
	
	// Agent Deployment
	
	/**
	 * Start to deploy agent to the specified nodes according to the specified
	 * configurations.
	 * 
	 * @param	targetList
	 * 			The list of targets and their deployment configurations to
	 * 			which the agent will be deployed.
	 * @throws	UDPServiceFault
	 */
	void startDeployingAgent(
		@NotNull @NotEmpty @WebParam( name = "targetList" ) List<DeployTarget> targetList
		) throws UDPServiceFault;
	
	/**
	 * Get agent deployment details of specified nodes, including information
	 * for submit the deployment job and deploying status.
	 * 
	 * UDP keep one record for one node ID, so the details maybe rewritten if
	 * a new deployment of the node ID was started. Or the record maybe deleted
	 * in some cases. If you try to use this function to monitor the deploying
	 * status, don't wait too long between two polls. Say 30 seconds is a safety
	 * interval. 
	 * 
	 * @param	nodeIdList
	 * 			The list of IDs of nodes whose deployment details will be fetched.
	 * @return	List of deployment status.
	 * @throws	UDPServiceFault
	 */
	List<DeployTargetDetail> getAgentDeploymentDetails(
		@WebParam( name = "nodeIdList" ) List<Integer> nodeIdList
		) throws UDPServiceFault;

	
	// License Management
	
	/**
	 * Add a license key to UDP.
	 * 
	 * @param	licenseKey
	 * 			The license key.
	 * @throws	UDPServiceFault
	 */
	void addLicenseKey(
		@NotNull @NotEmpty @WebParam( name = "licenseKey" ) String licenseKey
		) throws UDPServiceFault;
	
	/**
	 * Get license information of all added license keys.
	 * 
	 * @return	A list contains all the information
	 * @throws	UDPServiceFault
	 */
	List<LicenseInformation> getLicenses(
		) throws UDPServiceFault;
	
	//Site Management
	
	/**
	 * Add a Site to UDP
	 * @param  siteInfo
	 *         The information of the site which is about to be added.
	 * @return The ID of the new site.
	 * @throws UDPServiceFault
	 */
	int addSite( @NotNull @NotEmpty @WebParam( name = "siteInfo" ) SiteInfo siteInfo
			)throws UDPServiceFault;
	
	/**
	 * Update the information of an site which was already added into UDP.
	 * 
	 * @param  siteId
	 *         The ID of the site.
	 * @param  siteInfo
	 *         New information of the Site.
	 * @throws UDPServiceFault
	 */
	void updateSite(
			@WebParam( name = "siteId" ) int siteId,
			@NotNull @WebParam( name = "siteInfo" ) SiteInfo siteInfo
			)throws UDPServiceFault;
	
	/**
	 * Delete a site which was already added into UDP.
	 * 
	 * @param  siteId
	 *         The ID of the site
	 * @throws UDPServiceFault
	 */
	void deleteSite(
			@WebParam( name = "siteId" ) int siteId
			)throws UDPServiceFault;
	
	/**
	 * Get detailed information of the specified site.
	 * 
	 * @param  siteId
	 *         The ID of the site whose information will be queried.
	 * @return The detailed information of the site.
	 *         The gatewayId can be used to add node/RPS to this site.
	 *         The registration text can be used to registration gateway.
	 * @throws UDPServiceFault
	 */
	SiteInfo getSiteInfo(
			@WebParam( name = "siteId" ) int siteId
			)throws UDPServiceFault;
	
	/**
	 * Get site list.
	 * You can set filter to get only those sites satisfied your condition,
	 * and you can specify how many records you want the API to return by specifying the pagingConfig.
	 * 
	 * @param  siteFilter
	 *         Filter for site. Set it to null if don't want to filter anything.
	 * @param  pagingConfig
	 *         Configuration for getting site list page by page. Set it to null if you don't want to use
	 *         paging mechanism.
	 * @return The query result including total record count and returned.
	 * @throws UDPServiceFault
	 */
	SitePagingResult getSites(SiteFilter siteFilter,
			SitePagingConfig pagingConfig)throws UDPServiceFault;
}
