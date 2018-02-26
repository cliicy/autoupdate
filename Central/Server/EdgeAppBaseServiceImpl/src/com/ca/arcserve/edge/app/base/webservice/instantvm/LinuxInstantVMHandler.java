package com.ca.arcserve.edge.app.base.webservice.instantvm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ca.arcflash.instantvm.HypervisorType;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcserve.edge.app.base.common.D2DServiceUtils;
import com.ca.arcserve.edge.app.base.common.D2DServiceUtils.D2DServiceConnectInfo;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationBrowser;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationBrowser.RPBrowserType;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.AdapterForInstantVM;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.HypervisorWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVM;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVmStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StartInstantVMOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.VMInfoInCPM;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.VMWareInfoForIVM;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Hypervisor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PlanDestinationType;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.RecoveryPointInformationForCPM;
import com.ca.arcserve.edge.app.base.webservice.recoverypoints.RecoveryPointBrowseUtil;
import com.ca.arcserve.edge.app.rps.webservice.datastore.DataStoreManager;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.data.BackupLocationInfo;
import com.ca.arcserve.linuximaging.webservice.data.D2DTime;
import com.ca.arcserve.linuximaging.webservice.data.JobScript;
import com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint;
import com.ca.arcserve.linuximaging.webservice.data.RestoreScript;
import com.ca.arcserve.linuximaging.webservice.data.ServerInfo;
import com.ca.arcserve.linuximaging.webservice.data.restore.VirtualMachineRestoreTarget;
import com.ca.arcserve.linuximaging.webservice.data.restore.VirtualMachineSetting;
import com.ca.arcserve.linuximaging.webservice.data.restore.VirtualNetworkInfo;
import com.ca.arcserve.linuximaging.webservice.data.restore.VirtualizationServer;
import com.ca.arcserve.linuximaging.webservice.data.restore.VirtualizationServerType;

public class LinuxInstantVMHandler {
//	private RecoveryPointServiceImpl rpImpl = new RecoveryPointServiceImpl();
	private static final Logger logger = Logger.getLogger(LinuxInstantVMHandler.class);
	public InstantVMOperationResult handleLinuxInstantVM(  NodeRegistrationInfo proxyInfo, RecoveryPointInformationForCPM rpForCPM, 
			StartInstantVMOperation operationPara ) throws EdgeServiceFault {
		D2DServiceConnectInfo linuxServerInfo = D2DServiceUtils.getD2DConnectInfo( proxyInfo.getId() );
		try(LinuxD2DConnection connection = D2DServiceUtils.createLinuxService(linuxServerInfo)){
			ILinuximagingService service = connection.getService();
			RestoreScript script = new RestoreScript();
			script.setNeedGenerateJobHistory(true);
	        script.setJobName( operationPara.getVmInfo().getVmDisplayName() + "_" + System.currentTimeMillis() );

	        ///backup location
	        BackupLocationInfo locationForLinux = RecoveryPointBrowseUtil.getInstance().getDestinationWithPasswordForLinux(rpForCPM.getBrowser());
	        if(rpForCPM.getBrowser().getDestinationType() ==  PlanDestinationType.SharedFolder){
	        	if(rpForCPM.getProtectedNode().getDestination().startsWith("\\\\") || rpForCPM.getProtectedNode().getDestination().startsWith("//")){
	        		locationForLinux.setType(BackupLocationInfo.BACKLOCATION_TYPE_CIFS);
		        }else{
		        	locationForLinux.setType(BackupLocationInfo.BACKLOCATION_TYPE_NFS);
		        }
	        }
	        
	        
	        script.setBackupLocationInfo( locationForLinux );
	        
	        //recovery point;
	        if(rpForCPM.getBrowser().getDestinationType() ==  PlanDestinationType.RPS){
	        	script.setMachine( rpForCPM.getProtectedNode().getNodeName()+"["+rpForCPM.getProtectedNode().getNodeUuid()+"]" );
	        }else{
	        	script.setMachine( rpForCPM.getProtectedNode().getNodeName() );
	        }
	        
	        RecoveryPoint linuxRP = new RecoveryPoint();
	        //linux agent back up server will get this infomation itself.
//	        if( !rpForCPM.isBackByWindowsAgent() ) {
//	        	 //linux node browsed by linux servre which return RecoveryPointForLinux();( may backup to nfs or cifs share; )
//	            if( rpForCPM.getRecoveryPoint() instanceof RecoveryPointForLinux ) { 
//	            	linuxRP = RecoveryPointBrowseUtil.getInstance().convertToLinuxRecoveryPoint( rpForCPM.getRecoveryPoint() );
//	            }
//	            ///linux backup to datastore or cifs folder(?) but browsed by windows agent; so we need refetch recovery point here; 
//	            //beause rp browsed using windows agent lack some important information for linux instantvm
//	            else {  
//	            	rpForCPM.getBrowser().setBrowserId(proxyInfo.getId());
//	            	//TODO
//	            	linuxRP = rpImpl.reGetLinuxAgentRPFromAgentRP( rpForCPM ); 
//	            }
//	            List<RecoveryPointItem>  rpitems = service.getDiskInfo( script.getBackupLocationInfo(), script.getMachine(), linuxRP );
//	            linuxRP.setItems( rpitems );
//	        }
//	        else { //hbbu node;
//	        	linuxRP = RecoveryPointBrowseUtil.getInstance().convertToLinuxRecoveryPoint( rpForCPM.getRecoveryPoint() );
//	        }       
	        linuxRP.setEncryptionPassword( rpForCPM.getSessionPassword() );
//	        linuxRP.setName(rpForCPM.getRecoveryPoint().getName());
	        linuxRP.setName(rpForCPM.getRecoveryPoint().getPath().replace("\\", "/"));
	        script.setRecoveryPoint( linuxRP );
	        D2DTime time = new D2DTime();
	        time.setRunNow(true);
	        script.setStartTime(time);
	        script.setRestoreType(JobScript.RESTORE_VM);
	        
//	        if( operationPara.getHypervisorWrapper().getHypervisorType() == HypervisorType.HYPERV){
//	        	ServerInfo serverInfo = new ServerInfo();
//		        serverInfo.setPort(5985);
//		        serverInfo.setProtocol("http");
//		        script.setServerInfo(serverInfo);
//	        }
	        
	        
	        List<VirtualMachineRestoreTarget> targetList = new ArrayList<VirtualMachineRestoreTarget>();
	        VirtualMachineRestoreTarget target = new VirtualMachineRestoreTarget();
	        
	        target.setInstant(true);
	        target.setBootLater(!operationPara.getVmInfo().isNeedpowerOn());
	        target.setServerInfo( getLinuxHyperVisor( operationPara.getHypervisorWrapper() ) );
	        target.setVmSetting( getLinuxVMModel(operationPara.getHypervisorWrapper(),operationPara.getVmInfo() , linuxRP ) );
	        targetList.add(target);
	        script.setVmRestoreTargetList(targetList);
	        
	        // if hbbu and cifs, need rps server connection information
	        if(RPBrowserType.SharedFolderUsingRPS.equals(rpForCPM.getBrowser().getBrowserType()) && !rpForCPM.isWindowsSession()){
	        	script.setServerInfo(getServerInfo(rpForCPM.getBrowser()));
	        }
	        
	        logger.info("connect "+proxyInfo.getNodeName() +"["+proxyInfo.getId()+"]"+ " to submit linux InstantVMJob: "+InstantVMServiceUtil.printObject(script));
	        String ivmJobUUID = service.submitInstantVMJob(script);
	        logger.info(proxyInfo.getNodeName() +"["+proxyInfo.getId()+"]"+ " submitInstantVMJob() return: "+ivmJobUUID);
	        
	        InstantVMOperationResult result = new InstantVMOperationResult( false );
	        
	        if(StringUtil.isEmptyOrNull(ivmJobUUID)){
	        	InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, rpForCPM.getProtectedNode().getNodeId(), rpForCPM.getProtectedNode().getNodeName(), EdgeCMWebServiceMessages.getMessage("startInstantVMFailed", rpForCPM.getProtectedNode().getNodeName()), JobType.JOBTYPE_START_INSTANT_VM);
			}else {
				InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Information, rpForCPM.getProtectedNode().getNodeId(), rpForCPM.getProtectedNode().getNodeName(), EdgeCMWebServiceMessages.getMessage("startInstantVMSuccessful", rpForCPM.getProtectedNode().getNodeName()), JobType.JOBTYPE_START_INSTANT_VM);
				result.setResult(true);
				result.setIVMJobUUID(ivmJobUUID);
				InstantVM vm = generateInstantVM(ivmJobUUID, proxyInfo, rpForCPM, operationPara);
				InstantVMManager.getInstance().save(vm);
			}
			
	        return result;
		}catch(Exception e){
			logger.error(e);
			InstantVMServiceUtil.generateInstantVMActivityLog(Severity.Error, rpForCPM.getProtectedNode().getNodeId(), rpForCPM.getProtectedNode().getNodeName(), EdgeCMWebServiceMessages.getMessage("startInstantVMFailed", rpForCPM.getProtectedNode().getNodeName()), JobType.JOBTYPE_START_INSTANT_VM);
			throw e;
		}
	}
	
	/**
	 * get RPS info
	 * @param browser
	 * @return
	 * @throws EdgeServiceFault
	 */
	private ServerInfo getServerInfo(DestinationBrowser browser) throws EdgeServiceFault {
		ServerInfo serverInfo = new ServerInfo();
		try {
			D2DServiceConnectInfo connInfo = D2DServiceUtils.getD2DConnectInfo( browser.getBrowserId() );
			serverInfo.setName(connInfo.hostname);
			serverInfo.setUser(connInfo.username);
			serverInfo.setPassword(connInfo.password);
			serverInfo.setPort(connInfo.port);
			serverInfo.setProtocol(connInfo.protocol);
		} catch (EdgeServiceFault e) {
			 logger.error("get serverInfo error");
			 logger.error(e);
			 throw e;
		}
		
		return serverInfo;
	}
	
	private VirtualizationServer getLinuxHyperVisor( HypervisorWrapper hypervisorWrapper  ) {
	      VirtualizationServer server = new VirtualizationServer();
	    Hypervisor vmwareInfo = hypervisorWrapper.getHyperVisor();
		server.setServerName( vmwareInfo.getServerName() );
		server.setUserName( vmwareInfo.getUsername() );
		server.setPassword( vmwareInfo.getPassword() );
		server.setPort( (int)vmwareInfo.getPort() ); 
		server.setProtocol( vmwareInfo.getProtocol() == Protocol.Http ? 0:1 ); ///http 0; https 1
	    if( hypervisorWrapper.getHypervisorType() == HypervisorType.VMWARE ) {
	    	 //[liuyu07] original logic didn't consider cluster, nested resource pool/virtual apps, need rebuild in the future
//			 VWWareESXNode esxNode =  hypervisorWrapper.getEsxHost();
//		     server.setServerType(VirtualizationServerType.ESX.getValue());
//		     server.setSubServer( esxNode.getEsxName() );
//		     server.setSubServerDcName( esxNode.getDcDisplayName() ); 
		     VMWareInfoForIVM model = hypervisorWrapper.getVmWareInfo();
		     server.setServerType(VirtualizationServerType.ESX.getValue());
		     server.setSubServer(model.getEsxHost());
		     server.setSubServerDcName(model.getDatacenter()); 
		} else if (hypervisorWrapper.getHypervisorType() == HypervisorType.HYPERV){
		     server.setServerType(VirtualizationServerType.HYPERV.getValue());
		     
		     //hard code. when lu yu check in the code. this is not useful.
		     server.setPort(-1); 
			 server.setProtocol(0); //http 0; https 1
		}
        return server;
	}
	private VirtualMachineSetting getLinuxVMModel( HypervisorWrapper hypervisorWrapper,VMInfoInCPM vmInfo, RecoveryPoint linuxRecoveryPoint ) { 
		VirtualMachineSetting vm = new VirtualMachineSetting();
		vm.setVmName( vmInfo.getVmDisplayName() ); 
	    vm.setCpuCount( (int)vmInfo.getCPU() );
	
	    vm.setMemory((int)vmInfo.getMemorySize() );
	    String dsName = vmInfo.getDatastoreName();
	    if( hypervisorWrapper.getHypervisorType() == HypervisorType.VMWARE ) {
	    	dsName = vmInfo.getDatastoreName();
	    }else{
	    	dsName = vmInfo.getVmConfigPath();
	    }
	
	    if(vmInfo.getHostname() != null || !"".equals(vmInfo.getHostname())){
	    	vm.setNetwork_hostName(vmInfo.getHostname());
	    }
	    vm.setReboot(true);
	    //confirm with lixiang: hard code;
        vm.setUserName("root");
        vm.setPassword("cad2d");
        vm.setDsName(dsName);
        
        if(hypervisorWrapper.getVmWareInfo()!=null){
            vm.setResourcePoolId(hypervisorWrapper.getVmWareInfo().getResourcePoolRefID());
            vm.setResourcePoolName(hypervisorWrapper.getVmWareInfo().getResourcePool());
        }

        List<VirtualNetworkInfo> networkList = new ArrayList<VirtualNetworkInfo>();
	    if( vmInfo.getAdapterSetting().size() >0 ) {
	    	for(AdapterForInstantVM adaSett : vmInfo.getAdapterSetting()){
	    		VirtualNetworkInfo vn = new VirtualNetworkInfo();
	    		vn.setVmAdapterType(adaSett.getAdapterType());
	    		vn.setVmNetwork(adaSett.getNetworkType());
	    		vn.setConnectedToLinuxBackupServer(adaSett.isDefaultConnect());
	    		if( adaSett.isDhcp() ) {
	    			vn.setDHCP( true );
		    	}
		    	else {
		    		vn.setIpAddress( adaSett.getIpInfos().get(0).getIp() );
		    		vn.setNetmask( adaSett.getIpInfos().get(0).getSubnet() );
		    		// gate way
		    		if(!CollectionUtils.isEmpty(adaSett.getGateWayList())){
		    			vn.setGateway(adaSett.getGateWayList().get(0));
		    		}
		    	}
	    		
	    		
				//dns
				if(!CollectionUtils.isEmpty(adaSett.getDnsList())){
	    			vn.setDns(adaSett.getDnsList().get(0));
	    		}
				
	    		networkList.add(vn);
	    	}
	    	
	    }
	    vm.setNetworkList(networkList);
	    //linux agent get this info by itself.
//	    List<VirtualDisk> diskList = new ArrayList<VirtualDisk>();
//	    for( RecoveryPointItem rpItem : linuxRecoveryPoint.getItems()  ) {
//	    	
//	        VirtualDisk disk = new VirtualDisk();
////	        disk.setSize( rpItem.getSize() );
//	        DataStore ds = new DataStore();
//	        ds.setName( dsName );
//	        disk.setStorage( ds );
//	        diskList.add( disk );
//	    }
//	    if( diskList.size() ==0 ) {
//	    	VirtualDisk disk = new VirtualDisk();
//		    disk.setSize( 1024*1024*1024*100L );  //by default 100GB
//		    DataStore ds = new DataStore();
//		    ds.setName( dsName);
//		    disk.setStorage( ds );
//		    diskList.add( disk );
//	    }
//        vm.setDiskList(diskList);
		return vm;
	}
	
	
	private InstantVM generateInstantVM(String ivmJobUUID, NodeRegistrationInfo proxyInfo, RecoveryPointInformationForCPM rpForCPM, StartInstantVMOperation operationPara) {
		InstantVM vm = new InstantVM();
		vm.setUuid(ivmJobUUID);
		vm.setNodeId(rpForCPM.getProtectedNode().getNodeId());
		vm.setNodeName(rpForCPM.getProtectedNode().getNodeName());
		vm.setNodeUuid(rpForCPM.getProtectedNode().getNodeUuid());
		vm.setName(operationPara.getVmInfo().getVmDisplayName());
		vm.setRecoveryPoint(rpForCPM.getRecoveryPoint());
		vm.setVmLocation(operationPara.getHypervisorWrapper().getVmLocation());
		vm.setRecoveryServerType(InstantVM.RECOVERY_SERVER_TYPE_LINUX);
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
		
		for(AdapterForInstantVM temp : operationPara.getVmInfo().getAdapterSetting()){
			if(temp.isDhcp()){
				temp.setGateWayList(new ArrayList<String>());
			}
		}
		
		detail.setVmInfo(operationPara.getVmInfo());
//		detail.setVmFilePath(operationPara.getVmInfo().getVmConfigPath());
//		detail.setCpuCount(operationPara.getVmInfo().getCPU());
//		detail.setMemorySize(operationPara.getVmInfo().getMemorySize());
//		detail.setAdapterSetting(operationPara.getVmInfo().getAdapterSetting());
		
		return vm;
	}
}
