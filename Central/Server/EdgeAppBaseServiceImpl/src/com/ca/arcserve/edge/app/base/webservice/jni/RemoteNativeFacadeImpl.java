package com.ca.arcserve.edge.app.base.webservice.jni;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.model.ESXServerInfo;
import com.ca.arcflash.webservice.AxisFault;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.common.VSphereLicenseCheck;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.jni.WSJNIException;
import com.ca.arcflash.webservice.jni.model.JHypervInfo;
import com.ca.arcflash.webservice.jni.model.JHypervPFCDataConsistencyStatus;
import com.ca.arcflash.webservice.jni.model.JHypervResult;
import com.ca.arcflash.webservice.jni.model.JHypervVMInfo;
import com.ca.arcflash.webservice.jni.model.JRWLong;
import com.ca.arcflash.webservice.jni.model.JWindowsServiceModel;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.jni.BaseWSJNI;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ServiceState;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicenseMachineType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AdminAccountValidationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HypervProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.webservice.jni.model.DomainUser;
import com.ca.arcserve.edge.webservice.jni.model.JNode;
import com.ca.ha.webservice.jni.HyperVException;
import com.ca.ha.webservice.jni.HyperVJNI;
import com.ca.ha.webservice.jni.JHyperVSystemInfo;

public class RemoteNativeFacadeImpl implements IRemoteNativeFacade {
	
	private static Logger logger = Logger.getLogger(RemoteNativeFacadeImpl.class);
	private static com.ca.arcflash.webservice.jni.NativeFacade d2dNativeFacade =
			new com.ca.arcflash.webservice.jni.NativeFacadeImpl();
	
	private static RemoteNativeFacadeImpl instance = new RemoteNativeFacadeImpl();
	
	private RemoteNativeFacadeImpl()
	{
	}
	
	public static RemoteNativeFacadeImpl getInstance()
	{
		return instance;
	}

	@Override
	public AdminAccountValidationResult validateAdminAccount(
			String computerName, String userName, String password)
			throws EdgeServiceFault {
		return WSJNI.validateAdminAccount(computerName, userName, password);
	}
	@Override
	public AdminAccountValidationResult verifyHyperVAdminAccount(String computerName, String userName, String password, boolean isCluster) throws EdgeServiceFault {
		return WSJNI.verifyHyperVAdminAccount(computerName, userName, password, isCluster);
	}
	@Override
	public RemoteNodeInfo scanRemoteNode(String edgeUser, String edgeDomain,
			String edgePassword, String computerName, String userName,
			String password) throws EdgeServiceFault {
		return WSJNI.scanRemoteNode(edgeUser, edgeDomain, edgePassword, computerName, userName, password);
	}

	@Override
	public void verifyADAccount(String adServerName, String adUser,
			String adPassword) throws EdgeServiceFault {
		WSJNI.verifyActiveDirectoryAccount(adServerName, adUser, adPassword);
	}

	@Override
	public List<JNode> getNodes(String adServerName, String adUser,
			String adPassword, String computerName, String computerOS,
			boolean bSQL, boolean bExch) throws EdgeServiceFault {
		return WSJNI.browseNodes(adServerName, adUser, adPassword, computerName, computerOS, bSQL, bExch);
	}

	@Override
	public LicenseMachineType getLicenseMachineType(String server,
			String username, String password) throws EdgeServiceFault {
		int[] machineType = new int[1];
    	int result;
    	
    	try {
			result = BaseWSJNI.getMachineTypeForLicensing(server, username, password, machineType);
		} catch (Throwable e) {
			logger.error("get license machine type failed.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
		}
    	
    	if (result != 0) {
			logger.error("get license machine type failed, error code = " + result);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, String.valueOf(result));
		}
    	
    	logger.debug("get license machine type succeed, return value = " + machineType[0]);
    	LicenseMachineType type = LicenseMachineType.parseNative(machineType[0]);
    	if (type == null) {
			logger.error("Undefined license machine type " + machineType[0]);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, String.valueOf(machineType[0]));
		}
    	
    	return type;
	}

	@Override
	public List<JHypervVMInfo> GetVmList(String serverName, String user, String password, boolean onlyUnderThisHyperv) throws EdgeServiceFault {
		try {
			return com.ca.arcflash.webservice.jni.WSJNI.GetVmList(serverName, user, password, onlyUnderThisHyperv);
		} catch (ServiceException e) {
			logger.debug("Failed to get VMs from Hyper-V server [" + serverName + "], error code = " + e.getErrorCode() + ", error message = " + e.getMessage());
			
			switch (e.getErrorCode()) {
			case "100": throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_HYPERV_CONNECT_ERR, e.getMessage());
			case "200": throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_HYPERV_GETVMLIST_ERR, e.getMessage());
			default: throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
			}
		}
	}
	
	@Override
	public GetHyperVProtectionTypeResult getHyperVProtectionType(
		String serverName, String user, String password )
	{
		JRWLong type = new JRWLong();
		JRWLong errorCode = new JRWLong();
		JHypervResult hypervResult = new JHypervResult();
		com.ca.arcflash.webservice.jni.WSJNI.getHyperVProtectionTypes( serverName, user, password, type, errorCode, hypervResult);
		
		GetHyperVProtectionTypeResult result = new GetHyperVProtectionTypeResult();
		result.setErrorCode( errorCode.getValue() );
		result.setProtectionType( HypervProtectionType.parse( (int) type.getValue() ) );
		if(hypervResult.getErrorHyperv() != null)
			result.setAdditionInfo(hypervResult.getErrorHyperv());
		
		return result;
	}
	
	@Override
	public JHypervPFCDataConsistencyStatus getHypervPFCDataConsistentStatus(String hostName, String userName, String password,
			String vmGuid, String vmUserName, String vmPassword) {
		long handle = 0;
		
		try {
			handle = d2dNativeFacade.OpenHypervHandle(hostName, userName, password);
			return com.ca.arcflash.webservice.jni.WSJNI.getHypervPFCDataConsistentStatus(vmGuid, vmUserName, vmPassword, handle);
		} catch (Exception e) {
			return null;
		} finally {
			try {
				d2dNativeFacade.CloseHypervHandle(handle);
			} catch (ServiceException e) {
				logger.warn("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}

	@Override
	public void testConnection(String host, String user, String password) throws EdgeServiceFault {
		long handle = 0;
		
		try {
			handle = d2dNativeFacade.OpenHypervHandle(host, user, password);
		} catch (ServiceException e) {
			logger.error("Failed to connect to hyper-V server [" + host + "], error message = " + e.getMessage());
			
			String logMessage = EdgeCMWebServiceMessages.getResource("autoDiscovery_hyperV_ConnectFail_InvalidLoginException");
			logger.error(logMessage, e);
			
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_HYPERV_ConnectFail_UserNotEnoghtPrevilege, logMessage);
		} finally {
			if (handle != 0) {
				try {
					d2dNativeFacade.CloseHypervHandle(handle);
				} catch (ServiceException e) {
					logger.warn("Failed to close hyperv manager handle, error messagte = " + e.getMessage());
				}
			}
		}
	}
	
	@Override
	public void setClusterAccessHint(String serverName, String username, String password) {
		WSJNI.setClusterAccessHint(serverName, username, password);
	}
	@Override
	public String[] getClusterVirtualByPyhsicalNode( String serverName,
		String user, String password )
	{
		return WSJNI.getClusterVirtualByPyhsicalNode(serverName, user, password);
	}

	@Override
	public String getIpByHostName( String hostName ) throws UnknownHostException
	{
		if(hostName==null)
			return null;
		String ip = "";
		try {
			InetAddress addr = InetAddress.getByName(hostName);
		    ip = addr.getHostAddress();
		} catch (UnknownHostException e) {
			logger.error("getIpByName: " + hostName, e); //$NON-NLS-1$
			throw e;
		}
		return ip;
	}
	
	@Override
	public boolean isHostReachble(String hostName) throws Exception {
		try {
			InetAddress address = InetAddress.getByName(hostName);
			if(address.isReachable(3000))
				return true;
		} catch (Exception e) {
			//do nothing
		}
		return EdgeCommonUtil.isReachableByPing(hostName); //If address.isReachable failed, then use real ping command
	}
	
	@Override
	public int getHyperVCPUSocketCount( String serverName, String username,
		String password )
	{
		return VSphereLicenseCheck.getHyperVCPUSocketCount( serverName, username, password );
	}

	//Jan sprint
	@Override	
	public int validateNASServer(String nasServerName, String userName, String password, String port, String protocol){
		return WSJNI.validateNASServer(nasServerName, userName, password, port, protocol);
	}
	@Override	
	public boolean isHyperVRoleInstalledAndHostOS(){
		if(com.ca.arcflash.webservice.jni.WSJNI.IsHyperVRoleInstalled()){
			try {
				//to check if >= w2k8sp2, call HA_IsHostOSGreaterEqual(6, 0, 2, 0)
				return d2dNativeFacade.HAIsHostOSGreaterEqual(6, 0, (short)2, (short)0);
			} catch (ServiceException e) {
				logger.error(e);
				return true;  //code from HAService.java
			}
			}else{
				return false;
		}
	}
	
//	public boolean isHostOSGreaterEqualW2K8SP2(){
//		//to check if >= w2k8sp2, call HA_IsHostOSGreaterEqual(6, 0, 2, 0)
//		try {
//			return d2dNativeFacade.HAIsHostOSGreaterEqual(6, 0, (short)2, (short)0);
//		} catch (ServiceException e) {
//			logger.error(e);
//			return true;  //code from HAService.java
//		}
//	}
	@Override	
	public String[] getHypervNetworksFromMonitor(String host, String username,
			String password){
			
		long handle = 0;
		try
		{
			handle = HyperVJNI.OpenHypervHandle(host, username, password);
			Map<String, String> networks = HyperVJNI.GetVirutalNetworkList(handle);
			Collection<String> values = networks.values();
			String[] results = values.toArray(new String[0]);
			return results;
	
		}catch (HyperVException e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault(e.getMessage(),FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		finally{
			try {
				HyperVJNI.CloseHypervHandle(handle);
			} catch (HyperVException e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}
	

//	@Override	
//	public String[] getHypervNetworkAdapterTypes(){
//		String[] types = new String[]{HYPERV_NETWORK_ADAPTER,HYPERV_LEGACY_NETWORK_ADAPTER};
//		return types;
//	}
	
	@Override	
	public ESXServerInfo getHypervInfo(String host, String user,String password){
		long handle = 0;
		try {
			handle = d2dNativeFacade.OpenHypervHandle(host, user, password);
//			int cpuCount = d2dNativeFacade.GetMaxCPUSForHypervVm(handle);
//			long memorySize = d2dNativeFacade.GetMaxRAMForHypervVm(handle);
//			ESXServerInfo info = new ESXServerInfo();
//			info.setCpuCount(cpuCount);
//			info.setMemorySize(memorySize);
//			return info;
            JHyperVSystemInfo hyperVSysInfo = new JHyperVSystemInfo();
            d2dNativeFacade.GetHyperVSystemInfo(handle,hyperVSysInfo);
            
            ESXServerInfo info = new ESXServerInfo();
            info.setCpuCount(hyperVSysInfo.getCpuCount());
            info.setMemorySize(hyperVSysInfo.getTotalPhysicalMemory());
            info.setAvailableMemorySize(hyperVSysInfo.getAvailablePhysicalMemory());
            return info;

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw AxisFault.fromAxisFault(e.getMessage(),FlashServiceErrorCode.Common_ErrorOccursInService);
		}finally{
			try {
				d2dNativeFacade.CloseHypervHandle(handle);
			} catch (ServiceException e) {
				logger.error("Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}
	
	@Override
	public JHypervVMInfo getHypervVMInfo(String hostName, String userName,
			String password, String vmInstanceUUID) {
		long handle = 0;
		JHypervVMInfo vmInfo = null;
		com.ca.arcflash.webservice.jni.NativeFacade d2dNativeFacade = new com.ca.arcflash.webservice.jni.NativeFacadeImpl();
		try{
			handle = d2dNativeFacade.OpenHypervHandle(hostName, userName, password);
			vmInfo = com.ca.arcflash.webservice.jni.WSJNI.getHypervVMInfo(handle,vmInstanceUUID);
			return vmInfo;
		} catch (Exception e) {
			logger.error("[getHypervVMInfo] failed.",e);
			throw AxisFault.fromAxisFault(e.getMessage(),FlashServiceErrorCode.Common_ErrorOccursInService);
		} finally {
			try {
				d2dNativeFacade.CloseHypervHandle(handle);
			} catch (ServiceException e) {
				logger.error("[getHypervVMInfo]:Failed to close hyperv manager handle." + e.getMessage());
			}
		}
	}
	
	@Override
	public ServiceState checkServiceIsRunning(String hostName, String serviceName,
		String userName, String password){
		final int serviceNotExist = 1168;
		try {
			JWindowsServiceModel model = new JWindowsServiceModel();
			int ret = d2dNativeFacade.checkServiceState(hostName, serviceName, userName, password, model);
			if(ret == 0){
				if(model.isExist()){
					if(model.isStarted() && model.getState().equalsIgnoreCase("running"))
						return ServiceState.Running; //running
					else
						return ServiceState.NotRunning; //Not running
				}
			}else if(ret==serviceNotExist){
				return ServiceState.NotExist;
			}else {
				return ServiceState.UnKnown;
			}
		} catch (Exception e) {
			logger.error("[RemoteNativeFacadeImpl] "
					+ "checkServiceIsRunning() for the service: "+serviceName +" of node: "+hostName +"failed.",e); 
		}
		return ServiceState.UnKnown;
	}
	
	@Override
	public List<DomainUser> getAllDomainUsers( String domainName,
		String dcName, String userName, String password, Date changeTime ) throws Exception
	{
		List<DomainUser> userList = new ArrayList<>();
		long hResult = WSJNI.getAllDomainUsers( domainName, dcName, userName, password, changeTime, userList );
		if (hResult != 0) // S_OK
			throw new Exception( "Native API returns error. hResult: " + hResult );
		return userList;
	}

	@Override
	public List<JHypervInfo> getHypervList(String serverName, String user, String password) throws EdgeServiceFault {
			int ret = 0;
			logger.info("[IVM] start GetHypervList(). serverName: " + serverName + " user: "+user);
			ArrayList<JHypervInfo> result = new ArrayList<JHypervInfo>();
			try {
				ret = com.ca.arcflash.webservice.jni.WSJNI.GetHypervInfoList(serverName, user, password, result);
			} catch (Exception e) {
				logger.debug("Failed to GetHypervList [" + serverName + "]", e);
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
			}
			
			logger.info("[IVM] GetHypervList() return: " + ret);
			if(ret == 0){
				return result;
			}else if(ret == 1){
				logger.error("Failed to GetHypervList [" + serverName + "] result 1");
				String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_HYPERV_CREDENTIAL, serverName, serverName);
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_HYPERV_CREDENTIAL, new Object[] { serverName, serverName }, errorMessage);
			}else if(ret == 2){
				logger.error("Failed to GetHypervList [" + serverName + "] result 2");
				String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_HYPERV_SERVICE, serverName);
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_HYPERV_SERVICE, new Object[] { serverName }, errorMessage);
			}else if(ret == 3){
				logger.error("Failed to GetHypervList [" + serverName + "] result 3");
				String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_HYPERV_FIREWALL, serverName);
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_HYPERV_FIREWALL, new Object[] { serverName }, errorMessage);
			}else if(ret == 4){
				logger.error("Failed to GetHypervList [" + serverName + "] result 4");
				String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_HYPERV_OTHER_ERROR, serverName);
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_HYPERV_OTHER_ERROR, new Object[] { serverName }, errorMessage);
			}else{
				return result;
			}
		
	}

	@Override
	public List<String> getFqdnNamebyHostNameOrIp(String hostnameOrIp)
			throws Exception {
		if(StringUtil.isEmptyOrNull(hostnameOrIp))
			return null;
		List<String> fqdnNameList = new ArrayList<String>();
		try {
			InetAddress addr = InetAddress.getByName(hostnameOrIp);
			String hostnameCanonical = addr.getCanonicalHostName();
			if (hostnameCanonical != null
					&& hostnameCanonical.indexOf('.') > 0) {
				fqdnNameList.add(hostnameCanonical);
			}

		} catch (Exception e) {
			// do nothing
		}
		return fqdnNameList;
	}

	@Override
	public long testConnection(ArchiveCloudDestInfo cloudDestInfo)
			throws ServiceException {
		
		long connectionStatus = 0L;
		try {
			connectionStatus = com.ca.arcflash.webservice.jni.WSJNI.testConnection(cloudDestInfo);
			logger.info("error code in testConnection( )" + connectionStatus);
		} catch (WSJNIException se) {
			logger.error(se.getMessage(), se);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} catch (Exception t) {
			logger.error(t.getMessage(), t);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return connectionStatus;
	}
}
