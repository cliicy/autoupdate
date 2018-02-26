package com.ca.arcflash.webservice.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.ca.arcflash.service.common.FlashSyncher;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.JobMonitorService;
import com.ca.arcflash.webservice.service.VSphereMergeService;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.VSphereBackupJobTask;
import com.ca.arcflash.webservice.service.internal.VSphereJobQueue;

public class VSphereJobMonitorSyncher {
	
	private static final Logger logger = Logger.getLogger(VSphereJobMonitorSyncher.class);
	private static String d2dServerUUID = null;
	private static Hashtable<String, JobMonitor> monitorTable = new Hashtable<String, JobMonitor>();
	
	public static void startSync() {
		logger.info("Start vSphere Job Monitor syncher thread");
		d2dServerUUID = CommonService.getInstance().getNodeUUID();
		logger.debug("vSphere Job Monitor Syncher");
		CommonService.getInstance().getUtilTheadPool().submit(new SyncThread());
//		Thread syncThread = new Thread(new SyncThread(), "vSphere Job Monitro Syncher");
//		syncThread.setDaemon(true);
//		syncThread.start();
	}
	
	private static class SyncThread implements Runnable {
		@Override
		public void run() {
			while(true) {
				try {
					Set<String> reportedRunningJobs = reportVSphereJobMonitor();
					reportVSphereWaitingJobMonitor(reportedRunningJobs);
					reportVSphereMerge();
					Thread.sleep(1000);
				}catch(InterruptedException e){						
					logger.debug("Interrupted in sync job monitor");
					break;
				}catch(Throwable t) {
					logger.error("Error in sync job monitor", t);
				}
			}
		}
	}
	
	private static Set<String> reportVSphereJobMonitor() {
		Map<String, Map<String, Map<Long, JobMonitor>>>  vmJMs = JobMonitorService.getInstance().getVMJobMonitorMap();
		Set<String> reportedRunningJobs = new HashSet<String>();
		if(vmJMs != null){
			for(Map.Entry<String, Map<String, Map<Long, JobMonitor>>> vmJM : vmJMs.entrySet()) {
				String vmInstanceUUID = vmJM.getKey();
				Map<String, Map<Long, JobMonitor>> jms = vmJM.getValue();
				for(Map<Long, JobMonitor> idJMS : jms.values()){
					for(JobMonitor jm : idJMS.values()) {
						if(logger.isDebugEnabled()){
							logger.debug("report VM job monitor for " + 
									vmInstanceUUID + " JobMonitor is " + jm);
						}
						if(!JobMonitorService.getInstance().isValidJobMonitor(jm))
							continue;
						if(FlashSyncher.getInstance().reportJobMonitor(jm, vmInstanceUUID, 
								jm.getRpsPolicyUUID(), d2dServerUUID) != 0){
							logger.error("Failed to report VM job monitor for " + 
									vmInstanceUUID + " JobMonitor is " + jm);
						}
						reportedRunningJobs.add(vmInstanceUUID);
					}
				}
			}
		}
		return reportedRunningJobs;
	}

	private static void reportVSphereWaitingJobMonitor(Set<String> reportedRunningJobs) {
		ArrayList<Runnable>  waitQueue = VSphereJobQueue.getInstance().getWaitingJobQueue();
		monitorTable = VSphereJobQueue.getInstance().getWaitingJobTable();
		if(waitQueue == null || waitQueue.size() == 0)
			return;
		
		JobMonitor jobMonitor = null;
		for(Runnable runnable : waitQueue) {
			if(runnable instanceof VSphereBackupJobTask){
				String vmInstanceUUID = ((VSphereBackupJobTask) runnable).getVMInstanceUUID();
				String policyUUID = VSphereService.getInstance().getRpsPolicyUUID(vmInstanceUUID);
				String vmName = ((VSphereBackupJobTask) runnable).getVirtualMachine().getVmName();
				
				if (reportedRunningJobs.contains(vmInstanceUUID))
					continue;
				
				if(monitorTable.containsKey(vmInstanceUUID)) {
					jobMonitor = monitorTable.get(vmInstanceUUID);
				} else {
					jobMonitor = new JobMonitor();
					jobMonitor.setJobPhase(Constants.PHASE_BACKUP_PHASE_WAITING);
					jobMonitor.setJobType(Constants.AF_JOBTYPE_VM_BACKUP);
					jobMonitor.setD2dServerName(vmName);
					jobMonitor.setJobStatus(JobStatus.JOBSTATUS_WAITING);
					jobMonitor.setStartTime(new Date().getTime());
					jobMonitor.setVmInstanceUUID(vmInstanceUUID);
					//jobMonitor.setJobMethod(((VSphereBackupJobTask) runnable).getJobType());
					jobMonitor.setJobMethod(((VSphereBackupJobTask) runnable).getVMBackupJob().getJobType());
					monitorTable.put(vmInstanceUUID, jobMonitor);
				}
				
				if(logger.isDebugEnabled()) {
					logger.debug("report VM job monitor for " + 
							vmInstanceUUID + " JobMonitor is " + jobMonitor);
				}
				
				if(FlashSyncher.getInstance().reportJobMonitor(jobMonitor, vmInstanceUUID, 
						policyUUID, d2dServerUUID) != 0) {
					logger.error("Failed to report VM job monitor for " + 
							vmInstanceUUID + " JobMonitor is " + jobMonitor);
				}
			}
		}
	}
	
	private static void reportVSphereMerge() {
		MergeStatus[] vmMS = VSphereMergeService.getInstance().getMergeStatusList();
		if(vmMS != null) {
			for(MergeStatus vms : vmMS){
				String vmInstanceUUID = vms.getUUID();
				if(logger.isDebugEnabled()) {
					logger.debug("Report VM merge status for " 
							+ vmInstanceUUID + ":" + vms);
				}
				if(vms.getJobMonitor() != null || vms.getStatus() == MergeStatus.Status.PAUSED_MANUALLY){
					if(FlashSyncher.getInstance().reportJobMonitor(vms, vmInstanceUUID, 
							null, 
							d2dServerUUID) != 0){
						logger.error("Failed to report VM merge status for " 
								+ vmInstanceUUID + ":" + vms);
					}
				}
			}
		}
	}
}
