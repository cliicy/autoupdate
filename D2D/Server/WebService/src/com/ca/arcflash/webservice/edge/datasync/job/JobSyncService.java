package com.ca.arcflash.webservice.edge.datasync.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class JobSyncService {
	
	private static Logger logger = Logger.getLogger(JobSyncService.class);
	private static JobSyncService instance = new JobSyncService();
	
	private ExecutorService executor;
	private List<AbstractSyncMonitor> syncMonitors;
	
	public static JobSyncService getInstance() {
		return instance;
	}
	
	private JobSyncService() {
		syncMonitors = new ArrayList<AbstractSyncMonitor>();
		syncMonitors.add(D2DBackupJobSyncMonitor.getInstance());
		syncMonitors.add(VSphereBackupJobSyncMonitor.getInstance());
//		syncMonitors.add(ConversionJobSyncMonitor.getInstance());
		syncMonitors.add(MergeJobSyncMonitor.getInstance());
		syncMonitors.add(VSphereMergeJobSyncMonitor.getInstance());
	}
	
	public synchronized void start() {
		if (executor != null) {
			return;
		}
		
		executor = Executors.newCachedThreadPool();
		
		for (AbstractSyncMonitor monitor : syncMonitors) {
			monitor.start(executor);
		}
		
		logger.info("Job sync service started.");
	}
	
	public synchronized void stop() {
		if (executor == null) {
			return;
		}
		
		for (AbstractSyncMonitor monitor : syncMonitors) {
			monitor.stop();
		}
		
		executor.shutdownNow();
		executor = null;
		
		logger.info("Job sync service stopped.");
	}

}
