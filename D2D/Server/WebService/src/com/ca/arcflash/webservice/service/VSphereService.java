package com.ca.arcflash.webservice.service;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.locks.Lock;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.AbstractTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ha.vcloudmanager.VCloudManager;
import com.ca.arcflash.ha.vcloudmanager.VCloudManagerException;
import com.ca.arcflash.ha.vcloudmanager.VCloudManagerFactory;
import com.ca.arcflash.ha.vcloudmanager.VcloudManagerConnectionCache;
import com.ca.arcflash.ha.vcloudmanager.common.VAppSessionInfo;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudESXHost;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudNetwork;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudOrganization;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudStorageProfile;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudVApp;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudVDC;
import com.ca.arcflash.ha.vcloudmanager.objects.VCloudVM;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareVirtualInfrastructureManager;
import com.ca.arcflash.ha.vmwaremanager.Disk_Info;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanager.InvalidLoginException;
import com.ca.arcflash.ha.vmwaremanager.ResPool_Info;
import com.ca.arcflash.ha.vmwaremanager.StandardNetworkInfo;
import com.ca.arcflash.ha.vmwaremanager.VMNetworkConfigInfo;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.ha.vmwaremanager.VMwareServerType;
import com.ca.arcflash.ha.vmwaremanager.VMwareStorage;
import com.ca.arcflash.ha.vmwaremanager.powerState;
import com.ca.arcflash.ha.vmwaremanager.vDSPortGroupInfo;
import com.ca.arcflash.ha.vmwaremanager.vDSSwitchInfo;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.jobscript.base.GenerateType;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreHealthStatus;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreRunningState;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcflash.rps.webservice.data.host.RegisterNodeInfo;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4D2D;
import com.ca.arcflash.service.common.ActivityLogSyncher;
import com.ca.arcflash.service.data.PeriodRetentionValue;
import com.ca.arcflash.service.jni.model.JActivityLog;
import com.ca.arcflash.service.jni.model.JActivityLogResult;
import com.ca.arcflash.service.jni.model.JProtectionInfo;
import com.ca.arcflash.service.jni.model.JRestorePoint;
import com.ca.arcflash.service.util.ActivityLogConverter;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.common.ConfigRPSInD2DService;
import com.ca.arcflash.webservice.common.VSphereLicenseCheck;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.BackupInformationSummary;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.LicInfo;
import com.ca.arcflash.webservice.data.NextScheduleEvent;
import com.ca.arcflash.webservice.data.ProtectionInformation;
import com.ca.arcflash.webservice.data.RPSDataStoreInfo;
import com.ca.arcflash.webservice.data.RPSInfo;
import com.ca.arcflash.webservice.data.RecoveryInfoStatistics;
import com.ca.arcflash.webservice.data.RecoveryPointSummary;
import com.ca.arcflash.webservice.data.RpsDataStoreHealthStatus;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.data.activitylog.ActivityLog;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.backup.RetryPolicy;
import com.ca.arcflash.webservice.data.backup.RpsPolicy4D2D;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Folder;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.job.rps.BackupJobArg;
import com.ca.arcflash.webservice.data.job.rps.CopyJobArg;
import com.ca.arcflash.webservice.data.job.rps.RestoreJobArg;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;
import com.ca.arcflash.webservice.data.restore.CopyJob;
import com.ca.arcflash.webservice.data.restore.RecoverVMOption;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.restore.RestoreJob;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.DataStore;
import com.ca.arcflash.webservice.data.vsphere.Disk;
import com.ca.arcflash.webservice.data.vsphere.ESXServer;
import com.ca.arcflash.webservice.data.vsphere.ResourcePool;
import com.ca.arcflash.webservice.data.vsphere.SavePolicyWarning;
import com.ca.arcflash.webservice.data.vsphere.StandNetworkConfigInfo;
import com.ca.arcflash.webservice.data.vsphere.StorageAppliance;
import com.ca.arcflash.webservice.data.vsphere.VAppChildBackupVMRestorePointWrapper;
import com.ca.arcflash.webservice.data.vsphere.VCloudDirector;
import com.ca.arcflash.webservice.data.vsphere.VCloudOrg;
import com.ca.arcflash.webservice.data.vsphere.VCloudVC;
import com.ca.arcflash.webservice.data.vsphere.VCloudVDCStorageProfile;
import com.ca.arcflash.webservice.data.vsphere.VCloudVirtualDC;
import com.ca.arcflash.webservice.data.vsphere.VDSInfo;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VMItem;
import com.ca.arcflash.webservice.data.vsphere.VMNetworkConfig;
import com.ca.arcflash.webservice.data.vsphere.VMStatus;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.data.vsphere.vDSPortGroup;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.d2dstatus.SyncD2DStatusService;
import com.ca.arcflash.webservice.edge.data.d2dstatus.VMPowerStatus;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyCheckStatus;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyDeploymentCache;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.jni.model.JApplicationStatus;
import com.ca.arcflash.webservice.jni.model.JBackupInfo;
import com.ca.arcflash.webservice.jni.model.JBackupInfoSummary;
import com.ca.arcflash.webservice.jni.model.JBackupVM;
import com.ca.arcflash.webservice.jni.model.JBackupVMOriginalInfo;
import com.ca.arcflash.webservice.jni.model.JDisk;
import com.ca.arcflash.webservice.jni.model.JHypervVMInfo;
import com.ca.arcflash.webservice.jni.model.JJobContext;
import com.ca.arcflash.webservice.jni.model.JRWLong;
import com.ca.arcflash.webservice.jni.model.JVAppChildBackupVMRestorePointWrapper;
import com.ca.arcflash.webservice.jni.model.JVMNetworkConfig;
import com.ca.arcflash.webservice.replication.MachineDetailManager;
import com.ca.arcflash.webservice.scheduler.AdvancedScheduleTrigger;
import com.ca.arcflash.webservice.scheduler.BaseVSphereJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.VMMakeupProcessor;
import com.ca.arcflash.webservice.scheduler.VMRPSJobSubmitter;
import com.ca.arcflash.webservice.scheduler.VSphereBackupJob;
import com.ca.arcflash.webservice.scheduler.VSphereBackupThrottleJob;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.internal.IVSphereJobQueue;
import com.ca.arcflash.webservice.service.internal.JobQueueAddingLock;
import com.ca.arcflash.webservice.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.webservice.service.internal.StorageApplianceXMLDAO;
import com.ca.arcflash.webservice.service.internal.VSphereBackupConfigurationXMLDAO;
import com.ca.arcflash.webservice.service.internal.VSphereConverter;
import com.ca.arcflash.webservice.service.internal.VSphereJobQueue;
import com.ca.arcflash.webservice.service.internal.VSphereRestoreJobQueue;
import com.ca.arcflash.webservice.service.internal.VirtualCenterXMLDAO;
import com.ca.arcflash.webservice.service.rps.RPSServiceProxyManager;
import com.ca.arcflash.webservice.service.rps.SettingsService;
import com.ca.arcflash.webservice.service.validator.CopyJobValidator;
import com.ca.arcflash.webservice.service.validator.RestoreJobValidator;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcflash.webservice.util.ApacheServiceUtil;
import com.ca.arcflash.webservice.util.AsyncTaskRunner;
import com.ca.arcflash.webservice.util.DSTUtils;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.ScheduleUtils.PeriodTrigger;
import com.ca.arcflash.webservice.util.WebServiceMessages;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.ha.webservice.jni.HyperVException;
import com.ca.ha.webservice.jni.HyperVJNI;
// October sprint 
//October Sprint -

public class VSphereService extends AbstractBackupService {
	public static final String VMSNAPSHOTCONFIGINFO = "VMSnapshotConfigInfo.vsci";

	private static final Logger logger = Logger.getLogger(VSphereService.class);
	private static final VSphereService instance = new VSphereService();
	private VSphereConverter converter = new VSphereConverter();
	private ActivityLogConverter activityLogConverter = new ActivityLogConverter();	
	
	private VSphereBackupConfigurationXMLDAO backupConfigurationDAO = new VSphereBackupConfigurationXMLDAO();
	//October sprint - 
	private StorageApplianceXMLDAO storageApplianceDAO = new StorageApplianceXMLDAO();
	private RestoreJobValidator restoreJobValidator = new RestoreJobValidator();
	private CopyJobValidator copyJobValidator = new CopyJobValidator();
	private VirtualCenterXMLDAO virtualCenterDAO = new VirtualCenterXMLDAO();
	
	//wanqi06
	private Map<String, ArrayList<String>> jobNamesMap = new HashMap<String, ArrayList<String>>();
	
	public static final String VDDK_NOT_INSTALL = "-201";
	public static final String VDDK_NO_64BIT_BINARY = "-202";
	public static final String VIX_NOT_INSTALL = "-203";
	public static final String VDDK_VERSION_REQUIRED = "1.2.1";
	public static final String VIX_VERSION_REQUIRED = "266898.0.0"; //1.1.0
	public static final String RESOURCE_POOL_DEFAULT_NAME = "ha-root-pool";
	
	public static final int VM_STATUS_TYPE_WARNING = 0;
	public static final int VM_STATUS_TYPE_ERROR = 1;
	
	public static final int VM_TOOL_STATUS_ERROR = -1;
	public static final int VM_TOOL_STATUS_NOT_INSTALL = 0;
	public static final int VM_TOOL_STATUS_OUTOFDATE = 1;
	public static final int VM_TOOL_STATUS_OK = 2;
	public static final int VM_TOOL_STATUS_TIMEOUT = 3;
	
	public static final int VM_POWER_ERROR = -1;
	public static final int VM_POWER_ON = 0;
	public static final int VM_POWER_OFF = 1;
	public static final int VM_SUSPENDED = 2;
	
	
	public static final int VIX_STATUS_OK = 0;
	public static final int VIX_STATUS_NOT_INSTALL = 1;
	public static final int VIX_STATUS_OUT_OF_DATE = 2;
	
	public static final int WARNING_TYPE_VIX = 0;
	public static final int WARNING_TYPE_VMTOOL =1;
	public static final int WARNING_TYPE_VMPOWER = 2;
	
	public static final int VM_STATUS_ERROR_TYPE_VC = 0;
	public static final int VM_STATUS_ERROR_TYPE_VCLOUD_DIRECTOR = 1;
	
	public static final int VM_STATUS_ERROR_VC_OK = 0;
	public static final int VM_STATUS_ERROR_VC_CREDENTIAL_WRONG = 1;
	public static final int VM_STATUS_ERROR_VC_CANNOT_CONNECT = 2;
	
	public static final int GENERATE_TYPE_MANUALCONVERSION = 1;
	
	public static final String VMCONFIG_PREFIX = "WMConfig";
	
	public static final String JOB_ID = "jobID";
	
	public static final int	Standalone = 0;
	public static final int	Cluster_Physical_Node = 1;
	public static final int	Cluster_Virutal_Node = 2;
	
	public static final String Windows_Server_2008_R2 = "6.1";
	
	private VMMakeupProcessor makeupProcessor = new VMMakeupProcessor();	
	
	private VSphereService() {
		try {
			int maxJob = ServiceContext.getInstance().getvSphereMaxJobNum();
			maxJob = maxJob == -1?10:maxJob;
			//scheduler is only for vsphere backup job
			bkpScheduler = new StdSchedulerFactory(getProperties(2, "QuartzScheduler-vSphere")).getScheduler();
			bkpScheduler.start();
			
			//catalogScheduler is for vsphere catalog job and other jobs (restore, copy, etc)
			maxJob = maxJob * 2;			
			otherScheduler = new StdSchedulerFactory(getProperties(maxJob, "QuartzScheduler-vSphere-catalog")).getScheduler();
			otherScheduler.start();
		} catch (SchedulerException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
	}
	
	private Properties getProperties(int jobNum, String poolName){
		logger.info("Max-Thread-Count for vm scheduler:"+jobNum);
		Properties properties = new Properties();
		properties.setProperty("org.quartz.scheduler.instanceName", poolName);
		properties.setProperty("org.quartz.scheduler.rmi.export", "false");
		properties.setProperty("org.quartz.scheduler.rmi.proxy", "false");
		properties.setProperty("org.quartz.scheduler.wrapJobExecutionInUserTransaction", "false");
		properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		properties.setProperty("org.quartz.threadPool.threadCount", String.valueOf(jobNum));
		properties.setProperty("org.quartz.threadPool.threadPriority", "5");
		properties.setProperty("org.quartz.jobStore.misfireThreshold", String.valueOf(60*1000));
		properties.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
		properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
		return properties;
	}

	public static VSphereService getInstance() {
		return instance;
	}
	
	public Scheduler getCatalogScheduler() {
		return this.otherScheduler;
	}

	public void addVirtualCenter(VirtualCenter vc) throws Exception {
		logger.debug("addVirtualCenter(VirtualCenter) - start");
		if (logger.isDebugEnabled()) {
			if (vc != null) {
				logger.debug("Name:" + vc.getVcName());
				logger.debug("Port:" + vc.getPort());
			}
		}

		if (vc == null)
			return;

		virtualCenterDAO.addVirtualCenter(ServiceContext.getInstance()
				.getVirtualCenterFilePath(), vc);

		logger.debug("addVirtualCenter(VirtualCenter) - end");
	}

	public VirtualCenter[] getVirtualCenters() throws Exception {
		logger.debug("getVirtualCenters - start");

		VirtualCenter[] hosts = virtualCenterDAO.getVirtualCenters(
				ServiceContext.getInstance().getVirtualCenterFilePath(), true);

		logger.debug("getVirtualCenters - end");
		return hosts;
	}

	public void removeVirtualCenter(VirtualCenter vc) throws Exception {
		logger.debug("removeVirtualCenter - start");
		if (logger.isDebugEnabled()) {
			if (vc != null) {
				logger.debug("Name:" + vc.getVcName());
				logger.debug("Port:" + vc.getPort());
			}
		}

		if (vc == null)
			return;

		virtualCenterDAO.remove(ServiceContext.getInstance()
				.getVirtualCenterFilePath(), vc, getNativeFacade());

		logger.debug("removeVirtualCenter - end");
	}

	public ESXServer[] getESXServers(VirtualCenter vc) throws Exception {
		logger.debug("GetESXServers start");
		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
					.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc
							.getUsername(), vc.getPassword(), vc.getProtocol(),
							true, vc.getPort());
			ArrayList<ESXNode> nodeList = vmwareOBJ.getESXNodeList();
			if (nodeList != null && nodeList.size() != 0) {
				List<ESXServer> serverList = converter
						.ESXNodeConverter(nodeList);
				logger.debug("GetESXServers success");
				return serverList.toArray(new ESXServer[serverList.size()]);
			}
		} catch (Exception e) {
			logger.debug("GetESXServers failed");
			logger.error(e.getMessage() == null ? e : e.getMessage());
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		return null;
	}

	public String[] getESXServerDataStore(VirtualCenter vc, ESXServer esxServer)
			throws Exception {
		logger.debug("getESXServerDataStore start");
		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
					.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc
							.getUsername(), vc.getPassword(), vc.getProtocol(),
							true, vc.getPort());
			ArrayList<String> dataStoreList = vmwareOBJ
					.getESXHostDataStoreList(converter
							.ESXServerToESXNode(esxServer));
			if (dataStoreList != null && dataStoreList.size() != 0) {
				logger.debug("getESXServerDataStore success");
				String[] dataStores = dataStoreList
						.toArray(new String[dataStoreList.size()]);
				return dataStores;
			}
		} catch (Exception e) {
			logger.debug("getESXServerDataStore failed");
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		return null;
	}

	public DataStore[] getVMwareDataStore(VirtualCenter vc, ESXServer esxServer) throws Exception {
		logger.debug("getESXServerDataStore start");
		
		List<DataStore> dataStores = new LinkedList<DataStore>();
		CAVirtualInfrastructureManager vmwareOBJ = null;
		
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(
					vc.getVcName(), vc.getUsername(), vc.getPassword(), vc.getProtocol(), true, vc.getPort());
			
			ArrayList<ESXNode> esxNodes = esxServer != null ? new ArrayList<ESXNode>() : vmwareOBJ.getESXNodeList();
			if (esxServer != null) {
				esxNodes.add(converter.ESXServerToESXNode(esxServer));
			}
			
			if (esxNodes != null) {
				for (ESXNode node : esxNodes) {
					VMwareStorage[] vmwareStorages = vmwareOBJ.getVMwareStorages(node, null);
					if (vmwareStorages != null) {
						for (VMwareStorage storage : vmwareStorages) {
							dataStores.add(converter.ConvertToDataStore(storage));
						}
					}
				}
			}
			
			return dataStores.toArray(new DataStore[0]);
		} catch (Exception e) {
			logger.debug("getESXServerDataStore failed");
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		
		return null;
	}

	public VirtualMachine[] getVirtualMachine(VirtualCenter vc,
			ESXServer esxServer) {
		logger.debug("getVirtualMachine start");
		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
					.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc
							.getUsername(), vc.getPassword(), vc.getProtocol(),
							true, vc.getPort());
			ArrayList<VM_Info> vmList = vmwareOBJ.getVMNames(converter
					.ESXServerToESXNode(esxServer), true);
			if (vmList != null && vmList.size() != 0) {
				// filter vm which os is not windows
				List<VM_Info> vmList_Filtered = new ArrayList<VM_Info>();
				for (VM_Info vm : vmList) {
					String os = vm.getvmGuestOS();
					if (os == null || os.equals("")
							|| !os.contains("Microsoft")) {
						continue;
					}
					vmList_Filtered.add(vm);
				}
				List<VirtualMachine> vMachineList = converter
						.ESXVmConverter(vmList_Filtered);
				logger.debug("getVirtualMachine success");

				VirtualMachine[] vms = vMachineList
						.toArray(new VirtualMachine[vMachineList.size()]);
				return vms;
			}
		} catch (Exception e) {
			logger.debug("getVirtualMachine failed");
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		return null;
	}
	
	public ResourcePool[] getResourcePool(VirtualCenter vc,ESXServer esxServer,ResourcePool parentResourcePool){
		logger.debug("getResourcePool start");
		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
					.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc
							.getUsername(), vc.getPassword(), vc.getProtocol(),
							true, vc.getPort());
			ArrayList<ResPool_Info> resList = vmwareOBJ.getresPool(converter
					.ESXServerToESXNode(esxServer));
			if(resList!=null){
				List<ResourcePool> poolList = new ArrayList<ResourcePool>();
				if(parentResourcePool == null){
					for(ResPool_Info resPool : resList){
						if(resPool.getpoolMoref().equals(resPool.getparentPoolMoref())){
							parentResourcePool = ConvertToResourcePool(resPool);
						}
					}
				}
				for(ResPool_Info resPool : resList){
					if(!resPool.getparentPoolMoref().equals(parentResourcePool.getPoolMoref())|| resPool.getpoolMoref().equals(parentResourcePool.getPoolMoref())){
						continue;
					}
					ResourcePool resourcePool = new ResourcePool();
					resourcePool.setPoolName(resPool.getpoolName());
					resourcePool.setPoolMoref(resPool.getpoolMoref());
					resourcePool.setParentPoolMoref(resPool.getparentPoolMoref());
					poolList.add(resourcePool);
				}
				return poolList.toArray(new ResourcePool[0]);
			}
		} catch (Exception e) {
			logger.debug("getVirtualMachine failed");
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		return null;
	}
	
	private ResourcePool ConvertToResourcePool(ResPool_Info resPool){
		if(resPool == null){
			return null;
		}
		ResourcePool resourcePool = new ResourcePool();
		resourcePool.setPoolName(resPool.getpoolName());
		resourcePool.setPoolMoref(resPool.getpoolMoref());
		resourcePool.setParentPoolMoref(resPool.getparentPoolMoref());
		return resourcePool;
	}
	
	public int powerOnVM(VirtualCenter vc, VirtualMachine vm) {
		logger.debug("powerOnVM start");
		CAVirtualInfrastructureManager vmwareOBJ = null;
		int retVal = 0;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
					.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc
							.getUsername(), vc.getPassword(), vc.getProtocol(),
							true, 0);
			vmwareOBJ.powerOnVM(vm.getVmName(), vm.getVmUUID());
		} catch (Exception e) {
			logger.debug("powerOnVM failed");
			retVal = 1;
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		return retVal;
	}

	public int powerOffVM(VirtualCenter vc, VirtualMachine vm) {
		logger.debug("powerOffVM start");
		CAVirtualInfrastructureManager vmwareOBJ = null;
		int retVal = 0;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
					.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc
							.getUsername(), vc.getPassword(), vc.getProtocol(),
							true, 0);
			vmwareOBJ.powerOffVM(vm.getVmName(), vm.getVmUUID());
		} catch (Exception e) {
			logger.debug("powerOffVM failed");
			retVal = 1;
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		return retVal;
	}

	public int rebootVM(VirtualCenter vc, VirtualMachine vm) {
		logger.debug("rebootVM start");
		CAVirtualInfrastructureManager vmwareOBJ = null;
		int retVal = 0;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
					.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc
							.getUsername(), vc.getPassword(), vc.getProtocol(),
							true, 0);
			vmwareOBJ.rebootVM(vm.getVmName(), vm.getVmUUID());
		} catch (Exception e) {
			logger.debug("rebootVM failed");
			retVal = 1;
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		return retVal;
	}

	public int shutdownVM(VirtualCenter vc, VirtualMachine vm) {
		logger.debug("shutdownVM start");
		CAVirtualInfrastructureManager vmwareOBJ = null;
		int retVal = 0;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
					.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc
							.getUsername(), vc.getPassword(), vc.getProtocol(),
							true, 0);
			vmwareOBJ.shutdownVM(vm.getVmName(), vm.getVmUUID());
		} catch (Exception e) {
			logger.debug("shutdownVM failed");
			retVal = 1;
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		return retVal;
	}

	public int validateVC(VirtualCenter vc) throws ServiceException {
		CAVMwareVirtualInfrastructureManager vmwareOBJ = null;
		int retVal = 0;
		try {
			vmwareOBJ = new CAVMwareVirtualInfrastructureManager();
			retVal = vmwareOBJ.init(vc.getVcName(), vc.getUsername(), vc
					.getPassword(), vc.getProtocol(), true, vc.getPort());
		} catch (InvalidLoginException e) {
			logger.error("validateVC failed", e);
			throw new ServiceException(
					FlashServiceErrorCode.VSPHERE_CONNECT_VCENTER_CREDENTIAL,new Object[]{vc.getVcName()});
		}
		catch(Exception ex){
			logger.error("validateVC failed", ex);
			throw new ServiceException(FlashServiceErrorCode.VSPHERE_CONNECT_VCENTER_ERROR,new Object[]{vc.getVcName()});
			// e.printStackTrace();
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		return retVal;
	}
	
	public int isServerReachable(VMBackupConfiguration configuration){
		int ret = 0;
		try{
			if (configuration.getBackupVM().getVmType() == BackupVM.Type.VMware.ordinal() || 
				configuration.getBackupVM().getVmType() == BackupVM.Type.VMware_VApp.ordinal() ){
				VirtualCenter vc = new VirtualCenter();
				vc.setPassword(configuration.getBackupVM().getEsxPassword());
				vc.setPort(configuration.getBackupVM().getPort());
				vc.setProtocol(configuration.getBackupVM().getProtocol());
				vc.setUsername(configuration.getBackupVM().getEsxUsername());
				vc.setVcName(configuration.getBackupVM().getEsxServerName());
				ret = validateVC(vc);
			}else{
				WSJNI.GetVmList(configuration.getBackupVM().getEsxServerName(), configuration.getBackupVM().getEsxUsername(), configuration.getBackupVM().getEsxPassword(), false);
			}
		}catch (ServiceException e){
			logger.error("isServerReachable",e);
			ret = 1;
		}
		return ret;
	}
	
	public DataStore[] validateRecoveryVMToOriginal(VirtualCenter vc, BackupVM backupVM,int sessionNum) throws ServiceException{
		DataStore[] dataStore = null;
		int ret = 0;
		try{
			ret = validateVC(vc);
		}catch(ServiceException e){
			throw e;
		}
		if(ret!=0){
			throw new ServiceException(FlashServiceErrorCode.VSPHERE_CONNECT_VCENTER_ERROR,new Object[]{vc.getVcName()});
		}
		String userName = backupVM.getDesUsername();
		String domain = "";
		String pwd = "";
		if (userName != null && userName.trim().length() > 0) {
			userName = userName.trim();
			int index = userName.indexOf("\\");
			if (index > 0) {
				domain = userName.substring(0, index);
				userName = userName.substring(index + 1);
			}
			pwd = backupVM.getDesPassword();
		}
		JBackupVMOriginalInfo originalInfo = this.getNativeFacade().getBackupVMOriginalInfo(backupVM.getBrowseDestination(), sessionNum, domain, userName, pwd);
		if(originalInfo == null){
			logger.debug("failed to get backupvm original info");
		}else{
			String originalVC = originalInfo.getOriginalVcName();
			String originalEsx = originalInfo.getOriginalEsxServer();
			//check if original esx exists
			if(originalVC !=null && !originalVC.equals("")){
				try {
					ESXServer[] esxs = this.getESXServers(vc);
					
					if(esxs!=null && esxs.length>0){
						boolean oriEsxExist = false;
						ESXServer oriEsx = null;
						for(ESXServer esx : esxs){
							if(esx.getEsxName().equalsIgnoreCase(originalEsx)){
								oriEsx = esx;
								oriEsxExist = true;
								break;
							}
						}
						if(oriEsxExist){
							return this.getVMwareDataStore(vc, oriEsx);
						}else{
							logger.debug("original esx does not exist in vc"+vc.getVcName());
							throw new ServiceException(FlashServiceErrorCode.VSPHERE_ORIGINAL_ESX_NOT_EXIST,new Object[]{originalEsx,vc.getVcName()});
						}
					}
				} catch (ServiceException e){
					throw e;
				} catch (Exception e) {
					logger.debug("failed to load esxserver");
					throw new ServiceException(FlashServiceErrorCode.VSPHERE_ORIGINAL_ESX_NOT_EXIST,new Object[]{originalEsx,vc.getVcName()});
				}
				
			}else{
				try {
					ESXServer[] esxs = this.getESXServers(vc);
					return this.getVMwareDataStore(vc, esxs[0]);
				} catch (ServiceException e){
					throw e;
				} catch (Exception e) {
					logger.debug("failed to load esxserver");
					throw new ServiceException(FlashServiceErrorCode.VSPHERE_ORIGINAL_ESX_NOT_EXIST,new Object[]{originalEsx,vc.getVcName()});
				}
			}
		}
		return dataStore;
	}
	
	public int getVCStatus(VirtualCenter vc) {
		CAVMwareVirtualInfrastructureManager vmwareOBJ = null;
		int retVal = 0;
		try {
			if(vc == null)
				return retVal;
			vmwareOBJ = new CAVMwareVirtualInfrastructureManager();
			retVal = vmwareOBJ.init(vc.getVcName(), vc.getUsername(), vc
					.getPassword(), vc.getProtocol(), true, vc.getPort());
		} catch (InvalidLoginException e) {
			logger.error("validateVC failed", e);
			retVal = VM_STATUS_ERROR_VC_CREDENTIAL_WRONG;
		}
		catch(Exception ex){
			logger.error("validateVC failed", ex);
			retVal = VM_STATUS_ERROR_VC_CANNOT_CONNECT;
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		return retVal;
	}
	
	public int getVCloudStatus(VirtualCenter vc) {
		int retVal = VM_STATUS_ERROR_VC_OK;
		try {
			VCloudManagerFactory.connect(vc.getVcName(), 
					vc.getProtocol(), vc.getPort(), vc.getUsername(), vc.getPassword());
		} catch (VCloudManagerException e) {
			logger.error("Connect vCloud Director failed", e);
			retVal = VM_STATUS_ERROR_VC_CANNOT_CONNECT;
		}
		return retVal;
	}

	public int getVMToolsStatus(VirtualCenter vc,VirtualMachine vm){
		CAVMwareVirtualInfrastructureManager vmwareOBJ = null;
		int retVal = VM_TOOL_STATUS_ERROR;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
			.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc
					.getUsername(), vc.getPassword(), vc.getProtocol(),
					true, vc.getPort());
			retVal = vmwareOBJ.checkVMToolsVersion(vm.getVmName(), vm.getVmInstanceUUID());
		} 
		catch(Exception ex){
			logger.error("getVMToolsVersion failed", ex);
			if(ex.getCause()!=null && ex.getCause() instanceof ConnectException){
				return VM_TOOL_STATUS_TIMEOUT;
			}
			// e.printStackTrace();
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		return retVal;
	}
	
	public int getVMPowerStatus(VirtualCenter vc, VirtualMachine vm){
		CAVMwareVirtualInfrastructureManager vmwareOBJ = null;
		
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
			.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc
					.getUsername(), vc.getPassword(), vc.getProtocol(),
					true, vc.getPort());
			return getVMPowerState(vmwareOBJ, vm);
		} catch(Exception ex){
			logger.error("getVMPowerStatus failed", ex);
			return VM_POWER_ERROR;
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
	}
	
	private int getVMPowerState(CAVMwareVirtualInfrastructureManager vmwareOBJ, VirtualMachine vm) {
		powerState state;
		
		try {
			state = vmwareOBJ.getVMPowerstate(vm.getVmName(), vm.getVmInstanceUUID());
		} catch (Exception e) {
			logger.error("getVMPowerstate failed", e);
			return VM_POWER_ERROR;
		}
		
		switch (state) {
		case poweredOff: return VM_POWER_OFF;
		case poweredOn: return VM_POWER_ON;
		case suspended: return VM_SUSPENDED;
		default: return VM_POWER_ERROR;
		}
	}
	
	public VSphereBackupConfiguration getVSphereBackupConfiguration(
			VirtualCenter vc) throws ServiceException {
		return null;
	}

	public void detachAllExistPolicy(){
		if (!StringUtil.isExistingPath(ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath())) {
			return;
		}
		String configurationPath = ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath();
		File file = new File(configurationPath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		try{
			List<VirtualMachine> vmList = new ArrayList<VirtualMachine>();
			for (File one : files) {
				String filename = one.getName();
				if(filename.length() != 40){
					continue;
				}
				VirtualMachine vm = new VirtualMachine();
				vmList.add(vm);
			}
			detachVSpherePolicy(vmList.toArray(new VirtualMachine[0]));
		}catch(Exception se){
			logger.error("Failed to startVSphereCatalogJobForAllVM",se);
		}
	}
	
	public long detachVSpherePolicy(VirtualMachine[] vmArray) throws ServiceException {
		return detachVSpherePolicy( vmArray, true );
	}
	
	public long detachVSpherePolicy(VirtualMachine[] vmArray, boolean removePolicyInfo) throws ServiceException {
		try{
			if(vmArray == null || vmArray.length ==0){
				throw generateInternalErrorAxisFault(); 
			}
			List<String> hostuuids = new ArrayList<String>();
			for(VirtualMachine vm : vmArray){
				VMBackupConfiguration conf = this.getVMBackupConfiguration(vm);
				if(conf != null && conf.getBackupRpsDestSetting() != null){
					unregistryD2D2RPS(conf.getBackupRpsDestSetting().getRpsHost(), vm.getVmInstanceUUID());
				}
				removeAdvScheduleJob(vm.getVmInstanceUUID());
				File file = new File(ServiceContext.getInstance()
						.getVsphereBackupConfigurationFolderPath()+"\\"+vm.getVmInstanceUUID()+".xml");
				if(file.exists()){
					file.delete();
				}
				
				logger.info("VM's configuration has been deleted for plan undeployment:"+vm.getVmInstanceUUID());
				VSphereArchiveToTapeService.getInstance().deleteArchive2TapeConfigurationFile(vm.getVmInstanceUUID());				
				MachineDetailManager.getInstance().removeVSphereVMDetail(vm.getVmInstanceUUID());
				ActivityLogSyncher.getInstance().removeVM(vm.getVmInstanceUUID());
				VSphereMergeService.getInstance().removeVMStatus(vm.getVmInstanceUUID());
				
				hostuuids.add(vm.getVmInstanceUUID());
			}
			if (removePolicyInfo)
				new D2DEdgeRegistration().RemovePolicyUuidFromXml(ApplicationType.vShpereManager, hostuuids);
		}catch(Exception e){
			throw generateInternalErrorAxisFault();
		}
		return 0;
	}
	
	@Override
	protected void removeAdvScheduleJob(String vmInstanceUUID) {
		try{
			ArrayList<String> jobNameList = jobNamesMap.get(vmInstanceUUID);
			if(jobNameList!=null){
				for(String jobName : jobNameList) {
					bkpScheduler.deleteJob(new JobKey(jobName, getBackupJobGroupName(vmInstanceUUID)));
				}
				jobNameList.clear();
				jobNamesMap.put(vmInstanceUUID, jobNameList);
			}
	    }catch(Exception e){
	    	logger.error("removeVMScheduleJob", e);
	    }
	}
	
	@Override
	protected void fillJobNamesMap(String vmInstanceUUID, String jobName) {
		ArrayList<String> jobNameList = jobNamesMap.get(vmInstanceUUID);
		if(jobNameList == null){
			jobNameList = new ArrayList<String>();
			jobNamesMap.put(vmInstanceUUID, jobNameList);
		}
		jobNameList.add(jobName);
	}
	
	@Override
	protected void fillJobDetailDataMap(JobDetailImpl jd, String vmInstanceUUID, int backupType, boolean isGenerateCatalog) {
		jd.getJobDataMap().put("vm", new VirtualMachine(vmInstanceUUID));
		jd.getJobDataMap().put("jobType", backupType);
		//jd.getJobDataMap().put("jobName", jd.getName());
		//jd.getJobDataMap().put(BaseService.BACKUP_TYPE_FLAG, BackupJobUtils.getBackupTypeFlag(backupType, BackupJobUtils.BACKUPJOB_SCHEDULE_FLAG));
		jd.getJobDataMap().put(BaseService.RPS_CATALOG_GENERATION, isGenerateCatalog);
	}
	
	
	private void saveBackupStartTimeByD2DTime(VSphereBackupConfiguration configuration) {
		if(configuration.getStartTime() != null && configuration.getStartTime().getYear() > 1900) {
			configuration.setBackupStartTime(getCalTimeInMillies(configuration.getStartTime()));
		}
	}
	
	private void makeupRetentionPolicy(VSphereBackupConfiguration backupConfig) {
		if(backupConfig.getRetentionPolicy() == null) {
			RetentionPolicy policy = new RetentionPolicy();
			backupConfig.setRetentionPolicy(policy);
		}
	}
	
	boolean isVAppNode(BackupVM vm) {
		return vm.getVmType() == BackupVM.Type.VMware_VApp.ordinal();
	}
	
	private SavePolicyWarning[] saveVSphereBackupConfiguration(
			VSphereBackupConfiguration configuration, RpsPolicy4D2D policy) throws ServiceException {
		if(configuration.getBackupVMList() == null || configuration.getBackupVMList().length ==0){
			throw new ServiceException(
					FlashServiceErrorCode.VSPHERE_NO_VM_ERROR);
		}
		
		//creating regsitry key for HW Snapshot NFS Client. 
		if (configuration.isSoftwareOrHardwareSnapshotType() == false)
		{
			try
			{
				  WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\ClientForNFS\\CurrentVersion\\Default");
				   WSJNI.SetRegIntValue("Default", "AnonymousUid","SOFTWARE\\Microsoft\\ClientForNFS\\CurrentVersion\\",0);
				   WSJNI.SetRegIntValue("Default", "AnonymousGid","SOFTWARE\\Microsoft\\ClientForNFS\\CurrentVersion\\",0);
				
				} catch (IllegalArgumentException e1) {
					logger.error(e1.getMessage() == null? e1 : e1.getMessage());
				} catch (IllegalAccessException e1) {
					logger.error(e1.getMessage() == null? e1 : e1.getMessage());
				} catch (InvocationTargetException e1) {
					logger.error(e1.getMessage() == null? e1 : e1.getMessage());
				}
				catch (Exception e) 
				{
					logger.error("saveVSphereBackupConfiguration()", e);
					if (e instanceof ServiceException) 
					{
						throw (ServiceException) e;
					} else
					{
						throw generateInternalErrorAxisFault();
					}
				}
		}
		
		
		if (configuration.getGenerateType() == GENERATE_TYPE_MANUALCONVERSION){
			try {
				if (configuration.getDestination() == null) {
					logger.error("The backup destination is not confugred.");
					throw new ServiceException(FlashServiceErrorCode.BackupConfig_IvalidDestinationPath, new Object[]{});
				}
				// Just save the configuration file
				for (BackupVM vm:configuration.getBackupVMList()){
					if (vm.getInstanceUUID() == null){
						logger.error("The vm's instanceUUID is empty.");
						continue;
					}
					backupConfigurationDAO.saveVM(ServiceContext.getInstance()
							.getVsphereBackupConfigurationFolderPath(),
							configuration, vm);
				}
				return new SavePolicyWarning[0];
			} catch (Exception e) {
				logger.error("saveVSphereBackupConfiguration()", e);
				if (e instanceof ServiceException) {
					throw (ServiceException) e;
				} else {
					throw generateInternalErrorAxisFault();
				}
			}
		}
		VersionInfo versionInfo = CommonService.getInstance().getVersionInfo();
		if(versionInfo!=null && (versionInfo.getProductType()!=null)&& versionInfo.getProductType().equals("1")){
			throw new ServiceException(FlashServiceErrorCode.VSPHERE_PROXY_IS_SAAS_NODE,new Object[]{ServiceContext.getInstance().getLocalMachineName()});
		}
		
		CommonService.getInstance().validateBackupStartTime(configuration.getStartTime());
		
		saveBackupStartTimeByD2DTime(configuration);
		
		boolean backupToRPS = !configuration.isD2dOrRPSDestType(); 
		if(!backupToRPS)
			this.makeupRetentionPolicy(configuration);
		
		List<SavePolicyWarning> warningList = new ArrayList<SavePolicyWarning>();
		
		VMwareManagerCache vmwareManagerCache = new VMwareManagerCache();
		
		boolean isAdjustJavaHeapSize = getNativeFacade().isAdjustJavaHeapSize(ApplicationType.vShpereManager);
		
		//Stores all the member VM under the vApp to this set
		//and later remove the schedule for these VMs
		Set<String> memberVMSet = new HashSet<String>();
		List<BackupVM> backupVMList = new ArrayList<BackupVM>();
		for(BackupVM tempVM : configuration.getBackupVMList()) {
			backupVMList.add(tempVM);
			if(isVAppNode(tempVM)) {
				if(tempVM.getVAppMemberVMs() != null) {
					backupVMList.addAll(Arrays.asList(tempVM.getVAppMemberVMs()));
					for(BackupVM memberVM : tempVM.getVAppMemberVMs()) {
						memberVMSet.add(memberVM.getInstanceUUID());
					}
				}
			} 
		}
		
		HashMap<String, BackupVM> skipMap = new HashMap<String, BackupVM>();
		Map<Integer, Map<String, Object>> checkedHypervisorSvr = new HashMap<Integer, Map<String, Object>>();
		
		for (BackupVM vm : backupVMList) {
			if(skipMap.containsKey(vm.getInstanceUUID())) { 
				continue;
			}
			VirtualCenter vc = null;
			if (vm.getVmType() == BackupVM.Type.VMware.ordinal()){
				vc = converter.ConvertToVirtualCenter(vm);
				int vcStatus = vmwareManagerCache.validate(vc);
				if(vcStatus != VM_STATUS_ERROR_VC_OK){
					checkVCStatus(vcStatus,vm,vc,warningList);
					continue;
				}
			} else if (isVAppNode(vm)) {
				vc = converter.ConvertToVirtualCenter(vm);
				VCloudManager conn =null;
				try {
					conn = VcloudManagerConnectionCache.getVcloudManagerConnection(vc.getVcName(), vc.getUsername(), 
							vc.getPassword(), vc.getProtocol(), vc.getPort());
					if(conn == null){
						SavePolicyWarning warning = new SavePolicyWarning();
						Object[] warningMessages = new Object[1];
						warningMessages[0] = vc.getVcName();
						warning.setWarningCode(FlashServiceErrorCode.VSPHERE_CONNECT_VCLOUD_DIRECTOR_ERROR);
						warning.setType(Constants.AFRES_AFALOG_ERROR);
						warning.setVm(vm);
						warning.setWarningMessages(warningMessages);
						warningList.add(warning);
						//If the vApp node cannot connect to vcloud director,
						//then the member VM does not need to save plan XML
						if(vm.getVAppMemberVMs() != null) {
							for(BackupVM memberVM : vm.getVAppMemberVMs()) {
								skipMap.put(memberVM.getInstanceUUID(), memberVM);
							}
						}
						continue;
					}
				} catch (Exception e) {
					SavePolicyWarning warning = new SavePolicyWarning();
					Object[] warningMessages = new Object[1];
					warningMessages[0] = vc.getVcName();
					warning.setWarningCode(FlashServiceErrorCode.VSPHERE_CONNECT_VCLOUD_DIRECTOR_ERROR);
					warning.setType(Constants.AFRES_AFALOG_ERROR);
					warning.setVm(vm);
					warning.setWarningMessages(warningMessages);
					warningList.add(warning);
					//If the vApp node cannot connect to vcloud director,
					//then the member VM does not need to save plan XML
					if(vm.getVAppMemberVMs() != null) {
						for(BackupVM memberVM : vm.getVAppMemberVMs()) {
							skipMap.put(memberVM.getInstanceUUID(), memberVM);
						}
					}
					continue;
				}
			}
			
			boolean isExist = checkVMExistOnHypervisor(vmwareManagerCache, vm, warningList, checkedHypervisorSvr);
			if (!isExist) {
				continue;
			}
			
			VirtualMachine virtualMachine = converter.ConvertToVirtuaMachine(vm);
			// validate
			if(policy == null)
				policy = backupConfigurationValidator.validateRpsDestSetting(configuration);
			VMBackupConfiguration oldConfiguration = this.getVMBackupConfiguration(virtualMachine); 
			if(policy != null){	
				this.updateBackupConfiguration4RPSPolicy(policy, configuration, vm);
				this.updateBackupDestChange4RPS(oldConfiguration, configuration);
			}
			int pathMaxWithoutHostName = backupConfigurationValidator.validate(
					configuration, vm);

			// verify destination threshold value
			verifyDestThresholdValue(configuration, vm);

			try {
				synchronized (lock) {
					try {
						String originalDest = vm.getDestination();
						String dest = vm.getDestination();
						boolean isLocalOrShareFolder = configuration.isD2dOrRPSDestType();
						if (!originalDest.contains(vm.getVmName() + "@"
								+ vm.getEsxServerName().trim())) {
							dest = appendVMInfoIfNeeded(originalDest, filterVMName(vm
									.getVmName())
									+ "@" + vm.getEsxServerName().trim(), vm
									.getInstanceUUID(), isLocalOrShareFolder);
							vm.setDestination(dest);
						}
					
						if(!backupToRPS)
							this.validateChangedRetentionPolicy(configuration, vm);
						
						if (!StringUtil.isEmptyOrNull(dest)) {
							if (dest.length() > pathMaxWithoutHostName)
								backupConfigurationValidator
										.generatePathExeedLimitException(pathMaxWithoutHostName);
						}

						// saveRPSSetting has bad performance, we call saveRPSSettingEx instead at the
						// end of function  ZhangHeng
//						if(backupToRPS) {
//							//save rps setting
//							saveRPSSetting(configuration, vm);
//							// added by liuho04 for defect 88356
//							logger.info("Succeeded to saveRPSSetting in D2D for VM[InstanceUuid=" + vm.getInstanceUUID());
//							// end of added
//						}
						
						if(!isVAppNode(vm) && !memberVMSet.contains(vm.getInstanceUUID())) {
							long ret = 0;
							if (oldConfiguration == null) {
								CONN_INFO newConnection = getCONN_INFO(vm);
								CONN_INFO connection = new CONN_INFO();
								ret = getNativeFacade().initBackupDestination(
										vm.getDestination(),
										newConnection.getDomain(),
										newConnection.getUserName(),
										newConnection.getPwd(), null,
										connection.getDomain(),
										connection.getUserName(),
										connection.getPwd(),
										configuration.getChangedBackupDestType());
							} else {
								String oldDest = oldConfiguration.getBackupVM()
										.getDestination();
								if (oldDest.endsWith("\\") || oldDest.endsWith("/")) {
									oldDest = oldDest.substring(0,
											oldDest.length() - 1);
								}
	
								if (dest.endsWith("\\") || dest.endsWith("/")) {
									dest = dest.substring(0, dest.length() - 1);
								}
								if (oldDest.equalsIgnoreCase(dest)) {
									logger.debug("Is Dest Chagned: false");
								} else {
									if (!vm.getDestination().equals(
											oldConfiguration.getBackupVM()
													.getDestination())) {
										CONN_INFO newConnection = getCONN_INFO(vm);
										CONN_INFO connection = getCONN_INFO(oldConfiguration
												.getBackupVM());
										ret = this
												.getNativeFacade()
												.initBackupDestination(
														vm.getDestination(),
														newConnection.getDomain(),
														newConnection.getUserName(),
														newConnection.getPwd(),
														oldConfiguration.getBackupVM()
																.getDestination(),
														connection.getDomain(),
														connection.getUserName(),
														connection.getPwd(),
														configuration
																.getChangedBackupDestType());
									}
								}
							}
						}
					} catch (ServiceException ex) {
						logger.error(ex.getMessage(), ex);
						if(backupToRPS) {
							unregistryD2D2RPS(configuration.getBackupRpsDestSetting().getRpsHost(), 
									vm.getInstanceUUID());
						}
						throw ex;
					}
					
					unRegistryD2DIfNeed(configuration,oldConfiguration);
					ActivityLogSyncher.getInstance().addVM(vm.getInstanceUUID());
					
					if (vm.getVmType() == BackupVM.Type.VMware.ordinal()){
						int powerStatus = getVMPowerState(vmwareManagerCache.get(vc), virtualMachine);
						if(powerStatus == VM_POWER_ON){
							int vmToolStatus = getVMToolsStatus(vc,virtualMachine);
							checkVMToolsStatus(vmToolStatus,vm,warningList);
						}
						
						if (!isProxyESXVDDKCompatible(vm, warningList, vmwareManagerCache)){
							logger.info("proxy is x86 and ESX server is 5.5.0 or higher, VM plan deployment should fail");
							continue;
						}
					}else{
						if (!isHyperVSupported(vm, warningList, checkedHypervisorSvr)){
							logger.info("don't support Hyper-V server:"+vm.getEsxServerName());
							continue;
						}
					}
					
					//checkVIXVersion(version,vm,warningList);
					addJavaHeapSizeWarning(isAdjustJavaHeapSize, vm, warningList);
	
					//remove the schedule for member VM under the vApp
					if(memberVMSet.contains(vm.getInstanceUUID())) {
						VSphereBackupConfiguration newConfiguration = configuration;
						newConfiguration.setAdvanceSchedule(null);
						newConfiguration.setFullBackupSchedule(null);
						newConfiguration.setIncrementalBackupSchedule(null);
						newConfiguration.setResyncBackupSchedule(null);
						backupConfigurationDAO.saveVM(ServiceContext.getInstance()
								.getVsphereBackupConfigurationFolderPath(),
								newConfiguration, vm);
						
					} else {
						//wanqi06
						if(configuration.getAdvanceSchedule()!=null)
						{
							backupConfigurationValidator.validateAdvanceSchedule(configuration.getAdvanceSchedule());
						}

						backupConfigurationDAO.saveVM(ServiceContext.getInstance()
								.getVsphereBackupConfigurationFolderPath(),
								configuration, vm);
						this.configSchedule(vm.getInstanceUUID());
						
						this.logScheduledVMJobs(vm.getInstanceUUID());
					}
					
					MachineDetailManager.getInstance().removeVSphereVMDetail(vm.getInstanceUUID());
					
					AsyncTaskRunner.submit(VSphereBackupThrottleService.getInstance(), VSphereBackupThrottleService.class.getMethod("startImmediateTrigger"));
					
					if(configuration.getRetentionPolicy() != null 
							&& configuration.getRetentionPolicy().isUseBackupSet()){
						AsyncTaskRunner.submit(VSphereBackupSetService.getInstance(), 
								VSphereBackupSetService.class.getMethod(
								"markBackupSetFlag", VMBackupConfiguration.class), 
								this.getVMBackupConfiguration(virtualMachine));
					}else 
						AsyncTaskRunner.submit(VSphereMergeService.getInstance(), VSphereMergeService.class.getMethod(
							"scheduleMergeJob", BackupVM.class), vm);
					VSphereMergeService.getInstance().initializeMergeStatus(vm.getInstanceUUID());
					ActivityLogSyncher.getInstance().addVM(vm.getInstanceUUID());

				}
			} catch (Exception e) {
				logger.error("saveVSphereBackupConfiguration()", e);
				if (e instanceof ServiceException) {
					throw (ServiceException) e;
				} else {
					throw generateInternalErrorAxisFault();
				}
			}
		}
		
		RpsHost rpsHost = configuration.getBackupRpsDestSetting().getRpsHost();

		if(backupToRPS && rpsHost != null) 
		{
			saveRPSSettingEx(configuration);
			ConfigRPSInD2DService.getInstance().addVMToFlashListener(rpsHost, backupVMList);
		}
		
		vmwareManagerCache.close();
		SyncD2DStatusService.getInstance().syncVSphereStatus2Edge();
		return warningList.toArray(new SavePolicyWarning[0]);
	}

	private boolean isHyperVSupported(BackupVM vm,
					                  List<SavePolicyWarning> warningList,
					                  Map<Integer, Map<String, Object>> checkedHypervisorSvr)
	{
		long handle = 0;
		Map<String, Object> hpvSvr = checkedHypervisorSvr.get(vm.getVmType());
		try
		{
			if (hpvSvr != null)
			{
				Object obj = hpvSvr.get(vm.getEsxServerName());
				if (obj != null)
					handle = (long) obj;
				else
					handle = HyperVJNI.OpenHypervHandle(vm.getEsxServerName(),
									vm.getEsxUsername(), vm.getEsxPassword());
			}
			else
				handle = HyperVJNI.OpenHypervHandle(vm.getEsxServerName(),
								vm.getEsxUsername(), vm.getEsxPassword());

			int serverType = WSJNI.getHyperVServerType(handle);
			if (Cluster_Physical_Node == serverType
							|| Cluster_Virutal_Node == serverType)
			{
				String version = WSJNI.getHyperVServerOsVersion(handle);
				if (Windows_Server_2008_R2.equals(version.substring(0, 3)))
				{
					SavePolicyWarning warning = new SavePolicyWarning();
					warning.setVm(vm);
					warning.setType(Constants.AFRES_AFALOG_ERROR);
					warning.setWarningCode(FlashServiceErrorCode.VSPHERE_HYPERV_CLUSTER_VM_NOT_SUPPORT);
					warningList.add(warning);
					return false;
				}
			}
		} 
		catch (Exception e)
		{
			logger.error(e);
		}
		// move this to outside
		// finally{
		// if (handle!=0)
		// try{
		// HyperVJNI.CloseHypervHandle(handle);
		// }catch(Exception e){
		// logger.error(e);
		// }
		// }
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public SavePolicyWarning[] saveVSphereBackupConfiguration(
			VSphereBackupConfiguration configuration) throws ServiceException {
		
		return  saveVSphereBackupConfiguration(configuration, null);
	}

	private boolean checkVMExistOnHypervisor(
					VMwareManagerCache vmwareManagerCache, BackupVM vm,
					List<SavePolicyWarning> warningList,
					Map<Integer, Map<String, Object>> checkedHypervisorSvr)
	{
		boolean isExist = false;
		int vmType = vm.getVmType();
		String vmInstanceUuid = vm.getInstanceUUID();
		String hypervisorSvrName = vm.getEsxServerName();

		Map<String, Object> hypervisorSvr = checkedHypervisorSvr.get(vmType);
		if (hypervisorSvr == null)
		{
			hypervisorSvr = new HashMap<String, Object>();
			checkedHypervisorSvr.put(vmType, hypervisorSvr);
		}

		if (BackupVM.Type.VMware.ordinal() == vmType)
		{

			VirtualCenter vc = converter.ConvertToVirtualCenter(vm);
			CAVMwareVirtualInfrastructureManager vmInfManager = vmwareManagerCache
							.get(vc);
			VM_Info vmInfo = null;
			try
			{
				if (vmInfManager != null)
				{
					vmInfo = vmInfManager.getVMInfo(vm.getVmName(),
									vmInstanceUuid);
				}
			} catch (Exception e)
			{
				logger.warn(e);
				vmInfo = null;
			}

			if (vmInfo != null)
			{
				isExist = true;
			}
		} else if (BackupVM.Type.HyperV.ordinal() == vmType)
		{
			long handle = 0;
			JHypervVMInfo vmInfo = null;
			try
			{
				Object obj = hypervisorSvr.get(hypervisorSvrName);
				if (obj == null)
				{
					handle = HyperVJNI.OpenHypervHandle(vm.getEsxServerName(),
									vm.getEsxUsername(), vm.getEsxPassword());
					hypervisorSvr.put(hypervisorSvrName, handle);
				} else
					handle = (long) obj;
				vmInfo = WSJNI.getHypervVMInfo(handle, vmInstanceUuid);
			} catch (Exception e)
			{
				logger.warn(e);
				vmInfo = null;
			}
			// move to outside of function --ZhangHeng
			// finally {
			// if (handle != 0) {
			// try {
			// HyperVJNI.CloseHypervHandle(handle);
			// } catch (Exception e) {
			// logger.error(e);
			// }
			// }
			// }

			if (vmInfo != null)
			{
				isExist = true;
			}
		} 
		else if (BackupVM.Type.HyperV_Cluster.ordinal() == vmType)
		{
			String serverName = null;
			try
			{
				Object obj = hypervisorSvr.get(hypervisorSvrName);

				if (obj == null)
				{
					serverName = WSJNI.AFGetHyperVPhysicalName(
									vm.getEsxServerName(), vm.getEsxUsername(),
									vm.getEsxPassword(), vmInstanceUuid);
					hypervisorSvr.put(hypervisorSvrName, serverName);
				} 
				else
					serverName = (String) obj;

			} catch (Exception ex)
			{
				logger.warn(ex);
				serverName = null;
			}

			if (serverName != null && !serverName.trim().isEmpty())
			{
				isExist = true;
			}
		} 
		else if (BackupVM.Type.VMware_VApp.ordinal() == vmType)
		{
			VCloudManager vCloudManager = null;
			VCloudVApp vApp = null;
			try
			{
				Object obj = hypervisorSvr.get(hypervisorSvrName);

				if (obj == null)
				{
					vCloudManager = VCloudManagerFactory.connect(
									vm.getEsxServerName(), vm.getProtocol(),
									vm.getPort(), vm.getEsxUsername(),
									vm.getEsxPassword());
					if (vCloudManager == null)
					{
						logger.warn("Failed to build connection.");
					} else
						hypervisorSvr.put(hypervisorSvrName, vCloudManager);
				} else
				{
					vCloudManager = (VCloudManager) obj;
					vApp = vCloudManager.getVApp(vm.getInstanceUUID());
				}
			} catch (Exception e)
			{
				logger.error("Failed to get vCenters for vCloud", e);
				vApp = null;
			}
			// move to outside of function --ZhangHeng
			// finally {
			// if (vCloudManager != null) {
			// vCloudManager.disconnect();
			// }
			// }

			if (vApp != null)
			{
				isExist = true;
			}
		} 
		else
		{
			SavePolicyWarning warning = new SavePolicyWarning();
			warning.setVm(vm);
			warning.setType(Constants.AFRES_AFALOG_ERROR);
			warning.setWarningCode(FlashServiceErrorCode.VSPHERE_VM_UNKNOWN_TYPE);
			warningList.add(warning);

			return false;
		}

		if (isExist)
		{
			return true;
		}
		else
		{
			SavePolicyWarning warning = new SavePolicyWarning();
			warning.setVm(vm);
			warning.setType(Constants.AFRES_AFALOG_ERROR);
			warning.setWarningCode(FlashServiceErrorCode.VSPHERE_VM_NOT_EXIST_ON_HYPERVISOR);
			Object[] warningMessages = new Object[1];
			warningMessages[0] = vm.getEsxServerName();
			warning.setWarningMessages(warningMessages);
			warningList.add(warning);

			return false;
		}
	}
	

	private void validateChangedRetentionPolicy(VSphereBackupConfiguration configuration, 
			BackupVM vm) throws ServiceException {
		if(vm == null)
			return;
		VMBackupConfiguration backupConfiguration = getVMBackupConfiguration(converter
				.ConvertToVirtuaMachine(vm));
		if(backupConfiguration == null)
			return;
		
		if(backupConfiguration.getRetentionPolicy() != null){
				if(backupConfiguration.getRetentionPolicy().isUseBackupSet() 
						&& configuration.getRetentionPolicy().isUseBackupSet()
						|| !backupConfiguration.getRetentionPolicy().isUseBackupSet()
						&& !configuration.getRetentionPolicy().isUseBackupSet()){
					return;
				}else{
					if(!backupConfigurationValidator.isCleanDestination(vm.getDestination(), configuration.getUserName(),
							configuration.getPassword())){
						throw new ServiceException(
								WebServiceMessages.getResource("backupConfRetentionChangeOldDest"),
								FlashServiceErrorCode.BackupConfig_ERR_RETENTION_CHANGE);
					}else if(configuration.getChangedBackupDestType() != BackupType.Full) {
						configuration.setChangedBackupDestType(BackupType.Full);
					}
				}
		}else if(configuration.getRetentionPolicy().isUseBackupSet()) {
			if(!backupConfigurationValidator.isCleanDestination(vm.getDestination(), configuration.getUserName(),
					configuration.getPassword())){
				throw new ServiceException(
						WebServiceMessages.getResource("backupConfRetentionChangeOldDest"),
						FlashServiceErrorCode.BackupConfig_ERR_RETENTION_CHANGE);
			}else if(configuration.getChangedBackupDestType() != BackupType.Full) {
				configuration.setChangedBackupDestType(BackupType.Full);
			}
		}	
	}
	
	public void saveHasSendEmail(boolean hasSendEmail, boolean allowSendEmail,BackupVM vm,VMBackupConfiguration configuration) throws Exception{
    	synchronized (lock) {
    		configuration.setAllowSendEmail(allowSendEmail);
    		configuration.setHasSendEmail(hasSendEmail);
    		backupConfigurationDAO.saveVM(ServiceContext.getInstance()
					.getVsphereBackupConfigurationFolderPath(),
					backupConfigurationDAO.VMConfigToVSphereConfig(configuration),vm);
    	}
    }
	
	private void addJavaHeapSizeWarning(Boolean isAdjustJavaHeapSize, BackupVM vm,List<SavePolicyWarning> warningList){
		if(isAdjustJavaHeapSize){
			try {
				boolean isAMD64 = false;
				short cpu = getNativeFacade().GetHostProcessorArchitectural();
				if(cpu == 9){
					isAMD64 = true;
				}
				
				String keyName = "Java";
				String valueName = "JvmMx";
				JRWLong jValue = new JRWLong();
				WSJNI.GetRegIntValue(keyName, valueName, isAMD64?ApacheServiceUtil.AMD64_PATH:ApacheServiceUtil.X86_PATH, jValue);
				
				String hostName = InetAddress.getLocalHost().getHostName();
				SavePolicyWarning warning = new SavePolicyWarning();
				warning.setVm(vm);
				warning.setType(Constants.AFRES_AFALOG_WARNING);
				warning.setWarningCode(FlashServiceErrorCode.VSPHERE_APPLY_POLICY_ADJUST_JAVA_HEAP);
				//Bug 761293
				//warning.setWarningMessages(new Object[]{hostName, (int)jValue.getValue()});
				warning.setWarningMessages(new Object[]{hostName});
				warningList.add(warning);
			} catch (Exception e) {
				logger.error(e);
			}
			
		}
	}
	
	private void checkVMToolsStatus(int status , BackupVM vm,List<SavePolicyWarning> warningList){
		if(status == VM_TOOL_STATUS_OK){
			return;
		}else{
			SavePolicyWarning warning = new SavePolicyWarning();
			warning.setVm(vm);
			Object[] warningMessages = new Object[1];
			warningMessages[0] = vm.getVmName();
			String warningCode = "";
			if(status == VM_TOOL_STATUS_TIMEOUT){
				// defect 124341, ignore connection timeout
				return;
			}
			if(status == VM_TOOL_STATUS_ERROR){
				warningCode = FlashServiceErrorCode.VSPHERE_VM_TOOL_STATUS_ERROR;
				this.getNativeFacade().addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_VSPHERE_VMTOOL_ERROR,new String[]{vm.getVmName(), BackupConverterUtil.dateToString(new Date()),"","",""},vm.getInstanceUUID());
			}else if(status == VM_TOOL_STATUS_NOT_INSTALL){
				warningCode = FlashServiceErrorCode.VSPHERE_VM_TOOL_STATUS_NOT_INSTALL;
				this.getNativeFacade().addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_VSPHERE_VMTOOL_NOT_INSTALL,new String[]{vm.getVmName(), BackupConverterUtil.dateToString(new Date()),"","",""},vm.getInstanceUUID());
			}else if (status == VM_TOOL_STATUS_OUTOFDATE){
				warningCode = FlashServiceErrorCode.VSPHERE_VM_TOOL_STATUS_OUTOFDATE;
				this.getNativeFacade().addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_VSPHERE_VMTOOL_OUT_OF_DATE,new String[]{vm.getVmName(), BackupConverterUtil.dateToString(new Date()),"","",""},vm.getInstanceUUID());
			}
			warning.setType(Constants.AFRES_AFALOG_WARNING);
			warning.setWarningCode(warningCode);
			warning.setWarningMessages(warningMessages);
			warningList.add(warning);
		}
	}
	
	private boolean isProxyESXVDDKCompatible(BackupVM vm, List<SavePolicyWarning> warningList, VMwareManagerCache vmwareManagerCache){
		boolean isAMD64 = false;
		try {
			short cpu = WSJNI.GetHostProcessorArchitectural();
			if(cpu == 9){
				isAMD64 = true;
			}
		} catch (Exception e) {
			logger.error(e);
			return true;
		}
		
		VMBackupConfiguration configuration = new VMBackupConfiguration();
		configuration.setBackupVM(vm);
		String esxHost = null;
		try{
			esxHost = VSphereLicenseCheck.getNormalizedESXHostName(configuration);
		}catch(Exception e){
			logger.error(e);
			return true;
		}
		
		String version = null;
		CAVMwareVirtualInfrastructureManager manager = vmwareManagerCache.get(converter.ConvertToVirtualCenter(vm));
		try{
			if (manager.getVMwareServerType() == VMwareServerType.esxServer)
				version = manager.GetESXServerVersion();
			else{
				ArrayList<ESXNode> nodes = manager.getESXNodeList();
				for (ESXNode node:nodes){
					if (node.getEsxName().equalsIgnoreCase(esxHost)){
						version = manager.getESXHostVersion(node);
						break;
					}
				}
			}
		}catch(Exception e){
			logger.error(e);
			return true;
		}
		
		//if (!isAMD64 && "5.5.0".equals(version)){
		if (!isAMD64 && compareTwoVersions("5.5.0",version)<=0){
			SavePolicyWarning warning = new SavePolicyWarning();
			warning.setVm(vm);
			warning.setType(Constants.AFRES_AFALOG_ERROR);
			warning.setWarningCode(FlashServiceErrorCode.VSPHERE_VM_PROXY_ESX_X86_COMPATIBLE);
			warningList.add(warning);
			return false;
		}
		
		return true;
	}
	
	private void checkVMPowerStatus(int status , BackupVM vm,List<SavePolicyWarning> warningList){
		if(status == VM_POWER_ON){
			return;
		}else{
			SavePolicyWarning warning = new SavePolicyWarning();
			warning.setType(Constants.AFRES_AFALOG_WARNING);
			warning.setVm(vm);
			Object[] warningMessages = new Object[1];
			warningMessages[0] = vm.getVmName();
			String warningCode = getVMPowerStatusErrorCode(status);
			warning.setWarningCode(warningCode);
			warning.setWarningMessages(warningMessages);
			warningList.add(warning);
		}
	}
	
	private void checkVCStatus(int status, BackupVM vm,VirtualCenter vc,List<SavePolicyWarning> warningList){
		SavePolicyWarning warning = new SavePolicyWarning();
		String warningCode = getVCStatusErrorCode(status);
		Object[] warningMessages = new Object[1];
		warningMessages[0] = vc.getVcName();
		warning.setWarningCode(warningCode);
		warning.setType(Constants.AFRES_AFALOG_ERROR);
		warning.setVm(vm);
		warning.setWarningMessages(warningMessages);
		warningList.add(warning);
	}
	
	
	private long getMajorVer(String version)	{
		int idx = version.indexOf('.');
		if(idx > 0)
			version = version.substring(0, idx);
		return Long.valueOf(version);
	}
	
	public int checkVIXStatus(){
		String version = this.getNativeFacade().getVIXVersion();
		int status = VIX_STATUS_OK;
		if(version !=null) {
			long majorVer = getMajorVer(version);
			long checkMajorVer = getMajorVer(VIX_VERSION_REQUIRED);
			if(version.equals(VIX_NOT_INSTALL)){
				status = VIX_STATUS_NOT_INSTALL;
			}
			else if(majorVer < checkMajorVer){
				status = VIX_STATUS_OUT_OF_DATE;
			}
		}else{
			status = VIX_STATUS_NOT_INSTALL;
		}
		return status;
	}
	
	private String getVCStatusErrorCode(int status){
		String errorCode = null;
		switch(status){
		case VM_STATUS_ERROR_VC_CREDENTIAL_WRONG:
			errorCode = FlashServiceErrorCode.VSPHERE_CONNECT_VCENTER_CREDENTIAL;
			break;
		case VM_STATUS_ERROR_VC_CANNOT_CONNECT:
			errorCode = FlashServiceErrorCode.VSPHERE_CONNECT_VCENTER_ERROR;
			break;
			default:
				break;
		}
		return errorCode;
	}
	
	private String getVMPowerStatusErrorCode(int status){
		String errorCode = null;
		switch(status){
		case VM_POWER_ERROR:
			errorCode = FlashServiceErrorCode.VSPHERE_VM_POWER_ERROR;
			break;
		case VM_POWER_OFF:
			errorCode = FlashServiceErrorCode.VSPHERE_VM_POWER_OFF;
			break;
		case VM_SUSPENDED:
			errorCode = FlashServiceErrorCode.VSPHERE_VM_POWER_SUSPENDED;
			break;
			default:
				break;
		}
		return errorCode;
	}
	
	public List<VMStatus> checkVMStatus(VMBackupConfiguration config, BackupVM backupVM, VirtualCenter vc) {
		List<VMStatus> statusList = new ArrayList<VMStatus>();
		
		if (needToCheckVIXInstalled(config, vc)){
			int vixStatus = checkVIXStatus();
			if(vixStatus != VIX_STATUS_OK && isShowVIXNotInstallMessage(config)){
				VMStatus vixWarning = new VMStatus();
				vixWarning.setStatus(vixStatus);
				vixWarning.setStatusType(VM_STATUS_TYPE_WARNING);
				vixWarning.setSubType(WARNING_TYPE_VIX);
				vixWarning.setParameters(new String[]{ServiceContext.getInstance().getLocalMachineName()});
				statusList.add(vixWarning);
			}
		}
		
		VirtualMachine virtualMachine = converter.ConvertToVirtuaMachine(backupVM);
		int powerStatus = getVMPowerStatus(vc,virtualMachine);
		if(powerStatus == VM_POWER_ON){
			int toolStatus = getVMToolsStatus(vc, virtualMachine); 
			if(toolStatus != VM_TOOL_STATUS_OK){
				if(toolStatus==VM_TOOL_STATUS_TIMEOUT)
					toolStatus=VM_TOOL_STATUS_ERROR;
				VMStatus toolWarning = new VMStatus();
				toolWarning.setStatusType(VM_STATUS_TYPE_WARNING);
				toolWarning.setStatus(toolStatus);
				toolWarning.setSubType(WARNING_TYPE_VMTOOL);
				toolWarning.setParameters(new String[]{backupVM.getVmName()});
				statusList.add(toolWarning);
			}
		}else{
			VMStatus powerWarning = new VMStatus();
			powerWarning.setStatusType(VM_STATUS_TYPE_WARNING);
			powerWarning.setStatus(powerStatus);
			powerWarning.setSubType(WARNING_TYPE_VMPOWER);
			powerWarning.setParameters(new String[]{backupVM.getVmName()});
			statusList.add(powerWarning);
		}
		return statusList;
	}
	
	public VMStatus[] getVMStatus(VirtualMachine vm) {
		try{
			VMBackupConfiguration config = this.getVMBackupConfiguration(vm);
			if (config.getBackupVM().getVmType() == BackupVM.Type.VMware.ordinal())
				return getVMwareVMStatus(config);
			else
				return getHyperVMStatus(config);
			
		}catch(Exception e){
			logger.error(e);
			return null;
		}
	}
	
	private VMStatus[] getHyperVMStatus(VMBackupConfiguration config) {
		
		List<VMStatus> statusList = new ArrayList<VMStatus>();
		BackupVM vm = config.getBackupVM();
		long handle;
		try {
			handle = HyperVJNI.OpenHypervHandle(vm.getEsxServerName(), vm.getEsxUsername(), vm.getEsxPassword());
			int state = HyperVJNI.GetVmState(handle, vm.getInstanceUUID());
			
			if (state != VMPowerStatus.power_on.ordinal()){
				VMStatus powerWarning = new VMStatus();
				powerWarning.setStatusType(VM_STATUS_TYPE_WARNING);
				powerWarning.setStatus(VM_POWER_OFF);
				powerWarning.setSubType(WARNING_TYPE_VMPOWER);
				statusList.add(powerWarning);
			}
		} catch (HyperVException e) {
			logger.error(e);
		}
	
		return statusList.toArray(new VMStatus[0]);
	}

	private VMStatus[] getVMwareVMStatus(VMBackupConfiguration config) {
		boolean isVApp = false;
		List<VMStatus> statusList = new ArrayList<VMStatus>();
		try {
			
			BackupVM configVM = config.getBackupVM();
			if (configVM.getVmType() != BackupVM.Type.VMware.ordinal() && 
				configVM.getVmType() != BackupVM.Type.VMware_VApp.ordinal()){
				logger.debug("VM "+ configVM.getInstanceUUID()+" is not VMware VM, no need to check VM status");
				return null;
			}
			VirtualCenter vc = null;
			int vcStatus = VM_STATUS_ERROR_VC_OK;
			if(configVM.getVmType() == BackupVM.Type.VMware_VApp.ordinal()) {
				isVApp = true;
				//check vCloud Director access status
				vc = converter.ConvertToVirtualCenter(configVM);
				vcStatus = getVCloudStatus(vc);
				if(vcStatus != VM_STATUS_ERROR_VC_OK) {
					VMStatus vcError = new VMStatus();
					vcError.setStatusType(VM_STATUS_TYPE_ERROR);
					vcError.setStatus(vcStatus);
					vcError.setSubType(VM_STATUS_ERROR_TYPE_VCLOUD_DIRECTOR);
					vcError.setParameters(new String[]{vc.getVcName()});
					statusList.add(vcError);
				}
				//If the vApp does not have any member VM, then will not check the VCenter access
				if(configVM.getVAppMemberVMs() != null && configVM.getVAppMemberVMs().length > 0) {
					vc = converter.ConvertToVirtualCenter(configVM.getVAppMemberVMs()[0]);
				} else {
					vc = null;
				}
			}else {
				vc = converter.ConvertToVirtualCenter(configVM);
			}
			
			vcStatus = getVCStatus(vc);
			if(vcStatus == VM_STATUS_ERROR_VC_OK){
				if(isVApp) {
					if(configVM.getVAppMemberVMs() != null && configVM.getVAppMemberVMs().length > 0) {
						List<BackupVM> windowsVMs = new ArrayList<BackupVM>();
						for(BackupVM memberVM : configVM.getVAppMemberVMs()) {
							vc = converter.ConvertToVirtualCenter(memberVM);
							if(!isWindowsVM(vc, memberVM.getInstanceUUID())){
								continue; //skip checking non-windows VM
							} else {
								windowsVMs.add(memberVM);
							}
						}
						for(BackupVM checkVM : windowsVMs) {
							statusList.addAll(checkVMStatus(config, checkVM, vc));
						}
					}
				} else {
					if (!isWindowsVM(vc, configVM.getInstanceUUID())){
						logger.debug("This is not a Windows VM, not need to check other VM status");
						return statusList.toArray(new VMStatus[0]);
					} else {
						statusList.addAll(checkVMStatus(config, configVM, vc));
					}
				}
			} else {
				VMStatus vcError = new VMStatus();
				vcError.setStatusType(VM_STATUS_TYPE_ERROR);
				vcError.setStatus(vcStatus);
				vcError.setSubType(VM_STATUS_ERROR_TYPE_VC);
				vcError.setParameters(new String[]{vc.getVcName()});
				statusList.add(vcError);
			}
		} catch (Exception e) {
			logger.debug("Failed to getVMStatus",e);
		}
		return statusList.toArray(new VMStatus[0]);
	}
	
	private boolean isWindowsVM(VirtualCenter vc, String instanceUUID) {
		CAVMwareVirtualInfrastructureManager vmwareOBJ = null;
		
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc
					.getUsername(), vc.getPassword(), vc.getProtocol(), true, vc.getPort());
			VM_Info vmInfo = vmwareOBJ.getVMInfo("", instanceUUID);
			return vmInfo.getvmGuestOS()!=null && vmInfo.getvmGuestOS().contains("Microsoft");
		} catch(Exception ex){
			logger.error("isWindowsVM failed", ex);
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		return false;
	}

	/*private void checkVIXVersion(String version , BackupVM vm,List<SavePolicyWarning> warningList){
		try{
			SavePolicyWarning warning = new SavePolicyWarning();
			String warningCode = "";
			if(version !=null){
				long majorVer = getMajorVer(version);
				long checkMajorVer = getMajorVer(VIX_VERSION_REQUIRED);
				if(version.equals(VIX_NOT_INSTALL)){
					throw new ServiceException(
							FlashServiceErrorCode.VSPHERE_VIX_NOT_INSTALL);
					warningCode = FlashServiceErrorCode.VSPHERE_VIX_NOT_INSTALL;
					this.getNativeFacade().addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_VSPHERE_VIX_NOT_INSTALL,new String[]{vm.getVmName(), BackupConverterUtil.dateToString(new Date()),"","",""},vm.getInstanceUUID());
					warning.setWarningCode(warningCode);
					warningList.add(warning);
				}else{
					if(majorVer < checkMajorVer) {
						throw new ServiceException(
								FlashServiceErrorCode.VSPHERE_VIX_VERSION_ERROR);
						warningCode = FlashServiceErrorCode.VSPHERE_VIX_VERSION_ERROR;
						this.getNativeFacade().addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_VSPHERE_VIX_OUT_OF_DATE,new String[]{vm.getVmName(), BackupConverterUtil.dateToString(new Date()),"","",""},vm.getInstanceUUID());
						warning.setWarningCode(warningCode);
						warningList.add(warning);
					}
				}
			}else{
				throw new ServiceException(
						FlashServiceErrorCode.VSPHERE_VIX_NOT_INSTALL);
				warningCode = FlashServiceErrorCode.VSPHERE_VIX_NOT_INSTALL;
				this.getNativeFacade().addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_VSPHERE_VIX_NOT_INSTALL,new String[]{vm.getVmName(), BackupConverterUtil.dateToString(new Date()),"","",""},vm.getInstanceUUID());
				warning.setWarningCode(warningCode);
				warningList.add(warning);
			}
			
		} catch (Exception e){
			logger.debug("Failed to get vix version");
		}
	} */
	
	@SuppressWarnings("deprecation")
	private void verifyDestThresholdValue(
			VSphereBackupConfiguration configuration, BackupVM backupVM)
			throws ServiceException {
		try {
			if (configuration.isEnableSpaceNotification()){
				
				CONN_INFO info = getCONN_INFO(backupVM);
				JBackupInfoSummary summary = this.getNativeFacade()
						.GetBackupInfoSummary(backupVM.getDestination(),
								info.getDomain(), info.getUserName(),
								info.getPwd(), true);
				
				double configFreeSpace = 0;
				long actualFreeSpace = StringUtil.string2Long(summary.getDestinationInfo().getTotalFreeSize(), 0) >> 20;
				long actualTotalSpace = StringUtil.string2Long(summary.getDestinationInfo().getTotalSize(), 0) >> 20;
				
				if ("%".equals(configuration.getSpaceMeasureUnit())) {
					configFreeSpace = (actualTotalSpace * configuration.getSpaceMeasureNum())/100;
				}else if ("MB".equals(configuration.getSpaceMeasureUnit())){
					configFreeSpace = configuration.getSpaceMeasureNum();
				}

				if (configFreeSpace >= actualFreeSpace) {
					logger.debug("configFreeSpace: " + configFreeSpace);
					logger.debug("actualFreeSpace" + actualFreeSpace);
					throw new ServiceException(
							FlashServiceErrorCode.BackupConfig_ERR_ThresholdIseTooBig);
				}
			}
		} catch (ServiceException se) {
			if (se instanceof ServiceException) {
				throw se;
			}
		} catch (Throwable e) {
			logger.error(
					"destination threshold is bigger than actural free space",
					e);
			throw generateInternalErrorAxisFault();
		}
	}

	@Override
	public VMBackupConfiguration getBackupConfiguration(String vmInstanceUUID) throws ServiceException {
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vmInstanceUUID);
		
		return getVMBackupConfiguration(vm);
	}
	
	public String getPlanUUIDByVMInstanceUUID(String vmInstanceUUID)
	{
		try
		{
			return getBackupConfiguration(vmInstanceUUID).getPlanGUID();
		}
		catch (ServiceException e)
		{
			logger.error("getPlanUUIDByVMInstanceUUID EXCEPTION " + vmInstanceUUID, e);
			return null;
		}
	}
	
	public VMBackupConfiguration getVMBackupConfiguration(VirtualMachine vm)
			throws ServiceException {
		if(vm == null ){
			return null;
		}
		if (!StringUtil.isExistingPath(ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath())) {
			return null;
		}
		try {
			VMBackupConfiguration backupConfiguration = backupConfigurationDAO.get(
					ServiceContext.getInstance()
					.getVsphereBackupConfigurationFolderPath(), vm);
			if(backupConfiguration != null) {
				if(backupConfiguration.getRetentionPolicy() == null) {
					RetentionPolicy policy = new RetentionPolicy();
					backupConfiguration.setRetentionPolicy(policy);
				}
				long throttle = VSphereBackupThrottleJob.getCurrentThrottling(backupConfiguration);
				backupConfiguration.setThrottling(throttle);
				// October sprint 
				backupConfiguration.setStorageApplianceList(getStorageApplianceConfiguration());
				backupConfiguration.setScheduledExportConfiguration(VMCopyService.getInstance().getScheduledExportConfiguration(vm));
			}
			return backupConfiguration;
		} catch (ServiceException ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		} catch (Exception e) {
			logger.error("getVMBackupConfiguration()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	// October sprint 
	public List<StorageAppliance> getStorageApplianceConfiguration()
			throws ServiceException {
		if (!StringUtil.isExistingPath(ServiceContext.getInstance()
				.getStorageApplianceConfigurationFilePath())) {
			return null;
		}
		try {
			List<StorageAppliance> storageApplianceList = storageApplianceDAO.get(
					ServiceContext.getInstance()
					.getStorageApplianceConfigurationFilePath());
			return storageApplianceList;
		} catch (ServiceException ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		} catch (Exception e) {
			logger.error("getStorageApplianceConfiguration()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	// Feb sprint 
	public void saveStorageApplianceConfiguration(List<StorageAppliance> storageApplianceList)
			throws ServiceException {
		if (!StringUtil.isExistingPath(ServiceContext.getInstance()
				.getStorageApplianceConfigurationFilePath())) {
		}
		try {
			storageApplianceDAO.save(ServiceContext.getInstance().getStorageApplianceConfigurationFilePath(), storageApplianceList);
		} catch (ServiceException ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		} catch (Exception e) {
			logger.error("getStorageApplianceConfiguration()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	
	public VMItem[] getConfiguredVM() throws ServiceException {
		return getConfiguredVMByGenerateType(0);
	}
	
	public VMItem[] getConfiguredVMByGenerateType(int generateType) throws ServiceException {
		if (!StringUtil.isExistingPath(ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath())) {
			return null;
		}
		String configurationPath = ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath();
		File file = new File(configurationPath);
		if (!file.exists())
			return null;
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return null;
		}
		try {
			HashMap<String, VMItem> map = new HashMap<String, VMItem>();
			List<VMItem> vmList = new ArrayList<VMItem>();
			VMItem temp = null;
			VirtualMachine tempVM = null;
			VMBackupConfiguration vmConfig = null;
			String instanceUUID = null;
			for (File one : files) {
				String filename = one.getName();
				if(filename.length() != 40){
					continue;
				}
				
				tempVM = new VirtualMachine();
				vmConfig = new VMBackupConfiguration();
				
				instanceUUID = new String();
				instanceUUID = filename.substring(0, filename.lastIndexOf('.'));
				tempVM.setVmInstanceUUID(instanceUUID);

				vmConfig = this.getVMBackupConfiguration(tempVM);
				if (vmConfig.getGenerateType() == generateType)
				{
					BackupVM configVM = vmConfig.getBackupVM();
					temp = converter.VMItemConverter(configVM);
					
					if(!map.containsKey(instanceUUID)) {
						map.put(instanceUUID, temp);
					} else {
						continue;
					}
					
					if(vmConfig.isEnableEncryption())
						temp.setEnableEncryption(1);
					else
						temp.setEnableEncryption(0);
					
					if (!vmConfig.isD2dOrRPSDestType())
					{
						BackupRPSDestSetting backupRpsDestSetting = vmConfig.getBackupRpsDestSetting();
						if(backupRpsDestSetting != null)
						{
							if(backupRpsDestSetting.isDedupe())
								temp.setDestType(3);//dedup data store
							else
								temp.setDestType(2);//merge phase II data store
						}
						else
							temp.setDestType(0);
					}
					else if(!vmConfig.isDisablePlan())
					{
						temp.setDestType(1);//standalone d2d with rps plan, local disk
					}
					else
					{
						temp.setDestType(0);//standalone d2d with local disk
					}
					
					if(configVM.getVmType() == BackupVM.Type.VMware_VApp.ordinal()) {
						if(configVM.getVAppMemberVMs() != null) {
							temp.setVmType(configVM.getVmType());
							List<VMItem> lst = new ArrayList<VMItem>();
							for(int i = 0; i < configVM.getVAppMemberVMs().length; ++i) {
								VMItem subItem = converter.VMItemConverter(configVM.getVAppMemberVMs()[i]); 
								subItem.setEnableEncryption(temp.getEnableEncryption());
								subItem.setDestType(temp.getDestType());
								subItem.setVmType(BackupVM.Type.VMware.ordinal());
								lst.add(subItem);
								//if the VM is under the vApp, then remove the upper level VM
								if(map.containsKey(subItem.getVmInstanceUUID())) {
									map.remove(subItem.getVmInstanceUUID());
									for(VMItem sameVM : vmList) {
										if(sameVM.getVmInstanceUUID().equalsIgnoreCase(subItem.getVmInstanceUUID())) {
											vmList.remove(sameVM);
											break;
										}
									}
								}
								map.put(subItem.getVmInstanceUUID(), subItem);
							}
							temp.setVmItems(lst.toArray(new VMItem[lst.size()]));
						}
					}
					vmList.add(temp);
				}
			}
			return vmList.toArray(new VMItem[vmList.size()]);
		} catch (ServiceException ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		} catch (Exception e) {
			logger.error("getConfiguredVMByGenerateType()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	//sonle01 20110225
	public List<String> getSelfManagedVMFromEdge() throws ServiceException{
		D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
		EdgeRegInfo edgeRegInfo = edgeReg.getEdgeRegInfo(ApplicationType.vShpereManager);
		if(edgeRegInfo == null || edgeRegInfo.getEdgeWSDL() == null || edgeRegInfo.getEdgeWSDL().isEmpty()) {
			logger.debug("The D2D VSPhere proxy is not managed by any Edge VSPhere Manager!");
			return null;
		}
		
		IEdgeCM4D2D proxy =WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(),IEdgeCM4D2D.class);
		if(proxy == null)
		{
			logger.error("D2DSync - Failed to get proxy handle!!\n");
			throw new ServiceException("Cannot get Edge proxy handle", "EdgeConnectError");
		}
		
		String UUID = CommonService.getInstance().getNodeUUID();

		try {
			proxy.validateUserByUUID(edgeRegInfo.getEdgeUUID());
		}catch(EdgeServiceFault e) {
			logger.error("D2DSync - Failed to establish connection to Edge Server(login failed)\n");
			throw new ServiceException(e.getMessage(), "EdgeLoginError");
		}
		
		try {
			List<String> vmList = proxy.getManagedVMbyProxy(UUID);
			logger.info("The instance UUIDs of the VMs which are managed by proxy are " + Arrays.toString(vmList.toArray(new String[0])));
			return vmList;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(),"EdgeConnectError");
		}
	}
	
	//sonle01 20110225
	public List<VirtualMachine> RefreshBackupConfigSettingWithEdge() throws ServiceException {
		VMItem[] 				vmItemLst 		= null;
		List<String>			managedVMLst	= null;
		List<VirtualMachine>	stillManagedVM	= new ArrayList<VirtualMachine>();
		List<VirtualMachine>	unManagedVM	= new ArrayList<VirtualMachine>();
		
		stillManagedVM.clear();
		try {
			managedVMLst = getSelfManagedVMFromEdge();
			vmItemLst = getConfiguredVM();
			
			if(vmItemLst == null)
				return stillManagedVM;
			
			for(int i = 0 ; i < vmItemLst.length ; i++) {
				VirtualMachine vm = new VirtualMachine();
				vm.setVmInstanceUUID(vmItemLst[i].getVmInstanceUUID());
				if(managedVMLst != null && managedVMLst.contains(vmItemLst[i].getVmInstanceUUID())) {
					stillManagedVM.add(vm);
					logger.debug("The VM [instanceUUID=" + vm.getVmInstanceUUID() + "] is still managed by the proxy.");
				} else {
					unManagedVM.add(vm);
					logger.debug("The VM [instanceUUID=" + vm.getVmInstanceUUID() + "] is not managed by the proxy.");
				}
			}
			
			Iterator<VirtualMachine> vmIterator = unManagedVM.iterator();
			while (vmIterator.hasNext()) {
				VirtualMachine vm = vmIterator.next();
				if (PolicyDeploymentCache.getInstance().isDeployingVM(vm.getVmInstanceUUID())) {
					vmIterator.remove();
					logger.info("The VM [instanceUUID=" + vm.getVmInstanceUUID() + "] is being deployed to the proxy, " +
							"remove it from the need unassignment list.");
				}
			}
			
			if(!unManagedVM.isEmpty()) {
				logger.warn(WebServiceMessages.getResource("autoUnassignPolicy"));
				detachVSpherePolicy(unManagedVM.toArray(new VirtualMachine[unManagedVM.size()]));
			}
			
			return stillManagedVM;
		} catch(ServiceException ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		}catch (Exception e) {
			logger.error("CheckBackupConfigSettingWithEdge()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	public int checkPolicyFromEdge(String vmInstanceUuid, String policyUuid, boolean justcheck) throws ServiceException{
		D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
		EdgeRegInfo edgeRegInfo = edgeReg.getEdgeRegInfo(ApplicationType.vShpereManager);
		if(edgeRegInfo == null || edgeRegInfo.getEdgeWSDL() == null || edgeRegInfo.getEdgeWSDL().isEmpty()) {
			return PolicyCheckStatus.UNKNOWN;
		}

		try{
		IEdgeCM4D2D proxy =WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(),IEdgeCM4D2D.class);
		
		try {
			proxy.validateUserByUUID(edgeRegInfo.getEdgeUUID());
		}catch(EdgeServiceFault e) {
			logger.error("D2DSync - Failed to establish connection to Edge Server(login failed)\n");
			return PolicyCheckStatus.UNKNOWN;
		}
		
		try {
			return proxy.checkPolicyStatus(vmInstanceUuid, policyUuid, justcheck);
		} catch (EdgeServiceFault e) {
			logger.error(e.getMessage() == null? e : e.getMessage());
			return PolicyCheckStatus.UNKNOWN;
		}
		}catch(Exception e){
			logger.error(e.getMessage() == null? e : e.getMessage());
			return PolicyCheckStatus.UNKNOWN;
		}
	}
	
	public boolean isVSphereProxy() {
		return true;
	}

	public VMItem[] getAllVM(VirtualCenter vc) throws Exception {
		logger.debug("getAllVM start");
		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
					.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc
							.getUsername(), vc.getPassword(), vc.getProtocol(),
							true, vc.getPort());
			ArrayList<ESXNode> nodeList = vmwareOBJ.getESXNodeList();

			if (nodeList != null && nodeList.size() != 0) {
				List<VMItem> vmItemList = new ArrayList<VMItem>();
				VMItem item = null;
				for (ESXNode esx : nodeList) {
					List<VM_Info> vmList = vmwareOBJ.getVMNames(esx, true);
					for (VM_Info vmInfo : vmList) {
						item = converter.VMItemConverter(esx, vmInfo);
						vmItemList.add(item);
					}
				}
				return vmItemList.toArray(new VMItem[vmItemList.size()]);
			}
		} catch (Exception e) {
			logger.debug("getAllVM failed");
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		return null;
	}

	public void submitRecoveryVMJob(RestoreJob job) throws ServiceException {
		logger.debug("submitRecoveryVMJob - start");
		logger.debug("vm name: " + job.getRecoverVMOption().getVmName() + 
				", overwrite: " + job.getRecoverVMOption().isOverwriteExistingVM() + 
				", generateNewInstUUID:" + job.getRecoverVMOption().isGenerateNewVMinstID());

		try {
			restoreJobValidator.validate(job);
		} catch (ServiceException e) {
			logger
					.debug("RecoveryVM Job Validation Failed:"
							+ e.getErrorCode());
			throw e;
		} catch (Exception e) {
			logger.debug(e);
		}

		// check whether there is running jobs
		Scheduler scheduler = VSphereService.getInstance().getCatalogScheduler();
		if (scheduler == null)
			return;

		if (BaseVSphereJob.isJobRunning(job.getRecoverVMOption()
				.getVmInstanceUUID(),String.valueOf(Constants.AF_JOBTYPE_VM_RECOVERY))) {
			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
		}
		
		if (job.getJobType()==JobType.JOBTYPE_VM_RECOVERY && !isProxyESXVDDKCompatible(job.getRecoverVMOption()))
			throw generateAxisFault(FlashServiceErrorCode.VSPHERE_VM_RESTORE_PROXY_ESX_X86_COMPATIBLE);

		try {
			JobDetailImpl jobDetail = new JobDetailImpl(
					JOB_NAME_RESTORE
							+ job.getRecoverVMOption().getVmInstanceUUID(),
					null,
					com.ca.arcflash.webservice.scheduler.VSphereRecoveryJob.class);
			jobDetail.getJobDataMap().put("Job", job);
			jobDetail.getJobDataMap().put("vm", new VirtualMachine(job.getRecoverVMOption().getVmInstanceUUID()));
			jobDetail.getJobDataMap().put("NativeFacade", this.getNativeFacade());
			jobDetail.getJobDataMap().put(RPS_POLICY_UUID, job.getRpsPolicy());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_UUID, job.getRpsDataStoreName());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_DISPLAY_NAME, job.getRpsDataStoreDisplayName());
			jobDetail.getJobDataMap().put(RPS_HOST, job.getSrcRpsHost());
			jobDetail.getJobDataMap().put(JOB_ID, job.getJobId());
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			trigger.setName(jobDetail.getName() + "Trigger");
			scheduler.scheduleJob(jobDetail, trigger);

			logger.debug("submitRecoveryVMJob - end");
		} catch (Throwable e) {
			logger.error("submitRecoveryVMJob()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	@SuppressWarnings("deprecation")
	public void submitVMCopyJob(CopyJob job) throws ServiceException {
		logger.debug("submitVMCopyJob - start");
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(job.getVmInstanceUUID());
		VMBackupConfiguration config = this.getVMBackupConfiguration(vm);
		int pathMaxWithoutHostName =0;
		RpsPolicy4D2D rpsPolicy = null;
		
		String sourceServer = getLastFolderName(job.getSessionPath());
		logger.debug("Server name(folder name) :" + sourceServer);
		if (sourceServer == null || sourceServer.length() == 0) {
			throw new ServiceException(FlashServiceErrorCode.CopyJob_InvalidSessionPath);
		}
		
		try {
			// The following two lines's position cannot be exchanged because
			// the validateDestPath will establish
			// a connection on which appendHostNameIfNeeded depends.
			copyJobValidator.validate(job);
			pathMaxWithoutHostName = copyJobValidator.validateDestPath(job, sourceServer);
			rpsPolicy = SettingsService.instance().checkDataStore4Job(job.getRpsHost(),
					job.getRpsDataStore(), JobType.JOBTYPE_COPY);
		} catch (ServiceException e) {
			logger.debug("submitVMCopyJob Validation Failed:"
					+ e.getErrorCode());
			throw e;
		}

		// check whether there is running jobs
		Scheduler scheduler = VSphereService.getInstance().getCatalogScheduler();
		if (scheduler == null)
			return;

		if (BaseVSphereJob.isJobRunning(job.getVmInstanceUUID(),String.valueOf(Constants.AF_JOBTYPE_COPY))) {
			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
		}

		checkSameSourceDestPath(job);
		
		String originalDest = job.getDestinationPath(); 
		String dest = appendVMInfoIfNeeded(job.getDestinationPath(), sourceServer, null, config.isD2dOrRPSDestType());		
		if(!StringUtil.isEmptyOrNull(dest) &&  !StringUtil.isEmptyOrNull(originalDest)
				&& dest.length() > originalDest.length()) {
				//int maxLength = pathMaxWithoutHostName - BackupConfigurationValidator.WINDOWS_HOST_NAME_MAX_LENGTH;
			int backslash = 1;
			if(originalDest.endsWith("\\") || originalDest.endsWith("/")){
				backslash = 0;
			}
			if(dest.length() > (pathMaxWithoutHostName + backslash))
				copyJobValidator.generatePathExeedLimitException(pathMaxWithoutHostName, sourceServer);
		}
		
		
		job.setDestinationPath(dest);
		
		checkSameSourceDestPath(job);

		String username = job.getDestinationUserName();
		String password = job.getDestinationPassword();

		if (username == null)
			username = "";
		if (password == null)
			password = "";

		logger.debug("username" + username);
		String domain = "";
		int indx = username.indexOf('\\');
		if (indx > 0 && indx < username.length() - 1) {
			domain = username.substring(0, indx);
			username = username.substring(indx + 1);
		}

		getNativeFacade().initCopyDestination(dest, domain, username,
				password);

		try {
			JobDetailImpl jobDetail = new JobDetailImpl(JOB_NAME_COPYJOB
					+ job.getVmInstanceUUID(), null,
					com.ca.arcflash.webservice.scheduler.VSphereCopyJob.class);
			jobDetail.getJobDataMap().put("Job", job);
			jobDetail.getJobDataMap().put("vm", vm);
			jobDetail.getJobDataMap().put("NativeFacade",
					this.getNativeFacade());
			if (rpsPolicy != null) {
				String hashKey = BackupService.getInstance().getNativeFacade().getRPSDataStoreHashKey(
						rpsPolicy.getDataStoreSharedPath(), rpsPolicy.getStoreUserName(), 
						rpsPolicy.getStorePassword(), 0, rpsPolicy.getEncryptionPassword());
				job.setDataStoreHashKey(hashKey);
			}
			jobDetail.getJobDataMap().put(RPS_POLICY_UUID, job.getRpsPolicy());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_UUID, job.getRpsDataStore());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_DISPLAY_NAME, job.getRpsDataStoreDisplayName());
			jobDetail.getJobDataMap().put(RPS_HOST, job.getRpsHost());;
			jobDetail.getJobDataMap().put(ON_DEMAND_JOB, true);
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			trigger.setName(jobDetail.getName() + "Trigger");
			scheduler.scheduleJob(jobDetail, trigger);

			logger.debug("submitVMCopyJob - end");
		} catch (Throwable e) {
			logger.error("submitVMCopyJob()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	private String getLastFolderName(String sourcePath) {
		if (sourcePath == null || sourcePath.isEmpty())
			return null;
		if (sourcePath.endsWith("\\") || sourcePath.endsWith("/"))
			sourcePath = sourcePath.substring(0, sourcePath.length() - 1);

		int indexBSlash = sourcePath.lastIndexOf("\\");
		int indexSlash = indexBSlash < 0 || sourcePath.lastIndexOf("/") > indexBSlash ? sourcePath.lastIndexOf("/") : indexBSlash;
		if (indexSlash >= 0) {
			String retPath = sourcePath.substring(indexSlash + 1);
			if (retPath.endsWith("]")) {
				int indx = retPath.lastIndexOf("[");
				if (indx > 0) {
					return retPath.substring(0, indx);
				}
			} else {
				return retPath;
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	private String checkSameSourceDestPath(CopyJob job) throws ServiceException {
		String destinationPath = getNormalizedPath(job.getDestinationPath());
		String sourcePath = getNormalizedPath(job.getSessionPath());
		if (destinationPath.equalsIgnoreCase(sourcePath))
			throw new ServiceException(
					FlashServiceErrorCode.CopyJob_SameSourceDestPath);
		return destinationPath;
	}

	private String getNormalizedPath(String destinationPath) {
		String path = destinationPath == null ? "" : destinationPath;
		if (path.endsWith("\\") || path.endsWith("/"))
			path = path.substring(0, path.length() - 1);
		return path;
	}

	public long backupVM(int type, String name, VirtualMachine vm, 
			boolean convertForBackupSet, String generatedDestinationPath) throws ServiceException{
		return backupVM(type, name, vm, convertForBackupSet, generatedDestinationPath, 0);
	}
	
	public long backupVM(int type, String name, VirtualMachine vm, 
			boolean convertForBackupSet, String generatedDestinationPath, long jobID)throws ServiceException{
		return backupVM(type, name, vm, convertForBackupSet, generatedDestinationPath, jobID,0);
	}
	
	public long backupVM(int type, String name, VirtualMachine vm, 
			boolean convertForBackupSet, String generatedDestinationPath, long jobID,int scheduletype) throws ServiceException{
		logger.info("backupVM() - start");
		logger.info("backupVM type:" + type);
		logger.info("backupVM name:" + name);
		logger.debug("scheduletype:"+scheduletype);
		
		if (StringUtil.isEmptyOrNull(name)) {
			logger.error("no backup name, return error code " + FlashServiceErrorCode.Backup_NoBackupName);
			throw generateAxisFault(FlashServiceErrorCode.Backup_NoBackupName);
		}
		
		VMBackupConfiguration configuration = this.getVMBackupConfiguration(vm);
		if (configuration == null) {
			logger.error("There is no backup configuration, return error code");
			throw generateAxisFault(FlashServiceErrorCode.Backup_NoBackupConfiguration);
		}
		
//		if (configuration.isDisablePlan()) {
//			logger.info("The backup job is disabled.");
//			throw generateAxisFault(FlashServiceErrorCode.Backup_BackupDisabled);
//		}

		boolean isLaunchedByVAppJob = StringUtil.isEmptyOrNull(generatedDestinationPath) ? false : true;
		VSphereVAppJobValidator validator = new VSphereVAppJobValidator(configuration, isLaunchedByVAppJob);
		validator.validate();
		
		// check whether there is running jobs
		if (VSphereJobQueue.getInstance().isJobRunning(vm.getVmInstanceUUID(),String.valueOf(Constants.AF_JOBTYPE_VM_BACKUP)) 
				&& !VSphereJobQueue.getInstance().isJobWaitingAtRPS(vm.getVmInstanceUUID())) 
		{
			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
		}
		
		//if it's not full backup, then we need to check whether need to convert this 
		//job to full and mark it as backup set start, if yes, we will ask user whether he wants to do that
		//if it's full backup, we will mark it as backup set start as needed
		if(convertForBackupSet && type != BackupType.Full && vm != null) {
			checkForBackupSetStart(vm);
		}
		
		//check for RPS server and datastore status
		checkRPS4Backup(configuration);
		
		//if the backup node is vApp, we will not validate the running count for 2 reasons
		//1. backup vApp metadata is lightweight and fast
		//2. if user manually sets the max job count = 1, then after launch the vApp backup,
		//   child vm backup will be blocked due to the count limit, and vApp backup master job will never end.
		VSphereJobQueue jobQInst = VSphereJobQueue.getInstance(); 
		synchronized (JobQueueAddingLock.addingToQueueLock)
		{
			try
			{
				String jobType = "";
				if (type == BackupType.Full)
				{
					jobType = "Full";
				} else if (type == BackupType.Incremental)
				{
					jobType = "Increament";
				} else if (type == BackupType.Resync)
				{
					jobType = "Resync";
				}
				JobDetailImpl jobDetail;
				jobDetail = new JobDetailImpl(jobType
								+ VSphereBackupJob.class.getSimpleName()
								+ vm.getVmInstanceUUID()
								+ JOB_NAME_BACKUP_NOW_SUFFIX, null,
								VSphereBackupJob.class);

				// When back-end invoke the vApp child VM backup, it will pass
				// the generated backup destination
				// and it's job priority is higher in the job queue
				int priority = isLaunchedByVAppJob ? VSphereJobQueue.JOBQUEUE_VAPPCHILDVM_PRIORITY
								: VSphereJobQueue.JOBQUEUE_VM_PRIORITY;
				jobDetail.getJobDataMap().put("jobName", name);
				jobDetail.getJobDataMap().put("jobType", type);
				jobDetail.getJobDataMap().put("vm", vm);
				jobDetail.getJobDataMap().put("jobQueuePriority", priority);
				jobDetail.getJobDataMap().put("generatedDestinationPath",
								generatedDestinationPath);
				jobDetail.getJobDataMap().put(JOB_ID, jobID);
				jobDetail.getJobDataMap().put("retriedTimes", 0);
				jobDetail.getJobDataMap().put("periodRetentionFlag", scheduletype);
				jobDetail.getJobDataMap().put("manualFlag", true);
				switch (scheduletype) {
				case PeriodRetentionValue.QJDTO_B_Backup_Daily: {
					jobDetail.getJobDataMap().put("isDaily", true);
					break;
				}
				case PeriodRetentionValue.QJDTO_B_Backup_Weekly: {
					jobDetail.getJobDataMap().put("isWeekly", true);
					break;
				}
				case PeriodRetentionValue.QJDTO_B_Backup_Monthly: {
					jobDetail.getJobDataMap().put("isMonthly", true);
					break;
				}
				default:
					break;
				}
				SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(
								0, 0);
				trigger.setName(jobDetail.getName() + "Trigger");
				logger.info("setting trigger : " + jobDetail.getName()
								+ "Trigger");

				String log = String.format("scheduling vm backup job: %s, type %d, priority %d, dest %s, jobID %d, vm uuid: %s ",
										   name, type, priority,
										   generatedDestinationPath,
										   jobID, vm.getVmInstanceUUID());
				logger.info(log);
				bkpScheduler.scheduleJob(jobDetail, trigger);
				logger.info("backupVM() - end");
			} catch (Throwable e)
			{
				logger.error("backupVM()", e);
				throw generateInternalErrorAxisFault();
			}
			
			int result = 0;			
			// no need to check when this type of VMs'job are waiting at RPS (in this case, the proxy queue cannot be full)
			if (!VSphereJobQueue.getInstance().isJobWaitingAtRPSByType(configuration.getBackupVM().getVmType()))   
			{
				result = jobQInst.validateRunningCount(configuration.getBackupVM().getVmType());
			}
			
			int result2 = 0;
			
			if (result == 1)				
				if (jobQInst.isJobWaiting(vm.getVmInstanceUUID()))
					result2 = 1;

			if (result == 1)
			{
				int maxjobcount = jobQInst.getMaxJobCount(configuration.getBackupVM().getVmType());

				if (result2 == 0)
				{
					logger.info(String.format("VSPHERE_EXCEED_JOB_LIMITATION getMaxJobCount: %d, result %d, %s",
										   	  maxjobcount, result2,
											  vm.getVmName()));
					throw new ServiceException(String.valueOf(maxjobcount), FlashServiceErrorCode.VSPHERE_EXCEED_JOB_LIMITATION);
				} 
				else
				{
					logger.info(String.format("VSPHERE_EXCEED_JOB_LIMITATION_MERGE getMaxJobCount: %d, result %d, %s",
										      maxjobcount, result2,
											  vm.getVmName()));
					throw new ServiceException(String.valueOf(maxjobcount), FlashServiceErrorCode.VSPHERE_EXCEED_JOB_LIMITATION_MERGE);
				}
			}
			else
			{
				try
				{
					JobQueueAddingLock.addingToQueueLock.wait(5000);
					logger.info("addingToQueueLock.wait(3000) returned.");
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return 0;
	}
	
	public long backupVM(int type, String name, 
			VirtualMachine vm) throws ServiceException {
		return backupVM(type, name, vm, true);
	}
	
	public long backupVM(int type, String name, VirtualMachine vm, 
			boolean convertForBackupSet) throws ServiceException {
		return backupVM(type, name, vm, true, "");
	}

	public static String getJobName(int jobtype, String vmInstanceUUID) {
		String jobName = "";

		switch (jobtype) {
		case BackupType.Full:
			jobName = BaseService.JOB_NAME_BACKUP_FULL + vmInstanceUUID;
			break;
		case BackupType.Incremental:
			jobName = BaseService.JOB_NAME_BACKUP_INCREMENTAL + vmInstanceUUID;
			break;
		case BackupType.Resync:
			jobName = BaseService.JOB_NAME_BACKUP_RESYNC + vmInstanceUUID;
			break;
		}

		return jobName;
	}
	
	private void checkForBackupSetStart(VirtualMachine vm) throws ServiceException {
		if(VSphereBackupSetService.getInstance().isManuallyBackupSetStart(vm.getVmInstanceUUID())) {
			throw generateAxisFault(WebServiceMessages.getResource("covertManualJobToBackupSetWarning"),
					FlashServiceErrorCode.MERGE_CONVERT_MANUAL_JOB_FULL);
		}
	}
	
	public BackupInformationSummary getBackupInformationSummary(
			VirtualMachine vm) throws ServiceException {
		logger.debug("getBackupInformationSummary() - start");

		try {
			VMBackupConfiguration configuration = this
					.getVMBackupConfiguration(vm);
			if (configuration == null)
				return null;

			CONN_INFO info = getCONN_INFO(configuration);
			JBackupInfoSummary summary = this.getNativeFacade()
					.GetBackupInfoSummary(
							configuration.getBackupVM().getDestination(),
							info.getDomain(), info.getUserName(),
							info.getPwd(), false, vm.getVmInstanceUUID());

			BackupInformationSummary returnBackupInformationSummary = backupSummaryConverter
					.convert(summary);
			
			if (!configuration.isD2dOrRPSDestType())
			{	
				RpsHost tempRPSHost = configuration.getBackupRpsDestSetting().getRpsHost();
				DataStoreHealthStatus dsHealth = DataStoreHealthStatus.UNKNOWN;
				try {
					IRPSService4D2D client = RPSServiceProxyManager
							.getRPSServiceClient(tempRPSHost.getRhostname(),
									tempRPSHost.getUsername(), tempRPSHost
											.getPassword(), tempRPSHost
											.getPort(), tempRPSHost
											.isHttpProtocol() ? "http"
											: "https", tempRPSHost.getUuid());

					if (client != null)
						dsHealth = client.getDataStoreHealthStatus(configuration.getBackupRpsDestSetting().getRPSDataStore());	
				}catch(Exception e) {
					logger.error("Failed to get data store status", e);
				}
				
				switch(dsHealth) {
				    case GREEN: returnBackupInformationSummary.setDsHealth(RpsDataStoreHealthStatus.GREEN); break;
			        case YELLOW: returnBackupInformationSummary.setDsHealth(RpsDataStoreHealthStatus.YELLOW); break;
			        case RED: returnBackupInformationSummary.setDsHealth(RpsDataStoreHealthStatus.RED); break;
			        case UNKNOWN: returnBackupInformationSummary.setDsHealth(RpsDataStoreHealthStatus.UNKNOWN); break;              	
				}
				
				RPSInfo rpsinfo = new RPSInfo();
				rpsinfo.setRpsHostName(configuration.getBackupRpsDestSetting().getRpsHost().getRhostname());
				rpsinfo.setRpsPolicy(configuration.getBackupRpsDestSetting().getRPSPolicy());
				rpsinfo.setRpsDataStore(configuration.getBackupRpsDestSetting().getRPSDataStoreDisplayName());
				rpsinfo.setRpsDataStoreGuid(configuration.getBackupRpsDestSetting().getRPSDataStore());
				returnBackupInformationSummary.setRpsInfo(rpsinfo);
			}
			
			String mergeScheduleTime = VSphereMergeService.getInstance().getMergeScheduleTime(vm.getVmInstanceUUID());
			returnBackupInformationSummary.setMergeJobScheduleTime(mergeScheduleTime);
			if(!configuration.getRetentionPolicy().isUseBackupSet()){
				returnBackupInformationSummary.setRetentionCount(configuration
						.getRetentionCount());
				if(mergeScheduleTime != null){
					if(mergeScheduleTime.equals(AbstractMergeService.MANUAL_MERGE_STRING)){
						returnBackupInformationSummary.setInSchedule(true);
					}else{
						returnBackupInformationSummary.setInSchedule(false);
					}
				}
			}else{
				returnBackupInformationSummary.setRetentionCount(configuration.getRetentionPolicy().
						getBackupSetCount());
			}returnBackupInformationSummary.setBackupSet(configuration.getRetentionPolicy().isUseBackupSet());
			returnBackupInformationSummary.setBackupDestination(configuration
					.getBackupVM().getDestination());
			returnBackupInformationSummary.setSpaceMeasureNum(configuration
					.getSpaceMeasureNum());
			returnBackupInformationSummary.setSpaceMeasureUnit(configuration
					.getSpaceMeasureUnit());

			// LicInfo licInfo = this.getNativeFacade().getLicInfo();
			// returnBackupInformationSummary.setLicInfo(licInfo);
			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil
						.convertObject2String(returnBackupInformationSummary));
				if (returnBackupInformationSummary != null) {
					logger
							.debug(StringUtil
									.convertObject2String(returnBackupInformationSummary
											.getDestinationCapacity()));
					logger
							.debug(StringUtil
									.convertObject2String(returnBackupInformationSummary
											.getRecentFullBackup()));
					logger
							.debug(StringUtil
									.convertObject2String(returnBackupInformationSummary
											.getRecentIncrementalBackup()));
					logger
							.debug(StringUtil
									.convertObject2String(returnBackupInformationSummary
											.getRecentResyncBackup()));
					// logger
					// .debug(StringUtil
					// .convertObject2String(returnBackupInformationSummary
					// .getLicInfo()));
				}
			}
			logger.debug("getBackupInformationSummary() - end");
			return returnBackupInformationSummary;
		} catch (Throwable e) {
			logger.error("getBackupInformationSummary()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public BackupInformationSummary getBackupInformationSummaryWithLicInfo(
			VirtualMachine vm) throws ServiceException {
		logger.debug("getBackupInformationSummaryWithLicInfo() - start");

		try {
			VMBackupConfiguration configuration = this
					.getVMBackupConfiguration(vm);
			if (configuration == null)
				return null;

			CONN_INFO info = getCONN_INFO(configuration);
			JBackupInfoSummary summary = this.getNativeFacade()
					.GetBackupInfoSummary(
							configuration.getBackupVM().getDestination(),
							info.getDomain(), info.getUserName(),
							info.getPwd(), false, vm.getVmInstanceUUID());

			BackupInformationSummary returnBackupInformationSummary = backupSummaryConverter
					.convert(summary);

			returnBackupInformationSummary.setAdvanced(configuration.getBackupDataFormat() > 0);
			returnBackupInformationSummary.setAdvanceSchedule(configuration.getAdvanceSchedule());
			if(configuration.getAdvanceSchedule() != null){
				returnBackupInformationSummary.setPeriodEnabled(configuration.getAdvanceSchedule().isPeriodEnabled());
			}
			
			if (!configuration.isD2dOrRPSDestType())
			{
				RpsHost tempRPSHost = configuration.getBackupRpsDestSetting().getRpsHost();
				DataStoreHealthStatus dsHealth = DataStoreHealthStatus.UNKNOWN;
				DataStoreStatusListElem[] source = null;
				boolean bNotExist = false;
				try {
					IRPSService4D2D client = RPSServiceProxyManager
							.getRPSServiceClient(tempRPSHost.getRhostname(),
									tempRPSHost.getUsername(), tempRPSHost
											.getPassword(), tempRPSHost
											.getPort(), tempRPSHost
											.isHttpProtocol() ? "http"
											: "https", tempRPSHost.getUuid());

					if(client !=  null){
						dsHealth = client.getDataStoreHealthStatus(configuration.getBackupRpsDestSetting().getRPSDataStore());
						source = client.getDataStoreStatus(configuration.getBackupRpsDestSetting().getRPSDataStore());
					}
				}catch(Exception e) {
					logger.error("Failed to get data store status", e);
					if(e instanceof SOAPFaultException)
					{
						SOAPFaultException se = (SOAPFaultException)e;
						if(se.getFault().getFaultCode().equalsIgnoreCase(FlashServiceErrorCode.Common_ServiceSessionTimeout))
							bNotExist = true;
					}
				}
				
				switch(dsHealth) {
				    case GREEN: returnBackupInformationSummary.setDsHealth(RpsDataStoreHealthStatus.GREEN); break;
			        case YELLOW: returnBackupInformationSummary.setDsHealth(RpsDataStoreHealthStatus.YELLOW); break;
			        case RED: returnBackupInformationSummary.setDsHealth(RpsDataStoreHealthStatus.RED); break;
			        case UNKNOWN: returnBackupInformationSummary.setDsHealth(RpsDataStoreHealthStatus.UNKNOWN); break;              	
				}
				
				DataStoreRunningState runningState = DataStoreRunningState.UNKNOWN;
				if(source!=null && source[0]!=null)
					runningState =	DataStoreRunningState.parseInt((int) source[0].getDataStoreStatus().getOverallStatus());
				else if(bNotExist)
					runningState = DataStoreRunningState.DELETED;
				
				returnBackupInformationSummary.setDsRunningState(runningState);	
				
				RPSInfo rpsinfo = new RPSInfo();
				rpsinfo.setRpsHostName(configuration.getBackupRpsDestSetting().getRpsHost().getRhostname());
				rpsinfo.setRpsPolicy(configuration.getBackupRpsDestSetting().getRPSPolicy());
				rpsinfo.setRpsDataStore(configuration.getBackupRpsDestSetting().getRPSDataStoreDisplayName());
				rpsinfo.setRpsDataStoreGuid(configuration.getBackupRpsDestSetting().getRPSDataStore());
				returnBackupInformationSummary.setRpsInfo(rpsinfo);
			}
			
			String mergeScheduleTime = VSphereMergeService.getInstance().getMergeScheduleTime(vm.getVmInstanceUUID());
			returnBackupInformationSummary.setMergeJobScheduleTime(mergeScheduleTime);
			if(!configuration.getRetentionPolicy().isUseBackupSet()){
				returnBackupInformationSummary.setRetentionCount(configuration
						.getRetentionCount());
				if(mergeScheduleTime != null){
					if(mergeScheduleTime.equals(AbstractMergeService.MANUAL_MERGE_STRING)){
						returnBackupInformationSummary.setInSchedule(true);
					}else{
						returnBackupInformationSummary.setInSchedule(false);
					}
				}
			}else{
				returnBackupInformationSummary.setRetentionCount(configuration.getRetentionPolicy().
						getBackupSetCount());
			}returnBackupInformationSummary.setBackupSet(configuration.getRetentionPolicy().isUseBackupSet());
			returnBackupInformationSummary.setBackupDestination(configuration
					.getBackupVM().getDestination());
			returnBackupInformationSummary.setSpaceMeasureNum(configuration
					.getSpaceMeasureNum());
			returnBackupInformationSummary.setSpaceMeasureUnit(configuration
					.getSpaceMeasureUnit());

			LicInfo licInfo = this.getNativeFacade().getLicInfo();
			returnBackupInformationSummary.setLicInfo(licInfo);
			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil
						.convertObject2String(returnBackupInformationSummary));
				if (returnBackupInformationSummary != null) {
					logger
							.debug(StringUtil
									.convertObject2String(returnBackupInformationSummary
											.getDestinationCapacity()));
					logger
							.debug(StringUtil
									.convertObject2String(returnBackupInformationSummary
											.getRecentFullBackup()));
					logger
							.debug(StringUtil
									.convertObject2String(returnBackupInformationSummary
											.getRecentIncrementalBackup()));
					logger
							.debug(StringUtil
									.convertObject2String(returnBackupInformationSummary
											.getRecentResyncBackup()));
					logger
							.debug(StringUtil
									.convertObject2String(returnBackupInformationSummary
											.getLicInfo()));
				}
			}
			logger.debug("getBackupInformationSummaryWithLicInfo() - end");
			return returnBackupInformationSummary;
		} catch (Throwable e) {
			logger.error("getBackupInformationSummaryWithLicInfo()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public ProtectionInformation[] getProtectionInformation(VirtualMachine vm)
			throws ServiceException {
		logger.debug("getProtectionInformation() - start");

		try {
			VMBackupConfiguration configuration = this
					.getVMBackupConfiguration(vm);

			if (configuration == null)
				return null;
			
			boolean isDedupe = false;
			if (!configuration.isD2dOrRPSDestType() && configuration.getBackupRpsDestSetting() != null)
				isDedupe = configuration.getBackupRpsDestSetting().isDedupe();

			CONN_INFO info = getCONN_INFO(configuration);

			JProtectionInfo[] sources = this
					.getNativeFacade()
					.GetProtectionInformation(
							configuration.getBackupVM().getDestination(),
							info.getDomain(), info.getUserName(), info.getPwd());
			List<ProtectionInformation> returnProtectionInformationArray = protectionInformationConverter
					.convert(sources, isDedupe);

			if (bkpScheduler != null) {
				Date[] nextRunTimeResult = getNextRunTime(vm);
				for (ProtectionInformation protectionInfo : returnProtectionInformationArray){
					Date nextRunTime = null;
					if (protectionInfo.getBackupType() == BackupType.Full){
						nextRunTime = nextRunTimeResult[0];
					}else if (protectionInfo.getBackupType() == BackupType.Incremental){
						nextRunTime = nextRunTimeResult[2];
					}else if (protectionInfo.getBackupType() == BackupType.Resync){
						nextRunTime = nextRunTimeResult[1];
					}

					if (nextRunTime!=null){
						protectionInfo.setNextRunTime(nextRunTime);
						protectionInfo.setNextTimeZoneOffset(DSTUtils.getTimezoneOffset(nextRunTime));
					}
				}
			}
			
			returnProtectionInformationArray.add(VMCopyService.getInstance().getScheduledExportProtectionInfo(vm));

			if (logger.isDebugEnabled()) {
				System.out.println(StringUtil.convertArray2String(returnProtectionInformationArray.toArray(new ProtectionInformation[0])));
			}
			logger.debug("getProtectionInformation() - end");
			return returnProtectionInformationArray.toArray(new ProtectionInformation[0]);
		} catch (Throwable e) {
			logger.error("getProtectionInformation()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	public ProtectionInformation[] updateProtectionInformation(VirtualMachine vm)
			throws ServiceException {
		
		logger.debug("updateProtectionInformation() - start");
		VMBackupConfiguration configuration = null;
		try {
			
			synchronized (lock) {
				configuration = this.getVMBackupConfiguration(vm);
			}
		
			if (configuration == null || bkpScheduler == null)
				return null;
		
			ProtectionInformation[] result = new ProtectionInformation[3];
			ProtectionInformation info = new ProtectionInformation();
			info.setBackupType(BackupType.Full);
			result[0] = info;
		
			info = new ProtectionInformation();
			info.setBackupType(BackupType.Incremental);
			result[1] = info;
		
			info = new ProtectionInformation();
			info.setBackupType(BackupType.Resync);
			result[2] = info;
		
			if (bkpScheduler != null) {
				Date[] nextRunTimeResult = getNextRunTime(vm);
				for (ProtectionInformation protectionInfo : result) {
					Date nextRunTime = null;
					if (protectionInfo.getBackupType() == BackupType.Full){
						nextRunTime = nextRunTimeResult[0];
					}else if (protectionInfo.getBackupType() == BackupType.Incremental){
						nextRunTime = nextRunTimeResult[2];
					}else if (protectionInfo.getBackupType() == BackupType.Resync){
						nextRunTime = nextRunTimeResult[1];
					}

					if (nextRunTime!=null){
						protectionInfo.setNextRunTime(nextRunTime);
						protectionInfo.setNextTimeZoneOffset(DSTUtils.getTimezoneOffset(nextRunTime));
					}
				}
			}
		
			logger.debug(StringUtil.convertArray2String(result));
			logger.debug("updateProtectionInformation() - end");
		
			return result;
		
		} catch (Throwable e) {
			logger.error("updateProtectionInformation()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public RecoveryPoint[] getMostRecentRecoveryPoints(int backupType,
			int backupStatus, int top, VirtualMachine vm)
			throws ServiceException {
		logger.debug("getMostRecentRecoveryPoints(int) - start");
		logger.debug("top:" + top);

		try {
			VMBackupConfiguration configuration = this
					.getVMBackupConfiguration(vm);
			if (configuration == null)
				return null;
			CONN_INFO info = getCONN_INFO(configuration);

			JBackupInfo[] restorePoints = getNativeFacade()
					.getMostRecentRecoveryPoints(
							configuration.getBackupVM().getDestination(),
							info.getDomain(), info.getUserName(),
							info.getPwd(), backupType, backupStatus, top,
							vm.getVmInstanceUUID());
			RecoveryPoint[] result = recoveryPointConverter
					.convert2RecoveryPointsFromBackupInfo(restorePoints);

			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertArray2String(restorePoints));
			logger.debug("getRecoveryPoints - end");
			return result;
		} catch (Throwable e) {
			logger.error("getRecoveryPoints()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	public RecoveryPoint[] getRecentBackupsByServerTime(int backupType, int backupStatus, Date beginDate, Date endDate, boolean needCatalogStatus,VirtualMachine vm) throws ServiceException {
		logger.debug("getRecentBackupsByServerTime(int) - start");

		try{
			VMBackupConfiguration configuration = this.getVMBackupConfiguration(vm);
			if (configuration==null)
				return null;
			CONN_INFO info = getCONN_INFO(configuration);

			JBackupInfo[] restorePoints = getNativeFacade()
					.getRecentBackupsByServerTime(
							configuration.getBackupVM().getDestination(), info.getDomain(), info.getUserName(), info.getPwd(),backupType,backupStatus, beginDate, endDate, vm.getVmInstanceUUID());
			RecoveryPoint[] result = recoveryPointConverter.convert2RecoveryPointsFromBackupInfo(restorePoints, 
					configuration.getRetentionPolicy().isUseBackupSet());
			
			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertArray2String(restorePoints));
			logger.debug("getRecoveryPoints - end");
			return result;
		}catch(Throwable e){
			logger.error("getRecoveryPoints()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public NextScheduleEvent getNextScheduleEvent(VirtualMachine vm)
			throws ServiceException {
		logger.debug("getNextScheduleEvent() - start");

		try {
			if (getBackupSchedule() == null)
				return null;
			
			return getNewNextEvent(vm.getVmInstanceUUID());

		} catch (Throwable e) {
			logger.error("getNextScheduleEvent()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public boolean isVMBackupCompressionLevelChanged(VirtualMachine vm)
			throws ServiceException {
		logger.debug("isBackupCompressionLevelChanged() - start");

		try {
			VMBackupConfiguration configuration = this
					.getVMBackupConfiguration(vm);

			if (configuration == null) {
				logger.debug("backup configuration is null, return false");
				return false;
			}

			CONN_INFO info = getCONN_INFO(configuration);

			boolean returnboolean = this.getNativeFacade()
					.isCompressionLevelChanged(
							configuration.getBackupVM().getDestination(),
							info.getDomain(), info.getUserName(),
							info.getPwd(), configuration.getCompressionLevel());

			logger.debug("return value:" + returnboolean);
			logger.debug("isBackupCompressionLevelChanged() - end");
			return returnboolean;
		} catch (Throwable e) {
			logger.error("isBackupCompressionLevelChanged()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	@Override
	protected JobDetail generateJobDetail(String vmInstanceUUID, int backupType) {
		String jobName = JOB_NAME_BACKUP_FULL;
		if (backupType == BackupType.Full) {
			jobName = JOB_NAME_BACKUP_FULL;
		} else if (backupType == BackupType.Resync) {
			jobName = JOB_NAME_BACKUP_RESYNC;
		} else if (backupType == BackupType.Incremental) {
			jobName = JOB_NAME_BACKUP_INCREMENTAL;
		}
		
		JobDetail jobDetail = new JobDetailImpl(jobName, getBackupJobGroupName(vmInstanceUUID), VSphereBackupJob.class);
		jobDetail.getJobDataMap().put("jobType", backupType);
		VirtualMachine vm = new VirtualMachine(vmInstanceUUID);
		jobDetail.getJobDataMap().put("vm", vm);
		
		return jobDetail;
	}

	// log important info of the scheduled backup jobs and triggers for debug
	// this function will be called when service started and plan deployed
	private void logScheduledVMJobs(String vmInstanceUUID)
	{
		try
		{
			logger.info("====== Scheduled Jobs for VM = " + vmInstanceUUID);
			do 
			{
				Scheduler scheduler = getBackupSchedule(); // from the scheduler for backup job
				if (scheduler == null)
				{
					logger.error("scheduler is null");
					break;
				}
				
				// get all jobs for this VM
				Set<JobKey> jobKeySet = scheduler.getJobKeys(GroupMatcher.jobGroupContains(vmInstanceUUID));
				if (jobKeySet == null)
				{
					logger.error("jobKeySet is null");
					break;
				}
				
				int i = -1;
				for (JobKey jobKey : jobKeySet)
				{	
					i++;
					logger.info("\tJob #" + i + "/" + jobKeySet.size());
					
					try
					{
						// job info
						JobDetail jobDetail = scheduler.getJobDetail(jobKey);
						if (jobDetail instanceof JobDetailImpl)
						{
							JobDetailImpl jobDetailImpl = (JobDetailImpl) jobDetail;
							logger.info("\t\tJobDetailImpl name = " + jobDetailImpl.getName());
							logger.info("\t\tJobDetailImpl group = " + jobDetailImpl.getGroup());
						}
						
						// get triggers of the job
						List<? extends Trigger> triggerList = scheduler.getTriggersOfJob(jobKey);
						if (triggerList == null)
						{
							logger.error("triggerList is null");
							continue;
						}
						
						int j = -1;
						for (Trigger trigger : triggerList)
						{
							j++;
							logger.info("\t\tTrigger #" + i + "." + j + "/" + triggerList.size());
							
							try
							{
								// from AbstractTrigger
								if (trigger instanceof AbstractTrigger)
								{
									AbstractTrigger abstractTrigger = (AbstractTrigger) trigger;
									logger.info("\t\t\tAbstractTrigger name = " + abstractTrigger.getName());
									logger.info("\t\t\tAbstractTrigger group = " + abstractTrigger.getGroup());
									logger.info("\t\t\tAbstractTrigger priority = " + abstractTrigger.getPriority());
									logger.info("\t\t\tAbstractTrigger startTime = " + abstractTrigger.getStartTime());
									logger.info("\t\t\tAbstractTrigger nextFireTime = " + abstractTrigger.getNextFireTime());
								}
								
								// from AdvancedScheduleTrigger
								if (trigger instanceof AdvancedScheduleTrigger)
								{
									AdvancedScheduleTrigger advancedScheduleTrigger = (AdvancedScheduleTrigger) trigger;
									logger.info("\t\t\tAdvancedScheduleTrigger endTime = " + advancedScheduleTrigger.getEndTime());
									logger.info("\t\t\tAdvancedScheduleTrigger dayOfWeek = " + advancedScheduleTrigger.getDayOfWeek());
									
									ScheduleDetailItem item = advancedScheduleTrigger.getScheduleItem();
									if (item != null)
									{
										logger.info("\t\t\tScheduleDetailItem startTime = " + item.getStartTime());
										logger.info("\t\t\tScheduleDetailItem endTime = " + item.getEndTime());
										logger.info("\t\t\tScheduleDetailItem interval = " + item.getInterval());
										logger.info("\t\t\tScheduleDetailItem intervalUnit = " + item.getIntervalUnit());
									}
								}
								
								// from PeriodTrigger
								if (trigger instanceof PeriodTrigger)
								{
									PeriodTrigger periodTrigger = (PeriodTrigger) trigger;
									logger.info("\t\t\tPeriodTrigger cronExpression = " + periodTrigger.getCronExpression());		
								}
								
								// from SimpleTriggerImpl
								if (trigger instanceof SimpleTriggerImpl)
								{
									SimpleTriggerImpl simpleTriggerImpl = (SimpleTriggerImpl) trigger;
									logger.info("\t\t\tSimpleTriggerImpl repeatInterval = " + simpleTriggerImpl.getRepeatInterval());		
								}
							}
							catch(Exception e)
							{
								logger.error("Error occured when log VM job trigger", e);
							}
						}
					}
					catch(Exception e)
					{
						logger.error("Error occured when log VM job detail", e);
					}
				}
			}while(false);
		}
		catch(Exception e)
		{
			logger.error("Error occured when log VM job schedule", e);
		}
	}

	public void configAllVMJobSchedule() {
		logger.debug("configJobSchedule() - start");
		if (!StringUtil.isExistingPath(ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath())) {
			return;
		}
		String configurationPath = ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath();
		File file = new File(configurationPath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		
		for (File one : files) {
			String filename = one.getName();
			String instanceUUID = new String();
			instanceUUID = filename.substring(0, filename.lastIndexOf('.'));
			if(instanceUUID != null && instanceUUID.startsWith(VMCONFIG_PREFIX))
				continue;
			this.configSchedule(instanceUUID);
			
			logScheduledVMJobs(instanceUUID);
		}
		
	}

	private CONN_INFO getCONN_INFO(BackupVM backupVM) {

		CONN_INFO info = new CONN_INFO();
		if (backupVM != null) {
			String domain = "";
			String userName = backupVM.getDesUsername();
			String pwd = "";
			if (userName != null && userName.trim().length() > 0) {
				userName = userName.trim();
				int index = userName.indexOf("\\");
				if (index > 0) {
					domain = userName.substring(0, index);
					userName = userName.substring(index + 1);
				}
				pwd = backupVM.getDesPassword();
			}
			info.setDomain(domain);
			info.setUserName(userName);
			info.setPwd(pwd);
		}

		if (info.getDomain() == null)
			info.setDomain("");
		if (info.getUserName() == null)
			info.setUserName("");
		if (info.getPwd() == null)
			info.setPwd("");

		return info;
	}

	public void cancelVMJob(long jobID, String vmIdentification)
			throws ServiceException {
		logger.debug("cancelVMJob()");
		int returnVlaue;
		try {
			returnVlaue = this.getNativeFacade().cancelVMJob(jobID,
					vmIdentification);
		} catch (Throwable e) {
			throw this.generateInternalErrorAxisFault();
		}
		if (returnVlaue != 0)
			throw generateAxisFault(FlashServiceErrorCode.Common_CancelJobFailed);
	}

	public void cancelWaitingJob(String vmInstanceUuid)
	throws ServiceException {
		logger.debug("cancelWaitingJob()");
		boolean returnVlaue;
		try {
			returnVlaue=VSphereJobQueue.getInstance().removeWaitingJob(vmInstanceUuid);
		} catch (Throwable e) {
			throw this.generateInternalErrorAxisFault();
		}
		if (!returnVlaue)
			throw generateAxisFault(FlashServiceErrorCode.VSPHERE_CancelWaitingJobFailed);
	}
	
	public JobMonitor[] getWaitingJobTable(String vmInstanceUuid)
	throws ServiceException {
		logger.debug("getWaitingJobTable() start...");

		try {
			return VSphereJobQueue.getInstance().getWaitingJobTable(vmInstanceUuid);
			
		} catch (Throwable e) {
			logger.error(" getWaitingJobTable() throw an Error: " + e.getMessage());
			throw this.generateInternalErrorAxisFault();
		}
	}

	public BackupVM[] getBackupVMList(String destination, String domain,
			String username, String password) throws ServiceException {
		logger.debug("getBackupVM()");
		try {
			List<JBackupVM> jbackupVMList = this.getNativeFacade()
					.getBackupVMList(destination, domain, username, password);
			if (jbackupVMList != null && jbackupVMList.size() > 0) {
				BackupVM[] backupVMs = new BackupVM[jbackupVMList.size()];
				int i = 0;
				for (JBackupVM vm : jbackupVMList) {
					backupVMs[i++] = converter.ConertToBackupVM(vm);
				}
				return backupVMs;
			}
			return null;
		} catch (Throwable e) {
			throw this.generateInternalErrorAxisFault();
		}

	}
	
	public BackupVM getBackupVM(String destination, String domain,
			String username, String password) throws ServiceException {
		logger.debug("getBackupVM()");
		try {
			JBackupVM jbackupVM = this.getNativeFacade()
			.getBackupVM(destination, domain, username, password);
			if(jbackupVM !=null){
				return converter.ConertToBackupVM(jbackupVM);
			}else{
				return null;
			}
		} catch (Throwable e) {
			throw this.generateInternalErrorAxisFault();
		}
		
	}
	
	public VCloudVirtualDC getVAppVDCFromSession(String vAppDestination, int vAppSessionNumer, String fullName, String password) throws ServiceException {
		logger.debug("getVAppVDCFromSession()");
		try {
			VAppSessionInfo sessionInfo = getVAppSessionInformations(vAppDestination, vAppSessionNumer, fullName, password);
			if (sessionInfo != null) {
				return converter.convertVCouldVDC2VCloudVirtualDC(sessionInfo.getVdc());
			} else { 
				return null;
			}
		} catch (Throwable e) {
			throw this.generateInternalErrorAxisFault();
		} 
	}
	
	public List<VAppChildBackupVMRestorePointWrapper> getVAppChildBackupVMsAndRecoveryPoints(String vAppDestination, int vAppSessionNumer, String domain, String username, String password) throws ServiceException {
		logger.debug("getVAppChildBackupVMsAndRecoveryPoints()");
		try {
			List<VAppChildBackupVMRestorePointWrapper> resultList = new ArrayList<VAppChildBackupVMRestorePointWrapper>();
			
			List<JVAppChildBackupVMRestorePointWrapper> jWrapperList = this.getNativeFacade().getVAppChildBackupVMsAndRecoveryPoints(vAppDestination, vAppSessionNumer, domain, username, password);
			if (jWrapperList == null || jWrapperList.isEmpty()) {
				return resultList;
			}
			
			String fullName = username;
			if (domain != null && !domain.isEmpty()) {
				fullName = domain + "\\" + username;
			}
			VAppSessionInfo sessionInfo = getVAppSessionInformations(vAppDestination, vAppSessionNumer, fullName, password);
			
			List<VCloudVM> vCloudVMList = null;
			String vDCName = null;
			String vDCId = null;
			if (sessionInfo != null ) {
				vCloudVMList = sessionInfo.getVmList();	
				
				VCloudVDC vDC = sessionInfo.getVdc();
				if (vDC != null) {
					vDCName = vDC.getName();
					vDCId = vDC.getId();
				}
			}
			
			for(JVAppChildBackupVMRestorePointWrapper jWrapper : jWrapperList) {
				JBackupVM jBackupVM = jWrapper.getBackupVM();
				JRestorePoint jRestorePoint = jWrapper.getRestorePoint();
				if (jBackupVM != null && jRestorePoint != null) {
					BackupVM backupVM = converter.ConertToBackupVM(jBackupVM);
					if (vCloudVMList != null && !vCloudVMList.isEmpty()) {
						for (VCloudVM vCloudVM : vCloudVMList) {
							if (vCloudVM.getName().endsWith(backupVM.getVmName())) {
								backupVM.setCpuCount(vCloudVM.getCpuNumber());
								backupVM.setMaxCpuCount(vCloudVM.getMaximumCPUs());
								
								backupVM.setMemorySize(vCloudVM.getMemoryMB());
								backupVM.setMaxMemorySizeGB(vCloudVM.getMaximumMemoryGB());
								
								backupVM.setStoragePolicyName(vCloudVM.getStorageProfile());
								backupVM.setStoragePolicyId(vCloudVM.getStorageProfileId());
								
								backupVM.setVirtualDataCenterName(vDCName);
								backupVM.setVirtualDataCenterId(vDCId);
								
								backupVM.setVmxDataStoreName(vCloudVM.getDatastore());
								backupVM.setVmxDataStoreId(vCloudVM.getDatastoreId());
								
								backupVM.setVmIdInVApp(vCloudVM.getId());
							}
						}
					}
					
					RecoveryPoint recoveryPoint = recoveryPointConverter.convertRestorePoint2RecoveryPoint(jRestorePoint);
					resultList.add(new VAppChildBackupVMRestorePointWrapper(backupVM, recoveryPoint));
				}
			}
			
			return resultList;
		} catch (Throwable e) {
			throw this.generateInternalErrorAxisFault();
		}
	}

	public boolean checkVMDestination(String destination, String domain,
			String username, String password) throws ServiceException {
		try {
			return this.getNativeFacade().checkVMDestination(destination,
					domain, username, password);
		} catch (Throwable e) {
			throw this.generateInternalErrorAxisFault();
		}
	}

	public Disk[] getBackupVMDisk(String destination, String subPath,
			String domain, String username, String password)
			throws ServiceException {
		try {
			int sessionNum = Integer.parseInt(subPath.substring(1));
			
			Map<String, Disk_Info> map = getVMDiskConfigList(destination, sessionNum, username, password);
			
			List<JDisk> jDiskList = this.getNativeFacade().getBackupVMDisk(
					destination, subPath, domain, username, password);
			if (jDiskList != null && jDiskList.size() > 0) {
				Disk[] diskArray = new Disk[jDiskList.size()];
				int i = 0;
				for (JDisk jDisk : jDiskList) {
					diskArray[i] = converter.ConvertToDisk(jDisk);
					if (map.containsKey(diskArray[i].getDiskUrl()))
						diskArray[i].setDiskType(map.get(diskArray[i].getDiskUrl()).getDiskProvisioning());
					i++;
				}
				return diskArray;
			}
		} catch (Throwable e) {
			throw this.generateInternalErrorAxisFault();
		}
		return null;
	}

	public ActivityLogResult getVMActivityLogs(int start, int count,
			VirtualMachine vm) throws Exception {
		logger.debug("getVMActivityLogs - start");
		logger.debug("start:" + start);
		logger.debug("count:" + count);

		try {
			ActivityLogResult result = new ActivityLogResult();

			JActivityLogResult jActivityLogResult = this.getNativeFacade()
					.getVMActivityLogs(start, count, vm.getVmInstanceUUID());
			ActivityLog[] logs = activityLogConverter
					.convert(jActivityLogResult.getLogs().toArray(
							new JActivityLog[0]));
			result.setLogs(logs);
			result.setTotalCount(jActivityLogResult.getTotalCount());

			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertArray2String(result.getLogs()));
			}
			logger.debug("getVMActivityLogs - end");
			return result;
		} catch (Throwable e) {
			logger.error("getVMActivityLogs()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public ActivityLogResult getVMJobActivityLogs(long jobNo, int start,
			int count, VirtualMachine vm) throws Exception {
		logger.debug("getVMJobActivityLogs - start");
		logger.debug("jobNo:" + jobNo);
		logger.debug("start:" + start);
		logger.debug("count:" + count);

		try {
			ActivityLogResult result = new ActivityLogResult();

			JActivityLogResult jActivityLogResult = this.getNativeFacade()
					.getVMJobActivityLogs(jobNo, start, count,
							vm.getVmInstanceUUID());
			ActivityLog[] logs = activityLogConverter
					.convert(jActivityLogResult.getLogs().toArray(
							new JActivityLog[0]));
			result.setLogs(logs);
			result.setTotalCount(jActivityLogResult.getTotalCount());

			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertArray2String(result.getLogs()));
			}
			logger.debug("getVMJobActivityLogs - end");
			return result;
		} catch (Throwable e) {
			logger.error("getVMJobActivityLogs()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public ActivityLogResult GetJobLogActivityForVM(long jobNo, int start,
			int count, VirtualMachine vm) throws Exception {
		logger.debug("GetJobLogActivityForVM - start");
		logger.debug("jobNo:" + jobNo);
		logger.debug("start:" + start);
		logger.debug("count:" + count);

		try {
			ActivityLogResult result = new ActivityLogResult();

			JActivityLogResult jActivityLogResult = this.getNativeFacade()
					.GetJobLogActivityForVM(jobNo, start, count,
							vm.getVmInstanceUUID());
			ActivityLog[] logs = activityLogConverter
					.convert(jActivityLogResult.getLogs().toArray(
							new JActivityLog[0]));
			result.setLogs(logs);
			result.setTotalCount(jActivityLogResult.getTotalCount());

			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertArray2String(result.getLogs()));
			}
			logger.debug("GetJobLogActivityForVM - end");
			return result;
		} catch (Throwable e) {
			logger.error("GetJobLogActivityForVM()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public void deleteVMActivityLogs(Date date, VirtualMachine vm)
			throws ServiceException {
		logger.debug("deleteVMActivityLogs - start");
		logger.debug("date:" + date);

		try {
			TimeZone timeZone = TimeZone.getTimeZone("UTC");
			Calendar calendar = Calendar.getInstance(timeZone);
			if (date != null) {
				calendar.setTime(date);
			} else {
				calendar.setTime(new Date());
				calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
			}
			logger.debug("Calendar.YEAR:" + calendar.get(Calendar.YEAR));
			logger.debug("Calendar.MONTH:" + calendar.get(Calendar.MONTH) + 1);
			logger.debug("Calendar.DAY_OF_MONTH:"
					+ calendar.get(Calendar.DAY_OF_MONTH));
			logger.debug("Calendar.HOUR_OF_DAY:"
					+ calendar.get(Calendar.HOUR_OF_DAY));
			logger.debug("Calendar.MINUTE:" + calendar.get(Calendar.MINUTE));
			logger.debug("Calendar.SECOND:" + calendar.get(Calendar.SECOND));

			getNativeFacade().deleteVMActivityLog(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH) + 1,
					calendar.get(Calendar.DAY_OF_MONTH),
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE),
					calendar.get(Calendar.SECOND), vm.getVmInstanceUUID());

			logger.debug("deleteVMActivityLogs - end");
		} catch (Throwable e) {
			logger.error("deleteVMActivityLogs()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	public void submitVSphereRestoreJob(RestoreJob job) throws ServiceException{
		logger.debug("submitRestoreJob - start");
		
		try{
			restoreJobValidator.validate(job);
		}catch(ServiceException e){
			logger.debug("vSphere Restore Job Validation Failed:"+e.getErrorCode());
			throw e;
		}catch(Exception e){
			logger.debug(e);
		}
		
		//check whether there is running jobs
		Scheduler scheduler = VSphereService.getInstance().getCatalogScheduler();
		if (scheduler==null)
			return;
			
		if(job.getRestoreExchangeGRTOption() == null){
			if (BaseVSphereJob.isJobRunning(job.getVmInstanceUUID(),String.valueOf(Constants.AF_JOBTYPE_RESTORE))){
				throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
			}
		}else{
			if (com.ca.arcflash.webservice.scheduler.RestoreJob.isJobRunning() 
					|| this.getNativeFacade().checkJobExist()){
				throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
			}
		}
		
		try {
			JobDetailImpl jobDetail = new JobDetailImpl(JOB_NAME_RESTORE+job.getVmInstanceUUID(),null,com.ca.arcflash.webservice.scheduler.VSphereRestoreJob.class);
			jobDetail.getJobDataMap().put("Job", job);
			jobDetail.getJobDataMap().put("vm", new VirtualMachine(job.getVmInstanceUUID()));
			jobDetail.getJobDataMap().put("NativeFacade", this.getNativeFacade());
			jobDetail.getJobDataMap().put(RPS_POLICY_UUID, job.getRpsPolicy());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_UUID, job.getRpsDataStoreName());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_DISPLAY_NAME, job.getRpsDataStoreDisplayName());
			jobDetail.getJobDataMap().put(RPS_HOST, job.getSrcRpsHost());;
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0,0);
			trigger.setName(jobDetail.getName()+"Trigger");
			scheduler.scheduleJob(jobDetail, trigger);
			
			logger.debug("submitVSphereRestoreJob - end");
		} catch (Throwable e) {
			logger.error("submitVSphereRestoreJob()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	public long getVSpherePathMaxLength() throws ServiceException
	{
		logger.debug("getVSpherePathMaxLength - started");
		long maxLength = -1;
		try{
			maxLength = this.getNativeFacade().getVSpherePathMaxLength();
		}
		catch (Throwable e)
		{
			throw generateInternalErrorAxisFault();
		}
		return maxLength;
	}
	
	/**
	 * Call back end API to do real catalog
	 * @param id]
	 * @param type : catalog type: 1 for regular, 2 for ondemand
	 * @throws ServiceException 
	 * 
	 */
	/*public long launchVSphereCatalogJob(long jobId, long type,String vmInstanceUUID) throws ServiceException {
		try {
			return this.getNativeFacade().launchVSphereCatalogJob(jobId, type, vmInstanceUUID, null, null);			
		}catch(Exception se){
			throw generateInternalErrorAxisFault();
		}
	}
	
	public void startVSphereCatalogJob(String vmInstanceUUID){
		if(BaseVSphereJob.isJobRunning(vmInstanceUUID,String.valueOf(BaseVSphereJob.JOBTYPE_CATALOG_FS))){
			logger.info("Vsphere Catalog job is running, no need to schedule it again");
			return;
		}
		
		try {
			JobDetail jobDetail = new JobDetail(JOB_NAME_CATALOG + vmInstanceUUID, JOB_GROUP_CATALOG_NAME, VSphereCatalogJob.class);
			jobDetail.getJobDataMap().put("vmInstanceUUID", vmInstanceUUID);
			Trigger trigger = TriggerUtils.makeImmediateTrigger(0, 0);
			trigger.setName(JOB_NAME_CATALOG + vmInstanceUUID);
			getCatalogScheduler().scheduleJob(jobDetail, trigger);
		}catch(SchedulerException e){
			logger.error("Failed to scheduler Vsphere catalog job", e);
		}
	}*/
	
	public void startVSphereCatalogJobForAllVM(){
		if (!StringUtil.isExistingPath(ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath())) {
			return;
		}
		String configurationPath = ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath();
		File file = new File(configurationPath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		try{
			for (File one : files) {
				String filename = one.getName();
				if(filename.length() != 40){
					continue;
				}
				String instanceuuid = filename.substring(0, filename.lastIndexOf('.'));
				if(instanceuuid != null && instanceuuid.startsWith(VMCONFIG_PREFIX))
					continue;
				VSPhereCatalogService.getInstance().startRegularJob(true,instanceuuid);
			}
		}catch(Exception se){
			logger.error("Failed to startVSphereCatalogJobForAllVM",se);
		}
		
	}
	
	public void resumeJobAfterRestart(){
		try{
			ArrayList<JJobContext> jobs = new ArrayList<JJobContext> ();
			long ret = WSJNI.getActiveJobs(jobs);
			if(ret != 0){
				logger.error("Failed to get active jobs from backend for error " + ret);
			}else {
				for(JJobContext job : jobs) {
					BaseVSphereJob.resumeJobAfterRestart(converter.ConvertToVSphereJobContext(job));
				}
			}
		}catch(Exception se){
			logger.error("Failed to resumeJobAfterRestart",se);
		}
	}
	
	public boolean checkRecoveryVMJobExist(String vmName , String esxServerName){
		return this.getNativeFacade().checkRecoveryVMJobExist(vmName, esxServerName);
	}
	
	public void updateApplicationStatus2Edge(String instanceUUID){
		EdgeRegInfo info = null;
		try {
			D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();

			info = edgeRegInfo.getEdgeRegInfo(ApplicationType.vShpereManager);

			if(info == null){
				logger.error("no Edge info in this vSphere");
				return;
			}
		}
		catch(Exception ex) {
			logger.error("Unexpected exception occurs. Fail to update application status to Edge:" + ex.getMessage(), ex);
		}
		
		IEdgeCM4D2D edgeService = null;
		try {
			edgeService = WebServiceFactory.getEdgeService(info.getEdgeWSDL(),IEdgeCM4D2D.class);
		}
		catch(Exception ex) {
			logger.error("Unexpected exception occurs when connecting to Edge. " +
					"Fail to update application status to Edge:" + ex.getMessage(), ex);
		}
		
		JApplicationStatus appStatus = new JApplicationStatus();
		long ret = this.getNativeFacade().getVMApplicationStatus(instanceUUID, appStatus);
		try{
			if(edgeService !=null){
				if(ret == 0){
					edgeService.setVMApplicationStatus(instanceUUID, converter.ConvertToApplicationStatus(appStatus));
				}else{
					logger.error("Can't get application status from backend");
				}
			}
		}catch(Exception ex) {
			logger.error("Unexpected exception occurs when update application statusto Edge. " + ex.getMessage(), ex);
		}
		
	}
	
	//added by Liang.Shu for enhancement to update OS column in console after each backup
	public void updateConsoleWithInfoGotFromBackupJob(VirtualMachine vm){
		String instanceUUID = vm.getVmInstanceUUID();
		VMBackupConfiguration configuration;
		
		try {
			configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
		}
		catch(Exception ex) {
			logger.error("Unexpected exception occurs when geting VM configuration. Failed to update application and OS info to console: " + ex.getMessage(), ex);
			return;
		}		
		
		EdgeRegInfo info = null;
		try {
			D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();

			info = edgeRegInfo.getEdgeRegInfo(ApplicationType.vShpereManager);

			if(info == null){
				logger.error("no Console info");
				return;
			}
		}
		catch(Exception ex) {
			logger.error("Unexpected exception occurs. Failed to update application and OS info to console: " + ex.getMessage(), ex);
		}
		
		IEdgeCM4D2D edgeService = null;
		try {
			edgeService = WebServiceFactory.getEdgeService(info.getEdgeWSDL(),IEdgeCM4D2D.class);
		}
		catch(Exception ex) {
			logger.error("Unexpected exception occured when connecting to Console. " +
					"Failed to update application and OS info to console:" + ex.getMessage(), ex);
		}
		
		JApplicationStatus appStatus = new JApplicationStatus();
		//get application info and set in appStatus
		long ret1 = this.getNativeFacade().getVMApplicationStatus(instanceUUID, appStatus);
		//get OS info and set in appStatus (extend JApplicationStatus by adding OSVersion member, so that we can reuse JApplicationStatus to pass OS info to console)
		long ret2 = getOSVersionFromVMBackupInfoDBXML(configuration, appStatus);
		
		try{
			if(edgeService != null){
				if(ret1 == 0 || ret2 == 0){
					//to reduce API call and also avoid compatibility problem, reuse setVMApplicationStatus() method to update console with both application info and OS info
					edgeService.setVMApplicationStatus(instanceUUID, converter.ConvertToApplicationStatus(appStatus));
				}else{
					logger.error("Couldn't get application information or OS version information from backend.");
				}
			}
		}catch(Exception ex) {
			logger.error("Unexpected exception occured when updating application information or OS Version informtion to Console. " + ex.getMessage(), ex);
		}
	}
	
	//added by Liang.Shu for enhancement to update OS column in console after each backup
	private long getOSVersionFromVMBackupInfoDBXML(VMBackupConfiguration configuration, JApplicationStatus appStatus)
	{
		//Update console with OS version information
		String backuDestPath = configuration.getBackupVM().getDestination();
		String destUserName = configuration.getBackupVM().getDesUsername();
		String destPwd = configuration.getBackupVM().getDesPassword();
		Lock lock = null;
		NativeFacade nvFacade = this.getNativeFacade();
		long ret = 0;
		
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(backuDestPath);
			if (lock != null) {
				lock.lock();
			}
			nvFacade.NetConn(destUserName, destPwd, backuDestPath);
			
			File file = new File(backuDestPath + "\\VMBackupInfoDB.XML");
			if(!file.exists()) {
				logger.error("Cannot access " + backuDestPath + " VMBackupInfoDB.XML.");
				ret = -1;
			}
			else {
				try {
					XPath xpath = XPathFactory.newInstance().newXPath();
					FileInputStream fis = new FileInputStream(file);
					String osVersion = xpath.evaluate("/VMList/VM/@OSVersion", new InputSource(fis));
					appStatus.setOSVersion(osVersion);
					logger.debug("OS Version is: " + osVersion);
				}catch(Exception e) {
					logger.error("Unexpected exception occured when geting OS information from VMBackupInfoDB.XML in backup destination. " + e.getMessage(), e);
					ret = -1;
				}
			}
		} catch (Exception e) {
			logger.error("Failed to connect to backup destination: " + backuDestPath + ". Exception occured: " + e.getMessage(), e);
			ret = -1;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			try {
				nvFacade.disconnectRemotePath(backuDestPath, "", destUserName, destPwd, false);
			} catch (Exception e) {
				logger.error("Failed to disconnect " + backuDestPath + ". Exception occured: " + e.getMessage(), e);
			}
		}	
		return ret;
	}

	public int checkVMRecoveryPointESXUefi(VirtualCenter vc, ESXServer esxServer, String dest, String domain, String user,String pwd, String subPath){
		String sessionPath = getNativeFacade().GetRestorePointPath(dest, domain, user, pwd, subPath);
		if(StringUtil.isEmptyOrNull(sessionPath)){
			logger.error("can't get the session path.");
			return 0;
		}
		
		if(sessionPath.endsWith("\\"))
			sessionPath = sessionPath + VMSNAPSHOTCONFIGINFO;
		else
			sessionPath = sessionPath + "\\" + VMSNAPSHOTCONFIGINFO;
		
		File file = new File(sessionPath);
		if(!file.exists()){
			String msg = String.format("The file[%s] doesn't exit.", sessionPath);
			logger.error(msg);
			return 0;
		}
		
		CAVMwareVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ = new CAVMwareVirtualInfrastructureManager();
			boolean isBackupSessionUEFI = vmwareOBJ.getGuestOSIsUefiFromSerializedFile(sessionPath);
			if(!isBackupSessionUEFI)
				return 0;
			
			int retVal = vmwareOBJ.init(vc.getVcName(), vc.getUsername(), vc.getPassword(), vc.getProtocol(), true, vc.getPort());
			if (retVal == 0) {
				// Successfully connect to esx server
				if (StringUtil.isEmptyOrNull(esxServer.getDataCenter())) {
					//try to restore the org esx server and get the data center
					for (ESXNode esxNode : vmwareOBJ.getESXNodeList()) {
						if (esxNode.getEsxName().equals(esxServer.getEsxName())) {
							esxServer.setEsxName(esxNode.getEsxName());
							esxServer.setDataCenter(esxNode.getDataCenter());
							break;
						}
					}
				}
				boolean isESXSupportUEFI = vmwareOBJ.isESXHostSupportUEFI(converter.ESXServerToESXNode(esxServer));
				vmwareOBJ.close();
				return isESXSupportUEFI?0:1;
			}
			
		}catch (Exception e) {
			logger.error(e);
		}finally{
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		
		return 0;
		
	}
	
	public boolean existScheduledBackup(int date, VirtualMachine vm) {
		try {
			NextScheduleEvent event = this.getNextScheduleEvent(vm);
			if(event == null || event.getDate().getDate() != date)
				return false;
			else
				return true;
		}catch(ServiceException se) {
			logger.error("Failed to get next scheduled event " + se);
		}
		return false;
		
	}
	
	/**
	 * Fetch the volumes on vm.
	 * @param details if false, only volume name and GUID are valid in the returned Volume objects.
	 * @param backupDest if <code>details</code> is true, this value is used to fetch some volume detail information.
	 * @return
	 * @throws ServiceException
	 */
	public Volume[] getVMVolumes(BackupVM vm) throws ServiceException {
		logger.debug("getVMVolumes(boolean, String, String, String) - start");

		try
		{
			FileFolderItem item = getNativeFacade().getVMVolumes(convertToJBackupVM(vm));

			if(item==null || item.getFolders()==null){
				return new Volume[0];
			}
			
			Volume[] returnVolumeArray = new Volume[item.getFolders().length];
			for(int i = 0 ; i < item.getFolders().length; i++){
				Folder folder = item.getFolders()[i];
				Volume volume = new Volume();
				volume.setDisplayName(folder.getName());
				volume.setName(folder.getName());
				returnVolumeArray[i] = volume;
			}
			
			if (returnVolumeArray!=null){
				Arrays.sort(returnVolumeArray, new Comparator<Volume>(){

					@Override
					public int compare(Volume arg0, Volume arg1) {
						return arg0.getName().compareTo(arg1.getName());
					}

				});
			}

			List<Volume> tempArrayList = new LinkedList<Volume>();

			for (int i = 0 ; i<returnVolumeArray.length; i++)
			{
				tempArrayList.add(returnVolumeArray[i]);
			}

			returnVolumeArray = tempArrayList.toArray(new Volume[0]);

			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertArray2String(returnVolumeArray));
			logger.debug("getVMVolumes() - end");
			return returnVolumeArray;
		} catch(Throwable e){
			logger.error("getVMVolumes()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	public FileFolderItem getVMFileFolder(String path, BackupVM vm) throws ServiceException {
		logger.debug("getVMFileFolder(String) - start");
		logger.debug(path);
		
		// check input
		if (StringUtil.isEmptyOrNull(path))
			throw generateAxisFault(FlashServiceErrorCode.Browser_PathNotFound);
		
		try {
			FileFolderItem result = getNativeFacade().getVMFileFolderItem(path, convertToJBackupVM(vm));
		
			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertArray2String(result.getFiles()));
				logger.debug(StringUtil
						.convertArray2String(result.getFolders()));
			}
			logger.debug("getVMFileFolder(String) - end");
			return result;
		} catch (ServiceException ex) {
			throw ex;
		} catch (Throwable e) {
			logger.error("getVMFileFolder()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	public void createVMFolder(String parentPath, String subDir,BackupVM vm) throws ServiceException
	{
		logger.debug("createVMFolder(String, String) - start");
		logger.debug("parentPath:" + parentPath);
		logger.debug("subDir:" + subDir);

		if (StringUtil.isEmptyOrNull(parentPath))
			throw new IllegalArgumentException("Parent pathcan not be null.");
		if(StringUtil.isEmptyOrNull(subDir))
			throw new IllegalArgumentException("subDir can not be null.");

		try{
			getNativeFacade().createVMDir(parentPath, subDir,convertToJBackupVM(vm));
		}
		catch (SecurityException e)
		{
			logger.debug("createVMFolder - SecurityException");
			throw generateAxisFault(FlashServiceErrorCode.Browser_PathNotFound);
		}
	}
	
	private JBackupVM convertToJBackupVM(BackupVM backupVM){
		JBackupVM jBackupVM = new JBackupVM();
		jBackupVM.setEsxServerName(backupVM.getEsxServerName());
		jBackupVM.setEsxUsername(backupVM.getEsxUsername());
		jBackupVM.setEsxPassword(backupVM.getEsxPassword());
		jBackupVM.setProtocol(backupVM.getProtocol());
		jBackupVM.setPort(backupVM.getPort());
		
		jBackupVM.setVmName(backupVM.getVmName());
		jBackupVM.setUsername(backupVM.getUsername());
		jBackupVM.setPassword(backupVM.getPassword());
		jBackupVM.setInstanceUUID(backupVM.getInstanceUUID());
		jBackupVM.setVmVMX(backupVM.getVmVMX());
		
		return jBackupVM;
	}
	
	/**
	 * Get RPS policy uuid for the specified vm if backup to RPS.
	 * @param vmInstanceUUID
	 * @return
	 */
	public String getRpsPolicyUUID(String vmInstanceUUID){
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vmInstanceUUID);
		try {
			VMBackupConfiguration configuration = this.getVMBackupConfiguration(vm);
			if(configuration != null && !configuration.isD2dOrRPSDestType())
				return configuration.getBackupRpsDestSetting().getRPSPolicyUUID();
		} catch (ServiceException e) {
			logger.error("Failed to get vm backup configuration");
		}
		return null;
	}
	
	public String getVMName(String vmInstanceUUID) {
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vmInstanceUUID);
		try {
			VMBackupConfiguration configuration = this.getVMBackupConfiguration(vm);
			if(configuration != null && configuration.getBackupVM() != null)
				return configuration.getBackupVM().getVmName();
			else
				return null;
		} catch (ServiceException e) {
			logger.error("Failed to get vm backup configuration");
		} catch(Throwable t) {
			logger.error("Failed to get vm backup configuration");
		}
		return null;
	}
	
	public BackupRPSDestSetting getRpsSetting(String vmInstanceUUID){
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vmInstanceUUID);
		try {
			VMBackupConfiguration configuration = this.getVMBackupConfiguration(vm);
			if(configuration != null && !configuration.isD2dOrRPSDestType())
				return configuration.getBackupRpsDestSetting();
		} catch (ServiceException e) {
			logger.error("Failed to get vm backup configuration");
		}
		return null;
	}
	
	/**
	 * Get RPS datastore uuid for the specified vm if backup to RPS
	 * @param vmInstanceUUID
	 * @return
	 */
	public String getRpsDataStoreUUID(String vmInstanceUUID){
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vmInstanceUUID);
		try {
			VMBackupConfiguration configuration = this.getVMBackupConfiguration(vm);
			if(configuration != null && !configuration.isD2dOrRPSDestType())
				return configuration.getBackupRpsDestSetting().getRPSDataStore();
		} catch (ServiceException e) {
			logger.error("Failed to get vm backup configuration");
		}
		return null;	
	}

	private Date[] getNextRunTime(VirtualMachine vm){
		Date result[] = new Date[3];
		try {
			String[] triggerGroupNames = new String[]{
					getFullBackupTriggerGroupName(vm.getVmInstanceUUID()), 
					getResyncBackupTriggerGroupName(vm.getVmInstanceUUID()),
					getIncBackupTriggerGroupName(vm.getVmInstanceUUID())};
			for (int i = 0; i < triggerGroupNames.length; i++) {
				String[] triggerNames = ScheduleUtils.getTriggerNames(bkpScheduler, triggerGroupNames[i]);
				if((triggerNames == null) ||(triggerNames.length == 0)){
					result[i] = null;
				}
			}
			
			NextScheduleEvent event = getNextScheduleEvent(vm);
			if (event == null) {
				return result;
			}
			if (event.getBackupType() == BackupType.Full) {
				result[0] = event.getDate();
			}
			else if(event.getBackupType() == BackupType.Resync){
				result[1] = event.getDate();
			}
			else{
				result[2] = event.getDate();
			}
			
			Date beginDate = event.getDate();
			java.util.Calendar endCal = java.util.Calendar.getInstance();
			endCal.setTime(beginDate);
			endCal.add(java.util.Calendar.DATE, 8);
			Date endDate = endCal.getTime();
			List<Trigger> backupTriggers = getBackupTriggers(vm.getVmInstanceUUID());

			while ((beginDate.compareTo(endDate) < 0) &&(backupTriggers.size()>0)) {
				Trigger resultTrigger = null;
				Date resultDate = null;
				for (Trigger trigger : backupTriggers) {
					if (resultTrigger == null) {
						resultTrigger = trigger;
						resultDate = trigger.getFireTimeAfter(beginDate);
					} else {
						int compareResult = resultDate.compareTo(trigger
								.getFireTimeAfter(beginDate));
						if (compareResult == 0) {
							compareResult = trigger.getPriority()
									- resultTrigger.getPriority();
						}
						if (compareResult > 0) {
							resultTrigger = trigger;
							resultDate = trigger.getFireTimeAfter(beginDate);
						}
					}
				}

				if (((AbstractTrigger)resultTrigger).getGroup().equals(getFullBackupTriggerGroupName(vm.getVmInstanceUUID())) 
						&& result[0] == null) {
					result[0] = resultDate;
				} else if (((AbstractTrigger)resultTrigger).getGroup().equals(getResyncBackupTriggerGroupName(vm.getVmInstanceUUID()))
						&& result[1] == null) {
					result[1] = resultDate;
				} else if(((AbstractTrigger)resultTrigger).getGroup().equals(getIncBackupTriggerGroupName(vm.getVmInstanceUUID()))
						&& result[2] == null){
					result[2] = resultDate;
				}

				beginDate = resultDate;
				if((result[0]!=null) && (result[1]!=null) && (result[2]!=null))
					return result;

			}
			return result;
			
		} catch (Exception e) {
			logger.error(e);
		}
		return result;
	}
	
	private void updateBackupConfiguration4RPSPolicy(RpsPolicy4D2D policy, VSphereBackupConfiguration configuration, BackupVM vm){
		configuration.setDestination(policy.getDataStoreSharedPath());
		vm.setDestination(policy.getDataStoreSharedPath());
		String username = policy.getStoreUserName();
		String password = policy.getStorePassword();
		if(StringUtil.isEmptyOrNull(username)){
			username = configuration.getBackupRpsDestSetting().getRpsHost().getUsername();
			if(!username.contains("\\")){
				username = configuration.getBackupRpsDestSetting().getRpsHost()
						.getRhostname() +"\\" + username;
			}
			password = configuration.getBackupRpsDestSetting().getRpsHost().getPassword();
		}
		configuration.setUserName(username);
		configuration.setPassword(password);
		vm.setDesUsername(username);
		vm.setDesPassword(password);
		configuration.setRetentionCount(policy.getRetentionCount());
		configuration.setCompressionLevel(policy.getCompressionMethod());
		configuration.setEnableEncryption(policy.getEncryptionMethod() > 0);
		if(policy.getEncryptionMethod() > 0)
			configuration.setEncryptionAlgorithm(1 << 16 | policy.getEncryptionMethod());
		else
			configuration.setEncryptionAlgorithm(0);
		configuration.getBackupRpsDestSetting().setDedupe(policy.isEnableGDD());
	}
	
	private void updateBackupDestChange4RPS(
			VMBackupConfiguration oldConfiguration,
			VSphereBackupConfiguration newConfiguration) {
		if(oldConfiguration == null)
			return;
		boolean changed = false;
		if(oldConfiguration.isD2dOrRPSDestType() && !newConfiguration.isD2dOrRPSDestType())
			changed = true;
		else if(!oldConfiguration.isD2dOrRPSDestType() && newConfiguration.isD2dOrRPSDestType())
			changed = true;
		else if(!oldConfiguration.isD2dOrRPSDestType() && !newConfiguration.isD2dOrRPSDestType()){
			if(oldConfiguration.getBackupRpsDestSetting() != null 
					&& oldConfiguration.getBackupRpsDestSetting().getRPSDataStore() != null
					&& !oldConfiguration.getBackupRpsDestSetting().getRPSDataStore().equals(
							newConfiguration.getBackupRpsDestSetting().getRPSDataStore())){
				changed = true;
			}
		}
		
		if(changed){
			newConfiguration.setChangedBackupDest(true);
			newConfiguration.setChangedBackupDestType(BackupType.Full);
		}
	}
	
	private void unRegistryD2DIfNeed(VSphereBackupConfiguration newConfiguration,
			VMBackupConfiguration oldConfiguration) {
		if(oldConfiguration!=null && !oldConfiguration.isD2dOrRPSDestType()){
			if(!newConfiguration.isD2dOrRPSDestType()){
				String newRpsHostName = newConfiguration.getBackupRpsDestSetting().getRpsHost().getRhostname();
				String oldRpsHostName = oldConfiguration.getBackupRpsDestSetting().getRpsHost().getRhostname();
				if(newRpsHostName == null || !newRpsHostName.equalsIgnoreCase(oldRpsHostName)){
					unregistryD2D2RPS(oldConfiguration.getBackupRpsDestSetting().getRpsHost(), 
							oldConfiguration.getBackupVM().getInstanceUUID());
				}
			}
			else{
				unregistryD2D2RPS(oldConfiguration.getBackupRpsDestSetting().getRpsHost(), 
						oldConfiguration.getBackupVM().getInstanceUUID());				
			}
		}
	}

	private void unregistryD2D2RPS(RpsHost rpsHost,
			String vmInstanceUUID) {
		if(rpsHost == null || StringUtil.isEmptyOrNull(vmInstanceUUID)){
			logger.error("Invalid parameter");
			return;
		}
		ConfigRPSInD2DService.getInstance().unRegisterD2DToRPSServer(rpsHost, vmInstanceUUID);
	}

	private void saveRPSSetting(VSphereBackupConfiguration configuration,
			BackupVM vm) throws ServiceException {
		// added by liuho04 for defect 88356
		logger.info("Start saveRPSSetting in D2D for VM[InstanceUuid=" + vm.getInstanceUUID() + "]");
		// end added
		registryD2D2PPS(configuration, vm);
		
		logger.info("after registryD2D2PPS  " + vm.getInstanceUUID() + "]");
		
		assignPolicy2D2D(configuration, vm);
		// added by liuho04 for defect 88356
		logger.info("End saveRPSSetting in D2D for VM[InstanceUuid=" + vm.getInstanceUUID() + "]");
		// end added
	}
	
	private boolean saveRPSSettingEx(VSphereBackupConfiguration configuration) throws ServiceException 
	{
		RpsHost rpsHost = configuration.getBackupRpsDestSetting().getRpsHost();
		if (rpsHost == null)
		{
			logger.info("saveRPSSettingEx, rpsHost is NULL");
			return false;
		}

		String protocol = rpsHost.isHttpProtocol() ? "http" : "https";

		IRPSService4D2D client =  
		    RPSServiceProxyManager.getRPSServiceClient(
						rpsHost.getRhostname(), rpsHost.getUsername(),
				        rpsHost.getPassword(),rpsHost.getPort(), 
				        protocol, rpsHost.getUuid());

		String policyId = configuration.getBackupRpsDestSetting().getRPSPolicyUUID();		
		
		List<RegisterNodeInfo> nodeInfoList = new LinkedList<RegisterNodeInfo>();
		String loginUUID = CommonService.getInstance().getLoginUUID();
		Long port = new Long(CommonService.getInstance().getServerPort());
		String serverPortocol = CommonService.getInstance().getServerProtocol();
		if (!serverPortocol.endsWith(":"))
			serverPortocol += ":";

		StringBuilder sbLog = new StringBuilder (); 
		BackupVM[] vmList = configuration.getBackupVMList();
		for (BackupVM vm : vmList)
		{
			RegisterNodeInfo nodeInfo = new RegisterNodeInfo();
			nodeInfo.setLoginUUID(loginUUID);
			nodeInfo.setClientUUID(vm.getInstanceUUID());
			nodeInfo.setNodeName(CommonService.getInstance().getLocalHostAsTrust().getName());
			nodeInfo.setPort(port);
			nodeInfo.setProtocol(serverPortocol);

			nodeInfo.setVmInstanceUUID(vm.getInstanceUUID());
			nodeInfo.setVmName(vm.getVmName() + "@" + vm.getEsxServerName());
			nodeInfo.setBackupDestination(vm.getDestination());
			nodeInfo.setVmUUID(vm.getUuid());
			nodeInfo.setVmHostName(vm.getVmHostName());
			nodeInfo.setIpList(CommonService.getInstance().getNativeFacade().getD2DIPList());			
			nodeInfo.setD2dSID(vm.getInstanceUUID());
			
			sbLog.append("added " + vm.getVmName() + "\r\n");
			nodeInfoList.add(nodeInfo);
		}
		
		sbLog.append("policy Id is " + policyId);
		logger.info("calling RegisterD2DListToPolicy with parameter: " + sbLog.toString());
		client.RegisterD2DListToPolicy(nodeInfoList, policyId);
		
		return true;
	}
	
	
	private void registryD2D2PPS(VSphereBackupConfiguration newConfiguration, BackupVM vm) throws ServiceException{
		if(newConfiguration == null || newConfiguration.getBackupRpsDestSetting() == null || vm == null){
			logger.error("Invalid parameter");
			return ;
		}
		RpsHost rpsHost = newConfiguration.getBackupRpsDestSetting().getRpsHost();

		if(rpsHost == null){
			logger.error("Invalid parameter");
			return ;
		}
		String vmname = vm.getVmName() + "@" + vm.getEsxServerName().trim();
		
		logger.info("22222  " + vm.getInstanceUUID());
		ConfigRPSInD2DService.getInstance().registryD2D2PPS(rpsHost, vm.getInstanceUUID(), 
				vmname, vm.getDestination(), vm.getUuid(), vm.getVmHostName());
	}
	
	private void assignPolicy2D2D(VSphereBackupConfiguration newConfiguration, BackupVM vm) throws ServiceException{
		if(newConfiguration == null || newConfiguration.getBackupRpsDestSetting() == null || vm == null){
			logger.error("Invalid parameter");
			return ;
		}
		RpsHost rpsHost = newConfiguration.getBackupRpsDestSetting().getRpsHost();
		
		logger.info("13131313131  " + vm.getInstanceUUID());
		if(rpsHost == null){
			logger.error("Invalid parameter");
			return ;
		}
		ConfigRPSInD2DService.getInstance().callAssignPolicyToD2D(rpsHost, vm.getInstanceUUID(), 
				newConfiguration.getBackupRpsDestSetting().getRPSPolicyUUID());		
		
		logger.info("1414141414  " + vm.getInstanceUUID());
	}
	
	/**
	 * Run a backup job immediately without goes into RPS job queue.
	 * @param arg
	 * @return
	 * @throws ServiceException
	 */
	public long backupNow(BackupJobArg arg) throws ServiceException {
	    if (handleErrorFromRPS(arg) == -1)
	    	return -1;
		
	    // 2015-07-23 fix TFS Bug 416690:[211815] HBBU catalog job doesn't start occasionally when HBBU job queue is smaller than data store job queue
	    // if keepRPSJobInProxyRunningQueue is enabled, no need to schedule the job from the beginning
	    if (VMRPSJobSubmitter.keepRPSJobInProxyRunningQueue())
	    {	    	
	    	VMRPSJobSubmitter.getInstance().setReturnedBackupJobArg(arg);
	    	logger.info("RPS submit the backup back for VM: "+ arg.getD2dServerUUID() + " jobId: " + arg.getJobId());
	    	return 0;
	    }
	    else
	    {
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(arg.getD2dServerUUID());
			
			logger.info("start to run backup for VM:"+vm.getVmInstanceUUID());
			VMBackupConfiguration vmBackupConfig = getVMBackupConfiguration(vm);
			if (vmBackupConfig == null){
				logger.debug("There is no backup configuration, return error code");
				throw generateAxisFault(FlashServiceErrorCode.Backup_NoBackupConfiguration);
			}
	
			int retryMax = 3;
			int retryInterval = 1000*3;
			// check whether there is running jobs
			int retryCount = 0;
			boolean isJobRunning = true;
			while(retryCount<retryMax){
				try{
					if (VSphereJobQueue.getInstance().isJobRunning(vm.getVmInstanceUUID(),String.valueOf(Constants.AF_JOBTYPE_VM_BACKUP))){
						retryCount++;
						Thread.currentThread().sleep(retryInterval);
					} else{
						isJobRunning = false;
						break;
					}
				}catch(Exception e){
					logger.error(e);
				}
			}
			
			if (isJobRunning)
				throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
	
			try {
				JobDetailImpl jobDetail;
				
				//Fix for the customer RTC issue 214793
				/*jobDetail = new JobDetailImpl(arg.getJobDetailName(),
						"backupNow", VSphereBackupJob.class);*/
				
				jobDetail = new JobDetailImpl(arg.getJobDetailName(),
						arg.getJobDetailGroup() + "Now", VSphereBackupJob.class);
				
				// update the job detail by the returned job arg
				VSphereBackupJob.updateJobDetailByJobArg(jobDetail, arg, true);
				
				
				SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
				//comment out this code as it will cause failed job retry times not work
	//			//when 2 or more vms backups jobs are scheduled at the same time by quartz schedular, only one vm job will be triggered since auto created name is same for all. 
	//			//hence adding the vm instance uuid name also as part of name.
	//			jobDetail.setName(jobDetail.getName() + "_" + vm.getVmInstanceUUID());
				jobDetail.setName(jobDetail.getName());
				trigger.setName(jobDetail.getName() + "NowTrigger");
				//logger.info("jobDetail.getName is: " + jobDetail.getName() + ", full name: " + jobDetail.getFullName() + ", trigger name: " + trigger.getName());
				bkpScheduler.scheduleJob(jobDetail, trigger);
				logger.debug("backup() - end");
				return 0;
			}  catch(SchedulerException e){
				logger.error("Scheduler exception, delete the job", e);
				try {
					bkpScheduler.deleteJob(new JobKey(arg.getJobDetailName(), arg.getJobDetailGroup()+"Now"));
				}catch(Throwable t) {
					logger.debug("Ignore", t);
				}
				throw generateInternalErrorAxisFault();
			} catch (Throwable e){
				logger.error("validateUser()", e);
				throw generateInternalErrorAxisFault();
			}
		}
	}
	
	public long restoreNow(RestoreJobArg jobArg) throws ServiceException {
	    if (handleErrorFromRPS(jobArg) == -1)
	    	return -1;
		
		RestoreJob job = jobArg.getJobScript();
		
		if(job.getRestoreExchangeGRTOption() == null){
			if (BaseVSphereJob.isJobRunning(job.getVmInstanceUUID(),String.valueOf(Constants.AF_JOBTYPE_RESTORE))){
				throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
			}
		}else{
			if (com.ca.arcflash.webservice.scheduler.RestoreJob.isJobRunning() 
					|| this.getNativeFacade().checkJobExist()){
				throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
			}
		}
		
		try {
			JobDetailImpl jobDetail = new JobDetailImpl(jobArg.getJobDetailName(),Constants.RUN_NOW,
					com.ca.arcflash.webservice.scheduler.VSphereRestoreJob.class);
			jobDetail.getJobDataMap().put("Job", jobArg.getJobScript());
			jobDetail.getJobDataMap().put("vm", new VirtualMachine(job.getVmInstanceUUID()));
			jobDetail.getJobDataMap().put("NativeFacade", this.getNativeFacade());
			jobDetail.getJobDataMap().put(RPS_POLICY_UUID, jobArg.getPolicyUUID());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_UUID, jobArg.getDataStoreUUID());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_DISPLAY_NAME, jobArg.getDataStoreName());
			jobDetail.getJobDataMap().put(RPS_HOST, jobArg.getSrcRps());
			jobDetail.getJobDataMap().put(Constants.RUN_NOW, Boolean.TRUE);
			jobDetail.getJobDataMap().put(JOB_ID, jobArg.getJobId());
			
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0,0);
			trigger.setName(jobDetail.getName()+"Trigger");
			BackupService.getInstance().getOtherScheduler().scheduleJob(jobDetail, trigger);
			
			logger.debug("RestoreNow - end");
			return 0;
		} catch (Throwable e) {
			logger.error("submitRestoreJob()", e);
			throw generateInternalErrorAxisFault();
		}	
	}
	
	public long copyNow(CopyJobArg jobArg) throws ServiceException {
	    if (handleErrorFromRPS(jobArg) == -1)
	    	return -1;
		
		CopyJob job = jobArg.getJobScript();
		try {
			if (BaseVSphereJob.isJobRunning(job.getVmInstanceUUID(),String.valueOf(Constants.AF_JOBTYPE_COPY))) {
				throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
			}
			JobDetailImpl jobDetail = new JobDetailImpl(JOB_NAME_COPYJOB + job.getVmInstanceUUID(),Constants.RUN_NOW,
						com.ca.arcflash.webservice.scheduler.VSphereCopyJob.class);
			jobDetail.getJobDataMap().put("Job", jobArg.getJobScript());
			jobDetail.getJobDataMap().put("vm", new VirtualMachine(job.getVmInstanceUUID()));
			jobDetail.getJobDataMap().put("NativeFacade", this.getNativeFacade());
			jobDetail.getJobDataMap().put(RPS_POLICY_UUID, jobArg.getPolicyUUID());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_UUID, jobArg.getDataStoreUUID());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_DISPLAY_NAME, jobArg.getDataStoreName());
			jobDetail.getJobDataMap().put(RPS_HOST, jobArg.getSrcRps());
			jobDetail.getJobDataMap().put(JOB_ID, jobArg.getJobId());
			jobDetail.getJobDataMap().put(Constants.RUN_NOW, Boolean.TRUE);
			jobDetail.getJobDataMap().put(ON_DEMAND_JOB, jobArg.isOnDemand());
			
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0,0);
			trigger.setName(jobDetail.getName()+"Trigger");
			BackupService.getInstance().getOtherScheduler().scheduleJob(jobDetail, trigger);
			
			logger.debug("submitCopyJob - end");
			return 0;
		} catch (ServiceException se) {
			throw se;
		} catch (Throwable e) {
			logger.error("submitCopyJob()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	public Set<String> getAllConfiguratedVMs() {
		Set<String> vms = new HashSet<String>();
		try {
			if (!StringUtil.isExistingPath(ServiceContext.getInstance()
					.getVsphereBackupConfigurationFolderPath())) {
				return vms;
			}
			String configurationPath = ServiceContext.getInstance()
					.getVsphereBackupConfigurationFolderPath();
			File file = new File(configurationPath);
			if (!file.exists())
				return vms;
			File[] files = file.listFiles();
			if (files == null || files.length == 0) {
				return vms;
			}
			
			for (File one : files) {
				String filename = one.getName();
				String instanceUUID = new String();
				instanceUUID = filename.substring(0, filename.lastIndexOf('.'));
				if(instanceUUID != null && instanceUUID.startsWith(VMCONFIG_PREFIX))
					continue;
				VirtualMachine vm = new VirtualMachine();
				vm.setVmInstanceUUID(instanceUUID);
				VMBackupConfiguration configuration = getVMBackupConfiguration(vm);
				if (configuration != null && configuration.getGenerateType() == GenerateType.MSPManualConversion) {
					logger.info("The configuration is for remote nodes.");
					continue;
				}
				vms.add(instanceUUID);
			}
		}catch(Throwable t) {
			logger.error("Failed to get all configurated vms", t);
		}
		
		return vms;
	}
	
	//there are several cases backup job cannot run if backup to RPS
	//1. Cannot connect to RPS webservice.
	//2. RPS policy not exist.
	//3. RPS datastore service stopped.
	//4. RPS datastore not running.
	@SuppressWarnings("deprecation")
	public RpsPolicy4D2D checkRPS4Backup(VMBackupConfiguration configuration) throws ServiceException {
		logger.debug("Check RPS server and datastore status for backup job");
		
		if(configuration == null || configuration.isD2dOrRPSDestType())
			return null;
			
		BackupRPSDestSetting setting = null;
		
		if((setting = configuration.getBackupRpsDestSetting()) == null)
			return null;
		
		if(configuration.isEnableEncryption() && StringUtil.isEmptyOrNull(configuration.getEncryptionKey())){
			throw new ServiceException(FlashServiceErrorCode.BackupConfig_ERR_Empty_Session_Pwd);
		}
		
		return SettingsService.instance().checkRPS4Backup(setting);
	}	
	
	public VMBackupConfiguration rpsPolicyUpdated(RpsPolicy4D2D policy, boolean encrypted, String vmInstanceUUID) 
			throws ServiceException {
		logger.info("Check whether need to update backup configuration for rps policy");
		if(policy == null){
			logger.error("The input policy is null");
		}
		VMBackupConfiguration configuration = getBackupConfiguration(vmInstanceUUID);
		if(configuration == null)
			return null;
		if(configuration.isD2dOrRPSDestType() || configuration.getBackupRpsDestSetting() == null){
			logger.warn("D2D does not use rps policy");
			return null;
		}
		boolean changed = false;
		BackupRPSDestSetting setting = configuration.getBackupRpsDestSetting();
		if(!setting.getRPSDataStore().equals(policy.getDataStoreName())){
			setting.setRPSDataStore(policy.getDataStoreName());
			changed = true;
		}
		if(setting.getRPSDataStoreDisplayName() == null
				|| !setting.getRPSDataStoreDisplayName().equals(policy.getDataStoreDisplayName()))
			changed = true;
		setting.setRPSDataStoreDisplayName(policy.getDataStoreDisplayName());
		String destination = configuration.getDestination();
		String storePath = policy.getDataStoreSharedPath();		
		BackupVM backupVM = configuration.getBackupVM();
		if(!destination.equals(storePath)){
			configuration.setDestination(storePath);
			backupVM.setDestination(storePath);
			changed = true;
		}
		if(!StringUtil.isEmptyOrNull(policy.getStoreUserName()) 
				&& !StringUtil.equals(configuration.getUserName(), policy.getStoreUserName())){
			configuration.setUserName(policy.getStoreUserName());
			backupVM.setDesUsername(policy.getStoreUserName());
			changed = true;
		}
		if(!StringUtil.isEmptyOrNull(policy.getStorePassword())
				&& !StringUtil.isEmptyOrNull(policy.getStorePassword())){
			String decryptedPass = policy.getStorePassword();
			if(encrypted){
				decryptedPass =	WSJNI.AFDecryptStringEx(policy.getStorePassword());
				policy.setStorePassword(decryptedPass);
			}
			if(!decryptedPass.equals(configuration.getPassword())){
				configuration.setPassword(decryptedPass);
				backupVM.setDesPassword(decryptedPass);
				changed = true;
			}	
		}
		
		if(configuration.getRetentionCount() != policy.getRetentionCount()){
			changed = true;
			configuration.setRetentionCount(policy.getRetentionCount());
		}
		if(changed){
			configuration.setBackupRpsDestSetting(setting);
			VSphereBackupConfiguration vsphereConf = 
					backupConfigurationDAO.VMConfigToVSphereConfig(configuration);
			BackupVM[] vms = new BackupVM[]{backupVM};
			vsphereConf.setBackupVMList(vms);
			saveVSphereBackupConfiguration(vsphereConf, policy);
		}
		logger.info("Check whether need to update backup configuration for rps policy end");
		return configuration;
	}
	
	

	public synchronized boolean isBackupToRPS(String vmInstanceUUID) {
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vmInstanceUUID);
		try {
			VMBackupConfiguration configuration = this.getVMBackupConfiguration(vm);
			if (configuration != null && configuration.getGenerateType() == GenerateType.MSPManualConversion) {
				logger.info("The configuration is for remote nodes.");
				return false;
			}
			return configuration != null && !configuration.isD2dOrRPSDestType();
		} catch (ServiceException e) {
			logger.error("Failed to get vm backup configuration");
		}
		return false;
	}
	
	public synchronized boolean isPlanDisabled(String vmInstanceUUID) {
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vmInstanceUUID);
		try {
			VMBackupConfiguration configuration = this.getVMBackupConfiguration(vm);
			return configuration != null && configuration.isDisablePlan();
		} catch (ServiceException e) {
			logger.error("Failed to get vm backup configuration");
		}
		return false;
	}
	
	protected boolean isShowVIXNotInstallMessage(VMBackupConfiguration config){
		String pre = config.getCommandBeforeBackup();
		String post = config.getCommandAfterBackup();
		String postSnap = config.getCommandAfterSnapshot();
		
		if (config.getPurgeSQLLogDays()!=0 || config.getPurgeExchangeLogDays()!=0 ||
				!StringUtil.isEmptyOrNull(pre) || !StringUtil.isEmptyOrNull(post) || !StringUtil.isEmptyOrNull(postSnap))
			return true;
		return false;
	}
	
	public List<VDSInfo> getVDSInfoList(VirtualCenter vc, String esx) throws ServiceException{
		logger.debug("getVDSInfoList start");
		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory
					.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc
							.getUsername(), vc.getPassword(), vc.getProtocol(),
							true, vc.getPort());
			
			ArrayList<vDSSwitchInfo> arrayListvDSSwitchInfo = new ArrayList<vDSSwitchInfo>();
			vmwareOBJ.enumeratevDSSwitchInfo(esx, arrayListvDSSwitchInfo);
			
			List<VDSInfo> result = new LinkedList<VDSInfo>();
			for (vDSSwitchInfo vds : arrayListvDSSwitchInfo){
				try{
					ArrayList<vDSPortGroupInfo> portGroups = new ArrayList<vDSPortGroupInfo>();
					ArrayList<vDSPortGroup> vdsGroup = new ArrayList<vDSPortGroup>();
					
					int errorCode = vmwareOBJ.enumeratevDSPortGroupInfo(vds.getvDSSwitchName(), portGroups);
					
					VDSInfo vdsInfo = new VDSInfo();
					vdsInfo.setvDSSwitchName(vds.getvDSSwitchName());
					vdsInfo.setvDSSwitchUUID(vds.getvDSSwitchUUID());
					
					if (errorCode==0){
						for(int i = 0; i < portGroups.size(); ++i){
							vDSPortGroup portGrp = new vDSPortGroup();
							portGrp.setvDSPortGroupKey(portGroups.get(i).getvDSPortGroupKey());
							portGrp.setvDSPortGroupName(portGroups.get(i).getvDSPortGroupName());
							vdsGroup.add(portGrp);
						}
						vdsInfo.setPortGroups(vdsGroup);
						result.add(vdsInfo);
					}
					else
						logger.error("failed to get port groups with error code:"+errorCode);
				}catch(Exception e){
					logger.error("failed to get port groups", e);
				}
			}
			
			return result;
		} catch (Exception e) {
			logger.error("getVDSInfoList failed", e);
			throw new ServiceException("failed to get VDS information", "");
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
	}
	
		
	public List<VMNetworkConfig> getVMNetworkConfigList(String rootPath, int sessionNum, String userName, String password) throws ServiceException{
		logger.debug("getVMNetworkConfigList start");
		CAVirtualInfrastructureManager vmwareOBJ = new CAVMwareVirtualInfrastructureManager();
		ArrayList<VMNetworkConfigInfo> networkConfigInfoList = new ArrayList<VMNetworkConfigInfo>();
		ArrayList<VMNetworkConfig> networkConfigList = new ArrayList<VMNetworkConfig>();
		Lock lock = null;
		String sessionPath = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(rootPath);
			if(lock != null) {
				lock.lock();
			}
			this.getNativeFacade().NetConn(userName, password, rootPath);
			
			sessionPath = WSJNI.getSessPathByNo(rootPath, sessionNum);
			if(0 == vmwareOBJ.getVMNetworkConfigInfo(sessionPath, networkConfigInfoList)) {
				for(int i = 0; i < networkConfigInfoList.size(); ++i) {
					VMNetworkConfig config = new VMNetworkConfig();
					config.setBackingInfoType(networkConfigInfoList.get(i).getBackingInfoType());
					config.setDeviceName(networkConfigInfoList.get(i).getDeviceName());
					config.setDeviceType(networkConfigInfoList.get(i).getDeviceType());
					config.setLabel(networkConfigInfoList.get(i).getLabel());
					config.setPortgroupKey(networkConfigInfoList.get(i).getPortgroupKey());
					config.setPortgroupName(networkConfigInfoList.get(i).getPortgroupName());
					config.setSwitchName(networkConfigInfoList.get(i).getSwitchName());
					config.setSwitchUUID(networkConfigInfoList.get(i).getSwitchUUID());
					networkConfigList.add(config);
				}
			}
		  
		} catch (Exception e) {
			logger.error("getVMNetworkConfigList failed", e);
			throw new ServiceException("failed to get VM NetworkConfig information", "");
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
		return networkConfigList;
	}
	
	public List<StandNetworkConfigInfo> getESXStandardNetworkInfoList(VirtualCenter vc, String esx) throws ServiceException{
		logger.debug("getStandardNetworkInfoList start");
		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc.getUsername(), vc.getPassword(), vc.getProtocol(), true, vc.getPort());
			ArrayList<StandardNetworkInfo> networkInfoList = new ArrayList<StandardNetworkInfo>();
			ArrayList<StandNetworkConfigInfo> resultList = new ArrayList<StandNetworkConfigInfo>();

			if (0 == vmwareOBJ.enumerateStandardNetworkInfo(esx, networkInfoList)) {
				int size = networkInfoList.size();
				for (int i = 0; i < size; ++i) {
					StandNetworkConfigInfo config = new StandNetworkConfigInfo();
					config.setNetworkName(networkInfoList.get(i).getNetworkName());
					resultList.add(config);
				}
				return resultList;
			}
		} catch (Exception e) {
			logger.error("getStandardNetworkInfoList failed", e);
			throw new ServiceException("failed to get VM standard network configure information", "");
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close VCenter/ESX server failed");
				}
			}
		}
		return null;
	}

	public long recoveryVMNow(RestoreJobArg jobArg) throws ServiceException {
	    if (handleErrorFromRPS(jobArg) == -1)
	    	return -1;
		
		RestoreJob job = jobArg.getJobScript();
		try {
			JobDetailImpl jobDetail = new JobDetailImpl(jobArg.getJobDetailName()+"Now",
					null,
					com.ca.arcflash.webservice.scheduler.VSphereRecoveryJob.class);
			jobDetail.getJobDataMap().put("Job", job);
			jobDetail.getJobDataMap().put("vm", new VirtualMachine(job.getVmInstanceUUID()));
			jobDetail.getJobDataMap().put("NativeFacade",
					this.getNativeFacade());
			jobDetail.getJobDataMap().put(RPS_POLICY_UUID, jobArg.getPolicyUUID());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_UUID, jobArg.getDataStoreUUID());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_DISPLAY_NAME, jobArg.getDataStoreName());
			jobDetail.getJobDataMap().put(RPS_HOST, jobArg.getSrcRps());
			jobDetail.getJobDataMap().put(Constants.RUN_NOW, Boolean.TRUE);
			jobDetail.getJobDataMap().put(JOB_ID, jobArg.getJobId());
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			trigger.setName(jobDetail.getName() + "Trigger");
			bkpScheduler.scheduleJob(jobDetail, trigger);
			logger.debug("submitRecoveryVMJob - end");
		} catch (Throwable e) {
			logger.error("submitRecoveryVMJob()", e);
			throw generateInternalErrorAxisFault();
		}
		return 0;
	}
	
	public RPSDataStoreInfo getVMDataStoreInformation(VirtualMachine vm, String dataStoreUUID) throws ServiceException {
		VMBackupConfiguration configuration = this.getVMBackupConfiguration(vm);
		RpsHost tempRPSHost = configuration.getBackupRpsDestSetting().getRpsHost();
		IRPSService4D2D client = RPSServiceProxyManager.getRPSServiceClient(
				tempRPSHost.getRhostname(), tempRPSHost.getUsername(),
				tempRPSHost.getPassword(), tempRPSHost.getPort(),
				tempRPSHost.isHttpProtocol() ? "http" : "https", tempRPSHost.getUuid());
		RPSDataStoreInfo rpsDSInfo = null;
		if(client !=  null)
			rpsDSInfo = backupSummaryConverter.convert(client.getDataStoreStatus(dataStoreUUID));
		return rpsDSInfo;
	}
	
	public void validateHyperV(String host, String user, String password) throws ServiceException{
		try{
			WSJNI.GetVmList(host, user, password, false);
		}catch(ServiceException e) {
			if(e.getErrorCode().equals("100")) {
				throw new ServiceException(FlashServiceErrorCode.VSPHERE_VALIDATE_HYPERV_CANNOTCONNECT);
			} else if(e.getErrorCode().equals("200")) {
				throw new ServiceException(FlashServiceErrorCode.VSPHERE_VALIDATE_HYPERV_CANNOTGETVMLIST);
			}
			
			throw e;
		}
	}
	
	public Disk[] getHyperVBackupVMDisk(String destination, String subPath,	String domain, String username, String password)
			throws ServiceException {
		logger.debug(destination);
		logger.debug(subPath);
		logger.debug(username);
		
		try {
			List<JDisk> jDiskList = this.getNativeFacade().getHyperVBackupVMDisk(
					destination, subPath, domain, username, password);
			
			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertList2String(jDiskList));
			
			if (jDiskList != null && jDiskList.size() > 0) {
				Disk[] diskArray = new Disk[jDiskList.size()];
				int i = 0;
				for (JDisk jDisk : jDiskList) {
					diskArray[i++] = converter.ConvertToDisk(jDisk);
				}
				return diskArray;
			}
		} catch (Throwable e) {
			throw this.generateInternalErrorAxisFault();
		}
		return null;
	}

	public List<VMNetworkConfig> getHyperVVMNetworkConfigList(
			String destination, String subPath, String domain, String username,
			String password) {
		logger.debug(destination);
		logger.debug(subPath);
		logger.debug(username);
		
		List<JVMNetworkConfig> networkList = new ArrayList<JVMNetworkConfig>();
		WSJNI.AFGetHyperVVMNetworkList(destination, subPath, "", username, password, networkList);
		
		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertList2String(networkList));
		
		List<VMNetworkConfig> networkConfigList = new LinkedList<VMNetworkConfig>();
		for(int i = 0; i < networkList.size(); ++i) {
			VMNetworkConfig config = new VMNetworkConfig();
			config.setSwitchUUID(networkList.get(i).getSwitchUUID());
			config.setHyperVAdapterType(networkList.get(i).getAdapterType());
			config.setDeviceName(networkList.get(i).getAdapterFriendlyName());
			config.setHyperVAdapterID(networkList.get(i).getId());
			networkConfigList.add(config);
		}
		return networkConfigList;
	}
	
	public RetryPolicy getRetryPolicy(String jobTypeName) {
		logger.debug("getRetryPolicy - start");
		NativeFacade nativeFacade = getNativeFacade();
		RetryPolicy result = null;
		boolean error = false;
		String msg = "";
		try {
			result = CommonService.getInstance().getRetryPolicy(jobTypeName);
			if (result == null) {
				if (jobTypeName != null && jobTypeName.equals(CommonService.RETRY_CATALOG)) {
					result = CatalogService.getInstance().defaultRetryPolicy();
				} else {
					result = new RetryPolicy();
				}
			}
		} catch (Exception e) {
			error = true;
			msg = e.getMessage();
			nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY, new String[] {
					WebServiceMessages.getResource(Constants.RETRYPOLICY_READ_ERROR), "", "", "", "" });
			logger.error("getRetryPolicy - " + e.getMessage(), e);
		}
		if (!error) {
			if (result.isEnabled() && result.isFailedEnabled()) {
				if (result.getMaxTimes() <= 0) {
					error = true;
					msg = "invalid MaxTimes attribute";
				} else if (!result.isImmediately() && result.getTimeToWait() <= 0) {
					error = true;
					msg = "invalid TimeToWait attribute";
				}
			}
			if (!error && result.isEnabled() && (result.isFailedEnabled() || result.isMissedEnabled())) {
				if (result.getNearToNextEvent() <= 0) {
					error = true;
					msg = "invalid NearToNextEvent attribute";
				}
			}
			if (error) {
				nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY, new String[] {
						WebServiceMessages.getResource(Constants.RETRYPOLICY_READ_ERROR), "", "", "", "" });
				logger.debug("getRetryPolicy - " + msg);
				result = new RetryPolicy();
			}
		}

		logger.debug("getRetryPolicy - end");
		return result;
	}
	
	private boolean isProxyESXVDDKCompatible(RecoverVMOption recoverOption){
		boolean isAMD64 = false;
		try {
			short cpu = WSJNI.GetHostProcessorArchitectural();
			if(cpu == 9){
				isAMD64 = true;
			}
		} catch (Exception e) {
			logger.error(e);
			return true;
		}

		VMwareManagerCache vmwareManagerCache = new VMwareManagerCache();
		String esxHost = recoverOption.getEsxServerName();
		
		String version = null;
		CAVMwareVirtualInfrastructureManager manager = vmwareManagerCache.get(recoverOption.getVc());
		try{
			if (manager.getVMwareServerType() == VMwareServerType.esxServer)
				version = manager.GetESXServerVersion();
			else{
				ArrayList<ESXNode> nodes = manager.getESXNodeList();
				for (ESXNode node:nodes){
					if (node.getEsxName().equalsIgnoreCase(esxHost)){
						version = manager.getESXHostVersion(node);
						break;
					}
				}
			}
		}catch(Exception e){
			logger.error(e);
			return true;
		}
		
		//if (!isAMD64 && "5.5.0".equals(version)){
		if (!isAMD64 && compareTwoVersions("5.5.0",version)<=0){
			return false;
		}
		
		return true;
	}
	
	public String getHyperVDefaultFolderOfVHD(String server, String userName, String password){
		long handle = 0;
		try{
			handle = getNativeFacade().OpenHypervHandle(server, userName, password);
			return this.getNativeFacade().getHyperVDefaultFolderOfVHD(handle);
		}catch(Exception e){
			logger.error("fail to get default VHD path for Hyper-V", e);
			return null;
		}finally{
			try {
				getNativeFacade().CloseHypervHandle(handle);
			} catch (Exception e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}
	
	public String GetHyperVDefaultFolderOfVM(String server, String userName, String password){
		long handle = 0;
		try{
			handle = getNativeFacade().OpenHypervHandle(server, userName, password);
			return this.getNativeFacade().GetHyperVDefaultFolderOfVM(handle);
		}catch(Exception e){
			logger.error("fail to get default VM path for Hyper-V", e);
			return null;
		}finally{
			try {
				getNativeFacade().CloseHypervHandle(handle);
			} catch (Exception e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}
	
	public long getVMVFlashReadCache(String rootPath, int sessionNum, String userName, String password) throws ServiceException {
		logger.debug("getVMVFlashReadCache start");
		CAVirtualInfrastructureManager vmwareOBJ = new CAVMwareVirtualInfrastructureManager();
		Lock lock = null;
		NativeFacade nvFacade = this.getNativeFacade();
		String sessionPath = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(rootPath);
			if (lock != null) {
				lock.lock();
			}
			nvFacade.NetConn(userName, password, rootPath);
			sessionPath = WSJNI.getSessPathByNo(rootPath, sessionNum);
			return vmwareOBJ.getVMVFlashReadCache(sessionPath);
		} catch (Exception e) {
			logger.error("getVMVFlashReadCache failed", e);
			throw new ServiceException("failed to get VM flash read cache size from backup session", "");
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
			if (lock != null) {
				lock.unlock();
			}

			try {
				nvFacade.disconnectRemotePath(sessionPath, "", userName, password, false);
			} catch (Exception e) {
				logger.error("Disconnect " + sessionPath + " failed");
			}
		}
	}

	public long getESXVFlashResource(VirtualCenter vc, ESXServer esxServer) throws ServiceException {
		logger.debug("getESXVFlashResource start");
		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc.getUsername(), vc.getPassword(), vc.getProtocol(), true, vc.getPort());
			return vmwareOBJ.getESXVFlashResource(esxServer.getEsxName(), esxServer.getDataCenter());
		} catch (Exception e) {
			logger.debug("getESXVFlashResource failed");
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}

		return -1;
	}
	
	public RecoveryPointSummary[] getRecoveryPointSummary(String[] vmInstanceUUIDs) throws ServiceException {
		if (vmInstanceUUIDs==null || vmInstanceUUIDs.length == 0){
			VMItem[] vms = getConfiguredVM();
			if (vms == null)
				return new RecoveryPointSummary[0];
			vmInstanceUUIDs = new String[vms.length];
			for (int i=0;i<vms.length;i++)
				vmInstanceUUIDs[i] = vms[i].getVmInstanceUUID();
		}
		
		List<RecoveryPointSummary> result = new LinkedList<RecoveryPointSummary>();
		for (String uuid:vmInstanceUUIDs){
			try{
				VirtualMachine vm = new VirtualMachine();
				vm.setVmInstanceUUID(uuid);
				RecoveryPointSummary recoveryPointSummary = getRecoveryPointSummary(vm);
				if (recoveryPointSummary!=null)
					result.add(recoveryPointSummary);
			}catch(Exception e){
				logger.error("getRecoveryPointSummary", e);
			}
		}
		
		return result.toArray(new RecoveryPointSummary[0]);
	}
	
	private RecoveryPointSummary getRecoveryPointSummary(VirtualMachine vm) throws ServiceException {
		
		logger.debug("getVMRecoveryPointSummary() - start");
		
		RecoveryPointSummary rpSummary = new RecoveryPointSummary();
		rpSummary.setNodeId(vm.getVmInstanceUUID());
		
		java.util.Calendar beginDate = java.util.Calendar.getInstance();
		beginDate.set(1970, 0, 1);
		java.util.Calendar endDate = java.util.Calendar.getInstance();
		endDate.set(2999, 11, 31);
		
		VMBackupConfiguration conf = this.getVMBackupConfiguration(vm);
		if (!conf.isD2dOrRPSDestType()){
			logger.info("VM's destination is RPS, ignore it");
			return null;
		}
		RecoveryPoint[] recoveryPoints = RestoreService.getInstance().getRecoveryPoints(conf.getBackupVM().getDestination(), "", conf.getBackupVM().getDesUsername(), conf.getBackupVM().getDesPassword(), beginDate.getTime(), endDate.getTime(), false);		 

		if (recoveryPoints != null && recoveryPoints.length > 0) {		
			String backupDest = null;
			RecoveryInfoStatistics data = null;

			for (RecoveryPoint pi : recoveryPoints) {
				if (backupDest==null || !backupDest.equalsIgnoreCase(pi.getBackupDest())) {											
					data = new RecoveryInfoStatistics();
					data.setDestination(pi.getBackupDest());
					rpSummary.getStatisData().add(data);
					backupDest = pi.getBackupDest();
				}

				if (pi.getBackupType() == BackupType.Full || pi.getBackupType() == BackupType.Incremental || pi.getBackupType() == BackupType.Resync) {
					data.setDataSize(data.getDataSize() +  pi.getDataSize());
					data.setRawdataSize(data.getRawdataSize() +  pi.getLogicalSize());					
				}
			}
		}
		
		logger.debug("getVMRecoveryPointSummary() - end");
		return rpSummary;
	}
	
	
	public int getHyperVServerType(String server, String userName, String password) {
		long handle = 0;
		try{
			handle = getNativeFacade().OpenHypervHandle(server, userName, password);
			return WSJNI.getHyperVServerType(handle);
		}catch(Exception e){
			logger.error("fail to get hyper-v server type", e);
			return -1;
		}finally{
			try {
				getNativeFacade().CloseHypervHandle(handle);
			} catch (Exception e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}

	
	public List<String> getHyperVClusterNodes(String server, String userName, String password) {
		long handle = 0;
		try{
			handle = getNativeFacade().OpenHypervHandle(server, userName, password);
			return getNativeFacade().getHyperVClusterNodes(handle);
		}catch(Exception e){
			logger.error("fail to get hyper-v cluster nodes", e);
			return null;
		}finally{
			try {
				getNativeFacade().CloseHypervHandle(handle);
			} catch (Exception e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}
	
	protected String getUserNameFromConfiguration(BackupConfiguration config) {
		return ((VMBackupConfiguration)config).getBackupVM().getDesUsername();
	}
	
	protected String getPasswordFromConfiguration(BackupConfiguration config) {
		return ((VMBackupConfiguration)config).getBackupVM().getDesPassword();
	}
	
	public int validateVCloud(VCloudDirector vCloudInfo) throws ServiceException {
		VCloudManager vCloudManager = null;
		try {
			vCloudManager = VCloudManagerFactory.connect(vCloudInfo.getName(), vCloudInfo.getProtocol(), vCloudInfo.getPort(),
					vCloudInfo.getUsername(), vCloudInfo.getPassword());
			if (vCloudManager != null) {
				return 0;
			} else {
				logger.error("validateVCloud failed for vCloud: failed to get VCloudManager from VCloudManagerFactory#connect");
				throw new ServiceException(FlashServiceErrorCode.VSPHERE_CONNECT_VCENTER_ERROR, new Object[] { vCloudInfo.getName() });
			}
		} catch (Exception e) {
			logger.error("validateVCloud failed for vCloud", e);
			throw new ServiceException(FlashServiceErrorCode.VSPHERE_CONNECT_VCENTER_ERROR, new Object[] { vCloudInfo.getName() });
		} finally {
			if (vCloudManager != null) {
				vCloudManager.disconnect();
			}
		}
	}
	
	public List<VCloudOrg> getVCloudOrganizations(VCloudDirector vCloudInfo) {
		List<VCloudOrg> resultOrgList = new ArrayList<VCloudOrg>();
		
		if (vCloudInfo == null) {
			logger.warn("Invalid vCloud connection information.");
			return resultOrgList;
		}

		VCloudManager vCloudManager = null;
		try {
			vCloudManager = VCloudManagerFactory.connect(vCloudInfo.getName(), vCloudInfo.getProtocol(), vCloudInfo.getPort(),
					vCloudInfo.getUsername(), vCloudInfo.getPassword());
			if (vCloudManager == null) {
				logger.warn("Failed to build connection.");
				return resultOrgList;
			}

			List<VCloudOrganization> orgs = vCloudManager.getOrganizations();
			if (orgs == null || orgs.size() <= 0) {
				return resultOrgList;
			}
			
			for (VCloudOrganization org : orgs) {
				VCloudOrg resultOrg = converter.convertVCouldOrganization2VCloudOrg(org);
				resultOrg.setVCloudVDCs(getVDCList(vCloudManager, org.getId()));

				resultOrgList.add(resultOrg);
			}

			Collections.sort(resultOrgList, converter.new NameComparator<VCloudOrg>());
			return resultOrgList;
		} catch (Exception e) {
			logger.error("Failed to get vCenters for vCloud", e);
			resultOrgList.clear();
			return resultOrgList;
		} finally {
			if (vCloudManager != null) {
				vCloudManager.disconnect();
			}
		}
	}
	
	private List<VCloudVirtualDC> getVDCList(VCloudManager vCloudManager, String orgId){
		List<VCloudVirtualDC> vDCResultList = new ArrayList<VCloudVirtualDC>();
		
		try {
			List<VCloudVDC> vDCList = vCloudManager.getVDCs(orgId);
			if (vDCList != null && vDCList.size() > 0) {
				for (VCloudVDC vDC :  vDCList) {
					VCloudVirtualDC resultVDC = converter.convertVCouldVDC2VCloudVirtualDC(vDC);
					
					VCloudVC vCenter = converter.convertVCloudVCenter2VCloudVC(vCloudManager.getVCenter(vDC.getvCenterId()));
					if (vCenter != null) {
						resultVDC.setVirtrualDataCenter(Arrays.asList(new VCloudVC[] {vCenter}));
					} else {
						resultVDC.setVirtrualDataCenter(new ArrayList<VCloudVC>());
					}
					
					vDCResultList.add(resultVDC);
				}
			}
			
			Collections.sort(vDCResultList, converter.new NameComparator<VCloudVirtualDC>());
			return vDCResultList;
		} catch (Exception e) {
			logger.warn("Failed to get VDC list");
			vDCResultList.clear();
			return vDCResultList;
		}
	}

	public List<VCloudVDCStorageProfile> getStorageProfilesOfVDC(VCloudDirector vCloudInfo, String vDCId) {
		List<VCloudVDCStorageProfile> resultProfileList = new ArrayList<VCloudVDCStorageProfile>();
		
		if (vCloudInfo == null) {
			logger.warn("Invalid vCloud connection information.");
			return resultProfileList;
		}

		VCloudManager vCloudManager = null;
		try {
			vCloudManager = VCloudManagerFactory.connect(vCloudInfo.getName(), vCloudInfo.getProtocol(), vCloudInfo.getPort(),
					vCloudInfo.getUsername(), vCloudInfo.getPassword());
			if (vCloudManager == null) {
				logger.warn("Failed to build connection.");
				return resultProfileList;
			}

			List<VCloudStorageProfile> profiles = vCloudManager.getStorageProfilesOfVDC(vDCId);
			if (profiles == null || profiles.size() <= 0) {
				return resultProfileList;
			}
			
			for (VCloudStorageProfile profile : profiles) {
				VCloudVDCStorageProfile resultProfile = converter.convertVCloudStorageProfile2VCloudVDCStorageProfile(profile);
				if (resultProfile != null) {
					resultProfileList.add(resultProfile);
				}
			}
			
			Collections.sort(resultProfileList, converter.new NameComparator<VCloudVDCStorageProfile>());
			return resultProfileList;
		} catch (Exception e) {
			logger.error("Failed to get vCenters for vCloud", e);
			resultProfileList.clear();
			return resultProfileList;
		} finally {
			if (vCloudManager != null) {
				vCloudManager.disconnect();
			}
		}
	}
	
	public List<ESXServer> getESXHosts4VAppChildVM(VCloudDirector vCloudInfo, VirtualCenter vCenter, String vDCId,
			String datastoreMoRef) {
		List<ESXNode> resultESXList = new ArrayList<ESXNode>();

		List<String> esxListOnVCloud = getESXHostsByVDCOnVCloud(vCloudInfo, vDCId);
		if (!esxListOnVCloud.isEmpty()) {
			List<String> esxListOnVCenter = getESXHostsByDatastoreMorefOnVCenter(vCenter, datastoreMoRef);
			if (!esxListOnVCenter.isEmpty()) {
				esxListOnVCenter.retainAll(esxListOnVCloud);
				if (!esxListOnVCenter.isEmpty()) {
					CAVirtualInfrastructureManager vmwareOBJ = null;
					try {
						vmwareOBJ = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(
								vCenter.getVcName(), vCenter.getUsername(), vCenter.getPassword(),
								vCenter.getProtocol(), true, vCenter.getPort());

						for (String esxName : esxListOnVCenter) {
							ESXNode temp = vmwareOBJ.getESXNodeByName(esxName);
							if (temp != null) {
								resultESXList.add(temp);
							}
						}
					} catch (Exception e) {
						logger.error("Failed to get ESX host from vCenter " + vCenter.getVcName(), e);
						resultESXList.clear();
					} finally {
						if (vmwareOBJ != null) {
							try {
								vmwareOBJ.close();
							} catch (Exception e) {
								logger.debug("Close esxserver failed");
							}
						}
					}
				}
			}
		}

		return converter.ESXNodeConverter(resultESXList);
	}
	
	private List<String> getESXHostsByVDCOnVCloud(VCloudDirector vCloudInfo, String vDCId) {
		List<String> esxList = new ArrayList<String>();

		if (vCloudInfo == null) {
			logger.warn("Invalid vCloud connection information.");
			return esxList;
		}

		VCloudManager vCloudManager = null;
		try {
			vCloudManager = VCloudManagerFactory.connect(vCloudInfo.getName(), vCloudInfo.getProtocol(),
					vCloudInfo.getPort(), vCloudInfo.getUsername(), vCloudInfo.getPassword());
			if (vCloudManager == null) {
				logger.warn("Failed to build connection.");
				return esxList;
			}

			List<VCloudESXHost> vClouldESXList = vCloudManager.getESXHostsOfVDC(vDCId);
			if (vClouldESXList != null && !vClouldESXList.isEmpty()) {
				for (VCloudESXHost host : vClouldESXList) {
					esxList.add(host.getName());
				}
			}

			return esxList;
		} catch (Exception e) {
			logger.error("Failed to get ESX host from vCloud for VDC id " + vDCId, e);
			esxList.clear();
			return esxList;
		} finally {
			if (vCloudManager != null) {
				vCloudManager.disconnect();
			}
		}
	}
	
	private List<String> getESXHostsByDatastoreMorefOnVCenter(VirtualCenter vCenter, String datastoreMoRef) {
		List<String> esxList = new ArrayList<String>();
		if (vCenter == null) {
			logger.warn("Invalid vCenter connection information.");
			return esxList;
		}

		CAVirtualInfrastructureManager vmwareOBJ = null;
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(
					vCenter.getVcName(), vCenter.getUsername(), vCenter.getPassword(), vCenter.getProtocol(), true,
					vCenter.getPort());

				List<String> temp = vmwareOBJ.getESXHostsByDatastoreMoRef(datastoreMoRef);
				if (temp != null && !temp.isEmpty()) {
					esxList.addAll(temp);
				}
		} catch (Exception e) {
			logger.error("Failed to get ESX host from vCenter " + vCenter.getVcName(), e);
			esxList.clear();
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
		
		return esxList;
	}
	
	public List<VMNetworkConfig> getVAppAndChildVMNetworkConfigLists(String rootPath, int sessionNum,
			String userName, String password) {
		logger.debug("getVAppAndChildVMNetworkConfigLists start");
		List<VMNetworkConfig> resultList = new ArrayList<>();

		VAppSessionInfo sessionInfo = null;
		try {
			sessionInfo = getVAppSessionInformations(rootPath, sessionNum, userName, password);
		} catch (ServiceException e) {
			return resultList;
		}
		
		if (sessionInfo == null) {
			return resultList;
		}

		String vAppName = sessionInfo.getvApp().getName();
		List<VCloudNetwork> vCloudNetworks = sessionInfo.getvApp().getvAppNetworks();
		if (vCloudNetworks != null && !vCloudNetworks.isEmpty()) {
			List<VMNetworkConfig> networkConfigList = converter
					.convertVCloudNetworkList2VMNetworkConfigList(vAppName, vCloudNetworks);
			if (networkConfigList != null && !networkConfigList.isEmpty()) {
				resultList.addAll(networkConfigList);
			}
		}

		List<VCloudVM> childVMs = sessionInfo.getVmList();
		if (childVMs != null && !childVMs.isEmpty()) {
			for (VCloudVM childVM : childVMs) {
				List<VCloudNetwork> childVMNetworks = childVM.getVmNetworkCards();
				List<VMNetworkConfig> networkConfigList = converter
						.convertVCloudNetworkList2VMNetworkConfigList(childVM.getName(), childVMNetworks);
				if (networkConfigList != null && !networkConfigList.isEmpty()) {
					resultList.addAll(networkConfigList);
				}
			}
		}

		return resultList;
	}

	private VAppSessionInfo getVAppSessionInformations(String rootPath, int sessionNum, String userName, String password)
			throws ServiceException {
		logger.debug("getVAppSessionInformations start");

		Lock lock = null;
		String sessionPath = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(rootPath);
			if (lock != null) {
				lock.lock();
			}
			this.getNativeFacade().NetConn(userName, password, rootPath);
			sessionPath = WSJNI.getSessPathByNo(rootPath, sessionNum);

			VCloudManager sessionManager = VCloudManagerFactory.connectToSession(sessionPath);
			return sessionManager.loadVAppSessionInfo();
		} catch (Exception e) {
			logger.error("getVMNetworkConfigList failed", e);
			throw new ServiceException("failed to get VM NetworkConfig information", "");
		} finally {
			if (lock != null) {
				lock.unlock();
			}

			try {
				this.getNativeFacade().disconnectRemotePath(sessionPath, "", userName, password, false);
			} catch (Exception e) {
				logger.error("Disconnect " + sessionPath + " failed");
			}
		}
	}
	
	public void cancelvAppChildVMJob(String vAppInstanceUUID, long jobType) {
		if(StringUtil.isEmptyOrNull(vAppInstanceUUID)) {
			logger.info("vApp instance uuid is null or empty!");
			return;
		}
		VirtualMachine virtualMachine = new VirtualMachine();
		virtualMachine.setVmInstanceUUID(vAppInstanceUUID);
		VMBackupConfiguration configuration;
		IVSphereJobQueue jobQueue = null;
		try {
			if(jobType == JobType.JOBTYPE_VM_RECOVERY) {
				jobQueue = VSphereRestoreJobQueue.getInstance();
			} else {
				jobQueue = VSphereJobQueue.getInstance();
			}
			configuration = getVMBackupConfiguration(virtualMachine);
			if(configuration == null) {
				logger.info("Failed to get plan for vApp " + vAppInstanceUUID);
				return;
			}
			BackupVM vm = configuration.getBackupVM();
			if(vm.getVAppMemberVMs() != null) {
				//Need to first cancel the waiting job then cancel the running job so that
				//The waiting job will not go into the running queue when running job has free slot
				List<VirtualMachine> vmList = new ArrayList<VirtualMachine>();
				for(BackupVM childVM : vm.getVAppMemberVMs()) {
					if(jobQueue.isJobRunning(childVM.getInstanceUUID(), 
							String.valueOf(jobType))) {
						VirtualMachine tempVM = new VirtualMachine();
						tempVM.setVmInstanceUUID(childVM.getInstanceUUID());
						tempVM.setVmName(childVM.getVmName());
						vmList.add(tempVM);
					} else if(jobQueue.isJobWaiting(childVM.getInstanceUUID())) {
						cancelWaitingJob(childVM.getInstanceUUID());
					}
				}
				for(VirtualMachine runningVM : vmList) {
					CommonService.getInstance().cancelVMJob(runningVM, jobType);
				}
			}
		} catch (ServiceException e) {
			logger.info("Cancel vApp child VM job failed " + e.getMessage());
		}
	}
	
	public void waitUntilvAppChildJobCancelled(String vAppInstanceUUID, long jobType) {
		if(StringUtil.isEmptyOrNull(vAppInstanceUUID)) {
			logger.info("vApp instance uuid is null or empty!");
		}
		int retryCount = 60;
		do {
			if(isAllvAppChildJobCanceled(vAppInstanceUUID, jobType)) { 
				break;
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
		} while (--retryCount > 0);
	}
	
	public boolean isAllvAppChildJobCanceled(String vAppInstanceUUID, long jobType) {
		if(StringUtil.isEmptyOrNull(vAppInstanceUUID)) {
			logger.info("vApp instance uuid is null or empty!");
			return true;
		}
		VirtualMachine virtualMachine = new VirtualMachine();
		virtualMachine.setVmInstanceUUID(vAppInstanceUUID);
		VMBackupConfiguration configuration;
		IVSphereJobQueue jobQueue = null;
		try {
			if(jobType == JobType.JOBTYPE_VM_RECOVERY) {
				jobQueue = VSphereRestoreJobQueue.getInstance();
			} else {
				jobQueue = VSphereJobQueue.getInstance();
			}
			configuration = getVMBackupConfiguration(virtualMachine);
			if(configuration == null) {
				logger.info("Failed to get plan for vApp " + vAppInstanceUUID);
				return true;
			}
			BackupVM vm = configuration.getBackupVM();
			if(vm.getVAppMemberVMs() != null) {
				for(BackupVM childVM : vm.getVAppMemberVMs()) {
					if(jobQueue.isJobRunning(childVM.getInstanceUUID(), 
							String.valueOf(jobType)) ||
							jobQueue.isJobWaiting(childVM.getInstanceUUID())) {
						return false;
					} 
				}
			}
		} catch (ServiceException e) {
			logger.info("Wait until vApp child job cancelled failed " + e.getMessage());
		} 
		return true;
	}

	public void cancelGroupJob(String groupInstanceUUID, long jobID, long jobType) throws ServiceException {
		if(StringUtil.isEmptyOrNull(groupInstanceUUID)) {
			logger.info("vApp instance uuid is null or empty!");
			return;
		}
		int returnVlaue = 0;
		try {
			
			VirtualMachine virtualMachine = new VirtualMachine();
			virtualMachine.setVmInstanceUUID(groupInstanceUUID);
			VMBackupConfiguration configuration;
			IVSphereJobQueue jobQueue = null;
			if(jobType == JobType.JOBTYPE_VM_RECOVERY) {
				jobQueue = VSphereRestoreJobQueue.getInstance();
			} else {
				jobQueue = VSphereJobQueue.getInstance();
			}
			configuration = getVMBackupConfiguration(virtualMachine);
			if(configuration == null) {
				logger.info("Failed to get plan for group " + groupInstanceUUID + ". Hence try to cancel by using Job ID value");
				returnVlaue = this.getNativeFacade().cancelJob(jobID);
			}
			else{
				BackupVM vm = configuration.getBackupVM();
				List<Long> finishedChildJobIDs = new ArrayList<Long>();
				if(vm.getVAppMemberVMs() != null) {
					for(BackupVM childVM : vm.getVAppMemberVMs()) {
						if(jobQueue.isJobWaiting(childVM.getInstanceUUID())) {
							long childJobID = jobQueue.getWaitingJobID(childVM.getInstanceUUID());
							finishedChildJobIDs.add(childJobID);
							cancelWaitingJob(childVM.getInstanceUUID());
						}
					}
				}
				returnVlaue = this.getNativeFacade().cancelGroupJob(jobType, jobID, finishedChildJobIDs);
			}
		} catch (Throwable e) {
			throw generateInternalErrorAxisFault();
		}
		if (returnVlaue != 0) {
			throw generateAxisFault(FlashServiceErrorCode.Common_CancelJobFailed);
		}
	}
	
	public void cancelWaitingJobByDataStoreUUID(String datastoreUUID) throws ServiceException {
		VSphereJobQueue.getInstance().removeWaitingJobByDatastoreUUID(datastoreUUID);
		VSphereRestoreJobQueue.getInstance().removeWaitingJobByDatastoreUUID(datastoreUUID);
	}
	
	public Map<String, Disk_Info> getVMDiskConfigList(String rootPath, int sessionNum, String userName, String password) throws ServiceException{
		logger.debug("getVMDiskConfigList start");
		CAVirtualInfrastructureManager vmwareOBJ = new CAVMwareVirtualInfrastructureManager();
		Lock lock = null;
		String sessionPath = null;
		Map<String, Disk_Info> result = new HashMap<String, Disk_Info>();
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(rootPath);
			if(lock != null) {
				lock.lock();
			}
			this.getNativeFacade().NetConn(userName, password, rootPath);
			sessionPath = WSJNI.getSessPathByNo(rootPath, sessionNum);
			List<Disk_Info> networkConfigInfoList = vmwareOBJ.getVMDiskInfo(sessionPath);
			for (Disk_Info disk:networkConfigInfoList)
				result.put(disk.getdiskURL(), disk);
			
			return result;
		} catch (Exception e) {
			logger.error("getVMDiskConfigList failed", e);
			return result;
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
	
	private boolean needToCheckVIXInstalled(VMBackupConfiguration config, VirtualCenter vc) {
		
		CAVMwareVirtualInfrastructureManager vmwareOBJ = null;
		
		try {
			vmwareOBJ = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(vc.getVcName(), vc
					.getUsername(), vc.getPassword(), vc.getProtocol(),
					true, vc.getPort());
			VSphereLicenseCheck.getNormalizedESXHostName(config);
			
			String esxHost = null;
			String version = null;
			try{
				esxHost = VSphereLicenseCheck.getNormalizedESXHostName(config);
			}catch(Exception e){
				logger.error(e);
				return false;
			}
			
			if (vmwareOBJ.getVMwareServerType() == VMwareServerType.esxServer)
				version = vmwareOBJ.GetESXServerVersion();
			else{
				ArrayList<ESXNode> nodes = vmwareOBJ.getESXNodeList();
				for (ESXNode node:nodes){
					if (node.getEsxName().equalsIgnoreCase(esxHost)){
						version = vmwareOBJ.getESXHostVersion(node);
						break;
					}
				}
			}
			
			String[] versions = version.split("\\.");
	        int majorVersion = Integer.parseInt(versions[0]);
	        
	        if (majorVersion < 5){
	        	return true;
	        }
			
	        return false;
		} catch(Exception ex){
			logger.error("needToCheckVIXInstalled failed", ex);
			return false;
		} finally {
			if (vmwareOBJ != null) {
				try {
					vmwareOBJ.close();
				} catch (Exception e) {
					logger.debug("Close esxserver failed");
				}
			}
		}
	}
	
	public void modifyPeriodJobDetail(JobDetail jobDetail, JobDetail oriJobDetail) {
		if (jobDetail == null || oriJobDetail == null)
			return;

		boolean isDaily = false, isWeekly = false, isMonthly = false;

		if (oriJobDetail.getJobDataMap().containsKey("periodRetentionFlag")) {
			int periodRetentionFlag = oriJobDetail.getJobDataMap().getInt("periodRetentionFlag");
			isDaily = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Daily) > 0;
			isWeekly = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Weekly) > 0;
			isMonthly = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Monthly) > 0;
		} else {
			if (oriJobDetail.getJobDataMap().containsKey("isDaily")) {
				isDaily = oriJobDetail.getJobDataMap().getBoolean("isDaily");
			}

			if (oriJobDetail.getJobDataMap().containsKey("isWeekly")) {
				isWeekly = oriJobDetail.getJobDataMap().getBoolean("isWeekly");
			}

			if (oriJobDetail.getJobDataMap().containsKey("isMonthly")) {
				isMonthly = oriJobDetail.getJobDataMap().getBoolean("isMonthly");
			}
		}

		if (isDaily) {
			jobDetail.getJobDataMap().put("isDaily", new Boolean(isDaily));
		}

		if (isWeekly) {
			jobDetail.getJobDataMap().put("isWeekly", new Boolean(isWeekly));
		}

		if (isMonthly) {
			jobDetail.getJobDataMap().put("isMonthly", new Boolean(isMonthly));
		}
	}
	
	
	//fix for RTC 220939
	public static int compareTwoVersions(String version1, String version2) {
		logger.info("version1: " + version1 + ", version2: " + version2);
        if(version2 == null || version2.isEmpty()){
        	logger.info("returning 1");
            return 1;
        }
        String[] thisParts = version1.split("\\.");
        String[] thatParts = version2.split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for(int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ?
                Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ?
                Integer.parseInt(thatParts[i]) : 0;
            if(thisPart < thatPart){
            	logger.info("returning -1");
                return -1;
            }
            if(thisPart > thatPart){
            	logger.info("returning 1");
                return 1;
            }
        }
        logger.info("returning 0");
        return 0;
    }

	/**
	 * to compensate the missed backup, if full backup is missed, start a full
	 * backup, else if resync backup is missed, start a resync, else if
	 * incremental is missed, start a incremental.
	 */

	public void dealWithMissedBackup() {
		logger.debug("configJobSchedule() - start");
		// BackupVM[] backupVMArray = configuration.getBackupVMList();
		// for(BackupVM backupVM : backupVMArray){
		if (!StringUtil.isExistingPath(ServiceContext.getInstance()	.getVsphereBackupConfigurationFolderPath())) {
			return;
		}
		String configurationPath = ServiceContext.getInstance().getVsphereBackupConfigurationFolderPath();
		File file = new File(configurationPath);
		if (!file.exists())
			return;
		
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		
		for (File one : files) {
			try{
				VirtualMachine vm = new VirtualMachine();
				String filename = one.getName();
				String instanceUUID = new String();
				instanceUUID = filename.substring(0, filename.lastIndexOf('.'));
				if(instanceUUID != null && instanceUUID.startsWith(VMCONFIG_PREFIX))
					continue;
				vm.setVmInstanceUUID(instanceUUID);
				
				VMBackupConfiguration configuration = this.getVMBackupConfiguration(vm);
				if (configuration == null) {
					logger.debug("dealWithMissedBackup() - end with null configuration");
					continue;
				}
				if (configuration.getBackupDataFormat() == 0) {
					makeupProcessor.dealWithMissedBackupStandardFormat(vm);
				} else {
					makeupProcessor.dealWithMissedBackupAdvancedFormat(vm);
				}
			}catch(Exception e){
				logger.error("fail to deal missed job", e);
			}
		}
	}

	public long getRPSDatastoreVersion(String dataStoreUUID, String vmInstanceUuid) {
		long version = 0;
		try {
			if (!StringUtil.isEmptyOrNull(dataStoreUUID)) {
				
				VirtualMachine vm = new VirtualMachine();
				vm.setVmInstanceUUID(vmInstanceUuid);
				
				RPSDataStoreInfo rpsDataStoreInfo = getVMDataStoreInformation(vm, dataStoreUUID);
				version = rpsDataStoreInfo.getVersion();
			}
			
			VMBackupConfiguration configuration = getBackupConfiguration(vmInstanceUuid);
			RpsHost rpsHost = configuration.getBackupRpsDestSetting().getRpsHost();
			return SettingsService.instance().getRPSDataStoreVersion(rpsHost, dataStoreUUID);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

		return version;
	}
	
	public long getAgentInstalledVolumeFreeSpaceInMB(StringBuilder driveLetter) {  //driverLetter is an output parameter to return the drive letter of the volume where agent is installed
		long freeSpace = 0;
		try {
			String installPath = ServiceContext.getInstance().getVsphereBackupConfigurationFolderPath();
			
			File file = new File(installPath);
			freeSpace = file.getFreeSpace();
			driveLetter.append(installPath.substring(0, 2));
			
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

		return freeSpace/1024/1024;
	}
	
	public void agentInstalledVolumeFreeSpaceCheck(VMBackupConfiguration configuration) {
		long defaultThresholdValue = 1024; //default threshold is 1024MB
		long thresholdValue = 0;
		
		try {
			JRWLong jValue = new JRWLong();
			String d2dRegRoot = CommonRegistryKey.getD2DRegistryRoot();
			int result = WSJNI.GetRegIntValue("Engine", RegConstants.REGISTRY_PROXY_INSTALL_VOLUME_ALERT_THRESHOLD, d2dRegRoot.substring(0, d2dRegRoot.lastIndexOf('\\')), jValue);
			if (result != 0){
				logger.debug("Failed to get the threshold of agent install volume free space.");
				thresholdValue = defaultThresholdValue; //if threshold is not set in registry, use default threshold value 1024MB
			}
			else
				thresholdValue = (long)jValue.getValue();
			
			if (thresholdValue == 0) //if threshold is set to 0 in registry, it means there is no threshold or threshold is infinity
				return;
			
			StringBuilder driveLetter = new StringBuilder();
			long freeSpace = getAgentInstalledVolumeFreeSpaceInMB(driveLetter);
			
			if (freeSpace < thresholdValue)
			{
				String[] errorParameters = new String[] { System.getenv("COMPUTERNAME"), driveLetter.toString(), String.valueOf(thresholdValue), "", ""};
				this.getNativeFacade().addVMLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_VSPHERE_HBBU_PROXY_INSTALL_VOLUME_FREE_SPACE_ALERT, errorParameters, configuration.getBackupVM().getInstanceUUID());
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
		
		return;
	}

}
