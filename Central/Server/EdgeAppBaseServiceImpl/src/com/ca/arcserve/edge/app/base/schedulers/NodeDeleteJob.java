package com.ca.arcserve.edge.app.base.schedulers;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeNodeDeleteProbingSetting;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSettingDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.scheduler.EdgeSchedulerException;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;
import com.ca.arcserve.edge.app.base.scheduler.ISchedulerID2DataMapper;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTask;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTaskFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.abintegration.ABFuncServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncManageStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.srm.NodeDeleteProbing;


public class NodeDeleteJob implements IScheduleCallBack {

	public static final int NodeDelete_JOB_EC_SUCCEED          = 1;
	public static final int NodeDelete_JOB_EC_FAILED           = 0;
	public static final int NodeDelete_JOB_EC_GET_GROUP_FAILED = -1;
	public static final int NodeDelete_JOB_EC_GET_HOST_FAILED  = -2;
	public static final int NodeDelete_JOB_ACTION_TYPE_WEEKLY_EVENT  = 4;
	public static final int NodeDelete_JOB_ACTION_TYPE_MONTHLY_EVENT = 5;
	private static Logger _log = Logger.getLogger(NodeDeleteJob.class);
	public static String data = "NodeDelete Job";
	private static NodeDeleteJob instance = null;
	IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IActivityLogService _iSyncActivityLog = new ActivityLogServiceImpl();
	private ActivityLog         activity_log              = new ActivityLog();
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);

	@Override
	public ISchedulerID2DataMapper getID2DataMapper() {
		return EdgeDBIDMapper.getInstance();
	}

	public static void init(){
		if(instance==null)
			instance  = new NodeDeleteJob();
		try {
			ArrayList<Integer> scheduleIDs = new ArrayList<Integer>();
			// populate NodeDelete probe schedule ID
			List<EdgeNodeDeleteProbingSetting> settings = new ArrayList<EdgeNodeDeleteProbingSetting>();
			IEdgeSettingDao edao = DaoFactory.getDao(IEdgeSettingDao.class);
			edao.as_edge_nodedeleteprobing_setting_get(settings);
			if (settings != null && settings.size() > 0) {
				scheduleIDs.add(settings.get(0).getScheduleID());
				SchedulerUtilsImpl.getInstance().registerIDs(instance, scheduleIDs);
			}
		} catch (EdgeSchedulerException e) {
			_log.debug("[NodeDelete probe init Exception]" + e.getMessage());
		} catch (Exception e) {
			_log.debug("[NodeDelete probe init Exception]" + e.getMessage());
		}
	}

	public static IScheduleCallBack getInstance(){
		return instance;
	}

	private List<Integer> deletedNodesIDList = null;


	public List<Integer> getDeletedNodesIDList() {
		return deletedNodesIDList;
	}

	public void setDeletedNodesIDList(List<Integer> deletedNodesIDList) {
		this.deletedNodesIDList = deletedNodesIDList;
	}

	@Override
	public int run(ScheduleData scheduleData, Object arg) {
		data = scheduleData.getScheduleName();
		Date d = new Date(System.currentTimeMillis());
		data += d.toString();
		_log.debug("+++++++++++++" + data + "+++++++++++++++++++++++++");

		IEdgeHostMgrDao edao = DaoFactory.getDao(IEdgeHostMgrDao.class);

		List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		edao.as_edge_deletedhost_list(hosts);
		if (0 == hosts.size())
			return NodeDelete_JOB_EC_GET_HOST_FAILED;

		EdgeTask nodeDeleteTask = EdgeTaskFactory.getInstance().getTask(EdgeTaskFactory.EDGE_TASK_NodeDelete);
		if (nodeDeleteTask != null) {
			// there no probe job is running, add a start status job.
			boolean newCreateNodeDeleteJobFlag = false;
			if (nodeDeleteTask.getWaitingQueueSize() == 0 && nodeDeleteTask.getExecuteQueueSize() == 0) {
				// Add the activity log in here
				activity_log.setModule(Module.Common);
				activity_log.setSeverity(Severity.Information);
				activity_log.setTime(new Date(System.currentTimeMillis()));
				activity_log.setMessage(EdgeCMWebServiceMessages.getResource("nodeDeleteJobStart"));
				try {
					_iSyncActivityLog.addLog(activity_log);
				} catch (EdgeServiceFault e) {
					_log.error(e.getMessage(), e);
				}
				newCreateNodeDeleteJobFlag = true;
			}
			_log.debug("deletedNodesIDList==="+deletedNodesIDList);
			for (EdgeHost h : hosts) {
				// if the user request probe all nodes or the node is request to
				// be probed.
				_log.debug("all deleted nodes contain NodeID==="+h.getRhostid()+"; NodeName==="+h.getRhostname());
				/*
				 * Fix issue 20311355    
				 * Remove management status check from node delete 
				 * Original design: when unmanaged ARCserve or D2D failed don't delete data from Edge
				 * Current design: Even unmanaged failed still clean data from Edge, because When ARcserve and D2D
				 * can connect edge, they can unmanaged their self.
				 */
				//boolean markD2DUnmanagedSuccess = true;
				//boolean markArcserveBackupUnmanagedSuccess = true;
				try {
					if (h.getArcserveManagedStatus() == NodeManagedStatus.Managed.ordinal()){
						String uuid = CommonUtil.retrieveCurrentAppUUID();

						List<EdgeArcserveConnectInfo> output = new LinkedList<EdgeArcserveConnectInfo>();
						connectionInfoDao.as_edge_arcserve_connect_info_list(h.getRhostid(), output);

						if (output !=null && output.size()>0){
							String sessionNo = "";
							ABFuncServiceImpl funcimpl = null;
							synchronized(ABFuncServiceImpl.class)
							{
								GatewayEntity gateway = gatewayService.getGatewayByHostId(h.getRhostid()) ;
								funcimpl = new ABFuncServiceImpl(h.getRhostname(), output.get(0).getPort());
								sessionNo = funcimpl.ConnectARCserve(gateway, output.get(0).getCauser(), output.get(0).getCapasswd(), ABFuncAuthMode.values()[output.get(0).getAuthmode()]);
							}
							
							funcimpl.MarkArcserveManageStatus(sessionNo, uuid, false, ABFuncManageStatus.UN_MANAGED);
						}
					}
				/*} catch (EdgeServiceFault e){
					if (EdgeServiceErrorCode.ABFunc_HaveManagedByAnotherServer.equals(e.getFaultInfo().getCode()))
						markArcserveBackupUnmanagedSuccess = true;
					else
						markArcserveBackupUnmanagedSuccess = false;*/
				}catch (Exception e1) {
					_log.error("$Thread.run() - exception ignored", e1); //$NON-NLS-1$
					//markArcserveBackupUnmanagedSuccess = false;
				}

				try {
					if (h.getD2dManagedStatus() == NodeManagedStatus.Managed.ordinal()){
						EdgeWebServiceImpl serviceImpl = new EdgeWebServiceImpl();
						serviceImpl.RemoveRegInfoFromD2DForDeleteNodeDataJob(h.getRhostid(), true);
					}
				/*} catch (EdgeServiceFault e){
					if (EdgeServiceErrorCode.Node_D2D_UnReg_Not_Owner.equals(e.getFaultInfo().getCode())
						||EdgeServiceErrorCode.Node_D2D_UnReg_Not_Exist.equals(e.getFaultInfo().getCode()))
						markD2DUnmanagedSuccess = true;
					else
						markD2DUnmanagedSuccess = false;*/
				} catch (Exception e) {
					_log.error("$Thread.run() - exception ignored", e); //$NON-NLS-1$
					//markD2DUnmanagedSuccess = false;
				}

				/*if(markD2DUnmanagedSuccess && markArcserveBackupUnmanagedSuccess)
				{*/
					if (deletedNodesIDList == null
							|| deletedNodesIDList.contains(h.getRhostid())) {
						NodeDeleteProbing nodeDelete = new NodeDeleteProbing();
						nodeDelete.setHost(h.getRhostname());
						nodeDelete.setHostID(h.getRhostid());
						try {
							if (!nodeDeleteTask.IsExistedInWaitingQueue(nodeDelete)) {
								nodeDeleteTask.AddToWaitingQueue(nodeDelete);
							}
						} catch (InterruptedException e) {
							_log.error(e.getMessage(), e);
							return NodeDelete_JOB_EC_FAILED;
						}
						EdgeTaskStatusItem endStatusItem = new EdgeTaskStatusItem();
						endStatusItem.setStatus(EdgeTaskStatus.Task_Canncel);
						endStatusItem.setDescription(String.format(EdgeCMWebServiceMessages.getResource("nodeDeleteJobDeleteSuccessful"), h.getRhostname()));
						endStatusItem.setTaskName(EdgeTaskFactory.EDGE_TASK_NodeDelete);
						try {
							nodeDeleteTask.AddToWaitingQueue(endStatusItem);
						} catch (InterruptedException e1) {
							_log.error(e1.getMessage(), e1);
						}
					}
				/*}else
				{
					String resourceID = "EDGE_SYNC_ALERT_PRODUCT_NAME_ASBU";
					if(!markD2DUnmanagedSuccess)
						resourceID = "EDGE_SYNC_ALERT_PRODUCT_NAME_D2D";
					
					EdgeTaskStatusItem endStatusItem = new EdgeTaskStatusItem();
					endStatusItem.setStatus(EdgeTaskStatus.Task_Error);
					endStatusItem.setDescription(String.format(
							EdgeCMWebServiceMessages
									.getResource("nodeDeleteJobDeleteFail"), h
									.getRhostname(), EdgeCMWebServiceMessages
									.getResource(resourceID)));
					endStatusItem.setTaskName(EdgeTaskFactory.EDGE_TASK_NodeDelete);
					try {
						nodeDeleteTask.AddToWaitingQueue(endStatusItem);
					} catch (InterruptedException e1) {
						_log.error(e1.getMessage(), e1);
					}
				}*/


			}

			if (newCreateNodeDeleteJobFlag) {
				EdgeTaskStatusItem endStatusItem = new EdgeTaskStatusItem();
				endStatusItem.setStatus(EdgeTaskStatus.Task_Finish);
				endStatusItem.setDescription(EdgeCMWebServiceMessages.getResource("nodeDeleteJobEnd"));
				endStatusItem.setTaskName(EdgeTaskFactory.EDGE_TASK_NodeDelete);
				try {
					nodeDeleteTask.AddToWaitingQueue(endStatusItem);
				} catch (InterruptedException e1) {
					_log.error(e1.getMessage(), e1);
				}
			}
		}
		return NodeDelete_JOB_EC_SUCCEED;
	}
}
