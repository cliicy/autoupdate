package com.ca.arcserve.edge.app.base.webservice.syncmonitor;


import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;

public class EdgeSyncMonitor {

	public static synchronized EdgeSyncMonitor getInstance() {
		if (instance == null) {
			instance = new EdgeSyncMonitor();
		}

		return instance;
	}

	private EdgeSyncMonitor() {
		EdgeExecutors.getSchedulePool().scheduleAtFixedRate(new SystemTimerChangeMonitor(), 0, 60000, TimeUnit.MILLISECONDS);
	}
	
	public static SimpleTriggerImpl makeSecondlyTrigger(int intervalInSeconds, int repeatCount) {
		SimpleTriggerImpl trig = new SimpleTriggerImpl();
		trig.setRepeatInterval(intervalInSeconds * 1000l);
		trig.setRepeatCount(repeatCount);
		trig.setStartTime(new Date());

		return trig;
	}
	
	public EdgeSyncMonitorStatus start() {
		try {
			IEdgeSyncMonitorConfiguration configuration = new EdgeSyncMonitorConfiguration();
			Scheduler scheduler = SchedulerUtilsImpl.getScheduler();

			JobDetail jd = scheduler.getJobDetail(new JobKey(EdgeSyncCheckJob.edgeSyncCheckJobName,
					edgeSyncMonitorName));
			if (jd != null) {
				_log.debug("[EdgeSyncMonitor] existed the sync monitor job");
				return EdgeSyncMonitorStatus.JobExisted;
			}

			JobDetail jobDetail = new JobDetailImpl(EdgeSyncCheckJob.edgeSyncCheckJobName, edgeSyncMonitorName,EdgeSyncCheckJob.class);
			SimpleTriggerImpl trigger = makeSecondlyTrigger(configuration.getMonitorInterval(), -1);
			trigger.setName(EdgeSyncCheckJob.edgeSyncCheckJobName + "-Trigger");
			trigger.setGroup(edgeSyncMonitorName + "-Trigger");
			scheduler.scheduleJob(jobDetail, trigger);


			_log.debug("[EdgeSyncMonitor] succeed.");
			return EdgeSyncMonitorStatus.Succeed;

		} catch (SchedulerException e) {
			_log.debug("[EdgeSyncMonitor] ", e);
			return EdgeSyncMonitorStatus.Failed;
		}
	}

	public EdgeSyncMonitorStatus stop() {

		try {
			Scheduler scheduler = SchedulerUtilsImpl.getScheduler();
			return scheduler.deleteJob(new JobKey(EdgeSyncCheckJob.edgeSyncCheckJobName, edgeSyncMonitorName))
					? EdgeSyncMonitorStatus.Succeed : EdgeSyncMonitorStatus.Failed;

		} catch (SchedulerException e) {
			_log.debug("[EdgeSyncMonitor] ", e);
			return EdgeSyncMonitorStatus.Failed;
		}
	}


	public static final String edgeSyncMonitorName = "EdgeSyncMonitor";
	private static Logger _log = Logger.getLogger(EdgeSyncMonitor.class);
	private static EdgeSyncMonitor instance = null;

	public enum EdgeSyncMonitorStatus {
		Succeed, Failed, JobExisted
	}

	public class SystemTimerChangeMonitor implements Runnable {

		private Calendar lastTime = null;

		@Override
		public void run() {
			try {
				Calendar currentTime = Calendar.getInstance();
				if (lastTime != null && lastTime.after(currentTime)) {
					EdgeSyncMonitor monitor = EdgeSyncMonitor.getInstance();
					monitor.stop();
					monitor.start();
				}

				lastTime = currentTime;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				_log.error(e.getMessage(), e);
			}
		}

	}
}
