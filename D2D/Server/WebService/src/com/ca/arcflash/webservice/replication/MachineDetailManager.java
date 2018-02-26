package com.ca.arcflash.webservice.replication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.HashMap;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jobqueue.encrypt.Base64;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.webservice.common.VSphereLicenseCheck;
import com.ca.arcflash.webservice.data.MachineDetail;
import com.ca.arcflash.webservice.data.MachineType;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;

public class MachineDetailManager {
	private static Logger logger = Logger.getLogger(MachineDetailManager.class);
	public static final String detailPath = CommonUtil.D2DHAInstallPath+"Configuration\\AFJobQueue\\";
//	public static final String DetailPath = "d:\\temp\\AFJobQueue\\";
	public static final String fileName = "machineDetail.xml";
	private MachineDetail machDetail;
	private Object vSphereLock = new Object();
	//this map is used to cache the vSphere vm machine details.
	private HashMap<String, MachineDetail> vSphereVMDetailsMap = new HashMap<String, MachineDetail>();
	private static final MachineDetailManager manager = new MachineDetailManager();
	
	private MachineDetailManager() {
		loadEsxVMDetail();
	}
	
	public static MachineDetailManager getInstance() {
		return manager;
	}
	
	private void loadEsxVMDetail() {
		machDetail = new MachineDetail();
		machDetail.setHostName(ServiceContext.getInstance().getLocalMachineName());
		if(!StringUtil.isEmptyOrNull(CommonUtil.hyperVHostNameIfVM())){
			machDetail.setMachineType(MachineType.HYPERV_VM);
			machDetail.setHypervisorHostName(CommonUtil.hyperVHostNameIfVM());
		}
		else { 
			MachineDetail detail = readMachineDetail(null);
			if(detail != null)
				machDetail = detail;
		}
		
		loadVSphereVMDetails();
	}

	private MachineDetail readMachineDetail(String detailFileName) {
		String qualifiedFileName = StringUtil.isEmptyOrNull(detailFileName) ? fileName : detailFileName;
		File file = new File(detailPath + qualifiedFileName);
		MachineDetail detail = null;
		if(file.exists()) {
			try {
				FileInputStream inputStream = new FileInputStream(file);
				detail = (MachineDetail)JAXB.unmarshal(inputStream, MachineDetail.class);
			} catch (Exception e) {
				logger.error("fials to read machine, file: " + qualifiedFileName + ". details:" + e.getMessage(), e);
			}
			if(detail != null) {
				detail.setHypervisorPassword(Base64.decode(detail.getHypervisorPassword()));
			}
//				if(detail == null) {
//					machDetail.setMachineType(MachineType.ESX_VM);
//				}
		}
		
		return detail;
	}
	
	private void loadVSphereVMDetails() {
		logger.info("loadVSphereVMDetails - bgein");
		try {
			File dir = new File(detailPath);
						
			if(dir.exists() && dir.isDirectory()) {
				String[] files = dir.list(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						if(name != null 
						  && name.length() > fileName.length() 
						  && name.toLowerCase().endsWith(fileName.toLowerCase())
						  && new File(dir, name).isFile()) {
								return true;
						}
						return false;
					}
					
				});
				
				for (String file : files) {
					MachineDetail detail = readMachineDetail(file);
					if(detail != null) {
						int index = file.indexOf(fileName);
						if(index > 0) {
							String uuid = file.substring(0, index);
							logger.info("Read machine detail for uuid:" + uuid);
							synchronized (vSphereLock) {
								vSphereVMDetailsMap.put(uuid, detail);
							}
						}
					}
						
				}
			}
		}
		catch(Exception e) {
			logger.warn("Fails to load vSphere vm machine detail", e);
		}
		
	}
	
	public void removeVSphereVMDetail(String afGuid) {
		try {
			synchronized (vSphereLock) {
				vSphereVMDetailsMap.remove(afGuid);
			}
			
			String qualifiedFileName = StringUtil.isEmptyOrNull(afGuid) ? fileName : afGuid + fileName;
			File vmFile = new File(detailPath + qualifiedFileName);
			if(vmFile.exists())
				vmFile.delete();
			
		}catch(Exception e){
			logger.warn("Fails to remove vSphere vm machine detail", e);
		}
	}

	/**
	 * Returns the {@link MachineDetails} of the current machine
	 * @return
	 */
	public synchronized MachineDetail getMachineDetail() {
		return machDetail;
	}
	
	public MachineDetail getMachineDetail(String afGuid) {
		if(StringUtil.isEmptyOrNull(afGuid) || HAService.getInstance().retrieveCurrentNodeID().equals(afGuid))
			return getMachineDetail(); 
			
		synchronized (vSphereLock) {
			if(vSphereVMDetailsMap.containsKey(afGuid))
				return vSphereVMDetailsMap.get(afGuid);
		}
			
		MachineDetail detail = null;
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(afGuid);
		try {
			VMBackupConfiguration vmBackup = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if(vmBackup != null && vmBackup.getBackupVM() != null) {
				detail = new MachineDetail();
				detail.setMachineType(MachineType.VSPHERE_ESX_VM);
				
				String esxServerName = vmBackup.getBackupVM().getEsxServerName();
				String protocol = vmBackup.getBackupVM().getProtocol();
				int port = vmBackup.getBackupVM().getPort();
				String esxUsername = vmBackup.getBackupVM().getEsxUsername();
				String esxPassword = vmBackup.getBackupVM().getEsxPassword();
				detail.setHypervisorHostName(esxServerName);
				detail.setHypervisorProtocol(protocol);
				detail.setHypervisorPort(port);
				detail.setHypervisorUserName(esxUsername);
				detail.setHypervisorPassword(esxPassword);
				
				FailoverJobScript failoverScript = HAService.getInstance().getFailoverJobScript(afGuid);
				if(failoverScript != null && !StringUtil.isEmptyOrNull(failoverScript.getProductionServerName()))
					detail.setHostName(failoverScript.getProductionServerName());
				
				String esxHostName = VSphereLicenseCheck.getNormalizedESXHostName(vmBackup);
				
				if(!StringUtil.isEmptyOrNull(esxHostName)) {
					detail.setESXHostName(esxHostName);
				}
			}
		} catch (ServiceException e) {
			logger.error("Fail to get ESX detail for vSphere VM replication. MachineDetail:" + detail + ". Error:" + e.getMessage(), e);
		}
		
		if(detail != null) {
			synchronized (vSphereLock) {
				vSphereVMDetailsMap.put(afGuid, detail);
			}
			try {
				setMachineDetail(detail, afGuid);
			}catch(Exception e) {
				logger.warn("Fails to save vSphere vm machine detail", e);
			}
		}
		return detail;
	}
	
	public synchronized void setMachineDetail(MachineDetail detail, String afGuid) throws Exception{
		try {
			String qualifiedFileName = StringUtil.isEmptyOrNull(afGuid) ? fileName : afGuid + fileName;
			if(detail == null) {
				File file = new File(detailPath + qualifiedFileName);
				if(file.exists()) 
					file.delete();
				
				return;
			}
			
			File dir = new File(detailPath);
			if(!dir.exists()) 
				dir.mkdirs();
			File file = new File(detailPath + qualifiedFileName);
			FileOutputStream output = new FileOutputStream(file);
			
			String originalPassword = detail.getHypervisorPassword();
			detail.setHypervisorPassword(Base64.encode(originalPassword));
			try {
				JAXB.marshal(detail, output);
			}
			finally {
				detail.setHypervisorPassword(originalPassword);
			}
			
			// update cache in vSphereVMDetailsMap
			synchronized (vSphereLock) {
				vSphereVMDetailsMap.put(afGuid, detail);
			}
			
		}
		catch(Exception e) {
			logger.error("Fails to save the machine detail:" + e.getMessage(), e);
			throw e;
		}
	
	}

	public synchronized void setMachineDetail(MachineDetail detail) throws Exception{
		machDetail = detail;
		setMachineDetail(detail, null);
	}
	
//	public static void main(String[] args) throws Exception {
//		System.out.println(manager.getMachineDetail() + ";password:" + manager.getMachineDetail().getHypervisorPassword());
//		
//		MachineDetail detail = new MachineDetail();
//		detail.setMachineType(MachineType.ESX_VM);
//		detail.setESXHostName("esx hostanme");
//		detail.setHostName("hostname");
//		detail.setHypervisorHostName("hypervisorHostName");
//		detail.setHypervisorUserName("hypervisorUserName");
//		detail.setHypervisorPassword("HypervisorPassword");
//		detail.setHypervisorPort(10002);
//		detail.setHypervisorProtocol("http:");
//		manager.setMachineDetail(detail );
//		System.out.println(manager.getMachineDetail());
//	}
	
}
