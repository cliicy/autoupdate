package com.ca.arcserve.edge.app.base.webservice.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.action.ActionTaskData;
import com.ca.arcserve.edge.app.base.webservice.contract.action.ActionTaskParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ValuePair;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.CancelJobParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogProductType;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHyperVHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskStatus;
import com.ca.arcserve.edge.app.base.webservice.jobhistory.JobHistoryServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.monitor.handle.JobMonitorHandlerManager;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail.SourceType;

public class CancelJobTaskRunner <T extends Serializable> implements Runnable{
	private static final Logger logger = Logger.getLogger(UpdateNodeTaskRunner.class);
	
	private ActivityLogServiceImpl logService = new ActivityLogServiceImpl();
	private JobHistoryServiceImpl JobHistoryService = new JobHistoryServiceImpl();
	private ActionTaskManager<T> manager;
	private CountDownLatch doneSignal;
	private CancelJobParameter jobParameter;
	private JobDetail jobDetail;
	private ActionTaskData<T> taskData;
	private T id;
	public CancelJobTaskRunner (T id, CancelJobParameter jobParameter, ActionTaskParameter<T> parameter, CountDownLatch doneSignal, ActionTaskManager<T> manager){
		this.jobParameter = jobParameter;
		this.manager = manager;
		this.doneSignal =doneSignal;
		this.taskData = manager.getData();
		this.id = id;
		this.jobDetail = new JobDetail();
		jobDetail.setServerId(jobParameter.getServerId());
		jobDetail.setNodeId(jobParameter.getNodeId());
		jobDetail.setJobType(jobParameter.getJobType());			
		jobDetail.setJobId(jobParameter.getJobId());
	}
	
	@Override
	public void run(){
		excute();
		doneSignal.countDown();
		manager.updateTask(TaskStatus.InProcess);
	}

	private void excute() {
		try {
			SourceType sourceType = JobHistoryService.getSourceTypeByHostId(jobParameter.getServerId());
			jobDetail.setSource(sourceType);
			if(sourceType == SourceType.NO_TYPE){
				logger.error("[CancelJobTaskRunner] Cancel Job failed, ERROR SOURCETYPE, serverId="+jobDetail.getServerId());
				addTaskDataFailedEntities("Cancel Job failed, ERROR SOURCETYPE");
				return;
			}
			// getJobMonitor 's jobStatus, if jobStatus!=(0-Active) no need to cancel
			Map<String, String> uuidMap = getD2dUUIDAndVMInstanceUUIDById(jobDetail.getNodeId());
			String d2dUUID = uuidMap.get("d2dUUID");
			String vmInstanceUUID = uuidMap.get("vmInstanceUUID");
			if (StringUtil.isEmptyOrNull(vmInstanceUUID)) {
				jobDetail.addNodeUUID("D2D:"+d2dUUID,jobDetail.getNodeId()+"","");
			} else {
				jobDetail.addNodeUUID("VM:"+vmInstanceUUID,jobDetail.getNodeId()+"","");
			}
			List<FlashJobMonitor> jobMonitors = new ArrayList<>();
			jobMonitors = JobMonitorHandlerManager.getJobMonitorOnServer(jobDetail,false);
			if(jobMonitors!=null&&jobMonitors.size()>0){
				logger.debug("[CancelJobTaskRunner] jobMonitors.size="+jobMonitors.size());
				for (FlashJobMonitor job:jobMonitors) {
					if (job.getJobType()==jobDetail.getJobType()
							&& job.getJobId()==jobDetail.getJobId()) {
						logger.debug("[CancelJobTaskRunner] jobMonitor="+job.toString());
						if(job.getJobStatus()==JobStatus.Canceled.getValue()
								||job.getJobStatus()==JobStatus.Finished.getValue()
								||job.getJobStatus()==JobStatus.Failed.getValue()
								||job.getJobStatus()==JobStatus.Incomplete.getValue()
								||job.getJobStatus()==JobStatus.Crash.getValue()
								||job.getJobStatus()==JobStatus.Stop.getValue()
								){
							logger.debug("[CancelJobTaskRunner] job.getJobStatus()="+job.getJobStatus());
							//addTaskDataWarnEntities("Job cannot be canceled, for job status is "+job.getJobStatus());
							addTaskDataSuccessfullEntities();
						} else {
							logger.debug("[CancelJobTaskRunner] start cancelJob");
							boolean result = JobMonitorHandlerManager.cancelJob(jobDetail);
							logger.debug("[CancelJobTaskRunner] end cancelJob result="+result);
							if(result){
								addTaskDataSuccessfullEntities();
							}else{
								addTaskDataFailedEntities("Cancel Job failed");
							}
						}
						break;
					}
				}
			} else {
				logger.debug("[CancelJobTaskRunner] jobMonitors=null");
				//addTaskDataWarnEntities("Job had been run completely.");
				addTaskDataSuccessfullEntities();
			}			
		} catch (EdgeServiceFault e) {
			logger.error("[CancelJobTaskRunner] cancelJobForDashBoard() failed", e);
			addTaskDataFailedEntities("Cancel Job failed, " + e.getMessage());
		}
	}
	
	private void addTaskDataSuccessfullEntities(){
		taskData.getSuccessfullEntities().add(id);
		manager.updateTask(TaskStatus.InProcess);
	}
	
	private void addTaskDataFailedEntities(String message){
		long logId = generateLog(Severity.Error, message);
		taskData.getFailedEntities().add(new ValuePair<T,Long>(id,logId));
		manager.updateTask(TaskStatus.InProcess);
	}
	
	
	private long generateLog(Severity severity, String message) {
		ActivityLog log = new ActivityLog();
		if(jobDetail == null){
			logger.error("jobDetail is null!");
			return 0L;
		}
		log.setHostId(jobDetail.getNodeId());
		log.setJobId(jobDetail.getJobId());
		log.setJobType((int)jobDetail.getJobType());
		log.setModule(Module.CancelMutipleJob);
		log.setSeverity(severity);
		log.setTime(new Date());
		log.setMessage(message);		
		log.setProductType(getProductType(jobDetail.getSource()));
		try {
			 return logService.addLogForCancelJob(log);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return 0L;
		}
	}
	
	private LogProductType getProductType (SourceType sourceType){
		if(sourceType == SourceType.D2D)
			return LogProductType.D2D;
		if(sourceType == SourceType.LINUXD2D)
			return LogProductType.LinuxD2D;
		if(sourceType == SourceType.RPS)
			return LogProductType.RPS;
		if(sourceType == SourceType.ASBU)
			return LogProductType.ASBU;
		return LogProductType.CPM;
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
	
}
