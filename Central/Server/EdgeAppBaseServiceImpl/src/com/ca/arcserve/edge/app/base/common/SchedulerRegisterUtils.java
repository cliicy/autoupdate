package com.ca.arcserve.edge.app.base.common;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.scheduler.EdgeSchedulerException;
import com.ca.arcserve.edge.app.base.schedulers.AutoDiscoveryJob;
import com.ca.arcserve.edge.app.base.schedulers.NodeDeleteJob;
import com.ca.arcserve.edge.app.base.schedulers.QueryD2DStatusJob;
import com.ca.arcserve.edge.app.base.schedulers.SrmArchiveJob;
import com.ca.arcserve.edge.app.base.schedulers.SrmJob;
import com.ca.arcserve.edge.app.base.schedulers.SrmPkiMonitorJob;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTask;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTaskFactory;
import com.ca.arcserve.edge.app.base.webservice.dataSync.RecoveryPointSyncManager;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.SyncArcserveIncJob;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;
import com.ca.arcserve.edge.app.base.webservice.syncmonitor.EdgeSyncMonitor;

public class SchedulerRegisterUtils {

	private static Logger logger = Logger.getLogger( SchedulerRegisterUtils.class );
		
	/**
	 * various jobs should be registered here
	 * @throws EdgeSchedulerException
	 */
		public static  synchronized  void registerJobs() {
			
			try
			{

				// Moved to ??ContextListener.configScheduler()
				//
				// Since Edge VCM and VSphere will not have schedules, but they
				// still need back end threads for deploying policy, put the
				// initialization here will cause Edge VCM and VSphere to initialize
				// many unnecessary mechanism. And actually, the back end threads
				// for policy deploying is too simple to be a schedule. According to
				// Robin's suggestion, we move following initialization to context
				// listener.
				//
				// Pang, Bo (panbo01)
				// 2010-12-17
				//
				//PolicyDeploymentJob.getInstance().initialize();
				
				// initialize SRM scheduler background thread
				EdgeTaskFactory etf = EdgeTaskFactory.getInstance();
				EdgeTask srmTask = new EdgeTask();
				etf.Add(EdgeTaskFactory.EDGE_TASK_SRM, srmTask);
				etf.LanuchTask(EdgeTaskFactory.EDGE_TASK_SRM);
	
				SrmJob.init();
				
				EdgeTask nodeDeleteTask = new EdgeTask();
				etf.Add(EdgeTaskFactory.EDGE_TASK_NodeDelete, nodeDeleteTask);
				etf.LanuchTask(EdgeTaskFactory.EDGE_TASK_NodeDelete);
				NodeDeleteJob.init();
				
				SrmArchiveJob.SrmArchiveWeeklyJob.init();
				SrmArchiveJob.SrmArchiveMonthlyJob.init();
	
				// initialize SRM PKI collection schedule
				EdgeTask srmPkiTask = new EdgeTask();
				etf.Add(EdgeTaskFactory.EDGE_TASK_SRM_PKI_MONITOR, srmPkiTask);
				etf.LanuchTask(EdgeTaskFactory.EDGE_TASK_SRM_PKI_MONITOR);
	
				SrmPkiMonitorJob.init();
	
				EdgeTask arcSyncTask = new EdgeTask();
				arcSyncTask.setMaxExecuteQueueSize(ConfigurationOperator._maxJobQueueSize);
				etf.Add(EdgeTaskFactory.EDGE_TASK_ARCSERVE_SYNC, arcSyncTask);
				etf.LanuchTask(EdgeTaskFactory.EDGE_TASK_ARCSERVE_SYNC);
				SyncArcserveIncJob.init();
	
				AutoDiscoveryJob.init();
				
				//EmailSchedulerJob.init();
				
				EdgeSyncMonitor.getInstance().start();
				
				QueryD2DStatusJob.init();
					
				RecoveryPointSyncManager.getInstance().init();
				
			}
			catch (Throwable t)
			{
				logger.error( "Error initializing jobs.", t );
			}
		}
}
