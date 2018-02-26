package com.ca.arcserve.edge.app.base.webservice.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao.JobHistoryProductType;
import com.ca.arcserve.edge.app.base.common.NamingThreadFactory;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.LinuxD2DServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.LinuxD2DJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DAllJobStatusCache;
import com.ca.arcserve.edge.app.base.webservice.instantvm.InstantVMManager;
import com.ca.arcserve.edge.app.base.webservice.jobhistory.JobHistoryServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.data.JobScript;
import com.ca.arcserve.linuximaging.webservice.data.JobStatus;
import com.ca.arcserve.linuximaging.webservice.edge.SynchronizeContext;

/**
 * D2DAgent job monitor tracker which used for get current job detail
 * information
 * 
 * @author lijyo03
 * 
 */
public class LinuxD2DJobMonitorReader implements JobMonitorReader {

	public static long ENCRYPTION_UNKNOWN = 0;
	public static long ENCRYPTION_AES_128BIT = 1;
	public static long ENCRYPTION_AES_192BIT = 2;
	public static long ENCRYPTION_AES_256BIT = 3;

	public static final int COMPRESSIONNONE = 0;
	public static final int COMPRESSIONSTANDARD = 1;
	public static final int COMPRESSIONMAX = 9;

	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private static final Logger logger = Logger.getLogger(LinuxD2DJobMonitorReader.class);
	private String errorMessage;

	private LinuxD2DJobMonitorReader() {
	}

	private static class LazyHolder {
		public static final LinuxD2DJobMonitorReader tracker = new LinuxD2DJobMonitorReader();
	}

	public static LinuxD2DJobMonitorReader getInstance() {
		return LazyHolder.tracker;
	}

	@Override
	@Deprecated
	public List<JobMonitor> getJobMonitor(JobDetail jobDetail)
			throws EdgeServiceFault {
		logger.error("LinuxD2DJobMonitorReader getJobMonitor has no implement !!!!");
		return null;
	}

	private int convert2JobType(int jobType) {
		switch (jobType) {
		case JobScript.BACKUP:
		case JobScript.BACKUP_FULL:
		case JobScript.BACKUP_INCREMENTAL:
		case JobScript.BACKUP_VERIFY:
			return SynchronizeContext.JOBTYPE_BACKUP;
		case JobScript.RESTORE:
		case JobScript.RESTORE_FILE:
		case JobScript.RESTORE_VOLUME:
			return SynchronizeContext.JOBTYPE_RESTORE;
		case JobScript.RESTORE_BMR:
			return SynchronizeContext.JOBTYPE_BMR;
		case JobScript.RESTORE_VM:
			return SynchronizeContext.JOBTYPE_VM_RECOVERY;
		default:
			return SynchronizeContext.JOBTYPE_NONE;
		}
	}

	private long convert2JobMethod(int jobType) {
		switch (jobType) {
		case JobScript.BACKUP:
			return SynchronizeContext.All;
		case JobScript.BACKUP_FULL:
			return SynchronizeContext.Full;
		case JobScript.BACKUP_INCREMENTAL:
			return SynchronizeContext.Incremental;
		case JobScript.BACKUP_VERIFY:
			return SynchronizeContext.Resync;
		case JobScript.RESTORE:
		case JobScript.RESTORE_FILE:
		case JobScript.RESTORE_VM:
		case JobScript.RESTORE_VOLUME:
			return SynchronizeContext.All;
		case JobScript.RESTORE_BMR:
			return SynchronizeContext.All;
		default:
			return SynchronizeContext.Unknown;
		}
	}

	private long convert2JobStatus(int status) {
		switch (status) {
		case JobStatus.READY:
			return SynchronizeContext.JOBSTATUS_SKIPPED;
		case JobStatus.IDLE:
			return SynchronizeContext.JOBSTATUS_IDLE;
		case JobStatus.FINISHED:
			return SynchronizeContext.JOBSTATUS_FINISHED;
		case JobStatus.CANCELLED:
			return SynchronizeContext.JOBSTATUS_CANCELLED;
		case JobStatus.FAILED:
			return SynchronizeContext.JOBSTATUS_FAILED;
		case JobStatus.INCOMPLETE:
			return SynchronizeContext.JOBSTATUS_INCOMPLETE;
		case JobStatus.ACTIVE:
			return SynchronizeContext.JOBSTATUS_ACTIVE;
		case JobStatus.WAITING:
			return SynchronizeContext.JOBSTATUS_WAITING;
		case JobStatus.CRASHED:
			return SynchronizeContext.JOBSTATUS_CRASH;
		case JobStatus.NEEDREBOOT:
			return SynchronizeContext.JOBSTATUS_STOP;
		case JobStatus.FAILED_NO_LICENSE:
			return SynchronizeContext.JOBSTATUS_LICENSE_FAILED;
		default:
			return SynchronizeContext.JOBSTATUS_MISSED;
		}
	}
/*
	private long convertToEncryption(long encryptionType) {
		long encryption = 0;
		if (encryptionType == 0) {
			encryption = ENCRYPTION_UNKNOWN;
		} else if (encryptionType == 65537) {
			encryption = ENCRYPTION_AES_128BIT;
		} else if (encryptionType == 65538) {
			encryption = ENCRYPTION_AES_192BIT;
		} else if (encryptionType == 65539) {
			encryption = ENCRYPTION_AES_256BIT;
		}
		return encryption;
	}
*/
	private long convertToEncryption(String encryptionName) {
		long encryption = 0;
		if ("AES128".equals(encryptionName)) {
			encryption = ENCRYPTION_AES_128BIT;
		} else if ("AES192".equals(encryptionName)) {
			encryption = ENCRYPTION_AES_192BIT;
		} else if ("AES256".equals(encryptionName)) {
			encryption = ENCRYPTION_AES_256BIT;
		} else {
			encryption = ENCRYPTION_UNKNOWN;
		}
		return encryption;
	}

	private long convertToCompression(int compression) {
		switch (compression) {
		case 0:
			return COMPRESSIONNONE;
		case 1:
			return COMPRESSIONSTANDARD;
		case 2:
			return COMPRESSIONMAX;
		default:
			return COMPRESSIONNONE;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<FlashJobMonitor> getJobMonitorOnServer(JobDetail jobDetail)
			throws EdgeServiceFault {
		logger.debug("[linuxD2D] start getJobMonitorOnServer jobDetail.getServerId()="+jobDetail.getServerId());

		ILinuximagingService linuxService;
		List<FlashJobMonitor> list = new ArrayList<FlashJobMonitor>();

		try (LinuxD2DConnection linuxD2DConnection = connectionFactory
				.createLinuxD2DConnection(jobDetail.getServerId())) {
			linuxD2DConnection.connect();
			linuxService = linuxD2DConnection.getService();
			com.ca.arcserve.linuximaging.webservice.data.JobStatus[] statusArray = linuxService
					.getRunningJobStatusList();
			if (statusArray == null || statusArray.length <= 0) {
				logger.debug("[linuxD2D] getJobStatusList return size is 0");
				return list;
			}
			for (com.ca.arcserve.linuximaging.webservice.data.JobStatus status : statusArray) {
				if (status.getJobID() < 0)
					continue;
				logger.debug("status [ id:" + status.getId() + "; jobType:"
						+ status.getJobType() + "; jobId:" + status.getJobID()
						+ "; jobMethod:" + status.getJobMethod() + "; uuid:"
						+ status.getUuid() + "; startTime:"
						+ status.getStartTime() + "; executeTime:"
						+ status.getExecuteTime() + "; elapsedTime:"
						+ status.getElapsedTime() + "; finishTime:"
						+ status.getFinishTime() + "; compressLevel:"
						+ status.getCompressLevel() + "; encryptalgoType:"
						+ status.getEncryptAlgoType() + "; encryptAlgoName:"
						+ status.getEncryptAlgoName() + "; jobName:"
						+ status.getJobName() + "; jobPhase:"
						+ status.getJobPhase() + "; progress:"
						+ status.getProgress() + "; targetServer:"
						+ status.getTargetServer() + "; processData:"
						+ status.getProcessedData() + "; throughput:"
						+ status.getThroughput() + "; writeData:"
						+ status.getWriteData() + "; writeThroughput:"
						+ status.getWriteThroughput() + "; volume:"
						+ status.getVolume() + "; totalSizeRead:"
						+ status.getTotalSizeRead() + "; totalSizeWrite:"
						+ status.getTotalSizeWritten() + "; totalUnique:"
						+ status.getTotalUniqueData() + "; isEnableDedup:"
						+ status.isDedupeEnabled() + "]");

				if (status.getJobType() == JobScript.RESTORE_VM) {
					InstantVMJobMonitor jobMonitor = InstantVMManager
							.getInstance().getInstantVMJobMonitor4Linux(
									status.getUuid(),
									JobHistoryProductType.LinuxD2D);
					if(jobMonitor!=null){
						list.add(jobMonitor);
						logger.debug("[linuxD2D] AddJobMonitor:"+ jobMonitor.toString());
					}
				} else {
					LinuxD2DJobMonitor jobMonitor = getNormalJobMonitor(
							jobDetail, status);
					list.add(jobMonitor);
					logger.debug("[linuxD2D] AddJobMonitor:"+ jobMonitor.toString());
				}

			}
		}catch (Exception e) { 
			if(e instanceof SOAPFaultException){
				String errorMessage = ((SOAPFaultException)e).getFault().getFaultString();				
				if (errorMessage != null && errorMessage.contains(LinuxD2DServiceFault.METHOD_NOT_DEFINED_MESSAGE)) {
					logger.debug("[D2DAgent] getJobMonitorOnServer catch ERROR(Cannot find dispatch method)");
					return D2DAllJobStatusCache.getD2DAllJobStatusCache().getJobStatusInfoList(
							"LinuxD2D-" + jobDetail.getNodeId() + "-");	
				}					
			}
			if(errorMessage==null||(!errorMessage.equals(e.getMessage()))){
				errorMessage = e.getMessage();
				logger.error("[linuxD2D] getJobMonitorOnServer catch Error:"+errorMessage);
			}
			return D2DAllJobStatusCache.getD2DAllJobStatusCache().getJobStatusInfoList(
					"LinuxD2D-" + jobDetail.getNodeId() + "-");	
		}
		
		logger.debug("[linuxD2D] end getJobMonitorOnServer return size is "+ list.size());
		// be compatible for prior versions
		if (list.size() == 0) {
			logger.debug("[linuxD2D] getJobMonitorOnServer getJobMonitorFromCache list.size = "
					+ list.size()
					+ " Key=LinuxD2D-"
					+ jobDetail.getNodeId()
					+ "-");
			return D2DAllJobStatusCache.getD2DAllJobStatusCache()
					.getJobStatusInfoList(
							"LinuxD2D-" + jobDetail.getNodeId() + "-");
		}
		return list;
	}

	private LinuxD2DJobMonitor getNormalJobMonitor(JobDetail jobDetail,
			com.ca.arcserve.linuximaging.webservice.data.JobStatus status) {
		LinuxD2DJobMonitor jobMonitor = new LinuxD2DJobMonitor();
		jobMonitor.setJobType(convert2JobType(status.getJobType()));
		jobMonitor.setJobId(status.getJobID());
		jobMonitor.setElapsedTime(status.getElapsedTime() * 1000);
		jobMonitor.setStartTime(status.getExecuteTime() * 1000);
		// get jobMethod by jobType
		jobMonitor.setJobMethod(convert2JobMethod(status.getJobType()));
		jobMonitor.setJobPhase(status.getJobPhase());
		jobMonitor.setJobStatus(convert2JobStatus(status.getStatus()));
		jobMonitor.setProgress(status.getProgress());
		jobMonitor.setnReadSpeed(status.getThroughput());
		jobMonitor.setnWriteSpeed(status.getWriteThroughput());
		jobMonitor.setCurrentProcessDiskName(status.getVolume());
		jobMonitor.setTransferBytesJob(status.getProcessedData());
		jobMonitor.setCompressLevel(convertToCompression(status
				.getCompressLevel()));
		jobMonitor.setEncInfoStatus(convertToEncryption(status
				.getEncryptAlgoName()));
		jobMonitor.setD2dServerName(status.getTargetServer());
		jobMonitor.setJobUUID(status.getUuid());
		jobMonitor.setRunningServerId(jobDetail.getServerId());
		jobMonitor.setRunningOnRPS(status.isBackupToRps());
		jobMonitor.setHistoryProductType(JobHistoryProductType.LinuxD2D
				.getValue());
		//jobMonitor.setNodeId(jobDetail.getNodeId());
		jobMonitor.setTotalSizeRead(status.getTotalSizeRead());
		jobMonitor.setTotalSizeWritten(status.getTotalSizeWritten());
		jobMonitor.setUniqueData(status.getTotalUniqueData());
		jobMonitor.setEnableDedupe(status.isDedupeEnabled());
		jobMonitor.setNodeId(jobDetail.getNodeId());
		jobMonitor.setJobMonitorId("Monitor_LinuxD2D-"
				+ jobMonitor.getJobType() + "-" + jobMonitor.getJobId());
		List<JobHistory> historys = jobDetail.getHistorysList();
		if(historys!=null&& (!historys.isEmpty())){
			if(logger.isDebugEnabled()){
				for (JobHistory history:historys) {
					logger.debug("[linuxD2D] syncJobMonitor historyList "+history.toString());
				}
				logger.debug("[linuxD2D] syncJobMonitor FlashJobMonitor "+jobMonitor.toString());
			}
			for (JobHistory history:historys) {
				if (Integer.parseInt(history.getServerId())!=jobDetail.getServerId()) {
					logger.debug("[linuxD2D] syncJobMonitor Serverid not equals history="+history.toString());
					continue;
				}
				if(history.getJobType()==jobMonitor.getJobType()
						&& history.getJobId()==jobMonitor.getJobId()
						//&& Integer.parseInt(history.getServerId())==jobDetail.getServerId()
						//&& Integer.parseInt(history.getAgentId())==jobMonitor.getNodeId()
						){
					//log filter add agentNodeName and ServerNodeName 
					jobMonitor.setNodeId(Integer.parseInt(history.getAgentId()));
					jobMonitor.setAgentNodeName(history.getAgentNodeName());
					jobMonitor.setServerNodeName(history.getServerNodeName());
					jobMonitor.setD2dServerName(history.getAgentNodeName());
					if(history.getAgentUUID()!=null && (!history.getAgentUUID().isEmpty())){
						if (history.getAgentUUID().contains("VM:")) {
							jobMonitor.setVmInstanceUUID(history.getAgentUUID().replace("VM:", ""));
						} else if (history.getAgentUUID().contains("D2D:")) {
							jobMonitor.setD2dUuid(history.getAgentUUID().replace("D2D:", ""));
						}
					}
					logger.debug("[linuxD2D] syncJobMonitor end jobMonitor="+jobMonitor.toString());
					break;
				}
			}
		}
		return jobMonitor;
	}

	@Override
	public boolean cancelJob(JobDetail jobDetail) throws EdgeServiceFault {
		logger.debug("[linuxD2D] start cancelJob");
		NodeServiceImpl nodeService = new NodeServiceImpl();
		return nodeService.cancelJob(jobDetail.getNodeId(),
				jobDetail.getHostName(), jobDetail.getJobId());
	}
	
	private static BlockingQueue<JobDetail> blockingQueueForLinux;
	private static ThreadPoolExecutor jobsExcutor = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, 
			new LinkedBlockingQueue<Runnable>(),new NamingThreadFactory( "QueryLinuxD2DJobMonitor" ));
	
	public static void initReaderThread(BlockingQueue<JobDetail> blockingQueue){
		if(blockingQueueForLinux!=null)
			return;
		logger.debug("LinuxD2DJobMonitorReader initReaderThread");
		blockingQueueForLinux = blockingQueue;
		for(int i=0; i < 10; i++){
			final LinuxD2DJobMonitorReader reader = new LinuxD2DJobMonitorReader();
			jobsExcutor.submit(new Runnable() {
				
				@Override
				public void run() {
					readJobMonitor(reader);	
				}
			});			
		}
	}
	
	public static void shutdonwReaderThread() {
		if(jobsExcutor!=null)
			jobsExcutor.shutdownNow();
	}
	
	
	public static void readJobMonitor(LinuxD2DJobMonitorReader reader){		
		while(true){		
			try {
				JobDetail jobDetail = blockingQueueForLinux.take();
				logger.debug("LinuxD2DJobMonitorReader readJobMonitor blockingQueueForRPS.take() "+jobDetail.toString());
				if(jobDetail.getHistorysList()==null||jobDetail.getHistorysList().isEmpty()){					
					logger.debug("LinuxD2DJobMonitorReader readJobMonitor NoNeed get as history.size=0");				
					continue;
				}
				try {
					List<FlashJobMonitor> jobMonitors = reader.getJobMonitorOnServer(jobDetail);
					if(jobMonitors!=null && jobMonitors.size()>0){	
						logger.debug("LinuxD2DJobMonitorReader readJobMonitor JobMonitor.size"+jobMonitors.size()
								+" history.size="+jobDetail.getHistorysList().size());
						JobHistoryServiceImpl.cacheGlobalMonitorMap(jobDetail.getHistorysList(),jobMonitors);						
					} else {
						logger.debug("LinuxD2DJobMonitorReader readJobMonitor JobMonitor.size=0 "+jobDetail.toString());
					}
				} catch (EdgeServiceFault e) {
					logger.debug("LinuxD2DJobMonitorReader readJobMonitor catch Error )",e);
				}
				Thread.sleep(100);
			} catch (InterruptedException|RejectedExecutionException e) {
				logger.error("LinuxD2DJobMonitorReader readJobMonitor catch Error, and will exit while(true)",e);
				return;
			}
		
		}		
	}
}
