package com.ca.arcserve.edge.app.base.schedulers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcserve.edge.app.base.appdaos.EdgeD2DNodeStatus;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.common.ApplicationUtil;
import com.ca.arcserve.edge.app.base.common.NamingThreadFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.WindowsRegistry;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;

public class QueryD2DStatusJob implements Job {
	private static Logger log = Logger.getLogger(QueryD2DStatusJob.class);

	
	public static void init() {
		isFirst = true;
		scheduleRegMonJob();
	}

	public static volatile boolean isRunning = false;
	private static BlockingQueue<Runnable> bq = new LinkedBlockingQueue<Runnable>();
	private static ThreadPoolExecutor jobsExcutor = new ThreadPoolExecutor(Util.concurNo, Util.concurNo, 0L, TimeUnit.MILLISECONDS, bq,
		new NamingThreadFactory( "QueryD2DStatusJob" ));
	
	public static void shutdownThreadPools()
	{
		if (jobsExcutor != null)
			jobsExcutor.shutdownNow();
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		if (jobsExcutor.getActiveCount() == 0 && bq.size() == 0) {
			try {
				// jobsExcutor.prestartAllCoreThreads();
				// Get D2d node list
				List<Runnable> list = getD2DHostQueryTask();
				for (Runnable r : list) {
					jobsExcutor.execute(r);
				}
				// Get VM node list
				/*list = getVMHostQueryTask();
				for (Runnable r : list) {
					jobsExcutor.execute(r);
				}*/
				//Get linux d2d server list
				list = getLinuxD2DServerQueryTask();
				for (Runnable r : list) {
					jobsExcutor.execute(r);
				}
			} catch (Throwable th) {
				log.warn("QueryD2DStatusJob.execute:", th);
			} 
			log.debug("Done...");
		} else {
			log.info("Skip this job, " + "Current active count = " + jobsExcutor.getActiveCount() + " and BQ size = " + bq.size());
		}
	}

	private static synchronized boolean tryRun() {
		if (!isRunning) {
			isRunning = true;
			return true;
		}
		return false;
	}

	private List<Runnable> getD2DHostQueryTask() {
		List<Runnable> retlst = new ArrayList<Runnable>();
		try {
			List<EdgeD2DNodeStatus> hostlst = new ArrayList<EdgeD2DNodeStatus>();
			IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
			hostDao.as_edge_enum_D2D_node(hostlst);
			
			List<EdgeHost> hosts = new LinkedList<EdgeHost>();
			for (EdgeD2DNodeStatus d2dnode : hostlst) {
				hosts.clear();
				hostDao.as_edge_host_list(d2dnode.getRhostid(), 1, hosts);
				
				if (hosts.size()>0) {
					if (ApplicationUtil.isD2DInstalled(hosts.get(0).getAppStatus())) {						
						retlst.add(new D2DQueryTask(d2dnode, hosts.get(0)));
					}
				}
			}
		} catch (Exception e) {
			log.debug("getD2DHostQueryTask:", e);
		}
		return retlst;
	}

	private List<Runnable> getVMHostQueryTask() {
		List<Runnable> retlst = new ArrayList<Runnable>();
		try {
			List<EdgeD2DNodeStatus> hostlst = new ArrayList<EdgeD2DNodeStatus>();
			IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
			hostDao.as_edge_enum_D2D_node(hostlst);
			Map<Integer, EdgeD2DNodeStatus> statusMap = new HashMap<Integer, EdgeD2DNodeStatus>();
			for (EdgeD2DNodeStatus status : hostlst) {
				statusMap.put(status.getRhostid(), status);
			}
			List<EdgeHost> hosts = new LinkedList<EdgeHost>();
			//Get all proxy nodes
			hostDao.as_edge_host_list_proxy(hosts);
			for (EdgeHost proxy : hosts) {
				//Get all VM nodes managed by this proxy
				int proxyId = proxy.getRhostid();
				EdgeD2DNodeStatus status = statusMap.get(proxyId);
				if (status != null) {
					List<EdgeHost> vmNodes = new LinkedList<EdgeHost>();
					hostDao.as_edge_host_list_node_under_proxy(proxyId,vmNodes);
					if (vmNodes.size() > 0) {
						for (EdgeHost vmNode : vmNodes) {							
							retlst.add(new VMQueryTask(proxy, vmNode, status));
						}
					}
				}				
			}
			
		} catch (Exception e) {
			log.debug("getD2DHostQueryTask4VM:", e);
		}
		return retlst;
	}
	
	private List<Runnable> getLinuxD2DServerQueryTask(){
		List<Runnable> retlst = new ArrayList<Runnable>();
		try{
			List<EdgeHost> hostlst = new ArrayList<EdgeHost>();
			IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
			hostDao.as_edge_host_list(NodeGroup.LinuxD2D, 1, hostlst);
			for(EdgeHost d2dServer : hostlst){
				retlst.add(new LinuxD2DQueryTask(d2dServer));
			}
		}catch (Exception e) {
			log.debug("getLinuxD2DServerQueryTask:", e);
		}
		return retlst;
	}
	
	public enum Status {
		NA, FIT, WARN, ERROR,WARNINGNOSETTING,WARNINGLOWFREEDISK,ERRORNOTACCESS,ERRORWEBSERVICE,ERROR_D2D_CANNOT_ACCESS_EDGE;
	}

	public static class ReloadRegistryJob implements Job {

		@Override
		public void execute(JobExecutionContext context)
				throws JobExecutionException {
			synchronized (ReloadRegistryJob.class) {
				int oldInterval = Util.intervalInSeconds;
				boolean oldDisable = Util.isDisabled;
				Util.getSettingsFromRegistry();
				if (isFirst || oldDisable != Util.isDisabled
						|| oldInterval != Util.intervalInSeconds) {
					scheduleQueryJob();
					isFirst = false;
				}
			}
		}
	}

	private static boolean isFirst = true;

	private static class Util {

		public static int intervalInSeconds = 60;
		public static boolean isDisabled = false;
		public static int concurNo = 10;

		public static void getSettingsFromRegistry() {

			try {
				try {
					String jobInterval = CommonUtil.getApplicationExtentionKey(WindowsRegistry.VALUE_NAME_D2D_SYNCJOB_INTERVAL);
					log.debug("jobInterval:" + jobInterval);
					if (jobInterval != null && jobInterval.trim().length() > 0) {
						intervalInSeconds = Integer.parseInt(jobInterval);
					}
					log.debug("intervalInSeconds:" + intervalInSeconds);
				} catch (Exception e) {
				}

				try {
					String disable = CommonUtil.getApplicationExtentionKey(WindowsRegistry.VALUE_NAME_D2D_SYNCJOB_DISABLE);
					log.debug("disable:" + disable);
					isDisabled = disable != null
							&& (disable.trim().equals("1") || disable.trim()
									.equalsIgnoreCase("true"));
					log.debug("isDisabled:" + isDisabled);
				} catch (Exception e) {
				}

				try {
					String concur = CommonUtil.getApplicationExtentionKey(WindowsRegistry.VALUE_NAME_D2D_SYNCJOB_CONCURRENT);
					log.debug("concur:" + concur);
					if (concur != null && concur.trim().length() > 0) {
						concurNo = Integer.parseInt(concur);
					}
					log.debug("concurNo:" + concurNo);
				} catch (Exception e) {
				}

			} catch (Exception e) {
				log.error("getSettingsFromRegistry:", e);
			}
		}
	}

	public static final String groupD2DQuery = "Group_D2D_Query";
	public static final String jobD2DQueryStatus = "Job_D2D_Query_Status";
	
	public static final long MILLISECONDS_IN_MINUTE = 60l * 1000l;
	
	public static SimpleTriggerImpl makeMinutelyTrigger(int intervalInMinutes) {
		return makeMinutelyTrigger(intervalInMinutes, -1);
	}

	public static SimpleTriggerImpl makeMinutelyTrigger(int intervalInMinutes, int repeatCount) {
		SimpleTriggerImpl trig = new SimpleTriggerImpl();
		trig.setRepeatInterval(intervalInMinutes * MILLISECONDS_IN_MINUTE);
		trig.setRepeatCount(repeatCount);
		trig.setStartTime(new Date());
		return trig;
	}
	
	public static void scheduleQueryJob() {
		try {
			Scheduler scheduler = SchedulerUtilsImpl.getScheduler();// StdSchedulerFactory.getDefaultScheduler();
			scheduler.deleteJob(new JobKey(jobD2DQueryStatus, groupD2DQuery));
			if (!Util.isDisabled) {
				JobDetailImpl jobDetail = new JobDetailImpl(jobD2DQueryStatus,
						groupD2DQuery, QueryD2DStatusJob.class);
				SimpleTriggerImpl trigger = makeMinutelyTrigger(15);
//						.makeSecondlyTrigger(Util.intervalInSeconds);
				trigger.setName(jobDetail.getName() + "-Trigger");
				scheduler.scheduleJob(jobDetail, trigger);
			}
		} catch (SchedulerException e) {
			log.debug("scheduleQueryJob:", e);
		}
	}

	public static final String jobRegistryMonitor = "Job_Registry_Monitor";

	public static void scheduleRegMonJob() {
		try {
			Scheduler scheduler = SchedulerUtilsImpl.getScheduler();// StdSchedulerFactory.getDefaultScheduler();
			scheduler.deleteJob(new JobKey(jobRegistryMonitor, groupD2DQuery));

			JobDetailImpl jobDetail = new JobDetailImpl(jobRegistryMonitor,
					groupD2DQuery, ReloadRegistryJob.class);
			SimpleTriggerImpl trigger = makeMinutelyTrigger(15);
			trigger.setName(jobDetail.getName() + "-Trigger");
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			log.error("scheduleRegMonJob:", e);

		}
	}

	// @Override
	// public void execute(JobExecutionContext context)
	// throws JobExecutionException {
	// if (!isRunning) {
	// isRunning = true;
	// final BlockingQueue<Future<?>> bq = new ArrayBlockingQueue<Future<?>>(
	// Util.concurNo);
	// ExecutorService jobs = Executors.newFixedThreadPool(Util.concurNo);
	// ExecutorService checkbq = Executors.newSingleThreadExecutor();
	// checkbq.submit(new FutureTask<Integer>(new Runnable() {
	// @Override
	// public void run() {
	// if (!bq.isEmpty()) {
	// for (Future<?> result : bq) {
	// if (result.isDone()) {
	// bq.remove(result);
	// }
	// }
	// }
	// }
	// }, 0));
	//
	// List<Runnable> list = getD2DHostQueryTask();
	// for (int i = 0; i < list.size(); i++) {
	// Future<?> f = jobs.submit(list.get(i));
	// try {
	// bq.put(f);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// jobs.shutdown();
	// checkbq.shutdown();
	//
	// isRunning = false;
	// }
	// }
}
