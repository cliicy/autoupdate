package com.ca.arcflash.webservice.common;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareVirtualInfrastructureManager;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.ha.vmwaremanager.VMwareServerType;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.license.LICENSEDSTATUS;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcflash.webservice.util.WebServiceMessages;
import com.ca.ha.webservice.jni.HyperVJNI;

public class VSphereLicenseCheck extends LicenseCheck<VSphereLicenseCheck.HyperVisorInfo>{
	private static final VSphereLicenseCheck instance = new VSphereLicenseCheck();

	protected static final String licFileName = "vSphereLicense.lic";
	
	public static class HyperVisorInfo{
		private String hostname;
		private int cpuSockets;
		private int hyperVisorType;
		private String protectedVMName;
		private String protectedVmInstanceUuid;
		private boolean vmwareEssentialsLicense;
		public String getHostname() {
			return hostname;
		}
		public void setHostname(String hostname) {
			this.hostname = hostname;
		}
		public int getCpuSockets() {
			return cpuSockets;
		}
		public void setCpuSockets(int cpuSockets) {
			this.cpuSockets = cpuSockets;
		}
		public int getHyperVisorType() {
			return hyperVisorType;
		}
		public void setHyperVisorType(int hyperVisorType) {
			this.hyperVisorType = hyperVisorType;
		}
		public String getProtectedVMName() {
			return protectedVMName;
		}
		public void setProtectedVMName(String protectedVMName) {
			this.protectedVMName = protectedVMName;
		}
		public boolean isVmwareEssentialsLicense() {
			return vmwareEssentialsLicense;
		}
		public void setVmwareEssentialsLicense(boolean vmwareEssentialsLicense) {
			this.vmwareEssentialsLicense = vmwareEssentialsLicense;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + cpuSockets;
			result = prime * result
					+ ((hostname == null) ? 0 : hostname.hashCode());
			result = prime * result + hyperVisorType;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			HyperVisorInfo other = (HyperVisorInfo) obj;
			if (cpuSockets != other.cpuSockets)
				return false;
			if (hostname == null) {
				if (other.hostname != null)
					return false;
			} else if (!hostname.equals(other.hostname))
				return false;
			if (hyperVisorType != other.hyperVisorType)
				return false;
			return true;
		}
		public String getProtectedVmInstanceUuid() {
			return protectedVmInstanceUuid;
		}
		public void setProtectedVmInstanceUuid(String protectedVmInstanceUuid) {
			this.protectedVmInstanceUuid = protectedVmInstanceUuid;
		}
	}

	private VSphereLicenseCheck() {
		super(licFileName);
	}

	public static VSphereLicenseCheck getInstance() {
		return instance;
	}

	@Override
	protected String getCacheKey(VSphereLicenseCheck.HyperVisorInfo subject) {
		return String.valueOf(subject.hashCode());
	}

	@Override
	protected LICENSEDSTATUS getLicenseFromEdge(HyperVisorInfo hyperVisorInfo)
			throws LicenseCheckException {
		EdgeRegInfo info = null;
		try {
			D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();

			info = edgeRegInfo.getEdgeRegInfo(ApplicationType.vShpereManager);

			if(info == null)
				return LICENSEDSTATUS.VALID;
		}
		catch(Exception ex) {
			logger.error("Unexpected exception occurs. Fail to get Fetch license from Edge:" + ex.getMessage(), ex);
			return null;
		}
		
		IEdgeCM4D2D edgeService = null;
		try {
			edgeService = WebServiceFactory.getEdgeService(info.getEdgeWSDL(),IEdgeCM4D2D.class);
			edgeService.validateUserByUUID(info.getEdgeUUID());
		}
		catch(Exception ex) {
			logger.error("Unexpected exception occurs when connecting to Edge. " +
					"Fail to get Fetch license from Edge:" + ex.getMessage(), ex);
			throw new LicenseCheckException( info.getEdgeHostName(), LicenseCheckException.FAIL_CONNECT_EDGE);
		}
		
		try {
			LICENSEDSTATUS status = LICENSEDSTATUS.INVALID;
			MachineInfo machineInfo = new MachineInfo();
			machineInfo.setHostName(hyperVisorInfo.getProtectedVMName());
			machineInfo.setHostUuid(hyperVisorInfo.getProtectedVmInstanceUuid());
			machineInfo.setServerName(hyperVisorInfo.getHostname());
			machineInfo.setSocketCount(hyperVisorInfo.getCpuSockets());	// should change to vm socket, but current not used for HBBU license
			machineInfo.setServerSocketCount(hyperVisorInfo.getCpuSockets());
			
			logger.info("MachineInfo:"+StringUtil.convertObject2String(machineInfo));
			long required_feature = LicenseDef.SUBLIC_BLI | LicenseDef.SUBLIC_OS_HYPERV;
			
			if (hyperVisorInfo.getHyperVisorType() == BackupVM.Type.VMware.ordinal()
					&& hyperVisorInfo.isVmwareEssentialsLicense()){
				logger.info("VMware's license is Essentials");
				required_feature = required_feature | LicenseDef.SUBLIC_VMWare_Essential;
			}
			
			LicenseCheckResult result = edgeService.checkLicense(LicenseDef.UDP_CLIENT_TYPE.UDP_CLIENT_HBBU, machineInfo, required_feature);
			logger.info("License Result:"+StringUtil.convertObject2String(result));
			if (result!=null && result.getLicense()!=null){
				logger.info("valid license return from Edge");
				status = LICENSEDSTATUS.VALID;
			}
			
			return status;
		} 
		catch(Exception ex) {
			logger.error("Internal error occurs in Edge side. Fail to get Fetch license from Edge:" 
					+ ex.getMessage(), ex);
			throw new LicenseCheckException("Internal error occurs in Edge side", LicenseCheckException.EDGE_INTERNAL_ERROR);
		}

	}

	public static String getNormalizedESXHostName(VMBackupConfiguration vmBackup) throws ServiceException{
		return getNormalizedESXHostName(vmBackup, null);		
	}
	
	protected static String getNormalizedESXHostName(VMBackupConfiguration vmBackup, CAVMwareVirtualInfrastructureManager vmwareManager) throws ServiceException
	{
		if (vmBackup == null) {
			logger.error("The VM backup configuration is null.");
			return null;
		}
		if(vmBackup.getBackupVM() == null) {
			logger.error("Invalid VM backup configuration, the VM info is null.");
			return null;
		}
		
		String esxHostName = "";
		BackupVM backupVM = vmBackup.getBackupVM();

		if(backupVM.getVmType() == BackupVM.Type.VMware_VApp.ordinal()) {
			VirtualCenter[] vc = backupVM.getVAppVCInfos();
			if(vc != null && vc.length > 0) {
				esxHostName = vc[0].getVcName();
			} else {
				logger.error("The VCenter name in vApp VM backup configuration is null or empty.");
				return null;
			}
		} else {
			esxHostName = backupVM.getEsxServerName();
		}
		
		if (StringUtil.isEmptyOrNull(esxHostName)) {
			logger.error("The ESX host name in VM backup configuration is null or empty.");
		}
		
		StringBuilder conStatus = new StringBuilder();
		String result = getESXHostName(vmBackup, conStatus, vmwareManager);
		if(StringUtil.isEmptyOrNull(result)){
			if(backupVM.getVmType() == BackupVM.Type.VMware_VApp.ordinal()) {
				return result;
			}
			logger.error("The ESX host name in getESXHostName is null or empty. The value in VM backup configuration is: " + esxHostName);
			if (conStatus.toString().equals("ConnectionFailed")) {
				//Failed to connect to VC/ESX
				throw new ServiceException("00000001", new Object[0]);
			}
			else {
				//Failed to detect VM in VC/ESX
				throw new ServiceException("00000000", new Object[0]);
			}
		}
		
		return result;
		
	}
	
	
	protected HyperVisorInfo getHyperVisorInfo(VMBackupConfiguration vmBackup) throws ServiceException
	{
		HyperVisorInfo result = new HyperVisorInfo();
		result.setHyperVisorType(vmBackup.getBackupVM().getVmType());
		result.setProtectedVMName(vmBackup.getBackupVM().getVmName());
		result.setProtectedVmInstanceUuid(vmBackup.getBackupVM().getInstanceUUID());
		
		logger.info("VMName = " + vmBackup.getBackupVM().getVmName() + " VMUuid = " + vmBackup.getBackupVM().getInstanceUUID());
		
		// VMware
		if (vmBackup.getBackupVM().getVmType() == BackupVM.Type.VMware.ordinal() || vmBackup.getBackupVM().getVmType() == BackupVM.Type.VMware_VApp.ordinal())
		{
			CAVMwareVirtualInfrastructureManager vmwareManager = null;
			try
			{
				vmwareManager = getVmwareManager(vmBackup); // connect to vSphere
			}
			catch(Exception e)
			{
				logger.error(e);
				//Failed to connect to VC/ESX
				throw new ServiceException("00000001", new Object[0]);		
			}
			
			try{
				
				String esxServerName = getNormalizedESXHostName(vmBackup, vmwareManager);				
				result.setHostname(HostNameUtil.getHostName(esxServerName));			
				result.setCpuSockets(getVMwareESXCPUSocketCount(vmwareManager, esxServerName));
				result.setVmwareEssentialsLicense(isVMwareEssentialLicense(vmwareManager, esxServerName));
			}
			catch(ServiceException e)
			{
				logger.error(e);
				throw e;				
			}
			catch(Exception e)
			{
				logger.error(e);
			}
			finally
			{
				if (vmwareManager != null)
				{
					try
					{
						vmwareManager.close();
					}
					catch (Exception e)
					{
						logger.error(e);
					}
				}
			}
		}
		// Hyper-V
		else
		{
			try
			{
				String hyperVServerName = WSJNI.AFGetHyperVPhysicalName(
						vmBackup.getBackupVM().getEsxServerName(), 
						vmBackup.getBackupVM().getEsxUsername(), 
						vmBackup.getBackupVM().getEsxPassword(),  
						vmBackup.getBackupVM().getInstanceUUID());
				
				if (hyperVServerName == null || hyperVServerName.isEmpty())
				{
					throw new ServiceException(FlashServiceErrorCode.Common_General_Message);
				}
				
				hyperVServerName = HostNameUtil.getHostName(hyperVServerName);
				result.setHostname(hyperVServerName);
				result.setCpuSockets(getHyperVCPUSocketCount(hyperVServerName, 
						vmBackup.getBackupVM().getEsxUsername(), 
						vmBackup.getBackupVM().getEsxPassword()));
			}
			catch(ServiceException e)
			{
				logger.error(e);
				throw e;				
			}
			catch(Exception e)
			{
				logger.error(e);
			}
		}
		
		return result;
	}
	
	private static int getHyperVCPUSocketCount(VMBackupConfiguration vmBackup) {
		return getHyperVCPUSocketCount(vmBackup.getBackupVM().getEsxServerName(), 
				vmBackup.getBackupVM().getEsxUsername(), 
				vmBackup.getBackupVM().getEsxPassword());
	}
	
	public static int getHyperVCPUSocketCount(String serverName, String username, String password) {
		long handle = 0;
		
		try{
			handle = HyperVJNI.OpenHypervHandle(serverName, username, password);
			return WSJNI.getHyperVCPUSocketCount(handle);
		} catch (Exception e) {
			logger.error(e);
		} finally{
			if (handle!=0)
				try{
					HyperVJNI.CloseHypervHandle(handle);
				}catch(Exception e){
					logger.error(e);
				}
		}
		
		return 0;
	}

	public static int getVMwareESXCPUSocketCount(VMBackupConfiguration vmBackup, String esxHost){
		BackupVM configVM = vmBackup.getBackupVM();
		String esxServerName = configVM.getEsxServerName();
		String protocol = configVM.getProtocol();
		int port = configVM.getPort();
		String esxUsername = configVM.getEsxUsername();
		String esxPassword = configVM.getEsxPassword();
		
		
		if(configVM.getVAppVCInfos() != null && configVM.getVAppVCInfos().length > 0) {
			esxServerName = configVM.getVAppVCInfos()[0].getVcName();
			protocol = configVM.getVAppVCInfos()[0].getProtocol();
			port = configVM.getVAppVCInfos()[0].getPort();
			esxUsername = configVM.getVAppVCInfos()[0].getUsername();
			esxPassword = configVM.getVAppVCInfos()[0].getPassword();
		}
		CAVMwareVirtualInfrastructureManager vmwareManager = null;
		try {
			vmwareManager = CAVMwareInfrastructureManagerFactory
				.getCAVMwareVirtualInfrastructureManager(esxServerName,esxUsername,esxPassword, protocol, true, port);
			return vmwareManager.getESXCPUSockets(esxHost);
		} catch (Exception e) {
			logger.error("Failed to connect to ESX/VC " + esxServerName + " to get ESX name. " +
					"Error:" + e.getMessage(), e);
		}finally {
			if(vmwareManager != null) {
				try {
					vmwareManager.close();
				} catch (Exception e) {
				}
			}
		}
		
		return 0;
	}

	private static String getESXHostName(VMBackupConfiguration vmBackup, StringBuilder conStatus, CAVMwareVirtualInfrastructureManager vmwareManager) {
		if(vmBackup == null || vmBackup.getBackupVM() == null)
			return null;
		BackupVM configVM = vmBackup.getBackupVM();
		String esxServerName = configVM.getEsxServerName();
		String protocol = configVM.getProtocol();
		int port = configVM.getPort();
		String esxUsername = configVM.getEsxUsername();
		String esxPassword = configVM.getEsxPassword();
		String result = null;
		boolean isVApp = false;
		
		if(configVM.getVAppVCInfos() != null && configVM.getVAppVCInfos().length > 0) {
			esxServerName = configVM.getVAppVCInfos()[0].getVcName();
			protocol = configVM.getVAppVCInfos()[0].getProtocol();
			port = configVM.getVAppVCInfos()[0].getPort();
			esxUsername = configVM.getVAppVCInfos()[0].getUsername();
			esxPassword = configVM.getVAppVCInfos()[0].getPassword();
			isVApp = true;
		}
		
		boolean needCloseConnection = true; // should close the connection or not
		
		try {
			
			if (vmwareManager == null)
			{
				vmwareManager = CAVMwareInfrastructureManagerFactory
					.getCAVMwareVirtualInfrastructureManager(esxServerName,esxUsername,esxPassword, protocol, true, port);
			}
			else
			{
				needCloseConnection = false;
			}
		
			//if(vmwareManager.getVMwareServerType() == VMwareServerType.virtualCenter){ // by Liang.Shu - return null if VM doesn't exist
				// if it is vCenter , get esx node name
				String vmName = configVM.getVmName();
				String vmUUID = configVM.getInstanceUUID();
				if(isVApp) {
					if(configVM.getVAppMemberVMs() != null && configVM.getVAppMemberVMs().length > 0) {
						vmName = configVM.getVAppMemberVMs()[0].getVmName();
						vmUUID = configVM.getVAppMemberVMs()[0].getInstanceUUID();
					} else {
						return null;
					}
				}
				VM_Info vmInfo = null;
				try {
					vmInfo = vmwareManager.getVMInfo(vmName, vmUUID);
					if(vmInfo != null){
						result = vmInfo.getvmEsxHost();
					}
					else{
						logger.warn("Failed to get vm info, uuid:" + vmUUID);
					}
				} catch (Exception e) {
					logger.warn("Failed to get esx node info for vm , uuid::" + vmUUID);
				}
			//}else{
				// if it is esx server , just return the server name;
				//result = esxServerName;
			//}
		} catch (Exception e) {
			logger.error("Failed to connect to ESX/VC " + esxServerName + " to get ESX name. " +
					"Error:" + e.getMessage(), e);
			conStatus.append("ConnectionFailed");
		}
		finally {
			if(vmwareManager != null && needCloseConnection) {
				try {
					vmwareManager.close();
				} catch (Exception e) {
				}
			}
		}
		return result;
	}
	
	public static VM_Info getVMDetails(VMBackupConfiguration vmBackup) {
		if(vmBackup == null || vmBackup.getBackupVM() == null)
			return null;
		
		String esxServerName = vmBackup.getBackupVM().getEsxServerName();
		String protocol = vmBackup.getBackupVM().getProtocol();
		int port = vmBackup.getBackupVM().getPort();
		String esxUsername = vmBackup.getBackupVM().getEsxUsername();
		String esxPassword = vmBackup.getBackupVM().getEsxPassword();
		
		VM_Info info = null;
		CAVMwareVirtualInfrastructureManager vmwareManager = null;
		try {
			vmwareManager = CAVMwareInfrastructureManagerFactory
				.getCAVMwareVirtualInfrastructureManager(esxServerName,esxUsername,esxPassword, protocol, true, port);
		
			String vmName = vmBackup.getBackupVM().getVmName();
			String vmUUID = vmBackup.getBackupVM().getInstanceUUID();
			try {
				info = vmwareManager.getVMInfo(vmName, vmUUID);
			} catch (Exception e) {
				logger.warn("Fail to get VMInfo for vmname:" + vmName + ", vmUID:" + vmUUID);
			}
		} catch (Exception e) {
			logger.error("Failed to connect to ESX/VC " + esxServerName + " to get ESX name. " +
					"Error:" + e.getMessage(), e);
		}
		finally {
			if(vmwareManager != null) {
				try {
					vmwareManager.close();
				} catch (Exception e) {
				}
			}
		}
		return info;
	}
	
	public static boolean isVMwareEssentialLicense(VMBackupConfiguration vmBackup, String esxHost){
		BackupVM configVM = vmBackup.getBackupVM();
		String esxServerName = vmBackup.getBackupVM().getEsxServerName();
		String protocol = vmBackup.getBackupVM().getProtocol();
		int port = vmBackup.getBackupVM().getPort();
		String esxUsername = vmBackup.getBackupVM().getEsxUsername();
		String esxPassword = vmBackup.getBackupVM().getEsxPassword();
		if(configVM.getVAppVCInfos() != null && configVM.getVAppVCInfos().length > 0) {
			esxServerName = configVM.getVAppVCInfos()[0].getVcName();
			protocol = configVM.getVAppVCInfos()[0].getProtocol();
			port = configVM.getVAppVCInfos()[0].getPort();
			esxUsername = configVM.getVAppVCInfos()[0].getUsername();
			esxPassword = configVM.getVAppVCInfos()[0].getPassword();
		}
		CAVMwareVirtualInfrastructureManager vmwareManager = null;
		try {
			vmwareManager = CAVMwareInfrastructureManagerFactory
				.getCAVMwareVirtualInfrastructureManager(esxServerName,esxUsername,esxPassword, protocol, true, port);
			return isVMwareEssentialLicense(vmwareManager, esxHost);
		} catch (Exception e) {
			logger.error("Failed to connect to ESX/VC " + esxServerName + " to get ESX name. " +
					"Error:" + e.getMessage(), e);
		}finally {
			if(vmwareManager != null) {
				try {
					vmwareManager.close();
				} catch (Exception e) {
				}
			}
		}
		
		return false;
	}
	
	public static boolean isVMwareEssentialLicense(CAVirtualInfrastructureManager vmwareManager, String esxHost) {
		try {
			String[] licenseTypes = vmwareManager.getESXLicenseType(esxHost);
			logger.debug(StringUtil.convertArray2String(licenseTypes));
			
			if (licenseTypes==null || licenseTypes.length==0)
				return false;
			
			String license = licenseTypes[0];
			logger.info("VMware License:"+license);
			
			return license.toLowerCase().contains("essentials");
		} catch (Exception e) {
			logger.error("Failed to get ESX license type.", e);
			return false;
		}
	}
	
	public class LicensePrepareThread extends Thread
	{
		public LicensePrepareThread(VMBackupConfiguration configuration)
		{
			this.configuration = configuration;
		}

		private VMBackupConfiguration configuration;
		private long errorCode = 0;
		private String[] errorParameters = null;
		private HyperVisorInfo hyperVisorInfo = null;
		
		@Override
		public void run()
		{
			// prepare license check info 
			long start = System.currentTimeMillis();
			logger.info("Prepare HyperVisor info for license check begin");
			
			try
			{
				hyperVisorInfo = VSphereLicenseCheck.getInstance().getHyperVisorInfo(configuration);

			}
			catch (ServiceException e)
			{
				String errorLogs = String.format("Failed to get the vm[%s,%s] in the esx host[%s]", 
						configuration.getBackupVM().getVmName(), 
						configuration.getBackupVM().getInstanceUUID(), 
						configuration.getBackupVM().getEsxServerName());
				logger.error(errorLogs);
				
				String activeLogs = null;
				if (configuration.getBackupVM().getVmType() == BackupVM.Type.VMware.ordinal())
				{
					if (e.getErrorCode().equals("00000000"))
					{
						activeLogs = String.format(WebServiceMessages.getResource("vsphereBackupJobLicenseFailedSearchVM"), configuration.getBackupVM().getVmName(), configuration.getBackupVM().getInstanceUUID(), configuration.getBackupVM().getEsxServerName());
					}
					else
					{
						activeLogs = String.format(WebServiceMessages.getResource("vsphereBackupJobLicenseFailedConnect"), configuration.getBackupVM().getEsxServerName());
					}
				}
				else
				{
					if (e.getErrorCode().equals("-2"))
					{
						activeLogs = String.format(WebServiceMessages.getResource("failGetVMFromHyperServer"), configuration.getBackupVM().getVmName(), configuration.getBackupVM().getInstanceUUID(), configuration.getBackupVM().getEsxServerName());
					}
					else
					{
						activeLogs = String.format(WebServiceMessages.getResource("failConnectHyperServerToGetName"), configuration.getBackupVM().getEsxServerName());
					}
				}

				errorParameters = new String[] { activeLogs, "", "", "", "" };
				errorCode = Constants.AFRES_AFJWBS_GENERAL;
			}
			
			String time = String.format(" (%.3f Seconds)", ((double)System.currentTimeMillis()-start)/1000);
			logger.info("Prepare HyperVisor info for license check end: errorCode = " + errorCode + time);
		}

		public long getErrorCode()
		{
			return errorCode;
		}

		public String[] getErrorParameters()
		{
			return errorParameters;
		}

		public HyperVisorInfo getHyperVisorInfo()
		{
			return hyperVisorInfo;
		}
		
	}
	
	public LicensePrepareThread startPrepare(VMBackupConfiguration configuration)
	{
		LicensePrepareThread result = new LicensePrepareThread(configuration);			
		result.start();		
		return result;
	}
	
	public boolean waitPrepare(LicensePrepareThread licensePrepareThread)
	{
		boolean result = true;
		
		if (licensePrepareThread != null)
		{
			while (true)
			{
				if (licensePrepareThread.isAlive())
				{
					try
					{
						Thread.sleep(3000);
					}
					catch (InterruptedException e)
					{
						result = false;
						break;
					}
				}
				else
				{
					break;
				}
			}			
		}
		
		logger.info("result = " + result);
		return result;		
	}
	
	protected CAVMwareVirtualInfrastructureManager getVmwareManager(VMBackupConfiguration vmBackup) throws Exception
	{
		CAVMwareVirtualInfrastructureManager result = null;
		logger.info("begin");
		
		if (vmBackup.getBackupVM().getVmType() == BackupVM.Type.VMware.ordinal() || vmBackup.getBackupVM().getVmType() == BackupVM.Type.VMware_VApp.ordinal())
		{
			String esxServerName = vmBackup.getBackupVM().getEsxServerName();
			String esxUsername = vmBackup.getBackupVM().getEsxUsername();
			String esxPassword = vmBackup.getBackupVM().getEsxPassword();
			String protocol = vmBackup.getBackupVM().getProtocol();
			int port = vmBackup.getBackupVM().getPort();
			
			if(vmBackup.getBackupVM().getVAppVCInfos() != null && vmBackup.getBackupVM().getVAppVCInfos().length > 0) 
			{
				esxServerName = vmBackup.getBackupVM().getVAppVCInfos()[0].getVcName();
				esxUsername = vmBackup.getBackupVM().getVAppVCInfos()[0].getUsername();
				esxPassword = vmBackup.getBackupVM().getVAppVCInfos()[0].getPassword();
				protocol = vmBackup.getBackupVM().getVAppVCInfos()[0].getProtocol();
				port = vmBackup.getBackupVM().getVAppVCInfos()[0].getPort();
			}
			
			result = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager( esxServerName, esxUsername, esxPassword, protocol, true, port);
		}
		logger.info("end");
		return result;
	}
	
	protected static int getVMwareESXCPUSocketCount(CAVirtualInfrastructureManager vmwareManager, String esxHost){
		try
		{
			return vmwareManager.getESXCPUSockets(esxHost);
		}
		catch (Exception e)
		{
			logger.error("Failed to getVMwareESXCPUSocketCount, esxHost = " + esxHost, e);
		}
		
		return 0;
	}
	
}
