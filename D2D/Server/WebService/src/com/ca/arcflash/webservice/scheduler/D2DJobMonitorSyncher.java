package com.ca.arcflash.webservice.scheduler;

import java.util.Map;

import org.apache.log4j.Logger;

import com.ca.arcflash.instantvm.InstantVMConfig;
import com.ca.arcflash.service.common.FlashSyncher;
import com.ca.arcflash.webservice.data.ConvertJobMonitor;
import com.ca.arcflash.webservice.data.IVMJobMonitor;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.InstantVMService;
import com.ca.arcflash.webservice.service.JobMonitorService;
import com.ca.arcflash.webservice.service.MergeService;

public class D2DJobMonitorSyncher {
	private static final Logger logger = Logger.getLogger(D2DJobMonitorSyncher.class);
	private static String d2dServerUUID = null;
	public static void startSync() {
		logger.info("Start Job Monitor syncher thread");
		d2dServerUUID = CommonService.getInstance().getNodeUUID();
//		Thread syncThread = new Thread(new SyncThread(), "D2D Job Monitro Syncher");
//		syncThread.setDaemon(true);
//		syncThread.start();
		
		logger.debug("D2D Job Monitor Syncher");
		CommonService.getInstance().getUtilTheadPool().submit(new SyncThread());
	}
	
	private static class SyncThread implements Runnable {
		@Override
		public void run() {
			while(true) {
				try {
					reportD2DJobMonitor();
					//reportVSphereJobMonitor();
					//reportVSphereWaitingJobMonitor();
					reportD2DMerge();
					//reportVSphereMerge();
					reportConversionJobMonitor();
					reportInstantVMJobMonitor();
					//report job monitor every 1 second
					Thread.sleep(1000);
				}catch(InterruptedException e){						
					logger.debug("Interrupted in sync job monitor");
					break;
				}catch(Throwable t) {
					logger.error("Error in sync job monitor");
				}
			}
		}
	}
	
	private static void reportD2DJobMonitor() {
		JobMonitor[] d2dJMs = JobMonitorService.getInstance().getAllJobMonitors();
		if(d2dJMs != null) {
			for(JobMonitor jm : d2dJMs) {
				if(logger.isDebugEnabled()){
					logger.debug("report d2d job monitor" + jm);
				}
				if(!JobMonitorService.getInstance().isValidJobMonitor(jm))
					continue;
				if(FlashSyncher.getInstance().reportJobMonitor(jm, jm.getVmInstanceUUID(), 
					jm.getRpsPolicyUUID(), 
					d2dServerUUID) != 0){
					logger.error("Failed to report d2d job monitor" + jm);
				}
			}
		}
	}
	
	
	
	
	
	
	private static void reportD2DMerge(){
		MergeStatus ms = MergeService.getInstance().getMergeJobStatus();
		if(ms != null) {
			if(logger.isDebugEnabled()) {
				logger.debug("Report D2D merge status: " + ms);
			}
			if(ms.getJobMonitor() != null || ms.getStatus() == MergeStatus.Status.PAUSED_MANUALLY){
				if(FlashSyncher.getInstance().reportJobMonitor(ms, null, 
						null, d2dServerUUID) != 0){
					logger.error("Failed to report D2D merge status: " + ms);
				}
			}
		}
	}

	private static void reportConversionJobMonitor() {
		ConvertJobMonitor[] repJobMonitors = HAService.getInstance().getAllRepJobMonitors();
		if(repJobMonitors != null) {
			for(ConvertJobMonitor jm : repJobMonitors) {
				if(logger.isDebugEnabled()){
					logger.debug("report conversion job monitor: " + jm);
				}
				if(FlashSyncher.getInstance().reportJobMonitor(jm, jm.getVmInstanceUUID(), 
						jm.getRpsPolicyUUID(), d2dServerUUID) != 0){
					logger.error("Failed to report conversion job monitor: " + jm);
				}
			}
		}
	}

	private static void reportInstantVMJobMonitor() {
		Map<String, IVMJobMonitor> ivmJobMonitors = InstantVMService.getInstance().getAllIVMJobMonitors();
		if(ivmJobMonitors != null) {
			for(Map.Entry<String, IVMJobMonitor> entry : ivmJobMonitors.entrySet()) {
				IVMJobMonitor jm = entry.getValue();
				if(logger.isDebugEnabled()){
					logger.debug("report instant vm job monitor: " + jm);
				}
				if(FlashSyncher.getInstance().reportJobMonitor(jm, jm.getVmInstanceUUID(), 
						jm.getRpsPolicyUUID(), d2dServerUUID) != 0){
					logger.error("Failed to report instant vm job monitor: " + jm);
				}
			}
		}
	}
}
