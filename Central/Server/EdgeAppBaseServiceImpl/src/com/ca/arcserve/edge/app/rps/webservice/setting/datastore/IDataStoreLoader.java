package com.ca.arcserve.edge.app.rps.webservice.setting.datastore;

import java.util.Date;
import java.util.List;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public interface IDataStoreLoader {

	DataStoreSettingInfo loadDataStoreByUUID(int nodeid, String uuid)
			throws EdgeServiceFault;
	
	List<DataStoreSettingInfo> loadDataStoreHistoryByUUID(int nodeid, String uuid, Date timeStamp)
			throws EdgeServiceFault;
}
