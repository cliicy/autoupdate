package com.ca.arcflash.webservice.servlet;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ProxySelector;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.impl.StdSchedulerFactory;
import org.w3c.dom.Document;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.MessageFormatEx;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.failover.HyperVFailoverCommand;
import com.ca.arcflash.failover.VMwareCenterServerFailoverCommand;
import com.ca.arcflash.failover.VMwareESXFailoverCommand;
import com.ca.arcflash.ha.alert.EmailAlertCommand;
import com.ca.arcflash.ha.model.manager.HeartBeatModelManager;
import com.ca.arcflash.job.AFJob;
import com.ca.arcflash.jobqueue.JobQueueFactory;
import com.ca.arcflash.jobscript.alert.AlertJobScript;
import com.ca.arcflash.jobscript.base.JobScript;
import com.ca.arcflash.jobscript.base.JobType;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.listener.manager.IListenerDao;
import com.ca.arcflash.listener.manager.ListenerManager;
import com.ca.arcflash.listener.service.event.FlashEvent;
import com.ca.arcflash.replication.ReplicationService;
import com.ca.arcflash.service.common.ActivityLogSyncher;
import com.ca.arcflash.service.common.JobHistorySyncher;
import com.ca.arcflash.service.common.PollingService;
import com.ca.arcflash.service.common.ReplicationSyncher;
import com.ca.arcflash.service.jni.ICommonNative;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.service.util.ListenerDaoImpl;
import com.ca.arcflash.service.util.LogLevelMonitor;
import com.ca.arcflash.webservice.FlashServiceImpl;
import com.ca.arcflash.webservice.data.archive.JArchiveJob;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegStatusRefresh;
import com.ca.arcflash.webservice.edge.d2dstatus.SyncD2DStatusService;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.edge.datasync.job.JobSyncService;
import com.ca.arcflash.webservice.edge.notify.EdgeNotifyServiceUtil;
import com.ca.arcflash.webservice.edge.srmagent.SrmAlertMonitor;
import com.ca.arcflash.webservice.edge.srmagent.SrmJniCaller;
import com.ca.arcflash.webservice.heartbeat.ConcreteHeartBeatCommand;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.nimsoft.NimsoftService;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.D2DJobMonitorSyncher;
import com.ca.arcflash.webservice.scheduler.MakeUp;
import com.ca.arcflash.webservice.scheduler.RemoteVCMSessionMonitor;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BackupThrottleService;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.CallbackService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.DeleteArchiveService;
import com.ca.arcflash.webservice.service.DiskMonitorService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.InstantVMService;
import com.ca.arcflash.webservice.service.MergeService;
import com.ca.arcflash.webservice.service.PurgeArchiveService;
import com.ca.arcflash.webservice.service.RegConstants;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSPhereFailoverService;
import com.ca.arcflash.webservice.service.internal.D2DIPMonitor;
import com.ca.arcflash.webservice.service.internal.PatchManager;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.ServiceUtils;

public class D2DInitor implements IInitable {
	private static final Logger logger = Logger.getLogger(D2DInitor.class);
	private static String dataFolderPath = "";
	private static String logFilePath = "";
	private static String rssFilePath = "";
	private String strD2DHomePath ="";
	public static final String D2D_PROPERTIES_NAME = "commonProp.properties";
	private static final String PARAM_NAME_DEFAULT_PROXY_SELECTOR = "defaultProxySelector";

	private static D2DInitor instance = null;
	private static ServletContextEvent sce;
	
	public static D2DInitor getInstance(ServletContextEvent context) {
		if (instance == null) {
			instance = new D2DInitor();
			instance.sce = context;
		}
		return instance;
	}

	private D2DInitor() {
	}
	
	@Override
	public void initialize() {
		logger.debug("init Agent");
		try {
			ServiceContext.getInstance().setLocalMachineName(InetAddress.getLocalHost().getHostName().toUpperCase());
		} catch (UnknownHostException e) {
			logger.error("get local host error, set it to localhost", e);
			ServiceContext.getInstance().setLocalMachineName("localhost");
		}
		
		strD2DHomePath = Util.getAgentHomePath();
		trustAllSSL();
		prepareEnvironment(sce);
		configLog4J(sce);
		MessageFormatEx.init(strD2DHomePath + "Configuration\\");	//zxh,do MessageFormatEx init
		configServiceContext(sce);
		initProxySelector(sce);

		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		ICommonNative iCommonNative = CommonNativeInstance.getICommonNative(); 
		IListenerDao listenerDao = new ListenerDaoImpl("Listeners-D2D.xml", iCommonNative);
		ListenerManager.getInstance().initialize(listenerDao);
		configSessionDismountTime();
		getCustomizedInfo();
		// start d2d callback in C++
		CallbackService.getInstance().startD2dCallback();
		CommonService.getInstance().getNativeFacade().startNICMonitor();
		CommonService.getInstance().getNativeFacade().startClusterMonitor();
		D2DIPMonitor.startMonitorIP();

		// fix issue 18954752 add time zone information about server
		CommonService.getInstance().getNodeUUID();
		Util.logTimeZoneInfo(nativeFacade);

		getD2DProperties(strD2DHomePath + "Configuration\\");

		JobHistorySyncher jobHistorySyncher = new JobHistorySyncher(CommonService.getInstance().getNodeUUID(),
				FlashEvent.Source.D2D, iCommonNative);
		PollingService.getInstance().enroll(jobHistorySyncher);
		ReplicationSyncher replicationSyncher = new ReplicationSyncher(CommonService.getInstance().getNodeUUID(),
				FlashEvent.Source.D2D, iCommonNative, true);
		PollingService.getInstance().enroll(replicationSyncher);
		PollingService.getInstance().initialize();
		D2DJobMonitorSyncher.startSync();
		// sonle01: start D2D data sync Thread
		EdgeDataSynchronization.startSyncThread();

		// lijxi03 check if upgrade from 16, if yes we will change backup
		// configuration file to enable catalog generation when file copy
		// enabled.
		BackupService.getInstance().checkIfUpgradeFromOldBuild();

		notifyEdgeD2DVersion();
		// sonle01: start registration status refresh thread to refresh reg
		// status with Edge
		D2DEdgeRegStatusRefresh RegStatusRefresh = new D2DEdgeRegStatusRefresh();
		RegStatusRefresh.startRefreshThread();
		
		BackupService.getInstance().configSchedule();
		
		BackupService.getInstance().dealWithMissedBackup();
		DiskMonitorService.getInstance().startDiskMonitorJob();
		DiskMonitorService.getInstance().startVSphereDiskMonitorJob();
		BrowserService.getInstance().startMountManager();
		BackupService.getInstance().regenerateWriterMetadata();
		copyRssToTarget(dataFolderPath, rssFilePath, "\\jobfeed.xml");

		CommonService.getInstance().resumeJobOrStartJobInQueue();
		CommonService.getInstance().scheduleMoveLog();
		

		// //Initialize Patch Manager process
		Util.launchPatchManager();
		
		// start a thread to send alter mail
		PatchManager.getInstance().startMailAlterThread();

		// if (SrmAlertMonitor.isManagedByEdge()) {
		// SRM tries to startup AgPkiMon.exe
		startSRMAndPkiMonitor();
		// }

		initializeJobQueue();

		// check whether there is any archive job need run
		checkAndLaunchArchiveJob();		

		NimsoftService.getInstance().startD2DProbeMonitorThread();

		// For merge job
		MergeService.getInstance().fixMergeStatusAfterRestart();
		MergeService.getInstance().scheduleMergeJob();

		CommonService.getInstance().scheduleEmailSender(12, 0);

		// For throttle job
		BackupThrottleService.getInstance().scheduleBackupThrottleJob();

		// Send backup status to Edge
//		SyncD2DStatusService.getInstance().syncAllBackupStatus2Edge();

		// Start merge job synchronization
		JobSyncService.getInstance().start();
		
		// For Instant VM job
		InstantVMService.getInstance().startMonitorIVMStatus();
		logger.debug("init Agent exit");

	}

	private void configLog4J(ServletContextEvent sce) {
		InputStream input = null;
		try {

			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(RegConstants.REGISTRY_INSTALLPATH);
			String homeFolder = registry.getValue(handle, RegConstants.REGISTRY_KEY_PATH);
			String configurtionFolder = homeFolder + "Configuration\\";
			registry.closeKey(handle);

			String log4jFile = configurtionFolder + "log4j-webservice.properties";

			input = new FileInputStream(log4jFile);
			java.util.Properties props = new java.util.Properties();
			props.load(input);

			props.setProperty("log4j.appender.logout.File", logFilePath);
			PropertyConfigurator.configure(props);

			LogLevelMonitor.startMonitor(configurtionFolder, "log4j-webservice.properties");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ServiceUtils.closeResource(input);
		}
	}

	private void configServiceContext(ServletContextEvent sce) {
		ServiceContext.getInstance().setHomeFolderPath(strD2DHomePath);
		ServiceContext.getInstance().setDataFolderPath(dataFolderPath);
		ServiceContext.getInstance().setLogFolderPath(strD2DHomePath + "Logs");
		ServiceContext.getInstance().setSubscriptionConfigurationFilePath(
				dataFolderPath + "\\SubscriptionConfiguration.xml");
		ServiceContext.getInstance().setBackupConfigurationFilePath(dataFolderPath + "\\BackupConfiguration.xml");
		ServiceContext.getInstance().setPreferencesConfigurationFilePath(
				dataFolderPath + "\\PreferencesConfiguration.xml");
		ServiceContext.getInstance().setArchiveConfigurationFilePath(dataFolderPath + "\\ArchiveConfiguration.xml");
		ServiceContext.getInstance().setArchiveSourceDeleteConfigurationFilePath(dataFolderPath + "\\ArchiveSourceDeleteConfiguration.xml");
		ServiceContext.getInstance()
				.setArchiveUIConfigurationFilePath(dataFolderPath + "\\ArchiveConfiguration_UI.xml");
		ServiceContext.getInstance().setArchiveSourcePoliciesFilePath(dataFolderPath + "\\ArchiveSourcePolicies.xml");
		ServiceContext.getInstance().setTrustedHostFilePath(dataFolderPath + "\\TrustedHosts.xml");
		ServiceContext.getInstance().setRetryPolicyFilePath(dataFolderPath + "\\RetryPolicy.xml");
		ServiceContext.getInstance().setCloudVendorInfoFilePath(dataFolderPath + "\\CloudVendorInformation.xml");
		ServiceContext.getInstance().setTomcatFilePath(rssFilePath);
		ServiceContext.getInstance().setApmSettingsIniFilePath(strD2DHomePath + "\\Update Manager\\D2DPMSettings.INI");
		ServiceContext.getInstance().setD2DPMClientXMLFilePath(strD2DHomePath + "\\Update Manager\\D2DPMClient.xml");
		ServiceContext.getInstance().setScheduledExportConfigurationPath(
				dataFolderPath + "\\ScheduledExportConfiguration.xml");
		ServiceContext.getInstance().setBinFolderPath(strD2DHomePath + "\\BIN");

		ServiceContext.getInstance().setVsphereBackupConfigurationFolderPath(dataFolderPath + "\\VMConfiguration");
		// October sprint
		ServiceContext.getInstance().setStorageApplianceConfigurationFilePath(dataFolderPath + "\\StorageApplianceDetails.xml");
		ServiceContext.getInstance().setVsphereScheduleExportConfigurationFolderPath(dataFolderPath + "\\VMScheduleExport");
		ServiceContext.getInstance().setAutoUpdateSettingsFilePath(
				BackupService.getInstance().getNativeFacade().getUpdateSettingsFile() );
		
		ServiceContext.getInstance().setArchiveToTapeFilePath(dataFolderPath + "\\ArchiveToTapeConfig.xml");
		
		ServiceContext.getInstance().setDiagInfoCollectorConfigurationFilePath(ServiceContext.getInstance().getBinFolderPath() + "\\DiagnosticUtility\\DiagInfoCollectorConfig.xml");
	}

	private void configSessionDismountTime() {
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String time = registry.getValue(handle, RegConstants.REGISTRY_KEY_SESSION_DISMOUNT_TIME);
			if (time != null && !time.equals("")) {
				ServiceContext.getInstance().setSessionDismountTime(Integer.parseInt(time));
			}

			logger.info("Session Dismount time: " + time);
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		} finally {
			if (handle != 0) {
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
				}
				;
			}
		}
	}

	private void getCustomizedInfo() {
		File cus = new File(strD2DHomePath + "Customization\\Customized.xml");
		try {
			if (cus.exists()) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document dom = builder.parse(cus);
				XPath xpath = XPathFactory.newInstance().newXPath();
				String companyName = xpath.evaluate("/Customization/CompanyName/text()", dom);
				if (companyName != null) {
					companyName = companyName.trim();
				}
				String productNameD2D = xpath.evaluate("/Customization/ProductNameD2D/text()", dom);
				if (productNameD2D != null) {
					productNameD2D = productNameD2D.trim();
				}
				ServiceContext.getInstance().setCompanyName(companyName);
				ServiceContext.getInstance().setProductNameD2D(productNameD2D);
			}
		} catch (Throwable t) {
			logger.error("Get customization information error", t);
		}
	}

	private void prepareEnvironment(ServletContextEvent sce) {
		try {

			System.setProperty(StdSchedulerFactory.PROPERTIES_FILE,
					"com/ca/arcflash/webservice/scheduler/default-sched-quartz.properties");

			String enableSessionCheck = sce.getServletContext().getInitParameter("enableSessionCheck");
			if (enableSessionCheck != null && enableSessionCheck.toLowerCase().equals("false"))
				FlashServiceImpl.setEnableSessionCheckV2(false);

			dataFolderPath = sce.getServletContext().getRealPath("\\WEB-INF");
			logFilePath = sce.getServletContext().getRealPath("\\WEB-INF\\WebService.log");

			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(RegConstants.REGISTRY_INSTALLPATH);
			String homeFolder = registry.getValue(handle, RegConstants.REGISTRY_KEY_PATH);
			registry.closeKey(handle);

			if (!StringUtil.isEmptyOrNull(homeFolder)) {
				logFilePath = (homeFolder + "Logs\\WebService.log").replace('\\', '/');
				dataFolderPath = homeFolder + "Configuration";
				rssFilePath = homeFolder + "TOMCAT\\webapps\\ROOT";
			}

			File file = new File(dataFolderPath);
			if (!file.exists())
				file.mkdir();
			
			ScheduleUtils.makeupFilePath = dataFolderPath+"\\makeup.xml";
			File makeupfile = new File(ScheduleUtils.makeupFilePath);
			if(!makeupfile.exists()){
				MakeUp mp = new MakeUp();				
				ScheduleUtils.saveMakeUp(mp);
			}else{			
				ScheduleUtils.loadMakeUp();		
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void notifyEdgeD2DVersion() {
		try {
//			logger.info("Start to notify d2d version...");
//			CommonService.getInstance().notifyD2DVersion();
//			logger.info("notify d2d version finished.");
			EdgeNotifyServiceUtil.notifyD2DVersion();
		} catch (Exception e) {
			logger.error("Error on update D2D version to Edge.", e);
		}
	}

	private void trustAllSSL() {
		try {
			CommonUtil.prepareTrustAllSSLEnv();
		} catch (Exception e) {
			logger.debug("Failed to prepare SSL env:" + e.getMessage());
		}
	}

	private void startSRMAndPkiMonitor() {
		try {
			SrmAlertMonitor.startMonitor();
			SrmJniCaller.startPkiMonitor();
		} catch (Throwable e) {
			logger.error("Failed to startup AgPkiMon.exe: " + e.getMessage());
		}
	}

	private void checkAndLaunchArchiveJob() {
		try {
			ArchiveService.getInstance().configArchiveSchedule();
			DeleteArchiveService.getInstance().configSchedule();
			PurgeArchiveService.getInstance().configJobSchedule();
			JArchiveJob archiveJob = new JArchiveJob();
			logger.debug("checking whether archive job needs to be submitted on start");
			if (ArchiveService.getInstance().checkSubmitArchiveJob(archiveJob)) {
				logger.debug("submitting archive job on start");
				ArchiveService.getInstance().submitArchiveJob("archiveJob", archiveJob);
			}
		} catch (ServiceException e) {
			logger.error("checkSubmitArchiveJob error when web service starts");
		}
	}

	@SuppressWarnings(value = { "rawtypes", "unchecked" })
	private void initProxySelector(ServletContextEvent sce) {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("init the ProxySelector");
			}
			String proxySelectorClassName = sce.getServletContext().getInitParameter(PARAM_NAME_DEFAULT_PROXY_SELECTOR);
			if (proxySelectorClassName != null && proxySelectorClassName.length() > 0) {
				Class proxySelectorClass = Class.forName(proxySelectorClassName);
				if (proxySelectorClass != null) {
					Method getInstanceMethod = proxySelectorClass.getDeclaredMethod("getInstance", null);
					Object returnValue = getInstanceMethod.invoke(null, new Object[] {});
					if (returnValue instanceof ProxySelector) {
						ProxySelector.setDefault((ProxySelector) returnValue);
						if (logger.isInfoEnabled()) {
							logger.info("Register the proxy selector successfully!");
						}
					} else {
						logger.error(MessageFormatEx.format("{0} is not an instance of ProxySelector",
								proxySelectorClassName));
					}
				} else {
					logger.error(MessageFormatEx.format("Failed to load defaultProxyClass:{0}", proxySelectorClassName));
				}
			} else {
				logger.error("The defaultProxySelector is not configured!");
			}
		} catch (Throwable e) {
			logger.error("Failed to init the ProxySelector.", e);
		}
	}

	private void initializeJobQueue() {

		// monitor service part
		{
			{
				Collection<AFJob> findByJobType = JobQueueFactory.getDefaultJobQueue().findByJobType(JobType.Failover);

				Iterator<AFJob> iterator = findByJobType.iterator();
				while (iterator.hasNext()) {
					AFJob afJob = (AFJob) iterator.next();
					JobScript jobScript = afJob.getJobScript();
					if (jobScript != null && jobScript instanceof FailoverJobScript) {
						FailoverJobScript script = (FailoverJobScript) jobScript;
						if (script.getFailoverMechanism().isEmpty())
							continue;
						Virtualization virtualization = script.getFailoverMechanism().get(0);

						switch (virtualization.getVirtualizationType()) {
						case HyperV: {
							script.setFailoverCommand(new HyperVFailoverCommand());
							break;
						}
						case VMwareESX: {
							script.setFailoverCommand(new VMwareESXFailoverCommand());
							break;
						}
						case VMwareVirtualCenter: {
							script.setFailoverCommand(new VMwareCenterServerFailoverCommand());
							break;
						}
						}

					}
				}
			}
			{
				Collection<AFJob> findByJobType = JobQueueFactory.getDefaultJobQueue().findByJobType(
						JobType.Replication);

				Iterator<AFJob> iterator = findByJobType.iterator();
				while (iterator.hasNext()) {
					AFJob afJob = (AFJob) iterator.next();
					JobScript jobScript = afJob.getJobScript();
					if (jobScript != null && jobScript instanceof ReplicationJobScript) {
						ReplicationJobScript repli = (ReplicationJobScript) jobScript;
						HAService.getInstance().setReplicationCommand(repli);

						if (repli.getBackupToRPS())
							ActivityLogSyncher.getInstance().addVM(repli.getAFGuid());
						// Smart Copy, always add Observer
						// if(repli.getAutoReplicate())
						// HAService.getInstance().initilizeObserver();
						// Smart Copy
					} else if (jobScript != null && jobScript instanceof HeartBeatJobScript) {
						HeartBeatJobScript heartB = (HeartBeatJobScript) jobScript;
						heartB.setHeartBeatCommand(new ConcreteHeartBeatCommand());
					}
				}
			}

			{
				Collection<AFJob> findByJobType = JobQueueFactory.getDefaultJobQueue().findByJobType(JobType.HeartBeat);

				VSPhereFailoverService.getInstance().startAllMoniteeHeartBeat(findByJobType);

				// Iterator<AFJob> iterator = findByJobType.iterator();
				// while (iterator.hasNext()) {
				// AFJob afJob = (AFJob) iterator.next();
				// JobScript jobScript = afJob.getJobScript();
				// if (jobScript != null
				// && jobScript instanceof HeartBeatJobScript) {
				// HeartBeatJobScript heartB = (HeartBeatJobScript) jobScript;
				// heartB
				// .setHeartBeatCommand(new ConcreteHeartBeatCommand());
				// if(afJob.getJobStatus().getStatus() ==
				// JobStatus.Status.Active || afJob.getJobStatus().getStatus()
				// == JobStatus.Status.Failed){
				// if(HACommon.isTargetPhysicalMachine(heartB.getAFGuid())){
				// HAService.getInstance().startHeartBeat(heartB.getAFGuid());
				// }
				// }
				// }
				// }
			}

			// Init the alert job script
			{
				Collection<AFJob> findByJobType = JobQueueFactory.getDefaultJobQueue().findByJobType(JobType.Alert);

				Iterator<AFJob> iterator = findByJobType.iterator();
				while (iterator.hasNext()) {
					AFJob afJob = (AFJob) iterator.next();
					JobScript jobScript = afJob.getJobScript();
					if (jobScript != null && jobScript instanceof AlertJobScript) {

						AlertJobScript alertJobScript = (AlertJobScript) jobScript;
						alertJobScript.setAlertCommand(new EmailAlertCommand());
					}
				}
			}
			// end

			String heartbeatXml = CommonUtil.D2DHAInstallPath + "Configuration\\HeartBeatModel.xml";
			if (logger.isDebugEnabled()) {
				logger.debug("Loading HeartBeat Model from " + heartbeatXml + " ....");
			}
			HeartBeatModelManager.initizlize(heartbeatXml);

			if (logger.isDebugEnabled()) {
				logger.debug("Starting replication server ....");
			}
			ReplicationService.startHATransServerForMonitor();
		}

	}

	private Properties getD2DProperties(String configPath) {
		Properties d2dProperties = new Properties();
		FileInputStream fis = null;
		try {
			File file = new File(configPath + D2D_PROPERTIES_NAME);
			if (!file.exists()) {
				file.createNewFile();
			}
			fis = new FileInputStream(file);
			d2dProperties.load(fis);
		} catch (Exception e) {
			logger.error("Failed to load D2D properties", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
		}
		ServiceContext.getInstance().setD2dProperties(d2dProperties);

		return d2dProperties;
	}

	private void copyRssToTarget(String srcPath, String targetPath, String rssXML) {
		try {
			File in = new File(srcPath + rssXML);
			if (!in.exists()) {
				return;
			} else {
				in.setWritable(true);
			}
			StringUtil.copy(in.getAbsolutePath(), targetPath + rssXML);

			FileFilter filter = new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return (pathname.getName().endsWith("html") && pathname.getName().startsWith("job"));
				}
			};

			File srcFolder = new File(srcPath);
			File[] srcHtmlFiles = srcFolder.listFiles(filter);
			if (srcHtmlFiles == null || srcHtmlFiles.length == 0)
				return;

			for (File srcFile : srcHtmlFiles) {
				StringUtil.copy(srcPath + "\\" + srcFile.getName(), targetPath + "\\" + srcFile.getName());

				File targFolder = new File(targetPath);
				File[] htmlFiles = targFolder.listFiles(filter);
				if (htmlFiles.length > Constants.FAILED_JOB_RSS_MAX) {
					File oldest = null;
					for (int i = 0; i < htmlFiles.length; i++) {
						if (i == 0 && htmlFiles[i] != null) {
							oldest = htmlFiles[i];
						} else if (oldest.lastModified() > htmlFiles[i].lastModified()) {
							oldest = htmlFiles[i];
						}
					}
					if (oldest.delete()) {
						logger.debug("Oldest RSS file = " + oldest.getName());
						logger.info("Successfully deleted oldest file");
					} else {
						logger.error("Failed to delete oldest failed job html file");
					}
				}
			}
		} catch (Exception e) {
			logger.error("copyRssToTarget() - end " + e.getMessage(), e);
		}
	}

}
