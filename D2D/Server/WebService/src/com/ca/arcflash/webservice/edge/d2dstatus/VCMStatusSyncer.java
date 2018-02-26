package com.ca.arcflash.webservice.edge.d2dstatus;

import java.util.Set;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.d2dstatus.statuscollectors.VCMStatusCollector;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.replication.LRUCache;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public class VCMStatusSyncer {
	private static VCMStatusSyncer instance = new VCMStatusSyncer();
	private static final Logger logger = Logger.getLogger(VCMStatusSyncer.class);
	boolean initialized = false;
	private Thread syncThread;
	private volatile boolean stopped = false;

	public static VCMStatusSyncer getInstance() {
		return instance;
	}

	public void initialize() {
		logger.info("Sync VCM send thread initialized...");
		startSyncThread();
		initialized = true;
		logger.info("Sync VCM send thread initialized finish...");
	}

	private void startSyncThread() {
		logger.info("Sync VCM send thread start...");
		syncThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (!stopped) {
						doSync();
						Thread.sleep(HAService.getInstance().getSyncIntervalInMillisecond());
					}
				} catch (InterruptedException ie) {
					logger.info("Sync VCM send thread stop...");
				} catch (Throwable t) {
					logger.error("Sync VCM send thread failed...", t);
				}
			}
		});
		syncThread.setDaemon(true);
		CommonService.getInstance().getUtilTheadPool().submit(syncThread);
	}

	public void stopSync() {
		logger.info("Stop sync log thread");
		stopped = true;
		syncThread.interrupt();
		VCMStatusCollector.getInstance().clearLRUCache();
	}

	public void syncVCMStatus2Edge(final String afguid) {
		Thread syncThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					syncVCMStatus(afguid);
					Thread.sleep(HAService.getInstance().getSyncIntervalInMillisecond());
				} catch (Throwable t) {
					logger.error("Sync VCM send thread failed...", t);
				}
			}
		});
		CommonService.getInstance().getUtilTheadPool().submit(syncThread);
	}

	private void doSync() {
		Set<String> UUIDSet = CommonService.getInstance().getUUIDCollection();
		if (UUIDSet == null || UUIDSet.size() == 0)
			return;

		IEdgeD2DService proxy = null;
		try {
			proxy = getEdgeConnection(ApplicationType.VirtualConversionManager);
		} catch (Exception e) {
			logger.error("Fail to sync VCM status!" + e.getMessage());
		}
		if (proxy == null)
			return;

		for (String instanceUUID : UUIDSet) {
			D2DStatusInfo statusInfo = CommonService.getInstance().getVCMStatusInfo(instanceUUID);

			if (StringUtil.isEmptyOrNull(instanceUUID))
				continue;

			if (statusInfo == null || statusInfo == D2DStatusInfo.NullObject)
				continue;

			//logger.info("Sync VCM status, VM name: " + statusInfo.getVmName());
			try {
				proxy.syncD2DStatusInfo(instanceUUID, ApplicationType.VirtualConversionManager, statusInfo);
				logger.debug("Sync VCM status of node " + instanceUUID + ".");
			} catch (Exception e) {
				logger.error("Fail to sync VCM status of node " + instanceUUID + "." + e.getMessage());
			}
		}
	}

	private void syncVCMStatus(String uuid) {
		IEdgeD2DService proxy = null;
		try {
			proxy = getEdgeConnection(ApplicationType.VirtualConversionManager);
		} catch (Exception e) {
			logger.error("Fail to sync VCM status!" + e.getMessage());
		}
		if(proxy == null)
			return;

		D2DStatusInfo statusInfo = VCMStatusCollector.getInstance().getVCMStatusInfo(uuid);
		if (statusInfo == null || statusInfo == D2DStatusInfo.NullObject)
			return;

		CommonService.getInstance().setVCMStatusInfo(uuid, statusInfo);
		// D2DStatusInfo statusInfo = CommonService.getInstance().getVCMStatusInfo(uuid);

		try {
			proxy.syncD2DStatusInfo(uuid, ApplicationType.VirtualConversionManager, statusInfo);
		} catch (Exception e) {
			logger.error("Fail to sync VCM status of node " + uuid + "." + e.getMessage());
		}
	}

	private IEdgeD2DService getEdgeConnection(ApplicationType appType) throws EdgeServiceFault {		
		D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
		EdgeRegInfo edgeRegInfo = edgeReg.getEdgeRegInfo(appType);
		if(edgeRegInfo==null) {
			// logger.info(appType.name() + " - Edge configration file don't exist or reading failed! Stop Sync D2D Backup Status Job! ");
			return null;
		}
		IEdgeD2DService proxy = WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(),IEdgeD2DService.class);
		proxy.validateUserByUUID(edgeRegInfo.getEdgeUUID());
		return proxy;
	}
}
