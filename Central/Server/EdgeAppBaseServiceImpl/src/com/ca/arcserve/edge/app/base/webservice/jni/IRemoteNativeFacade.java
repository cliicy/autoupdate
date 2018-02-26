package com.ca.arcserve.edge.app.base.webservice.jni;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.ha.model.ESXServerInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ServiceState;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicenseMachineType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AdminAccountValidationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.webservice.jni.model.DomainUser;
import com.ca.arcserve.edge.webservice.jni.model.JNode;

/**
 * This interface includes all the JNI APIs that may cross LAN, and generally for WAN support.
 * @author qiubo01
 *
 */
public interface IRemoteNativeFacade extends IHyperVNativeFacade {
	
	AdminAccountValidationResult validateAdminAccount(String computerName, String userName, String password) throws EdgeServiceFault;
	AdminAccountValidationResult verifyHyperVAdminAccount(String computerName, String userName, String password, boolean isCluster) throws EdgeServiceFault;
	RemoteNodeInfo scanRemoteNode(String edgeUser, String edgeDomain, String edgePassword, String computerName, String userName, String password) throws EdgeServiceFault;
	
	void verifyADAccount(String adServerName, String adUser, String adPassword) throws EdgeServiceFault;
	
	List<JNode> getNodes(String adServerName, String adUser, String adPassword, String computerName, String computerOS, boolean bSQL, boolean bExch) throws EdgeServiceFault;
	
	LicenseMachineType getLicenseMachineType(String server, String username, String password) throws EdgeServiceFault;
	void setClusterAccessHint(String serverName, String username, String password);
	
	String[] getClusterVirtualByPyhsicalNode(String serverName, String user, String password);
	
	String getIpByHostName( String hostName ) throws UnknownHostException;
	
	boolean isHostReachble(String hostName) throws Exception;
	
	int getHyperVCPUSocketCount(String serverName, String username, String password);

		//Jan sprint
	int validateNASServer(String nasServerName, String userName,
			String password, String port, String protocol) throws EdgeServiceFault;
	boolean isHyperVRoleInstalledAndHostOS();
	String[] getHypervNetworksFromMonitor(String host, String username,
			String password);
	ESXServerInfo getHypervInfo(String host, String user, String password);
	
	ServiceState checkServiceIsRunning(String hostName, String serviceName, String userName, String password);
	public long testConnection(ArchiveCloudDestInfo cloudDestInfo)throws ServiceException;
	
	/**
	 * Get all users from active directory of a specified domain. The DC name
	 * is optional in case Windows cannot locate the DC from the domain name.
	 * Currently, this function only returns users of current domain of current
	 * logged in user, so all the parameters are not used now.
	 * 
	 * @param	domainName
	 * 			Domain of the user.
	 * @param	dcName
	 * 			DC of the domain. Specify null to find DC automatically.
	 * @param	userName
	 * 			Name of the user.
	 * @param	password
	 * 			Password of the user.
	 * @param	changeTime
	 * 			The time filter that returned accounts should be created or changed
	 * 			after this time. Specify null for to indicate no filter.
	 * 
	 * @return	A list contains all users of the active directory.
	 * 
	 * @throws	Exception
	 */
	List<DomainUser> getAllDomainUsers( String domainName, String dcName, String userName,
		String password, Date changeTime ) throws Exception;
	
	List<String> getFqdnNamebyHostNameOrIp(String hostnameOrIp) throws Exception;
}
