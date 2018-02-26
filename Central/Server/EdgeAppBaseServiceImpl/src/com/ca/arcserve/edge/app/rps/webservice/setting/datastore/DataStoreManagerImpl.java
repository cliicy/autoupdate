package com.ca.arcserve.edge.app.rps.webservice.setting.datastore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import com.ca.arcflash.rps.webservice.IRPSRegisterService;
import com.ca.arcflash.rps.webservice.RPSWebServiceClientProxy;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreRunningState;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreOperationElement;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcflash.rps.webservice.data.ds.DedupSettingInfo;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4CPM;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.NodeConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.RpsServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVM;
import com.ca.arcserve.edge.app.base.webservice.instantvm.InstantVMManager;
import com.ca.arcserve.edge.app.base.webservice.notify.StatusUtil;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsDataStoreDao;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsNodeDao;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsDataStore;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsNode;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsCommonUtil;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsDataStoreUtil;
import com.ca.arcserve.edge.app.rps.webservice.setting.deploy.DataStoreDeploy;
import com.ca.arcserve.edge.app.rps.webservice.setting.deploy.DataStoreRemove;
import com.ca.arcserve.edge.app.rps.webservice.setting.deploy.RpsSettingDeployBase;

public class DataStoreManagerImpl implements IDataStoreManager {

	private IRpsDataStoreDao datastoreDao = DaoFactory
			.getDao(IRpsDataStoreDao.class);
	private static IRpsNodeDao nodeDao = DaoFactory.getDao(IRpsNodeDao.class);
	private IDataStoreLoader loader = new DataStoreDBLoaderImpl();
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);

	@Override
	public String save(DataStoreSettingInfo settingInfo) throws EdgeServiceFault {
		trimName(settingInfo);
//		if(settingInfo.getDatastore_name()==null || settingInfo.getDatastore_name().isEmpty()){
//			settingInfo.setDatastore_name(UUID.randomUUID().toString());
//		}

		if (checkDataStoreDuplicate(settingInfo))
			throw DataStoreWebUtil
					.generateD2DException(FlashServiceErrorCode.RPS_DATASTORE_INSTANCE_ALREADY_EXISTS);

		RpsSettingDeployBase deploy = new DataStoreDeploy(
				settingInfo.getNode_id(), settingInfo);
		deploy.doDeploy();
		
		//if change data store name, the starting instantvm job should show new name in detail page.
		InstantVMManager.getInstance().changeDataStoreName(settingInfo.getDatastore_name(), settingInfo.getDisplayName());
		
		return settingInfo.getDatastore_name();
	}
	

	private void trimName(DataStoreSettingInfo settingInfo) {
		if(settingInfo.getDatastore_name()!=null){
			settingInfo.setDatastore_name(settingInfo.getDatastore_name().trim());
		}
		if(settingInfo.getDisplayName()!=null){
			settingInfo.setDisplayName(settingInfo.getDisplayName().trim());
		}
	}

	@Override
	public void deleteDataStoreByGuid(int nodeid, String uuid) throws EdgeServiceFault {
		RpsSettingDeployBase deploy = new DataStoreRemove(nodeid, uuid);
		deploy.doDeploy();
	}
	
	@Override
	public boolean checkDataStoreDuplicate(DataStoreSettingInfo settingInfo) {

		return checkDataStoreName(settingInfo.getNode_id(),
				settingInfo.getDisplayName(), settingInfo.getDatastore_name());
	}

	private boolean checkDataStoreName(Integer nodeId, String dataStoreName,
			String guid) {
		List<EdgeRpsDataStore> dataStoreList = new ArrayList<EdgeRpsDataStore>();
		datastoreDao.as_edge_rps_datastore_setting_list_by_nodeid(nodeId,
				dataStoreList);
		for (EdgeRpsDataStore edgeRpsDedup : dataStoreList) {
			if (edgeRpsDedup.getDatastore_name().toLowerCase().equals(dataStoreName.toLowerCase())
					&& !edgeRpsDedup.getDatastore_uuid().equals(guid))
				return true;
		}
		return false;
	}


	@Override
	public DataStoreSettingInfo getDataStoreByGuid(int nodeid, String guid)
			throws EdgeServiceFault {
		return loader.loadDataStoreByUUID(nodeid, guid);
	}


	@Override
	public DataStoreStatusListElem[] getDataStoreSummariesByNode(int nodeId) throws EdgeServiceFault {
		ConnectionContext context = new NodeConnectionContextProvider(nodeId).create();
		
		try (RPSConnection connection = connectionFactory.createRPSConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			
			DataStoreStatusListElem[] result = connection.getService().getDataStoreStatus(null);
			
			return result;
		} catch (SOAPFaultException e) {
			throw e;
		} catch (WebServiceException e) {
			throw DataStoreWebUtil.generateException(
					EdgeServiceErrorCode.POLICY_RPS_CANNOT_CONNECT,
					MessageReader.getErrorMessage(EdgeServiceErrorCode.POLICY_RPS_CANNOT_CONNECT, context.getHost()),
					new String[] { context.getHost() });
		}
	}


	@Override
	public DataStoreStatusListElem getDataStoreSummary(int nodeId, String guid)
			throws EdgeServiceFault {
		List<EdgeRpsNode> hosts = new ArrayList<EdgeRpsNode>();
		nodeDao.as_edge_rps_node_list(nodeId, hosts);
		if(hosts.isEmpty())
			return null;
		EdgeRpsNode host= hosts.get(0);
		
		IRPSService4CPM webService;
		RPSConnection conn = null;
		try{
			try {
				conn = DataStoreWebUtil.getWebservice(nodeId);
				webService=conn.getService();
			} catch (Exception e) {
				throw DataStoreWebUtil.generateException(
						EdgeServiceErrorCode.POLICY_RPS_CANNOT_CONNECT,
						MessageReader.getErrorMessage(EdgeServiceErrorCode.POLICY_RPS_CANNOT_CONNECT, host.getNode_name()),
						new String[] { host.getNode_name() });
			}
			
			try {
				DataStoreStatusListElem[] result = webService.getDataStoreStatus(guid);
				return result[0];
			} catch (SOAPFaultException e) {
				throw RpsServiceFault.getRpsFault(true, e.getFault().getFaultCode(),
						e.getFault().getFaultString());
			}
		}finally{
			if(conn!=null)
				conn.close();
		}
	}


	@Override
	public void startDataStoreInstance(int nodeId, String dataStoreUuid)
			throws EdgeServiceFault {
		List<EdgeRpsNode> hosts = new ArrayList<EdgeRpsNode>();
		nodeDao.as_edge_rps_node_list(nodeId, hosts);
		if(hosts.isEmpty())
			return;
		EdgeRpsNode host= hosts.get(0);
		
		IRPSService4CPM webService;
		RPSConnection conn = null;
		try{
			try {
				conn = DataStoreWebUtil.getWebservice(nodeId);
				webService=conn.getService();
			}catch(SOAPFaultException e){
				throw DataStoreWebUtil.generateException(
						EdgeServiceErrorCode.POLICY_RPS_WrongCredential,
						MessageReader.getErrorMessage(EdgeServiceErrorCode.POLICY_RPS_WrongCredential, host.getNode_name()),
						new String[] { host.getNode_name() });
			} catch (Exception e) {
				throw DataStoreWebUtil.generateException(
						EdgeServiceErrorCode.POLICY_RPS_CANNOT_CONNECT,
						MessageReader.getErrorMessage(EdgeServiceErrorCode.POLICY_RPS_CANNOT_CONNECT, host.getNode_name()),
						new String[] { host.getNode_name() });
			}
			
			webService.startDataStoreInstance(new DataStoreOperationElement(dataStoreUuid, false));
			StatusUtil.setDatastoreStatus(host.getUuid(), dataStoreUuid, DataStoreRunningState.STARTING, null);
		}finally{
			if(conn!=null)
				conn.close();
		}
	}


	@Override
	public void stopDataStoreInstance(int nodeId, String dataStoreUuid)
			throws EdgeServiceFault {
		List<EdgeRpsNode> hosts = new ArrayList<EdgeRpsNode>();
		nodeDao.as_edge_rps_node_list(nodeId, hosts);
		if(hosts.isEmpty())
			return;
		EdgeRpsNode host= hosts.get(0);
		
		IRPSService4CPM webService;
		RPSConnection conn = null;
		try{
			try {
				conn = DataStoreWebUtil.getWebservice(nodeId);
				webService=conn.getService();
			}catch(SOAPFaultException e){
				throw DataStoreWebUtil.generateException(
						EdgeServiceErrorCode.POLICY_RPS_WrongCredential,
						MessageReader.getErrorMessage(EdgeServiceErrorCode.POLICY_RPS_WrongCredential, host.getNode_name()),
						new String[] { host.getNode_name() });
			} catch (Exception e) {
				throw DataStoreWebUtil.generateException(
						EdgeServiceErrorCode.POLICY_RPS_CANNOT_CONNECT,
						MessageReader.getErrorMessage(EdgeServiceErrorCode.POLICY_RPS_CANNOT_CONNECT, host.getNode_name()),
						new String[] { host.getNode_name() });
			}		
			
			webService.stopDataStoreInstance(new DataStoreOperationElement(dataStoreUuid, false));		
			StatusUtil.setDatastoreStatus(host.getUuid(), dataStoreUuid, DataStoreRunningState.STOPPING, null);
		}finally{
			if(conn!=null)
				conn.close();
		}
	}

	@Override
	public DataStoreSettingInfo importDataStoreInstance(
			int nodeID, DataStoreSettingInfo storeSettings, boolean bOverWrite,
			boolean bForceTakeOwnership) throws EdgeServiceFault {
		List<EdgeRpsNode> nodeList = new ArrayList<EdgeRpsNode>();
		nodeDao.as_edge_rps_node_list(nodeID, nodeList);
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(nodeID)){
			RPSWebServiceClientProxy proxy = conn.getClientProxy();
			if (RpsCommonUtil.isTheRPSServerManagedByCurrentApp((IRPSRegisterService)proxy.getServiceForCPM())) {
				DataStoreSettingInfo newinfo = proxy.getServiceForCPM().importDataStoreInstance(storeSettings, bOverWrite, bForceTakeOwnership);
				StatusUtil.setDatastoreStatus(nodeList.get(0).getUuid(), newinfo.getDatastore_name(), DataStoreRunningState.STARTING, null);
				newinfo.setNode_id(nodeID);
				RpsDataStoreUtil.saveDatabase(newinfo);
				return newinfo;
			} else {
				throw DataStoreWebUtil
						.generateD2DException(
						  FlashServiceErrorCode.RPS_MANAGED_BY_ANOTHER);
			}
		}
	}

	@Override
	public DataStoreSettingInfo getDataStoreInfoFromDisk(int nodeID, String strPath,
			String strUser, String strPassword, String strDataStorePassword) throws EdgeServiceFault {
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(nodeID)){
			RPSWebServiceClientProxy proxy = conn.getClientProxy();
			DataStoreSettingInfo info= proxy.getServiceForCPM().getDataStoreInfoFromDisk(strPath, strUser, strPassword, strDataStorePassword);
			if(info.getEnableEncryption()==1){
				info.setEncryptionPwd(WSJNI.AFDecryptStringEx(info.getEncryptionPwd()));
			}
			if(info.getDSCommSetting().getPassword()!=null){
				info.getDSCommSetting().setPassword(WSJNI.AFDecryptStringEx(info.getDSCommSetting().getPassword()));
			}
			if(info.getEnableGDD()==1){
				DedupSettingInfo gdd = info.getGDDSetting();
				if(gdd.getDataStorePassword()!=null){
					gdd.setDataStorePassword(WSJNI.AFDecryptStringEx(gdd.getDataStorePassword()));
				}
				if(gdd.getIndexStorePassword()!=null){
					gdd.setIndexStorePassword(WSJNI.AFDecryptStringEx(gdd.getIndexStorePassword()));
				}
				if(gdd.getHashStorePassword()!=null){
					gdd.setHashStorePassword(WSJNI.AFDecryptStringEx(gdd.getHashStorePassword()));
				}
			}
			return info;
		}
	}


	@Override
	public List<DataStoreSettingInfo> getDataStoreHistoryByGuid(int nodeid,
			String guid, Date timeStamp) throws EdgeServiceFault {
		return loader.loadDataStoreHistoryByUUID(nodeid, guid, timeStamp);
	}

	@Override
	public long getDataStoreDedupeRequiredMinMemSizeByte(int nodeID, String dataStoreId)
			throws EdgeServiceFault {
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(nodeID)){
			RPSWebServiceClientProxy proxy = conn.getClientProxy();
			if (RpsCommonUtil.isTheRPSServerManagedByCurrentApp((IRPSRegisterService)proxy.getServiceForCPM())) {
				return proxy.getServiceForCPM().DataStoreGetDedupeRequiredMinMemSizeByte(dataStoreId);
			} else {
				throw DataStoreWebUtil
						.generateD2DException(
						  FlashServiceErrorCode.RPS_MANAGED_BY_ANOTHER);
			}
		}
	}
	
	@Override
	public void forceRefreshDataStoreStatus(int nodeId) throws EdgeServiceFault {
		List<EdgeRpsNode> hosts = new ArrayList<EdgeRpsNode>();
		nodeDao.as_edge_rps_node_list(nodeId, hosts);
		if (hosts.isEmpty())
			return;
		EdgeRpsNode host = hosts.get(0);

		IRPSService4CPM webService;
		RPSConnection conn = null;
		try{
			try {
				conn = DataStoreWebUtil.getWebservice(nodeId);
				webService=conn.getService();
			} catch (Exception e) {
				throw DataStoreWebUtil.generateException(
						EdgeServiceErrorCode.POLICY_RPS_CANNOT_CONNECT,
						MessageReader.getErrorMessage(
								EdgeServiceErrorCode.POLICY_RPS_CANNOT_CONNECT,
								host.getNode_name()), new String[] { host
								.getNode_name() });
			}
	
			try {
				webService.DataStoreUpdateDSPathStatus("");
	
			} catch (SOAPFaultException e) {			
				throw RpsServiceFault.getRpsFault(true, e.getFault().getFaultCode(),
						e.getFault().getFaultString());
			}
		}finally{
			if(conn!=null)
				conn.close();
		}
	}
}
