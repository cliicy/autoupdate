package com.ca.arcflash.webservice.edge.datasync.job;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dstatus.D2DStatusServiceImpl;
import com.ca.arcflash.webservice.edge.d2dstatus.SyncD2DStatusService;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;

public class VSphereBackupJobSyncMonitor extends AbstractBackupJobSyncMonitor {
	
	private static Logger logger = Logger.getLogger(VSphereBackupJobSyncMonitor.class);
	private static VSphereBackupJobSyncMonitor instance = new VSphereBackupJobSyncMonitor();
	
	private EdgeWebServiceCache<IEdgeD2DService> serviceCache = new EdgeWebServiceCache<IEdgeD2DService>(ApplicationType.vShpereManager);
	
	public static VSphereBackupJobSyncMonitor getInstance() {
		return instance;
	}

	private VSphereBackupJobSyncMonitor() {
	}
	
	@Override
	protected void doInitSync() throws SOAPFaultException, WebServiceException, Exception {
		SyncD2DStatusService.getInstance().syncVSphereStatusAll();
	}

	@Override
	protected boolean doSync() {
		IEdgeD2DService service = serviceCache.getService(IEdgeD2DService.class);
		if (service == null) {
			return false;
		}
		
		JobMonitor jobMonitor = convertJJobMonitor2JobMonitor(getExecutingJobMonitor(), getExecutingJobId());
		
		if (jobMonitor.getJobPhase() == Constants.JobExitPhase || jobMonitor.getJobPhase() == Constants.BackupJob_Phase_PROC_EXIT) {
			D2DStatusInfo summary = D2DStatusServiceImpl.getInstance().getVSphereVMStatusInfo(getExecutingUuid());
			jobMonitor.setD2DStatusInfo(summary);
			logger.info("job is to exit, send the last backup status info to edge.");
		}
		
		int result;
		
		try {
			result = service.syncBackupJobsStatus(getExecutingUuid(), jobMonitor);
		} catch (Exception e) {
			logSyncErrorMessage("doSync vSphere backup job status failed, error message = " + e.getMessage(), e);
			serviceCache.clear();
			return false;
		}
		
		if (result != 0) {
			logger.warn("VSphereSyncJobStatus failed, return code = " + result);
		}
		
		return true;
	}
	
	private JobMonitor convertJJobMonitor2JobMonitor(JJobMonitor dataForSync, long shrmemid) {
		JobMonitor jobMonitor = new JobMonitor();
		jobMonitor.setBackupStartTime(dataForSync.getUlBackupStartTime());
		jobMonitor.setCompressLevel(dataForSync.getUlCompressLevel());
		jobMonitor.setCtBKJobName(dataForSync.getCtBKJobName());
		jobMonitor.setCtBKStartTime(dataForSync.getCtBKStartTime());
		jobMonitor.setCtCurCatVol(dataForSync.getCtCurCatVol());
		jobMonitor.setCtDWBKJobID(dataForSync.getCtDWBKJobID());
		jobMonitor.setCurrentProcessDiskName(dataForSync.getWszDiskName());
		jobMonitor.setCurVolMntPoint(dataForSync.getWzCurVolMntPoint());
		jobMonitor.setDwBKSessNum(dataForSync.getDwBKSessNum());
		jobMonitor.setElapsedTime(dataForSync.getUlElapsedTime());
		jobMonitor.setEncInfoStatus(dataForSync.getUlEncInfoStatus());
		jobMonitor.setEstimateBytesDisk(dataForSync.getUlEstBytesDisk());
		jobMonitor.setEstimateBytesJob(dataForSync.getUlEstBytesJob());
		jobMonitor.setFlags(dataForSync.getUlFlags());
		jobMonitor.setJobId(shrmemid);
		jobMonitor.setJobMethod(dataForSync.getUlJobMethod());
		jobMonitor.setJobPhase(dataForSync.getUlJobPhase());
		jobMonitor.setJobStatus(dataForSync.getUlJobStatus());
		jobMonitor.setJobType(dataForSync.getUlJobType());
		jobMonitor.setnProgramCPU(dataForSync.getnProgramCPU());
		jobMonitor.setnReadSpeed(dataForSync.getnReadSpeed());
		jobMonitor.setnSystemCPU(dataForSync.getnSystemCPU());
		jobMonitor.setnSystemReadSpeed(dataForSync.getnSystemReadSpeed());
		jobMonitor.setnSystemWriteSpeed(dataForSync.getnSystemWriteSpeed());
		jobMonitor.setnWriteSpeed(dataForSync.getnWriteSpeed());
		jobMonitor.setProductType(dataForSync.getProductType());
		jobMonitor.setSessionID(dataForSync.getUlSessionID());
		jobMonitor.setThrottling(dataForSync.getUlThrottling());
		jobMonitor.setTotalSizeRead(dataForSync.getUlTotalSizeRead());
		jobMonitor.setTotalSizeWritten(dataForSync.getUlTotalSizeWritten());
		jobMonitor.setTransferBytesDisk(dataForSync.getUlXferBytesDisk());
		jobMonitor.setTransferBytesJob(dataForSync.getUlXferBytesJob());
		jobMonitor.setTransferMode(dataForSync.getTransferMode());
		jobMonitor.setUlMergedSessions(dataForSync.getUlMergedSession());
		jobMonitor.setUlProcessedFolder(dataForSync.getUlProcessedFolder());
		jobMonitor.setUlTotalFolder(dataForSync.getUlTotalFolder());
		jobMonitor.setUlTotalMegedSessions(dataForSync.getUlTotalMergedSessions());
		jobMonitor.setVmInstanceUUID(dataForSync.getVmInstanceUUID());
		jobMonitor.setVolMethod(dataForSync.getUlVolMethod());
		jobMonitor.setWszEDB(dataForSync.getWszEDB());
		jobMonitor.setWszMailFolder(dataForSync.getWszMailFolder());
		jobMonitor.setWzBKBackupDest(dataForSync.getWzBKBackupDest());
		jobMonitor.setWzBKDestPassword(dataForSync.getWzBKDestPassword());
		jobMonitor.setWzBKDestUsrName(dataForSync.getWzBKDestUsrName());
		jobMonitor.setVmHostName(dataForSync.getVmHostName());
		jobMonitor.setJobSubStatus(dataForSync.getJobSubStatus());
		return jobMonitor;
	}

}
