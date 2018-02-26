package com.ca.arcflash.webservice.edge.datasync;

import java.io.StringWriter;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.d2dstatus.D2DStatusServiceImpl;
import com.ca.arcflash.webservice.edge.d2dstatus.SyncD2DStatusService;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.edge.data.jobstatus.JobStatus2Edge;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.scheduler.BaseJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.internal.VSphereJobContext;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public class JobStatusSynchronization {
	private static final Logger logger = Logger.getLogger(BaseJob.class);
	private static JobStatusSynchronization instance = null;
	
	private JobStatusSynchronization() {
		
	}
	
	public synchronized static JobStatusSynchronization getInstance() {
		if(instance == null)
			instance = new JobStatusSynchronization();
		
		return instance;
	}
	
	private class JobMonitorDataForSync extends JJobMonitor {
		private long		 jobId;
		private boolean		 stopUpdateFlag;
		private boolean		 isReadyForUpdateFlag;
		
		public void setJobId(long value) {
			jobId = value;
		}
		
		public long getJobId() {
			return jobId;
		}
		
		public void setReadyForUpdate() {
			isReadyForUpdateFlag = true;
		}
		
		public void clearReadyForUpdate() {
			isReadyForUpdateFlag = false;
		}
		
		public boolean isReadyForUpdate() {
			return isReadyForUpdateFlag;
		}
		
		public void setStopUpdate() {
			stopUpdateFlag = true;
		}
		
		public void clearStopUpdate() {
			stopUpdateFlag = false;
		}
		
		public boolean isStopUpdate() {
			return stopUpdateFlag;
		}
	}
	
	private static Object 	updateLock = new Object();
	private static Object 	updateLock4VSphere = new Object();
	private static Map<String, JobMonitorDataForSync> updateDataList = new HashMap<String, JobMonitorDataForSync>();
	private static Map<String, JobMonitorDataForSync> updateDataList4VSphere = new HashMap<String, JobMonitorDataForSync>();
	
	private void copyJJobMonitor(JJobMonitor srcJM, JJobMonitor destJM) {
		if(srcJM == null || destJM == null)
			return;
		
		destJM.setCtBKJobName(srcJM.getCtBKJobName());
		destJM.setCtBKStartTime(srcJM.getCtBKStartTime());
		destJM.setCtCurCatVol(srcJM.getCtCurCatVol());
		destJM.setCtDWBKJobID(srcJM.getCtDWBKJobID());
		destJM.setDwBKSessNum(srcJM.getDwBKSessNum());
		destJM.setnProgramCPU(srcJM.getnProgramCPU());
		destJM.setnReadSpeed(srcJM.getnReadSpeed());
		destJM.setnSystemCPU(srcJM.getnSystemCPU());
		destJM.setnSystemReadSpeed(srcJM.getnSystemReadSpeed());
		destJM.setnSystemWriteSpeed(srcJM.getnSystemWriteSpeed());
		destJM.setnWriteSpeed(srcJM.getnWriteSpeed());
		destJM.setProductType(srcJM.getProductType());
		destJM.setTransferMode(srcJM.getTransferMode());
		destJM.setUlBackupStartTime(srcJM.getUlBackupStartTime());
		destJM.setUlCompressLevel(srcJM.getUlCompressLevel());
		destJM.setUlElapsedTime(srcJM.getUlElapsedTime());
		destJM.setUlEncInfoStatus(srcJM.getUlEncInfoStatus());
		destJM.setUlEstBytesDisk(srcJM.getUlEstBytesDisk());
		destJM.setUlEstBytesJob(srcJM.getUlEstBytesJob());
		destJM.setUlFlags(srcJM.getUlFlags());
		destJM.setUlJobMethod(srcJM.getUlJobMethod());
		destJM.setUlJobPhase(srcJM.getUlJobPhase());
		destJM.setUlJobStatus(srcJM.getUlJobStatus());
		destJM.setUlJobType(srcJM.getUlJobType());
		destJM.setUlMergedSession(srcJM.getUlMergedSession());
		destJM.setUlProcessedFolder(srcJM.getUlProcessedFolder());
		destJM.setUlSessionID(srcJM.getUlSessionID());
		destJM.setUlThrottling(srcJM.getUlThrottling());
		destJM.setUlTotalFolder(srcJM.getUlTotalFolder());
		destJM.setUlTotalMergedSessions(srcJM.getUlTotalMergedSessions());
		destJM.setUlTotalSizeRead(srcJM.getUlTotalSizeRead());
		destJM.setUlTotalSizeWritten(srcJM.getUlTotalSizeWritten());
		destJM.setUlVolMethod(srcJM.getUlVolMethod());
		destJM.setUlXferBytesDisk(srcJM.getUlXferBytesDisk());
		destJM.setUlXferBytesJob(srcJM.getUlXferBytesJob());
		destJM.setVmInstanceUUID(srcJM.getVmInstanceUUID());
		destJM.setWszDiskName(srcJM.getWszDiskName());
		destJM.setWszEDB(srcJM.getWszEDB());
		destJM.setWszMailFolder(srcJM.getWszMailFolder());
		destJM.setWzBKBackupDest(srcJM.getWzBKBackupDest());
		destJM.setWzBKDestPassword(srcJM.getWzBKDestPassword());
		destJM.setWzBKDestUsrName(srcJM.getWzBKDestUsrName());
		destJM.setWzCurVolMntPoint(srcJM.getWzCurVolMntPoint());
		destJM.setUlTotalVMJobCount(srcJM.getUlTotalVMJobCount());
		destJM.setUlFinishedVMJobCount(srcJM.getUlFinishedVMJobCount());
		destJM.setUlCanceledVMJobCount(srcJM.getUlCanceledVMJobCount());
		destJM.setUlFailedVMJobCount(srcJM.getUlFailedVMJobCount());
	}
	
	public void setUpdateData(String uuid, JJobMonitor jJM, long jobId){
			if(uuid == null)
				return;
			
			synchronized(updateLock) {
				try {
					if(updateDataList.containsKey(uuid) == false) {
						updateDataList.put(uuid, new JobMonitorDataForSync());
					}
					
					JobMonitorDataForSync theData = updateDataList.get(uuid);
					
					if(jJM == null) {
						theData.setStopUpdate(); //stop sync thread
					}
					else {
						copyJJobMonitor(jJM, theData);
						theData.setJobId(jobId);
						theData.setReadyForUpdate();
						theData.clearStopUpdate();
					}
				}catch(Throwable t) {
					logger.debug(t.toString());
				}
			}
	}
	
	public void setUpdateData4VSphere(String uuid, JJobMonitor jJM, long jobId){
		if(uuid == null)
			return;
		
		synchronized(updateLock4VSphere) {
			try {
				if(updateDataList4VSphere.containsKey(uuid) == false) {
					updateDataList4VSphere.put(uuid, new JobMonitorDataForSync());
				}
				
				JobMonitorDataForSync theData = updateDataList4VSphere.get(uuid);
				
				if(jJM == null) {
					theData.setStopUpdate(); //stop sync thread
				}
				else {
					copyJJobMonitor(jJM, theData);
					theData.setJobId(jobId);
					theData.setReadyForUpdate();
					theData.clearStopUpdate();
				}
			}catch(Throwable t) {
				logger.debug(t.toString());
			}
		}
	}
	
	private void initUpdateData(String uuid) {
		synchronized(updateLock) {
			if(updateDataList.containsKey(uuid) == false) {
				updateDataList.put(uuid, new JobMonitorDataForSync());
			}
			
			JobMonitorDataForSync theData = updateDataList.get(uuid);
			theData.clearStopUpdate();
			theData.clearReadyForUpdate();
		}
	}
	
	private void initUpdateData4VSphere(String uuid) {
		synchronized(updateLock4VSphere) {
			if(updateDataList4VSphere.containsKey(uuid) == false) {
				updateDataList4VSphere.put(uuid, new JobMonitorDataForSync());
			}
			
			JobMonitorDataForSync theData = updateDataList4VSphere.get(uuid);
			theData.clearStopUpdate();
			theData.clearReadyForUpdate();
		}
	}
	
	private void getUpdateData(String uuid, JobMonitorDataForSync dataForSync) {
		synchronized(updateLock) {
			if(updateDataList.containsKey(uuid)) {
				JobMonitorDataForSync theData = updateDataList.get(uuid);
				if(theData.isStopUpdate()) {
					dataForSync.setStopUpdate();
				}
				else {
					if(theData.isReadyForUpdate()) {
						copyJJobMonitor(theData, dataForSync);
						dataForSync.setJobId(theData.getJobId());
						dataForSync.setReadyForUpdate();
						theData.clearReadyForUpdate();
						return;
					}
				}
			}
			else {
				dataForSync.setStopUpdate();
			}
		}
	}
	
	private void getUpdateData4VSphere(String uuid, JobMonitorDataForSync dataForSync) {
		synchronized(updateLock4VSphere) {
			if(updateDataList4VSphere.containsKey(uuid)) {
				JobMonitorDataForSync theData = updateDataList4VSphere.get(uuid);
				if(theData.isStopUpdate()) {
					dataForSync.setStopUpdate();
				}
				else {
					if(theData.isReadyForUpdate()) {
						copyJJobMonitor(theData, dataForSync);
						dataForSync.setJobId(theData.getJobId());
						dataForSync.setReadyForUpdate();
						theData.clearReadyForUpdate();
						return;
					}
				}
			}
			else {
				dataForSync.setStopUpdate();
			}
		}
	}
	
	public class JobMonitorEdgeThread implements Runnable {
		protected IEdgeD2DService proxy = null;
		protected String uuid = null;
		
		public JobMonitorEdgeThread(String inUuid) {
			uuid = inUuid;
		}
	
		@Override
		public void run() {
			try {
				doRun();
			}catch(Throwable t) {
				logger.debug(t.toString());
			}finally {
				proxy = null;
			}
		}
		
		public void doRun() {
			logger.info("JobMonitorEdgeThread doRun(): enter ... uuid = " + uuid);
			
			initUpdateData(uuid);
			
			JobMonitorDataForSync dataForSync = new JobMonitorDataForSync();
			dataForSync.clearReadyForUpdate();
			
			while(true) {
				getUpdateData(uuid, dataForSync);
				if(dataForSync.isStopUpdate()) {
					logger.debug("Job Status sync finished. thread exit!");
					break;
				}
				
				if(dataForSync.isReadyForUpdate() == true) {
					try {
						String xmlContent = getXMLContent(dataForSync);
						if(xmlContent == null)
						{
							logger.debug("JobMonitorEdgeThread.run(): getXMLContent() failed!");
							logger.debug("Exit the Job Status Sync thread");
							return;
						}
						
						if(proxy == null) {
							logger.debug("Get Edge connection ...");
							proxy = getEdgeConnection();
							if(proxy == null) {
								logger.debug("Cannot get connection with Central Protection Manager. Job Status Sync Thread exit!");
								logger.debug("Exit the Job Status Sync thread");
								return;
							}
						}
						
						int result = proxy.D2DSyncJobStatus(xmlContent, CommonService.getInstance().getNodeUUID());
						if (result == 0)
							logger.debug("D2DSync(job status) - succeeded!!\n");
						else if( result == 1)
							logger.debug("D2DSync(job status) - XML parser failed!!\n");
						else if (result == 2 )
							logger.debug("D2DSync(job status) - SQL operation failed!!\n");
						else
							logger.debug("D2DSync(job status) - Other error!!\n");
						dataForSync.clearReadyForUpdate();
					} catch (EdgeServiceFault e) {
						SyncD2DStatusService.getInstance().syncD2DStatus2Edge(uuid);
//						logger.error(e.toString()); //authentication failure
						logger.error("EdgeServiceFault! Exit the Job Status Sync thread, uuid = " + uuid, e);
						return;
					} catch (Exception t) {
						SyncD2DStatusService.getInstance().syncD2DStatus2Edge(uuid);
						logger.error("Exception! Exit the Job Status Sync thread, uuid = " + uuid, t);
						if(t instanceof WebServiceException) {// communication problem
							proxy = null; //need to try connection next time
						}	
						else
						if((t.getCause() != null ) && (t.getCause() instanceof ConnectException)) {// communication problem
							proxy = null; //need to try connection next time
						}
					}
				}
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					logger.debug(e.toString());
				}
			}
			logger.info("JobMonitorEdgeThread doRun(): end ... uuid = " + uuid);
		}
		
		public boolean isJobFinishedWithStatus(int status) {
			switch(status){
			case Constants.JOBSTATUS_CRASH:
			case Constants.JOBSTATUS_FAILED:
			case Constants.JOBSTATUS_FINISHED:
				return true;
				default:
					return false;
			}
		}
		
		public IEdgeD2DService getEdgeConnection() throws EdgeServiceFault {
			D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();
			String edgeWSDL = edgeRegInfo.GetEdgeWSDL();
			if(edgeWSDL == null)
			{
				logger.debug("sync2Edge(): there is no edge registration flag!");
				return null;
			}

			String edgeUUID = edgeRegInfo.GetEdgeUUID();
			
			IEdgeD2DService proxy = WebServiceFactory.getEdgeService(edgeWSDL, IEdgeCM4D2D.class);

			proxy.validateUserByUUID(edgeUUID);
			
			return proxy;
		}

		private String getXMLContent(JobMonitorDataForSync JM)
		{
			StringWriter st = new StringWriter();

	        JobStatus2Edge jobStatus = new JobStatus2Edge();

			JAXBContext jaxbContext;
			try {
				jobStatus.setJobId(JM.getJobId());
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
			    jobStatus.setUlTotalVMJobCount(JM.getUlTotalVMJobCount());
			    jobStatus.setUlFinishedVMJobCount(JM.getUlFinishedVMJobCount());
			    jobStatus.setUlCanceledVMJobCount(JM.getUlCanceledVMJobCount());
			    jobStatus.setUlFailedVMJobCount(JM.getUlFailedVMJobCount());
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
				// TODO Auto-generated catch block
				logger.debug(e.toString());
				return null;
			}

	        return st.toString();
		}
	}
	
	public class JobMonitorEdgeThread4VSphere extends JobMonitorEdgeThread {
		
		protected VSphereJobContext jobContext;
		protected long shrmemid;
		
		public JobMonitorEdgeThread4VSphere(String inUuid) {
			super(inUuid);
		}
		
		public JobMonitorEdgeThread4VSphere(String inUuid, VSphereJobContext jobContext, long shrmemid) {
			super(inUuid);
			this.jobContext = jobContext;
			this.shrmemid = shrmemid;
		}

		public void doRun() {
			logger.info("JobMonitorEdgeThread4VSphere doRun(): enter ... uuid = " + uuid);
			
			initUpdateData4VSphere(uuid);
			
			JobMonitorDataForSync dataForSync = new JobMonitorDataForSync();
			dataForSync.clearReadyForUpdate();
			
			while(true) {
				getUpdateData4VSphere(uuid, dataForSync);
				if(dataForSync.isStopUpdate()) {
					break;
				}
				
				if(dataForSync.isReadyForUpdate() == true) {
					try {
						if(proxy == null) {
							logger.debug("Get Edge connection ...");
							proxy = getEdgeConnection();
							if(proxy == null) {
								logger.debug("Cannot get connection with HBBU. Job Status Sync Thread exit!");
								logger.debug("Exit the vm Job Status Sync thread");
								return;
							}
						}
						JobMonitor jobMonitor = convertJJobMonitor2JobMonitor(dataForSync);
						if (jobMonitor.getJobPhase() == Constants.JobExitPhase || jobMonitor.getJobPhase() == Constants.BackupJob_Phase_PROC_EXIT) {
							// sync the backup information summary to edge side							
							D2DStatusInfo summary = D2DStatusServiceImpl.getInstance().getVSphereVMStatusInfo(jobContext.getExecuterInstanceUUID());
							jobMonitor.setD2DStatusInfo(summary);
						}
						int result = proxy.syncBackupJobsStatus(jobContext.getExecuterInstanceUUID(), jobMonitor);
						if (result == 0)
							logger.debug("D2DSync(vm job status) - succeeded!!\n");
						dataForSync.clearReadyForUpdate();
					} catch (EdgeServiceFault e) {
						SyncD2DStatusService.getInstance().syncVSphereStatus2Edge(jobContext.getExecuterInstanceUUID());
						logger.error("EdgeServiceFault! Exit the vm Job Status Sync thread, uuid = " + uuid, e); //authentication failure
						return;
					} catch (Exception t) {
						SyncD2DStatusService.getInstance().syncVSphereStatus2Edge(jobContext.getExecuterInstanceUUID());
						logger.error("Exception! Exit the vm Job Status Sync thread, uuid = " + uuid, t);
						if(t instanceof WebServiceException) {// communication problem
							proxy = null; //need to try connection next time
						}	
						else
						if((t.getCause() != null ) && (t.getCause() instanceof ConnectException)) {// communication problem
							proxy = null; //need to try connection next time
						}
					}
				}
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					logger.debug(e.toString());
				}
			}
			logger.info("JobMonitorEdgeThread4VSphere doRun(): end ... uuid = " + uuid);
		}

		private JobMonitor convertJJobMonitor2JobMonitor(JobMonitorDataForSync dataForSync) {
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
			jobMonitor.setUlTotalVMJobCount(dataForSync.getUlTotalVMJobCount());
			jobMonitor.setUlFinishedVMJobCount(dataForSync.getUlFinishedVMJobCount());
			jobMonitor.setUlCanceledVMJobCount(dataForSync.getUlCanceledVMJobCount());
			jobMonitor.setUlFailedVMJobCount(dataForSync.getUlFailedVMJobCount());
			return jobMonitor;
		}
		
		public IEdgeD2DService getEdgeConnection() throws EdgeServiceFault {		
			D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
			EdgeRegInfo edgeRegInfo = edgeReg.getEdgeRegInfo(ApplicationType.vShpereManager);
			IEdgeD2DService proxy = WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(),IEdgeD2DService.class);
			proxy.validateUserByUUID(edgeRegInfo.getEdgeUUID());
			return proxy;
		}
	}
}
