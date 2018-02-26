package com.ca.arcflash.webservice.edge.datasync.job;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.jni.model.JJobMonitor;

abstract class AbstractBackupJobSyncMonitor extends AbstractSyncMonitor {
	
	private static Logger logger = Logger.getLogger(AbstractBackupJobSyncMonitor.class);
	
	private Map<String, JJobMonitor> waitingJobMonitor = new HashMap<String, JJobMonitor>();
	private Map<String, Long> waitingJobId = new HashMap<String, Long>();
	
	private String executingUuid;
	private JJobMonitor executingJobMonitor;
	private long executingJobId;
	
	protected String getExecutingUuid() {
		return executingUuid;
	}

	protected JJobMonitor getExecutingJobMonitor() {
		return executingJobMonitor;
	}

	protected long getExecutingJobId() {
		return executingJobId;
	}
	
	@Override
	protected void resetSyncData() {
		waitingJobMonitor.clear();
		waitingJobId.clear();
		executingUuid = null;
		executingJobMonitor = null;
		executingJobId = 0;
	}

	@Override
	protected synchronized void onSyncFailed() {
		if (!waitingJobMonitor.containsKey(executingUuid)) {
			waitingJobMonitor.put(executingUuid, executingJobMonitor);
			waitingJobId.put(executingUuid, executingJobId);
			logger.debug(getConcrateClassPrefix() + "the failed data need to sync again.");
		} else {
			logger.debug(getConcrateClassPrefix() + "there is queued data to sync and ignore the faield data.");
		}
	}

	@Override
	protected synchronized boolean setupSyncData() {
		Iterator<String> uuidIterator = waitingJobMonitor.keySet().iterator();
		
		if (uuidIterator.hasNext()) {
			executingUuid = uuidIterator.next();
			executingJobMonitor = waitingJobMonitor.get(executingUuid);
			executingJobId = waitingJobId.get(executingUuid);
			
			waitingJobMonitor.remove(executingUuid);
			waitingJobId.remove(executingUuid);
			
			logger.debug(getConcrateClassPrefix() + "has more data to sync, uuid = " + executingUuid);
			
			return true;
		} else {
			executingUuid = null;
			executingJobMonitor = null;
			executingJobId = 0;
			
			logger.debug(getConcrateClassPrefix() + "no more data to sync.");
			
			return false;
		}
	}
	
	public synchronized void addSyncData(String uuid, JJobMonitor jobMonitor, long jobId) {
		if (uuid == null || jobMonitor == null) {
			return;
		}
		
		if (!isInitialized() || executingUuid != null) {
			waitingJobMonitor.put(uuid, jobMonitor);
			waitingJobId.put(uuid, jobId);
			
			logger.debug(getConcrateClassPrefix() + "queue the new sync data.");
		} else {
			executingUuid = uuid;
			executingJobMonitor = jobMonitor;
			executingJobId = jobId;
			
			logger.debug(getConcrateClassPrefix() + "start to sync the new data immediately.");
			
			startSync();
		}
	}

}
