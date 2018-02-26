package com.ca.arcserve.edge.app.base.webservice.node.discovery;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcflash.webservice.jni.model.JHypervInfo;
import com.ca.arcflash.webservice.jni.model.JHypervVMInfo;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVEntityInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HypervProtectionType;
import com.ca.arcserve.edge.app.base.webservice.jni.IHyperVNativeFacade.GetHyperVProtectionTypeResult;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;

public class HyperVManagerAdapter {
	
	private static HyperVManagerAdapter instance = new HyperVManagerAdapter();
	//private static NativeFacade NATIVEFACADE = new NativeFacadeImpl();
	private static Logger logger = Logger.getLogger(HyperVManagerAdapter.class);
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
	
	private HyperVManagerAdapter() {
	}
	
	public static HyperVManagerAdapter getInstance() {
		return instance;
	}
	
	public List<DiscoveryVirtualMachineInfo> getHypervVMList(GatewayId gatewayId, String host, String user,
			@NotPrintAttribute String password, HypervProtectionType hypervProtectionType) throws EdgeServiceFault {
		try {
			
			IRemoteNativeFacade nativeFacade =
				remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId );
			
			List<DiscoveryVirtualMachineInfo> hypervVMList = new ArrayList<DiscoveryVirtualMachineInfo>();
//			List<JHypervVMInfo> hvList = nativeFacade.GetVmList(host, user, password, false);
//			if (hvList == null || hvList.isEmpty()) {				
//				return hypervVMList;
//			}
			if (hypervProtectionType == HypervProtectionType.STANDALONE) {
				//issue 762224
				List<JHypervVMInfo> hvList = nativeFacade.GetVmList(host, user, password, true);
				if (hvList == null || hvList.isEmpty()) {				
					return hypervVMList;
				}
				int socketcount=nativeFacade.getHyperVCPUSocketCount(host, user, password);
				for (JHypervVMInfo hvInfo : hvList) {
					if(hvInfo.getVmType() == 1){
						DiscoveryVirtualMachineInfo vmInfo = convertVMInfo(hvInfo);
						hypervVMList.add(vmInfo);
						vmInfo.setVmEsxSocketCount(socketcount);
					}
				}
			} else if (hypervProtectionType == HypervProtectionType.CLUSTER) { //input is clust virtual nodes , return all the vm
				List<JHypervVMInfo> hvList = nativeFacade.GetVmList(host, user, password, false);
				if (hvList == null || hvList.isEmpty()) {				
					return hypervVMList;
				}
				Map<String, Integer> temp=new HashMap<String, Integer>();
				for (JHypervVMInfo hvInfo : hvList) {
					DiscoveryVirtualMachineInfo vmInfo = convertVMInfo(hvInfo);
					if(temp.containsKey(hvInfo.getHypervisor())){
						vmInfo.setVmEsxSocketCount(temp.get(hvInfo.getHypervisor()));
					}else{
						int socketcount=nativeFacade.getHyperVCPUSocketCount(hvInfo.getHypervisor(), user, password);
						temp.put(hvInfo.getHypervisor(), socketcount);
						vmInfo.setVmEsxSocketCount(temp.get(hvInfo.getHypervisor()));
					}
					hypervVMList.add(vmInfo);
				}
			} else if (hypervProtectionType == HypervProtectionType.STANDALONEANDCLUSTER) {
				List<JHypervVMInfo> hvList = nativeFacade.GetVmList(host, user, password, false);
				if (hvList == null || hvList.isEmpty()) {				
					return hypervVMList;
				}
				Map<String, Integer> temp=new HashMap<String, Integer>();
				for (JHypervVMInfo hvInfo : hvList) {
					if(hvInfo.getVmType() == 2 || (hvInfo.getVmType() ==1 && hvInfo.getHypervisor().equalsIgnoreCase(host))){
						DiscoveryVirtualMachineInfo vmInfo = convertVMInfo(hvInfo);
						if(temp.containsKey(hvInfo.getHypervisor())){
							vmInfo.setVmEsxSocketCount(temp.get(hvInfo.getHypervisor()));
						}else{
							int socketcount=nativeFacade.getHyperVCPUSocketCount(hvInfo.getHypervisor(), user, password);
							temp.put(hvInfo.getHypervisor(), socketcount);
							vmInfo.setVmEsxSocketCount(temp.get(hvInfo.getHypervisor()));
						}
						hypervVMList.add(vmInfo);
					}
				}
			}
			java.util.Collections.sort(hypervVMList);
			
			String message = EdgeCMWebServiceMessages.getMessage("autoDiscovery_hyperV_DiscoverInfo", String.valueOf(hypervVMList.size()),host);
			logger.info(message);
			
			ActivityLogServiceImpl activityLogService = new ActivityLogServiceImpl();
			ActivityLog log = new ActivityLog();
			log.setModule( Module.Common );
			log.setSeverity( Severity.Information );
			log.setNodeName( host );
			log.setMessage(message);
			log.setTime( new Date() );
			activityLogService.addLog(log);
			
			return hypervVMList;
		} catch (EdgeServiceFault e) {
			logger.error("Failed to get Hyper-V VM list from " + host + ", because " + e.getMessage(), e);
			String edgeServiceError = EdgeServiceErrorCode.Common_Service_General;
			if("100".equals(e.getFaultInfo().getCode())) {
				edgeServiceError = EdgeServiceErrorCode.Node_HYPERV_CONNECT_ERR;
			} else if("200".equals(e.getFaultInfo().getCode())) {
				edgeServiceError = EdgeServiceErrorCode.Node_HYPERV_GETVMLIST_ERR;
			}else if("12884901951".equals(e.getFaultInfo().getCode())){
				edgeServiceError = EdgeServiceErrorCode.Node_HYPERV_CONNECT_ERR;
			}
			EdgeServiceFaultBean faultInfo = new EdgeServiceFaultBean(
					edgeServiceError, "Fail to get Hyper-V information");
			throw new EdgeServiceFault("", faultInfo);
		}
	}
	private DiscoveryVirtualMachineInfo convertVMInfo(JHypervVMInfo hvInfo){
		DiscoveryVirtualMachineInfo vmInfo = new DiscoveryVirtualMachineInfo();
		vmInfo.setVmInstanceUuid(hvInfo.getVmUuid());
		vmInfo.setVmName(hvInfo.getVmName());
		vmInfo.setVmHostName(hvInfo.getVmHostName());
		vmInfo.setVmGuestOS(hvInfo.getVmGuestOS());
		vmInfo.setbRunning(hvInfo.getVmPowerStatus() == 2);
		vmInfo.setWindowsOS(vmInfo.getVmGuestOS() != null && vmInfo.getVmGuestOS().contains("Windows"));
		vmInfo.setVmType(hvInfo.getVmType());
		vmInfo.setVmEsxHost(hvInfo.getHypervisor());
		vmInfo.setClusterVirtualName(hvInfo.getClusterName());
		return vmInfo;
	}

	public void validateHyperVAccount(DiscoveryHyperVOption hyperVOption) throws EdgeServiceFault {
		if (hyperVOption == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_HYPERV_InvalidServer, "");
		}
		
		IRemoteNativeFacade nativeFacade =
			remoteNativeFacadeFactory.createRemoteNativeFacade( hyperVOption.getGatewayId() );
		
		String serverName = hyperVOption.getServerName();
		String userName = hyperVOption.getUsername();
		String password = hyperVOption.getPassword();
		boolean isCluster = hyperVOption.getHypervProtectionType() == HypervProtectionType.CLUSTER;
		if (serverName == null || serverName.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_HYPERV_InvalidServer, "");
		}
		if (userName == null || userName.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_InvalidUser, "");
		}
		
		try {
			nativeFacade.verifyHyperVAdminAccount(serverName, userName, password, isCluster);
			nativeFacade.setClusterAccessHint(serverName, userName, password);
		} catch (EdgeServiceFault e) {
			logger.error("Failed to validate Hyper-V server account for " + hyperVOption.getServerName() + ", because " + e.getMessage());
			EdgeServiceFault result = e;
			
			EdgeServiceFaultBean faultInfo = e.getFaultInfo();
			if (faultInfo != null) {
				String code = faultInfo.getCode();
				if (EdgeServiceErrorCode.Login_WrongCredential.equals(code)
						|| EdgeServiceErrorCode.Login_EmptyPassword.equals(code)) {
					throwExcetpion(e, "autoDiscovery_hyperV_ConnectFail_InvalidLoginException",
							EdgeServiceErrorCode.Node_HYPERV_ConnectFail_InvalidLoginException);
				} else if (EdgeServiceErrorCode.Login_NotAdministrator.equals(code)) {
					throwExcetpion(e, "autoDiscovery_hyperV_ConnectFail_InvalidLoginException",
							EdgeServiceErrorCode.Node_HYPERV_ConnectFail_NotAdministratorException);
				} else if (EdgeServiceErrorCode.Login_WrongNode.equals(code)
						|| EdgeServiceErrorCode.Login_Fail.equals(code)) {
					throwExcetpion(e, "autoDiscovery_hyperV_ConnectFail",
							EdgeServiceErrorCode.Node_HYPERV_InvalidInformationOrServerNotAvailable);
				} else {
					throwExcetpion(e, "autoDiscovery_hyperV_ConnectFail",
							EdgeServiceErrorCode.Node_HYPERV_ConnectFail_Unkown);
				}
			} 
			
			throw result;
		}
		
		nativeFacade.testConnection(serverName, userName, password);
	}
	
	//ERROR_CODE_SUCCESS = 0, ERROR_CODE_UNKNOWN = -1, ERROR_CODE_CONNECT_TO_SERVER = 1,
	//ERROR_CODE_VALID_ACCOUNT_IN_OTHER_NODE = 2, ERROR_CODE_CONNECT_TO_SERVER_CLUSTER = 3, 
	//ERROR_CODE_CONNECT_LOCAL_ACCOUNT_FORMAT = 4, ERROR_CODE_CONNECT_TO_SERVER_ACCESS_DENIED = 5, 
	//ERROR_CODE_VALID_ACCOUNT_IN_OTHER_NODE_ACCESS_DENIED = 6,ERROR_CODE_SERVICE_NOT_INSTALLED =7 
	public HypervProtectionType getHyperVProtectionType(GatewayId gatewayId, String host, String user, @NotPrintAttribute String password)throws EdgeServiceFault {
		
		IRemoteNativeFacade nativeFacade =
			remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId );
		
		GetHyperVProtectionTypeResult result = nativeFacade.getHyperVProtectionType( host, user, password );
		long errorCode = result.getErrorCode();
		String hypervName = result.getAdditionInfo();
		HypervProtectionType type = result.getProtectionType();
		if(errorCode == 0){// have no error
			return type;
		}else {
			EdgeServiceFaultBean serviceFault =  new EdgeServiceFaultBean(EdgeServiceErrorCode.Common_Service_General,"Fail to get Hyper-V protection type.");;
			if (type == HypervProtectionType.DEFAULT) { // PROTECTION_TYPE_UNKNOWN = 0
				if(errorCode == 1){ //Failed to connect to server
					serviceFault = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_HYPERV_CONNECT_ERR,"Fail to get Hyper-V protection type.");
				}else if (errorCode == 5) { // ERROR_CODE_CONNECT_TO_SERVER_ACCESS_DENIED = 5
					serviceFault = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_HYPERV_ConnectFail_Access_Deny,"Fail to get Hyper-V protection type.");
				}else if(errorCode == 7){
					serviceFault = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_HYPERV_ConnectFail_ServiceNotExist,"Fail to get Hyper-V protection type.");
				}
			} else if (type == HypervProtectionType.STANDALONE) { // PROTECTION_TYPE_PRIVATE = 1
				if (errorCode == 2) {
					serviceFault = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_HYPERV_Private_Valid_Account_In_Other_Node,"Fail to get Hyper-V protection type.");
					if(result.getAdditionInfo() != null){
						serviceFault.setMessageParameters(new String[]{result.getAdditionInfo()});
					}
				} else if (errorCode == 3) {
					serviceFault = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_HYPERV_ConnectFail_Cluster_Service,"Fail to get Hyper-V protection type.");
				}else if (errorCode == 4) {//local credentials
					String[] virtualInfo  =  nativeFacade.getClusterVirtualByPyhsicalNode(host, user, password);
					String clusterVirtualName = "";
					if(virtualInfo != null){
						clusterVirtualName = virtualInfo[0];
					}
					serviceFault = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_HYPERV_Invalid_Cluster_Credential,"Fail to get Hyper-V protection type.");
					serviceFault.setMessageParameters(new String[]{host,clusterVirtualName});
				} else if (errorCode == 6) {
					serviceFault = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_HYPERV_Private_Valid_Account_Access_Deny,"Fail to get Hyper-V protection type.");
					serviceFault.setMessageParameters(new String[]{hypervName});
				}
			} else if (type == HypervProtectionType.CLUSTER) { // PROTECTION_TYPE_CLUSTER = 2
				if (errorCode == 2) {
					serviceFault = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_HYPERV_CONNECT_Invalidate_In_Other_Node,"Fail to get Hyper-V protection type.");
					if(result.getAdditionInfo() != null){
						serviceFault.setMessageParameters(new String[]{result.getAdditionInfo()});
					}
				}else if(errorCode == 3){//Failed to connect to cluster service on server
					serviceFault = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_HYPERV_ConnectFail_Cluster_Service,"Fail to get Hyper-V protection type.");
				} else if (errorCode == 4) {
					serviceFault = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_HYPERV_Cluster_CONNECT_Local_Account_Format,"Fail to get Hyper-V protection type.");
				} else if (errorCode == 6) {
					serviceFault = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_HYPERV_CONNECT_InValidate_In_Other_Node_Permisson,"Fail to get Hyper-V protection type.");
					serviceFault.setMessageParameters(new String[]{hypervName});
				}
			} else {
				serviceFault = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_HYPERV_ConnectFail_Unkown,"Fail to get Hyper-V protection type.");
			}
			throw new EdgeServiceFault(host, serviceFault);
		}
	}
	
	private void throwExcetpion(Exception e, String resorce, String errorCode) throws EdgeServiceFault {
		String logMessage = EdgeCMWebServiceMessages.getResource(resorce);
		logger.error(logMessage, e);
		throw EdgeServiceFault.getFault(errorCode, logMessage);
	}
	
	public JHypervVMInfo getHypervVMInfo(GatewayId gatewayId, String host, String user, @NotPrintAttribute String password, String vmInstanceUUID)throws EdgeServiceFault {
		IRemoteNativeFacade nativeFacade =
				remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId );
		return nativeFacade.getHypervVMInfo(host, user, password, vmInstanceUUID);
	}
	
	public DiscoveryHyperVEntityInfo getHyperVTreeRootEntity(GatewayId gatewayId, String host, String user,
			@NotPrintAttribute String password, HypervProtectionType hypervProtectionType) throws EdgeServiceFault {
		try {
			IRemoteNativeFacade nativeFacade =remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId );
			List<JHypervInfo> hvInfos = nativeFacade.getHypervList(host, user, password);
			return convertJHypervInfo(hvInfos);
		} catch (EdgeServiceFault e) {
			logger.error("Failed to getHyperVTreeRootEntity from " + host + ", because " + e.getMessage(), e);
//			String edgeServiceError = EdgeServiceErrorCode.Common_Service_General;
//			EdgeServiceFaultBean faultInfo = new EdgeServiceFaultBean(
//					edgeServiceError, "Fail to get Hyper-V information");
			throw e;
		}
	}

	private DiscoveryHyperVEntityInfo convertJHypervInfo(List<JHypervInfo> hvInfos) {
		if(hvInfos == null || hvInfos.size()==0)
			return null;
		DiscoveryHyperVEntityInfo result = new DiscoveryHyperVEntityInfo();
		if(hvInfos.size()==1){ //not cluster
			result.setCluster(hvInfos.get(0).getCluster());
			result.setHypervName(hvInfos.get(0).getHypervName());
		}else{  //cluster
			result.setCluster(true);
			result.setHypervName(hvInfos.get(0).getHypervName());
			for(JHypervInfo info : hvInfos){
				if(info.getNodeName()!=null && !"".equals(info.getNodeName())){
					result.getServerList().add(info.getNodeName());
					if(info.getCurrentHostServer()){
						result.setActiveNodeName(info.getNodeName());
					}
				}
			}
		}
		
		return result;
	}
}