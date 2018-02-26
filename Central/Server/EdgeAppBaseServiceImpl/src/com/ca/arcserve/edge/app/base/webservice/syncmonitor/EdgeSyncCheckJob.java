package com.ca.arcserve.edge.app.base.webservice.syncmonitor;


import java.util.Calendar;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeSrmDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;


public class EdgeSyncCheckJob implements Job {

	public static long s_perviousExeSecCount = 0;
	public final int HOUR_SECOND_COUNT = 3600;
	public static final String edgeSyncCheckJobName = "EdgeSyncCheckJob";
	private static Logger _log = Logger.getLogger(EdgeSyncCheckJob.class);
	private static IEdgeSrmDao m_idao = DaoFactory.getDao(IEdgeSrmDao.class);
	
	static {
		s_perviousExeSecCount = Calendar.getInstance().getTimeInMillis();
	}

	public int run() {
		_log.debug("[Edge Sync Check Job Startup]");

		EdgeNodesMap nodesMap = EdgeNodesMap.getInstance();
		IEdgeSyncMonitorConfiguration configuration = new EdgeSyncMonitorConfiguration();

		Calendar c = Calendar.getInstance();
		long curSecCount = c.getTimeInMillis();
		
		if ( (curSecCount - s_perviousExeSecCount) / (HOUR_SECOND_COUNT * 1000)
				% configuration.getRefreshPeriod() == 0) {
			nodesMap.refresh();
			CleanupOldAlertRecords(configuration);
			
			s_perviousExeSecCount = curSecCount;
		}
		
		// if user change the system time to early
		if (curSecCount < s_perviousExeSecCount) {
			s_perviousExeSecCount = curSecCount;
		}

		nodesMap.setConfiguration(configuration);
		nodesMap.checkAll();
		System.out.println(c.getTime().toLocaleString() + " Edge Sync Check Job runing");

		_log.debug("[Edge Sync Check Job End]");
		return 0;
	}


	public void CleanupOldAlertRecords(IEdgeSyncMonitorConfiguration configuration) {
		String condition = "send_time < DATEADD(yy, " + Integer.toString(0 - configuration.getCleanupPeriod()) + ", GETDATE())";
		m_idao.spsrmedgeAlertMessageDelete(condition);
		_log.debug("[Edge Sync Check Job] execute cleanup operation, time:" +
				Calendar.getInstance().getTime().toLocaleString() + ", condition: " +
				condition);
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		run();

	}

}
