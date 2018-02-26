package com.ca.arcserve.edge.app.base.webservice.policymanagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.HostInfoCache;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployFlags;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanEnableStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;

public enum ProtectedResourcePlanAssigner {

	Node(ProtectedResourceType.node) {
		@Override
		protected List<Integer> getNodeIds(int protectedResourceId) {
			return Arrays.asList(protectedResourceId);
		}
	},
	ESX(ProtectedResourceType.group_esx) {
		@Override
		protected List<Integer> getNodeIds(int protectedResourceId) {
			IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
			List<EdgeHost> hostList = new ArrayList<EdgeHost>();
			esxDao.as_edge_vsphere_entity_map_getVappHostList_By_EsxGroup(protectedResourceId, hostList);
			List<Integer> vAppHostList = new ArrayList<Integer>();
			for (EdgeHost host : hostList) {
				vAppHostList.add(host.getRhostid());
			}
			return vAppHostList;
		}
	},
	HyperV(ProtectedResourceType.group_hyperv) {
		@Override
		protected List<Integer> getNodeIds(int protectedResourceId) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	Customer(ProtectedResourceType.group_customer) {
		@Override
		protected List<Integer> getNodeIds(int protectedResourceId) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	private ProtectedResourceType type;
	
	private IEdgePolicyDao edgePolicyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	private IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private static final Logger logger = Logger.getLogger(ProtectedResourcePlanAssigner.class);
	private ActivityLogServiceImpl logService = new ActivityLogServiceImpl();
	
	private ProtectedResourcePlanAssigner(ProtectedResourceType type) {
		this.type = type;
	}
	
	public static ProtectedResourcePlanAssigner create(ProtectedResourceType type) {
		for (ProtectedResourcePlanAssigner assigner : ProtectedResourcePlanAssigner.values()) {
			if (assigner.type == type) {
				return assigner;
			}
		}

		throw new RuntimeException("Not supported ProtectedResourceType: " + type);
	}
	
	protected abstract List<Integer> getNodeIds(int protectedResourceId);

	public int assign(UnifiedPolicy plan, int protectedResourceId, int deployFlag) throws EdgeServiceFault {
		if (type.isGroup()) {
			edgePolicyDao.as_edge_plan_group_map_update(type.ordinal(), protectedResourceId, plan.getId());
		}

		int count = 0;

		List<Integer> nodeIds = getNodeIds(protectedResourceId);
		if (nodeIds == null || nodeIds.isEmpty()) {
			return count;
		}

		for (Integer nodeId : nodeIds) {
			assignSingle(plan, nodeId, deployFlag);
			count++;
		}

		return count;
	}
	
	private void assignSingle(UnifiedPolicy plan, int nodeId, int deployFlag) throws EdgeServiceFault {
		List<EdgeHost> hosts = new LinkedList<EdgeHost>();
		hostMgrDao.as_edge_host_list(nodeId, 1, hosts);
		if (hosts.isEmpty()) {
			return;
		}
		
		int planId = plan.getId();
		int assignedPlanId = getNodeAssignedPlanId(nodeId);
		if (assignedPlanId == planId) {
			return;
		}
		
		UnifiedPolicy assignedPlan = null;
		if (assignedPlanId > 0) {
			assignedPlan = PolicyManagementServiceImpl.getInstance().loadUnifiedPolicyById(assignedPlanId);
			deployFlag = addTaskDeleteDeployFlag(assignedPlan, plan, deployFlag);
		}
		
		if ((deployFlag & PolicyDeployFlags.VMBackupTaskDeleted) != 0) {
			PolicyManagementServiceImpl.getInstance().undeployHBBUPlanByHostIdIgnoreException(assignedPlan.getId(), nodeId);
		}
		
		edgePolicyDao.assignPolicy(nodeId, PolicyTypes.Unified, planId, PolicyDeployStatus.ToBeDeployed,(type.isGroup()?1:0));
		
		//log
		EdgeHost hostInfo = HostInfoCache.getInstance().getHostInfo(nodeId);
		logger.info("[ProtectedResourcePlanAssigner]assignSingle()- Assign the plan "+plan.getName()+" to the node "+hostInfo.getRhostname());
		String message = EdgeCMWebServiceMessages.getMessage("assignPlanToNode",plan.getName(),hostInfo.getRhostname());
		generateLog(Severity.Information, nodeId, hostInfo.getRhostname(), message, Module.PolicyManagement);
		
		if (deployFlag != 0) {
			edgePolicyDao.as_edge_policy_updateMapStatus(planId, nodeId, PolicyDeployReasons.PolicyAssigned, PolicyDeployStatus.ToBeDeployed, deployFlag, 
					plan.isEnable() ? PlanEnableStatus.Enable.getValue() : PlanEnableStatus.Disable.getValue());
		}
		
		if (assignedPlan != null) {
			PolicyManagementServiceImpl.getInstance().updateOldPlanStatus(assignedPlan);
		}
	}
	
	/**
	 * Get the assigned plan id for the protected resource id.
	 * @param protectedResourceId protected resource id.
	 * @return Plan id if assigned. Otherwise, 0.
	 */
	public int getAssignedPlanId(int protectedResourceId) {
		switch (type) {
		case node: return getNodeAssignedPlanId(protectedResourceId);
		default: return getGroupAssignedPlanId(protectedResourceId);
		}
	}

	private int getNodeAssignedPlanId(int nodeId) {
		List<EdgeHostPolicyMap> maps = new LinkedList<EdgeHostPolicyMap>();
		edgePolicyDao.getHostPolicyMap(nodeId, PolicyTypes.Unified, maps);
		if (maps.isEmpty()) {
			return 0;
		}

		EdgeHostPolicyMap map = maps.get(0);
		if (map.getDeployReason() == PolicyDeployReasons.PolicyUnassigned) {
			return 0;
		}

		return map.getPolicyId();
	}
	
	private int getGroupAssignedPlanId(int groupId) {
		int[] planIds = new int[1];
		edgePolicyDao.as_edge_plan_group_map_getPlanByGroup(groupId,type.ordinal(), planIds);
		return planIds[0];
	}
	
	private static int addTaskDeleteDeployFlag(UnifiedPolicy assignedPlan, UnifiedPolicy newPlan, int deployFlag) {
		if (assignedPlan.getBackupConfiguration() != null && newPlan.getBackupConfiguration() == null) {
			deployFlag |= PolicyDeployFlags.BackupTaskDeleted;
		}

		if (assignedPlan.getVSphereBackupConfiguration() != null && newPlan.getVSphereBackupConfiguration() == null) {
			deployFlag |= PolicyDeployFlags.VMBackupTaskDeleted;
		}
		
		if (assignedPlan.getConversionConfiguration() != null && newPlan.getConversionConfiguration() == null) {
			deployFlag |= PolicyDeployFlags.ConversionTaskDeleted;
		}
		
		return deployFlag;
	}
	
	public int unassign(UnifiedPolicy plan, int protectedResourceId, int deployFlag) throws EdgeServiceFault {
		if (type.isGroup()) {
			edgePolicyDao.as_edge_plan_group_map_delete(type.ordinal(),protectedResourceId,plan.getId());
		}

		int count = 0;

		List<Integer> nodeIds = getNodeIds(protectedResourceId);
		if (nodeIds == null || nodeIds.isEmpty()) {
			return count;
		}

		deployFlag = addTaskDeleteDeployFlag(plan, deployFlag);
		for (Integer nodeId : nodeIds) {
			unassignSingle(plan, nodeId, deployFlag);
			count++;
		}

		return count;
	}
	
	private static int addTaskDeleteDeployFlag(UnifiedPolicy plan, int deployFlag) {
		if (plan.getBackupConfiguration() != null) {
			deployFlag |= PolicyDeployFlags.BackupTaskDeleted;
		}

		if (plan.getVSphereBackupConfiguration() != null) {
			deployFlag |= PolicyDeployFlags.VMBackupTaskDeleted;
		}
		
		if (plan.getConversionConfiguration() != null) {
			deployFlag |= PolicyDeployFlags.ConversionTaskDeleted;
		}
		
		return deployFlag;
	}
	
	private void unassignSingle(UnifiedPolicy plan, int nodeId, int deployFlag) throws EdgeServiceFault {
		edgePolicyDao.as_edge_policy_updateMapStatus(plan.getId(), nodeId, PolicyDeployReasons.PolicyUnassigned, PolicyDeployStatus.ToBeDeployed, deployFlag, 
				plan.isEnable() ? PlanEnableStatus.Enable.getValue() : PlanEnableStatus.Disable.getValue());
		//log
		EdgeHost hostInfo = HostInfoCache.getInstance().getHostInfo(nodeId);
		logger.info("[ProtectedResourcePlanAssigner]unassignSingle()- Unassign the plan "+plan.getName()+" from the node "+hostInfo.getRhostname());
		String message = EdgeCMWebServiceMessages.getMessage("unAssignPlanFromNode",plan.getName(),hostInfo.getRhostname());
		generateLog(Severity.Information, nodeId, hostInfo.getRhostname(), message, Module.PolicyManagement);
	}
	
	public int updateOnPlanChanged(UnifiedPolicy currentPlan, UnifiedPolicy updatedPlan, int protectedResourceId, int initDeployFlag) {
		int count = 0;

		List<Integer> nodeIds = getNodeIds(protectedResourceId);
		if (nodeIds == null || nodeIds.isEmpty()) {
			return count;
		}

		int deployFlag = addTaskDeleteDeployFlag(currentPlan, updatedPlan, initDeployFlag);
		for (Integer nodeId : nodeIds) {
			edgePolicyDao.as_edge_policy_updateMapStatus(updatedPlan.getId(), nodeId, PolicyDeployReasons.PolicyContentChanged, PolicyDeployStatus.ToBeDeployed, deployFlag, 
					updatedPlan.isEnable() ? PlanEnableStatus.Enable.getValue() : PlanEnableStatus.Disable.getValue());
			count++;
		}

		return count;
	}

	public void assign(int planId, List<Integer> nodeIds, boolean deployNow) throws EdgeServiceFault {
		if (nodeIds == null || nodeIds.isEmpty()) {
			return;
		}

		UnifiedPolicy plan = PolicyManagementServiceImpl.getInstance().loadUnifiedPolicyById(planId);
		for (Integer nodeId : nodeIds) {
			assignSingle(plan, nodeId, 0);
		}

		if (deployNow) {
			edgePolicyDao.as_edge_policy_updateStatus(planId, PlanStatus.Deploying);
			PolicyManagementServiceImpl.getInstance().getPolicyDeploymentScheduler().doDeploymentNowByPlanId(planId);
		}
	}

	public void unassign(int planId, List<Integer> nodeIds, boolean deployNow) throws EdgeServiceFault {
		if (nodeIds == null || nodeIds.isEmpty()) {
			return;
		}

		UnifiedPolicy plan = PolicyManagementServiceImpl.getInstance().loadUnifiedPolicyById(planId);
		int deployFlag = addTaskDeleteDeployFlag(plan, 0);
		for (Integer nodeId : nodeIds) {
			unassignSingle(plan, nodeId, deployFlag);
		}

		if (deployNow) {
			edgePolicyDao.as_edge_policy_updateStatus(plan.getId(), PlanStatus.Deploying);
			PolicyManagementServiceImpl.getInstance().getPolicyDeploymentScheduler().doDeploymentNowByPlanId(planId);
		}
	}
	
	private long generateLog(Severity severity, int nodeId, String nodeName , String message, Module module) {
		if(StringUtil.isEmptyOrNull(message))
			return 0;
		String name = (nodeName==null?"":nodeName);
		ActivityLog log = new ActivityLog();
		log.setNodeName(name);
		log.setHostId(nodeId);
		if(module != null){
			log.setModule(module);
		}
		log.setSeverity(severity);
		log.setTime(new Date());
		log.setMessage(message);
		
		try {
			return logService.addLog(log);
		} catch (Exception e) {
			logger.error("Error occurs during add activity log",e);
		}
		return 0;
	}
}
