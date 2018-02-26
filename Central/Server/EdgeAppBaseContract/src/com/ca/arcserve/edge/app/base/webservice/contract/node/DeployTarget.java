package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.RebootType;

/**
 * The information of the target to which the a agent will be deployed.
 */
public class DeployTarget implements Serializable
{
	private static final long serialVersionUID = -1307633729282840043L;

	private int nodeID = 0;
	private String username = "";
	private @NotPrintAttribute String password  = "";
	private int port = 0;
	private String installDirectory = "";
	private RebootType rebootType = RebootType.RebootAtOnce;
	private Protocol protocol = Protocol.Https;
	private boolean isInstallDriver = true;
	private Date startDeploymentTime = new Date();

	/**
	 * Set the ID of the node to which the agent will be deployed.
	 * 
	 * @param nodeID
	 */
	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	/**
	 * Get the ID of the node to which the agent will be deployed.
	 * 
	 * @return
	 */
	public int getNodeID() {
		return nodeID;
	}
	
	/**
	 * Get the user name of the node to which the agent will be deployed.
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Set the user name of the node to which the agent will be deployed.
	 * 
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Get the password of the node to which the agent will be deployed.
	 * 
	 * @return
	 */
	@EncryptSave
	public String getPassword() {
		return password;
	}

	/**
	 * Set the password of the node to which the agent will be deployed.
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get the port number the new deployed agent will use for its web
	 * service.
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Set the port number the new deployed agent will use for its web
	 * service.
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Get the directory the new deployed agent will be installed to.
	 * 
	 * @return
	 */
	public String getInstallDirectory() {
		return installDirectory;
	}

	/**
	 * Set the directory the new deployed agent will be installed to.
	 * 
	 * @param installDirectory
	 */
	public void setInstallDirectory(String installDirectory) {
		this.installDirectory = installDirectory;
	}

	/**
	 * Get the protocol the new deployed agent will use for its web service.
	 * 
	 * @return
	 */
	public Protocol getProtocol() {
		return protocol;
	}

	/**
	 * Set the protocol the new deployed agent will use for its web service.
	 * 
	 * @param protocol
	 */
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	/**
	 * Get when to reboot the host if the installation of the agent requires
	 * rebooting the machine.
	 * 
	 * @return
	 */
	public RebootType getRebootType() {
		return rebootType;
	}

	/**
	 * Set when to reboot the host if the installation of the agent requires
	 * rebooting the machine.
	 * 
	 * @param rebootType
	 */
	public void setRebootType(RebootType rebootType) {
		this.rebootType = rebootType;
	}

	/**
	 * Set whether to install driver when installing agent.
	 * 
	 * @param installDriver
	 */
	public void setInstallDriver(boolean installDriver){
		this.isInstallDriver = installDriver;
	}
	
	/**
	 * Whether to install driver when installing agent.
	 * 
	 * @return
	 */
	public boolean isInstallDriver(){
		return isInstallDriver;
	}
	
	/**
	 * Get start time of the deployment.
	 * 
	 * @param progressMessage
	 */
	public Date getStartDeploymentTime() {
		return startDeploymentTime;
	}

	/**
	 * Set start time of the deployment.
	 * 
	 * @param startDeploymentTime
	 */
	public void setStartDeploymentTime(Date startDeploymentTime) {
		this.startDeploymentTime = startDeploymentTime;
	}

}
