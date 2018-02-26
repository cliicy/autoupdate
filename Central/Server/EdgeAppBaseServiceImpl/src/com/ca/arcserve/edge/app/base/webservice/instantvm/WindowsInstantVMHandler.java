package com.ca.arcserve.edge.app.base.webservice.instantvm;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.instantvm.AdvancedIPConfig;
import com.ca.arcflash.instantvm.DNSUpdaterParameter;
import com.ca.arcflash.instantvm.Gateway;
import com.ca.arcflash.instantvm.HyperVInfo;
import com.ca.arcflash.instantvm.HypervisorInfo;
import com.ca.arcflash.instantvm.HypervisorType;
import com.ca.arcflash.instantvm.IPAddressInfo;
import com.ca.arcflash.instantvm.InstantVHDInfo;
import com.ca.arcflash.instantvm.InstantVHDResult;
import com.ca.arcflash.instantvm.InstantVMConfig;
import com.ca.arcflash.instantvm.InstantVMNode;
import com.ca.arcflash.instantvm.NetworkInfo;
import com.ca.arcflash.instantvm.SessionInfo;
import com.ca.arcflash.instantvm.VMInfo;
import com.ca.arcflash.instantvm.VMWareInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.webservice.IInstantVMService;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcserve.edge.app.base.common.D2DServiceUtils;
import com.ca.arcserve.edge.app.base.common.D2DServiceUtils.D2DServiceConnectInfo;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.InstantVMConnection;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationBrowser;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.AdapterForInstantVM;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.DNSInfoForIVM;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.HypervisorWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.IPForInstantVM;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVHDOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVM;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVmStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StartInstantVHDOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StartInstantVMOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StopInstantVHDOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.VMInfoInCPM;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.VMWareInfoForIVM;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DNSUpdateSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Hypervisor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PlanDestinationType;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.RecoveryPointInformationForCPM;
import com.ca.arcserve.edge.app.base.webservice.recoverypoints.RecoveryPointBrowseUtil;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;
import com.ca.arcserve.edge.app.rps.webservice.datastore.DataStoreManager;

public class WindowsInstantVMHandler {
	private static final Logger logger = Logger.getLogger(WindowsInstantVMHandler.class);
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	public InstantVMOperationResult handleWindowsInstantVM ( NodeRegistrationInfo proxyInfo, RecoveryPointInformationForCPM rpForCPM, 
			StartInstantVMOperation operationPara ) throws EdgeServiceFault {
		InstantVMConfig configure = new InstantVMConfig();
		configure.setNeedInstallNFSFeature(operationPara.isNeedInstallNFS());
		configure.setNeedPowerOn( operationPara.getVmInfo().isNeedpowerOn() );
		ProtectedNodeInDestination protectedNode = rpForCPM.getProtectedNode();
		configure.setNodeHostname(  protectedNode.getNodeName() );
		configure.setNodeName( protectedNode.getNodeName() );
		configure.setNodeUUID( protectedNode.getNodeUuid() );
		configure.setVmConfigPath( operationPara.getVmInfo().getVmConfigPath() );

		// Currently there's only one node yet
		InstantVMNode node = new InstantVMNode();
		node.setNodeHostname(  protectedNode.getNodeName() );
		node.setNodeName( protectedNode.getNodeName() );
		node.setNodeUUID( protectedNode.getNodeUuid() );
		node.setSessionInfo(convert2SessionInfo(rpForCPM));
		node.setVmInfo(convertToVMInfo(operationPara.getVmInfo()));
		
		List<InstantVMNode> nodes = new ArrayList<InstantVMNode>();
		nodes.add(node);
		configure.setInstantVMNodes(nodes);
		
		HypervisorWrapper hypervisorWrapper = operationPara.getHypervisorWrapper();
		configure.setHypervisorInfo( getWindowsInstanVMHypervisor( hypervisorWrapper ) );

		configure.setHypervisorType( hypervisorWrapper.getHypervisorType() );
		
		configure.setProxyHostname(proxyInfo.getNodeName());
		//agent get the info by itself.
//		configure.setProxyProtocol(proxyInfo.getD2dProtocol().toString());
//		configure.setProxyPort(proxyInfo.getD2dPort());
//		configure.setProxyUsername(proxyInfo.getUsername());
//		configure.setProxyPassword(proxyInfo.getPassword());
		
		InstantVMOperationResult result = new InstantVMOperationResult(false);
		try(InstantVMConnection connection = connectionFactory.createInstantVMConnection(proxyInfo.getId())){
			connection.connect();
			IInstantVMService service = connection.getService();
			logger.info("connect "+proxyInfo.getNodeName() +"["+proxyInfo.getId()+"]"+ " to start InstantVM: "+InstantVMServiceUtil.printObject(configure));
			String ivmJobUUID = service.startInstantVM(configure);
			logger.info(proxyInfo.getNodeName() +"["+proxyInfo.getId()+"]"+ " startInstantVM() return: "+ivmJobUUID);
			if(StringUtil.isEmptyOrNull(ivmJobUUID)){
				InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, rpForCPM.getProtectedNode().getNodeId(), rpForCPM.getProtectedNode().getNodeName(), EdgeCMWebServiceMessages.getMessage("startInstantVMFailed", rpForCPM.getProtectedNode().getNodeName()), JobType.JOBTYPE_START_INSTANT_VM);
			}else {
				InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Information, rpForCPM.getProtectedNode().getNodeId(), rpForCPM.getProtectedNode().getNodeName(), EdgeCMWebServiceMessages.getMessage("startInstantVMSuccessful", rpForCPM.getProtectedNode().getNodeName()), JobType.JOBTYPE_START_INSTANT_VM);
				result.setResult(true);
				result.setIVMJobUUID(ivmJobUUID);
				InstantVM vm = generateInstantVM(ivmJobUUID, proxyInfo, rpForCPM, operationPara);
				InstantVMManager.getInstance().save(vm);
			}
		}catch(Exception e){
			logger.error(e);
			InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, rpForCPM.getProtectedNode().getNodeId(), rpForCPM.getProtectedNode().getNodeName(), EdgeCMWebServiceMessages.getMessage("startInstantVMFailed", rpForCPM.getProtectedNode().getNodeName()), JobType.JOBTYPE_START_INSTANT_VM);
			throw e;
		}
		return result;
	}

	private SessionInfo convert2SessionInfo(RecoveryPointInformationForCPM rpForCPM) throws EdgeServiceFault {
		// To support multiple nodes in a single Instant VM job
		DestinationBrowser browser = rpForCPM.getBrowser();
		///handle session
		SessionInfo session = new SessionInfo();
		String sessionNumber = RecoveryPointBrowseUtil.getInstance().parseSessionFromRecoveryPointPath( rpForCPM );
		session.setSessionNum( sessionNumber ); 
		session.setBackupDestination( rpForCPM.getNodeBackupDestination() );
		session.setSessionPassword( rpForCPM.getSessionPassword() );
		session.setSessionUUID(rpForCPM.getRecoveryPoint().getSessionGuid());
		
		DestinationInfo locationInfo = RecoveryPointBrowseUtil.getInstance().getDestinationWithPasswordForWin(browser);
		session.setUserName( locationInfo.getUserName() );
		session.setPassword( locationInfo.getPassword() );
		session.setRPSSession( rpForCPM.getBrowser().getDestinationType() == PlanDestinationType.RPS ? true :false);
		//for rps we provide backup job operator information; for shared folder we cannot provide now
		if( browser.getDestinationType() == PlanDestinationType.RPS ) {
			D2DServiceConnectInfo connInfo = D2DServiceUtils.getD2DConnectInfo( browser.getDestinationId() );
			session.setRPSSession(true);
			session.setBackupOperatorHostname( connInfo.hostname );
			session.setBackupOperatorUserName( connInfo.username );
			session.setBackupOperatorPassword( connInfo.password );
			session.setBackupOperatorProtocol( connInfo.protocol );
			session.setBackupOperatorPort( connInfo.port );
			session.setBackupOperatorUUID( connInfo.uuid );
			session.setBackupOperatorAuthID( connInfo.authuuid );
			session.setRpsDatastoreUUID(browser.getSubDest());
		}else if(browser.getDestinationType() == PlanDestinationType.LocalDisk){
			D2DServiceConnectInfo connInfo = D2DServiceUtils.getD2DConnectInfo(browser.getBrowserId());
			//path D:\Destination -> \\hostname\D$\Destination
			session.setBackupDestination("\\\\"+connInfo.hostname+"\\"+rpForCPM.getNodeBackupDestination().replace(":", "$"));
			session.setUserName(connInfo.username);
			session.setPassword(connInfo.password); 
		}
		return session;
	}
	
	public InstantVHDOperationResult startWindowsInstantVHD(StartInstantVHDOperation para, int proxyID) throws EdgeServiceFault {
		InstantVHDOperationResult result = new InstantVHDOperationResult();
		InstantVMConfig config = new InstantVMConfig();
		config.setJobType(InstantVMConfig.START_INSTANT_VHD_JOB);
		config.setNeedPowerOn(false);
		String nodeName = para.getNodeName();
		String nodeUUID = para.getNodeUUID();
		config.setNodeName(nodeName);
		config.setNodeHostname(nodeName);
		config.setNodeUUID(nodeUUID);
		
		SessionInfo session = new SessionInfo();
		session.setSessionNum(String.format("S%010d", para.getSessionNum()));
		session.setBackupDestination(para.getBackupDestination());
		session.setSessionPassword(para.getSessionPassword());
		session.setUserName(para.getUserName());
		session.setPassword(para.getUserPassword());
		
		InstantVMNode node = new InstantVMNode();
		node.setNodeName(nodeName);
		node.setNodeHostname(nodeName);
		node.setNodeUUID(nodeUUID);
		node.setSessionInfo(session);
		
		VMInfo vmInfo = new VMInfo();
		String vhdPath = para.getVhdPath();
		vmInfo.setVmPath(vhdPath);
		config.setVmConfigPath(vhdPath);
		node.setVmInfo(vmInfo);
		
		List<InstantVMNode> nodes = new ArrayList<InstantVMNode>();
		nodes.add(node);
		config.setInstantVMNodes(nodes);
		config.setHypervisorType(HypervisorType.HYPERV);
		int diskType = para.getDiskType();
		if (diskType == 0)
			diskType = 2;
		config.setDiskType(diskType);

		// TODO set more parameters
		try(InstantVMConnection connection = connectionFactory.createInstantVMConnection(proxyID)){
			connection.connect();
			IInstantVMService service = connection.getService();
			InstantVHDResult instantVHDResult = service.startInstantVHD(config, para.getTimeout());
			String jobUUID = instantVHDResult.getJobUUID();
			if (StringUtil.isEmptyOrNull(jobUUID)) {
				logger.error("Fail to start instant VHD job.");
			} else {
				result.setJobUUID(jobUUID);
				logger.info(String.format("Start instant VHD job %s successfully.", jobUUID));
			}
			List<InstantVHDInfo> vhdInfoList = instantVHDResult.getVhdList();
			List<String> vhdList = new ArrayList<String>();
			if (vhdInfoList == null || vhdInfoList.size() == 0) {
				logger.warn(String.format("No VHD is created for instant VHD job %s.", jobUUID));
			} 
			else
			{
				long maxDiskSize = 0;
				for (InstantVHDInfo vhdInfo : vhdInfoList) {
					String vhdFile = vhdInfo.getVhdFile();
					vhdList.add(vhdFile);
					if (vhdInfo.isBootVhd())
						result.setBootVHD(vhdFile);
					if (vhdInfo.isSysVhd())
						result.setSysVHD(vhdFile);
					long diskSize = vhdInfo.getDiskSize();
					if (diskSize > maxDiskSize)
						maxDiskSize = diskSize;
				}
				result.setMaxDiskSize(maxDiskSize);
			}
			result.setUEFI(instantVHDResult.isUEFI());
			result.setErrCode(instantVHDResult.getErrCode());
			result.setErrString(instantVHDResult.getErrString());
			result.setVhdList(vhdList);
			// TODO Save Instant VHD job information to local console.
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		return result;
	}
	
	public InstantVHDOperationResult stopWindowsInstantVHD(StopInstantVHDOperation para, int proxyID) throws EdgeServiceFault {
		InstantVHDOperationResult result = new InstantVHDOperationResult();
		try(InstantVMConnection connection = connectionFactory.createInstantVMConnection(proxyID)){
			connection.connect();
			IInstantVMService service = connection.getService();
			String jobUUID = para.getJobUUID();
			long ret = service.stopInstantVM(jobUUID, true);
			result.setErrCode((int)ret);
			if (ret != 0)
				logger.equals(String.format("Fail to stop instant VHD job %s.", jobUUID));
			else
				logger.info(String.format("Stop instant VHD job %s successfully.", jobUUID));
		}catch (Exception e) {
			logger.error(e);
			throw e;
		}
		return result;
	}

	private VMInfo convertToVMInfo( VMInfoInCPM  vmInCPM ){
		VMInfo vmInfo = new VMInfo();
		vmInfo.setCPU( vmInCPM.getCPU() );
		vmInfo.setMemorySize( vmInCPM.getMemorySize() );
		//vmInfo.setVmNamePrefix( vmInCPM.getVmNamePrefix() );
		vmInfo.setVmDisplayName( vmInCPM.getVmDisplayName() );
		List<NetworkInfo>  networks = new ArrayList<NetworkInfo>();
		vmInfo.setNetworkInfo(networks);
		
		if(vmInCPM.getHostname()!=null && !"".equals(vmInCPM.getHostname())){
			vmInfo.setNeedChangeHostname(true);
			vmInfo.setNewHostname(vmInCPM.getHostname());
			vmInfo.setDomainAccount(vmInCPM.getUsername());
			vmInfo.setDomainPassword(vmInCPM.getPassword());
		}else{
			vmInfo.setNeedChangeHostname(false);
		}
		
		if(vmInCPM.getAdapterSetting()==null){
			NetworkInfo network = new NetworkInfo();
			networks.add(network);;
		}else{
			for( AdapterForInstantVM adapter : vmInCPM.getAdapterSetting() ) {
				NetworkInfo network = new NetworkInfo();
				
				network.setAdapterType(adapter.getAdapterType());
				network.setVirtualNetwork(adapter.getNetworkType());
				network.setVirtualNetworkID(adapter.getNetworkID());
				network.setIsVDS(adapter.isVDS());
				network.setAdvancedIPConfig(new AdvancedIPConfig());
				//ip
				network.setUseDHCP(adapter.isDhcp());
				if( !adapter.isDhcp() ) {
					for( IPForInstantVM ipInCPM : adapter.getIpInfos() ) {
						IPAddressInfo ip = new IPAddressInfo();
						ip.setIp(ipInCPM.getIp() );
						ip.setSubnet( ipInCPM.getSubnet() );
						network.getAdvancedIPConfig().getIpAddresses().add(ip);
					}
				}
				/// gate way
				for( String gateway: adapter.getGateWayList() ) {
					Gateway gw = new Gateway();
					gw.setGatewayAddress(gateway);
					network.getAdvancedIPConfig().getGateways().add( gw );
				}
				//dns
				network.setDynamicDNS(adapter.isAutodns());
				if( !adapter.isAutodns() ) {
					network.getAdvancedIPConfig().setDnses( adapter.getDnsList() );
				}
				///wins
				network.getAdvancedIPConfig().setWins( adapter.getWinsList() );
				networks.add(network);
			}
		}
		vmInfo.setDnsParameters(convertToDnsParameters(vmInCPM.getUpdateDNSInfo()));
		
		return vmInfo;
	}
	
	private List<DNSUpdaterParameter> convertToDnsParameters(DNSInfoForIVM updateDNSInfo) {
		if(updateDNSInfo == null || updateDNSInfo.getDnsList() == null){
			return null;
		}
		List<DNSUpdaterParameter> result = new ArrayList<DNSUpdaterParameter>();
		for(DNSUpdateSetting dns : updateDNSInfo.getDnsList()){
			DNSUpdaterParameter param = new DNSUpdaterParameter();
			param.setDns(dns.getDnsAddress());
			param.setHostIp(dns.getIpAddresses());
			param.setTtl(updateDNSInfo.getTimeToLive());
			param.setDnsServerType(updateDNSInfo.getDnsType());
			param.setUsername(updateDNSInfo.getDnsAccount());
			param.setCredential(updateDNSInfo.getDnsPassword());
			param.setKeyFile(updateDNSInfo.getKeyFilePath());
			result.add(param);
		}
		return result;
	}

//	private HypervisorInfo getWindowsInstanVMHypervisor( HypervisorWrapper hypervisorWrapper ) {
//		HypervisorType hypervisorType = hypervisorWrapper.getHypervisorType();
//		Hypervisor hypervisor = hypervisorWrapper.getHyperVisor();
//		VWWareESXNode  esxNode = hypervisorWrapper.getEsxHost();
//		ResourcePoolForInstantVM resPol =hypervisorWrapper.getPoolModel();
//		HypervisorInfo hypervisorForWinVM = null;  
//		if( hypervisorType == HypervisorType.VMWARE ) {
//			VMWareInfo hypervisorInnerUse = new VMWareInfo(); 
//			hypervisorInnerUse.setDatacenter( esxNode.getDcDisplayName() );   
//			hypervisorInnerUse.setDatacenterRefID( esxNode.getDataCenter() ); //the code in HAService assign esxhost.dc() a dcrefId;
//			/**
//			 * if it's a v-center we input esxhost name; if it's an esx, we direct input server name 
//			 */
//			hypervisorInnerUse.setEsxHost( hypervisor.isVCenter()? hypervisor.getEsxHost() : hypervisor.getServerName()  ); 
//			hypervisorInnerUse.setEsxHostRefID( esxNode.getEsxMoID() );
//			hypervisorInnerUse.setPort( hypervisor.getPort() );
//			if( hypervisor.getProtocol() != null ) {
//				hypervisorInnerUse.setProtocol( hypervisor.getProtocol().toString() );
//			}
//			if( resPol !=null ) {
//				hypervisorInnerUse.setResourcePool( resPol.getPoolName() );
//				hypervisorInnerUse.setResourcePoolRefID( resPol.getPoolRef() );
//			}
//			
//			hypervisorInnerUse.setHostname( hypervisor.getServerName() );
//			hypervisorInnerUse.setUserName( hypervisor.getUsername() );
//			hypervisorInnerUse.setPassword( hypervisor.getPassword() ); 
//			hypervisorForWinVM = hypervisorInnerUse;
//		}
//		else { ///hyperv
//			hypervisorForWinVM = new HyperVInfo();
//			hypervisorForWinVM.setHostname( hypervisor.getServerName() );
//			hypervisorForWinVM.setUserName( hypervisor.getUsername() );
//			hypervisorForWinVM.setPassword( hypervisor.getPassword() ); 
//		}
//		return hypervisorForWinVM;
//	}
	
	private HypervisorInfo getWindowsInstanVMHypervisor( HypervisorWrapper hypervisorWrapper ) {
		HypervisorType hypervisorType = hypervisorWrapper.getHypervisorType();
		Hypervisor hypervisor = hypervisorWrapper.getHyperVisor();
		HypervisorInfo hypervisorForWinVM = null;  
		if( hypervisorType == HypervisorType.VMWARE ) {
			VMWareInfo info = new VMWareInfo(); 
			VMWareInfoForIVM model = hypervisorWrapper.getVmWareInfo();
			info.setResourcePool(model.getResourcePool());
			info.setResourcePoolRefID(model.getResourcePoolRefID());
			info.setCluster(model.isCluster());
			info.setClusterName(model.getClusterName());
			info.setClusterRefID(model.getClusterRefID());
			info.setEsxHost(model.getEsxHost());
			info.setEsxHostRefID(model.getEsxHostRefID());
			info.setDatacenter(model.getDatacenter());
			info.setDatacenterRefID(model.getDatacenterRefID());

			info.setPort( hypervisor.getPort() );
			if( hypervisor.getProtocol() != null ) {
				info.setProtocol( hypervisor.getProtocol().toString() );
			}
			
			info.setHostname( hypervisor.getServerName() );
			info.setUserName( hypervisor.getUsername() );
			info.setPassword( hypervisor.getPassword() ); 
			hypervisorForWinVM = info;
		}else { ///hyperv
			hypervisorForWinVM = new HyperVInfo();
			hypervisorForWinVM.setHostname( hypervisor.getServerName() );
			hypervisorForWinVM.setUserName( hypervisor.getUsername() );
			hypervisorForWinVM.setPassword( hypervisor.getPassword() ); 
		}
		return hypervisorForWinVM;
	}
	
	private InstantVM generateInstantVM(String ivmJobUUID, NodeRegistrationInfo proxyInfo, RecoveryPointInformationForCPM rpForCPM, StartInstantVMOperation operationPara) {
		InstantVM vm = new InstantVM();
		vm.setUuid(ivmJobUUID);
		vm.setName(operationPara.getVmInfo().getVmDisplayName());
		vm.setNodeId(rpForCPM.getProtectedNode().getNodeId());
		vm.setNodeUuid(rpForCPM.getProtectedNode().getNodeUuid());
		vm.setNodeName(rpForCPM.getProtectedNode().getNodeName());
		vm.setRecoveryPoint(rpForCPM.getRecoveryPoint());
		vm.setVmLocation(operationPara.getHypervisorWrapper().getVmLocation());
		vm.setRecoveryServerType(InstantVM.RECOVERY_SERVER_TYPE_WINDOWS);
		vm.setRecoveryServerId(proxyInfo.getId());
		vm.setRecoveryServer(proxyInfo.getNodeName());
		vm.setGatewayId(proxyInfo.getGatewayId().getRecordId());
		vm.setDescription(operationPara.getVmInfo().getDescription());
		vm.setStatus(InstantVmStatus.Unknown);
		
		InstantVMDetail detail = new InstantVMDetail();
		vm.setDetail(detail);
		DestinationBrowser browser = rpForCPM.getBrowser();
		switch (browser.getBrowserType()) {
		case DataStoreUsingRPS:
			detail.setSourceType(InstantVMDetail.SOURCE_TYPE_RPS);
			detail.setRpsServerId(browser.getDestinationId());
			detail.setDataStoreUuid(browser.getSubDest());
			try {
				D2DServiceConnectInfo connInfo = D2DServiceUtils.getD2DConnectInfo(browser.getDestinationId());
				detail.setRpsServer(connInfo.hostname);
				DataStoreSettingInfo dsInfo = DataStoreManager.getDataStoreManager().getDataStoreByGuid(browser.getDestinationId(), browser.getSubDest());
				detail.setDataStore(dsInfo.getDisplayName());
			} catch (Exception e) {
				logger.error("can't get datastore info from DB.", e);
			}
			break;
		case SharedFolderUsingRPS:
		case SharedFolderUsingLinuxServer:
			detail.setSourceType(InstantVMDetail.SOURCE_TYPE_SHARED_FOLDER);
			detail.setSharedFolderId(browser.getDestinationId());
			try {
				DestinationInfo locationInfo = RecoveryPointBrowseUtil.getInstance().getDestinationWithPasswordForWin(browser);
				detail.setSharedFolder(locationInfo.getPath());
			} catch (Exception e) {
				logger.error("can't get share folder info from DB.", e);
			}
			break;
		default:
		}
		HypervisorWrapper hw = operationPara.getHypervisorWrapper();
		Hypervisor hypervisor = hw.getHyperVisor();
		detail.setHypervisor(hypervisor.getServerName());
		switch(hw.getHypervisorType()){
		case HYPERV:
			detail.setHypervisorType(InstantVMDetail.HYPERVISOR_TYPE_HYPERV);
			break;
		case VMWARE:
			detail.setHypervisorType(InstantVMDetail.HYPERVISOR_TYPE_VMWARE);
			detail.setvCenter(hypervisor.isVCenter());
			detail.setVmWareInfo(hw.getVmWareInfo());
			break;
		case UNKNOWN:
			detail.setHypervisorType(InstantVMDetail.HYPERVISOR_TYPE_UNKNOWN);
			break;
		default:
			detail.setHypervisorType(InstantVMDetail.HYPERVISOR_TYPE_UNKNOWN);
		}
		detail.setVmInfo(operationPara.getVmInfo());
//		detail.setVmFilePath(operationPara.getVmInfo().getVmConfigPath());
//		detail.setCpuCount(operationPara.getVmInfo().getCPU());
//		detail.setMemorySize(operationPara.getVmInfo().getMemorySize());
//		detail.setAdapterSetting(operationPara.getVmInfo().getAdapterSetting());
		
		return vm;
	}
}
