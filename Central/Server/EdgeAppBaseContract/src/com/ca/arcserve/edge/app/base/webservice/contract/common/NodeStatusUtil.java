package com.ca.arcserve.edge.app.base.webservice.contract.common;


import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.edge.data.d2dstatus.VMPowerStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeProtectionStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployStatus;

public class NodeStatusUtil {
	/**1 If node have no plan , warning , [The node is not protected by any plan]
	 * 2 If node have plan , but plan deploy failed , warning ,[The node is not protected by the current plan: xxx]
	 * 3 If plan succeed , but no job , warning , [No finished job]
	 * 4 If plan succeed , but last backup job or asbu job failed, [failed]
	 * 5 If plan succeed , but last backup job or asbu job is warning [warning]
	 * 6 If plan succeed , last backup job or asbu job succeed , other job failed or warning [warning]
	 * 7 If plan succeed , last backup job or asbu job succeed , other job success [successful]
	 * @param node
	 * @return
	 */
	public static NodeProtectionStatus getNodeProtectionStatus(Node node) {
		if (node.getRemoteDeployStatus() == DeployStatus.DEPLOY_SUCCESS_NEEDREBOOT.value()
				|| node.getRemoteDeployStatus() == DeployStatus.DEPLOY_COMPLETE_REBOOTTIMEOUT.value()) {
			return NodeProtectionStatus.ProtectedWithError;
		}
		String policyName = node.getPolicyName();
		if(StringUtil.isEmptyOrNull(policyName)){
			return NodeProtectionStatus.UnprotectedWithAnyPlan;
		}
		int deployStatus = node.getPolicyDeployStatus();
		if(0==deployStatus || deployStatus!=PolicyDeployStatus.DeployedSuccessfully){
			return NodeProtectionStatus.UnprotectedWithCurrentPlan;
		}
		if (!havePlanConfigJob(node.getLstJobHistory())) {
			return NodeProtectionStatus.ProtectedWithNoJob;
		}
		NodeProtectionStatus status = NodeProtectionStatus.ProtectedSuccessful;
		NodeProtectionStatus tempstatus = NodeProtectionStatus.ProtectedSuccessful;
		if(Utils.hasBackupTask(node.getPolicyContentFlag())){
			JobHistory item = getBackupJobHistory(node.getLstJobHistory());
			if(item==null || item.getJobStatus() == JobStatus.Finished || item.getJobStatus() == JobStatus.BackupJob_PROC_EXIT){
				status = NodeProtectionStatus.ProtectedSuccessful;
			}
			else if (item.getJobStatus()== JobStatus.Canceled || item.getJobStatus()==JobStatus.Waiting || item.getJobStatus()==JobStatus.Skipped
			|| item.getJobStatus()== JobStatus.Stop || item.getJobStatus()==JobStatus.Missed || item.getJobStatus()==JobStatus.Incomplete) {
				return NodeProtectionStatus.ProtectedWithWarning;
			}
			else {
				return NodeProtectionStatus.ProtectedWithError;
			}
		}
		if(Utils.hasArchive2TapeTask(node.getPolicyContentFlag())){
			JobHistory item = getArchive2TapeJobHistory(node.getLstJobHistory());
			if(item==null || item.getJobStatus() == JobStatus.Finished || item.getJobStatus() == JobStatus.BackupJob_PROC_EXIT){
				// continue to check job status that other type
			}
			else if (item.getJobStatus()== JobStatus.Canceled || item.getJobStatus()==JobStatus.Waiting || item.getJobStatus()==JobStatus.Skipped
			|| item.getJobStatus()== JobStatus.Stop || item.getJobStatus()==JobStatus.Missed || item.getJobStatus()==JobStatus.Incomplete) {
				return NodeProtectionStatus.ProtectedWithWarning;
			}
			else {
				return NodeProtectionStatus.ProtectedWithError;
			}
		}
		if(Utils.hasReplicationTask(node.getPolicyContentFlag())){
			JobHistory replicationOutJob = getReplicationJobHistory(node.getLstJobHistory() ,true);
			tempstatus = getStatusAccordingJobHistory(replicationOutJob);
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
			
			JobHistory replicationInJob = getReplicationJobHistory(node.getLstJobHistory() ,false);
			tempstatus = getStatusAccordingJobHistory(replicationInJob);
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
		}
		
		if(Utils.hasFileCopyTask(node.getPolicyContentFlag())){
			JobHistory filecopyJob_backup = getFileCopyJobHistory(node.getLstJobHistory());
			tempstatus = getStatusAccordingJobHistory(filecopyJob_backup);
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
		}
		
		if(Utils.hasFileArchiveTask(node.getPolicyContentFlag())){
			JobHistory fileArchiveJob_backup = getFileArchiveJobHistory(node.getLstJobHistory());
			tempstatus = getStatusAccordingJobHistory(fileArchiveJob_backup);
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
		}
		
		if(Utils.hasCopyRecoveryPointTask(node.getPolicyContentFlag())){
			JobHistory copyJob = getCopyRecovryPointJobHistory(node.getLstJobHistory());
			tempstatus = getStatusAccordingJobHistory(copyJob);
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
		}
		
		if(Utils.hasVSBTask(node.getPolicyContentFlag())){
			JobHistory vsbJob = getVSBJobHistory(node.getLstJobHistory());
			tempstatus = getStatusAccordingJobHistory(vsbJob);
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
			int vsbStatus = getVSBoverallStatus(node);
			if(vsbStatus == 2 || vsbStatus == 1)
				tempstatus = NodeProtectionStatus.ProtectedWithWarning;
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
		}
		
		if(Utils.enableFileSystemCatalog(node.getPolicyContentFlag())){
			JobHistory fileSystemCatalog = getCatalogJobHistory(node.getLstJobHistory());
			tempstatus = getStatusAccordingJobHistory(fileSystemCatalog);
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
		}
		
		if(Utils.enableGRTCatalog(node.getPolicyContentFlag())){
			JobHistory grtCatalogJob = getGRTCatalogJobHistory(node.getLstJobHistory());
			tempstatus = getStatusAccordingJobHistory(grtCatalogJob);
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
		}
		
//		List<JobHistory> otherJobHistories = getOtherErrorJob(node);//this api will filter catalog job if node disable catalog
//		if(otherJobHistories!=null && otherJobHistories.size()>0){
//			tempstatus = NodeProtectionStatus.ProtectedWithWarning;
//			if(status.ordinal() < tempstatus.ordinal())
//				status = tempstatus;
//		}
		
		return status;
	}
	
	private static boolean havePlanConfigJob(List<JobHistory> jobList){
		if(jobList == null || jobList.isEmpty())
			return false;
		for(JobHistory job : jobList){
			if(job.getJobType() == JobType.JOBTYPE_BACKUP
					|| job.getJobType() == JobType.JOBTYPE_VM_BACKUP
					|| job.getJobType() == JobType.JOBTYPE_RPS_REPLICATE
					|| job.getJobType() == JobType.JOBTYPE_RPS_REPLICATE_IN_BOUND
					|| job.getJobType() == JobType.JOBTYPE_CONVERSION
					|| job.getJobType() == JobType.JOBTYPE_RPS_CONVERSION
					|| job.getJobType() == JobType.JOBTYPE_FILECOPY_BACKUP
					|| job.getJobType() == JobType.JOBTYPE_FILECOPY_SOURCEDELETE
					|| job.getJobType() == JobType.JOBTYPE_COPY
					|| job.getJobType() == JobType.JOBTYPE_CATALOG_FS
					|| job.getJobType() == JobType.JOBTYPE_CATALOG_FS_ONDEMAND
					|| job.getJobType() == JobType.JOBTYPE_VM_CATALOG_FS
					|| job.getJobType() == JobType.JOBTYPE_VM_CATALOG_FS_ONDEMAND
					|| job.getJobType() == JobType.JOBTYPE_CATALOG_GRT)
				return true;
		}
		return false;
	}
	
	public static int getVSBoverallStatus(Node node) {
		D2DStatusInfo vsbStatusInfo = node.getVsbSatusInfo();
		boolean forRemoteNode = (Utils.hasRemoteVSBTask(node.getPolicyContentFlag()) || node.isCrossSiteVsb());
		int status = -1;
		if(vsbStatusInfo == null)
			return 0;
		int tempStatus = getStandbyStatus(vsbStatusInfo);
		if(tempStatus>status)
			status = tempStatus;
		tempStatus = getStandbyVMStatusForOwerAll(vsbStatusInfo, forRemoteNode);
		if(tempStatus>status)
			status = tempStatus;
		if (!forRemoteNode) {
			tempStatus = getVSBHeartBeatStatus(vsbStatusInfo);
			if (tempStatus > status)
				status = tempStatus;
		}
		return status;
	}
	
	public static Integer getStandbyStatus(D2DStatusInfo vsbStatusInfo) {
		int result = 1;//warning
		if(vsbStatusInfo !=null && vsbStatusInfo.getAutoOfflieCopyStatus()==ReplicationJobScript.AUTO_OFFLINE_COPY_ENABLED){
				result = 0;//ok
		}
		return result;
	}
	
	public static int getStandbyVMStatusForOwerAll(D2DStatusInfo vsbsStatusInfo, boolean forRemoteNode){
		//vm status depend on source status
		if(vsbsStatusInfo == null || vsbsStatusInfo.getVmPowerStatus()==null)
			return 1; //can't get vm status , warning
		VMPowerStatus vmPowerStatus = vsbsStatusInfo.getVmPowerStatus();
		// For remote node, we will show warning only when vm is powered on
		if (forRemoteNode) {
			return (vmPowerStatus == VMPowerStatus.power_on) ? 1 : 0;
		}
		int result = 1;
		int heartBeatState = vsbsStatusInfo.getHeartbeatStatus();
		if (heartBeatState == HeartBeatJobScript.STATE_REGISTERED){
			if(vmPowerStatus == VMPowerStatus.power_on)
				result = 1 ; // source running , vm running, warning
			else
				result = 0; // source running , vm no running , ok
		}else if (heartBeatState == HeartBeatJobScript.STATE_PAUSED) {
			if(vmPowerStatus == VMPowerStatus.power_on)
				result = 1;
			else
				result = 0;
		}
		else {
			if(vmPowerStatus == VMPowerStatus.power_on)
				result =1 ; //source down , vm running ,warning
			else
				result = 2; //source down , vm down , error
		}
		return result;
	}

	public static int getStandbyVMStatus(D2DStatusInfo vsbsStatusInfo) {
		if (vsbsStatusInfo == null || vsbsStatusInfo.getVmPowerStatus() == null)
			return 2;
		VMPowerStatus vmPowerStatus = vsbsStatusInfo.getVmPowerStatus();
		if (vmPowerStatus == VMPowerStatus.power_on)
			return 0;
		if(vmPowerStatus == VMPowerStatus.power_off)
			return 1;
		return 2;
	}

	public static int getVSBHeartBeatStatus(D2DStatusInfo vsbStatusInfo){
		if(vsbStatusInfo==null)
			return 1;
		int heartBeatState = vsbStatusInfo.getHeartbeatStatus();
		if (heartBeatState == HeartBeatJobScript.STATE_REGISTERED){
			return 0;
		}
		else {
			return 1;
		}
	}
	
	public static JobHistory getJobAccordingJobType(List<JobHistory> jobs , List<Long> jobTypes){
		if(jobs == null || jobs.isEmpty())
			return null;
		for (JobHistory item : jobs) {
			if (jobTypes.contains(item.getJobType())) {
				return item;
			}
		}
		return null;
	}
	
	public static JobHistory getBackupJobHistory(List<JobHistory> jobHistories) {
		if(jobHistories == null || jobHistories.isEmpty())
			return null;
		List<Long> backupTypes = new ArrayList<Long>();
		backupTypes.add(JobType.JOBTYPE_BACKUP);
		JobHistory bk = getJobAccordingJobType(jobHistories, backupTypes);
		backupTypes.clear();
		backupTypes.add(JobType.JOBTYPE_VM_BACKUP);
		JobHistory vmBk = getJobAccordingJobType(jobHistories, backupTypes);
		if(bk != null && vmBk != null){
			if(bk.getJobUTCStartDate().compareTo(vmBk.getJobUTCStartDate())>0)
				return bk;
			else 
				return vmBk;
		}
		else if(bk == null && vmBk!=null)
			return vmBk;
		else 
			return bk;
	}
	
	public static JobHistory getReplicationJobHistory(List<JobHistory> jobHistories , boolean isOut) {
		List<Long> replicationTypes = new ArrayList<Long>();
		if(isOut)
			replicationTypes.add(JobType.JOBTYPE_RPS_REPLICATE);
		else
			replicationTypes.add(JobType.JOBTYPE_RPS_REPLICATE_IN_BOUND);
		return getJobAccordingJobType(jobHistories, replicationTypes);
	}
	
	public static JobHistory getFileCopyJobHistory(List<JobHistory> jobHistories) {
		List<Long> fileCopyTypes = new ArrayList<Long>();
		fileCopyTypes.add(JobType.JOBTYPE_FILECOPY_BACKUP);
		return getJobAccordingJobType(jobHistories, fileCopyTypes);
	}
	
	public static JobHistory getFileArchiveJobHistory(List<JobHistory> jobHistories) {
		List<Long> fileCopyTypes = new ArrayList<Long>();
		fileCopyTypes.add(JobType.JOBTYPE_FILECOPY_SOURCEDELETE);
		return getJobAccordingJobType(jobHistories, fileCopyTypes);
	}
	
	public static JobHistory getCopyRecovryPointJobHistory(List<JobHistory> jobHistories) {
		List<Long>copyrcvpTypes = new ArrayList<Long>();
		copyrcvpTypes.add(JobType.JOBTYPE_COPY);
		return getJobAccordingJobType(jobHistories, copyrcvpTypes);
	}
	
	public static JobHistory getVSBJobHistory(List<JobHistory> jobHistories) {
		List<Long>copyrcvpTypes = new ArrayList<Long>();
		copyrcvpTypes.add(JobType.JOBTYPE_CONVERSION);
		copyrcvpTypes.add(JobType.JOBTYPE_RPS_CONVERSION);
		return getJobAccordingJobType(jobHistories, copyrcvpTypes);
	}
	
	public static JobHistory getCatalogJobHistory(List<JobHistory> jobHistories) {
		List<Long>catalogTypes = new ArrayList<Long>();
		catalogTypes.add(JobType.JOBTYPE_CATALOG_FS);
		catalogTypes.add(JobType.JOBTYPE_CATALOG_FS_ONDEMAND);
		catalogTypes.add(JobType.JOBTYPE_VM_CATALOG_FS);
		catalogTypes.add(JobType.JOBTYPE_VM_CATALOG_FS_ONDEMAND);
		return getJobAccordingJobType(jobHistories, catalogTypes);
	}
	
	public static JobHistory getGRTCatalogJobHistory(List<JobHistory> jobHistories) {
		List<Long>catalogTypes = new ArrayList<Long>();
		catalogTypes.add(JobType.JOBTYPE_CATALOG_GRT);
		return getJobAccordingJobType(jobHistories, catalogTypes);
	}	
	
	public static JobHistory getArchive2TapeJobHistory(List<JobHistory> jobHistories) {
		List<Long>catalogTypes = new ArrayList<Long>();
		catalogTypes.add(JobType.JOBTYPE_ARCHIVE_TO_TAPE);
		return getJobAccordingJobType(jobHistories, catalogTypes);
	}
	
//	public static List<JobHistory> getOtherErrorJob(Node node){
//		List<JobHistory> jobResults = new ArrayList<JobHistory>();
//		for (JobHistory item : node.getLstJobHistory()) {
//			if (isOtherJob(item ,Utils.enableFileSystemCatalog(node.getPolicyContentFlag()),Utils.enableGRTCatalog(node.getPolicyContentFlag()))
//					&&(item.getJobStatus()==JobStatus.Failed ||item.getJobStatus()==JobStatus.Crash ||item.getJobStatus()==JobStatus.LicenseFailed || item.getJobStatus()==JobStatus.Incomplete 
//					|| item.getJobStatus()==JobStatus.Skipped || item.getJobStatus()==JobStatus.Canceled ||item.getJobStatus()==JobStatus.Missed 
//					||item.getJobStatus()==JobStatus.Stop ||item.getJobStatus()==JobStatus.Waiting )) {
//				jobResults.add(item);
//			}
//		}
//		return jobResults;
//	}

//	public static boolean isOtherJob(JobHistory job ,boolean containFSCatalog , boolean containGRTCatalog){
//		if(job.getJobType() == JobType.JOBTYPE_BACKUP || job.getJobType() == JobType.JOBTYPE_VM_BACKUP
//				|| job.getJobType() == JobType.JOBTYPE_RPS_REPLICATE || job.getJobType() == JobType.JOBTYPE_RPS_REPLICATE_IN_BOUND
//				|| job.getJobType() == JobType.JOBTYPE_CONVERSION || job.getJobType() == JobType.JOBTYPE_RPS_CONVERSION
//				|| job.getJobType() == JobType.JOBTYPE_FILECOPY_BACKUP || job.getJobType() == JobType.JOBTYPE_COPY)
//			return false;
//		if(!containFSCatalog && 
//				(job.getJobType() == JobType.JOBTYPE_CATALOG_FS
//				||job.getJobType()==JobType.JOBTYPE_VM_CATALOG_FS
//				||job.getJobType()==JobType.JOBTYPE_CATALOG_FS_ONDEMAND
//				||job.getJobType()==JobType.JOBTYPE_VM_CATALOG_FS_ONDEMAND))
//			return false;
//		if(!containGRTCatalog && job.getJobType() == JobType.JOBTYPE_CATALOG_GRT)
//			return false;
//		return true;
//	}

	private static NodeProtectionStatus getStatusAccordingJobHistory(JobHistory jobHistory) {
		if(jobHistory == null)
			return NodeProtectionStatus.ProtectedSuccessful;
		switch (jobHistory.getJobStatus()) {
		case Finished:		
		case BackupJob_PROC_EXIT:
			return NodeProtectionStatus.ProtectedSuccessful;
		default:
			return NodeProtectionStatus.ProtectedWithWarning;
		}
	}
	
	//Just used for member vm of vapp, if used for all node please add some logic of virtual standby
	public static NodeProtectionStatus getNodeEntityProtectionStatus(NodeEntity node) {
		String policyName = node.getPlanSummary()==null ? "" : node.getPlanSummary().getName();
		if(StringUtil.isEmptyOrNull(policyName)){
			return NodeProtectionStatus.UnprotectedWithAnyPlan;
		}
		int deployStatus = node.getPlanSummary() == null ? 0 : node.getPlanSummary().getDeployStatus();
		if(0==deployStatus || deployStatus!=PolicyDeployStatus.DeployedSuccessfully){
			return NodeProtectionStatus.UnprotectedWithCurrentPlan;
		}
		List<JobHistory> latestJobHistory = node.getJobSummary() == null ? null:node.getJobSummary().getLatestJobHistories();
		if (!havePlanConfigJob(latestJobHistory)) {
			return NodeProtectionStatus.ProtectedWithNoJob;
		}
		NodeProtectionStatus status = NodeProtectionStatus.ProtectedSuccessful;
		NodeProtectionStatus tempstatus = NodeProtectionStatus.ProtectedSuccessful;
		int policyContentFlag = node.getPlanSummary() == null ? 0 : node.getPlanSummary().getContentFlag();
		if(Utils.hasBackupTask(policyContentFlag)){
			JobHistory item = getBackupJobHistory(latestJobHistory);
			if(item==null || item.getJobStatus() == JobStatus.Finished || item.getJobStatus() == JobStatus.BackupJob_PROC_EXIT){
				status = NodeProtectionStatus.ProtectedSuccessful;
			}
			else if (item.getJobStatus()== JobStatus.Canceled || item.getJobStatus()==JobStatus.Waiting || item.getJobStatus()==JobStatus.Skipped
			|| item.getJobStatus()== JobStatus.Stop || item.getJobStatus()==JobStatus.Missed || item.getJobStatus()==JobStatus.Incomplete) {
				return NodeProtectionStatus.ProtectedWithWarning;
			}
			else {
				return NodeProtectionStatus.ProtectedWithError;
			}
		}
		if(Utils.hasArchive2TapeTask(policyContentFlag)){
			JobHistory item = getArchive2TapeJobHistory(latestJobHistory);
			if(item==null || item.getJobStatus() == JobStatus.Finished || item.getJobStatus() == JobStatus.BackupJob_PROC_EXIT){
				// continue to check job status that other type
			}
			else if (item.getJobStatus()== JobStatus.Canceled || item.getJobStatus()==JobStatus.Waiting || item.getJobStatus()==JobStatus.Skipped
			|| item.getJobStatus()== JobStatus.Stop || item.getJobStatus()==JobStatus.Missed || item.getJobStatus()==JobStatus.Incomplete) {
				return NodeProtectionStatus.ProtectedWithWarning;
			}
			else {
				return NodeProtectionStatus.ProtectedWithError;
			}
		}
		if(Utils.hasReplicationTask(policyContentFlag)){
			JobHistory replicationOutJob = getReplicationJobHistory(latestJobHistory ,true);
			tempstatus = getStatusAccordingJobHistory(replicationOutJob);
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
			
			JobHistory replicationInJob = getReplicationJobHistory(latestJobHistory ,false);
			tempstatus = getStatusAccordingJobHistory(replicationInJob);
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
		}
		
		if(Utils.hasFileCopyTask(policyContentFlag)){
			JobHistory filecopyJob_backup = getFileCopyJobHistory(latestJobHistory);
			tempstatus = getStatusAccordingJobHistory(filecopyJob_backup);
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
		}
		
		if(Utils.hasFileArchiveTask(policyContentFlag)){
			JobHistory fileArchiveJob_backup = getFileArchiveJobHistory(latestJobHistory);
			tempstatus = getStatusAccordingJobHistory(fileArchiveJob_backup);
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
		}
		
		if(Utils.hasCopyRecoveryPointTask(policyContentFlag)){
			JobHistory copyJob = getCopyRecovryPointJobHistory(latestJobHistory);
			tempstatus = getStatusAccordingJobHistory(copyJob);
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
		}
		
//		if(Utils.hasVSBTask(policyContentFlag)){
//			JobHistory vsbJob = getVSBJobHistory(latestJobHistory);
//			tempstatus = getStatusAccordingJobHistory(vsbJob);
//			if(status.ordinal() < tempstatus.ordinal())
//				status = tempstatus;
//			int vsbStatus = getVSBoverallStatus(latestJobHistory);
//			if(vsbStatus == 2 || vsbStatus == 1)
//				tempstatus = NodeProtectionStatus.ProtectedWithWarning;
//			if(status.ordinal() < tempstatus.ordinal())
//				status = tempstatus;
//		}
		
		if(Utils.enableFileSystemCatalog(policyContentFlag)){
			JobHistory fileSystemCatalog = getCatalogJobHistory(latestJobHistory);
			tempstatus = getStatusAccordingJobHistory(fileSystemCatalog);
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
		}
		
		if(Utils.enableGRTCatalog(policyContentFlag)){
			JobHistory grtCatalogJob = getGRTCatalogJobHistory(latestJobHistory);
			tempstatus = getStatusAccordingJobHistory(grtCatalogJob);
			if(status.ordinal() < tempstatus.ordinal())
				status = tempstatus;
		}
		return status;
	}
}
