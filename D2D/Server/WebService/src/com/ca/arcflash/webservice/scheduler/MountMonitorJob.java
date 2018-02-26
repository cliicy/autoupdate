package com.ca.arcflash.webservice.scheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.webservice.data.MountNode;
import com.ca.arcflash.webservice.jni.model.JMountPoint;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.DiskMonitorService;
import com.ca.arcflash.webservice.service.MountManagerConfiguration;
import com.ca.arcflash.webservice.service.ServiceContext;

public class MountMonitorJob implements Job {
	private static final Logger logger = Logger
	.getLogger(DiskMonitorService.class);
	private static boolean firstInvoke = true;
	private static final String mountConfigFile = CommonUtil.D2DInstallPath+"Configuration\\mount_configuration.xml";
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		logger.debug("MountManagerJob-------start");
		//zxh, only do VDDismountResBrsVols,because all check was done in c++.
		long iReVal = -1;
		try {
			iReVal = BrowserService.getInstance().getNativeFacade().VDDismountResBrsVols(false);	
		} catch (Exception e) {
			logger.error("VDDismountResBrsVols---failed--- " + e.getMessage());
		}
		logger.info("VDDismountResBrsVols---reVal is:" + iReVal);
	}

}
