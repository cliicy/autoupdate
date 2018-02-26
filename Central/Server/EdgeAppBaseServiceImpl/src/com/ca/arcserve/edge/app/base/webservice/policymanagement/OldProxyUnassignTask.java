package com.ca.arcserve.edge.app.base.webservice.policymanagement;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceIdentifier;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl.D2DConnectInfo;

public class OldProxyUnassignTask implements Runnable{
	private int oldProxyHostId;
	private List<ProtectedResourceIdentifier> protectedResourceIdentifiers;
	private List<Integer> nodeIds = null;
	
	private static PolicyManagementServiceImpl policyManagementService = new PolicyManagementServiceImpl();
	private static Logger logger = Logger.getLogger( OldProxyUnassignTask.class );
	
	public OldProxyUnassignTask(int oldProxyHostId, List<ProtectedResourceIdentifier> protectedResourceIdentifiers){
		this.oldProxyHostId = oldProxyHostId;
		this.protectedResourceIdentifiers = protectedResourceIdentifiers;
	}

	@Override
	public void run() {
		try {
			if(oldProxyHostId <= 0 || protectedResourceIdentifiers == null || protectedResourceIdentifiers.isEmpty())
				return;
			nodeIds = new ArrayList<Integer>();
			for (ProtectedResourceIdentifier identifier : protectedResourceIdentifiers) {
				nodeIds.add(identifier.getId());
			}
			logger.info("[OldProxyUnassignTask] Begin unassign nodes: "+nodeIds.toString()+" for proxy: "+oldProxyHostId);
			D2DConnectInfo proxyConnectInfo = policyManagementService.getD2DConnectInfo(oldProxyHostId);
			if(proxyConnectInfo == null){
				logger.error("[OldProxyUnassignTask] Failed unassign nodes: "+nodeIds.toString()+" for proxy: "+oldProxyHostId+" because the have no proxy for this proxyhostid.");
				return;
			}
			policyManagementService.removePolicyVM(proxyConnectInfo,
					PolicyTypes.VMBackup, false, nodeIds, false);
		} catch (Exception e) {
			logger.error("[OldProxyUnassignTask] Failed unassign nodes: "+nodeIds.toString()+" for proxy: "+oldProxyHostId);
		}
		logger.info("[OldProxyUnassignTask] Failed unassign nodes: "+nodeIds.toString()+" for proxy: "+oldProxyHostId);
	}

}
