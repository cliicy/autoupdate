package com.ca.arcserve.edge.app.base.schedulers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeSRMProbingSetting;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSettingDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSrmDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeTaskIdDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.scheduler.EdgeSchedulerException;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;
import com.ca.arcserve.edge.app.base.scheduler.ISchedulerID2DataMapper;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTask;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTaskFactory;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;
import com.ca.arcserve.edge.app.base.webservice.srm.SrmCommand;
import com.ca.arcserve.edge.app.base.webservice.srm.SrmProbing;

public class SrmJob implements IScheduleCallBack {
	public static final int SRM_JOB_EC_SUCCEED          = 1;
	public static final int SRM_JOB_EC_FAILED           = 0;
	public static final int SRM_JOB_EC_GET_GROUP_FAILED = -1;
	public static final int SRM_JOB_EC_GET_HOST_FAILED  = -2;

	public static final int SRM_JOB_ACTION_TYPE_WEEKLY_EVENT  = 4;
	public static final int SRM_JOB_ACTION_TYPE_MONTHLY_EVENT = 5;

	private static Logger _log = Logger.getLogger(SrmJob.class);
	public static String data = "SRM Job";
	private static SrmJob instance = null;
	private static Integer timeZoneOffset = null;

	@Override
	public ISchedulerID2DataMapper getID2DataMapper() {
		// TODO Auto-generated method stub
		return EdgeDBIDMapper.getInstance();
	}
	public static void init(){
		if(instance==null)
		instance  = new SrmJob();
		try {
			ArrayList<Integer> scheduleIDs = new ArrayList<Integer>();

			// populate SRM probe schedule ID
			List<EdgeSRMProbingSetting> settings = new ArrayList<EdgeSRMProbingSetting>();
			IEdgeSettingDao edao = DaoFactory.getDao(IEdgeSettingDao.class);
			edao.as_edge_srmprobing_setting_get(settings);
			if (settings != null && settings.size() > 0) {
				scheduleIDs.add(settings.get(0).getScheduleID());
				SchedulerUtilsImpl.getInstance().registerIDs(instance, scheduleIDs);
			}
		} catch (EdgeSchedulerException e) {
			_log.debug("[SRM probe init Exception]" + e.getMessage());
		} catch (Exception e) {
			_log.debug("[SRM probe init Exception]" + e.getMessage());
		}
	}

	public static IScheduleCallBack getInstance(){
		return instance;
	}

	private List<Integer> probeNodesIDList = null;

	public List<Integer> getProbeNodesIDList() {
		return probeNodesIDList;
	}

	public void setProbeNodesIDList(List<Integer> probeNodesIDList) {
		this.probeNodesIDList = probeNodesIDList;
	}
	

	////move this code from EdgeTaskStatusItem( Task_start to here ); it need run every time synchronized no matter srmprobe is running;
	///it just connect db, assume it don't have performance problem,
	private void collectDataForBackupSizeReport(){
		try {
			
			_log.info("start collect data for backup size trend report ");
			 //fix issue 23739; comment out it, it will delete [as_edge_srm_node2host] table when srm fail.
			// now we not clear why the initial version add this statement. if any regression issue appear, please contract fanda03
			//edao.spsrmedgecleanupNode2Host(); 
			IEdgeSrmDao edao = DaoFactory.getDao(IEdgeSrmDao.class);
			//server time zone change! purge duplicated record; this operation may cause record in old day loss!; but can avoid issue caused by dup record in one day.
			Integer offSetNow = TimeZone.getDefault().getRawOffset(); 
			if( timeZoneOffset == null || !timeZoneOffset.equals(offSetNow)  ) {
				timeZoneOffset = offSetNow;
				 edao.backupSizeMergeForTimeZoneChange(timeZoneOffset);
			}
			edao.spsrmedgeCollectARCserveBackupSize( timeZoneOffset );
			edao.spsrmedgeCollectD2DBackupSize( timeZoneOffset );
			_log.info("sucessfully collect data for backup size trend report ");
		}
		catch( Throwable e ) {
			_log.error("failed collect data for backup size trend report!! " , e);
		}
		
	}
	@Override
	public int run(ScheduleData scheduleData, Object arg) {
		data = "SRM probing ";
		Date d = new Date(System.currentTimeMillis());
		data += d.toString();
		_log.debug("+++++++++++++" + data + "+++++++++++++++++++++++++");

		collectDataForBackupSizeReport();
		
		IEdgeSettingDao edao = DaoFactory.getDao(IEdgeSettingDao.class);
		IEdgeHostMgrDao hostdao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		IEdgeConnectInfoDao conDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
		List<EdgeSRMProbingSetting> settings = new ArrayList<EdgeSRMProbingSetting>();
		edao.as_edge_srmprobing_setting_get(settings);
		if (settings.size() == 0) {
			_log.debug("No SRM probe setting populated from data base.");
		}

		List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		hostdao.as_edge_host_list_for_srm(0, hosts);
		if (0 == hosts.size())
			return SRM_JOB_EC_GET_HOST_FAILED;

		EdgeTask srmTask = EdgeTaskFactory.getInstance().getTask(
				EdgeTaskFactory.EDGE_TASK_SRM);
		if (srmTask != null) {

			long[] jobids = new long[1];
			IEdgeTaskIdDao taskIdDao = DaoFactory.getDao(IEdgeTaskIdDao.class);
			taskIdDao.as_edge_get_next_taskid(jobids);
			
			// there no probe job is running, add a start status job.
			boolean newCreateSrmJobFlag = false;
			if (srmTask.getWaitingQueueSize() == 0 && srmTask.getExecuteQueueSize() == 0) {
				EdgeTaskStatusItem startStatusItem = new EdgeTaskStatusItem();
				startStatusItem.setStatus(EdgeTaskStatus.Task_Start);
				startStatusItem.setDescription(EdgeCMWebServiceMessages.getResource("SRM_PROBE_STATUS_START"));
				startStatusItem.setTaskName(EdgeTaskFactory.EDGE_TASK_SRM);
				startStatusItem.setJobID(jobids[0]);
				try {
					srmTask.AddToWaitingQueue(startStatusItem);
					newCreateSrmJobFlag = true;
				} catch (InterruptedException e1) {
					_log.error(e1.getMessage(), e1);
				}
			}

			for (EdgeHost h : hosts) {
				if(HostTypeUtil.isNodeImportFromRPSReplica(h.getRhostType())
					|| HostTypeUtil.isNodeImportFromRHA(h.getRhostType())){
					_log.debug("Remote node is skipped for SRM. hostname=" + h.getRhostname());
					continue;
				}

				// if the user request probe all nodes or the node is request to
				// be probed.
				if (probeNodesIDList == null
						|| probeNodesIDList.contains(h.getRhostid())) {
					List<EdgeConnectInfo> infos = new ArrayList<EdgeConnectInfo>();
					conDao.as_edge_connect_info_list(h.getRhostid(), infos);

					for (EdgeConnectInfo ci : infos) {
						SrmProbing probe = new SrmProbing();
						probe.setHost(h.getRhostname());
						probe.setHostID(h.getRhostid());
						probe.setProtocol(Protocol.parse(ci.getProtocol()).toString()); // !! need change to
						probe.setPort(ci.getPort());
						probe.setCommand(SrmCommand.GET_HARDWARE_INFO
								| SrmCommand.GET_SOFTWARE_INFO);
						probe.setJobID(jobids[0]);

						if (settings.size() != 0) {
							probe.setRetryTimes(settings.get(0)
									.getRetryTimes());
							probe.setRetryInterval(settings.get(0)
									.getRetryInterval());
						}

						try {
							if (!srmTask.IsExistedInWaitingQueue(probe)) {
								srmTask.AddToWaitingQueue(probe);
							}
							break;
						} catch (InterruptedException e) {
							_log.error(e.getMessage(),e);
							return SRM_JOB_EC_FAILED;
						}
					}
				}
			}

			if (newCreateSrmJobFlag) {
				EdgeTaskStatusItem endStatusItem = new EdgeTaskStatusItem();
				endStatusItem.setStatus(EdgeTaskStatus.Task_Finish);
				endStatusItem.setDescription(EdgeCMWebServiceMessages.getResource("SRM_PROBE_STATUS_FINISH"));
				endStatusItem.setTaskName(EdgeTaskFactory.EDGE_TASK_SRM);
				endStatusItem.setJobID(jobids[0]);
				try {
					srmTask.AddToWaitingQueue(endStatusItem);
				} catch (InterruptedException e1) {
					_log.error(e1.getMessage(), e1);
				}
			}
		}


		return SRM_JOB_EC_SUCCEED;
	}
}
