package com.ca.arcflash.ha.model.manager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.HeartBeatModel;
import com.ca.arcflash.ha.model.VirtualMachineInfo;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.service.common.ActivityLogSyncher;
import com.ca.arcflash.webservice.edge.d2dstatus.statuscollectors.VCMStatusCollector;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.replication.ManualConversionUtility;
import com.ca.arcflash.webservice.service.CommonService;


public class HeartBeatModelManager {

//	private final static String DES = "DES";
//	private static final String KEY_ENCRYPT = "CaWorld@))*";
	private static String MODEL_XML = "";

	private static HeartBeatModel model = null;
	private static Logger log = Logger.getLogger(HeartBeatModelManager.class);

	public synchronized static void initizlize(String file) {
		MODEL_XML = file;
		try{
//			SecureRandom secureRandom = new SecureRandom();
//			DESKeySpec desKeySpe = new DESKeySpec(KEY_ENCRYPT
//					.getBytes());
//			SecretKeyFactory keyFactory = SecretKeyFactory
//					.getInstance(DES);
//			SecretKey securekey = keyFactory.generateSecret(desKeySpe);
//			Cipher cipher = Cipher.getInstance(DES);
//			cipher.init(Cipher.DECRYPT_MODE, securekey, secureRandom);
//			BufferedInputStream bis = new BufferedInputStream(
//					new CipherInputStream(
//							new FileInputStream(MODEL_XML), cipher));
			
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(MODEL_XML));
			model = JAXB.unmarshal(bis, HeartBeatModel.class);
	
	//		for (ARCFlashNode flash : model.getMonitoredARCFlashNodes()) {
	//			flash.setLastUpdate(Calendar.getInstance().getTime());
	//		}
	
			List<ARCFlashNode> nodes = model.getMonitoredARCFlashNodes();
			for (ARCFlashNode arcFlashNode : nodes) {
				ActivityLogSyncher.getInstance().addVM(arcFlashNode.getUuid());
			}

			log.debug("Initialize Heart Beat Model Success.");
		} catch (Exception e) {
			log.info("Initialize Heart Beat Model Failed.");
			model = new HeartBeatModel();
		}
	}
	public synchronized static void setHeartBeatState(String guid,int state){
		if(model!=null) {
			ARCFlashNode arcFlashNode = model.getMonitoredARCFlashNodesMap().get(guid);
			if(arcFlashNode!=null)
				arcFlashNode.setState(state);
		}
	}
	
	public synchronized static void updateHeartBeat(String guid, double perc, int toRep, boolean isLastUpdateFromVM){
		if(model!=null) {
			ARCFlashNode arcFlashNode = model.getMonitoredARCFlashNodesMap().get(guid);
			if(arcFlashNode!=null){
				arcFlashNode.setLastUpdate(Calendar.getInstance().getTime().getTime());
				arcFlashNode.setRepPerc(perc);
				arcFlashNode.setRepToRep(toRep);
				arcFlashNode.setLastUpdateFromVM(isLastUpdateFromVM);
			}
		}
	}
	public synchronized static long  getLatestHeartBeat(String guid){
		if(model!=null) {
			ARCFlashNode arcFlashNode = model.getMonitoredARCFlashNodesMap().get(guid);
			if(arcFlashNode!=null)
				return arcFlashNode.getLastUpdate();
		}
		return -1;
	}
	public synchronized static void updateVMInfo(String guid, VirtualMachineInfo vmInfo){
		if(model!=null) {
			ARCFlashNode arcFlashNode = model.getMonitoredARCFlashNodesMap().get(guid);
			boolean changed = false;
			VirtualMachineInfo info = getVMInfo(guid);
			if(info == null ){
				if(vmInfo != null)
					changed = true;
			}
			else if(!info.completeEquals(vmInfo) )
				changed = true;
			 
			if(changed && arcFlashNode != null){
				arcFlashNode.setVmInfo(vmInfo);
				store();
			}
			
		}
	}
	public synchronized static VirtualMachineInfo getVMInfo(String afGuid) {
		if(model!=null) {
			ARCFlashNode arcFlashNode = model.getMonitoredARCFlashNodesMap().get(afGuid);
			if(arcFlashNode!=null)
				return arcFlashNode.getVmInfo();
		}
		return null;
	}
	
	public synchronized static void registerHeartBeat(FailoverJobScript script, int registered,int newVmType){
		String guid = script.getAFGuid();
		String protocol = script.getProductionServerProtocol();
		String hostname = script.getProductionServerName(); 
		String hostport = script.getProductionServerPort();
		int heartBeatTimeoutInsec = script.getHeartBeatFailoverTimeoutInSecond();
		
		if(model!=null) {
			List<ARCFlashNode> monitoredARCFlashNodes = model.getMonitoredARCFlashNodes();
			ARCFlashNode temp = new ARCFlashNode();
			temp.setUuid(guid);
			temp.setHostname(hostname);
			try {
				InetAddress[] ipAddresses = InetAddress.getAllByName(hostname);
				for (InetAddress inetAddress : ipAddresses) {
					temp.getHostip().add(inetAddress.getHostAddress());
				}
			} catch (UnknownHostException e) {
			} catch (SecurityException e) {
			}
			
			if(script.isVSphereBackup()) {
				temp.setVSphereManagedVM(script.isVSphereBackup());
				temp.setVSphereproxyServer(script.getVSphereproxyServer());
			}

			temp.setHostport(hostport);
			temp.setHostProtocol(protocol);
			temp.setHeartBeatFailoverTimeoutInSecond(heartBeatTimeoutInsec);
			temp.setState(registered);
			int indexOf = monitoredARCFlashNodes.indexOf(temp);
			while(indexOf!=-1){
				ARCFlashNode f = monitoredARCFlashNodes.remove(indexOf);
				VirtualMachineInfo vmInfo = f.getVmInfo();
				temp.setVmInfo(vmInfo);
				indexOf = monitoredARCFlashNodes.indexOf(temp);
			}
			
			temp.setRemoteNode(ManualConversionUtility.isVSBWithoutHASupport(script));
			monitoredARCFlashNodes.add(temp);
			store();
			VirtualMachineInfo vmInfo = temp.getVmInfo();
			log.info(String.format("Register heartbeat for Host %s, VM %s.",
					hostname, vmInfo == null ? null : vmInfo.getAfguid()));

			try
			{
				D2DStatusInfo d2dStatusInfo = VCMStatusCollector.getInstance().getVCMStatusInfo(guid);	
				CommonService.getInstance().addVCMStatusInfo(guid,d2dStatusInfo);
			}
			catch(Exception e)
			{
				log.warn("Can't get the VCM " + guid + " status info, insert null.");
				CommonService.getInstance().addVCMStatusInfo(guid,null);
			}
			
			ActivityLogSyncher.getInstance().addVM(guid);
		}
	}
	public synchronized static void deRegisterHeartBeat(String guid){
		
		if(model!=null) {
			List<ARCFlashNode> monitoredARCFlashNodes = model.getMonitoredARCFlashNodes();
			ARCFlashNode temp = new ARCFlashNode();
			temp.setUuid(guid);
			int indexOf = monitoredARCFlashNodes.indexOf(temp);
			boolean deleted = false;
			while(indexOf != -1) {
				monitoredARCFlashNodes.remove(indexOf);
				log.info(String.format("Unregister hearbeat for Host %s. ", temp.getHostname()));
				deleted = true;
				indexOf = monitoredARCFlashNodes.indexOf(temp);
			}
			if(deleted) store();
			
			CommonService.getInstance().removeVCMStatusInfo(guid);
			ActivityLogSyncher.getInstance().removeVM(guid);
		}
		
	}
	public synchronized static HeartBeatModel getHeartBeatModel() {
		if (model != null) {
			try {
				return (HeartBeatModel)model.clone();
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
	public synchronized static ARCFlashNode getHeartbeatNode(String guid) {
		return model.getMonitoredARCFlashNodesMap().get(guid);
	}

	public synchronized static void store() {
		if(model==null) return;
		BufferedOutputStream bos = null;
			try {
//				SecureRandom secureRandom = new SecureRandom();
//				DESKeySpec desKeySpe = new DESKeySpec(KEY_ENCRYPT.getBytes());
//				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
//				SecretKey securekey = keyFactory.generateSecret(desKeySpe);
//				Cipher cipher = Cipher.getInstance(DES);
//				cipher.init(Cipher.ENCRYPT_MODE, securekey, secureRandom);
//				bos = new BufferedOutputStream(
//						new CipherOutputStream(new FileOutputStream(MODEL_XML),
//								cipher));
				
				bos = new BufferedOutputStream(new FileOutputStream(MODEL_XML));
				JAXB.marshal(model, bos);
				
				log.debug("Store Heart Beat Model Success.");
			} catch (Exception e) {
				log.debug("Store Heart Beat Model Failed.");
			} finally{
				try{if(bos!=null) bos.close();}catch(Exception e){}
			}
		
	}
	
	public static synchronized ARCFlashNode getHeartBeatModel(String vmuuid,String vmname){
		
		log.info("vmuuid: " + vmuuid);
		log.info("vmname: " + vmname);
		ARCFlashNode retNode = null;
		HeartBeatModel model = getHeartBeatModel();
		List<ARCFlashNode> nodes = model.getMonitoredARCFlashNodes();
		for (ARCFlashNode arcFlashNode : nodes) {
			VirtualMachineInfo vmInfo = arcFlashNode.getVmInfo();
			log.info(String.format("Retrieve Host %s, VM %s in heart beat. ",
					arcFlashNode.getHostname(),
					vmInfo == null ? null : vmInfo.getAfguid()));
			if(vmInfo == null || StringUtil.isEmptyOrNull(vmInfo.getVmGUID()))
				continue;
			if(vmInfo.getVmGUID().equalsIgnoreCase(vmuuid)){
				log.info("Find ARCFlashNode.");
				log.info(vmuuid);
				retNode = arcFlashNode;
				break;
			}
		}
		return retNode;
	}

}
