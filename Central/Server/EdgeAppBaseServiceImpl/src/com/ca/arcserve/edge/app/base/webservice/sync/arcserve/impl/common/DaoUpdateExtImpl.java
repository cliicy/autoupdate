package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common;

import com.ca.arcserve.edge.app.base.appdaos.EdgeArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;

public class DaoUpdateExtImpl {

	public static void as_edge_arcserve_connect_info_update_type(
			EdgeArcserveConnectInfo curInfo, int type) {
		IEdgeConnectInfoDao conDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
		conDao.as_edge_arcserve_connect_info_update(curInfo.getHostid(), curInfo
				.getCauser(), curInfo.getCapasswd(), curInfo.getAuthmode(),
				curInfo.getProtocol(), curInfo.getPort(), type, curInfo
						.getVersion(), curInfo.getManaged());
	}

}
