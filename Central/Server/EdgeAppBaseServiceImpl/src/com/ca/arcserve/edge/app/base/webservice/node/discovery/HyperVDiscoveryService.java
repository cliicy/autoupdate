package com.ca.arcserve.edge.app.base.webservice.node.discovery;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryMonitor.DiscoverServerError;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryPhase;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;

public class HyperVDiscoveryService {
	
	private static Logger logger = Logger.getLogger(HyperVDiscoveryService.class);
	private static HyperVDiscoveryService instance = new HyperVDiscoveryService();
	
	private HyperVDiscoveryTask task;
	private DiscoveryMonitor monitor;
	private Future<Void> job;
	private long startTime;
	
	private IHyperVDiscoveryMonitor discoveryMonitor = new IHyperVDiscoveryMonitor () {
		@Override
		public void onDiscoveryFail(DiscoveryHyperVOption hyperVOption,
				List<DiscoveryVirtualMachineInfo> vmList,
				String errorCode, String errorMessage) {
			String logMessage = EdgeCMWebServiceMessages.getMessage(
					"autoDiscovery_hyperV_JobDiscoverHyperVFail", 
					hyperVOption.getServerName(), errorMessage);
			addActivityLog(Severity.Error, logMessage);
			
			DiscoverServerError hypervError = new DiscoverServerError();
			hypervError.setErrorMsg( logMessage );
			if( hyperVOption.getServerName()!=null ) {
				hypervError.setName( hyperVOption.getServerName() );
			}
			else {
				hypervError.setName("");
			}
			monitor.setServerError(hypervError);
		}

		@Override
		public void onDiscoveryStart(DiscoveryHyperVOption hyperVOption) {
			String logMessage = EdgeCMWebServiceMessages.getMessage(
					"autoDiscovery_hyperV_JobDiscoverHyperVBegin", 
					hyperVOption.getServerName());
			addActivityLog(Severity.Information, logMessage);
		}

		@Override
		public void onDiscoverySuccessful(DiscoveryHyperVOption hyperVOption,
				List<DiscoveryVirtualMachineInfo> vmList) {
			String logMessage = EdgeCMWebServiceMessages.getMessage(
					"autoDiscovery_hyperV_JobDiscoverHyperVEnd", 
					hyperVOption.getServerName(), vmList.size());
			addActivityLog(Severity.Information, logMessage);
		}
		
		@Override
		public void onDiscoveryUpdate(DiscoveryVirtualMachineInfo vm) {
			synchronized (monitor) {
				monitor.setProcessedNodeNum(monitor.getProcessedNodeNum() + 1);
			}
		}

		@Override
		public void onTaskFail(String errorCode, String errorMessage) {
			synchronized (monitor) {
				monitor.setDiscoveryStatus(DiscoveryStatus.DISCOVERY_STATUS_FAILED);
				monitor.setDiscoveryPhase(DiscoveryPhase.DISCOVERY_PHASE_END);
				monitor.setElapsedTime(System.currentTimeMillis() - startTime);
				monitor.setErrorCode(errorCode);
			}
			
			if (errorMessage != null && !errorMessage.isEmpty()) {
				addActivityLog(Severity.Error, errorMessage);
			}
			
			addActivityLog(Severity.Information, EdgeCMWebServiceMessages.getResource("autoDiscovery_hyperV_JobEnd"));
		}

		@Override
		public void onTaskStart() {
			addActivityLog(Severity.Information, EdgeCMWebServiceMessages.getResource("autoDiscovery_hyperV_JobBegin"));
		}

		@Override
		public void onTaskSuccessful() {
			synchronized (monitor) {
				monitor.setDiscoveryStatus(DiscoveryStatus.DISCOVERY_STATUS_FINISHED);
				monitor.setDiscoveryPhase(DiscoveryPhase.DISCOVERY_PHASE_END);
				monitor.setElapsedTime(System.currentTimeMillis() - startTime);
			}
			
			addActivityLog(Severity.Information, EdgeCMWebServiceMessages.getResource("autoDiscovery_hyperV_JobEnd"));
		}
		
	};
	
	private HyperVDiscoveryService() {
		monitor = new DiscoveryMonitor();
		monitor.setCurrentProcessNodeName("");
		monitor.setDiscoveryPhase(DiscoveryPhase.DISCOVERY_PHASE_END);
		monitor.setDiscoveryStatus(DiscoveryStatus.DISCOVERY_STATUS_IDLE);
		monitor.setElapsedTime(0);
		monitor.setErrorCode(null);
		monitor.setProcessedNodeNum(0);
		
		task = new HyperVDiscoveryTask();
	}
	
	public HyperVDiscoveryTask getHyperVDiscoveryTask() {
		return this.task;
	}
	
	public void setHyperVDiscoveryTask(HyperVDiscoveryTask task) {
		this.task = task;
	}
	
	public IHyperVDiscoveryMonitor getHyperVDiscoveryMonitor() {
		return this.discoveryMonitor;
	}
	
	public void setHyperVDiscoveryMonitor(IHyperVDiscoveryMonitor discoveryMonitor) {
		this.discoveryMonitor = discoveryMonitor;
	}
	
	public static HyperVDiscoveryService getInstance() {
		return instance;
	}
	
	public String startDiscovery(DiscoveryHyperVOption[] hyperVOptions) throws EdgeServiceFault {
		if (hyperVOptions == null || hyperVOptions.length == 0) {
			return null;
		}
		synchronized (monitor) {
			if (monitor.getDiscoveryStatus() == DiscoveryStatus.DISCOVERY_STATUS_ACTIVE) {
				return null;
			}
			String uuid = UUID.randomUUID().toString();
			monitor.setUuid(uuid);
			task.setHyperVOptions(hyperVOptions);
			task.setMonitor(discoveryMonitor);
			
			try {
				job = EdgeExecutors.getCachedPool().submit(task);
			} catch (Exception e) {
				logger.error("Submit ESX discovery job failed", e);
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
			}
			
			startTime = System.currentTimeMillis();
			monitor.setDiscoveryStatus(DiscoveryStatus.DISCOVERY_STATUS_ACTIVE);
			monitor.setDiscoveryPhase(DiscoveryPhase.DISCOVERY_PHASE_NODE);
			monitor.setErrorCode(null);
			monitor.setProcessedNodeNum(0);
			monitor.setCurrentProcessNodeName("");
			monitor.getServerErrors().clear();
			return uuid; 
		}
	}

	public DiscoveryMonitor getDiscoveryMonitor() {
		if (monitor.getDiscoveryPhase() != DiscoveryPhase.DISCOVERY_PHASE_END) {
			monitor.setElapsedTime(System.currentTimeMillis() - startTime);
		}
		
		DiscoveryMonitor copyMonitor = new DiscoveryMonitor();
		
		synchronized (monitor) {
			copyMonitor.setCurrentProcessNodeName(monitor.getCurrentProcessNodeName());
			copyMonitor.setDiscoveryPhase(monitor.getDiscoveryPhase());
			copyMonitor.setDiscoveryStatus(monitor.getDiscoveryStatus());
			copyMonitor.setElapsedTime(monitor.getElapsedTime());
			copyMonitor.setErrorCode(monitor.getErrorCode());
			copyMonitor.setProcessedNodeNum(monitor.getProcessedNodeNum());
			copyMonitor.setUuid(monitor.getUuid());
			copyMonitor.setServerErrors( monitor.getServerErrors() );
		}
		
		return copyMonitor;
	}
	
	public void cancel() throws EdgeServiceFault {
		if (job == null) {
			return;
		}
		
		if (job.isDone() || job.isCancelled()
				|| monitor.getDiscoveryStatus() != DiscoveryStatus.DISCOVERY_STATUS_ACTIVE) {
			return;
		}
		
		synchronized (monitor) {
			if (job.isDone() || job.isCancelled()
					|| monitor.getDiscoveryStatus() != DiscoveryStatus.DISCOVERY_STATUS_ACTIVE) {
				return;
			}
			
			job.cancel(true);
			
			monitor.setDiscoveryStatus(DiscoveryStatus.DISCOVERY_STATUS_CANCELLED);
			monitor.setDiscoveryPhase(DiscoveryPhase.DISCOVERY_PHASE_END);
			monitor.setElapsedTime(System.currentTimeMillis() - startTime);
		}
		
		addActivityLog(Severity.Information, EdgeCMWebServiceMessages.getResource("autoDiscovery_hyperV_JobCanceled"));
	}

	private void addActivityLog(Severity severity, String message) {
		IActivityLogService logService = new ActivityLogServiceImpl();
		
		ActivityLog activityLog = new ActivityLog();

		activityLog.setModule(Module.Common);
		activityLog.setSeverity(severity);
		activityLog.setMessage(message);

		try {
			logService.addLog(activityLog);
		} catch (EdgeServiceFault e) {
			logger.debug("add activity log failed", e);
		}
	}
	
	public DiscoveryMonitor getMonitor() {
		return monitor;
	}
	
}
