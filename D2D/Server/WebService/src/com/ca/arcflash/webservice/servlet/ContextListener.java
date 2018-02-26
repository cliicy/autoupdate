package com.ca.arcflash.webservice.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.MessageFormatEx;
import com.ca.arcflash.listener.manager.ListenerManager;
import com.ca.arcflash.service.common.ActivityLogSyncher;
import com.ca.arcflash.service.common.PollingService;
import com.ca.arcflash.service.jni.CommonJNIProxy;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.service.util.LogLevelMonitor;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeD2DRegConfigImpl;
import com.ca.arcflash.webservice.edge.d2dstatus.VCMStatusSyncer;
import com.ca.arcflash.webservice.edge.d2dstatus.statuscollectors.VCMStatusSyncerCollector;
import com.ca.arcflash.webservice.edge.datasync.job.JobSyncService;
import com.ca.arcflash.webservice.edge.srmagent.SrmAlertMonitor;
import com.ca.arcflash.webservice.edge.srmagent.SrmJniCaller;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CallbackService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.JobMonitorService;
import com.ca.arcflash.webservice.service.MergeService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.VSphereMergeService;
import com.ca.arcflash.webservice.service.internal.D2DIPMonitor;
import com.ca.arcflash.webservice.service.internal.VSphereJobQueue;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcflash.webservice.util.OnlineDiskThread;
import com.ca.arcflash.webservice.util.TheadPoolManager;
import com.ca.ha.webservice.jni.HyperVJNI;

public class ContextListener implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(ContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		loadNativeFacade();
		
		CommonNativeInstance.initialize(CommonJNIProxy.JNIType.WSJNI);
		configureDisplayLanguage();
		WebServiceFactory.setEdgeD2DRegConfig(new EdgeD2DRegConfigImpl());
		
		List<IInitable> inits = getInitables(sce);
		for (IInitable it : inits)
			it.initialize();
		
		configInFailoverAndV2PMachine();
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {		
		logger.info("Tomcat is beginging to shut down......");
		ServiceContext.getInstance().setServiceStoped(true);
		CommonService.getInstance().getNativeFacade().stopNICMonitor();
		CommonService.getInstance().getNativeFacade().stopClusterMonitor();
		// Stop merge job synchronization.
		VCMStatusSyncerCollector.getInstance().stopSync();
		VCMStatusSyncer.getInstance().stopSync();
		JobSyncService.getInstance().stop();
		ActivityLogSyncher.getInstance().stopSync();
		PollingService.getInstance().stopSync();
		VSphereJobQueue.getInstance().saveJobQueueToFile();
		MergeService.getInstance().saveMergeStatus();
		VSphereMergeService.getInstance().saveMergeStatus();

		Util.destroyPathManager();
		
		// SRM tries to stop AgPkiMon.exe
		try {
			SrmJniCaller.stopPkiMonitor();
		} catch (Throwable e) {
			logger.error("Failed to stop AgPkiMon.exe: " + e.getMessage());
		}

		try {
			NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
			nativeFacade.stopHAServerProxy();
		} catch (Exception e) {
			logger.warn("contextDestroyed() - error, Fail to stop HAServerProxy: " + e.getMessage());
		}

		saveD2DProperties(Util.getAgentHomePath() + "Configuration\\");
		JobMonitorService.getInstance().cleanJobMonitor();

		// stop d2d callback in C++
		CallbackService.getInstance().stopD2dCallback();

		try {
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			if (scheduler.isStarted()) {
				scheduler.shutdown();
			}			
			
		} catch (Exception e) {
			logger.warn("contextDestroyed() - error, Fail to stop Quartz Scheduler: " + e.getMessage());
		}
		
		HAService.getInstance().destroy();
		
		BackupService.getInstance().destory();
		
		ArchiveService.getInstance().destory();

		CommonService.getInstance().cleanUpThreads();

		ListenerManager.getInstance().destroy();

		logger.info("Tomcat ends shuting down.");

		LogLevelMonitor.endMonitorLogLevel();
		
		try {
			SrmAlertMonitor.stopMonitor();
		} catch (Throwable e) {
			logger.error("Failed to stop SrmAlertMonitor: " + e.getMessage());
		}
		
		try {
			if(LogLevelMonitor.getWatcher()!= null){
				LogLevelMonitor.getWatcher().close();
			}
		} catch (Throwable e) {
			logger.error("Failed to stop LogLevelMonitor: " + e.getMessage());
		}
		
		MessageFormatEx.uninit(); //zxh,do MessageFormatEx uninit
		
		try {
			if(D2DIPMonitor.getWatcher()!= null){
				D2DIPMonitor.getWatcher().close();
			}
		} catch (Throwable e) {
			logger.error("Failed to stop D2DIPMonitor: " + e.getMessage());
		}
		
		VSphereJobQueue.getInstance().destroy();
		
		TheadPoolManager.destory();
		
		try {
			//cheda16: Don't comment this line, waiting for other schedule thread terminate. Sometime the tomcat shutdown too quickly, other thread failed to exit. 
			//This helps show that it's just Tomcat being a bit too quick on the draw with its "detection", and helps explain why it happens to some folks but not others 
			//(it's likely that many folks have other operations after the shutdown() call, which effectively causes a delay like the sleep() does). http://forums.terracotta.org/forums/posts/list/3479.page
			//Maybe it is due to a race condition between when Tomcat generates the report, and when the thread actually goes away.(https://code.google.com/p/myschedule/issues/detail?id=72			
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	private void loadNativeFacade() {
		logger.info("Loading Nativefacade library ...");
		try {
			System.loadLibrary("NativeFacade");
		} catch (Throwable e) {
			logger.error("ContextListener load NativeFacade failed" + e.getMessage());
		}
	}
	
	private List<IInitable> getInitables(ServletContextEvent sce) {
		return Arrays.asList(D2DInitor.getInstance(sce), VSphereInitor.getInstance(), VCMInitor.getInstance());
	}

	private void configInFailoverAndV2PMachine() {
		try {
			if (CommonUtil.isFailoverMode()) {
				logger.info("D2D system is running in Failover mode:" + CommonUtil.FailoverVM + "  ...");
				if (CommonUtil.isFailoverReboot() || BackupService.getInstance().getNativeFacade().checkBMRPerformed()) {
					logger.info("D2D system is running in Failover reboot mode or BMR mode.");
					BackupService.getInstance().stopAllBackJobs();
					logger.info("Pause Virtual Standby job in Failover reboot mode or BMR mode.");
					try {
						HAService.getInstance().enableAutoOfflieCopy(null, false);
					} catch (Exception e) {
						logger.info("Pause Virtual Standby job error.", e);
					}
				}

				if (CommonUtil.isFailoverReboot()) {
					logger.info("D2D system is running in Failover reboot mode.");
					int installResult = 3010;
					if (CommonUtil.FailoverVM.equals(CommonUtil.FailoverVM_HYPERV)) {
						logger.info("Running in failover mode as HyperV, install Integration service ...");
						installResult = HyperVJNI.InstallIntegrationService(true, false);
						logger.info("Install Integration service successfully, ret=" + installResult);
					} else if (CommonUtil.FailoverVM.equals(CommonUtil.FailoverVM_VMWARE)) {
						logger.info("Running in failover mode in VMWare, installing vmware tools....");
						BackupService.getInstance().getNativeFacade().InstallVMwareTools();
						logger.info("Intall VMware Tools successfully!!!");
					}
					CommonUtil.removeFailoverRebootFlag();
					logger.info("remove the failover reboot flag");
					if (installResult == 3010 || installResult == 3011) {
						BackupService.getInstance().getNativeFacade().RebootSystem(true);
						logger.info("reboot system after installing wmware tools or integration service");
						System.exit(0);
					} else if (installResult == 0)
						logger.info("success install wmware tools or integration service");
					else {
						logger.error("fail to install wmware tools or integration service");
					}
				}

				if (CommonUtil.isNeedOnlineDisk()) {
					logger.info("Begin to start the online disk thread...");
					OnlineDiskThread onlineDiskThread = new OnlineDiskThread();
					onlineDiskThread.start();
				}
			}
		} catch (Exception e) {
			logger.error("contextInitialized() - error:" + e.getMessage(), e);
		}
	}
	
	private void configureDisplayLanguage(){	
		String lang=(new NativeFacadeImpl()).getDisplayLanguage();
		// supported language IDs are
		// de, en, es, fr, it, ja, pt, zh_CN, zh_TW
		// they can be set in registry 
		// HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection: Language:string
		logger.info("System will run in lang: " + lang);
		
		String [] langids = lang.split("_"); // for zh_CN / zh_TW
		
		if(langids != null && langids.length > 1) {
			//correct usage is - new Locale("zh", "CN");
			Locale.setDefault(new Locale(langids[0], langids[1]));
		}
		else {
			Locale.setDefault(new Locale(lang));	
		}
	}

	private void saveD2DProperties(String configPath) {
		Properties d2dProperties = ServiceContext.getInstance().getD2dProperties();

		if (d2dProperties != null) {
			FileOutputStream fos = null;
			try {
				File file = new File(configPath + D2DInitor.D2D_PROPERTIES_NAME);
				if (file.exists()) {
					file.delete();
				}
				fos = new FileOutputStream(file);
				d2dProperties.store(fos, null);
			} catch (Exception e) {
				logger.error("Failed to load D2D properties", e);
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (Exception e) {
					}
				}
			}
		}
	}
}
