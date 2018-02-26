package com.ca.arcserve.edge.app.rps.webservice.setting.datastore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsDataStoreDao;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsDataStore;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsNode;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsDataStoreUtil;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsNodeUtil;
import com.ca.arcserve.edge.app.rps.webservice.serviceexception.EdgeRpsServiceErrorCode;

public class DataStoreDBLoaderImpl implements IDataStoreLoader {

	private IRpsDataStoreDao datastoreDao = DaoFactory
			.getDao(IRpsDataStoreDao.class);

	@Override
	public DataStoreSettingInfo loadDataStoreByUUID(int nodeid, String uuid)
			throws EdgeServiceFault {
		List<EdgeRpsDataStore> edgeSettingList = new ArrayList<EdgeRpsDataStore>();

		try {
			datastoreDao.as_edge_rps_datastore_setting_list(nodeid, uuid,
					edgeSettingList);
		} catch (Exception e) {
			throw DataStoreWebUtil.generateException(
					EdgeRpsServiceErrorCode.Common_Service_Database_Error,
					e.getMessage(), null);
		}
		if (edgeSettingList.isEmpty())
			return null;

		return RpsDataStoreUtil.converEdgeRpsDataStore(edgeSettingList.get(0));
	}

	@Override
	public List<DataStoreSettingInfo> loadDataStoreHistoryByUUID(int nodeid,
			String uuid, Date timeStamp) throws EdgeServiceFault {
		List<EdgeRpsDataStore> edgeSettingList = new ArrayList<EdgeRpsDataStore>();

		try {
			datastoreDao.as_edge_rps_datastore_setting_history_list(nodeid, uuid, timeStamp,
					edgeSettingList);
		} catch (Exception e) {
			throw DataStoreWebUtil.generateException(
					EdgeRpsServiceErrorCode.Common_Service_Database_Error,
					e.getMessage(), null);
		}
		if (edgeSettingList.isEmpty())
			return null;

		List<DataStoreSettingInfo> lst = new ArrayList<DataStoreSettingInfo>();
		for (EdgeRpsDataStore item : edgeSettingList) {
			DataStoreSettingInfo info = RpsDataStoreUtil.converEdgeRpsDataStore(item);
			EdgeRpsNode rpsNode = RpsNodeUtil.getNodeById(nodeid);
			if (rpsNode != null) {
				info.setOwnershipHostName(rpsNode.getNode_name());
			}
			
			lst.add(info);
		}
		
		return lst;
	}

}
