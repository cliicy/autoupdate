package com.ca.arcserve.edge.app.msp.webservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.policy.MspPlanSettings;
import com.ca.arcflash.rps.webservice.data.policy.MspReplicationSettings;
import com.ca.arcflash.rps.webservice.data.policy.RPSReplicationSettings;
import com.ca.arcflash.rps.webservice.replication.HttpProxy;
import com.ca.arcflash.webservice.edge.data.d2dstatus.AgentOsInfoType;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanEnableStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.msp.webservice.contract.Customer;
import com.ca.arcserve.edge.webservice.msp.IMsp4RpsService;
import com.ca.arcserve.edge.webservice.msp.data.RemoteNodeRegInfo;

public class Msp4RpsServiceImpl implements IMsp4RpsService {
	
	private static Logger logger = Logger.getLogger(Msp4RpsServiceImpl.class);
	private IEdgePolicyDao policyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	
	@Override
	public void validateCustomer(String username, String password) throws EdgeServiceFault {
		MspCustomerServiceImpl customerService = new MspCustomerServiceImpl();
		Customer customer = customerService.getCustomerByName(username);
		customer.setPassword(password);
		customerService.validateCustomer(customer);
	}

	@Override
	public RPSReplicationSettings getRemoteRpsReplicationSettings(String globalPlanUuid) throws EdgeServiceFault {
		int[] policyId = new int[1];
		policyDao.as_edge_policy_getId(globalPlanUuid, policyId);
		if (policyId[0] == 0) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.PolicyManagement_PolicyNotFound, "Cannot find the policy by global uuid " + globalPlanUuid);
		}
		
		UnifiedPolicy mspPlan = PolicyManagementServiceImpl.getInstance().loadUnifiedPolicyById(policyId[0]);
		
		RPSReplicationSettings setting = mspPlan.getMspServerReplicationSettings();
		if (setting != null) {
			setting.setTargetPlanUuid(mspPlan.getUuid());
			
			setting.setMspReplicationSettings(new MspReplicationSettings());
			setting.getMspReplicationSettings().setMspPlanSettings(new MspPlanSettings(globalPlanUuid, mspPlan.getUuid(), mspPlan.getName()));
			
			if (setting.isEnableProxy()) {
				HttpProxy targetRpsServerHttpProxy = setting.isProxyRequireAuthentication()
						? new HttpProxy(setting.getProxyHostname(), setting.getProxyPort(), setting.getProxyUsername(), setting.getProxyPassword())
						: new HttpProxy(setting.getProxyHostname(), setting.getProxyPort());
				setting.getMspReplicationSettings().setMspReplicationHttpProxy(targetRpsServerHttpProxy);
			}
		}
		
		return mspPlan.getMspServerReplicationSettings();
	}

	@Override
	public void registerRemoteNodes(List<RemoteNodeRegInfo> nodes) throws EdgeServiceFault {
		logger.debug("Msp4RpsServiceImpl.registerRemoteNodes - begin.");
		
		MspNodeServiceImpl nodeService = new MspNodeServiceImpl();
		for (RemoteNodeRegInfo node : nodes) {
			logger.debug("Msp4RpsServiceImpl.registerRemoteNodes - register node, node name = " + node.getNodeName() 
					+ ", instance uuid = " + node.getInstanceUuid() 
					+ ", MSP plan uuid = " + node.getMspPlanUuid()
					+ ", os is "+node.getAgentOSInfo());
			
			if (!isMspPlanEnabled(node.getMspPlanUuid())) {
				logger.debug("Msp4RpsServiceImpl.registerRemoteNodes - the MSP plan is not enabled, igore the remote node.");
				continue;
			}
			
			logger.debug("Msp4RpsServiceImpl.registerRemoteNodes - add node.");
			int nodeId = -1;
			try {
				long osType = AgentOsInfoType.AgentOSType.UNKNOWN;
				if(node.getAgentOSInfo()!=null){
					osType = node.getAgentOSInfo().getAgentOSType();
				}
				nodeId = nodeService.addNode(node.getNodeName(), node.getInstanceUuid(), osType);
			} catch (EdgeServiceFault e) {
				if(EdgeServiceErrorCode.Node_AlreadyExist == e.getFaultInfo().getCode()){
					logger.warn("Msp4RpsServiceImpl.registerRemoteNodes - node aready exist, "
							+ "skip this node "+nodeId+"_"+node.getNodeName()+" and continue.");
					continue;
				}else {
					throw e;
				}
			}
			logger.debug("Msp4RpsServiceImpl.registerRemoteNodes - assign plan.");
			nodeService.assignPlan(nodeId, node.getNodeName(), node.getMspPlanUuid(), node.getInstanceUuid());
		}
		
		logger.debug("Msp4RpsServiceImpl.registerRemoteNodes - end.");
	}

	private boolean isMspPlanEnabled(String mspPlanUuid) {
		List<EdgePolicy> policies = new ArrayList<EdgePolicy>();
		policyDao.as_edge_policy_list_by_uuid(mspPlanUuid, policies);
		if (policies.isEmpty()) {
			logger.debug("Msp4RpsServiceImpl.isMspPlanEnabled - cannot find the plan by uuid " + mspPlanUuid);
			return false;
		}
		
		EdgePolicy policy = policies.get(0);
		logger.debug("Msp4RpsServiceImpl.isMspPlanEnabled - plan enable status = " + policy.getEnablestatus());
		
		return policy.getEnablestatus() == PlanEnableStatus.Enable;
	}

}
