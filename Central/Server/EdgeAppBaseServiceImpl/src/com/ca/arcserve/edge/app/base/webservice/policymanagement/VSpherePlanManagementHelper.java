package com.ca.arcserve.edge.app.base.webservice.policymanagement;

import com.ca.arcflash.webservice.data.vsphere.VSphereProxy;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.D2DRole;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;

public class VSpherePlanManagementHelper {
	public static int saveVMUnifiedPolicy(PolicyManagementServiceImpl serviceImpl, UnifiedPolicy policy, UnifiedPolicy oldPolicy, boolean planContentChanged) throws EdgeServiceFault{
		//check whether proxy changed
		if(policy.getId() > 0 && policy != null &&oldPolicy != null
				&& policy.getVSphereBackupConfiguration()!=null && oldPolicy.getVSphereBackupConfiguration() != null){//update
			int newProxyId = policy.getVSphereBackupConfiguration().getvSphereProxy().getVSphereProxyHostID();
			int oldProxyId = oldPolicy.getVSphereBackupConfiguration().getvSphereProxy().getVSphereProxyHostID();
			if(newProxyId > 0 && oldProxyId > 0 && newProxyId != oldProxyId){
				OldProxyUnassignTask task = new OldProxyUnassignTask(oldProxyId, oldPolicy.getProtectedResources());
				EdgeExecutors.getFixedPool().submit(task);
			}
		}
		
		int policyId = serviceImpl.saveUnifiedPolicy(policy, planContentChanged);
		VSphereProxy proxy = policy.getVSphereBackupConfiguration().getvSphereProxy();
		serviceImpl.getEdgePolicyDao().as_edge_policy_AddD2DRole(policyId, proxy.getVSphereProxyHostID(),
				D2DRole.WindowsProxy);
		return policyId;
	}
}