package com.ca.arcflash.webservice.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.vmwaremanager.CAVMwareVirtualInfrastructureManager;
import com.ca.arcflash.ha.vmwaremanager.InvalidLoginException;
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;

public class VMwareManagerCache {
	
	private static final Logger logger = Logger.getLogger(VMwareManagerCache.class);
	
	private Map<String, CAVMwareVirtualInfrastructureManager> cache;
	
	public VMwareManagerCache(){
		cache = new HashMap<String, CAVMwareVirtualInfrastructureManager>();
	}
	
	public int validate(VirtualCenter vc) {
		if (cache.containsKey(vc.getVcName())) {
			return VSphereService.VM_STATUS_ERROR_VC_OK;
		}
		
		CAVMwareVirtualInfrastructureManager vmwareOBJ = null;
		int retVal = VSphereService.VM_STATUS_ERROR_VC_OK;
		
		try {
			vmwareOBJ = new CAVMwareVirtualInfrastructureManager();
			retVal = vmwareOBJ.init(vc.getVcName(), vc.getUsername(), vc.getPassword(), vc.getProtocol(), true, vc.getPort());
			if (retVal == VSphereService.VM_STATUS_ERROR_VC_OK) {
				cache.put(vc.getVcName(), vmwareOBJ);
			}
		} catch (InvalidLoginException e) {
			logger.error("validateVC failed", e);
			retVal = VSphereService.VM_STATUS_ERROR_VC_CREDENTIAL_WRONG;
		} catch(Exception ex) {
			logger.error("validateVC failed", ex);
			retVal = VSphereService.VM_STATUS_ERROR_VC_CANNOT_CONNECT;
		}
		
		return retVal;
	}
	
	public CAVMwareVirtualInfrastructureManager get(VirtualCenter vc) {
		if (cache.containsKey(vc.getVcName())) {
			return cache.get(vc.getVcName());
		}
		
		return validate(vc) == VSphereService.VM_STATUS_ERROR_VC_OK ? cache.get(vc.getVcName()) : null;
	}
	
	public void close() {
		for (CAVMwareVirtualInfrastructureManager manager : cache.values()) {
			try {
				manager.close();
			} catch (Exception e) {
				logger.debug("Close esx server failed");
			}
		}
		
		cache.clear();
	}

}
