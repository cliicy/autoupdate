package com.ca.arcflash.webservice.edge.policymanagement;

import java.util.HashSet;
import java.util.Set;

public class PolicyDeploymentCache {
	
	private static final PolicyDeploymentCache instance = new PolicyDeploymentCache();
	
	private Set<String> cachedDeployingVMs;
	
	private PolicyDeploymentCache() {
		cachedDeployingVMs = new HashSet<String>();
	}
	
	public static PolicyDeploymentCache getInstance() {
		return instance;
	}
	
	public synchronized void cacheDeployingVM(String vmInstanceUuid) {
		if (!cachedDeployingVMs.contains(vmInstanceUuid)) {
			cachedDeployingVMs.add(vmInstanceUuid);
		}
	}
	
	public synchronized void clearCachedDeployingVM(String vmInstanceUuid) {
		if (cachedDeployingVMs.contains(vmInstanceUuid)) {
			cachedDeployingVMs.remove(vmInstanceUuid);
		}
	}
	
	public synchronized boolean isDeployingVM(String vmInstanceUuid) {
		return cachedDeployingVMs.contains(vmInstanceUuid);
	}

}
