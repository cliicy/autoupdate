package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.EdgeASDataSyncSetting;
import com.ca.arcserve.edge.app.base.appdaos.EdgeArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSettingDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;
import com.ca.arcserve.edge.app.base.scheduler.ISchedulerID2DataMapper;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.schedulers.EdgeDBIDMapper;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

public class SyncArcserveIncJob implements IScheduleCallBack {
	private static SyncArcserveIncJob instance = null;
	@Override
	public ISchedulerID2DataMapper getID2DataMapper() {
		// TODO Auto-generated method stub
		return EdgeDBIDMapper.getInstance();
	}
	public static void init() {
		if (instance == null)
			instance = new SyncArcserveIncJob();
		try {
			IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
			IEdgeConnectInfoDao edao = DaoFactory
					.getDao(IEdgeConnectInfoDao.class);
			IEdgeSettingDao setDao = DaoFactory.getDao(IEdgeSettingDao.class);
			ArrayList<EdgeHost> hosts = new ArrayList<EdgeHost>();
			ArrayList<Integer> scheduleIDs = new ArrayList<Integer>();

			hostDao.as_edge_host_list(0, ConfigurationOperator._IsVisible,
					hosts);
			Iterator<EdgeHost> e = hosts.iterator();
			while (e.hasNext()) {
				EdgeHost item = e.next();

				ArrayList<EdgeArcserveConnectInfo> infos = new ArrayList<EdgeArcserveConnectInfo>();
				edao.as_edge_arcserve_connect_info_list(item.getRhostid(),
						infos);
				if (infos.isEmpty()
						|| infos.get(0).getType() == ABFuncServerType.BRANCH_PRIMARY.ordinal()
						|| infos.get(0).getType() == ABFuncServerType.ARCSERVE_MEMBER.ordinal())
					continue;

				ArrayList<EdgeASDataSyncSetting> Settings = new ArrayList<EdgeASDataSyncSetting>();
				setDao.as_edge_asdatasync_setting_get(item.getRhostid(),
						Settings);
				if (Settings.isEmpty())
					continue;
				if (scheduleIDs.contains(Settings.get(0).getScheduleID()))
					scheduleIDs.add(Settings.get(0).getScheduleID());
			}

			ArrayList<EdgeASDataSyncSetting> globalSettings = new ArrayList<EdgeASDataSyncSetting>();
			setDao.as_edge_asdatasync_setting_get(0, globalSettings);
			scheduleIDs.add(globalSettings.get(0).getScheduleID());

			SchedulerUtilsImpl.getInstance().registerIDs(instance, scheduleIDs);
		} catch (Exception e) {
			ConfigurationOperator.errorMessage(e.getMessage());
		}
	}

	public static IScheduleCallBack getInstance() {
		return instance;
	}

	@Override
	public int run(ScheduleData scheduleData, Object arg) {

		ConfigurationOperator
				.debugMessage("Incremental Schedule Job is launched");
		List<EdgeArcserveConnectInfo> infos = new ArrayList<EdgeArcserveConnectInfo>();
		 IEdgeConnectInfoDao edao = DaoFactory.getDao(IEdgeConnectInfoDao.class);

		 edao.as_edge_arcserve_connect_info_list_byschedid(scheduleData
				.getScheduleID(), infos);

		Iterator<EdgeArcserveConnectInfo> e = infos.iterator();

		SyncArcserveServiceMgr syncMgr = new SyncArcserveServiceMgr();
		int[] rhostIDs = new int[1];

		while(e.hasNext()) {
			EdgeArcserveConnectInfo item = e.next();
			if(item.getType() != ABFuncServerType.NORNAML_SERVER.ordinal() 
					&& item.getType() != ABFuncServerType.GDB_PRIMARY_SERVER.ordinal()
					&& item.getType() != ABFuncServerType.STANDALONE_SERVER.ordinal())
				continue;
			rhostIDs[0] = item.getHostid();
			try {
				syncMgr.InvokeSync(rhostIDs, false, false);
			} catch (EdgeServiceFault e2) {
				// TODO Auto-generated catch block
				ConfigurationOperator.errorMessage(
						"ARCserveSyncTask throw exception. Err: "
								+ e2.getMessage(), e2);
			}

		}
		return 1;
	}
}
