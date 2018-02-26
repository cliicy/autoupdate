package com.ca.arcserve.edge.app.base.webservice.policymanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

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
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.asbuintegration.ASBUDestinationManager;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanEnableStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;

public class PlanPauseResumeService {
	private static Logger logger = Logger.getLogger(PlanRedeployService.class);
	private static PlanPauseResumeService instance = new PlanPauseResumeService();
	
	private IEdgePolicyDao edgePolicyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	private ExecutorService service;
	private final ReentrantLock lock = new ReentrantLock();
	
	private PlanPauseResumeService() {
	}
	
	public static PlanPauseResumeService getInstance() { 
		return instance;
	}
	
	public void initialize(ExecutorService service) {
		this.service = service;
	}
	
	private boolean enableRpsPolicy(boolean value, UnifiedPolicy plan) {
		logger.info((value?"Enable":"Disable")+"rps policy");
		edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.DeployingRpsPolicy);
		edgePolicyDao.as_edge_policy_setDeployErrorMessage(plan.getId(), "");
		RpsSettingTaskDeployment deployment = new RpsSettingTaskDeployment();
		if (value) {
			PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Information, "",
					EdgeCMWebServiceMessages.getResource("policyEnableConnectRps"));
		} else {
			PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Information, "",
					EdgeCMWebServiceMessages.getResource("policyDisableConnectRps"));
		}
		try {
			edgePolicyDao.as_edge_policy_updateStatus_4node(plan.getId(), PolicyDeployStatus.CreateRPSPolicy_Creating);
			//PolicyManagementServiceImpl.getInstance().checkRpsVersion(plan, plan.getId());
			deployment.createRpsPolicySettings(plan);
			if (value) {
				PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Information, "",
						EdgeCMWebServiceMessages.getResource("policyEnableConnectRpsSucc"));
			} else {
				PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Information, "",
						EdgeCMWebServiceMessages.getResource("policyDisableConnectRpsSucc"));
			}
			return true;
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			if (e instanceof SOAPFaultException) {
				errorMessage = ((SOAPFaultException) e).getFault().getFaultString();
			}
			
			if(e instanceof EdgeServiceFault){
				errorMessage = WebServiceFaultMessageRetriever.getErrorMessage( DataFormatUtil.getServerLocale(),((EdgeServiceFault) e).getFaultInfo());
			}
			
			edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.DeployRpsPolicyFailed);
			edgePolicyDao.as_edge_policy_updateStatus_4node(plan.getId(), PolicyDeployStatus.CreateRPSPolicy_Failed);
			edgePolicyDao.as_edge_policy_setDeployErrorMessage(plan.getId(), errorMessage);
			PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Error, "", errorMessage);
			logger.error("PlanPauseResumeService.enableRpsPolicy(UnifiedPlan) failed, error message = " + errorMessage, e);
			return false;
		}
	}
	
	private boolean enableAsbuPolicy(boolean value, UnifiedPolicy plan) {
		logger.info((value?"Enable ":"Disable ")+"asbu policy");
		edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.DeployingAsbuPolicy);
		edgePolicyDao.as_edge_policy_setDeployErrorMessage(plan.getId(), "");
		if (value) {
			PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Information, "",
					EdgeCMWebServiceMessages.getResource("policyEnableConnectAsbu"));
		} else {
			PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Information, "",
					EdgeCMWebServiceMessages.getResource("policyDisableConnectAsbu"));
		}
		try {
			edgePolicyDao.as_edge_policy_updateStatus_4node(plan.getId(), PolicyDeployStatus.CreateASBUPolicy_Creating);
			ASBUDestinationManager.getInstance().doASBUSettingsDeployment(plan);
			if (value) {
				PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Information, "",
						EdgeCMWebServiceMessages.getResource("policyEnableConnectAsbuSucc"));
			} else {
				PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Information, "",
						EdgeCMWebServiceMessages.getResource("policyDisableConnectAsbuSucc"));
			}
			return true;
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			if (e instanceof EdgeServiceFault) {
				errorMessage = WebServiceFaultMessageRetriever.getErrorMessage( DataFormatUtil.getServerLocale(),((EdgeServiceFault) e).getFaultInfo());
			}
			edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.DeployAsbuPolicyFailed);
			edgePolicyDao.as_edge_policy_updateStatus_4node(plan.getId(), PolicyDeployStatus.CreateASBUPolicy_Failed);
			edgePolicyDao.as_edge_policy_setDeployErrorMessage(plan.getId(), errorMessage);
			//doASBUSettingsDeployment has ASBU's ActivityLog 
			//PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Error, "", errorMessage);
			logger.error("PlanPauseResumeService.enableAsbuPolicy(UnifiedPlan) failed, error message = " + errorMessage, e);
			return false;
		}
	}
	
	public void enablePolicies(final boolean value, List<Integer> policyIdList)
			throws EdgeServiceFault {
		logger.info("[PlanPauseResumeService] Start to " + (value ? "Enable " : "Disable ") + " plans: "+policyIdList.toString());
		if(value){
			PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Information, "",
					EdgeCMWebServiceMessages.getResource("policyEnableStart"));
		}else {
			PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Information, "",
					EdgeCMWebServiceMessages.getResource("policyDisableStart"));
		}
		List<UnifiedPolicy> plans = getValidPlans(policyIdList, value);
		for (final UnifiedPolicy plan : plans) {
			logger.info("[PlanPauseResumeService] Start to " + (value ? "Enable " : "Disable ") + " plan: "+plan.getName());
			edgePolicyDao.as_edge_policy_enable_by_plan_id(plan.getId(),value?PlanEnableStatus.Enable:PlanEnableStatus.Disable);
			service.submit(new Runnable() {
				
				@Override
				public void run() {
					try {
						logger.info("[PlanPauseResumeService] try to lock enable/disable block for plan: "+plan.getName());
						lock.lock();
						enablePlan(value, plan);
					} finally{
						lock.unlock();
						logger.info("[PlanPauseResumeService] try to unlock enable/disable block for plan: "+plan.getName());
					}
				}
				
			});
		}
	}

	private void enablePlan(boolean value, UnifiedPolicy plan) {
		try {
			logger.info("[PlanPauseResumeService] Begin to "+(value?"enable ":"disable")+" plan: " + plan.getName());
			int deployReason = value?PolicyDeployReasons.EnablePlan:PolicyDeployReasons.DisablePlan;
			int enableStatus = value?PlanEnableStatus.Enable.getValue():PlanEnableStatus.Disable.getValue();
			edgePolicyDao.as_edge_policy_enable_nodes_by_plan_id(plan.getId(),deployReason,enableStatus);
			plan.setEnable(value);
			
			if (plan.getArchiveToTapeSettingsWrapperList()!=null && !plan.getArchiveToTapeSettingsWrapperList().isEmpty())
			{
				if(!enableAsbuPolicy(value, plan))
					return;
			}
			
			if(!enableRpsPolicy(value, plan))
				return;	
			
			List<EdgeHostPolicyMap> maps = new ArrayList<EdgeHostPolicyMap>();
			edgePolicyDao.as_edge_plan_getDeployList(plan.getId(),-1, maps);
			if (maps.isEmpty() || !PlanRedeployService.getInstance().hasDeployTask(plan)) {
				if (value) {
					PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Information, "",
							EdgeCMWebServiceMessages.getResource("policyEnableSucc"));
				} else {
					PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Information, "",
							EdgeCMWebServiceMessages.getResource("policyDisableSucc"));
				}
				edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.DeploySuccess);
				
				if(!PlanRedeployService.getInstance().hasDeployTask(plan)){
					edgePolicyDao.as_edge_policy_updateStatus_4node(plan.getId(), PolicyDeployStatus.DeployedSuccessfully);
				}
				return;
			}
			edgePolicyDao.redeployPolicy(PolicyTypes.Unified, plan.getId(),deployReason);
			PolicyManagementServiceImpl.getInstance().getPolicyDeploymentScheduler().doDeploymentNowByPlanId(plan.getId());
			logger.info("[PlanPauseResumeService] Finish to submit the "+(value?"enable ":"disable ")+" command for plan" + plan.getName());
		} catch (Exception e) {
			logger.error( "[PlanPauseResumeService] "+(value?"enable":"disable") + " plan"+ plan.getName()+"failed.", e );
		}
	}
	
	public List<UnifiedPolicy> getValidPlans(List<Integer> planIds) throws EdgeServiceFault {
		return getValidPlans(planIds,null);
	}
	
	public List<UnifiedPolicy> getValidPlans(List<Integer> planIds, Boolean enable) throws EdgeServiceFault {
		List<UnifiedPolicy> plans = new ArrayList<UnifiedPolicy>();
		
		for (Integer policyId : planIds) {
			List<EdgePolicy> policies = new ArrayList<EdgePolicy>();
			edgePolicyDao.as_edge_policy_list(policyId, 0, policies);
			List<EdgeHostPolicyMap> policyMaps = new ArrayList<EdgeHostPolicyMap>();
			edgePolicyDao.as_edge_plan_getDeployList(policyId, -1, policyMaps);
			if (policies.isEmpty()) {
				if(enable == null){
					logger.error("policyId: "+policyId + " is not valid, there is no plan for this id.");
				}else {
					logger.error("[PlanPauseResumeService] policyId: "+policyId + " is not valid, there is no plan for this id.");
				}
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
				if(enable != null){
					if(enable){
						PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Warning, "",
								EdgeCMWebServiceMessages.getResource("policySkipResume"));
						logger.error("[PlanPauseResumeService] policy: "+policyId +" name is :" +policy.getName()+ 
								" is in progress or it have d2d installing, so cannot resume it, the status is: "+policy.getStatus());	
					}else {
						PolicyManagementServiceImpl.getInstance().writeActivityLog(Severity.Warning, "",
								EdgeCMWebServiceMessages.getResource("policySkipPause"));
						logger.error("[PlanPauseResumeService] policy: "+policyId +" name is :" +policy.getName()+ 
								" is in progress or it have d2d installing, so cannot pause it, the status is: "+policy.getStatus());	
					}
				}
				continue;
			}
			plans.add(PolicyManagementServiceImpl.getInstance().loadUnifiedPolicyById(policyId));
		}
		
		return plans;
	}
}
