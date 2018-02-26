package com.ca.arcserve.edge.app.base.webservice.jni;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcflash.webservice.data.PM.PatchInfo;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.jni.BaseWSJNI;
import com.ca.arcserve.edge.app.base.jni.EdgeJNIException;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.GDBServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AdminAccountValidationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.webservice.jni.model.ComputerNameType;
import com.ca.arcserve.edge.webservice.jni.model.DeployD2DConstants;
import com.ca.arcserve.edge.webservice.jni.model.DomainUser;
import com.ca.arcserve.edge.webservice.jni.model.EdgeAccount;
import com.ca.arcserve.edge.webservice.jni.model.HttpDownloadResult;
import com.ca.arcserve.edge.webservice.jni.model.HttpProxySettings;
import com.ca.arcserve.edge.webservice.jni.model.IDownloadStatusCallback;
import com.ca.arcserve.edge.webservice.jni.model.IHttpDownloadCallback;
import com.ca.arcserve.edge.webservice.jni.model.JD2DPatchInfo;
import com.ca.arcserve.edge.webservice.jni.model.JNode;
import com.ca.arcserve.edge.webservice.jni.model.ScanNodeInfo;


public class WSJNI {
	
	private static final Logger logger = Logger.getLogger(WSJNI.class);
	
	public static  int getEdgeAccount(EdgeAccount edgeAccount){
		return BaseWSJNI.getEdgeAccount(edgeAccount);
	}
	
	public static int saveEdgeAccount(String username, String password){
		return BaseWSJNI.saveEdgeAccount(username, password);
	}
	
	private static  int verifyAdminAccount(String computerName, String userName, @NotPrintAttribute String password){
		return BaseWSJNI.verifyAdminAccount(computerName, userName, password);
	}
	private static  int scanRemoteNode(String edgeUser, String edgeDomain, @NotPrintAttribute String edgePassword, String computerName, String userName, @NotPrintAttribute String password, ScanNodeInfo nodeInfo){
		return BaseWSJNI.scanRemoteNode(edgeUser, edgeDomain, edgePassword, computerName, userName, password, nodeInfo);
	}
	
	private static  int getDcList(String domainName, ArrayList<String> retArr){
		return BaseWSJNI.getDcList(domainName, retArr);
	}
	private static  int verifyADAccount(String adServerName, String adUser,@NotPrintAttribute String adPassword){
		return BaseWSJNI.verifyADAccount(adServerName, adUser, adPassword);
	}
	private static  int browseNodes(String adServerName, String adUser,@NotPrintAttribute String adPassword, String computerName, String computerOS, boolean bSQL, boolean bExch, ArrayList<JNode> retArr){
		return BaseWSJNI.browseNodes(adServerName, adUser, adPassword, computerName, computerOS, bSQL, bExch, retArr);
	}
	static  int deploy(String serverListFile){
		return BaseWSJNI.deploy(serverListFile);
	}
	public static  String AFEncryptString(@NotPrintAttribute String str){
		return BaseWSJNI.AFEncryptString(str);
	}
	public static  String AFDecryptString(@NotPrintAttribute String str){
		return BaseWSJNI.AFDecryptString(str);
	}
	
	public static String getWindowsDirectory()
	{
		return BaseWSJNI.getWindowsDirectory();
	}

	/**
	 * @return int value:1 - user/password is wrong
	 * 					 2 - user/password is passed but non-administrator.
	 * 					 0 - the user is valid and in the administrator group
	 */
	public static  int validate(String username, String domain,@NotPrintAttribute String password){
		return BaseWSJNI.validate(username, domain, password);
	}

	private static final int 	ERROR_AD_OK				= 0;
//	private static final int 	ERROR_AD_NOTOPERATIONAL	= 1;
	private static final int 	ERROR_AD_INVALIDUSER	= 2;
	private static final int 	ERROR_AD_PRIVILEGE		= 3;
	private static final int 	ERROR_AD_DOMAIN_CANNOT_BE_CONTATCED = 1355;

	private static final int 	ERROR_DISCOVERY_SUCCESS 		= 0x00000000;
	private static final int 	ERROR_DISCOVERY_ACCESS_DENIED 		= 0x00000005;
//	private static final int 	ERROR_DISCOVERY_INVALID_HANDLE		= 0x00000006;
	private static final int 	ERROR_DISCOVERY_BAD_NETPATH   		= 0x00000035;
//	private static final int 	ERROR_DISCOVERY_INVALID_NAME		= 0x0000007B;
	private static final int 	ERROR_DISCOVERY_INVALID_PASSWORD	= 0x00000056;
	private static final int 	ERROR_DISCOVERY_INVALID_USER_OR_PASSWORD        = 0x0000052E;
	private static final int 	ERROR_DISCOVERY_ACCOUNT_RESTRICTION        = 0x0000052F;
//	private static final int 	ERROR_DISCOVERY_INVALID_PARAMETER	= 0x00000057;
//	private static final int 	ERROR_DISCOVERY_WAIT_TIMEOUT		= 0x00000102;
	private static final int 	ERROR_DISCOVERY_BAD_NET_NAME		= 0x00000043;
	private static final int 	ERROR_DISCOVERY_SERVICE_DISABLED 	= 0x00000422;
	private static final int 	ERROR_DISCOVERY_SERVICE_LOGON_FAILED 	= 0x0000042D;
	private static final int 	ERROR_DISCOVERY_SERVICE_REQUEST_TIMEOUT = 0x0000041D;
	private static final int	ERROR_DISCOVERY_MEMBER_NOT_IN_GROUP	= 0x00000529;

	public static AdminAccountValidationResult validateAdminAccount(String computerName, String userName,@NotPrintAttribute String password) throws EdgeServiceFault {
		if(password == null)	password = "";
		int ret = verifyAdminAccount(computerName, userName, password);
		return generateResult(ret, computerName);
	}
	public static AdminAccountValidationResult generateResult(int ret, String computerName) throws EdgeServiceFault {
		switch(ret)
		{
			case ERROR_DISCOVERY_SUCCESS:
				return AdminAccountValidationResult.Succeed;

			case ERROR_DISCOVERY_INVALID_USER_OR_PASSWORD:
			case ERROR_DISCOVERY_INVALID_PASSWORD:
			{
				String msg = String.format("Invalid user credentials");
				EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Login_WrongCredential, msg);
				EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
				throw esf;
			}
			case ERROR_DISCOVERY_ACCESS_DENIED:
			case ERROR_DISCOVERY_MEMBER_NOT_IN_GROUP:
			{
				String msg = String.format("Administrator privilege is required");
				EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Login_NotAdministrator, msg);
				EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
				throw esf;
			}
			case ERROR_DISCOVERY_BAD_NET_NAME:
			case ERROR_DISCOVERY_BAD_NETPATH: // wrong node name
			{
				String msg = String.format("Cannot connect to the specified server. Please check the server name and share \\\\%s\\ADMIN$", computerName);
				EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Login_WrongNode, msg);
				EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
				throw esf;
			}
			case ERROR_DISCOVERY_ACCOUNT_RESTRICTION:
			{
				String msg = String.format("Login failure: user account restriction. Possible reason is blank passwords not allowed.");
				EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Login_EmptyPassword, msg);
				EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
				throw esf;
			}
			default:
			{
				String msg = "Failed to validate admin account, error code = " + ret;
				
				Logger logger = Logger.getLogger(WSJNI.class);
				logger.error(msg);
				
				if (EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.vShpereManager) {
					return AdminAccountValidationResult.NotReachable;
				}
				
				EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Login_Fail, msg);
				EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
				throw esf;
			}
		}
	}
	
	public static AdminAccountValidationResult verifyHyperVAdminAccount(String computerName, String userName, String password, boolean isCluster) throws EdgeServiceFault {
		if(password == null)	password = "";
		int ret = com.ca.arcflash.webservice.jni.WSJNI.verifyHyperVAdminAccount(computerName, userName, password, isCluster);
		return generateResult(ret, computerName);
	}
	
	public static boolean isUserExists(String userName) throws EdgeServiceFault {
		try {
			return BaseWSJNI.isUserExists(userName);
		} catch (Throwable e) {
			logger.error("check user exists failed, error message = " + e.getMessage());
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
		}
	}
	
	public static void validateLocalAccount(String userName, String password) throws EdgeServiceFault {
		int ret;
		
		try {
			ret = BaseWSJNI.verifyLocalAccount(userName, password);
		} catch (Throwable e) {
			logger.error("validate local account failed, error message = " + e.getMessage());
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
		}
		
		if (ret != 0) {
			String errorMessage = "validate local account failed, return code = " + ret;
			logger.debug(errorMessage);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Login_WrongCredential, errorMessage);
		}
	}
	
	public static void validateUserLocalGroup(String userName, String localGroupName) throws EdgeServiceFault {
		int ret;
		
		try {
			ret = BaseWSJNI.verifyUserLocalGroup(userName, localGroupName);
		} catch (Throwable e) {
			logger.error("validate user local group failed, error message = " + e.getMessage());
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
		}
		
		if (ret != 0) {
			String errorMessage = "validate user local group failed, return code = " + ret;
			logger.debug(errorMessage);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Login_WrongCredential, errorMessage);
		}
	}
	
	public static List<String> getLocalUserNames(String localGroupName) throws EdgeServiceFault {
		List<String> localUserNames = new ArrayList<String>();
		
		try {
			BaseWSJNI.getLocalUserNames(localGroupName, localUserNames);
		} catch (Throwable e) {
			logger.error("get all local usernames failed, error message = " + e.getMessage());
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
		}
		
		return localUserNames;
	}

	static void verifyActiveDirectoryAccount(String adServerName, String adUser,@NotPrintAttribute String adPassword) throws EdgeServiceFault {
		if(adPassword == null)	adPassword = "";
		int ret = verifyADAccount(adServerName, adUser, adPassword);

		if (ret == ERROR_AD_OK) {
			return;
		}
		else if (ret == ERROR_AD_INVALIDUSER)
		{
			// String msg = String.format("Invalid AD user information");
			String msg = EdgeCMWebServiceMessages.getMessage("autoDiscovery_InvalidUser_WithName", adUser);
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_InvalidUser_WithName, msg);
			bean.setMessageParameters(new Object[]{adUser});
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
		else if (ret == ERROR_AD_PRIVILEGE)
		{
			Logger logger = Logger.getLogger(WSJNI.class);
			logger.error("The specified user account does not have enough privilege to perform this operation.");

			// String msg = String.format("Not enough user privilege");
			String msg = EdgeCMWebServiceMessages.getMessage("autoDiscovery_UserPrivilegeNotEnough");
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_UserPrivilegeNotEnough, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
		else if (ret == ERROR_AD_DOMAIN_CANNOT_BE_CONTATCED)
		{
			Logger logger = Logger.getLogger(WSJNI.class);
			logger.error("The specified domain either does not exist or could not be contacted with user account " + adUser +
				" and AD server: '" + adServerName + "'");
			
			// String msg = String.format("The specified domain either does not exist or could not be contacted.");
			String gatewayHost = EdgeCommonUtil.getLocalFqdnName();
			String msg = EdgeCMWebServiceMessages.getMessage("autoDiscovery_DomainCannotBeContatced",gatewayHost);
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_DomainCannotBeContatced, msg);
			bean.setMessageParameters(new String[]{gatewayHost});
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
		else
		{
			Logger logger = Logger.getLogger(WSJNI.class);
			logger.error("Failed to obtain nodes from Active Directory, error code = " + Integer.toString(ret));
			
			// String msg = String.format("Can't obtain nodes from AD");
			String msg = EdgeCMWebServiceMessages.getMessage("autoDiscovery_CantObtainNodesFromAD");
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_CantObtainNodesFromAD, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
	}

	static List<JNode> browseNodes(String adServerName, String adUser,@NotPrintAttribute String adPassword, String computerName, String computerOS, boolean bSQL, boolean bExch) throws EdgeServiceFault {
		if(adPassword == null)	adPassword = "";

		ArrayList<JNode> retArr = new ArrayList<JNode>();
		int ret = browseNodes(adServerName, adUser, adPassword, computerName, computerOS, bSQL, bExch, retArr);

		if (ret == ERROR_AD_OK) {
			return retArr;
		}
		else if (ret == ERROR_AD_INVALIDUSER)
		{
			String msg = String.format("Invalid AD user information");
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_InvalidUser, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
		else if (ret == ERROR_AD_PRIVILEGE)
		{
			String msg = String.format("Not enough user privilege");
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_UserPrivilegeNotEnough, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
		else if (ret == ERROR_AD_DOMAIN_CANNOT_BE_CONTATCED)
		{
			Logger logger = Logger.getLogger(WSJNI.class);
			logger.error("The specified domain either does not exist or could not be contacted with user account " + adUser);
			
			String msg = String.format("The specified domain either does not exist or could not be contacted.");
			String gatewayHostName = EdgeCommonUtil.getLocalFqdnName();
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_DomainCannotBeContatced, msg);
			bean.setMessageParameters(new String[]{gatewayHostName});
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
		else
		{
			Logger logger = Logger.getLogger(WSJNI.class);
			logger.error("Failed to obtain nodes from Active Directory, error code = " + Integer.toString(ret));

			String msg = String.format("Can't obtain nodes from AD");
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_CantObtainNodesFromAD, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
	}

	static List<String> getDcList(String domainName) throws EdgeServiceFault {
		ArrayList<String> retArr = new ArrayList<String>();

		int ret = getDcList(domainName, retArr);
		if (ret == ERROR_AD_OK) {
			return retArr;
		}
		else if (ret == ERROR_AD_INVALIDUSER)
		{
			String msg = String.format("Invalid AD user information");
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_InvalidUser, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
		else if (ret == ERROR_AD_PRIVILEGE)
		{
			String msg = String.format("Not enough user privilege");
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_UserPrivilegeNotEnough, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
		else
		{
			Logger logger = Logger.getLogger(WSJNI.class);
			logger.error("Failed to get domain controller list, error code = " + Integer.toString(ret));
			
			String msg = String.format("Can't obtain nodes from AD");
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_CantObtainNodesFromAD, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
	}

	static RemoteNodeInfo scanRemoteNode(String edgeUser, String edgeDomain, @NotPrintAttribute String edgePassword, String computerName, String userName,@NotPrintAttribute String password) throws EdgeServiceFault {
		ScanNodeInfo nodeInfo = new ScanNodeInfo();
		if(edgePassword == null) 	edgePassword = "";
		if(password == null)	password = "";

		int ret = scanRemoteNode(edgeUser, edgeDomain, edgePassword, computerName, userName, password, nodeInfo);
		logger.debug("[WSJNI] scanRemoteNode() return code is :"+ret);
		if(ret == ERROR_DISCOVERY_ACCESS_DENIED)
		{
			try
			{
				InetAddress[] localaddrs = InetAddress.getAllByName(computerName);
				for(InetAddress addrs : localaddrs)
				{
					ret = scanRemoteNode( edgeUser, edgeDomain, edgePassword, addrs.getHostAddress(), userName, password, nodeInfo);
					if(ret == ERROR_DISCOVERY_SUCCESS)	break;
					ret = ERROR_DISCOVERY_ACCESS_DENIED;
				}
			}
			catch (UnknownHostException e)
			{
			}
		}

		if(ret == ERROR_DISCOVERY_SUCCESS)
		{
			RemoteNodeInfo remoteNodeInfo = new RemoteNodeInfo();

			remoteNodeInfo.setSQLServerInstalled(nodeInfo.isSqlServerInstalled());
			remoteNodeInfo.setExchangeInstalled(nodeInfo.isExchangeInstalled());

			remoteNodeInfo.setRPSInstalled(nodeInfo.isRpsInstalled());
			remoteNodeInfo.setD2DInstalled(nodeInfo.isD2dInstalled());
			remoteNodeInfo.setD2DODInstalled(nodeInfo.isD2dodInstalled());
			if( nodeInfo.isD2dInstalled() || nodeInfo.isD2dodInstalled())
			{
				remoteNodeInfo.setD2DMajorVersion(String.valueOf(nodeInfo.getD2dMajorVersion()));
				remoteNodeInfo.setD2DMinorVersion(String.valueOf(nodeInfo.getD2dMinorVersion()));
				remoteNodeInfo.setUpdateVersionNumber(String.valueOf(nodeInfo.getUpdateVersionNumber()));
				remoteNodeInfo.setD2DBuildNumber(String.valueOf(nodeInfo.getD2dBuildNumber()));
				remoteNodeInfo.setD2DPortNumber(nodeInfo.getD2dPort());
				remoteNodeInfo.setD2DUUID(nodeInfo.getD2dUuid());

				String d2dUrl = nodeInfo.getD2dUrl();
				if( d2dUrl.toLowerCase().contains("https:"))
				{
					remoteNodeInfo.setD2DProtocol(Protocol.Https);
				}
				else if( d2dUrl.toLowerCase().contains("http:"))
				{
					remoteNodeInfo.setD2DProtocol(Protocol.Http);
				}
				else
				{
					remoteNodeInfo.setD2DProtocol(Protocol.UnKnown);
				}
			}

			remoteNodeInfo.setARCserveBackInstalled(nodeInfo.isArcserveInstalled());
			if( nodeInfo.isArcserveInstalled() )
			{
				String arcVersion = String.valueOf(nodeInfo.getArcMajorVersion()) + "." + String.valueOf(nodeInfo.getArcMinorVersion());
				remoteNodeInfo.setARCserveVersion(arcVersion);

				if(nodeInfo.isArcserveCentral())
				{
					remoteNodeInfo.setARCserveType(ABFuncServerType.GDB_PRIMARY_SERVER);
					if(nodeInfo.isArcserveBranch())
						remoteNodeInfo.setGdbType(GDBServerType.GDB_IN_BRANCH);
					else
						remoteNodeInfo.setGdbType(GDBServerType.GDB_REGULAR);
				}
				else if (nodeInfo.isArcserveBranch())
				{
					remoteNodeInfo.setARCserveType(ABFuncServerType.BRANCH_PRIMARY);
				}
				else if (nodeInfo.isArcserveStandAlone())
				{
					remoteNodeInfo.setARCserveType(ABFuncServerType.STANDALONE_SERVER);
				}
				else if (nodeInfo.isArcservePrimary())
				{
					remoteNodeInfo.setARCserveType(ABFuncServerType.NORNAML_SERVER);
				}
				else
				{
					remoteNodeInfo.setARCserveType(ABFuncServerType.ARCSERVE_MEMBER);
				}

				String arcProtocol = nodeInfo.getArcProtocol();
				if( arcProtocol.toLowerCase().contains("https"))
				{
					remoteNodeInfo.setARCserveProtocol(Protocol.Https);
					remoteNodeInfo.setARCservePortNumber(nodeInfo.getArcPort());
				}
				else if(arcProtocol.toLowerCase().contains("http"))
				{
					remoteNodeInfo.setARCserveProtocol(Protocol.Http);
					remoteNodeInfo.setARCservePortNumber(nodeInfo.getArcPort());
				}
				else
				{
					remoteNodeInfo.setARCserveProtocol(Protocol.UnKnown);
				}
			}
			logger.debug("nodeInfo.isConsoleInstalled() is: " + nodeInfo.isConsoleInstalled());
			
			if( nodeInfo.isConsoleInstalled())
			{
				remoteNodeInfo.setConsoleInstalled(true);
				remoteNodeInfo.setConsolePortNumber(nodeInfo.getConsolePort());
				remoteNodeInfo.setConsoleUUID(nodeInfo.getConsoleGUID());
				String consoleUrl = nodeInfo.getConsoleUrl();
				if( consoleUrl.toLowerCase().contains("https:"))
				{
					remoteNodeInfo.setConsoleProtocol(Protocol.Https);
				}
				else if( consoleUrl.toLowerCase().contains("http:"))
				{
					remoteNodeInfo.setConsoleProtocol(Protocol.Http);
				}
				else
				{
					remoteNodeInfo.setConsoleProtocol(Protocol.UnKnown);
				}
			}


			if(nodeInfo.getOsVersion() != null && !nodeInfo.getOsVersion().isEmpty())
			{
				remoteNodeInfo.setOsVersion(nodeInfo.getOsVersion());
			}

			if(nodeInfo.getOsName() != null && !nodeInfo.getOsName().isEmpty())
			{
				remoteNodeInfo.setOsDescription(nodeInfo.getOsName());
			}
			if(nodeInfo.getOsType() != null && !nodeInfo.getOsType().isEmpty())
			{
				remoteNodeInfo.setOsType(nodeInfo.getOsType());
			}

			return remoteNodeInfo;
		}
		else if(ret == ERROR_DISCOVERY_INVALID_USER_OR_PASSWORD)
		{
			String msg = String.format("Invalid user credentials");
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_RemoteRegistry_WrongCredential, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
		else if(ret == ERROR_DISCOVERY_INVALID_PASSWORD)
		{
			String msg = String.format("Invalid user credentials");
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_RemoteRegistry_WrongCredential, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
		else if(ret == ERROR_DISCOVERY_ACCESS_DENIED)
		{
			String msg = String.format("Administrator privilege is required");
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_RemoteRegistry_NoPermission, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
		else if((ret == ERROR_DISCOVERY_SERVICE_DISABLED) || (ret == ERROR_DISCOVERY_SERVICE_LOGON_FAILED) || (ret == ERROR_DISCOVERY_SERVICE_REQUEST_TIMEOUT) )
		{
			String msg = String.format("Can't start Remote Registry service on remote node");
			String errorCode = EdgeServiceErrorCode.Node_RemoteRegistry_CantStartService;
			if (EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.VirtualConversionManager)
				errorCode = EdgeServiceErrorCode.Node_RemoteRegistry_CantStartService_VCMAPP;
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
				errorCode, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
		else
		{
			Logger logger = Logger.getLogger(WSJNI.class);
			logger.error("Failed to get application information through Remote Registry, error code = " + Integer.toString(ret));

			String msg = String.format("Failed to read values through Remote Registry");
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Node_RemoteRegistry_FailedToRead, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
	}
	
	static int createMailSlot( String mailSlotString ) throws EdgeServiceFault{
		try{
			return BaseWSJNI.createMailSlot(mailSlotString);
		}catch(EdgeJNIException e){
			String msg = "Fail to create mail slot " + mailSlotString + " , " + e.getMessage();
			logger.error(msg);
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(String.valueOf(e.getErrorCode()), msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
			throw esf;
		}
	}
	
    static PatchInfo loadD2DPatchInfoFromDll(String dllFilePath){
		try{
			JD2DPatchInfo patchInfo = new JD2DPatchInfo();
			int ret = BaseWSJNI.getD2DPatchInfo(dllFilePath, patchInfo);
			if(ret != 0) {
				logger.error("getD2DPatchInfo failed, return code:" + ret);
				return null;
			}
			PatchInfo d2dPatchInfo = new PatchInfo();
			d2dPatchInfo.setMajorversion(Integer.parseInt(patchInfo.getMajorversion()));
			d2dPatchInfo.setMinorVersion(Integer.parseInt(patchInfo.getMinorVersion()));
			d2dPatchInfo.setPatchVersionNumber(Integer.parseInt(patchInfo.getPatchVersionNumber()));
			d2dPatchInfo.setPackageID(patchInfo.getPackageID());
			d2dPatchInfo.setPatchURL(patchInfo.getPatchURL());
			d2dPatchInfo.setPublishedDate(patchInfo.getPublishedDate());
			return d2dPatchInfo;
		}catch(Exception e){
			logger.error(e);
			return null;
		}
    }
    
    static int isDeployingD2D()
    {
    	int returnCode = BaseWSJNI.isDeployingD2D();
    	
    	if ((returnCode != DeployD2DConstants.NotDeploying) &&
    		(returnCode != DeployD2DConstants.Deploying))
    		logger.error( "Error getting D2D deploying status. Return code: " + returnCode );
    	
    	return returnCode;
    }
    
    public static boolean initFusion2Img( String logpath ) {
    	return BaseWSJNI.initFusion2Image( logpath );  	
    }
    public static boolean fusion2Image( String xmlPath, int width, int height, String swfpath, String imageFile){   
    	
    	try {
    		String format = "failed export image for swffile: %s with error code: %d";
	    	int ret = BaseWSJNI.fusion2Image( xmlPath,  width,  height, swfpath, imageFile);
	    	if(ret != 0) {
				logger.error( String.format(format, swfpath , ret) );
				return false;
			}
	    	return true;
    	}
    	catch( Exception e ){
    		logger.error(e.getMessage(), e);
    		return false;
    	}
    }
    private static final int dotNetInstallMark = 0; 
    private static final int dotNetNotInstallMark = 1; 
    public static boolean isDotNetExist(  String key, String valueName  ){
    	boolean bret = false;
    	try {
	    	String format1 = "the dotnet version: %s not exist" ;
	    	String format2 = "failed query dotNet information %s in registry with error code %s";
	    	int result = BaseWSJNI.windowRegDWORDValue(key, valueName);
	    	if( result == dotNetInstallMark ){
	    		bret =true;
	    	}
	    	else if( result == dotNetNotInstallMark ) {
	    		logger.debug( String.format(format1, key ) );
	    	}
	    	else  {
	    		logger.error( String.format(format2, key, result) );
			}
	    	return bret;
    	}
    	catch( Throwable e ) {
    		logger.error( e.getMessage(), e );
    		return false;
    	}
    }
    
    public static void setClusterAccessHint(String serverName, String username, String password) {
    	com.ca.arcflash.webservice.jni.WSJNI.setClusterAccessHint(serverName, username, password);
    }
    
    public static int isD2DDeployProcessRunning( String path )
    {
    	return BaseWSJNI.isD2DDeployProcessRunning( path );
    }
    
    public static String[] getClusterVirtualByPyhsicalNode( String serverName,
		String user, String password )
	{
		return com.ca.arcflash.webservice.jni.WSJNI.getClusterVirtualByPyhsicalNode(serverName, user, password);
	}
	 //Jan sprint
  	public static int validateNASServer(String nasServerName, String userName,@NotPrintAttribute String password, String port, String protocol){
  		if(password == null)	password = "";
  		return BaseWSJNI.validateNASServer(nasServerName, userName, password, port, protocol);
  	}
  	
	public static int updateASBUDomainName(String cmdDir, String newName,
			String usrName, String usrPwd, String carootPwd) {
		return BaseWSJNI.updateASBUDomainName(cmdDir, newName,
				usrName, usrPwd, carootPwd);
	}

	public static int updateASBUPrimaryServerName(String cmdDir, String usrName,
			String usrPwd, String carootPwd) {
		return BaseWSJNI.updateASBUPrimaryServerName(cmdDir,usrName, usrPwd, carootPwd);
	}
	
	public static long urlDownloadToFile(
		String url, String saveAs, IDownloadStatusCallback statusCallback )
	{
		return BaseWSJNI.urlDownloadToFile( url, saveAs, statusCallback );
	}
	
	public static boolean httpDownload( String server, int port, boolean isHttps,
		String serverPath, String saveAs, IHttpDownloadCallback callback,
		HttpDownloadResult result )
	{
		return BaseWSJNI.httpDownload( server, port, isHttps, serverPath, saveAs, callback, result );
	}
	
	public static long getAllDomainUsers( String domainName,
		String dcName, String userName, String password, Date changeTime, List<DomainUser> userList )
	{
		return BaseWSJNI.getAllDomainUsers( domainName, dcName, userName, password, changeTime, userList );
	}
	
	public static long getIEProxySettings( String username, String password, HttpProxySettings proxySettings )
	{
		return BaseWSJNI.getIEProxySettings( username, password, proxySettings );
	}
	
	public static long getAccountDomain(
		String accountName, String domain, String password, StringBuffer normalizedDomain )
	{
		if (accountName == null)
			throw new IllegalArgumentException( "accountName is null" );
		
		if (normalizedDomain == null)
			throw new IllegalArgumentException( "normalizedDomain is null" );
		
		return BaseWSJNI.getAccountDomain( accountName, domain, password, normalizedDomain );
	}
	
	public static boolean getComputerName( ComputerNameType nameType, StringBuffer name )
	{
		if (nameType == null)
			throw new IllegalArgumentException( "nameType is null" );
		
		if (name == null)
			throw new IllegalArgumentException( "name is null" );
		
		return BaseWSJNI.getComputerName( nameType.getValue(), name );
	}
}
