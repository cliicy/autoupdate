package com.ca.arcserve.edge.app.base.webservice.monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao;
import com.ca.arcserve.edge.app.base.common.NamingThreadFactory;
import com.ca.arcserve.edge.app.base.common.connection.ASBUConnection;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.HostInfoCache;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.asbuintegration.ASBUDestinationManager;
import com.ca.arcserve.edge.app.base.webservice.contract.asbumonitor.ASBUJobMonitorModel;
import com.ca.arcserve.edge.app.base.webservice.contract.asbumonitor.JobMonitorModel;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;
import com.ca.arcserve.edge.app.base.webservice.d2d.D2DEdgeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DAllJobStatusCache;
import com.ca.arcserve.edge.app.base.webservice.jobhistory.JobHistoryServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobMonitorResult;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobMonitorResultModel;
import com.ca.arcserve.edge.app.rps.webservice.datastore.RPSDataStoreServiceImpl;
import com.ca.asbu.webservice.IArchiveToTapeService;
import com.google.gson.Gson;

/**
 * ASBU job monitor tracker which used for get current job detail information\
 * 
 * @author zhati04
 *
 */
public class ASBUJobMonitorReader implements JobMonitorReader {
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private static RPSDataStoreServiceImpl dsService = new RPSDataStoreServiceImpl();
	private static final Logger logger = Logger.getLogger(ASBUJobMonitorReader.class);
	private String rpsUUID;
	private String rpsHostName;
	private int rpsId;
	private String datastoreUUID;
	private String datastoreName;
	private int serverId = 0;
	private String serverHostName;
	private String errorMessage;
	private static IEdgeJobHistoryDao jobHistoryDao = DaoFactory.getDao(IEdgeJobHistoryDao.class);
	
	private ASBUJobMonitorReader(){}
	private static class LazyHolder {
		public static final ASBUJobMonitorReader tracker = new ASBUJobMonitorReader();
    }
    public static ASBUJobMonitorReader getInstance() {
        return LazyHolder.tracker;
    }
	
	@Override
	@Deprecated 
	public List<JobMonitor> getJobMonitor(JobDetail jobDetail) throws EdgeServiceFault{
		logger.debug("[ASBU] start get job details jobDetail.getServerId()="+jobDetail.getServerId());
		IArchiveToTapeService service;
		List<JobMonitor> jobMonitors = new ArrayList<>();
		try(ASBUConnection connection = connectionFactory.createASBUConnection(jobDetail.getServerId())){
			service = ASBUDestinationManager.getInstance().initArchiveToTapeService(connection);
			logger.debug("[ASBU] connect success, info parameter is" + jobDetail.getNodeUUIDs());
			if(logger.isDebugEnabled()){
				logger.debug("[ASBU] get job monitor info parameter are: ");
				for(String nodeUUID : jobDetail.getNodeUUIDs()){
					logger.debug(nodeUUID);
				}
			}
			String resultStr = service.getJobMonitorInfo(jobDetail.getNodeUUIDs());
			logger.debug("[ASBU] returned str is " + resultStr);
//			String resultStr = "{\"JobMonResult\":[]}";
//			String resultStr = "{"JobMonResult":[{"PlanGlobalID":"bb82ce0b-8eb3-4a62-bdb3-663f24a1a0a8","PlanVersionID":"8977af57-9428-4d32-98c6-5f7206cae2c7","NodeUUID":"fc961243-6683-ca38-58d9-1231392befcf","NodeName":"10.57.58.99","ScheduleType":"0","JobID":"239","KBPerSec":"3423","JobPercent":"58","ElapsedTime":"16622","RemainTime":"1092","KBProcessed":"5163592","KBEstimated":"8902745","Status":"0","StartTime":"1447186582","EndTime":"1447187674","BackupSession":[38],"CurrentSession":[38],"Encrypt":"1","Compress":"0","RPSUUID":"4f596ffd-9429-4878-888a-e9b1c70199d1","DataStoreUUID":"aa0daaf3-fbd8-485e-9ef6-ad9b4b383824","GroupName":"PGRP1"}]}";
			JobMonitorResult result = new Gson().fromJson(resultStr, JobMonitorResult.class);
			logger.debug("[ASBU] convert success");
			if(result != null && result.getJobMonResult() != null){
				for(JobMonitorResultModel model : result.getJobMonResult()){
					if(StringUtil.isEmptyOrNull(model.getJobID()) 
							&& StringUtil.isEmptyOrNull(model.getNodeUUID())){
						logger.info("[ASBU] returned str is " + resultStr +", serverId="+jobDetail.getServerId()+", NodeUUIDs="+jobDetail.getNodeUUIDs());
						for(String nodeUUID : jobDetail.getNodeUUIDs()){
							logger.info("[ASBU] param nodeuuid:"+nodeUUID);
						}
						continue;
					}
					JobMonitor monitor = new JobMonitor();
					monitor.setPlanGlobalUUID(model.getPlanGlobalID());
					monitor.setPlanUUID(model.getPlanVersionID());
					monitor.setNodeUUID(model.getNodeUUID());
					monitor.setElapsedTime(model.getElapsedTime());
					monitor.setEndTime(StringUtil.isNotEmpty(model.getEndTime()) ? new Date(Long.valueOf(model.getEndTime())) : null);
					monitor.setJobId(model.getJobID());
					monitor.setJobPercent(model.getJobPercent());
					monitor.setNodeName(model.getNodeName());
					monitor.setKbEstimated(model.getKBEstimated());
					monitor.setKbPerSec(model.getKBPerSec());
					monitor.setKbProcessed(model.getKBProcessed());
					monitor.setStartTime(StringUtil.isNotEmpty(model.getStartTime()) ? new Date(Long.valueOf(model.getStartTime())) : null);
					monitor.setScheduleType(model.getScheduleType());
					monitor.setRemainTime(model.getRemainTime());
					monitor.setStatus(model.getStatus());
					monitor.setJobType(JobType.JOBTYPE_ARCHIVE_TO_TAPE);
					monitor.setServerId(jobDetail.getServerId());
					monitor.setBackupSession(model.getBackupSession());
					monitor.setCurrentSession(model.getCurrentSession());
					monitor.setEncrypt(model.getEncrypt());
					monitor.setCompress(model.getCompress());
					monitor.setRpsUUID(model.getRPSUUID());
					monitor.setDataStoreUUID(model.getDataStoreUUID());
					monitor.setGroupName(model.getGroupName());
					monitor.setRelatedNodes(model.getRelatedNodes());
					jobMonitors.add(monitor);
				}
			}
		}catch (Exception e) { 
			if(errorMessage==null|| (!errorMessage.equals(e.getMessage()))){
				errorMessage = e.getMessage();
				logger.error("[ASBU] getJobMonitor catch Error:"+errorMessage);
			}
			return null;
		}
		return jobMonitors;
	}

	@Override
	public boolean cancelJob(JobDetail jobDetail) throws EdgeServiceFault {
		if(jobDetail == null){
			logger.error("[ASBU] jobDetail must not be null");
		}
		/*if(StringUtil.isEmptyOrNull(jobDetail.getNodeUUID())){
			logger.error("node uuid is required");
		}*/
		if(jobDetail.getJobId() <= 0){
			logger.error("[ASBU] JobId is required:" + jobDetail.getJobId());
		}
		if(logger.isDebugEnabled()){
			logger.debug("[ASBU] node uuid is " + jobDetail.getNodeUUID());
			logger.debug("[ASBU] schedule type is " + jobDetail.getScheduleType());
		}
		IArchiveToTapeService service;
		try(ASBUConnection connection = connectionFactory.createASBUConnection(jobDetail.getServerId())){
			service = ASBUDestinationManager.getInstance().initArchiveToTapeService(connection);
			int result = service.cancelJob(String.valueOf(jobDetail.getJobId()), jobDetail.getScheduleType());
			return result == 0;
		}
	}

	@Override
	public List<FlashJobMonitor> getJobMonitorOnServer(JobDetail jobDetail)
			throws EdgeServiceFault {
		List<String> nodeUUIDs = new ArrayList<>();
		if (jobDetail.getNodeUUIDs()!=null && jobDetail.getNodeUUIDs().size()>0) {		
			for (String uuid:jobDetail.getNodeUUIDs()) {
				if(!jobDetail.splitNodeUUIDStr(uuid)){
					logger.debug("[ASBU] getJobMonitorOnServer splitNodeUUIDStr Error param="+uuid);
					continue;
				}
				logger.debug("[ASBU] getJobMonitorOnServer splitNodeUUIDStr add NodeUUID="+jobDetail.getNodeUUID());
				nodeUUIDs.add(jobDetail.getNodeUUID());
			}
		}
		if(nodeUUIDs==null||nodeUUIDs.size()<=0){
			logger.info("[ASBU] getJobMonitorOnServer param=null no need to getMonitor");
			return null;
		}
		jobDetail.setNodeUUIDs(nodeUUIDs);
		List<JobMonitor> jobsList = this.getJobMonitor(jobDetail);
		if(jobsList==null||jobsList.isEmpty()){
			logger.info("[ASBU] getJobMonitorOnServer return size is 0");
			return null;
		}
		List<FlashJobMonitor> modelList = new ArrayList<FlashJobMonitor>();
		for (JobMonitor jobMonitor:jobsList) {
			JobMonitorModel uiModel = new ASBUJobMonitorModel();
			if(StringUtil.isNotEmpty(jobMonitor.getJobId())){
				uiModel.setJobId(Integer.valueOf(jobMonitor.getJobId()));
			}
			uiModel.setPlanVersionUUID(jobMonitor.getPlanUUID());
			uiModel.setNodeUUID(jobMonitor.getNodeUUID());
			uiModel.setElapsedTime(StringUtil.isNotEmpty(jobMonitor.getElapsedTime()) ? Long.valueOf(jobMonitor.getElapsedTime()) : 0L);
			uiModel.setEndTime(jobMonitor.getEndTime() != null ? jobMonitor.getEndTime().getTime() : 0L);
			uiModel.setJobId(StringUtil.isNotEmpty(jobMonitor.getJobId()) ? Integer.valueOf(jobMonitor.getJobId()) : 0);
			uiModel.setJobPercent(jobMonitor.getJobPercent());
			uiModel.setNodeName(jobMonitor.getNodeName());
			uiModel.setKbEstimated(StringUtil.isNotEmpty(jobMonitor.getKbEstimated()) ? Long.valueOf(jobMonitor.getKbEstimated()) : 0L);
			uiModel.setKbPerSec(StringUtil.isNotEmpty(jobMonitor.getKbPerSec()) ? Long.valueOf(jobMonitor.getKbPerSec()) : 0L);
			uiModel.setKbProcessed(StringUtil.isNotEmpty(jobMonitor.getKbProcessed()) ? Long.valueOf(jobMonitor.getKbProcessed()) : 0L);
			uiModel.setStartTime(jobMonitor.getStartTime() != null ? jobMonitor.getStartTime().getTime() : 0L);
			uiModel.setScheduleType(jobMonitor.getScheduleType());
			uiModel.setRemainTime(StringUtil.isNotEmpty(jobMonitor.getRemainTime()) ? Long.valueOf(jobMonitor.getRemainTime()) : 0L);
			uiModel.setStatus(jobMonitor.getStatus());
			uiModel.setJobType(jobMonitor.getJobType());
			uiModel.setServerId(jobMonitor.getServerId());
			if(serverId==jobMonitor.getServerId()){
				uiModel.setServerNodeName(serverHostName);
			} else {
				EdgeHost host = HostInfoCache.getInstance().getHostInfo(jobMonitor.getServerId());
				uiModel.setServerNodeName(host.getRhostname());
				serverId = jobMonitor.getServerId();
				serverHostName = new String(host.getRhostname());
			}
			uiModel.setCompress(jobMonitor.getCompress());
			uiModel.setEncrypt(jobMonitor.getEncrypt());
			uiModel.setBackupSession(jobMonitor.getBackupSession());
			uiModel.setCurrentSession(jobMonitor.getCurrentSession());
			uiModel.setRpsUUID(jobMonitor.getRpsUUID());
			uiModel.setDataStoreUUID(jobMonitor.getDataStoreUUID());
			if(StringUtil.isNotEmpty(jobMonitor.getRpsUUID())){
				if(rpsUUID!=null&&rpsUUID.equals(jobMonitor.getRpsUUID())){
					uiModel.setSrcRPS(rpsHostName);
					if(datastoreUUID!=null&&datastoreUUID.equals(jobMonitor.getRpsUUID())){
						uiModel.setSrcDataStore(datastoreName);
					} else {
						DataStoreSettingInfo dataStoreInfo = dsService.getDataStoreByGuid(rpsId, jobMonitor.getDataStoreUUID());
						if(dataStoreInfo != null){
							uiModel.setSrcDataStore(dataStoreInfo.getDisplayName());
							datastoreUUID = new String(jobMonitor.getRpsUUID());
							datastoreName = new String(dataStoreInfo.getDisplayName());
						}
					}
				} else {
					int[] rhostid = new int[1];
					String[] hostname = new String[1];
					String[] protocol = new String[1];
					int[] port = new int[1];
					D2DEdgeServiceImpl.GetRpsConnInfoByUUID(jobMonitor.getRpsUUID(), rhostid, hostname, protocol, port);
					if(StringUtil.isNotEmpty(hostname[0])){
						uiModel.setSrcRPS(hostname[0]);
						rpsUUID = new String(jobMonitor.getRpsUUID());
						rpsHostName = new String(hostname[0]);
						rpsId = rhostid[0];
					}					
					if(rhostid[0] != 0 && StringUtil.isNotEmpty(jobMonitor.getDataStoreUUID())){
						if(datastoreUUID!=null&&datastoreUUID.equals(jobMonitor.getRpsUUID())){
							uiModel.setSrcDataStore(datastoreName);
						} else {
							DataStoreSettingInfo dataStoreInfo = dsService.getDataStoreByGuid(rhostid[0], jobMonitor.getDataStoreUUID());
							if(dataStoreInfo != null){
								uiModel.setSrcDataStore(dataStoreInfo.getDisplayName());
								datastoreUUID = new String(jobMonitor.getRpsUUID());
								datastoreName = new String(dataStoreInfo.getDisplayName());
							}
						}
					}
				}				
			}
			uiModel.setGroupName(jobMonitor.getGroupName());
			uiModel.setRelatedNodes(jobMonitor.getRelatedNodes());
			uiModel.setD2dUuid(jobMonitor.getNodeUUID());
			uiModel.setRunningServerId(jobMonitor.getServerId());
			uiModel.setJobMonitorId("ASBU-"+jobMonitor.getJobType()+"_"+jobMonitor.getJobId()+"_"+jobMonitor.getServerId()+"_"+jobMonitor.getNodeUUID());
			
			List<JobHistory> historys = jobDetail.getHistorysList();
			if(historys!=null && (!historys.isEmpty())){
				if(logger.isDebugEnabled()){
					for (JobHistory history:historys) {
						logger.debug("[ASBU] syncJobMonitor historyList "+history.toString());
					}
					logger.debug("[ASBU] syncJobMonitor uiModel="+uiModel.toString());
				}			
				for (JobHistory history:historys) {
					if (Integer.parseInt(history.getServerId())!=jobDetail.getServerId()) {
						logger.debug("[ASBU] syncJobMonitor serverId not equals history="+history.toString());
						continue;
					}
					if(history.getJobType()==uiModel.getJobType()
							&& history.getJobId()==uiModel.getJobId()
							//&& Integer.parseInt(history.getServerId())==jobDetail.getServerId()
							&& history.getAgentUUID().contains(uiModel.getNodeUUID())){
						uiModel.setD2dServerName(history.getAgentNodeName());	
						uiModel.setVmHostName(history.getAgentNodeName());
						uiModel.setNodeName(history.getAgentNodeName()); // show in progressPlan 's nodeName
						logger.debug("[ASBU] syncJobMonitor end uiModel="+uiModel.toString());
						break;
					}
				}	
			}
			modelList.add(uiModel);
		}		
		return modelList;
	}


	private static BlockingQueue<JobDetail> blockingQueueForASBU;	
	private static ThreadPoolExecutor jobsExcutor = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, 
			new LinkedBlockingQueue<Runnable>(),new NamingThreadFactory( "QueryASBUJobMonitor" ));
	
	public static void initReaderThread(BlockingQueue<JobDetail> blockingQueue){
		if(blockingQueueForASBU!=null)
			return;
		logger.debug("ASBUJobMonitorReader initReaderThread");
		blockingQueueForASBU = blockingQueue;
		for(int i=0; i < 10; i++){
			final ASBUJobMonitorReader reader = new ASBUJobMonitorReader();
			jobsExcutor.submit(new Runnable() {
				
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
	
	
	public static void readJobMonitor(ASBUJobMonitorReader reader){		
		while(true){		
			try {
				JobDetail jobDetail = blockingQueueForASBU.take();
				logger.debug("ASBUJobMonitorReader readJobMonitor blockingQueueForRPS.take() "+jobDetail.toString());
				if(jobDetail.getHistorysList()==null||jobDetail.getHistorysList().isEmpty()){					
					logger.debug("ASBUJobMonitorReader readJobMonitor NoNeed get as history.size=0");				
					continue;
				}
				try {
					List<FlashJobMonitor> jobMonitors = reader.getJobMonitorOnServer(jobDetail);
					syncHistoryAndMonitor(jobDetail.getHistorysList(),jobMonitors);
					if(jobMonitors!=null && jobMonitors.size()>0){
						logger.debug("ASBUJobMonitorReader readJobMonitor JobMonitor.size"+jobMonitors.size()
								+" history.size="+jobDetail.getHistorysList().size());
						JobHistoryServiceImpl.cacheGlobalMonitorMap(jobDetail.getHistorysList(),jobMonitors);						
					} else {
						logger.debug("ASBUJobMonitorReader readJobMonitor JobMonitor.size=0 "+jobDetail.toString());
					}
				} catch (EdgeServiceFault e) {
					logger.debug("ASBUJobMonitorReader readJobMonitor catch Error )",e);
				}
				Thread.sleep(100);
			} catch (InterruptedException|RejectedExecutionException e) {
				logger.error("ASBUJobMonitorReader readJobMonitor catch Error, and will exit while(true)",e);
				return;
			}
		
		}		
	}	
	
	private static void syncHistoryAndMonitor(List<JobHistory> historys, List<FlashJobMonitor> monitors){		
		int historySize = (historys==null)?0:historys.size();
		int monitorSize = (monitors==null)?0:monitors.size();
		if(historySize>monitorSize)
			logger.debug("ASBUJobMonitorReader syncHistoryAndMonitor historySize=" + historySize+" monitorSize=" + monitorSize);
		
		if (historys==null || historys.isEmpty())
			return;
		if(monitors == null)
			monitors = new ArrayList<FlashJobMonitor>();	
	
		for(JobHistory history : historys){
			if(history!=null&&history.getJobType()==71){
				JobHistoryServiceImpl.removeGlobalMonitorMap(history.getHistoryId());
			}
			boolean findflg = false;			
			for (FlashJobMonitor monitor : monitors) {	
				if(!JobHistoryServiceImpl.compareHistoryAndMonitor(monitor, history,true)){
					continue;
				}						
				findflg = true;
				break;
			}
			// history no monitor corresponding will be delete
			if (!findflg) {
				logger.info("ASBUJobMonitorReader syncHistoryAndMonitor delete no corresponding's history:"+ history.toString());
				jobHistoryDao.as_edge_d2dJobHistory_monitor_delete(history.getJobId(), history.getJobType(),
						Integer.parseInt(history.getAgentId()),Integer.parseInt(history.getServerId()));
				D2DAllJobStatusCache.getD2DAllJobStatusCache().removeCacheJobMonitorByHistory(history);
			}		
		}
	}
	
}
