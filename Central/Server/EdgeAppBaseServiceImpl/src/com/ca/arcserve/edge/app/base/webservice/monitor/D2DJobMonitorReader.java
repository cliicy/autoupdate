package com.ca.arcserve.edge.app.base.webservice.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.ConvertJobMonitor;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.foredge.ID2D4EdgeService_Oolong;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao.JobHistoryProductType;
import com.ca.arcserve.edge.app.base.common.NamingThreadFactory;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.VMConnectionContextProvider;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.HostInfoCache;
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
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;


/**
 * D2DAgent job monitor tracker which used for get current job detail information
 * 
 * @author lijyo03
 *
 */
public class D2DJobMonitorReader implements JobMonitorReader {
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private static final Logger logger = Logger.getLogger(D2DJobMonitorReader.class);
	private String errorMessage;
	private D2DJobMonitorReader(){}
	private static class LazyHolder {
		public static final D2DJobMonitorReader tracker = new D2DJobMonitorReader();
    }
    public static D2DJobMonitorReader getInstance() {
        return LazyHolder.tracker;
    }
    
	@Override
	@Deprecated 
	public List<JobMonitor> getJobMonitor(JobDetail jobDetail)
			throws EdgeServiceFault {
		logger.error("[D2DAgent] D2DJobMonitorReader getJobMonitor has no implement !!!!");
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public List<FlashJobMonitor> getJobMonitorOnServer(JobDetail jobDetail)
			throws EdgeServiceFault {
		logger.debug("[D2DAgent] start getJobMonitorOnServer jobDetail.getServerId()="+jobDetail.getServerId());
		List<FlashJobMonitor> list = new ArrayList<FlashJobMonitor>();	
		
		try(D2DConnection connection = connectionFactory.createD2DConnection(jobDetail.getServerId())){
			connection.connect();
			ID2D4EdgeService_Oolong service = connection.getService();
			if (jobDetail.getNodeUUIDs()!=null && jobDetail.getNodeUUIDs().size()>0) {		
				for (String uuid:jobDetail.getNodeUUIDs()) {
					if(!jobDetail.splitNodeUUIDStr(uuid)){
						logger.error("[D2DAgent] splitNodeUUIDStr Error param="+uuid);
						continue;
					}
					list.addAll(this.getWaitJobMonitor(jobDetail,service));	
					list.addAll(this.getInstantVMJobMonitor(jobDetail));
					list.addAll(this.getCovertJobMonitor(jobDetail,service));
					list.addAll(this.getMergeJobMonitor(jobDetail,service));
					list.addAll(this.getBaseJobMonitor(jobDetail,service));	
				}			
			} else {
				logger.error("[D2DAgent] start getJobMonitorOnServer has no NodesUUIDS");
			}			
		}catch (Exception e) { 
			//getWaitingJobTable
			if(e instanceof SOAPFaultException){
				String errorMessage = ((SOAPFaultException)e).getFault().getFaultString();				
				if (errorMessage != null && errorMessage.contains(LinuxD2DServiceFault.METHOD_NOT_DEFINED_MESSAGE)) {
					logger.debug("[D2DAgent] getJobMonitorOnServer catch ERROR(Cannot find dispatch method)");
					return D2DAllJobStatusCache.getD2DAllJobStatusCache().getJobStatusInfoList("D2D-"+jobDetail.getNodeId()+"-"+jobDetail.getServerId());	
				}					
			}
			if(errorMessage==null||(!errorMessage.equals(e.getMessage()))){
				errorMessage = e.getMessage();
				logger.error("[D2DAgent] getJobMonitorOnServer catch Error:"+errorMessage);
			}
			return D2DAllJobStatusCache.getD2DAllJobStatusCache().getJobStatusInfoList("D2D-"+jobDetail.getNodeId()+"-"+jobDetail.getServerId());
		}
		logger.debug("[D2DAgent] end getJobMonitorOnServer return size is " + list.size());
		return list;
	}
	
	@Override
	public boolean cancelJob(JobDetail jobDetail) throws EdgeServiceFault {
		logger.debug("[D2DAgent] start cancelJob");
		NodeServiceImpl nodeService = new NodeServiceImpl();
		EdgeHost host = HostInfoCache.getInstance().getHostInfo(jobDetail.getNodeId());
		String vmInstanceUUID = "";
		if(host == null){
			logger.debug("[D2DAgent] cancelJob host is null");
			return false;
		}else{
			if (HostTypeUtil.isVMWareVirtualMachine(host.getRhostType())) { // VMware
				List<EdgeEsxVmInfo> vmList = new LinkedList<>();
				IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
				esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(jobDetail.getNodeId(), vmList);
				if(!vmList.isEmpty()){
					vmInstanceUUID = vmList.get(0).getVmInstanceUuid();
				}
			} else { // Hyper-V
				List<EdgeHyperVHostMapInfo> hostMapInfo = new ArrayList<EdgeHyperVHostMapInfo>();
				IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
				hyperVDao.as_edge_hyperv_host_map_getById(jobDetail.getNodeId(), hostMapInfo);
				if (hostMapInfo != null && hostMapInfo.size() > 0) {
					vmInstanceUUID = hostMapInfo.get(0).getVmInstanceUuid();
				}
			}
		}
		if(StringUtil.isEmptyOrNull(vmInstanceUUID)){
			logger.debug("[D2DAgent] cancelJob is not vm job");
		} else {
			logger.debug("[D2DAgent] cancelJob is vm job");
		}
		if (JobType.JOBTYPE_CONVERSION == jobDetail.getJobType()) {// cancel conversion job
			nodeService.cancelReplication(jobDetail.getNodeId(), "", vmInstanceUUID);
			logger.debug("[D2DAgent] end cancelJob and Cancel succeed : CONVERSION");
			return true;	// Cancel succeed, return 0, else, it will throw exception
		} else if (!StringUtil.isEmptyOrNull(vmInstanceUUID)) {
			try (D2DConnection connection = connectionFactory.createD2DConnection(new VMConnectionContextProvider(jobDetail.getNodeId()))) {
				connection.connect();
				if (jobDetail.getJobType() == JobType.JOBTYPE_VM_BACKUP && jobDetail.getJobId() == 0) { // For vShpere waiting job
					connection.getService().cancelWaitingJob(vmInstanceUUID);
					logger.debug("[D2DAgent] end cancelJob and Cancel succeed : VSHPERE WAITING JOB");
				    return true;
				} else {		
					if(HostTypeUtil.isVapp(host.getRhostType())){
						nodeService.cancelvAppJob(jobDetail.getNodeId(), vmInstanceUUID, jobDetail.getJobId(),jobDetail.getJobType(),connection);
						logger.debug("[D2DAgent] end cancelJob and Cancel succeed : VAPP");
						return true;
					}else {
						connection.getService().cancelJob(jobDetail.getJobId());
						logger.debug("[D2DAgent] end cancelJob and Cancel succeed : VM-OTHER");
						return true;
					}
				}
			}
		} else {				
				try (D2DConnection connection = connectionFactory.createD2DConnection(jobDetail.getNodeId())) {
					connection.connect();
					connection.getService().cancelJob(jobDetail.getJobId());
					logger.debug("[D2DAgent] end cancelJob and Cancel succeed : OTHER");
					return true;
				}
		}
	}
	
	private Collection<? extends FlashJobMonitor> getInstantVMJobMonitor(JobDetail jobDetail) {
		logger.debug("[D2DAgent] start getIVMJobMonitor");
		List<InstantVMJobMonitor> list = InstantVMManager.getInstance().getInstantVMJobMonitor(jobDetail.getNodeId(), jobDetail.getServerId());
		logger.debug("[D2DAgent] end getInstantVMJobMonitor return size is " + list.size());
		return list;
	}

	private List<FlashJobMonitor> getCovertJobMonitor(JobDetail jobDetail,ID2D4EdgeService_Oolong service)
			throws EdgeServiceFault {
		List<FlashJobMonitor> list = new ArrayList<FlashJobMonitor>();
		FlashJobMonitor flashJobMoitor = new ConvertJobMonitor();	

		logger.debug("[D2DAgent] getRepJobMonitor param UUID="+jobDetail.getNodeUUID()+" isVm="+jobDetail.isForVm());
		RepJobMonitor repJobMonitor;
		repJobMonitor = service.getRepJobMonitor(jobDetail.getNodeUUID());				

		if(repJobMonitor == null || (repJobMonitor!=null && repJobMonitor.getId()<0)){
			logger.debug("[D2DAgent] getRepJobMonitor == null return size is 0");
			return list;
		}
		logger.debug("D2DAgent getCovertJobMonitor "+repJobMonitor.toString());
		flashJobMoitor.setJobType(JobType.JOBTYPE_CONVERSION);
		flashJobMoitor.setJobId(repJobMonitor.getId());
		flashJobMoitor.setJobMonitorId("D2D-"+JobType.JOBTYPE_CONVERSION+"-"+repJobMonitor.getId()
				+"-"+jobDetail.getNodeUUID()+"-"+jobDetail.getServerId()+"-"+flashJobMoitor.getJobUUID());			
		flashJobMoitor.setStartTime(repJobMonitor.getRepJobStartTime());
		flashJobMoitor.setElapsedTime(repJobMonitor.getRepJobElapsedTime());	
		flashJobMoitor.setJobPhase(repJobMonitor.getRepPhase());
		if(jobDetail.isForVm()){
			flashJobMoitor.setVmInstanceUUID(jobDetail.getNodeUUID());
			flashJobMoitor.setVmHostName(jobDetail.getHostName());
		} else {
			flashJobMoitor.setD2dUuid(jobDetail.getNodeUUID());
			flashJobMoitor.setD2dServerName(jobDetail.getHostName());
		}
		flashJobMoitor.setNodeId(jobDetail.getNodeId());			
		flashJobMoitor.setRunningServerId(jobDetail.getServerId());
		flashJobMoitor.setRunningOnRPS(false);
		flashJobMoitor.setHistoryProductType(JobHistoryProductType.D2D.getValue());	
		((ConvertJobMonitor)flashJobMoitor).setJobMonitor(repJobMonitor);
		syncJobMonitor(flashJobMoitor,jobDetail);
		list.add(flashJobMoitor);
		logger.debug("[D2DAgent] addCovertJobMonitor:"+flashJobMoitor.toString());

		return list;
	}
	
	private List<FlashJobMonitor> getMergeJobMonitor(JobDetail jobDetail,ID2D4EdgeService_Oolong service)
			throws EdgeServiceFault {
		logger.debug("[D2DAgent] start getMergeJobMonitor param UUID="+jobDetail.getNodeUUID()+" isVm="+jobDetail.isForVm());	
		List<FlashJobMonitor> list = new ArrayList<FlashJobMonitor>();
		FlashJobMonitor flashJobMoitor = null;	
		if(!jobDetail.isForVm())					
			flashJobMoitor = service.getMergeJobStatus();
		else					
			flashJobMoitor = service.getVMMergeJobStatus(jobDetail.getNodeUUID());
		if(flashJobMoitor == null || (flashJobMoitor!=null && flashJobMoitor.getJobId()<=0)){	
			logger.debug("[D2DAgent] getMergeJobMonitor == null return size is 0");
			return list;	
		}
		logger.debug("[D2DAgent] getMergeJobMonitor "+flashJobMoitor.toString());
		flashJobMoitor.setJobMonitorId("D2D-"+flashJobMoitor.getJobType()+"-"+flashJobMoitor.getJobId()
				+"-"+jobDetail.getNodeUUID()+"-"+jobDetail.getServerId()+"-"+flashJobMoitor.getJobUUID());
		flashJobMoitor.setRunningServerId(jobDetail.getServerId());
		flashJobMoitor.setRunningOnRPS(false);
		flashJobMoitor.setNodeId(jobDetail.getNodeId());	
		flashJobMoitor.setHistoryProductType(JobHistoryProductType.D2D.getValue());	
		syncJobMonitor(flashJobMoitor,jobDetail);
		list.add(flashJobMoitor);
		logger.debug("[D2DAgent] addMergeJobMonitor:"+flashJobMoitor.toString());
		return list;
	}
	
	private List<FlashJobMonitor> getBaseJobMonitor(JobDetail jobDetail,ID2D4EdgeService_Oolong service)
			throws EdgeServiceFault {
		logger.debug("[D2DAgent] start getBaseJobMonitor param UUID="+jobDetail.getNodeUUID()+" isVm="+jobDetail.isForVm());
		List<FlashJobMonitor> list = new ArrayList<FlashJobMonitor>();
		com.ca.arcflash.webservice.data.JobMonitor[] jobMonitors = null;
		if(!jobDetail.isForVm()){
			jobMonitors = service.getJobMonitorMap();
		} else {
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(jobDetail.getNodeUUID());
			jobMonitors = service.getVMJobMonitorMap(vm);
		}
		if(jobMonitors == null || jobMonitors.length <= 0){
			logger.debug("[D2DAgent] getBaseJobMonitor size is 0");
			return list;
		}			
		for(com.ca.arcflash.webservice.data.JobMonitor jobMonitor:jobMonitors){					
			if(jobDetail.getNodeId()!=0 && jobMonitor.getNodeId()!=0 && jobDetail.getNodeId()!=jobMonitor.getNodeId()){
				logger.debug("[D2DAgent] Monitor is not for this Node! jobDetail's nodeid:"+jobDetail.getNodeId()+" Monitor's nodeid:"+jobMonitor.getNodeId());
				continue;
			}
			jobMonitor.setJobMonitorId("D2D-"+jobMonitor.getJobType()+"-"+jobMonitor.getJobId()
					+"-"+jobDetail.getNodeUUID()+"-"+jobDetail.getServerId()+"-"+jobMonitor.getJobUUID());
			jobMonitor.setRunningServerId(jobDetail.getServerId());
			jobMonitor.setRunningOnRPS(false);
			jobMonitor.setNodeId(jobDetail.getNodeId());	
			jobMonitor.setHistoryProductType(JobHistoryProductType.D2D.getValue());	
			syncJobMonitor(jobMonitor,jobDetail);
			list.add(jobMonitor);
			logger.debug("[D2DAgent] AddBaseJobMonitor:"+jobMonitor.toString());
		}
		logger.debug("[D2DAgent] end getBaseJobMonitor return size is " + list.size());
		return list;
	}
	
	private List<FlashJobMonitor> getWaitJobMonitor(JobDetail jobDetail,ID2D4EdgeService_Oolong service)
			throws EdgeServiceFault {
		logger.debug("[D2DAgent] start getWaitJobMonitor param UUID="+jobDetail.getNodeUUID()+" isVm="+jobDetail.isForVm());
		List<FlashJobMonitor> list = new ArrayList<FlashJobMonitor>();
		if(!jobDetail.isForVm()){
			logger.debug("[D2DAgent] only HBBU Policy has Waiting Job. so return null");	
			return list;
		}	
		com.ca.arcflash.webservice.data.JobMonitor[] jobMonitors = null;
		jobMonitors = service.getWaitingJobTable(jobDetail.getNodeUUID());
		
		if(jobMonitors == null || jobMonitors.length <= 0){
			logger.debug("[D2DAgent] getWaitJobMonitor size is 0");
			return list;
		}			
		for(com.ca.arcflash.webservice.data.JobMonitor jobMonitor:jobMonitors){					
			if(jobDetail.getNodeId()!=0 && jobMonitor.getNodeId()!=0 && jobDetail.getNodeId()!=jobMonitor.getNodeId()){
				logger.debug("[D2DAgent] Monitor is not for this Node! jobDetail's nodeid:"+jobDetail.getNodeId()+" Monitor's nodeid:"+jobMonitor.getNodeId());
				continue;
			}
			logger.debug("[D2DAgent] getWaitJobMonitor:"+jobMonitor.toString());
			jobMonitor.setJobMonitorId("D2D-"+jobMonitor.getJobType()+"-"+jobMonitor.getJobId()
					+"-"+jobDetail.getNodeUUID()+"-"+jobDetail.getServerId()+"-"+jobMonitor.getJobUUID());
			jobMonitor.setRunningServerId(jobDetail.getServerId());
			jobMonitor.setRunningOnRPS(false);
			jobMonitor.setNodeId(jobDetail.getNodeId());
			jobMonitor.setHistoryProductType(JobHistoryProductType.D2D.getValue());	
			jobMonitor.setVmInstanceUUID(jobDetail.getNodeUUID());
			syncJobMonitor(jobMonitor,jobDetail);
			list.add(jobMonitor);
		}	
		logger.debug("[D2DAgent] end getWaitJobMonitor return size is " + list.size());
		return list;
	}
	
	private void syncJobMonitor(FlashJobMonitor job,JobDetail jobDetail){
		List<JobHistory> historyList = jobDetail.getHistorysList();
		if (historyList==null||historyList.isEmpty()){
			logger.debug("[D2DAgent] syncJobMonitor historyList is null");
			return;	
		}
		if(logger.isDebugEnabled()){
			for (JobHistory history:historyList) {
				logger.debug("[D2DAgent] syncJobMonitor historyList "+history.toString());
			}
			logger.debug("[D2DAgent] syncJobMonitor FlashJobMonitor "+job.toString());
		}
		for (JobHistory history:historyList) {
			if (Integer.parseInt(history.getServerId())!=jobDetail.getServerId()) {
				continue;
			}
			if(history.getJobType()==job.getJobType()
					&& history.getJobId()==job.getJobId()						
					&& Integer.parseInt(history.getAgentId())==job.getNodeId()){
				job.setD2dServerName(history.getAgentNodeName());	
				job.setVmHostName(history.getAgentNodeName());
				break;
			}
		}
	}
	
	private static BlockingQueue<JobDetail> blockingQueueForAgent;	
	private static ThreadPoolExecutor jobsExcutor = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, 
			new LinkedBlockingQueue<Runnable>(),new NamingThreadFactory( "QueryD2DJobMonitor" ));
	
	public static void initReaderThread(BlockingQueue<JobDetail> blockingQueue){
		if(blockingQueueForAgent!=null)
			return;
		logger.debug("D2DJobMonitorReader initReaderThread");
		blockingQueueForAgent = blockingQueue;		
		for(int i=0; i < 10; i++){
			final D2DJobMonitorReader reader = new D2DJobMonitorReader();
			jobsExcutor.submit(new Runnable() {
				
				@Override
				public void run() {
					readJobMonitor(reader);	
				}
			});		
		}
	}
	
	public static void shutdonwThreadReader() {
		if(jobsExcutor!=null)
			jobsExcutor.shutdownNow();
	}
	
	
	public static void readJobMonitor(D2DJobMonitorReader reader){		
		while(true){		
			try {
				JobDetail jobDetail = blockingQueueForAgent.take();
				logger.debug("D2DJobMonitorReader readJobMonitor blockingQueueForRPS.take() "+jobDetail.toString());
				if(jobDetail.getHistorysList()==null||jobDetail.getHistorysList().isEmpty()){					
					logger.debug("D2DJobMonitorReader readJobMonitor NoNeed get as history.size=0");				
					continue;
				}
				try {
					List<FlashJobMonitor> jobMonitors = reader.getJobMonitorOnServer(jobDetail);
					if(jobMonitors!=null && jobMonitors.size()>0){	
						logger.debug("D2DJobMonitorReader readJobMonitor JobMonitor.size"+jobMonitors.size()
								+" history.size="+jobDetail.getHistorysList().size());
						JobHistoryServiceImpl.cacheGlobalMonitorMap(jobDetail.getHistorysList(),jobMonitors);						
					} else {
						logger.debug("D2DJobMonitorReader readJobMonitor JobMonitor.size=0 "+jobDetail.toString());
					}
				} catch (EdgeServiceFault e) {
					logger.debug("D2DJobMonitorReader readJobMonitor catch Error )",e);
				}
				Thread.sleep(100);
			} catch (InterruptedException|RejectedExecutionException e) {
				logger.error("D2DJobMonitorReader readJobMonitor catch Error, and will exit while(true)",e);
				return;
			}
		
		}		
	}	
}
