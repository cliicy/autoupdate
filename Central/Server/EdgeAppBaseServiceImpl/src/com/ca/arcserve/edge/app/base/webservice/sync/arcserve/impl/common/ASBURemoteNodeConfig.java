package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.EdgeArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeD2DSyncDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.ChangeStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.EdgeSyncComponents;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.ASBUJobInfo;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.SyncASBUActivityLog;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.SyncActivityLogMsg;

public class ASBURemoteNodeConfig {
	private RemoteServerInfo serverInfo = RemoteServerInfo.getInstance();
	private RemoteNodeInfo nodeInfo = null;
	private Boolean bIsStatusChanged = false;
	private IEdgeD2DSyncDao iDao = DaoFactory.getDao(IEdgeD2DSyncDao.class);
	private IEdgeConnectInfoDao conDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private EdgeHost curHost = null;
	private EdgeArcserveConnectInfo curInfo = null;
	private ASBUJobInfo jobinfo = null;

	public boolean IsNodeValidate(){
		if(nodeInfo == null)
			return false;
		return true;
	}

	public RemoteNodeInfo getNodeInfo() {
		return nodeInfo;
	}

	public void setNodeInfo(RemoteNodeInfo nodeInfo) {
		this.nodeInfo = nodeInfo;
	}

	public Boolean getbIsStatusChanged() {
		return bIsStatusChanged;
	}

	public void setbIsStatusChanged(Boolean bIsStatusChanged) {
		this.bIsStatusChanged = bIsStatusChanged;
	}

	public void updateStatus() {
		if (curHost == null || curInfo == null || nodeInfo == null)
			return;

		if (bIsStatusChanged) {
			ABFuncServerType abType = nodeInfo.getARCserveType();
			if (abType == ABFuncServerType.ARCSERVE_MEMBER)
				iDao.as_edge_update_change_status(
						EdgeSyncComponents.ARCserve_Backup.getValue(), curInfo
								.getHostid(), ChangeStatus.BLOCKSYNC
								.getValue());
			else
				iDao.as_edge_update_change_status(
						EdgeSyncComponents.ARCserve_Backup.getValue(), curInfo
								.getHostid(), ChangeStatus.REFULLSYNC
								.getValue());
			SyncASBUActivityLog activityLog = SyncASBUActivityLog.GetInstance(jobinfo);
			
			activityLog.WriteWarning(curHost.getRhostname(), SyncActivityLogMsg
					.getSyncasbuservertypechanged(curHost.getRhostname(),
							ABFuncServerType.values()[curInfo.getType()],
							abType));
			
			DaoUpdateExtImpl.as_edge_arcserve_connect_info_update_type(curInfo,
					abType.ordinal());
			
		}
	}

	public void clearStatus() {

		if(bIsStatusChanged)
			DaoUpdateExtImpl.as_edge_arcserve_connect_info_update_type(curInfo,
					nodeInfo.getARCserveType().ordinal());

		iDao.as_edge_update_change_status(
				EdgeSyncComponents.ARCserve_Backup.getValue(), curInfo
						.getHostid(), ChangeStatus.NORMAL.getValue());
	}

	public ASBURemoteNodeConfig(int rhostid, ASBUJobInfo jobinfo) {
		this.jobinfo = jobinfo;
		List<EdgeConnectInfo> infos = new ArrayList<EdgeConnectInfo>();
		List<EdgeArcserveConnectInfo> arcInfos = new ArrayList<EdgeArcserveConnectInfo>();
		List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		hostDao
				.as_edge_host_list(rhostid, ConfigurationOperator._IsVisible,
						hosts);
		conDao.as_edge_connect_info_list(rhostid, infos);
		conDao.as_edge_arcserve_connect_info_list(rhostid, arcInfos);

		if (infos.isEmpty() || hosts.isEmpty())
			return;

		curHost = hosts.get(0);
		curInfo = arcInfos.get(0);
		EdgeConnectInfo connectInfo = infos.get(0);

		nodeInfo = serverInfo.getNodeInfo(curHost, curInfo, connectInfo);
		if (nodeInfo != null) {
			ABFuncServerType abType = nodeInfo.getARCserveType();

			// Server Type is changed
			if (curInfo.getType() != abType.ordinal()) {
				bIsStatusChanged = true;
			}
		}
	}
}
