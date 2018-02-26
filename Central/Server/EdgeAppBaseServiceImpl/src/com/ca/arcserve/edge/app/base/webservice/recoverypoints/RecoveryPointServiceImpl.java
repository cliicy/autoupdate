package com.ca.arcserve.edge.app.base.webservice.recoverypoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.ca.arcflash.rps.webservice.data.ConnectionInfoEx;
import com.ca.arcflash.rps.webservice.data.RecoveryPointWithNodeInfo;
import com.ca.arcflash.webservice.data.ConnectionInfo;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.restore.RecoveryPointItem;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.IRecoveryPointService;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationBrowser;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.sharefolder.SharedFolderBrowseParam;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.MachineConfigure;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.RecoveryPointForLinux;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.RecoveryPointInformationForCPM;
import com.ca.arcserve.edge.app.base.webservice.instantvm.InstantVMServiceUtil;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsNodeDao;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsNode;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.data.BackupLocationInfo;

public class RecoveryPointServiceImpl implements IRecoveryPointService {
	private IRpsNodeDao rpsNodeDao = DaoFactory.getDao(IRpsNodeDao.class);
	private static final Logger logger = Logger.getLogger( RecoveryPointServiceImpl.class );
	private AdrConfigureHandler adrConfigureHandler = new AdrConfigureHandler();
	private SessionPasswordHandler paswordHandler = new SessionPasswordHandler();
	/**
	 * datastore recovery point
	 */
	@Override
	public List<RecoveryPoint> getRecoveryPointsByTimePeriod(int rpsNodeId , ProtectedNodeInDestination node,Date beginTime,Date endTime)throws EdgeServiceFault{
		List<RecoveryPoint> recoveryPoints;
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(rpsNodeId)){
			recoveryPoints = conn.getService().getRecoveryPoints(getDatastoreRecoveryPointConnInfo(rpsNodeId, node), beginTime, endTime);
		}
//		for(RecoveryPoint rp: recoveryPoints){
//			TimeZone tz = Calendar.getInstance().getTimeZone();
//			rp.setTimeZoneOffset(tz.getOffset(rp.getTime().getTime()));   //has calculate DST
//			
//		}
		Collections.sort(recoveryPoints, new Comparator<RecoveryPoint>() {
			//sort by time DESC
			@Override
			public int compare(RecoveryPoint o1, RecoveryPoint o2) {
				if(o2.getTime()!=null && o1.getTime()!=null){ 
					return o2.getTime().compareTo(o1.getTime());
				}else if (o2.getTime()==null && o1.getTime()!=null) {
					return -1;
				}else if (o2.getTime()!=null && o1.getTime()==null) {
					return 1;
				}else {
					return 0;
				}
			}
		});
		
		logger.info("[RecoveryPointServiceImpl] getRecoveryPointsByTimePeriod(): destination "+ node.getDestination() +", result size: "+
				recoveryPoints.size() + ", start time: " + beginTime.toString() + ", end time: "+ endTime.toString());
		return recoveryPoints;
	} 
	
	@Override
	public RecoveryPointItem[] getRecoveryPointItems(RecoveryPoint recoveryPoint, int rpsNodeId, ProtectedNodeInDestination node) throws EdgeServiceFault {
		RecoveryPointItem[] recoveryPointItems;
		try(D2DConnection conn = EdgeCommonUtil.getD2DProxyByNodeId(rpsNodeId)){
			ConnectionInfoEx connectionInfo = getDatastoreRecoveryPointConnInfo(rpsNodeId, node);
			recoveryPointItems = conn.getService().getRecoveryPointItems(connectionInfo.getDestination(), connectionInfo.getDomain(),
					connectionInfo.getUserName(), connectionInfo.getPassword(), recoveryPoint.getPath());
		}
		
		return recoveryPointItems;
	} 
	
	private ConnectionInfoEx getDatastoreRecoveryPointConnInfo(int rpsNodeId , ProtectedNodeInDestination node){
		if(StringUtil.isEmptyOrNull(node.getUsername())||StringUtil.isEmptyOrNull(node.getPassword())){ //for rps local , need set the username and password for destination access
			List<EdgeRpsNode> nodeList = new ArrayList<EdgeRpsNode>();
			rpsNodeDao.as_edge_rps_node_list(rpsNodeId, nodeList);
			if(nodeList!=null && nodeList.size()>0){
				if(StringUtil.isEmptyOrNull(node.getUsername())){
					node.setUsername(nodeList.get(0).getUsername());
				}
				if(StringUtil.isEmptyOrNull(node.getPassword())){
					node.setPassword(nodeList.get(0).getPassword());
				}
			}
		}
		ConnectionInfoEx connectionInfo = new ConnectionInfoEx();
		connectionInfo.setClientUUID(node.getNodeUuid());
		connectionInfo.setDestination(node.getDestination());
		connectionInfo.setUserName(com.ca.arcserve.edge.app.base.webservice.contract.common.Utils.getUserNameNoDomain(node.getUsername()));
		connectionInfo.setPassword(node.getPassword());
		connectionInfo.setDomain(com.ca.arcserve.edge.app.base.webservice.contract.common.Utils.getDomainByUserName(node.getUsername()));
		connectionInfo.setClientName(node.getNodeName());
		return connectionInfo;
	}
	@Override
	public boolean validateRecoveryPointPassword( RecoveryPointInformationForCPM rpWithNode ) throws EdgeServiceFault { 
		try {
			return paswordHandler.validateRecoveryPointPassword(rpWithNode);
		}
		catch( Exception e ) {
			logger.info( "failed validate session pasword " + rpWithNode.toString() ,e);
			throw e;
		}
	}

	@Override
	public MachineConfigure getRecoveryPointMachineConfig( RecoveryPointInformationForCPM rpWithNode ) throws EdgeServiceFault {
		try {
			logger.debug("getRecoveryPointMachineConfig start: "+InstantVMServiceUtil.printObject(rpWithNode));
			MachineConfigure conf = adrConfigureHandler.handleAdrConfigure(rpWithNode);
			logger.debug("getRecoveryPointMachineConfig return: "+InstantVMServiceUtil.printObject(conf));
			return conf;
		}
		catch( Exception e ) {
			logger.error("failed get adrconfigure of recovery point: " + rpWithNode.toString() ,e );
			throw e;
		}
	}

	private  List<com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint> 
		innerGetLinuxRecoveryPoints( DestinationBrowser browser, String nodeName, Date startDate,Date endDate ) throws EdgeServiceFault {
		try(LinuxD2DConnection linuxConn = RecoveryPointBrowseUtil.getInstance().getLinuxDestinationBrowser(browser)) {
			ILinuximagingService linuxServer = linuxConn.getService();
			BackupLocationInfo backupLocation = RecoveryPointBrowseUtil.getInstance().getDestinationWithPasswordForLinux(browser);
						
//			System.out.println(backupLocation.getBackupDestLocation());
//			System.out.println(backupLocation.getBackupDestPasswd());
//			System.out.println(backupLocation.getCurrentJobCount());
//			System.out.println(backupLocation.getBackupDestUser());
//			System.out.println(backupLocation.getFreeSize());
//			System.out.println(backupLocation.getFreeSizeAlert());
//			System.out.println(backupLocation.getFreeSizeAlertUnit());
//			System.out.println(backupLocation.getJobLimit());
//			System.out.println(backupLocation.getScript());
//			System.out.println(backupLocation.getTotalSize());
//			System.out.println(backupLocation.getType());
//			System.out.println(backupLocation.getUuid());
//			
//			System.out.println(backupLocation.getWaitingJobCount());
//			System.out.println("DataStoreInfo-------------");
//			System.out.println(backupLocation.getDataStoreInfo().getEnableDedup());
//			System.out.println(backupLocation.getDataStoreInfo().getName());
//			System.out.println(backupLocation.getDataStoreInfo().getSharePath());
//			System.out.println(backupLocation.getDataStoreInfo().getSharePathPassword());
//			System.out.println(backupLocation.getDataStoreInfo().getSharePathUsername());
//			System.out.println(backupLocation.getDataStoreInfo().getUuid());
//			System.out.println("serverinfo-------------");
//			System.out.println(backupLocation.getServerInfo().getAuthKey());
//			System.out.println(backupLocation.getServerInfo().getDescription());
//			System.out.println(backupLocation.getServerInfo().getId());
//			System.out.println(backupLocation.getServerInfo().getName());
//			System.out.println(backupLocation.getServerInfo().getPassword());
//			System.out.println(backupLocation.getServerInfo().getPort());
//			System.out.println(backupLocation.getServerInfo().getProtocol());
//			System.out.println(backupLocation.getServerInfo().getServerType());
//			System.out.println(backupLocation.getServerInfo().getUser());
//			System.out.println(backupLocation.getServerInfo().getUuid());
//			System.out.println(backupLocation.getServerInfo().getAuthType());
			
			return linuxServer.getRecoveryPointList(backupLocation, nodeName, startDate, endDate);
		}
		catch( Exception e ) {
			logger.info( "innerGetLinuxRecoveryPoints() failed obtain linux recovery point from shared folder ID: " + browser.getDestinationId() + " and machine: " + nodeName ,e);
			throw e;
		} 
 	}
	
	public com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint reGetLinuxAgentRPFromAgentRP( RecoveryPointInformationForCPM rpForCPM ) throws EdgeServiceFault {
		Date sessionDate = rpForCPM.getRecoveryPoint().getTime();
		Date start = new Date( sessionDate.getTime() - 1000*3600*24 ); /// 1 day before
		Date end = new Date( sessionDate.getTime() + 1000*3600*24 ); /// 1 hour before
		List<com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint> rpList = innerGetLinuxRecoveryPoints( 
				rpForCPM.getBrowser(), rpForCPM.getProtectedNode().getNodeName() , start, end );
		for( com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint rp: rpList ) {
			if( rp.getName().contains( rpForCPM.getRecoveryPoint().getName() ) ) {
				return rp;
			}
		}
		logger.info("reGetLinuxAgentRPFromAgentRP(): return null" );
		return null;
	}
	/**
	 * linux agent recovery point; call in both cifs and nfs case;
	 */
	public List<RecoveryPoint> getLinuxRecoveryPoints( SharedFolderBrowseParam param, Date startDate,Date endDate ) throws EdgeServiceFault {
		if( param.getCurrentNode() == null ) {
			return null;
		}
		List<com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint> linuxRawRP = innerGetLinuxRecoveryPoints(
				param.getBrowserInfo(), param.getCurrentNode().getNodeName(), startDate, endDate );
		List<RecoveryPoint> rpForLinux = new ArrayList<RecoveryPoint>();
		if( linuxRawRP != null ) {
			for( com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint linuxrp : linuxRawRP ) { 
				RecoveryPointForLinux rp = RecoveryPointBrowseUtil.convertToAgentRecoveryPointForLinux( linuxrp );
				rpForLinux.add( rp );
			}
		}
		/**reverse sequence!
		 */
		Collections.sort(rpForLinux, new Comparator<RecoveryPoint>() {
			@Override
			public int compare(RecoveryPoint o1, RecoveryPoint o2) {
				if(o1.getTime()!=null && o2.getTime()!=null){
					return o1.getTime().compareTo(o2.getTime());
				}else if (o1.getTime()==null && o2.getTime()!=null) {
					return -1;
				}else if (o1.getTime()!=null && o2.getTime()==null) {
					return 1;
				}else {
					return 0;
				}
			}
		});
		return rpForLinux; 
 	}

	/**
	 * cifs  agent recovery point
	 */
	@Override
	public List<RecoveryPointWithNodeInfo> getGroupedRecoveryPointsFromSharedFolder( SharedFolderBrowseParam param, Date beginTime,Date endTime ) throws EdgeServiceFault {
		try (RPSConnection rpBrowser = RecoveryPointBrowseUtil.getInstance().getDestinationBrowserRPSService(param.getBrowserInfo())){
			DestinationInfo sharedfolderWithpassword = RecoveryPointBrowseUtil.getInstance().getDestinationWithPasswordForWin( param.getBrowserInfo() );
		
			ConnectionInfo sharedFoldeConnInfo = new ConnectionInfo();
			String path = sharedfolderWithpassword.getPath();
			if( param.getCurrentNode() !=null ) {//only one node
				path = param.getCurrentNode().getDestination();
			}
			sharedFoldeConnInfo.setDestination( path );
			sharedFoldeConnInfo.setUserName( sharedfolderWithpassword.getUserName() );
			sharedFoldeConnInfo.setPassword( sharedfolderWithpassword.getPassword() );
			List<RecoveryPointWithNodeInfo> rpWithNodes = rpBrowser.getService().getRecoveryPointsWithNodeInfo( sharedFoldeConnInfo, beginTime, endTime );
			//fillLinuxRP( rpWithNodes ); //test11
			for( RecoveryPointWithNodeInfo node: rpWithNodes ) {
				if( node.getRecoveryPoints() == null ) {
					node.setRecoveryPoints( new ArrayList<RecoveryPoint>() );
				}
				Collections.sort( node.getRecoveryPoints(), new Comparator<RecoveryPoint>(){
					@Override
					/**reverse sequence!
					 * if o1 = null and p2 = null; return 0;
					 * if o1 = null o2 != null; return 1; null is bigger than other datetime;
					 * if o1 != null and o2 = null; return -1; not null is less than null;
					 * else o2 compare o1;
					 * @param o1
					 * @param o2
					 * @return
					 */
					public int compare(RecoveryPoint o1, RecoveryPoint o2) {
						return o1.getTime() == null ? ( o2.getTime() == null ? 0: 1 ) :     /// o1 time ==null;
							( o2.getTime() == null ? -1: o2.getTime().compareTo(o1.getTime()) ) ;
					}
				});
			}
			return rpWithNodes;
		}
		catch( Exception e ) {
			logger.info( "failed obtain recovery point list from shared folder ID: " + param.getBrowserInfo().getDestinationId() ,e);
			throw e;
		} 
	}

	@Override
	public List<RecoveryPoint> getRecoveryPointsByNodeList(int rpsNodeId,
			List<ProtectedNodeInDestination> nodeList, Date beginTime,
			Date endTime) throws EdgeServiceFault {
		List<RecoveryPoint> recoveryPointsList = new ArrayList<RecoveryPoint>();
		logger.debug( "getRecoveryPointsByNodeList rpsNodeId: " + rpsNodeId+", nodeList:"+nodeList+",beginTime:"+beginTime+",endTime:"+endTime);
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(rpsNodeId)){
			for(ProtectedNodeInDestination node:nodeList){
				List<RecoveryPoint> recoveryPoints = conn.getService().getRecoveryPoints(getDatastoreRecoveryPointConnInfo(rpsNodeId, node), beginTime, endTime);				
				logger.debug( "getRecoveryPointsByNodeList getRecoveryPoints node: " + node.getNodeUuid() +" " + node.getNodeName()
						+ " size="+recoveryPoints.size());
				recoveryPointsList.addAll(recoveryPoints);
			}
		}
		Collections.sort(recoveryPointsList, new Comparator<RecoveryPoint>() {
			//sort by NodeUUID time DESC
			@Override
			public int compare(RecoveryPoint o1, RecoveryPoint o2) {
				if(o2.getNodeUuid()!=null && o1.getNodeUuid()!=null){ 
					if(o2.getNodeUuid().equals(o1.getNodeUuid())){
						if(o2.getTime()!=null && o1.getTime()!=null){ 
							return o2.getTime().compareTo(o1.getTime());
						}else if (o2.getTime()==null && o1.getTime()!=null) {
							return -1;
						}else if (o2.getTime()!=null && o1.getTime()==null) {
							return 1;
						}else {
							return 0;
						}
					} else {						
						return o2.getNodeUuid().compareTo(o1.getNodeUuid());
					}
				}else if (o2.getNodeUuid()==null && o1.getNodeUuid()!=null) {
					return -1;
				}else if (o2.getNodeUuid()!=null && o1.getNodeUuid()==null) {
					return 1;
				}else {
					return 0;
				}				
			}
		});
		return recoveryPointsList;
	}
}
