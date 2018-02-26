package com.ca.arcserve.edge.app.rps.webservice.setting.datastore;

import java.util.Date;
import java.util.List;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public interface IDataStoreManager {
	String save(DataStoreSettingInfo settingInfo) throws EdgeServiceFault;

	DataStoreSettingInfo getDataStoreByGuid(int nodeid, String guid)
			throws EdgeServiceFault;

	List<DataStoreSettingInfo> getDataStoreHistoryByGuid(int nodeid, String guid, Date timeStamp)
			throws EdgeServiceFault;
	
	DataStoreStatusListElem[] getDataStoreSummariesByNode(int nodeid)
			throws EdgeServiceFault;
	
	DataStoreStatusListElem getDataStoreSummary(int nodeId, String guid)
			throws EdgeServiceFault;

	void deleteDataStoreByGuid(int nodeId, String uuid) throws EdgeServiceFault;
	
	void startDataStoreInstance(int nodeId, String dataStoreUuid) throws EdgeServiceFault;
	void stopDataStoreInstance(int nodeId, String dataStoreUuid) throws EdgeServiceFault;

	DataStoreSettingInfo importDataStoreInstance(
			int nodeID, DataStoreSettingInfo storeSettings, boolean bOverWrite,
			boolean bForceTakeOwnership) throws EdgeServiceFault;

	DataStoreSettingInfo getDataStoreInfoFromDisk(int nodeID, String strPath,
			String strUser, String strPassword, String strDataStorePassword) throws EdgeServiceFault;

	long getDataStoreDedupeRequiredMinMemSizeByte(int nodeid, String dataStoreId) throws EdgeServiceFault;
	
	void forceRefreshDataStoreStatus(int nodeId) throws EdgeServiceFault;

	boolean checkDataStoreDuplicate(DataStoreSettingInfo settingInfo);
}
