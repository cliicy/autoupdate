package com.ca.arcserve.edge.app.rps.webservice.setting.deploy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.soap.SOAPFaultException;

import com.ca.arcflash.rps.webservice.IRPSRegisterService;
import com.ca.arcflash.rps.webservice.data.policy.ItemOperationResult;
import com.ca.arcflash.rps.webservice.data.policy.PolicyOperationErrorCode;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4CPM;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsNode;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsPolicy;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsCommonUtil;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsNodeUtil;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.policy.PolicyDeployReason;
import com.ca.arcserve.edge.app.rps.webservice.i18n.EdgeRPSWebServiceMessages;
import com.ca.arcserve.edge.app.rps.webservice.policy.PolicyLogProxy;
import com.ca.arcserve.edge.app.rps.webservice.setting.datastore.DataStoreWebUtil;

public class PolicyRemove extends RpsSettingDeployBase {

	private String guid;

	public PolicyRemove(int nodeid) {
		this.nodeid = nodeid;
	}

	public PolicyRemove(String guid) {
		this.guid = guid;
	}

	@Override
	public void doDeploy() throws EdgeServiceFault {
		if (nodeid != 0) {
			EdgeRpsNode node = RpsNodeUtil.getNodeById(nodeid);
			deletePolicyByNode(node);
		} else if (!StringUtil.isEmptyOrNull(guid)) {

			unassignPolicy(guid, false);
		}

	}

	private void unassignPolicy(String guid, boolean force)
			throws EdgeServiceFault {

		List<EdgeRpsPolicy> policyList = new ArrayList<EdgeRpsPolicy>();
		policyDao.as_edge_rps_policy_list_by_uuid(guid, policyList);

		if (policyList.isEmpty())
			return;

		EdgeRpsNode node = RpsNodeUtil.getNodeById(policyList.get(0)
				.getNode_id());

		List<String> guidList = new ArrayList<String>();
		guidList.add(guid);

		deletePoliciesOnRPSServer(node, policyList, guidList);

		deletePolicyRecordByIDOnly(policyList.get(0).getPolicy_id());

		outputSuccMessageToActivityLog(policyList.get(0).getPolicy_name(),
				node.getNode_name(), PolicyDeployReason.UnAssign);
	}

	private void deletePolicyByNode(EdgeRpsNode node) throws EdgeServiceFault {
		try {
			DaoFactory.beginTrans();
			// Pick all of the policy which related this node
			List<EdgeRpsPolicy> edgeRpsPolicyList = new ArrayList<EdgeRpsPolicy>();
			policyDao.as_edge_rps_policy_list_by_nodeId(node.getNode_id(),
					edgeRpsPolicyList);
			if (edgeRpsPolicyList.size() == 0)
				return;

			List<String> policyGUIDLst = new ArrayList<String>();
			for (EdgeRpsPolicy erp : edgeRpsPolicyList) {
				policyGUIDLst.add(erp.getPolicy_uuid());
			}

			deletePoliciesOnRPSServer(node, edgeRpsPolicyList, policyGUIDLst);
			for (EdgeRpsPolicy erp : edgeRpsPolicyList) {
				deletePolicyRecordByIDOnly(erp.getPolicy_id());
			}
		} catch(EdgeServiceFault e) {
			try {
				DaoFactory.rollbackTrans();
			} catch (Exception e1) {
			}
			throw e;
		} catch (SOAPFaultException e) {
			try {
				DaoFactory.rollbackTrans();
				
			} catch (Exception e1) {
			}
			throw e;
		}
		
		catch (Throwable e) {
			try {
				DaoFactory.rollbackTrans();
			} catch (Exception e1) {
			}
			throw new RuntimeException();
		} finally {
			if(!DaoFactory.isTransEnd()) {
				try {
					DaoFactory.commitTrans();
				} catch (Exception e) {
				}
			}
		}
		
	}

	private void deletePolicyRecordByIDOnly(int policyid) {
		policyDao.as_edge_rps_policy_delete(policyid);
	}

	private void deletePoliciesOnRPSServer(EdgeRpsNode node,
			List<EdgeRpsPolicy> edgeRpsPolicyList, List<String> policyGUIDLst)
			throws EdgeServiceFault {
		// Go to delete policy file from RPS server
		Map<String, EdgeRpsPolicy> guidToPolicyMap = new HashMap<String, EdgeRpsPolicy>();
		for (EdgeRpsPolicy erp : edgeRpsPolicyList) {
			guidToPolicyMap.put(erp.getPolicy_uuid(), erp);
		}
		try(RPSConnection conn=DataStoreWebUtil.getWebservice(node.getNode_id())){
			IRPSService4CPM webService = conn.getService();
			
			if (RpsCommonUtil.isTheRPSServerManagedByCurrentApp((IRPSRegisterService) webService)) {
	
				List<ItemOperationResult> operationResults = webService
						.deleteRPSPolicies(policyGUIDLst);
				for (ItemOperationResult result : operationResults) {
	
					dealWithItemOperationResult(node,
							guidToPolicyMap.get(result.getItemId())
									.getPolicy_name(), result);
				}
	
			} else {
				PolicyLogProxy
						.getInstance()
						.addLog(Severity.Warning,
								Module.RpsManagement,
								node.getNode_name(),
								EdgeRPSWebServiceMessages
										.getResource("COMMON_THE_NODE_IS_MANAGED_BY_ANOTHER_APP", node.getNode_name()));
			}
		}
	}

	private void dealWithItemOperationResult(EdgeRpsNode node,
			String policyName, ItemOperationResult result)
			throws EdgeServiceFault {
		String message = String.format(
				EdgeRPSWebServiceMessages
						.getMessage("POLICY_MANAGEMENT_DELETE_INFO"),
				policyName,
				node.getNode_name(),
				result.getResultCode() == 0 ? EdgeRPSWebServiceMessages
						.getMessage("POLICY_MANAGEMENT_SUCCEED")
						: EdgeRPSWebServiceMessages
								.getMessage("POLICY_MANAGEMENT_FAILED"));

		switch (result.getResultCode()) {
		case PolicyOperationErrorCode.Success:
			PolicyLogProxy.getInstance().addSuccessLog(
					Module.RpsPolicyManagement, node.getNode_name(), message);
			break;

		case PolicyOperationErrorCode.PolicyNotExist:
			break;

		case PolicyOperationErrorCode.FailedToDeletePolicy:
			PolicyLogProxy.getInstance().addErrorLog(
					Module.RpsPolicyManagement, node.getNode_name(), message);
			Object[] messageParameters = null;
			if (policyName == null || policyName.isEmpty()) {
				messageParameters = new Object[] { node.getNode_name(),
						EdgeServiceErrorCode.POLICY_RPS_DELETE_FAILED };
			} else {
				messageParameters = new Object[] { policyName,
						node.getNode_name(),
						EdgeServiceErrorCode.POLICY_RPS_DELETE_FAILED };
			}
			throw DataStoreWebUtil.generateException(
					EdgeServiceErrorCode.POLICY_RPS_DELETE_FAILED, "",
					messageParameters);
		}
	}
}
