package com.ca.arcserve.edge.app.base.webservice.jobhistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeJobHistory;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao.JobHistoryProductType;
import com.ca.arcserve.edge.app.base.appdaos.IntegerId;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.IJobHistroryService;
import com.ca.arcserve.edge.app.base.webservice.action.ActionTaskManager;
import com.ca.arcserve.edge.app.base.webservice.contract.action.ActionTaskParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ServerDate;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.dashboard.DashboardFilterType;
import com.ca.arcserve.edge.app.base.webservice.contract.filter.BaseFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.CancelJobParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.DashboardSortCol;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryFilter4Dashboard;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobTypeForGroupByPlan;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHyperVHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DAllJobStatusCache;
import com.ca.arcserve.edge.app.base.webservice.monitor.ASBUJobMonitorReader;
import com.ca.arcserve.edge.app.base.webservice.monitor.D2DJobMonitorReader;
import com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitor;
import com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitorReaderFactory;
import com.ca.arcserve.edge.app.base.webservice.monitor.LinuxD2DJobMonitorReader;
import com.ca.arcserve.edge.app.base.webservice.monitor.RPSJobMonitorReader;
import com.ca.arcserve.edge.app.base.webservice.monitor.cache.ServerSourceTypeCache;
import com.ca.arcserve.edge.app.base.webservice.monitor.handle.JobMonitorHandlerManager;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail.SourceType;

public class JobHistoryServiceImpl implements IJobHistroryService {
	private static final Logger logger = Logger.getLogger(JobHistoryServiceImpl.class);
	private static IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private IEdgeJobHistoryDao jobHistoryDao = DaoFactory.getDao(IEdgeJobHistoryDao.class);
	
	
	private static final BlockingQueue<JobDetail> blockingQueueForLinux = new PriorityBlockingQueue<JobDetail>();
	
	private static final BlockingQueue<JobDetail> blockingQueueForASBU = new PriorityBlockingQueue<JobDetail>();
	
	private static final BlockingQueue<JobDetail> blockingQueueForAgent = new PriorityBlockingQueue<JobDetail>();
	
	private static final BlockingQueue<JobDetail> blockingQueueForRPS = new PriorityBlockingQueue<JobDetail>();

	private static final ConcurrentMap<Long, FlashJobMonitor> HISTORY_MONITOR_MAP = new ConcurrentHashMap<Long, FlashJobMonitor>();
	
	private static QueryJobHistorysScheduler historySchedulerTimer;
	
	private void ensureValid(JobHistoryPagingConfig config, JobHistoryFilter filter) {
			
		if (config.getCount() < 1) {
			config.setCount(1);
		}
		
		if (config.getStartIndex() < 0) {
			config.setStartIndex(0);
		}
		
		if (filter.getJobStatus() == null) {
			filter.setJobStatus(JobStatus.All);
		}
		
	}
	
	private JobHistoryPagingResult getJobHistoryList(JobHistoryProductType type, int targetOrServerId, JobHistoryPagingConfig config, JobHistoryFilter filter) throws EdgeServiceFault {
		ensureValid(config, filter);
		int[] totalCount = new int[1];
		List<EdgeJobHistory> jobHistroy = new ArrayList<EdgeJobHistory>();
		jobHistoryDao.as_edge_d2dJobHistory_getPagingList(type.getValue(), targetOrServerId, filter.getServerId(), filter.getJobId(),filter.getJobStatus().getValue(), filter.getDatastoreUUID(),
				config.getStartIndex(), config.getCount(), 
				totalCount, jobHistroy);
		
		JobHistoryPagingResult retval = new JobHistoryPagingResult();

		retval.setStartIndex(config.getStartIndex());
		retval.setTotalCount(totalCount[0]);
		retval.setData(this.toContract(jobHistroy,true));

		return retval;
	}
	
	@Override
	public JobHistoryPagingResult getDashboardJobHistoryList(JobHistoryPagingConfig config, JobHistoryFilter4Dashboard filter)throws EdgeServiceFault {
		int[] totalCount = new int[1];
		List<EdgeJobHistory> jobHistroy = new ArrayList<EdgeJobHistory>();
		JobHistoryPagingResult retval = new JobHistoryPagingResult();
		
		if (filter.getJobTimeFilter().getType() == 1) {
			jobHistoryDao.as_edge_d2dJobHistory_getLastJobHistory(filter.getJobStatusGroup().getValue(),
					config.getStartIndex(), config.getCount(), config.getOrderType().value(), config.getSortCol().value(), totalCount, jobHistroy);
			retval.setStartIndex(config.getStartIndex());
			retval.setTotalCount(totalCount[0]);
			retval.setData(this.toContract(jobHistroy,true));
		} else {
			Date time;
			if (filter.getJobStatusGroup() == DashboardFilterType.JobsInProgress) {
				time = CommonUtil.getSomeDate(1970, 1, 1);
			} else {
				time = getServerDate(filter.getJobTimeFilter());
			}
			jobHistoryDao.as_edge_d2dJobHistory_getJobHistory(filter.getJobStatusGroup().getValue(), time,
					config.getStartIndex(), config.getCount(), config.getOrderType().value(), config.getSortCol().value(), totalCount, jobHistroy);
			retval.setStartIndex(config.getStartIndex());
			retval.setTotalCount(totalCount[0]);
			retval.setData(this.toContract(jobHistroy,true));
		}
		if (filter.getJobStatusGroup() == DashboardFilterType.JobsInProgress) {
			// notify ASBU/D2D/RPS/linuxD2D to start Thread loading cache
			try {
				notifyJobsCache(retval.getData());
			} catch (InterruptedException e) {
				logger.error("getDashboardJobHistoryList catch InterruptedException ",e);
			}
		}
		if (retval.getData() != null) {
			Iterator<JobHistory> it = retval.getData().iterator();
			while(it.hasNext()) {
				JobHistory jobHistory = it.next();
				if (jobHistory.getNodeName() == null || jobHistory.getNodeName().equals("")) {
					it.remove();
				}
			}
			
		}
		
		return retval;
	}
	
	
	public static void shutdownJobsReaderThread(){
		ASBUJobMonitorReader.shutdonwReaderThread();
		D2DJobMonitorReader.shutdonwThreadReader();
		LinuxD2DJobMonitorReader.shutdonwReaderThread();
		RPSJobMonitorReader.shutdonwReaderThread();
	}
	
	private void populateJobDetailPriority(JobDetail jobDetail){
		if(jobDetail==null||jobDetail.getHistorysList()==null||jobDetail.getHistorysList().isEmpty()){
			logger.debug("populateServerPriority jobDetail==null "+jobDetail.toString());
			return;
		}
		int allSize = jobDetail.getHistorysList().size();
		int index = 0;
		boolean loadFLg = false;
		for (int i = 0; i < allSize; i++) {
			JobHistory history = jobDetail.getHistorysList().get(i);
			if(HISTORY_MONITOR_MAP.containsKey(history.getHistoryId())){
				loadFLg = true;
				index++;
				logger.debug("populateServerPriority HISTORY_MONITOR_MAP.containsKey index="+index+" "+history.toString());
				break;
			}
			index++;
		}
		// noLoad any history set HighPriority
		if(!loadFLg){
			logger.debug("populateServerPriority jobDetail.setPriotity(1) "+jobDetail.toString());
			jobDetail.setPriotity(1);
		}else{
			// Load all history set LowPriority
			if(index==allSize){
				logger.debug("populateServerPriority jobDetail.setPriotity(3) "+jobDetail.toString());
				jobDetail.setPriotity(3);
			}
			// Load all history set MiddlePriority
			else{
				logger.debug("populateServerPriority jobDetail.setPriotity(2) "+jobDetail.toString());
				jobDetail.setPriotity(2);
			}
		}
	}
	
	private void notifyJobsCache(List<JobHistory> historys) throws InterruptedException{
		if(historySchedulerTimer==null)
			historySchedulerTimer = new QueryJobHistorysScheduler(HISTORY_MONITOR_MAP);
		
		if (historys==null||historys.size()<=0) {
			logger.debug("notifyJobsCache clear cache");
			ServerSourceTypeCache.getInstance().clear();
			HISTORY_MONITOR_MAP.clear();
			return;		
		}
		// start JobMonitorReader Thread
		ASBUJobMonitorReader.initReaderThread(blockingQueueForASBU);
		D2DJobMonitorReader.initReaderThread(blockingQueueForAgent);
		LinuxD2DJobMonitorReader.initReaderThread(blockingQueueForLinux);
		RPSJobMonitorReader.initReaderThread(blockingQueueForRPS);
		
		logger.debug("notifyJobsCache historys.size="+(historys==null?0:historys.size()));
		// group runningServerId and set its HistoryList
		Map<String,JobDetail> serverMap = getRunningServersForNode(historys);
		if(serverMap==null||serverMap.size()<=0){
			logger.debug("notifyJobsCache after getRunningServersForNode serverMap==null");
			return;
		}
		for (Iterator<String> iterator = serverMap.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			JobDetail jobDetail = serverMap.get(key);
			switch (jobDetail.getSource()) {
			case ASBU:
				if(!blockingQueueForASBU.contains(jobDetail)){
					populateJobDetailPriority(jobDetail);
					logger.debug("notifyJobsCache put into blockingQueueForASBU : "+jobDetail.toString());
					blockingQueueForASBU.put(jobDetail);
				}
				 break;
			case D2D:
				if(!blockingQueueForAgent.contains(jobDetail)){
					populateJobDetailPriority(jobDetail);
					logger.debug("notifyJobsCache put into blockingQueueForAgent : "+jobDetail.toString());
					blockingQueueForAgent.put(jobDetail);
				}
				 break;
			case RPS:				
				if(!blockingQueueForRPS.contains(jobDetail)){
					populateJobDetailPriority(jobDetail);
					logger.debug("notifyJobsCache put into blockingQueueForRPS : "+jobDetail.toString());
					blockingQueueForRPS.put(jobDetail);
				}
				break;
			case LINUXD2D:
				if(!blockingQueueForLinux.contains(jobDetail)){
					populateJobDetailPriority(jobDetail);
					logger.debug("notifyJobsCache put into blockingQueueForLinux : "+jobDetail.toString());
					blockingQueueForLinux.put(jobDetail);
				}
				break;
			case NO_TYPE:
			default:
				logger.debug("notifyJobsCache get Error ServerSourceTYPE "+jobDetail.toString());
				break;
			}	
		}		
		/*
		// start JobMonitorReader Thread
		ASBUJobMonitorReader.initReaderThread(blockingQueueForASBU,HISTORY_MONITOR_MAP);
		D2DJobMonitorReader.initReaderThread(blockingQueueForAgent,HISTORY_MONITOR_MAP);
		LinuxD2DJobMonitorReader.initReaderThread(blockingQueueForLinux,HISTORY_MONITOR_MAP);
		RPSJobMonitorReader.initReaderThread(blockingQueueForRPS,HISTORY_MONITOR_MAP);
		for(int idx=0; idx < historys.size() ; idx++){			
			JobHistory history = historys.get(idx);
			logger.debug("notifyJobsCache printHistory "+history.toString());
			if(history.getJobStatus()==JobStatus.Waiting){
				if(Integer.parseInt(history.getTargetRPSId())!=0){	
					if(!blockingQueueForRPS.contains(history)){
						logger.debug("notifyJobsCache put into blockingQueueForRPS RPSWaiting: "+history.toString());
						JobHistory history2 = JobHistory.clone(history);
						history2.setVersion("RPSWaiting");// used for token WaitJob 
						blockingQueueForRPS.put(history2);		
					}	
					if(Integer.parseInt(history.getTargetRPSId())==Integer.parseInt(history.getServerId())){						
						continue;
					}
				}
			}			
			if(Integer.parseInt(history.getServerId())==0){
				logger.error("notifyJobsCache catch history serverId=0 "+history.toString());
				continue;
			} 
			SourceType type = ServerSourceTypeCache.getInstance()
					.getHostSoureType(Integer.parseInt(history.getServerId()));
			switch (type) {
			case ASBU:
				if(!blockingQueueForASBU.contains(history)){
					logger.debug("notifyJobsCache put into blockingQueueForASBU : "+history.toString());
					blockingQueueForASBU.put(history);
				}
				 break;
			case D2D:
				if(!blockingQueueForAgent.contains(history)){
					logger.debug("notifyJobsCache put into blockingQueueForAgent : "+history.toString());
					blockingQueueForAgent.put(history);
				}
				 break;
			case RPS:				
				if(!blockingQueueForRPS.contains(history)){
					logger.debug("notifyJobsCache put into blockingQueueForRPS : "+history.toString());
					blockingQueueForRPS.put(history);
				}
				break;
			case LINUXD2D:
				if(!blockingQueueForLinux.contains(history)){
					logger.debug("notifyJobsCache put into blockingQueueForLinux : "+history.toString());
					blockingQueueForLinux.put(history);
				}
				break;
			}			
		}	*/	
		
	}
	
	public static void cacheGlobalMonitorMap(List<JobHistory> historys, List<FlashJobMonitor> monitors){		
		if (historys==null || historys.isEmpty())
			return;
		if (monitors==null || monitors.isEmpty())
			return;	
		for(JobHistory history : historys){
			for (FlashJobMonitor monitor : monitors) {	
				if(!compareHistoryAndMonitor(monitor, history,true)){
					continue;
				}	
				logger.debug("cacheGlobalMonitorMap "+history.toString()+" "+monitor.toString());
				HISTORY_MONITOR_MAP.put(history.getHistoryId(), monitor);
				break;
			}	
		}
	}
	
	public static void removeGlobalMonitorMap(Long historyId){		
		HISTORY_MONITOR_MAP.remove(historyId);
	}
	
	private Date getServerDate(BaseFilter dashboardFilter) {
		Date date = new Date();
		if (dashboardFilter.getType() == 2) {
			if (dashboardFilter.getUnit() == 1) {
				date = CommonUtil.getLastMinutes(dashboardFilter.getAmount());
			} else if (dashboardFilter.getUnit() == 2) {
				date = CommonUtil.getLastHours(dashboardFilter.getAmount());
			} else {
				date = CommonUtil.getLastDays(dashboardFilter.getAmount());
			}
		} else {
			date = CommonUtil.toDate(dashboardFilter.getServerTimeStemp());				
		}
		return date;
	}
	
	private List<JobHistory> toContract(List<EdgeJobHistory> jobHistorys, Boolean needSetNodeName) {
		IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
		IEdgeHyperVDao edgeHypervDao= DaoFactory.getDao(IEdgeHyperVDao.class);
		List<EdgeHyperVHostMapInfo> hostMapInfoV = new ArrayList<EdgeHyperVHostMapInfo>();
		edgeHypervDao.as_edge_hyperv_host_map_list(hostMapInfoV);
		List<JobHistory> retval = new ArrayList<JobHistory>();
		for (EdgeJobHistory jobHistory : jobHistorys) {
			if(jobHistory==null)
				continue;
			JobHistory jh = new JobHistory();
			
			jh.setHistoryId(jobHistory.getId());
			jh.setVersion(jobHistory.getVersion());
			jh.setProductType(jobHistory.getProductType());
			jh.setJobId(jobHistory.getJobId());
			jh.setJobMethod(jobHistory.getJobMethod());
			jh.setJobType(jobHistory.getJobType());
			jh.setJobStatus(JobStatus.parse(jobHistory.getJobStatus()));
			jh.setJobUTCStartDate(jobHistory.getJobUTCStartTime());
			jh.setJobLocalStartDate(jobHistory.getJobLocalStartTime());
			jh.setJobUTCEndDate(jobHistory.getJobUTCEndTime());
			jh.setJobLocalEndDate(jobHistory.getJobLocalEndTime());
			jh.setServerId(jobHistory.getServerId());
			jh.setAgentId(jobHistory.getAgentId());
			jh.setSourceRPSId(jobHistory.getSourceRPSId());
			jh.setTargetRPSId(jobHistory.getTargetRPSId());
			jh.setAgentUUID(jobHistory.getAgentUUID()); 
			jh.setSourceDataStoreUUID(jobHistory.getSourceDataStoreUUID());
			jh.setTargetDataStoreUUID(jobHistory.getTargetDataStoreUUID());
			jh.setPlanName(jobHistory.getName()); 	//name - planName
			jh.setPlanId(jobHistory.getPlanId());
			jh.setPlanUuid(jobHistory.getPlanUuid());
			jh.setNodeId(jobHistory.getNodeId());
			jh.setServerName(jobHistory.getServerName());
			jh.setJobUUID(jobHistory.getJobUUID());
			jh.setAgentNodeName(jobHistory.getAgentNodeName());
			jh.setServerNodeName(jobHistory.getServerNodeName());
			// JobHistory success/cancel/fail 
			if (jobHistory.getAgentName() != null && !"".equals(jobHistory.getAgentName())) {
				jh.setNodeName(jobHistory.getAgentName());
			} else { // job in progress
				if(!needSetNodeName){
					retval.add(jh);
					continue;
				}
				List<EdgeEsxVmInfo> vmInfos = new LinkedList<>();
				// get Node/vm 's hostName and vmName
				esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(Integer.valueOf(jobHistory.getAgentId()), vmInfos);
				if(!vmInfos.isEmpty() && vmInfos.get(0)!=null){
					String hostName = vmInfos.get(0).getHostName();
					if(hostName!=null && (!hostName.isEmpty())){
						jh.setNodeName(hostName);
					} 
					if(jh.getNodeName()==null||jh.getNodeName().isEmpty()){
						String vmName = vmInfos.get(0).getVmName();
						if(vmName!=null && (!vmName.isEmpty())){
							if (jobHistory.getAgentNodeName() != null && !"".equals(jobHistory.getAgentNodeName())) {
								if(jobHistory.getAgentNodeName().equals(vmName)){
									jh.setNodeName(EdgeCMWebServiceMessages.getMessage("unknown_vm", vmName));
								} else {
									jh.setNodeName(jobHistory.getAgentNodeName());
								}
							} else {
								jh.setNodeName(EdgeCMWebServiceMessages.getMessage("unknown_vm", vmName));
							}	
						}
					}									
//					jh.setEmptyNodeName(true);
				} 
				if(jh.getNodeName()==null||jh.getNodeName().isEmpty()){
					for (EdgeHyperVHostMapInfo info : hostMapInfoV) {
						if (info.getHostId() == Integer.valueOf(jobHistory.getAgentId())) {							
							String vmName = info.getVmName();
							if (jobHistory.getAgentNodeName() != null && !"".equals(jobHistory.getAgentNodeName())) {
								if(jobHistory.getAgentNodeName().equals(vmName)){
									jh.setNodeName(EdgeCMWebServiceMessages.getMessage("unknown_vm", vmName));
								} else {
									jh.setNodeName(jobHistory.getAgentNodeName());
								}
							} else {
								jh.setNodeName(EdgeCMWebServiceMessages.getMessage("unknown_vm", vmName));
							}	
//							jh.setEmptyNodeName(true);
							break;
						}
					}
				}				
			}
			retval.add(jh);
		}

		return retval;
	}
	
	@Override
	public JobHistoryPagingResult getD2DJobHistoryList(int nodeId, JobHistoryPagingConfig config, JobHistoryFilter filter) throws EdgeServiceFault {
		return getJobHistoryList(JobHistoryProductType.D2D, nodeId, config, filter);
	}
	
	@Override
	public JobHistoryPagingResult getASBUJobHistoryList(int nodeId, JobHistoryPagingConfig config, JobHistoryFilter filter) throws EdgeServiceFault {
		return getJobHistoryList(JobHistoryProductType.ASBU, nodeId, config, filter);
	}

	@Override
	public void deleteAllD2DJobHistorys(int nodeId) throws EdgeServiceFault {
		jobHistoryDao.as_edge_d2dJobHistory_deleteAll(JobHistoryProductType.D2D.getValue() , nodeId);
	}

	@Override
	public void deleteOldD2DJobHistorys(int nodeId, ServerDate serverDate) throws EdgeServiceFault {
		Date date = CommonUtil.toDate(serverDate);
		jobHistoryDao.as_edge_d2dJobHistory_deleteOld(JobHistoryProductType.D2D.getValue(), nodeId, date);
	}

	@Override
	public JobHistoryPagingResult getRpsJobHistoryList(int nodeId, JobHistoryPagingConfig config, JobHistoryFilter filter) throws EdgeServiceFault {
		return getJobHistoryList(JobHistoryProductType.RPS, nodeId, config, filter);
	}

	@Override
	public void deleteAllRpsJobHistorys(int nodeId) throws EdgeServiceFault {
		jobHistoryDao.as_edge_d2dJobHistory_deleteAll(JobHistoryProductType.RPS.getValue(), nodeId);
	}

	@Override
	public void deleteOldRpsJobHistorys(int nodeId, ServerDate serverDate) throws EdgeServiceFault {
		Date date = CommonUtil.toDate(serverDate);
		jobHistoryDao.as_edge_d2dJobHistory_deleteOld(JobHistoryProductType.RPS.getValue(), nodeId, date);
	}
	
	@Override
	public List<PolicyInfo> getPlans(JobHistoryFilter4Dashboard filter) throws EdgeServiceFault {
		
		List<EdgeJobHistory> jobHistories = new ArrayList<EdgeJobHistory>();
		boolean recent = filter.getJobTimeFilter().getType() == 1;
		int group = filter.getJobStatusGroup().getValue();
		Date time = getFilterTime( filter.getJobTimeFilter() );
		
		if (recent) {
			jobHistoryDao.as_edge_d2dJobHistory_getLastPlans(group, time, jobHistories);
		} else {			
			jobHistoryDao.as_edge_d2dJobHistory_getPlans(group, time, jobHistories);
		}
		
		List<PolicyInfo> plans = new ArrayList<PolicyInfo>();
		for (EdgeJobHistory history : jobHistories) {
			PolicyInfo info = new PolicyInfo();
			
			info.setPolicyUuid(history.getPlanUuid());
			info.setPolicyName(history.getName());
			
			plans.add(info);
		}
		
		boolean[] onDemandJobExist = new boolean[1];
		jobHistoryDao.as_edge_d2dJobHistory_isOnDemandJobExists(group, recent, time, onDemandJobExist);
		if (onDemandJobExist[0]) {
			PolicyInfo onDemandPlan = new PolicyInfo();
			onDemandPlan.setPolicyUuid(null);
			onDemandPlan.setPolicyName("Others");
			plans.add(onDemandPlan);
		}
		
		return plans;
	}

	@Override
	public List<JobTypeForGroupByPlan> getJobTypes(JobHistoryFilter4Dashboard filter) throws EdgeServiceFault {
		
		List<EdgeJobHistory> jobHistories = new ArrayList<EdgeJobHistory>();
		boolean recent = filter.getJobTimeFilter().getType() == 1;
		int group = filter.getJobStatusGroup().getValue();
		Date time = getFilterTime( filter.getJobTimeFilter() );
		
		if (filter.getPlanUUID() == null) {
			jobHistoryDao.as_edge_d2dJobHistory_getOnDemandJobTypes(group, recent, time, jobHistories);
		} else if (recent) {
			jobHistoryDao.as_edge_d2dJobHistory_getLastJobTypes(group, time, filter.getPlanUUID(), jobHistories);
		} else {
			jobHistoryDao.as_edge_d2dJobHistory_getJobTypes(group, time, filter.getPlanUUID(), jobHistories);
		}
		
		List<JobTypeForGroupByPlan> jobTypes = new ArrayList<JobTypeForGroupByPlan>();
		for (EdgeJobHistory history : jobHistories) {
			JobTypeForGroupByPlan jobTypeForPlan = new JobTypeForGroupByPlan();
			jobTypeForPlan.setJobType(history.getJobType());
			filter.setJobType(history.getJobType());
			jobTypes.add(jobTypeForPlan);
		}
		
		return jobTypes;
	}
	
	private Date getFilterTime( BaseFilter filter )
	{
		Date time;
		
		if (filter.getType() == 1) {
			time = CommonUtil.getSomeDate(1970, 1, 1);
		} else {			
			time = getServerDate(filter);
		}
		
		return time;
	}

	@Override
	public JobHistoryPagingResult getJobHistories(JobHistoryFilter4Dashboard filter) throws EdgeServiceFault {
		
		List<EdgeJobHistory> jobHistories = new ArrayList<EdgeJobHistory>();
		boolean recent = filter.getJobTimeFilter().getType() == 1;
		int group = filter.getJobStatusGroup().getValue();
		Date time = getFilterTime( filter.getJobTimeFilter() );
		
		JobHistoryPagingConfig pagingConfig = filter.getPagingConfig();
		if (pagingConfig == null)
		{
			pagingConfig = new JobHistoryPagingConfig();
			pagingConfig.setStartIndex( 0 );
			pagingConfig.setCount( -1 );
			pagingConfig.setOrderType( EdgeSortOrder.ASC );
			pagingConfig.setSortCol( DashboardSortCol.jobUTCStartTime );
		}
		
		int[] totalCount = new int[1];
		
		if (filter.getPlanUUID() == null) {
			jobHistoryDao.as_edge_d2dJobHistory_getOnDemandJobHistories(
				group, recent, time, filter.getJobType(),
				pagingConfig.getStartIndex(), pagingConfig.getCount(),
				(pagingConfig.getOrderType() == EdgeSortOrder.ASC) ? 0 : 1,
				pagingConfig.getSortCol().value(),
				totalCount, jobHistories);
		} else if (recent) {
			jobHistoryDao.as_edge_d2dJobHistory_getLastJobHistories(
				group, filter.getPlanUUID(), filter.getJobType(),
				pagingConfig.getStartIndex(), pagingConfig.getCount(),
				(pagingConfig.getOrderType() == EdgeSortOrder.ASC) ? 0 : 1,
				pagingConfig.getSortCol().value(),
				totalCount, jobHistories);
		} else {
			jobHistoryDao.as_edge_d2dJobHistory_getJobHistories(
				group, time, filter.getPlanUUID(), filter.getJobType(),
				pagingConfig.getStartIndex(), pagingConfig.getCount(),
				(pagingConfig.getOrderType() == EdgeSortOrder.ASC) ? 0 : 1,
				pagingConfig.getSortCol().value(),
				totalCount, jobHistories);
		}
		
		JobHistoryPagingResult result = new JobHistoryPagingResult();
		result.setStartIndex( pagingConfig.getStartIndex() );
		result.setCount( pagingConfig.getCount() );
		result.setData( toContract(jobHistories,true) );
		result.setTotalCount( totalCount[0] );
		return result;
	}
	
	@Override
	public JobHistoryPagingResult getLinuxD2DJobHistoryList(int nodeId, JobHistoryPagingConfig config, JobHistoryFilter filter) throws EdgeServiceFault {
		return getJobHistoryList(JobHistoryProductType.LinuxD2D, nodeId, config, filter);
	}
	
	@Override
	@Deprecated
	public List<JobMonitor> findRemoteJobMonitor(JobDetail jobDetail) throws EdgeServiceFault{
		List<JobMonitor> remoteJobMonitors = new ArrayList<>();
		try{
			if(jobDetail.getServerId() == 0){
				logger.debug("server id is 0, it a agent level job monitor search, need get server id by node uuid");
				List<IntegerId> ids = new ArrayList<>();
				jobHistoryDao.getJobMonitorServerIdByJobTypeAndNodeId(jobDetail.getJobType(), jobDetail.getNodeId(), ids);
				if(ids.size() > 0){
					logger.debug("search result, server id is " + ids.get(0).getId());
					jobDetail.setServerId(ids.get(0).getId());
				}
			}
			if(jobDetail.getServerId() > 0){
				remoteJobMonitors = JobMonitorReaderFactory.getReader(jobDetail.getSource()).getJobMonitor(jobDetail);	
		
				List<JobHistory> historyList; 
				if(jobDetail.getNodeId()>0){
					logger.debug("findRemoteJobMonitor jobDetail.getNodeId()="+jobDetail.getNodeId());
					historyList = getJobMonitorsFromDB(jobDetail.getNodeId(), -1, "");
				}else{ 
					logger.debug("findRemoteJobMonitor jobDetail.getServerId()="+jobDetail.getServerId());
					historyList = getJobMonitorsFromDB(-1, jobDetail.getServerId(), "");
				}
				logger.debug("findRemoteJobMonitor remoteJobMonitors.size="+(remoteJobMonitors==null?"0":remoteJobMonitors.size())
						+ ", historyList.size="+(historyList==null?"0":historyList.size()));
				for (JobHistory history:historyList) {
					if (history.getJobType() != JobType.JOBTYPE_ARCHIVE_TO_TAPE) 
						continue;
					logger.debug("findRemoteJobMonitor History= "+history.toString());	
					boolean findflg = false;
					String historyAgent = history.getAgentUUID()==null?"":history.getAgentUUID();
					for (JobMonitor monitor : remoteJobMonitors) {							
						if(!historyAgent.contains(monitor.getNodeUUID()))
							continue;
						logger.debug("findRemoteJobMonitor find corresponding's history,no need to delete this history");
						findflg = true;
						break;
					}
					// history no monitor corresponding will be delete
					if (!findflg) {
						logger.info("findRemoteJobMonitor delete no corresponding's history: jobtype="+ history.getJobType()
								+ ",jobId="+ history.getJobId()+ ",agentId="+ history.getAgentId()+ ",serverId="+ history.getServerId());
						jobHistoryDao.as_edge_d2dJobHistory_monitor_delete(history.getJobId(), history.getJobType(),
								Integer.parseInt(history.getAgentId()),Integer.parseInt(history.getServerId()));
						for (JobMonitor monitor:remoteJobMonitors) {
							logger.info("findRemoteJobMonitor delete no corresponding's history monitor[jobtype:"+ monitor.getJobType()
									+",jobid:"+monitor.getJobId()+",serverid:"+monitor.getServerId()+",nodeUUID:"+monitor.getNodeUUID()
									+",nodeName:"+monitor.getNodeName());
						}
					}	
				}
			}
		}catch(Exception e){
			logger.error("get job monitor fail, ", e);
		}
		return remoteJobMonitors;
	}
	
	@Override
	public List<JobMonitor> findJobMonitors(JobDetail jobDetail) throws EdgeServiceFault {
		List<JobMonitor> monitors = new ArrayList<>();
		if(selectNodeLevel(jobDetail)){
			jobHistoryDao.selectJobMonitorByJobTypeAndNodeId(jobDetail.getJobType(), jobDetail.getNodeId(), monitors);
		}else{
			jobHistoryDao.selectJobMonitorByJobTypeAndServerId(jobDetail.getJobType(), jobDetail.getServerId(), monitors);
		}
		logger.debug("find job monitor's count is " + monitors.size());
		for(JobMonitor monitor : monitors){
			logger.debug("node uuid is " + monitor.getNodeUUID());
			logger.debug("findJobMonitors monitor[jobid="+monitor.getJobId() +"; type="+monitor.getJobType()
					+"; serverId="+monitor.getServerId()
					+"; nodeName="+monitor.getNodeName()+" ]");
		}
		return monitors;
	}

	private boolean selectNodeLevel(JobDetail jobDetail) {
		return jobDetail.getNodeId() > 0 && jobDetail.getServerId() == 0;
	}

	@Override
	public void createJobMonitor(JobMonitor jobMonitor) throws EdgeServiceFault {
//		JobMonitorTrackerFactory.getTracker(jobMonitor.getSource()).checkJobStarted(jobMonitor);
		try{
			DaoFactory.beginTrans();
			jobHistoryDao.as_edge_d2dJobHistory_monitor_insert(Long.valueOf(jobMonitor.getJobId()), jobMonitor.getJobType(), jobMonitor.getJobMethod(), jobMonitor.getJobStatus(), Integer.valueOf(jobMonitor.getNodeUUID()), jobMonitor.getServerId(), 0l, 0l, jobMonitor.getStartTime(), jobMonitor.getProductType(), 0l, jobMonitor.getPlanUUID(), "", "", "", "", jobMonitor.getNodeUUID());
			DaoFactory.commitTrans();
		}catch(Exception e){
			logger.error("insert job monitor record fail", e);
			DaoFactory.rollbackTrans();
		}
		
	}

	@Override
	public void deleteJobMonitor(JobMonitor jobMonitor) throws EdgeServiceFault {

	}

	@Override
	public boolean cancelJobForEdge(JobDetail jobDetail) throws EdgeServiceFault {
		return JobMonitorHandlerManager.cancelJob(jobDetail);
	}
	
	private void syncHistoryAndMonitor(List<JobHistory> historys, List<FlashJobMonitor> monitors,boolean isDelete){		
		int historySize = (historys==null)?0:historys.size();
		int monitorSize = (monitors==null)?0:monitors.size();
		if(historySize>monitorSize)
			logger.debug("syncHistoryAndMonitor historySize=" + historySize+" monitorSize=" + monitorSize);
		
		if (historys==null || historys.isEmpty())
			return;
		if(monitors == null)
			monitors = new ArrayList<FlashJobMonitor>();	
	
		for(JobHistory history : historys){
			boolean findflg = false;			
			for (FlashJobMonitor monitor : monitors) {	
				if(!compareHistoryAndMonitor(monitor, history,true)){
					continue;
				}						
				findflg = true;
				break;
			}
			// history no monitor corresponding will be delete
			if (!findflg && isDelete) {
				logger.info("syncHistoryAndMonitor delete no corresponding's history:"+ history.toString());
				jobHistoryDao.as_edge_d2dJobHistory_monitor_delete(history.getJobId(), history.getJobType(),
						Integer.parseInt(history.getAgentId()),Integer.parseInt(history.getServerId()));
				D2DAllJobStatusCache.getD2DAllJobStatusCache().removeCacheJobMonitorByHistory(history);
			}		
		}
	}
	
	private List<JobHistory> getJobMonitorsFromDB(int agentId,int serverId,String dataStoreUUID) throws EdgeServiceFault {
		List<EdgeJobHistory> lstJobHistory = new ArrayList<EdgeJobHistory>();
		jobHistoryDao.as_edge_d2dJobHistory_monitor_getJobMonitor(agentId, serverId, dataStoreUUID, lstJobHistory);
		List<JobHistory> retval = this.toContract(lstJobHistory,false);
		for(JobHistory his:retval){
			logger.debug(his.toString());
		}
		return retval;		
	}
	
	private List<EdgeJobHistory> getJobMonitorsFromDBByJobType(long jobType) throws EdgeServiceFault {
		List<EdgeJobHistory> lstJobHistory = new ArrayList<EdgeJobHistory>();
		jobHistoryDao.selectJobMonitorByJobType(jobType, lstJobHistory);
		return lstJobHistory;		
	}
	
    public Map<Integer, String> checkRunningVMJobForHbbuPlan(List<Integer> vmIDs, int serverId) throws EdgeServiceFault{
    	logger.debug("checkRunningVMJobForHbbuPlan, proxyId = " + serverId );	
    	if(vmIDs == null || vmIDs.isEmpty())
    		return null;
    	List<EdgeJobHistory> jmList= getJobMonitorsFromDBByJobType(JobType.JOBTYPE_VM_BACKUP);
    	if(jmList == null)
			return null;
    	Map<String, Integer> vmMap = new HashMap<String, Integer> ();
    	for(Integer vmId : vmIDs){
    		vmMap.put(String.valueOf(vmId), vmId);
    	}
    	Map<Integer, String> vmMap_RunningJob = new HashMap<Integer, String> ();
		for(EdgeJobHistory jm : jmList){
			if(jm == null)
				continue;
			logger.debug("checkRunningVMJobForHbbuPlan, proxyId = " + serverId + "jobId = " + jm.getJobId());
			if(jm.getAgentId() == null){
				logger.debug("checkRunningVMJobForHbbuPlan agentId is null, proxyId = " + serverId);
				continue;
			}
			if(jm.getServerId() == null || jm.getServerId().equalsIgnoreCase("0")){// serverId is 0 or null, continue, issue 765040
				logger.debug("checkRunningVMJobForHbbuPlan serverId is 0 or null, proxyId = " + serverId);		
				continue;
			}
			if(String.valueOf(serverId).equalsIgnoreCase(jm.getServerId())){// not change proxy
				logger.debug("checkRunningVMJobForHbbuPlan not change proxy, proxyId = " + serverId);		
				continue;
			}
			if(vmMap.containsKey(jm.getAgentId())){
				vmMap_RunningJob.put(vmMap.get(jm.getAgentId()), jm.getServerNodeName());
			}
		}
		return vmMap_RunningJob;
    }
		
	@Override
	public List<FlashJobMonitor> getJobMonitorsForDashBoard(JobHistory jobHistory) throws EdgeServiceFault {
		List<FlashJobMonitor> jobMonitors = new ArrayList<>();
		try{
			JobDetail jobDetail = new JobDetail();
			// Rps waitingJob need connect Rps to get JobMonitor
			if(	jobHistory.getJobStatus()==JobStatus.Waiting 
					//&&(!StringUtil.isEmptyOrNull(jobHistory.getServerId()) && Integer.parseInt(jobHistory.getServerId())==0)
					&&(!StringUtil.isEmptyOrNull(jobHistory.getTargetRPSId()) && Integer.parseInt(jobHistory.getTargetRPSId())>0)){
					logger.debug("getJobMonitorsForDashBoard JobStatus=Waiting ");					
					jobDetail.setSource(getSourceTypeByHostId(Integer.parseInt(jobHistory.getTargetRPSId())));
					jobDetail.setServerId(Integer.parseInt(jobHistory.getTargetRPSId()));
					jobDetail.setNodeId(Integer.parseInt(jobHistory.getAgentId()));
					if(StringUtil.isEmptyOrNull(jobHistory.getAgentUUID()))
						logger.debug("getJobMonitorsForDashBoard waitJob jobHistory.getAgentUUID=null "+jobHistory.toString());						
					jobDetail.addNodeUUID(jobHistory.getAgentUUID(),jobHistory.getAgentId(),jobHistory.getAgentNodeName());
					jobDetail.setJobType(jobHistory.getJobType());			
					jobDetail.setJobId(jobHistory.getJobId());	
					logger.debug("getJobMonitorsForDashBoard JobStatus=Waiting serverId="+jobHistory.getTargetRPSId());	
			} else {
				logger.debug("getJobMonitorsForDashBoard serverId="+jobHistory.getServerId());		
				jobDetail.setSource(getSourceTypeByHostId(Integer.parseInt(jobHistory.getServerId())));
				jobDetail.setServerId(Integer.parseInt(jobHistory.getServerId()));
				jobDetail.setNodeId(jobHistory.getNodeId());
				if(StringUtil.isEmptyOrNull(jobHistory.getAgentUUID()))
					logger.debug("getJobMonitorsForDashBoard jobHistory.getAgentUUID=null"+jobHistory.toString());					
				jobDetail.addNodeUUID(jobHistory.getAgentUUID(),jobHistory.getAgentId(),jobHistory.getAgentNodeName());
				jobDetail.setJobType(jobHistory.getJobType());			
				jobDetail.setJobId(jobHistory.getJobId());				
			}			
			
			if(jobDetail.getSource() == SourceType.NO_TYPE)
				logger.error("getJobMonitorsForDashBoard Has the ERROR SOURCETYPE, serverId="+jobHistory.getServerId());
			else {
				logger.debug("getJobMonitorsForDashBoard source="+jobDetail.getSource());
				List<JobHistory> jobHistorys = new ArrayList<JobHistory>();
				jobHistorys.add(jobHistory);
				jobDetail.setHistorysList(jobHistorys);
				jobMonitors = JobMonitorHandlerManager.getJobMonitorOnServer(jobDetail,true);
			} 
			if(jobMonitors!=null&&jobMonitors.size()>0){	
				logger.debug("getJobMonitorsForDashBoard start get right JobMonitor by History:"+jobHistory.toString());
				List<FlashJobMonitor> retainList = new ArrayList<FlashJobMonitor>();
				for (FlashJobMonitor job:jobMonitors) {
					if(compareHistoryAndMonitor(job, jobHistory, true)){
						retainList.add(job);
						break;
					}
				}
				logger.debug("getJobMonitorsForDashBoard end get right JobMonitor by History size="+retainList.size());
//				// remove invalid history from DB
//				List<JobHistory> jobHistorys = new ArrayList<JobHistory>();
//				jobHistorys.add(jobHistory);
//				syncHistoryAndMonitor(jobHistorys, jobMonitors,false);
				return retainList;
			} 
		}catch(Exception e){
			logger.error("get job monitor fail, ", e);
		}
		return jobMonitors;
	}

	@Override
	public List<FlashJobMonitor> getJobMonitorsForDashBoardHistorys(List<JobHistory> historys) throws EdgeServiceFault {
		if(historys == null || historys.size()<=0)
			return null;		
		logger.debug("getJobMonitorsForDashBoardHistorys historys.size is "+((historys==null)?0:historys.size()));
		// use JobsReaderThread to Load 
		//List<FlashJobMonitor> list = this.getJobMonitorsFormRunningServers(this.getRunningServersForNode(historys),historys);
		List<FlashJobMonitor> list = new ArrayList<FlashJobMonitor>();
		for (int i = 0; i < historys.size(); i++) {
			FlashJobMonitor jobMonitor = HISTORY_MONITOR_MAP.get(historys.get(i).getHistoryId());
			if(jobMonitor!=null)
				list.add(jobMonitor);
		}
		//use JobsReaderThread end
		
		// remove invalid history from DB
//		syncHistoryAndMonitor(historys,list,true);
		return list;
	}
	
	@Override
	public List<FlashJobMonitor> getJobMonitorsForNodeView(int nodeId) throws EdgeServiceFault {	
		logger.debug("getJobMonitorsForNodeView nodeId="+nodeId);
		List<JobHistory> historys = this.getJobMonitorsFromDB(nodeId,-1,"");
		if(historys == null || historys.size()<=0)
			return null;
		List<FlashJobMonitor> list = this.getJobMonitorsFormRunningServers(this.getRunningServersForNode(historys),historys);
		// remove invalid history from DB
//		syncHistoryAndMonitor(historys,list,true);
		return list;
	}

	@Override
	public List<FlashJobMonitor> getJobMonitorsForAsbuView(int serverId) throws EdgeServiceFault {		
		// direct connect ASBU get all jobMonitors
		List<JobHistory> historys = this.getJobMonitorsFromDB(-1,serverId,"");
		if (historys==null||historys.isEmpty()) {
			logger.debug("getJobMonitorsForAsbuView Has historys size==0, serverId="+serverId);
			return null;
		}
		JobDetail jobDetail = new JobDetail();			
		jobDetail.setSource(getSourceTypeByHostId(serverId));
		jobDetail.setServerId(serverId);
		if(jobDetail.getSource() == SourceType.NO_TYPE){
			logger.error("getJobMonitorsForAsbuView Has the ERROR SOURCETYPE, serverId="+serverId);
			return null;
		}
		for (JobHistory history:historys) {
			if(StringUtil.isEmptyOrNull(history.getAgentUUID()))
				logger.debug("getJobMonitorsForAsbuView jobHistory.getAgentUUID=null "+history.toString());
			else
				jobDetail.addNodeUUID(history.getAgentUUID(),history.getAgentId(),history.getAgentNodeName());	
		}
		if(jobDetail.getNodeUUIDs()==null||jobDetail.getNodeUUIDs().isEmpty()){
			logger.debug("getJobMonitorsForAsbuView getNodeUUIDs=null ");
			return null;
		}		
		jobDetail.setHistorysList(historys);
		List<FlashJobMonitor> monitors = JobMonitorHandlerManager.getJobMonitorOnServer(jobDetail,false);		
		// remove invalid history from DB
		logger.debug("getJobMonitorsForAsbuView check IsHaveInvalid historys begin historys.size="+historys.size()+" monitor.size="+((monitors==null)?0:monitors.size()));
		syncHistoryAndMonitor(historys,monitors,true);
		logger.debug("getJobMonitorsForAsbuView check IsHaveInvalid historys end historys.size="+historys.size()+" monitor.size="+((monitors==null)?0:monitors.size()));	
		
		return monitors;
		
	}
	
	@Override
	public List<FlashJobMonitor> getJobMonitorsForRpsView(int serverId, String dataStoreUUID) throws EdgeServiceFault {		
		logger.debug("getJobMonitorsForRpsView serverid="+serverId+"; datastoreUUID="+dataStoreUUID+";");	
		
		// direct connect RPS get all jobMonitors
		JobDetail jobDetail = new JobDetail();			
		jobDetail.setSource(getSourceTypeByHostId(serverId));
		jobDetail.setServerId(serverId);
		jobDetail.setDataStoreUUID(dataStoreUUID);
		if(jobDetail.getSource() == SourceType.NO_TYPE){
			logger.error("getJobMonitorsForRpsView Has the ERROR SOURCETYPE, serverId="+serverId);
			return null;
		}
		List<JobHistory> historys = this.getJobMonitorsFromDB(-1,serverId,"");
		jobDetail.setHistorysList(historys);
		List<FlashJobMonitor> monitors = JobMonitorHandlerManager.getJobMonitorOnServer(jobDetail,true);		
//		if(jobDetail.getSource()==SourceType.RPS){	
//			logger.debug("getJobMonitorsForRpsView begin to syncJobMonitorForRPS");
//			monitors = syncJobMonitorForRPS(monitors,jobDetail.getServerId());
//			for(FlashJobMonitor job:monitors){
//				logger.debug("getJobMonitorsForRpsView syncJobMonitorForRPS job[ "+job.toString()+"]");
//			}
//			logger.debug("getJobMonitorsForRpsView end syncJobMonitorForRPS");
//		} else {
//			logger.debug("getJobMonitorsForRpsView no need syncJobMonitorForRPS source="+jobDetail.getSource());
//		}
//		// remove invalid history from DB
//		List<JobHistory> historys = this.getJobMonitorsFromDB(-1,serverId,"");
//		logger.debug("getJobMonitorsForRpsView check IsHaveInvalid historys begin");
//		syncHistoryAndMonitor(historys,monitors,true);
//		logger.debug("getJobMonitorsForRpsView check IsHaveInvalid historys end");		
				
		List<FlashJobMonitor> retainList = null;
		// if datastore Param not null. remove jobMonitor not belong to this datastore
		if(!StringUtil.isEmptyOrNull(dataStoreUUID)){	
			retainList = new ArrayList<FlashJobMonitor>();
			for (FlashJobMonitor monitor : monitors) {
				if(monitor.getDataStoreUUID()!=null&&monitor.getDataStoreUUID().equals(dataStoreUUID)){
					retainList.add(monitor);
				}
			}
		} else {
			retainList = monitors;
		}
		logger.debug("getJobMonitorsForRpsView retainList.size is "+((retainList==null)?0:retainList.size()));	
		
		return retainList;
	}
	
	public static boolean compareHistoryAndMonitor(FlashJobMonitor monitor,JobHistory history,boolean compareServer){
		if(history.getJobType()==80 && (monitor.getJobType()==60 ||monitor.getJobType()==61) && history.getJobUUID().equals(monitor.getJobUUID())){
			return true; //for linux instantvm, monitor and history jobtype is not same.  --add by wanhu08
		}
		if(history.getJobType()!=monitor.getJobType())
			return false;
		if (history.getJobId()!=monitor.getJobId()) 
			return false;
		String agentUUID = StringUtil.isEmptyOrNull(monitor.getD2dUuid())?monitor.getVmInstanceUUID():monitor.getD2dUuid();
		if(agentUUID==null)agentUUID = "";
		if((!StringUtil.isEmptyOrNull(history.getAgentUUID())) && (!history.getAgentUUID().trim().contains(agentUUID))){
			logger.debug("compareHistoryAndMonitor jobhistory and monitor has same Jobtype and jobId but agentUUID not euqal"
					+" "+monitor.getJobType()+" "+monitor.getJobId()
					+" historyAgentUUID="+history.getAgentUUID()+" monitorAgentUUID="+agentUUID);
			return false;
		}
		String jobUUID = StringUtil.isEmptyOrNull(monitor.getJobUUID())?"":monitor.getJobUUID().trim();
		if((!StringUtil.isEmptyOrNull(history.getJobUUID())) 
				&& (!StringUtil.isEmptyOrNull(jobUUID)) 
				&& (!history.getJobUUID().trim().contains(monitor.getJobUUID().trim()))){
			logger.debug("compareHistoryAndMonitor jobhistory and monitor has same Jobtype and jobId but jobUUID not euqal"
					+" "+monitor.getJobType()+" "+monitor.getJobId()
					+" historyJOBUUID="+history.getJobUUID()+" monitorJOBUUID="+monitor.getJobUUID());
			return false;
		}	
		if(compareServer){
			if(monitor.getJobStatus()==JobStatus.Waiting.getValue()&&(Integer.parseInt(history.getServerId())==0 || history.getJobId()==0))
				return true;
			if(Integer.parseInt(history.getServerId()) != monitor.getRunningServerId()){
				logger.info("compareHistoryAndMonitor jobhistory and monitor has same Jobtype and jobId but runningServerId not euqal"
						+" "+monitor.getJobType()+" "+monitor.getJobId()
						+" historyServerId="+history.getServerId()+" monitorServerId="+monitor.getRunningServerId()+" JobMonitorId="+monitor.getJobMonitorId());
				return false;
			}
		}
		return true;
	}

	/**
	 * form db table 'as_edge_d2dJobHistory_JobMonitor' to get ServerInfo for specified nodeId
	 * @param historys
	 * @return Map<String,JobDetail> serverMap
	 * @throws EdgeServiceFault
	 */
	 private Map<String,JobDetail> getRunningServersForNode(List<JobHistory> historys){
		
		Map<String,JobDetail> serverMap = new HashMap<String,JobDetail>();
		for(JobHistory history : historys){
			// wait job maybe have serverID=0 but targetRPSId is not 0,so below code should before 'filter invalid history data'		
			// RPS waitingJob need connect RPS to get JobMonitor
			if(history.getJobStatus()==JobStatus.Waiting){
				//if(((!StringUtil.isEmptyOrNull(history.getServerId())&& Integer.parseInt(history.getServerId())==0) ||  history.getJobId()==0)
				//		&&(!StringUtil.isEmptyOrNull(history.getTargetRPSId())&& Integer.parseInt(history.getTargetRPSId())>0)){
				if(!StringUtil.isEmptyOrNull(history.getTargetRPSId())&& Integer.parseInt(history.getTargetRPSId())>0){
					logger.debug("getRunningServersForNode JobStatus=Waiting ");
					JobDetail jobDetail;					
					String key = history.getTargetRPSId();
					if(serverMap.containsKey(key))
						jobDetail = serverMap.get(key);
					else {
						jobDetail = new JobDetail();
						//jobDetail.setSource(getSourceTypeByHostId(Integer.parseInt(history.getTargetRPSId())));	
						jobDetail.setSource(ServerSourceTypeCache.getInstance().getHostSoureType(Integer.parseInt(history.getTargetRPSId())));
						if(jobDetail.getSource() == SourceType.NO_TYPE){
							logger.error("getRunningServersForNode Has the ERROR SOURCETYPE, TargetRPSId="+history.getTargetRPSId());
							continue;
						}	
						jobDetail.setServerId(Integer.parseInt(history.getTargetRPSId()));
						jobDetail.setNodeId(Integer.parseInt(history.getAgentId()));
						serverMap.put(key, jobDetail);
					}
					if(StringUtil.isEmptyOrNull(history.getAgentUUID()))
						logger.debug("getRunningServersForNode waitjob jobHistory.getAgentUUID=null "+history.toString());
					jobDetail.addNodeUUID(history.getAgentUUID(),history.getAgentId(),history.getAgentNodeName());
					if(jobDetail.getHistorysList()==null)
						jobDetail.setHistorysList(new ArrayList<JobHistory>());			
					jobDetail.getHistorysList().add(history);					
					/*if(history.getJobId()==0 && history.getProductType() == 3){
						logger.info("getRunningServersForNode JobStatus=Waiting, jobId=0, productType=3");
						continue;
					}*/
				}
			}
			// filter invalid history data
			if(StringUtil.isEmptyOrNull(history.getServerId())|| Integer.parseInt(history.getServerId())<=0){
				logger.debug("unvalid history data in table as_edge_d2dJobHistory_JobMonitor jobid="
					+history.getJobId()+" jobtype="+history.getJobType()+" serverId="+history.getServerId());				
				continue;
			}
			
			JobDetail jobDetail;
			String key = history.getServerId();
			if(serverMap.containsKey(key))
				jobDetail = serverMap.get(key);
			else {
				jobDetail = new JobDetail();
				//jobDetail.setSource(getSourceTypeByHostId(Integer.parseInt(history.getServerId())));
				jobDetail.setSource(ServerSourceTypeCache.getInstance().getHostSoureType(Integer.parseInt(history.getServerId())));
				if(jobDetail.getSource() == SourceType.NO_TYPE){
					logger.debug("getRunningServersForNode Has the ERROR SOURCETYPE, serverId="+history.getServerId());
					continue;
				}	
				jobDetail.setServerId(Integer.parseInt(history.getServerId()));
				jobDetail.setNodeId(Integer.parseInt(history.getAgentId()));
				serverMap.put(key, jobDetail);
			}	
			if(StringUtil.isEmptyOrNull(history.getAgentUUID()))
				logger.debug("getRunningServersForNode jobHistory.getAgentUUID=null "+history.toString());
			jobDetail.addNodeUUID(history.getAgentUUID(),history.getAgentId(),history.getAgentNodeName());		
			if(jobDetail.getHistorysList()==null)
				jobDetail.setHistorysList(new ArrayList<JobHistory>());			
			jobDetail.getHistorysList().add(history);			
		}
		return serverMap;
	}
		 
	/**
	 * connect Server to get JobMonitors
	 * @param serverMap:servers need to connect
	 * @param historys:filter by history
	 * @return null/List<FlashJobMonitor>
	 * @throws EdgeServiceFault
	 */
	private List<FlashJobMonitor> getJobMonitorsFormRunningServers(Map<String,JobDetail> serverMap,List<JobHistory> historys) throws EdgeServiceFault {
		/*		
		List<FlashJobMonitor> jobMonitorlist = new ArrayList<FlashJobMonitor>();
		
		for (Iterator<String> iterator = serverMap.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			JobDetail jobDetail = serverMap.get(key);
			jobDetail.setHistorysList(historys);
			List<FlashJobMonitor> monitors = null;
			try {
				monitors = JobMonitorHandlerManager.getJobMonitorOnServer(jobDetail,true);
			} catch (Exception e) {
				logger.error("getJobMonitorsFormConnectServers catch Error :"+e.getMessage());
				continue;
			}
			if(monitors == null || monitors.size()<=0){
				logger.debug("getJobMonitorsFormConnectServers return size is 0");
				continue;
			}
			if(jobDetail.getSource()==SourceType.RPS){
				for (FlashJobMonitor monitor:monitors) {
					if (monitor.isRunningOnRPS()) {
						logger.debug("getJobMonitorsFormConnectServers Add runOnRPS JobMonitor:"+monitor.toString());
						jobMonitorlist.add(monitor);	
					} else {
						logger.debug("getJobMonitorsFormConnectServers no Add !runOnRPS JobMonitor:"+monitor.toString());
					}
				}
			} else {
				jobMonitorlist.addAll(monitors);	
			}
		}*/
		
		///////////////////////////////////////////////////////////////////////////////////////////
		// user ThreadPool to getJobMonitor
		List<JobDetail> serverList = new ArrayList<JobDetail>();
		List<FlashJobMonitor> jobMonitorlist = new ArrayList<FlashJobMonitor>();
		for (Iterator<String> iterator = serverMap.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			JobDetail jobDetail = serverMap.get(key);
			jobDetail.setHistorysList(historys);
			serverList.add(jobDetail);			
		}
		jobMonitorlist = QueryJobMonitorsThreadPool.getInstance(serverList).getJobMonitors();
		///////////////////////////////////////////////////////////////////////////////////////////	
		
		List<FlashJobMonitor> retainList = new ArrayList<FlashJobMonitor>();
		for (JobHistory history : historys) {
			boolean findflg = false;	
			for (FlashJobMonitor job:jobMonitorlist) {
				if(compareHistoryAndMonitor(job, history, true)){
					retainList.add(job);
					findflg = true;
					break;
				} 
			}
			if(!findflg){
				logger.info("getJobMonitorsFormRunningServers delete no corresponding's history: "+ history.toString());
				jobHistoryDao.as_edge_d2dJobHistory_monitor_delete(history.getJobId(), history.getJobType(),
						Integer.parseInt(history.getAgentId()),Integer.parseInt(history.getServerId()));
				D2DAllJobStatusCache.getD2DAllJobStatusCache().removeCacheJobMonitorByHistory(history);
			}
		}		
		return retainList;
	}
		
	/**
	 * set ServerType(WIN_D2D/RPS/LINUX_D2D) by serverId 's ProtectionTypeBitmap
	 * @param serverId
	 * @throws EdgeServiceFault
	 */
	public SourceType getSourceTypeByHostId(int serverId) throws EdgeServiceFault{
		List<EdgeHost> hostList = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_list(serverId, 1, hostList);
		if(hostList == null || hostList.isEmpty())
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_NOTFOUND, "Cannot find host information by node id.");
		switch (hostList.get(0).getProtectionTypeBitmap()) {
		case 0x00000001:	//ProtectionType.WIN_D2D;
			return SourceType.D2D;
		case 0x00000004:	//ProtectionType.RPS;
			return SourceType.RPS;
		case 0x00000080:	//ProtectionType.LINUX_D2D_SERVER;
			return SourceType.LINUXD2D;
		case 0x00000100:	//ProtectionType.ASBUServer;
			return SourceType.ASBU;
		default:			//ProtectionType.Unprotected;
			return SourceType.NO_TYPE;
		}		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int cancelMultipleJobs(List<CancelJobParameter> parameters) throws EdgeServiceFault {
		if(parameters == null || parameters.isEmpty())
			return 0;	
		ActionTaskParameter parameter =  new ActionTaskParameter();
		parameter.setEntityIds(parameters);
		parameter.setModule(Module.CancelMutipleJob);
		ActionTaskManager taskManager = new ActionTaskManager(parameter);
		return taskManager.doAction();
	}
	
}
