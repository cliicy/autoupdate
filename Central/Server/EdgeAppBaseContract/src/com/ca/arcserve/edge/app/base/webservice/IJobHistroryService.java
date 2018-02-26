package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;

import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ServerDate;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.CancelJobParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryFilter4Dashboard;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobTypeForGroupByPlan;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitor;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail;

public interface IJobHistroryService {
	
	JobHistoryPagingResult getD2DJobHistoryList(int nodeId, JobHistoryPagingConfig config, JobHistoryFilter filter) throws EdgeServiceFault;
	JobHistoryPagingResult getASBUJobHistoryList(int nodeId, JobHistoryPagingConfig config, JobHistoryFilter filter) throws EdgeServiceFault;
	void deleteAllD2DJobHistorys(int nodeId) throws EdgeServiceFault;
	void deleteOldD2DJobHistorys(int nodeId, ServerDate serverDate) throws EdgeServiceFault;
	
	JobHistoryPagingResult getRpsJobHistoryList(int nodeId, JobHistoryPagingConfig config, JobHistoryFilter filter) throws EdgeServiceFault;
	void deleteAllRpsJobHistorys(int nodeId) throws EdgeServiceFault;
	void deleteOldRpsJobHistorys(int nodeId, ServerDate serverDate) throws EdgeServiceFault;
	
	// Paging view
	JobHistoryPagingResult getDashboardJobHistoryList(JobHistoryPagingConfig config, JobHistoryFilter4Dashboard filter) throws EdgeServiceFault;
	
	// Grouping view
	List<PolicyInfo> getPlans(JobHistoryFilter4Dashboard filter) throws EdgeServiceFault;
	List<JobTypeForGroupByPlan> getJobTypes(JobHistoryFilter4Dashboard filter) throws EdgeServiceFault;
	JobHistoryPagingResult getJobHistories(JobHistoryFilter4Dashboard filter) throws EdgeServiceFault;
	
	JobHistoryPagingResult getLinuxD2DJobHistoryList(int nodeId, JobHistoryPagingConfig config, JobHistoryFilter filter) throws EdgeServiceFault;
	
	/**
	 * find job monitor from other platform
	 * 
	 * @param jobMonitor
	 * @throws EdgeServiceFault
	 * @return
	 */
	List<JobMonitor> findRemoteJobMonitor(JobDetail jobDetail)throws EdgeServiceFault;
	
	/**
	 * find job monitors under node
	 * @param jobDetail
	 * @return
	 * @throws EdgeServiceFault
	 */
	
	List<JobMonitor> findJobMonitors(JobDetail jobDetail)throws EdgeServiceFault;
	/**
	 * create a job monitor
	 * 
	 * @param jobDetail
	 * @throws EdgeServiceFault
	 */
	void createJobMonitor(JobMonitor jobMonitor)throws EdgeServiceFault;
	
	/**
	 * delete a job monitor
	 * 
	 * @param jobMonitor
	 * @throws EdgeServiceFault
	 */
	void deleteJobMonitor(JobMonitor jobMonitor)throws EdgeServiceFault;
	
	/**
	 * cancel job
	 * 
	 * @param jobDetail
	 * @return
	 * @throws EdgeServiceFault
	 */
	boolean cancelJobForEdge(JobDetail jobDetail) throws EdgeServiceFault;
	
	
	/**
	 * get jobMonitor form db table 'as_edge_d2djobhistory_moniotr' 
	 * @param agentId:if not -1, get jobMonitor for d2d view
	 * @param serverId:if not -1, get jobMonitor for RPS View
	 * @param dataStoreUUID:if not '' , get jobMonitor for RPS_Datastore view
	 * @return null/List<JobHistory>
	 * @throws EdgeServiceFault
	 */
//	List<JobHistory> getJobMonitorsFromDB(int agentId,int serverId,String dataStoreUUID) throws EdgeServiceFault;
	
	/**
	 * connect to server get jobMonitor by specified <nodeId,serverId,jobType,jobId> 
	 * @param nodeId
	 * @param serverId
	 * @param jobType
	 * @param jobId
	 * @return null/List<FlashJobMonitor>
	 * @throws EdgeServiceFault
	 */
//	List<FlashJobMonitor> getJobMonitors(int nodeId,int serverId,Long jobType,Long jobId) throws EdgeServiceFault;
		
	/**
	 * get jobMonitor for DashBoard view 
	 * @param agentId 
	 * @param serverId
	 * @param jobType
	 * @param jobId
	 * @return null/List<FlashJobMonitor>
	 * @throws EdgeServiceFault
	 */	
	List<FlashJobMonitor> getJobMonitorsForDashBoard(JobHistory jobHistory) throws EdgeServiceFault;
	
	/**
	 * get jobMonitor for DashBoard's history 
	 * @param historys 
	 * @return null/List<FlashJobMonitor>
	 * @throws EdgeServiceFault
	 */	
	List<FlashJobMonitor> getJobMonitorsForDashBoardHistorys(List<JobHistory> historys) throws EdgeServiceFault;
	
	/**
	 * get jobMonitor for Resource_node view 
	 * @param nodeId
	 * @return null/List<FlashJobMonitor>
	 * @throws EdgeServiceFault
	 */
	List<FlashJobMonitor> getJobMonitorsForNodeView(int nodeId) throws EdgeServiceFault;
	
	/**
	 * get jobMonitor for Resource_Rps view 
	 * @param serverId
	 * @param dataStoreUUID
	 * @return null/List<FlashJobMonitor>
	 * @throws EdgeServiceFault
	 */
	List<FlashJobMonitor> getJobMonitorsForRpsView(int serverId, String dataStoreUUID) throws EdgeServiceFault;
	
	/**
	 * get jobMonitor for Resource_Asbu view 
	 * @param serverId
	 * @return null/List<FlashJobMonitor>
	 * @throws EdgeServiceFault
	 */
	List<FlashJobMonitor> getJobMonitorsForAsbuView(int serverId) throws EdgeServiceFault;
	
	/**
	 * cancel job list by CancelJobParameter list on DashBoard
	 * 
	 * @param parameters
	 * @return
	 * @throws EdgeServiceFault
	 */
	int cancelMultipleJobs(List<CancelJobParameter> parameters)
			throws EdgeServiceFault;
}
