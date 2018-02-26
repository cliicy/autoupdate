package com.ca.arcserve.edge.app.base.webservice.jni;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcflash.ha.model.ESXServerInfo;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.PM.PatchInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.jni.WSJNIException;
import com.ca.arcflash.webservice.jni.model.JHypervInfo;
import com.ca.arcflash.webservice.jni.model.JHypervPFCDataConsistencyStatus;
import com.ca.arcflash.webservice.jni.model.JHypervVMInfo;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcserve.edge.app.base.dao.IEncrypt;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ServiceState;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicenseMachineType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AdminAccountValidationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.webservice.jni.model.ComputerNameType;
import com.ca.arcserve.edge.webservice.jni.model.DomainUser;
import com.ca.arcserve.edge.webservice.jni.model.EdgeAccount;
import com.ca.arcserve.edge.webservice.jni.model.HttpDownloadResult;
import com.ca.arcserve.edge.webservice.jni.model.HttpProxySettings;
import com.ca.arcserve.edge.webservice.jni.model.IDownloadStatusCallback;
import com.ca.arcserve.edge.webservice.jni.model.IHttpDownloadCallback;
import com.ca.arcserve.edge.webservice.jni.model.JNode;

public class NativeFacadeImpl implements NativeFacade, IEncrypt {
	
	private static Logger logger = Logger.getLogger( NativeFacadeImpl.class );
	
	private IRemoteNativeFacade remoteNativeFacade = RemoteNativeFacadeImpl.getInstance();
	
	@Override
	public void verifyADAccount(String adServerName, String adUser, @NotPrintAttribute String adPassword) throws EdgeServiceFault {
		remoteNativeFacade.verifyADAccount(adServerName, adUser, adPassword);
	}

	public List<JNode> getNodes(String adServerName, String adUser,@NotPrintAttribute String adPassword, String computerName, String computerOS, boolean bSQL, boolean bExch) throws EdgeServiceFault {
		return remoteNativeFacade.getNodes(adServerName, adUser, adPassword, computerName, computerOS, bSQL, bExch);
	}

	@Override
	public List<String> getDcList(String domainName) throws EdgeServiceFault {
		return WSJNI.getDcList(domainName);
	}


	@Override
	public RemoteNodeInfo scanRemoteNode(String edgeUser, String edgeDomain, @NotPrintAttribute String edgePassword, String computerName, String userName, @NotPrintAttribute String password) throws EdgeServiceFault {
		return remoteNativeFacade.scanRemoteNode(edgeUser, edgeDomain, edgePassword, computerName, userName, password);
	}

	@Override
	public int validateUser(String username, @NotPrintAttribute String password, String domain) throws EdgeServiceFault {
		int result = WSJNI.validate(username, domain, password);
		switch (result) {
			case 1: throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Login_WrongCredential, "Invalid user credentials !");
			case 2: throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Login_NotAdministrator, "Not administrator's group !");
		}
		return result;
	}

	@Override
	public AdminAccountValidationResult validateAdminAccount(String computerName, String userName,@NotPrintAttribute String password) throws EdgeServiceFault {
		return remoteNativeFacade.validateAdminAccount(computerName, userName, password);
	}
	@Override
	public AdminAccountValidationResult verifyHyperVAdminAccount(String computerName, String userName, String password, boolean isCluster) throws EdgeServiceFault {
		return remoteNativeFacade.verifyHyperVAdminAccount(computerName, userName, password, isCluster);
	}
	@Override
	public int deploy(String serverListFile) {
		return WSJNI.deploy(serverListFile);
	}

	@Override
	public String AFEncryptString(@NotPrintAttribute String str)
	{
		return WSJNI.AFEncryptString(str);
	}

	@Override
	public String AFDecryptString(@NotPrintAttribute String str)
	{
		return WSJNI.AFDecryptString(str);
	}

	@Override
	public EdgeAccount getEdgeAccount()
	{
		EdgeAccount account = new EdgeAccount();
		int result = WSJNI.getEdgeAccount(account);

		return (result == 0) ? account : null;
	}

	@Override
	public String decryptString(@NotPrintAttribute String value) {
		return WSJNI.AFDecryptString(value);
	}

	@Override
	public String encryptString(@NotPrintAttribute String value) {
		return WSJNI.AFEncryptString(value);
	}

	@Override
	public String getWindowsDirectory()
	{
		return WSJNI.getWindowsDirectory();
	}

	@Override
	public int createMailSlot(String mailSlotString) throws EdgeServiceFault {
		return WSJNI.createMailSlot(mailSlotString);
	}

	@Override
	public void saveEdgeAccount(EdgeAccount account) throws EdgeServiceFault {
		String username;
		String password = "";
		if (account.getDomain()!=null && !account.getDomain().isEmpty())
			username = account.getDomain()+"\\"+account.getUserName();
		else
			username = account.getUserName();
		
		if (account.getPassword()!=null && !account.getPassword().isEmpty())
			password = account.getPassword();
		
		WSJNI.saveEdgeAccount(username, password);
	}

	public PatchInfo loadD2DPatchInfoFromDll(String dllFilePath) throws EdgeServiceFault {
		return WSJNI.loadD2DPatchInfoFromDll(dllFilePath);
	}

	@Override
	public int isDeployingD2D()
	{
		return WSJNI.isDeployingD2D();
	}

	@Override
	public LicenseMachineType getLicenseMachineType(String server,
			String username, String password) throws EdgeServiceFault {
		return remoteNativeFacade.getLicenseMachineType(server, username, password);
	}

	@Override
	public List<JHypervVMInfo> GetVmList(String serverName, String user, String password, boolean onlyUnderThisHyperv) throws EdgeServiceFault {
		return remoteNativeFacade.GetVmList(serverName, user, password, onlyUnderThisHyperv);
	}

	@Override
	public GetHyperVProtectionTypeResult getHyperVProtectionType(
		String serverName, String user, String password )
	{
		return remoteNativeFacade.getHyperVProtectionType( serverName, user, password );
	}

	@Override
	public JHypervPFCDataConsistencyStatus getHypervPFCDataConsistentStatus(String hostName,String userName, String password,
			String vmGuid, String vmUserName, String vmPassword) {
		return remoteNativeFacade.getHypervPFCDataConsistentStatus(hostName, userName, password,vmGuid, vmUserName, vmPassword);
	}

	@Override
	public void testConnection(String host, String user, String password) throws EdgeServiceFault {
		remoteNativeFacade.testConnection(host, user, password);
	}
	
	@Override
	public void setClusterAccessHint(String serverName, String username, String password) {
		remoteNativeFacade.setClusterAccessHint(serverName, username, password);
	}

	@Override
	public int getD2DDeployProcessStatus( String path )
	{
		return WSJNI.isD2DDeployProcessRunning( path );
	}

	@Override
	public String[] getClusterVirtualByPyhsicalNode( String serverName,
		String user, String password )
	{
		return WSJNI.getClusterVirtualByPyhsicalNode(serverName, user, password);
	}

	@Override
	public String getIpByHostName( String hostName ) throws UnknownHostException
	{
		return remoteNativeFacade.getIpByHostName( hostName );
	}
	
	@Override
	public boolean isHostReachble(String hostName) throws Exception {
		return remoteNativeFacade.isHostReachble(hostName);
	}
	
	@Override
	public int getHyperVCPUSocketCount( String serverName, String username,
		String password )
	{
		return remoteNativeFacade.getHyperVCPUSocketCount( serverName, username, password );
	}

	// Jan sprint
	@Override
	public int validateNASServer(String nasServerName, String username, @NotPrintAttribute String password, String port, String protocol) throws EdgeServiceFault {
		int result = WSJNI.validateNASServer(nasServerName, username, password, port, protocol);
		return result;
	}
	
	@Override
	public int updateASBUDomainName(String cmdDir, String newName,
			String usrName, String usrPwd, String carootPwd) {
		return WSJNI.updateASBUDomainName(cmdDir,newName,usrName,usrPwd,carootPwd);
	}

	@Override
	public int updateASBUPrimaryServerName(String cmdDir, String usrName,
			String usrPwd, String carootPwd) {
		return WSJNI.updateASBUPrimaryServerName(cmdDir,usrName,usrPwd,carootPwd);
	}

	@Override
	public boolean isHyperVRoleInstalledAndHostOS() {
		// TODO Auto-generated method stub
		return remoteNativeFacade.isHyperVRoleInstalledAndHostOS();
	}

	@Override
	public String[] getHypervNetworksFromMonitor(String host, String username,
			String password) {
		// TODO Auto-generated method stub
		return remoteNativeFacade.getHypervNetworksFromMonitor(host, username, password);
	}

	@Override
	public ESXServerInfo getHypervInfo(String host, String user, String password) {
		// TODO Auto-generated method stub
		return remoteNativeFacade.getHypervInfo(host, user, password);
	}
	
	@Override
	public ServiceState checkServiceIsRunning(String hostName, String serviceName,
			String userName, String password) {
		return remoteNativeFacade.checkServiceIsRunning(hostName, serviceName, userName, password);
	}

	@Override
	public long urlDownloadToFile( String url, String saveAs,
		IDownloadStatusCallback statusCallback )
	{
		return WSJNI.urlDownloadToFile( url, saveAs, statusCallback );
	}

	@Override
	public boolean httpDownload( String server, int port, boolean isHttps,
		String serverPath, String saveAs, IHttpDownloadCallback callback,
		HttpDownloadResult result )
	{
		return WSJNI.httpDownload( server, port, isHttps, serverPath, saveAs, callback, result );
	}

	@Override
	public List<DomainUser> getAllDomainUsers( String domainName,
		String dcName, String userName, String password, Date changeTime ) throws Exception
	{
		return remoteNativeFacade.getAllDomainUsers( domainName, dcName, userName, password, changeTime );
	}

	@Override
	public JHypervVMInfo getHypervVMInfo(String hostName, String userName,
			String password, String vmInstanceUUID) {
		return remoteNativeFacade.getHypervVMInfo(hostName, userName, password, vmInstanceUUID);
	}

	@Override
	public List<JHypervInfo> getHypervList(String serverName, String user, String password) throws EdgeServiceFault {
		return remoteNativeFacade.getHypervList(serverName, user, password);
	}

	@Override
	public HttpProxySettings getIEProxySettings( String username, String password ) throws Exception
	{
		String logPrefix = this.getClass().getSimpleName() + ".getIEProxySettings(): ";
		logger.info( logPrefix + "Begin to get IE proxy settings." );
		
		HttpProxySettings proxySettings = new HttpProxySettings();
		long result = WSJNI.getIEProxySettings( username, password, proxySettings );
		if (result != 0)
		{
			logger.error( logPrefix + "Error getting IE proxy settings. Return value: " + result );
			throw new Exception( "NativeFacadeImpl.getIEProxySettings(): Native API returns error. Return: " + result );
		}
		
		logger.info( logPrefix + "IE proxy settings got successfully. Proxy settings: " + proxySettings );
		return proxySettings;
	}

	@Override
	public List<String> getFqdnNamebyHostNameOrIp(String hostnameOrIp)
			throws Exception {
		return remoteNativeFacade.getFqdnNamebyHostNameOrIp(hostnameOrIp);
	}

	@Override
	public long testConnection(ArchiveCloudDestInfo cloudDestInfo)
			throws ServiceException {
		
		long connectionStatus = 0L;
		try {
			connectionStatus = com.ca.arcflash.webservice.jni.WSJNI.testConnection(cloudDestInfo);
			logger.info("error code in testConnection( )" + connectionStatus);
		} catch (WSJNIException se) {
			logger.error(se.getMessage(), se);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} catch (Exception t) {
			logger.error(t.getMessage(), t);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return connectionStatus;
	}

	@Override
	public long getAccountDomain( String accountName, String domain,
		String password, StringBuffer normalizedDomain )
	{
		return WSJNI.getAccountDomain( accountName, domain, password, normalizedDomain );
	}

	@Override
	public boolean getComputerName( ComputerNameType nameType, StringBuffer name )
	{
		return WSJNI.getComputerName( nameType, name );
	}
}
