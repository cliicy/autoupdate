package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PolicyDeployingCache {
	
	private static final PolicyDeployingCache instance = new PolicyDeployingCache();
	
	// <proxy_uuid, <vmId, vmInstanceUuid>>
	private Map<String, Map<Integer, String>> cachedDeployingVMs;
	
	private PolicyDeployingCache() {
		cachedDeployingVMs = new HashMap<String, Map<Integer,String>>();
	}
	
	public static PolicyDeployingCache getInstance() {
		return instance;
	}

	public synchronized void cache(String proxyUuid, int vmId, String vmInstanceUuid) {
		if (!cachedDeployingVMs.containsKey(proxyUuid)) {
			cachedDeployingVMs.put(proxyUuid, new HashMap<Integer, String>());
		}
		
		cachedDeployingVMs.get(proxyUuid).put(vmId, vmInstanceUuid);
	}
	
	public synchronized void clear(String proxyUuid, int vmId) {
		if (!cachedDeployingVMs.containsKey(proxyUuid)) {
			return;
		}
		
		cachedDeployingVMs.get(proxyUuid).remove(vmId);
		
		if (cachedDeployingVMs.get(proxyUuid).isEmpty()) {
			cachedDeployingVMs.remove(proxyUuid);
		}
	}
	
	public synchronized Set<String> getCachedVMInstanceUuids(String proxyUuid) {
		Set<String> vmInstanceUuids = new HashSet<String>();
		
		if (cachedDeployingVMs.containsKey(proxyUuid)) {
			vmInstanceUuids.addAll(cachedDeployingVMs.get(proxyUuid).values());
		}
		
		return vmInstanceUuids;
	}

}
