package com.ca.arcserve.edge.app.base.webservice.monitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.RPSJobInfo;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4CPM;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcflash.webservice.data.merge.MergeAPISource;
import com.ca.arcserve.edge.app.base.appdaos.EdgeD2DHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao.JobHistoryProductType;
import com.ca.arcserve.edge.app.base.common.NamingThreadFactory;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.LinuxD2DServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHyperVHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DAllJobStatusCache;
import com.ca.arcserve.edge.app.base.webservice.instantvm.InstantVMManager;
import com.ca.arcserve.edge.app.base.webservice.jobhistory.JobHistoryServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail;

/**
 * RPSServer job monitor tracker which used for get current job detail
 * information
 * 
 * @author lijyo03
 * 
 */
public class RPSJobMonitorReader implements JobMonitorReader {
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private static final Logger logger = Logger.getLogger(RPSJobMonitorReader.class);
	private String errorMessage;
	private RPSJobMonitorReader() {
		
	}

	private static class LazyHolder {
		public static final RPSJobMonitorReader tracker = new RPSJobMonitorReader();
	}

	public static RPSJobMonitorReader getInstance() {
		return LazyHolder.tracker;
	}

	@Override
	@Deprecated
	public List<JobMonitor> getJobMonitor(JobDetail jobDetail)
			throws EdgeServiceFault {
		logger.error("[Rps] RPSJobMonitorReader getJobMonitor has no implement !!!!");
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<FlashJobMonitor> getJobMonitorOnServer(JobDetail jobDetail)
			throws EdgeServiceFault {
		logger.debug("[Rps] start get job details jobDetail.getServerId()="+jobDetail.getServerId());
		IRPSService4CPM service;
		List<FlashJobMonitor> list = new ArrayList<FlashJobMonitor>();
		try (RPSConnection connection = connectionFactory.createRPSConnection(jobDetail.getServerId())) {
			connection.connect();
			service = connection.getService();
			FlashJobMonitor[] flashJobMonitors = null;
			FlashJobMonitor[] flashPauseMergeJobMonitors = null;
			// get Running job On RPS
			flashJobMonitors = service.getJobMonitorMapByPolicyId(null);
			flashPauseMergeJobMonitors = service.getAllPauseMergeJobMonitors();
			logger.debug("[Rps] getJobMonitorOnServer getMonitor size is "+ flashJobMonitors.length
					+" getPauseMergeMonitor size is "+ flashPauseMergeJobMonitors.length);
			List<FlashJobMonitor> jobMonitors = new ArrayList<FlashJobMonitor>();
			for(FlashJobMonitor job : flashJobMonitors){
				jobMonitors.add(job);
			}
			for(FlashJobMonitor job : flashPauseMergeJobMonitors){
				jobMonitors.add(job);
			}
			for (FlashJobMonitor job : jobMonitors) {
				logger.debug("[Rps] JobType = " + job.getClass().getName());
				if (job.getJobType() == JobType.JOBTYPE_START_INSTANT_VM
						|| job.getJobType() == JobType.JOBTYPE_STOP_INSTANT_VM) {
					InstantVMJobMonitor ivmJob = InstantVMManager.getInstance().getInstantVMJobMonitor4RPS(job);
					if (ivmJob != null) {
						logger.debug("[Rps] AddJobMonitor InstantVMJobMonitor:"+ ivmJob.toString());
						list.add(ivmJob);
					}
				} else if (job.getJobType() == JobType.JOBTYPE_LINUX_INSTANT_VM) {
					InstantVMJobMonitor ivmJob = InstantVMManager.getInstance().getInstantVMJobMonitor4Linux(job.getJobUUID(),
									JobHistoryProductType.RPS);
					if (ivmJob != null) {
						logger.debug("[Rps] AddJobMonitor Linux InstantVMJobMonitor:"+ ivmJob.toString());
						list.add(ivmJob);
					}
				} else if (job instanceof com.ca.arcflash.webservice.data.JobMonitor
						|| job instanceof com.ca.arcflash.webservice.data.merge.MergeStatus
						|| job instanceof com.ca.arcflash.webservice.data.ConvertJobMonitor){				
					logger.debug("[Rps]sync AddJobMonitor job:"+job.toString());		
					job.setJobMonitorId("RPS-"+job.getJobType()+"-"+job.getJobId()
							+"-"+jobDetail.getServerId()+"-"+job.getJobUUID());
					if(job.isRunningOnRPS())
						job.setRunningServerId(jobDetail.getServerId());
					job.setHistoryProductType(JobHistoryProductType.RPS.getValue());
					if(job.getJobType()==JobType.JOBTYPE_RPS_PURGE_DATASTORE){
						job.setRunningOnRPS(true);
						job.setRunningServerId(jobDetail.getServerId());
					} else {
						syncJobMonitorInfo(job,jobDetail);
					}	
					list.add(job);	
				} else{
					// PreviousVersion RPS only can return FlashJobMonitor,cannot return other Type Monitor
					if(list.size()<=0){
						logger.debug("[Rps] isPreviousVersion = true; for list.size()==0 and getMonitor is FlashJobMonitor");
					}
				}							
			}

			// get Waiting jobs On rps
			RPSJobInfo[] rpsJobInfoArray = service.getRPSWaitingJobs(null);
			logger.debug("[Rps] getRPSWaitingJobs return size is "+ rpsJobInfoArray.length);
			if (rpsJobInfoArray != null && rpsJobInfoArray.length > 0) {
				for (RPSJobInfo info : rpsJobInfoArray) {
					logger.debug("[Rps] waitingJob = " + info.toString());
					com.ca.arcflash.webservice.data.JobMonitor jobMonitor = convertRPSJobInfoToJobMonitor(info);
					if (jobMonitor == null) {
						logger.debug("[Rps] jobMonitor = null ");
						continue;
					}
	
					jobMonitor.setJobMonitorId("RPS-"+jobMonitor.getJobType()+"-"+jobMonitor.getJobId()
							+"-"+jobDetail.getServerId()+"-"+jobMonitor.getJobUUID());	
					jobMonitor.setRunningOnRPS(true);
					jobMonitor.setRunningServerId(jobDetail.getServerId());
					jobMonitor.setHistoryProductType(JobHistoryProductType.RPS.getValue());
					jobMonitor.setJobStatus(JobStatus.JOBSTATUS_WAITING);						
					if (jobMonitor.getJobType() == JobType.JOBTYPE_RPS_REPLICATE) 
						jobMonitor.setSourceRPSId(jobDetail.getServerId());
					else         							
						jobMonitor.setTargetRPSId(jobDetail.getServerId());
					syncJobMonitorInfo(jobMonitor,jobDetail);
					list.add(jobMonitor);
				}
			}
		}catch (Exception e) { 
			if(e instanceof SOAPFaultException){
				String errorMessage = ((SOAPFaultException)e).getFault().getFaultString();				
				if (errorMessage != null && errorMessage.contains(LinuxD2DServiceFault.METHOD_NOT_DEFINED_MESSAGE)) {					
					logger.debug("[Rps] isPreviousVersion=true; for catch ERROR(Cannot find dispatch method)");
					return D2DAllJobStatusCache.getD2DAllJobStatusCache().getJobStatusInfoList("RPS-"+jobDetail.getServerId()+"-");	
				}
			}
			if(errorMessage==null||(!errorMessage.equals(e.getMessage()))){
				errorMessage = e.getMessage();
				logger.error("[Rps] getJobMonitorOnServer catch Error:"+errorMessage);
			}
			return D2DAllJobStatusCache.getD2DAllJobStatusCache().getJobStatusInfoList("RPS-"+jobDetail.getServerId()+"-");	
		}
		logger.debug("[Rps] getJobMonitorOnServer return size is " + list.size());			
		return list;
	}

	/*
	 * int returnValue = nodeService.cancelJobById(jobDetail.getServerId(),
	 * jobDetail.getHostName(), jobDetail.getJobId(),
	 * jobDetail.getJobType(), d2dUUID, vmInstanceUUID, true);
	 */	
	@Override
	public boolean cancelJob(JobDetail jobDetail) throws EdgeServiceFault {
		logger.debug("[Rps] start cancelJob");
		Map<String, String> uuidMap = getD2dUUIDAndVMInstanceUUIDById(jobDetail
				.getNodeId());
		String d2dUUID = uuidMap.get("d2dUUID");
		String vmInstanceUUID = uuidMap.get("vmInstanceUUID");
		if (StringUtil.isEmptyOrNull(vmInstanceUUID)) {
			logger.debug("[Rps] cancelJob is not vm job");
		} else {
			logger.debug("[Rps] cancelJob is vm job");
		}
		try (RPSConnection connection = connectionFactory
				.createRPSConnection(jobDetail.getServerId())) {
			connection.connect();
			int value;
			if (jobDetail.getJobType() == JobType.JOBTYPE_RPS_MERGE) {
				value = connection.getService().pauseMerge(
						MergeAPISource.MANUALLY,
						StringUtil.isEmptyOrNull(vmInstanceUUID) ? d2dUUID
								: vmInstanceUUID);
			} else {
				value = (int) connection.getService().cancelJob(
						StringUtil.isEmptyOrNull(vmInstanceUUID) ? d2dUUID
								: vmInstanceUUID, jobDetail.getJobId(),
						jobDetail.getJobType());
			}
			logger.debug("[Rps] end cancelJob: " + value);
			if (value == 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	private com.ca.arcflash.webservice.data.JobMonitor convertRPSJobInfoToJobMonitor(
			RPSJobInfo info) {
		com.ca.arcflash.webservice.data.JobMonitor jobMonitor = new com.ca.arcflash.webservice.data.JobMonitor();
		jobMonitor.setD2dServerName(info.getD2dServerName());
		jobMonitor.setAgentNodeName(info.getD2dServerName());
		jobMonitor.setServerNodeName(info.getRpsServerName());
		if (getHostIdByD2DUUID(info.getD2duuid()) > 0) {
			jobMonitor.setD2dUuid(info.getD2duuid());
		} else {
			jobMonitor.setVmInstanceUUID(info.getD2duuid());
		}
		jobMonitor.setRpsPolicyUUID(info.getPolicyUUID());
		jobMonitor.setPlanUUID(info.getPlanUUID());
		jobMonitor.setTargetPlanUUID(info.getTargetPlanUUID());
		jobMonitor.setDataStoreUUID(info.getDataStoreUUID());
		jobMonitor.setJobId(info.getJobId());
		jobMonitor.setJobMethod(info.getJobMethod());
		jobMonitor.setJobType(info.getJobType());
		jobMonitor
				.setElapsedTime((Calendar.getInstance().getTimeInMillis() - info
						.getStartTime().getTime()));
		jobMonitor.setStartTime(info.getStartTime().getTime());
		jobMonitor.setJobStatus(JobStatus.JOBSTATUS_WAITING);
		return jobMonitor;
	}

	private int getHostIdByD2DUUID(String d2duuid) {
		List<EdgeD2DHost> hostList = new ArrayList<EdgeD2DHost>();
		IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		hostMgrDao.getHostByUUID(d2duuid, hostList);
		if (hostList.size() == 0) {
			logger.debug("getHostByD2DUUID(" + d2duuid + "): Returns -1");
			return -1;
		}
		logger.debug("getHostByD2DUUID(" + d2duuid + "): Returns " + hostList.get(0).getRhostid());
		return hostList.get(0).getRhostid();
	}

	private Map<String, String> getD2dUUIDAndVMInstanceUUIDById(int vmId) {
		Map<String, String> uuidMap = new HashMap<String, String>();

		IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
		IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
		List<EdgeHost> hosts = new LinkedList<EdgeHost>();
		hostMgrDao.as_edge_host_list(vmId, 1, hosts);
		if (hosts.size() == 0) {
			return uuidMap;
		}
		String d2dUUID = hosts.get(0).getD2DUUID();

		uuidMap.put("d2dUUID", d2dUUID);
		if (HostTypeUtil.isVMWareVirtualMachine(hosts.get(0).getRhostType())) { // VMware
			List<EdgeEsxVmInfo> vmList = new LinkedList<>();
			esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(vmId, vmList);
			if (!vmList.isEmpty()) {
				String vmInstanceUUID = vmList.get(0).getVmInstanceUuid();
				uuidMap.put("vmInstanceUUID", vmInstanceUUID);
			}
			return uuidMap;
		} else { // Hyper-V
			List<EdgeHyperVHostMapInfo> hostMapInfo = new ArrayList<EdgeHyperVHostMapInfo>();
			hyperVDao.as_edge_hyperv_host_map_getById(vmId, hostMapInfo);
			if (hostMapInfo != null && hostMapInfo.size() > 0) {
				String vmInstanceUUID = hostMapInfo.get(0).getVmInstanceUuid();
				uuidMap.put("vmInstanceUUID", vmInstanceUUID);
			}
			return uuidMap;
		}
	}

	
	private void syncJobMonitorInfo(FlashJobMonitor job,JobDetail jobDetail){
		List<JobHistory> historyList = jobDetail.getHistorysList();
		if (historyList==null||historyList.isEmpty()){
			logger.debug("[RPS] syncJobMonitorInfo historyList is null");
			return;	
		}
		if(logger.isDebugEnabled()){
			for (JobHistory history:historyList) {
				logger.debug("[RPS] syncJobMonitorInfo historyList "+history.toString());
			}
			logger.debug("[RPS] syncJobMonitorInfo FlashJobMonitor "+job.toString());
		}
		String agentUUID = StringUtil.isEmptyOrNull(job.getD2dUuid())?job.getVmInstanceUUID():job.getD2dUuid();
		if(agentUUID==null)agentUUID = "";	
		logger.debug("[RPS] syncJobMonitorInfo agentUUID="+agentUUID);
		for (JobHistory history:historyList) {
			if(history.getJobType()!=job.getJobType() || history.getJobId()!=job.getJobId()){		
				continue;
			}
			logger.debug("[RPS] syncJobMonitorInfo jobtype jobid verify ok");
			if(history.getJobStatus()==com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobStatus.Waiting){
				if( Integer.parseInt(history.getTargetRPSId())!=jobDetail.getServerId()){
					logger.debug("[RPS] syncJobMonitorInfo jobtype jobid verify ok ServerID not contions 1");
					continue;
				}
			}
			else if(history.getProductType()!=JobHistoryProductType.RPS.getValue()
					&& Integer.parseInt(history.getTargetRPSId())!=jobDetail.getServerId()){
				logger.debug("[RPS] syncJobMonitorInfo jobtype jobid verify ok ServerID not contions 2");
				continue;
			} else if(history.getProductType()==JobHistoryProductType.RPS.getValue()
					&& Integer.parseInt(history.getServerId())!=jobDetail.getServerId()){
				logger.debug("[RPS] syncJobMonitorInfo jobtype jobid verify ok ServerID not contions 3");
				continue;
			}
			if(history.getAgentUUID()==null||history.getAgentUUID().isEmpty()){
				logger.debug("[RPS] syncJobMonitorInfo history.getAgentUUID() is null");
				continue;
			}
			if(history.getAgentUUID().contains(agentUUID)){	
				logger.debug("[RPS] syncJobMonitorInfo find dest history history.getAgentUUID()="+history.getAgentUUID()+" agentUUID="+agentUUID);
				job.setAgentNodeName(history.getAgentNodeName());
				job.setD2dServerName(history.getAgentNodeName());
				job.setVmHostName(history.getAgentNodeName());
				job.setNodeId(Integer.parseInt(history.getAgentId()));		
				if(job.getJobStatus()==JobStatus.JOBSTATUS_WAITING){
					if(Integer.parseInt(history.getServerId())==0){						
						job.setRunningOnRPS(true);
						job.setRunningServerId(jobDetail.getServerId());
						job.setHistoryProductType(JobHistoryProductType.RPS.getValue());		
					}else{
						job.setRunningServerId(Integer.parseInt(history.getServerId()));
					}
				} else {
					job.setRunningOnRPS(jobDetail.getServerId()==Integer.parseInt(history.getServerId()));
					job.setRunningServerId(Integer.parseInt(history.getServerId()));
					job.setHistoryProductType(history.getProductType());				
				}
				job.setJobMonitorId("RPS-"+job.getJobType()+"-"+job.getJobId()
						+"-"+job.getRunningServerId()+"-"+job.getNodeId());
				logger.debug("[RPS] syncJobMonitorInfo end  job="+job.toString());
				break;
			} else {
				logger.debug("[RPS] syncJobMonitorInfo not find dest history history.getAgentUUID()="+history.getAgentUUID()+" agentUUID="+agentUUID);
			}
		}		
	}
	
	private static BlockingQueue<JobDetail> blockingQueueForRPS;	
	private static ThreadPoolExecutor jobsExcutor = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, 
			new LinkedBlockingQueue<Runnable>(),new NamingThreadFactory( "QueryRPSJobMonitor" ));
	
	public static void initReaderThread(BlockingQueue<JobDetail> blockingQueue){
		if(blockingQueueForRPS!=null)
			return;		
		logger.debug("RPSJobMonitorReader initReaderThread");
		blockingQueueForRPS = blockingQueue;	
		for(int i=0; i < 10; i++){
			final RPSJobMonitorReader reader = new RPSJobMonitorReader();
			jobsExcutor.execute(new Runnable() {
				
				@Override
				public void run() {
					readJobMonitor(reader);
				}
			});			
		}
	}
	
	public static void shutdonwReaderThread() {
		if(jobsExcutor!=null)
			jobsExcutor.shutdownNow();
	}
	
	
	public static void readJobMonitor(RPSJobMonitorReader reader){		
		while(true){		
			try {
				JobDetail jobDetail = blockingQueueForRPS.take();
				logger.debug("RPSJobMonitorReader readJobMonitor blockingQueueForRPS.take() "+jobDetail.toString());
				if(jobDetail.getHistorysList()==null||jobDetail.getHistorysList().isEmpty()){					
					logger.debug("RPSJobMonitorReader readJobMonitor NoNeed get as history.size=0");				
					continue;
				}
				try {
					List<FlashJobMonitor> jobMonitors = reader.getJobMonitorOnServer(jobDetail);
					if(jobMonitors!=null && jobMonitors.size()>0){	
						logger.debug("RPSJobMonitorReader readJobMonitor JobMonitor.size"+jobMonitors.size()
								+" history.size="+jobDetail.getHistorysList().size());
						JobHistoryServiceImpl.cacheGlobalMonitorMap(jobDetail.getHistorysList(),jobMonitors);						
					} else {
						logger.debug("RPSJobMonitorReader readJobMonitor JobMonitor.size=0 "+jobDetail.toString());
					}
				} catch (EdgeServiceFault e) {
					logger.debug("RPSJobMonitorReader readJobMonitor catch Error )",e);
				}
				
				Thread.sleep(100);
			} catch (InterruptedException|RejectedExecutionException e) {
				logger.error("RPSJobMonitorReader readJobMonitor catch Error, and will exit while(true)",e);
				return;
			}
		
		}		
	}
}
