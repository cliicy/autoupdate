package com.ca.arcflash.webservice.edge.d2dstatus;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.webservice.data.vsphere.VMItem;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public class SyncD2DStatusService {
	
	private static final Logger logger = Logger.getLogger(SyncD2DStatusService.class);
	private static SyncD2DStatusService syncD2DStatusService = null;
	private static final int THREE_MINUTE = 3*60*1000;
	
	public static synchronized SyncD2DStatusService getInstance() {
		if (syncD2DStatusService == null)
			syncD2DStatusService = new SyncD2DStatusService();
		return syncD2DStatusService;
	}
	
	public void syncD2DStatus() throws EdgeServiceFault {
		// Cancel sync to CPM temporarily
//		syncD2DStatus(CommonService.getInstance().getUUID());
	}
	
	private void syncD2DStatus(String uuid) throws EdgeServiceFault {
		logger.debug("syncD2DStatus starting uuid = " + uuid);
		IEdgeD2DService proxy = getEdgeConnection(ApplicationType.CentralManagement);
		if(proxy==null)
			return;
		D2DStatusInfo d2DStatusInfo = D2DStatusServiceImpl.getInstance().getD2DStatusInfo();
		proxy.syncD2DStatusInfo(uuid, ApplicationType.CentralManagement, d2DStatusInfo);
		logger.debug("syncD2DStatus ending uuid = " + uuid);
		
	}
	
	private void syncVSphereStatus(String vmInstanceUuid) throws EdgeServiceFault {
		logger.debug("syncVSphereStatus starting vmInstanceUuid = " + vmInstanceUuid);
		IEdgeD2DService proxy = getEdgeConnection(ApplicationType.vShpereManager);
		if(proxy==null)
			return;
		D2DStatusInfo d2DStatusInfo = D2DStatusServiceImpl.getInstance().getVSphereVMStatusInfo(vmInstanceUuid);
		proxy.syncD2DStatusInfo(vmInstanceUuid, ApplicationType.vShpereManager, d2DStatusInfo);
		logger.debug("syncVSphereStatus ending vmInstanceUuid = " + vmInstanceUuid);
	}
	
	public void syncVSphereStatusAll() throws EdgeServiceFault, ServiceException {
		// Cancel sync to CPM temporarily
//		logger.debug("syncVSphereStatusAll starting.");
//		VMItem[] vmItems = VSphereService.getInstance().getConfiguredVM();
//		if (vmItems == null)
//			return;
//		IEdgeD2DService proxy = getEdgeConnection(ApplicationType.vShpereManager);
//		if(proxy==null)
//			return;
//		List<D2DStatusInfo> infoList = new ArrayList<D2DStatusInfo>();
//		D2DStatusInfo d2DStatusInfo = null;
//		for (VMItem vm : vmItems) {
//			d2DStatusInfo = D2DStatusServiceImpl.getInstance().getVSphereVMStatusInfo(vm.getVmInstanceUUID());
//			if(d2DStatusInfo != null) {				
//				d2DStatusInfo.setUuid(vm.getVmInstanceUUID());
//				infoList.add(d2DStatusInfo);
//			}
//		}
//		proxy.syncVSphereStatusAll(infoList);
//		logger.debug("syncVSphereStatusAll ending ");
		
	}
	
	private void syncVCMStatus(String uuid) throws EdgeServiceFault {
		logger.debug("syncVCMStatus starting uuid = " + uuid);
		IEdgeD2DService proxy = getEdgeConnection(ApplicationType.VirtualConversionManager);
		if(proxy==null)
			return;
		D2DStatusInfo d2DStatusInfo = D2DStatusServiceImpl.getInstance().getVCMStatusInfo(uuid);
		proxy.syncD2DStatusInfo(uuid, ApplicationType.VirtualConversionManager, d2DStatusInfo);
		logger.debug("syncVCMStatus ending uuid = " + uuid);
	}
	
	public void syncVCMStatusAll() throws EdgeServiceFault, ServiceException {
		logger.debug("syncVCMStatusAll starting");
		IEdgeD2DService proxy = getEdgeConnection(ApplicationType.VirtualConversionManager);
		if(proxy==null)
			return;
		List<D2DStatusInfo> d2DStatusInfoList = new ArrayList<D2DStatusInfo>();
		VMItem[] VMs = VSphereService.getInstance().getConfiguredVM();
		VMItem[] remoteVMs = VSphereService.getInstance().getConfiguredVMByGenerateType(1);
		if (VMs == null || VMs.length == 0) {
			VMs = remoteVMs;
		} else if (remoteVMs != null && remoteVMs.length > 0) {
			int index = 0;
			VMItem[] newVMArray = new VMItem[VMs.length + remoteVMs.length];
			for (VMItem vm : VMs) {
				newVMArray[index++] = vm;
			}
			for (VMItem vm : remoteVMs) {
				newVMArray[index++] = vm;
			}
			VMs = newVMArray;
		}
		ReplicationJobScript tmp = null;
		D2DStatusInfo d2DStatusInfo = null;
		
		if (VMs != null) {
			for(VMItem vm : VMs){
				tmp = HAService.getInstance().getReplicationJobScript(vm.getVmInstanceUUID());
				if(tmp != null){
					d2DStatusInfo = D2DStatusServiceImpl.getInstance().getVCMStatusInfo(vm.getVmInstanceUUID());
					if(d2DStatusInfo != null) {					
						d2DStatusInfo.setUuid(vm.getVmInstanceUUID());
						d2DStatusInfoList.add(d2DStatusInfo);
					}
				}
			}
		}
		
		String uuid = CommonService.getInstance().getNodeUUID();
		tmp = HAService.getInstance().getReplicationJobScript(uuid);
		if(tmp != null){
			d2DStatusInfo = D2DStatusServiceImpl.getInstance().getVCMStatusInfo(uuid);
			if(d2DStatusInfo != null) {				
				d2DStatusInfo.setUuid(uuid);
				d2DStatusInfoList.add(d2DStatusInfo);
			}
		}
		
		// D2D nodes that backup to this RPS server
		List<String> d2dGuids = HAService.getInstance().getRPSReplicationJobGUIDs();
		if(d2dGuids != null){
			for (String d2dGuid : d2dGuids) {
				tmp = HAService.getInstance().getReplicationJobScript(d2dGuid);
				if(tmp != null){
					d2DStatusInfo = D2DStatusServiceImpl.getInstance().getVCMStatusInfo(d2dGuid);
					if(d2DStatusInfo != null) {				
						d2DStatusInfo.setUuid(d2dGuid);
						d2DStatusInfoList.add(d2DStatusInfo);
					}
				}
			}
		}
		
		proxy.syncVCMStatusAll(d2DStatusInfoList);
		logger.debug("syncVCMStatusAll ending");
	}
	
	private IEdgeD2DService getEdgeConnection(ApplicationType appType) throws EdgeServiceFault {		
		D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
		EdgeRegInfo edgeRegInfo = edgeReg.getEdgeRegInfo(appType);
		if(edgeRegInfo==null) {
			logger.info(appType.name() + " - Edge configration file don't exist or reading failed! Stop Sync D2D Backup Status Job! ");
			return null;
		}
		IEdgeD2DService proxy = WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(),IEdgeD2DService.class);
		proxy.validateUserByUUID(edgeRegInfo.getEdgeUUID());
		return proxy;
	}
	
	public void syncD2DStatus2Edge(final String uuid) {
//		Thread syncThread = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//
//				for(int i = 0; i < 3; i++) {
//					try {
//						syncD2DStatus(uuid);
//						break;
//					} catch (EdgeServiceFault e) {
//						logger.error("Sync D2D status failed!", e);
//						try {
//							Thread.sleep(THREE_MINUTE);
//						} catch (InterruptedException e1) {
//							logger.error("Sync D2D status failed!", e);
//						}
//					}
//				}
//				
//			}
//			
//		});
//		syncThread.start();
		
	}
	
	public void syncVSphereStatus2Edge(final String vmInstanceUuid) {
		// Cancel sync to CPM temporarily
//		Thread syncThread = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				for(int i = 0; i < 3; i++) {
//					try {
//						syncVSphereStatus(vmInstanceUuid);
//						break;
//					} catch (EdgeServiceFault e) {
//						logger.error("Sync D2D vSphere status failed!", e);
//						try {
//							Thread.sleep(THREE_MINUTE);
//						} catch (InterruptedException e1) {
//							logger.error("Sync D2D vSphere status failed!", e);
//						}
//					}
//				}
//			}
//			
//		});
//		syncThread.start();
	}
	
	public void syncVSphereStatus2Edge() {
		// Cancel sync to CPM temporarily
//		Thread syncThread = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					syncVSphereStatusAll();
//				} catch (Exception e) {
//					logger.error("Sync vSphere status failed!", e);
//				}
//			}
//			
//		});
//		syncThread.start();
	}
		
	public void syncVCMStatus2Edge(final String afguid) {
		Thread syncThread = new Thread(new Runnable() {

			@Override
			public void run() {
				for(int i = 0; i < 3; i++) {					
					try {
						syncVCMStatus(afguid);
						break;
					} catch (EdgeServiceFault e) {
						logger.error("Sync VCM status failed!", e);
						try {
							Thread.sleep(THREE_MINUTE);
						} catch (InterruptedException e1) {
							logger.error("Sync VCM status failed!", e1);
						}
					}
				}
			}
			
		});
		syncThread.start();
	}
	
	public void syncAllBackupStatus2Edge() {
		Thread syncThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					logger.info("Try to sleep 5 min, waiting for Edge to manage this D2D!");
					Thread.sleep(5*60*1000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
				try {
					syncD2DStatus(CommonService.getInstance().getNodeUUID());
				} catch (Exception e) {
					logger.error("Sync D2D status failed!", e);
				} 
				try {
					syncVSphereStatusAll();
				} catch (Exception e) {
					logger.error("Sync vSphere status failed!", e);
				} 
//				try {
//					syncVCMStatusAll();
//				} catch (Exception e) {
//					logger.error("Sync VCM status failed!", e);
//				} 
			}
			
		});
		syncThread.start();
	}
}
