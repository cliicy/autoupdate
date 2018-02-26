package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.GetASBUServerInfo;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.IASBUServerInfo;

public class GDBSyncFactory {

	public static GDBSyncObject Create(int gdbServerID) {
		GDBSyncObject syncObj = new GDBSyncObject();
		syncObj.setGdbRhostid(gdbServerID);
		
		IASBUServerInfo serverInfo = GetASBUServerInfo.GetServiceInfo(gdbServerID);
		String gdbUrl = serverInfo.get_URL();
		syncObj.setGdbUrl(gdbUrl);
		return syncObj;
	}
	

}
