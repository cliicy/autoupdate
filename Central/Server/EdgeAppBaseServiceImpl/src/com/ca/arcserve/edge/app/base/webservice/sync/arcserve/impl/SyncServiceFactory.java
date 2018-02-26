package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.util.ArrayList;

import com.ca.arcserve.edge.app.base.appdaos.EdgeArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;


public class SyncServiceFactory {
	
	public static IMySyncService createSyncService(String url, int arcserveId){
		IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
		ArrayList<EdgeArcserveConnectInfo> infos = new ArrayList<EdgeArcserveConnectInfo>();
		connectionInfoDao.as_edge_arcserve_connect_info_list(arcserveId, infos);
		if(infos.size() > 0)
			return new MyService(url, infos.get(0));

		return null;
	}
}
