package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

/*import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;*/
import java.util.concurrent.LinkedBlockingQueue;

import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

//import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTask;

public class SyncGDB2EdgeDBMonitor implements Runnable{

	private static final long DEFAULT_SLEEP_TIME = 20000;
	
	private static SyncGDB2EdgeDBMonitor instance;
	
	private LinkedBlockingQueue<SyncFileQueueItem> syncDataFileQueue;
	//private ExecutorService syncService;
	//private EdgeTask syncGDBTask;
	Thread monitorThread;
	SyncGDB2EdgeDBTask syncGDBTaskItem = null;
	ImportDataToDataBase syncRegularPrimaryTaskItem = null;
	private ASBUJobInfo jobinfo = null;
	
	public ASBUJobInfo getJobinfo() {
		return jobinfo;
	}

	public void setJobinfo(ASBUJobInfo jobinfo) {
		this.jobinfo = jobinfo;
	}

	protected SyncGDB2EdgeDBMonitor() {
		syncDataFileQueue = new LinkedBlockingQueue<SyncFileQueueItem>();
		//syncService = Executors.newCachedThreadPool();
		//syncGDBTask = new EdgeTask();
		monitorThread = new Thread(this);
		monitorThread.start();
	}
	
	public static synchronized SyncGDB2EdgeDBMonitor GetInstance(ASBUJobInfo jobinfo) {
		if ( instance == null ) {
			instance = new SyncGDB2EdgeDBMonitor();
		}
		
		instance.setJobinfo(jobinfo);
		
		return instance;
	}
	
	public void AddToFileQueue(SyncFileQueueItem item) {
		if ( item == null) {
			return;
		}
		
		synchronized(syncDataFileQueue) {
			try {
				syncDataFileQueue.put(item);
			} catch (InterruptedException e) {
				ConfigurationOperator
				.debugMessage("AddToFileQueue run err:" + e.getMessage(), e);
			}
		}
	}
	
	public SyncFileQueueItem GetFileFromQueue() {
		synchronized(syncDataFileQueue) {
			try {
				return syncDataFileQueue.take();
			} catch (InterruptedException e) {
				ConfigurationOperator
				.debugMessage("GetFileFromQueue run err:" + e.getMessage(), e);
			}
			
			return null;
		}
	}
	
	public boolean IsQueueEmpty() {
		return syncDataFileQueue.isEmpty();
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				if (!IsQueueEmpty()) {
					SyncFileQueueItem item = GetFileFromQueue();
					if (item == null) {
						continue;
					}
					// SyncGDB2EdgeDBTask syncTaskItem = new
					// SyncGDB2EdgeDBTask();
					try {
						ImportTaskBase syncImporter = GetImportTaskItem(item);
						syncImporter.run();
					} catch (Exception e) {
						ConfigurationOperator
								.debugMessage("SyncGDB2EdgeDBMonit run err:"
										+ e.getMessage(), e);
					}
					/*
					 * try { syncGDBTask.AddToWaitingQueue(syncTaskItem); }
					 * catch (InterruptedException e1) { e1.printStackTrace(); }
					 */
				} else {
					try {
						Thread.sleep(DEFAULT_SLEEP_TIME);
					} catch (InterruptedException e) {
						//e.printStackTrace();
						break;
					}
				}
			}
		} catch (Throwable e) {
			ConfigurationOperator.errorMessage(e.getMessage());
			ConfigurationOperator.errorMessage("Thread of SyncGDB2EdgeDBMonitor is interrupted Clear it.");
			synchronized (instance) {
				instance.syncDataFileQueue.clear();
				SyncGDB2EdgeDBMonitor.instance = null;
			}
		}
	}
	
	private ImportTaskBase GetImportTaskItem(SyncFileQueueItem item)
	{
		if (item.getSyncType() == ConfigurationOperator._ArcserveType)
		{		
			if (syncRegularPrimaryTaskItem == null)
				syncRegularPrimaryTaskItem = new ImportDataToDataBase();
			syncRegularPrimaryTaskItem.SetConfiguration(jobinfo, item);
			return syncRegularPrimaryTaskItem;
		}
		else
		{
			if(syncGDBTaskItem == null)
				syncGDBTaskItem = new SyncGDB2EdgeDBTask();
			syncGDBTaskItem.SetConfiguration(jobinfo, item);
			return syncGDBTaskItem;
		}
	}

}
