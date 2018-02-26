package com.ca.arcserve.edge.app.base.common;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.arcserve.edge.util.WindowsRegistryUtils;
import com.ca.arcflash.rps.webservice.registration.RPSRegInfo;
import com.ca.arcflash.service.jni.CommonJNIProxy;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IntegerId;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.WindowsRegistry;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.IEdgeCommonService;
import com.ca.arcserve.edge.app.base.webservice.INodeService;
import com.ca.arcserve.edge.app.base.webservice.WebServiceFactory;
import com.ca.arcserve.edge.app.base.webservice.abintegration.ABFuncServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.common.EdgeCommonServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.DeployCommonUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseService;
import com.ca.arcserve.edge.app.base.webservice.license.LicenseServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsConnectionInfoDao;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsNodeDao;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsConnectionInfo;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsNode;

public class EdgeCommonUtil {

	private static final String ARCSERVE_SESSION = "com.ca.arcserve.edge.app.base.webservice.abintegration.ABFuncServiceImpl";

	public static final String EdgeSrvName = "CAARCAppSvc";

	public static String EdgeInstallPath = CommonUtil.BaseEdgeInstallPath;
	public static final String EdgeCONFIGURATION_DIR = CommonUtil.BaseEdgeCONFIGURATION_DIR;
	public static String EdgeConfigurtionFolder =CommonUtil.getConfigurationFolder(EdgeApplicationType.CentralManagement); //"C:\\Program Files\\CA\\ARCserve D2D\\Configuration\\";
	public static String EdgeDefaultUser = CommonUtil.EdgeDefaultUser;
	private static final Logger logger = Logger.getLogger(EdgeCommonUtil.class);

	private static String edgeWebServiceProtocol = "http";
	private static int edgeWebServicePort = 8015;
	private static Calendar edgeCalWithoutDst = null;
	private static ILicenseService licenseService=null;
	private static IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private static INodeService nodeService = new NodeServiceImpl();
	private static IEdgeCommonService commonService = new EdgeCommonServiceImpl();
	
	private static IEdgeConnectInfoDao connectInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private static IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	
	public static String getEdgeWebServiceProtocol() {
		return edgeWebServiceProtocol;
	}

	public static int getEdgeWebServicePort() {
		return edgeWebServicePort;
	}



	public static String readFileAsString(String filePath) throws Exception {
		return CommonUtil.readFileAsString(filePath);
	}

	public static void saveStringToFile(String source, String filePath)
			throws Exception {
		CommonUtil.saveStringToFile(source, filePath);

	}

	@SuppressWarnings("unchecked")
	public static ABFuncServiceImpl getARCserveSessionNo(HttpSession session,
			String sessionNo) {
		if (session != null) {
			Object sessionMap = session.getAttribute(ARCSERVE_SESSION);
			Hashtable<String, ABFuncServiceImpl> sessionHash = null;
			if (sessionMap != null && sessionMap instanceof Hashtable) {
				sessionHash = (Hashtable<String, ABFuncServiceImpl>) sessionMap;
				return sessionHash.get(sessionNo);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static void saveARCserveSessionNo(HttpSession session,
			String sessionNo, ABFuncServiceImpl fucSerImp) {
		if (session != null) {
			Object sessionMap = session.getAttribute(ARCSERVE_SESSION);
			Hashtable<String, ABFuncServiceImpl> sessionHash = null;
			if (sessionMap != null && sessionMap instanceof Hashtable) {
				sessionHash = (Hashtable<String, ABFuncServiceImpl>) sessionMap;

			} else {
				sessionHash = new Hashtable<String, ABFuncServiceImpl>();
				session.setAttribute(ARCSERVE_SESSION, sessionHash);
			}
			sessionHash.put(sessionNo, fucSerImp);
		}
		return;
	}

	public static void initProtocolAndPort() throws Exception {
		edgeWebServiceProtocol = WindowsRegistryUtils.getUDPUrl().substring(0,WindowsRegistryUtils.getUDPUrl().indexOf("://"));
		edgeWebServicePort = Integer.valueOf(WindowsRegistryUtils.getUDPUrl().substring(WindowsRegistryUtils.getUDPUrl().lastIndexOf(":") + 1,WindowsRegistryUtils.getUDPUrl().length()));
	}
	/**
	 * change local Date into UTC date
	 * @param localDate
	 * @return
	 */
	public static Date toUTC(Date localDate){
		Calendar gc = Calendar.getInstance();
		gc.setTimeInMillis(localDate.getTime() - java.util.TimeZone.getDefault().getRawOffset());

		Date utcDate = new Date(gc.getTimeInMillis());
		return utcDate;
	}
	/**
	 * change UTC Date into Local Date
	 * @param utcDate
	 * @return
	 */
	public static Date fromUTC(Date utcDate){
		Calendar gc = Calendar.getInstance();
		gc.setTimeInMillis(utcDate.getTime() + java.util.TimeZone.getDefault().getRawOffset());

		Date localDate = new Date(gc.getTimeInMillis());
		return localDate;
	}
	
	public static String getDomainName(String fullName){
		String domainName = "";

		if (fullName == null || fullName.isEmpty()) {
			return domainName;
		}

		int pos = fullName.indexOf("\\");

		if (pos == -1) {

		} else {
			domainName = fullName.substring(0, pos);
		}

		return domainName;
	}
	
	public static String getUserName(String fullName) {
		String userName = "";

		if (fullName == null || fullName.isEmpty()) {
			return userName;
		}

		int pos = fullName.indexOf("\\");

		if (pos == -1) {
			userName = fullName;
		} else {
			userName = fullName.substring(pos + 1);
		}

		return userName;
	}
	
	
	
	public static EdgeApplicationType[] getInstalledApplications(){
		ArrayList<EdgeApplicationType> appList = new ArrayList<EdgeApplicationType>();
		EdgeApplicationType[] array = new EdgeApplicationType[0];
		
		for( EdgeApplicationType app : EdgeApplicationType.values() ){
			if(CommonUtil.isAppInstalled(app))
				appList.add(app);
		}
		
		return appList.toArray(array);
	}
	
	public static Calendar getCalWithoutDST(){
		if (edgeCalWithoutDst == null) {
			edgeCalWithoutDst = Calendar.getInstance();
			TimeZone defaultTimeZone = TimeZone.getDefault();
			SimpleTimeZone value = new SimpleTimeZone(defaultTimeZone
					.getRawOffset(), defaultTimeZone.getID(), 0, 0, 0, 0, 0, 0,
					0, 0);
			edgeCalWithoutDst.setTimeZone(value);
		}
		return edgeCalWithoutDst;
	}
	
	public static RPSConnection getRPSServerProxyByNodeId(int nodeId) throws EdgeServiceFault {
		
		IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
		RPSConnection connection = connectionFactory.createRPSConnection(nodeId);
		connection.connect();
		
		return connection;
		
	}
	
	public static D2DConnection getD2DProxyByNodeId(int nodeId) throws EdgeServiceFault {		
		IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
		D2DConnection connection = connectionFactory.createD2DConnection(nodeId);
		connection.connect();
		
		return connection;		
	}
	
	public static EdgeRpsConnectionInfo getRPSConnectionInfo(int nodeID,IRpsConnectionInfoDao rpsConnectionInfoDao){
		
		List<EdgeRpsConnectionInfo> connInfoLst = new ArrayList<EdgeRpsConnectionInfo>();
		rpsConnectionInfoDao.as_edge_rps_connection_info_list(nodeID, connInfoLst);
		if(connInfoLst == null || connInfoLst.size()==0){
			return null;
		}
			
		return connInfoLst.get(0);
		
	}

	public static EdgeRpsNode getRPSNodeInfo(int nodeID,IRpsNodeDao rpsNodeDao){
		
		List<EdgeRpsNode> nodeList = new ArrayList<EdgeRpsNode>();
		rpsNodeDao.as_edge_rps_node_list(nodeID, nodeList);
		if(nodeList == null || nodeList.size()==0){
			return null;
		}
		
		return nodeList.get(0);
		
	}

	
	public static String getProtocolString(int protocol){
		
		if (protocol == Protocol.Https.ordinal()){
			return "https";
		}else{
			return "http";
		}
		
	}
	
	/**
	 * Update the managed status to database ,and then do other things
	 * @param hosId
	 * @param managedStatus
	 */
	public static void changeNodeManagedStatus(int hosId , NodeManagedStatus managedStatus){
		connectInfoDao.as_edge_connect_update_managedStatus(hosId, managedStatus.ordinal());
		OnFinishUpdateManagedStatus(hosId,managedStatus);
	}
	
	/**
	 * After changed managed status , do some other things , for example: remove license
	 * @param hosId
	 * @param managedStatus
	 */
	private static void OnFinishUpdateManagedStatus(int hosId , NodeManagedStatus managedStatus){
		//remove license
		if(managedStatus != NodeManagedStatus.Managed && EdgeWebServiceContext.getApplicationType()==EdgeApplicationType.CentralManagement){
			String hostUUID = null;
			String hostName = null;
			List<EdgeHost> hosts = new ArrayList<EdgeHost>();
			IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
			hostMgrDao.as_edge_host_list(hosId, 1, hosts);
			if (hosts.size() == 0 || hosts.get(0) == null) {
				return;
			}
			hostName = hosts.get(0).getRhostname();
			List<EdgeConnectInfo> connectInfoList = new ArrayList<EdgeConnectInfo>();
			connectInfoDao.as_edge_connect_info_list( hosId, connectInfoList );
			
			if(connectInfoList.size()!=0 && connectInfoList.get(0)!=null){
				hostUUID = connectInfoList.get(0).getUuid();
			}
			try {
				getLicenseService().deleteLicenseByMachine(hostName, UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT);
			} catch (EdgeServiceFault e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	private static ILicenseService getLicenseService(){
		if(licenseService == null)
			licenseService = new LicenseServiceImpl();
		return licenseService;
	}
	
	public static String encryptXml(String xml){
		return XmlEncrypter.CommonEncrypter.encryptXml_NoException(xml);
	}
	
	public static String decryptXml(String xml){
		return XmlDecrypter.CommonDecrypter.decryptXml_NoException(xml);
	}
	
	public static void initialCommonNative(){
		
		CommonNativeInstance.initialize(CommonJNIProxy.JNIType.WSJNI);		
	}
	
	public static void generateRegConfigration( String edgeUUID ) {

		String edgeHostName = getLocalFqdnName();
		String edgeProtocol = EdgeCommonUtil.getEdgeWebServiceProtocol();
		int    edgePort = EdgeCommonUtil.getEdgeWebServicePort();
		String edgeWSDL = "";
		edgeWSDL = com.ca.arcserve.edge.app.base.webservice.WebServiceFactory.getGateWayWSDL(edgeHostName, edgePort, edgeProtocol);
		String consoleUrl = EdgeCommonUtil.getConsoleUrl( edgeHostName, edgePort, edgeProtocol );
		EdgeRegInfo	regInfo = new EdgeRegInfo();
		regInfo.setEdgeHostName(edgeHostName);
		regInfo.setEdgeWSDL(edgeWSDL);
		String encryptUUID = BackupService.getInstance().getNativeFacade().encrypt(edgeUUID);
		regInfo.setEdgeUUID(encryptUUID);
		regInfo.setEdgeAppType(ApplicationType.CentralManagement);
		regInfo.setEdgeLocale(Locale.getDefault().toString());
		regInfo.setConsoleUrl(consoleUrl);
		regInfo.setEdgeConnectNameList(CommonUtil.getConnectNameList());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(EdgeInstallPath + CommonUtil.BaseDeploymentInstallPath + "RegConfigPM.xml");
			JAXB.marshal(regInfo, fos);
		} catch (Exception e) {
			logger.error("Generate RegConfigPM.xml failed", e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public static void generateRegRPSConfigration( String edgeUUID ) {
		String edgeHostName = getLocalFqdnName();
		String edgeProtocol = EdgeCommonUtil.getEdgeWebServiceProtocol();
		int    edgePort = EdgeCommonUtil.getEdgeWebServicePort();
		String edgeWSDL = "";
		edgeWSDL = com.ca.arcserve.edge.app.base.webservice.WebServiceFactory.getGateWayWSDL(edgeHostName, edgePort, edgeProtocol);
		String consoleUrl = EdgeCommonUtil.getConsoleUrl( edgeHostName, edgePort, edgeProtocol );
		RPSRegInfo regInfo = new RPSRegInfo();
		regInfo.setRpsAppHostName(edgeHostName);
		regInfo.setRpsAppWSDL(edgeWSDL);
		regInfo.setConsoleUrl( consoleUrl );
		String encryptUUID = BackupService.getInstance().getNativeFacade().encrypt(edgeUUID);
		regInfo.setRpsAppUUID(encryptUUID);
		regInfo.setRpsAppLocale(Locale.getDefault().toString());
		regInfo.setRpsConnectNameList(CommonUtil.getConnectNameList());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(EdgeInstallPath + CommonUtil.BaseDeploymentInstallPath + "RegConfigRPSApp.xml");
			JAXB.marshal(regInfo, fos);
		} catch (Exception e) {
			logger.error("Generate RegConfigRPSApp.xml failed", e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public static boolean isReachableByPing(String ip) {
		
		BufferedReader br = null;
		
	    try {
	    	
	        String command;

	        if(System.getProperty("os.name").toLowerCase().startsWith("windows")) {
	            // For Windows
	            command = "ping -n 2 " + ip;
	        } else {
	            // For Linux and OSX
	            command = "ping -c 2 " + ip;
	        }
	        Process proc = Runtime.getRuntime().exec(command);
	        
	        br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	        
	        List<String> result = new LinkedList<String>();
	        String line = null;
	        while((line = br.readLine()) != null) {
	            result.add(line);
	         }
	        
	        return checkAvailability(result);
	        
	    } catch(IOException  ex) {
	    	
	    	return false;
	       
	    } finally{
	    	if(br != null){
	    		try {
					br.close();
				} catch (IOException e) {
				}
	    	}
	    }
	}
	
	private static boolean checkAvailability(List<String> outputLines) {
	    for(String line : outputLines) {
	        if(line.contains("unreachable")) {
	            return false;
	        }
	        if(line.contains("TTL=")) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public static String getConsoleUrl( String hostName, int port, String protocol )
	{
		String wsdlUrl = WebServiceFactory.getEdgeWSDL( hostName, port, protocol );
		return wsdlUrl.split("services")[0];
	}
	
	public static String getGatewayHostNameByNodeId(int nodeId){
		GatewayEntity gatewayEntity = null;
		String gatewayHostName = getLocalFqdnName();
		try {
			gatewayEntity = gatewayService.getGatewayByHostId(nodeId);
		} catch (EdgeServiceFault e) {
			logger.error("[LogUtility] getGatewayHostNameByNodeId() failed.",e);
		}
		if(gatewayEntity != null 
				&& gatewayEntity.getHostName() != null 
				&& !gatewayEntity.getHostName().equalsIgnoreCase("localhost")
				&& !gatewayEntity.getHostName().equalsIgnoreCase("127.0.0.1")){
			gatewayHostName = gatewayEntity.getHostName();
		}
		return gatewayHostName;
	}
	
	public static String getGatewayHostNameByGateWayId(GatewayId gatewayId){
		GatewayEntity gatewayEntity = null;
		String gatewayHostName = getLocalFqdnName();
		try {
			gatewayEntity = gatewayService.getGatewayById(gatewayId);
		} catch (EdgeServiceFault e) {
			logger.error("[LogUtility] getGatewayHostNameByNodeId() failed.",e);
		}
		if(gatewayEntity != null 
				&& gatewayEntity.getHostName() != null 
				&& !gatewayEntity.getHostName().equalsIgnoreCase("localhost")
				&& !gatewayEntity.getHostName().equalsIgnoreCase("127.0.0.1")){
			gatewayHostName = gatewayEntity.getHostName();
		}
		return gatewayHostName;
	}
	
	public static int compareWithConsoleVersion(VersionInfo agentVersion){
		EdgeVersionInfo edgeVersionInfo = null;
		try {
			edgeVersionInfo = commonService.getVersionInformation();
		} catch (EdgeServiceFault e) {
			logger.error("[EdgeCommonUtil] compareWithConsoleVersion() get console version failed."); 
		}
		if(edgeVersionInfo == null)
			return 0;
		String edgeVersionString = edgeVersionInfo.getVersionString()+"."+(StringUtil.isEmptyOrNull(edgeVersionInfo.getUpdateNumber())?"0":edgeVersionInfo.getUpdateNumber());
		String nodeVersion = getVersionString(agentVersion.getMajorVersion())+"."
				+getVersionString(agentVersion.getMinorVersion())+"."
				+getVersionString(agentVersion.getBuildNumber())+"."
				+getVersionString(agentVersion.getUpdateNumber());
		return nodeVersion.compareTo(edgeVersionString);
	}
	
	public static int compareWithConsoleVersion(int hostId){
		EdgeVersionInfo edgeVersionInfo = null;
		try {
			edgeVersionInfo = commonService.getVersionInformation();
		} catch (EdgeServiceFault e) {
			logger.error("[EdgeCommonUtil] compareWithConsoleVersion() get console version failed."); 
		}
		if(edgeVersionInfo == null)
			return 0;
		String edgeVersionString = edgeVersionInfo.getVersionString()+"."+(StringUtil.isEmptyOrNull(edgeVersionInfo.getUpdateNumber())?"0":edgeVersionInfo.getUpdateNumber());
		String nodeVersion = getVersionByHostId(hostId);
		return nodeVersion.compareTo(edgeVersionString);
	}
	
	public static int compareNodeVersion(int hostId1, int hostId2){
		if(hostId1 == 0 || hostId2 == 0){
			return hostId1 - hostId2;
		}
		
		String version1 = getVersionByHostId(hostId1);
		String version2 = getVersionByHostId(hostId2);
		
		return version1.compareTo(version2);
	}
	
	public static String getVersionByHostId(int hostId){
		String version = "";
		List<EdgeConnectInfo> connectInfoList = new ArrayList<EdgeConnectInfo>();
		connectInfoDao.as_edge_connect_info_list( hostId, connectInfoList );
		if(!connectInfoList.isEmpty()){
			EdgeConnectInfo connectInfo = connectInfoList.get(0);
			version = getVersionString(connectInfo.getMajorversion())+"."
					+getVersionString(connectInfo.getMinorversion())+"."
					+getVersionString(connectInfo.getBuildnumber())+"."
					+getVersionString(connectInfo.getUpdateversionnumber());
		}
		return version;
	}
	
	//major.minor Update updatenumber
	public static String getDisplayVersionByHostId(int hostId){
		StringBuilder version = new StringBuilder("");
		String majorVersion = "0";
		String minorVersion = "0";
		String updateVersion = "0";
		List<EdgeConnectInfo> connectInfoList = new ArrayList<EdgeConnectInfo>();
		connectInfoDao.as_edge_connect_info_list( hostId, connectInfoList );
		if(!connectInfoList.isEmpty()){
			EdgeConnectInfo connectInfo = connectInfoList.get(0);
			if(connectInfo.getMajorversion()!=null && !connectInfo.getMajorversion().isEmpty()){
				majorVersion = connectInfo.getMajorversion();
			}
			if(connectInfo.getMinorversion() != null && !connectInfo.getMinorversion().isEmpty()){
				minorVersion = connectInfo.getMinorversion();
			}
			if(connectInfo.getUpdateversionnumber() != null && !connectInfo.getUpdateversionnumber().isEmpty()){
				updateVersion = connectInfo.getUpdateversionnumber();
			}
			version.append(EdgeCMWebServiceMessages.getMessage("versionMajor",majorVersion,minorVersion));
			if(!updateVersion.equals("0")){
				version.append(" ").append(EdgeCMWebServiceMessages.getMessage("versionUpdate",updateVersion));
			}
		}
		return version.toString();
	}
	
	//major.minor Update updatenumber
	public static String getDisplayVersionByHostId(VersionInfo agentVersion){
		StringBuilder version = new StringBuilder("");
		String majorVersion = "0";
		String minorVersion = "0";
		String updateVersion = "0";
		if(agentVersion.getMajorVersion()!=null && !agentVersion.getMajorVersion().isEmpty()){
			majorVersion = agentVersion.getMajorVersion();
		}
		if(agentVersion.getMinorVersion()!=null && !agentVersion.getMinorVersion().isEmpty()){
			minorVersion = agentVersion.getMinorVersion();
		}
		if(agentVersion.getUpdateNumber() != null && !agentVersion.getUpdateNumber().isEmpty()){
			updateVersion = agentVersion.getUpdateNumber();
		}
		version.append(EdgeCMWebServiceMessages.getMessage("versionMajor",majorVersion,minorVersion));
		if(!updateVersion.equals("0")){
			version.append(" ").append(EdgeCMWebServiceMessages.getMessage("versionUpdate",updateVersion));
		}
		return version.toString();
	}
	
	
	private static String getVersionString( String v ){
        return v!= null && !v.equals("")  ? v : "0" ;
	}
	
	public static String getLocalFqdnName(){
		try {
			String fqdnNameBeSet = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT,WindowsRegistry.CONSOLE_FQDN_NAME);
			if(!StringUtil.isEmptyOrNull(fqdnNameBeSet))
				return fqdnNameBeSet.toLowerCase();
			String fqdnName = CommonService.getInstance().getNativeFacade().getHostFQDN();
			if(!StringUtil.isEmptyOrNull(fqdnName)){
				return fqdnName.toLowerCase();
			}else {
				return "";
			}
		} catch (Exception e) {
			logger.error("[EdgeCommonServiceImpl] getLocalFqdnName() failed.",e);
			return "";
		}
	}
	
	
	//------------------update connect info-----------
	public static void updateSameNameMachineConnectInfo(String hostName, int[] ignoreIds){
		logger.debug("[EdgeCommonUtil] Begin to update same name nodes connect info.");
		try {
			List<Integer> otherNodes = getHostIdsByHostName(hostName);
			for(Integer id : otherNodes){
				boolean ignore = false;
				for (int i = 0; i < ignoreIds.length; i++) {
					if(id == ignoreIds[i]){
						ignore = true;
						break;
					}
				}
				if(!ignore){ // not update itself
					updateConnectInfoById(id);
					logger.debug("[EdgeCommonUtil] update same name: "+hostName+" version. node id is: "+id);
				}
			}
		} catch (Exception e) {
			logger.error("[EdgeCommonUtil] update same name nodes connect info failed. ",e);
		}
		logger.debug("[EdgeCommonUtil] Ento to update same name nodes connect info.");
	}
	
	private static List<Integer> getHostIdsByHostName(String hostName){
		List<Integer> nodeIdList = new ArrayList<Integer>();
		List<IntegerId> ids = new LinkedList<IntegerId>();
		hostMgrDao.as_edge_host_getIdsByHostName(hostName, ids);
		for (IntegerId id : ids) {
			if(id.getId() > 0)
				nodeIdList.add(id.getId());
		}
		return nodeIdList;
	}
	
	private static void updateConnectInfoById(int hostId){
		try {
			NodeDetail node = nodeService.getNodeDetailInformation(hostId);
			GatewayEntity gateway = gatewayService.getGatewayByHostId(hostId);
			String protocol = "http";
			if(Protocol.Https == node.getD2dConnectInfo().getProtocol())
				protocol = "https";
			RemoteNodeInfo nodeInfo = nodeService.queryRemoteNodeInfo(gateway.getId(), hostId, 
					node.getHostname(), node.getUsername(), node.getPassword(), 
					protocol, node.getD2dConnectInfo().getPort());
			if(isRpsAndRpsInstalled(node, nodeInfo)
					|| isAgentAndAgentInstalled(node, nodeInfo)){
				List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
				connectInfoDao.as_edge_connect_info_list(hostId, connInfoLst);
				if(connInfoLst.size() > 0){
					connectInfoDao.as_edge_connect_info_update(hostId, node.getUsername(),node.getPassword(),
							connInfoLst.get(0).getUuid(), nodeInfo.getD2DProtocol().ordinal(), nodeInfo.getD2DPortNumber(), 
							connInfoLst.get(0).getType(), nodeInfo.getD2DMajorVersion(), nodeInfo.getD2DMinorVersion(), nodeInfo.getUpdateVersionNumber(), 
							nodeInfo.getD2DBuildNumber(), connInfoLst.get(0).getManaged());
				}
				String version = DeployCommonUtil.getVersionString(nodeInfo.getD2DMajorVersion()+"."+DeployCommonUtil.getVersionString(nodeInfo.getD2DMinorVersion())
						+ "."+DeployCommonUtil.getVersionString(nodeInfo.getUpdateVersionNumber()) + "."+DeployCommonUtil.getVersionString(nodeInfo.getD2DBuildNumber()));
						
				logger.info("[EdgeCommonUtil] updateConnectInfoById() Have updated the connect info of node: "
						+hostId+"_"+node.getHostname()+" the protocol is: "+nodeInfo.getD2DProtocol().ordinal()+"the port is: "+
						nodeInfo.getD2DPortNumber()+" , the version is: " + version);
			}
		} catch (Exception e) {
			logger.error("[EdgeCommonUtil] updateConnectInfoById(hostId) failed. the host id is: "+hostId,e);
		}
	}
	
	private static boolean isRpsAndRpsInstalled(NodeDetail node, RemoteNodeInfo nodeInfo){
		if(node==null || nodeInfo == null)
			return false;
		if(((node.getProtectionTypeBitmap() & ProtectionType.RPS.getValue()) ==	ProtectionType.RPS.getValue())
				&& nodeInfo.isRPSInstalled()){
			return true;
		}
		return false;
	}
	
	private static boolean isAgentAndAgentInstalled(NodeDetail node, RemoteNodeInfo nodeInfo){
		if(node==null || nodeInfo == null)
			return false;
		if(((node.getProtectionTypeBitmap() & ProtectionType.WIN_D2D.getValue()) ==	ProtectionType.WIN_D2D.getValue())
				&& nodeInfo.isD2DInstalled()){
			return true;
		}
		return false;
	}
	//------------------end update connect info-------
}
