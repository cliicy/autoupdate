package com.ca.arcserve.edge.app.base.schedulers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeScheduler_Schedule;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSchedulerDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.scheduler.EdgeSchedulerException;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;
import com.ca.arcserve.edge.app.base.scheduler.ISchedulerID2DataMapper;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTask;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTaskFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;
import com.ca.arcserve.edge.app.base.webservice.srm.SrmCommand;
import com.ca.arcserve.edge.app.base.webservice.srm.SrmProbing;

public class SrmPkiMonitorJob implements IScheduleCallBack {
	public static final int SRM_JOB_EC_SUCCEED          = 1;
	public static final int SRM_JOB_EC_FAILED           = 0;
	public static final int SRM_JOB_EC_GET_GROUP_FAILED = -1;
	public static final int SRM_JOB_EC_GET_HOST_FAILED  = -2;
	
	public static final int SRM_PKI_MONITOR_JOB_ID      = -1;

	public static final int SRM_JOB_ACTION_TYPE_PKI_MONITOR  = 6;




	private static Logger _log = Logger.getLogger(SrmPkiMonitorJob.class);
	public static String data = "SRM PKI monitor Job";
	private static SrmPkiMonitorJob instance = null;
	@Override
	public ISchedulerID2DataMapper getID2DataMapper() {
		// TODO Auto-generated method stub
		return EdgeDBIDMapper.getInstance();
	}
	public static void init(){
		if(instance==null)
		instance  = new SrmPkiMonitorJob();
		try {
			ArrayList<Integer> scheduleIDs = new ArrayList<Integer>();

			// populate SRM  PKI monitor schedule ID
			List<EdgeScheduler_Schedule> schedules = new ArrayList<EdgeScheduler_Schedule>();
			IEdgeSchedulerDao edao = DaoFactory.getDao(IEdgeSchedulerDao.class);
			edao.as_edge_schedule_list(0, schedules);
			for (EdgeScheduler_Schedule schedule : schedules) {
				if (schedule.getActionType() == SRM_JOB_ACTION_TYPE_PKI_MONITOR) {
					scheduleIDs.add(schedule.getID());
					break;
				}
			}

			SchedulerUtilsImpl.getInstance().registerIDs(instance, scheduleIDs);
		} catch (EdgeSchedulerException e) {
			_log.debug("[SRM PKI monitor init Exception]" + e.getMessage());
		}
	}
	public static IScheduleCallBack getInstance(){
		return instance;
	}
	@Override
	public int run(ScheduleData scheduleData, Object arg) {
		data = "SRM PKI Monitor ";
		Date d = new Date(System.currentTimeMillis());
		data += d.toString();
		_log.debug("+++++++++++++" + data + "+++++++++++++++++++++++++");

		IEdgeHostMgrDao hostdao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		IEdgeConnectInfoDao conDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
		List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		hostdao.as_edge_host_list_for_srm(0, hosts);
		if (0 == hosts.size())
			return SRM_JOB_EC_GET_HOST_FAILED;

		for (EdgeHost h : hosts) {
			if(HostTypeUtil.isNodeImportFromRPSReplica(h.getRhostType())
				|| HostTypeUtil.isNodeImportFromRHA(h.getRhostType())){
				_log.debug("Remote node is skipped for SRMKpiMonitor. hostname=" + h.getRhostname());
				continue;
			}
			List<EdgeConnectInfo> infos = new ArrayList<EdgeConnectInfo>();
			conDao.as_edge_connect_info_list(h.getRhostid(), infos);

			for (EdgeConnectInfo ci : infos) {
				SrmProbing probe = new SrmProbing();
				probe.setHost(h.getRhostname());
				probe.setHostID(h.getRhostid());
				probe.setProtocol(Protocol.parse(ci.getProtocol()).toString()); 
				probe.setPort(ci.getPort());
				probe.setCommand(SrmCommand.GET_SERVERPKI_INFO);
				probe.setJobID(SRM_PKI_MONITOR_JOB_ID);
				EdgeTask srmPkiTask = EdgeTaskFactory.getInstance().getTask(
						EdgeTaskFactory.EDGE_TASK_SRM_PKI_MONITOR);
				if (srmPkiTask != null) {
					try {
						srmPkiTask.AddToWaitingQueue(probe);
						break;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						_log.error(e.getMessage(),e);
						return SRM_JOB_EC_FAILED;
					}
				}
			}
		}

		return SRM_JOB_EC_SUCCEED;
	}
}
