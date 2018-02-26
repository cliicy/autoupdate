package com.ca.arcserve.edge.app.rps.webservice.setting.deploy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import com.ca.arcflash.rps.webservice.IRPSRegisterService;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreRunningState;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreOperationElement;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4CPM;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.RPSPolicyWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.notify.StatusUtil;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsDataStore;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsNode;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsCommonUtil;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsNodeUtil;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.policy.PolicyDeployReason;
import com.ca.arcserve.edge.app.rps.webservice.i18n.EdgeRPSWebServiceMessages;
import com.ca.arcserve.edge.app.rps.webservice.policy.PolicyLogProxy;
import com.ca.arcserve.edge.app.rps.webservice.serviceexception.EdgeRpsServiceErrorCode;
import com.ca.arcserve.edge.app.rps.webservice.setting.datastore.DataStoreWebUtil;

public class DataStoreRemove extends RpsSettingDeployBase {

	private String dsuuid = "";

	// Remove datastore by nodeid task
	public DataStoreRemove(int nodeid) {
		this.nodeid = nodeid;
	}

	// Remove datastore by uuid task
	public DataStoreRemove(int nodeid, String uuid) {
		this.nodeid = nodeid;
		this.dsuuid = uuid;
	}

	@Override
	public void doDeploy() throws EdgeServiceFault {
		if (!StringUtil.isEmptyOrNull(dsuuid))
			unassignDataStoreByUUID(nodeid, dsuuid);
		else
			unassignDataStoreByNode(nodeid);
	}

	private void unassignDataStoreByNode(int nodeId) throws EdgeServiceFault {
		List<EdgeRpsDataStore> datastoreList = new ArrayList<EdgeRpsDataStore>();
		datastoreDao.as_edge_rps_datastore_setting_list_by_nodeid(nodeId,
				datastoreList);
		for (EdgeRpsDataStore ds : datastoreList) {
			try {
				unassignDataStoreByUUID(nodeId, ds.getDatastore_uuid());
			} catch (EdgeServiceFault e) {
				if (e.getFaultInfo()
						.getCode()
						.compareTo(
								EdgeServiceErrorCode.POLICY_RPS_DELETE_FAILED_USED) == 0) {
					throw DataStoreWebUtil
							.generateException(
									EdgeServiceErrorCode.POLICY_RPS_DELETE_FAILED_USED,
									"Failed_PolicyIsInUse",
									new Object[] {
											e.getFaultInfo()
													.getMessageParameters()[0],
											ds.getDatastore_name(),
											ds.getDatastore_uuid() });
				}
			}
		}
		
	}

	private void unassignDataStoreByUUID(int nodeid, String datastoreUUID)
			throws EdgeServiceFault {
			List<EdgeRpsDataStore> datastoreList = new ArrayList<EdgeRpsDataStore>();
	
			datastoreDao.as_edge_rps_datastore_setting_list(nodeid, datastoreUUID,
					datastoreList);
	
			if (datastoreList.isEmpty())
				throw DataStoreWebUtil.generateException(
						EdgeRpsServiceErrorCode.Common_Service_General,
						"DataStoreList is empty", null);
	
			EdgeRpsNode node = RpsNodeUtil.getNodeById(nodeid);
			checkIsDataStoreInUse(node, datastoreUUID);
			
			try {
				deleteDataStoreOnRPSServer(node, datastoreList.get(0));
			} catch (EdgeServiceFault e) {
				throw e;
			} catch (SOAPFaultException e) {
				throw e;
			} catch (Exception e) {
				DataStoreWebUtil.generateException(
						EdgeRpsServiceErrorCode.Common_Service_General,
						e.toString(), null);
			}
			
			outputSuccMessageToActivityLog(
					datastoreList.get(0).getDatastore_name(), node.getNode_name(),
					PolicyDeployReason.UnAssign);
	}

	private void checkIsDataStoreInUse(EdgeRpsNode node, String dataStoreUUID)
			throws EdgeServiceFault {
		List<PolicyInfo> planInfos =  PolicyManagementServiceImpl.getInstance().getPlanList();
		for( PolicyInfo pi : planInfos ) {
			UnifiedPolicy planDetail = PolicyManagementServiceImpl.getInstance().loadUnifiedPolicyById( pi.getPolicyId() );
			String lastName="";
			if(planDetail.getBackupConfiguration()!=null){
				if(!planDetail.getBackupConfiguration().isD2dOrRPSDestType()){
					lastName=planDetail.getBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostname();
				}
			}else if(planDetail.getVSphereBackupConfiguration()!=null){
				if(!planDetail.getVSphereBackupConfiguration().isD2dOrRPSDestType()){
					lastName=planDetail.getVSphereBackupConfiguration().getBackupRpsDestSetting().getRpsHost().getRhostname();
				}
			}else if(planDetail.getMspServerReplicationSettings()!=null){
				lastName=planDetail.getMspServerReplicationSettings().getHostName();
			}
			List<RPSPolicyWrapper> rpsPolicies =planDetail.getRpsPolices();
			for( RPSPolicyWrapper rpsPolicy :  rpsPolicies ) {				
				String datastoreString= rpsPolicy.getRpsPolicy().getRpsSettings().getRpsDataStoreSettings().getDataStoreName();
				if((lastName+"_"+datastoreString).equals(node.getNode_name()+"_"+dataStoreUUID))
					throw DataStoreWebUtil.generateD2DException(FlashServiceErrorCode.RPS_DATASTORE_REFERENCED_BY_POLICY,
							"Failed_PolicyIsInUse", null);
				if(rpsPolicy.getRpsPolicy().getRpsSettings().getRpsReplicationSettings()!=null)
					lastName=rpsPolicy.getRpsPolicy().getRpsSettings().getRpsReplicationSettings().getHostName();
			}
		}
	}

	private void deleteDataStoreOnRPSServer(EdgeRpsNode node,
			EdgeRpsDataStore datastoreSetting) throws EdgeServiceFault {

		IRPSService4CPM webService = null;
		RPSConnection conn = null;
		try{
			try {
				conn = DataStoreWebUtil.getWebservice(node.getNode_id());
				webService=conn.getService();
			} catch (Exception e) {
				throw DataStoreWebUtil.generateException(
						EdgeServiceErrorCode.POLICY_RPS_CANNOT_CONNECT,
						EdgeRPSWebServiceMessages.getResource(
								"DATASTORE_DELETE_POLICY_FAILED",
								node.getNode_name()), new String[] { node
								.getNode_name() });
			}
	
			if (RpsCommonUtil.isTheRPSServerManagedByCurrentApp((IRPSRegisterService)webService)) {
	
				try {
					webService.removeDataStoreInstance(
						new DataStoreOperationElement(datastoreSetting.getDatastore_uuid(),true));
					StatusUtil.setDatastoreStatus(node.getUuid(), datastoreSetting.getDatastore_uuid(), DataStoreRunningState.DELETED, null);
				} catch (SOAPFaultException e) {
					boolean ignore = false;
					if (e.getFault() != null) {
						String error = e.getFault().getFaultCodeAsQName()
								.getLocalPart();
						if (FlashServiceErrorCode.RPS_DATASTORE_INSTANCE_NOT_EXISTS
								.equals(error)) {
							ignore = true;
						}
					}
					if (!ignore)
						throw e;
					else
						StatusUtil.setDatastoreStatus(node.getUuid(), datastoreSetting.getDatastore_uuid(), DataStoreRunningState.DELETED, null);
				}
	
			} else {
				PolicyLogProxy
						.getInstance()
						.addErrorLog(
								Module.RpsManagement,
								node.getNode_name(),
								EdgeRPSWebServiceMessages
										.getResource("COMMON_THE_NODE_IS_MANAGED_BY_ANOTHER_APP",node.getNode_name()));
	            throw DataStoreWebUtil.generateD2DException(FlashServiceErrorCode.RPS_MANAGED_BY_ANOTHER, 
	            		EdgeRPSWebServiceMessages.getResource("COMMON_THE_NODE_IS_MANAGED_BY_ANOTHER_APP",node.getNode_name()),
	            		null);
	
			}
		}finally{
			if(conn!=null)
				conn.close();
		}

	}
}
