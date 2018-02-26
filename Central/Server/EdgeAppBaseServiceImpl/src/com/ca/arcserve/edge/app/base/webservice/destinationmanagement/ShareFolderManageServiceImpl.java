package com.ca.arcserve.edge.app.base.webservice.destinationmanagement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.RecoveryPointWithNodeInfo;
import com.ca.arcflash.webservice.data.NetworkPath;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.appdaos.ISharedFolderDao;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IRecoveryPointService;
import com.ca.arcserve.edge.app.base.webservice.IRemoteShareFolderService;
import com.ca.arcserve.edge.app.base.webservice.IShareFolderManagementService;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.SimpleSortPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationBrowser;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationBrowser.RPBrowserType;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.sharefolder.ShareFolderDestinationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.sharefolder.SharedFolderBrowseParam;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PlanDestinationType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.FileCopySettingWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.LinuxBackupLocationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.TaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.RecoveryPointForLinux;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.linux.EdgeService4LinuxD2DUtil;
import com.ca.arcserve.edge.app.base.webservice.recoverypoints.RecoveryPointBrowseUtil;
import com.ca.arcserve.edge.app.base.webservice.recoverypoints.RecoveryPointServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.PlanInDestination;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.data.BackupLocationInfo;
import com.ca.arcserve.linuximaging.webservice.data.restore.BackupMachine;

public class ShareFolderManageServiceImpl implements IShareFolderManagementService { 
	
	protected static final Logger logger = Logger.getLogger( ShareFolderManageServiceImpl.class );
	private IEdgePolicyDao edgePolicyDao = DaoFactory.getDao( IEdgePolicyDao.class );
	private ISharedFolderDao shardFolderDao = DaoFactory.getDao( ISharedFolderDao.class );
	private IRecoveryPointService rpService;
	private IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	
	public ShareFolderManageServiceImpl( IRecoveryPointService rpService ) {
		this.rpService = rpService;
	}
	public ShareFolderManageServiceImpl() {
		this.rpService = new RecoveryPointServiceImpl();
	}
	@Override
	public PagingResult<ShareFolderDestinationInfo>  getSharedFolderDestinationList(  SimpleSortPagingConfig shareFolderPagingConfig, GatewayId gatewayId ) {
		int[] totalCount = new int[1];
		List<ShareFolderDestinationInfo> desharedFolders = new ArrayList<ShareFolderDestinationInfo>();
		shardFolderDao.as_edge_sharefolderlist( 
				shareFolderPagingConfig.getStartIndex(), 
				shareFolderPagingConfig.getCount(),
				shareFolderPagingConfig.getSortColumn(),
				shareFolderPagingConfig.isAsc() ? 1:0,
				gatewayId.getRecordId(),
				totalCount,		
				desharedFolders );
		for( ShareFolderDestinationInfo sharedFolder : desharedFolders ) {
			if( sharedFolder.getPath() == null ) {
				logger.info("getSharedFolderDestinationList encounter empty shared folder path with destinationId = " 
					+ sharedFolder.getDestinationId() );	
			}
			else {
				if( sharedFolder.getPath().matches("^[\\w\\W&&[^:/]]*:/.*") ) { //nfs
					sharedFolder.setNFS(true);
				}
			}
		}
 		PagingResult<ShareFolderDestinationInfo> pageResult = new PagingResult<ShareFolderDestinationInfo>();
		pageResult.setData(desharedFolders);
		pageResult.setStartIndex( shareFolderPagingConfig.getStartIndex() );
		pageResult.setTotalCount( totalCount[0] ); 
		return pageResult;	
	}
	
	@Override
	public ShareFolderDestinationInfo getSharedFolderWithpassword(
			int destinationId) {
		List<ShareFolderDestinationInfo> desharedFolders = new ArrayList<ShareFolderDestinationInfo>();
		shardFolderDao.as_edge_sharefolderwithpassword(destinationId,
				desharedFolders);
		for (ShareFolderDestinationInfo sharedFolder : desharedFolders) {
			if (sharedFolder.getPath() == null) {
				logger.info("getSharedFolderWithpassword encounter empty shared folder path with destinationId = "
						+ sharedFolder.getDestinationId());
			} else {
				if (sharedFolder.getPath().matches("^[\\w\\W&&[^:/]]*:/.*")) { // nfs
					sharedFolder.setNFS(true);
				}
			}
		}
		return desharedFolders.get(0);
	}
	
	public void updateSharedFolderAndPlanMap ( int planId, UnifiedPolicy planDetail ){
		try {
		////even linux plan contain backupconfiguration, but all property are empty; so we handle linux setting firstly
			if( planDetail.getLinuxBackupsetting() != null ) {
				LinuxBackupLocationInfo locationInfo =  planDetail.getLinuxBackupsetting().getBackupLocationInfo();
				if( locationInfo != null ) { //backup to shared folder; not rps
					int type = locationInfo.getType();
					//type definition  in LinuxBackupLocationInfoModel; it's an UI model which cannot used here; so I  hard code;
					if( type == 1 || type == 2) {//1 means nfs; 2 means cifs:  2 means local folder, this case is not handled
						updateLinuxSharedFolder( planId,  locationInfo.getBackupDestLocation(),locationInfo.getBackupDestUser(),  locationInfo.getBackupDestPasswd(), planDetail.getGatewayId());
					}else { //not sharefolder, should delete plan_share_folder_map
						edgePolicyDao.deletePlanDestinationMap(planId, 0,PlanDestinationType.SharedFolder.ordinal(),TaskType.LinuxBackUP.ordinal());
					}
				} else { //not sharefolder, should delete plan_share_folder_map
					edgePolicyDao.deletePlanDestinationMap(planId, 0,PlanDestinationType.SharedFolder.ordinal(),TaskType.LinuxBackUP.ordinal());
				}	
			}
			else if( planDetail.getBackupConfiguration() != null) {	
				if(planDetail.getBackupConfiguration().isD2dOrRPSDestType()){
					String destinationUserName = planDetail.getBackupConfiguration().getUserName();
					String destinationPassword = planDetail.getBackupConfiguration().getPassword();
					
					updateSharedFolder( planId , planDetail.getBackupConfiguration().getDestination(), destinationUserName, destinationPassword ,TaskType.BackUP, planDetail.getGatewayId()); 
				}else {
					edgePolicyDao.deletePlanDestinationMap(planId, 0, PlanDestinationType.SharedFolder.ordinal(),TaskType.BackUP.ordinal()); 
				}
				
			}
			else if( planDetail.getVSphereBackupConfiguration()!=null) {
				if(planDetail.getVSphereBackupConfiguration().isD2dOrRPSDestType()){
					String destinationUserName = planDetail.getVSphereBackupConfiguration().getUserName();
					String destinationPassword = planDetail.getVSphereBackupConfiguration().getPassword();
					updateSharedFolder( planId , planDetail.getVSphereBackupConfiguration().getDestination(), destinationUserName, destinationPassword, TaskType.VSphereBackUP, planDetail.getGatewayId() );
				}else {
					edgePolicyDao.deletePlanDestinationMap(planId, 0, PlanDestinationType.SharedFolder.ordinal(),TaskType.VSphereBackUP.ordinal());
				}
			}
			
			//file copy
			//Should delete the map relationship between plan and file copy destination firstly.
			edgePolicyDao.deletePlanDestinationMap(planId, 0, PlanDestinationType.SharedFolder.ordinal(),TaskType.FileCopy.ordinal());
			//add sharefolder destination for file copy
			if (planDetail.getFileCopySettingsWrapper()!=null && !planDetail.getFileCopySettingsWrapper().isEmpty()) {
				List<FileCopySettingWrapper> fileCopySettingWrappers = planDetail.getFileCopySettingsWrapper();
				for (FileCopySettingWrapper fileCopySettingWrapper : fileCopySettingWrappers) {
					if(fileCopySettingWrapper.getArchiveConfiguration().isbArchiveToDrive()){ //share folder
						String destinationUserName = fileCopySettingWrapper.getArchiveConfiguration().getStrArchiveDestinationUserName();
						String destinationPassword = fileCopySettingWrapper.getArchiveConfiguration().getStrArchiveDestinationPassword();
						updateSharedFolder( planId , fileCopySettingWrapper.getArchiveConfiguration().getStrArchiveToDrivePath()
								, destinationUserName, destinationPassword, TaskType.FileCopy, planDetail.getGatewayId());
					}
				}
			}
			
			//copy recovery point
			//Should delete the map relationship between plan and copy recovery point destination firstly
			edgePolicyDao.deletePlanDestinationMap(planId, 0, PlanDestinationType.SharedFolder.ordinal(),TaskType.CopyRecoveryPoints.ordinal());
			//add sharefolder destination for copy recovery point
			if (planDetail.getExportConfiguration()!=null) {
				String destinationUserName = planDetail.getExportConfiguration().getDestUserName()==null
						?"":planDetail.getExportConfiguration().getDestUserName();
				String destinationPassword = planDetail.getExportConfiguration().getDestPassword()==null
						?"":planDetail.getExportConfiguration().getDestPassword();
				updateSharedFolder( planId , planDetail.getExportConfiguration().getDestination(), 
						destinationUserName, destinationPassword, TaskType.CopyRecoveryPoints, planDetail.getGatewayId());
			}
		}
		catch( Exception e ) {
			logger.error("failed update sharedfolder for plan: " + planId, e);
		}
	}
	private void updateLinuxSharedFolder(  int planId, String destination, String usename, String password, GatewayId gatewayId  ) {
			//linux nfs share may have empty user name and password;
			int[] sharedFolderId = new int[1];
			if( usename ==null || password ==null ) {
				logger.info( "updateLinuxSharedFolder()  "
						+ "encounter empty usename/password when save sharedfolder " + destination + "from linux plan: " + planId  );
			}
			shardFolderDao.as_edge_sharefolder_addorupdate(gatewayId.getRecordId(), destination, usename ==null ? "" : usename, password == null ? "" : password , sharedFolderId );
			if( sharedFolderId[0] >0 ) {
				gatewayService.bindEntity(gatewayId, sharedFolderId[0], EntityType.ShareFolder);
				edgePolicyDao.addOrUpdatePlanDestinationMap(planId, sharedFolderId[0], PlanDestinationType.SharedFolder.ordinal(), "",TaskType.LinuxBackUP.ordinal());
			}
			else {
				logger.error( "updateLinuxSharedFolder()  empty sharedFolder Id when save shared folder from Linux plan:" + destination + "with plan Id: " + planId  );
			}
	}
	
	private void updateSharedFolder( int planId, String destination, String usename, String password, TaskType taskType, GatewayId gatewayId){
		///is shared folder;
		if( destination.matches("^[\\\\]{2}[\\w]+.*") && !StringUtil.isEmptyOrNull( usename ) && !StringUtil.isEmptyOrNull( password )  ) {
			int[] sharedFolderId = new int[1];
			shardFolderDao.as_edge_sharefolder_addorupdate(gatewayId.getRecordId(), destination, usename, password, sharedFolderId );
			if( sharedFolderId[0] >0 ) {
				gatewayService.bindEntity(gatewayId, sharedFolderId[0], EntityType.ShareFolder);
				edgePolicyDao.addOrUpdatePlanDestinationMap(planId, sharedFolderId[0], PlanDestinationType.SharedFolder.ordinal(), "", taskType.ordinal());
			}
			else {
				logger.error( "empty sharedFolder Id when save shared folder:" + destination + "with plan Id: " + planId  );
			}
		}
		else {
			logger.info("not add destination into sharedfolder table, because the destination: "  + destination +" is not shared folder"); 
		}
	}
	
	@Override
	public void updateSharedFolder(String destination, String usename, String password, GatewayId gatewayId){
		//is shared folder;
		if( destination.matches("^[\\\\]{2}[\\w]+.*") && !StringUtil.isEmptyOrNull( usename ) && !StringUtil.isEmptyOrNull( password )  ) {
			int[] sharedFolderId = new int[1];
			shardFolderDao.as_edge_sharefolder_addorupdate(gatewayId.getRecordId(), destination, usename, password, sharedFolderId );
		} else {
			logger.info("not add destination into sharedfolder table, because the destination: "  + destination +" is not shared folder"); 
		}
	}
	
   ////get data from shared folder
	@Override
	public List<PlanInDestination> getPlansFromSharedFolder( SharedFolderBrowseParam param ) throws EdgeServiceFault {
		try {
			List<ProtectedNodeInDestination> protectedNodes = null;
			if( param.getBrowserInfo().getBrowserType() == RPBrowserType.SharedFolderUsingLinuxServer ) {
				protectedNodes = getProtectedNodesUsingLinuxServer( param );
			}
			else if(param.getBrowserInfo().getBrowserType() == RPBrowserType.SharedFolderUsingRPS) {
				protectedNodes = getProtectedNodesUsingWinAgent( param );
			}
			return RecoveryPointBrowseUtil.getInstance().groupProtectedNodeByPlan(protectedNodes, false);
		}
		catch( Exception e ) {
			logger.error("getPlansFromSharedFolder() failed to get detination information " , e);
			throw e;
		}
	}
	/**
	 * should enhance to get nodeId by node UUID in future;
	 */
	private int getNodeIdByNodeName( String nodeName ){
		int id = EdgeService4LinuxD2DUtil.getLinuxNodeHostId( nodeName ); //for linux node, uuid is  nodename;
		if( id == 0 ) { //agent node
			int[] hostids = new int[1];  
			this.hostMgrDao.as_edge_host_getIdByHostname( nodeName, hostids ); 
			id= hostids[0];
		}
		if(id == 0){//vm node
			if(nodeName.contains("@")){
				String vmName = nodeName.substring(0,nodeName.indexOf("@"));
				String esxName = nodeName.substring(nodeName.indexOf("@")+1);
				int[] hostids = new int[1];  
				this.hostMgrDao.as_edge_getVmHostId_ByVmNameAndEsxName(vmName, esxName,hostids ); 
				id= hostids[0];
			}
		}
		if( id == 0 ) {
			logger.info("getPlansFromSharedFolder():cannot find node Id by node name: " +  nodeName );
		}
		return id;
	}
	
	private List<ProtectedNodeInDestination> getProtectedNodesUsingWinAgent ( SharedFolderBrowseParam param ) throws EdgeServiceFault {
		List<ProtectedNodeInDestination> protectedNodes = new ArrayList<ProtectedNodeInDestination>();
		try {
			Calendar calendar = Calendar.getInstance();       
		    calendar.setTime(new Date());   
		    calendar.set(Calendar.DAY_OF_YEAR,calendar.get(Calendar.DAY_OF_YEAR) + 1);   
			List<RecoveryPointWithNodeInfo> rpWithNodes = rpService.getGroupedRecoveryPointsFromSharedFolder(param, null, calendar.getTime() );
			
			//should use nodeUUId to match node; shuzhi will add nodeUUid in RecoveryPointWithNodeInfo in future;
			for( RecoveryPointWithNodeInfo rpNode : rpWithNodes ) {
				ProtectedNodeInDestination protectedNode = new ProtectedNodeInDestination();
				protectedNode.setLinux(false);
				DestinationBrowser browser = new DestinationBrowser();
				browser.setBrowserType( RPBrowserType.SharedFolderUsingRPS );
				
				protectedNode.setNodeId( getNodeIdByNodeName(rpNode.getNodeName()) ); //now we don't have uuid; need shuzhi provide
				protectedNode.setNodeName( rpNode.getNodeName() );
				protectedNode.setDestination( rpNode.getFullPath() );
				protectedNode.setIntegral(true);
				if( rpNode.getRecoveryPoints() == null ) {
					rpNode.setRecoveryPoints(new ArrayList<RecoveryPoint>() ); 
				}
				List<RecoveryPoint> recoverypoints = rpNode.getRecoveryPoints();
				protectedNode.setRecoveryCount( recoverypoints.size() );
				protectedNode.setHaveSessions( recoverypoints.size()>0 ? true: false );
				if(protectedNode.isHaveSessions()){
					protectedNode.setNodeUuid(recoverypoints.get(0).getNodeUuid());
					if(!StringUtil.isEmptyOrNull(protectedNode.getNodeUuid())){
						int nodeId = 0;
						int[] hostids = new int[1];  
						this.hostMgrDao.as_edge_host_getHostIdByUuid(protectedNode.getNodeUuid(), ProtectionType.WIN_D2D.getValue(), hostids);
						nodeId= hostids[0];
						if(nodeId == 0){
							this.hostMgrDao.as_edge_host_vm_by_instanceUUID(protectedNode.getNodeUuid(), hostids);
							nodeId= hostids[0];
						}
						if(nodeId != 0){
							protectedNode.setNodeId(nodeId);
						}
					}
					
					if(RecoveryPointForLinux.NODE_OS_LINUX == recoverypoints.get(0).getAgentOSType()
						|| RecoveryPointForLinux.NODE_OS_LINUX == recoverypoints.get(0).getVmGuestOsType()){
						protectedNode.setLinux(true);
					}
				}
				
				protectedNode.setMostRecentRecoveryPoint( protectedNode.isHaveSessions()? recoverypoints.get(0).getTime() : null );
				protectedNodes.add(protectedNode);
			}
			return protectedNodes;
		}
		catch( Exception e ) {
			logger.error("fillProtectedNodesUsingWinAgent() failed get nodes Inforamtion with location id: " + param.getBrowserInfo().getDestinationId(), e );
			throw e;
		} 
	}
	
	private List<ProtectedNodeInDestination> getProtectedNodesUsingLinuxServer( SharedFolderBrowseParam param ) throws EdgeServiceFault{
		List<ProtectedNodeInDestination> protectedNodes = new ArrayList<ProtectedNodeInDestination>();
		BackupLocationInfo locationInfo = RecoveryPointBrowseUtil.getInstance().getDestinationWithPasswordForLinux(param.getBrowserInfo());
		
		try(LinuxD2DConnection linuxConn = RecoveryPointBrowseUtil.getInstance().getLinuxDestinationBrowser(param.getBrowserInfo())) {
			ILinuximagingService linuxServer = linuxConn.getService();
			com.ca.arcserve.linuximaging.webservice.data.PagingConfig linuxPagingInfo = new com.ca.arcserve.linuximaging.webservice.data.PagingConfig();
			linuxPagingInfo.setAscend(true);
			linuxPagingInfo.setCount(Integer.MAX_VALUE);
			linuxPagingInfo.setStartIndex(0);

			com.ca.arcserve.linuximaging.webservice.data.PagingResult<BackupMachine>  machines = linuxServer.getBackupMachineList( locationInfo, linuxPagingInfo );
			if( machines == null ) {
				logger.info("getProtectedNodesUsingLinuxServer() , machines list from linux server is null " );
				return protectedNodes;
			}
			List<BackupMachine> backupMachines = machines.getData();
			for( BackupMachine machine: backupMachines ) {
				if(machine.getMachineType() == BackupMachine.TYPE_HBBU_MACHINE){
					continue;
				}
				ProtectedNodeInDestination protectedNode = new ProtectedNodeInDestination();
				DestinationBrowser browser = new DestinationBrowser();
				browser.setBrowserType( RPBrowserType.SharedFolderUsingLinuxServer );
				
				protectedNode.setNodeId ( getNodeIdByNodeName(machine.getMachineName()) );
				protectedNode.setNodeName( machine.getMachineName() );
				protectedNode.setNodeUuid( machine.getMachineName() );  //for linux node: uuid = nodename;
				protectedNode.setDestination( locationInfo.getBackupDestLocation() + "/" +  machine.getMachineName() );
				protectedNode.setMostRecentRecoveryPoint(  machine.getLastDate() );
				protectedNode.setRecoveryCount( machine.getRecoveryPointCount() );
				protectedNode.setIntegral(true);
				protectedNode.setLinux(true);
				protectedNodes.add(protectedNode);
			}
			return protectedNodes;
		}
		catch( Exception e ) {
			logger.error("getProtectedNodesUsingLinuxServer() failed get nodes Inforamtion from location: " + locationInfo.getBackupDestLocation() , e );
			throw e;
		}
	}
	
	@Override
	public List<ProtectedNodeInDestination> getNodesDetailFromSharedFolder(SharedFolderBrowseParam param, List<ProtectedNodeInDestination> needUpdates) throws EdgeServiceFault {
		if( param.getBrowserInfo().getBrowserType() == RPBrowserType.SharedFolderUsingLinuxServer ) {
			 refreshNodeDetailUsingLinuxServer( param,needUpdates );
		}
		else if( param.getBrowserInfo().getBrowserType() == RPBrowserType.SharedFolderUsingRPS ) {
			 refreshNodeDetailUsingWinAgent( param,needUpdates );
		}
		return needUpdates;
	}
	private void refreshNodeDetailUsingWinAgent( SharedFolderBrowseParam param, List<ProtectedNodeInDestination> needUpdates ) throws EdgeServiceFault {
		List<RecoveryPointWithNodeInfo> rpWithNodes = rpService.getGroupedRecoveryPointsFromSharedFolder(param, null, null);
		for( ProtectedNodeInDestination needUpdate:  needUpdates ) {
			needUpdate.setLinux(false);
			for( RecoveryPointWithNodeInfo rpWithNode: rpWithNodes ){
				if( rpWithNode.getNodeName().equals(needUpdate.getNodeName() ) ) {
					List<RecoveryPoint> recoverypoints = rpWithNode.getRecoveryPoints();
					needUpdate.setRecoveryCount( recoverypoints.size() );
					needUpdate.setHaveSessions( recoverypoints.size()>0 ? true: false );						
					needUpdate.setMostRecentRecoveryPoint( needUpdate.isHaveSessions()? recoverypoints.get(0).getTime() : null );
					if(recoverypoints != null && !recoverypoints.isEmpty()){
						if(RecoveryPointForLinux.NODE_OS_LINUX == recoverypoints.get(0).getAgentOSType()
								|| RecoveryPointForLinux.NODE_OS_LINUX == recoverypoints.get(0).getVmGuestOsType()
								){
							needUpdate.setLinux(true);
						}
					}
				}
			}
		}
	}
	private void refreshNodeDetailUsingLinuxServer( SharedFolderBrowseParam param,  List<ProtectedNodeInDestination> needToUpdates ) throws EdgeServiceFault {
	    Calendar calendar = Calendar.getInstance();      
	    calendar.setTime(new Date());   
	    calendar.set(Calendar.DAY_OF_YEAR,calendar.get(Calendar.DAY_OF_YEAR) + 1);   
		
	    Date endTime = calendar.getTime();
	    Date startTime = new Date(0);

		for( ProtectedNodeInDestination protectedNode : needToUpdates ) {
			param.setCurrentNode(protectedNode);
			List<RecoveryPoint> recPoints = rpService.getLinuxRecoveryPoints(param, startTime, endTime  );
			if( recPoints.size() > 0 ) {
				protectedNode.setMostRecentRecoveryPoint( recPoints.get(0).getTime() );
				protectedNode.setRecoveryCount( recPoints.size() ); 
				protectedNode.setHaveSessions(true);
				protectedNode.setLinux(true);
			}
			else {
				protectedNode.setMostRecentRecoveryPoint(null);
				protectedNode.setRecoveryCount( 0 ); 
				protectedNode.setHaveSessions( false );
				protectedNode.setLinux(true);
			}
		}
	}
	@Override
	public FileFolderItem getFileFolderWithCredentials(GatewayId gateway,String path,String user, String pwd) throws EdgeServiceFault{
		
		IRemoteShareFolderServiceFactory remoteShareFolderServiceFactory = EdgeFactory.getBean(IRemoteShareFolderServiceFactory.class);
		IRemoteShareFolderService remoteShareFolderService = remoteShareFolderServiceFactory.createRemoteShareFolderService(gateway);
		
		return remoteShareFolderService.getFileFolderWithCredentials(path, user, pwd);

	}
	
	@Override
	public boolean createFolderOnDestination(GatewayId gateway,String parentPath, String subDir) throws EdgeServiceFault{
		
		IRemoteShareFolderServiceFactory remoteShareFolderServiceFactory = EdgeFactory.getBean(IRemoteShareFolderServiceFactory.class);
		IRemoteShareFolderService remoteShareFolderService = remoteShareFolderServiceFactory.createRemoteShareFolderService(gateway);
		
		return remoteShareFolderService.createFolder(parentPath, subDir);
		
	}
	
	@Override
	public NetworkPath[] getMappedNetworkPathOnDestination(GatewayId gateway, String userName) throws EdgeServiceFault{
		
		
		IRemoteShareFolderServiceFactory remoteShareFolderServiceFactory = EdgeFactory.getBean(IRemoteShareFolderServiceFactory.class);
		IRemoteShareFolderService remoteShareFolderService = remoteShareFolderServiceFactory.createRemoteShareFolderService(gateway);
		return remoteShareFolderService.getMappedNetworkPath(userName);
		
		
	}
	
	@Override
	public long getDestDriveType(GatewayId gateway, String path) throws EdgeServiceFault{

		IRemoteShareFolderServiceFactory remoteShareFolderServiceFactory = EdgeFactory.getBean(IRemoteShareFolderServiceFactory.class);
		IRemoteShareFolderService remoteShareFolderService = remoteShareFolderServiceFactory.createRemoteShareFolderService(gateway);
		return remoteShareFolderService.getDestDriveType(path);
	}
	
	@Override
	public Volume[] getVolumesFromDestination(GatewayId gateway) throws EdgeServiceFault{
		
		IRemoteShareFolderServiceFactory remoteShareFolderServiceFactory = EdgeFactory.getBean(IRemoteShareFolderServiceFactory.class);
		IRemoteShareFolderService remoteShareFolderService = remoteShareFolderServiceFactory.createRemoteShareFolderService(gateway);
		return remoteShareFolderService.getVolumes();
		
	}
	
	@Override
	public String getMntPathFromVolumeGUID(GatewayId gateway, String strGUID) throws EdgeServiceFault{
		
		IRemoteShareFolderServiceFactory remoteShareFolderServiceFactory = EdgeFactory.getBean(IRemoteShareFolderServiceFactory.class);
		IRemoteShareFolderService remoteShareFolderService = remoteShareFolderServiceFactory.createRemoteShareFolderService(gateway);
		return remoteShareFolderService.getMntPathFromVolumeGUID(strGUID);
	}
	
	@Override
	public long validateDest(GatewayId gateway, String path, String domain, String user, String pwd) throws EdgeServiceFault{
		
		IRemoteShareFolderServiceFactory remoteShareFolderServiceFactory = EdgeFactory.getBean(IRemoteShareFolderServiceFactory.class);
		IRemoteShareFolderService remoteShareFolderService = remoteShareFolderServiceFactory.createRemoteShareFolderService(gateway);
		
		return remoteShareFolderService.validateDest(path, domain, user, pwd);
	}
	
	@Override
	public long validateDestForMode(GatewayId gateway, String path, String domain, String user, String pwd,int mode)throws EdgeServiceFault{

		
		IRemoteShareFolderServiceFactory remoteShareFolderServiceFactory = EdgeFactory.getBean(IRemoteShareFolderServiceFactory.class);
		IRemoteShareFolderService remoteShareFolderService = remoteShareFolderServiceFactory.createRemoteShareFolderService(gateway);
		
		return remoteShareFolderService.validateDestForMode(path, domain, user, pwd, mode);
		
	}
	
	@Override
	public void deleteShareFolderByid(int destinationId) throws EdgeServiceFault {
		shardFolderDao.as_edge_sharefolder_delete(destinationId);
		gatewayService.unbindEntity(destinationId, EntityType.ShareFolder);
	}
}
