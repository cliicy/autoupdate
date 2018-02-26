package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.EdgeASDataSyncSetting;
import com.ca.arcserve.edge.app.base.appdaos.EdgeArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSettingDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;

public class GetASBUServerInfo {

	public synchronized static IASBUServerInfo GetServiceInfo(Integer rhostid) {

		ASBUServerInfo serverInfo = new ASBUServerInfo();

		IEdgeConnectInfoDao conDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
		List<EdgeArcserveConnectInfo> arcInfoList = new ArrayList<EdgeArcserveConnectInfo>();
		conDao.as_edge_arcserve_connect_info_list(rhostid, arcInfoList);
		IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		if (arcInfoList.isEmpty()) {
			return null;
		}

		List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		List<EdgeASDataSyncSetting> Settings = new ArrayList<EdgeASDataSyncSetting>();

		hostDao
				.as_edge_host_list(rhostid, ConfigurationOperator._IsVisible,
						hosts);
		IEdgeSettingDao setDao = DaoFactory.getDao(IEdgeSettingDao.class);
		setDao.as_edge_asdatasync_setting_get(rhostid, Settings);
		if (Settings.isEmpty())
			setDao.as_edge_asdatasync_setting_get(0, Settings);

		serverInfo.set_branchID(rhostid);
		serverInfo.set_serverName(hosts.get(0).getRhostname());
		serverInfo.set_URL(GetUrlByRhostID(rhostid));
		serverInfo.set_serverType(arcInfoList.get(0).getType());
		if(Settings.isEmpty()){
			serverInfo.set_syncPath(ConfigurationOperator._DefaultSyncPath);
			serverInfo.set_retryTimes(ConfigurationOperator._defaultRetryTimes);
			serverInfo.set_retryInterval(ConfigurationOperator._defaultRetryInterval);
		}
		else{
			serverInfo.set_syncPath(Settings.get(0).getSyncFilepath());
			serverInfo.set_retryTimes(Settings.get(0).getRetryTimes());
			serverInfo.set_retryInterval(Settings.get(0).getRetryInterval());
		}
		return serverInfo;

	}

	private static String GetUrlByRhostID(int rhostID) {
		List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		List<EdgeArcserveConnectInfo> infos = new ArrayList<EdgeArcserveConnectInfo>();
		List<EdgeASDataSyncSetting> Settings = new ArrayList<EdgeASDataSyncSetting>();

		IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		IEdgeConnectInfoDao conDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
		IEdgeSettingDao setDao = DaoFactory.getDao(IEdgeSettingDao.class);
		hostDao
				.as_edge_host_list(rhostID, ConfigurationOperator._IsVisible,
						hosts);
		conDao.as_edge_arcserve_connect_info_list(rhostID, infos);
		if (hosts.isEmpty() || infos.isEmpty()) {
			ConfigurationOperator.errorMessage("Hosts of infos is empty");
			return null;
		}

		setDao.as_edge_asdatasync_setting_get(rhostID, Settings);
		if (Settings.isEmpty())
			setDao.as_edge_asdatasync_setting_get(0, Settings);

		String strUrl = ConfigurationOperator.GetServiceConnectString(hosts
				.get(0).getRhostname(), infos.get(0).getPort());

		return strUrl;
	}

}
