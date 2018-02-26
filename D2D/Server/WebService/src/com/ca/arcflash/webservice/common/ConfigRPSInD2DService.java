package com.ca.arcflash.webservice.common;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jobscript.base.GenerateType;
import com.ca.arcflash.listener.manager.ListenerManager;
import com.ca.arcflash.rps.webservice.RPSWebServiceClientProxy;
import com.ca.arcflash.rps.webservice.RPSWebServiceFactory;
import com.ca.arcflash.rps.webservice.data.host.RegisterNodeInfo;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4D2D;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.listener.FlashListenerInfo;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.rps.RPSServiceProxyManager;
import com.ca.arcflash.webservice.service.rps.SettingsService;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class ConfigRPSInD2DService {
	private static final ConfigRPSInD2DService instance = new ConfigRPSInD2DService();
	private static final Logger logger = Logger
			.getLogger(ConfigRPSInD2DService.class);

	private ConfigRPSInD2DService() {
	}

	public static ConfigRPSInD2DService getInstance() {
		return instance;
	}	
	
	public void registryD2D2PPS(RpsHost rpsHost, String backupDestination) throws ServiceException{
		registryD2D2PPS(rpsHost, null, null, backupDestination, null, null);
	}
	
	public void registryD2D2PPS(RpsHost rpsHost, String vmInstanceUUID, 
			String vmName, String backupDestination, String vmUUID, String vmHostName) throws ServiceException {
		String newRpsHostName = rpsHost.getRhostname();
		int newRpsPort = rpsHost.getPort();
		String newRpsProtocol = rpsHost.isHttpProtocol()?"Http:":"Https:";
		try{
			boolean ret = ConfigRPSInD2DService.getInstance()
					.registerD2DToRPSServer(newRpsHostName, newRpsPort,
							newRpsProtocol, rpsHost.getUsername(),
							rpsHost.getPassword(), rpsHost.getUuid(),
							vmInstanceUUID, vmName, backupDestination, vmUUID,
							vmHostName);
			if(!ret){
				throw new ServiceException(WebServiceMessages.getResource("failedToRegisterToRPS"),
						FlashServiceErrorCode.BackupConfig_RPS_REGISTRY_TO_RPS_SERVER_FAILED);
			}
			
			// following code cause bad performance, we moved them to addVMToFlashListener()
			// and call it in saveVSphereBackupConfiguration()
//			FlashListenerInfo listener = new FlashListenerInfo();
//			listener.setWsdlURL(RPSServiceProxyManager.makeRPSServiceURL(newRpsHostName, 
//					newRpsProtocol, newRpsPort, RPSWebServiceFactory.wsdl4D2D));
//			listener.setType(FlashListenerInfo.ListenerType.RPS);
//			listener.setUuid(SettingsService.instance().getRpsServerUUID(newRpsHostName, 
//					rpsHost.getUsername(), 
//					rpsHost.getPassword(), newRpsProtocol, rpsHost.getPort()));
//			List<String> vms = this.getVMList4RPS(rpsHost.getRhostname());
//			String uuid = vmInstanceUUID;
//			if(StringUtil.isEmptyOrNull(uuid)){
//				uuid = CommonService.getInstance().getNodeUUID();
//			}
//			if(!vms.contains(uuid))
//				vms.add(uuid);
//			listener.getInstanceUuids().addAll(vms);
//			ListenerManager.getInstance().addFlashListener(listener);
		}catch(ServiceException e){
			throw e;
		}catch (Exception e){			
			throw new ServiceException(WebServiceMessages.getResource("failedToRegisterToRPSWithException"),
					FlashServiceErrorCode.BackupConfig_RPS_REGISTRY_TO_RPS_SERVER_FAILED);
		}
	}
	
	public void addVMToFlashListener(RpsHost rpsHost, List<BackupVM> backupVMs)
	{
		String newRpsHostName = rpsHost.getRhostname();
		int newRpsPort = rpsHost.getPort();
		String newRpsProtocol = rpsHost.isHttpProtocol()?"Http:":"Https:";
		
		FlashListenerInfo listener = new FlashListenerInfo();
		listener.setWsdlURL(RPSServiceProxyManager.makeRPSServiceURL(
						newRpsHostName, newRpsProtocol, newRpsPort,
						RPSWebServiceFactory.wsdl4D2D));
		listener.setType(FlashListenerInfo.ListenerType.RPS);
		listener.setUuid(SettingsService.instance().getRpsServerUUID(
						newRpsHostName, rpsHost.getUsername(),
						rpsHost.getPassword(), newRpsProtocol,
						rpsHost.getPort()));

		List<String> vms = this.getVMList4RPS(rpsHost.getRhostname());

		if (backupVMs == null)
		{
			String uuid = CommonService.getInstance().getNodeUUID();
			if(!vms.contains(uuid))
				vms.add(uuid);
		}
		else
		{
			for (BackupVM bkVm : backupVMs)
			{
				if (!vms.contains(bkVm.getInstanceUUID()))
					vms.add(bkVm.getInstanceUUID());
			}
		}

		listener.getInstanceUuids().addAll(vms);
		ListenerManager.getInstance().addFlashListener(listener);
	}
	
	
	private boolean registerD2DToRPSServer(String hostName, int port,
			String protocol, String userName, String password,
			String rpsLoginUUID, String vmInstanceUUID, String vmName,
			String backupDestination, String vmUUID, String vmHostName)
			throws ServiceException {
	
		IRPSService4D2D client = RPSServiceProxyManager.getRPSServiceClient(
				hostName, userName, password, port, protocol, rpsLoginUUID);
		//String loginUUID = CommonService.getInstance().getUUID();
		String loginUUID = CommonService.getInstance().getLoginUUID();
		protocol = CommonService.getInstance().getServerProtocol();
		if(protocol == null){
			logger.error("D2D server protocol is null");
			return false;
		}
		if(protocol.endsWith(":") == false)
		{
			protocol += ":";
		}
		
		RegisterNodeInfo nodeInfo = new RegisterNodeInfo();
		nodeInfo.setLoginUUID(loginUUID);
		nodeInfo.setClientUUID(CommonService.getInstance().getNodeUUID());
		try {
			nodeInfo.setNodeName(InetAddress.getLocalHost().getHostName());
//			nodeInfo.setNodeName(getRegNodeName());
		} catch (UnknownHostException e) {
			logger.error("Failed to get D2D host name", e);
		}
		nodeInfo.setPort(new Long(CommonService.getInstance().getServerPort()));
		nodeInfo.setProtocol(protocol);
		nodeInfo.setVmInstanceUUID(vmInstanceUUID);
		nodeInfo.setVmName(vmName);
		nodeInfo.setBackupDestination(backupDestination);
		nodeInfo.setVmUUID(vmUUID);
		nodeInfo.setVmHostName(vmHostName);
		nodeInfo.setIpList(CommonService.getInstance().getNativeFacade().getD2DIPList());
		if(StringUtil.isEmptyOrNull(vmInstanceUUID))
			nodeInfo.setD2dSID(CommonService.getInstance().getServerSID());
		try {
			long registerFlag = client.RegisterD2DToRPS(nodeInfo );
			if (registerFlag == 0) {
				return true;
			}
			return false;
		}catch(WebServiceException we){
			if(we instanceof SOAPFaultException){
				SOAPFaultException fault = (SOAPFaultException)we;
				throw new ServiceException(fault.getMessage(), fault.getFault().getFaultNode());			
			}
			logger.error("Failed to register D2D", we);
			return false;
		}
	}
	
	private String getRegNodeName() throws UnknownHostException{
		String nodeName = null;
		EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.CentralManagement);
		if(edgeRegInfo!=null){
			nodeName = edgeRegInfo.getRegHostName();
		}
		if(StringUtil.isEmptyOrNull(nodeName)){
			logger.warn("there is no reg host name in edge registration. use local host name as reg host name!");
			nodeName = InetAddress.getLocalHost().getHostName();
		}
		return nodeName;
	}
	
	public void callAssignPolicyToD2D(RpsHost rpsHost, String policyUUID) throws ServiceException{
		callAssignPolicyToD2D(rpsHost, null, policyUUID);
	}
	
	public void callAssignPolicyToD2D(RpsHost rpsHost, String vmInstanceUUID, String policyUUID) throws ServiceException{
		String newRpsHostName = rpsHost.getRhostname();
		int newRpsPort = rpsHost.getPort();
		String newRpsProtocol = rpsHost.isHttpProtocol() ? "Http:" : "Https:";
		try {
			boolean ret = ConfigRPSInD2DService.getInstance()
					.callAssignPolicyToD2D(newRpsHostName, newRpsPort,
							newRpsProtocol, policyUUID, rpsHost.getUsername(),
							rpsHost.getPassword(), rpsHost.getUuid(),
							vmInstanceUUID);
			if(!ret){
//				throw new ServiceException(WebServiceMessages.getResource("failedToAssignPolicyToD2D"),
//						FlashServiceErrorCode.BackupConfig_RPS_REGISTRY_TO_RPS_SERVER_FAILED);
				logger.error("Failed to assign rps policy to d2d, please check rps webservice log");
			}
		}catch(ServiceException e){
			throw e;
		}catch (Exception e){
			logger.error("fail to call callAssignPolicyToD2D", e);
			throw new ServiceException(WebServiceMessages.getResource("failedToAssignPolicyToD2DWithException"),
					FlashServiceErrorCode.BackupConfig_RPS_REGISTRY_TO_RPS_SERVER_FAILED);
		}
	}
	
	private boolean callAssignPolicyToD2D(String hostName, int port,
			String protocol, String policyId, String userName, String password,
			String rpsLoginUUID, String vmInstanceUUID) throws ServiceException {
		
//		nodeInfo.setPort(new Long(CommonService.getInstance().getServerPort()));
//		nodeInfo.setProtocol(protocol);
//		nodeInfo.setVmInstanceUUID(vmInstanceUUID);
//		nodeInfo.setVmName(vmName);
//		nodeInfo.setBackupDestination(backupDestination);
//		nodeInfo.setVmUUID(vmUUID);
//		nodeInfo.setVmHostName(vmHostName);
//		nodeInfo.setIpList(CommonService.getInstance().getNativeFacade().getD2DIPList());

		
		IRPSService4D2D client =  RPSServiceProxyManager.getRPSServiceClient(hostName, userName, 
				password, port, protocol, rpsLoginUUID);
		try {
			String uuid = !StringUtil.isEmptyOrNull(vmInstanceUUID) ? 
					vmInstanceUUID : CommonService.getInstance().getNodeUUID();  
			long registerFlag =  client.assignPolicyToD2D(
					uuid, policyId);
			if(registerFlag == 0){
				return true;
			}
		}catch(WebServiceException we) {
			if(we instanceof SOAPFaultException){
				SOAPFaultException fault = (SOAPFaultException)we;
				throw new ServiceException(fault.getMessage(), fault.getFault().getFaultNode());			
			}
			logger.error("Failed to assign policy to D2D", we);
		}
		return false;
	}
	
	public void unRegisterD2DToRPSServer(RpsHost rpsHost) {
		unRegisterD2DToRPSServer(rpsHost, null);
	}
	
	public void unRegisterD2DToRPSServer(RpsHost rpsHost, String vmInstanceUUID) {
		String newRpsHostName = rpsHost.getRhostname();
		int newRpsPort = rpsHost.getPort();
		String newRpsProtocol = rpsHost.isHttpProtocol()?"Http:":"Https:";
		try {
			unRegisterD2DToRPSServer(newRpsHostName, newRpsPort,
					newRpsProtocol, rpsHost.getUsername(),
					rpsHost.getPassword(), rpsHost.getUuid(), vmInstanceUUID);
			removeRPSListenerIfNeeded(newRpsHostName, newRpsProtocol, newRpsPort, 
					rpsHost.getUsername(), rpsHost.getPassword(), vmInstanceUUID);
		}catch (Exception e){
			logger.error("failed to unregistry D2D to RPS Server:"+newRpsHostName,e);
		}
	}
	
	private boolean isBackupToRPS(String rpsHostName, RpsHost host){
		if(host == null || StringUtil.isEmptyOrNull(rpsHostName))
			return false;
		if(rpsHostName.equalsIgnoreCase(host.getRhostname()))
			return true;
		else
			return false;
	}
	
	private void removeRPSListenerIfNeeded(String hostName, String protocol, int port, String userName, 
			String password, String vmInstanceUUID){
		//first check if no D2D/VM bakcup to this RPS server. then remove it
		List<String> vms = this.getVMList4RPS(hostName);

		
		FlashListenerInfo listener = new FlashListenerInfo();
		listener.setWsdlURL(RPSServiceProxyManager.makeRPSServiceURL(hostName, 
				protocol, port, RPSWebServiceFactory.wsdl4D2D));
		listener.setType(FlashListenerInfo.ListenerType.RPS);
		listener.setUuid(SettingsService.instance().getRpsServerUUID(hostName, 
				userName, 
				password, protocol, port));
		
		if(StringUtil.isEmptyOrNull(vmInstanceUUID))
			vmInstanceUUID = CommonService.getInstance().getNodeUUID();
		vms.remove(vmInstanceUUID);
		listener.getInstanceUuids().addAll(vms);
		if(vms.isEmpty())
			ListenerManager.getInstance().removeFlashListener(listener);
		else
			ListenerManager.getInstance().addFlashListener(listener);
	}
	
	private List<String> getVMList4RPS(String rpsHost) {
		List<String> vms = new ArrayList<String>();
		for(String vmInstanceUUID : VSphereService.getInstance().getAllConfiguratedVMs()){
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(vmInstanceUUID);
			try {
				VMBackupConfiguration vmConf = VSphereService.getInstance().getVMBackupConfiguration(vm);
				if (vmConf.getGenerateType() == GenerateType.MSPManualConversion) {
					continue;
				}
				if(vmConf != null && vmConf.getBackupRpsDestSetting() != null){
					if(isBackupToRPS(rpsHost, vmConf.getBackupRpsDestSetting().getRpsHost()))
						vms.add(vmInstanceUUID);
				}
			}catch(Throwable t){
				logger.error("Failed to get vm backup configuration", t);
			}
		}
		
		try {
			//check D2D
			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			if(configuration != null && configuration.getBackupRpsDestSetting() != null)
				if(isBackupToRPS(rpsHost, configuration.getBackupRpsDestSetting().getRpsHost()))
					vms.add(CommonService.getInstance().getNodeUUID());
			
		} catch(Throwable t) {
			logger.error("Failed to check the backup configuration for rps " + rpsHost, t);
		}
		return vms;
	}
	
	private boolean unRegisterD2DToRPSServer(String hostName, int port,
			String protocol, String userName, String password,
			String rpsLoginUUID, String vmInstanceUUID) throws ServiceException {
		try {
			IRPSService4D2D client = RPSServiceProxyManager
					.getRPSServiceClient(hostName, userName, password, port,
							protocol, rpsLoginUUID);
			RegisterNodeInfo nodeInfo = new RegisterNodeInfo();
			nodeInfo.setClientUUID(CommonService.getInstance().getNodeUUID());
			nodeInfo.setLoginUUID(CommonService.getInstance().getLoginUUID());
			nodeInfo.setNodeName(CommonService.getInstance()
					.getLocalHostAsTrust().getName());
			nodeInfo.setVmInstanceUUID(vmInstanceUUID);
			if (client != null) {
				return client.UnRegisterD2DFromRPS(nodeInfo) == 0;
			} else {
				return false;
			}
		} catch (WebServiceException we) {
			if (we instanceof SOAPFaultException) {
				SOAPFaultException fault = (SOAPFaultException) we;
				throw new ServiceException(fault.getMessage(), fault.getFault()
						.getFaultNode());
			}
			logger.error("Failed to assign policy to D2D", we);
		} catch (Throwable t) {
			logger.error("Failed to unregistry D2D");
		}
		return false;
	}
	
	
	public void updateD2DInfoToRPSIfNeed(){
		/*BackupConfiguration backupConfig = null;
		try {
			backupConfig = BackupService.getInstance().getBackupConfiguration();
			if(backupConfig !=null && !backupConfig.isD2dOrRPSDestType()){
				if(!rpsServerIsLocalhost(backupConfig.getRPSHostName())){
					String protocol = backupConfig.isRPSProtocol()?"Http:":"Https:";
					boolean ret = this.registerD2DToRPSServer(backupConfig.getRPSHostName(), backupConfig.getRPSUserName(), backupConfig.getRPSPassword(), backupConfig.getRPSPort(), protocol);
					if(!ret)
						logger.info("Failed to update D2D info to RPS");
				}else{
					String loginUUID = CommonService.getInstance().getUUID();
					String protocol = CommonService.getInstance().getLocalHostAsTrust().getProtocol();
					//if(loginUUID==null) return false;
					if(protocol.endsWith(":") == false)
					{
						protocol += ":";
					}
					if(!RPSCommonFunction.getInstance().GetRPSRole().RegisterD2D(CommonService.getInstance().getLocalHostAsTrust().getName(), protocol, CommonService.getInstance().getLocalHostAsTrust().getPort(), loginUUID)){
						logger.info("Failed to update D2D info to RPS to local");
					}
				}
			}
		} catch (Exception e) {
			logger.info("Failed to update D2D info to RPS",e);
		}*/
	}
	
	

	// D2D call this method to get the data password from RPS server, not ready
	public String getRPSDataPassword(String hostName, int port, String protocol) throws ServiceException
	{
		RPSWebServiceClientProxy connection =  RPSWebServiceFactory.getRPSService4D2D(protocol,hostName,port);
		
		//return connection.getServiceForRPS().getRPSDataPassword();
		return null;
	}

	

		
}
