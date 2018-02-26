package com.ca.arcserve.edge.app.base.webservice.d2djobstatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.appdaos.EdgeJobHistory;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao.JobHistoryProductType;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;

public class D2DAllJobStatusCache {
	private static final Logger logger = Logger.getLogger(D2DAllJobStatusCache.class);
	private static D2DAllJobStatusCache allJobCache = new D2DAllJobStatusCache();
	private ConcurrentMap<String, FlashJobMonitor> allJobStatusMap = new ConcurrentHashMap<String, FlashJobMonitor>();
	private DelayQueue<DelayJobs<D2DJobStatusPair<String, FlashJobMonitor>>> delayQueue = new DelayQueue<DelayJobs<D2DJobStatusPair<String, FlashJobMonitor>>>();
	private TimeUnit TIMEUNIT = TimeUnit.SECONDS;
	private int DELAYSECONDS = 60;
	private long NANOTIME = TimeUnit.NANOSECONDS.convert(DELAYSECONDS, TIMEUNIT);
	private Thread daemonThread;
	private IEdgeJobHistoryDao jobHistoryDao = DaoFactory.getDao(IEdgeJobHistoryDao.class);
	private IEdgePolicyDao edgepolicyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	private final static int JOBID_ZERO = 0;
	
	private D2DAllJobStatusCache() {
		Runnable daemonTask = new Runnable() {
			public void run() {
				checkDelayJobs();
			}
		};

		daemonThread = new Thread(daemonTask);
		daemonThread.setDaemon(true);
		daemonThread.setName("D2DAllJobStatusCache");
		daemonThread.start();
	}
	
	public static D2DAllJobStatusCache getD2DAllJobStatusCache() {
		return allJobCache;
	}
	
	private void checkDelayJobs() {
		for (;;) {
			try {
				logger.debug("Start checking delaying job");
				DelayJobs<D2DJobStatusPair<String, FlashJobMonitor>> delayJob = delayQueue.take();
				if (delayJob != null) {
					logger.debug("Before Map size is " + allJobStatusMap.size() + " " + allJobStatusMap.toString() + " , Current delayQueue size is " 
							+ delayQueue.size());
					boolean flag = allJobStatusMap.remove(delayJob.getItem().nodeId,delayJob.getItem().infoBean);
					logger.debug("Delayed job removed from allJobStatusMap, flag = " + flag + ", Removed key = " + delayJob.getItem().nodeId
							+ " , Removed value = " + delayJob.getItem().infoBean.toString());
					logger.debug("Start deleteing delay job from DB, jobId = " + ((FlashJobMonitor)(delayJob.getItem().infoBean)).getJobId() + " jobType = "
							+ ((FlashJobMonitor)(delayJob.getItem().infoBean)).getJobType() + " nodeID = " + ((FlashJobMonitor)(delayJob.getItem().infoBean)).getNodeId()
							+ " serverID= " + ((FlashJobMonitor)(delayJob.getItem().infoBean)).getRunningServerId());
					removeRunningJobFromHistory((FlashJobMonitor)(delayJob.getItem().infoBean));
					logger.debug("End Map size is " + allJobStatusMap.size() + " " + allJobStatusMap.toString() + " , Current delayQueue size is " 
							+ delayQueue.size());
				}
			} catch (InterruptedException e) {
				logger.info("Delayed Exception 1:" + e.toString());
				break;
			}catch (Exception e){
				logger.error("Delayed Exception 2:" + e.getMessage());
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					logger.info("Delayed Exception 3:" + e1.toString());
					break;
				}
			}
		}

	}
	
	public synchronized void insertOrUpdateRunningJobToHistory(FlashJobMonitor jobMonitor) {
		long[] id = new long[1];
		if (jobMonitor.getJobType() == JobType.JOBTYPE_VM_BACKUP) {
			checkHistoryForVMWaitingJob(jobMonitor, id);
			logger.debug("checkHistoryForVMWaitingJob result : "+id);
			if (id[0] == 0) {
				checkHistoryForCommonJob(jobMonitor, id);
				logger.debug("checkHistoryForCommonJob result : "+id);
				saveOrUpdateHistory(jobMonitor, id);
			} else {
				// Update job monitor, replace the VM waiting job monitor with the real job monitor
				saveOrUpdateHistory(jobMonitor, id);
			}
		} else {			
			checkHistoryForCommonJob(jobMonitor, id);
			logger.debug("checkHistoryForCommonJob result : "+id);
			saveOrUpdateHistory(jobMonitor, id);
		}
	}
	
	private void checkHistoryForCommonJob(FlashJobMonitor jobMonitor, long[] id) {
		jobHistoryDao.as_edge_d2dJobHistory_monitor_select(jobMonitor.getJobId(), jobMonitor.getJobType(), jobMonitor.getNodeId(), jobMonitor.getRunningServerId(), id);
	}
	private void checkHistoryForVMWaitingJob(FlashJobMonitor jobMonitor, long[] id) {
		jobHistoryDao.as_edge_d2dJobHistory_monitor_select(JOBID_ZERO, jobMonitor.getJobType(), jobMonitor.getNodeId(), jobMonitor.getRunningServerId(), id);
	}

	private void saveOrUpdateHistory(FlashJobMonitor jobMonitor, long[] id) {
		// the value of jobMonitor.getStartTime() is in seconds rather than milliseconds
		String planUuid = jobMonitor.getJobType() == JobType.JOBTYPE_RPS_REPLICATE_IN_BOUND ? jobMonitor.getTargetPlanUUID() : jobMonitor.getPlanUUID();
		logger.debug("save or update history, plan uuid = "+planUuid);
		if(jobMonitor.isPendingJobMonitor() && !jobMonitor.isOnDemand()){ 
			if(StringUtil.isEmptyOrNull(planUuid) && !StringUtil.isEmptyOrNull(jobMonitor.getVmInstanceUUID())) {
				String[] planUUID = new String[1];
				edgepolicyDao.as_edge_policy_uuid_by_vminstanceuuid(jobMonitor.getVmInstanceUUID(), planUUID);
				planUuid = planUUID[0];
				logger.debug("Save or update history get plan UUID by vm instance uuid (" + jobMonitor.getVmInstanceUUID() + ") : " + planUUID);
			}
		}
		logger.debug("execute insert job monitor to db: as_edge_d2dJobHistory_monitor_insert");
		logger.debug("job detail is "+ jobMonitor);
		
		jobHistoryDao.as_edge_d2dJobHistory_monitor_insert(jobMonitor.getJobId(), jobMonitor.getJobType(), jobMonitor.getJobMethod(), jobMonitor.getJobStatus(), 
				jobMonitor.getNodeId(), jobMonitor.getRunningServerId(),jobMonitor.getSourceRPSId(),jobMonitor.getTargetRPSId(), new Date(jobMonitor.getStartTime()), jobMonitor.getHistoryProductType(), id[0], 
				planUuid == null ? "" : planUuid,
			jobMonitor.getTargetPlanUUID() == null ? "" : jobMonitor.getTargetPlanUUID(),
			jobMonitor.getJobUUID() == null ? "" : jobMonitor.getJobUUID(), jobMonitor.getAgentNodeName(), jobMonitor.getServerNodeName(), getJobMonitorAgentUUID(jobMonitor));
	}
	
	private String getJobMonitorAgentUUID(FlashJobMonitor jobMonitor){
		String agentUUID;
		if(!StringUtil.isEmptyOrNull(jobMonitor.getD2dUuid())){
			agentUUID = "D2D:"+jobMonitor.getD2dUuid();
		} else {
			if(!StringUtil.isEmptyOrNull(jobMonitor.getVmInstanceUUID()))
				agentUUID = "VM:"+jobMonitor.getVmInstanceUUID();
			else 
				agentUUID = "";		
		}
		return agentUUID;
	}
	
	private boolean isMergeJob(FlashJobMonitor jobMonitor) {
		return jobMonitor.getJobType() == JobType.JOBTYPE_MERGE || jobMonitor.getJobType() == JobType.JOBTYPE_RPS_MERGE || jobMonitor.getJobType() == JobType.JOBTYPE_VM_MERGE;
	}
	
	public void removeRunningJobFromHistory(FlashJobMonitor jobMonitor) {
		JobHistory removeHistory = null;
		List<EdgeJobHistory> lstJobHistory = new ArrayList<EdgeJobHistory>();
		jobHistoryDao.as_edge_d2dJobHistory_monitor_getJobMonitorByHistoy(jobMonitor.getJobType(), jobMonitor.getJobId(), jobMonitor.getRunningServerId(), jobMonitor.getNodeId(), lstJobHistory);
		logger.debug("removeRunningJobFromHistory lstJobHistory.size="+(lstJobHistory==null?0:lstJobHistory.size()));
		if( (lstJobHistory!=null) && (!lstJobHistory.isEmpty())){
			removeHistory = new JobHistory();
			removeHistory.setJobType(lstJobHistory.get(0).getJobType());
			removeHistory.setJobId(lstJobHistory.get(0).getJobId());
			removeHistory.setServerId(lstJobHistory.get(0).getServerId());
			removeHistory.setAgentId(lstJobHistory.get(0).getAgentId());
			removeHistory.setJobUUID(lstJobHistory.get(0).getJobUUID());
			removeHistory.setTargetRPSId(lstJobHistory.get(0).getTargetRPSId());
		}
		
		logger.debug("removeRunningJobFromHistory JobId = " + jobMonitor.getJobId()+" , JobType = " + jobMonitor.getJobType()+" NodeId = " + jobMonitor.getNodeId() + " ServerId = " + jobMonitor.getRunningServerId()
				+ " Finished = " + jobMonitor.isFinished() + " NodeName = " + jobMonitor.getAgentNodeName() + " Key = " + jobMonitor.getJobMonitorId()
				+ " isRunningOnRPS = " + jobMonitor.isRunningOnRPS() + " job status = " + jobMonitor.getJobStatus());
		jobHistoryDao.as_edge_d2dJobHistory_monitor_delete(jobMonitor.getJobId(), jobMonitor.getJobType(), jobMonitor.getNodeId(), jobMonitor.getRunningServerId());			
		
		if(removeHistory != null){
			logger.debug("removeRunningJobFromHistory history="+removeHistory.toString());
			this.removeCacheJobMonitorByHistory(removeHistory);
		}
	}
	/**
	 * Since we don't save the redirect job monitor from RPS into DB(except waiting backup job monitor), so we don't delete the record
	 * when the job monior is the redirected job monitor.  
	 * @param jobMonitor
	 * @return
	 */
	private boolean isAllowedToDelete(FlashJobMonitor jobMonitor) {
		return jobMonitor.isRunningOnRPS() || (!jobMonitor.isRunningOnRPS() && !jobMonitor.getJobMonitorId().startsWith("RPS")) || jobMonitor.getJobStatus() == JobStatus.JOBSTATUS_WAITING;
	}
	
	public void put(String key, FlashJobMonitor jobMonitor) {
		FlashJobMonitor oldValue = allJobStatusMap.put(key, jobMonitor);
		logger.debug("Cache old value = " + oldValue);
		if (oldValue != null) {
			delayQueue.remove(new DelayJobs<D2DJobStatusPair<String, FlashJobMonitor>>(new D2DJobStatusPair<String, FlashJobMonitor>(key, jobMonitor), NANOTIME));	
			// for JobMonitors reported,only insert not update,except when waitingJob change to RunningJob will update waitingJob
//			if (isAllowedToUpdate(jobMonitor, oldValue) && isAllowedToInsert(key, jobMonitor)) {
//				// Update
//				logger.debug("execute update job");
//				insertOrUpdateRunningJobToHistory(jobMonitor);
//			}
			// for waitingJob change to RunningJob,update the table
			if( (oldValue.getJobStatus() != jobMonitor.getJobStatus())  
					&& (oldValue.getJobStatus() == JobStatus.JOBSTATUS_WAITING ) ){
				if (isAllowedToUpdate(jobMonitor, oldValue) && isAllowedToInsert(key, jobMonitor)) {
					// Update
					logger.debug("execute update job");
					insertOrUpdateRunningJobToHistory(jobMonitor);
				}
			}
		} else {
			if (isAllowedToInsert(key, jobMonitor)) {
				// Insert
				logger.debug("execute insert job");
				insertOrUpdateRunningJobToHistory(jobMonitor);				
			}
		}
		delayQueue.put(new DelayJobs<D2DJobStatusPair<String, FlashJobMonitor>>(new D2DJobStatusPair<String, FlashJobMonitor>(key, jobMonitor), NANOTIME));
	}
	
	public void removeCacheJobMonitorByHistory(JobHistory history) {
//		int productType = history.getProductType();
		int agentId = history.getAgentId() != null ? Integer.valueOf(history.getAgentId()) : 0;
		int serverId = history.getServerId() != null ? Integer.valueOf(history.getServerId()):0;
		int rpsId = history.getTargetRPSId() != null ? Integer.valueOf(history.getTargetRPSId()):0;
		long jobType = history.getJobType();
		long jobId = history.getJobId();
		String jobUUID = history.getJobUUID();		

		// D2Dcache has this key
		StringBuilder d2dSb = new StringBuilder();
		d2dSb.append("D2D").append("-").append(agentId).append("-").append(serverId)
			.append("-").append(jobType)
			.append("-").append(jobId);
		if (jobType == JobType.JOBTYPE_VM_RECOVERY) {
			d2dSb.append("-").append(jobId);
		}else if(jobType == JobType.JOBTYPE_START_INSTANT_VM || jobType == JobType.JOBTYPE_STOP_INSTANT_VM){
			d2dSb.append("-").append(jobUUID);
		}else if (agentId == 0 && (jobType == JobType.JOBTYPE_RPS_DATA_SEEDING 
				|| jobType == JobType.JOBTYPE_RPS_DATA_SEEDING_IN || jobType == JobType.JOBTYPE_RPS_PURGE_DATASTORE)) {
			d2dSb.append("-").append(jobUUID);
		}
		if(allJobStatusMap.containsKey(d2dSb.toString())){
			logger.debug("removeCacheJobMonitorByHistory D2DCacheRemove begin " + "key="+d2dSb.toString()
					+" allJobStatusMap.size="+allJobStatusMap.size()
					+" delayQueue.size="+delayQueue.size()
					);
			allJobStatusMap.remove(d2dSb.toString());
			delayQueue.remove(new DelayJobs<D2DJobStatusPair<String, FlashJobMonitor>>(new D2DJobStatusPair<String, FlashJobMonitor>(d2dSb.toString(), new FlashJobMonitor()), NANOTIME));	
			logger.debug("removeCacheJobMonitorByHistory D2DCacheRemove end " + "key="+d2dSb.toString()
					+" allJobStatusMap.size="+allJobStatusMap.size()
					+" delayQueue.size="+delayQueue.size()
					);
		}
		// RPScache has this key
		StringBuilder rpsSb = new StringBuilder();
		if (history.getProductType()==JobHistoryProductType.D2D.getValue()) {			
			rpsSb.append("RPS").append("-").append(rpsId).append("-").append(serverId).append("-").append(agentId)
			.append("-").append(jobType)
			.append("-").append(jobId);
		} else {
			rpsSb.append("RPS").append("-").append(serverId).append("-").append(serverId).append("-").append(agentId)
			.append("-").append(jobType)
			.append("-").append(jobId);
		}
			
		if (jobType == JobType.JOBTYPE_VM_RECOVERY) {
			rpsSb.append("-").append(jobId);
		} else if (agentId == 0 && (jobType == JobType.JOBTYPE_RPS_DATA_SEEDING 
				|| jobType == JobType.JOBTYPE_RPS_DATA_SEEDING_IN || jobType == JobType.JOBTYPE_RPS_PURGE_DATASTORE)) {
			rpsSb.append("-").append(jobUUID);
		} else if(jobType == JobType.JOBTYPE_START_INSTANT_VM || jobType == JobType.JOBTYPE_STOP_INSTANT_VM || jobType == JobType.JOBTYPE_LINUX_INSTANT_VM){
			d2dSb.append("-").append(jobUUID);
		}		
		if(allJobStatusMap.containsKey(rpsSb.toString())){
			logger.debug("removeCacheJobMonitorByHistory RPSCacheRemove begin " + "key="+d2dSb.toString()
					+" allJobStatusMap.size="+allJobStatusMap.size()
					+" delayQueue.size="+delayQueue.size()
					);
			allJobStatusMap.remove(rpsSb.toString());
			delayQueue.remove(new DelayJobs<D2DJobStatusPair<String, FlashJobMonitor>>(new D2DJobStatusPair<String, FlashJobMonitor>(rpsSb.toString(), new FlashJobMonitor()), NANOTIME));	
			logger.debug("removeCacheJobMonitorByHistory RPSCacheRemove end " + "key="+d2dSb.toString()
					+" allJobStatusMap.size="+allJobStatusMap.size()
					+" delayQueue.size="+delayQueue.size()
					);
		}

		// LinuxCache has this key
		StringBuilder linuxSb = new StringBuilder();
		linuxSb.append("LinuxD2D").append("-").append(agentId).append("-").append(serverId).append("-").append(jobType).append("-").append(jobUUID);
		if(allJobStatusMap.containsKey(linuxSb.toString())){
			logger.debug("removeCacheJobMonitorByHistory LinuxCacheRemove begin " + "key="+d2dSb.toString()
					+" allJobStatusMap.size="+allJobStatusMap.size()
					+" delayQueue.size="+delayQueue.size()
					);
			allJobStatusMap.remove(linuxSb.toString());
			delayQueue.remove(new DelayJobs<D2DJobStatusPair<String, FlashJobMonitor>>(new D2DJobStatusPair<String, FlashJobMonitor>(linuxSb.toString(), new FlashJobMonitor()), NANOTIME));	
			logger.debug("removeCacheJobMonitorByHistory LinuxCacheRemove end " + "key="+d2dSb.toString()
					+" allJobStatusMap.size="+allJobStatusMap.size()
					+" delayQueue.size="+delayQueue.size()
					);
		}
	}

	/**
	 * When some status of the job changed, we need update it to DB, so keep UI display the correct job monitor in Dashboard.
	 * oldValue is the job monitor already saved in the cache map.  jobMonitor is the newcoming job monitor from agent or rps
	 * We need update DB with new job monitor when the new job monitor satisfied below compare conditions .
	 * @param jobMonitor (new job monitor)
	 * @param oldValue (old job monitor with the same key)
	 * @return
	 */
	private boolean isAllowedToUpdate(FlashJobMonitor jobMonitor, FlashJobMonitor oldValue) {
		return (oldValue.getJobStatus() == JobStatus.JOBSTATUS_WAITING && jobMonitor.getJobStatus() != JobStatus.JOBSTATUS_WAITING)
				|| oldValue.getJobMethod() != jobMonitor.getJobMethod() || oldValue.getJobId() != jobMonitor.getJobId() 
				|| isPauseMergeJobToUpdate(jobMonitor, oldValue);
	}
	
	/**
	 * For merge job, when old merge job monitor is active and new merge job monitor is stopped, we should update it into DB.
	 * When merge job is paused, D2D/RPS will sync pause merge job to CPM, so that user could resume the merge job from UI. There are 4 problems here.
	 * #1 When merge job was paused, we should update job status from active to stopped in DB.
	 * #2 When merge job was paused, D2D/RPS will sync job history to CPM, then it will try to clean the corresponding job monitor in DB, but for merge job history with Stopped status, we should not do this, since the pause merge job monitor would be deleted.
	 * #3 When merge job was resumed, this merge job monitor will have another new job id, in this case we need find and update the previous merge job monitor in DB.
	 * #4 In dashboard progress group view, we should display pause merge job monitor(with Stopped job status), but for pause merge job history(with Stopped job status) in other group, we should not display the job monitor in detail panel.
	 * @param jobMonitor
	 * @param oldValue
	 * @return
	 */
	private boolean isPauseMergeJobToUpdate(FlashJobMonitor jobMonitor, FlashJobMonitor oldValue) {
		if (isMergeJob(jobMonitor)) {
			return jobMonitor.getJobStatus() == JobStatus.JOBSTATUS_STOP && oldValue.getJobStatus() == JobStatus.JOBSTATUS_ACTIVE;
		} else {			
			return false;
		}
	}
	
	/**
	 * Don't save the redirect job monitor from RPS server(except waiting backup job monitor), so in dash board we only 
	 * display the backup job monitor from D2D or running on RPS job.
	 * @param key (corresponding key in cache)
	 * @param jobMonitor (new job monitor)
	 * @return
	 */
	private boolean isAllowedToInsert(String key, FlashJobMonitor jobMonitor) {
		return jobMonitor.isRunningOnRPS() || (!jobMonitor.isRunningOnRPS() && !key.startsWith("RPS")) || jobMonitor.getJobStatus() == JobStatus.JOBSTATUS_WAITING;
	}
	
	public FlashJobMonitor get(String key) {
		return allJobStatusMap.get(key);
	}

	public List<FlashJobMonitor> getD2DJobStatusInfoList(List<String> nodeIdList) {
		List<FlashJobMonitor> infoList = new ArrayList<FlashJobMonitor>();
		for (String nodeId : nodeIdList) {
			if (allJobStatusMap.containsKey(nodeId)) {
				infoList.add(allJobStatusMap.get(nodeId));
			}
		}
		return infoList;
	}
	
	public List<String> getD2DRunningJobNodeIdList(List<String> nodeIdList) {
		List<String> idList = new ArrayList<String>();
		for (String nodeId : nodeIdList) {
			List<FlashJobMonitor> infoList = getJobStatusInfoList("D2D" + "-" + nodeId + "-");
			if(infoList != null && !infoList.isEmpty()){
				idList.add(nodeId);
			}
			List<FlashJobMonitor> linuxInfoList = getJobStatusInfoList("LinuxD2D" + "-" + nodeId + "-");
			if(linuxInfoList != null && !linuxInfoList.isEmpty()){
				idList.add(nodeId);
			}
		}
		return idList;
	}
	
	
	public List<FlashJobMonitor> getJobMonitorForDashboard(int productType, int nodeId, int rpsNodeId, long jobType, long jobId, String jobUUID) {
		List<FlashJobMonitor> list = new ArrayList<FlashJobMonitor>();
		FlashJobMonitor monitor = getMonitorFromD2DCache(nodeId, rpsNodeId, jobType, jobId);
		if (monitor != null) {			
			list.add(monitor);
		} else {
			monitor = getMonitorFromRPSCache(nodeId, rpsNodeId, jobType, jobId, jobUUID);
			if (monitor != null) {
				list.add(monitor);
			}else{
				monitor = getMonitorFromLinuxD2DCache(nodeId, rpsNodeId, jobType, jobId, jobUUID);
				if(monitor != null){
					list.add(monitor);
				}
			}
		}
		return list;
	}
	
	private FlashJobMonitor getMonitorFromD2DCache(int nodeId, int serverId, long jobType, long jobId) {
		StringBuilder sb = new StringBuilder();
		sb.append("D2D").append("-").append(nodeId).append("-").append(serverId).append("-").append(jobType);
		if (jobType == JobType.JOBTYPE_VM_RECOVERY) {
			sb.append("-").append(jobId);
		}
		return allJobStatusMap.get(sb.toString());
	}
	
	private FlashJobMonitor getMonitorFromLinuxD2DCache(int nodeId, int serverId, long jobType, long jobId, String jobUUID) {
		StringBuilder sb = new StringBuilder();
		sb.append("LinuxD2D").append("-").append(nodeId).append("-").append(serverId).append("-").append(jobType).append("-").append(jobUUID);
		return allJobStatusMap.get(sb.toString());
	}
	
	private FlashJobMonitor getMonitorFromRPSCache(int nodeId, int rpsNodeId, long jobType, long jobId, String jobUUID) {
		StringBuilder sb = new StringBuilder();
		sb.append("RPS").append("-").append(rpsNodeId).append("-").append(nodeId).append("-").append(jobType);
		if (jobType == JobType.JOBTYPE_VM_RECOVERY) {
			sb.append("-").append(jobId);
		} else if (nodeId == 0 && (jobType == JobType.JOBTYPE_RPS_DATA_SEEDING 
				|| jobType == JobType.JOBTYPE_RPS_DATA_SEEDING_IN || jobType == JobType.JOBTYPE_RPS_PURGE_DATASTORE)) {
			sb.append("-").append(jobUUID);
		}
		
		return allJobStatusMap.get(sb.toString());
	}
	
	@Deprecated
	public List<FlashJobMonitor> getJobStatusInfoList(String jobStatusKey) {
		List<FlashJobMonitor> list = new ArrayList<FlashJobMonitor>();
		Iterator<Map.Entry<String, FlashJobMonitor>> it = allJobStatusMap.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, FlashJobMonitor> entry = it.next();
			if (entry.getKey().startsWith(jobStatusKey)) {
				list.add(entry.getValue());
				if (entry.getValue().isFinished()) {
					it.remove();
//					removeRunningJobFromHistory(entry.getValue());
				}
			}
		}
		return list;
	}
}
