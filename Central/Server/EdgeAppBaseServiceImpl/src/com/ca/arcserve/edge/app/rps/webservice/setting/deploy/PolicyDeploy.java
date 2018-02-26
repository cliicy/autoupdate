package com.ca.arcserve.edge.app.rps.webservice.setting.deploy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import com.ca.arcflash.rps.webservice.IRPSRegisterService;
import com.ca.arcflash.rps.webservice.data.DisabledNodes;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4CPM;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsNode;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsPolicy;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsCommonUtil;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsNodeUtil;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsPolicyUtil;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.policy.PolicyDeployReason;
import com.ca.arcserve.edge.app.rps.webservice.i18n.EdgeRPSWebServiceMessages;
import com.ca.arcserve.edge.app.rps.webservice.serviceexception.EdgeRpsServiceErrorCode;
import com.ca.arcserve.edge.app.rps.webservice.setting.datastore.DataStoreWebUtil;

public class PolicyDeploy extends RpsSettingDeployBase {

	private RPSPolicy policy = null;

	public PolicyDeploy(int nodeid, RPSPolicy policy) {
		this.nodeid = nodeid;
		this.policy = policy;
	}

	@Override
	public void doDeploy() throws EdgeServiceFault {
		EdgeRpsNode node = RpsNodeUtil.getNodeById(nodeid);
//		RpsPolicyUtil.encryptPolicy(policy);
		deployPolicy(node, policy);
	}

	private void deployPolicy(EdgeRpsNode node, RPSPolicy policy)
			throws EdgeServiceFault {

		trimName(policy);
		checkPolicyDuplicate(node, policy);

		try{
			DaoFactory.beginTrans();
			RpsPolicyUtil.encryptPolicy(policy);
			RpsPolicyUtil.saveDatabase(node.getNode_id(), policy);

			
			IRPSService4CPM webService;
			RPSConnection conn = null;
			try{
				try {
					conn = DataStoreWebUtil.getWebservice(node.getNode_id());
					webService=conn.getService();
				} catch (Exception e) {
					throw DataStoreWebUtil.generateException(
							EdgeServiceErrorCode.POLICY_RPS_CANNOT_CONNECT,
							EdgeRPSWebServiceMessages.getResource(
									"RPS_SERVER_LOGIN_FAILED", node.getNode_name()),
							new String[] { node.getNode_name() });
				}
	
				if (RpsCommonUtil.isTheRPSServerManagedByCurrentApp((IRPSRegisterService) webService)) {
					RpsPolicyUtil.decryptPolicy(policy);
					deployPolicyToRPSServer(webService, policy);
					RpsPolicyUtil.encryptPolicy(policy);
				} else {
					throw DataStoreWebUtil
							.generateD2DException(
							  FlashServiceErrorCode.RPS_MANAGED_BY_ANOTHER);
				}
		
				outputSuccMessageToActivityLog(policy.getName(), node.getNode_name(),
						PolicyDeployReason.Assign);
			}finally{
				if(conn!=null)
					conn.close();
			}
		}catch(EdgeServiceFault e){
			try {
				DaoFactory.rollbackTrans();
			} catch (Exception e1) {
			}
			throw e;
		}catch (SOAPFaultException e) {
			try {
				DaoFactory.rollbackTrans();
				
			} catch (Exception e1) {
			}
			throw e;
		}
		catch(Throwable e){
			try {
				DaoFactory.rollbackTrans();
			} catch (Exception e1) {
			}
			throw new RuntimeException();
		}finally{
			if(!DaoFactory.isTransEnd()){
				try {
					DaoFactory.commitTrans();
				} catch (Exception e) {
				}
			}
		}
	}

	private void trimName(RPSPolicy policy) {
		if(policy.getName()!=null){
			policy.setName(policy.getName().trim());
		}
	}

	private void deployPolicyToRPSServer(IRPSService4CPM flashRps,
			RPSPolicy policy) throws EdgeServiceFault {

		if (flashRps != null) {
			// save policy to RPS server
			DisabledNodes dn = new DisabledNodes();
			dn.setDisablePlan(false);			
			flashRps.saveRPSPolicy(policy, dn);
		} else {
			throw DataStoreWebUtil.generateException(
					EdgeRpsServiceErrorCode.Common_Service_General,
					EdgeRPSWebServiceMessages
							.getMessage("COMMON_ERROR_NULL_POINTER"), null);
		}
	}

	private void checkPolicyDuplicate(EdgeRpsNode node, RPSPolicy policy)
			throws EdgeServiceFault {
		List<EdgeRpsPolicy> policyList = new ArrayList<EdgeRpsPolicy>();
		policyDao.as_edge_rps_policy_list_by_nodeId(node.getNode_id(),
				policyList);

		for (EdgeRpsPolicy edgePolicy : policyList) {
			if (edgePolicy.getPolicy_name().toLowerCase().equals(policy.getName().toLowerCase())
					&& edgePolicy.getNode_id() == node.getNode_id()
					&& !edgePolicy.getPolicy_uuid().equals(policy.getId())) {
				throw DataStoreWebUtil.generateException(
						EdgeServiceErrorCode.PolicyManagement_NameDuplicated,
						"PolicyManagement_NameDuplicated", null);
			}
		}
	}
}
