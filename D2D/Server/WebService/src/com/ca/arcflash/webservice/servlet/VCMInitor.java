package com.ca.arcflash.webservice.servlet;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.d2dstatus.VCMStatusSyncer;
import com.ca.arcflash.webservice.edge.d2dstatus.statuscollectors.VCMStatusSyncerCollector;
import com.ca.arcflash.webservice.scheduler.RemoteVCMSessionMonitor;

public class VCMInitor implements IInitable {
	private static final Logger logger = Logger.getLogger(VCMInitor.class);
	private static VCMInitor instance = new VCMInitor();
	public static VCMInitor getInstance(){
		return instance;
	}
	@Override
	public void initialize() {
		logger.debug("init VCM");
		RemoteVCMSessionMonitor.getInstance().Initialize();
		VCMStatusSyncer.getInstance().initialize();
		VCMStatusSyncerCollector.getInstance().initialize();	
		logger.debug("init VCM exit");
	}

}
