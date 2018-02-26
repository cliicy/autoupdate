package com.ca.arcflash.webservice.servlet;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.listener.service.event.FlashEvent;
import com.ca.arcflash.service.common.ActivityLogSyncher;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.webservice.scheduler.VSphereJobMonitorSyncher;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.RegConstants;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.VSphereBackupThrottleService;
import com.ca.arcflash.webservice.service.VSphereMergeService;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.VSphereJobQueue;

public class VSphereInitor implements IInitable {
	private static final Logger logger = Logger.getLogger(VSphereInitor.class);
	private static VSphereInitor instance = new VSphereInitor();
	public static VSphereInitor getInstance(){
		return instance;
	}
	@Override
	public void initialize() {
		logger.debug("init vSphere");
		configvSphereMaxJobNum();	
		String uuid = CommonService.getInstance().getNodeUUID();
		ActivityLogSyncher.getInstance().initialize(uuid, FlashEvent.Source.D2D,
				CommonNativeInstance.getICommonNative(), VSphereService.getInstance().getAllConfiguratedVMs());
		VSphereService.getInstance().configAllVMJobSchedule();
		VSphereService.getInstance().resumeJobAfterRestart();
		VSphereService.getInstance().startVSphereCatalogJobForAllVM();
		
		// deal with missed backup
		Thread dealWithMissedBackupThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				VSphereService.getInstance().dealWithMissedBackup();
			}

		}, "vsphere-deal-with-missed-backup");
		dealWithMissedBackupThread.start();		
		
		// fix bug 751269, resubmit the job queue jobs need to connect to RPS server. It may take a long time if RPS is not available		
		Thread resumeJobQueueThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				VSphereJobQueue.getInstance().readJobQueueFromFile();
			}

		}, "vsphere-resume-job-queue");
		resumeJobQueueThread.start();
		
		//Fix defect 175412
		//When lots of VM in proxy, check isMergeAvaliable take long time.
		Thread scheduleMergeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				VSphereMergeService.getInstance().fixMergeStatusAfterRestart();
				VSphereMergeService.getInstance().scheduleAllVMMergeJob();
			}
		}, "start-schedule-merge");
		scheduleMergeThread.start();
		VSphereBackupThrottleService.getInstance().startImmediateTrigger();	
		VSphereJobMonitorSyncher.startSync();
		logger.debug("init vSphere exit");
	}
	
	private void configvSphereMaxJobNum() {
		
		int maxJobNum = 10;
		try
		{
			maxJobNum = configVMwareMaxJobNum() + configHyperVMaxJobNum();
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		finally
		{
			ServiceContext.getInstance().setvSphereMaxJobNum(maxJobNum);
		}
		
		/*
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String max = registry.getValue(handle, RegConstants.REGISTRY_KEY_VSPHERE_MAX_JOB_NUM);
			int defaultmaxnum = 10;
			if (max == null || max.equals("")) {
				ServiceContext.getInstance().setvSphereMaxJobNum(defaultmaxnum);
			} else {
				int n = defaultmaxnum;
				try {
					n = Integer.parseInt(max);
					if (n <= 0)
						n = defaultmaxnum;
				} catch (Exception e) {
					logger.error(e);
				}
				ServiceContext.getInstance().setvSphereMaxJobNum(n);
			}

			logger.info("VSphere max job num " + max);
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
		
		*/		
	}
	
	private int configVMwareMaxJobNum()
	{
		int maxJobNum = 4; // default value
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try
		{
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String max = registry.getValue(handle, RegConstants.REGISTRY_KEY_VMWARE_MAX_JOB_NUM);
			
			if (max != null && !max.isEmpty())
			{
				int temp = Integer.parseInt(max);
				if (temp > 0)
				{
					maxJobNum = temp;
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Failed to parse " + RegConstants.REGISTRY_KEY_VMWARE_MAX_JOB_NUM, e);
		}
		finally
		{
			try
			{
				if (handle != 0)
				{
					registry.closeKey(handle);
				}
			}
			catch (Exception e)
			{
			}
			
			ServiceContext.getInstance().setVmwareMaxJobNum(maxJobNum);			
			logger.info("VMware max job num " + maxJobNum);
		}
		
		return maxJobNum;
	}
	
	private int configHyperVMaxJobNum()
	{
		int maxJobNum = 10; // default value
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try
		{
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String max = registry.getValue(handle, RegConstants.REGISTRY_KEY_HYPERV_MAX_JOB_NUM);
			
			if (max != null && !max.isEmpty())
			{
				int temp = Integer.parseInt(max);
				if (temp > 0)
				{
					maxJobNum = temp;
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Failed to parse " + RegConstants.REGISTRY_KEY_HYPERV_MAX_JOB_NUM, e);
		}
		finally
		{
			try
			{
				if (handle != 0)
				{
					registry.closeKey(handle);
				}
			}
			catch (Exception e)
			{
			}
			
			ServiceContext.getInstance().setHypervMaxJobNum(maxJobNum);			
			logger.info("Hyper-V max job num " + maxJobNum);
		}
		
		return maxJobNum;
	}

}
