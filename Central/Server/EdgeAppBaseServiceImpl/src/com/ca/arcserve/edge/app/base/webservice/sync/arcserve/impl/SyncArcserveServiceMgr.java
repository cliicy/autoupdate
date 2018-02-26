package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeTaskIdDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.abintegration.ABFuncServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncManageStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.GDBServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeSortCol;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ASBURemoteNodeConfig;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.SyncARCServerType;

public class SyncArcserveServiceMgr {

	private CheckSyncProperties syncProperty = new CheckSyncProperties();
	private IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private SyncASBUActivityLog activityLog = null;
	private IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private ASBUJobInfo jobinfo = new ASBUJobInfo();
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private EdgeWebServiceImpl serviceImpl;
	private static Logger logger = Logger.getLogger( SyncArcserveServiceMgr.class );

	public SyncArcserveServiceMgr() {
		IEdgeTaskIdDao itask = DaoFactory.getDao(IEdgeTaskIdDao.class);
		long[] taskId = new long[1];
		itask.as_edge_get_next_taskid(taskId);
		jobinfo.setJobid(taskId[0]);

		activityLog = SyncASBUActivityLog.GetInstance(jobinfo);
	}
	/**
	 * Invoke the sync for ARCserve, it get a array of hostid if one node is
	 * invalid for incremental this function won't sync all the node.
	 * 
	 * @param rhostId
	 *            the hostid in as_edge_host table
	 * @param bIsFull
	 *            the sync type is full or incremental
	 * @param bIsAutoConvert
	 *            the flag for auto convert incremental to full
	 * @return
	 * @throws EdgeServiceFault
	 */
	public boolean InvokeSync(int[] rhostId, Boolean bIsFull,
			Boolean bIsAutoConvert) throws EdgeServiceFault {

		ArrayList<ASBUServerInfo> serverInfoList = new ArrayList<ASBUServerInfo>(
				rhostId.length);

		hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		// Check Host status for the host list
		String errMsg = "";
		for (int i = 0; i < rhostId.length; i++) {
			if (!isArcserveManaged(rhostId[i])) {
				continue;
			}
			ASBUServerInfo serverInfo = new ASBUServerInfo();
			serverInfo.setHostid(rhostId[i]);
			serverInfo.setType(nodeControl(rhostId[i], bIsFull));
			if (syncProperty.CheckStatus(rhostId[i])) {
				serverInfo.setValid(true);
			} else {
				serverInfo.setValid(false);
				if (!bIsFull && !bIsAutoConvert) {
					List<EdgeHost> hosts = new ArrayList<EdgeHost>();
					hostDao.as_edge_host_list(rhostId[i],
							ConfigurationOperator._IsVisible, hosts);
					activityLog.WriteWarning(hosts.isEmpty() ? "" : hosts.get(0)
							.getRhostname(), SyncActivityLogMsg
							.getSyncincsyncfullnotfinishedMsg());
				}
			}

			if (!serverInfo.isValid()) {
				if (StringUtil.isEmptyOrNull(errMsg))
					errMsg += rhostId[i];
				else
					errMsg += ", " + rhostId[i];
			}
			serverInfoList.add(serverInfo);
		}

		// If the sync is not full or auto convert throw exception to UI
		if (!bIsFull && !bIsAutoConvert) {
			if (!StringUtil.isEmptyOrNull(errMsg)) {
				EdgeServiceFaultBean faultBean = new EdgeServiceFaultBean(
						EdgeServiceErrorCode.ARCserve_Sync_FullSyncNotFinished,
						errMsg);
				EdgeServiceFault fault = new EdgeServiceFault(errMsg, faultBean);
				throw fault;
			}
		}

		Boolean syncType = bIsFull;
		Iterator<ASBUServerInfo> iter = serverInfoList.iterator();
		while (iter.hasNext()) {
			ASBUServerInfo serverInfo = iter.next();
			if (serverInfo.getHostid() == 0
					|| serverInfo.getType() == SyncARCServerType.SERVER_UNKNOW) {
				ConfigurationOperator
						.debugMessage("sync check invalid. hostid:"
								+ serverInfo.getHostid());
				continue;
			}

			syncType = bIsFull;
			// if the sync is incremental and the server is invalid, auto
			// convert to full
			if (!bIsFull && !serverInfo.isValid()) {
				syncType = true;
			}
			try {
				SyncArcserveServiceImpl.InvokeARCserveSync(jobinfo, serverInfo
						.getHostid(), serverInfo.getType(), syncType);
			} catch (EdgeServiceFault e) {
				ConfigurationOperator
						.debugMessage("Incremental sync check error. hostid:"
								+ serverInfo.getHostid() + " Msg:"
								+ e.getMessage());
			}
		}

		return true;
	}

	/**
	 * @param hostname
	 *            is arcserve server name
	 * @return true is managed, false is unmanaged.
	 */
	public boolean checkNodeManageStatus(String guid) {
		IEdgeConnectInfoDao idao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
		List<EdgeArcserveConnectInfo> infos = new ArrayList<EdgeArcserveConnectInfo>();
		idao.as_edge_arcserve_connect_info_list_get_by_guid(guid, infos);

		if (!infos.isEmpty()
				&& infos.get(0).getManaged() == NodeManagedStatus.Managed
						.ordinal())
			return true;

		return false;
	}

	public SyncARCServerType nodeControl(int rhostid, Boolean bIsFull) {
		if (bIsFull)
			return fullSyncControl(rhostid);
		else
			return incrementalSyncControl(rhostid);
	}

	private SyncARCServerType fullSyncControl(int rhostid) {
		ASBURemoteNodeConfig nodeConfig = new ASBURemoteNodeConfig(rhostid,jobinfo);
		SyncARCServerType type = getServerType(nodeConfig.getNodeInfo());
		nodeConfig.updateStatus();
		return type;
	}

	private SyncARCServerType incrementalSyncControl(int rhostid) {
		ASBURemoteNodeConfig nodeConfig = new ASBURemoteNodeConfig(rhostid,jobinfo);
		SyncARCServerType type = getServerType(nodeConfig.getNodeInfo());
		if (nodeConfig.getbIsStatusChanged())
			nodeConfig.updateStatus();
		return type;
	}

	public SyncARCServerType getServerType(RemoteNodeInfo node) {

		if (node == null)
			return SyncARCServerType.SERVER_UNKNOW;

		if (node.getARCserveType() == ABFuncServerType.ARCSERVE_MEMBER) {
			return SyncARCServerType.REGULAR_MEMBER;
		} else if (node.getARCserveType() == ABFuncServerType.STANDALONE_SERVER) {
			return SyncARCServerType.REGULAR_STAND_ALONE;
		} else if (node.getARCserveType() == ABFuncServerType.GDB_PRIMARY_SERVER) {
			if (node.getGdbType() == GDBServerType.GDB_UNKNOW)
				return SyncARCServerType.SERVER_UNKNOW;
			else if (node.getGdbType() == GDBServerType.GDB_IN_PRIMARY)
				return SyncARCServerType.GDB_SELF_PRIMARY;
			else if (node.getGdbType() == GDBServerType.GDB_IN_BRANCH)
				return SyncARCServerType.GDB_SELF_BRANCH;
			else
				return SyncARCServerType.GDB_SELF_GDB;

		} else if (node.getARCserveType() == ABFuncServerType.BRANCH_PRIMARY) {
			return SyncARCServerType.REGULAR_BRANCH;
		} else if (node.getARCserveType() == ABFuncServerType.NORNAML_SERVER) {
			return SyncARCServerType.REGULAR_PRIMARY;
		}

		return SyncARCServerType.SERVER_UNKNOW;
	}

	public Boolean isArcserveManaged(int rhostid) {
		List<EdgeArcserveConnectInfo> arcInfos = new ArrayList<EdgeArcserveConnectInfo>();
		List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		hostDao.as_edge_host_list(rhostid, ConfigurationOperator._IsVisible,
				hosts);
		connectionInfoDao.as_edge_arcserve_connect_info_list(rhostid, arcInfos);

		
		if (arcInfos.isEmpty() || hosts.isEmpty())
			return false;

		EdgeHost curHost = hosts.get(0);
		EdgeArcserveConnectInfo connectInfo = arcInfos.get(0);
		logger.info("IsARcserveManaged. connectInfo port =" + connectInfo.getPort());
		
		boolean isMgred = true;

		try {
			// check whether it can be marked as managed
			String uuid = CommonUtil.retrieveCurrentAppUUID();
			synchronized (ABFuncServiceImpl.class) {
				ABFuncServiceImpl abImpl = new ABFuncServiceImpl(curHost
						.getRhostname(), connectInfo.getPort());
				GatewayEntity gateway = gatewayService.getGatewayByHostId(curHost.getRhostid());
				String strSessionNo = abImpl.ConnectARCserve(gateway, connectInfo
						.getCauser(), connectInfo.getCapasswd(), ABFuncAuthMode
						.values()[connectInfo.getAuthmode()]);
				ABFuncManageStatus mgrSts = abImpl.GetArcserveManageStatus(
						strSessionNo, uuid);
				if (mgrSts != ABFuncManageStatus.MANAGED) {
					isMgred = false;
					try {
						connectionInfoDao
								.as_edge_arcserve_connect_update_managedStatus(
										curHost.getRhostid(),
										ABFuncManageStatus.UN_MANAGED.ordinal(),
										null);
					} catch (Exception e) {
						ConfigurationOperator.errorMessage(e.getMessage());
					}
				}

				if (mgrSts == ABFuncManageStatus.MANAGED_BY_OTHER_SERVER) {
					activityLog
							.WriteWarning(
									curHost.getRhostname(),
									SyncActivityLogMsg
											.getSyncasbuservernotmanagedbycurrentserver());
				}
			}

		} catch (EdgeServiceFault e) {
			isMgred = false;
			activityLog.WriteError(curHost.getRhostname(), String.format(
					SyncActivityLogMsg.getSyncasbuabfuncserviceisinvalidate(),
					curHost.getRhostname()));
			ConfigurationOperator
					.errorMessage(
							"Connect ARCserve sevices throw Exception, Msg: " + e.getMessage(),
							e);
		}

		return isMgred;

	}
	
	public void submitARCserveFullSyncForGroup(int groupID, int groupType) throws EdgeServiceFault{
		InvokeSync(getNodeList(groupID, groupType), true, false );
	}
	
	public void submitARCserveIncrementalSyncForGroup(int groupID, int groupType) throws EdgeServiceFault{
		InvokeSync(getNodeList(groupID, groupType), false, false );
	}
	
	public int[] getNodeList(int groupID, int groupType){
		EdgeNodeFilter nodeFilter = new EdgeNodeFilter();
		NodePagingConfig pagingConfig = new NodePagingConfig();
		
		List<Node> nodeList = null;
		pagingConfig.setOrderCol(NodeSortCol.hostname);
		pagingConfig.setOrderType(EdgeSortOrder.ASC);
		pagingConfig.setPagesize(Integer.MAX_VALUE);
		pagingConfig.setStartpos(0);
		
		try {
			NodePagingResult result = serviceImpl.getNodesESXByGroupAndTypePaging(groupID, groupType, nodeFilter, pagingConfig);
			nodeList = result.getData();
		} catch (Exception e) {
			return null;
		}
		
		List<Integer> nodeIDs = new LinkedList<Integer>();
		for (Node node:nodeList){
			if (node.isArcserveInstalled() && node.getArcserveManaged() == NodeManagedStatus.Managed)
				nodeIDs.add(node.getId());
		}
		
		return CommonUtil.convertIntegerList2Array(nodeIDs);
	}
	public void setServiceImpl(EdgeWebServiceImpl serviceImpl) {
		this.serviceImpl = serviceImpl;
	}
	
}
