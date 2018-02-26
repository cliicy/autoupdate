package com.ca.arcflash.failover;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.failover.model.ADRConfigureFactory;
import com.ca.arcflash.failover.model.ADRConfigureUtil;
import com.ca.arcflash.failover.model.BackupInfo;
import com.ca.arcflash.failover.model.BackupInfoFactory;
import com.ca.arcflash.failover.model.DiskExtent;
import com.ca.arcflash.ha.event.VCMEvent;
import com.ca.arcflash.ha.event.VCMEventManager;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.ha.model.internal.HAException;
import com.ca.arcflash.ha.model.manager.VMwareSnapshotModelManager;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanager.VirtualNetworkInfo;
import com.ca.arcflash.ha.vmwaremanager.powerState;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualNICCard;
import com.ca.arcflash.jobscript.alert.AlertType;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.IPSetting;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.jobscript.failover.VMwareESX;
import com.ca.arcflash.jobscript.failover.VMwareVirtualCenter;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.data.VMwareConnParams;
import com.ca.arcflash.webservice.data.VMwareVolumeInfo;

public abstract class VMwareBaseFailoverCommand extends PreFailoverCommand {

	private static final long serialVersionUID = 1797905242132919144L;
	private static final Logger log = Logger.getLogger(VMwareBaseFailoverCommand.class);
	private CAVirtualInfrastructureManager vmwareOBJ;
	private static final int ADD_NIC_TIME = 3 * 1000;
	private static boolean VDDKServiceInstalled = false;
	
	@Override
	public int executeFailover(String jobID,FailoverJobScript jobScript,String sessionGuid, boolean isAutoFailover) throws Exception {
		int result = 0;
		try {
			Date startTime = new Date();
			
			if(isAutoFailover) {
				log.debug("Miss heart beat of source machine");
				sessionGuid = null;
			}
			
			long foJobID = BackupService.getInstance().getNativeFacade().getJobID();
			result = doRealFailover(jobScript, sessionGuid, isAutoFailover, foJobID);
			
			//send the alert email
			if(result == 0) {
				
				String esxHost = "ESX";
				Virtualization virtualization = jobScript.getFailoverMechanism().get(0);
				if(virtualization!=null) {
					if(virtualization instanceof VMwareESX) {
						VMwareESX vMwareESX = (VMwareESX)virtualization;
						esxHost = String.format("ESX[%s]", vMwareESX.getHostName());
					}
					else if(virtualization instanceof VMwareVirtualCenter) {
						VMwareVirtualCenter vCenter = (VMwareVirtualCenter)virtualization;
						esxHost = String.format("ESX[%s]", vCenter.getEsxName());
					}
				}
				AlertType alertType= AlertType.Unknown;
				if(isAutoFailover) {
					alertType = AlertType.AutoFaiover;
				}
				else {
					alertType = AlertType.MaualFaiover;
				}
				String vmName = jobScript.getFailoverMechanism().get(0).getVirtualMachineDisplayName();
				String productHostName = jobScript.getProductionServerName();
				HAService.getInstance().sendAlertMailWithParameters(jobScript.getAFGuid(), foJobID,alertType, startTime, vmName, productHostName, esxHost);
			
			}
			
			//end
			
			return result;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	private int doRealFailover(FailoverJobScript jobScript,String sessionGuid, boolean isAutoFailover, long jobID) throws Exception {
		
		int retValue = 1;
		List<Virtualization> virMachanism = jobScript.getFailoverMechanism();
		if(virMachanism == null || virMachanism.size() == 0){
			log.error("Virtualization channel is null!!!!!!!");
			return retValue;
		}
		
		Virtualization virtualization = virMachanism.get(0);
		
		HAService.getInstance().activityLogForStartFailover(jobScript, jobID, isAutoFailover);

		VMWareInfo vmConf = getVMWareInfo(virtualization,jobScript.getAFGuid());
		log.info("vmname: " + vmConf.vmName);
		log.info("vminstanceuuid: " + vmConf.vmuuid);

		String msg = "";
		try {
			try {
				
				log.debug("Begin to get Connection!!!");
				vmwareOBJ =  CAVMwareInfrastructureManagerFactory
				.getCAVMwareVirtualInfrastructureManager(vmConf.hostname,vmConf.username, 
						vmConf.password, vmConf.protocol, true, vmConf.port);
				
				if(vmwareOBJ == null){
					log.error("Failed to connect to vmware host!!!!");
					log.error(vmConf.hostname+ " : " +vmConf.username);
					return retValue;
				}
				else
				{
					vmConf.setExParams(vmwareOBJ);
				}
				
				if(virtualization instanceof VMwareESX){
					ESXNode node = vmwareOBJ.getESXNodeList().get(0);
					vmConf.esxHost = node.getEsxName();
				}
				
				powerState vmstates = vmwareOBJ.getVMPowerstate(vmConf.vmName, vmConf.vmuuid);
				if(vmstates != powerState.poweredOff){
					vmwareOBJ.powerOffVM(vmConf.vmName, vmConf.vmuuid);

					msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_VM_SHUTDOWN,vmConf.vmName);
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				}	
				
			}catch (Exception e) {
				
				log.error(e.getMessage());
				
				msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_ERROR_ESX_FAILOVER_FAIL,vmConf.hostname,vmConf.vmName);
				msg += FailoverMessage.getResource(FailoverMessage.FAILOVER_Process_ESX_RETURN_MESSAGE) + e.getMessage();
				
				HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.FailoverFailure, msg);
				
				return retValue;
			}
			
			//if manual failover sessionGuid is not null or empty string
			//if auto failover sessionGuid is null or empty string
			VMSnapshotsInfo lastSnapshot = getSpecificSnapshot(vmConf,sessionGuid,jobScript.getAFGuid());
			if(lastSnapshot == null){
				log.error("Failed to get the session snapshot");
				return retValue;
			}
	
			try {
				
				msg = FailoverMessage.getResource(
						FailoverMessage.FAILOVER_PROCESS_VMWARE_REVERT_SNAPSHOT_BEGIN,lastSnapshot.getSessionName(), vmConf.vmName);
			
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
				vmwareOBJ.revertToSnapshot(vmConf.vmName, vmConf.vmuuid, lastSnapshot.getBootableSnapGuid());
				
			} catch (Exception e) {
				msg = "Failed to revert snapshot: sessionName["+lastSnapshot.getSessionName()
					+"]BootableSsnapshot["+lastSnapshot.getBootableSnapGuid()+"]:";
				log.error(msg+e.getMessage(),e);
				
				msg = FailoverMessage.getResource(
						FailoverMessage.FAILOVER_PROCESS_VMWARE_REVERT_SNAPSHOT_FAIL,lastSnapshot.getSessionName(), vmConf.vmName);
			
				msg += FailoverMessage.getResource(FailoverMessage.FAILOVER_Process_ESX_RETURN_MESSAGE) + e.getMessage();
				
				HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.FailoverFailure, msg);
				
				return retValue;
			}
			
			if (!VDDKServiceInstalled) {
				try {
					log.info("Try install the VDDK Service on local machine");
					HAService.getInstance().installVDDKService();
				} catch (Exception e) {
					log.warn("Failed to install the VDDK Service", e);
				}
				VDDKServiceInstalled = true;
			}
			
			// sessionGuid is null means auto failover
			if (jobScript.isPowerOnWithIPSettings() || sessionGuid == null) {				
				List<IPSetting> ipSettings = new ArrayList<IPSetting>();
				if (jobScript.getIpSettings()!=null&&jobScript.getIpSettings().size()>0) {
					ipSettings = jobScript.getIpSettings();
					HAService.getInstance().setFailoverJobScript(jobScript);
				} else {
					if (jobScript.getFailoverMechanism()!=null&&jobScript.getFailoverMechanism().size()>0) {
						Virtualization virtualInfo = jobScript.getFailoverMechanism().get(0);
						if (virtualInfo.getNetworkAdapters()!=null&&virtualInfo.getNetworkAdapters().size()>0) {							
							List<NetworkAdapter> networkAdapters = virtualInfo.getNetworkAdapters();
							for (NetworkAdapter networkAdapter : networkAdapters) {
								if (networkAdapter.getIpSettings()!=null&&networkAdapter.getIpSettings().size()>0) {								
									IPSetting ipSetting = networkAdapter.getIpSettings().get(0);
									ipSettings.add(ipSetting);
								}
							}
						}
					}
				}
				
				if (ipSettings!=null && ipSettings.size()>0) {
					vmwareOBJ.removeAllVirtualNics(vmConf.vmName, vmConf.vmuuid, vmConf.esxHost);
					
					ArrayList<String> virtualNetworkList = new ArrayList<String>();
					try {
						ArrayList<ESXNode> nodeList = vmwareOBJ.getESXNodeList();
						for (ESXNode esxNode : nodeList) {
							if(esxNode.getEsxName().equalsIgnoreCase(vmConf.esxHost)){
								ArrayList<VirtualNetworkInfo> virtualNetworkInfos = vmwareOBJ.getVirtualNetworkList(esxNode);
								
								for (int i = 0; i < virtualNetworkInfos.size(); i++) {
									virtualNetworkList.add(virtualNetworkInfos.get(i).getVirtualName());
								}
								
								break;
							}
						}
					} catch (Exception e) {
						log.error("Failed to get virtual nework list from " + vmConf.esxHost, e);
					}
					
//					List<IPSettingDetail> ipSettingDetails = new ArrayList<IPSettingDetail>();
					for (int i=0;i<ipSettings.size();i++) {
						IPSetting ipSetting = ipSettings.get(i);
//						IPSettingDetail detail = new IPSettingDetail();
//						detail.setDhcp(ipSetting.isDhcp());
//						detail.setDns(ipSetting.getDnses());
//						List<String> gatewayList = new ArrayList<String>();
//						for (Gateway gateway : ipSetting.getGateways()) {
//							gatewayList.add(gateway.getGatewayAddress());
//						}
//						detail.setGateways(gatewayList);
//						List<String> ipList = new ArrayList<String>();
//						List<String> subnetList = new ArrayList<String>();
//						for (IPAddressInfo ipAddress :ipSetting.getIpAddresses()) {
//							ipList.add(ipAddress.getIp());
//							subnetList.add(ipAddress.getSubnet());
//						}
//						detail.setIps(ipList);
//						detail.setSubnets(subnetList);
//						detail.setNicType(ipSetting.getNicType());
//						detail.setVirtualNetwork(ipSetting.getVirtualNetwork());
//						for (int j = 0; j < ipSetting.getWins().size(); j++) {	
//							if (j==0)
//								detail.setWinsPrimary(ipSetting.getWins().get(j));
//							if (j==1)
//								detail.setWinsSecond(ipSetting.getWins().get(j));
//						}
//						ipSettingDetails.add(detail);
						CAVirtualNICCard nic = new CAVirtualNICCard();
						nic.setadapterType(ipSetting.getNicType());
						nic.setLabel("Adapter");
						nic.setNetworkName(ipSetting.getVirtualNetwork());
						try {
							if (virtualNetworkList==null || !virtualNetworkList.contains(ipSetting.getVirtualNetwork())) {
								msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_Error_VIRTUAL_NETWORK_NOTAVAILABLE, ipSetting.getVirtualNetwork(), jobScript.getProductionServerName());
								HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
							}
							log.info("addNicCard for power on VM - " + "VM name is " + vmConf.vmName + ". NIC type is " + ipSetting.getNicType() + ". Virtual network is " + ipSetting.getVirtualNetwork());
							vmwareOBJ.addNicCard(vmConf.vmName, vmConf.vmuuid, vmConf.esxHost, nic);
							Thread.sleep(ADD_NIC_TIME);
						} catch (Exception e) {
							log.error("Failed to add nic card." + " VM name is " + vmConf.vmName + ". NIC type is " + ipSetting.getNicType() + ". Virtual network is " + ipSetting.getVirtualNetwork());
						}
					}
					String morefId = vmwareOBJ.getVMMorefID(vmConf.vmName, vmConf.vmuuid);
					List<String> diskUrls = vmwareOBJ.getCurrentDiskUrls(vmConf.vmName, vmConf.vmuuid);
//					List<DNSUpdaterParameters> dnsParameters = new ArrayList<DNSUpdaterParameters>();
//					if (jobScript.getDnsParameters() == null) {
//						jobScript.setDnsParameters(dnsParameters);
//					}
//					for (DNSUpdaterParameters dnsParameter : jobScript.getDnsParameters()) {
//						DNSUpdaterParameters parameter = new DNSUpdaterParameters();
//						parameter.setDns(dnsParameter.getDns());
//						parameter.setDnsServerType(dnsParameter.getDnsServerType());
//						parameter.setHostIp(dnsParameter.getHostIp());
//						parameter.setHostname(dnsParameter.getHostname());
//						parameter.setTtl(dnsParameter.getTtl());
//						if (dnsParameter.getDnsServerType() == 0) {
//							parameter.setUsername(dnsParameter.getUsername());
//							if (dnsParameter.getCredential() != null) {								
//								parameter.setCredential(BackupService.getInstance().getNativeFacade().decrypt(dnsParameter.getCredential()));
//							}
//						} else {
//							parameter.setKeyFile(dnsParameter.getKeyFile());
//						}
//						dnsParameters.add(parameter);
//					}
					String localSessionGuid = null;
					{
						String sessionGuids = lastSnapshot.getSessionGuid();
						String[] tokens = sessionGuids.split("[|]");
						localSessionGuid = tokens[tokens.length-1];
					}
					String localSessionName = lastSnapshot.getSessionName();
					String remoteFileName = localSessionName + "-" + localSessionGuid +"-" + CommonUtil.ADRCONFIG_XML_FILE;
					InputStream out = getRemoteFileFromStorage(vmConf.vmName, vmConf.vmuuid, remoteFileName);
					if(out != null){
						injectServiceWithADRConfigure(jobScript, out, diskUrls, jobID, ipSettings, morefId, vmConf);
					} else {
						String backupInfoFileName = localSessionName + "-" + localSessionGuid +"-" + "BackupInfo.XML";
					    InputStream backupInfoOut = getRemoteFileFromStorage(vmConf.vmName, vmConf.vmuuid, backupInfoFileName);
						DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
						domFactory.setNamespaceAware(true); // never forget this!
						DocumentBuilder builder;
						builder = domFactory.newDocumentBuilder();
						Document backupInfoDoc = builder.parse(new InputSource(backupInfoOut));
					    BackupInfo backupInfo = BackupInfoFactory.parseBackupInfoDocument(backupInfoDoc);
					    boolean isX86 = backupInfo.getServerInfo().getOsType().equals("64-bit") ? false : true;
						String scriptPath = PrepareInstantVMHelperScript(jobScript, ipSettings, null);
						injectServiceWithoutADRConfigure(jobScript, diskUrls, jobID, morefId, vmConf, isX86, scriptPath);
					}
				}
			}
			
			try{
					
				int ret = vmwareOBJ.checkVMToolsVersion(vmConf.vmName, vmConf.vmuuid);
				if(ret == 1){
					msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_VMWARE_TOOL_OLD_VERSION);
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());

				}else if (ret == 0){
					msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_PROCESS_VMWARE_TOOL_QUERY_FAILED);
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				}
					
				vmwareOBJ.powerOnVM(vmConf.vmName, vmConf.vmuuid);
				
				msg = FailoverMessage.getResource(
						FailoverMessage.FAILOVER_PROCESS_VM_POWER_ON, vmConf.vmName);
			
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
			}catch (Exception e) {
				
				log.error(e.getMessage(),e);
				
				msg = FailoverMessage.getResource(
							FailoverMessage.FAILOVER_PROCESS_VM_POWER_ON_FAIL, vmConf.vmName);
				
				msg += FailoverMessage.getResource(FailoverMessage.FAILOVER_Process_ESX_RETURN_MESSAGE) + e.getMessage();
				
				HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.FailoverFailure, msg);
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
				return retValue;
			}
				
			try{
				//if mount VMware Tools Failed we can still power on VM
				log.debug("mount tools image!!!");
				msg = FailoverMessage.getResource(
							FailoverMessage.FAILOVER_PROCESS_VMWARE_MOUNT_VM_TOOLS_BEGIN, vmConf.vmName);
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
				vmwareOBJ.mountVMwareToolsImage(vmConf.vmName, vmConf.vmuuid);
				
			}catch (Exception e) {
				
				log.error(e.getMessage(),e);
				
				msg = FailoverMessage.getResource(
							FailoverMessage.FAILOVER_PROCESS_VMWARE_MOUNT_VM_TOOLS_FAIL, vmConf.vmName);
				
				msg += FailoverMessage.getResource(FailoverMessage.FAILOVER_Process_ESX_RETURN_MESSAGE) + e.getMessage();
				
				HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.FailoverFailure, msg);
	
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
				//return retValue;
					
			}
		}
		finally {
			if(vmwareOBJ != null){
				vmwareOBJ.close();
			}
		}
		
		return 0;
	}
	
	private void injectServiceWithADRConfigure(FailoverJobScript jobScript, InputStream out, List<String> diskUrls, long jobID, List<IPSetting> ipSettings,
			String morefId, VMWareInfo vmConf) throws HAException {
		ADRConfigure adrConfigure = ADRConfigureFactory.parseADRConfigureXML(out);
		String scriptPath = PrepareInstantVMHelperScript(jobScript, ipSettings, adrConfigure);
		String systemVolumeDiskSignature = ADRConfigureUtil.getSystemVolumeDiskSignature(adrConfigure);
		String bootVolumeDiskSignature = ADRConfigureUtil.getBootVolumeDiskSignature(adrConfigure);
		String bootVolumeWindowsSystemRootFolder = ADRConfigureUtil.getBootVolumeWindowsSystemRootDirectory(adrConfigure);
		if (StringUtil.isEmptyOrNull(bootVolumeWindowsSystemRootFolder))
			bootVolumeWindowsSystemRootFolder = "C:\\Windows";
		String systemVolumeVmdkUrl = null;
		String bootVolumeVmdkUrl = null;
		VMwareVolumeInfo volumeInfo = new VMwareVolumeInfo();
		volumeInfo.setBootVolumeWindowsSystemRootFolder(bootVolumeWindowsSystemRootFolder);
		if(adrConfigure.isUEFI())
		{
			volumeInfo.setBootVolumeGUID(adrConfigure.getBootvolume().getGuidPath().toUpperCase());
			volumeInfo.setSystemVolumeGUID(adrConfigure.getSystemVolume().getGuidPath().toUpperCase());
		}
		for(String diskUrl : diskUrls){
			String childDiskSig = getChildDiskSig(diskUrl, vmConf, morefId, jobID, jobScript.getAFGuid());
			if(systemVolumeDiskSignature.equalsIgnoreCase(fillSignature(childDiskSig))){
				systemVolumeVmdkUrl = diskUrl;
			}
			if(bootVolumeDiskSignature.equalsIgnoreCase(fillSignature(childDiskSig))){
				bootVolumeVmdkUrl = diskUrl;
			}
		}
		
		if(systemVolumeVmdkUrl == null || bootVolumeVmdkUrl == null){
			log.error("System volume VMDK URL or boot volume VMDK URL is not found, stop to inject service.");
			return;
		}
		
		List<String> vmdkUrls = new ArrayList<String>();
		vmdkUrls.add(systemVolumeVmdkUrl);
		if(!systemVolumeVmdkUrl.equals(bootVolumeVmdkUrl)){
			vmdkUrls.add(bootVolumeVmdkUrl);
		}
		
		if (jobScript.getDnsParameters() != null) {
			log.info("DoRVCMInjectService - Adapter size is " + ipSettings.size() + " and DNS redirection size is " + jobScript.getDnsParameters().size());
		} else {
			log.info("DoRVCMInjectService - Adapter size is " + ipSettings.size() + " and DNS redirection size is null.");
		}

		long value = BackupService.getInstance().getNativeFacade().DoRVCMInjectService(HACommon.getHostNameForVddk(vmConf.hostname,vmConf.port),vmConf.port,vmConf.exParams, vmConf.username,
				vmConf.password, morefId, volumeInfo, CommonUtil.FailoverVM_VMWARE, vmdkUrls,
				"Key", String.valueOf(jobID), ADRConfigureUtil.GetVolumesOnNonSystemOrBootDisk(adrConfigure), adrConfigure.isX86(), scriptPath);
		log.info("DoRVCMInjectService in power on VM return value is " + value);
	}
	
	private void injectServiceWithoutADRConfigure(FailoverJobScript jobScript, List<String> diskUrls, long jobID, String morefId, VMWareInfo vmConf, boolean isX86, String scriptPath) {
		VMwareVolumeInfo volumeInfo = new VMwareVolumeInfo();
		volumeInfo.setBootVolumeGUID("C:\\Windows");
		for (String url : diskUrls) {
			List<String> vmdkUrl = new ArrayList<String>();
			vmdkUrl.add(url);
			log.info("In power on VM injectServiceWithoutADRConfigure. Try to mount URL is " + url);
			if (jobScript.getDnsParameters() != null) {
				log.info("DoRVCMInjectService - DNS redirection size is " + jobScript.getDnsParameters().size());
			} else {
				log.info("DoRVCMInjectService - DNS redirection size is null.");
			}

			long value = BackupService.getInstance().getNativeFacade().DoRVCMInjectService(HACommon.getHostNameForVddk(vmConf.hostname,vmConf.port),vmConf.port,vmConf.exParams, vmConf.username,
					vmConf.password, morefId, volumeInfo, CommonUtil.FailoverVM_VMWARE, vmdkUrl,
					"Key", String.valueOf(jobID), null, isX86, scriptPath);
			log.info("DoRVCMInjectService in power on VM return value is " + value);
		}
	}
	
	public int configureBootableSession(String job,FailoverJobScript jobScript, VMSnapshotsInfo lastSnapshot, long jobID) throws Exception {
		
		int result = FAILOVER_FAILURE;
		
		List<Virtualization> virMachanism = jobScript.getFailoverMechanism();
		if(virMachanism == null || virMachanism.size() == 0){
			log.error("Virtualization channel is null!!!!!!!");
			return FAILOVER_FAILURE;
		}
		
		Virtualization virtualization = virMachanism.get(0);
		
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		
		if (jobID == -1){
			jobID = nativeFacade.getJobID();
		}
		//VMSnapshotsInfo lastSnapshot = preBootableSnapshot;
		
		String msg = FailoverMessage.getResource(
				FailoverMessage.FAILOVER_PROCESS_BEGIN_CONFIGURE_BOOTABLE_SESSION,lastSnapshot.getSessionName(),jobScript.getProductionServerName());
		
		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
				new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
		
		
		VMWareInfo vmConf = getVMWareInfo(virtualization,jobScript.getAFGuid());
		
		InputStream out = null;
		try {
			
			log.debug("Begin to get Connection!!!");
			vmwareOBJ =  CAVMwareInfrastructureManagerFactory
			.getCAVMwareVirtualInfrastructureManager(vmConf.hostname,vmConf.username, 
					vmConf.password, vmConf.protocol, true, vmConf.port);
			
			if(vmwareOBJ == null){
				log.error("Failed to connect to vmware host!!!!");
				log.error(vmConf.hostname+ " : " +vmConf.username);
				return FAILOVER_FAILURE;
			}
			else
			{
				vmConf.setExParams(vmwareOBJ);
			}
			
			
			if(virtualization instanceof VMwareESX){
				ESXNode node = vmwareOBJ.getESXNodeList().get(0);
				vmConf.esxHost = node.getEsxName();
			}
			
			//if manual failover sessionGuid is not null or empty string
			//if auto failover sessionGuid is null or empty string
			
			String localSessionGuid = lastSnapshot.getSessionGuid();
			String localSessionName = lastSnapshot.getSessionName();
			String localSnapshotUrl = lastSnapshot.getSnapGuid();
						
			log.debug("Get adrconfigure.xml from VM Storage!!!!!");
			
			msg = FailoverMessage.getResource(
					FailoverMessage.FAILOVER_PROCESS_VMWARE_OBTAIN_ADRCONFIG, vmConf.vmName);
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
			
			String remoteFileName = localSessionName + "-" + localSessionGuid +"-" + CommonUtil.ADRCONFIG_XML_FILE;
			
			out = getRemoteFileFromStorage(vmConf.vmName, vmConf.vmuuid, remoteFileName);
			if(out != null){
				result = configureBootableSessionWithAdrconfigure(out, jobScript, jobID, vmConf, localSnapshotUrl, jobScript.getAFGuid(), lastSnapshot);
				
			}else{
				log.info(String.format("Doesn't find file '%s' in vmware datastore. ", remoteFileName));
				remoteFileName = localSessionName + "-" + localSessionGuid +"-" + CommonUtil.PARTIAL_ADRCONFIG_File;
				out = getRemoteFileFromStorage(vmConf.vmName, vmConf.vmuuid, remoteFileName);
				// TODO
				// We have better remove this judgment in the future, since VMDiskInfo.xml is always existed. 
				if(out != null){
					String backupInfoFileName = localSessionName + "-" + localSessionGuid +"-" + "BackupInfo.XML";
				    InputStream backupInfoOut = getRemoteFileFromStorage(vmConf.vmName, vmConf.vmuuid, backupInfoFileName);
					DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
					domFactory.setNamespaceAware(true); // never forget this!
					DocumentBuilder builder;
					builder = domFactory.newDocumentBuilder();
					Document backupInfoDoc = builder.parse(new InputSource(backupInfoOut));
				    BackupInfo backupInfo = BackupInfoFactory.parseBackupInfoDocument(backupInfoDoc);
				    boolean isX86 = backupInfo.getServerInfo().getOsType().equals("64-bit") ? false : true;
				    String scriptPath = PrepareInstantVMHelperScript(jobScript, new ArrayList<IPSetting>(), null);
					result = configureBootableSessionWithoutAdrconfigure(out, jobScript, jobID, vmConf, localSnapshotUrl, isX86, scriptPath);
				}else {
					log.error(String.format("Neither %s nor %s is found. Fail to configure bootable snapshot. ", 
							CommonUtil.ADRCONFIG_XML_FILE, CommonUtil.PARTIAL_ADRCONFIG_File));
				}
				
			}
		} catch (Exception e) {
			log.error("VMWare Failover Failed!!!!->"+e.getMessage(),e);
			msg = FailoverMessage.getResource(FailoverMessage.FAILOVER_RET_MSG, e.getMessage());
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			result = FAILOVER_FAILURE;
		}finally{
			if(out != null) {
				try {
					out.close();
				}catch(Exception e) {}
			}
			
			if(vmwareOBJ != null){
				vmwareOBJ.close();
			}
			
		}
		
		if (result == FAILOVER_SUCCESS) {
			msg = FailoverMessage.getResource(
					FailoverMessage.FAILOVER_PROCESS_FINISH_CONFIGURE_BOOTABLE_SESSION,lastSnapshot.getSessionName(),jobScript.getProductionServerName());
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
		} else { 
			msg = FailoverMessage.getResource(
					FailoverMessage.FAILOVER_PROCESS_FAILED_CREATE_BOOTABLE_SNAHSOT,lastSnapshot.getSessionName(),jobScript.getProductionServerName());
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
		}

		return result;
	}
	
	private void addNicCardToVM(FailoverJobScript jobScript, ADRConfigure adrConfigure, VMWareInfo vmInfo) throws Exception {
		try {
			vmwareOBJ.removeAllVirtualNics(vmInfo.vmName, vmInfo.vmuuid, vmInfo.esxHost);
		} catch (Exception e) {
			log.error("Failed to remove all virtual nic in " + vmInfo.esxHost + " for " + vmInfo.vmName);
		}
		Virtualization dest = jobScript.getFailoverMechanism().get(0);
		List<NetworkAdapter> adapters = null;
		if (dest instanceof VMwareESX) {
			adapters = ((VMwareESX) dest).getNetworkAdapters();
		} else if (dest instanceof VMwareVirtualCenter) {
			adapters = ((VMwareVirtualCenter) dest).getNetworkAdapters();
		}
		if (adapters == null || adapters.size() == 0) {
			log.error("No network adapter defination in FailoverJobScript xml.");
			return;
		}
		if (jobScript.isConfiguredSameNetwork() && adrConfigure!=null && adrConfigure.getNetadapters()!=null && adrConfigure.getNetadapters().size()>0) {
			for (int i=0;i<adrConfigure.getNetadapters().size();i++) {				
				addNicCard(adapters.get(0), vmInfo);				
			}
		} else {
			for (NetworkAdapter adapter : adapters) {
				addNicCard(adapter, vmInfo);
			}
		}
	}
	
	private void addNicCard(NetworkAdapter adapter, VMWareInfo vmInfo) throws Exception{
		CAVirtualNICCard nic = new CAVirtualNICCard();
		nic.setadapterType(adapter.getAdapterType());
		nic.setLabel(adapter.getAdapterName());
		nic.setNetworkName(adapter.getNetworkLabel());
		nic.setAddressType("generated");
		try {
			log.info("addNicCard in configureBootableSession. " + "VM name is " + vmInfo.vmName + ". NIC type is " + adapter.getAdapterType() + ". Virtual network is " + adapter.getNetworkLabel());
			vmwareOBJ.addNicCard(vmInfo.vmName, vmInfo.vmuuid, vmInfo.esxHost, nic);
			Thread.sleep(ADD_NIC_TIME);
		} catch (Exception e) {
			log.error("Failed to add nic card." + vmInfo.vmName);
			throw e;
		}
	}
	
	private int configureBootableSessionWithAdrconfigure(InputStream out,FailoverJobScript jobScript,long jobID,VMWareInfo vmConf,
														String localSnapshotUrl, String afguid, VMSnapshotsInfo lastSnapshot) throws Exception{
		
		log.info("Begin to configure bootable session with adrconfigure file");
		
		ADRConfigure adrConfigure = ADRConfigureFactory.parseADRConfigureXML(out);
		String scriptPath = PrepareInstantVMHelperScript(jobScript, new ArrayList<IPSetting>(), adrConfigure);
		String systemVolumeDiskSignature = ADRConfigureUtil.getSystemVolumeDiskSignature(adrConfigure);
		String bootVolumeDiskSignature = ADRConfigureUtil.getBootVolumeDiskSignature(adrConfigure);
		String bootVolumeDriveLetter = ADRConfigureUtil.getBootVolumeDriveLetter(adrConfigure);

		VMwareVolumeInfo volumeInfo = new VMwareVolumeInfo();
		
		if (StringUtil.isEmptyOrNull(bootVolumeDriveLetter))
			bootVolumeDriveLetter = "C:";
		else
			bootVolumeDriveLetter = bootVolumeDriveLetter.substring(0,1) + ":";
		String bootVolumeWindowsSystemRootFolder = ADRConfigureUtil.getBootVolumeWindowsSystemRootDirectory(adrConfigure);
		if (StringUtil.isEmptyOrNull(bootVolumeWindowsSystemRootFolder))
			bootVolumeWindowsSystemRootFolder = "C:\\Windows";
		
		String msg = FailoverMessage.getResource(
				FailoverMessage.FAILOVER_PROCESS_VMWARE_FIND_BOOT_VOLUME, bootVolumeDriveLetter);
		
		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
				new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
		
		
		String morefId = vmwareOBJ.getVMMorefID(vmConf.vmName, vmConf.vmuuid);
		
		String systemVolumeVmdkUrl = null;
		String bootVolumeVmdkUrl = null;
		vmwareOBJ.revertToSnapshot(vmConf.vmName, vmConf.vmuuid, localSnapshotUrl);
		ArrayList<String> diskUrls  = vmwareOBJ.getCurrentDiskUrls(vmConf.vmName, vmConf.vmuuid);
		if(log.isDebugEnabled()){
			for(int i=0; i<diskUrls.size();i++){
				log.debug(diskUrls.get(0) + "\n");
			}
		}
		
		log.info("Get system volume vmdk url via signature!!!!!");
		log.info("systemVolumeDiskSignature: " + systemVolumeDiskSignature);
		log.info("bootVolumeDiskSignature: " + bootVolumeDiskSignature);
		for(String diskUrl : diskUrls){
			
			String childDiskSig = getChildDiskSig(diskUrl, vmConf, morefId, jobID, afguid);

			log.info("Disk Signature: " + childDiskSig);
			
			if(StringUtil.isEmptyOrNull(childDiskSig)){
				
				msg = FailoverMessage.getResource(
						FailoverMessage.FAILOVER_ERROR_VMWARE_INVALID_DISKSIGNATURE, diskUrl);
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
				powerState vmstates = vmwareOBJ.getVMPowerstate(vmConf.vmName, vmConf.vmuuid);
				if (vmstates == powerState.poweredOn) {
					msg = FailoverMessage.getResource(
							FailoverMessage.FAILOVER_PROCESS_FAILED_CREATE_BOOTABLE_SNAHSOT_VM_POWER_ON,vmConf.vmName);
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				}
				
				log.error("Invalid disk signature: " + childDiskSig);
				return FAILOVER_FAILURE;
			}
			
			log.info("fillSignature(childDiskSig)): " + fillSignature(childDiskSig));
			
			if(systemVolumeDiskSignature.equalsIgnoreCase(fillSignature(childDiskSig))){
				systemVolumeVmdkUrl = diskUrl;
			}
			
			if(bootVolumeDiskSignature.equalsIgnoreCase(fillSignature(childDiskSig))){
				bootVolumeVmdkUrl = diskUrl;
			}
			
			/*if(!StringUtil.isEmptyOrNull(systemVolumeVmdkUrl) && 
			   !StringUtil.isEmptyOrNull(bootVolumeVmdkUrl)){
				break;
			}*/
			ArrayList<DiskExtent> dskExtnsArrayList = ADRConfigureUtil.getFirstDiskExtents(adrConfigure, childDiskSig);
			if(dskExtnsArrayList != null)
			{
				for(int list = 0; list < dskExtnsArrayList.size(); list++)
				{
					
					long setGeometry = BackupService.getInstance().getNativeFacade().SetVMDKGeometry(
													HACommon.getHostNameForVddk(vmConf.hostname,vmConf.port), 
													vmConf.username, vmConf.password, morefId, vmConf.port,vmConf.exParams,diskUrl, 
													dskExtnsArrayList.get(list).getOffset(), jobID + "", jobScript.getAFGuid());
					if(setGeometry == 0)
					{
						log.info("Successfully set disk geometry of disk " + diskUrl + "at offset " + dskExtnsArrayList.get(list).getOffset());
					}
					else
					{
						log.error("Failed to set disk geometry of disk " + diskUrl + "at offset " + dskExtnsArrayList.get(list).getOffset());
					}
				}
			}
			else {
				log.info("No disk entents found with volume offset 0 on disk bearing signature " + childDiskSig);
			}
			
		}
		
		if(systemVolumeVmdkUrl == null || bootVolumeVmdkUrl == null){
			msg = FailoverMessage.getResource(
					FailoverMessage.FAILOVER_ERROR_VMWARE_INVALID_SYS_BOOT_DISKSIGNATURE);
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
			log.error("No system volume vmdkUrl or boot volume vmdkUrl!!!  systemVolumeDiskSignature:"+systemVolumeDiskSignature+" bootVolumeDiskSignature:"+bootVolumeDiskSignature);
			return FAILOVER_FAILURE;
		}
		
		List<String> vmdkUrls = new ArrayList<String>();
		vmdkUrls.add(systemVolumeVmdkUrl);
		if(!systemVolumeVmdkUrl.equals(bootVolumeVmdkUrl)){
			vmdkUrls.add(bootVolumeVmdkUrl);
		}
		
//		msg = FailoverMessage.getResource(
//				FailoverMessage.FAILOVER_PROCESS_VMWARE_FIND_VMDK_URL, systemVolumeVmdkUrl+ ";" + bootVolumeVmdkUrl);
//		
//		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
//				new String[] { msg,"", "", "", "" }, null);
		
		
		msg = FailoverMessage.getResource(
				FailoverMessage.FAILOVER_PROCESS_VMWARE_DRIVER_INJECTION_BEGIN, bootVolumeDriveLetter);
		
		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
				new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
		
		
		String jobStringStr = CommonUtil.marshal(jobScript);
		String failoverOsNameString = "";
		
		//change failover os name for automation test
		String changedOSName = CommonUtil.getNewGuestOSName();
		if(!StringUtil.isEmptyOrNull(changedOSName)){
			failoverOsNameString = changedOSName;
		}else {
			//VSphere VM can not get host name if vm shuts down or vmware tools is not installed properly.
			if(jobScript.isVMJobScript()){
				failoverOsNameString = "";
			}
		}
		for (String url : vmdkUrls) {
			log.info("In configureBootableSessionWithAdrconfigure mount url is " + url);
		}
		
		long isInjected = 0;
		volumeInfo.setBootVolumeWindowsSystemRootFolder(bootVolumeWindowsSystemRootFolder);
		if (adrConfigure.isUEFI()) {
			// For UEFI, save bcd file locally
			volumeInfo.setBootVolumeGUID(adrConfigure.getBootvolume().getGuidPath().toUpperCase());
			volumeInfo.setSystemVolumeGUID(adrConfigure.getSystemVolume().getGuidPath().toUpperCase());
			
			String remoteFileName = lastSnapshot.getSessionName() + "-" + lastSnapshot.getSessionGuid() +"-" + CommonUtil.ADRINFOC_DRZ_FILE;
			InputStream drzFile = getRemoteFileFromStorage(vmConf.vmName, vmConf.vmuuid, remoteFileName);
			String drzFilePath = inputstreamToFile(drzFile, jobScript.getAFGuid());
			if (drzFilePath.equals("")) {
				log.error("Fail to save the file AdrInfoC.drz");
			}

			
			// Driver injection
			isInjected = BackupService.getInstance().getNativeFacade().DoVMWareDriverInjection(
					HACommon.getHostNameForVddk(vmConf.hostname,vmConf.port), 
					vmConf.username, vmConf.password, 
					morefId, vmConf.port, vmConf.exParams, vmdkUrls, volumeInfo,
					failoverOsNameString,CommonUtil.FailoverVM_VMWARE,
					CommonUtil.D2DFailoverJobScript,jobStringStr,jobID + "", jobScript.getAFGuid(), true, drzFilePath);
			
			// Remove drz file
			FileUtils.deleteDirectory(new File(CommonUtil.D2DHAInstallPath + "Temp\\" + jobScript.getAFGuid()));
		} else {	
			
			isInjected = BackupService.getInstance().getNativeFacade().DoVMWareDriverInjection(
												HACommon.getHostNameForVddk(vmConf.hostname,vmConf.port), 
												vmConf.username, vmConf.password,  
												morefId, vmConf.port, vmConf.exParams, vmdkUrls, volumeInfo,
												failoverOsNameString,CommonUtil.FailoverVM_VMWARE,
												CommonUtil.D2DFailoverJobScript,jobStringStr,jobID + "", jobScript.getAFGuid(), false, "");
		}
		if(isInjected == 0){
			log.debug("Successfully inject disk driver!!!");
		}else{
			msg = FailoverMessage.getResource(
					FailoverMessage.FAILOVER_PROCESS_VMWARE_DRIVER_INJECTION_FAIL, bootVolumeDriveLetter);
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
			return FAILOVER_FAILURE;
		}
		// Add adapter for VM with ADRConfigure
		
		addNicCardToVM(jobScript, adrConfigure, vmConf);

		long value = BackupService.getInstance().getNativeFacade().DoRVCMInjectService(HACommon.getHostNameForVddk(vmConf.hostname,vmConf.port),vmConf.port, vmConf.exParams, vmConf.username,
				vmConf.password, morefId, volumeInfo, CommonUtil.FailoverVM_VMWARE, vmdkUrls,
				"Key", String.valueOf(jobID), ADRConfigureUtil.GetVolumesOnNonSystemOrBootDisk(adrConfigure), adrConfigure.isX86(), scriptPath);
		log.info("DoRVCMInjectService in bootable snapshot return value is " + value);
		return FAILOVER_SUCCESS;
		
	}
	
	public String inputstreamToFile(InputStream ins, String afguid){
		File dir = new File(CommonUtil.D2DHAInstallPath + "Temp\\" + afguid);
		if (!dir.exists())
			dir.mkdirs();
		
		File file = new File(CommonUtil.D2DHAInstallPath + "Temp\\" + afguid + "\\" + CommonUtil.ADRINFOC_DRZ_FILE); 
		
		OutputStream os;
		try {
			os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			ins.close();
			return dir.getAbsolutePath();
		} catch (IOException e) {
			return "";
		}

	}
	
	private int configureBootableSessionWithoutAdrconfigure(InputStream out,
			FailoverJobScript jobScript, long jobID, VMWareInfo vmConf,
			String localSnapshotUrl, boolean isX86, String scriptPath) throws Exception {

		log.info("Begin to configure bootable session without adrconfigure file");
		
		String morefId = vmwareOBJ.getVMMorefID(vmConf.vmName, vmConf.vmuuid);

		vmwareOBJ.revertToSnapshot(vmConf.vmName, vmConf.vmuuid,
				localSnapshotUrl);
		VMwareVolumeInfo volumeInfo = new VMwareVolumeInfo();
		
		ArrayList<String> diskUrls = vmwareOBJ.getCurrentDiskUrls(vmConf.vmName, vmConf.vmuuid);
		
		String jobStringStr = CommonUtil.marshal(jobScript);
		String failoverOsNameString = "";

		// change failover os name for automation test
		//change failover os name for automation test
		String changedOSName = CommonUtil.getNewGuestOSName();
		if(!StringUtil.isEmptyOrNull(changedOSName)){
			failoverOsNameString = changedOSName;
		}else {
			//VSphere VM can not get host name if vm shuts down or vmware tools is not installed properly.
			if(jobScript.isVMJobScript()){
				failoverOsNameString = "";
			}
		}

		int result = 1;
		volumeInfo.setBootVolumeWindowsSystemRootFolder("C:\\Windows");
		for (String vmdkUrl : diskUrls) {
			

			long isInjected = BackupService.getInstance().getNativeFacade()
							.DriverInjectSingleVMDK(HACommon.getHostNameForVddk(vmConf.hostname,vmConf.port), 
													vmConf.username, vmConf.password, morefId, vmConf.port, vmConf.exParams, vmdkUrl,
												    failoverOsNameString,
												    CommonUtil.FailoverVM_VMWARE,
												    CommonUtil.D2DFailoverJobScript, jobStringStr,
												    jobID + "", jobScript.getAFGuid());
			
			result &= isInjected;
			List<String> vmdkUrlList = new ArrayList<String>();
			vmdkUrlList.add(vmdkUrl);
			
			log.info("In configureBootableSessionWithoutAdrconfigure, try to mount url is " + vmdkUrl);
			long value = BackupService.getInstance().getNativeFacade().DoRVCMInjectService(HACommon.getHostNameForVddk(vmConf.hostname,vmConf.port),vmConf.port,vmConf.exParams, vmConf.username,
					vmConf.password, morefId, volumeInfo, CommonUtil.FailoverVM_VMWARE, vmdkUrlList,
					"Key", String.valueOf(jobID), null, isX86, scriptPath);
			log.info("DoRVCMInjectService in bootable snapshot return value is " + value);
		}
		// Add adapter for VM without ADRConfigure
		addNicCardToVM(jobScript, null, vmConf);
		return result;

	}
	
	private String getChildDiskSig(String diskUrl, VMWareInfo vmConf, String morefId, long jobID, String afguid) {
		String childDiskSig = "";
		for(int i=0; i<5; i++ ){
			log.info("Try to get the disk signature for the time:"+i);

			childDiskSig = BackupService.getInstance().getNativeFacade().GetVMDKSignature(afguid,
					HACommon.getHostNameForVddk(vmConf.hostname,vmConf.port), vmConf.username,
					vmConf.password, morefId, vmConf.port, vmConf.exParams,"", diskUrl,jobID + "");
			if(!StringUtil.isEmptyOrNull(childDiskSig)){
				if(childDiskSig.compareTo("0")!=0){
					break;
				}
			}
		}
		return childDiskSig;
	}

	abstract VMWareInfo getVMWareInfo(Virtualization virtualization,String afguid);
	
	
	class VMWareInfo{	
	    String esxHost;
	    String hostname;
		String username;
		String password;
		String protocol;
		int    port;
		VMwareConnParams exParams;	
		String vmuuid;
		String vmName;
		
		public VMWareInfo()
		{
			exParams = new VMwareConnParams();
		}
		
		public void setExParams(CAVirtualInfrastructureManager vmwaremanager)
		{
			try {
				exParams.setThumbprint(vmwaremanager.getThumbprint());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void setExParams(VMwareConnParams exParams)
		{
			this.exParams = exParams;
		}
		
		public void setThumbprint(String thumbprint)
		{
			this.exParams.setThumbprint(thumbprint);
		}
		
	}
	
	private VMSnapshotsInfo getSpecificSnapshot(VMWareInfo vmConf,String sessionGuid,String afguid) throws Exception{
		
		VMSnapshotsInfo snapshot = null;
		
		Map<String, String> vmSnapshots = null;
		Set<String> keys = null;
		try {
			//check snapshot whether exists
			//if there are snapshots in configure file and no snapshot in vm.This means snapshots 
			//are deleted manually and all changes are merged into base disk
			vmSnapshots = vmwareOBJ.listVMSnapShots(vmConf.vmName, vmConf.vmuuid);
			if(vmSnapshots == null || vmSnapshots.size() == 0){
				return snapshot;
			}
			
			keys = vmSnapshots.keySet();
			
		} catch (Exception e1) {
			log.error(e1.getMessage());
			return snapshot;
		}

		VMwareSnapshotModelManager vmwareSnapshot = VMwareSnapshotModelManager.getManagerInstance(vmwareOBJ, vmConf.vmName,vmConf.vmuuid);
					
		if(vmwareSnapshot.isReady()){
							
			SortedSet<VMSnapshotsInfo> repositorySnapshots = vmwareSnapshot.getSnapshots(afguid);
			
			if(repositorySnapshots == null || repositorySnapshots.size() == 0){
				return snapshot;
			}

			ArrayList<VMSnapshotsInfo> tmp = new ArrayList<VMSnapshotsInfo>();
			for(VMSnapshotsInfo vm : repositorySnapshots){
				if(!keys.contains(vm.getSnapGuid())){
					tmp.add(vm);
				}
				else if(vm.isDRSnapshot()){
					tmp.add(vm);
				}
			}
			if(tmp.size()>0){
				//remove all unexisted snapshot
				try{
					repositorySnapshots.removeAll(tmp);
				}catch (Exception e) {
					log.error(e.getMessage());
				}				
			}
			
			if(repositorySnapshots.size() == 0){
				return null;
			}

			SortedSet<VMSnapshotsInfo> orderSnapshots = new TreeSet<VMSnapshotsInfo>(new Comparator<VMSnapshotsInfo>() {
				@Override
				public int compare(VMSnapshotsInfo o1, VMSnapshotsInfo o2) {
					return (int)(o1.getSnapNo() - o2.getSnapNo());
				}
			});
			
			orderSnapshots.addAll(repositorySnapshots);
			
			if(StringUtil.isEmptyOrNull(sessionGuid)){
				snapshot = orderSnapshots.last();
			}else{
				for(VMSnapshotsInfo onesnapshot : orderSnapshots){
					if(onesnapshot.getSessionGuid().equals(sessionGuid)){
						snapshot = onesnapshot;
						break;
					}
				}
			}
			
			return snapshot;

		}else{
			return null;
		}
		
	}
	
	private String fillSignature(String signature){
		
		if(StringUtil.isEmptyOrNull(signature)){
			return "";
		}

		if(signature.length() == 10){
			return signature;
		}
		
		if(signature.length() < 10){
			char[] prefix = new char[10-signature.length()];
			Arrays.fill(prefix, '0');
			signature = new String(prefix) + signature;
 		}
		
		return signature;
	}
	
	private void saveVCMEvent(FailoverJobScript jobScript, Date startTime, String status){
		try {
			VCMEventManager evnetManager = VCMEventManager.getInstance();
			VCMEvent event = getVCMEvent(jobScript);
			event.setStartTime(startTime);
			event.setStatus(status);
			evnetManager.saveVCMEvent(jobScript.getAFGuid(),event);
		} catch (Exception e) {
			CommonUtil.getExceptionStackMessage(e.getStackTrace());
		}
	}
	
	abstract VCMEvent getVCMEvent(FailoverJobScript jobScript);

	private InputStream getRemoteFileFromStorage(String vmName, String vmUUID, String fileName) throws Exception{

		int retryDownloadTimes = 0;
		InputStream in = null;
		while (true) {
			try {
				in = vmwareOBJ.getVMConfig(vmName, vmUUID, fileName);
				
				break;
				
			} catch (Exception e) {
				
				if (++retryDownloadTimes >= 3) {
					log.error("Failed to download file " + fileName, e);
					return null;
				}

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
				}
			}
		}

		return in;

	}
}
