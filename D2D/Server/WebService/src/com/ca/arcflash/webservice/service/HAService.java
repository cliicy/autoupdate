package com.ca.arcflash.webservice.service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.HACoordinator;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.common.modelmanager.RepositoryUtil;
import com.ca.arcflash.failover.FailoverMessage;
import com.ca.arcflash.failover.HyperVFailoverCommand;
import com.ca.arcflash.failover.VMwareCenterServerFailoverCommand;
import com.ca.arcflash.failover.VMwareESXFailoverCommand;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.failover.model.ADRConfigureFactory;
import com.ca.arcflash.failover.model.Disk;
import com.ca.arcflash.failover.model.Volume;
import com.ca.arcflash.ha.alert.EmailAlertCommand;
import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.ARCFlashNodesSummary;
import com.ca.arcflash.ha.model.DiskInfo;
import com.ca.arcflash.ha.model.EdgeLicenseInfo;
import com.ca.arcflash.ha.model.EsxHostInformation;
import com.ca.arcflash.ha.model.EsxServerInformation;
import com.ca.arcflash.ha.model.FileVersion;
import com.ca.arcflash.ha.model.HeartBeatModel;
import com.ca.arcflash.ha.model.JobScriptCombo;
import com.ca.arcflash.ha.model.ProductionServerRoot;
import com.ca.arcflash.ha.model.ReplicaRoot;
import com.ca.arcflash.ha.model.SummaryModel;
import com.ca.arcflash.ha.model.TransServerReplicaRoot;
import com.ca.arcflash.ha.model.VCMConfigStatus;
import com.ca.arcflash.ha.model.VCMD2DBackupInfo;
import com.ca.arcflash.ha.model.VCMPolicyNodeValidation;
import com.ca.arcflash.ha.model.VCMSavePolicyWarning;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.ha.model.VMWareESXHostReplicaRoot;
import com.ca.arcflash.ha.model.VMWareVirtualCenterReplicaRoot;
import com.ca.arcflash.ha.model.VirtualMachineInfo;
import com.ca.arcflash.ha.model.internal.HAException;
import com.ca.arcflash.ha.model.manager.CustomSessionPasswordManager;
import com.ca.arcflash.ha.model.manager.HeartBeatModelManager;
import com.ca.arcflash.ha.model.manager.VMInfomationModelManager;
import com.ca.arcflash.ha.model.manager.VMwareSnapshotModelManager;
import com.ca.arcflash.ha.modelWebService.MonitorWebServiceErrorCode;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.ha.utils.VCMPolicyUtils;
import com.ca.arcflash.ha.utils.VDDKService;
import com.ca.arcflash.ha.utils.VirtualConversionEmailAlertUtil;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanager.Host_Info;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.ha.vmwaremanager.VMwareServerType;
import com.ca.arcflash.ha.vmwaremanager.VMwareStorage;
import com.ca.arcflash.ha.vmwaremanager.VirtualNetworkInfo;
import com.ca.arcflash.ha.vmwaremanager.powerState;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.ha.webservice.MonitorWebClientManager;
import com.ca.arcflash.job.AFJob;
import com.ca.arcflash.job.HAJobStatus;
import com.ca.arcflash.job.alert.AlertJob;
import com.ca.arcflash.job.failover.FailoverJob;
import com.ca.arcflash.job.heartbeat.HeartBeatJob;
import com.ca.arcflash.job.replication.ReplicationJob;
import com.ca.arcflash.jobqueue.JobQueue;
import com.ca.arcflash.jobqueue.JobQueueFactory;
import com.ca.arcflash.jobscript.alert.AlertJobScript;
import com.ca.arcflash.jobscript.alert.AlertType;
import com.ca.arcflash.jobscript.alert.EmailModel;
import com.ca.arcflash.jobscript.base.GenerateType;
import com.ca.arcflash.jobscript.base.JobScript;
import com.ca.arcflash.jobscript.base.JobType;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.HyperV;
import com.ca.arcflash.jobscript.failover.HyperVNetworkAdapter;
import com.ca.arcflash.jobscript.failover.IPSetting;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.jobscript.failover.VMwareESX;
import com.ca.arcflash.jobscript.failover.VMwareNetworkAdapter;
import com.ca.arcflash.jobscript.failover.VMwareVirtualCenter;
import com.ca.arcflash.jobscript.failover.VSphereProxyServer;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ARCFlashStorage;
import com.ca.arcflash.jobscript.replication.DiskDestination;
import com.ca.arcflash.jobscript.replication.DiskModel;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMStorage;
import com.ca.arcflash.jobscript.replication.VMwareESXStorage;
import com.ca.arcflash.jobscript.replication.VMwareVirtualCenterStorage;
import com.ca.arcflash.jobscript.replication.VSphereBackupType;
import com.ca.arcflash.listener.manager.ListenerManager;
import com.ca.arcflash.replication.ReplicationService;
import com.ca.arcflash.repository.RepositoryManager;
import com.ca.arcflash.rps.webservice.RPSWebServiceFactory;
import com.ca.arcflash.rps.webservice.data.host.RegisterNodeInfo;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4D2D;
import com.ca.arcflash.service.common.ActivityLogSyncher;
import com.ca.arcflash.service.common.FlashSwitch;
import com.ca.arcflash.service.common.FlashSwitchDefine;
import com.ca.arcflash.service.common.FlashSyncher;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.serviceinfo.ServiceInfoConstants;
import com.ca.arcflash.webservice.AxisFault;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.FlashServiceImpl;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.WebServiceFactory;
import com.ca.arcflash.webservice.common.VCMLicenseCheck;
import com.ca.arcflash.webservice.common.VCMMachineInfo;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.data.ConvertJobMonitor;
import com.ca.arcflash.webservice.data.DestinationCapacity;
import com.ca.arcflash.webservice.data.HyperVDestinationInfo;
import com.ca.arcflash.webservice.data.MachineDetail;
import com.ca.arcflash.webservice.data.MachineType;
import com.ca.arcflash.webservice.data.RPSInfo;
import com.ca.arcflash.webservice.data.SourceNodeSysInfo;
import com.ca.arcflash.webservice.data.TrustedHost;
import com.ca.arcflash.webservice.data.VMwareServer;
import com.ca.arcflash.webservice.data.VWWareESXNode;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.backup.Account;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.backup.RpsPolicy4D2D;
import com.ca.arcflash.webservice.data.edge.datasync.d2d.D2DInfo;
import com.ca.arcflash.webservice.data.edge.datasync.d2d.D2DStatus;
import com.ca.arcflash.webservice.data.edge.datasync.d2d.D2DType;
import com.ca.arcflash.webservice.data.job.rps.ConversionJobArg;
import com.ca.arcflash.webservice.data.job.rps.IJobDependency;
import com.ca.arcflash.webservice.data.job.rps.JobDependencySource;
import com.ca.arcflash.webservice.data.listener.EventType;
import com.ca.arcflash.webservice.data.listener.FlashListenerInfo;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;
import com.ca.arcflash.webservice.data.restore.BackupD2D;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.d2dstatus.statuscollectors.VCMStatusCollector;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.edge.data.d2dstatus.VMPowerStatus;
import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcflash.webservice.edge.data.policy.VCMPolicyDeployParameters;
import com.ca.arcflash.webservice.edge.license.LICENSEDSTATUS;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyApplyerFactory;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyCheckStatus;
import com.ca.arcflash.webservice.heartbeat.ConcreteHeartBeatCommand;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.jni.model.JVMJobMonitorDetail;
import com.ca.arcflash.webservice.replication.BackupDestinationInfo;
import com.ca.arcflash.webservice.replication.BaseReplicationCommand;
import com.ca.arcflash.webservice.replication.MachineDetailManager;
import com.ca.arcflash.webservice.replication.ManualConversionUtility;
import com.ca.arcflash.webservice.replication.ReplicationMessage;
import com.ca.arcflash.webservice.replication.SessionInfo;
import com.ca.arcflash.webservice.replication.ShareFolderReplicationCommand;
import com.ca.arcflash.webservice.replication.TransServerReplicationCommand;
import com.ca.arcflash.webservice.replication.VMwareCenterServerReplicationCommand;
import com.ca.arcflash.webservice.replication.VMwareESXReplicationCommand;
import com.ca.arcflash.webservice.scheduler.BaseBackupJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.RemoteVCMSessionMonitor;
import com.ca.arcflash.webservice.service.rps.RPSServiceProxyManager;
import com.ca.arcflash.webservice.service.rps.SettingsService;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.ha.webservice.jni.HyperVException;
import com.ca.ha.webservice.jni.HyperVJNI;
import com.ca.ha.webservice.jni.VMWareJNI;
//import com.ca.arcflash.webservice.common.VSphereLicenseCheck;
//import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
//import com.ca.arcflash.webservice.edge.policymanagement.PolicyQueryStatus;

public class HAService extends BaseService implements Observer, IJobDependency{
	private static final Logger logger = Logger.getLogger(HAService.class);
	private static HAService instance = new HAService();

	private JobQueue jobQueue = null;
	private HAServiceObservable backupJob = new  HAServiceObservable();
	public static final String REGISTRY_KEY_BUILDNUMBER = "Build";

	public static final String REGISTRY_KEY_GUID = "GUID";

	public static final String REGISTRY_KEY_MAJORVERSION = "Major";

	public static final String REGISTRY_KEY_MINORVERSION = "Minor";

	public static final String REGISTRY_KEY_PATH = "Path";
	
	public static final int    WAIT_INTERVALS =  30 * 1000;
	public static final int    REQUEST_TAKE_VM_SNAPSHOT_VALUE		= 	 3 * 60 * 60 * 1000;     // 3 Hours.
	public static final String SWT_VSB_VM_TAKESNAPSHOT_REQUEST_TIMOUT = "VSBRequestSnapshotTimeout";

	private static final String COLDSTANDBY_SETTING_STOPHEARTBEAT = "COLDSTANDBY_SETTING_STOPHEARTBEAT";
	private static final String COLDSTANDBY_SETTING_STOPHEARTBEAT_FAIL = "COLDSTANDBY_SETTING_STOPHEARTBEAT_FAIL";
	private static final String COLDSTANDBY_SETTING_UNREGSTER_VSPHERE_FAILOVERSCRIPT = "COLDSTANDBY_SETTING_UNREGSTER_VSPHERE_FAILOVERSCRIPT";
	private static final String COLDSTANDBY_SETTING_START_HEARTBEAT = "COLDSTANDBY_SETTING_START_HEARTBEAT";
	private static final String COLDSTANDBY_SETTING_PAUSE_HEARTBEAT = "COLDSTANDBY_SETTING_PAUSE_HEARTBEAT";
	private static final String COLDSTANDBY_SETTING_PAUSE_HEARTBEAT_FAIL = "COLDSTANDBY_SETTING_PAUSE_HEARTBEAT_FAIL";
	private static final String COLDSTANDBY_SETTING_RESUME_HEARTBEAT = "COLDSTANDBY_SETTING_RESUME_HEARTBEAT";
	private static final String COLDSTANDBY_SETTING_RESUME_HEARTBEAT_FAIL = "COLDSTANDBY_SETTING_RESUME_HEARTBEAT_FAIL";
	private static final String COLDSTANDBY_SETTING_REPLICATION_AUTO = "COLDSTANDBY_SETTING_REPLICATION_AUTO";
	
	private static final String COLDSTANDBY_SETTING_STOPHEARTBEAT_MONITOR = "COLDSTANDBY_SETTING_STOPHEARTBEAT_MONITOR";
	
	private static final String COLDSTANDBY_SETTING_CANCEL_CONVERSION_FAILED = "COLDSTANDBY_SETTING_CANCEL_CONVERSION_FAILED";

	public static final String HYPERV_NETWORK_ADAPTER = "Network Adapter";
	public static final String HYPERV_LEGACY_NETWORK_ADAPTER = "Legacy Network Adapter";
	
	public static final String VDDK_DRIVER_FILE_X86 = "vstor2-mntapi10-shared.sys";
	public static final String VDDK_DRIVER_FILE_X64 = "vstor2-mntapi20-shared.sys";
	public static final String VDDK_DRIVER_NAME_X86 = "vstor2-mntapi10-shared";
	public static final String VDDK_DRIVER_NAME_X64 = "vstor2-mntapi20-shared";
	
	public static final String IVM_UDP_NOTE = "UDP_IVM";
	
	String testuuid = null;
	
	private final int VSB_VDDK_ENFORCE_NBD_FLAG_DEFAULT_VALUE = 0;
	private final int VSB_HATRANS_SERVER_PORT_DEFAULT_VALUE = 4090;
	private final int VSB_STATUS_SYNC_INTERVAL_DEFAULT_VALUE = 60;  // seconds
	private final int VSB_THREAD_COUNT_DEFAULT_VALUE = 10;
	
	/*by wanwe14
	 * This flag is designed to indicate whether to force next replication to merge all its sessions.
	 * This flag is initialized with false,and will be set to false after each replication job whatever the
	 * job succeed or not.
	 */
	public static final String SMART_COPY = "smart_copy";
	public static final String NON_SMART_COPY = "non_smart_copy";
	private Map<String, String> forceSmartCopy = new Hashtable<String, String>();

	//If sessions are backed up before VCM setting. 
	//These sessions need to copy with following incremental session as smart copy. 
	public static final String INCREMENTAL_COMPLETE = "incremental_complete";
	
	//change failover mode
	public static final String COLDSTANDBY_CHANGE_FAILOVER_MODE="COLDSTANDBY_CHANGE_FAILOVER_MODE";
	
	
	public static final String COLDSTANDBY_SETTING_PAUSEHEARTBEAT_MONITOR = "COLDSTANDBY_SETTING_PAUSEHEARTBEAT_MONITOR";
	public static final String COLDSTANDBY_SETTING_RESUMEHEARTBEAT_MONITOR = "COLDSTANDBY_SETTING_RESUMEHEARTBEAT_MONITOR";
	public static final String COLDSTANDBY_SETTING_REGISTER_HEARTBEAT_MONITOR = "COLDSTANDBY_SETTING_REGISTER_HEARTBEAT_MONITOR";

	
	private static final String COLDSTANDBY_ALERT_START_AUTO_FAILOVER = "COLDSTANDBY_ALERT_START_AUTO_FAILOVER";
	private static final String COLDSTANDBY_ALERT_START_MANUAL_FAILOVER = "COLDSTANDBY_ALERT_START_MANUAL_FAILOVER";
	//private static final String COLDSTANDBY_ALERT_MISS_HEATBEAT = "COLDSTANDBY_ALERT_MISS_HEATBEAT";
	//private static final String COLDSTANDBY_ALERT_MISS_HEATBEAT_BUT_ALIVE = "COLDSTANDBY_ALERT_MISS_HEATBEAT_BUT_ALIVE";
	//private static final String COLDSTANDBY_ALERT_AUTO_FAILOVER_SKIPPED_BY_PLAN_PAUSED = "COLDSTANDBY_ALERT_AUTO_FAILOVER_SKIPPED_BY_PLAN_PAUSED";
	
	
	Scheduler scheduler = null;
	
	private HAService() {
		startVsbScheduler();
		if (FlashServiceImpl.edgeFlag)
			return;
		jobQueue = JobQueueFactory.getDefaultJobQueue();
	}

	public static HAService getInstance() {
		return instance;
	}
	
	/**
	 * It will set the failoverjobscript's afguid, and all the failover jobs for
	 * that afguid. So there is only one failoverjobscript for one afguid
	 * 
	 * @param failoverJobScript
	 */
	public void setFailoverJobScript(FailoverJobScript failoverJobScript) {
		logger.debug("setFailoverJobScript(FailoverJobScript) - start"); //$NON-NLS-1$

		if(StringUtil.isEmptyOrNull(failoverJobScript.getAFGuid())){
			failoverJobScript.setAFGuid(HAService.getInstance()
				.retrieveCurrentNodeID());
		}
		
		synchronized (jobQueue) {
			Collection<AFJob> jobs = jobQueue.findByJobType(JobType.Failover);
			for (AFJob job : jobs) {
				FailoverJob failoverJob = (FailoverJob) job;
				if (failoverJobScript.getAFGuid().equals(
						failoverJob.getJobScript().getAFGuid())) {
					jobQueue.remove(failoverJob);
				}
			}
			jobQueue.add(failoverJobScript);
		}

		logger.debug("setFailoverJobScript(FailoverJobScript) - end"); //$NON-NLS-1$
	}

	public FailoverJobScript getFailoverJobScript(String afGuid) {
		logger.debug("getFailoverJobScript() - start"); //$NON-NLS-1$

		String localAFGuid = evaluteAFGuid(afGuid);
		
		synchronized (jobQueue) {
			Collection<AFJob> jobs = jobQueue.findByJobType(JobType.Failover);
			for (AFJob job : jobs) {
				FailoverJob failoverJob = (FailoverJob) job;
				if (failoverJob.getJobScript().getAFGuid().equals(localAFGuid)) {
					FailoverJobScript returnFailoverJobScript = failoverJob
							.getJobScript();
					logger.debug("getFailoverJobScript() - end"); //$NON-NLS-1$
					return returnFailoverJobScript;
				}
			}
		}

		logger.debug("getFailoverJobScript() - end"); //$NON-NLS-1$
		return null;
	}
	public AFJob getFailoverJob(String afGuid) {
		logger.debug("getFailoverJob() - start"); //$NON-NLS-1$

		String localAFGuid = evaluteAFGuid(afGuid);
		
		synchronized (jobQueue) {
			AFJob job = jobQueue.findFailoverJobByID(localAFGuid);
			return job;
		}

	}
	public AFJob getHeartBeatJob(String afGuid) {
		
		logger.debug("getHeartBeatJob() - start"); //$NON-NLS-1$

		String localAFGuid = evaluteAFGuid(afGuid);
		
		synchronized (jobQueue) {
			Collection<AFJob> jobs = jobQueue.findByJobType(JobType.HeartBeat);
			for (AFJob job : jobs) {
				HeartBeatJob heartBeatJob = (HeartBeatJob) job;
				if (heartBeatJob.getJobScript().getAFGuid().equals(localAFGuid)) {
					logger.debug("getHeartBeatJob() - end"); //$NON-NLS-1$
					return job;
				}
			}
		}

		logger.debug("getHeartBeatJob() - end"); //$NON-NLS-1$
		return null;
	}
	
	public AFJob getReplicationJob(String afGuid) {
		logger.debug("getReplicationJob() - start"); //$NON-NLS-1$

		String localAFGuid = evaluteAFGuid(afGuid);
		
		synchronized (jobQueue) {
			Collection<AFJob> jobs = jobQueue.findByJobType(JobType.Replication);
			for (AFJob job : jobs) {
				if(job.getJobScript().getAFGuid().equals(localAFGuid)){
					return job;
				}
			}
		}

		logger.debug("getReplicationJob() - end"); //$NON-NLS-1$
		return null;
	}

	/**
	 * set the beatjobscript's afguid and add it into jobQueue. There is only
	 * one HeartBeat Job can be added.
	 * 
	 * @param heartBeatJobScript
	 */
	public void setHeartBeatJobScript(HeartBeatJobScript heartBeatJobScript, boolean needNewCommand) {
		logger.debug("setHeartBeatJobScript(HeartBeatJobScript) - start"); //$NON-NLS-1$

		if (needNewCommand)
			heartBeatJobScript.setHeartBeatCommand(new ConcreteHeartBeatCommand());
		synchronized (jobQueue) {
			
			AFJob heartbeatJob = jobQueue.findHeartBeatJobByID(heartBeatJobScript.getAFGuid());
			if(heartbeatJob != null){
				jobQueue.remove(heartbeatJob);
			}
			jobQueue.add(heartBeatJobScript);
		}

		logger.debug("setHeartBeatJobScript(HeartBeatJobScript) - end"); //$NON-NLS-1$
	}

	public HeartBeatJobScript getHeartBeatJobScript(String afGuid) {
		logger.debug("getHeartBeatJobScript() - start"); //$NON-NLS-1$

		String localAFGuid = evaluteAFGuid(afGuid);
		
		synchronized (jobQueue) {
			Collection<AFJob> jobs = jobQueue.findByJobType(JobType.HeartBeat);
			// [li$fe01] Should always have 1 heart beat job in the queue at
			// most.
			for (AFJob afJob : jobs) {
				if(afJob.getJobScript().getAFGuid().equals(localAFGuid)){
					return (HeartBeatJobScript)afJob.getJobScript();
				}
			}
		}

		logger.debug("getHeartBeatJobScript() - end"); //$NON-NLS-1$
		return null;
	}
	
	/**
	 * return 1: both heartbeat job and failover job exist,
	 * 		  3: run pause on monitor side, 
	 * 		  7: modify the heartbeat job status into Active, 
	 * 		or the combined value i.e. (1+3+7) for whole process 
	 * @throws SOAPFaultException
	 */
	public int resumeHeartBeatForD2D(String afGuid){
		logger.debug("resumeHeartBeatForD2D() - start"); //$NON-NLS-1$
		int result = 0;
		HeartBeatJobScript heartBeatJobScript = null;
		
		String localAFGuid = evaluteAFGuid(afGuid);
		
		synchronized (jobQueue) {
			AFJob heartBeatJob = this.getHeartBeatJob(localAFGuid);
			if (isNullJob(heartBeatJob))
				return result;
			heartBeatJobScript = (HeartBeatJobScript) heartBeatJob.getJobScript();
			if (isNullJobScript(heartBeatJobScript))
				return result;
			
			boolean needResume = false;
			if (heartBeatJobScript.getBackupToRPS()) {
				if (heartBeatJob.getJobStatus().getStatus().equals(HAJobStatus.Status.Canceled)
						&& !MonitorWebClientManager.isRVCMMonitorLocalhost(heartBeatJobScript)) {
					needResume = true;
				}
			}
			else {
				AFJob failoverJob = this.getFailoverJob(localAFGuid);
				if(failoverJob!=null){
					result = 1;
					FailoverJobScript failoverJobScript = ((FailoverJob)failoverJob).getJobScript();
					if (failoverJobScript != null && failoverJobScript.getState() != FailoverJobScript.UNREGISTERED
							&& heartBeatJob.getJobStatus().getStatus().equals(HAJobStatus.Status.Canceled)
							&& !MonitorWebClientManager.isRVCMMonitorLocalhost(heartBeatJobScript)) {
						needResume = true;
					}
				}
			}
			
			if (needResume) {
				WebServiceClientProxy client = null;
				try {
					logger.info("Resume heartbeat");
					client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);
					client.getServiceV2().resumeHeartBeat(heartBeatJobScript.getAFGuid());
					result += 3;
				} catch (SOAPFaultException e) {
					
					logger.error(e.getMessage(),e);
					
					String msg = WebServiceMessages.getResource(
							COLDSTANDBY_SETTING_RESUME_HEARTBEAT_FAIL,heartBeatJobScript.getHeartBeatMonitorHostName());
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, 
							Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "",
									"", "", "" }, localAFGuid);
					
					
					throw e;
				}
			}

			if( heartBeatJob.getJobStatus().getStatus().equals(HAJobStatus.Status.Canceled)){
				logger.info("The HaJobStatus is Canceled and will be set Active(resume).");
				heartBeatJob.schedule();
				HAJobStatus jobStatus = new HAJobStatus();
				jobStatus.setStatus(HAJobStatus.Status.Active);
				heartBeatJob.setJobStatus(jobStatus);
				jobQueue.reStoreJob(heartBeatJob);
				result+=7;
			}
			String msg = WebServiceMessages.getResource(
					COLDSTANDBY_SETTING_RESUME_HEARTBEAT,heartBeatJobScript.getHeartBeatMonitorHostName());
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, 
					Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "",
							"", "", "" }, localAFGuid);
			
		}	
		return result;
	}
	
	private boolean isNullJob(AFJob job) {
		return job == null;
	}
	
	private boolean isNullJobScript(JobScript jobScript) {
		return jobScript == null;
	}
	
	/**
	 * return 1: both heartbeat job and failover job exist, 3: run pause on monitor side, 7: modify the heartbeat job status into Canceled, or the combined value 
	 * @throws ServiceException 
	 */
	public int resumeHeartBeat(String afGuid) throws ServiceException {
		logger.debug("resumeHeartBeat() - start"); //$NON-NLS-1$
		String localAFGuid = evaluteAFGuid(afGuid);
		AFJob failoverJob = this.getFailoverJob(localAFGuid);
		if (isNullJob(failoverJob))
			return 0;
		FailoverJobScript failoverJobScript = (FailoverJobScript) failoverJob.getJobScript();
		if (isNullJobScript(failoverJobScript))
			return 0;
		
		if (ManualConversionUtility.isVSBWithoutHASupport(failoverJobScript))
			return 0;

//		if (failoverJobScript.getIsPlanPaused()) {
//			logger.info("The plan is paused.");
//			throw generateAxisFault(FlashServiceErrorCode.Backup_BackupDisabled);
//		}

		String hbHostName = failoverJobScript.getProductionServerName();
		if (failoverJobScript.isVSphereBackup())
			hbHostName = failoverJobScript.getVSphereproxyServer().getVSphereProxyName();

		int ret = 0;
		if (failoverJobScript.getBackupToRPS()) {
			try {
				logger.info("Transfer the command to D2D agent and D2D agent will perform the resume operation.");
				ret = getD2DService(failoverJobScript).getServiceV2().resumeHeartBeatForD2D(localAFGuid);
			} catch (ServiceException e) {
				logger.error(e);
				throw(e);
			} catch (Exception e) {
				logger.error(e);
				throw new ServiceException(FlashServiceErrorCode.Common_CantConnectHost, new String[]{hbHostName});
			}
		}
		else {
			ret = resumeHeartBeatForD2D(localAFGuid);
		}

		syncVCMStatus2Monitor(afGuid);
		return ret;
	}
	
	/**
	 * 
	 * @param jobStatusStr
	 * @param register
	 * @return 1 if has failover and heartbeat job, 3 if these job are in correct status, or combined values
	 * @throws SOAPFaultException
	 */
	public int isHeartBeatInState(String afGuid,String jobStatusStr, int register) {
		logger.debug("isHeartBeatInState() - start " + jobStatusStr + ":" + register); //$NON-NLS-1$
		int result = 0;
		HAJobStatus.Status jobStatus = HAJobStatus.Status.valueOf(jobStatusStr);
		if(jobStatus == null) 
			throw  AxisFault.fromAxisFault("illeage job status value");
		
		String localAFGuid = evaluteAFGuid(afGuid);
		
		synchronized (jobQueue) {
			
			
			Collection<AFJob> jobs = jobQueue.findByJobType(JobType.HeartBeat);
			Iterator<AFJob> iter = jobs.iterator();
			while (iter.hasNext()) {
				AFJob job = iter.next();
				AFJob failoverJob = getFailoverJob(localAFGuid);
				if(failoverJob!=null){
					result = 1;
					FailoverJobScript failoverJobScript = ((FailoverJob)failoverJob).getJobScript();
					if(failoverJobScript!=null && failoverJobScript.getState() == register && job.getJobStatus().getStatus().equals(jobStatus)){
						result+=3;
					}
				}
			}
			
		}	
		return result;
		
	}
	
	public String getVmName( String afGuid, ProductionServerRoot serverRoot ) {
		logger.debug("getVmName() - start "); //$NON-NLS-1$
		
		if ( serverRoot == null || serverRoot.getReplicaRoot() == null || serverRoot.getReplicaRoot().getVmname() == null) {
			String localAFGuid = evaluteAFGuid(afGuid);
			
			synchronized (jobQueue) {
				AFJob failoverJob = this.getFailoverJob(localAFGuid);
				if (isNullJob(failoverJob))
					return null;
				FailoverJobScript failoverJobScript = (FailoverJobScript) failoverJob.getJobScript();
				if (isNullJobScript(failoverJobScript))
					return null;
				
				return failoverJobScript.getFailoverMechanism().get(0).getVirtualMachineDisplayName();
			}
		}
		else
			return serverRoot.getReplicaRoot().getVmname();
	}
	
	public Integer[] getAutoOfflieCopyStatusAndHeartbeatStatus( String afGuid, WebServiceClientProxy converterClient, FailoverJobScript failoverJobScript ) {
		logger.debug("getAutoOfflieCopyStatusAndHeartbeatStatus() - start "); //$NON-NLS-1$
		
		String localAFGuid = evaluteAFGuid(afGuid);
		Integer[] result = {HeartBeatJobScript.STATE_NO_EXIST, ReplicationJobScript.AUTO_OFFLINE_COPY_NO_EXIST};
		
		// Get vsb status
		if (converterClient != null) {
			try {
				result = converterClient.getServiceV2().getStatesThis(localAFGuid);
			} catch (Exception e) {
				result[1] = ReplicationJobScript.AUTO_OFFLINE_COPY_NO_EXIST;
				logger.error(e.getMessage());
			}			
		}
		else {
			logger.error("The converter service null, skip to get the status.");
		}

		
		// Get heartbeat status
		try {
			if (!ManualConversionUtility.isVSBWithoutHASupport(failoverJobScript)) {
				boolean isTimeout = ((FailoverJob)getFailoverJob(afGuid)).isTimeout();
				if (HeartBeatModelManager.getHeartbeatNode(afGuid).getState() == HeartBeatJobScript.STATE_PAUSED)
					result[0] = HeartBeatJobScript.STATE_PAUSED;
				else
					result[0] = isTimeout ? HeartBeatJobScript.STATE_NO_EXIST : HeartBeatJobScript.STATE_REGISTERED;
			}
		} catch (Exception e) {
			result[0] = HeartBeatJobScript.STATE_NO_EXIST;
			logger.error(e.getMessage());
		}
	
		return result;
	}
	
	public Integer[] getStatesThis(String afGuid) {
		logger.debug("getStatesThis() - start "); //$NON-NLS-1$
		
		String localAFGuid = evaluteAFGuid(afGuid);
		
		int result = HeartBeatJobScript.STATE_NO_EXIST;
		int result2 = ReplicationJobScript.AUTO_OFFLINE_COPY_NO_EXIST;
		
		synchronized (jobQueue) {
			
			HeartBeatJob heartBeatJob = (HeartBeatJob)jobQueue.findHeartBeatJobByID(localAFGuid);
			if(heartBeatJob != null){
			
				AFJob failoverJob = getFailoverJob(localAFGuid);
				FailoverJobScript failoverJobScript = null;
				if (failoverJob != null) {
					failoverJobScript = ((FailoverJob) failoverJob).getJobScript();
				}
				
				if (failoverJobScript != null) {
					if (failoverJobScript.getState() == FailoverJobScript.REGISTERED){
							result |= HeartBeatJobScript.STATE_REGISTERED;
					}else if (failoverJobScript.getState() == FailoverJobScript.UNREGISTERED){
						result |= HeartBeatJobScript.STATE_UNREGISTERED;
					}
				}

				switch (heartBeatJob.getJobStatus().getStatus()) {
					case Pending:
						result |= HeartBeatJobScript.STATE_PENDING;
						break;
					case Active:
						result |= HeartBeatJobScript.STATE_ACTIVE;
						break;
					case Canceled:
						result |= HeartBeatJobScript.STATE_CANCELED;
						break;
				default:
					break;
				}
				
				ReplicationJobScript repJobScript = getReplicationJobScript(localAFGuid);
				
				if (repJobScript != null)
					result2 =  repJobScript.getAutoReplicate()?ReplicationJobScript.AUTO_OFFLINE_COPY_ENABLED:ReplicationJobScript.AUTO_OFFLINE_COPY_DISABLED;
				
			}
		}
		
		return new Integer[]{result,result2};
	}
	/**
	 * return 1: both heartbeat job and failover job exist, 3: run pause on monitor side, 7: modify the heartbeat job status into Canceled, or the combined value 
	 * @throws SOAPFaultException
	 */
	public int pauseHeartBeatForD2D(String afGuid) throws SOAPFaultException {
		logger.info("pauseHeartBeatForD2D() - start"); //$NON-NLS-1$
		int result  = 0;
		HeartBeatJobScript heartBeatJobScript = null;
		
		String localAFGuid = evaluteAFGuid(afGuid);
		
		synchronized (jobQueue) {
			AFJob heartBeatJob = this.getHeartBeatJob(localAFGuid);
			if (isNullJob(heartBeatJob))
				return result;
			heartBeatJobScript = (HeartBeatJobScript) heartBeatJob.getJobScript();
			if (isNullJobScript(heartBeatJobScript))
				return result;
			
			boolean needPause = false;
			if (heartBeatJobScript.getBackupToRPS()){
                if (heartBeatJob.getJobStatus().getStatus().equals(HAJobStatus.Status.Active)
                        && !MonitorWebClientManager.isRVCMMonitorLocalhost(heartBeatJobScript)) {
                	needPause = true;
                }
			}
			else {			
				AFJob failoverJob = this.getFailoverJob(localAFGuid);
				if(failoverJob!=null){
					result = 1;
					FailoverJobScript failoverJobScript = ((FailoverJob)failoverJob).getJobScript();
					if (failoverJobScript != null && failoverJobScript.getState() == FailoverJobScript.REGISTERED
							&& heartBeatJob.getJobStatus().getStatus().equals(HAJobStatus.Status.Active)
							&& !MonitorWebClientManager.isRVCMMonitorLocalhost(heartBeatJobScript)) {
						needPause = true;
					}
				}
			}
			
			if (needPause) {
				WebServiceClientProxy client = null;
				try {
					logger.info("Pause heartbeat");
					client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);
					client.getServiceV2().pauseHeartBeat(heartBeatJobScript.getAFGuid());
					result += 3;
				} catch (SOAPFaultException e) {
					String msg = WebServiceMessages.getResource(
							COLDSTANDBY_SETTING_PAUSE_HEARTBEAT_FAIL,heartBeatJobScript.getHeartBeatMonitorHostName());
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, -1, 
							Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "",
									"", "", "" }, afGuid);							
						logger.error(e);
						throw e;
				}
			}
		
			if( heartBeatJob.getJobStatus().getStatus().equals(HAJobStatus.Status.Active))
			{
				logger.info("The HaJobStatus is Active and will be set Canceled(pause).");
				heartBeatJob.unschedule();
				HAJobStatus jobStatus = new HAJobStatus();
				jobStatus.setStatus(HAJobStatus.Status.Canceled);
				heartBeatJob.setJobStatus(jobStatus);
				jobQueue.reStoreJob(heartBeatJob);
				result +=7;
			}
			String msg = WebServiceMessages.getResource(
					COLDSTANDBY_SETTING_PAUSE_HEARTBEAT,heartBeatJobScript.getHeartBeatMonitorHostName());
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg, "", "", "", "" }, localAFGuid);	
			
		}	
		return result;
	}
	
	/**
	 * return 1: both heartbeat job and failover job exist, 3: run pause on monitor side, 7: modify the heartbeat job status into Canceled, or the combined value 
	 * @throws ServiceException 
	 */
	public int pauseHeartBeat(String afGuid, boolean checkIfPlanPaused) throws ServiceException {
		logger.info("pauseHeartBeat() - start"); //$NON-NLS-1$
		String localAFGuid = evaluteAFGuid(afGuid);
		AFJob failoverJob = this.getFailoverJob(localAFGuid);
		if (isNullJob(failoverJob)){
			logger.info("pauseHeartBeat() - isNullJob");
			return 0;
		}
		FailoverJobScript failoverJobScript = (FailoverJobScript) failoverJob.getJobScript();
		if (isNullJobScript(failoverJobScript)){
			logger.info("pauseHeartBeat() - isNullJobScript");
			return 0;
		}			
		
		if (ManualConversionUtility.isVSBWithoutHASupport(failoverJobScript)){
			logger.info("pauseHeartBeat() - MSPManualConversion");
			return 0;
		}
			
		
//		if (checkIfPlanPaused && failoverJobScript.getIsPlanPaused()) {
//			logger.info("The plan is paused.");
//			throw generateAxisFault(FlashServiceErrorCode.Backup_BackupDisabled);
//		}

		String hbHostName = failoverJobScript.getProductionServerName();
		if (failoverJobScript.isVSphereBackup())
			hbHostName = failoverJobScript.getVSphereproxyServer().getVSphereProxyName();

		int ret = 0;
		if (failoverJobScript.getBackupToRPS()) {
			try {
				logger.info("Transfer the command to D2D agent and D2D agent will perform the pause operation.");
				ret = getD2DService(failoverJobScript).getServiceV2().pauseHeartBeatForD2D(localAFGuid);
			} catch (ServiceException e) {
				logger.error(e);
				throw(e);
			} catch (Throwable e) {
				logger.error(e);
				throw new ServiceException(FlashServiceErrorCode.Common_CantConnectHost, new String[]{hbHostName});
			}
		}
		else {
			ret = pauseHeartBeatForD2D(localAFGuid);
		}
		
		syncVCMStatus2Monitor(afGuid);
		return ret;
	}
	
	public void syncVCMStatus2Monitor(final String afGuid) {	
		Thread syncThread = new Thread(new Runnable() {

			@Override
			public void run() {
				WebServiceClientProxy monitorClient = null;
				try {
					monitorClient = VCMPolicyUtils.validateMonitor(afGuid, HAService.getInstance().getHeartBeatJobScript(afGuid), 
							HAService.getInstance().getReplicationJobScript(afGuid), HAService.getInstance().getFailoverJobScript(afGuid));
				} catch (Exception e) {
					logger.error("Fail to connect to monitor.");
					logger.error(e.getMessage());
				}
				
				try {
					if (monitorClient != null) {
						logger.info("Sync VCM status to monitor.");
						monitorClient.getServiceV2().syncVCMStatus2Monitor(afGuid);
					}
				} catch (Exception e) {
					logger.error("Fail to sync VCM status to monitor.");
					logger.error(e.getMessage());
				}

			}
		});
		CommonService.getInstance().getUtilTheadPool().submit(syncThread);
	}
	
	public long deregisterHeartBeatForHA(String afGuid, String converterID) {

		String localAFGuid = evaluteAFGuid(afGuid);
		synchronized (jobQueue) {
			
			HeartBeatJob heartBeatJob = (HeartBeatJob)jobQueue.findHeartBeatJobByID(localAFGuid);
			if(heartBeatJob != null){

				HeartBeatJobScript heartBeatJobScript = heartBeatJob.getJobScript();
				logger.info(String.format("The input converterID is %s and the converterID in hbjobscript is %s", converterID, heartBeatJobScript.getConverterID()));
				if (converterID != null && heartBeatJobScript.getConverterID() != null && !converterID.equalsIgnoreCase(heartBeatJobScript.getConverterID())) {
					logger.warn("The input converterID and the converterID in hbjobscript are different, so skip deregisterHeartBeatForHA for " + afGuid);
					return 0;
				}

				heartBeatJob.unschedule();
				jobQueue.remove(heartBeatJob);
			}
		}	
		return 0;
	}
	
	
	private int deregisterHeartBeatScriptMSP(String afGuid, String converterID) throws SOAPFaultException
	{
		logger.info("De-register the script of heart beat for " + afGuid);
		int result = 0;
		
		String localAFGuid = evaluteAFGuid(afGuid);
		
		synchronized (jobQueue) {
			HeartBeatJob heartBeatJob = (HeartBeatJob)jobQueue.findHeartBeatJobByID(localAFGuid);
			if(heartBeatJob == null)
				return result;

			HeartBeatJobScript heartBeatJobScript = heartBeatJob.getJobScript();
			
			logger.info(String.format("The input converterID is %s and the converterID in hbjobscript is %s", converterID, heartBeatJobScript.getConverterID()));
			if (converterID != null && heartBeatJobScript.getConverterID() != null && !converterID.equalsIgnoreCase(heartBeatJobScript.getConverterID())) {
				logger.warn("The input converterID and the converterID in hbjobscript are different, so skip stop heart beat for " + afGuid);
				return result;
			}
			
			WebServiceClientProxy client = null;
			try {
				logger.info("Start to De-register HA on the monitor");
				
				client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);
				client.getServiceV2().deregisterForHA(heartBeatJobScript.getAFGuid(), converterID);
				
				logger.info("Finish deleting the script files.");
			} catch (SOAPFaultException e) {
				
				logger.error("Failed to stop heart beat!!"+ heartBeatJobScript.getHeartBeatMonitorHostName() + ":" + heartBeatJobScript.getHeartBeatMonitorPort());
				String msg = null;

				if(HACommon.isTargetPhysicalMachine(localAFGuid))
					msg = WebServiceMessages.getResource(
						COLDSTANDBY_SETTING_STOPHEARTBEAT_FAIL,heartBeatJobScript.getHeartBeatMonitorHostName());
				else
					msg = WebServiceMessages.getResource(
							COLDSTANDBY_SETTING_UNREGSTER_VSPHERE_FAILOVERSCRIPT,
							HACommon.getProductionServerNameByAFGuid(localAFGuid), heartBeatJobScript.getHeartBeatMonitorHostName());
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, 
						Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "",
								"", "", "" }, localAFGuid);
				
				throw  e;
			}
		}
		return result;
	}
	
	/**
	 * return 1: both heartbeat job and failover job exist;
	 * 		  3: degister from Monitor successfully;
	 * 		  7: the failover job script's status is changed into UNREGISTERED
	 * 		 15: heartbeat job's status is changed into Pending
	 * @throws SOAPFaultException
	 */
	public int stopHeartBeat(String afGuid, String converterID) throws SOAPFaultException{
		logger.info("Stop heart beat for " + afGuid);
		int result = 0;
		
		String localAFGuid = evaluteAFGuid(afGuid);
		
		synchronized (jobQueue) {
			
			HeartBeatJob heartBeatJob = (HeartBeatJob)jobQueue.findHeartBeatJobByID(localAFGuid);
			if(heartBeatJob == null)
				return result;

			HeartBeatJobScript heartBeatJobScript = heartBeatJob.getJobScript();
			
			logger.info(String.format("The input converterID is %s and the converterID in hbjobscript is %s", converterID, heartBeatJobScript.getConverterID()));
			if (converterID != null && heartBeatJobScript.getConverterID() != null && !converterID.equalsIgnoreCase(heartBeatJobScript.getConverterID())) {
				logger.warn("The input converterID and the converterID in hbjobscript are different, so skip stop heart beat for " + afGuid);
				return result;
			}
			
			if (MonitorWebClientManager.isRVCMMonitorLocalhost(heartBeatJobScript)) {
				// Remove node from heart beat model file
				// The node in the heart beat model will display at left list when log in monitor
				HeartBeatModelManager.deRegisterHeartBeat(heartBeatJobScript.getAFGuid());
			} else {
				if (!heartBeatJobScript.getBackupToRPS()) {
					FailoverJob failoverJob = (FailoverJob)jobQueue.findFailoverJobByID(localAFGuid);
					if(failoverJob != null) {
						
						result = 1;
						FailoverJobScript failoverJobScript = failoverJob.getJobScript();
						if (failoverJobScript != null
								&& ((failoverJobScript.getState() == FailoverJobScript.REGISTERED && !ManualConversionUtility
										.isVSBWithoutHASupport(failoverJobScript)) || !MonitorWebClientManager
										.isRVCMMonitorLocalhost(heartBeatJobScript))) {
							WebServiceClientProxy client = null;
							try {
								logger.info("De-register HA on the monitor");
								
								client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);
								client.getServiceV2().deregisterForHA(heartBeatJobScript.getAFGuid(), converterID);
								result += 3;
								failoverJobScript.setState(FailoverJobScript.UNREGISTERED);
								HACoordinator.setHeartBeatStopped(localAFGuid,true);
								failoverJob.setJobScript(failoverJobScript);
								jobQueue.reStoreJob(failoverJob);
								result += 7;
							} catch (SOAPFaultException e) {
								
								logger.error("Failed to stop heart beat!!"+ heartBeatJobScript.getHeartBeatMonitorHostName() + ":" + heartBeatJobScript.getHeartBeatMonitorPort());
								String msg = null;

								if(HACommon.isTargetPhysicalMachine(localAFGuid))
									msg = WebServiceMessages.getResource(
										COLDSTANDBY_SETTING_STOPHEARTBEAT_FAIL,heartBeatJobScript.getHeartBeatMonitorHostName());
								else
									msg = WebServiceMessages.getResource(
											COLDSTANDBY_SETTING_UNREGSTER_VSPHERE_FAILOVERSCRIPT,
											failoverJobScript.getProductionServerName(), heartBeatJobScript.getHeartBeatMonitorHostName());
								
								HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, 
										Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "",
												"", "", "" }, localAFGuid);
								
								throw  e;
							}
						}
					}
				} else {
					// for Backup to RPS case
					result = 1;
					if (!ManualConversionUtility.isVSBWithoutHASupport(heartBeatJobScript)
						|| !MonitorWebClientManager.isRVCMMonitorLocalhost(heartBeatJobScript)) {
						WebServiceClientProxy client = null;
						try {
							logger.info("BackupToRPS: De-register HA on the monitor");
							
							client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);
							client.getServiceV2().deregisterForHA(heartBeatJobScript.getAFGuid(), converterID);
							result += 3;
							HACoordinator.setHeartBeatStopped(localAFGuid,true);
							result += 7;
						} catch (SOAPFaultException e) {
							
							logger.error("Failed to stop heart beat!!"+ heartBeatJobScript.getHeartBeatMonitorHostName() + ":" + heartBeatJobScript.getHeartBeatMonitorPort());
							String msg = null;

							if(HACommon.isTargetPhysicalMachine(localAFGuid))
								msg = WebServiceMessages.getResource(
									COLDSTANDBY_SETTING_STOPHEARTBEAT_FAIL,heartBeatJobScript.getHeartBeatMonitorHostName());
							else
								msg = WebServiceMessages.getResource(
										COLDSTANDBY_SETTING_UNREGSTER_VSPHERE_FAILOVERSCRIPT,
										HACommon.getProductionServerNameByAFGuid(localAFGuid), heartBeatJobScript.getHeartBeatMonitorHostName());
							
							HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, 
									Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "",
											"", "", "" }, localAFGuid);
							
							throw  e;
						}
					}
				}
			}
			
			heartBeatJob.unschedule();
			HAJobStatus jobStatus = new HAJobStatus();
			jobStatus.setStatus(HAJobStatus.Status.Pending);
			heartBeatJob.setJobStatus(jobStatus);
			jobQueue.reStoreJob(heartBeatJob);
			result += 15;
			String msg = WebServiceMessages.getResource(
					COLDSTANDBY_SETTING_STOPHEARTBEAT,heartBeatJobScript.getHeartBeatMonitorHostName());
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, 
					Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "",
							"", "", "" }, localAFGuid);
		}	
		return result;
	}
	/**
	 * Before invoke this method, invoke
	 * {@link #setHeartBeatJobScript(HeartBeatJobScript)} and invoke
	 * This method will schedule the heartbeat job in local machine
	 * return 1, scheduled heartbeat job, 3 the job's status changed into Active
	 * @throws SOAPFaultException
	 */
	public int startHeartBeat(String afGuid) {
		
		logger.debug("startHeartBeat() - start"); //$NON-NLS-1$
		int result = 0;
		String localAFGuid = evaluteAFGuid(afGuid);
		
		synchronized (jobQueue) {
			
			HeartBeatJob job = (HeartBeatJob)jobQueue.findHeartBeatJobByID(localAFGuid);
			if(job != null){
				
				job.schedule();
				result = 1;
				HAJobStatus jobStatus = new HAJobStatus();
				jobStatus.setStatus(HAJobStatus.Status.Active);
				job.setJobStatus(jobStatus);
				jobQueue.reStoreJob(job);
				result+=3;
				HeartBeatJobScript heartBeatJobScript = ((HeartBeatJob)job).getJobScript();
				String msg = WebServiceMessages.getResource(
							COLDSTANDBY_SETTING_START_HEARTBEAT,heartBeatJobScript.getHeartBeatMonitorHostName());
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, 
						Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "",
								"", "", "" }, localAFGuid);
				
				
				//When heart beat start, tell the corrdinator
				HACoordinator.setHeartBeatStopped(localAFGuid,false);
				
			}
		}
		
		logger.debug("startHeartBeat() - end"); //$NON-NLS-1$
		return result;
	}
	public boolean reStoreJob(AFJob job){
		synchronized (jobQueue) {
			return jobQueue.reStoreJob(job);
		}
	}
	
	public void reportProductionServerRoot(
			ReplicationJobScript replicationJobScript,String sessionGuids,String backupLocalTime){
		logger.debug("reportProductionServerRoot(ReplicationJobScript) - start"); //$NON-NLS-1$

		String afGuid = replicationJobScript.getAFGuid();
		HeartBeatJobScript jobScript = getHeartBeatJobScript(afGuid);
		if (jobScript == null)
			throw AxisFault
					.fromAxisFault("Failed to get HeartBeatJob Script from local job queue");

		String productionServer = HACommon.getProductionServerNameByAFRepJobScript(replicationJobScript);

		if (productionServer.equals("localhost"))
			throw AxisFault.fromAxisFault("Failed to get local host name");
		if (replicationJobScript.getReplicationDestination().isEmpty())
			throw AxisFault
					.fromAxisFault("Failed to get replication destinaiton from replication job script");
		
		
		ProductionServerRoot rsr = new ProductionServerRoot();
		rsr.setProductionServerAFUID(afGuid);
		rsr.setProductionServerHostname(productionServer);
		
		try {
			// Set most recent replication time
			// the most recent replication time will show on summary panel when login monitor
			String xml = CommonUtil.getRepositoryConfPath();
			ProductionServerRoot psr = RepositoryUtil.getInstance(xml).getProductionServerRoot(afGuid);
			rsr.setMostRecentRepStatus(psr.getMostRecentRepStatus());
			rsr.setMostRecentRepTime(psr.getMostRecentRepTime());
			rsr.setMostRecentRepTimeMilli(psr.getMostRecentRepTimeMilli());
			RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(afGuid);
			rsr.setMostRecentRepDuration(jobMonitor.getRepJobElapsedTime());
		} catch (Exception e) {
			logger.error("Failed to get the production server root from repository.xml.");
		}
		
		ReplicaRoot rr = null;
		long replicateTime = -1;
		ReplicationDestination replicationDestination = replicationJobScript.getReplicationDestination().get(0);
		switch (replicationDestination.getDestProtocol()) {
		case SharedFolder:
			break;
		case HeartBeatMonitor:
				
				String  repSubRoot = null;
				try {
					String xml = CommonUtil.getRepositoryConfPath();
					RepositoryUtil repository = RepositoryUtil.getInstance(xml);
					repSubRoot = repository.getProductionServerRoot(afGuid).getReplicaRoot().getRepSubRoot();
					
			} catch (Exception e) {}

			ARCFlashStorage as = (ARCFlashStorage) replicationDestination;
			List<DiskDestination> diskDestinations = as.getDiskDestinations();
			Collections.sort(diskDestinations,new Comparator<DiskDestination>() {
				@Override
				public int compare(DiskDestination dd1,DiskDestination dd2) {
					try {
						return dd1.getDisk().getDiskNumber() - dd2.getDisk().getDiskNumber();
					} catch (Exception e2) {
						return 0;
					}
				}
			});
			TransServerReplicaRoot temp2 = new TransServerReplicaRoot();
			temp2.setSessionGuids(sessionGuids);
			String rootPath = diskDestinations.get(0).getStorage()
						.getName();
				if (!rootPath.endsWith("\\"))
					rootPath += "\\";
				logger.info("RootPath in Monitee: rootPath=" + rootPath
						+ productionServer);
				
				temp2.setRepSubRoot(repSubRoot);
				temp2.setRootPath(rootPath + productionServer);
				temp2.setLatestReplicatedSession(replicationJobScript
						.getSession());
				temp2.setVmuuid(as.getVmUUID());
				temp2.setVmname(as.getVirtualMachineDisplayName());
				temp2.setBackupLocalTime(backupLocalTime);
				replicateTime = replicationJobScript.getReplicateTime();
				temp2.setReplicaTime(replicateTime);
				List<DiskDestination> tmp = new ArrayList<DiskDestination>();
				for (DiskDestination diskDestination : diskDestinations) {

					DiskDestination diskDest = diskDestination.deepCopy();
					String path = diskDestination.getStorage().getName();
					if (!path.endsWith("\\")) {
						path += "\\";
					}
					diskDest.getStorage().setName(
							path + productionServer + "\\");
					tmp.add(diskDest);

			}
			temp2.getDiskDestinations().addAll(tmp);
				
			rr = temp2;
			
			rsr.setMaxSnapshotCount(HACommon.getMaxSnapshotCountForHyperV(afGuid));
			
			break;
		
		case VMwareESX: 
			
			VMwareESXStorage esxStorage = (VMwareESXStorage) replicationDestination;
			VMWareESXHostReplicaRoot esxHost = new VMWareESXHostReplicaRoot();
			esxHost.setVmuuid(esxStorage.getVmUUID());
			esxHost.setVmname(esxStorage.getVmName());
			esxHost.setLatestReplicatedSession(replicationJobScript
						.getSession());
			replicateTime = replicationJobScript.getReplicateTime();
			esxHost.setReplicaTime(replicateTime);
			esxHost.getDiskDestinations().addAll(
						replicationDestination.getDiskDestinations());
			rr = esxHost;
			
			break;
			
		case VMwareVCenter: 
		
			VMwareVirtualCenterStorage centerStorage = (VMwareVirtualCenterStorage) replicationDestination;
			VMWareVirtualCenterReplicaRoot virtualCenter = new VMWareVirtualCenterReplicaRoot();
			virtualCenter.setVmuuid(centerStorage.getVmUUID());
			virtualCenter.setVmname(centerStorage.getVmName());
			replicateTime = replicationJobScript.getReplicateTime();
			virtualCenter.setReplicaTime(replicateTime);
			virtualCenter.getDiskDestinations().addAll(
						replicationDestination.getDiskDestinations());
			rr = virtualCenter;
		
			break;
		
		}
		if (rr == null)
			throw AxisFault
					.fromAxisFault("Unknown replication destination type.");

		rsr.setRetentionCount(replicationJobScript.getStandbySnapshots());
		rsr.setReplicaRoot(rr);
		
//		if(replicateTime > 0){
//			rsr.clearMostRecentReplic();
//		}
		
		String ProductionServerRootStr = "";
		WebServiceClientProxy client = null;
		try {
			client = MonitorWebClientManager.getMonitorWebClientProxy(jobScript);
		} catch (Exception e) {
			logger.error("Fail to connect to monitor", e); 
		}

		try {
			ProductionServerRootStr = CommonUtil.marshal(rsr);

		} catch (JAXBException e) {
			logger.error("reportProductionServerRoot(ReplicationJobScript)", e); //$NON-NLS-1$
			throw AxisFault.fromAxisFault("Failed to marshal production server root");
		}
		
		// Save to monitor
		if (client != null) {
			try{
				client.getServiceV2().reportProductionServerRoot(ProductionServerRootStr);
			} catch (Exception e) {
				logger.error("Fail to reportProductionServerRoot to the monitor." + e.getMessage());
			}
		}
		
		// Save to local
		try {
			RepositoryUtil.getInstance(CommonUtil.getRepositoryConfPath()).saveProductionServerRoot(rsr);
		} catch (Exception e) {
			logger.error("Fail to save production server root.");
		}

		logger.debug("reportProductionServerRoot(ReplicationJobScript) - end"); //$NON-NLS-1$
	}
	
	public void reportProductionServerRoot(
			ReplicationJobScript replicationJobScript,String sessionGuids) {
		
		reportProductionServerRoot(replicationJobScript, sessionGuids,"");
		
	}

	public long resumeVSBJob(String afGuid) throws ServiceException {
		ReplicationJobScript repJobScript = (ReplicationJobScript)getReplicationJobScript(afGuid);
		if (repJobScript == null) {
			logger.error("Failed to get replication AFJob script.");
			return 1;
		}
		
		if (repJobScript.getIsPlanPaused()) {
			logger.info("The plan is paused.");
			throw generateAxisFault(FlashServiceErrorCode.VCM_VSB_DISABLED);
		}
		
		return startReplication(afGuid);
	}
	
	public long startReplication(String afGuid) {
		
		return startReplication(afGuid, true);
	}

	// 0: success, 1: failure, 2: skip
	private long startReplication(String afGuid, boolean needSubmitRPSConversion) {
		
		logger.info("startReplication() - start: " + afGuid); //$NON-NLS-1$

		String localAFGuid = evaluteAFGuid(afGuid);
		
		ReplicationJobScript repJobScript = (ReplicationJobScript)getReplicationJobScript(localAFGuid);
		if (repJobScript == null) {
			logger.error("Failed to get replication AFJob script.");
			return 1;
		}
			
		// 1, Check If There Is Already A Job Running -
		boolean jobExist = CommonService.getInstance().ifRepJobExist(localAFGuid);
		if (jobExist) {
			
			logger.info("Another virtual standby job is running, so skip it.");

			Date date1 = new Date();
		  	SimpleDateFormat formatter = new SimpleDateFormat(CommonNativeInstance.getICommonNative().getDateTimeFormat().getTimeDateFormat(),DataFormatUtil.getDateFormatLocale());
			String dateString = formatter.format(date1);
			String msg = ReplicationMessage.getResource(ReplicationMessage.SKIP_ONE_REPLICAION, dateString);
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "", "",
							"", "" }, localAFGuid);
			
			BaseReplicationCommand.setPendingJob(repJobScript);
			return 2;  
		}

		// 2, Check If VSB converter is changed
		if (!repJobScript.getBackupToRPS() || !needSubmitRPSConversion) {
			boolean isSameConverter = checkConverterSameForNode(repJobScript.getPlanUUID(), localAFGuid);
			if (!isSameConverter) {
				logger.warn(String.format("The converter for node %s is changed, need to un-deploy VSB task", localAFGuid));
				
				logger.warn(WebServiceMessages.getResource("autoUnassignPolicy"));
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, new String[] { 
						WebServiceMessages.getResource("autoUnassignPolicy"), "", "", "", "" }, afGuid);
				
				autoUnapplyVCMPolicy(afGuid);
				return 1;
			}
		}
		
		// 3, Check If plan is paused
		if (repJobScript.getIsPlanPaused()) {
			
			logger.info("Plan is paused, so skip the virtual standby job.");

			String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICAIION_PLAN_PAUSED);
							HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, localAFGuid);

			BaseReplicationCommand.setPendingJob(repJobScript);
			createSessionBitmap(repJobScript);
			return 2;  
		}

		// Issue 50764
		// If auto replication is false, create bitmap and return here, then the monitor progress bar does not display.
		// 4, Check If VSB job is paused
		if (repJobScript.getAutoReplicate() == false) {
			
			logger.info("AutoReplicate is false, so skip the virtual standby job.");

			String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICAIION_PAUSED);
							HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, localAFGuid);
							
			BaseReplicationCommand.setPendingJob(repJobScript);
			createSessionBitmap(repJobScript);
			return 2;  
		}

		if (repJobScript.getBackupToRPS() && needSubmitRPSConversion) {
			logger.info("Need to submit the conversion job on RPS server.");
			long ret = submitRPSConversionJob(localAFGuid, repJobScript);
			if (ret != 0) {
				logger.error("Fail to submit the conversion job on RPS server.");
			}
		} else {
			synchronized (jobQueue) {
				AFJob afjob = jobQueue.findReplicationJobByID(localAFGuid);
				if (afjob == null) {
					logger.error("Failed to get AFJob.");
					return 1;
				}
				logger.info("schedule replication job.");
				afjob.schedule();
			}
		}

		logger.info("startReplication() - end"); //$NON-NLS-1$
		return 0;
	}
	
	public long createSessionBitmap(String d2dUUID) {
		long ret = -1;
		try {
			ReplicationJobScript jobScript = getReplicationJobScript(d2dUUID);
			if (jobScript == null)
				return ret;
			
			return createSessionBitmap(jobScript);
		} catch (Exception e) {
			logger.error("Exception occurred while creating session bitmap:" + e.getMessage(), e);
		}
		return ret;
	}

	public long createSessionBitmap(ReplicationJobScript jobScript) {

		if (ManualConversionUtility.isVSBWithoutHASupport(jobScript) && !jobScript.getBackupToRPS()) {
			logger.info("Skip create session bitmap for remote VSB with RHA integrated.");
			return 0;
		}
		
		long ret = -1;

		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		if (nativeFacade == null)
			return ret;

		BackupDestinationInfo backupDestInfo = null;
		try {
			backupDestInfo = BaseReplicationCommand.getBackupDestinationInfo(jobScript, false, -1);
		} catch (Exception e) {
			logger.error("Failed to get backup destination, " + e.getMessage());
			return ret;
		}
		String remotePath = backupDestInfo.getBackupDestination();
		boolean isRemote = CommonUtil.isRemote(remotePath);
		try {
			if (isRemote) {
				try {
					BaseReplicationCommand.connectToRemote(backupDestInfo, jobScript.getAFGuid(), -1);
				} catch (HAException e) {
					logger.error("Failed to connect to configuration destination, " + e.getMessage());
				}
			}

			ret = nativeFacade.CreateSessionBitmap( backupDestInfo.getBackupDestination(), backupDestInfo.getBackupDestination(), jobScript.getBackupDestType());
			logger.info("Create session bitmap returns [" + ret + "]");

		} catch (Exception e) {
			logger.error("Exception occurred while creating session bitmap:" + e.getMessage(), e);
			String bitMapError = ReplicationMessage.getResource( ReplicationMessage.REPLICATION_FAILED_CREATE_BITMAP );
			HACommon.addActivityLogByAFGuid(
					Constants.AFRES_AFALOG_ERROR, -1, Constants.AFRES_AFJWBS_GENERAL,
					new String[] { bitMapError, "", "", "", "" },
					jobScript.getAFGuid() );
		} finally {
			if (isRemote) {
				try {
					BaseReplicationCommand.closeRemoteConnect(backupDestInfo);
				} catch (Exception e) {
					logger.debug(e.getMessage());
				}
			}
		}
		return ret;
	}
	
	public long convertNow(ConversionJobArg jobArg) {
		
		// TODO launch convert process here
		String afGuid = jobArg.getD2duuid();
		String submitted = null;
		try {
			submitted = CommonUtil.getSubmitIncrementalFlag(afGuid, true);
		} catch (Exception e) {
			logger.warn("Fail to get SubmitIncrementalFlag for " + afGuid);
		}
		
		logger.info("SubmitIncremental Flag for " + afGuid + " is " + submitted);

		return startReplication(afGuid, false);
	}
	
	public ProductionServerRoot GetProductionServerRoot(String d2dNodeUUID, boolean deleteOrignal) {
		String xml = CommonUtil.D2DHAInstallPath+"Configuration\\repository.xml";
		RepositoryUtil repository = RepositoryUtil.getInstance(xml);
		if (repository == null)
			return null;
		ProductionServerRoot ret = null;
		try {
			 ret = repository.getProductionServerRoot(d2dNodeUUID);
		} catch (HAException e) {
			return null;
		}
		if (ret != null){
			try {
				repository.removeProductionServerRoot(ret);
			} catch (HAException e) {
			}
		}
		return ret;
	}
	
	
	public void syncADRConfigureToVCM(final String afGuid, final long jobID, final BackupDestinationInfo backupDestinationInfo) {
		try {
			ADRConfigure adrConfigInfo = null;
			String msg = "";
			try {
				adrConfigInfo = HACommon.getADRConfiguration(afGuid, backupDestinationInfo);
			} catch (ServiceException e) {
				logger.warn("syncADRConfigureToVCM failed - getADRConfiguration: Null or empty backup configuration destination!");
			} catch (HAException e) {
				logger.warn("syncADRConfigureToVCM failed - getADRConfiguration: Failed to connect to configuration destination");
			}
			if (adrConfigInfo == null || adrConfigInfo.getNetadapters() == null || adrConfigInfo.getNetadapters().size() == 0) {
				logger.info("No ADRConfigure.xml, we skip synchronize, Virtual Standby will parse policy directly.");
				return;
			}
	
			D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
			EdgeRegInfo edgeRegInfo = edgeReg.getEdgeRegInfo(ApplicationType.VirtualConversionManager);
			if(edgeRegInfo==null) {
				logger.info("Edge configration file don't exist or reading failed! Stop synchronizing ADRConfigure.xml to VCM! ");
				return;
			}
			try {
				IEdgeD2DService proxy = com.ca.arcflash.webservice.toedge.WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(),IEdgeD2DService.class);
				proxy.validateUserByUUID(edgeRegInfo.getEdgeUUID());
				proxy.syncADRConfigureToVCM(afGuid, adrConfigInfo);
				msg = FailoverMessage.getResource(FailoverMessage.SYNC_ADRCONFIGURE_TO_VCM_SUCCESSFUL);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "", "", "", "" }, afGuid);
			} catch (EdgeServiceFault e) {
				msg = FailoverMessage.getResource(FailoverMessage.SYNC_ADRCONFIGURE_TO_VCM_FAILED);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "", "", "", "" }, afGuid);
				logger.warn("syncADRConfigureToVCM failed - EdgeServiceFault", e);
			}
		} catch (Exception e) {
			logger.warn("Failed to sync ADRConfigure to CPM", e);
		}
	}

	public int updateRepJobScript(ReplicationJobScript newReplicationJobScript) {
		if(newReplicationJobScript==null)
			return 1;
		
		synchronized (jobQueue) {
			Collection<AFJob> jobs = jobQueue.findByJobType(JobType.Replication);
			for (AFJob job : jobs) {
				ReplicationJob repJob = (ReplicationJob) job;
				if (newReplicationJobScript.getAFGuid().equals(
						repJob.getJobScript().getAFGuid())) {
					repJob.setJobScript(newReplicationJobScript);
					jobQueue.storeJob(repJob);
				}
			}
		}
		
		return 0;
	}
	
	public void setReplicationCommand( ReplicationJobScript replicationJobScript )
	{
		List<ReplicationDestination> destinations = replicationJobScript.getReplicationDestination();
		if (!destinations.isEmpty())
		{
			BaseReplicationCommand replicationCommand = null;
			
			switch (destinations.get(0).getDestProtocol())
			{
			case SharedFolder:
				replicationCommand = new ShareFolderReplicationCommand();
				break;
				
			case HeartBeatMonitor:
				replicationCommand = new TransServerReplicationCommand();
				break;
				
			case VMwareESX:
				replicationCommand = new VMwareESXReplicationCommand();
				break;
				
			case VMwareVCenter:
				replicationCommand = new VMwareCenterServerReplicationCommand();
				break;
			}
			
			if (replicationCommand != null)
			{
				replicationCommand.setGenerateType( replicationJobScript.getGenerateType() );
				replicationJobScript.setReplicationCommand( replicationCommand );
			}
		}
	}
	
	public void setReplicationJobScript(
			ReplicationJobScript replicationJobScript) {
		logger.debug("setReplicationJobScript(ReplicationJobScript) - start"); //$NON-NLS-1$
		
		setReplicationCommand( replicationJobScript );

		synchronized (jobQueue) {
			Collection<AFJob> jobs = jobQueue.findByJobType(JobType.Replication);
			for (AFJob job : jobs) {
				ReplicationJob repJob = (ReplicationJob) job;
				if (replicationJobScript.getAFGuid().equals(
						repJob.getJobScript().getAFGuid())) {
					jobQueue.remove(repJob);
					break;
				}
			}
			jobQueue.add(replicationJobScript);
		}

		logger.debug("setReplicationJobScript(ReplicationJobScript) - end"); //$NON-NLS-1$
	}

	public ReplicationJobScript getReplicationJobScript(String afGuid) {
		logger.debug("getReplicationJobScript() - start"); //$NON-NLS-1$

		String localAFGuid = evaluteAFGuid(afGuid);
		
		synchronized (jobQueue) {
			Collection<AFJob> jobs = jobQueue
					.findByJobType(JobType.Replication);
			// [li$fe01] Should always have 1 replication job in the queue at
			// most.
			for (AFJob afJob : jobs) {
				ReplicationJobScript jobScript = (ReplicationJobScript)afJob.getJobScript();
				if(jobScript.getAFGuid().equals(localAFGuid)){
					return jobScript;
				}
			}
		}

		logger.debug("getReplicationJobScript() - end"); //$NON-NLS-1$
		return null;
	}

	public List<String> getRPSReplicationJobGUIDs() {
		logger.debug("getRPSReplicationJobGUIDs() - start"); //$NON-NLS-1$

		List<String> result = new ArrayList<String>();
	
		synchronized (jobQueue) {
			Collection<AFJob> jobs = jobQueue
					.findByJobType(JobType.Replication);

			for (AFJob afJob : jobs) {
				ReplicationJobScript jobScript = (ReplicationJobScript)afJob.getJobScript();
				if (jobScript.getBackupToRPS())
				{
					result.add(jobScript.getAFGuid());
				}
			}
		}

		logger.debug("getRPSReplicationJobGUIDs() - end"); //$NON-NLS-1$
		return result;
	}
	
	public List<String> getReplicationJobGUIDs() {
		logger.debug("getReplicationJobGUIDs() - start"); //$NON-NLS-1$

		List<String> result = new ArrayList<String>();
	
		synchronized (jobQueue) {
			Collection<AFJob> jobs = jobQueue
					.findByJobType(JobType.Replication);

			for (AFJob afJob : jobs) {
				ReplicationJobScript jobScript = (ReplicationJobScript)afJob.getJobScript();
				result.add(jobScript.getAFGuid());
			}
		}

		logger.debug("getReplicationJobGUIDs() - end"); //$NON-NLS-1$
		return result;
	}

	/**
	 * from registry get the GUID for this machine for authentication 
	 * 
	 * @return
	 */
	public String retrieveCurrentAuthUUID(boolean needDecrypt) {
		String authUUID = CommonService.getInstance().getLoginUUID();
		
		if (needDecrypt)
			authUUID = getNativeFacade().decrypt(authUUID);
		return authUUID;
	}
	
	/**
	 * from registry get the Node ID for this machine to identify the node uniquely
	 * 
	 * @return
	 */
	public String retrieveCurrentNodeID() {
		return CommonService.getInstance().getNodeUUID();
	}
	
	public Scheduler GetScheduler() throws SchedulerException {
		return scheduler != null ? scheduler : StdSchedulerFactory.getDefaultScheduler();
	}
	
	public void startVsbScheduler() {
		int threadCount = getQuartzThreadCountFromReg();
		logger.info(String.format("Set the thread count of VSB scheduler to %d.", threadCount));
		Properties properties = new Properties();
		properties.setProperty("org.quartz.scheduler.instanceName",  "QuartzScheduler-VSB");
		properties.setProperty("org.quartz.scheduler.rmi.export", "false");
		properties.setProperty("org.quartz.scheduler.rmi.proxy", "false");
		properties.setProperty("org.quartz.scheduler.wrapJobExecutionInUserTransaction", "false");
		properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		properties.setProperty("org.quartz.threadPool.threadCount", String.valueOf(threadCount));
		properties.setProperty("org.quartz.threadPool.threadPriority", "5");
		properties.setProperty("org.quartz.jobStore.misfireThreshold", String.valueOf(60000));
		properties.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
		properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
		try {
			scheduler = new StdSchedulerFactory(properties).getScheduler();
			scheduler.start();
			logger.info("Start the VSB scheduler successfully.");
		} catch (SchedulerException e) {
			logger.warn("Fail to create exclusive scheduler for VSB, using default scheduler instead.", e);
			scheduler = null;
		}
		AFJob.setVsbScheduler(scheduler);
	}
	
	public void shutdownVsbScheduler() {
		Scheduler scheduler = AFJob.vsbScheduler;
		if (scheduler != null) {
			try {
				scheduler.shutdown();
				logger.info("Shutdown the vsb scheduler successfully.");
			} catch (SchedulerException e) {
				logger.warn("Fail to shutdown vsb scheduler.");
			}
		}
	}
	
	public void destroy() {
		shutdownVsbScheduler();
	}
	
	public int getQuartzThreadCountFromReg() {
		int threadCount = VSB_THREAD_COUNT_DEFAULT_VALUE;
		try {
			threadCount = FlashSwitch.getSwitchIntFromReg(
							FlashSwitchDefine.VSBModule.SWT_VSB_THREAD_COUNT,
							VSB_THREAD_COUNT_DEFAULT_VALUE,
							FlashSwitchDefine.VSB_REG_ROOT);
		} catch (Exception e) {
			logger.warn("Fail to retrieve VSBThreadCount from registry.", e);
		}
		
		return threadCount;
	}

	public int getSyncIntervalInMillisecond() {
		int vsbStatusSyncInterval = VSB_STATUS_SYNC_INTERVAL_DEFAULT_VALUE;
		try {
			vsbStatusSyncInterval = FlashSwitch.getSwitchIntFromReg(
							FlashSwitchDefine.VSBModule.SWT_VSB_STATUS_SYNC_INTERVAL,
							VSB_STATUS_SYNC_INTERVAL_DEFAULT_VALUE,
							FlashSwitchDefine.VSB_REG_ROOT);
		} catch (Exception e) {
			logger.warn("Fail to retrieve HATransPort from registry.", e);
		}
		
		return vsbStatusSyncInterval * 1000;
	}

	public boolean getEnforeVDDKNBDFlag(){
		boolean isNBDEnfored = false;
		try {
			int vddkEnforceNBDValue = FlashSwitch.getSwitchIntFromReg(
							FlashSwitchDefine.VSBModule.SWT_VSB_VDDK_ENFORCE_NBD_FLAG,
							VSB_VDDK_ENFORCE_NBD_FLAG_DEFAULT_VALUE,
							FlashSwitchDefine.D2D_REG_ROOT);

			isNBDEnfored = vddkEnforceNBDValue== 1? true:false;
		} catch (Exception e) {
			logger.warn("Fail to retrieve VDDKEnforceNBD from registry.", e);
		}
		return isNBDEnfored;
	}

	public int getHATransPort(){
		int haPort = VSB_HATRANS_SERVER_PORT_DEFAULT_VALUE;
		try {
			haPort = FlashSwitch.getSwitchIntFromReg(
							FlashSwitchDefine.VSBModule.SWT_VSB_HATRANS_SERVER_PORT,
							VSB_HATRANS_SERVER_PORT_DEFAULT_VALUE,
							FlashSwitchDefine.VSB_REG_ROOT);
		} catch (Exception e) {
			logger.warn("Fail to retrieve HATransPort from registry.", e);
		}
		
		return haPort;
	}

	public String[] getGuidsOfReplicatedSessionsFromVMSnapshots(
			ReplicationJobScript jobScript) throws Exception {
		logger.debug("getGuidsOfReplicatedSessionsFromVMSnapshots(ReplicationDestination) - start"); //$NON-NLS-1$

		String[] result = new String[0];
		ReplicationDestination des = jobScript.getReplicationDestination().get(0);
		
		String afGuid = jobScript.getAFGuid();
		
		WebServiceClientProxy client;
		switch (des.getDestProtocol()) {
		case HeartBeatMonitor:

			HeartBeatJobScript heartBeatScript = HAService.getInstance().getHeartBeatJobScript(afGuid);

			client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatScript);
			
			String destFolder = getRepDestination(afGuid);
			
			logger.info("destFoler: " + destFolder);			

			logger.info("begin call monitor getReplicatedSessionsFromServer");
			result = client.getServiceV2().getReplicatedSessionsFromServer(destFolder,afGuid);
			logger.info("end call monitor getReplicatedSessionsFromServer");

			logger
			.debug("getGuidsOfReplicatedSessionsFromVMSnapshots(ReplicationDestination) - end"); //$NON-NLS-1$
			return result;
		
		default: 
			String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_DEST_DOES_EXIST, des.toString());
			throw new Exception(msg);
		}	
	}

	public VWWareESXNode[] getESXNodeList(String esxServer, String username,
			String passwod, String protocol, int port) {
		logger
				.debug("getESXNodeList(String, String, String, String, int) - start"); //$NON-NLS-1$
		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
					.getCAVMwareVirtualInfrastructureManager(esxServer,
							username, passwod, protocol, true, port);
			if (vmwareOBJ == null) {
				logger
						.debug("getESXNodeList(String, String, String, String, int) - end"); //$NON-NLS-1$
				return null;
			}
			ArrayList<ESXNode> nodes = vmwareOBJ.getESXNodeList();
			if (nodes == null || nodes.size() == 0) {
				logger
						.debug("getESXNodeList(String, String, String, String, int) - end"); //$NON-NLS-1$
				return null;
			}
			VWWareESXNode[] nodesArr = new VWWareESXNode[nodes.size()];
			for (int i = 0; i < nodes.size(); i++) {
				ESXNode node = nodes.get(i);
				nodesArr[i] = new VWWareESXNode(node.getEsxName(), node
						.getDataCenter());
			}

			logger
					.debug("getESXNodeList(String, String, String, String, int) - end"); //$NON-NLS-1$
			return nodesArr;
		} catch (Exception e) {
			
			logger.error("Failed to getESXNodeList!!!");
			throw  AxisFault.fromAxisFault("Failed to getESXNodeList!!!");
		}
		finally {
			if(vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				}
				catch(Exception e) {
				}
			}
		}
	}

	public String[] getESXHostDataStoreList(String esxServer, String username,
			String passwod, String protocol, int port, VWWareESXNode host)
			{
		logger
				.debug("getESXHostDataStoreList(String, String, String, String, int, VWWareESXNode) - start"); //$NON-NLS-1$

		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
					.getCAVMwareVirtualInfrastructureManager(esxServer,
							username, passwod, protocol, true, port);
			if (vmwareOBJ == null) {
				logger
						.debug("getESXHostDataStoreList(String, String, String, String, int, VWWareESXNode) - end"); //$NON-NLS-1$
				return null;
			}

			ESXNode esxNode = new ESXNode();
			esxNode.setDataCenter(host.getDataCenter());
			esxNode.setEsxName(host.getEsxName());

			ArrayList<String> dataStorages = vmwareOBJ
					.getESXHostDataStoreList(esxNode);
			if (dataStorages == null || dataStorages.size() == 0) {
				logger
						.debug("getESXHostDataStoreList(String, String, String, String, int, VWWareESXNode) - end"); //$NON-NLS-1$
				return null;
			}
			String[] storagesAttr = new String[dataStorages.size()];
			String[] returnStringArray = dataStorages.toArray(storagesAttr);
			logger
					.debug("getESXHostDataStoreList(String, String, String, String, int, VWWareESXNode) - end"); //$NON-NLS-1$
			return returnStringArray;
		} catch (Exception e) {
			logger.error("Failed to getESXHostDataStoreList!!!");
			throw  AxisFault.fromAxisFault("Failed to getESXHostDataStoreList!!!");
		}
		finally {
			if(vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				}
				catch(Exception e) {
				}
			}
		}
		
	}

	public ReplicationDestination getReplicationDestinaiton(
			ReplicationJobScript jobScript) {
		logger.debug("getReplicationDestinaiton(ReplicationJobScript) - start"); //$NON-NLS-1$

		List<ReplicationDestination> reps = jobScript
				.getReplicationDestination();
		if (reps.isEmpty())
			return null;
		else
			return reps.get(0);
	}

	public void validateMonitorUserCredential(String serverName, String proto, int port,
			String username, String password) throws ServiceException {
		logger
				.debug("validateMonitorUserCredential(String, int, String, String) - start"); //$NON-NLS-1$

		try {
			WebServiceClientProxy client = WebServiceFactory.getFlashServiceV2(proto,serverName,port);

			//FlashHAServiceClient client = new FlashHAServiceClient(proto,serverName,port);
			client.getServiceV2().validateUser(VCMPolicyUtils.GetUserNameFromUserTextField(username), password,VCMPolicyUtils.GetDomainNameFromUser(username));
		} catch (SOAPFaultException e) {
			logger.error(String.format(
					"Failed to test monitor connection {0}:{1}", serverName,
					port));
			throw generateAxisFault(FlashServiceErrorCode.Common_CantConnectService);
		}

		logger
				.debug("validateMonitorUserCredential(String, int, String, String) - end"); //$NON-NLS-1$
	}
	/*private String GetDomainNameFromUser (String strUserInput)
	{
		String strDomain = "";
		
		if (strUserInput == null || strUserInput.isEmpty())
			return strDomain;
		
		int pos = strUserInput.indexOf("\\"); // ex) tant-a01\kimwo01
		if (pos == -1) // If not exist.
		{
			// Normal user input without domain field.
		}
		else 
		{
			// Extract domain part
			strDomain = strUserInput.substring(0, pos);
		}
		return strDomain;
	}
	private String GetUserNameFromUserTextField (String strUserInput)
	{
		String strUser = "";
		
		if (strUserInput == null || strUserInput.isEmpty())
			return strUser;
		
		int pos = strUserInput.indexOf("\\"); // ex) tant-a01\kimwo01
		if (pos == -1) // If not exist.
		{
			// Normal user input without domain field.
			strUser = strUserInput;
		}
		else 
		{
			// Extract user name part
			strUser = strUserInput.substring(pos+1);
		}
		return strUser;
	}*/
	
	public long destroyHyperVVM(String vmGuid) {
		long handle = 0;
		try {
			handle = HyperVJNI.OpenHypervHandle("", "", "");
		} catch (HyperVException he) {
			throw AxisFault.fromAxisFault(
					"Failed to open hyperv manger handle",
					MonitorWebServiceErrorCode.HyperV_Operation_Error);
		}
		try {
			HyperVJNI.DestroyVM(handle, vmGuid);
		} catch (HyperVException e) {

			String msg = HyperVJNI.GetLastErrorMessage();
			throw AxisFault.fromAxisFault(msg,
					MonitorWebServiceErrorCode.HyperV_Operation_Error);

		} finally {
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (HyperVException hyperVe) {
				logger.error(hyperVe.getMessage());
			}
		}
		return 0;
	}
	
	public String createHyperVVM(String arcDesStr, String afGuid, int vmGeneration) {

		long handle = 0;
		ARCFlashStorage ardDes = null;
		try {
			ardDes = CommonUtil.unmarshal(arcDesStr, ARCFlashStorage.class);
		} catch (JAXBException e) {
			if (logger.isDebugEnabled())
				logger.debug("createHyperVVM failed:" + e.getMessage());

			throw  AxisFault.fromAxisFault("Failed to unmarshal ARCFlashStorage");
		}
		// if have no such HyperV VM for this afguid, create one
		// we cannot rely on HeatBeatModel manager
		try {
			handle = HyperVJNI.OpenHypervHandle("", "", "");
		} catch (HyperVException he) {
			throw  AxisFault.fromAxisFault("Failed to open hyperv manger handle",
					MonitorWebServiceErrorCode.HyperV_Operation_Error);
		}

		VirtualMachineInfo vmInfo = null;

		try {
			
			vmInfo = createHyperVVM(handle, afGuid, ardDes, vmGeneration);
		
		} catch (HyperVException e) {
			
			String msg = HyperVJNI.GetLastErrorMessage();
			throw  AxisFault.fromAxisFault(
					msg,
					MonitorWebServiceErrorCode.HyperV_Operation_Error);

		} finally {
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (HyperVException hyperVe) {
				logger.error(hyperVe.getMessage());
			}
		}
		
		try {
			ReplicationService.startHATransServerDirectly();
		} catch (Exception e) {
			logger.error("Fails to start HATransServer.exe process", e);
		}

		return vmInfo.getVmGUID();
	
	}

	public boolean IsVmFileExist(String name)
    {
        try{
            File file = new File(name);  

            if (!file.exists())    
            {     
                return false;
            } 
		    else 
            {
                return true;
            }
        }catch (SecurityException e)
		{
			logger.warn("IsVmFileExist - SecurityException");
			return false;
		}
    }
    
	public String generateRemoteRepositoryPath(String vmname,
			ReplicationJobScript jobScript) {
		ReplicationDestination replicationDestinaiton = HAService.getInstance().getReplicationDestinaiton(jobScript);
		ARCFlashStorage arcDes = (ARCFlashStorage) replicationDestinaiton;
		String path = arcDes.getVMLocationPath();
		if (!path.endsWith("\\"))
			path += "\\";
		path += vmname;
		if (!path.endsWith("\\"))
			path += "\\";
		return path + "repository.xml";
		
//		String subRoot = arcDes.getRepSubRoot();
//		String repSubRoot = StringUtil.isEmptyOrNull(subRoot) ? vmname + "\\" + "sub0001" : subRoot;
//		String moniteeHost = arcDes.getMoniteeHostName();
//		if (moniteeHost == null) {
//			moniteeHost = HACommon.getProductionServerNameByAFRepJobScript(jobScript);
//		}
//		DiskDestination diskDestination = arcDes.getDiskDestinations().get(0);
//		String repDest = diskDestination.getStorage().getName();
//		if (!repDest.endsWith("\\")) {
//			repDest += "\\";
//		}
//		if (!repSubRoot.endsWith("\\")) {
//			repSubRoot += "\\";
//		}
//		String fileName = "repository.xml";
//		return !repDest.endsWith(repSubRoot) ? 
//				repDest+ repSubRoot + moniteeHost + "\\" + fileName 
//				: repDest + moniteeHost + "\\" + fileName;
	}

	public long uploadProductionServerRoot(ProductionServerRoot serverRoot, String remotePath) {
		RepositoryUtil repository = new RepositoryUtil(remotePath);
		try {
			repository.saveProductionServerRoot(serverRoot);
		} catch (HAException e) {
			logger.error(String.format("Fail to save %s.", remotePath), e);
			return -1;
		}
		return 0;
	}
	
	public ProductionServerRoot downloadProductionServerRoot(String remotePath, String afguid) {
		RepositoryUtil repository = new RepositoryUtil(remotePath);
		try {
			return repository.getProductionServerRoot(afguid);
		} catch (HAException e) {
			return null;
		}
	}
	
	/**
	 * create a HyperV VM,
	 * @param afGuid
	 * @return VirtualMachineInfo
	 * @throws HyperVException
	 */
	private VirtualMachineInfo createHyperVVM(long handle, String afGuid,
			ARCFlashStorage ardDes, int vmGeneration) throws HyperVException {
		VirtualMachineInfo vmInfo = null;

		String vmLoationPath = "";
		if(!StringUtil.isEmptyOrNull(ardDes.getVMLocationPath())){
			vmLoationPath = ardDes.getVMLocationPath();
		}
		logger.info("create hyperV vm path:"+vmLoationPath);
		String temp_vmGuid = HyperVJNI.CreateVM(handle, 
				ardDes.getVirtualMachineDisplayName(),vmLoationPath, vmGeneration);

		HyperVJNI.SetVMMemorySize(handle, temp_vmGuid, ardDes
				.getMemorySizeInMB());

		HyperVJNI.SetVMLogicalProcessorNum(handle, temp_vmGuid, ardDes
				.getVirtualMachineProcessorNumber());

		String basesnape = "";
		// basesnape = HyperVJNI.TakeVmSnapshot(temp_vmGuid, BASESNAPSHOT,
		// " the parent base snapshot for following D2D sessions");
		vmInfo = new VirtualMachineInfo(0, temp_vmGuid, afGuid, basesnape);

		String file = ardDes.getOldDestFolder();
		VMInfomationModelManager manager = null;
		String standbyVMName = ardDes.getVirtualMachineDisplayName();
		//fanda03 164911 .don't check oldDestFolder when create VM.if a new vm created. we think it has no relationship with old conversion session,
	//	if(StringUtil.isEmptyOrNull(ardDes.getOldDestFolder())){
			String repSubRoot = "";
			if(StringUtil.isEmptyOrNull(ardDes.getRepSubRoot())){
				repSubRoot = standbyVMName + "\\" + "sub0001";
			}else{
				repSubRoot = ardDes.getRepSubRoot();
			}

			String moniteeHost = ardDes.getMoniteeHostName();
			DiskDestination diskDestination = ardDes.getDiskDestinations().get(0);
			String repDest = diskDestination.getStorage().getName();
			if(!repDest.endsWith("\\")){
				repDest += "\\";
			}
			
			if(!repSubRoot.endsWith("\\")){
				repSubRoot += "\\";
			}

			if(!repDest.endsWith(repSubRoot)){
				file = repDest + repSubRoot + moniteeHost + "\\" + CommonUtil.SNAPSHOT_XML_FILE;
			}else{
				file = repDest + moniteeHost + "\\" + CommonUtil.SNAPSHOT_XML_FILE;
			}
			logger.info("xml path: " + file);
			/*
			 * fanda03 fix 164911; if VMSnapshotsModel.xml already exist; means it had a VM which already convert some session but the vm not exist now.
			 * so we delete previous sessions and start new conversion.
			 *  we don't clean all session Link, just clean the newest folder's session. but we need to traverse the link to find if snapshotmange file exist.  
			 */
			cleanOldConversionDiskFolder( file );
			manager = VMInfomationModelManager.getModeManagerInstance(file);
				
			vmInfo.SetVmGeneration(vmGeneration);
			vmInfo.setVmName(ardDes.getVirtualMachineDisplayName());
			manager.putNewVM(vmInfo,new TreeSet<VMSnapshotsInfo>());
		
		FileWriter fw = null;
		try {
			String rootPath = Paths.get(file).getParent().getParent().getParent().toString();
			fw = new FileWriter(rootPath + "\\" + CommonUtil.SNAPSHOT_XML_LOCATION_FILE);
			fw.write(file);
		} catch (Exception e) {
			logger.warn(String.format("Fail to create %s", CommonUtil.SNAPSHOT_XML_LOCATION_FILE), e);
		}finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
		}
			

		try {
			String snapshotXMLDest = manager.getMODEL_XML();
			if(snapshotXMLDest != null && snapshotXMLDest.indexOf(CommonUtil.SNAPSHOT_XML_FILE) > 1) {
				snapshotXMLDest = snapshotXMLDest.substring(0, snapshotXMLDest.length() - CommonUtil.SNAPSHOT_XML_FILE.length());
				
				String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
				ProductionServerRoot serverRoot = null;
				ReplicaRoot root = null;
				try {
					serverRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(afGuid);
					if(serverRoot != null) {
						root = serverRoot.getReplicaRoot();
						if(root == null) {
							root = new TransServerReplicaRoot();
							serverRoot.setReplicaRoot(root);
						}
					}
				}
				catch(Exception e1) {
					serverRoot = new ProductionServerRoot();
					serverRoot.setProductionServerAFUID(afGuid);
					
					root = new TransServerReplicaRoot();
					serverRoot.setReplicaRoot(root);
					
				}
				
				if(root instanceof TransServerReplicaRoot) {
					((TransServerReplicaRoot)root).setFirstReplicDest(snapshotXMLDest);
					((TransServerReplicaRoot)root).setVmuuid(temp_vmGuid);
				}
				
				RepositoryUtil.getInstance(xml).saveProductionServerRoot(serverRoot);
			}

			HeartBeatModelManager.updateVMInfo(afGuid, vmInfo);
		}
		catch(Exception e) {
			logger.info("The creation of repository.xml to store the first replication dest failed", e);
		}
		
		return vmInfo;
	}
	// fanda03 fix 164911
	private  void cleanOldConversionDiskFolder ( String snapshotManageFile ) {
		try {
			if ( isExistSnapShotManage( snapshotManageFile ) ){
				//extract conversion root folder
				Matcher m = Pattern.compile("\\\\sub[\\d]{4}\\\\").matcher(snapshotManageFile);
				String rootFolder = "";
				if ( m.find() ) {
					int subIndex = m.start();
					rootFolder = snapshotManageFile.substring(0, subIndex);
				}
				if( rootFolder.isEmpty() ) {
					throw new Exception( "failed get old destination root folder" );
				}
				File rootFile = new File(rootFolder);
				if(  !rootFile.exists() || !rootFile.isDirectory()) {
					throw new Exception( "failed get old destination root folder" );
				}
				//extract all sub folder
				File[] subXFolders = rootFile.listFiles(new FilenameFilter(){
					@Override
					public boolean accept(File dir, String name) {
						File tmp = new File(dir.getAbsolutePath()+"\\"+name);
						if( tmp.isDirectory() && tmp.getName().matches("sub[\\d]{4}") )
							return true;
						else
							return false;
					}
				});
				//we should delete all subXXX folder, if we remain these folder( such as sub0001/0002 )and new conversion use this folder, it may fail.
				for( File subFolder : subXFolders ){
					recursiveDelFolder ( subFolder.getAbsolutePath() );
					if( subFolder.exists() ){
						logger.warn( "failed delete folder: " + subFolder.getAbsolutePath() +" it may make subsequent conversion fail! ");
					}
				}
			}
		}
		catch( Exception e ){
			logger.error("failed delete old conversion folder", e );
		}
	} 
	private   boolean isExistSnapShotManage( String snapshotManageFile ){
		boolean isExistOldConversion = false;
		File tmp = new File(snapshotManageFile);
		if( tmp.exists() ) {
			logger.info( "find previous snapshotmanage file in last replication destination: "+ tmp.getAbsolutePath() );
			isExistOldConversion = true;
		}
		///test VCM destination link
		else {	
			String snapShotFilePrefix = snapshotManageFile.substring(0, snapshotManageFile.lastIndexOf("\\")); ///the file path should not have tail slash
			tmp = new File( snapShotFilePrefix );
			if( tmp.exists() ) {

				NativeFacade facade = BackupService.getInstance().getNativeFacade();
				try {
					String chainDest = facade.getHAConfigurationFileURL( snapShotFilePrefix,CommonUtil.SNAPSHOT_XML_FILE);
					logger.info( "find previous snapshotmanage file in replication destination chain: "+ chainDest );
					isExistOldConversion = true;
					
				} catch (ServiceException e) {
					logger.info( "failed get snapshot infomation, it's a brand new conversion");
				}
			}
		}
		return isExistOldConversion;
	}
	private  void recursiveDelFolder( String folderName ){
		 
		 File file = new File( folderName );
		 if( file.exists() ){
			File[] childrenFiles = file.listFiles();
			if( childrenFiles ==null ){ //file
				file.delete();
			}
			else if( childrenFiles.length ==0 ){ //empty folder
				file.delete();
			}
			else {
				for( File childFile : childrenFiles  ){
					recursiveDelFolder( childFile.getAbsolutePath() );
				}
				file.delete();
			}
		}		
	 }
	//end fanda03 fix 164911
	public String createHyperVVM(ReplicationJobScript replicationJobScript, int vmGeneration, String vmFinalName, WebServiceClientProxy client) {
		logger.debug("createHyperVVM() - start"); //$NON-NLS-1$
	
		ReplicationDestination replicationDestinaiton = this.getReplicationDestinaiton(replicationJobScript);
		
		String afguid = replicationJobScript.getAFGuid();
		
		ARCFlashStorage arcDes = (ARCFlashStorage)replicationDestinaiton;
		RepositoryUtil repository = RepositoryUtil.getInstance(CommonUtil.getRepositoryConfPath());
		String lastRepDest = null;
//		String repSubRoot = null;
		try {
			lastRepDest = repository.getProductionServerRoot(afguid).getReplicaRoot().getLastRepDest();
//			repSubRoot = repository.getProductionServerRoot(afguid).getReplicaRoot().getRepSubRoot();
		} catch (Exception e1) {
		}
		arcDes.setOldDestFolder(lastRepDest);
		arcDes.setRepSubRoot(null);
		arcDes.setVirtualMachineDisplayName(vmFinalName);
		String arcDesStr;
		try {
			arcDesStr = CommonUtil.marshal(arcDes);
		} catch (JAXBException e) {
			logger.debug(e.getMessage());
			throw  AxisFault.fromAxisFault("Failed to marshal ARCFlashStorage");
		}
		
		String vmguid = client.getServiceV2().createHyperVVM(arcDesStr,afguid, vmGeneration);
		
		logger.debug("createHyperVVM() - end with "+vmguid); //$NON-NLS-1$
		return vmguid;
	}
	
	public VMwareServer getVMwareServerType(String host,String username,String password,String protocol,int port) throws SOAPFaultException{
		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ =  CAVMwareInfrastructureManagerFactory
						.getCAVMwareVirtualInfrastructureManager(host,username,password, protocol, true, port);
			VMwareServerType serverType = vmwareOBJ.getVMwareServerType();
			VMwareServer server = new VMwareServer();
			switch (serverType) {
				case esxServer:
					server.setVmtype(1);
					break;
				case virtualCenter:
					server.setVmtype(2);
					break;
				default:
					server.setVmtype(0);
			} 
			return server;
		} catch (Exception e) {	
			throw  AxisFault.fromAxisFault("Failed to get VMwareServer Type.");
		}
		finally {
			if(vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				}
				catch(Exception e) {
				}
			}
		}
	}
	
	
	public String getESXServerVersion(String host,String username,String password,String protocol,int port) throws SOAPFaultException{
		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ =  CAVMwareInfrastructureManagerFactory
			.getCAVMwareVirtualInfrastructureManager(host,username,password, protocol, true, port);
			String version = vmwareOBJ.GetESXServerVersion();
			return version;
		} catch (Exception e) {
			throw  AxisFault.fromAxisFault("Failed to get esxserver version.");
		}
		finally {
			if(vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				}
				catch(Exception e) {
				}
			}
		}
	}
	
	public VMSnapshotsInfo[] getVMSnapshots(String afGuid,String vmGuid,String vmName) {

		if(logger.isDebugEnabled()) 
			logger.debug("getVMSnapshots begin: " + afGuid );
		VMSnapshotsInfo[]  EMPTY = new VMSnapshotsInfo[0]; 
		VirtualMachineInfo vmInfo = HeartBeatModelManager.getVMInfo(afGuid);
		FailoverJobScript failoverJobScript = HACommon.getFailoverJobScriptObject(afGuid);
		if(vmInfo==null){
			if(failoverJobScript == null)
				return EMPTY;
			vmInfo = new VirtualMachineInfo();
			VirtualizationType virtualType = failoverJobScript.getVirtualType();
			switch (virtualType) {
			case HyperV:	
				vmInfo.setType(0);
				break;
			case VMwareESX:
			case VMwareVirtualCenter:
				vmInfo.setType(1);
				break;
			}
		}
		if(vmInfo!=null && vmInfo.getType() == 0){
			ProductionServerRoot serverRoot = null;
			try {	
				serverRoot = RepositoryManager.getProductionServerRoot(afGuid);
				if(serverRoot.getRetentionCount() <= 0) //if no replication once finished
					serverRoot.setRetentionCount(failoverJobScript.getRetentionCount()); 
				
			} catch (Exception e) {
				logger.debug("Failed to get production server root." + e.getMessage());
			}
			
			//if no replication once was launched
			if(serverRoot == null || serverRoot.getReplicaRoot() == null) {
				logger.debug("No replication once was launched: (serverRoot == null) =" + (serverRoot == null));
				return new VMSnapshotsInfo[0];
			}
			
			TransServerReplicaRoot transRoot = (TransServerReplicaRoot)serverRoot.getReplicaRoot();
			
			return HACommon.getVMSnapshotsHyperV(transRoot.getRootPath(),afGuid, vmInfo);
			
		}else if(vmInfo!=null && vmInfo.getType() == 1){			
			CAVirtualInfrastructureManager vmwareOBJ = getVMWareObj(afGuid);
			if (vmwareOBJ == null) {
				return EMPTY;
			}
			
			String xml = CommonUtil.D2DInstallPath+"Configuration\\repository.xml";
			ProductionServerRoot prodRoot = null;
			
			String localVmuuid = vmGuid;
			String localVmname = vmName;
			try {
				if(StringUtil.isEmptyOrNull(localVmuuid) || StringUtil.isEmptyOrNull(localVmname)){
					try {	
						prodRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(afGuid);
					} catch (Exception e) {
						logger.debug("Failed to get production server root.");
					}
					//if no replication once was launched
					if(prodRoot == null || prodRoot.getReplicaRoot() == null) {
						logger.debug("No replication once was launched: (prodRoot == null) =" + (prodRoot == null));
						return new VMSnapshotsInfo[0];
					}
					ReplicaRoot hostRoot = prodRoot.getReplicaRoot();
					if(hostRoot.getVmuuid() == null || hostRoot.getVmname() == null){
						logger.warn("VM UUID and name is not found. Failover stop!!!!!");
						return EMPTY;
					}
					
					localVmuuid = hostRoot.getVmuuid();
					localVmname = hostRoot.getVmname();
					
				}
				return getVMWareSnapshots(vmwareOBJ, afGuid, localVmname, localVmuuid);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return EMPTY;
			}
			finally {
				if(vmwareOBJ != null) {
					try {
						vmwareOBJ.close();
					}
					catch(Exception e) {
					}
				}
			}
			
		}
		return EMPTY;
		
	
	}
	
	public VMSnapshotsInfo[] getSnapshotsForProductionServer(String afGuid) throws ServiceException{
		logger.debug("getSnapshotsForProductionServer() - start"); //$NON-NLS-1$

		String localAFGuid = evaluteAFGuid(afGuid);
		
		//TODO yaoyu01
		//before call monitor service, should login first and reuse instance of WebServiceClient
		HeartBeatJobScript script = this.getHeartBeatJobScript(localAFGuid);
		if (script == null)
			return null;
		
		ProductionServerRoot prodRoot = null;
		String vmGuid = "";
		String vmName = "";
		try{	
			String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
			prodRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(localAFGuid);
			ReplicaRoot repRoot = prodRoot.getReplicaRoot();
			
			if(repRoot instanceof VMWareESXHostReplicaRoot){
				vmGuid = ((VMWareESXHostReplicaRoot)repRoot).getVmuuid();
			}else if (repRoot instanceof VMWareVirtualCenterReplicaRoot){
				vmGuid = ((VMWareVirtualCenterReplicaRoot)repRoot).getVmuuid();
			}	
			
			ReplicationDestination dest =HAService.getInstance().getReplicationJobScript(localAFGuid).getReplicationDestination().get(0);
			
			if(dest instanceof VMwareESXStorage){
				vmName = ((VMwareESXStorage)dest).getVirtualMachineDisplayName();
			}else if(dest instanceof VMwareVirtualCenterStorage){
				vmName = ((VMwareVirtualCenterStorage)dest).getVirtualMachineDisplayName();
			}
			
		}catch(Exception e){
		}
		
		try {
			logger.debug("protocol:"+script.getHeartBeatMonitorProtocol());
			logger.debug("host:"+script.getHeartBeatMonitorHostName());
			WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(script);
			VMSnapshotsInfo[] returnVMSnapshotsInfoArray = client.getServiceV2().getVMSnapshots(localAFGuid, vmGuid, vmName);
			for (VMSnapshotsInfo info :returnVMSnapshotsInfoArray) {
				if(info.getTimestamp() > 0) {
					info.setTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(new Date(info.getTimestamp())));
				}
			}
			logger.debug("getSnapshotsForProductionServer() - end"); //$NON-NLS-1$
			return returnVMSnapshotsInfoArray;
		} catch (SOAPFaultException e) {
			logger.warn("getSnapshotsForProductionServer() - exception ignored", e); //$NON-NLS-1$
		}
		

		logger.debug("getSnapshotsForProductionServer() - end"); //$NON-NLS-1$
		return null;
	}
	
	public boolean isNeedRemoveReplicatedInfo(ReplicationJobScript newReplicationJobScript, HeartBeatJobScript newHeartBeatJobScript, String newVMName){
		boolean isNeedRemove = false;
		String afGuid = newReplicationJobScript.getAFGuid();
		String localAFGuid = evaluteAFGuid(afGuid);
		ReplicationJobScript oldReplicationJobScript = getReplicationJobScript(localAFGuid);
		HeartBeatJobScript   oldHeartBeatJobScript = getHeartBeatJobScript(localAFGuid);
		if((oldReplicationJobScript == null) || (oldHeartBeatJobScript == null)){
			return isNeedRemove;
		}
		
		//If user change the VCM setting vm name
		VirtualizationType oldVirtualizationType = oldReplicationJobScript.getVirtualType();
		VirtualizationType newVirtualizationType = newReplicationJobScript.getVirtualType();
		if(oldVirtualizationType == newVirtualizationType){
			
			if(oldVirtualizationType == VirtualizationType.HyperV){
				ARCFlashStorage oldStorage = (ARCFlashStorage)oldReplicationJobScript.getReplicationDestination().get(0);
				// ARCFlashStorage newStorage = (ARCFlashStorage)newReplicationJobScript.getReplicationDestination().get(0);
				//change the VM name
				if(oldStorage.getVirtualMachineDisplayName().compareToIgnoreCase(newVMName)!=0){
					return true;
				}
			}
			
			if(oldHeartBeatJobScript.getHeartBeatMonitorHostName().compareToIgnoreCase(newHeartBeatJobScript.getHeartBeatMonitorHostName())==0){
				if(oldVirtualizationType == VirtualizationType.VMwareESX){
					VMwareESXStorage oldStorage = (VMwareESXStorage)oldReplicationJobScript.getReplicationDestination().get(0);
					// VMwareESXStorage newStorage = (VMwareESXStorage)newReplicationJobScript.getReplicationDestination().get(0);
					//change the VM name
					if(oldStorage.getVirtualMachineDisplayName().compareToIgnoreCase(newVMName)!=0){
						return true;
					}
				}
				else if(oldVirtualizationType == VirtualizationType.VMwareVirtualCenter){
					VMwareVirtualCenterStorage oldStorage = (VMwareVirtualCenterStorage)oldReplicationJobScript.getReplicationDestination().get(0);
					VMwareVirtualCenterStorage newStorage = (VMwareVirtualCenterStorage)newReplicationJobScript.getReplicationDestination().get(0);
					//change the VM name
					
					if((oldStorage.getEsxName().compareTo(newStorage.getEsxName())==0) &&
					 (oldStorage.getVirtualMachineDisplayName().compareToIgnoreCase(newVMName)!=0)){
						return true;
					}
				}
			}
			
		}
		/*
		 * fanda03 fix 164911 , delete old repository if change virtualization type!. if we don't delete and you change an ESX policy to a hyperV policy.
		 * HyperV conversion will has repository root type as VMWareESXHostReplicaRoot. it cause some issue.!
		 */
		if(oldVirtualizationType != newVirtualizationType && (oldVirtualizationType== VirtualizationType.HyperV||newVirtualizationType == VirtualizationType.HyperV) ) {
			 isNeedRemove = true;
		}
		return isNeedRemove;
	}
	
	public int removeReplicatedInfo(String afguid, VirtualizationType virtualizationType, String converterID){
		
		synchronized(jobQueue){
			FailoverJob job = (FailoverJob) getFailoverJob(afguid);
			if(job != null) {
				FailoverJobScript jobScript = job.getJobScript();
				if (converterID != null && jobScript.getConverterID() != null && !converterID.equalsIgnoreCase(jobScript.getConverterID())) {
					logger.warn("The input converterID and the converterID in hbjobscript are different, so skip removeReplicatedInfo for " + afguid);
					return 0;
				}
			}
		}
		
		try {
			//Remove the repository.xml info
			String xml = CommonUtil.D2DHAInstallPath+"Configuration\\repository.xml";
			ProductionServerRoot pServerRoot= RepositoryUtil.getInstance(xml).getProductionServerRoot(afguid);
			RepositoryUtil.getInstance(xml).removeProductionServerRoot(pServerRoot);
			
//			if(virtualizationType == VirtualizationType.HyperV){
//				//Remove the snapshot from VMSnapshotModel.xml
//				VMInfomationModelManager.clearSnapshots(afguid);
//
//			}
			
			return 0;
		} catch (Exception e) {
			logger.warn("Failed to removeReplicatedInfo, afguid:" + afguid + "; " + e.getMessage());
			return 1;
		}
	
	}

	public boolean isSwitchBetweenESXAndVCenterForSameESX(ReplicationJobScript newReplicationJobScript){
		boolean isSwitch = false;
		ReplicationJobScript oldrReplicationJobScript = getReplicationJobScript(newReplicationJobScript.getAFGuid());
		if((oldrReplicationJobScript == null)||(newReplicationJobScript == null)){
			return isSwitch;
		}
		
		ReplicationDestination newReplicationDestination = newReplicationJobScript.getReplicationDestination().get(0);
		ReplicationDestination oldReplicationDestination = oldrReplicationJobScript.getReplicationDestination().get(0);
		
		if((newReplicationDestination == null)||(oldReplicationDestination == null)){
			return isSwitch;
		}

		//If user switches the same ESX between ESX Server and VC 
		VirtualizationType oldVirtualizationType = oldrReplicationJobScript.getVirtualType();
		VirtualizationType newVirtualizationType = newReplicationJobScript.getVirtualType();
		if((oldVirtualizationType==VirtualizationType.VMwareESX)&&
				(newVirtualizationType== VirtualizationType.VMwareVirtualCenter)){
			VMwareESXStorage oldESXStorage = (VMwareESXStorage)oldReplicationDestination;
			VMwareVirtualCenterStorage newVirtualCenterStorage = (VMwareVirtualCenterStorage)newReplicationDestination;
			if(oldESXStorage.getESXHostName().equals(newVirtualCenterStorage.getEsxName())){
				isSwitch = true;
			}
		}
		else if((oldVirtualizationType==VirtualizationType.VMwareVirtualCenter)&&
				(newVirtualizationType== VirtualizationType.VMwareESX)){
			VMwareVirtualCenterStorage oldVirtualCenterStorage = (VMwareVirtualCenterStorage)oldReplicationDestination;
			VMwareESXStorage newESXStorage = (VMwareESXStorage)newReplicationDestination;
			if(oldVirtualCenterStorage.getEsxName().equals(newESXStorage.getESXHostName())){
				isSwitch = true;
			}
		}
		
		return isSwitch;
		
	}
	public int updateInfoForSwitchESX(String afGuid){
		
		String localAFGuid = evaluteAFGuid(afGuid);
		
		int result =0;
		String vmname = "";
		String vmuuid = "";
		
		
		FailoverJobScript failoverJobScript = getFailoverJobScript(localAFGuid);
		ReplicationJobScript replicationJobScript = getReplicationJobScript(localAFGuid);
		if((failoverJobScript==null)||(replicationJobScript == null)){
			return result;
		}
		
		VirtualizationType newVirtualizationType = failoverJobScript.getVirtualType();
		String afguid = failoverJobScript.getAFGuid();
		//convert the  repository.xml
		try {

			String xml = CommonUtil.D2DHAInstallPath+"Configuration\\repository.xml";
			RepositoryUtil repositoryUtil = RepositoryUtil.getInstance(xml);
			ProductionServerRoot pServerRoot= repositoryUtil.getProductionServerRoot(afguid);
			
			if(newVirtualizationType == VirtualizationType.VMwareVirtualCenter){
				VMWareESXHostReplicaRoot ESXRoot = (VMWareESXHostReplicaRoot)pServerRoot.getReplicaRoot();
				VMWareVirtualCenterReplicaRoot vCenterRoot = new VMWareVirtualCenterReplicaRoot();
				vCenterRoot.setVmuuid(ESXRoot.getVmuuid());
				vCenterRoot.setVmname(ESXRoot.getVmname());
				vCenterRoot.setReplicaTime(ESXRoot.getReplicaTime());
				List<DiskDestination> diskDestinations = replicationJobScript.getReplicationDestination().get(0).getDiskDestinations();
				vCenterRoot.getDiskDestinations().clear();
				for (DiskDestination diskDestination : diskDestinations) {
					vCenterRoot.getDiskDestinations().add(diskDestination);
				}
				vCenterRoot.setLatestReplicatedSession(ESXRoot.getLatestReplicatedSession());
				pServerRoot.setReplicaRoot(vCenterRoot);
				if(ESXRoot.getReplicaTime() > 0)
					pServerRoot.clearMostRecentReplic();
				
				vmname = ESXRoot.getVmname();
				vmuuid = ESXRoot.getVmuuid();
				repositoryUtil.saveProductionServerRoot(pServerRoot);
			}
			else if(newVirtualizationType == VirtualizationType.VMwareESX){
				VMWareVirtualCenterReplicaRoot vCenterRoot = (VMWareVirtualCenterReplicaRoot)pServerRoot.getReplicaRoot();
				VMWareESXHostReplicaRoot ESXRoot = new VMWareESXHostReplicaRoot();
				ESXRoot.setVmuuid(vCenterRoot.getVmuuid());
				ESXRoot.setVmname(vCenterRoot.getVmname());
				ESXRoot.setReplicaTime(vCenterRoot.getReplicaTime());
				List<DiskDestination> diskDestinations = replicationJobScript.getReplicationDestination().get(0).getDiskDestinations();
				vCenterRoot.getDiskDestinations().clear();
				for (DiskDestination diskDestination : diskDestinations) {
					ESXRoot.getDiskDestinations().add(diskDestination);
				}
				ESXRoot.setLatestReplicatedSession(vCenterRoot.getLatestReplicatedSession());
				
				pServerRoot.setReplicaRoot(ESXRoot);
				if(vCenterRoot.getReplicaTime() > 0)
					pServerRoot.clearMostRecentReplic();
				vmname = vCenterRoot.getVmname();
				vmuuid = vCenterRoot.getVmuuid();
				repositoryUtil.saveProductionServerRoot(pServerRoot);
			}
			
		} catch (Exception e) {
			logger.warn("Failed to update repository.xml info:"+e.getMessage());
			
		}
		
		//update the snapshot URL
		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			String host = "";
			String username = "";
			String password = "";
			String protocol = "";
			int port = 0;

			Virtualization virtualization = failoverJobScript.getFailoverMechanism().get(0);
			if(virtualization instanceof VMwareESX){
				VMwareESX vMwareESX=(VMwareESX)virtualization;
				host = vMwareESX.getHostName();
				username = vMwareESX.getUserName();
				password = vMwareESX.getPassword();
				protocol = vMwareESX.getProtocol();
				port = vMwareESX.getPort();
				
			}
			else if(virtualization instanceof VMwareVirtualCenter){
				VMwareVirtualCenter virtualCenter= (VMwareVirtualCenter)virtualization;
				host = virtualCenter.getHostName();
				username = virtualCenter.getUserName();
				password = virtualCenter.getPassword();
				protocol = virtualCenter.getProtocol();
				port = virtualCenter.getPort();
			}
			
			
			vmwareOBJ =  CAVMwareInfrastructureManagerFactory.
					getCAVMwareVirtualInfrastructureManager(host,username,password, protocol, true, port);
			Map<String, String> realSnapshots = vmwareOBJ.listVMSnapShots(vmname, vmuuid);
			VMwareSnapshotModelManager snapshotModelManager = VMwareSnapshotModelManager.getManagerInstance(vmwareOBJ, vmname, vmuuid);
			if(!snapshotModelManager.isReady()){
				logger.info("The vmsnapshotmodel.xml is null");
				return 1;
			}
			
			VirtualMachineInfo vmInfo = snapshotModelManager.getInternalVMInfo(afguid);
			SortedSet<VMSnapshotsInfo> vcmSnapshots = snapshotModelManager.getSnapshots(afguid);
			
			for (Entry<String, String> entry : realSnapshots.entrySet()) {
				String sessionGUID = vmwareOBJ.getSnpashotDescription(vmname, vmuuid, entry.getKey());
				for (VMSnapshotsInfo vmSnapshotsInfo : vcmSnapshots) {
					if(vmSnapshotsInfo.getSessionGuid().equals(sessionGUID)){
						vmSnapshotsInfo.setSnapGuid(entry.getKey());
					}
					else if(!vmSnapshotsInfo.isDRSnapshot()){
						String bootableSessonGUID = vmSnapshotsInfo.getSessionGuid()+"_bootable";
						if(sessionGUID.equals(bootableSessonGUID)){
							vmSnapshotsInfo.setBootableSnapGuid(entry.getKey());
						}
					}
				}
				
			}
			
			if(vmInfo != null){
				if(!snapshotModelManager.replaceSnapshots(vmInfo, vcmSnapshots)){
					logger.error("Failed to update the snapshot info");
					result = 1;
				}
			}
			
		} catch (Exception e) {
			logger.error("updateh info get exception:"+e.getMessage());
			result = 1;
		}
		finally {
			if(vmwareOBJ!=null){
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.error("Failed to close connection:"+e.getMessage());
				}
				
			}
		}
		
		return result;
	}
	public boolean setJobScriptCombo(JobScriptCombo jobScript, VCMPolicyDeployParameters deployPolicyParameters, List<VCMSavePolicyWarning> warningList) throws ServiceException {
		logger.info("Begin setJobScriptCombo");

		ReplicationJobScript repScript = jobScript.getRepJobScript();
		boolean isMSPManualConversion = ManualConversionUtility.isVSBWithoutHASupport(repScript);
		boolean isBackupToRPS = repScript.getBackupToRPS();
		boolean isVSphereBackup = repScript.isVSphereBackup();
		
		String afGuid = jobScript.getFailoverJobScript().getAFGuid();
		String currentAFGuid = retrieveCurrentNodeID();
		if(StringUtil.isEmptyOrNull(afGuid)){
			afGuid = currentAFGuid;
			jobScript.getFailoverJobScript().setAFGuid(afGuid);
			jobScript.getHbJobScript().setAFGuid(afGuid);
			jobScript.getRepJobScript().setAFGuid(afGuid);
		}
		else if(!afGuid.equals(currentAFGuid)) {
			if (!isBackupToRPS || isVSphereBackup)
				jobScript.getFailoverJobScript().setVSphereBackup(true);
		}

		boolean destChange = false; 
		HeartBeatJobScript oldHeart = null;
		boolean ifNeedStopOldHeartBeat = false;
		synchronized (jobQueue) {
			keepAutoReplicSatus(jobScript);

			oldHeart = getHeartBeatJobScript(afGuid);
			
			if(oldHeart!=null){
				FailoverJobScript oldFail = null;
				Collection<AFJob> jobs = jobQueue.findByJobType(JobType.Failover);
				for (AFJob job : jobs) {
					FailoverJob failoverJob = (FailoverJob) job;
					if (failoverJob.getJobScript().getAFGuid().equals(afGuid)) {
						oldFail = failoverJob.getJobScript();
						break;
					}
				}
				if(oldFail != null)
					ifNeedStopOldHeartBeat = true;
			}
		}
			
		destChange = isVCMDestinationChange(repScript);
		logger.info("VCM destination change: " + destChange);
		
		if (ifNeedStopOldHeartBeat) {
			if (!isMSPManualConversion) {
				try{
					if (isBackupToRPS){
						logger.info("BackupToRPS: Stop HeartBeat on the monitee.");
						WebServiceClientProxy proxy = getD2DService(deployPolicyParameters);
						proxy.getServiceV2().stopHeartBeatThis(afGuid, null);
					}
					else
						stopHeartBeat(afGuid, null);
				}catch(Exception af){
					logger.warn(af.getMessage(), af);
				}
			}
			else {
				logger.info("Directly delete the HeartBeat, on the monitor.");
				try {
					deregisterHeartBeatScriptMSP(afGuid, null);
				}catch (Exception af) {
					logger.warn(af.getMessage(), af);
				}
			}
		}	
		
		FailoverJobScript failoverObj = jobScript.getFailoverJobScript();
		failoverObj.setProductionServerBackupDestName(repScript.getAgentVMName());
		String agentNodename = deployPolicyParameters.getHostname();
		if (isVSphereBackup) {
			if (StringUtil.isEmptyOrNull(deployPolicyParameters.getHostname()))
				agentNodename = deployPolicyParameters.getSourceMachineInfo().getVmName();
			failoverObj.setVMJobScript(true);
		}
		failoverObj.setProductionServerName(agentNodename);
		if (isBackupToRPS){
			if (isMSPManualConversion){
				String URL = CommonUtil.getProductionServerURL();
				failoverObj.setProductionServerPort(CommonUtil.getProductionServerPort(URL));
				failoverObj.setProductionServerProtocol(CommonUtil.getProductionServerProtocol(URL));
			}
			else{
				failoverObj.setProductionServerPort(String.valueOf(deployPolicyParameters.getSourceMachineInfo().getHypervisorPort()));
				failoverObj.setProductionServerProtocol(deployPolicyParameters.getSourceMachineInfo().getHypervisorProtocol());
			}
		}
		else{
			String URL = CommonUtil.getProductionServerURL();
			failoverObj.setProductionServerPort(CommonUtil.getProductionServerPort(URL));
			failoverObj.setProductionServerProtocol(CommonUtil.getProductionServerProtocol(URL));
		}

		failoverObj.setRetentionCount(repScript.getStandbySnapshots());
		failoverObj.setJobType(JobType.Failover);
		if (jobScript.getHbJobScript() != null && !isMSPManualConversion) {
			failoverObj.setHeartBeatFrequencyInSeconds(jobScript.getHbJobScript().getHeartBeatFrequencyInSeconds());
			failoverObj.setState(FailoverJobScript.REGISTERED); //set register state once script is deployed to target machine
		}
		
		boolean standaloneD2D = false; // including Standalone D2D server backup to sharefolder and RPS
		standaloneD2D = HACommon.isTargetPhysicalMachine(afGuid) || (isBackupToRPS && !isMSPManualConversion && !isVSphereBackup);
		if(!standaloneD2D){
			//For vsphered managed VM, auto failover is not supported
			//This is because, when proxy machine is down, all standby VM will boot up if auto failover is enabled.
			
			if(failoverObj.isAutoFailover()){
				
				String msg = WebServiceMessages.getResource(
						COLDSTANDBY_CHANGE_FAILOVER_MODE,
						failoverObj.getProductionServerName(),
						failoverObj.getProductionServerName());
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, 
						Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "",
								"", "", "" }, afGuid);
				if (warningList != null) {
					VCMSavePolicyWarning savePolicyWarning = new VCMSavePolicyWarning(
							FlashServiceErrorCode.VCM_APPLY_POLICY_COLDSTANDBY_CHANGE_FAILOVER_MODE,
							new String[] { msg });
					warningList.add(savePolicyWarning);
				}
			}
			
			failoverObj.setAutoFailover(false);
					
			VSphereProxyServer proxyServer = new VSphereProxyServer();
			if (isBackupToRPS) {
				if (isVSphereBackup) {
					MachineDetail srcMachineInfo = deployPolicyParameters.getSourceMachineInfo();
					try {
						proxyServer.setVSphereProxyName(srcMachineInfo.getHypervisorHostName());
					} catch (Exception e) {
						throw AxisFault.fromAxisFault(e.getMessage(), e);
					}
					proxyServer.setVSphereProxyPort(srcMachineInfo.getHypervisorPort()+"");
					proxyServer.setVSphereProxyProtocol(srcMachineInfo.getHypervisorProtocol());
					
					WebServiceClientProxy proxy = null;
					proxy = WebServiceFactory.getFlassService(deployPolicyParameters.getSourceMachineInfo().getHypervisorProtocol(),
							deployPolicyParameters.getSourceMachineInfo().getHypervisorHostName(),
							deployPolicyParameters.getSourceMachineInfo().getHypervisorPort(),
							ServiceInfoConstants.SERVICE_ID_D2D_R16_5,0,0);
			        String username = HACommon.getUserFromUsername(deployPolicyParameters.getSourceMachineInfo().getHypervisorUserName());
			        String domain   = HACommon.getDomainFromUsername(deployPolicyParameters.getSourceMachineInfo().getHypervisorUserName());
			        String password = deployPolicyParameters.getSourceMachineInfo().getHypervisorPassword();
			        
			        String proxyuuid = proxy.getFlashServiceR16_5().EstablishTrust(username, password, domain);
			        proxyuuid = getNativeFacade().decrypt(proxyuuid);
					proxyServer.setVSphereUUID(proxyuuid);
					failoverObj.setVSphereproxyServer(proxyServer);
					failoverObj.setVSphereBackup(true);
				}
			}
			else {
				try {
					proxyServer.setVSphereProxyName(InetAddress
							.getLocalHost().getHostName());
				} catch (UnknownHostException e) {
					throw AxisFault.fromAxisFault(e.getMessage(), e);
				}
				String URL = CommonUtil.getProductionServerURL();
				proxyServer.setVSphereProxyPort(CommonUtil.getProductionServerPort(URL));
				proxyServer.setVSphereProxyProtocol(CommonUtil.getProductionServerProtocol(URL));
				proxyServer.setVSphereUUID(HAService.getInstance().retrieveCurrentAuthUUID(true));
				failoverObj.setVSphereBackup(true);
			}
			failoverObj.setVSphereproxyServer(proxyServer);
		}
		HAService.getInstance().setFailoverJobScript(failoverObj);
		
		HeartBeatJobScript script = jobScript.getHbJobScript();
		if (script != null) {
			script.setJobType(JobType.HeartBeat);
			HAService.getInstance().setHeartBeatJobScript(script, !isBackupToRPS);
		}
		
		ReplicationDestination repDest = repScript.getReplicationDestination().get(0);
		if(repDest instanceof ARCFlashStorage){
			ARCFlashStorage dest = ((ARCFlashStorage)repDest);
			dest.setMoniteeHostName(HACommon.getProductionServerNameByAFRepJobScript(repScript));
			List<DiskDestination> cfgDiskDestinations = dest.getCFGDiskDestinations();
			for (DiskDestination diskDestination : dest.getDiskDestinations()) {
				cfgDiskDestinations.add(diskDestination.deepCopy());
			}
		}
		repScript.setJobType(JobType.Replication);
		HAService.getInstance().setReplicationJobScript(repScript);
		
		AlertJobScript alertJobScript = jobScript.getAlertJobScript();
		if(alertJobScript!=null) {
			HAService.getInstance().setAlertJobScript(alertJobScript);
		}

		
		// chefr03, if the failover setting correctly saved, 
		// And if it is the first time of configuration, save a registry key
		boolean firstVCMSettingFlag = false;  
		try {
			if ((oldHeart == null && !isMSPManualConversion)
				|| destChange ) {		// Only set this flag for the first time
				firstVCMSettingFlag = true;
			}				
		} catch (Exception e){
			logger.error(e.getMessage(),e);
		}
		
		if (!isBackupToRPS && !isMSPManualConversion) {
			//Don't put this into jobQueue monitor area
			if(jobScript.getRepJobScript()!=null && jobScript.getRepJobScript().getAutoReplicate())
			{
				this.backupJob.addObserver(this);
			}else{
				this.backupJob.deleteObserver(this);
			}
		}
		//end
		logger.info("Finish setJobScriptCombo()");
		
		return firstVCMSettingFlag;
	}

	private void keepAutoReplicSatus(JobScriptCombo jobScript) {
		try {
			ReplicationJobScript replicJob = getReplicationJobScript(jobScript.getFailoverJobScript().getAFGuid());
			ReplicationJobScript newScript = jobScript.getRepJobScript();
			if(replicJob != null && !replicJob.getAutoReplicate()) {
				newScript.setAutoReplicate(replicJob.getAutoReplicate());
			}
		}
		catch(Exception e) {
			logger.warn("Fails to keep AutoReplicate", e);
		}
	}

//	private void setHostName2Script(String afGuid, String currentAFGuid,
//			FailoverJobScript failoverObj) throws ServiceException {
//		if(currentAFGuid.equals(afGuid)) {
//			try {
//				failoverObj.setProductionServerName(InetAddress.getLocalHost().getHostName());
//			} catch (UnknownHostException e) {
//				throw  AxisFault.fromAxisFault(e.getMessage(),e);
//			}
//		}
//		else {
//			VirtualMachine vm = new VirtualMachine();
//			vm.setVmInstanceUUID(afGuid);
//			VMBackupConfiguration vmBackup = VSphereService.getInstance().getVMBackupConfiguration(vm);
//			VM_Info vmInfo = VSphereLicenseCheck.getVMDetails(vmBackup);
//			
//			String vmHostName = null;
//			if(vmInfo != null && !StringUtil.isEmptyOrNull(vmInfo.getVMHostName())) {
//				vmHostName = vmInfo.getVMHostName();
//			}
//			else if(vmBackup != null && vmBackup.getBackupVM() != null){
//				BackupVM backupVM = vmBackup.getBackupVM();
//				// TODO vm name may not be unique when using to replace vm host name.
//				vmHostName = StringUtil.isEmptyOrNull(backupVM.getVmHostName()) ? backupVM.getVmName() : backupVM.getVmHostName();
//			}
//			
//			failoverObj.setProductionServerName(vmHostName);
//			failoverObj.setVMJobScript(true);
//		}
//	}
	
	public NativeFacade getNativeFacade(){
		return BackupService.getInstance().getNativeFacade();
	}

	public int isFailoverJobScriptInState(String afguid, int paused) {
		int result = 0;
		synchronized (jobQueue) {
				Collection<AFJob> jobs = jobQueue.findByJobType(JobType.Failover);
				for (AFJob job : jobs) {
					FailoverJob failoverJob = (FailoverJob) job;
					if (failoverJob.getJobScript().getAFGuid().equals(afguid)) {
						result = 1;
						if(failoverJob.getJobScript().getState() == paused)
							result +=3;
						return result;
					}
				}
		}
		return result;
	}

	
	public ArrayList<VirtualNetworkInfo> getVirtualNetworkList(String host,String username,String password,
										String protocol,boolean ignoreCertAuthentidation,long viPort,ESXNode node) throws SOAPFaultException{
		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
								.getCAVMwareVirtualInfrastructureManager(host,username,password,protocol, 
																		ignoreCertAuthentidation, viPort);
			ArrayList<VirtualNetworkInfo> networkList = vmwareOBJ.getVirtualNetworkList(node);
			if(networkList == null || networkList.size()== 0){
				return new ArrayList<VirtualNetworkInfo>(0);
			}
			
			return networkList;
			
		} catch (Exception e) {
			logger.error("Failed to get network list");
			throw  AxisFault.fromAxisFault("Failed to get network list.");
		}
		finally {
			if(vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				}
				catch(Exception e) {
				}
			}
		}
	}
	
	public String[] getAdapterTypes(String host,String username,String password,
									String protocol,boolean ignoreCertAuthentidation,long viPort,ESXNode node) throws SOAPFaultException{
		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
					.getCAVMwareVirtualInfrastructureManager(host,username,password,protocol, 
					ignoreCertAuthentidation, viPort);
			List<String> os_NicTypesMap = vmwareOBJ.getOsSupportedVirtualNetworkAdapterMap(node);
			List<String> result = new ArrayList<String>();
			if(os_NicTypesMap!=null){
				for(String os_nic : os_NicTypesMap){
					String[] tempNic = os_nic.substring(os_nic.indexOf("+")+1).split(",");
					for(String nic : tempNic){
						//nic like: VirtualE1000e , should be E1000E
						String nicType = nic.substring(nic.indexOf("Virtual")+7).toUpperCase().trim();
						if(!result.contains(nicType))
							result.add(nicType);
					}
				}
			}
			if(result.contains("PCNET32")||result.contains("VMXNET")){
				result.remove("PCNET32");
				result.remove("VMXNET");
				result.add("Flexible");
			}
			if (result.contains("SRIOVETHERNETCARD")) {
				result.remove("SRIOVETHERNETCARD");
			}
			return result.toArray(new String[result.size()]);
			
		} catch (Exception e) {
			logger.error("Failed to get network adapter types");
			throw  AxisFault.fromAxisFault("Failed to get network adapter types.");
		}
		finally {
			if(vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				}
				catch(Exception e) {
				}
			}
		}
	}
	
	public String[] getHypervAdapterTypes(){
		String[] types = new String[]{HYPERV_NETWORK_ADAPTER,HYPERV_LEGACY_NETWORK_ADAPTER};
		return types;
	}

	public Host_Info getHostInfo(String host,String username,String password,
								 String protocol,boolean ignoreCertAuthentidation,long viPort,ESXNode node) throws SOAPFaultException{

		CAVirtualInfrastructureManager vmwareOBJ = null;
		
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
			.getCAVMwareVirtualInfrastructureManager(host,username,password,protocol, 
													ignoreCertAuthentidation, viPort);	
		} catch (Exception e) {
			logger.error("Failed to connect to vmware.");
			throw  AxisFault.fromAxisFault("Failed to connect to vmware.");
		}
			
		Host_Info info = new Host_Info();
		try {
			info = vmwareOBJ.getHostInfo(node);
		} catch (Exception e) {
			logger.error("Failed to get cpu count and memory size");
			throw  AxisFault.fromAxisFault("Failed to get cpu count and memory size.");
		}
		finally {
			if(vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				}
				catch(Exception e) {
				}
			}
		}
			
		return info;
			
	}

	public String getHostNameForVmguid(String vmguid) {
		if(logger.isDebugEnabled()){
			logger.debug("getHostNameForVmguid begin:" + vmguid);
		}
		String result = null;
		
		HeartBeatModel model = HeartBeatModelManager.getHeartBeatModel();
		if(model == null) {
			
			if(logger.isDebugEnabled()){
				logger.debug("getHostNameForVmguid end with hostname:" + result);
			}
			return result;
		}
		List<ARCFlashNode> monitoredARCFlashNodes = model.getMonitoredARCFlashNodes();
		if(monitoredARCFlashNodes == null) {
			
			if(logger.isDebugEnabled()){
				logger.debug("getHostNameForVmguid end with hostname:" + result);
			}
			return result;
		}
		ListIterator<ARCFlashNode> listIterator = monitoredARCFlashNodes.listIterator();
		while (listIterator.hasNext()) {
			ARCFlashNode arcFlashNode = (ARCFlashNode) listIterator.next();
			VirtualMachineInfo vmInfo = arcFlashNode.getVmInfo();
			if (vmInfo == null
					|| vmInfo.getType() != VirtualMachineInfo.VIRTUAL_TYPE_HYPERV
					|| vmInfo.getVmGUID() == null
					|| !vmInfo.getVmGUID().equals(vmguid)) {
				continue;
			} else {
				result = arcFlashNode.getHostname();
				break;
			}
		}

		if(logger.isDebugEnabled()){
			logger.debug("getHostNameForVmguid end with hostname:" + result);
		}
		return result;
	}

    public void notifyBackupEnds(long jobStatus){
    	
    	this.backupJob.notifyObserversWithChanged(jobStatus);
    }
	@Override
	public void update(Observable o, Object jJMInput) {
		/**
		 * Here we are called by BaseBackupJob or VSphereBackupJob when backup job finishes successfully.
		 * This method is in monitor scope of o
		 *  
		 */
		
		String afGuid = null;
		// boolean isHBBUVM = false;
		if (jJMInput instanceof JJobMonitor) {
			JJobMonitor jJM = (JJobMonitor)jJMInput;
			if (jJM.getUlJobType() !=  Constants.AF_JOBTYPE_BACKUP)
				return;
			if(jJM.getUlJobStatus() != BaseBackupJob.BackupJob_PROC_EXIT)
				return;

			afGuid = retrieveCurrentNodeID();
			//isHBBUVM = false;
		} else if (jJMInput instanceof JVMJobMonitorDetail) {
			JVMJobMonitorDetail jobMonitorDetail = (JVMJobMonitorDetail)jJMInput;
			if (jobMonitorDetail.getjJobMonitor().getUlJobType() !=  Constants.AF_JOBTYPE_VM_BACKUP && 
				jobMonitorDetail.getjJobMonitor().getUlJobType() !=  Constants.AF_JOBTYPE_HYPERV_VM_BACKUP &&
				jobMonitorDetail.getjJobMonitor().getUlJobType() !=  Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP && 
				jobMonitorDetail.getjJobMonitor().getUlJobType() !=  Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP)
				return;
			if(jobMonitorDetail.getjJobMonitor().getUlJobStatus() != BaseBackupJob.BackupJob_PROC_EXIT)
				return;

			afGuid = jobMonitorDetail.getJobContext().getExecuterInstanceUUID();
			// isHBBUVM = true;
		} else
			return;
				
		if(getReplicationJobScript(afGuid) == null){
			logger.info("There's no conversion AFJob script for Node " + afGuid + " ." );
			return;
		}
		
		if (HACommon.isBackupToRPS(afGuid)) {
			logger.info("Node " + afGuid + " is managed by RPS, skip the virtual standby job." );
			return;
		}

		logger.info("Backup job for node " + afGuid + " is finished, start the conversion job." );
		
		String msg = WebServiceMessages.getResource(COLDSTANDBY_SETTING_REPLICATION_AUTO);
		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, 
				Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "",
						"", "", "" }, afGuid);
				
		startReplication(afGuid);
	}

	public int enableAutoOfflieCopy(String afGuid,boolean b) throws ServiceException {
		String localAFGuid = evaluteAFGuid(afGuid);
		ReplicationJobScript replicationJobScript = this.getReplicationJobScript(localAFGuid);
		if (replicationJobScript == null)
			return 0;
		
		if (replicationJobScript.getIsPlanPaused()) {
			logger.info("The plan is paused.");
			throw generateAxisFault(FlashServiceErrorCode.VCM_VSB_DISABLED);
		}

		if(replicationJobScript.getAutoReplicate()!=b)
		{
			replicationJobScript.setAutoReplicate(b);
			int result = this.updateRepJobScript(replicationJobScript);
			if(result == 0) {
				String msg = null;
				if(b) 
					msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_RESUMED);
				else
					msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_PAUSED);
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1,
					Constants.AFRES_AFJWBS_GENERAL, new String[] {msg, "", "", "", "" }, afGuid);
			}		
		}
		
		if (replicationJobScript != null && !replicationJobScript.getBackupToRPS()) {		
			// chefr03, Smart Copy, don't delete Observer at all
			// Every time after the job finished, we need start replication
			// And we'll check whether it is Paused inside replication
			//Don't put this into jobQueue monitor area
			this.backupJob.addObserver(this);
		}
		
		syncVCMStatus2Monitor(afGuid);
		return 0;
	}
    /**
     * called by contextListener
     */
//	public void initilizeObserver(){
//		this.backupJob.addObserver(this);
//	}
	
	public SummaryModel getProductionServerSummaryModelFromMonitor(String afGuid) {
		String localAFGuid = evaluteAFGuid(afGuid);
		
		ProductionServerRoot prodRoot = null;
		String vmGuid = "";
		String vmName = "";
		String mostRecentRepTime = "";
		long mostRecentRepTimeMilli = -1;
		int mostRecentRepStatus = -1;
		try{	
			String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
			prodRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(localAFGuid);
			mostRecentRepTime = prodRoot.getMostRecentRepTime();
			mostRecentRepTimeMilli = prodRoot.getMostRecentRepTimeMilli();
			mostRecentRepStatus =  prodRoot.getMostRecentRepStatus();
			ReplicaRoot repRoot = prodRoot.getReplicaRoot();
			
			if(repRoot != null){
				vmGuid = repRoot.getVmuuid();
			}
			
		}catch(Exception e){
			logger.debug(e.getMessage());
		}
		
		FailoverJobScript failoverJobScript = this.getFailoverJobScript(localAFGuid);
		vmName = failoverJobScript.getFailoverMechanism().get(0).getVirtualMachineDisplayName();
		try {
			SummaryModel model = getSummaryModel(localAFGuid, vmGuid, vmName, true);
//			if (model != null && model.getSnapshots() != null) {
//				for (VMSnapshotsInfo info : model.getSnapshots()) {
//					if(info.getTimestamp() > 0) {
//						info.setTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(new Date(info.getTimestamp())));
//					}
//				}
//			}
			
//			EdgeLicenseInfo license = null;
//			try {
//			if (failoverJobScript.getBackupToRPS())
//				license = getConverterService(failoverJobScript).getServiceV2().getConversionLicense(localAFGuid);
//			else
//				license = getD2DService(failoverJobScript).getServiceV2().getConversionLicense(localAFGuid);
//			} catch (Exception e) {
//				logger.error("Fail to get converter license.");
//				logger.error(e.getMessage());
//			}
//			model.setLicenseInfo(license);
			
			ProductionServerRoot root = model.getServerRoot();
			if(root != null) {
				root.setMostRecentRepStatus(mostRecentRepStatus);
				root.setMostRecentRepTime(mostRecentRepTime);
				root.setMostRecentRepTimeMilli(mostRecentRepTimeMilli);
			}
				
			return model;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return null;
	}
	
	
	public SummaryModel getProductionServerSummaryModel(String afGuid) throws ServiceException{
		logger.debug("getSnapshotsForProductionServer() - start"); //$NON-NLS-1$
		String localAFGuid = evaluteAFGuid(afGuid);
	
		//TODO yaoyu01
		//before call monitor service, should login first and reuse instance of WebServiceClient
		HeartBeatJobScript script = this.getHeartBeatJobScript(localAFGuid);
		if (script == null)
			return null;
		
		ProductionServerRoot prodRoot = null;
		String vmGuid = "";
		String vmName = "";
		String mostRecentRepTime = "";
		long mostRecentRepTimeMilli = -1;
		int mostRecentRepStatus = -1;
		try{	
			String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
			prodRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(localAFGuid);
			mostRecentRepTime = prodRoot.getMostRecentRepTime();
			mostRecentRepTimeMilli = prodRoot.getMostRecentRepTimeMilli();
			mostRecentRepStatus =  prodRoot.getMostRecentRepStatus();
			ReplicaRoot repRoot = prodRoot.getReplicaRoot();
			
			if(repRoot != null){
				vmGuid = repRoot.getVmuuid();
			}
			
		}catch(Exception e){
		}
		
		ReplicationDestination dest =HAService.getInstance().getReplicationJobScript(localAFGuid).getReplicationDestination().get(0);
		
		if(dest != null){
			vmName = dest.getVirtualMachineDisplayName();
		}
		
		try {
			logger.debug("protocol:"+script.getHeartBeatMonitorProtocol());
			String monitorName = script.getHeartBeatMonitorHostName();
			logger.debug("host:"+monitorName);
			WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(script);
			SummaryModel model = client.getServiceV2().getSummaryModel(localAFGuid, vmGuid, vmName);
			if (model != null && model.getSnapshots() != null) {
				for (VMSnapshotsInfo info : model.getSnapshots()) {
					if(info.getTimestamp() > 0) {
						info.setTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(new Date(info.getTimestamp())));
					}
				}
			}
			logger.debug("getSnapshotsForProductionServer() - end"); //$NON-NLS-1$
			
//			getLicense(localAFGuid, model);
			
			ProductionServerRoot root = model.getServerRoot();
			if(root != null) {
				root.setMostRecentRepStatus(mostRecentRepStatus);
				root.setMostRecentRepTime(mostRecentRepTime);
				root.setMostRecentRepTimeMilli(mostRecentRepTimeMilli);
			}
				
			return model;
		} catch (SOAPFaultException e) {
			logger.error("getSnapshotsForProductionServer() - exception ignored", e); //$NON-NLS-1$
		}
		
		logger.debug("getSnapshotsForProductionServer() - end"); //$NON-NLS-1$
		return null;
	}

	private void getLicense(String localAFGuid, SummaryModel model) {
		EdgeLicenseInfo license = getConversionLicense(localAFGuid);
		
		if (license != null)
			model.setLicenseInfo(license);
	}

	public EdgeLicenseInfo getConversionLicense(String aFGuid) {
		String localAFGuid = evaluteAFGuid(aFGuid);
		
		EdgeLicenseInfo license = null;
		try {
			MachineDetail detail = new MachineDetail();
			LICENSEDSTATUS statuc = BaseReplicationCommand.checkLicense(localAFGuid, detail, true);
			if (statuc == null)
				return null;
			int licenseInt = EdgeLicenseInfo.LICENSE_ERR;
			if(statuc == LICENSEDSTATUS.VALID)
				licenseInt = EdgeLicenseInfo.LICENSE_SUC;
			else if(statuc == LICENSEDSTATUS.TRIAL)
				licenseInt = EdgeLicenseInfo.LICENSE_WAR;
				
//			if(licenseInt == EdgeLicenseInfo.LICENSE_SUC) {
//				//if succeed, do not discriminate the license type, since the Gui will not show. 
//				//What's more, we can not know the license type of the fetched License since we'll
//				//try physical license if failing to get hypervisor license.
//				license.setPhysicalMachineLicense(licenseInt);
//				license.setVSphereVMLicense(licenseInt);
//				license.setHyperVLicense(licenseInt);
//			}
//			else {
				//Discriminate the license type. Only useful for LICENSE_ERR.
				//For LICENSE_SUC, UI will not show them
				//For LICENSE_WAR, Edge does not produce this type.
			
			MachineType machineType = MachineType.PHYSICAL;
			if (!StringUtil.isEmptyOrNull(detail.getHostName()))
				machineType = detail.getMachineType();
			else{
				MachineDetail detail2 = MachineDetailManager.getInstance().getMachineDetail(aFGuid);
				machineType = detail2.getMachineType();
			}
			license = new EdgeLicenseInfo();
			license.setPhysicalMachineLicense(EdgeLicenseInfo.LICENSE_UNKNOWN);
			license.setVSphereVMLicense(EdgeLicenseInfo.LICENSE_UNKNOWN);
			license.setHyperVLicense(EdgeLicenseInfo.LICENSE_UNKNOWN);
			if(machineType == MachineType.HYPERV_VM) {
				license.setPhysicalMachineLicense(licenseInt);
				license.setHyperVLicense(licenseInt);
			}
			else if(machineType == MachineType.ESX_VM || machineType == MachineType.VSPHERE_ESX_VM) {
				license.setPhysicalMachineLicense(licenseInt);
				license.setVSphereVMLicense(licenseInt);
			}
			else {
				license.setPhysicalMachineLicense(licenseInt);
			}
//			}
		}
		catch(Exception e) {
			logger.error("Error occurs when fetching license:", e);
		}
		
		return license;
	}

	
	public String[] getHypervNetworksFromMonitor(String afGuid,String host, String username,String password){
		
		String localAFGuid = evaluteAFGuid(afGuid);
		
		try {
			HeartBeatJobScript jobScript = HAService.getInstance().getHeartBeatJobScript(localAFGuid);
			WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(jobScript);
			
			String[] networks = client.getServiceV2().getHypervNetworksFromMonitor(host, username, password);
			return networks;
		} catch (Exception e) {
			return new String[0];
		}
		
	}
	
	public String getMonitorD2DInstallPath(String afGuid){
		String installPath = null;
		String localAFGuid = evaluteAFGuid(afGuid);
		try {
			HeartBeatJobScript jobScript = HAService.getInstance().getHeartBeatJobScript(localAFGuid);
			WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(jobScript);
			installPath = client.getServiceV2().getMonitorD2DInstallPath();
			return installPath;
		} catch (Exception e) {
			logger.error("Failed to get monitor D2D install path.");
			return installPath;
		}
	}
	
	public void startFailoverForProductionServer(String afGuid,VMSnapshotsInfo vmSnapInfo) {
		logger.debug("startFailoverForProductionServer(VMSnapshotsInfo) - start"); //$NON-NLS-1$
		if(StringUtil.isEmptyOrNull(afGuid)){
			afGuid = retrieveCurrentNodeID();
		}
		HeartBeatJobScript script = this.getHeartBeatJobScript(afGuid);
		if (script == null)
			throw AxisFault.fromAxisFault("Null heartbeat",MonitorWebServiceErrorCode.Common_NULL_HeartBeat);
		
		try {
			logger.debug("protocol:"+script.getHeartBeatMonitorProtocol());
			String monitorName = script.getHeartBeatMonitorHostName();
			logger.debug("host:"+monitorName);
			
			WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(script);
			client.getServiceV2().startFailover(afGuid, vmSnapInfo);
			logger.debug("startFailoverForProductionServer(VMSnapshotsInfo) - end"); //$NON-NLS-1$
			
			syncVCMStatus2Monitor(afGuid);
			
		} catch (SOAPFaultException e) {
			logger.error("startFailoverForProductionServer(VMSnapshotsInfo) - exception.", e); //$NON-NLS-1$
			throw e;
		}
		
	}
	
	public SummaryModel getSummaryModel(String afguid, String vmuuid, String vmname, boolean skipHypervisor) {
		
		SummaryModel model = new SummaryModel();
		FailoverJobScript jobScript = HACommon.getFailoverJobScriptObject(afguid);
		if(jobScript == null){
			logger.error("Failover jobscript is not set.");
			return model;
		}
		
		//retrieve last replication time from it
		ProductionServerRoot serverRoot = null;
		try {	
			serverRoot = RepositoryManager.getProductionServerRoot(afguid);
			// always update the latest retention count
			// if(serverRoot.getRetentionCount() <= 0) //if no replication once finished
			if (serverRoot != null){
				serverRoot.setRetentionCount(jobScript.getRetentionCount()); 
			}
		} catch (Exception e) {
			logger.debug("Failed to get production server root." + e.getMessage());
		}
		
		//if no replication once was launched
		if(serverRoot == null) {
			serverRoot = new ProductionServerRoot(); 
			serverRoot.setProductionServerHostname(jobScript.getProductionServerName());
			serverRoot.setRetentionCount(jobScript.getRetentionCount());
		}
			
		model.setServerRoot(serverRoot);
				
		Virtualization mechanism = jobScript.getFailoverMechanism().get(0);		
		model.setHyperVModel(mechanism instanceof HyperV ? true : false);
		
		if (skipHypervisor)
			return model;
		
		if(mechanism instanceof HyperV){
			int HyperVRetentionCount = CommonUtil.getMaxSnapshotCountForHyperV();
			if(serverRoot.getRetentionCount() > HyperVRetentionCount)
				 serverRoot.setRetentionCount(HyperVRetentionCount);
			
			VMSnapshotsInfo[] snapshots = getVMSnapshots(afguid, vmuuid, vmname);
			model.setSnapshots(Arrays.asList(snapshots));
			
			String subRoot = null;
			try{
				if (serverRoot.getReplicaRoot() != null)
					subRoot = serverRoot.getReplicaRoot().getRepSubRoot();
				
			}catch (Exception e) {
				logger.warn(e.getMessage(),e);
			}
			
			if(StringUtil.isEmptyOrNull(subRoot)){
				subRoot = vmname + "\\sub0001";
			}
			
			fetchHyperVDestinationSize(model, jobScript, mechanism,subRoot);
			
			return model;
		}else if(mechanism instanceof VMwareESX || mechanism instanceof VMwareVirtualCenter){
			if(serverRoot.getRetentionCount() > CommonUtil.getMaxSnapshotCountForVMware())
				 serverRoot.setRetentionCount(CommonUtil.getMaxSnapshotCountForVMware());
			
			CAVirtualInfrastructureManager vmwareManager = null;
			try {
				vmwareManager = getVirtualInfrastructureMgr(mechanism);
				if(vmwareManager == null)
					return model;
				
				try {
					if(StringUtil.isEmptyOrNull(vmuuid)) {
						ReplicaRoot repRoot = serverRoot.getReplicaRoot();
						if(repRoot instanceof VMWareESXHostReplicaRoot){
							vmuuid = ((VMWareESXHostReplicaRoot)repRoot).getVmuuid();
							vmname = ((VMWareESXHostReplicaRoot)repRoot).getVmname();
						}else if (repRoot instanceof VMWareVirtualCenterReplicaRoot){
							vmuuid = ((VMWareVirtualCenterReplicaRoot)repRoot).getVmuuid();
							vmname = ((VMWareVirtualCenterReplicaRoot)repRoot).getVmname();
						}	
					}
					
				} catch (Exception e) {
				}
				
				fechSnapShots(afguid, vmuuid, vmname, model, vmwareManager);
					
				fetchDataStore(vmuuid, vmname, model, mechanism, vmwareManager);
			}
			finally {
				if(vmwareManager != null) {
					try {
						vmwareManager.close();
					}catch(Exception e) 
					{
						logger.error("Failed to close CAVirtualInfrastructureManager connection:"+e.getMessage());
					}
				}
			}
		}
		return model;
	
	}

	private void fetchDataStore(String vmuuid, String vmname,
			SummaryModel model, Virtualization mechanism,
			CAVirtualInfrastructureManager vmwareManager) {
		String esxHost = "";
		if(mechanism instanceof VMwareESX){
			VMwareESX esxServer = (VMwareESX)mechanism;
			esxHost = esxServer.getEsxName();
		}else if (mechanism instanceof VMwareVirtualCenter) {
			VMwareVirtualCenter vCenter = (VMwareVirtualCenter)mechanism;
			esxHost = vCenter.getEsxName();
		}
		
		ESXNode node = null;
		try {
			ArrayList<ESXNode> nodeList = vmwareManager.getESXNodeList();
			for (ESXNode esxNode : nodeList) {
				if(esxNode.getEsxName().equals(esxHost)){
					node = esxNode;
					break;
				}
			}
		} catch (Exception e) {
			logger.error("Failed to get esxnode." + e.getMessage());
			logger.error(e);
			return;
		}
		
		VMwareStorage[] results = new VMwareStorage[0];
		try {
			if(!StringUtil.isEmptyOrNull(vmuuid) && !StringUtil.isEmptyOrNull(vmname))
				results = vmwareManager.getVMwareStoragesPerVM(node,vmuuid,vmname);
		} catch (Exception e) {
			logger.info("Failed to get vm storage by vm uuid and vm name." + e.getMessage());
			logger.info(e);
		}
		finally {
			if(results == null || results.length == 0)
			{
				Set<String> storages = new HashSet<String>();
				List<DiskDestination> diskDestinations = mechanism.getDiskDestinations();
				for (DiskDestination diskDestination : diskDestinations) {
					storages.add(diskDestination.getStorage().getName());
				}
				try {
					results = vmwareManager.getVMwareStorages(node,
											storages.isEmpty()?null:storages.toArray(new String[0]));
				} catch (Exception e1) {
					logger.error("Failed to get vm storage by storage." + e1.getMessage());
					logger.error(e1);
					return;
				}
			}
		}
		
		for(VMwareStorage storage : results){
			VMStorage tmp = new VMStorage();
			tmp.setName(storage.getName());
			tmp.setColdStandySize(storage.getColdStandySize());
			tmp.setFreeSize(storage.getFreeSize());
			tmp.setTotalSize(storage.getTotalSize());
			tmp.setOtherSize(storage.getOtherSize());
			model.getStorages().add(tmp);
		}
		
	}

	private void fechSnapShots(String afguid, String vmuuid, String vmname,
			SummaryModel model, CAVirtualInfrastructureManager vmwareManager) {
		try {
			VMSnapshotsInfo[] info = getVMWareSnapshots(vmwareManager, afguid, vmname, vmuuid);
			model.getSnapshots().addAll(Arrays.asList(info));
			logger.equals("hahaha:" + info.length);
			
		} catch (Exception e) {
			logger.error("Fails to get snap shot number:" + e.getMessage(), e);
		}
	}

	private CAVirtualInfrastructureManager getVirtualInfrastructureMgr(
			Virtualization mechanism) {
		CAVirtualInfrastructureManager vmwareManager = null;
		String hostname = "";
		String username = "";
		String password = "";
		String protocol = "";
		int port = 0;
		if(mechanism instanceof VMwareESX){
			VMwareESX esxServer = (VMwareESX)mechanism;
			hostname = esxServer.getHostName();
			username = esxServer.getUserName();
			password = esxServer.getPassword();
			protocol = esxServer.getProtocol();
			port = esxServer.getPort();
		}else if (mechanism instanceof VMwareVirtualCenter) {
			VMwareVirtualCenter vCenter = (VMwareVirtualCenter)mechanism;
			hostname = vCenter.getESXHostName();
			username = vCenter.getUserName();
			password = vCenter.getPassword();
			protocol = vCenter.getProtocol();
			port = vCenter.getPort();
		}
		else {
			return null;
		}
		
		try {
			vmwareManager = CAVMwareInfrastructureManagerFactory
			.getCAVMwareVirtualInfrastructureManager(hostname,username,password, protocol, true, port);
		} catch (Exception e) {
			logger.error("Failed to get vmware web service connection." + e.getMessage());
			logger.error(e);
		}
		return vmwareManager;
	}

	private void fetchHyperVDestinationSize(SummaryModel model,
			FailoverJobScript jobScript, Virtualization mechanism,String subRoot) {
		List<DiskDestination> destList = mechanism.getDiskDestinations();
		
		Set<String> destSet = new HashSet<String>();
		
		for (DiskDestination dest : destList) {
			String destName = dest.getStorage().getName();
			String volume = getVolume(destName);
			if(destSet.contains(volume.toLowerCase()))
				continue;
			
			destSet.add(volume.toLowerCase());
			
			BackupConfiguration configuration = new BackupConfiguration();
			
			if(!destName.endsWith("\\") && !destName.endsWith("/"))
				destName += "\\";
			
			String productServerName = model.getServerRoot().getProductionServerHostname();
		
			configuration.setDestination(destName);
			
			long coldStandbySize = 0;
			
			if(!subRoot.endsWith("\\")){
				subRoot += "\\";
			}
			
			String destination = destName + subRoot + productServerName;
			
			logger.debug("destination: " + destination);
			
			File vmDestination = new File(destination);
			if(vmDestination.exists()){
				try {
					coldStandbySize = getNativeFacade().GetOfflinecopySize(destination);
					dest.getStorage().setColdStandySize(coldStandbySize);
				}catch(Exception e) {
					logger.warn("Fails to get the OfflinecopySize of " + destination + ". The directory may not exist." + e.getMessage());
				}
			}
			
			try {
				DestinationCapacity capacity = BackupService.getInstance().getDestSizeInformation(configuration);
				
				if(capacity.getTotalVolumeSize() == 0 && destName.indexOf(":") > 0) {
					configuration.setDestination(destName.substring(0, (destName.indexOf(":") + 1)));
					capacity = BackupService.getInstance().getDestSizeInformation(configuration);
				}
					
				long totalFreeSize = capacity.getTotalFreeSize();
				dest.getStorage().setFreeSize(totalFreeSize);
				long totalVolumeSize = capacity.getTotalVolumeSize();
				dest.getStorage().setTotalSize(totalVolumeSize);
				dest.getStorage().setOtherSize(totalVolumeSize - totalFreeSize - coldStandbySize);
				model.getStorages().add(dest.getStorage());
				
			} catch (ServiceException e) {
				logger.error("Fails to get the capacity of " + destName, e);
			}
			
		}
	}
	
	private String getVolume(String destName) {
		
		int colonIndex = destName.indexOf(':');
		if(colonIndex == 1) {
			return destName.substring(0, colonIndex) + ":\\";
		}
		else if(destName.length() == 1)
			return destName + ":\\";
		else 
			return destName;
	
	}

	//Operate the local HyperV 
	//Get the VM list which has offline copied.
	public VirtualMachineInfo[] GetHyperVVmList(){
		logger.debug("GetHyperVVmList() - start"); 
		long handle = 0;
		NativeFacade facade = HAService.getInstance().getNativeFacade();
		try {
			handle = facade.OpenHypervHandle("","","");
			Map<String, String> vmMap=facade.GetHyperVVmList(handle);
			List<VirtualMachineInfo> vmList=new ArrayList<VirtualMachineInfo>();
			VMInfomationModelManager manager = null;
			VirtualMachineInfo vMachineInfo = null;
			if((vmMap!=null)&&(vmMap.size()>0)){
				Iterator<Entry<String, String>> itor=vmMap.entrySet().iterator();   
				while(itor.hasNext()){   
					Map.Entry<String,String> entry=itor.next(); 
					String vmGUID=entry.getKey();
					String vmName=entry.getValue();
					//load vmsnapshot.xml from multi-replication destinations.
					manager = HACommon.getSnapshotModeManagerByVMGUID(vmGUID);
					if(manager != null){
						//return new VirtualMachineInfo[0];
						vMachineInfo=manager.getVirtualMachineInfo(vmGUID);
						if(vMachineInfo!=null){
							vMachineInfo.setVmName(vmName);
							vmList.add(vMachineInfo);
							}
					}
					else
					{
						// Instant VM case, get vm notes from hyperv jni
						String vmNotes = facade.GetHyperVVmNotes(handle, vmGUID);
						if(vmNotes.equals(IVM_UDP_NOTE))
						{
							vMachineInfo = new VirtualMachineInfo();
							vMachineInfo.setVmName(vmName);
							vMachineInfo.setVmGUID(vmGUID);
							vMachineInfo.SetIsInstantVM(true);
							vmList.add(vMachineInfo);
						}
					}
				}  
			}
			
			return vmList.toArray(new VirtualMachineInfo[0]);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw AxisFault.fromAxisFault(e.getMessage(),FlashServiceErrorCode.Common_ErrorOccursInService);
		}finally{
			try {
				logger.debug("GetHyperVVmList() - end"); 
				facade.CloseHypervHandle(handle);
			} catch (ServiceException e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}
	
	public int GetHyperVVmState(String vmGuid){
		logger.debug("GetHyperVVmState(vmGuid) - start"); 
		long handle = 0;
		NativeFacade facade = HAService.getInstance().getNativeFacade();
		try {
			
			handle = facade.OpenHypervHandle("","","");
			return facade.GetHyperVVmState(handle, vmGuid);
		} catch (Exception e) {
			logger.error(e.getMessage());
			
			throw AxisFault.fromAxisFault(e.getMessage(),FlashServiceErrorCode.Common_ErrorOccursInService);
		}finally{
			try {
				logger.debug("GetHyperVVmState(vmGuid) - end"); 
				facade.CloseHypervHandle(handle);
			} catch (ServiceException e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}
	
	public VMSnapshotsInfo[] GetHyperVVmSnapshots(String vmGuid){
		logger.debug("GetHyperVVmSnapshots(vmGuid) - start"); 
		long handle = 0;
		NativeFacade facade = HAService.getInstance().getNativeFacade();
		VMInfomationModelManager manager = HACommon.getSnapshotModeManagerByVMGUID(vmGuid);
		try {
				
			handle = facade.OpenHypervHandle("","","");
			Map<String, String> snapShotMap=facade.GetHyperVVmSnapshots(handle, vmGuid);
			List<VMSnapshotsInfo> vmSnapshotList=new ArrayList<VMSnapshotsInfo>();
			if((snapShotMap!=null)&&(snapShotMap.size()>0)){
				VirtualMachineInfo vm=manager.getVirtualMachineInfo(vmGuid);
				SortedSet<VMSnapshotsInfo> vmSnapshotsSet=null;
				if(vm!=null){
					vmSnapshotsSet=manager.getSnapshots(vm);
				}
				Iterator<Entry<String, String>> itor=snapShotMap.entrySet().iterator();
				while(itor.hasNext()&&vmSnapshotsSet!=null){
					Entry<String, String> entry=itor.next();
					String snapShotGUID=entry.getKey();
					for (VMSnapshotsInfo vmSnapshotsInfo : vmSnapshotsSet) {
						if((vmSnapshotsInfo.getSnapGuid().compareToIgnoreCase(snapShotGUID)==0)
								&&(!vmSnapshotsInfo.isDRSnapshot())){
							vmSnapshotList.add(vmSnapshotsInfo);
						}
					}

				}
			}
			logger.debug("GetHyperVVmSnapshots(vmGuid) - end"); 
			return vmSnapshotList.toArray(new VMSnapshotsInfo[0]);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw AxisFault.fromAxisFault(e.getMessage(),FlashServiceErrorCode.Common_ErrorOccursInService);
		}finally{
			try {
				facade.CloseHypervHandle(handle);
			} catch (ServiceException e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}
	
	public int PowerOnHyperVVM(String vmGuid){
		logger.debug("PowerOnHyperVVM(vmGuid) - start"); 
		long handle = 0;
		NativeFacade facade = HAService.getInstance().getNativeFacade();
		try {
			
			handle = facade.OpenHypervHandle("","","");
			return facade.PowerOnHyperVVM(handle, vmGuid);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw AxisFault.fromAxisFault(e.getMessage(),FlashServiceErrorCode.Common_ErrorOccursInService);
		}finally{
			try {
				logger.debug("PowerOnHyperVVM(vmGuid) - end"); 
				facade.CloseHypervHandle(handle);
			} catch (ServiceException e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}
	
	public int ShutdownHyperVVM(String vmGuid){
		logger.debug("ShutdownHyperVVM(vmGuid) - start"); 
		long handle = 0;
		NativeFacade facade = HAService.getInstance().getNativeFacade();
		try {
			
			handle = facade.OpenHypervHandle("","","");
			return facade.ShutdownHyperVVM(handle, vmGuid);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw AxisFault.fromAxisFault(e.getMessage(),FlashServiceErrorCode.Common_ErrorOccursInService);
		}finally{
			try {
				logger.debug("ShutdownHyperVVM(vmGuid) - end"); 
				facade.CloseHypervHandle(handle);
			} catch (ServiceException e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}
	
	public int shutdownVMForProductServer(String afGuid){

		logger.debug("shutdownVMForProductServer() - start"); //$NON-NLS-1$
		
		String localAFGuid = evaluteAFGuid(afGuid);
		
		try {
			
			WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(localAFGuid);
			final int shutdownVM = client.getServiceV2().shutdownVM(localAFGuid);
			logger.info("shutdownVMForProductServer return " + shutdownVM); 
			logger.debug("shutdownVMForProductServer() - end"); 

			syncVCMStatus2Monitor(afGuid);
			return shutdownVM;
		} catch (SOAPFaultException e) {
			logger.error("shutdownVMForProductServer() - exception.", e); //$NON-NLS-1$
			throw e;
		}
	}
	
	public int shutdownVM(String afGuid) {
		
		logger.debug("shutdownVM(String) - start"); //$NON-NLS-1$
		
		FailoverJobScript script = HACommon.getFailoverJobScriptObject(afGuid);
		Virtualization virtualization = script.getFailoverMechanism().get(0);
		
		switch (virtualization.getVirtualizationType())
		{
			case HyperV: 
				VirtualMachineInfo vmInfo = HeartBeatModelManager.getVMInfo(afGuid);
				if (vmInfo == null && ManualConversionUtility.isVSBWithoutHASupport(script)) {
					vmInfo = getVirtualMachineInfoFromRepository(afGuid);
				}
				if(vmInfo == null) {
					logger.error("fails to get vm information.");
					return 1;
				}
				
				String vmGuid = vmInfo.getVmGUID();
				
				String vmName = vmInfo.getVmName();
				if(StringUtil.isEmptyOrNull(vmName)) {
					vmName = virtualization.getVirtualMachineDisplayName();
				}
				
				int ret = ShutdownHyperVVM(vmGuid);
				if (ret == 0) {
					String msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_VM_SHUTDOWN, vmName);
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, afGuid);
				} else {
					String msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_VM_SHUTDOWN_FAILED, vmName);
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, -1, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, afGuid);
				}
				
				return ret;
			case VMwareESX: 
			case VMwareVirtualCenter:
				return shutdownVMwareVM(afGuid, virtualization);
		}
		
		logger.debug("shutdownVM(String) return 1 - end");
		return 1;
		
	}

	private int shutdownVMwareVM(String afGuid, Virtualization virtualization) {
		String vmname = "";
		String vmuuid = "";
		if(virtualization instanceof VMwareESX){
			VMwareESX esxServer = (VMwareESX)virtualization;
			vmname = esxServer.getVmname();
			vmuuid = esxServer.getUuid();
		}else if (virtualization instanceof VMwareVirtualCenter) {
			VMwareVirtualCenter vCenter = (VMwareVirtualCenter)virtualization;
			vmname = vCenter.getVmname();
			vmuuid = vCenter.getUuid();
		}
		
		if(StringUtil.isEmptyOrNull(vmname) || StringUtil.isEmptyOrNull(vmuuid)){
			String xml = CommonUtil.D2DHAInstallPath+"Configuration\\repository.xml";
			ProductionServerRoot prodRoot;
			try {
				prodRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(afGuid);
				ReplicaRoot hostRoot = prodRoot.getReplicaRoot();
				if(virtualization instanceof VMwareESX){
					VMWareESXHostReplicaRoot ESXRoot = (VMWareESXHostReplicaRoot)hostRoot;
					vmname = ESXRoot.getVmname();
					vmuuid = ESXRoot.getVmuuid();
				}
				else if(virtualization instanceof VMwareVirtualCenter){
					VMWareVirtualCenterReplicaRoot vCenterRoot = (VMWareVirtualCenterReplicaRoot)hostRoot;
					vmname = vCenterRoot.getVmname();
					vmuuid = vCenterRoot.getVmuuid();
				}
				if(StringUtil.isEmptyOrNull(vmname) || StringUtil.isEmptyOrNull(vmuuid)){
					logger.error("VM UUID and name is not found. shutdown vm stop!!!!!");
					return 1;
				}
			} catch (Exception e) {
				logger.error("VM UUID and name is not found. shutdown vm stop!!!!!", e);
				return 1;
			}
			
		}
		
		logger.info("Shutting down the VM " + vmname);
		CAVirtualInfrastructureManager vmwareManager = null;
		try {
			vmwareManager = getVirtualInfrastructureMgr(virtualization);
			
			String msg = "";
			int ret = vmwareManager.checkVMToolsVersion(vmname, vmuuid);
			if(ret == 1){
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_VMWARE_TOOL_OLD_VERSION);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, afGuid);
			}else if (ret == 0){				
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_VMWARE_TOOL_QUERY_FAILED);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, afGuid);
			}			
			
			vmwareManager.shutdownVM(vmname, vmuuid);

			msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_VM_SHUTDOWN, vmname);
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, afGuid);
			return 0;
		} catch (Exception e) {
			String msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_VM_SHUTDOWN_FAILED, vmname);
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, -1, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, afGuid);

			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault(e.getMessage(),FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally {
			if(vmwareManager != null) {
				try {
					vmwareManager.close();
				}catch(Exception e) {
					logger.error("Failed to close CAVirtualInfrastructureManager connection:"+e.getMessage());
				}
			}
		}
	}
	
	
	public String TakeHyperVVmSnapshot(String vmGuid, String snapshotName, 
			String snapshotNotes) throws Exception{
		logger.debug("TakeHyperVVmSnapshot(vmGuid,snapshotName,snapshotNotes) - start"); 
		
		long handle = 0;
		NativeFacade facade = HAService.getInstance().getNativeFacade();
		try {
			handle = facade.OpenHypervHandle("","","");
		}catch (Exception e) {
			logger.error("Failed to open hyperv handle:"+e.getMessage());
			return "";
		}
		
		//get vm information
		VMInfomationModelManager manager = HACommon.getSnapshotModeManagerByVMGUID(vmGuid);
		VirtualMachineInfo vmInfo = null;
		boolean isInstantVM = false;
		if(manager != null){
			vmInfo=manager.getVirtualMachineInfo(vmGuid);
			if(vmInfo==null){
				logger.error("Failed to find the vmGUID:"+vmGuid+" in the VMSnapshotsModel.xml file");
				CloseHyperVHandle(facade, handle);
				return "";
			}
		}
		else
		{
			// Instant VM case, get vm notes from hyperv jni
			String vmNotes = facade.GetHyperVVmNotes(handle, vmGuid);
			if(vmNotes.equals(IVM_UDP_NOTE))
			{
				logger.info("The vm is instantVM.");
				vmInfo = new VirtualMachineInfo();
				vmInfo.setVmGUID(vmGuid);
				vmInfo.SetIsInstantVM(true);
				isInstantVM = true;
			}
			else
			{
				logger.info("The vm is not instantVM.");
				CloseHyperVHandle(facade, handle);
				return "";
			}
		}
		
		String snapGUID="";
		if(!isInstantVM){
			ProductionServerRoot prodRoot = null;
			String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
			try{	
				prodRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(vmInfo.getAfguid());
			}catch(Exception e){
				logger.error("Failed to get the ProductionServerRoot:"+e.getMessage(),e);
				prodRoot = null;
			}
			if(prodRoot==null){
				logger.error("The prodRoot with afguid:"+vmInfo.getAfguid()+" is not found");
				CloseHyperVHandle(facade, handle);
				return "";
			}
					
			try {
				
				SortedSet<VMSnapshotsInfo> lastSnapshotForD2DSession = HACommon.getSnapshotForD2DSessionByVMGUID(vmGuid,handle,vmInfo.getAfguid(), false);
	
				Date d = new Date();
				String sessionGUID="DR-"+Long.toString(d.getTime());
				VMSnapshotsInfo snapshotInfo = new VMSnapshotsInfo();
				snapshotInfo.setSessionName(snapshotName);
				snapshotInfo.setSessionGuid(sessionGUID);
				snapshotInfo.setDesc(snapshotNotes);
				long time = System.currentTimeMillis();
				snapshotInfo.setTimestamp(time);
				snapshotInfo.setTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(new Date(time)));
				snapGUID=RepositoryManager.createSessionSnapshot(handle, prodRoot, vmInfo, lastSnapshotForD2DSession,
						snapshotInfo, true,manager,null);
				
			}catch (Exception e) {
				logger.error("Failed to take snapshot:"+e.getMessage());
				//CloseHyperVHandle(facade, handle);
			}
		}else{
			try {
				snapGUID = facade.TakeHyperVVmSnapshot(handle, vmGuid, snapshotName, snapshotNotes);
			}catch (Exception e) {
				logger.error("Failed to take snapshot for instant vm:"+e.getMessage());
				//CloseHyperVHandle(facade, handle);
			}
			
			try {
				ReplicationService.startHATransServerDirectly();
			} catch (Exception e) {
				logger.error("Fails to start HATransServer.exe process", e);
			}
		}
		
		CloseHyperVHandle(facade, handle);
		return snapGUID;
	}
	
	private void CloseHyperVHandle(NativeFacade facade,long handle){
		try {
			facade.CloseHypervHandle(handle);
		} catch (Exception e) {
			logger.error("Failed to close the HyperV:"+e.getMessage(),e);
		}
	}
	
	public void cancelReplication(String afGuid) throws ServiceException {
		
		String localAFGuid = evaluteAFGuid(afGuid);
		
		logger.info("Begin to cancel the conversion job, afGuid: " + afGuid);

		ReplicationJobScript replicationJobScript = getReplicationJobScript(localAFGuid);
		if (!replicationJobScript.getReplicationDestination().isEmpty()) {
			
			RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(localAFGuid);
			int oldPhase = -1; 
			synchronized (jobMonitor) {
				oldPhase = jobMonitor.getRepPhase();
				
				if (oldPhase == RepJobMonitor.REP_JOB_PHASE_EXIT || jobMonitor.getId() == -1) {
					logger.info("There's no conversion job running, no need to cancel it.");
					return;
				}
			}
			
			if (!changeMonitorStateToCancelling(localAFGuid))
				return;
			
			String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_BEGIN_TO_CANCEL);
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobMonitor.getId(), Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, afGuid);
			
			logger.info("Cancelling the conversion job, jobID: " + jobMonitor.getId() + ", source machine: " + replicationJobScript.getAgentNodeName());
			
			try {
				NativeFacade facade = getNativeFacade();
				ReplicationDestination replicationDestination = replicationJobScript
					.getReplicationDestination().get(0);
				switch (replicationDestination.getDestProtocol()) {
				case SharedFolder:
				case HeartBeatMonitor:
					cancelHyperRepliation(localAFGuid,facade);
					break;
				case VMwareESX:
				case VMwareVCenter:
					if(replicationDestination.isProxyEnabled()) 
						cancelHyperRepliation(localAFGuid,facade);
					else {
						String id = CommonService.getInstance().getRepJobMonitor(localAFGuid).getId()+"";
						facade.CancelReplicationForVMware(id);
					}
					break;
				}
				
				//Change again in case during the stopping phase backend api changes the state.
				changeMonitorStateToCancelling(localAFGuid);
				
				logger.info("Ok to cancel the conversion job.");
			}
			catch(ServiceException e) {
				synchronized (jobMonitor) {
					//if the phase is not changed by other Threads
					if(jobMonitor.getRepPhase() == RepJobMonitor.REP_JOB_PHASE_CANCELLING) 
						jobMonitor.setRepPhase(oldPhase);
					
					jobMonitor.setCurrentJobCancelled(false);
				}
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_CANCEL_FAILED);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobMonitor.getId(), Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, localAFGuid);
				
				logger.info("Failed to cancel the conversion job.");
				throw e;
			}
		}
	}

	private boolean changeMonitorStateToCancelling(String afGuid) {
		RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(afGuid);
		synchronized (jobMonitor) {
			if (jobMonitor.isCurrentJobCancelled())
			{
				logger.info("The replication has already been set to cancelling, skip the operation");
				return false;
			}
			else {			
				jobMonitor.setRepPhase(RepJobMonitor.REP_JOB_PHASE_CANCELLING);
				jobMonitor.setCurrentJobCancelled(true);
			}
		}
		return true;
	}
	
	private void cancelHyperRepliation(String afGuid,NativeFacade facade)
			throws ServiceException {
		facade.CancelReplicationForHyperV(afGuid);
	}

	public FileVersion GetVDDKVersion(){
		FileVersion fileVersion=new FileVersion();
		try {
			int result = VMWareJNI.GetVDDKVersion(fileVersion);
			if(result!=0){
				logger.error("Failed to get the VDDK version,with ErrorCode="+result);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return fileVersion;
	}
	
	public String getCurrentRunningSnapShotGuid(String afGuid) throws ServiceException {
		logger.debug("getCurrentRunningSnapShotGuid(String) - start"); 
		
		VirtualMachineInfo vmInfo = HeartBeatModelManager.getVMInfo(afGuid);
		if (vmInfo == null && ManualConversionUtility.isVSBWithoutHASupport(getFailoverJobScript(afGuid))) {
			vmInfo = getVirtualMachineInfoFromRepository(afGuid);
		}
		if(vmInfo == null) {
			logger.error("fails to get vm information.");
			return null;
		}
		
		String vmGuid = vmInfo.getVmGUID();
		
		FailoverJobScript script = HACommon.getFailoverJobScriptObject(afGuid);
		
		Virtualization virtualization = script.getFailoverMechanism().get(0);
		
		switch (virtualization.getVirtualizationType())
		{
			case HyperV: 
				return getSnapShotGuiForHyperV(vmGuid);
				
			case VMwareESX: 
			case VMwareVirtualCenter:
				return getSnapShotGuiForVMware(afGuid, vmInfo, virtualization);
				
			default:
				return null;
		}
		
	}
	
	private VirtualMachineInfo getVirtualMachineInfoFromRepository(String afGuid) {
		VirtualMachineInfo vmInfo = null;
		logger.info("The jobscript is Remote VCM, try to retrieve vmInfo from local.");
		try {
			ProductionServerRoot serverRoot = RepositoryManager.getProductionServerRoot(afGuid);
			ReplicaRoot repRoot = serverRoot.getReplicaRoot();
			if (repRoot.getVmuuid() != null) {
				vmInfo = new VirtualMachineInfo();
				vmInfo.setVmGUID(repRoot.getVmuuid());
				vmInfo.setVmName(repRoot.getVmname());
			}
		} catch (HAException e) {
			logger.error("Error on fetch vmuuid for remote vcm.", e);
		}
		return vmInfo;
	}

	private String getSnapShotGuiForVMware(String afGuid, VirtualMachineInfo vmInfo,
			Virtualization virtualization) {
		
		String vmName = vmInfo.getVmName();
		String vmUUID = vmInfo.getVmGUID();
		if(logger.isDebugEnabled()) {
			logger.debug("vm name: " + vmName + ", vm uuid:" + vmUUID);
		}
		ProductionServerRoot serverRoot = null;
		try {	
			if(StringUtil.isEmptyOrNull(vmName)) {
				serverRoot = RepositoryManager.getProductionServerRoot(afGuid);
				ReplicaRoot repRoot = serverRoot.getReplicaRoot();
				if(repRoot instanceof VMWareESXHostReplicaRoot){
					vmUUID = ((VMWareESXHostReplicaRoot)repRoot).getVmuuid();
					vmName = ((VMWareESXHostReplicaRoot)repRoot).getVmname();
				}else if (repRoot instanceof VMWareVirtualCenterReplicaRoot){
					vmUUID = ((VMWareVirtualCenterReplicaRoot)repRoot).getVmuuid();
					vmName = ((VMWareVirtualCenterReplicaRoot)repRoot).getVmname();
				}
			}
			
		} catch (Exception e) {
			logger.error("Failed to get vm name and vm uuid: " + e.getMessage(), e);
			return null;
		}
		
		CAVirtualInfrastructureManager vmwareManager = null; 
		try {
			vmwareManager = getVirtualInfrastructureMgr(virtualization);
			if(vmwareManager == null) {
				logger.error("Fails to get CAVirtualInfrastructureManager.");
				logger.error("vm name: " + vmName + ", vm uuid:" + vmUUID);
				return null;
			}
			
			return vmwareManager.getCurrentSnapshot(vmName, vmUUID);
		} catch (Exception e) {
			logger.error("Fails to get snapshot guid corresponding to the current running vm:" + e.getMessage(), e);
			return null;
		}
		finally {
			if(vmwareManager != null) {
				try {
					vmwareManager.close();
				}catch(Exception e) 
				{
					logger.error("Failed to close CAVirtualInfrastructureManager connection:"+e.getMessage());
				}
			}
		}
	}

	private String getSnapShotGuiForHyperV(String vmGuid) {
		long handle = 0;
		NativeFacade facade = HAService.getInstance().getNativeFacade();
		try {
			handle = facade.OpenHypervHandle("","","");
			String snapShotId = facade.GetLastSnapshotForHyper(handle, vmGuid);
			if(snapShotId != null) {
			   int state = facade.GetHyperVVmState(handle, vmGuid);
			   if(state != HyperVJNI.VM_STATE_RUNNING)
				   return null;
			}
			
			logger.debug("getCurrentRunningSnapShotGuid(String) - end"); 
			return snapShotId;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault(e.getMessage(),FlashServiceErrorCode.Common_ErrorOccursInService);
		}finally{
			try {
				logger.debug("getCurrentRunningSnapShotGuid(String) - end"); 
				if(handle != 0)
					facade.CloseHypervHandle(handle);
			} catch (ServiceException e) {
				logger.error("Failed to getCurrentRunningSnapShotGuid(String):" + e.getMessage());
			}
		}
	}
	
	public String getRunningSnapShotGuidForProduction(String afGuid) {
		logger.debug("getRunningSnapShotGuidForProductServer(VMSnapshotsInfo) - start"); //$NON-NLS-1$
		
		String localAFGuid = evaluteAFGuid(afGuid);
		
		try {
			
			WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(localAFGuid);
			logger.debug("getRunningSnapShotGuidForProductServer(VMSnapshotsInfo) - end"); //$NON-NLS-1$
			return client.getServiceV2().getCurrentRunningSnapShotGuid(localAFGuid);
		} catch (SOAPFaultException e) {
			logger.error("getRunningSnapShotGuidForProductServer(VMSnapshotsInfo) - exception.", e); //$NON-NLS-1$
			throw e;
		}
			
	}

//	public WebServiceClientProxy getMonitorSevice(String afGuid) {
//		HeartBeatJobScript script = this.getHeartBeatJobScript(afGuid);
//		if (script == null)
//			throw AxisFault.fromAxisFault("Null heartbeat",MonitorWebServiceErrorCode.Common_NULL_HeartBeat);
//		
//		logger.debug("protocol:"+script.getHeartBeatMonitorProtocol());
//		String monitorName = script.getHeartBeatMonitorHostName();
//		logger.debug("host:"+monitorName);
//		String protocol = script.getHeartBeatMonitorProtocol() +":";
//		int monitorPort = script.getHeartBeatMonitorPort();
//		
//		WebServiceClientProxy client = WebServiceFactory.getFlashServiceV2(protocol, monitorName, monitorPort);
//		return client;
//	}
	
//	private WebServiceClientProxy getMonitorSeviceWithSpecialTimeout(String afGuid,int connectTimeout,int requestionTimeout) {
//		HeartBeatJobScript script = this.getHeartBeatJobScript(afGuid);
//		if (script == null)
//			throw AxisFault.fromAxisFault("Null heartbeat",MonitorWebServiceErrorCode.Common_NULL_HeartBeat);
//		
//		logger.debug("protocol:"+script.getHeartBeatMonitorProtocol());
//		String monitorName = script.getHeartBeatMonitorHostName();
//		logger.debug("host:"+monitorName);
//		String protocol = script.getHeartBeatMonitorProtocol() +":";
//		int monitorPort = script.getHeartBeatMonitorPort();
//		
//		WebServiceClientProxy client = WebServiceFactory.getFlassService(protocol, monitorName, monitorPort,connectTimeout, requestionTimeout);
//		return client;
//	}
	
	public boolean isFailoverJobFinishOfProductServer(String afGuid) {
		logger.debug("isFailoverJobFinishOfProductServer(String) - start"); //$NON-NLS-1$
		
		String localAFGuid = evaluteAFGuid(afGuid);
		
		WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(localAFGuid);
		boolean failoverJobFinish = client.getServiceV2().isFailoverJobFinish(localAFGuid);
		logger.debug("isFailoverJobFinishOfProductServer(String) - end"); //$NON-NLS-1$
		return failoverJobFinish;
	}

	public int heartBeat(String uuid, double perc, int toRep, boolean isFailOverVM) {
		int result = 1;
		   {
			   boolean found = false;
			   FailoverJobScript jobScript = null;
				 synchronized(JobQueueFactory.getDefaultJobQueue()){
					Collection<AFJob> jobs = JobQueueFactory.getDefaultJobQueue()
							.findByJobType(JobType.Failover);
					for (AFJob job : jobs) {
						FailoverJob failoverJob = (FailoverJob) job;
						if (uuid.equals(failoverJob.getJobScript().getAFGuid())) {
							found = true;
							if(logger.isDebugEnabled())
								logger.debug("failover job unscheduled because of heartBeat siginal from  " +uuid);
							
							if (!isFailOverVM)
								failoverJob.setTimeout(false);
							failoverJob.unschedule();
							jobScript = failoverJob.getJobScript();
							if(FailoverJobScript.PAUSED!= jobScript.getState() && !isFailOverVM){
								result = 0;
								failoverJob.schedule();
							}else{
								result  = 2;
							}
							
							/*if(!failoverJob.getJobScript().isAutoFailover()){
								//IF auto failover is disabled.Failover can only be triggered manually.
								failoverJob.unschedule();
							}*/
							break;
						}
					}
				  }
				 
			 if(found){
				HeartBeatModelManager.updateHeartBeat(uuid,perc,toRep, isFailOverVM);
				try {
					ReplicationService.startHATransServerDirectly();
				} catch (Exception e) {
					logger.error("Fails to start HATransServer.exe process", e);
				}
			 }
		}
		return result;
	
	}

	public String getHATranServerPort(String afGuid){
		String localAFGuid = evaluteAFGuid(afGuid);

		WebServiceClientProxy clientProxy = MonitorWebClientManager.getMonitorWebClientProxy(localAFGuid);
		int port = clientProxy.getServiceV2().getHATransPort();
		return port + "";
	}
	
	public FileVersion GetMonitorVDDKVersion(String afGuid){
		String localAFGuid = evaluteAFGuid(afGuid);
		WebServiceClientProxy clientProxy = MonitorWebClientManager.getMonitorWebClientProxy(localAFGuid);
		if(clientProxy == null){
			logger.info("The monitor poxy is null");
			return new FileVersion();
		}
		
		try {
			return clientProxy.getServiceV2().GetVDDKVersion();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new FileVersion();
		}
	}
	
	public DiskInfo GetMonitorDiskFreeSize(String afGuid,String diskName){
		String localAFGuid = evaluteAFGuid(afGuid);
		WebServiceClientProxy clientProxy = MonitorWebClientManager.getMonitorWebClientProxy(localAFGuid);
		if(clientProxy == null){
			logger.info("The monitor proxy is null");
			return null;
		}
		
		try {
			return clientProxy.getServiceV2().GetDiskFreeSize(diskName);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	public int configBootableSessionByMonitor(String afGuid,VMSnapshotsInfo vmSnapshotsInfo, long jobID){
		
		String localAFGuid = evaluteAFGuid(afGuid);
		
		int result = 0;
		WebServiceClientProxy clientProxy = MonitorWebClientManager.getMonitorWebClientProxy(localAFGuid);
		if(clientProxy==null){
			logger.info("The monitor proxy is null");
			return 1;
		}
		
		try {
			result = clientProxy.getServiceV2().configBootableSessionWithJobID(localAFGuid, vmSnapshotsInfo, jobID);
		} catch (Exception e) {
			logger.error("Failed to configBootableSessionByMonitor:"+e.getMessage());
			if (e instanceof ConnectException || e instanceof WebServiceException || e instanceof SocketTimeoutException) {
				logger.error("Fail to connect to monitor host. " + e.getMessage());
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_FAIL_TO_CONNECT_HOST, 
						getHeartBeatJobScript(localAFGuid).getHeartBeatMonitorHostName(), e.getMessage());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, afGuid);
			} else {
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_RET_MSG, e.getMessage());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, afGuid);
			}
			result = 1;
		}
		
		return result;
	}
	
	
	public int configBootableSession(String afGuid,VMSnapshotsInfo vmSnapshotsInfo, long jobID){
		int result=0;
		
		String localAFGuid = evaluteAFGuid(afGuid);
		
		logger.info("ConfigBootableSession begin.");
		FailoverJobScript failoverJobScript=HACommon.getFailoverJobScriptObject(localAFGuid);
		if(failoverJobScript==null){
			logger.error("The failover jobscript is null");
			return -2;  // -2 means failover jobscript not found
		}
		try {
			if(failoverJobScript.getVirtualType()==VirtualizationType.VMwareESX){
				VMwareESXFailoverCommand vMwareESXFailoverCommand=new VMwareESXFailoverCommand();
				//vMwareESXFailoverCommand.setBootableSessionParameter(false, vmSnapshotsInfo);
				//result = vMwareESXFailoverCommand.executeFailover("", failoverJobScript, "");
				result = vMwareESXFailoverCommand.configureBootableSession("", failoverJobScript, vmSnapshotsInfo, jobID);
			}
			else if(failoverJobScript.getVirtualType()==VirtualizationType.VMwareVirtualCenter){
				VMwareCenterServerFailoverCommand vMwareCenterServerFailoverCommand=new VMwareCenterServerFailoverCommand();
				//vMwareCenterServerFailoverCommand.setBootableSessionParameter(false, vmSnapshotsInfo);
				//result = vMwareCenterServerFailoverCommand.executeFailover("", failoverJobScript, "");
				result = vMwareCenterServerFailoverCommand.configureBootableSession("", failoverJobScript, vmSnapshotsInfo, jobID);
			}
			else if(failoverJobScript.getVirtualType() == VirtualizationType.HyperV){
				HyperVFailoverCommand hyperVFailoverCommand=new HyperVFailoverCommand();
				//hyperVFailoverCommand.setBootableSessionParameter(false, vmSnapshotsInfo);
				//result = hyperVFailoverCommand.executeFailover("", failoverJobScript, vmSnapshotsInfo.getSessionGuid());
				result = hyperVFailoverCommand.configureBootableSession("", failoverJobScript, vmSnapshotsInfo, jobID);
			}

			if(result!=0){
				logger.error("Failed to configure the bootable session:"+vmSnapshotsInfo.getSessionName());
			}
			
			logger.info("ConfigBootableSession end.");
			return result;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			String msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_RET_MSG, e.getMessage());
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, localAFGuid);
			return -1;
		}
		
	}
	public int deleteHyperVVMSnapshotByMonitor(String afGuid,String sessionGuid, long jobID){
		String localAFGuid = evaluteAFGuid(afGuid);
		int result = 0;
		WebServiceClientProxy clientProxy = MonitorWebClientManager.getMonitorWebClientProxy(localAFGuid);
		if(clientProxy == null){
			logger.info("The monitor proxy is null");
			return 1;
		}
		
		try {
			
			String rootPath = getRepDestination(localAFGuid);

			result = clientProxy.getServiceV2().deleteHyperVVMSnapshot(rootPath,localAFGuid,sessionGuid);
		} catch (Exception e) {
			logger.error("Failed to deleteHyperVVMSnapshotByMonitor:"+e.getMessage());
			if (e instanceof ConnectException || e instanceof WebServiceException || e instanceof SocketTimeoutException) {
				logger.error("Fail to connect to monitor host. " + e.getMessage());
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_FAIL_TO_CONNECT_HOST, 
						getHeartBeatJobScript(localAFGuid).getHeartBeatMonitorHostName(), e.getMessage());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, afGuid);
				
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_DELETE_HYPERV_SNAPSHOT, sessionGuid);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, afGuid);
			} else {
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_RET_MSG, e.getMessage());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, afGuid);
			}
			result = 1;
		}
		return result;
	}
	
	public int deleteHyperVVMSnapshot(String lastRepDest,String afGuid,String sessionGuid){
		int result = 0;
		long handle = 0;
		
		// VM should be created already
		logger.info("rootPath=" + lastRepDest);
		VMInfomationModelManager manager = HACommon.getSnapshotModeManager(lastRepDest, afGuid);
		VirtualMachineInfo vmInfo = manager.getInternalVMInfo(afGuid);
		if (vmInfo == null || vmInfo.getType() != 0) {
			logger.error("The vmInfo is null and the search afguid: "+afGuid);
			return 1;

		}
		
		VMSnapshotsInfo vmSnapshotsInfo = manager.getSnapshot(afGuid, sessionGuid);
		if(vmSnapshotsInfo==null){
			logger.error("Failed to find the VMSnapshotInfo with afguid:"+afGuid +" sessionGUID:"+sessionGuid);
			return 1;
		}
		
		try {
			
			handle = HyperVJNI.OpenHypervHandle("", "", "");
			
			HyperVJNI.RevertToVmSnapshot(handle,vmInfo.getVmGUID(), vmSnapshotsInfo.getSnapGuid());
			HyperVJNI.DeleteVmSnapshot(handle, vmInfo.getVmGUID(), vmSnapshotsInfo.getSnapGuid());
			
			SortedSet<VMSnapshotsInfo> vmSnapshotsSet = manager.getSnapshots(afGuid);
			vmSnapshotsSet.remove(vmSnapshotsInfo);
			manager.replaceSnapshots(vmInfo, vmSnapshotsSet);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			result = 1;
		}finally{
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (Exception e2) {
				logger.error("Failed to close HyperV handle:"+e2.getMessage());
			}
			
		}
		
		return result;
	}
	
	public	int DeleteVmSnapshotAsync(String strVMGuid, String strSnapGuid){
		int result = 0;
		long handle = 0;
		try {
			handle = HyperVJNI.OpenHypervHandle("", "", "");
			HyperVJNI.DeleteVmSnapshotAsync(handle, strVMGuid, strSnapGuid);
		} catch (Exception e) {
			logger.error(e.getMessage());
			result = 1;
		}finally{
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (Exception e2) {
				logger.error("Failed to close HyperV handle:"+e2.getMessage());
			}
			
		}
		
		return result;
	}
	
	public int createHyperVBootableSnapshotByMonitor(String afGuid,String sessionGuid, String bootableSnapshotName, long jobID){
		
		int result = 0;
		WebServiceClientProxy clientProxy = MonitorWebClientManager.getMonitorWebClientProxy(afGuid);
		if(clientProxy==null){
			logger.info("The monitor proxy is null");
			return 1;
		}
		
		try {

			String repPath = getRepDestination(afGuid);

			result = clientProxy.getServiceV2().createHyperVBootableSnapshotWithJobID(repPath,afGuid, sessionGuid, bootableSnapshotName, jobID);
		} catch (Exception e) {
			logger.error("Fail to createHyperVBootableSnapshotByMonitor " + e.getMessage());
			if (e instanceof ConnectException || e instanceof WebServiceException || e instanceof SocketTimeoutException) {
				logger.error("Fail to connect to monitor host. " + e.getMessage());
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_FAIL_TO_CONNECT_HOST, 
						getHeartBeatJobScript(afGuid).getHeartBeatMonitorHostName(), e.getMessage());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, afGuid);
			} else {
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_RET_MSG, e.getMessage());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, afGuid);
			}
			result = 1;
		}
		
		return result;
		
	}
	
	public int createHyperVBootableSnapshot(String lastRepDest,String afGuid,String sessionGuid, String bootableSnapshotName, long jobID){
		String bootableSnapshotGuid=null;
		long handle=0;
		int result=0;
		
		logger.info("Entry HyperV createBootableSnapshot");
		
		// VM should be created already
		logger.info("rootPath=" + lastRepDest);
		
		VMInfomationModelManager manager = HACommon.getSnapshotModeManager(lastRepDest, afGuid);
		VirtualMachineInfo vmInfo = manager.getInternalVMInfo(afGuid);
		if (vmInfo == null || vmInfo.getType() != 0) {
			logger.error("The vmInfo is null and the search afguid: "+afGuid);
			return 1;

		}
		
		VMSnapshotsInfo preBootableSnapshot = manager.getSnapshot(afGuid, sessionGuid);
		if(preBootableSnapshot==null){
			logger.error("Failed to find the VMSnapshotInfo with afguid:"+afGuid +" sessionGUID:"+sessionGuid);
			return 1;
		}
		
		
		try {
			
			result = HAService.getInstance().configBootableSession(afGuid, preBootableSnapshot, jobID);
			if(result!=0){
				logger.error("Failed to configure the bootable session");
				return 1;
			}
			logger.info("Begin to take the bootable snapshot");
			
			handle= HyperVJNI.OpenHypervHandle("", "", "");
			
			bootableSnapshotGuid= HyperVJNI.TakeVmSnapshot(handle, vmInfo.getVmGUID(),bootableSnapshotName , bootableSnapshotName);
			
			logger.info("Create the bootable snaphot:"+bootableSnapshotName+" "+bootableSnapshotGuid);
			
			preBootableSnapshot.setBootableSnapGuid(bootableSnapshotGuid);
			
			manager.putSnapShot(vmInfo, preBootableSnapshot);
			
		} catch (HyperVException  e) {
			
			FailoverJobScript failoverScript = HACommon.getFailoverJobScriptObject(afGuid);
			String msg = FailoverMessage.getResource(
						FailoverMessage.FAILOVER_PROCESS_VMI_FAIL_TAKE_SNAPSHOT,
						bootableSnapshotName,
						failoverScript.getProductionServerName(),HACommon.getRealHostName(),
						vmInfo.getVmName());
				
			logger.error("wmi return message: " + HyperVJNI.GetLastErrorMessage());
				
			msg += FailoverMessage.getResource(FailoverMessage.FAILOVER_Process_HYPERV_Returne_MESSAGE)
						+ HyperVJNI.GetLastErrorMessage();
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, afGuid);
				
			
		}catch (Exception e) {
			logger.error("Failed to create bootable snaphot:"+e.getMessage(),e);
			
			result = 1;
			
		}finally{
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (HyperVException hyperVe) {
				logger.debug(hyperVe.getMessage());
			}
		}
		
		return result;
	}

	public boolean isMoniteeIsHyperVRole(String currentAfguid){
		if(!HACommon.isTargetPhysicalMachine(currentAfguid)){
			//The source machine is VM, don't check the HyperV Role
			return false;
		}
		return isHyperVRoleInstalled();
	}
	
	public boolean isHyperVRoleInstalled(){
		try {
			return WSJNI.IsHyperVRoleInstalled();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		
	}
	
	public int getHyperVVmAllSnapshotsCount(String vmGuid){
		long handle = 0;
		try {
			handle = HyperVJNI.OpenHypervHandle("", "", "");
			Map<String, String> snapShots = HyperVJNI.GetVmSnapshots(handle, vmGuid);
			return snapShots.size();
		} catch (Exception e) {
			logger.error("Failed to get the snapshots:"+e.getMessage());
			return -1;
		}finally{
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (Exception e2) {
				logger.info(e2.getMessage());
				return -1;
			}
		}
	}
	
	public ARCFlashNodesSummary getARCFlashNodesSummary() {
		ARCFlashNodesSummary result = new ARCFlashNodesSummary();
		result.setServerTime((new Date()).getTime());
		
		HeartBeatModel heartBeatModel = HeartBeatModelManager.getHeartBeatModel();
		if(heartBeatModel == null) 
			throw AxisFault.fromAxisFault("Null heartbeat",MonitorWebServiceErrorCode.Common_NULL_HeartBeat);
		
		List<ARCFlashNode> list = heartBeatModel.getMonitoredARCFlashNodes();		
		
		JobScriptCombo jobSc = getJobScriptComboObject(null);
		if(list.size() > 0 && jobSc != null 
				&& jobSc.getHbJobScript() != null && jobSc.getRepJobScript() != null 
				&& jobSc.getFailoverJobScript() != null) {
			TrustedHost host = CommonService.getInstance().getLocalHostAsTrust();
			ARCFlashNode node = new ARCFlashNode();
			node.setUuid(host.getUuid());
			node.setHostname(host.getName());
			node.setHostport(host.getPort()+"");
			node.setHostProtocol(host.getProtocol());
			node.setMonitor(true);
			list.add(node);
		}
		
		ARCFlashNode[] resulta = list.toArray(new ARCFlashNode[0]);
		result.setNodes(resulta);
		
		return result;
	}
	
	public boolean isVMWareVMNameExist(String host, String username, String password,
			 String protocol, boolean ignoreCertAuthentidation, long viPort,VWWareESXNode vmWareEsxNode, String vmName){
		CAVirtualInfrastructureManager vmwareManager = null;
		try {
			logger.info(String.format("CAVMwareInfrastructureManagerFactory host %s, username %s, password %s, portocol %s, viPort %d", 
							host,username,password, protocol, viPort));
			vmwareManager =  CAVMwareInfrastructureManagerFactory.
				getCAVMwareVirtualInfrastructureManager(host,username,password, protocol, true, viPort);
			
			return isVMWareVMNameExist(vmwareManager,vmWareEsxNode,vmName);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return false;
		}
		finally {
			if(vmwareManager != null) {
				try {
					vmwareManager.close();
				}
				catch(Exception e) {
				}
			}
		}
	}
	
	
	public boolean isVMWareVMNameExist(CAVirtualInfrastructureManager vmwareManager,VWWareESXNode vmWareEsxNode, String vmName){
		boolean isFound = false;
		try 
		{			
			String dataCenter = vmWareEsxNode.getDataCenter();
			if (dataCenter == null)
			{
				ArrayList<ESXNode> nodeList = vmwareManager.getESXNodeList();
				if (nodeList == null)
					return false;
				
				for (ESXNode node : nodeList)
				{
					logger.info("isVMWareVMNameExist checking esx " + vmWareEsxNode.getEsxName());
					if (node.getEsxName().compareToIgnoreCase(vmWareEsxNode.getEsxName()) == 0)
					{
						dataCenter = node.getDataCenter();
						logger.info("isVMWareVMNameExist get data center esx " + dataCenter );						
					}
				}
			}
			
			if (dataCenter == null)
				return false;
			
			ESXNode esxNode = new ESXNode();
			logger.info("getEsxName " + vmWareEsxNode.getEsxName() + " getDataCenter " + vmWareEsxNode.getDataCenter());
			esxNode.setEsxName(vmWareEsxNode.getEsxName());
			esxNode.setDataCenter(dataCenter);
			
			return isVMWareVMNameExist(vmwareManager, esxNode, vmName, null, null);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return isFound;
		}
		
	}
	
	
	public boolean isVMWareVMNameExist(CAVirtualInfrastructureManager vmwareOBJ,ESXNode esxNode,String vmName, StringBuilder vmUUID, StringBuilder sameVMEsx){
		try {
			logger.info("isVMWareVMNameExist entered, vmName " + vmName + " vmUUID " + vmUUID);
			List<ESXNode> esxNodeList = vmwareOBJ.getESXNodeList();
			for (ESXNode e : esxNodeList) {
				if (e.getDataCenter().equalsIgnoreCase(esxNode.getDataCenter())) {
					ArrayList<VM_Info> vmList = vmwareOBJ.getVMNames(e, false);
					for (VM_Info vmInfo : vmList) {
						logger.info("isVMWareVMNameExist checking " + vmInfo.getVMName());
						if(vmInfo.getVMName().compareToIgnoreCase(vmName)==0){
							if (sameVMEsx != null)
								sameVMEsx.append(vmInfo.getvmEsxHost());
							if (vmUUID != null)
								vmUUID.append(vmInfo.getVMvmInstanceUUID());
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return false;
	}
	
	public VM_Info isVMWareVMNameExist(CAVirtualInfrastructureManager vmwareOBJ,String esxName,String dataCenter,String vmName)throws Exception{
		
		//esxNode.setEsxName(vmWareEsxNode.getEsxName());
		//esxNode.setDataCenter(vmWareEsxNode.getDataCenter());
			
		ESXNode esxNode = new ESXNode();
		esxNode.setEsxName(esxName);
		esxNode.setDataCenter(dataCenter);
			
		ArrayList<VM_Info> vmList = vmwareOBJ.getVMNames(esxNode, false);
		for (VM_Info vmInfo : vmList) {
			if(vmInfo.getVMName().equalsIgnoreCase(vmName)){
				vmInfo.setvmEsxHost(esxName);
				return vmInfo;
			}
		}
			
		return null;
		
	}
	
	public VM_Info getVMFromVC(CAVirtualInfrastructureManager vmwareOBJ,String vmName) throws Exception{
		
		ArrayList<ESXNode> nodes = vmwareOBJ.getESXNodeList();
		
		VM_Info result = null;
		
		for (ESXNode node : nodes) {
			try {
				result = isVMWareVMNameExist(vmwareOBJ,node.getEsxName(),node.getDataCenter(),vmName);
				if(result != null){
					result.setvmEsxHost(node.getEsxName());
					break;
				}
			} catch (Exception e) {
			}
		}
			
		return result;
		
	}
	

	public String isHyperVVMNameExist(String vmName){
		return findHyperVVM(vmName);
	}
	
	public String findHyperVVM(String vmname){
		
		long handle = 0;
		String vmGuid = null;
		try {
			
			handle = HyperVJNI.OpenHypervHandle("", "", "");
			Map<String, String> vmList = HyperVJNI.GetVmList(handle);
			
			for (Entry<String, String> vmEntry : vmList.entrySet()){
				if(vmEntry.getValue().equals(vmname)){
					vmGuid = vmEntry.getKey();
					break;
				}
			}
			
		} catch (Exception e) {
			logger.error("Failed to get the vmList:"+e.getMessage());
			return vmGuid;
		}finally{
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (Exception e2) {
				logger.info(e2.getMessage());
			}
		}
		
		return vmGuid;
		
	}
	
	public List<String> getIpAddressFromDns(String hostName){
		return getNativeFacade().getIpAddressFromDns(hostName);
	}
	
	public boolean isBootOrSystemVolumeOnDynamicDisk() throws ServiceException{
		boolean res = false;
		Vector<Volume> volumes = null;
		try{
			volumes = WSJNI.GetOnlineVolumes();
		}catch (Exception e) {
			logger.error("Fail to call WSJNI.GetOnlineVolumes");
			throw new ServiceException("Fail to call WSJNI.GetOnlineVolumes",new Object[0]);
		}
		if( volumes.size() == 0 ){
			throw new ServiceException("Fail to get online volumes",new Object[0]);
		}
		for (Volume vol : volumes) {
			if(vol.isDynamic()){
				int flag = vol.getFlag();
				if( (flag & (Volume.VOLUME_FLAG_BOOT_VOLUME|Volume.VOLUME_FLAG_SYSTEM_VOLUME)) != 0 ){
					res = true;
					break;
				}
					
			}
		}
		return res;
	}
	
	private String getRepDestination(String afGuid){
		
		ReplicationJobScript replicationJobScript = HAService.getInstance().getReplicationJobScript(afGuid);
		ARCFlashStorage arcflash = (ARCFlashStorage)replicationJobScript.getReplicationDestination().get(0);
		RepositoryUtil repository = RepositoryUtil.getInstance(CommonUtil.getRepositoryConfPath());
		String lastRepDest = null;
		try {
			lastRepDest = repository.getProductionServerRoot(afGuid).getReplicaRoot().getLastRepDest();
		} catch (Exception e1) {
		}
		if(!StringUtil.isEmptyOrNull(lastRepDest)){
			return lastRepDest;
		}
		
		String standbyVM = arcflash.getVirtualMachineDisplayName();
		String repSubRoot = null;
		try {
			repSubRoot = repository.getProductionServerRoot(afGuid).getReplicaRoot().getRepSubRoot();
		} catch (Exception e1) {
		}
		if(StringUtil.isEmptyOrNull(repSubRoot)){
			repSubRoot = standbyVM + "\\sub0001";
		}
		
		repSubRoot += "\\";
		
		String destFolder = arcflash.getDiskDestinations().get(0).getStorage().getName();
		
		if(!destFolder.endsWith("\\")){
			destFolder += "\\";
		}
		
		if(!destFolder.endsWith(repSubRoot)){

			destFolder += repSubRoot;
		}
		
		if(!destFolder.endsWith("\\")){
			destFolder += "\\";
		}
		
		String productionServerName = HACommon.getProductionServerNameByAFRepJobScript(replicationJobScript);
		destFolder += productionServerName;
		
		logger.info("destFolder: " + destFolder);
		
		return destFolder;
	}
	
	
	/**Force the next replication job to merge all its session to one.
	 * This function only effect the next replication job, and will not effect after web service reboot.
	 * @param merge, If true, force to merge. If false, not force to merge
	 */
	public void forceNextReplicationMergeAllSessions(String afGuid,boolean merge){
		String localAFGuid = evaluteAFGuid(afGuid);
		synchronized (forceSmartCopy) {
			if(merge){
				forceSmartCopy.put(localAFGuid, SMART_COPY);
			}else{
				forceSmartCopy.remove(localAFGuid);
			}
		}
	}
	
	/**
	 * @return whether the next replication job will force all its sessions to be merged to one.
	 */
	public boolean isNextReplicationForcedToMergeAllSessions(String afGuid){
		String localAFGuid = evaluteAFGuid(afGuid);
		synchronized (forceSmartCopy) {
			if(SMART_COPY.equals(forceSmartCopy.get(localAFGuid))){
				return true;
			}else{
				return false;
			}
		}
	}
	
	
	/** This function gets the count of sessions that has not been replicated.
	 * 
	 * @return the count of sessions to be replicated
	 * @throws HAException
	 */
	public SessionInfo[] getReplicationQueueSize(String afGuid) throws HAException{
		// fix 154635 ,fanda03 conversion job failed because snapshot is delete by other thread
		// Fix defect 55008, when the conversion job is running, the remote connection shouldn't be closed
		String localAFGuid = evaluteAFGuid(afGuid);	
		ReplicationJobScript replicationJobScript = getReplicationJobScript(localAFGuid);
		if (replicationJobScript == null) {
			logger.error("There's no VSB job script configured for node " + localAFGuid);
			return null;
		}
		BaseReplicationCommand replicationCommand = (BaseReplicationCommand)replicationJobScript.getReplicationCommand();
		SessionInfo sessions[] = null;
		Lock jobLock = CommonService.getInstance().getRepJobLock(localAFGuid);
		boolean isLocked = false;
		try {
			isLocked = jobLock.tryLock();
			if (isLocked) {				
				BackupDestinationInfo backupDestinationInfo = BaseReplicationCommand.getBackupDestinationInfo(replicationJobScript, false, -1);
				BaseReplicationCommand.connectToRemote(backupDestinationInfo,localAFGuid,-1);
				sessions = replicationCommand.getNextSessionsToReplicate(replicationJobScript, true, null,true, null,backupDestinationInfo);
				BaseReplicationCommand.closeRemoteConnect(backupDestinationInfo);
			}
		} 
		catch (Exception e) {
			logger.error("some excepion thrown when process getNextSessionsToReplicate", e);
		} 
		finally {
			try {
				if( isLocked )
					jobLock.unlock();
			}	
			catch(Exception e){	
			}
		}	
		return sessions;
	}

	public int unApplyVCMJobPolicy(String instanceUuid) throws Exception {
		VCMPolicyDeployParameters deployParameters = new VCMPolicyDeployParameters();
		deployParameters.setInstanceUuid( instanceUuid );
		return unApplyVCMJobPolicy( deployParameters );
	}
		
	public int unApplyVCMJobPolicy( VCMPolicyDeployParameters deployPolicyParameters ) throws Exception {
		//logger.info("Enter unApplyVCMJobPolicy. instanceUuid :" + instanceUuid);
		String instanceUuid = deployPolicyParameters.getInstanceUuid();
		String afguid = evaluteAFGuid(instanceUuid);
		
		logger.info("Un-deploy the VSB policy for " + afguid);

		//Try to lock the replication job
		Lock jobLock = CommonService.getInstance().getRepJobLock(afguid);
		boolean canRun = jobLock.tryLock();
		if (!canRun) {
			logger.warn("The conversion job is doing now, will cancel it first before un-deploy the VSB policy");
			jobLock = cancelConversionAndWait(afguid);
			if (jobLock == null) {
				ReplicationJobScript repJobScript = getReplicationJobScript(afguid);
				String hostName = repJobScript.getAgentNodeName();
				String msg = WebServiceMessages.getResource(COLDSTANDBY_SETTING_CANCEL_CONVERSION_FAILED, hostName);
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, 
						Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "",
								"", "", "" }, afguid);
				return 1;
			}
		} else
			logger.info("There's no conversion job running now.");
		
		boolean isForRemoteVCM = false;
		boolean isBackupToRPS = false;
		RPSInfo rpsInfo = null;
		try {
			{
				ReplicationJobScript replicationJobScript = getReplicationJobScript(afguid);
				if (replicationJobScript != null) {
					isForRemoteVCM = ManualConversionUtility.isVSBWithoutHASupport(replicationJobScript);
					isBackupToRPS = replicationJobScript.getBackupToRPS();
					if (isBackupToRPS) {
						rpsInfo = new RPSInfo();
						rpsInfo.setRpsHostName(replicationJobScript.getRpsHostName());
						rpsInfo.setRpsPort(replicationJobScript.getRpsPort());
						rpsInfo.setRpsProtocol(replicationJobScript.getRpsProtocol());
						rpsInfo.setRpsUserName(replicationJobScript.getRpsUserName());
						rpsInfo.setRpsPassword(WSJNI.AFDecryptString(replicationJobScript.getRpsPassword()));
					}
				}
			}
			
			deleteVCMJobScripts(afguid);
		}catch (Throwable e) {
			logger.error(e);
		} 
		finally{
			jobLock.unlock();
		}
		
		if (isBackupToRPS) {
			registerToRPS(afguid, rpsInfo, false);
			
			ActivityLogSyncher.getInstance().removeVM(afguid);
		}
		
		if (isForRemoteVCM)
		{
			if (!isBackupToRPS) {
				logger.info( "The policy is for remote VCM, detach vSphere policy." );
				VirtualMachine vm = new VirtualMachine();
				vm.setVmInstanceUUID( afguid );
				long ret = VSphereService.getInstance().detachVSpherePolicy( new VirtualMachine[] { vm }, false );
				logger.info( "Detach vSphere policy complete. Return value: " + ret );
				
				RemoteVCMSessionMonitor.getInstance().removeNode(afguid);
			}
			
			// clear password from custom password pool
			CustomSessionPasswordManager.clearCustomPassword(afguid);
		}
		
		//logger.info("Leave unApplyVCMJobPolicy. instanceUuid :" + instanceUuid);
		logger.info("Un-deploy the VSB policy for " + afguid + " successfully.");
		return 0;
	}
	
	public int cancelReplicationSync(String afguid) {
		Lock jobLock = CommonService.getInstance().getRepJobLock(afguid);
		if (!jobLock.tryLock()) {
			logger.warn("The conversion job is running now; Cancel it firstly.");
			jobLock = cancelConversionAndWait(afguid);
			if (jobLock == null) {
				ReplicationJobScript repJobScript = getReplicationJobScript(afguid);
				String hostName = repJobScript.getAgentNodeName();
				String msg = WebServiceMessages.getResource(COLDSTANDBY_SETTING_CANCEL_CONVERSION_FAILED, hostName);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING,
						-1, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "", "", "", "" }, afguid);
				return 1;
			}else {
				jobLock.unlock();
			}
		} else {
			logger.info("No conversion job is running. Needn't cancel");
			jobLock.unlock();
		}
		return 0;
	}
	
	public Lock cancelConversionAndWait(String afguid) {
		
		final int CANCEL_CONVERSION_WAIT_MAX_TIMEOUT = 1800;  // seconds

		String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_CANCEL_BEFORE_UNDEPLOY);
		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, 
				new String[] { msg,"", "", "", "" }, afguid);

		try {
			cancelReplication(afguid);
		} catch (Exception e) {
			logger.warn("Failed to cancel the conversion job", e);
			return null;
		}
		
		logger.info("Cancel the conversion job successfully, will wait until the job exits.");

		int i = 0;
		while(i++ < CANCEL_CONVERSION_WAIT_MAX_TIMEOUT) {
			RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(afguid);
			synchronized (jobMonitor) {
				if (jobMonitor.isFinished())
					break;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		};
		
		RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(afguid);
		synchronized (jobMonitor) {
			if (!jobMonitor.isFinished()) {
				logger.warn("The conversion job is not canceled in about 30 minutes.");
				return null;
			}
		}
		logger.info("The conversion job is finished.");
		
		do {
			Lock jobLock = CommonService.getInstance().getRepJobLock(afguid);
			boolean canRun = jobLock.tryLock();
			if (canRun)
				return jobLock;
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		} while (true);
	}
	
	private int deleteVCMJobScripts(String afguid){
		AFJob failoverJob = getFailoverJob(afguid);
		AFJob heatBeatJob = getHeartBeatJob(afguid);

		String convertID = retrieveCurrentNodeID();
		boolean isForRemoteVCM = false;
		boolean isBackupToRPS = false;
		WebServiceClientProxy proxy = null;
		if (failoverJob != null && failoverJob.getJobScript() != null) {
			FailoverJobScript failoverJobScript = ((FailoverJob)failoverJob).getJobScript();
			isForRemoteVCM = ManualConversionUtility.isVSBWithoutHASupport(failoverJobScript);
			isBackupToRPS = failoverJobScript.getBackupToRPS();

			if (isBackupToRPS && !isForRemoteVCM) {
				try {
					proxy = getD2DService(failoverJobScript);
				} catch (Exception e) {
					logger.warn("Failed to get D2D service", e);
				}
			}
		}
		
		//Step 1: remove the Replication info from monitor
		try {
			if(heatBeatJob != null && failoverJob != null) {
				WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy((HeartBeatJobScript)heatBeatJob.getJobScript());		
				client.getServiceV2().removeReplicatedInfo(afguid, failoverJob.getJobScript().getVirtualType(), convertID);
			}
		}catch(Exception e) {
			logger.warn("Failed to remove the Replication info from monitor", e);
		}
		//fanda03 fix 164911; remove monitee information.
		try {
			if(failoverJob != null)
				this.removeReplicatedInfo(afguid, failoverJob.getJobScript().getVirtualType(), convertID);
		}
		catch(Exception e) {
			logger.warn("Failed to remove the Replication info from monitee", e);
		}

		//step 2: stop heat beat
		try {
			if (!isForRemoteVCM) {
				if (isBackupToRPS && proxy != null){
					logger.info("BackupToRPS: Stop HeartBeat on the monitee.");
					proxy.getServiceV2().stopHeartBeatThis(afguid, convertID);
				}
				else
					stopHeartBeat(afguid, convertID);
			} else {
				logger.info("For remote VSB, try remove failover job script from monitor");
				WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy((HeartBeatJobScript)heatBeatJob.getJobScript());		
				client.getServiceV2().deregisterForHA(afguid, convertID);
			}
		} catch (Exception e) {
			logger.warn("Failed to stop heat beat", e);
		}

		//Step 3: delete the job script
		synchronized (jobQueue) {
			AFJob failoverJob2 = getFailoverJob(afguid);
			if(failoverJob2!=null) {
				FailoverJobScript jobScript2 = (FailoverJobScript) failoverJob2.getJobScript();
				if (convertID != null && jobScript2.getConverterID() != null && !convertID.equalsIgnoreCase(jobScript2.getConverterID())) {
					logger.warn("The input converterID and the converterID in failover jobscript are different, so skip remove it.");
				} else 
					jobQueue.remove(failoverJob2);
			}
			AFJob heatBeatJob2 = getHeartBeatJob(afguid);
			if(heatBeatJob2!=null) {
				HeartBeatJobScript jobScript2 = (HeartBeatJobScript) heatBeatJob2.getJobScript();
				if (convertID != null && jobScript2.getConverterID() != null && !convertID.equalsIgnoreCase(jobScript2.getConverterID())) {
					logger.warn("The input converterID and the converterID in heartbeat jobscript are different, so skip remove it.");
				} else 
					jobQueue.remove(heatBeatJob2);
			}
			AFJob replicationJob2 = getReplicationJob(afguid);
			if(replicationJob2!=null) {
				ReplicationJobScript jobScript2 = (ReplicationJobScript) replicationJob2.getJobScript();
				if (convertID != null && jobScript2.getConverterID() != null && !convertID.equalsIgnoreCase(jobScript2.getConverterID())) {
					logger.warn("The input converterID and the converterID in replication jobscript are different, so skip remove it.");
				} else 
					jobQueue.remove(replicationJob2);
			}
			AFJob alertJob2 = getAlertJob(afguid);
			if(alertJob2!=null) {
				AlertJobScript jobScript2 = (AlertJobScript) alertJob2.getJobScript();
				if (convertID != null && jobScript2.getConverterID() != null && !convertID.equalsIgnoreCase(jobScript2.getConverterID())) {
					logger.warn("The input converterID and the converterID in alert jobscript are different, so skip remove it.");
				} else 
					jobQueue.remove(alertJob2);
			}
		}
		try {
			if (isBackupToRPS && proxy != null){
				logger.info("BackupToRPS: delete HeartBeat script on the monitee.");
				proxy.getServiceV2().deregisterHeartBeatForHA(afguid, convertID);
			}
		} catch (Exception e) {
			logger.warn("Failed to delete HeartBeat script on the monitee", e);
		}

		return 0;
	}
	
	public VCMSavePolicyWarning[] applyVCMJobPolicy(String jobScriptComboStr, String instanceUuid) throws Exception{
		VCMPolicyDeployParameters deployPolicyParameters = new VCMPolicyDeployParameters();
		deployPolicyParameters.setInstanceUuid( instanceUuid );
		return this.applyVCMJobPolicy( jobScriptComboStr, deployPolicyParameters, false, true );
	}
	
	public String ensureLocalPath( String path )
	{
		if (!path.startsWith( "\\\\" ))
			return path;
		
		String localPath = "";
		
		int shareNameStart = path.indexOf( "\\", 2 ) + 1;
		int shareNameEnd = path.indexOf( "\\", shareNameStart );
		String shareName = path.substring( shareNameStart, shareNameEnd );
		
		List<String> localPathList = new ArrayList<String>();
		List<Integer> errorCodeList = new ArrayList<Integer>();
		int ret = getNativeFacade().GetLocalPathOfShareName( shareName, localPathList, errorCodeList );
		
		if (ret == 0)
		{
			if (localPathList.size() > 0)
				localPath = localPathList.get( 0 );
			else
				logger.error( "GetLocalPathOfShareName() succeeds but there is no local path returned." );
		}
		else if (ret == 1)
		{
			logger.error( "Share name cannot be found. Path: " + path );
			return null;
		}
		else // error
		{
			String errorCodeString = "<no error code>";
			if (errorCodeList.size() > 0)
				errorCodeString = errorCodeList.get( 0 ).toString();
			logger.error( "GetLocalPathOfShareName() encounters error. Path: " +
				path + ", Error code: " + errorCodeString );
			return null;
		}
		
		localPath = localPath + path.substring( shareNameEnd );
		return localPath;
	}
	
	public VCMSavePolicyWarning[] applyVCMJobPolicy(
		String jobScriptComboStr, VCMPolicyDeployParameters deployPolicyParameters, boolean isForRemoteVCM, boolean isPolicyChanged) throws Exception{
		//logger.info("vm instanceUuid=" + instanceUuid);
		//logger.info("physical machine afguid=" + physicalUUID);
		
		logger.info("Deploy the VSB policy for node " + deployPolicyParameters.getInstanceUuid() +  
				", backup to RPS: " + deployPolicyParameters.isRPSNode() + 
				", remote VSB: " + isForRemoteVCM + 
				", policy changed: " + isPolicyChanged);
		
		// Always make deep deployment even there's no VSB policy changed in CPM. 2014-01-18
		isPolicyChanged = true;
		
		if (isForRemoteVCM)
		{
			try
			{
				if (!deployPolicyParameters.isRPSNode()) {
					logger.info( "The policy is for remote VCM, save backup configuration first." );

					String sessionFolderPath = deployPolicyParameters.getSessionFolderPath();
					String vmName = deployPolicyParameters.getHostname();
					
					String normalizedSessionFolderPath = ensureLocalPath( sessionFolderPath );
					if (normalizedSessionFolderPath == null)
					{
						logger.error( "Invalid session folder path. Path: " + sessionFolderPath );
						throw new Exception( "Invalid session folder path." );
					}
					
					VSphereBackupConfiguration backupConfig = new VSphereBackupConfiguration();
					backupConfig.setGenerateType( GenerateType.MSPManualConversion );
					backupConfig.setDestination( normalizedSessionFolderPath );
					backupConfig.setRetentionCount( 31 );
					
					BackupVM backupVM = new BackupVM();
					backupVM.setInstanceUUID( deployPolicyParameters.getInstanceUuid() );
					backupVM.setDestination( normalizedSessionFolderPath );
					backupVM.setVmName( vmName );
					
					backupConfig.setBackupVMList( new BackupVM[] { backupVM } );
					
					RetentionPolicy retentionPolicy = new RetentionPolicy();
					backupConfig.setRetentionPolicy(retentionPolicy);
					
					VSphereService.getInstance().saveVSphereBackupConfiguration( backupConfig );
					
					logger.info( "Saving backup configuration complete." );
					
					RemoteVCMSessionMonitor.getInstance().addNode(
						deployPolicyParameters.getInstanceUuid(), normalizedSessionFolderPath );
				}

				// Fetch password list from parameter object and update session password
				List<String> passwordList = deployPolicyParameters.getSessionPasswordList();
				updateSessionPassword(evaluteAFGuid(deployPolicyParameters.getInstanceUuid()), passwordList);
			}
			catch (Exception e)
			{
				logger.error(e);
				logger.error( "Failed to save backup configuration. UUID: " + deployPolicyParameters.getInstanceUuid() );
				throw new ServiceException( FlashServiceErrorCode.VCM_FAILED_TO_SET_SESSIONFOLDER, new Object[0] );
			}
		}
		
		if (isPolicyChanged)
		{
			JobScriptCombo jobScript = null;
			try {
				jobScript = CommonUtil.unmarshal(jobScriptComboStr, JobScriptCombo.class);
	
			} catch (Exception e) {
				logger.error("Failed to convert the string to object:"+e.getMessage());
				throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_GET_JOBSCRIPT,new Object[0]);
			}
			
			if (isForRemoteVCM)
			{
				int generateType = GenerateType.MSPManualConversion;
				
				jobScript.getRepJobScript().setGenerateType( generateType );
				jobScript.getFailoverJobScript().setGenerateType( generateType );
				jobScript.getHbJobScript().setGenerateType( generateType );
				jobScript.getAlertJobScript().setGenerateType( generateType );
				// set monitor to localhost for remote VCM when virtualType is ESX
				FailoverJobScript failoverJobScript = jobScript.getFailoverJobScript();
				HeartBeatJobScript heartBeatJobScript = jobScript.getHbJobScript();
				heartBeatJobScript.setVirtualType(failoverJobScript.getVirtualType());
				String productionServerURL = CommonUtil.getProductionServerURL();
				if (failoverJobScript.getVirtualType() != VirtualizationType.HyperV) {
					try {
						heartBeatJobScript.setHeartBeatMonitorHostName(InetAddress.getLocalHost().getHostName());
					} catch (UnknownHostException e) {
						logger.warn("Failed to get the hostname, set localhost as hostname.", e);
						heartBeatJobScript.setHeartBeatMonitorHostName("localhost");
					}
					heartBeatJobScript.setHeartBeatMonitorProtocol(CommonUtil
							.getProductionServerProtocol(productionServerURL));
					String strPort = CommonUtil.getProductionServerPort(productionServerURL);
					int port;
					try {
						port = Integer.parseInt(strPort);
					} catch (Exception e) {
						logger.warn("Failed to parse port.", e);
						port = 8014;
					}
					heartBeatJobScript.setHeartBeatMonitorPort(port);
					
					try {
						Account account = getNativeFacade().getAdminAccount();

						heartBeatJobScript.setHeartBeatMonitorUserName(account.getUserName());
						heartBeatJobScript.setHeartBeatMonitorPassword(account.getPassword());
					} catch (ServiceException e) {
						logger.warn("Failed to get Admin Account.", e);
					}
				}

			}
			// Save VM network configuration into failover job script.
			updateIPSettingToFailoverJob(jobScript.getFailoverJobScript(), deployPolicyParameters);

			return applyVCMJobPolicy(jobScript, deployPolicyParameters);
		}
		else // policy is not changed
		{
			String afguid = evaluteAFGuid(deployPolicyParameters.getInstanceUuid());
			FailoverJobScript updateFailoverJobScript = getFailoverJobScript(afguid);
			HeartBeatJobScript heartBeatJobScript = getHeartBeatJobScript(afguid);
			if (updateFailoverJobScript != null && heartBeatJobScript != null) {				
				updateIPSettingToFailoverJob(updateFailoverJobScript, deployPolicyParameters);
				this.setFailoverJobScript(updateFailoverJobScript);
				String failoverScriptXml = CommonUtil.marshal(updateFailoverJobScript);			
				try {
					if (deployPolicyParameters.isRPSNode()){
						logger.info("BackupToRPS: registerForHA to the monitor");
					}

					WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);		
					client.getServiceV2().registerForHA(failoverScriptXml,false);
				} catch (Exception e) {
					if(e instanceof ServiceException) {
						throw (ServiceException)e;
					}
					else {
						String host = "";
						ReplicationJobScript replicationJobScript = getReplicationJobScript(afguid);
						if (replicationJobScript.getBackupToRPS())
							host = replicationJobScript.getRpsHostName();
						else {
							if (replicationJobScript.isVSphereBackup())
								host = updateFailoverJobScript.getVSphereproxyServer().getVSphereProxyName();
							else
								host = replicationJobScript.getAgentNodeName();
						}
						
						String msg = "";
						if (e instanceof SOAPFaultException) {
							msg = HACommon.processWebServiceException((SOAPFaultException)e, new Object[]{heartBeatJobScript.getHeartBeatMonitorHostName()});
						} else {
							msg = e.getMessage();
						}
						throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_CONNECT_MONITOR,new Object[]{host, heartBeatJobScript.getHeartBeatMonitorHostName(), msg});
					}
				}
			}
			return new VCMSavePolicyWarning[0];
		}
	}
	
	public void updateSessionPassword(String afGuid, List<String> passwordList) {
		logger.info("Update session password for node:" + afGuid);
		if (passwordList != null) {
			logger.info("The password count is:" + passwordList.size());
		}

		CustomSessionPasswordManager.clearCustomPassword(afGuid);
		if (passwordList != null && passwordList.size() > 0) {
			for (String password : passwordList) {
				CustomSessionPasswordManager.addCustomPassword(afGuid, password);
			}
		}
	}
	
	public void updateVCMIPSettings(VCMPolicyDeployParameters deployPolicyParameters) {
		logger.info("Update network settings for node:" + deployPolicyParameters.getInstanceUuid());

		saveVCMIPSettingsInFailoverJobScript(deployPolicyParameters);

		logger.info("Update network settings on the monitor.");
		try {
			HeartBeatJobScript heartBeatJobScript = getHeartBeatJobScript(deployPolicyParameters.getInstanceUuid());
			WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);
			
			client.getServiceV2().saveVCMIPSettingsForHA(deployPolicyParameters);
		} catch (Exception e) {
			logger.error("Failed to update network settings on the monitor.");
		}
	}
	public void saveVCMIPSettingsInFailoverJobScript(VCMPolicyDeployParameters deployPolicyParameters) {
		synchronized (jobQueue) {
			FailoverJobScript jobScript = getFailoverJobScript(deployPolicyParameters.getInstanceUuid());
			if (jobScript == null)
				return;
			
			updateIPSettingToFailoverJob(jobScript, deployPolicyParameters);
			
			setFailoverJobScript(jobScript);
		}
	}
	
	private void updateIPSettingToFailoverJob(FailoverJobScript failoverJobScript, VCMPolicyDeployParameters deployPolicyParameters) {
		if (failoverJobScript.getFailoverMechanism()!=null&&failoverJobScript.getFailoverMechanism().size()>0) {
			Virtualization virtualization = failoverJobScript.getFailoverMechanism().get(0);
			if (virtualization!=null) {
				switch (virtualization.getVirtualizationType()) {
				case HyperV:
					virtualization = (HyperV)virtualization;
					break;
				case VMwareESX:
					virtualization = (VMwareESX)virtualization;
					break;
				case VMwareVirtualCenter:
					virtualization = (VMwareVirtualCenter)virtualization;
					break;
				}
				List<NetworkAdapter> networkAdapters = (List<NetworkAdapter>) virtualization.getNetworkAdapters();
				if (networkAdapters!=null&&networkAdapters.size()>0&&deployPolicyParameters.getIpSettings()!=null&&deployPolicyParameters.getIpSettings().size()>0) {
					failoverJobScript.setIPSettingsFromVCM(true);
					networkAdapters.clear();
					for (int i=0;i<deployPolicyParameters.getIpSettings().size();i++) {
						List<IPSetting> ipSettingList = new ArrayList<IPSetting>();
						IPSetting ipSetting = deployPolicyParameters.getIpSettings().get(i);
						ipSettingList.add(ipSetting);
						NetworkAdapter adapter;
						if (VirtualizationType.HyperV == virtualization.getVirtualizationType()) {								
							adapter = new HyperVNetworkAdapter();
						} else {
							adapter = new VMwareNetworkAdapter();
						}
						adapter.setAdapterName("Adapter" + (i+1));
						adapter.setNetworkLabel(ipSetting.getVirtualNetwork());
						adapter.setAdapterType(ipSetting.getNicType());
						adapter.setIpSettings(ipSettingList);
						adapter.setMACAddress("MacAddress" + (i+1)); // Same as VirtualMachinePanel - MacAddress
						networkAdapters.add(adapter);
					}
				}
			}
		}
		failoverJobScript.setDnsParameters(deployPolicyParameters.getDnsParameters()); 
	}
	
	private boolean registerToRPS(String afguid,
			RPSInfo rpsInfo, boolean addOrRemove) {

		//
		RegisterNodeInfo nodeInfo = new RegisterNodeInfo();
		nodeInfo.setNodeName(CommonService.getInstance().getLocalHostAsTrust()
				.getName());
		nodeInfo.setPort(new Long(CommonService.getInstance().getServerPort()));
		nodeInfo.setProtocol(CommonService.getInstance().getServerProtocol());
		nodeInfo.setLoginUUID(retrieveCurrentAuthUUID(false));
		nodeInfo.setClientUUID(retrieveCurrentNodeID());
		nodeInfo.setVmInstanceUUID(null);

		boolean ret = false;
		String protocol = rpsInfo.getRpsProtocol() == 1 ? "http:" : "https:";
		try {
			IRPSService4D2D client = RPSServiceProxyManager.getRPSServiceClient(
					rpsInfo.getRpsHostName(), rpsInfo.getRpsUserName(),
					rpsInfo.getRpsPassword(), rpsInfo.getRpsPort(), protocol, "");
			if (client != null)
				ret = client.addConverterToRPS(nodeInfo, afguid, addOrRemove);
			else
				logger.error(String
					.format("fail to get rps client on server %s, port %d, protocol %s, user %s.",
							rpsInfo.getRpsHostName(), rpsInfo.getRpsPort(),
							protocol, rpsInfo.getRpsUserName()));
		} catch (Exception e) {
			logger.error(String.format(
					"fail to add converter to RPS. d2d:%s; RPS %s. %s",
					afguid, nodeInfo.getNodeName(), e.getMessage()));
		}
		
		if (!ret)
			return ret;
		
		FlashListenerInfo listener = new FlashListenerInfo();
		listener.setWsdlURL(RPSServiceProxyManager.makeRPSServiceURL(rpsInfo.getRpsHostName(), 
				protocol, rpsInfo.getRpsPort(), RPSWebServiceFactory.wsdl4D2D));
		listener.setType(FlashListenerInfo.ListenerType.RPS);
		listener.setUuid(SettingsService.instance().getRpsServerUUID(rpsInfo.getRpsHostName(), 
				rpsInfo.getRpsUserName(), 
				rpsInfo.getRpsPassword(), protocol, rpsInfo.getRpsPort()));
		listener.setEventType(EventType.ConversionJob);
		
		if (addOrRemove){
			ListenerManager.getInstance().addFlashListenerInstUUID(listener, afguid);
		}else{
			ListenerManager.getInstance().removeFlashListenerInstUUID(listener, afguid);
		}
		
		return ret;
	}
	private long submitRPSConversionJob(String afguid, ReplicationJobScript repJobScript) {

		// RPS credential info and RPS policy UUID
		long ret = 1;
		String protocol = repJobScript.getRpsProtocol() == 1 ? "http:" : "https:";
		try {
			String rpsPwd = WSJNI.AFDecryptString(repJobScript.getRpsPassword());

			IRPSService4D2D client = RPSServiceProxyManager.getRPSServiceClient(
					repJobScript.getRpsHostName(), repJobScript.getRpsUserName(),
					rpsPwd, repJobScript.getRpsPort(), protocol, "");

			logger.info(String
					.format("Submit RPS conversion job on server %s, port %d, protocol %s, user %s.",
							repJobScript.getRpsHostName(), repJobScript.getRpsPort(),
							protocol, repJobScript.getRpsUserName()));

			if (client != null)
				ret = client.RPSSubmitConversion(afguid);
			else
				logger.error("Failed to get RPS service client.");
		} catch (Exception e) {
			logger.error("Failed to get RPS service client. " + e.getMessage());
		}
		
		return ret;
	}
	
	private WebServiceClientProxy validateD2DClient(VCMPolicyDeployParameters deployPolicyParameters, boolean isMSPManualConversion) throws ServiceException{
		WebServiceClientProxy proxy = null;
		if (deployPolicyParameters.isRPSNode() && !isMSPManualConversion) {
			try {
				proxy = getD2DService(deployPolicyParameters);
			} catch (Throwable e) {
				logger.error(e);
				throw new ServiceException(FlashServiceErrorCode.Common_CantConnectHost, new String[]{deployPolicyParameters.getSourceMachineInfo().getHypervisorHostName()});
			}
		}
		return proxy;
	}
	
//	private boolean isVSphereBackup(VCMPolicyDeployParameters deployPolicyParameters) {
//		if (deployPolicyParameters.getSourceMachineInfo() == null 
//				|| deployPolicyParameters.getSourceMachineInfo().getMachineType() == MachineType.PHYSICAL) {
//			return false;
//		}
//		return true;
//	}
	
	private VSphereBackupType getVSphereBackup(VCMPolicyDeployParameters deployPolicyParameters) {
		if (deployPolicyParameters.getSourceMachineInfo() == null)
			return VSphereBackupType.NON_HBBU_BACKUP;
		
		if (deployPolicyParameters.getSourceMachineInfo().getMachineType() == MachineType.PHYSICAL) {
			return VSphereBackupType.NON_HBBU_BACKUP;
		} 
		else if (deployPolicyParameters.getSourceMachineInfo().getMachineType() == MachineType.HYPERV_VM) {
			return VSphereBackupType.HYPERV_HBBU_BACKUP;
		}
		else
			return VSphereBackupType.VMWARE_HBBU_BACKUP;
	}
	
	public boolean hasSufficientPermissionForVSB(CAVirtualInfrastructureManager vmwareOBJ) {
		List<String> privList = new ArrayList<String>();
		privList.add("Global.EnableMethods");
		privList.add("Global.DisableMethods");
		privList.add("Global.Licenses");
		
		try {
			return vmwareOBJ.hasSufficientPermissionEx(privList);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}
	
	public VCMSavePolicyWarning[] applyVCMJobPolicy(JobScriptCombo jobScript, VCMPolicyDeployParameters deployPolicyParameters) throws Exception {
		if(jobScript == null){
			logger.error("the jobscript is null");
			throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_GET_JOBSCRIPT,new Object[0]); 
		}
		
		String instanceUuid = deployPolicyParameters.getInstanceUuid();
		String afguid = evaluteAFGuid(instanceUuid);
		
		//Try to lock the replication job
		Lock jobLockCheck = CommonService.getInstance().getRepJobLock(afguid);
		boolean canRunCheck = jobLockCheck.tryLock();
		if (!canRunCheck) {
			logger.error("The replication job is doing now, can't set the new jobscripts");
			throw new ServiceException(FlashServiceErrorCode.VCM_APPLY_POLICY_REPLICAION_JOB_IS_DO_NOW, new Object[]{ VCMPolicyUtils.getCurrentMachineHostName()});
		}
		
		try {
			return applyVCMJobPolicyInternal(jobScript, deployPolicyParameters);
		} catch (ServiceException se) {
			throw se;
		} catch (Throwable e) {
			logger.error("Failed to apply VSB job script:"+e.getMessage(),e);
			throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_SAVE_JOBSCRIPT);
		} finally {
			jobLockCheck.unlock();
		}
	}
	
	private boolean isConverterSameAsProxy(WebServiceClientProxy monitorClient) {
		String proxyUUID = monitorClient.getServiceV2().getNodeUUID();
		String converterUUID = CommonService.getInstance().getNodeUUID();
		logger.info(String.format("Proxy UUID is %s; Converter UUID is %s", proxyUUID, converterUUID));
		return proxyUUID.equalsIgnoreCase(converterUUID);
	}
	
	private void printDeployInformation(String nodeName, String monitor, int generateType, boolean isBackupToRPS) {
		logger.info("----------Deploy parameter----------");
		logger.info("Node: " + nodeName);
		logger.info("Monitor: " + monitor);
		logger.info("BackupToRPS: " + isBackupToRPS);
		logger.info("GenerateType: " + generateType);
		logger.info("------------------------------------");
	}
	
	private VCMSavePolicyWarning[] applyVCMJobPolicyInternal(JobScriptCombo jobScript, VCMPolicyDeployParameters deployPolicyParameters) throws Exception {

		List<VCMSavePolicyWarning> warningList = new ArrayList<VCMSavePolicyWarning>();
		
		String instanceUuid = deployPolicyParameters.getInstanceUuid();
		
		String afguid = evaluteAFGuid(instanceUuid);
		
		FailoverJobScript failoverJobScript = jobScript.getFailoverJobScript();
		ReplicationJobScript replicationJobScript = jobScript.getRepJobScript();
		HeartBeatJobScript heartBeatJobScript = jobScript.getHbJobScript();
		heartBeatJobScript.setVirtualType(replicationJobScript.getVirtualType());
		AlertJobScript alertJobScript = jobScript.getAlertJobScript();
		boolean isVSBWithoutHASupport = ManualConversionUtility.isVSBWithoutHASupport(replicationJobScript);
		boolean isCrossSite = ManualConversionUtility.isCrossSite(replicationJobScript);
		boolean isVSBOnMSP = ManualConversionUtility.isVSBOnMSP(replicationJobScript);
		VSphereBackupType HbbuNodeType = getVSphereBackup(deployPolicyParameters);
		boolean isHbbuNode = HbbuNodeType != VSphereBackupType.NON_HBBU_BACKUP;
		
		String nodeName;
		String agentNodename;
		if (isHbbuNode) {
			if (StringUtil.isEmptyOrNull(deployPolicyParameters.getHostname()))
				agentNodename = deployPolicyParameters.getSourceMachineInfo().getVmName();
			else
				agentNodename = deployPolicyParameters.getHostname();
			nodeName = deployPolicyParameters.getSourceMachineInfo().getHostName();
			if (StringUtil.isEmptyOrNull(nodeName))
				nodeName = agentNodename;
		}
		else {
			agentNodename = deployPolicyParameters.getHostname();
			nodeName = agentNodename;
		}
		
		
		printDeployInformation(agentNodename, heartBeatJobScript.getHeartBeatMonitorHostName(), replicationJobScript.getGenerateType(), deployPolicyParameters.isRPSNode());

		// Step 0: validate the D2D client in case of local VSB and backup to RPS
		WebServiceClientProxy proxy = validateD2DClient(deployPolicyParameters, isVSBWithoutHASupport);
		
		{
			String thisNodeID = retrieveCurrentNodeID();
			failoverJobScript.setConverterID(thisNodeID);
			replicationJobScript.setConverterID(thisNodeID);
			heartBeatJobScript.setConverterID(thisNodeID);
			alertJobScript.setConverterID(thisNodeID);

			failoverJobScript.setAFGuid(afguid);
			replicationJobScript.setAFGuid(afguid);
			heartBeatJobScript.setAFGuid(afguid);
			alertJobScript.setAFGuid(afguid);
			
			replicationJobScript.setIsPlanPaused(!deployPolicyParameters.isEnabled());
			failoverJobScript.setIsPlanPaused(!deployPolicyParameters.isEnabled());
		}
		
		replicationJobScript.setBackupToRPS(deployPolicyParameters.isRPSNode());
		failoverJobScript.setBackupToRPS(deployPolicyParameters.isRPSNode());
			
		replicationJobScript.setNodeName(nodeName);
		replicationJobScript.setAgentNodeName(agentNodename);
		replicationJobScript.setVSphereBackupType(HbbuNodeType);
		replicationJobScript.setPlanUUID(deployPolicyParameters.getPlanUUID());
		
		VCMD2DBackupInfo vcmBackupInfo = new VCMD2DBackupInfo();
		if (isCrossSite) {
			vcmBackupInfo = jobScript.getVcmBackupInfo();
			vcmBackupInfo.setRpsPolicyUUID(deployPolicyParameters.getRPSInfo().getRpsPolicyUUID());
		}
		else
			vcmBackupInfo = getVCMD2DBackupInfo(afguid, isVSBWithoutHASupport, proxy, deployPolicyParameters, agentNodename);
		
		replicationJobScript.setAgentBackupDestName(vcmBackupInfo.getBackupDestName());
		
		if (!isVSBOnMSP && !isHbbuNode) {
			if (deployPolicyParameters.isRPSNode()){
				replicationJobScript.setAgentUUID(deployPolicyParameters.getSourceMachineInfo().getAuthUuid());
			} else {
				replicationJobScript.setAgentUUID(retrieveCurrentAuthUUID(true));
			}
		}

		if (deployPolicyParameters.isRPSNode()){
			replicationJobScript.setRpsPolicyUUID(vcmBackupInfo.getRpsPolicyUUID());

			replicationJobScript.setRpsHostName(deployPolicyParameters.getRPSInfo().getRpsHostName());
			replicationJobScript.setRpsPort(deployPolicyParameters.getRPSInfo().getRpsPort());
			replicationJobScript.setRpsProtocol(deployPolicyParameters.getRPSInfo().getRpsProtocol());
			replicationJobScript.setRpsUserName(deployPolicyParameters.getRPSInfo().getRpsUserName());
			replicationJobScript.setRpsPassword(WSJNI.AFEncryptString(deployPolicyParameters.getRPSInfo().getRpsPassword()));
			if (isHbbuNode)
				replicationJobScript.setEsxHostnameForHBBU(deployPolicyParameters.getSourceMachineInfo().getESXHostName());
			
			try{
				RpsPolicy4D2D rpsPolicy = getRPSPolicy(deployPolicyParameters.getRPSInfo().getRpsHostName(), deployPolicyParameters.getRPSInfo().getRpsUserName(), 
						deployPolicyParameters.getRPSInfo().getRpsPassword(), deployPolicyParameters.getRPSInfo().getRpsPort(), 
						deployPolicyParameters.getRPSInfo().getRpsProtocol(), replicationJobScript.getRpsPolicyUUID(), true);
				
				if (rpsPolicy == null){
					String message = WebServiceMessages.getResource("rpsPolicyNotExist", deployPolicyParameters.getRPSInfo().getRpsPolicy(), 
							deployPolicyParameters.getRPSInfo().getRpsHostName());
					throw new ServiceException(message, FlashServiceErrorCode.BackupConfig_ERR_Policy_Not_Exist);
				}

				replicationJobScript.setDataStoreUUID(rpsPolicy.getDataStoreName());
				replicationJobScript.setDataStoreDisplayName(rpsPolicy.getDataStoreDisplayName());
				replicationJobScript.setDataStorePath(rpsPolicy.getStorePath());
				if(!StringUtil.isEmptyOrNull(rpsPolicy.getStoreUserName())){
					replicationJobScript.setDataStoreUserName(rpsPolicy.getStoreUserName());
					replicationJobScript.setDataStorePassword(WSJNI.AFEncryptString(rpsPolicy.getStorePassword()));
				}
			}catch (Exception e){
				logger.error("Failed to get the RPS policy UUID for the D2D from RPS server.", e);
				throw new ServiceException(FlashServiceErrorCode.BackupConfig_RPS_SERVER_NOT_REACHABLE);
			}
		}

		replicationJobScript.setAgentVMName(getVMNameFromBackupDestName(replicationJobScript, deployPolicyParameters.isRPSNode(), isHbbuNode));

		// Set converter info to failover job script in case of backup to RPS
		if (deployPolicyParameters.isRPSNode()){
			failoverJobScript.setConverterHostname(deployPolicyParameters.getRPSInfo().getRpsHostName());
			failoverJobScript.setConverterProtocol(deployPolicyParameters.getRPSInfo().getRpsProtocol() == 1 ? "http" : "https");
			failoverJobScript.setConverterPort(deployPolicyParameters.getRPSInfo().getRpsPort());
			failoverJobScript.setConverterUUID(retrieveCurrentAuthUUID(true));
		}
		failoverJobScript.setAgentUUID(replicationJobScript.getAgentUUID());
		
		heartBeatJobScript.setBackupToRPS(deployPolicyParameters.isRPSNode());
		
		if (isVSBOnMSP) {
			replicationJobScript.setAutoReplicate(true);
		}
		
		if (!isVSBWithoutHASupport) {
			// 1, check if is full machine backup
			// 2, check the monitee is hyperV role
			// 3, check if the monitee/proxy are on the same server
			// 4, check if source machine uses UEFI
			validateMoniteeNode(afguid, deployPolicyParameters, heartBeatJobScript, proxy, warningList);
		}
		
		//Step 1: validate the alter configuration information
		VCMPolicyUtils.validateAlertConfiguration(alertJobScript);
		
	
		//Step 2: validate the monitor information
		WebServiceClientProxy monitorClient = null;
		monitorClient = VCMPolicyUtils.validateMonitor(instanceUuid, heartBeatJobScript, replicationJobScript, failoverJobScript);
		try {
			long adjust = monitorClient.getServiceV2().checkMonitorJavaHeapSize();
			if (adjust != 0l) {
				String hostName = heartBeatJobScript.getHeartBeatMonitorHostName();
				VCMSavePolicyWarning savePolicyWarning = new VCMSavePolicyWarning(FlashServiceErrorCode.VCM_APPLY_POLICY_ADJUST_JAVA_HEAP, new String[]{ hostName});
				warningList.add(savePolicyWarning);
			}
		} catch (Exception e) {
			logger.error("Fail to adjust monitor's Java heap size.", e);
		}
		
		try {
			ReplicationDestination dest = replicationJobScript.getReplicationDestination().get(0);
			if (dest.isProxyEnabled() && isConverterSameAsProxy(monitorClient)) {
				dest.setProxyEnabled(false);
				logger.info("Proxy is same as converter. Disable proxy.");
			}
		}catch (Exception e) {
			logger.error("Fail to adjust proxy setting.", e);
		}
		
		//Step 3: validate the ESX
		CAVirtualInfrastructureManager vmwareOBJ = VCMPolicyUtils.validateESX(instanceUuid, jobScript, false);
		
		//Step 4:begin to install the VDDK service
		StringBuilder installedVDDKMachine = new StringBuilder();
		boolean isVDDKNeedRebootMachine = VCMPolicyUtils.installVDDKService(replicationJobScript, heartBeatJobScript, installedVDDKMachine);
		
		// Check if have sufficient permission
		if (replicationJobScript.getVirtualType() == VirtualizationType.VMwareVirtualCenter && !hasSufficientPermissionForVSB(vmwareOBJ)) {
			ReplicationDestination dest = replicationJobScript.getReplicationDestination().get(0);
			String userName = ((VMwareVirtualCenterStorage)dest).getVirtualCenterUserName();
			VCMSavePolicyWarning checkPermissionWarning = new VCMSavePolicyWarning(FlashServiceErrorCode.VCM_APPLY_NOT_SUFFICIENT_PERMISSION, new String[]{userName});
			warningList.add(checkPermissionWarning);
			logger.warn("The user " + userName + " may not have sufficient permissions to perform virtual standby job. A user with administrative privileges is recommended.");
		}
		
		if (replicationJobScript.getVirtualType() == VirtualizationType.VMwareVirtualCenter || replicationJobScript.getVirtualType() == VirtualizationType.VMwareESX) {
			// If the source node is x86 and the target hypervisor is vSphere 5.5 and it is a share folder case
			// The vSphere 5.5 does not have x86 version, in this case, virtual standby may fail, give a warning.
			if (isLaterThanESX55(replicationJobScript, vmwareOBJ)) {
				boolean isRPS = replicationJobScript.getBackupToRPS();
				boolean isProxy = replicationJobScript.getReplicationDestination().get(0).isProxyEnabled();
				String hypervisorName = null;
				if (replicationJobScript.getVirtualType() == VirtualizationType.VMwareESX)
					hypervisorName = ((VMwareESXStorage)replicationJobScript.getReplicationDestination().get(0)).getESXHostName();
				else
					hypervisorName = ((VMwareVirtualCenterStorage)replicationJobScript.getReplicationDestination().get(0)).getVirtualCenterHostName();
				if (isProxy) {
					// Check if the monitor is x86
					if (monitorClient.getServiceV2().isX86()) {
						VCMSavePolicyWarning checkMonitorOSWarning = new VCMSavePolicyWarning(
								FlashServiceErrorCode.VCM_NOT_SUPPORTED_VSPHERE_MONITOR, new String[] { hypervisorName });
						warningList.add(checkMonitorOSWarning);
						logger.warn("The target platform is vSphere 5.5 which does not have a 32-bit version and the monitor is x86, virtual standby may fail.");
					}		
				} else {
					if (!isRPS) {
						if (vcmBackupInfo.isX86()) {
							VCMSavePolicyWarning checkSourceOSWarning = new VCMSavePolicyWarning(
									FlashServiceErrorCode.VCM_NOT_SUPPORTED_VSPHERE_SOURCE, new String[] { hypervisorName });
							warningList.add(checkSourceOSWarning);
							logger.warn("The target platform is vSphere 5.5 which does not have a 32-bit version and the source is x86, virtual standby may fail.");
						}
					}
				}
			}
		} else {
			// Check if the hyper-v destination folder is compressed/encrypted
			List<DiskDestination> disks = replicationJobScript.getReplicationDestination().get(0).getDiskDestinations();
			List<String> vmPathList = new ArrayList<String>();
			for (DiskDestination d : disks) {
				String vmPath = d.getStorage().getName();
				vmPathList.add(vmPath);
			}
			HyperVDestinationInfo hypervDestInfo = monitorClient.getServiceV2().getHyperVDestInfo(vmPathList);
			List<Long> pathValid = hypervDestInfo.isInvalidPathList();
			if (pathValid.contains(1)) {
				String hostName = ((ARCFlashStorage)(replicationJobScript.getReplicationDestination().get(0))).getHostName();
				logger.error("The vm path is compressed/encrypted/compressed volume. hostname = " + hostName + " path = " + vmPathList.get(pathValid.indexOf(1)));
				throw new ServiceException(FlashServiceErrorCode.VCM_SOCK_VHD_CMPRS_VOL, new String[]{hostName, vmPathList.get(pathValid.indexOf(1))});
			}	
		}
		
		// Check if the destination vSphere support the source machine's OS, if not, add a warning.
		validatePlatformDestination(afguid, replicationJobScript, vcmBackupInfo, vmwareOBJ, warningList);
		
		//Step 5: check VM name
		String vmNamePrefix = failoverJobScript.getFailoverMechanism().get(0).getVirtualMachineDisplayName();
		replicationJobScript.setVmNamePrefix(vmNamePrefix);
		String vmName = vmNamePrefix + replicationJobScript.getAgentVMName();

		vmName = checkVMNameFromExistingJobScript(afguid, vmName, vmNamePrefix);
		
		StringBuilder vmUuid = new StringBuilder();
		boolean isExist = VCMPolicyUtils.checkVMNameExist(replicationJobScript, heartBeatJobScript, failoverJobScript, 
				vmName, monitorClient, vmwareOBJ, afguid, vmUuid);
		
		// If vm is not running, remove failover vm tag in register.
		if (!isHbbuNode && !isVSBWithoutHASupport) {
			boolean isRunning = false;
			if (isExist) {
				// Check if vm is running
				try {
					if (failoverJobScript.getVirtualType() == VirtualizationType.HyperV) {
						int power = monitorClient.getServiceV2().GetHyperVVmState(vmUuid.toString());
						isRunning = (power == 2) ? true : false;
					} else {
						powerState state = vmwareOBJ.getVMPowerstate(vmName, vmUuid.toString());
						isRunning = (state == powerState.poweredOn) ? true : false;
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
			
			if (!isExist || !isRunning) {
				// Remove failover vm tag
				if (deployPolicyParameters.isRPSNode()) {
					proxy.getServiceV2().removeFailoverTag();
				} else {
					CommonUtil.removeFailoverTag();
				}
			}
		}
		
		if (vmwareOBJ != null)
			vmwareOBJ.close();

		//Step 6: adjust the job script
		VCMPolicyUtils.adjustJobScripts(failoverJobScript, replicationJobScript, vmName);
		
		VCMPolicyUtils.validateStandbyVMSnapshotCount(replicationJobScript);
		
		//Check if heartbeat is paused before this deployment for local VSB
		boolean isHeartBeatPausedBefore = false;
		if (!isVSBWithoutHASupport) {
			isHeartBeatPausedBefore = CheckIsHeartBeatPausedBefore(afguid, proxy);
		}
		
		//Step 7: set the job script
		boolean firstVCMSettingFlag = false;  
		try {
			firstVCMSettingFlag = setJobScriptComboEx(jobScript, deployPolicyParameters, warningList);
		} catch (Throwable e) {
			// TODO: handle exception
			logger.error("Failed to set the jobscript:"+e.getMessage(),e);
			throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_SAVE_JOBSCRIPT);
		}
		
		//Step 8: start the heart beat
		if (!isVSBWithoutHASupport) {
			try {
				if (deployPolicyParameters.isRPSNode()){
					logger.info("BackupToRPS: Start heartbeat on the monitee");
					proxy.getServiceV2().registerHeartBeatForHA(CommonUtil.marshal(heartBeatJobScript));
				}
				else{
					startHeartBeat(afguid);
				}
			} catch (Throwable e) {
				// TODO: handle exception
				logger.error(e);
				throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_START_HEARTBEAT);
			}
		}
		
		//Step 9: Register to RPS
		if (deployPolicyParameters.isRPSNode()){
			boolean ret = registerToRPS(afguid, deployPolicyParameters.getRPSInfo(), true);
			if (!ret){
				logger.error("Failed register to RPS.");
				throw new ServiceException(
					WebServiceMessages.getResource("cannotConnectServer", new Object[]{deployPolicyParameters.getRPSInfo().getRpsHostName()}),
							FlashServiceErrorCode.Common_CannotConnectRPSServer); 
			}
			
			ActivityLogSyncher.getInstance().addVM(afguid);
		}

		//
		if (!isVSBWithoutHASupport && isHeartBeatPausedBefore) {
			logger.info("Try pause heartbeat after deployment completed.");
			try {
				pauseHeartBeat(afguid, false);
			} catch (Throwable e) {
				logger.warn("Failed to pause heartbeat after deployment completed, " + e.getMessage(), e);
			}
		}

		saveProductionServerRoot(afguid, monitorClient);
		
		updateHeartbeatModel(afguid, monitorClient);
		
		syncVCMStatus2Monitor(afguid);

		//Step 10: start the backup jobs or replication jobs
		try {
			if(!isVSBWithoutHASupport && firstVCMSettingFlag){
				SessionInfo[] sessions = getReplicationQueueSize(afguid);
				if(sessions == null){
//					logger.warn("Find no sessions to be replicated, so don't submit the backup job on the monitee!");
					logger.info("Find no sessions to be replicated.");
				} else {
					// long result = 2;
					if (!isHbbuNode) {
						if (deployPolicyParameters.isRPSNode()) {
							try{
								proxy.getServiceV2().submitIncrementalBackupForVisualStandby(afguid, sessions.length, firstVCMSettingFlag);
							}catch (Exception e) {
//								logger.warn("Failed to submit the backup job! " + e.getMessage());
								logger.warn("Failed to set firstVCMSettingFlag! " + e.getMessage());
							}
						} else {
							submitIncrementalBackup(afguid, sessions.length, firstVCMSettingFlag);
						}
					}

//					if (result != 2) {
						try {
							CommonUtil.setSubmitIncrementalFlag(afguid, INCREMENTAL_COMPLETE, deployPolicyParameters.isRPSNode() || isHbbuNode);
						} catch (Exception e) {
							logger.warn("Failed to set submit incremental flag! " + e.getMessage());
						}
//					}
//					
//					if (result == 0) {
//						String activityLog = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_SUBMIT_INCREMENTAL_BACKUP);
//					
//						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1,
//								Constants.AFRES_AFJWBS_GENERAL, new String[] {activityLog, "", "", "", "" }, afguid);
//					}
				}
			}
			else if (isVSBOnMSP){
				// msp manual conversion set force smart copy and start replication job
				HAService.getInstance().forceNextReplicationMergeAllSessions(afguid, true);
				
				SessionInfo[] sessions = getReplicationQueueSize(afguid);
				if (sessions != null && sessions.length > 0)
					startReplicationDirectlyForRemoteVSB(afguid);
			}
		} catch (Exception e) {
			logger.error("Failed to start job:" + e );
		}
		
		if((replicationJobScript.getVirtualType()!= VirtualizationType.HyperV) && isVDDKNeedRebootMachine){
			logger.info("Don't start the backup or replcation jobs with reboot machine");
			
			VCMSavePolicyWarning savePolicyWarning = new VCMSavePolicyWarning(FlashServiceErrorCode.VCM_VDDK_SERVICE_REQUIRE_REBOOT, new String[]{installedVDDKMachine.toString()});
			warningList.add(savePolicyWarning);
			return warningList.toArray(new VCMSavePolicyWarning[0]);
		}
		
		logger.info("Successfully set the VCM policy.");
		return warningList.toArray(new VCMSavePolicyWarning[0]);
	}
	
	private String checkVMNameFromExistingJobScript(String afguid, String vmName, String vmNamePrefix) {
		ReplicationJobScript oldRepScript = HAService.getInstance().getReplicationJobScript(afguid);
		if(oldRepScript==null){
			logger.info(String.format("No existing VSB job script for node %s", afguid));
			return vmName;
		}
		
		String oldVMName = oldRepScript.getReplicationDestination().get(0).getVirtualMachineDisplayName();
		if (oldVMName.equalsIgnoreCase(vmName)) {
			logger.info(String.format("The VM name of existing VSB job script for node %s is %s ", afguid, vmName));
			return vmName;
		} else {
			logger.info(String.format("The VM name of existing VSB job script for node %s is %s, which is different to the configured name %s ", afguid, oldVMName, vmName));
			
			String oldVMNamePrefix = oldRepScript.getVmNamePrefix();
			
			if(oldVMNamePrefix != null && oldVMNamePrefix.compareToIgnoreCase(vmNamePrefix) != 0) {
				logger.info(String.format("The VM name prefix of existing VSB job script for node %s is %s, which is different to the configured %s ", afguid, oldVMNamePrefix, vmNamePrefix));
				return vmName;
			}
			return oldVMName;
		}
	}

	public VCMD2DBackupInfo getVCMD2DBackupInfo(String afguid) throws ServiceException{
		
		VCMD2DBackupInfo backupInfo = new VCMD2DBackupInfo();
		String productionServer = HACommon.getProductionServerNameByAFGuid(afguid);
		backupInfo.setBackupDestName(productionServer);
		
		if (HACommon.isTargetPhysicalMachine(afguid)) {
			// get os version and check if it is x86
			SourceNodeSysInfo sourceNodeSysInfo = new SourceNodeSysInfo();
			try {
				getNativeFacade().getSourceNodeSysInfo(sourceNodeSysInfo);
			} catch (Throwable e) {
				logger.error("Fail to get source node information, afguid = " + afguid);
				backupInfo.setOsVersion(null);
				return backupInfo;
			}
			backupInfo.setOsVersion(sourceNodeSysInfo.getVersion());
			backupInfo.setX86(sourceNodeSysInfo.isX86());
			boolean isUEFI = HAService.getInstance().getNativeFacade().IsFirmwareuEFI();
			backupInfo.setUEFI(isUEFI);
			boolean diskLargerThan2T = HAService.getInstance().getNativeFacade().CheckIfExistDiskLargerThan2T();
			backupInfo.setDiskLargerThan2T(diskLargerThan2T);
		}
		else // HBBU, or remote VSB with RHA integrated
		{	
			try {
				VCMPolicyUtils.getBackupVMD2DBackupInfo(afguid, backupInfo);
			} catch (Throwable e) {
				logger.error("Fail to get node information of backup VM, afguid = " + afguid);
				backupInfo.setOsVersion(null);
				return backupInfo;
			}
		}
		return backupInfo;
	}
	private VCMD2DBackupInfo getVCMD2DBackupInfo(String afguid, boolean isMSPManualConversion, WebServiceClientProxy proxy, VCMPolicyDeployParameters deployPolicyParameters, String agentNodename)  throws ServiceException{

		if (deployPolicyParameters.isRPSNode()){
			if (!isMSPManualConversion) {
				try {
					VCMD2DBackupInfo backupInfo =  proxy.getServiceV2().getVCMD2DBackupInfo(afguid);
					if (backupInfo != null)
						backupInfo.setRpsPolicyUUID(deployPolicyParameters.getRPSInfo().getRpsPolicyUUID());
					else {
						throw new ServiceException(FlashServiceErrorCode.Common_CantConnectHost, new String[]{deployPolicyParameters.getSourceMachineInfo().getHypervisorHostName()});
					}
					
					return backupInfo;
				} catch (Exception e) {
					logger.error(e);
					throw new ServiceException(FlashServiceErrorCode.Common_CantConnectHost, new String[]{deployPolicyParameters.getSourceMachineInfo().getHypervisorHostName()});
				}
			} else {
				// MSP case
				BackupD2D backupD2D = getRPSPolicyUUID(deployPolicyParameters.getRPSInfo().getRpsHostName(), deployPolicyParameters.getRPSInfo().getRpsUserName(), 
						deployPolicyParameters.getRPSInfo().getRpsPassword(), deployPolicyParameters.getRPSInfo().getRpsPort(), 
						deployPolicyParameters.getRPSInfo().getRpsProtocol(), afguid);
				
				if (backupD2D == null || StringUtil.isEmptyOrNull(backupD2D.getPolicyUUID())){
					logger.error("Failed to get the D2D backup info of node " + afguid + " from RPS server " + deployPolicyParameters.getRPSInfo().getRpsHostName());
					throw new ServiceException(FlashServiceErrorCode.VCM_APPLY_POLICY_FAILED_FOR_MSP, new String[]{agentNodename, deployPolicyParameters.getRPSInfo().getRpsHostName()});
				}
				
				VCMD2DBackupInfo backupInfo = new VCMD2DBackupInfo();
				
				String fullBackupDestination = backupD2D.getFullBackupDestination();
				int pos = fullBackupDestination.lastIndexOf("\\");
				if (pos != -1)
					fullBackupDestination = fullBackupDestination.substring(pos+1, fullBackupDestination.length());
				backupInfo.setBackupDestName(fullBackupDestination);
				backupInfo.setRpsPolicyUUID(backupD2D.getPolicyUUID());
				return backupInfo;
			}
		}else{
			return getVCMD2DBackupInfo(afguid);
		}
	}
	
	private String getVMNameFromBackupDestName(ReplicationJobScript replicationJobScript, boolean isBackupToRPS, boolean isHbbuNode){
		String agentVMName = replicationJobScript.getAgentBackupDestName();
		
		if (isBackupToRPS) {
			if(agentVMName.contains("[") && agentVMName.contains("]")){
				// Standalone D2D backup to RPS, backup destination name is hostname[machine SID]
				agentVMName = agentVMName.substring(0, agentVMName.lastIndexOf("["));
			}
		}
		
		if (ManualConversionUtility.isVSBWithoutHASupport(replicationJobScript)) {
			// recheck if HBBU node for remote VSB
			VCMMachineInfo vcmMachineInfo = BaseReplicationCommand.getVCMMachineInfo(replicationJobScript, true);
			if (vcmMachineInfo == null) {
				logger.warn("Failed to get machine info from backup destination.");
				isHbbuNode = true;
			} else if (vcmMachineInfo.getNodeType() == VCMMachineInfo.VCMNodeType.HBBU_VM)
				isHbbuNode = true;
		}
		
		if (isHbbuNode) {
			if(agentVMName.contains("@")){
				agentVMName = agentVMName.replace('@', '(');
				agentVMName += ")";
			}
		} 

		return agentVMName;
	}
	
	private boolean CheckIsHeartBeatPausedBefore(String afguid, WebServiceClientProxy proxy) {
		boolean isHeartBeatPausedBefore = false;
		Integer[] result = null;
		try {
			if (proxy != null)
				result = proxy.getServiceV2().getStatesThis(afguid);
			else
				result = getStatesThis(afguid);
		} catch (Throwable e) {
			logger.warn("Failed to get states from monitee, " + e.getMessage(), e);
		}
		
		if (result != null) {
			isHeartBeatPausedBefore = ((result[0] & HeartBeatJobScript.STATE_CANCELED) != 0);
		}
		
		if (isHeartBeatPausedBefore) 
			logger.info("Heartbeat is paused before this deployment for local VSB, so after new redeployment, will pause heartbeat again.");
		
		return isHeartBeatPausedBefore;
	}
	
	private void saveProductionServerRoot(String afguid, WebServiceClientProxy monitorClient) {
		saveEmptyProductionServerRoot(afguid);
		saveProductionServerRootToMonitor(afguid, monitorClient);
	}
	
	private void saveProductionServerRootToMonitor(String afguid, WebServiceClientProxy monitorClient) {
		String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
		try {
			ProductionServerRoot serverRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(afguid);
			monitorClient.getServiceV2().reportProductionServerRoot(CommonUtil.marshal(serverRoot));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public void getHyperVVersion(ReplicationJobScript replicationJobScript, StringBuilder version) {
		ARCFlashStorage dest = (ARCFlashStorage)replicationJobScript.getReplicationDestination().get(0);
		WebServiceClientProxy proxy = WebServiceFactory.getFlassService(dest.getMonitorProtocol(),
				dest.getHostName(), dest.getMonitorPort(), ServiceInfoConstants.SERVICE_ID_D2D_R16_5,0,0);
		proxy.getServiceV2().validateUser(dest.getUserName(), dest.getPassword(), null);
		
		version.append(proxy.getServiceV2().getOSVersion());
	}
	
	public boolean isLaterThanESX55(ReplicationJobScript replicationJobScript, CAVirtualInfrastructureManager vmwareOBJ) {
		StringBuilder strMajor = new StringBuilder();
		StringBuilder strMinor = new StringBuilder();
		StringBuilder strBuild = new StringBuilder();
		getESXVersion(replicationJobScript, vmwareOBJ, strMajor, strMinor, strBuild);
		
		int major = Integer.parseInt(strMajor.toString());
		int minor = Integer.parseInt(strMinor.toString());
		
		if (major <= 4)
			return false;
		else if (major == 5 && minor < 5)
			return false;
		else
			return true;
	}
	
	public void getESXVersion(ReplicationJobScript replicationJobScript, CAVirtualInfrastructureManager vmwareOBJ, 
			StringBuilder major, StringBuilder minor, StringBuilder build) {
		try {
			String platformVersion = "";
			if (replicationJobScript.getVirtualType() == VirtualizationType.VMwareESX) {
				// Get the destination esx version and build
				platformVersion = vmwareOBJ.GetESXServerVersion();
				build.append(vmwareOBJ.GetESXServerBuild());
			}
			else {
				ArrayList<ESXNode> nodeList = vmwareOBJ.getESXNodeList();
				String esxName = replicationJobScript.getESXName();
				if (StringUtil.isEmptyOrNull(esxName)) {
					logger.error("ESX name is empty, skip th check.");
					return;
				}
				for (int i = 0; i < nodeList.size(); i++) {
					if (nodeList.get(i).getEsxName().equals(esxName)) {
						// Get the destination vCenter version and build
						platformVersion = vmwareOBJ.getESXHostVersion(nodeList.get(i));
						build.append(vmwareOBJ.getESXHostBuild(nodeList.get(i)));
						break;
					}
				}
			}
			major.append(platformVersion.substring(0, platformVersion.indexOf(".")));
			minor.append(platformVersion.substring(platformVersion.indexOf(".") + 1, platformVersion.lastIndexOf(".")));
		} catch (Exception e) {
			logger.error(e);
			return;
		}
	}
	
	private void updateHeartbeatModel(String afguid, WebServiceClientProxy monitorClient) {
		String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
		ProductionServerRoot prodRoot = null;
		try {
			RepositoryUtil instance = RepositoryUtil.getInstance(xml);
			prodRoot = instance.getProductionServerRoot(afguid);
		}
		catch(Exception ex) {
			logger.info("ProductionServerRoot has not been set.");
		}
		
		// Update vm info to heartbeat model
		if (prodRoot == null)
			return;
		
		ReplicaRoot repRoot = prodRoot.getReplicaRoot();
		if (repRoot != null) {
			VirtualMachineInfo vmInfo = new VirtualMachineInfo();
			vmInfo.setAfguid(afguid);
			vmInfo.setVmGUID(prodRoot.getReplicaRoot().getVmuuid());
			vmInfo.setVmName(prodRoot.getReplicaRoot().getVmname());
			if (repRoot instanceof TransServerReplicaRoot) {
				vmInfo.setType(0);
			} else {
				vmInfo.setType(1);
			}
			
			try {
				if (monitorClient != null)
					monitorClient.getServiceV2().updateHeatbeatModel(afguid, vmInfo);
			} catch(Exception ex) {
				logger.info("Fail to updateHeatbeatModel on the monitor", ex);
			}
		}
	}
	
	public boolean validatePlatformDestination(String afguid, ReplicationJobScript replicationJobScript, VCMD2DBackupInfo vcmBackupInfo, 
			CAVirtualInfrastructureManager vmwareOBJ, List<VCMSavePolicyWarning> warningList) throws Exception {
		boolean result = true;
		String targetPlatform = "";
		String platformVersion = "";
		boolean isMSPManualConversion = ManualConversionUtility.isVSBWithoutHASupport(replicationJobScript);
		boolean isUEFI = false; 
		boolean isDiskLargerThan2T = false;
		boolean isX86 = false;
		
		try {
			// Get source machine's OS version
			ADRConfigure adrConfigInfo = HACommon.getADRConfiguration(afguid, BaseReplicationCommand.getBackupDestinationInfo(replicationJobScript, false, -1));
			String osVersion;
			if (adrConfigInfo == null) {
				if (replicationJobScript.isVSphereBackup() || isMSPManualConversion) {// If it is a HBBU node and did not perform a backup, we cannot get the adr config
					return true;
				} else {
					if (vcmBackupInfo == null) {
						logger.error("VCM backup info is null, skip the check.");
						return true;
					}
					osVersion = vcmBackupInfo.getOsVersion();	
					isUEFI = vcmBackupInfo.isUEFI();
					isDiskLargerThan2T = vcmBackupInfo.isDiskLargerThan2T();
					isX86 = vcmBackupInfo.isX86();
				}					
			}
			else {
				osVersion = adrConfigInfo.getOSVersion();
				isUEFI = adrConfigInfo.isUEFI();
				isX86 = adrConfigInfo.isX86();

				// Check the adr config info to get the disk size
				if (adrConfigInfo != null){
					SortedSet<Disk> disks = adrConfigInfo.getDisks();
					for(Disk disk : disks) {
						if (disk.getSize() > 2199023255552L) {
							isDiskLargerThan2T = true;
							break;
						}
					}
				}
			}
			
			if (osVersion == null) {
				logger.error("OS version is null, skip OS check! afguid = " + afguid);
				return true;
			}
			
			boolean targetPlatformSupportUEFI = false;
			BigDecimal osVer = new BigDecimal(osVersion);
			
			if (osVer.compareTo(new BigDecimal("0.0")) == 0) {
				// Source node's os is linux, does not support vsb job
				logger.error("The source node is a Linux node, does not support vsb job.");
				throw new ServiceException(FlashServiceErrorCode.VCM_DOESNOT_SUPPORT_LINUX, new String[]{replicationJobScript.getAgentNodeName()});
			}
			
			if (replicationJobScript.getVirtualType() == VirtualizationType.VMwareVirtualCenter || replicationJobScript.getVirtualType() == VirtualizationType.VMwareESX) {			
				int build = 0;
				StringBuilder strBuildNum = new StringBuilder();
				StringBuilder strMajor = new StringBuilder();
				StringBuilder strMinor = new StringBuilder();
				getESXVersion(replicationJobScript, vmwareOBJ, strMajor, strMinor, strBuildNum);
				if (StringUtil.isEmptyOrNull(strMajor.toString()) || StringUtil.isEmptyOrNull(strMinor.toString()) || StringUtil.isEmptyOrNull(strBuildNum.toString())) {
					// Do not get the version, skip the ckeck
					logger.error("ESX platform verions or build number is empty, skip the OS check.");
					return true;
				}
				
				platformVersion = strMajor + "." + strMinor;
				build = Integer.parseInt(strBuildNum.toString());
				
				int major = Integer.parseInt(strMajor.toString());
				int minor = Integer.parseInt(strMinor.toString());
				targetPlatform = "ESX/ESXi " + platformVersion + ", " + build;
				
				if (major < 4) {
					result = false;
					targetPlatformSupportUEFI = false;
				}
				else if (major == 4) {
					if (osVer.compareTo(new BigDecimal("6.2")) >= 0)
					{
						logger.error("The target platform " + targetPlatform + " does not support source machine's OS version " + osVersion + ".");
						throw new ServiceException(FlashServiceErrorCode.VCM_NOT_SUPPORTED_PLATFORM, new String[]{targetPlatform, replicationJobScript.getAgentNodeName()});
					}
					targetPlatformSupportUEFI = false;
				}
				else if(major == 5) {
					targetPlatformSupportUEFI = true;
					if (minor == 0) {
						if (build < 623860 && osVer.compareTo(new BigDecimal("6.2")) >= 0)  // 5.0 does not support server 2012
						{
							result = false;
						}
					}
				}
				else {
					result = true;
					targetPlatformSupportUEFI = true;
				}
				
				// If hypervisor is not ESX/ESXi 5.5 and disk size is larger than 2TB, the target hypervisor does not support
				if ( major < 5 || (major == 5 && minor < 5 )) {
					if (isDiskLargerThan2T) {
						VCMSavePolicyWarning checkDiskSizeWarning = new VCMSavePolicyWarning(
								FlashServiceErrorCode.VCM_NOT_SUPPORTED_VSPHERE_DISK_SIZE, new String[] { targetPlatform, replicationJobScript.getAgentNodeName() });
						warningList.add(checkDiskSizeWarning);
						logger.warn("The target platform " + targetPlatform + " does not support source machine's disk size.");
					}
				}
			}
			else if (replicationJobScript.getVirtualType() == VirtualizationType.HyperV) {
				StringBuilder version = new StringBuilder();
				getHyperVVersion(replicationJobScript, version);
				platformVersion = version.toString();  // For example: 6.2
				targetPlatform = platformVersion;
				switch (targetPlatform) {
					case "6.0" : targetPlatform = "2008";
						break;	
					case "6.1" : targetPlatform = "2008 R2";
						break;
					case "6.2" : targetPlatform = "2012";
						break;
					case "6.3" : targetPlatform = "2012 R2";
						break;
					default :
						targetPlatform = "";
				}
				targetPlatform = "Hyper-V " + targetPlatform;
				
				if (osVer.compareTo(new BigDecimal(platformVersion)) == 1)
					result = false;

				if (isDiskLargerThan2T) {
					// Fix defect 58672 Policy deploy  – when Source>2TB by lijwe02 on 2012-03-07 >>
					boolean targetPlatformSupport2TDisk = (new BigDecimal(platformVersion)).compareTo(new BigDecimal("6.2")) >= 0;
					if (!targetPlatformSupport2TDisk) {
						throw new ServiceException(FlashServiceErrorCode.VCM_DISK_LARGER_THAN2TB_HYPERV, new Object[] {});
					}
				}
				// << by lijwe02
				targetPlatformSupportUEFI = RepositoryManager.IsTargetPlatformSupportVMGeneretion2(osVersion, !isX86, platformVersion);
			}
			else
				result = false;
			
			if (!result) {
				VCMSavePolicyWarning checkOSWarning = new VCMSavePolicyWarning(
						FlashServiceErrorCode.VCM_NOT_SUPPORTED_PLATFORM, new String[] { targetPlatform, replicationJobScript.getAgentNodeName() });
				warningList.add(checkOSWarning);
				logger.warn("The target platform " + targetPlatform + " does not support source machine's OS version " + osVersion + ".");
			}
	
			if(isUEFI && !targetPlatformSupportUEFI)
			{
				String msg = String.format("The target platform does not support UEFI of the source machine.");
				logger.error(msg);
				throw new ServiceException(FlashServiceErrorCode.VCM_DOESNOT_SUPPORT_UEFI, new String[]{replicationJobScript.getAgentNodeName()});
			}
		}
		catch (Exception e) {
			if (e instanceof ServiceException)
				throw e;
			logger.error(e);
		}
		return result;
	}
	
	public boolean isX86() {
		return getNativeFacade().isX86();
	}
	
	private void validateMoniteeNode(String afguid, VCMPolicyDeployParameters deployPolicyParameters, HeartBeatJobScript heartBeatJobScript, WebServiceClientProxy proxy, List<VCMSavePolicyWarning> warningList) throws ServiceException{
		VCMPolicyNodeValidation nodeCheck = new VCMPolicyNodeValidation();
		nodeCheck.setInstanceUUID(afguid);
		if (getVSphereBackup(deployPolicyParameters) == VSphereBackupType.NON_HBBU_BACKUP)
			nodeCheck.setInstanceHostName(deployPolicyParameters.getHostname());
		else
			nodeCheck.setInstanceHostName(deployPolicyParameters.getSourceMachineInfo().getHypervisorHostName());
			
		nodeCheck.setMonitorHostName(heartBeatJobScript.getHeartBeatMonitorHostName());
		
		VCMSavePolicyWarning[] warnings = null;
		if (deployPolicyParameters.isRPSNode()){
			try {
				warnings = proxy.getServiceV2().validateNodeForVisualStandby(nodeCheck);
			} catch (Exception e) {
				if(e instanceof ServiceException) {
					throw (ServiceException)e;
				} else if(e instanceof SOAPFaultException) {
					logger.error(e);
					SOAPFaultException se = (SOAPFaultException) e;
					if(se.getFault() != null) { 
						String msg = HACommon.processWebServiceException(se, new Object[]{nodeCheck.getInstanceHostName()});
						throw new ServiceException(msg, FlashServiceErrorCode.Common_General_Message);
					}
				}

				//Consider as cannot connect to server
				logger.error(e);
				throw new ServiceException(FlashServiceErrorCode.Common_CantConnectHost, new String[]{deployPolicyParameters.getSourceMachineInfo().getHypervisorHostName()});
			}
		}else{
			warnings = validateNodeForVisualStandby(nodeCheck);
		}
		
		if (warnings != null) {
			for (VCMSavePolicyWarning warning : warnings) {
				warningList.add(warning);
			}
		}
	}
	

	public VCMSavePolicyWarning[] validateNodeForVisualStandby(VCMPolicyNodeValidation nodeCheck) throws ServiceException{
		String afGuid = nodeCheck.getInstanceUUID();
		
		// 1, check if is full machine backup
		BackupConfiguration backupConfiguration = HACommon.getBackupConfigurationViaAFGuid(afGuid);
		if(backupConfiguration == null) {
			logger.error("The user doesn't configure the backup setting.");
			throw new ServiceException(FlashServiceErrorCode.VCM_BACKUPSETTING_FULL_MACHINE);
		}
		
		if(HACommon.isTargetPhysicalMachine(afGuid) && !backupConfiguration.getBackupVolumes().isFullMachine()) {
			logger.error("The backup seting isn't full machine");
			throw new ServiceException(FlashServiceErrorCode.VCM_BACKUPSETTING_FULL_MACHINE);
		}
		
		// 2, check the monitee is hyperV role, if yes, block it
		if(HAService.getInstance().isMoniteeIsHyperVRole(afGuid)) {
			logger.error("The monitee installed the HyperV role and block it");
			throw new ServiceException(FlashServiceErrorCode.VCM_INSTALLED_HYPERV_ROLE);
		}
		
		List<VCMSavePolicyWarning> warningList = new ArrayList<VCMSavePolicyWarning>();

		// 3, check if the monitee/proxy are on the same server
		String monitorHost = nodeCheck.getMonitorHostName();
		try	{
			//get the host information
			List<String> localMachine = new ArrayList<String>();
			String monteeMachineName = InetAddress.getLocalHost().getHostName();
			localMachine.add(monteeMachineName);
			//localMachine.add(localInetAddress.getCanonicalHostName());
			InetAddress[] ipAddresses = InetAddress.getAllByName(monteeMachineName);
			for (InetAddress inetAddress : ipAddresses) {
				localMachine.add(inetAddress.getHostAddress());
			}
			for (String host : localMachine) {
				if(host.equalsIgnoreCase(monitorHost)) {
					String msg = String.format("The monitee[%s] and the montor[%s] are the same machine", nodeCheck.getInstanceHostName(), monitorHost);
					if(HACommon.isTargetPhysicalMachine(afGuid)){
						logger.error(msg);
						throw new ServiceException(FlashServiceErrorCode.VCM_SAME_MONITEE_MONITOR, new String[]{nodeCheck.getInstanceHostName(),monitorHost});
					}else{
						logger.warn(msg);
						VCMSavePolicyWarning savePolicyWarning = new VCMSavePolicyWarning(FlashServiceErrorCode.VCM_SAME_PROXY_MONITOR, new String[]{ nodeCheck.getInstanceHostName(),monitorHost});
						warningList.add(savePolicyWarning);
						//throw new ServiceException(FlashServiceErrorCode.VCM_SAME_PROXY_MONITOR, new Object[]{host,monitorHost});
					}
					
				}
			}
		} catch(Exception e) {
			if(e instanceof ServiceException) {
				throw (ServiceException)e;
			}
			else {
				logger.error("Failed to validate the monitor user information for the montor:" + monitorHost);
				logger.error(e.getMessage());
				throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_CONNECT_MONITOR, new String[]{nodeCheck.getInstanceHostName(), monitorHost, e.getMessage()});
			}
		}
		
		return warningList.toArray(new VCMSavePolicyWarning[0]);
	}
	
	private WebServiceClientProxy getD2DService(VCMPolicyDeployParameters deployPolicyParameters) throws ServiceException
	{
		WebServiceClientProxy proxy = null;
		proxy = WebServiceFactory.getFlassService(deployPolicyParameters.getSourceMachineInfo().getHypervisorProtocol(),
				deployPolicyParameters.getSourceMachineInfo().getHypervisorHostName(),
				deployPolicyParameters.getSourceMachineInfo().getHypervisorPort(),
				ServiceInfoConstants.SERVICE_ID_D2D_R16_5,0,0);

		if (getVSphereBackup(deployPolicyParameters) != VSphereBackupType.NON_HBBU_BACKUP) {
            String username = HACommon.getUserFromUsername(deployPolicyParameters.getSourceMachineInfo().getHypervisorUserName());
            String domain   = HACommon.getDomainFromUsername(deployPolicyParameters.getSourceMachineInfo().getHypervisorUserName());
            String password = deployPolicyParameters.getSourceMachineInfo().getHypervisorPassword();

            proxy.getServiceV2().validateUser(username, password, domain);
		} else {
			proxy.getServiceV2().validateUserByUUID(deployPolicyParameters.getSourceMachineInfo().getAuthUuid());
		}
		return proxy;
	}
	
	public static WebServiceClientProxy getInstantVMService(String uuid, String hostName, int port, String protocol) throws ServiceException
	{
		WebServiceClientProxy proxy = null;
		proxy = WebServiceFactory.getInstantVMService(protocol, hostName, port, ServiceInfoConstants.SERVICE_ID_INSTANT_VM_SERVICE,0,0);
		proxy.getInstantVMService().validateUserByUUID(uuid);
		return proxy;
	}
	
	public void activityLogForStartFailover(FailoverJobScript jobScript, long jobID, boolean isAutoFailover) {
		Virtualization virtualization = jobScript.getFailoverMechanism().get(0);
		String virtualServerHost = "";
		if(virtualization!=null) {
			if(virtualization instanceof HyperV) {
				HyperV hyperVJobscript = (HyperV) virtualization;
				virtualServerHost = String.format("Hyper-V [%s]",hyperVJobscript.getHostName());
			}
			else if(virtualization instanceof VMwareESX) {
				VMwareESX vMwareESX = (VMwareESX)virtualization;
				virtualServerHost = String.format("ESX [%s]", vMwareESX.getHostName());
			}
			else if(virtualization instanceof VMwareVirtualCenter) {
				VMwareVirtualCenter vCenter = (VMwareVirtualCenter)virtualization;
				virtualServerHost = String.format("ESX [%s]", vCenter.getEsxName());
			}
		}
		if (isAutoFailover) {
			String msg = WebServiceMessages.getResource(HAService.COLDSTANDBY_ALERT_START_AUTO_FAILOVER, 
					virtualization.getVirtualMachineDisplayName(), jobScript.getProductionServerName(), virtualServerHost);
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL,
					new String[] { msg, "", "", "", "" }, jobScript.getAFGuid());
		}
		else{
			String msg = WebServiceMessages.getResource(HAService.COLDSTANDBY_ALERT_START_MANUAL_FAILOVER, 
					virtualization.getVirtualMachineDisplayName(), jobScript.getProductionServerName(), virtualServerHost);
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL,
					new String[] { msg, "", "", "", "" }, jobScript.getAFGuid());
		}

	}
	public boolean ifNeedExecuteAutoFailover(FailoverJobScript jobScript, Date startTime) {

//		if (jobScript.getIsPlanPaused()) {
//			logger.warn("Plan is paused, there's no need to start auto failover job");
//			String msg = WebServiceMessages.getResource(COLDSTANDBY_ALERT_AUTO_FAILOVER_SKIPPED_BY_PLAN_PAUSED, jobScript.getProductionServerName());
//			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, Constants.AFRES_AFJWBS_GENERAL,
//					new String[] { msg, "", "", "", "" }, jobScript.getAFGuid());
//			return false;
//		}

		if (!ManualConversionUtility.isVSBWithoutHASupport(jobScript)) {
			String msg = String.format("Miss heart beat of source machine [%s] nodeID [%s], try check if it's still alive.", 
					jobScript.getProductionServerName(), jobScript.getAFGuid());
			logger.info(msg);

			boolean sourceAlive = validateSourceNodeForFailover(jobScript);
			if (sourceAlive) {
//				logger.warn("Source machine is still alive, no need to execute failover.");
				
//				msg = WebServiceMessages.getResource(COLDSTANDBY_ALERT_MISS_HEATBEAT_BUT_ALIVE, jobScript.getProductionServerName());
//				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, Constants.AFRES_AFJWBS_GENERAL,
//						new String[] { msg, "", "", "", "" }, jobScript.getAFGuid());
				
				logger.warn("Reschedule failover job as the source machine is still alive.");
				rescheduleFailover(jobScript.getAFGuid());
				return false;
			}

			HAService.getInstance().sendAlertMailWithParameters(jobScript.getAFGuid(), -1, AlertType.MissHeatBeat, startTime, jobScript.getProductionServerName());
		}

		if(!jobScript.isAutoFailover()) {
			logger.info("It's not auto failover in job script, no need to execute failover for " + jobScript.getAFGuid());
			return false;
		}
		
		return true;
	}
	
	private void rescheduleFailover(final String afguid) {

		try {
			FailoverJob job = (FailoverJob) getFailoverJob(afguid);
			if(job == null)
				return;
			
			logger.info("Reschedule failover job for node: " + afguid);
			job.unschedule();
			job.schedule();

		}catch (Exception e) {
			logger.error( "Failed to reschedule failover job for node: " + afguid, e );
		}
	}
	
	private boolean validateSourceNodeForFailover(FailoverJobScript failoverJobScript) {

		try {
			getD2DService(failoverJobScript, 5*1000, 5*1000);
		} catch (WebServiceException e) {
			logger.info("Failed to connect to the source machine " + failoverJobScript.getProductionServerName(), e);
			return false;
		} catch (Exception e) {
			logger.info("Exception occurred when validating user for the source machine " + failoverJobScript.getProductionServerName(), e);
		}

		return true;
	}
	
	private WebServiceClientProxy getD2DService(FailoverJobScript failoverJobScript, int connectTimeout, int requestTimeout) throws ServiceException {
		WebServiceClientProxy proxy = null;
		if (failoverJobScript.isVSphereBackup()) {
			proxy = WebServiceFactory.getFlassService(failoverJobScript.getVSphereproxyServer().getVSphereProxyProtocol(),
					failoverJobScript.getVSphereproxyServer().getVSphereProxyName(),
					Integer.parseInt(failoverJobScript.getVSphereproxyServer().getVSphereProxyPort()),
					ServiceInfoConstants.SERVICE_ID_D2D_R16_5, connectTimeout, requestTimeout);

			proxy.getServiceV2().validateUserByUUID(failoverJobScript.getVSphereproxyServer().getVSphereUUID());
		} else {
			proxy = WebServiceFactory.getFlassService(failoverJobScript.getProductionServerProtocol(),
					failoverJobScript.getProductionServerName(),
					Integer.parseInt(failoverJobScript.getProductionServerPort()),
					ServiceInfoConstants.SERVICE_ID_D2D_R16_5, connectTimeout, requestTimeout);
			
			proxy.getServiceV2().validateUserByUUID(failoverJobScript.getAgentUUID());
		}

		return proxy;
	}
	
	public WebServiceClientProxy getD2DService(FailoverJobScript failoverJobScript) throws ServiceException {
		return getD2DService(failoverJobScript, 0, 0);
	}
	
	public WebServiceClientProxy getConverterService(FailoverJobScript failoverJobScript) throws ServiceException {
		WebServiceClientProxy proxy = null;
		if (failoverJobScript.getBackupToRPS()) {
			proxy = WebServiceFactory.getFlassService(failoverJobScript.getConverterProtocol(),
					failoverJobScript.getConverterHostname(),
					failoverJobScript.getConverterPort(),
					ServiceInfoConstants.SERVICE_ID_D2D_R16_5,0,0);
	
			proxy.getServiceV2().validateUserByUUID(failoverJobScript.getConverterUUID());
		}
		else {
			proxy = getD2DService(failoverJobScript);
		}
		return proxy;
	}
	
	public BackupD2D getRPSPolicyUUID(String hostName,
			String userName, String password, int port, int protocolType, 
			String d2dHostUUID) throws ServiceException {

		String protocol = protocolType == 1 ? "http:" : "https:";

		IRPSService4D2D client = RPSServiceProxyManager.getRPSServiceClient(hostName, userName, 
				password, port, protocol, "");
		if(client !=  null){
			List<BackupD2D> d2ds = client.getRegistedClientList();

			for(BackupD2D d2d : d2ds) {
				if(StringUtil.isEmptyOrNull(d2d.getClientUUID()))
					continue;
				
				if(d2d.getClientUUID().equalsIgnoreCase(d2dHostUUID)){
					return d2d;
				}
			}
		}
		logger.error("Failed to get the RPS policy ID for the D2D host " + d2dHostUUID + " on the RPS server " + hostName);
		return null;
	}
	public RpsPolicy4D2D getRPSPolicy(String hostName,
			String userName, String password, int port, int protocolType, 
			String rpsPolicyUUID, boolean withDataStore) throws ServiceException {
		String protocol = protocolType == 1 ? "http:" : "https:";
		return SettingsService.instance().getRPSPolicy(hostName, userName, 
				password, port, protocol, rpsPolicyUUID, "", withDataStore);
	}
	
	public long checkMonitorJavaHeapSize() {
		return checkJavaHeapSize();
	}

	private void saveEmptyProductionServerRoot(String afguid) {
		try {
			String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
			ProductionServerRoot prodRoot = null;
			try {
				RepositoryUtil instance = RepositoryUtil.getInstance(xml);
				prodRoot = instance.getProductionServerRoot(afguid);
			}
			catch(HAException ex) {
				logger.info("ProductionServerRoot has not been set.");
			}
			
			if(prodRoot == null) {
				prodRoot = new ProductionServerRoot();
				prodRoot.setProductionServerAFUID(afguid);

				 RepositoryUtil.getInstance(xml).saveProductionServerRoot(prodRoot);
			}
		}
		catch(Exception e) {
			logger.warn("Fails to save empty ProductionServerRoot to repository.xml. Error msg:" + e.getMessage());
		}
	}

//	private void checkJavaHeapSize(List<VCMSavePolicyWarning> warningList){
//		if(getNativeFacade().isAdjustJavaHeapSize(ApplicationType.VirtualConversionManager)){
//			try {
//				String hostName = InetAddress.getLocalHost().getHostName();
//				VCMSavePolicyWarning savePolicyWarning = new VCMSavePolicyWarning(FlashServiceErrorCode.VCM_APPLY_POLICY_ADJUST_JAVA_HEAP, new String[]{ hostName});
//				warningList.add(savePolicyWarning);
//			} catch (Exception e)
//			{
//			}
//		}
//	}
	
	private long checkJavaHeapSize(){
		return getNativeFacade().isAdjustJavaHeapSize(ApplicationType.VirtualConversionManager) ? 1l : 0l;
	}
	
	public boolean setJobScriptComboEx(JobScriptCombo jobScript, VCMPolicyDeployParameters deployPolicyParameters, List<VCMSavePolicyWarning> warningList) {
		boolean firstVCMSettingFlag = false;  
		
		String afGuid = jobScript.getFailoverJobScript().getAFGuid();
		
		if(StringUtil.isEmptyOrNull(afGuid)){
			afGuid = retrieveCurrentNodeID();
			jobScript.getFailoverJobScript().setAFGuid(afGuid);
			jobScript.getHbJobScript().setAFGuid(afGuid);
			jobScript.getRepJobScript().setAFGuid(afGuid);
			jobScript.getAlertJobScript().setAFGuid(afGuid);
		}
		
		try {

			firstVCMSettingFlag = setJobScriptCombo(jobScript, deployPolicyParameters, warningList);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw AxisFault.fromAxisFault(e.getMessage());
		}
		
		try {
			//send failover job script to monitor
			FailoverJobScript failoverJobScript = getFailoverJobScript(afGuid);
			HeartBeatJobScript heartBeatJobScript = getHeartBeatJobScript(afGuid);
			WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);		
			if (deployPolicyParameters.isRPSNode()){
				logger.info("BackupToRPS: registerForHA to the monitor");
			}

			String failoverScriptXml = CommonUtil.marshal(failoverJobScript);			
			client.getServiceV2().registerForHA(failoverScriptXml,false);
			
			//register the alert job scirpt to monitor
			AlertJobScript alertJobScript = getAlertJobScript(afGuid);
			if(alertJobScript!=null) {
				String alertJobScriptXML = CommonUtil.marshal(alertJobScript);
				client.getServiceV2().registerAlertJobscript(alertJobScriptXML);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
			
		return firstVCMSettingFlag;
	}
	
	public int updateReplicationJobScript(ReplicationJobScript replicationJobScript,ADRConfigure adrConfigure) {
		int result = 1;
		if(adrConfigure == null) {
			logger.warn("The adrconfigure is null");
			return result;
		}
		
		//update the disk information
		SortedSet<Disk> backupDisks = adrConfigure.getDisks();
		List<DiskDestination> diskDestinations = replicationJobScript.getReplicationDestination().get(0).getDiskDestinations();
		VMStorage defaultStorage = diskDestinations.get(0).getStorage();
		
		List<DiskDestination> realDiskDestinations = new ArrayList<DiskDestination>();
		for (Disk backupDisk : backupDisks) {
			boolean isFoundDiskNumber = false;
			int diskNumber = backupDisk.getDiskNumber();
			for (DiskDestination diskDestination : diskDestinations) {
				DiskModel diskModel = diskDestination.getDisk();
				if(diskModel.getDiskNumber() == diskNumber) {
					isFoundDiskNumber = true;
					diskModel.setSignature(backupDisk.getSignature());
					diskModel.setDiskType(backupDisk.getDiskType());
					diskModel.setControllerType(backupDisk.getControllerType());
					diskModel.setPartitionType(backupDisk.getPartitionType());
					diskModel.setSize(backupDisk.getSize());
					
					realDiskDestinations.add(diskDestination);
					break;
				}
			}
			
			if(!isFoundDiskNumber) {
				DiskModel diskModel = new DiskModel();
				diskModel.setDiskNumber(backupDisk.getDiskNumber());
				diskModel.setSignature(backupDisk.getSignature());
				diskModel.setDiskType(backupDisk.getDiskType());
				diskModel.setControllerType(backupDisk.getControllerType());
				diskModel.setPartitionType(backupDisk.getPartitionType());
				diskModel.setSize(backupDisk.getSize());
				
				VMStorage storage = new VMStorage();
				storage.setName(defaultStorage.getName());
				
				DiskDestination des = new DiskDestination();
				des.setDisk(diskModel);
				des.setStorage(storage);
				realDiskDestinations.add(des);
			}
			
		}
		diskDestinations.clear();
		diskDestinations.addAll(realDiskDestinations);
		
		//update the network adapter informaiton
		//If monitee is physical d2d machine.
//		if(HACommon.isTargetPhysicalMachine(replicationJobScript.getAFGuid())){
//			SortedSet<NetworkAdapter> backupAdapters = adrConfigure.getNetadapters();
//			List<NetworkAdapter> configNetworkAdapters = replicationJobScript.getReplicationDestination().get(0).getNetworkAdapters();
//			int i = 0;
//			for (NetworkAdapter networkAdapter : backupAdapters) {
//				NetworkAdapter tempAdapter = null;
//				if(i< configNetworkAdapters.size()) {
//					tempAdapter = configNetworkAdapters.get(i);
//				}
//				else {
//					tempAdapter = configNetworkAdapters.get(0);
//				}
//				networkAdapter.setAdapterType(tempAdapter.getAdapterType());
//				networkAdapter.setNetworkLabel(tempAdapter.getNetworkLabel());
//				i++;
//			}
//			configNetworkAdapters.clear();
//			configNetworkAdapters.addAll(backupAdapters);
//		}
		
		updateRepJobScript(replicationJobScript);

		return 0;
	}
	
	public VMSnapshotsInfo[] GetHyperVVmSnapshotsByMonitor(String afGuid,String vmGuid){
		
		WebServiceClientProxy clientProxy = MonitorWebClientManager.getMonitorWebClientProxy(afGuid);
		if(clientProxy!=null) {
			return clientProxy.getServiceV2().GetHyperVVmSnapshots(vmGuid);
		}
		else {
			return null;
		}
	}
	
	public boolean isHyperVVMNameExist(WebServiceClientProxy clientProxy,String vmName, StringBuilder vmUuid) {
		//WebServiceClientProxy clientProxy = getMonitorSevice(heartBeatJobScript);
		if(clientProxy != null) {
			String vmGUID = clientProxy.getServiceV2().isHyperVVMNameExist(vmName);
			if (!StringUtil.isEmptyOrNull(vmGUID))
				vmUuid.append(vmGUID);
			return !StringUtil.isEmptyOrNull(vmGUID);
		}
		else {
			return false;
		}
	}
	
	
	public boolean detectIfFailoverOngoing(String afguid) {
		try {
			HeartBeatJobScript heartBeatJobScript = getHeartBeatJobScript(afguid);
			WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);
			return (client != null) ? client.getFlashServiceR16_5().isFailoverOngoing(afguid) : false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean isFailoverOngoing(String afguid) {
		FailoverJob job = HACommon.getFailoverJob(afguid);
		boolean ret=  (job != null) ? job.getPendingToRun() : false;
		logger.info("Is failover ongoing? " + (ret ? "yes." : "no."));
		return ret;
	}
	
	public boolean isIntegratedEdge(){
		boolean result = false;
		try {
			WindowsRegistry registry = new WindowsRegistry();
			int regHandle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			if( regHandle!= 0 ){
				String tagEdge = registry.getValue(regHandle, "IntegratedEdge");
				if ("1".equals(tagEdge)) {
					result = true;
				}
				else {
					result = false;
				}
			}
			registry.closeKey(regHandle);
		} catch (Exception e) {
		}
		
		return result;
	}
	
	public boolean setIntegratedEdge(String value) {
		boolean result = false;
		try {
			WindowsRegistry registry = new WindowsRegistry();
			int regHandle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			if( regHandle!= 0 ){
				registry.setValue(regHandle, "IntegratedEdge", value);
				result = true;
			}
			registry.closeKey(regHandle);
		} catch (Throwable e) {
			logger.error(e);
		}
		
		return result;
	}
	
	//D2D lock session API
	public long HALockD2DSessions(String sessionRoot, int startSession, int endSession, boolean isForRemoteVCM, List<Integer> resultCodeList) {
		try {
			
			long lockHandle = 0;
			int lastResultCode = 0;
			List<Integer> newResultCodeList = new ArrayList<Integer>();
			for (int i = 0; i < 6; i++) {
				
				newResultCodeList.clear();
				lockHandle = VMWareJNI.HALockD2DSessions(sessionRoot, startSession, endSession, isForRemoteVCM, newResultCodeList);

				resultCodeList.clear();
				resultCodeList.add( lastResultCode );
				if (newResultCodeList.size() >= 1)
					lastResultCode = newResultCodeList.get( 0 );
				else // no result code returned
					logger.error( "VMWareJNI.HALockD2DSessions() doesn't return result code." );
				
				if(lockHandle != 0){
					break;
				}
				//sleep 10 seconds
				Thread.sleep(10000);
			}
			return lockHandle;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return 0;
		}
		
	}
	
	public void HAUnlockD2DSessions(long handle) {
		if(handle == 0) {
			return;
		}
		try {
			VMWareJNI.HAUnlockD2DSessions(handle);
		} catch (Exception e) {
		}
	}
	
	public String evaluteAFGuid(String afGuid){
		if(StringUtil.isEmptyOrNull(afGuid)){
			return retrieveCurrentNodeID();
		}else{
			return afGuid;
		}
	}
	
		//VCM Alert function
	public void setAlertJobScript(AlertJobScript alertJobScript) {
		logger.debug("setAlertJobScript(AlertJobScript) - start"); //$NON-NLS-1$

		alertJobScript.setAlertCommand(new EmailAlertCommand());
		synchronized (jobQueue) {
			Collection<AFJob> jobs = jobQueue.findByJobType(JobType.Alert);
			for (AFJob job : jobs) {
				AlertJob alertJob = (AlertJob)job;
				if(alertJobScript.getAFGuid().equals(alertJob.getJobScript().getAFGuid())) {
					jobQueue.remove(alertJob);
				}
			}
			jobQueue.add(alertJobScript);
		}

		logger.debug("setAlertJobScript(AlertJobScript) - end"); //$NON-NLS-1$
	}

	public AlertJobScript getAlertJobScript(String afguid) {
		logger.debug("getAlertJobScript() - start"); //$NON-NLS-1$

		String localAfguid = evaluteAFGuid(afguid);
		synchronized (jobQueue) {
			Collection<AFJob> jobs = jobQueue.findByJobType(JobType.Alert);
			for (AFJob job : jobs) {
				AlertJob alertJob = (AlertJob)job;
				if(alertJob.getJobScript().getAFGuid().equals(localAfguid)) {
					AlertJobScript alertJobScript = alertJob.getJobScript();
					return alertJobScript;
				}
			}
		}

		logger.debug("getAlertJobScript() - end"); //$NON-NLS-1$
		return null;
	}
	public AFJob getAlertJob(String afguid) {
		logger.debug("getAlertJob() - start"); //$NON-NLS-1$

		synchronized (jobQueue) {
			Collection<AFJob> jobs = jobQueue.findByJobType(JobType.Alert);
			for (AFJob job : jobs) {
				AlertJob alertJob = (AlertJob)job;
				if(alertJob.getJobScript().getAFGuid().equals(afguid)) {
					return alertJob;
				}
			}
		}

		logger.debug("getAlertJob() - end"); //$NON-NLS-1$
		return null;
	}
	
	public int sendAlertMail(AlertType alertType,  String afguid, long jobID) {
		try {
			String alertMessage = VirtualConversionEmailAlertUtil.getAlertMessage(alertType);
			return VirtualConversionEmailAlertUtil.sendAlertMail(alertType, alertMessage, alertMessage, afguid, jobID, null);
		}
		catch(Exception e) {
			logger.error("Error occurs while sending email alert.", e);
		}
		
		return -1;
	}
	
	public int sendAlertMailWithParameters(String afguid, long jobID, AlertType alertType, Date executeTime, Object... pars ) {
		try {
			String messageFormat = VirtualConversionEmailAlertUtil.getAlertMessage(alertType);
			String alertMessage = String.format(messageFormat, (Object[])pars);
			return VirtualConversionEmailAlertUtil.sendAlertMail(alertType,alertMessage, alertMessage, afguid, jobID, executeTime);
		}
		catch(Exception e) {
			logger.error("Error occurs while sending email alert.", e);
		}
	
		return -1;
	}
	
	public int sendAlertMail(String afguid, long jobID, AlertType alertType, Object... pars) {
		try {
			String alertMessage = "";
			
			if((alertType == AlertType.ConversionFailed || alertType == AlertType.ConversionSuccess
				|| alertType == AlertType.FailoverFailure || alertType == AlertType.CancelConversionJob) 
				&& pars != null && pars.length > 0)
				alertMessage = pars[0].toString();
			else {
				String messageFormat = VirtualConversionEmailAlertUtil.getAlertMessage(alertType);
				alertMessage = String.format(messageFormat, (Object[])pars);
			}
			
			return VirtualConversionEmailAlertUtil.sendAlertMail(alertType,alertMessage, alertMessage, afguid, jobID, null);
		}
		catch(Exception e) {
			logger.error("Error occurs while sending email alert.", e);
		}
	
		return -1;
	}
	
	public int registerAlertJobscript(String alertJobscriptStr) {
		try {
			AlertJobScript alertJobScript = CommonUtil.unmarshal(alertJobscriptStr, AlertJobScript.class);
			if(alertJobScript == null) {
				logger.error("The alert job script is null from the monitee");
				return 1;
			}
			setAlertJobScript(alertJobScript);
			
		} catch (Exception e) {
			logger.error(e);
			return 1;
		}
		return 0;
	}
	
	public int unRegisterAlertJobscript(String afguid) {
		logger.debug("unRegisterAlertJobscript() - start"); //$NON-NLS-1$

		synchronized (jobQueue) {
			Collection<AFJob> jobs = jobQueue.findByJobType(JobType.Alert);
			for (AFJob job : jobs) {
				AlertJob alertJob = (AlertJob)job;
				if(alertJob.getJobScript().getAFGuid().equals(afguid)) {
					jobQueue.remove(alertJob);
				}
			}
		}

		logger.debug("unRegisterAlertJobscript() - end"); //$NON-NLS-1$
		return 0;
	}
	
	public boolean isSetEmail(EmailModel emailModel) {
		if(emailModel == null) {
			return false;
		}
		
		if(StringUtil.isEmptyOrNull(emailModel.getSMTP())) {
			return false;
		}
		
		return true;
	}
	public int checkAlertEmailSetting(String afguid) {
		int result = 0;
		AlertJobScript alertJobScript = getAlertJobScript(afguid);
		if(alertJobScript == null) {
			return 1;
		}
		
		if(!isSetEmail(alertJobScript.getEmailModel())) {
			try {
				PreferencesConfiguration preferencesConfiguration = CommonService.getInstance().getPreferences();
				if(preferencesConfiguration == null) {
					return 1;
				}
				BackupEmail model = preferencesConfiguration.getEmailAlerts();
				if(model==null || model.isEnableSettings()) {
					return 1;
				}
				
				EmailModel emailModel = new EmailModel();
				emailModel.setContent("");
				boolean isEnableProxy = model.isEnableProxy();
				emailModel.setEnableProxy(isEnableProxy);
				if(isEnableProxy) {
					emailModel.setProxyAddress(model.getProxyAddress());
					emailModel.setProxyPassword(model.getProxyPassword());
					emailModel.setProxyUsername(model.getProxyUsername());
					emailModel.setProxyPort(model.getProxyPort());
				}
			
				emailModel.setMailPassword(model.getMailPassword());
				emailModel.setUseSsl(model.isEnableSsl());
				emailModel.setSmptPort(model.getSmtpPort());
				emailModel.setMailUser(model.getMailUser());
				emailModel.setUseTls(model.isEnableTls());
				emailModel.setMailAuth(model.isMailAuth());
				emailModel.setSMTP(model.getSmtp());
				emailModel.setSubject(model.getSubject());
				emailModel.setFromAddress(model.getFromAddress());
				emailModel.setHtmlFormat(model.isEnableHTMLFormat());
				String[] recipients = model.getRecipientsAsArray();
				emailModel.setRecipients(recipients);
				
				alertJobScript.setEmailModel(emailModel);
				
				String alertJobscriptStr = CommonUtil.marshal(alertJobScript);
				WebServiceClientProxy clientProxy = MonitorWebClientManager.getMonitorWebClientProxy(afguid);
				result = clientProxy.getServiceV2().registerAlertJobscript(alertJobscriptStr);
				if(result == 0) {
					logger.debug("Successfully register the alert job script");
				}
				else {
					logger.debug("Failed to register the alert job script");
				}
				
			} catch (Exception e) {
				logger.error(e);
			}
		}
		
		return result;
	}
	
	public synchronized boolean isIncrementalBackupSubmitted(String afGuid, long jobID, boolean useGUID) throws HAException, ServiceException{
		
		//only on a D2D machine can incremental backup be submitted. 
		//if(!HAService.getInstance().retrieveCurrentUUID().equals(afGuid))
		//	return false;
		
		String submitted;
		try {
			submitted = CommonUtil.getSubmitIncrementalFlag(afGuid, useGUID);
		} catch (Exception e) {
			return false;
		}
		
		if(StringUtil.isEmptyOrNull(submitted)){
			return false;
		}
		
		if(INCREMENTAL_COMPLETE.equals(submitted)){
			return false;
		}
		return true;
		
	}
	
	public synchronized long submitIncrementalBackup(String afGuid, int sessionLength, boolean firstVCMSettingFlag) {
		
		if (firstVCMSettingFlag){
			try {
				logger.info("Set FirstVCMSettingFlag for vistual standby on monitee.");
				CommonUtil.setFirstVCMSettingFlag(true);
			} catch (Exception e) {
				logger.error("Fail to setFirstVCMSettingFlag for vistual standby.");
			}
		}

		return 2;
//		//If sessions are backed up preceding VCM setting,
//		//These sessions needs to be smart copied with following incrmental session.
//		if (sessionLength > 0 || BaseBackupJob.isJobRunning()) {
//			long ret = 0;
//			logger.info("Submit incremental backup job for Visual Standby");
//			try {
//				HACommon.submitBackupJob(afGuid);
//			} catch (ServiceException se) {
//				if (FlashServiceErrorCode.Common_OtherJobIsRunning.equals(se.getErrorCode())) {
//					logger.warn("Another backup job is running. ");
//				}else{
//					logger.error(String.format("Fail to submit incremental backup job: %s. Error code %s. ",
//								se.getMessage(), se.getErrorCode()));
//					ret = 1; // Failed to submit backup
//				}
//
//			} catch (Exception e) {
//				logger.error(String.format("Fail to submit incremental backup job: %s. ",
//						e.getMessage()));
//				ret = 1;  // Failed to submit backup
//			}
//			return ret; 
//		} else {
//			logger.info("Find no sessions to be converted, so don't submit the backup job!");
//			return 2; // backup not submitted
//		}
	}
	
	public synchronized boolean isIncrmentalBackupComplete(ReplicationJobScript jobScript){
		
		String afGuid = jobScript.getAFGuid();
		boolean useGUID = jobScript.getBackupToRPS() || jobScript.isVSphereBackup();
		String submitted;
		try {
			submitted = CommonUtil.getSubmitIncrementalFlag(afGuid, useGUID);
		} catch (Exception e) {
			return false;
		}
		if(INCREMENTAL_COMPLETE.equals(submitted)){
			return true;
		}else{
			return false;
		}
		
	}
	
	public synchronized void clearIncrementalBackupComplete(ReplicationJobScript jobScript){
		
		String afGuid = jobScript.getAFGuid();
		boolean useGUID = jobScript.getBackupToRPS() || jobScript.isVSphereBackup();
		try {
			CommonUtil.clearSubmitIncrementalFlag(afGuid, useGUID);
		} catch (Exception e) {
		}
	}

	public String getJobScriptCombo(String afGuid) {
		String localAFGuid = HAService.getInstance().evaluteAFGuid(afGuid);
		long basecur = System.currentTimeMillis();
		JobScriptCombo jobScript = getJobScriptComboObject(localAFGuid);
		long cur2 = System.currentTimeMillis();
		long cur3 = 0;
		try {

			String str = CommonUtil.marshal(jobScript);
			cur3 =  System.currentTimeMillis();
			if(logger.isDebugEnabled())
				logger.debug("getJobScriptCombo: get script time:"+(cur2 - basecur)  + " marshal time:"+ (cur3-cur2));

			return str;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	private JobScriptCombo getJobScriptComboObject(String localAFGuid) {
		FailoverJobScript failoverJobScript = HAService.getInstance().getFailoverJobScript(localAFGuid);
		HeartBeatJobScript heartBeatJobScript = HAService.getInstance().getHeartBeatJobScript(localAFGuid);
		ReplicationJobScript script = HAService.getInstance().getReplicationJobScript(localAFGuid);
		
		if(script != null){
			
			ReplicationDestination dest = script.getReplicationDestination().get(0);
			
			try{
				
				if(dest instanceof ARCFlashStorage){
					String xml = CommonUtil.getRepositoryConfPath();
					RepositoryUtil repository  = RepositoryUtil.getInstance(xml);
					String subRoot = repository.getProductionServerRoot(localAFGuid).getReplicaRoot().getRepSubRoot();
					if(!StringUtil.isEmptyOrNull(subRoot)){
						List<DiskDestination> diskDests = dest.getDiskDestinations();
						String path = "";
						if(!subRoot.endsWith("\\")){
							subRoot += "\\";
						}
						for (DiskDestination diskDestination : diskDests) {
							path = diskDestination.getStorage().getName();
							if(!path.endsWith("\\")){
								path += "\\";
							}
							if(path.endsWith(subRoot)){
								path = path.substring(0, path.length()-subRoot.length());
								diskDestination.getStorage().setName(path);
							}
						}
					}
				}
				
			}catch (Exception e) {}
			
		}
		
		AlertJobScript alertJobScript = HAService.getInstance().getAlertJobScript(localAFGuid);
		JobScriptCombo jobScript = new JobScriptCombo();
		jobScript.setFailoverJobScript(failoverJobScript);
		jobScript.setHbJobScript(heartBeatJobScript);
		jobScript.setRepJobScript(script);
		jobScript.setAlertJobScript(alertJobScript);
		return jobScript;
	}
	
	public VCMConfigStatus getVCMConfigStatus (String vmInstanceUUID) {
		VCMConfigStatus status = new VCMConfigStatus(); 
		JobScriptCombo jobScript = getJobScriptComboObject(vmInstanceUUID);
		if(jobScript != null && jobScript.getFailoverJobScript() != null 
							&& jobScript.getHbJobScript() != null && jobScript.getRepJobScript() != null 
							&& !jobScript.getFailoverJobScript().getBackupToRPS())
			status.setVcmConfigured(true);
		
		if (jobScript != null && jobScript.getRepJobScript() != null) {
			if (ManualConversionUtility.isVSBWithoutHASupport(jobScript.getRepJobScript())) {
				status.setVcmConfigured(true);
			}
		}
		
		ARCFlashNodesSummary summary = getARCFlashNodesSummary();
		if (summary.getNodes().length > 1 || (!status.isVcmConfigured() && summary.getNodes().length == 1)
				|| MonitorWebClientManager.isRVCMMonitorLocalhost(jobScript.getHbJobScript())) {
			status.setMonitor(true);
		}
		
		return status;
	}
	
	public boolean HAIsHostOSGreaterEqual(int dwMajor,int dwMinor,short servicePackMajor,
			 short servicePackMinor) {
		NativeFacade facade = BackupService.getInstance().getNativeFacade();
		try {
			return facade.HAIsHostOSGreaterEqual(dwMajor, dwMinor, servicePackMajor, servicePackMinor);
		} catch (Exception e) {
			logger.error(e);
			return true;
		}
		
	}
	
	public int[] checkPathIsSupportHyperVVM(String[] allPaths){
		if((allPaths == null)||(allPaths.length ==0)){
			return new int[]{0};
		}
		int[] results = new int[allPaths.length];
		for (int i=0; i< allPaths.length; i++) {
			results[i] = 0;
		}
		
		for (int i=0; i< allPaths.length; i++) {
			
			int folderAttribute = CheckFolderCompressAttribute(allPaths[i]);
			if(folderAttribute > 0){
				results[i] = results[i] | folderAttribute;
				String msg = String.format("The folder [%s] attribute is %d", allPaths[i], results[i]);
				logger.info(msg);
				break;
			}
			int volumeAttribute = CheckVolumeCompressAttribute(allPaths[i]);
			if(volumeAttribute>0 ){
				results[i] = results[i] | volumeAttribute;
				String msg = String.format("The volume [%s] attribute is %d", allPaths[i], results[i]);
				logger.info(msg);
				break;
			}
		}
		
		return results;
	}
	public int CheckFolderCompressAttribute(String folderPath){
		NativeFacade facade = BackupService.getInstance().getNativeFacade();
		try {
			return facade.CheckFolderCompressAttribute(folderPath);
		} catch (Exception e) {
			logger.error(e);
			return 0;
		}
	}
	
	public int CheckVolumeCompressAttribute(String volumePath){
		NativeFacade facade = BackupService.getInstance().getNativeFacade();
		try {
			return facade.CheckVolumeCompressAttribute(volumePath);
		} catch (Exception e) {
			logger.error(e);
			return 0;
		}
	}

	/**
	 * Returns the machine type according to the type of the machine and user settings. 
	 * 
	 * @return 1. {@link MachineType#PHYSICAL} if the current machine is physical machine, 
	 * 				or if the current machine is ESX VM while user does not specify ESX/VC server
	 * 				details.  
	 * 	 	   2. {@link MachineType#ESX_VM} if the current machine is ESX VM and user specifies
	 * 				ESX/VC server details.
	 * 		   3. {@link MachineType#HYPERV_VM} if  if the current machine is Hyper-V VM
	 */
	public MachineType getMachineType() {
		MachineDetail detail = MachineDetailManager.getInstance().getMachineDetail();
		
		return detail.getMachineType();
//		String hyperVName = CommonUtil.hyperVHostNameIfVM();
//		if(!StringUtil.isEmptyOrNull(hyperVName))
//			return MachineType.HYPERV_VM;
//		else if(CommonUtil.isESXServerVM()){
//			return MachineType.ESX_VM;
//		}
//			
//		return MachineType.PHYSICAL;
	}

	public MachineDetail updateMachineDetail(MachineDetail detail) throws Exception{
		if(detail == null)
			return null;
		
//		if(!CommonUtil.isESXServerVM())
//		{
//			logger.error("This machine is not a ESX VM. CommonUtil.isESXServerVM():" + CommonUtil.isESXServerVM());
//			throw new ServiceException("This machine is not a ESX VM",
//					FlashServiceErrorCode.VCM_MACHINE_ISNOT_ESXVM);
//		}
				
		VM_Info vmInfo = VCMLicenseCheck.validateVMByHostName(detail);
		if(vmInfo == null) {
			int vmWareTool = getNativeFacade().getVMwareToolStatus();
			logger.info("VMware tools status in this machine:" + vmWareTool);
			if((vmWareTool & 2) > 0) {
				logger.error("VMware tools is running in this machine, But vm does not exist on the specified ESX server:" + detail);
				throw new ServiceException("vm does not exist on the specified ESX server",
						FlashServiceErrorCode.VCM_VM_DOESNOT_EXIST_ON_ESX);
			}
			else if((vmWareTool & 1) > 0){
				logger.error("VMware tools is not running in this machine");
				throw new ServiceException("VMware tools is not running in this machine",
						FlashServiceErrorCode.VCM_MACHINE_VMWARE_TOOLS_NOT_RUNNING);
			}
			else {
				logger.error("VMware tools is not installed in this machine");
				throw new ServiceException("VMware tools is not installed in this machine",
						FlashServiceErrorCode.VCM_MACHINE_VMWARE_TOOLS_NOT_INSTALLED);
			}
		}
		
		detail.setVmName(vmInfo.getVMName());
		detail.setInstanceUuid(vmInfo.getVMvmInstanceUUID());
		
		MachineDetailManager.getInstance().setMachineDetail(detail);

		return detail;
	}
	
	private boolean isVCMDestinationChange(ReplicationJobScript newRepJobScript){
		
		if(newRepJobScript == null){
			return false;
		}

		boolean isMSPManualConversion = ManualConversionUtility.isVSBWithoutHASupport(newRepJobScript);
		boolean isBackupToRPS = newRepJobScript.getBackupToRPS();
		boolean isVSphereBackup = newRepJobScript.isVSphereBackup();

		boolean standaloneD2D = false; // including Standalone D2D server backup to sharefolder and RPS
		standaloneD2D = HACommon.isTargetPhysicalMachine(newRepJobScript.getAFGuid()) || (isBackupToRPS && !isMSPManualConversion && !isVSphereBackup);
		if(!standaloneD2D){
			return false;
		}
		
		ReplicationJobScript oldRepJobScript = getReplicationJobScript(newRepJobScript.getAFGuid());
		
		if(oldRepJobScript == null){
			return true;
		}
		
		logger.info("Check if VCM destination is changed.");
		ReplicationDestination oldDest = oldRepJobScript.getReplicationDestination().get(0);
		ReplicationDestination newDest = newRepJobScript.getReplicationDestination().get(0);
		
		if(newDest instanceof ARCFlashStorage){
			
			if(oldDest instanceof VMwareESXStorage || oldDest instanceof VMwareVirtualCenterStorage){
				return true;
			}
			
			ARCFlashStorage oldStorage = (ARCFlashStorage)oldDest;
			ARCFlashStorage newStorage = (ARCFlashStorage)newDest;
			
			if(!oldStorage.getHostName().equals(newStorage.getHostName())){
				return true;
			}
			
			if(!oldStorage.getVirtualMachineDisplayName().equals(newStorage.getVirtualMachineDisplayName())){
				return true;
			}
			
		}else if(newDest instanceof VMwareESXStorage){
			
			VMwareESXStorage newStorage = (VMwareESXStorage)newDest;
			
			if(oldDest instanceof ARCFlashStorage){
				return true;
			}
			
			else if(oldDest instanceof VMwareVirtualCenterStorage){
				VMwareVirtualCenterStorage oldStorage = (VMwareVirtualCenterStorage)oldDest;
				if(!newStorage.getESXHostName().equals(oldStorage.getEsxName())){
					return true;
				}
				
				if(!newStorage.getVirtualMachineDisplayName().equals(oldStorage.getVirtualMachineDisplayName())){
					return true;
				}
				
			}
			
			else if(oldDest instanceof VMwareESXStorage){
				
				VMwareESXStorage oldStorage = (VMwareESXStorage)oldDest;
				if(!newStorage.getESXHostName().equals(oldStorage.getESXHostName())){
					return true;
				}
				
				if(!oldStorage.getVirtualMachineDisplayName().equals(newStorage.getVirtualMachineDisplayName())){
					return true;
				}
			}
			
		}else if(newDest instanceof VMwareVirtualCenterStorage){
			
			VMwareVirtualCenterStorage newStorage = (VMwareVirtualCenterStorage)newDest;
			
			if(oldDest instanceof ARCFlashStorage){
				return true;
			}
			
			
			else if(oldDest instanceof VMwareESXStorage){
				VMwareESXStorage oldStorage = (VMwareESXStorage)oldDest;
				if(!oldStorage.getESXHostName().equals(newStorage.getEsxName())){
					return true;
				}
				
				if(!newStorage.getVirtualMachineDisplayName().equals(oldStorage.getVirtualMachineDisplayName())){
					return true;
				}
				
			}
			
			else if(oldDest instanceof VMwareVirtualCenterStorage){
				
				VMwareVirtualCenterStorage oldStorage = (VMwareVirtualCenterStorage)oldDest;
				
				if(!oldStorage.getVirtualCenterHostName().equals(newStorage.getVirtualCenterHostName())){
					return true;
				}
				
				if(!oldStorage.getVirtualMachineDisplayName().equals(newStorage.getVirtualMachineDisplayName())){
					return true;
				}
				
				if (!oldStorage.getDcName().equalsIgnoreCase(newStorage.getDcName())) {
					return true;
				}
			}
			
		}
		
		return false;
		
		
		
	}
	
	public int deregisterForHA(String afGuid, String converterID) {

		logger.info("deregisterForHA for " + afGuid);
		
		synchronized(jobQueue){
			FailoverJob job = (FailoverJob) getFailoverJob(afGuid);
			if(job == null)
				return 1;
			FailoverJobScript jobScript = job.getJobScript();

			logger.info(String.format("The input converterID is %s and the converterID in failover jobscript is %s", converterID, jobScript.getConverterID()));
			if (converterID != null && jobScript.getConverterID() != null && !converterID.equalsIgnoreCase(jobScript.getConverterID())) {
				logger.warn("The input converterID and the converterID in hbjobscript are different, so skip deregisterForHA for " + afGuid);
			} else {
				jobQueue.remove(job);
				//deregister heartbeat
				HeartBeatModelManager.deRegisterHeartBeat(afGuid);
				HeartBeatModelManager.setHeartBeatState(afGuid,HeartBeatJobScript.STATE_UNREGISTERED);
				
				if (!ManualConversionUtility.isVSBWithoutHASupport(jobScript)) {
					String msg = WebServiceMessages.getResource(
							COLDSTANDBY_SETTING_STOPHEARTBEAT_MONITOR,jobScript.getProductionServerName());
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, 
											Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "",
													"", "", "" }, afGuid);
				}
			}
			
			AlertJob alertJob = (AlertJob) getAlertJob(afGuid);
			if(alertJob != null) {
				AlertJobScript alertJobScript = alertJob.getJobScript();
				if (converterID != null && alertJobScript.getConverterID() != null && !converterID.equalsIgnoreCase(alertJobScript.getConverterID())) {
					logger.warn("The input converterID and the converterID in alert jobscript are different, so skip delete alert job script for " + afGuid);
				} else {
					jobQueue.remove(alertJob);
				}
			}
		}

		return 0;
	}
	
	public String[] deleteMonitorHartFiles(String moniteeName,String sessionName){
		
		
		String hartFileFolder ;
		if(StringUtil.isEmptyOrNull(sessionName)){
			hartFileFolder = CommonUtil.D2DInstallPath + "HART\\" + moniteeName + "\\VStore"; 
		}else{
			hartFileFolder = CommonUtil.D2DInstallPath + "HART\\" + moniteeName + "\\VStore\\" + sessionName;
		}
		
		logger.info("hartFileFolder=" + hartFileFolder);
		
		String[] EMPTY = new String[0];
		
		File sessionFolder = new File(hartFileFolder);
		
		if(!sessionFolder.exists()){
			return EMPTY;
		}
		
		List<String> undeleteFiles = new ArrayList<String>();

		if(StringUtil.isEmptyOrNull(sessionName)){
			
			File[] allSessions = sessionFolder.listFiles();
			
			for (File singleSession : allSessions) {
				
				File[] hartFiles = singleSession.listFiles(new FileFilter() {
					@Override
					public boolean accept(File file) {
						return file.getName().endsWith(".hart");
					}
				});
				
				for (File file : hartFiles) {
					if(!file.delete()){
						undeleteFiles.add(file.getName());
					}
				}
			}
			
		}else{
			
			File[] hartFiles = sessionFolder.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File file) {
					return file.getName().endsWith(".hart");
				}
			});
			
			if(hartFiles == null || hartFiles.length == 0){
				return EMPTY;
			}
			
			for (File file : hartFiles) {
				if(!file.delete()){
					undeleteFiles.add(file.getName());
				}
			}
			
		}
		
		return undeleteFiles.toArray(EMPTY);
		
	}
	
	public boolean checkMonitorHartFilesExistence(String moniteeName,String sessionName){
		
		String hartFileFolder = CommonUtil.D2DInstallPath + "HART\\" + moniteeName + "\\VStore\\" + sessionName; 
		
		logger.info("hartFileFolder=" + hartFileFolder);
		
		File sessionFolder = new File(hartFileFolder);
		
		if(!sessionFolder.exists()){
			return false;
		}
		
		File[] hartFiles = sessionFolder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(".hart");
			}
		});
		
		if(hartFiles == null || hartFiles.length == 0){
			return false;
		}
		
		return true;
		
	}
	
	public boolean checkResourcePoolExist(String esxServer, String username,
			String passwod, String protocol, int port, VWWareESXNode host, String resPoolRef)
	{
		logger.debug("checkResourcePoolExist(String, String, String, String, int, VWWareESXNode) - start"); //$NON-NLS-1$

		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
					.getCAVMwareVirtualInfrastructureManager(esxServer,
							username, passwod, protocol, true, port);
			if (vmwareOBJ == null) {
				logger.error("the esx connection failed");
				return false;
			}

			ESXNode esxNode = new ESXNode();
			esxNode.setDataCenter(host.getDataCenter());
			esxNode.setEsxName(host.getEsxName());
	
			return vmwareOBJ.checkResPool(esxNode, resPoolRef);
		} catch (Exception e) {
			logger.error(e);
			throw  AxisFault.fromAxisFault("Failed to checkResPool!!!");
		}
		finally {
			if(vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				}
				catch(Exception e) {
				}
			}
		}
	}
	
	public boolean checkResourcePoolExist(CAVirtualInfrastructureManager vmwareOBJ, String dataCenter,String esxName,String poolName, String poolRef)
	{

		try {
			
			if(StringUtil.isEmptyOrNull(poolRef)){
				return true;
			}
			
			String msg = String.format("Begin to check the resource pool: poolName[%s] poolRef[%s] dataCenter[%s] esxName[%s]",
					poolName,poolRef,dataCenter,esxName);
			logger.debug(msg);
			
			ESXNode esxNode = new ESXNode();
			esxNode.setDataCenter(dataCenter);
			esxNode.setEsxName(esxName);
			return vmwareOBJ.checkResPool(esxNode, poolRef);
		} catch (Exception e) {
			logger.error(e);
			throw  AxisFault.fromAxisFault("Failed to checkResPool!!!");
		}
	}
	
	public int installVDDKService(){
		return VDDKService.getInstance().install();
	}
	
	//The method that vcm monitor calls 
	public void removeMonitee(String afGuid) {
		
		VirtualizationType virtType = null;
		FailoverJobScript jobScript = getFailoverJobScript(afGuid);
		
		if(jobScript != null)
			virtType = jobScript.getVirtualType();
		
		deregisterForHA(afGuid, null);
		
		removeReplicatedInfo(afGuid, virtType, null);
	}
	
	public int RefreshBackupConfigSettingWithEdge(String afGuid, String planUUID){

		boolean isSameConverter = checkConverterSameForNode(planUUID, afGuid);
		if (!isSameConverter) {
			logger.warn(WebServiceMessages.getResource("autoUnassignPolicy"));
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, new String[] { 
					WebServiceMessages.getResource("autoUnassignPolicy"), "", "", "", "" }, afGuid);
			
			autoUnapplyVCMPolicy(afGuid);
			return PolicyCheckStatus.NOPOLICY;
		}

		int status = checkPolicyFromEdge(planUUID, afGuid, false);
		switch(status){
		case PolicyCheckStatus.UNKNOWN:
		case PolicyCheckStatus.SAMEPOLICY:
		case PolicyCheckStatus.POLICYDEPLOYING: 
			// status
			return status;
		case PolicyCheckStatus.NOPOLICY:
			// unassigned policy
			logger.warn(WebServiceMessages.getResource("autoUnassignPolicy"));
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, new String[] { 
					WebServiceMessages.getResource("autoUnassignPolicy"), "", "", "", "" }, afGuid);
			
			autoUnapplyVCMPolicy(afGuid);
			return status;
		case PolicyCheckStatus.DIFFERENTPOLICY:
			logger.warn(WebServiceMessages.getResource("autoRedeployPolicy"));
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, new String[] { 
					WebServiceMessages.getResource("autoRedeployPolicy"), "", "", "", "" }, afGuid);
			return status;
		case PolicyCheckStatus.POLICYFAILED:
			logger.warn(WebServiceMessages.getResource("autoRedeployFailedPolicy"));
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, new String[] { 
					WebServiceMessages.getResource("autoRedeployFailedPolicy"), "", "", "", "" }, afGuid);
			return status;
		}
		return status;
	}
	
	private int checkPolicyFromEdge(String policyUuid, String d2duuid, boolean justcheck) {
		EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.VirtualConversionManager);
		if(edgeRegInfo == null || edgeRegInfo.getEdgeWSDL() == null || edgeRegInfo.getEdgeWSDL().isEmpty())
			return PolicyCheckStatus.UNKNOWN;

		logger.info(String.format("Check the policy[%s] status for node %s in CPM [%s]", 
						policyUuid, d2duuid, edgeRegInfo.getEdgeHostName()));
		try{
			IEdgeCM4D2D service = com.ca.arcflash.webservice.toedge.WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(),IEdgeCM4D2D.class);
			
			try {
				service.validateUserByUUID(edgeRegInfo.getEdgeUUID());
			}catch(EdgeServiceFault e) {
				logger.error("checkPolicyFromEdge - Failed to establish connection to CPM(login failed)");
				return PolicyCheckStatus.UNKNOWN;
			}
			
			try {
				int state= service.checkPolicyStatus(d2duuid, policyUuid, justcheck);
				logger.info(String.format("The policy status for node %s is %d", d2duuid, state));
				return state;
			} catch (EdgeServiceFault e) {
				logger.error("checkPolicyFromEdge - Failed to check policy status from CPM", e);
				return PolicyCheckStatus.UNKNOWN;
			}
		}catch(Exception e){
			logger.error("Failed to check policy status from CPM", e);
			return PolicyCheckStatus.UNKNOWN;
		}
	}
	
	private void startReplicationDirectlyForRemoteVSB(final String afGuid) {	
		Thread remoteVSBThread = new Thread(new Runnable() {

			@Override
			public void run() {

				ReplicationJobScript repJobScript = getReplicationJobScript(afGuid);
				if (repJobScript == null)
					return;
				
				if (!ManualConversionUtility.isVSBWithoutHASupport(repJobScript))
					return;
				
				logger.info("Start remote VSB job immediately after policy deployed.");
				String planUUID = repJobScript.getPlanUUID();

				// wait at most about (times*millis/1000) seconds
				int times = 20;
				long millis = 6000;
				while(times -- > 0){
					try {
						Thread.sleep(millis);
					} catch (InterruptedException e) {
					}
					boolean waitMoreTime = false;
					int status = checkPolicyFromEdge(planUUID, afGuid, false);
					switch(status){
					case PolicyCheckStatus.POLICYDEPLOYING: 
						logger.info(String.format("The status of plan %s is under deploying, wait another round", planUUID));
						waitMoreTime = true;
						break;
					default:
						break;
					}
					
					if (!waitMoreTime)
						break;
				}

				HAService.getInstance().startReplication(afGuid);
			}
		});
		CommonService.getInstance().getUtilTheadPool().submit(remoteVSBThread);
	}

	private void autoUnapplyVCMPolicy(final String afGuid) {
		Thread autoUnapplyThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try
				{
					logger.info( "Start to unapply VCM policy for node: " + afGuid);
					VCMPolicyDeployParameters deployParameters = new VCMPolicyDeployParameters();
					deployParameters.setInstanceUuid( afGuid );
					String parameter = CommonUtil.marshal( deployParameters );
					
					List<PolicyDeploymentError> errorList = new ArrayList<PolicyDeploymentError>();
					
					ReplicationJobScript replicationJobScript = getReplicationJobScript( afGuid );
					if (replicationJobScript != null) {
						int policyType = ManualConversionUtility.isVSBWithoutHASupport(replicationJobScript) ?
								ID2DPolicyManagementService.PolicyTypes.RemoteVCM : ID2DPolicyManagementService.PolicyTypes.VCM;
							PolicyApplyerFactory.createPolicyApplyer(policyType)
								.unapplyPolicy(errorList, false, parameter);
					}
				}
				catch (Exception e) {
					logger.error( "Failed to unapply VCM policy for node: " + afGuid, e );
				}
			}
		});
		CommonService.getInstance().getUtilTheadPool().submit(autoUnapplyThread);
	}
	
	private boolean checkConverterSameForNode(String policyUuid, String d2duuid) {
		if (StringUtil.isEmptyOrNull(policyUuid))
			return true;
		
		EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.VirtualConversionManager);
		if(edgeRegInfo == null || edgeRegInfo.getEdgeWSDL() == null || edgeRegInfo.getEdgeWSDL().isEmpty())
			return true;

		try{
			IEdgeD2DService service = com.ca.arcflash.webservice.toedge.WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(),IEdgeD2DService.class);
			
			try {
				service.validateUserByUUID(edgeRegInfo.getEdgeUUID());
			}catch(EdgeServiceFault e) {
				logger.error("checkConverterSameForNode - Failed to establish connection to Edge Server(login failed)");
				return true;
			}
			
			String localNodeID = retrieveCurrentNodeID();
			D2DInfo d2dInfo = new D2DInfo();
			d2dInfo.setType(D2DType.VSB);
			d2dInfo.setNodeUuid(d2duuid);
			d2dInfo.setInstanceUuid(d2duuid);
			d2dInfo.setPlanUuid(policyUuid);
			d2dInfo.setConverterUuid(localNodeID);
			
			logger.info(String.format("Check the D2D status for node %s in CPM [%s]", d2duuid, edgeRegInfo.getEdgeHostName()));
			D2DStatus status = service.checkD2DStatus(d2dInfo);
			logger.info(String.format("The D2D status for node %s is %s", d2duuid, status.toString()));
			
			// for VSB case, only D2DStatus.Ok and D2DStatus.VSBConverterChanged will be returned
			boolean state = status != D2DStatus.VSBConverterChanged;
//			boolean state = service.isConverterSameForNode(localNodeID, d2duuid);
			logger.info(String.format("The converter is %s for node %s in CPM", state ? "same" : "different", d2duuid));
			return state;
		}catch(Exception e){
			logger.error("Failed to check the D2D status for node " + d2duuid, e);
			return true;
		}
	}
	
	public boolean ifNeedDeployVCMJobPolicyForMSP(String afGuid, String planUUID) {
		// Check if need to deploy VCM policy for MSP (remote VSB) 
		try {
			ReplicationJobScript jobScript = getReplicationJobScript(afGuid);
			if (jobScript != null) {
				if (!ManualConversionUtility.isVSBWithoutHASupport(jobScript)) {
					logger.info("VSB job script for node " + afGuid + " is not MSP.");
					return false;
				}
				if (planUUID.equalsIgnoreCase(jobScript.getPlanUUID())) {
					logger.info("No need to redeploy VSB job script for node " + afGuid);
					return false;
				} else {
					String msg = String.format("Need to redeploy VSB job script for node %s as the plan UUID is changed: old plan=%s, new plan=%s", 
							afGuid, jobScript.getPlanUUID(), planUUID);
					logger.info(msg);
				}
			} else {
				String msg = String.format("Need to deploy VSB job script for node %s on this converter.", afGuid);
				logger.info(msg);
			}
		}catch(Exception e){
			logger.warn("Failed to getReplicationJobScript for node " + afGuid, e);
		}
		
		return true;
	}

	private long getTimeoutOfVMSnapRequest() {
		long vsbRequestSnapshotTimeout = REQUEST_TAKE_VM_SNAPSHOT_VALUE;
		try {
			vsbRequestSnapshotTimeout = FlashSwitch.getSwitchIntFromReg(
					SWT_VSB_VM_TAKESNAPSHOT_REQUEST_TIMOUT,
					REQUEST_TAKE_VM_SNAPSHOT_VALUE,
					FlashSwitchDefine.VSB_REG_ROOT);
		} catch (Exception e) {
			logger.warn("Fail to retrieve the value of taking-snapshot from registry.", e);
		}
		return vsbRequestSnapshotTimeout;
	}
			
	public void takeHyperVVMSnapshot(ReplicationJobScript replicationJobScript, long jobID) {
		logger.info("take hyper-v vm snapshot.");
		String afGuid = replicationJobScript.getAFGuid();
		HeartBeatJobScript jobScript = getHeartBeatJobScript(afGuid);
		
		WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(jobScript);
		try {
			if (!MonitorWebClientManager.isRemoteService(client)) {
				client.getFlashServiceR16_5().takeHyperVVMSnapshotWithUuid(afGuid);
			}
			else {
				logger.info("Staring hyper-v vm remote snapshot task...");
				String taskId = client.getIFlashService_Tungsten().startTakeHyperVVMSnapshotTask(afGuid);
				logger.info(String.format("The hyper-v vm snapshot task id %s has stared.", taskId));
				
				long end = System.currentTimeMillis() + getTimeoutOfVMSnapRequest();
				while (!client.getIFlashService_Tungsten().isHyperVVmSnapshotTaskDone(taskId)){
					if (end - System.currentTimeMillis() <= 0 ){
						logger.error(String.format("The task %s of taking snapshot for hyperv-v vm timed out.", taskId));
						throw new SocketTimeoutException(String.format("The task %s of taking snapshot for hyperv-v vm timed out.", taskId));
					}
					Thread.sleep(WAIT_INTERVALS);
				}
				
				logger.info(String.format("The hyper-v vm snapshot task id %s has finished.", taskId));
			}
		} catch (SOAPFaultException e) {
			logger.error("Fail to connect to monitor host. " + e.getMessage());
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { e.getMessage(),"", "", "", "" }, afGuid);
		} catch (Exception e) {
			logger.error("Failed to takeHyperVVMSnapshot. " + e.getMessage());
			if (e instanceof ConnectException || e instanceof WebServiceException || e instanceof SocketTimeoutException) {
				logger.error("Fail to connect to monitor host. " + e.getMessage());
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_FAIL_TO_CONNECT_HOST, 
						getHeartBeatJobScript(afGuid).getHeartBeatMonitorHostName(), e.getMessage());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, afGuid);
			} else {
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_RET_MSG, e.getMessage());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, afGuid);
			}
		} 
	}
	
	public void getRegisterForHA(FailoverJobScript script) {

		logger.info("getRegisterForHA for " + script.getAFGuid());

		script.setState(FailoverJobScript.REGISTERED);
		int vmType = VirtualMachineInfo.VIRTUAL_TYPE_UNKNOWN;
		switch(script.getVirtualType()){
		case HyperV:
			vmType = VirtualMachineInfo.VIRTUAL_TYPE_HYPERV;
			break;
		case VMwareESX:
		case VMwareVirtualCenter:
			vmType = VirtualMachineInfo.VIRTUAL_TYPE_VMWARE;
			break;
		};

		HeartBeatModelManager.registerHeartBeat(script, HeartBeatJobScript.STATE_REGISTERED,vmType);
//		In new UI, VCM server is always the top one, for getting monitor webservice client in
//		distributed topology, its information needs to be saved.
//		HeartBeatModelManager.registerMonitor(HAService.getInstance().retrieveCurrentUUID(), script.getProductionServerProtocol(),script.getProductionServerName(),script.getProductionServerPort(), true);
		
		synchronized(JobQueueFactory.getDefaultJobQueue()){
			for (FailoverJob job : JobQueueFactory.getDefaultJobQueue()
					.findByJobType(JobType.Failover).toArray(new FailoverJob[] {})) {
				if (job.getJobScript() != null) {
					FailoverJobScript jobScript = job.getJobScript();
					if (jobScript!=null && jobScript.getAFGuid().equals(script.getAFGuid())) {
						JobQueueFactory.getDefaultJobQueue().remove(job);
					}
				}
			}
			String jobID = JobQueueFactory.getDefaultJobQueue().add(script);
			FailoverJob job = (FailoverJob) JobQueueFactory.getDefaultJobQueue().findFailoverJobByID(jobID);
			job.schedule();
			
			if (!ManualConversionUtility.isVSBWithoutHASupport(script)) {			
				String msg = WebServiceMessages.getResource(
						COLDSTANDBY_SETTING_REGISTER_HEARTBEAT_MONITOR,script.getProductionServerName());
	
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, Constants.AFRES_AFJWBS_GENERAL,
						new String[] { msg, "", "", "", "" }, script.getAFGuid());
			}
		}

	}
	
	public void specifyESXServerForRVCM(MachineDetail machineDetail,String backupDest, boolean isForceSave) {
		String afGuid = machineDetail.getInstanceUuid();
		if (!isForceSave) {
			VCMMachineInfo detail = BrowserService.getInstance().getNativeFacade().getMachineDetailFromBackupSession(backupDest);
			if (detail == null) {
				logger.error("Failed to get machine type from backup session.");
				throw AxisFault.fromAxisFault(
						"Failed to get machine type from backup session.",
						FlashServiceErrorCode.VCM_FAILED_GET_VM_BACKUPINFO);
			} else if (detail.getNodeType() != VCMMachineInfo.VCMNodeType.ESX_VM) {
				logger.error("the node is not a esx vm , but " + detail.toString());
				throw AxisFault.fromAxisFault(
						"can not verify if the node is esx vm",
						FlashServiceErrorCode.VCM_MACHINE_ISNOT_ESXVM);
			}
		}
		try {
			MachineDetailManager.getInstance().setMachineDetail(machineDetail,afGuid);
		} catch (Exception e) {
			logger.error("Failed to save machine detail for RVCM node.", e);
		}			
	}
	
	public boolean getEnforeVDDKNBDFlagFromMonitor(String afGuid){
		
		String localAFGuid = evaluteAFGuid(afGuid);
		WebServiceClientProxy clientProxy = MonitorWebClientManager.getMonitorWebClientProxy(localAFGuid);
		if(clientProxy == null){
			logger.info("The monitor proxy is null");
			return false;
		}
		
		try {
			return clientProxy.getFlashServiceR16_5().getEnforeVDDKNBDFlag();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		
	}
	
	public void reportJobMonitor(String afGuid) {
		RepJobMonitor repJobMonitor = CommonService.getInstance().getRepJobMonitorInternal(afGuid);
		if (repJobMonitor == null || repJobMonitor.getId() == -1)
			return;

		ConvertJobMonitor cjm = updateConvertJobMonitor(afGuid, repJobMonitor);
		if (cjm == null)
			return;
		
		String d2dServerUUID = HAService.getInstance().retrieveCurrentNodeID();
		if(FlashSyncher.getInstance().reportJobMonitor(cjm, afGuid, cjm.getRpsPolicyUUID(), d2dServerUUID) != 0){
			logger.error("Failed to report conversion job monitor: " + cjm);
		}
	}

	public ConvertJobMonitor[] getRepJobMonitors(String afGuid){
		List<ConvertJobMonitor> jobMonitorList = new ArrayList<ConvertJobMonitor>();

		RepJobMonitor repJobMonitor = CommonService.getInstance().getRepJobMonitor(afGuid);
		if (repJobMonitor == null){
			return null;
		}
		if (repJobMonitor.getId() == -1){
			return null;
		}
		ConvertJobMonitor cjm = updateConvertJobMonitor(afGuid, repJobMonitor);
		if (cjm != null){
			jobMonitorList.add(cjm);
		}
		return jobMonitorList.toArray(new ConvertJobMonitor[0]);
	}

	public ConvertJobMonitor[] getAllRepJobMonitors(){
		Map<String, RepJobMonitor> repJobMonitors = CommonService.getInstance().getAllRepJobMonitors();
		if (repJobMonitors == null)
			return null;
		
		List<ConvertJobMonitor> jobMonitorList = new ArrayList<ConvertJobMonitor>();

		for(Map.Entry<String, RepJobMonitor> jm : repJobMonitors.entrySet()) {
			String instanceUUID = jm.getKey();
			RepJobMonitor repJobMonitor = jm.getValue();
			
			if(StringUtil.isEmptyOrNull(instanceUUID) || repJobMonitor == null){
				continue;
			}
			
			if (repJobMonitor.getId() == -1){
				continue;
			}
			
			ConvertJobMonitor cjm = updateConvertJobMonitor(instanceUUID, repJobMonitor);
			if (cjm != null){
				jobMonitorList.add(cjm);
			}
		}
		if (jobMonitorList.isEmpty())
			return null;
		
		return jobMonitorList.toArray(new ConvertJobMonitor[0]);
	}

	protected ConvertJobMonitor updateConvertJobMonitor(String afGuid, RepJobMonitor repJobMonitor){
		ReplicationJobScript jobScript = getReplicationJobScript(afGuid);
		if (jobScript == null){
			return null;
		}
		ConvertJobMonitor cjm = new ConvertJobMonitor();
		
		cjm.setJobType(Constants.AF_JOBTYPE_CONVERSION);
		cjm.setJobMonitor(repJobMonitor);
		cjm.setJobId(repJobMonitor.getId());
		cjm.setJobPhase(repJobMonitor.getRepPhase());
		
		//If convert the source machine from HBBU or backup2RPS, consider the source machine as a VM 
		if (jobScript.getBackupToRPS() || jobScript.isVSphereBackup() || ManualConversionUtility.isVSBWithoutHASupport(jobScript)){
			cjm.setVmInstanceUUID(afGuid);
		}
		//cjm.setD2dUuid(afGuid);
		cjm.setRpsPolicyUUID(jobScript.getRpsPolicyUUID());
		cjm.setD2dServerName(jobScript.getAgentNodeName());
		cjm.setAgentNodeName(jobScript.getNodeName());
		cjm.setServerNodeName(CommonService.getInstance().getLocalHostAsTrust().getName());
		cjm.setDataStoreUUID(jobScript.getDataStoreUUID());
		cjm.setPlanUUID(jobScript.getPlanUUID());
		cjm.setStartTime(repJobMonitor.getRepJobStartTime());

		boolean isFinished = repJobMonitor.isFinished();
		cjm.setFinished(isFinished);
		if (isFinished){
			cjm.setJobStatus(repJobMonitor.getRepJobStatus());
			return cjm;
		}
		
		cjm.setJobStatus(JobStatus.JOBSTATUS_ACTIVE);
		cjm.setElapsedTime(repJobMonitor.getRepJobElapsedTime());
		cjm.setRemainTime(0);
		if (repJobMonitor.getRepElapsedTime() > 1000 && repJobMonitor.getRepTotalSize() > 0){
			long transSizeAfterResume = repJobMonitor.getRepTransAfterResume();
			
			double remainSize = repJobMonitor.getRepTotalSize() - repJobMonitor.getRepTransedSize();
			double transThroughput = (transSizeAfterResume/1024.0/1024.0) / (repJobMonitor.getRepElapsedTime()/1000.0/60.0);
			
			//Calc remainTime only if transfer throughput reach 1MB/min
			if(transThroughput > 1){
				double remainTime = remainSize/(transSizeAfterResume / repJobMonitor.getRepElapsedTime());
				cjm.setRemainTime((long)remainTime);
			}
		}
		float repProgress = 0;
		if (repJobMonitor.getRepTotalSize() > 0){
			repProgress =  (float)(repJobMonitor.getRepTransedSize()) /  (float)(repJobMonitor.getRepTotalSize());
		}
		cjm.setProgress(repProgress);
		
		return cjm;
	}

	@Override
	public boolean needRun(JobDependencySource source) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String getOSVersion() {
		return getNativeFacade().getOSVersion();
	}
	
	public VMPowerStatus getVmPowerState(String vmGuid, String AFGuid, String vmName, boolean isHyperVModel) {
		if (vmName == null || vmGuid == null)
			return VMPowerStatus.unknown;
		
		int power = 0;
		VMPowerStatus vmPowerStatus = VMPowerStatus.unknown;
		
		if (isHyperVModel) {
			power = GetHyperVVmState(vmGuid);
			switch (power) {
				case 3:
					vmPowerStatus = VMPowerStatus.power_off;
					break;
				case 2:
					vmPowerStatus = VMPowerStatus.power_on;
					break;
				default:
					vmPowerStatus = VMPowerStatus.unknown;
			}
		}
		else {
			powerState state = powerState.errorFault;
			CAVirtualInfrastructureManager vmwareOBJ = null;
			try {
				vmwareOBJ = VCMPolicyUtils.validateESX(AFGuid, getJobScriptComboObject(AFGuid), true);
				state = vmwareOBJ.getVMPowerstate(vmName, vmGuid);
			} catch (Exception e) {
				logger.error(e);
			}
			
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
				}
			}
			
			if (state == powerState.poweredOff)
				vmPowerStatus = VMPowerStatus.power_off;
			else if (state == powerState.poweredOn)
				vmPowerStatus = VMPowerStatus.power_on;
			else if (state == powerState.suspended)
				vmPowerStatus = VMPowerStatus.suspended;
			else
				vmPowerStatus = VMPowerStatus.unknown; 
		}
		
		return vmPowerStatus;
	}
	
	public boolean checkVCMMonitorSetting(String afGuid, String monitorUUID, WebServiceClientProxy converterClient) {
		try {
			return converterClient.getServiceV2().checkVCMMonitorSetting(afGuid, monitorUUID);
		} catch (Exception e) {
			logger.error("Fail to connect to D2D agent.");
			return true;
		}
	}
	
	public boolean getVSBStatus(D2DStatusInfo statusInfo, SummaryModel summaryModel, ProductionServerRoot serverRoot, String uuid) {
		FailoverJobScript failoverJobScript = this.getFailoverJobScript(uuid);
		if (failoverJobScript == null)
			return false;
		
		{
			// Get converter client
			WebServiceClientProxy converterClient = null;
			
			try {
				converterClient = getConverterService(failoverJobScript);
			} catch (Exception e) {
				logger.error(e);
				logger.error("Fail to get converter service.");
			}
			
			// Check vcm monitor setting
			if (converterClient != null && !checkVCMMonitorSetting(uuid, statusInfo.getMonitorUUID(), converterClient)) {
				HeartBeatModelManager.deRegisterHeartBeat(uuid);
				logger.warn("The node[" + uuid + "]'s monitor is changed, so remove the node from the heartbeat model");
				return false;
			}
			
			// Get virtual standby status and heartbeat status
			Integer[] status = getAutoOfflieCopyStatusAndHeartbeatStatus(uuid, converterClient, failoverJobScript);
			statusInfo.setHeartbeatStatus(status[0]);
			statusInfo.setAutoOfflieCopy(status[1]);
			converterClient = null;
		}
		
		// Get vm info
		getVMInfo(statusInfo, summaryModel, serverRoot, uuid);
			
		return true;
	}
	
	
	private void getHyperVStatusInfo(D2DStatusInfo statusInfo, ProductionServerRoot serverRoot, String uuid) {
		VMPowerStatus vmPowerStatus = VMPowerStatus.unknown;
		String vmUuid = null;
		ARCFlashNode node = HeartBeatModelManager.getHeartbeatNode(uuid);
		if (node == null) {
			logger.error("The monitee node does not exist, uuid = " + uuid);
			return;
		}
		VirtualMachineInfo vmInfo = node.getVmInfo();
		if (vmInfo != null)
			vmUuid = vmInfo.getVmGUID();

		if (serverRoot == null || serverRoot.getReplicaRoot() == null) {
			logger.debug("No replication once was launched: (serverRoot == null) ="	+ (serverRoot == null));
			statusInfo.setSnapshots(new VMSnapshotsInfo[0]);
			return;
		}

		TransServerReplicaRoot transRoot = (TransServerReplicaRoot) serverRoot.getReplicaRoot();
		statusInfo.setSnapshots(HACommon.getVMSnapshotsHyperV(transRoot.getRootPath(), uuid, vmInfo));

		if (statusInfo.getSnapshots() == null || statusInfo.getSnapshots().length == 0)
			return;

		int power = GetHyperVVmState(vmUuid);
		vmPowerStatus = (power == 2) ? VMPowerStatus.power_on : VMPowerStatus.power_off;
		statusInfo.setVmStatus(vmPowerStatus);

		if (statusInfo.getVmStatus() == VMPowerStatus.power_on) {
			statusInfo.setCurrentRunningSnapshot(getSnapShotGuiForHyperV(vmUuid));
		}
	}

	private void getVmWareStatusInfo(D2DStatusInfo statusInfo, ProductionServerRoot serverRoot, String uuid) {
		String vmUuid = null;
		ARCFlashNode node = HeartBeatModelManager.getHeartbeatNode(uuid);
		if (node == null) {
			logger.error("The monitee node does not exist, uuid = " + uuid);
			return;
		}
		VirtualMachineInfo vmInfo = node.getVmInfo();
		if (vmInfo != null)
			vmUuid = vmInfo.getVmGUID();

		// Get vm name
		statusInfo.setVmName(getVmName(uuid, serverRoot));

		if (StringUtil.isEmptyOrNull(uuid))
			return;
		
		// Get snapshots and power on status and running snapshot
		VMPowerStatus vmPowerStatus = VMPowerStatus.unknown;

		CAVirtualInfrastructureManager vmwareOBJ = getVMWareObj(uuid);
		if (vmwareOBJ == null)
			return;	
	
		try {
			
			Object vmMor = VCMStatusCollector.getInstance().lookupLRUCache(vmUuid);
			if (vmMor == null)
			{
				vmMor = vmwareOBJ.findVMInstObjectByUuidAndvmname(vmUuid, statusInfo.getVmName());
				VCMStatusCollector.getInstance().putLRUCache(vmUuid, vmMor);
			}
						
			statusInfo.setSnapshots(getVMWareSnapshotsByMoref(vmwareOBJ, uuid, statusInfo.getVmName(), vmUuid, vmMor));

			if (statusInfo.getSnapshots() == null || statusInfo.getSnapshots().length == 0)
				return;

			powerState state = vmwareOBJ.getVMPowerstateByMoref(statusInfo.getVmName(), vmMor);
			if (state == powerState.poweredOn)
				vmPowerStatus = VMPowerStatus.power_on;
			else
				vmPowerStatus = VMPowerStatus.power_off;

			statusInfo.setVmStatus(vmPowerStatus);
			if (statusInfo.getVmStatus() == VMPowerStatus.power_on) {
				statusInfo.setCurrentRunningSnapshot(vmwareOBJ.getCurrentSnapshotByMoref(statusInfo.getVmName(), vmMor));
			}
		} catch (Exception e) {
			logger.error("Fail to get vm info, uuid = " + uuid);
			statusInfo.setSnapshots(new VMSnapshotsInfo[0]);
			VCMStatusCollector.getInstance().putLRUCache(uuid, null);
		} finally {
			try {
				if (vmwareOBJ != null)
					vmwareOBJ.close();
			} catch (Exception e) {
			}
		}
	}

	private void getVMInfo(D2DStatusInfo statusInfo, SummaryModel summaryModel, ProductionServerRoot serverRoot, String uuid) {
		if (summaryModel.isHyperVModel()) {
			getHyperVStatusInfo(statusInfo, serverRoot, uuid);
		} else {
			getVmWareStatusInfo(statusInfo, serverRoot, uuid);
		}
	}
	
	private VMSnapshotsInfo[] getVMWareSnapshots(CAVirtualInfrastructureManager vmwareOBJ, String afGuid, String vmName, String vmUuid) {
		return getVMWareSnapshotsByUUIDOrMoref(vmwareOBJ, afGuid, vmName, vmUuid, null);
	}
	
	
	private VMSnapshotsInfo[] getVMWareSnapshotsByMoref(CAVirtualInfrastructureManager vmwareOBJ, String afGuid, String vmName, String vmUuid, Object vmMor) {
		return getVMWareSnapshotsByUUIDOrMoref(vmwareOBJ, afGuid, vmName, vmUuid, vmMor);
	}
	
	private VMSnapshotsInfo[] getVMWareSnapshotsByUUIDOrMoref(CAVirtualInfrastructureManager vmwareOBJ, String afGuid, String vmName, String vmUuid, Object vmMor) {
		if (vmUuid == null || vmUuid.equals(""))
			return new VMSnapshotsInfo[0];
		try {
			
			VMwareSnapshotModelManager vmwareModelManager;
			if (null == vmMor) {
				vmwareModelManager = VMwareSnapshotModelManager.getManagerInstance(vmwareOBJ, vmName, vmUuid);
			} else {
				vmwareModelManager = VMwareSnapshotModelManager.getManagerInstance(vmwareOBJ, vmName, vmUuid, vmMor);
			}
			
			if(vmwareModelManager.isReady()){
				SortedSet<VMSnapshotsInfo> repotortySnapshots = vmwareModelManager.getSnapshots(afGuid);		
				if(repotortySnapshots == null || repotortySnapshots.size() == 0) {
					return new VMSnapshotsInfo[0];
				}
				
				List<VMSnapshotsInfo> temp = new ArrayList<VMSnapshotsInfo>();
				Map<String, String> snapshotUrls = null;
				
				if (null == vmMor) {
					snapshotUrls = vmwareOBJ.listVMSnapShots(vmName, vmUuid);
				} else {
					snapshotUrls = vmwareOBJ.listVMSnapShotsByMoref(vmName, vmMor);
				}
				
				Set<String> keySet = snapshotUrls == null ? new HashSet<String>() : snapshotUrls.keySet();
				for(VMSnapshotsInfo info : repotortySnapshots){
					if(info.getTimestamp() > 0) {
						info.setTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(new Date(info.getTimestamp())));
					}
					if(!keySet.contains(info.getSnapGuid())){
						temp.add(info);
					}else if(info.isDRSnapshot()){
						temp.add(info);
					}
				}
				
				if(!temp.isEmpty()){
					repotortySnapshots.removeAll(temp);
				}
				
				VMSnapshotsInfo[] infos = new VMSnapshotsInfo[repotortySnapshots.size()];
				VMSnapshotsInfo[] reInfos = repotortySnapshots.toArray(infos);
				if(logger.isDebugEnabled()){
					for(VMSnapshotsInfo info : reInfos){
						logger.debug(info.toString());
					}
				}
				return reInfos;
			}			
		} catch (Exception e) {
			logger.error("Fail to get VMWare snapshots, uuid = " + vmUuid);
			return null;
		}
		return null;
	}
	
	private CAVirtualInfrastructureManager getVMWareObj(String uuid) {
		FailoverJobScript jobScript = HACommon.getFailoverJobScriptObject(uuid);
		if(jobScript == null){
			logger.error("No Failoverjobscript is set!!!!");
			return null;
		}
		List<Virtualization> vList = jobScript.getFailoverMechanism();
		if(vList==null || vList.size() == 0){
			logger.error("No virtualization is set!!!");
			return null;
		}

		String hostname="";
		String username="";
		String password="";
		String protocol = "";
		int port = 0;
		Virtualization virtual = vList.get(0);			
		if(virtual instanceof VMwareESX){
			VMwareESX vmware = (VMwareESX)virtual;
			hostname = vmware.getHostName();
			username = vmware.getUserName();
			password = vmware.getPassword();
			protocol = vmware.getProtocol();
			port = vmware.getPort();
		}else if(virtual instanceof VMwareVirtualCenter){
			VMwareVirtualCenter vCenter = (VMwareVirtualCenter)virtual;
			hostname = vCenter.getESXHostName();
			username = vCenter.getUserName();
			password = vCenter.getPassword();
			protocol = vCenter.getProtocol();
			port = vCenter.getPort();
		}
		
		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ =  CAVMwareInfrastructureManagerFactory
			.getCAVMwareVirtualInfrastructureManager(hostname,username,password, protocol, true, port);
			
			if(vmwareOBJ == null){
				return null;
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		
		return vmwareOBJ;
	}
	
	public MachineDetail getMachineDetail(String afGuid) {
		return MachineDetailManager.getInstance().getMachineDetail(afGuid);
	}

	public EsxServerInformation getEsxServerInformation(String esxServer, String username, String passwod,
			String protocol, int port) throws SOAPFaultException {
		try {
			CAVirtualInfrastructureManager vmwareOBJ = VMWareConnectionCache.getVMWareConnection(esxServer, username,
					passwod, protocol, true, port);
			if (vmwareOBJ == null) {
				logger.error("Failed to create vmware object.");
				throw AxisFault.fromAxisFault("Failed to getVMWareServerInformation!!!");
			}

			return VMwareService.getInstance().getEsxServerInformation(vmwareOBJ, 
					esxServer, username, passwod, protocol, port);
		} catch (Exception e) {
			logger.error("Failed to getVMWareServerInformation!!!", e);
			throw AxisFault.fromAxisFault("Failed to getVMWareServerInformation!!!", e);
		}
	}

	public EsxHostInformation getEsxHostInformation(String esxServer, String username, String passwod, String protocol,
			int port, VWWareESXNode esxNode) throws SOAPFaultException {
		try {
			CAVirtualInfrastructureManager vmwareOBJ = VMWareConnectionCache.getVMWareConnection(esxServer, username,
					passwod, protocol, true, port);
			if (vmwareOBJ == null) {
				logger.error("Failed to create vmware object.");
				throw AxisFault.fromAxisFault("Failed to getEsxHostInformation!!!");
			}
			
			return VMwareService.getInstance().getEsxHostInformation(vmwareOBJ, esxNode);
		} catch (Exception e) {
			logger.error("Failed to getEsxHostInformation!!!", e);
			throw AxisFault.fromAxisFault("Failed to getEsxHostInformation!!!", e);
		}
	}

	// For VMWare
	public ProductionServerRoot ifVmOwnedByAgent(CAVirtualInfrastructureManager vmOBJ, String vmName, String vmUUID, String afguid) {
		String remoteFile = "repository.xml";
		InputStream stream = null;
		try {
			stream = vmOBJ.getVMConfig(vmName, vmUUID, remoteFile);
		} catch (Exception e) {
			logger.error(String.format("Fail to get %s from vmware datastore. ", remoteFile), e);
			return null;
		}
		if (stream == null)
			return null;
		try {
			ProductionServerRoot serverRoot = RepositoryUtil.getInstance(stream).getProductionServerRoot(afguid);
			return serverRoot;
		} catch (Exception e) {
			logger.error("Fail to get production server root. ", e);
			return null;
		}
	}
	
	// For HyperV
	public ProductionServerRoot ifVmOwnedByAgent(String vmName, 
			ReplicationJobScript replicationJobScript, HeartBeatJobScript heartBeatJobScript) {
		String remoteFile = HAService.getInstance().generateRemoteRepositoryPath(vmName, replicationJobScript);
		String afguid = replicationJobScript.getAFGuid();
		WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);
		try {
			return client.getServiceV2().downloadProductionServerRoot(remoteFile, afguid);
		}catch (Exception e) {
			logger.error("Fail to download repository.xml from hyper-V datastore.", e);
		}
		return null;
	}
	
	public void updateLocalRepository(ProductionServerRoot serverRoot) {
		String localFile = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
		// TODO. Pull existing product server root per server GUID, and compare its version number to decide to update or not.
		try {
			RepositoryUtil.getInstance(localFile).saveProductionServerRoot(serverRoot);
		} catch (HAException e) {
			logger.warn(String.format("Fail to update local %s. ", localFile), e);
		}
	}
	
	public void connectToRemote(String remotePath, String userName, String domain, String password) throws Exception {
		if (StringUtil.isEmptyOrNull(remotePath))
			return;

		if (!StringUtil.isEmptyOrNull(domain)) {
			if (!domain.trim().endsWith("\\"))
				domain += "\\";
			userName = domain + userName;
		}

		try {
			long netConn = BrowserService.getInstance().getNativeFacade().NetConn(userName, password, remotePath);
			if (netConn != 0) {
				logger.error("Failed to connect to remote configuration destination");
				logger.error("RemotePath: " + remotePath);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public ADRConfigure getAdrConfigure(String sessionPath) {
		
		if(!sessionPath.endsWith("\\")){
			sessionPath += "\\";
		}
		
		String adrConfigXML = sessionPath + CommonUtil.ADRCONFIG_XML_FILE;
		File f = new File(adrConfigXML);
		if(!f.exists()){
			logger.warn("The adrconfigure file under "+ sessionPath + " does not exist.");
			return null;
		}
		
		ADRConfigure adrConfigure = null;
		try {
			adrConfigure = ADRConfigureFactory.parseADRConfigureXML(adrConfigXML);
		} catch (Exception e) {
			logger.warn("Error in getting adrconfigure."+e.getMessage(),e);
			return null;
		}
		
		return adrConfigure;
		
	}
	
	public void closeRemoteConnect(String remotePath) {
		if (StringUtil.isEmptyOrNull(remotePath))
			return;

		try {
			BrowserService.getInstance().getNativeFacade().NetCancel(remotePath, false);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public List<String> GetHyperVVMAttachedDiskImage(String vmGuid) {

		long handle = 0;
		try {
			handle = HyperVJNI.OpenHypervHandle("", "", "");
		} catch (HyperVException he) {
			logger.error("Failed to open hyperv manger handle, " + he.getMessage());
			return null;
		}
		
		List<String> getAttachedDiskImage = new ArrayList<String>();
		try {
			HyperVJNI.GetAttachedDiskImage(handle, vmGuid, getAttachedDiskImage);
		} catch (HyperVException he) {
			logger.error("Failed to get disk image file of VM " + vmGuid, he);
		} finally {
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (HyperVException he) {
				logger.warn("Failed to close HyperV handle, " + he.getMessage());
			}
		}
		return getAttachedDiskImage;
	}
	
	public boolean isDiskSizeChanged(ProductionServerRoot prodRoot,
			ADRConfigure adrConfig) {
		if (prodRoot == null || adrConfig == null)
			return false;
		ReplicaRoot replicaRoot = prodRoot.getReplicaRoot();
		if (replicaRoot == null) {
			return false;
		}
		List<DiskDestination> preDisks = replicaRoot.getDiskDestinations();
		if (preDisks == null) {
			return false;
		}
		SortedSet<Disk> curDisks = adrConfig.getDisks();
		if (curDisks == null) {
			return false;
		}
		for (DiskDestination preDisk : preDisks) {
			String preSignature = preDisk.getDisk().getSignature();
			if (StringUtil.isEmptyOrNull(preSignature))
				continue;
			long preSize = preDisk.getDisk().getSize();
			if (preSize <= 0)
				continue;
			for (Disk curDisk : curDisks) {
				String curSignature = curDisk.getSignature();
				if (StringUtil.isEmptyOrNull(curSignature))
					continue;
				long curSize = curDisk.getSize();
				if (curSize <= 0)
					continue;
				String curDiskGuid = curDisk.getDiskGuid();
				String changedDiskTag = StringUtil.isEmptyOrNull(curDiskGuid) ? curSignature : curDiskGuid;
				if (StringUtil.equals(preSignature, curSignature)) { 
					if (curSize > preSize) {
						logger.warn(String.format("Disk %s's size has been increased.", changedDiskTag));
						return true;
						}
					break;
				}
			}
		}
		return false;
	}

	public int pauseHeartBeatForVcmUpgrade(String afGuid) throws SOAPFaultException {
		logger.info("pauseHeartBeatForVcmUpgrade() - start");
		int result  = 0;
		HeartBeatJobScript heartBeatJobScript = null;
		
		String localAFGuid = evaluteAFGuid(afGuid);
		
		synchronized (jobQueue) {
			AFJob heartBeatJob = this.getHeartBeatJob(localAFGuid);
			if (isNullJob(heartBeatJob)){
				logger.info("pauseHeartBeatForVcmUpgrade() - isNullJob");
				return 1;	
			}
				
			heartBeatJobScript = (HeartBeatJobScript) heartBeatJob.getJobScript();
			if (isNullJobScript(heartBeatJobScript)){
				logger.info("pauseHeartBeatForVcmUpgrade() - isNullJobScript");
				return 2;
			}
				
			WebServiceClientProxy client = null;
			try {
				logger.info("pauseHeartBeatForVcmUpgrade()- Pause heartbeat");
				client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);
				client.getServiceV2().pauseHeartBeat(heartBeatJobScript.getAFGuid());
			} catch (SOAPFaultException e) {
				String msg = WebServiceMessages.getResource(
							COLDSTANDBY_SETTING_PAUSE_HEARTBEAT_FAIL,heartBeatJobScript.getHeartBeatMonitorHostName());
					
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, -1, 
							Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "",
									"", "", "" }, afGuid);							
				logger.error(e);
				throw e;
			}
			
		
			if( heartBeatJob.getJobStatus().getStatus().equals(HAJobStatus.Status.Active))
			{
				logger.info("The HaJobStatus is Active and will be set Canceled(pause).");
				heartBeatJob.unschedule();
				HAJobStatus jobStatus = new HAJobStatus();
				jobStatus.setStatus(HAJobStatus.Status.Canceled);
				heartBeatJob.setJobStatus(jobStatus);
				jobQueue.reStoreJob(heartBeatJob);
			}
			String msg = WebServiceMessages.getResource(
					COLDSTANDBY_SETTING_PAUSE_HEARTBEAT,heartBeatJobScript.getHeartBeatMonitorHostName());
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg, "", "", "", "" }, localAFGuid);	
			
		}	
		return result;
	}

	
    public int resumeHeartBeatForVcmUpgrade(String afGuid) throws SOAPFaultException {
        logger.info("resumeHeartBeatForVcmUpgrade() - start");
        int result  = 0;
        HeartBeatJobScript heartBeatJobScript = null;
            
        String localAFGuid = evaluteAFGuid(afGuid);
        
        synchronized (jobQueue) {
            AFJob heartBeatJob = this.getHeartBeatJob(localAFGuid);
            if (isNullJob(heartBeatJob)){
                logger.info("resumeHeartBeatForVcmUpgrade() - isNullJob");
                return 1;   
            }
                
            heartBeatJobScript = (HeartBeatJobScript) heartBeatJob.getJobScript();
            if (isNullJobScript(heartBeatJobScript)){
                logger.info("resumeHeartBeatForVcmUpgrade() - isNullJobScript");
                return 2;
            }
                
            WebServiceClientProxy client = null;
            try {
                logger.info("resumeHeartBeatForVcmUpgrade()- Resume heartbeat");
                client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);
                client.getServiceV2().resumeHeartBeat(heartBeatJobScript.getAFGuid());
            } catch (SOAPFaultException e) {
                String msg = WebServiceMessages.getResource(
                            COLDSTANDBY_SETTING_RESUME_HEARTBEAT_FAIL,heartBeatJobScript.getHeartBeatMonitorHostName());
                    
                HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, -1, 
                            Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "",
                                    "", "", "" }, afGuid);                          
                logger.error(e);
                throw e;
            }
            

            if( heartBeatJob.getJobStatus().getStatus().equals(HAJobStatus.Status.Canceled)){
                logger.info("The HaJobStatus is Canceled and will be set Active(resume).");
                heartBeatJob.schedule();
                HAJobStatus jobStatus = new HAJobStatus();
                jobStatus.setStatus(HAJobStatus.Status.Active);
                heartBeatJob.setJobStatus(jobStatus);
                jobQueue.reStoreJob(heartBeatJob);
            }
            String msg = WebServiceMessages.getResource(
                COLDSTANDBY_SETTING_RESUME_HEARTBEAT,heartBeatJobScript.getHeartBeatMonitorHostName());
        
            HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, 
                Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "",
                        "", "", "" }, localAFGuid);
            
        }   
        return result;
    }

}
