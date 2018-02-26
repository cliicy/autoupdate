package com.ca.arcflash.webservice.service;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.jni.common.JJobHistoryResult;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.listener.manager.ListenerManager;
import com.ca.arcflash.service.common.CatalogQueueType;
import com.ca.arcflash.service.common.WebServiceErrorMessages;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.service.jni.model.JActivityLog;
import com.ca.arcflash.service.jni.model.JActivityLogResult;
import com.ca.arcflash.service.jni.model.JMergeActiveJob;
import com.ca.arcflash.service.jni.model.JSystemInfo;
import com.ca.arcflash.service.util.ActivityLogConverter;
import com.ca.arcflash.service.util.JobHistoryConverter;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.D2DTime;
import com.ca.arcflash.webservice.data.EdgeInfo;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.JobMonitorHistoryItem;
import com.ca.arcflash.webservice.data.LicInfo;
import com.ca.arcflash.webservice.data.MountSession;
import com.ca.arcflash.webservice.data.NetworkPath;
import com.ca.arcflash.webservice.data.TrustedHost;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.data.PM.AutoUpdateSettings;
import com.ca.arcflash.webservice.data.PM.GeneralSettings;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.activitylog.ActivityLog;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.CloudProviderInfo;
import com.ca.arcflash.webservice.data.backup.Account;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.backup.CloudVendor;
import com.ca.arcflash.webservice.data.backup.D2DConfiguration;
import com.ca.arcflash.webservice.data.backup.RetryPolicy;
//October Sprint 
import com.ca.arcflash.webservice.data.vsphere.StorageAppliance;
import com.ca.arcflash.webservice.data.backup.SRMPkiAlertSetting;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.edge.datasync.d2d.D2DInfo;
import com.ca.arcflash.webservice.data.edge.datasync.d2d.D2DStatus;
import com.ca.arcflash.webservice.data.edge.datasync.d2d.D2DType;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.data.job.history.FlashJobHistoryFilter;
import com.ca.arcflash.webservice.data.job.history.FlashJobHistoryResult;
import com.ca.arcflash.webservice.data.listener.FlashListenerInfo;
import com.ca.arcflash.webservice.data.listener.FlashListenerInfo.ListenerType;
import com.ca.arcflash.webservice.data.merge.MergeAPISource;
import com.ca.arcflash.webservice.data.merge.MergeJobMonitor;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.edge.data.notify.NotifyMessage;
import com.ca.arcflash.webservice.edge.data.notify.NotifyMessageConstants;
import com.ca.arcflash.webservice.edge.srmagent.SrmAgentServerImpl;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.jni.model.JJobContext;
import com.ca.arcflash.webservice.jni.model.JMountSession;
import com.ca.arcflash.webservice.scheduler.BaseJob;
import com.ca.arcflash.webservice.scheduler.BaseVSphereJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.EmailSenderForManualMergeJob;
import com.ca.arcflash.webservice.scheduler.MoveLogJob;
import com.ca.arcflash.webservice.scheduler.RemoteVCMSessionMonitor;
import com.ca.arcflash.webservice.scheduler.VSphereMergeJob;
import com.ca.arcflash.webservice.service.internal.BackupConfigurationXMLDAO;
import com.ca.arcflash.webservice.service.internal.CloudVendorXMLDAO;
import com.ca.arcflash.webservice.service.internal.RetryPolicyXMLDAO;
//October sprint 
import com.ca.arcflash.webservice.service.internal.StorageApplianceXMLDAO;
import com.ca.arcflash.webservice.service.internal.TrustedHostXMLDAO;
import com.ca.arcflash.webservice.service.validator.BackupConfigurationValidator;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcflash.webservice.util.ConvertErrorCodeUtil;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.TheadPoolManager;
import com.ca.arcflash.webservice.util.WebServiceMessages;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public final class CommonService extends BaseService {

	public static final String REGISTRY_RPS_FLAG = "RPSServerFlag";
	public static final int JOBMONITOR_HISTORY_COUNT = 183; // history buffer
															// size in seconds;

	private static final int maxUuidLen = 50;
	private static final int maxUsernameLen = 256;
	private static final int maxDomainLen = 256;
	private static final int maxPasswordLen = 256;

	private static final Logger logger = Logger.getLogger(CommonService.class);
	private static final CommonService instance = new CommonService();

	private static final LinkedList<JobMonitorHistoryItem> jobMonitorHistory = new LinkedList<JobMonitorHistoryItem>();
	private static TrustedHost localAsTrusthost;

	private ActivityLogConverter activityLogConverter = new ActivityLogConverter();
	private TrustedHostXMLDAO trustedHostDAO = new TrustedHostXMLDAO();

	private static final Map<String, RepJobMonitor> repJobMonitors = new TreeMap<String, RepJobMonitor>();
	private static final Map<String, Lock> repJobLocks = new TreeMap<String, Lock>();

	private static final Map<String, D2DStatusInfo> repD2DStatusInfo = new TreeMap<String, D2DStatusInfo>();

	private BackupConfigurationXMLDAO backupConfigurationXMLDAO = new BackupConfigurationXMLDAO();
	private BackupConfigurationValidator backupConfigurationValidator = new BackupConfigurationValidator();
	private PreferencesConfiguration preferences = null;
	private static List<String> localhostIPList;

	private RetryPolicyXMLDAO retryPolicyXMLDAO = new RetryPolicyXMLDAO();
	//October Sprint - 
	private StorageApplianceXMLDAO storageApplianceXMLDAO = new StorageApplianceXMLDAO();
	private CloudVendorXMLDAO cloudVendorXMLDAO = new CloudVendorXMLDAO();
	public static String RETRY_BACKUP = "Backup";
	public static String RETRY_CATALOG = "Catalog";
	public static String RETRY_FILECOPY = "FileCopy";
	public static String RETRY_FILEARCHIVE = "FileArchive";

	public static final int ENCRYPTION_LIC = 0;
	public static final int SCHEDULEDEXPORT_LIC = 1;
	public static final int EX_DB_LIC = 2;
	public static final int EX_GR_LIC = 3;
	public static final int FILE_COPY_2D_LIC = 4;

	public static final int AFLIC_INFO_ID_ENCRYPTION = 0x00000001;
	public static final int AFLIC_INFO_ID_D2D2D = 0x00000002;
	public static final int AFLIC_INFO_ID_SCHEDULE_EXPORT = 0x00000004;
	public static final int AFLIC_INFO_ID_BLI = 0x00000008;
	public static final int AFLIC_INFO_ID_HYPERVM = 0x00000010;
	public static final int AFLIC_INFO_ID_SQL = 0x00000020;
	public static final int AFLIC_INFO_ID_EXCH_DB = 0x00000040;
	public static final int AFLIC_INFO_ID_EXCH_GR = 0x00000080;
	public static final int AFLIC_INFO_ID_MASK = 0x000000FF;
	
	public static final int LICENSE_BASE_SUCCESS           		= 0x00;            // license stats is ok
	public static final int LICENSE_BASE_ERR_WORKSTATION   		= 0x01;            // need workstation license
	public static final int LICENSE_BASE_ERR_STANDARD_SOCKET 	= 0x02;             // need STANDARD_Per_SOCKET license
	public static final int LICENSE_BASE_ERR_ADVANCED_SOCKET 	= 0x03;            // need ADVANCED_Per_SOCKET license
	public static final int LICENSE_BASE_WARN_TRIAL            	= 0x04;             // is using trial license  
	public static final int LICENSE_BASE_NCE            		= 0x10;           // no change edition
	
	private static final String SETUP_REBOOT_FLAG_FILE = "as_reboot_d2d.ini";  //this is internel file of setup. Don't modify or delete it

	private String d2dServerSID = null;

	private JobMonitorService jms = JobMonitorService.getInstance();

	static {
		try {
			localAsTrusthost = new TrustedHost();
			localAsTrusthost.setName(ServiceContext.getInstance().getLocalMachineName());
			localAsTrusthost.setPort(0);
			localAsTrusthost.setType(0);

			String majorVersion = instance.getVersionInfoInternal().getMajorVersion();
			if (majorVersion != null && !majorVersion.isEmpty()) {
				localAsTrusthost.setD2dVersion(Integer.parseInt(majorVersion));
			}
		} catch (Exception e) {
			logger.error("Failed to add trust host:" + e);
		}
	}

	private CommonService() {

	}

	protected boolean validateLoginInfoFormat(String username, String password, String domain) throws ServiceException {
		if (null != username && username.length() > maxUsernameLen)
			throw generateAxisFault(FlashServiceErrorCode.Login_WrongCredential);

		if (null != password && password.length() > maxPasswordLen)
			throw generateAxisFault(FlashServiceErrorCode.Login_WrongCredential);

		if (null != domain && domain.length() > maxDomainLen)
			throw generateAxisFault(FlashServiceErrorCode.Login_WrongCredential);

		return true;
	}

	public boolean isUuidFormatValid(String uuid) throws ServiceException {
		if (null != uuid && uuid.length() > maxUuidLen) {
			throw generateAxisFault(FlashServiceErrorCode.Login_WrongCredential);
		}

		return true;
	}

	public static CommonService getInstance() {
		return instance;
	}

	public void resumeJobOrStartJobInQueue() {
		ArrayList<JJobContext> jobs = new ArrayList<JJobContext>();
		long ret = WSJNI.getActiveJobs(jobs);
		if (ret != 0) {
			logger.error("Failed to get active jobs from backend for error " + ret);
		} else {
			for (JJobContext job : jobs) {
				BaseJob.resumeJobAfterRestart(job);
			}
		}
		logger.info("We try to launch jobs in queue");
		CatalogService.getInstance().startRegularJob(true, "");
		CatalogService.getInstance().startOnDemandCatalogJob(-1);

		// wanqi06
		List<JMergeActiveJob> mergeJobs = getNativeFacade().getActiveMergeJobInfo();
		for (int i = 0; i < mergeJobs.size(); i++) {
			if (mergeJobs.get(i).getJobType() != 0)
				continue;

			int jobID = mergeJobs.get(i).getJobId();
			String vmUUID = mergeJobs.get(i).getVmInstanceUUID();
			if (vmUUID == null || vmUUID.isEmpty())
				MergeService.getInstance().resumeJobAfterWSRestart(jobID);
			else
				VSphereMergeService.getInstance().resumeJobAfterWSRestart(jobID, vmUUID);

		}

	}

	public VersionInfo getVersionInfoInternal() {
		TimeZone tz = Calendar.getInstance().getTimeZone();
		VersionInfo version = new VersionInfo();
		version.setTimeZoneID(tz.getID());
		version.setTimeZoneOffset(tz.getOffset(System.currentTimeMillis()));
		version.setLocale(DataFormatUtil.getServerLocale().getLanguage());
		version.setCountry(DataFormatUtil.getServerLocale().getCountry());
		version.setDataFormat(CommonNativeInstance.getICommonNative().getDateTimeFormat());

		try {
			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(RegConstants.REGISTRY_VERSION_ROOTKEY);
			version.setMajorVersion(registry.getValue(handle, RegConstants.REGISTRY_KEY_MAJORVERSION));
			version.setMinorVersion(registry.getValue(handle, RegConstants.REGISTRY_KEY_MINORVERSION));
			version.setBuildNumber(registry.getValue(handle, RegConstants.REGISTRY_KEY_BUILDNUMBER));
			version.setUpdateNumber(registry.getValue(handle, RegConstants.REGISTRY_KEY_UPDATENUMBER));
			version.setProductType(registry.getValue(handle, RegConstants.REGISTRY_KEY_PRODUCTTYPE));
			version.setDisplayVersion(registry.getValue(handle, RegConstants.REGISTRY_KEY_DisplayVersion));
			version.setUpdateBuildNumber(registry.getValue(handle, RegConstants.REGISTRY_KEY_UPDATEBUILDNUMBER));	
			registry.closeKey(handle);
			version.setUefiFirmware(this.getNativeFacade().IsFirmwareuEFI());
		} catch (Exception e) {
			logger.error("Read registry error", e);
		}
		return version;
	}

	private void getOSName(VersionInfo version) {
		try {
			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(RegConstants.REGISTRY_OS_NAME);
			version.setOsName(registry.getValue(handle, RegConstants.REGISTRY_KEY_OS_NAME));
			registry.closeKey(handle);
		} catch (Exception e) {
			logger.error("Read registry error", e);
		}
	}

	public VersionInfo getVersionInfo() {
		logger.debug("getVersionInfo() - start");

		VersionInfo version = getVersionInfoInternal();
		try {
			logger.debug("getAdminAccount - start");
			Account account = getNativeFacade().getAdminAccount();
			version.setAdminName(account.getUserName());
			logger.debug("getAdminAccount - return UserName: " + account.getUserName());
		} catch (Exception e) {
			logger.error("getAdminAccount error", e);
		}

		logger.debug("Admin.name" + version.getAdminName());

		try {
			Volume[] volumes = BrowserService.getInstance().getVolumes(false, null, null, null);
			logger.debug("volume size:" + (volumes == null ? 0 : volumes.length));

			List<String> volumeList = new ArrayList<String>();
			if (volumes != null && volumes.length > 0) {
				for (int i = 0, count = volumes.length; i < count; i++) {
					String name = volumes[i].getName();
					volumeList.add(name);
					// if(name.length() > 1 && name.indexOf(":") == 1)
					// letters.append(name.charAt(0));
					// else if(name.length() == 1)
					// letters.append(name);
					// else
					// logger.error("Unrecognized driver:" + name);
				}
			}
			version.setLocalDriverLetters(volumeList.toArray(new String[0]));

			// check Settings configed.
			boolean isSettingsExist = StringUtil.isExistingPath(ServiceContext.getInstance().getBackupConfigurationFilePath());
			version.setSettingConfiged(isSettingsExist);

		} catch (Exception e) {
			logger.error("get local driver letters error", e);
		}

		try {
			JSystemInfo systemInfo = getNativeFacade().getSystemInfo();
			if (systemInfo != null) {
				version.setDedupInstalled(systemInfo.isDedupInstalled());
				version.setWin8(systemInfo.isWin8());
				version.setReFsSupported(systemInfo.isReFsSupported());
			}
		} catch (Exception e) {
			logger.error("get system info error", e);
		}
		logger.debug("getOSName start");
		getOSName(version);
		logger.debug("getOSName end");

		logger.debug("getLocalADTPackage start");
		int localADTPackage = getLocalADTPackage();
		version.setLocalADTPackage(localADTPackage);
		logger.debug("getLocalADTPackage end:" + localADTPackage);

		logger.debug("getEdgeInfoForApp start");
		getEdgeInfoForApp(version);
		logger.debug("getEdgeInfoForApp end");

		version.setD2DInstalled(isD2DInstalled());
		version.setRPSInstalled(isRPSServer());
		version.setSQLServerInstalled(isSQLServerInstalled());
		version.setExchangeInstalled(isExchangeServerInstalled());

		// log the result before return
		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertObject2String(version));

		logger.debug("getVersionInfo() - end");
		return version;
	}

	private void getEdgeInfoForApp(VersionInfo versionInfo) {
		D2DEdgeRegistration registration = new D2DEdgeRegistration();
		EdgeRegInfo regInfo = registration.getEdgeRegInfo(ApplicationType.vShpereManager);
		addEdgeInfo(regInfo, versionInfo);

		regInfo = registration.getEdgeRegInfo(ApplicationType.CentralManagement);
		addEdgeInfo(regInfo, versionInfo);

		regInfo = registration.getEdgeRegInfo(ApplicationType.VirtualConversionManager);
		addEdgeInfo(regInfo, versionInfo);
	}

	private void addEdgeInfo(EdgeRegInfo regInfo, VersionInfo version) {
		if (regInfo == null)
			return;
		EdgeInfo edgeInfo = new EdgeInfo();
		edgeInfo.setEdgeHostName(getEdgeHostName( regInfo ));
		edgeInfo.setEdgeUrl(getEdgeUrl(regInfo));
		if (regInfo.getEdgeAppType() == ApplicationType.vShpereManager) {
			version.setEdgeInfoVS(edgeInfo);
		} else if (regInfo.getEdgeAppType() == ApplicationType.CentralManagement) {
			version.setEdgeInfoCM(edgeInfo);
		} else if (regInfo.getEdgeAppType() == ApplicationType.VirtualConversionManager) {
			version.setEdgeInfoVCM(edgeInfo);
		}
	}
	
	protected String getEdgeHostName( EdgeRegInfo regInfo ) {
		// fix issue 761394
		// upgrade Agent, don't show the host name of managed Console
		try {
			if(!StringUtil.isEmptyOrNull(regInfo.getConsoleUrl())){
				URL consoleUrl = new URL( regInfo.getConsoleUrl() );
				return consoleUrl.getHost();
			} 
			return regInfo.getEdgeHostName();
		}
		catch (MalformedURLException e) {
			logger.error( "Invalid console URL. The URL: " + regInfo.getConsoleUrl(), e );
			return "";
		}
	}

	protected String getEdgeUrl(EdgeRegInfo regInfo) {
		if(!StringUtil.isEmptyOrNull(regInfo.getConsoleUrl())){
			return regInfo.getConsoleUrl();
		}
		return regInfo.getEdgeWSDL();
//		String url = "";
//		String edgeWSDL = regInfo.getEdgeWSDL();
//		if (edgeWSDL != null) {
//			url = edgeWSDL.split("services")[0];
//		}
//		return url;
	}

	public String validateUser(String username, String password, String domain) throws ServiceException {
		logger.debug(String.format("validateUser(%s, pwd, %s) - start", username, domain));

		validateLoginInfoFormat(username, password, domain); // may throw
																// "invalid
																// credentials
																// exception

		logger.info(username);
		logger.info(domain);

		// validate
		if (StringUtil.isEmptyOrNull(username))
			throw generateAxisFault(FlashServiceErrorCode.Login_UsernameRequired);
		if (StringUtil.isEmptyOrNull(password))
			throw generateAxisFault(FlashServiceErrorCode.Login_PasswordRequired);
		if (StringUtil.isEmptyOrNull(domain))
			domain = ServiceContext.getInstance().getLocalMachineName();

		// invoke JNI
		int result = 0;
		try {
			result = this.getNativeFacade().validateUser(username, password, domain);
		} catch (Throwable e) {
			logger.error("Error during invoke JNI", e);
			throw generateInternalErrorAxisFault();
		}

		logger.debug("JIN return:" + result);

		// Throw exception when error
		switch (result) {
		case 1:
			throw generateAxisFault(FlashServiceErrorCode.Login_WrongCredential);
		case 2:
			throw generateAxisFault(FlashServiceErrorCode.Login_NotAdministrator);
		}

		String uuid = retrieveCurrentUUID();

		try {
			localAsTrusthost.setPassword(password);
			localAsTrusthost.setUserName(username);
			localAsTrusthost.setUuid(uuid);
		} catch (Exception e) {
			logger.error("Failed to add trust host:" + e);
		}

		logger.debug("validateUser(String, String, String, boolean) - end");
		return uuid;
	}

	public String validateUser(String username, String password, String domain, boolean bGetNodeID) throws ServiceException {
		logger.debug("validateUser(String, String, String, boolean) - start");

		validateLoginInfoFormat(username, password, domain); // may throw
																// "invalid
																// credentials
																// exception

		logger.debug(username);
		logger.debug(domain);
		logger.debug(bGetNodeID);

		// validate
		if (StringUtil.isEmptyOrNull(username))
			throw generateAxisFault(FlashServiceErrorCode.Login_UsernameRequired);
		if (StringUtil.isEmptyOrNull(password))
			throw generateAxisFault(FlashServiceErrorCode.Login_PasswordRequired);
		if (StringUtil.isEmptyOrNull(domain))
			domain = ServiceContext.getInstance().getLocalMachineName();

		// invoke JNI
		int result = 0;
		try {
			result = this.getNativeFacade().validateUser(username, password, domain);
		} catch (Throwable e) {
			logger.error("Error during invoke JNI", e);
			throw generateInternalErrorAxisFault();
		}

		logger.debug("JIN return:" + result);

		// Throw exception when error
		switch (result) {
		case 1:
			throw generateAxisFault(FlashServiceErrorCode.Login_WrongCredential);
		case 2:
			throw generateAxisFault(FlashServiceErrorCode.Login_NotAdministrator);
		}

		String uuid = getUUID(bGetNodeID);

		try {
			localAsTrusthost.setPassword(password);
			localAsTrusthost.setUserName(username);
			localAsTrusthost.setUuid(uuid);
		} catch (Exception e) {
			logger.error("Failed to add trust host:" + e);
		}

		logger.debug("validateUser(String, String, String, boolean) - end");
		return uuid;
	}

	public String getLoginUUID() {
		return getUUID(false);
	}

	public String getNodeUUID() {
		return getUUID(true);
	}

	/**
	 * 
	 * @param bGetNodeID
	 * @Code{true is get node identification id
	 * @code{false is get node login UUID
	 * @return
	 */
	private String getUUID(boolean bGetNodeID) {
		String uuid = null;
		if (bGetNodeID) {
			uuid = retrieveNodeID(useNodeUUID());
		} else {
			uuid = retrieveCurrentUUID();
			if (null != uuid)
				uuid = getNativeFacade().encrypt(uuid);
		}
		return uuid;
	}

	public void validateUser(String uuid) throws ServiceException {
		logger.debug(String.format("validateUser(%s) - start", uuid));

		isUuidFormatValid(uuid); // may throw "invalid credentials exception

		// validate
		if (StringUtil.isEmptyOrNull(uuid))
		{
			logger.error("validateUser, uuid is NULL");
			throw generateAxisFault(FlashServiceErrorCode.Login_UUIDRequired);
		}

		String uuidReg = retrieveCurrentUUID();
		if (!uuid.equals(uuidReg))
		{
			logger.info(String.format("validateUser not equal: %s != %s", getNativeFacade().encrypt(uuid), getNativeFacade().encrypt(uuidReg)));
			throw generateAxisFault(FlashServiceErrorCode.Login_WrongUUID);
		}

		localAsTrusthost.setUuid(uuid);

		logger.debug("validateUser(String) - end");
	}

	public void addTrustedHost(TrustedHost host) throws Exception {
		logger.debug("addTrustedHost - start");
		if (logger.isDebugEnabled()) {
			if (host != null) {
				logger.debug("Name:" + host.getName());
				logger.debug("Port:" + host.getPort());
			}
		}

		if (host == null)
			return;

		trustedHostDAO.addTrustedHost(ServiceContext.getInstance().getTrustedHostFilePath(), host);

		logger.debug("addTrustedHost - end");
	}

	public TrustedHost[] getTrustedHosts() throws Exception {
		logger.debug("getTrustedHosts - start");

		TrustedHost[] hosts = trustedHostDAO.getTrustedHosts(ServiceContext.getInstance().getTrustedHostFilePath(), true);

		logger.debug("getBackupConfiguration - end");
		return hosts;
	}

	public void removeTrustedHost(TrustedHost host) throws Exception {
		logger.debug("removeTrustedHost - start");
		if (logger.isDebugEnabled()) {
			if (host != null) {
				logger.debug("Name:" + host.getName());
				logger.debug("Port:" + host.getPort());
			}
		}

		if (host == null)
			return;

		trustedHostDAO.remove(ServiceContext.getInstance().getTrustedHostFilePath(), host, getNativeFacade());

		logger.debug("removeTrustedHost - end");
	}

	public ActivityLogResult getActivityLogs(int start, int count) throws Exception {
		logger.debug("getActivityLogs - start");
		logger.debug("start:" + start);
		logger.debug("count:" + count);

		try {
			ActivityLogResult result = new ActivityLogResult();

			JActivityLogResult jActivityLogResult = this.getNativeFacade().getActivityLogs(start, count);
			ActivityLog[] logs = activityLogConverter.convert(jActivityLogResult.getLogs().toArray(new JActivityLog[0]));
			result.setLogs(logs);
			result.setTotalCount(jActivityLogResult.getTotalCount());

			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertArray2String(result.getLogs()));
			}
			logger.debug("getActivityLogs - end");
			return result;
		} catch (Throwable e) {
			logger.error("getActivityLogs()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public ActivityLogResult getJobActivityLogs(long jobNo, int start, int count) throws Exception {
		logger.debug("getJobActivityLogs - start");
		logger.debug("jobNo:" + jobNo);
		logger.debug("start:" + start);
		logger.debug("count:" + count);

		try {
			ActivityLogResult result = new ActivityLogResult();

			JActivityLogResult jActivityLogResult = this.getNativeFacade().getJobActivityLogs(jobNo, start, count);
			ActivityLog[] logs = activityLogConverter.convert(jActivityLogResult.getLogs().toArray(new JActivityLog[0]));
			result.setLogs(logs);
			result.setTotalCount(jActivityLogResult.getTotalCount());

			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertArray2String(result.getLogs()));
			}
			logger.debug("getJobActivityLogs - end");
			return result;
		} catch (Throwable e) {
			logger.error("getJobActivityLogs()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public ActivityLogResult getVMJobActivityLogs(long jobNo, int start, int count, String vmUUID) throws Exception {
		logger.debug("getVMJobActivityLogs - start");
		logger.debug("jobNo:" + jobNo);
		logger.debug("start:" + start);
		logger.debug("count:" + count);
		logger.debug("vmUUID:" + vmUUID);

		try {
			ActivityLogResult result = new ActivityLogResult();

			JActivityLogResult jActivityLogResult = this.getNativeFacade().getVMJobActivityLogs(jobNo, start, count, vmUUID);
			ActivityLog[] logs = activityLogConverter.convert(jActivityLogResult.getLogs().toArray(new JActivityLog[0]));
			result.setLogs(logs);
			result.setTotalCount(jActivityLogResult.getTotalCount());

			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertArray2String(result.getLogs()));
			}
			logger.debug("getJobActivityLogs - end");
			return result;
		} catch (Throwable e) {
			logger.error("getJobActivityLogs()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public void addActivityLog(int level, long resourceID, String[] parameters) throws Exception {
		logger.debug("addActivityLog - start");
		logger.debug("level:" + level);
		logger.debug("resourceID:" + resourceID);

		try {
			if (parameters == null)
				return;
			this.getNativeFacade().addLogActivity(level, resourceID, parameters);

			logger.debug("addActivityLog - end");
		} catch (Throwable e) {
			logger.error("addActivityLog()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public void deleteActivityLogs(Date date) throws ServiceException {
		logger.debug("deleteActivityLogs - start");
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
			logger.debug("Calendar.DAY_OF_MONTH:" + calendar.get(Calendar.DAY_OF_MONTH));
			logger.debug("Calendar.HOUR_OF_DAY:" + calendar.get(Calendar.HOUR_OF_DAY));
			logger.debug("Calendar.MINUTE:" + calendar.get(Calendar.MINUTE));
			logger.debug("Calendar.SECOND:" + calendar.get(Calendar.SECOND));

			getNativeFacade().deleteActivityLog(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
					calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

			logger.debug("deleteActivityLogs - end");
		} catch (Throwable e) {
			logger.error("deleteActivityLogs()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public String retrieveCurrentUUID() {
		WindowsRegistry registry = new WindowsRegistry();
		String uuid = null;
		try {
			int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			if (handle == 0) {
				handle = registry.createKey(CommonRegistryKey.getD2DRegistryRoot());
				registry.closeKey(handle);
				handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			}

			uuid = registry.getValue(handle, RegConstants.REGISTRY_KEY_GUID);
			if (StringUtil.isEmptyOrNull(uuid)) {
				uuid = UUID.randomUUID().toString();
				registry.setValue(handle, RegConstants.REGISTRY_KEY_GUID, getNativeFacade().encrypt(uuid));
			} else {
				uuid = getNativeFacade().decrypt(uuid);
			}
			registry.closeKey(handle);
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		
		logger.debug("retrieveCurrentUUID returnning " + uuid);

		return uuid;
	}

	String retrieveNodeID(boolean bUseNodeID) {
		WindowsRegistry registry = new WindowsRegistry();
		String keyPath = CommonRegistryKey.getD2DRegistryRoot();
		String valNodeIDName = RegConstants.REGISTRY_KEY_NODEID;
		String uuid = null;
		String nodeID = null;

		try {
			// always call try to retrieve both Auth ID and Node ID so they are
			// generated on the first retrieval in registry
			uuid = retrieveCurrentUUID();

			int handle = registry.openKey(keyPath);
			if (0 == handle) {
				handle = registry.createKey(keyPath);
				registry.closeKey(handle);
				handle = registry.openKey(keyPath);
			}

			nodeID = registry.getValue(handle, valNodeIDName);
			if (StringUtil.isEmptyOrNull(nodeID)) {
				nodeID = UUID.randomUUID().toString();
				registry.setValue(handle, valNodeIDName, nodeID);
			}
			registry.closeKey(handle);
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}

		return bUseNodeID ? nodeID : uuid;
	}

	/*
	 * wanji10 - temp API, will be removed once all UUID changes are done
	 * 
	 * check if below registry value is set or not. if it set to 1, a new node
	 * UUID is returned by "login", otherwise the old UUID is returned.
	 * HKEY_LOCAL_MACHINE\SOFTWARE\CA\ARCserve Unified Data
	 * Protection\Engine\WebService:EnableNodeID, type: string
	 */
	boolean useNodeUUID() {
		boolean bUseNodeUUID = false;
		WindowsRegistry registry = new WindowsRegistry();
		String switchKeyName = RegConstants.REGISTRY_WEBSERVICE;
		String switchValName = "EnableNodeID";
		String switchVal = null;

		try {
			int handle = registry.openKey(switchKeyName);
			if (0 != handle) {
				switchVal = registry.getValue(handle, switchValName);
				registry.closeKey(handle);
				handle = 0;
			}
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}

		if (null == switchVal || switchVal.equalsIgnoreCase("1")) {
			bUseNodeUUID = true;
		}

		return bUseNodeUUID;
	}

	@Deprecated
	public String getUUID() {
		WindowsRegistry registry = new WindowsRegistry();
		String uuid = null;
		try {
			int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			if (handle == 0) {
				handle = registry.createKey(CommonRegistryKey.getD2DRegistryRoot());
				registry.closeKey(handle);
				handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			}

			uuid = registry.getValue(handle, RegConstants.REGISTRY_KEY_GUID);
			if (StringUtil.isEmptyOrNull(uuid)) {
				uuid = null;
			} else {
				uuid = getNativeFacade().decrypt(uuid);
			}
			registry.closeKey(handle);
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}

		return uuid;
	}

	public D2DConfiguration getD2DConfiguration() throws ServiceException {
		BackupConfiguration backupConfiguration = BackupService.getInstance().getBackupConfiguration();
		PreferencesConfiguration preferencesConfiguration = CommonService.getInstance().getPreferences();

		if (backupConfiguration == null) {
			// For default backup configuration
			backupConfiguration = new BackupConfiguration();
			try {
				Account account = BackupService.getInstance().getAdminAccount();
				if (account != null) {
					backupConfiguration.setAdminPassword(account.getPassword());
					backupConfiguration.setAdminUserName(account.getUserName());
				}
				long serverTime = getServerTime().getTime();
				// set backup start time plus 5 minutes
				backupConfiguration.setBackupStartTime(serverTime + 5 * 60 * 1000);
				backupConfiguration.setPreAllocationBackupSpace(BackupService.getInstance().getPreAllocationValue());
			} catch (Throwable t) {
				logger.debug(t.getMessage());
			}
		}

		ArchiveConfiguration archiveConfiguration = null;
		try {
			archiveConfiguration = ArchiveService.getInstance().getArchiveConfiguration();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		ScheduledExportConfiguration scheduledExportConfiguration = CopyService.getInstance().getScheduledExportConfiguration();

		D2DConfiguration d2dConfiguration = new D2DConfiguration();
		d2dConfiguration.setBackupConfiguration(backupConfiguration);
		d2dConfiguration.setPreferencesConfiguration(preferencesConfiguration);
		d2dConfiguration.setArchiveConfiguration(archiveConfiguration);
		d2dConfiguration.setScheduledExportConfiguration(scheduledExportConfiguration);

		return d2dConfiguration;
	}

	public long saveD2DConfiguration(D2DConfiguration configuration) throws Exception {
		long lRet = -1;
		BackupConfiguration backupConfiguration = configuration.getBackupConfiguration();
		PreferencesConfiguration preferencesConfiguration = configuration.getPreferencesConfiguration();
		ArchiveConfiguration archiveConfiguration = configuration.getArchiveConfiguration();
		ScheduledExportConfiguration scheduledExportConfiguration = configuration.getScheduledExportConfiguration();
		// backupConfiguration.setEmail(preferencesConfiguration.getEmailAlerts());

		if (backupConfiguration != null) {
			lRet = BackupService.getInstance().saveBackupConfiguration(backupConfiguration);
		}
		if (preferencesConfiguration != null) {
			lRet = CommonService.getInstance().savePreferences(preferencesConfiguration);
		}
		if (archiveConfiguration != null) {
			lRet = ArchiveService.getInstance().saveArchiveConfiguration(archiveConfiguration);
		}

		if (scheduledExportConfiguration != null) {
			try {
				CopyService.getInstance().saveScheduledExportConfiguration(scheduledExportConfiguration);
			} catch (ServiceException e) {
				// wanqi06
				ConvertErrorCodeUtil.checkScheduleConfigurationConvert(e);
				throw e;
			}
		}

		return lRet;
	}

	// Job Monitor for replication start
	private RepJobMonitor addRepJobMonitor(String afGuid) {
		synchronized (repJobMonitors) {
			RepJobMonitor monitor = new RepJobMonitor(-1L);
			repJobMonitors.put(afGuid, monitor);
			return monitor;
		}
	}

	public synchronized Map<String, RepJobMonitor> getAllRepJobMonitors() {
		return repJobMonitors;
	}

	public void removeRepJobMonitor(String afGuid) {
		synchronized (repJobMonitors) {
			repJobMonitors.remove(afGuid);
		}
	}

	public RepJobMonitor getRepJobMonitor(String afGuid) {
		synchronized (repJobMonitors) {
			RepJobMonitor repJobMonitor = repJobMonitors.get(afGuid);
			if (repJobMonitor == null) {
				repJobMonitor = addRepJobMonitor(afGuid);
			}
			return repJobMonitor;
		}
	}

	public RepJobMonitor getRepJobMonitorInternal(String afGuid) {
		synchronized (repJobMonitors) {
			RepJobMonitor repJobMonitor = repJobMonitors.get(afGuid);
			if (repJobMonitor == null) {
				repJobMonitor = addRepJobMonitor(afGuid);
			}
			return repJobMonitor;
		}

	}

	// Job monitor for replication end

	public Map<String, D2DStatusInfo> getAllVCMStatusInfo() {
		synchronized (repD2DStatusInfo) {
			Map<String, D2DStatusInfo> repD2DStatusInfoCloned = new Hashtable<String, D2DStatusInfo>();
			repD2DStatusInfoCloned.putAll(repD2DStatusInfo);
			return repD2DStatusInfoCloned;
		}
	}

	public D2DStatusInfo getVCMStatusInfo(String afGuid) {
		synchronized (repD2DStatusInfo) {
			return repD2DStatusInfo.get(afGuid);
		}
	}

	public Set<String> getUUIDCollection() {
		synchronized (repD2DStatusInfo) {
			return new TreeSet<String>(repD2DStatusInfo.keySet());
		}
	}

	public void setVCMStatusInfo(String afGuid, D2DStatusInfo d2dStatusInfo) {
		if (afGuid == null || d2dStatusInfo == null) {
			logger.warn("The uuid or d2dStatusInfo is null.");
			return;
		}

		synchronized (repD2DStatusInfo) {
			repD2DStatusInfo.put(afGuid, d2dStatusInfo);
		}
	}
	
	public void updateVCMStatusInfo(String afGuid, D2DStatusInfo d2dStatusInfo) {
		if (afGuid == null || d2dStatusInfo == null) {
			logger.warn("The uuid or d2dStatusInfo is null.");
			return;
		}

		synchronized (repD2DStatusInfo) {
			if(repD2DStatusInfo.containsKey(afGuid))
				repD2DStatusInfo.put(afGuid, d2dStatusInfo);
			else
				logger.warn("The node "+ afGuid + " is removed.");
		}
	}

	public void removeVCMStatusInfo(String afGuid) {
		synchronized (repD2DStatusInfo) {
			repD2DStatusInfo.remove(afGuid);
		}
	}
	
	public void addVCMStatusInfo(String afGuid,D2DStatusInfo d2dStatusInfo) {
		synchronized (repD2DStatusInfo) {
			repD2DStatusInfo.put(afGuid,d2dStatusInfo);
		}
	}

	// Job Lock for replication start
	public Lock addRepJobLock(String afGuid) {
		synchronized (repJobLocks) {
			Lock lock = new ReentrantLock();
			repJobLocks.put(afGuid, lock);
			return lock;
		}
	}

	public void removeRepJobLock(String afGuid) {
		repJobLocks.remove(afGuid);
	}

	public Lock getRepJobLock(String afGuid) {
		synchronized (repJobLocks) {
			Lock repJobLock = repJobLocks.get(afGuid);
			if (repJobLock == null) {
				repJobLock = addRepJobLock(afGuid);
			}
			return repJobLock;
		}
	}

	public boolean ifRepJobExist(String afGuid) {
		synchronized (repJobLocks) {
			Lock repJobLock = repJobLocks.get(afGuid);
			if (repJobLock == null) {
				return false;
			}
			boolean lock = repJobLock.tryLock();
			if (lock) {
				repJobLock.unlock();
				return false;
			}
			return true;
		}
	}

	// Job Lock for replication end

	public JobMonitorHistoryItem[] queryJobMonitorHistoryItems(int interval) throws ServiceException {
		synchronized (jobMonitorHistory) {
			if (jobMonitorHistory.isEmpty())
				return new JobMonitorHistoryItem[0];

			int size = jobMonitorHistory.size() / interval;
			int i = 0;
			JobMonitorHistoryItem[] jobMonitorHistoryItemsArray = null;
			if (jobMonitorHistory.size() % interval == 0) {
				jobMonitorHistoryItemsArray = new JobMonitorHistoryItem[size];
			} else {
				jobMonitorHistoryItemsArray = new JobMonitorHistoryItem[size + 1];
			}
			for (Iterator<JobMonitorHistoryItem> itr = jobMonitorHistory.iterator(); i < size; i++) {
				JobMonitorHistoryItem item = itr.next();
				jobMonitorHistoryItemsArray[i] = item;
				// if (itr.hasNext()) {
				// itr.next();
				// if (itr.hasNext())
				// itr.next();
				// else
				// break;
				// } else
				// break;
				itr.next();
				itr.next();
			}
			if (jobMonitorHistory.size() % interval != 0)
				jobMonitorHistoryItemsArray[i] = jobMonitorHistory.getLast();

			return jobMonitorHistoryItemsArray;
		}
	}

	public JobMonitorHistoryItem jobMontor2JobMonitorHistory(JobMonitor jobMonitor) {
		JobMonitorHistoryItem item = new JobMonitorHistoryItem();
		item.setnReadSpeed(jobMonitor.getnReadSpeed());
		item.setnWriteSpeed(jobMonitor.getnWriteSpeed());
		return item;
	}

	public void addJobmonitorHistory(JobMonitor jobMonitor) {
		JobMonitorHistoryItem item = jobMontor2JobMonitorHistory(jobMonitor);
		synchronized (jobMonitorHistory) {
			if (jobMonitorHistory.size() >= JOBMONITOR_HISTORY_COUNT) {
				jobMonitorHistory.removeFirst();
			}
			jobMonitorHistory.addLast(item);
		}
	}

	public void clearJobmonitorHistory() {
		synchronized (jobMonitorHistory) {
			jobMonitorHistory.clear();
		}
	}

	public TrustedHost getLocalHostAsTrust() {
		logger.debug("getLocalHostAsTrust - start");
		logger.debug("getLocalHostAsTrust - end");
		return localAsTrusthost;
	}

	public void cancelJob(long jobID) throws ServiceException {
		logger.debug("cancelJob()");
		int returnVlaue;
		try {
			returnVlaue = this.getNativeFacade().cancelJob(jobID);
		} catch (Throwable e) {
			throw this.generateInternalErrorAxisFault();
		}
		if (returnVlaue != 0)
			throw generateAxisFault(FlashServiceErrorCode.Common_CancelJobFailed);
	}

	public void cancelJob(long jobID, int reason, String vmInstanceUUID) throws ServiceException {
		logger.debug("cancelJob()");
		int returnVlaue;
		try {
			returnVlaue = this.getNativeFacade().cancelJob(jobID);
		} catch (Throwable e) {
			throw this.generateInternalErrorAxisFault();
		}
		if (returnVlaue != 0)
			throw generateAxisFault(FlashServiceErrorCode.Common_CancelJobFailed);

		String msgCode = null;
		if (reason == 1) {
			msgCode = "cancelJobWithReasonDelete";
		} else if (reason == 2) {
			msgCode = "cancelJobWithReasonModify";
		} else if (reason == 3) {
			msgCode = "cancelJobWithReasonStop";
		}

		if (msgCode != null) {
			String message = WebServiceMessages.getResource(msgCode);
			if (StringUtil.isEmptyOrNull(vmInstanceUUID))
				this.getNativeFacade().addLogActivityWithJobID(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL,
						new String[] { message, "", "", "", "" });
			else
				this.getNativeFacade().addVMLogActivityWithJobID(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL,
						new String[] { message, "", "", "", "" }, vmInstanceUUID);
		}
	}

	public void cancelJob(String jobType) throws ServiceException {
		logger.debug("cancelJob by jobType");
		int returnValue = 0;
		try {
			if (Integer.parseInt(jobType) == JobType.JOBTYPE_MERGE) {
				MergeService.getInstance().pauseMerge(MergeAPISource.MANUALLY);
				return;
			} else {
				Map<Long, JobMonitor> jobMonitorMap = jms.getJobMonitorMap().get(jobType);
				if (jobMonitorMap != null && !jobMonitorMap.keySet().isEmpty()) {
					Object jobID = jobMonitorMap.keySet().toArray()[0];
					returnValue = this.getNativeFacade().cancelJob(Long.valueOf(String.valueOf(jobID)).longValue());
				}
			}
		} catch (Throwable e) {
			throw this.generateInternalErrorAxisFault();
		}
		if (returnValue != 0)
			throw generateAxisFault(FlashServiceErrorCode.Common_CancelJobFailed);
	}

	public void cancelOrPauseJobById(long jobId) throws ServiceException {
		logger.debug("cancelJob by jobId");

		if (!isJobRunning(jobId)) {
			logger.debug("No running job found for the jobId:" + jobId);
			return;
		}
		int retValue = 0;
		try {
			if (isRunningMergeJob(jobId)) {
				MergeService.getInstance().pauseMerge(MergeAPISource.UNKNOWN);
			} else {
				retValue = this.getNativeFacade().cancelJob(jobId); // return 1
																	// means
																	// canceling
																	// is in
																	// processing.
				if (retValue == 1)
					return;
			}
		} catch (Throwable e) {
			throw this.generateInternalErrorAxisFault();
		}
		if (retValue != 0)
			throw generateAxisFault(FlashServiceErrorCode.Common_CancelJobFailed);
	}

	private boolean isRunningMergeJob(long jobId) {
		MergeJobMonitor jobMonitor = MergeService.getInstance().getMergeJobMonitor();
		if (jobMonitor == null)
			return false;
		long id = jobMonitor.getDwJobID();
		return id == jobId;
	}

	private boolean isJobRunning(long jobId) {
		Enumeration<Map<Long, JobMonitor>> enuJobMonitorMap = jms.getJobMonitorMap().elements();
		while (enuJobMonitorMap.hasMoreElements()) {
			if (containsJob(enuJobMonitorMap.nextElement(), jobId))
				return true;
		}
		return false;
	}

	private boolean containsJob(Map<Long, JobMonitor> jobMonitorMap, long jobId) {
		if (jobMonitorMap != null) {
			Iterator<Long> itJobMonitor = jobMonitorMap.keySet().iterator();
			while (itJobMonitor.hasNext()) {
				long id = itJobMonitor.next();
				if (id == jobId) {
					return true;
				}
			}
		}
		return false;
	}

	public void cancelVMJobForUnassignPolicy(VirtualMachine vm) {
		cancelVMJob(vm, Constants.AF_JOBTYPE_VM_BACKUP);
	}

	public void cancelVMJob(VirtualMachine vm, long jobType) {
		Map<String, Map<Long, JobMonitor>> vmJobTypeMap = jms.getVMJobMonitorMap().get(vm.getVmInstanceUUID());
		if (vmJobTypeMap != null) {
			Map<Long, JobMonitor> vmJobIdMap = vmJobTypeMap.get(String.valueOf(jobType));
			if (vmJobIdMap != null) {
				for (Map.Entry<Long, JobMonitor> entry : vmJobIdMap.entrySet()) {
					try {
						cancelJob(entry.getKey());
					} catch (ServiceException e) {
						logger.error("Cancel VM backup job failed. VM name = " + vm.getVmName(), e);
					}
				}
			}
		}
	}
	
	public boolean validateDirPath(String path, String domain, String user, String pwd) throws ServiceException {
		logger.debug("validatePath() - start");
		if (path.length() > BrowserService.BROWSE_LENGTH)
			throw generateAxisFault(BrowserService.BROWSE_LENGTH + "", FlashServiceErrorCode.Browser_Source_Path_Exceeds_Max);
		boolean ret = getNativeFacade().validateDirPath(path, domain, user, pwd);
		logger.debug("result:" + ret);
		logger.debug("validatePath - end");

		return ret;
	}

	public boolean isFolderAccessible(String path, String domain, String user, String pwd) {
		logger.debug("isFolderAccessible(" + path + "," + domain + "," + user + ", *) - begin");
		if (StringUtil.isEmptyOrNull(path)) {
			logger.debug("isFolderAccessible() - end. path is null, return false");
			return false;
		}

		String userName = getNormailizedStr(user);
		String passwd = getNormailizedStr(pwd);
		if (StringUtil.isEmptyOrNull(domain)) {
			int indx = userName.indexOf('\\');
			if (indx > 0 && indx < userName.length() - 1) {
				domain = userName.substring(0, indx);
				userName = userName.substring(indx + 1);
			} else
				domain = "";
		}

		try {
			// check whether path is valid, not to create a new empty folder.
			return getNativeFacade().checkDirPathValid(path, domain, userName, passwd);
		} catch (ServiceException e) {
			if (FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG.equals(e.getErrorCode())) {
				logger.debug("isFolderAccessible() - end error: FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG  false");
				return false;
			}
			logger.error("isFolderAccessible() - end error");
		}
		return false;
	}

	private String getNormailizedStr(String str) {
		return str == null ? "" : str;
	}

	public boolean checkBaseLicense() {
		return this.getNativeFacade().checkBaseLic();
	}

	public int getLocalADTPackage() {
		logger.debug("getLocalADTPackage() - begin");
		int ret = this.getNativeFacade().getLocalADTPackage();
		logger.debug("getLocalADTPackage() - end:" + ret);
		return ret;
	}

	public NetworkPath[] getMappedNetworkPath(String userName) throws ServiceException {
		List<NetworkPath> pathList = getNativeFacade().getNetworkPathForMappedDrive(userName);
		NetworkPath[] pathArr = pathList.toArray(new NetworkPath[0]);
		logger.debug("Mapped network path: " + StringUtil.convertArray2String(pathArr));
		return pathArr;
	}

	public boolean validateSessionPassword(String password, String destination, long sessionNum, HttpSession session) throws ServiceException {
		boolean isValid = getNativeFacade().validateSessionPassword(password, destination, sessionNum, session);
		return isValid;
	}

	public boolean validateSessionPasswordByHash(String password, long pwdLen, String hashValue, long hashLen, HttpSession session) throws ServiceException {
		boolean isValid = getNativeFacade().AFValidateSessPasswordByHash(password, pwdLen, hashValue, hashLen, session);
		return isValid;
	}

	public String[] getSessionPasswordBySessionGuid(String[] sessionGuid) throws ServiceException {
		String[] pwdStrings = getNativeFacade().getSessionPasswordBySessionGuid(sessionGuid);
		if (pwdStrings != null) {
			for (int i = 0; i < pwdStrings.length; i++) {
				if (pwdStrings[i] == null) {
					pwdStrings[i] = "";
				}
			}
		}
		return pwdStrings;
	}

	public long validatePreferences(PreferencesConfiguration in_PreferencesConfig) throws Exception {

		int iRet = backupConfigurationValidator.ValidateEmailSettings(in_PreferencesConfig.getEmailAlerts());

		iRet = backupConfigurationValidator.validateSelfUpdateConfigurations(in_PreferencesConfig.getupdateSettings());

		return iRet;
	}

	public long savePreferences(PreferencesConfiguration in_PreferencesConfig) throws Exception {
		return savePreferences(in_PreferencesConfig, false);
	}

	public long savePreferences(PreferencesConfiguration in_PreferencesConfig, boolean isFromEdge) throws Exception {
		logger.info("saving D2D Preferences - start, at " + new Date());

		// SelfUpdateConfiguration selfUpdateConfiguration = new
		// SelfUpdateConfiguration();
		try {

			int iRet = backupConfigurationValidator.ValidateEmailSettings(in_PreferencesConfig.getEmailAlerts());

			iRet = backupConfigurationValidator.validateSelfUpdateConfigurations(in_PreferencesConfig.getupdateSettings(), isFromEdge);

			if (iRet == 0) {
				BackupEmail be = in_PreferencesConfig.getEmailAlerts();

				// save auto update settings to a separated file under ..\Update
				// Manager\Config\

				AutoUpdateSettings tempSettings = in_PreferencesConfig.getupdateSettings();
				in_PreferencesConfig.setupdateSettings(null);
				backupConfigurationXMLDAO.savePreferences(ServiceContext.getInstance().getPreferencesConfigurationFilePath(), in_PreferencesConfig);
				backupConfigurationXMLDAO.saveAutoUpdateSettings(ServiceContext.getInstance().getAutoUpdateSettingsFilePath(), tempSettings);
				in_PreferencesConfig.setupdateSettings(tempSettings);

				// save and active SRM configuration

				SRMPkiAlertSetting alertSetting = new SRMPkiAlertSetting();
				alertSetting.getDefaultValue();
				alertSetting.setCputhreshold(be.getCpuThreshold());
				alertSetting.setMemorythreshold(be.getMemoryThreshold());
				alertSetting.setDiskthreshold(be.getDiskThreshold());
				alertSetting.setNetworkthreshold(be.getNetworkThreshold());
				alertSetting.setValidalert(be.isEnableSrmPkiAlert());

				D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
				String edgeWSDL = edgeReg.GetEdgeWSDL();
				if (edgeWSDL != null && edgeWSDL.length() > 0) {
					alertSetting.setValidpkiutl(true);
				} else {
					alertSetting.setValidpkiutl(false);
				}
				preferences = in_PreferencesConfig;
				SrmAgentServerImpl.SaveAlertSetting(alertSetting);
			}
		} catch (Exception e) {
			throw e;
		}

		logger.info("saving D2D Preferences - End");
		return 0;
	}
	
	private Object lock = new Object();

	public PreferencesConfiguration getPreferences() throws ServiceException {
		logger.debug("getPreferences - start");
		// PreferencesConfiguration preferences = null;
		try {
			synchronized (lock) {

				if (preferences == null) {
					if (!StringUtil.isExistingPath(ServiceContext.getInstance().getPreferencesConfigurationFilePath()))
						return null;

					preferences = backupConfigurationXMLDAO.getPreferences(ServiceContext.getInstance().getPreferencesConfigurationFilePath());

					if (preferences != null) {
						AutoUpdateSettings updateSettings = backupConfigurationXMLDAO.getUpdateSettings(ServiceContext.getInstance()
								.getAutoUpdateSettingsFilePath());
						preferences.setupdateSettings(updateSettings);
					}
				}

				/*
				 * BackupConfiguration backupConfg =
				 * BackupService.getInstance().getBackupConfiguration();
				 * if(backupConfg != null) { DestinationCapacity destCapacity =
				 * BackupService
				 * .getInstance().getDestSizeInformation(backupConfg);
				 * if(destCapacity != null) {
				 * preferences.setDestCapacity(destCapacity); } }
				 */

				// liuwe05 2011-2-10 fix Issue: 20000805 Title: NOTIFICAITON NOT
				// UPDATED INTRA
				// the general settings can be change on both web UI and tray
				// icon
				// so we need to update web service if it was changed by tray
				// icon
				if (preferences != null && preferences.getGeneralSettings() == null) {
					GeneralSettings generalSettings = new GeneralSettings();
					backupConfigurationXMLDAO.GetGeneralSettings(generalSettings);
					preferences.setGeneralSettings(generalSettings);
				}

				/*
				 * if(preferences.getupdateSettings() != null) {
				 * testDownloadServerConnnections
				 * (preferences.getupdateSettings()); }
				 */

			}

			logger.debug("getPreferences - end");
			return preferences;
		} catch (ServiceException ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		} catch (Exception e) {
			logger.error("getBackupConfiguration()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	

	public LicInfo getLicInfo() throws ServiceException {
		LicInfo lic= new LicInfo();
		long status = this.getNativeFacade().getLicenseStatus(isStandAlone());
		lic.setBase((int)status);
		return lic;
	}

	public void cleanupPreferenceConfiguration() {
		logger.debug("clean preference configuration");
		synchronized (lock) {
			preferences = null;
		}
	}

	public void cleanupGeneralSettings() throws Exception {
		logger.debug("clean General settings");

		try {
			synchronized (lock) {
				if (preferences != null) {
					preferences.setGeneralSettings(null);
				}
			}

		} catch (Exception e) {
			throw e;
		}
	}

	public boolean validateEmailFromAddress(String address) throws ServiceException {
		if (!StringUtil.isValidEmailAddress(address)) {
			throw new ServiceException(FlashServiceErrorCode.BackupConfig_IvalidEmailFromAddress);
		}

		if (!backupConfigurationValidator.validateEmail(address))
			throw new ServiceException(FlashServiceErrorCode.BackupConfig_IvalidEmailFromAddress);

		return true;
	}

	public long checkServiceStatus(String serviceName) throws Exception {
		logger.debug("checkServiceStatus" + serviceName);

		try {
			return this.getNativeFacade().aoeCheckServiceStatus(serviceName);
		} catch (Throwable t) {
			logger.error("checkServiceStatus", t);
			throw generateInternalErrorAxisFault();
		}
	}

	public boolean checkLocalHost(String host, boolean isLocalCall) {
		logger.debug("checkLocalHost begin, host:" + host + ", localCall:" + isLocalCall);
		if (localhostIPList == null)
			initializeIPList();

		if (host == null || host.length() == 0)
			return false;

		host = host.trim();

		if (logger.isDebugEnabled()) {
			logger.debug("localhostIPList size:" + localhostIPList.size());
			for (String h : localhostIPList) {
				logger.debug("LocalHost:" + h);
			}
		}

		boolean isLocal = false;
		for (String h : localhostIPList) {
			if (host.equalsIgnoreCase(h)) {
				isLocal = true;
				break;
			}
		}
		logger.debug("checkLocalHost end, isLocal:" + isLocal);
		return isLocal;
	}

	private static synchronized void initializeIPList() {
		if (localhostIPList != null)
			return;

		localhostIPList = new ArrayList<String>();
		localhostIPList.add("localhost");
		localhostIPList.add("127.0.0.1");

		try {
			InetAddress localHost = InetAddress.getLocalHost();
			String localAddr = localHost.getHostAddress();
			String localName = localHost.getHostName();
			if (localAddr != null && !localhostIPList.contains(localAddr)) {
				localhostIPList.add(localAddr);
			}

			if (localName != null && !localhostIPList.contains(localName)) {
				localhostIPList.add(localName);
			}

			String fQDN = localHost.getCanonicalHostName();

			if (fQDN != null) {
				int indx = fQDN.indexOf('.');
				if (indx > 0) {
					localhostIPList.add("localhost" + fQDN.substring(indx));
					localhostIPList.add(fQDN);
				}
			}
		} catch (Exception e) {
			logger.error("InetAddress error:", e);
		}

		try {
			Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();
			if (ni != null) {
				while (ni.hasMoreElements()) {
					NetworkInterface element = ni.nextElement();
					Enumeration<InetAddress> ips = element.getInetAddresses();
					while (ips.hasMoreElements()) {
						InetAddress ip = ips.nextElement();
						String ipaddress = ip.getHostAddress();
						if (ipaddress != null && !localhostIPList.contains(ipaddress)) {
							if (ipaddress.indexOf('%') != -1) { // truncate the
																// ipv6 address
								ipaddress = ipaddress.substring(0, ipaddress.indexOf('%'));
							}
							localhostIPList.add(ipaddress);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("NetworkInterface error:", e);
		}
	}

	public String getMntPathFromVolumeGUID(String strGUID) {
		return WSJNI.getMntFromVolumeGUID(strGUID);
	}

	public MountSession[] getMountedSessions() {
		List<MountSession> mountedSessions = new ArrayList<MountSession>();
		try {
			List<JMountSession> sessions = new ArrayList<JMountSession>();
			long ret = WSJNI.AFGetMntSess(sessions);
			if (ret == 0) {
				for (JMountSession session : sessions) {
					MountSession msess = new MountSession();
					msess.setSessionNum(session.getSessionNum());
					msess.setSessionPath(session.getSessionPath());
					mountedSessions.add(msess);
				}
			} else {
				logger.error("Failed to get mounted sessions with error " + ret);
			}
		} catch (Exception e) {
			logger.error("Failed to get mounted sessions", e);
		}

		return mountedSessions.toArray(new MountSession[0]);
	}

	public MountSession[] getMountedSessions(String currentDest) {
		List<MountSession> mountedSessions = new ArrayList<MountSession>();
		try {
			List<JMountSession> sessions = new ArrayList<JMountSession>();
			if (WSJNI.AFGetMntSess(sessions) == 0) {
				List<String> dests = new ArrayList<String>();
				if (WSJNI.GetAllBackupDestinations(currentDest, dests) == 0) {
					for (JMountSession session : sessions) {
						for (String dest : dests) {
							if (session.getSessionPath() != null && session.getSessionPath().startsWith(dest)) {
								MountSession msess = new MountSession();
								msess.setSessionNum(session.getSessionNum());
								msess.setSessionPath(session.getSessionPath());
								mountedSessions.add(msess);
								break;
							}
						}
					}
				} else {
					logger.error("Failed  to get all backup destinations by: " + currentDest);
					for (JMountSession session : sessions) {
						MountSession msess = new MountSession();
						msess.setSessionNum(session.getSessionNum());
						msess.setSessionPath(session.getSessionPath());
						mountedSessions.add(msess);
					}
				}
			} else {
				logger.error("Failed  to get mounted sessions");
				// throw new Exception("Failed to get mount sessions");
			}
		} catch (Exception e) {
			logger.error("Failed  to get mounted sessions");
			// throw e;
		}

		return mountedSessions.toArray(new MountSession[0]);
	}

	public long getServerTimezoneOffset(Date date) {
		return TimeZone.getDefault().getOffset(date.getTime());
	}

	public long getServerTimezoneOffsetByMillis(long date) {
		return TimeZone.getDefault().getOffset(date);
	}

	public boolean checkLicense(int module) throws ServiceException {
		LicInfo lic = this.getNativeFacade().AFGetLicenseEx(false);
		switch (module) {
		case CommonService.ENCRYPTION_LIC:
			return lic.getDwEncryption() == 0;
		case CommonService.SCHEDULEDEXPORT_LIC:
			return lic.getDwScheduledExport() == 0;
		case CommonService.EX_GR_LIC:
			return lic.getDwExchangeGR() == 0;
		case CommonService.EX_DB_LIC:
			return lic.getDwExchangeDB() == 0;
		case CommonService.FILE_COPY_2D_LIC:
			return lic.getDwD2D2D() == 0;
		default:
			return true;
		}
	}

	public RetryPolicy getRetryPolicy(String jobType) {
		Map<String, RetryPolicy> retryPolicies = null;

		NativeFacade nativeFacade = CommonService.getInstance().getNativeFacade();
		if (!StringUtil.isExistingPath(ServiceContext.getInstance().getRetryPolicyFilePath())) {
			nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY,
					new String[] { WebServiceMessages.getResource(Constants.RETRYPOLICY_READ_ERROR_NON), "", "", "", "" });
			logger.debug("getRetryPolicy - end with nonexist retry policy file");
		} else {
			try {
				retryPolicies = retryPolicyXMLDAO.get(ServiceContext.getInstance().getRetryPolicyFilePath());
			} catch (Exception e) {
				// String msg = e.getMessage();
				nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY,
						new String[] { WebServiceMessages.getResource(Constants.RETRYPOLICY_READ_ERROR), "", "", "", "" });
				logger.error("getRetryPolicy - " + e.getMessage(), e);
			}
		}

		if (retryPolicies != null) {
			return retryPolicies.get(jobType);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public List<CloudProviderInfo> getCloudVendor() {

		Map<String, CloudVendor> cloudVendorList = null;
		// HashMap<String,CloudProviderInfo> cloudProviderMap = null;
		List<CloudProviderInfo> providerList = null;
		if (!StringUtil.isExistingPath(ServiceContext.getInstance().getRetryPolicyFilePath())) {
			logger.info("getCloudVendorList - end with nonexist cloud vendor configuration file");
		} else {
			try {
				cloudVendorList = cloudVendorXMLDAO.get(ServiceContext.getInstance().getCloudVendorInfoFilePath());
			} catch (Exception e) {
				logger.error("getCloudVendorList - " + e.getMessage(), e);
			}
		}
		// convert VendorList to cloudProviderMap
		if (cloudVendorList != null) {
			// cloudProviderMap = new HashMap<String, CloudProviderInfo>();
			providerList = new ArrayList<CloudProviderInfo>();
			Iterator it = cloudVendorList.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				CloudVendor object = (CloudVendor) pairs.getValue();
				CloudProviderInfo info = new CloudProviderInfo();
				info.setUrl(object.getVendorUrl());
				info.setVendorID(object.getVendorId());
				providerList.add(info);
			}
		}

		return providerList;

	}

	public void saveRetryPolicy(Map<String, RetryPolicy> policies) {
		try {
			retryPolicyXMLDAO.save(ServiceContext.getInstance().getRetryPolicyFilePath(), policies);
		} catch (Exception e) {
			logger.error("Failed to save retry policy ", e);
		}

	}
	
	//October Sprint - 
	public void saveStorageAppliance(List<StorageAppliance> storageApplianceList) {
		try {
			storageApplianceXMLDAO.save(ServiceContext.getInstance().getStorageApplianceConfigurationFilePath(), storageApplianceList);
		} catch (Exception e) {
			logger.error("Failed to save retry policy ", e);
		}

	}

	public void scheduleMoveLog() {
		try {
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
			JobDetail jobDetail = new JobDetailImpl("MoveLogJob", "CommonJobGroup", MoveLogJob.class);
			// every month
			SimpleTriggerImpl trigger = ScheduleUtils.makeHourlyTrigger(7 * 24);
			trigger.setName("MoveLogJob");
			Properties props = ServiceContext.getInstance().getD2dProperties();
			if (props == null) {
				logger.error("D2D properties file is not exist, should be error");
				return;
			}
			String startTime = (String) props.get("StartTime");
			Date date = new Date();
			if (startTime == null || startTime.isEmpty()) {
				startTime = String.valueOf(date.getTime());
				props.setProperty("StartTime", startTime);
			}
			trigger.setStartTime(new Date(Long.parseLong(startTime)));
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			logger.error("Failed to schedule move log job", e);
		}
	}

	/**
	 * If use manually pause the merge job, we will send a mail alert for him
	 * every day, in D2D r16.5, the default time is 12:00pm.
	 * 
	 * @param startTime
	 */
	public void scheduleEmailSender(int startHour, int startMinute) {
		try {
			logger.info("Schedule email sender job for manually paused merge every day at: " + startHour + ":" + startMinute);
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
			JobDetailImpl jobDetail = new JobDetailImpl(AbstractMergeService.MERGE_EMAIL_JOB_NAME, AbstractMergeService.MERGE_JOB_GROUP_NAME,
					EmailSenderForManualMergeJob.class);
			// every day
			CronTriggerImpl trigger = ScheduleUtils.makeDailyTrigger("Trigger" + jobDetail.getName(), startHour, startMinute);

			Calendar startTime = Calendar.getInstance();
			trigger.setStartTime(startTime.getTime());
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			logger.error("Failed to schedule email sender job for manually paused merge", e);
		}
	}

	public synchronized long updateSessionPasswordsByGUID(String backupDest, String domain, String userName, String destPwd, String[] uuids,
			String[] passwords, String[] passwordHash) throws Exception {
		try {
			if (!backupDest.endsWith(InetAddress.getLocalHost().getHostName()))
				return 0;
			Date startDate = new Date(0);
			Calendar endDate = Calendar.getInstance();
			endDate.set(9999, 11, 31);
			RecoveryPoint[] recoveryPoints = RestoreService.getInstance().getRecoveryPoints(backupDest, domain, userName, destPwd, startDate,
					endDate.getTime(), true);

			HashMap<String, String> pwdsByHash = new HashMap<String, String>();
			for (int i = 0; i < passwords.length; i++) {
				pwdsByHash.put(passwordHash[i], passwords[i]);
			}

			List<String> newUUIDs = new ArrayList<String>();
			;
			List<String> newPwds = new ArrayList<String>();
			for (int i = 0; i < recoveryPoints.length; i++) {
				RecoveryPoint rp = recoveryPoints[i];
				if (rp.getEncryptType() > 0) {
					String uuid = rp.getSessionGuid();
					String password = pwdsByHash.get(rp.getEncryptPasswordHash());
					if (password != null && !password.isEmpty()) {
						String[] pwds = this.getNativeFacade().getSessionPasswordBySessionGuid(new String[] { uuid });
						if (pwds == null || pwds.length == 0 || pwds[0] == null) {
							newUUIDs.add(uuid);
							newPwds.add(password);
						}
					}
				}
			}

			return getNativeFacade().updateSessionPasswordByGUID(newUUIDs.toArray(new String[0]), newPwds.toArray(new String[0]));
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw e;
		}

	}

	public boolean isDriverInstalled() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		/*String str = null;		
		try{
			str = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\AFFlt", "DisplayName");
		}catch(Exception e){}
		
		if(str == null)
			str = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\ARCFlashVolDrv", "DisplayName");	
		
		return str != null;*/
		return true;
	}

	public boolean isRestartedAfterDriver() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		/*String str = null;	
		try{
			str = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\AFFlt\\Enum", "0");
		}catch(Exception e){}
		
		if(str == null)
			str = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Services\\ARCFlashVolDrv\\Enum", "0");
		
		return str != null;*/
		return true;
	}

	public boolean isTimeInDSTBeginInterval(int year, int month, int date, int hour, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, date, hour, minute, 0);
		Calendar cal2 = Calendar.getInstance();
		cal2.set(year, month, date, hour + 1, minute, 0);

		if (cal.getTimeInMillis() == cal2.getTimeInMillis())
			return true;
		else
			return false;
	}

	public boolean isTimeInDSTEndInterval(int year, int month, int date, int hour, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, date, hour, minute, 0);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTimeInMillis(cal.getTimeInMillis() - 3600 * 1000);

		// DST ends
		if (cal.getTime().getTimezoneOffset() > cal2.getTime().getTimezoneOffset())
			return true;
		else
			return false;
	}

	public void validateBackupStartTime(D2DTime time) throws ServiceException {
		if (time == null)
			return;
		if (CommonService.getInstance().isTimeInDSTBeginInterval(time.getYear(), time.getMonth(), time.getDay(), time.getHourOfday(), time.getMinute())) {
			String[] timearr = formatStartTimeErrorMsg(time.getHourOfday());
			/*
			 * String start = String.valueOf(time.getHour()) + ":00"; String
			 * startP1 = String.valueOf(time.getHour() + 1) + ":00";
			 * if(time.getAmPM() == java.util.Calendar.AM) { String am =
			 * WebServiceMessages.getResource("scheduleStartTimeAM"); start +=
			 * " " + am; startP1 += " " + am; }else if(time.getAmPM() ==
			 * java.util.Calendar.PM) { String pm =
			 * WebServiceMessages.getResource("scheduleStartTimePM"); start +=
			 * " " + pm; startP1 += " " + pm; }
			 */
			throw new ServiceException(FlashServiceErrorCode.BackupConfig_ERR_DST_Start, new Object[] { timearr[0], timearr[0] + "-" + timearr[1] });
		}
	}

	public boolean notifyD2DVersion() {
		D2DEdgeRegistration ereg = new D2DEdgeRegistration();
		EdgeRegInfo edgeRegInfo = ereg.getEdgeRegInfo(ApplicationType.CentralManagement);
		if(edgeRegInfo==null||StringUtil.isEmptyOrNull(edgeRegInfo.getEdgeWSDL()) || StringUtil.isEmptyOrNull(edgeRegInfo.getEdgeUUID())){
			return true;
		}
		
		String uuid = getNodeUUID();
		if (StringUtil.isEmptyOrNull(uuid)) {
			logger.error("failed to retrieve UUID!");
			return true;
		}
		NotifyMessage notifyMessage = getNotifyMessage(edgeRegInfo);
		notifyMessage.addParameter(NotifyMessageConstants.KEY_UUID, uuid);
		String edgeWSDL = edgeRegInfo.getEdgeWSDL();
		try {
			IEdgeCM4D2D edgeService = WebServiceFactory.getEdgeService(edgeWSDL, IEdgeCM4D2D.class);
			edgeService.validateUserByUUID(edgeRegInfo.getEdgeUUID());
			edgeService.notify(notifyMessage);
			logger.info("Succeeded in notifying Console by " + edgeWSDL);
			return true;
		} catch (Exception e) {
			logger.error("Failed to notify Console by " + edgeWSDL, e);
			return false;
		}
	}

	private NotifyMessage getNotifyMessage(EdgeRegInfo edgeRegInfo) {
		NotifyMessage message = new NotifyMessage();
		message.setType(NotifyMessageConstants.TYPE_D2D_VERSION);
		VersionInfo versionInfo = CommonService.getInstance().getVersionInfo();
		message.addParameter(NotifyMessageConstants.KEY_PRODUCT_NAME, NotifyMessageConstants.PRODUCT_NAME_D2D);
		message.addParameter(NotifyMessageConstants.KEY_D2D_MAJOR_VERSION, versionInfo.getMajorVersion());
		message.addParameter(NotifyMessageConstants.KEY_D2D_MINOR_VERSION, versionInfo.getMinorVersion());
		message.addParameter(NotifyMessageConstants.KEY_D2D_BUILD_NUMBER, versionInfo.getBuildNumber());
		message.addParameter(NotifyMessageConstants.KEY_D2D_UPDATE_VERSION_NUMBER, versionInfo.getUpdateNumber());
		
		try {
			String nodeName = edgeRegInfo.getRegHostName();
			if(StringUtil.isEmptyOrNull(nodeName)){
				logger.warn("there is no reg host name in edge registration. use local host name as reg host name!");
				nodeName = InetAddress.getLocalHost().getHostName();
			}
			message.addParameter(NotifyMessageConstants.KEY_AGENT_HOSTNAME, nodeName);
			message.addParameter(NotifyMessageConstants.KEY_UUID, getNodeUUID());
			message.addParameter(NotifyMessageConstants.KEY_AGENT_IP, InetAddress.getLocalHost().getHostAddress());
			String windowsTempDir = getNativeFacade().getWindowsTempDir();
			if(!windowsTempDir.endsWith("\\")){
				windowsTempDir = windowsTempDir + "\\";
			}
			File flag = new File(windowsTempDir + SETUP_REBOOT_FLAG_FILE);
			if(flag.exists()){
				logger.info("[SETUP]as_reboot_d2d.ini: "+flag.getAbsolutePath());
			}
			message.addParameter(NotifyMessageConstants.KEY_AGENT_REBOOT_STATUS, flag.exists()?"false":"true");
			logger.info("NotifyMessage: "+message.getParameter(NotifyMessageConstants.KEY_UUID)+" "+nodeName+" "+message.getParameter(NotifyMessageConstants.KEY_AGENT_IP)+" "+message.getParameter(NotifyMessageConstants.KEY_AGENT_REBOOT_STATUS));
		} catch (Exception e) {
			logger.equals(e);
		}
		return message;
	}

	private String[] formatStartTimeErrorMsg(int hour) {
		// hour is start time, we also need to compute the DST end time
		boolean isAM = false;
		boolean endAM = false;
		int endHour = 0;
		if (!ServiceUtils.is24Hours()) { // for 12 hours
			if (ServiceUtils.minHour() == 0) { // for 0-11 clock
				if (hour < 12) { // for am
					isAM = true;
					endHour = hour + 1;
					if (endHour == 12) {
						endAM = false;
						endHour = 0;
					} else {
						endAM = true;
					}
				} else { // for pm
					isAM = false;
					if (hour == 12) // translate 12:30 to 0:30 pm
						hour = 0;
					else
						hour = hour - 12; // translate 18:30 to 6:30 pm
					endHour = hour + 1;
					endAM = false;
					if (endHour == 12) {
						endHour = 0;
						endAM = true;
					}
				}
			} else { // for 1-12 clock
				if (hour < 12) { // for am
					isAM = true;
					endHour = hour + 1;
					endAM = true;
					if (endHour == 12) {
						endAM = false;
					}
					if (hour == 0) // translate 0:30 to 12:30 am
						hour = 12;
				} else { // for pm
					isAM = false;
					if (hour != 12) { // translate 12:30 to 12:30 pm
						hour -= 12;
						endHour = hour + 1;
						endAM = false;
						if (endHour == 12)
							endAM = true;
					} else {
						endHour = 1;
						endAM = false;
					}
				}
			}
		} else { // for 24 hours
			if (ServiceUtils.minHour() == 1) // for 1-24 clock
			{
				if (hour == 0) // translate 0:30 to 24:30
					hour = 24;
				endHour = hour + 1;
				if (endHour > 24)
					endHour -= 24;
			} else {
				endHour = hour + 1;
				if (endHour == 24)
					endHour = 0;
			}
		}

		String start = hourToString(hour, isAM);
		String end = hourToString(endHour, endAM);
		return new String[] { start, end };
	}

	private String hourToString(int hour, boolean isAM) {
		String hourVal = "";
		if (ServiceUtils.isHourPrefix())
			hourVal = ServiceUtils.prefixZero(hour, 2) + ":00";
		else
			hourVal = Integer.toString(hour) + ":00";

		if (!ServiceUtils.is24Hours()) {
			if (isAM)
				hourVal += WebServiceMessages.getResource("scheduleStartTimeAM");
			else
				hourVal += WebServiceMessages.getResource("scheduleStartTimePM");
		}

		return hourVal;
	}

	public boolean isUEFIFirmware() {
		return this.getNativeFacade().IsFirmwareuEFI();
	}

	// retrieve service error message from error code
	public String getServiceError(String errorCode, Object[] arguments) {
		String errorMessage = null;
		try {
			errorMessage = WebServiceErrorMessages.getServiceError(errorCode, arguments);
		} catch (Exception e) {
			logger.error("Error occurred on parsing service error code" + e);
		}

		return errorMessage;
	}

	public boolean isShowUpdate() {
		return WSJNI.isShowUpdate();
	}

	public String generateHashValue(String plainText) {
		if (plainText == null || plainText.isEmpty())
			return null;
		try {
			MessageDigest disgst = MessageDigest.getInstance("MD5");
			disgst.update(plainText.getBytes());
			return new String(encodeHex(disgst.digest()));
		} catch (NoSuchAlgorithmException e) {
			logger.error("Failed to generate hash value", e);
		}
		return null;
	}

	public String generateHashValueForEncryptedText(String encryptedText) {
		if (encryptedText == null || encryptedText.isEmpty()) {
			return null;
		}

		String plainText = this.getNativeFacade().decrypt(encryptedText);
		return generateHashValue(plainText);
	}

	/**
	 * Converts an array of bytes into an array of characters representing the
	 * hexadecimal values of each byte in order. The returned array will be
	 * double the length of the passed array, as it takes two characters to
	 * represent any given byte.
	 * 
	 * @param data
	 * @return
	 */
	private char[] encodeHex(byte[] data) {
		char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		int l = data.length;

		char[] out = new char[l << 1];

		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}

		return out;
	}

	/**
	 * Validate the user name and password, if validate passed, then check the
	 * account in the registry, if the account in the registry is not valid,
	 * then update the registry account with current account for validate
	 * 
	 * @param username
	 *            The user name for validate
	 * @param password
	 *            The password
	 * @param domain
	 *            domain
	 * @return The UUID of current d2d
	 * @throws ServiceException
	 *             Failed to validate the account
	 */
	public String validateUserAndUpdateIfNeeded(String username, String password, String domain) throws ServiceException {
		validateLoginInfoFormat(username, password, domain); // may throw
																// "invalid
																// credentials
																// exception

		String uuid = validateUser(username, password, domain);
		// check admin account in the registry
		Account account = getNativeFacade().getAdminAccount();
		try {
			getNativeFacade().validateAdminAccount(account);
		} catch (Exception e) {
			logger.error("Validate Admin Account failed, we will try to validate with new account.");
			Account newAccount = new Account();
			String newUserName = username;
			if (domain != null && domain.length() > 0) {
				newUserName = domain + "\\" + username;
			}
			newAccount.setUserName(newUserName);
			newAccount.setPassword(password);
			try {
				getNativeFacade().validateAdminAccount(newAccount);
				logger.info("Update Admin Account with new account");
				getNativeFacade().saveAdminAccount(newAccount);
			} catch (Exception e1) {
				logger.error("Failed to update admin account.", e1);
			}
		}
		return uuid;
	}

	/**
	 * Validate the user name and password, if validate passed, then check the
	 * account in the registry, if the account in the registry is not valid,
	 * then update the registry account with current account for validate
	 * 
	 * @param username
	 *            The user name for validate
	 * @param password
	 *            The password
	 * @param domain
	 *            domain
	 * @param bGetNodID
	 *            get node ID or auth ID
	 * @return The UUID of current d2d
	 * @throws ServiceException
	 *             Failed to validate the account
	 */
	public String validateUserAndUpdateIfNeeded(String username, String password, String domain, boolean bGetNodID) throws ServiceException {
		validateLoginInfoFormat(username, password, domain); // may throw
																// "invalid
																// credentials
																// exception
		String uuid = validateUser(username, password, domain, bGetNodID);
		// check admin account in the registry
		Account account = getNativeFacade().getAdminAccount();
		try {
			getNativeFacade().validateAdminAccount(account);
		} catch (Exception e) {
			logger.error("Validate Admin Account failed, we will try to validate with new account.");
			Account newAccount = new Account();
			String newUserName = username;
			if (domain != null && domain.length() > 0) {
				newUserName = domain + "\\" + username;
			}
			newAccount.setUserName(newUserName);
			newAccount.setPassword(password);
			try {
				getNativeFacade().validateAdminAccount(newAccount);
				logger.info("Update Admin Account with new account");
				getNativeFacade().saveAdminAccount(newAccount);
			} catch (Exception e1) {
				logger.error("Failed to update admin account.", e1);
			}
		}
		return uuid;
	}

	public boolean isWin8() {
		try {
			JSystemInfo info = this.getNativeFacade().getSystemInfo();
			if (info != null) {
				return info.isWin8();
			}
		} catch (Exception e) {
			logger.error("Failed to get system info", e);
		}
		return false;
	}

	public boolean isExchange2013() {
		boolean result = false;
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(RegConstants.REGISTRY_EXCHANGE2013);
			logger.debug("REGISTRY_EXCHANGE2013 is: " + handle);
			if (handle != 0) {
				result = true;
			}

		} catch (Exception e) {
			logger.error("Read registry error", e);
		} finally {
			if (handle != 0)
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
		}

		return result;
	}

	public Date getServerTime() {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTimeInMillis(System.currentTimeMillis());
		return cal.getTime();
	}

	public int getServerPort() {
		try {
			String url = CommonUtil.getProductionServerURL();
			return Integer.parseInt(CommonUtil.getProductionServerPort(url));
		} catch (Exception e) {
			logger.error("Failed to get D2D port");
			return 8014;
		}
	}

	public String getServerProtocol() {
		try {
			String url = CommonUtil.getProductionServerURL();
			return CommonUtil.getProductionServerProtocol(url);
		} catch (Exception e) {
			logger.error("Failed to get server protocol");
			return null;
		}
	}

	public static boolean isRPSServer() {
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String rpsFlag = registry.getValue(handle, CommonService.REGISTRY_RPS_FLAG);
			if (!StringUtil.isEmptyOrNull(rpsFlag)) {
				return Integer.parseInt(rpsFlag) > 0;
			}
			return false;
		} catch (Exception e) {
			logger.error("Failed to check RPS server");
			return false;
		} finally {
			if (handle != 0) {
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
				}

			}
		}
	}

	public static boolean isSQLServerInstalled() {
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(RegConstants.REGISTRY_SQLSERVER);
			if (handle == 0) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to check SQL server registry");
			return false;
		} finally {
			if (handle != 0) {
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
				}

			}
		}
	}

	public static boolean isD2DInstalled() {
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			if (handle == 0) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to check d2d registry");
			return false;
		} finally {
			if (handle != 0) {
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
				}

			}
		}
	}

	public static boolean isExchangeServerInstalled() {
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(RegConstants.REGISTRY_EXCHANGESERVER);
			if (handle == 0) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to check Exchange server registry");
			return false;
		} finally {
			if (handle != 0) {
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
				}

			}
		}
	}

	/**
	 * Called when stop tomcat to shutdown the thread pools and schedulers.
	 */

	public ExecutorService getUtilTheadPool() {
		return TheadPoolManager.getThreadPool(TheadPoolManager.UtilTheadPool);
	}

	public void cleanUpThreads() {
		try {
			logger.info("Shutdown VSphere scheduler");
			VSphereService.getInstance().getBackupSchedule().shutdown();
		} catch (Throwable e) {
			logger.error("Failed to shutdown vsphere scheduler", e);
		}

		try {
			logger.info("Shutdown VSphere catalog scheduler");
			VSphereService.getInstance().getCatalogScheduler().shutdown();
		} catch (Throwable e) {
			logger.error("Failed to shutdown vsphere catalog scheduler", e);
		}

		logger.info("Shutdown vsphere base job thread pool");
		BaseVSphereJob.shutDownThreadPool();
		logger.info("Shutdown base job thread pool");
		BaseJob.pool.shutdownNow();
		logger.info("Shutdown vsphere merge job thread pool");
		VSphereMergeJob.pool.shutdownNow();

		RemoteVCMSessionMonitor.getInstance().shutDown();

		getUtilTheadPool().shutdownNow();
	}

	public void cancelAllJobs(String vmInstanceUUID, int reason) throws ServiceException {
		if (!StringUtil.isEmptyOrNull(vmInstanceUUID)) {
			logger.debug("VM instance uuid is null, it is for D2D jobs");
			synchronized (jms.getVMJobMonitorMap()) {
				cancelJobs(jms.getVMJobMonitorMap().get(vmInstanceUUID), reason, vmInstanceUUID);
			}
		} else {
			synchronized (jms.getJobMonitorMap()) {
				cancelJobs(jms.getJobMonitorMap(), reason, "");
			}
		}
	}

	private void cancelJobs(Map<String, Map<Long, JobMonitor>> jobMonitors, int reason, String vmInstanceUUID) throws ServiceException {
	/*	for (Map<Long, JobMonitor> jms : jobMonitors.values()) {
			for (JobMonitor jobMonitor : jms.values()) {
				this.cancelJob(jobMonitor.getJobId(), reason, vmInstanceUUID);
			}
		}
	 */
	//fix for 218575
	if(jobMonitors!=null){
			
			if(jobMonitors.size()==0 || jobMonitors.values().size()==0){
				VSphereService.getInstance().cancelWaitingJob(vmInstanceUUID);
				return;
			}
			
			for (Map<Long, JobMonitor> jms : jobMonitors.values()) {
				for (JobMonitor jobMonitor : jms.values()) {
					
					if(jobMonitor.getJobId()!=0)
						this.cancelJob(jobMonitor.getJobId(), reason, vmInstanceUUID);
					else{
						logger.info("job id is zero. could be waiting job");
						VSphereService.getInstance().cancelWaitingJob(vmInstanceUUID);
					}
				}
			}
		}
		//it could be in the waiting queue
		else{
			logger.info("there is no actual job monitor exist means it might be in the waiting queue");
			VSphereService.getInstance().cancelWaitingJob(vmInstanceUUID);
		}
	}

	public void updateThrottling4AllJobs(String vmInstanceUUID, long throttling) throws ServiceException {
		if (!StringUtil.isEmptyOrNull(vmInstanceUUID)) {
			synchronized (jms.getVMJobMonitorMap()) {
				updateThrottling4Jobs(jms.getVMJobMonitorMap().get(vmInstanceUUID), throttling);
			}
		} else {
			synchronized (jms.getJobMonitorMap()) {
				logger.debug("VM instance uuid is null, it is for D2D jobs");
				updateThrottling4Jobs(jms.getJobMonitorMap(), throttling);
			}
		}
	}

	private void updateThrottling4Jobs(Map<String, Map<Long, JobMonitor>> jobMonitors, long throttling) throws ServiceException {
		if (jobMonitors == null)
			return;
		for (Map<Long, JobMonitor> jms : jobMonitors.values()) {
			for (JobMonitor jobMonitor : jms.values()) {
				if (jobMonitor.getJobType() == Constants.AF_JOBTYPE_BACKUP || jobMonitor.getJobType()==Constants.AF_JOBTYPE_VM_BACKUP)
					this.updateThrottling4Job(jobMonitor.getJobId(), throttling);
			}
		}
	}

	private void updateThrottling4Job(long jobID, long throttling) throws ServiceException {
		logger.debug("updateThrottling4Job");
		int returnVlaue;
		try {
			returnVlaue = this.getNativeFacade().updateThrottling(jobID, throttling);
		} catch (Throwable e) {
			throw this.generateInternalErrorAxisFault();
		}
		if (returnVlaue != 0)
			throw generateAxisFault(FlashServiceErrorCode.Common_UpdateThrottling4JobFailed);
	}

	public synchronized String getServerSID() {
		if (this.d2dServerSID == null) {
			d2dServerSID = WSJNI.getD2DServerSID();
		}
		return this.d2dServerSID;
	}

	public FlashJobHistoryResult getJobHistory(long start, long request, FlashJobHistoryFilter filter) {
		JJobHistoryResult result = CommonNativeInstance.getICommonNative().getJobHistory(start, request,
				JobHistoryConverter.converFlashFilterToJFilterCol(filter));

		return JobHistoryConverter.convertJResultToFlashResult(result);
	}

	public D2DStatus checkD2DStatusFromEdgeCM() {
		D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
		EdgeRegInfo edgeRegInfo = edgeReg.getEdgeRegInfo(ApplicationType.CentralManagement);
		if (edgeRegInfo == null || edgeRegInfo.getEdgeWSDL() == null || edgeRegInfo.getEdgeWSDL().isEmpty()) {
			logger.debug("There is no local registration information for CentralManagement");
			return D2DStatus.StandAlone;
		}

		IEdgeCM4D2D proxy = WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(), IEdgeCM4D2D.class);

		if (proxy == null) {
			logger.error("Failed to get Edge proxy handle!");
			return D2DStatus.Unknown;
		}

		try {
			proxy.validateUserByUUID(edgeRegInfo.getEdgeUUID());
		} catch (EdgeServiceFault e) {
			logger.error("Failed to establish connection to Edge Server(login failed).", e);
			return D2DStatus.Unknown;
		}

		try {
			D2DInfo d2dInfo = new D2DInfo();
			d2dInfo.setType(D2DType.WindowsD2D);
			d2dInfo.setNodeUuid(getNodeUUID());
			d2dInfo.setPlanUuid(getPlanID());
			d2dInfo.setInstanceUuid(d2dInfo.getNodeUuid());
			return proxy.checkD2DStatus(d2dInfo);
		} catch (EdgeServiceFault e) {
			logger.error("Failed to check D2D Status.", e);
			return D2DStatus.Unknown;
		}
	}

	public String getPlanID() {
		try {
			String planId = BackupService.getInstance().getBackupConfiguration() == null ? null : BackupService.getInstance().getBackupConfiguration()
					.getPlanId();
			if (StringUtil.isEmptyOrNull(planId)) {
				EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.CentralManagement);
				if (edgeRegInfo == null)
				{
					logger.info("getPlanID: edgeRegInfo is NULL");
					return null;
				}
				String policyUuid = edgeRegInfo.getPolicyUuids().get("Default");
				if (StringUtil.isEmptyOrNull(policyUuid)) {				
					logger.info("getPlanID: policyUuid is NULL");
					return null;				
				} else {
					planId = policyUuid.split(":")[0];
				}
			}
			logger.info("getPlanID : returnning :" + planId);
			return planId;
		} catch (ServiceException e) {
			logger.error(e);
			return null;
		}
	}

	public boolean isStandAlone() {
		EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.CentralManagement);
		;
		if (edgeRegInfo == null || StringUtil.isEmptyOrNull(edgeRegInfo.getEdgeWSDL()) || StringUtil.isEmptyOrNull(edgeRegInfo.getEdgeUUID())) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isCatalogJobInQueue(long queueType, long sessionNumber, String vmInstanceUUID){
		
		String binpath = ServiceContext.getInstance().getBinFolderPath();
		
		String tempSessionNumber = "0000000001";
		String jsName = "JS_" + tempSessionNumber.substring(0,tempSessionNumber.length()-String.valueOf(sessionNumber).length())+sessionNumber + "_13.xml";
		
		String queueFolder = "";
		if(queueType == CatalogQueueType.ONDEMAND_JOB)
			queueFolder = "OnDemand";
		else if(queueType == CatalogQueueType.REGULAR_JOB)
			queueFolder = "Regular";
		else 
			return false;
			
		String targetFullJSName = binpath + "\\JobQueue\\"+ queueFolder + "\\" + jsName;
		if(vmInstanceUUID != null && !vmInstanceUUID.isEmpty()){
			targetFullJSName =  binpath + "\\JobQueue\\" + vmInstanceUUID + "\\" + queueFolder + "\\" + jsName;
		}
		File file = new File(targetFullJSName);
		if(file.exists()) {
			return true;
		}
		
		return false;		
	}
	
	public long getCacheLicenseExpiration() {
		int day = 0;
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(RegConstants.REGISTRY_WEBSERVICE);
			logger.debug("Handle of REGISTRY_CACHE_LICENSE_EXPIRATION is: " + handle);
			if (handle != 0) {
				String value = registry.getValue(handle, RegConstants.REGISTRY_CACHE_LICENSE_EXPIRATION);
				if(!StringUtil.isEmptyOrNull(value)){
					day = Integer.parseInt(value);
					if(day < 0){
						day = 0;
					}else if(day > 3){//the maximum of cache license expiration is 3 day
						day = 3;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Read registry error", e);
		} finally {
			if (handle != 0)
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
				}
		}
		if(day == 0){
			logger.info("The expiration of local cache license is 25 hours.");
			return 25*60*60*1000L;
		}else{
			logger.info("The expiration of local cache license is " + day + " day.");
			return day*24*60*60*1000L;
		}
		
	}
}
