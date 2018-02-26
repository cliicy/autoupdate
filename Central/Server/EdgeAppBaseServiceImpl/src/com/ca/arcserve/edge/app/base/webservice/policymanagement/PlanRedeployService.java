package com.ca.arcserve.edge.app.base.webservice.policymanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.WebServiceFaultMessageRetriever;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan.RpsSettingTaskDeployment;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;

public class PlanRedeployService {
	
	private static Logger logger = Logger.getLogger(PlanRedeployService.class);
	private static PlanRedeployService instance = new PlanRedeployService();
	
	private IEdgePolicyDao edgePolicyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	private ExecutorService service;
	
	private PlanRedeployService() {
	}
	
	public static PlanRedeployService getInstance() { 
		return instance;
	}
	
	public void initialize(ExecutorService service) {
		this.service = service;
	}
	
	public List<UnifiedPolicy> getValidPlans(List<Integer> planIds) throws EdgeServiceFault {
		List<UnifiedPolicy> plans = new ArrayList<UnifiedPolicy>();
		
		for (Integer policyId : planIds) {
			List<EdgePolicy> policies = new ArrayList<EdgePolicy>();
			edgePolicyDao.as_edge_policy_list(policyId, 0, policies);
			List<EdgeHostPolicyMap> policyMaps = new ArrayList<EdgeHostPolicyMap>();
			edgePolicyDao.as_edge_plan_getDeployList(policyId, -1, policyMaps);
			if (policies.isEmpty()) {
				continue;
			}
			
			EdgePolicy policy = policies.get(0);
			boolean haveDeployD2DStatus = false;
			for (EdgeHostPolicyMap edgeHostPolicyMap : policyMaps) {
				if(edgeHostPolicyMap.getDeployStatus()==PolicyDeployStatus.DeployD2DSucceed)
					haveDeployD2DStatus = true;
			}
			if (policy.getStatus().isInProgress()
					&& !haveDeployD2DStatus ){
				continue;
			}
			
			plans.add(PolicyManagementServiceImpl.getInstance().loadUnifiedPolicyById(policyId));
		}
		
		return plans;
	}
	
	public void redeploy(List<Integer> planIds) throws EdgeServiceFault {
		List<UnifiedPolicy> plans = getValidPlans(planIds);
		
		for (final UnifiedPolicy plan : plans) {
			edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.DeployingRpsPolicy);
			edgePolicyDao.as_edge_policy_setDeployErrorMessage(plan.getId(), "");
			service.submit(new Runnable() {
				
				@Override
				public void run() {
					redeploy(plan);
				}
				
			});
		}
	}

	private void redeploy(UnifiedPolicy plan) {
		if (!redeployRpsPolicy(plan)) {
			return;
		}
		List<EdgeHostPolicyMap> maps = new ArrayList<EdgeHostPolicyMap>();
		edgePolicyDao.as_edge_plan_getDeployList(plan.getId(),-1, maps);
		if (maps.isEmpty()) {
			edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.DeploySuccess);
			return;
		}
		if (!hasDeployTask(plan)) {
			edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.DeploySuccess);
			edgePolicyDao.as_edge_policy_updateStatus_4node(plan.getId(), PolicyDeployStatus.DeployedSuccessfully);
			return;
		}
		try {
			edgePolicyDao.redeployPolicy(PolicyTypes.Unified, plan.getId(),PolicyDeployReasons.ReDeployManually);
			PolicyManagementServiceImpl.getInstance().getPolicyDeploymentScheduler().doDeploymentNowByPlanId(plan.getId());
		} catch (Exception e) {
			logger.error("[PlanRedeployService].redeploy(UnifiedPlan) failed. plan is: "+plan.getName(), e);
		}
	}
	
	private boolean redeployRpsPolicy(UnifiedPolicy plan) {
		RpsSettingTaskDeployment deployment = new RpsSettingTaskDeployment();
		
		try {
			edgePolicyDao.as_edge_policy_updateStatus_4node(plan.getId(), PolicyDeployStatus.CreateRPSPolicy_Creating);
			PolicyManagementServiceImpl.getInstance().checkRpsVersion(plan, plan.getId());
			deployment.createRpsPolicySettings(plan);
			return true;
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			if (e instanceof SOAPFaultException) {
				errorMessage = ((SOAPFaultException) e).getFault().getFaultString();
			}
			
			if(e instanceof EdgeServiceFault){
				errorMessage = WebServiceFaultMessageRetriever.getErrorMessage( DataFormatUtil.getServerLocale(),((EdgeServiceFault) e).getFaultInfo());
			}
			
			logger.debug("PlanRedeployService.redeployRpsPolicy(UnifiedPlan) failed, error message = " + errorMessage);
			edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.DeployRpsPolicyFailed);
			edgePolicyDao.as_edge_policy_updateStatus_4node(plan.getId(), PolicyDeployStatus.CreateRPSPolicy_Failed);
			edgePolicyDao.as_edge_policy_setDeployErrorMessage(plan.getId(), errorMessage);
			return false;
		}
	}
	
	public boolean hasDeployTask(UnifiedPolicy plan) {
		boolean hasDeployTask = true;
		if (plan.getMspServerReplicationSettings() != null
				&& plan.getConversionConfiguration() == null) {
			hasDeployTask = false;
		}
		return hasDeployTask;
	}
}
