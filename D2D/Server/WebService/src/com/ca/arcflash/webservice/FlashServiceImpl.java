package com.ca.arcflash.webservice;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.locks.Lock;

import javax.annotation.Resource;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.net.ssl.SSLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.xpath.XPathConstants;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import sun.misc.BASE64Encoder;

import com.arcserve.cserp.entitlements.entitlementReg.client.EntitlementRegister;
import com.arcserve.cserp.entitlements.entitlementReg.utility.EntitlementRegisterUtility;
import com.ca.arcflash.asbu.webservice.data.ASBUHost;
import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.JobMonitorConstants;
import com.ca.arcflash.common.MessageFormatEx;
import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.modelmanager.RepositoryUtil;
import com.ca.arcflash.common.xml.PMXPathReader;
import com.ca.arcflash.failover.HyperVFailoverCommand;
import com.ca.arcflash.failover.VMwareCenterServerFailoverCommand;
import com.ca.arcflash.failover.VMwareESXFailoverCommand;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.failover.model.DiskExtent;
import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.ARCFlashNodesSummary;
import com.ca.arcflash.ha.model.DiskInfo;
import com.ca.arcflash.ha.model.ESXServerInfo;
import com.ca.arcflash.ha.model.EdgeLicenseInfo;
import com.ca.arcflash.ha.model.EsxHostInformation;
import com.ca.arcflash.ha.model.EsxServerInformation;
import com.ca.arcflash.ha.model.FileVersion;
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
import com.ca.arcflash.ha.model.manager.HeartBeatModelManager;
import com.ca.arcflash.ha.model.manager.VMInfomationModelManager;
import com.ca.arcflash.ha.modelWebService.MonitorWebServiceErrorCode;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.ha.utils.VCMPolicyUtils;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanager.Host_Info;
import com.ca.arcflash.ha.vmwaremanager.VMwareStorage;
import com.ca.arcflash.ha.vmwaremanager.VirtualNetworkInfo;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.jni.common.JMountRecoveryPointParams;
import com.ca.arcflash.job.AFJob;
import com.ca.arcflash.job.HAJobStatus;
import com.ca.arcflash.job.JobFactory;
import com.ca.arcflash.job.failover.FailoverJob;
import com.ca.arcflash.job.heartbeat.HeartBeatJob;
import com.ca.arcflash.jobqueue.JobQueueFactory;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.DiskExtentModel;
import com.ca.arcflash.jobscript.replication.DiskModel;
import com.ca.arcflash.jobscript.replication.Protocol;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMStorage;
import com.ca.arcflash.listener.manager.ListenerManager;
import com.ca.arcflash.replication.ReplicationService;
import com.ca.arcflash.repository.RepositoryManager;
import com.ca.arcflash.repository.RepositoryModel;
import com.ca.arcflash.rps.webservice.data.RpsArchiveConfiguationWrapper;
import com.ca.arcflash.rps.webservice.data.catalog.RpsCatalogJobQueryItem;
import com.ca.arcflash.rps.webservice.data.catalog.RpsCatalogStatusItem;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4D2D;
import com.ca.arcflash.service.common.ActivityLogSyncher;
import com.ca.arcflash.serviceinfo.ServiceInfoConstants;
import com.ca.arcflash.webservice.common.HostNameUtil;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.BackupInformationSummary;
import com.ca.arcflash.webservice.data.ConvertJobMonitor;
import com.ca.arcflash.webservice.data.D2DOnRPS;
import com.ca.arcflash.webservice.data.DataSizesFromStorage;
import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.DeployUpgradeInfo;
import com.ca.arcflash.webservice.data.DestinationCapacity;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.EveryMonthSchedule;
import com.ca.arcflash.webservice.data.EveryWeekSchedule;
import com.ca.arcflash.webservice.data.HyperVDestinationInfo;
import com.ca.arcflash.webservice.data.JMountPoint;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.JobMonitorHistoryItem;
import com.ca.arcflash.webservice.data.LicInfo;
import com.ca.arcflash.webservice.data.MachineDetail;
import com.ca.arcflash.webservice.data.MachineType;
import com.ca.arcflash.webservice.data.MountSession;
import com.ca.arcflash.webservice.data.NetworkPath;
import com.ca.arcflash.webservice.data.NextArchiveScheduleEvent;
import com.ca.arcflash.webservice.data.NextScheduleEvent;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.ProtectionInformation;
import com.ca.arcflash.webservice.data.RPSDataStoreInfo;
import com.ca.arcflash.webservice.data.RecoveryPointSummary;
import com.ca.arcflash.webservice.data.SourceNodeSysInfo;
import com.ca.arcflash.webservice.data.TrustedHost;
import com.ca.arcflash.webservice.data.VMwareConnParams;
import com.ca.arcflash.webservice.data.VMwareServer;
import com.ca.arcflash.webservice.data.VWWareESXNode;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.data.PM.AutoUpdateSettings;
import com.ca.arcflash.webservice.data.PM.BIPatchInfo;
import com.ca.arcflash.webservice.data.PM.PMResponse;
import com.ca.arcflash.webservice.data.PM.PatchInfo;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.ad.ADAttribute;
import com.ca.arcflash.webservice.data.ad.ADNode;
import com.ca.arcflash.webservice.data.ad.ADNodeFilter;
import com.ca.arcflash.webservice.data.ad.ADPagingConfig;
import com.ca.arcflash.webservice.data.ad.ADPagingResult;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationConfig;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationDetailsConfig;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationVolumeConfig;
import com.ca.arcflash.webservice.data.archive.ArchiveDiskDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveFileItem;
import com.ca.arcflash.webservice.data.archive.ArchiveJobInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveSourceInfoConfiguration;
import com.ca.arcflash.webservice.data.archive.CloudProviderInfo;
import com.ca.arcflash.webservice.data.archive.JArchiveJob;
import com.ca.arcflash.webservice.data.archive.RestoreArchiveJob;
import com.ca.arcflash.webservice.data.archive.RestoreJobArchiveVolumeNode;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveConfig;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveJobSession;
import com.ca.arcflash.webservice.data.backup.Account;
import com.ca.arcflash.webservice.data.backup.ApplicationWriter;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.BackupVolumes;
import com.ca.arcflash.webservice.data.backup.D2DConfiguration;
import com.ca.arcflash.webservice.data.backup.HBBUConfiguration;
import com.ca.arcflash.webservice.data.backup.RpsPolicy4D2D;
import com.ca.arcflash.webservice.data.backup.SRMPkiAlertSetting;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.callback.MergeFailureInfo;
import com.ca.arcflash.webservice.data.catalog.ArchiveCatalogItem;
import com.ca.arcflash.webservice.data.catalog.CatalogItem;
import com.ca.arcflash.webservice.data.catalog.CatalogJobPara;
import com.ca.arcflash.webservice.data.catalog.GRTBrowsingContext;
import com.ca.arcflash.webservice.data.catalog.GRTCatalogItem;
import com.ca.arcflash.webservice.data.catalog.PagedCatalogItem;
import com.ca.arcflash.webservice.data.catalog.PagedExchangeDiscoveryItem;
import com.ca.arcflash.webservice.data.catalog.PagedGRTCatalogItem;
import com.ca.arcflash.webservice.data.catalog.SearchContext;
import com.ca.arcflash.webservice.data.catalog.SearchResult;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.data.job.history.FlashJobHistoryFilter;
import com.ca.arcflash.webservice.data.job.history.FlashJobHistoryResult;
import com.ca.arcflash.webservice.data.job.rps.ArchiveJobArg;
import com.ca.arcflash.webservice.data.job.rps.BackupJobArg;
import com.ca.arcflash.webservice.data.job.rps.CatalogJobArg;
import com.ca.arcflash.webservice.data.job.rps.ConversionJobArg;
import com.ca.arcflash.webservice.data.job.rps.CopyJobArg;
import com.ca.arcflash.webservice.data.job.rps.JobDependencySource;
import com.ca.arcflash.webservice.data.job.rps.RestoreJobArg;
import com.ca.arcflash.webservice.data.listener.FlashListenerInfo;
import com.ca.arcflash.webservice.data.logcollect.DiagInfoCollectorConfiguration;
import com.ca.arcflash.webservice.data.login.LoginDetail;
import com.ca.arcflash.webservice.data.merge.BackupSetInfo;
import com.ca.arcflash.webservice.data.merge.MergeAPISource;
import com.ca.arcflash.webservice.data.merge.MergeJobMonitor;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.data.remotedeploy.RemoteDeployTarget;
import com.ca.arcflash.webservice.data.remotedeploy.RemoteDeployTargetDetail;
import com.ca.arcflash.webservice.data.restore.AlternativePath;
import com.ca.arcflash.webservice.data.restore.CatalogInfo;
import com.ca.arcflash.webservice.data.restore.CopyJob;
import com.ca.arcflash.webservice.data.restore.ExchangeDiscoveryItem;
import com.ca.arcflash.webservice.data.restore.MountedRecoveryPointItem;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.restore.RecoveryPointItem;
import com.ca.arcflash.webservice.data.restore.RestoreJob;
import com.ca.arcflash.webservice.data.restore.RestoreJobType;
import com.ca.arcflash.webservice.data.restore.RpsPolicy4D2DRestore;
import com.ca.arcflash.webservice.data.subscription.SubscriptionConfiguration;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.DataStore;
import com.ca.arcflash.webservice.data.vsphere.Disk;
import com.ca.arcflash.webservice.data.vsphere.ESXServer;
import com.ca.arcflash.webservice.data.vsphere.HyperVHostStorage;
import com.ca.arcflash.webservice.data.vsphere.ResourcePool;
import com.ca.arcflash.webservice.data.vsphere.SavePolicyWarning;
import com.ca.arcflash.webservice.data.vsphere.StandNetworkConfigInfo;
import com.ca.arcflash.webservice.data.vsphere.StorageAppliance;
import com.ca.arcflash.webservice.data.vsphere.VAppChildBackupVMRestorePointWrapper;
import com.ca.arcflash.webservice.data.vsphere.VCloudDirector;
import com.ca.arcflash.webservice.data.vsphere.VCloudOrg;
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
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.D2DRegServiceImpl;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.d2dresync.D2DReSyncServiceImpl;
import com.ca.arcflash.webservice.edge.d2dstatus.D2DStatusServiceImpl;
import com.ca.arcflash.webservice.edge.d2dstatus.VCMStatusSyncer;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.edge.data.pfc.PFCVMInfo;
import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcflash.webservice.edge.data.policy.VCMPolicyDeployParameters;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.edge.pfc.D2DPFCServiceImpl;
import com.ca.arcflash.webservice.edge.policymanagement.D2DPolicyManagementServiceImpl;
import com.ca.arcflash.webservice.edge.srmagent.SrmAgentServerImpl;
import com.ca.arcflash.webservice.foredge.ID2D4EdgeVSphere;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.jni.model.JHostNetworkConfig;
import com.ca.arcflash.webservice.jni.model.JHypervVMInfo;
import com.ca.arcflash.webservice.nimsoft.Alert;
import com.ca.arcflash.webservice.nimsoft.NimsoftRegisterInfo;
import com.ca.arcflash.webservice.nimsoft.NimsoftService;
import com.ca.arcflash.webservice.replication.SessionInfo;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.AERPService;
import com.ca.arcflash.webservice.service.ApplicationService;
import com.ca.arcflash.webservice.service.ArchiveCatalogSyncService;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.ArchiveToTapeService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BackupSetService;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.CallbackService;
import com.ca.arcflash.webservice.service.CatalogService;
import com.ca.arcflash.webservice.service.CollectDiagnosticInfoService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.CopyService;
import com.ca.arcflash.webservice.service.DeleteArchiveService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.JobMonitorService;
import com.ca.arcflash.webservice.service.MergeService;
import com.ca.arcflash.webservice.service.MountVolumeService;
import com.ca.arcflash.webservice.service.PurgeArchiveService;
import com.ca.arcflash.webservice.service.RemoteDeployService;
import com.ca.arcflash.webservice.service.RestoreArchiveService;
import com.ca.arcflash.webservice.service.RestoreService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.SubscriptionService;
import com.ca.arcflash.webservice.service.VMCopyService;
import com.ca.arcflash.webservice.service.VSPhereCatalogService;
import com.ca.arcflash.webservice.service.VSPhereFailoverService;
import com.ca.arcflash.webservice.service.VSphereArchiveToTapeService;
import com.ca.arcflash.webservice.service.VSphereBackupSetService;
import com.ca.arcflash.webservice.service.VSphereMergeService;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.PatchManager;
import com.ca.arcflash.webservice.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.webservice.service.rps.RPSServiceProxyManager;
import com.ca.arcflash.webservice.service.rps.SettingsService;
import com.ca.arcflash.webservice.servlet.Util;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;
import com.ca.arcflash.webservice.toedge.exception.EdgeServiceErrorCode;
import com.ca.arcflash.webservice.util.ArchiveToTapeUtils;
import com.ca.arcflash.webservice.util.ArchiveUtil;
import com.ca.arcflash.webservice.util.CommonServiceUtilImpl;
import com.ca.arcflash.webservice.util.ConvertErrorCodeUtil;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.ha.webservice.jni.HyperVException;
import com.ca.ha.webservice.jni.HyperVJNI;
import com.ca.ha.webservice.jni.JHyperVSystemInfo;

@WebService(endpointInterface="com.ca.arcflash.webservice.IFlashService_R16_5")
@Path("/AgentService")
public class FlashServiceImpl implements IFlashService_R16_5 {
	
	@Resource
	private WebServiceContext context;
	CommonServiceUtilImpl commonUtil = null;
	private Object newLock = new Object();

    /**
     * @author robin Gong
     * This is for edge, in that edge will need different behaviour than D2D. 
     * For example, if we want to validate the recovery point copy setting, we don't want to validate the license.  
     * This field will be set true by Edge's MockD2DServiceImpl
     */
    public static boolean edgeFlag = false;
    
	private static final Logger logger = Logger.getLogger(FlashServiceImpl.class);
	public static  String REGISTRY_INSTALLPATH		=	CommonRegistryKey.getD2DRegistryRoot()+"\\InstallPath";
	public static  String REGISTRY_KEY_PATH			=	"Path";
	public static int ERROR_PM_REQ_FAILED = -1;
	public static int ERROR_PM_REQ_SUCCESS = 0;
	public static int ERROR_PM_COMM_FAILED = 1;
	public static int ERROR_PM_COMM_SUCCESS = 2;
	public static int ERROR_PM_SEND_SUCCESS = 3;
	public static int ERROR_PM_SEND_FAILED = 4;
	public static int ERROR_PM_READ_SUCCESS = 5;
	public static int ERROR_PM_READ_FAILED = 6;
	public static final int EVLT_RAID5 = 5;
	public static int JOBTYPE_CATALOG_FS_ONDEMAND = 17;	

	private CommonServiceUtilImpl getCommonUtil() {
		if (commonUtil == null) {
			synchronized (newLock) {
				if (commonUtil == null)
					commonUtil = new CommonServiceUtilImpl(context, httpRequest);
			}
		}
		return commonUtil;
	}
	
	public HttpSession getSession() {
		return getCommonUtil().getSession();
	}

	public void setSession(HttpSession session) {
		getCommonUtil().setSession(session);
	}
	public static void setEnableSessionCheckV2(boolean enableSessionCheck) {
		CommonServiceUtilImpl.setEnableSessionCheck(enableSessionCheck);
	}

	@Override
	public void setEnableSessionCheck(boolean enableSessionCheck) {
		// do nothing, just for compatibility testing
		throw AxisFault.fromAxisFault("Unsupported Method",
				FlashServiceErrorCode.Common_ErrorOccursInService);
	}

	@Override
	public boolean isLocalHost(String host) {
		logger.debug("isLocalHost begin, host:" + host);
		boolean isLocal = CommonService.getInstance().checkLocalHost(host, false);
		logger.debug("isLocalHost end, isLocal:" + isLocal);
		return isLocal;
	}	

	

	protected void checkSession() {
		getCommonUtil().checkSession();
	}

	@Override
	public VersionInfo getVersionInfo() {
		return CommonService.getInstance().getVersionInfo();
	}

	@Override
	@Path("/{username}/{password}/{domain}")
	@GET	
	@Produces({MediaType.APPLICATION_JSON}) 
	public String validateUser(@PathParam("username")String username, @PathParam("password")String password, @PathParam("domain")String domain) {
		return getCommonUtil().validateUser(username, password, domain, true);
	}

	@Override
	public int validateUserByUUID(String uuid) {
		return getCommonUtil().validateUserByUUID(uuid);
	}
	
	/* wanji10
	 * This API will be exposed to the limited interface for documented API
	 * 
	 * This API returns the UUID which is used to identify a ^UDP Agent^ uniquely. 
	 * It cannot be used to pass authentication if passing to validateUserByUUID.
	 * 
	 * 11/20/2013 - wanji10 - for not it still returns the UUID which can be used
	 * to pass authentication, but by setting below registry value to "1" it will return
	 * a different UUID, which cannot be used for authentication.
	 * 
	 *  HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\WebService:EnableNodeID, type: string
	 * 
	 */
	@Override
	public String login(String username, String password, String domain) {
		return getCommonUtil().validateUser(username, password, domain, true);		
	}

	@Override
	public String EstablishTrust(String username, String password, String domain) {
		return getCommonUtil().validateUser(username, password, domain, false);	
	}
	
	
	/* wanji10
	 * This API will be exposed to the limited interface for documented API
	 */
	@Override
	public void logout() {
		logger.debug("logout entered");
		try {
			this.getSession().invalidate();
		} catch (Exception e) {
			logger.info("renewSession got exception:" + e.getMessage());
		}
		setSession(null);
		logger.debug("logout exited");
	}

	@Override
	public boolean createFolder(String parentPath, String subDir) 	{
		checkSession();
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(parentPath);
			//if lock != null, this path is a remote path
			if(lock != null) {
				lock.lock();
				RemoteFolderConnCache.reEstalishConnetion(getSession());
			}
			BrowserService.getInstance().createFolder(parentPath, subDir);
			return true;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally {
			if(lock != null)
				lock.unlock();
		}
	}

	@Override
	public FileFolderItem getFileFolder(String path) {
		checkSession();
		try {
			return BrowserService.getInstance().getFileFolder(path);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public FileFolderItem getFileFolderWithCredentials(String path,
			String user, String pwd) {
		checkSession();
		try {
			return BrowserService.getInstance().getFileFolder(path, user, pwd);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public Volume[] getVolumes() {
		checkSession();
		try {
			Volume[] archiveVolumes = BrowserService.getInstance().getVolumes(
					false, null, null, null);
		   return archiveVolumes;			
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public void cutAllRemoteConnections() {
		checkSession();
		try {
			BrowserService.getInstance().cutAllRemoteConnections();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("cutAllRemoteConnections() got exception",
					"");
		}

	}

	@Override
	public Volume[] getVolumesWithDetails(String backupDest, String usr, String pwd) {
		checkSession();
		try {
			return BrowserService.getInstance().getVolumes(true, backupDest, usr, pwd);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Context HttpServletRequest httpRequest;
	@Override
	@Path("/setting")
	@GET 
    @Produces({MediaType.APPLICATION_JSON}) 
	public BackupConfiguration getBackupConfiguration() {		
		checkSession();		
		try {
			return BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long saveBackupConfiguration(BackupConfiguration configuration)
			{
		checkSession();
		try {
//			SRMPkiAlertSetting alertSetting = configuration.getSrmPkiAlertSetting();
//			if ( alertSetting != null ) {
//			   SaveAlertSetting(alertSetting);
//			}
			return BackupService.getInstance().saveBackupConfiguration(configuration);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long validateBackupConfiguration(BackupConfiguration configuration)
			{
		checkSession();
		try {
//			SRMPkiAlertSetting alertSetting = configuration.getSrmPkiAlertSetting();
//			if ( alertSetting != null ) {
//			   SaveAlertSetting(alertSetting);
//			}
			return BackupService.getInstance().validateBackupConfiguration(configuration);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ScheduledExportConfiguration getScheduledExportConfiguration() {
		checkSession();

		try {
			return CopyService.getInstance().getScheduledExportConfiguration();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long saveScheduledExportConfiguration(
			ScheduledExportConfiguration configuration) {
		checkSession();

		try {
			CopyService.getInstance().saveScheduledExportConfiguration(configuration);
		} catch (ServiceException e) {
			//wanqi06
			ConvertErrorCodeUtil.checkScheduleConfigurationConvert(e);
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}

		return 0;
	}

	@Override
	public long validateScheduledExportConfiguration(
			ScheduledExportConfiguration configuration) {
		checkSession();

		try {
			CopyService.getInstance().setEdgeFlag(edgeFlag);
			return CopyService.getInstance().validateScheduledExportConfiguration(configuration);
		} catch (ServiceException e) {
			//wanqi06
			ConvertErrorCodeUtil.checkScheduleConfigurationConvert(e);
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public CatalogItem[] getCatalogItems(String catalogFilePath, long parentID)
			{
		checkSession();
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(catalogFilePath);
			//if lock != null, this path is a remote path
			if(lock != null) {
				lock.lock();
				RemoteFolderConnCache.reEstalishConnetion(getSession());
			}
			return BrowserService.getInstance().getCatalogItems(
					catalogFilePath, parentID);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally {
			if(lock != null) {
				lock.unlock();
			}
		}
	}	
	
	@Override
	public CatalogItem[] getCatalogItemsEx(String userName, String passWord, long sessionNumber, String volumeGUID, String catlogFilePath, 
			long parentID, String parentPath, String encryptedPwd)
	{
		checkSession();
		
		try {
			return RestoreService.getInstance().getCatalogItems(null, userName, passWord, sessionNumber, volumeGUID, 
					catlogFilePath, parentID, parentPath, encryptedPwd, getSession());
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public PagedCatalogItem getPagedCatalogItemsEx(String userName, String passWord, long sessionNumber, 
			String volumeGUID, String catlogFilePath, long parentID, 
			String parentPath,int start, int size, String encryptedPwd) {
		checkSession();
		
		try {
			return RestoreService.getInstance().getPagedCatalogItemsEx(null, userName, passWord, sessionNumber, volumeGUID, 
					catlogFilePath, parentID, parentPath, start, size, encryptedPwd, getSession());
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} 
	}

	@Override
	public PagedCatalogItem getPagedFileItems(String mountVolGUID, String catPath, String parentID, int start, int size) {
		checkSession();
		Lock lock = null;
		try {
			return BrowserService.getInstance().getPagedFileItems(mountVolGUID, catPath,parentID,start,size);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally {
			if(lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	public long getRecoveryPointItemChildrenCount(long sessNum, String catalogPath, 
			String volumeGUID, String userName, String passWord, String encryptedPwd)
	{
		checkSession();
		try {
			long childCount = RestoreService.getInstance()
					.getRecoveryPointItemsChildCount(sessNum, catalogPath, volumeGUID, null,  
							userName, passWord, encryptedPwd);
			logger.debug("getRecoveryPointItemsChildCount end");
			return childCount;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	private void ProcessResponseForRequestStatus(PMResponse in_objPMResponse)
	{
		StringReader objResponseReader = new StringReader(in_objPMResponse.getM_PMResponse());
		InputSource inpSource = new InputSource(objResponseReader);
		PMXPathReader objXPathReader = new PMXPathReader(inpSource);
		objXPathReader.Initialise();
		try {

			/* * molve01:
			 * Add code to find whether Patch Manager sent Scheduled Job Active message
			 * If Scheduled job is active inform in the Tray Icon, that job is already running.
			 * Close the connection and re-submit the CheckForUpdate Request
			 * "BackEnd Message" will be sent from the Patch Manager only in the case of
			 * Active Job failed.
			 * */

			//Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( inputSource );
			String sXPath = "/Message/Header/Type";
			Object obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
			if(obj != null)
			{
				if(obj.toString().compareToIgnoreCase("BackEnd Message")== 0)
				{
					sXPath = "/Message/Body/JobStatus";
					obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
					if(obj != null)
					{
						if(obj.toString().compareToIgnoreCase("Failed")== 0)
						{
							in_objPMResponse.setIsRequestFailed(true);
							sXPath = "/Message/Body/ErrorMessage";
							obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
							in_objPMResponse.setM_ErrorMessage(obj.toString());
						}
					}
				}
			}
		}
		catch(Exception e)
		{

		}
		return;
	}
//	public boolean SendRequest(PMRequest objPMRequest)
//	{
//		try {
//			int iretStatus = BrowserService.getInstance().getNativeFacade().WriteFile((int)namedPipeHandle/*file*/,
//					(String)objPMRequest.getM_PMReq()/*buffer*/,
//					(int)objPMRequest.getM_PMReq().length()/*numberOfBytesToWrite*/);
//			if(iretStatus !=0L)
//			{
//				CloseFile((int)namedPipeHandle/*file*/);
//				namedPipeHandle = -1;
//				return false;
//			}
//		}
//		catch (Throwable e)
//		{
//			try
//			{
//				CloseFile((int)namedPipeHandle/*file*/);
//				namedPipeHandle = -1;
//			} catch (Throwable e1)
//			{
//				e1.printStackTrace();
//			}
//			e.printStackTrace();
//			return false;
//		}
//
//		return true;
//	}
	
	@Override
	public int updateHeatbeatModel(String afguid, VirtualMachineInfo vmInfo){
		checkSession();
		if(vmInfo!=null){
			HeartBeatModelManager.updateVMInfo(afguid, vmInfo);
		}
		return 0;
	}

	public String getCACloudStorageKey(ArchiveCloudDestInfo cloudDestInfo, String userName, String password) {
		checkSession();
		String key=null;
		try {
			key = SubscriptionService.getInstance().getCACloudStorageKey(cloudDestInfo,userName,password);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return key;
	}

	@Override
	public CatalogItem[] getFileList(String mountVolGUID, String catPath, String parentPath) {
		checkSession();
		Lock lock = null;
		try {
			if(catPath != null)
			{
				String volumeGUID = catPath.substring(catPath.length()-BrowserService.VOL_GUID_START_OFFSET, catPath.length());
				return BrowserService.getInstance().getFileItems(mountVolGUID, volumeGUID, parentPath);
			}else {
				return null;
			}
			
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally {
			if(lock != null) {
				lock.unlock();
			}
		}
	}
	
	@Override
	@Deprecated
	/**
	 * This API is deprecated, and to call it may not get corrrect result, please don't use
	 */
	public JMountPoint MountVolume(String userName, String passWord, String catPath)
	{
		checkSession();
		JMountPoint mountPoint;
		com.ca.arcflash.webservice.jni.model.JMountPoint mntPoint = null;
		try {
			mntPoint =  BrowserService.getInstance().MountVolume(userName, passWord, catPath, null);
			if(mntPoint != null)
			{
				mountPoint = new JMountPoint();
				mountPoint.setDiskSignature(mntPoint.getDiskSignature());
				mountPoint.setMountID(mntPoint.getMountID());
				mountPoint.setMountHandle(mntPoint.getMountHandle());
				return mountPoint;
			}
			else
			{
				return null;
			}
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		
	}
	
	@Override
	public int UnMountVolume(String mountID, String diskSignature, long mountHandle)
	{
		checkSession();
		com.ca.arcflash.webservice.jni.model.JMountPoint mountPoint = new com.ca.arcflash.webservice.jni.model.JMountPoint();
		mountPoint.setDiskSignature(diskSignature);
		mountPoint.setMountID(mountID);
		mountPoint.setMountHandle(mountHandle);
		try {
			return BrowserService.getInstance().unmountVolume(mountPoint);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		
	}

	@Override
	//zxh,add ForceDismountAllResBrsVols interface impl
	/*
	 * this api is used to force Dismount All ResBrsVols.
	 * */
	public int ForceDismountAllResBrsVols() {
		checkSession();
		long iReVal = -1;
		try {
			iReVal = BrowserService.getInstance().getNativeFacade().VDDismountResBrsVols(true);	
		} catch (Exception e) {
			logger.error("VDDismountResBrsVols---failed--- " + e.getMessage());
		}
		logger.info("VDDismountResBrsVols---reVal is:" + iReVal);
		
		return (int) iReVal;
	}
	
	@Override
	public long generateCatalogOnDemandEx(CatalogJobPara para) {
		logger.info("generateCatalogOnDemandEx is called");
		long ret = 0;
		checkSession();
		try {
			para.setCurrentAgent(ServiceContext.getInstance().getLocalMachineName());
			para.setCurrentAgentSID(this.getMachineSId());
			para.setCurrentAgentUUID(CommonService.getInstance().getNodeUUID());
			
			long flag=CatalogService.getInstance().submitFSOnDemandJob(para);
			logger.info("CatalogService.getInstance().submitFSOnDemandJob return value: "+flag);
			if(flag==0){//successful
				ret=1;
//				Thread.sleep(5000);
//				Map<String,Map<Long,JobMonitor>> jobMonitorMap = CommonService.getInstance().getJobMonitorMap();
//				if(jobMonitorMap != null){
//					List<JobMonitor> jobMonitorList = new ArrayList<JobMonitor>();
//					for(Map<Long,JobMonitor> jobMonitors : jobMonitorMap.values()){
//						jobMonitorList.addAll(jobMonitors.values());
//					}
//					
//					JobMonitor[] jbs = jobMonitorList.toArray(new JobMonitor[0]);
//					for(int i = 0; i < jbs.length; i++)
//					{
//						//We return 0 if catalog generation fails and 1 if we are not able to retrive the catalog job ID. This is the case
//						//when the catalog job has not been scheduled yet or has already completed.
//						//If we find a active catalog job we return jobID+1;
//						if(jbs[i].getJobType() == BaseJob.JOBTYPE_CATALOG_FS_ONDEMAND)
//						{
//							ret = jbs[i].getId();
//							ret = ret + 1;
//							logger.info("generateCatalogOnDemandEx return jobID: "+ret);
//							break;
//						}
//					}
//				
//				}
			}
		}catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
//		} catch (Throwable e) {
//			logger.error(e.getMessage(), e);
//			throw AxisFault.fromAxisFault("Unhandled exception in web service",
//					FlashServiceErrorCode.Common_ErrorOccursInService);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("generateCatalogOnDemandEx return value: "+ret);
		return ret;
	}
	
	@Override
	public long generateVMCatalogOnDemand(CatalogJobPara para, String vmInstanceUUID) {
		logger.info("generateVMCatalogOnDemand is called! vmInstanceUUID: "+vmInstanceUUID);
		long ret=0;
		checkSession();
		try {
			long flag = VSPhereCatalogService.getInstance().submitFSOnDemandJob(para, vmInstanceUUID);
			logger.info("VSPhereCatalogService.getInstance().submitFSOnDemandJob return value: "+flag);
			if(flag==0){//successful
				ret=1;
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("generateVMCatalogOnDemand return value: "+ret);
		return ret;
	}
	
	@Override
	public boolean checkifJobActive(long jobID)
	{
		boolean ret = false;
		Map<String,Map<Long,JobMonitor>> jobMonitorMap = JobMonitorService.getInstance().getJobMonitorMap();
		if(jobMonitorMap == null){
			return false;
		}

		List<JobMonitor> jobMonitorList = new ArrayList<JobMonitor>();
		for(Map<Long,JobMonitor> jobMonitors : jobMonitorMap.values()){
			jobMonitorList.addAll(jobMonitors.values());
		}
		JobMonitor[] jbs = jobMonitorList.toArray(new JobMonitor[0]);
		for(int i = 0; i < jbs.length; i++)
		{
			//We return 0 if catalog generation fails and 1 if we are not able to retrive the catalog job ID. This is the case
			//when the catalog job has not been scheduled yet or has already completed.
			//If we find a active catalog job we return jobID+1;
			if(jbs[i].getJobId() == jobID-1 && ( jbs[i].getJobType() == Constants.JOBTYPE_CATALOG_FS_ONDEMAND || jbs[i].getJobType() == Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND) && jbs[i].getJobStatus() == JobMonitorConstants.JOBSTATUS_ACTIVE)
			{
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	@Override
	public int queryCatalogStatus(long sessNum, String backupDest,
			String userName, String passWord) {
		logger.info("call queryCatalogStatus. backupDest: "+backupDest+" sessNum: "+sessNum);
		int ret = 0;
		try {
			ret = BrowserService.getInstance().queryCatalogStatus(sessNum, backupDest, userName, passWord);
			if(ret==2){//if catalog job is pending, sleep 10 seconds to avoid frequent query.
				Thread.sleep(10000);
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("queryCatalogStatus return: "+ret);
		return ret;
	}
	
	@Override
	public SearchContext openSearchCatalogEx(
			RecoveryPoint[] sessionItemsList, long sessionItemsCount,
			String sessionPath, String domain, String username, String password, String searchPath, boolean caseSensitive,
			boolean includeSubDir, String pattern, String[] encryptedHashKey, String[] encryptedPwd) {
		// pidma02: New method added to search
		checkSession();
		Lock lock = null;
		try {
			return BrowserService.getInstance().openSearchCatalogEx(sessionItemsList, sessionItemsCount, 
					sessionPath, domain, username, password, searchPath, caseSensitive, includeSubDir, pattern,
					encryptedHashKey, encryptedPwd);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally {
			if(lock != null) {
				lock.unlock();
			}
		}
	}//end openSearchCatalogEx

	
	@Override
	public long updateSessionPassword(String backupDest, String domain, String userName, String destPwd, 
			String[] uuids, String[] passwords, String[] passwordHash) {
		checkSession();
		try {
			return CommonService.getInstance().updateSessionPasswordsByGUID(backupDest, domain, userName, destPwd,
					uuids, passwords, passwordHash);
		}catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	//[manpi01] Submitting delete job after archiving 
	@Override
	public long submitArchiveDeleteJob(ArchiveJobArg job) {
		checkSession();
		try {
			//if(job.getArchiveNodes()[0]!=null)
			//job.getArchiveNodes()[0].setPDeleteVolumeAppList(volumes);			
			long lRet = ((DeleteArchiveService) DeleteArchiveService.getInstance()).submitArchiveSourceDeleteJob(job);
			return lRet;
		} catch (ServiceException e) {
			logger.info("Exception occurred in submitting archive delete job" + e.getErrorCode() + "\tmessage: "+ e.getMessage());
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.info("exception ocured. Throwing error");
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	/*
	@Override
	public long canBackup(ArchiveCloudDestInfo cloudDestInfo) {
		checkSession();
		try {
			return SubscriptionService.getInstance().canBackup(cloudDestInfo);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public String updateStorageKey(ArchiveCloudDestInfo cloudDestInfo) {
		checkSession();
		String key=null;
		try {
			key = SubscriptionService.getInstance().updateStorageKey(cloudDestInfo);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return key;
	}*/
	
/*	@Override
	public long consumeCACloudLicense(ArchiveCloudDestInfo cloudDestInfo) {
		checkSession();
		try {
			return SubscriptionService.getInstance().consumeCACloudLicense(cloudDestInfo);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public String getGeminarePortalURL(ArchiveCloudDestInfo cloudDestInfo) {
		checkSession();
		String url=null;
		try {
			url = SubscriptionService.getInstance().getGeminarePortalURL(cloudDestInfo);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return url;
	}*/
	@Override
	public SearchContext openSearchCatalogWithCredentials(String dest, String domain,
			String userName, String password, String sessionPath,
			String searchPath, boolean caseSensitive, boolean includeSubDir,
			String pattern) {
		checkSession();
		
		try {
			SearchContext context =  BrowserService.getInstance().openSearchCatalog(dest, domain, userName, password, 
					sessionPath, searchPath, caseSensitive, includeSubDir, pattern);
			RemoteFolderConnCache.cachePathToSession(getSession(),dest, domain, userName, password);
			return context;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Deprecated
	@Override
	public SearchContext openSearchCatalog(String sessionPath, String sDir,
			boolean bCaseSensitive, boolean bIncludeSubDir, String pattern)
			{
		checkSession();
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(sessionPath);
			//if lock != null, this path is a remote path
			if(lock != null) {
				lock.lock();
				RemoteFolderConnCache.reEstalishConnetion(getSession());
			}
			return BrowserService.getInstance().openSearchCatalog(sessionPath,
					sDir, bCaseSensitive, bIncludeSubDir, pattern);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally {
			if(lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	public int closeSearchCatalog(SearchContext sContext) {
		checkSession();
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(RemoteFolderConnCache.getCachedPath(getSession()));
			//if lock != null, this path is a remote path
			if(lock != null) {
				lock.lock();
				RemoteFolderConnCache.reEstalishConnetion(getSession());
			}

			BrowserService.getInstance().closeSearchCatalog(sContext);
			return 0;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally {
			if(lock != null)
				lock.unlock();
		}
	}

	@Override
	public SearchResult searchNext(SearchContext sContext) {
		checkSession();

		SearchResult ret = null;
		HashMap<String, CatalogItem> cache = new HashMap<String, CatalogItem>();
		try {
			long begin = System.currentTimeMillis();
			long elapseTime = 0;
			int limit = 100;

			List<CatalogItem> list = new ArrayList<CatalogItem>();
			boolean isExceedLimit = false;
			while (elapseTime < 30) {
				SearchResult result = null;
				Lock lock = null;
				try {
					lock = RemoteFolderConnCache.getInstance().getLockByPath(RemoteFolderConnCache.getCachedPath(getSession()));
					//if lock != null, this path is a remote path
					if(lock != null) {
						lock.lock();
						//RemoteFolderConnCache.reEstalishConnetion(getSession());
					}

					result = BrowserService.getInstance().searchNext(
						sContext);
				}
				finally {
					if(lock != null)
						lock.unlock();
				}
				if (result == null)
					break;

				if (ret == null) {
					ret = result;
				} else {
					ret.setCurrent(result.getCurrent());
				}

				if (result.getDetail() != null && result.getDetail().length > 0) {
					for (int i = 0; i < result.getDetail().length; i++) {
						CatalogItem item = result.getDetail()[i];
						if (item != null) {
							String key = item.getPath() + "\\" + item.getName()
									+ "@" + item.getDate() + "@"
									+ item.getType() + "@" + item.getSize();

							if (!cache.containsKey(key)) {
								cache.put(key, item);
								list.add(item);

								if (list.size() >= limit) {
									isExceedLimit = true;
									break;
								}
							}
						}
					}
				}

				if (isExceedLimit || !result.hasNext()) {
					break;
				}

				elapseTime = (System.currentTimeMillis() - begin) / 1000;
			}
			if (ret != null) {
				ret.setDetail(list.toArray(new CatalogItem[0]));
				ret.setFound(list.size());
			}

		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}

		return ret;
	}

	@Override
	public long backup(int type, String name) {
		checkSession();
		try {
			return BackupService.getInstance().backup(type, name);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
		
	
	public long backupManually(int type, String name,int period, VirtualMachine vm){
		checkSession();
		try {
			if(vm!=null)
			return VSphereService.getInstance().backupVM(type, name, vm, true,"",period);
			else return BackupService.getInstance().backup(type, name,true,period);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	

	@Override
	public int addTrustedHost(TrustedHost host) {
		checkSession();
		try {
			CommonService.getInstance().addTrustedHost(host);
			return 0;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public TrustedHost[] getTrustedHosts() {
		checkSession();
		try {
			return CommonService.getInstance().getTrustedHosts();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int removeTrustedHost(TrustedHost host) {
		checkSession();
		try {
			CommonService.getInstance().removeTrustedHost(host);
			return 0;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public RecoveryPoint[] getRecoveryPoints(String destination, String domain,
			String userName, String pwd, Date beginDate, Date endDate)
			{
		checkSession();
		try {
			RecoveryPoint[] recoveryPoints = RestoreService.getInstance().getRecoveryPoints(destination,
					domain, userName, pwd, beginDate, endDate, true);
			//the cached connection information will be used when calling method
			//getCatalogItems(String catalogFilePath, long parentID). That method relies on the connection
			//established in this method.
			RemoteFolderConnCache.cachePathToSession(getSession(),destination, domain, userName, pwd);
			return recoveryPoints;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int submitRestoreJob(RestoreJob job) {
		checkSession();
		try {
			if(job.getJobLauncher() == 1){
				VSphereService.getInstance().submitVSphereRestoreJob(job);
			}else{
				RestoreService.getInstance().submitRestoreJob(job);
			}
			return 0;
		} catch (ServiceException e) {
			if(job.getJobLauncher() == 1){
				ConvertErrorCodeUtil.submitRecoveryVMJobConvert(e);
			}
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public BackupInformationSummary getBackupInformationSummary()
			{
		checkSession();
		try {
			return BackupService.getInstance().getBackupInformationSummary();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	@Override
	public BackupInformationSummary getBackupInformationSummaryWithLicInfo()
			{
		checkSession();
		try {
			return BackupService.getInstance().getBackupInformationSummaryWithLicInfo();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public DestinationCapacity getDestSizeInformation(BackupConfiguration configuration)
			{
		checkSession();
		try {

			return BackupService.getInstance().getDestSizeInformationForArchiveMode(configuration);

		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}


	@Override
	public ProtectionInformation[] getProtectionInformation() {
		checkSession();
		try {
			return BackupService.getInstance().getProtectionInformation();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public RecoveryPointSummary getRecoveryPointSummary() {
		checkSession();
		try {
			return BackupService.getInstance().getRecoveryPointSummary();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ProtectionInformation[] updateProtectionInformation() {
		checkSession();
		try {
			return BackupService.getInstance().updateProtectionInformation();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ProtectionInformation[] updateVMProtectionInformation(VirtualMachine vm) {
		checkSession();
		try {
			return VSphereService.getInstance().updateProtectionInformation(vm);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public RecoveryPoint[] getMostRecentRecoveryPoints(int backupType, int backupStatus,int top)
			{
		checkSession();
		try {
			return BackupService.getInstance().getMostRecentRecoveryPoints(backupType, backupStatus,top);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public RecoveryPoint[] getRecentBackupsByServerTime(int backupType, int backupStatus, String serverBeginDate, String serverEndDate, boolean needCatalogStatus)
			{
		checkSession();
		try {
			Date beginD = string2Date(serverBeginDate);
			Date endD = string2Date(serverEndDate);
			return BackupService.getInstance().getRecentBackupsByServerTime(backupType, backupStatus, beginD, endD);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int deleteActivityLogs(Date date) {
		checkSession();
		try {
			CommonService.getInstance().deleteActivityLogs(date);
			return 0;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ActivityLogResult getActivityLogs(int start, int count)
			{
		checkSession();
		try {
			return CommonService.getInstance().getActivityLogs(start, count);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ActivityLogResult getJobActivityLogs(long jobNo, int start, int count)
			{
		checkSession();
		try {
			return CommonService.getInstance().getJobActivityLogs(jobNo, start, count);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public NextScheduleEvent getNextScheduleEvent() {
		checkSession();
		try {
			return BackupService.getInstance().getNextScheduleEvent();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	protected SOAPFaultException convertServiceException2AxisFault(ServiceException serviceException) {
		if(serviceException.getWebServiceCause() != null){
			return serviceException.getWebServiceCause();
		}
		return getCommonUtil().convertServiceException2AxisFault(serviceException);
	}

	private SOAPFaultException convertHaException2SOAPFaultException(HAException haException){
		return AxisFault.fromAxisFault(haException.getMessage(), haException.getCode());
	}

	@Override
	public RemoteDeployTargetDetail[] getRemoteDeployTargets(
			String[] serverNames) {
		checkSession();
		try {
			return RemoteDeployService.getInstance().getRemoteDeployTargets(
					serverNames);

		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int setRemoteDeployTargets(RemoteDeployTarget[] remoteDeployTargets)
			{
		checkSession();
		try {
			RemoteDeployService.getInstance().setRemoteDeployTargets(
					remoteDeployTargets);

		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return 0;
	}

	@Override
	public int startRemoteDeploy(String localDomain, String localUser,
			String localPassword, String[] serverNames) {
		checkSession();
		try {
			RemoteDeployService.getInstance().startRemoteDeploy(localDomain,
					localUser, localPassword, serverNames);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return 0;
	}

	@Override
	public boolean isBackupCompressionLevelChanged() {
		checkSession();
		try {
			return BackupService.getInstance()
					.isBackupCompressionLevelChanged();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean isBackupEncryptionAlgorithmAndKeyChanged(){
		checkSession();
		try {
			return BackupService.getInstance().isBackupEncryptionAlgorithmAndKeyChanged();
		} catch(Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean isBackupEncryptionAlgorithmAndKeyChangedWithParams(
			int encryptionAlgorithm, String encryptionKey){
		checkSession();
		try {
			return BackupService.getInstance().isBackupEncryptionAlgorithmAndKeyChanged(encryptionAlgorithm, encryptionKey);
		} catch(Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean isBackupCompressionLevelChangedWithLevel(int compressionLevel) {
		checkSession();
		try {
			return BackupService.getInstance()
					.isBackupCompressionLevelChanged(compressionLevel);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	
	@Override
	@Deprecated
	public JobMonitor getJobMonitor() {
		checkSession();
		try {
			return null;// JobMonitorService.getInstance().getJobMonitor();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public JobMonitor getNewJobMonitor(String jobType,Long jobId) {
		checkSession();
		try {
			return JobMonitorService.getInstance().getJobMonitor(jobType,jobId);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public JobMonitor[] getJobMonitorMap() {
		checkSession();
		return JobMonitorService.getInstance().getAllJobMonitors();
	}

	@Override
	public TrustedHost getLocalHostAsTrust(){
		checkSession();
		try {
			return CommonService.getInstance().getLocalHostAsTrust();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public DeployUpgradeInfo validRemoteDeploy(String localDomain, String localUser,
			String localPassword, RemoteDeployTarget remoteTarget)
			{
		checkSession();
		try {
			return RemoteDeployService.getInstance().validRemoteDeploy(localDomain,
					localUser, localPassword, remoteTarget);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int submitCopyJob(CopyJob job) {
		checkSession();
		try {
			RestoreService.getInstance().submitCopyJob(job);
			return 0;
		} catch (ServiceException e) {
			//wanqi06
			ConvertErrorCodeUtil.submitCopyJobConvert(e);
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int cancelJob(long jobID) {
		checkSession();
		try {
			CommonService.getInstance().cancelJob(jobID);
			return 0;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	public Date getServerTime() {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTimeInMillis(System.currentTimeMillis());
		return cal.getTime();
	}
	public Calendar getServerCalendar() {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTimeInMillis(System.currentTimeMillis());
		return cal;
	}

	public long validateDestForMode(String path, String domain, String user, String pwd,int mode)
			{
		checkSession();
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(path);
			if(lock != null) {
				lock.lock();
				RemoteFolderConnCache.getInstance().disconnectAllToMachine(RemoteFolderConnCache.getMachineName(path));
			}
			 // if(!(mode == 2))	public static final int COPY_MODE = 2; //FIX Bug 749838
				RemoteFolderConnCache.cachePathToSession(getSession(),path, domain, user, pwd);//
			
			long validateDest = BrowserService.getInstance().validateDest(path, domain,user, pwd,mode);
			return validateDest;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally {
			if(lock != null)
				lock.unlock();
		}
	}
	
	public long validateDest(String path, String domain, String user, String pwd)
	{
		checkSession();
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(path);
			if(lock != null) {
				lock.lock();
				RemoteFolderConnCache.getInstance().disconnectAllToMachine(RemoteFolderConnCache.getMachineName(path));
			}
			RemoteFolderConnCache.cachePathToSession(getSession(),path, domain, user, pwd);
			long validateDest = BrowserService.getInstance().validateDest(path, domain,user, pwd);
			return validateDest;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally {
			if(lock != null)
				lock.unlock();
		}
}
	
	   

	@Override
	public long validateSource(String path, String domain, String user,
			String pwd) {
		checkSession();
		return validateSourceGenFolder(path, domain, user, pwd, true);
	}

	@Override
	public long validateSourceGenFolder(String path, String domain,
			String user, String pwd, boolean isNeedCreateFolder) {
		checkSession();
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(path);
			if(lock != null) {
				lock.lock();
				RemoteFolderConnCache.getInstance().disconnectAllToMachine(RemoteFolderConnCache.getMachineName(path));
			}
			RemoteFolderConnCache.cachePathToSession(getSession(),path, domain, user, pwd);
			if(!isNeedCreateFolder) {
				boolean validatePath = CommonService.getInstance().validateDirPath(path, domain, user, pwd);
				if(!validatePath) { // dir path doesn't exist.
					return 0;
				}
			}
			long validateSource = BrowserService.getInstance().validateSource(path, domain,
					user, pwd);
			return validateSource;
		} catch (ServiceException e) {
			if (FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG
					.equalsIgnoreCase(e.getErrorCode())) {
				e.setErrorCode(FlashServiceErrorCode.RestoreJob_SourceInvalid);
			}
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}finally {
			if(lock != null)
				lock.unlock();
		}
	}

	@Override
	public long checkDestinationValid(String path) {
		checkSession();
		try{
			return BackupService.getInstance().checkDestinationValid(path);
		}
		catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long getPathMaxLength() {
		checkSession();
		try{
			return BackupService.getInstance().getPathMaxLength();
		}
		catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	public void saveAdminAccount(Account adminAccount){
	   	throw AxisFault.fromAxisFault("Unsupported Method",
				FlashServiceErrorCode.Common_ErrorOccursInService);

//		checkSession();
//		try {
//			BackupService.getInstance().saveAdminAccount(adminAccount);
//		} catch (ServiceException e) {
//			throw convertServiceException2AxisFault(e);
//		} catch (Throwable e) {
//			logger.error(e.getMessage(), e);
//			throw AxisFault.fromAxisFault("Unhandled exception in web service",
//					FlashServiceErrorCode.Common_ErrorOccursInService);
//		}
	}

    public Account getAdminAccount()  {
    	checkSession();
    	try {
			return BackupService.getInstance().getAdminAccount();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

    public void validateAdminAccount(Account adminAccount)  {
    	throw AxisFault.fromAxisFault("Unsupported Method",
				FlashServiceErrorCode.Common_ErrorOccursInService);
//     	checkSession();
//    	try {
//			BackupService.getInstance().validateAdminAccount(adminAccount);
//		} catch (ServiceException e) {
//			throw convertServiceException2AxisFault(e);
//		} catch (Throwable e) {
//			logger.error(e.getMessage(), e);
//			throw AxisFault.fromAxisFault("Unhandled exception in web service",
//					FlashServiceErrorCode.Common_ErrorOccursInService);
//		}
	}

    public long getDestDriveType(String path) {
		checkSession();
		try {
			return BackupService.getInstance().getDestDriveType(path);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
    
    public long getDestDriveTypeForMode(String path,int mode) {
		checkSession();
		try {
			return BackupService.getInstance().getDestDriveType(path,mode);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public Boolean checkRemotePathAccess(String path, String domain,
			String user, String pwd) {
			checkSession();
			try {
				return BrowserService.getInstance().checkRemotePathAccess(path, domain, user, pwd);
			} catch (ServiceException e) {
				throw convertServiceException2AxisFault(e);
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
				throw AxisFault.fromAxisFault("Unhandled exception in web service",
						FlashServiceErrorCode.Common_ErrorOccursInService);
			}
	}

	@Override
	public long disconnectRemotePath(String path, String domain, String user,
			String pwd, boolean force) {
		checkSession();
		try {
			return BrowserService.getInstance().disconnectRemotePath(path, domain, user, pwd, force);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public PagedCatalogItem getPagedCatalogItems(String catPath, long parentID,
			int start, int size) {
		checkSession();
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(catPath);
			//if lock != null, this path is a remote path
			if(lock != null) {
				lock.lock();
				RemoteFolderConnCache.reEstalishConnetion(getSession());
			}
			return BrowserService.getInstance().getPagedCatalogItems(catPath,
					parentID, start, size);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally {
			if(lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	public RecoveryPoint[] getRecoveryPointsByServerTime(String destination,
			String domain, String userName, String pwd, String serverBeginDate,
			String serverEndDate, boolean isQueryDetail) {
		checkSession();
		logger.debug("getRecoveryPointsByServerTime begin");
		logger.debug("dest:" + destination);
		logger.debug("domain:" + domain);
		logger.debug("userName:" + userName);
		logger.debug("pwd:***");
		logger.debug("serverBeginDate:" + serverBeginDate);
		logger.debug("serverEndDate:" + serverEndDate);
		logger.debug("isQueryDetail:" + isQueryDetail);
		try {
			Date beginD = string2Date(serverBeginDate);
			Date endD = string2Date(serverEndDate);
			logger.debug("beginD:" + beginD);
			logger.debug("endD:" + endD);
			RecoveryPoint[] rps = RestoreService.getInstance()
					.getRecoveryPoints(destination, domain, userName, pwd,
							beginD, endD, isQueryDetail);
			RemoteFolderConnCache.cachePathToSession(getSession(),destination, domain, userName, pwd);
			
			logger.debug("getRecoveryPointsByServerTime end");

			return rps;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	protected Date string2Date(String source) throws ServiceException {			
		try {
			Calendar cal = Calendar.getInstance();
			StringTokenizer token = new StringTokenizer(source, "/-: ");
			int year = Integer.parseInt(token.nextToken());
			int month = Integer.parseInt(token.nextToken()) - 1;
			int date = Integer.parseInt(token.nextToken());
			int hourOfDay = Integer.parseInt(token.nextToken());
			int minute = Integer.parseInt(token.nextToken());
			int second = Integer.parseInt(token.nextToken());
			cal.set(year, month, date, hourOfDay, minute, second);
			return cal.getTime();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			//			throw new IllegalArgumentException("Wrong date time format: "
			//					+ source + ", " + e.getMessage());
			throw new ServiceException("",	FlashServiceErrorCode.Common_Invalid_Date);
		}	
	}

	@Override
	public AlternativePath[] checkSQLAlternateLocation(String[] basePath, String[] instName,
			String[] dbName) {
		checkSession();
		try {
			return RestoreService.getInstance().checkSQLAlternateLocation(basePath, instName, dbName);
		}
		catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean checkBLILic() {
		logger.debug("checkBLILic - enter");
		checkSession();
		boolean hasLic = BackupService.getInstance().checkBLILic();
		logger.debug("checkBLILic - exit:" + hasLic);
		return hasLic;
	}

	@Override
	public boolean checkBaseLicence() {
		checkSession();
		try{
			logger.debug("checkBaseLicence - enter");
			boolean hasLic = CommonService.getInstance().checkBaseLicense();
			logger.debug("checkBaseLicence - exit:" + hasLic);
			return hasLic;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ApplicationWriter[] getExcludedAppComponents(
			String[] volumes) {
		logger.debug("getExcludedAppComponents begin");
		logger.debug("volumes:" + StringUtil.convertArray2String(volumes));
		checkSession();

		try {
			//if volumes == null, returns all applications in all volumes
			if(volumes == null)
				volumes = new String[0];

			List<String> volumeList = Arrays.asList(volumes);
			ApplicationWriter[] excludedAppList =  BackupService.getInstance().getExcludedAppComponents(volumeList);
			logger.debug("getExcludedAppComponents end");
			return excludedAppList;
		}
		catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public DestinationCapacity getDestCapacity(String destination,
			String domain, String userName, String pwd) {
		logger.debug("getDestCapacity(String, String, String) begin");
		logger.debug("destination:" + destination);
		logger.debug("domain:" + domain);
		logger.debug("userName:" + userName);
		checkSession();

		try {
			DestinationCapacity capacity = BackupService.getInstance().getDestCapacity(destination, domain, userName, pwd);
			logger.debug("getDestCapacity end");
			return capacity;
		}catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public String[] getLocalDestVolumes(String destPath) {
		logger.debug("getLocalDestVolumes(String) begin");
		checkSession();

		try {
			 return BackupService.getInstance().getLocalDestVolumes(destPath);
		}catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}


	@Override
	public RecoveryPointItem[] getRecoveryPointItems(String dest,String domain, String user, String pwd, String subPath) {
		checkSession();
		logger.debug("getRecoveryPointItems(String, String, String, String, String) - start");
		logger.debug("dest:" + dest);
		logger.debug("domain:" + domain);
		logger.debug("user:" + user);
		logger.debug("pwd:");
		logger.debug("subPath:" + subPath);
		try {
			RecoveryPointItem[] recItems = RestoreService.getInstance().getRecoveryPointItems(dest, domain, user, pwd, subPath);
			RemoteFolderConnCache.cachePathToSession(getSession(),dest, domain, user, pwd);
			logger.debug("getRecoveryPointItems end");
			return recItems;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch(SOAPFaultException se) {
			throw se;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public NetworkPath[] getMappedNetworkPath(String userName){
		checkSession();
		logger.debug("getMappedNetworkPath(String) - start");
		logger.debug("userName:" + userName);
		try {

		NetworkPath[] pathArr = CommonService.getInstance().getMappedNetworkPath(userName);

		logger.debug("getMappedNetworkPath(String) - end");
		return pathArr;

		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean validateSessionPasswordByHash(String password, long pwdLen, @NotPrintAttribute String hashValue,
			long hashLen) {
		checkSession();
		if(logger.isDebugEnabled()) {
			logger.debug("validateSessionPasswordByHash(String, long, String, long) - start");
		}

		try {
			boolean isValid = CommonService.getInstance()
					.validateSessionPasswordByHash(password, password.length() /*pwdLen*/, hashValue, hashValue.length()/*hashLen*/, getSession());
			if(logger.isDebugEnabled())
				logger.debug("validateSessionPasswordByHash(String, long, String, long) - end");
				logger.debug("isValid:" + isValid);

			return isValid;
		}catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	public boolean validateSessionPassword(String password, String destination,
			long sessionNum){
		checkSession();
		if(logger.isDebugEnabled()) {
			logger.debug("validateSessionPassword(String, String, long) - start");
			logger.debug("password:");
			logger.debug("destination:" + destination);
			logger.debug("sessionNum:" + sessionNum);
		}

		try {
			boolean isValid = CommonService.getInstance().validateSessionPassword(password, destination, sessionNum, getSession());
			if(logger.isDebugEnabled())
				logger.debug("validateSessionPassword(String, String, long) - end");
				logger.debug("isValid:" + isValid);

			return isValid;
		}catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}

	}

	@Override
	public JobMonitorHistoryItem[] getJobMonitorHistory(){
		checkSession();
		logger.debug("webservice: getJobMonitorHistory - start");
		try {
			JobMonitorHistoryItem[] result = CommonService.getInstance().queryJobMonitorHistoryItems(3);
			return result;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public String[] getSessionPasswordBySessionGuid(String[] sessionGuid){
		checkSession();
		if (logger.isDebugEnabled()) {
			logger.debug("webservice: getSessionPasswordBySessionGuid - start");
			logger.debug("guid: " + StringUtil.convertArray2String(sessionGuid));
		}
		try {
			String[] pwdStrings = CommonService.getInstance().getSessionPasswordBySessionGuid(sessionGuid);
			return  pwdStrings;

		}catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	/* Patch Manager */ 
	@Override
	public  PatchInfo getPatchInfo ()
	{
		checkSession();
		return PatchManager.getInstance().getPatchInfo();
	}
	
	//added by cliicy.luo
	/* Patch Manager */ 
	@Override
	public  BIPatchInfo getPMBIPatchInfo ()
	{
		checkSession();
		return PatchManager.getInstance().getPMBIPatchInfo();
	}
	//added by cliicy.luo
	
	
	@Override
	public AutoUpdateSettings testDownloadServerConnection(AutoUpdateSettings in_TestSettings){
		checkSession();
		logger.debug("testDownloadServerConnection - will start to call PatchManager testDownloadServerConnnections");
		return PatchManager.getInstance().testDownloadServerConnnections(in_TestSettings);
	}
	
	//added by cliicy.luo
	@Override
	public AutoUpdateSettings testBIDownloadServerConnection(AutoUpdateSettings in_TestSettings){
		checkSession();
		logger.debug("testBIDownloadServerConnection - will start to call PatchManager testBIDownloadServerConnnections");
		return PatchManager.getInstance().testBIDownloadServerConnnections(in_TestSettings);
	}
	//added by cliicy.luo
	
	@Override
	public boolean IsPatchManagerBusy()
	{
		checkSession();
		return PatchManager.getInstance().isPatchmangerBusy();
	}
	
	@Override
	public PMResponse checkUpdate( )
	{
		logger.debug("FlashFlash oooo ServiceImple checkUpdate");
		checkSession();
		return PatchManager.getInstance().checkUpdate();
	}
		
	
	@Override
	public int installUpdate( )
	{
		checkSession();
		return PatchManager.getInstance().installUpdate();
	}
	
	//added by cliicy.luo
	@Override
	public PMResponse checkBIUpdate( )
	{
		logger.debug("FlashFlash oooo ServiceImple checkBIUpdate");
		checkSession();
		return PatchManager.getInstance().checkBIUpdate();
	}
	
	@Override
	public int installBIUpdate( )
	{
		checkSession();
		return PatchManager.getInstance().installBIUpdate();
	}
	//added by cliicy.luo
	
	@Deprecated	
	@Override
	public int PMInstallPatch(PatchInfo in_PatchInfo) throws IOException {		
		return installUpdate();
	}

	@Deprecated
	@Override
	public int PMInstallPatchFromTrayIcon(String DownloadLocation, int iReboot) throws IOException {
		return installUpdate();
	}
	
	@Deprecated
	@Override
	public PMResponse SubmitRequest(int RequestType)
	{
		return checkUpdate();
	}	
	
	@Deprecated
	@Override
	public  long AFRunPatchJob(String spatchURL)
	{		
		checkSession();
		return PatchManager.getInstance().installUpdate();
	}
		
	@Override
	public boolean IsPatchManagerRunning()
	{
		logger.debug("IsPatchManagerRunning() - start");
		checkSession();
		try
		{
			boolean bRet = BrowserService.getInstance().getNativeFacade().IsPatchManagerRunning("");
			return bRet;
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public int IsPatchManagerReady() {
		logger.debug("IsPatchManagerReady() - start");
		checkSession();
		int iPatchManagerStatus = 0;// Not Running;

		try {
			if (!BrowserService.getInstance().getNativeFacade().IsPatchManagerRunning("")) {
				iPatchManagerStatus = 0;// Patch Manager is not running
			} else if (BrowserService.getInstance().getNativeFacade().IsPatchManagerBusy("")) {
				iPatchManagerStatus = 2;// Patch Manager Is Busy with other
										// request;
			} else {
				iPatchManagerStatus = 1;// Patch Manager is ready
			}

		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return iPatchManagerStatus;
	}

	@Deprecated
	@Override
	public    long GetLastError()
	{
		logger.debug("GetLastError() - start");
		checkSession();

		try
		{
			return BrowserService.getInstance().getNativeFacade().GetLastError();
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
//			try {
				throw AxisFault.fromAxisFault("Unhandled exception in web service",
						FlashServiceErrorCode.Common_ErrorOccursInService);
//			} catch (AxisFault e1) {
//				e1.printStackTrace();
//			}
		}

	}
	
	@Deprecated
	@Override
	public    String FormatMessage(int errorCode)
	{
		logger.debug("FormatMessage() - start");
		try {
			checkSession();
			return BrowserService.getInstance().getNativeFacade().FormatMessage(errorCode);
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	/* Patch Manager End */
	
	@Override
	public boolean ValidateServerName(String in_serverName)
	{
		checkSession();
		try {
			return BackupService.getInstance().ValidateServerName(in_serverName);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public PreferencesConfiguration getPreferences() {
		checkSession();
		try {
			return CommonService.getInstance().getPreferences();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long savePreferences(PreferencesConfiguration in_ReferencesConfig)
			 {
		checkSession();
		try{
			return CommonService.getInstance().savePreferences(in_ReferencesConfig);
		}catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable t){
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long validatePreferences(PreferencesConfiguration in_ReferencesConfig)
			 {
		checkSession();
		try{
			return CommonService.getInstance().validatePreferences(in_ReferencesConfig);
		}catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable t){
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	public boolean PMSendMail()
	{
		logger.debug("PMSendMail() - start");
		try {
			checkSession();
			return SendMail();
//		} catch (AxisFault e1) {
//			e1.printStackTrace();
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
//		return false;
	}
	public boolean PMSendMailForFailures(String strErrorMessage)
	{
		logger.debug("PMSendMailForFailures() - start");
		try {
			checkSession();
			return SendMailOnCheckUpdateOrDwnldFailure(strErrorMessage);
//		} catch (AxisFault e1) {
//			e1.printStackTrace();
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
//		return false;
	}
	private boolean SendMail()
	{
		PatchInfo objPatchInfo = new PatchInfo();
		try
		{
			objPatchInfo= PatchManager.getInstance().getPatchInfo();
			PreferencesConfiguration preferencesConfig = CommonService.getInstance().getPreferences();

			BackupEmail emailSetting =  preferencesConfig.getEmailAlerts(); 
			boolean bIsEmailReq= emailSetting.isNotifyOnNewUpdates() && emailSetting.isEnableSettings();
			/*BackupConfiguration configuration;
			configuration = BackupService.getInstance().getBackupConfiguration();
			boolean bIsEmailReq= configuration.getbcSelfUpdateSettings().isNotifyOnNewUpdates();*/
			if((objPatchInfo.getError_Status()== PatchInfo.ERROR_GET_PATCH_INFO_SUCCESS) && (bIsEmailReq == true))
			{
				////////Email on New Update Available//////////
				BackupEmail email = preferencesConfig.getEmailAlerts();
				int iServerType = preferencesConfig.getupdateSettings().getServerType();

				/*BackupEmail email = configuration.getEmail();
				int iServerType = configuration.getbcSelfUpdateSettings().getServerType();*/
				String serverName = "CA Server";
//				if(iServerType == 0)
//				{
//					serverName = "CA Server";
//				}
//				else
//				{
//					/*serverName = configuration.getbcSelfUpdateSettings().getStagingServer();*/
//					//serverName = preferencesConfig.getupdateSettings().getStagingServers()[0].getStagingServer();//update this with active staging server - kappr01
//					WindowsRegistry registry = new WindowsRegistry();
//					int handle = registry.openKey(CommonService.REGISTRY_INSTALLPATH);
//					String homeFolder = registry.getValue(handle, CommonService.REGISTRY_KEY_PATH);
//					registry.closeKey(handle);
//					String strD2DPMSettingsINIPath = "";
//					if (!StringUtil.isEmptyOrNull(homeFolder))
//					{
//						strD2DPMSettingsINIPath = homeFolder + "Update Manager\\D2DPMSettings.INI";
//					}
//					INIFile objINI = null;
//					objINI = new INIFile(strD2DPMSettingsINIPath);
//					if(objINI.isbIsFileExists())
//					{
//						//DownloadServer
//						serverName = objINI.getStringProperty("DownloadServer", "ServerName");
//					}
//					if(serverName == null)
//					{
//						serverName = "";
//					}
//				}
				EmailSender emailSender = new EmailSender();
				// Subject//
				String hostName = ServiceContext.getInstance().getLocalMachineName();
				//String productName = WebServiceMessages.getResource("ProductNameD2D");
				String configuredEmailSubject = email.getSubject(); 
				String emailSubject = WebServiceMessages.getResource("PM_New_Update_Avbl_Email_Sub",configuredEmailSubject,hostName,serverName);
				emailSender.setSubject(emailSubject);
				if (email.isEnableHTMLFormat())
				{
					emailSender.setContent(EmailContentTemplate.getPMHtmlContent(objPatchInfo.getPackageID(),objPatchInfo.getPatchURL(),objPatchInfo.getPublishedDate(),objPatchInfo.getDescription()));
				}
				else
				{
					emailSender.setContent(EmailContentTemplate.getPMPlainTextContent(objPatchInfo.getPackageID(),objPatchInfo.getPublishedDate(),objPatchInfo.getDescription()));
				}
				emailSender.setUseSsl(email.isEnableSsl());
				emailSender.setSmptPort(email.getSmtpPort());
				emailSender.setMailPassword(email.getMailPassword());
				emailSender.setMailUser(email.getMailUser());
				emailSender.setUseTls(email.isEnableTls());
				emailSender.setProxyAuth(email.isProxyAuth());
				emailSender.setMailAuth(email.isMailAuth());

				emailSender.setFromAddress(email.getFromAddress());
				emailSender.setRecipients(email.getRecipientsAsArray());
				emailSender.setSMTP(email.getSmtp());
				emailSender.setEnableProxy(email.isEnableProxy());
				emailSender.setProxyAddress(email.getProxyAddress());
				emailSender.setProxyPort(email.getProxyPort());
				emailSender.setProxyUsername(email.getProxyUsername());
				emailSender.setProxyPassword(email.getProxyPassword());
				
				emailSender.setJobStatus( CommonEmailInformation.EVENT_TYPE.AGENT_RPS_UPDTE_AVAILABLE.getValue() );//event type;
				emailSender.setProductType( CommonEmailInformation.PRODUCT_TYPE.ARCFlash.getValue() );
				
				emailSender.sendEmail(email.isEnableHTMLFormat());
			}
		}
		 catch (ServiceException e) {
				e.printStackTrace();
			}
		catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	private boolean SendMailOnCheckUpdateOrDwnldFailure(String strErrorMessage)
	{
		try
		{
			PreferencesConfiguration preferencesConfig = CommonService.getInstance().getPreferences();
			BackupEmail emailSetting = preferencesConfig.getEmailAlerts(); 
			boolean bIsEmailReq= emailSetting.isNotifyOnNewUpdates() && emailSetting.isEnableSettings();
			if(bIsEmailReq == true)
			{
				////////Email on new update download fail//////////
				BackupEmail email = preferencesConfig.getEmailAlerts();
//				int iServerType = preferencesConfig.getupdateSettings().getServerType();

//				String serverName = "";
//				if(iServerType == 0)
//				{
//					serverName = "CA Technologies server";
//				}
//				else
//				{
//					serverName = preferencesConfig.getupdateSettings().getStagingServers()[0].getStagingServer();//update this with active staging server - kappr01
//				}
				EmailSender emailSender = new EmailSender();

				// Host name for subject//
//				String hostName = ServiceContext.getInstance().getLocalMachineName();
				String configuredEmailSubject = email.getSubject();
				//String productName = WebServiceMessages.getResource("ProductNameD2D");
				String emailSubject = WebServiceMessages.getResource("PM_New_Update_DwnldFailed_Email_Sub",configuredEmailSubject);
				emailSender.setSubject(emailSubject);
				if (email.isEnableHTMLFormat())
				{
					emailSender.setContent(EmailContentTemplate.getPMFailureHtmlContent(WebServiceMessages.getResource("PM_CheckForUpdates"),WebServiceMessages.getResource("PM_Failed"),strErrorMessage));
				}
				else
				{
					emailSender.setContent(EmailContentTemplate.getPMFailurePlainTextContent(WebServiceMessages.getResource("PM_CheckForUpdates"),WebServiceMessages.getResource("PM_Failed"),strErrorMessage));
				}
				emailSender.setUseSsl(email.isEnableSsl());
				emailSender.setSmptPort(email.getSmtpPort());
				emailSender.setMailPassword(email.getMailPassword());
				emailSender.setMailUser(email.getMailUser());
				emailSender.setUseTls(email.isEnableTls());
				emailSender.setProxyAuth(email.isProxyAuth());
				emailSender.setMailAuth(email.isMailAuth());

				emailSender.setFromAddress(email.getFromAddress());
				emailSender.setRecipients(email.getRecipientsAsArray());
				emailSender.setSMTP(email.getSmtp());
				emailSender.setEnableProxy(email.isEnableProxy());
				emailSender.setProxyAddress(email.getProxyAddress());
				emailSender.setProxyPort(email.getProxyPort());
				emailSender.setProxyUsername(email.getProxyUsername());
				emailSender.setProxyPassword(email.getProxyPassword());
				emailSender.setJobStatus( CommonEmailInformation.EVENT_TYPE.AGENT_RPS_UPDTE_AVAILABLE.getValue() );//event type;
				emailSender.setProductType( CommonEmailInformation.PRODUCT_TYPE.ARCFlash.getValue() );
				
				emailSender.sendEmail(email.isEnableHTMLFormat());
			}
		}
		 catch (ServiceException e) {
				e.printStackTrace();
			}
		catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/*
	 * This is for Edge SRM
	 * */
	@Override
	public String GetSrmInfo(int command) {
		checkSession();
		return SrmAgentServerImpl.GetSrmInfo(command);
	}

	@Override
	public int SaveAlertSetting(SRMPkiAlertSetting setting) {
		checkSession();
		return SrmAgentServerImpl.SaveAlertSetting(setting);
	}

	@Override
	public String D2DRegister4Edge(String uuid, String policyUuid, ApplicationType appType, String edgeHostName, String edgeWSDL, String edgeLocale, boolean forceRegFlag, String regHostName) {
		checkSession();

		D2DRegServiceImpl regImpl = new D2DRegServiceImpl();

		String result = regImpl.D2DRegister4Edge(uuid, policyUuid, appType, edgeHostName, edgeWSDL, edgeLocale, forceRegFlag, regHostName);
		return result;
	}
	
	
	
	private static String composeServiceURL(String hostName, String protocol, int port, String wsdl){
		StringBuilder sb = new StringBuilder(protocol);
		if(!protocol.endsWith(":"))
			sb.append(":");
		sb.append("//");
		sb.append(hostName);
		sb.append(":");
		sb.append(port);
		sb.append(wsdl);
		return sb.toString();
	}	
	
	private static final String wsdl4ASBU = "/WebServiceImpl/services/ASBUServiceImpl?wsdl";
	
	@Override
	public int registryASBU(ASBUHost asbuHost){
		String hostName = asbuHost.getHostName();
		int port = asbuHost.getPort();
		String protocol = asbuHost.isHttpProtocol()?"Http:":"Https:";
		try{			
			FlashListenerInfo listener = new FlashListenerInfo();
			listener.setWsdlURL(composeServiceURL(hostName, 
					protocol, port, wsdl4ASBU));
			
			listener.setType(FlashListenerInfo.ListenerType.ASBU);					
			ListenerManager.getInstance().addFlashListener(listener);
			return 0;
		}catch (Exception e){			
			logger.info("Failed to register ASBU lisenter:" + e.getMessage());
		}
		return 0;
	}
	
	
	@Override
	public List<Long> getArchiveSessions(int startSessionNo, int scheduleType){	
		checkSession();
		try {
			return ArchiveToTapeService.getInstance().getArchiveSesssions(startSessionNo,scheduleType);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}	
	}
	
	@Override
	public List<Long> getVSphereArchiveSessions(String vmInstanceUUID, int startSessionNo, int scheduleType){	
		checkSession();
		try {
			return VSphereArchiveToTapeService.getInstance().getArchiveSesssions(vmInstanceUUID, startSessionNo,scheduleType);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}	
	}

	@Override
	public int D2DUnRegister4Edge(String uuid, ApplicationType appType, String edgeHostName, boolean forceUnRegFlag) {
		D2DRegServiceImpl regImpl = new D2DRegServiceImpl();

		checkSession();

		int result = regImpl.D2DUnRegister4Edge(uuid, appType, edgeHostName, forceUnRegFlag);
		return result;
	}

	@Override
	public int DeplyD2DPolicy(BackupConfiguration configuration) {
		checkSession();
		D2DPolicyManagementServiceImpl poMag = new D2DPolicyManagementServiceImpl();
		return poMag.DeplyD2DPolicy(configuration);

	}

	@Override
	public int RemoveD2DPolicy() {
		checkSession();
		D2DPolicyManagementServiceImpl poMag = new D2DPolicyManagementServiceImpl();
		return poMag.RemoveD2DPolicy();

	}

	@Override
	public long backupVM(int type, String name,VirtualMachine vm)  {
		checkSession();
		try {
			return VSphereService.getInstance().backupVM(type, name,vm);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public NextScheduleEvent getVMNextScheduleEvent(VirtualMachine vm)  {
		checkSession();
		try {
			return VSphereService.getInstance().getNextScheduleEvent(vm);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int cancelVMJob(long jobID,String vmIdentification) {
		checkSession();
		try {
			VSphereService.getInstance().cancelVMJob(jobID,vmIdentification);
			return 0;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int cancelWaitingJob(String vmInstanceUuid) {
		checkSession();
		try {
			VSphereService.getInstance().cancelWaitingJob(vmInstanceUuid);
			return 0;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public int validateVC(VirtualCenter vCenter) {
		checkSession();
		int ret = 0;
		try {
			ret =  VSphereService.getInstance().validateVC(vCenter);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return ret;
	}

	@Override
	public int addVirtualCenter(VirtualCenter vc) {
		checkSession();
		try {
			VSphereService.getInstance().addVirtualCenter(vc);
			return 0;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public VirtualCenter[] getVirtualCenters() {
		checkSession();
		try {
			return VSphereService.getInstance().getVirtualCenters();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int removeVirtualCenter(VirtualCenter vc) {
		checkSession();
		try {
			VSphereService.getInstance().removeVirtualCenter(vc);
			return 0;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public VSphereBackupConfiguration getVSphereBackupConfiguration(
			VirtualCenter vc) {
		checkSession();
		VSphereBackupConfiguration config = null;
		try {
			config = VSphereService.getInstance().getVSphereBackupConfiguration(vc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return config;
	}

	// Feb Sprint
	@Override
	public void saveStorageAppliance(List<StorageAppliance> storageApplianceList)
			 {
		checkSession();
		
		try {
			VSphereService.getInstance().saveStorageApplianceConfiguration(storageApplianceList);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public SavePolicyWarning[] saveVSphereBackupConfiguration(
			VSphereBackupConfiguration vsphereBackupConfiguration)
			 {
		checkSession();
		//long ret = 0;
		try {
			return VSphereService.getInstance().saveVSphereBackupConfiguration(vsphereBackupConfiguration);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	@Override
	public long detachVSpherePolicy(VirtualMachine[] vmArray){
		checkSession();
		long ret = 0;
		try {
			ret = VSphereService.getInstance().detachVSpherePolicy(vmArray);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return ret;
	}

	@Override
	public VMItem[] getAllVM(VirtualCenter vc) {
		checkSession();
		try {
			return VSphereService.getInstance().getAllVM(vc);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public VMBackupConfiguration getVMBackupConfiguration(VirtualMachine vm)
			 {
		checkSession();
		try {
			return VSphereService.getInstance().getVMBackupConfiguration(vm);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

        @Override
	public VMBackupConfiguration getVMBackupConfigurationByInstanceUUID(String vmInstanceUUID)
			 {
		checkSession();
		try {
			VirtualMachine vm = new VirtualMachine();
	        vm.setVmInstanceUUID(vmInstanceUUID);
	        return VSphereService.getInstance().getVMBackupConfiguration(vm);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public BackupInformationSummary getVMBackupInformationSummary(
			VirtualMachine vm) {
		checkSession();
		try {
			return VSphereService.getInstance().getBackupInformationSummary(vm);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public BackupInformationSummary getVMBackupInformationSummaryWithLicInfo(
			VirtualMachine vm) {
		checkSession();
		try {
			return VSphereService.getInstance().getBackupInformationSummaryWithLicInfo(vm);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public VMItem[] getConfiguredVM(){
		checkSession();
		try{
			return VSphereService.getInstance().getConfiguredVM();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean isVSphereProxy(){
		checkSession();
		return VSphereService.getInstance().isVSphereProxy();
	}

	@Override
	public JobMonitor getVMJobMonitor(VirtualMachine vm,String jobType,Long jobId) {
		checkSession();
		try {
			if(vm == null){
				return JobMonitorService.getInstance().getVMJobMonitor(jobType,jobId); 
			}
			return JobMonitorService.getInstance().getVMJobMonitorByJobTypeAndJobId(vm.getVmInstanceUUID(),jobType,jobId);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public JobMonitor[] getVMJobMonitorMap(VirtualMachine vm) {
		checkSession();
		try{
			Map<String,Map<Long,JobMonitor>> jobMonitorTypeMap = JobMonitorService.getInstance().getVMJobMonitorMap(vm.getVmInstanceUUID());
			if(jobMonitorTypeMap == null){
				return null;
			}else{
				List<JobMonitor> jobMonitorList = new ArrayList<JobMonitor>();
				for(Map<Long,JobMonitor> jobMonitorMap : jobMonitorTypeMap.values()){
					for(JobMonitor jm : jobMonitorMap.values()){
						if(JobMonitorService.getInstance().isValidJobMonitor(jm)){
							jobMonitorList.add(jm);
						}
					}
				}
				return jobMonitorList.toArray(new JobMonitor[0]);
			}

		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ProtectionInformation[] getVMProtectionInformation(VirtualMachine vm)
			 {
		checkSession();
		try {
			return VSphereService.getInstance().getProtectionInformation(vm);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public RecoveryPoint[] getVMMostRecentRecoveryPoints(int backupType,
			int backupStatus, int top, VirtualMachine vm) {
		checkSession();
		try {
			return VSphereService.getInstance().getMostRecentRecoveryPoints(backupType, backupStatus,top,vm);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean isVMBackupCompressionLevelChanged(VirtualMachine vm)
			 {
		checkSession();
		try {
			return VSphereService.getInstance()
					.isVMBackupCompressionLevelChanged(vm);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public String[] getESXServerDataStore(VirtualCenter vc, ESXServer esxServer){
		checkSession();
		try {
			return VSphereService.getInstance().getESXServerDataStore(vc, esxServer);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	@Override
	public DataStore[] getVMwareDataStore(VirtualCenter vc, ESXServer esxServer){
		checkSession();
		try {
			return VSphereService.getInstance().getVMwareDataStore(vc, esxServer);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public BackupVM[] getBackupVMList(String destination, String domain,String username,
			String password) {
		checkSession();
		try {
			return VSphereService.getInstance().getBackupVMList(destination,domain, username, password);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	@Override
	public BackupVM getBackupVM(String destination, String domain,String username,
			String password) {
		checkSession();
		try {
			return VSphereService.getInstance().getBackupVM(destination,domain, username, password);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public VCloudVirtualDC getVAppVDCFromSession(String vAppDestination, int vAppSessionNumer, String fullUsername, String password) {
		checkSession();
		try {
			return VSphereService.getInstance().getVAppVDCFromSession(vAppDestination, vAppSessionNumer, fullUsername, password);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public List<VAppChildBackupVMRestorePointWrapper> getVAppChildBackupVMsAndRecoveryPoints(String vAppDestination,
			int vAppSessionNumer, String domain, String username, String password) {
		checkSession();
		try {
			return VSphereService.getInstance().getVAppChildBackupVMsAndRecoveryPoints(vAppDestination,
					vAppSessionNumer, domain, username, password);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean checkVMDestination(String destination, String domain,
			String username, String password) {
		checkSession();
		try{
			return VSphereService.getInstance().checkVMDestination(destination, domain, username, password);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public Disk[] getBackupVMDisk(String destination, String subPath,
			String domain, String username, String password) {
		checkSession();
		try{
			return VSphereService.getInstance().getBackupVMDisk(destination, subPath, domain, username, password);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int submitRecoveryVMJob(RestoreJob job) {
		checkSession();
		try {
			int count = 1;
			if(job.getChildVMRestoreJobList()!=null) {
				count += job.getChildVMRestoreJobList().size();
			}
			NativeFacade facade = BackupService.getInstance().getNativeFacade();
			long jobID = facade.getJobIDs(count);
			if(jobID <= 0) {
				logger.error("submitRecoveryVMJob getJobID failed.");
				throw AxisFault.fromAxisFault("Unhandled exception in web service",
						FlashServiceErrorCode.Common_ErrorOccursInService);
			}
			job.setJobId(jobID);
			if(job.getChildVMRestoreJobList() != null) {
				int index = 1;
				for(RestoreJob childJob : job.getChildVMRestoreJobList()) {
					childJob.setJobId(jobID + index);
					index++;
					childJob.setMasterJobId(jobID);
					childJob.setJobType(RestoreJobType.Recover_VM);
				}
			}
			VSphereService.getInstance().submitRecoveryVMJob(job);
			if(job.getChildVMRestoreJobList() != null) {
				for(RestoreJob childJob : job.getChildVMRestoreJobList()) {
					VSphereService.getInstance().submitRecoveryVMJob(childJob);
				}
			}
			return 0;
		} catch (ServiceException e) {
			//wanqi06
			ConvertErrorCodeUtil.submitRecoveryVMJobConvert(e);
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int submitVMCopyJob(CopyJob job) {
		checkSession();
		try {
			VSphereService.getInstance().submitVMCopyJob(job);
			return 0;
		} catch (ServiceException e) {
			//wanqi06
			ConvertErrorCodeUtil.submitVMCopyJobConvert(e);
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ESXServer[] getESXServers(VirtualCenter vCenter){
		checkSession();
		try {
			return VSphereService.getInstance().getESXServers(vCenter);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public VirtualMachine[] getVirtualMachines(VirtualCenter vc,ESXServer esxServer){
		checkSession();
		try {
			return VSphereService.getInstance().getVirtualMachine(vc, esxServer);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int deleteVMActivityLogs(Date date, VirtualMachine vm) {
		checkSession();
		try {
			VSphereService.getInstance().deleteVMActivityLogs(date, vm);
			return 0;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ActivityLogResult getVMActivityLogs(int start, int count,
			VirtualMachine vm) {
		checkSession();
		try {
			return VSphereService.getInstance().getVMActivityLogs(start, count, vm);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ActivityLogResult getVMJobActivityLogs(long jobNo, int start,
			int count, VirtualMachine vm) {
		checkSession();
		try {
			return VSphereService.getInstance().getVMJobActivityLogs(jobNo, start, count, vm);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public String getFailoverJobScript(String afGuid) {
		checkSession();
		FailoverJobScript failoverJobScript = HAService.getInstance()
				.getFailoverJobScript(afGuid);
		try {
			return CommonUtil.marshal(failoverJobScript);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public String getJobScriptCombo(String afGuid) {
		checkSession();
		return HAService.getInstance().getJobScriptCombo(afGuid);
	}

	@Override
	public int removeReplicatedInfo(String afguid, VirtualizationType virtualizationType, String converterID){
		checkSession();
		return HAService.getInstance().removeReplicatedInfo(afguid, virtualizationType, converterID);
	}
	@Override
	public int setJobScriptCombo(String jobScriptComboStr) {
		checkSession();

		try {
			long basecur = System.currentTimeMillis();
			JobScriptCombo jobScript = CommonUtil.unmarshal(jobScriptComboStr, JobScriptCombo.class);
			long cur2 = System.currentTimeMillis();
			long cur3 = 0;
			if(jobScript == null) throw AxisFault.fromAxisFault("null jobscript combo");

			VCMPolicyDeployParameters deployPolicyParameters = new VCMPolicyDeployParameters();
			HAService.getInstance().applyVCMJobPolicy(jobScript, deployPolicyParameters);
			cur3 =  System.currentTimeMillis();
			if(logger.isDebugEnabled())
				logger.debug("setJobScriptCombo unmarshal time:"+(cur2 - basecur)  + " store time:"+ (cur3-cur2));

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault(e.getMessage());
		}

		return 0;
	}
//	@Override
//	public int setFailoverJobScript(String failoverJobScript)   {
//		try {
//			FailoverJobScript failoverObj = CommonUtil.unmarshal(failoverJobScript, FailoverJobScript.class);
//			failoverObj.setProductionServerName(InetAddress.getLocalHost().getHostName());
//			failoverObj.setProductionServerPort(CommonUtil.getProductionServerPort());
//			HAService.getInstance().setFailoverJobScript(failoverObj);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return 0;
//	}

	@Override
	public String getHeartBeatJobScript(String afGuid) {
		HeartBeatJobScript heartBeatJobScript = HAService.getInstance().getHeartBeatJobScript(afGuid);
		try {
			return CommonUtil.marshal(heartBeatJobScript);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;

	}

//	@Override
//	public int setHeartBeatJobScript(String heartBeatJobScript)   {
//		try {
//			HeartBeatJobScript script = CommonUtil.unmarshal(heartBeatJobScript, HeartBeatJobScript.class);
//			HAService.getInstance().setHeartBeatJobScript(script);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return 0;
//	}
	@Override
	public int startHeartBeat(String afGuid)    {
		checkSession();
		try {
			String localAFGuid = HAService.getInstance().evaluteAFGuid(afGuid);
			HAService.getInstance().startHeartBeat(localAFGuid);
		} catch (SOAPFaultException e) {
			logger.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public int pauseHeartBeatThis(String afGuid){
		checkSession();
        logger.info("pauseHeartBeatThis start");
		try {
			return HAService.getInstance().pauseHeartBeat(afGuid, true);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public int pauseHeartBeatForVcmUpgrade(String afGuid){
		checkSession();
		logger.info("pauseHeartBeatForVcmUpgrade start");
		try {
			return HAService.getInstance().pauseHeartBeatForVcmUpgrade(afGuid);
		} catch (SOAPFaultException e) {
			logger.error("pauseHeartBeatForVcmUpgrade:SOAPFaultException e");
			throw e;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public int pauseHeartBeatForD2D(String afGuid){
		checkSession();

		return HAService.getInstance().pauseHeartBeatForD2D(afGuid);

	}
	
	@Override
	public int syncVCMStatus2Monitor(String afGuid) {
		checkSession();

		VCMStatusSyncer.getInstance().syncVCMStatus2Edge(afGuid);
		return 0;

	}

	@Override
	public int resumeHeartBeatThis(String afGuid){
		checkSession();

		try {
			return HAService.getInstance().resumeHeartBeat(afGuid);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public int resumeHeartBeatForD2D(String afGuid){
		checkSession();

		return HAService.getInstance().resumeHeartBeatForD2D(afGuid);

	}

	@Override
	public int stopHeartBeatThis(String afGuid, String converterID) {
		checkSession();

		return HAService.getInstance().stopHeartBeat(afGuid, converterID);

	}
//	@Override
//	public int isHeartBeatInState(String jobStatus, int register)
//			{
//		return HAService.getInstance().isHeartBeatInState(afGuid,jobStatus,register);
//
//	}
	@Override
	public Integer[] getStatesThis(String afGuid) {
		checkSession();
		return HAService.getInstance().getStatesThis(afGuid);
	}

	@Override
	public int startReplication(String afGuid) { //Resume VSB
		checkSession();
		
		try {
			HAService.getInstance().resumeVSBJob(afGuid);
		}catch(ServiceException se) {
			throw convertServiceException2AxisFault(se);
		}catch(Exception e){
			logger.error("failed to resume VSB job", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		
		return 0;
	}

	@Override
	public String getReplicationJobScript(String afGuid) {
		checkSession();
		ReplicationJobScript script = HAService.getInstance().getReplicationJobScript(afGuid);
		try {
			return CommonUtil.marshal(script);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

//	@Override
//	public int setReplicationJobScript(String replicationJobScript) {
//		try {
//			ReplicationJobScript script = CommonUtil.unmarshal(replicationJobScript, ReplicationJobScript.class);
//			HAService.getInstance().setReplicationJobScript(script);
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}
//		return 0;
//	}

	@Override
	public boolean isFailoverMode() {
		checkSession();
		return CommonUtil.isFailoverMode();
	}

	@Override
	public VWWareESXNode[] getESXNodeList(String esxServer, String username,
			String passwod, String protocol, int port) {
		checkSession();

		try{
			return HAService.getInstance().getESXNodeList(esxServer, username, passwod, protocol, port);
		}catch(Exception e){
			logger.error("Failed to getESXNodeList!!!");
			throw AxisFault.fromAxisFault("Failed to getESXNodeList!!!");
		}
	}
	@Override
	public String[] getESXHostDataStoreList(String esxServer, String username,
			String passwod, String protocol, int port, VWWareESXNode host)
	{
		checkSession();
		try{
			return HAService.getInstance().getESXHostDataStoreList(esxServer, username, passwod, protocol, port, host);
		}catch (Exception e) {
			logger.error("Failed to getESXHostDataStoreList!!!");
			throw AxisFault.fromAxisFault("Failed to getESXHostDataStoreList!!!");
		}
	}

	@Override
	public int validateMonitorUserCredential(String serverName,String proto, int port,
			String username, String password) {
		checkSession();

		try {
			HAService.getInstance().validateMonitorUserCredential(serverName, proto,port, username, password);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return 0;
	}



	//for monitor model service



	@Override
	public ProductionServerRoot[] getReplicaRootPaths() {
		checkSession();
		String xml = CommonUtil.D2DInstallPath+"Configuration\\repository.xml";
		RepositoryModel repository = com.ca.arcflash.common.modelmanager.RepositoryUtil.getInstance(xml).getRepository();
		return repository.getProductionServers().toArray(new ProductionServerRoot[0]);

	}

	@Override
	public String getFailoverJobScriptOfProductionServer(String afGuid) {
		checkSession();
//		if(log.isDebugEnabled())
//			log.debug("getFailoverJobScript begin: " + afGuid);
		String result = "";
		FailoverJobScript jobScript = HACommon.getFailoverJobScriptObject(afGuid);
		 if(jobScript!=null)
			try {
				result = CommonUtil.marshal(jobScript);
//				if(log.isDebugEnabled())
//					log.debug("getFailoverJobScript end");
				return result;
			} catch (JAXBException e) {
				throw AxisFault.fromAxisFault("Marshal failover job script for "+afGuid + " failed.",MonitorWebServiceErrorCode.Common_FailoverJobScript_JaxBError);

			}
		return result;
	}

	@Override
	public int startFailover(String afGuid, VMSnapshotsInfo vmSnapInfo) {
		checkSession();

		logger.info("start failover for " + afGuid);

		FailoverJob fj = HACommon.getFailoverJob(afGuid);
		 if(fj != null){
			fj.getJobStatus().setStatus(HAJobStatus.Status.Active);
			fj.setSessionGUID(vmSnapInfo.getSessionGuid());
			fj.getJobScript().setPowerOnWithIPSettings(vmSnapInfo.isPowerOnWithIPSettings());
			fj.getJobScript().setIpSettings(vmSnapInfo.getIpSettings());
			if (vmSnapInfo.getDnsParameters() != null) {				
				fj.getJobScript().setDnsParameters(vmSnapInfo.getDnsParameters());
			}
			try {
				fj.scheduleNow();
			} catch (HAException e) {
				throw AxisFault.fromAxisFault(e.getMessage(),e.getCode());
			}
			
			if(logger.isDebugEnabled())
				logger.debug("startFailover end");
			return 1;
		 }else {
			 logger.error("Failed to get failover job script for " + afGuid);
			 throw AxisFault.fromAxisFault("no failover job script for "+afGuid,MonitorWebServiceErrorCode.Common_FailoverJobScript_NonExist);
		 }

	}

	@Override
	public boolean isFailoverJobFinish(String afGuid) {
		checkSession();
		if(logger.isDebugEnabled())
			logger.debug("isFailoverJobFinish begin: " + afGuid +"->" + afGuid);

		FailoverJob job = HACommon.getFailoverJob(afGuid);
		 if(job != null && job.getJobStatus().getStatus() == HAJobStatus.Status.Active)
			 return false;

		 return true;
	}

	@Override
	public boolean isFailoverJobFinishOfProductServer(String afGuid) {
		checkSession();

		try {
			return HAService.getInstance().isFailoverJobFinishOfProductServer(afGuid);
		} catch (SOAPFaultException e) {
			logger.error("isFailoverJobFinishOfProductServer(String) - exception.", e); //$NON-NLS-1$
			throw e;
		}
	}

	@Override
	public int startFailoverFromFailoverJobScript(String afGuid,
			VMSnapshotsInfo vmSnapInfo, String failoverJobScript)
	{
		checkSession();
		
		logger.info("start failover for " + afGuid);

		FailoverJobScript jobScript = null;
		 try {
			jobScript = CommonUtil.unmarshal(failoverJobScript,FailoverJobScript.class);
		} catch (JAXBException e1) {
			throw AxisFault.fromAxisFault(e1.getMessage()+":"+afGuid,MonitorWebServiceErrorCode.Common_FailoverJobScript_NonExist);
		}

		 if(jobScript!=null){
				Virtualization virtualization = jobScript.getFailoverMechanism()
				.get(0);

				switch (virtualization.getVirtualizationType()) {
					case HyperV: {
						jobScript.setFailoverCommand(new HyperVFailoverCommand());
						break;
					}
					case VMwareESX: {
						jobScript.setFailoverCommand(new VMwareESXFailoverCommand());
						break;
					}
					case VMwareVirtualCenter: {
						jobScript
								.setFailoverCommand(new VMwareCenterServerFailoverCommand());
						break;
					}
				}
				AFJob job = JobFactory.create(jobScript);
				job.setJobID(UUID.randomUUID().toString());
				FailoverJob fj = (FailoverJob)job;
				fj.setSessionGUID(vmSnapInfo.getSessionGuid());
				try {
					fj.scheduleNow();
				} catch (HAException e) {
					throw AxisFault.fromAxisFault(e.getMessage(),e.getCode());
				}
				if(logger.isDebugEnabled())
					logger.debug("startFailover end");
				return 1;
		 }else
			 throw AxisFault.fromAxisFault("no failover job script for "+afGuid,MonitorWebServiceErrorCode.Common_FailoverJobScript_NonExist);

	}

	@Override
	public VMSnapshotsInfo[] getVMSnapshots(String afGuid,String vmGuid,String vmName) {
		checkSession();
		return HAService.getInstance().getVMSnapshots(afGuid, vmGuid, vmName);
	}


	@Override
	public VMwareServer getVMwareServerType(String host,String username,String password,String protocol,int port){
		checkSession();
		try {
			VMwareServer server = HAService.getInstance().getVMwareServerType(host, username, password,protocol,port);
			return server;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Failed to get VMwareServer Type.");
		}
	}

	@Override
	public String getESXServerVersion(String host,String username,String password,String protocol,int port){
		checkSession();
		try {
			String version = HAService.getInstance().getESXServerVersion(host, username, password,protocol,port);
			return version;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Failed to get esxserver version.");
		}
	}

	@Override
	public VMSnapshotsInfo[] getSnapshotsForProductionServer(String afGuid) {
		checkSession();
		try {
			VMSnapshotsInfo[] snapShots = HAService.getInstance().getSnapshotsForProductionServer(afGuid);
			return snapShots;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int isFailoverJobScriptInState(String afguid, int paused)
	{
		checkSession();
		return HAService.getInstance().isFailoverJobScriptInState(afguid,paused);
	}

	@Override
	public String[] getVirtualNetworkList(String host, String username,
			String password, String protocol, boolean ignoreCertAuthentidation,
			long viPort, VWWareESXNode vNode) {
		checkSession();

		ESXNode eNode = new ESXNode();
		eNode.setDataCenter(vNode.getDataCenter());
		eNode.setEsxName(vNode.getEsxName());
		ArrayList<VirtualNetworkInfo> networkList = HAService.getInstance().getVirtualNetworkList(host, username, password, protocol, ignoreCertAuthentidation, viPort, eNode);
		String[] result = new String[networkList.size()];
		for (int i = 0; i < networkList.size(); i++) {
			result[i] = networkList.get(i).getVirtualName();
		}
		return result;
	}

	@Override
	public String[] getHypervNetworkAdapterTypes(){
		checkSession();

		return HAService.getInstance().getHypervAdapterTypes();
	}

	@Override
	public String[] getAdapterTypes(String host, String username,
			String password, String protocol, boolean ignoreCertAuthentidation,
			long viPort, VWWareESXNode vNode) {
		checkSession();

		ESXNode eNode = new ESXNode();
		eNode.setDataCenter(vNode.getDataCenter());
		eNode.setEsxName(vNode.getEsxName());

		return HAService.getInstance().getAdapterTypes(host, username, password, protocol,
													   ignoreCertAuthentidation, viPort, eNode);

	}

	@Override
	public ESXServerInfo getESXServerInfo(String host, String username, String password,
			String protocol, boolean ignoreCertAuthentidation, long viPort,
			VWWareESXNode vNode) {
		checkSession();

		ESXNode eNode = new ESXNode();
		eNode.setDataCenter(vNode.getDataCenter());
		eNode.setEsxName(vNode.getEsxName());

		Host_Info info = HAService.getInstance().getHostInfo(host, username, password, protocol,
															 true, viPort, eNode);

		ESXServerInfo serverInfo = new ESXServerInfo();
		serverInfo.setCpuCount(info.getCpuCount());
		serverInfo.setMemorySize(info.getMemorySize());
		serverInfo.setAvailableMemorySize(info.getAvailableMemorySize());

		return serverInfo;

	}

	@Override
	public ESXServerInfo getHypervInfo(String host, String user,String password){
		checkSession();

		long handle = 0;
		NativeFacade facade = BackupService.getInstance().getNativeFacade();
		try {

			handle = facade.OpenHypervHandle(host, user, password);
			
			JHyperVSystemInfo hyperVSysInfo = new JHyperVSystemInfo();
			facade.GetHyperVSystemInfo(handle, hyperVSysInfo);
			
			ESXServerInfo info = new ESXServerInfo();
			info.setCpuCount(hyperVSysInfo.getCpuCount());
			info.setMemorySize(hyperVSysInfo.getTotalPhysicalMemory());
			info.setAvailableMemorySize(hyperVSysInfo.getAvailablePhysicalMemory());
			
			return info;
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

	@Override
	public ARCFlashNodesSummary getARCFlashNodesSummary() {
		checkSession();

		logger.debug("getARCFlashNodesSummary() - start");
		ARCFlashNodesSummary summary = HAService.getInstance().getARCFlashNodesSummary();
		logger.debug("getARCFlashNodesSummary() - end");
		return summary;

	}

	@Override
	public int enableAutoOfflieCopy(String afGuid,boolean b) {
		checkSession();
		
		try {
			return HAService.getInstance().enableAutoOfflieCopy(afGuid,b);
		}catch(ServiceException se) {
			throw convertServiceException2AxisFault(se);
		}catch(Exception e){
			logger.error("failed to set the flag of pause/resume for VSB job script", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public String[] getHypervNetworks(String afGuid,String host, String username,
			String password){
		checkSession();
		return HAService.getInstance().getHypervNetworksFromMonitor(afGuid,host, username, password);

	}

	@Override
	public String[] getHypervNetworksFromMonitor(String host, String username,
			String password){
		checkSession();

		long handle = 0;
		try
		{
			handle = HyperVJNI.OpenHypervHandle(host, username, password);
			Map<String, String> networks = HyperVJNI.GetVirutalNetworkList(handle);
			Collection<String> values = networks.values();
			String[] results = values.toArray(new String[0]);
			return results;

		}catch (HyperVException e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault(e.getMessage(),FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally{
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (HyperVException e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}

	@Override
	public List<String> browseHyperVHostFolder(String server, String userName,	String password, String parentFolder) {
		checkSession();
		long handle = 0;
		try
		{
			handle = getNativeFacade().OpenHypervHandle(server, userName, password);
			List<String> folders = WSJNI.browseHyperVHostFolder(handle, parentFolder);
			return folders;

		} catch (ServiceException e) {
			if(e.getErrorCode().equals("3")) {
				e.setErrorCode(FlashServiceErrorCode.Browser_HyperVHostPathNotFound);
			} else {
				e.setErrorCode(FlashServiceErrorCode.Browser_HyperVHostGetFolderFailed);
			}
			throw convertServiceException2AxisFault(e);
		} catch (Throwable t) {
			logger.error(t.getMessage());
			throw AxisFault.fromAxisFault(t.getMessage(),FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally{
			try {
				getNativeFacade().CloseHypervHandle(handle);
			} catch (Exception e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}
	
	@Override
	public List<HyperVHostStorage> getHyperVHostStorage(String server, String userName, String password) {
		long handle = 0;
		try
		{
			handle = getNativeFacade().OpenHypervHandle(server, userName, password);
			return WSJNI.getHyperVHostStorage(handle);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable t) {
			logger.error(t.getMessage());
			throw AxisFault.fromAxisFault(t.getMessage(),FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally{
			try {
				getNativeFacade().CloseHypervHandle(handle);
			} catch (Exception e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}
	
	@Override
	public boolean createHyperVHostFolder(String server, String userName,
			String password, String path, String folder) {
		checkSession();
		long handle = 0;
		try	{
			handle = getNativeFacade().OpenHypervHandle(server, userName, password);
			boolean ret = WSJNI.createHyperVHostFolder(handle, path, folder);
			return ret;

		} catch (ServiceException e) {
			logger.error(e.getMessage());
			if(e.getErrorCode().equals("3")) {
				e.setErrorCode(FlashServiceErrorCode.Browser_HyperVHostPathNotFound);
			} else if(e.getErrorCode().equals("183")) {
				e.setErrorCode(FlashServiceErrorCode.Browser_CreateHyperVHostFolderFailed_AlreadyExist);
			} else {
				e.setErrorCode(FlashServiceErrorCode.Browser_CreateHyperVHostFolderFailed);
			}
			throw convertServiceException2AxisFault(e);
		} catch (Throwable t) {
			logger.error(t.getMessage());
			throw AxisFault.fromAxisFault(t.getMessage(),FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally{
			try {
				getNativeFacade().CloseHypervHandle(handle);
			} catch (Exception e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}
	
	
	public int heartBeat(String uuid, double perc, int toRep, boolean isFailOverVM) {
		logger.debug("heartBeat() - start");
		checkSession();
		int ret = HAService.getInstance().heartBeat(uuid, perc, toRep, isFailOverVM);
		logger.debug("heartBeat() - end");
		return ret;
	}
	@Override
	public int pauseHeartBeat(String uuid) {
		checkSession();
		
		FailoverJob failoverJob = (FailoverJob) HAService.getInstance().getFailoverJob(uuid);
		if (failoverJob == null) {
			logger.error("Fail to find failover job, uuid = " + uuid);
			return 1;
		}
		failoverJob.unschedule();
		failoverJob.getJobScript().setState(FailoverJobScript.PAUSED);
		boolean reStoreJob = JobQueueFactory.getDefaultJobQueue().reStoreJob(failoverJob);
		String msg = WebServiceMessages.getResource(
				HAService.COLDSTANDBY_SETTING_PAUSEHEARTBEAT_MONITOR,failoverJob.getJobScript().getProductionServerName());

		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, Constants.AFRES_AFJWBS_GENERAL,
				new String[] { msg, "", "", "", "" }, uuid);
		
		if(reStoreJob)  
			logger.info("pauseHeartBeat: failover job paused and stored successfully for " +uuid);
		else if(reStoreJob)  
			logger.info("pauseHeartBeat: failover job paused but failed to be stored for " +uuid);

		HeartBeatModelManager.setHeartBeatState(uuid,HeartBeatJobScript.STATE_PAUSED);
		return 0;
	}

	@Override
	public int resumeHeartBeat(String uuid) {
		checkSession();
		
		FailoverJob failoverJob = (FailoverJob) HAService.getInstance().getFailoverJob(uuid);
		if (failoverJob == null) {
			logger.error("Fail to find failover job, uuid = " + uuid);
			return 1;
		}
		failoverJob.unschedule();
		failoverJob.getJobScript().setState(FailoverJobScript.REGISTERED);
		boolean reStoreJob = JobQueueFactory.getDefaultJobQueue().reStoreJob(failoverJob);
		String msg = WebServiceMessages.getResource(
				HAService.COLDSTANDBY_SETTING_RESUMEHEARTBEAT_MONITOR,failoverJob.getJobScript().getProductionServerName());

		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, -1, Constants.AFRES_AFJWBS_GENERAL,
				new String[] { msg, "", "", "", "" }, uuid);
		
		if(reStoreJob)  
			logger.debug("resumeHeartBeat: failover job resumed and stored successfully for " +uuid);
		else if(reStoreJob)  
			logger.debug("resumeHeartBeat: failover job resumed but failed to be stored for " +uuid);

		HeartBeatModelManager.setHeartBeatState(uuid,HeartBeatJobScript.STATE_REGISTERED);
		return 0;
	}

	@Override
	public int deregisterForHA(String afGuid, String converterID) {
		checkSession();
		try {
			return HAService.getInstance().deregisterForHA(afGuid, converterID);
		}
		 catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault(e.getMessage(),
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public boolean isX86() {
		checkSession();
		return HAService.getInstance().isX86();
	}

	@Override
	public int registerForHA(String failoverJobScript,boolean isHeartBeat) {
		checkSession();
		if (logger.isDebugEnabled())
			logger.debug("registerForHA start");
		FailoverJobScript script = null;
		try {
			script = (FailoverJobScript) CommonUtil.unmarshal(failoverJobScript, FailoverJobScript.class);
		} catch (JAXBException e) {
			throw  AxisFault.fromAxisFault(e.getMessage(), e);
		}
		
		Virtualization virtualization = null;
		try {
			virtualization = script.getFailoverMechanism().get(0);
		} catch (Exception e) {
			logger.error(e);
			throw AxisFault.fromAxisFault(e.getMessage());
		}

		switch (virtualization.getVirtualizationType())
		{
			case HyperV:
				{
					script.setFailoverCommand(new HyperVFailoverCommand());

					break;
				}
			case VMwareESX:
				{
					script.setFailoverCommand(new VMwareESXFailoverCommand());
					break;
				}
			case VMwareVirtualCenter:
				{
					script.setFailoverCommand(new VMwareCenterServerFailoverCommand());
					break;
				}
		}

		HAService.getInstance().getRegisterForHA(script);
		
		if (logger.isDebugEnabled())
			logger.debug("registerForHA end");
		return 0;
	}

	@Override
	public int reportProductionServerRoot(String productionServerRoot)
	{
		checkSession();
		if (logger.isDebugEnabled()){
			logger.debug("reportProductionServerRoot start " +productionServerRoot);
		}
		ProductionServerRoot psr = null;
		try {
			psr = (ProductionServerRoot) CommonUtil.unmarshal(productionServerRoot, ProductionServerRoot.class);
		} catch (JAXBException e) {
			throw  AxisFault.fromAxisFault(e.getMessage(), e);
		}
		if(psr!=null)
			try {

				ReplicaRoot repRoot = psr.getReplicaRoot();
				if(repRoot instanceof TransServerReplicaRoot){
					String xml = CommonUtil.D2DHAInstallPath+"Configuration\\repository.xml";
					{
						ProductionServerRoot preRoot = null;
						try {
							preRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(psr.getProductionServerAFUID());
						} catch (HAException e) {
						}
						if (preRoot != null) {
							ReplicaRoot root = preRoot.getReplicaRoot();
							String firstRepDest = null;
							if (root != null && root instanceof TransServerReplicaRoot) {
								firstRepDest = ((TransServerReplicaRoot) root).getFirstReplicDest();
							}

							if (firstRepDest != null) {
								((TransServerReplicaRoot) repRoot).setFirstReplicDest(firstRepDest);
							}
						}
					}
					RepositoryUtil.getInstance(xml).saveProductionServerRoot(psr);
					String rootPath = RepositoryManager.getRootPath(psr, Protocol.HeartBeatMonitor);
					VirtualMachineInfo vminfo = HACommon.getSnapshotModeManager(rootPath, psr.getProductionServerAFUID()).getInternalVMInfo(psr.getProductionServerAFUID());
					HeartBeatModelManager.updateVMInfo(psr.getProductionServerAFUID(), vminfo);
				}else if(repRoot instanceof VMWareESXHostReplicaRoot){
					String xml = CommonUtil.D2DHAInstallPath+"Configuration\\repository.xml";
					RepositoryUtil.getInstance(xml).saveProductionServerRoot(psr);
					VirtualMachineInfo vmInfo = new VirtualMachineInfo(1,((VMWareESXHostReplicaRoot) repRoot).getVmuuid(),psr.getProductionServerAFUID());
					HeartBeatModelManager.updateVMInfo(psr.getProductionServerAFUID(), vmInfo);
				}else if(repRoot instanceof VMWareVirtualCenterReplicaRoot){
					String xml = CommonUtil.D2DHAInstallPath+"Configuration\\repository.xml";
					RepositoryUtil.getInstance(xml).saveProductionServerRoot(psr);
					VirtualMachineInfo vmInfo = new VirtualMachineInfo(1,((VMWareVirtualCenterReplicaRoot) repRoot).getVmuuid(),psr.getProductionServerAFUID());
					HeartBeatModelManager.updateVMInfo(psr.getProductionServerAFUID(), vmInfo);
				}

			} catch (HAException e) {
				logger.error(e.getMessage(),e);
				throw  AxisFault.fromAxisFault(e.getMessage(),e.getCode());
			} catch (Throwable e) {
				logger.error("failed to report production server root.", e);
				throw AxisFault.fromAxisFault("Unhandled exception in web service",
						FlashServiceErrorCode.Common_ErrorOccursInService);
			}
		if (logger.isDebugEnabled())
			logger.debug("reportProductionServerRoot end");
		return 0;
	}

	@Override
	public String[] getReplicatedSessionsFromServer(String lastRepDest,String afguid)
	 {
		checkSession();
		/**
		 * This function will make sure the VMSnapshot for HyperV be consistent the VM machine
		 */
		String[] EMPTY =  new String[0];
		List<String> re = new ArrayList<String>();
		//we don't use heartbeat model manager because it is used only by monitor UI
		//VirtualMachineInfo vmInfo = HeartBeatModelManager.getVMInfo(afguid);

		VMInfomationModelManager manager = HACommon.getSnapshotModeManager(lastRepDest, afguid);
		VirtualMachineInfo vmInfo = manager.getInternalVMInfo(afguid);
		VMSnapshotsInfo[] andPurgeVMSnapshotsHyperV = HACommon.getAndPurgeVMSnapshotsHyperV(lastRepDest,afguid,vmInfo);
		for(VMSnapshotsInfo t : andPurgeVMSnapshotsHyperV){
			String sessionGuids = t.getSessionGuid();
			String[] tokens = sessionGuids.split("[|]");
			re.addAll(Arrays.asList(tokens));
		}
		return re.toArray(EMPTY);
	}

	@Override
	public String createHyperVVM(String arcDesStr, String afGuid, int vmGeneration) 
	{
		checkSession();
		return HAService.getInstance().createHyperVVM(arcDesStr, afGuid, vmGeneration);
	}
	
	@Override
	public long destroyHyperVVM(String vmGuid) {
		checkSession();
		return HAService.getInstance().destroyHyperVVM(vmGuid);
	}
	
    @Override
	public boolean IsVmFileExist(String name)
    {
       checkSession();
       return HAService.getInstance().IsVmFileExist(name);
    }
	// Internal use. non-public
	@Override
	public long uploadProductionServerRoot(ProductionServerRoot serverRoot, String remotePath) {
		checkSession();
		return HAService.getInstance().uploadProductionServerRoot(serverRoot, remotePath);
	}
	
	// Internal use. non-public
	@Override
	public ProductionServerRoot downloadProductionServerRoot(String remotePath, String afguid) {
		checkSession();
		return HAService.getInstance().downloadProductionServerRoot(remotePath, afguid);
	}

	public NativeFacade getNativeFacade(){
		return BackupService.getInstance().getNativeFacade();
	}

	public VMStorage[] getVmStorages(String host, String username, String password,
			 String protocol, boolean ignoreCertAuthentidation, long viPort,
			 VWWareESXNode esxNode, String[] storageNames){
		CAVirtualInfrastructureManager vmwareManager = null;
		try {

			vmwareManager =  CAVMwareInfrastructureManagerFactory.
					getCAVMwareVirtualInfrastructureManager(host,username,password, protocol, true, viPort);
			ESXNode eNode = new ESXNode();
			eNode.setDataCenter(esxNode.getDataCenter());
			eNode.setEsxName(esxNode.getEsxName());
			VMwareStorage[] results = vmwareManager.getVMwareStorages(eNode, storageNames);
			List<VMStorage> vmRet = new ArrayList<VMStorage>();
			for(VMwareStorage storage : results){
				VMStorage tmp = new VMStorage();
				tmp.setName(storage.getName());
				tmp.setColdStandySize(storage.getColdStandySize());
				tmp.setFreeSize(storage.getFreeSize());
				tmp.setTotalSize(storage.getTotalSize());
				tmp.setOtherSize(storage.getOtherSize());
				vmRet.add(tmp);
			}

			return vmRet.toArray(new VMStorage[0]);

		} catch (Exception e) {

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

		return null;

	}

	//Get production server network adapter list
	@Override
	public NetworkAdapter[] getProdServerNetworkAdapters(){
		checkSession();
		try {
			NativeFacade facade = BackupService.getInstance().getNativeFacade();
			Vector<JHostNetworkConfig> networkConfigs=facade.GetHostNetworkConfig();

			NetworkAdapter[] adapterArray = new NetworkAdapter[networkConfigs.size()];
			for (int i=0;i<networkConfigs.size();i++) {
				NetworkAdapter adapter = new NetworkAdapter();
				JHostNetworkConfig jHostNetworkConfig=networkConfigs.get(i);

				adapter.setMACAddress(jHostNetworkConfig.getMacAddress());
				adapter.setAdapterName(jHostNetworkConfig.getNetworkAdapterName());
				adapter.setDynamicIP(jHostNetworkConfig.isDHCPEnabled());
				for (String ip : jHostNetworkConfig.getVecIP()) {
					adapter.getIP().add(ip);
				}
				for(String subnetMask : jHostNetworkConfig.getVecMask()){
					adapter.setSubnetMask(subnetMask);
					break;
				}
				for(String gateway: jHostNetworkConfig.getGateway()){
					adapter.setGateway(gateway);
					break;
				}

				adapter.setDynamicDNS(jHostNetworkConfig.isAutoDnsEnabled());
				for (int j=0;j<jHostNetworkConfig.getVecDnsServer().size();j++) {
					if(j==0){
						adapter.setPreferredDNS(jHostNetworkConfig.getVecDnsServer().get(j));
					}
					else if(j==1){
						adapter.setAlternateDNS(jHostNetworkConfig.getVecDnsServer().get(j));
					}
					else{
						break;
					}
				}

				adapterArray[i] = adapter;
			}

			return adapterArray;

		} catch (Exception e) {
			return new NetworkAdapter[0];
		}
	}

	@Override
	public short GetHostProcessorArchitectural(){
		checkSession();
		try{
			NativeFacade facade = BackupService.getInstance().getNativeFacade();
			return facade.GetHostProcessorArchitectural();
		}catch (Exception e) {
			return -1;
		}
	}

	@Override
	public boolean isHostOSGreaterEqualW2K8SP2() {
		checkSession();
		//to check if >= w2k8sp2, call HA_IsHostOSGreaterEqual(6, 0, 2, 0)
		return HAService.getInstance().HAIsHostOSGreaterEqual(6, 0, (short)2, (short)0);
	}
	
	@Override
	public int[] checkPathIsSupportHyperVVM(String[] allPaths){
		checkSession();
		return HAService.getInstance().checkPathIsSupportHyperVVM(allPaths);
	}
	
	@Override
	public boolean checkResourcePoolExist(String esxServer, String username,
			String passwod, String protocol, int port, VWWareESXNode host, String resPoolRef){
		checkSession();
		return HAService.getInstance().checkResourcePoolExist(esxServer, username, passwod, protocol, port, host, resPoolRef);
	}
	
	@Override
	public int installVDDKService(){
		checkSession();
		return HAService.getInstance().installVDDKService();
	}

	//Get production server disk info
	@Override
	public DiskModel[] getProductionServerDiskList(){
		checkSession();
		NativeFacade facade = BackupService.getInstance().getNativeFacade();
		SortedSet<DiskModel> models = new TreeSet<DiskModel>(new Comparator<DiskModel>() {
			@Override
			public int compare(DiskModel d1, DiskModel d2) {
				return d1.getDiskNumber() - d2.getDiskNumber();
			}
		});

		try {
			Vector<com.ca.arcflash.failover.model.Disk> disks = new Vector<com.ca.arcflash.failover.model.Disk>();
			Vector<com.ca.arcflash.failover.model.Volume> onlineVolumes = new Vector<com.ca.arcflash.failover.model.Volume>();
			int result = facade.GetOnlineDisksAndVolumes(disks, onlineVolumes);
			try {
				HACommon.dealWithGPT(disks);
			} catch (HAException e) {
				logger.error("Error in handling GPT." + e.getMessage());
				logger.error(e);
				return new DiskModel[0];
			}

			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			BackupVolumes volumes = configuration.getBackupVolumes();
			String destnation = configuration.getDestination();
			boolean isShareFolder = CommonUtil.isRemote(destnation);
			String backupDestVolume="";
			if(!isShareFolder){
				backupDestVolume = destnation.substring(0,1);
			}
			for (com.ca.arcflash.failover.model.Disk disk : disks) {
				SortedSet<DiskExtent> extents = disk.getDiskExtents();
				DiskModel model = new DiskModel();
				model.setDiskNumber(disk.getDiskNumber());
				model.setSignature(disk.getSignature());
				model.setSize(disk.getSize());
				boolean flag = false;
				for(DiskExtent extent : extents){
					for (com.ca.arcflash.failover.model.Volume vol : onlineVolumes){
						if(extent.getVolumeID().equals(vol.getVolumeID())){
							if(volumes.isFullMachine()){
								if(isShareFolder || !backupDestVolume.equals(vol.getDriveLetter())){
									DiskExtentModel extentModel = new DiskExtentModel();
									extentModel.setVolumeID(vol.getVolumeID());
									extentModel.setDriveLetter(vol.getDriveLetter());
									model.getVolumes().add(extentModel);
									flag = true;
								}
							}else if(volumes.getVolumes().contains(vol.getDriveLetter()+":")){
								DiskExtentModel extentModel = new DiskExtentModel();
								extentModel.setVolumeID(vol.getVolumeID());
								extentModel.setDriveLetter(vol.getDriveLetter());
								model.getVolumes().add(extentModel);
								flag = true;
							}
						}
					}
				}
				if(flag){
					models.add(model);
				}
			}

			return models.toArray(new DiskModel[0]);

		} catch (ServiceException e) {
			logger.error("Failed to get disks.");
			return new DiskModel[0];
		}
	}

	@Override
	public SummaryModel getSummaryModel(String afguid, String vmuuid, String vmname){
		checkSession();
		try {
			return HAService.getInstance().getSummaryModel(afguid, vmuuid, vmname, false);
		} catch (Exception e) {
			logger.error("Failed to getSummaryModel." + e.getMessage(), e);
			return new SummaryModel();
		}
	}

	public SummaryModel getProductionServerSummaryModel(String afGuid){
		checkSession();
		try {
			return HAService.getInstance().getProductionServerSummaryModel(afGuid);
		} catch (Exception e) {
			logger.error("Failed to getProductionServerSummaryModel." + e.getMessage());
			logger.error(e);
			return new SummaryModel();
		}
	}

	@Override
	public String getVMDiskSignature(String afguid, String esxHost,String esxUser,String esxPassword,
			String moreInf, int port, String snapMoref, String vmdkUrl, String jobID, VMwareConnParams exParams){
		checkSession();
		try {
			if (null == exParams) { //Backward compatible with UDP 5.0 for this parameter is not set by legacy web client.
				exParams = new VMwareConnParams();
			}
			NativeFacade facade = BackupService.getInstance().getNativeFacade();
			String signature = facade.GetVMDKSignature(afguid, esxHost,esxUser,esxPassword,
					  								   moreInf,port,exParams,snapMoref, vmdkUrl,jobID);
			return signature;

		} catch (Exception e) {
			logger.error("Failed to get vmware web service connection." + e.getMessage());
			logger.error(e);
			return "";
		}
	}

	public String getMonitorD2DInstallPath(){
		checkSession();
		return CommonUtil.D2DInstallPath;
	}

	@Override
	public int startFailoverForProductionServer(String afGuid,VMSnapshotsInfo vmSnapInfo) {
		checkSession();
		logger.debug("startFailoverForProductionServer(VMSnapshotsInfo) - start");

		HAService.getInstance().startFailoverForProductionServer(afGuid,vmSnapInfo);

		logger.debug("startFailoverForProductionServer(VMSnapshotsInfo) - end");
		return 0;
	}


	//Operate the local HyperV
	public VirtualMachineInfo[] GetHyperVVmList(){
		checkSession();
		return HAService.getInstance().GetHyperVVmList();
	}

	public int GetHyperVVmState(String vmGuid){
		checkSession();
		return HAService.getInstance().GetHyperVVmState(vmGuid);
	}

	public VMSnapshotsInfo[] GetHyperVVmSnapshots(String vmGuid){
		checkSession();
		return HAService.getInstance().GetHyperVVmSnapshots(vmGuid);
	}

	public int PowerOnHyperVVM(String vmGuid){
		checkSession();
		return HAService.getInstance().PowerOnHyperVVM(vmGuid);
	}

	@Override
	public int ShutdownHyperVVM(String vmGuid){
		checkSession();
		return HAService.getInstance().ShutdownHyperVVM(vmGuid);
	}

	@Override
	public int shutdownVM(String afGuid){
		checkSession();
		return HAService.getInstance().shutdownVM(afGuid);
	}

	@Override
	public int shutdownVMForProductServer(String afGuid) {
		checkSession();
		return HAService.getInstance().shutdownVMForProductServer(afGuid);
	}

	@Override
	public String TakeHyperVSnapshot(String vmGuid, String snapshotName,
			String snapshotNotes){
		checkSession();
		try {
			return HAService.getInstance().TakeHyperVVmSnapshot(vmGuid, snapshotName, snapshotNotes);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return "";
		}
	}

	@Override
	public int cancelReplication(String afGuid) {
		checkSession();
		logger.debug("cancelReplication() - start");
		try {
			HAService.getInstance().cancelReplication(afGuid);
			logger.debug("cancelReplication() - end");
			return 0;
		}
		catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}

	}
	
	@Override
	public int cancelReplicationSync(String afGuid) {
		checkSession();
		logger.debug("cancelReplicationSync() - start");
		int ret = HAService.getInstance().cancelReplicationSync(afGuid);
		logger.debug("cancelReplicationSync() - end");
		return ret;
	}
	
	@Override
	public boolean isFailoverOngoing(String afGuid) {
		checkSession();
		logger.debug("isFailoverOngoing() - start");
		boolean ret = HAService.getInstance().isFailoverOngoing(afGuid);
		logger.debug("isFailoverOngoing() - end");
		return ret;
	}
	

	public FileVersion GetVDDKVersion() {
		checkSession();
		return HAService.getInstance().GetVDDKVersion();
	}

	public DiskInfo GetDiskFreeSize(String diskName){
		checkSession();
		long totalFreeSize = 0;
		long totalSize = 0;
		BackupConfiguration configuration = null;	

		try{
			if(diskName != null) {
				configuration = new BackupConfiguration();
				configuration.setDestination(diskName);
			}else {
				configuration = BackupService.getInstance().getBackupConfiguration();
			}

			DestinationCapacity capacity = BackupService.getInstance().getDestSizeInformation(configuration);

			if(capacity.getTotalVolumeSize() == 0){
				if(diskName == null || diskName.isEmpty())
					return null;
				else if(diskName.indexOf(":") > 0) {
					configuration.setDestination(diskName);
					capacity = BackupService.getInstance().getDestSizeInformation(configuration);
				}
			}

			totalFreeSize = capacity.getTotalFreeSize();
			totalSize = capacity.getTotalVolumeSize();

		} catch (ServiceException e) {
			logger.error("Fails to get the capacity of " + diskName, e);
			totalFreeSize = 0;
		}

		return new DiskInfo(diskName, totalSize, totalFreeSize);
	}
	@Override
	public String getCurrentRunningSnapShotGuid(String afGuid) {
		checkSession();
		logger.debug("getCurrentRunningSnapShotGuid() - start");
		try {
			String currentRunningSnapShotGuid = HAService.getInstance().getCurrentRunningSnapShotGuid(afGuid);
			logger.debug("getCurrentRunningSnapShotGuid() - end");
			return currentRunningSnapShotGuid;
		}
		catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}
	}

	@Override
	public String getRunningSnapShotGuidForProduction(String afGuid) {
		checkSession();
		logger.debug("getRunningSnapShotGuidForProduction() - start");

		String snapShotUID = HAService.getInstance().getRunningSnapShotGuidForProduction(afGuid);

		logger.debug("getRunningSnapShotGuidForProduction() - end");
		return snapShotUID;
	}

	@Override
	public int getHATransPort(){
		checkSession();
		return HAService.getInstance().getHATransPort();

	}

	public String getAdrconfigFromMonitor(String vmuuid,String vmname){
		checkSession();

		logger.info(vmuuid);
		logger.info(vmname);
		ARCFlashNode flashNode = HeartBeatModelManager.getHeartBeatModel(vmuuid,vmname);

		if(flashNode == null){
			logger.error("Did not find out flashnode in heart beat model.");
			return null;
		}

		final String hostProtocol = flashNode.getHostProtocol();
		final String hostName = flashNode.getHostname();
		final int port = Integer.parseInt(flashNode.getHostport());

		logger.info("protocol: " + hostProtocol);
		logger.info("host: " + hostName);
		logger.info("port: " + flashNode.getHostport());
		WebServiceClientProxy client = getMoniteeWebServiceClient(hostProtocol, hostName, port);

		String afGuid = flashNode.getUuid();
		FailoverJobScript failoverJobScript = HAService.getInstance().getFailoverJobScript(afGuid);
		if (failoverJobScript == null) {
			logger.error("Failed to find failover job script for node " + afGuid);
			return null;
		}
		
		client.getServiceV2().validateUserByUUID(failoverJobScript.getAgentUUID());
		String adrconfigure = client.getServiceV2().getAdrconfigFromVMMonitee(vmuuid, vmname);

		if(adrconfigure == null) {
			logger.error("Failed to find adrconfigure for node " + afGuid);
			return null;
		}

		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		try {
			String fullPath = nativeFacade.GetDrInfoLocalCopyPathForSnapNow_HyperV(vmname, vmuuid);
			OutputStream out = new FileOutputStream(fullPath);
			out.write(adrconfigure.getBytes("UTF-8"));
			out.close();
		} catch (ServiceException e) {
			logger.error("Failed to get path.");
			logger.error(e.getMessage());
			logger.error(e);
			return null;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			logger.error(e);
			return null;
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error(e);
			return null;
		}

		return adrconfigure;
	}

	public String GetIVMAdrconfigFromMonitor(String vmuuid,String vmname){
		checkSession();
		NativeFacade facade = HAService.getInstance().getNativeFacade();
		try {
			logger.info("AdrConfigure dest: " + CommonUtil.D2DInstallPath);
			int ret = facade.generateIVMAdrconfigure(CommonUtil.D2DInstallPath, vmuuid, vmname);
			if(ret != 0){
				logger.error("Failed to genereate IVM adrconfigure.");
				return null;
			}
		} catch (ServiceException e){
			logger.error("Failed to genereate IVM adrconfigure.");
			logger.error(e.getMessage());
			logger.error(e);
			return null;
		}

		String path = CommonUtil.D2DInstallPath;
		if(!path.endsWith("\\")){
			path += "\\";
		}

		InputStream in = null;
		ByteArrayOutputStream arrayBuffer = null;
		try {
			logger.info("adrconfigure: " + path + "AdrConfigure.xml");
			in = new FileInputStream(path + "AdrConfigure.xml");
			arrayBuffer = new ByteArrayOutputStream();
			byte[] tmp = new byte[1024];
			int len = 0;
			while((len = in.read(tmp)) > 0) {
				arrayBuffer.write(tmp, 0, len);
			}
			
			return arrayBuffer.toString("UTF-8");
			
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			logger.error(e);
			return null;
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error(e);
			return null;
		} finally {
			ServiceUtils.closeResource(arrayBuffer);
			ServiceUtils.closeResource(in);
		}
	}
	
	public String GetAdrInfoCFromMonitor(String vmuuid,String vmname){
		checkSession();

		logger.info(vmuuid);
		logger.info(vmname);
		ARCFlashNode flashNode = HeartBeatModelManager.getHeartBeatModel(vmuuid,vmname);

		if(flashNode == null){
			logger.error("Did not find out flashnode in heart beat model.");
			return null;
		}

		final String hostProtocol = flashNode.getHostProtocol();
		final String hostName = flashNode.getHostname();
		final int port = Integer.parseInt(flashNode.getHostport());

		logger.info("protocol: " + hostProtocol);
		logger.info("host: " + hostName);
		logger.info("port: " + flashNode.getHostport());
		WebServiceClientProxy client = getMoniteeWebServiceClient(hostProtocol, hostName, port);

		String afGuid = flashNode.getUuid();
		FailoverJobScript failoverJobScript = HAService.getInstance().getFailoverJobScript(afGuid);
		if (failoverJobScript == null) {
			logger.error("Failed to find failover job script for node " + afGuid);
			return null;
		}
		
		client.getServiceV2().validateUserByUUID(failoverJobScript.getAgentUUID());
		String adrinfoc = client.getServiceV2().GetAdrInfoCFromVMMonitee(vmuuid, vmname);

		if(adrinfoc == null) {
			logger.error("Failed to find adrinfoc for node " + afGuid);
			return null;
		}

		return adrinfoc;
	}
	
	public	int DeleteSnapshotAsync(String strVMGuid, String strSnapGuid){
		checkSession();
		try {
			return HAService.getInstance().DeleteVmSnapshotAsync(strVMGuid, strSnapGuid);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return -1;
		}
	}
	
	private WebServiceClientProxy getMoniteeWebServiceClient(
			final String hostProtocol, final String hostName, final int port) {
		WebServiceClientProxy client;
		try {
			client = WebServiceFactory.getFlashServiceV2(hostProtocol, hostName, port);
		} catch (Exception e1) {
			logger.info("Fail to connect to monitee :" + hostName + ". Refresh monitor DNS and try again.");
			NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
			//refresh the monitor DNS. Only refresh and do not use the returned IPs now.
			nativeFacade.getIpAddressFromDns(hostName);
			logger.info("Refresh monitor DNS and still fail to connect to monitee.");

			client = WebServiceFactory.getFlashServiceV2(hostProtocol, hostName, port);

		}
		return client;
	}

	@Override
	public String getAdrconfigFromVMMonitee(String vmuuid,String vmname){
		checkSession();
		NativeFacade facade = BackupService.getInstance().getNativeFacade();
		try {
			logger.info("AdrConfigure dest: " + CommonUtil.D2DInstallPath);
			int ret = facade.generateAdrconfigure(CommonUtil.D2DInstallPath);
			if(ret != 0){
				logger.error("Failed to genereate adrconfigure.");
				return null;
			}
		} catch (ServiceException e){
			logger.error("Failed to genereate adrconfigure.");
			logger.error(e.getMessage());
			logger.error(e);
			return null;
		}

		String path = CommonUtil.D2DInstallPath;
		if(!path.endsWith("\\")){
			path += "\\";
		}

		InputStream in = null;
		ByteArrayOutputStream arrayBuffer = null;
		try {
			logger.info("adrconfigure: " + path + "AdrConfigure.xml");
			in = new FileInputStream(path + "AdrConfigure.xml");
			arrayBuffer = new ByteArrayOutputStream();
			byte[] tmp = new byte[1024];
			int len = 0;
			while((len = in.read(tmp)) > 0) {
				arrayBuffer.write(tmp, 0, len);
			}
			
			return arrayBuffer.toString("UTF-8");
			
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			logger.error(e);
			return null;
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error(e);
			return null;
		} finally {
			ServiceUtils.closeResource(arrayBuffer);
			ServiceUtils.closeResource(in);
		}
	}

	@Override
	public String GetAdrInfoCFromVMMonitee(String vmuuid,String vmname){
		checkSession();
		NativeFacade facade = BackupService.getInstance().getNativeFacade();
		String filepath = null;
		try {
			filepath = facade.GenerateAdrInfoC(); // maybe need add new interface
			logger.info("AdrInfoC dest: " + filepath);
		} catch (ServiceException e){
			logger.error("Failed to genereate AdrInfoC.drz file.");
			logger.error(e.getMessage());
			logger.error(e);
			return null;
		}

		try {
			File file = new File(filepath);
			byte[] bytes = new byte[(int)file.length()];
			DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(filepath)));
			dataInputStream.readFully(bytes);           
			dataInputStream.close();   
			
			String encodedText = (new BASE64Encoder()).encode(bytes);
			 if (encodedText.length() > 76) {
				 //RFC2045 The encoded output stream must be represented in lines of no more than 76 characters each 
				 //So leave out the CRLF to avoid the escape of XML serialization, otherwise there are "&#xd;&#xa;"
				 //in the output XML stream.
				 encodedText = encodedText.replaceAll("\r\n", "");
				 return encodedText;
			 }
			 return encodedText;
			
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			logger.error(e);
			return null;
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error(e);
			return null;
		}catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(e);
			return null;
		}
	}
	
	@Override
	public int configBootableSession(String afGuid,VMSnapshotsInfo vmSnapshotsInfo){
		checkSession();
		return HAService.getInstance().configBootableSession(afGuid, vmSnapshotsInfo, -1);
	}
	@Override
	public int configBootableSessionWithJobID(String afGuid,VMSnapshotsInfo vmSnapshotsInfo, long jobID){
		checkSession();
		try {
			int result =  HAService.getInstance().configBootableSession(afGuid, vmSnapshotsInfo, jobID);
			
			ActivityLogSyncher.getInstance().sync(afGuid);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return -1;
		}
	}

	@Override
	public int createHyperVBootableSnapshot(String lastRepDest,String afGuid,String sessionGuid, String bootableSnapshotName){
		checkSession();
		try {
			return HAService.getInstance().createHyperVBootableSnapshot(lastRepDest,afGuid, sessionGuid, bootableSnapshotName, -1);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return -1;
		}
	}
	@Override
	public int createHyperVBootableSnapshotWithJobID(String lastRepDest,String afGuid,String sessionGuid, String bootableSnapshotName, long jobID){
		checkSession();
		try {
			int result = HAService.getInstance().createHyperVBootableSnapshot(lastRepDest,afGuid, sessionGuid, bootableSnapshotName, jobID);
			
			ActivityLogSyncher.getInstance().sync(afGuid);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return -1;
		}
	}

	@Override
	public boolean isHyperVRoleInstalled(){
		checkSession();
		return HAService.getInstance().isHyperVRoleInstalled();
	}

	@Override
	public int getHyperVVmAllSnapshotsCount(String vmGuid){
		checkSession();
		return HAService.getInstance().getHyperVVmAllSnapshotsCount(vmGuid);
	}

	@Override
	public int deleteHyperVVMSnapshot(String lastRepDest,String afGuid,String sessionGuid){
		checkSession();
		return HAService.getInstance().deleteHyperVVMSnapshot(lastRepDest,afGuid, sessionGuid);
	}

	@Override
	public boolean isVMWareVMNameExist(String host, String username, String password,
			 String protocol, boolean ignoreCertAuthentidation, long viPort,VWWareESXNode esxNode, String vmName){
		checkSession();
		
		Boolean b = HAService.getInstance().isVMWareVMNameExist(host, username, password, protocol,
				                                                ignoreCertAuthentidation, viPort, esxNode, vmName);
		logger.info(String.format("isVMWareVMNameExist host %s, user %s, exsNode %s, vmName %s, exist %s", host, username, esxNode, vmName, b.toString()));
		
		return b;
	}

	@Override
	public String isHyperVVMNameExist(String vmName){
		checkSession();
		return HAService.getInstance().isHyperVVMNameExist(vmName);
	}

	@Override
	public List<String> getIpAddressFromDns(String hostName) {
		checkSession();
		return HAService.getInstance().getIpAddressFromDns(hostName);
	}

	@Override
	public boolean isBootOrSystemVolumeOnDynamicDisk() {
		checkSession();
		boolean res = false;
		try{
			res = HAService.getInstance().isBootOrSystemVolumeOnDynamicDisk();
		}catch(ServiceException e){
			convertServiceException2AxisFault(e);
		}
		return res;
	}

	@Override
	public VCMSavePolicyWarning[] applyVCMJobPolicy(String jobScriptComboStr, String instanceUuid) throws Exception {
		checkSession();
		return HAService.getInstance().applyVCMJobPolicy(jobScriptComboStr, instanceUuid);
	}

	@Override
	public VCMSavePolicyWarning[] applyVCMJobPolicyWithJobCombo(JobScriptCombo jobScript, String instanceUuid)
			throws Exception {
		checkSession();
		VCMPolicyDeployParameters deployPolicyParameters = new VCMPolicyDeployParameters();
		deployPolicyParameters.setInstanceUuid( instanceUuid );
		return HAService.getInstance().applyVCMJobPolicy(jobScript, deployPolicyParameters);
	}

	@Override
	public int unApplyVCMJobPolicy(String instanceUuid) throws Exception {
		checkSession();
		return HAService.getInstance().unApplyVCMJobPolicy(instanceUuid);
	}

	@Override
	public int registerAlertJobscript(String alertJobscriptStr) {
		checkSession();
		return HAService.getInstance().registerAlertJobscript(alertJobscriptStr);
	}

	@Override
	public int unRegisterAlertJobscript(String afguid) {
		checkSession();
		return HAService.getInstance().unRegisterAlertJobscript(afguid);
	}

	@Override
	public RepJobMonitor getRepJobMonitor(String afGuid) {
		checkSession();
		try {
			String localAFGuid = HAService.getInstance().evaluteAFGuid(afGuid);
			RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitor(localAFGuid);
			return jobMonitor;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}


	@Override
	public GRTCatalogItem[] getGRTCatalogItems(String catalogFilePath, long lowSelfid, long highSelfid){
		checkSession();
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(catalogFilePath);
			if (lock != null) {
				lock.lock();
				RemoteFolderConnCache.reEstalishConnetion(getSession());
			}
			return BrowserService.getInstance().getGRTCatalogItems(
					catalogFilePath, lowSelfid, highSelfid);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	public String getMsgCatalogPath(String dbIdentify, String backupDestination,
			long sessionNumber, long subSessionNumber)
			{
		checkSession();
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(backupDestination);
			if (lock != null) {
				lock.lock();
				RemoteFolderConnCache.reEstalishConnetion(getSession());
			}
			return BrowserService.getInstance().getMsgCatalogPath(dbIdentify, backupDestination,
					sessionNumber, subSessionNumber);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	public PagedGRTCatalogItem getPagedGRTCatalogItems(String msgCatPath,long lowSelfID, long highSelfID,
			int start, int size) {
		checkSession();
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(msgCatPath);
			//if lock != null, this path is a remote path
			if(lock != null) {
				lock.lock();
				RemoteFolderConnCache.reEstalishConnetion(getSession());
			}
			return BrowserService.getInstance().getPagedGRTCatalogItems(msgCatPath,
					lowSelfID, highSelfID, start, size);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally {
			if(lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	public PagedGRTCatalogItem browseGRTCatalog(GRTBrowsingContext grtContext) {
		checkSession();
		Lock lock = null;
		try
		{
			if (grtContext != null)
			{
				String msgCatPath = grtContext.getCatalogFilePath();
				lock = RemoteFolderConnCache.getInstance().getLockByPath(msgCatPath);
				// if lock != null, this path is a remote path
				if (lock != null)
				{
					lock.lock();
					RemoteFolderConnCache.reEstalishConnetion(getSession());
				}
			}

			return BrowserService.getInstance().browseGRTCatalog(grtContext);
		}
		catch (ServiceException e)
		{
			throw convertServiceException2AxisFault(e);
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally
		{
			if (lock != null)
			{
				lock.unlock();
			}
		}
	}

	@Override@Deprecated
	public int closeBrowseGRTCatalog(GRTBrowsingContext grtContext) {
		checkSession();
		return 0;
//		Lock lock = null;
//		try {
//			lock = RemoteFolderConnCache.getInstance().getLockByPath(RemoteFolderConnCache.getCachedPath(getSession()));
//			//if lock != null, this path is a remote path
//			if(lock != null) {
//				lock.lock();
//				RemoteFolderConnCache.reEstalishConnetion(getSession());
//			}
//
//			BrowserService.getInstance().closeBrowseGRTCatalog(grtContext);
//			return 0;
//		} catch (ServiceException e) {
//			throw convertServiceException2AxisFault(e);
//		} catch (Throwable e) {
//			logger.error(e.getMessage(), e);
//			throw AxisFault.fromAxisFault("Unhandled exception in web service",
//					FlashServiceErrorCode.Common_ErrorOccursInService);
//		}
//		finally {
//			if(lock != null)
//				lock.unlock();
//		}
	}

	@Override
	public long d2dExCheckUser(String domain,String user, String password)  {
		checkSession();
		Lock lock = null;
		try {
			//lock = RemoteFolderConnCache.getInstance().getLockByPath(msgCatPath);
			//if lock != null, this path is a remote path
			//if(lock != null) {
			//	lock.lock();
			//	RemoteFolderConnCache.reEstalishConnetion();
			//}
			return BrowserService.getInstance().d2dExCheckUser(domain, user, password);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally {
			if(lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	public ExchangeDiscoveryItem[] getTreeExchangeChildren(ExchangeDiscoveryItem parentItem, String strUser, String strPassword)  {
		checkSession();
		Lock lock = null;
		try {
//			lock = RemoteFolderConnCache.getInstance().getLockByPath(catalogFilePath);
//			if (lock != null) {
//				lock.lock();
//				RemoteFolderConnCache.reEstalishConnetion();
//			}
			return BrowserService.getInstance().getTreeExchangeChildren(
					parentItem, strUser, strPassword);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	public long submitCatalogJob(CatalogJobPara catalogJobPara)
	{
		checkSession();
		try
		{
			if(StringUtil.isEmptyOrNull(catalogJobPara.getVmInstanceUUID()))
				return CatalogService.getInstance().submitCatalogJob(catalogJobPara);
			else
				return VSPhereCatalogService.getInstance().submitCatalogJob(catalogJobPara);				
		}
		catch (ServiceException e)
		{
			throw convertServiceException2AxisFault(e);
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long submitFSCatalogJob(CatalogJobPara catalogJobPara, String vmInstanceUUID)
	{
		checkSession();
		try
		{
			return CatalogService.getInstance().submitFSCatalogJob(catalogJobPara, vmInstanceUUID);
		}
		catch (ServiceException e)
		{
			throw convertServiceException2AxisFault(e);
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}


	@Override
	public long validateCatalogFileExist(String dbIdentify, String backupDestination, long sessionNumber,
			long subSessionNumber)
	{
		checkSession();
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(backupDestination);
			if (lock != null) {
				lock.lock();
				RemoteFolderConnCache.reEstalishConnetion(getSession());
			}
			return BrowserService.getInstance().validateCatalogFileExist(dbIdentify, backupDestination,
					sessionNumber, subSessionNumber);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	public PagedExchangeDiscoveryItem getPagedTreeExchangeChildren(ExchangeDiscoveryItem parentItem, int start, int size, String strUser, String strPassword) {
		checkSession();
		Lock lock = null;
		try {
//			lock = RemoteFolderConnCache.getInstance().getLockByPath(msgCatPath);
//			//if lock != null, this path is a remote path
//			if(lock != null) {
//				lock.lock();
//				RemoteFolderConnCache.reEstalishConnetion(context);
//			}
			return BrowserService.getInstance().getPagedTreeExchangeChildren(parentItem, start, size, strUser, strPassword);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally {
			if(lock != null) {
				lock.unlock();
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public List<PolicyDeploymentError> deployPolicy( int policyType, String policyUuid, String policyXml,String edgeID, ApplicationType appType, String parameter )
	{
		checkSession();
		D2DPolicyManagementServiceImpl serviceImpl =
			D2DPolicyManagementServiceImpl.getInstance();
		return serviceImpl.deployPolicy( policyType, policyUuid, policyXml, edgeID, appType, parameter );
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public List<PolicyDeploymentError> removePolicy( int policyType,
		boolean keepCurrentSettings, String edgeID, ApplicationType appType , String parameter)
	{
		checkSession();
		D2DPolicyManagementServiceImpl serviceImpl =
			D2DPolicyManagementServiceImpl.getInstance();
		return serviceImpl.removePolicy( policyType, keepCurrentSettings, edgeID, appType , parameter);
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isUsingEdgePolicySettings( int settingsType )
	{
		checkSession();
		D2DPolicyManagementServiceImpl serviceImpl =
			D2DPolicyManagementServiceImpl.getInstance();
		return serviceImpl.isUsingEdgePolicySettings( settingsType );
	}

	@Override
	public int getReplicationQueueSize(String afGuid) {
		checkSession();

		try {
			SessionInfo[] sessions = HAService.getInstance().getReplicationQueueSize(afGuid);
			/*fanda03 fix 154635; even if there has no session to be replicate; replicationCommand.getNextSessionsToReplicate() 
			  return SessionInfo[0]; not null. 
			   if null is returned, means getReplicationQueueSize() fail to execute.  we return -1 to indicate this scenario 
			  (for example:  if failed to get job lock ,this scenario will happen )
			*/ 
			if(sessions == null){
				return -1;
			}else{
				return sessions.length;
			}
		} catch (HAException e) {
			throw convertHaException2SOAPFaultException(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public void forceNextReplicationMerge(String afGuid,boolean force) {
		HAService.getInstance().forceNextReplicationMergeAllSessions(afGuid,force);
		if (!force) {
			ReplicationJobScript script = HAService.getInstance().getReplicationJobScript(afGuid);
			if (script != null)
				HAService.getInstance().clearIncrementalBackupComplete(script);
		}
	}

	@Override
	public int QueryEdgeMgrStatus(String uuid, ApplicationType appType, String edgeHostName) {

		checkSession();

		D2DRegServiceImpl regImpl = new D2DRegServiceImpl();

		int result = regImpl.QueryEdgeMgrStatus(uuid, appType, edgeHostName);

		return result;
	}
	
	@Override 
	public EdgeRegInfo getEdgeRegInfo(ApplicationType appType) {
		checkSession();
		D2DRegServiceImpl regImpl = new D2DRegServiceImpl();
		return regImpl.getEdgeRegInfo(appType);
	}

	@Override
	public int D2DResync2Edge(String edgeHostName) {
		// TODO Auto-generated method stub
		checkSession();

		D2DReSyncServiceImpl reSyncSvc = new D2DReSyncServiceImpl();
		return reSyncSvc.D2DResync2Edge(edgeHostName);
	}

	@Override
	public ArchiveConfiguration getArchiveConfiguration(){
		checkSession();
		try {
			ArchiveConfiguration archiveConfig = ArchiveService.getInstance().getArchiveConfiguration();
			return archiveConfig;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long saveArchiveConfiguration(ArchiveConfiguration archiveConfig)
			{
		checkSession();
		try {
			if(archiveConfig!=null&&archiveConfig.isbArchiveAfterBackup()){
				return ArchiveService.getInstance().saveArchiveConfiguration(archiveConfig);
			}
			else {
				return ArchiveService.getInstance().removeArchiveConfiguration();
			}
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long validateArchiveConfiguration(ArchiveConfiguration archiveConfig)
			{
		checkSession();
		try {
			return ArchiveService.getInstance().validateArchiveConfiguration(archiveConfig);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public NextArchiveScheduleEvent getNextArchiveScheduleEvent(){
		checkSession();
		try {
			return ArchiveService.getInstance().getNextArchiveScheduleEvent();
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public NextArchiveScheduleEvent getNextPurgeScheduleEvent(){
		checkSession();
		try {
			return PurgeArchiveService.getInstance().getNextPurgeScheduleEvent();
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	public boolean IsArchiveEnabled()  {
		checkSession();
		try {
			return ArchiveService.getInstance().IsArchiveEnabled();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public String[] getArchivedVolumesList(String strArchiveDestination,
			String strUserName, String strPassword)  {
		checkSession();

		try {
			String[] volumes = ArchiveService.getInstance().getArchivedVolumesList(strArchiveDestination,strUserName,strPassword);
			return volumes;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ArchiveDestinationVolumeConfig[] getArchiveDestinationItems(ArchiveDestinationConfig archiveDestConfig)	{
		checkSession();
		try {
			return RestoreArchiveService.getInstance().getArchiveDestinationItems(archiveDestConfig);
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public ArchiveCatalogItem[] getArchiveCatalogItems(long in_volumeHandle,
			String in_strCatPath)  {
		checkSession();
		try {
			ArchiveCatalogItem[] items = BrowserService.getInstance().getArchiveCatalogItems(
					in_volumeHandle, in_strCatPath);
			return items;
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public ArchiveCatalogItem[] getArchivePagedCatalogItems(long in_volumeHandle,
			String in_strCatPath, long lIndex, long lCount)  {
		checkSession();
		try {
			ArchiveCatalogItem[] items = BrowserService.getInstance().getArchivePagedCatalogItems(
					in_volumeHandle, in_strCatPath,lIndex,lCount);
			return items;
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}



	@Override
	public ArchiveDestinationConfig getArchiveDestinationConfig()
			 {

		ArchiveDestinationConfig archiveDestConfig = null;
		checkSession();
		try {
			archiveDestConfig = ArchiveService.getInstance().getArchiveDestinationConfig();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return archiveDestConfig;
	}

/*	@Override
	public long submitArchiveJob(String in_StrJobName) throws AxisFault {
		try {
			return ArchiveService.getInstance().submitArchiveJob(in_StrJobName);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return 0;
	}*/

	@Override
	public JobMonitor getArchiveJobMonitor()  {
		checkSession();
		try {
			//return CommonService.getInstance().getJobMonitor();
			return ArchiveService.getInstance().getArchiveBackupJobMonitor();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean checkSubmitArchiveJob(JArchiveJob archivJobDetails) {
		checkSession();
		try {
			//JArchiveJob archivJobDetails = new JArchiveJob();
			return ArchiveService.getInstance().checkSubmitArchiveJob(archivJobDetails);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long submitArchiveRestoreJob(RestoreArchiveJob job,RestoreJobArchiveVolumeNode[] volumes)  {
		checkSession();
		try {
			job.getArchiveNodes()[0].setPRestoreVolumeAppList(volumes);
			logger.info("job.getStorePath() is: " + job.getCatalogFolderPath());
			job.setCatalogFolderPath(job.getCatalogFolderPath());
			job.setCatalogFolderUser(job.getCatalogFolderUser());
			job.setCatalogFolderPassword(job.getCatalogFolderPassword());
			long lRet = RestoreArchiveService.getInstance().submitRestoreJob(job);
			return lRet;
		} catch (ServiceException e) {
			logger.info("Exception occurred in submitting archive restore job" + e.getErrorCode() + "messa "+ e.getMessage());
			//wanqi06
			ConvertErrorCodeUtil.submitArchiveRestoreJobConvert(e);
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.info("exception ocured. Throwing error");
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}


	@Override
	public Boolean ValidateRestoreArchiveJob(RestoreArchiveJob job,RestoreJobArchiveVolumeNode[] volumes)  {
		checkSession();
		try {
	        
			job.getArchiveNodes()[0].setPRestoreVolumeAppList(volumes);
			Boolean lRet = RestoreArchiveService.getInstance().ValidateRestoreArchiveJob(job);
			return lRet;
		} catch (ServiceException e) {
			logger.info("Exception occurred in Validating archive restore job" + e.getErrorCode() + "message "+ e.getMessage());
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.info("exception ocured. Throwing error");
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}


	@Override
	public Boolean ValidateRestoreJob(RestoreArchiveJob job,RestoreJobArchiveVolumeNode[] volumes)  {
		checkSession();
		try {
	        
			job.getArchiveNodes()[0].setPRestoreVolumeAppList(volumes);
			Boolean lRet = RestoreArchiveService.getInstance().ValidateRestoreJob(job);
			return lRet;
		} catch (ServiceException e) {
			logger.info("Exception occurred in Validating archive restore job" + e.getErrorCode() + "message "+ e.getMessage());
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.info("exception ocured. Throwing error");
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	


	@Override
	public long submitArchiveCatalogSyncJob(
			RestoreArchiveJob in_ArchiveCatalogJobDetails) {
		checkSession();
		try {
			long lRet = ArchiveCatalogSyncService.getInstance().submitArchiveCatalogSyncJob(in_ArchiveCatalogJobDetails);
			return lRet;
		} catch (ServiceException e) {
			logger.info("Exception occurred in submitting archive catalog sync job" + e.getErrorCode() + "messa "+ e.getMessage());
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.info("exception ocured. Throwing error");
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ArchiveFileItem[] getArchivableFilesList(
			ArchiveSourceInfoConfiguration[] inArchiveSourceInfo)
			 {
		checkSession();
		try
		{
			return ArchiveService.getInstance().getArchivableFilesList(inArchiveSourceInfo);
		}
		catch(Throwable ex)
		{

		}
		return null;
	}

	@Override
	public JobMonitor getArchiveRestoreJobMonitor() {
		checkSession();
		try {
			//return CommonService.getInstance().getJobMonitor();
			return RestoreArchiveService.getInstance().getArchiveRestoreJobMonitor();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ArchiveCatalogItem[] searchArchiveDestinationItems(
			ArchiveDestinationConfig archiveDestConfig, String path,long in_lSearchOptions,
			String strfileName,long in_lIndex,long in_lRequiredItems)  {
		checkSession();
		try {
			ArchiveCatalogItem[] items = BrowserService.getInstance().searchArchiveDestinationItems(archiveDestConfig,path,in_lSearchOptions,strfileName,in_lIndex,in_lRequiredItems);

			return items;
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			throw convertServiceException2AxisFault(e);
		}
	}

	@Override
	public JArchiveJob GetFinishedArchiveJobsInfo(JArchiveJob archivJobDetails)
			 {
		checkSession();
		try {
			return ArchiveService.getInstance().GetFinishedArchiveJobsInfo(archivJobDetails);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long validateArchiveSource(ArchiveDiskDestInfo inArchiveDiskDestConfig)  {
		checkSession();

		return ArchiveService.getInstance().validateArchiveSource(inArchiveDiskDestConfig);
	}
	
	@Override
	public String getSymbolicLinkActualPath(String sourcePath) {
		checkSession();
		try {
			return ArchiveService.getInstance().getSymbolicLinkActualPath(sourcePath);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public JobMonitor getArchivePurgeJobMonitor() {
		checkSession();
		try {
			return PurgeArchiveService.getInstance().getArchivePurgeJobMonitor();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ArchiveJobInfo[] GetArchiveJobsInfo(JArchiveJob in_ArchiveJob)
			 {
		checkSession();
		try {
			List<ArchiveJobInfo> jobsInfo = ArchiveService.getInstance().GetArchiveJobsInfo(in_ArchiveJob);
			if(jobsInfo == null)
				return null;
			else
				return jobsInfo.toArray(new ArchiveJobInfo[jobsInfo.size()]);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public Volume[] getSelectedBackupVolumes()  {
		checkSession();
		try {

			return ArchiveService.getInstance().getBackupVolumeDetails();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public Volume[] getFATVolumesList() {
		checkSession();
		try {

			return ArchiveService.getInstance().getFATVolumesList();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

    @Override
	public String QueryD2DTimeZoneID() {
    	checkSession();

		D2DRegServiceImpl d2dRegSer = new D2DRegServiceImpl();
		return d2dRegSer.QueryD2DTimeZoneID();
	}

    @Override
	public CatalogInfo[] checkCatalogExist(String destination, long sessionNumber)  {
		checkSession();
		Lock lock = null;
		try
		{
			lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
			// if lock != null, this path is a remote path
			if (lock != null)
			{
				lock.lock();
				RemoteFolderConnCache.reEstalishConnetion(getSession());
			}

			return BrowserService.getInstance().checkCatalogExist(destination, sessionNumber);
		}
		catch (ServiceException e)
		{
			throw convertServiceException2AxisFault(e);
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally
		{
			if (lock != null)
			{
				lock.unlock();
			}
		}
	}

    @Override
    public long saveD2DConfiguration(D2DConfiguration configuration){
		checkSession();
		long lRet = -1;
		BackupConfiguration backupConfiguration = configuration.getBackupConfiguration();
		PreferencesConfiguration preferencesConfiguration = configuration.getPreferencesConfiguration();
		ArchiveConfiguration archiveConfiguration = configuration.getArchiveConfiguration();
		ArchiveConfiguration archiveDelConfiguration = configuration.getArchiveDelConfiguration();
		ScheduledExportConfiguration scheduledExportConfiguration = configuration.getScheduledExportConfiguration();
		//backupConfiguration.setEmail(preferencesConfiguration.getEmailAlerts());
		
		// new file copy do not depend on catalog validation
//		validate(backupConfiguration, archiveConfiguration);
		
		ArchiveConfig archiveToTapeConfig = configuration.getArchiveToTapeConfig();
		BackupConfiguration old = null;
		try {
			old = BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
		}
		
		if(backupConfiguration!=null && backupConfiguration.getPlanGUID()!=null && old!=null &&!backupConfiguration.getPlanGUID().equals(old.getPlanGUID())){
			ArchiveToTapeUtils.removeArchiveToTape(); // plan is deleted and a new one deployed. Remove the old config.
		}

		if(backupConfiguration!=null) {
			lRet = saveBackupConfiguration(backupConfiguration);
		}
		if(preferencesConfiguration!=null) {
			lRet = savePreferences(preferencesConfiguration);
		}
	
		lRet = saveArchiveConfiguration(archiveConfiguration);
		lRet = saveArchiveDelConfiguration(archiveDelConfiguration);

		if(scheduledExportConfiguration != null) {
			lRet = saveScheduledExportConfiguration(scheduledExportConfiguration);
		}
		
		if(archiveToTapeConfig != null){
			lRet = saveArchiveToTapeConfig(archiveToTapeConfig);
		}

		return lRet;
	}

	@Override
	public long saveArchiveToTapeConfig(ArchiveConfig archiveToTapeConfig) {
		checkSession();
		try{
			return ArchiveToTapeService.getInstance().saveArchiveToTapeConfig(archiveToTapeConfig);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable t){
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	

	@Override
	public ArchiveConfig getArchiveToTapeConfig() {
		checkSession();
		try{
			return ArchiveToTapeService.getInstance().getArchiveToTapeConfig();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable t){
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}	
	

	public long saveSubscriptionConfiguration(SubscriptionConfiguration subscriptionConfiguration) {
		checkSession();
		try {
			return SubscriptionService.getInstance().saveSubscriptionConfiguration(subscriptionConfiguration);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public D2DConfiguration getD2DConfiguration(){
		checkSession();
		BackupConfiguration backupConfiguration = getBackupConfiguration();
		PreferencesConfiguration preferencesConfiguration = getPreferences();

		if(backupConfiguration == null) {
			//For default backup configuration
			backupConfiguration = initBackupConfiguration();
			try {
				Account account = this.getAdminAccount();
				if(account != null) {
					backupConfiguration.setAdminPassword(account.getPassword());
					backupConfiguration.setAdminUserName(account.getUserName());
				}
				long serverTime = getServerTime().getTime();
				//set backup start time plus 5 minutes
				backupConfiguration.setBackupStartTime(serverTime + 5 * 60 * 1000);
				backupConfiguration.setPreAllocationBackupSpace(
						BackupService.getInstance().getPreAllocationValue());
//				//wanqi06:adv schedule
//				BackupService.getInstance().initDefaultSchedule(backupConfiguration, getServerTime().getTime());
			}catch(Throwable t) {
				logger.debug(t.getMessage());
			}
		}
		
		ArchiveConfiguration archiveConfiguration = null;
		ArchiveConfiguration archiveDelConfiguration = null;
		try {
			archiveConfiguration = getArchiveConfiguration();
			archiveDelConfiguration=getArchiveDelConfiguration();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		ScheduledExportConfiguration scheduledExportConfiguration = getScheduledExportConfiguration();

		D2DConfiguration d2dConfiguration = new D2DConfiguration();
		d2dConfiguration.setBackupConfiguration(backupConfiguration);
		d2dConfiguration.setPreferencesConfiguration(preferencesConfiguration);
		d2dConfiguration.setArchiveConfiguration(archiveConfiguration);
		d2dConfiguration.setArchiveDelConfiguration(archiveDelConfiguration);
		d2dConfiguration.setScheduledExportConfiguration(scheduledExportConfiguration);
		
		d2dConfiguration.setArchiveToTapeConfig(getArchiveToTapeConfig());

		return d2dConfiguration;
	}
	
	private void validate(BackupConfiguration backupConfiguration,
			ArchiveConfiguration archiveConfiguration) {
		if(backupConfiguration!=null && archiveConfiguration !=null) {
			ServiceException e = null;
			//File copy requires you to also enable file system catalog generation.
			if(archiveConfiguration.isbArchiveAfterBackup()){
				
				//for Custom Week schedule
				//if(backupConfiguration.getAdvanceSchedule().getDailyScheduleDetailItems() != null && backupConfiguration.getAdvanceSchedule().getDailyScheduleDetailItems().size() > 0){
					if(!backupConfiguration.isGenerateCatalog()){// Custom week or manually submitted.
						e = new ServiceException(FlashServiceErrorCode.planFilecopyEnableCatalog);
					}
				//}
				
				if(backupConfiguration.getAdvanceSchedule() != null && backupConfiguration.getAdvanceSchedule().getPeriodSchedule()!=null){					
					PeriodSchedule ps = backupConfiguration.getAdvanceSchedule().getPeriodSchedule();		
					//for Daily
					if(ps.getDaySchedule() != null && ps.getDaySchedule().isEnabled() && !ps.getDaySchedule().isGenerateCatalog()){
						e = new ServiceException(FlashServiceErrorCode.planFilecopyEnableCatalog);
					}
					//for weekly
					if(ps.getWeekSchedule() != null && ps.getWeekSchedule().isEnabled() && !ps.getWeekSchedule().isGenerateCatalog()){
						e = new ServiceException(FlashServiceErrorCode.planFilecopyEnableCatalog);
					}
					// for Monthly
					if(ps.getMonthSchedule() != null && ps.getMonthSchedule().isEnabled() && !ps.getMonthSchedule().isGenerateCatalog()){
						e = new ServiceException(FlashServiceErrorCode.planFilecopyEnableCatalog);
					}
				}
			}
			if(e != null){
				throw convertServiceException2AxisFault(e);
			}
		}		
	}	
	
	private BackupConfiguration initBackupConfiguration() {	
		BackupConfiguration bm = new BackupConfiguration();
		bm.setD2dOrRPSDestType(false);
		BackupVolumes backVolModel=new BackupVolumes();
		backVolModel.setFullMachine(true);
		bm.setBackupVolumes(backVolModel);
		bm.getRetentionPolicy().setUseBackupSet(false);
		bm.getRetentionPolicy().setBackupSetCount(2);
		bm.getRetentionPolicy().setUseTimeRange(false);
		bm.setRetentionCount(31);
		bm.setCompressionLevel(1);
		bm.setEnableEncryption(false);
		bm.setEncryptionAlgorithm(0);
		bm.setChangedBackupDest(false);
		bm.setChangedBackupDestType(0);
		bm.setBackupDataFormat(1);	//new data format.
		
//		for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; ++i) {
//			DailyScheduleDetailItem item = new DailyScheduleDetailItem();
//			item.setDayofWeek(i);
//			item.getScheduleDetailItems().add(createDefaultScheduleDetailItem());
//			bm.getAdvanceSchedule().getDailyScheduleDetailItems().add(item);
//		}
//		//see com.ca.arcserve.edge.app.base.ui.client.components.policymanagement.impl.uicomponents.homepage.createwizard.PlanTaskComp.initPolicyBackupEventModel()
		initPeriodSchedule(bm.getAdvanceSchedule());
		return bm;
	}
	
//	private void initPeriodSchedule(AdvanceSchedule aSchedule) {
//		PeriodSchedule pSchedule = aSchedule.getPeriodSchedule();
//		
//		int startTime = 20;
//		
//		EveryDaySchedule dailySchedule = pSchedule.getDaySchedule();
//		dailySchedule.setBkpType(1);
//		dailySchedule.setDayTime(new DayTime(startTime, 0));
//		dailySchedule.setEnabled(false);
//		dailySchedule.setGenerateCatalog(false);
//		dailySchedule.setRetentionCount(7);
//		
//		EveryWeekSchedule weeklySchedule = pSchedule.getWeekSchedule();
//		weeklySchedule.setBkpType(1);
//		weeklySchedule.setDayOfWeek(6);
//		weeklySchedule.setDayTime(new DayTime(startTime,0));
//		weeklySchedule.setEnabled(false);
//		weeklySchedule.setGenerateCatalog(false);
//		weeklySchedule.setRetentionCount(5);
//		
//		EveryMonthSchedule monthlySchedule = pSchedule.getMonthSchedule();
//		monthlySchedule.setBkpType(1);
//		monthlySchedule.setDayOfMonth(32);
//		monthlySchedule.setDayOfMonthEnabled(true);
//		monthlySchedule.setDayTime(new DayTime(startTime,0));
//		monthlySchedule.setEnabled(false);
//		monthlySchedule.setGenerateCatalog(false);
//		monthlySchedule.setRetentionCount(12);
//		monthlySchedule.setWeekDayOfMonth(6);
//		monthlySchedule.setWeekNumOfMonth(0);
//		monthlySchedule.setWeekOfMonthEnabled(false);		
//	}
	
//	private ScheduleDetailItem createDefaultScheduleDetailItem() {
//		ScheduleDetailItem item = new ScheduleDetailItem();		
//		item.setJobType(BackupType.Incremental);
//		item.setStartTime(new DayTime(8, 0));
//		item.setEndTime(new DayTime(18, 0));
//		item.setInterval(3);
//		item.setIntervalUnit(1);
//		item.setRepeatEnabled(true);
//		
//		return item;
//	}
	
	
	protected void initPeriodSchedule(AdvanceSchedule aSchedule) {
		PeriodSchedule pSchedule = aSchedule.getPeriodSchedule();
		
		EveryDaySchedule dailySchedule = pSchedule.getDaySchedule();
		dailySchedule.setBkpType(1);
		dailySchedule.setDayTime(new DayTime(22, 0));
		dailySchedule.setEnabled(true);
		dailySchedule.setGenerateCatalog(false);
		dailySchedule.setRetentionCount(7);
		dailySchedule.setDayEnabled(new Boolean[]{true,true,true,true,true,true,true});
		dailySchedule.setCheckRecoveryPoint(false);
		
		EveryWeekSchedule weeklySchedule = pSchedule.getWeekSchedule();
		weeklySchedule.setBkpType(1);
		weeklySchedule.setDayOfWeek(6);
		weeklySchedule.setDayTime(new DayTime(20,0));
		weeklySchedule.setEnabled(false);
		weeklySchedule.setGenerateCatalog(false);
		weeklySchedule.setRetentionCount(5);
		weeklySchedule.setCheckRecoveryPoint(false);
		
		EveryMonthSchedule monthlySchedule = pSchedule.getMonthSchedule();
		monthlySchedule.setBkpType(1);
		monthlySchedule.setDayOfMonth(32);
		monthlySchedule.setDayOfMonthEnabled(true);
		monthlySchedule.setDayTime(new DayTime(20,0));
		monthlySchedule.setEnabled(false);
		monthlySchedule.setGenerateCatalog(false);
		monthlySchedule.setRetentionCount(12);
		monthlySchedule.setWeekDayOfMonth(6);
		monthlySchedule.setWeekNumOfMonth(0);
		monthlySchedule.setWeekOfMonthEnabled(false);
		monthlySchedule.setCheckRecoveryPoint(false);
	}
	
	public long isEnableEncryption(long agentType)
	{
		try {
			
			if(agentType == 0)
			{
				BackupConfiguration configuration = getBackupConfiguration();
				if (configuration == null)
					return -1;
			
				if(configuration.isEnableEncryption())
					return 1;
				else
					return 0;
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}		
		return -1;
	}
	
	public long getDestType(long agentType)
	{
		try {
			long destType = 0;
			
			if(agentType == 0)
			{
				BackupConfiguration configuration = getBackupConfiguration();
				if (configuration == null)
					return -1;

				if (!configuration.isD2dOrRPSDestType())
				{
					BackupRPSDestSetting backupRpsDestSetting = configuration.getBackupRpsDestSetting();
					if(backupRpsDestSetting != null)
					{
						if(backupRpsDestSetting.isDedupe())
							destType = 3;//dedup data store
						else
							destType = 2;//merge phase II data store
					}
					else
						return -1;
				}
				else if(configuration.getPlanId() != null)
				{
					destType = 1;//standalone d2d with rps plan, local disk
				}
				else
				{
					destType = 0;//standalone d2d with local disk
				}
			}
			else
				return -1;
			
			return destType;
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}		
		return -1;
	}
	
	public SubscriptionConfiguration getSubscriptionConfiguration() {
		checkSession();
		try {
			return SubscriptionService.getInstance().getSubscriptionConfiguration();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public String[] getCloudBuckets(ArchiveCloudDestInfo cloudDestInfo) {
		checkSession();
		String[] bucketList = null;
		try {
			bucketList = ArchiveService.getInstance().getCloudBuckets(cloudDestInfo);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return bucketList;
	}

	@Override
	public String[] getCloudRegions(ArchiveCloudDestInfo cloudDestInfo) {
		checkSession();
		String[] regionsList = null;
		try {
			regionsList = ArchiveService.getInstance().getCloudRegions(cloudDestInfo);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return regionsList;
	}

	@Override
	public String getRegionForBucket(ArchiveCloudDestInfo cloudDestInfo) {
		checkSession();
		String region = null;
		try {
			region = ArchiveService.getInstance().getRegionForBucket(
					cloudDestInfo);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return region;
	}

	@Override
	public long testConnection(ArchiveCloudDestInfo cloudDestInfo) {
		checkSession();
		long connectionStatus;
		try {
			connectionStatus = ArchiveService.getInstance().testConnection(
					cloudDestInfo);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}

		return connectionStatus;
	}

	@Override
	public long verifyBucketName(ArchiveCloudDestInfo cloudDestInfo) {
		checkSession();
		long isBucketExits;
		try {
			isBucketExits = ArchiveService.getInstance().verifyBucketName(
					cloudDestInfo);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return isBucketExits;
	}

	@Override
	public String GetArchiveDNSHostName() {
		checkSession();
		String hostName;
		try {
			hostName = ArchiveService.getInstance().GetArchiveDNSHostName();
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return hostName;
	}

	@Override
	public VCMConfigStatus getVCMConfigStatus(String vmInstanceUUID) {
		checkSession();

		return HAService.getInstance().getVCMConfigStatus(vmInstanceUUID);
	}

	@Override
	public MachineType getMachineType() {
		checkSession();
		return HAService.getInstance().getMachineType();
	}

	@Override
	public MachineDetail updateESXDetails(MachineDetail detail) {
		checkSession();
		try {
			return HAService.getInstance().updateMachineDetail(detail);
		}catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}
		catch (Throwable t) {

			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int startHATransServer() {
		checkSession();

		try {
			ReplicationService.startHATransServerDirectly();
			return 0;
		} catch (Exception e) {
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	@Override
	public int edgeChangeToProtocol(String hostname, String appUUID, ApplicationType appType,
			int toProtocol) throws EdgeServiceFault {
		logger.debug("edgeChangeToProtocol:" + hostname + ":" + appType + " protocol:"+toProtocol );
		if(toProtocol!=1 && toProtocol!=2){
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_Edge_ChangeProtocol_Invalid_Protocol, "invalid protoco!");
		}
		D2DEdgeRegistration d2dEdgeReg = new D2DEdgeRegistration();
		int i = d2dEdgeReg.changeProtocol(appUUID, appType, toProtocol);
		logger.debug("edgeChangeToProtocol end with: " + i );
		return i;
	}

	@Override
	public Boolean isDriverInstalled() {
		checkSession();
		boolean value = false;
		try {
			value = CommonService.getInstance().isDriverInstalled();
		}catch(Throwable t) {
			logger.error("Read driver registry key error", t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}

		return value;
	}

	@Override
	public Boolean isRestartedAfterDriver() {
		checkSession();
		boolean value = false;
		try {
			value = CommonService.getInstance().isRestartedAfterDriver();
		}catch(Throwable t) {
			logger.error("Read restart registry key error", t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}

		return value;
	}

		@Override
	public ArchiveDestinationDetailsConfig getArchiveChangedDestinationDetails(ArchiveDestinationConfig inArchiveConfig) {
		ArchiveDestinationDetailsConfig config = null;
		checkSession();
		try {
			config = ArchiveCatalogSyncService.getInstance().getArchiveChangedDestinationDetails(inArchiveConfig);
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
		}
		return config;
	}

	@Override
	public LicInfo getLicenseInfo() {
		checkSession();
		try {
			return CommonService.getInstance().getLicInfo();
		}catch(Throwable t) {
			logger.error("Get license meet error", t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ArchiveJobInfo getArchiveSummaryInfo() {
		checkSession();
		try {
			return ArchiveService.getInstance().getArchiveSummaryInfo();
		}catch(Throwable t) {
			logger.error("Get archive summary meet error", t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean checkRecoveryVMJobExist(String vmName,
			String esxServerName) {
		checkSession();
		try{
			return VSphereService.getInstance().checkRecoveryVMJobExist(vmName, esxServerName);
		}catch(Throwable t) {
			logger.error("checkRecoveryVMJobExist error", t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean validateEmailFromAddress(String address) {
		checkSession();
		try {
			return CommonService.getInstance().validateEmailFromAddress(address);
		}catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable t){
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ResourcePool[] getResourcePool(VirtualCenter vc, ESXServer esxServer,ResourcePool parentResourcePool) {
		checkSession();
		try{
			return VSphereService.getInstance().getResourcePool(vc, esxServer,parentResourcePool);
		}catch(Throwable t) {
			logger.error("checkRecoveryVMJobExist error", t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public void cleanupGeneralSettings()
	{
		checkSession();
		try{
			CommonService.getInstance().cleanupGeneralSettings();
		}catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable t){
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}

	}
	
	@Override
	public long checkServiceStatus(String serviceName)
	{
		checkSession();
		try{
			return CommonService.getInstance().checkServiceStatus(serviceName);
		}catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable t){
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public String[] deleteMonitorHartFiles(String moniteeName,String sessionName){
		checkSession();
		try {
			return HAService.getInstance().deleteMonitorHartFiles(moniteeName,sessionName);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		
	}
	
	@Override
	public boolean checkMonitorHartFilesExistence(String moniteeName,String sessionName){
		checkSession();
		try {
			return HAService.getInstance().checkMonitorHartFilesExistence(moniteeName,sessionName);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		
	}
	
	@Override
	public long checkMonitorJavaHeapSize() {
		checkSession();
		return HAService.getInstance().checkMonitorJavaHeapSize();		
	}
	
	@Override
	public EdgeLicenseInfo getConversionLicense(String aFGuid) {
		checkSession();
		try {
			return HAService.getInstance().getConversionLicense(aFGuid);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public List<CloudProviderInfo> getCloudProviderInfos() {
		List<CloudProviderInfo> cloudProviderInfo;
		try {
			cloudProviderInfo = ArchiveService.getInstance().getCloudProviderInfo();
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return cloudProviderInfo;
	}

	@Override
	public String getMntPathFromVolumeGUID(String strGUID) {
		checkSession();
		try {
			return CommonService.getInstance().getMntPathFromVolumeGUID(strGUID);
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public MountSession[] getMountedSessions() {
		checkSession();
		
		try {
			return CommonService.getInstance().getMountedSessions();
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public MountSession[] getMountedSessionsByBackupDest(String currentDest) {
		checkSession();
		
		try {
			return CommonService.getInstance().getMountedSessions(currentDest);
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public RecoveryPoint[] getVMRecentBackupsByServerTime(int backupType,
			int backupStatus, String serverBeginDate, String serverEndDate,
			boolean needCatalogStatus, VirtualMachine vm) {
		checkSession();
		try {
			Date beginD = string2Date(serverBeginDate);
			Date endD = string2Date(serverEndDate);
			return VSphereService.getInstance().getRecentBackupsByServerTime(backupType, backupStatus, beginD, endD, needCatalogStatus,vm);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	@Override
	public long getServerTimezoneOffset(int year, int month, int day, int hour, int min) {
		checkSession();
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(year, month, day, hour, min);
			return CommonService.getInstance().getServerTimezoneOffset(cal.getTime());
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long getServerTimezoneOffsetByMillis(long date) {
		checkSession();
		try {
			return CommonService.getInstance().getServerTimezoneOffsetByMillis(date);
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int checkVIXStatus() {
		checkSession();
		try {
			return VSphereService.getInstance().checkVIXStatus();
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public VMStatus[] getVMStatus(VirtualMachine vm) {
		checkSession();
		try{
			return VSphereService.getInstance().getVMStatus(vm);
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public boolean isHeartBeatPaused(String afguid){
		
		checkSession();
		try{
			
			HeartBeatJob hJob = (HeartBeatJob)HAService.getInstance().getHeartBeatJob(afguid);
			if (null == hJob) {	//zxh, fixed null pointer exception
				logger.warn("job is null,isHeartBeatPaused return true");
				return true;
			}
			
			return hJob.getJobStatus().getStatus() == HAJobStatus.Status.Canceled;
		
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		
		
		
	}

	@Override
	public void removeMonitee(String afGuid) {
		checkSession();
		
		try {
			HAService.getInstance().removeMonitee(afGuid);
		}
		catch(Exception e) {
			logger.error(e.getMessage(),e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	@Override
	public String updateDestAccess(String dest, String user, String pass,
			String domain) {
		checkSession();
		try {
			return BackupService.getInstance().updateDestConnection(dest, user, pass, domain);
		}catch(ServiceException e){
			throw convertServiceException2AxisFault(e);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public String checkDestChainAccess() {
		checkSession();
		try {
			return BackupService.getInstance().checkDestChainAccess();
		}catch(ServiceException e){
			throw convertServiceException2AxisFault(e);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long getArchiveChildrenCount(long volumeHandle, String strCatPath) {
		checkSession();
		long childCount = 0L;
		try {
			childCount = BrowserService.getInstance().getArchiveChildrenCount(volumeHandle, strCatPath);
			
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			throw this.convertServiceException2AxisFault(e);
		}
		return childCount;
	}

	@Override
	public DataStore[] validateRecoveryVMToOriginal(VirtualCenter vc, BackupVM backupVM,int sessionNum) {
		checkSession();
		try{
			return VSphereService.getInstance().validateRecoveryVMToOriginal(vc, backupVM,sessionNum);
		}catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public D2DStatusInfo getD2DStatusInfo()
	{
		return D2DStatusServiceImpl.getInstance().getD2DStatusInfo();
	}

	@Override
	public D2DStatusInfo getVCMStatusInfo( String uuid )
	{
		return D2DStatusServiceImpl.getInstance().getVCMStatusInfo( uuid );
	}

	@Override
	public D2DStatusInfo getVSphereVMStatusInfo( String vmInstanceUuid )
	{
		return D2DStatusServiceImpl.getInstance().getVSphereVMStatusInfo( vmInstanceUuid );
	}

	@Override
	public boolean isTimeInDSTBeginInterval(int year, int month, int date,
			int hour, int minute) {
		checkSession();
		return CommonService.getInstance().isTimeInDSTBeginInterval(year, month, date, hour, minute);
	}	
	@Override
	public boolean isTimeInDSTEndInterval(int year, int month, int date,
			int hour, int minute) {
		checkSession();
		return CommonService.getInstance().isTimeInDSTEndInterval(year, month, date, hour, minute);
	}

	@Override
	public long checkVIXVersion() {
		return D2DPFCServiceImpl.getInstance().checkVIXVersion();
	}

	@Override
	public PFCVMInfo getVMInformation(String esxServerName, String esxUserName,
			String esxPassword, String vmName, String vmVMX, String userName,
			String password) {
		return D2DPFCServiceImpl.getInstance().getVMInformation(
				esxServerName, esxUserName, esxPassword, 
				vmName, vmVMX, userName, password);
	}
	
	@Override
	public int checkVMRecoveryPointESXUefi(VirtualCenter vc, ESXServer esxServer,String dest, String domain, String user,String pwd, String subPath){
		checkSession();
		try{
			return VSphereService.getInstance().checkVMRecoveryPointESXUefi(vc, esxServer, dest, domain, user, pwd, subPath);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean isUEFIFirmware() {
		checkSession();
		try {
			return CommonService.getInstance().isUEFIFirmware();
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public MountedRecoveryPointItem[] getAllMountedRecoveryPointItems(){
		checkSession();
		return MountVolumeService.getInstance().getAllMountedRecoveryPointItems();
	}
	
	@Override
	public MountedRecoveryPointItem[] getMountedRecoveryPointItems(String dest,String domain, String user, String pwd, String subPath, String sessionGuid){
		checkSession();
		try {
			return MountVolumeService.getInstance().getMountedRecoveryPointItems(dest, domain, user, pwd, subPath, sessionGuid);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch(SOAPFaultException se) {
			throw se;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		
	}
	
	@Deprecated
	@Override
	public long mountRecoveryPointItem(String dest,String domain, String user, String pwd, String subPath,
			String volGUID,int encryptionType,String encryptPassword, String mountPath){
		checkSession();
		return MountVolumeService.getInstance().mountRecoveryPointItem(dest, domain, user, pwd, subPath, 
				volGUID, encryptionType, encryptPassword, mountPath);
	}
	
	@Override
	public long mountRecoveryPointItemEx( JMountRecoveryPointParams jMntParams ){
		checkSession();
		return MountVolumeService.getInstance().mountRecoveryPointItem( jMntParams );
	}
	
	@Override
	public long disMountRecoveryPointItem(String mountPath, int mountDiskSignature){
		checkSession();
		return MountVolumeService.getInstance().disMountRecoveryPointItem(mountPath, mountDiskSignature);
	}
	
	@Override
	public String[] getAvailableMountDriveLetters(){
		checkSession();
		return MountVolumeService.getInstance().getAvailableMountDriveLetters();
	}

	@Override
	public boolean testEmailSettings(BackupEmail mailConf) {
		checkSession();
		try {
			return BackupService.getInstance().testEmailSettings(mailConf);
		}catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean checkEdgeConnection(ApplicationType appType) {
		return new D2DRegServiceImpl().tryConnect2Edge(appType);
	}

	@Override
	public String getWebServiceErrorMessage(String errorCode, Object[] arguments)
	{
		checkSession();
		try
		{
			return CommonService.getInstance().getServiceError(errorCode, arguments);
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service", FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public MergeFailureInfo getMergeFailureInfo()
	{
		checkSession();
		try
		{
			return CallbackService.getInstance().getMergeFailureInfo();
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service", FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	@Override
	public boolean isShowUpdate() {
		checkSession();
		return CommonService.getInstance().isShowUpdate();
	}

	@Override
	public String getHashValue(String plainText) {
		checkSession();
		return CommonService.getInstance().generateHashValue(plainText);
	}

	@Override
	public String getHashValueForEncrytedString(String encrytedText) {
		checkSession();
		return CommonService.getInstance().generateHashValueForEncryptedText(encrytedText);
	}

	@Override
	public CloudProviderInfo getCloudProviderInfo(long cloudProviderType) {
		CloudProviderInfo cloudProviderInfo;
		try {
			cloudProviderInfo = ArchiveService.getInstance().getCloudProviderInfo(cloudProviderType);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return cloudProviderInfo;
	}
		/**********The following APIs are for merge job*************/
	@Override
	public MergeJobMonitor getVMMergeJobMonitor(String vmInstanceUUID) {
		checkSession();
		return VSphereMergeService.getInstance().getMergeJobMonitor(vmInstanceUUID);
	}

	@Override
    public MergeJobMonitor getMergeJobMonitor() {
		checkSession();
		return MergeService.getInstance().getMergeJobMonitor();
    }

	@Override
	public MergeJobMonitor[] getVMMergeJobMonitorList() {
		checkSession();
		return VSphereMergeService.getInstance().getMergeJobMonitorList();
	}

	@Override
	public MergeStatus getMergeJobStatus() {
		checkSession();
		return MergeService.getInstance().getMergeJobStatus();
	}
	
	@Override
	public MergeStatus[] getVMMergeStatusList() {
		checkSession();
		return VSphereMergeService.getInstance().getMergeStatusList();
	}

	@Override
    public MergeStatus getVMMergeJobStatus(String vmInstanceUUID) {
	   checkSession();
	   return VSphereMergeService.getInstance().getMergeJobStatus(vmInstanceUUID);
    }
	
	@Override
	public int pauseMergeEx(MergeAPISource source) {
		checkSession();
		try {
			return MergeService.getInstance().pauseMerge(source);
		}catch(ServiceException e) {
			throw this.convertServiceException2AxisFault(e);
		}
	}

	@Override
	public int pauseVMMergeEx(MergeAPISource source, String vmInstanceUUID) {
		checkSession();
		try {
			return VSphereMergeService.getInstance().pauseMerge(source, vmInstanceUUID);
		}catch(ServiceException e) {
			throw this.convertServiceException2AxisFault(e);
		}
	}

	@Override
	public int resumeMergeEx(MergeAPISource source) {
		checkSession();
		try {
			return MergeService.getInstance().resumeMerge(source);
		}catch(ServiceException e) {
			throw this.convertServiceException2AxisFault(e);
		}
	}

	@Override
	public int resumeVMMergeEx(MergeAPISource source, String vmInstanceUUID) {
		checkSession();
		try {
			return VSphereMergeService.getInstance().resumeVMMerge(source, vmInstanceUUID);
		}catch(ServiceException e) {
			throw this.convertServiceException2AxisFault(e);
		}
	}

	@Override
	public long backupWithFlag(int type, String name, boolean convertForBackupSet) {
		checkSession();
		try {
			return BackupService.getInstance().backup(type, name, convertForBackupSet);
		}catch(ServiceException e) {
			throw this.convertServiceException2AxisFault(e);
		}
	}
	
	
	@Override
	public long backupVMWithFlag(int type, String name, VirtualMachine vm,
			boolean convertForBackupSet) {
		try {
			return VSphereService.getInstance().backupVM(type, name, vm, convertForBackupSet);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	


	@Override
	public List<BackupSetInfo> getBackupSetInfo() {
		try {
			return BackupSetService.getInstance().getBackupSetInfo(null);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	


	@Override
	public List<BackupSetInfo> getVMBackupSetInfo(String vmInstanceUUID) {
		try {
			return VSphereBackupSetService.getInstance().getBackupSetInfo(vmInstanceUUID);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	/**********Merge job APIs complete*************/


	/**Webservice API for Nimsoft D2D probe start**/
	@Override
	public int registerNimsoft(NimsoftRegisterInfo nimsoftInfo) {
		checkSession();
		try {
			return NimsoftService.getInstance().registerNimsoft(nimsoftInfo);
		}catch(ServiceException e) {
			throw this.convertServiceException2AxisFault(e);
		}
	}

	@Override
	public int unRegisterNimsoft() {
		checkSession();
		return NimsoftService.getInstance().unRegisterNimsoft();
	}

	@Override
	public List<Alert> getAlertMessages() {
		checkSession();
		try {
			return NimsoftService.getInstance().getAlertMessages();
		}catch(ServiceException e) {
			throw this.convertServiceException2AxisFault(e);
		}
	}
	

	/**Webservice API for Nimsoft D2D probe end**/
	@Override
	public Volume[] getVMVolumes(BackupVM vm) {
		checkSession();
		try {
			return VSphereService.getInstance().getVMVolumes(vm);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public FileFolderItem getVMFileFolder(String path, BackupVM vm) {
		checkSession();
		try {
			return VSphereService.getInstance().getVMFileFolder(path, vm);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean createVMFolder(String parentPath, String subDir, BackupVM vm) {
		checkSession();
		try {
			VSphereService.getInstance().createVMFolder(parentPath, subDir, vm);
			return true;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	public void setLocalCheckSession(boolean localCheckSession) {
		getCommonUtil().setLocalCheckSession(localCheckSession);
	}

	@Override
	public String validateUserAndUpdateIfNeeded(String username,
			String password, String domain) {
		// checkSession(); 
		return getCommonUtil().validateUserAndUpdateIfNeeded(username, password,domain, true);
	}

	@Override
	public void takeHyperVVMSnapshotWithUuid(String afGuid) {
		checkSession();
		try {
			logger.info("taking hyper-v vm snapshot.");
			String xml = CommonUtil.getRepositoryConfPath();
			ProductionServerRoot psr = RepositoryUtil.getInstance(xml).getProductionServerRoot(afGuid);
			if (psr != null) {
				RepositoryManager.SnapeShotVM(psr);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault(e.getMessage(), "");
		} catch (Throwable e) {
			logger.error("failed to take hyper-v snapshot", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public String startTakeHyperVVMSnapshotTask(String afGuid)
	{
		checkSession();
		try {
			logger.info("taking hyper-v vm snapshot asynchronously.");
			String xml = CommonUtil.getRepositoryConfPath();
			ProductionServerRoot psr = RepositoryUtil.getInstance(xml).getProductionServerRoot(afGuid);
			if (psr != null) {
				return RepositoryManager.taskSnapshotVMAsync(psr);
			}
			else {
				logger.error(String.format("The Replication Repository does not contain the root server %s.", afGuid));
				throw new Exception(String.format("The Replication Repository does not contain the root server %s.", afGuid));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault(e.getMessage(), "");
		} catch (Throwable e) {
			logger.error("failed to take hyper-v snapshot", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public boolean isHyperVVmSnapshotTaskDone(String taskId)
	{
		checkSession();
		try { 
			return RepositoryManager.isSnapshotVmTaskDone(taskId);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault(e.getMessage(), "");
		} catch (Throwable e) {
			logger.error(String.format("failed to query hyper-v snapshot task id %s.", taskId), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public boolean isExchange2013() {
		checkSession();
		boolean flag = CommonService.getInstance().isExchange2013();
		logger.debug("isExchange2013:" + flag);
		return flag;
	}

	@Override
	public MountSession[] getMountSessionsToMerge() {
		checkSession();
		try {
			return MergeService.getInstance().getMountedSessionsToMerge();
		}catch(Exception e) {
			logger.error("failed to get mounted sessions to purge", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public MountSession[] getVMMountSessionsToMerge(String vmInstanceUUID) {
		checkSession();
		try {
			return VSphereMergeService.getInstance().getMountedSessionsToMerge(vmInstanceUUID);
		}catch(Exception e) {
			logger.error("failed to get mounted sessions to purge", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}	
	
	@Override
	public void specifyESXServerForRVCM(MachineDetail machineDetail, String backupDest, boolean isForceSave){
		checkSession();
		HAService.getInstance().specifyESXServerForRVCM(machineDetail, backupDest, isForceSave);
	}
	
	public boolean getEnforeVDDKNBDFlag(){
		checkSession();
		return HAService.getInstance().getEnforeVDDKNBDFlag();
		
	}

	//For RPS 
	/////////////////////ask D2D to launch Job///////////////////
	@Override
	public long backupNow(BackupJobArg jobArg) {
		checkSession();
		try {
			if(!jobArg.isVM())
				return BackupService.getInstance().backupNow(jobArg);
			else
				return VSphereService.getInstance().backupNow(jobArg);
		}catch(ServiceException se) {
			throw convertServiceException2AxisFault(se);
		}catch(Exception e){
			logger.error("failed to backupnow", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long restoreNow(RestoreJobArg jobArg) {
		try {
			if(jobArg.getJobType() == com.ca.arcflash.webservice.constants.JobType.JOBTYPE_VM_RECOVERY){
				return VSphereService.getInstance().recoveryVMNow(jobArg);
			}else if(!jobArg.isVM())
				return RestoreService.getInstance().restoreNow(jobArg);
			else
				return VSphereService.getInstance().restoreNow(jobArg);
		}catch(ServiceException se) {
			throw convertServiceException2AxisFault(se);
		}catch(Exception e){
			logger.error("failed to backupnow", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public long copyNow(CopyJobArg jobArg) {
		try {
			if(!jobArg.isVM())
				return CopyService.getInstance().copyNow(jobArg);
			else
				return VSphereService.getInstance().copyNow(jobArg);
		}catch(ServiceException se) {
			throw convertServiceException2AxisFault(se);
		}catch(Exception e){
			logger.error("failed to backupnow", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long runCatalogNow(CatalogJobArg jobArg) {
		try {
			if(!jobArg.isVM())
				return CatalogService.getInstance().runCatalogNow(jobArg);
			else
				return VSPhereCatalogService.getInstance().runCatalogNow(jobArg);
		}catch(ServiceException se) {
			throw convertServiceException2AxisFault(se);
		}catch(Exception e){
			logger.error("failed to backupnow", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public long archiveNow(ArchiveJobArg jobArg) {
		try {
			return ArchiveService.getInstance().archiveNow(jobArg);
		}catch(ServiceException se) {
			throw convertServiceException2AxisFault(se);
		}catch(Exception e){
			logger.error("failed to backupnow", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	//////////////////////////////////////////////////////////////////
	@Override
	public RpsPolicy4D2D[] getRPSPolicyList(String hostName,
			String userName, String password, int port, String protocol) {
		checkSession();
		try {
			return SettingsService.instance().getRPSPolicyList(hostName, 
					userName, password, port, protocol, "");					
		}catch(ServiceException se){
			String message = se.getMessage();
			if(FlashServiceErrorCode.RPS_DATASTORE_SERVICE_INACTIVE.equals(se.getErrorCode())){
				message = WebServiceMessages.getResource("rpsPolicyDataStoreInactive");
			}
			throw AxisFault.fromAxisFault(message, se.getErrorCode());
		}catch(Exception e){
			logger.error("failed to getRPSPolicyList", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public void updatePolicy(RpsPolicy4D2D policy4d2d, String vmInstanceUUID) {
		checkSession();
		try {
			if(StringUtil.isEmptyOrNull(vmInstanceUUID))
				BackupService.getInstance().rpsPolicyUpdated(policy4d2d, true);
			else{
				VirtualMachine vm = new VirtualMachine();
				vm.setVmInstanceUUID(vmInstanceUUID);
				VSphereService.getInstance().rpsPolicyUpdated(policy4d2d, true, vm.getVmInstanceUUID());
			}
		}catch(ServiceException se){
			throw AxisFault.fromAxisFault(se.getMessage(), se.getErrorCode());
		}catch(Exception e){
			logger.error("Failed to update RPS policy", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public RpsPolicy4D2D validateRpsDestSetting(
			BackupConfiguration backupConfiguration) {
		checkSession();
		
		try {
			return BackupService.getInstance().validateRpsDestSetting(backupConfiguration);
		}catch(ServiceException se){
			throw AxisFault.fromAxisFault(se.getMessage(), se.getErrorCode());
		}catch(Exception e){
			logger.error("Failed to update RPS policy", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public CatalogItem[] getCatalogItemsOolong(String destination,
			String userName, String passWord, long sessionNumber,
			String volumeGUID, String catlogFilePath, long parentID,
			String parentPath, String encryptedPwd) {
		checkSession();
		
		try {
			return RestoreService.getInstance().getCatalogItems(destination, userName, passWord, sessionNumber, 
					volumeGUID, catlogFilePath, parentID, parentPath, encryptedPwd, getSession());
		}catch(ServiceException se) {
			throw convertServiceException2AxisFault(se);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}	
	}

	@Override
	public PagedCatalogItem getPagedCatalogItemsOolong(String destination,
			String userName, String passWord, long sessionNumber,
			String volumeGUID, String catlogFilePath, long parentID,
			String parentPath, int start, int size, String encryptedPwd) {
		checkSession();
		
		try {
			return RestoreService.getInstance().getPagedCatalogItemsEx(destination, userName, passWord, sessionNumber, volumeGUID, 
					catlogFilePath, parentID, parentPath, start, size, encryptedPwd, getSession());
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long getRecoveryPointItemChildrenCountOolong(long sessNum,
			String catalogPath, String volumeGUID, String destination,
			String userName, String passWord, String encryptedPwd) {
		checkSession();
		try {
			long childCount = RestoreService.getInstance()
					.getRecoveryPointItemsChildCount(sessNum, catalogPath, volumeGUID, destination,  
							userName, passWord, encryptedPwd);
			logger.debug("getRecoveryPointItemsChildCount end");
			return childCount;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public long convertNow(ConversionJobArg jobArg) {
		checkSession();
		logger.debug("convertNow start");
		try {
			return HAService.getInstance().convertNow(jobArg);
		}
		catch (Exception e) {
			logger.error("Fail to execute converNow");
			return 1;
		}
	}
	
	@Override
	public ProductionServerRoot GetProductionServerRoot(String d2dNodeUUID, boolean deleteOrignal)
	{
		checkSession();
		logger.debug(String.format("GetProductionServerRoot start. d2d uuid is %s. deleteOrignal is %s",
				d2dNodeUUID, deleteOrignal? "true" : "false"));
		try {	
			return HAService.getInstance().GetProductionServerRoot(d2dNodeUUID, deleteOrignal);
		}catch (Exception e){
			logger.warn("Fail to execute GetProductionServerRoot");
			return null;
		}
	}
	
	@Override
	public long createSessionBitmap(String d2dUUID) {
		checkSession();
		logger.debug("createSessionBitmap start");
		try {
			return HAService.getInstance().createSessionBitmap(d2dUUID);
		}
		catch (Exception e) {
			logger.error("Fail to execute createSessionBitmap");
			return 1;
		}
	}

	@Override
	public long registerHeartBeatForHA(String heartBeatJobScript) {
		checkSession();
		logger.debug("registerHeartBeatForHA start");
		
		HeartBeatJobScript script = null;
		try {
			script = (HeartBeatJobScript) CommonUtil.unmarshal(heartBeatJobScript, HeartBeatJobScript.class);
		} catch (JAXBException e) {
			throw  AxisFault.fromAxisFault(e.getMessage(), e);
		}
		
		if (script == null) {
			logger.warn("Failed to registerHeartBeatForHA, heartBeatJobScript is null");
			return 0;
		}
		
		try {
			boolean ifBackupToRPS = HACommon.isBackupToRPS(script.getAFGuid());
			HAService.getInstance().setHeartBeatJobScript(script, ifBackupToRPS);
		} catch (Exception e) {
			logger.error("Failed to registerHeartBeatForHA." + e.getMessage());
			logger.error(e);
			logger.debug("registerHeartBeatForHA end");
			return 0;
		}
		
		logger.debug("registerHeartBeatForHA end");
		return HAService.getInstance().startHeartBeat(script.getAFGuid());
	}	
	
	@Override
	public long deregisterHeartBeatForHA(String afGuid, String converterID) {
		checkSession();
		return HAService.getInstance().deregisterHeartBeatForHA(afGuid, converterID);
	}	

	@Override
	public void monitorVMForHA(String afGuid) {
		checkSession();
		try {
			VSPhereFailoverService.getInstance().monitorVM(afGuid);
		} catch (Exception e) {
			logger.error("Fail to monitor VM for HA", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}	

	@Override
	public long submitIncrementalBackupForVisualStandby(String afGuid, int sessionLength, boolean firstVCMSettingFlag) {
		checkSession();
		
		return HAService.getInstance().submitIncrementalBackup(afGuid, sessionLength, firstVCMSettingFlag);
	}
	
	@Override
	public ConvertJobMonitor[] getRepJobMonitors(String d2dUUID){
		checkSession();
		try {
			return HAService.getInstance().getRepJobMonitors(d2dUUID);
		}
		catch (Exception e) {
			logger.error("Fail to execute getRepJobMonitors", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	@Override
	public VCMSavePolicyWarning[] validateNodeForVisualStandby(VCMPolicyNodeValidation nodeCheck) throws Exception{
		checkSession();
		
		try{
			return HAService.getInstance().validateNodeForVisualStandby(nodeCheck);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			String message = "";
			String errorCode = "";
			if(e instanceof ServiceException) {
				ServiceException se = (ServiceException)e;
				errorCode = se.getErrorCode();
				message = CommonService.getInstance().getServiceError(se.getErrorCode(), se.getMultipleArguments());
				if (StringUtil.isEmptyOrNull(message)) {
					message = e.getMessage();
				}
			}else{
				errorCode = FlashServiceErrorCode.Common_ErrorOccursInService;
				message = "Unhandled exception in web service";
			}
			throw AxisFault.fromAxisFault(message, errorCode);
		}
	}

	@Override
	public VCMD2DBackupInfo getVCMD2DBackupInfo(String afGuid) throws Exception {
		checkSession();
		logger.debug("getVCMD2DBackupInfo start");
		
		try {
			return HAService.getInstance().getVCMD2DBackupInfo(afGuid);
		}
		catch (Exception e) {
			logger.error("Fail to execute getVCMD2DBackupInfo", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public void updateVCMIPSettings(VCMPolicyDeployParameters deployPolicyParameters) {
		checkSession();
		
		HAService.getInstance().updateVCMIPSettings(deployPolicyParameters);
	}

	@Override
	public void saveVCMIPSettingsForHA(VCMPolicyDeployParameters deployPolicyParameters) {
		checkSession();
		
		logger.info("Update network settings on the monitor.");
		HAService.getInstance().saveVCMIPSettingsInFailoverJobScript(deployPolicyParameters);
	}
	
	@Override
	public void updateVCMSessionPassword(VCMPolicyDeployParameters deployPolicyParameters) {
		checkSession();
		
		// Fetch password list from parameter object and update session password
		List<String> passwordList = deployPolicyParameters.getSessionPasswordList();
		HAService.getInstance().updateSessionPassword(deployPolicyParameters.getInstanceUuid(), passwordList);
	}
	
	@Override
	public boolean ifNeedDeployVCMJobPolicyForMSP(String afGuid, String planUUID) {
		checkSession();
		
		// Check if need to deploy VCM policy for MSP (remote VSB) 
		return HAService.getInstance().ifNeedDeployVCMJobPolicyForMSP(afGuid, planUUID);
	}

	@Override
	public List<RpsHost> getRpsNodes() {
		// This method is used by CPM, the implement is in CPM mock.
		return null;
	}

	@Override
	public RpsPolicy4D2DRestore[] getRPSPolicyList4Restore(String hostName,
			String userName, String password, int port, String protocol) {
		checkSession();
		try {
			return SettingsService.instance().getRPSPolicyList4Restore(hostName, 
					userName, password, port, protocol);					
		}catch(ServiceException se){
			throw AxisFault.fromAxisFault(se.getMessage(), se.getErrorCode());
		}catch(Exception e){
			logger.error("failed to getRPSPolicyList", e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public List<VDSInfo> getVDSInfoList(VirtualCenter vc, String esx) {
		checkSession();
		try {
			return VSphereService.getInstance().getVDSInfoList(vc, esx);
		}catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public List<VMNetworkConfig> getVMNetworkConfigList(String rootPath, int sessionNum, String userName, String password) {
		checkSession();
		try {
			return VSphereService.getInstance().getVMNetworkConfigList(rootPath, sessionNum, userName, password);
		}catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean onlyFullManualBackup() {
		checkSession();
		try {
			return BackupService.getInstance().isOnlyFullBackup();
		}catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public int cancelJob4Reason(long jobID, int reason, String vmInstanceUUID){
		checkSession();
		try {
			CommonService.getInstance().cancelJob(jobID, reason, vmInstanceUUID);
			return 0;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public long cancelAllJobs4Reason(String vmInstanceUUID, int reason) {
		checkSession();
		try {
			CommonService.getInstance().cancelAllJobs(vmInstanceUUID, reason);
			return 0;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}


	@Override
	public boolean canRunMergeAfterBackup(String[] dependencies, JobDependencySource source) {
		checkSession();
		try {
			return BackupService.getInstance().canRunMergeNow(dependencies, source);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean canRunMergeAfterJobDone(String[] dependencies,
			JobDependencySource source) {
		checkSession();
		try {
			return BackupService.getInstance().canRunMergeNow(dependencies, source);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean canRunMergeAfterCatalog(String[] dependencies, JobDependencySource source) {
		checkSession();
		try {
			return CatalogService.getInstance().canRunMergeNow(dependencies, source);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	
	
	@Override
	public List<StandNetworkConfigInfo> getStandardNetworkConfigList(VirtualCenter vc, String esx) {
		checkSession();
		try {
			return VSphereService.getInstance().getESXStandardNetworkInfoList(vc, esx);
		}catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public String getOSVersion() {
		checkSession();
		return HAService.getInstance().getOSVersion();
	}
	
	@Override
	public RPSDataStoreInfo getDataStoreInformation(String dataStoreUUID) {
		// TODO Auto-generated method stub
		checkSession();
		try {
			return BackupService.getInstance().getDataStoreInformation(dataStoreUUID);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public FlashJobHistoryResult getJobHistory(int start, int request, FlashJobHistoryFilter filter) {
		checkSession();

		try {
			return CommonService.getInstance().getJobHistory(start, request, filter);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public FlashJobHistoryFilter getDefaultJobHistoryFilter()
	{
		return new FlashJobHistoryFilter();
	}
	
	
	public Volume[] getVolumeDetails() {
		checkSession();
		try {
			return BrowserService.getInstance().getVolumes(true, null, null, null);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public long getDataStoreStatus(RpsHost host, String dataStoreUUID) {
		checkSession();
		try {
			return SettingsService.instance().getDataStoreStatus(host, dataStoreUUID);
		} catch(WebServiceException e){
			throw e;
		} catch(ServiceException se){
			throw convertServiceException2AxisFault(se);
		} catch(Throwable t){
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public RPSDataStoreInfo getVMDataStoreInformation(VirtualMachine vm, String dataStoreUUID) {
		checkSession();
		try {
			return VSphereService.getInstance().getVMDataStoreInformation(vm, dataStoreUUID);
		} catch(WebServiceException e){
			throw e;
		} catch(ServiceException se){
			throw convertServiceException2AxisFault(se);
		} catch(Throwable t){
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean checkVCMMonitorSetting(String afGuid, String monitorUUID) {
		checkSession();
		HeartBeatJobScript heartbeatJobScript = HAService.getInstance().getHeartBeatJobScript(afGuid);
		WebServiceClientProxy client = WebServiceFactory.getFlashServiceV2(
				heartbeatJobScript.getHeartBeatMonitorProtocol(),
				heartbeatJobScript.getHeartBeatMonitorHostName(),
				heartbeatJobScript.getHeartBeatMonitorPort());


		String uuid = client.getServiceV2().validateUser(heartbeatJobScript.getHeartBeatMonitorUserName(), 
										   				 heartbeatJobScript.getHeartBeatMonitorPassword(),
										   				 VCMPolicyUtils.GetDomainNameFromUser(heartbeatJobScript.getHeartBeatMonitorUserName()));
		return monitorUUID.equals(uuid);
	}

	@Override
	public void validateHyperV(String host, String user, @NotPrintAttribute String password) {
		checkSession();
		try {
			VSphereService.getInstance().validateHyperV(host, user, password);
		} catch(WebServiceException e){
			throw e;
		} catch(ServiceException se){
			throw convertServiceException2AxisFault(se);
		} catch(Throwable t){
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",	FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public void validateHyperVAndCheckIfVMExist(String host, String user, String password, String vmInstantUUID, String vmName)
	{
		try
		{
			logger.info(String.format("uuid %s, vmname %s", vmInstantUUID, vmName));
			ArrayList<JHypervVMInfo> vmList = WSJNI.GetVmList(host, user, password, false);
			for (JHypervVMInfo vmInfo : vmList)
				if (vmInfo.getVmName().equals(vmName) && vmInfo.getVmUuid().equals(vmInstantUUID))
					throw new ServiceException(FlashServiceErrorCode.VSPHERE_HYPERV_VM_EXISTS,new Object[]{vmName});
		} catch(WebServiceException e){
			throw e;
		} catch(ServiceException se){
			throw convertServiceException2AxisFault(se);
		} catch(Throwable t){
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service GetHyperVVMList()", FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}


	@Override
	public List<String> getRegularCatalogJobScriptFiles(String serverName,
			String serverSID, String clientID) {
		checkSession();
		try {
			return CatalogService.getInstance().queryCatalogJobScript(
					serverName, serverSID, clientID);
		} catch(WebServiceException e){
			throw e;
		} catch(Throwable t){
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",	FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public String getRegularCatalogJobScript(String fileName) {
		checkSession();
		try {
			return CatalogService.getInstance().getRegularCatalogJobScript(
					fileName);
		} catch (WebServiceException e) {
			throw e;
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public void deleteRegularCatalogJobScript(String fileName) {
		checkSession();
		try {
			CatalogService.getInstance()
					.deleteRegularCatalogJobScript(fileName);
		} catch (WebServiceException e) {
			throw e;
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public void submitCatalogDependenceJob(CatalogJobArg arg) {
		checkSession();
		try {
			CatalogService.getInstance().launchCatalogDepenciesJob(arg);
		}  catch (WebServiceException e) {
			throw e;
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public VMNetworkConfig[] getHyperVAvailabeNetworkList(String hostname,
			String username, String password) {
		checkSession();

		long handle = 0;
		try
		{
			handle = HyperVJNI.OpenHypervHandle(hostname, username, password);
			Map<String, String> networks = HyperVJNI.GetVirutalNetworkList(handle);
			List<VMNetworkConfig> result = new LinkedList<VMNetworkConfig>();
			
			for (String key:networks.keySet()){
				VMNetworkConfig network = new VMNetworkConfig();
				network.setSwitchUUID(key);
				network.setLabel(networks.get(key));
				result.add(network);
			}
			return result.toArray(new VMNetworkConfig[0]);

		}catch (HyperVException e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault(e.getMessage(),FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally{
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (HyperVException e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}

	@Override
	public Disk[] getHyperVBackupVMDisk(String destination, String subPath,
			String domain, String username, String password) {
		checkSession();
		try{
			return VSphereService.getInstance().getHyperVBackupVMDisk(destination, subPath, domain, username, password);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public MachineDetail getMachineDetail(String afGuid) {
		checkSession();
		return HAService.getInstance().getMachineDetail(afGuid);
	}
	
	@Override
	public String getNodeID(){
		checkSession();
		try{
			return CommonService.getInstance().getNodeUUID();
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}		
	}
	
	@Override
	public String getMachineSId(){
		checkSession();
		try{
			return CommonService.getInstance().getServerSID();
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}		
	}

	@Override
	public List<VMNetworkConfig> getHyperVVMNetworkConfigList(String destination, String subPath, String domain, String username, String password) {
		checkSession();
		try{
			return VSphereService.getInstance().getHyperVVMNetworkConfigList(destination, subPath, domain, username, password);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public long getHyperVVMPathType(String vmPath) {
		checkSession();
		try {
			return HAService.getInstance().getNativeFacade().getHyperVVMPathType(vmPath);
		} catch (Throwable e) {
			logger.error("Fail to get vm path type.");
			logger.error(e.getMessage());
			return 0;
		}
	}
	
	@Override
	public HyperVDestinationInfo getHyperVDestInfo(List<String> vmPathList) {
		checkSession();
		try {
			HyperVDestinationInfo hypervDestInfo = new HyperVDestinationInfo();
			List<Long> resultList = new ArrayList<Long>();
			hypervDestInfo.setInvalidPathList(resultList);
			HAService.getInstance().getNativeFacade().getHyperVDestInfo(vmPathList, hypervDestInfo);
			return hypervDestInfo;
		} catch (Throwable e) {
			logger.error("Fail to get hyper-v dest info.");
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<String> getE15CASList(String userName, String password) {
		checkSession();
		return RestoreService.getInstance().getE15CASList(userName, password);
	}

	@Override
	public String getDefaultE15CAS(String userName, String password) {
		checkSession();
		return RestoreService.getInstance().getDefaultE15CAS(userName, password);
	}

	@Override
	public EsxServerInformation getEsxServerInformation(String esxServer, String username, String passwod,
			String protocol, int port) {
		checkSession();

		try {
			return HAService.getInstance().getEsxServerInformation(esxServer, username, passwod, protocol, port);
		} catch (Exception e) {
			logger.error("Failed to getVMWareServerInformation!!!", e);
			throw AxisFault.fromAxisFault("Failed to getVMWareServerInformation!!!", e);
		}
	}

	@Override
	public EsxHostInformation getEsxHostInformation(String esxServer, String username, String passwod, String protocol,
			int port, VWWareESXNode esxNode) {
		checkSession();

		try {
			return HAService.getInstance().getEsxHostInformation(esxServer, username, passwod, protocol, port, esxNode);
		} catch (Exception e) {
			logger.error("Failed to getEsxHostInformation!!!", e);
			throw AxisFault.fromAxisFault("Failed to getEsxHostInformation!!!", e);
		}
	}
	
	@Override
	public SourceNodeSysInfo getSourceNodeSysInfo() {
		checkSession();
		try {
			SourceNodeSysInfo sourceNodeSysInfo = new SourceNodeSysInfo();
			HAService.getInstance().getNativeFacade().getSourceNodeSysInfo(sourceNodeSysInfo);
			return sourceNodeSysInfo;
		} catch (Throwable t) {
			logger.error(t.getMessage());
			logger.error("Fail to get local node system information.");
			return null;
		}
	}
	
	
	@Override
	public List<String> GetHyperVVMAttachedDiskImage(String vmGuid) {
		checkSession();
		try {
			return HAService.getInstance().GetHyperVVMAttachedDiskImage(vmGuid);
		} catch (Exception e) {
			logger.error("Failed to get disk image file of VM " + vmGuid, e);
			return null;
		}
	}
	
	@Override
	public RecoveryPoint[] getRecoveryPointsByServerTimeRPSInfo(String destination,
			String domain, String userName, String pwd, String serverBeginDate,
			String serverEndDate, boolean isQueryDetail, D2DOnRPS rpsInfo) {
		checkSession();
		logger.debug("getRecoveryPointsByServerTime begin");
		logger.debug("dest:" + destination);
		logger.debug("domain:" + domain);
		logger.debug("userName:" + userName);
		logger.debug("pwd:***");
		logger.debug("serverBeginDate:" + serverBeginDate);
		logger.debug("serverEndDate:" + serverEndDate);
		logger.debug("isQueryDetail:" + isQueryDetail);
		logger.debug("rpsInfo:" + rpsInfo);
		try {
			Date beginD = string2Date(serverBeginDate);
			Date endD = string2Date(serverEndDate);
			logger.debug("beginD:" + beginD);
			logger.debug("endD:" + endD);
			RecoveryPoint[] rps = RestoreService.getInstance()
					.getRecoveryPoints(destination, domain, userName, pwd,
							beginD, endD, isQueryDetail);
			RemoteFolderConnCache.cachePathToSession(getSession(),destination, domain, userName, pwd);
			
			if(rpsInfo.getRpsHost() != null){
				Map<Long, RecoveryPoint> map = new HashMap<Long, RecoveryPoint>();
				List<RpsCatalogStatusItem> catItemList = new ArrayList<RpsCatalogStatusItem>();
				
				for(RecoveryPoint rp: rps){
					map.put(rp.getSessionID(), rp);
					RpsCatalogStatusItem ci = new RpsCatalogStatusItem();
					ci.setSessnum(rp.getSessionID());
					catItemList.add(ci);
				}
				
				IRPSService4D2D client = RPSServiceProxyManager.getServiceByHost(rpsInfo.getRpsHost());
				String agentUUID = rpsInfo.getAgentUUID();
				if( agentUUID == null){
					int from = destination.lastIndexOf("[");
					int to = destination.lastIndexOf("]");
					
					if(from>0 && (to == destination.length() - 1))
							agentUUID = destination.substring(from+1, to);
				}
				RpsCatalogJobQueryItem item = new RpsCatalogJobQueryItem();
				item.setBackupDest(destination);
				item.setDomain(domain);
				item.setUserName(userName);
				item.setPassword(pwd);
				item.setClientuuid(agentUUID);
				item.setDataStoreUUID(rpsInfo.getDataStoreUUID());
				item.setStatusItem(catItemList.toArray(new RpsCatalogStatusItem[0]));				
				
				RpsCatalogStatusItem[] catItems = client.queryCatalogStatus(item);
				
				logger.debug("queryCatalogStatus from RPS, ret:" + catItems);
				
				if (catItems != null) {
					for (RpsCatalogStatusItem catStatus : catItems) {
						RecoveryPoint rp = map.get(catStatus.getSessnum());
						if (rp != null) {
							if(catStatus.getCatalogStatus() == RecoveryPoint.FSCAT_PENDING)
								rp.setCanCatalog(false);
							rp.setFsCatalogStatus((int) catStatus.getCatalogStatus());
						} else {
							logger.debug("failed to find catatlog status for RP:" + rp.getSessionID());
						}
					}
				}
			}
	
			logger.debug("getRecoveryPointsByServerTimeRPSInfo end");

			return rps;
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int CompareHyperVVersion(String server, String userName,
			String password, String sessUserName, String sessPassword, 
			String sessRootPath, int sessNumber) {
		long handle = 0;
		checkSession();
		try
		{
			handle = getNativeFacade().OpenHypervHandle(server, userName, password);
			return WSJNI.CompareHyperVVersion(handle, sessUserName, sessPassword, sessRootPath, sessNumber);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}
		finally{
			try {
				getNativeFacade().CloseHypervHandle(handle);
			} catch (Exception e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}

	@Override
	public String getHyperVDefaultFolderOfVHD(String server, String userName,
			String password) {
		checkSession();
		return VSphereService.getInstance().getHyperVDefaultFolderOfVHD(server, userName, password);
	}

	@Override
	public String GetHyperVDefaultFolderOfVM(String server, String userName,
			String password) {
		checkSession();
		return VSphereService.getInstance().GetHyperVDefaultFolderOfVM(server, userName, password);
	}
	
	private String licenseText = "";	
	
	@Override
	public String getLicenseText() {

		if (licenseText != null && !licenseText.isEmpty())
			return licenseText;
		licenseText = "";
		String filePath = Util.getAgentHomePath() + "EULA\\";	
		String lang = DataFormatUtil.getServerLocale().getLanguage();
		String country = DataFormatUtil.getServerLocale().getCountry();
		
		if (lang.compareToIgnoreCase("de") == 0) {
			filePath = filePath + "GRM\\";
		} else if (lang.compareToIgnoreCase("es") == 0) {
			filePath = filePath + "SPA\\";
		} else if (lang.compareToIgnoreCase("fr") == 0) {
			filePath = filePath + "FRN\\";
		} else if (lang.compareToIgnoreCase("it") == 0) {
			filePath = filePath + "ITA\\";
		} else if (lang.compareToIgnoreCase("ja") == 0) {
			filePath = filePath + "JPN\\";
		} else if (lang.compareToIgnoreCase("pt") == 0) {
			filePath = filePath + "PRB\\";
		} else if (lang.compareToIgnoreCase("zh") == 0) {			
			if(country.equalsIgnoreCase("CN") || country.equalsIgnoreCase("SG"))
				filePath += "CHS\\";
			else
				filePath +=  "CHT\\";
		} else {
			filePath = filePath + "ENU\\";
		}		
		
		try {
			BufferedReader reader;
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePath + "License.txt"), "UTF-8"));
			String line;
			try {
				while ((line = reader.readLine()) != null) {
					if (line.indexOf(">") != -1)
						line = line.replaceAll(">", "&#62;");
					if (line.indexOf("<") != -1)
						line = line.replaceAll("<", "&#60;");
					licenseText += line + "<br>";
				}
			} catch (IOException e) {
				logger.error("Could not read License.txt!" , e);
			}
			try {
				reader.close();
			} catch (IOException e) {
				logger.debug("Could not close License.txt!", e);
			}
		} catch (FileNotFoundException e) {
			logger.error("Could not find License.txt!", e );
		} catch (UnsupportedEncodingException e) {
			logger.error("getLicenseText() - Do not support UTF-8!" ,e);
			e.printStackTrace();
		}

		return licenseText;
	}
	
	@Override
	public long getVMVFlashReadCache(String rootPath, int sessionNum, String userName, String password) {
		checkSession();
		try {
			return VSphereService.getInstance().getVMVFlashReadCache(rootPath, sessionNum, userName, password);
		}catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	
	}
	
	@Override
	public long getESXVFlashResource(VirtualCenter vc, ESXServer esxServer) {
		checkSession();
		try {
			return VSphereService.getInstance().getESXVFlashResource(vc, esxServer);
		}catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	
	}

	@Override
	public void removeFailoverTag(){
		checkSession();
		CommonUtil.removeFailoverTag();
	}

	@Override
	public RecoveryPointSummary[] getVMRecoveryPointSummary(
			String[] vmInstanceUUIDs) {
		checkSession();
		try {
			return VSphereService.getInstance().getRecoveryPointSummary(vmInstanceUUIDs);
		}catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int getHyperVServerType(String server, String userName,
			String password) {
		checkSession();
		return VSphereService.getInstance().getHyperVServerType(server, userName, password);
	}
	
	@Override
	public ADRConfigure getADRConfig(String backupDest, String sessionNum, String username, String password, String domain) {
		checkSession();
		if (!backupDest.endsWith("\\")) {
			backupDest = backupDest + "\\";
		}
		String sessionPath = backupDest + "VStore\\" + sessionNum;
		
		boolean isRemote = CommonUtil.isRemote(backupDest);
		ADRConfigure adr = null;
		try {
			if (isRemote) {
				try {
					HAService.getInstance().connectToRemote(backupDest, username, password, domain);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					logger.error("Failed to get adr config info.");
				}
			}
			adr = HAService.getInstance().getAdrConfigure(sessionPath);
		} finally {
			if (isRemote) {
				try {
					HAService.getInstance().closeRemoteConnect(backupDest);
				} catch (Exception e) {
					logger.error("getADRConfiguration: closeRemoteConnect", e);
				}
			}
		}
		return adr;
	}

	@Override
	public List<String> getHyperVClusterNodes(String server, String userName,
			String password) {
		checkSession();
		return VSphereService.getInstance().getHyperVClusterNodes(server, userName, password);
	}
	
	@Override
	public int validateVCloud(VCloudDirector vCloudInfo) {
		checkSession();
		try {
			return VSphereService.getInstance().validateVCloud(vCloudInfo);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public List<VCloudOrg> getVCloudOrganizations(VCloudDirector vCloudInfo) {
		checkSession();
		return VSphereService.getInstance().getVCloudOrganizations(vCloudInfo);
	}
	
	@Override
	public List<VCloudVDCStorageProfile> getStorageProfilesOfVDC(VCloudDirector vCloudInfo, String vDCId) {
		checkSession();
		return VSphereService.getInstance().getStorageProfilesOfVDC(vCloudInfo, vDCId);
	}
	
	@Override
	public List<ESXServer> getESXHosts4VAppChildVM(VCloudDirector vCloudInfo, VirtualCenter vcInfo, String vDCId, String datastoreMoRef) {
		checkSession();
		return VSphereService.getInstance().getESXHosts4VAppChildVM(vCloudInfo, vcInfo, vDCId, datastoreMoRef);
	}
	
	@Override
	public List<VMNetworkConfig> getVAppAndChildVMNetworkConfigLists(String rootPath, int sessionNum, String userName, String password) {
		checkSession();
		return VSphereService.getInstance().getVAppAndChildVMNetworkConfigLists(rootPath, sessionNum, userName, password);
	}
	
	@Override
	public String[] getVMAdapterTypes(VirtualCenter vCenter, ESXServer esxServer) {
		VWWareESXNode esxNode = new VWWareESXNode(esxServer.getEsxName(), esxServer.getDataCenter());
		return getAdapterTypes(vCenter.getVcName(), vCenter.getUsername(), vCenter.getPassword(),
				vCenter.getProtocol(), true, vCenter.getPort(), esxNode);
	}

	@Override
	public void cancelvAppChildVMJob(String vAppInstanceUUID, long jobType) {
		checkSession();
		VSphereService.getInstance().cancelvAppChildVMJob(vAppInstanceUUID, jobType);
	}
	
	@Override
	public void waitUntilvAppChildJobCancelled(String vAppInstanceUUID, long jobType) {
		checkSession();
		VSphereService.getInstance().waitUntilvAppChildJobCancelled(vAppInstanceUUID, jobType);
	}
	
	@Override
	public boolean isAllvAppChildJobCanceled(String vAppInstanceUUID, long jobType) {
		checkSession();
		return VSphereService.getInstance().isAllvAppChildJobCanceled(vAppInstanceUUID, jobType);
	}

	@Override
	public void cancelGroupJob(String groupInstanceUUID, long jobID, long jobType) {
		checkSession();
		try {
			if(groupInstanceUUID==null || groupInstanceUUID.isEmpty()){
				logger.info("Cancelling Agent side job, job id: " +  jobID);
				CommonService.getInstance().cancelJob(jobID);
			}
			else{
				logger.info("Cancelling HBBU side job, job id: " +  jobID);
				VSphereService.getInstance().cancelGroupJob(groupInstanceUUID, jobID, jobType);
			}
		}  catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}		
	}
	
	@Override
	public int cancelWaitingJobByDataStoreUUID(String datastoreUUID) {
		checkSession();
		try {
			VSphereService.getInstance().cancelWaitingJobByDataStoreUUID(datastoreUUID);
			return 0;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	
	@Override
	public List<ArchiveJobSession> getArchiveSessionsMore(int scheduleType){
		checkSession();
		try {
			return ArchiveToTapeService.getInstance().getArchiveSessionsMore(scheduleType);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}		
	}
	
	@Override
	public List<ArchiveJobSession> getVSphereArchiveSessionsMore(String vmInstanceUUID, int scheduleType){
		checkSession();
		try {
			return VSphereArchiveToTapeService.getInstance().getArchiveSessionsMore(vmInstanceUUID, scheduleType);
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}		
	}
	
	@Override
	public ADNode[] getADNodes(String destination, String userName, String password, long sessionNumber, long subSessionID, String encryptedPwd, long parentID) {
		checkSession();
		try {
			return ApplicationService.getInstance().getADNodes(destination, userName, password, sessionNumber, subSessionID, encryptedPwd, parentID);
		}catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ADAttribute[] getADAttributes(String destination, String userName, String password, long sessionNumber, long subSessionID, String encryptedPwd, long nodeID) {
		checkSession();
		try {
			return ApplicationService.getInstance().getADAttributes(destination, userName, password, sessionNumber, subSessionID, encryptedPwd, nodeID);
		}catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ADPagingResult getADPagingNodes(ADPagingConfig config, ADNodeFilter filter) {
		checkSession();
		try {
			return ApplicationService.getInstance().getADPagingNodes(config, filter);
		}catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}	
    @Override
	public ArrayList<DataSizesFromStorage> getDataSizesFromStorage(String path, String usrName, String usrPwd) {
		checkSession();	
	try {
			return BrowserService.getInstance().getNativeFacade().getDataSizesFromStorage(path, usrName, usrPwd);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
    
    private String getDomainNameFromUserName(String strUserInput){
		
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
	
	private String getUserName(String strUserInput)
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
	}
	
	private static final double REQUIRE_D2D_VERSION = 5.0;
	private static final double REQUIRE_D2D_UPDATE_NUMBER = 0;
	private static final String UNDEFINED_OPERATION_NAME = "Undefined operation name";
	
	private boolean isOlderProxyVersion(double d2dVersion, double d2dUpdateVersionNumber) {
		if((d2dVersion < REQUIRE_D2D_VERSION || 
				Math.abs(d2dVersion - 15.0) <= 0.000001 ||
				Math.abs(d2dVersion - 16.0) <= 0.000001 ||
				Math.abs(d2dVersion - 16.5) <= 0.000001 ||
				d2dVersion == REQUIRE_D2D_VERSION && d2dUpdateVersionNumber < REQUIRE_D2D_UPDATE_NUMBER)) {
			return true;
		}
		
		return false;
	}

	@Override
	public int validateProxyInfo( String hostName, String protocol, int port,
		String userName, String password, boolean isUseTimeRange,
		boolean isUseBackupSet )
	{
		checkSession();
		return this.validateProxyInfo_NoSessionCheck(
			hostName, protocol, port, userName, password, isUseTimeRange, isUseBackupSet );
	}
	
	/**
	 * The validateProxyInfoInternal was moved from LoginService of UI server side. In
	 * order to make it easy to compare with original implementation and track down
	 * issues, we keep that an independent function body and wrap web service exception
	 * process in this function.
	 * 
	 * This is a version that doesn't need session check. It will be invoked directly
	 * from Central's EdgeWebServiceImpl.
	 * 
	 * Pang, Bo (panbo01)
	 * 2015-01-14
	 */
	public int validateProxyInfo_NoSessionCheck( String hostName, String protocol, int port,
		String userName, String password, boolean isUseTimeRange,
		boolean isUseBackupSet )
	{
		try {
			return this.validateProxyInfoInternal(
				hostName, protocol, port, userName, password, isUseTimeRange, isUseBackupSet );
		}catch(ServiceException e) {
			throw convertServiceException2AxisFault(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	private int validateProxyInfoInternal( String hostName, String protocol, int port,
		String userName, String password, boolean isUseTimeRange,
		boolean isUseBackupSet ) throws ServiceException
	{
		int ret = 0;
		String name = hostName;
		String usernameOld = userName;
		String username = getUserName(usernameOld);
		String domain = getDomainNameFromUserName(usernameOld);
		
		/**
		 * qiubo01: To fix issue - EDGE R16 update 4 create/edit vSphere policy failed with old version proxy.
		 * This is because IFlashServiceV2 is not a compatible interface.
		 * When validate a proxy with old version, the web service initialization will fail.
		 * Instead, use compatible interface ID2D4EdgeVSphere.
		 */
		try
		{
			ID2D4EdgeVSphere service = ServiceProviders.getRemoteFlashServiceProvider().create(protocol, name, port, ServiceInfoConstants.SERVICE_ID_D2D_FOR_EDGE).getServiceForEdgeVSphere();
			service.validateUser(username, password, domain);
			//check the SaaS node
			VersionInfo versionInfo = service.getVersionInfo();

			double d2dVersion = 0;
			double d2dUpdateVersionNumber = 0;
			try{
				d2dVersion = Double.parseDouble(versionInfo.getMajorVersion()+ "." + versionInfo.getMinorVersion());
			}
			catch(Exception e){
				d2dVersion = 0;
			}
			try{
				d2dUpdateVersionNumber = Double.parseDouble(versionInfo.getUpdateNumber());
			}
			catch(Exception e){
				d2dUpdateVersionNumber = 0;		
			}
			//check version and policy info
			if (isOlderProxyVersion(d2dVersion, d2dUpdateVersionNumber)) {
				if(isUseTimeRange || isUseBackupSet){
					throw new ServiceException("", FlashServiceErrorCode.VSPHERE_LOWER_VERSION_PROXY_WITH_NEW_POLICY);
				}
			}
			else{
				if((versionInfo!=null)&&(versionInfo.getProductType()!=null)&&(versionInfo.getProductType().equals("1"))){
					throw new ServiceException("", FlashServiceErrorCode.VSPHERE_PROXY_IS_SAAS_NODE);
				}else{
					//ret = service.checkVIXStatus();
				}
			}
				
		}catch (WebServiceException exception){
			logger.error("Error occurs during validateProxyInfo can't connect that webservice");
			/*if (exception.getCause() instanceof ConnectException || exception.getCause() instanceof SocketException
					|| exception.getCause() instanceof UnknownHostException	|| exception.getCause() instanceof SSLHandshakeException || exception.getCause() instanceof FileNotFoundException) {
					String locale = this.getServerLocale();
					throw new ServiceConnectException(
							FlashServiceErrorCode.Common_CantConnectService,
							ResourcesReader.getResource("ServiceError_"
									+ FlashServiceErrorCode.Common_CantConnectService,
									locale));
				}*/
			if(exception.getCause() instanceof UnknownHostException){
				throw new ServiceException("", FlashServiceErrorCode.Common_CantConnectRemoteServer);
			}
			if(exception.getCause() instanceof ConnectException || exception.getCause() instanceof SSLException || exception.getCause() instanceof SocketException){
				throw new ServiceException("", FlashServiceErrorCode.Common_CantConnectService);
			} 
			if (exception.getCause() instanceof Error && exception.getMessage().startsWith(UNDEFINED_OPERATION_NAME)) {
				throw new ServiceException("", FlashServiceErrorCode.EDGE_D2D_INTERFACE_MISMATCH);
			}
			
			if (exception instanceof SOAPFaultException) {
				SOAPFaultException se = (SOAPFaultException) exception;
				if (se.getFault() != null
						&& (FlashServiceErrorCode.Login_WrongCredential
								.equals(se.getFault().getFaultCodeAsQName()
										.getLocalPart()))) {
					throw new ServiceException("", FlashServiceErrorCode.VSPHERE_PROXY_CREDENTIAL);
				}
				
				if (se.getFault() != null
						&& (FlashServiceErrorCode.Login_NotAdministrator
										.equals(se.getFault().getFaultCodeAsQName()
												.getLocalPart()))) {
					throw new ServiceException("", FlashServiceErrorCode.VSPHERE_PROXY_USER_NOT_ADMINISTRATOR);
				}
			}
			//proccessAxisFaultException(exception);
		}
		return ret;
	}
	
	@Override
	public String register4Console(EdgeRegInfo edgeRegInfo, boolean forceRegFlag) {
		checkSession();

		D2DRegServiceImpl regImpl = new D2DRegServiceImpl();

		String result = regImpl.register4Console(edgeRegInfo,forceRegFlag);
		return result;
	}

	@Override
	public JobMonitor[] getWaitingJobTable(String vmInstanceUuid) {
		checkSession();
		try {
			
			return VSphereService.getInstance().getWaitingJobTable(vmInstanceUuid);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long saveArchiveDelConfiguration(
			ArchiveConfiguration archiveDelConfig) {
		checkSession();
		try {
			if(archiveDelConfig!=null&&archiveDelConfig.isbArchiveAfterBackup())
				return DeleteArchiveService.getInstance().saveArchiveDelConfiguration(archiveDelConfig);
			else 
				return DeleteArchiveService.getInstance().removeArchiveDelConfiguration();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public synchronized int collectDiagnosticInfo(DiagInfoCollectorConfiguration config)
	{
		checkSession();
		
		logger.info("Entered into collectDiagnosticInfo from agent side");
		
		//Validation: check the destination is a shared path. If shared path, the validation already happen on console side. Hence Ignore here.
		//If its local path, check the volume exist. The other validation and creation of sub folder done by the diag utility and hence ignore.
		
		if(!config.getUploadDestination().startsWith("\\\\")){
			if(!validateVol(config.getUploadDestination())){
				String msg = String.format(WebServiceMessages.getResource("DiagUtilityCouldNotStart", config.getUploadDestination()));
				new NativeFacadeImpl().addLogActivity(
						Constants.AFRES_AFALOG_ERROR,
						Constants.AFRES_AFJWBS_GENERAL,
						new String[] { msg, "", "",	"", "" });
				return -1; //-1 = could not start the utility
			}
		}
				
		return CollectDiagnosticInfoService.getInstance().collectDiagnosticInfo(config);
		
	}
	
	@Override
	public synchronized DiagInfoCollectorConfiguration getDiagInfoFromXml()
	{
		checkSession();
		logger.info("Entered into getDiagInfoFromXml from agent side");
		return CollectDiagnosticInfoService.getInstance().getDiagInfoFromXml();
	}
	
	private boolean validateVol(String uploadDestination)  
	{
		String volLable = uploadDestination.split(":")[0];
		
		logger.info("The volumen lable used for uploading the log collection info: " + volLable);
		
		String volume = volLable + ":\\";
		
		if(volLable.trim().length()==1){
			Volume[] volumes = getVolumes();
			
			for(Volume vol: volumes){
				if(vol.getName().equalsIgnoreCase(volume))
					return true;
			}
		}
			
		return false;
		
	}


	private static final int UPDATE_ADMIN_ACCOUNT_OK=0;

	@Override
	public int updateAdminAccount(String userName, String password){
	checkSession();		
		try{
				if(userName==null)throw new ServiceException("The userName is null",FlashServiceErrorCode.BackupConfig_ERR_NullPostUsername);
				if(password==null)throw new ServiceException("The password is null",FlashServiceErrorCode.BackupConfig_ERR_NullPostUserPassword);		
				String user=null;
				String domain="";
				if(userName.contains("\\")){
					String[] subs=userName.split("\\\\");
					if(subs.length==2){
						domain=subs[0];
						user=subs[1];
						if(user.length()==0||domain.length()==0)
							throw new ServiceException("The username or domain is invalid",FlashServiceErrorCode.BackupConfig_ERR_INVALID_USER); 
					}
					else{
						throw new ServiceException("The username is illegal",FlashServiceErrorCode.BackupConfig_ERR_INVALID_USER);
					}
				}else {
					user=userName;
				}
				if(user.length()>=256||domain.length()>=256||password.length()>=256){
					throw new ServiceException("The username or domain or password is too long",FlashServiceErrorCode.BackupConfig_ERR_INVALID_USER); 
				}else {
					Account newaccount = new Account();
					newaccount.setUserName(userName);
					newaccount.setPassword(password);
					getNativeFacade().validateAdminAccount(newaccount);
					logger.info("Update Admin Account with new account");
					getNativeFacade().saveAdminAccount(newaccount);
					return UPDATE_ADMIN_ACCOUNT_OK;
				}			
		}catch(ServiceException e) {
			logger.error("Failed to update admin account.", e);
			throw convertServiceException2AxisFault(e);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	

	@Override
	public long saveScheduledExportConfigurationEx(BackupVM vm, ScheduledExportConfiguration configuration, 
			                                       VSphereBackupConfiguration vsphereCfg)
	{
		checkSession();

		try {
			VMCopyService.getInstance().saveScheduledExportConfiguration(vm, configuration, vsphereCfg);
		} catch (ServiceException e) {
			//wanqi06
			ConvertErrorCodeUtil.checkScheduleConfigurationConvert(e);
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}

		return 0;
	}



	@Override
	public List<PolicyDeploymentError> deployD2DConfigurationFromEdge(D2DConfiguration configuration,String planUUID,String edgeUUID){
		checkSession();
		return D2DPolicyManagementServiceImpl.getInstance().deployPolicy(configuration, planUUID, edgeUUID);
	}

	@Override
	public SavePolicyWarning[] saveHBBUConfiguration(HBBUConfiguration hbbuConfig)
	{
		logger.info("entered saveHBBUConfiguration");

		boolean isAMD64 = true;
		short cpu = GetHostProcessorArchitectural();
		logger.info("saveHBBUConfiguration cup is " + cpu);
		if(cpu != 9){
			isAMD64 = false;
			throw AxisFault.fromAxisFault(WebServiceMessages.getResource("DROPE_32BIT_PROXY"),
							FlashServiceErrorCode.Common_ErrorOccursInService);
		}

		if (hbbuConfig.getVSphereBackupConfiguration().getPlanUUID() == null)
			hbbuConfig.getVSphereBackupConfiguration().setPlanUUID(
							hbbuConfig.getPlanUUID());

		SavePolicyWarning[] warning = saveVSphereBackupConfiguration(hbbuConfig
										.getVSphereBackupConfiguration());

		BackupVM[] backupVMs = hbbuConfig.getVSphereBackupConfiguration()
						.getBackupVMList();
		for (BackupVM vm : backupVMs)
			this.saveScheduledExportConfigurationEx(vm,
							hbbuConfig.getScheduledExportConfiguration(),
							hbbuConfig.getVSphereBackupConfiguration());

		Map<String, String> policyUuids = new HashMap<String, String>();
		for (BackupVM vm : backupVMs)
		{
			policyUuids.put(vm.getInstanceUUID(), hbbuConfig.getPlanUUID());
		}

		new D2DEdgeRegistration().SavePolicyUuid2Xml(
						ApplicationType.vShpereManager, policyUuids);

		logger.info("leaving saveHBBUConfiguration");
		return warning;
	}

	@Override
	public ArchiveDestinationVolumeConfig[] getArchiveDestinationItemsFromRPS(
			RpsHost rpsInfo, String catalogPath, String catalogDirUserName, String catalogDirPassword, String hostName,
			ArchiveDestinationConfig destInfo) {
		try {
			//ArchiveUtil.decryptRpsHost(rpsInfo);
			IRPSService4D2D client=RPSServiceProxyManager.getServiceByHost(rpsInfo);
			ArchiveDestinationVolumeConfig[] result=client.getArchiveDestinationItems(catalogPath, catalogDirUserName, catalogDirPassword, hostName, destInfo);
			return result;
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public ArchiveCatalogItem[] getArchiveCatalogItemsFromRPS(RpsHost rpsInfo,
			String catalogPath, String catalogDirUserName, String catalogDirPassword, String hostName,ArchiveDestinationConfig destInfo, String strVolume,
			String strPath, long index, long count) {
		try {
			//ArchiveUtil.decryptRpsHost(rpsInfo);
			IRPSService4D2D client=RPSServiceProxyManager.getServiceByHost(rpsInfo);
			ArchiveCatalogItem[] result=client.getArchiveCatalogItems(catalogPath, catalogDirUserName, catalogDirPassword, hostName, destInfo,strVolume, strPath, index, count);
			return result;
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override	
   public ArchiveCatalogItem[] getAllArchiveCatalogItemsFromRPS(RpsHost rpsInfo, String catalogPath, String catalogDirUserName, String catalogDirPassword, String hostName, ArchiveDestinationConfig destInfo, String strVolume,String strPath){
		try {
			//ArchiveUtil.decryptRpsHost(rpsInfo);
			IRPSService4D2D client=RPSServiceProxyManager.getServiceByHost(rpsInfo);
			ArchiveCatalogItem[] result=client.getAllArchiveCatalogItems(catalogPath, catalogDirUserName, catalogDirPassword, hostName, destInfo,strVolume, strPath);
			return result;
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	@Override
	public long getArchiveCatalogItemsCountFromRPS(RpsHost rpsInfo, String catalogPath, String catalogDirUserName, String catalogDirPassword,String hostName, ArchiveDestinationConfig destInfo, String strVolume,String strPath){
		try {
			//ArchiveUtil.decryptRpsHost(rpsInfo);
			IRPSService4D2D client=RPSServiceProxyManager.getServiceByHost(rpsInfo);
			return client.getArchiveCatalogItemsCount(catalogPath, catalogDirUserName, catalogDirPassword, hostName, destInfo,strVolume, strPath);
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public String[] registerEntitlementDetails(String name, String company, String contactNumber, String emailID, String netSuiteId) {
		// TODO Auto-generated method stub
		String[] response = new String[2];
		checkSession();
		try
		{
			logger.info("Invoking AERPClient to register details " + this.getClass().getName());
			String responseCode = EntitlementRegister.registerEntitlement(name, company, contactNumber, emailID, netSuiteId);
			logger.info("Entitlement Register response " + responseCode);
			if(responseCode != null && (responseCode.equalsIgnoreCase("REGISTRATION_SUCCESS")|| responseCode.equalsIgnoreCase("REGISTRATION_SUCCESS_LINK")))
			{
				response[0] = WebServiceMessages.getResource("REGISTRATION_SUCCESS_AERP");	
				response[1] = "true";
			}
			else
			{
				response[0] = WebServiceMessages.getResource(responseCode);
				response[1] = "false";
			}
			logger.info("Entitlement Register Service, Success " + this.getClass().getName());
		}
		catch(Exception e)
		{
			logger.error("Entitlement Register failed, Service Error " + this.getClass().getName() + " " + e.getMessage());
		}
		return response;
	}
	
	@Override
	public String cancelRegistration(String name, String company, String contactNumber, String emailID, String netSuiteId) {
		// TODO Auto-generated method stub
		String responseCode = "";
		checkSession();
		try
		{
			logger.info("Invoking AERPClient to cancel registration details " + this.getClass().getName());
			responseCode = EntitlementRegister.cancelRegistration(name, company, contactNumber, emailID, netSuiteId);
			logger.info("Cancel Register response " + responseCode);
			if(responseCode != null && (responseCode.equalsIgnoreCase("CANCELREGISTRATION_SUCCESS")))
			{	
				logger.info("Stopping AERPJob for Cancel Registration " + responseCode);
				if(AERPService.getInstance().isAERPJobTriggered())
				{
					AERPService.getInstance().terminateAERPJob();
					logger.info("Cancel Register response " + responseCode);
				}
			}
			logger.info("Cancel registration Service, Success " + this.getClass().getName());
		}
		catch(Exception e)
		{
			logger.error("Cancel Register failed, Service Error " + this.getClass().getName() + " " + e.getMessage());
		}
		return responseCode;
	}
	
	@Override
	public String submitAERPJob() {
		checkSession();
		String responseCode = "SUBMITAERPJOB_FAILED";
		try {
			HashMap<String, String> entitlementFreq = new EntitlementRegisterUtility().getEntitlementFrequencies();
			if(entitlementFreq != null && entitlementFreq.containsKey("uploadfrequency") && entitlementFreq.containsKey("uploadTimeStamp"))
	    	{
				AERPService.getInstance().submitAERPJob(Integer.parseInt(entitlementFreq.get("uploadfrequency").trim()), entitlementFreq.get("uploadTimeStamp").trim());
				responseCode= "SUBMITAERPJOB_SUCCESS";
	    	}
		} catch (Exception e) {
			logger.error("Error submitting AERPJob " + this.getClass().getName() + " " + e.getMessage());
		}
		return WebServiceMessages.getResource(responseCode);
	}
	
	@Override
	public String isActivated()
	{
		String response = "ISACTIVATED_EXCEPTION";
		checkSession();
		try
		{
			logger.info("Invoking isActivated service");
			response = EntitlementRegister.isActivated();
			logger.info("isActivated Service response " + response);
			if((response.equalsIgnoreCase("ISACTIVATED_ACTIVE") || response.equalsIgnoreCase("ISACTIVATED_INACTIVE"))
					&& !AERPService.getInstance().isAERPJobTriggered())  
			{
				logger.info("Triggering  submitAERPJob for isActivated response " + response);
				submitAERPJob();
			}
		}
		catch(Exception e)
		{
			logger.error("Exception Invoking isActivated Service" + e.getMessage());
		}
		return response;
	}
		
	@Override
	public ArchiveConfiguration getArchiveDelConfiguration(){
		checkSession();
		try {
			return DeleteArchiveService.getInstance().getArchiveDelConfiguration();
		} catch (ServiceException e) {
			throw convertServiceException2AxisFault(e);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	} 
	
	@Override
	public List<RpsArchiveConfiguationWrapper> getRpsArchiveConfiguationSummary() {
		checkSession();
		List<RpsArchiveConfiguationWrapper> result = new ArrayList<RpsArchiveConfiguationWrapper>();
		try {
			result = ArchiveService.getInstance()
					.getRpsArchiveConfiguationSummary();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return result;
	}
	
	@Override
	public String getNodeUUID() {
		checkSession();
		return CommonService.getInstance().getNodeUUID();
	}
	
	@Override
	public LoginDetail validateUserByUUIDWithDetail(String uuid,String logindetail){
		//need not to check session;
		try {
			return getCommonUtil().validateUserByUUIDWithDetail(uuid, logindetail);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			throw convertServiceException2AxisFault(e);
		}catch(SOAPFaultException e){
			throw e;
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public LoginDetail validateUserWithDetail(String username, String password, String domain,String logindetail){
		//need not to check session;
		try {
			return getCommonUtil().validateUserWithDetail(username, password, domain, logindetail);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			throw convertServiceException2AxisFault(e);
		}catch(SOAPFaultException e){
			throw e;
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public void startFilecopyNow() 
	{
		checkSession();
		try {
			logger.info("Submitting filecopy now job");
			ArchiveService.getInstance().submitArchiveJob(true);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public void startFileArchiveNow() 
	{
		checkSession();
		try {
			logger.info("Submitting file archive now job");
			DeleteArchiveService.getInstance().submitArchiveSrcDeleteJob();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
}
