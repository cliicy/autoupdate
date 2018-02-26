package com.ca.arcflash.webservice.edge.datasync.job;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dstatus.D2DStatusServiceImpl;
import com.ca.arcflash.webservice.edge.d2dstatus.SyncD2DStatusService;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.edge.data.jobstatus.JobStatus2Edge;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;

public class D2DBackupJobSyncMonitor extends AbstractBackupJobSyncMonitor {
	
	private static Logger logger = Logger.getLogger(D2DBackupJobSyncMonitor.class);
	private static D2DBackupJobSyncMonitor instance = new D2DBackupJobSyncMonitor();
	
	private EdgeWebServiceCache<IEdgeD2DService> serviceCache = new EdgeWebServiceCache<IEdgeD2DService>(ApplicationType.CentralManagement);
	
	public static D2DBackupJobSyncMonitor getInstance() {
		return instance;
	}
	
	private D2DBackupJobSyncMonitor() {
	}

	@Override
	protected void doInitSync() throws SOAPFaultException, WebServiceException, Exception {
		SyncD2DStatusService.getInstance().syncD2DStatus();
	}

	@Override
	protected boolean doSync() {
		IEdgeD2DService service = serviceCache.getService(IEdgeD2DService.class);
		if (service == null) {
			return false;
		}
		
		JJobMonitor monitor = getExecutingJobMonitor();
		if (monitor.getUlJobPhase() == Constants.JobExitPhase || monitor.getUlJobPhase() == Constants.BackupJob_Phase_PROC_EXIT) {
			logger.info("job is to exit, send the last backup status info to edge.");
		}
		
		String xmlContent = getXMLContent(monitor, getExecutingJobId());
		if (xmlContent == null) {
			return false;
		}
		
		int result;
		
		try {
			result = service.D2DSyncJobStatus(xmlContent, CommonService.getInstance().getNodeUUID());
		} catch (Exception e) {
			logSyncErrorMessage("doSync d2d job status failed, error message = " + e.getMessage(), e);
			serviceCache.clear();
			return false;
		}
		
		if (result == 1) {
			logger.warn("D2DSyncJobStatus - XML parser failed!");
		} else if (result == 2) {
			logger.warn("D2DSyncJobStatus - SQL operation failed!");
		} else if (result != 0) {
			logger.warn("D2DSyncJobStatus - Other error, return code = " + result + "!");
		}
		
		return true;
	}
	
	private String getXMLContent(JJobMonitor JM, long jobId) {
		StringWriter st = new StringWriter();

        JobStatus2Edge jobStatus = new JobStatus2Edge();

		JAXBContext jaxbContext;
		try {
			jobStatus.setJobId(jobId);
			jobStatus.setStartTime(JM.getUlBackupStartTime());
			jobStatus.setCurProcessDiskName(JM.getWszDiskName());
			jobStatus.setEstimateBytesDisk(JM.getUlEstBytesDisk());
			jobStatus.setEstimateBytesJob(JM.getUlEstBytesJob());
			jobStatus.setFlags(JM.getUlFlags());
			jobStatus.setJobMethod(JM.getUlJobMethod());
			jobStatus.setJobPhase(JM.getUlJobPhase());
			jobStatus.setJobStatus(JM.getUlJobStatus());
			jobStatus.setJobType(JM.getUlJobType());
			jobStatus.setSessionID(JM.getUlSessionID());
			jobStatus.setTransferBytesDisk(JM.getUlXferBytesDisk());
			jobStatus.setTransferBytesJob(JM.getUlXferBytesJob());
			jobStatus.setElapsedTime(JM.getUlElapsedTime());
			jobStatus.setVolMethod(JM.getUlVolMethod());

		    jobStatus.setReadSpeed(JM.getnReadSpeed());
		    jobStatus.setWriteSpeed(JM.getnWriteSpeed());
		    jobStatus.setTotalSizeRead(JM.getUlTotalSizeRead());
		    jobStatus.setTotalSizeWritten(JM.getUlTotalSizeWritten());
		    jobStatus.setUlMergedSession(JM.getUlMergedSession());
		    jobStatus.setUlProcessedFolder(JM.getUlProcessedFolder());
		    jobStatus.setUlTotalFolder(JM.getUlTotalFolder());
		    jobStatus.setUlTotalMergedSessions(JM.getUlTotalMergedSessions());
		    jobStatus.setThrottling(JM.getUlThrottling());
		    jobStatus.setCtCurCatVol(JM.getCtCurCatVol());
		    jobStatus.setCompressLevel(JM.getUlCompressLevel());
		    jobStatus.setEncInfoStatus(JM.getUlEncInfoStatus());
		    jobStatus.setCtBKStartTime(JM.getCtBKStartTime());
		    jobStatus.setCurVolMntPoint(JM.getWzCurVolMntPoint());
		    jobStatus.setWszMailFolder(JM.getWszMailFolder());
		    if (JM.getUlJobPhase() == Constants.JobExitPhase || JM.getUlJobPhase() == Constants.BackupJob_Phase_PROC_EXIT) {
				// sync the backup information summary to edge side							
				D2DStatusInfo summary = D2DStatusServiceImpl.getInstance().getD2DStatusInfo();
				jobStatus.setLastBackupJobStatus(summary.getLastBackupJobStatus().ordinal());
				jobStatus.setLastBackupTime(summary.getLastBackupStartTime().getTime());
				jobStatus.setLastBackupStatus(summary.getLastBackupStatus().ordinal());
				jobStatus.setLastBackupType(summary.getLastBackupType().ordinal());
				jobStatus.setRecoveryPointCount(summary.getRecoveryPointCount());
				jobStatus.setRecoveryPointMounted(summary.getRecoveryPointMounted());
				jobStatus.setRecoveryPointRetentionCount(summary.getRecoveryPointRetentionCount());
				jobStatus.setRecoveryPointStatus(summary.getRecoveryPointStatus().ordinal());
				jobStatus.setIsUseBackupSets(summary.getIsUseBackupSets() ? true : false );
				jobStatus.setDestinationEstimatedBackupCount(summary.getDestinationEstimatedBackupCount());
				jobStatus.setDestinationFreeSpace(summary.getDestinationFreeSpace());
				jobStatus.setDestinationPath(summary.getDestinationPath());
				jobStatus.setDestinationStatus(summary.getDestinationStatus().ordinal());
				jobStatus.setIsDestinationAccessible(summary.isDestinationAccessible() ? 1 : 0 );
				jobStatus.setOverallStatus(summary.getOverallStatus().ordinal());
				jobStatus.setIsBackupConfiged(summary.isBackupConfiged() ? 1 : 0 );
				jobStatus.setIsDriverInstalled(summary.isDriverInstalled() ? 1 : 0 );
				jobStatus.setIsRestarted(summary.isRestarted() ? 1 : 0 );
				jobStatus.setEstimatedValue(summary.getEstimatedValue().ordinal());
			}
		    			    
			jaxbContext = JAXBContext.newInstance("com.ca.arcflash.webservice.edge.data.jobstatus");
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.marshal(jobStatus, st);
		} catch (JAXBException e) {
			logger.error("Marshall D2D job status failed, error message = " + e.getMessage());
			return null;
		}

        return st.toString();
	}

}
