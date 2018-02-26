package com.ca.arcflash.ha.utils;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.vmwaremanagerIntf.CAVMDetails;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.webservice.service.CommonService;

public class VMwareUploadManager {
	
	
	private static Logger logger = Logger.getLogger(VMwareUploadManager.class);

	
	public static boolean uploadToStorage(HARetryStrategy strategy,
			CAVirtualInfrastructureManager vmwareManager,
			CAVMDetails vmDetails, Map<String, String> fileUris, String afguid) {
		
		RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(afguid); 
		Iterator<String> iter = fileUris.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			String value = fileUris.get(key);
			for (int i = 0; i < strategy.getTimes(); ++i) {
				try {
					if (vmwareManager.putVMConfig(vmDetails.getVmName(),
							vmDetails.getUuid(), value, key)) {
						iter.remove();
						break;
					}

				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}

				synchronized (jobMonitor) {
					if (jobMonitor.isCurrentJobCancelled())
						return false;
				}

				try {
					Thread.sleep(strategy.getInterval() * 1000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return fileUris.size() == 0;
		
	}
	
	public static boolean deleteFromStorage(HARetryStrategy strategy,
			  								CAVirtualInfrastructureManager vmwareManager,
			  								CAVMDetails vmDetails,List<String> fileNames){
		boolean isDeleted =false;
		InputStream in = null;
		for (String name : fileNames) {
			for (int i = 0; i < strategy.getTimes(); i++) {
				try {
					in  = vmwareManager.getVMConfig(vmDetails.getVmName(), vmDetails.getUuid(), name);
					if(in != null){
						try {
							in.close();
						}catch(Exception e) {}
						
						isDeleted = vmwareManager.deleteVMConfig(vmDetails.getVmName(), vmDetails.getUuid(), name);
						if(isDeleted)
							break;
					}else {  // A null 'in' means the file doesn't exist, to retry more times is meaningless.
						break;
					}
				} catch (Exception e) {
				}
				try {
					Thread.sleep(strategy.getInterval() * 1000);
				} catch (InterruptedException e) {
				}
			}
		}
		return isDeleted;
		
	}
}
