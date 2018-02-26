package com.ca.arcserve.edge.app.base.webservice.instantvm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.model.ESXServerInfo;
import com.ca.arcflash.ha.model.EsxServerInformation;
import com.ca.arcflash.instantvm.HypervisorType;
import com.ca.arcflash.instantvm.InstantVMConfig;
import com.ca.arcflash.instantvm.PrecheckCriteria;
import com.ca.arcflash.instantvm.PrecheckResult;
import com.ca.arcflash.rps.webservice.data.RecoveryPointWithNodeInfo;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4CPM;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.restore.BackupD2D;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.restore.RecoveryPointItem;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcserve.edge.app.base.appdaos.EdgeEsx;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHyperV;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.InstantVMConnection;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.IInstantVMService;
import com.ca.arcserve.edge.app.base.webservice.INodeService;
import com.ca.arcserve.edge.app.base.webservice.IRecoveryPointService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.SortablePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationBrowser;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationBrowser.RPBrowserType;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.sharefolder.SharedFolderBrowseParam;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVHDOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.ProtectedNodeWithRecoveryPoints;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.RecoveryPointInfoForInstantVM;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.RecoveryServerResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StartInstantVHDOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StartInstantVMOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StopInstantVHDOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StopInstantVMOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AddNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HypervProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeConvertUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeSortCol;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.BitmapFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.CommonNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.OSFilterType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NodeFilter.NodeFilterType;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.RecoveryPointInformationForCPM;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.EsxVSphere;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntityType;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.MonitorHyperVInfo;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.recoverypoints.RecoveryPointBrowseUtil;
import com.ca.arcserve.edge.app.base.webservice.recoverypoints.RecoveryPointServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
//import com.ca.arcflash.instantvm.InstantVMConfig;
import com.ca.arcserve.linuximaging.webservice.data.BackupLocationInfo;

public class InstantVMServiceImpl implements IInstantVMService{
	private static final Logger logger = Logger.getLogger(InstantVMServiceImpl.class);
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
	public static final String HYPERV_NETWORK_ADAPTER = "Network Adapter";
	public static final String HYPERV_LEGACY_NETWORK_ADAPTER = "Legacy Network Adapter";
	
	private INodeService nodeService = new NodeServiceImpl();
	private IRecoveryPointService recoveryPointService = new RecoveryPointServiceImpl();
	
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	
	private LinuxInstantVMHandler linuxHandler = new LinuxInstantVMHandler();
	private WindowsInstantVMHandler windowsHandler = new WindowsInstantVMHandler();
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	
	EdgeWebServiceImpl serviceImpl;
	
	public InstantVMServiceImpl(){
		
	}
	public InstantVMServiceImpl(EdgeWebServiceImpl serviceImpl){
		this.serviceImpl = serviceImpl;
		nodeService = new NodeServiceImpl(serviceImpl);
	}
	
	@Override
	public  InstantVMOperationResult startInstantVM( StartInstantVMOperation operationPara ) throws EdgeServiceFault{
		logger.debug("startInstantVM: "+InstantVMServiceUtil.printObject(operationPara));
		
		NodeRegistrationInfo proxyInfo = operationPara.getProxyInfo();
		try {
			if( proxyInfo.getId() <= 0 ) {
				logger.warn( "startInstantVM()  node: " + proxyInfo.getNodeName() +" id is not valid" );
			}
			RecoveryPointInformationForCPM rpForCPM =  operationPara.getRecoveryPointForCPM(); 
			if( !rpForCPM.isWindowsSession() ) {
				return linuxHandler.handleLinuxInstantVM( proxyInfo, rpForCPM, operationPara );
			}
			else {
				return windowsHandler.handleWindowsInstantVM( proxyInfo, rpForCPM, operationPara );
			}
		} 
		catch (Exception e) {
//			InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, proxyInfo.getNodeName(), EdgeCMWebServiceMessages.getMessage("startInstantVMFailed", operationPara.getRecoveryPointForCPM().getProtectedNode().getNodeName()));
			throw e;
		}
	}
	


	@Override
	public InstantVMOperationResult stopInstantVM(StopInstantVMOperation operationPara) throws EdgeServiceFault {
		return InstantVMManager.getInstance().stopInstantVM(operationPara.getInstantVMJobUUID(), operationPara.isForceRemove());
	}
	
	@Override
	public InstantVHDOperationResult startInstantVHD(StartInstantVHDOperation para) throws EdgeServiceFault {
		InstantVHDOperationResult result = new InstantVHDOperationResult();
		String proxyName = para.getProxyNameOrIP();
		if (StringUtil.isEmptyOrNull(proxyName)) {
			result.setErrCode(-1);
			result.setErrString("Porxy name is empty.");
			return result;
		}
		int proxyID = nodeService.getHostIdByHostNameOrIP(gatewayService.getLocalGateway().getId().getRecordId(), proxyName, proxyName, 1);	// TODO: should get correct gateway here
		if (proxyID == 0) {
			result.setErrCode(-1);
			result.setErrString(String.format("The proxy %s is not managed by current console.", proxyName));
			return result;
		}
		if (para.getSessionNum() <= 0) {
			result.setErrCode(-1);
			result.setErrString("Incorrect session number.");
			return result;
		}
		if (StringUtil.isEmptyOrNull(para.getNodeName())) {
			result.setErrCode(-1);
			result.setErrString("Node name is empty.");
			return result;
		}
		if (StringUtil.isEmptyOrNull(para.getBackupDestination())) {
			result.setErrCode(-1);
			result.setErrString("Backup destination is empty.");
			return result;
		}
		if (StringUtil.isEmptyOrNull(para.getVhdPath())) {
			result.setErrCode(-1);
			result.setErrString("VHD path is empty.");
			return result;
		}
		if (para.getTimeout() < 30000)
			para.setTimeout(30000);
		
		try {
			return windowsHandler.startWindowsInstantVHD(para, proxyID);
		} catch(Exception e) {
			InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, 0, para.getNodeName(), EdgeCMWebServiceMessages.getMessage("startInstantVMFailed", para.getNodeName()), JobType.JOBTYPE_START_INSTANT_VHD);
			throw e;
		}
	}
	
	@Override
	public InstantVHDOperationResult stopInstantVHD(StopInstantVHDOperation para) throws EdgeServiceFault {
		InstantVHDOperationResult result = new InstantVHDOperationResult();
		String proxyName = para.getProxyNameOrIP();
		if (StringUtil.isEmptyOrNull(proxyName)) {
			result.setErrCode(-1);
			result.setErrString("Porxy name is empty.");
			return result;
		}
		int proxyID = nodeService.getHostIdByHostNameOrIP(gatewayService.getLocalGateway().getId().getRecordId(), proxyName, proxyName, 1);	// TODO: should get correct gateway here
		if (proxyID == 0) {
			result.setErrCode(-1);
			result.setErrString(String.format("The proxy %s is not managed by current console.", proxyName));
			return result;
		}
		if (StringUtil.isEmptyOrNull(para.getJobUUID())) {
			result.setErrCode(-1);
			result.setErrString("Job ID is empty.");
			return result;
		}
		try {
			return windowsHandler.stopWindowsInstantVHD(para, proxyID);
		} catch (Exception e) {
			logger.error(String.format("Fail to stop instant VHD job on %s.", proxyName), e);
			throw e;
		}
	}


	@Override
	public InstantVMPagingResult getInstantVMPagingNodes(InstantVMPagingConfig config, InstantVMFilter filter) {
		return InstantVMManager.getInstance().getPagingInstantVMs(config, filter);
	}



	@Override
	public long powerOnIVM(String instantVMJobUUID, String ivmUUID) throws EdgeServiceFault{
		return InstantVMManager.getInstance().powerOnIVM(instantVMJobUUID, ivmUUID);
	}

	

	@Override
	public long powerOffIVM(String instantVMJobUUID, String ivmUUID) throws EdgeServiceFault{
		return InstantVMManager.getInstance().powerOffIVM(instantVMJobUUID, ivmUUID);
	}
	
	
	//new
	@Override
	public MonitorHyperVInfo getMonitorHyperVInfoFromConsole(HostConnectInfo monitorInfo) throws EdgeServiceFault {

		GatewayId gate = new GatewayId();  
		if(GatewayId.INVALID_GATEWAY_ID.equals(monitorInfo.getGatewayId())){
			gate = gatewayService.getGatewayByEntityId(monitorInfo.getId(), EntityType.HyperVServer).getId();
		}else{
			gate = monitorInfo.getGatewayId();
		}
		IRemoteNativeFacade nativeFacade =
				remoteNativeFacadeFactory.createRemoteNativeFacade(gate);
		
//		boolean installedAndVaildOS = nativeFacade.isHyperVRoleInstalledAndHostOS();
//		if(!installedAndVaildOS){
//			throw new EdgeServiceFault("not installed or os not correct.", new EdgeServiceFaultBean("", ""));
//		}
		String[] netWorks = nativeFacade.getHypervNetworksFromMonitor(monitorInfo.getHostName(), monitorInfo.getUserName(), monitorInfo.getPassword());


		String[] networkAdapterTypes = new String[]{HYPERV_NETWORK_ADAPTER,HYPERV_LEGACY_NETWORK_ADAPTER};
		
		ESXServerInfo serverInfo = nativeFacade.getHypervInfo(monitorInfo.getHostName(), monitorInfo.getUserName(), monitorInfo.getPassword());
		MonitorHyperVInfo info = new MonitorHyperVInfo();
//		info.setInstalled(installedAndVaildOS);
//		info.setValidOS(installedAndVaildOS);
		info.setNetworks(netWorks);
		info.setNetworkAdapterTypes(networkAdapterTypes);
		info.setServerInfo(serverInfo);
		return info;
	}

	@Override
	public RecoveryPointInfoForInstantVM getRecoveryPointInfo(
			int rpsNodeId, RecoveryPointInformationForCPM rp)
			throws EdgeServiceFault {		
		RecoveryPointInfoForInstantVM info = new RecoveryPointInfoForInstantVM();
		boolean containsBootAndSystemVolume = false;
		
		if (RPBrowserType.SharedFolderUsingLinuxServer.equals(rp.getBrowser().getBrowserType())) {			  
			containsBootAndSystemVolume = containsBootAndSystemVolumeForLinux(rp);
		} else {
			containsBootAndSystemVolume = containsBootAndSystemVolumeForWindows(rpsNodeId, rp);
		}			
		
		info.setContainsBootAndSystemVolume(containsBootAndSystemVolume);
		info.setMachineConfigure(recoveryPointService.getRecoveryPointMachineConfig(rp));
		
		return info;
	}
	
	private boolean containsBootAndSystemVolumeForWindows(int rpsNodeId, RecoveryPointInformationForCPM rp) throws EdgeServiceFault {
		RecoveryPointItem[] items = null;
		boolean hasBootVolume = false;
		boolean hasSystemVolume = false;
		
		items = recoveryPointService.getRecoveryPointItems(rp.getRecoveryPoint(), rpsNodeId, rp.getProtectedNode());		
		if (null != items) {			
			for (RecoveryPointItem item : items) {
				if (hasSystemVolume && hasBootVolume) {							
					break;
				}									
				if ((item.getVolAttr() & RecoveryPointItem.BootVol) > 0) {
					hasBootVolume = true;
				}
				if ((item.getVolAttr() & RecoveryPointItem.SysVol) > 0) {
					hasSystemVolume = true;
				}					
			}
		}
		
		return hasSystemVolume && hasBootVolume;
	}
	
	private boolean containsBootAndSystemVolumeForLinux(RecoveryPointInformationForCPM rp) throws EdgeServiceFault {
		boolean containsBootAndSystemVolume = false;
		BackupLocationInfo locationInfo = RecoveryPointBrowseUtil.getInstance().getDestinationWithPasswordForLinux(rp.getBrowser());
		
		try (LinuxD2DConnection linuxConn = RecoveryPointBrowseUtil.getInstance().getLinuxDestinationBrowser(rp.getBrowser())) {				
			ILinuximagingService linuxServer = linuxConn.getService();
			com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint linuxRecoveryPoint = linuxServer.getRecoveryPointFromSession(locationInfo, 
					RecoveryPointBrowseUtil.getInstance().convertToLinuxRecoveryPoint(rp.getRecoveryPoint()), 
					rp.getRecoveryPoint().getNodeUuid());
			
			if (linuxRecoveryPoint.getRootVolumeBackupFlag() == 0) {
				containsBootAndSystemVolume = false;
			} else if (linuxRecoveryPoint.getBootVolumeExistFlag() == 1
					&& linuxRecoveryPoint.getBootVolumeBackupFlag() == 0) {
				containsBootAndSystemVolume = false;
			}									
		} catch(Exception e) {
			logger.error("containsBootAndSystemVolumeForLinux() failed get nodes Inforamtion from location: " + locationInfo.getBackupDestLocation(), e);
			throw e;
		}		 				
		
		return containsBootAndSystemVolume;
	}

	@Override
	public ProtectedNodeWithRecoveryPoints getRecoveryPointsByNode(DestinationBrowser browser, ProtectedNodeInDestination node,Date beginTime, Date endTime) throws EdgeServiceFault{
		logger.debug("getRecoveryPointsByNode() start.");
		logger.debug("DestinationBrowser: "+InstantVMServiceUtil.parseObjectToXmlString(browser));
		logger.debug("ProtectedNodeInDestination: "+InstantVMServiceUtil.printObject(node));
		ProtectedNodeWithRecoveryPoints result = new ProtectedNodeWithRecoveryPoints();
		List<RecoveryPoint> recoveryPointList = new ArrayList<RecoveryPoint>();
		if(browser.getBrowserType()==RPBrowserType.DataStoreUsingRPS){//datastore need node.getDestination()
			if(StringUtil.isEmptyOrNull(node.getDestination())){
				loadProtectedNodeFromDataStore(browser, node);
				logger.info("loadProtectedNodeFromDataStore(), node: "+InstantVMServiceUtil.printObject(node));
			}
			if(!StringUtil.isEmptyOrNull(node.getDestination())){
				recoveryPointList =recoveryPointService.getRecoveryPointsByTimePeriod(browser.getBrowserId(), node, beginTime, endTime);
			}else{
				logger.info("There is no recovey point of "+node.getNodeName()+"("+node.getNodeUuid()+") in DataStore "+browser.getSubDest());
			}
		}else if(browser.getBrowserType()==RPBrowserType.SharedFolderUsingRPS){//windows need node.getDestination(), which is share folder path
			SharedFolderBrowseParam param = new SharedFolderBrowseParam(browser, node);
			List<RecoveryPointWithNodeInfo> rpWIthNode = recoveryPointService.getGroupedRecoveryPointsFromSharedFolder( param,  beginTime, endTime);
//			RecoveryPointWithNodeInfo rpNode = filterProtectedNodeFromShareFolder(rpWIthNode, node);
			RecoveryPointWithNodeInfo rpNode = InstantVMServiceUtil.filterRecoveryPointWithNodeInfoByProtectedNode(rpWIthNode, node);
			logger.debug("getProtectedNodeFromShareFolder(), RecoveryPointWithNodeInfo: "+InstantVMServiceUtil.parseObjectToXmlString(rpNode));
			if(rpNode!=null){
				node.setDestination(rpNode.getFullPath());
				recoveryPointList = rpNode.getRecoveryPoints(); 
			}else{
				logger.info("There is no recovey point of "+node.getNodeName()+"("+node.getNodeUuid()+") in Share Folder " + node.getDestination());
			}
		}else if(browser.getBrowserType()==RPBrowserType.SharedFolderUsingLinuxServer){//linux need node.getNodeName()
			SharedFolderBrowseParam param = new SharedFolderBrowseParam(browser, node);
			recoveryPointList = recoveryPointService.getLinuxRecoveryPoints(param , beginTime, endTime);
		}else if(browser.getBrowserType()==RPBrowserType.LocalDiskUsingD2D){
			if(StringUtil.isEmptyOrNull(node.getDestination())){
				loadProtectedNodeFromD2DProxy(browser, node);
				logger.info("loadProtectedNodeFromD2DProxy(), node: "+InstantVMServiceUtil.printObject(node));
			}
			if(!StringUtil.isEmptyOrNull(node.getDestination())){
				recoveryPointList = getRecoveryPointsFromLocalDisk(browser, node, beginTime, endTime);
			}else{
				logger.info("There is no backup settings of "+node.getNodeName()+"("+node.getNodeUuid()+") in D2DProxy "+browser.getBrowserName());
			}
		}
		result.setProtectedNode(node);
		result.setRecoveryPointList(recoveryPointList);
		logger.debug("getRecoveryPointsByNode() return: "+InstantVMServiceUtil.printObject(result));
		return result;
	}
	
	private List<RecoveryPoint> getRecoveryPointsFromLocalDisk(DestinationBrowser browser, ProtectedNodeInDestination node, Date beginTime, Date endTime) throws EdgeServiceFault {
		try(D2DConnection conn = EdgeCommonUtil.getD2DProxyByNodeId(browser.getBrowserId())){
			String domain = "";
			String pwd = "";
			String userName = node.getUsername();
			if (userName != null) {
				int index = userName.indexOf('\\');
				if (index > 0) {
					domain = userName.substring(0, index);
					userName = userName.substring(index + 1);
				}
				pwd = node.getPassword();
			}
			RecoveryPoint[] array = conn.getService().getRecoveryPoints(node.getDestination(), domain, userName, pwd, beginTime, endTime);
			List<RecoveryPoint> recoveryPointList = new ArrayList<RecoveryPoint>();
			if(array!=null&&array.length>0){
				for(RecoveryPoint point : array){
					recoveryPointList.add(point);
				}
			}
			return recoveryPointList;
		}
		
	}
	
	private boolean loadProtectedNodeFromD2DProxy(DestinationBrowser browser, ProtectedNodeInDestination node) throws EdgeServiceFault {
		try(D2DConnection conn = EdgeCommonUtil.getD2DProxyByNodeId(browser.getBrowserId())){
			if(browser.getBrowserType()==RPBrowserType.LocalDiskUsingD2D){
				BackupConfiguration backupConfig = conn.getService().getBackupConfiguration();
				return InstantVMServiceUtil.loadDestiantionInfo(backupConfig, node);
			}else if(browser.getBrowserType()==RPBrowserType.LocalDiskUsingHBBUProxy){
				VirtualMachine vm = new VirtualMachine();
		        vm.setVmInstanceUUID(node.getNodeUuid());
		        VMBackupConfiguration vmConfig = conn.getService().getVMBackupConfiguration(vm);
		        return InstantVMServiceUtil.loadDestiantionInfo(vmConfig, node);
			}else{
				return false;
			}
		}
		
	}
	/*private RecoveryPointWithNodeInfo filterProtectedNodeFromShareFolder(List<RecoveryPointWithNodeInfo> rpWIthNode, ProtectedNodeInDestination node) {
		if(rpWIthNode == null)
			return null;
		
		RecoveryPointWithNodeInfo result = null;
		for(RecoveryPointWithNodeInfo protectedNode : rpWIthNode){
			if(protectedNode.getNodeName().equals(node.getNodeName())){
				result = protectedNode;
				break;
			}
		}
		return result;
	}*/
	
	private boolean loadProtectedNodeFromDataStore(DestinationBrowser browser, ProtectedNodeInDestination node) throws EdgeServiceFault{
		int rpsNodeId = browser.getBrowserId();
		String dataStoreUUID = browser.getSubDest();
		if(StringUtil.isEmptyOrNull(dataStoreUUID)){
			logger.error("dataStoreUUID is null. can't load protected node." );
			return false;
		}
		List<BackupD2D> nodes ;
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(rpsNodeId)){
			IRPSService4CPM service = conn.getClientProxy().getServiceForCPM();
			nodes = service.getRegistedClientByDatastore(dataStoreUUID);
		}
		if (nodes == null) {
			logger.error("can't find any protected node from datastore.");
			return false;
		}
//		if(node.getNodeUuid()==null || "".equals(node.getNodeUuid())){
//			logger.debug(" nodeUUID is null, get it from nodeDetail again.");
//			NodeDetail nodeDetail = nodeService.getNodeDetailInformation(node.getNodeId());
//			node.setNodeUuid(nodeDetail.getD2DUUID());
//		}
		BackupD2D target = null;
		for (BackupD2D d2d : nodes) {
			logger.debug("BackupD2D: "+InstantVMServiceUtil.printObject(d2d));
			if(StringUtil.isEmptyOrNull(node.getNodeUuid())){
				if(node.getNodeName().equalsIgnoreCase(d2d.getHostname())){
					target = d2d;
					break;
				}
			}else{
				if(node.getNodeUuid().equals(d2d.getClientUUID())){
					target = d2d;
					break;
				}
			}
		}
		if(target!=null){
			node.setNodeName(target.getHostname());//for hbbu, vm@hypervisor
			node.setDestination(target.getFullBackupDestination());
			node.setUsername(target.getDesUsername());
			node.setPassword(target.getDesPassword());
			node.setPlanUuid(target.getPlanUUID());
			node.setHaveSessions(target.getLastBackupTime()<=0?false:true);
			node.setIntegral(target.isIntegral());
			return true;
		}else{
			return false;
		}
	}

	@Override
	public int queryEdgeMgrStatusForNode(int nodeID) throws EdgeServiceFault {		
		try (D2DConnection connection = connectionFactory.createD2DConnection(nodeID)) {			
			connection.connect();			
			return connection.getService().QueryEdgeMgrStatus(CommonUtil.retrieveCurrentAppUUID(), ApplicationType.CentralManagement, EdgeCommonUtil.getLocalFqdnName());
		}		
	}
	
	@Override
	public int saveVMWareInfoToDB(DiscoveryESXOption esxOption) throws EdgeServiceFault {		
		EsxServerInformation info = nodeService.getEsxServerInformation(esxOption);
		
		List<VsphereEntityType> types = new ArrayList<VsphereEntityType>();
        types.add(VsphereEntityType.parse(info.getServerType()));
		List<EsxVSphere> existedEsxList = nodeService.getEsxInfoList(esxOption.getGatewayId().getRecordId(), types);
		for (EsxVSphere esx : existedEsxList) {
			if(esx.getHostname().equalsIgnoreCase(esxOption.getEsxServerName())){
				return esx.getId();
			}
		}
		
		//save esx to esx db table
		List<EdgeEsx> esxList = new ArrayList<EdgeEsx>();		
		esxDao.as_edge_esx_getByName(esxOption.getGatewayId().getRecordId(), info.getEsxServer(), esxList); //we need a uuid for esx sever/vcenter physical machine, then change the implementation		
		int esxID = esxList.isEmpty() ? 0 : esxList.get(0).getId();		
		int[] output = new int[1];
		esxDao.as_edge_esx_update(esxID, 
				info.getEsxServer(), 
				info.getUserName(), 
				info.getPasswod(), 
				Protocol.parse(info.getProtocol()).ordinal(), 
				info.getPort(),
				info.getServerType(),
				0,
				"",
				"",
				output);
		
		if (esxID == 0) {
			esxID = output[0];
		}								
		gatewayService.bindEntity(esxOption.getGatewayId(), esxID, EntityType.VSphereEntity);
		
		return esxID;
	}
	
	@Override
	public int saveHyperVInfoToDB(DiscoveryHyperVOption hyperVOption) throws EdgeServiceFault {
		List<EsxVSphere> existedHyperVList = nodeService.getHyperVInfoList(hyperVOption.getGatewayId().getRecordId());
		for (EsxVSphere hyperV : existedHyperVList) {
			if (hyperV.getHostname().equalsIgnoreCase(hyperVOption.getServerName())) {
				return hyperV.getId();
			}
		}
				
		List<EdgeHyperV> hypervList = new ArrayList<EdgeHyperV>();
		hyperVDao.as_edge_hyperv_getByName(hyperVOption.getGatewayId().getRecordId(), hyperVOption.getServerName(), hypervList);		
		
		int hypervServerId = hypervList.isEmpty() ? 0 : hypervList.get(0).getId();
		int hypervType = (hyperVOption.getHypervProtectionType() == HypervProtectionType.CLUSTER) ? 
				HypervProtectionType.CLUSTER.getValue() : HypervProtectionType.STANDALONE.getValue();

		int[] output = new int[1];
		hyperVDao.as_edge_hyperv_update(hypervServerId, 
				hyperVOption.getServerName(),
				hyperVOption.getUsername(), 
				hyperVOption.getPassword(), 
				0,  
				0,
				1,
				hypervType,
				output);
		
		if (hypervServerId == 0) {
			hypervServerId = output[0];
		}			
		gatewayService.bindEntity(hyperVOption.getGatewayId(), hypervServerId, EntityType.HyperVServer);
		
		return hypervServerId;
	}
	
//	@Override
//	public PrecheckResult checkRecoveryServer(HypervisorType type, NodeRegistrationInfo regInfo)  throws EdgeServiceFault{
//		
//		return InstantVMManager.getInstance().checkRecoveryServerOS(type, regInfo);
//		
//	}
	
//	@Override
//	public RecoveryServerResult serverAndNFScheck(NodeRegistrationInfo regInfo, 
//			HypervisorType type, boolean addNode)throws EdgeServiceFault {
//		return InstantVMManager.getInstance()
//				.serverAndNFScheck(regInfo, type, addNode);
//	}
//	
//	@Override
//	public RecoveryServerResult validateRecoveryServerConnectAndManage(
//			HypervisorType type, Node agent,
//			GatewayEntity gateway, boolean isLinux, boolean isRps,
//			boolean isHyperV) throws EdgeServiceFault {
//		
//		return InstantVMManager.getInstance()
//				.validateRecoveryServerConnectAndManage(type, agent,
//						gateway, isLinux, isRps, isHyperV);
//	}
//	
//	@Override
//	public RecoveryServerResult validateRecoveryServerAddRPSAndHypervNode(
//			HypervisorType type, NodeDetail detail,
//			GatewayEntity gateway, boolean isLinux, boolean isRps)
//			throws EdgeServiceFault {
//		return InstantVMManager.getInstance()
//				.validateRecoveryServerAddRPSAndHypervNode(type,
//						detail, gateway, isLinux, isRps);
//	}
//	
//	@Override
//	public RecoveryServerResult validateRecoveryServerUpdateNode(
//			HypervisorType type, NodeDetail detail,
//			GatewayEntity gateway, boolean isLinux) throws EdgeServiceFault {
//		return InstantVMManager.getInstance().validateRecoveryServerUpdateNode(
//				type, detail, gateway, isLinux);
//	}
//	
//	@Override
//	public RecoveryServerResult validateRecoveryServerVersionAndInstall(
//			HypervisorType type, NodeDetail detail,
//			GatewayEntity gateway, boolean isLinux) throws EdgeServiceFault {
//		return InstantVMManager.getInstance()
//				.validateRecoveryServerVersionAndInstall(type, detail,
//						gateway, isLinux);
//	}
	
	private PrecheckResult checkRecoveryServerOS(HypervisorType type, NodeRegistrationInfo regInfo) throws EdgeServiceFault{
		PrecheckResult result = null;
		
		
		ConnectionContext context = new ConnectionContext(regInfo.getD2dProtocol(), regInfo.getNodeName(), regInfo.getD2dPort());
		context.buildCredential(regInfo.getUsername(), regInfo.getPassword(), "");
		context.setGateway(gatewayService.getGatewayById(regInfo.getGatewayId()));
		try (InstantVMConnection connection = connectionFactory.createInstantVMConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			com.ca.arcflash.webservice.IInstantVMService service = connection.getService();
			
			PrecheckCriteria criteria = new PrecheckCriteria();
			if(HypervisorType.HYPERV==type){
				criteria.setCheckHyperVClientAndServerBitMask();
			}else if(HypervisorType.VMWARE==type){
				criteria.setCheckVMareVClientAndServerBitMask();
			}
			InstantVMConfig para = new InstantVMConfig();
			para.setNodeUUID(regInfo.getNodeInfo().getD2DUUID());
			para.setHypervisorType(type);
			result = service.checkPrerequisites(para, criteria);
			
		} catch (EdgeServiceFault e) {
			logger.error("failed to checkRecoveryServer: ", e);
			throw e;
		}
		return result;
	}
	
	public RecoveryServerResult validateRecoveryServerConnectAndManage(HypervisorType type, Node agent, GatewayEntity gateway, boolean isLinux, boolean isRps, boolean isHyperV) throws EdgeServiceFault{
		int i = -1;
		NodeDetail detail = null;
		RecoveryServerResult result = null;
		if(isRps){
			//this agent id is rps id.
			NodeDetail rpsDetail = nodeService.getNodeDetailInformation(agent.getId());
			if(rpsDetail!=null){
				
				List<Node> nodes = getAllNodes(rpsDetail.getHostname());
				
				Node rpsAgent = null;
				
				if (nodes != null && nodes.size()!=0) {
					for (Node node : nodes) {
						if (HostTypeUtil.isPhysicsMachine(node.getRhostType()) && rpsDetail.getHostname().equalsIgnoreCase(node.getHostname())) {
							rpsAgent = node;
							break;
						}
					}
					if(rpsAgent!=null){
						detail = nodeService.getNodeDetailInformation(rpsAgent.getId());
					}
				}
				
				if(rpsAgent==null){//rps agent does't managed by current console.go to vali step21
					result = new RecoveryServerResult();
					result.setHasError(true);
					result.setErrorCode(EdgeServiceErrorCode.INSTANTVM_RPSAGENT_DONT_EXIST_CURRENT_CONSLE);
					result.setNodeDetail(rpsDetail);
					return result;

				}
			}
		}else if(!isLinux && isHyperV){
			
			List<Node> nodes = getAllNodes(agent.getHostname());
			Node hypervAgent = null;
			if (nodes != null && nodes.size()!=0) {
				for (Node node : nodes) {
					if (HostTypeUtil.isPhysicsMachine(node.getRhostType()) && agent.getHostname().equalsIgnoreCase(node.getHostname())) {
						hypervAgent = node;
						break;
					}
				}
				if(hypervAgent!=null){
					detail = nodeService.getNodeDetailInformation(hypervAgent.getId());
				}
			}
			
			if(hypervAgent==null){//hyperv agent does't exist in current console.go to vali step21
//				result = new RecoveryServerResult();
//				result.setHasError(true);
//				result.setErrorCode(RecoveryServerResult.HYPERVAGENT_DONT_EXIST_CURRENT_CONSLE);
//				return result;
				String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_HYPERVAGENT_DONT_EXIST_CURRENT_CONSLE);
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_HYPERVAGENT_DONT_EXIST_CURRENT_CONSLE, errorMessage);
			}
			
		}else{
			detail = nodeService.getNodeDetailInformation(agent.getId());
			
		}
		
		if(detail!=null){
			if(!isLinux){
				i = recoveryServerWinValiConnectAndManaged(detail, gateway);
			}
		}
//		return i;
		if(i==1){
			result = validateRecoveryServerVersionAndInstall(type, detail, gateway, isLinux);
		}else if(i == 2){ //show force managed
			result = new RecoveryServerResult();
			result.setHasError(true);
			result.setErrorCode(EdgeServiceErrorCode.INSTANTVM_WINDOWS_AGENT_MANAGED_OTHERS);
			result.setNodeDetail(detail);
		}else if(i==0){
			result = new RecoveryServerResult();
			result.setHasError(true);
			result.setErrorCode(EdgeServiceErrorCode.INSTANTVM_WINDOWS_AGENT_NOT_REGISTER);
			result.setNodeDetail(detail);
		}
		return result;
	}
	
	private List<Node> getAllNodes(String nodeName) throws EdgeServiceFault{
		NodeGroup group = new NodeGroup();
		group.setId(NodeGroup.ALLGROUP);
		group.setType(NodeGroup.Default);
		List<NodeFilter> filters = new ArrayList<NodeFilter>();
		CommonNodeFilter filter1 = new CommonNodeFilter();
		filter1.setApplicationBitmap(0);
		filter1.setHostTypeBitmap(0);
		filter1.setNodeNamePattern(nodeName);
		filter1.setOsBitmap(OSFilterType.Windows.getValue());
		filter1.setType(NodeFilterType.Common);
		BitmapFilter filter2 = new BitmapFilter();
		filter2.setBitmap(0);
		filter2.setType(NodeFilterType.JobStatus);
		BitmapFilter filter3 = new BitmapFilter();
		filter3.setBitmap(0);
		filter3.setType(NodeFilterType.PlanProtectionType);
		BitmapFilter filter4 = new BitmapFilter();
		filter4.setBitmap(0);
		filter4.setType(NodeFilterType.NodeStatus);
		BitmapFilter filter5 = new BitmapFilter();
		filter5.setBitmap(0);
		filter5.setType(NodeFilterType.RemoteDeployStatus);
		BitmapFilter filter6 = new BitmapFilter();
		filter6.setBitmap(0);
		filter6.setType(NodeFilterType.NotNullField);
		BitmapFilter filter7 = new BitmapFilter();
		filter7.setBitmap(0);
		filter7.setType(NodeFilterType.NotNullField);
		BitmapFilter filter8 = new BitmapFilter();
		filter8.setBitmap(0);
		filter8.setType(NodeFilterType.LastBackupStatus);
		BitmapFilter filter9 = new BitmapFilter();
		filter9.setBitmap(0);
		filter9.setType(NodeFilterType.GateWay);
		filters.add(filter1);
		filters.add(filter2);
		filters.add(filter3);
		filters.add(filter4);
		filters.add(filter5);
		filters.add(filter6);
		filters.add(filter7);
		filters.add(filter8);
		filters.add(filter9);
		
		SortablePagingConfig<NodeSortCol> pagingConfig = new SortablePagingConfig<NodeSortCol>();
		pagingConfig.setAsc(true);
		pagingConfig.setSortColumn(NodeSortCol.hostname);
		pagingConfig.setCount(Integer.MAX_VALUE);
		pagingConfig.setStartIndex(0); 
		PagingResult<NodeEntity> nodeEntities= nodeService.getPagingNodes(group, filters, pagingConfig);
	
		List<NodeEntity> nodeEntitiesList = nodeEntities.getData();
		List<Node> nodes = new ArrayList<Node>();
		for (NodeEntity nodeEntity : nodeEntitiesList) {
			Node node = NodeConvertUtil.getNodeByNodeEntity(nodeEntity);
			nodes.add(node);
		}
		return nodes;
	}
	
	private int recoveryServerWinValiConnectAndManaged(NodeDetail detail, GatewayEntity gateway) throws EdgeServiceFault{
		RemoteNodeInfo info = nodeService.tryConnectD2D(gateway.getId(), detail.getD2dConnectInfo().getProtocol().toString(), detail.getHostname(), detail.getD2dConnectInfo().getPort(), 
				detail.getD2dConnectInfo().getUsername(), detail.getD2dConnectInfo().getPassword());
		
		int i = -1;
		try (D2DConnection connection = connectionFactory.createD2DConnection(detail.getId())) {			
			connection.connect();	
			/*
			 * return code:
			 * 		0 not registered yet
			 * 		1 registered already with same Edge host
			 * 		2 registered with different Edge host
			 */
			i = connection.getService().QueryEdgeMgrStatus(CommonUtil.retrieveCurrentAppUUID(), ApplicationType.CentralManagement, EdgeCommonUtil.getLocalFqdnName());
		}
		return i;
	}
	
	
	//Windows add rps and hyper-v case
	public RecoveryServerResult validateRecoveryServerAddRPSAndHypervNode(HypervisorType type, NodeDetail detail, GatewayEntity gateway, boolean isLinux, boolean isRps)  throws EdgeServiceFault{
		NodeRegistrationInfo info = null;
		if(isRps){
			info = getRpsNodeRegistrationInfo(detail, gateway);
		}else{
			info = getHyperVNodeRegistrationInfo(detail);
		}
		
		String protocol = (Protocol.Https==info.getD2dProtocol()?"https":"http");
		
		RemoteNodeInfo remoteNode = nodeService.queryRemoteNodeInfo(gateway.getId(), detail.getId(), info.getNodeName(), 
				info.getUsername(),	info.getPassword(), protocol, info.getD2dPort());
		if(remoteNode == null){
			String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_REMOTE_NODE_NULL);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_REMOTE_NODE_NULL, errorMessage);
		}
		else if(remoteNode!=null && !remoteNode.isD2DInstalled()){
			String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_D2D_DONT_INSTALL, info.getNodeName());
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_D2D_DONT_INSTALL,  new Object[] {info.getNodeName()}, errorMessage);
		
		}else if(remoteNode.getD2DMajorVersion() == null ||"".equals(remoteNode.getD2DMajorVersion())||Integer.valueOf(remoteNode.getD2DMajorVersion()).compareTo(6)<0){
			String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_AGENT_UPPER_SIX_VERSION, info.getNodeName());
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_AGENT_UPPER_SIX_VERSION, new Object[] {info.getNodeName()}, errorMessage);
		
		}else{
			List<NodeRegistrationInfo> nodes  = new ArrayList<NodeRegistrationInfo>();
			NodeRegistrationInfo regInfo = getNodeRegistrationInfo(detail, remoteNode, gateway);
			nodes.add(regInfo);
			AddNodeResult addNodeResult = nodeService.addNodes(nodes);
			regInfo.setId(addNodeResult.getNodeIdList().get(0).getNodeIds().get(0));
			return serverAndNFScheck(regInfo, type, true);
		}
		
		
	}

	
	//windows update node
	public RecoveryServerResult validateRecoveryServerUpdateNode(HypervisorType type, NodeDetail detail, GatewayEntity gateway, boolean isLinux) throws EdgeServiceFault{
		NodeRegistrationInfo nodeRegistrationInfo = getNodeRegistrationInfo(detail, gateway);
		String protocol = (Protocol.Https==nodeRegistrationInfo.getD2dProtocol()?"https":"http");
		RemoteNodeInfo remoteNode = nodeService.queryRemoteNodeInfo(gateway.getId(), detail.getId(), nodeRegistrationInfo.getNodeName(),nodeRegistrationInfo.getUsername(),nodeRegistrationInfo.getPassword(),
				protocol, nodeRegistrationInfo.getD2dPort());
		
		if(remoteNode == null){
			String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_REMOTE_NODE_NULL);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_REMOTE_NODE_NULL, errorMessage);
		}
		else if(remoteNode!=null && !remoteNode.isD2DInstalled()){
			String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_D2D_DONT_INSTALL, nodeRegistrationInfo.getNodeName());
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_D2D_DONT_INSTALL, new Object[] { nodeRegistrationInfo.getNodeName()}, errorMessage);

		}else if(remoteNode.getD2DMajorVersion() == null ||"".equals(remoteNode.getD2DMajorVersion())||Integer.valueOf(remoteNode.getD2DMajorVersion()).compareTo(6)<0){
			String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_AGENT_UPPER_SIX_VERSION,  nodeRegistrationInfo.getNodeName());
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_AGENT_UPPER_SIX_VERSION, new Object[] { nodeRegistrationInfo.getNodeName()}, errorMessage);

		}else{
			if(remoteNode.isD2DInstalled()){
				nodeRegistrationInfo.setRegisterD2D(true);
			}
			nodeRegistrationInfo.setNodeInfo(remoteNode);
			String[] result = nodeService.updateNode(false, nodeRegistrationInfo);
			
			if (EdgeServiceErrorCode.Node_D2D_Reg_Duplicate.equals(result[0]) || EdgeServiceErrorCode.ABFunc_HaveManagedByAnotherServer.equals(result[1])) {
				nodeService.markNodeAsManaged(nodeRegistrationInfo, true);
				return serverAndNFScheck(nodeRegistrationInfo, type, false);
			}else{
				String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_UPDATE_NODE_ERROR, remoteNode.getHostEdgeServer());
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_UPDATE_NODE_ERROR, new Object[] { remoteNode.getHostEdgeServer()}, errorMessage);
			}
		}
	}
	
	
	private NodeRegistrationInfo getNodeRegistrationInfo(NodeDetail nodeInfo, GatewayEntity gateway){
		NodeRegistrationInfo nodeRegistrationInfo = new NodeRegistrationInfo();
		nodeRegistrationInfo.setId(nodeInfo.getId());
		nodeRegistrationInfo.setNodeName(nodeInfo.getHostname());
		nodeRegistrationInfo.setUsername(nodeInfo.getD2dConnectInfo().getUsername());
		nodeRegistrationInfo.setPassword(nodeInfo.getD2dConnectInfo().getPassword());
		nodeRegistrationInfo.setNodeDescription(nodeInfo.getNodeDescription());
		nodeRegistrationInfo.setD2dProtocol(nodeInfo.getD2dConnectInfo().getProtocol());
		nodeRegistrationInfo.setD2dPort(nodeInfo.getD2dConnectInfo().getPort());
		nodeRegistrationInfo.setGatewayId(gateway.getId());
		if(nodeInfo.isArcserveInstalled()){
			nodeRegistrationInfo.setRegisterARCserveBackup(true);
			
		}
		if(nodeInfo.getArcserveConnectInfo()!=null){
			if(ABFuncAuthMode.WINDOWS==nodeInfo.getArcserveConnectInfo().getAuthmode()){
				nodeRegistrationInfo.setCarootUsername(nodeInfo.getUsername());
				nodeRegistrationInfo.setCarootPassword(nodeInfo.getPassword());
				
			}else{
				if(!StringUtil.isEmptyOrNull(nodeInfo.getArcserveConnectInfo().getCauser()))
					nodeRegistrationInfo.setCarootUsername(nodeInfo.getArcserveConnectInfo().getCauser());
				if(!StringUtil.isEmptyOrNull(nodeInfo.getArcserveConnectInfo().getCapasswd()))
					nodeRegistrationInfo.setCarootPassword(nodeInfo.getArcserveConnectInfo().getCapasswd());
				if(!StringUtil.isEmptyOrNull(nodeInfo.getArcservePort()))
					nodeRegistrationInfo.setArcservePort(Integer.parseInt(nodeInfo.getArcservePort()));
					nodeRegistrationInfo.setArcserveProtocol(Protocol.parse(nodeInfo.getArcserveProtocol()));
			}
		}
		
		return nodeRegistrationInfo;
	}
	
	
	private NodeRegistrationInfo getRpsNodeRegistrationInfo(NodeDetail detail, GatewayEntity gateway){
		NodeRegistrationInfo nodeRegistrationInfo = new NodeRegistrationInfo();
		nodeRegistrationInfo.setNodeName(detail.getHostname());
		nodeRegistrationInfo.setUsername(detail.getD2dConnectInfo().getUsername());
		nodeRegistrationInfo.setPassword(detail.getD2dConnectInfo().getPassword());
		nodeRegistrationInfo.setD2dProtocol(detail.getD2dConnectInfo().getProtocol());
		nodeRegistrationInfo.setD2dPort(detail.getD2dConnectInfo().getPort());
		nodeRegistrationInfo.setGatewayId(gateway.getId());
		if(detail.isArcserveInstalled()){
			nodeRegistrationInfo.setRegisterARCserveBackup(true);
			
		}
		
		return nodeRegistrationInfo;
	}
	
	private NodeRegistrationInfo getHyperVNodeRegistrationInfo(NodeDetail detail){
		NodeRegistrationInfo nodeRegistrationInfo = new NodeRegistrationInfo();
		nodeRegistrationInfo.setNodeName(detail.getHostname());
		nodeRegistrationInfo.setUsername(detail.getUsername());
		nodeRegistrationInfo.setPassword(detail.getPassword());
		nodeRegistrationInfo.setD2dProtocol(Protocol.Http); //default
		nodeRegistrationInfo.setD2dPort(8014);  //default
		return nodeRegistrationInfo;
	}
	
	
	private NodeRegistrationInfo getNodeRegistrationInfo(NodeDetail nodeInfo, RemoteNodeInfo remoteInfo, GatewayEntity gateway){
		NodeRegistrationInfo nodeRegistrationInfo = new NodeRegistrationInfo();	
		nodeRegistrationInfo.setNodeName(nodeInfo.getHostname());
		nodeRegistrationInfo.setUsername(nodeInfo.getUsername());						
		nodeRegistrationInfo.setPassword(nodeInfo.getPassword());
		nodeRegistrationInfo.setD2dPort(remoteInfo.getD2DPortNumber());
		nodeRegistrationInfo.setD2dProtocol(remoteInfo.getD2DProtocol());
		nodeRegistrationInfo.setPhysicsMachine(true);
		nodeRegistrationInfo.setRegisterD2D(remoteInfo.isD2DInstalled());
		nodeRegistrationInfo.setNodeInfo(remoteInfo);
		nodeRegistrationInfo.setGatewayId(gateway.getId());
		nodeRegistrationInfo.setConsoleInstalled(remoteInfo.isConsoleInstalled());
		nodeRegistrationInfo.setConsolePort(remoteInfo.getConsolePortNumber());
		nodeRegistrationInfo.setConsoleProtocol(remoteInfo.getConsoleProtocol());
		
		return nodeRegistrationInfo;
	}
	
	
	//select from node list case.
	public RecoveryServerResult validateRecoveryServerVersionAndInstall(HypervisorType type, NodeDetail detail, GatewayEntity gateway, boolean isLinux) throws EdgeServiceFault{
		NodeRegistrationInfo nodeRegistrationInfo = getNodeRegistrationInfo(detail, gateway);
		String protocol = (Protocol.Https==nodeRegistrationInfo.getD2dProtocol()?"https":"http");
		RemoteNodeInfo remoteNode = nodeService.queryRemoteNodeInfo(gateway.getId(), detail.getId(), nodeRegistrationInfo.getNodeName(),nodeRegistrationInfo.getUsername(),nodeRegistrationInfo.getPassword(),
				protocol, nodeRegistrationInfo.getD2dPort());
		
		if(remoteNode == null){
//			return buildResult(RecoveryServerResult.REMOTE_NODE_NULL);
			String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_REMOTE_NODE_NULL);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_REMOTE_NODE_NULL, errorMessage);

		}
		else if(remoteNode!=null && !remoteNode.isD2DInstalled()){
//			return buildResult(RecoveryServerResult.D2D_DONT_INSTALL);
			String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_D2D_DONT_INSTALL, nodeRegistrationInfo.getNodeName());
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_D2D_DONT_INSTALL, new Object[] { nodeRegistrationInfo.getNodeName() }, errorMessage);

		}else if(remoteNode.getD2DMajorVersion() == null ||"".equals(remoteNode.getD2DMajorVersion())||Integer.valueOf(remoteNode.getD2DMajorVersion()).compareTo(6)<0){
//			return buildResult(RecoveryServerResult.AGENT_UPPER_SIX_VERSION);
			String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_AGENT_UPPER_SIX_VERSION, nodeRegistrationInfo.getNodeName());
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_AGENT_UPPER_SIX_VERSION, new Object[] {nodeRegistrationInfo.getNodeName()}, errorMessage);

		}else{
			nodeRegistrationInfo.setNodeInfo(remoteNode);
			return serverAndNFScheck(nodeRegistrationInfo, type, false);
			
		}
	}

	public RecoveryServerResult serverAndNFScheck(NodeRegistrationInfo regInfo, 
			HypervisorType type, boolean addNode) throws EdgeServiceFault {
		logger.debug("serverAndNFScheck() regInfo.id:" + regInfo.getId());
				
		PrecheckResult  precheckResult = checkRecoveryServerOS(type, regInfo);
		if(precheckResult.result){
			RecoveryServerResult valiResult = new RecoveryServerResult();
			valiResult.setHasError(false);
			valiResult.setRecoveryServer(regInfo);
			valiResult.setAddNode(addNode);
			return valiResult;
		}else{
			if(!precheckResult.criteria.isWindowsServerCapable()){
				String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_WINDOWS_2008_R2);
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_WINDOWS_2008_R2, errorMessage);
			}else if(!precheckResult.criteria.isWindowsNFSFeatureInstalled()){
				String errorMessage = MessageReader.getErrorMessage(EdgeServiceErrorCode.INSTANTVM_NFS_NOT_INSTALL, regInfo.getNodeName(), regInfo.getNodeName());
//				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.INSTANTVM_NFS_NOT_INSTALL, errorMessage);
				
				RecoveryServerResult valiResult = new RecoveryServerResult();
				valiResult.setHasError(true);
				valiResult.setErrorCode(EdgeServiceErrorCode.INSTANTVM_NFS_NOT_INSTALL);
				valiResult.setErrorMessage(errorMessage);
				valiResult.setRecoveryServer(regInfo);
				valiResult.setAddNode(addNode);
				return valiResult;
			}else{
				RecoveryServerResult valiResult = new RecoveryServerResult();
				valiResult.setHasError(false);
				valiResult.setRecoveryServer(regInfo);
				valiResult.setAddNode(addNode);
				return valiResult;
			}
		}
	}
	
}
