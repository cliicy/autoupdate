package com.ca.arcserve.edge.app.rps.webservice.setting.deploy;

import javax.xml.ws.soap.SOAPFaultException;

import com.ca.arcflash.rps.webservice.IRPSRegisterService;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreRunningState;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4CPM;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.notify.StatusUtil;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsNode;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsCommonUtil;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsDataStoreUtil;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsNodeUtil;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.policy.PolicyDeployReason;
import com.ca.arcserve.edge.app.rps.webservice.i18n.EdgeRPSWebServiceMessages;
import com.ca.arcserve.edge.app.rps.webservice.serviceexception.EdgeRpsServiceErrorCode;
import com.ca.arcserve.edge.app.rps.webservice.setting.datastore.DataStoreWebUtil;

public class DataStoreDeploy extends RpsSettingDeployBase {

	private DataStoreSettingInfo setting = null;

	public DataStoreDeploy(int nodeid, DataStoreSettingInfo datastoreSetting) {
		this.nodeid = nodeid;
		this.setting = datastoreSetting;
	}

	@Override
	public void doDeploy() throws EdgeServiceFault {
		EdgeRpsNode target = RpsNodeUtil.getNodeById(nodeid);

		deployDatastore(target, setting);
	}

	private void deployDatastore(EdgeRpsNode target,
			DataStoreSettingInfo datastoreSetting) throws EdgeServiceFault {
		String hostname = target.getNode_name();
		IRPSService4CPM webService;
		RPSConnection conn = null;
		try{
			try {
				conn = DataStoreWebUtil.getWebservice(target.getNode_id());
				webService=conn.getService();
			} catch (Exception e) {
				if(!StringUtil.isEmptyOrNull(datastoreSetting.getDatastore_name()))
					StatusUtil.setDatastoreStatus(target.getUuid(), datastoreSetting.getDatastore_name(), DataStoreRunningState.UNKNOWN, null);
				if(e instanceof EdgeServiceFault){
					throw e;
				}else {
					throw DataStoreWebUtil.generateException(
							EdgeServiceErrorCode.POLICY_RPS_CANNOT_CONNECT,
							EdgeRPSWebServiceMessages.getResource(
									"RPS_SERVER_LOGIN_FAILED", hostname),
							new String[] { hostname });
				}
			}
	
			if (RpsCommonUtil.isTheRPSServerManagedByCurrentApp((IRPSRegisterService)webService)) {
				try{
					DataStoreSettingInfo newdatastoreSetting=deployToRPSServer(webService,target.getUuid(), datastoreSetting);	
					datastoreSetting.setDatastore_name(newdatastoreSetting.getDatastore_name());
					datastoreSetting.getDSCommSetting().setStoreSharedName(newdatastoreSetting.getDSCommSetting().getStoreSharedName());
					datastoreSetting=newdatastoreSetting;
					datastoreSetting.setFlags(0);					
				}catch(SOAPFaultException e){
					if(datastoreSetting.getFlags() == DataStoreSettingInfo.PHASE_DATASTORE_WAITING_CREATE)
						RpsDataStoreUtil.updateDataStoreMessageToDB(datastoreSetting,  e.getFault().getFaultString());	
					throw e;					
				}

			} else {
				throw DataStoreWebUtil
						.generateD2DException(
						  FlashServiceErrorCode.RPS_MANAGED_BY_ANOTHER);
			}
			RpsDataStoreUtil.saveDatabase(datastoreSetting);
			RpsDataStoreUtil.updateDataStoreMessageToDB(datastoreSetting,  ""); //after create Datastore and update DB successfully, clear the errorMessage from DB.
			
			outputSuccMessageToActivityLog(datastoreSetting.getDisplayName(),
					hostname, PolicyDeployReason.Assign);
		}finally{
			if(conn!=null)
				conn.close();
		}
	}
		
	private DataStoreSettingInfo deployToRPSServer(IRPSService4CPM flashRps,
			String serverUuid, DataStoreSettingInfo datastoreSetting) throws EdgeServiceFault {
		DataStoreSettingInfo newdatastoreSetting;
		if (flashRps != null) {
			// check the dedup whether have exist on rps server
			boolean bIsExist = false;
			if(datastoreSetting.getDatastore_name()!=null && !datastoreSetting.getDatastore_name().isEmpty() 
					&& datastoreSetting.getFlags()!= DataStoreSettingInfo.PHASE_DATASTORE_WAITING_CREATE)
				bIsExist=true;

			datastoreSetting.getDSCommSetting().setStoreSharedName(""); // generate in backend
			
			// save policy to RPS server
			long ret = 0;
			if (bIsExist) {
				newdatastoreSetting=flashRps.modifyDataStoreInstance(datastoreSetting);
				StatusUtil.setDatastoreStatus(serverUuid, newdatastoreSetting.getDatastore_name(), DataStoreRunningState.MODIFYING, null);
			} else {
				newdatastoreSetting=flashRps.addDataStoreInstance(datastoreSetting);
				StatusUtil.setDatastoreStatus(serverUuid, newdatastoreSetting.getDatastore_name(), DataStoreRunningState.STARTING, null);
			}

			if (ret != 0) {
				throw DataStoreWebUtil.generateException(
						EdgeRpsServiceErrorCode.Common_Service_General,
						String.valueOf(ret), null);
			}
			return newdatastoreSetting;
		} else {
//			StatusUtil.setDatastoreStatus(serverUuid, datastoreSetting.getDatastore_name(), DataStoreRunningState.UNKNOWN, null);
			throw DataStoreWebUtil.generateException(
					EdgeRpsServiceErrorCode.Common_Service_General,
					EdgeRPSWebServiceMessages
							.getMessage("COMMON_ERROR_NULL_POINTER"), null);

		}

	}
}
