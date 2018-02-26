package com.ca.arcserve.edge.app.base.webservice.recoverypoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.appdaos.ISharedFolderDao;
import com.ca.arcserve.edge.app.base.common.D2DServiceUtils;
import com.ca.arcserve.edge.app.base.common.D2DServiceUtils.D2DServiceConnectInfo;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationBrowser;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.sharefolder.ShareFolderDestinationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PlanDestinationType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.RecoveryPointForLinux;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.RecoveryPointForLinux.RecoveryPointItemForLinux;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.RecoveryPointInformationForCPM;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.PlanInDestination;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;
import com.ca.arcserve.edge.app.rps.webservice.datastore.DataStoreManager;
import com.ca.arcserve.linuximaging.webservice.data.BackupLocationInfo;
import com.ca.arcserve.linuximaging.webservice.data.RecoveryPointItem;
import com.ca.arcserve.linuximaging.webservice.data.ServerInfo;
import com.ca.arcserve.linuximaging.webservice.data.backup.DataStoreInfo;

public class RecoveryPointBrowseUtil {
	private static RecoveryPointBrowseUtil instance = new RecoveryPointBrowseUtil();
	private static final Logger logger = Logger.getLogger( RecoveryPointBrowseUtil.class );
	public static RecoveryPointBrowseUtil getInstance() {
		return instance;
	}
	private IEdgePolicyDao policyDao = DaoFactory.getDao( IEdgePolicyDao.class );
	private ISharedFolderDao sharedFolderDao = DaoFactory.getDao( ISharedFolderDao.class );
	
	//util functions
	public BackupLocationInfo getDestinationWithPasswordForLinux(DestinationBrowser browser) throws EdgeServiceFault {
		DestinationInfo destinationInfo = getDestinationWithPasswordForWin(browser) ;
		BackupLocationInfo locationForLinux = new BackupLocationInfo();
		PlanDestinationType destinationType =browser.getDestinationType();
		if(destinationType ==  PlanDestinationType.RPS){
			locationForLinux.setType(BackupLocationInfo.BACKLOCATION_TYPE_RPS_SERVER);
			DataStoreInfo dataStoreInfo = new DataStoreInfo();
			dataStoreInfo.setUuid(browser.getSubDest());
			locationForLinux.setDataStoreInfo(dataStoreInfo);
			D2DServiceConnectInfo connInfo = D2DServiceUtils.getD2DConnectInfo( browser.getDestinationId() );
			ServerInfo rpsServer = new ServerInfo();
			rpsServer.setName(connInfo.hostname);
			rpsServer.setPort(connInfo.port);
			rpsServer.setProtocol(connInfo.protocol);
			rpsServer.setUser(connInfo.username);
			rpsServer.setPassword(connInfo.password);
			rpsServer.setUuid(connInfo.uuid);
			rpsServer.setAuthKey(connInfo.authuuid);
			locationForLinux.setServerInfo(rpsServer);
		}
        locationForLinux.setBackupDestLocation( destinationInfo.getPath().replace("\\", "/") );
        locationForLinux.setBackupDestUser( destinationInfo.getUserName() );
        locationForLinux.setBackupDestPasswd( destinationInfo.getPassword() );
		return locationForLinux;
	}
	public DestinationInfo getDestinationWithPasswordForWin(DestinationBrowser browser) throws EdgeServiceFault {
		PlanDestinationType destinationType =browser.getDestinationType();
		int destinationId = browser.getDestinationId();
		String subDest = browser.getSubDest();
		DestinationInfo  locationInfo = new DestinationInfo();
		if( destinationType == PlanDestinationType.SharedFolder ) {
			ShareFolderDestinationInfo sharedFolder = null; 
			List<ShareFolderDestinationInfo> folderWithPassword  = new ArrayList<ShareFolderDestinationInfo>();
			sharedFolderDao.as_edge_sharefolderwithpassword( destinationId, folderWithPassword );
			sharedFolder = folderWithPassword.get(0);
			locationInfo.setUserName( sharedFolder.getUserName() );
			locationInfo.setPassword( sharedFolder.getPassword() ); 
			locationInfo.setPath( sharedFolder.getPath() );
		}else if(destinationType ==  PlanDestinationType.RPS && subDest != null) {
			
			DataStoreSettingInfo dsInfo = DataStoreManager.getDataStoreManager().getDataStoreByGuid( destinationId, subDest );
			locationInfo.setPath( dsInfo.getDSCommSetting().getStoreSharedName() );
			//datstore path is a sharedfolder
			if( !StringUtil.isEmptyOrNull( dsInfo.getDSCommSetting().getUser() ) ) {
				locationInfo.setUserName( dsInfo.getDSCommSetting().getUser() );
				locationInfo.setPassword( dsInfo.getDSCommSetting().getPassword() );
			}
			//path is datatsore local;
			else {
				///in this case, rps destination credential is rps node credential;		
				D2DServiceConnectInfo connInfo = D2DServiceUtils.getD2DConnectInfo( destinationId );
				if( connInfo != null ) {
					locationInfo.setUserName( connInfo.username );
					locationInfo.setPassword(  connInfo.password );
				}
			}
		}

		return locationInfo;
	}
	
	public com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint convertToLinuxRecoveryPoint( RecoveryPoint rp ) {
		com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint linuxRP = new com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint();
		if( rp instanceof RecoveryPointForLinux ) { 
			RecoveryPointForLinux nfsLinuxRP = (RecoveryPointForLinux)rp;
			linuxRP.setName( nfsLinuxRP.getName() );
			linuxRP.setBackupType( nfsLinuxRP.getBackupType() );
			linuxRP.setEncryptAlgoName( nfsLinuxRP.getLinuxencryptAlgoName() );
			linuxRP.setCompressLevel( nfsLinuxRP.getLinuxcompressLevel() );
			linuxRP.setEncryptionPassword( nfsLinuxRP.getLinuxencryptionPassword() ); 
			linuxRP.setArch( nfsLinuxRP.getLinuxcpuarch() );
			linuxRP.setOsType( nfsLinuxRP.getLinuxosType() );
			linuxRP.setTime( nfsLinuxRP.getTime().getTime() );
			for( RecoveryPointItemForLinux linuxRawItem:  nfsLinuxRP.getLinuxRecoveryPointItems() ) {
				com.ca.arcserve.linuximaging.webservice.data.RecoveryPointItem item1 = new com.ca.arcserve.linuximaging.webservice.data.RecoveryPointItem();
				item1.setName( linuxRawItem.getName() );
				item1.setSize( linuxRawItem.getSize() ); 
				linuxRP.getItems().add( item1 );
			}
			return linuxRP;
		}
		else {
			linuxRP.setName( rp.getPath().replace("\\", "/") );
			linuxRP.setBackupType( rp.getBackupType() );	
			linuxRP.setTime( rp.getTime().getTime() );
			linuxRP.setEncryptAlgoName(String.valueOf(rp.getEncryptType()));
			linuxRP.setEncryptionPasswordHash(rp.getEncryptPasswordHash());
			return linuxRP;
		}
	}
	
	public static RecoveryPointForLinux convertToAgentRecoveryPointForLinux(   com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint linuxrp ) {
		RecoveryPointForLinux rp = new RecoveryPointForLinux();
		rp.setName( linuxrp.getName() );
		rp.setPath( linuxrp.getName() );  //the window recovery point path is VStore//SXXXXX; linux is SXXXX; so it's same as name;
		rp.setTime( new Date( linuxrp.getTime() ) );
		rp.setBackupType( linuxrp.getBackupType() ); 
		rp.setLinuxencryptAlgoName( linuxrp.getEncryptAlgoName() );
		rp.setLinuxencryptionPassword( linuxrp.getEncryptionPassword() ); 
		rp.setLinuxcompressLevel( linuxrp.getCompressLevel() );
		rp.setLinuxcpuarch( linuxrp.getArch() );
		rp.setLinuxosType( linuxrp.getOsType() );
		///agent backup type; for cifs and datastore recovery point; this informaiton is provided by rps api; but for nfs linux; we fill it by ourselves; 
		rp.setAgentBackupType( RecoveryPointForLinux.BACKUP_TYPE_LOCALD2D );
		rp.setAgentOSType( RecoveryPointForLinux.NODE_OS_LINUX );
		rp.setVmGuestOsType( RecoveryPointForLinux.NODE_OS_LINUX ); 
		for( RecoveryPointItem linuxRawItem:  linuxrp.getItems() ) {
			RecoveryPointItemForLinux item1 = new RecoveryPointItemForLinux();
			item1.setName( linuxRawItem.getName() );
			item1.setSize( linuxRawItem.getSize() );
			rp.setLinuxRecoveryPointItem( item1 ); 
		}
		return rp;
	}
	///web service obtain functions
	public D2DConnection getDestinationBrowserAgentService( DestinationBrowser browser ) throws EdgeServiceFault { 
		return EdgeFactory.getBean(IConnectionFactory.class).createD2DConnection( browser.getBrowserId() );
	}
	public RPSConnection getDestinationBrowserRPSService(DestinationBrowser browser) throws EdgeServiceFault { 
		D2DServiceConnectInfo browserInfo = D2DServiceUtils.getD2DConnectInfo( browser.getBrowserId() );
		RPSConnection rpBrowser = D2DServiceUtils.createRPSService( browserInfo );
		return rpBrowser;
	}
	public LinuxD2DConnection getLinuxDestinationBrowser(DestinationBrowser browser) throws EdgeServiceFault{
		D2DServiceConnectInfo linuxServerInfo = D2DServiceUtils.getD2DConnectInfo(browser.getBrowserId());
		LinuxD2DConnection connection = D2DServiceUtils.createLinuxService(linuxServerInfo);
		return connection;
	}
	// util functions
	public List<PlanInDestination> groupProtectedNodeByPlan( List<ProtectedNodeInDestination>  protectedNodes , boolean validatePlannUUID  ) {
		Map<String, List<ProtectedNodeInDestination>> planCache = new HashMap<String, List<ProtectedNodeInDestination>>();//plan name <-> node list
		for(ProtectedNodeInDestination protectedNode: protectedNodes ) {
			String planName="";		
			//issue 108256
			List<PolicyInfo> policyInfoList = new ArrayList<PolicyInfo>();
			policyDao.as_edge_policy_list_by_hostId( protectedNode.getNodeId(), policyInfoList );
			if(policyInfoList!=null && policyInfoList.size()>0){
				if( validatePlannUUID && !StringUtil.isEmptyOrNull( protectedNode.getPlanUuid() )){
					List<EdgePolicy> policies = new ArrayList<EdgePolicy>();
					policyDao.as_edge_policy_list_by_uuid( protectedNode.getPlanUuid(), policies);
					if(policies!=null && policies.size()>0 && policyInfoList.get(0).getPolicyUuid().equalsIgnoreCase(policies.get(0).getUuid())){
						planName = policies.get(0).getName();
					}
				}
				else if( !validatePlannUUID ){
					planName = policyInfoList.get(0).getPolicyName();
				}
			}
			protectedNode.setPlanName(planName);
			List<ProtectedNodeInDestination> nodeList = planCache.get(planName);
			if(nodeList == null){
				nodeList = new ArrayList<ProtectedNodeInDestination>();
				nodeList.add( protectedNode );
				planCache.put(planName, nodeList);
			}else{
				nodeList.add( protectedNode );
			}
		}
		List<PlanInDestination> plans = new ArrayList<PlanInDestination>();
		Iterator<String> iterator = planCache.keySet().iterator();
		PlanInDestination noNamePlan = new PlanInDestination();
		while (iterator.hasNext()) {
			String planName = iterator.next();
			if(planName.equals("")){
				fillNodesToPlan(noNamePlan, planName, planCache);
				continue;
			}
			PlanInDestination plan = new PlanInDestination();
			fillNodesToPlan(plan, planName, planCache);
			plans.add(plan);
		}
		Collections.sort(plans, new Comparator<PlanInDestination>() {
			@Override
			public int compare(PlanInDestination o1, PlanInDestination o2) {
				if(o1.getPlanName()!=null && o2.getPlanName()!=null){
					return o1.getPlanName().compareTo(o2.getPlanName());
				}else if (o1.getPlanName()==null && o2.getPlanName()!=null) {
					return -1;
				}else if (o1.getPlanName()!=null && o2.getPlanName()==null) {
					return 1;
				}else {
					return 0;
				}
			}
		});
		if(noNamePlan.getNodeList()!=null)
			plans.add(noNamePlan);	
		return plans;
	}
	 
	private void fillNodesToPlan(PlanInDestination plan ,String planName, Map<String, List<ProtectedNodeInDestination>> planCache){
		plan.setPlanName(planName);
		List<ProtectedNodeInDestination> nodeList = planCache.get(planName);
		Collections.sort(nodeList,new Comparator<ProtectedNodeInDestination>() {

			@Override
			public int compare(ProtectedNodeInDestination o1, ProtectedNodeInDestination o2) {
				if(o1.getNodeName()!=null && o2.getNodeName()!=null){
					return o1.getNodeName().compareTo(o2.getNodeName());
				}else if (o1.getNodeName()==null && o2.getNodeName()!=null) {
					return -1;
				}else if (o1.getNodeName()!=null && o2.getNodeName()==null) {
					return 1;
				}else {
					return 0;
				}
			}
		});
		plan.setNodeList(nodeList);
		plan.setRecoveryPointCount(-1);
	}
	
	
	public  String parseSessionFromRecoveryPointPath( RecoveryPointInformationForCPM rpWithNode ){
		if( rpWithNode.getRecoveryPoint().getPath() == null ) {
			logger.error("parseSessionFromRecoveryPointPath() path is null!"  );
			return null;
		}
		//match pattern "...XXX/XXX/SessionNumber and ...XXX\XXX\SesionNumber; XXX size is  0-n" session number has format [Ss]00000121
		 Pattern p = Pattern.compile( "([^\\\\/]+[\\\\/]{1})*(([sS]{1}0*[\\d]+){1})" );
		 Matcher matcher = p.matcher( rpWithNode.getRecoveryPoint().getPath() );
		 while( matcher.find() &&  matcher.groupCount()>0 ) {
			return matcher.group( matcher.groupCount()-1 ); //return the last group; it's the SessionNumber
		 }
		logger.error("parseSessionFromRecoveryPointPath() cannot path session number from: " + rpWithNode.getRecoveryPoint().getPath()  );
		return null;
	}

}
