package com.ca.arcflash.replication;

import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.ARCFlashNodesSummary;
import com.ca.arcflash.ha.model.HeartBeatModel;
import com.ca.arcflash.ha.model.manager.HeartBeatModelManager;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.ha.webservice.jni.HyperVException;
import com.ca.ha.webservice.jni.HyperVJNI;



public class ReplicationService {
	private static final Logger log = Logger.getLogger(ReplicationService.class);
	private static final int HATransServerPort = HAService.getInstance().getHATransPort();
	private static boolean HATransServerStarted = false;
	public static void startHATransServerForMonitor() {
		
		//if this machine is a VCM monitor
		HeartBeatModel heartBeatModel = HeartBeatModelManager.getHeartBeatModel();
		if(heartBeatModel != null && heartBeatModel.getMonitoredARCFlashNodes().size() > 0) {
			startHATransServerDirectly();
		}
	}

	public static synchronized void stop() {
		try {
			if(HATransServerStarted) {
				HyperVJNI.stopTransServer();
				HATransServerStarted = false;
			}
		} catch (HyperVException e) {
			log.error("Fails to stop HATransServer.exe process", e);
		}
	}

	public static synchronized void startHATransServerDirectly() {
		
		if(!HATransServerStarted) {
			HATransServerStarted = true;
			Thread tProcess = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						HyperVJNI.startTransServer(HATransServerPort);
					} catch (HyperVException e) {
						log.error("Fails to start HATransServer.exe process", e);
					}
				}

			});
			
			tProcess.start();
		}
	}
}
