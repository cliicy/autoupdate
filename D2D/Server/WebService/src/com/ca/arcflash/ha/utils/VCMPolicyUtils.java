package com.ca.arcflash.ha.utils;

import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.modelmanager.RepositoryUtil;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.ha.model.JobScriptCombo;
import com.ca.arcflash.ha.model.ProductionServerRoot;
import com.ca.arcflash.ha.model.VCMD2DBackupInfo;
import com.ca.arcflash.ha.model.VCMSavePolicyWarning;
import com.ca.arcflash.ha.model.internal.HAException;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanager.Disk_Info;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.ha.webservice.MonitorWebClientManager;
import com.ca.arcflash.jobscript.alert.AlertJobScript;
import com.ca.arcflash.jobscript.base.GenerateType;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.HyperV;
import com.ca.arcflash.jobscript.failover.VMwareESX;
import com.ca.arcflash.jobscript.failover.VMwareVirtualCenter;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ARCFlashStorage;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMwareESXStorage;
import com.ca.arcflash.jobscript.replication.VMwareVirtualCenterStorage;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.WebServiceFactory;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VMItem;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.replication.BackupDestinationInfo;
import com.ca.arcflash.webservice.replication.ManualConversionUtility;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;

public class VCMPolicyUtils {
	private static final Logger logger = Logger.getLogger(VCMPolicyUtils.class);
	
	public static BackupVM getBackupVM(String instanceUuid) throws ServiceException{
		//Step 3 : check vsphere information
		if(!HACommon.isTargetPhysicalMachine(instanceUuid)) {
			
			try {
				VirtualMachine vm = new VirtualMachine();
				vm.setVmInstanceUUID(instanceUuid);
				VMBackupConfiguration vmConfig = VSphereService.getInstance().getVMBackupConfiguration(vm);
				String msg = "Failed to get the VMBackupConfiguration object:InstaceUuid"+instanceUuid;
				if(vmConfig == null) {
					logger.error(msg);
					throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_GET_VM_BACKUPINFO);
				}
				
				BackupVM backupVM = vmConfig.getBackupVM();
				if(backupVM == null) {
					logger.error(msg);
					throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_GET_VM_BACKUPINFO);
				}
				
				return backupVM;
				
			} catch (Exception e) {
				if(e instanceof ServiceException) {
					throw (ServiceException)e;
				}
				else {
					logger.error("Failed to create the vmwareOBJ for esxHost");
					logger.error(e.getMessage());
					throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_CONNECT_ESX);
				}

			}
		}
		
		return null;
	}
	
	public static String getCurrentMachineHostName(){
		try {
			 return InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			return "";
		}
	}
	
	public static String getHostName(String instanceUuid) throws ServiceException{
		String hostName = "";
		if(HACommon.isTargetPhysicalMachine(instanceUuid)){
			try {
				hostName = InetAddress.getLocalHost().getHostName();
			} catch (Exception e) {
				throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_GET_HOSTNAME);
			}
		}
		else{
			BackupVM backupVM = getBackupVM(instanceUuid);
			if(backupVM!=null)
				hostName = backupVM.getVmName();
		}
		
		return hostName;
	}
	
	public static void validateAlertConfiguration(AlertJobScript alertJobScript)throws ServiceException{
		try {
			//check the alert setting
			if(alertJobScript.isAutoFaiover()||alertJobScript.isLicense()||alertJobScript.isManualFailover()||
					alertJobScript.isNotreachable()||alertJobScript.isReplicationError()||alertJobScript.isReplicationSpaceWarning()) {
				if(!HAService.getInstance().isSetEmail(alertJobScript.getEmailModel())) {
					PreferencesConfiguration preferencesConfiguration = CommonService.getInstance().getPreferences();
					if(preferencesConfiguration == null) {
						logger.error("The local preference is null");
						throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_GET_EMAIL_SETTING);
					}
					BackupEmail model = preferencesConfiguration.getEmailAlerts();
					if(model==null || model.isEnableSettings()) {
						logger.error("The local preference doesn't configure the email");
						throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_GET_EMAIL_SETTING);
					}
					if(StringUtil.isEmptyOrNull(model.getSmtp())) {
						logger.error("The smtp is null");
						throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_GET_EMAIL_SETTING);
					}
				}
			}
			
		} catch (Exception e) {
			if(e instanceof ServiceException) {
				throw (ServiceException)e;
			}
			else {
				logger.error(e);
				throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_GET_EMAIL_SETTING);
			}
		}
	}
	public static CAVirtualInfrastructureManager validateESX(String instanceUuid, JobScriptCombo jobScriptCombo, boolean skipResourcePool) throws ServiceException{
		//Step 1: validate the ESX information
		FailoverJobScript failoverJobScript = jobScriptCombo.getFailoverJobScript();
		ReplicationJobScript replicationJobScript = jobScriptCombo.getRepJobScript();
		//HeartBeatJobScript heartBeatJobScript = jobScriptCombo.getHbJobScript();
		
		String esxName = "";
		CAVirtualInfrastructureManager vmwareOBJ = null;
		String dcName = "";
		if(failoverJobScript.getVirtualType() != VirtualizationType.HyperV) {
			//set the esx node
			String esxHost = "";
			
			String userName = "";
			String password = "";
			String protocol = "";
			int    port = 0;
			if(failoverJobScript.getVirtualType() == VirtualizationType.VMwareESX) {
				VMwareESX esx = (VMwareESX)failoverJobScript.getFailoverMechanism().get(0);
				esxHost = esx.getHostName();
				esxName = esx.getEsxName();
				dcName = esx.getDataCenter();
				userName = esx.getUserName();
				password = esx.getPassword();
				port = esx.getPort();
				protocol = esx.getProtocol();
			}
			else if(failoverJobScript.getVirtualType() == VirtualizationType.VMwareVirtualCenter) {
				VMwareVirtualCenter vCenter = (VMwareVirtualCenter)failoverJobScript.getFailoverMechanism().get(0);
				esxHost = vCenter.getHostName();
				esxName = vCenter.getEsxName();
				dcName = vCenter.getDataCenter();
				userName = vCenter.getUserName();
				password = vCenter.getPassword();
				port = vCenter.getPort();
				protocol = vCenter.getProtocol();
			}
			
			
			try {
				vmwareOBJ = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(
						esxHost, userName, password, protocol, true, port);
				String esxVersion = vmwareOBJ.GetESXServerVersion();
				logger.debug("Succesfully get the ESX version:"+esxVersion +" for the Host:"+esxHost);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				String msg = String.format("Failed to create the vmwareOBJ for esxHost:%s with errorCode=%d" , esxHost,1);
				logger.error(msg);
				logger.error(e.getMessage(),e);
				if (vmwareOBJ != null) {
					try {
						vmwareOBJ.close();
					}
					catch (Exception ex) {
					}
				}
//				throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_CONNECT_ESX);
				throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_CONNECT_ESX,new Object[]{esxHost});
			}
			
			if (skipResourcePool)
				return vmwareOBJ;
			
			//check the ESX resource pool
			String tempESXName = "";
			try {
				ReplicationDestination dest = replicationJobScript.getReplicationDestination().get(0);
				String poolName = dest.getResourcePool();
				String poolRef = dest.getResourcePoolRef();
				
				if(failoverJobScript.getVirtualType()==VirtualizationType.VMwareVirtualCenter){
					tempESXName = esxName;
				}
				else{
					tempESXName = esxHost;
				}
				
				if(!HAService.getInstance().checkResourcePoolExist(vmwareOBJ, dcName, esxName,poolName,poolRef)){
					vmwareOBJ.close();
					String errorMsg=String.format("Failed to get the resource pool[%s] with resource pool ref[%s] in the ESX [%s]",poolName, poolRef, tempESXName);
					logger.error(errorMsg);
					throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_GET_RESOURCE_POOL, new Object[]{poolName,poolRef,tempESXName});
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e);
				if(e instanceof ServiceException){
					throw (ServiceException)e;
				}
			}
			
		}
		
		return vmwareOBJ;
	}
	public static WebServiceClientProxy validateMonitor(String instanceUuid,HeartBeatJobScript heartBeatJobScript, 
			ReplicationJobScript replicationJobScript, FailoverJobScript failoverJobScript) throws ServiceException{
		//Step 2: validate the monitor information
		WebServiceClientProxy monitorClient = null;
		try {
			String monitorHost = heartBeatJobScript.getHeartBeatMonitorHostName();
			if (ManualConversionUtility.isVSBWithoutHASupport(heartBeatJobScript)) {
				if (replicationJobScript.getBackupToRPS()) { // For RPS remote VSB
					monitorClient = WebServiceFactory.getFlashServiceV2(heartBeatJobScript.getHeartBeatMonitorProtocol(),
							heartBeatJobScript.getHeartBeatMonitorHostName(), heartBeatJobScript.getHeartBeatMonitorPort());
					monitorClient.getServiceV2().validateUser(GetUserNameFromUserTextField(heartBeatJobScript.getHeartBeatMonitorUserName()), 
							heartBeatJobScript.getHeartBeatMonitorPassword(),GetDomainNameFromUser(heartBeatJobScript.getHeartBeatMonitorUserName()));
				}
				else // For RHA remote VSB
					monitorClient = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);
			} else {
				monitorClient = WebServiceFactory.getFlashServiceV2(heartBeatJobScript.getHeartBeatMonitorProtocol(),
						heartBeatJobScript.getHeartBeatMonitorHostName(), heartBeatJobScript.getHeartBeatMonitorPort());
				monitorClient.getServiceV2().validateUser(GetUserNameFromUserTextField(heartBeatJobScript.getHeartBeatMonitorUserName()), 
						heartBeatJobScript.getHeartBeatMonitorPassword(),GetDomainNameFromUser(heartBeatJobScript.getHeartBeatMonitorUserName()));
				logger.info("Succesfully validate the monitor information:"+heartBeatJobScript.getHeartBeatMonitorHostName());
			}
			VersionInfo versionInfo = monitorClient.getServiceV2().getVersionInfo();
			if((versionInfo!=null)&&(versionInfo.getProductType()!=null)&&(versionInfo.getProductType().equals("1"))){
				String msg = String.format("The monitor machine %s is the SaaS node", monitorHost);
				logger.info(msg);
				throw new ServiceException(FlashServiceErrorCode.VCM_MONITOR_IS_SAAS_NODE, new Object[]{monitorHost});
			}
		} catch (WebServiceException e) {
			logger.error("Failed to validate the monitor user information for the montor:"+heartBeatJobScript.getHeartBeatMonitorHostName());
			logger.error(e);
			String host = "";
			if (replicationJobScript.getBackupToRPS())
				host = replicationJobScript.getRpsHostName();
			else {
				if (replicationJobScript.isVSphereBackup())
					host = failoverJobScript.getVSphereproxyServer().getVSphereProxyName();
				else
					host = replicationJobScript.getAgentNodeName();
			}
			
			String msg = HACommon.processWebServiceException(e, new Object[]{heartBeatJobScript.getHeartBeatMonitorHostName()});
			
			throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_CONNECT_MONITOR, new Object[]{host, heartBeatJobScript.getHeartBeatMonitorHostName(), msg});
		} catch (Exception e) {
			if(e instanceof ServiceException) {
				throw (ServiceException)e;
			}
			else {
				logger.error("Failed to validate the monitor user information for the montor:"+heartBeatJobScript.getHeartBeatMonitorHostName());
				logger.error(e);
				String host = "";
				if (replicationJobScript.getBackupToRPS())
					host = replicationJobScript.getRpsHostName();
				else {
					if (replicationJobScript.isVSphereBackup())
						host = failoverJobScript.getVSphereproxyServer().getVSphereProxyName();
					else
						host = replicationJobScript.getAgentNodeName();
				}
				
				throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_CONNECT_MONITOR, new Object[]{host, heartBeatJobScript.getHeartBeatMonitorHostName(), e.getMessage()});
			}

		}
		return monitorClient;
	}
	
	public static boolean installVDDKService(ReplicationJobScript replicationJobScript,HeartBeatJobScript heartBeatJobScript,
			StringBuilder installedServiceMachine) throws ServiceException{
		//begin to install the VDDK service
		boolean isVDDKNeedRebootMachine = false;
		if(replicationJobScript.getVirtualType()!= VirtualizationType.HyperV){
			int VDDKInstalledResult = 0;
			// Check if the machine is x86, if yes, it should install vddk 5.1 and the error should be corrected.
			String driverName = null;
			try {
				boolean isProxy = replicationJobScript.getReplicationDestination().get(0).isProxyEnabled();
				boolean isMSPManualConversion = ManualConversionUtility.isVSBWithoutHASupport(replicationJobScript);
				if (isProxy && !isMSPManualConversion) {
					try {
						installedServiceMachine.append(heartBeatJobScript.getHeartBeatMonitorHostName());
						WebServiceClientProxy monitorClient = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);
						driverName = monitorClient.getServiceV2().isX86() ? HAService.VDDK_DRIVER_NAME_X86 : HAService.VDDK_DRIVER_NAME_X64;
						VDDKInstalledResult = monitorClient.getServiceV2().installVDDKService();
					} catch (Exception e) {
						if (e.getCause() instanceof ConnectException || e.getCause() instanceof SSLHandshakeException || 
								e.getCause() instanceof SocketException || e.getCause() instanceof SSLException)
							throw new ServiceException(FlashServiceErrorCode.Common_CantConnectHost, new Object[]{heartBeatJobScript.getHeartBeatMonitorHostName()});
						else throw e;
					}
				} else {
					installedServiceMachine.append(InetAddress.getLocalHost().getHostName());
					driverName = HAService.getInstance().isX86() ? HAService.VDDK_DRIVER_NAME_X86 : HAService.VDDK_DRIVER_NAME_X64;
					VDDKInstalledResult = HAService.getInstance().installVDDKService();
				}
				if((VDDKInstalledResult>=1)&&(VDDKInstalledResult<=4)){
					logger.debug("Succefully install the vddk service");
				}
				else if(VDDKInstalledResult==3010){
					logger.info("The VDDK service need to reboot the machine:"+installedServiceMachine.toString());
					isVDDKNeedRebootMachine = true;
				}
				else{
					logger.error("Failed to install the VDDK Service");
					throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_INSTALL_VDDK_SERVICE, new Object[]{installedServiceMachine.toString(), driverName});
				}
				
			} catch (Exception e) {
				if(e instanceof ServiceException)
					throw (ServiceException)e; 
				else {
					logger.error(e);
					throw new ServiceException(FlashServiceErrorCode.VCM_FAILED_INSTALL_VDDK_SERVICE, new Object[]{installedServiceMachine.toString(), driverName});
				}
			}
		}
		return isVDDKNeedRebootMachine;
	}
	
	public static boolean checkVMNameExist(
			ReplicationJobScript replicationJobScript,
			HeartBeatJobScript heartBeatJobScript, 
			FailoverJobScript failoverJobScript,  
			String vmName, WebServiceClientProxy monitorClient,
			CAVirtualInfrastructureManager vmwareOBJ, String afguid, StringBuilder vmUuid)throws ServiceException{
		boolean isExist = false;
		if(isVirtualizationHostChanged(failoverJobScript,vmName)) {
			ProductionServerRoot exServerRoot = null;
			try {
				logger.info("Begin to check the VMName: " + vmName);
				if (failoverJobScript.getVirtualType() == VirtualizationType.HyperV) {
					if (HAService.getInstance().isHyperVVMNameExist(monitorClient, vmName, vmUuid)) {
						isExist = true;
						logger.info("VM with name " + vmName + " exists, continue to check if VM is owned by the same node.");
						exServerRoot = HAService.getInstance().ifVmOwnedByAgent(vmName, replicationJobScript,heartBeatJobScript);
						if (exServerRoot == null) {
							logger.error("The VM Name has existed:" + vmName);
							throw new ServiceException(FlashServiceErrorCode.VCM_VM_NAME_EXIST, 
									new Object[] { vmName, ((HyperV)(failoverJobScript.getFailoverMechanism().get(0))).getHostName() });
						} 
					}
				}
				else {
					StringBuilder dcName = new StringBuilder();
					StringBuilder esxName = new StringBuilder();
					getEsxNameAndDcName(failoverJobScript, esxName, dcName);
					ESXNode esxNode = new ESXNode();
					esxNode.setDataCenter(dcName.toString());
					esxNode.setEsxName(esxName.toString());
					StringBuilder sameVMEsx = new StringBuilder();
					if (HAService.getInstance().isVMWareVMNameExist(vmwareOBJ,
							esxNode, vmName, vmUuid, sameVMEsx)) {
						isExist = true;
						logger.info("VM with name " + vmName + " exists, continue to check if VM is owned by the same node.");
						exServerRoot = HAService.getInstance().ifVmOwnedByAgent(vmwareOBJ, vmName, vmUuid.toString(), afguid);
						if (!esxNode.getEsxName().equalsIgnoreCase(sameVMEsx.toString()) || exServerRoot == null) {
							vmwareOBJ.close();
							logger.error("The VM name has existed:" + vmName + " ESX: " + sameVMEsx.toString());
							throw new ServiceException(FlashServiceErrorCode.VCM_VM_NAME_EXIST, new Object[] { vmName, sameVMEsx.toString() });
						} 
						
						replicationJobScript.getReplicationDestination().get(0).setVmUUID(vmUuid.toString());
						replicationJobScript.getReplicationDestination().get(0).setVmName(vmName);
					}
				}
				if (exServerRoot != null) {
					logger.info(String.format("The virtual machine %s was created against the current agent. Reuse it.", vmName));
					HAService.getInstance().updateLocalRepository(exServerRoot);
				} else {
					// VM not exist on the hypervisor
					logger.info("Remove the production server root info in repository.xml on converter");
					HAService.getInstance().removeReplicatedInfo(afguid, failoverJobScript.getVirtualType(), null);
				}
			} catch (Exception e) {
				if(e instanceof ServiceException ) {
					throw (ServiceException)e;
				}
				else {
					logger.error(e);
				}
			}
		} else {
			ReplicationJobScript oldRepScript = HAService.getInstance().getReplicationJobScript(afguid);
			if(oldRepScript != null){
				String vmUUID = oldRepScript.getReplicationDestination().get(0).getVmUUID();
				if (!StringUtil.isEmptyOrNull(vmUUID)) {
					replicationJobScript.getReplicationDestination().get(0).setVmUUID(vmUUID);
					isExist = true;
				}
				String vmName1 = oldRepScript.getReplicationDestination().get(0).getVmName();
				if (!StringUtil.isEmptyOrNull(vmName1))
					replicationJobScript.getReplicationDestination().get(0).setVmName(vmName1);
			}
		}
		return isExist;
	}
	
	public static void adjustJobScripts(FailoverJobScript failoverJobScript,ReplicationJobScript replicationJobScript, String vmName)throws ServiceException{
		//Step 5: adjust the job script
		try {
			failoverJobScript.getFailoverMechanism().get(0).setVirtualMachineDisplayName(vmName);
			replicationJobScript.getReplicationDestination().get(0).setVirtualMachineDisplayName(vmName);
		} catch (Exception e) {
			logger.error("Failed to adjust the job script:"+e.getMessage());
			throw new ServiceException(FlashServiceErrorCode.VCM_FAILEE_UPDATE_JOBSCRIPT);
		}
	}
	
	public static void validateStandbyVMSnapshotCount(ReplicationJobScript replicationJobScript) {
		int vmSnapshotCount = replicationJobScript.getStandbySnapshots();
		VirtualizationType virtualType = replicationJobScript.getVirtualType();
		switch (virtualType) {
		case HyperV:
			int maxHyperVCount = HACommon.getMaxSnapshotCountForHyperV(replicationJobScript.getAFGuid());
			if (vmSnapshotCount < 1 || vmSnapshotCount > maxHyperVCount) {
				String msg = String.format("The standby VM snapshot count [%d] is not valid (1 - %d), will be set to the maximum number [%d]", 
						vmSnapshotCount, maxHyperVCount, maxHyperVCount);
				logger.warn(msg);
				replicationJobScript.setStandbySnapshots(maxHyperVCount);
			}
			break;
		case VMwareESX:
		case VMwareVirtualCenter:
			int maxVMwareCount = HACommon.getMaxSnapshotCountForVMware(replicationJobScript.getAFGuid());
			if (vmSnapshotCount < 1 || vmSnapshotCount > maxVMwareCount) {
				String msg = String.format("The standby VM snapshot count [%d] is not valid (1 - %d), will be set to the maximum number [%d]", 
						vmSnapshotCount, maxVMwareCount, maxVMwareCount);
				logger.warn(msg);
				replicationJobScript.setStandbySnapshots(maxVMwareCount);
			}
			break;
		}
	}
	
	public static String GetDomainNameFromUser (String strUserInput)
	{
		String strDomain = "";
		
		if (strUserInput == null || strUserInput.isEmpty())
			return strDomain;
		
		int pos = strUserInput.indexOf("\\"); // ex) tant-a01\kimwo01
		if (pos == -1) // If not exist.
		{
			// Normal user input without domain field.
		}
		else 
		{
			// Extract domain part
			strDomain = strUserInput.substring(0, pos);
		}
		return strDomain;
	}
	
	public static String GetUserNameFromUserTextField (String strUserInput)
	{
		String strUser = "";
		
		if (strUserInput == null || strUserInput.isEmpty())
			return strUser;
		
		int pos = strUserInput.indexOf("\\"); // ex) tant-a01\kimwo01
		if (pos == -1) // If not exist.
		{
			// Normal user input without domain field.
			strUser = strUserInput;
		}
		else 
		{
			// Extract user name part
			strUser = strUserInput.substring(pos+1);
		}
		return strUser;
	}
	
	private static void getEsxNameAndDcName(FailoverJobScript failoverJobScript, StringBuilder esxName, StringBuilder dcName){
		if(failoverJobScript.getVirtualType() == VirtualizationType.VMwareESX) {
			VMwareESX esx = (VMwareESX)failoverJobScript.getFailoverMechanism().get(0);
			esxName.append(esx.getEsxName());
			dcName.append(esx.getDataCenter());
		}
		else if(failoverJobScript.getVirtualType() == VirtualizationType.VMwareVirtualCenter) {
			VMwareVirtualCenter vCenter = (VMwareVirtualCenter)failoverJobScript.getFailoverMechanism().get(0);
			esxName.append(vCenter.getEsxName());
			dcName.append(vCenter.getDataCenter());
		}
	}
    public static boolean isVirtualizationHostChanged(FailoverJobScript failoverJobScript,String newVMName) {
		
		String afGuid = failoverJobScript.getAFGuid();
		//first time configure the VCM setting
		FailoverJobScript oldFailoverScript = HAService.getInstance().getFailoverJobScript(afGuid);
		if(oldFailoverScript==null){
			return true;
		}
		
		boolean result=false;
		VirtualizationType newVirtualizationType=failoverJobScript.getVirtualType();
		VirtualizationType oldViVirtualizationType=oldFailoverScript.getVirtualType();
		Virtualization newVirtualization = failoverJobScript.getFailoverMechanism().get(0);
		Virtualization oldVirtualization = oldFailoverScript.getFailoverMechanism().get(0);
		String oldVMName = oldVirtualization.getVirtualMachineDisplayName();
		
		//change the VM name
		if(newVMName.compareToIgnoreCase(oldVMName)!=0){
			return true;
		}

		if(newVirtualizationType==VirtualizationType.HyperV){
			if((oldViVirtualizationType==VirtualizationType.VMwareESX)||
				(oldViVirtualizationType==VirtualizationType.VMwareVirtualCenter)){
				result=true;
			}
			else{
				//get HyperV Host
				HyperV newHyperV = (HyperV)newVirtualization;
				HyperV oldHyperV = (HyperV)oldVirtualization;
				if(newHyperV.getHostName().compareToIgnoreCase(oldHyperV.getHostName())!=0) {
					result = true;
				}
			}

		}
		
		else if(newVirtualizationType==VirtualizationType.VMwareESX){
			if(oldViVirtualizationType==VirtualizationType.HyperV){
				result=true;
			}
			else if(oldViVirtualizationType==VirtualizationType.VMwareESX){
				VMwareESX oldEsx = (VMwareESX)oldVirtualization;
				VMwareESX newEsx = (VMwareESX)newVirtualization;
				if(oldEsx.getEsxName().compareToIgnoreCase(newEsx.getEsxName())!=0) {
					result = true;
				}
				
			}
			else if(oldViVirtualizationType==VirtualizationType.VMwareVirtualCenter){
				VMwareVirtualCenter oldVC = (VMwareVirtualCenter)oldVirtualization;
				VMwareESX newEsx = (VMwareESX)newVirtualization;
				if(oldVC.getEsxName().compareToIgnoreCase(newEsx.getHostName())!=0) {
					result = true;
				}
				
			}
			
		}
		
		else if(newVirtualizationType==VirtualizationType.VMwareVirtualCenter){
			if(oldViVirtualizationType==VirtualizationType.HyperV){
				result=true;
			}
			else if(oldViVirtualizationType==VirtualizationType.VMwareVirtualCenter){
				VMwareVirtualCenter oldVC = (VMwareVirtualCenter)oldVirtualization;
				VMwareVirtualCenter newVC = (VMwareVirtualCenter)newVirtualization;
				if(oldVC.getEsxName().compareToIgnoreCase(newVC.getEsxName())!=0) {
					result = true;
				}
			}
			else if(oldViVirtualizationType==VirtualizationType.VMwareESX){
				VMwareESX oldEsx = (VMwareESX)oldVirtualization;
				VMwareVirtualCenter newVC = (VMwareVirtualCenter)newVirtualization;
				if(newVC.getEsxName().compareToIgnoreCase(oldEsx.getHostName())!=0) {
					result = true;
				}
				
			}
			
		}
		
		return result;		
	}
    
	/**
	 * Hyper-v Microsoft has a limitation that the VHD file cannot grow beyond 2TB.<br>
	 * If the node is not imported by HBBU, check local machine's disk size else check VM's disk size
	 * 
	 * @param instanceUuid
	 *            the source vm instance uuid
	 * @param vmwareOBJ
	 *            connection to ESX/VC
	 * @param warningList
	 *            Hold the warning information
	 * @throws ServiceException
	 *             if check failed, throw this exception
	 */
	public static void checkDiskSize(String instanceUuid, CAVirtualInfrastructureManager vmwareOBJ,
			List<VCMSavePolicyWarning> warningList) throws ServiceException {
		// Check whether the disk is large then 2TB
		if (!HACommon.isTargetPhysicalMachine(instanceUuid)) {
//			if (vmwareOBJ != null) {
//				VMItem[] vmItems = VSphereService.getInstance().getConfiguredVM();
//				for (VMItem vmItem : vmItems) {
//					String tmpInstanceUUID = vmItem.getVmInstanceUUID();
//					if (tmpInstanceUUID.equals(instanceUuid)) {
//						try {
//							ArrayList<Disk_Info> vmDisks = vmwareOBJ.getCurrentDiskInfo(vmItem.getVmName(),
//									instanceUuid);
//							for (Disk_Info disk : vmDisks) {
//								if (disk.getSizeinKB() > HACommon.VIRTUAL_DISK_MAX_SIZE_2024_GB / 1024) {
//									throw new ServiceException(FlashServiceErrorCode.VCM_DISK_LARGER_THAN2TB_HYPERV,
//											new Object[] {});
//								}
//							}
//							break;
//						} catch (Exception e) {
//							logger.error("Exception in getCurrentDiskInfo", e);
//							if (warningList != null) {
//								VCMSavePolicyWarning savePolicyWarning = new VCMSavePolicyWarning(
//										FlashServiceErrorCode.VCM_DISK_SIZE_CHECK_FAILED, new String[] {});
//								warningList.add(savePolicyWarning);
//							}
//						}
//					}
//				}
//			}
		} else {
			try {
				if (HAService.getInstance().getNativeFacade().CheckIfExistDiskLargerThan2T()) {
					logger.error("The disk is larger than 2T, so failed the policy deploy process.");
					if(vmwareOBJ == null){
						throw new ServiceException(FlashServiceErrorCode.VCM_DISK_LARGER_THAN2TB_HYPERV, new Object[] {});
					}else{
						throw new ServiceException(FlashServiceErrorCode.VCM_DISK_LARGER_THAN2TB_ESX, new Object[] {});
					}
				}
			} catch (Exception e) {
				if (e instanceof ServiceException) {
					throw (ServiceException) e;
				}
				logger.error("Check whether the disk larger than 2T failed, we will continue deploy policy.", e);
				if (warningList != null) {
					VCMSavePolicyWarning savePolicyWarning = new VCMSavePolicyWarning(
							FlashServiceErrorCode.VCM_DISK_SIZE_CHECK_FAILED,
							new String[] { getCurrentMachineHostName() });
					warningList.add(savePolicyWarning);
				}
			}
		}
	}

	public static void getBackupVMD2DBackupInfo(String instanceUuid, VCMD2DBackupInfo vcmBackupInfo) throws ServiceException{
		if(!HACommon.isTargetPhysicalMachine(instanceUuid)) {
			
			try {
				VirtualMachine vm = new VirtualMachine();
				vm.setVmInstanceUUID(instanceUuid);
				VMBackupConfiguration vmConfig = VSphereService.getInstance().getVMBackupConfiguration(vm);
				if(vmConfig == null) {
					logger.error("Failed to get the VMBackupConfiguration object:InstaceUuid "+instanceUuid);
					return;
				}
				
				BackupVM  backupVM = vmConfig.getBackupVM();
				if(backupVM == null) {
					logger.error("Failed to get the BackupVM object:InstaceUuid "+instanceUuid);
					return;
				}
				
				boolean isMPSConversion = ManualConversionUtility.isVSBWithoutHASupport(vmConfig);
				if (isMPSConversion) {
					BackupDestinationInfo backupDestinationInfo = new BackupDestinationInfo();
					backupDestinationInfo.setBackupDestination(backupVM.getDestination());
					ADRConfigure adrConfigInfo = null;
					try {
						adrConfigInfo = HACommon.getADRConfiguration(instanceUuid, backupDestinationInfo);
					} catch (Exception e) {
						logger.error("Failed to get any ADRConfigure.xml from " + backupVM.getDestination());
						return;
					}
					if (adrConfigInfo == null) {
						logger.error("No ADRConfigure.xml in " + backupVM.getDestination());
						return;
					}
					
					vcmBackupInfo.setOsVersion(adrConfigInfo.getOSVersion());
					// backupInfo.setX86(vmInfo.);
					vcmBackupInfo.setUEFI(adrConfigInfo.isUEFI());
				} else {
					CAVirtualInfrastructureManager vsphereVMOBJ = null;
					try{
						vsphereVMOBJ = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(
								backupVM.getEsxServerName(), backupVM.getEsxUsername(), backupVM.getEsxPassword(), backupVM.getProtocol(), true, backupVM.getPort());
						VM_Info vmInfo = vsphereVMOBJ.getVMInfo(backupVM.getVmName(), backupVM.getInstanceUUID());
						vcmBackupInfo.setOsVersion(vmInfo.getVmVersion());
						// backupInfo.setX86(vmInfo.);
						vcmBackupInfo.setUEFI(vmInfo.isUEFI());
			
					} catch (Exception e) {
						logger.error(e);						
						try {
							if (vsphereVMOBJ != null) {
								vsphereVMOBJ.close();
							}
						} catch (Exception ex){}
					}
				}
				
			} catch (Exception e) {
				logger.error("Failed to create the vmwareOBJ for esxHost", e);
			}
		}
	}
}
