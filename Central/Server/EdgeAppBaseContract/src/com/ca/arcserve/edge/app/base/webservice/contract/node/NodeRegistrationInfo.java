package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlSeeAlso;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.NodeRegistrationInfoForRPS;

/**
 * Information for registering a node.
 */
@XmlSeeAlso( {
	NodeRegistrationInfoForRHA.class,
	NodeRegistrationInfoForRPS.class,
    NodeRegistrationInfoForLinux.class,
    NodeRegistrationInfoForVcloud.class,
	} )
public class NodeRegistrationInfo implements Serializable {
	private static final long serialVersionUID = 8366586396608267853L;
	private int id;
	private String nodeName;
	private String nodeDescription;
	private String username;
	private @NotPrintAttribute String password;
	private boolean registerD2D;
	private boolean registerARCserveBackup;
	private RemoteNodeInfo nodeInfo;
	private int d2dPort;
	private Protocol d2dProtocol;
	private String carootUsername;
	private @NotPrintAttribute String carootPassword;
	private int arcservePort=6054;
	private Protocol arcserveProtocol;
	private ABFuncAuthMode abAuthMode = ABFuncAuthMode.AR_CSERVE;
	private boolean isPhysicsMachine = true;
	private boolean isVMWareVM = false;
	private boolean isHyperVVM = false;//stand alone hyperv VM
	private boolean isHyperVClusterVM=false;//cluster vm
	private boolean isVCMMonitor = false;
	private boolean isVCMMonitee = false;
	private boolean isLinux = false;
	private ProtectionType protectionType = ProtectionType.WIN_D2D;
	private DiscoveryESXOption discoveryESXOption; 
	private VMRegistrationInfo vmRegistrationInfo;
	private boolean failedReadRemoteRegistry;
	
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;
	
	private boolean consoleInstalled = false;
	private int consolePort;
	private Protocol consoleProtocol;
	
	/**
	 * Get authentication type of the ARCserve Backup that is running on the
	 * node
	 * 
	 * @return
	 */
	public ABFuncAuthMode getAbAuthMode() {
		return abAuthMode;
	}
	
	/**
	 * Set authentication type of the ARCserve Backup that is running on the
	 * node
	 * 
	 * @param abAuthMode
	 */
	public void setAbAuthMode(ABFuncAuthMode abAuthMode) {
		this.abAuthMode = abAuthMode;
	}
	
	/**
	 * Get ID of the node.
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Set ID of the node.
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Get the port number of the web service of the UDP agent that is running
	 * on the node.
	 * 
	 * @return
	 */
	public int getD2dPort() {
		return d2dPort;
	}
	
	/**
	 * Set the port number of the web service of the UDP agent that is running
	 * on the node.
	 * 
	 * @param d2dPort
	 */
	public void setD2dPort(int d2dPort) {
		this.d2dPort = d2dPort;
	}
	
	/**
	 * Get the protocol of the web service of the UDP agent that is running
	 * on the node.
	 * 
	 * @return
	 */
	public Protocol getD2dProtocol() {
		return d2dProtocol;
	}
	
	/**
	 * Set the protocol of the web service of the UDP agent that is running
	 * on the node.
	 * 
	 * @param d2dProtocol
	 */
	public void setD2dProtocol(Protocol d2dProtocol) {
		this.d2dProtocol = d2dProtocol;
	}
	
	/**
	 * Get user name of ARCserve Backup that is running on the node.
	 * 
	 * @return
	 */
	public String getCarootUsername() {
		return carootUsername;
	}
	
	/**
	 * Set user name of ARCserve Backup that is running on the node.
	 * 
	 * @param carootUsername
	 */
	public void setCarootUsername(String carootUsername) {
		this.carootUsername = carootUsername;
	}
	
	/**
	 * Get password of ARCserve Backup that is running on the node.
	 * 
	 * @return
	 */
	public String getCarootPassword() {
		return carootPassword;
	}
	
	/**
	 * Set password of ARCserve Backup that is running on the node.
	 * 
	 * @param carootPassword
	 */
	public void setCarootPassword(String carootPassword) {
		this.carootPassword = carootPassword;
	}
	
	/**
	 * Get port of web service of ARCserve Backup that is running on the node.
	 * 
	 * @return
	 */
	public int getArcservePort() {
		return arcservePort;
	}
	
	/**
	 * Set port of web service of ARCserve Backup that is running on the node.
	 * 
	 * @param arcservePort
	 */
	public void setArcservePort(int arcservePort) {
		this.arcservePort = arcservePort;
	}
	
	/**
	 * Get protocol of web service of ARCserve Backup that is running on the
	 * node.
	 * 
	 * @return
	 */
	public Protocol getArcserveProtocol() {
		return arcserveProtocol;
	}
	
	/**
	 * Set protocol of web service of ARCserve Backup that is running on the
	 * node.
	 * 
	 * @param arcserveProtocol
	 */
	public void setArcserveProtocol(Protocol arcserveProtocol) {
		this.arcserveProtocol = arcserveProtocol;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/**
	 * Get information of the node retrieved by scan the node's registry
	 * remotely.
	 * 
	 * @return
	 */
	public RemoteNodeInfo getNodeInfo() {
		return nodeInfo;
	}
	
	/**
	 * Set information of the node retrieved by scan the node's registry
	 * remotely.
	 * 
	 * @param nodeInfo
	 */
	public void setNodeInfo(RemoteNodeInfo nodeInfo) {
		this.nodeInfo = nodeInfo;
	}
	
	/**
	 * Get host name of the node.
	 * 
	 * @return
	 */
	public String getNodeName() {
		return nodeName;
	}
	
	/**
	 * Set host name of the node.
	 * 
	 * @param nodeName
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	/**
	 * Get description of the node.
	 * 
	 * @return
	 */
	public String getNodeDescription() {
		return this.nodeDescription;
	}
	
	/**
	 * Set description of the node.
	 * 
	 * @param nodeDescription
	 */
	public void setNodeDescription(String nodeDescription) {
		this.nodeDescription = nodeDescription;
	}	
	
	/**
	 * Get user name of the node.
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Set user name of the node.
	 * 
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Get password of the node.
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Set password of the node.
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Whether register the UDP agent that is running on the node.
	 * 
	 * @return
	 */
	public boolean isRegisterD2D() {
		return registerD2D;
	}
	
	/**
	 * Set whether register the UDP agent that is running on the node.
	 * 
	 * @param registerD2D
	 */
	public void setRegisterD2D(boolean registerD2D) {
		this.registerD2D = registerD2D;
	}
	
	/**
	 * Whether register the ARCserve Backup that is running on the node.
	 * 
	 * @return
	 */
	public boolean isRegisterARCserveBackup() {
		return registerARCserveBackup;
	}
	
	/**
	 * Set whether register the ARCserve Backup that is running on the node.
	 * 
	 * @param registerARCserveBackup
	 */
	public void setRegisterARCserveBackup(boolean registerARCserveBackup) {
		this.registerARCserveBackup = registerARCserveBackup;
	}
	
	/**
	 * Whether the node will be added as a physical machine. This doesn't mean
	 * the node is actually a physical machine. This only indicates the node
	 * will be treated as a physical machine.
	 * 
	 * @return
	 */
	public boolean isPhysicsMachine() {
		return isPhysicsMachine;
	}
	
	/**
	 * Set whether the node will be added as a physical machine. This doesn't
	 * mean the node is actually a physical machine. This only indicates the
	 * node will be treated as a physical machine.
	 * 
	 * @param isPhysicsMachine
	 */
	public void setPhysicsMachine(boolean isPhysicsMachine) {
		this.isPhysicsMachine = isPhysicsMachine;
	}
	
	/**
	 * Whether the node is a VMware virtual machine.
	 * 
	 * @return
	 */
	public boolean isVMWareVM() {
		return isVMWareVM;
	}
	
	/**
	 * Set whether the node is a VMware virtual machine.
	 * 
	 * @param isVMWareVM
	 */
	public void setVMWareVM(boolean isVMWareVM) {
		this.isVMWareVM = isVMWareVM;
	}
	
	/**
	 * Whether the node is a virtual conversion monitor.
	 * 
	 * @return
	 */
	public boolean isVCMMonitor()
	{
		return isVCMMonitor;
	}
	
	/**
	 * Set whether the node is a virtual conversion monitor.
	 * 
	 * @param isVCMMonitor
	 */
	public void setVCMMonitor( boolean isVCMMonitor )
	{
		this.isVCMMonitor = isVCMMonitor;
	}
	
	/**
	 * Whether the node is a virtual conversion monitee.
	 * 
	 * @return
	 */
	public boolean isVCMMonitee()
	{
		return isVCMMonitee;
	}
	
	/**
	 * Set whether the node is a virtual conversion monitee.
	 * 
	 * @param isVCMMonitee
	 */
	public void setVCMMonitee( boolean isVCMMonitee )
	{
		this.isVCMMonitee = isVCMMonitee;
	}
	
	/**
	 * Get the protection type of the node.
	 * 
	 * @return
	 */
	public ProtectionType getProtectionType() {
		return protectionType;
	}
	
	/**
	 * Set the protection type of the node.
	 * 
	 * @param protectionType
	 */
	public void setProtectionType(ProtectionType protectionType) {
		this.protectionType = protectionType;
	}
	
	/**
	 * Whether the node is a Hyper-V virtual machine.
	 * 
	 * @return
	 */
	public boolean isHyperVVM() {
		return isHyperVVM;
	}
	
	/**
	 * Set whether the node is a Hyper-V virtual machine.
	 * 
	 * @param isHyperVVM
	 */
	public void setHyperVVM(boolean isHyperVVM) {
		this.isHyperVVM = isHyperVVM;
	}
	
	/**
	 * Whether the node is a Linux node.
	 * 
	 * @return
	 */
	public boolean isLinux() {
		return isLinux;
	}
	
	/**
	 * Set whether the node is a Linux node.
	 * 
	 * @param isLinux
	 */
	public void setLinux(boolean isLinux) {
		this.isLinux = isLinux;
	}
	
	@Override
	public String toString(){
		if (nodeName != null && !"".equals(nodeName)) {
			return this.getNodeName();
		} else if(vmRegistrationInfo != null){
			//VM Node without host name
			return vmRegistrationInfo.getVmInfo().getVmName();
		} else {
			return "Unknown";
		}
	}
	
	/**
	 * Get options for ESX server discovery.
	 * 
	 * @return
	 */
	public DiscoveryESXOption getDiscoveryESXOption() {
		return discoveryESXOption;
	}
	
	/**
	 * Set options for ESX server discovery.
	 * 
	 * @param discoveryESXOption
	 */
	public void setDiscoveryESXOption(DiscoveryESXOption discoveryESXOption) {
		this.discoveryESXOption = discoveryESXOption;
	}
	
	/**
	 * Get VM information of the node retrieved by scanning the hypervisor
	 * of the node.
	 * 
	 * @return
	 */
	public VMRegistrationInfo getVmRegistrationInfo() {
		return vmRegistrationInfo;
	}
	
	/**
	 * Set VM information of the node retrieved by scanning the hypervisor
	 * of the node.
	 * 
	 * @param vmRegistrationInfo
	 */
	public void setVmRegistrationInfo(VMRegistrationInfo vmRegistrationInfo) {
		this.vmRegistrationInfo = vmRegistrationInfo;
	}
	public String getNodeUid() {
		if (nodeName != null && !"".equals(nodeName)) {
			return this.getNodeName();
		} else if(vmRegistrationInfo != null){
			//VM Node without host name
			return vmRegistrationInfo.getVmInfo().getVmName();
		} else {
			return "Unknown";
		}
	}
	
	/**
	 * Whether it's failed to read the registry of the node remotely.
	 * 
	 * @return
	 */
	public boolean isFailedReadRemoteRegistry() {
		return failedReadRemoteRegistry;
	}

	/**
	 * Set whether it's failed to read the registry of the node remotely.
	 * 
	 * @param failedReadRemoteRegistry
	 */
	public void setFailedReadRemoteRegistry(boolean failedReadRemoteRegistry) {
		this.failedReadRemoteRegistry = failedReadRemoteRegistry;
	}
	
	/**
	 * Whether the node is a Hyper-V cluster virtual machine.
	 * 
	 * @return
	 */
	public boolean isHyperVClusterVM() {
		return isHyperVClusterVM;
	}
	
	/**
	 * Set whether the node is a Hyper-V cluster virtual machine.
	 * 
	 * @param isHyperVClusterVM
	 */
	public void setHyperVClusterVM(boolean isHyperVClusterVM) {
		this.isHyperVClusterVM = isHyperVClusterVM;
	}

	public GatewayId getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(GatewayId gatewayId) {
		if (gatewayId == null)
			gatewayId = GatewayId.INVALID_GATEWAY_ID;
		this.gatewayId = gatewayId;
	}
	
	
	/**
	 * Get the port number of the web service of the UDP console that is running
	 * on the node.
	 * 
	 * @return
	 */
	public int getConsolePort() {
		return consolePort;
	}
	
	/**
	 * Set the port number of the web service of the UDP console that is running
	 * on the node.
	 * 
	 * @param consolePort
	 */
	public void setConsolePort(int consolePort) {
		this.consolePort = consolePort;
	}
	
	/**
	 * Get the protocol of the web service of the UDP console that is running
	 * on the node.
	 * 
	 * @return
	 */
	public Protocol getConsoleProtocol() {
		return consoleProtocol;
	}
	
	/**
	 * Set the protocol of the web service of the UDP console that is running
	 * on the node.
	 * 
	 * @param consoleProtocol
	 */
	public void setConsoleProtocol(Protocol consoleProtocol) {
		this.consoleProtocol = consoleProtocol;
	}
	
	public boolean isConsoleInstalled()
	{
		return this.consoleInstalled;
	}
	
	public void setConsoleInstalled(boolean consoleInstalled)
	{
		this.consoleInstalled = consoleInstalled;
	}
}
