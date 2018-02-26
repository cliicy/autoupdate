package com.ca.arcflash.webservice.edge.d2dresync;


import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.datasync.ActiveLogSyncer;
import com.ca.arcflash.webservice.edge.datasync.ArchiveSyncer;
import com.ca.arcflash.webservice.edge.datasync.BackupInfoSyncer;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.edge.datasync.VCMSyncer;
import com.ca.arcflash.webservice.edge.datasync.VSPhereSyncer;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.service.BackupService;

public class D2DReSyncServiceImpl implements ID2DReSyncService {
	private static final Logger logger = Logger.getLogger(D2DReSyncServiceImpl.class);
	NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
	
	public D2DReSyncServiceImpl() {
	}

	@Override
	public int D2DResync2Edge(String edgeHostName) {
		// TODO Auto-generated method stub
		logger.debug("D2DSync(full) - D2DResync2Edge() Enter ...");
		EdgeDataSynchronization dataSync = new EdgeDataSynchronization();
		
		int ret = dataSync.doFullSynchronization();
		if(ret != 0){
			logger.debug("D2DSync(full) - failed with result = " + ret + "!");
			return ret;
		}
		
		logger.debug("D2DSync(full) - finished!");
		return 0;
	}
}
