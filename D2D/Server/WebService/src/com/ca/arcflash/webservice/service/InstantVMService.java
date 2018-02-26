package com.ca.arcflash.webservice.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;

import com.ca.arcflash.assurerecovery.AssureRecoveryJobScript;
import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.ha.utils.VDDKService;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareVirtualInfrastructureManager;
import com.ca.arcflash.ha.vmwaremanager.VMConfigInfo;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.ha.vmwaremanager.powerState;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.instantvm.DNSUpdaterParameter;
import com.ca.arcflash.instantvm.DiskFileInfo;
import com.ca.arcflash.instantvm.HypervisorType;
import com.ca.arcflash.instantvm.InstantVHDInfo;
import com.ca.arcflash.instantvm.InstantVHDResult;
import com.ca.arcflash.instantvm.InstantVMConfig;
import com.ca.arcflash.instantvm.InstantVMNode;
import com.ca.arcflash.instantvm.InstantVMStatus;
import com.ca.arcflash.instantvm.InstantVMStatus.HearbeatState;
import com.ca.arcflash.instantvm.InstantVMStatus.InstantVmNodeStatus;
import com.ca.arcflash.instantvm.InstantVMStatus.JobState;
import com.ca.arcflash.instantvm.InstantVMStatus.VMState;
import com.ca.arcflash.instantvm.PrecheckCriteria;
import com.ca.arcflash.instantvm.PrecheckResult;
import com.ca.arcflash.instantvm.SessionInfo;
import com.ca.arcflash.instantvm.VMInfo;
import com.ca.arcflash.instantvm.VMWareInfo;
import com.ca.arcflash.jni.common.JJobHistory;
import com.ca.arcflash.jobqueue.ARJobQueue;
import com.ca.arcflash.listener.manager.ListenerManager;
import com.ca.arcflash.rps.webservice.RPSWebServiceFactory;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4D2D;
import com.ca.arcflash.service.common.ActivityLogSyncher;
import com.ca.arcflash.service.common.WebServiceErrorMessages;
import com.ca.arcflash.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.service.jni.model.JActLogDetails;
import com.ca.arcflash.service.util.WebServiceMessages;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.IVMJobMonitor;
import com.ca.arcflash.webservice.data.IVMNodeInfo;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcflash.webservice.data.job.rps.IJobDependency;
import com.ca.arcflash.webservice.data.job.rps.IVMJobArg;
import com.ca.arcflash.webservice.data.job.rps.JobDependencySource;
import com.ca.arcflash.webservice.data.listener.FlashListenerInfo;
import com.ca.arcflash.webservice.data.merge.MergeAPISource;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.PreCheckInstantVMProxyModel;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.jni.model.JHypervVMInfo;
import com.ca.arcflash.webservice.jni.model.JIVMJobStatus;
import com.ca.arcflash.webservice.jni.model.JRWLong;
import com.ca.arcflash.webservice.replication.ReplicationMessage;
import com.ca.arcflash.webservice.scheduler.AdvancedScheduleTrigger;
import com.ca.arcflash.webservice.scheduler.AssureRecoveryJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.rps.RPSServiceProxyManager;
import com.ca.arcflash.webservice.service.rps.SettingsService;
import com.ca.ha.webservice.jni.HyperVException;
import com.ca.ha.webservice.jni.HyperVJNI;

public class InstantVMService extends BaseService implements Observer, IJobDependency{
	private static final Logger logger = Logger.getLogger(InstantVMService.class);
	private static InstantVMService instance = new InstantVMService();
	
	private static final Map<String, IVMJobMonitor> ivmJobMonitors = new TreeMap<String, IVMJobMonitor>();
	private static final ConcurrentHashMap<String, InstantVMConfig> ivmJobMap = new ConcurrentHashMap<String, InstantVMConfig>();

	public static final String INSTANT_VM_CONFIG_PATH = CommonUtil.D2DHAInstallPath+"Configuration\\InstantVM\\";
	public static final String INSTANT_VM_LOG_PATH = CommonUtil.D2DHAInstallPath+"Logs\\";
	public static final String INSTANT_VM_JOB_FILENAME_EXTENSION = ".xml";
	
	private static Lock ivmMonitorThreadLock = new ReentrantLock();
	private static boolean monitorThreadStop = false;
	
	private static final String IVM_JOB_NAME = "InstantVmJob";
	private static final String IVM_JOB_GROUP_NAME = "InstantVmJobGroup";
	private static final String IVM_TRIGGER_NAME = "InstantVmTrigger";
	private static final String IVM_TRIGGER_GROUP_NAME = "InstantVmTriggerGroup";
	private static List<IVMJobMonitor> toBeCleanJobList = new ArrayList<IVMJobMonitor>();
	private static Thread ivmMonitorThread = null;
//	private static final long BYTE_PER_MEGABYTE = 1024 * 1024;
	
	private ARJobQueue jobQueue;
	
	private InstantVMService() {
		String jobQueueLocation = CommonUtil.D2DHAInstallPath + "Configuration\\AFJobQueue\\";
		jobQueue = new ARJobQueue(jobQueueLocation);
	}
	
	public static InstantVMService getInstance() {
		return instance;
	}
	
	
//	public void InitJobQueue() {
//		try {
//			File queueLocation = new File(INSTANT_VM_CONFIG_PATH);
//			if(!queueLocation.exists()) {
//				queueLocation.mkdirs();
//			}
//			
//			FilenameFilter filter = new FilenameFilter() {
//				@Override
//				public boolean accept(File dir, String name) {
//					if (name.endsWith(INSTANT_VM_JOB_FILENAME_EXTENSION)) {
//						return true;
//					}
//					return false;
//				}
//			};
//
//			JAXBContext ctx = JAXBContext.newInstance(InstantVMConfig.class);
//			for (File file : queueLocation.listFiles(filter)) {
//				try {
//					InstantVMConfig job = (InstantVMConfig) ctx.createUnmarshaller().unmarshal(file);
//					if(job != null){
//						enOrDecrypt(job, false);
//						
////						if (job.getJobStatus() == InstantVMConfig.IVM_JOB_FAILED || job.getJobStatus() == InstantVMConfig.IVM_JOB_STOPPED) {
////							removeJobScript(file);
////							continue;
////						}
//						
//						IVMJobMonitor jobMonitor = getIVMJobMonitor(job.getIVMJobUUID());
//						if (jobMonitor == null)
//							jobMonitor = addIVMJobMonitor(job.getIVMJobUUID());
//						synchronized (jobMonitor) {
//							constructIvmJobMonitor(job, jobMonitor);
//							
//							if (job.getJobStatus() == InstantVMConfig.IVM_JOB_RUNNING)
//								jobMonitor.setJobPhase(InstantVMConfig.IVM_JOB_INIT);
//							else
//								jobMonitor.setJobPhase(InstantVMConfig.IVM_JOB_STOPPING);
//							jobMonitor.setFinished(false);
//							jobMonitor.setJobStatus(JobStatus.JOBSTATUS_ACTIVE);
//						}
//						
//						setIVMJob(job);
//						resumeIvmJob(job, file.getPath());
//						
//					}
//				} catch (Exception e) {
//					logger.error("Failed to get Instant VM job from file " + file.getAbsoluteFile(), e);
//				}
//			}
//		} catch (Exception e) {
//			logger.error("Failed to load Instant VM jobs.", e);
//		}		
//		
//		if (!ivmJobMap.isEmpty())
//			startMonitorIVMStatus();
//	}
//	
	protected static final int RestartAgentFlag = 0x8;
	
//	protected void resumeIvmJob(InstantVMConfig job, String configFile) {
//		long jobStatus = job.getJobStatus();
//		if (jobStatus == InstantVMConfig.IVM_JOB_RUNNING) {
//			logger.info(String.format("Try resuming IVM for job %s", configFile));
//			long ret = 0;
//			try {
//				ret = getNativeFacade().startInstantVM(configFile, RestartAgentFlag);
//			} catch (Exception e) {
//				logger.error(e.getMessage(), e);
//				ret = -1;
//			} finally {
//				if (ret != 0) {
//					logger.error("Failed to resume instant vm job.");
//					completeIVMJob(job.getIVMJobUUID(), JobStatus.JOBSTATUS_FAILED);
//					logger.info("Update Instant VM job history.");
//					try {
//						getNativeFacade().markD2DJobEnd(job.getJobID(), JobStatus.JOBSTATUS_FAILED, null);
//					} catch (Exception e) {
//						logger.error("Fail to markD2DJobEnd.", e);
//					}
//				} else {
//					// start job monitor thread
//					 startMonitorIVMStatus();
//				}
//			}
//		} else {
//			String vmUUID = job.getIVMJobUUID();
//			logger.info(String.format("Try cleaning up instant vm job: %s.", vmUUID));
//			try {
//				stopInstantVM(vmUUID, true);
//			} catch (Exception e) {
//				logger.error(e.getMessage(), e);
//			}
//		}
//	}
//	
	public long restartInstantVM(String instantVMUUID) {
		logger.info(String.format("Try restarting Instant VM: %s", instantVMUUID));
		InstantVMConfig ivmConfig  = getIVMJob(instantVMUUID);
		if (null == ivmConfig) {
			ivmConfig = getInstantVMJobScript(instantVMUUID);
			if (null != ivmConfig) {
				setIVMJob(ivmConfig);
			} else {
				logger.error(String.format("Fail to get the Instant VM job script for job: %s", instantVMUUID));
				return -1;
			}
		}
		long jobStatus = ivmConfig.getJobStatus();
		logger.info(String.format("The status of Instant VM job %s is %d.", instantVMUUID, jobStatus));
		long ret = 0;
		if (jobStatus == InstantVMConfig.IVM_JOB_RUNNING
					|| jobStatus == InstantVMConfig.IVM_JOB_ERROR_RECOVERABLE) {
			IVMJobMonitor jobMonitor = getIVMJobMonitor(ivmConfig.getIVMJobUUID());
			if (jobMonitor == null)
			{
				jobMonitor = addIVMJobMonitor(ivmConfig.getIVMJobUUID());
				logger.info(String.format("Job Monitor %s has been created.", ivmConfig.getIVMJobUUID()));
			}
			synchronized (jobMonitor) {
				constructIvmJobMonitor(ivmConfig, jobMonitor);
				jobMonitor.setJobId(ivmConfig.getJobID());
				jobMonitor.setStartJobId(ivmConfig.getJobID());
				jobMonitor.setMarkStartJobEnd(false);      // for restart case
				jobMonitor.setFinished(false);
				jobMonitor.setJobStatus(JobStatus.JOBSTATUS_ACTIVE);
				
				UpdateJobScriptInfoByJobMonitor(jobMonitor);
			}		
			setIVMJob(ivmConfig);
			if (!HACommon.isTargetPhysicalMachine(ivmConfig.getNodeUUID())){
				ActivityLogSyncher.getInstance().addVM(ivmConfig.getNodeUUID());
			}
			
			SessionInfo sessInfo = new SessionInfo(ivmConfig.getInstantVMNodes().get(0).getSessionInfo());
			if (sessInfo.isRPSSession()) {
				IRPSService4D2D client;
				try {
					client = RPSServiceProxyManager.getRPSServiceClient(sessInfo.getBackupOperatorHostname(), sessInfo.getBackupOperatorUserName(), 
							sessInfo.getBackupOperatorPassword(), (int)sessInfo.getBackupOperatorPort(), sessInfo.getBackupOperatorProtocol(), sessInfo.getRpsDatastoreUUID());
				} catch (ServiceException e) {
					logger.error("Fail to genenrate webservice stub to IVM proxy", e);
					return -1;
				}
				IVMJobArg arg = new IVMJobArg();
				arg.setInstantVMConfig(ivmConfig);
				arg.setLocalD2DName(ivmConfig.getProxyHostname());
				arg.setLocalD2DPort(CommonService.getInstance().getServerPort());
				arg.setLocalD2DProtocol(CommonService.getInstance().getServerProtocol());
				arg.setD2dLoginUUID(CommonService.getInstance().getLoginUUID());
				logger.info("[IVM]Submit Start IVM Job: "+ivmConfig.getIVMJobUUID());
				logger.info("[IVM]LocalD2DName: "+arg.getLocalD2DName());
				logger.info("[IVM]D2dLoginUUID: "+arg.getD2dLoginUUID());
				ret = client.RPSSubmitInstantVM(arg);
				Register4RPS(sessInfo, instantVMUUID, true);
			} else {
				try {
					String configFilePath = InstantVMService.INSTANT_VM_CONFIG_PATH + instantVMUUID 
							+ InstantVMService.INSTANT_VM_JOB_FILENAME_EXTENSION;
					ret = getNativeFacade().startInstantVM(configFilePath, RestartAgentFlag);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					ret = -1;
				} finally {
					if (ret != 0) {
						logger.error(String.format("Fail to restart Instant VM: %s", instantVMUUID));
						completeIVMJob(instantVMUUID, JobStatus.JOBSTATUS_FAILED);
					} else {
						logger.info(String.format("Restart Instant VM: %s successfully.", instantVMUUID));
					}
				}
			}
		} else {
			logger.warn("Skip restarting Instant VM: incorrect job status.");
		}
		return ret;
	}
	
	private void dumpJobStatus(String ivmJobUUID) {
		try {
			JIVMJobStatus jobStatus = getIVMJobStatus(ivmJobUUID);
			logger.info(String.format("JobStatus, JobUUID=%s, JobPhase=%s, AgentExist=%s.",
						    ivmJobUUID, jobStatus.getJobPhase(), jobStatus.isAgentExist() + ""));
			IVMJobMonitor jobMonitor = getIVMJobMonitor(ivmJobUUID);
			if (null != jobMonitor) {
		    	logger.info(String.format("JobMonitor, JobUUID=%s, JobPhase=%s, JobStatus=%s, isFinished=%s.",
							ivmJobUUID, jobMonitor.getJobPhase(),jobMonitor.getJobStatus(), jobMonitor.isFinished() + ""));
		    }
		}
		catch(Exception e){
		}
	}
	
	public NativeFacade getNativeFacade(){
		return BackupService.getInstance().getNativeFacade();
	}
	
	// IVM job
	private synchronized Map<String, InstantVMConfig> getAllIVMJobs() {
		return ivmJobMap;
	}
	

	private InstantVMConfig getIVMJob(String instantVMJobUUID) {
		synchronized (ivmJobMap) {
			return ivmJobMap.get(instantVMJobUUID);
		}
	}
	
	private void setIVMJob(InstantVMConfig ivmJob) {
		if (StringUtil.isEmptyOrNull(ivmJob.getIVMJobUUID()))
			return;
		
		synchronized (ivmJobMap) {
			ivmJobMap.put(ivmJob.getIVMJobUUID(), ivmJob);
		}
	}
	private void removeIVMJob(String instantVMJobUUID) {
		synchronized (ivmJobMap) {
			ivmJobMap.remove(instantVMJobUUID);
			logger.info(String.format("IVMJob %s was removed from the Joblist.", instantVMJobUUID));
		}
	}
	
	// IVM job monitor
	public synchronized Map<String, IVMJobMonitor> getAllIVMJobMonitors() {
		return ivmJobMonitors;
	}

	private IVMJobMonitor addIVMJobMonitor(String instantVMJobUUID) {
		synchronized (ivmJobMonitors) {
			IVMJobMonitor monitor = new IVMJobMonitor();
			monitor.setJobUUID(instantVMJobUUID);
			ivmJobMonitors.put(instantVMJobUUID, monitor);
			return monitor;
		}
	}
	
	public void removeIVMJobMonitor(String instantVMJobUUID) {
		synchronized (ivmJobMonitors) {
			ivmJobMonitors.remove(instantVMJobUUID);
			logger.info(String.format("IVMJob monitor %s was removed from the Joblist.", instantVMJobUUID));
		}
	}

	public IVMJobMonitor getIVMJobMonitor(String instantVMJobUUID) {
		IVMJobMonitor monitor = null;
		synchronized (ivmJobMonitors) {
			monitor = ivmJobMonitors.get(instantVMJobUUID);
			if (null == monitor) {
				logger.warn(String.format("IVM job Monitor with UUID=%s does not exist in memory.", instantVMJobUUID));
				
				InstantVMConfig ivmConfig = getInstantVMJobScript(instantVMJobUUID);
				if (null == ivmConfig) {
					logger.error(String.format("Failed to read the JobScript File with UUID=%s.", instantVMJobUUID));
					return null;
				}

				monitor = new IVMJobMonitor();
				monitor.setJobUUID(instantVMJobUUID);
				monitor.setJobType(ivmConfig.getJobType());
				monitor.setJobId(ivmConfig.getJobID());
				monitor.setStartJobId(ivmConfig.getStartJobId());
				monitor.setStopJobId(ivmConfig.getStopJobId());
				monitor.setMarkFailedDeleted(ivmConfig.getIsMarkFailedDeleted());
				monitor.setMarkStartJobEnd(ivmConfig.getIsMarkStartJobEnd());
				monitor.setMarkStopJobEnd(ivmConfig.getIsMarkStopJobEnd());
				monitor.setJobPhase(ivmConfig.getJobStatus());
				monitor.setNodeUUID(ivmConfig.getNodeUUID());
				
				setIVMJob(ivmConfig);
				
				ivmJobMonitors.put(instantVMJobUUID, monitor);
				logger.info(String.format("Recreated the IVM job Monitor with UUID=%s successfully.", instantVMJobUUID));
			}
		}
		return monitor;
	}
	
	public InstantVHDResult getInstantVHD(String uuid, int timeout) throws ServiceException{
		InstantVHDResult result = new InstantVHDResult();
		result.setJobUUID(uuid);
		List<InstantVHDInfo> vhdList = new ArrayList<InstantVHDInfo>();
		result.setVhdList(vhdList);
		if (StringUtil.isEmptyOrNull(uuid)) {
			result.setErrCode(-2);
			result.setErrString("Fail to start instant VHD job.");
			return result;
		}
		long currentTime = System.currentTimeMillis();
		do {
			IVMJobMonitor jobMonitor = getIVMJobMonitor(uuid);
			long jobPhase = jobMonitor.getJobPhase();
			if (jobPhase == InstantVMConfig.IVM_JOB_FAILED) {
				result.setErrCode(-2);
				result.setErrString("Fail to start InstantVMAgent.");
				InstantVMConfig ivmConfig = getInstantVMJobScript(uuid);
				if (ivmConfig != null) {
					List<InstantVMNode> nodes = ivmConfig.getInstantVMNodes();
					if (nodes.size() > 0) {
						InstantVMNode node = nodes.get(0);
						VMInfo vmInfo = node.getVmInfo();
						if (vmInfo != null)
							result.setErrString(vmInfo.getLastErrorMessage());
					}
				}
				break;
			} else if (jobPhase == InstantVMConfig.IVM_JOB_RUNNING) {
				InstantVMConfig ivmConfig = getInstantVMJobScript(uuid);
				if (ivmConfig == null) {
					result.setErrCode(-3);
					result.setErrString(String.format("No corresponding script file for Instant VHD job %s", uuid));
					break;
				} else {
					List<InstantVMNode> nodes = ivmConfig.getInstantVMNodes();
					if (nodes.size() > 0) {
						InstantVMNode node = nodes.get(0);
						VMInfo vmInfo = node.getVmInfo();
						if (vmInfo != null) {
							result.setUEFI(vmInfo.isUEFI());
							List<DiskFileInfo> disks = vmInfo.getSnapshotDiskFileInfos();
							if (disks != null ) {
								for (DiskFileInfo diskFileInfo : disks) {
									InstantVHDInfo vhdInfo = new InstantVHDInfo();
									vhdInfo.setVhdFile(diskFileInfo.getDiskFilePath());
									vhdInfo.setDiskSize(diskFileInfo.getSize());
									vhdInfo.setBootVhd(diskFileInfo.isHasBootVolume());
									vhdInfo.setSysVhd(diskFileInfo.isHasSystemVolume());
									vhdList.add(vhdInfo);
								}
								result.setErrCode(0);
								break;
							}
						}
					}
				}
				result.setErrCode(-4);
				result.setErrString(String.format("Some critical information is missing in instant VHD job script %s.", uuid));
				break;
			} else {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					logger.error(e);
				}
				long timeSpan = System.currentTimeMillis() - currentTime;
				if (timeSpan > (long) timeout) {
					result.setErrCode(-5);
					result.setErrString("Time out to retrieve instant virtual disk information.");
					break;
				} else {
					continue;
				}
			}	
		} while (true);
		
		return result;
	}
	
	public String startInstantVM(InstantVMConfig para) throws ServiceException {
		// Step 0: Generate uuid and job id
		logger.info("Start instant VM for node " + para.getNodeName());
		logger.info("Generate UUID and jobID");
		
		NativeFacade nativeFacade = getNativeFacade();
		// Check if proxy server is 64-bit machine
		long hypervisor_type = para.getHypervisorType() == HypervisorType.HYPERV ? PreCheckInstantVMProxyModel.HYPERVISOR_HYPERV : PreCheckInstantVMProxyModel.HYPERVISOR_VMWARE;
		
		PreCheckInstantVMProxyModel precheckProxyStruct = new PreCheckInstantVMProxyModel(para.getVmConfigPath(), para.getNodeUUID(), para.getJobType(), -1, hypervisor_type);
		if (para.getNeedInstallNFSFeature()){
			// ignore the NFS Check.
			precheckProxyStruct.precheckCriteria.setAllBitMaskWithoutNFSCheck();
		}
		
		StringBuilder errMsg = new StringBuilder();
		ArrayList<String> warningList = new ArrayList<String>();
		if (nativeFacade.isInstantVMProxyMeetRequirement(precheckProxyStruct, warningList, errMsg) != 0) {
			logger.error(errMsg);
			throw new ServiceException(errMsg.toString(), FlashServiceErrorCode.INSTANT_VM_PROXY_NOT_64BIT);
		}

		long jobID = -1;
		try{
			jobID = nativeFacade.getJobID();
		}catch (Exception e){
			logger.error( "Error retreiving new job ID.", e );
		}
		
		String ivmJobUUID = java.util.UUID.randomUUID().toString().toUpperCase();
		
		ivmJobUUID = "{" + ivmJobUUID + "}";
		
		logger.info("Start IVM UUID: " + ivmJobUUID);
		logger.info("Start IVM job ID: " + jobID);
		para.setIVMJobUUID(ivmJobUUID);
		para.setStartJobId(jobID);

		// Step 2: Install VDDK driver
		if (para.getHypervisorType() == HypervisorType.VMWARE) {
			logger.info("Install VDDK driver.");
			installVDDKService();
		}

		dumpJobStatus(ivmJobUUID);
		
		//SessionInfo sessInfo = new SessionInfo(para.getInstantVMNodes().get(0).getSessionInfo());
		//Tungsten does not support multiple instant vm nodes in one script file.
		//if we want to support multiple instant vm , we must chage code here.
		SessionInfo sessInfo = new SessionInfo(para.getInstantVMNodes().get(0).getSessionInfo());
		
		if (para.getHypervisorType() == HypervisorType.VMWARE) {
			String version = GetVirtualMachineVersion(sessInfo.getBackupDestination(),
					sessInfo.getSessionNum(),sessInfo.getUserName(),sessInfo.getPassword());
			
			para.getInstantVMNodes().get(0).getVmInfo().setVmVersion(version);
		}
		
		// Step 4: Save to xml file
		para.setJobStatus(InstantVMConfig.IVM_JOB_INIT); 
		String path = UpdateJobInfo(para, jobID, para.getJobType());
		if (path == null) {
			logger.error("Failed to update job info");
		}
		addJobHistory(nativeFacade, para, true);
		
		long ret = 0;
		
		// Step 5: Submit to RPS server if the session is on RPS, if the session is on a share folder, do not submit to RPS server
		if (sessInfo.isRPSSession()) {
			// TODO: Submit to RPS
			IRPSService4D2D client = RPSServiceProxyManager.getRPSServiceClient(sessInfo.getBackupOperatorHostname(), sessInfo.getBackupOperatorUserName(), 
					sessInfo.getBackupOperatorPassword(), (int)sessInfo.getBackupOperatorPort(), sessInfo.getBackupOperatorProtocol(), sessInfo.getRpsDatastoreUUID());
			IVMJobArg arg = new IVMJobArg();
			arg.setInstantVMConfig(para);
			arg.setLocalD2DName(para.getProxyHostname());
			arg.setLocalD2DPort(CommonService.getInstance().getServerPort());
			arg.setLocalD2DProtocol(CommonService.getInstance().getServerProtocol());
			arg.setD2dLoginUUID(CommonService.getInstance().getLoginUUID());
			logger.info("[IVM]Submit Start IVM Job: "+para.getIVMJobUUID());
			logger.info("[IVM]LocalD2DName: "+arg.getLocalD2DName());
			logger.info("[IVM]D2dLoginUUID: "+arg.getD2dLoginUUID());
			ret = client.RPSSubmitInstantVM(arg);
			Register4RPS(sessInfo, ivmJobUUID, true);
		} else {
			ret = Start(para, path);
		}
		dumpJobStatus(ivmJobUUID);
		return ret == 0 ? ivmJobUUID : null;
	}
	
	public String GetVirtualMachineVersion(String rootPath, String sessionNum, String userName, String password)
	{
		CAVirtualInfrastructureManager vmwareOBJ = new CAVMwareVirtualInfrastructureManager();
		Lock lock = null;
		String sessionPath = null;
		
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(rootPath);
			if(lock != null) {
				lock.lock();
			}
			
			int index = 0;
			int len = sessionNum.length();
			char strs[] = sessionNum.toCharArray();
			for(int i = 1; i < len; i++)
			{
				if('0'!=strs[i])
				{
					index=i;
					break;
				}
			}
			String strLast = sessionNum.substring(index);
			this.getNativeFacade().NetConn(userName, password, rootPath);
			sessionPath = WSJNI.getSessPathByNo(rootPath, Integer.parseInt(strLast));
			String version = vmwareOBJ.getSourceVMVersionFromSession(sessionPath);
			
			return version;
		} catch (Exception e) {
			logger.error("Get source vm version failed", e);
			return "";
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
			
			if(lock != null){
				lock.unlock();
			}
			
			try {
				this.getNativeFacade().disconnectRemotePath(sessionPath, "", userName, password, false);
			} catch (Exception e){
				logger.error("Disconnect " + sessionPath + " failed");
			}
		}
	}
	
	public void Register4RPS(SessionInfo sessInfo, String ivmJobUUID, boolean addOrRemove) {
		FlashListenerInfo listener = new FlashListenerInfo();
		listener.setWsdlURL(RPSServiceProxyManager.makeRPSServiceURL(sessInfo.getBackupOperatorHostname(), sessInfo.getBackupOperatorProtocol(), (int)sessInfo.getBackupOperatorPort(), RPSWebServiceFactory.wsdl4D2D));
		listener.setType(FlashListenerInfo.ListenerType.RPS);
		listener.setUuid(SettingsService.instance().getRpsServerUUID(sessInfo.getBackupOperatorHostname(), 
				sessInfo.getBackupOperatorUserName(), sessInfo.getBackupOperatorPassword(), sessInfo.getBackupOperatorProtocol(), (int)sessInfo.getBackupOperatorPort()));
		
		if (addOrRemove) {  // Add
			ListenerManager.getInstance().addFlashListenerInstUUID(listener, ivmJobUUID);
		} else {  // Remove
			ListenerManager.getInstance().removeFlashListenerInstUUID(listener, ivmJobUUID);
		}
	}
	
	public boolean isAgentExist(String instantVMJobUUID) {
		NativeFacade nativeFacade = getNativeFacade();
		if (!nativeFacade.isIVMAgentExist(instantVMJobUUID)) {
			logger.debug(String.format("No corresponding IVM agent for %s", instantVMJobUUID));
			return false;
		}
		return true;
	}
	
	public long Start(InstantVMConfig para, String path) {
		logger.info("Start instant vm, JobUUID=" + para.getIVMJobUUID());
		long ret = 0;
		try {
			ret = getNativeFacade().startInstantVM(path, 0);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = -1;
		} finally {
			int jobStatus = JobStatus.JOBSTATUS_FINISHED;
			if (ret != 0) {
				logger.error("Failed to start instant vm");
				jobStatus = JobStatus.JOBSTATUS_FAILED;
				completeIVMJob(para.getIVMJobUUID(), jobStatus);
			} else {
				logger.info("Success to start instant vm, JobUUID=" + para.getIVMJobUUID());
			}
		}
		return ret;
	}
	
	public InstantVMStatus GetIVMStatus(String instantVMJobUUID) {
		InstantVMStatus status = new InstantVMStatus();		
		IVMJobMonitor ivmJobMonitor = getIVMJobMonitor(instantVMJobUUID);
		if (ivmJobMonitor == null){
			logger.error(String.format("There is no corresponding IVM job for %s", instantVMJobUUID));
			status.setJobState(JobState.Unknown);
			return status;
		}
		
		InstantVMConfig ivmConfig  = getIVMJob(instantVMJobUUID);
		if (null == ivmConfig) {
			logger.error(String.format("The corresponding IVM Config file for %s does not exist", instantVMJobUUID));
			status.setJobState(JobState.Unknown);
			return status;
		}
		
		
		// If the vm has not been created, the vm uuid has to be loaded from the job script.
		try {
			for (InstantVMNode node : ivmConfig.getInstantVMNodes()) {
				if (StringUtil.isEmptyOrNull(node.getVmInfo().getVmUUID())) {
					InstantVMConfig currentJobScript = getInstantVMJobScript(instantVMJobUUID);
					if (currentJobScript != null) {
						ivmConfig = currentJobScript;
						setIVMJob(ivmConfig);
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		 	
		status.setJobUUID(instantVMJobUUID);
		status.setJobID(ivmJobMonitor.getJobId());
		
		if (ivmJobMonitor.getJobPhase() == InstantVMConfig.IVM_JOB_INIT)
			return null;
		else if (ivmJobMonitor.getJobStatus() == JobStatus.JOBSTATUS_CRASH) {
			status.setJobState(JobState.Crash);
			return status;
		} else if (ivmJobMonitor.getJobStatus() == JobStatus.JOBSTATUS_FAILED) {
			status.setJobState(JobState.Error);
			return status;
		} 
		
		List<InstantVmNodeStatus> nodesStatus = new ArrayList<InstantVmNodeStatus>();
		status.setNodesStatus(nodesStatus);
		
		for (InstantVMNode node : ivmConfig.getInstantVMNodes()) {
			InstantVmNodeStatus nodeStatus = new InstantVmNodeStatus();
			nodesStatus.add(nodeStatus);
			nodeStatus.setHearbeatState(HearbeatState.Failure);
			HypervisorType hyperVType = ivmConfig.getHypervisorType();
			nodeStatus.setNodeUUID(node.getNodeUUID());
			if (status.getJobState() == JobState.Crash)
				return status;
			
			nodeStatus.setSessionNum(node.getSessionInfo().getSessionNum());
			nodeStatus.setBackupDestination(node.getSessionInfo().getBackupDestination());
			String ivmName = node.getVmInfo().getVmDisplayName();
			String ivmUUID = node.getVmInfo().getVmUUID();
			nodeStatus.setIvmName(ivmName);
			nodeStatus.setIvmUUID(ivmUUID);
			List<Long> sizes = new ArrayList<Long>();
			NativeFacade nativeFacade = getNativeFacade();
			int err = nativeFacade.getVolumeSize(ivmConfig.getVmConfigPath(), sizes); 
			if (err == 0) {
				if (sizes.size() == 2) { 
					long capacity = sizes.get(1).longValue();
					nodeStatus.setDataStoreCapacity(capacity);
					nodeStatus.setUsedSpace(capacity - sizes.get(0).longValue());
				} else {
					logger.error("JNI error. The returned 'sizes' of getVolumeSize is not correct.");
				}
			} else {
				logger.error(String.format("Fail to get data store size: %d", err));
			}
			if (StringUtil.isEmptyOrNull(ivmUUID)) {
				// At this time, the IVM may not be created.
				nodeStatus.setState(VMState.Unknown);
				continue;
			}
			
			if (hyperVType == HypervisorType.VMWARE) {
				CAVirtualInfrastructureManager vmwareOBJ = null;
				try {
					vmwareOBJ = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(
							ivmConfig.getHypervisorInfo().getHostname(), 
							ivmConfig.getHypervisorInfo().getUserName(), 
							ivmConfig.getHypervisorInfo().getPassword(),
						    ((VMWareInfo) ivmConfig.getHypervisorInfo()).getProtocol(), 
						    true,
						    ((VMWareInfo) ivmConfig.getHypervisorInfo()).getPort());
					
					if (!vmwareOBJ.isVMExistWithUuid(ivmUUID, ivmName)) {
						nodeStatus.setState(VMState.NotExist);
					} else {
                        VM_Info vmInfo = vmwareOBJ.getVMInfo(ivmName,ivmUUID);
						
						nodeStatus.setMemory(vmInfo.getMemoryMB());
						nodeStatus.setCpuNum(vmInfo.getCpuSocketCount());
						
						powerState power = (vmInfo.getVMPowerstate()== true ? powerState.poweredOn:powerState.poweredOff);
						switch (power) {
							case poweredOn :{
								nodeStatus.setState(VMState.PowerOn);
								if (vmwareOBJ.checkVMToolsVersion(ivmName, ivmUUID) != 0) {
									nodeStatus.setHearbeatState(HearbeatState.Success);
								}
								break;
							}
								
							case poweredOff : 
								nodeStatus.setState(VMState.PowerOff);
								break;
							default:
								nodeStatus.setState(VMState.Unknown);
								break;
						}						
					}
				}catch (Exception e) {
					logger.error(String.format("Fail to query instant VM status: %s", ivmName), e);
					nodeStatus.setState(VMState.Abnormal);
					return status;
				}finally {
					if (vmwareOBJ != null) {
						try {
							vmwareOBJ.close();
						}
						catch (Exception e) {
						}
					}
				}
			} else if (hyperVType == HypervisorType.HYPERV) {
				long handle = 0;
				try {
					handle = HyperVJNI.OpenHypervHandle("", "", "");
					if (!nativeFacade.isHyperVVmExist(ivmUUID, handle)) {
						nodeStatus.setState(VMState.NotExist);
					} else {
	                    JHypervVMInfo vmInfo = WSJNI.getHypervVMInfo(handle, ivmUUID);
						
						nodeStatus.setMemory(vmInfo.getVmMemoryMB());
						nodeStatus.setCpuNum(vmInfo.getVmCpuNum());
						
						int power = vmInfo.getVmPowerStatus();
						switch (power) {
							case 3 :
								nodeStatus.setState(VMState.PowerOff);
								break;
							case 2 : {
								nodeStatus.setState(VMState.PowerOn);
								long vmTool = getNativeFacade().checkHyperVVMToolVersion(handle, ivmUUID);
								if (vmTool == 2 || vmTool == 1) {
									nodeStatus.setHearbeatState(HearbeatState.Success);
								}
								break;
							}
							default:
								nodeStatus.setState(VMState.Unknown);
								break;
						}
					}
				} catch (ServiceException e) {
					logger.error(String.format("Fail to query instant VM status: %s", ivmName), e);
					nodeStatus.setState(VMState.Abnormal);
					return status;
				} catch (HyperVException e) {
					logger.error(String.format("Fail to query instant VM status: %s", ivmName), e);
					return null;
				}
				finally {
					try {
						if (0 != handle) {
							HyperVJNI.CloseHypervHandle(handle);
						}
					}
					catch (HyperVException hyperVe) {
					}
				}
				
			} else {
				logger.error(String.format("Not supported hypervisor type %s.", hyperVType.name()));
			}
		}
		return status;
	}
	
	public long PowerOnIvm(String instantVMJobUUID, String ivmUUID) {
		return powerOnOrOffIvm(instantVMJobUUID, ivmUUID, true);
	}
	
	public long PowerOffIvm(String instantVMJobUUID, String ivmUUID) {
		return powerOnOrOffIvm(instantVMJobUUID, ivmUUID, false);
	}
	
	private long powerOnOrOffIvm(String instantVMJobUUID, String ivmUUID, boolean isPoweron) {
		InstantVMConfig ivmConfig = getInstantVMJobScript(instantVMJobUUID);
		if (ivmConfig == null) {
			logger.error(String.format("No corresponding IVM job for %s", instantVMJobUUID));
			return -1;
		}
		InstantVMNode node = null;
		List<InstantVMNode> nodes = ivmConfig.getInstantVMNodes();
		if (nodes.size() == 1) {
			node = nodes.get(0);
		} else {
			for (InstantVMNode item : nodes) {
				String vmUUID = item.getVmInfo().getVmUUID();
				if (StringUtil.equals(vmUUID, ivmUUID)) {
					node = item;
					break;
				}
			}
		}
		if (node == null) {
			logger.error(String.format("No corresponding instant VM for %s", ivmUUID));
			return -1;
		}
		ivmUUID = node.getVmInfo().getVmUUID();
		String ivmName = node.getVmInfo().getVmDisplayName();
		
		HypervisorType hyperVType = ivmConfig.getHypervisorType();
		if (hyperVType == HypervisorType.VMWARE) {
			CAVirtualInfrastructureManager vmwareOBJ = null;
			try {
				vmwareOBJ = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(
						ivmConfig.getHypervisorInfo().getHostname(), 
						ivmConfig.getHypervisorInfo().getUserName(), 
						ivmConfig.getHypervisorInfo().getPassword(),
					    ((VMWareInfo) ivmConfig.getHypervisorInfo()).getProtocol(), 
					    true,
					    ((VMWareInfo) ivmConfig.getHypervisorInfo()).getPort());
				if (isPoweron) {
					vmwareOBJ.powerOnVM(ivmName, ivmUUID);
				} else {
					vmwareOBJ.shutdownVM(ivmUUID, ivmUUID);
				}
			}catch (Exception e) {
				logger.error(String.format("Fail to power %s instant VM.", (isPoweron? "on" : "off")), e);
				return -1;
			}finally {
				if (vmwareOBJ != null) {
					try {
						vmwareOBJ.close();
					}
					catch (Exception e) {
					}
				}
			}
		} else if (hyperVType == HypervisorType.HYPERV) {
			long handle = 0;
			try {
				handle = HyperVJNI.OpenHypervHandle("", "", "");
				if (isPoweron) {
					HyperVJNI.PowerOnVM(handle, ivmUUID);
				} else {
					HyperVJNI.ShutdownVM(handle, ivmUUID);
				}
			} catch (HyperVException e) {
				logger.error(String.format("Fail to power %s instant VM.", (isPoweron? "on" : "off")), e);
				return -1;
			}
			finally {
				try {
					if (0 != handle) {
						HyperVJNI.CloseHypervHandle(handle);
					}
				}
				catch (HyperVException hyperVe) {
				}
			}
		} else {
			logger.error(String.format("Not supported hypervisor type %s.", hyperVType.name()));
			return -1;
		}
		logger.info(String.format("Power %s intant VM successfully.", (isPoweron? "on" : "off")));
		return 0;
	}
	
	private String UpdateJobInfo(InstantVMConfig ivmConfig, long jobID, int jobType) {	
		File xmlLocation = new File(INSTANT_VM_CONFIG_PATH);
		if(!xmlLocation.exists()){
			xmlLocation.mkdirs();
		}

		String xmlFileName = ivmConfig.getIVMJobUUID() + INSTANT_VM_JOB_FILENAME_EXTENSION;
		
		logger.info("Save to xml file: " + xmlFileName);
		FileOutputStream fos = null;
		
		ivmConfig.setJobID(jobID);
		ivmConfig.setJobType(jobType);
		ivmConfig.setStartTime(System.currentTimeMillis());
		
		enOrDecrypt(ivmConfig, true);
		
		try {
			fos = new FileOutputStream(new File(xmlLocation, xmlFileName));
			JAXB.marshal(ivmConfig, fos);
			logger.info("Save to xml file successfully, file name is " + xmlFileName);
			return xmlLocation.getAbsolutePath() + "\\" + xmlFileName;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			return null;
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	private void UpdateJobScriptInfoByJobMonitor(IVMJobMonitor jobMonitor) {
		InstantVMConfig ivmConfig = getInstantVMJobScript(jobMonitor.getJobUUID());
		if (null== ivmConfig){
			logger.error(String.format("Failed to update the jobscript, JobUUID=%s.", jobMonitor.getJobUUID()));
			return;
		}
		ivmConfig.setIsMarkStartJobEnd(jobMonitor.isMarkStartJobEnd());
		ivmConfig.setIsMarkFailedDeleted(jobMonitor.isMarkFailedDeleted());
		ivmConfig.setIsMarkStopJobEnd(jobMonitor.isMarkStopJobEnd());
		ivmConfig.setStopJobId(jobMonitor.getStopJobId());
		ivmConfig.setStartJobId(jobMonitor.getStartJobId());
		UpdateJobInfo(ivmConfig, ivmConfig.getJobID(), ivmConfig.getJobType());
	}
	
	
	public long stopInstantVM(String instantVMJobUUID, boolean delete) throws ServiceException {
		logger.info("Stop instant VM job " + instantVMJobUUID);
		InstantVMConfig ivmConfig = getInstantVMJobScript(instantVMJobUUID);
		if (ivmConfig != null && isIVMCloningOrMigrating(ivmConfig)) {
			String jobType = "";
			if (ivmConfig.getJobType() == InstantVMConfig.ASSURANCE_RECOVERY_JOB)
				jobType = WebServiceMessages.getResource("AssuranceRecoveryJob");
			else
				jobType = WebServiceMessages.getResource("InstantVM");

			String msg = WebServiceErrorMessages.getServiceError(FlashServiceErrorCode.INSTANT_VM_BEING_MIGRATED, new String[] { jobType });
			throw new ServiceException(msg, FlashServiceErrorCode.INSTANT_VM_BEING_MIGRATED);
		}
		
		IVMJobMonitor ivmJobMonitor = getIVMJobMonitor(instantVMJobUUID);
		if (ivmJobMonitor != null
				&& ivmConfig.getJobResCleared()
				&& ivmJobMonitor.getJobPhase() == InstantVMConfig.IVM_JOB_FAILED) {
			ivmJobMonitor.setMarkFailedDeleted(true);
			logger.info(String.format("The resources assigned to IVM Job(UUID = %s, JobId=%s) has been been Deleted.", 
					instantVMJobUUID, ivmJobMonitor.getStartJobId()+""));
			return 0;
		}
		else if (null == ivmJobMonitor && ivmConfig.getJobResCleared())
		{
			logger.error(String.format("IVM Job Monitor(UUID = %s) does not exist, presumably the service having been restarted.", instantVMJobUUID));
			removeJobScript(instantVMJobUUID);
			return 0;
		}
		else if (null == ivmJobMonitor)
		{
			logger.error(String.format("Probably having been deleted, IVM Job Monitor(UUID = %s) and Script do not exist any more!", instantVMJobUUID));
			return 0;
		}
		
		long ret = 0;
		try {
			long jobID = ivmConfig.getJobID();;
			
			// The extra logic follows in case of multiple stopping action triggered by the user.
			synchronized (ivmJobMonitor) {

				if (ivmJobMonitor.getStopJobId() != 0) {
					logger.error(String.format("The previous Instant-VM-Stop Job %s with Job UUID=%s is in process!", ivmJobMonitor.getStopJobId() + "",
					             instantVMJobUUID));
					return 0;
				}

				/*
				 * For instant VM, Start & Stop are two jobs, so if the original
				 * job is Start IVM, we need to update the job information, and
				 * add a new job to the job history, if the original job is AR
				 * job, we can skip it.
				 */
				
				if (ivmConfig.getJobType() == InstantVMConfig.START_INSTANT_VM_JOB) {
					try {
						jobID = getNativeFacade().getJobID();
					} catch (Exception e) {
						logger.error("Error retreiving new job ID.", e);
					}

					logger.info("Stop IVM UUID: " + instantVMJobUUID);
					logger.info("Stop IVM job ID: " + jobID);
					ivmConfig.setStopJobId(jobID);
					UpdateJobInfo(ivmConfig, jobID, InstantVMConfig.STOP_INSTANT_VM_JOB);
					addJobHistory(getNativeFacade(), ivmConfig, false);
				}

				if (getRegFlag("StopSleep")) {
					try {
						Thread.sleep(1000 * 30);
					} catch (InterruptedException e1) {
						logger.error(e1);
					}
				}
			}

			ret = getNativeFacade().stopInstantVM(instantVMJobUUID, jobID, InstantVMConfig.STOP_INSTANT_VM_JOB, delete);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = -1;
		} finally {
		}
		return ret;
	}
	
	public long startHydration(String instantVMJobUUID) {
		long ret = 0;
		try {
			ret = getNativeFacade().startHydration(instantVMJobUUID);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return ret;
	}
	
	
	private void addActivityLogDetails(long level, long jobID, int jobType, String nodeUUID, 
			long resourceID, String[] msg) {

		try {
			NativeFacade nativeFacade = BackupService.getInstance()
					.getNativeFacade();

			logger.info("Adding IVM activity log [" + msg[0] + "], jobID = " + jobID);

			JActLogDetails logDetails = new JActLogDetails();
			logDetails.setProductType(1); // APT_D2D
			logDetails.setJobID(new Long(jobID).intValue());
			logDetails.setJobType(jobType);
			logDetails.setJobMethod(0);
			logDetails.setLogLevel(new Long(level).intValue());
			logDetails.setIsVMInstance(true);
			logDetails.setAgentNodeID(nodeUUID);
			
			logDetails.setSvrNodeName("");
			logDetails.setSvrNodeID("");
			logDetails.setAgentNodeName("");
			logDetails.setSourceRPSID("");
			logDetails.setTargetRPSID("");
			logDetails.setDSUUID("");
			logDetails.setTargetDSUUID("");

			nativeFacade.addLogActivityWithDetailsEx(logDetails, resourceID, msg);
		} catch (Exception e) {
			logger.error("Failed to add activity log:" + e.getMessage(), e);
		}
	}
	
	private void AddActivityLog(long level, long jobID, int jobType, String nodeUUID, String resID) {
		String msg = ReplicationMessage.getResource(resID);
		addActivityLogDetails(level, jobID, jobType, nodeUUID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "", "", "", "" });
	}
	
	public long stopHydration(String instantVMJobUUID) {
		long ret = 0;
		try {
			ret = getNativeFacade().stopHydration(instantVMJobUUID);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return ret;
	}
	
	public int pauseMerge(String afGuid) {
		if (HACommon.isTargetPhysicalMachine(afGuid)) {
			try {
				return MergeService.getInstance().pauseMerge(
						AbstractMergeService.MergeEvent.OTHER_JOB_START, null, this);
			} catch (ServiceException e) {
				logger.error(e);
			}
		} else {
			try {
				return VSphereMergeService.getInstance().pauseMerge(
						AbstractMergeService.MergeEvent.OTHER_JOB_START, afGuid, null, this);
			} catch (ServiceException e) {
				logger.error(e);
			}
		}
		return -1;
	}
	
	public int resumeMerge(String afGuid) {
		if (HACommon.isTargetPhysicalMachine(afGuid)) {
			MergeService.getInstance().jobEnd(this);
			try {
				return MergeService.getInstance().resumeMerge(
						AbstractMergeService.MergeEvent.OTHER_JOB_START);
			} catch (ServiceException e) {
				logger.error(e);
			}
		} else {
			VSphereMergeService.getInstance().jobEnd(afGuid, this);
			try {
				return VSphereMergeService.getInstance().resumeVMMerge(
						AbstractMergeService.MergeEvent.OTHER_JOB_START, afGuid);
			} catch (ServiceException e) {
				logger.error(e);
			}
		}
		return -1;
	}
	
	protected int pauseMerge(boolean isRPS, String afGuid, String hostName,
			String userName, String password, int port, String protocol,
			String loginUUID) throws ServiceException {
		if (isRPS) {
			IRPSService4D2D service = RPSServiceProxyManager
					.getRPSServiceClient(hostName, userName, password, port, protocol);
			return service.pauseMerge(MergeAPISource.D2D_JOBS, afGuid);
		} else {
			WebServiceClientProxy proxy = HAService.getInstantVMService(
					loginUUID, hostName, port, protocol);
			return proxy.getInstantVMService().pauseMerge(afGuid);
		}
	}

	protected int resumeMerge(boolean isRPS, String afGuid, String hostName,
			String userName, String password, int port, String protocol,
			String loginUUID) throws ServiceException {
		if (isRPS) {
			IRPSService4D2D service = RPSServiceProxyManager
					.getRPSServiceClient(hostName, userName, password, port,
							protocol);
			return service.resumeMerge(MergeAPISource.D2D_JOBS, afGuid);
		} else {
			WebServiceClientProxy proxy = HAService.getInstantVMService(
					loginUUID, hostName, port, protocol);
			return proxy.getInstantVMService().resumeMerge(afGuid);
		}
	}

	protected void addJobHistory(NativeFacade nativeFacade, InstantVMConfig para, boolean isStart) {
		logger.info(String.format("Adding Instant VM job history, Job Id=%s, JobUUID=%s, isStart=%s.", para.getJobID()+"", para.getIVMJobUUID(), isStart+""));
		boolean isPhysicalMachine = HACommon.isTargetPhysicalMachine(para.getNodeUUID());
		try {
			JJobHistory jobHistory = new JJobHistory();
			jobHistory.setJobId(para.getJobID());
			jobHistory.setJobType(para.getJobType());
			jobHistory.setTargetDatastoreUUID(para.getInstantVMNodes().get(0).getSessionInfo().getRpsDatastoreUUID());
			jobHistory.setTargetUUID(para.getInstantVMNodes().get(0).getSessionInfo().getBackupOperatorUUID());
			
			if(!isPhysicalMachine) {
				jobHistory.setJobDisposeNodeUUID(para.getNodeUUID());
				jobHistory.setJobDisposeNode(para.getNodeName());				
			} 
			nativeFacade.updateJobHistory( jobHistory );			
		} catch (Exception e) {
			logger.error("Fail to add Instant VM job history.", e);
		}

		IVMJobMonitor jobMonitor = getIVMJobMonitor(para.getIVMJobUUID());
		if (jobMonitor == null)
		{
			jobMonitor = addIVMJobMonitor(para.getIVMJobUUID());
			logger.info(String.format("Job Monitor %s has been created.", para.getIVMJobUUID()));
		}
		synchronized (jobMonitor) {
			constructIvmJobMonitor(para, jobMonitor);
			jobMonitor.setJobId(para.getJobID());
			if (isStart)
			{
				jobMonitor.setStartJobId(para.getJobID());
				jobMonitor.setMarkStartJobEnd(false);      // for restart case
			}
			else
			{
				jobMonitor.setStopJobId(para.getJobID());
			}
			jobMonitor.setFinished(false);
			jobMonitor.setJobStatus(JobStatus.JOBSTATUS_ACTIVE);
			
			UpdateJobScriptInfoByJobMonitor(jobMonitor);
		}		
		setIVMJob(para);
		if (!isPhysicalMachine){
			ActivityLogSyncher.getInstance().addVM(para.getNodeUUID());
		}
	}
	
	
	private void markJobHistory(IVMJobMonitor jobMonitor) {
		try {
			if (jobMonitor.getJobPhase() == InstantVMConfig.IVM_JOB_CANCELLED && !jobMonitor.isMarkStartJobEnd()){
				getNativeFacade().markD2DJobEnd(jobMonitor.getStartJobId(),JobStatus.JOBSTATUS_CANCELLED, null);
				jobMonitor.setMarkStartJobEnd(true);
				logger.info(String.format("Mark InstatVM-StartJob: JobUUID=%s, JobId=%s 'Cancelled'!", 
						jobMonitor.getJobUUID(), jobMonitor.getStartJobId()+""));
				UpdateJobScriptInfoByJobMonitor(jobMonitor);
			}else if ((jobMonitor.getJobPhase() == InstantVMConfig.IVM_JOB_RUNNING ||
					   jobMonitor.getJobPhase() == InstantVMConfig.IVM_JOB_ERROR_RECOVERABLE) && !jobMonitor.isMarkStartJobEnd()){
				getNativeFacade().markD2DJobEnd(jobMonitor.getStartJobId(),JobStatus.JOBSTATUS_FINISHED, null);
				jobMonitor.setMarkStartJobEnd(true);
				logger.info(String.format("Mark InstatVM-StartJob: JobUUID=%s, JobId=%s 'Finished'!", 
						jobMonitor.getJobUUID(), jobMonitor.getStartJobId()+""));
				UpdateJobScriptInfoByJobMonitor(jobMonitor);
			}
			else if (jobMonitor.getJobStatus() == JobStatus.JOBSTATUS_FAILED && !jobMonitor.isMarkStartJobEnd()){
				getNativeFacade().markD2DJobEnd(jobMonitor.getStartJobId(),JobStatus.JOBSTATUS_FAILED, null);
				jobMonitor.setMarkStartJobEnd(true);
				logger.info(String.format("Mark InstatVM-StartJob: JobUUID=%s, JobId=%s 'Failed'!", 
						jobMonitor.getJobUUID(), jobMonitor.getStartJobId()+""));
				UpdateJobScriptInfoByJobMonitor(jobMonitor);
			}
			else if (jobMonitor.getJobStatus() == JobStatus.JOBSTATUS_FINISHED && !jobMonitor.isMarkStopJobEnd()){
				if (!jobMonitor.isMarkStartJobEnd())
				{
					getNativeFacade().markD2DJobEnd(jobMonitor.getStartJobId(),JobStatus.JOBSTATUS_FINISHED, null);
					jobMonitor.setMarkStartJobEnd(true);
					logger.info(String.format("Mark InstatVM-StartJob: JobUUID=%s, JobId=%s 'Finished'!", 
							jobMonitor.getJobUUID(), jobMonitor.getStartJobId()+""));
				}
				getNativeFacade().markD2DJobEnd(jobMonitor.getStopJobId(),JobStatus.JOBSTATUS_FINISHED, null);
				jobMonitor.setMarkStopJobEnd(true);
				logger.info(String.format("Mark InstantVM-StopJob JobUUID=%s, JobId=%s 'Stopped'!", 
						jobMonitor.getJobUUID(), jobMonitor.getStopJobId()+""));
				UpdateJobScriptInfoByJobMonitor(jobMonitor);
			}
			else if (jobMonitor.getJobStatus() == JobStatus.JOBSTATUS_CRASH) {
				// Starting or Stopping Job crashed.
				if ((jobMonitor.getJobPhase() == InstantVMConfig.IVM_JOB_START || jobMonitor.getJobPhase() == InstantVMConfig.IVM_JOB_CREATED) &&
						!jobMonitor.isMarkStartJobEnd()){
					getNativeFacade().markD2DJobEnd(jobMonitor.getStartJobId(),JobStatus.JOBSTATUS_FAILED, null);
					jobMonitor.setMarkStartJobEnd(true);
					logger.info(String.format("Mark InstatVM-StartJob: JobUUID=%s, JobId=%s 'Failed(Crashed)'!", 
							jobMonitor.getJobUUID(), jobMonitor.getStartJobId()+""));
					UpdateJobScriptInfoByJobMonitor(jobMonitor);
				}
				else if (jobMonitor.getJobPhase() == InstantVMConfig.IVM_JOB_STOPPING && !jobMonitor.isMarkStopJobEnd()){
					getNativeFacade().markD2DJobEnd(jobMonitor.getStopJobId(),JobStatus.JOBSTATUS_FAILED, null);
					jobMonitor.setMarkStopJobEnd(true);
					logger.info(String.format("Mark InstatVM-StopJob: JobUUID=%s, JobId=%s 'Failed(Crashed)'!", 
							jobMonitor.getJobUUID(), jobMonitor.getStopJobId()+""));
					UpdateJobScriptInfoByJobMonitor(jobMonitor);
					toBeCleanJobList.add(jobMonitor);
				}
				
				if (jobMonitor.getPreviousJobStatus() != JobStatus.JOBSTATUS_CRASH) {
					long jobId = jobMonitor.getStartJobId();
					int jobType = (int)jobMonitor.getJobType();
					if (jobMonitor.getJobPhase() == InstantVMConfig.IVM_JOB_STOPPING){
						jobId = jobMonitor.getStopJobId();
					}
					AddActivityLog(Constants.AFRES_AFALOG_ERROR, jobId, jobType, jobMonitor.getNodeUUID(), ReplicationMessage.IVM_JOB_PROCESS_CRASH);
				}
			}
		}
		catch(Exception e) {
			logger.error(e);
		}
	}

	protected void completeIVMJob(String instantVMJobUUID, int jobStatus) {
		logger.info("Complete Instant VM job " + instantVMJobUUID);
		IVMJobMonitor jobMonitor = getIVMJobMonitor(instantVMJobUUID);
		if (jobMonitor != null) {
			synchronized (jobMonitor) {
				jobMonitor.setFinished(true);
				jobMonitor.setJobStatus(jobStatus);
			}
			removeIVMJobMonitor(instantVMJobUUID);
		}		
	}
	
	protected int installVDDKService(){
		return VDDKService.getInstance().install();
	}
	
	public void startMonitorIVMStatus() {
		try {
			ivmMonitorThreadLock.lock();
			if (null != ivmMonitorThread) {
				return;
			}

			ivmMonitorThread = new Thread(new Runnable() {
				@Override
				public void run() {
					logger.info("IVMStatus Monitor Thread has been started.");
					while (!monitorThreadStop) {
						try {
							monitorIVMJobStatus();
							Clean();
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							logger.error("Interrupted in ivm job monitor");
							break;
						} catch (Exception e) {
							logger.error("Fail to getInstantVMJobStatus.", e);
						}
					}
				}
			});
			CommonService.getInstance().getUtilTheadPool().submit(ivmMonitorThread);
		} finally {
			ivmMonitorThreadLock.unlock();
		}
	}
	
	protected void monitorIVMJobStatus() {
		toBeCleanJobList.clear();
		Map<String, InstantVMConfig> ivmJobs = getAllIVMJobs();
		for(Map.Entry<String, InstantVMConfig> entry : ivmJobs.entrySet()) {
			String ivmJobUUID = entry.getKey();
			if (StringUtil.isEmptyOrNull(ivmJobUUID))
				continue;
			// Get job status
			JIVMJobStatus jobStatus = getIVMJobStatus(ivmJobUUID);
			if (jobStatus == null)
				continue;
			
			// Set the job finished if the vm is running or failed to start ivm
			IVMJobMonitor jobMonitor = getIVMJobMonitor(ivmJobUUID);
			if (null == jobMonitor)
				continue;
			
			synchronized(jobMonitor) {
				jobMonitor.setJobPhase(jobStatus.getJobPhase());
				jobMonitor.setVmDisplayName(jobStatus.getVmDisplayName());
				jobMonitor.setElapsedTime(System.currentTimeMillis() - jobMonitor.getStartTime());

				int status = JobStatus.JOBSTATUS_ACTIVE;
				boolean isFinished = false;
				boolean isAgentExist = jobStatus.isAgentExist();
				if (isAgentExist) {
					if (jobStatus.getJobPhase() == InstantVMConfig.IVM_JOB_FAILED) {
						isFinished = true;
						status = JobStatus.JOBSTATUS_FAILED;
						jobMonitor.setJobPhase(InstantVMConfig.IVM_JOB_FAILED);
					}
				} 
				else {
					if (jobStatus.getJobPhase() == InstantVMConfig.IVM_JOB_FAILED) {
						isFinished = true;
						status = JobStatus.JOBSTATUS_FAILED;
						if (jobMonitor.isMarkFailedDeleted())
							toBeCleanJobList.add(jobMonitor); // job failed and the user has triggered a 'delete' action. 
					} else if (jobStatus.getJobPhase() == InstantVMConfig.IVM_JOB_STOPPED) {
						isFinished = true;
						status = JobStatus.JOBSTATUS_FINISHED;
						toBeCleanJobList.add(jobMonitor); //once job has finished , and then the process exits, the script will be deleted.
					} else if (jobStatus.getJobPhase() == InstantVMConfig.IVM_JOB_CANCELLED) {
						isFinished = true;
						status = JobStatus.JOBSTATUS_CANCELLED;
						toBeCleanJobList.add(jobMonitor); 
					} else if (jobStatus.getJobPhase() == InstantVMConfig.IVM_JOB_ERROR_RECOVERABLE) {
						// Although start-job succeeded, reading data caught error 
						isFinished = true;
						status = JobStatus.JOBSTATUS_FAILED;
					}
					else if (jobStatus.getJobPhase() != InstantVMConfig.IVM_JOB_INIT){
						isFinished = true;
						status = JobStatus.JOBSTATUS_CRASH;
					}
					logger.debug("JobGUID=" + ivmJobUUID + ", JobPhase=" + jobStatus.getJobPhase() + ", JobStatus=" + status + ", isAgentExist=" + isAgentExist + ", isFinished="+ isFinished);
				}
				jobMonitor.setFinished(isFinished);
				jobMonitor.setJobStatus(status);
				markJobHistory(jobMonitor);
				jobMonitor.setPreviousJobStatus(status);
			}
		}
	}
	
	protected void Clean() {
		try {
			if (toBeCleanJobList.size() == 0)
				return;
			for (IVMJobMonitor jobMonitor:toBeCleanJobList) {
				String ivmJobUUID = jobMonitor.getJobUUID();
				InstantVMConfig ivmConfig = getInstantVMJobScript(jobMonitor.getJobUUID());
				
				if (ivmConfig == null) {
					ivmConfig = getAllIVMJobs().get(ivmJobUUID);
				}
				
				SessionInfo sessInfo = ivmConfig.getInstantVMNodes().get(0).getSessionInfo();
				synchronized(jobMonitor)
				{
					logger.info(String.format("Cleaning JobMonitor with JobUUID %s.", ivmJobUUID));
					
					removeIVMJob(ivmJobUUID);
					removeIVMJobMonitor(ivmJobUUID);
					removeJobScript(ivmJobUUID);
				}
						
				if (sessInfo.isRPSSession()) {
					Register4RPS(sessInfo, ivmJobUUID, false);
				}
				getNativeFacade().CleanShareMemory(ivmJobUUID);
				logger.info("Delete IVMJob " + ivmJobUUID +" successfull!");
			}
		}
		catch (Exception e) {
			logger.error(e);
		}
	}
	
	public void removeJobScript(String instantVMJobUUID) {
		File jobScript = new File(INSTANT_VM_CONFIG_PATH + instantVMJobUUID + INSTANT_VM_JOB_FILENAME_EXTENSION);
		removeJobScript(jobScript);
	}
	
	public void removeJobScript(File jobScript) {
		
		if (getRegFlag("DebugFlag")) {       
			// Move file to new directory
	        jobScript.renameTo(new File(INSTANT_VM_LOG_PATH + jobScript.getName()));
			return;
		}
		
		boolean isDeleteSuc = jobScript.delete();
		logger.info("Delete job script " + jobScript.getName() +  ", return=" + isDeleteSuc + ".");
	}
	
	public InstantVMConfig getInstantVMJobScript(String instantVMJobUUID) {
		File xmlLocation = new File(INSTANT_VM_CONFIG_PATH + instantVMJobUUID + INSTANT_VM_JOB_FILENAME_EXTENSION);
		if (!xmlLocation.exists()) {
			logger.error("The job script does not exist. File name is "
					+ instantVMJobUUID + INSTANT_VM_JOB_FILENAME_EXTENSION);
			return null;
		}

		InstantVMConfig para = null;
		int i = 0;
		while (i < 10) {
			try {
				JAXBContext ctx = JAXBContext.newInstance(InstantVMConfig.class);
				para = (InstantVMConfig) ctx.createUnmarshaller().unmarshal(xmlLocation);
				if (para != null) {
					enOrDecrypt(para, false);
				}
				return para;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			i++;
		}
		return para;
	}
	
	private boolean isIVMCloningOrMigrating(InstantVMConfig ivmConfig) {

		boolean isVMGenerated = false;
		for (InstantVMNode node : ivmConfig.getInstantVMNodes()) {
			String vmUUID = node.getVmInfo().getVmUUID();
			if (!StringUtil.isEmptyOrNull(vmUUID)) {
				isVMGenerated = true;
				break;
			}
		}
		if (!isVMGenerated)
			return false;
		
		if (ivmConfig.getHypervisorType() == HypervisorType.VMWARE) {
			CAVirtualInfrastructureManager vmwareOBJ = null;
			try {
				vmwareOBJ = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(
						ivmConfig.getHypervisorInfo().getHostname(), 
						ivmConfig.getHypervisorInfo().getUserName(), 
						ivmConfig.getHypervisorInfo().getPassword(),
					    ((VMWareInfo) ivmConfig.getHypervisorInfo()).getProtocol(), 
					    true,
					    ((VMWareInfo) ivmConfig.getHypervisorInfo()).getPort());

				for (InstantVMNode node : ivmConfig.getInstantVMNodes()) {
					String vmUUID = node.getVmInfo().getVmUUID();
					if (StringUtil.isEmptyOrNull(vmUUID))
						continue;

					String vmDisplayName = node.getVmInfo().getVmDisplayName();
					try {
						List<String> tasks = vmwareOBJ.getVMQueuedTasks(vmDisplayName, vmUUID);
						for(String task : tasks) {
							logger.info(String.format("Task %s is queued or running on IVM %s.", task, vmDisplayName));
						}
						if (tasks.contains("VirtualMachine.relocate") 
								|| tasks.contains("VirtualMachine.migrate")
								|| tasks.contains("VirtualMachine.clone")) {
							logger.warn(String.format("The vm %s is moving or migrating storage.", vmUUID));
							return true;
						}
					} catch (Exception e) {
						logger.warn(String.format("Failed to get VM's queued tasks.", vmUUID), e);
					}
				}
			} catch (Exception e) {
			}
			finally {
				if (vmwareOBJ != null) {
					try {
						vmwareOBJ.close();
					}
					catch (Exception e) {
					}
				}
			}
		} else if (ivmConfig.getHypervisorType() == HypervisorType.HYPERV) {
			long handle = 0;
			try {
				handle = HyperVJNI.OpenHypervHandle("", "", "");

				for (InstantVMNode node : ivmConfig.getInstantVMNodes()) {
					String vmUUID = node.getVmInfo().getVmUUID();
					if (StringUtil.isEmptyOrNull(vmUUID))
						continue;

					if (HyperVJNI.IsVmInStorageOperationalStatus(handle, vmUUID)) {
						logger.warn(String.format("The vm %s is moving or migrating storage.", vmUUID));
						return true;
					}
				}
			} catch (HyperVException hyperVe) {
			}
			finally {
				try {
					if (0 != handle) {
						HyperVJNI.CloseHypervHandle(handle);
					}
				}
				catch (HyperVException hyperVe) {
				}
			}
		}
		return false;
	}
	
	private JIVMJobStatus getIVMJobStatus(String ivmJobUUID) {
		// Get job status
		JIVMJobStatus jobStatus = new JIVMJobStatus(ivmJobUUID);
		long ret = getNativeFacade().getInstantVMJobStatus(jobStatus);						
		if (ret != 0) {
			logger.debug(String.format("Failed to getInstantVMJobStatus from shared memory map, JobUUID=%s.", ivmJobUUID));
			// The share memory does not exist, check the job script, if the status is failed or stopped, set the status
			InstantVMConfig ivmConfig = getInstantVMJobScript(ivmJobUUID);
			if (ivmConfig != null) {
				jobStatus.setIvmJobUUID(ivmJobUUID);
				jobStatus.setJobPhase((int)ivmConfig.getJobStatus());
				logger.debug("GetInstantVMJobStatus from script IVM ID is " + ivmJobUUID + ", JobStatus=" + ivmConfig.getJobStatus());
			}
		}
		jobStatus.setAgentExist(isAgentExist(ivmJobUUID));	
		// there is a case, at first, the process does not exist, but while reading status from the file, 
		// the process has been started and the status value equals 3.
		// in conclusion, the method reading status should be always before the method "IsAgentExist", which will ensure the status is reliable.
		//		if (!jobStatus.isAgentExist())
		//		{
		//			// there exists a gap btw two sampling points.
		//			InstantVMConfig ivmConfig = getInstantVMJobScript(ivmJobUUID);
		//			if (ivmConfig != null) {
		//				jobStatus.setIvmJobUUID(ivmJobUUID);
		//				jobStatus.setJobPhase((int)ivmConfig.getJobStatus());
		//				logger.debug("Attempt to getInstantVMJobStatus from script IVM ID is " + ivmJobUUID + ", JobStatus=" + ivmConfig.getJobStatus());
		//				return jobStatus;
		//			}
		//		}
		return jobStatus;
	}
	
	private void enOrDecrypt(InstantVMConfig para, boolean encrypt) {
		NativeFacade nativeFacade = getNativeFacade();

		String enOrDecryptStr = null;
		// Hypervisor password
		if (para.getHypervisorInfo() != null 
				&& para.getHypervisorInfo().getPassword() != null 
				&& para.getHypervisorInfo().getPassword().length() > 0) {
			if (encrypt)
				enOrDecryptStr = nativeFacade.encrypt(para.getHypervisorInfo().getPassword());
			else
				enOrDecryptStr = nativeFacade.decrypt(para.getHypervisorInfo().getPassword());
			para.getHypervisorInfo().setPassword(enOrDecryptStr);
		}
		
		for (InstantVMNode node : para.getInstantVMNodes()) {
			// Session password
			if (node.getSessionInfo().getSessionPassword() != null 
					&& node.getSessionInfo().getSessionPassword().length() > 0) {
				if (encrypt)
					enOrDecryptStr = nativeFacade.encrypt(node.getSessionInfo().getSessionPassword());
				else
					enOrDecryptStr = nativeFacade.decrypt(node.getSessionInfo().getSessionPassword());
				node.getSessionInfo().setSessionPassword(enOrDecryptStr);
			}
			
			if (node.getSessionInfo().getPassword() != null 
					&& node.getSessionInfo().getPassword().length() > 0) {
				if (encrypt)
					enOrDecryptStr = nativeFacade.encrypt(node.getSessionInfo().getPassword());
				else
					enOrDecryptStr = nativeFacade.decrypt(node.getSessionInfo().getPassword());
				node.getSessionInfo().setPassword(enOrDecryptStr);
			}
			
			if(node.getVmInfo().getDomainPassword() != null && node.getVmInfo().getDomainPassword().length() > 0)
			{
				if(encrypt)
					enOrDecryptStr = nativeFacade.EncryptDNSPassword(node.getVmInfo().getDomainPassword());
				else
					enOrDecryptStr = nativeFacade.DecryptDNSPassword(node.getVmInfo().getDomainPassword());
				node.getVmInfo().setDomainPassword(enOrDecryptStr);
			}

			if (node.getSessionInfo().getBackupOperatorAuthID() != null 
					&& node.getSessionInfo().getBackupOperatorAuthID().length() > 0) {
				if (encrypt)
					enOrDecryptStr = nativeFacade.encrypt(node.getSessionInfo().getBackupOperatorAuthID());
				else
					enOrDecryptStr = nativeFacade.decrypt(node.getSessionInfo().getBackupOperatorAuthID());
				node.getSessionInfo().setBackupOperatorAuthID(enOrDecryptStr);
			}
			
			if(node.getVmInfo().getDnsParameters() != null && node.getVmInfo().getDnsParameters().size() > 0)
			{
				List<DNSUpdaterParameter> dnsUpdaterParameter = node.getVmInfo().getDnsParameters();
		        for(DNSUpdaterParameter iter: dnsUpdaterParameter)  
		        {
		        	if(iter.getCredential() != null && iter.getCredential().length() > 0)
		        	{
		        		if (encrypt)
							enOrDecryptStr = nativeFacade.EncryptDNSPassword(iter.getCredential());
						else
							enOrDecryptStr = nativeFacade.DecryptDNSPassword(iter.getCredential());
			            iter.setCredential(enOrDecryptStr);
		        	}
		        } 
			}
			
			if (node.getSessionInfo().getBackupOperatorPassword() != null 
					&& node.getSessionInfo().getBackupOperatorPassword().length() > 0) {
				if (encrypt)
					enOrDecryptStr = nativeFacade.encrypt(node.getSessionInfo().getBackupOperatorPassword());
				else
					enOrDecryptStr = nativeFacade.decrypt(node.getSessionInfo().getBackupOperatorPassword());
				node.getSessionInfo().setBackupOperatorPassword(enOrDecryptStr);
			}
		}
	}
	
	private void constructIvmJobMonitor(InstantVMConfig para, IVMJobMonitor jobMonitor) {
		jobMonitor.setJobType(para.getJobType());
		jobMonitor.setJobId(para.getJobID());
		jobMonitor.setStartTime(para.getStartTime());
		jobMonitor.setD2dUuid(para.getNodeUUID());
		jobMonitor.setJobUUID(para.getIVMJobUUID());
		jobMonitor.setDataStoreUUID(para.getInstantVMNodes().get(0).getSessionInfo().getRpsDatastoreUUID());
		jobMonitor.setTargetRpsUUID(para.getInstantVMNodes().get(0).getSessionInfo().getBackupOperatorUUID());

		if (!HACommon.isTargetPhysicalMachine(para.getNodeUUID())){
			jobMonitor.setVmInstanceUUID(para.getNodeUUID());
		}
		
		boolean isVMGenerated = false;
		List<IVMNodeInfo> nodes = new ArrayList<IVMNodeInfo>();
		for (InstantVMNode node : para.getInstantVMNodes()) {
			String vmUUID = node.getVmInfo().getVmUUID();
			if (!StringUtil.isEmptyOrNull(vmUUID)) {
				isVMGenerated = true;
			}

			IVMNodeInfo nodeInfo = new IVMNodeInfo();
			nodeInfo.setVmUUID(vmUUID);
			String vmDisplayName = node.getVmInfo().getVmDisplayName();
			nodeInfo.setVmDisplayName(vmDisplayName);
			if (StringUtil.isEmptyOrNull(jobMonitor.getVmDisplayName())) {
				jobMonitor.setVmDisplayName(vmDisplayName);
			}
			nodeInfo.setIvmSessionNum(node.getSessionInfo().getSessionNum());
			nodeInfo.setAliveCheckEnabled(node.getVmInfo().isAliveCheckEnabled());
			if (nodeInfo.isAliveCheckEnabled()) {
				nodeInfo.setAliveCheckType(node.getVmInfo().getAliveCheckType());
				nodeInfo.setAliveCheckStatus(node.getVmInfo().getAliveCheckStatus());
			}
			nodes.add(nodeInfo);
		}

		if (isVMGenerated) {
			if (para.getHypervisorType() == HypervisorType.VMWARE) {
				CAVirtualInfrastructureManager vmwareOBJ = null;
				try {
					vmwareOBJ = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(
							para.getHypervisorInfo().getHostname(), 
							para.getHypervisorInfo().getUserName(), 
							para.getHypervisorInfo().getPassword(),
						    ((VMWareInfo) para.getHypervisorInfo()).getProtocol(), 
						    true,
						    ((VMWareInfo) para.getHypervisorInfo()).getPort());

					for (IVMNodeInfo nodeInfo : nodes) {
						if (!StringUtil.isEmptyOrNull(nodeInfo.getVmUUID())) {
							constructVMwareVMStatus(vmwareOBJ, nodeInfo);
						}
					}
				} catch (Exception e) {
					logger.error("Fail to connect to server.", e);
					return;
				}
				finally {
					if (vmwareOBJ != null) {
						try {
							vmwareOBJ.close();
						}
						catch (Exception e) {
						}
					}
				}
			} else if (para.getHypervisorType() == HypervisorType.HYPERV) {
				// Open handle
				long handle = 0;
				try {
					handle = getNativeFacade().OpenHypervHandle("","","");

					for (IVMNodeInfo nodeInfo : nodes) {
						if (!StringUtil.isEmptyOrNull(nodeInfo.getVmUUID())) {
							constructHyperVVMStatus(handle, nodeInfo);
						}
					}
				} catch (Exception e) {
					logger.error("Fail to open Hyper-V handle.", e);
					return ;
				}
				finally {
					try {
						if (0 != handle) {
							HyperVJNI.CloseHypervHandle(handle);
						}
					}
					catch (HyperVException hyperVe) {
					}
				}
			}
		}
	}
	private void constructVMwareVMStatus(CAVirtualInfrastructureManager vmwareOBJ, IVMNodeInfo nodeInfo) {
		try {
			// Get power
			powerState power = vmwareOBJ.getVMPowerstate(nodeInfo.getVmDisplayName(), nodeInfo.getVmUUID());
			switch (power) {
				case poweredOn :
					nodeInfo.setPowerStatus(IVMNodeInfo.IVM_POWER_ON);
					break;
				case poweredOff : 
					nodeInfo.setPowerStatus(IVMNodeInfo.IVM_POWER_OFF);
					break;
				case suspended : 
					nodeInfo.setPowerStatus(IVMNodeInfo.IVM_POWER_SUSPEND);
					break;
				default:
					nodeInfo.setPowerStatus(IVMNodeInfo.IVM_POWER_UNKNOWN);
					break;
			}
			
			// Get integration tool
			int tool = vmwareOBJ.checkVMToolsVersion(nodeInfo.getVmDisplayName(), nodeInfo.getVmUUID());
			nodeInfo.setVmHeartbeat(tool == 0 ? false : true);
			
			// Get ip
			VM_Info vm_info = vmwareOBJ.getVMInfo(nodeInfo.getVmDisplayName(), nodeInfo.getVmUUID());
			List<String> ips = new ArrayList<String>();
			ips.add(vm_info.getVMIP());
			nodeInfo.setIps(ips);
		} catch (Exception e) {
			logger.error(String.format("Failed to get VM status.", nodeInfo.getVmUUID()), e);
		}
	}
	private void constructHyperVVMStatus(long handle, IVMNodeInfo nodeInfo) {
		try {
			// Power
			int power = getNativeFacade().GetHyperVVmState(handle, nodeInfo.getVmUUID());
			switch (power) {
				case 3 :
					nodeInfo.setPowerStatus(IVMNodeInfo.IVM_POWER_OFF);
					break;
				case 2 :
					nodeInfo.setPowerStatus(IVMNodeInfo.IVM_POWER_ON);
					break;
				default:
					nodeInfo.setPowerStatus(IVMNodeInfo.IVM_POWER_UNKNOWN);
					break;
			}
			
			// Integration tool
			long vmTool = getNativeFacade().checkHyperVVMToolVersion(handle, nodeInfo.getVmUUID());
			nodeInfo.setVmHeartbeat((vmTool == 2 || vmTool == 1) ? true : false);
			
			
			// IPAddress
			List<String> ips = new ArrayList<String>();
			getNativeFacade().getHyperVIPAddresses(handle, nodeInfo.getVmUUID(), ips);
			nodeInfo.setIps(ips);
		} catch (Exception e) {
			logger.error("Failed to get Hyper-V vm status, vm name = " + nodeInfo.getVmDisplayName(), e);
		}
	}
	
	@Override
	public boolean needRun(JobDependencySource source) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void validateAdvanceSchedule(AdvanceSchedule advanceSchedule) 
			throws ServiceException {
		// TODO check if schedule has conflict
	}

	protected void setupArAdvanceSchedule(AssureRecoveryJobScript jobScript) throws SchedulerException {
		String policyId = jobScript.getId();
		AdvanceSchedule advanceSchedule = jobScript.getAdvanceSchedule();
		long iStartTime = advanceSchedule.getScheduleStartTime();
		Date startTime = iStartTime > 0 ? new Date(iStartTime) : new Date();
		List<Trigger> triggers = setupArPeriodSchedule(policyId, advanceSchedule.getPeriodSchedule(), startTime);
		for (DailyScheduleDetailItem item : advanceSchedule
				.getDailyScheduleDetailItems()) {
			for (ScheduleDetailItem detail : item.getScheduleDetailItems()) {
				Trigger trigger = setupArCustomSchedule(jobScript, item.getDayofWeek(), detail, startTime);
				if (null != trigger) {
					triggers.add(trigger);
				}
			}
		}
	}

	protected List<Trigger> setupArPeriodSchedule(String afGuid,
			PeriodSchedule periodSchedule, Date startTime) {
		List<Trigger> triggers = new ArrayList<Trigger>();
		// TODO
		return triggers;
	}
	
	public static String generateArJobID(int dayOfWeek, ScheduleDetailItem sd, String afGuid) {
		StringBuilder sb = new StringBuilder();
		sb.append(afGuid);
		sb.append(dayOfWeek);
		sb.append(sd.getStartTime());
		sb.append(sd.getEndTime());
		sb.append(sd.getInterval());
		sb.append(sd.getIntervalUnit());
		return sb.toString();
		
	}
	
	public boolean getRegFlag(String valueName) {
		JRWLong jValue = new JRWLong();
		WSJNI.GetRegIntValue("InstantVM", valueName, "SOFTWARE\\Arcserve\\Unified Data Protection\\Engine\\", jValue);
		return jValue.getValue() == 0 ? false : true;
	}

	protected Trigger setupArCustomSchedule(AssureRecoveryJobScript jobScript, int dayOfWeek,
			ScheduleDetailItem detail, Date startTime) throws SchedulerException {
		String policyId = jobScript.getId();
		String id = generateArJobID(dayOfWeek, detail, policyId);
		String triggerName = IVM_TRIGGER_NAME + id;
		String triggerGroupName = IVM_TRIGGER_GROUP_NAME + policyId;
		AdvancedScheduleTrigger trigger = new AdvancedScheduleTrigger(triggerName, triggerGroupName,
				startTime, detail, dayOfWeek);
		int ivmJobPriority = 50;
		trigger.setPriority(ivmJobPriority);
		trigger.setMisfireInstruction(AdvancedScheduleTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
		
		String jobName = IVM_JOB_NAME + id;
		String jobGroupName = IVM_JOB_GROUP_NAME + policyId;
		JobDetailImpl jd = new JobDetailImpl();
		jd.setJobClass(AssureRecoveryJob.class);
		jd.setGroup(jobGroupName);
		jd.setName(jobName);
		
		JobDataMap dataMap = jd.getJobDataMap();
		dataMap.put(AssureRecoveryJob.JobScript, jobScript);
		Scheduler scheduler = HAService.getInstance().GetScheduler();
		if (null != scheduler) {
			scheduler.scheduleJob(jd, trigger);
		}
		return trigger;
	}
	
	protected void verifyPolicy(AssureRecoveryJobScript jobScript) throws ServiceException{
		
	}
	
	
	public void applyPolicy(AssureRecoveryJobScript jobScript) throws ServiceException, SchedulerException {
		verifyPolicy(jobScript);
		validateAdvanceSchedule(jobScript.getAdvanceSchedule());
		setupArAdvanceSchedule(jobScript);
		jobQueue.add(jobScript);
	}
	
	/**
	 * 
	 * @param para  which have contain the required parameters:
	 *                          Source Node Name and Node UUID, 
	 *                          Job Type,  
	 *                          Job ID == 0 
	 *                          VMConfigPath where the created VHD/VHDX files are located on, 
	 *                          Hypervisor type,   
	 * @param criteria
	 * @return
	 */
	public PrecheckResult checkPrerequisites(InstantVMConfig para, PrecheckCriteria criteria) {
		logger.info("check the Prerequisites for Node " + para.getNodeName());		
		PrecheckResult checkResult = new PrecheckResult();
		NativeFacade nativeFacade = getNativeFacade();
		long hypervisor_type = para.getHypervisorType() == HypervisorType.HYPERV ? PreCheckInstantVMProxyModel.HYPERVISOR_HYPERV : PreCheckInstantVMProxyModel.HYPERVISOR_VMWARE;
		PreCheckInstantVMProxyModel preCheckProxyStruct = new PreCheckInstantVMProxyModel(para.getVmConfigPath(), para.getNodeUUID(), para.getJobType(), 0, hypervisor_type);
		preCheckProxyStruct.precheckCriteria = criteria;
		checkResult.criteria = criteria;
		StringBuilder errMsg = new StringBuilder();
		ArrayList<String> warningList = new ArrayList<String>();
		if (nativeFacade.isInstantVMProxyMeetRequirement(preCheckProxyStruct, warningList, errMsg) != 0) {
			logger.error(errMsg);
			checkResult.result = false; 
			checkResult.criteria = preCheckProxyStruct.precheckCriteria;
			return checkResult;
		}
		checkResult.criteria = preCheckProxyStruct.precheckCriteria;
		return checkResult;
	}
}
