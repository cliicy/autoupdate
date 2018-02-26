package com.ca.arcflash.webservice.edge.d2dstatus.statuscollectors;

import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.HeartBeatModel;
import com.ca.arcflash.ha.model.manager.HeartBeatModelManager;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;


public class VCMStatusSyncerCollector {

	private static VCMStatusSyncerCollector instance = new VCMStatusSyncerCollector();
	private static final Logger logger = Logger.getLogger(VCMStatusSyncerCollector.class);
	boolean initialized = false;
	private Thread syncThread;
	private volatile boolean stopped = false;
	
	public static VCMStatusSyncerCollector getInstance() {
		return instance;
	}

	public void initialize() {
		logger.info("Sync VCM collection thread initialized...");
		startSyncThread();
		initialized = true;
		logger.info("Sync VCM collection thread initialized finish...");
	}
	
	private void startSyncThread() {
		logger.info("Sync VCM collection thread start...");
		syncThread = new Thread(new Runnable(){
			
			@Override
			public void run() {
				try {
					while(!stopped){						
						doSync();
						Thread.sleep(HAService.getInstance().getSyncIntervalInMillisecond());
					}
				} catch(InterruptedException ie){
					logger.info("Sync VCM collection thread stop...");
				} catch(Throwable t){
					logger.error("Sync VCM collection thread failed...", t);
				}
			}			
		});
		syncThread.setDaemon(true);
		CommonService.getInstance().getUtilTheadPool().submit(syncThread);
	}
	
	public void stopSync(){
		logger.info("Stop sync VCM collection thread");
		stopped = true;
		syncThread.interrupt();
		VCMStatusCollector.getInstance().clearLRUCache();
	}

	private void doSync() {
		HeartBeatModel heartBeatModel = HeartBeatModelManager.getHeartBeatModel();
		List<ARCFlashNode> list = heartBeatModel.getMonitoredARCFlashNodes();
		for(ARCFlashNode node : list) {
			syncVCMStatus(node.getUuid());
		}
	}
	
	private void syncVCMStatus(String uuid) {
		try {
			D2DStatusInfo d2dStatusInfo = VCMStatusCollector.getInstance().getVCMStatusInfo(uuid);			
			
			if (d2dStatusInfo == D2DStatusInfo.NullObject)
				return ;
			
			CommonService.getInstance().updateVCMStatusInfo(uuid, d2dStatusInfo);
		}
		catch (Exception e)
		{
			logger.error( "Error getting VCM status info.", e );
		}
	}
}
