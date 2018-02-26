package com.ca.arcflash.webservice.edge.datasync.job;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.toedge.IEdgeD2DJobService;

abstract class AbstractMergeJobSyncMonitor extends AbstractSyncMonitor implements Observer {
	
	private static Logger logger = Logger.getLogger(AbstractMergeJobSyncMonitor.class);
	
	private EdgeWebServiceCache<IEdgeD2DJobService> serviceCache;
	private Map<String, MergeStatus> waitingSyncData = new HashMap<String, MergeStatus>();
	private MergeStatus[] executingSyncData;
		
	protected abstract EdgeWebServiceCache<IEdgeD2DJobService> createServiceCache();
	protected abstract Observable getJobService();
	
	@Override
	public void start(ExecutorService executor) {
		serviceCache = createServiceCache();
		super.start(executor);
	}
	
	@Override
	public void stop() {
		getJobService().deleteObserver(this);
		logger.info(getConcrateClassPrefix() + "stop observing the job service.");
		super.stop();
	}
	
	@Override
	protected void ensureInitSync() {
		super.ensureInitSync();
		getJobService().addObserver(this);
		logger.info(getConcrateClassPrefix() + "start to observe the job service.");
	}
	
	@Override
	protected void resetSyncData() {
		waitingSyncData.clear();
		executingSyncData = null;
	}
	
	@Override
	protected synchronized boolean setupSyncData() {
		if (waitingSyncData.isEmpty()) {
			executingSyncData = null;
			logger.debug(getConcrateClassPrefix() + "no more data to sync.");
			return false;
		} else {
			executingSyncData = waitingSyncData.values().toArray(new MergeStatus[0]);
			waitingSyncData.clear();
			logger.debug(getConcrateClassPrefix() + "has more data to sync, size = " + executingSyncData.length);
			return true;
		}
	}
	
	@Override
	protected synchronized void onSyncFailed() {
		for (MergeStatus syncData : executingSyncData) {
			if (!waitingSyncData.containsKey(syncData.getUUID())) {
				waitingSyncData.put(syncData.getUUID(), syncData);
			}
		}
	}
	
	@Override
	protected boolean doSync() {
		return doSync(executingSyncData);
	}
	
	protected boolean doSync(MergeStatus[] syncData) {
		IEdgeD2DJobService service = serviceCache.getService(IEdgeD2DJobService.class);
		if (service == null) {
			return false;
		}
		
		try {
			service.syncMergeJob(syncData);
		} catch (Exception e) {
			logSyncErrorMessage("doSync merge job failed, error message = " + e.getMessage(), e);
			serviceCache.clear();
			return false;
		}
		return true;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof MergeStatus) {
			addSyncData((MergeStatus) arg);
		} else {
			logger.warn(getConcrateClassPrefix() + "the arg of Observer.update should be of type MergeStatus.");
		}
	}
	
	private synchronized void addSyncData(MergeStatus syncData) {
		if (!isInitialized() || executingSyncData != null) {
			waitingSyncData.put(syncData.getUUID(), syncData);
			logger.debug(getConcrateClassPrefix() + "queue the new sync data.");
		} else {
			executingSyncData = new MergeStatus[] { syncData };
			logger.debug(getConcrateClassPrefix() + "start to sync the new data immediately.");
			startSync();
		}
	}

}
