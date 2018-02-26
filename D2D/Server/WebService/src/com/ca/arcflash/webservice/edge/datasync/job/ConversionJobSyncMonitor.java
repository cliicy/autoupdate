package com.ca.arcflash.webservice.edge.datasync.job;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dstatus.SyncD2DStatusService;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;

public class ConversionJobSyncMonitor extends AbstractSyncMonitor {
	
	private static Logger logger = Logger.getLogger(ConversionJobSyncMonitor.class);
	private static ConversionJobSyncMonitor instance = new ConversionJobSyncMonitor();
	
	private EdgeWebServiceCache<IEdgeD2DService> serviceCache = new EdgeWebServiceCache<IEdgeD2DService>(ApplicationType.VirtualConversionManager);
	
	private Map<String, RepJobMonitor> waitingJobMonitor = new HashMap<String, RepJobMonitor>();
	private Map<String, D2DStatusInfo> waitingJobStatus = new HashMap<String, D2DStatusInfo>();
	
	private String executingUuid;
	private RepJobMonitor executingJobMonitor;
	private D2DStatusInfo executingJobStatus;
	
	public static ConversionJobSyncMonitor getInstance() {
		return instance;
	}
	
	private ConversionJobSyncMonitor() {
	}

	@Override
	protected void resetSyncData() {
		waitingJobMonitor.clear();
		waitingJobStatus.clear();
		executingUuid = null;
		executingJobMonitor = null;
		executingJobStatus = null;
	}

	@Override
	protected void doInitSync() throws SOAPFaultException, WebServiceException, Exception {
//		SyncD2DStatusService.getInstance().syncVCMStatusAll();
	}

	@Override
	protected boolean doSync() {
		IEdgeD2DService service = serviceCache.getService(IEdgeD2DService.class);
		if (service == null) {
			return false;
		}
		
		try {
			service.syncConversionJobInfo(executingUuid, executingJobMonitor, executingJobStatus);
		} catch (Exception e) {
			logSyncErrorMessage("doSync conversion job status failed, error message = " + e.getMessage(), e);
			serviceCache.clear();
			return false;
		}
		
		if (executingJobStatus != null) {
			logger.info("sync conversion job monitor and d2d status info successful.");
		}
		
		return true;
	}

	@Override
	protected synchronized void onSyncFailed() {
		if (!waitingJobMonitor.containsKey(executingUuid)) {
			waitingJobMonitor.put(executingUuid, executingJobMonitor);
			logger.debug("the failed job monitor need to sync again.");
		} else {
			logger.debug("there is queued job monitor to sync and ignore the faield data.");
		}
		
		if (executingJobStatus != null && !waitingJobStatus.containsKey(executingUuid)) {
			waitingJobStatus.put(executingUuid, executingJobStatus);
			logger.debug("the failed job status info need to sync again.");
		} else {
			logger.debug("there is queued job status info to sync and ignore the faield data.");
		}
	}

	@Override
	protected synchronized boolean setupSyncData() {
		Iterator<String> uuidIterator = waitingJobMonitor.keySet().iterator();
		
		if (uuidIterator.hasNext()) {
			executingUuid = uuidIterator.next();
			executingJobMonitor = waitingJobMonitor.get(executingUuid);
			executingJobStatus = waitingJobStatus.get(executingUuid);
			
			waitingJobMonitor.remove(executingUuid);
			waitingJobStatus.remove(executingUuid);
			
			logger.debug("has more data to sync, uuid = " + executingUuid);
			
			return true;
		} else {
			executingUuid = null;
			executingJobMonitor = null;
			executingJobStatus = null;
			
			logger.debug("no more data to sync.");
			
			return false;
		}
	}
	
	public synchronized void addSyncData(String uuid, RepJobMonitor jobMonitor, D2DStatusInfo jobStatus) {
		if (uuid == null || jobMonitor == null) {
			return;
		}
		
		if (!isInitialized() || executingUuid != null) {
			waitingJobMonitor.put(uuid, jobMonitor);
			if (jobStatus != null) {
				waitingJobStatus.put(uuid, jobStatus);
			}
			
			logger.debug("queue the new sync data.");
		} else {
			executingUuid = uuid;
			executingJobMonitor = jobMonitor;
			executingJobStatus = jobStatus;
			
			logger.debug("start to sync the new data immediately.");
			
			startSync();
		}
	}

}
