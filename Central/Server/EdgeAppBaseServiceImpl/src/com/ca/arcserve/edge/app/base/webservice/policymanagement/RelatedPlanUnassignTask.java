package com.ca.arcserve.edge.app.base.webservice.policymanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeVSphereProxyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ValuePair;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl.D2DConnectInfo;

/**
 * When plan create or update, the protected nodes may be
 * exist in other plan previously.
 * This class is used to unassign the original plan from these nodes.
 * 
 */
public class RelatedPlanUnassignTask implements Runnable{
	
	private List<ValuePair<Integer, Integer>> relatedPlanNodePairList;
	private int currentPlanId;
	private static PolicyManagementServiceImpl policyManagementService = new PolicyManagementServiceImpl();
	private static Logger logger = Logger.getLogger( RelatedPlanUnassignTask.class );
	
	public RelatedPlanUnassignTask(List<ValuePair<Integer, Integer>> relatedPlanNodePairList
			, int currentPlanId){
		this.relatedPlanNodePairList = relatedPlanNodePairList;
		this.currentPlanId = currentPlanId;
	}

	@Override
	public void run() {
		unassignOldPlanFromNodes();
	}
	
	public void unassignOldPlanFromNodes(){
		try {
			Map<Integer, List<Integer>> planNodesMap = convertPlanNodePairListToMap();
			if(planNodesMap==null || planNodesMap.isEmpty()){
				return;
			}
			if(currentPlanId != 0 && planNodesMap.containsKey(currentPlanId)){
				planNodesMap.remove(currentPlanId);
			}
			
			Iterator<Integer> planIterator = planNodesMap.keySet().iterator();
			while (planIterator.hasNext()) {
				int planId = planIterator.next();
				List<Integer> nodeIds = planNodesMap.get(planId);
				if(planId <= 0 || nodeIds == null || nodeIds.isEmpty()){
					continue;
				}
				logger.info("[RelatedPlanUnassignTask] Begin to unassign nodes: "+nodeIds.toString()+" for plan: "+planId);
				UnifiedPolicy plan = policyManagementService.loadUnifiedPolicyById(planId);
				if(plan.getVSphereBackupConfiguration() == null){
					logger.info("[RelatedPlanUnassignTask] Plan: "+planId+" is not HBBU plan, so skip it.");
					continue;
				}
				
				List<Integer> notUnassign = new ArrayList<Integer>();
				for(Integer vmId : nodeIds){
					//find proxy
					List<EdgeVSphereProxyInfo> list = new ArrayList<EdgeVSphereProxyInfo>();
					policyManagementService.checkNeedToDoUndeploy(vmId, list);
					
					if(list.size()>0 && plan.getVSphereBackupConfiguration().getvSphereProxy().getVSphereProxyHostID() == list.get(0).getId()){ //If the vm use same proxy in the two plans, then not do unassign operation
						notUnassign.add(vmId);
					}
				}
				nodeIds.removeAll(notUnassign);//If the vm use same proxy in the two plans, then not do unassign operation
				
				try {
					D2DConnectInfo proxyConnectInfo = policyManagementService.getD2DConnectInfo(
							plan.getVSphereBackupConfiguration().getvSphereProxy().getVSphereProxyHostID());
					if(proxyConnectInfo == null){
						logger.error("[RelatedPlanUnassignTask] Failed unassign nodes: "+nodeIds.toString()+" for plan: "+planId+" because the have no proxy.");
						continue;
					}
					policyManagementService.removePolicyVM(proxyConnectInfo,
							PolicyTypes.VMBackup, false, nodeIds, false);
				} catch (Exception e) {
					logger.error("[RelatedPlanUnassignTask] Failed unassign nodes: "+nodeIds.toString()+" for plan: "+planId,e);
					continue;
				}
				logger.info("[RelatedPlanUnassignTask] End to unassign nodes: "+nodeIds.toString()+" for plan: "+planId);
			}
		} catch (Exception e) {
			logger.error("[RelatedPlanUnassignTask]  unassign nodes from plan failed.",e);
		}
	}
	
	private Map<Integer, List<Integer>> convertPlanNodePairListToMap(){
		if(relatedPlanNodePairList == null || relatedPlanNodePairList.isEmpty())
			return null;
		Map<Integer, List<Integer>> planNodesMap = new HashMap<Integer, List<Integer>>();
		for (ValuePair<Integer, Integer> planNodePair : relatedPlanNodePairList) {
			int planId = planNodePair.getKey();
			int nodeId = planNodePair.getValue();
			
			if(planNodesMap.containsKey(planId)){
				planNodesMap.get(planId).add(nodeId);
			}else {
				List<Integer> nodeIds = new ArrayList<Integer>();
				nodeIds.add(nodeId);
				planNodesMap.put(planId, nodeIds);
			}
		}
		return planNodesMap;
	}
}
